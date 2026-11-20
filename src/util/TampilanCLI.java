package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import model.Jadwal;
import model.Pemesanan;
import model.StatusPesanan;
import model.Studio;

// Kumpulan fungsi untuk menggambar tampilan di terminal: header, menu, tabel, peta kursi, bar persentase, dan struk.
public final class TampilanCLI {

    // Lebar standar kotak dan garis.
    public static final int LEBAR = 66;
    // Format tanggal tampilan.
    public static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    // Format tanggal dan jam tampilan.
    public static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static final String[] NAMA_HARI = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private TampilanCLI() {
    }

    // Mengulang sebuah karakter.
    public static String ulang(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // Mencetak kotak judul.
    public static void header(String judul) {
        String garis = "+" + ulang('=', LEBAR - 2) + "+";
        int sisa = Math.max(0, LEBAR - 2 - judul.length());
        int kiri = sisa / 2;
        System.out.println();
        System.out.println(garis);
        System.out.println("|" + ulang(' ', kiri) + judul + ulang(' ', sisa - kiri) + "|");
        System.out.println(garis);
    }

    // Mencetak menu bernomor. Pilihan terakhir selalu 0 untuk keluar atau kembali.
    public static void menu(String judul, String labelKeluar, String... opsi) {
        header(judul);
        for (int i = 0; i < opsi.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opsi[i]);
        }
        System.out.println("  0. " + labelKeluar);
        System.out.println(ulang('-', LEBAR));
    }

    // Mencetak tabel dengan lebar kolom menyesuaikan isi.
    public static void tabel(String[] kolom, List<String[]> baris) {
        int[] lebar = new int[kolom.length];
        for (int i = 0; i < kolom.length; i++) {
            lebar[i] = kolom[i].length();
        }
        for (String[] b : baris) {
            for (int i = 0; i < kolom.length; i++) {
                lebar[i] = Math.max(lebar[i], b[i].length());
            }
        }
        StringBuilder pemisah = new StringBuilder("+");
        for (int w : lebar) {
            pemisah.append(ulang('-', w + 2)).append('+');
        }
        System.out.println(pemisah);
        System.out.println(barisTabel(kolom, lebar));
        System.out.println(pemisah);
        for (String[] b : baris) {
            System.out.println(barisTabel(b, lebar));
        }
        System.out.println(pemisah);
    }

