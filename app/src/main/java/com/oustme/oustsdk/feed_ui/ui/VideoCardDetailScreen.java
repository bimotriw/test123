package com.oustme.oustsdk.feed_ui.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT;
import static com.oustme.oustsdk.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.tools.OustSdkTools.stringToURL;
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
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ThreadPoolProvider;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.CacheDataSourceFactory;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VideoCardDetailScreen extends AppCompatActivity {

    private static final String TAG = "VideoCardDetailScreen";

    Toolbar toolbar_root;
    LinearLayout other_lay;
    ImageView feed_image_thumbnail;
    ImageView volume_control;
    ImageView pause_control;
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
    WebView feed_description_webView;
    TextView feed_link;
    View feed_link_div;
    LinearLayout feed_read_more;
    TextView feed_action_name;
    LinearLayout timer_layout;
    ProgressBar mandatory_timer_progress;
    TextView mandatory_timer_text;
    LinearLayout feed_action_lay;
    RelativeLayout file_attach_lay;
    ImageView file_attach_image;
    TextView file_attach_text;
    LinearLayout background_img;
    ImageView video_card_background_img;
    CircularProgressIndicator video_detail_progress_bar;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    private ImageButton exoplayer_pause;
    private ImageButton imageButtonFwd;
    private ImageView zoomImageView;
    private DefaultTimeBar exo_progress;
    private StyledPlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;
    private boolean updateComment;
    private boolean isFeedChange, isLikeClicked;
    private boolean countDownStarted = false;
    MandatoryCountDown mandatoryCountDown;
    int progress;
    private int color;
    private int bgColor;
    private boolean fastForwardMedia;
    private long mLastTimeClicked = 0;
    private String feedLink;
    private String videoFileName;
    private int height_toolbar;
    private int height_timeBar;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;

    private enum VolumeState {ON, OFF}

    private VolumeState volumeState;

    DTOUserFeedDetails.FeedDetails feed;
    boolean isFeedComment, isFeedLikeable;
    CourseCardClass courseCardClass;
    String courseCardClassString;
    boolean isFullScreen;
    private boolean feedAutoCloseAfterCompletion;
    String shareContent = " Download Oustsdk...";
    ActiveUser activeUser;
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private final int mPlayerLength = 0;
    Dialog dialog;
    String mUrl;
    long timeStamp;
    boolean isVideoPaused = false;


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        OustSdkTools.setLocale(VideoCardDetailScreen.this);
        setContentView(R.layout.activity_video_card_detail_screen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            zoomImageView = findViewById(R.id.video_zoom);
            toolbar_root = findViewById(R.id.toolbar_root);
            other_lay = findViewById(R.id.other_lay);
            feed_image_thumbnail = findViewById(R.id.feed_image_thumbnail);
            volume_control = findViewById(R.id.volume_control);
            pause_control = findViewById(R.id.pause_control);
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
            feed_like_image = findViewById(R.id.feed_like_image);
            feed_like = findViewById(R.id.feed_like);
            feed_comment_lay = findViewById(R.id.feed_comment_lay);
            feed_comment_image = findViewById(R.id.feed_comment_image);
            feed_comment = findViewById(R.id.feed_comment);
            feed_share_lay = findViewById(R.id.feed_share_lay);
            feed_share_image = findViewById(R.id.feed_share_image);
            feed_share = findViewById(R.id.feed_share);
            feed_description = findViewById(R.id.feed_description);
            feed_description_webView = findViewById(R.id.feed_description_webView);
            feed_link = findViewById(R.id.feed_link);
            feed_link_div = findViewById(R.id.feed_link_div);
            feed_read_more = findViewById(R.id.feed_read_more);
            feed_action_name = findViewById(R.id.feed_action_name);
            timer_layout = findViewById(R.id.timer_layout);
            mandatory_timer_progress = findViewById(R.id.mandatory_timer_progress);
            mandatory_timer_text = findViewById(R.id.mandatory_timer_text);
            feed_action_lay = findViewById(R.id.feed_action_lay);
            file_attach_lay = findViewById(R.id.file_attach_lay);
            file_attach_image = findViewById(R.id.file_attach_image);
            file_attach_text = findViewById(R.id.file_attach_text);
            background_img = findViewById(R.id.main_layout);
            video_card_background_img = findViewById(R.id.video_card_background_img);
            video_detail_progress_bar = findViewById(R.id.video_detail_progress_bar);


            String tenantId = OustPreferences.get("tanentid");
            getColors();
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

            height_toolbar = toolbar_root.getLayoutParams().height;
            height_timeBar = timer_layout.getLayoutParams().height;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            feedAutoCloseAfterCompletion = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.FEED_AUTO_CLOSE_AFTER_COMPLETION);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            video_detail_progress_bar.setIndicatorColor(color);
            video_detail_progress_bar.setTrackColor(getResources().getColor(R.color.gray));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Bundle feedBundle = getIntent().getExtras();
        if (feedBundle != null) {
            feed = feedBundle.getParcelable("Feed");
            courseCardClassString = feedBundle.getString("CardData");
            isFeedComment = feedBundle.getBoolean("FeedComment");
            isFeedLikeable = feedBundle.getBoolean("isFeedLikeable");
            timeStamp = feedBundle.getLong("timeStamp", 0);

            //courseCardClass = (CourseCardClass) feedBundle.getSerializable("CardData");
            activeUser = (ActiveUser) feedBundle.getSerializable("ActiveUser");
        }

        if (activeUser == null | Objects.requireNonNull(activeUser).getStudentid() == null) {
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            if (activeUser == null || activeUser.getStudentid() == null) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
        }
        try {
            if (courseCardClassString != null && !courseCardClassString.isEmpty()) {
                courseCardClass = new Gson().fromJson(courseCardClassString, CourseCardClass.class);
                if (courseCardClass.isPotraitModeVideo() || isFeedComment) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestManager = initGlide();

        try {
            setIconColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        toolbar_close.setOnClickListener(v -> {
            // VideoCardDetailScreen.this.finish();
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.stop();
            }
            removeVideoView(videoSurfaceView);
            backScreen();
        });

        toolbar_close_icon.setOnClickListener(v -> {
            //  VideoCardDetailScreen.this.finish();
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.stop();
            }
            removeVideoView(videoSurfaceView);
            backScreen();
        });


        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);


        LayoutInflater layoutInflater = LayoutInflater.from(VideoCardDetailScreen.this);

        View view = layoutInflater.inflate(R.layout.exo_player_card_view, null);

        videoSurfaceView = (StyledPlayerView) view.findViewById(R.id.video_card_view);

        videoSurfaceView.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics())));
        media_container.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics())));
        videoSurfaceView.setBackgroundColor(Color.TRANSPARENT);
        videoSurfaceView.setShowNextButton(false);
        videoSurfaceView.setShowFastForwardButton(false);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        imageButtonFwd = videoSurfaceView.findViewById(R.id.exo_forward);
        ImageButton imageButtonBackWord = videoSurfaceView.findViewById(R.id.exo_reverse);
        ImageView imgFullScreen = videoSurfaceView.findViewById(R.id.exo_full_screen);
        exoplayer_pause = videoSurfaceView.findViewById(R.id.exo_pause);
        exo_progress = videoSurfaceView.findViewById(R.id.exo_progress);

        imageButtonBackWord.setVisibility(View.VISIBLE);

        if (fastForwardMedia) {
            imageButtonFwd.setVisibility(View.VISIBLE);
        } else {
            imageButtonFwd.setVisibility(View.GONE);
        }

        imageButtonBackWord.setOnClickListener(v -> {
            if (videoPlayer != null) {
                if (videoPlayer.getCurrentPosition() - 10000 > 0) {
                    videoPlayer.seekTo(videoPlayer.getCurrentPosition() - 10000);
                } else {
                    videoPlayer.seekTo(0);
                }
            }
        });

        exo_progress.setOnTouchListener((view2, motionEvent) -> {
            if (fastForwardMedia) {
                imageButtonFwd.setVisibility(View.VISIBLE);
                return false;
            } else {
                imageButtonFwd.setVisibility(View.GONE);
                return true;
            }
        });


        imageButtonFwd.setOnClickListener(v -> {
            if (videoPlayer != null && fastForwardMedia) {
                Log.d("exoplayer", "" + videoPlayer.getDuration());
                videoPlayer.seekTo(Math.min(videoPlayer.getCurrentPosition() + 10000, videoPlayer.getDuration()));
            }
        });

        exoplayer_pause.setOnClickListener(v -> {
            if (videoPlayer.isPlaying()) {
                videoPlayer.setPlayWhenReady(false);
                exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
            } else {
                videoPlayer.setPlayWhenReady(true);
                exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
            }
        });

        zoomImageView.setOnClickListener(view1 -> {
            isFullScreen = !isFullScreen;
            if (isFullScreen) {
                if (courseCardClass.isPotraitModeVideo()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                isFullScreen = true;
                setPortraitVideoRatioFullScreen();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setLandscapeVideoRation();
            }
            try {
                mUrl = feed.getButton().getBtnActionURI();
                if ((mUrl != null) && (!mUrl.isEmpty())) {
                    if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                        mUrl = "http://" + mUrl;
                    }
                    feed_read_more.setVisibility(View.VISIBLE);
                } else {
                    feed_read_more.setVisibility(GONE);
                }
                feed_link.setVisibility(View.GONE);
                feed_link_div.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        imgFullScreen.setVisibility(GONE);
        imgFullScreen.setOnClickListener(v -> {
            isFullScreen = !isFullScreen;
            if (isFullScreen) {
                setPortraitVideoRatioFullScreen();
            } else {
                setLandscapeVideoRation();
            }
        });

        runOnUiThread(() -> {
            videoPlayer = new SimpleExoPlayer.Builder(VideoCardDetailScreen.this).build();
            videoSurfaceView.setUseController(true);
            videoSurfaceView.setPlayer(videoPlayer);

            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(GONE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(VISIBLE);
                            }
                            break;
                        case Player.STATE_ENDED:
                            Log.d(TAG, "onPlayerStateChanged: Video ended.");
                            //  videoPlayer.seekTo(0);
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(GONE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                            }
                            if (feed_image_thumbnail != null) {
                                feed_image_thumbnail.setVisibility(VISIBLE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
                            exo_progress.setEnabled(true);
                            feedRewardUpdate(feed);

                            if (feedAutoCloseAfterCompletion) {
                                backScreen();
                            } else {
                                if (mandatoryCountDown != null) {
                                    mandatoryCountDown.cancel();
                                }
                                progress = 0;
                                mandatoryCountDown = null;
                                toolbar_root.setVisibility(VISIBLE);
                                timer_layout.setVisibility(GONE);
                                if (isFullScreen) {
                                    isFullScreen = false;
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    setLandscapeVideoRation();
                                }
                            }
                            break;
                        case Player.STATE_IDLE:
                            Log.d(TAG, "onPlayerStateChanged: Video idle.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                                play_button.setBackground(getDrawable(R.drawable.ic_exo_icon_play));
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(GONE);
                            }
                            if (feed_image_thumbnail != null) {
                                feed_image_thumbnail.setVisibility(VISIBLE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
                            break;

                        case Player.STATE_READY:
                            Log.e(TAG, "onPlayerStateChanged: Ready to play..");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(GONE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(GONE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(VISIBLE);
                                exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                            }
                            if (!isVideoViewAdded) {
                                addVideoView();
                            }
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        if (videoPlayer != null) {
//                        videoPlayer.stop();
                            videoPlayer.setPlayWhenReady(true);
                            videoPlayer.getPlaybackState();
                        }
                        if (video_detail_progress_bar != null) {
                            video_detail_progress_bar.setVisibility(VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });
        });

        String imageUrl1 = null;
        if (courseCardClass != null && courseCardClass.getBgImg() != null && !courseCardClass.getBgImg().isEmpty() && !courseCardClass.getBgImg().equalsIgnoreCase("")) {
            imageUrl1 = "course/mpower/" + courseCardClass.getBgImg();
            if (imageUrl1.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                imageUrl1 = imageUrl1.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
            }
            imageUrl1 = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + imageUrl1;
            Glide.with(this).load(imageUrl1).into(video_card_background_img);
            video_card_background_img.setVisibility(VISIBLE);
            background_img.setBackgroundColor(0);
        } else {
            background_img.setBackgroundColor(Color.parseColor("#ffffffff"));
            video_card_background_img.setVisibility(GONE);
        }

        if (courseCardClass != null) {
            playVideo();
            if (courseCardClass.getMandatoryViewTime() != 0) {
                mandatory_timer_progress.setMax((int) (courseCardClass.getMandatoryViewTime() * 1000));
                mandatoryCountDown = new MandatoryCountDown((courseCardClass.getMandatoryViewTime() * 1000) + 1000, 1000);
            }
            if (courseCardClass.isPotraitModeVideo()) {
                media_container.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
                media_container.requestLayout();
                feed_read_more.setVisibility(GONE);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                other_lay.setVisibility(GONE);
                toolbar_root.setVisibility(GONE);
                feed_read_more.setVisibility(GONE);
                media_container.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                media_container.requestLayout();
            }

            String mUrl = feed.getButton().getBtnActionURI();
            if (mUrl == null || mUrl.isEmpty()) {
                feed_read_more.setVisibility(GONE);
            }
        }


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

            if (courseCardClass != null && courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getDisplayText() != null && courseCardClass.getReadMoreData().getData() != null) {
                if (!courseCardClass.getReadMoreData().getData().isEmpty() && courseCardClass.getReadMoreData().getType() != null) {
                    file_attach_lay.setVisibility(VISIBLE);

                    if (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("URL_LINK")) {
                        Drawable linkDrawable = OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_weblink));
                        file_attach_image.setImageDrawable(linkDrawable);
                        file_attach_text.setText(courseCardClass.getReadMoreData().getDisplayText());
                        file_attach_lay.setOnClickListener(view3 -> {
                            Intent redirectScreen = new Intent(VideoCardDetailScreen.this, RedirectWebView.class);
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

//            feed.setLikeble(isFeedLikeable);
            setFeedLike(feed, false);

            Log.i("Oncreate Feed GFeedDS ", new Gson().toJson(feed));

            if (feed != null) {
                if (courseCardClass != null && courseCardClass.getCardTitle() != null && !courseCardClass.getCardTitle().isEmpty()) {
                    Spanned feedHeader = Html.fromHtml(courseCardClass.getCardTitle().trim());
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

            if (courseCardClass != null && courseCardClass.getContent() != null && !courseCardClass.getContent().isEmpty()) {
                if (courseCardClass.getContent().contains("<li>") || courseCardClass.getContent().contains("</li>") ||
                        courseCardClass.getContent().contains("<ol>") || courseCardClass.getContent().contains("</ol>") ||
                        courseCardClass.getContent().contains("<p>") || courseCardClass.getContent().contains("</p>")) {
                    feed_description_webView.setVisibility(View.VISIBLE);
                    feed_description.setVisibility(View.GONE);
                    feed_description_webView.setBackgroundColor(Color.TRANSPARENT);
                    String text = OustSdkTools.getDescriptionHtmlFormat(courseCardClass.getContent());
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
            } else if (feed != null && feed.getContent() != null && !feed.getContent().trim().isEmpty()) {
                if (feed.getContent().contains("<li>") || feed.getContent().contains("</li>") ||
                        feed.getContent().contains("<ol>") || feed.getContent().contains("</ol>") ||
                        feed.getContent().contains("<p>") || feed.getContent().contains("</p>")) {
                    feed_description_webView.setVisibility(View.VISIBLE);
                    feed_description.setVisibility(View.GONE);
                    feed_description_webView.setBackgroundColor(Color.TRANSPARENT);
                    String text = OustSdkTools.getDescriptionHtmlFormat(feed.getContent());
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


            mUrl = feed.getButton().getBtnActionURI();
            if ((mUrl != null) && (!mUrl.isEmpty())) {
                if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                    mUrl = "http://" + mUrl;
                }
                feed_read_more.setVisibility(View.VISIBLE);
            } else {
                feed_read_more.setVisibility(GONE);
            }
            feed_link.setVisibility(View.GONE);
            feed_link_div.setVisibility(View.GONE);

            feedLink = mUrl;

            if (feed.getButton().getBtnText() != null && !feed.getButton().getBtnText().isEmpty()) {
                feed_action_name.setText(OustSdkTools.returnValidString(feed.getButton().getBtnText()));
            }

            feed_read_more.setOnClickListener(v -> {
                if ((feedLink != null) && (!feedLink.isEmpty())) {
                    Intent redirectScreen = new Intent(VideoCardDetailScreen.this, RedirectWebView.class);
                    redirectScreen.putExtra("url", feedLink);
                    redirectScreen.putExtra("feed_title", feed.getHeader());
                    startActivity(redirectScreen);
                }
            });

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
            String imageUrl = "";
            if (feed != null && feed.getCardInfo().getCardMediaList() != null && feed.getCardInfo().getCardMediaList().size() != 0) {
                if (feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail() != null && !feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail().isEmpty()) {
                    imageUrl = feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail();
                } else {
                    if (courseCardClass.getBgImg() != null && !courseCardClass.getBgImg().isEmpty()) {
                        imageUrl = "course/mpower/" + courseCardClass.getBgImg();
                        if (imageUrl.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                            imageUrl = imageUrl.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                        }
                        imageUrl = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + imageUrl;
                        requestManager.load(imageUrl).into(feed_image_thumbnail);
                    } else {
                        if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                            imageUrl = feed.getImageUrl();
                        }
                    }
                }
            } else if (courseCardClass.getBgImg() != null && !courseCardClass.getBgImg().isEmpty()) {
                imageUrl = "course/mpower/" + courseCardClass.getBgImg();
                if (imageUrl.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                    imageUrl = imageUrl.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                }
                imageUrl = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + imageUrl;
                requestManager.load(imageUrl).into(feed_image_thumbnail);
            } else {
                if (feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail() != null && !feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail().isEmpty()) {
                    imageUrl = feed.getCardInfo().getCardMediaList().get(0).getMediaThumbnail();
                } else {
                    if (courseCardClass.getBgImg() != null && !courseCardClass.getBgImg().isEmpty()) {
                        imageUrl = "course/mpower/" + courseCardClass.getBgImg();
                        if (imageUrl.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                            imageUrl = imageUrl.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                        }
                        imageUrl = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + imageUrl;
                        requestManager.load(imageUrl).into(feed_image_thumbnail);
                    } else {
                        if (feed.getImageUrl() != null && !feed.getImageUrl().isEmpty()) {
                            imageUrl = feed.getImageUrl();
                        }
                    }
                }
            }

            feed_like_lay.setOnClickListener(v -> feedLike(feed));

            final String finalImageUrl = imageUrl;
            feed_share_lay.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastTimeClicked < 2000) {
                    return;
                }
                mLastTimeClicked = SystemClock.elapsedRealtime();
                feed_share_lay.setClickable(false);
                shareContent = feed.getHeader() + "\nHi There, " + " ....Lets get more feed on Oust.The new way to study smarter .. https://bnc.oustme.com/rVzVFAzrw5";
                isFeedChange = true;
                URL testFinalImageUrl;
                if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
                    testFinalImageUrl = stringToURL("" + finalImageUrl);
                } else {
                    testFinalImageUrl = null;
                }
                if (testFinalImageUrl != null) {
                    ThreadPoolProvider.getInstance().getFeedShareSingleThreadExecutor().execute(() -> {
                        HttpURLConnection connection = null;
                        try {
                            connection = (HttpURLConnection) testFinalImageUrl.openConnection();
                            connection.connect();
                            InputStream inputStream = connection.getInputStream();
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                            Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);
                            Uri bmpUri = null;
                            if (bm != null) {
                                try {
                                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bm, "IMG_" + System.currentTimeMillis(), null);
                                    if (path != null) {
                                        bmpUri = Uri.parse(path);
                                    }
                                    if (bmpUri != null) {
                                        //posting to UI thread
                                        Uri finalBmpUri = bmpUri;
                                        ThreadPoolProvider.getInstance().getHandler().post(() -> {
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, finalBmpUri);
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
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
                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
                    });
                } else {
                    Intent shareIntent;
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


        feed_comment_lay.setOnClickListener(v -> feedComment(feed));
        play_button.setOnClickListener(v -> {
            if (videoPlayer != null) {
                videoPlayer.seekTo(0);
                videoPlayer.setPlayWhenReady(true);
                videoPlayer.getPlaybackState();
            }
        });

        if (isFeedComment) {
            videoPlayer.setPlayWhenReady(false);
            exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
        } else {
            if (courseCardClass.isPotraitModeVideo()) {
                setPortraitVideoRatioFullScreen();
            } else {
                setLandscapeVideoRation();
            }
        }
    }

    private void setPortraitVideoRatioFullScreen() {
        isFullScreen = true;
        try {
            other_lay.setVisibility(GONE);
            feed_read_more.setVisibility(GONE);
            if (timer_layout.getVisibility() == VISIBLE) {
                toolbar_root.setVisibility(GONE);
            } else {
                toolbar_root.setVisibility(VISIBLE);
            }
            feed_detail_lay_scroll.setBackgroundColor(Color.parseColor("#333333"));

            if (videoSurfaceView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurfaceView.getLayoutParams();
                float h = metrics.heightPixels;
                params.width = scrWidth;
                if (timer_layout.getVisibility() == VISIBLE) {
                    params.height = (int) h - height_timeBar - height_toolbar;
                } else {
                    params.height = (int) h - height_toolbar - height_timeBar;
                }
                media_container.setLayoutParams(params);
                videoSurfaceView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLandscapeVideoRation() {
        isFullScreen = false;
        try {
            if (timer_layout.getVisibility() == VISIBLE) {
                toolbar_root.setVisibility(GONE);
            } else {
                toolbar_root.setVisibility(VISIBLE);
            }
            other_lay.setVisibility(VISIBLE);
            if (feedLink != null && !feedLink.isEmpty()) {
                feed_read_more.setVisibility(VISIBLE);
            }
            feed_detail_lay_scroll.setBackgroundColor(Color.TRANSPARENT);
            if (videoSurfaceView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurfaceView.getLayoutParams();
                float h = metrics.heightPixels;
                params.width = scrWidth;
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
                media_container.setLayoutParams(params);
                videoSurfaceView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setVolumeControl(VolumeState state) {
        try {
            volumeState = state;
            volume_control.setVisibility(VISIBLE);
            if (videoPlayer != null) {
                if (state == VolumeState.OFF) {
                    videoPlayer.setVolume(0f);
                    animateVolumeControl();
                } else if (state == VolumeState.ON) {
                    videoPlayer.setVolume(1f);
                    animateVolumeControl();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addVideoView() {
        try {
            media_container.addView(videoSurfaceView);
            isVideoViewAdded = true;
            videoSurfaceView.requestFocus();
            videoSurfaceView.setVisibility(VISIBLE);
            videoSurfaceView.setAlpha(1);
            feed_image_thumbnail.setVisibility(GONE);
            zoomImageView.bringToFront();
            volume_control.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    // Remove the old player
    private void removeVideoView(StyledPlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
        }
    }

    public void playVideo() {
        setVolumeControl(VolumeState.ON);
        volume_control.setOnClickListener(v -> {
            if (videoPlayer != null) {
                if (volumeState == VolumeState.OFF) {
                    Log.d(TAG, "togglePlaybackState: enabling volume.");
                    setVolumeControl(VolumeState.ON);

                } else if (volumeState == VolumeState.ON) {
                    Log.d(TAG, "togglePlaybackState: disabling volume.");
                    setVolumeControl(VolumeState.OFF);
                }
            }
        });

        Log.i("Feed video ", new Gson().toJson(feed));
        if (courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() != 0) {

            if (courseCardClass.getCardMedia().get(0).getGumletVideoUrl() != null && !courseCardClass.getCardMedia().get(0).getGumletVideoUrl().isEmpty()) {
                videoFileName = courseCardClass.getCardMedia().get(0).getGumletVideoUrl();
            } else {
                videoFileName = courseCardClass.getCardMedia().get(0).getData();
            }
            fastForwardMedia = courseCardClass.getCardMedia().get(0).isFastForwardMedia();
            if (!fastForwardMedia && videoSurfaceView != null) {
                videoSurfaceView.setShowFastForwardButton(false);
                videoSurfaceView.setShowNextButton(false);
            }
            if (fastForwardMedia) {
                imageButtonFwd.setVisibility(View.VISIBLE);
            } else {
                imageButtonFwd.setVisibility(View.GONE);
            }
            if (videoFileName != null) {
                String filename = videoFileName;
                if (videoFileName.contains(".mp4")) {
                    filename = videoFileName.replace(".mp4", "");
                }
                playNormalVideo();
            }
            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(GONE);
                            }
                            if (video_detail_progress_bar != null && video_detail_progress_bar.getVisibility() == GONE) {
                                video_detail_progress_bar.setVisibility(VISIBLE);
                            }
                            break;
                        case Player.STATE_ENDED:
                            Log.d(TAG, "onPlayerStateChanged: Video ended.");
                            //  videoPlayer.seekTo(0);
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(GONE);
                            }
                            if (feed_image_thumbnail != null) {
                                feed_image_thumbnail.setVisibility(VISIBLE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
//                            exo_progress.setEnabled(true);
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    play_button.setBackground(getDrawable(R.drawable.ic_exo_icon_play));
                                }
                            }
                            feedRewardUpdate(feed);

                            if (feedAutoCloseAfterCompletion) {
                                backScreen();
                            } else {
                                if (mandatoryCountDown != null) {
                                    mandatoryCountDown.cancel();
                                }
                                progress = 0;
                                mandatoryCountDown = null;
                                toolbar_root.setVisibility(VISIBLE);
                                timer_layout.setVisibility(GONE);
                                if (isFullScreen) {
                                    isFullScreen = false;
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    setLandscapeVideoRation();
                                }
                            }
                            break;
                        case Player.STATE_IDLE:
                            Log.d(TAG, "onPlayerStateChanged: Video idle.");
                            if (opacity_view != null) {
                                opacity_view.setVisibility(VISIBLE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    play_button.setBackground(getDrawable(R.drawable.ic_exo_icon_play));
                                }
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(GONE);
                            }
                            if (feed_image_thumbnail != null) {
                                feed_image_thumbnail.setVisibility(VISIBLE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
                            break;

                        case Player.STATE_READY:
                            Log.e(TAG, "onPlayerStateChanged: Ready to play..");
                            if (mandatoryCountDown != null && !countDownStarted) {
                                countDownStarted = true;
                                mandatoryCountDown.start();
                            }
                            if (opacity_view != null) {
                                opacity_view.setVisibility(GONE);
                            }
                            if (play_button != null) {
                                play_button.setVisibility(GONE);
                            }
                            if (video_detail_progress_bar != null) {
                                video_detail_progress_bar.setVisibility(GONE);
                            }
                            if (exoplayer_pause != null) {
                                exoplayer_pause.setVisibility(VISIBLE);
                                exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                            }
                            if (!isVideoViewAdded) {
                                addVideoView();
                            }
                            break;
                        default:
                            break;
                    }
                }

                /*@Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        if (videoPlayer != null) {
                            videoPlayer.prepare();
                          *//*  videoPlayer.setPlayWhenReady(true);
                            videoPlayer.getPlaybackState();*//*
                        }
                        if (video_detail_progress_bar != null && video_detail_progress_bar.getVisibility() == GONE) {
                            video_detail_progress_bar.setVisibility(VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }*/
            });

            setVolumeControl(VolumeState.ON);
            volume_control.setOnClickListener(v -> {
                if (videoPlayer != null) {
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
    }

    private void playNormalVideo() {
        try {
            runOnUiThread(() -> {
//                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this);
//              DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(VideoCardDetailScreen.this, Util.getUserAgent(VideoCardDetailScreen.this, "Feeds RecyclerView"));
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(VideoCardDetailScreen.this, new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(VideoCardDetailScreen.this, "Feeds RecyclerView")));
                String path = null;
                if (courseCardClass.getCardMedia().get(0).getGumletVideoUrl() != null && !courseCardClass.getCardMedia().get(0).getGumletVideoUrl().isEmpty()) {
                    path = courseCardClass.getCardMedia().get(0).getGumletVideoUrl();
                } else {
                    path = "course/media/video/" + videoFileName;
                    if (path.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                        path = path.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
                    }
                    path = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + path;
                }
                Uri videoUri = Uri.parse(path);

                Log.e(TAG, "playNormalVideo: Path " + path);
                //"1073741824" means 1GB  -- "6553600" means 50MB
//                CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(this, 1073741824, 6553600);
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                if (videoPlayer != null) {
                    videoPlayer.addMediaSource(videoSource);
                    videoPlayer.setPlayWhenReady(true);
                    videoPlayer.prepare();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
        isFeedChange = true;
        updateFeedViewed(feed);
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
            OustSdkTools.sendSentryException(e);
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

            MediaPlayer player = MediaPlayer.create(VideoCardDetailScreen.this, R.raw.thud_sound);
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
        updateComment = true;
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            isVideoPaused = true;
            if (exoplayer_pause != null) {
                exoplayer_pause.performClick();
            }
        }
        dialog = new Dialog(VideoCardDetailScreen.this, R.style.DialogTheme);
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

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(VideoCardDetailScreen.this, commentsList, activeUser);
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

                                    Intent intent = new Intent(VideoCardDetailScreen.this, FeedUpdatingServices.class);
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
                                total_comments_text.setText(totalComments);
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

                comment_text.setText("");

            }

        });

        dialog.setOnDismissListener(dialog -> {
            try {
                if (videoPlayer != null) {
                    videoPlayer.setPlayWhenReady(true);
                    exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        comment_close.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                if (videoPlayer != null) {
                    videoPlayer.play();
                    videoPlayer.setPlayWhenReady(true);
                    videoPlayer.getPlaybackState();
                    exoplayer_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
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


    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
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
        try {
            if (feedAutoCloseAfterCompletion) {
                backScreen();
            } else {
                if (isFullScreen) {
                    zoomImageView.performClick();
                } else {
                    backScreen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void backScreen() {
        try {
            if (progress == 0) {
                if (videoPlayer != null && videoPlayer.isPlaying()) {
                    videoPlayer.stop();
                    videoPlayer.release();
                    videoPlayer = null;
                }
                removeVideoView(videoSurfaceView);
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
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isFullScreen = true;
                other_lay.setVisibility(GONE);
                toolbar_root.setVisibility(GONE);
                feed_read_more.setVisibility(GONE);
                zoomImageView.setVisibility(View.VISIBLE);
                media_container.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                videoSurfaceView.setLayoutParams(new StyledPlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                feed_detail_lay_scroll.setBackgroundColor(Color.parseColor("#333333"));
                media_container.requestLayout();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if ((mUrl != null) && (!mUrl.isEmpty())) {
                    if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
                        mUrl = "http://" + mUrl;
                    }
                    feed_read_more.setVisibility(View.VISIBLE);
                } else {
                    feed_read_more.setVisibility(GONE);
                }
                feed_link.setVisibility(View.GONE);
                feed_link_div.setVisibility(View.GONE);
                other_lay.setVisibility(VISIBLE);
                if (timer_layout.getVisibility() == VISIBLE) {
                    toolbar_root.setVisibility(GONE);
                } else {
                    toolbar_root.setVisibility(VISIBLE);
                }
                zoomImageView.setVisibility(VISIBLE);
                feed_detail_lay_scroll.setBackgroundColor(Color.TRANSPARENT);
                media_container.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
                media_container.requestLayout();
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurfaceView.getLayoutParams();
                float h = metrics.heightPixels;
                params.width = scrWidth;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
                } else {
                    params.height = (int) h;
                }
                media_container.setLayoutParams(params);
                videoSurfaceView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
                OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void onFinish() {
            try {
                progress = 0;
                mandatoryCountDown = null;
                toolbar_root.setVisibility(VISIBLE);
                timer_layout.setVisibility(GONE);
                // feedRewardUpdate(feed);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
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
            request.setDestinationInExternalFilesDir(VideoCardDetailScreen.this, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
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
//                                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                //update percentage

                                Log.i("DownloadHandler", "Download completed " + uriString);
                                if (uriString != null) {
                                    File sourceFile = new File(Objects.requireNonNull(Uri.parse(uriString).getPath()));
                                    File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                    try {
                                        com.oustme.oustsdk.tools.OustSdkTools.copyFile(sourceFile, destinationFile);
                                        try {
                                            branding_mani_layout.setVisibility(GONE);
                                            Uri outputFileUri = FileProvider.getUriForFile(VideoCardDetailScreen.this, OustSdkApplication.getContext().getPackageName() + ".provider", destinationFile);
                                            Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
                                            downloadIntent.setDataAndType(outputFileUri, "application/pdf");
                                            downloadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(downloadIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                            branding_mani_layout.setVisibility(GONE);
                                            openAttachment(downloadUrl, feed.getHeader() + "");
                                        }
                                    } catch (IOException e) {
                                        branding_mani_layout.setVisibility(GONE);
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openAttachment(String fileName, String header) {
        try {
            Intent redirectScreen = new Intent(VideoCardDetailScreen.this, RedirectWebView.class);
            redirectScreen.putExtra("url", fileName);
            redirectScreen.putExtra("feed_title", header);
            startActivity(redirectScreen);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (videoPlayer != null) {
                videoPlayer.stop();
                videoPlayer.release();
                videoPlayer = null;
            }
            removeVideoView(videoSurfaceView);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isVideoPaused) {
                if (exoplayer_pause != null) {
                    exoplayer_pause.performClick();
                }
                isVideoPaused = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        if (videoPlayer != null && videoPlayer.isPlaying()) {
            isVideoPaused = true;
            if (exoplayer_pause != null) {
                exoplayer_pause.performClick();
            }
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (Util.SDK_INT > 23) {
                initializePlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initializePlayer() {
        try {
            if (videoPlayer == null) {
                videoPlayer = new SimpleExoPlayer.Builder(VideoCardDetailScreen.this).build();
                videoPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT);
                videoSurfaceView.setUseController(true);
                videoSurfaceView.setPlayer(videoPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}