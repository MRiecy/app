package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_TEMPERATURE_HUMIDITY;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_EQUIPMENT_FAILURE;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_OUT_GOODS;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_OUT_GOODS_RESULT;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SIGN_IN;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SYN_TIME;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_UP_DEVICE_MESSAGE;
import static com.jianjia.medicinevendingmachine.utils.BytesUtils.SubAndBase64Decode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.NetworkConfiguration;
import com.jianjia.medicinevendingmachine.utils.AppUtils;
import com.jianjia.medicinevendingmachine.utils.TimerManager;
import com.jianjia.medicinevendingmachine.work.CrashLogWork;
import com.jianjia.medicinevendingmachine.work.LogWork;
import com.jianjia.medicinevendingmachine.work.WorkProcessing;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;

import org.xutils.common.Callback;

import java.io.File;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class RemoteRepository {
    private final String TAG = "RemoteRepository";
    Context context;
    SocketNet socketNet;
    WorkProcessing workProcessing;
    HttpUtils mHttpUtils;

    @Inject
    public RemoteRepository(@ApplicationContext Context context, SocketNet socketNet, HttpUtils httpUtils, WorkProcessing workProcessing) {
        this.context = context;
        this.socketNet = socketNet;
        this.workProcessing = workProcessing;
        this.mHttpUtils = httpUtils;
    }

    public void initSockNet(String hostAddress, SocketActionAdapter socketActionAdapter) {
        socketNet.setConnectConfiguration(hostAddress, NetworkConfiguration.PORT).addListener(socketActionAdapter).connect();
    }

    public void signIn(String macAddress) {
        XLog.tag(TAG).i("设备签到：" + macAddress + " 程序版本：" + AppUtils.getAppVersionName(context));
        if (socketNet != null) {
            socketNet.sendData(CMD_CODE_SYN_TIME, new String[]{String.format("%-10s", "SYNCTIME"), String.format("%-10s", "TSFSERVER")});
            socketNet.sendData(CMD_CODE_SIGN_IN, new String[]{String.format("%-20s", macAddress),
//            socketNet.sendData(CMD_CODE_SIGN_IN, new String[]{String.format("%-20s", "d4:9c:dd:7c:1c:46"),
                    String.format("%-8s", AppUtils.getAppVersionName(context))
            });
        }
    }

    public void startHeartbeat() {
        XLog.tag(TAG).i("开始心跳");
        if (socketNet != null) {
            socketNet.startHeartbeat();
        }
    }

    public void heartFeed() {
        XLog.tag(TAG).i("心跳喂狗");
        if (socketNet != null) {
            socketNet.heartFeed();
        }
    }

    @SuppressLint("DefaultLocale")
    public void upDeviceMessage(String message) {
        XLog.tag(TAG).i("上传设备信息：" + message);
        if (socketNet != null) {
            socketNet.sendData(CMD_CODE_UP_DEVICE_MESSAGE, new String[]{String.format("%03d", message.length()), message, String.format("%-10s", System.currentTimeMillis() / 1000)});
        }
    }

    public void sendErrorCode(int code, int codeRow, int codeCol, long time) {
        XLog.tag(TAG).i("发送错误码：" + code + " " + codeRow + " " + codeCol);
        if (socketNet != null) {
            socketNet.sendData(CMD_CODE_EQUIPMENT_FAILURE, new String[]{String.format("%4s", code), String.format("%3s", codeRow),
                    String.format("%3s", codeCol), String.format("%4s", ""), String.format("%-10s", time)});
        }
    }

    public void getOutShoppingGoodsInfo(@NonNull String outGoodsCode) {
        if (socketNet != null) {
            XLog.tag(TAG).i("获取出货信息：" + outGoodsCode);
            socketNet.sendData(CMD_CODE_OUT_GOODS, new String[]{String.format("%-30s", outGoodsCode), String.format("%-10s", System.currentTimeMillis() / 1000)});
        }
    }

    @SuppressLint("DefaultLocale")
    public void sendGoodsShoppingResult(@NonNull String orderNum, @NonNull String outStatus,
                                        @NonNull String result, String time) {
        XLog.tag(TAG).i("发送出货结果:" + orderNum + " " + outStatus + " " + result);
        if (socketNet != null) {
            socketNet.sendData(CMD_CODE_OUT_GOODS_RESULT, new String[]{String.format("%-30s", orderNum), outStatus,
                    String.format("%04d", result.length()), result, String.format("%-10s", time)});
        }
    }

    public void upTemperatureAndHumidity(String temperature, String humidity) {
        XLog.tag(TAG).i("发送温度:" + temperature + "湿度：" + humidity);
        if (socketNet != null) {
            socketNet.sendData(CMD_CODE_DEVICE_TEMPERATURE_HUMIDITY, new String[]{String.format("%-8s", temperature),
                    String.format("%-8s", humidity)});
        }
    }

    public void timingUpLog(String deviceNo) {
        XLog.tag(TAG).i("定时上传日志开启");
        TimerManager.scheduledTasksOnDay(1, 0, 0, TimerManager.LOG_UPLOAD_TIME, new TimerTask() {
            @Override
            public void run() {
                Data deviceName = new Data.Builder().putString("deviceNO", deviceNo).putString("logFileName", "").build();
                workProcessing.doOneTimeWork(deviceName, LogWork.class, CrashLogWork.class);
            }
        });
    }

    public void upLoadLog(String logDate, String deviceNo) {
        Data logFileData = new Data.Builder().putString("logFileName", logDate).putString("deviceNO", deviceNo).build();
        workProcessing.doOneTimeWork(logFileData, LogWork.class, CrashLogWork.class);
    }

    public void getQRMessage(String deviceNO, Callback.CommonCallback<String> callback) {
        mHttpUtils.getQRInfo(deviceNO, callback);
    }

    public void downLoadFile(String url, String filePath, Callback.CommonCallback<File> callback) {
        mHttpUtils.downLoadFile(url, filePath, callback);
    }

    public void getNewAppInfo(String deviceNO, String macAddress, Callback.CommonCallback<String> callback) {
        mHttpUtils.getAppDownloadInfo(deviceNO, macAddress, callback);
    }

    public void getDeviceNewExtendedInformation(String deviceNo, String macAddress, Callback.CommonCallback<String> callback) {
        mHttpUtils.getDeviceNewExtendedInformation(deviceNo, macAddress, callback);
    }

    public void downLoadApk(String url, String filePath, Callback.CommonCallback<File> callback) {
        mHttpUtils.downLoadFile(url, filePath, callback);
    }

    public void upDataScreenCapture(@NonNull String filePath, String deviceNo) {
        XLog.tag(TAG).i("上传截屏文件：" + filePath);
        mHttpUtils.upLoadPicture(deviceNo, filePath, new Callback.CommonCallback<>() {
            @Override
            public void onSuccess(@NonNull String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject lJSONObject = JSON.parseObject(result);
                    String lCode = lJSONObject.getString("code");
                    if (lCode.equals("200")) {
                        XLog.tag(TAG).i("上传截图文件成功");
                    } else {
                        String lMsg = lJSONObject.getString("msg");
                        String lS = SubAndBase64Decode(lMsg);
                        XLog.tag(TAG).i("上传截图文件失败 ：" + lS);
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                XLog.tag(TAG).i("上传截屏文件失败:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void getDeviceConfigS(String deviceNO, Callback.CommonCallback<String> callback) {
        mHttpUtils.getDeviceConfigInfo(deviceNO, callback);
    }

    public void upDeviceConfigS(String jsonString) {
        mHttpUtils.UpDeviceInfo(jsonString, new Callback.CommonCallback<>() {
            @Override
            public void onSuccess(String result) {
                XLog.tag(TAG).i("结果：" + result);
                com.alibaba.fastjson.JSONObject mJson = com.alibaba.fastjson.JSON.parseObject(result);
                int lCode = mJson.getInteger("code");
                if (lCode == 200) {
                    XLog.tag(TAG).i("配置信息上传成功");
                } else {
                    XLog.tag(TAG).i("配置信息上传失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("配置信息上传失败：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void closeConnect() {
        if (socketNet != null) {
            socketNet.closeConnect();
        }
        mHttpUtils.cancel();
    }

}

