package com.jianjia.medicinevendingmachine.manger;

import android.content.Context;

import com.elvishew.xlog.XLog;
import com.szsicod.print.escpos.PrinterAPI;
import com.szsicod.print.io.USBAPI;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class Printer {
    private static final String TAG = "Printer";
    private PrinterAPI mPrinterAPI;
    private Context mContext;

    @Inject
    public Printer(@ApplicationContext Context context) {
        this.mContext = context;
    }

    public void init() {
        XLog.tag(TAG).i("打印机初始化");
        mPrinterAPI = PrinterAPI.getInstance();
        if (mPrinterAPI.connect(new USBAPI(mContext)) == PrinterAPI.SUCCESS) {
            XLog.tag(TAG).i("连接设备");
        } else {
            XLog.tag(TAG).i("连接失败");
        }
    }

    public int getStatus() {
        XLog.tag(TAG).i("获取打印机状态");
        return mPrinterAPI.getStatus();
    }

    public boolean isConnect() {
        if (mPrinterAPI != null) {
            return mPrinterAPI.isConnect();
        }
        return false;
    }

    public void printString(String content) {
        try {
            mPrinterAPI.printString(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setAlignMode(int align_type) {
        mPrinterAPI.setAlignMode(align_type - 1);
    }

    public void setLineSpace(int line_spac) {
        mPrinterAPI.setLineSpace(line_spac);
    }

    public void printFeed() {
        mPrinterAPI.printFeed();
    }

    public void setCharSize(int hsize, int vsize) {
        mPrinterAPI.setCharSize(hsize, vsize);
    }

    public void printBarCode(int br_width, int br_height, String brString) {
        try {
            mPrinterAPI.sendOrder(new byte[]{0x1d, 0x48, 0x02});
            mPrinterAPI.setBarCodeWidth(br_width);
            mPrinterAPI.setBarCodeHeight(br_height);
            mPrinterAPI.printBarCode(72, brString.length(), brString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void printQRCode(String content, int modeSize, boolean isCut) {
        mPrinterAPI.printQRCode(content, modeSize, isCut);
    }

    public void fullCutPaper() {
        mPrinterAPI.fullCut();
    }

    public void close() {
        mPrinterAPI.disconnect();
    }
}
