package com.oustme.oustsdk.question_module.model;

import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;

import java.util.ArrayList;

public class QuestionBaseModel {

    String moduleName;
    String bgImage;
    int questionIndex;
    int totalCards;
    boolean showNavigationArrow;
    boolean isSurvey;
    boolean isSurveySubmitted;
    boolean isAssessment;
    boolean isReviewMode;
    DTOQuestions questions;
    int type;
    DTOCourseCard courseCardClass;
    CourseLevelClass courseLevelClass;
    Scores scores;
    String answerType;
    ArrayList<QuestionAnswerModel> answerModels = new ArrayList<>();
    boolean secureSessionOn;
    boolean containCertificate;
    float totalTimeTaken = 0;
    boolean isCplModule;
    boolean isFromCourse;
    long mappedCourseId;
    boolean courseAssociated;
    boolean showAssessmentResultScore;
    boolean reAttemptAllowed;
    long nAttemptCount;
    long nAttemptAllowedToPass;
    long courseId;
    ActiveUser activeUser;
    ActiveGame activeGame;
    SubmitRequest submitRequest;
    GamePoints gamePoints;
    long questionResumeTime;
    boolean showLoader;
    boolean surveyAssociated;
    boolean surveyMandatory;
    long mappedSurveyId;
    String bannerImage;
    long duration;
    boolean examMode;
    boolean timerEnd;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(int totalCards) {
        this.totalCards = totalCards;
    }

    public boolean isShowNavigationArrow() {
        return showNavigationArrow;
    }

    public void setShowNavigationArrow(boolean showNavigationArrow) {
        this.showNavigationArrow = showNavigationArrow;
    }

    public boolean isSurvey() {
        return isSurvey;
    }

    public void setSurvey(boolean survey) {
        isSurvey = survey;
    }

    public boolean isAssessment() {
        return isAssessment;
    }

    public void setAssessment(boolean assessment) {
        isAssessment = assessment;
    }

    public boolean isReviewMode() {
        return isReviewMode;
    }

    public void setReviewMode(boolean reviewMode) {
        isReviewMode = reviewMode;
    }

    public DTOQuestions getQuestions() {
        return questions;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DTOCourseCard getCourseCardClass() {
        return courseCardClass;
    }

    public void setCourseCardClass(DTOCourseCard courseCardClass) {
        this.courseCardClass = courseCardClass;
    }

    public CourseLevelClass getCourseLevelClass() {
        return courseLevelClass;
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public ArrayList<QuestionAnswerModel> getAnswerModels() {
        return answerModels;
    }

    public void setAnswerModels(ArrayList<QuestionAnswerModel> answerModels) {
        this.answerModels = answerModels;
    }

    public boolean isSurveySubmitted() {
        return isSurveySubmitted;
    }

    public void setSurveySubmitted(boolean surveySubmitted) {
        isSurveySubmitted = surveySubmitted;
    }

    public boolean isSecureSessionOn() {
        return secureSessionOn;
    }

    public void setSecureSessionOn(boolean secureSessionOn) {
        this.secureSessionOn = secureSessionOn;
    }

    public boolean isContainCertificate() {
        return containCertificate;
    }

    public void setContainCertificate(boolean containCertificate) {
        this.containCertificate = containCertificate;
    }

    public float getTotalTimeTaken() {
        return totalTimeTaken;
    }

    public void setTotalTimeTaken(float totalTimeTaken) {
        this.totalTimeTaken = totalTimeTaken;
    }

    public boolean isCplModule() {
        return isCplModule;
    }

    public void setCplModule(boolean cplModule) {
        isCplModule = cplModule;
    }

    public boolean isFromCourse() {
        return isFromCourse;
    }

    public void setFromCourse(boolean fromCourse) {
        isFromCourse = fromCourse;
    }

    public long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public boolean isCourseAssociated() {
        return courseAssociated;
    }

    public void setCourseAssociated(boolean courseAssociated) {
        this.courseAssociated = courseAssociated;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
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

    public void setnAttemptCount(long nAttemptCount) {
        this.nAttemptCount = nAttemptCount;
    }

    public long getnAttemptAllowedToPass() {
        return nAttemptAllowedToPass;
    }

    public void setnAttemptAllowedToPass(long nAttemptAllowedToPass) {
        this.nAttemptAllowedToPass = nAttemptAllowedToPass;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public SubmitRequest getSubmitRequest() {
        return submitRequest;
    }

    public void setSubmitRequest(SubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
    }

    public GamePoints getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(GamePoints gamePoints) {
        this.gamePoints = gamePoints;
    }

    public long getQuestionResumeTime() {
        return questionResumeTime;
    }

    public void setQuestionResumeTime(long questionResumeTime) {
        this.questionResumeTime = questionResumeTime;
    }

    public boolean isShowLoader() {
        return showLoader;
    }

    public void setShowLoader(boolean showLoader) {
        this.showLoader = showLoader;
    }

    public boolean isSurveyAssociated() {
        return surveyAssociated;
    }

    public void setSurveyAssociated(boolean surveyAssociated) {
        this.surveyAssociated = surveyAssociated;
    }

    public boolean isSurveyMandatory() {
        return surveyMandatory;
    }

    public void setSurveyMandatory(boolean surveyMandatory) {
        this.surveyMandatory = surveyMandatory;
    }

    public long getMappedSurveyId() {
        return mappedSurveyId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isExamMode() {
        return examMode;
    }

    public void setExamMode(boolean examMode) {
        this.examMode = examMode;
    }

    public boolean isTimerEnd() {
        return timerEnd;
    }

    public void setTimerEnd(boolean timerEnd) {
        this.timerEnd = timerEnd;
    }
}
