package com.oustme.oustsdk.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.NewLandingActivity;
import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
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
import com.oustme.oustsdk.request.SignInRequest;
import com.oustme.oustsdk.request.UserUpdate;
import com.oustme.oustsdk.request.VerifyAndSignInRequest;
import com.oustme.oustsdk.response.common.LanguageClass;
import com.oustme.oustsdk.response.common.LanguagesClasses;
import com.oustme.oustsdk.response.common.LevelOneAuthCheckResponseData;
import com.oustme.oustsdk.response.common.LoginType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.response.common.VerifyAndSignInResponse;
import com.oustme.oustsdk.room.EntityResourceCollection;
import com.oustme.oustsdk.room.EntityResourseStrings;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.ResourceCollectionModel;
import com.oustme.oustsdk.room.dto.ResourseStringsModel;
import com.oustme.oustsdk.service.login.DownloadInitListener;
import com.oustme.oustsdk.service.login.DownloadInitService;
import com.oustme.oustsdk.service.login.OustLoginApiCallBack;
import com.oustme.oustsdk.service.login.OustLoginService;
import com.oustme.oustsdk.response.common.VerifyOrgIdResponseA;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NotificationData;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.apache.commons.io.FileUtils;
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
import java.io.StringReader;
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

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.STUDE_KEY;

/**
 * This is OustNewLauncher class to launch oust
 * It checks for all the validations
 */

public class OustNewLauncher implements OustLoginApiCallBack,DownloadInitListener {
    private String userName = null;
    private String fName = null;
    private String lName = null;
    private String password = null;
    private String language = null;
    private String orgId = null;
    private String groupId = null;
    private String myAPIKey;
    private String token, serverKey;
    private Activity activity;
    private Context context;
    private OustAppConfigData oustAppConfigData;
    OustAuthData oustAuthData;
    private OustNotificationConfig notificationConfig;
    private PushNotificationType pushNotificationType;
    private OustLoginCallBack oustLoginCallBack;
    private OustApiListener oustApiListener;
    private boolean isApiCall=false, isStopLogin = false;

    //private DownloadFiles downloadFiles;
    private Map<String, String> oustNotificationData = null;
    private OustModuleCallBack oustModuleCallBack;

    private static final String TAG = "OustNewLauncher";

    private static OustNewLauncher OustNewLauncher = null;

    private OustNewLauncher() {
    }

    public OustModuleCallBack getOustModuleCallBack() {
        return oustModuleCallBack;
    }

    public void setOustModuleCallBack(OustModuleCallBack oustModuleCallBack) {
        this.oustModuleCallBack = oustModuleCallBack;
    }

    public static OustNewLauncher getInstance() {
        if (OustNewLauncher == null) {
            synchronized (OustNewLauncher.class) {
                if (OustNewLauncher == null)
                    OustNewLauncher = new OustNewLauncher();
            }
        }
        return OustNewLauncher;
    }

    public void addOustModuleCallBack(OustModuleCallBack oustModuleCallBack){
        this.oustModuleCallBack = oustModuleCallBack;
    }

    public void removeOustModulecallBack(){
        this.oustModuleCallBack = null;
    }

    public void launch(Activity activity, OustAuthData oustAuthData, OustLoginCallBack oustLoginCallBack) throws OustException {
        Log.d(TAG, "launch: ");
        this.oustLoginCallBack = oustLoginCallBack;
        this.oustAuthData = oustAuthData;

        this.orgId = oustAuthData.getOrgId();
        this.userName = oustAuthData.getUsername();
        this.fName = oustAuthData.getfName();
        this.lName = oustAuthData.getlName();

        isLoggedInCalled = false;
        launchMe(activity, null, oustAuthData, null);
    }

    public void launch(OustAuthData oustAuthData) throws OustException {
        Log.d(TAG,"Launch");
        launchMe(oustAuthData);
    }

    public void launch(Activity activity, OustAuthData oustAuthData, OustNotificationConfig notificationConfig, OustLoginCallBack oustLoginCallBack) throws OustException {
        this.oustLoginCallBack = oustLoginCallBack;
        isLoggedInCalled = false;
        launchMe(activity, null, oustAuthData, notificationConfig);
    }

    public void launchInit(Activity activity, OustAuthData oustAuthData, OustApiListener oustApiListener) throws OustException {
        Log.d(TAG,"InitResources");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);

        //this.oustApiListener = (OustApiListener) activity;
        isStopLogin = true;

        this.oustApiListener = oustApiListener;
        //setPrevioustAppLanguage();
        launchMe(oustAuthData);
    }

    public void launchInitWithContext(Context context, OustAuthData oustAuthData) throws OustException {
        Log.d(TAG,"InitResources");
        if (context == null) {
            throw new NullActivityException();
        }
        //this.activity = activity;
        this.context = context;
        //OustSdkApplication.setmContext(context);
        //this.oustApiListener = (OustApiListener) activity;
        isStopLogin = true;

        this.oustApiListener = null;
        //setPrevioustAppLanguageWithContext();
        launchMeWithContext(oustAuthData);
    }

    public void launchNotification(Activity activity, OustAuthData oustAuthData, OustLoginCallBack oustLoginCallBack, Map<String, String> oustNotificationData) throws OustException {
        this.oustLoginCallBack = oustLoginCallBack;
        this.oustNotificationData = oustNotificationData;
        launchMe(activity, null, oustAuthData, null);
    }
    //    public void launch(Activity activity, OustAuthData oustAuthData, OustAppConfigData oustAppConfigData) throws OustException {
