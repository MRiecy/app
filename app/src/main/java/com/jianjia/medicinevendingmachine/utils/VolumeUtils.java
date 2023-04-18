package com.jianjia.medicinevendingmachine.utils;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.media.AudioManager;

import com.elvishew.xlog.XLog;

public class VolumeUtils {
    private String TAG = "VolumeUtils";
    private final AudioManager mAudioManager;
    private static VolumeUtils mVolumeUtils;

    public VolumeUtils(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static synchronized VolumeUtils getInstance(Context context) {
        if (mVolumeUtils == null) {
            mVolumeUtils = new VolumeUtils(context);
        }
        return mVolumeUtils;
    }

    public int getMaxVolume(int type) {
        return mAudioManager.getStreamMaxVolume(type);
    }

    public int getNowVolume(int type) {
        return mAudioManager.getStreamVolume(type);
    }

    public void setVolume(int type, int volume) {
        int lMaxVolume = getMaxVolume(type);
         XLog.tag(TAG).i("最大音量:" + lMaxVolume);
        int lNowVolume = getNowVolume(type);
         XLog.tag(TAG).i("当前音量:" + lNowVolume);
        mAudioManager.setStreamVolume(type, volume, 0);
    }
}
