package payment;

import exception.PembayaranGagalException;

// Kontrak semua metode pembayaran.
public interface Pembayaran {

    // Mengambil kode singkat metode, dipakai untuk penyimpanan file.
    String getKode();

    // Mengambil nama metode untuk ditampilkan.
    String getNamaMetode();

    // Menghitung biaya admin untuk sebuah nominal.
    double getBiayaAdmin(double jumlah);

    // Memproses nominal menjadi jumlah akhir yang harus dibayar (nominal ditambah biaya admin).
    double proses(double total) throws PembayaranGagalException;

    // Menyusun petunjuk cara membayar untuk ditampilkan di struk.
    String buatInstruksi(String kodeBooking, double totalBayar);
}
