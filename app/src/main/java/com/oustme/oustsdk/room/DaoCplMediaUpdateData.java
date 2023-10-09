package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoCplMediaUpdateData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrUpdateCplMediaUpdateData(EntityCplMediaUpdateData cplMediaUpdateData);

    @Query("select * from EntityCplMediaUpdateData where cplId = :tntCplId limit 1")
    EntityCplMediaUpdateData getCplUpdateModel(String tntCplId);
}
