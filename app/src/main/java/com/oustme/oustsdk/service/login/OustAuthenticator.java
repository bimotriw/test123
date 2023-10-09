package com.oustme.oustsdk.service.login;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOGUE_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOG_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CAT_BANNER;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.DISABLE_LEARNING_DIARY;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.STUDE_KEY;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

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
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.api_sdk.models.OustCatalogueData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.response.common.LoginType;
import com.oustme.oustsdk.response.common.VerifyAndSignInResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oust on 4/26/19.
 */

public class OustAuthenticator {

    private static final String TAG = "OustAuthenticator";
    private final Context context;
    private static OustLoginApiCallBack oustLoginApiCallBack;
    private final boolean isActivityActive;
    private final boolean isFirstTime = false;
    private final String orgId;

    public static void setOustLoginApiCallBack(OustLoginApiCallBack oustLoginApiCallBack) {
        OustAuthenticator.oustLoginApiCallBack = oustLoginApiCallBack;
    }

    public OustAuthenticator(Context context, String orgId, OustLoginApiCallBack oustLoginApiCallBack, boolean isActivityActive) {
        this.context = context;
        this.orgId = orgId;
        OustAuthenticator.oustLoginApiCallBack = oustLoginApiCallBack;
        this.isActivityActive = isActivityActive;
    }

    private void sendErrorMessageToListener(String message) {
        OustAppState.getInstance().setLandingPageOpen(false);
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);

