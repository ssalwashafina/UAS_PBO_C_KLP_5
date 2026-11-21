package exception;

// Induk semua exception buatan sendiri di program ini.
public class BioskopException extends Exception {

    private static final long serialVersionUID = 1L;

    // Membuat exception dengan pesan.
    public BioskopException(String pesan) {
        super(pesan);
    }
}
