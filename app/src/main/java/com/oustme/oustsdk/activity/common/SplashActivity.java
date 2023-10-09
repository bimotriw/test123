package com.oustme.oustsdk.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.interfaces.common.OustLoginCallBack;
import com.oustme.oustsdk.launcher.OustAuthData;
//import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.launcher.OustNewLauncher;
import com.oustme.oustsdk.service.GCMType;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements OnNetworkChangeListener,OustLoginCallBack {

    private final String TAG = SplashActivity.class.getName();
    private TextView stringdownloadprogresstext;
    private RelativeLayout  stringprogressLayout;
    private LinearLayout splash_parentscreen;
    private ProgressBar stringdownloadprogress;


    private String institutional_id = "";
    private String userName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try{
            OustSdkTools.setLocale(SplashActivity.this);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        setContentView(R.layout.activity_splash);
        OustSdkApplication.setmContext(SplashActivity.this);
        initViews();
       // OustGATools.getInstance().reportPageViewToGoogle(SplashActivity.this,"Splash");

        userName=OustPreferences.get("test_userid");
        institutional_id=OustPreferences.get("test_orgid");
        if((userName!=null)&&(!userName.isEmpty())&&(institutional_id!=null)&&(!institutional_id.isEmpty())){
            try {
                OustAuthData oustAuthData = new OustAuthData(institutional_id,userName);
                
                /*Map<String, String> notification = new HashMap<>();
                notification.put("id", "3285");
                notification.put("title", "oust");
                notification.put("type", String.valueOf(GCMType.ASSESSMENT_DISTRIBUTE));
                notification.put("message", "Please complete, Assessment1-notification. Go: https://bnc.oustme.com/SKiFDhvyFX");
                notification.put("imageUrl","http://di5jfel2ggs8k.cloudfront.net/assessment/MJ/Default_Assmnt_Banner.jpg");
                OustLauncher.getInstance().launchNotification(SplashActivity.this, oustAuthData, SplashActivity.this, notification);*/

                /*Map<String, String> notification = new HashMap<>();
                notification.put("id", "6388");
                notification.put("title", "oust");
                notification.put("type", String.valueOf(GCMType.ASSESSMENT_DISTRIBUTE));
                notification.put("message", "OUST-UPDATE");
                notification.put("imageUrl","https://di5jfel2ggs8k.cloudfront.net/banner/mpower/banner_5b26458f-1058-393f-a9d1-93f7a8d98637.jpg");*/

                //OustLauncher.getInstance().launchNotification(SplashActivity.this, oustAuthData, SplashActivity.this, notification);

                OustLauncher.getInstance().launch(SplashActivity.this, oustAuthData, SplashActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }else {
            OustSdkTools.setSnackbarElements(splash_parentscreen, SplashActivity.this);
            final LinearLayout username_passlayout = (LinearLayout) findViewById(R.id.username_passlayout);
            final EditText orgtext = (EditText) findViewById(R.id.orgtext);
            final EditText usernametext = (EditText) findViewById(R.id.usernametext);
            orgtext.setHint(getResources().getString(R.string.org_id));
            usernametext.setHint(getResources().getString(R.string.user_name));
            TextView startbtn = (TextView) findViewById(R.id.startbtn);
            startbtn.setText(getResources().getString(R.string.start));
            username_passlayout.setVisibility(View.VISIBLE);
            stringprogressLayout.setVisibility(View.GONE);

            Log.d(TAG, "onCreate: start");
            startbtn.setOnClickListener(view -> {
                try {
                    Log.d(TAG, "onCreate: ");
                    if (!orgtext.getText().toString().isEmpty() && !usernametext.getText().toString().isEmpty()) {
                        OustAuthData oustAuthData = new OustAuthData(orgtext.getText().toString(), usernametext.getText().toString());
                        Log.d(TAG, "onClick: "+oustAuthData.getOrgId()+" "+oustAuthData.getUsername());
                        OustLauncher.getInstance().launch(SplashActivity.this, oustAuthData, SplashActivity.this);
                        username_passlayout.setVisibility(View.GONE);
                        stringprogressLayout.setVisibility(View.VISIBLE);
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                        SplashActivity.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });
        }
    }
//----------------
    //set previously selected language

    private void setUserData() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        institutional_id = intent.getStringExtra("orgId");
    }


//    =================================================

    private void initViews() {
        stringprogressLayout = findViewById(R.id.stringprogressLayout);
        stringdownloadprogress = findViewById(R.id.stringdownloadprogress);
        stringdownloadprogresstext = findViewById(R.id.stringdownloadprogresstext);
        TextView stringdownloadtext = findViewById(R.id.stringdownloadtext);
        splash_parentscreen = findViewById(R.id.splash_parentscreen);
        TextView errorButton = findViewById(R.id.errorButton);
        TextView oust_link = findViewById(R.id.oust_link);
        stringdownloadprogress.setMax(100);

        errorButton.setText(getResources().getString(R.string.try_again));
        oust_link.setText(getResources().getString(R.string.oust_link));
        stringdownloadtext.setText(getResources().getString(R.string.downloading_resources));
    }

    @Override
    public void onError(String message) {
        OustSdkTools.showToast(message);
    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onOustLoginStatus(String message) {

    }

    @Override
    public void onOustContentLoaded() {

    }

    @Override
    public void onStartDownloadingResourses() {

    }

    @Override
    public void onProgressChanged(int progress) {
        stringdownloadprogress.setProgress(progress);
        String progressText = progress +"%";
        stringdownloadprogresstext.setText(progressText);
    }

    @Override
    public void onLoginError(String message) {
        OustSdkTools.showToast(message);
    }

    @Override
    public void onLoginProcessStart() {
    }

    @Override
    public void onLoginSuccess(boolean status) {
        SplashActivity.this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            OustSdkTools.speakInit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onChange(String status) {
        Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

}
