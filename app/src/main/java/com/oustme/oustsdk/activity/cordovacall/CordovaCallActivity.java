package com.oustme.oustsdk.activity.cordovacall;

import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.common.OustLoginCallBack;
import com.oustme.oustsdk.launcher.CordovaLauncher;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;

public class CordovaCallActivity extends AppCompatActivity implements OustLoginCallBack {
    private static final String TAG = "CordovaCallActivity";
    private Button mOustButton;
    private ProgressBar mTextViewProgress;
    private TextView textViewPerc, txtView_status;
    private String username, orgid;
    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cordova_call);

        /*CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        // each of the below mentioned fields are optional
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", "Duraivel Sellapillai");    // String
        profileUpdate.put("Identity", "Durai2");      // String or number
        profileUpdate.put("Email", "duraivel.sellapillai@betterplace.co.in"); // Email address of the user
        profileUpdate.put("Phone", "");   // Phone (with the country code, starting with +)
        profileUpdate.put("Gender", "M");             // Can be either M or F

        clevertapDefaultInstance.onUserLogin(profileUpdate);
        clevertapDefaultInstance.pushProfile(profileUpdate);*/

        textViewPerc = findViewById(R.id.textViewPerc);
        txtView_status = findViewById(R.id.txtView_status);
        txtView_status.setText("Loading Oust..");

        if (getIntent() != null) {
            username = getIntent().getStringExtra("USERNAME");
            orgid = getIntent().getStringExtra("ORGID");
        }
        isFirstTime = true;
        mTextViewProgress = findViewById(R.id.progress_bar);

        if (username == null)
            username = OustPreferences.get("test_userid");
        if (orgid == null)
            orgid = OustPreferences.get("test_orgid");

        if(username==null)
            username="mlearning";
        if(orgid==null)
            orgid="exidelife";
        gotoOust();

    }

    private void gotoOust() {
        OustAuthData oustAuthData = new OustAuthData();
        oustAuthData.setOrgId(orgid);
        oustAuthData.setUsername(username);
        /*OustNotificationConfig notificationConfig = new OustNotificationConfig();
        notificationConfig.setPushNotificationType(PushNotificationType.FCM);
        notificationConfig.setToken("your FCM Token");
        notificationConfig.setServerKey("Your server Key");*/
        /*try {
            OustLauncher.getInstance().launch(CordovaCallActivity.this, oustAuthData,CordovaCallActivity.this);
        } catch (OustException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }*/

        try {
            CordovaLauncher.getInstance().launch(CordovaCallActivity.this, oustAuthData,CordovaCallActivity.this);
        } catch (OustException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if(e!=null && e.getMessage().equalsIgnoreCase("User name not found in Oust Auth Data")){
                txtView_status.setText(""+e.getMessage());
                ToastError(e.getMessage(), true);
            }else if(e!=null && e.getMessage().equalsIgnoreCase("OrgId can not be null or empty in Oust Auth Data")){
                txtView_status.setText(""+e.getMessage());
                ToastError(e.getMessage(), true);
            }
        }
    }

    @Override
    public void onStartDownloadingResourses() {
        ToastError("onStartDownloadingResourses", false);
    }

    private int progress = 0;

    @Override
    public void onProgressChanged(int i) {
        Log.d(TAG, "onProgressChanged: " + i);
        textViewPerc.setText(i + "%");
        progress = i;
        txtView_status.setText("Loading Oust Resources..");
    }

    @Override
    public void onError(String s) {
        Log.d(TAG, "onError: " + s);
        txtView_status.setText("onError: "+s);
        ToastError(s, true);
    }


    @Override
    public void onLoginError(String s) {
        txtView_status.setText("onLoginError: "+s);
        ToastError(s, true);
    }

    @Override
    public void onLoginProcessStart() {
        txtView_status.setText("Login Process Start..");
        ToastError("onLoginProcessStart", false);
    }

    @Override
    public void onLoginSuccess(boolean b) {
        txtView_status.setText("Login Success");
    }

    @Override
    public void onNetworkError() {
        txtView_status.setText("Network error");
        ToastError("onNetworkError", true);
    }

    @Override
    public void onOustLoginStatus(String message) {

    }

    @Override
    public void onOustContentLoaded() {
        Log.d(TAG, "onOustContentLoaded: ");
    }

    private void ToastError(String msg, boolean doFinish) {
        try {
            Toast.makeText(CordovaCallActivity.this, msg, Toast.LENGTH_LONG).show();
            if(doFinish) {
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: and closing");
        try {
            if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                progress = 100;
            }
            if (progress >= 100) {
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isFirstTime->"+isFirstTime);
        try{
            if(isFirstTime){
                isFirstTime = false;
            }else{
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}