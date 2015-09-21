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

t:
	lein test $(NAMESPACES)

v:
	lein vanity


.PHONY: clean cov deps
.PHONY: a at c e k p t v
