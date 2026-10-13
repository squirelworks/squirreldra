# Squirreldra

Personal tools, fixes, and patches for Win16 / ToolBook analysis on top of Ghidra.

## What it does

- Shows a short “Squirreldra” splash screen
- Starts Ghidra (distribution or source-tree layout)
- Ships Win16 loader fixes, Pascal calling-convention prototypes, ordinal maps, and a helper script

## Layout

```
squirreldra/
├── squirreldra                          # launcher
├── scripts/
│   ├── Win16NEFixes.py
│   └── win16-ordinals.txt
├── patches/
│   ├── ghidra-win16-fixes.patch          # loader + Pascal prototypes
│   └── ghidra-win16-pascal-only.patch    # Pascal prototypes only
└── docs/
    └── README-win16-fixes.md
```

## Quick start

```bash
# Point at an extracted Ghidra distribution or a source tree
export GHIDRA_HOME=~/ghidra-win16/ghidra/build/dist/ghidra_12.3_DEV

# Run
./squirreldra
```

## Applying the patches

See `docs/README-win16-fixes.md` for details.

```bash
cd /path/to/ghidra
git apply /path/to/squirreldra/patches/ghidra-win16-fixes.patch
./gradlew buildGhidra
```

## Requirements

- Python 3
- tkinter (optional; falls back to a terminal banner)
- A Ghidra build

## Notes

This is the repository for our personal Ghidra tools, loader fixes, and Win16 analysis helpers. The launcher does not embed Ghidra; it only provides the splash and a clean entry point.
