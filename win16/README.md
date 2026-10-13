# Win16 / ToolBook support

Loader fixes, Pascal calling-convention prototypes, ordinal maps, and the `Win16NEFixes` script.

## Layout

```
win16/
├── patches/
│   ├── ghidra-win16-fixes.patch
│   └── ghidra-win16-pascal-only.patch
├── scripts/
│   ├── Win16NEFixes.java
│   ├── Win16NEFixes.py
│   └── win16-ordinals.txt
└── docs/
    └── README-win16-fixes.md
```

See `docs/README-win16-fixes.md` for how to apply the patches and run the script.
