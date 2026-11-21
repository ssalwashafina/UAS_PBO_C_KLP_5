package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import exception.BioskopException;
import exception.DataTidakDitemukanException;
import exception.KursiTidakTersediaException;
import exception.PembayaranGagalException;
import exception.UsiaTidakMemenuhiException;
import harga.KalkulatorHarga;
import harga.RincianHarga;
import harga.Voucher;
import model.Jadwal;
import model.Pemesanan;
import model.StatusPesanan;
import model.Studio;
import model.User;
import payment.Pembayaran;

// Mengelola pemesanan tiket bioskop, mulai dari pemilihan kursi hingga pencatatan penggunaan tiket dan pelaporan penjualan.
public class PemesananService {

    public static final int MAKS_KURSI = 6;

    private final List<Pemesanan> daftar = new ArrayList<>();
    private final KalkulatorHarga kalkulator;
    private int batasMenit = 30;

    // Membuat layanan pemesanan dengan kalkulator harga yang diberikan.
    public PemesananService(KalkulatorHarga kalkulator) {
        this.kalkulator = kalkulator;
    }

    // Mengembalikan kalkulator harga yang digunakan layanan pemesanan.
    public KalkulatorHarga getKalkulator() {
        return kalkulator;
    }

    // Mengembalikan batas waktu konfirmasi pembayaran dalam menit.
    public int getBatasMenit() {
        return batasMenit;
    }

    // Menetapkan batas waktu konfirmasi pembayaran antara 1 dan 1440 menit.
    public void setBatasMenit(int batasMenit) {
        if (batasMenit < 1 || batasMenit > 1440) {
            throw new IllegalArgumentException("Batas waktu harus 1 sampai 1440 menit.");
        }
        this.batasMenit = batasMenit;
    }


    // Menghitung rincian harga berdasarkan jadwal, jumlah kursi, voucer, dan metode pembayaran.
    public RincianHarga hitungRincian(Jadwal jadwal, int jumlah, Voucher voucher, Pembayaran metode)
            throws PembayaranGagalException {
        return kalkulator.hitung(jadwal, jumlah, voucher, metode);
    }

    // Mengembalikan nama baku kursi jika kodenya valid, kursinya kosong, dan belum dipilih.
    public String validasiKursi(Jadwal jadwal, String kode, List<String> sudahDipilih)
            throws KursiTidakTersediaException {
        String nama;
        try {
            int[] posisi = jadwal.getStudio().parseKursi(kode);
            nama = Studio.namaKursi(posisi[0], posisi[1]);
        } catch (IllegalArgumentException e) {
            throw new KursiTidakTersediaException(e.getMessage());
        }
        if (sudahDipilih.contains(nama)) {
            throw new KursiTidakTersediaException("Kursi " + nama + " sudah kamu pilih.");
        }
        if (jadwal.isKursiTerisi(nama)) {
            throw new KursiTidakTersediaException("Kursi " + nama + " sudah terisi.");
        }
        return nama;
    }

    // Merekomendasikan kursi dekat posisi ideal dengan mengutamakan kursi berdampingan.
    public SaranKursi rekomendasiKursi(Jadwal jadwal, int jumlah) {
        if (jumlah < 1 || jumlah > jadwal.getJumlahKosong()) {
            return null;
        }
        Studio s = jadwal.getStudio();
        int baris = s.getJumlahBaris();
        int kolom = s.getJumlahKolom();
        double tengahKolom = (kolom - 1) / 2.0;
        double barisIdeal = (baris - 1) * 0.6;

        double terbaik = Double.MAX_VALUE;
        List<String> pilihan = null;
        for (int b = 0; b < baris; b++) {
            for (int mulai = 0; mulai + jumlah <= kolom; mulai++) {
                boolean kosong = true;
                for (int k = mulai; k < mulai + jumlah; k++) {
                    if (jadwal.isKursiTerisi(Studio.namaKursi(b, k))) {
                        kosong = false;
                        break;
                    }
                }
                if (!kosong) {
                    continue;
                }
                double pusat = mulai + (jumlah - 1) / 2.0;
                double skor = Math.abs(pusat - tengahKolom) + 0.8 * Math.abs(b - barisIdeal);
                if (skor < terbaik) {
                    terbaik = skor;
                    pilihan = new ArrayList<>();
                    for (int k = mulai; k < mulai + jumlah; k++) {
                        pilihan.add(Studio.namaKursi(b, k));
                    }
                }
            }
        }
        if (pilihan != null) {
            return new SaranKursi(pilihan, true);
        }

        // Jika kursi berdampingan tidak mencukupi, pilih kursi kosong dengan skor jarak terkecil.
        final double idealBaris = barisIdeal;
        final double idealKolom = tengahKolom;
        Map<String, Double> skorKursi = new HashMap<>();
        for (int b = 0; b < baris; b++) {
            for (int k = 0; k < kolom; k++) {
                String nama = Studio.namaKursi(b, k);
                if (!jadwal.isKursiTerisi(nama)) {
                    skorKursi.put(nama, Math.abs(k - idealKolom) + 0.8 * Math.abs(b - idealBaris));
                }
            }
        }
        List<String> terdekat = skorKursi.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                .limit(jumlah)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
        return new SaranKursi(terdekat, false);
    }

