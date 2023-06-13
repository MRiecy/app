package com.jianjia.medicinevendingmachine.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.R;
import com.jianjia.medicinevendingmachine.constants.DeviceStateConstant;
import com.jianjia.medicinevendingmachine.constants.FilePathConstant;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertContent;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertMould;
import com.jianjia.medicinevendingmachine.databinding.ActivityMainBinding;
import com.jianjia.medicinevendingmachine.utils.AppUtils;
import com.jianjia.medicinevendingmachine.viewMoel.MainViewModel;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private MainViewModel mainViewModel;
    private ActivityMainBinding viewBind;
    private Timer mTimer;

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
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        viewBind.webAdvert.setLayerType(ViewGroup.LAYER_TYPE_HARDWARE, null);
        viewBind.webAdvert.setWebViewClient(new MyWebViewClient());
        viewBind.llTips.setVisibility(View.GONE);
        viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
        viewBind.btGoodsOut.setOnClickListener(v -> {
            viewBind.webAdvert.setVisibility(View.GONE);
            viewBind.webAdvert.pauseTimers();
            viewBind.inKeyboard.glKeyboard.setVisibility(View.VISIBLE);
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
                        viewBind.webAdvert.resumeTimers();
                        viewBind.webAdvert.setVisibility(View.VISIBLE);
                        viewBind.inKeyboard.tvInput.setText("");
                        viewBind.btGoodsOut.setClickable(true);
                    });
                }
            }, 15000);
        });
        addKeyBoardClickListener();

    }

    private void addKeyBoardClickListener() {
        viewBind.inKeyboard.btOne.setOnClickListener(this);
        viewBind.inKeyboard.btTwo.setOnClickListener(this);
        viewBind.inKeyboard.btThree.setOnClickListener(this);
        viewBind.inKeyboard.btFour.setOnClickListener(this);
        viewBind.inKeyboard.btFive.setOnClickListener(this);
        viewBind.inKeyboard.btSix.setOnClickListener(this);
        viewBind.inKeyboard.btSeven.setOnClickListener(this);
        viewBind.inKeyboard.btEight.setOnClickListener(this);
        viewBind.inKeyboard.btNine.setOnClickListener(this);
        viewBind.inKeyboard.btZero.setOnClickListener(this);
        viewBind.inKeyboard.btDelete.setOnClickListener(this);
        viewBind.inKeyboard.btConfirm.setOnClickListener(this);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_one:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "1");
                break;
            case R.id.bt_two:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "2");
                break;
            case R.id.bt_three:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "3");
                break;
            case R.id.bt_four:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "4");
                break;
            case R.id.bt_five:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "5");
                break;
            case R.id.bt_six:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "6");
                break;
            case R.id.bt_seven:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "7");
                break;
            case R.id.bt_eight:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "8");
                break;
            case R.id.bt_nine:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "9");
                break;
            case R.id.bt_zero:
                viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "0");
                break;
            case R.id.bt_delete:
                String pickCode = viewBind.inKeyboard.tvInput.getText().toString();
                if (pickCode.length() > 0) {
                    viewBind.inKeyboard.tvInput.setText(pickCode.substring(0, pickCode.length() - 1));
                }
                break;
            case R.id.bt_confirm:
                viewBind.btGoodsOut.setClickable(false);
                String outGoodsCode = viewBind.inKeyboard.tvInput.getText().toString();
                XLog.tag(TAG).i("取货码是：" + outGoodsCode);
                if (outGoodsCode.length() == 6) {
                    viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
                    viewBind.llTips.setVisibility(View.VISIBLE);
                    mainViewModel.outGoods(outGoodsCode);
                    viewBind.inKeyboard.tvInput.setText("");
                    mTimer.cancel();
                }
                break;
        }
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
        mainViewModel.getDeviceNoValue().observe(this, s -> {
            String appInfo = "No." + s + " Ver." + AppUtils.getAppVersionName(MainActivity.this);
            viewBind.tvDeviceInfo.setText(appInfo);
        });

        mainViewModel.getTempAndHumValue().observe(this, s -> {
            JSONObject jsonObject = (JSONObject) JSON.parse(s);
            String temp = (String) jsonObject.get("temp");
            String hum = (String) jsonObject.get("hum");
            String tempAndHum = "温度：" + temp + "°C" + " 湿度：" + hum + "%RH";
            viewBind.tvTemHum.setText(tempAndHum);
        });

        mainViewModel.getDeviceState().observe(this, integer -> {
            XLog.tag(TAG).i("android调用了js的togglePage方法：" + integer);
            changPage(integer);
        });

        mainViewModel.getAdvertMould().observe(this, advertMould -> {
            Log.i(TAG, "更新模板");
            loadWeb();
        });

        mainViewModel.getQrPath().observe(this, s -> {
            XLog.tag(TAG).i("二维码地址：" + s);
            if (s != null && !s.equals("")) {
                Glide.with(MainActivity.this)
                        .load(s)
                        .override(240, 240)
                        .into(viewBind.ivQr);
                viewBind.tvTic.setVisibility(View.VISIBLE);
            } else {
                XLog.tag(TAG).i("二维码信息为空");
            }
        });
    }

    public void changPage(int deviceState) {
        String tips = null;
        int tipsLogo = -1;
        if (deviceState == DeviceStateConstant.DEVICE_PROCESSING) {
            tips = "正在处理中,请不要走开";
            tipsLogo = R.mipmap.loading;
        } else if (deviceState == DeviceStateConstant.DEVICE_NO_ORDER) {
            tips = "订单不存在";
            tipsLogo = R.mipmap.fail;
        } else if (deviceState == DeviceStateConstant.DEVICE_ORDER_ERROR) {
            tips = "订单错误";
            tipsLogo = R.mipmap.fail;
        } else if (deviceState == DeviceStateConstant.DEVICE_OUTING_GOODS) {
            tips = "出货中,请不要走开";
            tipsLogo = R.mipmap.outting_goods;
        } else if (deviceState == DeviceStateConstant.DEVICE_OUT_GOODS_FAIL) {
            tips = "出货失败,请联系客服处理";
            tipsLogo = R.mipmap.fail;
        } else if (deviceState == DeviceStateConstant.DEVICE_OUT_GOODS_PART_FAIL) {
            tips = "出货部分失败,请联系客服处理";
            tipsLogo = R.mipmap.fail;
        } else if (deviceState == DeviceStateConstant.DEVICE_OUT_GOODS_SUCCESSFUL) {
            tips = "出货完成，请及时拿走您的商品";
            tipsLogo = R.mipmap.successful;
        }
        if (tips != null) {
            viewBind.tvTips.setText(tips);
        }
        if (tipsLogo != -1) {
            viewBind.ivTipsLogo.setImageResource(tipsLogo);
        }
        switch (deviceState) {
            case DeviceStateConstant.DEVICE_NO_ORDER:
            case DeviceStateConstant.DEVICE_ORDER_ERROR:
            case DeviceStateConstant.DEVICE_OUT_GOODS_FAIL:
            case DeviceStateConstant.DEVICE_OUT_GOODS_PART_FAIL:
            case DeviceStateConstant.DEVICE_OUT_GOODS_SUCCESSFUL:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            viewBind.llTips.setVisibility(View.GONE);
                            viewBind.webAdvert.resumeTimers();
                            viewBind.webAdvert.setVisibility(View.VISIBLE);
                            viewBind.btGoodsOut.setClickable(true);
                        });
                    }
                }, 15000);
        }
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
            Configuration newConfig = getResources().getConfiguration();
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH_LAND);
                //横屏
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH_PORT);
            }
        } else {
            Log.i(TAG, "加载sdk网页");
            AdvertContent lAdvertContent = mainViewModel.getAdvertContent();
            if (lAdvertContent == null || lAdvertContent.getContent() == null || lAdvertContent.getContent().equals("")) {
                XLog.tag(TAG).i("本地广告内容为空");
            } else {
                List<String> lFileList = JSONArray.parseArray(lAdvertContent.getContent(), String.class);
                String bannerList = String.join(",", lFileList);
                XLog.tag(TAG).i("加载的广告包内容为：" + bannerList);
                viewBind.webAdvert.loadUrl("file://" + FilePathConstant.FILE_PATH + "index.html" + "?BannerList=" + bannerList);
                new Thread(() -> deleteOldFile(bannerList)).start();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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


    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(@NonNull WebView view, String url) {
            Log.i(TAG, "加载地址:" + url);
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
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
            XLog.tag(TAG).i("web " + error.getDescription() + "");
        }

        @Override
        public void onReceivedError(@NonNull WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            XLog.tag(TAG).i("web " + description);
        }
    }
}
