package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 06/09/16.
 */
@Keep
public class CourseDataClass {
    private long courseId;
    private String courseName;
    private long numCards;
    private long numDislike;
    private long numLevels;
    private long numLike;
    private long reviewStarCount;
    private String updateTs;
    private String updateTime;
    private String language;
    private boolean removeDataAfterCourseCompletion;
    private List<CourseLevelClass> courseLevelClassList;
    private boolean autoDownload;
    private String addedOn;
    private boolean enrolled;
    private long userCompletionPercentage;
    private long weightage;
    private String icon;
    private long totalTime;
    private long contentDuration;
    private long myTotalOc;
    private long totalOc;
    private long numEnrolledUsers;
    private String notificationTitle;
    private String enrollNotificationContent;
    private String completeNotificationContent;
    private long reminderNotificationInterval;
    private String bgImg;
    private String mode;
    private String lpBgImage;
    private boolean locked;
    private boolean isCardttsEnabled;
    private boolean isQuesttsEnabled;
    private long rating;
    private boolean isDownloading;
    private long contentPlayListId;
    private long contentPlayListSlotId;
    private long contentPlayListSlotItemId;
    private boolean disableScreenShot;
    private String description;
    private boolean showQuesInReviewMode;
    private boolean disableCourseCompleteAudio;

    private long mappedAssessmentId;
    private long mappedSurveyId;
    private String mappedAssessmentName;
    private String mappedAssessmentImage;
    private long mappedAssessmentPercentage;
    private boolean assessmentEnrolled;
    private String assessmentCompletionDate;
    private boolean isCollection;

    private boolean isPurchased;
    private boolean certificate;
    private boolean enableBadge;
    private boolean playbook;
    private boolean startFromLastLevel;
    private String courseDeadline;
    private boolean listenerSet;
    private boolean zeroXpForLCard;
    private boolean zeroXpForQCard;
    private boolean fullScreenPotraitModeVideo;
    private boolean needsAck;
    private boolean acknowLedged;
    private CourseDesclaimerData courseDesclaimerData;
    private boolean salesMode;
    private Map<String, Object> cardInfo;

    private Map<String, Object> dataMap;
    private Map<String, Object> landingMap;

    private boolean hideLeaderBoard;
    private boolean hideBulletinBoard;
    private boolean disableReviewMode;
    private boolean autoPlay;
    private boolean regularMode;
    private boolean disableLevelCompletePopup = false;
    private boolean showVirtualCoins;
    private boolean disableBackButton;
    private boolean showRatingPopUp;
    private boolean showCourseCompletionPopup;
    //private boolean scormEventBased;
    private boolean mappedAssessmentEnrolled;

    private boolean enableVideoDownload;
    private boolean courseLandScapeMode;

    private long defaultPastDeadlineCoinsPenaltyPercentage;
    private boolean showPastDeadlineModulesOnLandingPage;
    private boolean showQuestionSymbolForQuestion;

    private String badgeIcon;
    private String badgeName;
    private long xp;
    private boolean showNudgeMessage;
    private boolean isSurveyMandatory;

    private String mappedSurveyName;
    private String mappedSurveyIcon;
    private String mappedSurveyDescription;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDisableCourseCompleteAudio() {
        return disableCourseCompleteAudio;
    }

