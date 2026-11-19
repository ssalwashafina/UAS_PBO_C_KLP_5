package payment;

import java.util.ArrayList;
import java.util.List;

// Pabrik objek metode pembayaran. Dipakai menu untuk menampilkan pilihan dan dipakai FileHandler untuk memulihkan metode dari kode yang tersimpan.
public final class PembayaranFactory {

    private PembayaranFactory() {
    }

    // Membuat metode pembayaran dari kodenya.
    public static Pembayaran dariKode(String kode) {
        switch (kode) {
            case "TUNAI":
                return new PembayaranTunai();
            case "EWALLET":
                return new PembayaranEWallet();
            case "TRANSFER":
                return new PembayaranTransfer();
            default:
                throw new IllegalArgumentException("Metode pembayaran tidak dikenal: " + kode);
        }
    }

    // Mengambil semua metode non tunai.
    public static List<Pembayaran> daftarNonTunai() {
        List<Pembayaran> daftar = new ArrayList<>();
        daftar.add(new PembayaranEWallet());
        daftar.add(new PembayaranTransfer());
        return daftar;
    }
}
