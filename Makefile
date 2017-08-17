.DEFAULT_GOAL := all

include boot.properties

boot_installer_url := https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh
version            := $(shell jq -r .version package.json)
path               := $(PWD)/bin:$(PWD)/node_modules/.bin:$(PATH)
electron_version   ?= 0.31.2
env                ?= dev

export PATH=$(path)

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
		target/ MyApp                          \
		--platform=$(OSTYPE)                   \
		--arch=x64                             \
		--electron-version=$(electron_version) \
		--overwrite
