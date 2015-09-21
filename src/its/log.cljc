(ns its.log
  (:require [clojure.string :as string]
            [its.compat :refer [now index-of]]
            [its.loggers :refer [loggers]]))

#?(:cljs
   (do
     (enable-console-print!)
     (try
       (nodejs/enable-util-print!)
       (catch :default _))))

(def levels [:debug :info :warn :error :off])

(def default-level :warn)

(def current-level (atom default-level))

(defn level [] @current-level)

(defn valid-level? [level]
  (some #(= level %) levels))

(defn set-level! [level]
  (assert (valid-level? level))
  (reset! current-level level))

(defn log
  [level & args]
  (assert (valid-level? level))
  (let [current-level @current-level]
    (when (and (not= current-level :off)
               (>= (index-of levels level)
                   (index-of levels current-level)))
      (let [entry (vec (concat [(now) level] args))]
        (doseq [[_ logger] @loggers]
          (logger entry))))))

(def debug (partial log :debug))
(def info  (partial log :info))
(def warn  (partial log :warn))
(def error (partial log :error))

(log :info :its.log {:activated-at (now)})
