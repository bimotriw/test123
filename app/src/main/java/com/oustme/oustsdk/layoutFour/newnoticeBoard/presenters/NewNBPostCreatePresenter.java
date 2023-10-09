package com.oustme.oustsdk.layoutFour.newnoticeBoard.presenters;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.noticeBoard.callBacks.NBPostCreateView;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostCreate;
import com.oustme.oustsdk.activity.common.noticeBoard.model.request.NBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks.NewNBPostCreateView;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewNBPostCreate;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewNBPostData;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class NewNBPostCreatePresenter {
    private static final String TAG = "NewNBPostCreatePresent";
    private NewNBPostCreateView mNbPostCreateView;

    public NewNBPostCreatePresenter(NewNBPostCreateView mNbPostCreateView) {
        this.mNbPostCreateView = mNbPostCreateView;
    }

    public void attachVideo() {

    }

    public void attachImage() {

    }

    public void attachAudio() {

    }

    public void attachDocument() {

    }

    public void createPost(NewNBPostData nbPostData, String mStudentId) {
        mNbPostCreateView.showProgressBar(1);
        String NbPostCreateUrl = OustSdkApplication.getContext().getResources().getString(R.string.nb_create_post);
        NbPostCreateUrl = NbPostCreateUrl.replace("{nbId}", nbPostData.getNbId() + "");
        NbPostCreateUrl = HttpManager.getAbsoluteUrl(NbPostCreateUrl);
        Gson gson = new GsonBuilder().create();
        String json = null;
        NewNBPostCreate nbPostCreate = new NewNBPostCreate();
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
        Log.d(TAG, "createPost: " + finalJsonObject.toString());

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
    }
}