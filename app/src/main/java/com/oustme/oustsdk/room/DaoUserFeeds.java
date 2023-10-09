package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DaoUserFeeds {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntityUserFeed(EntityUserFeeds newFeed);

    @Query("select * from EntityUserFeeds where feedId = :feedId limit 1")
    EntityUserFeeds getEntityUserFeeds(String feedId);

    @Query("select * from EntityUserFeeds order by distributedOn desc")
    List<EntityUserFeeds> getEntityAllUserFeeds();

    @Query("delete from EntityUserFeeds")
    void deleteEntityUserFeeds();
}
