package com.jianjia.medicinevendingmachine.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import com.szsicod.print.log.Utils;

import org.xutils.x;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application implements Configuration.Provider {
    private String TAG = "Application";
    @Inject
    HiltWorkerFactory mHiltWorkerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "初始化");
        Utils.init(this);
        x.Ext.init(this);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setWorkerFactory(mHiltWorkerFactory).build();
    }
}
