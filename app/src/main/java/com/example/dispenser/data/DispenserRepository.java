package com.example.dispenser.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dispenser.data.model.Dispenser;

import java.util.List;

public class DispenserRepository {
    // Di DispenserRepository.java

    private DispenserDao dispenserDao;
    private DispenserLocalDataSource local;
    private DispenserRemoteDataSource remoteDataSource;

    public DispenserRepository() {
    }
// ... (Firebase services)

    // Anggap DispenserRepository diinisialisasi dengan Room Database
    public DispenserRepository(Application application) {
        DispenserDatabase db = DispenserDatabase.getDatabase(application);
        dispenserDao = db.dispenserDao();
        local=new DispenserLocalDataSource(application);
        remoteDataSource = new DispenserRemoteDataSource();

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
    public void selectDispenser(String dispenserId) {
        local.saveSelectedDispenserId(dispenserId);
    }

    // Method yang digunakan oleh Home Fragment untuk membaca data
//    public LiveData<Dispenser> getLastDispenser() {
//        // Asumsi DAO punya query untuk mengambil data terakhir
//        return dispenserDao.getLastUsedDispenser();
//    }
    public String getDispenserLastId(){
        return local.getSelectedDispenserId();

    }

    // REALTIME DISPENSER
    public LiveData<Dispenser> listenDispenser(String deviceId) {
        return remoteDataSource.listenDispenserRealtime(deviceId);
    }
    public LiveData<Boolean> listenPower(String deviceId) {
        return remoteDataSource.listenPower(deviceId);
    }

    // STOP LISTENER
    public void stopListenDispenser() {
        remoteDataSource.stopRealtimeListener();
    }

    public void setDispenserPower(String deviceId, boolean power) {
        remoteDataSource.setDispenserPower(deviceId, power);
    }

    public void savePresetToFirestore(String name, int vA, int vB, String liquidA, String liquidB) {
        remoteDataSource.savePresetToFirestore(name, vA, vB, liquidA, liquidB);
    }

    public LiveData<List<PresetModel>> getAllPresets() {
        return remoteDataSource.getAllPresets();
    }

    public void updateSelectedRecipe(String deviceId, String namePresets, String liquidA, String liquidB, int volumeA, int volumeB) {
        remoteDataSource.updateSelectedRecipe(deviceId, namePresets, liquidA, liquidB, volumeA, volumeB);
    }

// (Pastikan kamu sudah mengatur Executor/Coroutines untuk menjalankan operasi Room)
}
