package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by shilpysamaddar on 17/04/17.
 */

@Keep
public class AnyOpenResponse extends CommonResponse{
    private String topic;

    private String groupName;

    private String challengerAvatar;


    private String challengerId;

    private String subject;

    private String opponentID;

    private long score;

    private String challengerScore;

    private String gameid;

    private String groupId;

    private String time;

    private String challengerDisplayName;

    private String opponentDisplayName;

    private String grade;

    private String questionsData;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChallengerAvatar() {
        return challengerAvatar;
    }

    public void setChallengerAvatar(String challengerAvatar) {
        this.challengerAvatar = challengerAvatar;
    }


    public String getChallengerId() {
        return challengerId;
    }

    public void setChallengerId(String challengerId) {
        this.challengerId = challengerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOpponentID() {
        return opponentID;
    }

    public void setOpponentID(String opponentID) {
        this.opponentID = opponentID;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getChallengerScore() {
        return challengerScore;
    }

    public void setChallengerScore(String challengerScore) {
        this.challengerScore = challengerScore;
    }


    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChallengerDisplayName() {
        return challengerDisplayName;
    }

    public void setChallengerDisplayName(String challengerDisplayName) {
        this.challengerDisplayName = challengerDisplayName;
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }

    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getQuestionsData() {
        return questionsData;
    }

    public void setQuestionsData(String questionsData) {
        this.questionsData = questionsData;
    }

    @Override
    public String toString() {
        return "AnyOpenResponse{" +
                "topic='" + topic + '\'' +
                ", groupName='" + groupName + '\'' +
                ", challengerAvatar='" + challengerAvatar + '\'' +
                ", challengerId='" + challengerId + '\'' +
                ", subject='" + subject + '\'' +
                ", opponentID='" + opponentID + '\'' +
                ", score_text=" + score +
                ", challengerScore='" + challengerScore + '\'' +
                ", gameid='" + gameid + '\'' +
                ", groupId='" + groupId + '\'' +
                ", time='" + time + '\'' +
                ", challengerDisplayName='" + challengerDisplayName + '\'' +
                ", opponentDisplayName='" + opponentDisplayName + '\'' +
                ", grade='" + grade + '\'' +
                ", questionsData='" + questionsData + '\'' +
                '}';
    }
}
