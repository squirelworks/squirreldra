# Brainfuck Decompiling Vanguard

A serious attempt to reverse-engineer Brainfuck programs with Ghidra.

Brainfuck has eight instructions. This is already more than most malware needs.

## Layout

```
brainfuck/
├── README.md
├── scripts/
│   └── BrainfuckDecompiler.java   # turns ,.-+[]<> into something almost readable
└── notes/
    └── eight-commands.md
```

## Status

Scaffold. The decompiler script is a stub that currently prints the eight commands and refuses to go further on principle.

## Philosophy

If your binary only uses eight opcodes, Ghidra is overqualified. We are here anyway.
