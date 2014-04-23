REPL_PORT=8787

all: # This is a self documenting Makefile
	@cat Makefile

clean:
	lein clean
	/bin/rm -rf out

# For building once:

c:
	lein compile

t:
	lein test


# For building automatically while developing:

x:
	lein cljx auto

a:
	lein cljsbuild auto


# For a cljs REPL for dev:

# First:
repl-server:
	lein trampoline cljsbuild repl-listen

# Then:
repl-client:
	lein with-profile repl run $(REPL_PORT)

# Lastly:
repl-open:
	open http://localhost:$(REPL_PORT)

repl-all:
	make repl-server &
	make repl-client &
	make repl-open

