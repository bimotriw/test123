package com.oustme.oustsdk.course_ui;

import static com.oustme.oustsdk.tools.htmlrender.HtmlTextView.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.utils.OustResourceUtils;

public class IntroScormCardActivity extends AppCompatActivity {

    private WebView scorm_web_view;
    private TextView error_info_text;
    private ProgressBar scorm_view_loader;
    private RelativeLayout layout_loader;
    private TextView loadingTxt;
    private Toolbar toolbar;
    private TextView screen_name;
    private ImageView back_button;

    int color;
    int bgColor;
    int height_toolbar;
    DTOCourseCard cardData;
    DTONewFeed dtoNewFeed;
    String courseId;
    boolean isScormEventBased;
    ActiveUser activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_scorm_card);
        initView();
    }

    private void initView() {
        try {
            scorm_web_view = findViewById(R.id.scorm_web_view);
            error_info_text = findViewById(R.id.error_info_text);
            scorm_view_loader = findViewById(R.id.scorm_view_loader);
            layout_loader = findViewById(R.id.scorm_layout_loader);
            loadingTxt = findViewById(R.id.loading_text_scorm_card);
            toolbar = findViewById(R.id.intro_scorm_toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            getColors();

            String courseDataString = OustStaticVariableHandling.getInstance().getCourseDataStr();
            Gson gson = new Gson();
            cardData = gson.fromJson(courseDataString, DTOCourseCard.class);
            Bundle feedBundle = getIntent().getExtras();
            if (feedBundle != null) {
                dtoNewFeed = feedBundle.getParcelable("Feed");
            }

            height_toolbar = toolbar.getLayoutParams().height;
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");

            setSupportActionBar(toolbar);

            back_button.setOnClickListener(view -> onBackPressed());

            loadingTxt.setText(getResources().getString(R.string.loading_module) + " " + getResources().getString(R.string.please_wait) + "..");
            int primaryColor = OustResourceUtils.getColors();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scorm_view_loader.setIndeterminateTintList(ColorStateList.valueOf(primaryColor));
            } else {
                scorm_view_loader.setBackgroundColor(primaryColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        handleData();
    }

    private void handleData() {
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

            if (OustSdkTools.checkInternetStatus()) {
                error_info_text.setVisibility(View.GONE);

                WebViewClient webViewClient = new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.d(TAG, "SCORM onPageFinished: ");
                        layout_loader.setVisibility(View.GONE);
                        super.onPageFinished(scorm_web_view, url);
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
                };

                String userAgent = scorm_web_view.getSettings().getUserAgentString();
                userAgent += "Oust_" + userAgent;
                scorm_web_view.getSettings().setUserAgentString(userAgent);
                scorm_web_view.setWebViewClient(webViewClient);
                scorm_web_view.clearCache(true);
                scorm_web_view.getSettings().setUseWideViewPort(true);
                scorm_web_view.setInitialScale(1);
                scorm_web_view.getSettings().setBuiltInZoomControls(true);
                scorm_web_view.clearHistory();
                scorm_web_view.getSettings().setAllowFileAccess(true);
                scorm_web_view.getSettings().setDomStorageEnabled(true);
                scorm_web_view.getSettings().setJavaScriptEnabled(true);
                scorm_web_view.getSettings().setLoadWithOverviewMode(true);
                scorm_web_view.getSettings().setUseWideViewPort(true);
                scorm_web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
                scorm_web_view.getSettings().setMediaPlaybackRequiresUserGesture(false);

                if (isScormEventBased) {
                    scorm_web_view.addJavascriptInterface(new WebAppInterface(), "Success");
                }

                if (cardData != null && cardData.getScormIndexFile() != null) {
                    String path1 = "";
                    if (cardData.getScormPlayerUrl() != null && !cardData.getScormPlayerUrl().isEmpty()) {

                        String scormPlayerUrl = cardData.getScormPlayerUrl();
                        Log.d(TAG, "handleData: " + scormPlayerUrl);
                        scormPlayerUrl = scormPlayerUrl.replace("{orgId}", OustPreferences.get("tanentid"));
                        scormPlayerUrl = scormPlayerUrl.replace("{userId}", activeUser.getStudentid());
                        scormPlayerUrl = scormPlayerUrl.replace("{courseId}", courseId);
                        scormPlayerUrl = scormPlayerUrl.replace("{cardId}", ("" + cardData.getCardId()));

                        if (scormPlayerUrl.contains("levelId")) {
                            scormPlayerUrl = scormPlayerUrl.replace("{levelId}", ("" + 0));
                        } else {
                            scormPlayerUrl = scormPlayerUrl + "&levelId=" + 0;
                        }

                        Log.d(TAG, "handleData: scormPlayerUrl : " + scormPlayerUrl);
                        path1 = scormPlayerUrl;
                    } else {
                        path1 = cardData.getScormIndexFile();
                    }
                    final String path = path1;
                    scorm_web_view.loadUrl(path);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    finish();
                }

                scorm_web_view.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        Log.d(TAG, "onProgressChanged: " + progress);
                        if (progress > 94) {
                            layout_loader.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                error_info_text.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public class WebAppInterface {
        WebAppInterface() {
        }

        @JavascriptInterface
        public void ScormComplete() {
            Log.d(TAG, "ScormComplete: Interface");
            Message message = webAppHandler.obtainMessage(1, "Complete");
            message.sendToTarget();
        }
    }

    private final Handler webAppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                isScormEventBased = false;
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };


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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (scorm_web_view != null) {
                scorm_web_view.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}