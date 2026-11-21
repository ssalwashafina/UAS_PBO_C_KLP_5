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

// Pusat logika pemesanan: membuat pesanan, menahan kursi, konfirmasi, penolakan, pesanan kedaluwarsa, check-in, riwayat, rekomendasi kursi, dan laporan.
public class PemesananService {

    // Jumlah kursi maksimal dalam satu pesanan.
    public static final int MAKS_KURSI = 6;

    private final List<Pemesanan> daftar = new ArrayList<>();
    private final KalkulatorHarga kalkulator;
    private int batasMenit = 30;

    // Membuat service.
    public PemesananService(KalkulatorHarga kalkulator) {
        this.kalkulator = kalkulator;
    }

    // Mengambil kalkulator harga.
    public KalkulatorHarga getKalkulator() {
        return kalkulator;
    }

    // Mengambil batas waktu konfirmasi.
    public int getBatasMenit() {
        return batasMenit;
    }

    // Mengatur batas waktu konfirmasi. Pengaturan ini hanya berlaku selama program jalan.
    public void setBatasMenit(int batasMenit) {
        if (batasMenit < 1 || batasMenit > 1440) {
            throw new IllegalArgumentException("Batas waktu harus 1 sampai 1440 menit.");
        }
        this.batasMenit = batasMenit;
    }

    // ------------------------------------------------------------ pembuatan

    // Menghitung rincian harga tanpa membuat pesanan, untuk ditampilkan sebelum user setuju.
    public RincianHarga hitungRincian(Jadwal jadwal, int jumlah, Voucher voucher, Pembayaran metode)
            throws PembayaranGagalException {
        return kalkulator.hitung(jadwal, jumlah, voucher, metode);
    }

    // Memeriksa satu kursi pilihan user dan mengembalikan namanya dalam huruf besar.
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

    // Mencari kursi terbaik untuk sejumlah orang.
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

        // Tidak ada deretan kosong yang cukup panjang: ambil kursi kosong terdekat dari titik ideal.
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

    // Membuat pesanan baru dengan status MENUNGGU dan langsung menahan kursinya.
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

    // Memasukkan pesanan dari file. Kursi ikut ditahan lagi kalau statusnya masih menahan kursi.
    public void muat(Pemesanan p) throws KursiTidakTersediaException {
        if (p.getStatus().menahanKursi()) {
            p.getJadwal().tahanKursi(p.getKursi(), p.getKodeBooking());
        }
        daftar.add(p);
    }

    // ----------------------------------------------------- proses admin

    // Menolak otomatis semua pesanan MENUNGGU yang sudah melewati batas konfirmasi, sekaligus melepas kursinya.
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
