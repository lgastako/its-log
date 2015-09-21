(ns its.test-helpers
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cemerick.cljs.test :as t :refer-macros [deftest is]])
            #?(:clj [clojure.edn :as edn])
            #?(:cljs [cljs.reader :as cljs-reader])
            [clojure.string :as string]
            [its.log :as log :refer [log]]
            [its.loggers :as loggers]))

#?(:clj
   (defn with-log-str [f]
     (with-out-str (f))))

#?(:cljs
   (defn with-log-str [f]
     (let [orig-log-fn *print-fn*
           messages (atom [])
           make-log-str #(str (string/join "" (map pr-str %)) "\n")
           shim (fn [& args]
                  (apply orig-log-fn args)
                  (swap! messages conj (make-log-str args)))]
       (with-redefs [*print-fn* shim]
         (f))
       (string/join "\n" @messages))))

(deftest test-with-log-str
  (is (= "laid back\n" (with-log-str #(println "laid back")))))

(def str->clj
  #?(:clj edn/read-string)
  #?(:cljs cljs-reader/read-string))

(def without-timestamp (comp vec rest))

(defn without-timestamps [logs]
  (mapv without-timestamp logs))

(def unstamped (comp without-timestamp str->clj with-log-str))

(defn log-to [dst]
  (fn [entry]
    (swap! dst conj entry)))

;; (run-tests)
