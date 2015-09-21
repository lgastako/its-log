(ns its.async-tests
  (:require #?(:clj  [clojure.test :refer :all])
            #?(:cljs [cljs.test :as t :refer-macros [async deftest is testing]])
            #?(:clj  [clojure.core.async :as async :refer [>! chan put! go]])
            #?(:cljs [cljs.core.async    :as async :refer [>! chan put!]])
            [its.async :as alog]
            [its.log :as log]
            [its.loggers :as loggers]))

#?(:clj

   (deftest test-async-same-thread
     (testing "values get logged"
       (loggers/clear)
       (let [c (chan)]
         (loggers/set :async (alog/make-log c))
         (log/set-level! :debug)
         (log/debug :foo)
         (= ))))

   (deftest test-async-different-thread
     (testing "values get logged"
       (loggers/clear)
       (let [c (chan)]
         (loggers/set :async (alog/make-log c))
         (log/set-level! :debug)
         (log/debug :foo)))))


#?(:cljs

   (deftest test-async-cljs
     (testing "values get logged"
       (async done
              (loggers/clear)
              (let [c (chan)]
                (loggers/set :async (alog/make-log c))
                (log/set-level! :debug)
                (log/debug :foo)
                (go (let [val (<! c)]
                      (= [:debug :foo]
                         ((comp vec rest) val))
                      (done))))))))


