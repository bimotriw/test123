package com.oustme.oustsdk.reminderNotification;

public class ContentReminderNotification {

    String userId;
    long contentId;
    String contentType;
    long reminderDateTime;
    String devicePlatform;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(long reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    public String getDevicePlatform() {
        return devicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        this.devicePlatform = devicePlatform;
    }
}
