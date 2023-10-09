package com.oustme.oustsdk.activity.assessments.learningdiary;

import com.oustme.oustsdk.model.response.diary.FilterModel;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;

import java.util.List;

public interface LearningDiaryView {
    interface LDView{
        void showProgressBar(int type);
        void hideProgressBar(int type);
        void onError(String error);
        void updateDataFromAPI(List<DTODiaryDetailsModel> diaryDetailsModelList, int totalCount, int type);
        void hideAlertDialog();
        void showAlertProgressBar();
        void hideAlertProgressbar();
        void successDelete(String userLD_id);
        void successUpdate();
        void successAdded();
        void failureAdded();
        void failureUpdate();
        void updateFilters(List<FilterModel> filterList);
        void failureFilterData();
        void updateLastEntryDate(String time);
    }
}
