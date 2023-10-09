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
import com.oustme.oustsdk.request.CourseAcknowledgeRequest;
import com.oustme.oustsdk.request.CourseCompleteAcknowledgeRequest;
import com.oustme.oustsdk.request.CourseRating_Request;
import com.oustme.oustsdk.request.SubmitCourseLevelCompleteRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustRestClient;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.List;
/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class SubmitLevelCompleteService extends IntentService {
    private static final String TAG = "SubmitLevelCompleteServ";
    private List<String> requests;
    private String SAVED_REQUESTS = "savedCourseLevelCompleteRequests";

    public SubmitLevelCompleteService() {
        super(SubmitLevelCompleteService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkForDownloadAvailability();
    }

    private void checkForDownloadAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_REQUESTS);
//        if ((requests != null) && (requests.size() > 0)) {
//            Gson gson = new Gson();
//            SubmitCourseLevelCompleteRequest submitCourseLevelComplteRequest = gson.fromJson(requests.get(0), SubmitCourseLevelCompleteRequest.class);
//            if (submitCourseLevelComplteRequest != null) {
//                sendRequest(submitCourseLevelComplteRequest);
//            }
//        } else {
            checkForAcknowledgeCourseList();
        //}
    }

    private void sendRequest(final SubmitCourseLevelCompleteRequest submitCourseLevelComplteRequest) {

        sendCourseLevelCompleteResponce(submitCourseLevelComplteRequest);
    }

    private void sendCourseLevelCompleteResponce(SubmitCourseLevelCompleteRequest submitCourseLevelComplteRequest) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseComplete_url = OustSdkApplication.getContext().getResources().getString(R.string.submitlevelCourseComplete_url);
            submitCourseComplete_url = submitCourseComplete_url.replace("{userId}", submitCourseLevelComplteRequest.getUserId());
            submitCourseComplete_url = submitCourseComplete_url.replace("{courseId}", submitCourseLevelComplteRequest.getCourseId());
            submitCourseComplete_url = submitCourseComplete_url.replace("{levelId}", submitCourseLevelComplteRequest.getLevelId());
            if (submitCourseLevelComplteRequest.getCourseColnId() != null) {
                submitCourseComplete_url = submitCourseComplete_url.replace("{courseColnId}", submitCourseLevelComplteRequest.getCourseColnId());
            } else {
                submitCourseComplete_url = submitCourseComplete_url.replace("{courseColnId}", "");
            }
            submitCourseComplete_url = HttpManager.getAbsoluteUrl(submitCourseComplete_url);
            JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();
            Log.d(TAG, "sendCourseLevelCompleteResponce:levelcompelteData: "+requestParams.toString());

            ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseComplete_url, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    final CommonResponse commonResponse = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            cardDownloadOver(commonResponse);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitCourseComplete_url, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final CommonResponse commonResponse = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            cardDownloadOver(commonResponse);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                        Log.e("params",params.toString());
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
            Log.e("sendCourseLevelComplete", "ERROR: ", e);
        }
    }

    public void cardDownloadOver(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                OustPreferences.saveLocalNotificationMsg(SAVED_REQUESTS, requests);
                checkForDownloadAvailability();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForDownloadAvailability();
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
        }
    }

    private void checkForAcknowledgeCourseList() {
        requests = OustPreferences.getLoacalNotificationMsgs("acknowledgecourse_list");
        if ((requests != null) && (requests.size() > 0)) {
            Gson gson = new Gson();
            CourseCompleteAcknowledgeRequest courseCompleteAcknowledgeRequest = gson.fromJson(requests.get(0), CourseCompleteAcknowledgeRequest.class);
            if (courseCompleteAcknowledgeRequest != null) {
                sendAcknowledgeRequest(courseCompleteAcknowledgeRequest);
            }
        } else {
            checkForRateCourseRequest();
        }
    }

    private void sendAcknowledgeRequest(final CourseCompleteAcknowledgeRequest courseCompleteAcknowledgeRequest) {
        CourseAcknowledgeRequest courseAcknowledgeRequest = new CourseAcknowledgeRequest();
        courseAcknowledgeRequest.setAckTimestamp(courseCompleteAcknowledgeRequest.getAckTimestamp());
        courseAcknowledgeRequest.setStudentid(courseCompleteAcknowledgeRequest.getStudentid());
        courseAcknowledgeRequest.setCourseColnId(courseCompleteAcknowledgeRequest.getCourseColnId());
        hitAcknowledgeUrl(courseAcknowledgeRequest, courseCompleteAcknowledgeRequest.getCourseId());
    }

    public void hitAcknowledgeUrl(CourseAcknowledgeRequest courseAcknowledgeRequest, String courseId) {
        final CommonResponse[] commonResponses = {null};
        try {
            String acknowledge_url = OustSdkApplication.getContext().getResources().getString(R.string.acknowledge_url);
            acknowledge_url = acknowledge_url.replace("{courseId}", courseId);
            acknowledge_url = HttpManager.getAbsoluteUrl(acknowledge_url);
            Gson gson = new GsonBuilder().create();
            final String jsonParams = gson.toJson(courseAcknowledgeRequest);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, acknowledge_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = () -> acknowledgeRequestOver(commonResponses[0]);
                    mainHandler.post(myRunnable);

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });



            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, acknowledge_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            acknowledgeRequestOver(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void acknowledgeRequestOver(CommonResponse commonResponse) {
        try {
            Log.e("--------",commonResponse.toString());
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                OustPreferences.saveLocalNotificationMsg("acknowledgecourse_list", requests);
                checkForAcknowledgeCourseList();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForAcknowledgeCourseList();
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
        }
    }


    private void checkForRateCourseRequest() {
        requests = OustPreferences.getLoacalNotificationMsgs("ratecourserequest_list");
        if ((requests != null) && (requests.size() > 0)) {
            Gson gson = new Gson();
            CourseRating_Request courseRating_request = gson.fromJson(requests.get(0), CourseRating_Request.class);
            courseRating_request.setFeedback(courseRating_request.getFeedback().replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]",""));
          //  Log.d("TAG", "checkForRateCourseRequest: "+courseRating_request.getFeedback());
            System.out.println(courseRating_request.toString());
            if (courseRating_request != null) {
                sendCourseRateRequest(courseRating_request);
            }
        } else {
            this.stopSelf();
        }
    }

    private void sendCourseRateRequest(final CourseRating_Request courseRating_request) {
        OustRestClient oustRestClient = new OustRestClient();
        sendCourseRating(courseRating_request);
    }

    private void sendCourseRating(CourseRating_Request courseRating_request) {
        final CommonResponse[] commonResponses = {null};
        try {
            String submitCourseRating_url = OustSdkApplication.getContext().getResources().getString(R.string.submitCourseRating_url);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(courseRating_request);
            submitCourseRating_url = HttpManager.getAbsoluteUrl(submitCourseRating_url);
            Log.d("TAg", "sendCourseRating: "+submitCourseRating_url);
            Log.d("RAG", "sendCourseRating:Json Data: "+jsonParams);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseRating_url, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Log.d("sendCourseRating", commonResponses[0].toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sendCourseRateRequestOver(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitCourseRating_url, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    Log.d("sendCourseRating", commonResponses[0].toString());
                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            sendCourseRateRequestOver(commonResponses[0]);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                        Log.e("params",params.toString());
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
            Log.d("sendCourseRating", "" + e);
        }
    }

    int noofAttempt = 0;

    public void sendCourseRateRequestOver(CommonResponse commonResponse) {
        try {
            if ((commonResponse != null) && (commonResponse.isSuccess())) {
                requests.remove(0);
                OustPreferences.saveLocalNotificationMsg("ratecourserequest_list", requests);
                checkForRateCourseRequest();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 3) {
                                checkForRateCourseRequest();
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 30000);
            }
        } catch (Exception e) {
        }
    }
}
