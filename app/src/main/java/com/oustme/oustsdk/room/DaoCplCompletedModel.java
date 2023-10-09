package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoCplCompletedModel {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCplCompletedModel(EntityCplCompletedModel cplCompletedModel);

    @Query("select * from EntityCplCompletedModel  where id = :id")
    EntityCplCompletedModel getCPLDataById(long id);

}
