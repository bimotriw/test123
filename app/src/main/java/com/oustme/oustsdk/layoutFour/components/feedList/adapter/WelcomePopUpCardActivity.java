package com.oustme.oustsdk.layoutFour.components.feedList.adapter;

import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.getYTVID;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.feed_ui.tools.FullScreenHelper;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.CacheDataSourceFactory;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.File;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class WelcomePopUpCardActivity extends AppCompatActivity {

    private static final String TAG = "WelcomePopUpCardActivity";
    RelativeLayout intro_card_root_layout;
    NestedScrollView scroll_lay;
    ImageView welcome_popup_background_img;
    LinearLayout content_welcome_main_layout;
    LinearLayout content_layout;
    RelativeLayout relative_layout;
    GifImageView intro_card_image;
    ImageView intro_card_image_gif;
    FrameLayout video_frame;
    StyledPlayerView video_player_view;
    TextView fullImageText;
    ImageView exo_thumbnail;
    ImageButton exo_reverse;
    ImageButton exo_forward;
    ImageButton exo_pause;
    DefaultTimeBar exo_progress;
    ImageView exo_full_screen;
    YouTubePlayerView youtube_video_player_view;
    CardView intro_card_info_layout;
    TextView intro_card_title;
    KatexView intro_card_title_latex;
    TextView intro_card_description;
    KatexView intro_card_description_latex;
    LinearLayout intro_card_attachment_lay;
    TextView intro_card_attachment;
    FrameLayout intro_card_frame;
    PhotoViewAttacher photoViewAttacher;
    ImageButton imageCloseButton;
    SimpleExoPlayer player;
    YouTubePlayer youtubePlayer;
    FullScreenHelper fullScreenHelper;
    MediaPlayer mediaPlayerForAudio;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    MenuItem itemAudio;

    int color;
    int bgColor;
    boolean isVideoCard = false;
    DTOCourseCard cardData;
    PopupWindow zoomImagePopup;
    String cardAudioFile;
    boolean isFastForward;
    long videoSeekTime;
    final FavCardDetails favCardDetails = new FavCardDetails();
    String videoFileName;
    String videoPath;
    String youtubeVideoKey;
    String videoThumbnail;
    boolean isHlsVideo = false;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;
    long totalTime = 0;
    long feedId = 0;
    int videoVolume;
    String studentKey = null;
    private boolean isAudioPlayTrackComplete = false;
    boolean isFullScreen;
    boolean isMute = false;
    boolean isAudioEnable = false;
    boolean pauseAudioPlayer = false;
    private boolean isVideoPaused = false;
    private boolean isPausedProgramatically = false;
    long time = 0;
    private boolean isVideoCompleted = false;
    int REQUEST_CODE_ATTACHMENT = 121;
    int REQUEST_CODE_VIDEO_EXIST = 105;

    boolean courseLandScapeMode;
    int height_toolbar, height_bottom_bar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_pop_up_card);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        intro_card_root_layout = findViewById(R.id.intro_card_root_layout);
        scroll_lay = findViewById(R.id.scroll_lay);
        content_layout = findViewById(R.id.content_layout);
        relative_layout = findViewById(R.id.relative_layout);
        intro_card_image = findViewById(R.id.intro_card_image);
        intro_card_image_gif = findViewById(R.id.intro_card_image_gif);
        video_frame = findViewById(R.id.video_frame);
        video_player_view = findViewById(R.id.video_player_view);
        exo_thumbnail = findViewById(R.id.exo_thumbnail);
        exo_reverse = findViewById(R.id.exo_reverse);
        exo_forward = findViewById(R.id.exo_forward);
        exo_pause = findViewById(R.id.exo_pause);
        exo_progress = findViewById(R.id.exo_progress);
        exo_full_screen = findViewById(R.id.exo_full_screen);
        youtube_video_player_view = findViewById(R.id.youtube_video_player_view);
        intro_card_info_layout = findViewById(R.id.intro_card_info_layout);
        intro_card_title = findViewById(R.id.intro_card_title);
        intro_card_title_latex = findViewById(R.id.intro_card_title_latex);
        intro_card_description = findViewById(R.id.intro_card_description);
        intro_card_description_latex = findViewById(R.id.intro_card_description_latex);
        intro_card_attachment_lay = findViewById(R.id.intro_card_attachment_lay);
        intro_card_attachment = findViewById(R.id.intro_card_attachment);
        intro_card_frame = findViewById(R.id.intro_card_frame);
        fullImageText = findViewById(R.id.full_card_attachment);
        toolbar = findViewById(R.id.toolbar_lay);
        screen_name = findViewById(R.id.screen_name);
        back_button = findViewById(R.id.back_button);
        welcome_popup_background_img = findViewById(R.id.welcome_popup_background_img);
        content_welcome_main_layout = findViewById(R.id.content_welcome_main_layout);

        getColors();
        initData();

        height_toolbar = toolbar.getLayoutParams().height;
        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        content_layout.setAlpha(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initData() {
        try {
            String courseDataString = OustStaticVariableHandling.getInstance().getCourseDataStr();
            Gson gson = new Gson();
            cardData = gson.fromJson(courseDataString, DTOCourseCard.class);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                feedId = bundle.getLong("feedId");
                studentKey = bundle.getString("studentKey");
            }
            if (feedId != 0 & studentKey != null) {
                updateFeedViewed(feedId, studentKey);
            }
            contentAppearAnimation();
            playbackPosition = 0;
            totalTime = 0;
            videoSeekTime = 0;
            isMute = false;
            isAudioPlayTrackComplete = false;
            setData();

            if (photoViewAttacher != null) {
                photoViewAttacher = new PhotoViewAttacher(intro_card_image);
                photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                    gifZoomPopup(intro_card_image.getDrawable());
                });
            }

            back_button.setOnClickListener(view -> {
                onBackPressed();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setData() {
        try {
            if (cardData != null) {
                if (cardData.getCardMedia() != null && cardData.getCardMedia().size() != 0) {
                    for (DTOCourseCardMedia cardMedia : cardData.getCardMedia()) {
                        if ((cardMedia != null) && (cardMedia.getMediaType() != null)) {
                            switch (cardMedia.getMediaType()) {
                                case "GIF":
                                    intro_card_image.setVisibility(View.GONE);
                                    intro_card_image_gif.setVisibility(View.VISIBLE);
                                    setGifImage(cardMedia.getData());
                                    favCardDetails.setImageUrl(cardMedia.getData());
                                    favCardDetails.setMediaType(cardMedia.getMediaType());
                                    break;
                                case "IMAGE":
                                    intro_card_image.setVisibility(View.VISIBLE);
                                    intro_card_image_gif.setVisibility(View.GONE);
                                    setImage(cardMedia.getData());
                                    favCardDetails.setImageUrl(cardMedia.getData());
                                    favCardDetails.setMediaType(cardMedia.getMediaType());
                                    break;
                                case "VIDEO":
                                case "YOUTUBE_VIDEO":
                                    isVideoCard = true;
                                    favCardDetails.setVideo(true);
                                    favCardDetails.setMediaType(cardMedia.getMediaType());
                                    videoThumbnail = cardMedia.getMediaThumbnail();
                                    isFastForward = cardMedia.isFastForwardMedia();
                                    initializePlayer(cardMedia);
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

                if (cardData != null && cardData.getBgImg() != null && !cardData.getBgImg().isEmpty()) {
                    Glide.with(this).load(cardData.getBgImg()).into(welcome_popup_background_img);
                    welcome_popup_background_img.setVisibility(VISIBLE);
                } else {
                    welcome_popup_background_img.setVisibility(View.GONE);
                }

                if (cardData != null && cardData.getCardTitle() != null && !cardData.getCardTitle().isEmpty()) {
                    if (cardData.getCardTitle().contains(KATEX_DELIMITER)) {
                        intro_card_title_latex.setTextColor(getResources().getColor(R.color.primary_text));
                        intro_card_title_latex.setTextColorString("#212121");
                        intro_card_title_latex.setText(cardData.getCardTitle());
                        intro_card_title_latex.setVisibility(View.VISIBLE);
                        intro_card_title.setVisibility(View.GONE);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intro_card_title.setText(Html.fromHtml(cardData.getCardTitle(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            intro_card_title.setText(Html.fromHtml(cardData.getCardTitle()));
                        }
                    }
                }

                if (cardData != null && cardData.getContent() != null && !cardData.getContent().isEmpty()) {
                    if (cardData.getContent().contains(KATEX_DELIMITER)) {
                        intro_card_description_latex.setTextColor(getResources().getColor(R.color.primary_text));
                        intro_card_description_latex.setTextColorString("#212121");
                        intro_card_description_latex.setText(cardData.getContent());
                        intro_card_description_latex.setVisibility(View.VISIBLE);
                        intro_card_description.setVisibility(View.GONE);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intro_card_description.setText(Html.fromHtml(cardData.getContent(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            intro_card_description.setText(Html.fromHtml(cardData.getContent()));
                        }
                    }
                }

                if (cardData != null && cardData.getReadMoreData() != null) {
                    if (cardData.getReadMoreData().getDisplayText() != null && !cardData.getReadMoreData().getDisplayText().isEmpty()) {
                        if (cardData.getReadMoreData().getData() != null && !cardData.getReadMoreData().getData().isEmpty()) {
                            if (cardData.getReadMoreData().getType() != null) {
                                if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                                    fullImageText.setVisibility(View.VISIBLE);
                                } else {
                                    intro_card_attachment_lay.setVisibility(View.VISIBLE);
                                }
                                String attachmentDataType = cardData.getReadMoreData().getType();
                                intro_card_attachment.setText(cardData.getReadMoreData().getDisplayText());
                                fullImageText.setText(cardData.getReadMoreData().getDisplayText());
                                if (attachmentDataType.equalsIgnoreCase("URL_LINK")) {
                                    Drawable linkDrawable = OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_weblink));
                                    linkDrawable.setBounds(0, 0, linkDrawable.getIntrinsicWidth(), linkDrawable.getIntrinsicHeight());
                                    intro_card_attachment.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setOnClickListener(v -> {
                                        Intent redirectScreen = new Intent(this, RedirectWebView.class);
                                        redirectScreen.putExtra("url", cardData.getReadMoreData().getData());
                                        redirectScreen.putExtra("feed_title", cardData.getCardTitle() + "");
                                        startActivity(redirectScreen);
                                    });
                                    intro_card_attachment.setOnClickListener(v -> {
                                        Intent redirectScreen = new Intent(this, RedirectWebView.class);
                                        redirectScreen.putExtra("url", cardData.getReadMoreData().getData());
                                        redirectScreen.putExtra("feed_title", cardData.getCardTitle() + "");
                                        startActivity(redirectScreen);
                                    });
                                } else {
                                    Drawable linkDrawable = OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_attach_fill));
                                    linkDrawable.setBounds(0, 0, linkDrawable.getIntrinsicWidth(), linkDrawable.getIntrinsicHeight());
                                    intro_card_attachment.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setCompoundDrawables(linkDrawable, null, null, null);
                                    intro_card_attachment.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ATTACHMENT));
                                    fullImageText.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ATTACHMENT));

                                    fullImageText.setOnClickListener(v -> {
                                        File file = OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData());
                                        if (file.exists()) {
                                            try {
                                                Uri outputFileUri = FileProvider.getUriForFile(WelcomePopUpCardActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(outputFileUri, "application/pdf");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(intent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                Intent redirectScreen = new Intent(WelcomePopUpCardActivity.this, RedirectWebView.class);
                                                redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                startActivity(redirectScreen);
                                            }
                                        } else {
                                            Intent redirectScreen = new Intent(WelcomePopUpCardActivity.this, RedirectWebView.class);
                                            redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                            redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                            startActivity(redirectScreen);
                                        }
                                    });
                                    intro_card_attachment.setOnClickListener(v -> {
                                        File file = OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData());
                                        if (file != null) {
                                            if (file.exists()) {
                                                try {
                                                    Uri outputFileUri = FileProvider.getUriForFile(WelcomePopUpCardActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(outputFileUri, "application/pdf");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                    Intent redirectScreen = new Intent(WelcomePopUpCardActivity.this, RedirectWebView.class);
                                                    redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                    redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                    startActivity(redirectScreen);
                                                }
                                            } else {
                                                Intent redirectScreen = new Intent(WelcomePopUpCardActivity.this, RedirectWebView.class);
                                                redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                startActivity(redirectScreen);
                                            }
                                        } else {
                                            Intent redirectScreen = new Intent(WelcomePopUpCardActivity.this, RedirectWebView.class);
                                            redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                            redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                            startActivity(redirectScreen);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

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

    private void setGifImage(String fileName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Glide.with(this).load(file).diskCacheStrategy(DiskCacheStrategy.ALL).into(intro_card_image_gif);

            } else {
                String url = CLOUD_FRONT_BASE_PATH + "course/media/gif/" + fileName;
                Log.d(TAG, "setFullImage: url:" + url);
                Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(intro_card_image_gif);
            }

            ViewGroup.LayoutParams layoutParams = intro_card_image_gif.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        scrHeight = metrics.heightPixels * 0.40;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        scrHeight = metrics.heightPixels * 0.65;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        intro_card_info_layout.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    intro_card_image_gif.setLayoutParams(layoutParams);
                }
            } else {
                double srcHeight = metrics.heightPixels;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.GONE);
                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    intro_card_image_gif.setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setImage(String fileName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Glide.with(this)
                        .asBitmap()
                        .load(file).diskCacheStrategy(DiskCacheStrategy.ALL).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            intro_card_image.setImageBitmap(resource);
                            Log.e("CardFragment", "Card Image loaded url " + resource.toString() + " file " + file);
                            photoViewAttacher = new PhotoViewAttacher(intro_card_image);

                            photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                                Log.e(TAG, "setImageOnPhoto0: " + uri);
                                gifZoomPopup(intro_card_image.getDrawable());
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            Log.e("CardFragment", "Card Image exception url " + e.getMessage());
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
                intro_card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + uri);
                    gifZoomPopup(intro_card_image.getDrawable());
                });
            } else {
                String url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/image/" + fileName;
                Log.d(TAG, "setFullImage: url:" + url);
                Glide.with(this)
                        .asBitmap()
                        .load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            intro_card_image.setImageBitmap(resource);
                            Log.e("CardFragment", "Card Image loaded file " + resource.toString() + " file " + url);
                            photoViewAttacher = new PhotoViewAttacher(intro_card_image);

                            photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                                Log.e(TAG, "setImageOnPhoto: " + url);
                                gifZoomPopup(intro_card_image.getDrawable());
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            Log.e("CardFragment", "Card Image exception file " + e.getMessage());
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
                intro_card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + url);
                    gifZoomPopup(intro_card_image.getDrawable());
                });
            }

            ViewGroup.LayoutParams layoutParams = intro_card_image.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        scrHeight = metrics.heightPixels * 0.40;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        scrHeight = metrics.heightPixels * 0.65;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        intro_card_info_layout.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    intro_card_image.setLayoutParams(layoutParams);
                }
            } else {
                double srcHeight = metrics.heightPixels;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        intro_card_info_layout.setVisibility(View.GONE);
                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    intro_card_image.setLayoutParams(layoutParams);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gifZoomPopup(Drawable file) {
        try {
            @SuppressLint("InflateParams")
            View popUpView = Objects.requireNonNull(this).getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
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
            OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
        }
    }

    private PopupWindow createPopUpForCardImage(View popUpView) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scrHeight = metrics.heightPixels;
        int height = (int) scrHeight - height_bottom_bar;
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
            OustSdkTools.sendSentryException(e);
        }
        return popupWindow;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void initializePlayer(DTOCourseCardMedia cardMedia) {
        try {
            fullScreenHelper = new FullScreenHelper(this);
            if (cardMedia.getMediaPrivacy() != null && cardMedia.getMediaPrivacy().equalsIgnoreCase("private")) {
                player = new SimpleExoPlayer.Builder(this).build();
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                video_player_view.setPlayer(player);
                double viewHeight;
                ViewGroup.LayoutParams layoutParams = video_player_view.getLayoutParams();

                viewHeight = layoutParams.height;

                video_frame.setVisibility(View.VISIBLE);
                exo_reverse.setVisibility(View.VISIBLE);
                if (isFastForward) {
                    exo_forward.setVisibility(View.VISIBLE);
                    Log.d(TAG, "isfast true check: " + isFastForward);

                } else {
                    Log.d(TAG, "isfast false check: " + isFastForward);
                    exo_forward.setVisibility(View.GONE);
                }

                playStoredVideo(cardMedia.getData());

                exo_progress.setOnTouchListener((v, event) -> {
                    Log.d(TAG, "onTouchEvent: " + isFastForward);
                    if (isFastForward) {
                        exo_forward.setVisibility(View.VISIBLE);
                        return false;
                    } else {
                        exo_forward.setVisibility(View.GONE);
                        return true;
                    }
                });

                exo_reverse.setOnClickListener(v -> {
                    if (player != null) {
                        if (player.getCurrentPosition() - 10000 > 0) {
                            player.seekTo(player.getCurrentPosition() - 10000);
                        } else {
                            player.seekTo(0);
                        }
                    }
                });
                exo_forward.setOnClickListener(v -> {
                    if (player != null && isFastForward) {
                        Log.d("exoplayer", "" + player.getDuration());
                        player.seekTo(Math.min(player.getCurrentPosition() + 10000, player.getDuration()));
                    }
                });

                exo_pause.setOnClickListener(v -> {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.setPlayWhenReady(false);
                            exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                        } else {
                            player.setPlayWhenReady(true);
                            exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                        }
                    }
                });

                exo_full_screen.setOnClickListener(v -> {
                    isFullScreen = !isFullScreen;
                    if (isFullScreen) {
                        setPotraitVideoRatioFullScreen(video_player_view);
                    } else {
                        setPortaitVideoRatio(video_player_view);
                    }
                });

            } else {
                youtubeVideoKey = getYTVID(cardMedia.getData());
                if (cardData.isPotraitModeVideo()) {
                    ViewGroup.LayoutParams layoutParams = youtube_video_player_view.getLayoutParams();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    double scrHeight = metrics.heightPixels * 0.75;
                    intro_card_description.setVisibility(View.GONE);
                    layoutParams.height = (int) scrHeight;
                    youtube_video_player_view.setLayoutParams(layoutParams);
                }

                youtube_video_player_view.setVisibility(View.VISIBLE);
                youtube_video_player_view.initialize(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {

                    }

                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youtubePlayer = youTubePlayer;
                        youtubePlayer.loadVideo(youtubeVideoKey, 0);
                        videoVolume = 80;
                        youTubePlayer.setVolume(videoVolume);
                        addFullScreenListenerToPlayer();
                    }

                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        if (state == PlayerConstants.PlayerState.ENDED) {
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVideoRatioFullScreen(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = true;
        try {
            intro_card_info_layout.setVisibility(View.GONE);
            fullImageText.setVisibility(View.GONE);
//            welcome_card_read_more.setVisibility(View.GONE);
            scroll_lay.setBackgroundColor(Color.parseColor("#333333"));
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = metrics.heightPixels;
                params.width = scrWidth;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    try {
                        params.height = (int) h - height_toolbar;
                    } catch (Exception e) {
                        params.height = (int) h - height_toolbar;
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    h = params.height;
                } else {
                    params.height = (int) h - height_toolbar;
                }
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPortaitVideoRatio(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = false;
        try {
            intro_card_info_layout.setVisibility(View.VISIBLE);
            fullImageText.setVisibility(View.GONE);
//            welcome_card_read_more.setVisibility(VISIBLE);
            scroll_lay.setBackgroundColor(Color.TRANSPARENT);
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addFullScreenListenerToPlayer() {
        youtube_video_player_view.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onExitFullscreen() {

            }

            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {

            }
        });
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            totalTime = player.getDuration();
            currentWindow = player.getCurrentWindowIndex();
            videoSeekTime = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isVideoPaused) {
                resumeVideoPlayer();
                isVideoPaused = false;
            } else {
                if (isPausedProgramatically && exo_pause != null) {
                    isPausedProgramatically = false;
                    exo_pause.performClick();
                }
            }

            if (pauseAudioPlayer) {
                if (mediaPlayerForAudio != null) {
                    if (!mediaPlayerForAudio.isPlaying()) {
                        pauseAudioPlayer();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resumeVideoPlayer() {
        try {
            if (videoPath != null && (!videoPath.isEmpty()) && (!OustSdkTools.isReadMoreFragmentVisible)) {
                playPrivateVideo(this.videoPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (isVideoCard && isHlsVideo && player.isPlaying()) {
                if (exo_pause != null) {
                    isPausedProgramatically = true;
                    exo_pause.performClick();
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onDetach() {
        try {
            releasePlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void playStoredVideo(final String videoFileName) {
        try {
            this.videoFileName = videoFileName;
            checkVideoExist();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkVideoExist() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String rootPath = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
                Log.d(TAG, "checkVideoMediaExist: path:" + rootPath);
                File rootFile = new File(rootPath);
                if (!rootFile.exists()) {
                    String subPath = OustSdkApplication.getContext().getFilesDir() + "/" + videoFileName;
                    File subFile = new File(subPath);

                    if (subFile.exists()) {
                        videoPath = subPath;
                        playPrivateVideo(videoPath);
                        return;
                    }
                    handleSignedURLVideo();
                } else {
                    videoPath = rootPath;
                    playPrivateVideo(videoPath);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_VIDEO_EXIST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    void handleSignedURLVideo() {

        if (OustSdkTools.checkInternetStatus()) {
            String filename = videoFileName;
            if (videoFileName.contains(".mp4")) {
                filename = videoFileName.replace(".mp4", "");
            }
            String hlsPath = "HLS/" + filename + "-HLS-Segment/" + filename + "-master-playlist.m3u8";
            Log.d(TAG, "startPlayingSignedUrlVideo: " + hlsPath);
            isVideoHlsPresentOnS3(hlsPath);
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    void isVideoHlsPresentOnS3(final String hlsPath) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String bucketName = AppConstants.MediaURLConstants.BUCKET_NAME;
                    String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
                    String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
                    AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                    s3Client.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                    s3Client.getObjectMetadata(bucketName, hlsPath);
                    playHlsVideo(true, hlsPath);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    playHlsVideo(false, "");
                }
            }
        }.start();
    }

    void playHlsVideo(final boolean isHlsVideo, final String hlsPath) {
        try {
            runOnUiThread(() -> playPrivateVideo(isHlsVideo, hlsPath));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void playPrivateVideo(boolean isHlsVideo, String videoPath) {
        if (isHlsVideo) {
            this.isHlsVideo = true;
            this.videoPath = videoPath;
        } else {
            this.videoPath = "course/media/video/" + videoFileName;
        }
        playPrivateVideo(this.videoPath);
    }

    void playPrivateVideo(String videoPath) {
        try {
            MediaSource mediaSource;
            if (videoPath != null && videoPath.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                videoPath = videoPath.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
            }

            if (isHlsVideo) {
                Log.d(TAG, "startVideoPlayer: HLS playing");
                videoPath = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + videoPath;
                Uri videoUri = Uri.parse(videoPath);

                Log.d(TAG, "Video URL:" + videoPath);
                CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024);

                mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
            } else {
                isHlsVideo = true;
                File file = new File(Objects.requireNonNull(videoPath));
                if (file.exists()) {
                    Uri videoUri = Uri.fromFile(file);
                    Log.e("Player", "" + videoUri);
                    Log.d(TAG, "startVideoPlayer: play from FILE");
                    DataSpec dataSpec = new DataSpec(videoUri);
                    final FileDataSource fileDataSource = new FileDataSource();
                    try {
                        fileDataSource.open(dataSpec);
                    } catch (FileDataSource.FileDataSourceException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    DataSource.Factory factory = () -> fileDataSource;
                    mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(fileDataSource.getUri());
                } else {
                    Log.d(TAG, "startVideoPlayer: tried to get from file but not available");
                    videoPath = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + videoPath;
                    Uri videoUri = Uri.parse(videoPath);
                    CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(this, 100 * 1024 * 1024, 5 * 1024 * 1024);
                    mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
                    Log.d(TAG, "video source path: " + videoPath);
                }
            }

            player.prepare(mediaSource);

            Player.EventListener eventListener = new Player.EventListener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_IDLE) {

                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(WelcomePopUpCardActivity.this).load(videoThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        exo_thumbnail.setVisibility(View.VISIBLE);

                    } else if (playbackState == Player.STATE_ENDED) {
                        isVideoCompleted = true;
                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(WelcomePopUpCardActivity.this).load(videoThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        try {
                            setPortaitVideoRatio(video_player_view);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        exo_thumbnail.setVisibility(View.VISIBLE);
                        exo_progress.setEnabled(true);
                    } else if (playbackState == Player.STATE_READY) {

                        exo_thumbnail.setVisibility(View.GONE);

                    } else if (playbackState == Player.STATE_BUFFERING) {

                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(WelcomePopUpCardActivity.this).load(videoThumbnail).diskCacheStrategy(DiskCacheStrategy.ALL).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        exo_thumbnail.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    try {
                        Log.e("-------", "onPlayerError  " + error.getMessage());
                        player.stop();
                        player.prepare(mediaSource);
                        OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                        exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                        player.setPlayWhenReady(false);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            };

            player.addListener(eventListener);
            player.setPlayWhenReady(true);
            player.seekTo(videoSeekTime);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == REQUEST_CODE_ATTACHMENT) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (player != null) {
                        if (player.isPlaying()) {
                            player.setPlayWhenReady(false);
                        } else {
                            player.setPlayWhenReady(true);
                        }
                    } else if (youtubePlayer != null) {
                        youtubePlayer.pause();
                    }

                    Intent attachmentOpen = new Intent(Intent.ACTION_VIEW);
                    attachmentOpen.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        if (cardData.getReadMoreData().getType().equalsIgnoreCase("IMAGE")) {
                            //TODO: handle image view open Attachment
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName()
                                        + ".provider", OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData()));
                                attachmentOpen.setDataAndType(fileUri, "application/pdf");
                            } else {
                                File file = OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData());
                                attachmentOpen.setData(Uri.fromFile(file));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    startActivity(attachmentOpen);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public int calculateVideoProgress() {
        try {
            Log.d(TAG, "calculateVideoProgress: ");
            if (video_player_view != null && player != null && video_player_view.getPlayer() != null) {
                if (isVideoCompleted) {
                    return 100;
                }
                time = video_player_view.getPlayer().getCurrentPosition();
                long total_time = video_player_view.getPlayer().getDuration();
                long progress = time * 100 / total_time;
                Log.d(TAG, "calculateVideoProgress time is: " + progress);

                return (int) progress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return 0;
    }

    public long getTimeInterval() {
        try {
            time = time - 5000;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return (time > 0 ? (time / 1000) : 0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        try {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            Log.d(TAG, "onConfigurationChanged: " + newConfig.orientation);
            if (!isVideoCard) {
                Log.d(TAG, "onConfigurationChanged:  else part");
                if (courseLandScapeMode) {
                    intro_card_title.setVisibility(View.GONE);
                    ViewGroup.LayoutParams layoutParams = intro_card_image.getLayoutParams();

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        double scrHeight = metrics.heightPixels * 0.5;
                        if (cardData.getClCode() != null) {
                            if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                                scrHeight = metrics.heightPixels * 0.40;
                                intro_card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                                scrHeight = metrics.heightPixels * 0.65;
                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                intro_card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                                scrHeight = metrics.heightPixels - height_toolbar;
                                intro_card_info_layout.setVisibility(View.GONE);
                                fullImageText.setVisibility(View.VISIBLE);
                            }
                            layoutParams.height = (int) scrHeight;
                            intro_card_image.setLayoutParams(layoutParams);
                            if (zoomImagePopup != null) {
                                if (zoomImagePopup.isShowing()) {
                                    zoomImagePopup.dismiss();
                                    gifZoomPopup(intro_card_image.getDrawable());
                                }
                            }
                        }
                    } else {
                        double srcHeight;
                        if (cardData.getClCode() != null) {
                            if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                                srcHeight = metrics.heightPixels * 0.80;
                                intro_card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                                srcHeight = metrics.heightPixels * 0.80;
                                intro_card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                                srcHeight = metrics.heightPixels * 0.80;
                                intro_card_info_layout.setVisibility(View.GONE);
                                fullImageText.setVisibility(View.VISIBLE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            }
                            intro_card_image.setLayoutParams(layoutParams);
                            if (zoomImagePopup != null) {
                                if (zoomImagePopup.isShowing()) {
                                    zoomImagePopup.dismiss();
                                    gifZoomPopup(intro_card_image.getDrawable());
                                }
                            }
                        }
                    }
                } else {
                    super.onConfigurationChanged(newConfig);
                }
            } else {
                if (video_player_view != null) {
                    if (courseLandScapeMode) {
                        isFullScreen = !isFullScreen;
                        if (isFullScreen) {
                            setPotraitVideoRatioFullScreen2(video_player_view);
                        } else {
                            setPortaitVideoRatio(video_player_view);
                        }
                        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            setLandscapeVideoRation(video_player_view);
                        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            setPortaitVideoRatio(video_player_view);
                        } else {
                            super.onConfigurationChanged(newConfig);
                        }
                    } else {
                        super.onConfigurationChanged(newConfig);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVideoRatioFullScreen2(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = true;
        try {
            intro_card_info_layout.setVisibility(View.GONE);
            fullImageText.setVisibility(View.GONE);
            scroll_lay.setBackgroundColor(Color.parseColor("#333333"));

            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                int h = metrics.heightPixels;
                params.width = scrWidth;
                params.height = h - height_toolbar; // 140pixels removing because of nested scroll view
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLandscapeVideoRation(StyledPlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
                params.height = scrHeight - height_toolbar;
                params.width = scrWidth;
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);

                ((CustomVideoControlListener) OustSdkApplication.getContext()).hideToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void prepareExoPlayer(String audioFilePath) {
        try {
            Log.d(TAG, "prepareExoPlayerFromUri: ");
            resetAudioPlayer();
//            File audioFile = new File(OustSdkApplication.getContext().getFilesDir(), audioFilePath);
            if (audioFilePath != null) {
                mediaPlayerForAudio = new MediaPlayer();
                mediaPlayerForAudio.reset();
//                FileInputStream fis = new FileInputStream(audioFile);
                mediaPlayerForAudio.setDataSource(audioFilePath);
                mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerForAudio.prepare();
                mediaPlayerForAudio.start();
                invalidateOptionsMenu();
                mediaPlayerForAudio.setOnCompletionListener(mediaPlayer -> {
                    Log.e(TAG, "onCompletion: ");
                    isAudioPlayTrackComplete = true;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
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
            OustSdkTools.sendSentryException(e);
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

    private void contentAppearAnimation() {
        try {
            scroll_lay.setVisibility(View.VISIBLE);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(content_layout, "translationY", scroll_lay.getHeight(), 0);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(content_layout, "alpha", 0.0f, 1.0f);
            scaleDownX.setDuration(600);
            scaleDownY.setDuration(450);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay(250);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(long mFeed, String studentKey) {
        try {
//            if (!mFeed.isClicked()) {
//            landingPage/58171/specialFeed/isClicked
            String message1 = "/landingPage/" + studentKey + "/specialFeed/" + AppConstants.StringConstants.IS_FEED_CLICKED;
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
                        Log.d("TAG", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                    } else {
                        Log.d("TAG", "Firebase counter increment succeeded.");
                    }
                }
            });
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}