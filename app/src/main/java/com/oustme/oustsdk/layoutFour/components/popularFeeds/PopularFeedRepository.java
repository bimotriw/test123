package com.oustme.oustsdk.layoutFour.components.popularFeeds;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.activity.FFContest.FFcontestStartActivity;
import com.oustme.oustsdk.firebase.FFContest.BasicQuestionClass;
import com.oustme.oustsdk.firebase.FFContest.ContestNotificationMessage;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.service.CourseNotificationReceiver;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PopularFeedRepository {

    private static final String TAG = "PopularFeedRepository";
    private static PopularFeedRepository instance;
    private MutableLiveData<List<DTOSpecialFeed>> liveData;
    private FastestFingerContestData fastestFingerContestData;
    private ActiveUser activeUser;
    private FirebaseRefClass ffcDataRefClass;
    private FirebaseRefClass ffcQDataRefClass;
    private String notificationContestId;
    private List<DTOSpecialFeed> dtoSpecialFeedList;
    private boolean isShowMultipleCpl = false;

    public static PopularFeedRepository getInstance() {
        if (instance == null) {
            instance = new PopularFeedRepository();
        }
        return instance;
    }


    public MutableLiveData<List<DTOSpecialFeed>> getFeedList() {
        dtoSpecialFeedList = new ArrayList<>();
        isShowMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
        activeUser = OustAppState.getInstance().getActiveUser();
        if (activeUser == null) {
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            }
        }
        liveData = new MutableLiveData<>();
        fetchFeedList();
        return liveData;
    }

    private void fetchFeedList() {
        getUserFFContest();
        getCPLData();
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
                            // initBottomSheetBannerData();
                        } else {
                            // isContest = false;
                            OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                            //initBottomSheetBannerData();

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
                DTOSpecialFeed dtoCPLSpecialFeed = new DTOSpecialFeed();
                dtoCPLSpecialFeed.setFastestFingerContestData(fastestFingerContestData);
                dtoCPLSpecialFeed.setHeader(fastestFingerContestData.getName());
                if (fastestFingerContestData.getDescription() != null) {
                    dtoCPLSpecialFeed.setContent(fastestFingerContestData.getDescription());
                } else {
                    dtoCPLSpecialFeed.setContent("");
                }
                dtoCPLSpecialFeed.setType("FFF_CONTEXT");
                String contestnotification_message = OustPreferences.get("contestnotification_message");

                if ((fastestFingerContestData.isEnrolled())) {

                    if ((fastestFingerContestData.getPlayBanner() != null) && (!fastestFingerContestData.getPlayBanner().isEmpty())) {
                        dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getPlayBanner());
                        saveData(dtoCPLSpecialFeed);
                    } else {
                        if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                            dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getJoinBanner());
                            saveData(dtoCPLSpecialFeed);
                        }
                    }

                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getRrBanner());
                                    saveData(dtoCPLSpecialFeed);
                                }
                            }
                        }
                    }
                } else {
                    if ((fastestFingerContestData.getJoinBanner() != null) && (!fastestFingerContestData.getJoinBanner().isEmpty())) {
                        dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getJoinBanner());
                        saveData(dtoCPLSpecialFeed);
                    }
                    if ((contestnotification_message != null) && (!contestnotification_message.isEmpty())) {
                        Gson gson = new Gson();
                        ContestNotificationMessage contestNotificationMessage = gson.fromJson(contestnotification_message, ContestNotificationMessage.class);
                        if (contestNotificationMessage != null) {
                            long currentTime = System.currentTimeMillis();
                            if (((contestNotificationMessage.getTotalContestTime() * 1000) + fastestFingerContestData.getStartTime()) < currentTime) {
                                if ((fastestFingerContestData.getRrBanner() != null) && (!fastestFingerContestData.getRrBanner().isEmpty())) {
                                    dtoCPLSpecialFeed.setImageUrl(fastestFingerContestData.getRrBanner());
                                    saveData(dtoCPLSpecialFeed);
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

    private void saveData(DTOSpecialFeed dtoCPLSpecialFeed) {
        try {
            if (dtoCPLSpecialFeed != null) {
                if (dtoSpecialFeedList != null && dtoSpecialFeedList.size() != 0) {
                    for (int i = 0; i < dtoSpecialFeedList.size(); i++) {
                        if (dtoSpecialFeedList.get(i).getType() != null) {
                            if (dtoSpecialFeedList.get(i).getType().equalsIgnoreCase("FFF_CONTEXT")) {
                                dtoSpecialFeedList.remove(dtoSpecialFeedList.get(i));
                            }
                        }
                    }
                    dtoSpecialFeedList.add(0, dtoCPLSpecialFeed);
                } else {
                    dtoSpecialFeedList.add(dtoCPLSpecialFeed);
                }
                liveData.postValue(dtoSpecialFeedList);
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
                        if ((System.currentTimeMillis() - fastestFingerContestData.getStartTime()) > bannerHideTime) {
                        } else {
                            OustStaticVariableHandling.getInstance().setContestLive(true);
                        }
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
                                boolean update = (updateChecksum <= 0) || (OustPreferences.getTimeForNotification("updateChecksum") <= 0) || (updateChecksum != OustPreferences.getTimeForNotification("updateChecksum"));

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
                                    liveData.postValue(dtoSpecialFeedList);
                                    notificationContestId = "";
                                    DTOSpecialFeed dtoCPLSpecialFeed = new DTOSpecialFeed();
                                    dtoCPLSpecialFeed.setFastestFingerContestData(fastestFingerContestData);
                                    dtoSpecialFeedList.add(dtoCPLSpecialFeed);
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

    private void getCPLData() {
        try {
            ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            final String message;
            if (isShowMultipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL";
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl";
            }
            Log.d(TAG, "getCPLData: " + message);

            ValueEventListener newsfeedListListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<DTOSpecialFeed> dtoCPLSpecialFeed = new ArrayList<>();
                            String cplId = "";
                            try {
                                Object o1 = dataSnapshot.getValue();

                                if (o1.getClass().equals(ArrayList.class)) {
                                    List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                    for (int i = 0; i < learningList.size(); i++) {
                                        Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                        if (lpMap != null) {
                                            DTOSpecialFeed dtoSpecialFeed = new DTOSpecialFeed();
                                            dtoSpecialFeed.setId(i);
                                            dtoSpecialFeed.setFeed(false);

                                            if (lpMap.get("cplName") != null) {
                                                dtoSpecialFeed.setHeader((String) lpMap.get("cplName"));
                                            }
                                            if (lpMap.get("cplDescription") != null) {
                                                dtoSpecialFeed.setContent((String) lpMap.get("cplDescription"));
                                            }
                                            if (lpMap.get("addedOn") != null) {
                                                dtoSpecialFeed.setCplAddOn((String) lpMap.get("addedOn"));
                                            }
                                            if (lpMap.get("completionPercentage") != null) {
                                                dtoSpecialFeed.setCplCompletionPercentage(OustSdkTools.convertToLong(lpMap.get("completionPercentage:")));
                                            }
                                            if (lpMap.get("CplCompletedOn") != null) {
                                                dtoSpecialFeed.setCplCompletionOn(String.valueOf(lpMap.get("CplCompletedOn")));
                                            }
                                            dtoSpecialFeed.setCPL(true);

                                            dtoCPLSpecialFeed.add(dtoSpecialFeed);
                                        }
                                    }
                                } else {
                                    Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                    String cplkey = "";
                                    Map<String, Object> cplMainMap;
                                    if (isShowMultipleCpl) {
                                        for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                                            cplMainMap = (Map<String, Object>) entry.getValue();
                                            DTOSpecialFeed dtoSpecialFeed = new DTOSpecialFeed();
                                            if (cplMainMap != null) {
                                                cplkey = entry.getKey();

                                                if (cplMainMap.get("cplName") != null) {
                                                    dtoSpecialFeed.setHeader((String) cplMainMap.get("cplName"));
                                                }
                                                if (cplMainMap.get("cplDescription") != null) {
                                                    dtoSpecialFeed.setContent((String) cplMainMap.get("cplDescription"));
                                                }
                                                if (cplMainMap.get("addedOn") != null) {
                                                    dtoSpecialFeed.setCplAddOn((String) cplMainMap.get("addedOn"));
                                                }
                                                if (cplMainMap.get("completionPercentage") != null) {
                                                    dtoSpecialFeed.setCplCompletionPercentage(OustSdkTools.convertToLong(cplMainMap.get("completionPercentage")));
                                                }
                                                if (cplMainMap.get("CplCompletedOn") != null) {
                                                    dtoSpecialFeed.setCplCompletionOn(String.valueOf(cplMainMap.get("CplCompletedOn")));
                                                }
                                                if (cplMainMap.get("activeChildCPL") != null) {
                                                    dtoSpecialFeed.setActiveChildCPL(OustSdkTools.convertToStr(cplMainMap.get("activeChildCPL")));
                                                } else {
                                                    dtoSpecialFeed.setActiveChildCPL("");
                                                }

                                                cplId = cplkey.replace("cpl", "");
                                                dtoSpecialFeed.setCPL(true);
                                                dtoSpecialFeed.setId(Long.parseLong(cplId));
                                                long bannerHideTimeNo;
                                                if (cplMainMap.get("CplCompletedOn") != null) {
                                                    bannerHideTimeNo = Long.parseLong(String.valueOf(cplMainMap.get("CplCompletedOn")));
                                                    long bannerHideTime = (bannerHideTimeNo + 86400000);
                                                    if (System.currentTimeMillis() < bannerHideTime) {
                                                        dtoCPLSpecialFeed.add(dtoSpecialFeed);
                                                    }
                                                } else {
                                                    dtoCPLSpecialFeed.add(dtoSpecialFeed);
                                                }
                                            }
                                        }
                                        if (dtoCPLSpecialFeed.size() > 0) {
                                            Collections.sort(dtoCPLSpecialFeed, DTOSpecialFeed.sortByDate);
                                            getExtraCplInfo(dtoCPLSpecialFeed);
                                        }
                                    } else {
                                        DTOSpecialFeed dtoSpecialFeed = new DTOSpecialFeed();
                                        for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
                                            cplMainMap = (Map<String, Object>) entry.getValue();
                                            cplkey = entry.getKey();
                                            dtoSpecialFeed.setFeed(false);
                                            if (cplMainMap.get("cplName") != null) {
                                                dtoSpecialFeed.setHeader((String) cplMainMap.get("cplName"));
                                            }
                                            if (cplMainMap.get("cplDescription") != null) {
                                                dtoSpecialFeed.setContent((String) cplMainMap.get("cplDescription"));
                                            }
                                        }
                                        cplId = cplkey.replace("cpl", "");
                                        dtoSpecialFeed.setCPL(true);
                                        dtoSpecialFeed.setId(Long.parseLong(cplId));
                                        dtoCPLSpecialFeed.add(dtoSpecialFeed);
                                        getExtraCplInfo(dtoCPLSpecialFeed);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    } catch (
                            Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError DatabaseError) {
                    Log.d(TAG, "onCancelled: ");
                }
            };
            DatabaseReference newsfeedRef = OustFirebaseTools.getRootRef().child(message);
            Query query = newsfeedRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(newsfeedListListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(newsfeedListListener, message));
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void getExtraCplInfo(ArrayList<DTOSpecialFeed> dataList) {
        try {
            //remove cpl
            if (dtoSpecialFeedList != null && dtoSpecialFeedList.size() != 0) {
                Iterator<DTOSpecialFeed> dtoSpecialFeedIterator = dtoSpecialFeedList.iterator();
                while (dtoSpecialFeedIterator.hasNext()) {
                    DTOSpecialFeed dtoSpecialFeed = dtoSpecialFeedIterator.next();
                    if (dtoSpecialFeed.isCPL()) {
                        dtoSpecialFeedIterator.remove();
                    }
                }
            }
            //end
            int size = dataList.size();
            if (size >= 5) {
                size = 4;
            } else {
                if (size != 0) {
                    size = size - 1;
                }
            }
            for (int i = 0; i <= size; i++) {
                DTOSpecialFeed cplCollectionData = dataList.get(i);
                if (cplCollectionData == null || cplCollectionData.equals("")) {
                    Log.d(TAG, "cplid is empty: ");
                    return;
                }
                String cplInfoNode = ("cpl/cpl" + cplCollectionData.getId());
                Log.d(TAG, "getExtraCplInfo: " + cplInfoNode);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                Map<String, Object> cplInfoMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (cplInfoMap != null) {
                                    if (cplInfoMap.get("banner") != null) {
                                        cplCollectionData.setImageUrl(String.valueOf(cplInfoMap.get("banner")));
                                    } else {
                                        cplCollectionData.setImageUrl("https://di5jfel2ggs8k.cloudfront.net/cpl/mpower/cpl-thumbnail.png");
                                    }

                                    if (cplInfoMap.get("parentCPLId") != null) {
                                        cplCollectionData.setParentCPLId(OustSdkTools.convertToLong(cplInfoMap.get("parentCPLId")));
                                    }
                                }
                            }
                            cplCollectionData.setType("CPL_CONTEXT");

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        boolean addData = false;
                        if (dtoSpecialFeedList.size() > 0) {
                            for (int i = 0; i < dtoSpecialFeedList.size(); i++) {
                                if (dtoSpecialFeedList.get(i).getType() != null && !dtoSpecialFeedList.get(i).getType().isEmpty()) {
                                    if (dtoSpecialFeedList.get(i).getType().equalsIgnoreCase("CPL_CONTEXT")) {
                                        if (cplCollectionData.getId() == dtoSpecialFeedList.get(i).getId()) {
                                            addData = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!addData) {
                                saveDataIntoSplFeedList(cplCollectionData);
                            }
                        } else {
                            saveDataIntoSplFeedList(cplCollectionData);
                        }
                        liveData.postValue(dtoSpecialFeedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: ");
                    }
                };
                OustFirebaseTools.getRootRef().child(cplInfoNode).addValueEventListener(eventListener);
                OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, cplInfoNode));
                OustFirebaseTools.getRootRef().child(cplInfoNode).keepSynced(false);
//            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void saveDataIntoSplFeedList(DTOSpecialFeed cplCollectionData) {
        try {
            if (cplCollectionData.getParentCPLId() == 0) {
                dtoSpecialFeedList.add(cplCollectionData);
            } else if (cplCollectionData.getParentCPLId() > 0 && cplCollectionData.getActiveChildCPL() != null && cplCollectionData.getActiveChildCPL().isEmpty()) {
                dtoSpecialFeedList.add(cplCollectionData);
            } else if (cplCollectionData.getParentCPLId() > 0 && cplCollectionData.getActiveChildCPL() != null && !cplCollectionData.getActiveChildCPL().isEmpty() && cplCollectionData.getActiveChildCPL().equalsIgnoreCase("TRUE")) {
                dtoSpecialFeedList.add(cplCollectionData);
            } else if (cplCollectionData.getParentCPLId() > 0 && cplCollectionData.getActiveChildCPL() != null && !cplCollectionData.getActiveChildCPL().isEmpty() && cplCollectionData.getActiveChildCPL().equalsIgnoreCase("FALSE")) {
                dtoSpecialFeedList.remove(cplCollectionData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
