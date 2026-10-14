# ForceDisassemblePrologues

## Why

Even after forcing every `C8` site, some functions remain undefined because their first instruction is not ENTER. The common cases are:

- classic prologues (`55 8B EC`)
- Win16 export prologues (`45 55 8B EC 1E 8E D8`)
- the instruction immediately after a `RET` or `RETF` that ends a previous function
- addresses listed in the NE entry table that the loader never disassembled

This script covers those four patterns. It is the second pass after `ForceDisassembleC8`.

## What it does not do

It does not name functions. It does not repair broken far-call fixups. It does not touch already-defined code.

## When to run

After `ForceDisassembleC8`. Re-running it is safe; it only creates a function where one does not already exist.
