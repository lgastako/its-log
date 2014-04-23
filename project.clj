(defproject its-log "0.2.0-bus-SNAPSHOT"
  :description "It's Log.  Logging that's better than bad -- it's good!"
  :url "http://github.com/lgastako/its-log"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :plugins [[lein-cljsbuild "1.0.2"]
            [com.keminglabs/cljx "0.3.2"]
            [com.cemerick/clojurescript.test "0.2.3"]]
  :hooks [cljx.hooks leiningen.cljsbuild]
  :cljx {:builds [{:source-paths ["src"]  :output-path "target/gen/src"  :rules :clj}
                  {:source-paths ["src"]  :output-path "target/gen/src"  :rules :cljs}
                  {:source-paths ["test"] :output-path "target/gen/test" :rules :clj}
                  {:source-paths ["test"] :output-path "target/gen/test" :rules :cljs}]}
  :cljsbuild {:builds {:dev {:source-paths ["target/dev/gen/src"]
                             :compiler {:output-to "target/dev/itslog.js"
                                        :output-dir "target/dev"
                                        :optimizations :whitespace
                                        :pretty-print true
                                        :source-map "target/dev/itslog.js.map"}}
                       :repl {:source-paths ["target/repl/gen/src"]
                              :compiler {:output-to "resources/public/itslog-repl.js"
                                         :output-dir "resources/public"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :source-map "resources/public/itslog.js.map"}}
                       :test {:source-paths ["target/test/gen/src"
                                             "target/test/gen/test"]
                              :compiler {:output-to "target/test/tests.js"
                                         :output-dir "target/test"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :source-map "target/test/tests.js.map"}}}
              :test-commands {"phantomjs" ["phantomjs"
                                           :runner
                                           "bind-polyfill.js"
                                           "target/gen/test/tests.js"]}}
  :source-paths ["src" "target/gen/src"]
  :test-paths ["test" "target/gen/test"]
  :profiles {:repl {:source-paths ["dev"]
                    :dependencies [[ring "1.2.2"]]
                    :main ^:skip-aot its.devserver}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]])
