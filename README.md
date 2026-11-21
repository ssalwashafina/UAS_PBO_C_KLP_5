# 🎓 UAS Praktikum Pemrograman Berorientasi Objek Kelas C

## 🎬 TIKET BIOSKOP KITA

**Kelompok 5 | Praktikum Pemrograman Berorientasi Objek | Kelas C**

**Tiket Bioskop Kita** merupakan aplikasi pemesanan tiket bioskop berbasis **Command Line Interface (CLI)** yang dikembangkan menggunakan bahasa pemrograman **Java** sebagai proyek Ujian Akhir Semester (UAS) Praktikum Pemrograman Berorientasi Objek.

Aplikasi ini menerapkan konsep-konsep **Object-Oriented Programming (OOP)** melalui sistem pengelolaan bioskop, mulai dari pengelolaan film, studio, dan jadwal tayang hingga pemesanan tiket, pemilihan kursi, pembayaran, serta laporan transaksi.

Sistem memiliki dua jenis pengguna dengan hak akses berbeda:

- 👨‍💼 **Admin:** Mengelola film, studio, jadwal tayang, memverifikasi pembayaran, melakukan check-in tiket, serta melihat laporan penjualan.
- 👤 **User:** Melihat film dan jadwal tayang, memilih kursi, melakukan pemesanan tiket, memilih metode pembayaran, serta melihat riwayat dan struk pemesanan.

---

## 👥 Anggota Kelompok 5

| No. | Nama Anggota | NPM |
|:---:|---|---|
| 1 | Al Aul Tsaqif | 250810701100034 |
| 2 | Keisya Zahira | 250810701100038 |
| 3 | Siti Salwa Shafina | 250810701100055 |
| 4 | Arinal Haq | 250810701100073 |
| 5 | Annisa Fadhilah | 250810701100086 |
| 6 | Shandy Aprilliko Hartianto | 250810701100094 |

---

## 📌 Informasi Proyek

| Informasi | Keterangan |
|---|---|
| **Nama Aplikasi** | Tiket Bioskop Kita |
| **Mata Kuliah** | Praktikum Pemrograman Berorientasi Objek |
| **Jenis Tugas** | Ujian Akhir Semester (UAS) |
| **Kelas** | C |
| **Kelompok** | 5 |
| **Bahasa Pemrograman** | Java |
| **Antarmuka** | Command Line Interface (CLI) |
| **Penyimpanan Data** | File CSV |
| **Paradigma** | Object-Oriented Programming (OOP) |
| **Jumlah Package Java** | 7 |
| **Jumlah File Java** | 47 |

---

## 🚀 Cara Compile dan Menjalankan Program

### 📋 Persyaratan

Sebelum menjalankan program, pastikan perangkat telah memiliki:

- **Java Development Kit (JDK) 8 atau lebih baru**
- Terminal, Command Prompt, atau PowerShell
- Sistem operasi yang mendukung Java

Periksa versi Java yang terpasang dengan perintah:

```bash
javac -version
```

### ▶️ Langkah Menjalankan Program

Buka terminal pada direktori utama proyek `UAS_PBO_C_KLP_5`, kemudian jalankan perintah berikut:

```bash
cd src
javac utama/Main.java
java utama.Main
```

**Penjelasan perintah:**

1. `cd src` — Masuk ke direktori yang menyimpan seluruh source code dan data.
2. `javac utama/Main.java` — Mengompilasi class utama beserta class lain yang dibutuhkan.
3. `java utama.Main` — Menjalankan aplikasi Tiket Bioskop Kita.

Proses kompilasi menghasilkan file `.class` pada direktori yang sama dengan file `.java`.

Apabila terdapat perubahan kode, lakukan kompilasi ulang menggunakan:

```bash
javac utama/Main.java
```

### 💾 Penyimpanan Data Otomatis

Program menggunakan folder **`src/data/`** untuk menyimpan data akun, film, studio, jadwal, dan transaksi pemesanan dalam format CSV.

