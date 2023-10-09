package com.oustme.oustsdk.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.LandingActivity;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

public class LogoutMsgActivity extends Activity {

    private TextView messgae_tv;
    private LinearLayout okbtn;
    String urlRedirect;
    boolean isUserFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_logout_msg);

        okbtn = findViewById(R.id.okbtn);
        messgae_tv = findViewById(R.id.message);

        if (getIntent() != null) {
            String message = getIntent().getStringExtra("message");
            isUserFailed = getIntent().getBooleanExtra("isUserFailed", false);
            urlRedirect = getIntent().getStringExtra("urlRedirect");
            if (message != null && !message.isEmpty()) {
                messgae_tv.setText(message);
            }
        }

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLandingPage();

              /*  try {
                    if (!isUserFailed) {
                        if (urlRedirect != null && !urlRedirect.isEmpty()) {
                            boolean isAppInstalled = appInstalledOrNot(urlRedirect);
                            if (isAppInstalled) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(urlRedirect);
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + urlRedirect)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + urlRedirect)));
                                }
                            }
                        } else {
                            launchLandingPage();
                        }
                    } else {
                        launchLandingPage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }*/
            }
        });
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return false;
    }

    private void launchLandingPage() {
        OustLogDetailHandler.getInstance().setUserForcedOut(false);
        Intent intent;
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            intent = new Intent(OustSdkApplication.getContext(), LandingActivity.class);
        } else {
            intent = new Intent(OustSdkApplication.getContext(), NewLandingActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        launchLandingPage();
    }
}
