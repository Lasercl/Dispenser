package com.example.dispenser.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dispenser.data.model.Dispenser;

public class DispenserRepository {
    // Di DispenserRepository.java

    private DispenserDao dispenserDao;
// ... (Firebase services)

    // Anggap DispenserRepository diinisialisasi dengan Room Database
    public DispenserRepository(Application application) {
        DispenserDatabase db = DispenserDatabase.getDatabase(application);
        dispenserDao = db.dispenserDao();
        // ... inisialisasi Firebase
    }

    // Method untuk menyimpan Dispenser ke Room
    public void insertDispenser(Dispenser dispenser) {
        // Jalankan operasi Room di background thread
        DispenserDatabase.databaseWriteExecutor.execute(() -> {
            // Room akan secara otomatis menginsert/mengganti data
            // jika kamu menggunakan @Insert(onConflict = OnConflictStrategy.REPLACE) di DAO.
            dispenserDao.insert(dispenser);
            Log.d("Repo", "Dispenser " + dispenser.getDeviceName() + " berhasil disimpan ke Room.");
        });
    }

    // Method yang digunakan oleh Home Fragment untuk membaca data
    public LiveData<Dispenser> getLastDispenser() {
        // Asumsi DAO punya query untuk mengambil data terakhir
        return dispenserDao.getLastUsedDispenser();
    }

// (Pastikan kamu sudah mengatur Executor/Coroutines untuk menjalankan operasi Room)
}
