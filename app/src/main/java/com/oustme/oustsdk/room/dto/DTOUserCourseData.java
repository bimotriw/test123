package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class DTOUserCourseData {
    private long id;
    private long currentLevel;
    private long currentCard;
    private long presentageComplete;
    private long totalOc;
    private long totalTime;
    private long totalCards;
    private boolean saved;
    private boolean isAlarmSet;
    private List<DTOUserLevelData> userLevelDataList;
    private int currentCompleteLevel;
    private int lastCompleteLevel;
    private boolean courseComplete;
    private int myCourseRating;
    private long lastPlayedLevel;
    private boolean askedVideoStorePermission;
    private boolean videoDownloadPermissionAllowed;
    private boolean deleteDataAfterComplete;

    private boolean isDownloading;
    private int downloadCompletePercentage;
    private String updateTS;
    private boolean acknowledged;
    private long currentLevelId;
    private boolean isMappedAssessmentPassed;
    private long bulletinLastUpdatedTime;

    private boolean courseCompleted;
    private boolean mappedAssessmentCompleted;
    private String addedOn;
    private boolean valid;
    private String enrollmentDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(long currentLevel) {
        this.currentLevel = currentLevel;
    }

    public long getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(long currentCard) {
        this.currentCard = currentCard;
    }

    public long getPresentageComplete() {
        return presentageComplete;
    }

    public void setPresentageComplete(long presentageComplete) {
        this.presentageComplete = presentageComplete;
    }

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public List<DTOUserLevelData> getUserLevelDataList() {
        return userLevelDataList;
    }

    public void setUserLevelDataList(List<DTOUserLevelData> userLevelDataList) {
        this.userLevelDataList = userLevelDataList;
    }

    public long getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(long totalCards) {
        this.totalCards = totalCards;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setAlarmSet(boolean alarmSet) {
        isAlarmSet = alarmSet;
    }

    public int getCurrentCompleteLevel() {
        return currentCompleteLevel;
    }

    public void setCurrentCompleteLevel(int currentCompleteLevel) {
        this.currentCompleteLevel = currentCompleteLevel;
    }

    public int getLastCompleteLevel() {
        return lastCompleteLevel;
    }

    public void setLastCompleteLevel(int lastCompleteLevel) {
        this.lastCompleteLevel = lastCompleteLevel;
    }

    public boolean isCourseComplete() {
        return courseComplete;
    }

    public void setCourseComplete(boolean courseComplete) {
        this.courseComplete = courseComplete;
    }

    public int getMyCourseRating() {
        return myCourseRating;
    }

    public void setMyCourseRating(int myCourseRating) {
        this.myCourseRating = myCourseRating;
    }

    public long getLastPlayedLevel() {
        return lastPlayedLevel;
    }

    public void setLastPlayedLevel(long lastPlayedLevel) {
        this.lastPlayedLevel = lastPlayedLevel;
    }

    public boolean isAskedVideoStorePermission() {
        return askedVideoStorePermission;
    }

    public void setAskedVideoStorePermission(boolean askedVideoStorePermission) {
        this.askedVideoStorePermission = askedVideoStorePermission;
    }

    public boolean isVideoDownloadPermissionAllowed() {
        return videoDownloadPermissionAllowed;
    }

    public void setVideoDownloadPermissionAllowed(boolean videoDownloadPermissionAllowed) {
        this.videoDownloadPermissionAllowed = videoDownloadPermissionAllowed;
    }

    public boolean isDeleteDataAfterComplete() {
        return deleteDataAfterComplete;
    }

    public void setDeleteDataAfterComplete(boolean deleteDataAfterComplete) {
        this.deleteDataAfterComplete = deleteDataAfterComplete;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getDownloadCompletePercentage() {
        return downloadCompletePercentage;
    }

    public void setDownloadCompletePercentage(int downloadCompletePercentage) {
        this.downloadCompletePercentage = downloadCompletePercentage;
    }

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public long getCurrentLevelId() {
        return currentLevelId;
    }

    public void setCurrentLevelId(long currentLevelId) {
        this.currentLevelId = currentLevelId;
    }

    public boolean isMappedAssessmentPassed() {
        return isMappedAssessmentPassed;
    }

    public void setMappedAssessmentPassed(boolean mappedAssessmentPassed) {
        isMappedAssessmentPassed = mappedAssessmentPassed;
    }

    public long getBulletinLastUpdatedTime() {
        return bulletinLastUpdatedTime;
    }

    public void setBulletinLastUpdatedTime(long bulletinLastUpdatedTime) {
        this.bulletinLastUpdatedTime = bulletinLastUpdatedTime;
    }

    public boolean isCourseCompleted() {
        return courseCompleted;
    }

    public void setCourseCompleted(boolean courseCompleted) {
        this.courseCompleted = courseCompleted;
    }

    public boolean isMappedAssessmentCompleted() {
        return mappedAssessmentCompleted;
    }

    public void setMappedAssessmentCompleted(boolean mappedAssessmentCompleted) {
        this.mappedAssessmentCompleted = mappedAssessmentCompleted;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}


