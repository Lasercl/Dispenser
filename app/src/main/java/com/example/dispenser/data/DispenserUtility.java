package com.example.dispenser.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DispenserUtility {
    public static String getPower(){

        return "bntr";
    }
    public static String  getStatus(boolean status){
        String statusBool;
        if(status){
            statusBool="Available";
        }else {
            statusBool="Unavailable";
        }
        return statusBool;
    }
    public static String getTimeUsed(long timeUsed){
        long ms = timeUsed;

        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        minutes = minutes % 60;
        seconds = seconds % 60;
        return hours + " jam " + minutes + " menit " + seconds + " detik ";

    }
    public static String formatHistoryDate(Date date) {
        if (date == null) return "-";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm | dd MMM yyyy", new Locale("id", "ID"));

        return sdf.format(date);
    }
}
