(defproject its-log "0.2.0-bus-SNAPSHOT"
  :description "It's Log.  Logging that's better than bad -- it's good!"
  :url "http://github.com/lgastako/its-log"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :plugins [[lein-cljsbuild "1.0.3"]
            [com.keminglabs/cljx "0.3.2"]
            [com.cemerick/clojurescript.test "0.2.2"]]
  :hooks [leiningen.cljsbuild
          cljx.hooks]
  :cljx {:builds [{:source-paths ["src"]  :output-path "target/gen/src"  :rules :clj}
                  {:source-paths ["src"]  :output-path "target/gen/src"  :rules :cljs}
                  {:source-paths ["test"] :output-path "target/gen/test" :rules :clj}
                  {:source-paths ["test"] :output-path "target/gen/test" :rules :cljs}]}
  :cljsbuild {:builds {:dev {:source-paths ["dev" "target/gen/cljs"]
                             :compiler {:output-to "target/itslog.js"
                                        :output-dir "target/"
                                        :optimizations :whitespace
                                        :pretty-print true
                                        :source-map "target/itslog.js.map"}}
                       :test {:source-paths ["target/gen/cljs" "target/gen/test"]
                              :compiler {:output-to "target/gen/test/tests.js"
                                         :output-dir "target/gen/test/"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :source-map "target/gen/test/tests.js.map"}}}
              :test-commands {"phantomjs" ["phantomjs" :runner
                                           "target/gen/test/tests.js"]}}
  :source-paths ["src/cljx"
                 "target/gen/clj"
                 "target/gen/cljs"]
  :test-paths ["target/gen/test" "target/gen/test"]
  :main ^:skip-aot replsrv.devserver
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]])
