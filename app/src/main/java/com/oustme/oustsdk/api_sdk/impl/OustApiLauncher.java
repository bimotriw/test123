package com.oustme.oustsdk.api_sdk.impl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.launcher.OustNewLauncher;
import com.oustme.oustsdk.request.UserUpdate;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by oust on 4/24/19.
 */

public class OustApiLauncher {
    private static final String TAG = "OustApiLauncher";
    public static OustApiLauncher oustApiLauncher;

    private OustApiLauncher() {

    }

    public static OustApiLauncher newInstance() {
        if (oustApiLauncher == null) {
            synchronized (OustApiLauncher.class) {
                if (oustApiLauncher == null) {
                    oustApiLauncher = new OustApiLauncher();
                }
            }
        }
        return oustApiLauncher;
    }

    public void launchOust(Activity activity, String userName, String orgId) {
        OustAuthData authData = new OustAuthData(orgId, userName, "");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchBasicOust(activity, authData);
    }

    public void launchOustApiService(Activity activity, String userName, String orgId, OustModuleData oustModuleData) {
        OustAuthData authData = new OustAuthData(orgId, userName, "");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void launchOustEventApiService(Activity activity, String userId, String orgId, String eventId, String event) {
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData(eventId, "", event);
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void getStatus(Activity activity, String userId, String orgId, String eventId) {
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData(eventId, "", "EventStatus");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void getStatusWithLanguage(Activity activity, String userId, String orgId, String eventId, String language) {
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        OustModuleData oustModuleData = new OustModuleData(eventId, "", "EventStatus");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void launchModule(Activity activity, String userId, String orgId, String eventId) {
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData(eventId, "", "LaunchModule");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void launchModuleWithLanguage(Activity activity, String userId, String orgId, String eventId, String language) {
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        OustModuleData oustModuleData = new OustModuleData(eventId, "", "LaunchModule");
        OustSdkApplication.setmContext(activity);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void initResources(Activity activity, String orgId, OustApiListener oustApiListener) {
        Log.d("Init","Resource");
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData();
        authData.setOrgId(orgId);
        try {
            OustSdkApplication.setmContext(activity);
            OustFirebaseTools.initFirebase();
            //RoomHelper.setDefaultConfig(activity);
            OustNewLauncher.getInstance().launchInit(activity, authData, oustApiListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initResourcesWithContext(Context context, String orgId) {
        Log.d("Init","initResourcesWithContext");
        OustSdkApplication.setmContext(context);
        OustAuthData authData = new OustAuthData();
        authData.setOrgId(orgId);
        try {
            OustSdkApplication.setmContext(context);
            OustFirebaseTools.initFirebase();
            //RoomHelper.setDefaultConfig(context);
            OustNewLauncher.getInstance().launchInitWithContext(context, authData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeResources(Activity activity){
        Log.d("Remove","Resource");
        try {
            OustSdkApplication.setmContext(activity);
            OustNewLauncher.getInstance().removeResources(activity);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeResourcesWithAppContext(Context context){
        Log.d("Remove","Resource");
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().removeResourcesWithAppContext(context);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLogout(Activity activity){
        Log.d("Logout","User");
        try {
            OustSdkApplication.setmContext(activity);
            OustNewLauncher.getInstance().oustLogout(activity);
            OustPreferences.clearAll();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLogoutwithAppContext(Context context){
        Log.d("Logout","User");
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustLogoutWithAppContext(context);
            OustPreferences.clearAll();
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLogin(Activity activity, String orgId, String userId){
        Log.d("Login","User");
        try {
            OustSdkApplication.setmContext(activity);
            OustNewLauncher.getInstance().oustLogout(activity);
            OustNewLauncher.getInstance().oustLogin(activity, orgId, userId);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLoginWithContext(Context context, String orgId, String userId,String fName,String lName){
        Log.d("Login","User with context");
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustLogoutWithContext(context, true);
            OustNewLauncher.getInstance().oustLoginWithContext(context, orgId, userId,fName,lName, true);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void isUserLoggedInOust(Context context){
        try {
            OustSdkApplication.setmContext(context);
            boolean isLoggedIn = OustNewLauncher.getInstance().checkUserAlreadyLoggedIn(context);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLoginWithAppContext(Context context, String orgId, String userId,String fName,String lName){
        Log.d("Login","User with context");
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustLogoutWithContext(context, false);
            OustNewLauncher.getInstance().oustLoginWithContext(context, orgId, userId,fName,lName, false);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLoginAndLaunch(Activity activity, String orgId, String userId, String language){
        //Log.d("Login","User with language and Launch");
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchWithLanguage");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void oustLoginWithLanguage(Activity activity, String orgId, String userId,String fName,String lName, String language){
        Log.d("Login","User with language");
        try {
            OustSdkApplication.setmContext(activity);
            OustNewLauncher.getInstance().oustLoginWithLanguage(activity, orgId, userId, fName,lName,language);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLanguageLoginWithAppContext(Context context, String orgId, String userId,String fName,String lName, String language){
        Log.d("Login","User with language");
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustLanguageLoginWithAppContext(context, orgId, userId, fName,lName,language);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void launchCPL(Activity activity, String orgId, String userId, String language, String group){
        group = group.toUpperCase();
        Log.d("Oust","launchCPL:"+group);
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        if(language!=null && !language.isEmpty() && !language.equals("")){
            authData.setLanguage(language);
        }
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchCPL", group);
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);

        try {
            OustNewLauncher.getInstance().oustLoginWithLanguage(activity, orgId, userId,"","", language);
        }catch (OustException e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateOustLanguage(Activity activity, String orgId, String userId, String language){
        Log.d(TAG, "updateOustLanguage: "+language);
        try {
            OustAuthData authData = new OustAuthData(orgId, userId, "");
            OustNewLauncher.getInstance().oustUpdateLanguage(activity, authData, language);
        }catch (OustException e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void oustLaunchCatalogueWithLanguage(final Activity activity, String orgId, String userId, String language) {
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchCatalogue");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void oustLaunchCatalogue(final Activity activity, String orgId, String userId){
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchCatalogue");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);

        /*String key = OustPreferences.get(STUDE_KEY);
        if(key!=null)
            OustFirebaseTools.getRootRef().child("landingPage/"+key+"/catalogueId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null && (dataSnapshot.getValue()!=null) && (long)dataSnapshot.getValue()!=0) {
                        OustPreferences.saveTimeForNotification(CATALOGUE_ID, (long)dataSnapshot.getValue());
                        Intent intent = new Intent(activity, NewCatalogActivity.class);
                        intent.putExtra(CATALOG_NAME, OustPreferences.get(CATALOG_NAME));
                        intent.putExtra("hasDeskData", false);
                        //intent.putExtra("deskDataMap", myDeskInfoMap);
                        activity.startActivity(intent);
                    }
                    else {
                        Log.d(TAG, "onDataChange: No catalogue distributed");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        Log.d("Login","User");*/
    }

    /*public void oustRegisterAndLaunch(final Activity activity, String orgId, String userId, String language) {
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchRegister");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }*/

    public void launchCourse(Activity activity, String userId, String orgId) {
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData("", "", "LaunchCourse");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }

    public void updateUserPreferredLanguage(Activity activity, String userId, String orgId, String language) {
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        try {
            OustSdkApplication.setmContext(activity);
            OustNewLauncher.getInstance().oustUpdateUserLanguage(activity, authData, language);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    public void updateUserDisplayName(Activity context, String orgId, String userId,String fName,String lName) {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setFname(fName);
        userUpdate.setLname(lName);
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustUpdateUserDisplayName(context, userUpdate, orgId,userId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateUserPreferredLanguageWithAppContext(Context context, String userId, String orgId, String language) {
        OustAuthData authData = new OustAuthData(orgId, userId, "", language);
        try {
            OustSdkApplication.setmContext(context);
            OustNewLauncher.getInstance().oustUpdateUserLanguageWithAppContext(context, authData, language);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserPreferredLanguage(Activity activity, String userId, String orgId) {
        OustSdkApplication.setmContext(activity);
        OustAuthData authData = new OustAuthData(orgId, userId, "");
        OustModuleData oustModuleData = new OustModuleData("", "GetLanguage");
        new OustModuleImpl().launchOustApi(activity, authData, oustModuleData);
    }
}
