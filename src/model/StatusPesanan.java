package model;

// Status sebuah pesanan tiket selama siklus hidupnya.
public enum StatusPesanan {
    // Pesanan baru dibuat, kursi sudah ditahan, pembayaran belum dikonfirmasi admin.
    MENUNGGU("Menunggu konfirmasi"),
    // Admin sudah memastikan pembayaran diterima. Tiket sah dipakai.
    TERVERIFIKASI("Terverifikasi"),
    // Pembayaran tidak diterima atau pesanan kedaluwarsa. Kursi dilepas.
    DITOLAK("Ditolak"),
    // Tiket sudah dipakai di pintu masuk (check-in).
    DIGUNAKAN("Sudah digunakan");

    private final String label;

    StatusPesanan(String label) {
        this.label = label;
    }

    // Mengambil teks status yang enak dibaca.
    public String getLabel() {
        return label;
    }

    // Menunjukkan apakah status ini masih menahan kursi di jadwal.
    public boolean menahanKursi() {
        return this != DITOLAK;
    }

    // Menunjukkan apakah pesanan dihitung sebagai penjualan di laporan.
    public boolean dihitungSebagaiPenjualan() {
        return this == TERVERIFIKASI || this == DIGUNAKAN;
    }
}
