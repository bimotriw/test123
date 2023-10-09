package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoResourceDataModel {

    @Query("select * from EntityResourceData where filename = :filename")
    EntityResourceData getResourceDataModel(String filename);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addorUpdateResourceDataModel(EntityResourceData entityResourceData);
}