    // Memeriksa syarat pemesanan, menahan kursi, dan mencatat pesanan dengan status MENUNGGU.
    public Pemesanan buatPesanan(User user, Jadwal jadwal, List<String> kursi, Pembayaran metode, Voucher voucher)
            throws BioskopException {
        perbaruiKedaluwarsa();
        LocalDateTime sekarang = LocalDateTime.now();
        if (jadwal.sudahMulai(sekarang)) {
            throw new BioskopException("Jadwal ini sudah dimulai dan tidak bisa dipesan lagi.");
        }
        if (kursi.isEmpty() || kursi.size() > MAKS_KURSI) {
            throw new BioskopException("Jumlah kursi per pesanan 1 sampai " + MAKS_KURSI + ".");
        }
        int umur = user.getUmur();
        if (umur < jadwal.getFilm().getRatingUsia()) {
            throw new UsiaTidakMemenuhiException(jadwal.getFilm().getJudul(), jadwal.getFilm().getRatingUsia(), umur);
        }
        List<String> rapi = new ArrayList<>();
        for (String k : kursi) {
            rapi.add(validasiKursi(jadwal, k, rapi));
        }
        RincianHarga rincian = kalkulator.hitung(jadwal, rapi.size(), voucher, metode);
        String kode = kodeBookingBaru(sekarang.toLocalDate());
        jadwal.tahanKursi(rapi, kode);
        Pemesanan p = new Pemesanan(kode, user, jadwal, rapi, metode, rincian.getHargaPerKursi(), voucher,
                rincian.getDiskon(), rincian.getBiayaAdmin(), rincian.getTotalBayar(), sekarang,
                StatusPesanan.MENUNGGU, null);
        daftar.add(p);
        return p;
    }

    // Membuat kode pemesanan dari tanggal dan nomor urut pesanan pada hari tersebut.
    private String kodeBookingBaru(LocalDate hari) {
        String awalan = "BK-" + hari.format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        int urut = 1;
        for (Pemesanan p : daftar) {
            if (p.getKodeBooking().startsWith(awalan)) {
                urut++;
            }
        }
        return awalan + String.format("%03d", urut);
    }

    // Memuat pesanan dan menahan kembali kursinya jika status pesanan mengharuskannya.
    public void muat(Pemesanan p) throws KursiTidakTersediaException {
        if (p.getStatus().menahanKursi()) {
            p.getJadwal().tahanKursi(p.getKursi(), p.getKodeBooking());
        }
        daftar.add(p);
    }


    // Menolak pesanan yang kedaluwarsa pada waktu yang diberikan, melepas kursinya, dan mengembalikan jumlah pesanan tersebut.
    public int perbaruiKedaluwarsa(LocalDateTime sekarang) {
        int jumlah = 0;
        for (Pemesanan p : daftar) {
            if (p.isKedaluwarsa(sekarang, batasMenit)) {
                p.setStatus(StatusPesanan.DITOLAK);
                p.setAlasan("Kedaluwarsa: pembayaran belum terkonfirmasi dalam " + batasMenit + " menit.");
                p.getJadwal().lepasKursi(p.getKodeBooking());
                jumlah++;
            }
        }
        return jumlah;
    }

    // Memproses pesanan kedaluwarsa berdasarkan waktu saat ini.
    public int perbaruiKedaluwarsa() {
        return perbaruiKedaluwarsa(LocalDateTime.now());
    }

    // Mengubah status pesanan dari MENUNGGU menjadi TERVERIFIKASI.
    public Pemesanan konfirmasi(String kode) throws DataTidakDitemukanException {
        perbaruiKedaluwarsa();
        Pemesanan p = cariKode(kode);
        periksaMenunggu(p, "dikonfirmasi");
        p.setStatus(StatusPesanan.TERVERIFIKASI);
        return p;
    }

    // Menolak pesanan yang masih menunggu, mencatat alasannya, dan melepas kursinya.
    public Pemesanan tolak(String kode, String alasan) throws DataTidakDitemukanException {
        perbaruiKedaluwarsa();
        Pemesanan p = cariKode(kode);
        periksaMenunggu(p, "ditolak");
        p.setStatus(StatusPesanan.DITOLAK);
        p.setAlasan(alasan);
        p.getJadwal().lepasKursi(p.getKodeBooking());
        return p;
    }

    // Memastikan pesanan masih berstatus MENUNGGU sebelum dikonfirmasi atau ditolak.
    private void periksaMenunggu(Pemesanan p, String aksi) {
        if (p.getStatus() != StatusPesanan.MENUNGGU) {
            throw new IllegalStateException("Pesanan " + p.getKodeBooking() + " berstatus "
                    + p.getStatus().getLabel() + ", jadi tidak bisa " + aksi + ".");
        }
    }

