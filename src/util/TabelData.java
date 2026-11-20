package util;

import java.util.ArrayList;
import java.util.List;

import harga.KalkulatorHarga;
import model.Akun;
import model.Film;
import model.Jadwal;
import model.Pemesanan;
import model.Studio;
import model.User;
import service.PemesananService;

// Mengubah daftar data (film, studio, jadwal, pesanan, user) menjadi tabel di layar.
public final class TabelData {

    private TabelData() {
    }

    // Mencetak tabel film.
    public static void film(List<Film> daftar) {
        if (daftar.isEmpty()) {
            System.out.println("  (tidak ada film)");
            return;
        }
        List<String[]> baris = new ArrayList<>();
        for (Film f : daftar) {
            baris.add(new String[] {f.getId(), f.getJudul(), f.getGenre(), f.getDurasi() + " mnt", f.getLabelRating()});
        }
        TampilanCLI.tabel(new String[] {"ID", "Judul", "Genre", "Durasi", "Rating"}, baris);
    }

    // Mencetak tabel studio.
    public static void studio(List<Studio> daftar) {
        if (daftar.isEmpty()) {
            System.out.println("  (tidak ada studio)");
            return;
        }
        List<String[]> baris = new ArrayList<>();
        for (Studio s : daftar) {
            baris.add(new String[] {s.getId(), s.getNama(), s.getTipe().getLabel(),
                    s.getJumlahBaris() + " x " + s.getJumlahKolom(), String.valueOf(s.getKapasitas())});
        }
        TampilanCLI.tabel(new String[] {"ID", "Nama", "Tipe", "Baris x Kolom", "Kursi"}, baris);
    }

    // Mencetak tabel jadwal lengkap dengan harga per kursi yang sudah memakai aturan harga.
    public static void jadwal(List<Jadwal> daftar, KalkulatorHarga kalkulator) {
        if (daftar.isEmpty()) {
            System.out.println("  (tidak ada jadwal)");
            return;
        }
        List<String[]> baris = new ArrayList<>();
        for (Jadwal j : daftar) {
            baris.add(new String[] {
                j.getId(),
                j.getFilm().getJudul() + " [" + j.getFilm().getLabelRating() + "]",
                j.getStudio().getNama(),
                TampilanCLI.namaHari(j.getTanggal()) + " " + j.getTanggal().format(TampilanCLI.FORMAT_TANGGAL),
                j.getJam() + "-" + j.getWaktuSelesai().toLocalTime(),
                TampilanCLI.rupiah(kalkulator.hargaPerKursi(j)),
                j.getJumlahKosong() + "/" + j.getStudio().getKapasitas()
            });
        }
        TampilanCLI.tabel(new String[] {"ID", "Film", "Studio", "Tanggal", "Jam", "Harga", "Kosong"}, baris);
    }

    // Mencetak tabel pesanan.
    public static void pesanan(List<Pemesanan> daftar) {
        if (daftar.isEmpty()) {
            System.out.println("  (tidak ada pesanan)");
            return;
        }
        List<String[]> baris = new ArrayList<>();
        for (Pemesanan p : daftar) {
            baris.add(new String[] {
                p.getKodeBooking(),
                p.getUser().getUsername(),
                p.getJadwal().getFilm().getJudul(),
                p.getJadwal().getTanggal().format(TampilanCLI.FORMAT_TANGGAL) + " " + p.getJadwal().getJam(),
                String.join(",", p.getKursi()),
                TampilanCLI.rupiah(p.getTotalHarga()),
                p.getMetode().getKode(),
                p.getStatus().getLabel()
            });
        }
        TampilanCLI.tabel(new String[] {"Kode", "Pemesan", "Film", "Tayang", "Kursi", "Total", "Metode", "Status"},
                baris);
    }

    // Mencetak tabel user beserta jumlah pesanannya.
    public static void user(List<User> daftar, PemesananService pesanan) {
        if (daftar.isEmpty()) {
            System.out.println("  (belum ada user)");
            return;
        }
        List<String[]> baris = new ArrayList<>();
        for (User u : daftar) {
            baris.add(new String[] {u.getUsername(), u.getNama(),
                    u.getTanggalLahir().format(TampilanCLI.FORMAT_TANGGAL), u.getUmur() + " th",
                    String.valueOf(pesanan.getRiwayat(u).size())});
        }
        TampilanCLI.tabel(new String[] {"Username", "Nama", "Tgl lahir", "Umur", "Pesanan"}, baris);
    }

    // Mencetak daftar akun apa adanya, dipakai untuk pemeriksaan cepat.
    public static void akun(List<Akun> daftar) {
        for (Akun a : daftar) {
            System.out.println("  " + a);
        }
    }
}