        if (oustLoginApiCallBack != null)
            oustLoginApiCallBack.onLoginApiError((message == null || message.isEmpty() || message.equals("")) ? "Something went wrong!!" : message);
    }

    /*public void authenticateWithFirebase(final String token, final SignInResponse signInResponse) {
        try {
            FirebaseAuth mAuth = null;
            OustPreferences.save("firebaseToken", token);
            if (token != null && !token.isEmpty()) {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    Log.e("firebase app name", firebaseApp.getName());
                    mAuth = FirebaseAuth.getInstance(firebaseApp);
                } else {
                    mAuth = FirebaseAuth.getInstance();
                }
                mAuth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task != null) {
                                    if (!task.isSuccessful()) {
                                        sendErrorMessageToListener("Firebase authentication error");
                                        //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
                                        if (isActivityActive) {
                                            firebaseAuntheticationFailed();
                                        }
                                    } else {
                                        if (signInResponse != null) {
                                            OustPreferences.save("firebaseToken", signInResponse.getFirebaseToken());
                                            Gson gson = new Gson();
                                            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getActiveUser(signInResponse)));
                                            OustPreferences.save("isLoggedIn", "true");
                                            OustPreferences.save("loginType", LoginType.Oust.toString());
                                        }
                                        onAuthComplete();

                                    }
                                } else {
                                    sendErrorMessageToListener("Firebase authentication error");
                                    //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
                                    if (isActivityActive) {
                                        firebaseAuntheticationFailed();
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            sendErrorMessageToListener("Firebase authentication error");
            //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void authenticateWithFirebase(final String token, final VerifyAndSignInResponse verifyAndSignInResponse) {
        try {
            FirebaseAuth mAuth = null;
            OustPreferences.save("firebaseToken", token);
            if (token != null && !token.isEmpty()) {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    Log.e("firebase app name", firebaseApp.getName());
                    mAuth = FirebaseAuth.getInstance(firebaseApp);
                } else {
                    mAuth = FirebaseAuth.getInstance();
                }
                mAuth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task != null) {
                            if (!task.isSuccessful()) {
                                sendErrorMessageToListener("Firebase authentication error");
                                //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
                                if (isActivityActive) {
                                    firebaseAuntheticationFailed();
                                }
                            } else {
                                if (verifyAndSignInResponse != null) {
                                    OustPreferences.save("firebaseToken", verifyAndSignInResponse.getFirebaseToken());
                                    Gson gson = new Gson();
                                    OustSdkTools.getInstance();
                                    OustPreferences.save("userdata", gson.toJson(OustSdkTools.getNewActiveUser(verifyAndSignInResponse)));
                                    OustPreferences.save("isLoggedIn", "true");
                                    OustPreferences.save("loginType", LoginType.Oust.toString());
                                }
                                onAuthComplete();

                            }
                        } else {
                            sendErrorMessageToListener("Firebase authentication error");
                            //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
                            if (isActivityActive) {
                                firebaseAuntheticationFailed();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            sendErrorMessageToListener("Firebase authentication error");
            //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void onAuthComplete() {
        OustAppState.getInstance().setLandingPageOpen(false);
        String key = OustPreferences.get(STUDE_KEY);
        Log.d(TAG, "StudentId:" + key);
        if (key != null) {
            OustFirebaseTools.getRootRef().child("landingPage/" + key + "/catalogue").addListenerForSingleValueEvent(new ValueEventListener() {
                //OustFirebaseTools.getRootRef().child("landingPage/"+key+"/catalogueId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Log.d(TAG, "Data:" + dataSnapshot.toString());
                        if (dataSnapshot != null && (dataSnapshot.getValue() != null)) {
                            extractCatologeData(dataSnapshot);
                        }

                        /*OustPreferences.saveTimeForNotification(CATALOGUE_ID, (long)dataSnapshot.getValue());
                        Log.d(TAG, "onDataChange catalogue Id: "+(long)dataSnapshot.getValue());*/
                    } catch (Exception e) {
                        Log.d(TAG, "Exception on loading catalogueId");
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Database error : " + databaseError.getDetails());
                }
            });
        }
        getUserAppConfiguration();
        getConstants();
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);
        OustAppState.getInstance().setLandingPageOpen(false);
        if (oustLoginApiCallBack != null)
            oustLoginApiCallBack.onLoginApiComplete();
    }

    private void getConstants() {
        try {
            AppConstants.MediaURLConstants.init();
            AppConstants.StringConstants.init();
            Log.d(TAG, "getConstants: MEDIA_SOURCE_BASE_URL:" + AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL);
            Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_PATH:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
            Log.d(TAG, "getConstants: CLOUD_FRONT_BASE_HTTPs:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
            Log.d(TAG, "getConstants: S3_BUCKET_NAME:" + AppConstants.MediaURLConstants.BUCKET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCatologeData(DataSnapshot dataSnapshot) {
        try {
            long catalogue_id = 0;
            OustCatalogueData oustCatalogueData = new OustCatalogueData();
            Object o1 = dataSnapshot.getValue();
            if (o1.getClass().equals(ArrayList.class)) {
                List<Object> lpMainList = (List<Object>) dataSnapshot.getValue();
                Map<String, Object> lpMap = (Map<String, Object>) lpMainList.get(0);
                if (lpMap != null) {
                    if (lpMap.containsKey("bannerImg")) {
                        oustCatalogueData.setBanner((String) lpMap.get("bannerImg"));
                    }

                    if (lpMap.containsKey("elementId")) {
                        oustCatalogueData.setCatalogueId(OustSdkTools.convertToLong(lpMap.get("elementId")));
                        catalogue_id = OustSdkTools.convertToLong(lpMap.get("elementId"));
                    }

                    if (lpMap.containsKey("name")) {
                        oustCatalogueData.setTitle((String) lpMap.get("name"));
                    }
                }

            } else if (o1.getClass().equals(HashMap.class)) {
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
                    catalogue_id = OustSdkTools.convertToLong(mainMap.get("elementId"));
                }

                if (mainMap.containsKey("name")) {
                    oustCatalogueData.setTitle((String) mainMap.get("name"));
                }
            }

            if (oustCatalogueData.getBanner() != null) {
                OustPreferences.save(CAT_BANNER, oustCatalogueData.getBanner());
            }
            if (oustCatalogueData.getTitle() != null) {
                OustPreferences.save(CATALOG_NAME, oustCatalogueData.getTitle());
            }

            if (catalogue_id > 0) {
                OustPreferences.saveTimeForNotification(CATALOGUE_ID, oustCatalogueData.getCatalogueId());
            }
            Log.d(TAG, "CatalogueId:" + catalogue_id);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void getUserAppConfiguration() {
        Log.e(TAG, "inside getUserAppConfiguration method");
        String message = "/system/appConfig";
        Log.d(TAG, "getUserAppConfiguration: " + message);
        ValueEventListener appConfigListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        Map<String, Object> appConfigMap = (Map<String, Object>) snapshot.getValue();
                        if (appConfigMap.get("isPlayModeEnabled") != null) {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", (boolean) appConfigMap.get("isPlayModeEnabled"));
                            //getPlayGameData();
                        } else {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", false);
                        }
                        if (appConfigMap.get(AppConstants.StringConstants.CAT_BANNER) != null) {
                            OustPreferences.save(AppConstants.StringConstants.CAT_BANNER, (String) appConfigMap.get(AppConstants.StringConstants.CAT_BANNER));
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
                        }
                        if (appConfigMap.get("panelColor") != null) {
                            OustPreferences.save("toolbarColorCode", (String) appConfigMap.get("panelColor"));
                            //toolbarColorCode = (String) appConfigMap.get("panelColor");
                            Log.e(TAG, "got panel color from appConfigMap" + appConfigMap.get("panelColor"));
                        } else {
                            OustPreferences.save("toolbarColorCode", "#ff01b5a2");
                            //toolbarColorCode = "#ff01b5a2";
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

                        if (appConfigMap.get("panelLogo") != null) {
                            OustPreferences.save("panelLogo", ((String) appConfigMap.get("panelLogo")));
                            Log.e(TAG, "got panel logo " + appConfigMap.get("panelLogo"));
                        } else {
                            OustPreferences.clear("panelLogo");
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
                        //if (isFirstTime) {
                        if (appConfigMap.get("layout") != null) {
                            if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_1")) {
                                OustStaticVariableHandling.getInstance().setIsNewLayout(1);
                            } else if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_2")) {
                                OustStaticVariableHandling.getInstance().setIsNewLayout(2);
                            } else if (((String) appConfigMap.get("layout")).equalsIgnoreCase("LAYOUT_3")) {
                                Log.d(TAG, "onDataChange: layout 3");
                                OustStaticVariableHandling.getInstance().setIsNewLayout(3);
                            }
                        }
                        OustPreferences.saveintVar("isNewLayout", OustStaticVariableHandling.getInstance().getIsNewLayout());
                        //isFirstTime = false;
                        //}
                        if (appConfigMap.get("liveTraining") != null) {
                            OustPreferences.saveAppInstallVariable("liveTraining", ((boolean) appConfigMap.get("liveTraining")));
                        } else {
                            OustPreferences.clear("liveTraining");
                        }

                        if (appConfigMap.get("features") != null) {
                            Map<String, Object> featuresMap = (Map<String, Object>) appConfigMap.get("features");
                            if (featuresMap != null) {

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

                                if (featuresMap.get("showCorn") != null) {
                                    Log.d(TAG, "Yes has showCorn");
                                    OustPreferences.saveAppInstallVariable("showCorn", ((boolean) featuresMap.get("showCorn")));
                                } else {
                                    Log.d(TAG, "No showCorn");
                                    OustPreferences.clear("showCorn");
                                }

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB) != null) {
                                    Log.e(TAG, "onDataChange: SHOW_CPL_COURSE_TAB--> " + featuresMap.get(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB));
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
                                    Log.e(TAG, "onDataChange: SHOW_COMMUNICATION--> " + featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION));
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_COMMUNICATION)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_COMMUNICATION, false);
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

                                if (featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, ((boolean) featuresMap.get(AppConstants.StringConstants.FEED_SHARE_DISABLE)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE, false);
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
                                if (featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE) != null) {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, ((long) featuresMap.get(AppConstants.StringConstants.MAX_DATE_RANGE)));
                                } else {
                                    OustPreferences.saveTimeForNotification(AppConstants.StringConstants.MAX_DATE_RANGE, 0);
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

                                if (featuresMap.get("showNewNoticeboardUI") != null) {
                                    OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", ((boolean) featuresMap.get("showNewNoticeboardUI")));
                                } else {
                                    OustPreferences.saveAppInstallVariable("showNewNoticeboardUI", false);
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

                                if (featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED) != null) {
                                    Log.d(TAG, "Yes has AppConstants.StringConstants.IS_PREV_DATE_ALLOWED");
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, ((boolean) featuresMap.get(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED)));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.IS_FUTURE_DATE_ALLOWED, false);
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
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FAQ, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FAQ));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.SHOW_FAQ);
                                }
                                if (featuresMap.get(AppConstants.StringConstants.URL_FAQ) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.URL_FAQ, (String) featuresMap.get(AppConstants.StringConstants.URL_FAQ));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.URL_FAQ);
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
                                                    //downloadImages(CourseStartIconURL, fileName);

                                                } else {
                                                    OustPreferences.clear("DEFAULT_START_ICON");
                                                    OustPreferences.clear(AppConstants.StringConstants.COURSE_START_ICON);

                                                }
                                                if (customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON) != null) {
                                                    String courseFinishIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_FINISH_ICON);
                                                    String fileName = OustMediaTools.getMediaFileName(courseFinishIconURL);
                                                    OustPreferences.save("DEFAULT_FINISH_ICON", fileName);
                                                    OustPreferences.save(AppConstants.StringConstants.COURSE_FINISH_ICON, courseFinishIconURL);
                                                    //downloadImages(courseFinishIconURL, fileName);
                                                } else {
                                                    OustPreferences.clear("DEFAULT_FINISH_ICON");
                                                    OustPreferences.clear(AppConstants.StringConstants.COURSE_FINISH_ICON);

                                                }
                                                if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON) != null) {
                                                    String courseLevelIconURL = (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                                    String fileName = OustMediaTools.getMediaFileName(courseLevelIconURL);
                                                    OustPreferences.save("DEFAULT_INDICATOR_ICON", fileName);
                                                    OustPreferences.save(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON, courseLevelIconURL);
                                                    //downloadImages(courseLevelIconURL, fileName);
                                                } else {
                                                    OustPreferences.clear("DEFAULT_INDICATOR_ICON");
                                                    OustPreferences.clear(AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON);
                                                }
                                                if (customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR) != null) {
                                                    OustPreferences.save(AppConstants.StringConstants.COURSE_LOCK_COLOR, (String) customCourseBranding.get(AppConstants.StringConstants.COURSE_LOCK_COLOR));
                                                } else {
                                                    OustPreferences.clear(AppConstants.StringConstants.COURSE_LOCK_COLOR);

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
                                        boolean val = (boolean) featuresMap.get(AppConstants.StringConstants.CUSTOM_COURSE_BRANDING);
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }

                                    //OustPreferences.save(AppConstants.StringConstants.CPL_LANG_CHANGE, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.CPL_LANG_CHANGE)));
                                } else {
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

                                if (featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, (boolean) featuresMap.get(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI, false);
                                }
                                Log.d(TAG, "onDataChange: SHOW_NEW_ASSESSMENT_UI:" + OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI));
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

                    }
                    Log.e(TAG, "about to reach setToolBArCOLOR method");
                } catch (Exception e) {
                    Log.e(TAG, "Error while saving app config data", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error + "");
            }
        };
        OustFirebaseTools.getRootRef().

                child(message).

                keepSynced(true);
        OustFirebaseTools.getRootRef().

                child(message).

                addValueEventListener(appConfigListener);
        OustAppState.getInstance().

                getFirebaseRefClassList().

                add(new FirebaseRefClass(appConfigListener, message));
    }

    private void getUserAppConfiguration1() {
        Log.e(TAG, "inside getUserAppConfiguration method");
        String message = "/system/appConfig";
        Log.d(TAG, "getUserAppConfiguration: " + message);
        ValueEventListener appConfigListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    Log.e(TAG, "inside onDataChange method");
                    if (null != snapshot.getValue()) {
                        Map<String, Object> appConfigMap = (Map<String, Object>) snapshot.getValue();
                        if (appConfigMap.get("isPlayModeEnabled") != null) {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", (boolean) appConfigMap.get("isPlayModeEnabled"));
                            //getPlayGameData();
                        } else {
                            OustPreferences.saveAppInstallVariable("isPlayModeEnabled", false);
                        }
                        if (appConfigMap.get(AppConstants.StringConstants.CAT_BANNER) != null) {
                            OustPreferences.save(CAT_BANNER, (String) appConfigMap.get(CAT_BANNER));
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
                        }
                        if (appConfigMap.get("panelColor") != null) {
                            OustPreferences.save("toolbarColorCode", (String) appConfigMap.get("panelColor"));
                            // toolbarColorCode = (String) appConfigMap.get("panelColor");
                            Log.e(TAG, "got panel color from appConfigMap" + appConfigMap.get("panelColor"));
                        } else {
                            OustPreferences.save("toolbarColorCode", "#11000000");
                            //toolbarColorCode = "#ff01b5a2";
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

                        if (appConfigMap.get("panelLogo") != null) {
                            OustPreferences.save("panelLogo", ((String) appConfigMap.get("panelLogo")));
                            Log.e(TAG, "got panel logo " + appConfigMap.get("panelLogo"));
                        } else {
                            OustPreferences.clear("panelLogo");
                        }
                        //setToolBarColorAndIcons();

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
                            //setLayoutAspectRatiosmall(newmainlanding_topsublayout, 0);
                        } else {
                            OustPreferences.clear("userAreaBgImg");
                        }
                        if (appConfigMap.get("redirectIcon") != null) {
                            String redirectIcon = ((String) appConfigMap.get("redirectIcon"));
                            OustPreferences.save("redirectIcon", redirectIcon);
                            //Log.e(TAG, "redirect icon " + redirectIcon);
                            // setHostAppIcon();
                        } else {
                            OustPreferences.clear("redirectIcon");
                            // setHostAppIcon();
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

                        if (appConfigMap.get("features") != null) {
                            Map<String, Object> featuresMap = (Map<String, Object>) appConfigMap.get("features");
                            if (featuresMap != null) {

                                if (featuresMap.get("disableUser") != null) {
                                    Log.d(TAG, "Yes has disableUser");
                                    OustPreferences.saveAppInstallVariable("disableUser", ((boolean) featuresMap.get("disableUser")));
                                } else {
                                    Log.d(TAG, "No disableUser");
                                    OustPreferences.clear("disableUser");
                                }

                                if (featuresMap.get("showCorn") != null) {
                                    Log.d(TAG, "Yes has showCorn");
                                    OustPreferences.saveAppInstallVariable("showCorn", ((boolean) featuresMap.get("showCorn")));
                                } else {
                                    Log.d(TAG, "No showCorn");
                                    OustPreferences.clear("showCorn");
                                }

                                if (featuresMap.get("disableCourse") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCourse", ((boolean) featuresMap.get("disableCourse")));
                                } else {
                                    OustPreferences.clear("hideCourse");
                                }
                                if (featuresMap.get("disableCatalogue") != null) {
                                    OustPreferences.saveAppInstallVariable("hideCatalog", ((boolean) featuresMap.get("disableCatalogue")));
                                } else {
                                    OustPreferences.clear("hideCatalog");
                                }
                                if (featuresMap.get("showCplLanguageInNavigation") != null) {
                                    OustPreferences.saveAppInstallVariable("showCplLanguageInNavigation", (boolean) featuresMap.get("showCplLanguageInNavigation"));
                                } else {
                                    OustPreferences.clear("showCplLanguageInNavigation");
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

                                if (featuresMap.get("learningDiaryName") != null) {
                                    OustPreferences.save("learningDiaryName", OustSdkTools.convertToStr(featuresMap.get("learningDiaryName")));
                                } else {
                                    OustPreferences.clear("learningDiaryName");
                                }

                                if (featuresMap.get(AppConstants.StringConstants.CATALOG_NAME) != null) {
                                    OustPreferences.save(AppConstants.StringConstants.CATALOG_NAME, OustSdkTools.convertToStr(featuresMap.get(AppConstants.StringConstants.CATALOG_NAME)));
                                } else {
                                    OustPreferences.clear(AppConstants.StringConstants.CATALOG_NAME);
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
                                    Log.d(TAG, "onDataChange: DISABLE_LEARNING_DIARY:" + OustPreferences.getAppInstallVariable(DISABLE_LEARNING_DIARY));
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.DISABLE_LEARNING_DIARY, true);
                                }
                                // addDrower();

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

                                if (featuresMap.get("showLanguageScreen") != null) {
                                    // isLanguageScreenenabled = (boolean) featuresMap.get("showLanguageScreen");
                                    if ((boolean) featuresMap.get("showLanguageScreen") && !OustPreferences.getAppInstallVariable(IS_LANG_SELECTED)) {
                                        if (featuresMap.get("cplIdForLanguage") != null) {
                                            //  OpenLanguageScreen((long) featuresMap.get("cplIdForLanguage"),false);
                                        } else {
                                            OustPreferences.saveLongVar("cplIdForLanguage", 0);
                                        }
                                    }
                                }

                                // feature to test showing FFC on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)));
                                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE)) {
                                        //  isContest = true;
                                    } else {
                                        // isContest = false;
                                        OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);

                                    }
                                    //initBottomSheetBannerData();
                                } else {
                                    // isContest = false;
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_FFC_ON_LANDING_PAGE, false);
                                }

                                // feature to test showing CPL on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE)));
                                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE)) {
                                        // isPlayList = true;
                                    } else {
                                        // isPlayList = false;
                                    }
                                    //initBottomSheetBannerData();
                                } else {
                                    //  isPlayList = false;
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_ON_LANDING_PAGE, false);
                                }

                                // feature to test showing To do on Landing Page
                                if (featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE) != null) {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, ((boolean) featuresMap.get(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE)));
                                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE)) {
                                        // isTodo = true;
                                    } else {
                                        // isTodo = false;
                                    }
                                    // initBottomSheetBannerData();
                                } else {
                                    OustPreferences.saveAppInstallVariable(AppConstants.StringConstants.SHOW_TO_DO_ON_LANDING_PAGE, false);
                                }

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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Error", error + "");
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(appConfigListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(appConfigListener, message));
    }

    public void authenticateWithFirebase() {
        try {
            FirebaseAuth mAuth = null;
            String token = OustPreferences.get("firebaseToken");
            if (token != null && !token.isEmpty()) {
                if (OustPreferences.get("tanentid") != null && !OustPreferences.get("tanentid").isEmpty()) {
                    FirebaseApp firebaseApp = FirebaseApp.getInstance(OustPreferences.get("tanentid"));
                    Log.e("firebase app name", firebaseApp.getName());
                    mAuth = FirebaseAuth.getInstance(firebaseApp);
                } else {
                    mAuth = FirebaseAuth.getInstance();
                }
                mAuth.signInWithCustomToken(token)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task != null) {
                                    if (!task.isSuccessful()) {
                                        if (!isFirstTime) {
                                            getNewTokenForFireBase();
                                        } else {
                                            firebaseAuntheticationFailed();
                                        }
                                    } else {
                                        onAuthComplete();
                                    }
                                } else {
                                    sendErrorMessageToListener("Firebase authentication error");
                                    //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
                                    if (isActivityActive) {
                                        firebaseAuntheticationFailed();
                                    }
                                }
                            }
                        });
            }
        } catch (Exception e) {
            sendErrorMessageToListener("Firebase authentication error");
            //oustLoginApiCallBack.onLoginApiError("Firebase authentication error");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
                            params.put("api-key", "test_secret_key");
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
                Log.e("VOLLEY", "Adding request object");
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
            }
        } else {
            OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);
            OustAppState.getInstance().setLandingPageOpen(false);
            if (oustLoginApiCallBack != null)
                oustLoginApiCallBack.onError("No network error!");

        }
    }

    private void gotFireBaseToken(JSONObject commonResponse) {
        if (commonResponse != null) {
            if (commonResponse.optBoolean("success")) {
                Log.e("firebaseToken taken", commonResponse.toString());
                String firebaseToken = commonResponse.optString("token");
                OustPreferences.save("firebaseToken", firebaseToken);
                authenticateWithFirebase();
            } else {
                OustPreferences.clear("userdata");
                OustPreferences.clear("tanentid");
            }
        } else {
            OustPreferences.clear("userdata");
            OustPreferences.clear("tanentid");
        }
    }


    public void firebaseAuntheticationFailed() {
        try {
            sendErrorMessageToListener("Failed to authenticate with firebase");
            //oustLoginApiCallBack.onLoginApiError("Failed to authenticate with firebase");
        } catch (Exception e) {

        }
    }
}
