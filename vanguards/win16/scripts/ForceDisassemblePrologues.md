# ForceDisassemblePrologues

## Why

Even after forcing every `C8` site, some functions remain undefined because their first instruction is not ENTER. The common cases are:

- classic prologues (`55 8B EC`)
- Win16 export prologues (`45 55 8B EC 1E 8E D8`)
- the instruction immediately after a real `RET` or `RETF` instruction (not merely the byte)
- NE entry-table / Ordinal_ symbols that the loader never turned into functions

This script covers those four patterns. It is the second pass after `ForceDisassembleC8`.

## What it does not do

It does not name functions. It does not repair broken far-call fixups. It does not touch already-defined code. The after-RET check requires an actual instruction so it does not fire on data that happens to follow a `C3`/`CB` byte.

## When to run

After `ForceDisassembleC8`. Re-running it is safe; it only creates a function where one does not already exist.
