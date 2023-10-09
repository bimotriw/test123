package com.oustme.oustsdk.room.dto;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class EntityNotificationData {

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("Id")
    private int id;

    @JsonProperty("contentId")
    private String contentId;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("message")
    private String message;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @JsonProperty("updateTime")
    private String updateTime;

    @JsonProperty("keyValue")
    private String keyValue;

    @JsonProperty("isFireBase")
    private Boolean isFireBase;

    @JsonProperty("isRead")
    private Boolean isRead;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("notificationKey")
    private String notificationKey;

    @JsonProperty("commentId")
    private String commentId;

    @JsonProperty("noticeBoardId")
    private String noticeBoardId;

    @JsonProperty("replyId")
    private String replyId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Boolean getFireBase() {
        return isFireBase;
    }

    public void setFireBase(Boolean fireBase) {
        isFireBase = fireBase;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getNoticeBoardId() {
        return noticeBoardId;
    }

    public void setNoticeBoardId(String noticeBoardId) {
        this.noticeBoardId = noticeBoardId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
}
