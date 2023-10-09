package com.oustme.oustsdk.fragments.courses.adaptive;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_VIDEO_BASE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.S3_BUCKET_NAME;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.material.snackbar.Snackbar;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReadMoreFavouriteCallBack;
import com.oustme.oustsdk.interfaces.course.YoutubeStateInformer;
import com.oustme.oustsdk.interfaces.course.openReadMore;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.response.course.LearningCardType;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.service.DownLoadIntentService;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.TrackSelectionHelper;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class AdaptiveModuleOverViewFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnTouchListener, ReadMoreFavouriteCallBack, YoutubeStateInformer, DialogKeyListener, VideoRendererEventListener {

    private static final String TAG = "AdaptiveModuleOverViewF";
    private GifImageView mainzooming_img;

    private LinearLayout moduleoverview_toplayout;
    private ProgressBar learningcard_progress;
    private TextView learningcard_coursename, downloadvideo_text, learningquiz_timertext;
    private ScrollView learningcard_tutscrollview;
    //----------

    public static PopupWindow zoomImagePopup;
    private HtmlTextView solution_headertext;
    private GifImageView solutionMain_imagea, solutionMain_imageb, learning_tutorial_imageView;
    private LinearLayout learningcard_tuta, learningcard_tutb;
    //------------
    private RelativeLayout tutorial_scr;
    private LinearLayout bottomswipe_view, learningcard_dynamic_ll;
    private boolean containImage = false;

    private PopupWindow mPopupWindowZoomImage;

    private LinearLayout mLinearLayoutRoot;
    private TrackSelectionHelper trackSelectionHelper;

    //-------------------------
    private YouTubePlayerView learningtut_videoframelayout;
    private RelativeLayout learnngtut_mainimagelayout, learningtut_videolayout, quesvideoLayout, top_icons,
            imageoverlayout, freeflow_ll, content_readmore_layout;
    private ImageView youtubemainImage, bulletin_icon;
    //-----------
    private PlayerView simpleExoPlayerView;
    private RelativeLayout video_player_layout;
    private ProgressBar video_progressbar;
    private WebView freeflow_webview;
    private ImageView videothumbnail_image, unfavourite, video_expandbtn, video_stopbtn;

    private HtmlTextView solution_desc, learningtop_title, learningtop_desc;

    private KatexView solution_desc_maths, learningtop_title_maths, learningtop_desc_maths;
    private KatexView solution_headertext_maths;

//---------------

    private TextView cardprogress_text, topcontent_readmore_text, content_readmore_text, learningcard_dynamic_url, learningcard_dynamic_text;
    private RelativeLayout moduleoverview_animviewa, halfimage_layout, fullimage_layout, rm_share_layout, bulletin_layout,
            topcontent_readmore_layout;
    private LinearLayout gotopreviousscreen_mainbtn, gotonextscreen_mainbtn, mainanim_layout;
    private ImageView learning_halfimgage, learning_fullimgage, gyan_backgroundimage, gyan_backgroundimage_downloaded, questionmore_btn,
            gyan_arrowback, gyan_arrowfoword, optionmain_speaker, youtube_playbtn;
    private Button show_quality;

    private GifImageView downloadvideo_icon;

    private MediaPlayer mediaPlayer;

    private boolean isfavouriteClicked = false, isReviewMode = false, isYoutubeInitialized = false;
    private int youtubeElapseTime = 0;
    private ProgressBar mandatory_timer_progress;
    private TextView mandatory_timer_text;
    private ImageView video_landscape_zoom;
    private boolean autoPlay;
    private TextView textViewContent;
    private WebView webViewContent;
    private File tempVideoFileName;
    private MyFileDownLoadReceiver myFileDownLoadReceiver;
    private boolean hideTutorial = false;
    private int cardIdInt;
    private int levelId;
    private int xp;
    private boolean isScormEventBased;

    public void setHideTutorial(boolean hideTutorial) {
        this.hideTutorial = hideTutorial;
    }

    private boolean isFileDownloadServiceStarted;

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private AdaptiveCourseLevelModel courseLevelClass;

    public void setCourseLevelClass(AdaptiveCourseLevelModel courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
        levelId = (int) courseLevelClass.getLevelId();
    }

    private boolean isComingFromFeed = false;

    public void isComingFromFeed(boolean isComingFromFeed) {
        this.isComingFromFeed = isComingFromFeed;
    }

    private boolean isSureveyIntroCard = false;

    public void setSureveyIntroCard(boolean sureveyIntroCard) {
        isSureveyIntroCard = sureveyIntroCard;
    }

    public void setScormEventBased(boolean isScormEventBased) {
        this.isScormEventBased = isScormEventBased;
    }

    @Override
    public void onDialogClose() {
        try {
            if (learningModuleInterface != null) {
                learningModuleInterface.changeOrientationPortrait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            if (simpleExoPlayerView != null || simpleExoPlayer != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setLandscapeVid(true);
                    setLandscapeVideoRation();
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setPotraitVid(true);
                    setPotraitVideoRatio();
                    setFullImageRatio();
                    setHalfImageRatio();
                }
            } else if ((courseCardClass.getCardType() != null) && (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    moduleoverview_toplayout.setVisibility(View.GONE);
                    bottomswipe_view.setVisibility(View.GONE);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    moduleoverview_toplayout.setVisibility(View.VISIBLE);
                    bottomswipe_view.setVisibility(View.VISIBLE);
                }
            } else
                super.onConfigurationChanged(newConfig);
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learningmapmoduleoverview, container, false);
        initViews(view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (isComingFromFeed || OustPreferences.getAppInstallVariable("learning_card_tutorial_shown") || hideTutorial) {
            initModuleViewFragment();
        } else {
            OustPreferences.saveAppInstallVariable("learning_card_tutorial_shown", true);
            showTutorial();
        }

        return view;
    }


    private MyVideoDownloadReceiver receiver;
    private boolean isVideoPaused = false;

    @Override
    public void onResume() {
        super.onResume();
        if (isDownloadButtonclicked || isVideoPaused) {
            resumeVideoPlayer();
            isVideoPaused = false;
        }
        try {
            IntentFilter filter = new IntentFilter(MyVideoDownloadReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new MyVideoDownloadReceiver();
            if (getActivity() != null) {
                getActivity().registerReceiver(receiver, filter);
            }
        } catch (Exception e) {
        }
        if (fullimage_layout.getVisibility() == View.VISIBLE) {
            setFullImageRatio();
        }
        if (halfimage_layout.getVisibility() == View.VISIBLE) {
            setHalfImageRatio();
        }

        questionmore_btn.setVisibility(View.GONE);//remove when needed

    }


    private void initViews(View view) {

        mLinearLayoutRoot = view.findViewById(R.id.rootLinearLayout);

        //    textViewContent = view.findViewById(R.id.textViewContent);
        webViewContent = view.findViewById(R.id.webViewContent);

        moduleoverview_toplayout = view.findViewById(R.id.moduleoverview_toplayout);
        learningcard_progress = view.findViewById(R.id.learningcard_progress);
        learningcard_coursename = view.findViewById(R.id.learningcard_coursename);
        learningquiz_timertext = view.findViewById(R.id.learningquiz_timertext);
        learningcard_tutscrollview = view.findViewById(R.id.learningcard_tutscrollview);
        //------------------------
        tutorial_scr = view.findViewById(R.id.tutorial_scr);
        learning_tutorial_imageView = view.findViewById(R.id.learning_tutorial_imageView);
        solution_headertext = view.findViewById(R.id.solution_headertext);
        solutionMain_imagea = view.findViewById(R.id.solutionMain_imagea);
        learningcard_tuta = view.findViewById(R.id.learningcard_tuta);
        solutionMain_imageb = view.findViewById(R.id.solutionMain_imageb);
        learningcard_tutb = view.findViewById(R.id.learningcard_tutb);
        //-------------------------

        bottomswipe_view = view.findViewById(R.id.bottomswipe_view);
        videothumbnail_image = view.findViewById(R.id.videothumbnail_image);
        unfavourite = view.findViewById(R.id.unfavourite);
        video_expandbtn = view.findViewById(R.id.video_expandbtn);
        video_stopbtn = view.findViewById(R.id.video_stopbtn);

        //-------------------------
        learningtut_videoframelayout = view.findViewById(R.id.learningtut_videoframelayout);
        learnngtut_mainimagelayout = view.findViewById(R.id.learnngtut_mainimagelayout);
        youtubemainImage = view.findViewById(R.id.youtubemainImage);
        learningtut_videolayout = view.findViewById(R.id.learningtut_videolayout);
        //-----------
        quesvideoLayout = view.findViewById(R.id.quesvideoLayout);
//        video_player_view = (VideoView) view.findViewById(R.id.video_player_view);
//        simpleExoPlayerView= (SimpleExoPlayerView) view.findViewById(R.id.video_player_view);
        video_player_layout = view.findViewById(R.id.video_player_layout);
        video_progressbar = view.findViewById(R.id.video_progressbar);
        show_quality = view.findViewById(R.id.show_quality);
        mandatory_timer_progress = view.findViewById(R.id.mandatory_timer_progress);
        mandatory_timer_text = view.findViewById(R.id.mandatory_timer_text);
        video_landscape_zoom = view.findViewById(R.id.video_landscape_zoom);

        //---------------

        topcontent_readmore_layout = view.findViewById(R.id.topcontent_readmore_layout);
        topcontent_readmore_text = view.findViewById(R.id.topcontent_readmore_text);
        content_readmore_text = view.findViewById(R.id.content_readmore_text);
        content_readmore_layout = view.findViewById(R.id.content_readmore_layout);

        solution_desc = view.findViewById(R.id.solution_desc);
        solution_desc_maths = view.findViewById(R.id.solution_desc_maths);
        solution_headertext_maths = view.findViewById(R.id.solution_headertext_maths);
        learningtop_title_maths = view.findViewById(R.id.learningtop_title_maths);
        learningtop_desc_maths = view.findViewById(R.id.learningtop_desc_maths);

        learningcard_dynamic_text = view.findViewById(R.id.learningcard_dynamic_text);
        learningcard_dynamic_url = view.findViewById(R.id.learningcard_dynamic_url);
        learningcard_dynamic_ll = view.findViewById(R.id.learningcard_dynamic_ll);

        moduleoverview_animviewa = view.findViewById(R.id.moduleoverview_animviewa);
        gotopreviousscreen_mainbtn = view.findViewById(R.id.gotopreviousscreen_mainbtn);
        gotonextscreen_mainbtn = view.findViewById(R.id.gotonextscreen_mainbtn);
        halfimage_layout = view.findViewById(R.id.halfimage_layout);
        learning_halfimgage = view.findViewById(R.id.learning_halfimgage);
        learningtop_title = view.findViewById(R.id.learningtop_title);
        learningtop_desc = view.findViewById(R.id.learningtop_desc);
        fullimage_layout = view.findViewById(R.id.fullimage_layout);
        learning_fullimgage = view.findViewById(R.id.learning_fullimgage);
        gyan_backgroundimage = view.findViewById(R.id.gyan_backgroundimage);
        gyan_backgroundimage_downloaded = view.findViewById(R.id.gyan_backgroundimage_downloaded);
        mainanim_layout = view.findViewById(R.id.mainanim_layout);
        cardprogress_text = view.findViewById(R.id.cardprogress_text);
        questionmore_btn = view.findViewById(R.id.questionmore_btn);
        gyan_arrowfoword = view.findViewById(R.id.gyan_arrowfoword);
        gyan_arrowback = view.findViewById(R.id.gyan_arrowback);
        optionmain_speaker = view.findViewById(R.id.optionmain_speaker);
        rm_share_layout = view.findViewById(R.id.rm_share_layout);

        freeflow_webview = view.findViewById(R.id.freeflow_webview);
        freeflow_ll = view.findViewById(R.id.freeflow_ll);

        start_videobutton = view.findViewById(R.id.start_videobutton);
        learningcard_videoloader = view.findViewById(R.id.learningcard_videoloader);
        youtube_playbtn = view.findViewById(R.id.youtube_playbtn);
        downloadvideo_icon = view.findViewById(R.id.downloadvideo_icon);
        downloadvideo_text = view.findViewById(R.id.downloadvideo_text);
        top_icons = view.findViewById(R.id.top_icons);
        bulletin_icon = view.findViewById(R.id.bulletin_icon);
        bulletin_layout = view.findViewById(R.id.bulletin_layout);
        imageoverlayout = view.findViewById(R.id.imageoverlayout);
        webview_nointernet_text = view.findViewById(R.id.webview_nointernet_text);
        learningcard_webloader = view.findViewById(R.id.learningcard_webloader);
        learningcard_webloaderback = view.findViewById(R.id.learningcard_webloaderback);

        OustSdkTools.setImage(bulletin_icon, OustSdkApplication.getContext().getString(R.string.bulletinboard));
        OustSdkTools.setImage(youtubemainImage, OustSdkApplication.getContext().getString(R.string.image));
        OustSdkTools.setImage(solutionMain_imageb, OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
        OustSdkTools.setImage(solutionMain_imagea, OustSdkApplication.getContext().getResources().getString(R.string.courses_bg));
        OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));

        OustSdkTools.setImage(gyan_backgroundimage, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));
        //OustSdkTools.setImage(youtube_playbtn, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
        //OustSdkTools.setImage(start_videobutton, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
        start_videobutton.setImageResource(R.drawable.ic_play_button_white);
        youtube_playbtn.setImageResource(R.drawable.ic_play_button_white);

        gotopreviousscreen_mainbtn.setOnTouchListener(this);
        gotonextscreen_mainbtn.setOnTouchListener(this);
        optionmain_speaker.setOnTouchListener(this);
        solutionMain_imagea.setOnTouchListener(this);
        solutionMain_imageb.setOnTouchListener(this);
        questionmore_btn.setOnTouchListener(this);
        learning_halfimgage.setOnTouchListener(this);
        learning_fullimgage.setOnTouchListener(this);
        unfavourite.setOnTouchListener(this);
        rm_share_layout.setOnTouchListener(this);
        bulletin_layout.setOnTouchListener(this);
        video_landscape_zoom.setOnTouchListener(this);

        gotopreviousscreen_mainbtn.setVisibility(View.INVISIBLE);

    }

    public void keepScreenOn() {
        try {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
        }
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private boolean isCardttsEnabled;

    public void setCardttsEnabled(boolean cardttsEnabled) {
        isCardttsEnabled = cardttsEnabled;
    }

    private Handler myHandler;
    private int progressVal = 0;

    public void setProgressVal(int progressVal) {
        this.progressVal = progressVal;
    }

    private DTOAdaptiveCardDataModel courseCardClass;

    public void setCourseCardClass(DTOAdaptiveCardDataModel courseCardClass2) {
        this.courseCardClass = courseCardClass2;
        cardIdInt = (int) courseCardClass2.getCardId();
        xp = (int) courseCardClass2.getXp();
        /*try {
            if (!isComingFromFeed) {
                int savedCardID = (int) courseCardClass2.getCardId();
                //this.courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            } else {
                this.courseCardClass = courseCardClass2;
            }
        } catch (Exception e) {
            this.courseCardClass = courseCardClass2;
        }*/
    }

    private FavCardDetails favCardDetails = new FavCardDetails();
    private List<FavCardDetails> favCardDetailsList;

    private LearningModuleInterface learningModuleInterface;
    private openReadMore mOpenReadMore;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setopenReadmore(openReadMore mOpenReadMore) {
        this.mOpenReadMore = mOpenReadMore;
    }

    String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    String courseName;

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setIsReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    private boolean isRMFavourite;
    private boolean hideBulletinBoard = false;

    public boolean isHideBulletinBoard() {
        return hideBulletinBoard;
    }

    public void setHideBulletinBoard(boolean hideBulletinBoard) {
        this.hideBulletinBoard = hideBulletinBoard;
    }

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    public void isFavourite(boolean isfavouriteClicked) {
        this.isfavouriteClicked = isfavouriteClicked;
    }

    private String cardId;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList, String cardId) {
        //this.favCardDetailsList = favCardDetailsList;
        //this.cardId = cardId;
    }

    private boolean favouriteMode = false;

    public void setFavouriteMode(boolean favouriteMode) {
        this.favouriteMode = favouriteMode;
    }


    public void initModuleViewFragment() {
        try {
            if (!isComingFromFeed) {
                setProgress();
                enableScorll();
                setFavouriteCard();
                setPageTitle();
            }
            if (!OustPreferences.getAppInstallVariable("hideCourseBulletin") && !isHideBulletinBoard()) {
                bulletin_layout.setVisibility(View.VISIBLE);
            } else {
                bulletin_layout.setVisibility(View.GONE);
            }
            // bulletin_layout.setVisibility(View.GONE);
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
            setFontStyle();
            detectCardLayoutAndSetData();
            setColors();
            disabletopbottom();
            if (favouriteMode) {
                questionmore_btn.setVisibility(View.GONE);
            }
            questionmore_btn.setVisibility(View.GONE);//remove when needed
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void disabletopbottom() {
        if (isComingFromFeed) {
            moduleoverview_toplayout.setVisibility(View.VISIBLE);
            moduleoverview_toplayout.setBackgroundColor(Color.TRANSPARENT);
            questionmore_btn.setVisibility(View.GONE);
            learningcard_coursename.setVisibility(View.GONE);
            bottomswipe_view.setVisibility(View.GONE);
        }
        if (isSureveyIntroCard) {
            imageoverlayout.setVisibility(View.GONE);
        }

    }


    private void setFavouriteCard() {
        if (!OustPreferences.getAppInstallVariable("disableFavCard")) {
            if (isfavouriteClicked) {
                isfavouriteClicked = true;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                unfavourite.setColorFilter(getResources().getColor(R.color.Orange));
            } else {
                isfavouriteClicked = false;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                unfavourite.setColorFilter(getResources().getColor(R.color.whitea));
            }
        } else {
            unfavourite.setVisibility(View.GONE);
        }
        unfavourite.setVisibility(View.GONE);//remove this when needed
    }

    private void enableScorll() {
        try {
            learningcard_tutscrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            y1 = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            y2 = event.getY();
                            float deltaX = x1 - x2;
                            float deltaY = y1 - y2;
                            if (deltaX > 0 && deltaY > 0) {
                                if (deltaX > deltaY) {
                                    if (deltaX > MIN_DISTANCE) {
                                        if (fullScreen) {
                                            fullScreen = false;
                                            setPotraitVid(false);
                                        }
                                        if (learningModuleInterface != null && OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
                                            learningModuleInterface.gotoNextScreen();
                                            nextScreenAtOnce();
                                        }

                                    }
                                }
                            } else if (deltaX < 0 && deltaY > 0) {
                                if ((-deltaX) > deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        if (fullScreen) {
                                            fullScreen = false;
                                            setPotraitVid(false);
                                        }
                                        if (learningModuleInterface != null) {
                                            learningModuleInterface.gotoPreviousScreen();
                                            adaptiveLearningModuleInterface.previousScreen();
                                        }
                                    }
                                }
                            } else if (deltaX < 0 && deltaY < 0) {
                                if (deltaX < deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        if (fullScreen) {
                                            fullScreen = false;
                                            setPotraitVid(false);
                                        }
                                        if (learningModuleInterface != null) {
                                            learningModuleInterface.gotoPreviousScreen();
                                            adaptiveLearningModuleInterface.previousScreen();
                                        }
                                    }
                                }
                            } else if (deltaX > 0 && deltaY < 0) {
                                if (deltaX > (-deltaY)) {
                                    if (deltaX > MIN_DISTANCE) {
                                        if (fullScreen) {
                                            fullScreen = false;
                                            setPotraitVid(false);
                                        }
                                        if (learningModuleInterface != null && OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
                                            learningModuleInterface.gotoNextScreen();
                                            nextScreenAtOnce();
                                        }
                                    }
                                }
                            }
                            x1 = 0;
                            y1 = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (x1 == 0 && y1 == 0) {
                                x1 = event.getX();
                                y1 = event.getY();
                            }
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private CounterClass timer;

    private void detectCardLayoutAndSetData() {

        Log.d(TAG, "detectCardLayoutAndSetData: card ID: " + courseCardClass.getCardId());
        Log.d(TAG, "detectCardLayoutAndSetData: card title " + courseCardClass.getCardTitle());
        if (courseCardClass.isShareToSocialMedia()) {
            //rm_share_layout.setVisibility(View.VISIBLE); //commented for adaptive
        }
        if ((courseCardClass.getCardType() != null) && (courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
            freeflow_ll.setVisibility(View.VISIBLE);
            unfavourite.setVisibility(View.GONE);
            openWebView();
            learningModuleInterface.changeOrientationUnSpecific();
        } else {
            Log.d(TAG, "detectCardLayoutAndSetData: card type:" + courseCardClass.getClCode());
            if (courseCardClass.getClCode() != null) {
                if (courseCardClass.getClCode().equals(LearningCardType.CL_FREE_FLOW)) {
                    freeflow_ll.setVisibility(View.VISIBLE);
                    Log.d(TAG, "detectCardLayoutAndSetData: CL ");
                    showHtmlData();
                } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_VI)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_VI_A))) {
                    fullimage_layout.setVisibility(View.VISIBLE);
                    Log.d(TAG, "detectCardLayoutAndSetData: CL T VI");
                    setFullImageRatio();
                    showAllMediaFullImage();
                    setTopContent();
                    setTopContentTitle();
                } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_VI)) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_T)) || (courseCardClass.getClCode().equals(LearningCardType.CL_VI_A))) {
                    Log.d(TAG, "detectCardLayoutAndSetData: full image CL VI");
                    fullimage_layout.setVisibility(View.VISIBLE);
                    setFullImageRatio();
                    showAllMediaFullImage();
                    setContent();
                    setContentTitle();
                } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T)) || ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI_T_A))) || ((courseCardClass.getClCode().equals(LearningCardType.CL_3_4_VI)))) {
                    halfimage_layout.setVisibility(View.VISIBLE);
                    setHalfImageRatio();
                    showAllMediaHalfImage();
                    setContent();
                    setContentTitle();
                } else if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_3_4_VI)) || ((courseCardClass.getClCode().equals(LearningCardType.CL_T_3_4_VI_A)))) {
                    Log.d(TAG, "detectCardLayoutAndSetData: CL T 3 4VI");
                    halfimage_layout.setVisibility(View.VISIBLE);
                    setHalfImageRatio();
                    showAllMediaHalfImage();
                    setTopContent();
                    setTopContentTitle();
                } else {
                    showAllMedia();
                    if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_I)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_I_A)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_VD))) {
                        Log.d(TAG, "detectCardLayoutAndSetData: CL TI CL IA TVD");
                        setTopContent();
                        setTopContentTitle();
                    } else {
                        Log.d(TAG, "detectCardLayoutAndSetData: type not :CLTI CLTIA CLTIVD");
                        setContent();
                        setContentTitle();
                    }
                }
            } else {
                Log.d(TAG, "detectCardLayoutAndSetData: not cl code");
                showAllMedia();
                setContent();
                setContentTitle();
            }
        }
        startTimer();
    }

    private void showTutorial() {
        tutorial_scr.setVisibility(View.VISIBLE);
        learning_tutorial_imageView.setImageResource(R.drawable.learning_tutorial);
        tutorial_scr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorial_scr.setVisibility(View.GONE);
                initModuleViewFragment();
            }
        });
    }

    private void showExtraVideoContent() {
        if ((courseCardClass.getClCode().equals(LearningCardType.CL_T_I)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_I_A)) || (courseCardClass.getClCode().equals(LearningCardType.CL_T_VD))) {
            setTopContent();
            setTopContentTitle();
        } else {
            setContent();
            setContentTitle();
        }
    }

    private void setDyanmicTextData() {
        try {
            String hubAddress = OustPreferences.get("hubAddress");
            if (hubAddress != null && !hubAddress.isEmpty()) {
                learningcard_dynamic_ll.setVisibility(View.VISIBLE);
                String url = "";
                if (hubAddress.contains("http")) {

                    int index = hubAddress.indexOf("http");
                    while (index < hubAddress.length() && hubAddress.charAt(index) != ' ') {
                        url = url + hubAddress.charAt(index);
                        index++;
                    }
                    hubAddress = hubAddress.replace(url, "");
                    learningcard_dynamic_url.setText(url);
                } else {
                    learningcard_dynamic_url.setVisibility(View.GONE);
                }
                clickAttachUrl(url);
                learningcard_dynamic_text.setText(hubAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void clickAttachUrl(final String url) {
        learningcard_dynamic_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
    }

    private TextView webview_nointernet_text;
    private ProgressBar learningcard_webloader;
    private RelativeLayout learningcard_webloaderback;

    private void openWebView() {
        try {
            freeflow_ll.setBackgroundColor(OustSdkTools.getColorBack(R.color.white_pressed));
            if (OustSdkTools.checkInternetStatus()) {
                Animation rotateAnim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.rotate_anim);
                learningcard_webloader.startAnimation(rotateAnim);
                learningcard_webloaderback.setVisibility(View.VISIBLE);
                webview_nointernet_text.setVisibility(View.GONE);
                WebViewClient webViewClient = new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(freeflow_webview, url);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(OustSdkApplication.getContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                            return false;

                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            view.getContext().startActivity(intent);
                            return true;
                        } catch (Exception e) {
                            Toast.makeText(OustSdkApplication.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Log.i(TAG, "shouldOverrideUrlLoading Exception:" + e);
                            return true;
                        }
                    }

                    @Override
                    public void onPageCommitVisible(WebView view, String url) {
                        super.onPageCommitVisible(view, url);
                    }

                    @Override
                    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                        return super.shouldOverrideKeyEvent(view, event);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                    }

                    @Override
                    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                        super.onUnhandledKeyEvent(view, event);
                    }

                    @Override
                    public void onScaleChanged(WebView view, float oldScale, float newScale) {
                        super.onScaleChanged(view, oldScale, newScale);
                    }

                    @Override
                    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                        super.onReceivedLoginRequest(view, realm, account, args);
                    }
                };
                String userAgent = freeflow_webview.getSettings().getUserAgentString();
                userAgent += "Oust_" + userAgent;
                freeflow_webview.getSettings().setUserAgentString(userAgent);
                freeflow_webview.setWebViewClient(webViewClient);
                freeflow_webview.clearCache(true);
                freeflow_webview.getSettings().setUseWideViewPort(true);
                freeflow_webview.setInitialScale(1);
                freeflow_webview.getSettings().setBuiltInZoomControls(true);
                freeflow_webview.clearHistory();
                freeflow_webview.getSettings().setAllowFileAccess(true);
                freeflow_webview.getSettings().setDomStorageEnabled(true);
                freeflow_webview.getSettings().setJavaScriptEnabled(true);
                freeflow_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
                freeflow_webview.getSettings().setLoadWithOverviewMode(true);
                freeflow_webview.getSettings().setUseWideViewPort(true);
                freeflow_webview.getSettings().setPluginState(WebSettings.PluginState.ON);

                if (isScormEventBased) {
                    freeflow_webview.addJavascriptInterface(new WebAppInterface(), "Success");
                    bottomswipe_view.setVisibility(View.GONE);
                } else {
                    bottomswipe_view.setVisibility(View.VISIBLE);
                }

                final String path = courseCardClass.getScormIndexFile();
                //fetch data form local server, for now as its not working we using parent one.
//                if (path.contains("https://s3-us-west-1.amazonaws.com/img.oustme.com/")) {
//                    path = path.replace("https://s3-us-west-1.amazonaws.com/img.oustme.com/", "http://di5jfel2ggs8k.cloudfront.net/");
//                }

                freeflow_webview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        freeflow_webview.loadUrl(path);
                    }
                }, 500);

                //freeflow_webview.loadUrl(path);
                freeflow_webview.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress > 94) {
                            learningcard_webloaderback.setVisibility(View.GONE);
                        }
                    }
                });
