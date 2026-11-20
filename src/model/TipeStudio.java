package model;

// Jenis studio. Tiap jenis membawa pengali harga sendiri.
public enum TipeStudio {
    // Studio biasa, harga dasar.
    REGULAR("Regular", 1.0),
    // Studio premium, harga 1,5 kali harga dasar.
    PREMIUM("Premium", 1.5);

    private final String label;
    private final double pengaliHarga;

    TipeStudio(String label, double pengaliHarga) {
        this.label = label;
        this.pengaliHarga = pengaliHarga;
    }

    // Mengambil nama tipe untuk ditampilkan.
    public String getLabel() {
        return label;
    }

    // Mengambil pengali harga tiket untuk tipe ini.
    public double getPengaliHarga() {
        return pengaliHarga;
    }
}
