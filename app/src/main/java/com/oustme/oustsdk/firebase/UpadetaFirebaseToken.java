package com.oustme.oustsdk.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Keep;
import androidx.core.app.NotificationCompat;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 04/10/16.
 */
@Keep
public class UpadetaFirebaseToken extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            String activeUserGet = OustPreferences.get("userdata");
            ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            if((activeUser!=null)&&(activeUser.getStudentid()!=null)){
                    sendRequestA(activeUser.getStudentid());
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendRequestA(final String studentId){
        String enrolllp_url = OustSdkApplication.getContext().getResources().getString(R.string.update_firebasetoken_url);
        enrolllp_url = enrolllp_url.replace("{userId}",studentId);

        ApiCallUtils.doNetworkCall(Request.Method.GET, enrolllp_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token=response.getString("token");
                    gotTokenA(token);
                } catch (JSONException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onFailure: ",error.toString());
            }
        });


    }

    public void gotTokenA(String firebaseToken){
        try {
            if ((firebaseToken != null) && (!firebaseToken.isEmpty())) {
                OustPreferences.save("firebaseToken",firebaseToken);
            }
        }catch (Exception e){}
    }
}
