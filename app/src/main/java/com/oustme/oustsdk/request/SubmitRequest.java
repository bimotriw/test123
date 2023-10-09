package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.assessment.Scores;

import java.util.List;

@Keep
public class SubmitRequest {

    private String startTime;

    private List<Scores> scores;

    private String winner;

    private String endTime;

    private String gameid;

    private long totalscore;

    private String challengerid;

    private String opponentid;

    private String studentid;

    private String groupId;

    private String mobileNum;
    private String deepLink;
    private boolean external;
    private String deviceToken;
    private String eventCode;



    private String assessmentId;
    private  long contentPlayListId;
    private String courseColnId;
    private String courseId;

    private long exitOC;
    private long rewardOC;
    private long associatedToAssessmentId;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<Scores> getScores() {
        return scores;
    }

    public void setScores(List<Scores> scores) {
        this.scores = scores;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public long getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(long totalscore) {
        this.totalscore = totalscore;
    }

    public String getChallengerid() {
        return challengerid;
    }

    public void setChallengerid(String challengerid) {
        this.challengerid = challengerid;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }


    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public long getExitOC() {
        return exitOC;
    }

    public void setExitOC(long exitOC) {
        this.exitOC = exitOC;
    }

    public long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(long rewardOC) {
        this.rewardOC = rewardOC;
    }

    public long getAssociatedToAssessmentId() {
        return associatedToAssessmentId;
    }

    public void setAssociatedToAssessmentId(long associatedToAssessmentId) {
        this.associatedToAssessmentId = associatedToAssessmentId;
    }

}
