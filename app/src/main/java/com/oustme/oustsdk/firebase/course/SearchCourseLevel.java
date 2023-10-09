package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class SearchCourseLevel {
    private long id;
    private String name;
    private String description;
    private List<SearchCourseCard> searchCourseCards;
    private boolean searchMode;
    private String refreshTimeStamp;
    private long sequence;
    private int lpId;
    private long totalOc;
    private long xp;
    private boolean levelLock;

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public int getLpId() {
        return lpId;
    }

    public void setLpId(int lpId) {
        this.lpId = lpId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SearchCourseCard> getSearchCourseCards() {
        return searchCourseCards;
    }

    public void setSearchCourseCards(List<SearchCourseCard> searchCourseCards) {
        this.searchCourseCards = searchCourseCards;
    }

    public boolean isSearchMode() {
        return searchMode;
    }

    public void setSearchMode(boolean searchMode) {
        this.searchMode = searchMode;
    }

    public String getRefreshTimeStamp() {
        return refreshTimeStamp;
    }

    public void setRefreshTimeStamp(String refreshTimeStamp) {
        this.refreshTimeStamp = refreshTimeStamp;
    }

    public boolean isLevelLock() {
        return levelLock;
    }

    public void setLevelLock(boolean levelLock) {
        this.levelLock = levelLock;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }
}
