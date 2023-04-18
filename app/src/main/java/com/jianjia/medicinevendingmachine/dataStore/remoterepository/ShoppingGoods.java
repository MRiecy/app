package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

public class ShoppingGoods implements Comparable<ShoppingGoods>  {

    private int line;
    private int colNo;
    private double iRC;
    private double iCC;
    private int AT = 1;//1是三吸头 2是两吸头
    private int successCount;
    private int failCount;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }

    public double getiRC() {
        return iRC;
    }

    public void setiRC(double iRC) {
        this.iRC = iRC;
    }

    public double getiCC() {
        return iCC;
    }

    public void setiCC(double iCC) {
        this.iCC = iCC;
    }

    public int getAT() {
        return AT;
    }

    public void setAT(int AT) {
        this.AT = AT;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    @Override
    public int compareTo(@NonNull ShoppingGoods o) {
        if (this.line == o.getLine()) {
            return (int) (iCC - o.getiCC());
        } else {
            return this.line - o.getLine();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "ShoppingGoods{" +
                "line=" + line +
                ", colNo=" + colNo +
                ", iRC=" + iRC +
                ", iCC=" + iCC +
                ", AT=" + AT +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                '}';
    }
}
