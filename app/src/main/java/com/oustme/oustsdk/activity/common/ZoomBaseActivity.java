package com.oustme.oustsdk.activity.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;


/**
 * Created by admin on 03/01/18.
 */

public class ZoomBaseActivity extends Activity {

    private static final String TAG = "ZoomBaseActivity";
    private RelativeLayout meeting_baseLayout,meeting_loaderback,downloadliveapppopup;
    private EditText meetingid_edittext;
    private SwipeRefreshLayout meeting_loader;
    private boolean meetingStarted=false;
    private ImageView startmeeting_btnlayout, mImageViewClose;
    private TextView appdownload_canclebutton,appdownload_button,live_training_text,error_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_zoombase);
        initViews();
        initListeners();
        boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
        if (!isAppInstalled) {
            showAppDownloadPopup();
        }else
            {
            startmeeting_btnlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((meetingid_edittext.getText().toString().length() > 8)) {
                        if (!meetingStarted) {
                            meetingStarted = true;
                            hideKeyboard(meetingid_edittext);
                            showLoader();
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                            intent.putExtra("zoommeetingId", meetingid_edittext.getText().toString());
                            intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                            intent.putExtra("isComingThroughOust", true);
                            startActivity(intent);
                            ZoomBaseActivity.this.finish();

//                    String packageName = getApplicationContext().getPackageName();
//                        Intent intent = new Intent("oust_zoommeeting");
//                        intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
//                        intent.putExtra("meetingId", meetingid_edittext.getText().toString());
//                        sendBroadcast(intent);
//                        ZoomBaseActivity.this.finish();
                        }
//                    if((packageName.equalsIgnoreCase("com.oustme.oustlive"))) {
//                        Intent intent = new Intent("oust_zoommeeting");
//                        intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
//                        intent.putExtra("meetingId", meetingid_edittext.getText().toString());
//                        sendBroadcast(intent);
//                        ZoomBaseActivity.this.finish();
//                    }else {
//                        boolean isAppInstalled = appInstalledOrNot("com.oustme.oustlive");
//                        if (isAppInstalled) {
//                            Intent intent = new Intent();
//                            intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.activity.SplashActivity_"));
//                            intent.putExtra("meetingId", meetingid_edittext.getText().toString());
//                            intent.putExtra("tanentid", OustPreferences.get("tanentid"));
//                            intent.putExtra("saml_userId", OustAppState.getInstance().getActiveUser().getStudentid());
//                            startActivity(intent);
//                            ZoomBaseActivity.this.finish();
//                        } else {
//                            //generate branch link
//                        }
//                    }
                    } else {
                        OustSdkTools.showToast("Enter valid meeting id");
                    }
                }
            });
        }
//        OustGATools.getInstance().reportPageViewToGoogle(ZoomBaseActivity.this,"Zoom Landing Page");
    }

    private void initListeners() {
        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {}
        return false;
    }

    private void initViews() {

        Log.d(TAG, "initViews: ");
        
        mImageViewClose = findViewById(R.id.iv_close);
        startmeeting_btnlayout= findViewById(R.id.startmeeting_btnlayout);
        meetingid_edittext= findViewById(R.id.meetingid_edittext);
        meeting_loader= findViewById(R.id.meeting_loader);
        meeting_baseLayout= findViewById(R.id.meeting_baseLayout);
        meeting_loaderback= findViewById(R.id.meeting_loaderback);
        appdownload_button= findViewById(R.id.appdownload_button);
        appdownload_canclebutton= findViewById(R.id.appdownload_canclebutton);
        downloadliveapppopup= findViewById(R.id.downloadliveapppopup);
        live_training_text= findViewById(R.id.live_training_text);
        error_text= findViewById(R.id.error_text);

        appdownload_button.setText(OustStrings.getString("download"));
        appdownload_canclebutton.setText(OustStrings.getString("cancel"));
        live_training_text.setText(OustStrings.getString("live_training"));
        error_text.setText(OustStrings.getString("zoom_error_msg"));
    }

    private void showLoader(){
        try {
            meeting_loaderback.setVisibility(View.VISIBLE);
            meeting_loader.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            meeting_loader.post(new Runnable() {
                @Override
                public void run() {
                    meeting_loader.setRefreshing(true);
                }
            });
            meeting_loader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showAppDownloadPopup(){
        downloadliveapppopup.setVisibility(View.VISIBLE);
        appdownload_canclebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZoomBaseActivity.this.finish();
            }
        });
        appdownload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.oustme.oustlive")));
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                ZoomBaseActivity.this.finish();
            }
        });
    }



    public void hideKeyboard(View v){
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
