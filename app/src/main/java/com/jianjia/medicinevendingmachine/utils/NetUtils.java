package com.jianjia.medicinevendingmachine.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class NetUtils {
    private static final String TAG = "NetUtils";
    private static long lastRxTx;

    private NetUtils() {
    }

    /**
     * 获取主机地址
     *
     * @param host 需要解析的url
     * @return 主机IP
     */
    @Nullable
    public static String getHostAddress(@NonNull String host) {
        AsyncTask<String, Integer, String> lExecute = new AsyncTask<String, Integer, String>() {
            @Nullable
            @Override
            protected String doInBackground(String... strings) {
                Log.i(TAG, "要解析的地址" + strings[0]);
                String hostAddress = null;
                try {
                    InetAddress inetAddress = InetAddress.getByName(strings[0]);
                    hostAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.i(TAG, "域名解析出错");
                }
                return hostAddress;
            }
        }.execute(host);
        try {
            return lExecute.get();
        } catch (@NonNull ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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

    public static void getNetSignal(Context context) {
        Log.i(TAG, "信号检测");
        String lS = netType(context);
        if (lS.equals("WIFI")) {
            checkWifiState(context);
        } else if (lS.equals("MOBILE")) {
            checkMobileState(context);
        } else {
            Log.i(TAG, "信号检测：" + lS);
        }
    }

    private static void checkMobileState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //通过 listen 方法来侦听电话信息的改变，这里用来侦听网络信号的强度变化，具体还能侦听什么需要看 PhoneStateListener 类的源码
        if (telephonyManager != null) {
            telephonyManager.listen(new PhoneStateListener() {
                @Override
                public void onServiceStateChanged(ServiceState serviceState) {
                    super.onServiceStateChanged(serviceState);
                }

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    Log.i(TAG, "信号信息:" + signalStrength);
                    int lSignalStrength = signalStrength.getGsmSignalStrength();
                    Log.i(TAG, "gsm信号强度:" + lSignalStrength);
                    if (lSignalStrength == 99) {
                        Log.i(TAG, "天线可能没插或损坏，没信号");
                    } else if (lSignalStrength >= 12) {
                        Log.i(TAG, "信号很好");
                    } else if (lSignalStrength >= 8) {
                        Log.i(TAG, "信号很好");
                    } else if (lSignalStrength >= 5) {
                        Log.i(TAG, "信号差");
                    } else if (lSignalStrength < 5) {
                        Log.i(TAG, "信号很差");
                    }
                    int lCdmaDbm = signalStrength.getCdmaDbm();
                    Log.i(TAG, "信号功率:" + lCdmaDbm);
                    int lCdmaEcio = signalStrength.getCdmaEcio();
                    Log.i(TAG, "信号能量值" + lCdmaEcio);
                    int lEvdoDbm = signalStrength.getEvdoDbm();
                    Log.i(TAG, "信号cdma功率:" + lEvdoDbm);
                    int lEvdoEcio = signalStrength.getEvdoEcio();
                    Log.i(TAG, "信号能量值" + lEvdoEcio);
                    int lGsmBitErrorRate = signalStrength.getGsmBitErrorRate();
                    Log.i(TAG, "信号码率" + lGsmBitErrorRate);
                }

                @Override
                public void onDataConnectionStateChanged(int state, int networkType) {
                    super.onDataConnectionStateChanged(state, networkType);
                }
            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    private static String getNetSpeed() {
        //获得此刻系统收到的总的流量数据
        long tempSum = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();
        //得到此刻和上次的流量差值（可以设置 1 秒钟获取一次）
        long rxtxLast = tempSum - lastRxTx;
        double totalSpeed = rxtxLast * 1000 / 2000d;
        lastRxTx = tempSum;
        return showSpeed(totalSpeed);
    }

    /**
     * 格式化网络速率
     */
    private static String showSpeed(double speed) {
        String speedString;
        if (speed >= 1048576d) {
            speedString = speed / 1048576d + "MB/s";
        } else {
            speedString = speed / 1024d + "KB/s";
        }
        return speedString;
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
            p = Runtime.getRuntime().exec("ping -c 1 -w 10 " + "www.baidu.com");// ping1次
            // 读取ping的内容，可不加。
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuffer = new StringBuilder();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.i(TAG, "ping的结果 : " + stringBuffer.toString());
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
            Log.i(TAG, "最强");
        } else if (wifi > -70 && wifi < -50) {//较强
            Log.i(TAG, "较强");
        } else if (wifi > -80 && wifi < -70) {//较弱
            Log.i(TAG, "较弱");
        } else if (wifi > -100 && wifi < -80) {//微弱
            Log.i(TAG, "微弱");
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




