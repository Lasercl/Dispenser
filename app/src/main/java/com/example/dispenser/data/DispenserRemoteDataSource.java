package com.example.dispenser.data;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.data.model.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispenserRemoteDataSource {
    private FirebaseDatabase database= FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference powerRef;
    private ValueEventListener powerListener;


    private ArrayList<Dispenser> dispenserList=new ArrayList<>();
    private DatabaseReference realtimeRef;
    private ValueEventListener realtimeListener;
    private Long lastOnTimestamp = null;
    private Boolean lastPowerState;
    private FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    public LiveData<Dispenser> listenDispenserRealtime(String deviceId) {

        MutableLiveData<Dispenser> realtimeData = new MutableLiveData<>();

        realtimeRef = database
                .getReference("dispenser")
                .child(deviceId);

        realtimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dispenser dispenser = snapshot.getValue(Dispenser.class);
                if (dispenser != null) {
                    dispenser.setDeviceId(deviceId);
                    realtimeData.setValue(dispenser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        realtimeRef.addValueEventListener(realtimeListener);
        return realtimeData;
    }

    // ==========================
    // 3️⃣ STOP LISTENER (PENTING)
    // ==========================
    public void stopRealtimeListener() {
        if (realtimeRef != null && realtimeListener != null) {
            realtimeRef.removeEventListener(realtimeListener);
        }
    }
    public LiveData<List<Dispenser>> listDispenser(){
        MutableLiveData<List<Dispenser>> liveData = new MutableLiveData<>();

        database.getReference("dispenser").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Dispenser> list = new ArrayList<>();
                DataSnapshot snapshot = task.getResult();
                Log.d("TAG", "snapshot children count: " + snapshot.getChildrenCount());


                for (DataSnapshot child : snapshot.getChildren()) {
                    //contoh aja
                    String id= child.getKey();
                    String name= child.child("deviceName").getValue(String.class);

                    Log.d("TAG", "listdispenser0 "+name );

                    boolean statusBool = child.child("status").getValue(Boolean.class);

                    Log.d("TAG", "listdispenser1 "+name );
                    int waterlevelTankA = child.child("waterLevelTankA").getValue(Integer.class);
                    int waterlevelTankB=child.child("waterLevelTankB").getValue(Integer.class);
                    int waterLiquidFilledA=child.child("volumeFilledA").getValue(Integer.class);
                    int waterLiquidFilledB=child.child("volumeFilledB").getValue(Integer.class);
                    String userId=child.child("userId").getValue(String.class);
                    boolean power=child.child("power").getValue(Boolean.class);
                    int bottlecount=child.child("bottleCount").getValue(Integer.class);
                    Long timeStart=child.child("timeStart").getValue(Long.class);
//                    String status;
//                    if(statusBool){
//                        status="Available";
//                    }else {
//                        status="Unavailable";
//                    }
                    Log.d("TAG", "listdispenser "+name + DispenserUtility.getStatus(statusBool));
                    Dispenser dispenser = new Dispenser(name, statusBool);
                    dispenser.setDeviceId(id);
                    dispenser.setWaterLevelTankA(waterlevelTankA);
                    dispenser.setWaterLevelTankB(waterlevelTankB);
                    dispenser.setVolumeFilledA(waterLiquidFilledA);
                    dispenser.setVolumeFilledB(waterLiquidFilledB);
                    dispenser.setTimeStart(timeStart);
                    dispenser.setBottleCount(bottlecount);
                    dispenser.setUserId(userId);
                    dispenser.setPower(power);
                    if (name != null) {
                        list.add(dispenser);
                    }
                }
                liveData.setValue(list);
            } else {
                Log.d("TAG", "error cijjjjj: " + task.getException());            }
        });

        return liveData;
    }


    public void updateDispenser(Dispenser dispenser){
        //salahh djangan set valuenya langsung
        database.getReference("dispenser").child(dispenser.getDeviceName()).setValue(dispenser);
    }
    public LiveData<Boolean> listenPower(String deviceId) {

        MutableLiveData<Boolean> powerLiveData = new MutableLiveData<>();

        powerRef = database
                .getReference("dispenser")
                .child(deviceId)
                .child("power");

        powerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean currentPower = snapshot.getValue(Boolean.class);
                if (currentPower == null) return;

                handlePowerChanged(deviceId, currentPower);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        powerRef.addValueEventListener(powerListener);
        return powerLiveData;
    }

    private void handlePowerChanged(String deviceId, boolean currentPower) {

        // pertama kali listener hidup → init state
        if (lastPowerState == null) {
            lastPowerState = currentPower;
            return;
        }

        // hanya trigger ON → OFF
        if (lastPowerState && !currentPower) {
            fetchDispenserAndSaveHistory(deviceId);
        }

        lastPowerState = currentPower;
    }
    private void fetchDispenserAndSaveHistory(String deviceId) {

        database.getReference("dispenser")
                .child(deviceId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Dispenser d = snapshot.getValue(Dispenser.class);
                    if (d == null) return;

                    long now = System.currentTimeMillis();
                    long timeUsed = 0;

                    if (d.getLastOnTimeStamp() !=0) {
                        timeUsed = now - d.getLastOnTimeStamp();
                    }

                    HistoryModel history = new HistoryModel(
                            deviceId,
                            d.getDeviceName(),
                            d.getUserId(),
                            false,
                            d.getVolumeFilledA(),
                            d.getVolumeFilledB(),
                            d.getLiquidNameA(),
                            d.getLiquidNameB(),
                            d.getWaterLevelTankA(),
                            d.getWaterLevelTankB(),
                            d.getBottleCount(),
                            d.getCurrentBottle(),
                            timeUsed
                    );

                    FirebaseFirestore.getInstance()
                            .collection("UserHistory")
                            .document(d.getUserId())
                            .collection("history")
                            .add(history);
                });
    }


    public void setDispenserPower(String deviceId, boolean power) {
        database.getReference("dispenser")
                .child(deviceId)
                .child("power")
                .setValue(power);
    }

    public void savePresetToFirestore(String name, int vA, int vB, String liquidA, String liquidB) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
