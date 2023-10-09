package com.oustme.oustsdk.survey_module.model;

import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.room.dto.DTOCourseCard;

public class SurveyModule {

    private boolean active;
    private boolean addBucketsToAssessment;
    private long assessmentId;
    private boolean assessmentRatings;
    private long assessmentScore;
    private boolean assessmentWeightage;
    private boolean autoAssignToNewUser;
    private boolean backgroundAudioForAssessment;
    private String banner;
    private String bgImg;
    private boolean certificate;
    private String completeReminderNotificationContent;
    private long completeReminderNotificationInterval;
    private long contentCompletionDeadline;
    private String completionDeadline;
    private long contentDuration;
    private long contentSize;
    private boolean courseAssociated;
    private long cplId;
    private boolean createEvent;
    private long defaultPastDeadlineCoinsPenaltyPercentage;
    private String description;
    private DTOCourseCard descriptionCard;
    private long distributionTS;
    private long endDate;
    private long enrolledCount;
    private long exitOC;
    private boolean hidden;
    private boolean hideAssessmentName;
    private boolean hideLeaderboard;
    private String icon;
    private long introCardId;
    private String logo;
    private long mappedCourseId;
    private long minPassCorrectAnswer;
    private String name;
    private boolean negativeMarkingAllowed;
    private long noOfAttemptAllowedToPass;
    private String notificationBeforeStartContent;
    private long notificationTimeBeforeStart;
    private long numQuestions;
    private boolean otpVerification;
    private long oustCoins;
    private long passPercentage;
    private boolean passed;
    private long questionXp;
    private boolean randomizeQuestion;
    private boolean reattemptAllowed;
    private boolean resumeSameQuestion;
    private long rewardOC;
    private String scope;
    private String scoreType;
    private boolean secured;
    private boolean showAssessmentRemark;
    private boolean showAssessmentResultScore;
    private boolean showNavigationArrows;
    private boolean showPastDeadlineModulesOnLandingPage;
    private boolean showPercentage;
    private boolean showQuestionsOnCompletion;
    private long startDate;
    private String state;
    private boolean timePenaltyDisabled;
    private String type;
    private long upperLimit;
    private long weightage;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAddBucketsToAssessment() {
        return addBucketsToAssessment;
    }

