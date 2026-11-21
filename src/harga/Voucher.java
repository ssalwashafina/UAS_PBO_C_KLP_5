package harga;

import java.util.Optional;

// Kode voucher diskon yang berlaku di bioskop. Daftarnya tetap di kode, cukup untuk keperluan tugas.
public enum Voucher {
    // Diskon 10% untuk siapa saja.
    HEMAT10(10),
    // Diskon 15% untuk pelajar.
    PELAJAR15(15),
    // Diskon 20% promo ujian.
    UAS20(20);

    private final int persen;

    Voucher(int persen) {
        this.persen = persen;
    }

    // Mengambil besar diskon.
    public int getPersen() {
        return persen;
    }

    // Mencari voucher dari kode yang diketik user.
    public static Optional<Voucher> cari(String kode) {
        if (kode == null) {
            return Optional.empty();
        }
        for (Voucher v : values()) {
            if (v.name().equalsIgnoreCase(kode.trim())) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