Saat aplikasi dijalankan dari direktori `src`, data akan dibaca dan disimpan melalui folder `data/` yang berada di dalam direktori tersebut.

Apabila data belum tersedia, program dapat membuat data contoh melalui proses inisialisasi awal. Untuk mengembalikan data ke kondisi awal, folder data yang digunakan program dapat dihapus sebelum program dijalankan kembali.

> **Perhatian:** Menghapus folder `data/` juga akan menghapus seluruh perubahan dan transaksi yang sebelumnya tersimpan. Pastikan data penting sudah dicadangkan.

---

## 🔐 Akun Login Contoh

Program menyediakan akun contoh untuk memudahkan pengujian fitur.

| Username | Password | Peran | Keterangan |
|---|---|---|---|
| `admin` | `admin123` | Admin | Mengelola seluruh sistem bioskop |
| `budi` | `budi123` | User | Pengguna dewasa dengan tiga pesanan contoh |
| `sinta` | `sinta123` | User | Pengguna berusia 14 tahun untuk pengujian batas usia film |

**Informasi tambahan:**

- Pengguna baru dapat membuat akun melalui menu **Register**.
- Password disimpan dalam bentuk hash menggunakan algoritma **SHA-256**.
- Setiap jenis akun memiliki menu dan hak akses yang berbeda.
- Login dilakukan menggunakan username dan password yang terdaftar.

---

## 🔄 Alur Kerja Sistem

**Mulai → Login / Register → Menu Admin / User → Proses Sesuai Hak Akses → Logout**

### 👤 Alur Pengguna (User)

1. Pengguna melakukan login menggunakan akun yang sudah terdaftar.
2. Sistem menampilkan menu khusus pengguna.
3. Pengguna melihat daftar film beserta jadwal tayang yang tersedia.
4. Pengguna memilih jadwal film yang ingin ditonton.
5. Sistem memvalidasi usia pengguna berdasarkan rating film dan memeriksa ketersediaan kursi.
6. Pengguna menentukan jumlah tiket yang akan dipesan.
7. Pengguna memilih kursi secara manual atau melalui rekomendasi otomatis.
8. Pengguna dapat memasukkan kode voucher untuk mendapatkan diskon.
9. Pengguna memilih metode pembayaran, yaitu tunai, e-wallet, atau transfer bank.
10. Sistem menampilkan rincian harga tiket, diskon, biaya administrasi, dan total pembayaran.
11. Setelah pengguna menyetujui transaksi, pesanan dibuat dengan status **MENUNGGU**.
12. Kursi yang dipilih langsung ditahan dan sistem menampilkan invoice beserta kode booking.
13. Setelah pembayaran diverifikasi admin, pengguna dapat melihat tiket melalui menu riwayat pemesanan.

### 👨‍💼 Alur Administrator (Admin)

1. Admin melakukan login ke dalam sistem.
2. Sistem menampilkan menu khusus administrator.
3. Admin dapat mengelola data film, studio, dan jadwal tayang.
4. Admin melihat daftar pesanan yang menunggu konfirmasi.
5. Jika pembayaran telah diterima, admin mengonfirmasi pesanan sehingga status menjadi **TERVERIFIKASI**.
6. Jika pembayaran belum diterima, admin dapat menolak pesanan sehingga status berubah menjadi **DITOLAK** dan kursi kembali tersedia.
7. Pada hari penayangan, admin memasukkan kode booking untuk memvalidasi tiket melalui proses check-in.
8. Tiket yang berhasil digunakan akan memiliki status **DIGUNAKAN**.
9. Admin dapat melihat laporan pendapatan, tiket terjual, film terlaris, serta tingkat okupansi bioskop.

### 🎟️ Status Pemesanan

| Status | Keterangan |
|---|---|
| ⏳ **MENUNGGU** | Pesanan sudah dibuat dan sedang menunggu verifikasi pembayaran |
| ✅ **TERVERIFIKASI** | Pembayaran telah dikonfirmasi oleh admin |
| ❌ **DITOLAK** | Pesanan ditolak dan kursi dilepas kembali |
| 🎫 **DIGUNAKAN** | Tiket telah digunakan melalui proses check-in |

