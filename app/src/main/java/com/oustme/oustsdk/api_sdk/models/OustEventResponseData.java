package com.oustme.oustsdk.api_sdk.models;

import androidx.annotation.Keep;

import java.util.ArrayList;

@Keep
public class OustEventResponseData {

    private String userId;
    private String contentType;
    private long contentId;
    private String title;
    private String description;
    private String bannerImg;
    private String eventStatus;
    private long parentEventId;
    private long courselevelId;
    private boolean regularMode;
    private ArrayList<OustEventResponseData> childCardStatus;
    private OustAssessmentModuleData assessmentData;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(long parentEventId) {
        this.parentEventId = parentEventId;
    }

    public long getCourselevelId() {
        return courselevelId;
    }

    public void setCourselevelId(long courselevelId) {
        this.courselevelId = courselevelId;
    }

    public boolean isRegularMode() {
        return regularMode;
    }

    public void setRegularMode(boolean regularMode) {
        this.regularMode = regularMode;
    }

    public ArrayList<OustEventResponseData> getChildCardStatus() {
        return childCardStatus;
    }

    public void setChildCardStatus(ArrayList<OustEventResponseData> childCardStatus) {
        this.childCardStatus = childCardStatus;
    }

    public OustAssessmentModuleData getAssessmentData() {
        return assessmentData;
    }

    public void setAssessmentData(OustAssessmentModuleData assessmentData) {
        this.assessmentData = assessmentData;
    }
}
