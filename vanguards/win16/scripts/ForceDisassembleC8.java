// ForceDisassembleC8.java
// Ghidra script: find undefined C8 (ENTER) bytes and force disassembly + function creation.
// Covers the 108 score-3 runs in the BAS ?? regions.
//
// Run from Script Manager on the TB80BAS project after the initial analysis.

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.address.AddressSet;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryBlock;

public class ForceDisassembleC8 extends GhidraScript {

    @Override
    public void run() throws Exception {
        Memory mem = currentProgram.getMemory();
        int found = 0;
        int created = 0;

        for (MemoryBlock block : mem.getBlocks()) {
            if (!block.isExecute()) continue;
            Address start = block.getStart();
            Address end = block.getEnd();
            Address addr = start;
            while (addr != null && addr.compareTo(end) <= 0) {
                if (monitor.isCancelled()) return;
                // Look for undefined C8
                if (getInstructionAt(addr) == null) {
                    try {
                        byte b = mem.getByte(addr);
                        if (b == (byte) 0xC8) {
                            found++;
                            // Force disassemble
                            AddressSet set = new AddressSet(addr, addr.add(1));
                            disassemble(addr);
                            // Create function if none exists
                            Function f = getFunctionContaining(addr);
                            if (f == null) {
                                f = createFunction(addr, null);
                                if (f != null) created++;
                            }
                        }
                    } catch (Exception e) {
                        // skip unreadable
                    }
                }
                addr = addr.next();
            }
        }
        println("C8 sites found: " + found);
        println("Functions created: " + created);
    }
}
