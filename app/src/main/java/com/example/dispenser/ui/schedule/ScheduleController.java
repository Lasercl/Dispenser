package com.example.dispenser.ui.schedule;
import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.dispenser.data.DispenserDao;
import com.example.dispenser.data.DispenserDatabase;
import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.PresetModel;
import com.example.dispenser.data.model.Dispenser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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


// --- Di dalam Activity atau Repository Class Anda ---
    // Di ScheduleController.java

// ... (Di bawah inisialisasi TAG dan rtdbRef) ...
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
    // ...
//    public LiveData<Dispenser> getLastDispenser() {
//
//        // Asumsi DAO punya query untuk mengambil data terakhir
//        return repository.getLastDispenser();
//    }
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
}
