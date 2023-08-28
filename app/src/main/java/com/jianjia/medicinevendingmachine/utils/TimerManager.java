package com.jianjia.medicinevendingmachine.utils;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    //时间间隔(一天)
    public static final long LOG_UPLOAD_TIME = 24 * 60 * 60 * 1000;
    public static final long TIMEOUT_DETECTION_TIME = 60 * 1000;
    public static final long TEMPERATURE_HUMIDITY_TIME = 60 * 1000;
    public static final long DELAY_UP_APP = 60 * 1000;

    private static List<Timer> mTimers = new ArrayList<>();

    private TimerManager() {
    }

    /**
     * 定期任务
     *
     * @param hour      时
     * @param minute    分
     * @param second    秒
     * @param period    延长时间
     * @param timerTask 任务处理器
     */
    public static void scheduledTasksOnDay(int hour, int minute, int second, long period, TimerTask timerTask) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour); //凌晨1点
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            date = addPeriodTime(date);
        }
        Timer timer = new Timer();
        mTimers.add(timer);
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(timerTask, date, period);
    }

    /**
     * 定时任务
     *
     * @param period    定时时间
     * @param timerTask 任务处理器
     */
    public static void scheduledTasksOnSecond(long period, TimerTask timerTask) {
        Timer timer = new Timer();
        mTimers.add(timer);
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(timerTask, 0, period);
    }

    /**
     * 定时延时任务
     *
     * @param delay     延时执行时间
     * @param period    定时时间
     * @param timerTask 任务处理器
     */
    public static void scheduledAndDelayTasksOnSecond(long delay, long period, TimerTask timerTask) {
        Timer timer = new Timer();
        mTimers.add(timer);
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(timerTask, delay, period);
    }

    /**
     * 延时任务
     *
     * @param delay     延长时间
     * @param timerTask 任务处理器
     */
    public static void delayedTaskTasksOnSecond(long delay, TimerTask timerTask) {
        Timer timer = new Timer();
        mTimers.add(timer);
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(timerTask, delay);
    }

    // 增加天数
    @NonNull
    private static Date addPeriodTime(@NonNull Date date) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, 1);
        return startDT.getTime();
    }

    public static void cancelTimer() {
        if (mTimers != null && mTimers.size() > 0) {
            for (Timer lMTimer : mTimers) {
                lMTimer.cancel();
            }
        }
    }
}
