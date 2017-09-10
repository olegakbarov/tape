.DEFAULT_GOAL := all

include boot.properties

boot_installer_url := https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh

name               := MyApp
electron_version   ?= 0.31.2
env                ?= dev
os                 ?= $(shell uname -s | awk '{print tolower($$0)}')
arch               ?= x64
version            := $(shell jq -r .version package.json)
path               := $(PWD)/bin:$(PWD)/node_modules/.bin:$(PATH)

export PATH=$(path)

all: build

.PHONY: boot
boot:
	@if [ ! -e ~/.boot/cache/bin/$(BOOT_VERSION)/boot.jar ]; \
	then                                                     \
		echo "Boot is installing..." 1>&2                \
		&& curl -Ls $(boot_installer_url)                \
			> boot-install                           \
		&& chmod          +x boot-install                \
		&& ./boot-install > /dev/null                    \
		&& rm             -f boot-install                \
		;                                                \
	else                                                     \
		echo "Boot already installed" 1>&2               \
		;                                                \
	fi

.PHONY: dependencies
dependencies: boot
	npm install

.PHONY: build
build: dependencies
	boot $(env)-build
	electron-packager                              \
		target/ $(name)                        \
		--platform=$(os)                       \
		--arch=$(arch)                         \
		--electron-version=$(electron_version) \
		--overwrite

.PHONY: run
run: run-$(os)

.PHONY: run-linux
run-linux:
	# I just love to isolate things :)
	bwrap                                          \
		--ro-bind   /            /             \
		--bind      $(shell pwd) /home/$(USER) \
		--proc      /proc                      \
		--dev       /dev                       \
		--share-net                            \
		/home/$(USER)/$(name)-$(os)-$(arch)/$(name)

.PHONY: run-darwin
run-darwin:
	./$(name)-$(os)-$(arch)/$(name)
