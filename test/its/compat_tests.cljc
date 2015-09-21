(ns its.compat-tests
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cljs.test :as t :refer-macros [deftest is testing]])
            [its.compat :refer [index-of]]))

(deftest test-index-of
  (testing "when substring does not occur"
    (is (= -1 (index-of "foo" "bar")))
    (is (= -1 (index-of "bar" "foo"))))

  (testing "when substring occurs at the beginning"
    (is (= 0 (index-of "foobar" "foo")))
    (is (= 0 (index-of "bazbif" "baz"))))

  (testing "when substring occurs in the middle"
    (is (= 1 (index-of "foobar" "oo")))
    (is (= 2 (index-of "bazbif" "zbi"))))

  (testing "when substring occurs at the end"
    (is (= 0 (index-of "foo" "foo")))
    (is (= 3 (index-of "bazbif" "bif")))
    (is (= 9 (index-of "abc def ghi" "hi")))))


;; (run-tests)
