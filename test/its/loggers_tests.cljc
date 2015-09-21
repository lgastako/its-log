(ns its.loggers-tests
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cemerick.cljs.test :as t :refer-macros [deftest is]])
            #?(:clj [clojure.edn :as edn])
            #?(:cljs [cljs.reader :as cljs-reader])
            [clojure.string :as string]
            [its.log :as log :refer [log]]
            [its.loggers :as loggers :refer [loggers]]
            [its.test-helpers :refer [log-to without-timestamps]]))

(deftest test-set
  (testing "setting a new logger"
    (loggers/clear)
    (let [logs1 (atom [])
          logs2 (atom [])
          logs3 (atom [])]
      (loggers/set :logger-1 (log-to logs1))
      (loggers/set :logger-2 (log-to logs2))
      (loggers/set :logger-3 (log-to logs3))
      (log/set-level! :debug)
      (log/debug :msg-1)
      (log/debug :msg-2 {:msg2-a "example"})
      (log/debug :msg-3)
      (is (= @logs1 @logs2 @logs3))
      (is (= (mapv (comp vec rest) @logs1)
             [[:debug :msg-1]
              [:debug :msg-2 {:msg2-a "example"}]
              [:debug :msg-3]]))))

  (testing "(re)setting an existing logger"
    (loggers/clear)
    (let [logs1 (atom [])
          logs2 (atom [])]
      (loggers/set :only (log-to logs1))
      (log/debug :foo)
      (loggers/set :only (log-to logs2))
      (log/debug :bar)
      (log/debug :baz)
      (is (= [[:debug :foo]]
             (without-timestamps @logs1)))
      (is (= [[:debug :bar]
              [:debug :baz]]
             (without-timestamps @logs2))))))

(deftest test-clear
  (testing "clearing when already clear is fine"
    (loggers/clear)
    (loggers/clear))

  (testing "clearing when loggers are set"
    (loggers/set-default)
    (= 1 (count @loggers))
    (loggers/set :identity identity)
    (= 1 (count @loggers))
    (loggers/clear)
    (= 0 (count @loggers))))

(deftest test-set-default
  (testing "when already default"
    (loggers/set-default)
    (loggers/set-default)
    (= #{:its.log/default} (keys @loggers)))

  (testing "when not already default"
    (loggers/clear)
    (loggers/set :identity identity)
    (loggers/set :print (comp println pr-str))
    (= #{:identity :print} (keys @loggers))
    (loggers/set-default)
    (= #{:its.log/default}) (keys @loggers)))

(deftest test-remove
  (testing "when not there"
    (loggers/clear)
    (loggers/remove :foo)
    (= #{} (keys @loggers)))

  (testing "when there"
    (loggers/clear)
    (loggers/set :foo identity)
    (loggers/set :bar identity)
    (loggers/remove :foo)
    (= #{:bar} (keys @loggers))))


;; (run-tests)
