package com.oustme.oustsdk.activity.courses.learningmapmodule;

import com.google.firebase.database.DataSnapshot;
import com.oustme.oustsdk.response.course.LearningCardResponceData;

import java.util.List;

public interface LearningMapModuleView {

    void updateFavCardsFromFB(DataSnapshot dataSnapshot);

    void onError();

    void hideLoader();

    void showLoader();

    void updateSubmitCardData(boolean levelDataIsUpDated , boolean apiIsFalling, List<LearningCardResponceData> getUserCardResponse, long mappedSurveyId, long mappedAssessmentId);
}
