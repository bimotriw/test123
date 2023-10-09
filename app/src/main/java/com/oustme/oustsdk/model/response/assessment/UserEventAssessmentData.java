package com.oustme.oustsdk.model.response.assessment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * Created by Durai on 25/Jun/2020.
 */

@Keep
public class UserEventAssessmentData {
    private String completionDate;
    private int nTotalQuestions;
    private int nQuestionAnswered;
    private int nQuestionCorrect;
    private int nQuestionSkipped;
    private int nQuestionWrong;
    private boolean passed;
    private long score;
    private int userCompletionPercentage;
    private long eventId;
    private String userProgress;
    private long assessmentId;

    public UserEventAssessmentData(){

    }

    public UserEventAssessmentData(String completionDate, int nQuestionAnswered, int nQuestionCorrect, int nQuestionSkipped, int nQuestionWrong, boolean passed, long score, int userCompletionPercentage) {
        this.completionDate = completionDate;
        this.nQuestionAnswered = nQuestionAnswered;
        this.nQuestionCorrect = nQuestionCorrect;
        this.nQuestionSkipped = nQuestionSkipped;
        this.nQuestionWrong = nQuestionWrong;
        this.passed = passed;
        this.score = score;
        this.userCompletionPercentage = userCompletionPercentage;
    }


    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public int getnQuestionAnswered() {
        return nQuestionAnswered;
    }

    public void setnQuestionAnswered(int nQuestionAnswered) {
        this.nQuestionAnswered = nQuestionAnswered;
    }

    public int getnQuestionCorrect() {
        return nQuestionCorrect;
    }

    public void setnQuestionCorrect(int nQuestionCorrect) {
        this.nQuestionCorrect = nQuestionCorrect;
    }

    public int getnQuestionSkipped() {
        return nQuestionSkipped;
    }

    public void setnQuestionSkipped(int nQuestionSkipped) {
        this.nQuestionSkipped = nQuestionSkipped;
    }

    public int getnQuestionWrong() {
        return nQuestionWrong;
    }

    public void setnQuestionWrong(int nQuestionWrong) {
        this.nQuestionWrong = nQuestionWrong;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public int getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(int userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public int getnTotalQuestions() {
        return nTotalQuestions;
    }

    public void setnTotalQuestions(int nTotalQuestions) {
        this.nTotalQuestions = nTotalQuestions;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(String userProgress) {
        this.userProgress = userProgress;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }
}
