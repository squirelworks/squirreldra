// Win16NEFixes.java
// Ghidra Java script
//
// What it does:
// 1. Renames pure Ordinal_N symbols using win16-ordinals.txt.
//    Already-named symbols (GETVERSION, MessageBox, etc.) are left untouched.
// 2. Applies the __pascal16far calling convention to known Win16 API functions
//    and to every external function in the system modules.
// 3. Promotes selected data that looks like far pointers to a 4-byte pointer type.

import ghidra.app.script.GhidraScript;
import ghidra.program.model.symbol.*;
import ghidra.program.model.data.*;
import ghidra.program.model.listing.*;
import ghidra.program.model.scalar.Scalar;
import java.io.*;
import java.util.*;

public class Win16NEFixes extends GhidraScript {

    private static final String[] SYSTEM_MODULES = {
        "KERNEL", "USER", "GDI", "KEYBOARD", "SHELL", "COMMDLG", "LZEXPAND"
    };

    private static final Set<String> PASCAL_FAR_APIS = new HashSet<>(Arrays.asList(
        "MessageBox", "MessageBoxEx", "GetWindowText", "SetWindowText", "GetWindowTextLength",
        "SendMessage", "PostMessage", "SendDlgItemMessage", "CreateWindow", "CreateWindowEx",
        "DefWindowProc", "DialogBox", "DialogBoxParam", "GetDlgItem", "GetDlgItemText", "SetDlgItemText",
        "LoadString", "LoadIcon", "LoadCursor", "LoadBitmap", "LoadMenu", "LoadAccelerators",
        "GetFocus", "SetFocus", "GetActiveWindow", "SetActiveWindow", "GetCapture", "SetCapture",
        "ReleaseCapture", "IsWindow", "IsChild", "IsWindowVisible", "IsWindowEnabled",
        "ClientToScreen", "ScreenToClient", "WindowFromPoint", "GetWindowWord", "SetWindowWord",
        "GetWindowLong", "SetWindowLong", "GetClassWord", "SetClassWord", "GetClassLong", "SetClassLong",
        "GetDesktopWindow", "GetParent", "SetParent", "GetWindow", "GetNextWindow", "GetTopWindow",
        "GetClientRect", "GetWindowRect", "AdjustWindowRect", "AdjustWindowRectEx",
        "TextOut", "ExtTextOut", "TabbedTextOut", "GetTextExtent", "GetTextExtentEx",
        "SelectObject", "DeleteObject", "GetStockObject", "GetObject", "BitBlt", "PatBlt",
        "CreatePen", "CreatePenIndirect", "CreateSolidBrush", "CreateHatchBrush", "CreatePatternBrush",
        "CreateBrushIndirect", "CreateBitmap", "CreateBitmapIndirect", "CreateCompatibleBitmap",
        "CreateDIBitmap", "GetTextMetrics", "GetTextFace", "SetTextAlign", "SetTextColor",
        "SetBkColor", "SetBkMode",
        "GlobalAlloc", "GlobalReAlloc", "GlobalFree", "GlobalLock", "GlobalUnlock", "GlobalSize",
        "LocalAlloc", "LocalReAlloc", "LocalFree", "LocalLock", "LocalUnlock", "LocalSize",
        "GetProcAddress", "GetModuleHandle", "GetModuleFileName", "LoadLibrary", "FreeLibrary",
        "GetWindowsDirectory", "GetSystemDirectory", "GetTempFileName", "GetFreeSpace",
        "InitTask", "WaitEvent", "PostEvent", "MakeProcInstance", "FreeProcInstance"
    ));

    @Override
    protected void run() throws Exception {
        if (currentProgram == null) {
            println("No program open");
            return;
        }
        println("=== Win16 NE Fixes ===");
        Map<String, Map<Integer, String>> ordinalMap = loadOrdinalMap();
        renameOrdinals(ordinalMap);
        applyPascalFar();
        promoteFarPointers();
        println("Done.");
    }

