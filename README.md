# its-log

A simple cljx logging library.

![](https://raw.github.com/lgastako/its-log/tmp/resources/log.jpg)

>     No man's knowledge here can go beyond his experience
>                                        - John Locke

## Features

* Logs are (Clojure) data structures.
* Identical API and semantics from Clojure and ClojureScript.
* Automatically enables console println in ClojureScript.
* Simple standard logging levels: `:debug`, `:info`, `:warning`, `:error`, and `:off`
* Change log levels at runtime

## Non-Features:

* Does not do anything clever like take advantage of macros to remove the code
  completely when the log level is set too low, prefering instead simplicity of
  implementation and the ability to change log level at run time.
* Does not have multiple types of loggers or support multiple destinations.
* No log rotation or other outside concerns.

## Target Audience

This library really evolved out of a combination my own needs and desires using
some combination of Clojure and ClojureScript to create homogeneous environments
across platforms (e.g. browser / web server / mobile).

It works well for my personal situation. It may not work as well for an
enterprise app running out of Immutant or another environment which has specific
requirements for a logging system that its-log doesn't support (e.g. requiring
log rotation or redirection, etc).

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

The above produces the below.

```clojure
[#inst "2014-03-10T07:21:43.968-00:00" :debug :process-dying {:pid 52, :load-info {:user-count 837, :db-conns 19, :averages [0.1 0.09 0.8]}, :last-vacuum #inst "2014-03-10T03:02:53.305-00:00"} [:heres :a :vector :just :because]]
```

### Helper Functions

There are four helper functions available in the `its-log` namespace
corresponding to those same four levels: `log/debug`, `log/info`, `log/warn` and
`log/error`.

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

## Parsing

    Coming soon.

## License

Copyright © 2014 John Evans

Distributed under the Eclipse Public License, the same as Clojure.
