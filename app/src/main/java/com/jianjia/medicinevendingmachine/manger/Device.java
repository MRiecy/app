package com.jianjia.medicinevendingmachine.manger;

import android.content.Context;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.tim.serialportlib.OnDataListener;
import com.tim.serialportlib.OnReportListener;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

public class Device {
    private static final String TAG = "Device";
    AndroidSerialPort mAndroidSerialPort;

    @Inject
    public Device(AndroidSerialPort androidSerialPort) {
        Log.i(TAG, "初始化");
        this.mAndroidSerialPort = androidSerialPort;
    }

    public void init(Context context) {
        XLog.tag(TAG).i("初始化串口");
        mAndroidSerialPort.init(DeviceConstants.SERIAL_PORT_PATH, DeviceConstants.BAUD_RATE);
    }

    public void addListener(OnDataListener onDataListener) {
        mAndroidSerialPort.addListener(onDataListener);
    }

    public void outGoods(double IRC, double iCC, int type) {
        XLog.tag(TAG).i("出货：" + IRC + "," + iCC + "," + type);
        String outGoodsCmd = IRC + "," + iCC + "," + type + "\\r\\n";
        mAndroidSerialPort.sendCmd(outGoodsCmd.getBytes(StandardCharsets.US_ASCII));
    }

    public void getTemperatureAndHumidity() {
        XLog.tag(TAG).i("获取温湿度");
        String tempAndHumCmd = "temp\\r\\n";
        mAndroidSerialPort.sendCmd(tempAndHumCmd.getBytes(StandardCharsets.US_ASCII));
    }

    public void outGoodsByReturn(double IRC, double iCC, int type, OnReportListener onReportListener) {
        XLog.tag(TAG).i("出货：" + IRC + "," + iCC + "," + type);
        String outGoodsCmd = IRC + "," + iCC + "," + type + "\\r\\n";
        mAndroidSerialPort.sendCmdByReturn(outGoodsCmd.getBytes(StandardCharsets.US_ASCII), onReportListener);
    }

    public void getTemperatureAndHumidityByReturn(OnReportListener onReportListener) {
        XLog.tag(TAG).i("获取温湿度");
        String tempAndHumCmd = "temp\\r\\n";
        mAndroidSerialPort.sendCmdByReturn(tempAndHumCmd.getBytes(StandardCharsets.US_ASCII), onReportListener);
    }

    public void closeDevice() {
        XLog.tag(TAG).i("关闭串口");
        mAndroidSerialPort.closeSerialPort();
    }

    public boolean isOpenDevice() {
        return mAndroidSerialPort.isOPen();
    }
}
