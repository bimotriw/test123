package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;
import java.util.Map;


/**
 * Created by oust on 2/21/19.
 */

public abstract class NBFirebasePostData {

    private int commentCount = 0;
    private String firebaseListenerType;
    private long postId;
    private String studentKey;

    public NBFirebasePostData(long postId,int commentCount,String studentKey, String firebaseListenerType) {
        this.commentCount = commentCount;
        this.firebaseListenerType = firebaseListenerType;
        this.postId=postId;
        this.studentKey=studentKey;

        getPostUserData();
    }

    public void getPostUserData() {
        try {
            new NoticeBoardUserPostDb(firebaseListenerType, studentKey, postId) {
                @Override
                public void notifyDataFound(NBPostData nbPostData) {
                    getExtraInfo(nbPostData);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getExtraInfo(NBPostData nbPostData) {
        try {
            new NoticeBoardExtraPostDb(firebaseListenerType, nbPostData, commentCount) {
                @Override
                public void notifyDataFound(NBPostData nbPostData) {
                    notifyPostDataFound(nbPostData);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public abstract void notifyPostDataFound(NBPostData nbPostData);

}
