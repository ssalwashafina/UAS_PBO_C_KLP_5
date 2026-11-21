
package utama;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import exception.BioskopException;
import model.Admin;
import model.Film;
import model.Jadwal;
import model.Pemesanan;
import model.StatusPesanan;
import model.Studio;
import model.TipeStudio;
import service.AuthService;
import service.FilmService;
import service.JadwalService;
import service.PemesananService;
import util.FileHandler;
import util.InputHelper;
import util.TabelData;
import util.TampilanCLI;

// Menu admin untuk mengelola data dan transaksi bioskop.
public class MenuAdmin extends Menu {

    // Menjalankan aksi menu yang dapat menghasilkan exception.
    private interface Aksi {
        void jalankan() throws BioskopException;
    }

    private final AuthService authService;
    private final FilmService filmService;
    private final JadwalService jadwalService;

    // Menginisialisasi layanan yang digunakan menu admin.
    public MenuAdmin(Admin admin, FileHandler fileHandler, AuthService authService, FilmService filmService,
                     JadwalService jadwalService, PemesananService pemesananService) {
        super(admin, fileHandler, pemesananService);
        this.authService = authService;
        this.filmService = filmService;
        this.jadwalService = jadwalService;
    }

    @Override
    protected int jumlahMenu() {
        return 9;
    }

    @Override
    protected void infoKedaluwarsa(int jumlah) {
        System.out.println("\n  [Info] " + jumlah + " pesanan melewati batas konfirmasi "
                + pemesananService.getBatasMenit() + " menit dan ditolak otomatis. Kursinya sudah dilepas.");
    }

    // Menjalankan fitur berdasarkan pilihan admin.
    @Override
    protected void tangani(int pilihan) throws BioskopException {
        switch (pilihan) {
            case 1:
                kelolaFilm();
                break;
            case 2:
                kelolaStudio();
                break;
            case 3:
                kelolaJadwal();
                break;
            case 4:
                daftarPesanan();
                break;
            case 5:
                prosesPesanan();
                break;
            case 6:
                checkIn();
                break;
            case 7:
                TampilanCLI.header("DAFTAR USER");
                TabelData.user(authService.getSemuaUser(), pemesananService);
                InputHelper.enter();
                break;
            case 8:
                laporan();
                break;
            case 9:
                aturBatas();
                break;
            default:
                break;
        }
    }

    // Menangani kesalahan tanpa menghentikan submenu.
    private void aman(Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (BioskopException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n  ! " + e.getMessage());
        }
    }

    // Menampilkan submenu pengelolaan film.
    private void kelolaFilm() {
        boolean lanjut = true;
        while (lanjut) {
            TampilanCLI.menu("KELOLA FILM", "Kembali", "Lihat semua film", "Tambah film", "Ubah film",
                    "Hapus film", "Cari film", "Urutkan film");
            switch (InputHelper.angka("Pilih: ", 0, 6)) {
                case 1:
                    TabelData.film(filmService.getAll());
                    break;
                case 2:
                    aman(this::tambahFilm);
                    break;
                case 3:
                    aman(this::ubahFilm);
                    break;
                case 4:
                    aman(this::hapusFilm);
                    break;
                case 5:
                    TabelData.film(filmService.cari(InputHelper.teks("Kata kunci judul/genre: ")));
                    break;
                case 6:
                    System.out.println("  1. Judul A-Z   2. Durasi terpendek");
                    TabelData.film(InputHelper.angka("Pilih: ", 1, 2) == 1
                            ? filmService.urutkanJudul() : filmService.urutkanDurasi());
                    break;
                default:
                    lanjut = false;
                    break;
            }
            if (lanjut) {
                fileHandler.simpanSemua();
            }
        }
    }

    // Menambahkan film baru.
    private void tambahFilm() throws BioskopException {
        String id = filmService.buatIdBaru();
        System.out.println("\n  ID film baru: " + id);
        String judul = InputHelper.teks("Judul: ");
        String genre = InputHelper.teks("Genre: ");
        int durasi = InputHelper.angka("Durasi (menit): ", 1, 600);
        int rating = InputHelper.angka("Rating usia (0 = semua umur, 13, 17, 21): ", 0, 21);
        filmService.tambah(new Film(id, judul, genre, durasi, rating));
        System.out.println("  Film ditambahkan.");
    }

