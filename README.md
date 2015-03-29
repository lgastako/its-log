# its-log

A simple cljx logging library.

![](https://raw.github.com/lgastako/its-log/master/resources/log.jpg)

>     No man's knowledge here can go beyond his experience
>                                        - John Locke

## Features

* Logs are (Clojure) data structures.
* Identical API and semantics from Clojure and ClojureScript.
* Automatically enables console println in ClojureScript.
* Simple standard logging levels: `:debug`, `:info`, `:warning`, `:error`, and `:off`
* Change log levels at runtime
* its.bus: (optional) Shared logging bus

## Goals

I am trying to keep a few explicit goals in mind as its-log evolves.

### Simplicity

My primary goal with its-log is to keep things as simple as possible.  I want as
little code as possible with as little surface area as possible, as few concepts
as possible, as few options as possible, etc.

### Extensibility

Assuming we don't have to sacrafice simplicity I want things to be as extensible
as possible.  I think I've acheived this as well as can reasonably be
accomplished right now with the its.log/&lt;log&gt; atom.

The its.log/<log> dynamic variable is where the rubber meets the road in terms
of a log entry being "emitted."  The ultimate goal of a call to one of its-log's
logging functions is to have a log entry emitted.

A log entry is the edn representation of the logging event.  Something like:

```clojure
[#inst "2014-04-22T02:04:40.074-00:00" :debug :foo :bar :baz :bif {:bam :boom}]
```

When its-log needs to emit a log entry, it calls the unary function stored in
the its.log/&lt;log&gt; atom.  The default function is `(comp println pr-str)` which
formats the arguments using pr-str then prints them using println.

You can replace this function with any function you want to have complete
control over anything you want, including formatting or destination of logs,
etc.

For example, the its-log logging operates by swapping in it's own its.log/&lt;log&gt;
logging function.

## Tradeoffs

Striving for simplicity I am intentionally avoiding many areas of log related
concerns (at least for now).

Specifically, its-log:

* Does not do anything clever like take advantage of macros to remove the code
  completely when the log level is set too low, prefering instead simplicity of
  implementation and the ability to change log level at run time.
* Only provides two types of basic loggers:
  - println
  - its.bus
  (...but trivially easy to extend in Clojure and/or ClojureScript!)
* Provides nothing for log rotation (or anything to do with files really).

## Dependencies

Right now its-log depends on ClojureScript and core.async.  If you aren't
interested in one or the other or both you can exclude them in your project.clj
with something like:

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

You might find its-bus helpful specifically in situations like a multithreaded
environment where stdout is lost.

There is one common case where it will not work well right now which is in web
workers.  Right now each web worker gets it's own isolated bus and the messages
in the web workers never see the light of day.  I intend to fix this webworker
issue when it becomes pressing in my work.

## Usage

At the most basic level its-log presents a fairly standard interface similar to
most other logging systems.

Usage is the same from either Clojure or ClojureScript:

```clojure
(ns my.app
  (:require [its.log :as log]))

(log/set-level! :debug)  ;; default is warning
(log/debug "This is a debug message.  It will be seen.")

(log/set-level! :info)
(log/debug "This is a debug message.  It will NOT be seen.")

(log/error "This is an error message.  It will be seen.")

(log/set-level! :off)
(log/error "This is an error that will not be seen.  How sad.")
```

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

Behind the scenes, these are calling log/log which just takes one of the four
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

## its.bus: The Shared Logging Bus

You can activate its.bus with either the bus/replace! or bus/wrap! functions.

replace! installs a logging handler that routes messages only to the shared
logging bus.

wrap! installs a logging handler that sends messages both to the shared logging
bus and then to the original handler.

For example, in a scenario where no other configuration of the logging system
has taken place, a call to replace! will prevent any logging messages from
appearing on any stdout anywhere.  And in fact until you tap the bus all logging
messages will fall into the bit bucket.

### Tapping that bus

To bring back stdout logging after, eg., its.bus/replace!-ing you can bus/watch!:

```clojure
(bus/watch!)
```

This actually has a slightly different effect than using its.bus/wrap!

For example, if you wrap! the logger in a multithreaded application where you
are only seeing logging from one of the threads on stdout then after wrap!-ing
you will still not see the log messages from the other threads.  If on the other
hand you replace! then bus/watch! you'll see all messages from all threads on
your current stdout.

### Tapping that bus... async style:

```clojure
(ns your.app
  (require [its.bus.async :refer [chan]]))

;; Manually log messages from its.bus via println via a go block for
;; demonstration purposes.
(let [logs (bus/chan)]
  (go (while true (println (<! logs)))))

 Control will resume here immediately, but from now on everything that hits
;; the bus will be printed via println on the stdout of whatever that code was
;; executed, e.g. in the repl.
```

### Development

You can get a cljs repl for interactive development by first running the
trampoline REPL:

### Implementation / Tradeoffs

For now the concurrency mechanism that backs its.bus in both Clojure and
ClojureScript is our stalwart friend the atom.

I had nearly implemented a first version based on `alter-var-root` before
realizing that ClojureScript doesn't have `alter-var-root`.  Doh.

I thought maybe I'll leave it in the Clojure version and in the ClojureScript
version use an atom or something else.  But then I thought, the atom will be
easy, good enough, and work nearly identically in both, so the atom it is.

I welcome any suggestions (or pull requests...) for better alternatives that
don't require ridiculous cljx contortions.

## Parsing

    Still coming soon.

## License

Copyright © 2014 John Evans

Distributed under the Eclipse Public License, the same as Clojure.
