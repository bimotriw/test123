package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.AddFavCardsRequestData;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by shilpysamaddar on 25/05/17.
 */

public class SubmitFavouriteCardRequestService extends IntentService {
    private List<String> requests;
    private String SAVED_REQUESTS = "savedFavouriteCardsRequests";
    private String UNFAV_SAVED_REQUESTS = "savedUnfavouriteCardsRequests";
    private String SAVED_RM_REQUESTS = "savedFavouriteRMRequests";
    private String SAVED_UnFavRM_REQUESTS = "savedUnfavouriteRMRequests";

    int noofAttempt = 0;

    public SubmitFavouriteCardRequestService() {
        super(SubmitFavouriteCardRequestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkForRequestAvailability();
    }

    private void checkForRequestAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_REQUESTS);
        if ((requests != null) && (requests.size() > 0)) {
            Gson gson = new Gson();
            AddFavCardsRequestData addFavCardsRequestData = gson.fromJson(requests.get(0), AddFavCardsRequestData.class);
            if (addFavCardsRequestData != null) {
                sendRequest(addFavCardsRequestData);
            }
        } else {
//            it will start many services at a time if we write unfavouritecard service seperatly
//            so, unfavourite card request api service is also written here and will start once
//            favourite is complete
            checkForUnfavouriteCardRequestAvailability();
        }
    }

    private void sendRequest(final AddFavCardsRequestData addFavCardsRequestData) {
        sendFavCardRequest(addFavCardsRequestData);
    }

    public void favRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                Log.e("Favourite", "" + commonResponse.isSuccess());
                OustPreferences.saveLocalNotificationMsg(SAVED_REQUESTS, requests);
                checkForRequestAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForRequestAvailability();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForUnfavouriteCardRequestAvailability() {
        try {
            requests = OustPreferences.getLoacalNotificationMsgs(UNFAV_SAVED_REQUESTS);
            if ((requests != null) && (requests.size() > 0)) {
                Gson gson = new Gson();
//            used same class to store data as format is same
                AddFavCardsRequestData addFavCardsRequestData = gson.fromJson(requests.get(0), AddFavCardsRequestData.class);
                if (addFavCardsRequestData != null) {
                    sendUnfavRequest(addFavCardsRequestData);
                }
            } else {
                checkForFavouriteRMRequestAvailability();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnfavRequest(final AddFavCardsRequestData addFavCardsRequestData) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sendUnFavCardRequest(addFavCardsRequestData);

            }
        });
    }

    public void UnfavRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                Log.e("Favourite", "" + commonResponse.isSuccess());
                OustPreferences.saveLocalNotificationMsg(UNFAV_SAVED_REQUESTS, requests);
                checkForUnfavouriteCardRequestAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForUnfavouriteCardRequestAvailability();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    =================================================================
//    Read More Fav and UnFav both Services
    private void checkForFavouriteRMRequestAvailability() {
        try {
            requests = OustPreferences.getLoacalNotificationMsgs(SAVED_RM_REQUESTS);
            if ((requests != null) && (requests.size() > 0)) {
                Gson gson = new Gson();
//            used same class to store data as format is same
                AddFavReadMoreRequestData addFavCardsRequestData = gson.fromJson(requests.get(0), AddFavReadMoreRequestData.class);
                if (addFavCardsRequestData != null) {
                    sendRMFavRequest(addFavCardsRequestData);
                }
            } else {
                checkForUnFavouriteRMRequestAvailability();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendRMFavRequest(final AddFavReadMoreRequestData addFavReadMoreRequestData) {
        try {
            sendFavRMRequest(addFavReadMoreRequestData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void RMfavRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                if (requests != null && requests.size() > 0) {
                    requests.remove(0);
                }
                Log.e("Favourite", "" + commonResponse.isSuccess());
                OustPreferences.saveLocalNotificationMsg(SAVED_RM_REQUESTS, requests);
                checkForFavouriteRMRequestAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForFavouriteRMRequestAvailability();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForUnFavouriteRMRequestAvailability() {
        try {
            requests = OustPreferences.getLoacalNotificationMsgs(SAVED_UnFavRM_REQUESTS);
            if ((requests != null) && (requests.size() > 0)) {
                Gson gson = new Gson();
//            used same class to store data as format is same
                AddFavReadMoreRequestData addFavCardsRequestData = gson.fromJson(requests.get(0), AddFavReadMoreRequestData.class);
                if (addFavCardsRequestData != null) {
                    sendUnFavRMRquest(addFavCardsRequestData);
                }
            } else {
                this.stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnFavRMRquest(final AddFavReadMoreRequestData addFavReadMoreRequestData) {
        try {
            sendUnFavRMRequest(addFavReadMoreRequestData);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void RMUnfavRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                Log.e("Favourite", "" + commonResponse.isSuccess());
                OustPreferences.saveLocalNotificationMsg(SAVED_UnFavRM_REQUESTS, requests);
                checkForUnFavouriteRMRequestAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForUnFavouriteRMRequestAvailability();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendFavCardRequest(AddFavCardsRequestData addFavCardsRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = getResources().getString(R.string.submitFavCard_url);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavCardsRequestData);
            submitCourseComplete_url = HttpManager.getAbsoluteUrl(submitCourseComplete_url);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("TAG", "Favourite - response: " + response.toString());
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = () -> favRequestSent(commonResponses[0]);
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitCourseComplete_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            favRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            Log.e("sendFavCardRequest ", "ERROR: ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendUnFavCardRequest(AddFavCardsRequestData addFavCardsRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitUnFavUrl = getResources().getString(R.string.submitUnFavUrl);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavCardsRequestData);
            submitUnFavUrl = HttpManager.getAbsoluteUrl(submitUnFavUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, submitUnFavUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            UnfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitUnFavUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            UnfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

        } catch (Exception e) {
            Log.e("sendUnFavCardRequest ", "ERROR: ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendFavRMRequest(AddFavReadMoreRequestData addFavReadMoreRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String send_fav_rm_url = getResources().getString(R.string.send_fav_rm_url);
            send_fav_rm_url = HttpManager.getAbsoluteUrl(send_fav_rm_url);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(addFavReadMoreRequestData);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, send_fav_rm_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RMfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, send_fav_rm_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RMfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            Log.e("sendFavRMRequest", "ERROR: ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendUnFavRMRequest(AddFavReadMoreRequestData addFavReadMoreRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String send_un_fav_rm_url = getResources().getString(R.string.send_un_fav_rm_url);
            Gson gson = new GsonBuilder().create();
            send_un_fav_rm_url = HttpManager.getAbsoluteUrl(send_un_fav_rm_url);
            String jsonParams = gson.toJson(addFavReadMoreRequestData);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, send_un_fav_rm_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RMUnfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            ApiCallUtils.doNetworkCall(Request.Method.PUT, send_un_fav_rm_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RMUnfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });



            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, send_un_fav_rm_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            RMUnfavRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("sendUnFavRMRequest", "ERROR: ", e);
        }
    }

}
