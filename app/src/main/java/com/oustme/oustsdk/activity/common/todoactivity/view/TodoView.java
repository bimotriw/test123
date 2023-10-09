package com.oustme.oustsdk.activity.common.todoactivity.view;

import com.google.firebase.database.DataSnapshot;

public interface TodoView {
    void updateFFCData();
    void extractFFCData(DataSnapshot dataSnapshot);
    void updateFFCEnrolledCount(long count);
    void updateUserFFCUserContest(DataSnapshot dataSnapshot);
}
