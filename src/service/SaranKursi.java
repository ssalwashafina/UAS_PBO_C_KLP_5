package service;

import java.util.ArrayList;
import java.util.List;

// Hasil rekomendasi kursi: daftar kursi yang disarankan dan apakah letaknya berdampingan.
public class SaranKursi {

    private final List<String> kursi;
    private final boolean berdampingan;

    // Membuat saran kursi.
    public SaranKursi(List<String> kursi, boolean berdampingan) {
        this.kursi = new ArrayList<>(kursi);
        this.berdampingan = berdampingan;