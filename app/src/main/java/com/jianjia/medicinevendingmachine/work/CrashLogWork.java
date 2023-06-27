package com.jianjia.medicinevendingmachine.work;

import static com.jianjia.medicinevendingmachine.utils.BytesUtils.SubAndBase64Decode;

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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class CrashLogWork extends Worker {
    private final String TAG = "LogWork";
    private final HttpUtils mHttpUtils;

    @AssistedInject
    public CrashLogWork(@Assisted @NonNull Context context, @Assisted @NonNull WorkerParameters workerParams, HttpUtils httpUtils) {
        super(context, workerParams);
        this.mHttpUtils = httpUtils;
    }

    @NonNull
    @Override
    public Result doWork() {
        XLog.tag(TAG).i("上传异常日志-开始");
        String path = FilePathConstant.CRASH_FILE_PATH;
        String zipPath = FilePathConstant.SDCARD_PATH + File.separator + "crash.zip";
        XLog.tag(TAG).i("压缩文件:" + zipPath);
        File lFile = new File(path);
        String deviceNo = getInputData().getString("deviceNO");
        if (lFile.exists() && deviceNo != null) {
            File[] lFiles = lFile.listFiles();
            if (lFiles != null && lFiles.length > 0) {
                if (ZipUtils.zipFile(path, zipPath, false)) {
                    XLog.tag(TAG).i("上传异常日志-日志文件压缩成功");
                    mHttpUtils.upLoadFile(deviceNo, "crash", zipPath, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(@Nullable String result) {
                            if (result != null) {
                                JSONObject lJSONObject = JSON.parseObject(result);
                                if (lJSONObject.getString("code").equals("200")) {
                                    XLog.tag(TAG).i("上传异常日志-文件成功");
                                    FileUtil.deleteFileOrDir(lFile);
                                } else {
                                    XLog.tag(TAG).i("上传异常日志-文件失败 ：" + SubAndBase64Decode(lJSONObject.getString("msg")));
                                }
                            } else {
                                XLog.tag(TAG).i("上传异常日志-返回数据为空");
                            }
                            FileUtil.deleteFileOrDir(new File(zipPath));
                        }

                        @Override
                        public void onError(@NonNull Throwable ex, boolean isOnCallback) {
                            FileUtil.deleteFileOrDir(new File(zipPath));
                            XLog.tag(TAG).i("异常日志上传失败" + ex.getMessage());
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    XLog.tag(TAG).i("上传异常日志-日志文件打压缩包失败");
                    FileUtil.deleteFileOrDir(new File(zipPath));
                }
            } else {
                XLog.tag(TAG).i("上传异常日志-目录文件为空");
            }
        } else {
            XLog.tag(TAG).i("上传异常日志-不存在该目录");
        }
        return Result.success();
    }
}
