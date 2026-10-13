/*
 * minidbg.c
 * Minimal user-mode debugger starter for Windows 98 and Windows XP.
 *
 * Uses the classic Win32 debugging APIs that have existed since Windows 95/NT:
 *   DebugActiveProcess, WaitForDebugEvent, ContinueDebugEvent,
 *   ReadProcessMemory, WriteProcessMemory, etc.
 *
 * Intended to be compiled with Open Watcom (or VC6 / Borland) targeting Win32.
 *
 * This is a scaffold, not a full debugger. It demonstrates:
 * - Attaching to a process
 * - Waiting for debug events
 * - Printing basic event information
 * - Continuing execution
 *
 * Build example (Open Watcom):
 *   wcl386 -bt=nt -l=nt minidbg.c
 *   or for a 9x-compatible binary that also runs on XP:
 *   wcl386 -bt=nt minidbg.c
 *
 * Usage:
 *   minidbg.exe <pid>
 */

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>

static void print_event(const DEBUG_EVENT *ev)
{
    printf("Debug event: ");
    switch (ev->dwDebugEventCode) {
    case EXCEPTION_DEBUG_EVENT:
        printf("EXCEPTION (code=0x%08lx, addr=%p)\n",
               ev->u.Exception.ExceptionRecord.ExceptionCode,
               ev->u.Exception.ExceptionRecord.ExceptionAddress);
        break;
    case CREATE_THREAD_DEBUG_EVENT:
        printf("CREATE_THREAD (tid=%lu)\n", ev->dwThreadId);
        break;
    case CREATE_PROCESS_DEBUG_EVENT:
        printf("CREATE_PROCESS (pid=%lu)\n", ev->dwProcessId);
        break;
    case EXIT_THREAD_DEBUG_EVENT:
        printf("EXIT_THREAD (tid=%lu, code=%lu)\n",
               ev->dwThreadId, ev->u.ExitThread.dwExitCode);
        break;
    case EXIT_PROCESS_DEBUG_EVENT:
        printf("EXIT_PROCESS (code=%lu)\n", ev->u.ExitProcess.dwExitCode);
        break;
    case LOAD_DLL_DEBUG_EVENT:
        printf("LOAD_DLL (base=%p)\n", ev->u.LoadDll.lpBaseOfDll);
        break;
    case UNLOAD_DLL_DEBUG_EVENT:
        printf("UNLOAD_DLL (base=%p)\n", ev->u.UnloadDll.lpBaseOfDll);
        break;
    case OUTPUT_DEBUG_STRING_EVENT:
        printf("OUTPUT_DEBUG_STRING\n");
        break;
    default:
        printf("UNKNOWN (code=%lu)\n", ev->dwDebugEventCode);
        break;
    }
}

int main(int argc, char **argv)
{
    DWORD pid;
    DEBUG_EVENT ev;
    DWORD continue_status;
    BOOL attached = FALSE;

    if (argc < 2) {
        printf("Usage: %s <pid>\n", argv[0]);
        printf("Minimal user-mode debugger scaffold (Win98 / XP).\n");
        return 1;
    }

    pid = (DWORD)strtoul(argv[1], NULL, 0);
    if (pid == 0) {
        printf("Invalid PID.\n");
        return 1;
    }

    printf("Attempting to attach to PID %lu...\n", pid);

    if (!DebugActiveProcess(pid)) {
        printf("DebugActiveProcess failed: %lu\n", GetLastError());
        return 1;
    }
    attached = TRUE;
    printf("Attached. Waiting for debug events (Ctrl+C to stop)...\n");

    while (1) {
        if (!WaitForDebugEvent(&ev, INFINITE)) {
            printf("WaitForDebugEvent failed: %lu\n", GetLastError());
            break;
        }

        print_event(&ev);

        continue_status = DBG_CONTINUE;
        if (ev.dwDebugEventCode == EXCEPTION_DEBUG_EVENT) {
            /* For first-chance exceptions you may want DBG_EXCEPTION_NOT_HANDLED
               in a more complete debugger. */
            if (ev.u.Exception.dwFirstChance) {
                continue_status = DBG_EXCEPTION_NOT_HANDLED;
            }
        }

        if (!ContinueDebugEvent(ev.dwProcessId, ev.dwThreadId, continue_status)) {
            printf("ContinueDebugEvent failed: %lu\n", GetLastError());
            break;
        }

        if (ev.dwDebugEventCode == EXIT_PROCESS_DEBUG_EVENT) {
            printf("Process exited. Detaching.\n");
            break;
        }
    }

    if (attached) {
        /* DebugActiveProcessStop exists on XP+; on pure Win98 you may just exit. */
        /* DebugActiveProcessStop(pid); */
    }

    return 0;
}
