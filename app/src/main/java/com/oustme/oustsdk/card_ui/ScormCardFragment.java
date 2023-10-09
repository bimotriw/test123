package com.oustme.oustsdk.card_ui;

import static com.oustme.oustsdk.tools.htmlrender.HtmlTextView.TAG;

import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.Base64;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScormCardFragment extends Fragment {

    WebView scorm_web_view;
    TextView error_info_text;
    ProgressBar scorm_view_loader;
    RelativeLayout layout_loader;
    TextView loadingTxt;

    int color;
    int bgColor;
    String backgroundImage;
    boolean showNudgeMessage;
    CourseLevelClass courseLevelClass;
    CourseDataClass courseDataClass;
    DTOCourseCard cardData;
    DTOUserCardData userCardData;
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    String courseId;
    String courseName;
    boolean isFavouriteCard;
    boolean isScormEventBased;
    private boolean isReviewMode;
    List<FavCardDetails> favouriteCardList;
    final FavCardDetails favCardDetails = new FavCardDetails();
    ActiveUser activeUser;

    public ScormCardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scorm_card, container, false);
        scorm_web_view = view.findViewById(R.id.scorm_web_view);
        error_info_text = view.findViewById(R.id.error_info_text);
        scorm_view_loader = view.findViewById(R.id.scorm_view_loader);
        layout_loader = view.findViewById(R.id.scorm_layout_loader);
        loadingTxt = view.findViewById(R.id.loading_text_scorm_card);

        try {
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
        return view;
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        try {
            this.courseDataClass = courseDataClass;
            this.courseId = "" + courseDataClass.getCourseId();
            this.courseName = "" + courseDataClass.getCourseName();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCard(DTOCourseCard cardData) {
        try {
            this.cardData = cardData;
            if (this.cardData != null) {
                isScormEventBased = this.cardData.isIfScormEventBased();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLevel(CourseLevelClass courseLevelClass) {
        try {
            this.courseLevelClass = courseLevelClass;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserCardData(DTOUserCardData userCardData) {
        try {
            this.userCardData = userCardData;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFavouriteCard(boolean isFavouriteCard) {
        this.isFavouriteCard = isFavouriteCard;
    }

    public void setFavouriteCardList(List<FavCardDetails> favouriteCardList) {
        this.favouriteCardList = favouriteCardList;
        if (this.favouriteCardList == null) {
            this.favouriteCardList = new ArrayList<>();
        }
    }

    public void setCardBgImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setShowNudgeMessage(boolean showNudgeMessage) {
        this.showNudgeMessage = showNudgeMessage;
    }

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setActiveUser(ActiveUser activeUser) {
        if (activeUser == null || activeUser.getStudentid() == null) {
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            HttpManager.setBaseUrl();
        }
        this.activeUser = activeUser;
    }

    private void handleData() {

        try {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.handleScormCard(isScormEventBased);
            }

            if (learningModuleInterface != null) {
                learningModuleInterface.changeOrientationUnSpecific();
            }

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

                if (learningModuleInterface != null)
                    learningModuleInterface.isLearnCardComplete(false);
                if (isScormEventBased) {
                    scorm_web_view.addJavascriptInterface(new WebAppInterface(), "Success");
                }

                if (cardData != null && cardData.getScormIndexFile() != null) {
                    String path1 = "";
                    if (cardData.getScormPlayerUrl() != null && !cardData.getScormPlayerUrl().isEmpty()) {
                        //byte[] data = cardData.getScormIndexFile().getBytes("UTF-8");
                        //String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                        //String scormPlayerUrl = OustSdkApplication.getContext().getResources().getString(R.string.scorm_player_url);
                        //{scormPlayerUrl}?courseId={courseId}&amp;userId={userId}&amp;cardId={cardId}&amp;url={base64}&amp;orgId={orgid}

                        String scormPlayerUrl = cardData.getScormPlayerUrl();
                        Log.d(TAG, "handleData: " + scormPlayerUrl);
                        //scormPlayerUrl = scormPlayerUrl.replace("{scormPlayerUrl}", cardData.getScormPlayerUrl());
                        scormPlayerUrl = scormPlayerUrl.replace("{orgId}", OustPreferences.get("tanentid"));
                        scormPlayerUrl = scormPlayerUrl.replace("{userId}", activeUser.getStudentid());
                        scormPlayerUrl = scormPlayerUrl.replace("{courseId}", courseId);
                        scormPlayerUrl = scormPlayerUrl.replace("{cardId}", ("" + cardData.getCardId()));
                        //scormPlayerUrl = scormPlayerUrl.replace("{base64}", base64);

                        if (scormPlayerUrl.contains("levelId")) {
                            scormPlayerUrl = scormPlayerUrl.replace("{levelId}", ("" + courseLevelClass.getLevelId()));
                        } else {
                            scormPlayerUrl = scormPlayerUrl + "&levelId=" + courseLevelClass.getLevelId();
                        }

                        Log.d(TAG, "handleData: scormPlayerUrl : " + scormPlayerUrl);
                        path1 = scormPlayerUrl;
                    } else {
                        path1 = cardData.getScormIndexFile();
                    }
                    final String path = path1;
                    scorm_web_view.loadUrl(path);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
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
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.handleLandScape();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.handlePortrait(isScormEventBased);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
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
            //super.handleMessage(msg);
            try {
                isScormEventBased = false;
                if (learningModuleInterface != null) {
                    learningModuleInterface.isLearnCardComplete(true);
                    if (courseDataClass != null && courseDataClass.isAutoPlay())
                        learningModuleInterface.gotoNextScreen();

                    if (courseContentHandlingInterface != null)
                        courseContentHandlingInterface.handlePortrait(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (scorm_web_view != null) {
                scorm_web_view.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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