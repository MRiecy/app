package com.jianjia.medicinevendingmachine.dataStore.remoterepository;

import androidx.annotation.NonNull;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.NetworkConfiguration;

import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import javax.inject.Inject;

public class HttpUtils {
    private final String TAG = "HttpUtils";
    private Callback.Cancelable cancelable;

    @Inject
    public HttpUtils() {
    }

    public void upLoadFile(String deviceNo, String date, @NonNull String filePath, Callback.CommonCallback<String> callback) {
        File lFile = new File(filePath);
        if (!lFile.exists()) {
            XLog.tag(TAG).i("upLoadFile文件不存在");
        } else {
            RequestParams requestParams = new RequestParams(NetworkConfiguration.UP_FILE_URL);
            requestParams.setUseCookie(false);
            requestParams.setAutoResume(true);
            requestParams.addQueryStringParameter("deviceCode", deviceNo);
            requestParams.addQueryStringParameter("fileName", date);
            requestParams.setAutoRename(true);
            requestParams.setMultipart(true);
            requestParams.setMaxRetryCount(5);
            requestParams.addBodyParameter("file", lFile, "multipart/form-data");
            cancelable = x.http().post(requestParams, callback);
        }
    }

    public void upLoadPicture(String deviceNo, @NonNull String filePath, Callback.CommonCallback<String> callback) {
        File lFile = new File(filePath);
        if (!lFile.exists()) {
            XLog.tag(TAG).i("upLoadPicture文件不存在");
        } else {
            String lFileName = lFile.getName().replace(".jpg", "");
            RequestParams requestParams = new RequestParams(NetworkConfiguration.UP_PICTURE_URL);
            requestParams.setHeader("content-type", "multipart/form-data");
            requestParams.setUseCookie(false);
            requestParams.setAutoResume(true);
            requestParams.setMultipart(true);
            requestParams.addQueryStringParameter("deviceCode", deviceNo);
            requestParams.addQueryStringParameter("fileName", lFileName);
            requestParams.addQueryStringParameter("capType", "gradevin");
            requestParams.setMaxRetryCount(5);
            requestParams.addBodyParameter("file", lFile);
            cancelable = x.http().post(requestParams, callback);
        }
    }

    public void downLoadFile(@NonNull String url, @NonNull String savePath, @NonNull Callback.CommonCallback<File> callBack) {
        if (!FileUtil.existsSdcard()) {
            XLog.tag(TAG).i("downLoadFile存储设备异常");
        } else {
            RequestParams requestParams = new RequestParams(url);
            requestParams.setAutoResume(true);
            requestParams.setAutoRename(true);
            requestParams.setMaxRetryCount(5);
            requestParams.setSaveFilePath(savePath);
            cancelable = x.http().get(requestParams, callBack);
        }
    }

    public void getDeviceConfigInfo(String deviceNo, Callback.CommonCallback<String> callback) {
        RequestParams requestParams = new RequestParams(NetworkConfiguration.LOAD_INFO_URL);
        requestParams.addQueryStringParameter("deviceCode", deviceNo);
        requestParams.setAsJsonContent(true);
        requestParams.setMaxRetryCount(5);
        cancelable = x.http().get(requestParams, callback);
    }

    public void getDeviceNewExtendedInformation(String deviceNo, String mac, Callback.CommonCallback<String> callback) {
        RequestParams requestParams = new RequestParams(NetworkConfiguration.GET_DEVICE_NEW_EXTENDED_INFORMATION_URL);
        requestParams.addQueryStringParameter("deviceCode", deviceNo);
        requestParams.addQueryStringParameter("macAddress", mac);
        requestParams.setAsJsonContent(true);
        requestParams.setMaxRetryCount(5);
        cancelable = x.http().get(requestParams, callback);
    }

    public void getAppDownloadInfo(String deviceNo, String mac, Callback.CommonCallback<String> callback) {
        RequestParams requestParams = new RequestParams(NetworkConfiguration.GET_DEVICE_NewAPP_INFO_URL);
        requestParams.addQueryStringParameter("deviceCode", deviceNo);
        requestParams.addQueryStringParameter("macAddress", mac);
        requestParams.setAsJsonContent(true);
        requestParams.setMaxRetryCount(5);
        cancelable = x.http().get(requestParams, callback);
    }

    public void UpDeviceInfo(String jsonString, Callback.CommonCallback<String> callBack) {
        RequestParams requestParams = new RequestParams(NetworkConfiguration.UP_DATA_INFO_URL);
        requestParams.setAsJsonContent(true);
        requestParams.setMaxRetryCount(5);
        requestParams.setBodyContent(jsonString);
        cancelable = x.http().post(requestParams, callBack);
    }

    public void getQRInfo(String deviceNo, Callback.CommonCallback<String> callback) {
        RequestParams requestParams = new RequestParams(NetworkConfiguration.QR_CODE_GM_URL);
        requestParams.addQueryStringParameter("ma", deviceNo);
        requestParams.setAsJsonContent(true);
        requestParams.setMaxRetryCount(5);
        cancelable = x.http().get(requestParams, callback);
    }

    public void cancel() {
        if (cancelable != null) {
            cancelable.cancel();
        }
    }
}