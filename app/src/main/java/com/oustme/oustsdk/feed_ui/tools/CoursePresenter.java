package com.oustme.oustsdk.feed_ui.tools;

import android.content.Context;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursePresenter {

    private static final String TAG = "CoursePresenter";

    private CourseView view;
    private int currentLevelNo = 0;

    private CourseDataClass courseDataClass;
    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    private DTOUserCourseData realmUserCourseData;

    public CoursePresenter(CourseView view, String lpIdStr, String courseColnId) {
        try {
            this.view = view;
            String activeUserGet = OustPreferences.get("userdata");
            ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            if ((lpIdStr != null) && (!lpIdStr.isEmpty())) {
                OustStaticVariableHandling.getInstance().setCurrentLearningPathId(Integer.parseInt(lpIdStr));
                String s1 = "" + activeUser.getStudentKey() + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    s1 = "" + activeUser.getStudentKey() + "" + courseColnId + "" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId();
                }
                long courseUniqueNo = Long.parseLong(s1);
                OustStaticVariableHandling.getInstance().setCourseUniqNo(courseUniqueNo);
                //OustStaticVariableHandling.getInstance().setCourseUniqNo(Integer.parseInt(s1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void loadUserDataFromFirebase(final int lpId, final CourseDataClass courseDataClass, final Map<String, Object> landingMap, String courseColnId) {
        try {
            Log.e(TAG, "inside loadUserDataFromFirebase ");
            DTOUserCourseData userCourseData = getUserCourseData();
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
            if (((userCourseData != null) && (userCourseData.getCurrentLevel() == 0)) || (OustSdkTools.checkInternetStatus() && (requests.size() == 0))) {
                if ((landingMap != null) && (landingMap.size() > 0)) {
                    view.updateUserData(lpId, courseDataClass, landingMap);
                } else {
                    String courseUserProgressBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_user_progress_api);
                    String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                    courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{org-id}", "" + tenantName);
                    courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{courseId}", "" + lpId);
                    courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

                    courseUserProgressBaseurl = HttpManager.getAbsoluteUrl(courseUserProgressBaseurl);

                    Log.d(TAG, "loadUserDataFromFirebase: " + courseUserProgressBaseurl);
                    ApiCallUtils.doNetworkCall(Request.Method.GET, courseUserProgressBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Map<String, Object> userDataMap = new HashMap<>();
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                userDataMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            view.updateUserData(lpId, courseDataClass, userDataMap);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            gotLpDataFromFirebase(courseDataClass);
                        }
                    });
                }
            } else {
                gotLpDataFromFirebase(courseDataClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //methode is called when got data from firebase
    public void gotLpDataFromFirebase(CourseDataClass courseDataClass1) {
        realmUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
        this.courseDataClass = courseDataClass1;

        DTOUserCourseData userCourseData = getUserCourseData();
        view.setDownloadCourseIcon(userCourseData);
        try {
            if (realmUserCourseData.getCurrentLevel() > currentLevelNo) {
                currentLevelNo = (int) realmUserCourseData.getCurrentLevel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOUserCourseData getUserCourseData() {
        if (userCourseScoreDatabaseHandler == null) {
            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        }
        return userCourseScoreDatabaseHandler.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }

    public void clickOnCourseDownload() {
        Log.d(TAG, "clickOnCourseDownload: ");
        view.downloadCourse(courseDataClass);
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

    public void updateLevelDownloadStatus() {
        try {
            realmUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            int totalNoofLevels = 1;
            view.updateLevelDownloadStatus(totalNoofLevels, courseDataClass, realmUserCourseData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hitInstrumentationForCompletion(Context context) {
        try {
            Log.d(TAG, "hitInstrumentationForCompletion: ");
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
