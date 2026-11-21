package exception;

// Dilempar ketika data tidak boleh diubah atau dihapus karena masih dipakai data lain, misalnya film yang masih punya jadwal, atau jadwal yang sudah punya kursi terpesan.

public class DataSedangDipakaiException extends BioskopException {

