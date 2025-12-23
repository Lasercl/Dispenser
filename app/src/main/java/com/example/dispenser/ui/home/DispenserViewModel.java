package com.example.dispenser.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.AddDispenserRepository;
import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.model.Dispenser;

public class DispenserViewModel extends AndroidViewModel {
    private DispenserRepository repo;
    private LiveData<Dispenser> realtimeDispenser;
    private LiveData<Boolean> realtimePower;


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


    // =====================
    // STOP LISTENER
    // =====================
    @Override
    protected void onCleared() {
        super.onCleared();
        repo.stopListenDispenser();
    }
    // TODO: Implement the ViewModel
}