//            freeflow_webview.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
//                    WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
//                    Log.i("------", "extra = " + hr.getExtra() + "    Type" + hr.getType());
//                    return false;
//                }
//            });
            } else {
                webview_nointernet_text.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
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

    private Handler webAppHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try {
                bottomswipe_view.setVisibility(View.VISIBLE);
                if (autoPlay)
                    gotoNextScrren();

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    private void startTimer() {
        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble() && !isReviewMode) {
            learningquiz_timertext.setVisibility(View.GONE);
            if (courseCardClass.getMandatoryViewTime() > 0) {
                mandatory_timer_text.setVisibility(View.VISIBLE);
                mandatory_timer_progress.setProgress(0);
                mandatory_timer_progress.setMax(100);
                mandatory_timer_progress.setVisibility(View.VISIBLE);
                //gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
                gyan_arrowfoword.setVisibility(View.GONE);
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                learningModuleInterface.isLearnCardComplete(false);
                timer = new CounterClass(Integer.parseInt(courseCardClass.getMandatoryViewTime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
                timer.start();
            } else {
                mandatory_timer_text.setVisibility(View.GONE);
                mandatory_timer_progress.setProgress(0);
                mandatory_timer_progress.setVisibility(View.GONE);
                gyan_arrowfoword.setVisibility(View.VISIBLE);
                if (courseLevelClass != null && courseLevelClass.getCourseCardClassList() != null && courseLevelClass.getCourseCardClassList().size() > 0) {
                    if (progressVal == (courseLevelClass.getCourseCardClassList().size()) - 1 && isReviewMode) {
                        gotonextscreen_mainbtn.setVisibility(View.GONE);
                    }
                }
              /*  if (progressVal != 0)
                    gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);*/
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
            }
        }
    }

    private void showHtmlData() {
        freeflow_webview.getSettings().setJavaScriptEnabled(true);
        freeflow_webview.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
        freeflow_webview.setWebViewClient(new AdaptiveModuleOverViewFragment.MyWebViewClient());
        freeflow_webview.loadData(courseCardClass.getContent(), "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.d(TAG, "onVideoSizeChanged: RES:(WxH):" + width + "X" + height);
    }

    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

  /*  @Override
    public void onDownLoadProgressChanged(String message, String progress) {
        try {
            setDownloadingPercentage(Integer.valueOf(progress), message);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDownLoadError(String message, int errorCode) {
        OustSdkTools.showToast(message);
        tempVideoFileName.delete();
    }

    @Override
    public void onDownLoadStateChanged(String message, int code) {
        String path=Environment.getExternalStorageDirectory()+"/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/"+videoFileName;
        final File finalFile = new File(path);
        tempVideoFileName.renameTo(finalFile);
    }

    @Override
    public void onAddedToQueue(String id) {

    }

    @Override
    public void onDownLoadStateChangedWithId(String message, int code, String id) {

    }
    */

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void setFontStyle() {
        if ((courseCardClass.getLanguage() != null) && (courseCardClass.getLanguage().equals("en"))) {
            learningcard_coursename.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            learningtop_title.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            solution_headertext.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            learningtop_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            //Typeface typeface1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/lt_std_med.ttf");
            //solution_desc.setTypeface(typeface1);
            //solution_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        }
    }

    private void setHalfImageRatio() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) learning_halfimgage.getLayoutParams();
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen8);
            Log.d(TAG, "setHalfImageRatio: width:" + params.height + " height:" + params.width);
            scrWidth = (scrWidth - size - size);
            if (isSureveyIntroCard) {
                int size1 = (int) getResources().getDimension(R.dimen.oustlayout_dimen10);
                scrWidth = (scrWidth - size1 - size1);
            }
            float h = (scrWidth * 1.15f);
            int h1 = (int) h;
            params.height = h1;
            learning_halfimgage.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFullImageRatio() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            int availableHeight = scrHeight - (int) getResources().getDimension(R.dimen.oustlayout_dimen40) - (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) learning_fullimgage.getLayoutParams();
            Log.d(TAG, "setFullmageRatio: width:" + params.height + " height:" + params.width);
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen8);
            scrWidth = (scrWidth - size - size);
            if (isSureveyIntroCard) {
                int size1 = (int) getResources().getDimension(R.dimen.oustlayout_dimen10);
                scrWidth = (scrWidth - size1 - size1);
            }
            float h = (scrWidth * 1.5f);
            int h1 = (int) h;
            params.height = h1;
            if (availableHeight - h1 > 0) {
                int margin = (availableHeight - h1) / 2;
                params.setMargins(0, margin, 0, margin);
            }

            learning_fullimgage.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = courseCardClass.getCardColorScheme();
            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    optionmain_speaker.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    gyan_arrowfoword.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    gyan_arrowback.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                }
                if ((cardColorScheme.getLevelNameColor() != null) && (!cardColorScheme.getLevelNameColor().isEmpty())) {
                    learningcard_coursename.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                }
                if ((cardColorScheme.getTitleColor() != null) && (!cardColorScheme.getTitleColor().isEmpty())) {
                    learningtop_title.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                    solution_headertext.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                }
                if ((cardColorScheme.getContentColor() != null) && (!cardColorScheme.getContentColor().isEmpty())) {
                    learningtop_desc.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
                    solution_desc.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
                    //learningcard_dynamic_text.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
                }
                if ((cardColorScheme.getBgImage() != null) && (!cardColorScheme.getBgImage().isEmpty())) {
                    setBackgroundImage(cardColorScheme.getBgImage());
                } else {
                    if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                        setBackgroundImage(cardBackgroundImage);
                    }
                }
            } else {
                if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                    setBackgroundImage(cardBackgroundImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            OustSdkTools.setImage(gyan_backgroundimage, getResources().getString(R.string.bg_1));
            if (bgImageUrl != null && !bgImageUrl.isEmpty()) {
                gyan_backgroundimage_downloaded.setVisibility(View.VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(bgImageUrl).into(gyan_backgroundimage_downloaded);
                } else {
                    Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(gyan_backgroundimage_downloaded);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setPageTitle() {
        try {
            learningcard_coursename.setText(courseLevelClass.getLevelName());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setProgress() {
        try {
            mainanim_layout.setAlpha(0);
            if (learningcard_progress != null) {
                learningcard_progress.setVisibility(View.INVISIBLE);
                learningcard_progress.setMax((courseLevelClass.getCourseCardClassList().size() * 50));
                learningcard_progress.setProgress((progressVal));

                if ((progressVal + 1) > courseLevelClass.getCourseCardClassList().size()) {
                    cardprogress_text.setText("" + (progressVal + 1) + "/" + (progressVal + 1));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                learningcard_tutscrollview.setVisibility(View.VISIBLE);
                                ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", ((progressVal) * 50), ((progressVal + 1) * 50));
                                animation.setDuration(600); // 0.5 second
                                animation.start();
                                contentAppearAnim();
                            } catch (Exception e) {
                            }
                        }
                    }, 500);
                } else {
                    cardprogress_text.setText("" + (progressVal + 1) + "/" + courseLevelClass.getCourseCardClassList().size());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                learningcard_tutscrollview.setVisibility(View.VISIBLE);
                                ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", ((progressVal) * 50), ((progressVal + 1) * 50));
                                animation.setDuration(600); // 0.5 second
                                animation.start();
                                contentAppearAnim();
                            } catch (Exception e) {
                            }
                        }
                    }, 500);
                }
                cardprogress_text.setVisibility(View.INVISIBLE);
            }
            /*if (progressVal == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            }*/
            if (isReviewMode) {
                Log.e("isReviewMode", " " + progressVal + " " + courseLevelClass.getCourseCardClassList().size());
                if (progressVal == (courseLevelClass.getCourseCardClassList().size()) - 1) {
                    gotonextscreen_mainbtn.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e("Favourite", "", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void contentAppearAnim() {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainanim_layout, "translationY", learningcard_tutscrollview.getHeight(), 0);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainanim_layout, "alpha", 0.0f, 1.0f);
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

    private void setContent() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty())) {
                if (courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getDisplayText() != null) {
                    Log.d(TAG, "setContent: having readmoredata");
                    if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                        solution_desc_maths.setText(courseCardClass.getContent());
                    } else {
                        solution_desc.setHtml(courseCardClass.getContent());
                    }
                    content_readmore_layout.setVisibility(View.VISIBLE);
//                    content_readmore_text.setHtml("<br/> <a href=http://www.oustme.com>" + courseCardClass.getReadMoreData().getDisplayText() + "</a>");
//                    content_readmore_text.setLinkTextColor(OustSdkTools.getColorBack(R.color.textBlack));
                    content_readmore_text.setText(courseCardClass.getReadMoreData().getDisplayText());
                    content_readmore_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
                        }
                    });
                } else {
                    Log.d(TAG, "setContent: not having readmore data:");
                    Log.d(TAG, "setContent: " + courseCardClass.getContent());
                    if (courseCardClass.getContent().contains("<li>") || courseCardClass.getContent().contains("</li>") ||
                            courseCardClass.getContent().contains("<ol>") || courseCardClass.getContent().contains("</ol>") ||
                            courseCardClass.getContent().contains("<p>") || courseCardClass.getContent().contains("</p>")) {
                        webViewContent.setVisibility(View.VISIBLE);
                        solution_desc.setVisibility(View.GONE);
                        webViewContent.setBackgroundColor(Color.TRANSPARENT);
                        String text = "<html><head>"
                                + "<style type=\"text/css\">ul,li,body{color: #fff;}"
                                + "</style></head>"
                                + "<body >"
                                + courseCardClass.getContent()
                                + "</body></html>";
                        webViewContent.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        Log.d(TAG, "setContent: not webdata:" + courseCardClass.getContent());
                        if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                            solution_desc_maths.setText(courseCardClass.getContent());
                        } else {
                            solution_desc.setHtml(courseCardClass.getContent());
                        }

                        solution_desc.setMovementMethod(new TextViewLinkHandler() {
                            @Override
                            public void onLinkClick(String mUrl) {
//                            mUrl1 = mUrl;
                                learningModuleInterface.setShareClicked(true);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(mUrl));
                                startActivity(browserIntent);
                            }
                        });
                    }
                }
                if (courseCardClass.getContent().contains("<li>") || courseCardClass.getContent().contains("</li>") ||
                        courseCardClass.getContent().contains("<ol>") || courseCardClass.getContent().contains("</ol>") ||
                        courseCardClass.getContent().contains("<p>") || courseCardClass.getContent().contains("</p>")) {

                } else {
                    if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                        solution_desc_maths.setVisibility(View.VISIBLE);
                    } else {
                        solution_desc.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setTopContent() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty()) && (!courseCardClass.getContent().isEmpty())) {
                if (courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getDisplayText() != null) {
                    if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                        learningtop_desc_maths.setText(courseCardClass.getContent());
                    } else {
                        learningtop_desc.setHtml(courseCardClass.getContent());
                    }
                    topcontent_readmore_layout.setVisibility(View.VISIBLE);
                    topcontent_readmore_text.setText(courseCardClass.getReadMoreData().getDisplayText());
                    topcontent_readmore_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
                        }
                    });

                } else {
                    if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                        learningtop_desc_maths.setText(courseCardClass.getContent());
                    } else {
                        learningtop_desc.setHtml(courseCardClass.getContent());
                    }
                    learningtop_desc.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(mUrl));
                            startActivity(browserIntent);
                            learningModuleInterface.setShareClicked(true);
                        }
                    });

                }
                if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                    learningtop_desc_maths.setVisibility(View.VISIBLE);
                } else {
                    learningtop_desc.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setContentTitle() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty()) && (courseCardClass.getCardTitle() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                if (courseCardClass.getCardTitle().contains(KATEX_DELIMITER)) {
                    solution_headertext_maths.setText(courseCardClass.getCardTitle());
                    solution_headertext_maths.setVisibility(View.VISIBLE);
                } else {
                    solution_headertext.setHtml(courseCardClass.getCardTitle());
                    solution_headertext.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setTopContentTitle() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                    if (courseCardClass.getCardTitle().contains(KATEX_DELIMITER)) {
                        learningtop_title_maths.setText(courseCardClass.getCardTitle());
                        learningtop_title_maths.setVisibility(View.VISIBLE);
                    } else {
                        learningtop_title.setHtml(courseCardClass.getCardTitle());
                        learningtop_title.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Spanned s1 = Html.fromHtml(s2, null, new OustTagHandler());
        return s1;
    }


    //-----------------------------------------------------------------------------------------------------
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 30;

    private int imageNO = 0;
    private boolean playAudio = true;

    private void showAllMedia() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        Log.d(TAG, "showAllMedia: mediatype:" + courseCardMedia.getMediaType());
                        Log.d(TAG, "showAllMedia: getdata:" + courseCardMedia.getData());
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                if (imageNO == 0) {
                                    imageNO++;
                                    // if media card contains only one image

                                    setGifImage("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                    favCardDetails.setMediaType("GIF");
                                } else if (imageNO == 1) {
                                    // if media card contains more than one image
                                    setGifImageSecond("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                    favCardDetails.setMediaType("GIF");
                                }
                                break;
                            case "IMAGE":
                                if (imageNO == 0) {
                                    imageNO++;
                                    setImage("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                    favCardDetails.setMediaType("IMAGE");
                                } else if (imageNO == 1) {
                                    setImageA("oustlearn_" + courseCardMedia.getData());
                                    favCardDetails.setImageUrl(courseCardMedia.getData());
                                    favCardDetails.setMediaType("IMAGE");
                                }
                                break;
                            case "VIDEO":
                                playAudio = false;
                                fastForwardMedia = courseCardMedia.isFastForwardMedia();
                                favCardDetails.setVideo(true);
                                favCardDetails.setMediaType("VIDEO");
                                Log.d(TAG, "showAllMedia: " + courseCardClass.isPotraitModeVideo());
                                if (!courseCardClass.isPotraitModeVideo()) {
                                    learningModuleInterface.changeOrientationUnSpecific();
                                } else {
                                    video_landscape_zoom.setVisibility(View.VISIBLE);
                                }
                                keepScreenOn();
                                if (!fastForwardMedia) {
                                    hideVideoControls();
                                    learningModuleInterface.isLearnCardComplete(false);
                                }
                                if (courseCardMedia.getMediaPrivacy() != null && courseCardMedia.getMediaPrivacy().equalsIgnoreCase("private")) {
                                    Log.d(TAG, "showAllMedia: priv");
                                    streamStoredVideo(courseCardMedia.getData());
                                } else {
                                    path = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/video/" + courseCardMedia.getData();
                                    playPublicVideo();
//                                    if (OustSdkTools.checkInternetStatus()) {
////                                        "http://di5jfel2ggs8k.cloudfront.net/course/mpower/c35d41c2-c086-36c5-b779-b1389b1738f1"
////                                        startPlayingVideo("https://s3-us-west-1.amazonaws.com/img.oustme.com/course/media/video/" + courseCardMedia.getData());
//                                        path = "http://di5jfel2ggs8k.cloudfront.net/course/media/video/" + courseCardMedia.getData();
//                                        startVideoPlayer(path);
//                                    } else {
//                                        OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
//                                    }
                                }
                                if ((courseCardMedia.getMediaThumbnail() != null) && (!courseCardMedia.getMediaThumbnail().isEmpty())) {
                                    Log.d(TAG, "showAllMedia: thmbnail");
                                    setThumbnailImage(courseCardMedia.getMediaThumbnail());
                                    favCardDetails.setImageUrl(courseCardMedia.getMediaThumbnail());
                                    favCardDetails.setVideo(true);
                                } else {
                                    Log.d(TAG, "showAllMedia: no thumbnail");
                                    videothumbnail_image.setVisibility(View.GONE);
                                    if (!courseCardClass.isPotraitModeVideo()) {
                                        Log.d(TAG, "showAllMedia: not portrair");
                                        video_player_layout.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.d(TAG, "showAllMedia: portrait");
                                        // simpleExoPlayerView.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;
                            case "YOUTUBE_VIDEO":
                                playAudio = false;
//                                learningModuleInterface.changeOrientationUnSpecific();
                                favCardDetails.setVideo(true);
                                youtubeKey = courseCardMedia.getData();
                                if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                    youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                }
                                if (youtubeKey.contains("https://youtu.be/")) {
                                    youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                }
                                if (youtubeKey.contains("&")) {
                                    int position = youtubeKey.indexOf("&");
                                    youtubeKey = youtubeKey.substring(0, position);
                                }
                                showYoutTubeLayout();
                                break;
                            case "AUDIO":
                                audioFileName = "oustlearn_" + courseCardMedia.getData();
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
            }
            if ((audioFileName != null) && (!audioFileName.isEmpty())) {
                playDownloadedAudioQues(audioFileName);
            } else {
                if (playAudio) {
                    speakAudio();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playPublicVideo() {
        if (OustSdkTools.checkInternetStatus()) {
            start_videobutton.setVisibility(View.GONE);
            start_videobutton.setClickable(false);
            startVideoPlayer(path);
        } else {
            if (!courseCardClass.isPotraitModeVideo()) {
                quesvideoLayout.setVisibility(View.VISIBLE);
            }
            start_videobutton.setClickable(true);
            start_videobutton.setVisibility(View.VISIBLE);
            start_videobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playPublicVideo();
                }
            });
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    public void setThumbnailImage(String imagePath) {
        try {
            try {
                DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videothumbnail_image.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.height = (int) h;
                videothumbnail_image.setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(imagePath).into(videothumbnail_image);
                } else {
                    Picasso.get().load(imagePath).networkPolicy(NetworkPolicy.OFFLINE).into(videothumbnail_image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void speakAudio() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String speakStr = "";
                    if (courseCardClass != null) {
                        if (courseCardClass.getCardTitle() != null) {
                            Spanned s1 = getSpannedContent(courseCardClass.getCardTitle());
                            speakStr += (s1.toString());
                            speakStr += ". ";
                        }
                        if (courseCardClass.getContent() != null) {
                            Spanned s1 = getSpannedContent(courseCardClass.getContent());
                            speakStr += (s1.toString());
                        }
                        if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isCardttsEnabled) {
                            optionmain_speaker.setVisibility(View.VISIBLE);
//                            if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                            speakString(speakStr);
//                            } else {
//                                optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                                if (OustSdkTools.textToSpeech != null) {
//                                    OustSdkTools.stopSpeech();
//                                }
//                                optionmain_speaker.setAnimation(null);
//                            }
                        } else {
                            optionmain_speaker.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }, 1200);
    }


    public void setGifImage(final String fileName) {
        try {
            myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadFile(fileName);
                }
            }, 500);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadFile(String fileName) {
        File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
        if (file != null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            solutionMain_imagea.setImageURI(uri);
            learningcard_tuta.setVisibility(View.VISIBLE);
        }
    }

    public void setImage(final String fileName) {
        try {
            containImage = true;
            final File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Picasso.get().load(file).into(solutionMain_imagea, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: First time:");
                    }

                    @Override
                    public void onError(Exception e) {
                        final String fileToDownload = fileName.replace("oustlearn_", "");
                        final String url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/image/" + fileToDownload;
                        Picasso.get().load(url).into(solutionMain_imagea, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: second time:");
                                file.delete();
                                DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                                    @Override
                                    public void onDownLoadProgressChanged(String message, String progress) {

                                    }

                                    @Override
                                    public void onDownLoadError(String message, int errorCode) {

                                    }

                                    @Override
                                    public void onDownLoadStateChanged(String message, int code) {

                                    }

                                    @Override
                                    public void onAddedToQueue(String id) {

                                    }

                                    @Override
                                    public void onDownLoadStateChangedWithId(String message, int code, String id) {

                                    }
                                });
                                downloadFiles.startDownLoad(url, OustSdkApplication.getContext().getFilesDir() + "/", fileToDownload, true, false);
                            }

                            @Override
                            public void onError(Exception e1) {
                                Log.d(TAG, "onError: error to load image either from local or internet");
                            }
                        });

                    }
                });
                learningcard_tuta.setVisibility(View.VISIBLE);
            } else {
                final String fileToDownload = fileName.replace("oustlearn_", "");
                final String url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/image/" + fileToDownload;
                Picasso.get().load(file).into(solutionMain_imagea);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, "setImage: exception:");
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setGifImageSecond(String fileName) {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                solutionMain_imageb.setImageURI(uri);
                learningcard_tutb.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageA(final String fileName) {
        try {
            containImage = true;
            final File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Picasso.get().load(file).into(solutionMain_imageb, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: First time:");
                    }

                    @Override
                    public void onError(Exception e) {
                        final String fileToDownload = fileName.replace("oustlearn_", "");
                        final String url = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "course/media/image/" + fileToDownload;
                        Picasso.get().load(url).into(solutionMain_imagea, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: second time:");
                                file.delete();
                                DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                                    @Override
                                    public void onDownLoadProgressChanged(String message, String progress) {

                                    }

                                    @Override
                                    public void onDownLoadError(String message, int errorCode) {

                                    }

                                    @Override
                                    public void onDownLoadStateChanged(String message, int code) {

                                    }

                                    @Override
                                    public void onAddedToQueue(String id) {

                                    }

                                    @Override
                                    public void onDownLoadStateChangedWithId(String message, int code, String id) {

                                    }
                                });
                                downloadFiles.startDownLoad(url, OustSdkApplication.getContext().getFilesDir() + "/", fileToDownload, true, false);
                            }

                            @Override
                            public void onError(Exception e1) {

                            }
                        });

                    }
                });
                learningcard_tutb.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //---------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        try {
            if (learningModuleInterface != null) {
                learningModuleInterface.dismissCardInfo();
            }
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler = null;
            }
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                optionmain_speaker.setAnimation(null);
            }
            resetAllData();
            removeVideoPlayer();
            if (getActivity() != null) {
                getActivity().unregisterReceiver(receiver);
            }
            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }

    private void resetAllData() {
        try {
            myHandler = null;
            courseCardClass = null;
            mediaPlayer = null;
            learningModuleInterface = null;
            solutionMain_imagea.setImageBitmap(null);
            solutionMain_imageb.setImageBitmap(null);
            youtubemainImage.setImageBitmap(null);
            learning_halfimgage.setImageBitmap(null);
            gyan_backgroundimage.setImageBitmap(null);
            questionmore_btn.setImageBitmap(null);
            optionmain_speaker.setImageBitmap(null);
            gyan_arrowback.setImageBitmap(null);
            gyan_arrowfoword.setImageBitmap(null);
            youtubemainImage.setImageBitmap(null);
            bulletin_icon.setImageBitmap(null);

            videothumbnail_image.setImageBitmap(null);
            unfavourite.setImageBitmap(null);
            video_expandbtn.setImageBitmap(null);
            video_stopbtn.setImageBitmap(null);
            learning_halfimgage.setImageBitmap(null);

            learning_fullimgage.setImageBitmap(null);
            gyan_backgroundimage.setImageBitmap(null);
            gyan_backgroundimage_downloaded.setImageBitmap(null);
            questionmore_btn.setImageBitmap(null);
            optionmain_speaker.setImageBitmap(null);
            youtube_playbtn.setImageBitmap(null);
            downloadvideo_icon.setImageBitmap(null);
            cancleTimer();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeVideoPlayer();
        //if audio is running stop and hide
        if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
            mediaPlayer.stop();
        }
        Log.e("-------", "onPause");
    }

    public void removeVideoPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                if (learningcard_videoloader.getVisibility() == View.VISIBLE) {
                    learningcard_videoloader.setVisibility(View.GONE);
                }
                isVideoPaused = true;
                time = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                simpleExoPlayer.stop();
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                trackSelector = null;
                Log.e("-------", "removeVideoPlayer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setReceiver();
        OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
        try {
//            if ((mediaPlayer != null)) {
//                mediaPlayer.start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
        try {
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
//                OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                mediaPlayer.stop();
//                optionmain_speaker.setAnimation(null);
//                optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
            }
            if (OustSdkTools.textToSpeech != null) {
//                OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                OustSdkTools.stopSpeech();
//                optionmain_speaker.setAnimation(null);
//                optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private String audioFileName;

    private void playDownloadedAudioQues(final String filename) {
        try {
            mediaPlayer = new MediaPlayer();
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(filename);
            File audioFile = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            if ((audioFile != null) && (audioFile.exists())) {
//                byte[] audBytes = Base64.decode(audStr, 0);
//                // create temp file that will hold byte array
//                File tempMp3 = File.createTempFile(filename, null, getActivity().getCacheDir());
//                tempMp3.deleteOnExit();
//                FileOutputStream fos = new FileOutputStream(tempMp3);
//                fos.write(audBytes);
//                fos.close();
                optionmain_speaker.setVisibility(View.VISIBLE);
                //OustPreferences.saveAppInstallVariable("isttssounddisable", true);
                //optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));

                // resetting mediaplayer instance to evade problems
                mediaPlayer.reset();

                FileInputStream fis = new FileInputStream(audioFile);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
//                if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                mediaPlayer.start();
                scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                optionmain_speaker.startAnimation(scaleanim);
//                } else {
//                    optionmain_speaker.setAnimation(null);
//                    optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                }

//                finalTime = (mediaPlayer.getDuration());
//                currentTime = (mediaPlayer.getCurrentPosition());
//                myHandler.postDelayed(UpdateSongTime, 100);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        musicComplete = true;
                        optionmain_speaker.setAnimation(null);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean musicComplete = false;
    private Animation scaleanim;

    private void restartAudio() {
        myHandler = new Handler();
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    musicComplete = false;
                    myHandler = new Handler();
                    mediaPlayer = new MediaPlayer();
//                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//                    String audStr = enternalPrivateStorage.readSavedData(audioFileName);
                    File audioFile = new File(OustSdkApplication.getContext().getFilesDir(), audioFileName);
                    if ((audioFile != null) && (audioFile.exists())) {
//                        byte[] audBytes = Base64.decode(audStr, 0);
//                        // create temp file that will hold byte array
//                        File tempMp3 = File.createTempFile(audioFileName, null, getActivity().getCacheDir());
//                        tempMp3.deleteOnExit();
//                        FileOutputStream fos = new FileOutputStream(tempMp3);
//                        fos.write(audBytes);
//                        fos.close();
                        optionmain_speaker.setVisibility(View.VISIBLE);

                        // resetting mediaplayer instance to evade problems
                        mediaPlayer.reset();

                        FileInputStream fis = new FileInputStream(audioFile);
                        mediaPlayer.setDataSource(fis.getFD());
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        finalTime = (mediaPlayer.getDuration());
                        currentTime = (mediaPlayer.getCurrentPosition());
                        scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                        optionmain_speaker.startAnimation(scaleanim);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                musicComplete = true;
                                optionmain_speaker.setAnimation(null);
                                currentTime = finalTime + 10000;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------
    public YouTubePlayer videoPlayer;
    private String youtubeKey = "";
    //private YouTubePlayerSupportFragment youTubePlayerFragment;

    private void showYoutTubeLayout() {
        try {
            learningtut_videolayout.setVisibility(View.VISIBLE);
            BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.image));
            Picasso.get().load("http://img.youtube.com/vi/" + youtubeKey + "/default.jpg").placeholder(bd).error(bd).into(youtubemainImage);
            favCardDetails.setImageUrl("http://img.youtube.com/vi/" + youtubeKey + "/default.jpg");
            favCardDetails.setVideo(true);
            favCardDetails.setMediaType("YOUTUBE_VIDEO");
            learnngtut_mainimagelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isPackageInstalled = true;
//                    try {
//                        PackageInfo pinf=getActivity().getPackageManager().getPackageInfo("com.google.android.youtube", 0);
//                        String versionName = pinf.versionName;
//                        versionName=versionName.substring(0,2);
//                        int versionNo=Integer.parseInt(versionName);
//                        if(versionNo<11){
//                            confirminstallYoutubePopup(true);
//                        }else {
//                            isPackageInstalled = true;
//                        }
//                    }catch (Exception e){
//                        confirminstallYoutubePopup(false);
//                    }
                    if (isPackageInstalled) {
                        OustSdkTools.stopSpeech();
                        learnngtut_mainimagelayout.setVisibility(View.GONE);
                        learningtut_videoframelayout.setVisibility(View.VISIBLE);
                        initYouTubeView();
                    }
                }
            });
            //if audio is running stop and hide
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                mediaPlayer.stop();
            }
            optionmain_speaker.setVisibility(View.GONE);
            audioFileName = null;
            optionmain_speaker.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void confirminstallYoutubePopup(boolean isUpdate) {
        try {
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.ask_infopopup, null);
            final PopupWindow installYoutubePopup = OustSdkTools.createPopUp(popUpView);
            final LinearLayout infopopup_animlayout = popUpView.findViewById(R.id.infopopup_animlayout);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            if (isUpdate) {
                popupContent.setText(OustStrings.getString("update_youtube"));
            } else {
                popupContent.setText(OustStrings.getString("install_youtube"));
            }
            btnYes.setText(OustStrings.getString("yes"));
            btnNo.setText(OustStrings.getString("no"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.google.android.youtube")));
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installYoutubePopup.dismiss();
                }
            });
            OustSdkTools.popupAppearEffect(infopopup_animlayout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean fullScreen = false;

    private void initYouTubeView() {
        try {
            Log.d(TAG, "initYouTubeView:" + youtubeKey);
            isYoutubeInitialized = true;
            getLifecycle().addObserver(learningtut_videoframelayout);

            YouTubePlayerListener youTubePlayerListener = (new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    //String videoId = "S0Q4gqBUs7c";
                    Log.d(TAG, "initYouTubeView -> onReady: " + youtubeKey);
                    youTubePlayer.loadVideo(youtubeKey, 0f);
                }
            });

            learningtut_videoframelayout.initialize(youTubePlayerListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void initYouTubeView() {
        try {
            if (youTubePlayerFragment == null) {
                isYoutubeInitialized = true;
                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.learningtut_videoframelayout, youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize(youtubeKey, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                        if (!wasRestored) {
                            videoPlayer = player;
                            videoPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                            videoPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                            videoPlayer.loadVideo(youtubeKey);
                            videoPlayer.play();
                            videoPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                @Override
                                public void onFullscreen(boolean _isFullScreen) {
                                    fullScreen = _isFullScreen;
                                    if (fullScreen) {
                                        setLandscapeVid(false);
                                    } else {
                                        setPotraitVid(false);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                        // YouTube error
                        String errorMessage = error.toString();
                        Log.d("errorMessage:", errorMessage);
                    }
                });
            } else {
                youTubePlayerFragment.onResume();
            }
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if (fullScreen) {
                            videoPlayer.setFullscreen(false);
                            return true;
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void resumeVideoPlayer() {
        if (path != null && (!path.isEmpty()) && (!OustSdkTools.isReadMoreFragmentVisible)) {
            if (!courseCardClass.isPotraitModeVideo()) {
                learningModuleInterface.changeOrientationUnSpecific();
            }
            startVideoPlayer(path);
        }
    }

    private void setLandscapeVid(boolean isConfigChange) {
        try {
            if (!courseCardClass.isPotraitModeVideo()) {
                if (!isConfigChange) {
                    learningModuleInterface.changeOrientationLandscape();
                    //learningModuleInterface.changeOrientationUnSpecific();
                }
            }
            moduleoverview_toplayout.setVisibility(View.GONE);
            solution_headertext.setVisibility(View.GONE);
            solution_desc.setVisibility(View.GONE);
            learningcard_tuta.setVisibility(View.GONE);
            bottomswipe_view.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPotraitVid(boolean isConfigChange) {
        try {
            if (!isConfigChange) {
                learningModuleInterface.changeOrientationPortrait();
                //learningModuleInterface.changeOrientationUnSpecific();
            }
            //learningModuleInterface.changeOrientationPortrait();
            moduleoverview_toplayout.setVisibility(View.VISIBLE);
            if (isComingFromFeed) {
//                learningcard_progress.setBackgroundColor(Color.TRANSPARENT);
                moduleoverview_toplayout.setBackgroundColor(Color.TRANSPARENT);
//                top_icons.setVisibility(View.GONE);
                questionmore_btn.setVisibility(View.GONE);
                learningcard_coursename.setVisibility(View.GONE);
                bottomswipe_view.setVisibility(View.GONE);
            }
            if ((courseCardClass != null) && (courseCardClass.getCardTitle() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                solution_headertext.setVisibility(View.VISIBLE);
            }
            if ((courseCardClass != null) && (courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty())) {
                solution_desc.setVisibility(View.VISIBLE);
            }
            if (containImage) {
                learningcard_tuta.setVisibility(View.VISIBLE);
            }
            if (!isComingFromFeed) {
                bottomswipe_view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //------------------------------------------------------------------------------------------------------
    //s3 video stream
    private MediaController media_Controller;
    private int finalTime, currentTime;
    private ImageView start_videobutton;

    private ProgressBar learningcard_videoloader;


    //------------------
    private boolean tochedScreen = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tochedScreen = true;
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (tochedScreen) {
                        tochedScreen = false;
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x1 - x2;
                        float deltaY = y1 - y2;
                        if (deltaX > 0 && deltaY > 0) {
                            if (deltaX > deltaY) {
                                if (deltaX > MIN_DISTANCE) {
                                    gotoNextScrren();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    gotoPreviousSceen();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    gotoPreviousSceen();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    gotoNextScrren();
                                } else {
                                    detectClickedView(v);
                                }
                            } else {
                                detectClickedView(v);
                            }
                        } else {
                            detectClickedView(v);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return true;
    }

    private AdaptiveLearningModuleInterface adaptiveLearningModuleInterface;

    public void setAdaptiveLearningModuleInterface(AdaptiveLearningModuleInterface learningModuleInterface) {
        this.adaptiveLearningModuleInterface = learningModuleInterface;
    }

    private void gotoNextScrren() {
        if (OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
            learningcard_videoloader.setVisibility(View.GONE);
            video_player_layout.setVisibility(View.GONE);
            learningtut_videoframelayout.setVisibility(View.GONE);
            if (fullScreen) {
                fullScreen = false;
                setPotraitVid(false);
            }

            /*if (youTubePlayerFragment != null) {
                youTubePlayerFragment.onDestroy();
                youTubePlayerFragment = null;
            }*/
            if (learningModuleInterface != null && OustStaticVariableHandling.getInstance().isLearniCardSwipeble()) {
                learningModuleInterface.gotoNextScreen();
                nextScreenAtOnce();
            }
        }
    }

    private void gotoPreviousSceen() {
        //if (isSwipeAllowed) {
        learningcard_videoloader.setVisibility(View.GONE);
        video_player_layout.setVisibility(View.GONE);
//            simpleExoPlayerView.setVisibility(View.GONE);
        learningtut_videoframelayout.setVisibility(View.GONE);
        //if (isSwipeAllowed)
        //   learningModuleInterface.gotoPreviousScreen(); //commented this
        if (fullScreen) {
            fullScreen = false;
            setPotraitVideoRatio();
            setPotraitVid(false);
        }
            /*if (youTubePlayerFragment != null) {
                youTubePlayerFragment.onDestroy();
                youTubePlayerFragment = null;
            }*/
        // adaptiveLearningModuleInterface.previousScreen(); //commented this
        //}
    }

    private boolean isAudioPlaying = true;

    private void detectClickedView(View v) {
        int id = v.getId();
        if (id == R.id.gotopreviousscreen_mainbtn) {
            gotoPreviousSceen();
        } else if (id == R.id.gotonextscreen_mainbtn) {
            gotoNextScrren();
        } else if (id == R.id.optionmain_speaker) {
            if ((audioFileName == null) || ((audioFileName != null) && (audioFileName.isEmpty()))) {
                try {
                    String speakStr = "";
                    if (courseCardClass.getCardTitle() != null) {
                        Spanned s1 = getSpannedContent(courseCardClass.getCardTitle());
                        speakStr += (s1.toString());
                        speakStr += ". ";
                    }
                    if (courseCardClass.getContent() != null) {
                        Spanned s1 = getSpannedContent(courseCardClass.getContent());
                        speakStr += (s1.toString());
                    }
                    if (isAudioPlaying && (!isAudioPausedFromOpenReadmore)) {
                        isAudioPlaying = false;
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                            optionmain_speaker.setAnimation(null);
                        }
                    } else {
                        isAudioPausedFromOpenReadmore = false;
                        isAudioPlaying = true;
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                        speakString(speakStr);
                    }
                } catch (Exception e) {
                }
            } else {
                if (musicComplete) {
                    OustSdkTools.oustTouchEffect(v, 50);
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.stop();
                    }
                    optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                    restartAudio();
                } else {
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.pause();
                        optionmain_speaker.setAnimation(null);
                        optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                        OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                    } else if ((mediaPlayer != null)) {
                        mediaPlayer.start();
                        try {
                            isAudioPausedFromOpenReadmore = false;
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
//                            OustPreferences.saveAppInstallVariable("isttssounddisable", true);
                            scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                            optionmain_speaker.startAnimation(scaleanim);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        } else if (id == R.id.solutionMain_imagea) {
            try {
                if (!OustSdkTools.isReadMoreFragmentVisible) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    //showZoomPopUp(solutionMain_imagea.getDrawable());
                    OustSdkTools.gifZoomPopup(solutionMain_imagea.getDrawable(), getActivity(), this);
                    Log.d(TAG, "detectClickedView: height: " + solutionMain_imagea.getHeight() + " width:" + solutionMain_imagea.getMeasuredWidth());
                    zoomImagePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            Log.d(TAG, "onDismiss: of image zoom:");
                            if (drawableGif != null)
                                solutionMain_imagea.setImageDrawable(drawableGif);
                        }
                    });
                }
            } catch (Exception e) {
                if (solutionMain_imagea.getDrawable() != null) {

                }
            }
        } else if (id == R.id.solutionMain_imageb) {
            try {
                if (!OustSdkTools.isReadMoreFragmentVisible) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    //showZoomPopUp(solutionMain_imageb.getDrawable());
                    OustSdkTools.gifZoomPopup(solutionMain_imageb.getDrawable(), getActivity(), this);
                }
            } catch (Exception e) {
            }
        } else if (id == R.id.learning_halfimgage) {
            try {
                if (!OustSdkTools.isReadMoreFragmentVisible) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    //showZoomPopUp(learning_halfimgage.getDrawable());
                    OustSdkTools.gifZoomPopup(learning_halfimgage.getDrawable(), getActivity(), this);
                }
            } catch (Exception e) {
            }
        } else if (id == R.id.learning_fullimgage) {
            try {
                if (!OustSdkTools.isReadMoreFragmentVisible) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(learning_fullimgage.getDrawable(), getActivity(), this);
                    //gifZoomPopup2(learning_fullimgage.getDrawable(), getActivity(), this);
                    zoomImagePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            Log.d(TAG, "onDismiss: of image zoom:");
                            if (drawableGif != null)
                                learning_fullimgage.setImageDrawable(drawableGif);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } else if (id == R.id.questionmore_btn) {
            learningModuleInterface.showCourseInfo();
        } else if (id == R.id.unfavourite) {
            if (!isfavouriteClicked) {
                isfavouriteClicked = true;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                unfavourite.setColorFilter(getResources().getColor(R.color.Orange));
                addDataTofavCardList();
                learningModuleInterface.setFavCardDetails(favCardDetailsList);
                learningModuleInterface.setFavoriteStatus(isfavouriteClicked);
            } else {
                isfavouriteClicked = false;
                unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                unfavourite.setColorFilter(getResources().getColor(R.color.whitea));
                learningModuleInterface.setFavoriteStatus(isfavouriteClicked);
            }
        } else if (id == R.id.rm_share_layout) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);

        } else if (id == R.id.bulletin_layout) {
            try {
                if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                    isAudioPausedFromOpenReadmore = true;
//                    OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                    mediaPlayer.pause();
                    optionmain_speaker.setAnimation(null);
                    optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                }
                if (OustSdkTools.textToSpeech != null) {
//                    OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                    isAudioPausedFromOpenReadmore = true;
                    OustSdkTools.stopSpeech();
                    optionmain_speaker.setAnimation(null);
                    optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));

                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            Intent intent = new Intent(getActivity(), BulletinBoardQuestionActivity.class);
            if (courseId != null) {
                intent.putExtra("courseId", "" + courseId);
            }
            if (courseName != null) {
                intent.putExtra("courseName", courseName);
            }
            OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
            startActivity(intent);
        } else if (id == R.id.video_landscape_zoom) {
            if (!OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
//                isVideoFullScreen = true;
                changeToPortraitFullScreen();
            } else {
//                isVideoFullScreen = false;
                minimizePortraitScreen();
            }
        }

    }

    private Drawable drawableGif;

    private void setDrawable(Drawable gif) {
        drawableGif = gif;
    }

    public void gifZoomPopup2(final Drawable gif, final Activity activity2, final DialogKeyListener dialogKeyListener) {
        try {
            if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {

            } else {
                View popUpView = activity2.getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
                zoomImagePopup = OustSdkTools.createPopUp(popUpView);
                mainzooming_img = popUpView.findViewById(R.id.mainzooming_img);
                mainzooming_img.setImageDrawable(gif);
                PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(mainzooming_img);
                ImageButton zooming_imgclose_btn = popUpView.findViewById(R.id.zooming_imgclose_btn);
                final RelativeLayout mainzoomimg_layout = popUpView.findViewById(R.id.mainzoomimg_layout);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 0.0f, 1);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 0.0f, 1);
                scaleDownX.setDuration(400);
                scaleDownY.setDuration(400);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                mainzoomimg_layout.setVisibility(View.VISIBLE);

                zoomImagePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (dialogKeyListener != null) {
                            dialogKeyListener.onDialogClose();
                        }
                    }
                });

                zooming_imgclose_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                        scaleDownX.setDuration(350);
                        scaleDownY.setDuration(350);
                        scaleDownX.setInterpolator(new DecelerateInterpolator());
                        scaleDownY.setInterpolator(new DecelerateInterpolator());
                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);
                        scaleDown.start();
                        scaleDown.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {
                                    zoomImagePopup.dismiss();
                                    setDrawable(mainzooming_img.getDrawable());
                                   /* ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                                    fragment.showAllMediaFullImage();
                                    fragment.setTopContent();
                                    fragment.setTopContentTitle();*/
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });
                    }
                });
                popUpView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                            scaleDownX.setDuration(350);
                            scaleDownY.setDuration(350);
                            scaleDownX.setInterpolator(new DecelerateInterpolator());
                            scaleDownY.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet scaleDown = new AnimatorSet();
                            scaleDown.play(scaleDownX).with(scaleDownY);
                            scaleDown.start();
                            scaleDown.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    if ((zoomImagePopup != null) && (zoomImagePopup.isShowing())) {
                                        zoomImagePopup.dismiss();
                                        setDrawable(mainzooming_img.getDrawable());
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
                    }
                });
            }

        } catch (Exception e) {
        }

    }

    private void showZoomPopUp(Drawable gif) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.gifzoom_popup, null);
            mPopupWindowZoomImage = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // display the popup in the center
            mPopupWindowZoomImage.showAtLocation(mLinearLayoutRoot, Gravity.CENTER, 0, 0);
            GifImageView mainzooming_img = layout.findViewById(R.id.mainzooming_img);
            // layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mainzooming_img.setImageDrawable(gif);
            ImageButton imageButtonClose = layout.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout mainzoomimg_layout = layout.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            mainzoomimg_layout.setVisibility(View.VISIBLE);
            imageButtonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                    scaleDownX.setDuration(350);
                    scaleDownY.setDuration(350);
                    scaleDownX.setInterpolator(new DecelerateInterpolator());
                    scaleDownY.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();
                    scaleDown.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mPopupWindowZoomImage.dismiss();
                            showAllMediaFullImage();
                            Log.d(TAG, "onAnimationEnd: module");
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void zoomAlert(Drawable gif) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View popUpView = getActivity().getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
        builder.setView(popUpView);
        GifImageView mainzooming_img = popUpView.findViewById(R.id.mainzooming_img);
        popUpView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mainzooming_img.setImageDrawable(gif);
        ImageButton imageButtonClose = popUpView.findViewById(R.id.zooming_imgclose_btn);
        final android.app.AlertDialog alertDialog = builder.create();
        final RelativeLayout mainzoomimg_layout = popUpView.findViewById(R.id.mainzoomimg_layout);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 0.0f, 1);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 0.0f, 1);
        scaleDownX.setDuration(400);
        scaleDownY.setDuration(400);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        mainzoomimg_layout.setVisibility(View.VISIBLE);
        alertDialog.show();
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mainzoomimg_layout, "scaleY", 1.0f, 0);
                scaleDownX.setDuration(350);
                scaleDownY.setDuration(350);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
                scaleDown.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        alertDialog.dismiss();
                        showAllMediaFullImage();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });


            }
        });
    }

    private void minimizePortraitScreen() {
        OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
        setPotraitVid(true);
        setPotraitVideoRatio();
    }

    private void changeToPortraitFullScreen() {
        OustStaticVariableHandling.getInstance().setVideoFullScreen(true);
        setLandscapeVid(true);
        setPotraitVideoRatioFullScreen();
    }


    private void shareScreenShot() {
        Bitmap bitmap = OustSdkTools.getScreenShot(moduleoverview_animviewa);
        String branchLinkUrl = "http://bit.ly/1xEh2HW";
        shareScreenUsingIntent(getActivity(), branchLinkUrl, bitmap);
    }

    public void shareScreenUsingIntent(Context cx, String branchLink, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "card_screenshot", null);
        Uri imageUri = Uri.parse(path);

        String oustShareMessage = OustStrings.getString("cpmmonsharetext");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, branchLink);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, oustShareMessage);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg,text/plain");

        cx.startActivity(Intent.createChooser(shareIntent, "Share via"));

    }


    boolean isShareClicked = false;

    private void addDataTofavCardList() {
        try {
            if ((courseCardClass.getCardTitle() != null) && (!courseCardClass.getCardTitle().isEmpty())) {
                favCardDetails.setCardTitle(courseCardClass.getCardTitle());
            } else favCardDetails.setCardTitle("");
            if ((courseCardClass.getContent() != null) && (!courseCardClass.getContent().isEmpty())) {
                favCardDetails.setCardDescription(courseCardClass.getContent());
            } else favCardDetails.setCardDescription("");
            if ((favCardDetails.getImageUrl() != null) && (!favCardDetails.getImageUrl().isEmpty())) {

            } else favCardDetails.setImageUrl("");
            favCardDetails.setCardId(cardId);

            if (!favCardDetails.isAudio()) {
                favCardDetails.setAudio(false);
            }
            if (!favCardDetails.isVideo()) {
                favCardDetails.setVideo(false);
            }

            favCardDetailsList.add(favCardDetails);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //---------------------------------
    public void showAllMediaFullImage() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                setGifFullImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                favCardDetails.setMediaType("GIF");
                                break;
                            case "IMAGE":
                                setFullImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                favCardDetails.setMediaType("IMAGE");
                                break;
                            case "AUDIO":
                                audioFileName = "oustlearn_" + courseCardMedia.getData();
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
            }
            if ((audioFileName != null) && (!audioFileName.isEmpty())) {
                playDownloadedAudioQues(audioFileName);
            } else {
                speakAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGifFullImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                learning_fullimgage.setImageDrawable(gifFromBytes);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                learning_fullimgage.setImageURI(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFullImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                learning_fullimgage.setImageBitmap(decodedByte);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Picasso.get().load(file).into(learning_fullimgage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showAllMediaHalfImage() {
        try {
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null)) {
                for (int i = 0; i < courseCardClass.getCardMedia().size(); i++) {
                    DTOCourseCardMedia courseCardMedia = courseCardClass.getCardMedia().get(i);
                    if ((courseCardMedia != null) && (courseCardMedia.getMediaType() != null)) {
                        switch (courseCardMedia.getMediaType()) {
                            case "GIF":
                                setGifHalfImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                favCardDetails.setMediaType("GIF");
                                break;
                            case "IMAGE":
                                setHalfImage("oustlearn_" + courseCardMedia.getData());
                                favCardDetails.setImageUrl(courseCardMedia.getData());
                                favCardDetails.setMediaType("IMAGE");
                                break;
                            case "AUDIO":
                                audioFileName = "oustlearn_" + courseCardMedia.getData();
                                favCardDetails.setAudio(true);
                                break;
                        }
                    }
                }
            }
            if ((audioFileName != null) && (!audioFileName.isEmpty())) {
                playDownloadedAudioQues(audioFileName);
            } else {
                speakAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setGifHalfImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                GifDrawable gifFromBytes = new GifDrawable(imageByte);
//                learning_halfimgage.setImageDrawable(gifFromBytes);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                learning_halfimgage.setImageURI(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setHalfImage(String fileName) {
        try {
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            String audStr = enternalPrivateStorage.readSavedData(fileName);
//            if ((audStr != null) && (!audStr.isEmpty())) {
//                byte[] imageByte = Base64.decode(audStr, 0);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                learning_halfimgage.setImageBitmap(decodedByte);
//            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
            if (file != null && file.exists()) {
                Picasso.get().load(file).into(learning_halfimgage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void speakString(String str) {
        try {
            str = str.replaceAll("[_]+", "    ");
            int n1 = str.length();
            if (n1 > 100)
                n1 = 100;
            boolean isHindi = false;
            for (int i = 0; i < n1; i++) {
                int no = str.charAt(i);
                if (no > 2300 && n1 < 3000) {
                    isHindi = true;
                    break;
                }
            }
            TextToSpeech textToSpeech = OustSdkTools.getSpeechEngin();
            if (isHindi) {
                textToSpeech = OustSdkTools.getHindiSpeechEngin();
            }
            if (textToSpeech != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            float count = str.length() / 20;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(optionmain_speaker, "scaleX", 1, 0.75f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(optionmain_speaker, "scaleY", 1, 0.75f);
            scaleDownX.setDuration(1000);
            scaleDownY.setDuration(1000);
            scaleDownX.setRepeatCount((int) count);
            scaleDownY.setRepeatCount((int) count);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isReadMoreOpen = false;
    private boolean isAudioPausedFromOpenReadmore = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            Log.d(TAG, "Permision for RequestCode:" + requestCode);
            int sdk = Build.VERSION.SDK_INT;
            if (requestCode == 102) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startDownloadingVideo();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            final Snackbar snackbar = Snackbar
                                    .make(mLinearLayoutRoot, Html.fromHtml("<font color=\"#ffffff\">Permission denied to store this video.</font>"), Snackbar.LENGTH_LONG)
                                    .setAction("Grant Access", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                                        }
                                    });
                            snackbar.show();
                        } else {
                            Toast.makeText(getActivity(), "Permission denied to store this video.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            if (requestCode == 105) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkVideoMediaExist();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            final Snackbar snackbar = Snackbar
                                    .make(mLinearLayoutRoot, Html.fromHtml("<font color=\"#ffffff\">Permission denied to store this video.</font>"), Snackbar.LENGTH_LONG)
                                    .setAction("Grant Access", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
                                        }
                                    });
                            snackbar.show();
                        } else {
                            Toast.makeText(getActivity(), "Permission denied to store this video.", Toast.LENGTH_LONG).show();
                        }

                        startPlayingSignedUrlVideo();
                    }
                }
            }
            if (requestCode == 107) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initializeExoPlayer();
                    }
                }
            }

            if (requestCode == 103) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        isShareClicked = true;
                        learningModuleInterface.setShareClicked(true);
                        shareScreenShot();
                    }
                }
            }

            if (requestCode == 120) {
                Log.e("PDF", "inside onRequestPermissionsResult 120");
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("PDF", "inside onRequestPermissionsResult 120 PERMISSION_GRANTED");
                        OustSdkTools tools = new OustSdkTools();
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                optionmain_speaker.setAnimation(null);
//                                OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                                isAudioPausedFromOpenReadmore = true;
                                optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            }
                        }
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
//                            OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                            isAudioPausedFromOpenReadmore = true;
                            optionmain_speaker.setAnimation(null);
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }
                        handleVideosOnPause();
                        if ((courseCardClass.getReadMoreData().getType() != null) && (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf"))) {
                            isReadMoreOpen = true;
                            learningModuleInterface.setShareClicked(true);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            if (sdk > Build.VERSION_CODES.M) {
                                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".com.oustme.oustapp.provider", OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData()));
                                intent.setDataAndType(fileUri, "application/pdf");
                            } else {

                                File file = OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData());
                                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            }
                            getActivity().startActivity(intent);
                        } else {
                            DTOReadMore readData = courseCardClass.getReadMoreData();
                            learningModuleInterface.setShareClicked(true);
                            if ((readData.getType() != null) && (readData.getType().equalsIgnoreCase("IMAGE"))) {
                                learningModuleInterface.changeOrientationUnSpecific();
                            }
                            isReadMoreOpen = true;
                            //learningModuleInterface.openReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, courseCardClass);
                        }
                    }
                }
            }
            if (requestCode == 121) {
                Log.e("PDF", "inside onRequestPermissionsResult 121");
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("PDF", "inside onRequestPermissionsResult 121 PERMISSION_GRANTED");
                        OustSdkTools tools = new OustSdkTools();
                        if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
//                            OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                            isAudioPlaying = false;
                            mediaPlayer.pause();
                            optionmain_speaker.setAnimation(null);
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }

                        if (OustSdkTools.textToSpeech != null) {
//                            OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                            isAudioPlaying = false;
                            OustSdkTools.stopSpeech();
                            optionmain_speaker.setAnimation(null);
                            optionmain_speaker.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }
                        handleVideosOnPause();

                        if ((courseCardClass.getReadMoreData().getType() != null) && (courseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf"))) {
                            learningModuleInterface.setShareClicked(true);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            if (sdk > Build.VERSION_CODES.M) {
                                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".provider", OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData()));
                                Log.e("PDF", "fileUri not null " + fileUri);
                                intent.setDataAndType(fileUri, "application/pdf");
                            } else {
                                File file = OustSdkTools.getDataFromPrivateStorage(courseCardClass.getReadMoreData().getData());
                                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            }
                            if (getActivity() != null) {
                                getActivity().startActivity(intent);
                            }
                        } else {
                            DTOReadMore readData = courseCardClass.getReadMoreData();
                            learningModuleInterface.setShareClicked(true);
                            if ((readData.getType() != null) && (readData.getType().equalsIgnoreCase("IMAGE"))) {
                                learningModuleInterface.changeOrientationUnSpecific();
                            }

                            mOpenReadMore.adaptiveOpenReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, courseCardClass);
