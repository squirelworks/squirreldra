@echo off
REM build-minidbg.bat
REM Example Open Watcom build for the minimal debugger scaffold.
REM Adjust WATCOM path and options as needed for your install.
REM
REM Targets a Win32 binary that runs on Windows 98 and XP.

if "%WATCOM%"=="" (
    echo WATCOM environment variable not set.
    echo Run the Open Watcom setvars.bat first.
    exit /b 1
)

echo Building minidbg.c with Open Watcom...
wcl386 -bt=nt -l=nt -fe=minidbg.exe src\minidbg.c

if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

echo Build succeeded: minidbg.exe
echo Usage: minidbg.exe ^<pid^>
