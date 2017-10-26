### Development

```
boot watch dev-build
```

```
electron target/                          # Do not omit the trailing '/'
```

### Packaging

[`electron-packager`](https://github.com/maxogden/electron-packager):

```
electron-packager target/ AppName --platform=darwin --arch=x64 --version=0.31.2
```

