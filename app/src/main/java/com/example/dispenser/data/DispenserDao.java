package com.example.dispenser.data;

// Di DispenserDao.java (Ini adalah interface/abstract class Room)

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dispenser.data.model.Dispenser;

@Dao
public interface DispenserDao {

    // Jika ada konflik pada Primary Key (yaitu deviceId), data lama akan diganti dengan data baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Dispenser dispenser);

    // Kamu juga butuh method untuk membaca data, yang dipakai oleh DispenserFragment
    @Query("SELECT * FROM dispenser_table ORDER BY timeStart DESC LIMIT 1")
    LiveData<Dispenser> getLastUsedDispenser();
}