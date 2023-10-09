package com.oustme.oustsdk.activity.common.noticeBoard.presenters;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostCreateView;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostCreate;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostData;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class NBPostCreatePresenter {
    private static final String TAG = "NBPostCreatePresenter";
    private NBPostCreateView mNbPostCreateView;

    public NBPostCreatePresenter(NBPostCreateView mNbPostCreateView) {
        this.mNbPostCreateView = mNbPostCreateView;
    }
    public void attachVideo()
    {

    }
    public void attachImage()
    {

    }
    public void attachAudio()
    {

    }
    public void attachDocument()
    {

    }
    public void createPost(NBPostData nbPostData, String mStudentId){
        mNbPostCreateView.showProgressBar(1);
        String NbPostCreateUrl = OustSdkApplication.getContext().getResources().getString(R.string.nb_create_post);
        NbPostCreateUrl = NbPostCreateUrl.replace("{nbId}", nbPostData.getNbId()+"");
        NbPostCreateUrl = HttpManager.getAbsoluteUrl(NbPostCreateUrl);
        Gson gson = new GsonBuilder().create();
        String json = null;
        NBPostCreate nbPostCreate = new NBPostCreate();
        nbPostCreate.setNbPostData(nbPostData);
        nbPostCreate.setStudentid(mStudentId);
        JSONObject jsonObject = null;
        try {
            json = gson.toJson(nbPostCreate);
            jsonObject = new JSONObject(json);
            //jsonObject.put("nbPostData", json);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        final JSONObject finalJsonObject = jsonObject;
        Log.d(TAG, "createPost: "+finalJsonObject.toString());

        ApiCallUtils.doNetworkCall(Request.Method.POST, NbPostCreateUrl, OustSdkTools.getRequestObjectforJSONObject(finalJsonObject), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mNbPostCreateView.hideProgressBar(1);
                    mNbPostCreateView.postCreatedSuccessfully(response.getBoolean("success"));
                } catch (JSONException e) {
                    mNbPostCreateView.errorCreatingPost("unexpected error from Server");
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "" + error);
                mNbPostCreateView.errorCreatingPost("Something wrong with network please try again");
                mNbPostCreateView.hideProgressBar(1);
            }
        });


        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, NbPostCreateUrl, finalJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mNbPostCreateView.hideProgressBar(1);
                    mNbPostCreateView.postCreatedSuccessfully(response.getBoolean("success"));
                } catch (JSONException e) {
                    mNbPostCreateView.errorCreatingPost("unexpected error from Server");
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "" + error);
                mNbPostCreateView.errorCreatingPost("Something wrong with network please try again");
                mNbPostCreateView.hideProgressBar(1);
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
            @Override
            public byte[] getBody() {
                Log.d(TAG, "getBody: "+finalJsonObject);
                return finalJsonObject.toString().getBytes();

            }

            @Override
            public String getBodyContentType() {
                {
                    return "application/json";
                }
            }
        };
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.POST, NbPostCreateUrl, jsonObject, json, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                Log.d(TAG, "onResult: "+tResult.toString());
            }

            @Override
            public void onFailure(int mError) {

            }
        });*/
    }
}