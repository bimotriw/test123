package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoPostViewData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addorUpdatePostViewData(EntityPostViewData postViewData);

    @Query("delete from EntityPostViewData where nbId =:nbId and postid =:postid and type = :type")
    void deletePostViewData(long nbId, long postid, String type);
}
