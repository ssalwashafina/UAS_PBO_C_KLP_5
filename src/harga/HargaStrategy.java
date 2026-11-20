package harga;

import model.Jadwal;

// Satu aturan penyesuaian harga tiket (pola Strategy).
public interface HargaStrategy {

    // Mengambil nama aturan untuk ditampilkan di rincian harga.
    String getNama();

    // Mengecek apakah aturan ini berlaku untuk sebuah jadwal.
    boolean berlaku(Jadwal jadwal);

    // Menerapkan aturan pada harga yang sedang berjalan.
    double sesuaikan(Jadwal jadwal, double hargaSaatIni);
}
