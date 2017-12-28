<div align="center">
  <img src="docs/img/live.png" width="220px">
  <img src="docs/img/folio.png" width="220px">
  <img src="docs/img/alerts.png" width="220px">
</div>

### %name%

... is a cryptocurrency price tracking app. It is designed to work with multiple (possibly a lot) exchanges, allowing you to gain instights into price fluctuations at rapid pace.

### API

App uses one websocket and two http endpoints:


### Development

To start working in development mode you got to have and `boot`, and `electron` installed on your machine. This tutorial doesn't assume you use any particular text editor, so if you use Vim or Emacs and looking for advanced REPL features YMMV.

In first tab run:

```
boot watch dev-build
```

After compilation on ClojureScript is complete, in second tab run:

```
electron target/
```

At this point you can connect to `boot` REPL, and execute `(start-repl)` command:

```
boot repl -c
boot.user=> (start-repl)
```


### Lint and check

```
boot check-sources
```

### Format code

```
boot fmt --git --mode overwrite --really --options '{:style :community :map {:comma? false} :vector {:wrap? false}}'
```

### Packaging

[`electron-packager`](https://github.com/maxogden/electron-packager):

```
electron-packager target/ AppName --platform=darwin --arch=x64 --version=0.31.2
```

### Roadmap



### LICENCE

MIT
