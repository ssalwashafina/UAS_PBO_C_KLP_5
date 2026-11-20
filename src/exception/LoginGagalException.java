package exception;

// Dilempar ketika username tidak ada atau password salah saat login.
public class LoginGagalException extends BioskopException {

    private static final long serialVersionUID = 1L;

    // Membuat exception login gagal.
    public LoginGagalException(String pesan) {
        super(pesan);
    }
}
