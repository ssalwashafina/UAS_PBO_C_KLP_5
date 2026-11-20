package exception;

// Dilempar ketika data yang dicari (film, studio, jadwal, pesanan, akun) tidak ada di penyimpanan.
public class DataTidakDitemukanException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception data tidak ditemukan.
    public DataTidakDitemukanException(String jenis, String id) {
        super(jenis + " dengan ID/kode '" + id + "' tidak ditemukan.");
    }
}
