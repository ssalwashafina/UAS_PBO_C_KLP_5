package model;

import java.time.LocalDate;
import java.time.Period;

import util.TampilanCLI;

// Akun pelanggan. Menyimpan tanggal lahir supaya rating usia film bisa dicek.
public class User extends Akun {

    private LocalDate tanggalLahir;

    // Membuat akun user.
    public User(String username, String passwordHash, String nama, LocalDate tanggalLahir) {
        super(username, passwordHash, nama);
        this.tanggalLahir = tanggalLahir;
    }

    // Mengambil tanggal lahir.
    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    // Mengubah tanggal lahir.
    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    } 

// Menghitung umur pada tanggal tertentu.
    public int getUmur(LocalDate acuan) {
        return Period.between(tanggalLahir, acuan).getYears();
    }

    // Menghitung umur saat ini.
    public int getUmur() {
        return getUmur(LocalDate.now());
    }

    @Override
    public void tampilkanMenu() {
        TampilanCLI.menu("MENU USER - " + getNama(), "Logout",
                "Daftar Film & Jadwal Tayang",
                "Cari / Urutkan Film",
                "Pesan Tiket",
                "Riwayat Pesanan",
                "Lihat Tiket / Struk");
    }

    @Override
    public String getPeran() {
        return "USER";
    }
}
