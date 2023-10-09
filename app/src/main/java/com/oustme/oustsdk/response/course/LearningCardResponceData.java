package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class LearningCardResponceData {
    private int courseCardId;
    private int courseId;
    private int courseLevelId;
    //    private int ocWon;
    private int xp;
    private int responseTime;
    private String cardSubmitDateTime;
    private String courseColnId;

    //Question Card
    private String userAnswer;
    private boolean correct;
    private String userSubjectiveAns;
    private String cardType;


    private long userVideoViewInterval;
    private long videoTotalTimeInterval;
    private String videoCompletionPercentage;
    private boolean cardCompleted;
    private long cardViewInterval;
    private boolean levelCompleted;
    private long levelUpdateTimeStampOfApp;


    //private List<VideoCardResponseData> videoCardResponseData;

    public List<LearningCardResponceData> getListNestedVideoQuestion() {
        return listNestedVideoQuestion;
    }

    public void setListNestedVideoQuestion(List<LearningCardResponceData> listNestedVideoQuestion) {
        this.listNestedVideoQuestion = listNestedVideoQuestion;
    }

    private List<LearningCardResponceData> listNestedVideoQuestion;

    public int getCourseCardId() {
        return courseCardId;
    }

    public void setCourseCardId(int courseCardId) {
        this.courseCardId = courseCardId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCourseLevelId() {
        return courseLevelId;
    }

    public void setCourseLevelId(int courseLevelId) {
        this.courseLevelId = courseLevelId;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public String getCardSubmitDateTime() {
        return cardSubmitDateTime;
    }

    public void setCardSubmitDateTime(String cardSubmitDateTime) {
        this.cardSubmitDateTime = cardSubmitDateTime;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    public String getUserSubjectiveAns() {
        return userSubjectiveAns;
    }

    public void setUserSubjectiveAns(String userSubjectiveAns) {
        this.userSubjectiveAns = userSubjectiveAns;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public long getUserVideoViewInterval() {
        return userVideoViewInterval;
    }

    public void setUserVideoViewInterval(long userVideoViewInterval) {
        this.userVideoViewInterval = userVideoViewInterval;
    }

    public long getVideoTotalTimeInterval() {
        return videoTotalTimeInterval;
    }

    public void setVideoTotalTimeInterval(long videoTotalTimeInterval) {
        this.videoTotalTimeInterval = videoTotalTimeInterval;
    }

    public String getVideoCompletionPercentage() {
        return videoCompletionPercentage;
    }

    public void setVideoCompletionPercentage(String videoCompletionPercentage) {
        this.videoCompletionPercentage = videoCompletionPercentage;
    }

    public boolean isCardCompleted() {
        return cardCompleted;
    }

    public void setCardCompleted(boolean cardCompleted) {
        this.cardCompleted = cardCompleted;
    }

    public long getCardViewInterval() {
        return cardViewInterval;
    }

    public void setCardViewInterval(long cardViewInterval) {
        this.cardViewInterval = cardViewInterval;
    }

    /*public List<VideoCardResponseData> getVideoCardResponseData() {
        return videoCardResponseData;
    }

    public void setVideoCardResponseData(List<VideoCardResponseData> videoCardResponseData) {
        this.videoCardResponseData = videoCardResponseData;
    }*/

    public boolean isLevelCompleted() {
        return levelCompleted;
    }

    public void setLevelCompleted(boolean levelCompleted) {
        this.levelCompleted = levelCompleted;
    }

    public long getLevelUpdateTimeStampOfApp() {
        return levelUpdateTimeStampOfApp;
    }

    public void setLevelUpdateTimeStampOfApp(long levelUpdateTimeStampOfApp) {
        this.levelUpdateTimeStampOfApp = levelUpdateTimeStampOfApp;
    }
}
