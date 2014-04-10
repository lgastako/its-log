(defproject its-log "0.1.0-SNAPSHOT"
  :description "It's Log.  Logging that's better than bad -- it's good!"
  :url "http://github.com/lgastako/its-log"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :plugins [[lein-cljsbuild "1.0.1"]
            [com.keminglabs/cljx "0.3.2"]
            [com.cemerick/clojurescript.test "0.2.2"]]
  :hooks [leiningen.cljsbuild
          cljx.hooks]
  :cljx {:builds [{:source-paths ["src"]
                   :output-path "target/generated/clj"
                   :rules :clj}
                  {:source-paths ["src"]
                   :output-path "target/generated/cljs"
                   :rules :cljs}
                  {:source-paths ["test"]
                   :output-path "target/generated-test/clj"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "target/generated-test/cljs"
                   :rules :cljs}]}
  :cljsbuild {:builds {:dev {:source-paths ["target/generated/cljs"]
                             :compiler {:output-to "target/itslog.js"
                                        :output-dir "target/"
                                        :optimizations :whitespace
                                        :pretty-print true
                                        :source-map "target/itslog.js.map"}}
                       :test {:source-paths ["target/generated/cljs"
                                             "target/generated-test/cljs"]
                              :compiler {:output-to "target/generated-test/js/tests.js"
                                         :output-dir "target/generated-test/js/"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :source-map "target/generated-test/js/tests.js.map"}}}
              :test-commands {"phantomjs" ["phantomjs" :runner
                                           "target/generated-test/js/tests.js"]}}
  :source-paths ["src/cljx"
                 "target/generated/clj"
                 "target/generated/cljs"]
  :test-paths ["target/generated-test/clj" "target/generated-test/cljs"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]])
