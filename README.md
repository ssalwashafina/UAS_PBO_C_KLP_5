# Tiket Bioskop Kita

Program Java berbasis CLI untuk memesan tiket bioskop. Dibuat untuk UAS Praktikum Pemrograman Berorientasi Objek (kelas C). Ada dua jenis akun. Admin mengelola film, studio, dan jadwal, lalu memproses pesanan. User memilih jadwal, memilih kursi di peta, membayar, dan melihat tiketnya.

## Cara compile dan menjalankan

Butuh JDK 8 atau lebih baru (cek dengan `javac -version`). Semua perintah dijalankan dari folder `src`:

```
cd src
javac utama/Main.java
java utama.Main
```

`javac` otomatis ikut mengompilasi semua file yang dipakai oleh `Main`, jadi cukup satu perintah. File `.class` muncul di samping file `.java`-nya. Kalau ada bagian kode yang diubah, jalankan `javac utama/Main.java` lagi.

Saat pertama dijalankan, program membuat folder `data/` (di dalam folder tempat kamu menjalankannya) berisi data contoh. Setiap perubahan tersimpan otomatis ke folder itu. Hapus folder `data/` kalau ingin kembali ke data awal.

### Akun contoh

| Username | Password | Peran | Catatan |
|---|---|---|---|
| admin | admin123 | Admin | |
| budi | budi123 | User | dewasa, sudah punya tiga pesanan contoh |
| sinta | sinta123 | User | berumur 14 tahun, ditolak saat memesan film 17+ |

User baru bisa dibuat lewat menu Register. Password disimpan sebagai hash SHA-256, bukan teks asli.

## Alur program

Menu awal berisi Login, Register, dan Keluar. Setelah login, program melihat tipe akun lalu membuka menu Admin atau menu User.

**User** memilih jadwal, memasukkan jumlah kursi, lalu memilih kursi di peta (satu per satu, atau pakai rekomendasi otomatis). Setelah itu ia mengisi voucher kalau ada, memilih metode bayar (tunai, atau non tunai lewat e-wallet atau transfer), dan melihat rincian harga. Begitu ia setuju, pesanan tercatat dengan status MENUNGGU dan kursi langsung ditahan. Invoice menampilkan cara membayar.

**Admin** memeriksa pesanan yang berstatus MENUNGGU. Kalau uang sudah diterima (transfer masuk atau tunai sudah dibayar di loket), pesanan dikonfirmasi dan menjadi TERVERIFIKASI. Kalau belum, pesanan ditolak (DITOLAK) dan kursi dilepas kembali. Di hari tayang, admin memasukkan kode booking di pintu masuk dan statusnya berubah menjadi DIGUNAKAN.

### Aturan program

- Pembayaran tidak bisa dikembalikan, jadi user tidak punya fitur pembatalan.
- Yang dikonfirmasi admin adalah pembayarannya. Admin hanya menolak pesanan kalau uang belum diterima, jadi tidak ada uang yang perlu dikembalikan.
- Kursi ditahan sejak status MENUNGGU dan baru dilepas kalau pesanan DITOLAK.
- Pesanan MENUNGGU yang tidak dikonfirmasi dalam 30 menit ditolak otomatis dan kursinya dilepas. Pengecekan dilakukan setiap kali menu ditampilkan. Admin bisa mengubah batas ini lewat menu 9 (berlaku selama program berjalan).
- Satu pesanan maksimal 6 kursi.
- Umur user dicek terhadap rating usia film.
- Pendapatan dan film terlaris hanya menghitung pesanan TERVERIFIKASI dan DIGUNAKAN.
- Film dan studio yang masih punya jadwal tidak bisa dihapus. Jadwal yang sudah pernah dipesan tidak bisa diubah atau dihapus. Dua jadwal di studio yang sama harus berjarak minimal 15 menit.

## Fitur

Dari rancangan awal: penyimpanan ke file CSV, peta kursi (`[ ]` kosong, `[X]` terisi, `[*]` pilihanmu), pencarian film berdasarkan judul atau genre, pengurutan berdasarkan judul atau durasi, invoice dan tiket dengan kode booking (misalnya `BK-20261118-001`), laporan admin, dan register.

Ditambahkan saat penyempurnaan:

- **Pesanan kedaluwarsa otomatis**, supaya kursi tidak terkunci selamanya.
- **Rekomendasi kursi terbaik.** Program mencari kursi kosong yang berdampingan dan paling dekat ke tengah ruangan. Kalau tidak ada yang cukup panjang, program menyarankan kursi kosong terdekat dan memberi tahu bahwa kursinya terpisah.
- **Harga dinamis.** Studio Premium x1,5, akhir pekan +20%, matinee hari kerja (sebelum 15.00) -15%. Ada juga voucher `HEMAT10`, `PELAJAR15`, `UAS20`, dan biaya admin sesuai metode bayar.
- **Check-in tiket** di hari tayang, dengan status baru DIGUNAKAN. Tiket yang sama tidak bisa dipakai dua kali.
- **Dashboard admin**: pendapatan, tiket terjual, jumlah pesanan per status, peringkat film, jam tersibuk, dan okupansi jadwal.
- Validasi jadwal bentrok dan rating usia.

## Struktur kode

