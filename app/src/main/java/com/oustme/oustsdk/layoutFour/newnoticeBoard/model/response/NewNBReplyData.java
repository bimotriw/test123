package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import androidx.annotation.Keep;

@Keep
public class NewNBReplyData {
    private long id, commentId, replied_on;
    private String reply, replied_by, avatar, userKey, designation, userRole;

    public String getAvatar() {
        return avatar;
    }

    public NewNBReplyData setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public long getId() {
        return id;
    }

    public NewNBReplyData setId(long id) {
        this.id = id;
        return this;
    }

    public long getCommentId() {
        return commentId;
    }

    public NewNBReplyData setCommentId(long commentId) {
        this.commentId = commentId;
        return this;
    }

    public long getReplied_on() {
        return replied_on;
    }

    public NewNBReplyData setReplied_on(long replied_on) {
        this.replied_on = replied_on;
        return this;
    }

    public String getReply() {
        return reply;
    }

    public NewNBReplyData setReply(String reply) {
        this.reply = reply;
        return this;
    }

    public String getReplied_by() {
        return replied_by;
    }

    public NewNBReplyData setReplied_by(String replied_by) {
        this.replied_by = replied_by;
        return this;
    }

    public boolean hasAvatar() {
        return this.avatar != null && !this.avatar.isEmpty();
    }

    public String getUserKey() {
        return userKey;
    }

    public NewNBReplyData setUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }
}
