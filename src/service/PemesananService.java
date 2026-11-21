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