    public void setDisableCourseCompleteAudio(boolean disableCourseCompleteAudio) {
        this.disableCourseCompleteAudio = disableCourseCompleteAudio;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isAutoDownload() {
        return autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public boolean isDisableReviewMode() {
        return disableReviewMode;
    }

    public boolean isShowQuesInReviewMode() {
        return showQuesInReviewMode;
    }

    public void setShowQuesInReviewMode(boolean showQuesInReviewMode) {
        this.showQuesInReviewMode = showQuesInReviewMode;
    }

    public void setDisableReviewMode(boolean disableReviewMode) {
        this.disableReviewMode = disableReviewMode;
    }

    public boolean isHideBulletinBoard() {
        return hideBulletinBoard;
    }

    public void setHideBulletinBoard(boolean hideBulletinBoard) {
        this.hideBulletinBoard = hideBulletinBoard;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(String updateTs) {
        this.updateTs = updateTs;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public boolean isRemoveDataAfterCourseCompletion() {
        return removeDataAfterCourseCompletion;
    }

    public void setRemoveDataAfterCourseCompletion(boolean removeDataAfterCourseCompletion) {
        this.removeDataAfterCourseCompletion = removeDataAfterCourseCompletion;
    }

    public List<CourseLevelClass> getCourseLevelClassList() {
        return courseLevelClassList;
    }

    public void setCourseLevelClassList(List<CourseLevelClass> courseLevelClassList) {
        this.courseLevelClassList = courseLevelClassList;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }


    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public long getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(long userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }

    public long getWeightage() {
        return weightage;
    }

    public void setWeightage(long weightage) {
        this.weightage = weightage;
    }

    public long getNumCards() {
        return numCards;
    }

    public void setNumCards(long numCards) {
        this.numCards = numCards;
    }

    public long getNumDislike() {
        return numDislike;
    }

    public void setNumDislike(long numDislike) {
        this.numDislike = numDislike;
    }

    public long getNumLevels() {
        return numLevels;
    }

    public void setNumLevels(long numLevels) {
        this.numLevels = numLevels;
    }

    public long getNumLike() {
        return numLike;
    }

    public void setNumLike(long numLike) {
        this.numLike = numLike;
    }

    public long getReviewStarCount() {
        return reviewStarCount;
    }

    public void setReviewStarCount(long reviewStarCount) {
        this.reviewStarCount = reviewStarCount;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getMyTotalOc() {
        return myTotalOc;
    }

    public void setMyTotalOc(long myTotalOc) {
        this.myTotalOc = myTotalOc;
    }

    public long getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(long totalOc) {
        this.totalOc = totalOc;
    }

    public long getNumEnrolledUsers() {
        return numEnrolledUsers;
    }

    public void setNumEnrolledUsers(long numEnrolledUsers) {
        this.numEnrolledUsers = numEnrolledUsers;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getEnrollNotificationContent() {
        return enrollNotificationContent;
    }

    public void setEnrollNotificationContent(String enrollNotificationContent) {
        this.enrollNotificationContent = enrollNotificationContent;
    }

    public String getCompleteNotificationContent() {
        return completeNotificationContent;
    }

    public void setCompleteNotificationContent(String completeNotificationContent) {
        this.completeNotificationContent = completeNotificationContent;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getLpBgImage() {
        return lpBgImage;
    }

    public void setLpBgImage(String lpBgImage) {
        this.lpBgImage = lpBgImage;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isCardttsEnabled() {
        return isCardttsEnabled;
    }

    public void setCardttsEnabled(boolean cardttsEnabled) {
        isCardttsEnabled = cardttsEnabled;
    }

    public boolean isQuesttsEnabled() {
        return isQuesttsEnabled;
    }

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
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

    public boolean isDisableScreenShot() {
        return disableScreenShot;
    }

    public void setDisableScreenShot(boolean disableScreenShot) {
        this.disableScreenShot = disableScreenShot;
    }

    public long getMappedAssessmentId() {
        return mappedAssessmentId;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }

    public long getMappedSurveyId() {
        return mappedSurveyId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }

    public boolean isAssessmentEnrolled() {
        return assessmentEnrolled;
    }

    public void setAssessmentEnrolled(boolean assessmentEnrolled) {
        this.assessmentEnrolled = assessmentEnrolled;
    }

    public String getAssessmentCompletionDate() {
        return assessmentCompletionDate;
    }

    public void setAssessmentCompletionDate(String assessmentCompletionDate) {
        this.assessmentCompletionDate = assessmentCompletionDate;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public boolean isPlaybook() {
        return playbook;
    }

    public void setPlaybook(boolean playbook) {
        this.playbook = playbook;
    }

    public boolean isStartFromLastLevel() {
        return startFromLastLevel;
    }

    public void setStartFromLastLevel(boolean startFromLastLevel) {
        this.startFromLastLevel = startFromLastLevel;
    }

    public String getCourseDeadline() {
        return courseDeadline;
    }

    public void setCourseDeadline(String courseDeadline) {
        this.courseDeadline = courseDeadline;
    }

    public boolean isListenerSet() {
        return listenerSet;
    }

    public void setListenerSet(boolean listenerSet) {
        this.listenerSet = listenerSet;
    }

    public long getReminderNotificationInterval() {
        return reminderNotificationInterval;
    }

    public void setReminderNotificationInterval(long reminderNotificationInterval) {
        this.reminderNotificationInterval = reminderNotificationInterval;
    }

    public boolean isZeroXpForLCard() {
        return zeroXpForLCard;
    }

    public void setZeroXpForLCard(boolean zeroXpForLCard) {
        this.zeroXpForLCard = zeroXpForLCard;
    }

    public boolean isZeroXpForQCard() {
        return zeroXpForQCard;
    }

    public void setZeroXpForQCard(boolean zeroXpForQCard) {
        this.zeroXpForQCard = zeroXpForQCard;
    }

    public boolean isFullScreenPotraitModeVideo() {
        return fullScreenPotraitModeVideo;
    }

    public void setFullScreenPotraitModeVideo(boolean fullScreenPotraitModeVideo) {
        this.fullScreenPotraitModeVideo = fullScreenPotraitModeVideo;
    }

    public boolean isNeedsAck() {
        return needsAck;
    }

    public void setNeedsAck(boolean needsAck) {
        this.needsAck = needsAck;
    }

    public CourseDesclaimerData getCourseDesclaimerData() {
        return courseDesclaimerData;
    }

    public void setCourseDesclaimerData(CourseDesclaimerData courseDesclaimerData) {
        this.courseDesclaimerData = courseDesclaimerData;
    }

    public boolean isAcknowLedged() {
        return acknowLedged;
    }

    public void setAcknowLedged(boolean acknowLedged) {
        this.acknowLedged = acknowLedged;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Map<String, Object> getLandingMap() {
        return landingMap;
    }

    public void setLandingMap(Map<String, Object> landingMap) {
        this.landingMap = landingMap;
    }

    public String getMappedAssessmentName() {
        return mappedAssessmentName;
    }

    public void setMappedAssessmentName(String mappedAssessmentName) {
        this.mappedAssessmentName = mappedAssessmentName;
    }

    public String getMappedAssessmentImage() {
        return mappedAssessmentImage;
    }

    public void setMappedAssessmentImage(String mappedAssessmentImage) {
        this.mappedAssessmentImage = mappedAssessmentImage;
    }

    public long getMappedAssessmentPercentage() {
        return mappedAssessmentPercentage;
    }

    public void setMappedAssessmentPercentage(long mappedAssessmentPercentage) {
        this.mappedAssessmentPercentage = mappedAssessmentPercentage;
    }

    public boolean isSalesMode() {
        return salesMode;
    }

    public void setSalesMode(boolean salesMode) {
        this.salesMode = salesMode;
    }

    public Map<String, Object> getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(Map<String, Object> cardInfo) {
        this.cardInfo = cardInfo;
    }

    public boolean isHideLeaderBoard() {
        return hideLeaderBoard;
    }

    public void setHideLeaderBoard(boolean hideLeaderBoard) {
        this.hideLeaderBoard = hideLeaderBoard;
    }

    public boolean isRegularMode() {
        return regularMode;
    }

    public void setRegularMode(boolean regularMode) {
        this.regularMode = regularMode;
    }

    public boolean isDisableLevelCompletePopup() {
        return disableLevelCompletePopup;
    }

    public void setDisableLevelCompletePopup(boolean disableLevelCompletePopup) {
        this.disableLevelCompletePopup = disableLevelCompletePopup;
    }

    public boolean isShowVirtualCoins() {
        return showVirtualCoins;
    }

    public void setShowVirtualCoins(boolean showVirtualCoins) {
        this.showVirtualCoins = showVirtualCoins;
    }

    public boolean isDisableBackButton() {
        return disableBackButton;
    }

    public void setDisableBackButton(boolean disableBackButton) {
        this.disableBackButton = disableBackButton;
    }

    public boolean isShowRatingPopUp() {
        return showRatingPopUp;
    }

    public void setShowRatingPopUp(boolean showRatingPopUp) {
        this.showRatingPopUp = showRatingPopUp;
    }

    /*public boolean isScormEventBased() {
        return scormEventBased;
    }

    public void setScormEventBased(boolean scormEventBased) {
        this.scormEventBased = scormEventBased;
    }*/

    public boolean isEnableVideoDownload() {
        return enableVideoDownload;
    }

    public void setEnableVideoDownload(boolean enableVideoDownload) {
        this.enableVideoDownload = enableVideoDownload;
    }

    public long getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(long contentDuration) {
        this.contentDuration = contentDuration;
    }

    public boolean isShowCourseCompletionPopup() {
        return showCourseCompletionPopup;
    }

    public void setShowCourseCompletionPopup(boolean showCourseCompletionPopup) {
        this.showCourseCompletionPopup = showCourseCompletionPopup;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isMappedAssessmentEnrolled() {
        return mappedAssessmentEnrolled;
    }

    public void setMappedAssessmentEnrolled(boolean mappedAssessmentEnrolled) {
        this.mappedAssessmentEnrolled = mappedAssessmentEnrolled;
    }

    public boolean isCourseLandScapeMode() {
        return courseLandScapeMode;
    }

    public void setCourseLandScapeMode(boolean courseLandScapeMode) {
        this.courseLandScapeMode = courseLandScapeMode;
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

    public boolean isShowQuestionSymbolForQuestion() {
        return showQuestionSymbolForQuestion;
    }

    public void setShowQuestionSymbolForQuestion(boolean showQuestionSymbolForQuestion) {
        this.showQuestionSymbolForQuestion = showQuestionSymbolForQuestion;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public boolean isShowNudgeMessage() {
        return showNudgeMessage;
    }

    public void setShowNudgeMessage(boolean showNudgeMessage) {
        this.showNudgeMessage = showNudgeMessage;
    }

    public boolean isEnableBadge() {
        return enableBadge;
    }

    public void setEnableBadge(boolean enableBadge) {
        this.enableBadge = enableBadge;
    }

    public boolean isSurveyMandatory() {
        return isSurveyMandatory;
    }

    public void setSurveyMandatory(boolean surveyMandatory) {
        isSurveyMandatory = surveyMandatory;
    }

    public String getMappedSurveyName() {
        return mappedSurveyName;
    }

    public void setMappedSurveyName(String mappedSurveyName) {
        this.mappedSurveyName = mappedSurveyName;
    }

    public String getMappedSurveyIcon() {
        return mappedSurveyIcon;
    }

    public void setMappedSurveyIcon(String mappedSurveyIcon) {
        this.mappedSurveyIcon = mappedSurveyIcon;
    }

    public String getMappedSurveyDescription() {
        return mappedSurveyDescription;
    }

    public void setMappedSurveyDescription(String mappedSurveyDescription) {
        this.mappedSurveyDescription = mappedSurveyDescription;
    }
}
