package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;

import java.util.List;

@Keep
public class AdaptiveCourseLevelModel{
    private String levelDescription;
    private String downloadStratergy;
    private boolean hidden;
    private String levelMode;
    private String levelName;
    private long levelId;
    private int lpId;
    private long sequence;
    private List<DTOAdaptiveCardDataModel> cardsList;
    private int downloadStatus;
    private String refreshTimeStamp;
    private String levelThumbnail;
    private long totalOc;
    private long totalXp;
    private boolean levelLock;

    public AdaptiveCourseLevelModel() {
    }

    public boolean isLevelLock() {
        return levelLock;
    }

    public void setLevelLock(boolean levelLock) {
        this.levelLock = levelLock;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getDownloadStratergy() {
        return downloadStratergy;
    }

    public void setDownloadStratergy(String downloadStratergy) {
        this.downloadStratergy = downloadStratergy;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getLevelMode() {
        return levelMode;
    }

    public void setLevelMode(String levelMode) {
        this.levelMode = levelMode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public List<DTOAdaptiveCardDataModel> getCourseCardClassList() {
        return cardsList;
    }

    public void setCourseCardClassList(List<DTOAdaptiveCardDataModel> courseCardClassList) {
        this.cardsList = courseCardClassList;
    }

    public long getLevelId() {
        return levelId;
    }

    public void setLevelId(long levelId) {
        this.levelId = levelId;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getRefreshTimeStamp() {
        return refreshTimeStamp;
    }

    public void setRefreshTimeStamp(String refreshTimeStamp) {
        this.refreshTimeStamp = refreshTimeStamp;
    }

    public int getLpId() {
        return lpId;
    }

    public void setLpId(int lpId) {
        this.lpId = lpId;
    }

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public String getLevelThumbnail() {
        return levelThumbnail;
    }

    public void setLevelThumbnail(String levelThumbnail) {
        this.levelThumbnail = levelThumbnail;
    }

    public long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;
    }


}
