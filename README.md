# Squirreldra

Personal tools, fixes, and patches for reverse engineering with Ghidra.

## Layout

```
squirreldra/
├── squirreldra              # launcher (splash + ghidraRun)
├── vanguards/               # language-oriented helpers and stubs
│   ├── win16/              # Win16 / ToolBook
│   ├── delphi567/         # Delphi 5/6/7
│   ├── brainfuck/
│   ├── intercal/
│   ├── whitespace/
│   ├── lolcode/
│   ├── piet/
│   └── malbolge/
├── debugging/               # Win98/XP debugger notes and minidbg scaffold
└── documentation/          # living notes
```

## Quick start

```bash
export GHIDRA_HOME=/path/to/ghidra
./squirreldra
```

Language-specific patches, scripts, and stubs live under `vanguards/`.
Debugging notes for Win98/XP live under `debugging/`.
