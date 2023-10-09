package com.oustme.oustsdk.activity.courses.bulletinboardcomments;

import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.BulletinBoardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

public class BulletinBoardCommentsPresenter {
    private static final String TAG = "BulletinBoardCommentsPr";

    private FirebaseRefClass firebaseRefClass;
    private BulletinBoardCommentsView mView;

    BulletinBoardCommentsPresenter(BulletinBoardCommentsView view) {
        this.mView = view;
    }

    public void getCommentsFromFirebase(String quesKey) {
        String message = "/discussionBoardThreadComments/" + quesKey;
        ValueEventListener allCommentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    mView.updateCommentFromFireBase(dataSnapshot);
                else
                    mView.updateCommentFromFireBase(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                Log.e("FirebaseD", "onCancelled()");
                mView.setToolBarColor();
                mView.onError();
                mView.hideLoader();
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allCommentsListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        firebaseRefClass = new FirebaseRefClass(allCommentsListener, message);
    }

    public void updateUserCountForQuestion(String quesKey) {
        String message = "discussionBoardThreads/" + quesKey + "/numComments";
        Log.d(TAG, "updateUserCountForQuestion: " + message);
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
        firebase.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                if (DatabaseError != null) {
                    Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                } else {
                    Log.e("", "Firebase counter increment succeeded.");
                }
            }
        });
    }

    public void setCommentToFB(BulletinBoardData bulletinBoardData) {
        String message = "/discussionBoardThreadComments/" + bulletinBoardData.getQuesKey();
        Log.d(TAG, "setCommentToFB: " + message);
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(bulletinBoardData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        setidToUserThread(newPostRef.getKey(), bulletinBoardData);
        updateUserCountForQuestion(bulletinBoardData.getQuesKey());
        updateTimeStamp(bulletinBoardData.getAddedOnDate(), false, bulletinBoardData.getCourseId());
    }

    private void setidToUserThread(String key, BulletinBoardData bulletinBoardData) {
        String message = "/userThreadComments/user" + bulletinBoardData.getUserKey() + "/" + bulletinBoardData.getQuesKey() + "/" + key;
        Log.d(TAG, "setidToUserThread: " + message);
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void updateTimeStamp(long millis, boolean isCplBulletin, long courseId) {
        if (courseId != 0) {
            Log.d(TAG, "updateTimeStamp: " + millis + " isCPL:" + isCplBulletin + " courseO:" + courseId);
            String message = "/courseThread/course" + courseId + "/updateTime";
            if (isCplBulletin) {
                message = "/cplThread/cpl" + courseId + "/updateTime";
            }
            Log.d(TAG, "updateTimeStamp: " + message);
            OustFirebaseTools.getRootRef().child(message).setValue(millis);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            updateLastUpdatedTime(millis);
        }
    }

    public void updateLastUpdatedTime(long updatedTime) {
        long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
        if (userCourseData != null) {
            userCourseData.setBulletinLastUpdatedTime(updatedTime);
            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqNo);
        }
    }

}
