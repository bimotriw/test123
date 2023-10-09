package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewNoticeBoardPostDb;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;



public abstract class NewNoticeBoardUserPostDb extends NewNoticeBoardPostDb {

    protected String firebaseListenerType,studentKey;
    protected long postId;
    private final String POST_USER_NODE = "/userNbPost/";


    public NewNoticeBoardUserPostDb(String firebaseListenerType, String studentKey, long postId) {
        this.firebaseListenerType = firebaseListenerType;
        this.studentKey = studentKey;
        this.postId = postId;
        getData();
    }

    public void getData(){
        String message = POST_USER_NODE + studentKey + "/post/post" + postId;
        ValueEventListener mypostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NewNBPostData nbPostData = new NewNBPostData();
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        if (firebaseListenerType.equals(FIREBASE_SINGLETON))
            addSingletonListenerToFireBase(message, mypostListener);
        else
            addLiveListenerToFireBase(message, mypostListener);
    }

}
