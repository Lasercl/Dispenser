package com.example.dispenser.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dispenser.data.model.Dispenser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DispenserRemoteDataSource {
    private FirebaseDatabase database= FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/");

    private ArrayList<Dispenser> dispenserList=new ArrayList<>();
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
}
