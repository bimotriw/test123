package com.oustme.oustsdk.feed_ui.ui;


import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.tools.OustSdkTools.stringToURL;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.GameLetActivity;
import com.oustme.oustsdk.activity.common.CplBaseActivity;
import com.oustme.oustsdk.activity.common.FeedCardActivity;
import com.oustme.oustsdk.activity.common.ZoomBaseActivity;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FeedType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.FeedClickListener;
import com.oustme.oustsdk.layoutFour.navigationFragments.HomeNavFragment;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustDataHandler;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class GeneralFeedDetailScreen extends AppCompatActivity {

    RelativeLayout feed_detail_lay;
    TextView feed_title;
    FrameLayout toolbar_close;
    CardView feed_image_lay;
    GifImageView feed_gif_view;
    ImageView feed_image_view;
    TextView feed_title_full;
    TextView feed_dead_line;
    TextView feed_action_name;
    TextView feed_date;
    RelativeLayout feed_like_lay;
    ImageView feed_like_image;
    TextView feed_like;
    RelativeLayout feed_comment_lay;
    ImageView feed_comment_image;
    TextView feed_comment;
    RelativeLayout feed_share_lay;
    ImageView feed_share_image;
    TextView feed_share;
    TextView feed_description;
    TextView feed_link;
    View feed_link_div;
    FrameLayout feed_action_button;
    FrameLayout feed_read_more;
    LinearLayout root_layout;
    NestedScrollView scroll_lay;
    LinearLayout feed_action_lay;
    RelativeLayout file_attach_lay;
    ImageView file_attach_image;
    TextView file_attach_text;
    WebView feed_description_webView;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    DTOUserFeedDetails.FeedDetails feed;
    String feedType;
    long timeStamp;
    boolean updateComment;
    private HashMap<String, String> landingPageModuleMap;
    long assessmentId;
    long courseId;
    boolean isFeedComment, isFeedAttach;
    String shareContent = " Download Oustsdk...";
    ActiveUser activeUser;
    DTOCourseCard courseCardClass;
    private FeedClickListener feedClickListener;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private String courseCardClassString;
    private boolean isMultipleCpl = false;
    private boolean isFeedChange, isLikeClicked;
    private long mLastTimeClicked = 0;


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        OustSdkTools.setLocale(GeneralFeedDetailScreen.this);
        setContentView(R.layout.activity_general_feed_detail_screen);

        feed_detail_lay = findViewById(R.id.feed_detail_lay);
        feed_title = findViewById(R.id.feed_title);
        toolbar_close = findViewById(R.id.toolbar_close);
        feed_image_lay = findViewById(R.id.feed_image_lay);
        feed_gif_view = findViewById(R.id.feed_image);
        feed_image_view = findViewById(R.id.feed_image_view);
        feed_title_full = findViewById(R.id.feed_title_full);
        feed_dead_line = findViewById(R.id.feed_dead_line);
        feed_action_name = findViewById(R.id.feed_action_name);
        feed_date = findViewById(R.id.feed_date);
        feed_like_lay = findViewById(R.id.feed_like_lay);
        feed_like_image = findViewById(R.id.feed_like_image);
        feed_like = findViewById(R.id.feed_like);
        feed_comment_lay = findViewById(R.id.feed_comment_lay);
        feed_comment_image = findViewById(R.id.feed_comment_image);
        feed_comment = findViewById(R.id.feed_comment);
        feed_share_lay = findViewById(R.id.feed_share_lay);
        feed_share_image = findViewById(R.id.feed_share_image);
        feed_share = findViewById(R.id.feed_share);
        feed_description = findViewById(R.id.feed_description);
        feed_link = findViewById(R.id.feed_link);
        feed_link_div = findViewById(R.id.feed_link_div);
        feed_action_button = findViewById(R.id.feed_action_button);
        feed_read_more = findViewById(R.id.feed_read_more);
        root_layout = findViewById(R.id.root_layout);
        scroll_lay = findViewById(R.id.scroll_lay);
        feed_action_lay = findViewById(R.id.feed_action_lay);
        file_attach_lay = findViewById(R.id.file_attach_lay);
        file_attach_image = findViewById(R.id.file_attach_image);
        file_attach_text = findViewById(R.id.file_attach_text);
        feed_description_webView = findViewById(R.id.feed_description_webView);

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        branding_bg = findViewById(R.id.branding_bg);
        branding_icon = findViewById(R.id.brand_loader);
        branding_percentage = findViewById(R.id.percentage_text);
        //End

        try {
            feedClickListener = new HomeNavFragment();
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

            isMultipleCpl = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        supportPostponeEnterTransition();

        Bundle feedBundle = getIntent().getExtras();
        if (feedBundle != null) {
            feed = feedBundle.getParcelable("Feed");
            courseCardClassString = feedBundle.getString("CardData");
            isFeedComment = feedBundle.getBoolean("FeedComment");
            isFeedAttach = feedBundle.getBoolean("FeedAttach");
//            isFeedLikeable = feedBundle.getBoolean("isFeedLikeable");
            feedType = feedBundle.getString("feedType");
            timeStamp = feedBundle.getLong("timeStamp", 0);
            activeUser = (ActiveUser) feedBundle.getSerializable("ActiveUser");
            assessmentId = feedBundle.getLong("AssessmentId");
            courseId = feedBundle.getLong("CourseId");
            landingPageModuleMap = (HashMap<String, String>) feedBundle.getSerializable("deskDataMap");
        }

        if (activeUser == null) {
            activeUser = OustAppState.getInstance().getActiveUser();
        }

        OustPreferences.saveAppInstallVariable("feedNot", true);

        if (courseCardClassString != null && !courseCardClassString.isEmpty()) {
            courseCardClass = new Gson().fromJson(courseCardClassString, DTOCourseCard.class);
        }

        if (feedType != null && !feedType.isEmpty() && feedType.equalsIgnoreCase(FeedType.GENERAL.toString())) {
            feed_action_button.setVisibility(View.GONE);
            feedRewardUpdate(feed);
//                updateFeedViewed(feed);
        } else {
            feed_action_button.setVisibility(View.VISIBLE);
            feed_read_more.setVisibility(View.GONE);
        }
        try {
            setIconColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        toolbar_close.setOnClickListener(v -> backScreen());
        feed_title.setOnClickListener(v -> backScreen());

        if (feed != null) {
            feed_link.setVisibility(View.GONE);
            feed_link_div.setVisibility(View.GONE);
//            feed.setLikeble(isFeedLikeable);
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE) ||
                    OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE) ||
                    OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE)) {
                feed_action_lay.setVisibility(View.VISIBLE);
                boolean hideLike = false;
                boolean hideComment = false;
                boolean hideShare = false;
                if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_LIKE_DISABLE)) {
                    feed_like_lay.setVisibility(View.GONE);
                    hideLike = true;
                }

                if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_COMMENT_DISABLE)) {
                    feed_comment_lay.setVisibility(View.GONE);
                    hideComment = true;
                }

                if (!OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_SHARE_DISABLE)) {
                    feed_share_lay.setVisibility(View.GONE);
                    hideShare = true;
                }

                if (hideLike && !hideShare) {
                    feed_comment_lay.setGravity(Gravity.START);
                }

                if (!hideLike && hideShare) {
                    feed_comment_lay.setGravity(Gravity.END);
                }

                if (hideComment && hideShare) {
                    feed_like_lay.setGravity(Gravity.START);
                }

                if (hideLike && hideComment) {
                    feed_share_lay.setGravity(Gravity.START);
                }

                if (hideLike && hideShare) {
                    feed_comment_lay.setGravity(Gravity.START);
                }


            } else {
                feed_action_lay.setVisibility(View.GONE);
            }

            if (feed.getFileName() != null && !feed.getFileName().isEmpty()) {
                String fileName = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "feed/" + feed.getFileName();
                file_attach_lay.setVisibility(View.VISIBLE);
                file_attach_text.setText(feed.getFileName());

                if (isFeedAttach) {
                    if (fileName.contains(".pdf")) {
                        if (OustSdkTools.checkInternetStatus()) {
                            branding_mani_layout.setVisibility(View.VISIBLE);
                            downloadManager(fileName, "feed_" + feed.getFileName());
                        } else {
                            openAttachment(fileName, feed.getHeader());
                        }
                    } else {
                        openAttachment(fileName, feed.getHeader());
                    }
                }

                file_attach_lay.setOnClickListener(v -> {
                    if (fileName.contains(".pdf")) {
                        if (OustSdkTools.checkInternetStatus()) {
                            branding_mani_layout.setVisibility(View.VISIBLE);
                            downloadManager(fileName, "feed_" + feed.getFileName());
                        } else {
                            openAttachment(fileName, feed.getHeader());
                        }
                    } else {
                        openAttachment(fileName, feed.getHeader());
                    }
                });
            } else {
                file_attach_lay.setVisibility(View.GONE);
            }

            setFeedLike(feed, false);

            if (feed.getButton() != null && feed.getButton().getBtnText() != null && !feed.getButton().getBtnText().isEmpty()) {
                feed_action_name.setText(OustSdkTools.returnValidString(feed.getButton().getBtnText()));
            } else if (feed.getButton() != null && feed.getDeepLinkInfo().getButtonLabel() != null && !feed.getDeepLinkInfo().getButtonLabel().isEmpty()) {
                feed_action_name.setText(OustSdkTools.returnValidString(feed.getDeepLinkInfo().getButtonLabel()));
            }

            if (feed != null) {
                if (courseCardClass != null && courseCardClass.getCardTitle() != null && !courseCardClass.getCardTitle().isEmpty()) {
                    Spanned feedHeader = Html.fromHtml(courseCardClass.getCardTitle().trim());
                    feed_title_full.setText(feedHeader);
                    feed_title_full.setVisibility(View.VISIBLE);
                } else {
                    if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
                        Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
                        feed_title_full.setText(feedHeader);
                        feed_title_full.setVisibility(View.VISIBLE);
                    }
                }
            }

            feed_action_button.setOnClickListener(v -> {
                gotoUsersFeedsPage(feed.getFeedId(), activeUser.getStudentKey());
                feedClicked(feed.getFeedId(), feed.getCplId());
                if (courseCardClass != null) {
                    OustDataHandler.getInstance().setCourseCardClass(courseCardClass);
                    feed.setCourseCardClass(courseCardClass);
                    Intent intent = new Intent(OustSdkApplication.getContext(), FeedCardActivity.class);
                    intent.putExtra("type", "card");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Feed", feed);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    if ((feedType != null && !feedType.isEmpty())) {
                        switch (feedType) {
                            case "APP_UPGRADE":
                                rateApp();
                                break;
                            case "GAMELET_WORDJUMBLE":
                            case "GAMELET_WORDJUMBLE_V2":
                            case "GAMELET_WORDJUMBLE_V3":
                                gotoGamelet("" + feed.getDeepLinkInfo().getAssessmentId(), feedType);
                                break;
                            case "JOIN_MEETING":
                                joinMeeting("" + feed.getDeepLinkInfo().getId());
                                break;
                            case "CONTENT_PLAY_LIST":
                                checkingCplExistOrNot(feed.getDeepLinkInfo().getCplId());
                                break;
                            case "SURVEY":
                                gotoSurvey("" + feed.getDeepLinkInfo().getAssessmentId(), feed.getHeader());
//                                OustAppState.getInstance().setCurrentSurveyFeed(feed);
                                break;
                            case "COURSE_UPDATE":
                                gotoCourse();
                                break;
                            case "ASSESSMENT_PLAY":
                                gotoAssessment(assessmentId);
                                break;
                            case "GENERAL":
                                break;
                        }
                    }
                }
            });

            if ((feedType != null && !feedType.isEmpty())) {
                if (feedType.equals("GENERAL")) {
                    if (feed.getButton() != null) {
                        String mUrl = feed.getButton().getBtnActionURI();
                        if ((mUrl != null) && (!mUrl.isEmpty())) {
                            feed_read_more.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            feed_read_more.setOnClickListener(v -> {
                if (feed.getButton() != null) {
                    String mUrl = feed.getButton().getBtnActionURI();
                    if ((mUrl != null) && (!mUrl.isEmpty())) {
                        openAttachment(mUrl, feed.getHeader());
                    }
                }
            });

            try {
                if (courseCardClass != null && courseCardClass.getContent() != null && !courseCardClass.getContent().isEmpty()) {
                    if (courseCardClass.getContent().contains("<li>") || courseCardClass.getContent().contains("</li>") ||
                            courseCardClass.getContent().contains("<ol>") || courseCardClass.getContent().contains("</ol>") ||
                            courseCardClass.getContent().contains("<p>") || courseCardClass.getContent().contains("</p>")) {
                        feed_description_webView.setVisibility(View.VISIBLE);
                        feed_description.setVisibility(View.GONE);
                        feed_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = com.oustme.oustsdk.tools.OustSdkTools.getDescriptionHtmlFormat(courseCardClass.getContent());
                        final WebSettings webSettings = feed_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(16);
                        feed_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        feed_description.setVisibility(VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            feed_description.setText(Html.fromHtml(courseCardClass.getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            feed_description.setText(Html.fromHtml(courseCardClass.getContent()));
                        }
                    }
                } else if (feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
                    if (feed.getContent().contains("<li>") || feed.getContent().contains("</li>") ||
                            feed.getContent().contains("<ol>") || feed.getContent().contains("</ol>") ||
                            feed.getContent().contains("<p>") || feed.getContent().contains("</p>")) {
                        feed_description_webView.setVisibility(View.VISIBLE);
                        feed_description.setVisibility(View.GONE);
                        feed_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = com.oustme.oustsdk.tools.OustSdkTools.getDescriptionHtmlFormat(feed.getContent());
                        final WebSettings webSettings = feed_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(16);
                        feed_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        feed_description.setVisibility(VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            feed_description.setText(Html.fromHtml(feed.getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            feed_description.setText(Html.fromHtml(feed.getContent()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }


            String feedDate = OustSdkTools.milliToDate("" + timeStamp);
            if (!feedDate.isEmpty()) {
                feed_date.setVisibility(View.VISIBLE);
                feed_date.setText(feedDate);
            }
            if (feed.getFeedExpiry() != null && !feed.getFeedExpiry().isEmpty() && !feed.getFeedExpiry().equalsIgnoreCase("0")) {
                String feedDeadLine = OustSdkTools.newMilliToDate("" + feed.getFeedExpiry());
                if (!feedDeadLine.isEmpty() && feed.getFeedExpiry() != null) {
                    feed_dead_line.setVisibility(View.VISIBLE);
                    feedDeadLine = getResources().getString(R.string.deadline) + " " + feedDeadLine.toUpperCase();
                    feed_dead_line.setText(feedDeadLine);
                }
            }
            if (feed.getNumComments() != 0) {
                feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
            } else {
                feed_comment.setText("0");
            }
            if (feed.isCommented()) {
                Drawable feedCommented = getResources().getDrawable(R.drawable.ic_comment_selected);
                feed_comment_image.setImageDrawable(drawableColor(feedCommented));
            }
            if (isFeedComment) {
                feedComment(feed);
            }

            if (feed.getNumShares() != 0) {
                feed_share.setText(String.valueOf(feed.getNumShares()));
            }

            String imageUrl = "";
            if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                //supportStartPostponedEnterTransition();
                imageUrl = feed.getImageUrl();
                try {
                    if (feed.getImageUrl().contains(".gif")) {
                        feed_gif_view.setVisibility(View.VISIBLE);
                        feed_image_view.setVisibility(View.GONE);
                        Glide.with(GeneralFeedDetailScreen.this)
                                .load(feed.getImageUrl())
                                .into(feed_gif_view);
                    } else {
                        feed_gif_view.setVisibility(View.GONE);
                        feed_image_view.setVisibility(View.VISIBLE);
                        Glide.with(GeneralFeedDetailScreen.this)
                                .load(feed.getImageUrl())
                                .into(feed_image_view);
                       /* Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    feed_gif_view.setImageBitmap(bitmap);
                                    PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(feed_gif_view);
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };

                        Picasso.get().load(imageUrl).into(target);*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } else {
                feed_image_lay.setVisibility(View.GONE);
            }

            feed_comment_lay.setOnClickListener(v -> feedComment(feed));
            feed_like_lay.setOnClickListener(v -> feedLike(feed));
            final String finalImageUrl = imageUrl;
            feed_share_lay.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                    return;
                }
                mLastTimeClicked = SystemClock.elapsedRealtime();
                feed_share_lay.setClickable(false);
                isFeedChange = true;
                shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                setUserShareCount(feed.getFeedId(), feed);
                if (!finalImageUrl.isEmpty()) {
                    ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                        HttpURLConnection connection = null;

                        try {
                            connection = (HttpURLConnection) stringToURL("" + finalImageUrl).openConnection();

                            connection.connect();

                            InputStream inputStream = connection.getInputStream();


                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                            Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);
                            Uri bmpUri;
                            if (bm != null) {
                                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
                                bmpUri = Uri.parse(path);

                                if (bmpUri != null) {
                                    //posting to UI thread
                                    ThreadPoolProvider.getInstance().getHandler().post(() -> {
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                                        shareIntent.setType("image/png");
                                        startActivity(Intent.createChooser(shareIntent, "Share with"));
                                        feed_share_lay.setClickable(true);
                                    });

                                } else {
                                    runOnUiThread(() -> {
                                        feed_share_lay.setClickable(true);
                                        Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    feed_share_lay.setClickable(true);
                                    Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                });
                            }

                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                feed_share_lay.setClickable(true);
                                Toast.makeText(this, "Unable to send,Check Permission", Toast.LENGTH_SHORT).show();
                            });
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        } finally {
                            assert connection != null;
                            connection.disconnect();
                        }


                    });
                } else {
                    Intent shareIntent;
                    isFeedChange = true;
                    setUserShareCount(feed.getFeedId(), feed);
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "Share with"));
                    feed_share_lay.setClickable(true);

                }
            });
        }
    }

    private void gotoUsersFeedsPage(long feedId, String studentKey) {
        try {
            String message = "/userFeed/" + studentKey + "/feed" + feedId;
            Log.e("TAG", "gotoUsersFeedsPage: " + message);
            Query query = OustFirebaseTools.getRootRef().child(message);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Object> userFeedNode = (HashMap<String, Object>) snapshot.getValue();
                    if (userFeedNode != null) {
                        boolean feedCoinsAdded = false;
                        if (userFeedNode.get("feedCoinsAdded") != null) {
                            feedCoinsAdded = (boolean) userFeedNode.get("feedCoinsAdded");
                        }
                        feed.setFeedCoinsAdded(feedCoinsAdded);
                        feedRewardUpdate(feed);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessment(long assessmentId) {
        try {
            if (feed.getDistributedId() > 0) {
                Gson gson = new GsonBuilder().create();
                Intent intent;
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                } else {
                    intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                }
                intent.putExtra("assessmentId", feed.getDistributedId());
                intent.putExtra("ActiveGame", gson.toJson(setGame(activeUser)));
                intent.putExtra("ActiveUser", gson.toJson(activeUser));
                startActivity(intent);
            } else {
                distibuteAndLaunchAssessmentFeed(feed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkingCplExistOrNot(long cpl_id) {
        try {
            String message;
            Log.e("TAG", "checkCPLDistributionOrNot: cpl id-> " + cpl_id + "  isMultipleCpl--> " + isMultipleCpl);
            if (!isMultipleCpl) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + cpl_id;
            } else {
                message = "/landingPage/" + activeUser.getStudentKey() + "/multipleCPL/" + cpl_id;
            }
            Log.e("TAG", "checkCPLDistributionOrNot: " + message);
            ValueEventListener avatarListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (null == snapshot.getValue()) {
                            distributeCPL(cpl_id);
                        } else {
                            Intent intent = new Intent(GeneralFeedDetailScreen.this, CplBaseActivity.class);
                            intent.putExtra("cplId", String.valueOf(cpl_id));
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(avatarListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoCourse() {
        try {
            Intent feedIntent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
            Bundle feedBundle = new Bundle();
            feedIntent.putExtra("catalog_id", "" + this.feed.getDeepLinkInfo().getCourseId());
            feedIntent.putExtra("catalog_type", "COURSE");
            feedBundle.putParcelable("Feed", this.feed);
            feedBundle.putBoolean("FeedComment", false);
            feedBundle.putBoolean("isFeedLikeable", this.feed.isLiked());
            feedBundle.putSerializable("ActiveUser", activeUser);
            feedBundle.putSerializable("deskDataMap", landingPageModuleMap);

            if (feed.getDistributedId() > 0) {
                feedIntent.putExtra("CourseId", this.feed.getDistributedId());
                feedIntent.putExtras(feedBundle);
                startActivityForResult(feedIntent, 1444);
            } else {
                distibuteAndLaunchCourseFeed(feed, feedBundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void distibuteAndLaunchAssessmentFeed(DTOUserFeedDetails.FeedDetails feed) {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());

            String distribution_url = getApplicationContext().getResources().getString(R.string.distribut_assessment_url);
            distribution_url = distribution_url.replace("{assessmentId}", "" + feed.getDeepLinkInfo().getAssessmentId());
            distribution_url = HttpManager.getAbsoluteUrl(distribution_url);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("users", jsonArray);
            jsonParams.put("reusabilityAllowed", true);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, distribution_url,
                    OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("TAG", "onResponse: " + response.toString());
                                if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                                    String distributedId = "" + response.optLong("distributedId");
                                    Gson gson = new GsonBuilder().create();
                                    Intent intent;
                                    if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                                        intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
                                    } else {
                                        intent = new Intent(OustSdkApplication.getContext(), AssessmentPlayActivity.class);
                                    }
                                    intent.putExtra("assessmentId", distributedId);
                                    intent.putExtra("ActiveGame", gson.toJson(setGame(activeUser)));
                                    intent.putExtra("ActiveUser", gson.toJson(activeUser));
                                    OustSdkApplication.getContext().startActivity(intent);
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void distibuteAndLaunchCourseFeed(DTOUserFeedDetails.FeedDetails feed, Bundle feedBundle) {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());

            String distribution_url = OustSdkApplication.getContext().getResources().getString(R.string.distribut_course_url);
            distribution_url = distribution_url.replace("{courseId}", "" + feed.getDeepLinkInfo().getCourseId());
            distribution_url = HttpManager.getAbsoluteUrl(distribution_url);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("users", jsonArray);
            jsonParams.put("reusabilityAllowed", true);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, distribution_url,
                    OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("TAG", "onResponse: " + response.toString());
                                if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                                    long distributedId = response.optLong("distributedId");
                                    feedBundle.putLong("CourseId", distributedId);
                                    Intent feedIntent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
                                    feedIntent.putExtras(feedBundle);
                                    ((Activity) OustSdkApplication.getContext()).startActivityForResult(feedIntent, 1444);
                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedRewardUpdate(DTOUserFeedDetails.FeedDetails newFeed) {
        try {
            if (newFeed == null) {
                Log.d("TAG", "feedUpdated: reward newfeed null");
                return;
            }

            if (newFeed.isFeedCoinsAdded()) {
                Log.d("TAG", "feedUpdated: reward coins already added");
                return;
            }

            if (newFeed.getFeedCoins() < 1) {
                Log.d("TAG", "feedUpdated: reward feedcoins is less than zero");
                return;
            }
            Log.d("TAG", "feedRewardUpdate: coins:" + newFeed.getFeedCoins());

            if (newFeed.getFeedId() > 0 && newFeed.getFeedCoins() > 0 && !newFeed.isFeedCoinsAdded()) {
                newFeed.setFeedCoinsAdded(true);
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setIconColors() {
        //comment_image_icon
        Drawable feedCommentDrawable = getResources().getDrawable(R.drawable.ic_comment_unselected);

        feed_comment_image.setImageDrawable(drawableColor(feedCommentDrawable));

        //like_image
        Drawable feedLikeDrawable = getResources().getDrawable(R.drawable.ic_like_common);

        feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));

        //feed_share_image
        Drawable feedShareDrawable = getResources().getDrawable(R.drawable.ic_share_common);

        feed_share_image.setImageDrawable(drawableColor(feedShareDrawable));

        //feed_attach_image
        Drawable feedAttachDrawable = getResources().getDrawable(R.drawable.ic_attach_fill);

        file_attach_image.setImageDrawable(drawableColor(feedAttachDrawable));

        //course_action_page
        Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);

        feed_action_button.setBackground(drawableColor(courseActionDrawable));

        //course_action_page
        Drawable readActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);

        feed_read_more.setBackground(drawableColor(readActionDrawable));

    }


    private void setFeedLike(DTOUserFeedDetails.FeedDetails feed, boolean isAnimation) {
        try {
            if (feed.getNumLikes() != 0) {
                feed_like.setText(OustSdkTools.formatMilliinFormat(feed.getNumLikes()));
            } else {
                feed_like.setText("0");
            }
            Drawable feedLikeDrawable = getResources().getDrawable(R.drawable.ic_like_common);
            if (feed.isLiked()) {
                feedLikeDrawable = getResources().getDrawable(R.drawable.ic_liked_common);
            }

            feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));
            likeButtonAnimation(isAnimation, feedLikeDrawable);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedLike(DTOUserFeedDetails.FeedDetails feed) {
        try {
            //        updateFeedViewed(feed);
//        if (feed.isLikeble()) {
            isFeedChange = true;
            if (!feed.isLiked()) {
                feed.setNumLikes(feed.getNumLikes() + 1);
                feed.setLiked(true);
                setUserLike(feed, true);
                isLikeClicked = true;
            } else {
                if (feed.getNumLikes() > 0) {
                    feed.setLiked(false);
                    feed.setNumLikes(feed.getNumLikes() - 1);
                    setUserLike(feed, false);
                    isLikeClicked = false;
                }
            }
//        }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUserLike(DTOUserFeedDetails.FeedDetails feed, final boolean value) {
        try {
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feed.getFeedId() + "/" + "isLiked";
            if (value) {
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
            } else {
                OustFirebaseTools.getRootRef().child(message1).setValue(false);
            }
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

            String message = "feeds/feed" + feed.getFeedId() + "/numLikes";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
            firebase.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                    } else {
                        if (value) {
                            currentData.setValue((Long) currentData.getValue() + 1);
                        } else {
                            if ((Long) currentData.getValue() > 0) {
                                currentData.setValue((Long) currentData.getValue() - 1);
                            }
                        }
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (DatabaseError != null) {
                        Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.e("", "Firebase counter increment succeeded.");

                        setFeedLike(feed, feed.isLiked());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void likeButtonAnimation(boolean animated, Drawable feedLikeDrawable) {
        if (animated) {
            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(feed_like_image, "rotation", 0f, 360f);
            rotationAnim.setDuration(1000);
            rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(feed_like_image, "scaleX", 0.5f, 1f);
            bounceAnimX.setDuration(1000);
            bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(feed_like_image, "scaleY", 0.5f, 1f);
            bounceAnimY.setDuration(1000);
            bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);


            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {

                    feed_like_image.setImageDrawable(feedLikeDrawable);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animatorSet.play(rotationAnim);
            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);


            animatorSet.start();
        } else {
            feed_like_image.setImageDrawable(feedLikeDrawable);
        }
    }

    private void feedComment(DTOUserFeedDetails.FeedDetails feed) {
        updateComment = true;
        Dialog dialog = new Dialog(GeneralFeedDetailScreen.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_dialog);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView total_comments_text = dialog.findViewById(R.id.total_comments_text);
        ImageView comment_close = dialog.findViewById(R.id.comment_close);
        EditText comment_text = dialog.findViewById(R.id.comment_text);
        ImageButton send_comment_button = dialog.findViewById(R.id.send_comment_button);
        TextView no_comments = dialog.findViewById(R.id.no_comments);
        RecyclerView comment_list_rv = dialog.findViewById(R.id.comment_list_rv);

        Drawable feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);

        send_comment_button.setBackground(drawableColor(feedSendDrawable));


        try {
            final String message = "/userFeedComments/" + "feed" + feed.getFeedId();
            ValueEventListener commentsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> allCommentsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCommentsMap != null) {
                            ArrayList<AlertCommentData> commentsList = new ArrayList<>();

                            for (String queskey : allCommentsMap.keySet()) {
                                Object commentDataObject = allCommentsMap.get(queskey);
                                final Map<String, Object> commentsDataMap = (Map<String, Object>) commentDataObject;
                                if (commentsDataMap != null) {

                                    AlertCommentData alertCommentData = new AlertCommentData();
                                    if (commentsDataMap.get("addedOnDate") != null) {
                                        alertCommentData.setAddedOnDate(OustSdkTools.newConvertToLong(commentsDataMap.get("addedOnDate")));
                                    }

                                    if (commentsDataMap.get("comment") != null) {
                                        alertCommentData.setComment((String) commentsDataMap.get("comment"));
                                    }
                                    if (commentsDataMap.get("userAvatar") != null) {
                                        alertCommentData.setUserAvatar((String) commentsDataMap.get("userAvatar"));
                                    }
                                    if (commentsDataMap.get("userDisplayName") != null) {
                                        alertCommentData.setUserDisplayName((String) commentsDataMap.get("userDisplayName"));
                                    }
                                    if (commentsDataMap.get("userId") != null) {
                                        alertCommentData.setUserId((String) commentsDataMap.get("userId"));
                                    }
                                    if (commentsDataMap.get("userKey") != null) {
                                        alertCommentData.setUserKey(OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }

                            String totalComments = "";


                            if (commentsList.size() != 0) {
                                comment_list_rv.setVisibility(View.VISIBLE);
                                no_comments.setVisibility(View.GONE);
                                Collections.sort(commentsList, AlertCommentData.commentSorter);

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(GeneralFeedDetailScreen.this, commentsList, activeUser);
                                comment_list_rv.setItemAnimator(new DefaultItemAnimator());
                                comment_list_rv.setAdapter(feedCommentAdapter);
                                if (commentsList.size() > 1) {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comments_text);
                                } else {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comment_text);
                                }


                                if (!updateComment) {
                                    feed.setNumComments(commentsList.size());
                                    feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
                                    feed.setCommented(true);
                                    if (feed.isCommented()) {
                                        Drawable feedCommented = getResources().getDrawable(R.drawable.ic_comment_selected);


                                        feed_comment_image.setImageDrawable(drawableColor(feedCommented));
                                    }

                                    Intent intent = new Intent(GeneralFeedDetailScreen.this, FeedUpdatingServices.class);
                                    intent.putExtra("FeedId", feed.getFeedId());
                                    intent.putExtra("FeedCommentSize", commentsList.size());
                                    startService(intent);
                                    //updateCommentCount(""+feed.getFeedId(),commentsList.size());
                                }
                            } else {
                                updateComment = false;
                                comment_list_rv.setVisibility(View.GONE);
                                no_comments.setVisibility(View.VISIBLE);
                            }
                            updateComment = false;
                            total_comments_text.setText(totalComments);

                            feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
                        } else {
                            updateComment = false;
                        }
                    } else {
                        updateComment = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(commentsListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(commentsListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String comment_send_text = comment_text.getText().toString();
                Drawable feedSendDrawable;

                if (comment_send_text.isEmpty()) {
                    send_comment_button.setEnabled(false);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);
                } else {
                    send_comment_button.setEnabled(true);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_selected);
                }
                send_comment_button.setBackground(drawableColor(feedSendDrawable));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        send_comment_button.setOnClickListener(v -> {

            String comment = comment_text.getText().toString();
            if (!comment.isEmpty()) {

                AlertCommentData alertCommentData = new AlertCommentData();
                alertCommentData.setComment(comment);
                alertCommentData.setAddedOnDate(System.currentTimeMillis());
                alertCommentData.setDevicePlatform("Android");
                alertCommentData.setUserAvatar(activeUser.getAvatar());
                alertCommentData.setUserId(activeUser.getStudentid());
                alertCommentData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
                alertCommentData.setUserDisplayName(activeUser.getUserDisplayName());
                alertCommentData.setNumReply(0);
                isFeedChange = true;
                sendCommentToFirebase(alertCommentData, "" + feed.getFeedId());
                isFeedChange = true;
                comment_text.setText("");
            }
        });

        comment_close.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void sendCommentToFirebase(AlertCommentData alertCommentData, String feedId) {
        String message = "/userFeedComments/" + "feed" + feedId;
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(alertCommentData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(false);
        setidToUserFeedThread(newPostRef.getKey(), feedId);
    }

    private void setidToUserFeedThread(String key, String feedId) {
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "commentThread/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
    }


    public void rateApp() {
        try {
            String packageName = OustSdkApplication.getContext().getPackageName();
            Log.e("Package : ", packageName);
            if (!packageName.isEmpty()) {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } else {
                OustSdkApplication.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.mili.jobsmili")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void gotoSurvey(String assessmentId, String surveyTitle) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }
                intent.putExtra("surveyTitle", surveyTitle);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoGamelet(String assessmentId, String type) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), GameLetActivity.class);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("feedType", type);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void joinMeeting(String meetingId) {
        if ((meetingId != null) && (meetingId.length() > 8) && (meetingId.length() < 12)) {
            boolean isAppInstalled = appInstalledOrNot();
            Intent intent;
            if (isAppInstalled) {
                intent = new Intent();
                intent.setComponent(new ComponentName("com.oustme.oustlive", "com.oustme.oustlive.ZoomJoinActivity"));
                intent.putExtra("zoommeetingId", meetingId);
                intent.putExtra("userName", OustAppState.getInstance().getActiveUser().getUserDisplayName());
                intent.putExtra("isComingThroughOust", true);
            } else {
                intent = new Intent(OustSdkApplication.getContext(), ZoomBaseActivity.class);
                intent.putExtra("joinMeeting", true);
            }
            startActivity(intent);
        } else {

            OustSdkTools.showToast(getResources().getString(R.string.invalid_meeting_id));
        }
    }

    private boolean appInstalledOrNot() {
        PackageManager pm = OustSdkApplication.getContext().getPackageManager();
        try {
            pm.getPackageInfo("com.oustme.oustlive", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    private void openAttachment(String fileUrl, String feedHeader) {
        Intent redirectScreen = new Intent(GeneralFeedDetailScreen.this, RedirectWebView.class);
        redirectScreen.putExtra("url", fileUrl);
        redirectScreen.putExtra("feed_title", feedHeader);
        startActivity(redirectScreen);
    }


    private void setUserShareCount(long feedId, DTOUserFeedDetails.FeedDetails feed) {

        try {
            String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isShared";
            OustFirebaseTools.getRootRef().child(message1).setValue(true);
            OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

            String message = "feeds/feed" + feedId + "/numShares";
            DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);

            firebase.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    int count;
                    if (currentData.getValue() == null) {
                        currentData.setValue(1);
                        count = 1;
                    } else {
                        currentData.setValue((Long) currentData.getValue() + 1);
                        count = (int) (long) currentData.getValue();
                    }
                    feed.setNumShares(count);
                    runOnUiThread(() -> feed_share.setText(String.valueOf(feed.getNumShares())));

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (DatabaseError != null) {
                        Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.e("", "Firebase counter increment succeeded.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        backScreen();
    }

    private void backScreen() {
        if (feed != null) {
            OustStaticVariableHandling.getInstance().setResult_code(1444);
            OustStaticVariableHandling.getInstance().setFeedId(feed.getFeedId());
            OustStaticVariableHandling.getInstance().setFeedChanged(isFeedChange);
            OustStaticVariableHandling.getInstance().setLikeClicked(isLikeClicked);
            OustStaticVariableHandling.getInstance().setNumOfComments(feed.getNumComments());
            OustStaticVariableHandling.getInstance().setNumOfLikes(feed.getNumLikes());
            OustStaticVariableHandling.getInstance().setNumOfShares(feed.getNumShares());
            Intent data = new Intent();
            data.putExtra("FeedPosition", feed.getFeedId());
            data.putExtra("FeedComment", feed.getNumComments());
            data.putExtra("isFeedChange", isFeedChange);
            data.putExtra("isLikeClicked", isLikeClicked);
            data.putExtra("isFeedShareCount", feed.getNumShares());
            data.putExtra("isFeedLikeCount", feed.getNumLikes());
            data.putExtra("isClicked", true);
            setResult(1444, data);
        }
        finish();
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
        try {
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);

        }

        return activeGame;
    }

    private void distributeCPL(long cplId) {
        String cplURL = OustSdkApplication.getContext().getResources().getString(R.string.cpl_distribution_api);
        cplURL = HttpManager.getAbsoluteUrl(cplURL);
        cplURL = cplURL.replace("{cplId}", "" + cplId);
        String user_id = activeUser.getStudentid();
        List<String> users = new ArrayList<>();
        users.add(user_id);
        CPLDistrClass cplDistrClass = new CPLDistrClass();
        cplDistrClass.setDistributeDateTime(OustSdkTools.getDateTimeFromMilli2(SystemClock.currentThreadTimeMillis()));
        cplDistrClass.setUsers(users);
        cplDistrClass.setReusabilityAllowed(true);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(cplDistrClass);

        ApiCallUtils.doNetworkCall(Request.Method.PUT, cplURL, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                OustSdkTools.showToast(getResources().getString(R.string.success));
                backScreen();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    public static class CPLDistrClass {
        List<String> users;
        boolean sendSMS;
        boolean sendEmail;
        boolean sendNotification;
        boolean onlyPANIndia;
        String distributeDateTime;

        boolean reusabilityAllowed;

        public CPLDistrClass(List<String> users, boolean sendSMS, boolean sendEmail, boolean sendNotification, boolean onlyPANIndia, String distributeDateTime) {
            this.users = users;
            this.sendSMS = sendSMS;
            this.sendEmail = sendEmail;
            this.sendNotification = sendNotification;
            this.onlyPANIndia = onlyPANIndia;
            this.distributeDateTime = distributeDateTime;
        }

        public CPLDistrClass() {
        }

        public boolean isReusabilityAllowed() {
            return reusabilityAllowed;
        }

        public void setReusabilityAllowed(boolean reusabilityAllowed) {
            this.reusabilityAllowed = reusabilityAllowed;
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public boolean isSendSMS() {
            return sendSMS;
        }

        public void setSendSMS(boolean sendSMS) {
            this.sendSMS = sendSMS;
        }

        public boolean isSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public boolean isSendNotification() {
            return sendNotification;
        }

        public void setSendNotification(boolean sendNotification) {
            this.sendNotification = sendNotification;
        }

        public boolean isOnlyPANIndia() {
            return onlyPANIndia;
        }

        public void setOnlyPANIndia(boolean onlyPANIndia) {
            this.onlyPANIndia = onlyPANIndia;
        }

        public String getDistributeDateTime() {
            return distributeDateTime;
        }

        public void setDistributeDateTime(String distributeDateTime) {
            this.distributeDateTime = distributeDateTime;
        }

    }

    public void downloadManager(String downloadUrl, String fileName) {

        try {
            final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(downloadUrl);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(fileName);
            request.setDescription(fileName);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(GeneralFeedDetailScreen.this, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
            long ref = downloadManager.enqueue(request);

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);


            final BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long downloadReference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                    if (downloadReference == -1) {
                        return;
                    }

                    if (downloadReference == ref) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadReference);

                        Cursor cur = downloadManager.query(query);

                        if (cur.moveToFirst()) {
                            int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            @SuppressLint("Range") int idFromCursor = cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_ID));


                            if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                                @SuppressLint("Range") String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                //update percentage

                                Log.i("DownloadHandler", "Download completed " + uriString);
                                if (uriString != null) {
                                    File sourceFile = new File(Objects.requireNonNull(Uri.parse(uriString).getPath()));
                                    File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                    try {
                                        OustSdkTools.copyFile(sourceFile, destinationFile);
                                        try {
                                            branding_mani_layout.setVisibility(View.GONE);
                                            Uri outputFileUri = FileProvider.getUriForFile(GeneralFeedDetailScreen.this, OustSdkApplication.getContext().getPackageName() + ".provider", destinationFile);
                                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                                            downloadIntent.setDataAndType(outputFileUri, "application/pdf");
                                            downloadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(downloadIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                            branding_mani_layout.setVisibility(View.GONE);
                                            openAttachment(fileName, feed.getHeader() + "");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        branding_mani_layout.setVisibility(View.GONE);
                                    }
                                }

                            } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                                branding_mani_layout.setVisibility(View.GONE);
                                int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                                int reason = cur.getInt(columnReason);
                                openAttachment(fileName, feed.getHeader() + "");
                                switch (reason) {
                                    case DownloadManager.ERROR_FILE_ERROR:
                                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    case DownloadManager.ERROR_CANNOT_RESUME:
                                    case DownloadManager.ERROR_UNKNOWN:
                                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                        //handle error
                                        break;
                                }
                            }
                            // downloadManager.remove(idFromCursor);
                        }
                        cur.close();
                    }
                }

            };
            getApplicationContext().registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void feedClicked(long feedId, long cplId) {
        try {
            if (feedClickListener != null) {
                feedClickListener.onFeedClick(feedId, (int) cplId);
                feedClickListener.onFeedViewed(feedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
