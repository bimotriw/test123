package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;
import java.util.Map;

public abstract class NewNoticeBoardExtraPostDb extends NewNoticeBoardPostDb {

    private final String POST_BASE_NODE = "/nbPost/post";
    protected String firebaseListenerType;
    protected NewNBPostData nbPostData;
    protected int commentCount;

    public NewNoticeBoardExtraPostDb(String firebaseListenerType, NewNBPostData nbPostData, int commentCount) {
        this.firebaseListenerType = firebaseListenerType;
        this.nbPostData = nbPostData;
        this.commentCount = commentCount;

        getPostExtraData();
    }

    public void getPostExtraData() {
        String message = POST_BASE_NODE + nbPostData.getId();
        ValueEventListener mypostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (lpMap != null) {
                            nbPostData.initBaseData(lpMap);
                            new NewNBFirebaseCommentData(nbPostData.getId(), commentCount, firebaseListenerType) {
                                @Override
                                public void notifyDataFound(NewNBPostData nbPostData) {

                                }

                                @Override
                                public void notifyCommentDataFound(List<NewNBCommentData> nbCommentDataList) {
                                    nbPostData.setNbCommentDataList(nbCommentDataList);
                                    NewNoticeBoardExtraPostDb.this.notifyDataFound(nbPostData);
                                }
                            }.getPostCommentData();

                        } else {
                            notifyDataFound(nbPostData);
                        }
                    } else {
                        notifyDataFound(nbPostData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    notifyDataFound(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        if (firebaseListenerType.equals(FIREBASE_SINGLETON))
            addSingletonListenerToFireBase(message, mypostListener);
        else
            addLiveListenerToFireBase(message, mypostListener);
    }


}
