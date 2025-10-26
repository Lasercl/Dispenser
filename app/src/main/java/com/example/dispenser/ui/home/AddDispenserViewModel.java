package com.example.dispenser.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.AddDispenserRepository;
import com.example.dispenser.data.DispenserRemoteDataSource;
import com.example.dispenser.data.model.Dispenser;

import java.util.List;

public class AddDispenserViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private AddDispenserRepository repo;

    public AddDispenserViewModel() {
        this.repo = new AddDispenserRepository();
    }

    public LiveData<List<Dispenser>> listDispenser(){
        LiveData<List<Dispenser>> liveData =repo.listDispenser();
        return liveData;
    }

    public void updateDispenser(Dispenser dispenser) {
        repo.updateDispenser(dispenser);

    }
}