//            Toast.makeText(this, "Anda harus login untuk menyimpan resep.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> presetData = new HashMap<>();
            // Gunakan categoryName sebagai nama dokumen atau field tambahan di sini
            presetData.put("namePresets", name); // Tambahkan field nama agar mudah dibaca
            presetData.put("createdBy", user.getUid());
            presetData.put("liquidA", liquidA);
            presetData.put("liquidB", liquidB);
            presetData.put("volumeA", vA);
            presetData.put("volumeB", vB);

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

    public LiveData<List<PresetModel>> getAllPresets() {
        MutableLiveData<List<PresetModel>> liveData = new MutableLiveData<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("presets")
                .whereEqualTo("createdBy", user.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    List<PresetModel> presets = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            try {
                                String id = document.getId();
                                String namePreset = document.getString("namePresets");
                                String createdBy = document.getString("createdBy");
                                String liquidA = document.getString("liquidA");
                                String liquidB = document.getString("liquidB");
                                String presetId = document.getId();

                                // Gunakan safe null check untuk Integer
                                Integer volA = document.getLong("volumeA") != null ? document.getLong("volumeA").intValue() : 0;
                                Integer volB = document.getLong("volumeB") != null ? document.getLong("volumeB").intValue() : 0;
                                PresetModel model=new PresetModel(id, namePreset, createdBy, liquidA, liquidB, volA, volB);
                                model.setPresetId(presetId);
                                presets.add(model);
                            } catch (Exception e) {
                                Log.e("Firestore", "Error parsing: " + e.getMessage());
                            }
                        }
                    }
                    liveData.setValue(presets);
                });

        return liveData;
    }

    public void updateSelectedRecipe(String deviceId, String namePresets, String liquidA, String liquidB, int volumeA, int volumeB) {
        database.getReference("dispenser")
                .child(deviceId).child("category").setValue(namePresets);
        database.getReference("dispenser")
                .child(deviceId).child("liquidNameA").setValue(liquidA);
        database.getReference("dispenser")
                .child(deviceId).child("liquidNameB").setValue(liquidB);
        database.getReference("dispenser")
                .child(deviceId).child("volumeFilledA").setValue(volumeA);
        database.getReference("dispenser")
                .child(deviceId).child("volumeFilledB").setValue(volumeB);
    }
    public void updateTankHeightA(String deviceId, int height) {
        database.getReference("dispenser").child(deviceId).child("containerHeightTankA").setValue(height);
    }
    public void updateTankHeightB(String deviceId, int height) {
        database.getReference("dispenser").child(deviceId).child("containerHeightTankB").setValue(height);
    }
    public void updateBottleCount(String deviceId, int bottleCount) {
        database.getReference("dispenser").child(deviceId).child("bottleCount").setValue(bottleCount);
    }
}
