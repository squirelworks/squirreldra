# Win16 Patches

## Combined patch

`patches/ghidra-win16-fixes.patch` contains three changes:

1. **EntryTable.java** — honor `ne_cbenttab` so the loader stops at the end of the entry table instead of reading until a zero count.
2. **NeLoader.java** — treat the entry-bundle type byte as unsigned and bounds-check the segment index. Prevents `Index -122` on segment numbers ≥ 128.
3. **x86-16.cspec** — add `__pascal16far` and `__pascal16near` prototypes.

## Pascal-only patch

`patches/ghidra-win16-pascal-only.patch` contains only the cspec change. Use it when the binary has already been pre-processed by toolbookunpacker (`patch_ne_entry.py` / `ghidra_cmp.py`).

## Duplicate prototypes

If the cspec is patched more than once, Ghidra refuses to load with:

```
Multiple prototype models with the name: __pascal16near
```

Keep exactly one of each prototype. A small Python filter can remove extras; see the conversation notes for an example.

## Script

`scripts/Win16NEFixes.py` (with `win16-ordinals.txt`) :

- Renames `Ordinal_N` symbols in KERNEL/USER/GDI namespaces
- Applies `__pascal16far` to a short list of known APIs
- Heuristically promotes some 4-byte values to pointer32

Run it from the Script Manager after the binary is loaded.
