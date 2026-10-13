# Squirreldra

Personal tools, fixes, and patches for reverse engineering with Ghidra.

## Layout

```
squirreldra/
├── squirreldra              # launcher (splash + ghidraRun)
├── win16/                  # Win16 / ToolBook support
│   ├── patches/
│   ├── scripts/
│   └── docs/
├── delphi567/              # Delphi 5/6/7 scaffold
│   ├── scripts/
│   ├── notes/
│   └── sigs/
└── documentation/          # living notes
```

## Quick start

```bash
export GHIDRA_HOME=/path/to/ghidra_12.3_DEV
./squirreldra
```

Win16 patches and the `Win16NEFixes` script live under `win16/`.
