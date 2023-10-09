package com.oustme.oustsdk.activity.courses.bulletinboardquestion;

import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.BulletinBoardData;
import com.oustme.oustsdk.response.course.UserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class BulletinBoardQuestionPresenter {
    private static final String TAG = "BulletinBoardQuestionPr";
    private BulletinBoardQuestionView mView;
    private FirebaseRefClass firebaseRefClass;

    public BulletinBoardQuestionPresenter(BulletinBoardQuestionView view) {
        this.mView = view;
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

    public void updateTimeStamp(long millis, boolean isCplBulletin, long courseId) {
        Log.d(TAG, "updateTimeStamp: "+millis+" isCPL:"+isCplBulletin+" courseO:"+courseId);
        String message = "/courseThread/course" + courseId + "/updateTime";
        if (isCplBulletin) {
            message = "/cplThread/cpl" + courseId + "/updateTime";
        }
        Log.d(TAG, "updateTimeStamp: "+message);
        OustFirebaseTools.getRootRef().child(message).setValue(millis);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void setIdToCourseThread(String key, String courseId, boolean isCplBulletin) {
        String message = "/courseThread/course" + courseId + "/questionThreads" + "/" + key;
        if (isCplBulletin)
            message = "/cplThread/cpl" + courseId + "/questionThreads" + "/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        getBulletinQuesFromFirebase(courseId, isCplBulletin);
    }

    public void getBulletinQuesFromFirebase(String courseId, boolean isCplBulletin) {
        String message = "/courseThread/" + "course" + courseId + "/questionThreads";
        if (isCplBulletin)
            message = "/cplThread/" + "cpl" + courseId + "/questionThreads";
        ValueEventListener allfavCardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mView.updateQuestionFromFireBase(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                mView.setToolBarColor();
                Log.e("FirebaseD", "onCancelled()");
            }
        };

        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allfavCardListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        firebaseRefClass = new FirebaseRefClass(allfavCardListener, message);
    }

    public void setidToUserThread(String key, String userId) {
        try {
            String message = "/userThreads/user" + userId + "/" + key;
            OustFirebaseTools.getRootRef().child(message).setValue(true);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getQuestionsDataFromFirebase(final String queskey) {
        Log.d(TAG, "getQuestionsDataFromFirebase: "+queskey);
        String message = "/discussionBoardThreads/" + queskey;
        ValueEventListener allfavCardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mView.updateQuestionDataFromFB(dataSnapshot, queskey);
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                mView.setToolBarColor();
                mView.onError();
                // setAdapter(null);
                Log.e("FirebaseD", "onCancelled()");
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(allfavCardListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        firebaseRefClass = new FirebaseRefClass(allfavCardListener, message);
    }

    public void setDataToFirebase(BulletinBoardData bulletinBoardData, boolean isCplBulletin) {
        Log.d(TAG, "setDataToFirebase: ");
        long millis = new Date().getTime();
        String message = "/discussionBoardThreads";
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(bulletinBoardData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        if (isCplBulletin) {
            Log.d(TAG, "setDataToFirebase: "+bulletinBoardData.getCplId());
            setIdToCourseThread(newPostRef.getKey(), bulletinBoardData.getCplId() + "", isCplBulletin);
            updateTimeStamp(bulletinBoardData.getAddedOnDate(), isCplBulletin, bulletinBoardData.getCplId() );
        } else {
            Log.d(TAG, "setDataToFirebase: "+bulletinBoardData.getCourseId());
            updateTimeStamp(bulletinBoardData.getAddedOnDate(), isCplBulletin, bulletinBoardData.getCourseId() );
            setIdToCourseThread(newPostRef.getKey(), bulletinBoardData.getCourseId() + "", isCplBulletin);
        }
        setidToUserThread(newPostRef.getKey(), bulletinBoardData.getUserKey()+"");

    }

}
