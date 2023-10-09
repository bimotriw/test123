package com.oustme.oustsdk.room.dto;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class DTOLearningDiary {

    @NonNull
    @PrimaryKey
    private String uid;
    List<DTODiaryDetailsModel> _newsList;

    public DTOLearningDiary() {
    }

    public DTOLearningDiary(String uid, List<DTODiaryDetailsModel> _newsList) {
        this.uid = uid;
        this._newsList = _newsList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<DTODiaryDetailsModel> get_newsList() {
        return _newsList;
    }

    public void set_newsList(List<DTODiaryDetailsModel> _newsList) {
        this._newsList = _newsList;
    }
}
