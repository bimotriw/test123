package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by oust on 2/26/19.
 */

@Entity
public class EntityNBReplyData {

    @NonNull
    @PrimaryKey
    private long id;
    private long commentId, replied_on;
    private String reply, replied_by, avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getReplied_on() {
        return replied_on;
    }

    public void setReplied_on(long replied_on) {
        this.replied_on = replied_on;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplied_by() {
        return replied_by;
    }

    public void setReplied_by(String replied_by) {
        this.replied_by = replied_by;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
