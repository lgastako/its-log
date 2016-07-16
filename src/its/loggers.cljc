(ns its.loggers
  (:refer-clojure :exclude [remove set])
  (:require [clojure.string :as string]
            [its.compat :refer [now index-of]]))

(def default-logger (comp println pr-str))

(def initial-loggers {::default default-logger})

(def loggers (atom initial-loggers))

(defn set [key logger]
  (swap! loggers assoc key logger))

(defn remove [key]
  (swap! loggers dissoc key))

(defn clear []
  (reset! loggers {}))

(defn set-default []
  (reset! loggers initial-loggers))
