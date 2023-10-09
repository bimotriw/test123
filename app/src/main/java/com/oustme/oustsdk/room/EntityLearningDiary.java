package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
public class EntityLearningDiary  {

    @NonNull
    @PrimaryKey
    private String uid;

    @TypeConverters(TCDiaryDetailsModel.class)
    List<EntityDiaryDetailsModel> _newsList;

    public EntityLearningDiary() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<EntityDiaryDetailsModel> get_newsList() {
        return _newsList;
    }

    public void set_newsList(List<EntityDiaryDetailsModel> _newsList) {
        this._newsList = _newsList;
    }
}
