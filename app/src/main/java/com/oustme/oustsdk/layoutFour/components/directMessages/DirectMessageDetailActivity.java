package com.oustme.oustsdk.layoutFour.components.directMessages;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.layoutFour.data.response.directMessageResponse.UserMessageList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DirectMessageDetailActivity extends AppCompatActivity {

    private int color;
    private int bgColor;
    private WebView directMessageWebView;
    private ImageView backButton;

    //Branding loader
    private RelativeLayout branding_mani_layout;
    //End

    private UserMessageList userMessageList;
    private ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_direct_message_detail);

        initView();
        initListener();
    }

    private void initListener() {
        backButton.setOnClickListener(view -> onBackPressed());

        directMessageWebView.setWebChromeClient(new MyBrowser());

        String directMsgUrl = OustPreferences.get("webAppLink");
        String tenantId = OustPreferences.get("tanentid").replace(" ", "");
        if (activeUser == null) {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        }
        if (directMsgUrl == null) {
            if (tenantId.equalsIgnoreCase("oustqa")) {
                directMsgUrl = "https://stagew2.oustme.com/#!/";
            } else if (tenantId.equalsIgnoreCase("oustdev")) {
                directMsgUrl = "https://dev.oustme.com/#!/";
            }
        }
        if (userMessageList != null) {
            directMsgUrl = directMsgUrl + "/inboxwebview/" + tenantId + "/" + userMessageList.getUserMessageId() + "/" + activeUser.getStudentid();
            Log.d("TAG", "initListener:-- directMsgUrl-->  " + directMsgUrl);
        }
        loadWebViewDataFinal(directMessageWebView, directMsgUrl);
    }

    @SuppressLint({"SetJavaScriptEnabled", "SdCardPath"})
    private void loadWebViewDataFinal(WebView forgotPasswordWebView, String webAppUrl) {
        WebSettings webSettings = forgotPasswordWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        try {
            Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", Boolean.TYPE);
            m1.invoke(webSettings, Boolean.TRUE);

            Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", Boolean.TYPE);
            m2.invoke(webSettings, Boolean.TRUE);

            Method m3 = WebSettings.class.getMethod("setDatabasePath", String.class);
            m3.invoke(webSettings, "/data/data/" + getCallingPackage() + "/databases/");

            Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", Long.TYPE);
            m4.invoke(webSettings, 1024 * 1024 * 8);

            Method m5 = WebSettings.class.getMethod("setAppCachePath", String.class);
            m5.invoke(webSettings, "/data/data/" + getCallingPackage() + "/cache/");

            Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", Boolean.TYPE);
            m6.invoke(webSettings, Boolean.TRUE);

//            Log.e("WEB_VIEW_JS", "Enabled HTML5-Features");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e("WEB_VIEW_JS", "Reflection fail", e);
        }
        forgotPasswordWebView.loadUrl(webAppUrl);
    }


    private void initView() {
        directMessageWebView = findViewById(R.id.directMessage_webView);
        Toolbar inboxToolbar = findViewById(R.id.toolbar_inbox_detail_layout);
        backButton = findViewById(R.id.back_button);
        TextView screenName = findViewById(R.id.screen_name);

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        ImageView branding_bg = branding_mani_layout.findViewById(R.id.branding_bg);
        ImageView branding_icon = branding_mani_layout.findViewById(R.id.brand_loader);
        //End

        getColors();

        Intent callingIntent = getIntent();
        String str = callingIntent.getStringExtra("userMessageList");
        Gson gson = new Gson();
        userMessageList = gson.fromJson(str, UserMessageList.class);

        if (userMessageList == null) {
            finish();
        }

        inboxToolbar.setBackgroundColor(bgColor);
        screenName.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(backButton.getDrawable(), color);
        inboxToolbar.setTitle("");
        screenName.setText(userMessageList.getMessageTitle());
        setSupportActionBar(inboxToolbar);

        try {
            activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
            String tenantId = OustPreferences.get("tanentid");

            if (tenantId != null && !tenantId.isEmpty()) {
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                        ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

                if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                    Picasso.get().load(brandingBg).into(branding_bg);
                } else {
                    String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                    Picasso.get().load(tenantBgImage).into(branding_bg);
                }

                File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
                if (brandingLoader.exists()) {
                    Picasso.get().load(brandingLoader).into(branding_icon);
                } else {
                    String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                    Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class MyBrowser extends WebChromeClient {

        public MyBrowser() {
            showProgress();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                hideProgress();
                directMessageWebView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideProgress() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showProgress() {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getColors() {
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            color = OustResourceUtils.getColors();
            bgColor = OustResourceUtils.getToolBarBgColor();
        } else {
            bgColor = OustResourceUtils.getColors();
            color = OustResourceUtils.getToolBarBgColor();
        }
    }
}
