package com.oustme.oustsdk.network;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oustme.oustsdk.interfaces.common.ServerResponseListener;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ItsMe on 6/8/16.
 */
public class VolleyRequest {

    private String Url, Json;
    private ServerResponseListener serverResponseListener;
    private Context context;
    private int RequestId;
    private int inputType = 1;

    //POST Method

    public VolleyRequest(Context context, String Url, String Json, ServerResponseListener serverResponseListener) {
        this.Url = Url;
        this.Json = Json;
        this.serverResponseListener = serverResponseListener;
        this.context = context;
        RequestId = -1;
        inputType = 1;
    }

    //POST Method

    public VolleyRequest(Context context, String Url, String Json, int RequestId, ServerResponseListener serverResponseListener) {
        this.Url = Url;
        this.Json = Json;
        this.serverResponseListener = serverResponseListener;
        this.context = context;
        this.RequestId = RequestId;
        inputType = 1;
    }

    //Get Method

    public VolleyRequest(Context context, String Url, int RequestId, ServerResponseListener serverResponseListener) {
        this.Url = Url;
        this.serverResponseListener = serverResponseListener;
        this.context = context;
        this.RequestId = RequestId;
        inputType = 0;
    }


    public void executeToServer() {


        switch (inputType) {

            case 0:

                setGETVolley();

                break;

            case 1:

                setPOSTVolley();

                break;

        }


    }


    private void setPOSTVolley() {

        Log.i("json...", Json);
        Log.i("url...", Url);

        ServerRequest serverRequest = new ServerRequest(inputType, Url, Json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("Response", "**" + jsonObject);

                if (serverResponseListener != null) {
                    if (RequestId == -1) {
                        serverResponseListener.onSuccess(jsonObject);
                    } else {
                        serverResponseListener.onSuccess(jsonObject, RequestId);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (serverResponseListener != null) {
                    serverResponseListener.onError(volleyError.toString());
                }
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
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (context != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(serverRequest);
        }


    }


    private void setGETVolley() {

        Log.i("Volley_GET...", Url);

        StringRequest stringRequest = new StringRequest(Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            Log.i("Response", "**" + jsonObject);

                            if (serverResponseListener != null) {
                                if (RequestId == -1) {
                                    serverResponseListener.onSuccess(jsonObject);
                                } else {
                                    serverResponseListener.onSuccess(jsonObject, RequestId);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (serverResponseListener != null) {
                            serverResponseListener.onError(error.toString());
                        }
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);

    }

}

