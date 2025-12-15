package com.example.dispenser.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dispenser.data.model.Dispenser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Anotasi Database: Tentukan Entities, Version, dan exportSchema
@Database(entities = {Dispenser.class}, version = 1, exportSchema = false)
public abstract class DispenserDatabase extends RoomDatabase {

    // Abstract method untuk mengakses DAO
    public abstract DispenserDao dispenserDao();

    // Singleton instance untuk mencegah banyak instance database terbuka
    private static volatile DispenserDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // ExecutorService untuk menjalankan operasi database secara asinkron (background thread)
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Singleton getter
    public static DispenserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DispenserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DispenserDatabase.class, "dispenser_database")
                            .build(); // Membangun database Room
                }
            }
        }
        return INSTANCE;
    }
}