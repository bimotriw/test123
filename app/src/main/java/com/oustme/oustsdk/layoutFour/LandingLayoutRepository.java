package com.oustme.oustsdk.layoutFour;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.downloadmanger.newimpl.DownloadFilesIntentService;
import com.oustme.oustsdk.firebase.common.FilterCategory;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.data.LandingLayout;
import com.oustme.oustsdk.layoutFour.data.Navigation;
import com.oustme.oustsdk.layoutFour.data.ToolbarItem;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.response.ParentCplDistributionResponse;
import com.oustme.oustsdk.response.common.TabInfoData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LandingLayoutRepository {
    private static final String TAG = "LandingLayoutRepository";
    private static LandingLayoutRepository instance;
    private MutableLiveData<LandingLayout> liveData;
    private LandingLayout landingLayout;
    private ActiveUser activeUser;
    private DownloadFiles downloadFiles;
    private MutableLiveData<Long> inBoxCount;
    private static Context context;
    private MutableLiveData<ArrayList<NotificationResponse>> notificationResponseMutableLiveData;
    private MutableLiveData<ParentCplDistributionResponse> mmCplDistributionResponseMutableLiveData;


    private LandingLayoutRepository() {
    }

    public static LandingLayoutRepository getInstance(Context getContext) {
        context = getContext;
        if (instance == null)
            instance = new LandingLayoutRepository();
        return instance;
    }

    public MutableLiveData<LandingLayout> getLandingLayout() {
        liveData = new MutableLiveData<>();
        getUserAppConfiguration();
        fetchBundleData();
        fetchLandingLayout();
        return liveData;
    }

    private void fetchBundleData() {
        activeUser = OustAppState.getInstance().getActiveUser();
        if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        }
    }

    private void fetchLandingLayout() {
        String msg = "/system/appConfig/layoutInfo/LAYOUT_4";
        ValueEventListener bottomNavEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    landingLayout = new LandingLayout();
                    landingLayout = dataSnapshot.getValue(LandingLayout.class);
                    if (landingLayout != null && landingLayout.getToolbar() != null) {
                        if (landingLayout.getToolbar().getContentColor() != null && !landingLayout.getToolbar().getContentColor().isEmpty())
                            OustPreferences.save("toolbarColorCode", landingLayout.getToolbar().getContentColor());

                        if (landingLayout.getToolbar().getBgColor() != null && !landingLayout.getToolbar().getBgColor().isEmpty())
                            OustPreferences.save("toolbarBgColor", landingLayout.getToolbar().getBgColor());
                    }

                    //liveData.postValue(landingLayout);
                    downloadNavIcons(landingLayout);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //liveData.postValue(landingLayout);
            }
        };

        OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg).addValueEventListener(bottomNavEventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(bottomNavEventListener, msg));

    }

    private void downloadNavIcons(LandingLayout landingLayout) {
        Log.d(TAG, "downloadNavIcons: ");
        ArrayList<String> urlList = new ArrayList<>();
        ArrayList<String> fileNameList = new ArrayList<>();
        //files_downloaded = 0;
        if (landingLayout == null)
            return;
        if (landingLayout.getTabNavigation() != null)
            for (Navigation navModel : landingLayout.getTabNavigation()) {
                if (navModel.getNavName() == null || navModel.getNavName().isEmpty()) {
                    if (navModel.getNavType().equalsIgnoreCase("home")) {
                        navModel.setNavName(context.getString(R.string.home));
                    } else if (navModel.getNavType().equalsIgnoreCase("mytask")) {
                        navModel.setNavName(context.getString(R.string.learning));
                    } else if (navModel.getNavType().equalsIgnoreCase("catalogue")) {
                        navModel.setNavName(context.getString(R.string.catalogue));
                    } else if (navModel.getNavType().equalsIgnoreCase("noticeBoard")) {
                        navModel.setNavName(context.getString(R.string.noticeBoard));
                    } else if (navModel.getNavType().equalsIgnoreCase("profile")) {
                        navModel.setNavName(context.getString(R.string.profile_text));
                    }
                }
                if (navModel.getNavIcon() == null)
                    continue;
                if (!OustSdkTools.isImageAvailable(navModel.getNavIcon())) {
                    String[] dirs = navModel.getNavIcon().split("/");
                    fileNameList.add(dirs[dirs.length - 1]);
                    urlList.add(navModel.getNavIcon());
                }
            }

        if (landingLayout.getProfileNavigation() != null)
            for (Navigation navModel : landingLayout.getProfileNavigation()) {
                if (navModel.getNavName().isEmpty() || navModel.getNavName() == null) {
                    if (navModel.getNavType().equalsIgnoreCase("faq")) {
                        navModel.setNavName(context.getString(R.string.faq));
                    } else if (navModel.getNavType().equalsIgnoreCase("teamanalytics")) {
                        navModel.setNavName(context.getString(R.string.teamanalytics));
                    } else if (navModel.getNavType().equalsIgnoreCase("helpsupport")) {
                        navModel.setNavName(context.getString(R.string.help_support));
                    } else if (navModel.getNavType().equalsIgnoreCase("rateapp")) {
                        navModel.setNavName(context.getString(R.string.rate_the_appText));
                    } else if (navModel.getNavType().equalsIgnoreCase("settings")) {
                        navModel.setNavName(context.getString(R.string.settings));
                    } else if (navModel.getNavType().equalsIgnoreCase("reportproblem")) {
                        navModel.setNavName(context.getString(R.string.report_problem));
                    } else if (navModel.getNavType().equalsIgnoreCase("logout")) {
                        navModel.setNavName(context.getString(R.string.logout));
                    } else if (navModel.getNavType().equalsIgnoreCase("myanalytics")) {
                        navModel.setNavName(context.getString(R.string.myanalytics));
                    } else if (navModel.getNavType().equalsIgnoreCase("favourites")) {
                        navModel.setNavName(context.getString(R.string.favourites));
                    } else if (navModel.getNavType().equalsIgnoreCase("myassessment")) {
                        navModel.setNavName(context.getString(R.string.my_assessment));
                    } else if (navModel.getNavType().equalsIgnoreCase("contest")) {
                        navModel.setNavName(context.getString(R.string.contest));
                    } else if (navModel.getNavType().equalsIgnoreCase("analytics")) {
                        navModel.setNavName(context.getString(R.string.analytics));
                    } else if (navModel.getNavType().equalsIgnoreCase("learningdiary")) {
                        navModel.setNavName(context.getString(R.string.my_diary));
                    }
                }
                if (navModel.getNavIcon() == null)
                    continue;
                if (!OustSdkTools.isImageAvailable(navModel.getNavIcon())) {
                    String[] dirs = navModel.getNavIcon().split("/");
                    fileNameList.add(dirs[dirs.length - 1]);
                    urlList.add(navModel.getNavIcon());
                }
            }
        if ((landingLayout.getToolbar() != null) && landingLayout.getToolbar().getContent() != null)
            for (ToolbarItem toolbarItem : landingLayout.getToolbar().getContent()) {
                if (toolbarItem.getIcon() == null)
                    continue;
                if (!OustSdkTools.isImageAvailable(toolbarItem.getIcon())) {
                    String[] dirs = toolbarItem.getIcon().split("/");
                    fileNameList.add(dirs[dirs.length - 1]);
                    urlList.add(toolbarItem.getIcon());
                }
            }
        try {
            if (urlList.size() != 0) {
                String moreUrl = urlList.get(0);
                if (!OustSdkTools.isImageAvailable(moreUrl)) {
                    String[] split = moreUrl.split("/");
                    String fileName = split[split.length - 1];
                    moreUrl = moreUrl.replace(fileName, "more.svg");
                    fileNameList.add("more.svg");
                    urlList.add(moreUrl);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (urlList.size() != 0) {
            DownloadFilesIntentService.startFileDownload(OustSdkApplication.getContext(), urlList, fileNameList, false, false, new DownloadResultReceiver(new Handler()));
        } else {
            liveData.postValue(landingLayout);
        }
    }

    public MutableLiveData<ArrayList<NotificationResponse>> getNotificationContRepository() {
        notificationResponseMutableLiveData = new MutableLiveData<>();
        String message = "/landingPage/" + activeUser.getStudentKey() + "/pushNotifications";
        Log.e("TAG", "getNotificationsFromFireBase: " + message);
        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue() instanceof ArrayList) {
                            final ArrayList<Object> notificationListData = (ArrayList<Object>) snapshot.getValue();
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            for (Object module : notificationListData) {
                                final Object notificationData = module;
                                Gson gson = new Gson();
                                JsonElement notificationElement = gson.toJsonTree(notificationData);
                                NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);

                                if (notificationResponse != null) {
                                    notificationResponseList.add(notificationResponse);
                                }
                            }
                            notificationResponseMutableLiveData.setValue(notificationResponseList);
                        } else if (snapshot.getValue() instanceof HashMap) {
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            for (DataSnapshot module : snapshot.getChildren()) {
                                final Object notificationData = module.getValue();
                                Gson gson = new Gson();
                                JsonElement notificationElement = gson.toJsonTree(notificationData);
                                NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);

                                if (notificationResponse != null) {
                                    notificationResponseList.add(notificationResponse);
                                }
                            }
                            notificationResponseMutableLiveData.setValue(notificationResponseList);
                        }
                    } else {
                        ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                        notificationResponseMutableLiveData.setValue(notificationResponseList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).orderByChild("updateTime").addValueEventListener(notificationListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(notificationListener, message));
        return notificationResponseMutableLiveData;
    }

    public MutableLiveData<Long> getDirectMessageCountRepository() {
        inBoxCount = new MutableLiveData<>();
        String message = "/landingPage/" + activeUser.getStudentKey() + "/unReadCount";
        Log.e("TAG", "getInBoxCountRepository: " + message);
        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        inBoxCount.setValue((long) snapshot.getValue());
                        Log.e(TAG, "onDataChange:-inBoxCount-> " + snapshot.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(notificationListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(notificationListener, message));
        return inBoxCount;
    }

    private class DownloadResultReceiver extends ResultReceiver {
        DownloadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 1:
                    //Log.d(TAG, "onReceiveResult: success");
                    /*files_downloaded++;
                    if(files_downloaded>=urlList.size()){
                        Log.d(TAG, "onReceiveResult: sending the postdata");
                        liveData.postValue(landingLayout);
                    }*/
                    //liveData.postValue(landingLayout);
                    break;

                case 2:
                    //Log.d(TAG, "onReceiveResult: progress");
                    break;

                case 3:
                    //Log.d(TAG, "onReceiveResult: fail");
                    /*files_downloaded++;
                    if(files_downloaded>=urlList.size()){
                        Log.d(TAG, "onReceiveResult: sending the postdata");
                        liveData.postValue(landingLayout);
                    }*/
                    break;

                case 4:
                    Log.d(TAG, "onReceiveResult: all success");
                    liveData.postValue(landingLayout);
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    // TODO: 2020-07-08 optimise below code
    private void getUserAppConfiguration() {
        Log.e(TAG, "inside getUserAppConfiguration method");
        String message = "/system/appConfig";
        Log.d(TAG, "getUserAppConfiguration: " + message);
        ValueEventListener appConfigListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    extractAppConfig(snapshot);
                    //liveData.postValue(landingLayout);
                    Log.e(TAG, "about to reach setToolBarCOLOR method");
                } catch (Exception e) {
                    Log.e(TAG, "Error while saving app config data", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error + "");
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(appConfigListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(appConfigListener, message));
    }

    private void extractAppConfig(DataSnapshot snapshot) {
        if (null != snapshot.getValue()) {
            Map<String, Object> appConfigMap = (Map<String, Object>) snapshot.getValue();
            if (appConfigMap.get("isPlayModeEnabled") != null) {
                OustPreferences.saveAppInstallVariable("isPlayModeEnabled", (boolean) appConfigMap.get("isPlayModeEnabled"));
            } else {
                OustPreferences.saveAppInstallVariable("isPlayModeEnabled", false);
            }
            if (appConfigMap.get("logoutButtonEnabled") != null) {
                OustPreferences.saveAppInstallVariable("logoutButtonEnabled", (boolean) appConfigMap.get("logoutButtonEnabled"));
            } else {
                OustPreferences.saveAppInstallVariable("logoutButtonEnabled", false);
            }
            if (appConfigMap.get("showGoalSetting") != null) {
                OustPreferences.saveAppInstallVariable("showGoalSetting", (boolean) appConfigMap.get("showGoalSetting"));
            } else {
                OustPreferences.saveAppInstallVariable("showGoalSetting", false);
            }
            if (appConfigMap.get("rewardPageLink") != null) {
                OustPreferences.save("rewardpagelink", (String) appConfigMap.get("rewardPageLink"));
            } else {
                OustPreferences.clear("rewardpagelink");
            }
            if (appConfigMap.get("companydisplayName") != null) {
                OustPreferences.save("companydisplayName", (String) appConfigMap.get("companydisplayName"));
                Log.d(TAG, "appConfigMap: companydisplayName:" + appConfigMap.get("companydisplayName"));
            }
            if (appConfigMap.get("panelColor") != null) {
                OustPreferences.save("toolbarColorCode", (String) appConfigMap.get("panelColor"));
                Log.e(TAG, "got panel color from appConfigMap" + (String) appConfigMap.get("panelColor"));
            } else {
                OustPreferences.save("toolbarColorCode", "#ff01b5a2");
                Log.e(TAG, "THIS IS NOT map data color" + "toolbarColorCode #ff01b5a2");
            }
            if (appConfigMap.get("contestHistoryBanner") != null) {
                OustPreferences.save("contestHistoryBanner", (String) appConfigMap.get("contestHistoryBanner"));
            } else {
                OustPreferences.clear("contestHistoryBanner");
            }
            if (appConfigMap.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER) != null) {
                OustPreferences.save(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER, (String) appConfigMap.get(AppConstants.StringConstants.LEARNING_DIARY_DEFAULT_BANNER));
            }
            if (appConfigMap.get("secondaryColor") != null) {
                OustPreferences.save("secondaryColor", (String) appConfigMap.get("secondaryColor"));
                Log.e(TAG, "got secondary Color from appConfigMap" + (String) appConfigMap.get("secondaryColor"));
            } else {
                OustPreferences.save("secondaryColor", "#FE9738");
                Log.e(TAG, "THIS IS NOT map data color" + "secondaryColor #FE9738");
            }
            if (appConfigMap.get("treasuryColor") != null) {
                OustPreferences.save("treasuryColor", (String) appConfigMap.get("treasuryColor"));
                Log.e(TAG, "got treasury Color from appConfigMap" + (String) appConfigMap.get("treasuryColor"));
            } else {
                OustPreferences.save("treasuryColor", "#46C7FA");
                Log.e(TAG, "THIS IS NOT map data color" + "treasuryColor #46C7FA");
            }
            if (appConfigMap.get("panelLogo") != null) {
                OustPreferences.save("panelLogo", ((String) appConfigMap.get("panelLogo")));
                Log.e(TAG, "appConfigMap got panel logo : " + (String) appConfigMap.get("panelLogo"));
            }

            if (appConfigMap.get("hostAppIcon") != null) {
                OustPreferences.save("hostAppIcon", ((String) appConfigMap.get("hostAppIcon")));
                Log.e(TAG, "appConfigMap got hostAppIcon " + (String) appConfigMap.get("hostAppIcon"));
            } else {
                OustPreferences.clear("hostAppIcon");
            }

            if (appConfigMap.get("autoLogout") != null) {
                OustPreferences.saveAppInstallVariable("oustautoLogout", ((boolean) appConfigMap.get("autoLogout")));
            } else {
                OustPreferences.saveAppInstallVariable("oustautoLogout", false);
            }
            if (appConfigMap.get("autoLogoutTimeout") != null) {
                OustPreferences.saveTimeForNotification("oustautoLogoutTimeout", ((long) appConfigMap.get("autoLogoutTimeout")));
            } else {
                OustPreferences.saveTimeForNotification("oustautoLogoutTimeout", 0);
            }
            if (appConfigMap.get("restrictUserImageEdit") != null) {
                OustPreferences.saveAppInstallVariable("restrictUserImageEdit", ((boolean) appConfigMap.get("restrictUserImageEdit")));
            } else {
                OustPreferences.saveAppInstallVariable("restrictUserImageEdit", false);
            }

            if (appConfigMap.get("userAreaBgImg") != null) {
                OustPreferences.save("userAreaBgImg", (String) appConfigMap.get("userAreaBgImg"));
            } else {
                OustPreferences.clear("userAreaBgImg");
            }
            if (appConfigMap.get("redirectIcon") != null) {
                String redirectIcon = ((String) appConfigMap.get("redirectIcon"));
                OustPreferences.save("redirectIcon", redirectIcon);
                Log.e(TAG, "redirect icon " + redirectIcon);
            } else {
                OustPreferences.clear("redirectIcon");
            }
            if (appConfigMap.get("redirectAppPackage") != null) {
                OustPreferences.save("redirectAppPackage", ((String) appConfigMap.get("redirectAppPackage")));
            } else {
                OustPreferences.clear("redirectAppPackage");
            }
            if (appConfigMap.get("liveTraining") != null) {
                OustPreferences.saveAppInstallVariable("liveTraining", ((boolean) appConfigMap.get("liveTraining")));
            } else {
                OustPreferences.clear("liveTraining");
            }

            if (appConfigMap.get("resetPasswordParamater") != null) {
                OustPreferences.save("resetPasswordParamater", ((String) appConfigMap.get("resetPasswordParamater")));
                Log.e(TAG, "got resetPasswordParameter ||| :" + appConfigMap.get("resetPasswordParamater"));
            } else {
                OustPreferences.clear("resetPasswordParamater");
            }

            if (appConfigMap.get(AppConstants.StringConstants.THEME_SOUND) != null) {
                OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.THEME_SOUND, ((boolean) appConfigMap.get(AppConstants.StringConstants.THEME_SOUND)));
            } else {
                OustPreferences.clear(AppConstants.StringConstants.THEME_SOUND);
            }


            if (appConfigMap.get("features") != null) {
                Map<String, Object> featuresMap = (Map<String, Object>) appConfigMap.get("features");
                if (featuresMap != null) {
                    if (featuresMap.get("showNewNoticeboardUI") != null) {
                        OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", ((boolean) featuresMap.get("showNewNoticeboardUI")));
                    } else {
                        OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", false);
                    }
                    Log.d("showNewNoticeboardUI3", "" + OustPreferences.getAppInstallVariable("showNewNoticeboardUI"));

                    if (featuresMap.get("catalogueName") != null) {
                        String catalogueName = OustSdkTools.convertToStr(featuresMap.get("catalogueName"));
                        if (catalogueName != null && !catalogueName.equals("catalogueName")) {
                            OustPreferences.save("catalogueName", catalogueName);
                        }
                    } else {
                        OustPreferences.clear("catalogueName");
                    }

                    if (featuresMap.get("disableUser") != null) {
                        Log.d(TAG, "Yes has disableUser");
                        OustPreferences.saveAppInstallVariable("disableUser", ((boolean) featuresMap.get("disableUser")));
                    } else {
                        Log.d(TAG, "No disableUser");
                        OustPreferences.clear("disableUser");
                    }

                    if (featuresMap.get(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION) != null) {
                        Log.d(TAG, "Yes has disableUserOnCplCompletion" + featuresMap.get(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION));
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION)));
                    } else {
                        Log.d(TAG, "No disableUserOnCplCompletion");
                        OustPreferences.clear(AppConstants.StringConstants.DISABLE_USER_ON_CPL_COMPLETION);
                    }

                    if (featuresMap.get("showCorn") != null) {
                        Log.d(TAG, "Yes has showCorn");
                        OustPreferences.saveAppInstallVariable("showCorn", ((boolean) featuresMap.get("showCorn")));
                    } else {
                        Log.d(TAG, "No showCorn");
                        OustPreferences.clear("showCorn");
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.DISABLE_PROFILE_EDIT) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_PROFILE_EDIT)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_PROFILE_EDIT, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, false);
                    }

                    if (featuresMap.get("sendPushNotifications") != null) {
                        Log.d(TAG, "Yes has sendPushNotifications");
                        OustPreferences.saveAppInstallVariable("sendPushNotifications", ((boolean) featuresMap.get("sendPushNotifications")));
                    } else {
                        Log.d(TAG, "No sendPushNotifications");
                        OustPreferences.saveAppInstallVariable("sendPushNotifications", true);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.FEED_COMMENT_DISABLE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_COMMENT_DISABLE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.FEED_LIKE_DISABLE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_LIKE_DISABLE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_COMMENT) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_COMMENT)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_COMMENT, true);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_LIKE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_LIKE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_LIKE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_LIKE, true);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_SHARE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_SHARE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_SHARE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_SHARE, true);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_CREATE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_CREATE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NB_POST_CREATE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NB_POST_CREATE, true);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_ZEPTO_CPL_COMPLETION_POPUP) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_ZEPTO_CPL_COMPLETION_POPUP, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_ZEPTO_CPL_COMPLETION_POPUP)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_ZEPTO_CPL_COMPLETION_POPUP, false);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.ZEPTO_REDIRECTION_URL) != null) {
                        OustPreferences.save(AppConstants.StringConstants.ZEPTO_REDIRECTION_URL, ((String) featuresMap.get(AppConstants.StringConstants.ZEPTO_REDIRECTION_URL)));
                    } else {
                        OustPreferences.save(AppConstants.StringConstants.ZEPTO_REDIRECTION_URL, "");
                    }
                    if (featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, false);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, false);
                    }
                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, true);
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI, false);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE, (boolean) featuresMap.get(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.NEW_ASSESSMENT_UI) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI, (boolean) featuresMap.get(AppConstants.StringConstants.NEW_ASSESSMENT_UI));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_CARD_NEW_UI) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CARD_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CARD_NEW_UI));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CARD_NEW_UI, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CATALOGUE_NEW_UI, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE) != null) {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, ((long) featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE)));
                    } else {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, 0);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_MULTIPLE_CPL) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_MULTIPLE_CPL)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE, ((boolean) featuresMap.get(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.READ_MULTILINGUAL_CPL_FROM_FIREBASE, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_MULTILINGUAL_LANGUAGE_SWITCH) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTILINGUAL_LANGUAGE_SWITCH, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_MULTILINGUAL_LANGUAGE_SWITCH)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_MULTILINGUAL_LANGUAGE_SWITCH, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION) != null) {
                        Log.d(TAG, "onDataChange: FEED_AUTO_CLOSE_AFTER_COMPLETION -> " + featuresMap.get(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION));
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE, ((boolean) featuresMap.get(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FREEZE_WORK_DIARY_START_DATE, true);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY) != null) {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY, ((long) featuresMap.get(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY)));
                    } else {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.LAST_DATE_OF_WORK_DIARY, 0);
                    }

                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME, false);


                    if (featuresMap.get(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED) != null) {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED, ((long) featuresMap.get(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED)));
                    } else {
                        OustPreferences.saveTimeForNotification(AppConstants.StringConstants.NO_OF_BACK_DAYS_ALLOWED, 0);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.HOST_APP_LINK_DISABLED) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.HOST_APP_LINK_DISABLED, ((boolean) featuresMap.get(AppConstants.StringConstants.HOST_APP_LINK_DISABLED)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.HOST_APP_LINK_DISABLED, true);
                    }

                    //isPreviousDateAllowed
                    if (featuresMap.get(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED) != null) {
                        Log.d(TAG, "Yes has AppConstants.StringConstants.IS_PREV_DATE_ALLOWED");
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_PREV_DATE_ALLOWED, false);
                    }


                    if (featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED) != null) {
                        Log.d(TAG, "Yes has AppConstants.StringConstants.IS_PREV_DATE_ALLOWED");
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.ENABLE_RD_WD) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.ENABLE_RD_WD, ((boolean) featuresMap.get(AppConstants.StringConstants.ENABLE_RD_WD)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.ENABLE_RD_WD, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.OPEN_WELCOME_POPUP) != null) {
                        Log.d(TAG, "Yes has AppConstants.StringConstants.OPEN_WELCOME_POPUP:");
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.OPEN_WELCOME_POPUP, ((boolean) featuresMap.get(AppConstants.StringConstants.OPEN_WELCOME_POPUP)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.OPEN_WELCOME_POPUP, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN_ALWAYS_ON_LOGIN, false);
                    }

                    if (featuresMap.get("disableCourse") != null) {
                        OustPreferences.saveAppInstallVariable("hideCourse", ((boolean) featuresMap.get("disableCourse")));
                    } else {
                        OustPreferences.clear("hideCourse");
                    }

                    if (featuresMap.get("enableGalleryUpload") != null) {
                        OustPreferences.saveAppInstallVariable("enableGalleryUpload", ((boolean) featuresMap.get("enableGalleryUpload")));
                    } else {
                        OustPreferences.clear("enableGalleryUpload");
                    }


                    if (featuresMap.get("disableCatalogue") != null) {
                        OustPreferences.saveAppInstallVariable("hideCatalog", ((boolean) featuresMap.get("disableCatalogue")));
                    } else {
                        OustPreferences.clear("hideCatalog");
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.SHOW_CPL_LANGUAGE_IN_NAVIGATION);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_FAQ) != null) {
                        OustPreferences.save(AppConstants.StringConstants.SHOW_FAQ, String.valueOf((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FAQ)));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.SHOW_FAQ);
                    }
                    if (featuresMap.get(AppConstants.StringConstants.URL_FAQ) != null) {
                        OustPreferences.save(AppConstants.StringConstants.URL_FAQ, (String) featuresMap.get(AppConstants.StringConstants.URL_FAQ));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.URL_FAQ);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.TEAM_ANALYTICS) != null) {
                        OustPreferences.save(AppConstants.StringConstants.TEAM_ANALYTICS, String.valueOf(((Boolean) featuresMap.get(AppConstants.StringConstants.TEAM_ANALYTICS))));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.TEAM_ANALYTICS);
                    }


                    if (featuresMap.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS) != null) {
                        OustPreferences.save(AppConstants.StringConstants.URL_TEAM_ANALYTICS, (String) featuresMap.get(AppConstants.StringConstants.URL_TEAM_ANALYTICS));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.URL_TEAM_ANALYTICS);
                    }

                    if (featuresMap.get("disableAssessment") != null) {
                        OustPreferences.saveAppInstallVariable("hideAssessment", ((boolean) featuresMap.get("disableAssessment")));
                    } else {
                        OustPreferences.clear("hideAssessment");
                    }

                    if (featuresMap.get("disableCplLogout") != null) {
                        OustPreferences.saveAppInstallVariable("disableCplLogout", ((boolean) featuresMap.get("disableCplLogout")));
                    } else {
                        OustPreferences.clear("disableCplLogout");
                    }

                    if (featuresMap.get("disableCplIntroIcon") != null) {
                        OustPreferences.saveAppInstallVariable("disableCplIntroIcon", ((boolean) featuresMap.get("disableCplIntroIcon")));
                    } else {
                        OustPreferences.clear("disableCplIntroIcon");
                    }

                    if (featuresMap.get("disableCplTitle") != null) {
                        OustPreferences.saveAppInstallVariable("disableCplTitle", ((boolean) featuresMap.get("disableCplTitle")));
                    } else {
                        OustPreferences.clear("disableCplTitle");
                    }

                    if (featuresMap.get("learningDiaryName") != null) {
                        OustPreferences.save("learningDiaryName", OustSdkTools.convertToStr(featuresMap.get("learningDiaryName")));
                    } else {
                        OustPreferences.clear("learningDiaryName");
                    }

                    if (featuresMap.get(AppConstants.StringConstants.HOST_APP_NAME) != null) {
                        OustPreferences.save(AppConstants.StringConstants.HOST_APP_NAME, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.HOST_APP_NAME)));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.HOST_APP_NAME);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.FEED_BACK_NAME) != null) {
                        OustPreferences.save(AppConstants.StringConstants.FEED_BACK_NAME, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.FEED_BACK_NAME)));
                    } else {
                        OustPreferences.save(AppConstants.StringConstants.FEED_BACK_NAME, "HELP/SUPPORT");
                    }

                    if (featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE) != null) {
                        OustPreferences.save(AppConstants.StringConstants.CPL_LANG_CHANGE, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE)));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.CPL_LANG_CHANGE);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING) != null) {
                        try {
                            if (featuresMap.get((AppConstants.StringConstants.CUSTOM_COURSE_BRANDING)) != null &&
                                    featuresMap.get((AppConstants.StringConstants.CUSTOM_COURSE_BRANDING)).getClass().equals(HashMap.class)) {
                                Map<String, Object> customCourseBranding = (Map<String, Object>) featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                                if (customCourseBranding != null) {
                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_START_ICON) != null) {
                                        String CourseStartIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_START_ICON);
                                        String fileName = OustMediaTools.getMediaFileName(CourseStartIconURL);
                                        OustPreferences.save("DEFAULT_START_ICON", fileName);
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_START_ICON, CourseStartIconURL);
                                    } else {
                                        OustPreferences.clear("DEFAULT_START_ICON");
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_START_ICON);

                                    }
                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON) != null) {
                                        String courseFinishIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON);
                                        String fileName = OustMediaTools.getMediaFileName(courseFinishIconURL);
                                        OustPreferences.save("DEFAULT_FINISH_ICON", fileName);
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_FINISH_ICON, courseFinishIconURL);
                                    } else {
                                        OustPreferences.clear("DEFAULT_FINISH_ICON");
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_FINISH_ICON);
                                    }
                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON) != null) {
                                        String courseLevelIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                        String fileName = OustMediaTools.getMediaFileName(courseLevelIconURL);
                                        OustPreferences.save("DEFAULT_INDICATOR_ICON", fileName);
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON, courseLevelIconURL);
                                    } else {
                                        OustPreferences.clear("DEFAULT_INDICATOR_ICON");
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                    }
                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR) != null) {
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_LOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR));
                                    } else {
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_LOCK_COLOR);
                                    }

                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH) != null) {
                                        String urlPath = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
                                        String fileName = OustMediaTools.getMediaFileName(urlPath);
                                        OustPreferences.save("DEFAULT_COURSE_AUDIO", fileName);
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH, urlPath);
                                        downloadImages(urlPath, fileName);

                                    } else {
                                        OustPreferences.clear("DEFAULT_COURSE_AUDIO");
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
                                    }

                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH) != null) {
                                        String urlPath = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH);
                                        String fileName = OustMediaTools.getMediaFileName(urlPath);
                                        OustPreferences.save("DEFAULT_LEVEL_AUDIO", fileName);
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH, urlPath);
                                        downloadImages(urlPath, fileName);

                                    } else {
                                        OustPreferences.clear("DEFAULT_LEVEL_AUDIO");
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH);
                                    }

                                    if (customCourseBranding.get(AppConstants.StringConstants.CURRENT_LEVEL_COLOR) != null) {
                                        OustPreferences.save(AppConstants.StringConstants.CURRENT_LEVEL_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.CURRENT_LEVEL_COLOR));
                                    } else {
                                        OustPreferences.clear(AppConstants.StringConstants.CURRENT_LEVEL_COLOR);

                                    }
                                    if (customCourseBranding.get(AppConstants.StringConstants.COURSE_UNLOCK_COLOR) != null) {
                                        OustPreferences.save(AppConstants.StringConstants.COURSE_UNLOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_UNLOCK_COLOR));
                                    } else {
                                        OustPreferences.clear(AppConstants.StringConstants.COURSE_UNLOCK_COLOR);
                                    }
                                    if (customCourseBranding.get(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR) != null) {
                                        OustPreferences.save(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR));
                                    } else {
                                        OustPreferences.clear(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR);
                                    }

                                    if (customCourseBranding.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR) != null) {
                                        OustPreferences.save(AppConstants.StringConstants.RM_USER_STATUS_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.RM_USER_STATUS_COLOR));
                                    } else {
                                        OustPreferences.clear(AppConstants.StringConstants.RM_USER_STATUS_COLOR);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            clearBrandingGuidLines();
                        }

                    } else {
                        clearBrandingGuidLines();
                        OustPreferences.clear(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                    }

                    //disableCourseReviewMode
                    if (featuresMap.get("disableCourseReviewMode") != null) {
                        OustPreferences.saveAppInstallVariable("disableCourseReviewMode", ((boolean) featuresMap.get("disableCourseReviewMode")));
                    } else {
                        OustPreferences.clear("disableCourseReviewMode");
                    }
                    if (featuresMap.get("disableFavCard") != null) {
                        OustPreferences.saveAppInstallVariable("disableFavCard", ((boolean) featuresMap.get("disableFavCard")));
                    } else {
                        OustPreferences.clear("disableFavCard");
                    }
                    if (featuresMap.get("disableArchive") != null) {
                        OustPreferences.saveAppInstallVariable("hideArchive", ((boolean) featuresMap.get("disableArchive")));
                    } else {
                        OustPreferences.clear("hideArchive");
                    }

                    if (featuresMap.get(AppConstants.StringConstants.DISABLE_LEARNING_DIARY) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_LEARNING_DIARY)));
                        Log.d(TAG, "onDataChange: DISABLE_LEARNING_DIARY:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY, true);
                    }

                    if (featuresMap.get("disableCplClose") != null) {
                        OustPreferences.saveAppInstallVariable("hideCplCloseBtn", ((boolean) featuresMap.get("disableCplClose")));
                    } else {
                        OustPreferences.clear("hideCplCloseBtn");
                    }

                    if (featuresMap.get("disableUserSeeting") != null) {
                        OustPreferences.saveAppInstallVariable("hideUserSetting", ((boolean) featuresMap.get("disableUserSeeting")));
                    } else {
                        OustPreferences.clear("hideUserSetting");
                    }
                    if (featuresMap.get("disableCourseLBShare") != null) {
                        OustPreferences.saveAppInstallVariable("restrictCourseLeaderboardShare", ((boolean) featuresMap.get("disableCourseLBShare")));
                    } else {
                        OustPreferences.clear("restrictCourseLeaderboardShare");
                    }
                    if (featuresMap.get("disableOrgLeaderboard") != null) {
                        OustPreferences.saveAppInstallVariable("disableOrgLeaderboard", ((boolean) featuresMap.get("disableOrgLeaderboard")));
                    } else {
                        OustPreferences.saveAppInstallVariable("disableOrgLeaderboard", false);
                    }

                    if (featuresMap.get("disableFabMenu") != null) {
                        OustPreferences.saveAppInstallVariable("disableFabMenu", ((boolean) featuresMap.get("disableFabMenu")));
                    } else {
                        OustPreferences.saveAppInstallVariable("disableFabMenu", false);
                    }
                    if (featuresMap.get("disableToolbarLB") != null) {
                        OustPreferences.saveAppInstallVariable("disableToolbarLB", ((boolean) featuresMap.get("disableToolbarLB")));
                    } else {
                        OustPreferences.saveAppInstallVariable("disableToolbarLB", true);
                    }

                    if (featuresMap.get("disableFavorite") != null) {
                        OustPreferences.saveAppInstallVariable("disableFavorite", ((boolean) featuresMap.get("disableFavorite")));
                    } else {
                        OustPreferences.saveAppInstallVariable("disableFavorite", false);
                    }

                    if (featuresMap.get("hideAllCourseLeaderBoard") != null) {
                        OustPreferences.saveAppInstallVariable("hideAllCourseLeaderBoard", ((boolean) featuresMap.get("hideAllCourseLeaderBoard")));
                    } else {
                        OustPreferences.saveAppInstallVariable("hideAllCourseLeaderBoard", false);
                    }

                    if (featuresMap.get("enableVideoDownload") != null) {
                        OustPreferences.saveAppInstallVariable("enableVideoDownload", ((boolean) featuresMap.get("enableVideoDownload")));
                    } else {
                        OustPreferences.saveAppInstallVariable("enableVideoDownload", false);
                    }

                    if (featuresMap.get("hideAllAssessmentLeaderBoard") != null) {
                        OustPreferences.saveAppInstallVariable("hideAllAssessmentLeaderBoard", ((boolean) featuresMap.get("hideAllAssessmentLeaderBoard")));
                    } else {
                        OustPreferences.saveAppInstallVariable("hideAllAssessmentLeaderBoard", false);
                    }

                    if (featuresMap.get("hideCourseBulletin") != null) {
                        OustPreferences.saveAppInstallVariable("hideCourseBulletin", ((boolean) featuresMap.get("hideCourseBulletin")));
                    } else {
                        OustPreferences.saveAppInstallVariable("hideCourseBulletin", false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.DISABLE_TODO_ONCLICK) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_TODO_ONCLICK, ((boolean) featuresMap.get(AppConstants.StringConstants.DISABLE_TODO_ONCLICK)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_TODO_ONCLICK, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_PAYOUT_AT_CITY_SELECTION, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.LB_RESET_PERIOD) != null) {
                        OustPreferences.save(AppConstants.StringConstants.LB_RESET_PERIOD, (String) featuresMap.get(AppConstants.StringConstants.LB_RESET_PERIOD));
                    } else {
                        OustPreferences.clear(AppConstants.StringConstants.LB_RESET_PERIOD);
                    }

                    boolean isCityScreenEnabled = false;
                   /* if (featuresMap.get("showCityListScreenForCPL") != null) {
                        isCityScreenEnabled = (boolean) featuresMap.get("showCityListScreenForCPL");
                        Log.d(TAG, "showCityListScreenForCPL: " + isCityScreenEnabled);
                        OustPreferences.saveAppInstallVariable("showCityListScreenForCPL", isCityScreenEnabled);

                    } else {
                        OustPreferences.saveAppInstallVariable("showCityListScreenForCPL", false);
                    }*/

                    boolean isLanguageScreenEnabled = false;
                    if (!isCityScreenEnabled) {
                        if (featuresMap.get("showLanguageScreen") != null) {
                            isLanguageScreenEnabled = (boolean) featuresMap.get("showLanguageScreen");
                            if ((boolean) featuresMap.get("showLanguageScreen") && !OustPreferences.getAppInstallVariable(IS_LANG_SELECTED)) {
                                if (featuresMap.get("cplIdForLanguage") != null) {
                                    OustPreferences.saveLongVar("cplIdForLanguage", (featuresMap.get("cplIdForLanguage") != null) ? (long) featuresMap.get("cplIdForLanguage") : 0);
                                } else {
                                    OustPreferences.saveLongVar("cplIdForLanguage", 0);
                                }
                            }
                        }
                    } else {
                        isLanguageScreenEnabled = true;
                    }

                    OustPreferences.saveAppInstallVariable("showLanguageScreen", isLanguageScreenEnabled);

                    // feature to test showing FFC on Landing Page
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                    }

                    // feature to test showing CPL on Landing Page
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, false);
                    }

                    // feature to test showing To do on Landing Page
                    if (featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE) != null) {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE)));
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, false);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_USER_OVERALL_CREDITS) != null) {
                        boolean isShowOverallCredits = (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_USER_OVERALL_CREDITS);
                    }

                    if (featuresMap.get(AppConstants.StringConstants.SHOW_LB_USER_LOCATION) != null) {
                        boolean isShow = (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_LB_USER_LOCATION);
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION, isShow);
                    } else {
                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION, false);
                    }
                    Log.d(TAG, "onDataChange: USER location:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_LB_USER_LOCATION));
                }
            }


            if (appConfigMap.get("notificationData") != null) {
                Map<String, Object> notificationMap = (Map<String, Object>) appConfigMap.get("notificationData");
                if (notificationMap != null) {
                    if (notificationMap.get("content") != null) {
                        OustPreferences.save("notificationContent", ((String) notificationMap.get("content")));
                    } else {
                        OustPreferences.clear("notificationContent");
                    }
                    if (notificationMap.get("title") != null) {
                        OustPreferences.save("notificationTitle", ((String) notificationMap.get("title")));
                    } else {
                        OustPreferences.clear("notificationTitle");
                    }
                    if (notificationMap.get("interval") != null) {
                        OustPreferences.saveTimeForNotification("notificationInterval", ((long) notificationMap.get("interval")));
                    } else {
                        OustPreferences.clear("notificationInterval");
                    }
                }
            }
            if (appConfigMap.get("layoutInfo") != null) {
                Map<Object, Object> layoutInfoData = (Map<Object, Object>) appConfigMap.get("layoutInfo");
                if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 2) {
                    if (layoutInfoData.get("LAYOUT_2") != null) {
                        Map<Object, Object> newLandingPageData = (Map<Object, Object>) layoutInfoData.get("LAYOUT_2");
                        if (newLandingPageData.get("moduleSection") != null) {
                            ArrayList<Object> moduleArray = (ArrayList<Object>) newLandingPageData.get("moduleSection");
                            if (moduleArray != null && moduleArray.size() > 0) {
                                for (int i = 0; i < moduleArray.size(); i++) {
                                    Map<Object, Object> moduleMap = (Map<Object, Object>) moduleArray.get(i);
                                    if (moduleMap != null) {
                                        if (moduleMap.get("label") != null) ;
//                                            moduleDataArrayList.add((String) moduleMap.get("label"));
                                    }
                                }
                            }
                        }
                        if (newLandingPageData.get("tabSection") != null) {
                            ArrayList<Object> tabSectionMap = (ArrayList<Object>) newLandingPageData.get("tabSection");
                            if (tabSectionMap != null && tabSectionMap.size() > 0) {
                                for (int i = 0; i < tabSectionMap.size(); i++) {
                                    TabInfoData tabInfoData = new TabInfoData();
                                    Map<Object, Object> tabInfoMap = (Map<Object, Object>) tabSectionMap.get(i);
                                    if (tabInfoMap.get("label") != null)
                                        tabInfoData.setLabel((String) tabInfoMap.get("label"));
                                    if (tabInfoMap.get("type") != null)
                                        tabInfoData.setType((String) tabInfoMap.get("type"));
                                    if (tabInfoMap.get("image") != null)
                                        tabInfoData.setImage((String) tabInfoMap.get("image"));
                                    if (tabInfoMap.get("showTodo") != null)
                                        tabInfoData.setShowTodo((boolean) tabInfoMap.get("showTodo"));
//                                    tabInfoDataArrayList.add(tabInfoData);
                                }
                            }
                        }
                    }

                } else if (OustStaticVariableHandling.getInstance().getIsNewLayout() == 3) {
                    if (layoutInfoData.get("LAYOUT_3") != null) {
                        Map<Object, Object> newLandingPageData = (Map<Object, Object>) layoutInfoData.get("LAYOUT_3");
                        int defaultTab;
                        if (newLandingPageData.get("defaultTab") != null)
                            defaultTab = (int) (long) newLandingPageData.get("defaultTab");
                        if (newLandingPageData.get("tabSection") != null) {
                            Log.d(TAG, "onDataChange: ");
                            ArrayList<Object> tabSectionMap = (ArrayList<Object>) newLandingPageData.get("tabSection");
                            if (tabSectionMap != null && tabSectionMap.size() > 0) {
                                for (int i = 0; i < tabSectionMap.size(); i++) {
                                    TabInfoData tabInfoData = new TabInfoData();
                                    Map<Object, Object> tabInfoMap = (Map<Object, Object>) tabSectionMap.get(i);
                                    if (tabInfoMap.get("label") != null)
                                        tabInfoData.setLabel((String) tabInfoMap.get("label"));
                                    if (tabInfoMap.get("type") != null)
                                        tabInfoData.setType((String) tabInfoMap.get("type"));
                                    if (tabInfoMap.get("indexName") != null) {
                                        String indexName = (String) tabInfoMap.get("indexName");
                                        if (indexName.equalsIgnoreCase("COURSE_AND_ASSESSMENT"))
                                            tabInfoData.setIndexName(indexName);
                                    }
                                    if (tabInfoMap.get("showTodo") != null) {
                                        tabInfoData.setShowTodo((boolean) tabInfoMap.get("showTodo"));
                                    }

                                    if (tabInfoMap.get("tabTag") != null)
                                        tabInfoData.setTabTags((String) tabInfoMap.get("tabTag"));
                                    if (tabInfoMap.get("image") != null)
                                        tabInfoData.setImage((String) tabInfoMap.get("image"));

                                    if (tabInfoMap.get("categoryId") != null)
                                        tabInfoData.setCategoryId((long) tabInfoMap.get("categoryId"));

                                    if (tabInfoMap.get("catalogueTabId") != null)
                                        tabInfoData.setCatalogueTabId((long) tabInfoMap.get("catalogueTabId"));
                                    if (tabInfoMap.get("catalogueType") != null) {
                                        tabInfoData.setCatalogueType((String) tabInfoMap.get("catalogueType"));
                                    }
                                    if (tabInfoMap.get("hideCatalogue") != null)
                                        tabInfoData.setHideCatalogue((Boolean) tabInfoMap.get("hideCatalogue"));
                                }

                            }
                        }
                        if (newLandingPageData.get("filterCategory") != null) {
                            ArrayList<Object> filterCategoryMap = (ArrayList<Object>) newLandingPageData.get("filterCategory");
                            if (filterCategoryMap != null && filterCategoryMap.size() > 0) {
                                ArrayList<FilterCategory> filterCategories = new ArrayList<>();
                                for (int i = 0; i < filterCategoryMap.size(); i++) {
                                    FilterCategory filterCategory = new FilterCategory();
                                    Map<Object, Object> filterInfoMap = (Map<Object, Object>) filterCategoryMap.get(i);
                                    if (filterInfoMap.get("label") != null)
                                        filterCategory.setCategoryName((String) filterInfoMap.get("label"));
                                    if (filterInfoMap.get("type") != null)
                                        filterCategory.setCategoryType((int) (long) filterInfoMap.get("type"));

                                    filterCategories.add(filterCategory);
                                }
                            }
                        }
                    }
                }

            }

        }
        //Log.e(TAG, "about to reach setToolBArCOLOR method");
    }

    private void clearBrandingGuidLines() {
        try {
            OustPreferences.clear("DEFAULT_COURSE_AUDIO");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
            OustPreferences.clear(AppConstants.StringConstants.COURSE_LOCK_COLOR);
            OustPreferences.clear("DEFAULT_INDICATOR_ICON");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
            OustPreferences.clear("DEFAULT_FINISH_ICON");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_FINISH_ICON);
            OustPreferences.clear("DEFAULT_START_ICON");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_START_ICON);
            OustPreferences.clear("DEFAULT_LEVEL_AUDIO");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_AUDIO_PATH);
            OustPreferences.clear("DEFAULT_COURSE_AUDIO");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH);
            OustPreferences.clear(AppConstants.StringConstants.COURSE_LOCK_COLOR);
            OustPreferences.clear("DEFAULT_INDICATOR_ICON");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
            OustPreferences.clear("DEFAULT_FINISH_ICON");
            OustPreferences.clear(AppConstants.StringConstants.COURSE_FINISH_ICON);
            OustPreferences.clear(AppConstants.StringConstants.RM_USER_STATUS_COLOR);
            OustPreferences.clear(AppConstants.StringConstants.RM_COURSE_LOCK_COLOR);
            OustPreferences.clear(AppConstants.StringConstants.COURSE_UNLOCK_COLOR);
            OustPreferences.clear(AppConstants.StringConstants.CURRENT_LEVEL_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadImages(String urlPath, String fileName) {
        try {
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {

                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {

                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            downloadFiles.startDownLoad(urlPath, OustSdkApplication.getContext().getFilesDir() + "/", fileName, true, false);

        } catch (Exception e) {
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }

    public MutableLiveData<ParentCplDistributionResponse> checkParentCplDistributesOrNot() {
        mmCplDistributionResponseMutableLiveData = new MutableLiveData<>();
        String mmCplDistributesOrNot = OustSdkApplication.getContext().getResources().getString(R.string.parent_cpl_distribution_or_not);
        String tenantName = OustPreferences.get("tanentid").trim();
        mmCplDistributesOrNot = mmCplDistributesOrNot.replace("{orgId}", tenantName);
        mmCplDistributesOrNot = mmCplDistributesOrNot.replace("{userId}", activeUser.getStudentid());
        mmCplDistributesOrNot = HttpManager.getAbsoluteUrl(mmCplDistributesOrNot);
        Gson mGson = new Gson();
        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
        Log.e("BranchTools", "checkParentCplDistributesOrNot: --> " + mmCplDistributesOrNot);
        ApiCallUtils.doNetworkCall(Request.Method.GET, mmCplDistributesOrNot, jsonParams, new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ParentCplDistributionResponse parentCplDistributionResponse = mGson.fromJson(response.toString(), ParentCplDistributionResponse.class);
                    if (parentCplDistributionResponse != null) {
                        if (parentCplDistributionResponse.getSuccess()) {
                            mmCplDistributionResponseMutableLiveData.postValue(parentCplDistributionResponse);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        return mmCplDistributionResponseMutableLiveData;
    }
}
