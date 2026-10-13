# Suggested Workflow

1. Unpack the ToolBook NE with toolbookunpacker (`unpack_optloader.py`, then `patch_ne_entry.py` or `ghidra_cmp.py` if you are not using the loader source fixes).
2. Build or obtain a Ghidra that includes the Pascal prototypes (and the loader fixes if you want to skip binary pre-processing).
3. Launch via `./squirreldra` (or `ghidraRun` directly).
4. Import the NE.
5. Run `Win16NEFixes.py` to rename ordinals and apply `__pascal16far`.
6. Attach Function ID databases if available (e.g. from `moralrecordings/ghidra-fidb-dos-win16`).
7. Review far-pointer promotion results and manually correct remaining calling conventions.
8. Export or continue analysis.

## Notes

- Dynamic traces from TBpipeline (Unicorn) remain useful for verifying calling conventions and pointer usage.
- Ordinal maps are partial; extend `scripts/win16-ordinals.txt` as real 16-bit system DLLs are examined.
