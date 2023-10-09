package com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.tools.OustSdkTools;


public abstract class NewNBFirebasePostData {

    private int commentCount = 0;
    private String firebaseListenerType;
    private long postId;
    private String studentKey;

    public NewNBFirebasePostData(long postId,int commentCount,String studentKey, String firebaseListenerType) {
        this.commentCount = commentCount;
        this.firebaseListenerType = firebaseListenerType;
        this.postId=postId;
        this.studentKey=studentKey;

        getPostUserData();
    }

    public void getPostUserData() {
        try {
            new NewNoticeBoardUserPostDb(firebaseListenerType, studentKey, postId) {
                @Override
                public void notifyDataFound(NewNBPostData nbPostData) {
                    getExtraInfo(nbPostData);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getExtraInfo(NewNBPostData nbPostData) {
        try {
            new NewNoticeBoardExtraPostDb(firebaseListenerType, nbPostData, commentCount) {
                @Override
                public void notifyDataFound(NewNBPostData nbPostData) {
                    notifyPostDataFound(nbPostData);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public abstract void notifyPostDataFound(NewNBPostData nbPostData);

}
