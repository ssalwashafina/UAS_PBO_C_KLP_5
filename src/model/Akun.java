package model;

import util.Keamanan;

// Kelas dasar semua akun. Dibuat abstract karena akun selalu berupa Admin atau User, tidak pernah akun "polos".
public abstract class Akun {

    private String username;
    private String passwordHash;
    private String nama;

    // Membuat akun.
    protected Akun(String username, String passwordHash, String nama) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nama = nama;
    }

    // Menampilkan menu utama sesuai jenis akun. Tiap subclass punya isi menu berbeda.
    public abstract void tampilkanMenu();

    // Mengambil nama peran akun.
    public abstract String getPeran();

    // Mencocokkan password yang diketik dengan hash yang tersimpan.
    public boolean cekPassword(String passwordPolos) {
        return passwordHash.equals(Keamanan.hash(username, passwordPolos));
    }

    // Mengambil username.
    public String getUsername() {
        return username;
    }

    // Mengubah username.
    public void setUsername(String username) {
        this.username = username;
    }

    // Mengambil hash password, dipakai saat menyimpan ke file.
    public String getPasswordHash() {
        return passwordHash;
    }

    // Mengganti hash password.
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Mengambil nama lengkap.
    public String getNama() {
        return nama;
    }

    // Mengubah nama lengkap.
    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public String toString() {
        return getPeran() + " " + username + " (" + nama + ")";
    }
}
