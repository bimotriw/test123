package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoNewFeed {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntityNewFeed(EntityNewFeed newFeed);

    @Query("select * from EntityNewFeed where feedId = :feedId limit 1")
    EntityNewFeed getEntityNewFeedById(String feedId);

    @Query("select * from EntityNewFeed order by timestamp desc")
    List<EntityNewFeed> getEntityNewFeeds();

    @Query("delete from EntityNewFeed")
    void deleteEntityNewFeeds();
}
