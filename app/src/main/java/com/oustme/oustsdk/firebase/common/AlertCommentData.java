package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import java.util.Comparator;

/**
 * Created by oust on 4/27/18.
 */

@Keep
public class AlertCommentData {
    private long addedOnDate;
    private long feedId;
    private String userAvatar;
    private String userDisplayName;
    private String userId;
    private long userKey;
    private long numReply;
    private String devicePlatform;
    private String comment;


    public long getAddedOnDate() {
        return addedOnDate;
    }

    public void setAddedOnDate(long addedOnDate) {
        this.addedOnDate = addedOnDate;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserDisplayName() {
        if(userDisplayName!=null&&!userDisplayName.isEmpty()){
            return userDisplayName;
        }else{
            return "";
        }
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getUserKey() {
        return userKey;
    }

    public void setUserKey(long userKey) {
        this.userKey = userKey;
    }

    public long getNumReply() {
        return numReply;
    }

    public void setNumReply(long numReply) {
        this.numReply = numReply;
    }

    public String getDevicePlatform() {
        return devicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        this.devicePlatform = devicePlatform;
    }

    public String getComment() {

        if(comment!=null&&!comment.isEmpty()){
            return comment;
        }else{
            return "";
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Comparator<AlertCommentData> commentSorter = new Comparator<AlertCommentData>() {
        public int compare(AlertCommentData s1, AlertCommentData s2) {
            if (s1.getAddedOnDate() == 0) {
                return -1;
            }
            if (s2.getAddedOnDate() == 0) {
                return 1;
            }
            if (s1.getAddedOnDate() == s2.getAddedOnDate()) {
                return 0;
            }
            return Long.compare(s2.getAddedOnDate(), s1.getAddedOnDate());
        }
    };
}
