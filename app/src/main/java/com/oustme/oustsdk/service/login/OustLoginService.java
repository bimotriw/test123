package com.oustme.oustsdk.service.login;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CATALOGUE_ID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CAT_BANNER;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.DISABLE_LEARNING_DIARY;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_LANG_SELECTED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.STUDE_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.launcher.OustExceptions.SecretKeyNotFound;
import com.oustme.oustsdk.request.VerifyAndSignInRequest;
import com.oustme.oustsdk.response.common.LoginType;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.response.common.VerifyAndSignInResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by oust on 4/24/19.
 */

public class OustLoginService implements DownloadInitListener {
    private String userName, fName, lName, orgId, language = null;
    private static OustLoginApiCallBack oustLoginApiCallBack;
    private static final String TAG = "OustLoginService";
    private SignInResponse signInResponse;
    private boolean isActivityActive = true;
    private Activity activity;
    private Context context;
    private boolean isFirebaseAuthReq = false;
    private float totalResources = 0;
    private float resourcesDownloaded = 0;
    private boolean isDownloadComplete = false;
    private int progress = 0;
    private OustAuthData oustAuthData;

    public OustLoginService(Activity activity, String userName, String fName, String lName, String orgId,
                            boolean isFirebaseAuthReq,
                            OustLoginApiCallBack oustLoginApiCallBack) {
        this.activity = activity;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.isFirebaseAuthReq = isFirebaseAuthReq;
        OustLoginService.oustLoginApiCallBack = oustLoginApiCallBack;
    }

    public OustLoginService(Context context, String userName, String fName, String lName, String orgId,
                            boolean isFirebaseAuthReq,
                            OustLoginApiCallBack oustLoginApiCallBack) {
        this.context = context;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.isFirebaseAuthReq = isFirebaseAuthReq;
        OustLoginService.oustLoginApiCallBack = oustLoginApiCallBack;
    }

    public OustLoginService(Activity activity, String userName, String fName, String lName, String orgId, String language,
                            boolean isFirebaseAuthReq,
                            OustLoginApiCallBack oustLoginApiCallBack) {
        this.activity = activity;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.language = language;
        this.isFirebaseAuthReq = isFirebaseAuthReq;
        OustLoginService.oustLoginApiCallBack = oustLoginApiCallBack;
    }

    public OustLoginService(Context context, String userName, String fName, String lName, String orgId, String language,
                            boolean isFirebaseAuthReq,
                            OustLoginApiCallBack oustLoginApiCallBack) {
        this.context = context;
        this.userName = userName;
        this.fName = fName;
        this.lName = lName;
        this.orgId = orgId;
        this.language = language;
        this.isFirebaseAuthReq = isFirebaseAuthReq;
        OustLoginService.oustLoginApiCallBack = oustLoginApiCallBack;
    }

