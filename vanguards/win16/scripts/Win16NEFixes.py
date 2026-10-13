# Win16NEFixes.py
# Ghidra Python script (Jython or PyGhidra)
#
# What it does:
# 1. Renames Ordinal_N symbols in KERNEL / USER / GDI (and similar) namespaces
#    using the maps in win16-ordinals.txt (or a hard-coded subset).
# 2. Applies the __pascal16far calling convention to known Win16 API functions
#    when that prototype exists in the current compiler spec.
# 3. Promotes selected data and parameters that are known to be far pointers
#    to a 4-byte pointer type.
#
# Usage:
# - Copy this script into your Ghidra scripts directory (or run via Script Manager).
# - Place win16-ordinals.txt next to it, or edit MAP_PATH below.
# - Open an NE binary, then run the script.

from ghidra.program.model.symbol import SymbolType, SourceType
from ghidra.program.model.data import PointerDataType, DataType
from ghidra.program.model.listing import Function
import os

MAP_PATH = os.path.join(os.path.dirname(__file__), "win16-ordinals.txt") if '__file__' in dir() else "win16-ordinals.txt"
SYSTEM_MODULES = ("KERNEL", "USER", "GDI", "KEYBOARD", "SHELL", "COMMDLG", "LZEXPAND")

PASCAL_FAR_APIS = {
    "MessageBox", "GetWindowText", "SetWindowText", "SendMessage", "PostMessage",
    "CreateWindow", "CreateWindowEx", "DefWindowProc", "DialogBox", "GetDlgItem",
    "LoadString", "LoadIcon", "LoadCursor", "LoadBitmap", "TextOut", "ExtTextOut",
    "SelectObject", "DeleteObject", "GetStockObject", "BitBlt", "PatBlt",
    "GlobalAlloc", "GlobalLock", "GlobalUnlock", "GlobalFree",
    "LocalAlloc", "LocalLock", "LocalUnlock", "LocalFree",
}

def load_ordinal_map(path):
    mapping = {}
    if not os.path.exists(path):
        print("Ordinal map not found at %s — skipping rename step" % path)
        return mapping
    current_module = None
    with open(path, "r") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                if line.startswith("# ") and line[2:].split()[0].isupper():
                    current_module = line[2:].split()[0]
                continue
            parts = line.split("\t")
            if len(parts) >= 2 and current_module:
                try:
                    ord_num = int(parts[0])
                    name = parts[1].strip()
                    mapping.setdefault(current_module, {})[ord_num] = name
                except ValueError:
                    pass
    return mapping

def rename_ordinals(program, ordinal_map):
    symtab = program.getSymbolTable()
    renamed = 0
    for module, ords in ordinal_map.items():
        ns = symtab.getNamespace(module, None)
        if ns is None:
            continue
        for ord_num, real_name in ords.items():
            ordinal_name = "Ordinal_%d" % ord_num
            symbols = symtab.getSymbols(ordinal_name, ns)
            for sym in symbols:
                try:
                    sym.setName(real_name, SourceType.IMPORTED)
                    renamed += 1
                    print("Renamed %s!%s -> %s" % (module, ordinal_name, real_name))
                except Exception as e:
                    print("Failed to rename %s!%s: %s" % (module, ordinal_name, e))
    print("Renamed %d ordinals" % renamed)
    return renamed

def apply_pascal_far(program):
    lang = program.getLanguage()
    cspec = lang.getCompilerSpec()
    try:
        proto = cspec.getCallingConvention("__pascal16far")
    except Exception:
        print("__pascal16far prototype not found in compiler spec — skipping")
        return 0

    func_mgr = program.getFunctionManager()
    applied = 0
    for func in func_mgr.getFunctions(True):
        name = func.getName()
        if name in PASCAL_FAR_APIS:
            try:
                func.setCallingConvention("__pascal16far")
                applied += 1
                print("Set %s to __pascal16far" % name)
            except Exception as e:
                print("Failed to set convention on %s: %s" % (name, e))
    print("Applied __pascal16far to %d functions" % applied)
    return applied

def promote_far_pointers(program):
    listing = program.getListing()
    ptr32 = PointerDataType.dataType
    try:
        from ghidra.program.model.data import Pointer32DataType
        ptr32 = Pointer32DataType.dataType
    except Exception:
        pass

    promoted = 0
    data_iter = listing.getDefinedData(True)
    while data_iter.hasNext():
        data = data_iter.next()
        if data.getLength() == 4 and not isinstance(data.getDataType(), PointerDataType):
            try:
                val = data.getValue()
                if val is not None and hasattr(val, "getUnsignedValue"):
                    v = val.getUnsignedValue()
                    seg = (v >> 16) & 0xFFFF
                    off = v & 0xFFFF
                    if seg != 0 and off < 0x10000:
                        data.setDataType(ptr32, SourceType.ANALYSIS)
                        promoted += 1
            except Exception:
                pass
    print("Promoted %d potential far pointers (heuristic)" % promoted)
    return promoted

def run():
    program = currentProgram
    if program is None:
        print("No program open")
        return
    print("=== Win16 NE Fixes ===")
    ordinal_map = load_ordinal_map(MAP_PATH)
    rename_ordinals(program, ordinal_map)
    apply_pascal_far(program)
    promote_far_pointers(program)
    print("Done.")

if __name__ == "__main__":
    run()
