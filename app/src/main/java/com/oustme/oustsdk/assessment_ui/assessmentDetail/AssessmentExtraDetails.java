package com.oustme.oustsdk.assessment_ui.assessmentDetail;

public class AssessmentExtraDetails {

    private boolean isFeedComment, isCplModule, containCertificate, isFromCourse,
            comingFormOldLandingPage, isComingFromBranchIO, isEvent, isEnrolled, courseAssociated;

    private long mappedCourseID;
    private int eventId = 0;
    private int nCorrect = 0;
    private int nWrong = 0;
    private String courseId;

    private boolean reAttemptAllowed;
    private int nAttemptCount;
    private int nAttemptAllowedToPass;

    private boolean showAssessmentResultScore;

    private boolean surveyAssociated, surveyMandatory;
    private long mappedSurveyId;
    private boolean fromWorkDairy;

    public boolean isMicroCourse() {
        return isMicroCourse;
    }

    public void setMicroCourse(boolean microCourse) {
        isMicroCourse = microCourse;
    }

    private boolean isMicroCourse;

    public boolean isFeedComment() {
        return isFeedComment;
    }

    public void setFeedComment(boolean feedComment) {
        isFeedComment = feedComment;
    }

    public boolean isCplModule() {
        return isCplModule;
    }

    public void setCplModule(boolean cplModule) {
        isCplModule = cplModule;
    }

    public boolean isContainCertificate() {
        return containCertificate;
    }

    public void setContainCertificate(boolean containCertificate) {
        this.containCertificate = containCertificate;
    }

    public boolean isFromCourse() {
        return isFromCourse;
    }

    public void setFromCourse(boolean fromCourse) {
        isFromCourse = fromCourse;
    }

    public boolean isComingFormOldLandingPage() {
        return comingFormOldLandingPage;
    }

    public void setComingFormOldLandingPage(boolean comingFormOldLandingPage) {
        this.comingFormOldLandingPage = comingFormOldLandingPage;
    }

    public boolean isComingFromBranchIO() {
        return isComingFromBranchIO;
    }

    public void setComingFromBranchIO(boolean comingFromBranchIO) {
        isComingFromBranchIO = comingFromBranchIO;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    public long getMappedCourseID() {
        return mappedCourseID;
    }

    public void setMappedCourseID(long mappedCourseID) {
        this.mappedCourseID = mappedCourseID;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isCourseAssociated() {
        return courseAssociated;
    }

    public void setCourseAssociated(boolean courseAssociated) {
        this.courseAssociated = courseAssociated;
    }

    public int getnCorrect() {
        return nCorrect;
    }

    public void setnCorrect(int nCorrect) {
        this.nCorrect = nCorrect;
    }

    public int getnWrong() {
        return nWrong;
    }

    public void setnWrong(int nWrong) {
        this.nWrong = nWrong;
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

    public int getnAttemptCount() {
        return nAttemptCount;
    }

    public void setnAttemptCount(int nAttemptCount) {
        this.nAttemptCount = nAttemptCount;
    }

    public int getnAttemptAllowedToPass() {
        return nAttemptAllowedToPass;
    }

    public void setnAttemptAllowedToPass(int nAttemptAllowedToPass) {
        this.nAttemptAllowedToPass = nAttemptAllowedToPass;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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

    public boolean isFromWorkDairy() {
        return fromWorkDairy;
    }

    public void setFromWorkDairy(boolean fromWorkDairy) {
        this.fromWorkDairy = fromWorkDairy;
    }
}
