package payment;

import exception.PembayaranGagalException;
import util.TampilanCLI;

// Induk pembayaran non tunai (e-wallet dan transfer).
public abstract class PembayaranNonTunai implements Pembayaran {

    // Membuat induk pembayaran non tunai, dipanggil oleh subclass.
    protected PembayaranNonTunai() {
    }

    // Mengambil tujuan pembayaran, misalnya nomor rekening atau nomor e-wallet.
    protected abstract String getTujuan();

    @Override
    public double proses(double total) throws PembayaranGagalException {
        if (total <= 0) {
            throw new PembayaranGagalException("Nominal pembayaran harus lebih dari 0.");
        }
        return total + getBiayaAdmin(total);
    }

    @Override
    public String buatInstruksi(String kodeBooking, double totalBayar) {
        return "Kirim " + TampilanCLI.rupiah(totalBayar) + " lewat " + getNamaMetode() + " ke:\n"
                + getTujuan() + "\n"
                + "Tulis kode booking " + kodeBooking + " di catatan pembayaran.";
    }
}
