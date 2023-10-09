package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.room.dto.DTOCourseCard;

import java.util.Comparator;
import java.util.List;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class CourseLevelClass {
    private String levelDescription;
    private String downloadStratergy;
    private boolean hidden;
    private String levelMode;
    private String levelName;
    private long levelId;
    private int lpId;
    private long sequence;
    private List<DTOCourseCard> courseCardClassList;
    private int downloadStatus;
    private String refreshTimeStamp;
    private String levelThumbnail;
    private long totalOc;
    private long totalXp;
    private boolean levelLock = true;

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

    public List<DTOCourseCard> getCourseCardClassList() {
        return courseCardClassList;
    }

    public void setCourseCardClassList(List<DTOCourseCard> courseCardClassList) {
        this.courseCardClassList = courseCardClassList;
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

    public static Comparator<CourseLevelClass> levelSorter = (o1, o2) -> Long.compare(o1.getSequence(), o2.getSequence());

}
