package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoCourseCardClass {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourseCardClass(EntityCourseCardClass courseCardClass);

    @Query("select * from EntityCourseCardClass where cardId=:id")
    EntityCourseCardClass findCourseCardClass(long id);

    @Query("select * from EntityCourseCardClass where cardId=:id")
    List<EntityCourseCardClass> findCourseCardClassByCourseId(long id);

    @Query("delete from EntityCourseCardClass where cardId = :cardId")
    void deleteCourseCardClass(long cardId);
}
