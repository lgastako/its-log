# its-log

A simple Clojure/ClojureScript logging library.

![](https://raw.github.com/lgastako/its-log/master/resources/log.jpg)

>     No man's knowledge here can go beyond his experience
>                                        - John Locke

## Usage

To use `its-log`, add the following to the `:dependencies` of your `project.clj`:

    [its-log "3.0"]

Then require its-log, in a namespace:

```clojure
(ns your.app
  :require [its.log :as log])
  ```

or in the REPL:

```clojure
(require '[its.log :as log])
```

At the most basic level its-log presents a fairly standard interface similar to
most other logging systems.

Usage is the same from either Clojure or ClojureScript:

```clojure
(log/set-level! :debug)  ;; default is warning
(log/debug "This is a debug message.  It will be seen.")

(log/set-level! :info)
(log/debug "This is a debug message.  It will NOT be seen.")

(log/error "This is an error message.  It will be seen.")

(log/set-level! :off)
(log/error "This is an error that will not be seen.  How sad.")
```

## Features

* Simplicity
* Logs are Clojure data structures.
* Identical API and semantics from Clojure and ClojureScript.
* Automatically enables console println in ClojureScript.
* Automatically enables node console println when running under nodejs.
* Simple standard logging levels: `:debug`, `:info`, `:warning`, `:error`, and `:off`
* Change log levels at runtime
* Add/remove/replace loggers (destinations) at runtime
* Helpers for parsing logs and accessing individual fields

### Setting Up Loggers

its-log v3 replaces the `<log>` atom from v2 with the notion of registering
loggers.  Loggers are identified by keywords.  By default a logger is
registered at `:its.log/default` that logs using `println`.

To set loggers, first require the `its.loggers` namespace in your app:

```clojure
(ns your.app
  :require [its.loggers :as loggers])
```

Or the REPL:

```clojure
(require '[its.loggers :as loggers])
```

To disable the default logger `(loggers/remove :its.log/default)`.

To register a new logger: `(loggers/set :my/logger foo-logger)`

Registering a new logger to the same key will replace the old one.

You can also use `(loggers/clear)` to clear all loggers.

## Log Entries

A log entry is the edn representation of the logging event.  Something like:

```clojure
[#inst "2014-04-22T02:04:40.074-00:00" :debug :foo :bar :baz :bif {:bam :boom}]
```

When its-log needs to emit a log entry it calls each registered logger with the
entry.  Order is not guaranteed.

## Dependencies

Right now its-log depends on core.async.  If you aren't interested in using
core.async you can exclude them in your project.clj with something like:

  ...
  :dependencies [its-log "x.x.x" :exclusions [org.clojure/core.async]]

TODO: I think... test if this does what I think it does.

## Target Audience

This library really evolved out of a combination my own needs and desires using
some combination of Clojure and ClojureScript to create homogeneous environments
across platforms (e.g. browser / web server / mobile).

It works well for my personal situation. It may not work as well for something
like an enterprise app running out of Immutant or another environment which has
specific requirements for a logging system that its-log doesn't support
(e.g. requiring log rotation or redirection, etc).

You might find its-log helpful specifically in situations like a multithreaded
environment where stdout is lost.

There is one common case where it will not work well right now which is in web
workers.  Right now each web worker gets it's own isolated bus and the messages
in the web workers never see the light of day.  I intend to fix this webworker
issue when/if it becomes pressing in my work.  Pull requests welcome :)

### Logs as Data

The first and probably biggest difference you will notice is in the output.  The
above produces these data structures:

```clojure
[#inst "2014-03-10T04:57:30.005-00:00" :debug "This is a debug message.  It will be seen."]
[#inst "2014-03-10T04:57:40.688-00:00" :error "This is an error message.  It will be seen."]
```

which serialize to this plain text:

```
[#inst "2014-03-10T04:57:30.005-00:00" :debug "This is a debug message.  It will be seen."]
[#inst "2014-03-10T04:57:40.688-00:00" :error "This is an error message.  It will be seen."]
```

Every logging system has it's own flavor of default logging format.  The flavor
of its-log's format is largely comes from of the delicious flavor of Rich
Hickey's magnificant Extensible Data Notation (EDN), upon which it is based.

Specifically each log line is a vector where the first and second arguments are
always the timestamp as a tagged inst and the log level as one of the four
keywords `:debug`, `:info`, `:warn` or `:error`.