    private Map<String, Map<Integer, String>> loadOrdinalMap() {
        Map<String, Map<Integer, String>> mapping = new HashMap<>();
        File mapFile = null;
        if (sourceFile != null && sourceFile.getFile(false) != null) {
            mapFile = new File(sourceFile.getFile(false).getParentFile(), "win16-ordinals.txt");
        }
        if (mapFile == null || !mapFile.exists()) {
            println("Ordinal map not found next to script — place win16-ordinals.txt beside Win16NEFixes.java");
            return mapping;
        }
        String currentModule = null;
        try (BufferedReader br = new BufferedReader(new FileReader(mapFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    if (line.startsWith("# ") && line.length() > 2) {
                        String token = line.substring(2).split("\\s+")[0];
                        if (token.equals(token.toUpperCase()) && token.matches("[A-Z]+")) {
                            currentModule = token;
                        }
                    }
                    continue;
                }
                String[] parts = line.split("\\t");
                if (parts.length >= 2 && currentModule != null) {
                    try {
                        int ord = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        mapping.computeIfAbsent(currentModule, k -> new HashMap<>()).put(ord, name);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            println("Failed to read ordinal map: " + e.getMessage());
        }
        return mapping;
    }

    private void renameOrdinals(Map<String, Map<Integer, String>> ordinalMap) throws Exception {
        SymbolTable symtab = currentProgram.getSymbolTable();
        int renamed = 0;
        for (Map.Entry<String, Map<Integer, String>> entry : ordinalMap.entrySet()) {
            String module = entry.getKey();
            Namespace ns = symtab.getNamespace(module, null);
            if (ns == null) continue;

            for (Symbol sym : symtab.getSymbols(ns)) {
                String current = sym.getName();
                // Only rename pure Ordinal_N symbols. Leave already-named
                // symbols (GETVERSION, MessageBox, etc.) untouched.
                if (current.startsWith("Ordinal_")) {
                    try {
                        int ord = Integer.parseInt(current.substring(8));
                        String preferredName = entry.getValue().get(ord);
                        if (preferredName != null && !preferredName.equals(current)) {
                            sym.setName(preferredName, SourceType.IMPORTED);
                            renamed++;
                            println("Renamed " + module + "!" + current + " -> " + preferredName);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        println("Renamed " + renamed + " Ordinal_N symbols");
    }

    private void applyPascalFar() throws Exception {
        try {
            currentProgram.getCompilerSpec().getCallingConvention("__pascal16far");
        } catch (Exception e) {
            println("__pascal16far prototype not found in compiler spec — skipping");
            return;
        }
        FunctionManager funcMgr = currentProgram.getFunctionManager();
        SymbolTable symtab = currentProgram.getSymbolTable();
        int applied = 0;
        FunctionIterator funcs = funcMgr.getFunctions(true);
        while (funcs.hasNext()) {
            Function func = funcs.next();
            if (PASCAL_FAR_APIS.contains(func.getName())) {
                if (trySetPascal(func)) applied++;
            }
        }
        for (String module : SYSTEM_MODULES) {
            Namespace ns = symtab.getNamespace(module, null);
            if (ns == null) continue;
            for (Symbol sym : symtab.getSymbols(ns)) {
                if (sym.getSymbolType() != SymbolType.FUNCTION) continue;
                Function func = funcMgr.getFunctionAt(sym.getAddress());
                if (func != null && func.isExternal()) {
                    if (trySetPascal(func)) applied++;
                }
            }
        }
        println("Applied __pascal16far to " + applied + " functions");
    }

    private boolean trySetPascal(Function func) {
        try {
            func.setCallingConvention("__pascal16far");
            println("Set " + func.getName() + " to __pascal16far");
            return true;
        } catch (Exception e) {
            println("Failed to set convention on " + func.getName() + ": " + e.getMessage());
            return false;
        }
    }

    private void promoteFarPointers() throws Exception {
        Listing listing = currentProgram.getListing();
        DataType ptr32;
        try {
            ptr32 = Pointer32DataType.dataType;
        } catch (Exception e) {
            ptr32 = PointerDataType.dataType;
        }
        int promoted = 0;
        DataIterator dataIter = listing.getDefinedData(true);
        while (dataIter.hasNext()) {
            Data data = dataIter.next();
            if (data.getLength() == 4 && !(data.getDataType() instanceof Pointer)) {
                try {
                    Object val = data.getValue();
                    if (val instanceof Scalar) {
                        long v = ((Scalar) val).getUnsignedValue();
                        long seg = (v >> 16) & 0xFFFF;
                        long off = v & 0xFFFF;
                        if (seg != 0 && off < 0x10000) {
                            listing.clearCodeUnits(data.getMinAddress(), data.getMaxAddress(), false);
                            listing.createData(data.getMinAddress(), ptr32);
                            promoted++;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        println("Promoted " + promoted + " potential far pointers (heuristic)");
    }
}
