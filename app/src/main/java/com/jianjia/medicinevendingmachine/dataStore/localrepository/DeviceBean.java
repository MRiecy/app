package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.annotation.NonNull;

import java.util.List;

public class DeviceBean {
    //设备编号
    private String DevNo;
    //取药柜类型（货柜高度1=1.5米，2=1.8米）
    private int QHT = 1;
    //取药柜类型（1=老对射，2=新对射）
    private int DT = 1;
    //波特率
    private int IB;
    //端口号
    private int ComNo = 4;
    //主柜类型 1：老主柜  2：新主柜
    private int ZT = 1;
    //是否关闭货物检测 0:不关闭，1：关闭对射1,2：关闭对射2:，3全部关闭
    private int isCD = 0;
    //机械臂放药是否抬起，0：是不抬起，大于0：是抬起距离
    private int UPLen = 0;
    //推进器伸出长度
    private int TJQOutLen = 0;
    //吸药感应长度
    private int XYN = 35;
    //验药感应长度
    private int YYN = 55;
    //商品信息，每个设备对应的货道列表
    private List<Goods> mList;

    public String getDevNo() {
        return DevNo;
    }

    public void setDevNo(String devNo) {
        DevNo = devNo;
    }

    public int getQHT() {
        return QHT;
    }

    public void setQHT(int QHT) {
        this.QHT = QHT;
    }

    public int getDT() {
        return DT;
    }

    public void setDT(int DT) {
        this.DT = DT;
    }

    public int getIB() {
        return IB;
    }

    public void setIB(int IB) {
        this.IB = IB;
    }

    public int getComNo() {
        return ComNo;
    }

    public void setComNo(int comNo) {
        ComNo = comNo;
    }

    public int getZT() {
        return ZT;
    }

    public void setZT(int ZT) {
        this.ZT = ZT;
    }

    public int getIsCD() {
        return isCD;
    }

    public void setIsCD(int isCD) {
        this.isCD = isCD;
    }

    public int getUPLen() {
        return UPLen;
    }

    public void setUPLen(int UPLen) {
        this.UPLen = UPLen;
    }

    public int getTJQOutLen() {
        return TJQOutLen;
    }

    public void setTJQOutLen(int TJQOutLen) {
        this.TJQOutLen = TJQOutLen;
    }

    public int getXYN() {
        return XYN;
    }

    public void setXYN(int XYN) {
        this.XYN = XYN;
    }

    public int getYYN() {
        return YYN;
    }

    public void setYYN(int YYN) {
        this.YYN = YYN;
    }

    public List<Goods> getmList() {
        return mList;
    }

    public void setmList(List<Goods> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceBean{" +
                "DevNo='" + DevNo + '\'' +
                ", QHT=" + QHT +
                ", DT=" + DT +
                ", IB=" + IB +
                ", ComNo=" + ComNo +
                ", ZT=" + ZT +
                ", isCD=" + isCD +
                ", UPLen=" + UPLen +
                ", TJQOutLen=" + TJQOutLen +
                ", XYN=" + XYN +
                ", YYN=" + YYN +
                ", mList=" + mList +
                '}';
    }
}
