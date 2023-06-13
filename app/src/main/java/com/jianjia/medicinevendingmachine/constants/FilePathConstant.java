package com.jianjia.medicinevendingmachine.constants;

import android.os.Environment;

public class FilePathConstant {
    private FilePathConstant() {
    }

    //    public static final String SDCARD_PATH = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String LOG_FILE_PATH = SDCARD_PATH + "/log/";
    public static final String CRASH_FILE_PATH = SDCARD_PATH + "/crash/";
    public static final String APK_PATH = SDCARD_PATH + "/apk/";
    public static final String FILE_PATH = SDCARD_PATH + "/NewWebFile/";
    public static final String BANNER_IMG = "BannerImg/";
    public static final String BIG_SCREEN_ADVERTISING_DF_PATH_PORT = "file:///android_asset/advertising_port/index.html";
    public static final String BIG_SCREEN_ADVERTISING_DF_PATH_LAND = "file:///android_asset/advertising_land/index.html";
    public static final String SCREEN_CAPTURE_PATH = SDCARD_PATH + "/screenshot.jpg";

    public static final String QR_IMAGE_PATH = SDCARD_PATH + "/qr/QR.jpg";
}
