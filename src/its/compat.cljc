(ns its.compat)

#?(:clj  (defn index-of [xs x]
           (.indexOf xs x)))

#?(:cljs (defn index-of
           ([xs x]
            (index-of xs x 0))
           ([xs x idx]
            (cond
              (empty? xs)       -1
              (= x (first xs))  idx
              :else             (recur (rest xs) x (inc idx))))))

#?(:clj  (defn now [] (new java.util.Date)))

#?(:cljs (defn now [] (new js/Date)))
