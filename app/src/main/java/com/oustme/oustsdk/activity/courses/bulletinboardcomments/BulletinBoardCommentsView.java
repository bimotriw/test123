package com.oustme.oustsdk.activity.courses.bulletinboardcomments;

import com.google.firebase.database.DataSnapshot;

public interface BulletinBoardCommentsView {
    void updateCommentFromFireBase(DataSnapshot dataSnapshot);

    void setToolBarColor();

    void onError();

    void showLoader();

    void hideLoader();
}
