<div align="center">
  <img src="docs/img/live.png" width="220px" />
  <img src="docs/img/folio.png" width="220px" />
  <img src="docs/img/alerts.png" width="220px" />
</div>

### Cryptounicorns Desktop

[![Build Status](https://travis-ci.org/cryptounicorns/desktop.svg)](https://travis-ci.org/cryptounicorns/desktop)

Cryptounicorns Desktop is a cryptocurrency price tracking app. It is designed to work with multiple (possibly a lot) exchanges, allowing you to gain instights about price fluctuations at rapid pace. Long-term goal is to make approachable interface for vast majority of exchange markets.

### API

Current api root is `cryptounicorns.io` (about to change to `dev.cryptounicorns.io`, though):

##### Websocker stream

```
ws://%API_ROOT%/api/v1/tickers-changes/stream
```

##### Http endpoint with initial state

```
http://%API_ROOT%/api/v1/tickers-changes
```


### Development

To start working in development mode you got to have and `boot`, and `electron` installed on your machine. You also need to run `npm i` to get JavaScript some dependencies.

In first tab run:

```
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

## Deploy and packaging

This is not implemented yet. We are looking for you feedback and help (especially from windows folks)

### Packaging

We probably go with [`electron-packager`](https://github.com/maxogden/electron-packager) for packaging, which offers this kind of workflow:

```
electron-packager target/ APP_NAME --platform=darwin --arch=x64 --version=VERSION
```

