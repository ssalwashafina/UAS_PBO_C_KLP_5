
package utama;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exception.BioskopException;
import exception.DataTidakDitemukanException;
import exception.KursiTidakTersediaException;
import exception.UsiaTidakMemenuhiException;
import harga.RincianHarga;
import harga.Voucher;
import model.Film;
import model.Jadwal;
import model.Pemesanan;
import model.User;
import payment.Pembayaran;
import payment.PembayaranFactory;
import service.FilmService;
import service.JadwalService;
import service.PemesananService;
import service.SaranKursi;
import util.FileHandler;
import util.InputHelper;
import util.TabelData;
import util.TampilanCLI;

// Menu untuk pelanggan: melihat film dan jadwal, memesan tiket, dan melihat riwayat.
public class MenuUser extends Menu {

    private final User user;
    private final FilmService filmService;
    private final JadwalService jadwalService;

    // Membuat menu user.
    public MenuUser(User user, FileHandler fileHandler, FilmService filmService, JadwalService jadwalService,
                    PemesananService pemesananService) {
        super(user, fileHandler, pemesananService);
        this.user = user;
        this.filmService = filmService;
        this.jadwalService = jadwalService;
    }

    @Override
    protected int jumlahMenu() {
        return 5;
    }

    @Override
    protected void tangani(int pilihan) throws BioskopException {
        switch (pilihan) {
            case 1:
                lihatFilmDanJadwal();
                break;
            case 2:
                cariUrutFilm();
                break;
            case 3:
                pesanTiket();
                break;
            case 4:
                lihatRiwayat();
                break;
            case 5:
                lihatStruk();
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------ lihat

    private void lihatFilmDanJadwal() throws BioskopException {
        TampilanCLI.header("FILM YANG TAYANG");
        TabelData.film(filmService.getAll());
        String id = InputHelper.baca("\nID film untuk melihat jadwalnya (Enter = semua jadwal): ");
        if (!id.isEmpty()) {
            filmService.cariById(id);
        }
        List<Jadwal> jadwal = new ArrayList<>();
        for (Jadwal j : jadwalService.getJadwalAktif(LocalDateTime.now())) {
            if (id.isEmpty() || j.getFilm().getId().equalsIgnoreCase(id)) {
                jadwal.add(j);
            }
        }
        TampilanCLI.header("JADWAL TAYANG");
        TabelData.jadwal(jadwal, pemesananService.getKalkulator());
        System.out.println("  Harga sudah termasuk aturan: Premium x1,5 | akhir pekan +20% | matinee hari kerja -15%");
        InputHelper.enter();
    }

    private void cariUrutFilm() {
        TampilanCLI.menu("CARI / URUTKAN FILM", "Kembali",
                "Cari berdasarkan judul atau genre", "Urutkan berdasarkan judul (A-Z)", "Urutkan berdasarkan durasi");
        int p = InputHelper.angka("Pilih: ", 0, 3);
        List<Film> hasil;
        switch (p) {
            case 1:
                String kata = InputHelper.teks("Kata kunci: ");
                hasil = filmService.cari(kata);
                break;
            case 2:
                hasil = filmService.urutkanJudul();
                break;
            case 3:
                hasil = filmService.urutkanDurasi();
                break;
            default:
                return;
        }
        TabelData.film(hasil);
        InputHelper.enter();
    }

    // ----------------------------------------------------------- pesan

    private void pesanTiket() throws BioskopException {
        List<Jadwal> aktif = jadwalService.getJadwalAktif(LocalDateTime.now());
        if (aktif.isEmpty()) {
            System.out.println("\n  Belum ada jadwal yang bisa dipesan.");
            InputHelper.enter();
            return;
        }
        TampilanCLI.header("PESAN TIKET - PILIH JADWAL");
        TabelData.jadwal(aktif, pemesananService.getKalkulator());
        String id = InputHelper.baca("\nID jadwal (Enter = batal): ");
        if (id.isEmpty()) {
            return;
        }
        Jadwal jadwal = jadwalService.cariById(id);
        if (jadwal.sudahMulai(LocalDateTime.now())) {
            throw new BioskopException("Jadwal ini sudah dimulai.");
        }
        int umur = user.getUmur();
        if (umur < jadwal.getFilm().getRatingUsia()) {
            throw new UsiaTidakMemenuhiException(jadwal.getFilm().getJudul(), jadwal.getFilm().getRatingUsia(), umur);
        }
        if (jadwal.getJumlahKosong() == 0) {
            throw new KursiTidakTersediaException("Semua kursi di jadwal ini sudah terisi.");
        }

        TampilanCLI.header(jadwal.getFilm().getJudul() + " - " + jadwal.getStudio().getNama());
        TampilanCLI.petaKursi(jadwal, Collections.<String>emptyList());
        List<String> aturan = pemesananService.getKalkulator().aturanBerlaku(jadwal);
        System.out.println("  Harga per kursi: "
                + TampilanCLI.rupiah(pemesananService.getKalkulator().hargaPerKursi(jadwal))
                + (aturan.isEmpty() ? "" : "  (" + String.join(", ", aturan) + ")"));

        int maks = Math.min(PemesananService.MAKS_KURSI, jadwal.getJumlahKosong());
        int jumlah = InputHelper.angka("\nJumlah kursi (1-" + maks + "): ", 1, maks);
        List<String> kursi = pilihKursi(jadwal, jumlah);
        Voucher voucher = tanyaVoucher();
        Pembayaran metode = pilihMetode(jadwal, jumlah, voucher);

        RincianHarga r = pemesananService.hitungRincian(jadwal, jumlah, voucher, metode);
        TampilanCLI.header("RINCIAN PESANAN");
        TampilanCLI.baris("Film", jadwal.getFilm().getJudul());
        TampilanCLI.baris("Tayang", jadwal.getTanggal().format(TampilanCLI.FORMAT_TANGGAL) + " " + jadwal.getJam());
        TampilanCLI.baris("Kursi", String.join(", ", kursi));
        TampilanCLI.baris("Harga kursi", TampilanCLI.rupiah(r.getHargaPerKursi()) + " x " + jumlah + " = "
                + TampilanCLI.rupiah(r.getSubtotal()));
        if (voucher != null) {
            TampilanCLI.baris("Diskon", "-" + TampilanCLI.rupiah(r.getDiskon()) + " (" + voucher.name() + ")");
        }
        if (r.getBiayaAdmin() > 0) {
            TampilanCLI.baris("Biaya admin", TampilanCLI.rupiah(r.getBiayaAdmin()));
        }
        TampilanCLI.baris("TOTAL", TampilanCLI.rupiah(r.getTotalBayar()));
        TampilanCLI.baris("Metode", metode.getNamaMetode());
        System.out.println("\n  PERHATIAN: pembayaran tidak dapat dikembalikan dan pesanan tidak bisa dibatalkan.");
        if (!InputHelper.ya("  Lanjutkan pesanan?")) {
            System.out.println("\n  Pesanan tidak jadi dibuat. Kursi tidak ditahan.");
            InputHelper.enter();
            return;
        }

        Pemesanan p = pemesananService.buatPesanan(user, jadwal, kursi, metode, voucher);
        TampilanCLI.struk(p, pemesananService.getBatasMenit());
        InputHelper.enter();
    }

    private List<String> pilihKursi(Jadwal jadwal, int jumlah) {
        System.out.println("\n  Cara memilih kursi:");
        System.out.println("  1. Pilih sendiri, satu per satu");
        System.out.println("  2. Rekomendasi kursi terbaik otomatis");
        int cara = InputHelper.angka("Pilih: ", 1, 2);
        if (cara == 2) {
            SaranKursi saran = pemesananService.rekomendasiKursi(jadwal, jumlah);
            if (saran != null) {
                TampilanCLI.petaKursi(jadwal, saran.getKursi());
                if (saran.isBerdampingan()) {
                    System.out.println("  Saran: " + String.join(", ", saran.getKursi())
                            + " (berdampingan, paling dekat ke tengah).");
                } else {
                    System.out.println("  Tidak ada " + jumlah + " kursi berdampingan yang kosong.");
                    System.out.println("  Saran terdekat dari posisi ideal: " + String.join(", ", saran.getKursi()));
                }
                if (InputHelper.ya("  Pakai kursi ini?")) {
                    return saran.getKursi();
                }
            }
        }
        List<String> terpilih = new ArrayList<>();
        while (terpilih.size() < jumlah) {
            TampilanCLI.petaKursi(jadwal, terpilih);
            String masukan = InputHelper.teks("Kursi ke-" + (terpilih.size() + 1) + " dari " + jumlah
                    + " (contoh A5): ");
            try {
                terpilih.add(pemesananService.validasiKursi(jadwal, masukan, terpilih));
            } catch (KursiTidakTersediaException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
        return terpilih;
    }

    private Voucher tanyaVoucher() {
        while (true) {
            String kode = InputHelper.baca("\nKode voucher (Enter kalau tidak ada, contoh HEMAT10): ");
            if (kode.isEmpty()) {
                return null;
            }
            java.util.Optional<Voucher> v = Voucher.cari(kode);
            if (v.isPresent()) {
                System.out.println("  Voucher " + v.get().name() + " dipakai, diskon " + v.get().getPersen() + "%.");
                return v.get();
            }
            System.out.println("  ! Voucher tidak dikenal.");
        }
    }

    private Pembayaran pilihMetode(Jadwal jadwal, int jumlah, Voucher voucher) throws BioskopException {
        TampilanCLI.menu("METODE PEMBAYARAN", "Batal", "Tunai (bayar di loket)", "Non tunai (e-wallet / transfer)");
        int jenis = InputHelper.angka("Pilih: ", 0, 2);
        if (jenis == 0) {
            throw new BioskopException("Pemesanan dibatalkan.");
        }
        if (jenis == 1) {
            return PembayaranFactory.dariKode("TUNAI");
        }
        List<Pembayaran> pilihan = PembayaranFactory.daftarNonTunai();
        System.out.println();
        for (int i = 0; i < pilihan.size(); i++) {
            Pembayaran m = pilihan.get(i);
            RincianHarga r = pemesananService.hitungRincian(jadwal, jumlah, voucher, m);
            System.out.println("  " + (i + 1) + ". " + m.getNamaMetode() + " - total "
                    + TampilanCLI.rupiah(r.getTotalBayar()) + " (biaya admin " + TampilanCLI.rupiah(r.getBiayaAdmin())
                    + ")");
        }
        return pilihan.get(InputHelper.angka("Pilih: ", 1, pilihan.size()) - 1);
    }

    // ---------------------------------------------------------- riwayat

    private void lihatRiwayat() throws BioskopException {
        List<Pemesanan> riwayat = pemesananService.getRiwayat(user);
        TampilanCLI.header("RIWAYAT PESANAN");
        TabelData.pesanan(riwayat);
        if (riwayat.isEmpty()) {
            InputHelper.enter();
            return;
        }
        String kode = InputHelper.baca("\nKode booking untuk lihat tiket / struk (Enter = kembali): ");
        if (!kode.isEmpty()) {
            tampilkanStruk(kode);
            InputHelper.enter();
        }
    }

    private void lihatStruk() throws BioskopException {
        if (pemesananService.getRiwayat(user).isEmpty()) {
            System.out.println("\n  Kamu belum punya pesanan.");
            InputHelper.enter();
            return;
        }
        tampilkanStruk(InputHelper.teks("\nKode booking: "));
        InputHelper.enter();
    }

    private void tampilkanStruk(String kode) throws DataTidakDitemukanException {
        Pemesanan p = pemesananService.cariKode(kode);
        if (!p.getUser().getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new DataTidakDitemukanException("Pesanan", kode);
        }
        TampilanCLI.struk(p, pemesananService.getBatasMenit());
    }
}
