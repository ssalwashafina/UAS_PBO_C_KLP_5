
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

// Mengatur menu dan fitur yang dapat digunakan admin.
public class MenuAdmin extends Menu {

    // Menjalankan aksi menu yang dapat menghasilkan exception.
    private interface Aksi {
        void jalankan() throws BioskopException;
    }

    private final AuthService authService;
    private final FilmService filmService;
    private final JadwalService jadwalService;

    // Menghubungkan menu admin dengan layanan sistem.
    public MenuAdmin(Admin admin, FileHandler fileHandler,
                     AuthService authService, FilmService filmService,
                     JadwalService jadwalService,
                     PemesananService pemesananService) {
        super(admin, fileHandler, pemesananService);
        this.authService = authService;
        this.filmService = filmService;
        this.jadwalService = jadwalService;
    }

    // Menentukan jumlah pilihan pada menu admin.
    @Override
    protected int jumlahMenu() {
        return 9;
    }

    // Menampilkan informasi pesanan yang kedaluwarsa.
    @Override
    protected void infoKedaluwarsa(int jumlah) {
        System.out.println("\n  [Info] " + jumlah
                + " pesanan melewati batas konfirmasi "
                + pemesananService.getBatasMenit()
                + " menit dan ditolak otomatis. Kursinya sudah dilepas.");
    }

    // Mengarahkan pilihan admin ke fitur yang sudah tersedia.
    @Override
    protected void tangani(int pilihan) throws BioskopException {
        switch (pilihan) {
            case 7:
                TampilanCLI.header("DAFTAR USER");
                TabelData.user(authService.getSemuaUser(), pemesananService);
                InputHelper.enter();
                break;
            default:
                break;
        }
    }

    // Menangani kesalahan saat menjalankan fitur admin.
    private void aman(Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (BioskopException | IllegalArgumentException
                | IllegalStateException e) {
            System.out.println("\n  ! " + e.getMessage());
        }
    }
}
