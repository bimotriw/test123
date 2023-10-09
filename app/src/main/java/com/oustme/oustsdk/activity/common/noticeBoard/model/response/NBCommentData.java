package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by oust on 2/18/19.
 */

@Keep
public class NBCommentData {
    private long id,postId,commentedOn;
    private String userKey,comment,avatar,commentedBy;
    private List<NBReplyData> nbReplyData;

    public long getCommentedOn() {
        return commentedOn;
    }

    public NBCommentData setCommentedOn(long commentedOn) {
        if(commentedOn==0){
            commentedOn=System.currentTimeMillis();
        }
        this.commentedOn = commentedOn;
        return this;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public NBCommentData setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
        return this;
    }

    public long getId() {
        return id;
    }

    public NBCommentData setId(long id) {
        this.id = id;
        return this;
    }

    public long getPostId() {
        return postId;
    }

    public NBCommentData setPostId(long postId) {
        this.postId = postId;
        return this;
    }

    public String getUserKey() {
        return userKey;
    }

    public NBCommentData setUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public NBCommentData setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public NBCommentData setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public List<NBReplyData> getNbReplyData() {
        return nbReplyData;
    }

    public NBCommentData setNbReplyData(List<NBReplyData> nbReplyData) {
        this.nbReplyData = nbReplyData;
        return this;
    }

    public boolean hasAvatar() {
        return this.avatar != null && !this.avatar.isEmpty();
    }

    public void initData(Map<String, Object> commentMap, long postId){
        try {
            this.avatar = OustSdkTools.convertToStr(commentMap.get("avatar"));
            long commentId = OustSdkTools.convertToLong(commentMap.get("commentId"));
            this.id = (commentId);
            this.comment = OustSdkTools.convertToStr(commentMap.get("comment"));
            this.commentedBy = OustSdkTools.convertToStr(commentMap.get("commentedBy"));
            this.commentedOn = OustSdkTools.convertToLong(commentMap.get("commentedOn"));
            this.postId = postId;
            this.userKey = (OustSdkTools.convertToStr(commentMap.get("userKey")));
            List<NBReplyData> replyDataList = new ArrayList<>();
            if (commentMap.containsKey("replies")) {
                HashMap<String, Object> repliesMap = (HashMap<String, Object>) commentMap.get("replies");
                for (String replyKey : repliesMap.keySet()) {
                    Map<String, Object> replyMap = (Map<String, Object>) repliesMap.get(replyKey);
                    NBReplyData nbReplyData = new NBReplyData();
                    nbReplyData.setReply(OustSdkTools.convertToStr(replyMap.get("replyComment")));
                    nbReplyData.setAvatar(OustSdkTools.convertToStr(replyMap.get("avatar")));
                    nbReplyData.setCommentId(commentId);
                    nbReplyData.setId(OustSdkTools.convertToLong(replyMap.get("replyId")));
                    nbReplyData.setReplied_by(OustSdkTools.convertToStr(replyMap.get("repliedBy")));
                    nbReplyData.setReplied_on(OustSdkTools.convertToLong(replyMap.get("repliedOn")));
                    nbReplyData.setUserKey(OustSdkTools.convertToStr(replyMap.get("userKey")));
                    replyDataList.add(nbReplyData);
                }
            }
            this.nbReplyData = replyDataList;
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
