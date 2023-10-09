package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoCatalogueItemData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCatalogueItemData(EntityCatalogueItemData catalogueItemData);

    @Query("select * from EntityCatalogueItemData where parentId = :catalogueId")
    List<EntityCatalogueItemData> getEntityCatalogueItemDatas(long catalogueId);

}
