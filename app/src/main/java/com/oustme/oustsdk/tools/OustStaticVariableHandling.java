package com.oustme.oustsdk.tools;

import android.media.MediaPlayer;
import android.widget.TextView;

import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
import com.oustme.oustsdk.assessment_ui.examMode.QuestionView;
import com.oustme.oustsdk.calendar_ui.custom.EventDay;
import com.oustme.oustsdk.catalogue_ui.model.CatalogueModuleUpdate;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.customviews.CustomSearchView;
import com.oustme.oustsdk.customviews.CustomViewPager;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.interfaces.common.CplCloseListener;
import com.oustme.oustsdk.reminderNotification.ContentReminderNotification;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by shilpysamaddar on 27/12/17.
 */

public class OustStaticVariableHandling {

    // static variable single_instance of type Singleton
    private static OustStaticVariableHandling single_instance = null;
    private MediaPlayer mediaPlayer;
    private CustomExoPlayerView customExoPlayerView;

    // private constructor restricted to this class itself
    private OustStaticVariableHandling() {
    }

    // static method to create instance of Singleton class
    public static OustStaticVariableHandling getInstance() {
        if (single_instance == null)
            single_instance = new OustStaticVariableHandling();

        return single_instance;
    }


    //    LearningMapModulActivity Variables
    private int[] answerSeconds;
    private LearningCardResponceData[] learningCardResponceDatas;
    private boolean isLearningShareClicked = false;
    private boolean isCameraStarted = false;
    private String courseDataStr;
    private String courseLevelStr;
    private boolean moduleClicked = false;
    private boolean isLearniCardSwipeble = true;
    private boolean isShareFeedOpen = false;
    private boolean isComingFromAssessment = false, isComingFromSurvey = false;
    private boolean isRefreshReq = false;
    private boolean isDownloadingResources = false;
    private boolean isAppTutorialShown;
    private ArrayList<DTOUserFeeds.FeedList> feeds = new ArrayList<>();
    private boolean isAssessmentCompleted, isSurveyCompleted, surveyOpened;
    private ArrayList<CommonLandingData> taskModuleList = new ArrayList<>();
    private ArrayList<CommonLandingData> reminderList = new ArrayList<>();
    private ArrayList<DTOQuestions> questionsArrayList = new ArrayList<>();
    private List<Scores> scoresList = new ArrayList<>();
    private HashMap<Integer, QuestionView> questionViewList = new HashMap<>();
    private ArrayList<ContentReminderNotification> contentReminderNotificationArrayList = new ArrayList<>();
    private List<EventDay> eventDayArrayList = new ArrayList<>();
    private boolean newPostcreated, nbPostClicked, nbStateChanged;
    private int nbPosition, nbPostSize;
    private int courseLevelCount;
    private CatalogueModuleUpdate catalogueModuleUpdate;
    private Map<String, ArrayList<CommonLandingData>> taskModuleHashMap = new TreeMap<>();
    private Map<String, ArrayList<CommonLandingData>> taskAssessmentModuleHashMap = new TreeMap<>();
    private Map<String, ArrayList<CommonLandingData>> taskPlayListModuleHashMap = new TreeMap<>();
    private Map<String, ArrayList<CommonLandingData>> taskContextModuleHashMap = new TreeMap<>();
    private int sortPosition = -1;
    private boolean isBackButtonCliked_forCPl;
    private boolean language_switch_done;
    private String cplId;
    boolean checkCPL = false;
    boolean isLevelCompleted = false;
    boolean catalogueGridView = false;
    boolean assessmentError = false;

    int result_code;
    int feedId;
    long numOfComments, numOfLikes, numOfShares;
    boolean feedChanged, feedRemove, isLikeClicked, isClicked;

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isLikeClicked() {
        return isLikeClicked;
    }

    public void setLikeClicked(boolean likeClicked) {
        isLikeClicked = likeClicked;
    }

    public boolean isFeedRemove() {
        return feedRemove;
    }

