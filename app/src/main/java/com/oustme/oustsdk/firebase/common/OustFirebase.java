package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class OustFirebase {
//    private static final String TAG = OustFirebase.class.getName();
//
//    // Listener defined earlier
//    public interface OustFirebaseAuthenticationListener {
//        public void onAuthenticated(AuthData authData);
//        public void onAuthenticationError(DatabaseError error);
//    }
//
//    // Listener defined earlier
//    public interface OustFirebaseDataListener {
//        public void onDataLoaded(DataSnapshot dataSnapshot);
//        public void onCancelled(DatabaseError error);
//    }
//
//    // Member variable was defined earlier
//    private OustFirebaseAuthenticationListener oustFirebaseAuthenticationListener;
//    private OustFirebaseDataListener oustFirebaseDataListener;
//
//    private String message;
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    // Assign the listener implementing events interface that will receive the events
//    public void setOustFirebaseDataListener(OustFirebaseDataListener listener) {
//        this.oustFirebaseDataListener = listener;
//        fetchData();
//    }
//
//
//    // Assign the listener implementing events interface that will receive the events
//    public void setOustFirebaseAuthenticationListener(OustFirebaseAuthenticationListener listener) {
//        this.oustFirebaseAuthenticationListener = listener;
//        authenticate();
//    }
//
//    // Constructor where listener events are ignored
//    public OustFirebase() {
//        // set null or default listener or accept as argument to constructor
//        this.oustFirebaseAuthenticationListener = null;
//        this.oustFirebaseDataListener = null;
///*        if(this.message != null && this.message.contains("token:")) {
//            this.message =  this.message.replace("token:", "");
//            authenticate();
//        }   else if(this.message != null) {
//            this.message =  this.message.replace("token:", "");
//            fetchData();
//        }*/
//
//    }
//
//
//    private void authenticate() {
//        try {
//            if (this.message != null) {
//                OustFirebaseTools.getRootRef().authWithCustomToken(this.message, new Firebase.AuthResultHandler() {
//                    @Override
//                    public void onAuthenticationError(DatabaseError error) {
//                        try {
//                            if (oustFirebaseAuthenticationListener != null) {
//                                oustFirebaseAuthenticationListener.onAuthenticationError(error);
//                            }
//                        }catch (Exception e){}
//                    }
//
//                    @Override
//                    public void onAuthenticated(AuthData authData) {
//                        try {
//                            if (oustFirebaseAuthenticationListener != null) {
//                                oustFirebaseAuthenticationListener.onAuthenticated(authData);
//                            }
//                        }catch (Exception e){}
//                    }
//                });
//            }
//        }catch (Exception e){
//
//        }
//    }
//
//
//    private void fetchData() {
//        OustFirebaseTools.getRootRef().child(this.message).addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                oustFirebaseDataListener.onDataLoaded(snapshot);
//            }
//            @Override public void onCancelled(DatabaseError error) {
//                oustFirebaseDataListener.onCancelled(error);
//            }
//        });
//
//    }

}
