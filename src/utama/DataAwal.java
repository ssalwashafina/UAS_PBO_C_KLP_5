package utama;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import exception.BioskopException;
import harga.Voucher;
import model.Film;
import model.Jadwal;
import model.Studio;
import model.TipeStudio;
import model.User;
import payment.PembayaranFactory;
import service.AuthService;
import service.FilmService;
import service.JadwalService;
import service.PemesananService;

// Mengisi data contoh saat program dijalankan pertama kali (belum ada folder data):
public final class DataAwal {

    private DataAwal() {
    }

    // Mengisi semua service dengan data contoh.
    public static void isi(AuthService auth, FilmService film, JadwalService jadwal, PemesananService pemesanan) {
        auth.buatAdmin("admin", "admin123", "Administrator");
        User budi = auth.register("budi", "budi123", "Budi Santoso", LocalDate.of(2000, 5, 12));
        auth.register("sinta", "sinta123", "Sinta Rahma", LocalDate.of(2012, 8, 17));

        film.tambah(new Film("F001", "Laskar Pelangi", "Drama", 124, 0));
        film.tambah(new Film("F002", "Agak Laen", "Komedi", 119, 13));
        film.tambah(new Film("F003", "Pengabdi Setan 2: Communion", "Horor", 119, 17));
        film.tambah(new Film("F004", "Avengers: Endgame", "Aksi", 181, 13));
        film.tambah(new Film("F005", "Inside Out 2", "Animasi", 96, 0));
        film.tambah(new Film("F006", "Dilan 1990", "Drama", 110, 13));

        jadwal.tambahStudio(new Studio("S1", "Studio 1", TipeStudio.REGULAR, 6, 8));
        jadwal.tambahStudio(new Studio("S2", "Studio 2", TipeStudio.REGULAR, 7, 10));
        jadwal.tambahStudio(new Studio("S3", "Studio 3 Premium", TipeStudio.PREMIUM, 5, 6));

        LocalDate hariIni = LocalDate.now();
        tambahJadwal(film, jadwal, "F005", "S1", hariIni, 22, 30);
        for (int d = 1; d <= 3; d++) {
            LocalDate hari = hariIni.plusDays(d);
            tambahJadwal(film, jadwal, "F005", "S1", hari, 12, 0);
            tambahJadwal(film, jadwal, "F001", "S1", hari, 14, 0);
            tambahJadwal(film, jadwal, "F002", "S1", hari, 16, 30);
            tambahJadwal(film, jadwal, "F006", "S1", hari, 19, 0);
            tambahJadwal(film, jadwal, "F004", "S2", hari, 12, 0);
            tambahJadwal(film, jadwal, "F003", "S2", hari, 15, 30);
            tambahJadwal(film, jadwal, "F004", "S2", hari, 18, 0);
            tambahJadwal(film, jadwal, "F002", "S3", hari, 13, 0);
            tambahJadwal(film, jadwal, "F005", "S3", hari, 16, 0);
            tambahJadwal(film, jadwal, "F003", "S3", hari, 19, 30);
        }

        pesanContoh(pemesanan, jadwal, budi, "F001", hariIni.plusDays(1), 14, "D4", "D5");
        pesanContoh(pemesanan, jadwal, budi, "F004", hariIni.plusDays(1), 12, "E5", "E6", "E7");
        pesanContoh(pemesanan, jadwal, budi, "F002", hariIni.plusDays(2), 16, "C4");
    }

    private static void tambahJadwal(FilmService film, JadwalService jadwal, String filmId, String studioId,
                                     LocalDate hari, int jam, int menit) {
        try {
            String id = jadwal.buatIdJadwal();
            jadwal.tambah(new Jadwal(id, film.cariById(filmId), jadwal.cariStudio(studioId), hari,
                    LocalTime.of(jam, menit), 40000));
        } catch (BioskopException | IllegalArgumentException e) {
            // Jadwal hari ini yang jamnya sudah lewat sengaja dilewati.
        }
    }

    private static void pesanContoh(PemesananService pemesanan, JadwalService jadwal, User user, String filmId,
                                    LocalDate hari, int jam, String... kursi) {
        for (Jadwal j : jadwal.getAll()) {
            if (j.getFilm().getId().equals(filmId) && j.getTanggal().equals(hari) && j.getJam().getHour() == jam) {
                try {
                    boolean transfer = kursi.length > 2;
                    String kode = pemesanan.buatPesanan(user, j, Arrays.asList(kursi),
                            PembayaranFactory.dariKode(transfer ? "TRANSFER" : "EWALLET"),
                            transfer ? Voucher.HEMAT10 : null).getKodeBooking();
                    pemesanan.konfirmasi(kode);
                } catch (BioskopException e) {
                    // Pesanan contoh gagal dibuat: laporan awal saja yang jadi lebih sepi.
                }
                return;
            }
        }
    }
}
