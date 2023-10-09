package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class UserLevelData {
    private long totalTime;
    private long totalOc;
    private long levelId;
    private long sequece;
    private long xp;
    private String timeStamp;
    private List<UserCardData> userCardDataList;
    private boolean isDownloading=false;
    private int completePercentage;
    private int currentCardNo;
    private boolean locked;
    private boolean downloadedAllCards;

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public long getLevelId() {
        return levelId;
    }

    public void setLevelId(long levelId) {
        this.levelId = levelId;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public List<UserCardData> getUserCardDataList() {
        return userCardDataList;
    }

    public void setUserCardDataList(List<UserCardData> userCardDataList) {
        this.userCardDataList = userCardDataList;
    }

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public long getSequece() {
        return sequece;
    }

    public void setSequece(long sequece) {
        this.sequece = sequece;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getCompletePercentage() {
        return completePercentage;
    }

    public void setCompletePercentage(int completePercentage) {
        this.completePercentage = completePercentage;
    }

    public int getCurrentCardNo() {
        return currentCardNo;
    }

    public void setCurrentCardNo(int currentCardNo) {
        this.currentCardNo = currentCardNo;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isDownloadedAllCards() {
        return downloadedAllCards;
    }

    public void setDownloadedAllCards(boolean downloadedAllCards) {
        this.downloadedAllCards = downloadedAllCards;
    }
}
