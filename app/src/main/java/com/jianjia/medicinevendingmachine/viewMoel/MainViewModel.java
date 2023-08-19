package com.jianjia.medicinevendingmachine.viewMoel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.NetworkConfiguration;
import com.jianjia.medicinevendingmachine.dataStore.Repository;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertContent;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertMould;
import com.jianjia.medicinevendingmachine.ui.MainActivity;
import com.jianjia.medicinevendingmachine.utils.ThreadPoolUtils;
import com.jianjia.medicinevendingmachine.work.NetWork;
import com.jianjia.medicinevendingmachine.work.WorkProcessing;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {
    private final String TAG = "MainViewModel";
    private final MutableLiveData<Integer> deviceState = new MutableLiveData<>();
    private final MutableLiveData<String> tempAndHumValue = new MutableLiveData<>();
    private final MutableLiveData<String> deviceNoValue = new MutableLiveData<>();
    private final MutableLiveData<String> qrPath = new MutableLiveData<>();
    private final MutableLiveData<String> macAddress = new MutableLiveData<>();
    private final MutableLiveData<AdvertMould> mAdvertMouldMutableLiveData = new MutableLiveData<>();
    Repository mRepository;
    WorkProcessing mWorkProcessing;

    @Inject
    public MainViewModel(@NonNull Application application, Repository repository, WorkProcessing workProcessing) {
        super(application);
        this.mRepository = repository;
        this.mWorkProcessing = workProcessing;
    }

    public MutableLiveData<Integer> getDeviceState() {
        return deviceState;
    }

    public MutableLiveData<String> getTempAndHumValue() {
        return tempAndHumValue;
    }

    public MutableLiveData<String> getDeviceNoValue() {
        return deviceNoValue;
    }

    public MutableLiveData<String> getQrPath() {
        return qrPath;
    }

    public MutableLiveData<AdvertMould> getAdvertMouldByLiveData() {
        return mAdvertMouldMutableLiveData;
    }

    public AdvertContent getAdvertContent() {
        return mRepository.getAdvertContent();
    }

    public String getMacAddress() {
        return macAddress.getValue();
    }

    public void outGoods(String outCode) {
        mRepository.outGoods(outCode);
    }

    public void initRepositoryData() {
        mRepository.startLog();
        mRepository.initViewModelData(deviceState,
                tempAndHumValue, deviceNoValue, qrPath,
                mAdvertMouldMutableLiveData, macAddress);
        mRepository.initDevice();
    }

    public void initNet(MainActivity activity) {
        Data domainNameData = new Data.Builder().putString("domainName", NetworkConfiguration.URL).build();
        mWorkProcessing.doOneTimeWork(domainNameData, NetWork.class);
        new Handler(Looper.getMainLooper()).post(() -> mWorkProcessing.doOneTimeWorkCallBack(activity, workInfo -> {
            if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                String hostAddress = workInfo.getOutputData().getString("hostAddress");
                XLog.tag(TAG).i("中转平台IP地址为：" + hostAddress + " " + NetworkConfiguration.PORT);
                if (hostAddress != null && !"".equals(hostAddress)) {
                    ThreadPoolUtils.getInstance().doThings(() -> {
                        XLog.tag(TAG).i("初始化设备");
                        mRepository.initSocket(hostAddress);
                    });
                }
            }
        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.closeConnect();
        mWorkProcessing.cancelWork();
    }
}
