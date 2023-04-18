package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object lo) {
        if (this == lo) return true;
        if (lo == null || getClass() != lo.getClass()) return false;
        ResultShoppingGoods lthat = (ResultShoppingGoods) lo;
        if (line == 0) {
            if (lthat.line != 0)
                return false;
        } else if (line != lthat.line)
            return false;
        if (colNo == 0) {
            return lthat.colNo == 0;
        } else return colNo == lthat.colNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((line == 0) ? 0 : Objects.hashCode(line));
        result = prime * result + ((colNo == 0) ? 0 : Objects.hashCode(colNo));
        return result;
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
