package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import android.annotation.SuppressLint;
import android.content.Context;

import com.elvishew.xlog.XLog;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocalRepository {
    private final DeviceDao mDeviceDao;
    private final String TAG = "LocalRepository";

    @Inject
    public LocalRepository(@ApplicationContext Context context) {
        mDeviceDao = DeviceDataBase.getInstance(context).getDeviceDao();
    }


    public void addGoods(Goods... goods) {
        mDeviceDao.addGoods(goods).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("存储Goods数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("存储Goods数据失败:" + e.getMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    public void deleteGoods() {
        mDeviceDao.clearGoods().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("删除Goods数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("删除Goods数据成功：" + e.getMessage());
            }
        });
    }

    public Goods getGoods(int ir, int ic) {
        return mDeviceDao.getGoods(ir, ic).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public List<Goods> getAllGoods() {
        return mDeviceDao.getAllGoods().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public void updateAndInsertAdvertMould(AdvertMould... advertMoulds) {
        mDeviceDao.addAdvertMould(advertMoulds).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("添加AdvertMould数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("添加AdvertMould数据失败：" + e.getMessage());
            }
        });
    }

    public void updateAndInsertAdvertContent(AdvertContent... advertContents) {
        mDeviceDao.addAdvertContent(advertContents).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("添加AdvertContent数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("添加AdvertContent数据失败：" + e.getMessage());
            }
        });
    }

    public AdvertMould getAdvertMould() {
        return mDeviceDao.getAdvertMould().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public AdvertContent getAdvertContent() {
        return mDeviceDao.getAdvertContent().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public void addResultShopping(ResultShopping... resultShopping) {
        mDeviceDao.addResultShopping(resultShopping).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("添加ResultShopping数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("添加ResultShopping数据失败：" + e.getMessage());
            }
        });
    }

    public ResultShopping getResultShopping() {
        return mDeviceDao.getResultShopping().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public void deleteResultShopping() {
        mDeviceDao.clearResultShopping().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("删除ResultShopping所有数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("删除ResultShopping数据失败：" + e.getMessage());
            }
        });
    }

    public Flowable<AdvertMould> getAdvertMouldByLiveData() {
        return mDeviceDao.getAdvertMouldByLiveData().subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public SystemRebootTimes getSystemRebootTimes() {
        return mDeviceDao.getSystemRebootTimes().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).blockingGet();
    }

    public void updateAndInsertSystemRebootTimes(SystemRebootTimes... systemRebootTimes) {
        mDeviceDao.addSystemRebootTimes(systemRebootTimes).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                XLog.tag(TAG).i("添加SystemRebootTimes数据成功");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                XLog.tag(TAG).i("添加SystemRebootTimes数据失败：" + e.getMessage());
            }
        });
    }
}
