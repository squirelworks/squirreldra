# SoftICE

Classic single-machine kernel-mode debugger from NuMega (later Compuware / Micro Focus).

## What it is

- Runs underneath Windows 9x as a VxD.
- Can break into ring 0, debug drivers, applications, and the OS itself.
- Single-machine (no second PC required), unlike classic WinDbg/KD setups.
- Full-screen command interface, symbolic debugging, hardware breakpoints, memory access breaks, etc.
- Last widely used versions targeted Windows 95/98/ME (and separately NT/2000/XP).

## Practical value for Win98

SoftICE is still one of the strongest options for low-level work on actual Windows 98 hardware or VMs. It already runs natively on the platform.

## Decompilation / reverse-engineering worth?

**Limited practical return for “improvements.”**

- Source remains proprietary (held by Micro Focus). Never released.
- Extremely complex: heavy assembly, tight coupling to 9x VxD/kernel structures, custom video drivers, symbol engine, breakpoint implementation, etc.
- Known issues exist (stability, occasional BPX residue written back to files, context-sensitive breakpoints in the lower 2 GB, etc.), but fixing them via a full decompilation is high-effort and fragile.
- Public reverse-engineering of portions has been done over the years (breakpoint internals, INT 41h interface, anti-debug surface, extensions). That knowledge is useful for education and for writing new tools.

**Useful reasons to study it:**

- Learn how a real ring-0 debugger hooks the system.
- Understand the anti-debug techniques that targeted it (and how extensions bypassed them).
- Extract ideas for a new open SoftICE-like tool.

## Alternatives / related projects

- IceDump (see icedump.md) — open-source extension that adds many useful commands.
- BugChecker and later SoftICE-inspired projects — mostly NT-based; porting the approach cleanly to pure 9x is non-trivial.
- Open Watcom debugger (see openwatcom.md) — open source, recompilable, but not a full SoftICE replacement for kernel work.

## Recommendation

Use SoftICE itself (plus IceDump) for serious Win98 kernel/user-mode debugging. Reverse-engineer specific internals only when you need to understand a particular mechanism or write an extension. Full decompilation for ongoing maintenance is not practical.
