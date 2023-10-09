package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;
import java.util.Map;


/**
 * Created by oust on 3/19/19.
 */

public abstract class NoticeBoardExtraPostDb extends NoticeBoardPostDb{

    private final String POST_BASE_NODE = "/nbPost/post";
    protected String firebaseListenerType;
    protected NBPostData nbPostData;
    protected int commentCount;

    public NoticeBoardExtraPostDb(String firebaseListenerType , NBPostData nbPostData, int commentCount) {
        this.firebaseListenerType = firebaseListenerType;
        this.nbPostData = nbPostData;
        this.commentCount = commentCount;

        getPostExtraData();
    }

    public void getPostExtraData(){
        String message = POST_BASE_NODE + nbPostData.getId();
        ValueEventListener mypostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (lpMap != null) {
                            nbPostData.initBaseData(lpMap);
                            new NBFirebaseCommentData(nbPostData.getId(),commentCount, firebaseListenerType) {
                                @Override
                                public void notifyDataFound(NBPostData nbPostData) {

                                }
                                @Override
                                public void notifyCommentDataFound(List<NBCommentData> nbCommentDataList) {
                                    nbPostData.setNbCommentDataList(nbCommentDataList);
                                    NoticeBoardExtraPostDb.this.notifyDataFound(nbPostData);
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
