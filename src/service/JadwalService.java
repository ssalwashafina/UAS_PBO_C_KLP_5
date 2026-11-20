package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import exception.DataSedangDipakaiException;
import exception.DataTidakDitemukanException;
import exception.JadwalBentrokException;
import model.Jadwal;
import model.Studio;

// Mengelola studio dan jadwal tayang. Jadwal baru dicek supaya tidak bentrok dengan jadwal lain di studio yang sama.
public class JadwalService implements CrudService<Jadwal, String> {

    // Membuat service jadwal dan studio yang masih kosong.
    public JadwalService() {
    }

    // Jeda minimal antar tayangan di studio yang sama, untuk membersihkan studio.
    public static final int JEDA_MENIT = 15;

    private final List<Studio> daftarStudio = new ArrayList<>();
    private final List<Jadwal> daftarJadwal = new ArrayList<>();
    private Predicate<String> punyaPesanan = id -> false;

    // Menyambungkan pemeriksa pesanan. Jadwal yang pernah dipesan tidak boleh dihapus.
    public void setPunyaPesanan(Predicate<String> punyaPesanan) {
        this.punyaPesanan = punyaPesanan;
    }

    // ---------------------------------------------------------------- studio

    // Membuat ID studio berikutnya, misalnya S4.
    public String buatIdStudio() {
        int maks = 0;
        for (Studio s : daftarStudio) {
            maks = Math.max(maks, Integer.parseInt(s.getId().substring(1)));
        }
        return "S" + (maks + 1);
    }

    // Menambah studio.
    public void tambahStudio(Studio studio) {
        for (Studio s : daftarStudio) {
            if (s.getId().equalsIgnoreCase(studio.getId())) {
                throw new IllegalArgumentException("ID studio " + studio.getId() + " sudah ada.");
            }
        }
        daftarStudio.add(studio);
    }

    // Mengubah studio. Tipe dan ukuran tidak boleh berubah kalau studio sudah punya jadwal.
    public void ubahStudio(Studio dataBaru) throws DataTidakDitemukanException, DataSedangDipakaiException {
        Studio lama = cariStudio(dataBaru.getId());
        boolean strukturBerubah = lama.getTipe() != dataBaru.getTipe()
                || lama.getJumlahBaris() != dataBaru.getJumlahBaris()
                || lama.getJumlahKolom() != dataBaru.getJumlahKolom();
        if (strukturBerubah && adaJadwalUntukStudio(lama.getId())) {
            throw new DataSedangDipakaiException("Tipe dan ukuran tidak bisa diubah karena studio masih punya jadwal.");
        }
        lama.setNama(dataBaru.getNama());
        lama.setTipe(dataBaru.getTipe());
        lama.setUkuran(dataBaru.getJumlahBaris(), dataBaru.getJumlahKolom());
    }

    // Menghapus studio.
    public void hapusStudio(String id) throws DataTidakDitemukanException, DataSedangDipakaiException {
        Studio s = cariStudio(id);
        if (adaJadwalUntukStudio(id)) {
            throw new DataSedangDipakaiException("Studio masih punya jadwal tayang. Hapus jadwalnya dulu.");
        }
        daftarStudio.remove(s);
    }

    // Mencari studio.
    public Studio cariStudio(String id) throws DataTidakDitemukanException {
        for (Studio s : daftarStudio) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        throw new DataTidakDitemukanException("Studio", id);
    }

    // Mengambil semua studio.
    public List<Studio> getAllStudio() {
        return new ArrayList<>(daftarStudio);
    }

    // ---------------------------------------------------------------- jadwal

    // Membuat ID jadwal berikutnya, misalnya J012.
    public String buatIdJadwal() {
        int maks = 0;
        for (Jadwal j : daftarJadwal) {
            maks = Math.max(maks, Integer.parseInt(j.getId().substring(1)));
        }
        return String.format("J%03d", maks + 1);
    }

    // Menambah jadwal baru. Waktu mulai harus di masa depan dan tidak boleh bentrok.
    @Override
    public void tambah(Jadwal jadwal) throws JadwalBentrokException {
        if (!jadwal.getWaktuMulai().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Waktu tayang harus di masa depan.");
        }
        periksaBentrok(jadwal, null);
        daftarJadwal.add(jadwal);
    }

