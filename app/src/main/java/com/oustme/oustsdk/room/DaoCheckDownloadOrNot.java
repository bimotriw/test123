package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DaoCheckDownloadOrNot {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDownloadedOrNot(EntityDownloadedOrNot entityDownloadedOrNot);

    @Query("SELECT * FROM EntityDownloadedOrNot where courseId = :courseId")
    List<EntityDownloadedOrNot> getDownloadedOrNot(long courseId);

    @Query("SELECT * FROM EntityDownloadedOrNot")
    List<EntityDownloadedOrNot> getDownloadedOrNotS();

    @Query("delete from EntityDownloadedOrNot where courseId = :courseId")
    void deleteDownloadedOrNot(long courseId);
}