package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoUserCourseData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserCourseData(EntityUserCourseData userCourseData);

    @Query("select * from EntityUserCourseData where id = :id limit 1")
    EntityUserCourseData findUserCourseDataById(long id);

    @Query("delete from EntityUserCourseData where id = :id")
    void deleteUserCourseData(long id);


}
