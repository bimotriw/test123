package com.oustme.oustsdk.notification.model;

import com.oustme.oustsdk.tools.ActiveUser;

import java.util.ArrayList;

public class NotificationComponentModel {
    private ArrayList<NotificationResponse> notificationResponseHashMap;
    private ActiveUser activeUser;

    public ArrayList<NotificationResponse> getNotificationResponseHashMap() {
        return notificationResponseHashMap;
    }

    public void setNotificationResponseHashMap(ArrayList<NotificationResponse> notificationResponseHashMap) {
        this.notificationResponseHashMap = notificationResponseHashMap;
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }
}