---

## ✨ Fitur Utama Aplikasi

### 🎞️ 1. Pengelolaan Film

Admin dapat melakukan pengelolaan data film melalui fitur:

- Menampilkan seluruh film.
- Menambahkan film baru.
- Mengubah informasi film.
- Menghapus film.
- Mencari film berdasarkan judul atau genre.
- Mengurutkan film berdasarkan judul atau durasi.
- Mengelola durasi dan rating usia film.

### 🏢 2. Pengelolaan Studio

Sistem menyediakan pengelolaan studio dengan tipe dan kapasitas berbeda.

Fitur yang tersedia:

- Melihat daftar studio.
- Menambahkan studio.
- Mengubah informasi studio.
- Menghapus studio.
- Mengatur tipe studio Regular atau Premium.
- Menentukan jumlah baris dan kolom kursi.

### 📅 3. Pengelolaan Jadwal Tayang

Admin dapat membuat dan mengelola jadwal penayangan berdasarkan film dan studio.

Pengaturan jadwal meliputi:

- Pemilihan film dan studio.
- Penentuan tanggal penayangan.
- Penentuan jam mulai.
- Pengaturan harga dasar tiket.
- Perubahan dan penghapusan jadwal.
- Validasi untuk mencegah bentrokan jadwal.

Sistem menerapkan jeda minimal **15 menit** antara dua jadwal tayang pada studio yang sama.

### 💺 4. Pemesanan dan Pemilihan Kursi

Pengguna dapat melihat peta kursi bioskop melalui tampilan terminal.

**Keterangan simbol kursi:**

| Simbol | Keterangan |
|:---:|---|
| `[ ]` | Kursi kosong |
| `[X]` | Kursi sudah terisi |
| `[*]` | Kursi yang sedang dipilih |

Terdapat dua metode pemilihan kursi:

**Pemilihan Manual**

Pengguna memilih kursi secara langsung berdasarkan posisi yang diinginkan. Setiap pilihan diperiksa agar kursi yang dipilih masih tersedia.

**Rekomendasi Otomatis**

Sistem mencari kursi kosong yang berdampingan dan memiliki posisi paling dekat dengan bagian tengah studio.

Jika kursi berdampingan tidak tersedia, sistem akan memberikan alternatif kursi kosong terdekat serta informasi bahwa kursi tersebut terpisah.

### 💰 5. Perhitungan Harga Dinamis

Harga tiket dihitung berdasarkan tipe studio, hari penayangan, waktu tayang, voucher, dan metode pembayaran.

| Kondisi | Penyesuaian Harga |
|---|---|
| Studio Regular | Harga normal |
| Studio Premium | Harga × 1,5 |
| Akhir pekan | Tambahan 20% |
| Matinee hari kerja sebelum pukul 15.00 | Diskon 15% |

**Voucher yang tersedia:**

| Kode Voucher | Diskon |
|---|---|
| `HEMAT10` | 10% |
| `PELAJAR15` | 15% |
| `UAS20` | 20% |

Program juga memperhitungkan biaya administrasi berdasarkan metode pembayaran yang digunakan.

### 💳 6. Metode Pembayaran

Program mendukung beberapa metode pembayaran:

- **Tunai:** Pembayaran melalui loket bioskop.
- **E-Wallet:** Pembayaran menggunakan dompet digital.
- **Transfer Bank:** Pembayaran melalui rekening bank.

Sebelum pesanan dibuat, sistem menampilkan rincian harga berupa subtotal, potongan voucher, biaya administrasi, dan total pembayaran.

> **Catatan:** Pembayaran hanya berupa simulasi. Program tidak terhubung secara langsung dengan layanan bank maupun e-wallet.

### ⏱️ 7. Pesanan Kedaluwarsa Otomatis

