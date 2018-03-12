.DEFAULT_GOAL := all

include boot.properties

name    := unicorneye
version := $(shell jq -r .version package.json)
path    := $(PATH):$(PWD)/bin:$(PWD)/node_modules/.bin
env     ?= dev

export PATH=$(path)

.PHONY: all
all: build

.PHONY: fmt
fmt:
	boot fmt                 \
		--git --mode overwrite \
		--really --options '{:style :community :map {:comma? false :force-nl? true} :vector {:respect-nl? true}}'

.PHONY: dependencies
dependencies:
	npm install

.PHONY: build
build: dependencies
	boot $(env)-build
	electron-packager       \
		target/ build         \
		--platform=$(OSTYPE)  \
		--arch=x64            \
		--overwrite

