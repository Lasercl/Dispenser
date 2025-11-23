package com.example.dispenser.data.model;

public class HistoryModel {
    private String dispenserId;
    private String variant;

    private String volume;
    private String bottleCount;
    
    private String timeUsed;
    private String dispenserVolume;
    private  String timestamp;

    public String getBottleCount() {
        return bottleCount;
    }

    public String getDispenserId() {
        return dispenserId;
    }

    public String getDispenserVolume() {
        return dispenserVolume;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimeUsed() {
        return timeUsed;
    }

    public String getVariant() {
        return variant;
    }

    public String getVolume() {
        return volume;
    }

    public HistoryModel(String bottleCount, String dispenserId, String dispenserVolume, String timestamp, String timeUsed, String variant, String volume) {
        this.bottleCount = bottleCount;
        this.dispenserId = dispenserId;
        this.dispenserVolume = dispenserVolume;
        this.timestamp = timestamp;
        this.timeUsed = timeUsed;
        this.variant = variant;
        this.volume = volume;
    }

}