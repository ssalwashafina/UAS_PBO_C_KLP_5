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
