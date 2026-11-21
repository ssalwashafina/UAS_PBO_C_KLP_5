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
