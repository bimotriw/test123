package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Keep
public class NewNBCommentData {
    private long id, postId, commentedOn;
    private String userKey, comment, avatar, commentedBy, designation, userRole;
    private List<NewNBReplyData> nbReplyData;

    public long getCommentedOn() {
        return commentedOn;
    }

    public NewNBCommentData setCommentedOn(long commentedOn) {
        if (commentedOn == 0) {
            commentedOn = System.currentTimeMillis();
        }
        this.commentedOn = commentedOn;
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

    public String getCommentedBy() {
        return commentedBy;
    }

    public NewNBCommentData setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
        return this;
    }

    public long getId() {
        return id;
    }

    public NewNBCommentData setId(long id) {
        this.id = id;
        return this;
    }

    public long getPostId() {
        return postId;
    }

    public NewNBCommentData setPostId(long postId) {
        this.postId = postId;
        return this;
    }

    public String getUserKey() {
        return userKey;
    }

    public NewNBCommentData setUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public NewNBCommentData setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public NewNBCommentData setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public List<NewNBReplyData> getNbReplyData() {
        return nbReplyData;
    }

    public NewNBCommentData setNbReplyData(List<NewNBReplyData> nbReplyData) {
        this.nbReplyData = nbReplyData;
        return this;
    }

    public boolean hasAvatar() {
        return this.avatar != null && !this.avatar.isEmpty();
    }

    public void initData(Map<String, Object> commentMap, long postId) {
        try {
            this.avatar = OustSdkTools.convertToStr(commentMap.get("avatar"));
            long commentId = OustSdkTools.convertToLong(commentMap.get("commentId"));
            this.id = (commentId);
            this.comment = OustSdkTools.convertToStr(commentMap.get("comment"));
            this.commentedBy = OustSdkTools.convertToStr(commentMap.get("commentedBy"));
            this.commentedOn = OustSdkTools.convertToLong(commentMap.get("commentedOn"));
            this.designation = OustSdkTools.convertToStr(commentMap.get("designation"));
            this.userRole = OustSdkTools.convertToStr(commentMap.get("userRole"));
            this.postId = postId;
            this.userKey = (OustSdkTools.convertToStr(commentMap.get("userKey")));
            List<NewNBReplyData> replyDataList = new ArrayList<>();
            if (commentMap.containsKey("replies")) {
                HashMap<String, Object> repliesMap = (HashMap<String, Object>) commentMap.get("replies");
                for (String replyKey : repliesMap.keySet()) {
                    Map<String, Object> replyMap = (Map<String, Object>) repliesMap.get(replyKey);
                    NewNBReplyData nbReplyData = new NewNBReplyData();
                    nbReplyData.setReply(OustSdkTools.convertToStr(replyMap.get("replyComment")));
                    nbReplyData.setAvatar(OustSdkTools.convertToStr(replyMap.get("avatar")));
                    nbReplyData.setCommentId(commentId);
                    nbReplyData.setId(OustSdkTools.convertToLong(replyMap.get("replyId")));
                    nbReplyData.setReplied_by(OustSdkTools.convertToStr(replyMap.get("repliedBy")));
                    nbReplyData.setReplied_on(OustSdkTools.convertToLong(replyMap.get("repliedOn")));
                    nbReplyData.setUserKey(OustSdkTools.convertToStr(replyMap.get("userKey")));
                    nbReplyData.setDesignation(OustSdkTools.convertToStr(replyMap.get("designation")));
                    nbReplyData.setUserRole(OustSdkTools.convertToStr(replyMap.get("userRole")));
                    replyDataList.add(nbReplyData);
                }
            }
            this.nbReplyData = replyDataList;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
