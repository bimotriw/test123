package com.oustme.oustsdk.course_ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.newlearnngmap.LearningMapView;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.SearchCourseCard;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.request.CourseCompleteAcknowledgeRequest;
import com.oustme.oustsdk.request.CourseRating_Request;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseModulePresenter {

    static final String TAG = "CourseModulePresenter";

    ActiveUser activeUser;
    LearningMapView learningMapView;
    int scrWidth = 0;
    int scrHeight = 0;
    int scrollViewHeight = 0;
    int totalNoOfLevels = 1;
    int totalCards = 0;
    int currentLevelNo = 0;
    int lastLevelNo = 0;
    boolean isPathInitialized = false;
    int levelClickedNo;
    int cardNo;
    int currentLevel;
    private boolean isMicroCourse;
    private boolean isMultipleCplEnable;
    private String isCplId;

    //intent data
    long courseId;
    boolean isRegularMode;
    String multilingualCourseId;
    String courseColId;

    boolean isAdaptive = false;
    boolean isSalesMode = false;
    boolean isReviewMode = false;
    boolean isEnrolled;

    UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    CourseDataClass courseDataClass;
    DTOUserCourseData dtoUserCourseData;

    public CourseModulePresenter(LearningMapView learningMapView, int scrWidth, int scrHeight, Bundle bundle) {
        try {
            this.learningMapView = learningMapView;
            this.scrWidth = scrWidth;
            this.scrHeight = scrHeight;

            activeUser = OustAppState.getInstance().getActiveUser();

            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }

            if (bundle != null) {
                courseId = bundle.getLong("learningId");
                multilingualCourseId = bundle.getString("multilingualId");
                courseColId = bundle.getString("courseColnId");
            }

            if (courseId != 0) {
                OustStaticVariableHandling.getInstance().setCurrentLearningPathId((int) courseId);
                String courseUniqueString = "" + activeUser.getStudentKey() + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                if ((courseColId != null) && (!courseColId.isEmpty())) {
                    courseUniqueString = "" + activeUser.getStudentKey() + "" + courseColId + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                }
                long courseUniqueNo = Long.parseLong(courseUniqueString);
                OustStaticVariableHandling.getInstance().setCourseUniqNo(courseUniqueNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    void getLearningMap(final long learningPathId) {
        if (OustSdkTools.checkInternetStatus()) {
            String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
            courseBaseurl = courseBaseurl.replace("{courseId}", "" + learningPathId);
            courseBaseurl = courseBaseurl.replace("{userId}", "" + activeUser.getStudentid());

            courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

            Log.d(TAG, "getLearningMap: " + courseBaseurl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "getLearningMap - onResponse: " + response.toString());
                    Map<String, Object> learningMap = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (learningMap != null) {
                        if (learningMapView != null) {
                            learningMapView.extractCourseData((int) learningPathId, learningMap, null);
                        }
                    } else {
                        if (learningMapView != null) {
                            learningMapView.endActivity();
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (learningMapView != null) {
                        learningMapView.endActivity();
                    }
                }
            });
        } else {
            if (learningMapView != null) {
                learningMapView.endActivity();
            }
        }
    }

    void getBulletinQuesFromFirebase(long learningId) {
        String message = "/courseThread/course" + learningId + "/updateTime";
        ValueEventListener courseThreadListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        learningMapView.setBulletinQuesFromFirebase(dataSnapshot);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                Log.e("FirebaseD", "onCancelled()");
            }
        };
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(courseThreadListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(courseThreadListener, message));
    }

    public void setAdaptive(boolean value) {
        isAdaptive = value;
    }

    public void setSalesMode(boolean isSalesMode) {
        this.isSalesMode = isSalesMode;
    }

    public void setRegularMode(boolean isRegularMode) {
        this.isRegularMode = isRegularMode;
    }

    public void loadUserDataFromFirebase(final int learningPathID, final CourseDataClass courseDataClass, final Map<String, Object> landingMap, String courseColId) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                callingProgressAPI(learningPathID, courseDataClass);
            } else {
                DTOUserCourseData userCourseData = getUserCourseData();
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
                if (((userCourseData != null) && (userCourseData.getCurrentLevel() == 0)) || (OustSdkTools.checkInternetStatus() && (requests.size() == 0))) {
                    if ((landingMap != null) && (landingMap.size() > 0)) {
                        learningMapView.updateUserData(learningPathID, courseDataClass, landingMap);
                    } else {
                        callingProgressAPI(learningPathID, courseDataClass);
                    }
                } else {
                    gotLpDataFromFirebase(courseDataClass);
                }
            }
        } catch (Exception e) {
            gotLpDataFromFirebase(courseDataClass);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void callingProgressAPI(int learningPathID, CourseDataClass courseDataClass) {
        try {
            String courseUserProgressBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_user_progress_api);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{org-id}", "" + tenantName);
            courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{courseId}", "" + learningPathID);
            courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

            courseUserProgressBaseurl = HttpManager.getAbsoluteUrl(courseUserProgressBaseurl);

            Log.d(TAG, "loadUserDataFromFirebase: " + courseUserProgressBaseurl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, courseUserProgressBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "loadUserDataFromFirebase - onResponse: " + response.toString());
                    Map<String, Object> userDataMap = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        userDataMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (learningMapView != null) {
                        learningMapView.updateUserData(learningPathID, courseDataClass, userDataMap);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotLpDataFromFirebase(CourseModulePresenter.this.courseDataClass);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void loadUserDataForRegularMode(final int learningPathID, final CourseDataClass courseDataClass, String courseColId) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                callingProgressAPI(learningPathID, courseDataClass);
            } else {
                DTOUserCourseData userCourseData = getUserCourseData();
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
                if (((userCourseData != null) && (userCourseData.getCurrentLevel() == 0)) || (OustSdkTools.checkInternetStatus() && (requests.size() == 0))) {
                    gotLpDataFromFirebase(courseDataClass);
                } else {
                    gotLpDataFromFirebase(courseDataClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setEnrolled(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public DTOUserCourseData getUserCourseData() {
        if (userCourseScoreDatabaseHandler == null) {
            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        }
        return userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }

    public void addUserData(DTOUserCourseData userCourseData) {
        try {
            if (userCourseScoreDatabaseHandler == null) {
                userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            }
            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, OustStaticVariableHandling.getInstance().getCourseUniqNo());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotLpDataFromFirebase(CourseDataClass courseDataClass) {
        Log.e(TAG, "gotLpDataFromFirebase: ");
        try {
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            this.courseDataClass = courseDataClass;

            DTOUserCourseData userCourseData = getUserCourseData();
            learningMapView.setDownloadCourseIcon(userCourseData);

            if (courseDataClass != null) {
                learningMapView.setLanguage(courseDataClass.getLanguage());
                if (courseDataClass.getLanguage() == null || courseDataClass.getLanguage().isEmpty() ||
                        courseDataClass.getLanguage() != null && !courseDataClass.getLanguage().isEmpty() && courseDataClass.getLanguage().equalsIgnoreCase("en")) {
                    learningMapView.setFontAndText();
                } else {
                    learningMapView.setText();
                }

                if (!OustPreferences.getAppInstallVariable("hideAllCourseLeaderBoard") && !courseDataClass.isHideLeaderBoard()) {
                    learningMapView.showLeaderBoard();
                }

                if (courseDataClass.isShowVirtualCoins()) {
                    learningMapView.showCoins();
                }

                if (!OustPreferences.getAppInstallVariable("hideCourseBulletin") && !courseDataClass.isHideBulletinBoard()) {
                    learningMapView.showBulletinBoard();
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

                try {
                    learningMapView.setCurrentModuleCompleteLevel(((int) dtoUserCourseData.getPresentageComplete()));
                    learningMapView.setLpOcText(dtoUserCourseData.getTotalOc(), courseDataClass.getTotalOc());

                    if (courseDataClass.getCourseLevelClassList() != null) {
                        totalNoOfLevels = courseDataClass.getCourseLevelClassList().size();
                        if (courseDataClass.getMappedAssessmentId() > 0) {
                            totalNoOfLevels++;
                        }

                        if (courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory()) {
                            totalNoOfLevels++;
                        }

                        totalCards = 0;
                        for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                            totalCards += courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size();
                        }
                        userCourseScoreDatabaseHandler.setTotalCards(totalCards, dtoUserCourseData);
                        startLearningMap(false);
                        if (!courseDataClass.isEnrolled() && courseDataClass.getCourseDesclaimerData() == null && courseDataClass.getCardInfo() == null) {
                            clickOnEnrollLp(true);
                        } else {
                            if (isMicroCourse && courseDataClass.getCourseDesclaimerData() == null) {
                                learningMapView.hideLoader();
                            }
                        }
                    } else {
                        learningMapView.hideLoader();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                try {
                    if (!OustPreferences.getAppInstallVariable("disableCourseReviewMode") && !courseDataClass.isDisableReviewMode()) {
                        if (dtoUserCourseData.getPresentageComplete() == 100 && !isAdaptive) {
                            isReviewMode = true;
                            learningMapView.setReviewMode(true);
                            learningMapView.setReviewModeStatus(courseDataClass.isSalesMode());
                            learningMapView.showCourseReviewLayout();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if (courseDataClass.isSalesMode()) {
                    isSalesMode = true;
                    learningMapView.setReviewMode(false);
                    learningMapView.setReviewModeStatus(courseDataClass.isSalesMode());
                    learningMapView.showCourseReviewLayout();
                }
            } else {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.course_data_is_missing));
                if (learningMapView != null) {
                    learningMapView.endActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startLearningMap(boolean updateReviewList) {
        try {
            Log.d(TAG, "inside startLearningMap: updateReviewList:" + updateReviewList);
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (isRegularMode) {
                learningMapView.setCurrentModuleCompleteLevel(((int) dtoUserCourseData.getPresentageComplete()));
            }

            if (isReviewMode || isRegularMode || isSalesMode) {
                if (updateReviewList) {
                    learningMapView.updateReviewList();
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
                        searchCourseLevel.setXp(courseDataClass.getCourseLevelClassList().get(i).getTotalXp());
                        searchCourseLevel.setLevelLock(courseDataClass.getCourseLevelClassList().get(i).isLevelLock());
                        if (i == 0) {
                            searchCourseLevel.setSearchMode(true);
                        }
                        List<SearchCourseCard> searchCourseCards = new ArrayList<>();
                        for (int j = 0; j < courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size(); j++) {
                            if (courseDataClass.isShowQuesInReviewMode() ||
                                    courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardType().equalsIgnoreCase("LEARNING")) {
                                SearchCourseCard searchCourseCard = new SearchCourseCard();
                                if (courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j) != null) {
                                    searchCourseCard.setDescription(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getContent());
                                    searchCourseCard.setName(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardTitle());
                                    searchCourseCard.setId(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardId());
                                    searchCourseCard.setCardType(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getCardType());
                                    searchCourseCard.setXp(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getXp());
                                    searchCourseCard.setSequence(courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(j).getSequence());
                                }
                                searchCourseCards.add(searchCourseCard);
                            }
                        }
                        searchCourseLevel.setSearchCourseCards(searchCourseCards);
                        searchCourseLevelList.add(searchCourseLevel);
                    }
                    learningMapView.setBackLayer(courseDataClass.getLpBgImage());
                    learningMapView.setAllLevelList(searchCourseLevelList);
                    learningMapView.createLevelList(searchCourseLevelList, false);
                    learningMapView.setCertificateVisibility(dtoUserCourseData, courseDataClass);
                    if ((courseDataClass.getCourseName() != null)) {
                        learningMapView.setLpName(courseDataClass.getCourseName());
                    }
                }
            } else {
                if ((courseDataClass != null) && (courseDataClass.getCourseLevelClassList() != null)) {
                    if (isPathInitialized) {
                        learningMapView.addLevelIcons(scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, lastLevelNo, courseDataClass, dtoUserCourseData);
                        learningMapView.addLevelDescription(scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, lastLevelNo, currentLevelNo, courseDataClass, dtoUserCourseData);
                        setAnimation();
                        if (!isAdaptive) {
                            learningMapView.setIndicatorPosition(lastLevelNo, currentLevelNo, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
                        } else {
                            int lastPlayedLevel = OustPreferences.getSavedInt("LAST_PLAYED_LEVEL_" + courseDataClass.getCourseId());
                            currentLevel = 0;
                            if (lastPlayedLevel == 0)
                                currentLevel = 1;
                            else
                                currentLevel = lastPlayedLevel + 1;

                            int cuLevel;
                            if (!isEnrolled)
                                cuLevel = lastPlayedLevel;
                            else
                                cuLevel = lastPlayedLevel + 1;
                            learningMapView.setIndicatorPosition(cuLevel, currentLevel + 1, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
                        }
                    } else {
                        learningMapView.startLearningPath();
                        isPathInitialized = true;
                        if (totalNoOfLevels < 4) {
                            scrollViewHeight = (((totalNoOfLevels)) * (65 * scrHeight / 480)) + (255 * scrHeight / 480);
                        } else {
                            scrollViewHeight = (((totalNoOfLevels)) * (70 * scrHeight / 480)) + (255 * scrHeight / 480);
                        }
                        if (scrollViewHeight < scrHeight) {
                            scrollViewHeight = scrHeight;
                        }
                        startLearningMapBack();
                    }
                }
            }
            learningMapView.checkMultiLingualCourse();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnimation() {
        try {
            if (!dtoUserCourseData.isCourseComplete()) {
                if (totalNoOfLevels > 0) {
                    boolean isAnimRequired = false;
                    if (currentLevelNo != lastLevelNo) {
                        isAnimRequired = true;
                        if (dtoUserCourseData.getPresentageComplete() == 100) {
                            learningMapView.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
                        } else {
                            learningMapView.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
                        }
                    }
                    if (isAnimRequired) {
                        learningMapView.setLevelChangeAnim(currentLevelNo, scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, courseDataClass);
                        lastLevelNo = currentLevelNo;
                    }
                }
            } else {
                if (totalNoOfLevels > 0) {
                    boolean isAnimRequired = false;
                    if (dtoUserCourseData.getCurrentCompleteLevel() != dtoUserCourseData.getLastCompleteLevel()) {
                        isAnimRequired = true;
                        if (dtoUserCourseData.getPresentageComplete() == 100) {
                            learningMapView.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
                        } else {
                            learningMapView.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
                        }
                    }
                    if (isAnimRequired) {
                        learningMapView.setLevelChangeAnim(dtoUserCourseData.getCurrentCompleteLevel(), scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, courseDataClass);
                        userCourseScoreDatabaseHandler.setLastCompleteLevel(dtoUserCourseData.getCurrentCompleteLevel(), dtoUserCourseData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startLearningMapBack() {
        Log.d(TAG, "startLearningMapBack: ");
//        Log.e(TAG, "scrWidth " + scrWidth + " - " + scrHeight);
        try {
            if ((courseDataClass.getCourseName() != null)) {
                learningMapView.setLpName(courseDataClass.getCourseName());
            }
            learningMapView.setBackLine(scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels);
            learningMapView.setBackLayer(courseDataClass.getLpBgImage());
            learningMapView.bringBackFront();
            learningMapView.setLearningStartIcon(scrWidth, scrHeight, scrollViewHeight);
            learningMapView.addLevelIcons(scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, lastLevelNo, courseDataClass, dtoUserCourseData);
            learningMapView.addLevelDescription(scrWidth, scrHeight, scrollViewHeight, totalNoOfLevels, lastLevelNo, currentLevelNo, courseDataClass, dtoUserCourseData);
            learningMapView.setIndicatorPosition(lastLevelNo, currentLevelNo, scrWidth, scrHeight, scrollViewHeight, dtoUserCourseData, courseDataClass);
            learningMapView.setCertificateVisibility(dtoUserCourseData, courseDataClass);

            if (lastLevelNo == 0 && currentLevelNo == 0) {
                learningMapView.addOverLay(scrWidth, scrHeight, scrollViewHeight);
                learningMapView.setLearningStartLabbelIcon(scrWidth, scrHeight, scrollViewHeight);
            }

            if (dtoUserCourseData.getPresentageComplete() == 100) {
                learningMapView.setCurrentLevelPosition(dtoUserCourseData.getCurrentCompleteLevel(), scrHeight, scrollViewHeight);
            } else {
                learningMapView.setCurrentLevelPosition(currentLevelNo, scrHeight, scrollViewHeight);
            }
            setAnimation();
            learningMapView.onScrollPostMeth();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickOnUserManualRow(int levelNo, int cardNo, boolean isRegularMode) {
        try {
            Log.d("onclick", "clickOnUserManualRow: regular:" + isRegularMode + " -- Review:" + isReviewMode);

            if (isSalesMode) {
                levelClickedNo = levelNo;
                String s1 = "" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                long courseUniqueNo = Long.parseLong(s1);

                DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                if (userCourseData.getCurrentLevel() == 0) {
                    this.cardNo = cardNo;
                    clickOnEnrollLp(true);
                } else {
                    learningMapView.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                }
            } else if (isRegularMode) {
                levelClickedNo = levelNo;
                if (levelClickedNo == 0 && !courseDataClass.isEnrolled()) {
                    this.cardNo = cardNo;
                    clickOnEnrollLp(true);
                } else {
                    learningMapView.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                }

            } else if (isReviewMode) {

                levelClickedNo = levelNo;
                learningMapView.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);

            } else {
                if (courseDataClass.isEnrolled()) {
                    if ((courseDataClass.getCourseLevelClassList().size() > (levelNo - 1))) {
                        if (dtoUserCourseData.isCourseComplete()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(levelNo, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(levelNo, dtoUserCourseData);
                        }
                        learningMapView.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        learningMapView.enableClick();
                    }
                } else {
                    levelClickedNo = levelNo;
                    clickOnEnrollLp(true);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*public void clickOnUserManualRow(int levelNo, int cardNo) {
        try {
            if (!isReviewMode) {
                if (courseDataClass.isEnrolled()) {
                    if ((courseDataClass.getCourseLevelClassList().size() > (levelNo - 1))) {
                        if (dtoUserCourseData.isCourseComplete()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(levelNo, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(levelNo, dtoUserCourseData);
                        }
                        learningMapView.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        learningMapView.enableClick();
                    }
                } else {
                    levelClickedNo = levelNo;
                    clickOnEnrollLp(true);
                }
            }
            else {
                levelClickedNo = levelNo;
                if (courseDataClass.isSalesMode()) {
                    String s1 = "" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                    int courseUniqueNo = Integer.parseInt(s1);

                    DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                    if (userCourseData.getCurrentLevel() == 0) {
                        this.cardNo = cardNo;
                        clickOnEnrollLp(true);
                    } else {
                        learningMapView.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                    }
                } else {
                    learningMapView.gotoQuizPageReviewMode(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, cardNo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void clickOnEnrollLp(boolean startCourse) {

        if (isSalesMode) {
            learningMapView.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
        } else if (isRegularMode) {
            learningMapView.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
        } else if (isReviewMode) {
            learningMapView.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
        } else {
            if ((currentLevelNo == 0) && (startCourse)) {
                if (!courseDataClass.getCourseLevelClassList().get(0).isLevelLock()) {
                    learningMapView.sendUnlockPathApi(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
                }
            }
        }

    }

    public void clickOnCourseDownload() {
        learningMapView.downloadCourse(courseDataClass);
    }

    public DTOUserCourseData getDTOUserCourseData() {
        return dtoUserCourseData;
    }

    public void hitCertificateRequestUrl(final SendCertificateRequest sendCertificateRequest,
                                         final CourseDataClass courseDataClass, final boolean isComingFormCourseCompletePopup) {
        final CommonResponse[] commonResponses = {null};
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentid", sendCertificateRequest.getStudentid());
            jsonObject.put("emailid", sendCertificateRequest.getEmailid());
            jsonObject.put("contentType", sendCertificateRequest.getContentType());
            jsonObject.put("contentId", sendCertificateRequest.getContentId());


            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            String sendCertificate_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcertificate_url);
            sendCertificate_url = HttpManager.getAbsoluteUrl(sendCertificate_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendCertificate_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    learningMapView.hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    learningMapView.gotCertificateToMailResponse(sendCertificateRequest.getEmailid(), courseDataClass, commonResponses[0], isComingFormCourseCompletePopup);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    learningMapView.hideCertificateLoader();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendCourseRating(int no, CourseDataClass courseDataClass, String feedback, String courseColId) {
        try {
            learningMapView.showstored_Popup();
            startShowingReviewModeOption();
            List<String> rateCourseRequestList = OustPreferences.getLoacalNotificationMsgs("ratecourserequest_list");
            CourseRating_Request courseRating_request = new CourseRating_Request();
            courseRating_request.setCourseId(("" + courseDataClass.getCourseId()));
            if ((courseColId != null) && (!courseColId.isEmpty())) {
                courseRating_request.setCourseColnId(courseColId);
            }
            courseRating_request.setUserId(activeUser.getStudentid());
            courseRating_request.setRating(no);
            courseRating_request.setFeedback(feedback);

            Gson gson = new Gson();
            String ackReqStr = gson.toJson(courseRating_request);
            if (rateCourseRequestList.size() <= 0) {
                rateCourseRequestList = new ArrayList<>();
            }
            rateCourseRequestList.add(ackReqStr);
            OustPreferences.saveLocalNotificationMsg("ratecourserequest_list", rateCourseRequestList);

            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
            setAcknowledged(true);
            pressClickOnAssessment();
         /*   final Handler handler = new Handler();
            handler.postDelayed(this::clickOnAssessmentIcon, 400);*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startShowingReviewModeOption() {
        //discuss bro for review mode in regular mode(after completion of regular)
        if (!OustPreferences.getAppInstallVariable("disableCourseReviewMode")) {
            if (dtoUserCourseData.getPresentageComplete() == 100) {
                isReviewMode = !courseDataClass.isDisableReviewMode();
                learningMapView.setReviewMode(isReviewMode);
                learningMapView.setReviewModeStatus(courseDataClass.isSalesMode());
            }
        }
    }

    public void setAcknowledged(boolean status) {
        userCourseScoreDatabaseHandler.setAcknowledged(status, dtoUserCourseData);
    }

    public void clickOnAssessmentIcon() {
        try {
            if (currentLevelNo > 0) {
                if ((totalNoOfLevels - 1) <= currentLevelNo) {
                    if ((courseDataClass.getMappedAssessmentId() > 0)) {
                        if (dtoUserCourseData.isCourseCompleted()) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                            learningMapView.gotoAssessmentPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass);
                        } else {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_assessment_unlock));
                            learningMapView.enableClick();
                        }
                    } else {
                        if (!isMicroCourse) {
                            learningMapView.enableClick();
                        } else {
                            learningMapView.endActivity();
                        }
                    }
                } else {
                    if ((totalNoOfLevels - 2) <= currentLevelNo) {
                        if ((courseDataClass.getMappedAssessmentId() > 0)) {
                            if (dtoUserCourseData.isCourseCompleted()) {
                                userCourseScoreDatabaseHandler.setCurrentCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                                userCourseScoreDatabaseHandler.setLastCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                                learningMapView.gotoAssessmentPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass);
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_assessment_unlock));
                                learningMapView.enableClick();
                            }
                        } else {
                            if (!isMicroCourse) {
                                learningMapView.enableClick();
                            } else {
                                learningMapView.endActivity();
                            }
                        }
                    } else {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_assessment_unlock));
                        learningMapView.enableClick();
                    }
                }
            } else {
                learningMapView.enableClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickOnSurveyIcon() {
        try {
            if (currentLevelNo > 0) {
                if ((totalNoOfLevels - 1) <= currentLevelNo || (courseDataClass.getMappedAssessmentId() > 0 && (totalNoOfLevels - 2) <= currentLevelNo)) {
                    if ((courseDataClass.getMappedSurveyId() > 0)) {
                        if (dtoUserCourseData.isCourseCompleted() && dtoUserCourseData.getPresentageComplete() >= 95) {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(totalNoOfLevels, dtoUserCourseData);
                            learningMapView.gotoSurveyPage();
                        } else {
                            if (!dtoUserCourseData.isCourseCompleted()) {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_level_unlock));
                            }
                            learningMapView.enableClick();
                        }
                    } else {
                        if (!isMicroCourse) {
                            learningMapView.enableClick();
                        } else {
                            learningMapView.endActivity();
                        }
                    }
                } else {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_level_unlock));
                    learningMapView.enableClick();
                }
            } else {
                learningMapView.enableClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public CourseDataClass getCourseDataClass() {
        return courseDataClass;
    }

    public void updateLevelDownloadStatus() {
        try {
            dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            learningMapView.updateLevelDownloadStatus(totalNoOfLevels, courseDataClass, dtoUserCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void enrolledLp(int lpId, String studentId, String courseColId, long multilingualID) {
        final CommonResponse[] commonResponses = new CommonResponse[]{null};

        if (isMicroCourse) {
            if (courseDataClass.isEnrolled() || isEnrolled) {
                Log.d(TAG, "enrolledLp: Already enrolled");
                return;
            }
        }

        try {
            String enrollLp_url = OustSdkApplication.getContext().getResources().getString(R.string.enrolllp_url);
            enrollLp_url = enrollLp_url.replace("{courseId}", ("" + lpId));
            enrollLp_url = enrollLp_url.replace("{userId}", studentId);
            if (courseColId != null) {
                enrollLp_url = enrollLp_url.replace("{courseColnId}", courseColId);
            } else {
                enrollLp_url = enrollLp_url.replace("{courseColnId}", "");
            }
            if (multilingualID != 0) {
                enrollLp_url = enrollLp_url.replace("{mlCourseId}", "" + multilingualID);
            } else {
                enrollLp_url = enrollLp_url.replace("{mlCourseId}", "");
            }
            try {
                PackageInfo packageInfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                enrollLp_url = enrollLp_url + "&devicePlatformName=Android";
                enrollLp_url = enrollLp_url + "&appVersion=" + packageInfo.versionName;
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            enrollLp_url = HttpManager.getAbsoluteUrl(enrollLp_url);
            Log.e(TAG, "enrolledLp:--> " + enrollLp_url);
            if (OustSdkTools.checkInternetStatus()) {
                String finalEnrollLp_url = enrollLp_url;
                ApiCallUtils.doNetworkCall(Request.Method.POST, enrollLp_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                            if (commonResponses[0].isSuccess()) {
                                if (!isMicroCourse) {
                                    learningMapView.hideLoader();
                                }
                                if (multilingualID != 0) {
                                    OustPreferences.save("multiLingualCourseId", "" + multilingualID);
                                    OustPreferences.saveAppInstallVariable("isRefreshCourse", true);
                                }
                                OustPreferences.saveAppInstallVariable("IS_ENROLLED_" + lpId, true);
                                isEnrolled = true;
                                enrollLpDone(commonResponses[0]);
                                if (isMicroCourse) {
                                    learningMapView.hideLoader();
                                }
                            } else {
                                InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                                instrumentationMailRequest.setModuleType("COURSE");
                                instrumentationMailRequest.setModuleId(courseId);
                                instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrollLp_url + "\n Error message : Response returning success as false");
                                instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                                InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                                instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);

                                if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                    if (commonResponses[0].getExceptionData().getMessage() != null) {
                                        OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                    } else {
                                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                                    }
                                } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                    OustSdkTools.showToast(commonResponses[0].getError());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                                }
                                learningMapView.endActivity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                            if (!isMicroCourse) {
                                learningMapView.hideLoader();
                            }
                            enrollLpDone(commonResponses[0]);
                            InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                            instrumentationMailRequest.setModuleType("COURSE");
                            instrumentationMailRequest.setModuleId(courseId);
                            instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrollLp_url + "\n Error message : " + e.getMessage());
                            instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                            InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                            instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Network_error", "" + error.getMessage());
                        if (!isMicroCourse) {
                            learningMapView.hideLoader();
                        }
                        enrollLpDone(commonResponses[0]);
                        InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                        instrumentationMailRequest.setModuleType("COURSE");
                        instrumentationMailRequest.setModuleId(courseId);
                        instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrollLp_url + "\n API Error message : " + error.getMessage());
                        instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                        InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                        instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);
                    }
                });
            } else {
                OustSdkTools.showToast("Please check your network connection. Course enrollment need network connection");
                try {
                    learningMapView.endActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void enrollLpDone(CommonResponse commonResponse) {
        try {
            //here need to handle the level one show download
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    currentLevelNo = 1;
                    if (courseDataClass != null)
                        courseDataClass.setEnrolled(true);
                    //courseDataClass
                    if (!isReviewMode && !isRegularMode && !isSalesMode) {
                        if (courseDataClass != null && courseDataClass.isAutoDownload()) {
                            learningMapView.startCourseDownload();
                        }

                        DTOUserCourseData userCourseData = getUserCourseData();
                        userCourseData.setCurrentLevel(1);
                        addUserData(userCourseData);
                        if (!isMicroCourse) {
//                            showDialogForDownload();
                            if (courseDataClass != null && !courseDataClass.isAutoDownload() && !OustPreferences.getAppInstallVariable("course_download") && courseDataClass.isEnableVideoDownload() && OustPreferences.getSavedInt("video_count") > 0) {
                                courseDataClass.setEnableVideoDownload(true);
                            } else {
                                if (courseDataClass != null) {
                                    courseDataClass.setEnableVideoDownload(false);
                                }
                            }
                            startLearningMap(false);
                        }
                    } else if (isSalesMode) {
                        DTOUserCourseData userCourseData = getUserCourseData();
                        userCourseData.setCurrentLevel(1);
                        addUserData(userCourseData);
                        clickOnUserManualRow(levelClickedNo, cardNo, false);
                    } else if (isRegularMode) {
                        DTOUserCourseData userCourseData = getUserCourseData();
                        userCourseData.setCurrentLevel(1);
                        addUserData(userCourseData);
                        clickOnUserManualRow(levelClickedNo, cardNo, isRegularMode);
                    }

                } else {
                    if (commonResponse.getPopup() != null) {
                        learningMapView.showPopup(commonResponse.getPopup());
                    } else if ((commonResponse.getError() != null) && (commonResponse.isSuccess())) {
                        OustSdkTools.showToast(commonResponse.getError());
                    }

                }
            } else {
                learningMapView.showRetry();
//                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                if (isMicroCourse) {
                    learningMapView.endActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCourseComplete(boolean isCourseComplete) {
        userCourseScoreDatabaseHandler.setCourseComplete(isCourseComplete, dtoUserCourseData);
    }

    public void clickOnLevelIcon(int levelNo) {
        try {
            if (isAdaptive) {
                currentLevelNo = OustPreferences.getSavedInt("LAST_PLAYED_LEVEL_" + courseDataClass.getCourseId()) + 1;
                if (currentLevelNo > courseDataClass.getCourseLevelClassList().size()) {
                    currentLevelNo = courseDataClass.getCourseLevelClassList().size();
                }
            }

            if (isMicroCourse) {
                currentLevelNo = 1;
            } else if (isEnrolled && currentLevelNo <= 0) {
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
                        learningMapView.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        learningMapView.enableClick();
                    }
                } else {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.complete_level_unlock));
                    learningMapView.enableClick();
                }
            } else {
                learningMapView.enableClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    public void killActivity() {
        learningMapView.endActivity();
    }

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

            if (dtoUserCourseData.getPresentageComplete() == 90 && dtoUserCourseData.isCourseCompleted()) {
                userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1, dtoUserCourseData);

            } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted() && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedAssessmentId() > 0) {
                userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2, dtoUserCourseData);

            } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1, dtoUserCourseData);
                if (dtoUserCourseData.isMappedAssessmentPassed()) {
                    userCourseScoreDatabaseHandler.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2, dtoUserCourseData);
                }
            }

            if (dtoUserCourseData.getCurrentLevel() != currentLevelNo) {
                currentLevelNo = (int) dtoUserCourseData.getCurrentLevel();
            }
            learningMapView.setCurrentModuleCompleteLevel(((int) dtoUserCourseData.getPresentageComplete()));
            learningMapView.setLpOcText(dtoUserCourseData.getTotalOc(), courseDataClass.getTotalOc());
            startLearningMap(updateReviewList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clickOnLeaderBoard() {
        try {
            learningMapView.gotoLeaderBoard(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(),
                    courseDataClass.getCourseName(), courseDataClass.getBgImg());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setReviewModeStatus(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
        startLearningMap(false);
    }

    public void pressClickOnAssessment() {
        final Handler handler = new Handler();
        handler.postDelayed(this::clickOnAssessmentIcon, 400);
    }

    public void setMicroCourse(boolean microCourse) {
        this.isMicroCourse = microCourse;
    }

    public boolean getEnrolled() {
        return isEnrolled;
    }

    public void hitInstrumentationForCompletion(Context context) {
        try {
            Log.d(TAG, "hitInstrumentationForCompletion: ");
            if (courseDataClass != null && courseDataClass.getCourseId() != 0) {
                //OustSdkTools.showToast("Seems, there is a problem with the course completion.  Please wait, while we sync with the data.");
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

    public void setIsMultipleCplEnable(boolean isMultipleCplEnable) {
        this.isMultipleCplEnable = isMultipleCplEnable;
    }

    public void setMultipleCPlId(String isCplId) {
        this.isCplId = isCplId;
    }

    public void sendAcknowledgedRequest(String courseColId, long courseId) {
        try {
            List<String> acknowledge_course_list = OustPreferences.getLoacalNotificationMsgs("acknowledgecourse_list");
            CourseCompleteAcknowledgeRequest courseCompleteAcknowledgeRequest = new CourseCompleteAcknowledgeRequest();
            courseCompleteAcknowledgeRequest.setStudentid(activeUser.getStudentid());
            if (courseColId != null && (!courseColId.isEmpty())) {
                courseCompleteAcknowledgeRequest.setCourseColnId(Integer.parseInt(courseColId));
            }
            long currentTimeStamp = System.currentTimeMillis();
            courseCompleteAcknowledgeRequest.setAckTimestamp(("" + currentTimeStamp));
            if (courseId != 0) {
                courseCompleteAcknowledgeRequest.setCourseId(String.valueOf(courseId));
            }
            Gson gson = new Gson();
            String ackReqStr = gson.toJson(courseCompleteAcknowledgeRequest);
            if (acknowledge_course_list.size() <= 0) {
                acknowledge_course_list = new ArrayList<>();
            }
            acknowledge_course_list.add(ackReqStr);

            OustPreferences.saveLocalNotificationMsg("acknowledgecourse_list", acknowledge_course_list);

            Intent intent = new Intent(Intent.ACTION_SYNC, null, OustSdkApplication.getContext(), SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
            setAcknowledged(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateReviewModeStatus() {
        try {
            startLearningMap(false);
            startShowingReviewModeOption();
            startLearningMap(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