    public void setAddBucketsToAssessment(boolean addBucketsToAssessment) {
        this.addBucketsToAssessment = addBucketsToAssessment;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public boolean isAssessmentRatings() {
        return assessmentRatings;
    }

    public void setAssessmentRatings(boolean assessmentRatings) {
        this.assessmentRatings = assessmentRatings;
    }

    public long getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(long assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public boolean isAssessmentWeightage() {
        return assessmentWeightage;
    }

    public void setAssessmentWeightage(boolean assessmentWeightage) {
        this.assessmentWeightage = assessmentWeightage;
    }

    public boolean isAutoAssignToNewUser() {
        return autoAssignToNewUser;
    }

    public void setAutoAssignToNewUser(boolean autoAssignToNewUser) {
        this.autoAssignToNewUser = autoAssignToNewUser;
    }

    public boolean isBackgroundAudioForAssessment() {
        return backgroundAudioForAssessment;
    }

    public void setBackgroundAudioForAssessment(boolean backgroundAudioForAssessment) {
        this.backgroundAudioForAssessment = backgroundAudioForAssessment;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public String getCompleteReminderNotificationContent() {
        return completeReminderNotificationContent;
    }

    public void setCompleteReminderNotificationContent(String completeReminderNotificationContent) {
        this.completeReminderNotificationContent = completeReminderNotificationContent;
    }

    public long getCompleteReminderNotificationInterval() {
        return completeReminderNotificationInterval;
    }

    public void setCompleteReminderNotificationInterval(long completeReminderNotificationInterval) {
        this.completeReminderNotificationInterval = completeReminderNotificationInterval;
    }

    public long getContentCompletionDeadline() {
        return contentCompletionDeadline;
    }

    public void setContentCompletionDeadline(long contentCompletionDeadline) {
        this.contentCompletionDeadline = contentCompletionDeadline;
    }

    public String getCompletionDeadline() {
        return completionDeadline;
    }

    public void setCompletionDeadline(String completionDeadline) {
        this.completionDeadline = completionDeadline;
    }

    public long getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(long contentDuration) {
        this.contentDuration = contentDuration;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public boolean isCourseAssociated() {
        return courseAssociated;
    }

    public void setCourseAssociated(boolean courseAssociated) {
        this.courseAssociated = courseAssociated;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public boolean isCreateEvent() {
        return createEvent;
    }

    public void setCreateEvent(boolean createEvent) {
        this.createEvent = createEvent;
    }

    public long getDefaultPastDeadlineCoinsPenaltyPercentage() {
        return defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public void setDefaultPastDeadlineCoinsPenaltyPercentage(long defaultPastDeadlineCoinsPenaltyPercentage) {
        this.defaultPastDeadlineCoinsPenaltyPercentage = defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DTOCourseCard getDescriptionCard() {
        return descriptionCard;
    }

    public void setDescriptionCard(DTOCourseCard descriptionCard) {
        this.descriptionCard = descriptionCard;
    }

    public long getDistributionTS() {
        return distributionTS;
    }

    public void setDistributionTS(long distributionTS) {
        this.distributionTS = distributionTS;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public long getExitOC() {
        return exitOC;
    }

    public void setExitOC(long exitOC) {
        this.exitOC = exitOC;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHideAssessmentName() {
        return hideAssessmentName;
    }

    public void setHideAssessmentName(boolean hideAssessmentName) {
        this.hideAssessmentName = hideAssessmentName;
    }

    public boolean isHideLeaderboard() {
        return hideLeaderboard;
    }

    public void setHideLeaderboard(boolean hideLeaderboard) {
        this.hideLeaderboard = hideLeaderboard;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getIntroCardId() {
        return introCardId;
    }

    public void setIntroCardId(long introCardId) {
        this.introCardId = introCardId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getMappedCourseId() {
        return mappedCourseId;
    }

    public void setMappedCourseId(long mappedCourseId) {
        this.mappedCourseId = mappedCourseId;
    }

    public long getMinPassCorrectAnswer() {
        return minPassCorrectAnswer;
    }

    public void setMinPassCorrectAnswer(long minPassCorrectAnswer) {
        this.minPassCorrectAnswer = minPassCorrectAnswer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNegativeMarkingAllowed() {
        return negativeMarkingAllowed;
    }

    public void setNegativeMarkingAllowed(boolean negativeMarkingAllowed) {
        this.negativeMarkingAllowed = negativeMarkingAllowed;
    }

    public long getNoOfAttemptAllowedToPass() {
        return noOfAttemptAllowedToPass;
    }

    public void setNoOfAttemptAllowedToPass(long noOfAttemptAllowedToPass) {
        this.noOfAttemptAllowedToPass = noOfAttemptAllowedToPass;
    }

    public String getNotificationBeforeStartContent() {
        return notificationBeforeStartContent;
    }

    public void setNotificationBeforeStartContent(String notificationBeforeStartContent) {
        this.notificationBeforeStartContent = notificationBeforeStartContent;
    }

    public long getNotificationTimeBeforeStart() {
        return notificationTimeBeforeStart;
    }

    public void setNotificationTimeBeforeStart(long notificationTimeBeforeStart) {
        this.notificationTimeBeforeStart = notificationTimeBeforeStart;
    }

    public long getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(long numQuestions) {
        this.numQuestions = numQuestions;
    }

    public boolean isOtpVerification() {
        return otpVerification;
    }

    public void setOtpVerification(boolean otpVerification) {
        this.otpVerification = otpVerification;
    }

    public long getOustCoins() {
        return oustCoins;
    }

    public void setOustCoins(long oustCoins) {
        this.oustCoins = oustCoins;
    }

    public long getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(long passPercentage) {
        this.passPercentage = passPercentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getQuestionXp() {
        return questionXp;
    }

    public void setQuestionXp(long questionXp) {
        this.questionXp = questionXp;
    }

    public boolean isRandomizeQuestion() {
        return randomizeQuestion;
    }

    public void setRandomizeQuestion(boolean randomizeQuestion) {
        this.randomizeQuestion = randomizeQuestion;
    }

    public boolean isReattemptAllowed() {
        return reattemptAllowed;
    }

    public void setReattemptAllowed(boolean reattemptAllowed) {
        this.reattemptAllowed = reattemptAllowed;
    }

    public boolean isResumeSameQuestion() {
        return resumeSameQuestion;
    }

    public void setResumeSameQuestion(boolean resumeSameQuestion) {
        this.resumeSameQuestion = resumeSameQuestion;
    }

    public long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(long rewardOC) {
        this.rewardOC = rewardOC;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public boolean isShowAssessmentRemark() {
        return showAssessmentRemark;
    }

    public void setShowAssessmentRemark(boolean showAssessmentRemark) {
        this.showAssessmentRemark = showAssessmentRemark;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public boolean isShowNavigationArrows() {
        return showNavigationArrows;
    }

    public void setShowNavigationArrows(boolean showNavigationArrows) {
        this.showNavigationArrows = showNavigationArrows;
    }

    public boolean isShowPastDeadlineModulesOnLandingPage() {
        return showPastDeadlineModulesOnLandingPage;
    }

    public void setShowPastDeadlineModulesOnLandingPage(boolean showPastDeadlineModulesOnLandingPage) {
        this.showPastDeadlineModulesOnLandingPage = showPastDeadlineModulesOnLandingPage;
    }

    public boolean isShowPercentage() {
        return showPercentage;
    }

    public void setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
    }

    public boolean isShowQuestionsOnCompletion() {
        return showQuestionsOnCompletion;
    }

    public void setShowQuestionsOnCompletion(boolean showQuestionsOnCompletion) {
        this.showQuestionsOnCompletion = showQuestionsOnCompletion;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isTimePenaltyDisabled() {
        return timePenaltyDisabled;
    }

    public void setTimePenaltyDisabled(boolean timePenaltyDisabled) {
        this.timePenaltyDisabled = timePenaltyDisabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(long upperLimit) {
        this.upperLimit = upperLimit;
    }

    public long getWeightage() {
        return weightage;
    }

    public void setWeightage(long weightage) {
        this.weightage = weightage;
    }
}
