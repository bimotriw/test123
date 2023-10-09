package com.oustme.oustsdk.activity.courses.newlearnngmap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.request.CourseCompleteAcknowledgeRequest;
import com.oustme.oustsdk.request.CourseRating_Request;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class NewLearningMapPresenter {

    private static final String TAG = "NewLearningMapPresenter";

    private ActiveUser activeUser;
    private LearningMapView view;
    private int totalNoofLevels = 1;
    private int currentLevelNo = 0;
    private int lastLevelNo = 0;
    private boolean isMicroCourse;

    private boolean isPathInitialized = false;
    private int scrHeight = 0;
    private int scrWidth = 0;
    private int scrollViewHeight = 0;
    private CourseDataClass courseDataClass;
    private boolean isAdaptive = false;
    //    public static int courseUniqNo;
    private int totalCards = 0;
    private boolean isReviewMode = false;
    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    private DTOUserCourseData dtoUserCourseData;
    private boolean clickOnStart = false;
    private int currentLevel;

    public NewLearningMapPresenter(LearningMapView view) {
        this.view = view;
    }

    public NewLearningMapPresenter(String lpIdStr, String courseColnId) {
        try {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            if ((lpIdStr != null) && (!lpIdStr.isEmpty())) {
                OustStaticVariableHandling.getInstance().setCurrentLearningPathId(Integer.parseInt(lpIdStr));
                String s1 = "" + activeUser.getStudentKey() + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    s1 = "" + activeUser.getStudentKey() + "" + courseColnId + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                }
                long courseUniqueNo = Long.parseLong(s1);
                OustStaticVariableHandling.getInstance().setCourseUniqNo(courseUniqueNo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    //sqlite method

    public void addUserData(DTOUserCourseData userCourseData) {
        Log.d(TAG, "addUserData: ");
        try {
            if (userCourseScoreDatabaseHandler == null) {
                userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            }
            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, OustStaticVariableHandling.getInstance().getCourseUniqNo());
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }
    }

    public void setAdaptive(boolean value) {
        isAdaptive = value;
    }

    public DTOUserCourseData getUserCourseData() {
        if (userCourseScoreDatabaseHandler == null) {
            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        }
        return userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }

    public void setReviewModeStatus(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
        startLearningMap(false);
    }

    public void setAcknowledged(boolean status) {
        userCourseScoreDatabaseHandler.setAcknowledged(status, dtoUserCourseData);
    }

    public void setCourseComplete(boolean isCourseComplete) {
        userCourseScoreDatabaseHandler.setCourseComplete(isCourseComplete, dtoUserCourseData);
    }

    public DTOUserCourseData getDtoUserCourseData() {
        return dtoUserCourseData;
    }
//------------------------

    public NewLearningMapPresenter(LearningMapView view, String lpIdStr, int scrWidth1, int scrHeight1, Intent CallingIntent, String courseColnId) {
        try {
            this.view = view;
            this.scrHeight = scrHeight1;
            this.scrWidth = scrWidth1;
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            if ((lpIdStr != null) && (!lpIdStr.isEmpty())) {
                OustStaticVariableHandling.getInstance().setCurrentLearningPathId(Integer.valueOf(lpIdStr));
                String s1 = "" + activeUser.getStudentKey() + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    s1 = "" + activeUser.getStudentKey() + "" + courseColnId + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                }
                long courseUniqueNo = Long.parseLong(s1);
                OustStaticVariableHandling.getInstance().setCourseUniqNo(courseUniqueNo);
            }
//            Map<String,Object> cardInfo=new HashMap<>();
//            Map<String ,Object> dataMap = OustAppState.getInstance().getModuleDataMap();
//            if ((OustAppState.getInstance().getLandingDataMap() != null)) {
//                if ((OustAppState.getInstance().getLandingDataMap().get("enrolled") != null) &&
//                        (!(boolean) OustAppState.getInstance().getLandingDataMap().get("enrolled"))) {
//                    if((dataMap!=null) && dataMap.get("descriptionCard")!=null){
//                        cardInfo= (Map<String, Object>) dataMap.get("descriptionCard");
//                    }
//                }
//            }
//            OustAppState.getInstance().setModuleDataMap(null);
//            OustAppState.getInstance().setLandingDataMap(null);
//            UserCourseData userCourseData = getUserCourseData();
//            String desclaimerData = CallingIntent.getStringExtra("desclaimerData");
//            boolean enrolled=CallingIntent.getBooleanExtra("enrolled",false);
//            clickOnStart=CallingIntent.getBooleanExtra("clickOnStart",false);
//            if ((desclaimerData != null) && (!desclaimerData.isEmpty())) {
//                if (!userCourseData.isAcknowledged()) {
//                    Gson gson = new Gson();
//                    CourseDesclaimerData courseDesclaimerData = gson.fromJson(desclaimerData, CourseDesclaimerData.class);
//                    view.setDesclaimerPopup(courseDesclaimerData);
//                } else if((cardInfo!=null) && (!cardInfo.isEmpty()) && (userCourseData!=null) && (userCourseData.getCurrentLevel()==0) && (!enrolled)){
//                    view.checkForDescriptionCardData(cardInfo);
//                }
//            } else if((cardInfo!=null) && (!cardInfo.isEmpty()) && (cardInfo.size()>0) && (userCourseData!=null) &&(userCourseData.getCurrentLevel()==0) && (!enrolled)){
//                view.checkForDescriptionCardData(cardInfo);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //methode is called when got data from firebase
    public void gotLpDataFromFirebase(CourseDataClass courseDataClass1) {
//        Log.e(TAG, "inside gotLpDataFromFirebase() CourseDataClass " + courseDataClass1.toString());
        dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
        this.courseDataClass = courseDataClass1;

        DTOUserCourseData userCourseData = getUserCourseData();
        view.setDownloadCourseIcon(userCourseData);

        if (courseDataClass != null) {
            view.setLanguage(courseDataClass.getLanguage());
            if (courseDataClass.getLanguage() == null || (courseDataClass != null && courseDataClass.getLanguage().isEmpty())
                    || (courseDataClass.getLanguage() != null && !courseDataClass.getLanguage().isEmpty()
                    && courseDataClass.getLanguage().equalsIgnoreCase("en"))) {
                view.setFontAndText();
            } else {
                view.setText();
            }
        }
        if (!OustPreferences.getAppInstallVariable("hideAllCourseLeaderBoard") && !courseDataClass.isHideLeaderBoard()) {
            view.showLeaderBoard();
        }
       /* if(!OustPreferences.getAppInstallVariable("disableDisplayCoins") && !courseDataClass.isDisableDisplayCoins())
        {
            view.showCoins();
        }*/

        if (courseDataClass.isShowVirtualCoins()) {
            view.showCoins();
        }

        if (!OustPreferences.getAppInstallVariable("hideCourseBulletin") && !courseDataClass.isHideBulletinBoard()) {
            view.showBulletinBoard();
        }

        try {
            if (dtoUserCourseData.getCurrentLevel() > currentLevelNo) {
                currentLevelNo = (int) dtoUserCourseData.getCurrentLevel();
                lastLevelNo = (int) dtoUserCourseData.getCurrentLevel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Log.d(TAG, "inside gotLpDataFromFirebase() currentLevelNo " + currentLevelNo);
        Log.d(TAG, "inside gotLpDataFromFirebase() lastLevelNo " + lastLevelNo);

        if (!OustPreferences.getAppInstallVariable("disableCourseReviewMode") && !courseDataClass.isDisableReviewMode()) {
            if (courseDataClass1.isSalesMode()) {
                isReviewMode = true;
                view.setReviewMode(true);
                view.setReviewModeStatus(courseDataClass1.isSalesMode());
                view.showCourseReviewLayout();
                if (courseDataClass1.isAutoDownload()) {
                    view.startCourseDownload();
                }
            }
            if (dtoUserCourseData.getPresentageComplete() == 100 && !isAdaptive) {
                isReviewMode = true;
                view.setReviewMode(true);
                view.setReviewModeStatus(courseDataClass1.isSalesMode());
                view.showCourseReviewLayout();
            }
        }
        try {
            view.setCurrentModuleCompleteLevel(((int) dtoUserCourseData.getPresentageComplete()));
            view.setLpOcText(dtoUserCourseData.getTotalOc(), courseDataClass.getTotalOc());
            if ((courseDataClass != null) && (courseDataClass.getCourseLevelClassList() != null)) {
                totalNoofLevels = courseDataClass.getCourseLevelClassList().size();
                if (courseDataClass.getMappedAssessmentId() > 0) {
                    totalNoofLevels++;
                }
                totalCards = 0;
                for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                    totalCards += courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size();
                }
                userCourseScoreDatabaseHandler.setTotalCards(totalCards, dtoUserCourseData);
                startLearningMap(false);
                if (!courseDataClass.isEnrolled()) {
                    clickOnEnrolleLp(false);
                }

                view.hideLoader();
            } else {
                view.hideLoader();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e(TAG, "caught exception inside gotLpDataFromFirebase() ", e);
        }
    }

    //show review_text after course complte
    public void startShowingReviewModeOptionn() {
        if (!OustPreferences.getAppInstallVariable("disableCourseReviewMode")) {
            if (dtoUserCourseData.getPresentageComplete() == 100) {
                isReviewMode = false;
                if (!courseDataClass.isDisableReviewMode()) {
                    isReviewMode = true;
                }

                view.setReviewMode(isReviewMode);
                view.setReviewModeStatus(courseDataClass.isSalesMode());
            }
        }
    }

    public void startLearningMap(boolean updateReviewList) {
        Log.e(TAG, "inside startLearningMap() ");
        try {
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (isReviewMode) {
                if (updateReviewList) {
                    view.updateReviewList();
                } else {
                    List<SearchCourseLevel> searchCourseLevelList = new ArrayList<>();
                    for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                        SearchCourseLevel searchCourseLevel = new SearchCourseLevel();
                        searchCourseLevel.setName(courseDataClass.getCourseLevelClassList().get(i).getLevelName());
                        searchCourseLevel.setDescription(courseDataClass.getCourseLevelClassList().get(i).getLevelDescription());
                        searchCourseLevel.setId(courseDataClass.getCourseLevelClassList().get(i).getLevelId());
                        searchCourseLevel.setRefreshTimeStamp(courseDataClass.getCourseLevelClassList().get(i).getRefreshTimeStamp());
                        searchCourseLevel.setSequence(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                        searchCourseLevel.setLpId(courseDataClass.getCourseLevelClassList().get(i).getLpId());
                        searchCourseLevel.setTotalOc(courseDataClass.getCourseLevelClassList().get(i).getTotalOc());
                        if (i == 0) {
                            searchCourseLevel.setSearchMode(true);
                        }
                        List<SearchCourseCard> searchCourseCards = new ArrayList<>();
                        for (int j = 0; j < courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size(); j++) {
                            if (courseDataClass.isShowQuesInReviewMode() || courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardType().equalsIgnoreCase("LEARNING")) {
                                SearchCourseCard searchCourseCard = new SearchCourseCard();
                                if (courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j) != null) {
                                    searchCourseCard.setDescription(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getContent());
                                    searchCourseCard.setName(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardTitle());
                                    searchCourseCard.setId(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardId());
                                    searchCourseCard.setCardType(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardType());
                                    searchCourseCard.setSequence(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getSequence());
                                }
                                searchCourseCards.add(searchCourseCard);
                            }
                        }
                        searchCourseLevel.setSearchCourseCards(searchCourseCards);
                        searchCourseLevelList.add(searchCourseLevel);
                    }
                    view.setBackLayer(courseDataClass.getLpBgImage());
                    view.setAllLevelList(searchCourseLevelList);
                    view.createLevelList(searchCourseLevelList, false);
                    view.setCertificateVisibility(dtoUserCourseData, courseDataClass);
                    if ((courseDataClass.getCourseName() != null)) {
                        view.setLpName(courseDataClass.getCourseName());
                    }
                }
            } else {
                Log.d(TAG, "startLearningMap: not review_text mode:");
                if ((courseDataClass != null) && (courseDataClass.getCourseLevelClassList() != null)) {
                    if (isPathInitialized) {
                        //UserCourseData userCourseData = NewLearningMapPresenter.getUserCourseData();
                        view.addLevelIcons(scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, lastLevelNo, courseDataClass, dtoUserCourseData);
                        view.addLevelDescription(scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, lastLevelNo, currentLevelNo, courseDataClass, dtoUserCourseData);
                        if (!isAdaptive) {
                            view.setIndicatorPosition(lastLevelNo, currentLevelNo, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
                            setAnimation();
                        } else {
                            int lastPlayedLevel = OustPreferences.getSavedInt("LAST_PLAYED_LEVEL_" + courseDataClass.getCourseId());
                            currentLevel = 0;
                            if (lastPlayedLevel == 0)
                                currentLevel = 1;
                            else
                                currentLevel = lastPlayedLevel + 1;

                            int cuLevel = 0;
                            if (!mIsEnrolled)
                                cuLevel = lastPlayedLevel;
                            else
                                cuLevel = lastPlayedLevel + 1;
                            view.setIndicatorPosition(cuLevel, currentLevel + 1, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
                        }
                    } else {
                        Log.d(TAG, "startLearningMap: path not iniliazed:");
                        view.startLearningPath();
                        isPathInitialized = true;
                        if (totalNoofLevels < 4) {
                            scrollViewHeight = (((totalNoofLevels)) * (75 * scrHeight / 480)) + (255 * scrHeight / 480);
                        } else {
                            scrollViewHeight = (((totalNoofLevels)) * (80 * scrHeight / 480)) + (255 * scrHeight / 480);
                        }
                        if (scrollViewHeight < scrHeight) {
                            scrollViewHeight = scrHeight;
                        }
                        startLearningMapBack();
                    }
                }

            }
            view.checkMultiLingualCourse();
        } catch (Exception e) {
            Log.e(TAG, "caught exception inside startLearningMap() ", e);
        }
    }

    public void setLastLevelNo() {
        try {
            userCourseScoreDatabaseHandler.setLastCompleteLevel(dtoUserCourseData.getCurrentCompleteLevel(), dtoUserCourseData);
            lastLevelNo = currentLevelNo;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startLearningMapBack() {
        Log.d(TAG, "startLearningMapBack: ");
        try {
            if ((courseDataClass.getCourseName() != null)) {
                view.setLpName(courseDataClass.getCourseName());
            }
            view.setBackLine(scrWidth, scrHeight, scrollViewHeight, totalNoofLevels);
            view.setBackLayer(courseDataClass.getLpBgImage());
            view.bringBackFront();
            view.setLearningStartIcon(scrWidth, scrHeight, scrollViewHeight);
            view.addLevelIcons(scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, lastLevelNo, courseDataClass, dtoUserCourseData);
            view.addLevelDescription(scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, lastLevelNo, currentLevelNo, courseDataClass, dtoUserCourseData);
            view.setIndicatorPosition(lastLevelNo, currentLevelNo, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
            view.setCertificateVisibility(dtoUserCourseData, courseDataClass);

            if (lastLevelNo == 0 && currentLevelNo == 0) {
                view.addOverLay(scrWidth, scrHeight, scrollViewHeight);
                view.setLearningStartLabbelIcon(scrWidth, scrHeight, scrollViewHeight);
            }

            setAnimation();
            if (dtoUserCourseData.getPresentageComplete() == 100) {
                view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
            } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);

            } else {
                view.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
            }
            view.onScrollPostMeth();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onScrollPost() {
        Log.d(TAG, "onScrollPost: ");
        //if(dtoUserCourseData.isValid()) {
        if (dtoUserCourseData != null && dtoUserCourseData.getPresentageComplete() == 100) {
            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
        } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
        } else {
            view.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
        }
        //}
    }

    public void setAnimation() {
        try {
            if (!dtoUserCourseData.isCourseComplete()) {
                Log.d(TAG, "setAnimation: " + currentLevelNo);
                if (totalNoofLevels > 0) {
                    boolean isAnimRequired = false;
                    if (currentLevelNo != lastLevelNo) {
                        isAnimRequired = true;
                        if (dtoUserCourseData.getPresentageComplete() == 100) {
                            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
                        } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);

                        } else {
                            view.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
                        }
                    }
                    if (isAnimRequired) {
                        view.setLevelChangeAnim(currentLevelNo, scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, courseDataClass);
                        lastLevelNo = currentLevelNo;
                    }
                }
            } else {
                if (totalNoofLevels > 0) {
                    boolean isAnimRequired = false;
                    if (dtoUserCourseData.getCurrentCompleteLevel() != dtoUserCourseData.getLastCompleteLevel()) {
                        isAnimRequired = true;
                        if (dtoUserCourseData.getPresentageComplete() == 100) {
                            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
                        } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                            view.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);

                        } else {
                            view.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
                        }
                    }
                    if (isAnimRequired) {
                        view.setLevelChangeAnim(dtoUserCourseData.getCurrentCompleteLevel(), scrWidth, scrHeight, scrollViewHeight, totalNoofLevels, courseDataClass);
                        userCourseScoreDatabaseHandler.setLastCompleteLevel(dtoUserCourseData.getCurrentCompleteLevel(), dtoUserCourseData);
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    public void clickOnLevelIcon(int levelNo) {
        try {
            Log.d(TAG, "clickOnLevelIcon: " + levelNo);
            if (isAdaptive) {
                currentLevelNo = OustPreferences.getSavedInt("LAST_PLAYED_LEVEL_" + courseDataClass.getCourseId()) + 1;
                if (currentLevelNo > courseDataClass.getCourseLevelClassList().size()) {
                    currentLevelNo = courseDataClass.getCourseLevelClassList().size();
                }
            }

            if (isMicroCourse) {
                currentLevelNo = 1;
            } else if (mIsEnrolled && currentLevelNo <= 0) {
                currentLevelNo = 1;
            }

            if (currentLevelNo > 0) {
                if (levelNo <= currentLevelNo) {
                    if (isMicroCourse) {
                        levelNo = 1;
                    }
                    if ((courseDataClass.getCourseLevelClassList().size() > (levelNo - 1))) {
                        if (dtoUserCourseData.isCourseComplete()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(levelNo, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(levelNo, dtoUserCourseData);
                        }
                      /*  if ((dtoUserCourseData.getUserLevelDataList().get(levelNo - 1).getCompletePercentage() <= 0 || dtoUserCourseData.getUserLevelDataList().get(levelNo - 1).getCompletePercentage() >= 100)) {

                        }else{
                            OustSdkTools.showToast("Please wait for media file to get downloaded");
                            view.setLearningCardOpen(false);
                            courseDataClass.getCourseLevelClassList().get(levelNo-1).setDownloadStatus(0);
                            clickonDownloadIcon((levelNo-1)+"");
                        }
*/
                        view.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        view.enableClick();
                    }
                } else {
                    OustSdkTools.showToast(OustStrings.getString("complete_level_unlock"));
                    view.enableClick();
                }
            } else {
                view.enableClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int levelClickedNo;
    private int cardNo;

    public void clickOnUserManualRow(int levelNo, int cardNo) {
        try {
            Log.d(TAG, "clickOnUserManualRow: " + levelNo + "' ---- card:" + cardNo);
            if (!isReviewMode) {
                if (courseDataClass.isEnrolled()) {
                    if ((courseDataClass.getCourseLevelClassList().size() > (levelNo - 1))) {
                        if (dtoUserCourseData.isCourseComplete()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(levelNo, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(levelNo, dtoUserCourseData);
                        }
                        view.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        view.enableClick();
                    }
                } else {
                    levelClickedNo = levelNo;
                    clickOnEnrolleLp(true);
                }
            } else {
                levelClickedNo = levelNo;

                if (courseDataClass.isSalesMode()) {
                    String s1 = "" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                    int courseUniqNo = Integer.parseInt(s1);
                    DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);

                    if (userCourseData.getCurrentLevel() == 0) {
                        this.cardNo = cardNo;
                        clickOnEnrolleLp(true);
                    } else {
                        view.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                    }
                } else {
                    view.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickOnAssessmentIcon() {
        Log.d(TAG, "clickOnAssessmentIcon: ");
        try {
            if (currentLevelNo > 0) {
                if ((totalNoofLevels - 1) <= currentLevelNo) {
                    if ((courseDataClass.getMappedAssessmentId() > 0)) {

                        if (dtoUserCourseData.isCourseCompleted()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(totalNoofLevels, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(totalNoofLevels, dtoUserCourseData);
                            view.gotoAssessmentPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass);
                        } else {
                            view.enableClick();
                        }
                    } else {
                        Log.d(TAG, "clickOnAssessmentIcon: ");
                        if (!isMicroCourse) {
                            view.enableClick();
                        } else {
                            view.endActivity();
                        }
                    }
                } else {
                    OustSdkTools.showToast(OustStrings.getString("complete_level_unlock"));
                    view.enableClick();
                }
            } else {
                view.enableClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //----------
    public void clickonDownloadIcon(String levelNostr) {
        try {
            if ((!OustSdkTools.checkInternetStatus()) || (!OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                return;
            }
            int clickedLevelN0 = (Integer.parseInt(levelNostr));
            if ((courseDataClass.getCourseLevelClassList().size() > clickedLevelN0)) {
                if (!courseDataClass.getCourseLevelClassList().get(clickedLevelN0).isLevelLock()) {
                    if ((courseDataClass.getCourseLevelClassList().get(clickedLevelN0).getDownloadStatus() == 0)) {
                        if (dtoUserCourseData.getCurrentLevel() > 0) {
                            courseDataClass.getCourseLevelClassList().get(clickedLevelN0).setDownloadStatus(1);
                            userCourseScoreDatabaseHandler.setUserLevelDataDownloadStatus(true, dtoUserCourseData, clickedLevelN0);
                            view.startDownloadLevel(courseDataClass.getCourseLevelClassList().get(clickedLevelN0), ("" + OustStaticVariableHandling.getInstance().getCourseUniqNo()));
                            if (!isReviewMode) {
                                startLearningMap(false);
                            }
                        } else {
                            OustSdkTools.showToast(OustStrings.getString("tap_start_course"));
                        }
                    } else if ((dtoUserCourseData.getUserLevelDataList().get(clickedLevelN0).getCompletePercentage() == 100)) {
                        OustSdkTools.showToast(OustStrings.getString("download_completed"));
                    }
                } else {
                    OustSdkTools.showToast(OustStrings.getString("level_download_error"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void restartDownload(String levelNostr) {
        try {
            if ((!OustSdkTools.checkInternetStatus()) || (!OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                return;
            }
            int clickedLevelN0 = (Integer.parseInt(levelNostr));
            if ((courseDataClass.getCourseLevelClassList().size() > clickedLevelN0)) {
                view.startDownloadLevel(courseDataClass.getCourseLevelClassList().get(clickedLevelN0), ("" + OustStaticVariableHandling.getInstance().getCourseUniqNo()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDwonloadStatus(CourseDataClass courseDataClass1) {
        this.courseDataClass = courseDataClass1;
    }

    //----------------
    public void onResumeCalled(boolean updateReviewList) {
        try {
            Log.d(TAG, "onResumeCalled: ");
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (dtoUserCourseData.getPresentageComplete() == 100) {
                userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1, dtoUserCourseData);
                if (dtoUserCourseData.isMappedAssessmentPassed()) {
                    userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2, dtoUserCourseData);
                }
            }

            if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1, dtoUserCourseData);
                if (dtoUserCourseData.isMappedAssessmentPassed()) {
                    userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2, dtoUserCourseData);
                }
            }

            if (dtoUserCourseData.getCurrentLevel() != currentLevelNo) {
                currentLevelNo = (int) dtoUserCourseData.getCurrentLevel();
            }
            view.setCurrentModuleCompleteLevel(((int) dtoUserCourseData.getPresentageComplete()));
            view.setLpOcText(dtoUserCourseData.getTotalOc(), courseDataClass.getTotalOc());

            startLearningMap(updateReviewList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void killActivity() {
        view.endActivity();
    }

    //-------------
    public void clcikOnLeaderBoard() {
        try {
            view.gotoLeaderBoard(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass.getCourseName(), courseDataClass.getBgImg());
        } catch (Exception e) {
        }
    }

    public void clickOnBulletinBoard() {
        view.gotoBulletinBoard(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass.getCourseName());
    }
    //-----------------

    public void clickOnEnrolleLp(boolean startCourse) {
        if (!isReviewMode) {
            if ((currentLevelNo == 0) && ((clickOnStart) || (startCourse))) {
                if (!courseDataClass.getCourseLevelClassList().get(0).isLevelLock()) {
                    view.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
                }
            }
        } else if (courseDataClass.isSalesMode() && startCourse) {
            view.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
        }
    }

    public void clickOnCourseDownload() {
        view.downloadCourse(courseDataClass);
    }

    public void enrollLpDone(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    currentLevelNo = 1;
                    if (courseDataClass != null)
                        courseDataClass.setEnrolled(true);

                    if (!isReviewMode) {
                        if (courseDataClass.isAutoDownload())
                            view.startCourseDownload();
                        DTOUserCourseData userCourseData = getUserCourseData();
                        userCourseData.setCurrentLevel(1);
                        addUserData(userCourseData);
                        if (!isMicroCourse) {
                            startLearningMap(false);
                        }
                    } else {
                        DTOUserCourseData userCourseData = getUserCourseData();
                        userCourseData.setCurrentLevel(1);
                        addUserData(userCourseData);
                        clickOnUserManualRow(levelClickedNo, cardNo);
                    }
                } else {
                    if (commonResponse.getPopup() != null) {
                        view.showPopup(commonResponse.getPopup());
                    } else if ((commonResponse.getError() != null) && (commonResponse.isSuccess())) {
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
                if (isMicroCourse) {
                    view.endActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateLevelDownloadStatus() {
        try {
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            view.updateLevelDownloadStatus(totalNoofLevels, courseDataClass, dtoUserCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setDownloadingLevelStatus(int position, boolean status) {
        userCourseScoreDatabaseHandler.setUserLevelDataDownloadStatus(status, dtoUserCourseData, position);
    }

    void getIfRegularMode(final int learningPathId) {
        String message = "/course/course" + learningPathId + "/regularMode";
        Log.e(TAG, "inside load data from firebase :" + message);

        ValueEventListener learningMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        Log.e(TAG, "got data from firebase " + dataSnapshot.getValue());
                        boolean isRegularMode = (boolean) dataSnapshot.getValue();
                        if (isRegularMode) {
                            view.gotoRegularModeActivity(learningPathId);
                        } else {
                            getLearningMap(learningPathId);
                        }

                    } else {
                        getLearningMap(learningPathId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    void getLearningMap(final int courseId) {
        Log.e(TAG, "inside load data from firebase ");
        String message = "/course/course" + courseId;
        ValueEventListener learningMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        Log.e(TAG, "got data from firebase ");
                        final Map<String, Object> learingMap = (Map<String, Object>) dataSnapshot.getValue();
                        view.extractCourseData(courseId, learingMap, null);
                    } else {
                        view.endActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }

    void getBulletinQuesFromFirebase(String learningId) {
        String message = "/courseThread/course" + learningId + "/updateTime";
        ValueEventListener allfavCardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        view.setBulletinQuesFromFirebase(dataSnapshot);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                Log.e("FirebaseD", "onCancelled()");
            }
        };
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(allfavCardListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(allfavCardListener, message));
    }

    public void sendAcknowledgedRequest(String courseColnId, String learningId) {
        try {
            List<String> acknowledgecourse_list = OustPreferences.getLoacalNotificationMsgs("acknowledgecourse_list");
            CourseCompleteAcknowledgeRequest courseCompleteAcknowledgeRequest = new CourseCompleteAcknowledgeRequest();
            courseCompleteAcknowledgeRequest.setStudentid(activeUser.getStudentid());
            if (courseColnId != null && (!courseColnId.isEmpty())) {
                courseCompleteAcknowledgeRequest.setCourseColnId(Integer.parseInt(courseColnId));
            }
            long currenttimestamp = System.currentTimeMillis();
            courseCompleteAcknowledgeRequest.setAckTimestamp(("" + currenttimestamp));
            courseCompleteAcknowledgeRequest.setCourseId(learningId);
            Gson gson = new Gson();
            String ackReqStr = gson.toJson(courseCompleteAcknowledgeRequest);
            if ((acknowledgecourse_list != null) && (acknowledgecourse_list.size() > 0)) {
                acknowledgecourse_list.add(ackReqStr);
            } else {
                acknowledgecourse_list = new ArrayList<>();
                acknowledgecourse_list.add(ackReqStr);
            }
            OustPreferences.saveLocalNotificationMsg("acknowledgecourse_list", acknowledgecourse_list);

            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
            setAcknowledged(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void loadUserDataFromFirebase(final int lpId,
                                         final CourseDataClass courseDataClass,
                                         final Map<String, Object> landingMap, String courseColnId) {
        try {
            Log.e(TAG, "inside loadUserDataFromFirebase ");
            DTOUserCourseData userCourseData = getUserCourseData();
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (((userCourseData != null) && (userCourseData.getCurrentLevel() == 0)) || (OustSdkTools.checkInternetStatus() && (requests.size() == 0))) {
                if ((landingMap != null) && (landingMap.size() > 0)) {
                    view.updateUserData(lpId, courseDataClass, landingMap);
                } else {
                    ValueEventListener enventInfoListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot courseData) {
                            try {
                                if (courseData.getValue() != null) {
                                    final Map<String, Object> userDataMap = (Map<String, Object>) courseData.getValue();
                                    view.updateUserData(lpId, courseDataClass, userDataMap);
                                } else {
                                    view.updateUserData(lpId, courseDataClass, null);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            gotLpDataFromFirebase(courseDataClass);
                        }
                    };
                    String msg = "landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course/" + lpId;
                    Log.e(TAG, "firebase link " + msg);
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        msg = "landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/courseColn/" + courseColnId + "/courses/course" + lpId;
                    }
                   /* if(isMicroCourse){

                    }else{
                        OustFirebaseTools.getRootRef().child(msg).addValueEventListener(enventInfoListener);
                    }*/
                    OustFirebaseTools.getRootRef().child(msg).addListenerForSingleValueEvent(enventInfoListener);
                    OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
                }
            } else {
                gotLpDataFromFirebase(courseDataClass);
            }
        } catch (Exception e) {
            Log.d(TAG, "loadUserDataFromFirebase: " + e.getLocalizedMessage());
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void enrolledLp(int lpId, String studentid, final String courseColnId, String multilingualID) {
        if (isMicroCourse) {
            if (courseDataClass.isEnrolled() || mIsEnrolled) {
                Log.d(TAG, "enrolledLp: Already enrolled");
                return;
            }
        }

        final CommonResponse[] commonResponses = new CommonResponse[]{null};
        final int cID = lpId;
        try {
            String enrolllp_url = OustSdkApplication.getContext().getResources().getString(R.string.enrolllp_url);
            enrolllp_url = enrolllp_url.replace("{courseId}", ("" + lpId));
            enrolllp_url = enrolllp_url.replace("{userId}", studentid);
            if (courseColnId != null) {
                enrolllp_url = enrolllp_url.replace("{courseColnId}", courseColnId);
            } else {
                enrolllp_url = enrolllp_url.replace("{courseColnId}", "");
            }
            if (multilingualID != null && !multilingualID.isEmpty()) {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", multilingualID);
            } else {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", "");
            }
            try {
                PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                Log.e("--------", enrolllp_url);
                enrolllp_url = enrolllp_url + "&devicePlatformName=Android";
                Log.e("--------", enrolllp_url);
                enrolllp_url = enrolllp_url + "&appVersion=" + pinfo.versionName;
                Log.e("--------", enrolllp_url);
            } catch (Exception e) {
                Log.e("--------", enrolllp_url);
            }
            enrolllp_url = HttpManager.getAbsoluteUrl(enrolllp_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, enrolllp_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        if (!isMicroCourse) {
                            view.hideLoader();
                        }
                        if (multilingualID != null && !multilingualID.isEmpty()) {
                            OustPreferences.save("multiLingualCourseId", "" + multilingualID);
                            OustPreferences.saveAppInstallVariable("isRefreshCourse", true);
                        }

                        enrollLpDone(commonResponses[0]);
                        OustPreferences.saveAppInstallVariable("IS_ENROLLED_" + cID, true);
                        mIsEnrolled = true;
                        if (isMicroCourse) {
                            view.hideLoader();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null && error.getMessage() != null) {
                        Log.d("Network_error", "" + error.getMessage());
                    }
                    if (!isMicroCourse) {
                        view.hideLoader();
                    }
                    enrollLpDone(commonResponses[0]);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendCourseRating(int no, CourseDataClass courseDataClass, String feedback, String courseColnId) {
        try {
            view.showstored_Popup();
            startShowingReviewModeOptionn();
            List<String> ratecourserequest_list = OustPreferences.getLoacalNotificationMsgs("ratecourserequest_list");
            CourseRating_Request courseRating_request = new CourseRating_Request();
            courseRating_request.setCourseId(("" + courseDataClass.getCourseId()));
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                courseRating_request.setCourseColnId(courseColnId);
            }
            courseRating_request.setUserId(activeUser.getStudentid());
            courseRating_request.setRating(no);
            courseRating_request.setFeedback(feedback);

            Gson gson = new Gson();
            String ackReqStr = gson.toJson(courseRating_request);
            if ((ratecourserequest_list != null) && (ratecourserequest_list.size() > 0)) {
                ratecourserequest_list.add(ackReqStr);
            } else {
                ratecourserequest_list = new ArrayList<>();
                ratecourserequest_list.add(ackReqStr);
            }
            OustPreferences.saveLocalNotificationMsg("ratecourserequest_list", ratecourserequest_list);

            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
            setAcknowledged(true);

            pressClickOnAssesement();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void pressClickOnAssesement() {
        Log.d(TAG, "pressClickOnAssesement: ");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickOnAssessmentIcon();
            }
        }, 400);
    }

    public void hitCertificateRequestUrl(
            final SendCertificateRequest sendCertificateRequest,
            final CourseDataClass courseDataClass,
            final boolean isComingFormCourseCompletePopup) {
        final CommonResponse[] commonResponses = {null};
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentid", sendCertificateRequest.getStudentid());
            jsonObject.put("emailid", sendCertificateRequest.getEmailid());
            jsonObject.put("contentType", sendCertificateRequest.getContentType());
            jsonObject.put("contentId", sendCertificateRequest.getContentId());


            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            String sendcertificate_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcertificate_url);
            sendcertificate_url = HttpManager.getAbsoluteUrl(sendcertificate_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcertificate_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    view.hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    view.gotCertificateToMailResponse(sendCertificateRequest.getEmailid(), courseDataClass, commonResponses[0], isComingFormCourseCompletePopup);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    view.hideCertificateLoader();
                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendcertificate_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    view.hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    view.gotCertificatetomailResponce(sendCertificateRequest.getEmailid(), courseDataClass, commonResponses[0], isComingFormCourseCompletePopup);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    view.hideCertificateLoader();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            Log.d("", "", e);
        }
    }

    private boolean mIsEnrolled;

    public void setEnrolled(boolean mIsEnrolled) {
        this.mIsEnrolled = mIsEnrolled;
    }

    public boolean getEnrolled() {
        return mIsEnrolled;
    }

    public void removeAllVideoFiles() {
        try {
            Log.d(TAG, "removeAllVideoFiles: ");
            //ArrayList<CourseCardClass> courseCardClassArrayList = new ArrayList<>();
            if (courseDataClass != null && courseDataClass.getCourseLevelClassList() != null) {
                for (CourseLevelClass courseLevelClass : courseDataClass.getCourseLevelClassList()) {
                    if (courseLevelClass != null) {
                        //courseLevelClass.getCourseCardClassList()
                        for (DTOCourseCard courseCardClass1 : courseLevelClass.getCourseCardClassList()) {
                            int savedCardID = (int) courseCardClass1.getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);

                            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                                //courseCardClassArrayList.add(courseCardClass);
                                for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                                    if (courseCardClass.getCardMedia().get(k).getData() != null) {
                                        String mediaType = courseCardClass.getCardMedia().get(k).getMediaType();
                                        if (mediaType.equalsIgnoreCase("VIDEO")) {
                                            String fileName = OustMediaTools.getMediaFileName(courseCardClass.getCardMedia().get(k).getData());
                                            try {
                                                final File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                //courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList()
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public CourseDataClass getCourseDataClass() {
        return courseDataClass;
    }

    public void setMicroCourse(boolean microCourse) {
        isMicroCourse = microCourse;
    }

    public void hitInstrumentationForCompletion(Context context) {
        try {
            if (courseDataClass != null && courseDataClass.getCourseId() != 0) {
                InstrumentationFixRequest instrumentationFixRequest = new InstrumentationFixRequest();
                instrumentationFixRequest.setCourseId((int) courseDataClass.getCourseId());
                if (courseDataClass.getContentPlayListId() != 0) {
                    instrumentationFixRequest.setCplId((int) courseDataClass.getContentPlayListId());
                }
                InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                instrumentationHandler.hitInstrumentationFixAPI(context, instrumentationFixRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
