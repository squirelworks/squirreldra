# Squirreldra

Thin launcher and ToolBook/Win16 workflow layer on top of Ghidra.

## What it does

- Shows a short “Squirreldra” splash screen
- Starts Ghidra (headless or GUI)
- Ships the Win16 Pascal prototypes patch, ordinal maps, and helper script

## Layout

```
squirreldra/
├── squirreldra              # launcher (Python)
├── scripts/
│   ├── Win16NEFixes.py
│   └── win16-ordinals.txt
├── patches/
│   └── ghidra-win16-pascal-only.patch
└── docs/
```

## Quick start

```bash
# Point at your Ghidra install / build
export GHIDRA_HOME=~/ghidra-win16/ghidra

# Run
./squirreldra
```

Or place the `squirreldra` script next to a `ghidra/` directory and run it directly.

## Requirements

- Python 3
- tkinter (optional; falls back to a terminal banner if missing)
- A Ghidra build that includes the Pascal prototypes (see `patches/`)

## Notes

This is a scaffold. The launcher does not yet embed Ghidra or implement custom views; it only provides the splash and a clean entry point for the ToolBook/Win16 workflow.
