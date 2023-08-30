package com.jianjia.medicinevendingmachine.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NetUtils {
    private static final String TAG = "NetUtils";
    private static ConnectivityManager mConnectivityManager;
    private static ConnectivityManager.NetworkCallback mNetworkCallback;

    private NetUtils() {
    }

    /**
     * 通用的获取mac地址的方式
     *
     * @return mac 地址
     */
    @Nullable
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().toLowerCase();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void registerNetworkMonitor(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        mNetworkCallback = networkCallback;
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mConnectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest.Builder lBuilder = new NetworkRequest.Builder();
            mConnectivityManager.registerNetworkCallback(lBuilder.build(), networkCallback);
        }
    }

    public static void unRegisterNetworkMonitor() {
        if (mConnectivityManager != null && mNetworkCallback != null) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    public static void getNetSignal(Context context) {
        Log.i(TAG, "信号检测");
        // String lS = netType(context);
        //  if (lS.equals("WIFI")) {
        checkWifiState(context);
        //  } else if (lS.equals("MOBILE")) {
        checkMobileState(context);
        //   } else {
        //      Log.i(TAG, "信号检测：" + lS);
        // }
    }

    private static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
        }
        return result;
    }


    private static void checkMobileState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //通过 listen 方法来侦听电话信息的改变，这里用来侦听网络信号的强度变化，具体还能侦听什么需要看 PhoneStateListener 类的源码
        if (hasSimCard(context)) {
            if (telephonyManager != null) {
                telephonyManager.listen(new PhoneStateListener() {
                    @Override
                    public void onServiceStateChanged(ServiceState serviceState) {
                        super.onServiceStateChanged(serviceState);
                    }

                    @Override
                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                        super.onSignalStrengthsChanged(signalStrength);
                        XLog.tag(TAG).i("信号信息:" + signalStrength);
                        int lSignalStrength = signalStrength.getGsmSignalStrength();
                        XLog.tag(TAG).i("gsm信号强度:" + lSignalStrength);
                        if (lSignalStrength == 99) {
                            XLog.tag(TAG).i("天线可能没插或损坏，没信号");
                        } else if (lSignalStrength >= 5 && lSignalStrength < 8) {
                            XLog.tag(TAG).i("信号差");
                        } else if (lSignalStrength >= 8 && lSignalStrength < 12) {
                            XLog.tag(TAG).i("信号比较好");
                        } else if (lSignalStrength >= 12) {
                            XLog.tag(TAG).i("信号很好");
                        } else {
                            XLog.tag(TAG).i("信号差");
                        }
                    }

                    @Override
                    public void onDataConnectionStateChanged(int state, int networkType) {
                        super.onDataConnectionStateChanged(state, networkType);
                    }
                }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        } else {
            XLog.tag(TAG).i("没有sim卡");
        }
    }

    /**
     * 网络接口是否可用（即网络连接是否可行）和/或连接（即是否存在网络连接，是否可以建立套接字并传递数据）
     *
     * @return {@code true} 网络可用
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = Objects.requireNonNull(connMgr).getActiveNetworkInfo();
        return (activeInfo != null && activeInfo.isConnected() && activeInfo.isAvailable());
    }

    /**
     * 网络接口是否可用（即网络连接是否可行）和/或连接（即是否存在网络连接，是否可以建立套接字并传递数据）
     *
     * @return {@code true} 网络可用
     */
    public static String netType(Context context) {
        if (isNetConnected(context)) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
            NetworkInfo lNetworkInfo = connManager.getActiveNetworkInfo();
            return lNetworkInfo.getTypeName();
        }
        return "无网络";
    }

    public static boolean ping() {
        Log.i(TAG, "开始网络检测");
        String result = null;
        Process p;
        try {
            p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + "www.baidu.com");// ping1次
            // 读取ping的内容，可不加。
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuffer = new StringBuilder();
            String content;
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.i(TAG, "ping的结果 : " + stringBuffer);
            // PING的状态
            int status = p.waitFor();
            p.destroy();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
                return false;
            }
        } catch (IOException | InterruptedException e) {
            result = "failed~ IOException";
        } finally {
            Log.i(TAG, "ping结果：" + result);
        }
        return false;
    }

    private static void checkWifiState(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        int wifi = mWifiInfo.getRssi();//获取wifi信号强度
        Log.i(TAG, "wifi信号强度：" + wifi);
        if (wifi > -50 && wifi < 0) {//最强
            XLog.tag(TAG).i("wifi信号最强");
        } else if (wifi > -70 && wifi < -50) {//较强
            XLog.tag(TAG).i("wifi信号较强");
        } else if (wifi > -80 && wifi < -70) {//较弱
            XLog.tag(TAG).i("wifi信号较弱");
        } else if (wifi > -100 && wifi < -80) {//微弱
            XLog.tag(TAG).i("wifi信号微弱");
        } else {
            XLog.tag(TAG).i("wifi没有信号");
        }
    }

    @SuppressLint("HardwareIds")
    public static String getSIMCardNumber(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String lSimSerialNumber = manager.getSimSerialNumber();
            Log.i(TAG, "sim序列号：" + lSimSerialNumber);
            if (lSimSerialNumber == null || lSimSerialNumber.equals("")) {
                return "00000000000000000000";
            }
            return manager.getSimSerialNumber();
        } catch (Exception e) {
            return "00000000000000000000";
        }
    }
}




