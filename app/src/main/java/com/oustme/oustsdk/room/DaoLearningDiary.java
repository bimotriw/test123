package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoLearningDiary {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLearningDiary(EntityLearningDiary learningDiary);

    @Query("select * from EntityLearningDiary where uid = :id")
    EntityLearningDiary getLearningDiary(String id);
}
