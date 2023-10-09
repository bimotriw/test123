package com.oustme.oustsdk.layoutFour.components.myTask;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.oustme.oustsdk.util.AchievementUtils.convertDate;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.api_sdk.models.OustCatalogueData;
import com.oustme.oustsdk.calendar_ui.model.MeetingCalendar;
import com.oustme.oustsdk.firebase.FFContest.BasicQuestionClass;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationManager;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.learning_page.LearningPageResponse;
import com.oustme.oustsdk.response.learning_page.PendingAssessment;
import com.oustme.oustsdk.response.learning_page.PendingCourse;
import com.oustme.oustsdk.response.learning_page.PendingCpl;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.service.CourseNotificationReceiver;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyTaskRepository {
    private static final String TAG = "MyTaskRepository";
    private final MutableLiveData<HashMap<String, CommonLandingData>> liveData;
    private FastestFingerContestData fastestFingerContestData;
    private ActiveUser activeUser;
    private HashMap<String, CommonLandingData> myDeskData;
    private final List<CommonLandingData> dtoCommonLandingList;
    ArrayList<CommonLandingData> commonLandingDataArrayList;
    ArrayList<CommonLandingData> reminderDataCommonLandingArrayList;
    ArrayList<CommonLandingData> commonPendingListData;
    private FirebaseRefClass ffcDataRefClass;
    private FirebaseRefClass ffcQDataRefClass;
    private String notificationContestId;
    private long lastFFFContestId;
    private boolean readMultilingualCplFromFirebase = false;

    public MyTaskRepository() {
        dtoCommonLandingList = new ArrayList<>();
        liveData = new MutableLiveData<>();
        fetchTaskMap();
    }

    public MutableLiveData<HashMap<String, CommonLandingData>> getTaskMap() {
        return liveData;
    }

    private void fetchTaskMap() {
        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            myDeskData = new HashMap<>();
            readMultilingualCplFromFirebase = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE);
            boolean checkCatalogueExistOrNot = OustPreferences.getAppInstallVariable("checkCatalogueInBottomNav");
            runOnUiThread(this::getUserFFContest);
            /*
              getPendingModules() we are getting data Course/Assessment/CPL
             */
            runOnUiThread(this::getPendingModules);
            getUserEvents();
            if (!checkCatalogueExistOrNot) {
                getCatalogueData();
            }
            getUserCoursesCollections();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getPendingModules() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String tenantName = OustPreferences.get("tanentid").trim();
                String pendingModuleUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_learning_page_data);
                pendingModuleUrl = pendingModuleUrl.replace("{orgId}", tenantName);
                pendingModuleUrl = pendingModuleUrl.replace("{userId}", activeUser.getStudentid());
                if (!readMultilingualCplFromFirebase) {
                    pendingModuleUrl = pendingModuleUrl + "?mmCpl=true";
                }
                pendingModuleUrl = HttpManager.getAbsoluteUrl(pendingModuleUrl);
                Log.d(TAG, "getPendingModules-->: " + pendingModuleUrl);

                ApiCallUtils.doNetworkCall(Request.Method.GET, pendingModuleUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optBoolean("success")) {
                            Gson gson = new Gson();
                            LearningPageResponse learningPageResponse = gson.fromJson(response.toString(), LearningPageResponse.class);
                            if (learningPageResponse != null) {
                                OustPreferences.saveintVar("pendingModuleCount", learningPageResponse.getPendingModuleCount());
                                OustPreferences.saveintVar("completedModuleCount", learningPageResponse.getCompletedModuleCount());
                                if ((learningPageResponse.getPendingCourseList() != null && learningPageResponse.getPendingCourseList().size() == 0) &&
                                        (learningPageResponse.getPendingAssessmentList() != null && learningPageResponse.getPendingAssessmentList().size() == 0)
                                        && (learningPageResponse.getPendingCplList() != null && learningPageResponse.getPendingCplList().size() == 0)) {
                                    OustPreferences.save("savePendingListData", "");
                                    liveData.postValue(myDeskData);
                                }
                                try {
                                    commonLandingDataArrayList = new ArrayList<>();
                                    commonPendingListData = new ArrayList<>();
                                    reminderDataCommonLandingArrayList = new ArrayList<>();
                                    OustPreferences.save("savePendingListData", "");
                                    String reminderData = OustPreferences.get("reminderData");
                                    if (reminderData != null && !reminderData.isEmpty()) {
                                        reminderDataCommonLandingArrayList = new Gson().fromJson(reminderData, new TypeToken<List<CommonLandingData>>() {
                                        }.getType());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                                if (learningPageResponse.getPendingCourseList() != null && learningPageResponse.getPendingCourseList().size() > 0) {
                                    for (PendingCourse pendingCourse : learningPageResponse.getPendingCourseList()) {
                                        CommonLandingData commonLandingData = new CommonLandingData();
                                        String id = "COURSE" + pendingCourse.getElementId();
                                        commonLandingData.setAddedOn(pendingCourse.getAddedOn());
                                        commonLandingData.setCompletionPercentage(pendingCourse.getCompletionPercentage());
                                        commonLandingData.setId(id);
                                        commonLandingData.setEnrolled(pendingCourse.getEnrolled());
                                        commonLandingData.setEnrolledDateTime(pendingCourse.getEnrolledDateTime());
                                        commonLandingData.setType("COURSE");
                                        commonLandingData.setName(pendingCourse.getName());
                                        commonLandingData.setDescription(pendingCourse.getDescription());
                                        commonLandingData.setIcon(pendingCourse.getThumbnail());
                                        commonLandingData.setBanner(pendingCourse.getBanner());
                                        commonLandingData.setTotalOc(pendingCourse.getTotalCoins());
                                        commonLandingData.setTime(pendingCourse.getContentDuration());
                                        commonLandingData.setReminderNotificationInterval(pendingCourse.getReminderNotificationInterval());
                                        commonLandingData.setMode(pendingCourse.getMode());

                                        boolean canAdd = commonLandingData.getMode() == null || !commonLandingData.getMode().equalsIgnoreCase("ARCHIVED");

                                        if (canAdd) {
                                            if (commonLandingData.getAddedOn() != null && !commonLandingData.getAddedOn().isEmpty() && !commonLandingData.getAddedOn().equalsIgnoreCase("0")) {
                                                myDeskData.put(id, commonLandingData);
                                                if (commonLandingData.getReminderNotificationInterval() != 0) {
                                                    commonPendingListData.add(commonLandingData);
                                                    String pendingListData = new Gson().toJson(commonPendingListData);
                                                    OustPreferences.save("savePendingListData", pendingListData);
                                                    reminderNotification(commonLandingData);
                                                }
                                            }
                                        } else {
                                            myDeskData.remove(id);
                                            removeReminderNotification(commonLandingData);
                                        }
                                    }
                                    liveData.postValue(myDeskData);
                                }

                                if (learningPageResponse.getPendingAssessmentList() != null && learningPageResponse.getPendingAssessmentList().size() > 0) {
                                    for (PendingAssessment pendingAssessment : learningPageResponse.getPendingAssessmentList()) {
                                        CommonLandingData commonLandingData = new CommonLandingData();
                                        String id = "ASSESSMENT" + pendingAssessment.getElementId();
                                        commonLandingData.setAddedOn(pendingAssessment.getAddedOn());
                                        commonLandingData.setCompletionPercentage(pendingAssessment.getCompletionPercentage());
                                        commonLandingData.setId(id);
                                        commonLandingData.setEnrolled(pendingAssessment.getEnrolled());
                                        commonLandingData.setEnrolledDateTime(pendingAssessment.getEnrolledTime());
                                        commonLandingData.setType("ASSESSMENT");
                                        commonLandingData.setName(pendingAssessment.getName());
                                        commonLandingData.setDescription(pendingAssessment.getDescription());
                                        commonLandingData.setIcon(pendingAssessment.getThumbnail());
                                        commonLandingData.setBanner(pendingAssessment.getBanner());
                                        commonLandingData.setTime(pendingAssessment.getContentDuration());
                                        commonLandingData.setReminderNotificationInterval(pendingAssessment.getReminderNotificationInterval());
                                        commonLandingData.setMode(pendingAssessment.getMode());
                                        commonLandingData.setRecurring(pendingAssessment.getRecurring());

                                        boolean canAdd = commonLandingData.getMode() == null || !commonLandingData.getMode().equalsIgnoreCase("ARCHIVED");

                                        if (canAdd) {
                                            if (commonLandingData.getAddedOn() != null && !commonLandingData.getAddedOn().isEmpty() && !commonLandingData.getAddedOn().equalsIgnoreCase("0")) {
                                                myDeskData.put(id, commonLandingData);
                                                if (commonLandingData.getReminderNotificationInterval() != 0) {
                                                    commonPendingListData.add(commonLandingData);
                                                    String pendingListData = new Gson().toJson(commonPendingListData);
                                                    OustPreferences.save("savePendingListData", pendingListData);
                                                    reminderNotification(commonLandingData);
                                                }
                                            }
                                        } else {
                                            myDeskData.remove(id);
                                            removeReminderNotification(commonLandingData);
                                        }
                                    }
                                    liveData.postValue(myDeskData);
                                }

                                if (learningPageResponse.getPendingCplList() != null && learningPageResponse.getPendingCplList().size() > 0) {
                                    for (PendingCpl pendingCpl : learningPageResponse.getPendingCplList()) {
                                        CommonLandingData commonLandingData = new CommonLandingData();
                                        String id = "CPL" + pendingCpl.getElementId();
                                        String type = pendingCpl.getType();
                                        commonLandingData.setAddedOn(pendingCpl.getAddedOn());
                                        commonLandingData.setCompletionPercentage(pendingCpl.getCompletionPercentage());
                                        commonLandingData.setId(id);
                                        commonLandingData.setEnrolled(pendingCpl.getEnrolled());
                                        if (type != null && !type.isEmpty()) {
                                            if (type.equalsIgnoreCase("MULTILINGUAL")) {
                                                commonLandingData.setType(type);
                                            } else {
                                                commonLandingData.setType("CPL");
                                            }
                                        } else {
                                            commonLandingData.setType("CPL");
                                        }
                                        commonLandingData.setName(pendingCpl.getCplName());
                                        commonLandingData.setDescription(pendingCpl.getCplDescription());
                                        commonLandingData.setIcon(pendingCpl.getThumbnail());
                                        commonLandingData.setActiveChildCpl(pendingCpl.getActiveChildCpl());
                                        commonLandingData.setParentCplId(pendingCpl.getParentCplId());

                                        if (commonLandingData.getAddedOn() != null && !commonLandingData.getAddedOn().isEmpty() && !commonLandingData.getAddedOn().equalsIgnoreCase("0")) {
                                            myDeskData.put(id, commonLandingData);
                                        } else {
                                            myDeskData.remove("CPL" + pendingCpl.getElementId());
                                        }
                                    }
                                    liveData.postValue(myDeskData);
                                }

                                try {
                                    OustPreferences.save("savePendingListData", "");
                                    String pendingListData = new Gson().toJson(commonPendingListData);
                                    OustPreferences.save("savePendingListData", pendingListData);

                                    if (reminderDataCommonLandingArrayList != null && reminderDataCommonLandingArrayList.size() > 0 &&
                                            commonPendingListData != null && commonPendingListData.size() > 0) {
                                        for (int i = 0; i < reminderDataCommonLandingArrayList.size(); i++) {
                                            boolean dataFound = false;
                                            for (CommonLandingData commonPendingListData : commonPendingListData) {
                                                if (reminderDataCommonLandingArrayList.get(i).getId() != null && !reminderDataCommonLandingArrayList.get(i).getId().isEmpty() &&
                                                        reminderDataCommonLandingArrayList.get(i).getId().equalsIgnoreCase(commonPendingListData.getId())) {
                                                    dataFound = true;
                                                    break;
                                                }
                                            }
                                            if (!dataFound) {
                                                removeReminderNotification(reminderDataCommonLandingArrayList.get(i));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            } else {
                                OustPreferences.saveintVar("pendingModuleCount", 0);
                                OustPreferences.saveintVar("completedModuleCount", 0);
                                OustPreferences.save("savePendingListData", "");
                                liveData.postValue(myDeskData);
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustPreferences.saveintVar("pendingModuleCount", 0);
                        OustPreferences.saveintVar("completedModuleCount", 0);
                        OustPreferences.save("savePendingListData", "");
                        liveData.postValue(myDeskData);
                    }
                });
            }
        } catch (Exception e) {
            OustPreferences.saveintVar("pendingModuleCount", 0);
            OustPreferences.saveintVar("completedModuleCount", 0);
            OustPreferences.save("savePendingListData", "");
            liveData.postValue(myDeskData);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserEvents() {
        try {
            String node = "/landingPage/" + activeUser.getStudentKey() + "/meetingCalendar";
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> meetingUserDataMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (meetingUserDataMap != null) {
                            for (String meetingId : meetingUserDataMap.keySet()) {
                                final HashMap<String, Object> meetingUserData = (HashMap<String, Object>) meetingUserDataMap.get(meetingId);
                                Gson gson = new Gson();
                                JsonElement meetingCalendarElement = gson.toJsonTree(meetingUserData);
                                MeetingCalendar meetingCalendar = gson.fromJson(meetingCalendarElement, MeetingCalendar.class);

                                if (meetingCalendar != null) {
                                    String id = "MEETINGCALENDAR" + meetingCalendar.getMeetingId();
                                    if (myDeskData.get(id) != null) {
                                        CommonLandingData commonLandingData = myDeskData.get(id);
                                        if (commonLandingData != null) {
                                            commonLandingData.setName(meetingCalendar.getClassTitle());
                                            commonLandingData.setType(meetingCalendar.getEventType());
                                            commonLandingData.setIcon(meetingCalendar.getThumbnailImage());
                                            commonLandingData.setBanner(meetingCalendar.getBannerImg());
                                            commonLandingData.setAddedOn(meetingCalendar.getAddedOn());
                                            commonLandingData.setStartTime(meetingCalendar.getMeetingStartTime());
                                            commonLandingData.setEndTime(meetingCalendar.getMeetingEndTime());
                                            commonLandingData.setTimeZone(meetingCalendar.getTimeZone());
                                            commonLandingData.setId(id);
                                            commonLandingData.setMeetingCalendar(meetingCalendar);
                                            myDeskData.put(id, commonLandingData);
                                            if (meetingCalendar.getAttendStatus() != null) {
                                                if (meetingCalendar.getAttendStatus().equalsIgnoreCase("ATTENDING") ||
                                                        meetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING") ||
                                                        meetingCalendar.getAttendStatus().equalsIgnoreCase("Not interested")) {
                                                    commonLandingData.setCompletionPercentage(100);
                                                    myDeskData.remove(id);
                                                }
                                            }

                                            if (meetingCalendar.getMeetingEndTime() < System.currentTimeMillis()) {
                                                commonLandingData.setCompletionPercentage(100);
                                                myDeskData.remove(id);
                                            }
                                            liveData.postValue(myDeskData);
                                        }
                                    } else {
                                        CommonLandingData commonLandingData = new CommonLandingData();
                                        commonLandingData.setName(meetingCalendar.getClassTitle());
                                        commonLandingData.setType(meetingCalendar.getEventType());
                                        commonLandingData.setIcon(meetingCalendar.getThumbnailImage());
                                        commonLandingData.setBanner(meetingCalendar.getBannerImg());
                                        commonLandingData.setAddedOn(meetingCalendar.getAddedOn());
                                        commonLandingData.setStartTime(meetingCalendar.getMeetingStartTime());
                                        commonLandingData.setEndTime(meetingCalendar.getMeetingEndTime());
                                        commonLandingData.setTimeZone(meetingCalendar.getTimeZone());
                                        commonLandingData.setId(id);
                                        commonLandingData.setMeetingCalendar(meetingCalendar);
                                        boolean canAdd = true;
                                        if (meetingCalendar.getAttendStatus() != null) {

                                            if (meetingCalendar.getAttendStatus().equalsIgnoreCase("ATTENDING") ||
                                                    meetingCalendar.getAttendStatus().equalsIgnoreCase("NOT_ATTENDING") ||
                                                    meetingCalendar.getAttendStatus().equalsIgnoreCase("Not interested")) {
                                                commonLandingData.setCompletionPercentage(100);
                                                canAdd = false;
                                                myDeskData.remove(id);
                                            }
                                        }

                                        if (meetingCalendar.getMeetingEndTime() < System.currentTimeMillis()) {
                                            commonLandingData.setCompletionPercentage(100);
                                            canAdd = false;
                                            myDeskData.remove(id);
                                        }
                                        if (canAdd) {
                                            myDeskData.put(id, commonLandingData);
                                        }
                                        liveData.postValue(myDeskData);
                                    }

                                    String msg1 = ("/meeting/meeting" + meetingCalendar.getMeetingId());
                                    ValueEventListener eventBaseListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshotBase) {
                                            try {
                                                if (null != dataSnapshotBase.getValue()) {
                                                    final Map<String, Object> meetingBaseData = (Map<String, Object>) dataSnapshotBase.getValue();
                                                    if (meetingBaseData != null) {
                                                        Gson gson = new Gson();
                                                        JsonElement meetingBaseElement = gson.toJsonTree(meetingBaseData);
                                                        MeetingCalendar meetingCalendarBase = gson.fromJson(meetingBaseElement, MeetingCalendar.class);

                                                        String id = ("MEETINGCALENDAR" + meetingCalendarBase.getMeetingId());

                                                        CommonLandingData commonLandingData = myDeskData.get(id);
                                                        if (commonLandingData != null) {
                                                            commonLandingData.setName(meetingCalendarBase.getClassTitle());
                                                            commonLandingData.setType(meetingCalendarBase.getEventType());
                                                            commonLandingData.setIcon(meetingCalendarBase.getThumbnailImage());
                                                            commonLandingData.setBanner(meetingCalendarBase.getBannerImg());
                                                            commonLandingData.setStartTime(meetingCalendarBase.getMeetingStartTime());
                                                            commonLandingData.setEndTime(meetingCalendarBase.getMeetingEndTime());
                                                            commonLandingData.setTimeZone(meetingCalendarBase.getTimeZone());
                                                            commonLandingData.setEnrollCount(meetingCalendarBase.getTotalEnrolledCount());
                                                            commonLandingData.setId(id);
                                                            commonLandingData.setMeetingCalendar(meetingCalendarBase);
                                                            if (meetingCalendarBase.getMeetingEndTime() != 0 &&
                                                                    System.currentTimeMillis() < meetingCalendarBase.getMeetingEndTime()) {
                                                                myDeskData.put(id, commonLandingData);
                                                            } else {
                                                                myDeskData.remove(id);

                                                            }
                                                            liveData.postValue(myDeskData);
                                                        }
                                                    }
                                                } else {
                                                    String eventId = dataSnapshotBase.getKey();
                                                    assert eventId != null;
                                                    eventId = "MEETINGCALENDAR" + (eventId.replace("meeting", ""));
                                                    myDeskData.remove(eventId);
                                                    liveData.postValue(myDeskData);
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "caught exception inside set singelton ", e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    };
                                    OustFirebaseTools.getRootRef().child(msg1).addValueEventListener(eventBaseListener);
                                    OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventBaseListener, msg1));
                                    OustFirebaseTools.getRootRef().child(msg1).keepSynced(true);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            DatabaseReference meetingRef = OustFirebaseTools.getRootRef().child(node);
            Query query = meetingRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(eventListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserCoursesCollections() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn";
            Log.d(TAG, "getUserCoursesCollections: " + message);
            ValueEventListener courseCollectionListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        if (lpMap.get("elementId") != null) {
                                            String id = "COLLECTION" + i;
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                if (commonLandingData != null) {
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                }

                                                myDeskData.put(id, commonLandingData);
                                            } else {
                                                CommonLandingData commonLandingData = new CommonLandingData();
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COLLECTION");
                                                commonLandingData.setId(id);
                                                myDeskData.put(id, commonLandingData);
                                            }
                                        }
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : lpMainMap.keySet()) {
                                    if ((lpMainMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(courseKey);
                                        if (lpMap != null) {
                                            if (lpMap.get("elementId") != null) {
                                                String id = "COLLECTION" + courseKey;
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    if (commonLandingData != null) {
                                                        commonLandingData.setType("COLLECTION");
                                                        commonLandingData.setId(id);
                                                    }

                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        liveData.postValue(myDeskData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(courseCollectionListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(courseCollectionListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCatalogueData() {
        try {
            String message = "/landingPage/" + activeUser.getStudentKey() + "/catalogue";
            ValueEventListener catalogueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            extractCatalogueData(dataSnapshot);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: error" + error);
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(catalogueListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(catalogueListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCatalogueData(DataSnapshot dataSnapshot) {
        try {
            OustCatalogueData oustCatalogueData = new OustCatalogueData();
            CommonLandingData commonLandingData = new CommonLandingData();
            Object o1 = dataSnapshot.getValue();
            if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                List<Object> lpMainList = (List<Object>) dataSnapshot.getValue();
                Map<String, Object> lpMap = (Map<String, Object>) lpMainList.get(0);
                if (lpMap != null) {
                    if (lpMap.containsKey("bannerImg")) {
                        oustCatalogueData.setBanner((String) lpMap.get("bannerImg"));
                    }

                    if (lpMap.containsKey("elementId")) {
                        oustCatalogueData.setCatalogueId(OustSdkTools.convertToLong(lpMap.get("elementId")));
                    }

                    if (lpMap.containsKey("name")) {
                        oustCatalogueData.setTitle((String) lpMap.get("name"));
                    }
                }

            } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                Map<String, Object> mainMap = null;
                //String key = "";
                for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                    mainMap = (Map<String, Object>) entry.getValue();
                    //key = entry.getKey();
                }

                if (mainMap.containsKey("bannerImg")) {
                    oustCatalogueData.setBanner((String) mainMap.get("bannerImg"));
                }

                if (mainMap.containsKey("elementId")) {
                    oustCatalogueData.setCatalogueId(OustSdkTools.convertToLong(mainMap.get("elementId")));
                }

                if (mainMap.containsKey("name")) {
                    oustCatalogueData.setTitle((String) mainMap.get("name"));
                }
                commonLandingData.setBanner(oustCatalogueData.getBanner());
                commonLandingData.setId("CATALOGUE" + oustCatalogueData.getCatalogueId());
                commonLandingData.setType("CATALOGUE");
                commonLandingData.setName(oustCatalogueData.getTitle());

                myDeskData.put(commonLandingData.getId(), commonLandingData);
                liveData.postValue(myDeskData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void reminderNotification(CommonLandingData commonLandingData) {
        try {
            if (!OustPreferences.getAppInstallVariable("sendPushNotifications")) {
                return;
            }
            if (commonLandingData != null) {
                AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                String alertTitle = commonLandingData.getName();
                String alertType = String.valueOf(GCMType.COURSE_REMINDER);
                String alertContent = "Hello " + activeUser.getUserDisplayName() + ", Reminder to please complete " + commonLandingData.getName();
                String courseId = commonLandingData.getId();
                int requestCode = 0;
                int contentId = 0;
                if (courseId.toUpperCase().contains("COURSE")) {
                    courseId = courseId.toUpperCase().replace("COURSE", "");
                    requestCode = Integer.parseInt("1" + courseId);
                    contentId = Integer.parseInt(courseId);
                } else if (courseId.toUpperCase().contains("ASSESSMENT")) {
                    courseId = courseId.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + courseId);
                    contentId = Integer.parseInt(courseId);
                    alertType = String.valueOf(GCMType.ASSESSMENT_REMINDER);
                }
                if (commonLandingData.getReminderNotificationInterval() != 0 && commonLandingData.getCompletionPercentage() < 100) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, (int) commonLandingData.getReminderNotificationInterval());
                    boolean deadlineOver = false;
                    if (commonLandingData.getCompletionDeadline() != null && !commonLandingData.getCompletionDeadline().isEmpty()) {
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String aa = convertDate(commonLandingData.getCompletionDeadline(), "yyyy-MM-dd HH:mm:ss");
                        Date completionDate = simpleDateFormat.parse(aa);
                        assert completionDate != null;
                        String contentDeadLine = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(completionDate);
                        if (calendar.getTimeInMillis() < completionDate.getTime()) {
                            alertContent = alertContent + " before " + contentDeadLine;
                        } else {
                            deadlineOver = true;
                        }
                    }
                    Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                    intent.putExtra("alertTitle", alertTitle);
                    intent.putExtra("alertContent", alertContent);
                    intent.putExtra("alertImage", commonLandingData.getIcon());
                    intent.putExtra("alertType", alertType);
                    intent.putExtra("alertId", contentId);
                    intent.putExtra("requestCode", requestCode);
                    intent.putExtra("time", commonLandingData.getCompletionDeadline());
                    PendingIntent pendingIntentOnCreate;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentOnCreate = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntentOnCreate = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                    }

                    boolean isAlready = (pendingIntentOnCreate != null);
                    if (!isAlready && !deadlineOver) {
                        commonLandingDataArrayList.add(commonLandingData);
                        String reminderData = new Gson().toJson(commonLandingDataArrayList);
                        OustPreferences.save("reminderData", reminderData);
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } else {
                            pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }
                        Log.e("intenttime", calendar.getTimeInMillis() + " - " + requestCode);
                        assert alarmMgr != null;
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), commonLandingData.getReminderNotificationInterval() * 3600000, pendingIntent);
                        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  180000, pendingIntent);
                    }
                    if (deadlineOver) {
                        if (isAlready) {
                            PendingIntent pendingIntent;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            }
                            assert alarmMgr != null;
                            alarmMgr.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    }
                } else if (commonLandingData.getCompletionPercentage() >= 100) {
                    removeReminderNotification(commonLandingData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeReminderNotification(CommonLandingData commonLandingData) {
        try {
            if (commonLandingData != null) {
                Log.e(TAG, "onReceive: --> removeReminderNotification--> -id-> " + commonLandingData.getId() + "  Type---> " + commonLandingData.getType());
                ArrayList<CommonLandingData> commonLandingDataArrayList = OustStaticVariableHandling.getInstance().getReminderNotification();
                int requestCode = 0;
                String moduleId = commonLandingData.getId();
                if (moduleId.toUpperCase().contains("COURSE")) {
                    moduleId = moduleId.toUpperCase().replace("COURSE", "");
                    requestCode = Integer.parseInt("1" + moduleId);
                } else if (moduleId.toUpperCase().contains("ASSESSMENT")) {
                    moduleId = moduleId.toUpperCase().replace("ASSESSMENT", "");
                    requestCode = Integer.parseInt("2" + moduleId);
                }
                AlarmManager alarmMgr = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(OustSdkApplication.getContext(), ReminderNotificationManager.class);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
                }

                boolean isAlready = (pendingIntent != null);
                if (isAlready) {
                    PendingIntent pendingIntentCancel;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                    } else {
                        pendingIntentCancel = PendingIntent.getBroadcast(OustSdkApplication.getContext(), requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                    assert alarmMgr != null;
                    alarmMgr.cancel(pendingIntentCancel);
                    pendingIntentCancel.cancel();
                }
                if (commonLandingDataArrayList != null && commonLandingDataArrayList.size() != 0) {
                    if (commonLandingDataArrayList.contains(commonLandingData)) {
                        commonLandingDataArrayList.remove(commonLandingData);
                        String reminderData = new Gson().toJson(commonLandingDataArrayList);

                        OustPreferences.save("reminderData", reminderData);
                        OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);

                    } else {
                        int position = findCommonLandingData(commonLandingDataArrayList, commonLandingData.getId());
                        if (position >= 0) {
                            commonLandingDataArrayList.remove(position);
                            String reminderData = new Gson().toJson(commonLandingDataArrayList);
                            OustPreferences.save("reminderData", reminderData);
                            OustStaticVariableHandling.getInstance().setReminderNotification(commonLandingDataArrayList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static int findCommonLandingData(ArrayList<CommonLandingData> a, String id) {

        for (int i = 0; i < a.size(); i++)
            if (a.get(i).getId().equalsIgnoreCase(id))
                return i;

        return -1;
    }

    private void getUserFFContest() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/f3c";
            Log.e("TAG", "getUserFFContest: " + message);
            ValueEventListener myFFCListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        if (dataSnapshot.getValue() != null) {
                            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)) {
                                // isContest = true;
                            }
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> ffcMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (ffcMap.get("elementId") != null) {
                                    long contestId = (long) ffcMap.get("elementId");
                                    if (fastestFingerContestData == null) {
                                        fastestFingerContestData = new FastestFingerContestData();
                                    }
                                    int lastContestId = OustPreferences.getSavedInt("lastContestTime");
                                    if ((lastContestId > 0) && (lastContestId != contestId)) {
                                        OustPreferences.clear("contestScore");
                                    }
                                    OustPreferences.saveintVar("lastContestTime", (int) contestId);
                                    if (fastestFingerContestData.getFfcId() != contestId) {
                                        lastFFFContestId = fastestFingerContestData.getFfcId();
                                        fastestFingerContestData = new FastestFingerContestData();
                                        fastestFingerContestData.setFfcId(contestId);
                                        OustStaticVariableHandling.getInstance().setContestOver(false);
                                        removeFFCDataListener();
                                        fetchFFCData(("" + fastestFingerContestData.getFfcId()));
                                        fetchQData(("" + fastestFingerContestData.getFfcId()));
                                    }

                                    if (ffcMap.get("enrolled") != null) {
                                        fastestFingerContestData.setEnrolled((boolean) ffcMap.get("enrolled"));
                                        ffcBannerStatus();
                                    }

                                    if (OustPreferences.getAppInstallVariable("sendPushNotifications")) {
                                        AlarmManager manager = (AlarmManager) OustSdkApplication.getContext().getSystemService(Context.ALARM_SERVICE);
                                        Intent f3cAlarmIntent = new Intent(OustSdkApplication.getContext(), CourseNotificationReceiver.class);
                                        PendingIntent f3cPendingIntent;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            f3cPendingIntent = PendingIntent.getService(OustSdkApplication.getContext(), 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                                        } else {
                                            f3cPendingIntent = PendingIntent.getService(OustSdkApplication.getContext(), 0, f3cAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        }
                                        Calendar f3cCalendar = Calendar.getInstance();
                                        f3cCalendar.setTimeInMillis(System.currentTimeMillis());
                                        manager.setRepeating(AlarmManager.RTC_WAKEUP, f3cCalendar.getTimeInMillis(), 30 * 1000, f3cPendingIntent);
                                    }
                                }
                            }
                        } else {
                            OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };


            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            newsfeedRef.keepSynced(true);
            newsfeedRef.addValueEventListener(myFFCListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myFFCListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void ffcBannerStatus() {
        try {
            Log.d(TAG, "ffcBannerStatus isContestLive-: " + OustStaticVariableHandling.getInstance().isContestLive());
            Log.d(TAG, "ffcBannerStatus- isEnrolled: " + fastestFingerContestData.isEnrolled());
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                CommonLandingData dtoFFFCCommonLandingData = new CommonLandingData();
                dtoFFFCCommonLandingData.setFastestFingerContestData(fastestFingerContestData);
                dtoFFFCCommonLandingData.setName(fastestFingerContestData.getName());
                dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getBanner());
                dtoFFFCCommonLandingData.setId("f3c" + fastestFingerContestData.getFfcId());
                dtoFFFCCommonLandingData.setEnrollCount(fastestFingerContestData.getEnrolledCount());
                dtoFFFCCommonLandingData.setStartTime(fastestFingerContestData.getStartTime());
                dtoFFFCCommonLandingData.setEndTime(fastestFingerContestData.getEndTime());
                if (fastestFingerContestData.getDescription() != null) {
                    dtoFFFCCommonLandingData.setDescription(fastestFingerContestData.getDescription());
                } else {
                    dtoFFFCCommonLandingData.setDescription("");
                }
                dtoFFFCCommonLandingData.setType("FFF_CONTEXT");
                String contestnotification_message = OustPreferences.get("contestnotification_message");

                if ((fastestFingerContestData.isEnrolled())) {

                    if ((fastestFingerContestData.getPlayBanner() != null) && (!fastestFingerContestData.getPlayBanner().isEmpty())) {
                        dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getPlayBanner());
                        saveData(dtoFFFCCommonLandingData);
                    } else {
                        if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                            dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getJoinBanner());
                            saveData(dtoFFFCCommonLandingData);
                        }
                    }

                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getRrBanner());
                                    saveData(dtoFFFCCommonLandingData);
                                }
                            }
                        }
                    }
                } else {
                    if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                        dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getJoinBanner());
                        saveData(dtoFFFCCommonLandingData);
                    }
                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoFFFCCommonLandingData.setBanner(fastestFingerContestData.getRrBanner());
                                    saveData(dtoFFFCCommonLandingData);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void saveData(CommonLandingData commonLandingData) {
        try {
            if (commonLandingData != null) {
                if (dtoCommonLandingList != null && dtoCommonLandingList.size() != 0) {
                    for (int i = 0; i < dtoCommonLandingList.size(); i++) {
                        if (dtoCommonLandingList.get(i).getType().equalsIgnoreCase("FFF_CONTEXT")) {
                            dtoCommonLandingList.remove(dtoCommonLandingList.get(i));
                            String id = "f3c" + lastFFFContestId;
                            myDeskData.remove(id);
                        }
                    }
                }
                if (dtoCommonLandingList != null) {
                    dtoCommonLandingList.add(commonLandingData);
                }
                myDeskData.put(commonLandingData.getId(), commonLandingData);
                liveData.postValue(myDeskData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeFFCDataListener() {
        try {
            if (ffcDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcDataRefClass.getFirebasePath()).removeEventListener(ffcDataRefClass.getEventListener());
            }
            if (ffcQDataRefClass != null) {
                OustFirebaseTools.getRootRef().child(ffcQDataRefClass.getFirebasePath()).removeEventListener(ffcQDataRefClass.getEventListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchFFCData(String ffcId) {
        try {
            final String message = "/f3cData/f3c" + ffcId;
            Log.e("TAG", "fetctFFCData: " + message);
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    extractFFCData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
            getFFCEnrolldedUsersCount(ffcId);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractFFCData(DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot != null) {
                final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                if (null != ffcDataMap) {
                    try {
                        fastestFingerContestData = OustSdkTools.getFastestFingerContestData(fastestFingerContestData, ffcDataMap);
                        //setF3cBannerSize();
                        long bannerHideTimeNo = 1;
                        if (ffcDataMap.get("bannerHideTime") != null) {
                            bannerHideTimeNo = (long) ffcDataMap.get("bannerHideTime");
                        }
                        long bannerHideTime = (bannerHideTimeNo * (86400000));
                        //false
                        OustStaticVariableHandling.getInstance().setContestLive((System.currentTimeMillis() - fastestFingerContestData.getStartTime()) <= bannerHideTime);
                        setContestNotificationData(ffcDataMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setContestNotificationData(Map<String, Object> ffcDataMap) {
        try {
            if (OustStaticVariableHandling.getInstance().isContestLive()) {
                String contestnotification_message = OustPreferences.get("contestnotification_message");
                Gson gson = new Gson();
                ContestNotificationMessage contestNotificationMessage;
                if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                    contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                    if (contestNotificationMessage.getContestID() != fastestFingerContestData.getFfcId()) {
                        contestNotificationMessage = new ContestNotificationMessage();
                    }
                } else {
                    contestNotificationMessage = new ContestNotificationMessage();
                }
                contestNotificationMessage.setContestID((fastestFingerContestData.getFfcId()));
                contestNotificationMessage.setStartTime(fastestFingerContestData.getStartTime());
                contestNotificationMessage.setContestName(fastestFingerContestData.getName());
                contestNotificationMessage.setStudentId(activeUser.getStudentid());
                contestNotificationMessage.setAvatar(activeUser.getAvatar());
                contestNotificationMessage.setDisplayName(activeUser.getUserDisplayName());
                contestNotificationMessage.setJoinBanner(fastestFingerContestData.getJoinBanner());
                contestNotificationMessage.setPlayBanner(fastestFingerContestData.getPlayBanner());
                contestNotificationMessage.setRrBanner(fastestFingerContestData.getRrBanner());
                contestNotificationMessage.setRegistered(fastestFingerContestData.isEnrolled());
                if (ffcDataMap.get("greaterThan24") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("greaterThan24");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setGreater24Time(86400 / frequency);
                                contestNotificationMessage.setGreater24Message((String) subMap.get("message"));
                            }
                        }
                    }
                }
                if (ffcDataMap.get("lessThan24") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThan24");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setGreatehourTime(86400 / frequency);
                                contestNotificationMessage.setGreatehourMessage((String) subMap.get("message"));
                            }
                        }
                    }
                }
                long lastMinute = 0;
                if (ffcDataMap.get("lastMinute") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lastMinute");
                    if (subMap != null) {
                        if (subMap.get("message") != null) {
                            contestNotificationMessage.setLastMinuteMessage((String) subMap.get("message"));
                        }
                        if (subMap.get("minutes") != null) {
                            contestNotificationMessage.setLastMinuteTime(((long) subMap.get("minutes") * 60));
                            lastMinute = ((long) subMap.get("minutes") * 60);
                        }
                    }
                }
                if (ffcDataMap.get("lessThanHour") != null) {
                    Map<String, Object> subMap = (Map<String, Object>) ffcDataMap.get("lessThanHour");
                    if (subMap != null) {
                        if (subMap.get("frequency") != null) {
                            long frequency = (long) subMap.get("frequency");
                            if ((subMap.get("message") != null) && (frequency > 0)) {
                                contestNotificationMessage.setLesshourTime((3600 - lastMinute) / frequency);
                                contestNotificationMessage.setLesshourMessage((String) subMap.get("message"));
                            }
                        }
                    }
                }
                if (ffcDataMap.get("LBReadyMessage") != null) {
                    contestNotificationMessage.setLBReadyMessage((String) ffcDataMap.get("LBReadyMessage"));
                }
                if (fastestFingerContestData.getqIds() != null) {
                    long totalContestTime = ((fastestFingerContestData.getQuestionTime() * fastestFingerContestData.getqIds().size()) +
                            (fastestFingerContestData.getRestTime() * (fastestFingerContestData.getqIds().size() - 1)));
                    contestNotificationMessage.setLeaderboardNotificationTime(((totalContestTime + fastestFingerContestData.getConstructingLBTime()) / 1000));
                    contestNotificationMessage.setTotalContestTime((totalContestTime / 1000));
                }

                if (ffcDataMap.get("contestStartMessage") != null) {
                    contestNotificationMessage.setContestStartMessage((String) ffcDataMap.get("contestStartMessage"));
                }
                String contestnotification_message1 = gson.toJson(contestNotificationMessage);
                OustPreferences.save("contestnotification_message", contestnotification_message1);
                ffcBannerStatus();
            } else {
                OustPreferences.clear("contestnotification_message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getFFCEnrolldedUsersCount(String contestId) {
        try {
            final String path = "/f3cEnrolledUserCount/f3c" + contestId + "/participants";
            DatabaseReference databaseReference = OustFirebaseTools.getRootRef().child(path);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        fastestFingerContestData.setEnrolledCount((long) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error:");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchQData(String ffcId) {
        try {
            final String message = "/f3cQData/f3c" + ffcId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            final Map<String, Object> ffcDataMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (null != ffcDataMap) {
                                List<BasicQuestionClass> basicQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("questions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("questions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicQuestionClassList, questionSorter);
                                }
                                List<BasicQuestionClass> basicWarmUpQuestionClassList = new ArrayList<>();
                                if (ffcDataMap.get("warmupQuestions") != null) {
                                    Map<String, Object> questionMap = (Map<String, Object>) ffcDataMap.get("warmupQuestions");
                                    if (questionMap != null) {
                                        for (String key : questionMap.keySet()) {
                                            Map<String, Object> questionSubMap = (Map<String, Object>) questionMap.get(key);
                                            if (questionSubMap != null) {
                                                BasicQuestionClass basicQuestionClass = new BasicQuestionClass();
                                                if (questionSubMap.get("qId") != null) {
                                                    basicQuestionClass.setqId((long) questionSubMap.get("qId"));
                                                }
                                                if (questionSubMap.get("sequence") != null) {
                                                    basicQuestionClass.setSequence((long) questionSubMap.get("sequence"));
                                                }
                                                basicWarmUpQuestionClassList.add(basicQuestionClass);
                                            }
                                        }
                                    }
                                    Collections.sort(basicWarmUpQuestionClassList, questionSorter);
                                }
                                long updateChecksum = 0;
                                if (ffcDataMap.get("updateChecksum") != null) {
                                    updateChecksum = (long) ffcDataMap.get("updateChecksum");
                                }
                                boolean update = true;
                                if ((updateChecksum > 0) && (OustPreferences.getTimeForNotification("updateChecksum") > 0) && (updateChecksum == OustPreferences.getTimeForNotification("updateChecksum"))) {
                                    update = false;
                                }
                                List<String> qList = new ArrayList<>();
                                for (int i = 0; i < basicQuestionClassList.size(); i++) {
                                    if (basicQuestionClassList.get(i).getqId() > 0) {
                                        if (update) {
                                            downloadQuestion(("" + basicQuestionClassList.get(i).getqId()), updateChecksum);
                                        }
                                        qList.add(("" + basicQuestionClassList.get(i).getqId()));
                                    }
                                }
                                fastestFingerContestData.setqIds(qList);
                                List<String> warmUpQList = new ArrayList<>();
                                for (int i = 0; i < basicWarmUpQuestionClassList.size(); i++) {
                                    if (basicWarmUpQuestionClassList.get(i).getqId() > 0) {
                                        if (update) {
                                            downloadQuestion(("" + basicWarmUpQuestionClassList.get(i).getqId()), updateChecksum);
                                        }
                                        warmUpQList.add(("" + basicWarmUpQuestionClassList.get(i).getqId()));
                                    }
                                }
                                fastestFingerContestData.setWarmupQList(warmUpQList);
                                if ((notificationContestId != null) && (!notificationContestId.isEmpty()) &&
                                        (notificationContestId.equalsIgnoreCase(("" + fastestFingerContestData.getFfcId())))) {
                                    notificationContestId = "";
                                    CommonLandingData dtoCPLSpecialFeed = new CommonLandingData();
                                    dtoCPLSpecialFeed.setFastestFingerContestData(fastestFingerContestData);
                                    dtoCommonLandingList.add(dtoCPLSpecialFeed);
                                    Intent intent = new Intent(OustSdkApplication.getContext(), FFcontestStartActivity.class);
                                    Gson gson = new Gson();
                                    intent.putExtra("fastestFingerContestData", gson.toJson(fastestFingerContestData));
                                    OustSdkApplication.getContext().startActivity(intent);
                                }
                                setContestNotificationData(ffcDataMap);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(ffcDataListener);
            ffcQDataRefClass = new FirebaseRefClass(ffcDataListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(ffcDataListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<BasicQuestionClass> questionSorter = (s1, s2) -> ((int) s1.getSequence()) - ((int) s2.getSequence());

    private void downloadQuestion(final String qId, final long updateChecksum) {
        try {
            final String message = "/questions/Q" + qId;
            ValueEventListener ffcDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> questionMap = (Map<String, Object>) dataSnapshot.getValue();
                        EncrypQuestions encrypQuestions = new EncrypQuestions();
                        if (questionMap != null) {
                            if (questionMap.get("image") != null) {
                                encrypQuestions.setImage((String) questionMap.get("image"));
                            }
                            if (questionMap.get("encryptedQuestions") != null) {
                                encrypQuestions.setEncryptedQuestions((String) questionMap.get("encryptedQuestions"));
                            }
                            DTOQuestions questions = OustSdkTools.decryptQuestion(encrypQuestions, null);
                            try {
                                if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                                    questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                                    questions.setQuestionType(QuestionType.LONG_ANSWER);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            OustSdkTools.databaseHandler.addToRealmQuestions(questions, true);
                            OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);

                            OustPreferences.saveTimeForNotification("updateChecksum", updateChecksum);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(ffcDataListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}