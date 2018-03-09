<div align="center">
  <img src="docs/img/live.png" width="220px" />
  <img src="docs/img/folio.png" width="220px" />
  <img src="docs/img/alerts.png" width="220px" />
</div>

## Cryptounicorns Desktop

[![Build Status](https://travis-ci.org/cryptounicorns/desktop.svg)](https://travis-ci.org/cryptounicorns/desktop)

Cryptounicorns Desktop is a cryptocurrency price tracking app. It is designed to work with multiple (possibly a lot) exchanges, allowing you to gain instights about price fluctuations at rapid pace. Long-term goal is to make approachable interface for vast majority of exchange markets.

## Development

To start working in development mode you got to have and `boot`, and `electron` installed on your machine. You also need to run `npm i` to get JavaScript dependencies. Rename your `run.sh.example` to `run.sh` and populate it with env variables:

In first tab run:

```
SENTRY=""
WS_ENDPOINT=""
HTTP_ENDPOINT=""
boot watch dev-build
```

After compilation of ClojureScript is complete, in second(!) tab run:

```
electron target/
```

At this point you can connect to `boot` REPL, and execute `(start-repl)` command:

```
boot repl -c
boot.user=> (start-repl)
```

## Code style

### Lint and check and format code

```
boot check-sources
```

```
make fmt
```

### Dependencies

Check outdated deps

```
boot -d boot-deps ancient
```

