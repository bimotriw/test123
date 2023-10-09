package com.oustme.oustsdk.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SingleSessionHandler {

    private static final String TAG = "SingleSessionHandler";
    static final int YES = 1;
    static final int NO = 0;
    private SingleSessionAlertReceiver receiver;
    SingleSessionAlertCallback callback;
    private static SingleSessionHandler sessionHandler;

    private List<ApiCallParam> apiCallParams;

    public static SingleSessionHandler getInstance() {
        if (sessionHandler == null)
            sessionHandler = new SingleSessionHandler();
        return sessionHandler;
    }

    private interface SingleSessionAlertCallback {
        void onContinue();

        void onCancel();
    }

    void handleNetworkCallWithoutAuth(int requestMethod, String url, JSONObject requestParams, ApiCallUtils.NetworkCallback callback) {
        registerSingleSessionAlertReceiver(requestMethod, url, requestParams, callback);
        Intent intent = new Intent(OustSdkApplication.getContext(), SingleSessionAlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
    }

    void handleNetworkCall(int requestMethod, String url, JSONObject requestParams, ApiCallUtils.NetworkCallback callback) {
        if (apiCallParams == null)
            apiCallParams = new ArrayList<>();
        apiCallParams.add(new ApiCallParam(requestMethod, url, requestParams, callback));
        registerSingleSessionAlertReceiverV1(requestMethod, url, requestParams, callback);
        Intent intent = new Intent(OustSdkApplication.getContext(), SingleSessionAlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(intent);
    }

    private void registerSingleSessionAlertReceiver(final int requestMethod, final String url, final JSONObject jsonObject, final ApiCallUtils.NetworkCallback callback) {

        setCallback(new SingleSessionAlertCallback() {
            @Override
            public void onContinue() {
                Log.e(TAG, "onContinue");

                String signInUrl = OustSdkApplication.getContext().getResources().getString(R.string.signinV3);
                String verifySignInUrl = OustSdkApplication.getContext().getResources().getString(R.string.verifyAndSignInUser);

                if (url.contains(signInUrl) || url.contains(verifySignInUrl)) {
                    Log.d(TAG, "onContinue: "+url);
                    try {
                        jsonObject.put("forceLogin", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
                ApiCallUtils.doNetworkCallWithoutAuth(requestMethod, url, jsonObject, callback);
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel");
            }
        });
    }

    private void registerSingleSessionAlertReceiverV1(final int requestMethod, final String url, final JSONObject jsonObject, final ApiCallUtils.NetworkCallback callback) {

        setCallback(new SingleSessionAlertCallback() {
            @Override
            public void onContinue() {
                Log.e(TAG, "onContinue");

                for (ApiCallParam apiCallParam : apiCallParams)
                    ApiCallUtils.doNetworkCall(apiCallParam.requestMethod, apiCallParam.url, apiCallParam.requestParams, apiCallParam.callback);
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel");
            }
        });
    }


    public class SingleSessionAlertReceiver extends BroadcastReceiver {
        public static final String ACTION = "com.oust.SingleSessionAlert";
        SingleSessionAlertCallback callback;

        public void setCallback(SingleSessionAlertCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int code = intent.getIntExtra("dataToPass", 0);
//            Toast.makeText(OustSdkApplication.getContext(), "" + code, Toast.LENGTH_SHORT).show();
            if (code == YES)
                callback.onContinue();
            if (code == NO)
                callback.onCancel();
            try {
                OustSdkApplication.getContext().unregisterReceiver(receiver);
            } catch (Exception e) {
                Log.e(TAG, "unable to unregister Receiver");
            }
        }
    }

    public void setCallback(SingleSessionAlertCallback callback) {
        this.callback = callback;
    }

    void setDialogCallbackCode(int code) {
        if (callback == null)
            return;
        if (code == YES)
            this.callback.onContinue();
        if (code == NO)
            this.callback.onCancel();
    }

    class ApiCallParam {
        private int requestMethod;
        private String url;
        private JSONObject requestParams;
        private ApiCallUtils.NetworkCallback callback;

        public ApiCallParam(int requestMethod, String url, JSONObject requestParams, ApiCallUtils.NetworkCallback callback) {
            this.requestMethod = requestMethod;
            this.url = url;
            this.requestParams = requestParams;
            this.callback = callback;
        }

        public int getRequestMethod() {
            return requestMethod;
        }

        public String getUrl() {
            return url;
        }

        public JSONObject getRequestParams() {
            return requestParams;
        }

        public ApiCallUtils.NetworkCallback getCallback() {
            return callback;
        }
    }

}
