package com.oustme.oustsdk.api_sdk.handlers.services;

import android.app.Activity;
import android.util.Log;


import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.service.login.OustLoginApiCallBack;
import com.oustme.oustsdk.service.login.OustLoginService;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oust on 4/26/19.
 */

public class OustModuleDataHandler implements OustLoginApiCallBack {
    private OustModuleData moduledata;
    private OustApiListener oustApiListener;
    private OustAuthData oustAuthData;
    public OustModuleDataHandler(Activity activity, OustAuthData oustAuthData, OustModuleData moduledata) {
        this.moduledata = moduledata;
        this.oustApiListener = (OustApiListener)activity;
        this.oustAuthData = oustAuthData;
        if(oustAuthData.getLanguage()!=null){
            checkForUserLoginLanguage(activity, oustAuthData.getUsername(),oustAuthData.getfName(),oustAuthData.getlName(), oustAuthData.getOrgId(), oustAuthData.getLanguage());
        }else{
            checkForUserLogin(activity, oustAuthData.getUsername(), oustAuthData.getfName(),oustAuthData.getlName(),oustAuthData.getOrgId());
        }
    }

    //// Todo : Move realmHelper
    public void checkForUserLogin(Activity activity, String userName, String fName,String lName,String orgId) {
        OustFirebaseTools.initFirebase();
        //RealmHelper.setDefaultConfig(activity);
        if(OustPreferences.getAppInstallVariable("IsLoginServiceRunning")) {
            OustLoginService.setOustLoginApiCallBack(this);
        }else {
            OustLoginService oustLoginService = new OustLoginService(activity, userName, fName,lName,orgId, true, this);
            oustLoginService.checkInitDownload();
        }
    }

    public void checkForUserLoginLanguage(Activity activity, String userName, String fName,String lName,String orgId, String language) {
        OustFirebaseTools.initFirebase();
        //RealmHelper.setDefaultConfig(activity);
        if(OustPreferences.getAppInstallVariable("IsLoginServiceRunning")) {
            OustLoginService.setOustLoginApiCallBack(this);
        }else {
            OustLoginService oustLoginService = new OustLoginService(activity, userName, fName,lName,orgId, language, true, this);
            oustLoginService.checkInitDownload();
        }
    }

    private void getConstants() {
        try {
            AppConstants.MediaURLConstants.init();
            AppConstants.StringConstants.init();
            Log.d("TAG", "getConstants: MEDIA_SOURCE_BASE_URL:" + AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL);
            Log.d("TAG", "getConstants: CLOUD_FRONT_BASE_PATH:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
            Log.d("TAG", "getConstants: CLOUD_FRONT_BASE_HTTPs:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
            Log.d("TAG", "getConstants: S3_BUCKET_NAME:"+AppConstants.MediaURLConstants.BUCKET_NAME);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void performModuleDataOperation() {
        if(moduledata==null){
            moduledata = new OustModuleData("","","next");
        }
        OustFirebaseTools.initFirebase();
        getConstants();

        try{
            long previousPingOn = OustPreferences.getTimeForNotification("SentPingRequestTime");
            Log.d("PingRequest", "performModuleDataOperation: 86400000 --- prevPing:"+previousPingOn);
            if(previousPingOn>0){
                long timeDuration = System.currentTimeMillis() - previousPingOn;
                Log.d("PingRequest", "performModuleDataOperation --- timeDuration:" + timeDuration + " --- Oneday:" + 86400000);
                if (timeDuration >= 86400000) {
                    OustSdkTools.sendPingApiWithStudentId(oustAuthData.getUsername());
                }
            }else {
                OustSdkTools.sendPingApiWithStudentId(oustAuthData.getUsername());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (moduledata.getRequestType().equalsIgnoreCase("next")) {
            new OustApiRequest(oustApiListener).getNextModuleData(oustAuthData,moduledata);
        }else if(moduledata.getRequestType().equalsIgnoreCase("status")){
            new OustApiRequest(oustApiListener).getModuleStatusReport(oustAuthData,moduledata);

        }else if(moduledata.getRequestType().equalsIgnoreCase("EventStatus") || moduledata.getRequestType().equalsIgnoreCase("LaunchModule")) {
            Map<String, Object> postParam = new HashMap<>();
            try {
                postParam.put("eventId", Integer.parseInt(moduledata.getId()));
                if(oustAuthData.getLanguage()!=null) {
                    postParam.put("userlangauge", oustAuthData.getLanguage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                oustApiListener.onError("Event ID should be a integer");
                return;
            }

            if (moduledata.getRequestType().equalsIgnoreCase("LaunchModule")) {
                postParam.put("autoDistribute", true);
            } else {
                postParam.put("autoDistribute", false);
            }
            new OustApiRequest(oustApiListener).getEventModuleReport(oustAuthData, new JSONObject(postParam), moduledata.getRequestType());

            //EventType  0:getEventStatus 1:LaunchMode

            /*JSONObject jsonObject = new JSONObject();
            try {
                String json = "\"eventId\":"+Integer.parseInt(moduledata.getId())+",\"userId\":"+oustAuthData.getUsername()+",\"autoDistribute\":"+false;
                //jsonObject = new JSONObject("{"+json+"}");
                Log.d("Event","Sring"+json);

                jsonObject.put("eventId", Integer.parseInt(moduledata.getId()));
                jsonObject.put("userId", oustAuthData.getUsername());
                if(moduledata.getRequestType().equalsIgnoreCase("LaunchModule")){
                    jsonObject.put("autoDistribute",true);
                    eventUrl = ApiConstants.GET_EVENT_DATA_URL;
                }else{
                    jsonObject.put("autoDistribute",false);
                }

                Log.d("Event","JSOn:"+jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }*/
        } else if(moduledata.getRequestType().equalsIgnoreCase("LaunchCatalogue")){
            new OustApiRequest(oustApiListener).launchCatalogue();

        } else if(moduledata.getRequestType().equalsIgnoreCase("LaunchWithLanguage")){
            new OustApiRequest(oustApiListener).launchCourseCard();

        } else if(moduledata.getRequestType().equalsIgnoreCase("LaunchCPL")){
            new OustApiRequest(oustApiListener).distributeCplAndLaunch(oustAuthData, moduledata.getGroup());

        } else if(moduledata.getRequestType().equalsIgnoreCase("launchCourse")){
            new OustApiRequest(oustApiListener).launchSpecialCourse(oustAuthData);

        } else if(moduledata.getRequestType().equalsIgnoreCase("GetLanguage")){
            new OustApiRequest(oustApiListener).getLanguage(oustAuthData);

        } else {
            new OustIntentHandler(moduledata).launchNewIntent();
        }
    }

    @Override
    public void onLoginApiComplete() {
        Log.d("Login", "onLoginApiComplete: ");

        /*if(oustApiListener!=null){
            oustApiListener.onLoginSuccess(true);
        }*/
        performModuleDataOperation();
    }

    @Override
    public void onLoginApiError(String s) {
        if(oustApiListener!=null){
            oustApiListener.onLoginError(s);
        }
    }

    @Override
    public void onLoginStart() {
        if(oustApiListener!=null){
            oustApiListener.onLoginProcessStart();
        }
    }

    @Override
    public void onError(String s) {
        if(oustApiListener!=null){
            oustApiListener.onError(s);
        }
    }

    @Override
    public void onDownloadProgress(int i) {
        if(oustApiListener!=null){
            oustApiListener.onProgressChanged(i);
        }
    }
}
