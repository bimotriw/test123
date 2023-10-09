package com.oustme.oustsdk.feed_ui.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.getYTVID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.tools.FullScreenHelper;
import com.oustme.oustsdk.feed_ui.tools.OustSdkTools;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PublicVideoFeedCardScreen extends AppCompatActivity {
    private static final String TAG = "PublicVideoFeedCard";
    YouTubePlayer youtubePlayer;
    private FullScreenHelper fullScreenHelper;

    Toolbar toolbar_root;
    LinearLayout other_lay;
    ImageView feed_image_thumbnail;
    ImageView volume_control;
    ImageView pause_control;
    public YouTubePlayerView feed_public_video;
    ImageView play_button;
    View opacity_view;
    FrameLayout media_container;
    NestedScrollView feed_detail_lay_scroll;
    TextView feed_title;
    FrameLayout toolbar_close;
    ImageView toolbar_close_icon;
    TextView feed_title_full;
    TextView feed_dead_line;
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
    FrameLayout feed_read_more;
    TextView feed_action_name;
    LinearLayout feed_action_lay;
    LinearLayout timer_layout;
    ProgressBar mandatory_timer_progress;
    TextView mandatory_timer_text;
    RelativeLayout file_attach_lay;
    ImageView file_attach_image;
    TextView file_attach_text;
    LinearLayout public_main_layout;
    ImageView public_video_card_background_img;
    WebView feed_description_webView;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    //End
    private enum VolumeState {ON, OFF}

    private VolumeState volumeState;
    private RequestManager requestManager;

    DTOUserFeedDetails.FeedDetails feed;
    boolean isFeedComment, isFeedLikeable;
    //    CourseCardClass courseCardClass;
    String courseCardClassString;
    String shareContent = " Download Oustsdk...";
    ActiveUser activeUser;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private final int mPlayerLength = 0;
    private boolean updateComment;
    private boolean isFeedChange, isLikeClicked;
    MandatoryCountDown mandatoryCountDown;
    int progress;
    private long mLastTimeClicked = 0;
    boolean fullScreen = false;
    private boolean feedAutoCloseAfterCompletion = false;
    Dialog dialog;
    String youtubeVideoKey;
    private String feedLink;
    long timeStamp;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        com.oustme.oustsdk.tools.OustSdkTools.setLocale(PublicVideoFeedCardScreen.this);
        setContentView(R.layout.activity_public_video_feed_card_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        fullScreenHelper = new FullScreenHelper(this);

        try {
            toolbar_root = findViewById(R.id.toolbar_root);
            file_attach_text = findViewById(R.id.file_attach_text);
            file_attach_image = findViewById(R.id.file_attach_image);
            file_attach_lay = findViewById(R.id.file_attach_lay);
            mandatory_timer_text = findViewById(R.id.mandatory_timer_text);
            mandatory_timer_progress = findViewById(R.id.mandatory_timer_progress);
            timer_layout = findViewById(R.id.timer_layout);
            feed_link = findViewById(R.id.feed_link);
            feed_action_name = findViewById(R.id.feed_action_name);
            feed_link_div = findViewById(R.id.feed_link_div);
            feed_read_more = findViewById(R.id.feed_read_more);
            feed_action_lay = findViewById(R.id.feed_action_lay);
            feed_comment_lay = findViewById(R.id.feed_comment_lay);
            feed_like_image = findViewById(R.id.feed_like_image);
            feed_like = findViewById(R.id.feed_like);
            feed_comment = findViewById(R.id.feed_comment);
            feed_share_lay = findViewById(R.id.feed_share_lay);
            feed_share_image = findViewById(R.id.feed_share_image);
            feed_share = findViewById(R.id.feed_share);
            feed_comment_image = findViewById(R.id.feed_comment_image);
            other_lay = findViewById(R.id.other_lay);
            feed_image_thumbnail = findViewById(R.id.feed_image_thumbnail);
            volume_control = findViewById(R.id.volume_control);
            pause_control = findViewById(R.id.pause_control);
            feed_public_video = findViewById(R.id.feed_public_video);
            play_button = findViewById(R.id.play_button);
            opacity_view = findViewById(R.id.opacity_view);
            media_container = findViewById(R.id.media_container);
            feed_detail_lay_scroll = findViewById(R.id.feed_detail_lay_scroll);
            feed_title = findViewById(R.id.feed_title);
            toolbar_close = findViewById(R.id.toolbar_close);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            feed_title_full = findViewById(R.id.feed_title_full);
            feed_dead_line = findViewById(R.id.feed_dead_line);
            feed_date = findViewById(R.id.feed_date);
            feed_like_lay = findViewById(R.id.feed_like_lay);
            feed_description = findViewById(R.id.feed_description);
            public_video_card_background_img = findViewById(R.id.public_video_card_background_img);
            public_main_layout = findViewById(R.id.public_main_layout);
            feed_description_webView = findViewById(R.id.feed_description_webView);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

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
            feedAutoCloseAfterCompletion = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }

        Bundle feedBundle = getIntent().getExtras();
        if (feedBundle != null) {
            feed = feedBundle.getParcelable("Feed");
            courseCardClassString = feedBundle.getString("CardData");
            isFeedComment = feedBundle.getBoolean("FeedComment");
            isFeedLikeable = feedBundle.getBoolean("isFeedLikeable");
            timeStamp = feedBundle.getLong("timeStamp", 0);
//            courseCardClass = (CourseCardClass) feedBundle.getSerializable("CardData");
            activeUser = (ActiveUser) feedBundle.getSerializable("ActiveUser");
        }

        if (activeUser == null | activeUser.getStudentid() == null) {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = com.oustme.oustsdk.tools.OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        }

        try {
            if (feed != null) {
                initializePlayer(feed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
        requestManager = initGlide();

        setIconColors();

        toolbar_close.setOnClickListener(v -> backScreen());
        toolbar_close_icon.setOnClickListener(v -> backScreen());


        if (feed != null) {
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
            setFeedLike(feed, false);

            if (feed.getCardInfo() != null && feed.getCardInfo().getBgImg() != null && !feed.getCardInfo().getBgImg().isEmpty() && !feed.getCardInfo().getBgImg().equalsIgnoreCase("")) {
                String imageUrl = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + "course/mpower/" + feed.getCardInfo().getBgImg();
                Glide.with(this).load(imageUrl).into(public_video_card_background_img);
                public_video_card_background_img.setVisibility(VISIBLE);
                public_main_layout.setBackgroundColor(0);
            } else {
                public_main_layout.setBackgroundColor(Color.parseColor("#ffffffff"));
                public_video_card_background_img.setVisibility(GONE);
            }

            if (feed.getCardInfo() != null && feed.getCardInfo().getReadMoreData() != null && feed.getCardInfo().getReadMoreData().getDisplayText() != null && feed.getCardInfo().getReadMoreData().getData() != null) {
                if (!feed.getCardInfo().getReadMoreData().getData().isEmpty() && feed.getCardInfo().getReadMoreData().getType() != null) {
                    file_attach_lay.setVisibility(VISIBLE);

                    if (feed.getCardInfo().getReadMoreData().getType().equalsIgnoreCase("URL_LINK")) {
                        Drawable linkDrawable = com.oustme.oustsdk.tools.OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_weblink));
                        file_attach_image.setImageDrawable(linkDrawable);
                        file_attach_text.setText(feed.getCardInfo().getReadMoreData().getDisplayText());
                        file_attach_lay.setOnClickListener(view -> {
                            Intent redirectScreen = new Intent(PublicVideoFeedCardScreen.this, RedirectWebView.class);
                            redirectScreen.putExtra("url", feed.getCardInfo().getReadMoreData().getData());
                            redirectScreen.putExtra("feed_title", feed.getHeader());
                            startActivity(redirectScreen);
                        });
                    } else {
                        String fileName = CLOUD_FRONT_BASE_PATH + "readmore/file/" + feed.getCardInfo().getReadMoreData().getData();
                        file_attach_text.setText(feed.getCardInfo().getReadMoreData().getDisplayText());
                        file_attach_lay.setOnClickListener(v -> {
                            if (fileName.contains(".pdf")) {
                                if (com.oustme.oustsdk.tools.OustSdkTools.checkInternetStatus()) {
                                    branding_mani_layout.setVisibility(VISIBLE);
                                    downloadManager(fileName, "feed_" + feed.getCardInfo().getReadMoreData().getData());
                                } else {
                                    openAttachment(fileName, feed.getHeader());
                                }
                            } else {
                                openAttachment(fileName, feed.getHeader());
                            }
                        });
                    }
                } else {
                    file_attach_lay.setVisibility(View.GONE);
                }
            } else {
                file_attach_lay.setVisibility(View.GONE);
            }

            feed_link.setVisibility(GONE);
            feed_link_div.setVisibility(GONE);

            if (feed != null) {
                if (feed.getCardInfo() != null && feed.getCardInfo().getCardTitle() != null && !feed.getCardInfo().getCardTitle().isEmpty()) {
                    Spanned feedHeader = Html.fromHtml(feed.getCardInfo().getCardTitle().trim());
                    feed_title_full.setText(feedHeader);
                    feed_title.setText(feedHeader);
                    feed_title_full.setVisibility(View.VISIBLE);
                    feed_title.setVisibility(View.VISIBLE);
                } else {
                    if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
                        Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
                        feed_title_full.setText(feedHeader);
                        feed_title.setText(feedHeader);
                        feed_title_full.setVisibility(View.VISIBLE);
                        feed_title.setVisibility(View.VISIBLE);
                    }
                }
            }

            feedLink = feed.getButton().getBtnActionURI();
            if ((feedLink != null) && (!feedLink.isEmpty())) {
                if (!feedLink.startsWith("http://") && !feedLink.startsWith("https://")) {
                    feedLink = "http://" + feedLink;
                }

                feed_read_more.setVisibility(View.VISIBLE);
                feed_link.setVisibility(View.GONE);
                feed_link_div.setVisibility(View.GONE);
            }

            feed_read_more.setOnClickListener(v -> {
                if ((feedLink != null) && (!feedLink.isEmpty())) {
                    Intent redirectScreen = new Intent(PublicVideoFeedCardScreen.this, RedirectWebView.class);
                    redirectScreen.putExtra("url", feedLink);
                    redirectScreen.putExtra("feed_title", feed.getHeader());
                    startActivity(redirectScreen);
                }
            });

            try {
                if (feed != null && feed.getCardInfo().getContent() != null && !feed.getCardInfo().getContent().isEmpty()) {
                    if (feed.getCardInfo().getContent().contains("<li>") || feed.getCardInfo().getContent().contains("</li>") ||
                            feed.getCardInfo().getContent().contains("<ol>") || feed.getCardInfo().getContent().contains("</ol>") ||
                            feed.getCardInfo().getContent().contains("<p>") || feed.getCardInfo().getContent().contains("</p>")) {
                        feed_description_webView.setVisibility(View.VISIBLE);
                        feed_description.setVisibility(View.GONE);
                        feed_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = com.oustme.oustsdk.tools.OustSdkTools.getDescriptionHtmlFormat(feed.getCardInfo().getContent());
                        final WebSettings webSettings = feed_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(16);
                        feed_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        feed_description.setVisibility(VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            feed_description.setText(Html.fromHtml(feed.getCardInfo().getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            feed_description.setText(Html.fromHtml(feed.getCardInfo().getContent()));
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
                com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            }

            String feedDate = OustSdkTools.milliToDate("" + timeStamp);
            if (!feedDate.isEmpty()) {
                feed_date.setVisibility(View.VISIBLE);
                feed_date.setText(feedDate);
            }
            if (feed.getFeedExpiry() != null && !feed.getFeedExpiry().isEmpty()) {
                String feedDeadLine = OustSdkTools.milliToDate("" + feed.getFeedExpiry());
                if (feedDeadLine != null && !feedDeadLine.isEmpty()) {
                    feed_dead_line.setVisibility(View.VISIBLE);
                    feedDeadLine = "Deadline " + feedDeadLine.toUpperCase();
                    feed_dead_line.setText(feedDeadLine);
                }
            }

            feed_comment.setText(OustSdkTools.formatMilliinFormat(feed.getNumComments()));
            if (isFeedComment) {
                feedComment(feed);
            }

            if (feed.isCommented()) {
                Drawable feedCommented = getResources().getDrawable(R.drawable.ic_comment_selected);
                feed_comment_image.setImageDrawable(drawableColor(feedCommented));
            }


            if (feed.getNumShares() != 0) {
                feed_share.setText(String.valueOf(feed.getNumShares()));
            }

            String imageUrl = "";
            if (feed != null && feed.getCardInfo().getCardMediaList() != null && feed.getCardInfo().getCardMediaList().size() != 0) {
                if (feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail() != null && !feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail().isEmpty()) {
                    imageUrl = feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail();
                } else {
                    if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                        imageUrl = feed.getImageUrl();
                    } else {
                        imageUrl = "https://img.youtube.com/vi/" + getYTVID(feed.getCardInfo().getCardMediaList().get(0).getData()) + "/0.jpg";

                    }
                }
            } else if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                imageUrl = feed.getImageUrl();

            }

            if (!imageUrl.isEmpty()) {
                Glide.with(PublicVideoFeedCardScreen.this).load(imageUrl).placeholder(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).
                        error(com.oustme.oustsdk.tools.OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.feed_default))).into(feed_image_thumbnail);

            }

            String finalImageUrl = imageUrl;

            if (feed.getButton().getBtnText() != null && !feed.getButton().getBtnText().isEmpty()) {
                feed_action_name.setText(OustSdkTools.returnValidString(feed.getButton().getBtnText()));
            }

            play_button.setOnClickListener(view -> {
                if (youtubePlayer != null) {
                    youtubePlayer.play();
                }
            });

            feed_share_lay.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                    return;
                }
                mLastTimeClicked = SystemClock.elapsedRealtime();
                feed_share_lay.setClickable(false);
                shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                setUserShareCount(feed.getFeedId(), feed);
                isFeedChange = true;
                if (!finalImageUrl.isEmpty()) {
                    ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                        HttpURLConnection connection = null;

                        try {
                            connection = (HttpURLConnection) com.oustme.oustsdk.tools.OustSdkTools.stringToURL("" + finalImageUrl).openConnection();

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
                            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                        } finally {
                            assert connection != null;
                            connection.disconnect();
                        }
                    });

                } else {
                    Intent shareIntent;
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "Share with"));
                    feed_share_lay.setClickable(true);
                }
            });

            feed_like_lay.setOnClickListener(v -> feedLike(feed));

            if (feed != null) {
                if (feed.getCardInfo().getMandatoryViewTime() != 0) {
                    mandatory_timer_progress.setMax((int) (feed.getCardInfo().getMandatoryViewTime() * 1000));
                    mandatoryCountDown = new MandatoryCountDown((feed.getCardInfo().getMandatoryViewTime() * 1000) + 1000, 1000);
                    mandatoryCountDown.start();
                }
            }
            setVolumeControl(VolumeState.ON);

            volume_control.setOnClickListener(v -> {
                if (feed_public_video != null) {
                    if (volumeState == VolumeState.OFF) {
                        Log.d(TAG, "togglePlaybackState: enabling volume.");
                        setVolumeControl(VolumeState.ON);

                    } else if (volumeState == VolumeState.ON) {
                        Log.d(TAG, "togglePlaybackState: disabling volume.");
                        setVolumeControl(VolumeState.OFF);
                    }
                }
            });
        }

        if (feed != null) {
            if (feed.getCardInfo().isPotraitModeVideo()) {
                media_container.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 560, getResources().getDisplayMetrics());
                media_container.requestLayout();
                //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                other_lay.setVisibility(GONE);
                toolbar_root.setVisibility(GONE);
                feed_read_more.setVisibility(GONE);
                media_container.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                media_container.requestLayout();
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if ((feedLink != null) && (!feedLink.isEmpty())) {
                    feed_read_more.setVisibility(VISIBLE);
                }
            }

            String mUrl = feed.getButton().getBtnActionURI();
            if (mUrl == null || mUrl.isEmpty()) {
                feed_read_more.setVisibility(GONE);
            }

        }
        feed_comment_lay.setOnClickListener(v -> feedComment(feed));
    }


    private void setIconColors() {
        //comment_image_icon
        feed_comment_image.setImageDrawable(drawableColor(getResources().getDrawable(R.drawable.ic_comment_unselected)));

        //share_image_icon

        feed_share_image.setImageDrawable(drawableColor(getResources().getDrawable(R.drawable.ic_share_common)));

        //like_image
        Drawable feedLikeDrawable = getResources().getDrawable(R.drawable.ic_like_common);

        feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));


        //feed_attach_image
        Drawable feedAttachDrawable = getResources().getDrawable(R.drawable.ic_attach_fill);

        file_attach_image.setImageDrawable(drawableColor(feedAttachDrawable));

        //course_action_page
        Drawable readActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);

        feed_read_more.setBackground(drawableColor(readActionDrawable));
    }


    private void setFeedLike(DTOUserFeedDetails.FeedDetails feed, boolean isAnimation) {

        feed_like.setText(OustSdkTools.formatMilliinFormat(feed.getNumLikes()));


        Drawable feedLikeDrawable = getResources().getDrawable(R.drawable.ic_like_common);
        if (feed.isLiked()) {
            feedLikeDrawable = getResources().getDrawable(R.drawable.ic_liked_common);
        }


        feed_like_image.setImageDrawable(drawableColor(feedLikeDrawable));
        likeButtonAnimation(isAnimation, feedLikeDrawable);
    }

    private void feedLike(DTOUserFeedDetails.FeedDetails feed) {
        updateFeedViewed(feed);
        isFeedChange = true;
//        if (feed.isLikeble()) {
        if (!feed.isLiked()) {
            feed.setNumLikes(feed.getNumLikes() + 1);
            feed.setLiked(true);
            isLikeClicked = true;
            setUserLike(feed, true);
        } else {
            if (feed.getNumLikes() > 0) {
                feed.setLiked(false);
                feed.setNumLikes(feed.getNumLikes() - 1);
                isLikeClicked = false;
                setUserLike(feed, false);
            }
        }
//        }
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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed) {
        try {
            if (!mFeed.isClicked()) {
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
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

            MediaPlayer player = MediaPlayer.create(PublicVideoFeedCardScreen.this, R.raw.thud_sound);
            player.setLooping(false);
            player.setVolume(100, 100);


            bounceAnimY.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    player.seekTo(mPlayerLength);
                    player.start();
                    feed_like_image.setImageDrawable(feedLikeDrawable);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (player.isPlaying()) {
                        player.stop();
                        //mPlayerLength = player.getCurrentPosition();
                    }
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
        if (youtubePlayer != null) {
            youtubePlayer.pause();
        }

        updateComment = true;
        dialog = new Dialog(PublicVideoFeedCardScreen.this, R.style.DialogTheme);
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
                                        alertCommentData.setAddedOnDate(com.oustme.oustsdk.tools.OustSdkTools.newConvertToLong(commentsDataMap.get("addedOnDate")));
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
                                        alertCommentData.setUserKey(com.oustme.oustsdk.tools.OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }

                            String totalComments = "";

                            if (commentsList.size() != 0) {
                                comment_list_rv.setVisibility(View.VISIBLE);
                                no_comments.setVisibility(View.GONE);
                                Collections.sort(commentsList, AlertCommentData.commentSorter);

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(PublicVideoFeedCardScreen.this, commentsList, activeUser);
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

                                    Intent intent = new Intent(PublicVideoFeedCardScreen.this, FeedUpdatingServices.class);
                                    intent.putExtra("FeedId", feed.getFeedId());
                                    intent.putExtra("FeedCommentSize", commentsList.size());
                                    startService(intent);
                                    //updateCommentCount(""+feed.getFeedId(),commentsList.size());
                                }

                                updateComment = false;
                            } else {
                                updateComment = false;
                                comment_list_rv.setVisibility(View.GONE);
                                no_comments.setVisibility(View.VISIBLE);
                            }

                            total_comments_text.setText(totalComments);

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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
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
                comment_text.setText("");
            }
        });

        dialog.setOnDismissListener(dialog -> {
            try {
                if (youtubePlayer != null) {
                    youtubePlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
                com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
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
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        setidToUserFeedThread(newPostRef.getKey(), feedId);
        updateFeedViewed(feed);
        // updateCommentCount(feedId);
    }

    private void setidToUserFeedThread(String key, String feedId) {
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "commentThread/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public void onBackPressed() {
        try {
//            onBackPressed = true;
            if (feedAutoCloseAfterCompletion) {
                backScreen();
            } else {
                if (fullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullScreenHelper.exitFullScreen();
                    fullScreen = false;
                } else {
                    backScreen();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void backScreen() {
        try {
            if (progress == 0) {
                feed_public_video.release();
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
                    data.putExtra("isFeedShareCount", feed.getNumShares());
                    data.putExtra("isClicked", true);
                    setResult(1444, data);
                }
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void initializePlayer(DTOUserFeedDetails.FeedDetails courseCardClass) {
        try {
            fullScreenHelper = new FullScreenHelper(PublicVideoFeedCardScreen.this);
            youtubeVideoKey = getYTVID(courseCardClass.getCardInfo().getCardMediaList().get(0).getData());
            if (courseCardClass.getCardInfo().isPotraitModeVideo()) {
                ViewGroup.LayoutParams layoutParams = feed_public_video.getLayoutParams();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                double scrHeight = metrics.heightPixels * 0.75;
                layoutParams.height = (int) scrHeight;
                feed_public_video.setLayoutParams(layoutParams);
            }
            feed_public_video.setVisibility(View.VISIBLE);
            feed_public_video.initialize(new AbstractYouTubePlayerListener() {
                @Override
                public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                    Log.d("error occured", error.toString());
                    feed_image_thumbnail.setVisibility(View.VISIBLE);
                    opacity_view.setVisibility(View.VISIBLE);
                    feed_public_video.setVisibility(View.GONE);
                    play_button.setVisibility(View.VISIBLE);
                }

                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youtubePlayer = youTubePlayer;
                    feed_image_thumbnail.setVisibility(GONE);
                    opacity_view.setVisibility(GONE);
                    feed_public_video.setVisibility(VISIBLE);
                    play_button.setVisibility(GONE);
                    youtubePlayer.loadVideo(youtubeVideoKey, 0);
                    youTubePlayer.setVolume(80);
                    addFullScreenListenerToPlayer();
                }

                @Override
                public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                    if (state == PlayerConstants.PlayerState.PAUSED) {
                        feed_image_thumbnail.setVisibility(GONE);
                        opacity_view.setVisibility(GONE);
                        feed_public_video.setVisibility(VISIBLE);
                        play_button.setVisibility(GONE);
                        volume_control.bringToFront();
//                        feedRewardUpdate(feed);
                    } else if (state == PlayerConstants.PlayerState.PLAYING) {
                        feed_image_thumbnail.setVisibility(View.GONE);
                        opacity_view.setVisibility(View.GONE);
                        feed_public_video.setVisibility(View.VISIBLE);
                        play_button.setVisibility(View.GONE);
                    } else if (state == PlayerConstants.PlayerState.ENDED) {
                        feed_image_thumbnail.setVisibility(View.VISIBLE);
                        opacity_view.setVisibility(View.VISIBLE);
                        feed_public_video.setVisibility(View.GONE);
                        play_button.setVisibility(View.VISIBLE);
                        volume_control.bringToFront();
//                        feedRewardUpdate(feed);
                        if (feedAutoCloseAfterCompletion) {
                            backScreen();
                        } else {
                            if (mandatoryCountDown != null) {
                                mandatoryCountDown.cancel();
                            }
                            progress = 0;
                            toolbar_root.setVisibility(VISIBLE);
                            timer_layout.setVisibility(GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void addFullScreenListenerToPlayer() {
        feed_public_video.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onExitFullscreen() {
//                customVideoControlListener.fullHideToolbar();
                fullScreenHelper.enterFullScreen();
                fullScreen = true;
            }

            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                //                customVideoControlListener.fullShowToolbar();
                fullScreenHelper.exitFullScreen();
                fullScreen = false;
            }
        });
        if (isFeedComment) {
            if (youtubePlayer != null)
                youtubePlayer.pause();
        }
        try {
            feedLink = feed.getButton().getBtnActionURI();
            if ((feedLink != null) && (!feedLink.isEmpty())) {
                if (!feedLink.startsWith("http://") && !feedLink.startsWith("https://")) {
                    feedLink = "http://" + feedLink;
                }
                feed_read_more.setVisibility(View.VISIBLE);
            } else {
                feed_read_more.setVisibility(GONE);
            }
            feed_link.setVisibility(View.GONE);
            feed_link_div.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (feed_public_video != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setLandscapeVideoRation(feed_public_video);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelper.enterFullScreen();
                fullScreen = true;
                other_lay.setVisibility(GONE);
                toolbar_root.setVisibility(GONE);
                feed_read_more.setVisibility(GONE);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setPortaitVideoRatio(feed_public_video);
                fullScreenHelper.exitFullScreen();
                fullScreen = false;
                other_lay.setVisibility(VISIBLE);
                toolbar_root.setVisibility(VISIBLE);
                feed_read_more.setVisibility(VISIBLE);
            }
        }
        try {
            feedLink = feed.getButton().getBtnActionURI();
            if ((feedLink != null) && (!feedLink.isEmpty())) {
                if (!feedLink.startsWith("http://") && !feedLink.startsWith("https://")) {
                    feedLink = "http://" + feedLink;
                }
                feed_read_more.setVisibility(View.VISIBLE);
            } else {
                feed_read_more.setVisibility(GONE);
            }
            feed_link.setVisibility(View.GONE);
            feed_link_div.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void setLandscapeVideoRation(YouTubePlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) media_container.getLayoutParams();
                params.height = scrHeight;
                params.width = scrWidth;
                media_container.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    public void setPortaitVideoRatio(YouTubePlayerView simpleExoPlayerView) {
        fullScreen = false;
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) media_container.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                media_container.setLayoutParams(params);
//                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    public class MandatoryCountDown extends CountDownTimer {

        public MandatoryCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            try {
                toolbar_root.setVisibility(GONE);
                timer_layout.setVisibility(VISIBLE);
                progress = (int) (millisUntilFinished / 1000);
                String hms = String.format(Locale.US, "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                mandatory_timer_text.setText(hms);
                mandatory_timer_progress.setProgress((progress * 1000));
            } catch (Exception e) {
                e.printStackTrace();
                com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void onFinish() {
            try {
                toolbar_root.setVisibility(VISIBLE);
                timer_layout.setVisibility(GONE);
//                feedRewardUpdate(feed);
            } catch (Exception e) {
                e.printStackTrace();
                com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void feedRewardUpdate(DTONewFeed newFeed) {
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
                Intent intent = new Intent(OustSdkApplication.getContext(), FeedUpdatingServices.class);
                intent.putExtra("feedId", newFeed.getFeedId());
                intent.putExtra("feedCoins", newFeed.getFeedCoins());
                intent.putExtra("feedCoinsUpdate", true);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadManager(String downloadUrl, String fileName) {
        try {
            final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Log.e(TAG, "downloadManager: downloadUrl-> " + downloadUrl);
            Uri uri = Uri.parse(downloadUrl);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(fileName);
            request.setDescription(fileName);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(PublicVideoFeedCardScreen.this, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
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
//                            int idFromCursor = cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_ID));

                            if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                                int value = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                                String uriString = cur.getString(value);
                                //update percentage

                                Log.i("DownloadHandler", "Download completed " + uriString);
                                if (uriString != null) {
                                    File sourceFile = new File(Objects.requireNonNull(Uri.parse(uriString).getPath()));
                                    File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                    try {
                                        com.oustme.oustsdk.tools.OustSdkTools.copyFile(sourceFile, destinationFile);
                                        try {
                                            branding_mani_layout.setVisibility(GONE);
                                            Uri outputFileUri = FileProvider.getUriForFile(PublicVideoFeedCardScreen.this, OustSdkApplication.getContext().getPackageName() + ".provider", destinationFile);
                                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                                            downloadIntent.setDataAndType(outputFileUri, "application/pdf");
                                            downloadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(downloadIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                                            branding_mani_layout.setVisibility(GONE);
                                            openAttachment(downloadUrl, feed.getHeader() + "");
                                        }
                                    } catch (IOException e) {
                                        branding_mani_layout.setVisibility(GONE);
                                        e.printStackTrace();
                                        com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                                    }
                                }

                            } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                                int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                                int reason = cur.getInt(columnReason);
                                branding_mani_layout.setVisibility(GONE);
                                openAttachment(downloadUrl, feed.getHeader() + "");
                                switch (reason) {

                                    case DownloadManager.ERROR_FILE_ERROR:
                                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                    case DownloadManager.ERROR_UNKNOWN:
                                    case DownloadManager.ERROR_CANNOT_RESUME:
                                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
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
            branding_mani_layout.setVisibility(GONE);
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void openAttachment(String fileName, String header) {
        try {
            Intent redirectScreen = new Intent(PublicVideoFeedCardScreen.this, RedirectWebView.class);
            redirectScreen.putExtra("url", fileName);
            redirectScreen.putExtra("feed_title", header);
            startActivity(redirectScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void setVolumeControl(VolumeState state) {
        try {
            volumeState = state;
            volume_control.setVisibility(VISIBLE);
            if (state == VolumeState.OFF) {
                youtubePlayer.setVolume(0);
                animateVolumeControl();
            } else if (state == VolumeState.ON) {
                if (youtubePlayer != null) {
                    youtubePlayer.setVolume(70);
                    animateVolumeControl();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void animateVolumeControl() {
        try {
            if (volume_control != null) {
                volume_control.bringToFront();
                if (volumeState == VolumeState.OFF) {
                    requestManager.load(R.drawable.ic_volume_off_grey_24dp)
                            .into(volume_control);
                } else if (volumeState == VolumeState.ON) {
                    requestManager.load(R.drawable.ic_volume_up_grey_24dp)
                            .into(volume_control);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (feed_public_video != null) {
                fullScreen = false;
                if (youtubePlayer != null) {
                    youtubePlayer.pause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (feed_public_video != null) {
                if (youtubePlayer != null) {
                    youtubePlayer.play();
                } else {
                    if (feed != null) {
                        initializePlayer(feed);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (feed_public_video != null) {
                fullScreen = false;
                feed_public_video.removeAllViews();
                feed_public_video.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }
}
