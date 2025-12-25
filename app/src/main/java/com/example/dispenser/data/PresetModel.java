package com.example.dispenser.data;

// File: PresetModel.java
public class PresetModel {
    private String id; // Ini akan menjadi nama/ID yang akan ditampilkan
    private String namePresets;
    private String createdBy;
    private String liquidA;
    private String liquidB;

    public int getVolumeA() {
        return volumeA;
    }

    public int getVolumeB() {
        return volumeB;
    }

    private int volumeA;
    private int volumeB;

    // Kontruktor
    public PresetModel() {}
    public PresetModel(String id,String namePresets, String createdBy, String liquidA, String liquidB, int volumeA, int volumeB) {
        this.id = id;
        this.namePresets=namePresets;
        this.createdBy = createdBy;
        this.liquidA = liquidA;
        this.liquidB = liquidB;
        this.volumeA = volumeA;
        this.volumeB = volumeB;
    }

    // Getter (diperlukan)
    public String getId() { return id; }
    public String getLiquidA() { return liquidA; }
    public String getLiquidB() { return liquidB; }
    // ... getter lainnya ...

    // Kita bisa override toString() untuk mendapatkan nama tampilan di dialog
    @Override
    public String toString() {
        // Gabungkan Liquid A dan Liquid B untuk tampilan yang ramah pengguna
        return liquidA + " & " + liquidB + " (" + volumeA + "ml)";
    }

    public String getNamePresets() {
        return namePresets;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLiquidA(String liquidA) {
        this.liquidA = liquidA;
    }

    public void setLiquidB(String liquidB) {
        this.liquidB = liquidB;
    }

    public void setNamePresets(String namePresets) {
        this.namePresets = namePresets;
    }

    public void setVolumeA(int volumeA) {
        this.volumeA = volumeA;
    }

    public void setVolumeB(int volumeB) {
        this.volumeB = volumeB;
    }
}