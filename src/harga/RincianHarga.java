package harga;

import java.util.ArrayList;
import java.util.List;

// Hasil hitung harga satu pesanan: harga per kursi, diskon, biaya admin, dan total.
public class RincianHarga {

    private final double hargaPerKursi;
    private final int jumlahKursi;
    private final Voucher voucher;
    private final double diskon;
    private final double biayaAdmin;
    private final double totalBayar;
    private final List<String> aturanDipakai;

    // Membuat rincian harga.
    public RincianHarga(double hargaPerKursi, int jumlahKursi, Voucher voucher, double diskon,
                        double biayaAdmin, double totalBayar, List<String> aturanDipakai) {
        this.hargaPerKursi = hargaPerKursi;
        this.jumlahKursi = jumlahKursi;
        this.voucher = voucher;
        this.diskon = diskon;
        this.biayaAdmin = biayaAdmin;
        this.totalBayar = totalBayar;
        this.aturanDipakai = new ArrayList<>(aturanDipakai);
    }

    // Mengambil harga per kursi.
    public double getHargaPerKursi() {
        return hargaPerKursi;
    }

    // Mengambil jumlah kursi.
    public int getJumlahKursi() {
        return jumlahKursi;
    }

    // Menghitung subtotal sebelum diskon.
    public double getSubtotal() {
        return hargaPerKursi * jumlahKursi;
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
    public double getTotalBayar() {
        return totalBayar;
    }

    // Mengambil nama aturan harga yang berlaku.
    public List<String> getAturanDipakai() {
        return new ArrayList<>(aturanDipakai);
    }
}
