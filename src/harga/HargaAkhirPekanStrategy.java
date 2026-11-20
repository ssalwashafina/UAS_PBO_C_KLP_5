package harga;

import java.time.DayOfWeek;

import model.Jadwal;

// Tambahan 20% untuk tayangan hari Sabtu dan Minggu.
public class HargaAkhirPekanStrategy implements HargaStrategy {

    // Membuat aturan harga akhir pekan.
    public HargaAkhirPekanStrategy() {
    }

    @Override
    public String getNama() {
        return "Akhir pekan (+20%)";
    }

    @Override
    public boolean berlaku(Jadwal jadwal) {
        DayOfWeek hari = jadwal.getTanggal().getDayOfWeek();
        return hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY;
    }

    @Override
    public double sesuaikan(Jadwal jadwal, double hargaSaatIni) {
        return hargaSaatIni * 1.20;
    }
}
