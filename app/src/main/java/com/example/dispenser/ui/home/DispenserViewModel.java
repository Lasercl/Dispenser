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

    public DispenserViewModel(@NonNull Application application) {
        super(application);
        // Sekarang kita bisa inisialisasi Repository dengan Application Context
        this.repo = new DispenserRepository(application);
    }

    public LiveData<Dispenser> getLastDispenser() {
        // Cukup panggil method dari Repository. LiveData akan diteruskan ke Fragment.
        return repo.getLastDispenser();
    }
    public void saveLastUsedDispenser(Dispenser dispenser) {
        // Kita panggil operasi Room dari background thread
        repo.insertDispenser(dispenser);
    }
    // TODO: Implement the ViewModel
}