package com.oustme.oustsdk.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

public class OustWebViewActivity extends AppCompatActivity {


    public static final String WEBURL = "weburl";
    private WebView webView;
    private ProgressBar mProgressBar;
    private ImageView backButton;
    private TextView screenName;
    private int bgColor;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oust_web_view);
        initView();
        initData();

    }

    private void initView() {

        Toolbar notificationToolbar = findViewById(R.id.toolbar_web_view_layout);
        mProgressBar = findViewById(R.id.user_completed_progress);
        webView = findViewById(R.id.wv_oust);
        backButton = findViewById(R.id.back_button);
        screenName = findViewById(R.id.screen_name);

        getColors();
        notificationToolbar.setBackgroundColor(bgColor);
        screenName.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(backButton.getDrawable(), color);
        notificationToolbar.setTitle("");
        screenName.setText(getResources().getString(R.string.team_analytics));
        setSupportActionBar(notificationToolbar);

        webView.clearCache(true);
        webView.setInitialScale(1);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.clearHistory();
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        webView.requestFocus();

        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                String webUrl = intent.getStringExtra(WEBURL);
                if (!TextUtils.isEmpty(webUrl)) {
                    webView.loadUrl(webUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
