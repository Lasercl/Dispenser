package com.example.dispenser.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.AddDispenserRepository;
import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.PresetModel;
import com.example.dispenser.data.model.Dispenser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DispenserViewModel extends AndroidViewModel {
    private DispenserRepository repo;
    private LiveData<Dispenser> realtimeDispenser;
    private LiveData<Boolean> realtimePower;

    public void deletePresetFromFirestore(String presetId) {
        if (presetId == null) return;
        // Ganti "presets" dengan nama node database kamu
        FirebaseFirestore.getInstance().collection("presets").document(presetId).delete();
    }
    public void updatePowerStatus(String deviceId, boolean power){
        repo.setDispenserPower(deviceId, power);
    }
    public DispenserViewModel(@NonNull Application application) {
        super(application);
        // Sekarang kita bisa inisialisasi Repository dengan Application Context
        this.repo = new DispenserRepository(application);
    }

//    public LiveData<Dispenser> getLastDispenser() {
//        // Cukup panggil method dari Repository. LiveData akan diteruskan ke Fragment.
//        return repo.getLastDispenser();
//    }
    public String getDispenserLastId(){
        return repo.getDispenserLastId();
    }
    public void saveLastUsedDispenser(String deviceid) {
        // Kita panggil operasi Room dari background thread
//        repo.insertDispenser(dispenser);
        repo.selectDispenser(deviceid);
    }

    // =====================
    // REALTIME MONITORING
    // =====================
    public LiveData<Dispenser> listenDispenser(String deviceId) {
        realtimeDispenser = repo.listenDispenser(deviceId);
        return realtimeDispenser;
    }
    public LiveData<Boolean> listenPower(String deviceId) {
        realtimePower = repo.listenPower(deviceId);
        return realtimePower;
    }
    public void updateBottleCount(String deviceId, int bottleCount) {
        repo.updateBottleCount(deviceId, bottleCount);
    }
    public void updateTankHeightA(String deviceId, int height) {
        repo.updateTankHeightA(deviceId,height);
    }
    public void updateTankHeightB(String deviceId, int height) {
        repo.updateTankHeightB(deviceId,height);
    }



    // =====================
    // STOP LISTENER
    // =====================
    @Override
    protected void onCleared() {
        super.onCleared();
        repo.stopListenDispenser();
    }

    public void savePresetToFirestore(String name, int vA, int vB, String liquidA, String liquidB) {
        repo.savePresetToFirestore(name, vA, vB, liquidA, liquidB);
    }

    public LiveData<List<PresetModel>>getAllPresets() {
        return repo.getAllPresets();
    }

    public void updateSelectedRecipe(String deviceId, String namePresets, String liquidA, String liquidB, int volumeA, int volumeB) {
        repo.updateSelectedRecipe(deviceId, namePresets, liquidA, liquidB, volumeA, volumeB);
    }
    // TODO: Implement the ViewModel
}