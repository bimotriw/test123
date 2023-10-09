package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;


/**
 * Created by oust on 3/19/19.
 */

public abstract class NoticeBoardUserPostDb extends NoticeBoardPostDb{

    protected String firebaseListenerType,studentKey;
    protected long postId;
    private final String POST_USER_NODE = "/userNbPost/";


    public NoticeBoardUserPostDb(String firebaseListenerType, String studentKey, long postId) {
        this.firebaseListenerType = firebaseListenerType;
        this.studentKey = studentKey;
        this.postId = postId;
        getData();
    }

    public void getData(){
        String message = POST_USER_NODE + studentKey + "/post/post" + postId;
        ValueEventListener mypostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NBPostData nbPostData = new NBPostData();
                nbPostData.setId(postId);
                try {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> lpMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (lpMap != null) {
                            nbPostData.initUSerData(lpMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);

                }
                notifyDataFound(nbPostData);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        if (firebaseListenerType.equals(FIREBASE_SINGLETON))
            addSingletonListenerToFireBase(message, mypostListener);
        else
            addLiveListenerToFireBase(message, mypostListener);
    }

}
