package com.example.dispenser.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dispenser.data.model.Dispenser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddDispenserRepository {
    public LiveData<List<Dispenser>> listDispenser(){
        DispenserRemoteDataSource dispenserRemoteDataSource=new DispenserRemoteDataSource();
        LiveData<List<Dispenser>> liveData =dispenserRemoteDataSource.listDispenser();
        return liveData;
    }

    public void updateDispenser(Dispenser dispenser) {
        DispenserRemoteDataSource dispenserRemoteDataSource=new DispenserRemoteDataSource();
        dispenserRemoteDataSource.updateDispenser(dispenser);
    }
}
