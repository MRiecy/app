package com.jianjia.medicinevendingmachine.work;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetWork extends Worker {
    public NetWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String hostAddress = "";
        String domainName = getInputData().getString("domainName");
        if (!TextUtils.isEmpty(domainName)) {
            try {
                InetAddress inetAddress = InetAddress.getByName(domainName);
                hostAddress = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Data hostAddressData = new Data.Builder().putString("hostAddress", hostAddress).build();
        return Result.success(hostAddressData);
    }
}
