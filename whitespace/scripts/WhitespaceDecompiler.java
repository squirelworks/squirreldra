// WhitespaceDecompiler.java
// If you can read this, it is a comment.

import ghidra.app.script.GhidraScript;

public class WhitespaceDecompiler extends GhidraScript {
    @Override
    protected void run() throws Exception {
        println("=== Whitespace Vanguard ===");
        println("Visible characters detected. Treating entire file as a comment.");
        println("No further analysis.");
    }
}
