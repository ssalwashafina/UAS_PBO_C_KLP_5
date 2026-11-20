package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import exception.KursiTidakTersediaException;

// Jadwal tayang. Jadwal "memiliki" sebuah Film dan sebuah Studio (composition), dan mencatat kursi mana yang sudah terisi beserta kode booking pemiliknya.
public class Jadwal {

    private String id;
    private Film film;
    private Studio studio;
    private LocalDate tanggal;
    private LocalTime jam;
    private double hargaDasar;
    private final Map<String, String> kursiTerisi = new HashMap<>();

    // Membuat jadwal tayang.
    public Jadwal(String id, Film film, Studio studio, LocalDate tanggal, LocalTime jam, double hargaDasar) {
        this.id = id;
        this.film = film;
        this.studio = studio;
        this.tanggal = tanggal;
        this.jam = jam;
        setHargaDasar(hargaDasar);
    }

    // Mengambil kode jadwal.
    public String getId() {
        return id;
    }

    // Mengambil film.
    public Film getFilm() {
        return film;
    }

    // Mengganti film.
    public void setFilm(Film film) {
        this.film = film;
    }

    // Mengambil studio.
    public Studio getStudio() {
        return studio;
    }

    // Mengganti studio.
    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    // Mengambil tanggal tayang.
    public LocalDate getTanggal() {
        return tanggal;
    }

    // Mengganti tanggal tayang.
    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    // Mengambil jam mulai.
    public LocalTime getJam() {
        return jam;
    }

    // Mengganti jam mulai.
    public void setJam(LocalTime jam) {
        this.jam = jam;
    }

    // Mengambil harga dasar tiket.
    public double getHargaDasar() {
        return hargaDasar;
    }

    // Mengubah harga dasar.
    public void setHargaDasar(double hargaDasar) {
        if (hargaDasar <= 0) {
            throw new IllegalArgumentException("Harga tiket harus lebih dari 0.");
        }
        this.hargaDasar = hargaDasar;
    }

    // Menghitung waktu mulai lengkap dengan tanggal.
    public LocalDateTime getWaktuMulai() {
        return LocalDateTime.of(tanggal, jam);
    }

    // Menghitung waktu selesai berdasarkan durasi film. Bisa jatuh di hari berikutnya.
    public LocalDateTime getWaktuSelesai() {
        return getWaktuMulai().plusMinutes(film.getDurasi());
    }

    // Mengecek apakah jadwal sudah mulai tayang.
    public boolean sudahMulai(LocalDateTime sekarang) {
        return !getWaktuMulai().isAfter(sekarang);
    }

    // Mengecek apakah jadwal ini bertabrakan dengan jadwal lain di studio yang sama.
    public boolean bentrokDengan(Jadwal lain, int bufferMenit) {
        if (!studio.getId().equals(lain.studio.getId())) {
            return false;
        }
        return getWaktuMulai().isBefore(lain.getWaktuSelesai().plusMinutes(bufferMenit))
                && lain.getWaktuMulai().isBefore(getWaktuSelesai().plusMinutes(bufferMenit));
    }

    // Mengecek apakah sebuah kursi sudah terisi.
    public boolean isKursiTerisi(String kursi) {
        return kursiTerisi.containsKey(kursi.toUpperCase());
    }

    // Menahan beberapa kursi untuk satu kode booking. Semua kursi diperiksa dulu, jadi kalau ada satu yang terisi, tidak ada kursi yang ditahan.
    public void tahanKursi(List<String> kursi, String kodeBooking) throws KursiTidakTersediaException {
        for (String k : kursi) {
            if (isKursiTerisi(k)) {
                throw new KursiTidakTersediaException("Kursi " + k.toUpperCase() + " sudah terisi.");
            }
        }
        for (String k : kursi) {
            kursiTerisi.put(k.toUpperCase(), kodeBooking);
        }
    }

    // Melepas semua kursi milik sebuah kode booking.
    public void lepasKursi(String kodeBooking) {
        Iterator<Map.Entry<String, String>> it = kursiTerisi.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(kodeBooking)) {
                it.remove();
            }
        }
    }

    // Mengambil salinan peta kursi terisi (kursi menuju kode booking).
    public Map<String, String> getKursiTerisi() {
        return Collections.unmodifiableMap(kursiTerisi);
    }

    // Mengambil daftar kursi terisi.
    public List<String> getDaftarKursiTerisi() {
        return new ArrayList<>(kursiTerisi.keySet());
    }

    // Menghitung kursi terisi.
    public int getJumlahTerisi() {
        return kursiTerisi.size();
    }

    // Menghitung kursi yang masih kosong.
    public int getJumlahKosong() {
        return studio.getKapasitas() - kursiTerisi.size();
    }

    // Menghitung persentase kursi terisi.
    public int getPersenOkupansi() {
        return (int) Math.round(100.0 * kursiTerisi.size() / studio.getKapasitas());
    }

    @Override
    public String toString() {
        return id + " | " + film.getJudul() + " | " + studio.getNama() + " | " + tanggal + " " + jam;
    }
}
