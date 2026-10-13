# Win98 Debugging Tools (Squirreldra)

Practical notes, comparisons, and starter code for debugging on Windows 98 and Windows XP.

## Contents

### Core tools
- [SoftICE](softice.md)
- [IceDump](icedump.md)
- [Open Watcom Debugger](openwatcom.md)
- [Turbo Debugger (TDW)](tdw.md)

### Platform guides
- [Windows 98](win98.md)
- [Windows XP](xp.md)

### Modern / open alternatives
- [OllyDbg](ollydbg.md)
- [BugChecker](bugchecker.md)

### Practical work
- [Recommendations](recommendations.md)
- [Open Watcom setup notes](setup-openwatcom.md)
- [Minimal user-mode debugger starter](src/minidbg.c) — Open Watcom / Win32 API, targets Win98 & XP

## Layout

```
debugging/
├── README.md
├── softice.md
├── icedump.md
├── openwatcom.md
├── tdw.md
├── win98.md
├── xp.md
├── ollydbg.md
├── bugchecker.md
├── recommendations.md
├── setup-openwatcom.md
├── scripts/
│   └── build-minidbg.bat
└── src/
    └── minidbg.c
```

## Goal

Identify what is usable today, what is open and improvable, and provide a concrete starting point (minimal debugger) that can be compiled for Win98 and XP.
