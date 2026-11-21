package util;

// Dilempar ketika input dari keyboard habis (misalnya Ctrl+D atau input dialihkan dari file).
public class InputBerakhirException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Membuat exception input berakhir.
    public InputBerakhirException() {
        super("Input berakhir.");
    }
}