//        this.oustLoginCallBack=oustLoginCallBack;
//        launchMe(activity,oustAppConfigData,oustAuthData,null);
//    }
//
//    public void launch(Activity activity, OustAuthData oustAuthData, OustAppConfigData oustAppConfigData, OustNotificationConfig notificationConfig) throws OustException{
//        launchMe(activity,oustAppConfigData,oustAuthData,notificationConfig);
//    }

    public void removeResources(Activity activity) throws OustException {
        Log.d(TAG,"RemoveResources");

        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;

        int totalImageResourceSize = (activity.getResources().getStringArray(R.array.all_images)).length;
        int totalResources = totalImageResourceSize;

        int totalAudioResourceSize = (activity.getResources().getStringArray(R.array.sounds)).length;
        totalResources += totalAudioResourceSize;

        int totalFontResourceSize = (activity.getResources().getStringArray(R.array.fonts)).length;
        totalResources += totalFontResourceSize;

        String[] audioResList = activity.getResources().getStringArray(R.array.sounds);
        String[] imageResList = activity.getResources().getStringArray(R.array.all_images);
        String[] fontResList = activity.getResources().getStringArray(R.array.fonts);

        for (int i = 0; i < totalResources; i++) {
            String filename = "";
            if (i < totalImageResourceSize) {
                filename = imageResList[i];
            } else if (i >= totalImageResourceSize && i < totalImageResourceSize + totalAudioResourceSize) {
                filename = audioResList[i - totalImageResourceSize];
            } else if (i >= totalImageResourceSize + totalAudioResourceSize && i < totalResources) {
                filename = fontResList[i - (totalImageResourceSize + totalAudioResourceSize)];
            }

            try {
                final File file = new File(activity.getFilesDir(), filename);
                Log.d(TAG,"FIle:"+file.getAbsolutePath());
                if ((file != null) && file.exists()) {
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", false);
        OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustStaticVariableHandling.getInstance().setDownloadingResources(false);
        OustSdkTools.clearAlldataAndlogout();
        this.oustApiListener.onResourcesRemoved();
    }

    public void removeResourcesWithAppContext(Context context) throws OustException {
        Log.d(TAG,"RemoveResources");

        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        this.oustApiListener = null;

        //this.oustApiListener = (OustApiListener) activity;

        int totalImageResourceSize = (context.getResources().getStringArray(R.array.all_images)).length;
        int totalResources = totalImageResourceSize;

        int totalAudioResourceSize = (context.getResources().getStringArray(R.array.sounds)).length;
        totalResources += totalAudioResourceSize;

        int totalFontResourceSize = (context.getResources().getStringArray(R.array.fonts)).length;
        totalResources += totalFontResourceSize;

        String[] audioResList = context.getResources().getStringArray(R.array.sounds);
        String[] imageResList = context.getResources().getStringArray(R.array.all_images);
        String[] fontResList = context.getResources().getStringArray(R.array.fonts);

        for (int i = 0; i < totalResources; i++) {
            String filename = "";
            if (i < totalImageResourceSize) {
                filename = imageResList[i];
            } else if (i >= totalImageResourceSize && i < totalImageResourceSize + totalAudioResourceSize) {
                filename = audioResList[i - totalImageResourceSize];
            } else if (i >= totalImageResourceSize + totalAudioResourceSize && i < totalResources) {
                filename = fontResList[i - (totalImageResourceSize + totalAudioResourceSize)];
            }

            try {
                final File file = new File(context.getFilesDir(), filename);
                Log.d(TAG,"FIle:"+file.getAbsolutePath());
                if ((file != null) && file.exists()) {
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", false);
        OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustStaticVariableHandling.getInstance().setDownloadingResources(false);
        OustSdkTools.clearAlldataAndlogout();
        //this.oustApiListener.onResourcesRemoved();
    }

    public void oustLogout(Activity activity) throws OustException{
        Log.d(TAG,"OustLogout");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;

        OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustSdkTools.clearAlldataAndlogout();

        this.oustApiListener.onLogoutSuccess();
    }

    public void oustLogoutWithAppContext(Context context) throws OustException{
        Log.d(TAG,"OustLogout");
        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        this.oustApiListener = null;

        //this.oustApiListener = (OustApiListener) activity;

        OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustSdkTools.clearAlldataAndlogout();

        //this.oustApiListener.onLogoutSuccess();
    }

    public void oustLogoutWithContext(Context context, boolean isListenerEnabled) throws OustException{
        Log.d(TAG,"OustLogout");
        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        if(isListenerEnabled) {
            this.oustApiListener = (OustApiListener) context;
        }else{
            this.oustApiListener = null;
        }
        OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustSdkTools.clearAlldataAndlogout();

        if(this.oustApiListener!=null) {
            this.oustApiListener.onLogoutSuccess();
        }
    }

    public void oustLogin(Activity activity, String orgId, String userId) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }

        retry = 0;
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = userId;
        this.orgId = orgId;
        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }
        checkLoginProcess();
        //OustLoginService oustLoginService = new OustLoginService(activity, userName, orgId, true, this);
    }

    public void oustLoginWithContext(Context context, String orgId, String userId, String fName,String lName,boolean isListenerEnabled) throws OustException{
        Log.d(TAG,"OustLogin");
        if (context == null) {
            throw new NullActivityException();
        }

        retry = 0;
        this.context = context;
        OustSdkApplication.setmContext(context);
        if(isListenerEnabled) {
            this.oustApiListener = (OustApiListener) context;
        }else{
            this.oustApiListener = null;
        }
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = userId;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(context);
        }
        loginProcessWithContext();
    }

    public void oustLoginWithLanguage(Activity activity, String orgId, String userId,String fName,String lName, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = userId;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.language = language;
        isApiCall = true;

        if(language==null){
            if(oustApiListener!=null){
                oustApiListener.onLoginError("Language should be mandatory");
            }
            return;
        }
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }
        checkLoginProcess();
        //OustLoginService oustLoginService = new OustLoginService(activity, userName, orgId, true, this);
    }

    public void oustLanguageLoginWithAppContext(Context context, String orgId, String userId,String fName,String lName, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        this.oustApiListener = null;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = userId;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.language = language;
        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(context);
        }
        if(language==null){
            if(oustApiListener!=null) {
                oustApiListener.onLoginError("Language should be mandatory");
            }
            return;
        }
        loginProcessWithContext();
        //OustLoginService oustLoginService = new OustLoginService(activity, userName, orgId, true, this);
    }

    public void oustUpdateLanguage(Activity activity, OustAuthData oustAuthData, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = oustAuthData.getUsername();
        this.orgId = oustAuthData.getOrgId();
        this.language = language;

        if(language==null){
            if(oustApiListener!=null){
                oustApiListener.onError("Language should be mandatory");
            }
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }
        //checkLoginProcess();
        //OustLoginService oustLoginService = new OustLoginService(activity, userName, orgId, true, this);

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            //this.oustApiListener.onError("Under construction");
            updateLanguage(oustAuthData, language);
        }else{
            if(this.oustApiListener!=null) {
                this.oustApiListener.onError("User is not loggedIN");
            }
        }
    }

    public void oustUpdateLanguageWithAppContext(Context context, OustAuthData oustAuthData, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        this.oustApiListener = null;

        //this.oustApiListener = (OustApiListener) context;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = oustAuthData.getUsername();
        this.orgId = oustAuthData.getOrgId();
        this.language = language;

        if(language==null){
            if(oustApiListener!=null){
                oustApiListener.onError("Language should be mandatory");
            }
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(context);
        }
        //checkLoginProcess();
        //OustLoginService oustLoginService = new OustLoginService(activity, userName, orgId, true, this);

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            //this.oustApiListener.onError("Under construction");
            updateLanguage(oustAuthData, language);
        }else{
            if(this.oustApiListener!=null) {
                this.oustApiListener.onError("User is not loggedIN");
            }
        }
    }

    public boolean checkUserAlreadyLoggedIn(Context context) throws  OustException{
        Log.d(TAG, "checkUserAlreadyLoggedIn: ");
        if (context == null) {
            throw new NullActivityException();
        }

        this.context = context;
        this.oustApiListener = (OustApiListener) activity;;

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if (activeUser.getStudentid() != null) {
                    isUserLoggedIn = true;
                }
            }
        }
        Log.d(TAG, "checkUserAlreadyLoggedIn: "+isUserLoggedIn);
        return isUserLoggedIn;
        /*if(oustApiListener!=null){
            oustApiListener.onUserLoggedIn(isUserLoggedIn);
        }*/
    }

    public void updateLanguage(final OustAuthData oustAuthData, String language) {
        Log.d("updateLanguage",""+language);

        String autoDistributeCPL_url = OustSdkApplication.getContext().getResources().getString(R.string.update_language);
        autoDistributeCPL_url = autoDistributeCPL_url.replace("{languageName}",""+language);
        final String eventUrl = HttpManager.getAbsoluteUrl(autoDistributeCPL_url);

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);
        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
        Log.d("updateLanguage", "updateLanguage: "+jsonParams.toString());

        Log.d(TAG, "updateLanguage: "+eventUrl);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateLanguage","Response:"+response.toString());
                if(response!=null && response.optBoolean("success")){
                    if(oustApiListener!=null){
                        oustApiListener.onLanguageUpdated();
                    }
                    //onError("language updated");
                }else{
                    if(oustApiListener!=null) {
                        oustApiListener.onError("Failed to update the language");
                    }
                    //onError("Failed to update the language");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("updateLanguage", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(oustApiListener!=null) {
                    oustApiListener.onError("Failed to update the language");
                }
                //onError("Failed to update the language");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", oustAuthData.getOrgId());
                    //params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("updateLanguage","contentType: "+jsonObjReq.getBodyContentType());
            Log.d("updateLanguage","Headers: "+jsonObjReq.getHeaders().toString());

        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

    }

    public void launchMe(OustAuthData oustAuthData) throws OustException {
        Log.d(TAG,"Launch Me loading");
        if (oustAuthData == null) {
            throw new NullAuthDataException();
        }else {
            if (oustAuthData.getOrgId() != null && !oustAuthData.getOrgId().isEmpty()) {
                if(!OustStaticVariableHandling.getInstance().isDownloadingResources()) {
                    new DownloadInitService(activity, oustAuthData.getOrgId(), this);
                }else{
                    DownloadInitService.setListener(this);
                }
            }else
                throw new NullAuthDataException();
        }
    }

    public void launchMeWithContext(OustAuthData oustAuthData) throws OustException {
        Log.d(TAG,"Launch Me loading with context");
        if (oustAuthData == null) {
            throw new NullAuthDataException();
        }else {
            if (oustAuthData.getOrgId() != null && !oustAuthData.getOrgId().isEmpty()) {
                if(!OustStaticVariableHandling.getInstance().isDownloadingResources()) {
                    new DownloadInitService(context, oustAuthData.getOrgId(), this);
                }else{
                    DownloadInitService.setListener(this);
                }
            }else
                throw new NullAuthDataException();
        }
    }

    public void launchMe(Activity activity, OustAppConfigData oustAppConfigData, OustAuthData oustAuthData, OustNotificationConfig notificationConfig) throws OustException {
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        try {
            this.context = (Context) activity;
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        retry =0;
        OustSdkApplication.setmContext(activity);

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
            if ((oustAuthData.getfName() == null) || (oustAuthData.getfName().isEmpty())) {
                throw new UserNameNotFoundException();
            } else {
                this.fName = oustAuthData.getfName();
            }
            if ((oustAuthData.getlName() == null) || (oustAuthData.getlName().isEmpty())) {
                throw new UserNameNotFoundException();
            } else {
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

        /*if(oustAuthData.getGroupId()!=null && !oustAuthData.getGroupId().isEmpty()){
            this.groupId = oustAuthData.getGroupId();
        }*/

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
        //validateAPIKey();
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

    private String regId = "";
    private String toolbarcolor;
    private String pushnotificationType;
    private int RES_AUDIO = 1, RES_IMAGE = 2, RES_FONT = 3;

    private SignInRequest signInRequest;
    private SignInResponse signInResponse;
    private boolean isActivityActive = true;
    public static Branch branch;
    private FirebaseAuth mAuth;

    private void onSplashStart() {
        Log.d(TAG, "onSplashStart: ");
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }
        OustPreferences.saveAppInstallVariable("isSecreteKeyValidated", true);
        //RoomHelper.setDefaultConfig(activity);
        //setPrevioustAppLanguage();
        firstTimeAuth = false;
        initializeRest();
        try {
            if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                Log.d("firebase app name", firebaseApp.getName());
                mAuth = FirebaseAuth.getInstance(firebaseApp);
            } else {
                mAuth = FirebaseAuth.getInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        downloadAllResourses();
    }

    /*public void setPrevioustAppLanguage() {
        try {
            String selectedlanguageprefix = getSelectedLanguagePrefix();
            if (selectedlanguageprefix == null) {
                selectedlanguageprefix = "en";
            } else if (selectedlanguageprefix.isEmpty()) {
                selectedlanguageprefix = "en";
            }
            setLocale(selectedlanguageprefix);
        } catch (Exception e) {
            *//*if(oustLoginCallBack!=null) {
                oustLoginCallBack.onError("Something went wrong!!");
            }*//*
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPrevioustAppLanguageWithContext() {
        try {
            String selectedlanguageprefix = getSelectedLanguagePrefix();
            if (selectedlanguageprefix == null) {
                selectedlanguageprefix = "en";
            } else if (selectedlanguageprefix.isEmpty()) {
                selectedlanguageprefix = "en";
            }
            setLocaleWithContext(selectedlanguageprefix);
        } catch (Exception e) {
            *//*if(oustLoginCallBack!=null) {
                oustLoginCallBack.onError("Something went wrong!!");
            }*//*
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public String getSelectedLanguagePrefix() {
        return OustPreferences.get("selectedlanguageprefix");
    }

   /* public void setLocale(String selectedlanguageprefix) {
        try {
            Locale locale = new Locale(selectedlanguageprefix);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
            OustStrings.init();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            *//*if(oustLoginCallBack!=null)
                oustLoginCallBack.onError("Something went wrong!!");*//*
        }
    }

    public void setLocaleWithContext(String selectedlanguageprefix) {
        try {
            Locale locale = new Locale(selectedlanguageprefix);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            OustStrings.init();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            *//*if(oustLoginCallBack!=null)
                oustLoginCallBack.onError("Something went wrong!!");*//*
        }
    }*/

    private void initializeRest() {
        initServerEndPoint();
        initlizeBranch();
    }

    public void initServerEndPoint() {
        try {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        } catch (Exception e) {
            /*if(oustLoginCallBack!=null) {
                oustLoginCallBack.onError("Something went wrong!!");
            }*/
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
            /*if(oustLoginCallBack!=null) {
                oustLoginCallBack.onError("Something went wrong!!");
            }*/
        }
    }

    //====================================================================================================
    public void initFirebase() {
        try {
            autheticateWithFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            updateAppResoursesOver();
            if(oustLoginCallBack!=null) {
                oustLoginCallBack.onError("Firebase initialization error");
            }
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
            }
        } else {
            checkLoginProcess();
        }
    }

    /*private void getNewTokenForFireBase() {
        if (OustSdkTools.checkInternetStatus()) {
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData();
                String user_id = activeUser.getStudentid();
                String get_fireBase_token_url = OustSdkApplication.getContext().getResources().getString(R.string.get_fireBase_token_url);
                get_fireBase_token_url = get_fireBase_token_url.replace("{userId}", user_id);
                get_fireBase_token_url = HttpManager.getAbsoluteUrl(get_fireBase_token_url);
                final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, get_fireBase_token_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        gotFireBaseToken(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        gotFireBaseToken(null);
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("api-key", getAPIKey());
                            params.put("org-id", orgId);
                        } catch (OustException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                        return params;
                    }
                };
                jsonObjReq.setShouldCache(false);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
                Log.e("VOLLEY", "Adding request object");
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");


            } else {
//                OustPreferences.clear("userdata");
//                OustPreferences.clear("tanentid");
                getUpdateResoursesData();
            }
        } else {
            checkLoginProcess1();
        }
    }*/

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

    int retry = 0;
    boolean isContext = false;

    private void sendErrorToOustCallback(String error){
        Log.d(TAG, "sendErrorToOustCallback: "+error);
        /*OustDataHandler.getInstance().resetData();
        OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
        OustAppState.getInstance().setLandingPageLive(false);
        OustStaticVariableHandling.getInstance().setAppActive(false);
        OustSdkTools.clearAlldataAndlogout();
        if(retry>=3){*/

        OustAppState.getInstance().setLandingPageOpen(false);
        isLoggedInCalled = false;

        if(oustLoginCallBack!=null)
            oustLoginCallBack.onError((error!=null && !error.isEmpty() && !error.equals(""))?error:"Something went wrong!!");

        if(isApiCall && oustApiListener!=null)
            oustApiListener.onError((error!=null && !error.isEmpty() && !error.equals(""))?error:"Something went wrong!!");

        /*}else{
            Log.d(TAG, "sendErrorToOustCallback: restarted login");
            if(isContext){
                loginProcessWithContext();
            }else{
                checkLoginProcess();
            }
        }*/
    }

    public void getUpdateResoursesData() {
        String msg = "system/appResourses/android";

        Log.d(TAG, "getUpdateResoursesData: "+msg);
        ValueEventListener languageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        LanguagesClasses classes = new LanguagesClasses();
                        Map<String, Object> mainMap = (Map<String, Object>) snapshot.getValue();
                        if (mainMap.get("languages") != null) {
                            List<LanguageClass> list = new ArrayList<>();
                            HashMap<String, Object> languageData = (HashMap<String, Object>) mainMap.get("languages");
                            classes.setLastTimestamp((long) languageData.get("updateTS"));
                            for (String langKey : languageData.keySet()) {
                                if (!langKey.equalsIgnoreCase("updateTS")) {
                                    Map<String, Object> languageMap1 = (Map<String, Object>) languageData.get(langKey);
                                    //Map<String, Object> languageSubMap = (Map<String, Object>) languageMap1.get("mobile");
                                    LanguageClass languageClass = new LanguageClass();
                                    languageClass.setLanguagePerfix(langKey);
                                    languageClass.setName((String) languageMap1.get("displayName"));
                                    languageClass.setIndex((int) ((long) languageMap1.get("index")));
                                    languageClass.setFileName((String) languageMap1.get("keyName"));
                                    if ((languageMap1.get("languageUpdatedTS")) != null) {
                                        Object o1 = languageMap1.get("languageUpdatedTS");
                                        if (o1.getClass().equals(String.class)) {
                                            long n1 = Long.parseLong((String) o1);
                                            languageClass.setTimeStamp(n1);
                                        } else if (o1.getClass().equals(Long.class)) {
                                            long n1 = ((long) o1);
                                            languageClass.setTimeStamp(n1);
                                        }
                                    }
                                    list.add(languageClass);
                                }
                            }
                            classes.setLanguageClasses(list);
                            checkForLannguageUpdate(classes);
                        } else {
                            classes = new LanguagesClasses();
                            LanguageClass languageClass = new LanguageClass();
                            languageClass.setIndex(1);
                            languageClass.setName("English");
                            languageClass.setLanguagePerfix("en");
                            languageClass.setFileName("englishStr.properties");
                            List<LanguageClass> languageClasses = new ArrayList<>();
                            languageClasses.add(languageClass);
                            classes.setLanguageClasses(languageClasses);
                            Gson gson = new Gson();
                            String langStr = gson.toJson(classes);
                            OustPreferences.save("alllanguage", langStr);
                        }

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
                    //sendErrorToOustCallback("Something went wrong!!");
                    //oustLoginCallBack.onError("Something went wrong!!");
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                updateAppResoursesOver();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //sendErrorToOustCallback("Something went wrong!!");
                //oustLoginCallBack.onError("Something went wrong!!");
                updateAppResoursesOver();
                Log.i("splash firebase ", error.toString());
            }

        };
        OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg).addValueEventListener(languageListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(languageListener, msg));


        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (!connected) {
                        updateAppResoursesOver();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                updateAppResoursesOver();
            }
        };
        OustFirebaseTools.getRootRef().child(".info/connected");
        OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
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
            //sendErrorToOustCallback("Something went wrong!!");
            //oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateAppResoursesOver() {
        startDownloadingResourses();
    }

    private boolean checkLoginCalled = false;

    private void checkLoginProcess() {
        Log.d(TAG, "checkLoginProcess: userId:"+userName);
        if (userName!=null && !userName.isEmpty()) {
            retry++;
            isContext = false;

            if(OustPreferences.getAppInstallVariable("IsLoginServiceRunning")){
                OustLoginService.setOustLoginApiCallBack(this);
            }else {
                if (language != null) {
                    new OustLoginService(activity, userName,fName,lName, orgId, language, true, this).checkLoginProcess();
                } else {
                    new OustLoginService(activity, userName,fName,lName, orgId, true, this).checkLoginProcess();
                }
            }
        }else{
            sendErrorToOustCallback("UserId is mandatory");
            //oustLoginCallBack.onError("UserId is mandatory");
        }
    }

    private void loginProcessWithContext() {
        Log.d(TAG, "loginProcessWithContext: userid:"+userName);
        if (userName!=null && !userName.isEmpty()) {
            retry++;
            isContext = true;

            if(OustPreferences.getAppInstallVariable("IsLoginServiceRunning")){
                OustLoginService.setOustLoginApiCallBack(this);
            }else {
                if (language != null) {
                    new OustLoginService(context, userName,fName,lName, orgId, language, true, this).checkLoginProcess();
                } else {
                    new OustLoginService(context, userName, fName,lName,orgId, true, this).checkLoginProcess();
                }
            }
        }else{
            sendErrorToOustCallback("UserId is mandatory");
            //oustLoginCallBack.onError("UserId is mandatory");
        }
    }

    @Override
    public void onLoginApiError(String error) {
        Log.d(TAG, "onLoginApiError: "+error);
        if(isApiCall){
            OustDataHandler.getInstance().resetData();
            OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
            OustAppState.getInstance().setLandingPageLive(false);
            OustStaticVariableHandling.getInstance().setAppActive(false);
            OustSdkTools.clearAlldataAndlogout();
            if(retry>=3){
                if(oustApiListener!=null){
                    oustApiListener.onLoginError(error);
                }
            }else{
                Log.d(TAG, "onLoginApiError: restarted login");
                if(isContext){
                    loginProcessWithContext();
                }else{
                    checkLoginProcess();
                }
            }
            return;
        }
        sendErrorToOustCallback(error);
        //oustLoginCallBack.onError(error);
    }

    /*private void startLoginProcess() {
        oustLoginCallBack.onLoginProcessStart();
        userName = (userName.trim());
        if ((userName != null) && (userName.length() > 1)) {
            {
                getBaseURL(orgId);
            }
        }
    }*/

    private void startLoginProcess() {
        retry++;
        if(oustLoginCallBack!=null)
            oustLoginCallBack.onLoginProcessStart();

        userName = (userName.trim());
        /*if ((userName != null) && (userName.length() > 1)) {
            verifyTenant(orgId);
        }*/

        if ((userName != null) && (userName.length() > 1)) {
            getBaseURL(orgId);
        }
    }

    private void getBaseURL(String orgId){
        urlForBaseURLCheck = AppConstants.StringConstants.SIGN_URL_BASE;
        urlForBaseURLCheck = urlForBaseURLCheck.replace("{orgId}", orgId);
        new CheckBaseURL().execute(urlForBaseURLCheck);
    }

    @Override
    public void onLoginStart() {
        if(isApiCall){
            if(oustApiListener!=null)
                oustApiListener.onLoginProcessStart();
            return;
        }
        if(oustLoginCallBack!=null)
            oustLoginCallBack.onLoginProcessStart();
    }

    @Override
    public void onError(String error) {
        if(isApiCall){
            if(oustApiListener!=null)
                oustApiListener.onLoginError(error);
            return;
        }
        sendErrorToOustCallback(error);
        //oustLoginCallBack.onError(error);
    }

    @Override
    public void onDownloadProgress(int progress) {
        if(isApiCall){
            if(oustApiListener!=null)
                oustApiListener.onStartDownloadingResourses();
            return;
        }
        if(oustLoginCallBack!=null)
            oustLoginCallBack.onProgressChanged(progress);
    }

    @Override
    public void onLoginApiComplete() {
        if(isApiCall){
            if(oustApiListener!=null)
                oustApiListener.onLoginSuccess(true);
            return;
        }
        if(oustLoginCallBack!=null)
            oustLoginCallBack.onLoginSuccess(true);
    }

    //====================================================================================================
