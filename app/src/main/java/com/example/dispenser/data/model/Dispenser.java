package com.example.dispenser.data.model;

public class Dispenser {
    private String deviceName;
    private String Status;
    private String waterlevel;


    public String getWaterlevel() {
        return waterlevel;
    }

    public void setWaterlevel(String waterlevel) {
        this.waterlevel = waterlevel;
    }


    public Dispenser(String deviceName, String status) {
        this.deviceName = deviceName;
        Status = status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
