package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DeviceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGoods(Goods... goods);

    @Transaction
    @Query("SELECT*FROM GOODS WHERE iR = :ir and iC = :ic")
    Goods queryGoods(int ir, int ic);

    @Transaction
    @Query("SELECT*FROM GOODS ORDER BY iR asc ")
    List<Goods> queryAllGoods();

    @Transaction
    @Query("DElETE FROM GOODS")
    void clearGoods();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAdvertContent(AdvertContent... advertContents);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAdvertMould(AdvertMould... advertMoulds);

    @Transaction
    @Query("SELECT*FROM advertmould ")
    LiveData<AdvertMould> getAdvertMouldByLiveData();

    @Transaction
    @Query("SELECT*FROM advertmould ")
    AdvertMould getAdvertMould();

    @Transaction
    @Query("SELECT*FROM advertcontent")
    AdvertContent getAdvertContent();

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addResultShopping(ResultShopping... resultShopping);

    @Transaction
    @Query("SELECT*FROM resultshopping")
    ResultShopping getResultShopping();

    @Transaction
    @Query("DElETE FROM resultshopping")
    void clearResultShopping();

}
