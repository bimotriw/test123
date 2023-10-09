package com.oustme.oustsdk.notification.model;

import java.util.ArrayList;

public class RemoveNotificationRequest {

    private ArrayList<String> notificationIdList;
    private String userId;

    public ArrayList<String> getNotificationIdList() {
        return notificationIdList;
    }

    public void setNotificationIdList(ArrayList<String> notificationIdList) {
        this.notificationIdList = notificationIdList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
