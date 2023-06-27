package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

public class ResultShoppingGoods {
    private int line;
    private int colNo;
    private int successCount;
    private int failCount;

    public ResultShoppingGoods() {
    }

    public ResultShoppingGoods(int line, int colNo, int successCount, int failCount) {
        this.line = line;
        this.colNo = colNo;
        this.successCount = successCount;
        this.failCount = failCount;
    }

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

    @NonNull
    @Override
    public String toString() {
        return "ResultShoppingGoods{" +
                "line=" + line +
                ", colNo=" + colNo +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                '}';
    }
}
