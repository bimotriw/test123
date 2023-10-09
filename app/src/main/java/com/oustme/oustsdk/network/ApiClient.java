package com.oustme.oustsdk.network;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;


import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.SecureSessionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ApiClient {

    private static final String TAG = "ApiClient";
    private static String BASE_URL = "";
    private static String LOGIN_BASE_URL = "";
    //private static Retrofit mRetrofit;

    public static void setBaseUrl(String baseUrl) {

        BASE_URL = baseUrl;
        // mRetrofit = intialise();

    }

    public static void stringRequest(Context mContext, int method, String url, final NResultListener<String> mStringNResultListener) {

        int type = method == 0 ? Request.Method.GET : Request.Method.POST;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: from string request" + error.getLocalizedMessage());
                        //mStringNResultListener.onFailure(error.networkResponse.statusCode);
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("api-key", OustPreferences.get("api_key"));
                headers.put("org-id", OustPreferences.get("tanentid"));
                return headers;
            }

        };
        NetworkRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }


    public static void stringRequest(Context mContext, int method, String url, final HashMap params, final String mSessionToken, final NResultListener<String> mStringNResultListener) {

        int type = method == 0 ? Request.Method.GET : Request.Method.POST;

        StringRequest stringRequest = new StringRequest(type, BASE_URL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: from string request" + error.getLocalizedMessage());
                        //mStringNResultListener.onFailure(error.networkResponse.statusCode);
                        // Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "8");
                headers.put("AuthToken", mSessionToken);

                return headers;
            }

            @Override
            public byte[] getBody() {


                //  Log.d(TAG, "getBody: "+params2.toString().getBytes());
                return new JSONObject(params).toString().getBytes();
            }


            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        NetworkRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public static void jsonRequest4(final Context mContext, final int method, final String url, final HashMap paramas, final String mSessionToken, final NResultListener<JSONObject> mStringNResultListener) {
        //int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        Log.d(TAG, "jsonRequest: type" + BASE_URL + url + " method:" + method);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("OnResponseDat:" + response.toString());
                        Log.d(TAG, "onResponse of JSON: " + response.toString());
                        // mTxtDisplay.setText("Response: " + response.toString());

                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: json object" + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                        Log.d(TAG, "onErrorResponse jsonobject: " + error.toString());
                        mStringNResultListener.onFailure(500);
                        //Log.d(TAG, "onErrorResponse: "+error);
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)

                            new SecureSessionHandler().createSession(new SecureSessionHandler.SessionHandlerCallback() {
                                @Override
                                public void onSessionCreated(SignInResponse signInResponse) {
                                    jsonRequest(mContext,method,url,paramas,mSessionToken,mStringNResultListener);
                                }

                                @Override
                                public void onSessionCreationFailed(String localizedMessage) {

                                }
                            });

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("api-key", OustPreferences.get("api_key"));
                headers.put("org-id", OustPreferences.get("tanentid"));
                //headers.put("Content-Type", "application/json");
                // Log.d(TAG, "getHeaders: "+headers.toString());
                return headers;
            }

            @Override
            public byte[] getBody() {
                if (mSessionToken == null && paramas == null) {
                    return null;
                }
                Log.d(TAG, "getBody json object: " + paramas.toString());
                if (mSessionToken != null) {
                    try {
                        return new JSONObject(mSessionToken).toString().getBytes();
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                return new JSONObject(paramas).toString().getBytes();

            }


            @Override
            public String getBodyContentType() {
                if (mSessionToken == null && paramas == null) {
                    return null;
                } else {
                    return "application/json";
                }
            }
        };

        NetworkRequest.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }


    public static void jsonRequest(final Context mContext, final int method, final String url, final HashMap paramas, final String mSessionToken, final NResultListener<JSONObject> mStringNResultListener) {
        //int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        Log.d(TAG, "jsonRequest: type" + BASE_URL + url + " method:" + method);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // mTxtDisplay.setText("Response: " + response.toString());

                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: json object" + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                        Log.d(TAG, "onErrorResponse jsonobject: " + error.toString());
                        mStringNResultListener.onFailure(500);
                        //Log.d(TAG, "onErrorResponse: "+error);
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
                            new SecureSessionHandler().createSession(new SecureSessionHandler.SessionHandlerCallback() {
                                @Override
                                public void onSessionCreated(SignInResponse signInResponse) {
                                    jsonRequest(mContext,method,url,paramas,mSessionToken,mStringNResultListener);
                                }

                                @Override
                                public void onSessionCreationFailed(String localizedMessage) {

                                }
                            });
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("api-key", OustPreferences.get("api_key"));
                headers.put("org-id", OustPreferences.get("tanentid"));
                String authToken = OustPreferences.get("authToken");
                if (!TextUtils.isEmpty(authToken))
                    headers.put("Authorization", authToken);
                //headers.put("Content-Type", "application/json");
                // Log.d(TAG, "getHeaders: "+headers.toString());
                return headers;
            }

            @Override
            public byte[] getBody() {
                if (mSessionToken == null && paramas == null) {
                    return null;
                }
                Log.d(TAG, "getBody json object: " + paramas.toString());
                if (mSessionToken != null) {
                    try {
                        return new JSONObject(mSessionToken).toString().getBytes();
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                return new JSONObject(paramas).toString().getBytes();

            }


            @Override
            public String getBodyContentType() {
                if (mSessionToken == null && paramas == null) {
                    return null;
                } else {
                    return "application/json";
                }
            }
        };

        NetworkRequest.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public static void jsonRequest3(final Context mContext, int method, String url, final NResultListener<JSONObject> mStringNResultListener) {
        //int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        Log.d(TAG, "jsonRequest: type" + BASE_URL + url + " method:" + method);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // mTxtDisplay.setText("Response: " + response.toString());

                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: json object" + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                        Log.d(TAG, "onErrorResponse jsonobject: " + error.toString());
                        mStringNResultListener.onFailure(500);
                        //Log.d(TAG, "onErrorResponse: "+error);
//                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
//                            ApiCallUtils.logoutApiCall();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("api-key", OustPreferences.get("api_key"));
                headers.put("org-id", OustPreferences.get("tanentid"));
                String authToken = OustPreferences.get("authToken");
                if (!TextUtils.isEmpty(authToken))
                    headers.put("Authorization", authToken);
                //headers.put("Content-Type", "application/json");
                // Log.d(TAG, "getHeaders: "+headers.toString());
                return headers;
            }
        };
        NetworkRequest.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public static void jsonRequestLogin(final Context mContext, int method, String url, final HashMap paramas, final String mSessionToken, final NResultListener<JSONObject> mStringNResultListener) {
        int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (type, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // mTxtDisplay.setText("Response: " + response.toString());

                        try {
                            mStringNResultListener.onResult(200, response);

                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                        mStringNResultListener.onFailure(400);
                        //Log.d(TAG, "onErrorResponse: " + error.networkResponse.statusCode);
//                        if (error.networkResponse != null && error.networkResponse.statusCode == 401)
//                            ApiCallUtils.logoutApiCall();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "8");
                headers.put("AuthToken", mSessionToken);
                headers.put("Content-Type", "application/json");

                Log.d(TAG, "getHeaders: " + headers.toString());
                return headers;
            }

            @Override
            public byte[] getBody() {
                Log.d(TAG, "getBody: " + paramas.toString());
                return new JSONObject(paramas).toString().getBytes();
            }


            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        NetworkRequest.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }


    public static void jsonArrayRequest(final FragmentActivity mContext, final Map<String, String> parameters, int method, String url, final String mSessionToken, final NResultListener<JSONArray> mStringNResultListener) {
        int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        Log.d(TAG, "jsonArrayRequest: " + url);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (type, BASE_URL + url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // mTxtDisplay.setText("Response: " + response.toString());
                        try {
                            mStringNResultListener.onResult(200, response);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                        mStringNResultListener.onFailure(400);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "8");
                headers.put("AuthToken", mSessionToken);
                return headers;
            }


        };
        //Log.d(TAG, "jsonArrayRequest: "+parameters.toString());

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                450000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NetworkRequest.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }

    public static void CancelRequests(final FragmentActivity mContext) {

    }


   /* public static void jsonArrayRequest2(final FragmentActivity mContext, final Map<String, String> parameters, int method, String url, final String mSessionToken, final NResultListener<JSONArray> mStringNResultListener) {
        int type = method == 0 ? Request.Method.GET : Request.Method.POST;
        Log.d(TAG, "jsonArrayRequest: " + mSessionToken);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (type, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // mTxtDisplay.setText("Response: " + response.toString());
                        try {
                            mStringNResultListener.onResult(200, response);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mStringNResultListener.onFailure(400);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "8");
                headers.put("AuthToken", mSessionToken);
                return headers;
            }


        };
        //Log.d(TAG, "jsonArrayRequest: "+parameters.toString());

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        NetworkRequest.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }
*/

    public interface NResultListener<T> {

        void onResult(int resultCode, T tResult);

        void onFailure(int mError);
    }




    /* public static Retrofit intialise() {

     *//*OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS).build();*//*
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

    }*/









/*
    Map<String, String> headers = new HashMap<>();

    GsonRequest<Message> gsonRequest = new GsonRequest<>("", Message.class, headers, new Response.Listener<Message>() {
        @Override
        public void onResponse(Message response) {

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });*/

}
