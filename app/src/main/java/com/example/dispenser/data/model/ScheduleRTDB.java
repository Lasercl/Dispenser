package com.example.dispenser.data.model;

public class ScheduleRTDB {
    public int enabled;      // 1 = Aktif, 0 = Mati
    public int hour;
    public int minute;
    public int dowMask;     // Bitmask hari
    public String categoryName;
    public int volA;
    public int volB;
    public int count;       // Jumlah botol
    private String key; // Tambahkan ini

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public ScheduleRTDB() {}

    public ScheduleRTDB(int enabled, int hour, int minute, int dowMask, String categoryName, int volA, int volB, int count) {
        this.enabled = enabled;
        this.hour = hour;
        this.minute = minute;
        this.dowMask = dowMask;
        this.categoryName = categoryName;
        this.volA = volA;
        this.volB = volB;
        this.count = count;
    }
}