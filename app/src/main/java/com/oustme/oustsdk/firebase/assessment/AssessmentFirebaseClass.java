package com.oustme.oustsdk.firebase.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.response.common.TabInfoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 30/05/16.
 */
@Keep
public class AssessmentFirebaseClass {
    private long priority;
    private long asssessemntId;
    private String banner;
    private String description;
    private String name;
    private String startdate;
    private String enddate;
    private String passcode;
    private String logo;
    private AssessmentType assessmentType;
    private String scope;
    private boolean active;
    private long enrolledCount;
    private long numQuestions;
    private String completionDate;
    private String addedOn;
    private long quesAttempted;
    private long rightQues;
    private long wrongQues;
    private long skippedQues;
    private boolean showQuestionsOnCompletion;
    private long score;
    private Map<String, String> modulesMap;
    private String resultMessage;
    private String bgImg;

    private boolean hideLeaderboard;
    private boolean showPercentage;
    private boolean otpVerification;
    private long totalTime;
    private boolean enrolled;
    private long enrolledTime;
    private boolean reattemptAllowed;
    private boolean locked;
    private boolean isTTSEnabled;

    private long questionXp;
    private long rewardOc;
    private long contentPlayListId;
    private long contentPlayListSlotId;
    private long contentPlayListSlotItemId;
    private long contentCompletionDeadline;
    private boolean isSecureAssessment;
    private boolean certificate;
    private List<String> rules;
    private boolean setListener;
    private String assessmentState;
    private String assessmentOverMessage;
    private boolean resumeSameQuestion;
    private boolean timePenaltyDisabled;
    private boolean backgroundAudioForAssessment;
    private boolean showAssessmentRemark;
    private boolean showAssessmentResultScore;
    private boolean disablePartialMarking;
    private long cplId;
    private long minPassCorrectAnswer;
    private long passPercentage;
    private long mappedCourseId;
    private boolean courseAssociated;
    private ArrayList<AssesmentResultBadge> assesmentResultBadges;
    private long attemptCount;
    private long noOfAttemptAllowedToPass = 0;
    private UserEventAssessmentData userEventAssessmentData;
    private boolean secureSessionOn = true;

    private long CompletionDeadline;
    private long defaultPastDeadlineCoinsPenaltyPercentage;
    private boolean showPastDeadlineModulesOnLandingPage;

    private boolean surveyAssociated, surveyMandatory;
    private long mappedSurveyId;
    private boolean recurring;
    private long frequency;
    private int surveyQuestionCount;
    private boolean conditionalFlow;
    private String conditionalFlowURL;


    //Exam Mode
    private boolean examMode;
    private long duration;

    public ArrayList<AssesmentResultBadge> getAssesmentResultBadges() {
        return assesmentResultBadges;
    }

