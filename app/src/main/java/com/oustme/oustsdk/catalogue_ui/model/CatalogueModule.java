package com.oustme.oustsdk.catalogue_ui.model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Comparator;

@Keep
public class CatalogueModule implements Serializable {

    String name;
    String banner;
    String icon;
    String thumbnail;
    String description;
    long contentId;
    String contentType;
    long trendingPoints;
    long numOfEnrolledUsers;
    long oustCoins;
    String viewStatus;
    long catalogueId;
    long catalogueCategoryId;
    long numOfModules;
    String distributeTS;
    String completionDateAndTime;
    double contentDuration;
    long completionPercentage;
    String mode;
    long assessmentScore;
    String state;
    boolean enrolled;
    boolean passed;
    boolean showAssessmentResultScore;
    boolean recurring;
    private long distributedId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getTrendingPoints() {
        return trendingPoints;
    }

    public void setTrendingPoints(long trendingPoints) {
        this.trendingPoints = trendingPoints;
    }

    public long getNumOfEnrolledUsers() {
        return numOfEnrolledUsers;
    }

    public void setNumOfEnrolledUsers(long numOfEnrolledUsers) {
        this.numOfEnrolledUsers = numOfEnrolledUsers;
    }

    public long getOustCoins() {
        return oustCoins;
    }

    public void setOustCoins(long oustCoins) {
        this.oustCoins = oustCoins;
    }

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    public long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public long getCatalogueCategoryId() {
        return catalogueCategoryId;
    }

    public void setCatalogueCategoryId(long catalogueCategoryId) {
        this.catalogueCategoryId = catalogueCategoryId;
    }

    public long getNumOfModules() {
        return numOfModules;
    }

    public void setNumOfModules(long numOfModules) {
        this.numOfModules = numOfModules;
    }

    public String getDistributeTS() {
        return distributeTS;
    }

    public void setDistributeTS(String distributeTS) {
        this.distributeTS = distributeTS;
    }

    public String getCompletionDateAndTime() {
        return completionDateAndTime;
    }

    public void setCompletionDateAndTime(String completionDateAndTime) {
        this.completionDateAndTime = completionDateAndTime;
    }

    public double getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(double contentDuration) {
        this.contentDuration = contentDuration;
    }

    public long getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(long completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(long assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public static Comparator<CatalogueModule> catalogueDescending = (s1, s2) -> s2.getName().compareTo(s1.getName());
    public static Comparator<CatalogueModule> catalogueAscending = (s1, s2) -> s1.getName().compareTo(s2.getName());

    public void setDistributedId(long distributedId) {
        this.distributedId = distributedId;
    }

    public long getDistributedId() {
        return distributedId;
    }
}
