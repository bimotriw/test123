package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.firebase.common.CplModelData;
import com.oustme.oustsdk.request.SubmitCourseCompleteRequest;
import com.oustme.oustsdk.request.UserSettingRequest;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class SubmitCourseCompleteService extends IntentService {
    private static final String TAG = "SubmitCourseCompleteSer";
    private List<String> requests;
    private String SAVED_REQUESTS = "savedCourseCompleteRequests";
    private boolean isComingFromCPL = false;
    private Gson gson;
    private int pendingSubmissions;

    public SubmitCourseCompleteService() {
        super(SubmitCourseCompleteService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        try {
            isComingFromCPL = intent.getBooleanExtra("isComingFromCpl", false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        checkForDownloadAvailability();
    }

    private void checkForDownloadAvailability() {
        requests = OustPreferences.getLoacalNotificationMsgs(SAVED_REQUESTS);
        if ((requests != null) && (requests.size() > 0)) {
            gson = new Gson();
            //this is the algorithm to swap CPL id's on top the list
         /*   if(requests.size()>=2) {
                for (int i = 0; i < requests.size(); i++) {
                    SubmitCourseCompleteRequest completeRequest = gson.fromJson(requests.get(i), SubmitCourseCompleteRequest.class);
                    if (completeRequest.getIsCPLCourse()) {
                        for (int j = 0; j < i; j++) {
                            if (!gson.fromJson(requests.get(j), SubmitCourseCompleteRequest.class).getIsCPLCourse()) {
                                Collections.swap(requests, j, i);
                                break;
                            }
                        }
                    }
                }
            }*/
            SubmitCourseCompleteRequest submitCourseCompleteRequest = gson.fromJson(requests.get(0), SubmitCourseCompleteRequest.class);
            pendingSubmissions = requests.size();
            if (submitCourseCompleteRequest != null) {
                if (OustSdkTools.checkInternetStatus()) {
                    if ((submitCourseCompleteRequest.getCourseColnId() != null) && (!submitCourseCompleteRequest.getCourseColnId().isEmpty())) {
                        sendCollectionCourseSubmitRequest(submitCourseCompleteRequest);
                    } else {
                        sendRequest(submitCourseCompleteRequest);

                    }
                } else {
                    OustSdkTools.showToast("No internet");
                    sendEndBroadCast(false, pendingSubmissions);
                }
            }
        } else {
            checkForLocallImageSave();
        }
    }

    private void sendStartBroadCastToCPLActivity() {
        Intent intent = new Intent("SEND_START");
        sendBroadcast(intent);
    }

    private void sendEndBroadCast(boolean isSuccess, long courseId) {
        Intent intent = new Intent();
//        Intent broadcastIntent = new Intent();
        intent.setAction("com.oustme.oustsdk.SEND_COMPLETE");
        intent.putExtra("IS_SUCCESS", isSuccess);
        intent.putExtra("PENDING_SUBMISSIONS", courseId);
//        intent.setComponent(new ComponentName("com.oustme.oustsdk", "com.oustme.oustsdk.service.SubmitCourseCompleteService"));
        intent.setPackage("com.oustme.oustsdk");
//        intent.setClassName("com.oustme.oustsdk", "com.oustme.oustsdk.service");
        sendBroadcast(intent);
    }

    private void sendRequest(final SubmitCourseCompleteRequest sumbitCourseCompleteRequest) {
        try {

            String submitCourseComplete_url = OustSdkApplication.getContext().getResources().getString(R.string.submitCourseComplete_url);
            //submitCourseComplete_url = submitCourseComplete_url.replace("{userId}", sumbitCourseCompleteRequest.getUserId());
            submitCourseComplete_url = submitCourseComplete_url.replace("{courseId}", sumbitCourseCompleteRequest.getCourseId());
            JSONObject jsonObject = OustSdkTools.getRequestObject("");
            jsonObject.put("timestamp", sumbitCourseCompleteRequest.getTimeStamp());
            jsonObject.put("contentPlayListId", sumbitCourseCompleteRequest.getCplId());
            jsonObject.put("studentid", sumbitCourseCompleteRequest.getUserId());
            //submitCourseComplete_url = submitCourseComplete_url.replace("{timestamp}", sumbitCourseCompleteRequest.getTimeStamp());
            try {
                submitCourseComplete_url = HttpManager.getAbsoluteUrl(submitCourseComplete_url);
                Log.d(TAG, "sendRequest: " + submitCourseComplete_url);
                Log.d(TAG, "sendRequest: courseCpleteDatA:" + jsonObject.toString());

                ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseComplete_url, jsonObject, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
                        if (cplCollectionData != null) {
                            CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                            cplModelData.setUploadedToServer(true);
                            cplCollectionData.getCplModelDataHashMap().put("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
                        }
                        OustPreferences.save("CplCollectionData", gson.toJson(cplCollectionData));
                        sendEndBroadCast(true, pendingSubmissions - 1);
                        OustPreferences.saveTimeForNotification("cplId", 0);
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        cardDownloadOver(commonResponse);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendEndBroadCast(false, pendingSubmissions);
                        //Toast.makeText(SubmitCourseCompleteService.this, "SubmitCourseCompleteService Error :: "+error, Toast.LENGTH_SHORT).show();
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitCourseComplete_url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
                        if(cplCollectionData!=null)
                        {
                            CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get(Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                            cplModelData.setUploadedToServer(true);
                            cplCollectionData.getCplModelDataHashMap().put(Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
                        }
                        OustPreferences.save("CplCollectionData",gson.toJson(cplCollectionData));
                        sendEndBroadCast(true, pendingSubmissions-1);
                        OustPreferences.saveTimeForNotification("cplId", 0);
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        cardDownloadOver(commonResponse);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendEndBroadCast(false, pendingSubmissions);
                        //Toast.makeText(SubmitCourseCompleteService.this, "SubmitCourseCompleteService Error :: "+error, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCollectionCourseSubmitRequest(final SubmitCourseCompleteRequest sumbitCourseCompleteRequest) {
        try {
            String submitCourseComplete_url = OustSdkApplication.getContext().getResources().getString(R.string.submitCourseComplete_url);
            //submitCourseComplete_url = submitCourseComplete_url.replace("{userId}", sumbitCourseCompleteRequest.getUserId());
            submitCourseComplete_url = submitCourseComplete_url.replace("{courseId}", sumbitCourseCompleteRequest.getCourseId());
            //submitCourseComplete_url = submitCourseComplete_url.replace("{timestamp}",sumbitCourseCompleteRequest.getTimeStamp());
            //submitCourseComplete_url = submitCourseComplete_url.replace("{courseColnId}", sumbitCourseCompleteRequest.getCourseColnId());
            JSONObject jsonObject = OustSdkTools.getRequestObject("");
            jsonObject.put("timestamp", sumbitCourseCompleteRequest.getTimeStamp());
            jsonObject.put("contentPlayListId", sumbitCourseCompleteRequest.getCplId());
            jsonObject.put("studentid", sumbitCourseCompleteRequest.getUserId());
            jsonObject.put("courseColnId", sumbitCourseCompleteRequest.getCourseColnId());

            try {
                submitCourseComplete_url = HttpManager.getAbsoluteUrl(submitCourseComplete_url);

                ApiCallUtils.doNetworkCall(Request.Method.PUT, submitCourseComplete_url, jsonObject, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
                        if (cplCollectionData != null) {
                            CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                            cplModelData.setUploadedToServer(true);
                            cplCollectionData.getCplModelDataHashMap().put("COURSE" + Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
                        }
                        OustPreferences.save("CplCollectionData", gson.toJson(cplCollectionData));
                        sendEndBroadCast(true, pendingSubmissions - 1);
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        cardDownloadOver(commonResponse);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendEndBroadCast(false, pendingSubmissions);
                        //Toast.makeText(SubmitCourseCompleteService.this, "SubmitCourseCompleteService Error :: "+error, Toast.LENGTH_SHORT).show();
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, submitCourseComplete_url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        CplCollectionData cplCollectionData = gson.fromJson(OustPreferences.get("CplCollectionData"), CplCollectionData.class);
                        if(cplCollectionData!=null)
                        {
                            CplModelData cplModelData = cplCollectionData.getCplModelDataHashMap().get(Long.valueOf(sumbitCourseCompleteRequest.getCourseId()));
                            cplModelData.setUploadedToServer(true);
                            cplCollectionData.getCplModelDataHashMap().put(Long.valueOf(sumbitCourseCompleteRequest.getCourseId()), cplModelData);
                        }
                        OustPreferences.save("CplCollectionData",gson.toJson(cplCollectionData));
                        sendEndBroadCast(true, pendingSubmissions-1);
                        CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                        cardDownloadOver(commonResponse);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendEndBroadCast(false, pendingSubmissions);
                        //Toast.makeText(SubmitCourseCompleteService.this, "SubmitCourseCompleteService Error :: "+error, Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    int noofAttempt = 0;

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

    private void checkForLocallImageSave() {
        try {
            if ((OustPreferences.get("localImagePathFormTanent") != null) && (!OustPreferences.get("localImagePathFormTanent").isEmpty())) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(OustPreferences.get("localImagePathFormTanent"), options);
                bitMapToString(bitmap);
            }
        } catch (Exception e) {
            this.stopSelf();
        }
    }

    public void bitMapToString(Bitmap bitmap) {
        try {
            Bitmap finalBitmap;
            if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
                finalBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            } else {
                finalBitmap = bitmap;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageString = Base64.encodeToString(b, Base64.DEFAULT);
            imageSaveBtnClick(imageString);
        } catch (Exception e) {
            this.stopSelf();
        }
    }

    public void imageSaveBtnClick(String imageString) {
        try {
            UserSettingRequest userSettingRequest = new UserSettingRequest();
            if ((imageString != null) && (imageString.length() > 10)) {
                userSettingRequest.setAvatarImgData(imageString);
                if (!OustSdkTools.checkInternetStatus()) {
                    return;
                }
                sendSaveSettingRequest(userSettingRequest, OustAppState.getInstance().getActiveUser());
            }
        } catch (Exception e) {
            this.stopSelf();
        }
    }

    public void sendSaveSettingRequest(UserSettingRequest userSettingRequest, ActiveUser activeUser) {
        String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.usersetting_url);
        settingUrl = settingUrl.replace("{studentid}", activeUser.getStudentid());
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(userSettingRequest);
        try {
            settingUrl = HttpManager.getAbsoluteUrl(settingUrl);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    saveSettingProcessFinish(commonResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, settingUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CommonResponse commonResponse = gson.fromJson(response.toString(), CommonResponse.class);
                    saveSettingProcessFinish(commonResponse);
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

    public void saveSettingProcessFinish(final CommonResponse commonResponse) {
        if ((commonResponse != null) && (commonResponse.isSuccess())) {
            OustPreferences.clear("localImagePathFormTanent");
        } else {
            noofAttempt++;
            if (noofAttempt < 3) {
                checkForLocallImageSave();
            }
        }
    }
}
