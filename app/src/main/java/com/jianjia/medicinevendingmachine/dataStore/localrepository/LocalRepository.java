package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LocalRepository {
    private final DeviceDao mDeviceDao;
    private String TAG = "LocalRepository";


    @Inject
    public LocalRepository(@ApplicationContext Context context) {
        Log.i(TAG, "初始化");
        mDeviceDao = DeviceDataBase.getInstance(context).getDeviceDao();
    }

    public void addGoods(Goods... goods) {
        new addGoodsTask().execute(goods);
    }

    public void deleteGoods() {
        new deleteAllGoodsTask().execute();
    }

    public Goods getGoods(int ir, int ic) {
        try {
            return new getGoodsTask().execute(ir, ic).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Goods> getAllGoods() {
        try {
            return new getAllGoodsTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateAndInsertAdvertMould(AdvertMould... advertMoulds) {
        //XLog.tag(TAG).i("更新数据");
        new UpdateAdvertMouldTask().execute(advertMoulds);
    }

    public void updateAndInsertAdvertContent(AdvertContent... advertContents) {
        // XLog.tag(TAG).i("更新数据");
        new UpdateAdvertContentTask().execute(advertContents);
    }

    public AdvertMould getAdvertMould() {
        try {
            return new getAdvertMouldTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AdvertContent getAdvertContent() {
        try {
            return new getAdvertContentTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<AdvertMould> getAdvertMouldByLiveData() {
        try {
            return new getAdvertMouldByLiveDataTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<AdvertContent> getAdvertContentByLiveData() {
        try {
            return new getAdvertContentByLiveDataTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class addGoodsTask extends AsyncTask<Goods, Void, Void> {
        @Override
        protected Void doInBackground(Goods... goods) {
            mDeviceDao.addGoods(goods);
            return null;
        }
    }

    private class getGoodsTask extends AsyncTask<Integer, Void, Goods> {
        @Override
        protected Goods doInBackground(Integer... integers) {
            return mDeviceDao.queryGoods(integers[0], integers[1]);
        }
    }

    private class getAllGoodsTask extends AsyncTask<Void, Void, List<Goods>> {
        @Override
        protected List<Goods> doInBackground(Void... args0) {
            return mDeviceDao.queryAllGoods();
        }
    }

    private class deleteAllGoodsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args0) {
            mDeviceDao.clearGoods();
            return null;
        }
    }

    private class UpdateAdvertMouldTask extends AsyncTask<AdvertMould, Void, Void> {
        @Override
        protected Void doInBackground(AdvertMould... advertMoulds) {
            mDeviceDao.addAdvertMould(advertMoulds);
            return null;
        }
    }

    private class UpdateAdvertContentTask extends AsyncTask<AdvertContent, Void, Void> {
        @Override
        protected Void doInBackground(AdvertContent... advertContents) {
            mDeviceDao.addAdvertContent(advertContents);
            return null;
        }
    }

    private class getAdvertMouldTask extends AsyncTask<Void, Void, AdvertMould> {
        @Override
        protected AdvertMould doInBackground(Void... voids) {
            return mDeviceDao.getAdvertMould();
        }
    }

    private class getAdvertContentTask extends AsyncTask<Void, Void, AdvertContent> {
        @Override
        protected AdvertContent doInBackground(Void... voids) {
            return mDeviceDao.getAdvertContent();
        }
    }

    private class getAdvertMouldByLiveDataTask extends AsyncTask<Void, Void, LiveData<AdvertMould>> {
        @Override
        protected LiveData<AdvertMould> doInBackground(Void... voids) {
            return mDeviceDao.getAdvertMouldByLiveData();
        }
    }

    private class getAdvertContentByLiveDataTask extends AsyncTask<Void, Void, LiveData<AdvertContent>> {
        @Override
        protected LiveData<AdvertContent> doInBackground(Void... voids) {
            return mDeviceDao.getAdvertContentByLiveData();
        }
    }

}
