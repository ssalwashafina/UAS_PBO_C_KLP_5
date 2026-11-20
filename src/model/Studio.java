package model;

// Studio bioskop beserta ukuran denah kursinya.
public class Studio {

    // Jumlah baris maksimal, sesuai jumlah huruf alfabet.
    public static final int MAKS_BARIS = 26;
    // Jumlah kolom maksimal per baris.
    public static final int MAKS_KOLOM = 20;

    private String id;
    private String nama;
    private TipeStudio tipe;
    private int jumlahBaris;
    private int jumlahKolom;

    // Membuat studio.
    public Studio(String id, String nama, TipeStudio tipe, int jumlahBaris, int jumlahKolom) {
        this.id = id;
        setNama(nama);
        this.tipe = tipe;
        setUkuran(jumlahBaris, jumlahKolom);
    }

    // Mengambil kode studio.
    public String getId() {
        return id;
    }

    // Mengambil nama studio.
    public String getNama() {
        return nama;
    }

    // Mengubah nama studio.
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama studio tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    // Mengambil tipe studio.
    public TipeStudio getTipe() {
        return tipe;
    }

    // Mengubah tipe studio.
    public void setTipe(TipeStudio tipe) {
        this.tipe = tipe;
    }

    // Mengambil jumlah baris.
    public int getJumlahBaris() {
        return jumlahBaris;
    }

    // Mengambil jumlah kolom.
    public int getJumlahKolom() {
        return jumlahKolom;
    }

    // Mengubah ukuran denah kursi.
    public void setUkuran(int jumlahBaris, int jumlahKolom) {
        if (jumlahBaris < 1 || jumlahBaris > MAKS_BARIS) {
            throw new IllegalArgumentException("Jumlah baris harus 1 sampai " + MAKS_BARIS + ".");
        }
        if (jumlahKolom < 1 || jumlahKolom > MAKS_KOLOM) {
            throw new IllegalArgumentException("Jumlah kolom harus 1 sampai " + MAKS_KOLOM + ".");
        }
        this.jumlahBaris = jumlahBaris;
        this.jumlahKolom = jumlahKolom;
    }

    // Menghitung jumlah seluruh kursi.
    public int getKapasitas() {
        return jumlahBaris * jumlahKolom;
    }

    // Membuat nama kursi dari posisi indeks.
    public static String namaKursi(int indeksBaris, int indeksKolom) {
        return (char) ('A' + indeksBaris) + String.valueOf(indeksKolom + 1);
    }

    // Mengubah nama kursi menjadi posisi indeks sambil memeriksa apakah kursi itu ada di studio ini.
    public int[] parseKursi(String kode) {
        if (kode == null || kode.length() < 2) {
            throw new IllegalArgumentException("Format kursi salah. Contoh yang benar: A5");
        }
        char huruf = Character.toUpperCase(kode.charAt(0));
        int baris = huruf - 'A';
        int kolom;
        try {
            kolom = Integer.parseInt(kode.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format kursi salah. Contoh yang benar: A5");
        }
        if (baris < 0 || baris >= jumlahBaris || kolom < 0 || kolom >= jumlahKolom) {
            throw new IllegalArgumentException("Kursi " + kode.toUpperCase() + " tidak ada di " + nama + ".");
        }
        return new int[] {baris, kolom};
    }

    @Override
    public String toString() {
        return id + " - " + nama + " (" + tipe.getLabel() + ", " + jumlahBaris + "x" + jumlahKolom
                + " = " + getKapasitas() + " kursi)";
    }
}
