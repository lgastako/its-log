(ns its.bus.async
  (:require #+clj [clojure.core.async :as async :refer [>! go]]
            #+cljs [cljs.core.async :as async :refer [>!]]
            [its.bus :as bus])
  #+cljs (:require-macros [cljs.core.async.macros :refer [go]]))

(defn chan-key
  "Produce a tupe of `[channel key]` where `chan` will receive all log entries
   from its.bus and closing `chan` will propagate upstream
   appropriately (specifically unwatch!-ing the bus).

   Overide the default key of `::default-async-chan` by proviiding a `key`."
  ([] (chan-key ::default-async-chan))
  ([key]
     (let [c (async/chan)]
       (bus/watch! key (fn [entry] (go (>! c entry))))
       {:chan c
        :key key})))

(defn chan
  "Produce a channel which will receive all log entries from its.bus.

   With no args, produces a core.async channel which produces its.bus
   entries and can be cancelled with `::default-async-chan`.

   An optional `key` can be provided to be used instead.
  "
  ([& args]
     (:chan (apply chan-key args))))
