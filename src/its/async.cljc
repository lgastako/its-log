(ns its.async
  (:require #?(:clj  [clojure.core.async :as async :refer [>! chan put! go]])
            #?(:cljs [cljs.core.async    :as async :refer [>! chan put!]]))
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go]])))

(defn make-log
  ([c] (partial put! c)))
