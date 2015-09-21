(defproject its-log "0.2.2"
  :description "It's Log.  Logging that's better than bad -- it's good!"
  :url "http://github.com/lgastako/its-log"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.5.1"
  :clean-targets ["target" "out"]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :cljsbuild {:builds {:test {:source-paths ["src"]
                              :test-paths ["test"]
                              :compiler {:output-to "target/itslog.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]])