    // Mengubah informasi film berdasarkan ID.
    private void ubahFilm() throws BioskopException {
        TabelData.film(filmService.getAll());
        Film lama = filmService.cariById(InputHelper.teks("ID film yang diubah: "));
        System.out.println("  (tekan Enter untuk tetap memakai nilai lama)");
        String judul = InputHelper.teksDefault("Judul", lama.getJudul());
        String genre = InputHelper.teksDefault("Genre", lama.getGenre());
        int durasi = InputHelper.angkaDefault("Durasi (menit)", 1, 600, lama.getDurasi());
        int rating = InputHelper.angkaDefault("Rating usia", 0, 21, lama.getRatingUsia());
        filmService.ubah(new Film(lama.getId(), judul, genre, durasi, rating));
        System.out.println("  Film diubah.");
    }

    // Menghapus film setelah mendapat konfirmasi admin.
    private void hapusFilm() throws BioskopException {
        TabelData.film(filmService.getAll());
        Film film = filmService.cariById(InputHelper.teks("ID film yang dihapus: "));
        if (InputHelper.ya("  Hapus \"" + film.getJudul() + "\"?")) {
            filmService.hapus(film.getId());
            System.out.println("  Film dihapus.");
        }
    }

    // Menampilkan submenu pengelolaan studio.
    private void kelolaStudio() {
        boolean lanjut = true;
        while (lanjut) {
            TampilanCLI.menu("KELOLA STUDIO", "Kembali", "Lihat semua studio", "Tambah studio", "Ubah studio",
                    "Hapus studio");
            switch (InputHelper.angka("Pilih: ", 0, 4)) {
                case 1:
                    TabelData.studio(jadwalService.getAllStudio());
                    break;
                case 2:
                    aman(this::tambahStudio);
                    break;
                case 3:
                    aman(this::ubahStudio);
                    break;
                case 4:
                    aman(this::hapusStudio);
                    break;
                default:
                    lanjut = false;
                    break;
            }
            if (lanjut) {
                fileHandler.simpanSemua();
            }
        }
    }

    // Menentukan tipe studio Regular atau Premium.
    private TipeStudio tanyaTipe(TipeStudio bawaan) {
        System.out.println("  1. Regular   2. Premium (harga x1,5)");
        int p = bawaan == null
                ? InputHelper.angka("Tipe: ", 1, 2)
                : InputHelper.angkaDefault("Tipe", 1, 2, bawaan == TipeStudio.REGULAR ? 1 : 2);
        return p == 1 ? TipeStudio.REGULAR : TipeStudio.PREMIUM;
    }

    // Menambahkan studio baru beserta kapasitas kursinya.
    private void tambahStudio() {
        String id = jadwalService.buatIdStudio();
        System.out.println("\n  ID studio baru: " + id);
        String nama = InputHelper.teks("Nama studio: ");
        TipeStudio tipe = tanyaTipe(null);
        int baris = InputHelper.angka("Jumlah baris (1-" + Studio.MAKS_BARIS + "): ", 1, Studio.MAKS_BARIS);
        int kolom = InputHelper.angka("Jumlah kolom (1-" + Studio.MAKS_KOLOM + "): ", 1, Studio.MAKS_KOLOM);
        jadwalService.tambahStudio(new Studio(id, nama, tipe, baris, kolom));
        System.out.println("  Studio ditambahkan.");
    }

