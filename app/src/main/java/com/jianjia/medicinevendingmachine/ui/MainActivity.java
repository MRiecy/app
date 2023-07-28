package com.jianjia.medicinevendingmachine.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
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
import com.jianjia.medicinevendingmachine.utils.NetUtils;
import com.jianjia.medicinevendingmachine.utils.ThreadPoolUtils;
import com.jianjia.medicinevendingmachine.viewMoel.MainViewModel;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private MainViewModel mainViewModel;
    private ActivityMainBinding viewBind;
    private CountDownTimer mTimer;
    private boolean mIsFirst = true;

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
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        viewBind.webAdvert.setLayerType(ViewGroup.LAYER_TYPE_HARDWARE, null);
        viewBind.webAdvert.setWebViewClient(new MyWebViewClient());
        viewBind.btGoodsOut.setOnClickListener(this::onClick2);
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
        viewBind.inKeyboard.tvBack.setOnClickListener(this);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_one) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "1");
        } else if (v.getId() == R.id.bt_two) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "2");
        } else if (v.getId() == R.id.bt_three) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "3");
        } else if (v.getId() == R.id.bt_four) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "4");
        } else if (v.getId() == R.id.bt_five) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "5");
        } else if (v.getId() == R.id.bt_six) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "6");
        } else if (v.getId() == R.id.bt_seven) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "7");
        } else if (v.getId() == R.id.bt_eight) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "8");
        } else if (v.getId() == R.id.bt_nine) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "9");
        } else if (v.getId() == R.id.bt_zero) {
            viewBind.inKeyboard.tvInput.setText(viewBind.inKeyboard.tvInput.getText() + "0");
        } else if (v.getId() == R.id.bt_delete) {
            String pickCode = viewBind.inKeyboard.tvInput.getText().toString();
            if (pickCode.length() > 0) {
                viewBind.inKeyboard.tvInput.setText(pickCode.substring(0, pickCode.length() - 1));
            }
        } else if (v.getId() == R.id.bt_confirm) {
            String outGoodsCode = viewBind.inKeyboard.tvInput.getText().toString();
            XLog.tag(TAG).i("取货码是：" + outGoodsCode);
            if (outGoodsCode.length() == 6) {
                mTimer.cancel();
                viewBind.inKeyboard.tvInput.setText("");
                viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
                mainViewModel.outGoods(outGoodsCode);
            }
        } else if (v.getId() == R.id.tv_back) {
            mTimer.cancel();
            viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
            viewBind.inKeyboard.tvInput.setText("");
            viewBind.webAdvert.resumeTimers();
            viewBind.webAdvert.setVisibility(View.VISIBLE);
            viewBind.btGoodsOut.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        addDataObserver();
        mainViewModel.initRepositoryData();
        addNetworkMonitor();
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
            String appInfo = "No." + s + " Ver." + AppUtils.getAppVersionName(this);
            viewBind.tvDeviceInfo.setText(appInfo);
        });

        mainViewModel.getTempAndHumValue().observe(this, s -> {
            JSONObject jsonObject = (JSONObject) JSON.parse(s);
            String temp = (String) jsonObject.get("temp");
            String hum = (String) jsonObject.get("hum");
            String tempAndHum = "温度:" + temp + "°C" + "    湿度:" + hum + "%RH";
            viewBind.tvTemHum.setText(tempAndHum);
        });

        mainViewModel.getDeviceState().observe(this, integer -> {
            XLog.tag(TAG).i("设备状态：" + integer);
            changPage(integer);
        });

        mainViewModel.getAdvertMouldByLiveData().observe(this, advertMould -> {
            Log.i(TAG, "更新模板");
            loadWeb(advertMould);
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
        switch (deviceState) {
            case DeviceStateConstant.DEVICE_PROCESSING:
                tips = "正在处理中,请不要走开";
                tipsLogo = R.mipmap.loading;
                break;
            case DeviceStateConstant.DEVICE_NO_NET:
                tips = "无网络";
                tipsLogo = R.drawable.no_net;
                break;
            case DeviceStateConstant.DEVICE_NO_ORDER:
                tips = "订单不存在";
                tipsLogo = R.mipmap.fail;
                break;
            case DeviceStateConstant.DEVICE_ORDER_ERROR:
                tips = "订单错误";
                tipsLogo = R.mipmap.fail;
                break;
            case DeviceStateConstant.DEVICE_ORDER_GET_TIME_OUT:
                tips = "订单获取失败";
                tipsLogo = R.mipmap.fail;
                break;
            case DeviceStateConstant.DEVICE_OUTING_GOODS:
                tips = "出货中,请不要走开";
                tipsLogo = R.mipmap.outting_goods;
                break;
            case DeviceStateConstant.DEVICE_OUT_GOODS_FAIL:
                tips = "出货失败,请联系客服处理";
                tipsLogo = R.mipmap.out_goods_fail;
                break;
            case DeviceStateConstant.DEVICE_OUT_GOODS_PART_FAIL:
                tips = "出货部分失败,请联系客服处理";
                tipsLogo = R.mipmap.out_goods_fail;
                break;
            case DeviceStateConstant.DEVICE_OUT_GOODS_SUCCESSFUL:
                tips = "出货完成，请及时拿走您的商品";
                tipsLogo = R.mipmap.successful;
                break;
            case DeviceStateConstant.DEVICE_UNREGISTERED:
                tips = "设备未注册,请注册,本设备mac为:\n" + NetUtils.getMacAddress();
                tipsLogo = R.mipmap.fail;
                break;
            case DeviceStateConstant.DEVICE_REGISTRATION_FAILED:
                tips = "设备绑定失败";
                tipsLogo = R.mipmap.fail;
                break;
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
            case DeviceStateConstant.DEVICE_ORDER_GET_TIME_OUT:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ThreadPoolUtils.getInstance().doThings(() -> {
                            if (NetUtils.ping()) {
                                runOnUiThread(() -> {
                                    viewBind.llTips.setVisibility(View.GONE);
                                    viewBind.webAdvert.resumeTimers();
                                    viewBind.webAdvert.setVisibility(View.VISIBLE);
                                    viewBind.btGoodsOut.setVisibility(View.VISIBLE);
                                });
                            } else {
                                runOnUiThread(() -> changPage(DeviceStateConstant.DEVICE_NO_NET));
                            }
                        });
                    }
                }, 10000);
                break;
            case DeviceStateConstant.DEVICE_NO_NET:
            case DeviceStateConstant.DEVICE_REGISTRATION_FAILED:
            case DeviceStateConstant.DEVICE_UNREGISTERED:
            case DeviceStateConstant.DEVICE_PROCESSING:
            case DeviceStateConstant.DEVICE_OUTING_GOODS:
                viewBind.btGoodsOut.setVisibility(View.GONE);
                viewBind.webAdvert.pauseTimers();
                viewBind.webAdvert.setVisibility(View.GONE);
                viewBind.llTips.setVisibility(View.VISIBLE);
                break;
            case DeviceStateConstant.DEVICE_NORMAL:
                viewBind.llTips.setVisibility(View.GONE);
                viewBind.webAdvert.resumeTimers();
                viewBind.webAdvert.setVisibility(View.VISIBLE);
                viewBind.btGoodsOut.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void deleteOldFile(String bannerList) {
        if (bannerList != null) {
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

    public void loadWeb(AdvertMould advertMould) {
        Log.i(TAG, "加载网页");
        if (advertMould == null || advertMould.getMouldId() == 0 || advertMould.getMouldVersion() == 0.0) {
            Log.i(TAG, "加载默认网页");
            loadDefaultWeb();
        } else {
            Log.i(TAG, "加载sdk网页");
            AdvertContent lAdvertContent = mainViewModel.getAdvertContent();
            if (lAdvertContent == null || lAdvertContent.getContent() == null || lAdvertContent.getContent().equals("")) {
                XLog.tag(TAG).i("本地广告内容为空");
                loadDefaultWeb();
            } else {
                List<String> lFileList = JSONArray.parseArray(lAdvertContent.getContent(), String.class);
                String bannerList = String.join(",", lFileList);
                XLog.tag(TAG).i("加载的广告包内容为：" + bannerList);
                viewBind.webAdvert.loadUrl("file://" + FilePathConstant.FILE_PATH + "index.html" + "?BannerList=" + bannerList);
                new Thread(() -> deleteOldFile(bannerList)).start();
            }
        }
    }

    private void loadDefaultWeb() {
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH_LAND);
            //横屏
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            viewBind.webAdvert.loadUrl(FilePathConstant.BIG_SCREEN_ADVERTISING_DF_PATH_PORT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "获取权限后初始化设备");
                init();
            }
        }
    }

    public void addNetworkMonitor() {
        ThreadPoolUtils.getInstance().doThings(() -> {
            if (!NetUtils.ping()) {
                XLog.tag(TAG).i("无网络");
                runOnUiThread(() -> changPage(DeviceStateConstant.DEVICE_NO_NET));
            }
        });
        XLog.tag(TAG).i("监听网络状态");
        NetUtils.registerNetworkMonitor(this, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                XLog.tag(TAG).i("网络连接");
                if (mIsFirst) {
                    if (NetUtils.ping()) {
                        mIsFirst = false;
                        mainViewModel.initNet(MainActivity.this);
                        NetUtils.getNetSignal(MainActivity.this);
                    } else {
                        runOnUiThread(() -> changPage(DeviceStateConstant.DEVICE_NO_NET));
                    }
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                XLog.tag(TAG).i("网络断开");
                if (mIsFirst) {
                    runOnUiThread(() -> changPage(DeviceStateConstant.DEVICE_NO_NET));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetUtils.unRegisterNetworkMonitor();
    }

    private void onClick2(View v) {
        viewBind.webAdvert.pauseTimers();
        viewBind.webAdvert.setVisibility(View.GONE);
        viewBind.btGoodsOut.setVisibility(View.GONE);
        viewBind.inKeyboard.glKeyboard.setVisibility(View.VISIBLE);
        mTimer = new CountDownTimer(30000, 1000) {
            private void run() {
                viewBind.inKeyboard.glKeyboard.setVisibility(View.GONE);
                viewBind.inKeyboard.tvInput.setText("");
                viewBind.webAdvert.resumeTimers();
                viewBind.webAdvert.setVisibility(View.VISIBLE);
                viewBind.btGoodsOut.setVisibility(View.VISIBLE);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(() -> viewBind.inKeyboard.tvBackTime.setText(" " + millisUntilFinished / 1000 + "s"));
            }

            @Override
            public void onFinish() {
                XLog.tag(TAG).i("输入取货码超时");
                runOnUiThread(this::run);
            }
        }.start();
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
            loadDefaultWeb();
        }

        @Override
        public void onReceivedError(@NonNull WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            XLog.tag(TAG).i("web " + description);
        }
    }
}
