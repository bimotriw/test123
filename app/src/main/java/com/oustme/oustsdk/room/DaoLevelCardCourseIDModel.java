package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoLevelCardCourseIDModel {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLevelCardCourseIDModel(EntityLevelCardCourseID levelCardCourseIDModel);

    @Query("select * from EntityLevelCardCourseID where levelId = :levelId")
    List<EntityLevelCardCourseID> getAllLevelCardCourseIDModel(long levelId);

    @Query("delete from EntityLevelCardCourseID where levelId = :levelId")
    void deleteLevelCardCourseIDModel(long levelId);

}
