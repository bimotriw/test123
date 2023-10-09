

package com.oustme.oustsdk.util;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_APP_TUTORIAL_SHOWN;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.ORG_ID_USER_ID_APP_TUTORIAL_VIEWED;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.SplashActivity;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.request.SignOutRequest;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApiCallUtils {

    private static final String TAG = "ApiCalls";

    private static boolean isLoggedOut = false;

    public interface NetworkCallback {

        void onResponse(JSONObject response);

        void onErrorResponse(VolleyError error);

    }

    public interface NetworkCallback2 {

        void onResponse(JSONObject response, String query);

        void onErrorResponse(VolleyError error);

    }

    public static void setIsLoggedOut(boolean isLoggedOut) {
        ApiCallUtils.isLoggedOut = isLoggedOut;
    }

    public static void doNetworkCall(final int requestMethod, final String url, final JSONObject requestParams, final NetworkCallback callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Success JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Success Response : " + (response != null ? response : "empty"));
                if (callback != null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                if (callback != null)
                    callback.onErrorResponse(error);

                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } /*else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    new SingleSessionHandler().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    if (!TextUtils.isEmpty(OustPreferences.get("api_key")))
                        params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public static void doNetworkCall2(final int requestMethod, final String url, final JSONObject requestParams, final String query, final NetworkCallback2 callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Success JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                if (callback != null)
                    callback.onResponse(response, query);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                if (callback != null)
                    callback.onErrorResponse(error);

                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } /*else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    new SingleSessionHandler().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    if (!TextUtils.isEmpty(OustPreferences.get("api_key")))
                        params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public static void doNetworkCallWithErrorBody(final int requestMethod, final String url, final JSONObject requestParams, final NetworkCallback callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Success JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                if (callback != null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                String body = null;
                if (callback != null) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }

                    if (body != null) {
                        try {
                            callback.onResponse(new JSONObject(body));
                        } catch (JSONException err) {
                            Log.d("Error", err.toString());
                            callback.onErrorResponse(error);
                        }
                    } else {
                        callback.onErrorResponse(error);
                    }

                }


                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } /*else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    new SingleSessionHandler().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    if (!TextUtils.isEmpty(OustPreferences.get("api_key")))
                        params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public static void doNetworkCallForSubmitGame(final int requestMethod, final String url, final JSONObject requestParams, final NetworkCallback callback) {
        try {
            requestParams.put("retry", true);
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, response -> {
            Log.e(TAG, "url " + url + " success");
            Log.e(TAG, "Success JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
            if (callback != null)
                callback.onResponse(response);
        }, error -> {
            Log.e(TAG, "Error URL : " + url);
            Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
            Log.e(TAG, "Error Message :" + error.getMessage());
            Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
            Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
            Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
            Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
            if (callback != null)
                callback.onErrorResponse(error);

            if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                if (!TextUtils.isEmpty(url) && url.contains("signout"))
                    return;
                OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                logoutApiCall();
            } else {
                try {
                    InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                    instrumentationMailRequest.setModuleType("ASSESSMENT");
                    instrumentationMailRequest.setModuleId(OustSdkTools.convertToLong(Objects.requireNonNull(requestParams).get("assessmentId")));
                    if (error.networkResponse != null) {
                        instrumentationMailRequest.setMessageDesc("Assessment game submit api was failed.\n Error Code : " + error.networkResponse.statusCode + "\n Error message got for below request :\n" + requestParams);
                    }
                    instrumentationMailRequest.setIssuesType(IssueTypes.ASSESSMENT_GAME_SUBMISSION.toString());
                    InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                    instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                try {
                    if (!TextUtils.isEmpty(OustPreferences.get("api_key")))
                        params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public static void doNetworkCallWithContentType(final int requestMethod, final String url, final JSONObject requestParams, final NetworkCallback callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                if (callback != null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                if (callback != null)
                    callback.onErrorResponse(error);

                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } /*else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    new SingleSessionHandler().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    if (!TextUtils.isEmpty(OustPreferences.get("api_key")))
                        params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);
                    params.put("Content-Type", "application/json; charset=utf-8");

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");
    }

    public static void doNetworkCall(final int requestMethod, final String url, final JSONObject requestParams, String tag, final NetworkCallback callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));

                if (callback != null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                if (callback != null)
                    callback.onErrorResponse(error);
                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } /*else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    new SingleSessionHandler().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
                    String authToken = OustPreferences.get("authToken");
                    Log.d(TAG, "JSONObject : " + authToken);

                    if (!TextUtils.isEmpty(authToken)) {
                        if (authToken.contains("Bearer")) {
                            params.put("Authorization", authToken);
                        } else {
                            params.put("Authorization", "Bearer " + authToken);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                return params;
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, tag);
    }

    public static void doNetworkCallWithoutAuth(final int requestMethod, final String url, final JSONObject requestParams, final NetworkCallback callback) {
        JsonObjectMetaRequest jsonObjReq = new JsonObjectMetaRequest(requestMethod, url, requestParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "url " + url + " success");
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                if (callback != null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error URL : " + url);
                Log.e(TAG, "Error JSONObject : " + (requestParams != null ? requestParams.toString() : "empty"));
                Log.e(TAG, "Error Message :" + error.getMessage());
                Log.e(TAG, "Error Network Time Ms : " + error.getNetworkTimeMs());
                Log.e(TAG, "Error Localized Message : " + error.getLocalizedMessage());
                Log.e(TAG, "Error statusCode : " + (error.networkResponse != null ? error.networkResponse.statusCode : 1));
                Log.e(TAG, "Error isLoggedOut : " + isLoggedOut);
                if (callback != null)
                    callback.onErrorResponse(error);
                if (error.networkResponse != null && error.networkResponse.statusCode == 401 && !isLoggedOut) {
                    if (!TextUtils.isEmpty(url) && url.contains("signout"))
                        return;
                    OustSdkTools.showToast(OustStrings.getString("session_restart_msg"));
                    logoutApiCall();
                } else if (error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    SingleSessionHandler.getInstance().handleNetworkCallWithoutAuth(requestMethod, url, requestParams, callback);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("api-key", OustPreferences.get("api_key"));
                    params.put("org-id", OustPreferences.get("tanentid"));
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
    }

    private static void navigateToLogin() {
        logout();
        Context context = OustSdkApplication.getContext();
        try {
            Intent i = new Intent(context, SplashActivity.class);
//            Intent i = new Intent().setComponent(new ComponentName("com.oustme.oustapp", "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (context.getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(i);
            }
            isLoggedOut = true;
        } catch (ActivityNotFoundException e) {
            isLoggedOut = false;
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        } catch (Exception e) {
            isLoggedOut = false;
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        }
    }

    private static void logout() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);

                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public static void logoutApiCall() {
        String signOutUrl = OustSdkApplication.getContext().getResources().getString(R.string.signout);
        signOutUrl = HttpManager.getAbsoluteUrl(signOutUrl);
        String userName = OustPreferences.get("test_userid");
        String institutional_id = OustPreferences.get("test_orgid");

        SignOutRequest signOutRequest = new SignOutRequest();
        if ((OustPreferences.get("gcmToken") != null)) {
            signOutRequest.setDeviceToken(OustPreferences.get("gcmToken"));
        }
        signOutRequest.setDeviceIdentity("android");
        signOutRequest.setStudentid(userName);
        signOutRequest.setInstitutionLoginId(institutional_id);
        signOutRequest.setDevicePlatformName("android");

        final Gson gson = new Gson();
        String jsonParams = gson.toJson(signOutRequest);

        doNetworkCall(Request.Method.PUT, signOutUrl, OustSdkTools.getRequestObject(jsonParams), new NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null && response.optBoolean("success"))
//                    navigateToLogin();
                    logout_old();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "unable to logout");
//                navigateToLogin();
                logout_old();
            }
        });
    }

    private static void logout_old() {
        try {
            if (!OustLogDetailHandler.getInstance().isUserForcedOut()) {
                boolean isAppTutorialShow = OustPreferences.getAppInstallVariable(IS_APP_TUTORIAL_SHOWN);
                String tenantId = OustPreferences.get("tanentid");
                String tenantName = tenantId.trim();
                ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
                OustDataHandler.getInstance().resetData();
                OustStaticVariableHandling.getInstance().setEnterpriseUser(false);
                OustAppState.getInstance().setLandingPageLive(false);
                OustStaticVariableHandling.getInstance().setAppActive(false);
                OustStaticVariableHandling.getInstance().setNewCplDistributed(false);

                OustSdkTools.showProgressBar();
                OustSdkTools.clearAlldataAndlogout();
                OustPreferences.clearAll();
                OustPreferences.saveAppInstallVariable("LOGOUT", true);
                if (isAppTutorialShow) {
                    if (activeUser != null) {
                        String tempValue = tenantName + "_" + activeUser.getStudentKey() + "_" + isAppTutorialShow;
                        OustPreferences.save(ORG_ID_USER_ID_APP_TUTORIAL_VIEWED, tempValue);
                    }
                }
                try {
                    Log.e(TAG, "from sdk LOGOUT");

                    Intent intent = new Intent().setComponent(new ComponentName("com.oustme.oustapp", "com.oustme.oustapp.newLayout.view.activity.NewLoginScreenActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if (OustSdkApplication.getContext().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        OustSdkApplication.getContext().startActivity(intent);
                    }

                    isLoggedOut = true;
                    Log.d(TAG, "islogout sdk: true:");
                } catch (Exception e) {
                    isLoggedOut = false;
                    Log.d(TAG, "islogout sdk: false:");
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}

