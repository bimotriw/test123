package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoFeedBackModel {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFeedBackModelDao(EntityFeedBackModel entityFeedBackModel);

    @Query("select * from EntityFeedBackModel")
    List<EntityFeedBackModel> getAllFeedBack();

    @Query("delete from EntityFeedBackModel where id = :id")
    void deleteFeedBack(long id);

}
