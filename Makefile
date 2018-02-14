.DEFAULT_GOAL := all

include boot.properties

version            := $(shell jq -r .version package.json)
path               := $(PATH):$(PWD)/bin:$(PWD)/node_modules/.bin
env                ?= dev

export PATH=$(path)

.PHONY: all
all: build

.PHONY: fmt
fmt:
	boot fmt                       \
		--git --mode overwrite \
		--really --options '{:style :community :map {:comma? false} :vector {:wrap? false}}'

.PHONY: dependencies
dependencies:
	npm install

.PHONY: build
build: dependencies
	boot $(env)-build
	electron-packager                              \
		target/ MyApp                          \
		--platform=$(OSTYPE)                   \
		--arch=x64                             \
		--overwrite
