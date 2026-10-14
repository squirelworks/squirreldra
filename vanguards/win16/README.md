# Win16 / ToolBook support

Loader fixes, Pascal calling-convention prototypes, ordinal maps, and the `Win16NEFixes` script.

## Layout

- `scripts/` — Win16NEFixes.py / .java and ordinal map
- `patches/` — Ghidra loader and related patches
- `docs/` — application notes
- `setup/` — build script and related setup files
- `x86-16-pascal-prototypes.xml` — calling-convention prototypes

See `docs/README-win16-fixes.md` for how to apply the patches and run the script.

## Force-disassemble helpers

- `ForceDisassembleC8.java` — force disassembly on every undefined `C8` (ENTER)
- `ForceDisassemblePrologues.java` — classic prologues, Win16 export prologues, post-RET, and NE entry points

Run C8 first, then Prologues. Both print the number of functions created. See the matching .md file next to each script for the rationale.
