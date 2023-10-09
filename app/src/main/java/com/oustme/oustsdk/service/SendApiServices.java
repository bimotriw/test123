package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.ContestUserDataRequest;
import com.oustme.oustsdk.request.PingApiRequest;
import com.oustme.oustsdk.request.UserF3CScoreRequestData_v2;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 31/08/17.
 */

public class SendApiServices extends IntentService {
    private List<String> requests;
    private final String SAVED_REQUESTS = "savedPingRequests";
    int noofAttempt = 0;

    public SendApiServices() {
        super(SendApiServices.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("REQUEST", "inside SendPingApiService   ");
        checkForRequestAvailability();
    }

    private void checkForRequestAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_REQUESTS);
        if (requests.size() > 0) {
            Gson gson = new Gson();
            PingApiRequest pingApiRequest = gson.fromJson(requests.get(0), PingApiRequest.class);
            if (pingApiRequest != null) {
                sendRequest(pingApiRequest);
            }
        } else {
            checkForContestRequestAvailability();
        }
    }

    public void sendRequest(PingApiRequest pingApiRequest) {
        Log.d("PING", "sendRequest: ping api");
        if ((OustPreferences.get("fcmToken") != null) && (!OustPreferences.get("fcmToken").isEmpty())) {
            pingApiRequest.setFcmToken(OustPreferences.get("fcmToken"));
        }
        HttpManager.setBaseUrl();
        OustFirebaseTools.initFirebase();
        String sendPingApi_url = OustSdkApplication.getContext().getResources().getString(R.string.sendPingApi);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(pingApiRequest);
        try {
            sendPingApi_url = HttpManager.getAbsoluteUrl(sendPingApi_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendPingApi_url, OustSdkTools.getRequestObjectWithPreference(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    requestSent(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    requestSent(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendPingApi_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    requestSent(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestSent(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void requestSent(CommonResponse commonResponse) {
        try {
            Log.e("PING ", "got response inside SendPingApiService " + commonResponse.getError());
            if (commonResponse.isSuccess()) {
                Log.e("PING ", "got response success ");
                requests.remove(0);
                OustPreferences.saveLocalNotificationMsg(SAVED_REQUESTS, requests);
                checkForRequestAvailability();
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        noofAttempt++;
                        if (noofAttempt < 3) {
                            checkForRequestAvailability();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForContestRequestAvailability() {
        String f3contestrequest = OustPreferences.get("f3contestrequest");
        String f3cuser_rightcount_request = OustPreferences.get("f3cuser_rightcount_request");
        if ((f3contestrequest != null) && (!f3contestrequest.isEmpty())) {
            Gson gson = new Gson();
            UserF3CScoreRequestData_v2 userF3CScoreRequestData_v2 = gson.fromJson(f3contestrequest, UserF3CScoreRequestData_v2.class);
            sendContestRequest(userF3CScoreRequestData_v2);
        } else if ((f3cuser_rightcount_request != null) && (!f3cuser_rightcount_request.isEmpty())) {
            Gson gson = new Gson();
            ContestUserDataRequest contestUserDataRequest = gson.fromJson(f3cuser_rightcount_request, ContestUserDataRequest.class);
            sendContestUserDataRequest(contestUserDataRequest);
        } else {
            this.stopSelf();
        }
    }

    public void sendContestRequest(UserF3CScoreRequestData_v2 userF3CScoreRequestData_v2) {
        String sendcontestscore_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcontestscore_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userF3CScoreRequestData_v2);
        try {
            sendcontestscore_url = HttpManager.getAbsoluteUrl(sendcontestscore_url);
            JSONObject jsonObject = new JSONObject(jsonParams);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcontestscore_url, OustSdkTools.getRequestObjectforJSONObject(jsonObject), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    contestRequestSent(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    contestRequestSent(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendcontestscore_url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    contestRequestSent(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    contestRequestSent(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void sendContestUserDataRequest(ContestUserDataRequest contestUserDataRequest) {
        HttpManager.setBaseUrl();
        OustFirebaseTools.initFirebase();
        String sendcontest_userrightcount_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcontest_userrightcount_url);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(contestUserDataRequest);
        try {
            sendcontest_userrightcount_url = HttpManager.getAbsoluteUrl(sendcontest_userrightcount_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcontest_userrightcount_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    contestUserRequestSent(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    contestUserRequestSent(null);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendcontest_userrightcount_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    contestUserRequestSent(commonResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    contestUserRequestSent(null);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void contestRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                OustPreferences.clear("f3contestrequest");
                checkForContestRequestAvailability();
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        noofAttempt++;
                        if (noofAttempt < 3) {
                            checkForContestRequestAvailability();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void contestUserRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                OustPreferences.clear("f3cuser_rightcount_request");
                checkForContestRequestAvailability();
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        noofAttempt++;
                        if (noofAttempt < 3) {
                            checkForContestRequestAvailability();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
