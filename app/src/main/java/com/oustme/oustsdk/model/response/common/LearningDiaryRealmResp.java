package com.oustme.oustsdk.model.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.model.response.diary.DiaryDetailsModel;

import java.util.List;


@Keep
public class LearningDiaryRealmResp {
    private String uid;
    List<DiaryDetailsModel> _newsList;

    public LearningDiaryRealmResp() {
    }

    public LearningDiaryRealmResp(String uid, List<DiaryDetailsModel> _newsList) {
        this.uid = uid;
        this._newsList = _newsList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<DiaryDetailsModel> get_newsList() {
        return _newsList;
    }

    public void set_newsList(List<DiaryDetailsModel> _newsList) {
        this._newsList = _newsList;
    }
}
