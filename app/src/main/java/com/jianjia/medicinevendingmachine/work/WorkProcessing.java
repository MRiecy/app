package com.jianjia.medicinevendingmachine.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class WorkProcessing {
    private final String TAG = "WorkProcessing";
    private final WorkManager workManager;
    private final Constraints constraints;
    private OneTimeWorkRequest oneTimeWorkRequest;

    @Inject
    public WorkProcessing(@ApplicationContext Context context) {
        Log.i(TAG, "初始化");
        workManager = WorkManager.getInstance(context);
        constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    }

    public void doOneTimeWork(Data data, @NonNull Class<? extends ListenableWorker> workerClass1, @NonNull Class<? extends ListenableWorker> workerClass2) {
        OneTimeWorkRequest oneTimeWorkRequest1 = new OneTimeWorkRequest.Builder(workerClass1)
                .setConstraints(constraints).setInputData(data).build();
        OneTimeWorkRequest oneTimeWorkRequest2 = new OneTimeWorkRequest.Builder(workerClass2)
                .setConstraints(constraints).setInputData(data).build();
        workManager.enqueue(List.of(oneTimeWorkRequest1, oneTimeWorkRequest2));
    }

    public void doOneTimeWork(Data data, @NonNull Class<? extends ListenableWorker> workerClass) {
        oneTimeWorkRequest = new OneTimeWorkRequest.Builder(workerClass)
                .setConstraints(constraints).setInputData(data).build();
        workManager.enqueue(oneTimeWorkRequest);
    }

    public void doOneTimeWorkCallBack(LifecycleOwner owner, Observer<WorkInfo> observer) {
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(owner, observer);
    }

    public void cancelWork() {
        workManager.cancelAllWork();
    }
}
