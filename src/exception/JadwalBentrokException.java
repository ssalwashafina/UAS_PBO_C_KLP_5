package exception;

// Dilempar ketika jadwal baru bertabrakan dengan jadwal lain di studio yang sama.
public class JadwalBentrokException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception jadwal bentrok.
    public JadwalBentrokException(String pesan) {
        super(pesan);
    }
}