//download all resourses for app
    private List<LanguageClass> updateLanguageClasses = new ArrayList<>();
    private float totalResources = 0;
    private float resourcesDownloaded = 0;

    private void downloadAllResourses() {
        Log.d(TAG, "downloadAllResourses: ");
        if (RoomHelper.getResourceClassCount() == 0) {
            addResourcesToRealm();
        }
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            startDownloadingResourses();
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

    /*private void downloadAllResourses() {
        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            new DownloadInitService(activity,this);
        } else {
            initFirebase();
        }
    }*/

    public void startDownloadingResourses() {
        Log.d(TAG, "startDownloadingResourses: ");

        totalResources = 0;
        totalImageResourceSize = 0;
        totalAudioResourceSize = 0;
        totalFontResourceSize = 0;
        totalDownloadedResources = 0;
        downloadStartedResources = 0;
        totalResourceTobeDownloaded = 0;
        resourcesDownloaded = 0;

        if (!OustPreferences.getAppInstallVariable("allresourcesDownloadeda")) {
            //have to remove, if possible

/*            String imagePath1 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/bgImage";
            downloadImage(orgId, imagePath1, "splashScreen", true);
            String imagePath2 = "appImages/splash/org/" + (orgId.toUpperCase()) + "/android/icon";
            downloadImage(orgId, imagePath2, "splashIcon", false);*/
//            downLoadAudioImagesResource(RES_AUDIO);
//            downLoadAudioImagesResource(RES_IMAGE);

            //2 lines commented
            /*calculateResourceSize();
            totalResources += 1;*/

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
            downLoad(languageClass, languageClass.getFileName(), 0);

            //loadLanResFromResources("myname_properties.tmp", languageClass, languageClass.getFileName(), 0);
        } else {
            //checkForResourceFileUpdate();
            showDownloadProgress();
        }
    }

    public void downLoad(final LanguageClass languageClass, String fileName1, final int languageIndex) {
        try {
            if (!OustSdkTools.checkInternetStatus(context)) {
                calculateResourceSize();
                //         checkLoginProcess();
                return;
            }
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = "languagePacks/mobile/" + fileName1;
            final File file = getTempFile();

            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
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
                        //downloadResurceFailed();
                        calculateResourceSize();
                        ex.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            //downloadResurceFailed();
            calculateResourceSize();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public File getTempFile() {
        File file;
        try {
            String fileName = "myname.properties";
            file = File.createTempFile(fileName, null, context.getCacheDir());
            return file;
        } catch (Exception e) {
            //downloadInitListener.onDownloadFailed();
            calculateResourceSize();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return null;
    }

    private int totalImageResourceSize = 0;
    private int totalAudioResourceSize = 0;
    private int totalFontResourceSize = 0;
    private int totalDownloadedResources = 0;
    private int downloadStartedResources = 0;
    private int totalResourceTobeDownloaded = 0;

    private void calculateResourceSize() {
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

    private void newDownloadResource(){
        try{
            String[] audioResList = context.getResources().getStringArray(R.array.sounds);
            String[] imageResList = context.getResources().getStringArray(R.array.all_images);
            String[] fontResList = context.getResources().getStringArray(R.array.fonts);
            for(int i=0;i<imageResList.length;i++){
                loadInitResFromResources(imageResList[i]);
            }
            for(int i=0;i<audioResList.length;i++){
                loadInitResFromResources(audioResList[i]);
            }
            for(int i=0;i<fontResList.length;i++){
                loadInitResFromResources(fontResList[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void downloadStrategyOfResource() {
        try {
            Log.d(TAG, "downloadStrategyOfResource: ");
            String[] audioResList = activity.getResources().getStringArray(R.array.sounds);
            String[] imageResList = activity.getResources().getStringArray(R.array.all_images);
            String[] fontResList = activity.getResources().getStringArray(R.array.fonts);

            Log.d(TAG, "downloadStrategyOfResource: downloadStartedResources:"+downloadStartedResources+" -- totalDownloadedResources:"+totalDownloadedResources);
            if (downloadStartedResources < totalResourceTobeDownloaded) {
                totalDownloadedResources = downloadStartedResources;
                downloadStartedResources += 10;
                if (downloadStartedResources > totalResourceTobeDownloaded) {
                    downloadStartedResources = totalResourceTobeDownloaded;
                }
                for (int i = totalDownloadedResources; i < downloadStartedResources; i++) {
                    if (i < totalImageResourceSize) {
                        loadInitResFromResources(imageResList[i]);
                        //downLoadAudioImagesResource(RES_IMAGE, imageResList[i]);

                    } else if (i >= totalImageResourceSize && i < (totalImageResourceSize + totalAudioResourceSize)) {
                        loadInitResFromResources(audioResList[i - totalImageResourceSize]);
                        //downLoadAudioImagesResource(RES_AUDIO, audioResList[i - totalImageResourceSize]);

                    } else if (i >= (totalImageResourceSize + totalAudioResourceSize) && i < totalResourceTobeDownloaded) {
                        loadInitResFromResources(fontResList[i - (totalImageResourceSize + totalAudioResourceSize)]);
                        //downLoadFontResources(RES_FONT, fontResList[i - (totalImageResourceSize + totalAudioResourceSize)]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //showDownloadProgress();
        }
    }*/

    private void loadInitResFromResources(String filename){
        try {
            final File file = new File(context.getFilesDir(), filename);
            String rawFilename = filename.split("\\.")[0];
            if ((file != null) && (!file.exists())) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    //com.oustme.oustsdk
                    int resId = context.getResources().getIdentifier("raw/"+rawFilename, null, context.getPackageName());
                    Log.d("NewInitRes", "loadInitResFromResources: "+rawFilename+" -- resid:"+resId);
                    in =  context.getResources().openRawResource(resId);
                    out = new FileOutputStream(file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(Exception e) {
                    Log.e("NewInitRes", "Failed to copy asset file: " + filename, e);
                }
            }/*else{
                Log.e("InitRes", "FNF : " + filename);
            }*/
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        showDownloadProgress();
        Log.e("NewInitRes", "copied raw file: " + filename);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadLanResFromResources(String filename, final LanguageClass languageClass, String fileName1, final int languageIndex){
        try {
            final File file = new File(context.getFilesDir(), filename);
            String rawFilename = filename.split("\\.")[0];
            if ((file != null) && (!file.exists())) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    //com.oustme.oustsdk
                    int resId = context.getResources().getIdentifier("raw/"+rawFilename, null, context.getPackageName());
                    Log.d("InitLanRes", "loadLanResFromResources: "+rawFilename+" -- resid:"+resId);
                    in =  context.getResources().openRawResource(resId);
                    out = new FileOutputStream(file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch(Exception e) {
                    Log.e("NewInitRes", "Failed to copy asset file: " + filename, e);
                }
            }
            createFileAndSave(languageClass, languageIndex, file);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //showDownloadProgress();
            calculateResourceSize();
        }
        //showDownloadProgress();
        Log.e("NewInitRes", "copied raw file: " + filename);
    }


    public void createFileAndSave(LanguageClass languageClass, int languageIndex, File file) {
        try {
            if(file!=null && file.exists()){
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

                Log.d(TAG, "createFileAndSave: "+file.getAbsolutePath());
            }

            OustStrings.init();
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

    public void saveData(File file, String fileName1, boolean isBackgroundImage) {
        try {
            Log.e(TAG, "saveData");
            boolean isSplshScreenImage = true;
            if (fileName1.equalsIgnoreCase("splashScreen")) {
                isSplshScreenImage = true;
            } else {
                isSplshScreenImage = false;
            }
            fileName1 = "oustlearn_" + (orgId.toUpperCase()) + fileName1;
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            enternalPrivateStorage.deleteFile(fileName1);
            byte[] bytes = FileUtils.readFileToByteArray(file);
            String encoded = Base64.encodeToString(bytes, 0);
            enternalPrivateStorage.saveFile(fileName1, encoded);
            enternalPrivateStorage.createResourceFile(fileName1, 2, file);
            file.delete();
        } catch (Exception e) {
            //sendErrorToOustCallback("Save data error");
            //oustLoginCallBack.onError("Save data error!!");
            Log.e(TAG, "saveData" + e.getMessage());
        }
    }

    public void showDownloadProgress() {
        //Log.d(TAG, "showDownloadProgress: ");
        resourcesDownloaded++;
        if (totalResources > 0) {
            Log.d("TAG", "showDownloadProgress: total:"+totalResources+" -- resDownloaded:"+resourcesDownloaded);

            float progress = ((float) (resourcesDownloaded / totalResources));
            int progress1 = (int) (progress * 100);
            if (progress1 > 100) {
                progress1 = 100;
            }
            if(oustLoginCallBack!=null)
                oustLoginCallBack.onProgressChanged(progress1);
            if (resourcesDownloaded >= (totalResources)) {
                OustPreferences.saveAppInstallVariable("allresourcesDownloadeda", true);
                checkLoginProcess1();
            } /*else {
                downloadStrategyOfResource();
            }*/
        } else {
            checkLoginProcess1();
        }
    }

    private boolean resourceDownloadFailed = false;

    @Override
    public void onDownloadFailed() {
        Log.d(TAG,"onDownloadFailed");
        if(oustLoginCallBack!=null){
            sendErrorToOustCallback("Resource download failed");
            //oustLoginCallBack.onError("Something went wrong!");
        }

        if(oustApiListener!=null){
            oustApiListener.onError("Resource download failed");
        }
    }

    @Override
    public void onDownloadComplete() {
        Log.d(TAG,"onDownloadComplete");
        if(oustLoginCallBack!=null)
            oustLoginCallBack.onProgressChanged(100);

        if(oustApiListener!=null){
            oustApiListener.onResourcesInitialized();
        }else {
            if(!isStopLogin){
                if(context!=null)
                    loginProcessWithContext();
                else
                    checkLoginProcess();
            }
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        //Log.d(TAG,"onProgressChanged :"+progress);
        if(oustLoginCallBack!=null){
            oustLoginCallBack.onProgressChanged(progress);
        }
        if(oustApiListener!=null){
            oustApiListener.onProgressChanged(progress);
        }
    }

    private boolean isLoggedInCalled = false;

    private void checkLoginProcess1(){
        Log.d(TAG, "checkLoginProcess1: ");
        try {
            if(!isLoggedInCalled){
                isLoggedInCalled = true;
                if (!OustAppState.getInstance().isLandingPageOpen()) {
                    OustAppState.getInstance().setLandingPageOpen(true);
                    final String userdata = OustPreferences.get("userdata");
                    if ((userdata != null) && (!userdata.isEmpty())) {
                        if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                            if(oustNotificationData!=null) {
                                startNotification();
                            }else{
                                startLandingPage();
                            }
                        } else {
                            startLoginProcess();
                        }
                    } else {
                        startLoginProcess();
                    }
                }else{
                    if(oustLoginCallBack!=null)
                        oustLoginCallBack.onOustLoginStatus("Oust Login is already in progress");
                    if(isApiCall && oustApiListener!=null)
                        oustApiListener.onOustLoginStatus("Oust Login is already in progress");
                }
            }

        } catch (Exception e) {
            sendErrorToOustCallback("Failed to start login process");
            //oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startNotification(){
        OustPreferences.save("test_userid", userName);
        OustPreferences.save("test_orgid", orgId);

        NotificationData notificationData=new NotificationData();
        if(oustNotificationData.get("imageUrl")!=null) {
            notificationData.setImgUrl(oustNotificationData.get("imageUrl"));
        }
        if(oustNotificationData.get("id")!=null) {
            notificationData.setId(oustNotificationData.get("id"));
        }
        if(oustNotificationData.get("title")!=null) {
            notificationData.setTitle(oustNotificationData.get("title"));
        }
        if(oustNotificationData.get("type")!=null) {
            notificationData.setGcmType(oustNotificationData.get("type"));
        }
        if(oustNotificationData.get("noticeBoardNotificationData")!=null){
            notificationData.setNoticeBoardNotificationData(oustNotificationData.get("noticeBoardNotificationData"));
        }

        try {
            Intent resultIntent = null;
            if (notificationData.getGcmType() != null) {
                Log.e("Notification", "  inside sendNotification() --- gcmType is "+notificationData.getGcmType());
                if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                    resultIntent = new Intent(activity, NewLandingActivity.class);
                } else {
                    resultIntent = new Intent(activity, NewLandingActivity.class);
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
                    resultIntent.putExtra("isFeedDistributed", true);
                }

                if(resultIntent!=null){
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    OustSdkTools.newActivityAnimation(resultIntent, activity);
                }else{
                    sendErrorToOustCallback("Couldn't able to load the notification");
                    //oustLoginCallBack.onError("Couldn't able to load the notification");
                }
            }else{
                sendErrorToOustCallback("Couldn't able to load the notification");
                //oustLoginCallBack.onError("Couldn't able to load the notification");
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendErrorToOustCallback("Couldn't able to load the notification");
            //oustLoginCallBack.onError("Couldn't able to load the notification");
        }

        //OustNotificationHandler oustNotificationHandler=new OustNotificationHandler(oustNotificationData, activity);
    }

    private void startLandingPage() {
        Log.d(TAG, "startLandingPage: ");
        OustPreferences.save("test_userid", userName);
        OustPreferences.save("test_orgid", orgId);

        checkLayoutOpenLandingPage();

        /*Intent intent = new Intent(activity, NewLandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        OustSdkTools.newActivityAnimation(intent, activity);*/

    }

    private void checkLayoutOpenLandingPage(){
        try {
            checkAWS(orgId);

            final String message = "system/appConfig/layoutInfo/LAYOUT_4";
            Log.e(TAG, "checkLayoutOpenLandingPage: " + message);
            ValueEventListener layoutDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                        openLandingPage(true);
                    }else{
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

    public void gotoLandingPage(VerifyAndSignInResponse verifyAndSignInResponse) {
        try {
            Gson gson = new GsonBuilder().create();
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();

            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getNewActiveUser(verifyAndSignInResponse)));
            OustPreferences.save("isLoggedIn", "true");
            OustPreferences.save("loginType", LoginType.Oust.toString());
            OustPreferences.save("test_userid", userName);
            OustPreferences.save("test_orgid", orgId);

            if(oustNotificationData!=null){
                startNotification();
            }else {

                if(oustLoginCallBack!=null)
                    oustLoginCallBack.onOustContentLoaded();

                isActivityActive = false;
                layout_toolbarcolor = toolbarcolor;
                layout_signInResponse = verifyAndSignInResponse;
                checkLayoutOpenLandingPage();

                /*Intent intent;
                intent = new Intent(activity, NewLandingActivity.class);
                isActivityActive = false;
                intent.putExtra("ActiveUser", gson.toJson(OustSdkTools.getInstance().getNewActiveUser(verifyAndSignInResponse)));
                intent.putExtra("toolbarcolor", toolbarcolor);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                OustSdkTools.newActivityAnimation(intent, activity);*/
            }
        } catch (Exception e) {
            sendErrorToOustCallback("Failed to launch Landing Page");
            //oustLoginCallBack.onError("Failed to launch Landing Page");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String layout_toolbarcolor;
    private VerifyAndSignInResponse layout_signInResponse;

    private void openLandingPage(boolean isLayout4){
        try{
            Intent intent;
            Gson gson = new GsonBuilder().create();

            if(oustLoginCallBack!=null)
                oustLoginCallBack.onOustContentLoaded();
            isLoggedInCalled = false;
            if(isLayout4){
                intent = new Intent(activity, LandingActivity.class );
            }else {
                intent = new Intent(activity, NewLandingActivity.class);
                OustPreferences.save("toolbarColorCode",toolbarcolor);
                if(layout_toolbarcolor!=null && !layout_toolbarcolor.isEmpty()) {
                    intent.putExtra("toolbarcolor", layout_toolbarcolor);
                }
            }

            if(layout_signInResponse!=null) {
                intent.putExtra("ActiveUser", gson.toJson(OustSdkTools.getInstance().getNewActiveUser(layout_signInResponse)));
            }

            if(userName!=null && !userName.isEmpty()) {
                OustPreferences.save("test_userid", userName);
            }
            if(orgId!=null && !orgId.isEmpty()) {
                OustPreferences.save("test_orgid", orgId);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            OustSdkTools.newActivityAnimation(intent, activity);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void verifyAndSignIn(final String orgId, final String userId) {
        try {
            Log.d(TAG,"verifyAndSignIn orgid:"+orgId+" -- userid:"+userId);
            setTanentID(orgId);
            String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyAndSignInUser);

            VerifyAndSignInRequest verifyAndSignInRequest = new VerifyAndSignInRequest();
            verifyAndSignInRequest.setStudentid(userId);
            verifyAndSignInRequest.setOrgId(orgId);
            verifyAndSignInRequest.setInstitutionLoginId(orgId);

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

            verifyAndSignInRequest.setPassword(encryptedPassword);
            verifyAndSignInRequest.setClientEncryptedPassword(true);

            final Gson gson = new GsonBuilder().create();
            verifyOrgUrl = HttpManager.getAbsoluteUrl(verifyOrgUrl);

            String jsonParams = gson.toJson(verifyAndSignInRequest);

            /*ApiCallUtils.doNetworkCallWithoutAuth(Request.Method.POST, verifyOrgUrl, OustSdkTools.getRequestObjectWithPreference(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiCallUtils.setIsLoggedOut(false);
                    Log.d(TAG, "onResponse: "+response.toString());
                    Log.d(TAG, "onResponse: " + response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAppId", response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey", response.optString("firebaseAPIKey"));

                    Gson gson = new Gson();
                    VerifyAndSignInResponse verifyAndSignInResponse = gson.fromJson(response.toString(), VerifyAndSignInResponse.class);
                    gotoVerifyAndSignInResponse(verifyAndSignInResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    sendErrorToOustCallback("Couldn't get the tenant details");
                    Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());                }
            });*/

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, verifyOrgUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: "+response.toString());
                    Log.d(TAG, "onResponse: " + response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAppId", response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey", response.optString("firebaseAPIKey"));

                    Gson gson = new Gson();
                    VerifyAndSignInResponse verifyAndSignInResponse = gson.fromJson(response.toString(), VerifyAndSignInResponse.class);
                    gotoVerifyAndSignInResponse(verifyAndSignInResponse);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendErrorToOustCallback("Couldn't get the tenant details");
                    Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", getAPIKey());
                        params.put("org-id", orgId);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoVerifyAndSignInResponse(VerifyAndSignInResponse verifyAndSignInResponse){
        Log.d(TAG, "gotoVerifyAndSignInResponse: ");
        if(verifyAndSignInResponse!=null) {
            if (verifyAndSignInResponse.isSuccess() && verifyAndSignInResponse.isValidTenant()) {
                if ((verifyAndSignInResponse.getStudentKey()!=null && !verifyAndSignInResponse.getStudentKey().equals("0")) && (verifyAndSignInResponse.getStudentid() != null) && (!verifyAndSignInResponse.getStudentid().isEmpty())) {

                    Log.d(TAG, "gotoVerifyAndSignInResponse: "+verifyAndSignInResponse.getStudentKey());

                    OustPreferences.save(STUDE_KEY, verifyAndSignInResponse.getStudentKey());
                    verifyAndSignInResponse.setLoginType(LoginType.Oust.name());
                    OustSdkTools.initServerWithNewEndPoints(verifyAndSignInResponse.getApiServerEndpoint(), verifyAndSignInResponse.getFirebaseEndpoint(), verifyAndSignInResponse.getFirebaseAppId(), verifyAndSignInResponse.getFirebaseAPIKey());

                    if(verifyAndSignInResponse.getS3_base_end()!=null && !verifyAndSignInResponse.getS3_base_end().isEmpty()) {
                        AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL = verifyAndSignInResponse.getS3_base_end()+verifyAndSignInResponse.getImg_bucket_name()+"/";
                        OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, verifyAndSignInResponse.getS3_base_end()+verifyAndSignInResponse.getImg_bucket_name()+"/");
                    } else {
                        String s3baseend = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL;
                        OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, s3baseend);
                    }

                    if(verifyAndSignInResponse.getS3_Bucket_Region()!=null && !verifyAndSignInResponse.getS3_Bucket_Region().isEmpty()) {
                        AppConstants.MediaURLConstants.BUCKET_REGION = verifyAndSignInResponse.getS3_Bucket_Region();
                        OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, verifyAndSignInResponse.getS3_Bucket_Region());
                    } else {
                        String s3BktRegion =AppConstants.MediaURLConstants.BUCKET_REGION;
                        OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, s3BktRegion);
                    }

                    if(verifyAndSignInResponse.getHttp_img_bucket_cdn()!=null && !verifyAndSignInResponse.getHttp_img_bucket_cdn().isEmpty()) {
                        AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH = verifyAndSignInResponse.getHttp_img_bucket_cdn();
                        OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, verifyAndSignInResponse.getHttp_img_bucket_cdn());
                    } else {
                        OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
                    }

                    if(verifyAndSignInResponse.getImg_bucket_cdn()!=null && !verifyAndSignInResponse.getImg_bucket_cdn().isEmpty()) {
                        AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS = verifyAndSignInResponse.getImg_bucket_cdn();
                        OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, verifyAndSignInResponse.getImg_bucket_cdn());
                    } else {
                        OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
                    }

                    if(verifyAndSignInResponse.getImg_bucket_name()!=null && !verifyAndSignInResponse.getImg_bucket_name().isEmpty()) {
                        S3_BUCKET_NAME = verifyAndSignInResponse.getImg_bucket_name();
                        OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, verifyAndSignInResponse.getImg_bucket_name());
                    } else {
                        OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, S3_BUCKET_NAME);
                    }

                    if (verifyAndSignInResponse.getWebAppLink() != null && !verifyAndSignInResponse.getWebAppLink().isEmpty()) {
                        Log.d(TAG, "onResponse: webAppLink:"+verifyAndSignInResponse.getWebAppLink());
                        com.oustme.oustsdk.tools.OustPreferences.save("webAppLink",verifyAndSignInResponse.getWebAppLink());
                    }else {
                        Log.d(TAG, "onResponse: webAppLink else-->:"+verifyAndSignInResponse.getWebAppLink());
                    }

                    if (verifyAndSignInResponse.getAwsS3KeyId() != null && !verifyAndSignInResponse.getAwsS3KeyId().isEmpty()) {
                        Log.e("TAG", "awsS3KeyId--> " + verifyAndSignInResponse.getAwsS3KeyId());
                        com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeyId", verifyAndSignInResponse.getAwsS3KeyId());
                    } else {
                        com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeyId", "");
                    }

                    if (verifyAndSignInResponse.getAwsS3KeySecret() != null && !verifyAndSignInResponse.getAwsS3KeySecret().isEmpty()) {
                        Log.e("TAG", "awsS3KeySecret--> " + verifyAndSignInResponse.getAwsS3KeySecret());
                        com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeySecret", verifyAndSignInResponse.getAwsS3KeySecret());
                    } else {
                        com.oustme.oustsdk.tools.OustPreferences.save("awsS3KeySecret", "");
                    }

                    if ((verifyAndSignInResponse.getFirebaseToken() != null) && (!verifyAndSignInResponse.getFirebaseToken().isEmpty())) {
                        OustPreferences.save("firebaseToken", verifyAndSignInResponse.getFirebaseToken());
                        Gson gson = new Gson();
                        OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getNewActiveUser(verifyAndSignInResponse)));
                        OustPreferences.save("isLoggedIn", "true");
                        OustPreferences.save("loginType", LoginType.Oust.toString());

                        if(groupId!=null && !groupId.isEmpty()){
                            updateUserGroupId(orgId, userName, groupId, verifyAndSignInResponse);
                        }else {
                            authenticateWithFirebase(verifyAndSignInResponse);
                        }

                        //onLoginComplete();
                    }
                }else{
                    sendErrorToOustCallback("Couldn't able to load the user details");
                }
            } else {
                /*if (verifyAndSignInResponse.isValidTenant())
                    sendErrorMessageToListener(""+OustStrings.getString("instutionalempty_message"));
                else
                    sendErrorMessageToListener(""+OustStrings.getString("instutionalempty_message"));*/

                Log.d(TAG, "gotoVerifyAndSignInResponse: error");
                sendErrorToOustCallback(verifyAndSignInResponse.getError());
            }
        }else{
            Log.d(TAG, "gotoVerifyAndSignInResponse: tenant not found");
            sendErrorToOustCallback(""+OustStrings.getString("instutionalempty_message"));
        }
    }

    /*public void verifyTenant(final String orgId) {
        try {
            setTanentID(orgId);
            String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyOrgId);
            verifyOrgUrl = verifyOrgUrl.replace("{orgId}", orgId);
            verifyOrgUrl=verifyOrgUrl+"?devicePlatformName=Android";
            verifyOrgUrl= HttpManager.getAbsoluteUrl(verifyOrgUrl);
            Log.d(TAG, "verifyTenant: "+verifyOrgUrl);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, verifyOrgUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: "+response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAppId",response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey",response.optString("firebaseAPIKey"));
                    Gson gson = new Gson();
                    VerifyOrgIdResponseA verifyOrgIdResponse = gson.fromJson(response.toString(),VerifyOrgIdResponseA.class);
                    gotVerifyTanentResponse(verifyOrgIdResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error.getLocalizedMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        Log.d(TAG, "getHeaders: "+getAPIKey());
                        params.put("api-key", getAPIKey());
                        params.put("org-id", orgId);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    /*private void gotVerifyTanentResponse(VerifyOrgIdResponseA verifyOrgIdResponse){
        Log.d(TAG, "gotVerifyTanentResponse: ");
        if ((verifyOrgIdResponse != null) && (verifyOrgIdResponse.isSuccess()))
        {
            if (verifyOrgIdResponse.isValidTenant())
            {
                signInRequest = new SignInRequest();
                if((OustPreferences.get("gcmToken")!=null)) {
                    signInRequest.setDeviceToken(OustPreferences.get("gcmToken"));
                }
                String encryptedPassword = null;
                String otpStr="test";
                try {
                    if(otpStr.matches("[a-fA-F0-9]{32}")){
                        encryptedPassword=otpStr;
                    }else {
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


                if(verifyOrgIdResponse.getS3_base_end()!=null && !verifyOrgIdResponse.getS3_base_end().isEmpty())
                {
                    AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL = verifyOrgIdResponse.getS3_base_end()+verifyOrgIdResponse.getImg_bucket_name()+"/";
                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, verifyOrgIdResponse.getS3_base_end()+verifyOrgIdResponse.getImg_bucket_name()+"/");
                }
                else {
                    String s3baseend = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL;
                    OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, s3baseend);
                }

                if(verifyOrgIdResponse.getS3_Bucket_Region()!=null && !verifyOrgIdResponse.getS3_Bucket_Region().isEmpty())
                {
                    AppConstants.MediaURLConstants.BUCKET_REGION = verifyOrgIdResponse.getS3_Bucket_Region();
                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, verifyOrgIdResponse.getS3_Bucket_Region());
                }
                else
                {
                    String s3BktRegion =AppConstants.MediaURLConstants.BUCKET_REGION;
                    OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, s3BktRegion);
                }

                if(verifyOrgIdResponse.getHttp_img_bucket_cdn()!=null && !verifyOrgIdResponse.getHttp_img_bucket_cdn().isEmpty())
                {
                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH = verifyOrgIdResponse.getHttp_img_bucket_cdn();
                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, verifyOrgIdResponse.getHttp_img_bucket_cdn());
                }
                else
                {
                    OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
                }


                if(verifyOrgIdResponse.getImg_bucket_cdn()!=null && !verifyOrgIdResponse.getImg_bucket_cdn().isEmpty())
                {
                    AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS = verifyOrgIdResponse.getImg_bucket_cdn();
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, verifyOrgIdResponse.getImg_bucket_cdn());
                }
                else
                {
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
                }


                if(verifyOrgIdResponse.getImg_bucket_name()!=null && !verifyOrgIdResponse.getImg_bucket_name().isEmpty())
                {
                    S3_BUCKET_NAME = verifyOrgIdResponse.getImg_bucket_name();
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, verifyOrgIdResponse.getImg_bucket_name());
                }
                else
                {
                    OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, S3_BUCKET_NAME);
                }

                oustLoginA(signInRequest);
            } else {
                Log.d(TAG, "gotVerifyTanentResponse: error");
                sendErrorToOustCallback(OustStrings.getString("instutionalempty_message"));
                //oustLoginCallBack.onError(OustStrings.getString("instutionalempty_message"));
            }
        }
    }*/

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

    /*public void oustLoginA(SignInRequest signInRequest) {
        Log.d(TAG, "oustLoginA: ");
        try {
            String signInUrl = OustSdkApplication.getContext().getResources().getString(R.string.signin);
            signInUrl = HttpManager.getAbsoluteUrl(signInUrl);

            final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(signInRequest);
            final SignInResponse[] signInResponse = new SignInResponse[1];
            Log.d(TAG, "oustLoginA: "+jsonParams);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, signInUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    signInResponse[0] = gson.fromJson(response.toString(), SignInResponse.class);
                    oustLoginProcessOverA(signInResponse[0]);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    oustLoginProcessOverA(signInResponse[0]);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", getAPIKey());
                        params.put("org-id", orgId);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

        } catch(Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLoginProcessOverA(SignInResponse signInResponse1){
        this.signInResponse=signInResponse1;
        if (signInResponse != null) {
            if(signInResponse.isSuccess()){
                if((signInResponse.getStudentid()!=null)&&(!signInResponse.getStudentid().isEmpty())) {
                    signInResponse.setLoginType(LoginType.Oust.name());
                    OustSdkTools.initServerWithNewEndPoints(signInResponse.getApiServerEndpoint(),signInResponse.getFirebaseEndpoint(),signInResponse.getFirebaseAppId(),signInResponse.getFirebaseAPIKey());
                    if((signInResponse.getFirebaseToken()!=null)&&(!signInResponse.getFirebaseToken().isEmpty())) {
                        authenticateWithFirebase(signInResponse.getFirebaseToken());
                    }
                }
            }else{
                setresisterUserRequest();
            }
        }
    }

    public void oustLoginProcessOver(LevelOneAuthCheckResponseData levelOneAuthCheckResponseData) {
        if (levelOneAuthCheckResponseData != null) {
            if (levelOneAuthCheckResponseData.isSuccess()) {
                //initlize end points
                setTanentID(orgId);
                OustSdkTools.initServerWithNewEndPoints(levelOneAuthCheckResponseData.getApiServerEndpoint(), levelOneAuthCheckResponseData.getFirebaseEndpoint(),levelOneAuthCheckResponseData.getFirebaseAppId(),levelOneAuthCheckResponseData.getFirebaseAPIKey());
                if (levelOneAuthCheckResponseData.isUserExists()) {
                    saveUserRole(levelOneAuthCheckResponseData.getRole());
                    saveUserRole(levelOneAuthCheckResponseData.getRole());
                    if ((levelOneAuthCheckResponseData.getFirebaseToken() != null) && (!levelOneAuthCheckResponseData.getFirebaseToken().isEmpty())) {
                        OustSdkTools.initServerWithNewEndPoints(levelOneAuthCheckResponseData.getApiServerEndpoint(), levelOneAuthCheckResponseData.getFirebaseEndpoint(),levelOneAuthCheckResponseData.getFirebaseAppId(),levelOneAuthCheckResponseData.getFirebaseAPIKey());
                        OustStaticVariableHandling.getInstance().setEnterpriseUser(true);
                        getSignResponceForLevelOne(levelOneAuthCheckResponseData);
                        authenticateWithFirebase(levelOneAuthCheckResponseData.getFirebaseToken());
                    } else {
                        if(oustLoginCallBack!=null)
                            oustLoginCallBack.onLoginError("Firebase initialization failed");
                    }
                } else {
                    if (levelOneAuthCheckResponseData.isTrustedTenant()) {
                        setresisterUserRequest();
                    } else {
                        if(oustLoginCallBack!=null)
                            oustLoginCallBack.onLoginError("Not trusted tanent.");
                    }
                }
            } else {
                if (levelOneAuthCheckResponseData.getPopup() != null) {
                    Popup popup = levelOneAuthCheckResponseData.getPopup();
                    if(oustLoginCallBack!=null)
                        oustLoginCallBack.onLoginError(popup.getContent());
                } else if ((levelOneAuthCheckResponseData.getError() != null) && (!levelOneAuthCheckResponseData.getError().isEmpty())) {
                    if(oustLoginCallBack!=null)
                        oustLoginCallBack.onLoginError(levelOneAuthCheckResponseData.getError());
                } else {
                    if(oustLoginCallBack!=null)
                        oustLoginCallBack.onLoginError("Internal server error");
                }
            }
        } else {
            if(oustLoginCallBack!=null)
                oustLoginCallBack.onLoginError(OustStrings.getString("networkfail_msg"));
        }
    }*/

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
            //sendErrorToOustCallback();
            //oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void saveUserRole(String role) {
        OustPreferences.save("userRole", role);
    }

    //    firebase authentication
    /*public void authenticateWithFirebase(String token) {
        try {
            OustPreferences.save("firebaseToken",token);
            if(token!=null && !token.isEmpty()) {
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
                                        sendErrorToOustCallback("Firebase authentication error");
                                        //oustLoginCallBack.onError("Firebase authentication error");
                                        if (isActivityActive) {
                                            firebaseAuntheticationFailed();
                                        }
                                    } else {
                                        gotoLandingPage(signInResponse);
                                    }
                                } else {
                                    sendErrorToOustCallback("Firebase authentication error");
                                    //oustLoginCallBack.onError("Firebase authentication error");
                                    if (isActivityActive) {
                                        firebaseAuntheticationFailed();
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            sendErrorToOustCallback("Firebase authentication error");
            //oustLoginCallBack.onError("Firebase authentication error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void authenticateWithFirebase(VerifyAndSignInResponse verifyAndSignInResponse) {
        try {
            token = verifyAndSignInResponse.getFirebaseToken();
            OustPreferences.save("firebaseToken",token);
            if(token!=null && !token.isEmpty()) {
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
                                        //sendErrorToOustCallback("Firebase authentication error");
                                        //oustLoginCallBack.onError("Firebase authentication error");
                                        //if (isActivityActive) {
                                        firebaseAuntheticationFailed();
                                        //}
                                    } else {
                                        gotoLandingPage(verifyAndSignInResponse);
                                    }
                                } else {
                                    //sendErrorToOustCallback("Firebase authentication error");
                                    //oustLoginCallBack.onError("Firebase authentication error");
                                    //if (isActivityActive) {
                                    firebaseAuntheticationFailed();
                                    //}
                                }
                            }
                        });
            }
        } catch (Exception e) {
            //sendErrorToOustCallback("Firebase authentication error");
            //oustLoginCallBack.onError("Firebase authentication error");
            firebaseAuntheticationFailed();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void firebaseAuntheticationFailed() {
        try {
            //OustAppState.getInstance().setLandingPageOpen(false);
            OustDataHandler.getInstance().resetData();
            OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
            OustAppState.getInstance().setLandingPageLive(false);
            OustStaticVariableHandling.getInstance().setAppActive(false);
            OustSdkTools.clearAlldataAndlogout();
            if(retry>=3){
                sendErrorToOustCallback("Failed to auntheticate with firebase");
            }else{
                Log.d(TAG, "firebaseAuntheticationFailed: restarted login");
                startLoginProcess();
            }

            //sendErrorToOustCallback("Failed to auntheticate with firebase");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            OustAppState.getInstance().setLandingPageOpen(false);
            sendErrorToOustCallback("Failed to auntheticate with firebase");
            //oustLoginCallBack.onError("Failed to auntheticate with firebase");
        }
    }


   /* private void setresisterUserRequest() {
        try {
            RegisterRequestData registerRequestData = new RegisterRequestData();
//            registerRequestData.setPhone(userName);
//            registerRequestData.setOrgId(orgId);
            registerRequestData.setStudentid(userName);
            if ((OustPreferences.get("gcmToken") != null)) {
                registerRequestData.setDeviceToken(OustPreferences.get("gcmToken"));
            }
            if(language!=null){
                registerRequestData.setLanguage(language);
            }

            if(oustAuthData!=null){
                if(oustAuthData.getfName()!=null && !oustAuthData.getfName().isEmpty()){
                    registerRequestData.setFname(oustAuthData.getfName());
                }

                if(oustAuthData.getlName()!=null && !oustAuthData.getlName().isEmpty()){
                    registerRequestData.setLname(oustAuthData.getlName());
                }
            }

            registerUser(registerRequestData);
        } catch (Exception e) {
            sendErrorToOustCallback("Couldn't able to register user");
            //oustLoginCallBack.onError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void registerUser(RegisterRequestData registerRequestData) {
        Log.d(TAG, "registerUser: ");
        String register_url = OustSdkApplication.getContext().getResources().getString(R.string.register_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(registerRequestData);
        try {
            register_url = HttpManager.getAbsoluteUrl(register_url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, register_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SignInResponse signInResponse = gson.fromJson(response.toString(), SignInResponse.class);
                    gotRegisterResponceData(signInResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendErrorToOustCallback("Register user error");
                    //oustLoginCallBack.onError("Register user error");
                    oustLoginProcessOver(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", getAPIKey());
                        params.put("org-id", orgId);
                    } catch (OustException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        } catch (Exception e) {
            sendErrorToOustCallback("Register user error");
            //oustLoginCallBack.onError("Register user error");
            oustLoginProcessOver(null);
        }
    }*/

    //    save user data in shared prefrence for future use
    public void setTanentID(String tanentId) {
        try {
            OustPreferences.save("tanentid", tanentId);
        } catch (Exception e) {
            //sendErrorToOustCallback("");
            //oustLoginCallBack.onError("Something went wrong!!");
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

                if(statusCode==405)
                {
                    return "405";
                }
                else if(statusCode==301)
                {
                    String baseUERL = urlConnection.getHeaderField("Location");
                    return baseUERL;
                }

                else if (statusCode == 200) {
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

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute BaseURL: "+result);
            if(result==null){
                sendErrorToOustCallback("Problem on getting Org url");
            }

            if(result!=null && result.equalsIgnoreCase("405")) {
                sendErrorToOustCallback("Problem on getting Org url");

            } else if(result!=null) {
                String delimeter = "services/";
                String[] parts = result.split(delimeter, 2);
                String BASE_URL = parts[0] + "" + delimeter;
                OustPreferences.save(AppConstants.StringConstants.BASE_URL_FROM_API, BASE_URL);
                //verifyTenant(orgId);
                verifyAndSignIn(orgId, userName);
            }
        }
    }

    public void oustUpdateUserLanguage(Activity activity, OustAuthData oustAuthData, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        this.userName = oustAuthData.getUsername();
        this.orgId = oustAuthData.getOrgId();
        this.language = language;

        if(userName==null || userName.isEmpty()){
            if(oustApiListener!=null){
                oustApiListener.onError("UserId should be mandatory");
            }
            return;
        }

        if(language==null){
            if(oustApiListener!=null){
                oustApiListener.onError("Language should be mandatory");
            }
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            updateUserLanguage();
        }else{
            if(this.oustApiListener!=null) {
                this.oustApiListener.onError("User is not loggedIN");
            }
        }
    }

    public void oustUpdateUserLanguageWithAppContext(Context context, OustAuthData oustAuthData, String language) throws OustException{
        Log.d(TAG,"OustLogin");
        if (context == null) {
            throw new NullActivityException();
        }
        this.context = context;
        OustSdkApplication.setmContext(context);
        this.oustApiListener = null;

        //this.oustApiListener = (OustApiListener) context;
        //this.oustLoginCallBack = (OustLoginCallBack) activity;

        this.userName = oustAuthData.getUsername();
        this.orgId = oustAuthData.getOrgId();
        this.language = language;

        if(userName==null || userName.isEmpty()){
            return;
        }
        if(language==null){
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(context);
        }
        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            updateUserLanguage();
        }
    }

    public void updateUserLanguage() {
        Log.d("updateLanguage",""+language);

        String url = OustSdkApplication.getContext().getResources().getString(R.string.user_preferred_language_update);
        url = url.replace("{language}",""+language);
        url = url.replace("{userId}",""+userName);

        final String eventUrl = HttpManager.getAbsoluteUrl(url);

        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);*/

        JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
        Log.d("updateLanguage", "updateLanguage: "+jsonParams.toString());
        Log.d("updateLanguage", "updateLanguage: "+eventUrl);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateLanguage","Response:"+response.toString());
                if(response!=null && response.optBoolean("success")){
                    if(oustApiListener!=null){
                        oustApiListener.onLanguageUpdated();
                    }
                }else{
                    if(oustApiListener!=null) {
                        oustApiListener.onError("Failed to update the language");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("updateLanguage", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(oustApiListener!=null) {
                    oustApiListener.onError("Failed to update the language");
                }
                //onError("Failed to update the language");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", orgId);
                    //params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("updateLanguage","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public void oustUpdateUserGroupID(Activity activity, String orgId, String userId, String groupId) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        this.userName = userId;
        this.orgId = orgId;

        if(userName==null || userName.isEmpty()){
            if(oustApiListener!=null){
                oustApiListener.onError("UserId should be mandatory");
            }
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            updateUserGroupId(orgId, userId, groupId, null);
        }else{
            if(this.oustApiListener!=null) {
                this.oustApiListener.onError("User is not loggedIN");
            }
        }
    }

    public void oustUpdateUserDisplayName(Activity activity, UserUpdate userUpdate, String orgId,String userId) throws OustException{
        Log.d(TAG,"OustLogin");
        if (activity == null) {
            throw new NullActivityException();
        }
        this.activity = activity;
        OustSdkApplication.setmContext(activity);
        this.oustApiListener = (OustApiListener) activity;
        this.userName = userId;
        this.orgId = orgId;

        if(userName==null || userName.isEmpty()){
            if(oustApiListener!=null){
                oustApiListener.onError("UserId should be mandatory");
            }
            return;
        }

        if(userUpdate==null){
            if(oustApiListener!=null){
                oustApiListener.onError("First Name should be mandatory");
            }
            return;
        }


        if(userUpdate.getFname() == null||userUpdate.getFname().isEmpty()){
            if(oustApiListener!=null){
                oustApiListener.onError("First Name should be mandatory");
            }
            return;
        }

        isApiCall = true;
        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(activity);
        }

        boolean isUserLoggedIn = false;
        String userdata = OustPreferences.get("userdata");
        if ((userdata != null) && (!userdata.isEmpty())) {
            if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                if(activeUser.getStudentid().equals(userName)) {
                    isUserLoggedIn = true;
                }
            }
        }

        if(isUserLoggedIn){
            updateUserDisplayName(userUpdate,orgId,userId);
        }else{
            if(this.oustApiListener!=null) {
                this.oustApiListener.onError("User is not loggedIN");
            }
        }
    }

    public void updateUserDisplayName(UserUpdate userUpdate,String orgId,String userId) {
        Log.d("updateUserDisplayName",""+userId);

        String url = OustSdkApplication.getContext().getResources().getString(R.string.update_display_name)+userId;
        final String eventUrl = HttpManager.getAbsoluteUrl(url);

        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);*/
        final Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userUpdate);

        JSONObject jsonObject = OustSdkTools.getRequestObject(jsonParams);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateUserDisplayName","Response:"+response.toString());
                if(response!=null && response.optBoolean("success")){
                    if(oustApiListener!=null){
                        oustApiListener.onUserDisplayNameUpdated();
                    }
                }else{
                    if(oustApiListener!=null) {
                        oustApiListener.onError("Failed to update the user display name");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("updateUserDisplayName", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(oustApiListener!=null) {
                    oustApiListener.onError("Failed to update the user display name");
                }
                //onError("Failed to update the language");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", orgId);
                    //params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("updateLanguage","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public void updateUserGroupId(String orgId, String userId, String groupId, VerifyAndSignInResponse verifyAndSignInResponse) {
        if(groupId==null || groupId.isEmpty()){
            if(verifyAndSignInResponse!=null){
                authenticateWithFirebase(verifyAndSignInResponse);
            }
            return;
        }
        Log.d("updateUserGroupId",""+userId+" -- groupId:"+groupId);

        String url = OustSdkApplication.getContext().getResources().getString(R.string.update_group_id);
        url = url.replace("{groupid}",""+groupId);
        url = url.replace("{memberid}",""+userId);

        final String eventUrl = HttpManager.getAbsoluteUrl(url);

        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("studentid", oustAuthData.getUsername());
        JSONObject jsonObject = new JSONObject(postParam);*/
        /*final Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userUpdate);*/

        JSONObject jsonObject = OustSdkTools.getRequestObject(null);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, eventUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("updateUserGroupId","Response:"+response.toString());
                if(verifyAndSignInResponse!=null){
                    authenticateWithFirebase(verifyAndSignInResponse);
                }
                /*if(response!=null && response.optBoolean("success")){
                    if(oustApiListener!=null){
                        oustApiListener.onUserDisplayNameUpdated();
                    }
                }else{
                    if(oustApiListener!=null) {
                        oustApiListener.onError("Failed to update the user display name");
                    }
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("updateUserGroupId", "" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if(verifyAndSignInResponse!=null){
                    authenticateWithFirebase(verifyAndSignInResponse);
                }
                /*if(oustApiListener!=null) {
                    oustApiListener.onError("Failed to update the user display name");
                }*/
                //onError("Failed to update the language");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    //params.put("api-key", "test_secret_key");
                    params.put("org-Id", orgId);
                    //params.put("studentid",oustAuthData.getUsername());
                    //params.put("Content-Type","application/json; charset=utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                return params;
            }
        };

        try {
            Log.d("updateUserGroupId","Headers: "+jsonObjReq.getHeaders().toString());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }


    private void checkAWS(String orgId) {
        try {
            Log.d(TAG, "checkAWS: ");
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
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
