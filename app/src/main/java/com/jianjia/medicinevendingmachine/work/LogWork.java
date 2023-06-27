package com.jianjia.medicinevendingmachine.work;

import static com.jianjia.medicinevendingmachine.utils.BytesUtils.SubAndBase64Decode;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.FilePathConstant;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.HttpUtils;
import com.jianjia.medicinevendingmachine.utils.ZipUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.Calendar;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class LogWork extends Worker {
    private final String TAG = "LogWork";
    private final HttpUtils mHttpUtils;

    @AssistedInject
    public LogWork(@Assisted @NonNull Context context, @Assisted @NonNull WorkerParameters workerParams, HttpUtils httpUtils) {
        super(context, workerParams);
        this.mHttpUtils = httpUtils;
    }

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    public Result doWork() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String logFileName = getInputData().getString("logFileName");
        String deviceNo = getInputData().getString("deviceNO");
        if (logFileName == null || logFileName.equals("")) {
            logFileName = String.format("%2d-%02d-%02d", year, month, day);
            XLog.tag(TAG).i("定时日志名称:" + logFileName);
        }
        String nowFileName = String.format("%2d-%02d-%02d", year, month, nowDay) + ".txt";
        String logFilePath = FilePathConstant.LOG_FILE_PATH + logFileName + ".txt";
        XLog.tag(TAG).i("日志路径：" + logFilePath);
        String zipLogFilePath = FilePathConstant.SDCARD_PATH + File.separator + logFileName + ".zip";
        XLog.tag(TAG).i("压缩文件" + zipLogFilePath);
        if (new File(logFilePath).exists() && deviceNo != null) {
            if (ZipUtils.zipFile(logFilePath, zipLogFilePath, false)) {
                mHttpUtils.upLoadFile(deviceNo, logFileName, zipLogFilePath, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(@Nullable String result) {
                        if (result != null) {
                            JSONObject lJSONObject = JSON.parseObject(result);
                            if (lJSONObject.getString("code").equals("200")) {
                                XLog.tag(TAG).i("上传日志-文件成功");
                                File lFile = new File(FilePathConstant.LOG_FILE_PATH);
                                if (lFile.exists()) {
                                    File[] files = lFile.listFiles();
                                    if (files != null) {
                                        for (File file1 : files) {
                                            if (!file1.getName().equals(nowFileName)) {
                                                FileUtil.deleteFileOrDir(file1);
                                            }
                                        }
                                    }
                                }
                            } else {
                                XLog.tag(TAG).i("上传日志-文件失败 ：" + SubAndBase64Decode(lJSONObject.getString("msg")));
                            }
                            FileUtil.deleteFileOrDir(new File(zipLogFilePath));
                        } else {
                            XLog.tag(TAG).i("上传日志-返回数据为空");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable ex, boolean isOnCallback) {
                        FileUtil.deleteFileOrDir(new File(zipLogFilePath));
                        XLog.tag(TAG).i("日志上传失败" + ex.getMessage());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            } else {
                XLog.tag(TAG).i("上传日志-压缩失败");
            }
        }
        return Result.success();
    }
}
