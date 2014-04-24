(ns its.devserver
  (:gen-class)
  (:require [compojure.core :refer [ANY defroutes]]
            [compojure.route :refer [resources]]
            [ring.adapter.jetty :refer [run-jetty]]))

(def home-page {:status 200
                :headers {"Content-Type" "text/html"}
                :body "<!DOCTYPE html>
<head>
    <title>its-log devserver</title>
<body>
    <p>This is the devserver.  If you're seeing this your REPL should be connected.</p>
    <script src=\"/itslog-repl.js\"></script>"})

(defroutes site
  (ANY "/" [] home-page)
  (resources "/"))

(defn serve [port] (run-jetty #'site {:port port}))
(defn -main [port] (serve (Integer/parseInt port)))

