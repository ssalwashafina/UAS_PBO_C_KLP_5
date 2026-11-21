package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import harga.Voucher;
import payment.Pembayaran;

// Satu pesanan tiket. Pemesanan "memiliki" User dan Jadwal (composition).
public class Pemesanan {

    private final String kodeBooking;
    private final User user;
    private final Jadwal jadwal;
    private final List<String> kursi;
    private final Pembayaran metode;
    private final double hargaPerKursi;
    private final Voucher voucher;
    private final double diskon;
    private final double biayaAdmin;
    private final double totalHarga;
    private final LocalDateTime waktuPesan;
    private StatusPesanan status;
    private String alasan;

    // Membuat pesanan baru.
    public Pemesanan(String kodeBooking, User user, Jadwal jadwal, List<String> kursi, Pembayaran metode, double hargaPerKursi, Voucher voucher, double diskon, double biayaAdmin, double totalHarga, LocalDateTime waktuPesan, StatusPesanan status, String alasan) {
        this.kodeBooking = kodeBooking;
        this.user = user;
        this.jadwal = jadwal;
        this.kursi = new ArrayList<>(kursi);
        this.metode = metode;
        this.hargaPerKursi = hargaPerKursi;
        this.voucher = voucher;
        this.diskon = diskon;
        this.biayaAdmin = biayaAdmin;
        this.totalHarga = totalHarga;
        this.waktuPesan = waktuPesan;
        this.status = status;
        this.alasan = alasan;
    }

    // Mengambil kode booking.
    public String getKodeBooking() {
        return kodeBooking;
    }

    // Mengambil pemesan.
    public User getUser() {
        return user;
    }

    // Mengambil jadwal.
    public Jadwal getJadwal() {
        return jadwal;
    }

    // Mengambil kursi pesanan.
    public List<String> getKursi() {
        return new ArrayList<>(kursi);
    }

    // Menghitung jumlah kursi.
    public int getJumlahKursi() {
        return kursi.size();
    }

    // Mengambil metode pembayaran.
    public Pembayaran getMetode() {
        return metode;
    }

    // Mengambil harga per kursi.
    public double getHargaPerKursi() {
        return hargaPerKursi;
    }

    // Menghitung subtotal sebelum diskon dan biaya admin.
    public double getSubtotal() {
        return hargaPerKursi * kursi.size();
    }

    // Mengambil voucher.
    public Voucher getVoucher() {
        return voucher;
    }

    // Mengambil nominal diskon.
    public double getDiskon() {
        return diskon;
    }

    // Mengambil biaya admin.
    public double getBiayaAdmin() {
        return biayaAdmin;
    }

    // Mengambil total bayar.
    public double getTotalHarga() {
        return totalHarga;
    }

    // Mengambil waktu pesan.
    public LocalDateTime getWaktuPesan() {
        return waktuPesan;
    }

    // Mengambil status.
    public StatusPesanan getStatus() {
        return status;
    }

    // Mengubah status.
    public void setStatus(StatusPesanan status) {
        this.status = status;
    }

    // Mengambil alasan penolakan.
    public String getAlasan() {
        return alasan;
    }

    // Mengisi alasan penolakan.
    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    // Menghitung batas akhir konfirmasi pembayaran.
    public LocalDateTime getBatasKonfirmasi(int batasMenit) {
        return waktuPesan.plusMinutes(batasMenit);
    }

    // Mengecek apakah pesanan menunggu sudah melewati batas konfirmasi.
    public boolean isKedaluwarsa(LocalDateTime sekarang, int batasMenit) {
        return status == StatusPesanan.MENUNGGU && sekarang.isAfter(getBatasKonfirmasi(batasMenit));
    }

    @Override
    public String toString() {
        return kodeBooking + " | " + jadwal.getFilm().getJudul() + " | " + String.join(",", kursi) + " | " + status.getLabel();
    }
}
