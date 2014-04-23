(ns its.devserver
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn site [_]
   {:status 200
    :headers {"Content-Type" "text/html"}
    :body "<!DOCTYPE html>
<head>
    <title>its-log devserver</title>
<body>
    <p>This is the devserver.  If you're seeing this your REPL should be connected.</p>
    <script src=\"/itslog-repl.js\"></script>"})

(defn serve [port] (run-jetty #'site {:port port}))
(defn -main [port] (serve (Integer/parseInt port)))

