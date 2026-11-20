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

    // Mengubah film. Nilai baru disalin ke film lama supaya jadwal yang menunjuk film itu ikut berubah.
    @Override
    public void ubah(Film dataBaru) throws DataTidakDitemukanException, DataSedangDipakaiException {
        Film lama = cariById(dataBaru.getId());
        if (lama.getDurasi() != dataBaru.getDurasi() && jadwalService != null
                && jadwalService.adaJadwalUntukFilm(lama.getId())) {
            throw new DataSedangDipakaiException("Durasi tidak bisa diubah karena film ini masih punya jadwal.");
        }
        lama.setJudul(dataBaru.getJudul());
        lama.setGenre(dataBaru.getGenre());
        lama.setDurasi(dataBaru.getDurasi());
        lama.setRatingUsia(dataBaru.getRatingUsia());
    }

    @Override
    public void hapus(String id) throws DataTidakDitemukanException, DataSedangDipakaiException {
        Film film = cariById(id);
        if (jadwalService != null && jadwalService.adaJadwalUntukFilm(id)) {
            throw new DataSedangDipakaiException("Film \"" + film.getJudul()
                    + "\" masih punya jadwal tayang. Hapus jadwalnya dulu.");
        }
        daftar.remove(film);
    }

    @Override
    public Film cariById(String id) throws DataTidakDitemukanException {
        for (Film f : daftar) {
            if (f.getId().equalsIgnoreCase(id)) {
                return f;
            }
        }
        throw new DataTidakDitemukanException("Film", id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(daftar);
    }

    // Mencari film yang judul atau genrenya memuat kata kunci.
    public List<Film> cari(String kataKunci) {
        String kunci = kataKunci.toLowerCase();
        List<Film> hasil = new ArrayList<>();
        for (Film f : daftar) {
            if (f.getJudul().toLowerCase().contains(kunci) || f.getGenre().toLowerCase().contains(kunci)) {
                hasil.add(f);
            }
        }
        return hasil;
    }

    // Mengurutkan film berdasarkan judul (urutan alami Film).
    public List<Film> urutkanJudul() {
        List<Film> hasil = getAll();
        Collections.sort(hasil);
        return hasil;
    }

    // Mengurutkan film dari durasi terpendek.
    public List<Film> urutkanDurasi() {
        List<Film> hasil = getAll();
        hasil.sort(Film.BERDASARKAN_DURASI);
        return hasil;
    }
}
