package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Created by oust on 2/26/19.
 */
@Entity
public class EntityPostViewData {

    @NonNull
    @PrimaryKey
    private long timeStamp;
    private long nbId, postid;
    private String type;
    private boolean isLike;
    @Embedded(prefix = "pst_cmnt_")
    private EntityNBCommentData entityNBCommentData;
    @Embedded(prefix = "pst_rply_")
    private EntityNBReplyData entityNBReplyData;

    public EntityNBReplyData getEntityNBReplyData() {
        return entityNBReplyData;
    }

    public void setEntityNBReplyData(EntityNBReplyData entityNBReplyData) {
        this.entityNBReplyData = entityNBReplyData;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNbId() {
        return nbId;
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public long getPostid() {
        return postid;
    }

    public void setPostid(long postid) {
        this.postid = postid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public EntityNBCommentData getEntityNBCommentData() {
        return entityNBCommentData;
    }

    public void setEntityNBCommentData(EntityNBCommentData entityNBCommentData) {
        this.entityNBCommentData = entityNBCommentData;
    }

    public boolean isPostTypeLike() {
        return "like".equals(this.type);
    }

    public boolean isPostTypeComment() {
        return "comment".equals(this.type);
    }

    public boolean isPostTypeReply() {
        return "reply".equals(this.type);
    }

    public boolean isPostTypeShare() {
        return "share".equals(this.type);
    }

}
