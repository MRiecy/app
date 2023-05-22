package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ResultShopping {
    @PrimaryKey
    private int id;
    private String orderNO;
    private String orderState;
    private String orderResult;
    private String time;

    public ResultShopping(int id, String orderNO, String orderState, String orderResult, String time) {
        this.id = id;
        this.orderNO = orderNO;
        this.orderState = orderState;
        this.orderResult = orderResult;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(String orderNO) {
        this.orderNO = orderNO;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderResult() {
        return orderResult;
    }

    public void setOrderResult(String orderResult) {
        this.orderResult = orderResult;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ResultShopping{" +
                "id=" + id +
                ", orderNO='" + orderNO + '\'' +
                ", orderState='" + orderState + '\'' +
                ", orderResult='" + orderResult + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
