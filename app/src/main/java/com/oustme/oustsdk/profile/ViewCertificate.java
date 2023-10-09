package com.oustme.oustsdk.profile;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
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
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;


public class ViewCertificate extends AppCompatActivity {

    private WebView webView;
    private String courseName;
    private String url;
    private Toolbar toolbar;
    private TextView screen_name;
    private ImageView back_button;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    private int color;
    private int bgColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_certificate);

        initView();
        initListener();
    }

    private void initListener() {
//        webView.setWebChromeClient(new MyBrowser());

        loadWebViewCertificatesData(webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebViewCertificatesData(WebView webView) {
        webView.clearCache(true);
        webView.setInitialScale(1);
        webView.clearHistory();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        try {

            String pdfUrl = "https://docs.google.com/gview?embedded=true&url=";
            if (url != null && !url.isEmpty()) {

                webView.setWebViewClient(new RedirectScreenClient());
                if (!url.contains("http")) {
                    url = "http://" + url;
                }

                if (url.contains(".pdf") || url.contains(".ppt") || url.contains(".doc") || url.contains(".x")) {
                    url = pdfUrl + url;
                }
                webView.loadUrl(url);


            } else {
                Toast.makeText(ViewCertificate.this, "No Content", Toast.LENGTH_SHORT).show();
            }
//            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
//            webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initView() {
        webView = findViewById(R.id.webView_kk);

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        branding_bg = findViewById(R.id.branding_bg);
        branding_icon = findViewById(R.id.brand_loader);
        branding_percentage = findViewById(R.id.percentage_text);
        //End

        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("certificateUrl");
            courseName = intent.getStringExtra("courseName");
        }

        getColors();
        toolbar = findViewById(R.id.toolbar_lay_view_certificate);
        screen_name = findViewById(R.id.screen_name);
        back_button = findViewById(R.id.back_button);
        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(color);
        screen_name.setText(courseName);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
        toolbar.setTitle("");

        try {
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

        setSupportActionBar(toolbar);

        back_button.setOnClickListener(v -> onBackPressed());

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


    private class MyBrowser extends WebChromeClient {

        public MyBrowser() {
            try {
                branding_mani_layout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                try {
                    branding_mani_layout.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }
    }

    private class RedirectScreenClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("intent://")) {
                try {
                    Intent urlValue = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (urlValue != null) {
                        String urlFallBack = urlValue.getStringExtra("browser_fallback_url");
                        if (urlFallBack != null) {
                            view.loadUrl(urlFallBack);
                        }
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (view.getTitle() != null) {
                if (view.getTitle().equals("")) {
                    view.reload();
                }
            }
            branding_mani_layout.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            branding_mani_layout.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }
    }

}