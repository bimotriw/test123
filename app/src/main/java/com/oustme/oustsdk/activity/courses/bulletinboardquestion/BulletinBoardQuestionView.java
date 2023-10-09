package com.oustme.oustsdk.activity.courses.bulletinboardquestion;

import com.google.firebase.database.DataSnapshot;

public interface BulletinBoardQuestionView {
    void setToolBarColor();

    void updateQuestionFromFireBase(DataSnapshot dataSnapshot);

    void updateQuestionDataFromFB(DataSnapshot dataSnapshot, String quesKey);

    void onError();

    void showLoader();

    void hideLoader();
}
