package com.jianjia.medicinevendingmachine.dataStore;

import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_ADVERTISING;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_ADVERT_DOWNLOAD;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_BANNER_DOWNLOAD;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_BOARD_REVERT;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_CHANGE_SOUND;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_DOWNLOAD_APK;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_MESSAGE_PUSH;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_OPEN_DOOR;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_PRINT_TICKET;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_SYNC_CONFIG_INFO;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DEVICE_TEMPERATURE_HUMIDITY;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_DOWNLOAD_DEVICE_LOGS;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_EQUIPMENT_FAILURE;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_FAULT_CHECK;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_GET_CONFIG_INFO;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_HEARTBEAT;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_MONITOR_CAPTURE;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_OUT_GOODS;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_OUT_GOODS_RESULT;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_RESTART_SYSTEM;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SCREEN_CAPTURE;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SHUTDOWN;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SIGN_IN;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_SYN_TIME;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_TEMPERATURE_HUMIDITY_STATE;
import static com.jianjia.medicinevendingmachine.dataStore.remoterepository.SocketCmdCode.CMD_CODE_UP_DEVICE_MESSAGE;
import static com.jianjia.medicinevendingmachine.utils.BytesUtils.SubAndBase64Decode;
import static com.jianjia.medicinevendingmachine.utils.BytesUtils.bytesToHexString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.R;
import com.jianjia.medicinevendingmachine.constants.DeviceStateConstant;
import com.jianjia.medicinevendingmachine.constants.FilePathConstant;
import com.jianjia.medicinevendingmachine.constants.NetworkConfiguration;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertContent;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertMould;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.DeviceBean;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.Goods;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.LocalRepository;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.DataOperation;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.PrintInfo;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.RemoteRepository;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.ResultShoppingGoods;
import com.jianjia.medicinevendingmachine.dataStore.remoterepository.ShoppingGoods;
import com.jianjia.medicinevendingmachine.manger.DeviceManger;
import com.jianjia.medicinevendingmachine.utils.AppUtils;
import com.jianjia.medicinevendingmachine.utils.BytesUtils;
import com.jianjia.medicinevendingmachine.utils.NetUtils;
import com.jianjia.medicinevendingmachine.utils.TimerManager;
import com.jianjia.medicinevendingmachine.utils.ZipUtils;
import com.tim.serialportlib.OnDataListener;
import com.tim.serialportlib.OnReportListener;
import com.tim.serialportlib.SerialPortError;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;

