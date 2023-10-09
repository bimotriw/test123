package com.oustme.oustsdk.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
@Entity
public class  EntityDiaryDetailsModel{

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String endTS, startTS, updateTS, createTS;
    private boolean isEditable, isdeleted;
    private String defaultBanner;
    private String mBanner;
    private String approvalStatus;
    private int type;
    private String fileName;
    private String fileSize;
    private String userLD_Id;
    private String activityName;
    private String comments;
    private Long sortingTime;
    @TypeConverters(TCMediaList.class)
    private List<EntityMediaList> learningDiaryMediaDataList;

    public EntityDiaryDetailsModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEndTS() {
        return endTS;
    }

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    public void setEndTS(String endTS) {
        this.endTS = endTS;
    }

    public String getStartTS() {
        return startTS;
    }

    public void setStartTS(String startTS) {
        this.startTS = startTS;
    }

    public Long getSortingTime() {
        return sortingTime;
    }

    public void setSortingTime(long sortingTime) {
        this.sortingTime = sortingTime;
    }

    public String getCreateTS() {
        return createTS;
    }

    public void setCreateTS(String createTS) {
        this.createTS = createTS;
    }

    public void setSortingTime(Long sortingTime) {
        this.sortingTime = sortingTime;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getDefaultBanner() {
        return defaultBanner;
    }

    public void setDefaultBanner(String defaultBanner) {
        this.defaultBanner = defaultBanner;
    }

    public String getmBanner() {
        return mBanner;
    }

    public void setmBanner(String mBanner) {
        this.mBanner = mBanner;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getUserLD_Id() {
        return userLD_Id;
    }

    public void setUserLD_Id(String userLD_Id) {
        this.userLD_Id = userLD_Id;
    }

    public String getActivity() {
        return activityName;
    }

    public void setActivity(String activityName) {
        this.activityName = activityName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public List<EntityMediaList> getLearningDiaryMediaDataList() {
        return learningDiaryMediaDataList;
    }

    public void setLearningDiaryMediaDataList(List<EntityMediaList> learningDiaryMediaDataList) {
        this.learningDiaryMediaDataList = learningDiaryMediaDataList;
    }

}
