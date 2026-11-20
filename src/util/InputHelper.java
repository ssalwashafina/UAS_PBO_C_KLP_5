package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Pembaca input keyboard.
public final class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm");

    private InputHelper() {
    }

    // Menampilkan pertanyaan dan membaca satu baris jawaban apa adanya.
    public static String baca(String prompt) {
        System.out.print(prompt);
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new InputBerakhirException();
        }
    }

    // Membaca teks yang tidak boleh kosong. Pertanyaan diulang kalau jawabannya kosong.
    public static String teks(String prompt) {
        while (true) {
            String s = baca(prompt);
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("  ! Tidak boleh kosong.");
        }
    }

    // Membaca angka bulat dalam rentang tertentu.
    public static int angka(String prompt, int min, int maks) {
        while (true) {
            try {
                int n = Integer.parseInt(baca(prompt));
                if (n < min || n > maks) {
                    System.out.println("  ! Angka harus antara " + min + " dan " + maks + ".");
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.println("  ! Masukkan angka bulat.");
            }
        }
    }

    // Membaca tanggal dengan format dd-MM-yyyy.
    public static LocalDate tanggal(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(baca(prompt + " (dd-MM-yyyy): "), FORMAT_TANGGAL);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Format tanggal salah. Contoh: 25-12-2026");
            }
        }
    }

    // Membaca jam dengan format HH:mm.
    public static LocalTime jam(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(baca(prompt + " (HH:mm): "), FORMAT_JAM);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Format jam salah. Contoh: 19:30");
            }
        }
    }

    // Membaca jawaban ya atau tidak.
    public static boolean ya(String prompt) {
        while (true) {
            String s = baca(prompt + " (y/n): ").toLowerCase();
            if (s.equals("y") || s.equals("ya")) {
                return true;
            }
            if (s.equals("n") || s.equals("t") || s.equals("tidak")) {
                return false;
            }
            System.out.println("  ! Jawab y atau n.");
        }
    }

    // Menunggu user menekan Enter.
    public static void enter() {
        baca("\nTekan Enter untuk lanjut...");
    }

    // Membaca teks dengan nilai bawaan. Kalau user hanya menekan Enter, nilai bawaan dipakai.
    public static String teksDefault(String prompt, String bawaan) {
        String s = baca(prompt + " [" + bawaan + "]: ");
        return s.isEmpty() ? bawaan : s;
    }

    // Membaca angka dengan nilai bawaan. Enter berarti memakai nilai bawaan.
    public static int angkaDefault(String prompt, int min, int maks, int bawaan) {
        while (true) {
            String s = baca(prompt + " [" + bawaan + "]: ");
            if (s.isEmpty()) {
                return bawaan;
            }
            try {
                int n = Integer.parseInt(s);
                if (n < min || n > maks) {
                    System.out.println("  ! Angka harus antara " + min + " dan " + maks + ".");
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.println("  ! Masukkan angka bulat.");
            }
        }
    }

    // Membaca tanggal dengan nilai bawaan. Enter berarti memakai nilai bawaan.
    public static LocalDate tanggalDefault(String prompt, LocalDate bawaan) {
        while (true) {
            String s = baca(prompt + " (dd-MM-yyyy) [" + bawaan.format(FORMAT_TANGGAL) + "]: ");
            if (s.isEmpty()) {
                return bawaan;
            }
            try {
                return LocalDate.parse(s, FORMAT_TANGGAL);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Format tanggal salah. Contoh: 25-12-2026");
            }
        }
    }

    // Membaca jam dengan nilai bawaan. Enter berarti memakai nilai bawaan.
    public static LocalTime jamDefault(String prompt, LocalTime bawaan) {
        while (true) {
            String s = baca(prompt + " (HH:mm) [" + bawaan.format(FORMAT_JAM) + "]: ");
            if (s.isEmpty()) {
                return bawaan;
            }
            try {
                return LocalTime.parse(s, FORMAT_JAM);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Format jam salah. Contoh: 19:30");
            }
        }
    }
}
