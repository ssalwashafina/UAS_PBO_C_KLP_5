package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import exception.BioskopException;
import harga.Voucher;
import model.Admin;
import model.Akun;
import model.Film;
import model.Jadwal;
import model.Pemesanan;
import model.StatusPesanan;
import model.Studio;
import model.TipeStudio;
import model.User;
import payment.PembayaranFactory;
import service.AuthService;
import service.FilmService;
import service.JadwalService;
import service.PemesananService;

// Menyimpan dan membaca semua data ke file CSV di sebuah folder, supaya data tidak hilang saat program ditutup.
public class FileHandler {

    private final Path folder;
    private final AuthService auth;
    private final FilmService filmService;
    private final JadwalService jadwalService;
    private final PemesananService pemesananService;

    // Membuat pengelola file.
    public FileHandler(String folder, AuthService auth, FilmService filmService, JadwalService jadwalService, PemesananService pemesananService) {
        this.folder = Paths.get(folder);
        this.auth = auth;
        this.filmService = filmService;
        this.jadwalService = jadwalService;
        this.pemesananService = pemesananService;
    }

    // Mengecek apakah sudah ada data tersimpan dari sesi sebelumnya.
    public boolean adaData() {
        return Files.exists(folder.resolve("akun.csv"));
    }

    // ------------------------------------------------------------- simpan

    // Menyimpan semua data. Kegagalan menulis file hanya menampilkan peringatan dan tidak menghentikan program.
    public boolean simpanSemua() {
        try {
            Files.createDirectories(folder);

            List<String> akun = new ArrayList<>();
            akun.add("tipe,username,passwordHash,nama,tanggalLahir");
            for (Akun a : auth.getSemuaAkun()) {
                String lahir = a instanceof User ? ((User) a).getTanggalLahir().toString() : "";
                akun.add(baris(a.getPeran(), a.getUsername(), a.getPasswordHash(), a.getNama(), lahir));
            }
            tulis("akun.csv", akun);

            List<String> film = new ArrayList<>();
            film.add("id,judul,genre,durasi,ratingUsia");
            for (Film f : filmService.getAll()) {
                film.add(baris(f.getId(), f.getJudul(), f.getGenre(), String.valueOf(f.getDurasi()),
                        String.valueOf(f.getRatingUsia())));
            }
            tulis("film.csv", film);

            List<String> studio = new ArrayList<>();
            studio.add("id,nama,tipe,baris,kolom");
            for (Studio s : jadwalService.getAllStudio()) {
                studio.add(baris(s.getId(), s.getNama(), s.getTipe().name(), String.valueOf(s.getJumlahBaris()),
                        String.valueOf(s.getJumlahKolom())));
            }
            tulis("studio.csv", studio);

            List<String> jadwal = new ArrayList<>();
            jadwal.add("id,filmId,studioId,tanggal,jam,hargaDasar");
            for (Jadwal j : jadwalService.getAll()) {
                jadwal.add(baris(j.getId(), j.getFilm().getId(), j.getStudio().getId(), j.getTanggal().toString(),
                        j.getJam().toString(), String.valueOf((long) j.getHargaDasar())));
            }
            tulis("jadwal.csv", jadwal);

            List<String> pesanan = new ArrayList<>();
            pesanan.add("kode,username,jadwalId,kursi,hargaPerKursi,voucher,diskon,biayaAdmin,total,metode,status,"
                    + "waktuPesan,alasan");
            for (Pemesanan p : pemesananService.getAll()) {
                pesanan.add(baris(p.getKodeBooking(), p.getUser().getUsername(), p.getJadwal().getId(),
                        String.join("|", p.getKursi()), String.valueOf((long) p.getHargaPerKursi()),
                        p.getVoucher() == null ? "" : p.getVoucher().name(), String.valueOf((long) p.getDiskon()),
                        String.valueOf((long) p.getBiayaAdmin()), String.valueOf((long) p.getTotalHarga()),
                        p.getMetode().getKode(), p.getStatus().name(), p.getWaktuPesan().toString(),
                        p.getAlasan() == null ? "" : p.getAlasan()));
            }
            tulis("pemesanan.csv", pesanan);
            return true;
        } catch (IOException e) {
            System.out.println("  ! Peringatan: data gagal disimpan (" + e.getMessage() + ")");
            return false;
        }
    }

    private void tulis(String namaFile, List<String> isi) throws IOException {
        Path tujuan = folder.resolve(namaFile);
        Path sementara = folder.resolve(namaFile + ".tmp");
        Files.write(sementara, isi, StandardCharsets.UTF_8);
        Files.move(sementara, tujuan, StandardCopyOption.REPLACE_EXISTING);
    }

    // --------------------------------------------------------------- baca

