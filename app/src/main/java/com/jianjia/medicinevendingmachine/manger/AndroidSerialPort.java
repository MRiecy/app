package com.jianjia.medicinevendingmachine.manger;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.tim.serialportlib.OnDataListener;
import com.tim.serialportlib.OnReportListener;
import com.tim.serialportlib.SerialPortManager;
import com.tim.serialportlib.SerialPortProtocol;

import javax.inject.Inject;

public class AndroidSerialPort {
    private static final String TAG = "AndroidSerialPort";
    private SerialPortManager mSerialPortManager;

    @Inject
    public AndroidSerialPort() {
        Log.i(TAG, "初始化");
    }

    public void init(String port, int baudRate) {
        try {
            mSerialPortManager = new SerialPortManager();
            XLog.tag(TAG).i("打开串口：" + port + "," + baudRate);
            mSerialPortManager.open(port, baudRate);
        } catch (Exception e) {
            XLog.tag(TAG).i(e.getMessage());
        }
    }

    public void sendCmd(byte[] cmd) {
        mSerialPortManager.sendBytes(cmd);
    }

    public void sendCmdByReturn(byte[] cmd, OnReportListener onReportListener) {
        mSerialPortManager.sendBytes(cmd, new SerialPortProtocol(), onReportListener);
    }

    public void addListener(OnDataListener onDataListener) {
        mSerialPortManager.setOnDataListener(onDataListener);
    }

    public void closeSerialPort() {
        XLog.tag(TAG).i("关闭串口");
        mSerialPortManager.close();
    }

    public boolean isOPen() {
        if (mSerialPortManager != null) {
            boolean open = mSerialPortManager.isOpen();
            XLog.tag(TAG).i("串口是否打开" + open);
            return open;
        }
        return false;
    }

}
