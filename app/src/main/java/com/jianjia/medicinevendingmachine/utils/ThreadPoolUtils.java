package com.jianjia.medicinevendingmachine.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtils {
    private final ExecutorService lExecutorService;
    private static ThreadPoolUtils mThreadPoolUtils;

    private ThreadPoolUtils() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lExecutorService = Executors.newWorkStealingPool();
        } else {
            lExecutorService = Executors.newFixedThreadPool(10);

        }
    }

    public static synchronized ThreadPoolUtils getInstance() {
        if (mThreadPoolUtils == null) {
            mThreadPoolUtils = new ThreadPoolUtils();
        }
        return mThreadPoolUtils;
    }

    public void doThings(Runnable runnable) {
        lExecutorService.execute(runnable);
    }
}