Untuk menghindari kursi terkunci terlalu lama akibat transaksi yang tidak diselesaikan, sistem menyediakan mekanisme kedaluwarsa otomatis.

Ketentuannya:

- Batas waktu konfirmasi default adalah **30 menit**.
- Pesanan berstatus MENUNGGU yang melewati batas waktu akan ditolak secara otomatis.
- Kursi dari pesanan yang ditolak kembali tersedia.
- Pemeriksaan dilakukan setiap kali menu ditampilkan.
- Admin dapat mengubah batas waktu melalui menu 9.
- Perubahan batas waktu hanya berlaku selama program berjalan.

### 🎟️ 8. Check-in Tiket

Admin dapat memvalidasi tiket menggunakan kode booking yang dimiliki pengguna.

Aturan check-in:

- Tiket harus berstatus TERVERIFIKASI.
- Check-in hanya dapat dilakukan pada hari penayangan.
- Tiket yang berhasil digunakan berubah status menjadi DIGUNAKAN.
- Tiket yang sama tidak dapat digunakan untuk check-in dua kali.

### 📊 9. Dashboard dan Laporan Admin

Dashboard menampilkan informasi mengenai aktivitas dan transaksi bioskop.

Informasi yang tersedia:

- Total pendapatan.
- Jumlah tiket terjual.
- Jumlah pesanan berdasarkan status.
- Peringkat film terlaris.
- Jam tayang tersibuk.
- Tingkat okupansi jadwal mendatang.
- Rata-rata persentase keterisian kursi.

Laporan disajikan dalam bentuk teks, tabel, dan grafik sederhana berbasis terminal.

---

## ⚙️ Aturan dan Validasi Sistem

Program menerapkan sejumlah aturan untuk menjaga konsistensi data dan transaksi.

1. Pembayaran tidak dapat dikembalikan dan pengguna tidak memiliki fitur pembatalan setelah pesanan dibuat.
2. Admin hanya menolak pesanan jika pembayaran belum diterima.
3. Kursi ditahan sejak pesanan berstatus MENUNGGU.
4. Kursi kembali tersedia apabila pesanan berstatus DITOLAK.
5. Pesanan yang melewati batas waktu konfirmasi akan ditolak secara otomatis.
6. Maksimal pemesanan adalah **6 kursi** dalam satu transaksi.
7. Usia pengguna harus memenuhi rating usia film.
8. Film dan studio yang masih memiliki jadwal tidak dapat dihapus.
9. Jadwal yang sudah pernah dipesan tidak dapat diubah atau dihapus.
10. Jadwal pada studio yang sama harus memiliki jeda minimal 15 menit.
11. Pendapatan dan film terlaris hanya menghitung pesanan berstatus TERVERIFIKASI dan DIGUNAKAN.
12. Tiket yang sudah digunakan tidak dapat dipakai kembali.

---

## 📁 Struktur Project

Project **Tiket Bioskop Kita** disusun menggunakan package yang memisahkan pengelolaan data, proses bisnis, pembayaran, perhitungan harga, penanganan exception, serta antarmuka CLI.

