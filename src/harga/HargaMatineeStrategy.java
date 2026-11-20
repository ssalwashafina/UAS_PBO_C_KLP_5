package harga;

import java.time.DayOfWeek;
import java.time.LocalTime;

import model.Jadwal;

// Potongan 15% untuk tayangan matinee, yaitu sebelum pukul 15.00 di hari kerja.
public class HargaMatineeStrategy implements HargaStrategy {

    // Membuat aturan harga matinee.
    public HargaMatineeStrategy() {
    }

    private static final LocalTime BATAS_MATINEE = LocalTime.of(15, 0);

    @Override
    public String getNama() {
        return "Matinee hari kerja (-15%)";
    }

    @Override
    public boolean berlaku(Jadwal jadwal) {
        DayOfWeek hari = jadwal.getTanggal().getDayOfWeek();
        boolean hariKerja = hari != DayOfWeek.SATURDAY && hari != DayOfWeek.SUNDAY;
        return hariKerja && jadwal.getJam().isBefore(BATAS_MATINEE);
    }

    @Override
    public double sesuaikan(Jadwal jadwal, double hargaSaatIni) {
        return hargaSaatIni * 0.85;
    }
}
