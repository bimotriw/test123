package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

@Keep
public class DTOCplCompletedModel {
    private long id;
    private long completedOn;
    private String type;
    private boolean isCompleted;
    private boolean isSubmittedToServer;
    private boolean mLearningStatus;
    private int mRetryCount;

    public DTOCplCompletedModel() {
    }

    public DTOCplCompletedModel(long id, String type, boolean isCompleted, boolean isSubmittedToServer, long completedOn, int mRetryCount, boolean mLearningStatus) {
        this.id = id;
        this.type = type;
        this.isCompleted = isCompleted;
        this.isSubmittedToServer = isSubmittedToServer;
        this.completedOn = completedOn;
        this.mRetryCount = mRetryCount;
        this.mLearningStatus = mLearningStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isSubmittedToServer() {
        return isSubmittedToServer;
    }

    public void setSubmittedToServer(boolean submittedToServer) {
        isSubmittedToServer = submittedToServer;
    }

    public long getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }

    public boolean ismLearningStatus() {
        return mLearningStatus;
    }

    public void setmLearningStatus(boolean mLearningStatus) {
        this.mLearningStatus = mLearningStatus;
    }

    public int getmRetryCount() {
        return mRetryCount;
    }

    public void setmRetryCount(int mRetryCount) {
        this.mRetryCount = mRetryCount;
    }
}