```text
UAS_PBO_C_KLP_5/
│
├── src/
│   │
│   ├── data/
│   │   ├── akun.csv
│   │   ├── film.csv
│   │   ├── jadwal.csv
│   │   ├── pemesanan.csv
│   │   └── studio.csv
│   │
│   ├── exception/
│   │   ├── BioskopException.java
│   │   ├── DataSedangDipakaiException.java
│   │   ├── DataTidakDitemukanException.java
│   │   ├── JadwalBentrokException.java
│   │   ├── KursiTidakTersediaException.java
│   │   ├── LoginGagalException.java
│   │   ├── PembayaranGagalException.java
│   │   └── UsiaTidakMemenuhiException.java
│   │
│   ├── harga/
│   │   ├── HargaAkhirPekanStrategy.java
│   │   ├── HargaMatineeStrategy.java
│   │   ├── HargaStrategy.java
│   │   ├── HargaStudioStrategy.java
│   │   ├── KalkulatorHarga.java
│   │   ├── RincianHarga.java
│   │   └── Voucher.java
│   │
│   ├── model/
│   │   ├── Admin.java
│   │   ├── Akun.java
│   │   ├── Film.java
│   │   ├── Jadwal.java
│   │   ├── Pemesanan.java
│   │   ├── StatusPesanan.java
│   │   ├── Studio.java
│   │   ├── TipeStudio.java
│   │   └── User.java
│   │
│   ├── payment/
│   │   ├── Pembayaran.java
│   │   ├── PembayaranEWallet.java
│   │   ├── PembayaranFactory.java
│   │   ├── PembayaranNonTunai.java
│   │   ├── PembayaranTransfer.java
│   │   └── PembayaranTunai.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── CrudService.java
│   │   ├── FilmService.java
│   │   ├── JadwalService.java
│   │   ├── PemesananService.java
│   │   └── SaranKursi.java
│   │
│   ├── utama/
│   │   ├── DataAwal.java
│   │   ├── Main.java
│   │   ├── Menu.java
│   │   ├── MenuAdmin.java
│   │   └── MenuUser.java
│   │
│   └── util/
│       ├── FileHandler.java
│       ├── InputBerakhirException.java
│       ├── InputHelper.java
│       ├── Keamanan.java
│       ├── TabelData.java
│       └── TampilanCLI.java
│
├── .gitignore
└── README.md
```

### 📦 Penjelasan Folder dan Package

| Folder / Package | Fungsi |
|---|---|
| 📂 `data` | Menyimpan data akun, film, jadwal, studio, dan pemesanan dalam format CSV |
| ⚠️ `exception` | Menangani kesalahan dan validasi khusus dalam sistem |
| 💰 `harga` | Mengelola strategi harga tiket, diskon, dan voucher |
| 🎬 `model` | Menyimpan class yang merepresentasikan objek utama bioskop |
| 💳 `payment` | Mengatur jenis dan proses pembayaran |
| ⚙️ `service` | Menangani autentikasi, pengelolaan data, dan proses bisnis |
| 🖥️ `utama` | Menjalankan aplikasi dan mengatur menu admin serta user |
| 🛠️ `util` | Menyediakan fungsi input, tampilan, keamanan, dan penyimpanan |

### 📊 Ringkasan Struktur Project

| Komponen | Jumlah |
|---|---:|
| Package Java | 7 |
| File Java | 47 |
| File CSV | 5 |
| README.md | 1 |
| .gitignore | 1 |
| **Total File** | **54** |

Struktur proyek berkembang dari rancangan awal sekitar 21 file menjadi 47 file Java karena adanya penambahan fitur, pemisahan tanggung jawab class, pengelolaan exception, perhitungan harga, dan pemisahan menu berdasarkan jenis pengguna.

---

## 🧩 Penerapan Konsep Pemrograman Berorientasi Objek

Aplikasi menerapkan berbagai konsep OOP sebagai bagian dari tujuan pembelajaran Praktikum Pemrograman Berorientasi Objek.

| Konsep | Implementasi dalam Program |
|---|---|
| 🔒 **Encapsulation** | Atribut class disembunyikan menggunakan akses `private` dan diakses melalui getter atau setter dengan validasi data. |
| 🌳 **Inheritance** | `Admin` dan `User` mewarisi `Akun`. `PembayaranEWallet` dan `PembayaranTransfer` mewarisi `PembayaranNonTunai`. |
| 🔄 **Polymorphism** | Method `tampilkanMenu()` di-override pada `Admin` dan `User`, kemudian dipanggil melalui variabel bertipe `Akun`. |
| 🧱 **Abstraction** | Menggunakan abstract class `Akun`, `Menu`, dan `PembayaranNonTunai`. |
| 🔌 **Interface** | Menggunakan `Pembayaran`, `HargaStrategy`, dan `CrudService<T, ID>`. |
| 📚 **Collection** | Menggunakan `ArrayList` dan `HashMap` untuk mengelola berbagai data. |
| ⚠️ **Exception Handling** | Menggunakan `try-catch`, `throw`, `throws`, dan custom exception untuk menangani kesalahan program. |
| 🔗 **Composition** | `Jadwal` memiliki relasi dengan `Film` dan `Studio`, sedangkan `Pemesanan` terhubung dengan `User` dan `Jadwal`. |

