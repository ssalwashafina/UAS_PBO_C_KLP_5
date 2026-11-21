package utama;

import exception.BioskopException;
import model.Akun;
import service.PemesananService;
import util.FileHandler;
import util.InputHelper;

// Kerangka menu setelah login.
public abstract class Menu {

    // Akun yang sedang login. Tipenya Akun, jadi tampilkanMenu() dipanggil secara polimorfik.
    protected final Akun akun;
    // Pengelola file untuk menyimpan data setelah tiap aksi.
    protected final FileHandler fileHandler;
    // Service pesanan, dipakai untuk menyapu pesanan kedaluwarsa.
    protected final PemesananService pemesananService;

    // Membuat kerangka menu.
    protected Menu(Akun akun, FileHandler fileHandler, PemesananService pemesananService) {
        this.akun = akun;
        this.fileHandler = fileHandler;
        this.pemesananService = pemesananService;
    }

    // Menjalankan perulangan menu sampai user memilih 0 (logout).
    public final void jalankan() {
        boolean lanjut = true;
        while (lanjut) {
            int dibatalkan = pemesananService.perbaruiKedaluwarsa();
            if (dibatalkan > 0) {
                infoKedaluwarsa(dibatalkan);
            }
            akun.tampilkanMenu();
            int pilihan = InputHelper.angka("Pilih menu: ", 0, jumlahMenu());
            if (pilihan == 0) {
                lanjut = false;
            } else {
                try {
                    tangani(pilihan);
                } catch (BioskopException | IllegalArgumentException | IllegalStateException e) {
                    System.out.println("\n  ! " + e.getMessage());
                    InputHelper.enter();
                }
                fileHandler.simpanSemua();
            }
        }
        System.out.println("\nSampai jumpa, " + akun.getNama() + ".");
    }

    // Dipanggil ketika ada pesanan menunggu yang baru saja kedaluwarsa. Bawaannya tidak menampilkan apa-apa.
    protected void infoKedaluwarsa(int jumlah) {
    }

    // Mengambil nomor pilihan terbesar di menu ini.
    protected abstract int jumlahMenu();

    // Mengerjakan satu pilihan menu.
    protected abstract void tangani(int pilihan) throws BioskopException;
}
