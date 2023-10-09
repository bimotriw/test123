package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.firebase.course.CourseDesclaimerData;

import java.util.List;
import java.util.Map;

@Keep
public class AdaptiveCourseDataModel{
    private long courseId;
    private String courseName;
    private long numCards;
    private long numDislike;
    private long numLevels;
    private long numLike;
    private long reviewStarCount;
    private String updateTs;
    private String language;
    private boolean removeDataAfterCourseCompletion;
    private List<AdaptiveCourseLevelModel> levelsData;
    private boolean autoDownload;
    private String addedOn;
    private boolean enrolled;
    private long userCompletionPercentage;
    private long weightage;
    private String icon;
    private long totalTime;
    private long myTotalOc;
    private long totalOc;
    private long numEnrolledUsers;
    private String notificationTitle;
    private String enrollNotificationContent;
    private String completeNotificationContent;
    private long reminderNotificationInterval;
    private String bgImg;
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
    private String mappedAssessmentName;
    private boolean assessmentEnrolled;
    private String assessmentCompletionDate;
    private boolean isCollection;

    private boolean isPurchased;
    private boolean certificate;
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

    private Map<String,Object> dataMap;
    private Map<String,Object> landingMap;

    private boolean hideLeaderBoard;
    private boolean hideBulletinBoard;
    private boolean disableReviewMode;
    private boolean autoPlay;
    private int numOfUniqueCards;
    private String elementStartDateTime;
    private String elementEndDateTime;
    private String elementCreationDateTime;
    private long courseTime;
    private String courseCategory;
    private long likeCount;
    private long unLikeCount;
    private long rewardOC;
    private long xp;
    private String userCompletionTime;
    private long cplId;
    private String enrollReminderNotificationContent;
    private String completeReminderNotificationContent;
    private String lpBgImageNew;
    private String[] tips;
    private long sequence;
    private boolean isTTSEnabledLC;
    private boolean isTTSEnabledQC;
    private MappedAssessmentDetails mappedAssessmentDetails;
    private boolean autoDelete;
    private long autoDeleteTimeStamp;
    private boolean autoModuleCreation;
    private String updateTime;
    private String ackPopup;
    private long introCardId;
    private String descriptionCard;
    private boolean hideLeaderboard;
    private long price;
    private String multilangCourseDataList;
    private List<Integer> courseIdList;
    private String courseType;
    private long parentCourseId;
    private long langId;
    private String courseTags;
    private boolean courseLandScapeMode;
    private boolean courseCompletionPercentage;
    private boolean autoMoveToNext;
    private boolean ttsenabledLC;
    private boolean ttsenabledQC;

