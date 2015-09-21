(ns its.parse
  #?(:clj  (:refer-clojure :exclude [slurp]))
  (:require [clojure.string :as string]
            #?(:clj [clojure.edn :as edn])
            #?(:cljs [cljs.reader :as cljs-reader])))

(def line
  #?(:clj  edn/read-string)
  #?(:cljs cljs-reader/read-string))

(def lines (partial map line))

(def log (comp lines string/split-lines))

(def string (comp lines #(string/split % #"\n")))

(def slurp (comp vec string clojure.core/slurp))
