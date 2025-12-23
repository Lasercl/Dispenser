package com.example.dispenser.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.data.model.HistoryModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DispenserRemoteDataSource {
    private FirebaseDatabase database= FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference powerRef;
    private ValueEventListener powerListener;


    private ArrayList<Dispenser> dispenserList=new ArrayList<>();
    private DatabaseReference realtimeRef;
    private ValueEventListener realtimeListener;
    private Long lastOnTimestamp = null;
    private Boolean lastPowerState;

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
}