```
src/
  model/       Akun (abstract), Admin, User, Film, Studio, TipeStudio, Jadwal,
               Pemesanan, StatusPesanan
  payment/     Pembayaran (interface), PembayaranTunai, PembayaranNonTunai (abstract),
               PembayaranEWallet, PembayaranTransfer, PembayaranFactory
  harga/       HargaStrategy (interface), HargaStudioStrategy, HargaAkhirPekanStrategy,
               HargaMatineeStrategy, Voucher, RincianHarga, KalkulatorHarga
  service/     CrudService<T, ID> (interface generik), AuthService, FilmService,
               JadwalService, PemesananService, SaranKursi
  exception/   BioskopException (induk) dan 7 exception turunannya
  util/        FileHandler, TampilanCLI, TabelData, InputHelper, Keamanan,
               InputBerakhirException
  utama/       Main, Menu (abstract), MenuAdmin, MenuUser, DataAwal
```

Rancangan awal memperkirakan sekitar 21 file. Hasil akhirnya 47 file, karena exception dibuat lebih rinci, ada package `harga` untuk aturan harga, dan menu dipisah dari `Main` supaya `Main` tetap pendek.

Urutan belajar yang disarankan: `model`, lalu `exception`, lalu `payment`, lalu `service`. Package `harga` dan `util` bisa dibaca belakangan.

## Pemetaan ke requirement OOP

| Konsep | Penerapan |
|---|---|
| Encapsulation | Atribut semua kelas model dan service `private`, diakses lewat getter dan setter. Setter memvalidasi nilai (misalnya durasi film harus lebih dari 0). |
| Inheritance | `Admin` dan `User` extends `Akun`. `PembayaranEWallet` dan `PembayaranTransfer` extends `PembayaranNonTunai`. Semua exception extends `BioskopException`. |
| Polymorphism | `tampilkanMenu()` dioverride di `Admin` dan `User`, lalu dipanggil lewat variabel bertipe `Akun` di kelas `Menu`. Metode bayar selalu ditampung sebagai `Pembayaran`, dan masing-masing menghitung biaya admin serta petunjuk bayarnya sendiri. `toString()` dioverride dengan `@Override`. |
| Abstraction / Interface | Abstract class `Akun`, `PembayaranNonTunai`, `Menu`. Interface `Pembayaran`, `HargaStrategy`, dan `CrudService<T, ID>` (dipakai `FilmService` dan `JadwalService`). |
| Collection | `ArrayList` untuk film, studio, jadwal, pesanan. `HashMap` untuk akun dan kursi terisi di jadwal. |
| Exception handling | `try-catch` pada login, input menu, pemilihan kursi, dan pembacaan file. Exception buatan sendiri dilempar dengan `throw` dan dideklarasikan dengan `throws`. Input salah ketik (huruf padahal diminta angka) ditangani di `InputHelper`. |
| Composition | `Jadwal` memiliki `Film` dan `Studio`. `Pemesanan` memiliki `User` dan `Jadwal`. |
| Javadoc | Komentar `/** ... */` ada di setiap class dan method. |
| Lainnya | `Comparable` pada `Film` dan `Comparator` untuk pengurutan. Enum dengan atribut (`TipeStudio`, `StatusPesanan`, `Voucher`). Stream dan lambda di `PemesananService` (untuk laporan). |

## Penyimpanan data

Data disimpan sebagai lima file CSV di folder `data/`: `akun.csv`, `film.csv`, `studio.csv`, `jadwal.csv`, `pemesanan.csv`. Objek yang saling terhubung disimpan sebagai ID, lalu dirangkai lagi saat dibaca. Kursi terisi dihitung ulang dari pesanan yang masih menahan kursi. Baris yang rusak dilewati dan dilaporkan sebagai peringatan, jadi satu baris rusak tidak menggagalkan seluruh data.

## Skenario demo

1. Login sebagai `sinta`, coba pesan film 17+, lihat penolakan karena umur.
2. Login sebagai `budi`, pesan 3 kursi dengan rekomendasi otomatis, pakai voucher `UAS20`, bayar lewat transfer.
3. Login sebagai `admin`, buka Konfirmasi / Tolak Pesanan, lalu konfirmasi pesanan Budi.
4. Kembali sebagai `budi`, lihat tiket di Riwayat Pesanan.
5. Sebagai `admin`, coba Check-in dua kali dengan kode yang sama, lalu buka Laporan & Dashboard.
6. Untuk menunjukkan pesanan kedaluwarsa: ubah batas menjadi 1 menit lewat menu 9, buat pesanan sebagai user, tunggu semenit, lalu buka menu apa saja. Pesanan berstatus DITOLAK dan kursinya bebas lagi.

## Catatan

- Check-in hanya menerima tiket untuk tayangan hari ini. Jadwal contoh hari ini ada di pukul 22.30. Kalau program dijalankan setelah jam itu, tambahkan dulu jadwal hari ini lewat menu admin.
- Data contoh dibuat dari tanggal hari ini, jadi jadwalnya selalu tiga hari ke depan sejak program pertama kali dijalankan.
- Nomor rekening dan e-wallet di invoice hanya contoh. Tidak ada uang yang diproses program.
- Password terlihat saat diketik.
- Pengaturan batas waktu konfirmasi tidak disimpan ke file. Setelah program ditutup, batasnya kembali 30 menit.
- Program dirancang untuk satu pengguna pada satu waktu.
