package com.example.dispenser.data;

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
}
