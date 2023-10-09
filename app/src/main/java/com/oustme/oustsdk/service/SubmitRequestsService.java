package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
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
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.request.SubmitCourseCardRequestDataV3;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitRequestsService extends IntentService {
    private static final String TAG = "SubmitRequestsService";
    private List<String> requests;
    private String SAVED_REQUESTS = "savedSubmitRequest";
    private SubmitCourseCardRequestData submitCourseCardRequestData;
    private CourseDataClass courseDataClass;
    private int courseId = 0;
    private String courseColnId;
    int noofAttempt = 0;
    public Comparator<DTOUserLevelData> courseUserCardSorter = (s1, s2) -> (int) s1.getSequece() - (int) s2.getSequece();


    public SubmitRequestsService() {
        super(SubmitRequestsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkForDownloadAvailability();
    }


    private void checkForDownloadAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_REQUESTS);
        if (requests.size() > 0) {
            Gson gson = new Gson();
            submitCourseCardRequestData = gson.fromJson(requests.get(0), SubmitCourseCardRequestData.class);
            Log.d(TAG, "checkForDownloadAvailability: " + submitCourseCardRequestData.toString());
            System.out.println(submitCourseCardRequestData.toString());
            sendRequest(submitCourseCardRequestData);
        } else {
            this.stopSelf();
        }
    }

    private void sendRequest(final SubmitCourseCardRequestData submitCourseCardRequestData_old) {
        Gson gson = new GsonBuilder().create();

        SubmitCourseCardRequestDataV3 submitCourseCardRequestDataV3 = new SubmitCourseCardRequestDataV3();
        submitCourseCardRequestDataV3.setCplId(submitCourseCardRequestData_old.getCplId());
        submitCourseCardRequestDataV3.setDeviceToken(submitCourseCardRequestData_old.getDeviceToken());
        submitCourseCardRequestDataV3.setStudentid(submitCourseCardRequestData_old.getStudentid());
        submitCourseCardRequestDataV3.setUserCardResponse(submitCourseCardRequestData_old.getUserCardResponse());
        submitCourseCardRequestDataV3.setOfflineRequest(submitCourseCardRequestData_old.isOfflineRequest());
        submitCourseCardRequestDataV3.setUserCardResponse(submitCourseCardRequestData_old.getUserCardResponse());

        final String jsonParams = gson.toJson(submitCourseCardRequestDataV3);

        String submitCard_urlv3 = this.getResources().getString(R.string.submitCard_url_v4);

        if (submitCourseCardRequestData_old.getMappedAssessmentId() > 0) {
            submitCard_urlv3 = submitCard_urlv3 + "mappedAssessmentId=" + submitCourseCardRequestData_old.getMappedAssessmentId();
        }
        if (submitCourseCardRequestData_old.getMappedSurveyId() > 0) {
            submitCard_urlv3 = submitCard_urlv3 + "&mappedSurveyId=" + submitCourseCardRequestData_old.getMappedSurveyId();
        }

        try {
            submitCard_urlv3 = HttpManager.getAbsoluteUrl(submitCard_urlv3);
            Log.d(TAG, "sendRequest: url:" + submitCard_urlv3);
            Log.d(TAG, "sendRequest: cardsubmitdata:" + jsonParams);

            ApiCallUtils.doNetworkCall(Request.Method.POST, submitCard_urlv3, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: cardsubmitdata:" + response.toString());
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    cardDownloadOver(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cardDownloadOver(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
//                checkCourseCompletedOrNot();
                if (requests.size() > 0) {
                    requests.remove(0);
                }
                OustPreferences.saveLocalNotificationMsg(SAVED_REQUESTS, requests);
                checkForDownloadAvailability();
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        noofAttempt++;
                        if (noofAttempt < 3) {
                            checkForDownloadAvailability();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkCourseCompletedOrNot() {
        try {
            if (submitCourseCardRequestData != null && submitCourseCardRequestData.getUserCardResponse() != null &&
                    submitCourseCardRequestData.getUserCardResponse().size() > 0) {
                for (int i = 0; i < submitCourseCardRequestData.getUserCardResponse().size(); i++) {
                    if (submitCourseCardRequestData.getUserCardResponse().get(i).getCourseId() != 0) {
                        courseId = submitCourseCardRequestData.getUserCardResponse().get(i).getCourseId();
                        courseColnId = submitCourseCardRequestData.getUserCardResponse().get(i).getCourseColnId();
                        break;
                    }
                }
            }
            if (OustSdkTools.checkInternetStatus()) {
                String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                courseBaseurl = courseBaseurl.replace("{courseId}", "" + courseId);
                courseBaseurl = courseBaseurl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

                courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                Log.d(TAG, "getLearningMap: " + courseBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "getLearningMap - onResponse: " + response.toString());
                        Map<String, Object> learningMap = new HashMap<>();
                        CommonTools commonTools = new CommonTools();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if ((learningMap != null) && (learningMap.get("courseId") != null)) {
                            CommonLandingData commonLandingData = new CommonLandingData();
                            commonLandingData.setId("COURSE" + courseId);
                            commonLandingData = commonTools.getCourseCommonData(learningMap, commonLandingData);
                            extractCourseData(learningMap);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCourseData(Map<String, Object> learningMap) {
        try {
            if (learningMap != null) {
                courseDataClass = new CourseDataClass();
                if (learningMap.get("courseId") != null) {
                    courseDataClass.setCourseId(OustSdkTools.convertToLong(learningMap.get("courseId")));
                }

                if (learningMap.get("mappedAssessmentId") != null) {
                    courseDataClass.setMappedAssessmentId(OustSdkTools.convertToLong(learningMap.get("mappedAssessmentId")));
                }

                if (learningMap.get("mappedSurveyId") != null) {
                    courseDataClass.setMappedSurveyId(OustSdkTools.convertToLong(learningMap.get("mappedSurveyId")));
                }
                Object o1 = learningMap.get("levels");
                if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                    List<Object> levelsList = (List<Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (int i = 0; i < levelsList.size(); i++) {
                            if (levelsList.get(i) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(i);
                                courseLevelClass.setLpId((int) courseId);
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                    //  courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }

                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                Log.e("Level Lock", "Level number unlocked " + i + " " + courseLevelClass.isLevelLock());
                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) levelMap.get("cards");
                                        if (cardMap != null) {
                                            for (String key : cardMap.keySet()) {
                                                if (cardMap.get(key) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    assert cardsubMap != null;
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType = "";
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                    }

                                                    if (cardsubMap.get("isIfScormEventBased") != null) {
                                                        courseCardClass.setIfScormEventBased((boolean) cardsubMap.get("isIfScormEventBased"));
                                                    } else {
                                                        courseCardClass.setIfScormEventBased(false);
                                                    }


                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                    }
                                                    Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                    if (cardColorScheme != null) {
                                                        DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                        if (cardColorScheme.get("contentColor") != null) {
                                                            cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                        }
                                                        if (cardColorScheme.get("bgImage") != null) {
                                                            cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                        }
                                                        if (cardColorScheme.get("iconColor") != null) {
                                                            cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                        }
                                                        if (cardColorScheme.get("levelNameColor") != null) {
                                                            cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                        }
                                                        courseCardClass.setCardColorScheme(cardColorScheme1);
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    String cardType = "";
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                    }
                                                    Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                    if (cardColorScheme != null) {
                                                        DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                        if (cardColorScheme.get("contentColor") != null) {
                                                            cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                        }
                                                        if (cardColorScheme.get("bgImage") != null) {
                                                            cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                        }
                                                        if (cardColorScheme.get("iconColor") != null) {
                                                            cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                        }
                                                        if (cardColorScheme.get("levelNameColor") != null) {
                                                            cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                        }
                                                        courseCardClass.setCardColorScheme(cardColorScheme1);
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0 && !courseLevelClassList.get(0).isLevelLock()) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        loadUserDataFromFirebase((int) courseId, courseDataClass);
                    }
                } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId((int) courseId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType = "";
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        loadUserDataFromFirebase((int) courseId, courseDataClass);
                    }
                } else {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final Map<String, Object> levelMap = (Map<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId((int) courseId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }

                                Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardsubMap != null;
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
//                                                if(cardsubMap.get("clCode")!=null){
//                                                    try{
//                                                        courseCardClass.setClCode(LearningCardType.valueOf((String) cardsubMap.get("clCode")));
//                                                    }catch (Exception e){}
//                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType = "";
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    } else {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardsubMap != null;
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        loadUserDataFromFirebase((int) courseId, courseDataClass);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadUserDataFromFirebase(int lpId, CourseDataClass courseDataClass) {
        try {
            ValueEventListener enventInfoListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot courseData) {
                    try {
                        if (courseData.getValue() != null) {
                            final Map<String, Object> userDataMap = (Map<String, Object>) courseData.getValue();
                            updateUserData(lpId, courseDataClass, userDataMap);
                        } else {
                            updateUserData(lpId, courseDataClass, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        updateUserData(lpId, courseDataClass, null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            String msg = "landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course/" + lpId;
            Log.e(TAG, "firebase link " + msg);
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                msg = "landingPage/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/courseColn/" + courseColnId + "/courses/course" + lpId;
            }
            OustFirebaseTools.getRootRef().child(msg).addListenerForSingleValueEvent(enventInfoListener);
            OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateUserData(int lpId, CourseDataClass courseDataClass, Map<String, Object> learningMap) {
        try {
            DTOUserCourseData userCourseData = new DTOUserCourseData();
            boolean CompletedCoursePercentage = false;
            boolean isAllCardCompleted = true;
            boolean courseCompleted = true;
            boolean isAssessmentStatus = false;
            boolean isSurveyStatus = false;

            try {
                if (learningMap != null && learningMap.get("completionPercentage") != null) {
                    Object o3 = learningMap.get("completionPercentage");
                    if (o3.getClass().equals(Long.class)) {
                        userCourseData.setPresentageComplete((long) learningMap.get("completionPercentage"));
                    } else if (o3.getClass().equals(String.class)) {
                        String s3 = (String) o3;
                        userCourseData.setPresentageComplete(Long.parseLong(s3));
                    } else if (o3.getClass().equals(Double.class)) {
                        Double s3 = (Double) o3;
                        long l = (s3).longValue();
                        userCourseData.setPresentageComplete(l);
                    }
                } else {
                    userCourseData.setPresentageComplete(0);
                }

                if (courseDataClass != null && courseDataClass.getMappedAssessmentId() != 0) {
                    isAssessmentStatus = true;
                }

                if (courseDataClass != null && courseDataClass.getMappedSurveyId() != 0) {
                    isSurveyStatus = true;
                }

                if (userCourseData.getPresentageComplete() == 100) {
                    CompletedCoursePercentage = true;
                }

                if (learningMap != null && learningMap.get("currentCourseLevelId") != null) {
                    userCourseData.setCurrentLevelId(OustSdkTools.convertToLong(learningMap.get("currentCourseLevelId")));
                }
                if (learningMap != null && learningMap.get("currentCourseCardId") != null) {
                    userCourseData.setCurrentCard(OustSdkTools.convertToLong(learningMap.get("currentCourseCardId")));
                }
                if (learningMap != null && learningMap.get("courseCompleted") != null) {
                    userCourseData.setCourseCompleted((boolean) learningMap.get("courseCompleted"));
                    courseCompleted = (boolean) learningMap.get("courseCompleted");
                } else {
                    courseCompleted = false;
                    userCourseData.setCourseCompleted(false);
                }
                if (learningMap != null && learningMap.get("mappedAssessmentCompleted") != null) {
                    userCourseData.setMappedAssessmentCompleted((boolean) learningMap.get("mappedAssessmentCompleted"));
                } else {
                    userCourseData.setMappedAssessmentCompleted(false);
                }

                if (learningMap != null && learningMap.get("userOC") != null) {
                    userCourseData.setTotalOc(OustSdkTools.convertToLong(learningMap.get("userOC")));
                }

                if (learningMap != null && learningMap.get("rating") != null) {
                    int rating = (int) (OustSdkTools.convertToLong(learningMap.get("rating")));
                    userCourseData.setMyCourseRating(rating);
                } else {
                    userCourseData.setMyCourseRating(0);
                }
                if (learningMap != null && learningMap.get("mappedAssessment") != null) {
                    Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learningMap.get("mappedAssessment");
                    if (mappedAssessmentMap.get("passed") != null) {
                        userCourseData.setMappedAssessmentPassed((boolean) mappedAssessmentMap.get("passed"));
                    }
                }

                if (learningMap != null && learningMap.get("completionDeadline") != null) {
                    if (courseDataClass != null) {
                        courseDataClass.setCourseDeadline((String) learningMap.get("completionDeadline"));
                    }
                }

                if (learningMap != null && learningMap.get("contentPlayListId") != null) {
                    courseDataClass.setContentPlayListId(OustSdkTools.convertToLong(learningMap.get("contentPlayListId")));
                }

                if (learningMap != null && learningMap.get("cplId") != null) {
                    courseDataClass.setContentPlayListId(OustSdkTools.convertToLong(learningMap.get("contentPlayListId")));
                }
                if (learningMap != null && learningMap.get("contentPlayListSlotId") != null) {
                    courseDataClass.setContentPlayListSlotId(OustSdkTools.convertToLong(learningMap.get("contentPlayListSlotId")));
                }
                if (learningMap != null && learningMap.get("contentPlayListSlotItemId") != null) {
                    courseDataClass.setContentPlayListSlotItemId(OustSdkTools.convertToLong(learningMap.get("contentPlayListSlotItemId")));
                }
                if (learningMap != null && learningMap.get("levels") != null) {
                    Object o1 = learningMap.get("levels");
                    if (o1.getClass().equals(ArrayList.class)) {
                        List<Object> objectList = (List<Object>) o1;
                        if (objectList != null) {
                            List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();
                            for (int k = 0; k < objectList.size(); k++) {
                                if (objectList.get(k) != null) {
                                    final Map<String, Object> levelMap = (Map<String, Object>) objectList.get(k);
                                    if (userLevelDataList == null) {
                                        userLevelDataList = new ArrayList<>();
                                    }
                                    int courseLevelNo = -1;
                                    for (int l = 0; l < userLevelDataList.size(); l++) {
                                        if (userLevelDataList.get(l).getLevelId() == k) {
                                            courseLevelNo = l;
                                        }
                                    }
                                    if (courseDataClass != null && courseDataClass.getCourseLevelClassList() != null) {
                                        for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                            if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                                if (courseLevelNo < 0) {
                                                    userLevelDataList.add(new DTOUserLevelData());
                                                    courseLevelNo = (userLevelDataList.size() - 1);
                                                }
                                                userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                                userLevelDataList.get(courseLevelNo).setLevelId(k);
                                                if (levelMap.get("userLevelOC") != null) {
                                                    userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                                }
                                                if (levelMap.get("userLevelXp") != null) {
                                                    userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
                                                }
                                                if (levelMap.get("locked") != null) {
                                                    userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                                } else {
                                                    userLevelDataList.get(courseLevelNo).setLocked(true);
                                                }
                                                Object o2 = levelMap.get("cards");
                                                if (o2 != null) {
                                                    if (o2.getClass().equals(ArrayList.class)) {
                                                        List<Object> objectCardList = (List<Object>) o2;
                                                        if (objectCardList != null) {
                                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                            for (int j = 0; j < objectCardList.size(); j++) {
                                                                if (objectCardList.get(j) != null) {
                                                                    final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                                    DTOUserCardData userCardData = new DTOUserCardData();
                                                                    if (cardMap.get("userCardAttempt") != null) {
                                                                        userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                                    }
                                                                    if (cardMap.get("cardCompleted") != null) {
                                                                        userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                                        if (isAllCardCompleted) {
                                                                            isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                                        }
                                                                    } else {
                                                                        userCardData.setCardCompleted(false);
                                                                        isAllCardCompleted = false;
                                                                    }

                                                                    if (cardMap.get("cardViewInterval") != null) {
                                                                        userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                                    } else {
                                                                        userCardData.setCardViewInterval(0);
                                                                    }

                                                                    userCardData.setCardId(j);
                                                                    userCardDataList.add(userCardData);
                                                                }
                                                            }
                                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                        }
                                                    } else if (o2.getClass().equals(HashMap.class)) {
                                                        final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                        List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                        for (String cardKey : objectCardMap.keySet()) {
                                                            final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                            DTOUserCardData userCardData = new DTOUserCardData();
                                                            if (cardMap.get("userCardAttempt") != null) {
                                                                userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                            }
                                                            if (cardMap.get("cardCompleted") != null) {
                                                                userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                                if (isAllCardCompleted) {
                                                                    isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                                }
                                                            } else {
                                                                userCardData.setCardCompleted(false);
                                                                isAllCardCompleted = false;
                                                            }

                                                            if (cardMap.get("cardViewInterval") != null) {
                                                                userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                            } else {
                                                                userCardData.setCardViewInterval(0);
                                                            }

                                                            Log.d("TAG", "updateUserData: cardid:" + cardKey + " --- viewinterval:" + userCardData.getCardViewInterval());


                                                            userCardData.setCardId(Integer.parseInt(cardKey));
                                                            userCardDataList.add(userCardData);
                                                        }
                                                        userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            userCourseData.setUserLevelDataList(userLevelDataList);
                        }
                    } else if (o1.getClass().equals(HashMap.class)) {
                        final Map<String, Object> objectMap = (Map<String, Object>) o1;
                        if (objectMap != null) {
                            List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();
                            for (String levelKey : objectMap.keySet()) {
                                final Map<String, Object> levelMap = (Map<String, Object>) objectMap.get(levelKey);
                                if (userLevelDataList == null) {
                                    userLevelDataList = new ArrayList<>();
                                }
                                int courseLevelNo = -1;
                                int k = Integer.parseInt(levelKey);
                                for (int l = 0; l < userLevelDataList.size(); l++) {
                                    if (userLevelDataList.get(l).getLevelId() == k) {
                                        courseLevelNo = l;
                                    }
                                }
                                if (courseDataClass != null && courseDataClass.getCourseLevelClassList() != null) {
                                    for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                        if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                            if (courseLevelNo < 0) {
                                                userLevelDataList.add(new DTOUserLevelData());
                                                courseLevelNo = (userLevelDataList.size() - 1);
                                            }
                                            userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                            userLevelDataList.get(courseLevelNo).setLevelId(k);
                                            if (levelMap.get("userLevelOC") != null) {
                                                userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                            }
                                            if (levelMap.get("userLevelXp") != null) {
                                                userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
                                            }
                                            if (levelMap.get("locked") != null) {
                                                userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                            } else {
                                                userLevelDataList.get(courseLevelNo).setLocked(true);
                                            }
                                            Object o2 = levelMap.get("cards");
                                            if (o2 != null) {
                                                if (o2.getClass().equals(ArrayList.class)) {
                                                    List<Object> objectCardList = (List<Object>) o2;
                                                    List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                    if (objectCardList != null) {
                                                        for (int j = 0; j < objectCardList.size(); j++) {
                                                            if (objectCardList.get(j) != null) {
                                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                                DTOUserCardData userCardData = new DTOUserCardData();
                                                                if (cardMap.get("userCardAttempt") != null) {
                                                                    userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                                }
                                                                if (cardMap.get("cardCompleted") != null) {
                                                                    userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                                    if (isAllCardCompleted) {
                                                                        isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                                    }
                                                                } else {
                                                                    userCardData.setCardCompleted(false);
                                                                    isAllCardCompleted = false;
                                                                }

                                                                if (cardMap.get("cardViewInterval") != null) {
                                                                    userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                                } else {
                                                                    userCardData.setCardViewInterval(0);
                                                                }

                                                                userCardData.setCardId(j);
                                                                userCardDataList.add(userCardData);
                                                            }
                                                        }
                                                    }
                                                    userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                } else if (o2.getClass().equals(HashMap.class)) {
                                                    List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                    final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                    for (String cardKey : objectCardMap.keySet()) {
                                                        final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                        DTOUserCardData userCardData = new DTOUserCardData();
                                                        if (cardMap.get("userCardAttempt") != null) {
                                                            userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                        }
                                                        if (cardMap.get("cardCompleted") != null) {
                                                            userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                            if (isAllCardCompleted) {
                                                                isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                            }
                                                        } else {
                                                            userCardData.setCardCompleted(false);
                                                            isAllCardCompleted = false;
                                                        }

                                                        if (cardMap.get("cardViewInterval") != null) {
                                                            userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                        } else {
                                                            userCardData.setCardViewInterval(0);
                                                        }

                                                        userCardData.setCardId(Integer.parseInt(cardKey));
                                                        userCardDataList.add(userCardData);
                                                    }
                                                    userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Collections.sort(userLevelDataList, courseUserCardSorter);
                            userCourseData.setUserLevelDataList(userLevelDataList);
                        }
                    }

                }

                if (!isAssessmentStatus && !isSurveyStatus) {
                    if (!CompletedCoursePercentage || !courseCompleted) {
                        if (!isAllCardCompleted) {
                            Log.d(TAG, "updateUserData: ");
                            hitInstrumentationForCompletion();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hitInstrumentationForCompletion() {
        try {
            Log.d(TAG, "hitInstrumentationForCompletion: ");
            if (courseDataClass != null && courseDataClass.getCourseId() != 0) {

                InstrumentationFixRequest instrumentationFixRequest = new InstrumentationFixRequest();
                instrumentationFixRequest.setCourseId((int) courseDataClass.getCourseId());
                if (courseDataClass.getContentPlayListId() != 0) {
                    instrumentationFixRequest.setCplId((int) courseDataClass.getContentPlayListId());
                }
                InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                instrumentationHandler.hitInstrumentationFixAPI(this, instrumentationFixRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


}
