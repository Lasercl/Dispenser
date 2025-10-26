package com.example.dispenser.ui.home;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.ui.FragmentDestinationMenu;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<FragmentDestinationMenu> currentDestination = new MutableLiveData<>();

    public LiveData<FragmentDestinationMenu> getCurrentDestination() {
        return currentDestination;
    }

    public void setDestination(FragmentDestinationMenu destination) {
        currentDestination.setValue(destination);
    }

}
