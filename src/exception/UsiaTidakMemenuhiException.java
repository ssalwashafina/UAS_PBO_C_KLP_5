package exception;

// Dilempar ketika umur user di bawah batas rating usia film yang ingin dipesan.
public class UsiaTidakMemenuhiException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception usia tidak memenuhi.
    public UsiaTidakMemenuhiException(String judul, int ratingUsia, int umurUser) {
        super("Film \"" + judul + "\" untuk usia " + ratingUsia + "+. Umur kamu " + umurUser + " tahun.");
    }
}
