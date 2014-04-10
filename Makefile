all: # This is a self documenting Makefile
	@cat Makefile

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
