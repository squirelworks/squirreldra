# Squirreldra Launcher

`squirreldra` is a small Python script that shows a splash and then execs `ghidraRun`.

## Locating Ghidra

It accepts either:

- **Distribution layout**: `$GHIDRA_HOME/ghidraRun`
- **Source-tree layout**: `$GHIDRA_HOME/Ghidra/RuntimeScripts/Linux/ghidraRun`

Set `GHIDRA_HOME` to the extracted distribution (the usual case after `./gradlew buildGhidra`):

```bash
export GHIDRA_HOME=~/ghidra-win16/ghidra/build/dist/ghidra_12.3_DEV
./squirreldra
```

## Permissions

A freshly extracted distribution often has scripts and native helpers without the execute bit. Fix once:

```bash
chmod +x $GHIDRA_HOME/ghidraRun $GHIDRA_HOME/support/*.sh
find $GHIDRA_HOME -type f -name "demangler*" -exec chmod +x {} \;
```

## Splash

- tkinter window when available
- Terminal banner otherwise
- Duration controlled by `SQUIRRELDRA_SPLASH` (seconds, default 2.0)
