package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class CreateGameRequest {
    private String topic;

    private String subject;

    private String grade;

    private boolean guestUser;

    private String opponentid;

    private String challengerid;

    private boolean rematch;

    private String moduleId;

    private String gameid;

    private int lpId;

    private String assessmentId;

    private String assessmentLanguage;
    private long courseId;
    private  long contentPlayListId;

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isGuestUser() {
        return guestUser;
    }

    public void setGuestUser(boolean guestUser) {
        this.guestUser = guestUser;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }

    public String getChallengerid() {
        return challengerid;
    }

    public void setChallengerid(String challengerid) {
        this.challengerid = challengerid;
    }

    public boolean isRematch() {
        return rematch;
    }

    public void setRematch(boolean rematch) {
        this.rematch = rematch;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getLpId() {
        return lpId;
    }

    public void setLpId(int lpId) {
        this.lpId = lpId;
    }

    public String getAssessmentLanguage() {
        return assessmentLanguage;
    }

    public void setAssessmentLanguage(String assessmentLanguage) {
        this.assessmentLanguage = assessmentLanguage;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    @Override
    public String toString() {
        return "CreateGameRequest{" +
                "topic='" + topic + '\'' +
                ", subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", guestUser=" + guestUser +
                ", opponentid='" + opponentid + '\'' +
                ", challengerid='" + challengerid + '\'' +
                ", rematch=" + rematch +
                ", moduleId='" + moduleId + '\'' +
                ", gameid='" + gameid + '\'' +
                ", lpId=" + lpId +
                ", assessmentId='" + assessmentId + '\'' +
                ", assessmentLanguage='" + assessmentLanguage + '\'' +
                ", courseId=" + courseId +
                ", contentPlayListId=" + contentPlayListId +
                '}';
    }
}
