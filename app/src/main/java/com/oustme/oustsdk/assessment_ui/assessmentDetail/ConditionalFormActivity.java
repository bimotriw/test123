package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import static com.oustme.oustsdk.tools.htmlrender.HtmlTextView.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

public class ConditionalFormActivity extends AppCompatActivity {

    WebView formWebView;
    Toolbar toolbar;
    String assessmentId,gameId,UserId,formUrl,assessmentName;
    RelativeLayout layout_loader;
    ImageView back_button;
    TextView screen_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditional_form);
        getIntentData();
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Notification activity for toolbar
    void getIntentData(){
        try {
            if(getIntent().getExtras() !=null){
                formUrl = getIntent().getStringExtra("formUrl");
                assessmentId = getIntent().getStringExtra("assessmentId");
                assessmentName = getIntent().getStringExtra("assessmentName");
                gameId = getIntent().getStringExtra("gameId");
                UserId = getIntent().getStringExtra("userId");
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void init(){
        toolbar = findViewById(R.id.toolbar_notification_layout);
        back_button = findViewById(R.id.back_button);
        screen_name = findViewById(R.id.screen_name);
        formWebView = findViewById(R.id.form_webview);
        formWebView.getSettings().setJavaScriptEnabled(true);
        Log.d("ConditionalFlow","Name: "+ assessmentName);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar.setBackgroundColor(OustResourceUtils.getToolBarBgColor());
        screen_name.setTextColor(OustResourceUtils.getColors());
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), OustResourceUtils.getColors());
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        screen_name.setText(assessmentName.toUpperCase());
        back_button.setOnClickListener(v -> onBackPressed());

        layout_loader = findViewById(R.id.scorm_layout_loader);
        layout_loader.setVisibility(View.VISIBLE);

        formWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "SCORM onPageFinished: ");
                layout_loader.setVisibility(View.GONE);
                super.onPageFinished(formWebView, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "SCORM onPageStarted: ");
                layout_loader.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.d(TAG, "SCORM onReceivedHttpError: ");
                layout_loader.setVisibility(View.GONE);
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d(TAG, "SCORM onReceivedSslError: ");
                layout_loader.setVisibility(View.GONE);
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(TAG, "SCORM onLoadResource: ");
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "SCORM onReceivedError: " + description);
                layout_loader.setVisibility(View.GONE);
                Toast.makeText(OustSdkApplication.getContext(), "Oh no! " + description, Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "SCORM shouldOverrideUrlLoading: ");
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "SCORM shouldOverrideUrlLoading: ");
                if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Toast.makeText(OustSdkApplication.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "SCORM shouldOverrideUrlLoading Exception:" + e);
                    return true;
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                Log.d(TAG, "SCORM onPageCommitVisible: " + url);
                super.onPageCommitVisible(view, url);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.d(TAG, "SCORM shouldOverrideKeyEvent: ");
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "SCORM onReceivedError: ");
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                Log.d(TAG, "SCORM onUnhandledKeyEvent: ");
                super.onUnhandledKeyEvent(view, event);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                Log.d(TAG, "SCORM onScaleChanged: ");
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                Log.d(TAG, "SCORM onReceivedLoginRequest: ");
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        });

        String userAgent = formWebView.getSettings().getUserAgentString();
        userAgent += "Oust_" + userAgent;
        formWebView.getSettings().setUserAgentString(userAgent);
        formWebView.clearCache(true);
        formWebView.getSettings().setUseWideViewPort(true);
        formWebView.setInitialScale(1);
        formWebView.getSettings().setBuiltInZoomControls(true);
        formWebView.clearHistory();
        formWebView.getSettings().setAllowFileAccess(true);
        formWebView.getSettings().setDomStorageEnabled(true);
        formWebView.getSettings().setJavaScriptEnabled(true);
        formWebView.getSettings().setLoadWithOverviewMode(true);
        formWebView.getSettings().setUseWideViewPort(true);
        formWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        formWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        if(formUrl!=null && !formUrl.isEmpty()) {
            String finalUrl = formUrl+"?assessmentId="+assessmentId+"&gameId="+gameId+"&userId="+UserId+"&startTime="+System.currentTimeMillis();
            Log.d(TAG, "init: final url:"+finalUrl);
            formWebView.loadUrl(finalUrl);
        }
    }
}