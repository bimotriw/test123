package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityCplCompletedModel {

    @NonNull
    @PrimaryKey
    private long id;
    private long completedOn;
    private String type;
    private boolean isCompleted;
    private boolean isSubmittedToServer;
    private boolean mLearningStatus;
    private int mRetryCount;

    public EntityCplCompletedModel() {
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

    public boolean isMLearningStatus() {
        return mLearningStatus;
    }

    public void setMLearningStatus(boolean mLearningStatus) {
        this.mLearningStatus = mLearningStatus;
    }

    public int getMRetryCount() {
        return mRetryCount;
    }

    public void setMRetryCount(int mRetryCount) {
        this.mRetryCount = mRetryCount;
    }
}
