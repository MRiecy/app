package com.jianjia.medicinevendingmachine.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.FilePathConstant;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertContent;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertMould;
import com.jianjia.medicinevendingmachine.databinding.ActivityMainBinding;
import com.jianjia.medicinevendingmachine.utils.AppUtils;
import com.jianjia.medicinevendingmachine.viewMoel.MainViewModel;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private MainViewModel mainViewModel;
    private ActivityMainBinding viewBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initView();
        requestPermission();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        WebSettings webSettings = viewBind.webAdvert.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        viewBind.webAdvert.setLayerType(ViewGroup.LAYER_TYPE_HARDWARE, null);
        viewBind.webAdvert.setWebViewClient(new MyWebViewClient());
        viewBind.webAdvert.addJavascriptInterface(new JavaObject(), "JavaObject");
    }

    private void init() {
        mainViewModel.init(this);
        addDataObserver();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 0);
        } else {
            Log.i(TAG, "已经获取权限初始化设备");
            init();
        }
    }

    private void addDataObserver() {
        mainViewModel.getDeviceNoValue().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String appInfo = "No." + s + " Ver." + AppUtils.getAppVersionName(MainActivity.this);
                viewBind.tvAppInfo.setText(appInfo);
            }
        });

        mainViewModel.getTempAndHumValue().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                JSONObject jsonObject = (JSONObject) JSON.parse(s);
                String temp = (String) jsonObject.get("temp");
                String hum = (String) jsonObject.get("hum");
                String tempAndHum = "温度：" + temp + "°C" + " 湿度：" + hum + "%RH";
                viewBind.tvTemHum.setText(tempAndHum);
            }
        });

        mainViewModel.getDeviceState().observe(this, integer -> {
            XLog.tag(TAG).i("android调用了js的togglePage方法：" + integer);
            viewBind.webAdvert.evaluateJavascript("javascript:togglePage(" + integer + ")",
                    value -> XLog.tag(TAG).i("android调用了js的togglePage方法返回：" + value));
        });

        mainViewModel.getAdvertMould().observe(this, advertMould -> {
            Log.i(TAG, "更新模板");
            loadWeb();
        });

        mainViewModel.getQrPath().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setQR();
            }
        });
    }

    private void setQR() {
        String path = mainViewModel.getQrPath().getValue();
        Log.i(TAG, "设置二维码:" + path);
        if (path != null && !path.equals("")) {
            JSONObject lJSONObject = new JSONObject();
            lJSONObject.put("qrCode", path);
            lJSONObject.put("mobile", "");
            String json = lJSONObject.toJSONString();
            XLog.tag(TAG).i("android调用了js的sendContent方法：" + json);
            viewBind.webAdvert.evaluateJavascript("javascript:sendContent(" + json + ")",
                    value -> XLog.tag("webView").i("android调用了js的sendContent方法返回：" + value));
        }
    }

    private void setAdvertContent() {
        AdvertContent lAdvertContent = mainViewModel.getAdvertContent();
        if (lAdvertContent == null) {
            XLog.tag(TAG).i("本地广告内容为空");
            return;
        }
        String lContent = lAdvertContent.getContent();
        Log.i(TAG, "加载广告包：" + lContent);
        String lS = "";
        if (lContent != null && !lContent.equals("")) {
            JSONArray advertJson = new JSONArray();
            JSONArray lObjects = JSONArray.parseArray(lContent);
            for (Object lObject : lObjects) {
                String banner = (String) lObject;
                JSONObject lJSONObject = new JSONObject();
                if (banner.toUpperCase(Locale.ROOT).endsWith("MP4")) {
                    lJSONObject.put("type", 2);
                } else {
                    lJSONObject.put("type", 1);
                }
                lJSONObject.put("url", banner);
                advertJson.add(lJSONObject);
            }
            lS = JSON.toJSONString(advertJson);
        }
        viewBind.webAdvert.evaluateJavascript("javascript:setSwiper(" + lS + ")", value ->
                Log.i(TAG, "android调用了js的setSwiper方法返回：" + value));
        new Thread(() -> deleteOldFile(lContent)).start();
    }

    private void deleteOldFile(String bannerList) {
        if (bannerList != null) {
            Log.i(TAG, "轮播图文件名：" + bannerList);
            String path = FilePathConstant.FILE_PATH + FilePathConstant.BANNER_IMG;
            File file = new File(path);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File file1 : files) {
                        if (!bannerList.contains(file1.getName())) {
                            FileUtil.deleteFileOrDir(file1);
                        }
                    }
                }
            }
        }
    }

    public void loadWeb() {
        Log.i(TAG, "加载网页");
        AdvertMould lAdvertMould = mainViewModel.getAdvertMouldNoLive();
        if (lAdvertMould == null || lAdvertMould.getMouldId() == 0 || lAdvertMould.getMouldVersion() == 0.0) {
            Log.i(TAG, "加载默认网页");
            viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH);
        } else {
            Log.i(TAG, "加载sdk网页");
            viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH);
            //   viewBind.webAdvert.loadUrl("file://" + FilePathConstant.FILE_PATH + "index.html");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "获取权限后初始化设备" + FilePathConstant.LOG_FILE_PATH);
                init();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class JavaObject {
        public JavaObject() {
        }

        @JavascriptInterface
        public void outGoodsCode(String message) {
            XLog.tag(TAG).i("js调Android指令：" + message);
            mainViewModel.outGoods(message);
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            if (!url.startsWith("https") || !url.startsWith("http")) {
                return false;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(@NonNull WebView view, String url) {
            Log.i(TAG, "加载地址:" + url);
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            setQR();
            setAdvertContent();
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //  XLog.tag(TAG).i("MyWebViewClient" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            viewBind.webAdvert.clearHistory();
        }

        @SuppressLint("NewApi")
        @Override
        public void onReceivedError(@NonNull WebView view, WebResourceRequest request, @NonNull WebResourceError error) {
            super.onReceivedError(view, request, error);
            //  XLog.tag(TAG).i("加载大屏默认广告");
            XLog.tag(TAG).i("web " + error.getDescription() + "");
        }

        @Override
        public void onReceivedError(@NonNull WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //     XLog.tag(TAG).i("加载大屏默认广告");
            XLog.tag(TAG).i("web " + description);
        }
    }
}