    // Mengubah data studio berdasarkan ID.
    private void ubahStudio() throws BioskopException {
        TabelData.studio(jadwalService.getAllStudio());
        Studio lama = jadwalService.cariStudio(InputHelper.teks("ID studio yang diubah: "));
        System.out.println("  (tekan Enter untuk tetap memakai nilai lama)");
        String nama = InputHelper.teksDefault("Nama", lama.getNama());
        TipeStudio tipe = tanyaTipe(lama.getTipe());
        int baris = InputHelper.angkaDefault("Jumlah baris", 1, Studio.MAKS_BARIS, lama.getJumlahBaris());
        int kolom = InputHelper.angkaDefault("Jumlah kolom", 1, Studio.MAKS_KOLOM, lama.getJumlahKolom());
        jadwalService.ubahStudio(new Studio(lama.getId(), nama, tipe, baris, kolom));
        System.out.println("  Studio diubah.");
    }

    // Menghapus studio setelah mendapat konfirmasi admin.
    private void hapusStudio() throws BioskopException {
        TabelData.studio(jadwalService.getAllStudio());
        Studio s = jadwalService.cariStudio(InputHelper.teks("ID studio yang dihapus: "));
        if (InputHelper.ya("  Hapus " + s.getNama() + "?")) {
            jadwalService.hapusStudio(s.getId());
            System.out.println("  Studio dihapus.");
        }
    }

    // Menampilkan submenu pengelolaan jadwal tayang.
    private void kelolaJadwal() {
        boolean lanjut = true;
        while (lanjut) {
            TampilanCLI.menu("KELOLA JADWAL TAYANG", "Kembali", "Lihat semua jadwal", "Tambah jadwal",
                    "Ubah jadwal", "Hapus jadwal");
            switch (InputHelper.angka("Pilih: ", 0, 4)) {
                case 1:
                    TabelData.jadwal(jadwalService.getAll(), pemesananService.getKalkulator());
                    break;
                case 2:
                    aman(this::tambahJadwal);
                    break;
                case 3:
                    aman(this::ubahJadwal);
                    break;
                case 4:
                    aman(this::hapusJadwal);
                    break;
                default:
                    lanjut = false;
                    break;
            }
            if (lanjut) {
                fileHandler.simpanSemua();
            }
        }
    }

    // Menambahkan jadwal berdasarkan film, studio, dan waktu tayang.
    private void tambahJadwal() throws BioskopException {
        TabelData.film(filmService.getAll());
        Film film = filmService.cariById(InputHelper.teks("ID film: "));
        TabelData.studio(jadwalService.getAllStudio());
        Studio studio = jadwalService.cariStudio(InputHelper.teks("ID studio: "));
        LocalDate tanggal = InputHelper.tanggal("Tanggal tayang");
        LocalTime jam = InputHelper.jam("Jam mulai");
        int harga = InputHelper.angka("Harga dasar tiket (Rp): ", 1000, 1000000);
        Jadwal j = new Jadwal(jadwalService.buatIdJadwal(), film, studio, tanggal, jam, harga);
        jadwalService.tambah(j);
        System.out.println("  Jadwal " + j.getId() + " ditambahkan. Selesai pukul " + j.getWaktuSelesai().toLocalTime()
                + ", harga per kursi " + TampilanCLI.rupiah(pemesananService.getKalkulator().hargaPerKursi(j)) + ".");
    }

    // Mengubah jadwal tayang berdasarkan ID.
    private void ubahJadwal() throws BioskopException {
        TabelData.jadwal(jadwalService.getAll(), pemesananService.getKalkulator());
        Jadwal lama = jadwalService.cariById(InputHelper.teks("ID jadwal yang diubah: "));
        System.out.println("  (tekan Enter untuk tetap memakai nilai lama)");
        Film film = filmService.cariById(InputHelper.teksDefault("ID film", lama.getFilm().getId()));
        Studio studio = jadwalService.cariStudio(InputHelper.teksDefault("ID studio", lama.getStudio().getId()));
        LocalDate tanggal = InputHelper.tanggalDefault("Tanggal", lama.getTanggal());
        LocalTime jam = InputHelper.jamDefault("Jam", lama.getJam());
        int harga = InputHelper.angkaDefault("Harga dasar (Rp)", 1000, 1000000, (int) lama.getHargaDasar());
        jadwalService.ubah(new Jadwal(lama.getId(), film, studio, tanggal, jam, harga));
        System.out.println("  Jadwal diubah.");
    }

