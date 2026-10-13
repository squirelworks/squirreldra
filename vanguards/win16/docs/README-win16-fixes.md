# Win16 Ghidra Improvements

## Files

| File | Purpose |
|------|---------|
| `patches/ghidra-win16-fixes.patch` | Combined loader fixes + Pascal prototypes |
| `patches/ghidra-win16-pascal-only.patch` | Pascal prototypes only |
| `scripts/win16-ordinals.txt` | Partial ordinal → name maps |
| `scripts/Win16NEFixes.java` | Rename ordinals, apply Pascal, promote far pointers |
| `scripts/Win16NEFixes.py` | Python version of the same script |

## Apply

```bash
cd /path/to/ghidra
git apply /path/to/squirreldra/vanguards/win16/patches/ghidra-win16-fixes.patch
./gradlew buildGhidra
```

Then copy `Win16NEFixes.java` (or `.py`) and `win16-ordinals.txt` into a Ghidra scripts directory and run the script on an open NE.
