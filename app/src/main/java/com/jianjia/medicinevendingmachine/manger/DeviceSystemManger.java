package com.jianjia.medicinevendingmachine.manger;

import android.content.Context;
import android.media.AudioManager;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.log.CrashLogManger;
import com.jianjia.medicinevendingmachine.log.LogManger;
import com.jianjia.medicinevendingmachine.utils.NetUtils;
import com.jianjia.medicinevendingmachine.utils.VolumeUtils;
import com.ys.rkapi.MyManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class DeviceSystemManger {
    private static final String TAG = "SystemManger";
    private final MyManager mMyManager;
    Context context;
    LogManger logManger;
    CrashLogManger crashLogManger;

    @Inject
    public DeviceSystemManger(@ApplicationContext Context context, LogManger logManger, CrashLogManger crashLogManger) {
        this.context = context;
        this.logManger = logManger;
        this.crashLogManger = crashLogManger;
        mMyManager = MyManager.getInstance(context);
    }

    public void init(MyManager.ServiceConnectedInterface serviceConnectedInterface) {
        mMyManager.setConnectClickInterface(serviceConnectedInterface);
        mMyManager.bindAIDLService(context);
    }

    public void startLog() {
        logManger.init();
        crashLogManger.init();
    }

    public void shutdown() {
        XLog.tag(TAG).i("关机");
        mMyManager.shutdown();
    }

    public void reboot() {
        XLog.tag(TAG).i("重启");
        mMyManager.reboot();
    }

    public boolean takeScreenshot(String path) {
        XLog.tag(TAG).i("截图：" + path);
        return mMyManager.takeScreenshot(path);
    }

    public boolean silentInstallApk(String apkPath) {
        XLog.tag(TAG).i("静默安装：" + apkPath);
        return mMyManager.silentInstallApk(apkPath, true);
    }

    public boolean installApk(String apkPath) {
        boolean isSuccess = false;
        XLog.tag(TAG).i("安装文件地址:" + apkPath);
        String command = "pm install -r " + apkPath + "\n";//拼接 pm install 命令，执行。-r表示若存在则覆盖安装
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            Process process = Runtime.getRuntime().exec("su");//申请root权限
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.write(command.getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                message.append(line);
            }
            XLog.tag(TAG).i("安装app" + message);
            if (!message.toString().contains("Failure")) {
                XLog.tag(TAG).i("安装app成功");
                isSuccess = true;
            }
            process.waitFor();//安装过程是同步的，安装完成后再读取结果
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    public boolean unInstallApk(String packageName) {
        XLog.tag(TAG).i("静默卸载：" + packageName);
        return mMyManager.unInstallApk(packageName);
    }

    public void setTime(int year, int month, int day, int hour, int minute, int sec) {
        mMyManager.setTime(year, month, day, hour, minute, sec);
    }

    public void switchAutoTime(boolean open) {
        mMyManager.switchAutoTime(open);
    }

    public void setSoftKeyboardHidden(boolean hidden) {
        mMyManager.setSoftKeyboardHidden(hidden);
    }

    public boolean isAutoSyncTime() {
        return mMyManager.isAutoSyncTime();
    }


    public String getMacAddress() {
        String macAddress = NetUtils.getMacAddress();
        XLog.tag(TAG).i("Mac1地址：" + macAddress);
        if (macAddress == null) {
            String lEthMacAddress = mMyManager.getEthMacAddress();
            XLog.tag(TAG).i("Mac2地址：" + lEthMacAddress);
            return lEthMacAddress;
        }
        return macAddress;
    }

    public void selfStart(String packageName) {
        XLog.tag(TAG).i("自启应用：" + packageName);
        mMyManager.selfStart(packageName);
    }

    public void daemon(String packageName, int value) {
        XLog.tag(TAG).i("设置守护程序：" + packageName);
        mMyManager.daemon(packageName, value);
    }

    public void hideNavBar(boolean hide) {
        XLog.tag(TAG).i("隐藏nav:" + hide);
        mMyManager.hideNavBar(hide);
    }

    public void hideStatusBar(boolean hide) {
        XLog.tag(TAG).i("隐藏statusBar:" + hide);
        mMyManager.hideStatusBar(hide);
    }

    public void changeVolume(int volume) {
        XLog.tag(TAG).i("设置音量：" + volume);
        VolumeUtils.getInstance(context).setVolume(AudioManager.STREAM_MUSIC, volume);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        String percentage = df.format(volume * 100L / 15);
        String soundPercentage = percentage.substring(0, percentage.length() - 3) + "%";
        XLog.tag(TAG).i("声音百分比：" + soundPercentage);
    }

    public void closeSystemManger() {
        XLog.tag(TAG).i("断开系统服务");
        if (mMyManager != null) {
            mMyManager.unBindAIDLService(context);
        }
    }
}
