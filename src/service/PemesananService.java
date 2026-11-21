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
