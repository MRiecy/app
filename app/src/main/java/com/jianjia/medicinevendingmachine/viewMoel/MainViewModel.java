package com.jianjia.medicinevendingmachine.viewMoel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.elvishew.xlog.XLog;
import com.jianjia.medicinevendingmachine.constants.NetworkConfiguration;
import com.jianjia.medicinevendingmachine.dataStore.Repository;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertContent;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.AdvertMould;
import com.jianjia.medicinevendingmachine.dataStore.localrepository.LocalRepository;
import com.jianjia.medicinevendingmachine.ui.MainActivity;
import com.jianjia.medicinevendingmachine.work.NetWork;
import com.jianjia.medicinevendingmachine.work.WorkProcessing;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {
    private String TAG = "MainViewModel";
    private final MutableLiveData<Integer> deviceState = new MutableLiveData<>();
    private final MutableLiveData<String> tempAndHumValue = new MutableLiveData<>();
    private final MutableLiveData<String> deviceNoValue = new MutableLiveData<>();
    private MutableLiveData<String> qrPath = new MutableLiveData<>();
    Repository mRepository;
    LocalRepository mLocalRepository;
    WorkProcessing mWorkProcessing;

    @Inject
    public MainViewModel(@NonNull Application application, Repository repository, LocalRepository localRepository, WorkProcessing workProcessing) {
        super(application);
        this.mRepository = repository;
        this.mLocalRepository = localRepository;
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

    public LiveData<AdvertMould> getAdvertMould() {
        return mLocalRepository.getAdvertMouldByLiveData();
    }

    public AdvertContent getAdvertContent() {
        return mLocalRepository.getAdvertContent();
    }

    public AdvertMould getAdvertMouldNoLive() {
        return mLocalRepository.getAdvertMould();
    }

    public void outGoods(String outCode) {
        mRepository.outGoods(outCode);
    }

    public void init(MainActivity activity) {
        mRepository.startLog();
        Data domainNameData = new Data.Builder().putString("domainName", NetworkConfiguration.URL).build();
        mWorkProcessing.doOneTimeWork(domainNameData, NetWork.class);
        mWorkProcessing.doOneTimeWorkCallBack(activity, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    XLog.tag(TAG).i("线程" + Thread.currentThread());
                    String hostAddress = workInfo.getOutputData().getString("hostAddress");
                    XLog.tag(TAG).i("中转平台IP地址为：" + hostAddress + " " + NetworkConfiguration.PORT);
                    if (hostAddress != null) {
                        new Thread(() -> mRepository.init(hostAddress,deviceState,tempAndHumValue,deviceNoValue,qrPath)).start();
                    }
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.closeConnect();
        mWorkProcessing.cancelWork();
    }
}
