# ForceDisassembleC8

## Why

Ghidra's NE loader and auto-analysis leave large stretches of 16-bit code as undefined. Many of those stretches begin with `C8` (ENTER). Because nothing ever flows into them, the decompiler never produces a function and the XREF graph stays empty.

This script walks every defined memory block, finds undefined bytes whose first byte is `C8`, forces disassembly, and creates a function. It is the cheapest recovery for the functions that the loader never reached.

## What it does not do

It does not name anything. It does not fix the NE entry-table signed-byte bug. It does not invent control flow that is not present in the bytes.

## When to run

Once, after the NE is loaded and before any manual renaming. Run it before `ForceDisassemblePrologues`.