    public void setAssesmentResultBadges(ArrayList<AssesmentResultBadge> assesmentResultBadges) {
        this.assesmentResultBadges = assesmentResultBadges;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public void initAssessmentResultBadges(ArrayList<Object> assessmentResBadgeMap) {
        if (assessmentResBadgeMap != null && assessmentResBadgeMap.size() > 0) {
            ArrayList<AssesmentResultBadge> assesmentResultBadges = new ArrayList<>();
            for (int i = 0; i < assessmentResBadgeMap.size(); i++) {
                AssesmentResultBadge assesmentResultBadge = new AssesmentResultBadge();
                Map<Object, Object> badgeMap = (Map<Object, Object>) assessmentResBadgeMap.get(i);
                assesmentResultBadge.init(badgeMap);
                assesmentResultBadges.add(assesmentResultBadge);
            }
            this.assesmentResultBadges = assesmentResultBadges;
        }
    }

    public boolean isShowAssessmentRemark() {
        return showAssessmentRemark;
    }

    public void setShowAssessmentRemark(boolean showAssessmentRemark) {
        this.showAssessmentRemark = showAssessmentRemark;
    }

    private List<CourseCardMedia> assessmentMediaList;

    public List<CourseCardMedia> getAssessmentMediaList() {
        return assessmentMediaList;
    }

    public void setAssessmentMediaList(List<CourseCardMedia> assessmentMediaList) {
        this.assessmentMediaList = assessmentMediaList;
    }

    public long getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(long passPercentage) {
        this.passPercentage = passPercentage;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public boolean isTimePenaltyDisabled() {
        return timePenaltyDisabled;
    }

    public void setTimePenaltyDisabled(boolean timePenaltyDisabled) {
        this.timePenaltyDisabled = timePenaltyDisabled;
    }

    public boolean isResumeSameQuestion() {
        return resumeSameQuestion;
    }

    public void setResumeSameQuestion(boolean resumeSameQuestion) {
        this.resumeSameQuestion = resumeSameQuestion;
    }

    public String getAssessmentOverMessage() {
        return assessmentOverMessage;
    }

    public void setAssessmentOverMessage(String assessmentOverMessage) {
        this.assessmentOverMessage = assessmentOverMessage;
    }

    public String getAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(String assessmentState) {
        this.assessmentState = assessmentState;
    }

    public long getQuestionXp() {
        return questionXp;
    }

    public void setQuestionXp(long questionXp) {
        this.questionXp = questionXp;
    }

    public long getRewardOc() {
        return rewardOc;
    }

    public void setRewardOc(long rewardOc) {
        this.rewardOc = rewardOc;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public boolean isSecureAssessment() {
        return isSecureAssessment;
    }

    public void setSecureAssessment(boolean secureAssessment) {
        isSecureAssessment = secureAssessment;
    }

    public boolean isReattemptAllowed() {
        return reattemptAllowed;
    }

    public void setReattemptAllowed(boolean reattemptAllowed) {
        this.reattemptAllowed = reattemptAllowed;
    }

    public long getAsssessemntId() {
        return asssessemntId;
    }

    public void setAsssessemntId(long asssessemntId) {
        this.asssessemntId = asssessemntId;
    }

    public String getBanner() {

        if (banner != null) {
            return banner;
        } else {
            return "";
        }

    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "";
        }

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public long getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(long numQuestions) {
        this.numQuestions = numQuestions;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLogo() {

        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Map<String, String> getModulesMap() {
        return modulesMap;
    }

    public void setModulesMap(Map<String, String> modulesMap) {
        this.modulesMap = modulesMap;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public long getQuesAttempted() {
        return quesAttempted;
    }

    public void setQuesAttempted(long quesAttempted) {
        this.quesAttempted = quesAttempted;
    }

    public long getRightQues() {
        return rightQues;
    }

    public void setRightQues(long rightQues) {
        this.rightQues = rightQues;
    }

    public long getWrongQues() {
        return wrongQues;
    }

    public void setWrongQues(long wrongQues) {
        this.wrongQues = wrongQues;
    }

    public long getSkippedQues() {
        return skippedQues;
    }

    public void setSkippedQues(long skippedQues) {
        this.skippedQues = skippedQues;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isShowQuestionsOnCompletion() {
        return showQuestionsOnCompletion;
    }

    public void setShowQuestionsOnCompletion(boolean showQuestionsOnCompletion) {
        this.showQuestionsOnCompletion = showQuestionsOnCompletion;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public boolean isHideLeaderboard() {
        return hideLeaderboard;
    }

    public void setHideLeaderboard(boolean hideLeaderboard) {
        this.hideLeaderboard = hideLeaderboard;
    }

    public boolean isShowPercentage() {
        return showPercentage;
    }

    public void setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
    }

    public boolean isOtpVerification() {
        return otpVerification;
    }

    public void setOtpVerification(boolean otpVerification) {
        this.otpVerification = otpVerification;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public long getEnrolledTime() {
        return enrolledTime;
    }

    public void setEnrolledTime(long enrolledTime) {
        this.enrolledTime = enrolledTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isTTSEnabled() {
        return isTTSEnabled;
    }

    public void setTTSEnabled(boolean TTSEnabled) {
        isTTSEnabled = TTSEnabled;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public long getContentPlayListSlotId() {
        return contentPlayListSlotId;
    }

    public void setContentPlayListSlotId(long contentPlayListSlotId) {
        this.contentPlayListSlotId = contentPlayListSlotId;
    }

    public long getContentPlayListSlotItemId() {
        return contentPlayListSlotItemId;
    }

    public void setContentPlayListSlotItemId(long contentPlayListSlotItemId) {
        this.contentPlayListSlotItemId = contentPlayListSlotItemId;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public boolean isSetListener() {
        return setListener;
    }

    public void setSetListener(boolean setListener) {
        this.setListener = setListener;
    }

    public boolean isBackgroundAudioForAssessment() {
        return backgroundAudioForAssessment;
    }

    public void setBackgroundAudioForAssessment(boolean backgroundAudioForAssessment) {
        this.backgroundAudioForAssessment = backgroundAudioForAssessment;
    }

    public boolean isDisablePartialMarking() {
        return disablePartialMarking;
    }

    public void setDisablePartialMarking(boolean disablePartialMarking) {
        this.disablePartialMarking = disablePartialMarking;
    }

    public long getMinPassCorrectAnswer() {
        return minPassCorrectAnswer;
    }

    public void setMinPassCorrectAnswer(long minPassCorrectAnswer) {
        this.minPassCorrectAnswer = minPassCorrectAnswer;
    }

    public long getNoOfAttemptAllowedToPass() {
        return noOfAttemptAllowedToPass;
    }

    public void setNoOfAttemptAllowedToPass(long noOfAttemptAllowedToPass) {
        this.noOfAttemptAllowedToPass = noOfAttemptAllowedToPass;
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

    public long getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(long attemptCount) {
        this.attemptCount = attemptCount;
    }

    public long getContentCompletionDeadline() {
        return contentCompletionDeadline;
    }

    public void setContentCompletionDeadline(long contentCompletionDeadline) {
        this.contentCompletionDeadline = contentCompletionDeadline;
    }

    public UserEventAssessmentData getUserEventAssessmentData() {
        return userEventAssessmentData;
    }

    public void setUserEventAssessmentData(UserEventAssessmentData userEventAssessmentData) {
        this.userEventAssessmentData = userEventAssessmentData;
    }

    public boolean isSecureSessionOn() {
        return secureSessionOn;
    }

    public void setSecureSessionOn(boolean secureSessionOn) {
        this.secureSessionOn = secureSessionOn;
    }

    public long getCompletionDeadline() {
        return CompletionDeadline;
    }

    public void setCompletionDeadline(long completionDeadline) {
        CompletionDeadline = completionDeadline;
    }

    public long getDefaultPastDeadlineCoinsPenaltyPercentage() {
        return defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public void setDefaultPastDeadlineCoinsPenaltyPercentage(long defaultPastDeadlineCoinsPenaltyPercentage) {
        this.defaultPastDeadlineCoinsPenaltyPercentage = defaultPastDeadlineCoinsPenaltyPercentage;
    }

    public boolean isShowPastDeadlineModulesOnLandingPage() {
        return showPastDeadlineModulesOnLandingPage;
    }

    public void setShowPastDeadlineModulesOnLandingPage(boolean showPastDeadlineModulesOnLandingPage) {
        this.showPastDeadlineModulesOnLandingPage = showPastDeadlineModulesOnLandingPage;
    }

    public boolean isSurveyAssociated() {
        return surveyAssociated;
    }

    public void setSurveyAssociated(boolean surveyAssociated) {
        this.surveyAssociated = surveyAssociated;
    }

    public long getMappedSurveyId() {
        return mappedSurveyId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }

    public boolean isSurveyMandatory() {
        return surveyMandatory;
    }

    public void setSurveyMandatory(boolean surveyMandatory) {
        this.surveyMandatory = surveyMandatory;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public boolean isExamMode() {
        return examMode;
    }

    public void setExamMode(boolean examMode) {
        this.examMode = examMode;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSurveyQuestionCount() {
        return surveyQuestionCount;
    }

    public void setSurveyQuestionCount(int surveyQuestionCount) {
        this.surveyQuestionCount = surveyQuestionCount;
    }

    public boolean isConditionalFlow() {
        return conditionalFlow;
    }

    public void setConditionalFlow(boolean conditionalFlow) {
        this.conditionalFlow = conditionalFlow;
    }

    public String getConditionalFlowURL() {
        return conditionalFlowURL;
    }

    public void setConditionalFlowURL(String conditionalFlowURL) {
        this.conditionalFlowURL = conditionalFlowURL;
    }
}
