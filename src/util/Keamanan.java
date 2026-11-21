package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Fungsi hash password. Pakai SHA-256 bawaan Java, digabung dengan username supaya dua user dengan password sama tidak punya hash sama.
public final class Keamanan {

    private Keamanan() {
    }

    // Menghitung hash password.
    public static String hash(String username, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hasil = md.digest((username.toLowerCase() + ":" + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hasil) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia di JVM ini", e);
        }
    }
}
