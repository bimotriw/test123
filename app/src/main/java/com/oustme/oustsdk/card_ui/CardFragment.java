package com.oustme.oustsdk.card_ui;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.getYTVID;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_VIDEO_BASE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
//import com.gumlet.insights.GumletInsightsConfig;
//import com.gumlet.insights.example.Sample;
//import com.gumlet.insights.example.Samples;
//import com.gumlet.insights.exoplayer.ExoPlayerCollector;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.feed_ui.tools.FullScreenHelper;
import com.oustme.oustsdk.feed_ui.ui.RedirectWebView;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.course.CourseLearningModuleActivity;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.service.DownLoadIntentService;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.CacheDataSourceFactory;
import com.oustme.oustsdk.util.WebViewClass;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class CardFragment extends Fragment {

    private static final String TAG = "CardFragment";
    RelativeLayout card_detail_root_layout;
    ImageView card_bg_image;
    NestedScrollView scroll_lay;
    LinearLayout content_layout;
    RelativeLayout relative_layout;
    GifImageView card_image;
    ImageView card_image_gif;
    FrameLayout video_frame;
    StyledPlayerView video_player_view;
    TextView fullImageText;
    ImageView exo_thumbnail;
    ImageButton exo_reverse;
    ImageButton exo_forward;
    ImageButton exo_pause;
    DefaultTimeBar exo_progress;
    ImageView exo_full_screen;
    ImageView exo_playback_speed;
    YouTubePlayerView youtube_video_player_view;
    ImageView card_favourite;
    CardView card_info_layout;
    TextView card_title;
    KatexView card_title_latex;
    WebViewClass webview_card_title;
    TextView card_description;
    KatexView card_description_latex;
    LinearLayout card_attachment_lay;
    TextView card_attachment;
    WebView webViewContent;
    int color;
    int bgColor;
    boolean isVideoCard = false;
    String backgroundImage;
    boolean showNudgeMessage;
    CourseLevelClass courseLevelClass;
    CourseDataClass courseDataClass;
    DTOCourseCard cardData;
    DTOUserCardData userCardData;
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    PopupWindow zoomImagePopup;
    GifImageView downloadGifImageView;
    RelativeLayout downloadVideoLayout;
    TextView downloadVideoPercentage;
    DTOCourseCardMedia tempDTOCourseCardMedia = new DTOCourseCardMedia();
    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    String cardAudioFile;
    boolean isFastForward;
    long videoSeekTime;
    String courseId;
    String courseName;
    boolean isFavouriteCard;
    List<FavCardDetails> favouriteCardList;
    final FavCardDetails favCardDetails = new FavCardDetails();
    SimpleExoPlayer videoPlayer;
    String videoFileName;
    String videoPath;
    String youtubeVideoKey;
    String videoThumbnail;
    boolean isHlsVideo = false;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;
    long totalTime = 0;
    YouTubePlayer youtubePlayer;
    FullScreenHelper fullScreenHelper;
    int videoVolume;
    boolean isFullScreen;

    int REQUEST_CODE_ATTACHMENT = 121;
    int REQUEST_CODE_VIDEO_EXIST = 105;
    int REQUEST_CODE_SHARE_CARD = 103;

    CustomVideoControlListener customVideoControlListener;

    FrameLayout card_frame;
    boolean courseLandScapeMode;
    int height_toolbar, width_toolbar, height_bottom_bar;
    private boolean isReviewMode;
    int questionNo;
    PhotoViewAttacher photoViewAttacher;
    ImageButton imageCloseButton;
    private ImageView mCardShare;
    boolean clickedAttachment = false;
    boolean stopped = false;
    private boolean isVideoPaused = false;
    private boolean isPausedProgramatically = false;
    boolean isYoutubePlayerPause = false;
    private boolean isVideoDownloading = false;
    private File tempVideoFileName;
    private boolean isDownloadButtonClicked = false;
    private boolean isFileDownloadServiceStarted;
    private boolean isMandatoryTimerStarted;
    private ConcatenatingMediaSource mediaSource;

    public CardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        initFunctions();
    }

    private void initUI(View view) {
        card_detail_root_layout = view.findViewById(R.id.card_detail_root_layout);
        card_bg_image = view.findViewById(R.id.card_bg_image);
        scroll_lay = view.findViewById(R.id.scroll_lay);
        content_layout = view.findViewById(R.id.content_layout);
        relative_layout = view.findViewById(R.id.relative_layout);
        card_image = view.findViewById(R.id.card_image);
        card_image_gif = view.findViewById(R.id.card_image_gif);
        video_frame = view.findViewById(R.id.video_frame);
        video_player_view = view.findViewById(R.id.video_player_view);
        exo_thumbnail = view.findViewById(R.id.exo_thumbnail);
        exo_reverse = view.findViewById(R.id.exo_reverse);
        exo_forward = view.findViewById(R.id.exo_forward);
        exo_pause = view.findViewById(R.id.exo_pause);
        exo_progress = view.findViewById(R.id.exo_progress);
        exo_full_screen = view.findViewById(R.id.exo_full_screen);
        exo_playback_speed = view.findViewById(R.id.exo_playback_speed);
        youtube_video_player_view = view.findViewById(R.id.youtube_video_player_view);
        card_favourite = view.findViewById(R.id.card_favourite);
        card_info_layout = view.findViewById(R.id.card_info_layout);
        card_title = view.findViewById(R.id.card_title);
        card_title_latex = view.findViewById(R.id.card_title_latex);
        webview_card_title = view.findViewById(R.id.webview_card_title);
        card_description = view.findViewById(R.id.card_description);
        card_description_latex = view.findViewById(R.id.card_description_latex);
        card_attachment_lay = view.findViewById(R.id.card_attachment_lay);
        card_attachment = view.findViewById(R.id.card_attachment);
        card_frame = view.findViewById(R.id.card_frame);
        fullImageText = view.findViewById(R.id.full_card_attachment);
        mCardShare = view.findViewById(R.id.card_share);
        webViewContent = view.findViewById(R.id.card_description_webView);
        downloadGifImageView = view.findViewById(R.id.download_video_icon);
        downloadVideoPercentage = view.findViewById(R.id.download_video_text);
        downloadVideoLayout = view.findViewById(R.id.download_video_layout);

        content_layout.setAlpha(0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
            getColors();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        try {
            this.courseDataClass = courseDataClass;
            this.courseId = "" + courseDataClass.getCourseId();
            this.courseName = "" + courseDataClass.getCourseName();
            this.courseLandScapeMode = courseDataClass.isCourseLandScapeMode();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCard(DTOCourseCard cardData) {
        try {
            this.cardData = cardData;
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

    public void setCustomVideoControlInterface(CustomVideoControlListener customVideoControlListener) {
        this.customVideoControlListener = customVideoControlListener;
    }

    private void initFunctions() {
        try {
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
            contentAppearAnimation();
            playbackPosition = 0;
            totalTime = 0;
            videoSeekTime = 0;
            //need to handle tutorial card for swipe
            setData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (photoViewAttacher != null) {
                photoViewAttacher = new PhotoViewAttacher(card_image);
                photoViewAttacher.setOnPhotoTapListener((view, x, y) -> gifZoomPopup(card_image.getDrawable()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setData() {
        try {
            if (cardData != null) {
                setFavouriteCard();
                setShareCard();
                if (cardData.getBgImg() != null && !cardData.getBgImg().isEmpty()) {
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(cardData.getBgImg())
                            .apply(new RequestOptions().override(720, 1280))
                            .into(card_bg_image);
                    card_bg_image.setVisibility(View.VISIBLE);
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(backgroundImage)
                            .apply(new RequestOptions().override(720, 1280))
                            .into(card_bg_image);
                    card_bg_image.setVisibility(View.VISIBLE);
                } else {
                    card_bg_image.setVisibility(View.GONE);
                }

                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(cardData.getQuestionData());
                    if (isReviewMode) {
                        courseContentHandlingInterface.cancelTimer();
                    }
                }

                if (cardData.getCardMedia() != null && cardData.getCardMedia().size() != 0) {
                    for (DTOCourseCardMedia cardMedia : cardData.getCardMedia()) {
                        if ((cardMedia != null) && (cardMedia.getMediaType() != null)) {
                            switch (cardMedia.getMediaType()) {
                                case "GIF":
                                    content_layout.setGravity(Gravity.CENTER);
                                    card_image.setVisibility(View.GONE);
                                    card_image_gif.setVisibility(View.VISIBLE);
                                    if (courseDataClass != null) {
                                        if (courseDataClass.isCourseLandScapeMode() && learningModuleInterface != null) {
                                            learningModuleInterface.changeOrientationUnSpecific();
                                        }
                                    }
                                    downloadGifImageView.setVisibility(View.GONE);
                                    setGifImage("oustlearn_" + cardMedia.getData(), cardMedia.getData());
                                    favCardDetails.setImageUrl(cardMedia.getData());
                                    favCardDetails.setMediaType(cardMedia.getMediaType());
                                    break;
                                case "IMAGE":
                                    content_layout.setGravity(Gravity.CENTER);
                                    card_image.setVisibility(View.VISIBLE);
                                    card_image_gif.setVisibility(View.GONE);
                                    downloadGifImageView.setVisibility(View.GONE);
                                    if (courseDataClass != null) {
                                        if (courseDataClass.isCourseLandScapeMode() && learningModuleInterface != null) {
                                            learningModuleInterface.changeOrientationUnSpecific();
                                        }
                                    }
                                    setImage("oustlearn_" + cardMedia.getData(), cardMedia.getData());
                                    favCardDetails.setImageUrl(cardMedia.getData());
                                    favCardDetails.setMediaType(cardMedia.getMediaType());

                                    break;
                                case "VIDEO":
                                case "YOUTUBE_VIDEO":
                                    content_layout.setGravity(Gravity.TOP);
                                    isVideoCard = true;
                                    favCardDetails.setVideo(true);
                                    favCardDetails.setMediaType(cardMedia.getMediaType());
                                    videoThumbnail = cardMedia.getMediaThumbnail();
                                    try {
                                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                                            Glide.with(Objects.requireNonNull(requireActivity())).load(videoThumbnail).into(exo_thumbnail);
                                        } else {
                                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                    isFastForward = cardMedia.isFastForwardMedia();
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.isLearnCardComplete(false);
                                    }
                                    if (courseContentHandlingInterface != null) {
                                        courseContentHandlingInterface.setVideoCard(true);
                                    }
                                    if (userCardData != null && userCardData.isCardCompleted()) {
                                        isFastForward = true;
                                    }
                                    initializePlayer(cardMedia);
                                    if (cardMedia.getMediaPrivacy() != null && cardMedia.getMediaPrivacy().equalsIgnoreCase("private")) {
                                        if (cardData.isPotraitModeVideo()) {
                                            learningModuleInterface.changeOrientationPortrait();
                                            customVideoControlListener.fullHideToolbar();
                                            setPortraitVideoRatioFullScreen(video_player_view);
                                            OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                                        } else if (courseDataClass.isCourseLandScapeMode() && !cardData.isPotraitModeVideo()) {
                                            learningModuleInterface.changeOrientationLandscape();
                                            customVideoControlListener.fullShowToolbar();
                                            setLandscapeVideoRation(video_player_view);
                                            card_info_layout.setVisibility(View.GONE);
                                            fullImageText.setVisibility(View.GONE);
                                            OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                                        } else if (!courseDataClass.isCourseLandScapeMode() && !cardData.isPotraitModeVideo()) {
                                            learningModuleInterface.changeOrientationPortrait();
                                            customVideoControlListener.fullShowToolbar();
                                            setPortraitVideoRatio(video_player_view);
                                            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                                        }
                                    } else {
                                        customVideoControlListener.fullShowToolbar();
                                        card_attachment.setClickable(false);
                                        card_attachment.setEnabled(false);
                                        if (courseDataClass != null) {
                                            if (courseDataClass.isCourseLandScapeMode() && learningModuleInterface != null) {
                                                learningModuleInterface.changeOrientationUnSpecific();
                                            }
                                        }
                                    }
                                    break;
                                case "AUDIO":
                                    content_layout.setGravity(Gravity.TOP);
                                    if (courseDataClass != null) {
                                        if (courseDataClass.isCourseLandScapeMode() && learningModuleInterface != null) {
                                            learningModuleInterface.changeOrientationUnSpecific();
                                        }
                                    }
                                    if (cardMedia.getData() != null && !cardMedia.getData().isEmpty()) {
                                        cardAudioFile = "oustlearn_" + cardMedia.getData();
                                    }
                                    favCardDetails.setAudio(true);
                                    break;
                            }

                        }
                    }
                    if (!isVideoCard && cardData != null && cardData.getMandatoryViewTime() == 0 &&
                            courseDataClass != null && !courseDataClass.isAutoPlay()) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.isLearnCardComplete(true);
                        }
                    }

                } else {
                    downloadGifImageView.setVisibility(View.GONE);
                }


                if ((cardAudioFile != null) && (!cardAudioFile.isEmpty())) {
                    // handle audio by interface
                    if (learningModuleInterface != null) {
                        learningModuleInterface.handleFragmentAudio(cardAudioFile);
                    }
                }

                //title with html support
                if (cardData != null && cardData.getCardTitle() != null && !cardData.getCardTitle().isEmpty()) {
                    if (cardData.getCardTitle().contains("<math") || cardData.getCardTitle().contains("MathML")) {
                        webview_card_title.setVisibility(View.VISIBLE);
                        card_title_latex.setVisibility(View.GONE);
                        card_title.setVisibility(View.GONE);
                        webview_card_title.setBackgroundColor(Color.TRANSPARENT);
                        OustSdkTools.getSpannedMathmlContent(cardData.getCardTitle(), webview_card_title, false);

                    } else if (cardData.getCardTitle().contains(KATEX_DELIMITER)) {
                        card_title_latex.setTextColor(getResources().getColor(R.color.primary_text));
                        card_title_latex.setTextColorString("#212121");
                        card_title_latex.setText(cardData.getCardTitle());
                        card_title_latex.setVisibility(View.VISIBLE);
                        card_title.setVisibility(View.GONE);
                    } else {
                        webview_card_title.setVisibility(View.GONE);
                        card_title_latex.setVisibility(View.GONE);
                        card_title.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            card_title.setText(Html.fromHtml(cardData.getCardTitle(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            card_title.setText(Html.fromHtml(cardData.getCardTitle()));
                        }
                    }
                }

                //description with html support

                if (cardData != null && cardData.getContent() != null && !cardData.getContent().isEmpty()) {
                    if (cardData.getContent().contains(KATEX_DELIMITER)) {
                        card_description_latex.setTextColor(getResources().getColor(R.color.primary_text));
                        card_description_latex.setTextColorString("#212121");
                        card_description_latex.setText(cardData.getContent());
                        card_description_latex.setVisibility(View.VISIBLE);
                        card_description.setVisibility(View.GONE);
                        webViewContent.setVisibility(View.GONE);
                    } else {
                        card_description_latex.setVisibility(View.GONE);

                        if (cardData.getContent().contains("<math") || cardData.getContent().contains("MathML")) {
                            card_description.setVisibility(View.GONE);
                            webViewContent.setVisibility(View.VISIBLE);
                            webViewContent.setBackgroundColor(Color.TRANSPARENT);
                            OustSdkTools.getSpannedMathmlContent(cardData.getContent(), webViewContent, false);

                        } else if (cardData.getContent().contains("<li>") || cardData.getContent().contains("</li>") ||
                                cardData.getContent().contains("<ol>") || cardData.getContent().contains("</ol>") ||
                                cardData.getContent().contains("<p>") || cardData.getContent().contains("</p>")) {
                            webViewContent.setVisibility(View.VISIBLE);
                            card_description.setVisibility(View.GONE);
                            webViewContent.setBackgroundColor(Color.TRANSPARENT);
                            String text = com.oustme.oustsdk.tools.OustSdkTools.getDescriptionHtmlFormat(cardData.getContent());
                            final WebSettings webSettings = webViewContent.getSettings();
                            // Set the font size (in sp).
                            webSettings.setDefaultFontSize(20);
                            webViewContent.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                card_description.setText(Html.fromHtml(cardData.getContent(), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                card_description.setText(Html.fromHtml(cardData.getContent()));
                            }
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
                                    card_attachment_lay.setVisibility(View.VISIBLE);
                                }
                                String attachmentDataType = cardData.getReadMoreData().getType();
                                card_attachment.setText(cardData.getReadMoreData().getDisplayText());
                                fullImageText.setText(cardData.getReadMoreData().getDisplayText());
                                if (attachmentDataType.equalsIgnoreCase("URL_LINK")) {
                                    @SuppressLint("UseCompatLoadingForDrawables") Drawable linkDrawable = OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_weblink));
                                    linkDrawable.setBounds(0, 0, linkDrawable.getIntrinsicWidth(), linkDrawable.getIntrinsicHeight());
                                    card_attachment.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setOnClickListener(v -> {
                                        clickedAttachment = true;
                                        Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                        redirectScreen.putExtra("url", cardData.getReadMoreData().getData());
                                        redirectScreen.putExtra("feed_title", courseLevelClass.getLevelName() + "");
                                        Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);

                                    });
                                    card_attachment.setOnClickListener(v -> {
                                        clickedAttachment = true;
                                        Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                        redirectScreen.putExtra("url", cardData.getReadMoreData().getData());
                                        redirectScreen.putExtra("feed_title", courseLevelClass.getLevelName() + "");
                                        Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                    });
                                } else {
                                    @SuppressLint("UseCompatLoadingForDrawables") Drawable linkDrawable = OustSdkTools.drawableColor(getResources().getDrawable(R.drawable.ic_attach_fill));
                                    linkDrawable.setBounds(0, 0, linkDrawable.getIntrinsicWidth(), linkDrawable.getIntrinsicHeight());
                                    card_attachment.setCompoundDrawables(linkDrawable, null, null, null);
                                    fullImageText.setCompoundDrawables(linkDrawable, null, null, null);
                                    card_attachment.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ATTACHMENT));
                                    fullImageText.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ATTACHMENT));

                                    fullImageText.setOnClickListener(v -> {
                                        clickedAttachment = true;
                                        File file = OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData());
                                        if (file.exists()) {
                                            try {
                                                Uri outputFileUri = FileProvider.getUriForFile(Objects.requireNonNull(requireActivity()), requireContext().getApplicationContext().getPackageName() + ".provider", file);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(outputFileUri, "application/pdf");
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                Objects.requireNonNull(requireActivity()).startActivity(intent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                OustSdkTools.sendSentryException(e);
                                                Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                                redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                            }
                                        } else {
                                            Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                            redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                            redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                            Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                        }
                                    });
                                    card_attachment.setOnClickListener(v -> {
                                        clickedAttachment = true;
                                        File file = OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData());
                                        try {
                                            if (file.exists()) {
                                                try {
                                                    Uri outputFileUri = FileProvider.getUriForFile(Objects.requireNonNull(requireActivity()), requireContext().getApplicationContext().getPackageName() + ".provider", file);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(outputFileUri, "application/pdf");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    Objects.requireNonNull(requireActivity()).startActivity(intent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                                    redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                    redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                    Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                                }
                                            } else {
                                                Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                                redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                                redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                                Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                            }
                                        } catch (Exception e) {
                                            Intent redirectScreen = new Intent(Objects.requireNonNull(requireActivity()), RedirectWebView.class);
                                            redirectScreen.putExtra("url", CLOUD_FRONT_BASE_PATH + "readmore/file/" + cardData.getReadMoreData().getData());
                                            redirectScreen.putExtra("feed_title", cardData.getReadMoreData().getDisplayText() + "");
                                            Objects.requireNonNull(requireActivity()).startActivity(redirectScreen);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                mCardShare.setOnClickListener(view -> {
                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        shareScreenShot();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SHARE_CARD);
                    }
                });

                card_favourite.setOnClickListener(v -> {
                    if (isFavouriteCard) {
                        isFavouriteCard = false;
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setFavoriteStatus(false);
                        }
                        card_favourite.setImageResource(R.drawable.ic_favourite_heart);

                    } else {
                        isFavouriteCard = true;
                        addToFavouriteCardList();
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setFavCardDetails(favouriteCardList);
                            learningModuleInterface.setFavoriteStatus(isFavouriteCard);
                        }
                        card_favourite.setImageResource(R.drawable.ic_favourite_heart_fill);
                        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(card_favourite,
                                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
                        scaleDown.setDuration(500);
                        scaleDown.setRepeatCount(1);
                        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                        scaleDown.start();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setShareCard() {
        if (cardData.isShareToSocialMedia()) {
            mCardShare.setVisibility(View.VISIBLE);
        } else {
            mCardShare.setVisibility(View.GONE);
        }
    }

    private void setGifImage(String fileName, String rawFileName) {

        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Glide.with(Objects.requireNonNull(requireActivity()))
                        .load(file)
                        .apply(new RequestOptions().override(720, 1280))
                        .into(card_image_gif);
            } else {
                String url = CLOUD_FRONT_BASE_PATH + "course/media/gif/" + rawFileName;
                Log.d(TAG, "setFullImage: url:" + url);
                Glide.with(Objects.requireNonNull(requireActivity()))
                        .load(url)
                        .apply(new RequestOptions().override(720, 1280))
                        .into(card_image_gif);
            }

            ViewGroup.LayoutParams layoutParams = card_image_gif.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        content_layout.setGravity(Gravity.TOP);
                        scrHeight = metrics.heightPixels * 0.40;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        content_layout.setGravity(Gravity.TOP);
                        scrHeight = metrics.heightPixels * 0.65;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        content_layout.setGravity(Gravity.CENTER);
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        card_info_layout.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    card_image_gif.setLayoutParams(layoutParams);
                }
            } else {
                content_layout.setGravity(Gravity.TOP);
                double srcHeight = metrics.heightPixels;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.GONE);
                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    card_image_gif.setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setImage(String fileName, String rawFileName) {

        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Glide.with(Objects.requireNonNull(requireActivity()))
                        .asBitmap()
                        .apply(new RequestOptions().override(720, 1280))
                        .load(file).into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    card_image.setImageBitmap(resource);
                                    Log.e("CardFragment", "Card Image loaded url " + resource + " file " + file);
                                    photoViewAttacher = new PhotoViewAttacher(card_image);

                                    photoViewAttacher.setOnPhotoTapListener((view, x, y) -> {
                                        Log.e(TAG, "setImageOnPhoto0: " + uri);
                                        gifZoomPopup(card_image.getDrawable());
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
                card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + uri);
                    gifZoomPopup(card_image.getDrawable());
                });
            } else {
                String url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/image/" + rawFileName;
                Log.d(TAG, "setFullImage: url:" + url);
                Glide.with(Objects.requireNonNull(requireActivity()))
                        .load(url)
                        .apply(new RequestOptions().override(720, 1280))
                        .into(card_image);
                card_image.setOnClickListener(view -> {
                    Log.e(TAG, "setImage: " + url);
                    gifZoomPopup(card_image.getDrawable());
                });

            }

            ViewGroup.LayoutParams layoutParams = card_image.getLayoutParams();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                double scrHeight = metrics.heightPixels * 0.5;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        content_layout.setGravity(Gravity.TOP);
                        scrHeight = metrics.heightPixels * 0.40;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        content_layout.setGravity(Gravity.TOP);
                        scrHeight = metrics.heightPixels * 0.65;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        content_layout.setGravity(Gravity.CENTER);
                        scrHeight = metrics.heightPixels - height_toolbar - 200;
                        card_info_layout.setVisibility(View.GONE);
                    }
                    layoutParams.height = (int) scrHeight;
                    card_image.setLayoutParams(layoutParams);
                }
            } else {
                content_layout.setGravity(Gravity.TOP);
                double srcHeight = metrics.heightPixels;
                if (cardData.getClCode() != null) {
                    if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.VISIBLE);
                        fullImageText.setVisibility(View.GONE);
                    } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                        srcHeight = metrics.heightPixels * 0.80;
                        card_info_layout.setVisibility(View.GONE);
                        fullImageText.setVisibility(View.VISIBLE);
                    }
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = (int) srcHeight;
                    card_image.setLayoutParams(layoutParams);
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
            View popUpView = Objects.requireNonNull(requireActivity()).getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = createPopUpForCardImage(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(file);

            imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            photoViewAttacher = new PhotoViewAttacher(gifImageView);
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
//            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
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
//        ViewGroup.LayoutParams layoutParams = popUpView.getLayoutParams();
        double scrHeight = metrics.heightPixels;
//        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = (int) scrHeight - height_bottom_bar;
//        popUpView.setLayoutParams(layoutParams);

        PopupWindow popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, height, false); //Creation of popup
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

    private void setFavouriteCard() {
        try {
            if (!OustPreferences.getAppInstallVariable("disableFavCard")) {
                if (isFavouriteCard) {
                    card_favourite.setImageResource(R.drawable.ic_favourite_heart_fill);

                } else {
                    card_favourite.setImageResource(R.drawable.ic_favourite_heart);
                }
            } else {
                card_favourite.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void addToFavouriteCardList() {
        try {
            if ((cardData.getCardTitle() != null) && (!cardData.getCardTitle().isEmpty())) {
                favCardDetails.setCardTitle(cardData.getCardTitle());
            } else favCardDetails.setCardTitle("");

            if ((cardData.getContent() != null) && (!cardData.getContent().isEmpty())) {
                favCardDetails.setCardDescription(cardData.getContent());
            } else favCardDetails.setCardDescription("");

            if (!((favCardDetails.getImageUrl() != null) && (!favCardDetails.getImageUrl().isEmpty()))) {
                favCardDetails.setImageUrl("");
            }

            favCardDetails.setCardId(cardData.getCardId() + "");

            if (!favCardDetails.isAudio()) {
                favCardDetails.setAudio(false);
            }
            if (!favCardDetails.isVideo()) {
                favCardDetails.setVideo(false);
            }

            favouriteCardList.add(favCardDetails);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    void initializePlayer(DTOCourseCardMedia cardMedia) {
        try {
            Log.d(TAG, "initializePlayer: ");
            tempDTOCourseCardMedia = cardMedia;
            fullScreenHelper = new FullScreenHelper(getActivity());
            if (cardMedia.getMediaPrivacy() != null && cardMedia.getMediaPrivacy().equalsIgnoreCase("private")) {
                DefaultRenderersFactory rf = new DefaultRenderersFactory(requireActivity()).setEnableDecoderFallback(true);
                videoPlayer = new SimpleExoPlayer.Builder(Objects.requireNonNull(requireActivity()), rf).build();

                video_player_view.setPlayer(videoPlayer);
                videoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                video_player_view.setControllerShowTimeoutMs(2000);
                video_frame.setVisibility(View.VISIBLE);
                content_layout.setGravity(Gravity.TOP);
                exo_reverse.setVisibility(View.VISIBLE);
                downloadVideoLayout.setVisibility(View.VISIBLE);
                videoPlayer.setPlaybackSpeed(1.0f);
                if (cardMedia.getGumletVideoUrl() != null && !cardMedia.getGumletVideoUrl().isEmpty()) {
                    playStoredVideo(cardMedia.getGumletVideoUrl());
                } else {
                    playStoredVideo(cardMedia.getData());
                }

                if (isFastForward) {
                    exo_playback_speed.setVisibility(View.VISIBLE);
                    exo_forward.setVisibility(View.VISIBLE);
                } else {
                    exo_playback_speed.setVisibility(View.GONE);
                    exo_forward.setVisibility(View.GONE);
                }

                exo_progress.setOnTouchListener((v, event) -> {
                    if (isFastForward) {
                        exo_forward.setVisibility(View.VISIBLE);
                        return false;
                    } else {
                        exo_forward.setVisibility(View.GONE);
                        return true;
                    }
                });

                exo_reverse.setOnClickListener(v -> {
                    if (videoPlayer != null) {
                        isVideoCompleted = false;
                        if (videoPlayer.getCurrentPosition() - 10000 > 0) {
                            videoPlayer.seekTo(videoPlayer.getCurrentPosition() - 10000);
                        } else {
                            videoPlayer.seekTo(0);
                        }
                    }
                });

                exo_forward.setOnClickListener(v -> {
                    if (videoPlayer != null && isFastForward) {
                        isVideoCompleted = false;
                        Log.d("exoplayer_forward", "" + videoPlayer.getDuration() + " - " + videoPlayer.getCurrentPosition());
                        videoPlayer.seekTo(videoPlayer.getCurrentPosition() + 10000);
                        //videoPlayer.seekTo(Math.min(videoPlayer.getCurrentPosition() + 10000, videoPlayer.getDuration()));
                    }
                });

                exo_pause.setOnClickListener(v -> {
                    if (isVideoCompleted) {
                        isVideoCompleted = false;
                        Log.d(TAG, "initializePlayer: onpause");
                        initializePlayer(cardMedia);
                    } else {
                        if (videoPlayer != null) {
                            if (videoPlayer.isPlaying()) {
                                videoPlayer.setPlayWhenReady(false);
                                exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                            } else {
                                videoPlayer.setPlayWhenReady(true);
                                exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                            }
                        }
                    }
                });

                exo_full_screen.setOnClickListener(v -> {
                    isFullScreen = !isFullScreen;
                    if (!cardData.isPotraitModeVideo()) {
                        if (isFullScreen) {
                            customVideoControlListener.fullHideToolbar();
                            learningModuleInterface.changeOrientationLandscape();
                            setLandscapeVideoRation(video_player_view);
                            card_info_layout.setVisibility(View.GONE);
                            fullImageText.setVisibility(View.GONE);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                        } else {
                            customVideoControlListener.fullShowToolbar();
                            learningModuleInterface.changeOrientationPortrait();
                            learningModuleInterface.changeOrientationUnSpecific();
                            setPortraitVideoRatio(video_player_view);
                            card_info_layout.setVisibility(View.VISIBLE);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                        }
                    } else {
                        learningModuleInterface.changeOrientationPortrait();
                        if (isFullScreen) {
                            customVideoControlListener.fullHideToolbar();
                            setPortraitVideoRatioFullScreen(video_player_view);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                        } else {
                            customVideoControlListener.fullShowToolbar();
                            setPortraitVideoRatio(video_player_view);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                        }
                    }
                });
            } else {
                downloadVideoLayout.setVisibility(View.GONE);
                youtubeVideoKey = getYTVID(cardMedia.getData());
                if (cardData.isPotraitModeVideo()) {
                    ViewGroup.LayoutParams layoutParams = youtube_video_player_view.getLayoutParams();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    double scrHeight = metrics.heightPixels * 0.75;
                    card_description.setVisibility(View.GONE);
                    webViewContent.setVisibility(View.GONE);
                    layoutParams.height = (int) scrHeight;
                    youtube_video_player_view.setLayoutParams(layoutParams);
                }
                youtube_video_player_view.setVisibility(View.VISIBLE);
                youtube_video_player_view.initialize(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                        Log.d("errorOccured", error.toString());
                    }

                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        if (!stopped) {
                            if (courseContentHandlingInterface != null && !isReviewMode && cardData.getMandatoryViewTime() > 0) {
                                courseContentHandlingInterface.startMandatoryViewTimer();
                            }
                            card_attachment.setClickable(true);
                            card_attachment.setEnabled(true);
                            youtubePlayer = youTubePlayer;
                            youtubePlayer.loadVideo(youtubeVideoKey, 0);
                            videoVolume = 80;
                            youTubePlayer.setVolume(videoVolume);
                            addFullScreenListenerToPlayer();
                            stopped = false;
                        }
                    }

                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        Log.d(TAG, "onStateChange: youtube Player is ENDED");
                        if (state == PlayerConstants.PlayerState.ENDED) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.cancelTimer();
                            }
                            if (learningModuleInterface != null) {
                                learningModuleInterface.isLearnCardComplete(true);
                                if (courseDataClass != null && courseDataClass.isAutoPlay())
                                    learningModuleInterface.gotoNextScreen();
                            }
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPortraitVideoRatioFullScreen(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = true;
        try {
            card_info_layout.setVisibility(View.GONE);
            fullImageText.setVisibility(View.GONE);
            card_favourite.setVisibility(View.GONE);
            downloadVideoLayout.setVisibility(View.GONE);
            scroll_lay.setBackgroundColor(Color.parseColor("#333333"));

            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = metrics.heightPixels;
                params.width = scrWidth;
                Log.e(TAG, "setPortraitVideoRatioFullScreen: Height--> " + h + " width--> " + scrWidth);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    try {
                        if (((CourseLearningModuleActivity) requireActivity()).checkTimerVisibility()) {
                            params.height = (int) h - height_toolbar;
                        } else {
                            params.height = (int) h;
                        }
                    } catch (Exception e) {
                        params.height = (int) h;
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else {
                    params.height = (int) h;
                }
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPortraitVideoRatio(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = false;
        try {
            card_info_layout.setVisibility(View.VISIBLE);
            fullImageText.setVisibility(View.GONE);
            card_favourite.setVisibility(View.VISIBLE);
            downloadVideoLayout.setVisibility(View.VISIBLE);
            scroll_lay.setBackgroundColor(Color.TRANSPARENT);
            setFavouriteCard();
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
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                customVideoControlListener.fullHideToolbar();
                fullScreenHelper.enterFullScreen();
            }

            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                customVideoControlListener.fullShowToolbar();
                fullScreenHelper.exitFullScreen();
            }
        });
    }

    private void releasePlayer() {
        try {
            if (videoPlayer != null) {
                playWhenReady = videoPlayer.getPlayWhenReady();
                playbackPosition = videoPlayer.getCurrentPosition();
                totalTime = videoPlayer.getDuration();
                currentWindow = videoPlayer.getCurrentWindowIndex();
                videoSeekTime = videoPlayer.getCurrentPosition();
                videoPlayer.release();
                videoPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resumeVideoPlayer() {
        Log.d(TAG, "resumeVideoPlayer: ");
        try {
            if (isPausedProgramatically && exo_pause != null) {
                isPausedProgramatically = false;
                exo_pause.performClick();
                Log.d(TAG, "onResume: isLauncher: exo_pause onResume::");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void playStoredVideo(final String videoFileName) {
        try {
            Log.d(TAG, "playStoredVideo: ");
            this.videoFileName = videoFileName;
            checkVideoExist();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void checkVideoExist() {
        try {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String rootPath = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
                Log.d(TAG, "checkVideoMediaExist: path:" + rootPath);
                File rootFile = new File(rootPath);
                if (!rootFile.exists()) {
                    String subPath = OustSdkApplication.getContext().getFilesDir() + "/" + videoFileName;
                    File subFile = new File(subPath);

                    if (subFile.exists()) {
                        videoPath = subPath;
                        OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                        playPrivateVideo(videoPath);
                        return;
                    }
                    setDownloadBtn();
                    if (tempDTOCourseCardMedia.getGumletVideoUrl() != null && !tempDTOCourseCardMedia.getGumletVideoUrl().isEmpty()) {
                        playPrivateVideo(tempDTOCourseCardMedia.getGumletVideoUrl());
                    } else {
                        handleSignedURLVideo();
                    }
                } else {
                    videoPath = rootPath;
                    isHlsVideo = false;
                    OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                    if (tempDTOCourseCardMedia.getGumletVideoUrl() != null && !tempDTOCourseCardMedia.getGumletVideoUrl().isEmpty()) {
                        playPrivateVideo(tempDTOCourseCardMedia.getGumletVideoUrl());
                    } else {
                        playPrivateVideo(videoPath);
                    }
                }
            } else {
                setDownloadBtn();
                if (tempDTOCourseCardMedia.getGumletVideoUrl() != null && !tempDTOCourseCardMedia.getGumletVideoUrl().isEmpty()) {
                    playPrivateVideo(tempDTOCourseCardMedia.getGumletVideoUrl());
                } else {
                    handleSignedURLVideo();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    void handleSignedURLVideo() {

        if (OustSdkTools.checkInternetStatus()) {
            String filename = videoFileName;
            if (videoFileName != null && videoFileName.contains(".mp4")) {
                filename = videoFileName.replace(".mp4", "");
            }
            String hlsPath = "HLS/" + filename + "-HLS-Segment/" + filename + "-master-playlist.m3u8";
            Log.d(TAG, "startPlayingSignedUrlVideo: " + hlsPath);
            isVideoHlsPresentOnS3(hlsPath);
        } else {
            OustSdkTools.showToast(Objects.requireNonNull(requireActivity()).getResources().getString(R.string.no_internet_connection));
        }
    }

    void isVideoHlsPresentOnS3(final String hlsPath) {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        String bucketName = AppConstants.MediaURLConstants.BUCKET_NAME;
                        String awsKeyId = OustPreferences.get("awsS3KeyId");
                        String awsSecretKeyId = OustPreferences.get("awsS3KeySecret");
                        AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                        s3Client.setRegion(Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                        s3Client.getObjectMetadata(bucketName, hlsPath);
                        playHlsVideo(true, hlsPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        playHlsVideo(false, "");
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void playHlsVideo(final boolean isHlsVideo, final String hlsPath) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> playPrivateVideo(isHlsVideo, hlsPath));
            }
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
            MediaSource mediaSource = null;
            if (videoPath != null && videoPath.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                videoPath = videoPath.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
            }
            if (userCardData != null) {
                if (userCardData.getCardViewInterval() > 0) {
                    if ((userCardData.getCardViewInterval() / 1000) > 1) {
                        videoSeekTime = userCardData.getCardViewInterval() / 1000;
                    } else {
                        videoSeekTime = (userCardData.getCardViewInterval() * 1000);
                    }
                }
            }
            if (tempDTOCourseCardMedia.getGumletVideoUrl() != null && !tempDTOCourseCardMedia.getGumletVideoUrl().isEmpty()) {
                try {
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                    mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (isHlsVideo) {
                    Log.d(TAG, "startVideoPlayer: HLS playing");
                    videoPath = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + videoPath;
                    Uri videoUri = Uri.parse(videoPath);

                    Log.d(TAG, "Video URL:" + videoPath);
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                    mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                    exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));

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

                        assert fileDataSource.getUri() != null;
//                        dataSourceFactory = new CacheDataSourceFactory(getContext(), 100 * 1024 * 1024, 5 * 1024 * 1024);
//                        defaultDataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));

                    } else {
                        Log.d(TAG, "startVideoPlayer: tried to get from file but not available");
                        videoPath = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + videoPath;
                        Uri videoUri = Uri.parse(videoPath);
//                        dataSourceFactory = new CacheDataSourceFactory(getContext(), 100 * 1024 * 1024, 5 * 1024 * 1024);
//                        defaultDataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireActivity());
                        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                        Log.d(TAG, "video source path: " + videoPath);
                    }
                }
            }

            videoPlayer.addMediaSource(mediaSource);
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.prepare();
            video_player_view.hideController();

            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    Player.Listener.super.onPlaybackStateChanged(state);
                    if (state == Player.STATE_IDLE) {
                        Log.d(TAG, "Player is Idle");
                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(Objects.requireNonNull(requireActivity())).load(videoThumbnail).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        exo_thumbnail.setVisibility(View.VISIBLE);

                    } else if (state == Player.STATE_ENDED) {
                        Log.d(TAG, "Player is ENDED");
                        isVideoCompleted = true;
                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(Objects.requireNonNull(requireActivity())).load(videoThumbnail).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                        exo_thumbnail.setVisibility(View.VISIBLE);
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.cancelTimer();
                        }
                        if (learningModuleInterface != null) {
                            learningModuleInterface.isLearnCardComplete(true);
                            if (courseDataClass != null && courseDataClass.isAutoPlay()) {
                                learningModuleInterface.gotoNextScreen();
                            } else {
                                try {
                                    customVideoControlListener.fullShowToolbar();
                                    learningModuleInterface.changeOrientationPortrait();
                                    setPortraitVideoRatio(video_player_view);
                                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                        OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                        exo_progress.setEnabled(true);
                        video_player_view.showController();
                    } else if (state == Player.STATE_READY) {
                        Log.d(TAG, "Player is READY");
                        if (!isMandatoryTimerStarted && courseContentHandlingInterface != null && !isReviewMode && cardData.getMandatoryViewTime() > 0) {
                            courseContentHandlingInterface.startMandatoryViewTimer();
                            isMandatoryTimerStarted = true;
                        }
                        isVideoCompleted = false;
                        exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
                        if (clickedAttachment) {
                            clickedAttachment = false;
                            try {
                                videoPlayer.setPlayWhenReady(false);
                                exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                        exo_thumbnail.setVisibility(View.GONE);
                        video_player_view.hideController();
                        video_frame.setVisibility(View.VISIBLE);
                    } else if (state == Player.STATE_BUFFERING) {
                        Log.d(TAG, "Player is BUFFERING");
                        isVideoCompleted = false;
                        if (videoThumbnail != null && !videoThumbnail.isEmpty()) {
                            Glide.with(Objects.requireNonNull(requireActivity())).load(videoThumbnail).into(exo_thumbnail);
                        } else {
                            exo_thumbnail.setImageDrawable(getResources().getDrawable(R.drawable.default_learning_card));
                        }
                        exo_thumbnail.setVisibility(View.VISIBLE);

                        try {
                            if (videoPlayer != null && videoPlayer.getCurrentPosition() == videoPlayer.getDuration()) {
                                Log.d(TAG, "onPlayerStateChanged: last sec Current-> " + videoPlayer.getCurrentPosition() + " --- Duration:" + videoPlayer.getDuration());
                                videoPlayer.seekTo(videoPlayer.getDuration() - 2000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        video_player_view.showController();
                    }

                    video_player_view.setControllerVisibilityListener(visibility -> {
                        if (visibility == View.GONE && (state == Player.STATE_BUFFERING || state == Player.STATE_ENDED)) {
                            video_player_view.showController();
                        }
                    });
                }

                @Override
                public void onPlayerError(@NonNull ExoPlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        Log.d(TAG, "Player is Error: " + error);
                        videoPlayer.setPlayWhenReady(true);
                        videoPlayer.getPlaybackState();
                        exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                            Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                            OustSdkTools.sendSentryException(error.getSourceException());
                            if (learningModuleInterface != null) {
                                learningModuleInterface.endActivity();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });

            exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
            videoPlayer.seekTo(videoSeekTime);
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
                    if (videoPlayer != null) {
                        if (videoPlayer.isPlaying()) {
                            videoPlayer.setPlayWhenReady(false);
                            exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_play));
                        } else {
                            videoPlayer.setPlayWhenReady(true);
                            exo_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_exo_icon_pause));
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
                                Uri fileUri = FileProvider.getUriForFile(Objects.requireNonNull(requireActivity()),
                                        Objects.requireNonNull(requireActivity()).getApplicationContext().getPackageName() + ".provider",
                                        OustSdkTools.getDataFromPrivateStorage(cardData.getReadMoreData().getData()));
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
                    Objects.requireNonNull(requireActivity()).startActivity(attachmentOpen);
                }
            } else if (requestCode == REQUEST_CODE_SHARE_CARD) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareScreenShot();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void shareScreenShot() {
        card_favourite.setVisibility(View.INVISIBLE);
        mCardShare.setVisibility(View.INVISIBLE);
        downloadVideoLayout.setVisibility(View.INVISIBLE);
        Bitmap bitmap = OustSdkTools.getScreenShot(card_detail_root_layout);
        card_favourite.setVisibility(View.VISIBLE);
        mCardShare.setVisibility(View.VISIBLE);
        downloadVideoLayout.setVisibility(View.VISIBLE);
        String branchLinkUrl = "http://bit.ly/1xEh2HW";
        shareScreenUsingIntent(requireActivity(), branchLinkUrl, bitmap);
    }

    public void shareScreenUsingIntent(Context cx, String branchLink, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "card_screenshot_" + System.currentTimeMillis(), null);
        Uri imageUri = Uri.parse(path);

        String oustShareMessage = OustStrings.getString("cpmmonsharetext");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, branchLink);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, oustShareMessage);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg,text/plain");

        cx.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    long time = 0;
    private boolean isVideoCompleted = false;

    public int calculateVideoProgress() {
        try {
            Log.d(TAG, "calculateVideoProgress: ");
            if (video_player_view != null && videoPlayer != null && video_player_view.getPlayer() != null) {
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
                    card_title.setVisibility(View.GONE);
                    ViewGroup.LayoutParams layoutParams = card_image.getLayoutParams();

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        double scrHeight = metrics.heightPixels * 0.5;
                        if (cardData.getClCode() != null) {
                            if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                                scrHeight = metrics.heightPixels * 0.40;
                                card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {
                                scrHeight = metrics.heightPixels * 0.65;
                                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                                content_layout.setGravity(Gravity.CENTER);
                                scrHeight = metrics.heightPixels - height_toolbar - 200;
                                layoutParams.width = metrics.widthPixels;
                                card_info_layout.setVisibility(View.GONE);
                                fullImageText.setVisibility(View.VISIBLE);
                            }
                            layoutParams.height = (int) scrHeight;
                            card_image.setLayoutParams(layoutParams);
                            if (zoomImagePopup != null) {
                                if (zoomImagePopup.isShowing()) {
                                    zoomImagePopup.dismiss();
                                    gifZoomPopup(card_image.getDrawable());
                                }
                            }
                        }
                    } else {
                        double srcHeight;
                        if (cardData.getClCode() != null) {
                            if ((cardData.getClCode().equals(LearningCardType.CL_I_T)) || (cardData.getClCode().equals(LearningCardType.CL_I_T_A))) {
                                srcHeight = metrics.heightPixels * 0.80;
                                card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || (cardData.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) {

                                srcHeight = metrics.heightPixels * 0.80;
                                card_info_layout.setVisibility(View.VISIBLE);
                                fullImageText.setVisibility(View.GONE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            } else if ((cardData.getClCode().equals(LearningCardType.CL_VI) || (cardData.getClCode().equals(LearningCardType.CL_VI_A)))) {
                                srcHeight = metrics.heightPixels * 0.80;
                                card_info_layout.setVisibility(View.GONE);
                                fullImageText.setVisibility(View.VISIBLE);
                                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layoutParams.height = (int) srcHeight;
                            }
                            card_image.setLayoutParams(layoutParams);
                            if (zoomImagePopup != null) {
                                if (zoomImagePopup.isShowing()) {
                                    zoomImagePopup.dismiss();
                                    gifZoomPopup(card_image.getDrawable());
                                }
                            }
                        }
                    }
                } else {
                    super.onConfigurationChanged(newConfig);
                }
            } else {
                if (video_player_view != null) {
                    if (!cardData.isPotraitModeVideo()) {
                        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            customVideoControlListener.fullHideToolbar();
//                            learningModuleInterface.changeOrientationLandscape();
                            setLandscapeVideoRation(video_player_view);
                            card_info_layout.setVisibility(View.GONE);
                            fullImageText.setVisibility(View.GONE);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
                        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            customVideoControlListener.fullShowToolbar();
//                            learningModuleInterface.changeOrientationPortrait();
                            setPortraitVideoRatio(video_player_view);
                            card_info_layout.setVisibility(View.VISIBLE);
//                            fullImageText.setVisibility(View.VISIBLE);
                            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
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

    public void setPortraitVideoRatioFullScreen2() {

        isFullScreen = false;
        try {
            customVideoControlListener.fullShowToolbar();
            learningModuleInterface.changeOrientationPortrait();
            card_info_layout.setVisibility(View.VISIBLE);
            fullImageText.setVisibility(View.GONE);
            card_favourite.setVisibility(View.VISIBLE);
            downloadVideoLayout.setVisibility(View.VISIBLE);
            scroll_lay.setBackgroundColor(Color.TRANSPARENT);
            setFavouriteCard();
            if (video_player_view != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) video_player_view.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                video_frame.setLayoutParams(params);
                video_player_view.setLayoutParams(params);
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
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                params.height = scrHeight;
                params.width = scrWidth;
                video_frame.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
                customVideoControlListener.fullHideToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLandscapeVideoRation(ImageView imageView) {
        try {
            if (imageView != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(params);

                ((CustomVideoControlListener) OustSdkApplication.getContext()).hideToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPortraitVideoRatio(ImageView imageView) {
        isFullScreen = false;
        try {
            card_info_layout.setVisibility(View.VISIBLE);
            fullImageText.setVisibility(View.GONE);
            card_favourite.setVisibility(View.VISIBLE);
            downloadVideoLayout.setVisibility(View.VISIBLE);
            scroll_lay.setBackgroundColor(Color.TRANSPARENT);
            setFavouriteCard();
            if (imageView != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
//                float h = (scrWidth * 0.60f);
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setToolbarHeight(int width_toolbar, int height_toolbar, int height_bottom_bar) {
        try {
            this.width_toolbar = width_toolbar;
            this.height_toolbar = height_toolbar;
            this.height_bottom_bar = height_bottom_bar;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public void closePopup() {
        try {
            if (imageCloseButton != null) {
                imageCloseButton.performClick();
            } else {
                if (zoomImagePopup != null) {
                    zoomImagePopup.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean isVideoFullScreen() {
        return isFullScreen;
    }

    private void setDownloadBtn() {
        Log.d(TAG, "setDownloadBtn: ");
        try {
            downloadGifImageView.setVisibility(View.VISIBLE);
            downloadGifImageView.setOnClickListener(view -> {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startDownloadingVideo();
                } else {
                    OustSdkTools.showToast(Objects.requireNonNull(requireActivity()).getResources().getString(R.string.video_permission_toast));
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_VIDEO_EXIST);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startDownloadingVideo() {
        DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                try {
                    setDownloadingPercentage(Integer.parseInt(progress), message);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                // OustSdkTools.showToast(message);
                tempVideoFileName.delete();
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    String path = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
                    Log.d(TAG, "onDownLoadStateChanged: path:" + path);
                    final File finalFile = new File(path);
                    tempVideoFileName.renameTo(finalFile);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });

        if (OustSdkTools.checkInternetStatus()) {
            if (!isVideoDownloading) {
                isVideoDownloading = true;
                isDownloadButtonClicked = true;
                OustSdkTools.setDownloadGifImage(downloadGifImageView);
                String path = OustSdkApplication.getContext().getFilesDir().getPath();
                tempVideoFileName = new File(path);
                String downloadPath;
                if (tempDTOCourseCardMedia.getGumletVideoUrl() != null && !tempDTOCourseCardMedia.getGumletVideoUrl().isEmpty()) {
                    downloadPath = videoFileName;
                } else {
                    downloadPath = CLOUD_FRONT_VIDEO_BASE + videoFileName;
                }

                sendToDownloadService(OustSdkApplication.getContext(), downloadPath, path, videoFileName, false);
            }
        } else {
            OustSdkTools.showToast(requireActivity().getResources().getString(R.string.no_internet_connection));
        }
    }

    private void sendToDownloadService(Context context, String downloadPath, String destn, String fileName, boolean isOustLearn) {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
        context.startService(intent);
        isFileDownloadServiceStarted = true;
    }

    private void setDownloadingPercentage(int percentage, String message) {
        try {
            if (percentage > 0) {
                if (!isDownloadButtonClicked) {
                    isDownloadButtonClicked = true;
                    OustSdkTools.setDownloadGifImage(downloadGifImageView);
                }

                if (percentage == 100) {
                    OustPreferences.clear(videoFileName);
                    downloadVideoPercentage.setText("");
                    new Handler().postDelayed(() -> {
                        isDownloadButtonClicked = false;
                        Drawable drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                        if (drawable != null) {
                            downloadGifImageView.setImageDrawable(drawable);
                        }
                    }, 3000);
                } else {
                    downloadVideoPercentage.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setReceiver() {
        try {
            myFileDownLoadReceiver = new MyFileDownLoadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_COMPLETE);
            intentFilter.addAction(ACTION_ERROR);
            intentFilter.addAction(ACTION_PROGRESS);
            if (getActivity() != null) {
                getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isFileDownloadServiceStarted) {
                    if (intent.getAction() != null) {
                        try {
                            if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                                setDownloadingPercentage(Integer.parseInt(intent.getStringExtra("MSG")), "");
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                                String path = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
                                final File finalFile = new File(path);
                                if (tempVideoFileName != null && tempVideoFileName.isFile()) {
                                    tempVideoFileName.renameTo(finalFile);
                                }
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                                OustSdkTools.showToast(intent.getStringExtra("MSG") + " Please try again");
                                if (tempVideoFileName != null && tempVideoFileName.isFile()) {
                                    tempVideoFileName.delete();
                                }
                                OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
                                isVideoDownloading = false;
                                isDownloadButtonClicked = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (myFileDownLoadReceiver != null) {
                if (getActivity() != null) {
                    getActivity().unregisterReceiver(myFileDownLoadReceiver);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            setReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isYoutubePlayerPause) {
                if (youtubePlayer != null) {
                    youtubePlayer.play();
                    youtubePlayer.unMute();
                    youtubePlayer.setVolume(50);
                }
                isYoutubePlayerPause = false;
            }
            if (isVideoPaused) {
                resumeVideoPlayer();
                isVideoPaused = false;
            }
        } catch (Exception e) {
            Log.d(TAG, "onResume: isLauncher::" + e.getMessage());
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (isVideoCard && isHlsVideo && videoPlayer.isPlaying()) {
                if (exo_pause != null) {
                    isPausedProgramatically = true;
                    exo_pause.performClick();
                    Log.d(TAG, "onPause: isLauncher: exo_pause onPause");
                }
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
            releasePlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            stopped = true;
            if (youtubePlayer != null) {
                youtubePlayer.pause();
                youtubePlayer.mute();
                youtubePlayer.setVolume(0);
                isYoutubePlayerPause = true;
            } else {
                isVideoPaused = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
