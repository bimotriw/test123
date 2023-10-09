package com.oustme.oustsdk.activity.common.noticeBoard.model.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;

/**
 * Created by oust on 2/22/19.
 */

@Keep
public class PostViewData {

    private final String COMMENT_TYPE="comment";
    private final String LIKE_TYPE="like";
    private final String SHARE_TYPE="share_text";
    private final String REPLY_TYPE="reply";
    private final String DELETE_COMMENT="delete_comment";
    private final String DELETE_REPLY="delete_reply";

    public PostViewData(){

    }
    public PostViewData(long nbId, long postid, long timeStamp) {
        this.nbId = nbId;
        this.postid = postid;
        this.timeStamp = timeStamp;
    }

    public PostViewData(long nbId, long postid) {
        this(nbId,postid,System.currentTimeMillis());
    }

    private long nbId,postid,timeStamp;
    private String type;
    private boolean isLike;
    private NBCommentData nbCommentData;
    private NBReplyData nbReplyData;

    public NBReplyData getNbReplyData() {
        return nbReplyData;
    }

    public void setNbReplyData(NBReplyData nbReplyData) {
        this.nbReplyData = nbReplyData;
    }

    public NBCommentData getNbCommentData() {
        return nbCommentData;
    }

    public void setNbCommentData(NBCommentData nbCommentData) {
        this.nbCommentData = nbCommentData;
    }

    public String getType() {
        return type;
    }

    public PostViewData setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isLike() {
        return isLike;
    }

    public PostViewData setLike(boolean like) {
        isLike = like;
        return this;
    }

    public long getNbId() {
        return nbId;
    }

    public PostViewData setNbId(long nbId) {
        this.nbId = nbId;
        return this;
    }

    public long getPostid() {
        return postid;
    }

    public PostViewData setPostid(long postid) {
        this.postid = postid;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public PostViewData setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public PostViewData setCommentType(){
        this.type = COMMENT_TYPE;
        return this;
    }
    public PostViewData setLikeType(){
        this.type = LIKE_TYPE;
        return this;
    }
    public PostViewData setShareType(){
        this.type = SHARE_TYPE;
        return this;
    }

    public PostViewData setReplyDeleteType(){
        this.type = DELETE_REPLY;
        return this;
    }
    public PostViewData setCommentDeleteType(){
        this.type = DELETE_COMMENT;
        return this;
    }

    public boolean isPostTypeLike(){
        return LIKE_TYPE.equals(this.type);
    }
    public boolean isPostTypeComment(){
        return COMMENT_TYPE.equals(this.type);
    }
    public boolean isPostTypeReply(){
        return REPLY_TYPE.equals(this.type);
    }
    public boolean isPostTypeShare(){
        return SHARE_TYPE.equals(this.type);
    }
    public boolean isPostTypeCommentDelete(){
        return DELETE_COMMENT.equals(this.type);
    }
    public boolean isPostTypeReplyDelete(){
        return DELETE_REPLY.equals(this.type);
    }
}
