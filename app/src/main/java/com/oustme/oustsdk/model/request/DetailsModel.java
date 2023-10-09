package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class DetailsModel {
    private String startTS, endTS, activityName, comments;
    private List<LearningDiaryMediaDataList> learningDiaryMediaDataList;
    private boolean mediaChanged;
    private String approvalStatus;
    private long userLD_Id;

    public DetailsModel() {
    }

    public DetailsModel(String startTS, String endTS, String activityName, String comments, List<LearningDiaryMediaDataList> learningDiaryMediaDataList, boolean mediaChanged, String approvalStatus, long userLD_Id) {
        this.startTS = startTS;
        this.endTS = endTS;
        this.activityName = activityName;
        this.comments = comments;
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
        this.mediaChanged = mediaChanged;
        this.approvalStatus = approvalStatus;
        this.userLD_Id = userLD_Id;
    }

    public String getStartTS() {
        return startTS;
    }

    public void setStartTS(String startTS) {
        this.startTS = startTS;
    }

    public String getEndTS() {
        return endTS;
    }

    public void setEndTS(String endTS) {
        this.endTS = endTS;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<LearningDiaryMediaDataList> getLearningDiaryMediaDataList() {
        return learningDiaryMediaDataList;
    }

    public void setLearningDiaryMediaDataList(List<LearningDiaryMediaDataList> learningDiaryMediaDataList) {
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }

    public boolean isMediaChanged() {
        return mediaChanged;
    }

    public void setMediaChanged(boolean mediaChanged) {
        this.mediaChanged = mediaChanged;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public long getUserLD_Id() {
        return userLD_Id;
    }

    public void setUserLD_Id(long userLD_Id) {
        this.userLD_Id = userLD_Id;
    }
}
