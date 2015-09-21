(ns its.examples
  (:require [clojure.string :as string]))

(def lines
  [[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing "My App"]
   [#inst "2014-03-10T03:02:51.407-00:00" :debug :loading-usercache {:count 1037}]
   [#inst "2014-03-10T03:02:52.702-00:00" :warn :the-roof [:the-roof] [[:the-roof]] {:is :on-fire}]
   [#inst "2014-03-10T03:02:53.305-00:00" :error :game :over]])

(def example-log (string/join "\n" (map pr-str lines)))
