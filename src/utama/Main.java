package utama;

import java.time.LocalDate;
import java.util.List;

import exception.LoginGagalException;
import harga.KalkulatorHarga;
import model.Admin;
import model.Akun;
import model.User;
import service.AuthService;
import service.FilmService;
import service.JadwalService;
import service.PemesananService;
import util.FileHandler;
import util.InputBerakhirException;
import util.InputHelper;
import util.TampilanCLI;

// Titik masuk program Sistem Pemesanan Tiket Bioskop.
public class Main {

    private static final int MAKS_PERCOBAAN_LOGIN = 3;

    private final AuthService auth = new AuthService();
    private final FilmService filmService = new FilmService();
    private final JadwalService jadwalService = new JadwalService();
    private final PemesananService pemesananService = new PemesananService(new KalkulatorHarga());
    private final FileHandler fileHandler;

    // Membuat aplikasi dan menyambungkan semua service.
    public Main(String folderData) {
        filmService.setJadwalService(jadwalService);
        jadwalService.setPunyaPesanan(pemesananService::adaPesananUntukJadwal);
        fileHandler = new FileHandler(folderData, auth, filmService, jadwalService, pemesananService);
    }

    // Menjalankan program.
    public static void main(String[] args) {
        new Main(args.length > 0 ? args[0] : "data").jalankan();
    }

    private void jalankan() {
        siapkanData();
        try {
            menuAwal();
        } catch (InputBerakhirException e) {
            System.out.println("\nInput berakhir.");
        }
        fileHandler.simpanSemua();
        System.out.println("Data tersimpan. Terima kasih.");
    }

    private void siapkanData() {
        if (fileHandler.adaData()) {
            List<String> peringatan = fileHandler.muatSemua();
            for (String p : peringatan) {
                System.out.println("  ! " + p);
            }
            System.out.println("Data dimuat dari file.");
        } else {
            DataAwal.isi(auth, filmService, jadwalService, pemesananService);
            fileHandler.simpanSemua();
            System.out.println("Data awal dibuat. Akun demo:");
            System.out.println("  admin / admin123   (Admin)");
            System.out.println("  budi  / budi123    (User, dewasa)");
            System.out.println("  sinta / sinta123   (User, 14 tahun, tidak bisa pesan film 17+)");
        }
    }

    private void menuAwal() {
        boolean lanjut = true;
        while (lanjut) {
            TampilanCLI.menu("TIKET BIOSKOP KITA", "Keluar", "Login", "Register (user baru)");
            switch (InputHelper.angka("Pilih menu: ", 0, 2)) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                default:
                    lanjut = false;
                    break;
            }
        }
    }

    private void login() {
        for (int percobaan = 1; percobaan <= MAKS_PERCOBAAN_LOGIN; percobaan++) {
            String username = InputHelper.teks("\nUsername: ");
            String password = InputHelper.teks("Password: ");
            try {
                Akun akun = auth.login(username, password);
                System.out.println("\nSelamat datang, " + akun.getNama() + " (" + akun.getPeran() + ").");
                Menu menu = akun instanceof Admin
                        ? new MenuAdmin((Admin) akun, fileHandler, auth, filmService, jadwalService, pemesananService)
                        : new MenuUser((User) akun, fileHandler, filmService, jadwalService, pemesananService);
                menu.jalankan();
                return;
            } catch (LoginGagalException e) {
                System.out.println("  ! " + e.getMessage() + " (percobaan " + percobaan + " dari "
                        + MAKS_PERCOBAAN_LOGIN + ")");
            }
        }
        System.out.println("\nTerlalu banyak percobaan gagal. Kembali ke menu awal.");
    }

    private void register() {
        TampilanCLI.header("REGISTER USER BARU");
        try {
            String username = InputHelper.teks("Username (4-15 karakter, huruf/angka/_): ");
            String password = InputHelper.teks("Password (minimal 6 karakter): ");
            String nama = InputHelper.teks("Nama lengkap: ");
            LocalDate lahir = InputHelper.tanggal("Tanggal lahir");
            auth.register(username, password, nama, lahir);
            fileHandler.simpanSemua();
            System.out.println("\n  Akun berhasil dibuat. Silakan login.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ! " + e.getMessage());
        }
    }
}
