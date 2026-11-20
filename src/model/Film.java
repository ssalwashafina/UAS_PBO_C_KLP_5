package model;

import java.util.Comparator;

// Data film. Urutan alami film adalah berdasarkan judul (Comparable), pengurutan lain tersedia lewat Comparator.
public class Film implements Comparable<Film> {

    // Pengurut film dari durasi terpendek.
    public static final Comparator<Film> BERDASARKAN_DURASI = Comparator.comparingInt(Film::getDurasi);

    private String id;
    private String judul;
    private String genre;
    private int durasi;
    private int ratingUsia;

    // Membuat film baru.
    public Film(String id, String judul, String genre, int durasi, int ratingUsia) {
        this.id = id;
        setJudul(judul);
        setGenre(genre);
        setDurasi(durasi);
        setRatingUsia(ratingUsia);
    }

    // Mengambil kode film.
    public String getId() {
        return id;
    }

    // Mengambil judul.
    public String getJudul() {
        return judul;
    }

    // Mengubah judul.
    public void setJudul(String judul) {
        if (judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul film tidak boleh kosong.");
        }
        this.judul = judul.trim();
    }

    // Mengambil genre.
    public String getGenre() {
        return genre;
    }

    // Mengubah genre.
    public void setGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre tidak boleh kosong.");
        }
        this.genre = genre.trim();
    }

    // Mengambil durasi.
    public int getDurasi() {
        return durasi;
    }
