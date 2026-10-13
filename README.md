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
├── documentation/          # living notes
├── brainfuck/               # eight-command decompiling vanguard
├── intercal/                # COME FROM and politeness
├── whitespace/              # invisible source
├── lolcode/                 # HAI / KTHXBYE
├── piet/                    # programs as paintings
└── malbolge/                # refuses to decompile
```

## Quick start

```bash
export GHIDRA_HOME=/path/to/ghidra_12.3_DEV
./squirreldra
```

## Contents

- **win16/** — loader fixes, Pascal prototypes, ordinal maps, and `Win16NEFixes`
- **delphi567/** — empty scaffold for Delphi 5/6/7 helpers
- **brainfuck/**, **intercal/**, **whitespace/**, **lolcode/**, **piet/**, **malbolge/** — stub Ghidra scripts that acknowledge the language and then stop
