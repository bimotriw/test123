package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoResourseStringsModel {

    @Query("select * from EntityResourseStrings where languagePerfix =:languagePerfix")
    EntityResourseStrings findResourseStringsModel(String languagePerfix);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResourseStringsModel(EntityResourseStrings resourseStringsModel);
}
