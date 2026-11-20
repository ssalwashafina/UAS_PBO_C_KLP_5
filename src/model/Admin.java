package model;

import util.TampilanCLI;

// Akun pengelola bioskop. Bisa mengelola film, studio, jadwal, dan memproses pesanan.
public class Admin extends Akun {

    // Membuat akun admin.
    public Admin(String username, String passwordHash, String nama) {
        super(username, passwordHash, nama);
    }

    @Override
    public void tampilkanMenu() {
        TampilanCLI.menu("MENU ADMIN - " + getNama(), "Logout",
                "Kelola Film",
                "Kelola Studio",
                "Kelola Jadwal Tayang",
                "Daftar Pesanan",
                "Konfirmasi / Tolak Pesanan",
                "Check-in Tiket",
                "Daftar User",
                "Laporan & Dashboard",
                "Atur Batas Waktu Konfirmasi");
    }

    @Override
    public String getPeran() {
        return "ADMIN";
    }
}
