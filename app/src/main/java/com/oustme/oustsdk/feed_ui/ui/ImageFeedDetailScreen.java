package com.oustme.oustsdk.feed_ui.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.drawableColor;
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
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
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

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.tools.FullScreenHelper;
import com.oustme.oustsdk.feed_ui.tools.OustSdkTools;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
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

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageFeedDetailScreen extends AppCompatActivity {

    private static final String TAG = "ImageFeedCard";

    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    ImageView feed_card_background_img;
    MenuItem itemAudio;
    LinearLayout other_lay;
    NestedScrollView feed_detail_lay_scroll;
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
    GifImageView feed_card_image;
    ImageView feed_card_image_gif;
    PopupWindow zoomImagePopup;
    ImageButton imageCloseButton;
    WebView feed_description_webView;

    PhotoViewAttacher photoViewAttacher;
    MediaPlayer mediaPlayerForAudio;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    DTOUserFeedDetails.FeedDetails feed;
    int color;
    int bgColor;
    boolean isFeedComment, isFeedLikeable;
    CourseCardClass courseCardClass;
    String courseCardClassString;
    String feedType;
    String shareContent = " Download Oustsdk...";
    ActiveUser activeUser;
    String finalImageUrl;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    final FavCardDetails favCardDetails = new FavCardDetails();
    String cardAudioFile;
    boolean isMute = false;
    boolean isAudioEnable = false;
    boolean pauseAudioPlayer = false;
    private boolean isAudioPlayTrackComplete = false;
    private final int mPlayerLength = 0;
    private boolean updateComment;
    private boolean isFeedChange, isLikeClicked;
    int height_toolbar;
    MandatoryCountDown mandatoryCountDown;
    int progress;
    private long mLastTimeClicked = 0;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        com.oustme.oustsdk.tools.OustSdkTools.setLocale(ImageFeedDetailScreen.this);
        setContentView(R.layout.activity_image_feed_detail_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FullScreenHelper fullScreenHelper = new FullScreenHelper(this);

        try {
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
            feed_detail_lay_scroll = findViewById(R.id.feed_detail_lay_scroll);
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            feed_title_full = findViewById(R.id.feed_title_full);
            feed_dead_line = findViewById(R.id.feed_dead_line);
            feed_date = findViewById(R.id.feed_date);
            feed_like_lay = findViewById(R.id.feed_like_lay);
            feed_description = findViewById(R.id.feed_description);
            feed_card_image = findViewById(R.id.feed_card_image);
            feed_card_image_gif = findViewById(R.id.feed_card_image_gif);
            feed_card_background_img = findViewById(R.id.feed_card_background_img);
            feed_description_webView = findViewById(R.id.feed_description_webView);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End
            getColors();

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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }

        Bundle feedBundle = getIntent().getExtras();
        if (feedBundle != null) {
            feed = feedBundle.getParcelable("Feed");
            timeStamp = feedBundle.getLong("timeStamp", 0);
            courseCardClassString = feedBundle.getString("CardData");
            isFeedComment = feedBundle.getBoolean("FeedComment");
            isFeedLikeable = feedBundle.getBoolean("isFeedLikeable");
            activeUser = (ActiveUser) feedBundle.getSerializable("ActiveUser");
            feedType = feedBundle.getString("feedType");
        }

        if (activeUser == null || activeUser.getStudentid() == null) {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = com.oustme.oustsdk.tools.OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        }

        if (courseCardClassString != null && !courseCardClassString.isEmpty()) {
            courseCardClass = new Gson().fromJson(courseCardClassString, CourseCardClass.class);
        }

        setIconColors();

        height_toolbar = toolbar.getLayoutParams().height;

        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        back_button.setOnClickListener(view -> onBackPressed());

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

//            feed.setLikeble(isFeedLikeable);
            setFeedLike(feed, false);

            if (courseCardClass != null && courseCardClass.getBgImg() != null && !courseCardClass.getBgImg().isEmpty() && !courseCardClass.getBgImg().equalsIgnoreCase("")) {
                if (!courseCardClass.getBgImg().contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                    Glide.with(this).load(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/mpower/" + courseCardClass.getBgImg()).into(feed_card_background_img);
                } else {
                    Glide.with(this).load(courseCardClass.getBgImg()).into(feed_card_background_img);
                }
                feed_card_background_img.setVisibility(VISIBLE);
            } else {
                feed_card_background_img.setVisibility(GONE);
            }

            if (courseCardClass != null && courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getDisplayText() != null && courseCardClass.getReadMoreData().getData() != null) {
                if (!courseCardClass.getReadMoreData().getData().isEmpty() && courseCardClass.getReadMoreData().getType() != null) {
                    file_attach_lay.setVisibility(VISIBLE);

                    if (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("URL_LINK")) {
                        Drawable linkDrawable = com.oustme.oustsdk.tools.OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_weblink));
                        file_attach_image.setImageDrawable(linkDrawable);
                        file_attach_text.setText(courseCardClass.getReadMoreData().getDisplayText());
                        file_attach_lay.setOnClickListener(view -> {
                            Intent redirectScreen = new Intent(ImageFeedDetailScreen.this, RedirectWebView.class);
                            redirectScreen.putExtra("url", courseCardClass.getReadMoreData().getData());
                            redirectScreen.putExtra("feed_title", feed.getHeader());
                            startActivity(redirectScreen);
                        });
                    } else {
                        String fileName = CLOUD_FRONT_BASE_PATH + "readmore/file/" + courseCardClass.getReadMoreData().getData();
                        file_attach_text.setText(courseCardClass.getReadMoreData().getDisplayText());
                        file_attach_lay.setOnClickListener(v -> {
                            if (fileName.contains(".pdf")) {
                                if (com.oustme.oustsdk.tools.OustSdkTools.checkInternetStatus()) {
                                    branding_mani_layout.setVisibility(VISIBLE);
                                    downloadManager(fileName, "feed_" + courseCardClass.getReadMoreData().getData());
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
                if (courseCardClass != null && courseCardClass.getCardTitle() != null && !courseCardClass.getCardTitle().isEmpty()) {
                    Spanned feedHeader = Html.fromHtml(courseCardClass.getCardTitle().trim());
                    feed_title_full.setText(feedHeader);
                    screen_name.setText(feedHeader);
                    feed_title_full.setVisibility(View.VISIBLE);
                } else {
                    if (feed.getHeader() != null && !feed.getHeader().trim().isEmpty()) {
                        Spanned feedHeader = Html.fromHtml(feed.getHeader().trim());
                        feed_title_full.setText(feedHeader);
                        screen_name.setText(feedHeader);
                        feed_title_full.setVisibility(View.VISIBLE);
                    }
                }
            }

            String mUrl = feed.getButton().getBtnActionURI();
            if ((mUrl != null) && (!mUrl.isEmpty())) {
                if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                    mUrl = "http://" + mUrl;
                }

                feed_read_more.setVisibility(View.VISIBLE);
                feed_link.setVisibility(View.GONE);
                feed_link_div.setVisibility(View.GONE);
            }

            final String feedLink = mUrl;

            feed_read_more.setOnClickListener(v -> {
                if ((feedLink != null) && (!feedLink.isEmpty())) {

                    Intent redirectScreen = new Intent(ImageFeedDetailScreen.this, RedirectWebView.class);
                    redirectScreen.putExtra("url", feedLink);
                    redirectScreen.putExtra("feed_title", feed.getHeader());
                    startActivity(redirectScreen);
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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
            }

            String feedDate = OustSdkTools.milliToDate("" + timeStamp);
            if (!feedDate.isEmpty()) {
                feed_date.setVisibility(View.VISIBLE);
                feed_date.setText(feedDate);
            }
            if (feed.getFeedExpiry() != null && !feed.getFeedExpiry().isEmpty()) {
                String feedDeadLine = OustSdkTools.milliToDate("" + feed.getFeedExpiry());
                if (feed.getFeedExpiry() != null && !feedDeadLine.isEmpty()) {
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
            if (courseCardClass != null && courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() != 0) {
                for (CourseCardMedia cardMedia : courseCardClass.getCardMedia()) {
                    if ((cardMedia != null) && (cardMedia.getMediaType() != null)) {
                        switch (cardMedia.getMediaType()) {
                            case "GIF":
                                feed_card_image.setVisibility(View.GONE);
                                feed_card_image_gif.setVisibility(View.VISIBLE);
                                setGifImage(cardMedia.getData());
                                favCardDetails.setImageUrl(cardMedia.getData());
                                favCardDetails.setMediaType(cardMedia.getMediaType());
                                break;
                            case "IMAGE":
                                feed_card_image.setVisibility(View.VISIBLE);
                                feed_card_image_gif.setVisibility(View.GONE);
                                setImage(cardMedia.getData());
                                favCardDetails.setImageUrl(cardMedia.getData());
                                favCardDetails.setMediaType(cardMedia.getMediaType());
                                break;
                            case "AUDIO":
                                if (cardMedia.getData() != null && !cardMedia.getData().isEmpty()) {
                                    cardAudioFile = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/audio/" + cardMedia.getData();
                                }
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
            }

            if ((cardAudioFile != null) && (!cardAudioFile.isEmpty())) {
                isAudioEnable = true;
                isMute = false;
                prepareExoPlayer(cardAudioFile);
            } else {
                isAudioEnable = false;
                isMute = true;
            }

            if (feed.getButton().getBtnText() != null && !feed.getButton().getBtnText().isEmpty()) {
                feed_action_name.setText(OustSdkTools.returnValidString(feed.getButton().getBtnText()));
            }

            feed_share_lay.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                    return;
                }
                isFeedChange = true;
                mLastTimeClicked = SystemClock.elapsedRealtime();
                feed_share_lay.setClickable(false);
                shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                setUserShareCount(feed.getFeedId(), feed);
                if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
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


            //  feed_comment_lay.setOnClickListener(v -> feedComment(feed));
            feed_like_lay.setOnClickListener(v -> feedLike(feed));

            if (courseCardClass != null) {
                if (courseCardClass.getMandatoryViewTime() != 0) {
                    mandatory_timer_progress.setMax((int) (courseCardClass.getMandatoryViewTime() * 1000));
                    mandatoryCountDown = new MandatoryCountDown((courseCardClass.getMandatoryViewTime() * 1000) + 1000, 1000);
                    mandatoryCountDown.start();
                } else {
//                    feedRewardUpdate(feed);
                }
            }
        }

        if (courseCardClass != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                other_lay.setVisibility(GONE);
                toolbar.setVisibility(GONE);
                feed_read_more.setVisibility(GONE);
            }

            String mUrl = feed.getButton().getBtnActionURI();
            if (mUrl == null || mUrl.isEmpty()) {
                feed_read_more.setVisibility(GONE);
            }
        }
        feed_comment_lay.setOnClickListener(v -> feedComment(feed));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_question_menu, menu);

        itemAudio = menu.findItem(R.id.action_audio);
        Drawable audioDrawable;
        if (isMute) {
            audioDrawable = getResources().getDrawable(R.drawable.ic_audiooff);
        } else {
            audioDrawable = getResources().getDrawable(R.drawable.ic_audio_on);
        }
        itemAudio.setIcon(OustResourceUtils.setDefaultDrawableColor(audioDrawable));
        itemAudio.setVisible(isAudioEnable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_audio) {
            handleAudio();
        }
        return super.onOptionsItemSelected(item);
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
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void feedComment(DTOUserFeedDetails.FeedDetails feed) {
        updateComment = true;
        Dialog dialog = new Dialog(ImageFeedDetailScreen.this, R.style.DialogTheme);
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
                                        alertCommentData.setAddedOnDate((long) commentsDataMap.get("addedOnDate"));
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

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(ImageFeedDetailScreen.this, commentsList, activeUser);
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

                                    Intent intent = new Intent(ImageFeedDetailScreen.this, FeedUpdatingServices.class);
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

            MediaPlayer player = MediaPlayer.create(ImageFeedDetailScreen.this, R.raw.thud_sound);
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

    public class MandatoryCountDown extends CountDownTimer {

        public MandatoryCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            try {
                toolbar.setVisibility(GONE);
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
                toolbar.setVisibility(VISIBLE);
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
                intent.putExtra("feedId", (int) newFeed.getFeedId());
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
            request.setDestinationInExternalFilesDir(ImageFeedDetailScreen.this, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
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
                                        com.oustme.oustsdk.tools.OustSdkTools.copyFile(sourceFile, destinationFile);
                                        try {
                                            branding_mani_layout.setVisibility(GONE);
                                            Uri outputFileUri = FileProvider.getUriForFile(ImageFeedDetailScreen.this, OustSdkApplication.getContext().getPackageName() + ".provider", destinationFile);
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
                                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    case DownloadManager.ERROR_CANNOT_RESUME:
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

    private void setGifImage(String fileName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Glide.with(this).load(file).into(feed_card_image_gif);

            } else {
                String url;
                if (fileName.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                    url = fileName;
                } else if (fileName.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                    url = fileName;
                } else if (fileName.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                    url = fileName;
                } else {
                    url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/media/gif/" + fileName;
                }
                Log.d(TAG, "setFullImage: url:" + url);
                finalImageUrl = url;
                Glide.with(this).load(url).into(feed_card_image_gif);
            }

            ViewGroup.LayoutParams layoutParams = feed_card_image_gif.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (courseCardClass.getClCode() != null) {
                    if ((courseCardClass.getClCode().equals(LearningCardType.CL_I_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        scrHeight = metrics.heightPixels * 0.40;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        scrHeight = metrics.heightPixels * 0.65;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        other_lay.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    feed_card_image_gif.setLayoutParams(layoutParams);
                }
            } else {
                double srcHeight = metrics.heightPixels;
                if (courseCardClass.getClCode() != null) {
                    if ((courseCardClass.getClCode().equals(LearningCardType.CL_I_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.GONE);
//                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    feed_card_image_gif.setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }

    }

    private void setImage(String fileName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Glide.with(this)
                        .asBitmap()
                        .load(file).into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    feed_card_image.setImageBitmap(resource);
                                    Log.e("CardFragment", "Card Image loaded url " + resource + " file " + file);
                                    photoViewAttacher = new PhotoViewAttacher(feed_card_image);

                                    photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                                        Log.e(TAG, "setImageOnPhoto0: " + uri);
                                        gifZoomPopup(feed_card_image.getDrawable());
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                                    Log.e("CardFragment", "Card Image exception url " + e.getMessage());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                feed_card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + uri);
                    gifZoomPopup(feed_card_image.getDrawable());
                });
            } else {
                String url;
                if (fileName.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                    url = fileName;
                } else if (fileName.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS)) {
                    url = fileName;
                } else if (fileName.contains(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                    url = fileName;
                } else {
                    url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS + "course/media/image/" + fileName;
                }
                Log.d(TAG, "setFullImage: url:" + url);
                finalImageUrl = url;
                Glide.with(this)
                        .asBitmap()
                        .load(url).into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    feed_card_image.setImageBitmap(resource);
                                    Log.e("CardFragment", "Card Image loaded file " + resource + " file " + url);
                                    photoViewAttacher = new PhotoViewAttacher(feed_card_image);

                                    photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                                        Log.e(TAG, "setImageOnPhoto: " + url);
                                        gifZoomPopup(feed_card_image.getDrawable());
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                                    Log.e("CardFragment", "Card Image exception file " + e.getMessage());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                feed_card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + url);
                    gifZoomPopup(feed_card_image.getDrawable());
                });
            }

            ViewGroup.LayoutParams layoutParams = feed_card_image.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (courseCardClass.getClCode() != null) {
                    if ((courseCardClass.getClCode().equals(LearningCardType.CL_I_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        scrHeight = metrics.heightPixels * 0.40;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        scrHeight = metrics.heightPixels * 0.65;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        other_lay.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    feed_card_image.setLayoutParams(layoutParams);
                }
            } else {
                double srcHeight = metrics.heightPixels;
                if (courseCardClass.getClCode() != null) {
                    if ((courseCardClass.getClCode().equals(LearningCardType.CL_I_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.VISIBLE);
//                        fullImageText.setVisibility(View.GONE);
                    } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        other_lay.setVisibility(View.GONE);
//                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    feed_card_image.setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void gifZoomPopup(Drawable file) {
        try {
            @SuppressLint("InflateParams")
            View popUpView = this.getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = createPopUpForCardImage(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(file);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(gifImageView);

            imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout zoomLayout = popUpView.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            zoomLayout.setVisibility(View.VISIBLE);
            imageCloseButton.setOnClickListener(view -> {
                ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                scaleDownX1.setDuration(350);
                scaleDownY1.setDuration(350);
                scaleDownX1.setInterpolator(new DecelerateInterpolator());
                scaleDownY1.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown1 = new AnimatorSet();
                scaleDown1.play(scaleDownX1).with(scaleDownY1);
                scaleDown1.start();
                scaleDown1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ObjectAnimator scaleDownX12 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY12 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                    scaleDownX12.setDuration(350);
                    scaleDownY12.setDuration(350);
                    scaleDownX12.setInterpolator(new DecelerateInterpolator());
                    scaleDownY12.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown12 = new AnimatorSet();
                    scaleDown12.play(scaleDownX12).with(scaleDownY12);
                    scaleDown12.start();
                    scaleDown12.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private PopupWindow createPopUpForCardImage(View popUpView) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scrHeight = metrics.heightPixels;
        int height = (int) scrHeight;
        final PopupWindow popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, height, false); //Creation of popup
        try {
            popupWindow.showAtLocation(popUpView, Gravity.TOP, 0, 0);    // Displaying popup
            popUpView.setFocusableInTouchMode(false);
            popUpView.requestFocus();
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
        return popupWindow;
    }

    private void openAttachment(String fileName, String header) {
        try {
            Intent redirectScreen = new Intent(ImageFeedDetailScreen.this, RedirectWebView.class);
            redirectScreen.putExtra("url", fileName);
            redirectScreen.putExtra("feed_title", header);
            startActivity(redirectScreen);
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
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

    private void prepareExoPlayer(String audioFilePath) {
        try {
            Log.d(TAG, "prepareExoPlayerFromUri: ");
            resetAudioPlayer();
           /* File audioFile = new File(OustSdkApplication.getContext().getFilesDir(), audioFilePath);
            if (audioFile.exists()) {*/
            mediaPlayerForAudio = new MediaPlayer();
            mediaPlayerForAudio.reset();
//                FileInputStream fis = new FileInputStream(audioFilePath);
            mediaPlayerForAudio.setDataSource(audioFilePath);
            mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayerForAudio.prepare();
            mediaPlayerForAudio.start();
            invalidateOptionsMenu();
            mediaPlayerForAudio.setOnCompletionListener(mediaPlayer -> {
                Log.e(TAG, "onCompletion: ");
                isAudioPlayTrackComplete = true;
            });
//            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void pauseAudioPlayer() {
        try {
            if (mediaPlayerForAudio != null) {
                if (mediaPlayerForAudio.isPlaying()) {
                    pauseAudioPlayer = true;
                    mediaPlayerForAudio.pause();
                    isMute = true;
                } else {
                    if (!isAudioPlayTrackComplete) {
                        mediaPlayerForAudio.start();
                        isMute = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAudioPlayer() {
        try {
            if (mediaPlayerForAudio != null) {
                mediaPlayerForAudio.stop();
                mediaPlayerForAudio.release();
                mediaPlayerForAudio = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    private void handleAudio() {
        try {
            if (mediaPlayerForAudio != null) {
                if (mediaPlayerForAudio.isPlaying()) {
                    mediaPlayerForAudio.pause();
                    isMute = true;
                } else {
                    mediaPlayerForAudio.start();
                    isMute = false;
                }
            }
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (pauseAudioPlayer) {
                if (mediaPlayerForAudio != null) {
                    if (!mediaPlayerForAudio.isPlaying()) {
                        pauseAudioPlayer();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mediaPlayerForAudio != null) {
                if (mediaPlayerForAudio.isPlaying()) {
                    pauseAudioPlayer = true;
                    mediaPlayerForAudio.pause();
                    isMute = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mediaPlayerForAudio != null) {
                if (mediaPlayerForAudio.isPlaying()) {
                    pauseAudioPlayer = true;
                    mediaPlayerForAudio.pause();
                    isMute = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.oustme.oustsdk.tools.OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        backScreen();
    }

    private void backScreen() {
        if (progress == 0) {
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
    }
}