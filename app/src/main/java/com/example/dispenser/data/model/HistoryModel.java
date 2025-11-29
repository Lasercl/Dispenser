package com.example.dispenser.data.model;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryModel {
    private String dispenserId;
    private String variant;

    private int volume;
    private int bottleCount;
    
    private Timestamp timeEnd;
    private int dispenserVolume;
    private  Timestamp timeStart;

    public void setDispenserId(String dispenserId) {
        this.dispenserId = dispenserId;
    }

    public void setBottleCount(int bottleCount) {
        this.bottleCount = bottleCount;
    }

    public void setDispenserVolume(int dispenserVolume) {
        this.dispenserVolume = dispenserVolume;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public HistoryModel() {

    }

    public int getBottleCount() {
        return bottleCount;
    }

    public String getDispenserId() {
        return dispenserId;
    }

    public int getDispenserVolume() {
        return dispenserVolume;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }
    public String getTimeUsed() {
        long diffMillis = timeEnd.toDate().getTime() - timeStart.toDate().getTime();

        if (diffMillis < 0) return "Invalid time";

        long seconds = diffMillis / 1000;
        long minutes = seconds / 60;
        long hours   = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        // Format: jam, menit, detik
        StringBuilder sb = new StringBuilder();

        if (hours > 0) sb.append(hours).append(" jam ");
        if (minutes > 0) sb.append(minutes).append(" menit ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append(" detik");

        return sb.toString().trim();
    }
    public String getTimeStamp() {
        Timestamp ts=timeStart;
        Date date = ts.toDate();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm | dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }


    public Timestamp getTimeStart() {
        return timeStart;
    }

    public String getVariant() {
        return variant;
    }

    public int getVolume() {
        return volume;
    }

    public HistoryModel(int bottleCount, String dispenserId, int dispenserVolume, Timestamp timeStart, Timestamp timeEnd, String variant, int volume) {
        this.bottleCount = bottleCount;
        this.dispenserId = dispenserId;
        this.dispenserVolume = dispenserVolume;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.variant = variant;
        this.volume = volume;
    }

}