package com.example.dispenser.data.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dispenser_table")
public class Dispenser implements Parcelable {
    @PrimaryKey
    @NonNull
    private String deviceId;
    private String deviceName;



    private String userId;

    private boolean status;
    private int bottleCount;
    private long timeStart;
    private long lastOnTimeStamp;


    private boolean power;

    private int waterLevelTankA;
    private int waterLevelTankB;

    private int volumeFilledA;
    private int volumeFilledB;
    private String liquidNameA;
    private String liquidNameB;
    private String category;
    private int currentBottle;
    public Dispenser() {
        // WAJIB untuk Firebase
    }
    public Dispenser(String deviceName, boolean status) {
        this.deviceName = deviceName;
        this.status = status;
    }

    protected Dispenser(Parcel in) {
        deviceName = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            status = in.readBoolean();
        }
        bottleCount = in.readInt();
        timeStart = in.readLong();
        waterLevelTankA = in.readInt();
        waterLevelTankB = in.readInt();
        volumeFilledA = in.readInt();
        volumeFilledB = in.readInt();
        deviceId = in.readString();
        category = in.readString();
        liquidNameA = in.readString();
        liquidNameB = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            power = in.readBoolean();
        }
        userId = in.readString();
    }

    public static final Creator<Dispenser> CREATOR = new Creator<Dispenser>() {
        @Override
        public Dispenser createFromParcel(Parcel in) {
            return new Dispenser(in);
        }

        @Override
        public Dispenser[] newArray(int size) {
            return new Dispenser[size];
        }
    };

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getStatus() {
        return status;
    }

    public int getVolumeFilledA() {
        return volumeFilledA;
    }

    public int getVolumeFilledB() {
        return volumeFilledB;
    }

    public int getWaterLevelTankA() {
        return waterLevelTankA;
    }

    public int getWaterLevelTankB() {
        return waterLevelTankB;
    }
    public boolean isPower() {
        return power;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setPower(boolean power) {
        this.power = power;
    }

    public int getBottleCount() {
        return bottleCount;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setVolumeFilledA(int volumeFilledA) {
        this.volumeFilledA = volumeFilledA;
    }

    public void setVolumeFilledB(int volumeFilledB) {
        this.volumeFilledB = volumeFilledB;
    }

    public void setWaterLevelTankA(int waterLevelTankA) {
        this.waterLevelTankA = waterLevelTankA;
    }

    public void setWaterLevelTankB(int waterLevelTankB) {
        this.waterLevelTankB = waterLevelTankB;
    }

    public void setBottleCount(int bottleCount) {
        this.bottleCount = bottleCount;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(@NonNull String deviceId) {
        this.deviceId = deviceId;
    }
    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(deviceId);
        parcel.writeString(deviceName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(status);
        }
        parcel.writeInt(waterLevelTankA);
        parcel.writeInt(waterLevelTankB);
        parcel.writeInt(volumeFilledA);
        parcel.writeInt(volumeFilledB);
        parcel.writeInt(bottleCount);
        parcel.writeLong(timeStart);
        parcel.writeString(liquidNameA);
        parcel.writeString(liquidNameB);
        parcel.writeString(category);
        parcel.writeLong(lastOnTimeStamp);

    }

    public String getLiquidNameB() {
        return liquidNameB;
    }

    public void setLiquidNameB(String liquidNameB) {
        this.liquidNameB = liquidNameB;
    }

    public String getLiquidNameA() {
        return liquidNameA;
    }

    public void setLiquidNameA(String liquidNameA) {
        this.liquidNameA = liquidNameA;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentBottle() {
        return currentBottle;
    }

    public void setCurrentBottle(int currentBottle) {
        this.currentBottle = currentBottle;
    }

    public long getLastOnTimeStamp() {
        return lastOnTimeStamp;
    }

    public void setLastOnTimeStamp(long lastOnTimeStamp) {
        this.lastOnTimeStamp = lastOnTimeStamp;
    }
}