    // Menghapus jadwal setelah mendapat konfirmasi admin.
    private void hapusJadwal() throws BioskopException {
        TabelData.jadwal(jadwalService.getAll(), pemesananService.getKalkulator());
        Jadwal j = jadwalService.cariById(InputHelper.teks("ID jadwal yang dihapus: "));
        if (InputHelper.ya("  Hapus jadwal " + j + "?")) {
            jadwalService.hapus(j.getId());
            System.out.println("  Jadwal dihapus.");
        }
    }

    // Menampilkan pesanan yang menunggu konfirmasi atau seluruh transaksi.
    private void daftarPesanan() {
        System.out.println("\n  1. Hanya yang MENUNGGU konfirmasi   2. Semua pesanan");
        boolean semua = InputHelper.angka("Pilih: ", 1, 2) == 2;
        TampilanCLI.header(semua ? "SEMUA PESANAN" : "PESANAN MENUNGGU KONFIRMASI");
        TabelData.pesanan(semua ? pemesananService.getAll() : pemesananService.getMenunggu());
        InputHelper.enter();
    }

    // Mengonfirmasi atau menolak pembayaran pesanan.
    private void prosesPesanan() throws BioskopException {
        List<Pemesanan> menunggu = pemesananService.getMenunggu();
        if (menunggu.isEmpty()) {
            System.out.println("\n  Tidak ada pesanan yang menunggu konfirmasi.");
            InputHelper.enter();
            return;
        }
        TampilanCLI.header("PESANAN MENUNGGU KONFIRMASI");
        TabelData.pesanan(menunggu);
        String kode = InputHelper.baca("\nKode booking (Enter = batal): ");
        if (kode.isEmpty()) {
            return;
        }
        Pemesanan p = pemesananService.cariKode(kode);
        TampilanCLI.struk(p, pemesananService.getBatasMenit());
        System.out.println("\n  1. Konfirmasi (pembayaran SUDAH diterima)");
        System.out.println("  2. Tolak (pembayaran BELUM diterima)");
        System.out.println("  0. Batal");
        int aksi = InputHelper.angka("Pilih: ", 0, 2);
        if (aksi == 1) {
            if (InputHelper.ya("  Yakin uang " + TampilanCLI.rupiah(p.getTotalHarga()) + " sudah masuk?")) {
                pemesananService.konfirmasi(p.getKodeBooking());
                System.out.println("\n  Pesanan " + p.getKodeBooking() + " TERVERIFIKASI. User bisa melihat tiketnya.");
            }
        } else if (aksi == 2) {
            String alasan = InputHelper.teksDefault("Alasan penolakan", "Pembayaran belum diterima");
            pemesananService.tolak(p.getKodeBooking(), alasan);
            System.out.println("\n  Pesanan " + p.getKodeBooking() + " DITOLAK. Kursi dilepas kembali.");
        }
        InputHelper.enter();
    }

    // Memeriksa keabsahan tiket saat pengguna masuk bioskop.
    private void checkIn() throws BioskopException {
        String kode = InputHelper.teks("\nKode booking di tiket: ");
        Pemesanan p = pemesananService.checkIn(kode);
        System.out.println("\n  TIKET SAH. Silakan masuk.");
        TampilanCLI.baris("Pemesan", p.getUser().getNama());
        TampilanCLI.baris("Film", p.getJadwal().getFilm().getJudul());
        TampilanCLI.baris("Studio", p.getJadwal().getStudio().getNama());
        TampilanCLI.baris("Jam", p.getJadwal().getJam().toString());
        TampilanCLI.baris("Kursi", String.join(", ", p.getKursi()));
        InputHelper.enter();
    }

