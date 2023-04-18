package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AdvertMould {
    @PrimaryKey
    private int id;
    private int mouldId;
    private double mouldVersion;
    @Ignore
    public AdvertMould() {
    }

    public AdvertMould(int id, int mouldId, double mouldVersion) {
        this.id = id;
        this.mouldId = mouldId;
        this.mouldVersion = mouldVersion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMouldId() {
        return mouldId;
    }

    public void setMouldId(int mouldId) {
        this.mouldId = mouldId;
    }

    public double getMouldVersion() {
        return mouldVersion;
    }

    public void setMouldVersion(double mouldVersion) {
        this.mouldVersion = mouldVersion;
    }

    @NonNull
    @Override
    public String toString() {
        return "AdvertMould{" +
                "id=" + id +
                ", mouldId=" + mouldId +
                ", mouldVersion=" + mouldVersion +
                '}';
    }
}
