package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGoods(Goods... goods);

    @Query("SELECT*FROM GOODS WHERE iR = :ir and iC = :ic")
    Goods queryGoods(int ir, int ic);

    @Query("SELECT*FROM GOODS ORDER BY iR asc ")
    List<Goods> queryAllGoods();

    @Query("DElETE FROM GOODS")
    void clearGoods();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAdvertContent(AdvertContent... advertContents);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAdvertMould(AdvertMould... advertMoulds);

    @Query("SELECT*FROM advertmould ")
    LiveData<AdvertMould> getAdvertMouldByLiveData();

    @Query("SELECT*FROM advertcontent")
    LiveData<AdvertContent> getAdvertContentByLiveData();

    @Query("SELECT*FROM advertmould ")
    AdvertMould getAdvertMould();

    @Query("SELECT*FROM advertcontent")
    AdvertContent getAdvertContent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addResultShopping(ResultShopping... resultShopping);

    @Query("SELECT*FROM resultshopping")
    ResultShopping getResultShopping();

    @Query("DElETE FROM resultshopping")
    void clearResultShopping();

}
