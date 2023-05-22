package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goods.class, AdvertMould.class, AdvertContent.class, ResultShopping.class}, version = 2, exportSchema = false)
public abstract class DeviceDataBase extends RoomDatabase {
    private static final String DB_NAME = "device.db";
    private static DeviceDataBase sMyDataBase;

    public static synchronized DeviceDataBase getInstance(Context context) {
        if (sMyDataBase == null) {
            sMyDataBase = Room.databaseBuilder(context.getApplicationContext(), DeviceDataBase.class, DB_NAME)
                    .createFromAsset("database/goods.db")
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sMyDataBase;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS  ResultShopping(id INTEGER PRIMARY KEY NOT NULL , orderNO TEXT, orderState TEXT, orderResult TEXT, time TEXT)");
        }
    };


    public abstract DeviceDao getDeviceDao();
}
