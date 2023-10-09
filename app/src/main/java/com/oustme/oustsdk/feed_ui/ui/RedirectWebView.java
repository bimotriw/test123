package com.oustme.oustsdk.feed_ui.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class RedirectWebView extends AppCompatActivity {

    private TextView feed_title_link;
    private ImageView toolbar_close;
    private WebView content_view;
    private CircularProgressIndicator progressBar;

    private String url;
    private String feed_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_web_view);

        feed_title_link = findViewById(R.id.feed_title_link);
        toolbar_close = findViewById(R.id.toolbar_close);
        content_view = findViewById(R.id.content_view);
        progressBar = findViewById(R.id.user_completed_progress);

        Log.d("RedirectWebView", "onCreate: ");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
            feed_title = bundle.getString("feed_title");
        }

        if (feed_title != null && !feed_title.isEmpty()) {
            String feed_link = "" + feed_title;
            Spanned feedContent = Html.fromHtml(feed_link.trim());
            feed_title_link.setText(feedContent);

        }
        toolbar_close.setOnClickListener(v -> RedirectWebView.this.finish());
        content_view.clearCache(true);
        content_view.setInitialScale(1);
        content_view.clearHistory();

        content_view.clearFormData();
        content_view.clearSslPreferences();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }
        WebStorage.getInstance().deleteAllData();

        WebSettings webSettings = content_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAppCacheEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        String pdfUrl = "https://docs.google.com/gview?embedded=true&url=";
//        String pdfUrl = "https://docs.google.com/viewer?url=";
        if (url != null && !url.isEmpty()) {
            Log.d("RedirectWebView", "onCreate: url:"+url);
            content_view.setWebViewClient(new RedirectScreenClient());
            if (!url.contains("http") || !url.contains("https")) {
                url = "http://" + url;
            }
            if (url.contains(".pdf") || url.contains(".ppt") || url.contains(".doc") || url.contains(".x")) {
                try {
                    url = URLEncoder.encode(url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                url = pdfUrl + url;
            }
            Log.d("RedirectWebView", "onCreate: url2: "+url);

            content_view.loadUrl(url);

        } else {
            Toast.makeText(RedirectWebView.this, "No Content", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && content_view.canGoBack()) {
            content_view.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
            if (url.contains(".pdf") || url.contains(".ppt") || url.contains(".doc") || url.contains(".x")) {
                if (view.getTitle() != null) {
                    if (view.getTitle().equals("")) {
                        view.reload();
                    }
                }
            }
            progressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (content_view != null) {
                content_view.clearFormData();
                content_view.destroy();
                content_view = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }
}
