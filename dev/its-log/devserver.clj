(ns its-log.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn site [_]
   "<!DOCTYPE html>
<head>
    <title>its-log devserver</title>
<body>
    <script>console.log(\"whats up?\");</script>")

(defn serve [port] (run-jetty #'site {:port port}))
(defn -main [port] (serve (Integer/parseInt port)))

