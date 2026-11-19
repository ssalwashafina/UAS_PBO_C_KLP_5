package payment;

import exception.PembayaranGagalException;
import util.TampilanCLI;

// Pembayaran tunai di loket. Tidak ada biaya admin.
public class PembayaranTunai implements Pembayaran {

    // Membuat metode pembayaran tunai.
    public PembayaranTunai() {
    }

    @Override
    public String getKode() {
        return "TUNAI";
    }

    @Override
    public String getNamaMetode() {
        return "Tunai (bayar di loket)";
    }

    @Override
    public double getBiayaAdmin(double jumlah) {
        return 0;
    }

    @Override
    public double proses(double total) throws PembayaranGagalException {
        if (total <= 0) {
            throw new PembayaranGagalException("Nominal pembayaran harus lebih dari 0.");
        }
        return total + getBiayaAdmin(total);
    }

    @Override
    public String buatInstruksi(String kodeBooking, double totalBayar) {
        return "Bayar " + TampilanCLI.rupiah(totalBayar) + " tunai di loket.\n"
                + "Sebutkan kode booking " + kodeBooking + " ke kasir.";
    }
}
