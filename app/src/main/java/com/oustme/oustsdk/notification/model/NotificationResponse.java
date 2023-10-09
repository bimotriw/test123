package com.oustme.oustsdk.notification.model;

public class NotificationResponse {
    private String contentId;
    private String imageUrl;
    private String message;
    private String title;
    private String type;
    private String updateTime;
    long completedOnSort;
    private Boolean isFireBase;
    private String key;
    private String notificationKey;
    private Boolean read;
    private String userId;
    private String status;
    private String notificationId;
    private String commentId;
    private String noticeBoardId;
    private String replyId;

    public NotificationResponse() {

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
//        setCompletedOnSort(Long.parseLong(updateTime));
    }

    public long getCompletedOnSort() {
        return completedOnSort;
    }

    public void setCompletedOnSort(long completedOnSort) {
        this.completedOnSort = completedOnSort;
    }

    public Boolean getFireBase() {
        return isFireBase;
    }

    public void setFireBase(Boolean fireBase) {
        isFireBase = fireBase;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read2) {
        read = read2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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
