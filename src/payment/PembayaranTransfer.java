package payment;

// Pembayaran lewat transfer bank. Biaya admin tetap Rp2.500.
public class PembayaranTransfer extends PembayaranNonTunai {

    // Membuat metode pembayaran transfer bank.
    public PembayaranTransfer() {
    }

    @Override
    public String getKode() {
        return "TRANSFER";
    }

    @Override
    public String getNamaMetode() {
        return "Transfer Bank";
    }

    @Override
    public double getBiayaAdmin(double jumlah) {
        return 2500;
    }

    @Override
    protected String getTujuan() {
        return "Rek. 1234567890 a.n. Bioskop Kita";
    }
}