    private static String barisTabel(String[] sel, int[] lebar) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < lebar.length; i++) {
            sb.append(' ').append(String.format("%-" + lebar[i] + "s", sel[i])).append(" |");
        }
        return sb.toString();
    }

    // Mencetak peta kursi sebuah jadwal. Kursi kosong ditulis [ ], terisi [X], dan kursi yang sedang dipilih atau disarankan [*].
    public static void petaKursi(Jadwal jadwal, Collection<String> ditandai) {
        Studio studio = jadwal.getStudio();
        int kolom = studio.getJumlahKolom();
        int lebarPeta = 5 + kolom * 4;
        String layar = "[ LAYAR ]";
        System.out.println();
        System.out.println(ulang(' ', Math.max(0, (lebarPeta - layar.length()) / 2)) + layar);
        StringBuilder nomor = new StringBuilder("     ");
        for (int k = 1; k <= kolom; k++) {
            nomor.append(k < 10 ? " " + k + "  " : k + "  ");
        }
        System.out.println(nomor);
        for (int b = 0; b < studio.getJumlahBaris(); b++) {
            StringBuilder baris = new StringBuilder("  " + (char) ('A' + b) + "  ");
            for (int k = 0; k < kolom; k++) {
                String nama = Studio.namaKursi(b, k);
                if (ditandai.contains(nama)) {
                    baris.append("[*] ");
                } else if (jadwal.isKursiTerisi(nama)) {
                    baris.append("[X] ");
                } else {
                    baris.append("[ ] ");
                }
            }
            System.out.println(baris);
        }
        System.out.println();
        System.out.println("  [ ] kosong   [X] terisi   [*] pilihanmu");
        System.out.println("  Kosong: " + jadwal.getJumlahKosong() + " dari " + studio.getKapasitas() + " kursi");
    }

    // Membuat bar persentase dari karakter ASCII.
    public static String bar(int persen, int lebar) {
        int isi = Math.round(lebar * persen / 100f);
        return "[" + ulang('#', isi) + ulang('.', lebar - isi) + "] " + String.format("%3d%%", persen);
    }

    // Memformat angka menjadi rupiah dengan titik pemisah ribuan.
    public static String rupiah(double nilai) {
        long bulat = Math.round(nilai);
        String digit = Long.toString(Math.abs(bulat));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digit.length(); i++) {
            if (i > 0 && (digit.length() - i) % 3 == 0) {
                sb.append('.');
            }
            sb.append(digit.charAt(i));
        }
        return (bulat < 0 ? "-Rp" : "Rp") + sb;
    }

    // Memotong teks yang terlalu panjang supaya kolom laporan tetap rapi.
    public static String potong(String teks, int maks) {
        return teks.length() <= maks ? teks : teks.substring(0, maks - 2) + "..";
    }

    // Mengambil singkatan nama hari berbahasa Indonesia.
    public static String namaHari(LocalDate tanggal) {
        return NAMA_HARI[tanggal.getDayOfWeek().getValue() - 1];
    }

    // Mencetak satu baris "label : isi" dengan label rata kiri.
    public static void baris(String label, String isi) {
        System.out.println("  " + String.format("%-14s", label) + ": " + isi);
    }

    // Mencetak struk. Isi catatan di bagian bawah menyesuaikan status pesanan:
    public static void struk(Pemesanan p, int batasMenit) {
        StatusPesanan st = p.getStatus();
        String judul = st.dihitungSebagaiPenjualan() ? "TIKET BIOSKOP" : "INVOICE PEMESANAN";
        header(judul);
        baris("Kode Booking", p.getKodeBooking());
        baris("Status", st.getLabel());
        baris("Pemesan", p.getUser().getNama());
        System.out.println(ulang('-', LEBAR));
        Jadwal j = p.getJadwal();
        baris("Film", j.getFilm().getJudul() + " (" + j.getFilm().getLabelRating() + ")");
        baris("Studio", j.getStudio().getNama() + " [" + j.getStudio().getTipe().getLabel() + "]");
        baris("Tayang", namaHari(j.getTanggal()) + ", " + j.getTanggal().format(FORMAT_TANGGAL) + " pukul " + j.getJam());
        baris("Kursi", String.join(", ", p.getKursi()));
        System.out.println(ulang('-', LEBAR));
        baris("Harga kursi", rupiah(p.getHargaPerKursi()) + " x " + p.getJumlahKursi() + " = " + rupiah(p.getSubtotal()));
        if (p.getVoucher() != null) {
            baris("Diskon", "-" + rupiah(p.getDiskon()) + " (" + p.getVoucher().name() + ")");
        }
        if (p.getBiayaAdmin() > 0) {
            baris("Biaya admin", rupiah(p.getBiayaAdmin()));
        }
        baris("TOTAL", rupiah(p.getTotalHarga()));
        baris("Metode", p.getMetode().getNamaMetode());
        baris("Dipesan", p.getWaktuPesan().format(FORMAT_WAKTU));
        System.out.println(ulang('-', LEBAR));
        switch (st) {
            case MENUNGGU:
                System.out.println("  CARA BAYAR:");
                for (String baris : p.getMetode().buatInstruksi(p.getKodeBooking(), p.getTotalHarga()).split("\n")) {
                    System.out.println("  " + baris);
                }
                System.out.println("  Batas konfirmasi: " + p.getBatasKonfirmasi(batasMenit).format(FORMAT_WAKTU));
                System.out.println("  Kursi dilepas otomatis kalau pembayaran belum terkonfirmasi.");
                System.out.println("  Pembayaran tidak dapat dikembalikan.");
                break;
            case TERVERIFIKASI:
                System.out.println("  Tunjukkan kode booking ini di pintu masuk studio.");
                break;
            case DIGUNAKAN:
                System.out.println("  Tiket ini sudah dipakai masuk.");
                break;
            case DITOLAK:
                System.out.println("  Alasan: " + (p.getAlasan() == null ? "-" : p.getAlasan()));
                System.out.println("  Kursi sudah dilepas kembali.");
                break;
            default:
                break;
        }
        System.out.println(ulang('=', LEBAR));
    }
}
