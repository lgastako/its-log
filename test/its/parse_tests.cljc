(ns its.parse-tests
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cemerick.cljs.test :as t :refer-macros [deftest is]])
            [its.examples :as examples]
            [its.log :as log :refer [log]]
            [its.parse :as parse]
            [its.test-helpers :as helpers]))

(deftest test-parse-line
  (testing "basic parse"
    (is (= [#inst "2014-03-10T03:02:50.855-00:00" :info :initializing "My App"]
           (parse/line (first examples/logged-lines))))))

(deftest test-parse-lines
  (testing "basic parsing of a sequence of lines"
    (is (= examples/lines
           (parse/lines examples/logged-lines)))))

(deftest test-parse-log
  (testing "basic parsing of one big log as a string"
    (is (= examples/lines
           (parse/log examples/example-log)))))


;; (run-tests)
