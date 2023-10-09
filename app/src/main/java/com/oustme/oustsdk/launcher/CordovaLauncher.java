package com.oustme.oustsdk.launcher;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.activity.common.SplashActivity;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.OustLoginCallBack;
import com.oustme.oustsdk.interfaces.common.OustModuleCallBack;
import com.oustme.oustsdk.launcher.OustExceptions.NotificationTypeNotFoundException;
import com.oustme.oustsdk.launcher.OustExceptions.NullActivityException;
import com.oustme.oustsdk.launcher.OustExceptions.NullAuthDataException;
import com.oustme.oustsdk.launcher.OustExceptions.OrgIdNotFoundException;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.launcher.OustExceptions.SecretKeyNotFound;
import com.oustme.oustsdk.launcher.OustExceptions.ServerKeyNotFoundException;
import com.oustme.oustsdk.launcher.OustExceptions.TokenNotFoundException;
import com.oustme.oustsdk.launcher.OustExceptions.UserNameNotFoundException;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.request.CheckUserRequestData;
import com.oustme.oustsdk.request.RegisterRequestData;
import com.oustme.oustsdk.request.SignInRequest;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.response.common.LevelOneAuthCheckResponseData;
import com.oustme.oustsdk.response.common.LoginType;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.response.common.VerifyOrgIdResponseA;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.EntityResourceCollection;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NotificationData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import io.branch.referral.Branch;

/**
 * This is CordovaLauncher class to launch oust
 * It checks for all the validations
 */

public class CordovaLauncher {
    private String userName = null;
    private String password = null;
    private String orgId = null;
    private String myAPIKey;
    private String token, serverKey;
    private Activity activity;
    private OustAppConfigData oustAppConfigData;
    OustAuthData oustAuthData;
    private OustNotificationConfig notificationConfig;
    private PushNotificationType pushNotificationType;
    private OustLoginCallBack oustLoginCallBack;
    private Map<String, String> oustNotificationData = null;
    private OustModuleCallBack oustModuleCallBack;

    private static final String TAG = "CordovaLauncher";

    private static CordovaLauncher CordovaLauncher = null;

    private CordovaLauncher() {
    }

    public OustModuleCallBack getOustModuleCallBack() {
        return oustModuleCallBack;
    }

    public void setOustModuleCallBack(OustModuleCallBack oustModuleCallBack) {
        this.oustModuleCallBack = oustModuleCallBack;
    }

    public static CordovaLauncher getInstance() {
        if (CordovaLauncher == null) {
            synchronized (CordovaLauncher.class) {
                if (CordovaLauncher == null)
                    CordovaLauncher = new CordovaLauncher();
            }
        }
        return CordovaLauncher;
    }

    public void launch(Activity activity, OustAuthData oustAuthData, OustLoginCallBack oustLoginCallBack) throws OustException {
        this.oustLoginCallBack = oustLoginCallBack;
        launchMe(activity, null, oustAuthData, null);
    }


    public void launchNotification(Activity activity, OustAuthData oustAuthData, OustLoginCallBack oustLoginCallBack, Map<String, String> oustNotificationData) throws OustException {
        this.oustLoginCallBack = oustLoginCallBack;
        this.oustNotificationData = oustNotificationData;
        launchMe(activity, null, oustAuthData, null);
    }


    public void launch(Activity activity, OustAuthData oustAuthData, OustNotificationConfig notificationConfig, OustLoginCallBack oustLoginCallBack) throws OustException {
        this.oustLoginCallBack = oustLoginCallBack;
        launchMe(activity, null, oustAuthData, notificationConfig);
    }


    public void launchMe(Activity activity, OustAppConfigData oustAppConfigData, OustAuthData oustAuthData, OustNotificationConfig notificationConfig) throws OustException {
        Log.d(TAG, "launchMe: ");
        if (activity == null) {
            throw new NullActivityException();
        }
        try {
            if (this.activity != null)
                this.activity.unregisterReceiver(myFileDownLoadReceiver);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, e.getLocalizedMessage());
            Log.d(TAG, e.toString());
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity.getApplicationContext());

        if (myFileDownLoadReceiver == null)
            setReceiver();

        if (oustAuthData == null) {
            throw new NullAuthDataException();
        }
        this.oustAuthData = oustAuthData;
        if ((oustAuthData.getUsername() == null) || (oustAuthData.getUsername().isEmpty())) {
            throw new UserNameNotFoundException();
        } else {
            this.userName = oustAuthData.getUsername();
        }

