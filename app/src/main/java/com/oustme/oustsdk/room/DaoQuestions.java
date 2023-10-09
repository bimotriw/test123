package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoQuestions {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void instertQuestions(EntityQuestions questions);

    @Query("select * from EntityQuestions where questionCardId = :quesCardId")
    EntityQuestions findQuestions(String quesCardId);

    @Query("delete from EntityQuestions where questionCardId = :quesCardId")
    void deleteQuestions(String quesCardId);
}
