package com.oustme.oustsdk.oustHandler;

import android.content.Context;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationFixRequest;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

public class InstrumentationHandler {

    private static final String TAG = "InstrumentationHandler";

    public InstrumentationHandler() {
    }

    public void hitInstrumentationAPI(Context context, InstrumentationMailRequest instrumentationMailRequest){

        try{

            ActiveUser activeUser= OustAppState.getInstance().getActiveUser();
            String instrumentation_api_url = context.getResources().getString(R.string.instrumentation_api_url);
            instrumentation_api_url = HttpManager.getAbsoluteUrl(instrumentation_api_url);
            String jsonParamsString = new Gson().toJson(instrumentationMailRequest);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(new JSONObject(jsonParamsString));
            if(activeUser!=null){
                jsonParams.put("userid",activeUser.getStudentid());
            }


            ApiCallUtils.doNetworkCall(Request.Method.PUT, instrumentation_api_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {

                            //TODO: handle success intimation to user (need discussion)
                            Log.e(TAG,"Success of instrumentation mail");

                        }else{
                            //TODO: handle error intimation to user (need discussion)
                            Log.e(TAG,"Error of instrumentation mail");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: handle api error  (need discussion)
                    Log.e(TAG,"API Error of instrumentation mail");

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void hitInstrumentationFixAPI(Context context, InstrumentationFixRequest instrumentationFixRequest){

        try{

            ActiveUser activeUser= OustAppState.getInstance().getActiveUser();
            String course_completion_api_url = context.getResources().getString(R.string.course_completion_api_url);
            course_completion_api_url = HttpManager.getAbsoluteUrl(course_completion_api_url);
            String jsonParamsString = new Gson().toJson(instrumentationFixRequest);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(new JSONObject(jsonParamsString));
            if(activeUser!=null){
                jsonParams.put("userId",activeUser.getStudentid());
            }

            ApiCallUtils.doNetworkCall(Request.Method.POST, course_completion_api_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success")) {

                            //TODO: handle success intimation to user (need discussion)
                            Log.e(TAG,"Success of instrumentation fix");

                        }else{
                            //TODO: handle error intimation to user (need discussion)
                            Log.e(TAG,"Error of instrumentation fix");
                            OustSdkTools.showToast("Please try again later");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: handle api error  (need discussion)
                    Log.e(TAG,"API Error of instrumentation fix");
                    OustSdkTools.showToast("Please try again later");

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
}
