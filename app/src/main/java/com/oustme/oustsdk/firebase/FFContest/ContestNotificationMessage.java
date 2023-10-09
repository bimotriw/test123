package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/08/17.
 */

@Keep
public class ContestNotificationMessage {
    private String greater24Message;
    private String greatehourMessage;
    private String lastMinuteMessage;
    private String lesshourMessage;
    private long lesshourTime;
    private long greater24Time;
    private long greatehourTime;
    private long lastMinuteTime;
    private String banner;
    private String joinBanner;
    private String playBanner;
    private String rrBanner;
    private long startTime;
    private String studentKey;
    private long contestID;
    private long lastNotificationShownTime;
    private String contestName;
    private String contestStartMessage;
    private String LBReadyMessage;
    private long leaderboardNotificationTime;
    private boolean isLBNotificationShown;

    private long totalContestTime;
    private long questionTime;
    private String studentId;
    private String displayName;
    private String avatar;
    private long luckyWinnerCorrectCount;
    private boolean isRegistered;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getGreater24Message() {
        return greater24Message;
    }

    public void setGreater24Message(String greater24Message) {
        this.greater24Message = greater24Message;
    }

    public String getGreatehourMessage() {
        return greatehourMessage;
    }

    public void setGreatehourMessage(String greatehourMessage) {
        this.greatehourMessage = greatehourMessage;
    }

    public String getLastMinuteMessage() {
        return lastMinuteMessage;
    }

    public void setLastMinuteMessage(String lastMinuteMessage) {
        this.lastMinuteMessage = lastMinuteMessage;
    }

    public String getLesshourMessage() {
        return lesshourMessage;
    }

    public void setLesshourMessage(String lesshourMessage) {
        this.lesshourMessage = lesshourMessage;
    }

    public long getLesshourTime() {
        return lesshourTime;
    }

    public void setLesshourTime(long lesshourTime) {
        this.lesshourTime = lesshourTime;
    }

    public long getGreater24Time() {
        return greater24Time;
    }

    public void setGreater24Time(long greater24Time) {
        this.greater24Time = greater24Time;
    }

    public long getGreatehourTime() {
        return greatehourTime;
    }

    public void setGreatehourTime(long greatehourTime) {
        this.greatehourTime = greatehourTime;
    }

    public long getLastMinuteTime() {
        return lastMinuteTime;
    }

    public void setLastMinuteTime(long lastMinuteTime) {
        this.lastMinuteTime = lastMinuteTime;
    }

    public String getJoinBanner() {
        return joinBanner;
    }

    public void setJoinBanner(String joinBanner) {
        this.joinBanner = joinBanner;
    }

    public String getPlayBanner() {
        return playBanner;
    }

    public void setPlayBanner(String playBanner) {
        this.playBanner = playBanner;
    }

    public String getRrBanner() {
        return rrBanner;
    }

    public void setRrBanner(String rrBanner) {
        this.rrBanner = rrBanner;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }


    public long getLastNotificationShownTime() {
        return lastNotificationShownTime;
    }

    public void setLastNotificationShownTime(long lastNotificationShownTime) {
        this.lastNotificationShownTime = lastNotificationShownTime;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getContestStartMessage() {
        return contestStartMessage;
    }

    public void setContestStartMessage(String contestStartMessage) {
        this.contestStartMessage = contestStartMessage;
    }

    public String getLBReadyMessage() {
        return LBReadyMessage;
    }

    public void setLBReadyMessage(String LBReadyMessage) {
        this.LBReadyMessage = LBReadyMessage;
    }

    public long getLeaderboardNotificationTime() {
        return leaderboardNotificationTime;
    }

    public void setLeaderboardNotificationTime(long leaderboardNotificationTime) {
        this.leaderboardNotificationTime = leaderboardNotificationTime;
    }

    public boolean isLBNotificationShown() {
        return isLBNotificationShown;
    }

    public void setLBNotificationShown(boolean LBNotificationShown) {
        isLBNotificationShown = LBNotificationShown;
    }

    public long getContestID() {
        return contestID;
    }

    public void setContestID(long contestID) {
        this.contestID = contestID;
    }

    public long getTotalContestTime() {
        return totalContestTime;
    }

    public void setTotalContestTime(long totalContestTime) {
        this.totalContestTime = totalContestTime;
    }

    public long getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(long questionTime) {
        this.questionTime = questionTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getLuckyWinnerCorrectCount() {
        return luckyWinnerCorrectCount;
    }

    public void setLuckyWinnerCorrectCount(long luckyWinnerCorrectCount) {
        this.luckyWinnerCorrectCount = luckyWinnerCorrectCount;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }
}