### 🔧 Penerapan Konsep Java Tambahan

Selain konsep utama OOP, program juga menggunakan:

- **Generics:** `CrudService<T, ID>` untuk pengelolaan data dengan tipe yang fleksibel.
- **Comparable:** Diterapkan pada class `Film`.
- **Comparator:** Digunakan untuk pengurutan data film.
- **Enum:** Digunakan pada `TipeStudio`, `StatusPesanan`, dan `Voucher`.
- **Lambda dan Stream:** Digunakan dalam pengolahan laporan dan statistik.
- **Method Overriding:** Menyesuaikan perilaku method pada class turunan.
- **Javadoc:** Dokumentasi class dan method menggunakan komentar `/** ... */`.

---

## 🗃️ Sistem Penyimpanan Data

Program menggunakan file CSV sebagai media penyimpanan data secara lokal.

Seluruh file data utama ditempatkan pada direktori **`src/data/`**.

| File CSV | Fungsi |
|---|---|
| `akun.csv` | Menyimpan informasi akun admin dan user |
| `film.csv` | Menyimpan data film |
| `jadwal.csv` | Menyimpan jadwal penayangan |
| `pemesanan.csv` | Menyimpan transaksi pemesanan tiket |
| `studio.csv` | Menyimpan informasi studio bioskop |

### Cara Kerja Penyimpanan

1. Program membaca data CSV ketika sistem dijalankan.
2. Hubungan antarobjek disimpan menggunakan ID.
3. Data yang memiliki hubungan akan dibentuk kembali menjadi objek saat proses pembacaan.
4. Perubahan data disimpan melalui `FileHandler`.
5. Ketersediaan kursi dihitung berdasarkan pesanan yang masih menahan kursi.
6. Baris CSV yang rusak akan dilewati dan dilaporkan sebagai peringatan agar tidak menggagalkan seluruh pembacaan.

Penggunaan CSV memungkinkan program menyimpan data secara permanen tanpa memerlukan database eksternal.

---

## 🧪 Skenario Pengujian Program

Berikut beberapa skenario demonstrasi untuk menguji fungsionalitas sistem.

### 🔞 Skenario 1 — Validasi Rating Usia

**Langkah pengujian:**

1. Login menggunakan akun `sinta`.
2. Pilih film yang memiliki rating usia 17+.
3. Coba lakukan pemesanan tiket.
4. Perhatikan pesan validasi yang ditampilkan.

**Hasil yang diharapkan:** Sistem menolak pemesanan karena usia pengguna belum memenuhi rating usia film.

### 💺 Skenario 2 — Pemesanan Tiket dan Rekomendasi Kursi

**Langkah pengujian:**

1. Login menggunakan akun `budi`.
2. Pilih film dan jadwal tayang yang tersedia.
3. Pesan sebanyak 3 kursi.
4. Pilih fitur rekomendasi kursi otomatis.
5. Masukkan kode voucher `UAS20`.
6. Pilih metode pembayaran transfer.
7. Periksa rincian harga dan konfirmasi pesanan.

**Hasil yang diharapkan:** Sistem membuat transaksi baru berstatus MENUNGGU, menahan kursi, serta menampilkan invoice beserta kode booking.

### ✅ Skenario 3 — Verifikasi Pembayaran

**Langkah pengujian:**

1. Login menggunakan akun `admin`.
2. Buka menu konfirmasi atau penolakan pesanan.
3. Pilih pesanan yang telah dibuat oleh Budi.
4. Konfirmasi bahwa pembayaran telah diterima.

