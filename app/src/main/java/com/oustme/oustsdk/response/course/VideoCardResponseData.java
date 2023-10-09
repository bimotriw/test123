package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

@Keep
public class VideoCardResponseData {

    private int courseId;
    private int courseLevelId;

    private int courseCardId;
    private int xp;
    private int responseTime;
    private String cardSubmitDateTime;
    private String courseColnId;

    //Question Card
    private String userAnswer;
    private boolean correct;
    private String userSubjectiveAns;

    public int getCourseCardId() {
        return courseCardId;
    }

    public void setCourseCardId(int courseCardId) {
        this.courseCardId = courseCardId;
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

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
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

    public String getUserSubjectiveAns() {
        return userSubjectiveAns;
    }

    public void setUserSubjectiveAns(String userSubjectiveAns) {
        this.userSubjectiveAns = userSubjectiveAns;
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
}
