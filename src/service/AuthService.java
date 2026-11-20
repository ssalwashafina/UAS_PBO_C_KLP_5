package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.DataTidakDitemukanException;
import exception.LoginGagalException;
import model.Admin;
import model.Akun;
import model.User;
import util.Keamanan;

// Mengurus login dan register. Semua akun disimpan di HashMap dengan username (huruf kecil) sebagai kunci.
public class AuthService {

    // Membuat service akun yang masih kosong.
    public AuthService() {
    }

    private final Map<String, Akun> akun = new HashMap<>();

    // Memeriksa username dan password.
    public Akun login(String username, String password) throws LoginGagalException {
        Akun a = akun.get(username.toLowerCase());
        if (a == null || !a.cekPassword(password)) {
            throw new LoginGagalException("Username atau password salah.");
        }
        return a;
    }

    // Mendaftarkan user baru.
    public User register(String username, String password, String nama, LocalDate tanggalLahir) {
        if (!username.matches("[A-Za-z0-9_]{4,15}")) {
            throw new IllegalArgumentException("Username 4-15 karakter, hanya huruf, angka, dan garis bawah.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
        if (nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        }
        if (tanggalLahir.isAfter(LocalDate.now()) || tanggalLahir.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new IllegalArgumentException("Tanggal lahir tidak masuk akal.");
        }
        if (usernameAda(username)) {
            throw new IllegalArgumentException("Username '" + username + "' sudah dipakai.");
        }
        User u = new User(username, Keamanan.hash(username, password), nama.trim(), tanggalLahir);
        akun.put(username.toLowerCase(), u);
        return u;
    }

    // Membuat akun admin. Dipakai untuk data awal.
    public Admin buatAdmin(String username, String password, String nama) {
        Admin a = new Admin(username, Keamanan.hash(username, password), nama);
        akun.put(username.toLowerCase(), a);
        return a;
    }

    // Memasukkan akun yang sudah jadi, dipakai saat membaca dari file.
    public void tambahAkun(Akun a) {
        akun.put(a.getUsername().toLowerCase(), a);
    }

    // Mengecek apakah username sudah dipakai.
    public boolean usernameAda(String username) {
        return akun.containsKey(username.toLowerCase());
    }

    // Mencari user berdasarkan username.
    public User cariUser(String username) throws DataTidakDitemukanException {
        Akun a = akun.get(username.toLowerCase());
        if (!(a instanceof User)) {
            throw new DataTidakDitemukanException("User", username);
        }
        return (User) a;
    }

    // Mengambil semua akun, diurutkan berdasarkan username.
    public List<Akun> getSemuaAkun() {
        List<Akun> hasil = new ArrayList<>(akun.values());
        hasil.sort(Comparator.comparing(a -> a.getUsername().toLowerCase()));
        return hasil;
    }

    // Mengambil semua user (tanpa admin), diurutkan berdasarkan username.
    public List<User> getSemuaUser() {
        List<User> hasil = new ArrayList<>();
        for (Akun a : getSemuaAkun()) {
            if (a instanceof User) {
                hasil.add((User) a);
            }
        }
        return hasil;
    }
}
