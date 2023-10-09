package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.ClickedFeedRequestData;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOFeedBackModel;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by oust on 9/26/17.
 */

public class FeedBackService extends IntentService {
    private List<String> requests;
    private String SAVED_FEEDCLICKED_REQUEST = "savedFeedClickedRequests";
    private int noofAttempt = 0;

    public FeedBackService(String name) {
        super(name);
    }

    private List<DTOFeedBackModel> dtoFeedBackModelList;

    public FeedBackService() {
        super(FeedBackService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        checkIfFeedsAvailable();
    }

    private void checkIfFeedsAvailable() {
        dtoFeedBackModelList = RoomHelper.getAllFeeds();
        if(dtoFeedBackModelList !=null && dtoFeedBackModelList.size()>0) {
            for (int i = 0; i < dtoFeedBackModelList.size(); i++) {
                sendFeedsToServer(dtoFeedBackModelList.get(i));
            }
        }
        else{
            checkForFeedClickedRequestAvailability();
        }

    }

    private void sendFeedsToServer(final DTOFeedBackModel dtoFeedBackModel) {
        try {
            String sendform_reques_url = getResources().getString(R.string.sendform_reques_url);
            //Gson gson = new GsonBuilder().create();
            String jsonParams = getFeedJSon(dtoFeedBackModel);

            sendform_reques_url = HttpManager.getAbsoluteUrl(sendform_reques_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    sendRquestOver(commonResponse, dtoFeedBackModel);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendform_reques_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    sendRquestOver(commonResponse, dtoFeedBackModel);
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
        }
    }

    private String getFeedJSon(DTOFeedBackModel dtoFeedBackModel) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("country",dtoFeedBackModel.getCountry()+"");
            jsonObject.put("city",dtoFeedBackModel.getCity()+"");
            jsonObject.put("area",dtoFeedBackModel.getArea()+"");
            jsonObject.put("longitude",dtoFeedBackModel.getLongitude());
            jsonObject.put("latitude",dtoFeedBackModel.getLatitude());
            jsonObject.put("mobile",dtoFeedBackModel.getMobile()+"");
            jsonObject.put("purpose",dtoFeedBackModel.getPurpose()+"");
            jsonObject.put("comments",dtoFeedBackModel.getComments()+"");
            jsonObject.put("photo",dtoFeedBackModel.getPhoto()+"");
            jsonObject.put("studentid",dtoFeedBackModel.getStudentid()+"");

        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return jsonObject.toString();
    }

    public void sendRquestOver(CommonResponse commonResponse, DTOFeedBackModel dtoFeedBackModel) {
        if (commonResponse != null) {
            if (commonResponse.isSuccess()) {
                RoomHelper.deleteFeeds(dtoFeedBackModel);
                checkIfFeedsAvailable();
            }
        }
    }


//    ============================================================================================
//    sendFeedAnalytics

    private void checkForFeedClickedRequestAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_FEEDCLICKED_REQUEST);
        if ((requests != null) && (requests.size() > 0)) {
            Gson gson = new Gson();
//            used same class to store data as format is same
            ClickedFeedRequestData clickedFeedRequestData = gson.fromJson(requests.get(0), ClickedFeedRequestData.class);
            if (clickedFeedRequestData != null) {
                sendFeedClickedRequest(clickedFeedRequestData);
            }
        } else {
            this.stopSelf();
        }
    }

    private void sendFeedClickedRequest(ClickedFeedRequestData clickedFeedRequestData) {
        final CommonResponse[] commonResponses = {null};
        try {
            String send_feed_request = getResources().getString(R.string.send_feed_request);
            Gson gson = new GsonBuilder().create();
            send_feed_request = HttpManager.getAbsoluteUrl(send_feed_request);
            String jsonParams = gson.toJson(clickedFeedRequestData);

            ApiCallUtils.doNetworkCall(Request.Method.POST, send_feed_request, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            FeedRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    FeedRequestSent(commonResponses[0]);
                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, send_feed_request, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            FeedRequestSent(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    FeedRequestSent(commonResponses[0]);
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
            Log.e("sendUnFavRMRequest", "ERROR: ", e);
        }
    }

    private void FeedRequestSent(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                if(requests!=null&&requests.size()!=0){
                    requests.remove(0);
                }

                OustPreferences.saveLocalNotificationMsg(SAVED_FEEDCLICKED_REQUEST, requests);
                checkForFeedClickedRequestAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForFeedClickedRequestAvailability();
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