//
                            //tools.showReadMorePopup(getActivity(), mUrl1, courseCardClass.getReadMoreData(), ModuleOverViewFragment.this, isRMFavourite, courseId);
                        }

                    }
                }
            }
        } catch (Exception e) {
            Log.e("PDF", "inside onRequestPermissionsResult Exception ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downLoadVideo(final String fileName1, String pathName) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                return;
            }
            Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim);
            learningcard_videoloader.startAnimation(rotateAnim);
            learningcard_videoloader.setVisibility(View.VISIBLE);
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = pathName;
            String path = "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + fileName1;
            File file = new File(Environment.getExternalStorageDirectory(), path);
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download(S3_BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            String path1 = "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                            String path = Environment.getExternalStorageDirectory() + path1;
                            startVideoPlayer(path);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            learningcard_videoloader.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                    }
                });
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void reInitialize() {
        if (!isYoutubeInitialized) {
            initYouTubeView();
            if (videoPlayer != null) {
                //videoPlayer.seekToMillis(youtubeElapseTime);
                videoPlayer.pause();
            }
        }
    }

//    =======================================================================

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    onLinkClick(link[0].getURL());
                    return false;
                }
            }
            return true;
        }

        abstract public void onLinkClick(String url);
    }

    @Override
    public void favouriteClicked(boolean isRMFavourite) {
        try {
            learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            FavCardDetails favCardDetails = new FavCardDetails();
            this.isRMFavourite = isRMFavourite;
            if (isRMFavourite) {
                favCardDetails.setCardId("" + courseCardClass.getCardId());
                favCardDetails.setRmId(courseCardClass.getReadMoreData().getRmId());
                favCardDetails.setRmData(courseCardClass.getReadMoreData().getData());
                favCardDetails.setRMCard(true);
                favCardDetails.setRmDisplayText(courseCardClass.getReadMoreData().getDisplayText());
                favCardDetails.setRmScope(courseCardClass.getReadMoreData().getScope());
                favCardDetails.setRmType(courseCardClass.getReadMoreData().getType());

                favCardDetailsList.add(favCardDetails);
                learningModuleInterface.setFavCardDetails(favCardDetailsList);

            } else {
                learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //==============================================================================================================================================================
//video related methode
    private String videoFileName;

    private void streamStoredVideo(final String videoFileName1) {
        try {
            if (!courseCardClass.isPotraitModeVideo()) {
                quesvideoLayout.setVisibility(View.VISIBLE);
            }
            this.videoFileName = videoFileName1;
            start_videobutton.setClickable(true);
            if (!fastForwardMedia) {
                start_videobutton.setVisibility(View.GONE);
                checkVideoMediaExist();
            } else if (courseCardClass.isPotraitModeVideo()) {
                quesvideoLayout.setVisibility(View.VISIBLE);
                start_videobutton.setVisibility(View.VISIBLE);
            }
            start_videobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start_videobutton.setClickable(false);
                    start_videobutton.setVisibility(View.GONE);
                    checkVideoMediaExist();
                }
            });
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                File file = new File(path);
                Log.d(TAG, "streamStoredVideo: file exist:" + file.exists());
                if (!file.exists()) {
                    setDownloadBtn();
                } else {
                    downloadvideo_icon.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                }
            } else {
                setDownloadBtn();
            }
            //if audio is running stop and hide
            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                mediaPlayer.stop();
            }
            optionmain_speaker.setVisibility(View.GONE);
            audioFileName = null;
            optionmain_speaker.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    boolean isDownloadButtonclicked = false;

    private void setDownloadBtn() {
        downloadvideo_icon.setVisibility(View.VISIBLE);
        downloadvideo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startDownloadingVideo();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
            }
        });
    }

    private void startDownloadingVideo() {
        DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                try {
                    setDownloadingPercentage(Integer.valueOf(progress), message);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                //  OustSdkTools.showToast(message);
                tempVideoFileName.delete();
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
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
            if (!isVideoDownloding) {
                isVideoDownloding = true;
                isDownloadButtonclicked = true;
//                downloadVideo(("course/media/video/" + videoFileName));
                OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                String path = Environment.getExternalStorageDirectory() + "/Android/data/com.oustme.oustapp/files";
                tempVideoFileName = new File(path);
                String downloadPath = CLOUD_FRONT_VIDEO_BASE + videoFileName;
                //downloadFiles.startDownLoad(downloadPath, path, videoFileName,false);

                sendToDownloadService(OustSdkApplication.getContext(), downloadPath, path, videoFileName, false);
                // Intent intent = new Intent(getActivity(), DownloadVideoService.class);
                // intent.putExtra("videoFileName", videoFileName);
                // getActivity().startService(intent);
            }
        } else {
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    private void sendToDownloadService(Context context, String downloadPath, String destn, String fileName, boolean isOustLearn) {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
        //DownLoadIntentService.setContext(this);
        context.startService(intent);
        isFileDownloadServiceStarted = true;
    }

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if (getActivity() != null) {
            getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isFileDownloadServiceStarted) {
                    if (intent.getAction() != null) {
                        try {
                            if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                                setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                                final File finalFile = new File(path);
                                if (tempVideoFileName != null && tempVideoFileName.exists()) {
                                    tempVideoFileName.renameTo(finalFile);
                                }
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                                OustSdkTools.showToast(intent.getStringExtra("MSG") + " Please try again");
                                if (tempVideoFileName != null && tempVideoFileName.exists()) {
                                    tempVideoFileName.delete();
                                }
                                OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
                                isVideoDownloding = false;
                                isDownloadButtonclicked = false;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        }
    }

    public class MyVideoDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "vinayak";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setDownloadingPercentage();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void setDownloadingPercentage(int percentage, String message) {
        try {
            if (percentage > 0) {
                if (!isDownloadButtonclicked) {
                    isDownloadButtonclicked = true;
                    OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                }

                if (percentage == 100) {
                    start_videobutton.setClickable(false);
                    OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDownloadButtonclicked = false;
//                    if(currentTime>0) {
//                        checkVideoMediaExist();
//                    }
                            Drawable drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                            if (drawable != null) {
                                downloadvideo_icon.setImageDrawable(drawable);
                            }
                            start_videobutton.setClickable(true);
                        }
                    }, 3000);
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
        }
    }

    private void setDownloadingPercentage() {
        try {
            int percentage = OustPreferences.getSavedInt(videoFileName);
            if (percentage > 0) {
                if (!isDownloadButtonclicked) {
                    isDownloadButtonclicked = true;
                    OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                }

                if (percentage == 100) {
                    OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
                    isDownloadButtonclicked = false;
//                    if(currentTime>0) {
//                        checkVideoMediaExist();
//                    }
                    Drawable drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                    if (drawable != null) {
                        downloadvideo_icon.setImageDrawable(drawable);
                    }
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean isVideoDownloding = false;

    private void checkVideoMediaExist() {
        try {
            Log.d(TAG, "checkVideoMediaExist: ");
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String path1 = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                File file = new File(path1);
                if (!file.exists()) {
                    startPlayingSignedUrlVideo();
                } else {
                    path = path1;
                    start_videobutton.setVisibility(View.GONE);
                    startVideoPlayer(path);
                }
            } else {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
//                startPlayingSignedUrlVideo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startPlayingSignedUrlVideo() {
        if (OustSdkTools.checkInternetStatus()) {
            String filename = videoFileName;
            if (videoFileName.contains(".mp4")) {
                filename = videoFileName.replace(".mp4", "");
            }
            String hlspath = "HLS/" + filename + "-HLS-Segment/" + filename + "-master-playlist.m3u8";
            Log.d(TAG, "startPlayingSignedUrlVideo: " + hlspath);
            isVideoHlsPresentOnS3(hlspath);
//            path = getSignedUrl(("course/media/video/" + videoFileName));
//            startPlayingVideo(signedPath);
//            startVideoPlayer(path);
            start_videobutton.setVisibility(View.GONE);
        } else {
            start_videobutton.setClickable(true);
            start_videobutton.setVisibility(View.VISIBLE);
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }


    private void isVideoHlsPresentOnS3(final String keyName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String bucketName = AppConstants.MediaURLConstants.BUCKET_NAME;
                    String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
                    String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
                    AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                    s3Client.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                    s3Client.getObjectMetadata(bucketName, keyName);
                    playHls(true, keyName);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    playHls(false, "");
                }
            }
        }.start();
    }

    public void playHls(final boolean b, final String s) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playHlsOrNormalSignedUrlVideo(b, s);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void playHlsOrNormalSignedUrlVideo(boolean ishls, String hlsPath) {
        if (ishls) {
            //String signedhlsPath = getSignedUrl(hlsPath);
            ishlsVideo = true;
            //if (signedhlsPath != null && (!signedhlsPath.isEmpty())) {
            path = hlsPath;
            startVideoPlayer(path);
            //}
        } else {
            path = "course/media/video/" + videoFileName;
            startVideoPlayer(path);
        }
        start_videobutton.setVisibility(View.GONE);
    }

    private boolean isVideoPlaying = true;
    private boolean videoCompleted = false;
    private int stopPosition;
    private boolean fastForwardMedia;
    private SimpleExoPlayer simpleExoPlayer;
    DefaultTrackSelector trackSelector;
    long time = 0;
    String path = null;
    boolean ishlsVideo = false;
    private DefaultTimeBar timeBar;
    private boolean isVideoCompleted = false;
    private LinearLayout nointernet_exo_ll;
    int count = 0;

    public void startVideoPlayer(String path) {
        try {
            if (ishlsVideo) {
                try {
                    AWSMobileClient.getInstance().initialize(getActivity(), new AWSStartupHandler() {
                        @Override
                        public void onComplete(AWSStartupResult awsStartupResult) {
                            Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
                        }
                    }).execute();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            if (path.contains(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL)) {
                path = path.replace(AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL, "");
            }
            if (courseCardClass != null && !courseCardClass.isPotraitModeVideo()) {
                quesvideoLayout.setVisibility(View.VISIBLE);
            }
            simpleExoPlayerView = new PlayerView(getActivity());
            simpleExoPlayerView.setLayoutParams(new PlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
            video_player_layout.addView(simpleExoPlayerView);
            if (video_landscape_zoom.getVisibility() == View.VISIBLE) {
                changeToPortraitFullScreen();
                quesvideoLayout.setVisibility(View.VISIBLE);
                showExtraVideoContent();
            } else {
                setPotraitVideoRatio();
            }
            start_videobutton.setVisibility(View.GONE);
            videothumbnail_image.setVisibility(View.GONE);
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            video_player_layout.setVisibility(View.VISIBLE);

            timeBar = simpleExoPlayerView.findViewById(R.id.exo_progress);
            nointernet_exo_ll = simpleExoPlayerView.findViewById(R.id.nointernet_exo_ll);

            /*RelativeLayout layout_exoplayer_new_ui = (RelativeLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_new_ui);
            LinearLayout layout_exoplayer_old_ui = (LinearLayout) simpleExoPlayerView.findViewById(R.id.layout_exoplayer_old_ui);
            layout_exoplayer_new_ui.setVisibility(View.GONE);
            layout_exoplayer_old_ui.setVisibility(View.VISIBLE);*/

            nointernet_exo_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initializeExoPlayer();
                }
            });

            timeBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (fastForwardMedia) {
                        return false;
                    }
                    if (!isReviewMode) {
                        return !isVideoCompleted;
                    }
                    return false;
                }
            });

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

            AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            final DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
            //simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            simpleExoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
            MediaSource videoSource;

            if (ishlsVideo) {
                Log.d(TAG, "startVideoPlayer: HLS playing");
                path = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + path;
                Uri videoUri = Uri.parse(path);
                //DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoplayer2example"), bandwidthMeter);

                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);//, dataSourceFactory, 1, null, null);
                simpleExoPlayer.seekTo(time);
                simpleExoPlayerView.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setMediaSource(videoSource);
                simpleExoPlayer.prepare();

                simpleExoPlayer.addVideoListener(new VideoListener() {
                    @Override
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        //Log.d(TAG, "onVideoSizeChanged: width X Height:"+width+" X "+height);
                    }

                    @Override
                    public void onRenderedFirstFrame() {

                    }
                });

                if (show_quality.getVisibility() == View.GONE) {
                    show_quality.setVisibility(View.VISIBLE);
                }
            } else {
                File file = new File(path);
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

                    DataSource.Factory factory = new DataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return fileDataSource;
                        }
                    };

                    videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));
                    //videoSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, extractorsFactory, null, null);
                } else {
                    Log.d(TAG, "startVideoPlayer: tried to get from file but not availble:");
                    path = AppConstants.StringConstants.CLOUD_FRONT_HLS_BASE_URL + path;
                    Uri videoUri = Uri.parse(path);
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoplayer2example"), bandwidthMeter);
                    videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                    //videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
                }
                simpleExoPlayer.seekTo(time);
                simpleExoPlayerView.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setMediaSource(videoSource);
                simpleExoPlayer.prepare();
                simpleExoPlayer.addVideoListener(new VideoListener() {
                    @Override
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        Log.d(TAG, "onVideoSizeChanged: RES:(WxH):" + width + "X" + height);
                    }

                    @Override
                    public void onRenderedFirstFrame() {

                    }
                });
            }

            ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.d(TAG, "onTracksChanged: ");
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.d("-------", "onLoadingChanged----" + isLoading);
                    try {
                        if (isLoading) {
                            if (nointernet_exo_ll.getVisibility() == View.VISIBLE) {
                                nointernet_exo_ll.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.d("-------", "onPlayerStateChanged-----" + playbackState);

                    if (playbackState == simpleExoPlayer.STATE_IDLE || playbackState == simpleExoPlayer.STATE_ENDED ||
                            !playWhenReady) {
                        if (simpleExoPlayerView != null)
                            simpleExoPlayerView.setKeepScreenOn(false);
                    } else { // STATE_IDLE, STATE_ENDED
                        // This prevents the screen from getting dim/lock
                        if (simpleExoPlayerView != null)
                            simpleExoPlayerView.setKeepScreenOn(true);
                    }

                    if (playbackState == simpleExoPlayer.STATE_READY) {
                        Log.e("-------", "STATE_READY");
                        if (learningcard_videoloader.getVisibility() == View.VISIBLE) {
                            learningcard_videoloader.clearAnimation();
                            learningcard_videoloader.setVisibility(View.GONE);
                        }
                    } else if (playbackState == simpleExoPlayer.STATE_BUFFERING) {
                        Log.d("-------", "STATE_BUFFERING");
                        if (learningcard_videoloader.getVisibility() == View.GONE) {
                            Animation rotateAnim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.rotate_anim);
                            if (rotateAnim != null) {
                                learningcard_videoloader.startAnimation(rotateAnim);
                            }
                            learningcard_videoloader.setVisibility(View.VISIBLE);
                        }
                    } else if (playbackState == simpleExoPlayer.STATE_IDLE) {
                        Log.d("-------", "STATE_IDLE");
                        if (learningcard_videoloader.getVisibility() == View.GONE) {
                            Animation rotateAnim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.rotate_anim);
                            if (rotateAnim != null)
                                learningcard_videoloader.startAnimation(rotateAnim);
                            learningcard_videoloader.setVisibility(View.VISIBLE);
                        }
                    } else if (playbackState == simpleExoPlayer.STATE_ENDED) {
                        Log.d("-------", "STATE_ENDED");
                        isVideoCompleted = true;
                        if (OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
                            minimizePortraitScreen();
                        }
                        if (learningModuleInterface != null) {
                            learningModuleInterface.isLearnCardComplete(true);
                            // adaptiveLearningModuleInterface.
                        }
                        if (learningcard_videoloader.getVisibility() == View.VISIBLE) {
                            learningcard_videoloader.clearAnimation();
                            learningcard_videoloader.setVisibility(View.GONE);
                        }
                        showVideoControls();
                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
                        if (autoPlay)
                            gotoNextScrren();

                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.e("-------", "onPlayerError");
                    learningcard_videoloader.setVisibility(View.INVISIBLE);
                    showNoNetworkVideoControl();
                    if (show_quality.getVisibility() == View.VISIBLE) {
                        show_quality.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }


                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Log.e("-------", "onPlaybackParametersChanged");
                }

                @Override
                public void onSeekProcessed() {

                }
            };
            simpleExoPlayer.addListener(eventListener);
            simpleExoPlayer.setPlayWhenReady(true);
            show_quality.setTag(0);
            show_quality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                    if (mappedTrackInfo != null) {
                        trackSelectionHelper.showSelectionDialog(getActivity(), ((Button) view).getText(), trackSelector.getCurrentMappedTrackInfo(), (int) view.getTag());
                    }
                }
            });
        } catch (Exception e) {
        }

    }

    private void showNoNetworkVideoControl() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nointernet_exo_ll.setVisibility(View.VISIBLE);
            }
        }, 1500);
    }

    private void initializeExoPlayer() {
        try {
            learningcard_videoloader.setVisibility(View.VISIBLE);
            nointernet_exo_ll.setVisibility(View.GONE);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String path1 = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                File file = new File(path1);
                if (!file.exists()) {
                    if (OustSdkTools.checkInternetStatus()) {
                        removeVideoPlayer();
                        resumeVideoPlayer();
                    }
                } else {
                    ishlsVideo = false;
                    path = path1;
                    start_videobutton.setVisibility(View.GONE);
                    removeVideoPlayer();
                    startVideoPlayer(path);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 107);
            }
        } catch (Exception e) {
        }
    }

    private void hideVideoControls() {
        if (!isReviewMode) {
            gotonextscreen_mainbtn.setVisibility(View.GONE);
            gyan_arrowfoword.setVisibility(View.GONE);
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
        }
    }

    private void showVideoControls() {
        if (!isReviewMode) {
            if (progressVal > 0) {
                //gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
            }
            gyan_arrowfoword.setVisibility(View.VISIBLE);
            gotonextscreen_mainbtn.setVisibility(View.VISIBLE);
            //  unfavourite.setVisibility(View.GONE);
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
            simpleExoPlayerView.showController();
            if (mandatory_timer_text.getVisibility() == View.VISIBLE) {
                mandatory_timer_text.setVisibility(View.GONE);
                mandatory_timer_progress.setVisibility(View.GONE);
            }
        }
    }


    public void setPotraitVideoRatio() {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                video_player_layout.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVideoRatioFullScreen() {
        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            float h = metrics.heightPixels;
            params.width = scrWidth;
            params.height = (int) h;
            video_player_layout.setLayoutParams(params);
            simpleExoPlayerView.setLayoutParams(params);
        }
    }

    private void setLandscapeVideoRation() {
        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
            params.height = scrHeight;
            params.width = scrWidth;
            simpleExoPlayerView.setLayoutParams(params);
            video_player_layout.setLayoutParams(params);
            if (simpleExoPlayerView.getVisibility() == View.GONE) {
                videothumbnail_image.setLayoutParams(params);
            }

            myHandler = new Handler();
            myHandler.postDelayed(UpdateVideoTime, 100);
        }
    }

    private Runnable UpdateVideoTime = new Runnable() {
        public void run() {
            if (currentTime > 0) {
                hideLoader();
            }
        }
    };

    public void hideLoader() {
        try {
            if (simpleExoPlayerView != null) {
                simpleExoPlayerView.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                learningcard_videoloader.setVisibility(View.GONE);
                simpleExoPlayerView.bringToFront();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getSignedUrl(String objectKey) {
        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += (2000000);
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AppConstants.MediaURLConstants.BUCKET_NAME, objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setExpiration(expiration);
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        if (url != null) {
            url.toString().replaceAll("https://", "http://");
        }
        return url.toString();
    }

    public void downloadVideo(String pathName) {
        try {
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = pathName;
            String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
            final File file = new File(path);
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            downloadvideo_text.setText("");
                            downloadvideo_icon.setImageDrawable(OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded)));
                            isDownloadButtonclicked = false;
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            OustSdkTools.showToast("Cancel downloading file");
                            learningcard_videoloader.setVisibility(View.GONE);
                            file.delete();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal > 0) {
                            float f1 = (((float) bytesCurrent) / ((float) bytesTotal)) * 100;
                            downloadvideo_text.setText("" + ((int) (f1)) + "%");
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        OustSdkTools.showToast("Error downloading file");
                        file.delete();
                    }
                });
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleVideosOnPause() {
//        time=simpleExoPlayerView.getPlayer().getCurrentPosition();
//        simpleExoPlayerView.getPlayer().release();
//        simpleExoPlayerView.setPlayer(null);
//        Log.e("-------","onPause");

//        if(videoPlayer!=null && videoPlayer.isPlaying()){
//            videoPlayer.pause();
//        }
//        if(videoPlayerView!=null && videoPlayer.isPlaying()){
//            isVideoPlaying = false;
//            stopPosition = videoPlayer.getCurrentPosition();
//            video_stopbtn.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_play_white));
//            videoPlayer.pause();
//        }
    }

    private long answeredSeconds;

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                mandatory_timer_text.setVisibility(View.GONE);
                mandatory_timer_progress.setProgress(0);
                mandatory_timer_progress.setVisibility(View.GONE);
                gyan_arrowfoword.setVisibility(View.VISIBLE);
                //gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
                learningquiz_timertext.setVisibility(View.GONE);
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
                learningModuleInterface.isLearnCardComplete(true);
                if (autoPlay) {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.isLearnCardComplete(true);
                        gotoNextScrren();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            learningquiz_timertext.setText(hms);
            try {
                int progress = (int) (answeredSeconds * 100 / courseCardClass.getMandatoryViewTime());
                mandatory_timer_progress.setProgress(progress);
                mandatory_timer_text.setText("" + answeredSeconds);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void cancleTimer() {
        try {
            if (null != timer)
                timer.cancel();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    LearningCardResponceData learningCardResponce = new LearningCardResponceData();

    public void setAnswer(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        learningCardResponce.setUserAnswer(userAns);
        learningCardResponce.setUserSubjectiveAns(subjectiveResponse);
        learningCardResponce.setCorrect(status);
        learningCardResponce.setXp(oc);
    }

    public void nextScreenAtOnce() {
        try {
            learningCardResponce.setCardSubmitDateTime(System.currentTimeMillis() + "");
            learningCardResponce.setCourseCardId(cardIdInt);
            learningCardResponce.setCourseId(Integer.parseInt(courseId));
            learningCardResponce.setCourseLevelId(levelId);
            //learningCardResponce.setXp(xp);
            adaptiveLearningModuleInterface.updateCardResponseData(learningCardResponce);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
            mediaPlayer.stop();
        }
        adaptiveLearningModuleInterface.nextScreen();
    }
}

