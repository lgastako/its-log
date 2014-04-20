(ns its.bus
  (:require [its.log :as log]))

(def ^:private bus (atom nil))

(defn log
  "Receives a formatted log line as an edn structure and dispatches it to the
   log bus"
  [entry]
  {:entry (reset! bus entry)
   :result :logged})

#+cljs (def watch-keys (atom #{}))

(defn watch!
  "Arranges for cb to be called with `key` as the key for canceling it via
   unwatch!  The new `cb` callback replaces any previously existing callback at
   `key`. "
  [key cb]
  (add-watch bus key (fn [k r o n] (cb n)))
  #+cljs (swap! watch-keys conj key)
  {:key key
   :result :watched})

(defn printer
  "A handy fn to use with watch! when e.g. debugging."
  [e]
  (println (pr-str e)))

(def ^:private collection (atom []))

(defn collector
  "Another debugging fn.  For example when your printer should be printing
   but it's not and you are trying to further partition your potential
   bugspace."
  [e]
  (swap! collection conj e))

(defn collected
  "Returns the current contents of `collector`'s collection."
  []
  @collection)

(defn pop-collected!
  "Pops the current contents of `collector`'s collection out
   returning the contents and leaving the collection empty."
  []
  (let [result (collected)]
    (reset! collection [])
    result))

(def ^:private auto-watch-sequence (atom 0))

(defn watch
  "Arranges for `cb` to be called as a callback on any its.bus entries.
   Returns a unique key that lets you cancel it via unwatch!"
  [cb]
  (letfn [(make-key [seed]
            (keyword (str "_-*-_auto-_=*!*=_-watch_-*-_..." seed)))]
    (let [key (make-key (swap! auto-watch-sequence inc))]
      (watch! key cb)
      key)))

(defn unwatch!
  "Cancel the calling of the callback associated with `key`
   with `key` defaulting to `::default-async-chan`."
  ([] (unwatch! ::default-async-chan))
  ([key]
     (remove-watch bus key)
     #+cljs (swap! watch-keys disj key)
     {:key key
      :result :unwatched}))

;; From http://stackoverflow.com/questions/12813695/is-there-a-way-of-getting-all-the-keys-of-the-watches-in-clojure
#+clj (defn- get-watches
        "Returns list of keys corresponding to watchers of the reference."
        [^clojure.lang.IRef reference]
        (keys (.getWatches reference)))

(defn watches
  "Returns list of keys corresponding to watchers of the bus."
  []
  #+clj (get-watches bus)
  #+cljs (vec @watch-keys))

(defn unwatch-all!
  "Remove all callbacks attached to the bus."
  []
  (vec (map unwatch! (watches))))

(defn replace!
  "Replace the its-log logger with a logger that logs events only
   to its.bus."
  []
  (reset! log/<log> log)
  {:result :replaced})

(defn- swallowing [f]
  (try
    (f)
    (catch #+clj Exception
      #+cljs js/Error
      ex)))

(defn wrap!
  "Replace the its-log logger with a logger that logs events to its.bus
   and also the original logger function (silencing exceptions from either)."
  []
  (let [old-log @log/<log>]
    (reset! log/<log> (fn [& args]
                        (swallowing #(apply log args))
                        (swallowing #(apply old-log args))))
    {:result :wrapped}))

(defn replace-with-printer!
  "Like replace! but also installs a printer by default."
  ([] (replace-with-printer! :printer))
  ([key]
     (replace!)
     (watch! key printer)))

(defn wrap-with-printer!
  "Like wrap! but also installs a printer by default."
  ([] (wrap-with-printer! :printer))
  ([key]
     (wrap!)
     (watch! key printer)))