    // Menampilkan laporan pendapatan dan statistik penjualan bioskop.
    private void laporan() {
        TampilanCLI.header("LAPORAN & DASHBOARD");
        TampilanCLI.baris("Pendapatan", TampilanCLI.rupiah(pemesananService.totalPendapatan())
                + " (pesanan terverifikasi dan terpakai)");
        TampilanCLI.baris("Tiket terjual", String.valueOf(pemesananService.totalTiketTerjual()));
        StringBuilder status = new StringBuilder();
        for (Map.Entry<StatusPesanan, Long> e : pemesananService.jumlahPerStatus().entrySet()) {
            status.append(e.getKey().getLabel()).append(" ").append(e.getValue()).append("   ");
        }
        TampilanCLI.baris("Pesanan", status.toString().trim());

        // Menampilkan peringkat film berdasarkan jumlah tiket terjual.
        System.out.println("\n  FILM TERLARIS (tiket terjual)");
        Map<String, Integer> peringkat = pemesananService.peringkatFilm();
        if (peringkat.isEmpty()) {
            System.out.println("  (belum ada penjualan)");
        } else {
            int tertinggi = peringkat.values().iterator().next();
            int no = 1;
            for (Map.Entry<String, Integer> e : peringkat.entrySet()) {
                int persen = (int) Math.round(100.0 * e.getValue() / tertinggi);
                System.out.println("  " + no++ + ". " + String.format("%-28s", TampilanCLI.potong(e.getKey(), 28)) + " "
                        + TampilanCLI.bar(persen, 20) + " " + e.getValue() + " tiket");
            }
        }

        // Menampilkan jumlah tiket terjual berdasarkan jam tayang.
        System.out.println("\n  JAM TAYANG TERSIBUK (tiket terjual)");
        Map<Integer, Integer> perJam = pemesananService.tiketPerJam();
        if (perJam.isEmpty()) {
            System.out.println("  (belum ada penjualan)");
        } else {
            int tertinggi = perJam.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            for (Map.Entry<Integer, Integer> e : perJam.entrySet()) {
                int persen = (int) Math.round(100.0 * e.getValue() / tertinggi);
                System.out.println("  " + String.format("%02d:00", e.getKey()) + "  " + TampilanCLI.bar(persen, 20)
                        + " " + e.getValue() + " tiket");
            }
        }

        // Menampilkan persentase kursi terisi pada jadwal mendatang.
        System.out.println("\n  OKUPANSI JADWAL MENDATANG (kursi terisi)");
        List<Jadwal> mendatang = jadwalService.getJadwalAktif(LocalDateTime.now());
        if (mendatang.isEmpty()) {
            System.out.println("  (tidak ada jadwal mendatang)");
        } else {
            int tampil = Math.min(10, mendatang.size());
            for (Jadwal j : mendatang.subList(0, tampil)) {
                System.out.println("  " + String.format("%-5s", j.getId()) + " "
                        + j.getTanggal().format(TampilanCLI.FORMAT_TANGGAL) + " " + j.getJam() + "  "
                        + String.format("%-26s", TampilanCLI.potong(j.getFilm().getJudul(), 26)) + " "
                        + TampilanCLI.bar(j.getPersenOkupansi(), 20) + " " + j.getJumlahTerisi() + "/"
                        + j.getStudio().getKapasitas());
            }
            if (mendatang.size() > tampil) {
                System.out.println("  ... " + tampil + " jadwal terdekat dari " + mendatang.size());
            }
            double rata = mendatang.stream().mapToInt(Jadwal::getPersenOkupansi).average().orElse(0);
            System.out.println("  Rata-rata okupansi seluruh jadwal mendatang: " + Math.round(rata) + "%");
        }
        InputHelper.enter();
    }

    // Mengatur batas waktu konfirmasi pembayaran pesanan.
    private void aturBatas() {
        System.out.println("\n  Batas konfirmasi sekarang: " + pemesananService.getBatasMenit() + " menit.");
        System.out.println("  Pesanan MENUNGGU yang melewati batas ini ditolak otomatis dan kursinya dilepas.");
        System.out.println("  (Pengaturan ini berlaku selama program berjalan.)");
        pemesananService.setBatasMenit(InputHelper.angka("Batas baru (menit, 1-1440): ", 1, 1440));
        System.out.println("  Batas diubah menjadi " + pemesananService.getBatasMenit() + " menit.");
    }
}
