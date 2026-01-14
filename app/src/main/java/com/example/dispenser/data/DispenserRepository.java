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

    public DispenserRepository(Application application) {
        DispenserDatabase db = DispenserDatabase.getDatabase(application);
        dispenserDao = db.dispenserDao();
        local=new DispenserLocalDataSource(application);
        remoteDataSource = new DispenserRemoteDataSource();

        // ... inisialisasi Firebase
    }

    public void insertDispenser(Dispenser dispenser) {
        // Jalankan operasi Room di background thread
        DispenserDatabase.databaseWriteExecutor.execute(() -> {

            dispenserDao.insert(dispenser);
            Log.d("Repo", "Dispenser " + dispenser.getDeviceName() + " berhasil disimpan ke Room.");
        });
    }
    public void selectDispenser(String dispenserId) {
        local.saveSelectedDispenserId(dispenserId);
    }


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

    public void updateBottleCount(String deviceId, int bottleCount) {
        remoteDataSource.updateBottleCount(deviceId, bottleCount);
    }
    public void updateTankHeightA(String deviceId, int height) {
        remoteDataSource.updateTankHeightA(deviceId,height);
    }
    public void updateTankHeightB(String deviceId, int height) {
        remoteDataSource.updateTankHeightB(deviceId,height);
    }

}