    public static void setOustLoginApiCallBack(OustLoginApiCallBack oustLoginApiCallBack1) {
        Log.d(TAG, "setOustLoginApiCallBack: ");
        oustLoginApiCallBack = oustLoginApiCallBack1;
        try {
            OustAuthenticator.setOustLoginApiCallBack(oustLoginApiCallBack);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendErrorMessageToListener(String message) {
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);
        OustAppState.getInstance().setLandingPageOpen(false);

        if (oustLoginApiCallBack != null)
            oustLoginApiCallBack.onLoginApiError((message == null || message.isEmpty() || message.equals("")) ? "Something went wrong!!" : message);

    }

    public void checkInitDownload() {
        Log.d("checkInitDownload", "Check before Login!");
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", true);
        if (!OustStaticVariableHandling.getInstance().isDownloadingResources()) {
            if (context != null)
                new DownloadInitService(context, orgId, this);
            else
                new DownloadInitService(activity, orgId, this);
        } else {
            DownloadInitService.setListener(this);
        }
    }

    public void checkLoginProcess() {
        try {
            OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", true);
            Log.d(TAG, "login checks here :" + isDownloadComplete);
            if (!OustAppState.getInstance().isLandingPageOpen()) {
                OustAppState.getInstance().setLandingPageOpen(true);
                String userdata = OustPreferences.get("userdata");
                if ((userdata != null) && (!userdata.isEmpty())) {
                    if ((OustPreferences.get("tanentid") != null) && (!OustPreferences.get("tanentid").isEmpty())) {
                        ActiveUser activeUser = OustSdkTools.getActiveUserData(userdata);
                        if (activeUser != null && activeUser.getStudentid().equals(userName)) {
                            isActivityActive = false;
                            authenticate();
                        } else {
                            OustPreferences.clearAll();
                            startLoginProcess();
                        }
                        //oustLoginApiCallBack.onLoginApiComplete();
                    } else {
                        startLoginProcess();
                    }
                } else {
                    startLoginProcess();
                }
            }
        } catch (Exception e) {
            sendErrorMessageToListener("Login process failed");
            //oustLoginApiCallBack.onLoginApiError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startLoginProcess() {
        Log.d(TAG, "startLoginProcess");
        oustLoginApiCallBack.onLoginStart();
        userName = (userName.trim());
        if ((userName != null) && (userName.length() > 1)) {
            //verifyTenant(orgId);
            getBaseURL(orgId);
        } else {
            sendErrorMessageToListener("User-Id should be mandatory");
        }
    }

    private void getBaseURL(String orgId) {
        urlForBaseURLCheck = urlForBaseURLCheck.replace("{orgId}", orgId);
        new CheckBaseURL().execute(urlForBaseURLCheck);
    }

    String urlForBaseURLCheck = AppConstants.StringConstants.SIGN_URL_BASE;

    public class CheckBaseURL extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d(TAG, "doInBackground: " + urlForBaseURLCheck);
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

                if (statusCode == 301) {
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
            if (result == null) {
                sendErrorMessageToListener("Problem on loading Org url");
            }

            if (result != null && result.equalsIgnoreCase("405")) {
                sendErrorMessageToListener("Problem on loading Org url");

            } else if (result != null) {
                String delimeter = "services/";
                String[] parts = result.split(delimeter, 2);
                String BASE_URL = parts[0] + "" + delimeter;
                com.oustme.oustsdk.tools.OustPreferences.save(AppConstants.StringConstants.BASE_URL_FROM_API, BASE_URL);
                //verifyTenant(orgId);
                verifyAndSignIn(orgId, userName, fName, lName);
            }
        }
    }

    public void verifyAndSignIn(final String orgId, final String userId, String fName, String lName) {
        try {
            Log.d(TAG, "verifyAndSignIn");
            setTanentID(orgId);
            String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyAndSignInUser);

            VerifyAndSignInRequest verifyAndSignInRequest = new VerifyAndSignInRequest();
            verifyAndSignInRequest.setStudentid(userId);
            verifyAndSignInRequest.setOrgId(orgId);
            verifyAndSignInRequest.setFname(fName);
            verifyAndSignInRequest.setLname(lName);
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
            Log.d(TAG, "verifyAndSignIn: " + verifyOrgUrl);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, verifyOrgUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.toString());
                    Log.d(TAG, "onResponse: " + response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAppId", response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey", response.optString("firebaseAPIKey"));

                    Gson gson = new Gson();
                    VerifyAndSignInResponse verifyAndSignInResponse = gson.fromJson(response.toString(), VerifyAndSignInResponse.class);
                    gotoVerifyAndSignInResponse(verifyAndSignInResponse);


                    //VerifyOrgIdResponseA verifyOrgIdResponse = gson.fromJson(response.toString(), VerifyOrgIdResponseA.class);
                    //gotVerifyTanentResponse(verifyOrgIdResponse);
                    /*if(oustLoginApiCallBack!=null)
                        sendErrorMessageToListener("Couldn't get the tenant details");*/

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (oustLoginApiCallBack != null)
                        sendErrorMessageToListener("Couldn't get the tenant details");
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

    /*public void verifyTenant(final String orgId) {
        try {
            Log.d(TAG,"verifyTenant");
            setTanentID(orgId);
            String verifyOrgUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyOrgId);
            verifyOrgUrl = verifyOrgUrl.replace("{orgId}", orgId);
            verifyOrgUrl = verifyOrgUrl + "?devicePlatformName=Android";
            verifyOrgUrl = HttpManager.getAbsoluteUrl(verifyOrgUrl);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, verifyOrgUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAppId", response.optString("firebaseAppId"));
                    OustPreferences.save("firebaseAPIKey", response.optString("firebaseAPIKey"));
                    Gson gson = new Gson();
                    VerifyOrgIdResponseA verifyOrgIdResponse = gson.fromJson(response.toString(), VerifyOrgIdResponseA.class);
                    gotVerifyTanentResponse(verifyOrgIdResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(oustLoginApiCallBack!=null)
                        sendErrorMessageToListener("Couldn't get the tenant details");
                        //oustLoginApiCallBack.onLoginApiError("Couldn't get the tenant details");
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
    }*/

    private void gotoVerifyAndSignInResponse(VerifyAndSignInResponse verifyAndSignInResponse) {
        if (verifyAndSignInResponse != null) {
            Gson gson = new Gson();
            String data = gson.toJson(verifyAndSignInResponse);
            Log.d(TAG, "gotoVerifyAndSignInResponse: " + data);
            if (verifyAndSignInResponse.isSuccess() && verifyAndSignInResponse.isValidTenant()) {
                if ((verifyAndSignInResponse.getStudentKey() != null && !verifyAndSignInResponse.getStudentKey().equals("0")) && (verifyAndSignInResponse.getStudentid() != null) && (!verifyAndSignInResponse.getStudentid().isEmpty())) {

                    OustPreferences.save(STUDE_KEY, verifyAndSignInResponse.getStudentKey());
                    verifyAndSignInResponse.setLoginType(LoginType.Oust.name());
                    OustSdkTools.initServerWithNewEndPoints(verifyAndSignInResponse.getApiServerEndpoint(), verifyAndSignInResponse.getFirebaseEndpoint(), verifyAndSignInResponse.getFirebaseAppId(), verifyAndSignInResponse.getFirebaseAPIKey());

                    if ((verifyAndSignInResponse.getFirebaseToken() != null) && (!verifyAndSignInResponse.getFirebaseToken().isEmpty())) {

                        if (verifyAndSignInResponse.getS3_base_end() != null &&
                                !verifyAndSignInResponse.getS3_base_end().isEmpty()) {
                            AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL = verifyAndSignInResponse.getS3_base_end() + verifyAndSignInResponse.getImg_bucket_name() + "/";
                            OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, verifyAndSignInResponse.getS3_base_end() + verifyAndSignInResponse.getImg_bucket_name() + "/");
                        } else {
                            String s3baseEnd = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL;
                            OustPreferences.save(AppConstants.StringConstants.S3_BASE_END, s3baseEnd);
                        }

                        if (verifyAndSignInResponse.getS3_Bucket_Region() != null && !verifyAndSignInResponse.getS3_Bucket_Region().isEmpty()) {
                            AppConstants.MediaURLConstants.BUCKET_REGION = verifyAndSignInResponse.getS3_Bucket_Region();
                            OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, verifyAndSignInResponse.getS3_Bucket_Region());
                        } else {
                            String s3BktRegion = AppConstants.MediaURLConstants.BUCKET_REGION;
                            OustPreferences.save(AppConstants.StringConstants.S3_BKT_REGION, s3BktRegion);
                        }

                        if (verifyAndSignInResponse.getHttp_img_bucket_cdn() != null && !verifyAndSignInResponse.getHttp_img_bucket_cdn().isEmpty()) {
                            AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH = verifyAndSignInResponse.getHttp_img_bucket_cdn();
                            OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, verifyAndSignInResponse.getHttp_img_bucket_cdn());
                        } else {
                            OustPreferences.save(AppConstants.StringConstants.HTTP_IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS);
                        }

                        if (verifyAndSignInResponse.getImg_bucket_cdn() != null && !verifyAndSignInResponse.getImg_bucket_cdn().isEmpty()) {
                            AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS = verifyAndSignInResponse.getImg_bucket_cdn();
                            OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, verifyAndSignInResponse.getImg_bucket_cdn());
                        } else {
                            OustPreferences.save(AppConstants.StringConstants.IMG_BKT_CDN, AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
                        }

                        if (verifyAndSignInResponse.getImg_bucket_name() != null && !verifyAndSignInResponse.getImg_bucket_name().isEmpty()) {
                            S3_BUCKET_NAME = verifyAndSignInResponse.getImg_bucket_name();
                            OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, verifyAndSignInResponse.getImg_bucket_name());
                        } else {
                            OustPreferences.save(AppConstants.StringConstants.IMG_BKT_NAME, S3_BUCKET_NAME);
                        }

                        /*if (verifyAndSignInResponse.getWebAppLink() != null && !verifyAndSignInResponse.getWebAppLink().isEmpty()) {
                            Log.d(TAG, "onResponse: webAppLink:" + verifyAndSignInResponse.getWebAppLink());
                            com.oustme.oustsdk.tools.OustPreferences.save("webAppLink", verifyAndSignInResponse.getWebAppLink());
                        }*/

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

                        if (isFirebaseAuthReq) {
                            authenticate(verifyAndSignInResponse.getFirebaseToken(), verifyAndSignInResponse);
                        } else {
                            OustPreferences.save("firebaseToken", verifyAndSignInResponse.getFirebaseToken());
                            //Gson gson = new Gson();
                            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getNewActiveUser(verifyAndSignInResponse)));
                            OustPreferences.save("isLoggedIn", "true");
                            OustPreferences.save("loginType", LoginType.Oust.toString());
                            onLoginComplete();
                        }
                    }
                } else {
                    sendErrorMessageToListener("Couldn't able to load the user details");
                }
            } else {
                /*if (verifyAndSignInResponse.isValidTenant())
                    sendErrorMessageToListener(""+OustStrings.getString("org_id_invalid"));
                else
                    sendErrorMessageToListener(""+OustStrings.getString("org_id_invalid"));*/

                sendErrorMessageToListener(verifyAndSignInResponse.getError());
            }
        } else {
            sendErrorMessageToListener("" + OustStrings.getString("org_id_invalid"));
        }
    }

    /*private void gotVerifyTanentResponse(VerifyOrgIdResponseA verifyOrgIdResponse) {
        Log.d(TAG, "gotVerifyTanentResponse: ");
        if ((verifyOrgIdResponse != null) && (verifyOrgIdResponse.isSuccess())) {
            if (verifyOrgIdResponse.isValidTenant()) {
                SignInRequest signInRequest = new SignInRequest();
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
                oustLoginA(signInRequest);
            } else {
                Log.d(TAG, "gotVerifyTanentResponse: error");
                sendErrorMessageToListener(""+OustStrings.getString("org_id_invalid"));
                //oustLoginApiCallBack.onLoginApiError(OustStrings.getString("org_id_invalid"));
            }
        }else{
            Log.d(TAG, "gotVerifyTanentResponse: error");
            sendErrorMessageToListener(""+OustStrings.getString("org_id_invalid"));
        }
    }*/

    public String getMD5EncodedString(String encryptString) throws NoSuchAlgorithmException {
        String encodedString = "";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptString.getBytes());

        byte[] byteData = md.digest();

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
            Log.d(TAG, "oustLoginA: " + jsonParams);
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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    private void getUserAppConfiguration() {
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
                        if (appConfigMap.get("logoutButtonEnabled") != null) {
                            OustPreferences.saveAppInstallVariable("logoutButtonEnabled", (boolean) appConfigMap.get("logoutButtonEnabled"));
                        } else {
                            OustPreferences.saveAppInstallVariable("logoutButtonEnabled", false);
                        }
                        if (appConfigMap.get(AppConstants.StringConstants.CAT_BANNER) != null) {
                            OustPreferences.save(CAT_BANNER, (String) appConfigMap.get(CAT_BANNER));
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
                                        }else {
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

    /*public void oustLoginProcessOverA(SignInResponse signInResponse1) {
        this.signInResponse = signInResponse1;
        if (signInResponse != null) {
            if (signInResponse.isSuccess()) {
                Log.d(TAG, "oustLoginProcessOverA: ");
                OustPreferences.save(STUDE_KEY, signInResponse.getStudentKey());
                if ((signInResponse.getStudentid() != null) && (!signInResponse.getStudentid().isEmpty())) {
                    signInResponse.setLoginType(LoginType.Oust.name());
                    OustSdkTools.initServerWithNewEndPoints(signInResponse.getApiServerEndpoint(), signInResponse.getFirebaseEndpoint(), signInResponse.getFirebaseAppId(), signInResponse.getFirebaseAPIKey());
                    if ((signInResponse.getFirebaseToken() != null) && (!signInResponse.getFirebaseToken().isEmpty())) {
                        if (isFirebaseAuthReq) {
                            authenticate(signInResponse.getFirebaseToken());
                        } else {
                            OustPreferences.save("firebaseToken", signInResponse.getFirebaseToken());
                            Gson gson = new Gson();
                            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getActiveUser(signInResponse)));
                            OustPreferences.save("isLoggedIn", "true");
                            OustPreferences.save("loginType", LoginType.Oust.toString());
                            onLoginComplete();
                            //oustLoginApiCallBack.onLoginApiComplete();
                        }
                    }
                }
            } else {
                setresisterUserRequest();
            }
        }else{
            if(oustLoginApiCallBack!=null)
                sendErrorMessageToListener("SignIn user error");
                //oustLoginApiCallBack.onLoginApiError("SignIn user error");
        }
    }

    private void setresisterUserRequest() {
        try {
            RegisterRequestData registerRequestData = new RegisterRequestData();
            registerRequestData.setStudentid(userName);
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
            sendErrorMessageToListener("User registration failed");
            //oustLoginApiCallBack.onLoginApiError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void registerUser(RegisterRequestData registerRequestData) {
        String register_url = OustSdkApplication.getContext().getResources().getString(R.string.register_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(registerRequestData);
        Log.d(TAG,"jsonParams:"+jsonParams);
        try {
            register_url = HttpManager.getAbsoluteUrl(register_url);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, register_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG,"RegisterResponse:"+response.toString());
                    Gson gson = new Gson();
                    SignInResponse signInResponse = gson.fromJson(response.toString(), SignInResponse.class);
                    gotRegisterResponceData(signInResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    *//*if(oustLoginApiCallBack!=null)
                        oustLoginApiCallBack.onLoginApiError("Register user error");*//*
                    oustLoginProcessOver(null);
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
        } catch (Exception e) {
            //oustLoginApiCallBack.onLoginApiError("Register user error");
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
                        if (isFirebaseAuthReq) {
                            authenticate(signInResponse.getFirebaseToken());
                        } else {
                            Gson gson = new Gson();
                            OustPreferences.save("firebaseToken", signInResponse.getFirebaseToken());
                            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getActiveUser(signInResponse)));
                            OustPreferences.save("isLoggedIn", "true");
                            OustPreferences.save("loginType", LoginType.Oust.toString());
                            onLoginComplete();
                        }
                    } else {
                        sendErrorMessageToListener("Firebase authentication failed");
                        //oustLoginApiCallBack.onLoginApiError("Something went wrong!!");
                    }
                } else {
                    if (levelOneAuthCheckResponseData.isTrustedTenant()) {
                        setresisterUserRequest();
                    } else {
                        sendErrorMessageToListener("Not trusted tanent");
                        //oustLoginApiCallBack.onLoginApiError("Not trusted tanent.");
                    }
                }
            } else {
                if (levelOneAuthCheckResponseData.getPopup() != null) {
                    Popup popup = levelOneAuthCheckResponseData.getPopup();
                    sendErrorMessageToListener(popup.getContent());
                    //oustLoginApiCallBack.onLoginApiError(popup.getContent());
                } else if ((levelOneAuthCheckResponseData.getError() != null) && (!levelOneAuthCheckResponseData.getError().isEmpty())) {
                    sendErrorMessageToListener(""+levelOneAuthCheckResponseData.getError());
                    //oustLoginApiCallBack.onLoginApiError(levelOneAuthCheckResponseData.getError());
                } else {
                    sendErrorMessageToListener("Internal server error");
                    //oustLoginApiCallBack.onLoginApiError("Internal server error");
                }
            }
        } else {
            sendErrorMessageToListener(""+OustStrings.getString("retry_internet_msg"));
            //oustLoginApiCallBack.onLoginApiError(OustStrings.getString("retry_internet_msg"));
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
            //sendErrorMessageToListener("Something went wrong!!");
            //oustLoginApiCallBack.onLoginApiError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void gotRegisterResponceData(final SignInResponse signInResponse1) {
        this.signInResponse = signInResponse1;
        if (signInResponse != null) {
            if (signInResponse.isSuccess())
            {
                if ((signInResponse.getStudentid() != null) && (!signInResponse.getStudentid().isEmpty()))
                {
                    OustPreferences.save(STUDE_KEY, signInResponse.getStudentKey());

                    OustSdkTools.initServerWithNewEndPoints(signInResponse.getApiServerEndpoint(), signInResponse.getFirebaseEndpoint(), signInResponse.getFirebaseAppId(), signInResponse.getFirebaseAPIKey());
                    setTanentID(orgId);
                    if ((signInResponse.getFirebaseToken() != null) && (!signInResponse.getFirebaseToken().isEmpty()))
                    {
                        OustStaticVariableHandling.getInstance().setEnterpriseUser(true);
                        if (isFirebaseAuthReq) {
                            authenticate(signInResponse.getFirebaseToken());
                        } else {
                            OustPreferences.save("firebaseToken", signInResponse.getFirebaseToken());
                            Gson gson = new Gson();
                            OustPreferences.save("userdata", gson.toJson(OustSdkTools.getInstance().getActiveUser(signInResponse)));
                            OustPreferences.save("isLoggedIn", "true");
                            OustPreferences.save("loginType", LoginType.Oust.toString());
                            onLoginComplete();
                        }
                    } else {
                        OustSdkTools.showToast("Firebase authentication failed");
                    }
                } else {
                    OustSdkTools.showToast("Login process failed");
                }
            } else {
                if (signInResponse.getPopup() != null) {
                    Popup popup = signInResponse.getPopup();
                    sendErrorMessageToListener(""+popup.getContent());
                    //oustLoginApiCallBack.onLoginApiError(popup.getContent());
                } else if ((signInResponse.getError() != null) && (!signInResponse.getError().isEmpty())) {
                    sendErrorMessageToListener(""+signInResponse.getError());
                    //oustLoginApiCallBack.onLoginApiError(signInResponse.getError());
                } else {
                    sendErrorMessageToListener("Internal server error");
                    //oustLoginApiCallBack.onLoginApiError("Internal server error");
                }
            }
        } else {
            sendErrorMessageToListener(""+OustStrings.getString("retry_internet_msg"));
            //oustLoginApiCallBack.onLoginApiError(OustStrings.getString("retry_internet_msg"));
        }
    }*/

    private void onLoginComplete() {
        String key = OustPreferences.get(STUDE_KEY);
        if (key != null) {
            OustFirebaseTools.getRootRef().child("landingPage/" + key + "/catalogueId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    OustPreferences.saveTimeForNotification(CATALOGUE_ID, (long) dataSnapshot.getValue());
                    Log.d(TAG, "onDataChange catalogue Id: " + (long) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);
        OustAppState.getInstance().setLandingPageOpen(false);

        if (oustLoginApiCallBack != null)
            oustLoginApiCallBack.onLoginApiComplete();
    }


    private String getAPIKey() throws OustException {
        String TAG = "Example Meta-Data";
        String myAPIKey = null;
        try {
            ApplicationInfo ai = OustSdkApplication.getContext().getPackageManager().getApplicationInfo(
                    OustSdkApplication.getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            myAPIKey = bundle.getString("com.oust.sdk.SecretKey");
            OustPreferences.save("api_key", myAPIKey);
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

    /*public void authenticate(String token) {
        new OustAuthenticator(context!=null?context:activity,orgId, oustLoginApiCallBack, isActivityActive).authenticateWithFirebase(token, signInResponse);
    }*/

    public void authenticate(String token, VerifyAndSignInResponse verifyAndSignInResponse) {
        new OustAuthenticator(context != null ? context : activity, orgId, oustLoginApiCallBack, isActivityActive).authenticateWithFirebase(token, verifyAndSignInResponse);
    }

    public void authenticate() {
        new OustAuthenticator(context != null ? context : activity, orgId, oustLoginApiCallBack, isActivityActive).authenticateWithFirebase();
    }

    public void setTanentID(String tanentId) {
        try {
            OustPreferences.save("tanentid", tanentId);
        } catch (Exception e) {
            //sendErrorMessageToListener("Something went wrong!!");
            //oustLoginApiCallBack.onLoginApiError("Something went wrong!!");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveUserRole(String role) {
        OustPreferences.save("userRole", role);
    }

    @Override
    public void onDownloadFailed() {
        OustPreferences.saveAppInstallVariable("IsLoginServiceRunning", false);
        OustAppState.getInstance().setLandingPageOpen(false);

        if (oustLoginApiCallBack != null) {
            oustLoginApiCallBack.onError("Resource download failed error!");
        }
    }

    @Override
    public void onDownloadComplete() {
        if (!isDownloadComplete) {
            isDownloadComplete = true;
            checkLoginProcess();
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 100) {
            oustLoginApiCallBack.onDownloadProgress(progress);
        } else {
            if (this.progress >= 100) {

            } else {
                this.progress = progress;
                oustLoginApiCallBack.onDownloadProgress(progress);
            }
        }
    }
}
