
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