    // Menandai tiket sebagai DIGUNAKAN setelah memeriksa status dan kesesuaian tanggal tayang.
    public Pemesanan checkIn(String kode, LocalDate hari) throws DataTidakDitemukanException {
        perbaruiKedaluwarsa();
        Pemesanan p = cariKode(kode);
        switch (p.getStatus()) {
            case DIGUNAKAN:
                throw new IllegalStateException("Tiket " + p.getKodeBooking() + " SUDAH DIGUNAKAN sebelumnya.");
            case MENUNGGU:
                throw new IllegalStateException("Tiket " + p.getKodeBooking() + " belum dikonfirmasi pembayarannya.");
            case DITOLAK:
                throw new IllegalStateException("Tiket " + p.getKodeBooking() + " ditolak, tidak sah.");
            default:
                break;
        }
        if (!p.getJadwal().getTanggal().equals(hari)) {
            throw new IllegalStateException("Tiket ini untuk tayangan tanggal "
                    + p.getJadwal().getTanggal().format(util.TampilanCLI.FORMAT_TANGGAL) + ", bukan hari ini.");
        }
        p.setStatus(StatusPesanan.DIGUNAKAN);
        return p;
    }

    // Mencatat penggunaan tiket dengan tanggal hari ini sebagai tanggal pemeriksaan.
    public Pemesanan checkIn(String kode) throws DataTidakDitemukanException {
        return checkIn(kode, LocalDate.now());
    }


    // Mencari pesanan berdasarkan kode pemesanan tanpa membedakan huruf besar dan kecil.
    public Pemesanan cariKode(String kode) throws DataTidakDitemukanException {
        for (Pemesanan p : daftar) {
            if (p.getKodeBooking().equalsIgnoreCase(kode.trim())) {
                return p;
            }
        }
        throw new DataTidakDitemukanException("Pesanan", kode);
    }

    // Mengembalikan riwayat pesanan pengguna mulai dari pesanan terbaru.
    public List<Pemesanan> getRiwayat(User user) {
        return daftar.stream()
                .filter(p -> p.getUser().getUsername().equalsIgnoreCase(user.getUsername()))
                .sorted(Comparator.comparing(Pemesanan::getWaktuPesan).reversed())
                .collect(Collectors.toList());
    }

    // Mengembalikan pesanan yang menunggu konfirmasi mulai dari pesanan terlama.
    public List<Pemesanan> getMenunggu() {
        return daftar.stream()
                .filter(p -> p.getStatus() == StatusPesanan.MENUNGGU)
                .sorted(Comparator.comparing(Pemesanan::getWaktuPesan))
                .collect(Collectors.toList());
    }

    // Mengembalikan seluruh pesanan mulai dari pesanan terbaru.
    public List<Pemesanan> getAll() {
        return daftar.stream()
                .sorted(Comparator.comparing(Pemesanan::getWaktuPesan).reversed())
                .collect(Collectors.toList());
    }

    // Memeriksa apakah ada pesanan dengan ID jadwal yang diberikan.
    public boolean adaPesananUntukJadwal(String jadwalId) {
        return daftar.stream().anyMatch(p -> p.getJadwal().getId().equalsIgnoreCase(jadwalId));
    }


    // Menjumlahkan harga pesanan yang statusnya diperhitungkan sebagai penjualan.
    public double totalPendapatan() {
        return daftar.stream()
                .filter(p -> p.getStatus().dihitungSebagaiPenjualan())
                .mapToDouble(Pemesanan::getTotalHarga)
                .sum();
    }

    // Menghitung tiket terjual berdasarkan jumlah kursi pada pesanan yang dihitung sebagai penjualan.
    public int totalTiketTerjual() {
        return daftar.stream()
                .filter(p -> p.getStatus().dihitungSebagaiPenjualan())
                .mapToInt(Pemesanan::getJumlahKursi)
                .sum();
    }

    // Mengurutkan film dari penjualan tiket terbanyak, lalu menurut abjad jika jumlahnya sama.
    public Map<String, Integer> peringkatFilm() {
        Map<String, Integer> hitung = new HashMap<>();
        for (Pemesanan p : daftar) {
            if (p.getStatus().dihitungSebagaiPenjualan()) {
                hitung.merge(p.getJadwal().getFilm().getJudul(), p.getJumlahKursi(), Integer::sum);
            }
        }
        return hitung.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    // Mengelompokkan jumlah tiket terjual berdasarkan jam mulai tayang.
    public Map<Integer, Integer> tiketPerJam() {
        return daftar.stream()
                .filter(p -> p.getStatus().dihitungSebagaiPenjualan())
                .collect(Collectors.groupingBy(p -> p.getJadwal().getJam().getHour(), TreeMap::new,
                        Collectors.summingInt(Pemesanan::getJumlahKursi)));
    }

    // Menghitung pesanan untuk setiap status, termasuk status dengan nol pesanan.
    public Map<StatusPesanan, Long> jumlahPerStatus() {
        Map<StatusPesanan, Long> hasil = daftar.stream()
                .collect(Collectors.groupingBy(Pemesanan::getStatus,
                        () -> new EnumMap<>(StatusPesanan.class), Collectors.counting()));
        for (StatusPesanan s : StatusPesanan.values()) {
            hasil.putIfAbsent(s, 0L);
        }
        return hasil;
    }
}
