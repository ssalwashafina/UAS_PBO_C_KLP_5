package service;

import java.util.List;

import exception.BioskopException;
import exception.DataTidakDitemukanException;

// Kontrak operasi dasar tambah, ubah, hapus, cari, dan lihat semua.
public interface CrudService<T, ID> {

    // Menambah data baru.
    void tambah(T data) throws BioskopException;

    // Mengubah data yang sudah ada, dicari lewat ID milik data baru.
    void ubah(T data) throws BioskopException;

    // Menghapus data.
    void hapus(ID id) throws BioskopException;

    // Mencari data berdasarkan kunci.
    T cariById(ID id) throws DataTidakDitemukanException;

    // Mengambil semua data.
    List<T> getAll();
}
