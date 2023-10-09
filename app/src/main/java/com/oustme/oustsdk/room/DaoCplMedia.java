package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoCplMedia {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCplMedia(EntityCplMedia cplMedia);

    @Query("select * from EntityCplMedia where id = :id limit 1")
    EntityCplMedia getCplMediaById(String id);

    @Query("select * from EntityCplMedia where cplId = :tnt_cplId")
    List<EntityCplMedia> getCplMedias(String tnt_cplId);
}
