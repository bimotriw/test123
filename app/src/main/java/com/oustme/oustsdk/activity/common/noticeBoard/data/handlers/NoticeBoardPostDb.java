package com.oustme.oustsdk.activity.common.noticeBoard.data.handlers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.OustAppState;

/**
 * Created by oust on 3/19/19.
 */

public abstract class NoticeBoardPostDb {

    protected final String FIREBASE_SINGLETON = "SINGLETON";
    protected final String FIREBASE_LIVE = "LIVE";
    protected int limitCount;

    public NoticeBoardPostDb() {
        this.limitCount = 0;
    }

    public NoticeBoardPostDb(int limitCount) {
        this.limitCount = limitCount;
    }

    public void addLiveListenerToFireBase(String message, ValueEventListener mynbPostListener) {
        DatabaseReference nbPostFBRef = OustFirebaseTools.getRootRef().child(message);
        Query query = nbPostFBRef.orderByChild("timeStamp");
        if (limitCount > 0) {
            query.limitToFirst(limitCount);
        }
        query.keepSynced(true);
        query.addValueEventListener(mynbPostListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(mynbPostListener, message));
    }

    public void addLiveListenerToFireBase(String sortBy, String message, ValueEventListener mynbPostListener) {
        DatabaseReference nbPostFBRef = OustFirebaseTools.getRootRef().child(message);
        Query query = nbPostFBRef.orderByChild(sortBy);
        if (limitCount > 0) {
            query.limitToFirst(limitCount);
        }
        query.keepSynced(true);
        query.addValueEventListener(mynbPostListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(mynbPostListener, message));
    }


    public void addSingletonListenerToFireBase(String message, ValueEventListener mynbPostListener) {
        if (limitCount > 0) {
            OustFirebaseTools.getRootRef().child(message).limitToFirst(limitCount).addListenerForSingleValueEvent(mynbPostListener);
        } else {
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(mynbPostListener);
        }
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public void addSingletonListenerToFireBase(String sortBy, String message, ValueEventListener mynbPostListener) {
        if (limitCount > 0) {
            OustFirebaseTools.getRootRef().child(message).limitToLast(limitCount).orderByChild(sortBy).addListenerForSingleValueEvent(mynbPostListener);
        } else {
            OustFirebaseTools.getRootRef().child(message).orderByChild(sortBy).addListenerForSingleValueEvent(mynbPostListener);
        }
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    public abstract void notifyDataFound(NBPostData nbPostData);
}