import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class Repository {
    private final String TAG = "Repository";
    private LocalRepository localRepository;
    private DeviceManger deviceManger;
    private RemoteRepository remoteRepository;
    private int outNo = 0;
    private int deviceState = DeviceStateConstant.DEVICE_NORMAL;
    private int orderState = 0;
    Context context;
    private String deviceNO;

    private int packageNo = 1;
    private final int packageCount = 2;
    private int failCount = 0;
    private boolean isOutGoods = false;
    private boolean isOutPackage = false;
    private int downLoadCount = 0;
    private MutableLiveData<Integer> mDeviceState;
    private MutableLiveData<String> mTempAndHumValue;
    private MutableLiveData<String> mQrPath;
    private Timer mTimer;

    @Inject

    public Repository(@ApplicationContext Context context, DeviceManger deviceManger, RemoteRepository remoteRepository, LocalRepository localRepository) {
        Log.i(TAG, "初始化");
        this.context = context;
        this.localRepository = localRepository;
        this.deviceManger = deviceManger;
        this.remoteRepository = remoteRepository;
    }

    public void init(String hostAddress, MutableLiveData<Integer> deviceStates, MutableLiveData<String> tempAndHumValue, MutableLiveData<String> deviceNoValue, MutableLiveData<String> qrPath) {
        this.mDeviceState = deviceStates;
        this.mTempAndHumValue = tempAndHumValue;
        this.mQrPath = qrPath;
        new Thread(() -> deviceManger.init()).start();
        remoteRepository.initSockNet(hostAddress, new SocketActionAdapter() {
            @Override
            public void onSocketIOThreadShutdown(String action, @NonNull Exception e) {
                XLog.tag(TAG).i("onSocketIOThreadShutdown:" + action + " " + e.getMessage());
            }

            //连接成功回调
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                XLog.tag(TAG).i("onSocketConnectionSuccess:" + info.getIp() + " " + action);
                remoteRepository.signIn();
            }

            //连接失败回调
            @SuppressLint("NewApi")
            @Override
            public void onSocketConnectionFailed(ConnectionInfo info, String action, @NonNull Exception e) {
                super.onSocketConnectionFailed(info, action, e);
                XLog.tag(TAG).i("onSocketConnectionFailed:" + action + " " + e.getMessage());
            }

            //连接状态由连接到断开回调
            @SuppressLint("NewApi")
            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, @NonNull Exception e) {
                super.onSocketDisconnection(info, action, e);
                XLog.tag(TAG).i("onSocketDisconnection:" + action + " " + e.getMessage());
            }

            //发送心跳后的回调
            @Override
            public void onPulseSend(ConnectionInfo info, @NonNull IPulseSendable data) {
                super.onPulseSend(info, data);
                XLog.tag(TAG).i("onPulseSend：" + bytesToHexString(data.parse()));
            }

            //发送给服务器回调
            @Override
            public void onSocketWriteResponse(ConnectionInfo info, String action, @NonNull ISendable data) {
                super.onSocketWriteResponse(info, action, data);
                String mCode = String.format("%02x", data.parse()[8]);
                XLog.tag(TAG).i("onSocketWriteResponse：" + mCode + action + "-- 数据：" + bytesToHexString(data.parse()));
            }

            //接受服务器数据回调
            @SuppressLint("NewApi")
            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, @NonNull OriginalData data) {
                XLog.tag(TAG).i("onSocketReadResponse：" + String.format("%02x", data.getHeadBytes()[8]) +
                        "-- 数据：头：" + bytesToHexString(data.getHeadBytes()) + "包体：" + bytesToHexString(data.getBodyBytes()));
                ByteBuffer bb = ByteBuffer.allocate(data.getHeadBytes().length + data.getBodyBytes().length);
                bb.order(ByteOrder.BIG_ENDIAN);
                bb.put(data.getHeadBytes());
                bb.put(data.getBodyBytes());
                byte[] RedData = bb.array();
                if (!DataOperation.DisposeSocketRedData(RedData)) {
                    XLog.tag(TAG).i("数据非法");
                    return;
                }
                byte[] cCode = new byte[2];//命令码
                System.arraycopy(RedData, 7, cCode, 0, cCode.length);
                byte[] bodyB = new byte[RedData.length - 10];//数据体
                System.arraycopy(RedData, 9, bodyB, 0, bodyB.length);
                String bodyStr = new String(bodyB);
                XLog.tag(TAG).i("包体：" + bodyStr);
                if (Arrays.equals(cCode, CMD_CODE_SIGN_IN)) {//签到返回
                    XLog.tag(TAG).i("签到返回内容：" + bodyStr);
                    String flag = bodyStr.substring(0, 3).trim();
                    XLog.tag(TAG).i("签到返回标识：" + flag);
                    switch (flag) {
                        case "200":
                            XLog.tag(TAG).i("签到成功");
                            remoteRepository.startHeartbeat();
                            int deviceLen = Integer.parseInt(bodyStr.substring(3, 5));
                            String lDeviceNo = bodyStr.substring(5, 5 + deviceLen);//设备编号
                            deviceNO = lDeviceNo;
                            deviceNoValue.postValue(deviceNO);
                            remoteRepository.sendErrorCode(0, 0, 0, (System.currentTimeMillis() / 1000));
                            XLog.tag(TAG).i("deviceNo: " + lDeviceNo);
                            String signMessage = SubAndBase64Decode(bodyStr, 5 + deviceLen, 8 + deviceLen);
                            XLog.tag(TAG).i(signMessage);
                            upDataConfig();
                            break;
                        case "-1":
                            XLog.tag(TAG).i("设备未注册,请注册,本设备mac为:\n" + NetUtils.getMacAddress());
                            break;
                        case "2":
                            XLog.tag(TAG).i("设备绑定失败");
                            break;
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_HEARTBEAT)) {//心跳返回
                    if (bodyStr.equals("000")) {
                        remoteRepository.heartFeed();
                        XLog.tag(TAG).i("心跳成功");
                    } else {
                        XLog.tag(TAG).i("心跳失败");
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_ADVERTISING)) {//获取设备广告程序
                    XLog.tag(TAG).i("通知下载广告程序和广告图片:" + bodyStr);
                    getDeviceExtendedInformation();
                } else if (Arrays.equals(cCode, CMD_CODE_OUT_GOODS)) {//出货
                    XLog.tag(TAG).i("出货信息接收:" + bodyStr);
                    String flag = bodyStr.substring(0, 3).trim();
                    String deviceGoodsStr = SubAndBase64Decode(bodyStr, 3, 7);
                    XLog.tag(TAG).i("出货信息：" + deviceGoodsStr);
                    if (flag.equals("200")) {
                        XLog.tag(TAG).i("出货接收:成功:" + deviceGoodsStr);
                        JSONObject lJSONObject = JSON.parseObject(deviceGoodsStr).getJSONObject("data");
                        String orderNo = lJSONObject.getString("orderNo");
                        int productCount = lJSONObject.getInteger("productCount");
                        JSONArray lOutCabinet = lJSONObject.getJSONArray("outCabinet");
                        ArrayList<ShoppingGoods> lShoppingGoodsList = new ArrayList<>();
                        ArrayList<ResultShoppingGoods> resultShoppingGoodsList = new ArrayList<>();
                        for (int i = 0; i < lOutCabinet.size(); i++) {
                            JSONObject lO = (JSONObject) lOutCabinet.get(i);
                            int rowNo = lO.getInteger("line");
                            int colNo = lO.getInteger("colNo");
                            int lOutCount = lO.getInteger("outCount");
                            ResultShoppingGoods resultShoppingGoods = new ResultShoppingGoods();
                            resultShoppingGoods.setLine(rowNo);
                            resultShoppingGoods.setColNo(colNo);
                            resultShoppingGoods.setSuccessCount(0);
                            resultShoppingGoods.setFailCount(0);
                            resultShoppingGoodsList.add(resultShoppingGoods);
                            Goods lGoods = localRepository.getGoods(rowNo, colNo);
                            if (lGoods != null) {
                                for (int j = 0; j < lOutCount; j++) {
                                    ShoppingGoods lShoppingGoods = new ShoppingGoods();
                                    double lIRC = lGoods.getIRC();
                                    double lICC = lGoods.getICC();
                                    lShoppingGoods.setLine(rowNo);
                                    lShoppingGoods.setColNo(colNo);
                                    lShoppingGoods.setiRC(lIRC);
                                    lShoppingGoods.setiCC(lICC);
                                    lShoppingGoodsList.add(lShoppingGoods);
                                }
                            } else {
                                XLog.tag(TAG).i("订单货道信息异常");
                            }
                        }
                        Collections.sort(lShoppingGoodsList);
                        if (lShoppingGoodsList.size() == productCount) {
                            shoppingGoods(orderNo, lShoppingGoodsList, resultShoppingGoodsList);
                        } else if (orderNo.equals("888888888888888888") && !lShoppingGoodsList.isEmpty()) {
                            deviceState = DeviceStateConstant.DEVICE_OUTING_GOODS;
                            mDeviceState.postValue(DeviceStateConstant.DEVICE_OUTING_GOODS);
                            ShoppingGoods lShoppingGoods = lShoppingGoodsList.get(0);
                            double lIR = lShoppingGoods.getiRC();
                            double lICC = lShoppingGoods.getiCC();
                            deviceManger.shoppingGoods(lIR, lICC, 0);
                        } else {
                            XLog.tag(TAG).i("订单信息异常");
                            mDeviceState.postValue(DeviceStateConstant.DEVICE_ORDER_ERROR);
                        }
                    } else {
                        XLog.tag(TAG).i("出货接收信息:" + deviceGoodsStr);
                        mDeviceState.postValue(DeviceStateConstant.DEVICE_NO_ORDER);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_OUT_GOODS_RESULT)) {//出货结果
                    int outGoodsFlag = Integer.parseInt(bodyStr.substring(0, 3).trim());
                    String sendOutGoodsBodyStr = SubAndBase64Decode(bodyStr.substring(3, bodyStr.length() + 1));
                    if (outGoodsFlag == 200) {
                        XLog.tag(TAG).i("出货结果接口接收成功:" + sendOutGoodsBodyStr);
                    } else {
                        XLog.tag(TAG).i("出货结果接口接收失败:" + sendOutGoodsBodyStr);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_SYN_TIME)) {//设备同步时间请求
                    XLog.tag(TAG).i("设备同步时间请求:" + bodyStr);
                    String flag = bodyStr.substring(0, 3);
                    if (flag.equals("200")) {
                        XLog.tag(TAG).i("设备同步时间请求成功:" + bodyStr);
                    } else {
                        XLog.tag(TAG).i("设备同步时间请求失败:" + bodyStr);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_GET_CONFIG_INFO)) {//获取机柜信息请求
                    XLog.tag(TAG).i("获取机柜信息请求:" + bodyStr);
                    String flag = bodyStr.substring(0, 1);
                    if (flag.equals("0")) {
                        String deviceConfigStr = SubAndBase64Decode(bodyStr, 1, 6);
                        XLog.tag(TAG).i("获取机柜信息请求成功:" + deviceConfigStr);
                    } else {
                        XLog.tag(TAG).i("获取机柜信息请求失败:" + bodyStr);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_EQUIPMENT_FAILURE)) {//发送机柜故障
                    XLog.tag(TAG).i("发送机柜故障:" + bodyStr);
                    String flag = bodyStr.substring(0, 3).trim();
                    if (flag.equals("200")) {
                        XLog.tag(TAG).i("发送机柜故障成功：" + bodyStr);
                    } else {
                        String lS = SubAndBase64Decode(bodyStr, 4, 6);
                        XLog.tag(TAG).i("发送机柜故障失败:" + lS);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_BOARD_REVERT)) {//上货
                    String goodsOnStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("上货:" + goodsOnStr);
                } else if (Arrays.equals(cCode, CMD_CODE_SCREEN_CAPTURE)) {//发起屏幕截屏
                    String adsStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("发起屏幕截屏:" + adsStr);
                    ADScreenCapture();
                } else if (Arrays.equals(cCode, CMD_CODE_MONITOR_CAPTURE)) {//发起监控截图
                    String adcStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("发起监控截图:" + adcStr);
                } else if (Arrays.equals(cCode, CMD_CODE_ADVERT_DOWNLOAD)) {//通知下载广告程序
                    String adpStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("通知下载广告程序:" + adpStr);
                    getDeviceExtendedInformation();
                } else if (Arrays.equals(cCode, CMD_CODE_BANNER_DOWNLOAD)) {//通知下载轮播图
                    String adBannerStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("通知下载轮播图:" + adBannerStr);
                    getDeviceExtendedInformation();
                } else if (Arrays.equals(cCode, CMD_CODE_RESTART_SYSTEM)) {//设备重启
                    String deviceReStar = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("设备重启:" + deviceReStar);
                    deviceManger.reboot();
                } else if (Arrays.equals(cCode, CMD_CODE_SHUTDOWN)) {//设备关机
                    String deviceOff = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("设备关机:" + deviceOff);
                    deviceManger.shutdown();
                } else if (Arrays.equals(cCode, CMD_CODE_FAULT_CHECK)) {//发起异常检测
                    String checkSelfString = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("发起异常检测:" + checkSelfString);
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_PRINT_TICKET)) {//打印请求给设备
                    //XLog.tag(TAG).i("打印请求:" + bodyStr);
                    String printStr = SubAndBase64Decode(bodyStr, 0, 6);
                    if (!TextUtils.isEmpty(printStr)) {
                        String lContent = JSON.parseObject(printStr).getString("content");
                        String lS = SubAndBase64Decode(lContent);
                        List<PrintInfo> list = com.alibaba.fastjson.JSONObject.parseArray(lS, PrintInfo.class);
                        deviceManger.printTicks(list);
                    } else {
                        XLog.tag(TAG).i("打印内容为空");
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_DOWNLOAD_DEVICE_LOGS)) {//设备发送日志
                    String logStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("设备发送日志:" + logStr);
                    String LogDate = JSON.parseObject(logStr).getString("logDate");
                    remoteRepository.upLoadLog(LogDate, deviceNO);
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_MESSAGE_PUSH)) {//语音本文推送
                    String voiceStr = SubAndBase64Decode(bodyStr, 0, 6);
                    XLog.tag(TAG).i("本文推送:" + voiceStr);
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_DOWNLOAD_APK)) {//通知下载Apk固件
                    getAppInfo();
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_SYNC_CONFIG_INFO)) {
                    XLog.tag(TAG).i("同步设备配置信息");
                    if (deviceState == DeviceStateConstant.DEVICE_NORMAL) {
                        getDeviceConfig();
                    } else {
                        XLog.tag(TAG).i("设备正在运行,同步设备配置信息失败");
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_OPEN_DOOR)) {
                    XLog.tag(TAG).i("设备取药门开启:" + bodyStr);
                } else if (Arrays.equals(cCode, CMD_CODE_DEVICE_TEMPERATURE_HUMIDITY)) {
                    XLog.tag(TAG).i("温湿度发送结果:" + bodyStr);
                    String lCode = bodyStr.substring(0, 3);
                    if (lCode.equals("200")) {
                        XLog.tag(TAG).i("温湿度发送成功:" + bodyStr);
                    } else {
                        XLog.tag(TAG).i("温湿度发送失败:" + bodyStr);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_CHANGE_SOUND)) {
                    XLog.tag(TAG).i("调整音量:" + bodyStr);
                    String lSoundMessage = SubAndBase64Decode(bodyStr, 0, 6);
                    if (!TextUtils.isEmpty(lSoundMessage)) {
                        int lSoundVolume = JSON.parseObject(lSoundMessage).getIntValue("content");
                        deviceManger.changeVolume(lSoundVolume);
                    }
                } else if (Arrays.equals(cCode, CMD_CODE_TEMPERATURE_HUMIDITY_STATE)) {
                    XLog.tag(TAG).i("温湿度:" + bodyStr);
                } else if (Arrays.equals(cCode, CMD_CODE_UP_DEVICE_MESSAGE)) {
                    XLog.tag(TAG).i("设备信息:" + bodyStr);
                    String lCode = bodyStr.substring(0, 3);
                    if (lCode.equals("200")) {
                        int deviceLen = Integer.parseInt(bodyStr.substring(3, 5));
                        int deviceMessageLen = Integer.parseInt(bodyStr.substring(3 + 2 + deviceLen, 3 + 2 + deviceLen + 3));
                        String lDeviceData = SubAndBase64Decode(bodyStr, 3 + 2 + deviceLen, 3 + 2 + deviceLen + 3);
                        XLog.tag(TAG).i("设备信息扩展信息:" + lDeviceData);
                        JSONObject lJSONObject = JSON.parseObject(lDeviceData);
                        int lSoundSize = lJSONObject.getInteger("soundSize");
                        deviceManger.changeVolume(lSoundSize);
                        String lDeviceMessage = SubAndBase64Decode(bodyStr, 3 + 2 + deviceLen + 3 + deviceMessageLen, deviceMessageLen + 3 + 2 + deviceLen + 3 + 2);
                        XLog.tag(TAG).i("设备信息上传结果:" + lDeviceMessage);
                    }
                }
            }
        });
    }

    public void startLog() {
        deviceManger.initLog();
    }

    private void ADScreenCapture() {
        File file = new File(FilePathConstant.SCREEN_CAPTURE_PATH);
        if (!file.exists()) {
            XLog.tag(TAG).i("截图开始");
            boolean isSuccess = deviceManger.takeScreenshot(FilePathConstant.SCREEN_CAPTURE_PATH);
            if (isSuccess) {
                XLog.tag(TAG).i("截图保存成功");
                remoteRepository.upDataScreenCapture(FilePathConstant.SCREEN_CAPTURE_PATH, deviceNO);
            } else {
                XLog.tag(TAG).i("截图保存失败");
            }
        } else {
            XLog.tag(TAG).i("删除旧图");
            if (file.delete()) {
                XLog.tag(TAG).i("删除旧图后截图");
                ADScreenCapture();
            }
        }
    }

    private void upDataConfig() {
        upDeviceMessage();
        getDeviceExtendedInformation();
        getAppInfo();
        remoteRepository.timingUpLog(deviceNO);
        getDeviceConfig();
        getQR();
        timGetTemperatureAndHumidity();
    }

    private void getQR() {
        remoteRepository.getQRMessage(deviceNO, new Callback.CommonCallback<>() {
            @Override
            public void onSuccess(String result) {
                XLog.tag(TAG).i("二维码信息" + result);
                com.alibaba.fastjson.JSONObject lJSONObject = com.alibaba.fastjson.JSON.parseObject(result);
                int lCode = lJSONObject.getInteger("status");
                String msg = lJSONObject.getString("msg");
                if (lCode == 1 && msg.equals("ok")) {
                    String qRUrl = lJSONObject.getString("url");
                    mQrPath.postValue(qRUrl);
                } else {
                    XLog.tag(TAG).i("获取二维码信息失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("获取二维码信息失败" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getDeviceConfig() {
        remoteRepository.getDeviceConfigS(deviceNO, new Callback.CacheCallback<>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                JSONObject mJson = JSON.parseObject(result);
                int lCode = mJson.getInteger("code");
                if (lCode == 200) {
                    String lData = mJson.getString("data");
                    DeviceBean lDeviceInfo = JSON.parseObject(lData, DeviceBean.class);
                    if (lDeviceInfo != null) {
                        List<Goods> goods = lDeviceInfo.getmList();
                        if (!goods.isEmpty()) {
                            List<Goods> lAllGoods = localRepository.getAllGoods();
                            if (lAllGoods.size() > goods.size()) {
                                localRepository.deleteGoods();
                                XLog.tag(TAG).i("删除本地数据");
                            }
                            int size = goods.size();
                            Goods[] goods1 = goods.toArray(new Goods[size]);
                            localRepository.addGoods(goods1);
                            XLog.tag(TAG).i("更新本地数据信息");
                        } else {
                            XLog.tag(TAG).i("远程数据为空");
                            upDataDeviceGoodsMessage();
                        }
                    }
                } else {
                    XLog.tag(TAG).i("获取的设备配置数据失败：" + result);
                    upDataDeviceGoodsMessage();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("获取的设备配置数据失败错误" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void upDataDeviceGoodsMessage() {
        XLog.tag(TAG).i("上传数据");
        List<Goods> upGoods = localRepository.getAllGoods();
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setDevNo(deviceNO);
        deviceBean.setmList(upGoods);
        String deviceJson = JSON.toJSONString(deviceBean);
        remoteRepository.upDeviceConfigS(deviceJson);
    }

    private void getDeviceExtendedInformation() {
        remoteRepository.getDeviceNewExtendedInformation(deviceNO, new Callback.CacheCallback<>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                XLog.tag(TAG).i("通过后台获取新广告信息:" + result);
                JSONObject lJSONObject = JSON.parseObject(result);
                if (lJSONObject.getInteger("code") == 200) {
                    JSONObject lData = lJSONObject.getJSONObject("data");
                    JSONObject lMoldboard = lData.getJSONObject("info");
                    Integer sProId = lMoldboard.getInteger("id");
                    XLog.tag(TAG).i("远程广告模板id：" + sProId);
                    double sProVersion = lMoldboard.getDouble("version");
                    XLog.tag(TAG).i("远程广告模板版本：" + sProVersion);
                    String sFilePath = lMoldboard.getString("filePath");
                    if (TextUtils.isEmpty(sFilePath) || sFilePath.length() < 5) {
                        XLog.tag(TAG).i("下载广告模板地址异常");
                        return;
                    }
                    String sUrl = NetworkConfiguration.ADVERT_FILE_URL + sFilePath;
                    AdvertMould lAdvertMould = localRepository.getAdvertMould();
                    int lPicId = 0;
                    double lVersion = 0.0;
                    if (lAdvertMould != null) {
                        lPicId = lAdvertMould.getMouldId();
                        lVersion = lAdvertMould.getMouldVersion();
                    }
                    XLog.tag(TAG).i("本地广告模板：" + lPicId + "   本地广告模板版本：" + lVersion);
                    String savePath = FilePathConstant.FILE_PATH;
                    if (sProId != lPicId | sProVersion != lVersion) {
                        XLog.tag(TAG).i("下载不同id或同id不同版广告模板");
                        downLoadAdvertProcedures(lData, sUrl, savePath, sProId, sProVersion);
                    } else {
                        XLog.tag(TAG).i("不需要更新广告模板");
                        upAdvertPicture(lData, savePath);

                    }
                } else {
                    String lMsg = lJSONObject.getString("msg");
                    String lS = SubAndBase64Decode(lMsg);
                    XLog.tag(TAG).i("获取广告信息失败:" + lS);
                    localRepository.updateAndInsertAdvertMould(new AdvertMould(1, 0, 0.0));
                    localRepository.updateAndInsertAdvertContent(new AdvertContent(1, ""));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("新广告信息获取失败：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void downLoadAdvertProcedures(@NonNull JSONObject jsonObject, @NonNull String url, String savePath, int id, double version) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String lFileSavePath = savePath + fileName;
        XLog.tag(TAG).i("广告模板下载地址：" + url);
        remoteRepository.downLoadFile(url, lFileSavePath, new Callback.CommonCallback<>() {
            @Override
            public void onSuccess(@NonNull File result) {
                XLog.tag(TAG).i("广告模板下载成功");
                if (result.exists() && result.length() > 0) {
                    String path = FilePathConstant.FILE_PATH;
                    File file = new File(path);
                    if (file.exists()) {
                        File[] files = file.listFiles();
                        if (files != null) {
                            for (File file1 : files) {
                                Log.i(TAG, "本地文件：" + file1.getName());
                                if (!file1.getName().endsWith(".zip")) {
                                    FileUtil.deleteFileOrDir(file1);
                                }
                            }
                        }
                    }
                    if (ZipUtils.unZipFile(result.getPath(), savePath)) {
                        XLog.tag(TAG).i("广告模板解压成功");
                        localRepository.updateAndInsertAdvertMould(new AdvertMould(1, id, version));
                        upAdvertPicture(jsonObject, savePath);
                    } else {
                        XLog.tag(TAG).i("解压广告模板失败");

                    }
                    FileUtil.deleteFileOrDir(result);
                } else {
                    XLog.tag(TAG).i("广告模板下载失败");
                }
            }

            @Override
            public void onError(@NonNull Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("下载广告模板失败:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void upAdvertPicture(@NonNull JSONObject jsonObject, String savePath) {
        ArrayList<String> bannerImageList = new ArrayList<>();
        String lPicFilePath = savePath + FilePathConstant.BANNER_IMG;
        JSONArray lList = jsonObject.getJSONArray("list");
        if (lList == null || lList.isEmpty()) {
            XLog.tag(TAG).i("远程广告包信息为空");
            return;
        } else {
            for (Object object : lList) {
                List<String> lFileList = ((JSONObject) object).getJSONArray("filePath").toJavaList(String.class);
                bannerImageList.addAll(lFileList);
            }
        }
        ArrayList<String> sBannerImageList = new ArrayList<>(new HashSet<>(bannerImageList));
        AdvertContent lAdvertContent = localRepository.getAdvertContent();
        List<String> lBannerImageList = null;
        if (lAdvertContent != null) {
            String lContent = lAdvertContent.getContent();
            if (lContent != null && !lContent.equals("")) {
                lBannerImageList = JSONArray.parseArray(lContent).toJavaList(String.class);
            }
        }
        ArrayList<String> downLoadUrls = new ArrayList<>();
        ArrayList<String> saveBannerImageList = new ArrayList<>();
        for (String lS : sBannerImageList) {
            String lSubstring = lS.substring(lS.lastIndexOf("/") + 1);
            XLog.tag(TAG).i("远程广告资源名称：" + lSubstring);
            saveBannerImageList.add(lSubstring);
            String url = NetworkConfiguration.ADVERT_FILE_URL + lS;
            if (lBannerImageList != null && !lBannerImageList.isEmpty()) {
                if (!lBannerImageList.contains(lSubstring)) {
                    downLoadUrls.add(url);
                }
            } else {
                downLoadUrls.add(url);
            }
        }
        downLoadAdvertPicture(downLoadUrls, lPicFilePath, saveBannerImageList);
    }

    private void downLoadAdvertPicture(@NonNull List<String> urlList, String savePath, @NonNull List<String> bannerList) {
        if (!urlList.isEmpty()) {
            for (String url : urlList) {
                XLog.tag(TAG).i("广告包下载地址：" + url + " " + urlList.size());
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                String lFileSavePath = savePath + fileName;
                if (url.length() > 5) {
                    remoteRepository.downLoadFile(url, lFileSavePath, new Callback.CommonCallback<>() {
                        @Override
                        public void onSuccess(@NonNull File result) {
                            if (result.exists() && result.length() > 0) {
                                downLoadCount++;
                            } else {
                                XLog.tag(TAG).i("文件无效");
                            }
                            XLog.tag(TAG).i("广告包内容下载成功" + downLoadCount + " 文件名：" + result.getName());
                            if (downLoadCount == urlList.size()) {
                                String bannerListString = JSON.toJSONString(bannerList);
                                XLog.tag(TAG).i("广告信息：" + bannerListString);
                                localRepository.updateAndInsertAdvertContent(new AdvertContent(1, bannerListString));
                                downLoadCount = 0;
                                XLog.tag(TAG).i("广告包全部下载成功");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable ex, boolean isOnCallback) {
                            XLog.tag(TAG).i("下载广告包失败:" + ex.getMessage());
                            ex.printStackTrace();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    XLog.tag(TAG).i("获取新的广告包下载地址为空");
                }
            }
        } else {
            XLog.tag(TAG).i("不需要更新广告包");
        }
    }

    private void getAppInfo() {
        remoteRepository.getNewAppInfo(deviceNO, NetUtils.getMacAddress(), new Callback.CacheCallback<>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                XLog.tag(TAG).i("app更新信息" + result);
                JSONObject lJSONObject = JSON.parseObject(result);
                int lCode = lJSONObject.getInteger("code");
                if (lCode == 200) {
                    JSONObject lData = lJSONObject.getJSONObject("data");
                    String sCode = lData.getString("version");
                    XLog.tag(TAG).i("app远程版本" + sCode);
                    if (!TextUtils.isEmpty(sCode)) {
                        int vCode = AppUtils.getAppVersionCode(context);
                        XLog.tag(TAG).i("app本地版本" + vCode);
                        if (vCode < Double.parseDouble(sCode)) {
                            XLog.tag(TAG).i("更新app");
                            String url = lData.getString("fileName");
                            String filePath = FilePathConstant.APK_PATH + context.getString(R.string.app_name) + "V" + sCode + ".apk";
                            XLog.tag(TAG).i("开始下载apk");
                            FileUtil.deleteFileOrDir(new File(FilePathConstant.APK_PATH));
                            downLoadApk(url, filePath);
                        }
                    }
                } else {
                    XLog.tag(TAG).i("获取app更新信息失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("获取app更新信息失败" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void downLoadApk(String url, String filePath) {
        remoteRepository.downLoadApk(url, filePath, new Callback.CacheCallback<>() {
            @Override
            public boolean onCache(File result) {
                return false;
            }

            @Override
            public void onSuccess(File result) {
                if (result.exists() && result.length() > 0) {
                    XLog.tag(TAG).i("安装文件下载成功");
                    upApp(result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                XLog.tag(TAG).i("下载app失败" + ex.getMessage());
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void upApp(File file) {
        if (deviceState != DeviceStateConstant.DEVICE_NORMAL) {
            XLog.tag(TAG).i("设备正在运行");
            TimerManager.delayedTaskTasksOnSecond(TimerManager.DELAY_UP_APP, new TimerTask() {
                @Override
                public void run() {
                    XLog.tag(TAG).i("延时1分钟执行");
                    upApp(file);
                }
            });
        } else {
            XLog.tag(TAG).i("更新软件");
            boolean b = deviceManger.installApk(file.getPath());
            XLog.tag(TAG).i("更新软件是否成功：" + b);
        }
    }

    private void upDeviceMessage() {
        JSONObject lJSONObject = new JSONObject();
        lJSONObject.put("netType", 1);
        lJSONObject.put("cmCard", NetUtils.getSIMCardNumber(context));
        XLog.tag(TAG).i("上传信息：" + lJSONObject.toJSONString());
        String lBase64 = BytesUtils.getBase64(lJSONObject.toJSONString());
        remoteRepository.upDeviceMessage(lBase64);
    }

    public void outGoods(String outCode) {
        mDeviceState.postValue(DeviceStateConstant.DEVICE_PROCESSING);
        remoteRepository.getOutShoppingGoodsInfo(outCode);
    }

    public void shoppingGoods(String orderNO, List<ShoppingGoods> shoppingGoodsList, List<ResultShoppingGoods> resultShoppingGoods) {
        deviceState = DeviceStateConstant.DEVICE_OUTING_GOODS;
        mDeviceState.postValue(DeviceStateConstant.DEVICE_OUTING_GOODS);
        int size = shoppingGoodsList.size();
        XLog.tag(TAG).i("出货数据：" + shoppingGoodsList);
        ShoppingGoods shoppingGoods = shoppingGoodsList.get(outNo);
        isOutGoods = true;
        deviceManger.addDeviceDataListener(new OnDataListener() {
            @Override
            public void onDataSend(byte[] bytes) {
                super.onDataSend(bytes);
                if (isOutGoods) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            XLog.tag(TAG).i("串口通讯超时");
                            remoteRepository.sendErrorCode(77, shoppingGoodsList.get(outNo).getLine(), shoppingGoodsList.get(outNo).getColNo(), (System.currentTimeMillis() / 1000));
                            orderState = 1;
                            for (ResultShoppingGoods resultShoppingGood : resultShoppingGoods) {
                                if (resultShoppingGood.getSuccessCount() == 0) {
                                    resultShoppingGood.setFailCount(resultShoppingGood.getFailCount() + 1);
                                }
                            }
                            String shoppingResult = JSONArray.toJSONString(resultShoppingGoods);
                            XLog.tag(TAG).i("出货完成结果：" + shoppingResult);
                            remoteRepository.sendGoodsShoppingResult(orderNO, String.valueOf(orderState), shoppingResult, (System.currentTimeMillis() / 1000));
                            if (outNo == 0) {
                                mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_FAIL);
                            } else {
                                mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_PART_FAIL);
                                getPackage();
                            }
                            failCount = 0;
                            outNo = 0;
                            orderState = 0;
                            isOutGoods = false;
                            remoteRepository.sendErrorCode(0, 0, 0, (System.currentTimeMillis() / 1000));
                            deviceState = DeviceStateConstant.DEVICE_NORMAL;
                        }
                    }, 50000);
                }
            }

            @Override
            public void onDataReceived(byte[] bytes) {
                super.onDataReceived(bytes);
                String resultMessage = new String(bytes, StandardCharsets.US_ASCII);
                XLog.tag(TAG).i("出货返回的数据：" + resultMessage);
                if (isOutGoods) {
                    mTimer.cancel();
                    if (resultMessage.startsWith("res:")) {
                        String result = resultMessage.replace("res:", "").replace("\r\n", "");
                        XLog.tag(TAG).i("返回数据是：" + result + "结果");
                        int resultCode = Integer.parseInt(result);
                        if (resultCode == 0) {
                            int line = shoppingGoodsList.get(outNo).getLine();
                            int colNo = shoppingGoodsList.get(outNo).getColNo();
                            for (ResultShoppingGoods resultShoppingGood : resultShoppingGoods) {
                                if (resultShoppingGood.getLine() == line && resultShoppingGood.getColNo() == colNo) {
                                    resultShoppingGood.setSuccessCount(resultShoppingGood.getSuccessCount() + 1);
                                }
                            }
                        } else {
                            failCount += 1;
                            orderState = 1;
                            int line = shoppingGoodsList.get(outNo).getLine();
                            int colNo = shoppingGoodsList.get(outNo).getColNo();
                            for (ResultShoppingGoods resultShoppingGood : resultShoppingGoods) {
                                if (resultShoppingGood.getLine() == line && resultShoppingGood.getColNo() == colNo) {
                                    resultShoppingGood.setFailCount(resultShoppingGood.getFailCount() + 1);
                                }
                            }
                            remoteRepository.sendErrorCode(resultCode, shoppingGoodsList.get(outNo).getLine(), shoppingGoodsList.get(outNo).getColNo(), (System.currentTimeMillis() / 1000));
                        }
                        if (outNo == size - 1) {
                            String shoppingResult = JSONArray.toJSONString(resultShoppingGoods);
                            XLog.tag(TAG).i("出货完成结果：" + shoppingResult);
                            remoteRepository.sendGoodsShoppingResult(orderNO, String.valueOf(orderState), shoppingResult, (System.currentTimeMillis() / 1000));
                            outNo = 0;
                            orderState = 0;
                            isOutGoods = false;
                            if (failCount == 0) {
                                mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_SUCCESSFUL);
                                getPackage();
                            } else if (failCount == size) {
                                mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_FAIL);
                            } else {
                                mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_PART_FAIL);
                                getPackage();
                            }
                            failCount = 0;
                            TimerManager.delayedTaskTasksOnSecond(1000, new TimerTask() {
                                @Override
                                public void run() {
                                    remoteRepository.sendErrorCode(0, 0, 0, (System.currentTimeMillis() / 1000));
                                }
                            });
                            deviceState = DeviceStateConstant.DEVICE_NORMAL;
                        } else {
                            outNo += 1;
                            ShoppingGoods shoppingGoods = shoppingGoodsList.get(outNo);
                            double icc = shoppingGoods.getiCC();
                            double irc = shoppingGoods.getiRC();
                            XLog.tag(TAG).i("再次出货");
                            deviceManger.shoppingGoods(irc, icc, 0);
                        }
                    }
                } else if (!isOutPackage) {
                    if (resultMessage.startsWith("res:")) {
                        String result = resultMessage.replace("res:", "").replace("\r\n", "");
                        XLog.tag(TAG).i("返回数据是：" + result + "结果");
                        int resultCode = Integer.parseInt(result);
                        if (resultCode == 0) {
                            mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_SUCCESSFUL);
                        } else {
                            mDeviceState.postValue(DeviceStateConstant.DEVICE_OUT_GOODS_FAIL);
                        }
                        deviceState = DeviceStateConstant.DEVICE_NORMAL;
                    }
                }
            }
        });
        double icc = shoppingGoods.getiCC();
        double irc = shoppingGoods.getiRC();
        XLog.tag(TAG).i("开始出货");
        deviceManger.shoppingGoods(irc, icc, 0);
    }

    private void getPackage() {
        deviceManger.addDeviceDataListener(new OnDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                super.onDataReceived(bytes);
                if (isOutPackage) {
                    String resultMessage = new String(bytes, StandardCharsets.US_ASCII);
                    XLog.tag(TAG).i("取包装袋返回的数据：" + resultMessage);
                    if (resultMessage.startsWith("res:")) {
                        String result = resultMessage.replace("res:", "").replace("\r\n", "");
                        int resultCode = Integer.parseInt(result);
                        if (resultCode == 0) {
                            XLog.tag(TAG).i("取包装袋成功");
                        } else {
                            XLog.tag(TAG).i("取包装袋失败");
                            if (packageNo != packageCount) {
                                packageNo += 1;
                                deviceManger.shoppingGoods(61, 123, 5);
                            } else {
                                packageNo = 1;
                                isOutPackage = false;
                            }
                        }
                    }
                }
            }
        });
        isOutPackage = true;
        deviceManger.shoppingGoods(39, 123, 5);
    }

    public void timGetTemperatureAndHumidity() {
        TimerManager.scheduledAndDelayTasksOnSecond(TimerManager.TIMEOUT_DETECTION_TIME, TimerManager.TEMPERATURE_HUMIDITY_TIME, new TimerTask() {
            @Override
            public void run() {
                if (deviceState == DeviceStateConstant.DEVICE_NORMAL) {
                    deviceManger.addDeviceDataListener(new OnDataListener() {
                        @Override
                        public void onDataReceived(byte[] bytes) {
                            super.onDataReceived(bytes);
                            String resultMessage = new String(bytes, StandardCharsets.US_ASCII);
                            XLog.tag(TAG).i("温湿度返回的数据：" + resultMessage);
                            if (resultMessage.startsWith("temp:")) {
                                String[] tempAndHum = resultMessage.replace("temp:", "").replace("\r\n", "").split(",");
                                XLog.tag(TAG).i("温度：" + tempAndHum[0] + " " + "湿度：" + tempAndHum[1]);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("temp", tempAndHum[0]);
                                jsonObject.put("hum", tempAndHum[1]);
                                String tempAndHumJson = jsonObject.toJSONString();
                                mTempAndHumValue.postValue(tempAndHumJson);
                                remoteRepository.upTemperatureAndHumidity(tempAndHum[0], tempAndHum[1]);
                            }
                        }
                    });
                    deviceManger.getTemperatureAndHumidity();
                }
            }
        });
    }

    public void timGetTemperatureAndHumidityByReturn() {
        TimerManager.scheduledAndDelayTasksOnSecond(TimerManager.TIMEOUT_DETECTION_TIME, TimerManager.TEMPERATURE_HUMIDITY_TIME, new TimerTask() {
            @Override
            public void run() {
                if (deviceState == DeviceStateConstant.DEVICE_NORMAL) {
                    deviceManger.getTemperatureAndHumidityByReturn(new OnReportListener() {
                        @Override
                        public void onSuccess(byte[] bytes, int flag) {
                            super.onSuccess(bytes, flag);
                            String resultMessage = new String(bytes, StandardCharsets.US_ASCII);
                            XLog.tag(TAG).i("温湿度返回的数据：" + resultMessage);
                            if (resultMessage.startsWith("temp:")) {
                                String[] tempAndHum = resultMessage.replace("temp:", "").replace("\r\n", "").split(",");
                                XLog.tag(TAG).i("温度：" + tempAndHum[0] + " " + "湿度：" + tempAndHum[1]);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("temp", tempAndHum[0]);
                                jsonObject.put("hum", tempAndHum[1]);
                                String tempAndHumJson = jsonObject.toJSONString();
                                mTempAndHumValue.postValue(tempAndHumJson);
                                remoteRepository.upTemperatureAndHumidity(tempAndHum[0], tempAndHum[1]);
                            }
                        }

                        @Override
                        public void onFailure(SerialPortError error, int flag) {
                            super.onFailure(error, flag);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                        }
                    });
                }
            }
        });
    }

    public void closeConnect() {
        remoteRepository.closeConnect();
        deviceManger.closeDevice();
    }
}