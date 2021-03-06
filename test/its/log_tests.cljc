(ns its.log-tests
  (:require #?(:clj [clojure.test :refer :all])
            #?(:cljs [cemerick.cljs.test :as t :refer-macros [deftest is]])
            #?(:clj [clojure.edn :as edn])
            #?(:cljs [cljs.reader :as cljs-reader])
            [clojure.string :as string]
            [its.log :as log :refer [log]]
            [its.loggers :as loggers]
            [its.test-helpers :refer [unstamped]]))

(deftest test-basics
  (loggers/set-default)
  (log/set-level! :debug)
  (is (= [:debug "gin and juice"]
         (unstamped #(log :debug "gin and juice"))))
  (is (= [:warn "with my mind on my money"]
         (unstamped #(log :warn "with my mind on my money"))))
  (is (= [:error "and my money on my mind"]
         (unstamped #(log :error "and my money on my mind"))))

  (log/set-level! :info)
  (is (= [] (unstamped #(log :debug "never say never"))))
  (is (= [:info "top gun"]
         (unstamped #(log :info "top gun"))))
  (is (= [:warn "chirped out"]
         (unstamped #(log :warn "chirped out"))))
  (is (= [:error "didn't even have to use my ak"]
         (unstamped #(log :error "didn't even have to use my ak"))))

  (log/set-level! :warn)
  (is (= [] (unstamped #(log :debug "beauty and a beat"))))
  (is (= [] (unstamped #(log :info "baby"))))
  (is (= [:warn "the way I am"]
         (unstamped #(log :warn "the way I am"))))
  (is (= [:error "whatever you say I am"]
         (unstamped #(log :error "whatever you say I am"))))

  (log/set-level! :error)
  (is (= [] (unstamped #(log :warn "Eenie Meenie"))))
  (is (= [] (unstamped #(log :warn "One Time"))))
  (is (= [] (unstamped #(log :warn "One Less Loney Girl"))))
  (is (= [:error "if I wasn't, why would I say I am?"]
         (unstamped #(log :error "if I wasn't, why would I say I am?"))))

  (log/set-level! :off)
  (is (= [[] [] [] []]
         (map (fn [level] (unstamped #(log level "beibs")))
              (butlast log/levels)))))


;; (run-tests)