        /*try {
            if ((oustAuthData.getfName() != null) || (!oustAuthData.getfName().isEmpty())) {
                this.fName = oustAuthData.getfName();
            }
            if ((oustAuthData.getlName() != null) || (!oustAuthData.getlName().isEmpty())) {
                this.lName = oustAuthData.getlName();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/

        if ((oustAuthData.getOrgId() == null) || (oustAuthData.getOrgId().isEmpty())) {
            throw new OrgIdNotFoundException();
        } else {
            this.orgId = oustAuthData.getOrgId();
        }
//        if((oustAuthData.getPassword()==null)||(oustAuthData.getPassword().isEmpty())) {
//            throw new PasswordNotFoundException();
//        }else {
//            this.password= oustAuthData.getPassword();
//
//        }
        if (notificationConfig != null) {
            this.notificationConfig = notificationConfig;
            if (notificationConfig.getPushNotificationType() == null) {
                throw new NotificationTypeNotFoundException();
            }
            this.pushNotificationType = notificationConfig.getPushNotificationType();
            if ((notificationConfig.getServerKey() == null) || notificationConfig.getServerKey().isEmpty()) {
                throw new ServerKeyNotFoundException();
            }
            this.serverKey = notificationConfig.getServerKey();
            if ((notificationConfig.getToken() == null) || notificationConfig.getToken().isEmpty()) {
                throw new TokenNotFoundException();
            }
            this.token = notificationConfig.getToken();
        }
        onSplashStart();
        validateAPIKey();
    }

    //    volley call to validate api key
    private void validateAPIKey() throws OustException {
        try {
            if (!OustPreferences.getAppInstallVariable("isSecreteKeyValidated")) {
                HttpManager.setBaseUrl();
                Log.e("URLSET", "THIS IS BASE URL " + HttpManager.getAbsoluteUrl(null));
                String secretKey = getAPIKey();
                Log.e("GOT KEY", "THIS IS SECRET KEY " + secretKey);
                String validateAPIKeyUrl = OustSdkApplication.getContext().getResources().getString(R.string.apiKey);
                validateAPIKeyUrl = validateAPIKeyUrl.replace("secretKey", secretKey);
                validateAPIKeyUrl = HttpManager.getAbsoluteUrl(validateAPIKeyUrl);
                Log.e("URL", "THIS IS URL " + validateAPIKeyUrl);
                Log.e("REQUEST", "ABOUT TO MAKE REQUEST" + validateAPIKeyUrl);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, validateAPIKeyUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("GOT RESPONSE", "THIS IS RESPONSE " + response);
                        Gson gson = new Gson();
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        Log.e("GOT RESPONSE", "THIS IS RESPONSE " + commonResponse);
                        if ((commonResponse != null) && (commonResponse.isSuccess())) {
                            Log.e("GOT RESPONSE", "THIS IS RESPONSE " + commonResponse.isSuccess());
                            onSplashStart();
                        } else {
                            oustLoginCallBack.onError("Invalid secret key");
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GOT RESPONSE", "THIS IS ERROR RESPONSE " + error);
                        oustLoginCallBack.onError("Invalid secret key");
                    }
                });
            } else {
                onSplashStart();
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Invalid secret key");
        }
    }

    //    start Popup activity to show invalid secret key msg and then end the activity to return back to native app
    private void showPopup() {
        Popup popup = new Popup();
        OustPopupButton oustPopupButton = new OustPopupButton();
        oustPopupButton.setBtnText("OK");
        List<OustPopupButton> btnList = new ArrayList<>();
        btnList.add(oustPopupButton);
        popup.setButtons(btnList);
        popup.setContent("Oops! invalid secret key");
        popup.setCategory(OustPopupCategory.NOACTION);
        OustStaticVariableHandling.getInstance().setOustpopup(popup);
        Intent intent = new Intent(activity, PopupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
        activity.startActivity(intent);
    }

    //    To get the api key from meta-data
    private String getAPIKey() throws OustException {
        String TAG = "Example Meta-Data";
        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            myAPIKey = bundle.getString("com.oust.sdk.SecretKey");
            OustPreferences.save("api_key", myAPIKey);
            Log.e(TAG, "API KEY : " + myAPIKey);
            if (myAPIKey == null) {
                throw new SecretKeyNotFound();
            }
            return myAPIKey;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        if (myAPIKey == null) {
            throw new SecretKeyNotFound();
        }
        return myAPIKey;
    }

    //    launch oust if everything is fine
    private void gotoOust() {
        Intent intent = new Intent(OustSdkApplication.getContext(), SplashActivity.class);
        if (((userName != null) && (!userName.isEmpty())) &&
                ((password != null) && (!password.isEmpty())) &&
                ((orgId != null) && (!orgId.isEmpty()))) {
            intent.putExtra("userName", userName);
            intent.putExtra("password", password);
            intent.putExtra("orgId", orgId);
            intent.putExtra("regId", token);
            intent.putExtra("serverKey", serverKey);

            if ((notificationConfig != null) && (notificationConfig.getPushNotificationType() != null)) {
                intent.putExtra("pushNotificationType", pushNotificationType.toString());
            }
            activity.startActivity(intent);
        }
    }

//=============================================================================================================

    private String regId = "";
    private String toolbarcolor;
    private String pushnotificationType;
    private int RES_AUDIO = 1, RES_IMAGE = 2, RES_FONT = 3;

    String isPlayModeEnabled, companydisplayName, panelColor;
    private SignInRequest signInRequest;
    private SignInResponse signInResponse;
    private boolean isActivityActive = true;
    public static Branch branch;
    private FirebaseAuth mAuth;

    private void onSplashStart() {
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity.getApplicationContext());
        }
        OustPreferences.saveAppInstallVariable("isSecreteKeyValidated", true);
        setPrevioustAppLanguage();
        firstTimeAuth = false;
        initializeRest();
        try {
            if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                Log.e("firebase app name", firebaseApp.getName());
                mAuth = FirebaseAuth.getInstance(firebaseApp);
            } else {
                mAuth = FirebaseAuth.getInstance();
            }
        } catch (Exception e) {
        }
        //mAuth = FirebaseAuth.getInstance();
        downloadAllResourses();
    }

    private void checkLoginProcess2() {
        Log.d(TAG, "checkLoginProcess: ");
        try {
            if (!OustAppState.getInstance().isLandingPageOpen()) {
                OustAppState.getInstance().setLandingPageOpen(true);
                final String userdata = OustPreferences.get("userdata");
                if ((userdata != null) && (!userdata.isEmpty())) {
                    if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                        if (oustNotificationData != null) {
                            startNotification();
                        } else {
                            startLandingPage();
                        }
                    } else {
                        startLoginProcess();
                    }
                } else {
                    startLoginProcess();
                }
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPrevioustAppLanguage() {
        try {
            String selectedlanguageprefix = getSelectedLanguagePrefix();
            if (selectedlanguageprefix == null) {
                selectedlanguageprefix = "en";
            } else if (selectedlanguageprefix.isEmpty()) {
                selectedlanguageprefix = "en";
            }
            setLocale(selectedlanguageprefix);
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String getSelectedLanguagePrefix() {
        return OustPreferences.get("selectedlanguageprefix");
    }

    public void setLocale(String selectedlanguageprefix) {
        try {
            Locale locale = new Locale(selectedlanguageprefix);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
            OustStrings.init();
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
        }
    }


    private void initializeRest() {
        initServerEndPoint();
        initlizeBranch();
    }

    public void initServerEndPoint() {
        try {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initlizeBranch() {
        try {
            Branch branch_local = Branch.getInstance();
            branch_local.initSession();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            oustLoginCallBack.onError("Something went wrong!!");
        }
    }

    //====================================================================================================
//check on firebase for app resourses update
//initiate firebase for first time
    public void initFirebase() {
        try {
            autheticateWithFirebase();
        } catch (Exception e) {
            Log.d(TAG, "initFirebase: ");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            updateAppResoursesOver();
            oustLoginCallBack.onError("Firebase initialization error");
        }
    }

    private boolean firstTimeAuth = false;

    public void autheticateWithFirebase() {
        try {
            final String firebaseToken = OustPreferences.get("firebaseToken");
            if ((firebaseToken != null) && (!firebaseToken.isEmpty())) {
                try {
                    if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                        FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                        Log.e("firebase app name", firebaseApp.getName());
                        mAuth = FirebaseAuth.getInstance(firebaseApp);
                    } else {
                        mAuth = FirebaseAuth.getInstance();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                mAuth.signInWithCustomToken(firebaseToken)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task != null) {
                                    if (!task.isSuccessful()) {
                                        if (!firstTimeAuth) {
                                            firstTimeAuth = true;
                                            getNewTokenForFireBase();
                                        } else {
                                            OustPreferences.clear("userdata");
                                            OustPreferences.clear("tanentid");
                                            updateAppResoursesOver();
                                        }
                                    } else {
                                        getUpdateResoursesData();
                                    }
                                } else {
                                    OustPreferences.clear("userdata");
                                    OustPreferences.clear("tanentid");
                                    updateAppResoursesOver();
                                }
                            }
                        });
            } else {
                OustPreferences.clear("userdata");
                OustPreferences.clear("tanentid");
                updateAppResoursesOver();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            updateAppResoursesOver();
        }
    }


    private void getNewTokenForFireBase() {
        Log.d(TAG, "getNewTokenForFireBase: ");
        if (OustSdkTools.checkInternetStatus()) {
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData();
                String user_id = activeUser.getStudentid();
                String get_fireBase_token_url = OustSdkApplication.getContext().getResources().getString(R.string.get_fireBase_token_url);
                get_fireBase_token_url = get_fireBase_token_url.replace("{userId}", user_id);
                get_fireBase_token_url = HttpManager.getAbsoluteUrl(get_fireBase_token_url);

                ApiCallUtils.doNetworkCall(Request.Method.GET, get_fireBase_token_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        gotFireBaseToken(response);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        gotFireBaseToken(null);
                    }
                });

            } else {
                getUpdateResoursesData();
                //updateAppResoursesOver();
            }
        } else {
            checkLoginProcess();
        }
    }

    private void gotFireBaseToken(JSONObject commonResponse) {
        if (commonResponse != null) {
            if (commonResponse.optBoolean("success")) {
                Log.e("firebaseToken taken", commonResponse.toString());
                String firebaseToken = commonResponse.optString("token");
                OustPreferences.save("firebaseToken", firebaseToken);
                autheticateWithFirebase();
            } else {
                OustPreferences.clear("userdata");
                OustPreferences.clear("tanentid");
                updateAppResoursesOver();
            }
        } else {
            OustPreferences.clear("userdata");
            OustPreferences.clear("tanentid");
            updateAppResoursesOver();
        }
    }

    public void getUpdateResoursesData() {
        String msg = "system/appResourses/android";
        Log.d(TAG, "getUpdateResoursesData: " + msg);

        ValueEventListener languageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        LanguagesClasses classes = new LanguagesClasses();
                        Map<String, Object> mainMap = (Map<String, Object>) snapshot.getValue();

                        if (mainMap.get("audioData") != null) {
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("audioData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(1);
                            if ((resCollectData != null) && (resCollectData.getTimeStamp() < updateTime)) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isAudioUpdated", true);
                            }
                        }
                        if (mainMap.get("imageData") != null) {
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("imageData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(2);
                            if ((resCollectData != null) && (resCollectData.getTimeStamp() < updateTime)) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isImageUpdated", true);
                            }
                        }
                        if (mainMap.get("fontData") != null) {
                            HashMap<String, Object> audioData = (HashMap<String, Object>) mainMap.get("fontData");
                            Log.i("tag", audioData.toString());
                            long updateTime = (long) audioData.get("updateTime");
                            HashMap<String, String> soundCollection = (HashMap<String, String>) audioData.get("data");
                            EntityResourceCollection resCollectData = RoomHelper.getResourceCollectionModel(3);
                            if ((resCollectData != null) && (resCollectData.getTimeStamp() < updateTime)) {
                                RoomHelper.updateResourceCollectionModel(resCollectData, soundCollection.toString(), updateTime);
                                OustPreferences.saveAppInstallVariable("isfontUpdated", true);
                            }
                        }
                        if (mainMap.get("splashData") != null) {
                            Map<String, Object> splashDataMap = (Map<String, Object>) mainMap.get("splashData");
                            long updateChecksum = (long) splashDataMap.get("updateChecksum");
                            if (updateChecksum > 0) {
                                String s1 = OustPreferences.get("lastSpalshUpdateTime");
                                Log.e(TAG, "updateTime" + s1);
                                if ((OustPreferences.get("lastSpalshUpdateTime") == null) ||
                                        (((OustPreferences.get("lastSpalshUpdateTime") != null) && ((OustPreferences.get("lastSpalshUpdateTime").isEmpty())))) ||
                                        ((OustPreferences.get("lastSpalshUpdateTime") != null) && (!("" + updateChecksum).equalsIgnoreCase(OustPreferences.get("lastSpalshUpdateTime"))))) {
                                    OustPreferences.save("lastSpalshUpdateTime", ("" + updateChecksum));
                                    if (OustPreferences.getAppInstallVariable("isDownloadedImageLastTime")) {
                                        OustPreferences.clear("isDownloadedImageLastTime");
                                    } else {
                                        OustPreferences.saveAppInstallVariable("isSplashUpdate", true);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    //oustLoginCallBack.onError("Something went wrong!!");
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                updateAppResoursesOver();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //oustLoginCallBack.onError("Something went wrong!!");
                Log.i("splash firebase ", error.toString());
                updateAppResoursesOver();
            }

        };
        OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg).addValueEventListener(languageListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(languageListener, msg));
    }

    private void checkForLannguageUpdate(LanguagesClasses classes) {
        try {
            String s1 = OustPreferences.get("alllanguage");
            List<LanguageClass> allUpdateClasses = new ArrayList<>();
            Gson gson = new Gson();
            if ((s1 != null) && (!s1.isEmpty())) {
                LanguagesClasses languagesClasses = gson.fromJson(s1, LanguagesClasses.class);
                if ((languagesClasses != null) && (languagesClasses.getLastTimestamp() > 0)) {
                    if (languagesClasses.getLastTimestamp() != classes.getLastTimestamp()) {
                        for (int i = 0; i < languagesClasses.getLanguageClasses().size(); i++) {
                            if (languagesClasses.getLanguageClasses().get(i).getTimeStamp() != classes.getLanguageClasses().get(i).getTimeStamp()) {
                                allUpdateClasses.add(classes.getLanguageClasses().get(i));
                            }
                        }
                        String langStr = gson.toJson(classes);
                        OustPreferences.save("alllanguage", langStr);
                    }
                } else {
                    String langStr = gson.toJson(classes);
                    OustPreferences.save("alllanguage", langStr);
                }
            } else {
                String langStr = gson.toJson(classes);
                OustPreferences.save("alllanguage", langStr);
            }

            String s2 = OustPreferences.get("updatelanguage");
            LanguagesClasses updateLanguages = new LanguagesClasses();
            if ((s2 != null) && (!s2.isEmpty())) {
                updateLanguages = gson.fromJson(s2, LanguagesClasses.class);
                if (updateLanguages == null) {
                    updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
                } else if (updateLanguages.getLanguageClasses() == null) {
                    updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
                }
            } else {
                updateLanguages.setLanguageClasses(new ArrayList<LanguageClass>());
            }
            for (int i = 0; i < allUpdateClasses.size(); i++) {
                EntityResourseStrings resourseStringsModel = RoomHelper.getResourceStringModel(allUpdateClasses.get(i).getLanguagePerfix());
                if ((resourseStringsModel != null) && (resourseStringsModel.getHashmapStr() != null)) {
                    updateLanguages.getLanguageClasses().add(allUpdateClasses.get(i));
                }
            }
            if (updateLanguages.getLanguageClasses().size() > 0) {
                updateLanguageClasses.addAll(updateLanguages.getLanguageClasses());
                String langStr1 = gson.toJson(updateLanguages);
                OustPreferences.save("updatelanguage", langStr1);
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateAppResoursesOver() {
        startDownloadingResourses2();
    }

    private void downloadImage(final String tanentId, String imagePath, final String filename, final boolean isBackgroundImage) {
        try {
            Log.e(TAG, "downloadImage");

            final File file = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            String awsKeyId = OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = OustPreferences.get("awsS3KeySecret");

            Log.d(TAG, "downloadImage: awsKeyId:" + awsKeyId);
            Log.d(TAG, "downloadImage: awsSecretKeyId:" + awsSecretKeyId);

            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(OustPreferences.get(AppConstants.StringConstants.S3_BKT_REGION)));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            TransferObserver transferObserver = transferUtility.download(OustPreferences.get(AppConstants.StringConstants.IMG_BKT_NAME), imagePath, file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        if (filename.endsWith("splashScreen")) {
                            OustPreferences.saveAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED, true);
                        }
                        //saveData(file, filename, isBackgroundImage);
                        Log.e(TAG, "doiwnload success " + filename);
                        //showDownloadProgress();
                    } else if (state == TransferState.FAILED) {
                        Log.e(TAG, "doiwnload failed " + filename);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "downloadImage" + e.getMessage());
        }
    }


    //==================================================================================================
//    Login process to get Data to load course and assessment from Server
    private boolean checkLoginCalled = false;

    private void checkLoginProcess() {
        Log.d(TAG, "checkLoginProcess: ");
        try {
            if (!OustAppState.getInstance().isLandingPageOpen()) {
                OustAppState.getInstance().setLandingPageOpen(true);
                final String userdata = OustPreferences.get("userdata");
                if ((userdata != null) && (!userdata.isEmpty())) {
                    if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                        if (oustNotificationData != null) {
                            startNotification();
                        } else {
                            startLandingPage();
                        }
                    } else {
                        startLoginProcess();
                    }
                } else {
                    startLoginProcess();
                }
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startLoginProcess() {
        oustLoginCallBack.onLoginProcessStart();
        userName = (userName.trim());
        if ((userName != null) && (userName.length() > 1)) {
            {
                getBaseURL(orgId);
            }
        }
    }

    private void getBaseURL(String orgId) {
        urlForBaseURLCheck = AppConstants.StringConstants.SIGN_URL_BASE;
        urlForBaseURLCheck = urlForBaseURLCheck.replace("{orgId}", orgId);
        //new HttpGetRequest().execute(urlForBaseURLCheck);
        new CheckBaseURL().execute(urlForBaseURLCheck);
        //verifyTenant(orgId);
    }

    private void startNotification() {
        OustPreferences.save("test_userid", userName);
        OustPreferences.save("test_orgid", orgId);

        NotificationData notificationData = new NotificationData();
        if (oustNotificationData.get("imageUrl") != null) {
            notificationData.setImgUrl(oustNotificationData.get("imageUrl"));
        }
        if (oustNotificationData.get("id") != null) {
            notificationData.setId(oustNotificationData.get("id"));
        }
        if (oustNotificationData.get("title") != null) {
            notificationData.setTitle(oustNotificationData.get("title"));
        }
        if (oustNotificationData.get("type") != null) {
            notificationData.setGcmType(oustNotificationData.get("type"));
        }
        if (oustNotificationData.get("noticeBoardNotificationData") != null) {
            notificationData.setNoticeBoardNotificationData(oustNotificationData.get("noticeBoardNotificationData"));
        }

        try {
            Intent resultIntent = null;
            if (notificationData.getGcmType() != null) {
                Log.e("Notification", "  inside sendNotification() --- gcmType is " + notificationData.getGcmType());
                if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                    resultIntent = new Intent(activity, LandingActivity.class);
                } else {
                    resultIntent = new Intent(activity, LandingActivity.class);
                }

                if (notificationData.getGcmType().equalsIgnoreCase(GCMType.NOTICEBOARD_REPLY.name())) {
                    int nbId = 0, postId = 0, commentId = 0, replyId = 0;
                    if (notificationData.getNoticeBoardNotificationData() != null && !notificationData.getNoticeBoardNotificationData().isEmpty()) {
                        JSONObject jsonObject = new JSONObject(notificationData.getNoticeBoardNotificationData());
                        nbId = jsonObject.optInt("nbId");
                        postId = jsonObject.optInt("postId");
                        commentId = jsonObject.optInt("commentId");
                        replyId = jsonObject.optInt("replyId");
                    }
                    resultIntent.putExtra("nbId", nbId);
                    resultIntent.putExtra("postId", postId);
                    resultIntent.putExtra("commentId", commentId);
                    resultIntent.putExtra("replyId", replyId);
                } else if (notificationData.getGcmType().equalsIgnoreCase(GCMType.NOTICEBOARD_POST.name())) {
                    int nbId = 0, postId = 0, commentId = 0, replyId = 0;
                    if (notificationData.getNoticeBoardNotificationData() != null && !notificationData.getNoticeBoardNotificationData().isEmpty()) {
                        JSONObject jsonObject = new JSONObject(notificationData.getNoticeBoardNotificationData());
                        nbId = jsonObject.optInt("nbId");
                        postId = jsonObject.optInt("postId");
                        commentId = jsonObject.optInt("commentId");
                        replyId = jsonObject.optInt("replyId");
                    }
                    resultIntent.putExtra("nbId", nbId);
                    resultIntent.putExtra("postId", postId);
                    resultIntent.putExtra("commentId", commentId);
                    resultIntent.putExtra("replyId", replyId);
                } else if (notificationData.getGcmType().equalsIgnoreCase(GCMType.ASSESSMENT_NOTIFICATION.name())) {
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("assessmentId", notificationData.getId());
                    }
                } else if (notificationData.getGcmType().equalsIgnoreCase(GCMType.ASSESSMENT_DISTRIBUTE.name())) {
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("assessmentId", notificationData.getId());
                    }
                } else if (notificationData.getGcmType().equalsIgnoreCase("COURSE_DISTRIBUTE")) {
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("courseId", notificationData.getId());
                    }
                } else if (notificationData.getGcmType().equalsIgnoreCase(GCMType.COLLECTION_DISTRIBUTE.name())) {
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("collectionId", notificationData.getId());
                    }
                } else if (notificationData.getGcmType().equalsIgnoreCase(GCMType.FEED_DISTRIBUTE.name())) {
                    Log.e("Notification", "  inside sendNotification() and notification build");
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("feedId", notificationData.getId());
                    }
                    resultIntent.putExtra("isFeedDistributed", true);

                } else if (notificationData.getGcmType().equalsIgnoreCase("NOTICE_BOARD_DISTRIBUTION")) {
                    Log.e("Notification", "  inside sendNotification() and NOTICE_BOARD_DISTRIBUTION build");
                    if ((notificationData.getId() != null) && (!notificationData.getId().isEmpty())) {
                        resultIntent.putExtra("noticeBoardId", notificationData.getId());
                    }
                }

                if (resultIntent != null) {
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    OustSdkTools.newActivityAnimation(resultIntent, activity);
                } else {
                    oustLoginCallBack.onError("Couldn't able to load the notification");
                }
            } else {
                oustLoginCallBack.onError("Couldn't able to load the notification");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            oustLoginCallBack.onError("Couldn't able to load the notification");
        }

        //OustNotificationHandler oustNotificationHandler=new OustNotificationHandler(oustNotificationData, activity);
    }

    private void startLandingPage() {
        Log.d(TAG, "startLandingPage: ");
        try {
            if (OustPreferences.getAppInstallVariableDefaultTrue("isSplashUpdate")) {
                Log.d(TAG, "startLandingPage: spalshupdate");
                OustPreferences.saveAppInstallVariable("isSplashUpdate", false);
                String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
                downloadImage(orgId, imagePath1, "oustlearn_" + (orgId.toUpperCase()) + "splashScreen", true);

                String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
                downloadImage(orgId, imagePath2, "oustlearn_" + (orgId.toUpperCase()) + "splashIcon", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        OustAppState.getInstance().setLandingPageOpen(false);

        try {
            unRegisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        checkLayoutOpenLandingPage();

        /*Intent intent = new Intent(activity, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        OustSdkTools.newActivityAnimation(intent, activity);
        OustPreferences.save("test_userid", userName);
        OustPreferences.save("test_orgid", orgId);*/
    }

    public void verifyTenant(final String orgId) {
        try {
            setTanentID(orgId);
            String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyOrgId);
            verifyOrgUrl = verifyOrgUrl.replace("{orgId}", orgId);
            verifyOrgUrl = verifyOrgUrl + "?devicePlatformName=Android";
            verifyOrgUrl = HttpManager.getAbsoluteUrl(verifyOrgUrl);
            Log.d(TAG, "verifyTenant: " + verifyOrgUrl);

            ApiCallUtils.doNetworkCallWithoutAuth(Request.Method.PUT, verifyOrgUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    OustPreferences.save("firebaseAppId", response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey", response.optString("firebaseAPIKey"));
                    Gson gson = new Gson();
                    VerifyOrgIdResponseA verifyOrgIdResponse = gson.fromJson(response.toString(), VerifyOrgIdResponseA.class);
                    gotVerifyTanentResponse(verifyOrgIdResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.networkResponse.allHeaders.toString());
                    Log.d(TAG, "onErrorResponse: " + error.networkResponse.statusCode);
                    Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotVerifyTanentResponse(VerifyOrgIdResponseA verifyOrgIdResponse) {
        Log.d(TAG, "gotVerifyTanentResponse: ");
        if ((verifyOrgIdResponse != null) && (verifyOrgIdResponse.isSuccess())) {
            if (verifyOrgIdResponse.isValidTenant()) {
                signInRequest = new SignInRequest();
                if ((OustPreferences.get("gcmToken") != null)) {
                    signInRequest.setDeviceToken(OustPreferences.get("gcmToken"));
                }
                String encryptedPassword = null;
                String otpStr = "test";
                try {
                    if (otpStr.matches("[a-fA-F0-9]{32}")) {
                        encryptedPassword = otpStr;
                    } else {
                        encryptedPassword = getMD5EncodedString(otpStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                signInRequest.setStudentid(userName);
                signInRequest.setPassword(encryptedPassword);
                signInRequest.setClientEncryptedPassword(true);
                signInRequest.setInstitutionLoginId(orgId);
                if (verifyOrgIdResponse.getS3_base_end() != null && !verifyOrgIdResponse.getS3_base_end().isEmpty()) {
                    AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL = verifyOrgIdResponse.getS3_base_end() + verifyOrgIdResponse.getImg_bucket_name() + "/";
                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, verifyOrgIdResponse.getS3_base_end() + verifyOrgIdResponse.getImg_bucket_name() + "/");
                } else {
                    String s3baseend = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL;
                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, s3baseend);
                }

                if (verifyOrgIdResponse.getS3_Bucket_Region() != null && !verifyOrgIdResponse.getS3_Bucket_Region().isEmpty()) {
                    AppConstants.MediaURLConstants.BUCKET_REGION = verifyOrgIdResponse.getS3_Bucket_Region();
                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, verifyOrgIdResponse.getS3_Bucket_Region());
                } else {
                    String s3BktRegion = AppConstants.MediaURLConstants.BUCKET_REGION;
                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, s3BktRegion);
                }

                if (verifyOrgIdResponse.getHttp_img_bucket_cdn() != null && !verifyOrgIdResponse.getHttp_img_bucket_cdn().isEmpty()) {
                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH = verifyOrgIdResponse.getHttp_img_bucket_cdn();
                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, verifyOrgIdResponse.getHttp_img_bucket_cdn());
                } else {
                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
                }


                if (verifyOrgIdResponse.getImg_bucket_cdn() != null && !verifyOrgIdResponse.getImg_bucket_cdn().isEmpty()) {
                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS = verifyOrgIdResponse.getImg_bucket_cdn();
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, verifyOrgIdResponse.getImg_bucket_cdn());
                } else {
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
                }


                if (verifyOrgIdResponse.getImg_bucket_name() != null && !verifyOrgIdResponse.getImg_bucket_name().isEmpty()) {
                    S3_BUCKET_NAME = verifyOrgIdResponse.getImg_bucket_name();
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, verifyOrgIdResponse.getImg_bucket_name());
                } else {
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, S3_BUCKET_NAME);
                }

                if (verifyOrgIdResponse.getWebAppLink() != null && !verifyOrgIdResponse.getWebAppLink().isEmpty()) {
                    Log.d(TAG, "onResponse: webAppLink:" + verifyOrgIdResponse.getWebAppLink());
                    com.oustme.oustsdk.tools.OustPreferences.save("webAppLink", verifyOrgIdResponse.getWebAppLink());
                } else {
                    Log.d(TAG, "onResponse: webAppLink else-->:" + verifyOrgIdResponse.getWebAppLink());
                }

                if (verifyOrgIdResponse.getAwsS3KeyId() != null && !verifyOrgIdResponse.getAwsS3KeyId().isEmpty()) {
                    Log.e("TAG", "awsS3KeyId--> " + verifyOrgIdResponse.getAwsS3KeyId());
                    com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeyId", verifyOrgIdResponse.getAwsS3KeyId());
                } else {
                    com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeyId", "");
                }

                if (verifyOrgIdResponse.getAwsS3KeySecret() != null && !verifyOrgIdResponse.getAwsS3KeySecret().isEmpty()) {
                    Log.e("TAG", "awsS3KeySecret--> " + verifyOrgIdResponse.getAwsS3KeySecret());
                    com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeySecret", verifyOrgIdResponse.getAwsS3KeySecret());
                } else {
                    com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeySecret", "");
                }

                oustLoginA(signInRequest);
            } else {
                Log.d(TAG, "gotVerifyTanentResponse: error");
                oustLoginCallBack.onError(OustStrings.getString("instutionalempty_message"));
            }
        }
    }

    public String getMD5EncodedString(String encryptString) throws NoSuchAlgorithmException {
        String encodedString = "";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptString.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }


        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        encodedString = hexString.toString();
        return encodedString;
    }

    public void oustLoginA(SignInRequest signInRequest) {
        Log.d(TAG, "oustLoginA: ");
        try {
            String signInUrl = OustSdkApplication.getContext().getResources().getString(R.string.signinV3);
            signInUrl = HttpManager.getAbsoluteUrl(signInUrl);

            final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(signInRequest);
            final SignInResponse[] signInResponse = new SignInResponse[1];
            Log.d(TAG, "oustLoginA: " + jsonParams);

            ApiCallUtils.doNetworkCallWithoutAuth(Request.Method.POST, signInUrl, OustSdkTools.getRequestObjectWithPreference(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiCallUtils.setIsLoggedOut(false);
                    Log.d(TAG, "islogout sdk: false:");
                    signInResponse[0] = gson.fromJson(response.toString(), SignInResponse.class);
                    oustLoginProcessOverA(signInResponse[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    oustLoginProcessOverA(signInResponse[0]);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLoginProcessOverA(SignInResponse signInResponse1) {
        this.signInResponse = signInResponse1;
        if (signInResponse != null) {
            if (!TextUtils.isEmpty(signInResponse.getAuthToken()))
                OustPreferences.save("authToken", signInResponse.getAuthToken());
            if (signInResponse.isSuccess()) {
                if ((signInResponse.getStudentid() != null) && (!signInResponse.getStudentid().isEmpty())) {
                    signInResponse.setLoginType(LoginType.Oust.name());
                    OustSdkTools.initServerWithNewEndPoints(signInResponse.getApiServerEndpoint(), signInResponse.getFirebaseEndpoint(), signInResponse.getFirebaseAppId(), signInResponse.getFirebaseAPIKey());
                    if ((signInResponse.getFirebaseToken() != null) && (!signInResponse.getFirebaseToken().isEmpty())) {
                        authenticateWithFirebase(signInResponse.getFirebaseToken());
                    }
                }
            } else {
                setresisterUserRequest();
            }
        }
    }

    public void setCheckUserRequest() {
        if (!OustSdkTools.checkInternetStatus()) {
            oustLoginCallBack.onNetworkError();
        }
        CheckUserRequestData checkUserRequestData = new CheckUserRequestData();
        checkUserRequestData.setC_userIdentifier(userName);
        checkUserRequestData.setOrgId(orgId);
        oustLogin(checkUserRequestData);
    }


    private void oustLogin(CheckUserRequestData checkUserRequestData) {
        String checkuserrequest_url = OustSdkApplication.getContext().getResources().getString(R.string.checkuserrequest_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(checkUserRequestData);
        try {
            checkuserrequest_url = HttpManager.getAbsoluteUrl(checkuserrequest_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, checkuserrequest_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    LevelOneAuthCheckResponseData levelOneAuthCheckResponseData = gson.fromJson(response.toString(), LevelOneAuthCheckResponseData.class);
                    oustLoginProcessOver(levelOneAuthCheckResponseData);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    oustLoginCallBack.onError("Oust Login error");
                    oustLoginProcessOver(null);
                }
            });
        } catch (Exception e) {
            oustLoginCallBack.onError("Oust Login error");
            oustLoginProcessOver(null);
        }
    }

    public void oustLoginProcessOver(LevelOneAuthCheckResponseData levelOneAuthCheckResponseData) {
        if (levelOneAuthCheckResponseData != null) {
            if (levelOneAuthCheckResponseData.isSuccess()) {
                //initlize end points
                setTanentID(orgId);
                OustSdkTools.initServerWithNewEndPoints(levelOneAuthCheckResponseData.getApiServerEndpoint(), levelOneAuthCheckResponseData.getFirebaseEndpoint(), levelOneAuthCheckResponseData.getFirebaseAppId(), levelOneAuthCheckResponseData.getFirebaseAPIKey());
                if (levelOneAuthCheckResponseData.isUserExists()) {
                    saveUserRole(levelOneAuthCheckResponseData.getRole());
                    saveUserRole(levelOneAuthCheckResponseData.getRole());
                    if ((levelOneAuthCheckResponseData.getFirebaseToken() != null) && (!levelOneAuthCheckResponseData.getFirebaseToken().isEmpty())) {
                        OustSdkTools.initServerWithNewEndPoints(levelOneAuthCheckResponseData.getApiServerEndpoint(), levelOneAuthCheckResponseData.getFirebaseEndpoint(), levelOneAuthCheckResponseData.getFirebaseAppId(), levelOneAuthCheckResponseData.getFirebaseAPIKey());
                        OustStaticVariableHandling.getInstance().setEnterpriseUser(true);
                        getSignResponceForLevelOne(levelOneAuthCheckResponseData);
                        authenticateWithFirebase(levelOneAuthCheckResponseData.getFirebaseToken());
                    } else {
                        oustLoginCallBack.onLoginError("Something went wrong!!");
                    }
                } else {
                    if (levelOneAuthCheckResponseData.isTrustedTenant()) {
                        setresisterUserRequest();
                    } else {
                        oustLoginCallBack.onLoginError("Not trusted tanent.");
                    }
                }
            } else {
                if (levelOneAuthCheckResponseData.getPopup() != null) {
                    Popup popup = levelOneAuthCheckResponseData.getPopup();
                    oustLoginCallBack.onLoginError(popup.getContent());
                } else if ((levelOneAuthCheckResponseData.getError() != null) && (!levelOneAuthCheckResponseData.getError().isEmpty())) {
                    oustLoginCallBack.onLoginError(levelOneAuthCheckResponseData.getError());
                } else {
                    oustLoginCallBack.onLoginError("Internal server error");
                }
            }
        } else {
            oustLoginCallBack.onLoginError(OustStrings.getString("networkfail_msg"));
        }
    }

    private void getSignResponceForLevelOne(LevelOneAuthCheckResponseData levelOneAuthCheckResponseData) {
        try {
            signInResponse = new SignInResponse();
            signInResponse.setApiServerEndpoint(levelOneAuthCheckResponseData.getApiServerEndpoint());
            signInResponse.setFirebaseEndpoint(levelOneAuthCheckResponseData.getFirebaseEndpoint());
            signInResponse.setFirebaseToken(levelOneAuthCheckResponseData.getFirebaseToken());
            signInResponse.setStudentid(levelOneAuthCheckResponseData.getUserId());
            signInResponse.setStudentKey(("" + levelOneAuthCheckResponseData.getUserKey()));
            signInResponse.setLoginType(LoginType.Oust.name());
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setresisterUserRequest() {
        try {
            RegisterRequestData registerRequestData = new RegisterRequestData();
//            registerRequestData.setPhone(userName);
//            registerRequestData.setOrgId(orgId);
            registerRequestData.setStudentid(userName);
            if ((OustPreferences.get("gcmToken") != null)) {
                registerRequestData.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            registerUser(registerRequestData);
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void registerUser(RegisterRequestData registerRequestData) {
        String register_url = OustSdkApplication.getContext().getResources().getString(R.string.register_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(registerRequestData);
        try {
            register_url = HttpManager.getAbsoluteUrl(register_url);

            ApiCallUtils.doNetworkCallWithoutAuth(Request.Method.POST, register_url, OustSdkTools.getRequestObjectWithPreference(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiCallUtils.setIsLoggedOut(false);
                    Log.d(TAG, "islogout sdkS: false:");
                    Gson gson = new Gson();
                    SignInResponse signInResponse = gson.fromJson(response.toString(), SignInResponse.class);
                    gotRegisterResponceData(signInResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    oustLoginCallBack.onError("Register user error");
                    oustLoginProcessOver(null);
                }
            });
        } catch (Exception e) {
            oustLoginCallBack.onError("Register user error");
            oustLoginProcessOver(null);
        }
    }

    public void gotRegisterResponceData(SignInResponse signInResponse1) {
        this.signInResponse = signInResponse1;
        if (signInResponse != null) {
            if (signInResponse.isSuccess()) {
                if (!TextUtils.isEmpty(signInResponse.getAuthToken()))
                    OustPreferences.save("authToken", signInResponse.getAuthToken());
                if ((signInResponse.getStudentid() != null) && (!signInResponse.getStudentid().isEmpty())) {
                    OustSdkTools.initServerWithNewEndPoints(signInResponse.getApiServerEndpoint(), signInResponse.getFirebaseEndpoint(), signInResponse.getFirebaseAppId(), signInResponse.getFirebaseAPIKey());
                    setTanentID(orgId);
                    if ((signInResponse.getFirebaseToken() != null) && (!signInResponse.getFirebaseToken().isEmpty())) {
                        OustStaticVariableHandling.getInstance().setEnterpriseUser(true);
                        authenticateWithFirebase(signInResponse.getFirebaseToken());
                    } else {
                        OustSdkTools.showToast("Something went wrong!!");
                    }
                } else {
                    OustSdkTools.showToast("Something went wrong!!");
                }
            } else {
                if (signInResponse.getPopup() != null) {
                    Popup popup = signInResponse.getPopup();
                    oustLoginCallBack.onLoginError(popup.getContent());
                } else if ((signInResponse.getError() != null) && (!signInResponse.getError().isEmpty())) {
                    oustLoginCallBack.onLoginError(signInResponse.getError());
                } else {
                    oustLoginCallBack.onLoginError("Internal server error");
                }
            }
        } else {
            oustLoginCallBack.onLoginError(OustStrings.getString("networkfail_msg"));
        }
    }


    //    firebase authentication

    public void authenticateWithFirebase(String token) {
        try {
            Log.d(TAG, "authenticateWithFirebase: ");
            OustPreferences.saveAppInstallVariable("isSplashUpdate", false);
            String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
            downloadImage(orgId, imagePath1, "oustlearn_" + (orgId.toUpperCase()) + "splashScreen", true);

            String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
            downloadImage(orgId, imagePath2, "oustlearn_" + (orgId.toUpperCase()) + "splashIcon", false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            OustPreferences.save("firebaseToken", token);
            if (token != null && !token.isEmpty()) {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    Log.e("firebase app name", firebaseApp.getName());
                    mAuth = FirebaseAuth.getInstance(firebaseApp);
                } else {
                    mAuth = FirebaseAuth.getInstance();
                }
                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task != null) {
                                    if (!task.isSuccessful()) {
                                        oustLoginCallBack.onError("Firebase authentication error");
                                        if (isActivityActive) {
                                            firebaseAuntheticationFailed();
                                        }
                                    } else {
                                        gotoLandingPage(signInResponse);
                                    }
                                } else {
                                    oustLoginCallBack.onError("Firebase authentication error");
                                    if (isActivityActive) {
                                        firebaseAuntheticationFailed();
                                    }
                                }
                            }
                        });
            } else {
                oustLoginCallBack.onError("Firebase token is not found");
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Firebase authentication error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void firebaseAuntheticationFailed() {
        try {
            oustLoginCallBack.onError("Failed to auntheticate with firebase");
        } catch (Exception e) {
            oustLoginCallBack.onError("Failed to auntheticate with firebase");
        }
    }


    //    save user data in shared prefrence for future use
    public void setTanentID(String tanentId) {
        try {
            OustPreferences.save("tanentid", tanentId);
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void saveUserRole(String role) {
        OustPreferences.save("userRole", role);
    }


    public void gotoLandingPage(SignInResponse signInResponse) {
        try {
            Gson gson = new GsonBuilder().create();
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getActiveUser(signInResponse)));
            OustPreferences.save("isLoggedIn", "true");
            OustPreferences.save("loginType", LoginType.Oust.toString());
            OustPreferences.save("test_userid", userName);
            OustPreferences.save("test_orgid", orgId);
            OustPreferences.save("toolbarColorCode", toolbarcolor);

            if (oustNotificationData != null) {
                startNotification();
            } else {
                OustAppState.getInstance().setLandingPageOpen(false);
                try {
                    unRegisterReceiver();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                isActivityActive = false;
                layout_toolbarcolor = toolbarcolor;
                layout_signInResponse = signInResponse;
                checkLayoutOpenLandingPage();
            }
        } catch (Exception e) {
            oustLoginCallBack.onError("Failed to launch Landing Page");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String layout_toolbarcolor;
    private SignInResponse layout_signInResponse;

    private void checkLayoutOpenLandingPage() {
        try {
            final String message = "system/appConfig/layoutInfo/LAYOUT_4";
            Log.e(TAG, "checkLayoutOpenLandingPage: " + message);
            ValueEventListener layoutDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        openLandingPage(true);
                    } else {
                        openLandingPage(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    openLandingPage(false);
                }
            };

            OustFirebaseTools.getRootRef().child(message).addValueEventListener(layoutDataListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(layoutDataListener, message));
            OustFirebaseTools.getRootRef().child(message).keepSynced(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkAWS(String orgId) {
        //TODO: handle aws key
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        //String awsKeyId = "";
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        //String awsSecretKeyId = "";
        if (awsKeyId == null || awsKeyId.isEmpty() || awsSecretKeyId == null || awsSecretKeyId.isEmpty()) {
            try {
                if (orgId != null && !orgId.isEmpty()) {
                    String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyOrgId);
                    verifyOrgUrl = verifyOrgUrl.replace("{orgId}", orgId);
                    verifyOrgUrl = verifyOrgUrl + "?devicePlatformName=Android";
                    verifyOrgUrl = HttpManager.getAbsoluteUrl(verifyOrgUrl);
                    //enableDisableEditText(false);
                    ApiCallUtils.doNetworkCall(Request.Method.PUT, verifyOrgUrl,
                            OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.optBoolean("success")) {
                                            Gson gson = new Gson();
                                            VerifyOrgIdResponseA verifyOrgIdResponse = gson.fromJson(response.toString(), VerifyOrgIdResponseA.class);
                                            if (verifyOrgIdResponse != null) {
                                                if (verifyOrgIdResponse.getS3_base_end() != null && !verifyOrgIdResponse.getS3_base_end().isEmpty()) {
                                                    AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL = verifyOrgIdResponse.getS3_base_end() + verifyOrgIdResponse.getImg_bucket_name() + "/";
                                                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, verifyOrgIdResponse.getS3_base_end() + verifyOrgIdResponse.getImg_bucket_name() + "/");
                                                } else {
                                                    String s3baseend = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL;
                                                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, s3baseend);
                                                }

                                                if (verifyOrgIdResponse.getS3_Bucket_Region() != null && !verifyOrgIdResponse.getS3_Bucket_Region().isEmpty()) {
                                                    AppConstants.MediaURLConstants.BUCKET_REGION = verifyOrgIdResponse.getS3_Bucket_Region();
                                                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, verifyOrgIdResponse.getS3_Bucket_Region());
                                                } else {
                                                    String s3BktRegion = AppConstants.MediaURLConstants.BUCKET_REGION;
                                                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, s3BktRegion);
                                                }

                                                if (verifyOrgIdResponse.getHttp_img_bucket_cdn() != null && !verifyOrgIdResponse.getHttp_img_bucket_cdn().isEmpty()) {
                                                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH = verifyOrgIdResponse.getHttp_img_bucket_cdn();
                                                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, verifyOrgIdResponse.getHttp_img_bucket_cdn());
                                                } else {
                                                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
                                                }

                                                if (verifyOrgIdResponse.getImg_bucket_cdn() != null && !verifyOrgIdResponse.getImg_bucket_cdn().isEmpty()) {
                                                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS = verifyOrgIdResponse.getImg_bucket_cdn();
                                                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, verifyOrgIdResponse.getImg_bucket_cdn());
                                                } else {
                                                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
                                                }

                                                if (verifyOrgIdResponse.getImg_bucket_name() != null && !verifyOrgIdResponse.getImg_bucket_name().isEmpty()) {
                                                    S3_BUCKET_NAME = verifyOrgIdResponse.getImg_bucket_name();
                                                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, verifyOrgIdResponse.getImg_bucket_name());
                                                } else {
                                                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, S3_BUCKET_NAME);
                                                }

                                                if (verifyOrgIdResponse.getWebAppLink() != null && !verifyOrgIdResponse.getWebAppLink().isEmpty()) {
                                                    Log.e("TAG", "onResponse: webAppLink:" + verifyOrgIdResponse.getWebAppLink());
                                                    OustPreferences.save("webAppLink", verifyOrgIdResponse.getWebAppLink());
                                                }

                                                if (verifyOrgIdResponse.getAwsS3KeyId() != null && !verifyOrgIdResponse.getAwsS3KeyId().isEmpty()) {
                                                    OustPreferences.save("awsS3KeyId", verifyOrgIdResponse.getAwsS3KeyId());
                                                } else {
                                                    OustPreferences.save("awsS3KeyId", "");
                                                }

                                                if (verifyOrgIdResponse.getAwsS3KeySecret() != null && !verifyOrgIdResponse.getAwsS3KeySecret().isEmpty()) {
                                                    OustPreferences.save("awsS3KeySecret", verifyOrgIdResponse.getAwsS3KeySecret());
                                                } else {
                                                    OustPreferences.save("awsS3KeySecret", "");
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Log.e("TAG", "volleyError-->" + volleyError.getMessage());
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } else {
            Log.d(TAG, "checkAWS: already found");
        }
    }

    private void openLandingPage(boolean isLayout4) {
        try {
            checkAWS(orgId);
            Intent intent;
            Gson gson = new GsonBuilder().create();

            if (isLayout4) {
                intent = new Intent(activity, LandingActivity.class);
            } else {
                intent = new Intent(activity, NewLandingActivity.class);
            }

            if (layout_signInResponse != null) {
                intent.putExtra("ActiveUser", gson.toJson(OustSdkTools.getInstance().getActiveUser(layout_signInResponse)));
            }
            if (layout_toolbarcolor != null && !layout_toolbarcolor.isEmpty()) {
                intent.putExtra("toolbarcolor", layout_toolbarcolor);
            }

            if (userName != null && !userName.isEmpty()) {
                OustPreferences.save("test_userid", userName);
            }
            if (orgId != null && !orgId.isEmpty()) {
                OustPreferences.save("test_orgid", orgId);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            OustSdkTools.newActivityAnimation(intent, activity);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //====================================================================================================
    //download all resourses for app
    private List<LanguageClass> updateLanguageClasses = new ArrayList<>();
    private float totalResources = 0;
    private float resourcesDownloaded = 0;

    private void downloadAllResourses() {
        if (RoomHelper.getResourceClassCount() == 0) {
            addResourcesToRealm();
        }
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            Log.d(TAG, "totalResources: count " + totalResources);
            Log.d(TAG, "resourcesDownloaded: count " + resourcesDownloaded);
            Log.d(TAG, "totalImageResourceSize: count " + totalImageResourceSize);
            Log.d(TAG, "totalAudioResourceSize: count " + totalAudioResourceSize);
            Log.d(TAG, "totalFontResourceSize: count " + totalFontResourceSize);
            Log.d(TAG, "totalDownloadedResources: count " + totalDownloadedResources);
            Log.d(TAG, "downloadStartzedResources: count " + downloadStartedResources);
            Log.d(TAG, "totalResourceTobeDownloaded: count " + totalResourceTobeDownloaded);

            startDownloadingResourses2();
        } else {
            initFirebase();
        }
    }


    private void addResourcesToRealm() {
        EntityResourceCollection audioCollModel = new EntityResourceCollection();
        audioCollModel.setId(1);
        audioCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(audioCollModel);

        EntityResourceCollection imageCollModel = new EntityResourceCollection();
        imageCollModel.setId(2);
        imageCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(imageCollModel);

        EntityResourceCollection fontCollModel = new EntityResourceCollection();
        audioCollModel.setId(3);
        audioCollModel.setTimeStamp(0);
        RoomHelper.addorUpdateResourceCollectionModel(fontCollModel);
    }

    public void startDownloadingResourses2() {
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {

            OustPreferences.saveAppInstallVariable("isDownloadedImageLastTime", true);
            LanguageClass languageClass = new LanguageClass();
            languageClass.setFileName("englishStr.properties");
            languageClass.setIndex(0);
            languageClass.setLanguagePerfix("en");
            languageClass.setName("English");

            LanguagesClasses classes = new LanguagesClasses();
            List<LanguageClass> languageClasses = new ArrayList<>();
            languageClasses.add(languageClass);
            classes.setLanguageClasses(languageClasses);
            Gson gson = new Gson();
            String langStr = gson.toJson(classes);
            OustPreferences.save("alllanguage", langStr);


            //downLoad(languageClass, languageClass.getFileName(), 0);
            loadLanResFromResources("myname_properties.tmp", languageClass, languageClass.getFileName(), 0);

            //downloadStrategyOfResource();
        } else {
//            checkForResourceFileUpdate();
            checkLoginProcess2();
        }
    }

    private int totalImageResourceSize = 0;
    private int totalAudioResourceSize = 0;
    private int totalFontResourceSize = 0;
    private int totalDownloadedResources = 0;
    private int downloadStartedResources = 0;
    private int totalResourceTobeDownloaded = 0;


    private void loadLanResFromResources(String filename, final LanguageClass languageClass, String fileName1, final int languageIndex) {
        try {
            final File file = new File(activity.getFilesDir(), filename);
            String rawFilename = filename.split("\\.")[0];
            if ((file != null) && (!file.exists())) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    //com.oustme.oustsdk
                    int resId = activity.getResources().getIdentifier("raw/" + rawFilename, null, activity.getPackageName());
                    Log.d("InitLanRes", "loadLanResFromResources: " + rawFilename + " -- resid:" + resId);
                    in = activity.getResources().openRawResource(resId);
                    out = new FileOutputStream(file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("NewInitRes", "Failed to copy asset file: " + filename, e);
                }
            }
            createFileAndSave2(languageClass, languageIndex, file);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //showDownloadProgress();
            calculateResourceSize();
        }
        //showDownloadProgress();
        Log.e("NewInitRes", "copied raw file: " + filename);
    }

    public void createFileAndSave2(LanguageClass languageClass, int languageIndex, File file) {
        try {
            if (file != null && file.exists()) {
                Properties prop = new Properties();
                FileInputStream inputStream = new FileInputStream(file);
                if (inputStream != null) {
                    prop.load(inputStream);
                }
                Map<String, String> map = new HashMap<>();
                for (Map.Entry<Object, Object> x : prop.entrySet()) {
                    map.put((String) x.getKey(), (String) x.getValue());
                }
                Gson gson = new Gson();
                String hashmapStr = gson.toJson(map);
                EntityResourseStrings resourseStringsModel = new EntityResourseStrings();
                resourseStringsModel.setLanguagePerfix(languageClass.getLanguagePerfix());
                resourseStringsModel.setIndex(languageClass.getIndex());
                resourseStringsModel.setName(languageClass.getName());
                resourseStringsModel.setHashmapStr(hashmapStr);
                RoomHelper.addorUpdateResourceStringModel(resourseStringsModel);
                //file.delete();

                Log.d(TAG, "createFileAndSave: " + file.getAbsolutePath());
            }

            //showDownloadProgress();
            calculateResourceSize();
        } catch (Exception e) {
            //sendErrorToOustCallback("Something went wrong");
            //oustLoginCallBack.onError("Something went wrong!!");
            Log.e(TAG, "save language file" + e.getMessage());
            //showDownloadProgress();
            calculateResourceSize();
        }
    }

    private void calculateResourceSize() {
        try {
            OustStrings.init();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Log.d(TAG, "calculateResourceSize: ");
        totalImageResourceSize = (activity.getResources().getStringArray(R.array.all_images)).length;
        totalResourceTobeDownloaded += totalImageResourceSize;
        totalResources += totalImageResourceSize;

        totalAudioResourceSize = (activity.getResources().getStringArray(R.array.sounds)).length;
        totalResourceTobeDownloaded += totalAudioResourceSize;
        totalResources += totalAudioResourceSize;

        totalFontResourceSize = (activity.getResources().getStringArray(R.array.fonts)).length;
        totalResourceTobeDownloaded += totalFontResourceSize;
        totalResources += totalFontResourceSize;

        newDownloadResource();
        //downloadStrategyOfResource();
    }

    private void newDownloadResource() {
        try {
            String[] audioResList = activity.getResources().getStringArray(R.array.sounds);
            String[] imageResList = activity.getResources().getStringArray(R.array.all_images);
            String[] fontResList = activity.getResources().getStringArray(R.array.fonts);
            for (int i = 0; i < imageResList.length; i++) {
                loadInitResFromResources(imageResList[i]);
            }
            for (int i = 0; i < audioResList.length; i++) {
                loadInitResFromResources(audioResList[i]);
            }
            for (int i = 0; i < fontResList.length; i++) {
                loadInitResFromResources(fontResList[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadInitResFromResources(String filename) {
        try {
            final File file = new File(activity.getFilesDir(), filename);
            String rawFilename = filename.split("\\.")[0];
            if ((file != null) && (!file.exists())) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    //com.oustme.oustsdk
                    int resId = activity.getResources().getIdentifier("raw/" + rawFilename, null, activity.getPackageName());
                    Log.d("NewInitRes", "loadInitResFromResources: " + rawFilename + " -- resid:" + resId);
                    in = activity.getResources().openRawResource(resId);
                    out = new FileOutputStream(file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("NewInitRes", "Failed to copy asset file: " + filename, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        showDownloadProgress2();
        //checkLoginProcess2();
        Log.e("NewInitRes", "copied raw file: " + filename);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showDownloadProgress2() {
        Log.d(TAG, "showDownloadProgress: ");
        resourcesDownloaded++;
        if (totalResources > 0) {
            float progress = ((float) (resourcesDownloaded / totalResources));
            int progress1 = (int) (progress * 100);
            if (progress1 > 100) {
                progress1 = 100;
            }
            Log.d(TAG, "onProgressChanged: count " + resourcesDownloaded + "/" + totalResources);
            Log.d(TAG, "onProgressChanged: % " + progress1);
            oustLoginCallBack.onProgressChanged(progress1);
            if (resourcesDownloaded >= (totalResources)) {
                //oustLoginCallBack.onProgressChanged(100);
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                checkLoginProcess2();
            }
        } else {
            oustLoginCallBack.onProgressChanged(100);
            OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
            checkLoginProcess2();
        }
    }

    private void downloadStrategyOfResource() {
        try {
            String[] audioResList = activity.getResources().getStringArray(R.array.sounds);
            String[] imageResList = activity.getResources().getStringArray(R.array.all_images);
            String[] fontResList = activity.getResources().getStringArray(R.array.fonts);

            ArrayList<String> urlList = new ArrayList();

            /*String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
            String imageUrl1 = CLOUD_FRONT_BASE_PATH + imagePath1;
            urlList.add(imageUrl1);

            String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
            String imageUrl2 = CLOUD_FRONT_BASE_PATH + imagePath2;
            urlList.add(imageUrl2);*/

            for (String audioRes : audioResList) {
                String audioPath = "AppResources/Android/All/Audios/";
                String url = CLOUD_FRONT_BASE_PATH + audioPath + audioRes;
                urlList.add(url);
            }

            for (String imageRes : imageResList) {
                String imagePath = "AppResources/Android/All/Images/";
                String url = CLOUD_FRONT_BASE_PATH + imagePath + imageRes;
                urlList.add(url);
            }

            for (String fontRes : fontResList) {
                String fontPath = "AppResources/Android/All/Fonts/";
                String url = CLOUD_FRONT_BASE_PATH + fontPath + fontRes;
                urlList.add(url);
            }

            ArrayList<String> fileNameList = new ArrayList();
            for (String fileUrl : urlList) {
                String fileName = OustMediaTools.getMediaFileName(fileUrl);
                fileNameList.add(fileName);
            }

            com.oustme.oustsdk.downloadmanger.newimpl.DownloadFilesIntentService.startFileDownload(OustSdkApplication.getContext(), urlList, fileNameList, false, true, new DownloadResultReceiver(new Handler()));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*public void downLoad(final LanguageClass languageClass, String fileName1, final int languageIndex) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                //         checkLoginProcess();
                return;
            }

            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = "languagePacks/mobile/" + fileName1;
            final File file = getTempFile();
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            createFileAndSave(languageClass, languageIndex, file);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        downloadResurceFailed();
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            downloadResurceFailed();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public File getTempFile() {
        File file;
        try {
            String fileName = "myname.properties";
            file = File.createTempFile(fileName, null, activity.getCacheDir());
            return file;
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }


    public void createFileAndSave(LanguageClass languageClass, int languageIndex, File file) {
        try {
            Properties prop = new Properties();
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                prop.load(inputStream);
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<Object, Object> x : prop.entrySet()) {
                map.put((String) x.getKey(), (String) x.getValue());
            }
            Gson gson = new Gson();
            String hashmapStr = gson.toJson(map);
            EntityResourseStrings resourseStringsModel = new EntityResourseStrings();
            resourseStringsModel.setLanguagePerfix(languageClass.getLanguagePerfix());
            resourseStringsModel.setIndex(languageClass.getIndex());
            resourseStringsModel.setName(languageClass.getName());
            resourseStringsModel.setHashmapStr(hashmapStr);
            RoomHelper.addorUpdateResourceStringModel(resourseStringsModel);
            file.delete();
            OustStrings.init();
//            showDownloadProgress();
        } catch (Exception e) {
            oustLoginCallBack.onError("Something went wrong!!");
            Log.e(TAG, "save language file" + e.getMessage());
        }
    }

    //=====================================================
//update all downloaded resources

    public void showDownloadProgress() {
        Log.d(TAG, "showDownloadProgress: ");
        resourcesDownloaded++;
        if (totalResources > 0) {
            float progress = ((float) (resourcesDownloaded / totalResources));
            int progress1 = (int) (progress * 100);
            if (progress1 > 100) {
                progress1 = 100;
            }
            Log.d(TAG, "onProgressChanged: count " + resourcesDownloaded + "/" + totalResources);
            Log.d(TAG, "onProgressChanged: % " + progress1);
            oustLoginCallBack.onProgressChanged(progress1);
            if (resourcesDownloaded >= (totalResources)) {
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                checkLoginProcess();
            } else {
                downloadStrategyOfResource();
            }
        } else {
            checkLoginProcess();
        }
    }

    private boolean resourceDownloadFailed = false;

    private void downloadResurceFailed() {
        if (!resourceDownloadFailed) {
            oustLoginCallBack.onError("Resource download error");
            resourceDownloadFailed = true;
        }
    }

    //String urlForBaseURLCheck = AppConstants.StringConstants.SIGN_URL_BASE;
    String urlForBaseURLCheck = "";

    public class CheckBaseURL extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                // Creating & connection Connection with url and required Header.
                URL url = new URL(urlForBaseURLCheck);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("org-id", orgId);
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.setRequestMethod("PUT");   //POST or GET
                urlConnection.connect();

                // Create JSONObject Request
                JSONObject jsonRequest = new JSONObject();
                //jsonRequest.put("username", "user.name");
                //jsonRequest.put("password", "pass@123");

                // Check the connection status.
                int statusCode = urlConnection.getResponseCode();
                String statusMsg = urlConnection.getResponseMessage();

                // Connection success. Proceed to fetch the response.
                Map<String, List<String>> hdrs = urlConnection.getHeaderFields();
                Set<String> hdrKeys = hdrs.keySet();

                if (statusCode == 405) {
                    return "405";
                } else if (statusCode == 301) {
                    String baseUERL = urlConnection.getHeaderField("Location");
                    return baseUERL;
                } else if (statusCode == 200) {
                    InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder dta = new StringBuilder();
                    String chunks;
                    while ((chunks = buff.readLine()) != null) {
                        dta.append(chunks);
                    }
                    String returndata = dta.toString();

                    return returndata;
                } else {
                    //Handle else case
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (IOException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute BaseURL: " + result);
            if (result != null && result.equalsIgnoreCase("405")) {
                //  new CheckBaseURL().execute(urlForBaseURLCheck);
            } else if (result != null) {
                String delimeter = "services/";
                String[] parts = result.split(delimeter, 2);
                String BASE_URL = parts[0] + "" + delimeter;
                OustPreferences.save(AppConstants.StringConstants.BASE_URL_FROM_API, BASE_URL);
                verifyTenant(orgId);
            }
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private void setReceiver() {
        if (activity == null)
            return;
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        OustSdkApplication.getContext().registerReceiver(myFileDownLoadReceiver, intentFilter);
        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        Log.d(TAG, "unRegisterReceiver: ");
        try {
            if (activity != null && myFileDownLoadReceiver != null)
                OustSdkApplication.getContext().unregisterReceiver(myFileDownLoadReceiver);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, e.getLocalizedMessage());
            Log.d(TAG, e.toString());
        }
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            showDownloadProgress();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            // mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    private class DownloadResultReceiver extends ResultReceiver {
        public DownloadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 1:
                    Log.d(TAG, "onReceiveResult: completed:" + resultData.getString("MSG"));
                    oustLoginCallBack.onProgressChanged(100);
                    OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                    checkLoginProcess2();
                    break;

                case 2:
                    //Log.d(TAG, "onReceiveResult: progress:"+resultData.getString("MSG"));
                    String percentageStr = resultData.getString("MSG");
                    if (!TextUtils.isEmpty(percentageStr))
                        oustLoginCallBack.onProgressChanged(Integer.parseInt(percentageStr));
                    break;

                case 3:
                    Log.d(TAG, "onReceiveResult: Error:" + resultData.getString("MSG"));

                    break;
            }
            super.

                    onReceiveResult(resultCode, resultData);
        }

    }
}