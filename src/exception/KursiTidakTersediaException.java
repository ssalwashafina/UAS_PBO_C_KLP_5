package exception;

// Dilempar ketika kursi yang dipilih tidak valid, sudah terisi, atau sudah dipilih pada pesanan yang sedang dibuat.
public class KursiTidakTersediaException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception kursi tidak tersedia.
    public KursiTidakTersediaException(String pesan) {
        super(pesan);
    }
}
