(ns its.async-tests
  (:require #?(:clj  [clojure.test :refer :all])
            #?(:cljs [cljs.test :as t :refer-macros [async deftest is testing]])
            #?(:clj  [clojure.core.async :as async :refer [<! >! chan put! go]])
            #?(:cljs [cljs.core.async    :as async :refer [<! >! chan put!] :refer-macros [go]])
            [its.async :as alog]
            [its.log :as log]
            [its.loggers :as loggers]
            [its.test-helpers :refer [test-async test-within without-timestamp]]))

#?(:clj

   (deftest test-async-same-thread
     (testing "values get logged"
       (loggers/clear)
       (let [c (chan)]
         (loggers/set :async (alog/make-log c))
         (log/set-level! :debug)
         (log/debug :foo)
         (test-async
          (test-within 10 (go (is (= [:debug :foo]
                                     (without-timestamp (<! c))))))))))

   (deftest test-async-different-thread
     (testing "values get logged"
       (loggers/clear)
       (let [c (chan)]
         (log/set-level! :debug)
         (loggers/set :async (alog/make-log c))
         (future (log/debug :foo))
         (test-async
          (test-within 10 (go (is (= [:debug :foo]
                                     (without-timestamp (<! c)))))))))))


#?(:cljs

   (deftest test-async-cljs
     (testing "values get logged"
       (loggers/clear)
       (let [c (chan)]
         (loggers/set :async (alog/make-log c))
         (log/set-level! :debug)
         (log/debug :foo)
         (test-async
          (test-within 10 (go (let [val (<! c)]
                                (is
                                 (= [:debug :foo]
                                    ((comp vec rest) val)))))))))))


(run-tests)
