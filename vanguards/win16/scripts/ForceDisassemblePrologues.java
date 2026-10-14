// ForceDisassemblePrologues.java
// Ghidra script: force disassembly on classic Win16 prologues and NE entry points
// in undefined regions. Companion to ForceDisassembleC8.java.
//
// Patterns:
//   55 8B EC              PUSH BP / MOV BP,SP
//   45 55 8B EC 1E 8E D8  INC BP / PUSH BP / MOV BP,SP / PUSH DS / MOV DS,AX
//   instruction after a real RET/RETF instruction (not just the byte)
//   NE entry-table symbols that still have no function
//
// Run from Script Manager after the C8 pass.

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.Listing;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.symbol.Symbol;
import ghidra.program.model.symbol.SymbolIterator;
import ghidra.program.model.symbol.SymbolTable;
import ghidra.program.model.symbol.SymbolType;

public class ForceDisassemblePrologues extends GhidraScript {

    @Override
    public void run() throws Exception {
        Listing listing = currentProgram.getListing();
        Memory memory = currentProgram.getMemory();
        Address start = currentProgram.getMinAddress();
        Address end = currentProgram.getMaxAddress();

        int classic = 0;
        int export = 0;
        int afterRet = 0;
        int entries = 0;
        int created = 0;

        Address a = start;
        while (a != null && a.compareTo(end) <= 0) {
            if (monitor.isCancelled()) break;

            // only look in undefined bytes
            if (listing.getUndefinedDataAt(a) == null && listing.getCodeUnitAt(a) != null) {
                a = a.next();
                continue;
            }

            byte[] b = new byte[8];
            int n;
            try {
                n = memory.getBytes(a, b);
            } catch (Exception ex) {
                a = a.next();
                continue;
            }
            if (n < 3) {
                a = a.next();
                continue;
            }

            boolean hit = false;

            // classic prolog
            if (b[0] == (byte)0x55 && b[1] == (byte)0x8B && b[2] == (byte)0xEC) {
                classic++;
                hit = true;
            }
            // Win16 export prolog
            else if (n >= 7 &&
                     b[0] == (byte)0x45 && b[1] == (byte)0x55 && b[2] == (byte)0x8B &&
                     b[3] == (byte)0xEC && b[4] == (byte)0x1E && b[5] == (byte)0x8E &&
                     b[6] == (byte)0xD8) {
                export++;
                hit = true;
            }
            // after a real RET / RETF instruction (not just the byte in data)
            else if (a.getOffset() > 0) {
                try {
                    Address prev = a.subtract(1);
                    Instruction prevInsn = listing.getInstructionAt(prev);
                    if (prevInsn == null) {
                        // the RET may start earlier; check the previous code unit
                        prevInsn = listing.getInstructionContaining(prev);
                    }
                    if (prevInsn != null) {
                        String mnem = prevInsn.getMnemonicString();
                        if ("RET".equals(mnem) || "RETF".equals(mnem)) {
                            afterRet++;
                            hit = true;
                        }
                    }
                } catch (Exception ex) {
                    // off the end of a block
                }
            }

            if (hit) {
                try {
                    disassemble(a);
                    Function f = createFunction(a, null);
                    if (f != null) created++;
                } catch (Exception ex) {
                    // already defined or bad address
                }
            }
            a = a.next();
        }

        // Symbols that look like entry points (FUNCTION symbols or names starting with Ordinal_)
        // that still have no function.
        SymbolTable st = currentProgram.getSymbolTable();
        SymbolIterator it = st.getAllSymbols(true);
        while (it.hasNext()) {
            if (monitor.isCancelled()) break;
            Symbol s = it.next();
            Address ea = s.getAddress();
            if (listing.getFunctionAt(ea) != null) continue;

            boolean isEntry = s.getSymbolType() == SymbolType.FUNCTION
                    || s.getName().startsWith("Ordinal_")
                    || s.getName().startsWith("entry_");
            if (!isEntry) continue;

            try {
                disassemble(ea);
                Function f = createFunction(ea, s.getName());
                if (f != null) {
                    entries++;
                    created++;
                }
            } catch (Exception ex) {
                // skip
            }
        }

        println("Classic prologues: " + classic);
        println("Export prologues:  " + export);
        println("After RET/RETF:    " + afterRet);
        println("Entry table:       " + entries);
        println("Functions created: " + created);
    }
}
