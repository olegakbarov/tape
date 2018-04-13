#!/usr/bin/env bash

# since clojurescript does not take my pull requests here's some old school moves:
boot prod-build && sed -i '' 's/"."/__dirname/g' target/main.js