    // Memasukkan jadwal tanpa pemeriksaan waktu dan bentrok. Dipakai saat membaca dari file, karena data lama boleh berisi jadwal yang sudah lewat.
    public void muat(Jadwal jadwal) {
        daftarJadwal.add(jadwal);
    }

    private void periksaBentrok(Jadwal baru, String idDiabaikan) throws JadwalBentrokException {
        for (Jadwal j : daftarJadwal) {
            if (j.getId().equals(idDiabaikan)) {
                continue;
            }
            if (baru.bentrokDengan(j, JEDA_MENIT)) {
                throw new JadwalBentrokException("Bentrok dengan jadwal " + j.getId() + " (" + j.getFilm().getJudul()
                        + ", " + j.getTanggal() + " " + j.getJam() + " s.d. " + j.getWaktuSelesai().toLocalTime()
                        + "). Beri jeda minimal " + JEDA_MENIT + " menit.");
            }
        }
    }

    // Mengubah jadwal. Tidak boleh kalau sudah ada kursi terpesan.
    @Override
    public void ubah(Jadwal dataBaru)
            throws DataTidakDitemukanException, DataSedangDipakaiException, JadwalBentrokException {
        Jadwal lama = cariById(dataBaru.getId());
        if (lama.getJumlahTerisi() > 0 || punyaPesanan.test(lama.getId())) {
            throw new DataSedangDipakaiException("Jadwal ini sudah pernah dipesan, tidak bisa diubah.");
        }
        if (!dataBaru.getWaktuMulai().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Waktu tayang harus di masa depan.");
        }
        periksaBentrok(dataBaru, lama.getId());
        lama.setFilm(dataBaru.getFilm());
        lama.setStudio(dataBaru.getStudio());
        lama.setTanggal(dataBaru.getTanggal());
        lama.setJam(dataBaru.getJam());
        lama.setHargaDasar(dataBaru.getHargaDasar());
    }

    // Menghapus jadwal yang belum pernah dipesan.
    @Override
    public void hapus(String id) throws DataTidakDitemukanException, DataSedangDipakaiException {
        Jadwal j = cariById(id);
        if (j.getJumlahTerisi() > 0 || punyaPesanan.test(id)) {
            throw new DataSedangDipakaiException("Jadwal ini sudah pernah dipesan, tidak bisa dihapus.");
        }
        daftarJadwal.remove(j);
    }

    @Override
    public Jadwal cariById(String id) throws DataTidakDitemukanException {
        for (Jadwal j : daftarJadwal) {
            if (j.getId().equalsIgnoreCase(id)) {
                return j;
            }
        }
        throw new DataTidakDitemukanException("Jadwal", id);
    }

    // Mengambil semua jadwal, termasuk yang sudah lewat, urut dari yang paling awal.
    @Override
    public List<Jadwal> getAll() {
        List<Jadwal> hasil = new ArrayList<>(daftarJadwal);
        hasil.sort(Comparator.comparing(Jadwal::getWaktuMulai));
        return hasil;
    }

    // Mengambil jadwal yang belum mulai tayang, urut dari yang paling awal.
    public List<Jadwal> getJadwalAktif(LocalDateTime sekarang) {
        List<Jadwal> hasil = new ArrayList<>();
        for (Jadwal j : getAll()) {
            if (!j.sudahMulai(sekarang)) {
                hasil.add(j);
            }
        }
        return hasil;
    }

    // Mengecek apakah sebuah film punya jadwal, lampau maupun mendatang.
    public boolean adaJadwalUntukFilm(String filmId) {
        for (Jadwal j : daftarJadwal) {
            if (j.getFilm().getId().equalsIgnoreCase(filmId)) {
                return true;
            }
        }
        return false;
    }

    // Mengecek apakah sebuah studio punya jadwal, lampau maupun mendatang.
    public boolean adaJadwalUntukStudio(String studioId) {
        for (Jadwal j : daftarJadwal) {
            if (j.getStudio().getId().equalsIgnoreCase(studioId)) {
                return true;
            }
        }
        return false;
    }
}