    // Membaca semua data dari file ke service.
    public List<String> muatSemua() {
        List<String> peringatan = new ArrayList<>();

        for (String[] f : bacaCsv("akun.csv", peringatan)) {
            coba("akun.csv", f, peringatan, () -> {
                if (f[0].equals("ADMIN")) {
                    auth.tambahAkun(new Admin(f[1], f[2], f[3]));
                } else {
                    auth.tambahAkun(new User(f[1], f[2], f[3], LocalDate.parse(f[4])));
                }
            });
        }
        for (String[] f : bacaCsv("film.csv", peringatan)) {
            coba("film.csv", f, peringatan, () -> filmService.tambah(
                    new Film(f[0], f[1], f[2], Integer.parseInt(f[3]), Integer.parseInt(f[4]))));
        }
        for (String[] f : bacaCsv("studio.csv", peringatan)) {
            coba("studio.csv", f, peringatan, () -> jadwalService.tambahStudio(new Studio(f[0], f[1],
                    TipeStudio.valueOf(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]))));
        }
        for (String[] f : bacaCsv("jadwal.csv", peringatan)) {
            coba("jadwal.csv", f, peringatan, () -> jadwalService.muat(new Jadwal(f[0],
                    filmService.cariById(f[1]), jadwalService.cariStudio(f[2]), LocalDate.parse(f[3]),
                    LocalTime.parse(f[4]), Double.parseDouble(f[5]))));
        }
        for (String[] f : bacaCsv("pemesanan.csv", peringatan)) {
            coba("pemesanan.csv", f, peringatan, () -> {
                List<String> kursi = new ArrayList<>();
                for (String k : f[3].split("\\|")) {
                    kursi.add(k);
                }
                Voucher voucher = f[5].isEmpty() ? null : Voucher.valueOf(f[5]);
                Pemesanan p = new Pemesanan(f[0], auth.cariUser(f[1]), jadwalService.cariById(f[2]), kursi,
                        PembayaranFactory.dariKode(f[9]), Double.parseDouble(f[4]), voucher,
                        Double.parseDouble(f[6]), Double.parseDouble(f[7]), Double.parseDouble(f[8]),
                        LocalDateTime.parse(f[11]), StatusPesanan.valueOf(f[10]), f[12].isEmpty() ? null : f[12]);
                pemesananService.muat(p);
            });
        }
        return peringatan;
    }

    // Satu langkah pembacaan baris yang boleh melempar exception.
    private interface Langkah {
        void jalankan() throws BioskopException;
    }

    private void coba(String namaFile, String[] baris, List<String> peringatan, Langkah langkah) {
        try {
            langkah.jalankan();
        } catch (BioskopException | RuntimeException e) {
            peringatan.add(namaFile + ": baris dilewati (" + String.join(",", baris) + ") -> " + e.getMessage());
        }
    }

    private List<String[]> bacaCsv(String namaFile, List<String> peringatan) {
        List<String[]> hasil = new ArrayList<>();
        Path berkas = folder.resolve(namaFile);
        if (!Files.exists(berkas)) {
            return hasil;
        }
        try {
            List<String> baris = Files.readAllLines(berkas, StandardCharsets.UTF_8);
            for (int i = 1; i < baris.size(); i++) {
                if (!baris.get(i).trim().isEmpty()) {
                    hasil.add(pecah(baris.get(i)));
                }
            }
        } catch (IOException e) {
            peringatan.add(namaFile + ": gagal dibaca (" + e.getMessage() + ")");
        }
        return hasil;
    }

    // ----------------------------------------------------------- format CSV

    // Menggabungkan kolom menjadi satu baris CSV. Kolom yang memuat koma atau tanda kutip diberi kutip.
    static String baris(String... kolom) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kolom.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String s = kolom[i] == null ? "" : kolom[i];
            if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
                s = "\"" + s.replace("\"", "\"\"") + "\"";
            }
            sb.append(s);
        }
        return sb.toString();
    }

    // Memecah satu baris CSV menjadi kolom, termasuk kolom berkutip.
    static String[] pecah(String baris) {
        List<String> hasil = new ArrayList<>();
        StringBuilder sel = new StringBuilder();
        boolean dalamKutip = false;
        for (int i = 0; i < baris.length(); i++) {
            char c = baris.charAt(i);
            if (dalamKutip) {
                if (c == '"' && i + 1 < baris.length() && baris.charAt(i + 1) == '"') {
                    sel.append('"');
                    i++;
                } else if (c == '"') {
                    dalamKutip = false;
                } else {
                    sel.append(c);
                }
            } else if (c == '"') {
                dalamKutip = true;
            } else if (c == ',') {
                hasil.add(sel.toString());
                sel.setLength(0);
            } else {
                sel.append(c);
            }
        }
        hasil.add(sel.toString());
        return hasil.toArray(new String[0]);
    }
}
