package com.jianjia.medicinevendingmachine.log;

import android.content.Context;
import android.util.Log;

import com.jianjia.medicinevendingmachine.constants.FilePathConstant;
import com.jianjia.medicinevendingmachine.utils.AppUtils;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import xcrash.ICrashCallback;
import xcrash.XCrash;

public class CrashLogManger {
    private String TAG = "CrashLogManger";
    Context context;

    @Inject
    public CrashLogManger(@ApplicationContext Context context) {
        this.context = context;
    }

    public void init() {
        ICrashCallback callback = (logPath, emergency) -> {
            Log.i(TAG, " 程序无响应");
            // SystemUtils.SystemReboot(this);
        };
        String appVersionName = AppUtils.getAppVersionName(context);
        XCrash.init(context, new XCrash.InitParameters()
                .setAppVersion(appVersionName)
                .setJavaRethrow(true)
                .setJavaLogCountMax(10)
                .setJavaDumpAllThreadsCountMax(10)
                .setNativeRethrow(true)
                .setNativeLogCountMax(10)
                .setNativeDumpAllThreadsCountMax(10)
                .setAnrRethrow(true)
                .setAnrCallback(callback)
                .setAnrLogCountMax(10)
                .setLogDir(FilePathConstant.CRASH_FILE_PATH)
                .setLogFileMaintainDelayMs(1000));
    }

}
