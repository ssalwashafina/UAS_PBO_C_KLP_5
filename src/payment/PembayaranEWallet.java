package payment;

// Pembayaran lewat e-wallet. Biaya admin 1,5% dari nominal, dibulatkan ke atas per Rp100.
public class PembayaranEWallet extends PembayaranNonTunai {

    // Membuat metode pembayaran e-wallet.
    public PembayaranEWallet() {
    }

    @Override
    public String getKode() {
        return "EWALLET";
    }

    @Override
    public String getNamaMetode() {
        return "E-Wallet";
    }

    @Override
    public double getBiayaAdmin(double jumlah) {
        return Math.ceil(jumlah * 0.015 / 100.0) * 100.0;
    }

    @Override
    protected String getTujuan() {
        return "0812-3456-7890 a.n. Bioskop Kita";
    }
}
