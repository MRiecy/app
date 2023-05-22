package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Goods.class,AdvertMould.class,AdvertContent.class, ResultShopping.class}, version = 1, exportSchema = false)
public abstract class DeviceDataBase extends RoomDatabase {
    private static final String DB_NAME = "device.db";
    private static DeviceDataBase sMyDataBase;

    public static synchronized DeviceDataBase getInstance(Context context) {
        if (sMyDataBase == null) {
            sMyDataBase = Room.databaseBuilder(context.getApplicationContext(), DeviceDataBase.class, DB_NAME)
                    .createFromAsset("database/goods.db")
                    .build();
        }
        return sMyDataBase;
    }

    public abstract DeviceDao getDeviceDao();
}
