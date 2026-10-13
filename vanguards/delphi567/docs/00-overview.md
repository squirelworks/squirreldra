# Overview: Delphi 5/6/7 Decompilation

## Why These Versions Matter

Delphi 5, 6, and 7 were extremely popular. Many industrial control systems, medical devices, custom business applications, and malware samples from the 2000s–2010s are still built with them. Their binaries are:

- 32-bit only (PE32)
- Statically linked with a large RTL/VCL
- Rich in recoverable metadata (RTTI for published members, VMT hierarchies, DFM forms)
- Relatively stable in internal layout across 5/6/7 (“Old Era”)

## Core Challenges in General-Purpose Tools

Ghidra, IDA, and Binary Ninja do not understand Delphi out of the box:

| Challenge | Symptom | Mitigation |
|-----------|---------|------------|
| Missing symbols | `FUN_0040xxxx` everywhere | IDR + Dhrake / RTTI recovery |
| Wrong calling convention | Parameters in wrong registers, clobbered values | Delphi `register` convention |
| Opaque objects | Pointer arithmetic instead of field access | VMT + FieldTable → structs |
| String operations | Refcount / length prefix noise | Markup AnsiString helpers |
| Form code | Event handlers hard to locate | DFM parsing + method tables |
| Init/finalization | Entry point looks strange | Unit init tables |

## What “Good” Looks Like

After a solid pipeline you should see:

- Class names and inheritance hierarchy recovered
- Virtual method tables turned into named structures
- Published fields and properties with offsets and types
- Methods using the correct `register` calling convention (`Self` in EAX, etc.)
- Common RTL string and memory helpers named and prototyped
- Form event handlers linked back to components

## Version Detection Quick Checks

- Presence of `DVCLAL` and `PACKAGEINFO` resources
- Code section named `CODE` (or `.text` in later builds)
- VMT self-pointer pattern and ShortString class names immediately after virtual methods
- Characteristic RTL entry sequences
- Tools: IDR auto-detect, undelphi, or manual VMT scan

## Scope of This Repository

Focus is on **static analysis** of unpacked Delphi 5/6/7 PE32 binaries using the best available free and open tools (IDR, Ghidra + Dhrake, undelphi, custom scripts). Dynamic analysis and full source recovery are out of scope but the recovered metadata dramatically accelerates both.
