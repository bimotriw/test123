package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoMapDataModel {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrUpdateMapDataModel(EntityMapDataModel entityMapDataModel);

    @Query("select * from EntityMapDataModel where id = :id")
    EntityMapDataModel getMapDataModel(String id);
}
