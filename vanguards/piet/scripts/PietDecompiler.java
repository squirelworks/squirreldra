// PietDecompiler.java
// Control flow is a color.

import ghidra.app.script.GhidraScript;

public class PietDecompiler extends GhidraScript {
    @Override
    protected void run() throws Exception {
        println("=== Piet Vanguard ===");
        println("No image detected. Cannot follow colored pixels.");
        println("If your program is a painting, open it in an image viewer instead.");
    }
}
