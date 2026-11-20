package harga;

import model.Jadwal;

// Harga mengikuti pengali tipe studio. Studio Premium lebih mahal dari Regular.
public class HargaStudioStrategy implements HargaStrategy {

    // Membuat aturan harga menurut tipe studio.
    public HargaStudioStrategy() {
    }

    @Override
    public String getNama() {
        return "Studio Premium (x1,5)";
    }

    @Override
    public boolean berlaku(Jadwal jadwal) {
        return jadwal.getStudio().getTipe().getPengaliHarga() != 1.0;
    }

    @Override
    public double sesuaikan(Jadwal jadwal, double hargaSaatIni) {
        return hargaSaatIni * jadwal.getStudio().getTipe().getPengaliHarga();
    }
}
