package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface DeviceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addGoods(Goods... goods);

    @Transaction
    @Query("SELECT*FROM GOODS WHERE iR = :ir and iC = :ic")
    Maybe<Goods> getGoods(int ir, int ic);

    @Transaction
    @Query("SELECT*FROM GOODS ORDER BY iR asc ")
    Maybe<List<Goods>> getAllGoods();

    @Transaction
    @Query("DElETE FROM GOODS")
    Completable clearGoods();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addAdvertContent(AdvertContent... advertContents);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addAdvertMould(AdvertMould... advertMoulds);

    @Transaction
    @Query("SELECT*FROM advertmould ")
    Flowable<AdvertMould> getAdvertMouldByLiveData();

    @Transaction
    @Query("SELECT*FROM advertmould ")
    Maybe<AdvertMould> getAdvertMould();

    @Transaction
    @Query("SELECT*FROM advertcontent")
    Maybe<AdvertContent> getAdvertContent();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addResultShopping(ResultShopping... resultShopping);

    @Transaction
    @Query("SELECT*FROM resultshopping")
    Maybe<ResultShopping> getResultShopping();

    @Transaction
    @Query("DElETE FROM resultshopping")
    Completable clearResultShopping();

    @Transaction
    @Query("SELECT*FROM systemreboottimes ")
    Maybe<SystemRebootTimes> getSystemRebootTimes();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addSystemRebootTimes(SystemRebootTimes... systemRebootTimes);
}