**Hasil yang diharapkan:** Status pesanan berubah menjadi TERVERIFIKASI dan tiket dapat dilihat oleh pengguna.

### 📄 Skenario 4 — Melihat Tiket dan Struk

**Langkah pengujian:**

1. Login kembali menggunakan akun `budi`.
2. Buka menu Riwayat Pesanan.
3. Pilih kode booking transaksi.
4. Periksa informasi tiket yang ditampilkan.

**Hasil yang diharapkan:** Sistem menampilkan informasi film, jadwal, studio, kursi, pembayaran, dan status pesanan.

### 🎟️ Skenario 5 — Validasi Check-in Tiket

**Langkah pengujian:**

1. Pastikan terdapat tiket terverifikasi untuk jadwal tayang hari ini.
2. Login menggunakan akun `admin`.
3. Masukkan kode booking tiket melalui fitur Check-in.
4. Lakukan check-in.
5. Coba masukkan kembali kode booking yang sama.
6. Buka menu Laporan dan Dashboard.

**Hasil yang diharapkan:** Check-in pertama berhasil, sedangkan percobaan kedua ditolak karena tiket sudah digunakan.

### ⏱️ Skenario 6 — Pesanan Kedaluwarsa Otomatis

**Langkah pengujian:**

1. Login sebagai admin.
2. Buka menu 9 untuk mengatur batas konfirmasi.
3. Ubah batas menjadi 1 menit.
4. Login sebagai user dan buat pesanan baru.
5. Biarkan pesanan melewati batas waktu konfirmasi.
6. Buka kembali menu sistem.

**Hasil yang diharapkan:** Pesanan otomatis berubah menjadi DITOLAK dan kursi kembali tersedia.

---

## ⚠️ Catatan Penting

- Check-in hanya dapat dilakukan pada hari penayangan.
- Data contoh dibuat berdasarkan tanggal ketika data awal pertama kali diinisialisasi.
- Jika jadwal contoh sudah lewat, admin dapat membuat jadwal baru.
- Nomor rekening dan informasi e-wallet hanya digunakan untuk simulasi.
- Program tidak melakukan transaksi keuangan secara langsung.
- Password masih terlihat ketika diketik pada terminal.
- Pengaturan batas waktu konfirmasi tidak disimpan secara permanen. Setelah program dijalankan kembali, batas kembali menjadi 30 menit.
- Aplikasi dirancang untuk satu pengguna dalam satu waktu.
- Sistem menggunakan penyimpanan lokal berbasis CSV, bukan database eksternal.

---

## 🎯 Tujuan Pengembangan

Pengembangan aplikasi **Tiket Bioskop Kita** bertujuan untuk:

1. Menerapkan konsep Pemrograman Berorientasi Objek dalam pengembangan aplikasi Java.
2. Mengimplementasikan pengelolaan data menggunakan class, object, inheritance, abstraction, dan polymorphism.
3. Menerapkan interface, generics, collection, dan exception handling.
4. Mengembangkan sistem pemesanan tiket dengan validasi transaksi serta ketersediaan kursi.
5. Mengimplementasikan penyimpanan dan pembacaan data menggunakan file CSV.
6. Mengembangkan kemampuan kerja sama tim melalui pengelolaan proyek menggunakan Git dan GitHub.

---

## 🏁 Penutup

**🎬 Tiket Bioskop Kita — UAS Praktikum Pemrograman Berorientasi Objek Kelas C**

Proyek ini dikembangkan oleh **Kelompok 5** sebagai bentuk penerapan materi Praktikum Pemrograman Berorientasi Objek melalui aplikasi pemesanan tiket bioskop berbasis Java CLI.

Berbagai konsep OOP diterapkan secara terintegrasi untuk mendukung pengelolaan film, studio, jadwal, pemesanan tiket, pembayaran, validasi, dan laporan transaksi.

**Kelompok 5 — Praktikum Pemrograman Berorientasi Objek Kelas C**