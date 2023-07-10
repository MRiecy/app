package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goods.class, AdvertMould.class, AdvertContent.class, ResultShopping.class, SystemRebootTimes.class}, version = 3, exportSchema = false)
public abstract class DeviceDataBase extends RoomDatabase {
    private static final String DB_NAME = "device.db";
    private static DeviceDataBase sMyDataBase;

    public static synchronized DeviceDataBase getInstance(Context context) {
        if (sMyDataBase == null) {
            sMyDataBase = Room.databaseBuilder(context.getApplicationContext(), DeviceDataBase.class, DB_NAME)
                    .createFromAsset("database/goods.db")
//                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS  SystemRebootTimes(id INTEGER PRIMARY KEY NOT NULL , systemRebootTimes INTEGER NOT NULL)");
        }
    };

    public abstract DeviceDao getDeviceDao();

}
