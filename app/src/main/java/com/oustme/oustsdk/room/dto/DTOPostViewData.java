package com.oustme.oustsdk.room.dto;

/**
 * Created by oust on 2/26/19.
 */

public class DTOPostViewData {
    private final String COMMENT_TYPE="comment";
    private final String LIKE_TYPE="like";
    private final String SHARE_TYPE="share";
    private final String REPLY_TYPE="reply";
    private final String DELETE_COMMENT="delete_comment";
    private final String DELETE_REPLY="delete_reply";

    public DTOPostViewData(){

    }
    public DTOPostViewData(long nbId, long postid, long timeStamp) {
        this.nbId = nbId;
        this.postid = postid;
        this.timeStamp = timeStamp;
    }

    public DTOPostViewData(long nbId, long postid) {
        this(nbId,postid,System.currentTimeMillis());
    }

    private long nbId,postid,timeStamp;
    private String type;
    private boolean isLike;
    private DTONBCommentData nbCommentData;
    private DTONBReplyData nbReplyData;

    public DTONBReplyData getNbReplyData() {
        return nbReplyData;
    }

    public void setNbReplyData(DTONBReplyData nbReplyData) {
        this.nbReplyData = nbReplyData;
    }

    public DTONBCommentData getNbCommentData() {
        return nbCommentData;
    }

    public void setNbCommentData(DTONBCommentData nbCommentData) {
        this.nbCommentData = nbCommentData;
    }

    public String getType() {
        return type;
    }

    public DTOPostViewData setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isLike() {
        return isLike;
    }

    public DTOPostViewData setLike(boolean like) {
        isLike = like;
        return this;
    }

    public long getNbId() {
        return nbId;
    }

    public DTOPostViewData setNbId(long nbId) {
        this.nbId = nbId;
        return this;
    }

    public long getPostid() {
        return postid;
    }

    public DTOPostViewData setPostid(long postid) {
        this.postid = postid;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public DTOPostViewData setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public DTOPostViewData setCommentType(){
        this.type = COMMENT_TYPE;
        return this;
    }
    public DTOPostViewData setLikeType(){
        this.type = LIKE_TYPE;
        return this;
    }
    public DTOPostViewData setShareType(){
        this.type = SHARE_TYPE;
        return this;
    }

    public DTOPostViewData setReplyDeleteType(){
        this.type = DELETE_REPLY;
        return this;
    }
    public DTOPostViewData setCommentDeleteType(){
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
