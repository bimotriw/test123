package com.oustme.oustsdk.assessment_ui.assessmentCompletion;

import android.os.Parcel;
import android.os.Parcelable;

import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentExtraDetails;

public class AssessmentCompletionModel implements Parcelable {

    int type;
    String status;
    String title;
    String completionDateAndTime;
    String completedMsg;
    String userScore;
    String coinsEarned;
    String totalCoins;
    String userPercentage;
    String timeTaken;
    String participantsCount;
    String passScore;
    String rating;
    int userProgress;
    int correctAnswer;
    int wrongAnswer;
    int correctAnswerProgress;
    int wrongAnswerProgress;
    boolean isPassed;
    boolean reAttemptAllowed;
    long nAttemptCount;
    long nAttemptAllowedToPass;
    long assessmentId;
    private boolean showAssessmentResultRemark;
    private boolean showAssessmentResultScore;

    AssessmentExtraDetails assessmentExtraDetails;

    public AssessmentCompletionModel() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected AssessmentCompletionModel(Parcel in) {
        status = in.readString();
        title = in.readString();
        completionDateAndTime = in.readString();
        completedMsg = in.readString();
        userScore = in.readString();
        coinsEarned = in.readString();
        totalCoins = in.readString();
        userPercentage = in.readString();
        timeTaken = in.readString();
        participantsCount = in.readString();
        passScore = in.readString();
        userProgress = in.readInt();
        correctAnswer = in.readInt();
        wrongAnswer = in.readInt();
        isPassed = in.readInt()==1;
    }

    public static final Creator<AssessmentCompletionModel> CREATOR = new Creator<AssessmentCompletionModel>() {
        @Override
        public AssessmentCompletionModel createFromParcel(Parcel in) {
            return new AssessmentCompletionModel(in);
        }

        @Override
        public AssessmentCompletionModel[] newArray(int size) {
            return new AssessmentCompletionModel[size];
        }
    };


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompletionDateAndTime() {
        return completionDateAndTime;
    }

    public void setCompletionDateAndTime(String completionDateAndTime) {
        this.completionDateAndTime = completionDateAndTime;
    }

    public String getCompletedMsg() {
        return completedMsg;
    }

    public void setCompletedMsg(String completedMsg) {
        this.completedMsg = completedMsg;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(String coinsEarned) {
        this.coinsEarned = coinsEarned;
    }

    public String getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(String totalCoins) {
        this.totalCoins = totalCoins;
    }

    public String getUserPercentage() {
        return userPercentage;
    }

    public void setUserPercentage(String userPercentage) {
        this.userPercentage = userPercentage;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(String participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public int getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(int userProgress) {
        this.userProgress = userProgress;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(status);
        dest.writeString(title);
        dest.writeString(completionDateAndTime);
        dest.writeString(completedMsg);
        dest.writeString(userScore);
        dest.writeString(coinsEarned);
        dest.writeString(totalCoins);
        dest.writeString(userPercentage);
        dest.writeString(timeTaken);
        dest.writeString(participantsCount);
        dest.writeString(passScore);
        dest.writeInt(userProgress);
        dest.writeInt(correctAnswer);
        dest.writeInt(wrongAnswer);
        dest.writeInt(isPassed ? 1 : 0);

    }

    public AssessmentExtraDetails getAssessmentExtraDetails() {
        return assessmentExtraDetails;
    }

    public void setAssessmentExtraDetails(AssessmentExtraDetails assessmentExtraDetails) {
        this.assessmentExtraDetails = assessmentExtraDetails;
    }

    public boolean isReAttemptAllowed() {
        return reAttemptAllowed;
    }

    public void setReAttemptAllowed(boolean reAttemptAllowed) {
        this.reAttemptAllowed = reAttemptAllowed;
    }

    public long getnAttemptCount() {
        return nAttemptCount;
    }

    public void setnAttemptCount(int nAttemptCount) {
        this.nAttemptCount = nAttemptCount;
    }

    public long getnAttemptAllowedToPass() {
        return nAttemptAllowedToPass;
    }

    public void setnAttemptAllowedToPass(int nAttemptAllowedToPass) {
        this.nAttemptAllowedToPass = nAttemptAllowedToPass;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }


    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(int wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }

    public int getCorrectAnswerProgress() {
        return correctAnswerProgress;
    }

    public void setCorrectAnswerProgress(int correctAnswerProgress) {
        this.correctAnswerProgress = correctAnswerProgress;
    }

    public int getWrongAnswerProgress() {
        return wrongAnswerProgress;
    }

    public void setWrongAnswerProgress(int wrongAnswerProgress) {
        this.wrongAnswerProgress = wrongAnswerProgress;
    }

    public void setnAttemptCount(long nAttemptCount) {
        this.nAttemptCount = nAttemptCount;
    }

    public void setnAttemptAllowedToPass(long nAttemptAllowedToPass) {
        this.nAttemptAllowedToPass = nAttemptAllowedToPass;
    }

    public boolean isShowAssessmentResultRemark() {
        return showAssessmentResultRemark;
    }

    public void setShowAssessmentResultRemark(boolean showAssessmentResultRemark) {
        this.showAssessmentResultRemark = showAssessmentResultRemark;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
