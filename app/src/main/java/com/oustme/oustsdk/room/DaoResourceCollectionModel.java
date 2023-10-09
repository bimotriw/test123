package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoResourceCollectionModel {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResourceCollectionModel(EntityResourceCollection collectionModel);

    @Query("select * from EntityResourceCollection where id = :id")
    EntityResourceCollection findResourceCollectionModel(int id);

    @Query("select count(id) from EntityResourceCollection")
    long getCount();
}
