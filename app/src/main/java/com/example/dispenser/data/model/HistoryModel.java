
package com.example.dispenser.data.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class HistoryModel {


    private String variant;
    private String deviceId;
    private String deviceName;
    private String userId;
    private boolean power;

    private int volumeFilledA;
    private int volumeFilledB;
    private String liquidNameA;
    private String liquidNameB;
    private int waterLevelTankA;
    private int waterLevelTankB;

    private int bottleCount;
    private int currentBottle;

    private long timeUsed; // durasi ON → OFF
    @ServerTimestamp
    private Date timeNow; // waktu event

    public HistoryModel() {}

    public HistoryModel(String deviceId, String deviceName, String userId,
                        boolean power, int volumeFilledA, int volumeFilledB,
                        String liquidNameA, String liquidNameB,
                        int waterLevelTankA, int waterLevelTankB,
                        int bottleCount, int currentBottle,
                        long timeUsed) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.userId = userId;
        this.power = power;
        this.volumeFilledA = volumeFilledA;
        this.volumeFilledB = volumeFilledB;
        this.liquidNameA = liquidNameA;
        this.liquidNameB = liquidNameB;
        this.waterLevelTankA = waterLevelTankA;
        this.waterLevelTankB = waterLevelTankB;
        this.bottleCount = bottleCount;
        this.currentBottle = currentBottle;
        this.timeUsed = timeUsed;
    }

    public void setBottleCount(int bottleCount) {
        this.bottleCount = bottleCount;
    }

    public void setCurrentBottle(int currentBottle) {
        this.currentBottle = currentBottle;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setLiquidNameA(String liquidNameA) {
        this.liquidNameA = liquidNameA;
    }

    public void setLiquidNameB(String liquidNameB) {
        this.liquidNameB = liquidNameB;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public void setTimeNow(Date timeNow) {
        this.timeNow = timeNow;
    }

    public void setTimeUsed(long timeUsed) {
        this.timeUsed = timeUsed;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getBottleCount() {
        return bottleCount;
    }

    public int getCurrentBottle() {
        return currentBottle;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getLiquidNameA() {
        return liquidNameA;
    }

    public String getLiquidNameB() {
        return liquidNameB;
    }

    public boolean isPower() {
        return power;
    }

    public Date getTimeNow() {
        return timeNow;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public String getUserId() {
        return userId;
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
    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
    // GETTER & SETTER semua field ...
}
