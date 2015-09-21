(ns its.access-tests
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cljs.test :as t :refer-macros [deftest is testing]])
            [its.examples :refer [example-log lines]]
            [its.access :as access]))

(deftest test-timestamp-access
  (is (= [#inst "2014-03-10T03:02:50.855-00:00"
          #inst "2014-03-10T03:02:51.407-00:00"
          #inst "2014-03-10T03:02:52.702-00:00"
          #inst "2014-03-10T03:02:53.305-00:00"]
         (map access/timestamp lines))))

(deftest test-level-access
  (is (= [:info :debug :warn :error]
         (map access/level lines))))

(deftest test-message-access
  (is (= [:initializing :loading-usercache :the-roof :game]
         (map access/message lines))))

(deftest test-args-access
  (is (= [["My App"]
          [{:count 1037}]
          [[:the-roof] [[:the-roof]] {:is :on-fire}]
          [:over]]
         (map access/args lines))))

(deftest test-debug?
  (is (= [false true false false]
         (map access/debug? lines))))

(deftest test-info?
  (is (= [true false false false]
         (map access/info? lines))))

(deftest test-warn?
  (is (= [false false true false]
         (map access/warn? lines))))

(deftest test-error?
  (is (= [false false false true]
         (map access/error? lines))))

(deftest test-debugs
  (is (= [[#inst "2014-03-10T03:02:51.407-00:00" :debug :loading-usercache {:count 1037}]]
         (access/debugs lines))))

(deftest test-infos
  (is (= [[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing "My App"]]
         (access/infos lines))))

(deftest test-warns
  (is (= [[#inst "2014-03-10T03:02:52.702-00:00" :warn :the-roof [:the-roof] [[:the-roof]] {:is :on-fire}]]
         (access/warns lines))))

(deftest test-errors
  (is (= [[#inst "2014-03-10T03:02:53.305-00:00" :error :game :over]]
         (access/errors lines))))


;; (run-tests)
