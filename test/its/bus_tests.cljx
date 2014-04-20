(ns its.bus-tests
  (:require #+cljs [cemerick.cljs.test :as t :refer-macros [deftest is]]
            #+clj [clojure.test :refer :all]
            [its.log :as log :refer [log]]
            [its.bus :as bus]))

(defn- reset-all! []
  (bus/unwatch-all!)
  (log/reset-emitter!))

(deftest test-watch!
  (let [key :test-watch!
        entries (atom [])]
    (bus/watch! key (partial swap! entries conj))
    (try
      (bus/log :foo)
      (bus/log :bar)
      (bus/log :baz)
      (is (= [:foo :bar :baz] @entries))
      (finally (reset-all!)))))

(deftest test-unwatch!
  (let [key :test-unwatch!
        entries (atom [])]
    (bus/watch! key (partial swap! entries conj))
    (try
      (bus/log :foo)
      (bus/log :bar)
      (bus/unwatch! key)
      (bus/log :baz)
      (is (= [:foo :bar] @entries))
      (finally (reset-all!)))))

(deftest test-collector-and-friends
  (let [key :test-collector-and-friends]
    (bus/pop-collected!)
    (bus/watch! key bus/collector)
    (try
      (bus/log [:bus :foo])
      (bus/log [:bus :bar])
      (bus/log [:bus :baz])
      (is (= [[:bus :foo] [:bus :bar] [:bus :baz]] (bus/collected)))
      (bus/log [:bus :a])
      (bus/log [:bus :b])
      (bus/log [:bus :c])
      (is (= [[:bus :foo] [:bus :bar] [:bus :baz]
              [:bus :a] [:bus :b] [:bus :c]] (bus/pop-collected!)))
      (is (= [] (bus/pop-collected!)))
      (finally (reset-all!)))))

(deftest test-unwatch-all!
  (let [key1 :test-unwatch-all-key1
        key2 :test-unwatch-all-key2]
    (try
      (bus/unwatch-all!)
      (is (zero? (count (bus/watches))))
      (bus/watch! key1 bus/collector)
      (bus/watch! key2 bus/printer)
      (is (= 2 (count (bus/watches))))
      (bus/unwatch-all!)
      (is (zero? (count (bus/watches))))
      (finally (reset-all!)))))

(deftest test-watches
  (let [key1 :test-unwatch-all-key1
        key2 :test-unwatch-all-key2]
    (try
      (bus/unwatch-all!)
      (is (zero? (count (bus/watches))))
      (bus/watch! key1 bus/collector)
      (bus/watch! key2 bus/printer)
      (is (= 2 (count (bus/watches))))
      (bus/unwatch-all!)
      (is (zero? (count (bus/watches))))
      (finally (reset-all!)))))

(deftest test-replace!
  (try
    (reset! log/<log> identity)
    (bus/unwatch-all!)
    (is (not= @log/<log> bus/log))
    (bus/replace!)
    (is (= @log/<log> bus/log))
    (is (zero? (count (bus/watches))))
    (finally (reset-all!))))

(deftest test-wrap!
  (let [emitted (atom [])
        emitter #(swap! emitted conj %)]
    (try
      (log/set-level! :debug)
      (reset! log/<log> emitter)
      (bus/unwatch-all!)

      (is (= @log/<log> emitter))
      (log/debug :test)
      (is (= (count @emitted) 1))

      (bus/wrap!)
      (is (not= @log/<log> emitter))

      (log/debug :test)
      (is (= (count @emitted) 2))

      (is (zero? (count (bus/watches))))

      (finally (reset-all!)))))

;;(run-tests)