    public AdaptiveCourseDataModel() {
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

    public List<AdaptiveCourseLevelModel> getCourseLevelClassList() {
        return levelsData;
    }

    public void setCourseLevelClassList(List<AdaptiveCourseLevelModel> courseLevelClassList) {
        this.levelsData = courseLevelClassList;
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

    public List<AdaptiveCourseLevelModel> getLevels() {
        return levelsData;
    }

    public void setLevels(List<AdaptiveCourseLevelModel> levels) {
        this.levelsData = levels;
    }

    public int getNumOfUniqueCards() {
        return numOfUniqueCards;
    }

    public void setNumOfUniqueCards(int numOfUniqueCards) {
        this.numOfUniqueCards = numOfUniqueCards;
    }

    public String getElementStartDateTime() {
        return elementStartDateTime;
    }

    public void setElementStartDateTime(String elementStartDateTime) {
        this.elementStartDateTime = elementStartDateTime;
    }

    public String getElementEndDateTime() {
        return elementEndDateTime;
    }

    public void setElementEndDateTime(String elementEndDateTime) {
        this.elementEndDateTime = elementEndDateTime;
    }

    public String getElementCreationDateTime() {
        return elementCreationDateTime;
    }

    public void setElementCreationDateTime(String elementCreationDateTime) {
        this.elementCreationDateTime = elementCreationDateTime;
    }

    public long getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(long courseTime) {
        this.courseTime = courseTime;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getUnLikeCount() {
        return unLikeCount;
    }

    public void setUnLikeCount(long unLikeCount) {
        this.unLikeCount = unLikeCount;
    }

    public long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(long rewardOC) {
        this.rewardOC = rewardOC;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public String getUserCompletionTime() {
        return userCompletionTime;
    }

    public void setUserCompletionTime(String userCompletionTime) {
        this.userCompletionTime = userCompletionTime;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public String getEnrollReminderNotificationContent() {
        return enrollReminderNotificationContent;
    }

    public void setEnrollReminderNotificationContent(String enrollReminderNotificationContent) {
        this.enrollReminderNotificationContent = enrollReminderNotificationContent;
    }

    public String getCompleteReminderNotificationContent() {
        return completeReminderNotificationContent;
    }

    public void setCompleteReminderNotificationContent(String completeReminderNotificationContent) {
        this.completeReminderNotificationContent = completeReminderNotificationContent;
    }

    public String getLpBgImageNew() {
        return lpBgImageNew;
    }

    public void setLpBgImageNew(String lpBgImageNew) {
        this.lpBgImageNew = lpBgImageNew;
    }

    public String[] getTips() {
        return tips;
    }

    public void setTips(String[] tips) {
        this.tips = tips;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public boolean isTTSEnabledLC() {
        return isTTSEnabledLC;
    }

    public void setTTSEnabledLC(boolean TTSEnabledLC) {
        isTTSEnabledLC = TTSEnabledLC;
    }

    public boolean isTTSEnabledQC() {
        return isTTSEnabledQC;
    }

    public void setTTSEnabledQC(boolean TTSEnabledQC) {
        isTTSEnabledQC = TTSEnabledQC;
    }

    public MappedAssessmentDetails getMappedAssessmentDetails() {
        return mappedAssessmentDetails;
    }

    public void setMappedAssessmentDetails(MappedAssessmentDetails mappedAssessmentDetails) {
        this.mappedAssessmentDetails = mappedAssessmentDetails;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public long getAutoDeleteTimeStamp() {
        return autoDeleteTimeStamp;
    }

    public void setAutoDeleteTimeStamp(long autoDeleteTimeStamp) {
        this.autoDeleteTimeStamp = autoDeleteTimeStamp;
    }

    public boolean isAutoModuleCreation() {
        return autoModuleCreation;
    }

    public void setAutoModuleCreation(boolean autoModuleCreation) {
        this.autoModuleCreation = autoModuleCreation;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAckPopup() {
        return ackPopup;
    }

    public void setAckPopup(String ackPopup) {
        this.ackPopup = ackPopup;
    }

    public long getIntroCardId() {
        return introCardId;
    }

    public void setIntroCardId(long introCardId) {
        this.introCardId = introCardId;
    }

    public String getDescriptionCard() {
        return descriptionCard;
    }

    public void setDescriptionCard(String descriptionCard) {
        this.descriptionCard = descriptionCard;
    }

    public boolean isHideLeaderboard() {
        return hideLeaderboard;
    }

    public void setHideLeaderboard(boolean hideLeaderboard) {
        this.hideLeaderboard = hideLeaderboard;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getMultilangCourseDataList() {
        return multilangCourseDataList;
    }

    public void setMultilangCourseDataList(String multilangCourseDataList) {
        this.multilangCourseDataList = multilangCourseDataList;
    }

    public List<Integer> getCourseIdList() {
        return courseIdList;
    }

    public void setCourseIdList(List<Integer> courseIdList) {
        this.courseIdList = courseIdList;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public long getParentCourseId() {
        return parentCourseId;
    }

    public void setParentCourseId(long parentCourseId) {
        this.parentCourseId = parentCourseId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public String getCourseTags() {
        return courseTags;
    }

    public void setCourseTags(String courseTags) {
        this.courseTags = courseTags;
    }

    public boolean isCourseLandScapeMode() {
        return courseLandScapeMode;
    }

    public void setCourseLandScapeMode(boolean courseLandScapeMode) {
        this.courseLandScapeMode = courseLandScapeMode;
    }

    public boolean isCourseCompletionPercentage() {
        return courseCompletionPercentage;
    }

    public void setCourseCompletionPercentage(boolean courseCompletionPercentage) {
        this.courseCompletionPercentage = courseCompletionPercentage;
    }

    public boolean isAutoMoveToNext() {
        return autoMoveToNext;
    }

    public void setAutoMoveToNext(boolean autoMoveToNext) {
        this.autoMoveToNext = autoMoveToNext;
    }

    public boolean isTtsenabledLC() {
        return ttsenabledLC;
    }

    public void setTtsenabledLC(boolean ttsenabledLC) {
        this.ttsenabledLC = ttsenabledLC;
    }

    public boolean isTtsenabledQC() {
        return ttsenabledQC;
    }

    public void setTtsenabledQC(boolean ttsenabledQC) {
        this.ttsenabledQC = ttsenabledQC;
    }
}

