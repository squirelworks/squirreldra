# Win16 Ghidra Improvements — Implementation Notes

These artifacts implement the practical fixes discussed for better Win16 (NE) decompilation in Ghidra.

## Files

| File | Purpose |
|------|---------|
| `patches/ghidra-win16-fixes.patch` | Combined loader fixes + Pascal prototypes |
| `patches/ghidra-win16-pascal-only.patch` | Pascal prototypes only (no loader changes) |
| `scripts/win16-ordinals.txt` | Partial ordinal → name maps for KERNEL, USER, GDI |
| `scripts/Win16NEFixes.py` | Ghidra script: rename ordinals, apply Pascal convention, promote far pointers |

## How to apply

### 1. Combined patch (loader + calling convention)
```bash
cd /path/to/ghidra
git apply /path/to/patches/ghidra-win16-fixes.patch
./gradlew buildGhidra
```

### 2. Pascal-only (if you already have loader workarounds)
```bash
git apply /path/to/patches/ghidra-win16-pascal-only.patch
```

### 3. Ordinal maps + script
- Copy `scripts/Win16NEFixes.py` and `scripts/win16-ordinals.txt` into your Ghidra scripts folder
- Open an NE binary
- Script Manager → run `Win16NEFixes.py`

## Limitations

- `__pascal16far` left-to-right parameter ordering may still require manual signature overrides.
- Far-pointer promotion is a simple heuristic; review and refine.
- Ordinal maps are partial; extend `win16-ordinals.txt` with full dumps from real 16-bit system DLLs.
- Applying the cspec portion more than once will produce duplicate prototype errors. Keep only one copy of each prototype.

## Expected improvement

After applying the loader patch and running the script on a typical Win16 NE:
- Entry points and segments load without sign-extension crashes
- Many KERNEL/USER/GDI ordinals become named APIs
- Known APIs get the Pascal convention
- Some far pointers are typed as 4-byte pointers, improving decompiler output
