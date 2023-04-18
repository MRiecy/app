package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "goods", primaryKeys = {"iR", "iC"})
public class Goods {
    private int iR;
    private int iC;
    private double iRC;
    private double iCC;
    private int AT = 1;//1是三吸头 2是两吸头

    @Ignore
    public Goods() {
    }

    public Goods(int iR, int iC, double iRC, double iCC, int AT) {
        this.iR = iR;
        this.iC = iC;
        this.iRC = iRC;
        this.iCC = iCC;
        this.AT = AT;
    }

    public int getIR() {
        return this.iR;
    }

    public void setIR(int iR) {
        this.iR = iR;
    }

    public int getIC() {
        return this.iC;
    }

    public void setIC(int iC) {
        this.iC = iC;
    }

    public double getIRC() {
        return this.iRC;
    }

    public void setIRC(double iRC) {
        this.iRC = iRC;
    }

    public double getICC() {
        return this.iCC;
    }

    public void setICC(double iCC) {
        this.iCC = iCC;
    }

    public int getAT() {
        return this.AT;
    }

    public void setAT(int AT) {
        this.AT = AT;
    }

    @NonNull
    @Override
    public String toString() {
        return "GoodsBean{" +
                "iR=" + iR +
                ", iC=" + iC +
                ", iRC=" + iRC +
                ", iCC=" + iCC +
                ", AT=" + AT +
                '}';
    }

}
