package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class FirebaseRefClass {
    public FirebaseRefClass(ValueEventListener eventListener, String firebasePath) {
        this.eventListener = eventListener;
        this.firebasePath = firebasePath;
    }

    ValueEventListener eventListener;
    ChildEventListener childEventListener;
    String firebasePath;

    public ValueEventListener getEventListener() {
        return eventListener;
    }

    public FirebaseRefClass(ChildEventListener eventListener, String firebasePath) {
        this.childEventListener = eventListener;
        this.firebasePath = firebasePath;
    }


    public ChildEventListener getChildEventListener() {
        return childEventListener;
    }

    public String getFirebasePath() {
        return firebasePath;
    }
}