This notion of "Logs as Data" (which you can hear more about
[Mark McGranahan's great 2013 talk](http://www.youtube.com/watch?v=rpmc-wHFUBs))
is very powerful.

It makes it easy to manipulate those data structures in order to answer questions.

```clojure
(ns my.app.stats
  (:require [its.log :as log]
            [its.parse :as parse))

(println (frequencies (map second (parse/slurp "my.app.log")))))
;; => {:info 28, :error 1, :warn 10, :debug 46}
```

You can also log an arbitrary number of arbitrarily complex nested data structures.

```clojure
(log/debug :process-dying {:pid pid
                           :load-info {:user-count (count users)
                                       :db-conns (count db-conns)
                                       :averages [0.1 0.09 0.8]}
                           :last-vacuum #inst "2014-03-10T03:02:53.305-00:00"}
                          [:heres :a :vector :just :because])
```

The above produces this:

```clojure
[#inst "2014-03-10T07:21:43.968-00:00" :debug :process-dying {:pid 52, :load-info {:user-count 837, :db-conns 19, :averages [0.1 0.09 0.8]}, :last-vacuum #inst "2014-03-10T03:02:53.305-00:00"} [:heres :a :vector :just :because]]
```

### Helper Functions

There are four helper functions available in the `its-log` namespace
corresponding to those same four levels: `log/debug`, `log/info`, `log/warn` and
`log/error`.  These (and/or the its.bus versions) are how I log the vast
majority of the time.

Behind the scenes, these are calling `log/log` which just takes one of the four
log level keywords as it's first argument, e.g.:

```clojure
(log/log :debug "hi there")
(log/log :warning "uh oh")
```

As opposed to:

```clojure
(log/debug "hi there")
(log/warn "uh oh")
```

Most people will probably find the helper functions more "natural", at least at
first.  The `log/log` remains helpful if you want to e.g. programatically
determine the log level of a statement:

```clojure
(log/log (config/get :reactor-log-level) "reactor override engaged")
```

In the real world you probably wouldn't call the `config/get` function inside
each log call but hopefully you get the idea.

If your fingers are tired and you would like even less typing, or perhaps you
have an irrational aversion to the `/` character, you can always refer to the
level helpers directly, something like:

```clojure
(ns my.app
  (:require [its.log :as log :refer [log debug warn info error]]))

;; Now you can...
(log :debug "whatever")
(debug "whatever else")
(info "Some handy info")
(warn "This is bad.")
(error "You didn't listen, did you?")
```

## Core.Async

```clojure
(ns your.ns
 :require [its.async :as async-log])
          [its.log :as log])

(let [log-chan ...]
  (log/set-logger :async (async-log/make log-chan))
  ...)
```

## Parsing

The functions in the `its.parse` namespace are helpful for parsing logs
produced by `its.log` back into Clojure data structures.

First, require the `its.parse` namespace, in your namespace:

```clojure
(ns your.app
  :require [its.parse :as parse])
```

or in the REPL:

```clojure
(require '[its.parse :as parse])
```

Use `parse/line` to parse a single log line and get back a single Clojure
vector representing that line:

```clojure
(parse/line "[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing \"AcmeApp v3.13.7\"]")

;; => [#inst "2014-03-10T03:02:50.855-00:00" :info :initializing \"AcmeApp v3.13.7\"]
```

or `parse/line` if you have a sequence of lines:

```clojure
(parse/lines ["[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing \"AcmeApp v3.13.7\"]"
              "[#inst "2014-03-10T03:02:50.861-00:00" :debug :gexposing {:as \"load_users\"}]"])

;; => [[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing "AcmeApp v3.13.7"]
;;     [#inst "2014-03-10T03:02:50.861-00:00" :debug :gexposing {:as "load_users"}]]
```

or `parse/log` if you have a string (e.g. read from a file) of concatenated log strings:

```clojure
(parse/log (slurp "example.log"))

;; => [[#inst "2014-03-10T03:02:50.855-00:00" :info :initializing "AcmeApp v3.13.7"]
;;     [#inst "2014-03-10T03:02:50.861-00:00" :debug :gexposing {:as "load_users"}]
;;     [#inst "2014-03-10T03:02:51.407-00:00" :debug :loading-usercache {:count 1037}]
;;     [#inst "2014-03-10T03:02:51.407-00:00" :debug :loaded-usercache {:count 1037}]
;;     [#inst "2014-03-10T03:02:51.407-00:00" :warn :usercache {:recommended-size-threshold {:xceeded-by 37}}]
;;     [#inst "2014-03-10T03:02:53.383-00:00" :debug :autocomplete/view :render-state {:label "Search by substring", :placeholder "", :input-ref "autocomplete"}]]
```

## Accessing parts of log lines as clojure data structures

Once you have log lines as clojure data structures the `its.access` namespace
provides helpers for easy access to the individual fields:

Do the usual require dance in your namespace:

```clojure
(ns your.app
  (:require [its.access :as access]))
```

or the REPL:

```clojure
(require '[its.access :as access])
```

And then, assuming:

```clojure
(def line [#inst "2014-03-10T03:02:51.407-00:00" :debug :loading-usercache {:count 1037}])
```

You can access the individual pieces like so:

```clojure
(access/timestamp line)  ;; => #inst "2014-03-10T03:02:51.407-00:00"
(access/level line)      ;; => :debug
(access/message line)    ;; => :loading-usercache
(access/args line)       ;; => [{:count 1037}]
```

Or check for specific levels:

```clojure
(access/debug? line)   ;; => true
(access/info? line)    ;; => true
(access/warn? line)    ;; => true
(access/error? line)   ;; => true
```

Or use the plural versions to filter lists of lines:

Assuming the lines from the test namespace `its.examples`:

```clojure
(access/debugs lines)
;; => [[#inst "2014-03-10T03:02:51.407-00:00" :debug :loading-usercache {:count 1037}]]

(access/warns lines)
;; => [[#inst "2014-03-10T03:02:52.702-00:00" :warn :the-roof [:the-roof] [[:the-roof]] {:is :on-fire}]]
```
## License

Copyright © 2014-2015 John Evans

Distributed under the Eclipse Public License, the same as Clojure.