    public void setFeedRemove(boolean feedRemove) {
        this.feedRemove = feedRemove;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public long getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(long numOfComments) {
        this.numOfComments = numOfComments;
    }

    public long getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(long numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public long getNumOfShares() {
        return numOfShares;
    }

    public void setNumOfShares(long numOfShares) {
        this.numOfShares = numOfShares;
    }

    public boolean isFeedChanged() {
        return feedChanged;
    }

    public void setFeedChanged(boolean feedChanged) {
        this.feedChanged = feedChanged;
    }

    public boolean isCatalogueGridView() {
        return catalogueGridView;
    }

    public void setCatalogueGridView(boolean catalogueGridView) {
        this.catalogueGridView = catalogueGridView;
    }

    public boolean isLevelCompleted() {
        return isLevelCompleted;
    }

    public void setLevelCompleted(boolean levelCompleted) {
        isLevelCompleted = levelCompleted;
    }

    public boolean isCheckCPL() {
        return checkCPL;
    }

    public void setCheckCPL(boolean checkCPL) {
        this.checkCPL = checkCPL;
    }

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }

    public boolean isRefreshReq() {
        return isRefreshReq;
    }

    public void setRefreshReq(boolean refreshReq) {
        isRefreshReq = refreshReq;
    }

    public boolean isComingFromAssessment() {
        return isComingFromAssessment;
    }

    public void setComingFromAssessment(boolean comingFromAssessment) {
        isComingFromAssessment = comingFromAssessment;
    }

    public boolean isShareFeedOpen() {
        return isShareFeedOpen;
    }

    public void setShareFeedOpen(boolean shareFeedOpen) {
        isShareFeedOpen = shareFeedOpen;
    }

    private boolean isVideoFullScreen = false;

    public boolean isVideoFullScreen() {
        return isVideoFullScreen;
    }

    public void setVideoFullScreen(boolean videoFullScreen) {
        isVideoFullScreen = videoFullScreen;
    }

    public boolean isLearniCardSwipeble() {
        return isLearniCardSwipeble;
    }

    public void setLearniCardSwipeble(boolean learniCardSwipeble) {
        isLearniCardSwipeble = learniCardSwipeble;
    }

    public static OustStaticVariableHandling getSingle_instance() {
        return single_instance;
    }

    public static void setSingle_instance(OustStaticVariableHandling single_instance) {
        OustStaticVariableHandling.single_instance = single_instance;
    }

    public boolean isModuleClicked() {
        return moduleClicked;
    }

    public void setModuleClicked(boolean moduleClicked) {
        this.moduleClicked = moduleClicked;
    }

    public String getCourseDataStr() {
        return courseDataStr;
    }

    public void setCourseDataStr(String courseDataStr) {
        this.courseDataStr = courseDataStr;
    }

    public String getCourseLevelStr() {
        return courseLevelStr;
    }

    public void setCourseLevelStr(String courseLevelStr) {
        this.courseLevelStr = courseLevelStr;
    }

    public boolean isCameraStarted() {
        return isCameraStarted;
    }

    public void setCameraStarted(boolean cameraStarted) {
        isCameraStarted = cameraStarted;
    }

    public int[] getAnswerSeconds() {
        return answerSeconds;
    }

    public void setAnswerSeconds(int[] answerSeconds) {
        this.answerSeconds = answerSeconds;
    }

    public LearningCardResponceData[] getLearningCardResponceDatas() {
        return learningCardResponceDatas;
    }

    public void setLearningCardResponceDatas(LearningCardResponceData[] learningCardResponceDatas) {
        this.learningCardResponceDatas = learningCardResponceDatas;
    }

    public boolean isLearningShareClicked() {
        return isLearningShareClicked;
    }

    public void setLearningShareClicked(boolean learningShareClicked) {
        isLearningShareClicked = learningShareClicked;
    }

    //    ReadMore Fragment
    private TextView learningquiz_timertext;

    public TextView getLearningquiz_timertext() {
        return learningquiz_timertext;
    }

    public void setLearningquiz_timertext(TextView learningquiz_timertext) {
        this.learningquiz_timertext = learningquiz_timertext;
    }

    //    PopUpActivity
    private Popup oustpopup = new Popup();

    public Popup getOustpopup() {
        return oustpopup;
    }

    public void setOustpopup(Popup oustpopup) {
        this.oustpopup = oustpopup;
    }

    //    FFContestPlayActivity
    private boolean contestOver = false;

    public boolean isContestOver() {
        return contestOver;
    }

    public void setContestOver(boolean contestOver) {
        this.contestOver = contestOver;
    }

    //    FFcontestStartActivity
    private boolean showInstructionPopup = false;

    public boolean isShowInstructionPopup() {
        return showInstructionPopup;
    }

    public void setShowInstructionPopup(boolean showInstructionPopup) {
        this.showInstructionPopup = showInstructionPopup;
    }

    //    NewLandingActivity
    private boolean isWhiteLabeledApp;
    private CustomViewPager newViewPager;
    private boolean isAppInForebackground = false;
    private CustomSearchView newSearchView;
    private int isNewLayout = 0;
    private boolean isEnterpriseUser = false;
    private boolean isNetConnectionAvailable = true;
    private boolean isContestLive = false;
    private boolean isAppActive = false;
    private boolean isContainerApp = false;
    private boolean isNewLandingSearch = false;
    private boolean isInternetConnectionOff = false;
    private boolean isForceUpgradePopupVisible = false;
    private boolean isFilterAllowed = true;
    private boolean multilingualChildCourseSelected = false;
    private boolean isCplLanguageScreenOpen = false;
    private boolean isNewCplDistributed = false;
    private boolean isSubmitCourseCompleteCalled = false;
    private boolean isVideoOverlayQuestion = false;
    private boolean isCplCityScreenOpen = false;
    private OustApiListener oustApiListener = null;
    private CplCloseListener cplCloseListener = null;

    public boolean isSubmitCourseCompleteCalled() {
        return isSubmitCourseCompleteCalled;
    }

    public void setSubmitCourseCompleteCalled(boolean submitCourseCompleteCalled) {
        isSubmitCourseCompleteCalled = submitCourseCompleteCalled;
    }

    public boolean isNewCplDistributed() {
        return isNewCplDistributed;
    }

    public void setNewCplDistributed(boolean newCplDistributed) {
        isNewCplDistributed = newCplDistributed;
    }

    public boolean isCplLanguageScreenOpen() {
        return isCplLanguageScreenOpen;
    }

    public void setCplLanguageScreenOpen(boolean cplLanguageScreenOpen) {
        isCplLanguageScreenOpen = cplLanguageScreenOpen;
    }

    public boolean isFilterAllowed() {
        return isFilterAllowed;
    }

    public void setFilterAllowed(boolean filterAllowed) {
        isFilterAllowed = filterAllowed;
    }

    public boolean isInternetConnectionOff() {
        return isInternetConnectionOff;
    }

    public void setInternetConnectionOff(boolean internetConnectionOff) {
        isInternetConnectionOff = internetConnectionOff;
    }

    public boolean isNewLandingSearch() {
        return isNewLandingSearch;
    }

    public void setNewLandingSearch(boolean newLandingSearch) {
        isNewLandingSearch = newLandingSearch;
    }

    public ArrayList<DTOUserFeeds.FeedList> getFeeds() {
        if (feeds != null) {
            return feeds;
        } else {
            return new ArrayList<>();
        }
        //  return feeds;
    }

    public void setFeeds(ArrayList<DTOUserFeeds.FeedList> feeds) {
        this.feeds = feeds;
    }

    public boolean isWhiteLabeledApp() {
        return isWhiteLabeledApp;
    }

    public void setWhiteLabeledApp(boolean whiteLabeledApp) {
        isWhiteLabeledApp = whiteLabeledApp;
    }

    public CustomViewPager getNewViewPager() {
        return newViewPager;
    }

    public void setNewViewPager(CustomViewPager newViewPager) {
        this.newViewPager = newViewPager;
    }

    public boolean isAppInForebackground() {
        return isAppInForebackground;
    }

    public void setAppInForebackground(boolean appInForebackground) {
        isAppInForebackground = appInForebackground;
    }

    public CustomSearchView getNewSearchView() {
        return newSearchView;
    }

    public void setNewSearchView(CustomSearchView newSearchView) {
        this.newSearchView = newSearchView;
    }

    public int getIsNewLayout() {
        return isNewLayout;
    }

    public void setIsNewLayout(int isNewLayout) {
        this.isNewLayout = isNewLayout;
    }

    public boolean isEnterpriseUser() {
        return isEnterpriseUser;
    }

    public void setEnterpriseUser(boolean enterpriseUser) {
        isEnterpriseUser = enterpriseUser;
    }

    public boolean isNetConnectionAvailable() {
        return isNetConnectionAvailable;
    }

    public void setNetConnectionAvailable(boolean netConnectionAvailable) {
        isNetConnectionAvailable = netConnectionAvailable;
    }

    public boolean isContestLive() {
        return isContestLive;
    }

    public void setContestLive(boolean contestLive) {
        isContestLive = contestLive;
    }

    public boolean isAppActive() {
        return isAppActive;
    }

    public void setAppActive(boolean appActive) {
        isAppActive = appActive;
    }

    public boolean isContainerApp() {
        return isContainerApp;
    }

    public void setContainerApp(boolean containerApp) {
        isContainerApp = containerApp;
    }

    public boolean isMultilingualChildCourseSelected() {
        return multilingualChildCourseSelected;
    }

    public void setMultilingualChildCourseSelected(boolean multilingualChildCourseSelected) {
        this.multilingualChildCourseSelected = multilingualChildCourseSelected;
    }

    //    NewLearningMapPresenter
    private int currentLearningPathId;
    private long courseUniqNo;

    public int getCurrentLearningPathId() {
        return currentLearningPathId;
    }

    public void setCurrentLearningPathId(int currentLearningPathId) {
        this.currentLearningPathId = currentLearningPathId;
    }

    public long getCourseUniqNo() {
        return courseUniqNo;
    }

    public void setCourseUniqNo(long courseUniqNo) {
        this.courseUniqNo = courseUniqNo;
    }

    public boolean isForceUpgradePopupVisible() {
        return isForceUpgradePopupVisible;
    }

    public void setForceUpgradePopupVisible(boolean forceUpgradePopupVisible) {
        isForceUpgradePopupVisible = forceUpgradePopupVisible;
    }

    public boolean isVideoOverlayQuestion() {
        return isVideoOverlayQuestion;
    }

    public void setVideoOverlayQuestion(boolean videoOverlayQuestion) {
        isVideoOverlayQuestion = videoOverlayQuestion;
    }

    public boolean isCplCityScreenOpen() {
        return isCplCityScreenOpen;
    }

    public void setCplCityScreenOpen(boolean cplCityScreenOpen) {
        isCplCityScreenOpen = cplCityScreenOpen;
    }

    public OustApiListener getOustApiListener() {
        return oustApiListener;
    }

    public void setOustApiListener(OustApiListener oustApiListener) {
        this.oustApiListener = oustApiListener;
    }

    public CplCloseListener getCplCloseListener() {
        return cplCloseListener;
    }

    public void setCplCloseListener(CplCloseListener cplCloseListener) {
        this.cplCloseListener = cplCloseListener;
    }

    public boolean isDownloadingResources() {
        return isDownloadingResources;
    }

    public void setDownloadingResources(boolean downloadingResources) {
        isDownloadingResources = downloadingResources;
    }

    public boolean isAppTutorialShown() {
        return isAppTutorialShown;
    }

    public void setAppTutorialShown(boolean appTutorialShown) {
        isAppTutorialShown = appTutorialShown;
    }

    public boolean isAssessmentCompleted() {
        return isAssessmentCompleted;
    }

    public void setAssessmentCompleted(boolean assessmentCompleted) {
        isAssessmentCompleted = assessmentCompleted;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public CustomExoPlayerView getCustomExoPlayerView() {
        return customExoPlayerView;
    }

    public void setCustomExoPlayerView(CustomExoPlayerView customExoPlayerView) {
        this.customExoPlayerView = customExoPlayerView;
    }

    public ArrayList<CommonLandingData> getTaskModule() {
        if (taskModuleList != null) {
            return taskModuleList;
        } else {
            return new ArrayList<>();
        }
        //  return feeds;
    }

    public void setTaskModule(ArrayList<CommonLandingData> taskModuleList) {
        this.taskModuleList = taskModuleList;
    }

    public List<EventDay> getEventDayArrayList() {
        return eventDayArrayList;
    }

    public void setEventDayArrayList(List<EventDay> eventDayArrayList) {
        this.eventDayArrayList = eventDayArrayList;
    }

    public ArrayList<CommonLandingData> getReminderNotification() {
        return reminderList;
    }

    public void setReminderNotification(ArrayList<CommonLandingData> reminderList) {
        this.reminderList = reminderList;
    }

    public ArrayList<DTOQuestions> getQuestionsArrayList() {
        return questionsArrayList;
    }

    public void setQuestionsArrayList(ArrayList<DTOQuestions> questionsArrayList) {
        this.questionsArrayList = questionsArrayList;
    }

    public List<Scores> getScoresList() {
        return scoresList;
    }

    public void setScoresList(List<Scores> scoresList) {
        this.scoresList = scoresList;
    }

    public HashMap<Integer, QuestionView> getQuestionViewList() {
        return questionViewList;
    }

    public void setQuestionViewList(HashMap<Integer, QuestionView> questionViewList) {
        this.questionViewList = questionViewList;
    }

    public ArrayList<ContentReminderNotification> getContentReminderNotificationArrayList() {
        return contentReminderNotificationArrayList;
    }

    public void setContentReminderNotificationArrayList(ArrayList<ContentReminderNotification> contentReminderNotificationArrayList) {
        this.contentReminderNotificationArrayList = contentReminderNotificationArrayList;
    }

    public boolean isNewPostcreated() {
        return newPostcreated;
    }

    public void setNewPostcreated(boolean newPostcreated) {
        this.newPostcreated = newPostcreated;
    }

    public int getNbPosition() {
        return nbPosition;
    }

    public void setNbPosition(int nbPosition) {
        this.nbPosition = nbPosition;
    }

    public boolean isNbPostClicked() {
        return nbPostClicked;
    }

    public void setNbPostClicked(boolean nbPostClicked) {
        this.nbPostClicked = nbPostClicked;
    }

    public boolean isNbStateChanged() {
        return nbStateChanged;
    }

    public void setNbStateChanged(boolean nbStateChanged) {
        this.nbStateChanged = nbStateChanged;
    }

    public int getNbPostSize() {
        return nbPostSize;
    }

    public void setNbPostSize(int nbPostSize) {
        this.nbPostSize = nbPostSize;
    }

    public int getCourseLevelCount() {
        return courseLevelCount;
    }

    public void setCourseLevelCount(int courseLevelCount) {
        this.courseLevelCount = courseLevelCount;
    }

    public CatalogueModuleUpdate getCatalogueModuleUpdate() {
        return catalogueModuleUpdate;
    }

    public void setCatalogueModuleUpdate(CatalogueModuleUpdate catalogueModuleUpdate) {
        this.catalogueModuleUpdate = catalogueModuleUpdate;
    }

    public boolean isComingFromSurvey() {
        return isComingFromSurvey;
    }

    public void setComingFromSurvey(boolean comingFromSurvey) {
        isComingFromSurvey = comingFromSurvey;
    }

    public boolean isSurveyCompleted() {
        return isSurveyCompleted;
    }

    public void setSurveyCompleted(boolean surveyCompleted) {
        isSurveyCompleted = surveyCompleted;
    }

    public boolean isSurveyOpened() {
        return surveyOpened;
    }

    public void setSurveyOpened(boolean surveyOpened) {
        this.surveyOpened = surveyOpened;
    }

    public boolean isBackButtonCliked_forCPl() {
        return isBackButtonCliked_forCPl;
    }

    public void setBackButtonCliked_forCPl(boolean backButtonCliked_forCPl) {
        isBackButtonCliked_forCPl = backButtonCliked_forCPl;
    }

    public boolean isLanguage_switch_done() {
        return language_switch_done;
    }

    public void setLanguage_switch_done(boolean language_switch_done) {
        this.language_switch_done = language_switch_done;
    }

    public ArrayList<CommonLandingData> getTaskModuleList() {
        return taskModuleList;
    }

    public void setTaskModuleList(ArrayList<CommonLandingData> taskModuleList) {
        this.taskModuleList = taskModuleList;
    }

    public ArrayList<CommonLandingData> getReminderList() {
        return reminderList;
    }

    public void setReminderList(ArrayList<CommonLandingData> reminderList) {
        this.reminderList = reminderList;
    }

    public Map<String, ArrayList<CommonLandingData>> getTaskModuleHashMap() {
        return taskModuleHashMap;
    }

    public void setTaskModuleHashMap(Map<String, ArrayList<CommonLandingData>> taskModuleHashMap) {
        this.taskModuleHashMap = taskModuleHashMap;
    }

    public int getSortPosition() {
        return sortPosition;
    }

    public void setSortPosition(int sortPosition) {
        this.sortPosition = sortPosition;
    }

    public Map<String, ArrayList<CommonLandingData>> getTaskAssessmentModuleHashMap() {
        return taskAssessmentModuleHashMap;
    }

    public void setTaskAssessmentModuleHashMap(Map<String, ArrayList<CommonLandingData>> taskAssessmentModuleHashMap) {
        this.taskAssessmentModuleHashMap = taskAssessmentModuleHashMap;
    }

    public Map<String, ArrayList<CommonLandingData>> getTaskPlayListModuleHashMap() {
        return taskPlayListModuleHashMap;
    }

    public void setTaskPlayListModuleHashMap(Map<String, ArrayList<CommonLandingData>> taskPlayListModuleHashMap) {
        this.taskPlayListModuleHashMap = taskPlayListModuleHashMap;
    }

    public Map<String, ArrayList<CommonLandingData>> getTaskContextModuleHashMap() {
        return taskContextModuleHashMap;
    }

    public void setTaskContextModuleHashMap(Map<String, ArrayList<CommonLandingData>> taskContextModuleHashMap) {
        this.taskContextModuleHashMap = taskContextModuleHashMap;
    }

    public boolean isAssessmentError() {
        return assessmentError;
    }

    public void setAssessmentError(boolean assessmentError) {
        this.assessmentError = assessmentError;
    }
}
