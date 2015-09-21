# workaround for https://github.com/technomancy/leiningen/issues/1940
NAMESPACES=its.access-tests \
		   its.async-tests \
		   its.compat-tests \
		   its.log-tests \
		   its.loggers-tests \
		   its.parse-tests

all: # This is a self documenting Makefile
	@cat Makefile

clean:
	lein clean

# For building once:

a:
	lein ancient

at:
	lein auto test $(NAMESPACES)

c:
	lein compile

cs:
	lein cljsbuild once

cov:
	lein cloverage

deps:
	lein deps

e:
	lein eastwood

k:
	lein kibit

p:
	lein pprint

r:
	lein repl

rs:
	lein trampoline run -m clojure.main node_repl.clj

t:
	lein test $(NAMESPACES)

ta:
	lein auto test $(NAMESPACES)

ts:
	lein cljsbuild auto test

v:
	lein vanity


.PHONY: clean cov deps
.PHONY: a at c cs e k p t ta ts v
