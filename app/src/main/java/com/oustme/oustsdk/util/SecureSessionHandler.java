package com.oustme.oustsdk.util;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.request.SignInRequest;
import com.oustme.oustsdk.response.common.SignInResponse;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureSessionHandler {

    private static final String TAG = "SecureSessionHandler";
    private SessionHandlerCallback sessionHandlerCallback;

    public interface SessionHandlerCallback{
        void onSessionCreated(SignInResponse signInResponse);

        void onSessionCreationFailed(String localizedMessage);
    }

    public void createSession(SessionHandlerCallback sessionHandlerCallback){
        this.sessionHandlerCallback = sessionHandlerCallback;
        loginApiCall();
    }

    private  void loginApiCall() {
        SignInRequest signInRequest = new SignInRequest();
        if ((OustPreferences.get("gcmToken") != null)) {
            signInRequest.setDeviceToken(OustPreferences.get("gcmToken"));
        }
        String encryptedPassword = null;
        String otpStr = "test";
        try {
            if (otpStr.matches("[a-fA-F0-9]{32}")) {
                encryptedPassword = otpStr;
            } else {
                encryptedPassword = getMD5EncodedString(otpStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        String userName = OustPreferences.get("test_userid");
        String institutional_id = OustPreferences.get("test_orgid");
        signInRequest.setStudentid(userName);
        signInRequest.setPassword(encryptedPassword);
        signInRequest.setClientEncryptedPassword(true);
        signInRequest.setInstitutionLoginId(institutional_id);
        Log.d(TAG, "oustLoginA: ");
        try {
            String signInUrl = OustSdkApplication.getContext().getResources().getString(R.string.signinV3);
            signInUrl = HttpManager.getAbsoluteUrl(signInUrl);

            final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(signInRequest);
            final SignInResponse[] signInResponse = new SignInResponse[1];
            Log.d(TAG, "oustLoginA: " + jsonParams);

            ApiCallUtils.doNetworkCallWithoutAuth(Request.Method.POST, signInUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ApiCallUtils.setIsLoggedOut(false);
                    Log.d(TAG, "islogout sdk: false:");
                    signInResponse[0] = gson.fromJson(response.toString(), SignInResponse.class);
                    sessionHandlerCallback.onSessionCreated(signInResponse[0]);
                    saveAuthToken(signInResponse[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    sessionHandlerCallback.onSessionCreationFailed(error.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getMD5EncodedString(String encryptString) throws NoSuchAlgorithmException {
        String encodedString = "";
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptString.getBytes());

        byte[] byteData = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }


        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        encodedString = hexString.toString();
        return encodedString;
    }

    public void saveAuthToken(SignInResponse signInResponse){
        if (signInResponse != null) {
            if(signInResponse.isSuccess()){
                OustPreferences.save("authToken",signInResponse.getAuthToken());
            }
        }
    }


}
