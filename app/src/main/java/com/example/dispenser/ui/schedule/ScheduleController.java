package com.example.dispenser.ui.schedule;
import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.dispenser.data.DispenserDao;
import com.example.dispenser.data.DispenserDatabase;
import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.PresetModel;
import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.data.model.ScheduleRTDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScheduleController {
    private DispenserDao dispenserDao;


    private DatabaseReference rtdbRef = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("dispenser");
    private DispenserRepository repository;
    public ScheduleController(Application application){
        DispenserDatabase db = DispenserDatabase.getDatabase(application);
        repository = new DispenserRepository(application);
        dispenserDao = db.dispenserDao();
    }

    public long calculateDateTimeMillis(
            String dateText,
            int hour12,
            int minute,
            int amPm
    ) {
        if (dateText == null || dateText.isEmpty()) {
            throw new IllegalStateException("Date not selected");
        }

        Calendar calendar = Calendar.getInstance();

        try {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
            calendar.setTime(sdf.parse(dateText));
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format", e);
        }

        // Convert 12h → 24h
        int hour = hour12;
        if (hour == 12) hour = 0;
        if (amPm == Calendar.PM) hour += 12;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public String getDispenserLastId(){
        return repository.getDispenserLastId();
    }
    public void confirmSchedule(String deviceId, PresetModel presetModel, long scheduledTimestamp,int bottleCount) {


        DatabaseReference deviceRef = rtdbRef.child(deviceId);
        deviceRef.child("category").setValue(presetModel.getNamePresets());
        deviceRef.child("timeStart").setValue(scheduledTimestamp);
        deviceRef.child("volumeFilledA").setValue(presetModel.getVolumeA());
        deviceRef.child("volumeFilledB").setValue(presetModel.getVolumeB());
        deviceRef.child("liquidNameA").setValue(presetModel.getLiquidA());
        deviceRef.child("liquidNameB").setValue(presetModel.getLiquidB());
        deviceRef.child("bottleCount").setValue(bottleCount);
        deviceRef.child("currentBottle").setValue(0);


    }
    public Single<List<PresetModel>> fetchPresetsRx(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return Single.create(emitter -> {

            // Menambahkan filter .whereEqualTo("createdBy", userId)
            db.collection("presets")
                    .whereEqualTo("createdBy", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<PresetModel> presets = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            try {
                                String id = document.getId();
                                String namePreset=document.getString("namePresets");
                                String createdBy = document.getString("createdBy");
                                String liquidA = document.getString("liquidA");
                                String liquidB = document.getString("liquidB");
                                int volA = document.get("volumeA",Integer.class);
                                int volB = document.get("volumeB",Integer.class);

                                if (liquidA != null && liquidB != null && volA != -1 && volB != -1) {
                                    PresetModel preset = new PresetModel(
                                            id,
                                            namePreset,
                                            createdBy,
                                            liquidA,
                                            liquidB,
                                            volA,
                                            volB
                                    );
                                    presets.add(preset);
                                }
                            } catch (Exception e) {
                                Log.e("Firestore", "Error parsing preset document: " + e.getMessage());
                            }
                        }
                        emitter.onSuccess(presets);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
    public void saveNewPresetToFirestore(String categoryName, String liquidA, int volumeA, String liquidB, int volumeB) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
//            Toast.makeText(this, "Anda harus login untuk menyimpan resep.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> presetData = new HashMap<>();
        // Gunakan categoryName sebagai nama dokumen atau field tambahan di sini
        presetData.put("namePresets", categoryName); // Tambahkan field nama agar mudah dibaca
        presetData.put("createdBy", user.getUid());
        presetData.put("liquidA", liquidA);
        presetData.put("liquidB", liquidB);
        presetData.put("volumeA", volumeA);
        presetData.put("volumeB", volumeB);

        db.collection("presets")
                .add(presetData)
                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(this, "Resep " + categoryName + " berhasil disimpan!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving preset", e);
//                    Toast.makeText(this, "Gagal menyimpan resep: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void confirmScheduleRTDB(String deviceId, int index, PresetModel preset,
                                    int hour, int minute, int dowMask, int count) {

        // Path: /dispenser/DEVICE123/schedules/0
        DatabaseReference ref = rtdbRef
                .child(deviceId)
                .child("schedules")
                .child(String.valueOf(index));

        // Buat Map data yang sesuai dengan struct Schedule di ESP32
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("enabled", 1);          // Langsung aktifkan
        scheduleData.put("hour", hour);
        scheduleData.put("minute", minute);
        scheduleData.put("dowMask", dowMask);    // Hasil hitungan bitmask hari
        scheduleData.put("categoryName", preset.getNamePresets());
        scheduleData.put("volA", preset.getVolumeA());
        scheduleData.put("volB", preset.getVolumeB());
        scheduleData.put("count", count);
        scheduleData.put("liquidNameA",preset.getLiquidA());
        scheduleData.put("liquidNameB",preset.getLiquidB());

        // Kirim ke Firebase RTDB
        ref.setValue(scheduleData)
                .addOnSuccessListener(aVoid -> {
                    // Berhasil
                })
                .addOnFailureListener(e -> {
                    // Gagal
                });
    }
    public Observable<List<ScheduleRTDB>> getAllSchedulesRx(String deviceId) {
        return Observable.create(emitter -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<ScheduleRTDB> list = new ArrayList<>();
                    for (DataSnapshot slot : snapshot.getChildren()) {
                        ScheduleRTDB s = slot.getValue(ScheduleRTDB.class);
                        if (s != null) {
                            s.setKey(slot.getKey()); // Mengambil "0", "1", dst dari Firebase
                            list.add(s);
                        }
                    }
                    if (!emitter.isDisposed()) {
                        emitter.onNext(list);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(error.toException());
                    }
                }
            };

            // Menempelkan listener ke Firebase
            DatabaseReference scheduleRef = rtdbRef.child(deviceId).child("schedules");
            scheduleRef.addValueEventListener(listener);

            // Membersihkan listener jika Rx disposes (mencegah memory leak)
            emitter.setCancellable(() -> scheduleRef.removeEventListener(listener));
        });
    }/**
     * Melakukan penyimpanan data ke slot index tertentu di RTDB
     */
    public void saveToRTDB(String deviceId, int index, PresetModel preset, int h, int m, int mask, int count) {
        // Path: dispenser/DEVICE123/schedules/0
        DatabaseReference ref = rtdbRef.child(deviceId).child("schedules").child(String.valueOf(index));

        // Bungkus data dalam HashMap agar struktur di Firebase rapi
        Map<String, Object> data = new HashMap<>();
        data.put("enabled", 1);
        data.put("hour", h);
        data.put("minute", m);
        data.put("dowMask", mask);
        data.put("categoryName", preset.getNamePresets());
        data.put("volA", preset.getVolumeA());
        data.put("volB", preset.getVolumeB());
        data.put("count", count);
        data.put("liquidNameA",preset.getLiquidA());
        data.put("liquidNameB",preset.getLiquidB());

        // Kirim ke Firebase
        ref.setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FIREBASE_SAVE", "Berhasil menyimpan di slot: " + index);
            } else {
                Log.e("FIREBASE_SAVE", "Gagal simpan", task.getException());
            }
        });
    }
    public Observable<ScheduleRTDB> getSingleScheduleRx(String deviceId, int index) {
        return Observable.create(emitter -> {
            rtdbRef.child(deviceId).child("schedules").child(String.valueOf(index))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ScheduleRTDB s = snapshot.getValue(ScheduleRTDB.class);
                            if (s != null) emitter.onNext(s);
                            emitter.onComplete();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            emitter.onError(error.toException());
                        }
                    });
        });
    }
    public void findEmptySlotAndSave(String deviceId, PresetModel preset, int h, int m, int mask, int count) {
        DatabaseReference ref = rtdbRef.child(deviceId).child("schedules");

        // Ambil data schedules dulu untuk cek mana yang kosong
        ref.get().addOnSuccessListener(snapshot -> {
            int targetSlot = -1;

            // Cek slot 0 sampai 7
            for (int i = 0; i < 8; i++) {
                if (!snapshot.child(String.valueOf(i)).exists() ||
                        Long.parseLong(snapshot.child(String.valueOf(i)).child("enabled").getValue().toString()) == 0) {
                    targetSlot = i;
                    break;
                }
            }

            if (targetSlot != -1) {
                // Simpan ke slot yang ketemu kosong tadi
                confirmScheduleRTDB(deviceId, targetSlot, preset, h, m, mask, count);
            } else {
                // Semua slot (0-7) penuh
                // Kamu bisa tambahkan callback ke Activity untuk kasih Toast "Jadwal Penuh"
            }
        });
    }
}
