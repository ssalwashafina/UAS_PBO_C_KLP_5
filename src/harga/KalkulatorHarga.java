package harga;

import java.util.ArrayList;
import java.util.List;

import exception.PembayaranGagalException;
import model.Jadwal;
import payment.Pembayaran;

// Menghitung harga tiket dari harga dasar jadwal, aturan harga (Strategy), voucher, dan biaya admin metode pembayaran.
public class KalkulatorHarga {

    private final List<HargaStrategy> aturan = new ArrayList<>();

    // Membuat kalkulator dengan tiga aturan bawaan: studio, akhir pekan, dan matinee.
    public KalkulatorHarga() {
        aturan.add(new HargaStudioStrategy());
        aturan.add(new HargaAkhirPekanStrategy());
        aturan.add(new HargaMatineeStrategy());
    }

    // Menambah aturan harga baru.
    public void tambahAturan(HargaStrategy strategi) {
        aturan.add(strategi);
    }
