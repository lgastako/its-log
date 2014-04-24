(ns its.autoconnect
  (:require [clojure.browser.repl :as repl]))

;; Use of "localhost" will only work for local development.
;; Change the port to match the :repl-listen-port.
(let [url "http://localhost:9000/repl"]
  (repl/connect url)
  (.log js/console "REPL Connected to: " url))
