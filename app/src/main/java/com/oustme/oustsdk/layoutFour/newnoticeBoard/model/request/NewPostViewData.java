package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBCommentData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBReplyData;


@Keep
public class NewPostViewData {

    private final String COMMENT_TYPE="comment";
    private final String LIKE_TYPE="like";
    private final String SHARE_TYPE="share_text";
    private final String REPLY_TYPE="reply";
    private final String DELETE_COMMENT="delete_comment";
    private final String DELETE_REPLY="delete_reply";

    public NewPostViewData(){

    }
    public NewPostViewData(long nbId, long postid, long timeStamp) {
        this.nbId = nbId;
        this.postid = postid;
        this.timeStamp = timeStamp;
    }

    public NewPostViewData(long nbId, long postid) {
        this(nbId,postid,System.currentTimeMillis());
    }

    private long nbId,postid,timeStamp;
    private String type;
    private boolean isLike;
    private NewNBCommentData nbCommentData;
    private NewNBReplyData nbReplyData;

    public NewNBReplyData getNbReplyData() {
        return nbReplyData;
    }

    public void setNbReplyData(NewNBReplyData nbReplyData) {
        this.nbReplyData = nbReplyData;
    }

    public NewNBCommentData getNbCommentData() {
        return nbCommentData;
    }

    public void setNbCommentData(NewNBCommentData nbCommentData) {
        this.nbCommentData = nbCommentData;
    }

    public String getType() {
        return type;
    }

    public NewPostViewData setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isLike() {
        return isLike;
    }

    public NewPostViewData setLike(boolean like) {
        isLike = like;
        return this;
    }

    public long getNbId() {
        return nbId;
    }

    public NewPostViewData setNbId(long nbId) {
        this.nbId = nbId;
        return this;
    }

    public long getPostid() {
        return postid;
    }

    public NewPostViewData setPostid(long postid) {
        this.postid = postid;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public NewPostViewData setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public NewPostViewData setCommentType(){
        this.type = COMMENT_TYPE;
        return this;
    }
    public NewPostViewData setLikeType(){
        this.type = LIKE_TYPE;
        return this;
    }
    public NewPostViewData setShareType(){
        this.type = SHARE_TYPE;
        return this;
    }

    public NewPostViewData setReplyDeleteType(){
        this.type = DELETE_REPLY;
        return this;
    }
    public NewPostViewData setCommentDeleteType(){
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
