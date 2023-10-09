package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoLevelUpdateTime {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLevelCardCourseUpdateTimeIDModel(EntityLevelCardCourseIDUpdateTime levelCardCourseIDModel);

    @Query("SELECT * FROM EntityLevelCardCourseIDUpdateTime where levelId = :levelId")
    List<EntityLevelCardCourseIDUpdateTime> getAllLevelCardCourseUpdateTimeIDModel(int levelId);

    @Query("delete from EntityLevelCardCourseIDUpdateTime where courseId = :courseId")
    void deleteLevelCardCourseUpdateTimeIDModel(int courseId);

}