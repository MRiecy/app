package com.jianjia.medicinevendingmachine.manger;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.jc.serialportmanger.OnDataListener;
import com.jc.serialportmanger.OnOpenSerialPortListener;
import com.jc.serialportmanger.SerialPortManger;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

public class Device {
    private static final String TAG = "Device";
    SerialPortManger mSerialPortManger;

    @Inject
    public Device() {
        Log.i(TAG, "初始化");
        mSerialPortManger = SerialPortManger.getInstance();
    }

    public void init() {
        XLog.tag(TAG).i("初始化串口");
        mSerialPortManger.setPathAndBaudRate(DeviceConstants.SERIAL_PORT_PATH, DeviceConstants.BAUD_RATE);
        mSerialPortManger.setReceivedTimeOut(30 * 1000);
        mSerialPortManger.openPort(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess() {
                XLog.tag(TAG).i("打开串口成功");
            }

            @Override
            public void onFail(Exception e) {
                XLog.tag(TAG).i("打开串口失败：" + e.getMessage());
            }
        });
    }

    public void addListener(OnDataListener onDataListener) {
        mSerialPortManger.addDataListener(onDataListener);
    }

    public void outGoods(double IRC, double iCC, int type) {
        XLog.tag(TAG).i("出货：" + IRC + "," + iCC + "," + type);
        String outGoodsCmd = IRC + "," + iCC + "," + type + "\\r\\n";
        mSerialPortManger.sendData(outGoodsCmd.getBytes(StandardCharsets.US_ASCII));
    }

    public void getTemperatureAndHumidity() {
        XLog.tag(TAG).i("获取温湿度");
        String tempAndHumCmd = "temp\\r\\n";
        mSerialPortManger.sendData(tempAndHumCmd.getBytes(StandardCharsets.US_ASCII));
    }

    public void closeDevice() {
        XLog.tag(TAG).i("关闭串口");
        mSerialPortManger.closeSerialPort();
    }

    public boolean isOpenDevice() {
        return mSerialPortManger.isOpen();
    }
}
