// BrainfuckDecompiler.java
// Ghidra script — decompiling vanguard for the eight-command language
//
// Currently a stub. It prints the instruction set and stops.
// Extending it would require defining a Brainfuck processor, which we have
// elected not to do.

import ghidra.app.script.GhidraScript;

public class BrainfuckDecompiler extends GhidraScript {

    @Override
    protected void run() throws Exception {
        println("=== Brainfuck Decompiling Vanguard ===");
        println("Instruction set:");
        println("  >  increment data pointer");
        println("  <  decrement data pointer");
        println("  +  increment byte at pointer");
        println("  -  decrement byte at pointer");
        println("  .  output byte");
        println("  ,  input byte");
        println("  [  jump forward if zero");
        println("  ]  jump backward if non-zero");
        println();
        println("No further decompilation will be attempted.");
        println("The program is already its own source.");
    }
}
