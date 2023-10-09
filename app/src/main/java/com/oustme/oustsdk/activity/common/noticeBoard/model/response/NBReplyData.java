package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

/**
 * Created by oust on 2/18/19.
 */

@Keep
public class NBReplyData {
    private long id , commentId,replied_on;
    private String reply,replied_by,avatar,userKey;

    public String getAvatar() {
        return avatar;
    }

    public NBReplyData setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public long getId() {
        return id;
    }

    public NBReplyData setId(long id) {
        this.id = id;
        return this;
    }

    public long getCommentId() {
        return commentId;
    }

    public NBReplyData setCommentId(long commentId) {
        this.commentId = commentId;
        return this;
    }

    public long getReplied_on() {
        return replied_on;
    }

    public NBReplyData setReplied_on(long replied_on) {
        this.replied_on = replied_on;
        return this;
    }

    public String getReply() {
        return reply;
    }

    public NBReplyData setReply(String reply) {
        this.reply = reply;
        return this;
    }

    public String getReplied_by() {
        return replied_by;
    }

    public NBReplyData setReplied_by(String replied_by) {
        this.replied_by = replied_by;
        return this;
    }

    public boolean hasAvatar() {
        return this.avatar != null && !this.avatar.isEmpty();
    }

    public String getUserKey() {
        return userKey;
    }

    public NBReplyData setUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }
}
