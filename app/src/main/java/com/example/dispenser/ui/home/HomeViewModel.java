package com.example.dispenser.ui.home;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.ui.FragmentDestinationMenu;

public class HomeViewModel extends AndroidViewModel {
    private DispenserRepository repo;


    private LiveData<Dispenser> realtimeDispenser;

    private final MutableLiveData<FragmentDestinationMenu> currentDestination = new MutableLiveData<>();

    public LiveData<FragmentDestinationMenu> getCurrentDestination() {
        return currentDestination;
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);
        // Sekarang kita bisa inisialisasi Repository dengan Application Context
        this.repo = new DispenserRepository(application);
    }
    public void setDestination(FragmentDestinationMenu destination) {
        currentDestination.setValue(destination);
    }
    public LiveData<Dispenser> listenDispenser(String deviceId) {
        realtimeDispenser = repo.listenDispenser(deviceId);
        return realtimeDispenser;
    }
    public String getDispenserLastId(){
        return repo.getDispenserLastId();
    }


}
