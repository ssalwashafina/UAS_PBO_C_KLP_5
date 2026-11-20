package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exception.DataSedangDipakaiException;
import exception.DataTidakDitemukanException;
import model.Film;

// Mengelola daftar film: tambah, ubah, hapus, cari, dan urutkan.
public class FilmService implements CrudService<Film, String> {

    // Membuat service film yang masih kosong.
    public FilmService() {
    }

    private final List<Film> daftar = new ArrayList<>();
    private JadwalService jadwalService;

    // Menyambungkan JadwalService supaya film yang masih punya jadwal tidak bisa dihapus.
    public void setJadwalService(JadwalService jadwalService) {
        this.jadwalService = jadwalService;
    }

    // Membuat ID film berikutnya, misalnya F007.
    public String buatIdBaru() {
        int maks = 0;
        for (Film f : daftar) {
            maks = Math.max(maks, Integer.parseInt(f.getId().substring(1)));
        }
        return String.format("F%03d", maks + 1);
    }

    @Override
    public void tambah(Film film) {
        for (Film f : daftar) {
            if (f.getId().equalsIgnoreCase(film.getId())) {
                throw new IllegalArgumentException("ID film " + film.getId() + " sudah ada.");
            }
        }
        daftar.add(film);
    }

