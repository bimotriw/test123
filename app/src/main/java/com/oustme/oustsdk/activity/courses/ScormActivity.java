package com.oustme.oustsdk.activity.courses;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.oustme.oustsdk.R;


/**
 * Created by admin on 16/04/18.
 */

public class ScormActivity extends Activity {
    private WebView scrom_webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scrom);
        scrom_webview= findViewById(R.id.scrom_webview);
        openWebView();
        ImageView closeWbImage= findViewById(R.id.closeWbImage);
        closeWbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScormActivity.this.finish();
                System.exit(0);
            }
        });
    }

    private void openWebView(){
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(scrom_webview, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                super.onUnhandledKeyEvent(view, event);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        };
        String userAgent = scrom_webview.getSettings().getUserAgentString();
        userAgent+="vinayak_"+userAgent;
        scrom_webview.getSettings().setUserAgentString(userAgent);
        scrom_webview.setWebViewClient(webViewClient);
        scrom_webview.clearCache(true);
        scrom_webview.getSettings().setUseWideViewPort(true);
        scrom_webview.setInitialScale(1);
        scrom_webview.getSettings().setBuiltInZoomControls(true);
        scrom_webview.clearHistory();
        scrom_webview.getSettings().setAllowFileAccess(true);
        scrom_webview.getSettings().setDomStorageEnabled(true);
        scrom_webview.getSettings().setJavaScriptEnabled(true);
        scrom_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        scrom_webview.getSettings().setLoadWithOverviewMode(true);
        scrom_webview.getSettings().setUseWideViewPort(true);
        scrom_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        scrom_webview.loadUrl("file:///android_asset/scormdemo/ess-sci-sample-assets/introduction.html");
        ///Users/admin/OustSDK/app/src/main/assets/scormdemo/ess-sci-sample-assets
        scrom_webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
                Log.i("------","extra = "+hr.getExtra()+"    Type"+hr.getType());
                return false;
            }
        });
    }
    private class VideoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }

    private void getUserResponse(){
        String userAgent = scrom_webview.getSettings().getUserAgentString();
        userAgent+="vinayak_"+userAgent;
        scrom_webview.getSettings().setUserAgentString(userAgent);
    }


}
