package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by oust on 2/26/19.
 */

@Entity
public class EntityNBCommentData{

    @NonNull
    @PrimaryKey
    private long id;
    private long postId,commentedOn;
    private String userKey,comment,avatar,commentedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(long commentedOn) {
        this.commentedOn = commentedOn;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

}
