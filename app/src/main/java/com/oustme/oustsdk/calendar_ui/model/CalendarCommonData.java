package com.oustme.oustsdk.calendar_ui.model;

public class CalendarCommonData {
    long id;
    String type;
    String name;
    long distributionTS;
    long coins;
    long xp;
    long totalOc;
    long rewardOC;
    String banner;
    String icon;
    String courseDeadLine;
    long contentCompletionDeadline;
    long contentDuration;
    long assessmentScore;
    int userCompletionPercentage;
    long startDate;
    String assessmentState;
    String userCompletionTime;
    boolean passed;
    boolean showAssessmentResultScore;
    String mode;
    long mappedCourseId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDistributionTS() {
        return distributionTS;
    }

    public void setDistributionTS(long distributionTS) {
        this.distributionTS = distributionTS;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(long rewardOC) {
        this.rewardOC = rewardOC;
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

    public String getCourseDeadLine() {
        return courseDeadLine;
    }

    public void setCourseDeadLine(String courseDeadLine) {
        this.courseDeadLine = courseDeadLine;
    }

    public long getContentCompletionDeadline() {
        return contentCompletionDeadline;
    }

    public void setContentCompletionDeadline(long contentCompletionDeadline) {
        this.contentCompletionDeadline = contentCompletionDeadline;
    }

    public long getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(long contentDuration) {
        this.contentDuration = contentDuration;
    }

    public long getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(long assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public int getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(int userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(String assessmentState) {
        this.assessmentState = assessmentState;
    }

    public String getUserCompletionTime() {
        return userCompletionTime;
    }

    public void setUserCompletionTime(String userCompletionTime) {
        this.userCompletionTime = userCompletionTime;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }
}
