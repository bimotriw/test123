package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.tools.ActiveGame;

public class AssessmentDetailModel implements Parcelable {

    private long assessmentId;
    private String title;
    private String description;
    private String image;
    private String time;
    private String questions;
    private String coins;
    private String distributionTime;
    private long deadLine;
    private String endTime;
    private String status;
    private boolean isShare;
    private boolean isInProgress;
    private int progress;
    private boolean isCompleted;
    private boolean isReAttempt;
    private boolean isCertificate;
    private String completionDate;
    private String userScore;
    private String totalScore;
    private boolean autoStartAssessment;
    String timeTaken;
    String participantsCount;
    String passScore;
    int userProgress;
    int correctAnswer;
    int wrongAnswer;
    int correctAnswerProgress;
    int wrongAnswerProgress;
    long enrolledCount;
    boolean isEnrolled;
    long noOfAttemptAllowed;
    long noOfAttemptLeft;
    boolean isPassed;
    boolean isReattemptAllowed;
    boolean isHideLeaderBoard;
    String userPercentage;
    private boolean showAssessmentResultScore;
    private boolean showAssessmentResultRemark;
    private boolean buttonEnabled = true;
    private boolean answer = false;
    private long startAssessment;
    String showPastDeadlineModulesOnLandingPage;
    private PlayResponse playResponse;
    private ActiveGame activeGame;
    private AssessmentPlayResponse assessmentPlayResponse;
    private boolean recurring;

    public AssessmentDetailModel() {
    }

    protected AssessmentDetailModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        image = in.readString();
        time = in.readString();
        questions = in.readString();
        coins = in.readString();
        distributionTime = in.readString();
        endTime = in.readString();
        status = in.readString();
        isShare =  in.readInt() == 1;
        isInProgress =  in.readInt() == 1;
        progress =  in.readInt() ;
        isCompleted =  in.readInt() == 1;
        isReAttempt =  in.readInt() == 1;
        isCertificate =  in.readInt() == 1;
        completionDate =  in.readString();
        userScore =  in.readString();
        totalScore =  in.readString();
        userPercentage = in.readString();
        timeTaken = in.readString();
        participantsCount = in.readString();
        passScore = in.readString();
        userProgress = in.readInt();
        correctAnswer = in.readInt();
        wrongAnswer = in.readInt();
        isPassed = in.readInt()==1;
        showAssessmentResultScore = in.readInt()==1;
    }

    public static final Creator<AssessmentDetailModel> CREATOR = new Creator<AssessmentDetailModel>() {
        @Override
        public AssessmentDetailModel createFromParcel(Parcel in) {
            return new AssessmentDetailModel(in);
        }

        @Override
        public AssessmentDetailModel[] newArray(int size) {
            return new AssessmentDetailModel[size];
        }
    };

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        if(image==null||image.isEmpty()){
            return "no";
        }else{
            return image;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getDistributionTime() {
        return distributionTime;
    }

    public void setDistributionTime(String distributionTime) {
        this.distributionTime = distributionTime;
    }

    public long getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(long deadLine) {
        this.deadLine = deadLine;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isReAttempt() {
        return isReAttempt;
    }

    public void setReAttempt(boolean reAttempt) {
        isReAttempt = reAttempt;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCertificate() {
        return isCertificate;
    }

    public void setCertificate(boolean certificate) {
        isCertificate = certificate;
    }

    public String getUserScore() {
        if(userScore!=null){
            return userScore;
        }else{
            return "";
        }
       // return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getTimeTaken() {
        if(timeTaken!=null){
            return timeTaken;
        }else{
            return "";
        }
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

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public String getUserPercentage() {
        return userPercentage;
    }

    public void setUserPercentage(String userPercentage) {
        this.userPercentage = userPercentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(time);
        parcel.writeString(questions);
        parcel.writeString(coins);
        parcel.writeString(distributionTime);
        parcel.writeString(endTime);
        parcel.writeString(status);
        parcel.writeInt(isShare ? 1 : 0);
        parcel.writeInt(isInProgress ? 1 : 0);
        parcel.writeInt(isCertificate ? 1 : 0);
        parcel.writeInt(progress);
        parcel.writeInt(isCompleted ? 1 : 0);
        parcel.writeInt(isReAttempt ? 1 : 0);
        parcel.writeString(completionDate);
        parcel.writeString(userScore);
        parcel.writeString(totalScore);
        parcel.writeString(userPercentage);
        parcel.writeString(timeTaken);
        parcel.writeString(participantsCount);
        parcel.writeString(passScore);
        parcel.writeInt(userProgress);
        parcel.writeInt(correctAnswer);
        parcel.writeInt(wrongAnswer);
        parcel.writeInt(isPassed ? 1 : 0);
        parcel.writeInt(showAssessmentResultScore ? 1 : 0);
    }

    public boolean isAutoStartAssessment() {
        return autoStartAssessment;
    }

    public void setAutoStartAssessment(boolean autoStartAssessment) {
        this.autoStartAssessment = autoStartAssessment;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
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

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public long getNoOfAttemptAllowed() {
        return noOfAttemptAllowed;
    }

    public void setNoOfAttemptAllowed(long noOfAttemptAllowed) {
        this.noOfAttemptAllowed = noOfAttemptAllowed;
    }

    public long getNoOfAttemptLeft() {
        return noOfAttemptLeft;
    }

    public void setNoOfAttemptLeft(long noOfAttemptLeft) {
        this.noOfAttemptLeft = noOfAttemptLeft;
    }

    public boolean isReattemptAllowed() {
        return isReattemptAllowed;
    }

    public void setReattemptAllowed(boolean reattemptAllowed) {
        isReattemptAllowed = reattemptAllowed;
    }

    public boolean isHideLeaderBoard() {
        return isHideLeaderBoard;
    }

    public void setHideLeaderBoard(boolean hideLeaderBoard) {
        isHideLeaderBoard = hideLeaderBoard;
    }

    public boolean getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    public boolean isShowAssessmentResultRemark() {
        return showAssessmentResultRemark;
    }

    public void setShowAssessmentResultRemark(boolean showAssessmentResultRemark) {
        this.showAssessmentResultRemark = showAssessmentResultRemark;
    }

    public long getStartAssessment() {
        return startAssessment;
    }

    public void setStartAssessment(long startAssessment) {
        this.startAssessment = startAssessment;
    }

    public boolean isButtonEnabled() {
        return buttonEnabled;
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        this.buttonEnabled = buttonEnabled;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public PlayResponse getPlayResponse() {
        return playResponse;
    }

    public void setPlayResponse(PlayResponse playResponse) {
        this.playResponse = playResponse;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public AssessmentPlayResponse getAssessmentPlayResponse() {
        return assessmentPlayResponse;
    }

    public void setAssessmentPlayResponse(AssessmentPlayResponse assessmentPlayResponse) {
        this.assessmentPlayResponse = assessmentPlayResponse;
    }

    public String getShowPastDeadlineModulesOnLandingPage() {
        return showPastDeadlineModulesOnLandingPage;
    }

    public void setShowPastDeadlineModulesOnLandingPage(String showPastDeadlineModulesOnLandingPage) {
        this.showPastDeadlineModulesOnLandingPage = showPastDeadlineModulesOnLandingPage;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
}
