
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
