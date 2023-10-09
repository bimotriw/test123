package com.oustme.oustsdk.tools;

import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.interfaces.assessment.AssessmentResultActivityEndInterface;
import com.oustme.oustsdk.interfaces.course.LearningCallBackInterface;
import com.oustme.oustsdk.request.All_Friends;
import com.oustme.oustsdk.response.common.CurrentGameInfo;
import com.oustme.oustsdk.response.common.OFBModules;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.LearningPathModule;
import com.oustme.oustsdk.skill_ui.model.SoccerSkillLevelDataList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class OustAppState {
    private LearningCallBackInterface learningCallBackInterface;
    private AssessmentResultActivityEndInterface assessmentResultActivityEndInterface;
    private static OustAppState instance = null;
    private boolean isLandingPageLive = false;
    private ActiveUser activeUser;
    private ActiveGame activeGame;
    private ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataLists;
    private PlayResponse playResponse = null;
    private DTONewFeed currentSurveyFeed;
    private ArrayList<CommonLandingData> myDeskList = new ArrayList<>();

    private ArrayList<CommonLandingData> myAssessmentList = new ArrayList<>();

    private boolean landingFragmentInit;
    private boolean challengeFragmentInit;
    private boolean neighbourFragmentInit;
    private boolean isContactFetch;
    private boolean groupFragmentInit;
    private boolean notificationFragmentInit;
    private boolean historyListInit;
    private boolean isEventInit;

    private OFBModules ofbModules = null;
    private OFBModules ofbAllModules = null;

    private List<LearningPathModule> allCources;
    private List<LearningPathModule> myCources;
    private List<AssessmentFirebaseClass> assessmentFirebaseClassList;
    private AssessmentFirebaseClass assessmentFirebaseClass;

    private List<String> my_oustgroups;
    private Map<String, Object> fun_modulesKeys;


    private String groupLeaderBoardNo;
    private String groupLeaderBoardName;
    private String leaderboardAllGradeFilter;

    private List<DTONewFeed> newsFeeds;
    private List<String> grades;
    private List<String> hintMessages;
    private List<CourseDataClass> userProfileCourses;

    private int decideResultGameInfo;

    private int friendTabNo;

    private volatile boolean hasPopup;
    private Stack<Popup> popupStack;
    private boolean isHistoryOfFriend;

    private boolean showLoader;
    private CurrentGameInfo currentGameInfo;
    private int landingTabNo;
    private boolean isNewNewsFeed;

    private boolean isSoundDisabled;

    private List<FirebaseRefClass> firebaseRefClassList;
    private boolean shouldLoadGameInfoFrom_LearningNode;
    private boolean isAssessmentGame;
    private boolean disableBackButton;
    private boolean isSurveyResume;
    private boolean assessmentRunning;
    private boolean isSurveySubmitted;
    private boolean isSurveyExit;
    private String exitMessage;
    private boolean allQuestionsAttempted;

    private List<CourseDataClass> newLandingCourses;
    private All_Friends friend_displayProfile;

    //    private Map<String,Object> moduleDataMap;
//    private Map<String,Object> landingDataMap;
    private boolean isLandingPageOpen = false;

    private OustAppState() {
        activeUser = new ActiveUser();
        activeGame = new ActiveGame();
        ofbModules = new OFBModules();

        my_oustgroups = new ArrayList<>();
        firebaseRefClassList = new ArrayList<>();
        allCources = new ArrayList<>();
        myCources = new ArrayList<>();
        newLandingCourses = new ArrayList<>();
        hintMessages = new ArrayList<>();

        grades = new ArrayList<>();

        newsFeeds = new ArrayList<>();

        popupStack = new Stack<>();

        landingFragmentInit = false;
        challengeFragmentInit = false;
        neighbourFragmentInit = false;
        notificationFragmentInit = false;
        groupFragmentInit = false;
        isContactFetch = false;
        isAssessmentGame = false;
        groupLeaderBoardNo = "";
        groupLeaderBoardName = "";

        leaderboardAllGradeFilter = "";

        friendTabNo = 2;
        hasPopup = false;
        isHistoryOfFriend = false;
        showLoader = false;
        decideResultGameInfo = 0;
        currentGameInfo = new CurrentGameInfo();
        landingTabNo = 0;
        isNewNewsFeed = false;
        isSoundDisabled = false;
        shouldLoadGameInfoFrom_LearningNode = false;
    }


    public boolean isSoundDisabled() {
        return isSoundDisabled;
    }

    public void setIsSoundDisabled(boolean isSoundDisabled) {
        this.isSoundDisabled = isSoundDisabled;
    }


    public List<DTONewFeed> getNewsFeeds() {
        return newsFeeds;
    }

    public void setNewsFeeds(List<DTONewFeed> newsFeeds) {
        this.newsFeeds = newsFeeds;
    }

    public int getDecideResultGameInfo() {
        return decideResultGameInfo;
    }

    public void setDecideResultGameInfo(int decideResultGameInfo) {
        this.decideResultGameInfo = decideResultGameInfo;
    }

    public Map<String, Object> getFun_modulesKeys() {
        return fun_modulesKeys;
    }

    public void setFun_modulesKeys(Map<String, Object> fun_modulesKeys) {
        this.fun_modulesKeys = fun_modulesKeys;
    }

    public boolean isShowLoader() {
        return showLoader;
    }

    public void setShowLoader(boolean showLoader) {
        this.showLoader = showLoader;
    }

    public List<String> getMy_oustgroups() {
        return my_oustgroups;
    }

    public void setMy_oustgroups(List<String> my_oustgroups) {
        this.my_oustgroups = my_oustgroups;
    }

    public boolean isHistoryOfFriend() {
        return isHistoryOfFriend;
    }

    public void setIsHistoryOfFriend(boolean isHistoryOfFriend) {
        this.isHistoryOfFriend = isHistoryOfFriend;
    }


    public static OustAppState getInstance() {

        if (instance == null) {
            synchronized (OustAppState.class) {
                if (instance == null) {
                    instance = new OustAppState();
                }
            }
        }
        return instance;
    }

    public void pushPopup(Popup popup) {
        popupStack.push(popup);
    }

    public Popup popStactedPopup() {
        if (!popupStack.empty()) {
            return popupStack.pop();
        } else {
            return null;
        }
    }

    public Stack<Popup> getPopupStack() {
        return popupStack;
    }

    public boolean isHasPopup() {
        return hasPopup;
    }

    public void setHasPopup(boolean hasPopup) {
        this.hasPopup = hasPopup;
    }

    public PlayResponse getPlayResponse() {
        return playResponse;
    }

    public void setPlayResponse(PlayResponse playResponse) {
        this.playResponse = playResponse;
    }

    public boolean isLandingFragmentInit() {
        return landingFragmentInit;
    }

    public void setLandingFragmentInit(boolean landingFragmentInit) {
        this.landingFragmentInit = landingFragmentInit;
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

    public ArrayList<SoccerSkillLevelDataList> getSoccerSkillLevelDataList() {
        return soccerSkillLevelDataLists;
    }


    public void setSoccerSkillLevelDataList(ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataLists) {
        this.soccerSkillLevelDataLists = soccerSkillLevelDataLists;
    }

    public void clearAll() {
        instance = new OustAppState();
    }


    public boolean isShouldLoadGameInfoFrom_LearningNode() {
        return shouldLoadGameInfoFrom_LearningNode;
    }

    public void setShouldLoadGameInfoFrom_LearningNode(boolean shouldLoadGameInfoFrom_LearningNode) {
        this.shouldLoadGameInfoFrom_LearningNode = shouldLoadGameInfoFrom_LearningNode;
    }


    public boolean isAssessmentGame() {
        return isAssessmentGame;
    }

    public void setAssessmentGame(boolean assessmentGame) {
        isAssessmentGame = assessmentGame;
    }

    public boolean isDisableBackButton() {
        return disableBackButton;
    }

    public void setDisableBackButton(boolean disableBackButton) {
        this.disableBackButton = disableBackButton;
    }

    public boolean isSurveyResume() {
        return isSurveyResume;
    }

    public void setSurveyResume(boolean surveyResume) {
        isSurveyResume = surveyResume;
    }

    public boolean isAssessmentRunning() {
        return assessmentRunning;
    }

    public void setAssessmentRunning(boolean assessmentRunning) {
        this.assessmentRunning = assessmentRunning;
    }

    public boolean isSurveySubmitted() {
        return isSurveySubmitted;
    }

    public void setSurveySubmitted(boolean surveySubmitted) {
        isSurveySubmitted = surveySubmitted;
    }

    public boolean isAllQuestionsAttempted() {
        return allQuestionsAttempted;
    }

    public void setAllQuestionsAttempted(boolean allQuestionsAttempted) {
        this.allQuestionsAttempted = allQuestionsAttempted;
    }

    public List<AssessmentFirebaseClass> getAssessmentFirebaseClassList() {
        return assessmentFirebaseClassList;
    }

    public void setAssessmentFirebaseClassList(List<AssessmentFirebaseClass> assessmentFirebaseClassList) {
        this.assessmentFirebaseClassList = assessmentFirebaseClassList;
    }

    public AssessmentFirebaseClass getAssessmentFirebaseClass() {
        return assessmentFirebaseClass;
    }

    public void setAssessmentFirebaseClass(AssessmentFirebaseClass assessmentFirebaseClass) {
        this.assessmentFirebaseClass = assessmentFirebaseClass;
    }


    public List<CourseDataClass> getNewLandingCourses() {
        return newLandingCourses;
    }

    public void setNewLandingCourses(List<CourseDataClass> newLandingCourses) {
        this.newLandingCourses = newLandingCourses;
    }


    public List<String> getHintMessages() {
        return hintMessages;
    }

    public void setHintMessages(List<String> hintMessages) {
        this.hintMessages = hintMessages;
    }

    public static void setInstance(OustAppState instance) {
        OustAppState.instance = instance;
    }


    public void setPopupStack(Stack<Popup> popupStack) {
        this.popupStack = popupStack;
    }

    public void setHistoryOfFriend(boolean historyOfFriend) {
        isHistoryOfFriend = historyOfFriend;
    }

    public void setNewNewsFeed(boolean newNewsFeed) {
        isNewNewsFeed = newNewsFeed;
    }

    public void setSoundDisabled(boolean soundDisabled) {
        isSoundDisabled = soundDisabled;
    }


    public DTONewFeed getCurrentSurveyFeed() {
        return currentSurveyFeed;
    }

    public void setCurrentSurveyFeed(DTONewFeed currentSurveyFeed) {
        this.currentSurveyFeed = currentSurveyFeed;
    }

    public List<FirebaseRefClass> getFirebaseRefClassList() {
        return firebaseRefClassList;
    }

    public void setFirebaseRefClassList(List<FirebaseRefClass> firebaseRefClassList) {
        this.firebaseRefClassList = firebaseRefClassList;
    }


    public ArrayList<CommonLandingData> getMyDeskList() {
        return myDeskList;
    }

    public void setMyDeskList(ArrayList<CommonLandingData> myDeskList) {
        this.myDeskList = myDeskList;
    }


    public ArrayList<CommonLandingData> getMyAssessmentList() {
        return myAssessmentList;
    }

    public void setMyAssessmentList(ArrayList<CommonLandingData> myAssessmentList) {
        this.myAssessmentList = myAssessmentList;
    }

    public boolean isLandingPageOpen() {
        return isLandingPageOpen;
    }

    public void setLandingPageOpen(boolean landingPageOpen) {
        isLandingPageOpen = landingPageOpen;
    }

    public boolean isChallengeFragmentInit() {
        return challengeFragmentInit;
    }

    public void setChallengeFragmentInit(boolean challengeFragmentInit) {
        this.challengeFragmentInit = challengeFragmentInit;
    }

    public boolean isLandingPageLive() {
        return isLandingPageLive;
    }

    public void setLandingPageLive(boolean landingPageLive) {
        isLandingPageLive = landingPageLive;
    }

    public LearningCallBackInterface getLearningCallBackInterface() {
        return learningCallBackInterface;
    }

    public void setLearningCallBackInterface(LearningCallBackInterface learningCallBackInterface) {
        this.learningCallBackInterface = learningCallBackInterface;
    }

    public AssessmentResultActivityEndInterface getAssessmentResultActivityEndInterface() {
        return assessmentResultActivityEndInterface;
    }

    public void setAssessmentResultActivityEndInterface(AssessmentResultActivityEndInterface assessmentResultActivityEndInterface) {
        this.assessmentResultActivityEndInterface = assessmentResultActivityEndInterface;
    }

    public boolean isSurveyExit() {
        return isSurveyExit;
    }

    public void setSurveyExit(boolean surveyExit) {
        isSurveyExit = surveyExit;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }

}
