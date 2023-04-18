package com.jianjia.medicinevendingmachine.manger;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.jianjia.medicinevendingmachine.utils.BytesUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

public class QR {
    private static final String TAG = "QR";
    private int mPid = 19841;
    private int mVid = 1121;
    private ScanBackInterface mScanBackInterface;
    private UsbEndpoint mUsbEndpointIn;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbDevice mUsbDevice;
    public boolean mIsRunning = false;
    private final Object locker = new Object();
    private int end_flag = 0;
    private boolean isHex;
    private String mformat = "gbk";
    private Queue<byte[]> receivedQueue = new LinkedList<byte[]>();

    @Inject
    public QR() {
        Log.i(TAG,"初始化");
    }

    public void init(Context context) {
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            Log.i(TAG, "usb vid: " + usbDevice.getVendorId() + ", pid: " + usbDevice.getProductId());
            if (mVid == usbDevice.getVendorId() && mPid == usbDevice.getProductId() && mUsbManager.hasPermission(usbDevice)) {
                mUsbDevice = usbDevice;
                UsbInterface mUsbInterface = usbDevice.getInterface(0);
                if (mUsbInterface != null) {
                    Log.i(TAG, "获取输入端成功");
                    mUsbEndpointIn = mUsbInterface.getEndpoint(0);
                    mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.claimInterface(mUsbInterface, true);
                        mIsRunning = true;
                    }
                }
            }
        }
    }

    public void startScan() {
        new Thread(() -> {
            if (mUsbDevice == null) {
                Log.i(TAG, "No device to read from");
                return;
            }
            boolean readerStartedMsgWasShown = false;
            // We will continuously ask for the data from the device and store it in the queue.
            while (mIsRunning) {
                // Lock that is common for read/write methods.
                synchronized (locker) {
                    try {
                        // Show the reader started message once.
                        if (!readerStartedMsgWasShown) {
                            Log.i(TAG, "!!! Reader was started !!!");
                            readerStartedMsgWasShown = true;
                        }
                        // Read the data as a bulk transfer with the size = MaxPacketSize
                        int packetSize = mUsbEndpointIn.getMaxPacketSize();
                        Log.i(TAG, "数据最大长度：" + packetSize);
                        byte[] bytes = new byte[packetSize];
                        int r = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, bytes, packetSize, 50);
                        Log.i(TAG, "获取数据的长度：" + r);
                        if (r >= 0) {
                            Log.i(TAG,"协议数据："+ BytesUtils.bytesToHexString(bytes));
                            if (bytes[0] != 0x02 || bytes[1] <= 0x01) {//带协议输出过滤
                                Log.i(TAG, "数据不正确");
                                continue;
                            }
                            int i = 0;
                            int len = bytes[1];
                            byte[] trancatedBytes = new byte[len]; // Truncate bytes in the honor of r
                            Log.i(TAG, "数据长度：" + len);
                            for (; i < len; ) {
                                trancatedBytes[i] = bytes[i + 2];
                                i++;
                                if (end_flag == 0 && (bytes[i + 1] == 0x0d && bytes[i + 2] == 0x0a)) //找到协议结尾
                                    end_flag = 1;
                            }
                            receivedQueue.add(trancatedBytes); // Store received data
                            Log.i(TAG, String.format("Message received of lengths %s and content: %s", r, bytesToHexString(bytes, packetSize)));
                        }
                        if (1 == end_flag)
                            onDataReceived(receivedQueue);
                    } catch (NullPointerException e) {
                        Log.i(TAG, "Error happened while reading. No device or the connection is busy");
                    }
                }
            }
        }).start();
    }

    public void onDataReceived(Queue<byte[]> recQueue) {
        int size = 0;
        int queue_size = recQueue.size();
        end_flag = 0;
        if (recQueue.size() > 0) {
            for (byte[] b : recQueue) {
                if (b[0] != 0x02)
                    size += b.length;
            }
            byte[] buffer = new byte[size];
            int countLength = 0;
            for (int i = 0; i < queue_size; i++) {
                byte[] bytes = recQueue.poll();
                Log.d(TAG, "total=" + size + ",size is " + bytes.length + " , " +
                        "content is " + new String(bytes, 0, bytes.length) + " , hex content is " + bytesToHexString(bytes, bytes.length));
                if (bytes[0] != 0x02) {
                    System.arraycopy(bytes, 0, buffer, countLength, bytes.length);
                    countLength += bytes.length;
                }
            }
            Log.d(TAG, "buffer size is " + buffer.length + " , " +
                    "content is " + new String(buffer, 0, size) + " , hex content is " + bytesToHexString(buffer, size));
            mScanBackInterface.onScan(getString(buffer, size).replace("\r\n", ""));
        }
        recQueue.clear();
    }

    public String getString(byte[] bytes, int len) {
        if (isHex) {
            return bytesToHexString(bytes, len);
        } else {
            try {
                return new String(bytes, 0, len, mformat);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return new String(bytes, 0, len);
        }
    }

    public static String bytesToHexString(byte[] var0, int var1) {
        StringBuilder var2 = new StringBuilder();
        for (int var3 = 0; var3 < var1; ++var3) {
            String var4 = Integer.toHexString(255 & var0[var3]);
            if (var4.length() == 1) {
                var2.append('0');
            }
            var2.append(var4);
        }
        return var2.toString();
    }

    public void addDataListener(ScanBackInterface scanBackInterface) {
        mScanBackInterface = scanBackInterface;
    }

    interface ScanBackInterface {
        void onScan(String code);
    }
}
