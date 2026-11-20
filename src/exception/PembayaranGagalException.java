package exception;

// Dilempar ketika nominal pembayaran tidak valid sehingga tidak bisa diproses.
public class PembayaranGagalException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception pembayaran gagal.
    public PembayaranGagalException(String pesan) {
        super(pesan);
    }
}
