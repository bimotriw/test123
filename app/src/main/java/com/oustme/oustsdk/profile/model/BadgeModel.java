package com.oustme.oustsdk.profile.model;

import java.io.Serializable;

public class BadgeModel implements Serializable {

    String badgeIcon;
    String badgeName;
    String completedOn;
    long completedOnSort;
    String contentType;
    String courseName;
    String contentId;

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(String completedOn) {
        this.completedOn = completedOn;
        setCompletedOnSort(Long.parseLong(completedOn));
    }

    public long getCompletedOnSort() {
        return completedOnSort;
    }

    public void setCompletedOnSort(long completedOnSort) {
        this.completedOnSort = completedOnSort;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
