package com.oustme.oustsdk.fragments.courses.adaptive;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.compression.video.Config;
import com.oustme.oustsdk.compression.video.MediaController;
import com.oustme.oustsdk.customviews.AutoFitTextureView;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReadMoreFavouriteCallBack;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.response.course.LearningQuestionData;
import com.oustme.oustsdk.response.course.ReadMoreData;
import com.oustme.oustsdk.room.dto.DTOAdaptiveCardDataModel;
import com.oustme.oustsdk.room.dto.DTOAdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

/*import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;*/

/**
 * Created by shilpysamaddar on 23/08/17.
 */


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AdaptiveMediaUploadQuestionFragment extends Fragment implements View.OnClickListener, ReadMoreFavouriteCallBack {

    private static final String TAG = "MediaUploadQuestionFrag";
    //      recording audio
    private TextView recording_time, mediaupload_mainquestionText, mediaupload_mainquestionTime,
            learningcard_coursename, cardprogress_text, mediaupload_timertext, uploadprogresstext, ocanim_text,
            video_timer_text, record_title_text;
    private KatexView learningcard_coursenameMaths;
    private RelativeLayout record_layout, submit, delete, questionno_layout, mediaupload_mainlayout,
            mediaupload_animviewa, mediaupload_progressbar,
            uploadprogressLayout, audio_layout, ref_image, animoc_layout, mediaupload_solutionlayout,
            rm_share_layout, gotonextscreen_btn,
            main_layout, video_timer_layout, solution_readmore;
    private ImageView record_icon, playrecordedaudio, pauserecordedaudio, stoprecordedaudio,
            mediaupload_mainquestionImage, quiz_backgroundimagea,
            quiz_backgroundimagea_downloaded, questionmore_btn, questionaudio_btn, question_arrowback,
            question_arrowfoword, main_layout_bgd;
    private LinearLayout gotopreviousscreen_mainbtn, gotonextscreen_mainbtn, playrecording_layout,
            bottomswipe_view, timer_layout, ocanim_view;
    private HtmlTextView solution_desc, mediaupload_mainquestion;
    private KatexView solution_descMaths, mediaupload_mainquestionMaths;

    private int scrWidth, scrHeight;
    private ProgressBar mediaupload_progress, uploadprogress, video_progressbar;
    private GifImageView learningquiz_imagequestion;
    private ImageView mediaupload_rightwrongimage;
    private ProgressBar saveImageProgressBar;

    //      camera
    private RelativeLayout camera_layout, click_photo_layout, mu_camera_image_view_layout,
            camera_click, delete_photo, attachphoto_layout, submit_photo, camera_frame_Layout, camera_bottom_view;
    private ImageView rotate_camera_layout, preview_ImageView, click_inside_round;
    private TextView click_text, solution_lable, solution_readmore_text;
    private FrameLayout ap_camera_frame;
    public AutoFitTextureView mTextureView;

    //      video
    private RelativeLayout video_layout, video_view_layout, video_click, delete_video, submit_video,
            quesvideoLayout, video_progressbar_layout, attach_vedio, attach_audio, audio_attach_ll, bottom_button_layout;
    //    private VideoView videoPreview;
    private PlayerView simpleExoPlayerView;
    private TextView video_click_text;
    private LinearLayout play_videorec_layout;
    private ImageView play_video_rec, pause_video_rec, stop_video_rec, video_preview, playonpreview, video_expandbtn, video_stopbtn, showsolution_img, camera_back_layout;
    private String filename;
    private GifImageView downloadvideo_icon;

    //    review_text mode Solution
    private LinearLayout media_solutionlayout;
    private TextView media_solution_label, myresponse_label, myresponse_desc, media_solution_readmore_text;
    private HtmlTextView solution_desc_review;

    private int learningcardProgress = 0;
    private AdaptiveMediaUploadQuestionFragment.CounterClass timer;
    private ActiveUser activeUser;

    private ImageButton solution_closebtn;
    private ScrollView mainoption_scrollview;
    private ImageView lpocimage;
    private boolean isReadMoreOpen = false;

    private AlertDialog mAlertDialogForCompress;
    private static final String COMPRESSED_FOLDER = "oust/compressed/";


    private RelativeLayout player_layout, mediaupload_animviewb, media_solution_readmore;

    private boolean isReviewMode = false;
    private boolean enableGalleryUpload = false;
    private View mediaPreparing;

    public void setIsReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    private int numberOfCards = 0;

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    private String userResponse;

    public void setuserResponseForAssessment(String userResponse) {
        this.userResponse = userResponse;
    }

    String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    private boolean isRMFavourite;

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    private boolean isQuesttsEnabled;

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }

    public void setQuestions(LearningQuestionData questions) {
//        this.questions = questions;
    }

    private AdaptiveCourseLevelModel courseLevelClass;
    int levelID;

    public void setCourseLevelClass(AdaptiveCourseLevelModel courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
        if (courseLevelClass != null)
            levelID = (int) this.courseLevelClass.getLevelId();
    }

    private DTOAdaptiveCardDataModel mainCourseCardClass;
    private long questionXp = 20;

    public void setMainCourseCardClass(DTOAdaptiveCardDataModel courseCardClass2) {

        int savedCardID = (int) courseCardClass2.getCardId();
        this.mainCourseCardClass = courseCardClass2;
        try {
            this.questionXp = courseCardClass2.getXp();
            // this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            if (this.mainCourseCardClass.getXp() == 0) {
                this.mainCourseCardClass.setXp(100);
            }
        } catch (Exception e) {
            this.mainCourseCardClass = courseCardClass2;
        }
//        questions = this.mainCourseCardClass.getQuestionData();
        questions = this.mainCourseCardClass.getQuestionData();
        if (questions == null) {
            if (learningModuleInterface != null) {
                learningModuleInterface.endActivity();
            }
        }
        if (isAssessmentQuestion) {
            mainCourseCardClass.setXp(questionXp);
        }
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private LearningModuleInterface learningModuleInterface;
    private AdaptiveLearningModuleInterface adaptiveLearningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setAdaptiveLearningModuleInterface(AdaptiveLearningModuleInterface learningModuleInterface) {
        this.adaptiveLearningModuleInterface = learningModuleInterface;
    }

    private String cardId;
    private List<FavCardDetails> favCardDetailsList;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList, String cardId) {
        this.favCardDetailsList = favCardDetailsList;
        this.cardId = cardId;
    }

    private boolean zeroXpForQCard;

    public void setZeroXpForQCard(boolean zeroXpForQCard) {
        this.zeroXpForQCard = zeroXpForQCard;
    }


    private DTOAdaptiveQuestionData questions;

    public void setQuestions(DTOAdaptiveQuestionData questions) {
        this.questions = questions;
    }

    private boolean isAssessmentQuestion = false;

    public void setAssessmentQuestion(boolean assessmentQuestion) {
        isAssessmentQuestion = assessmentQuestion;
    }

    private boolean isAssessmentQuestionReviewMode = false;

    public void setAssessmentQuestionReviewMode(boolean assessmentQuestion) {
        isAssessmentQuestionReviewMode = assessmentQuestion;
    }

    private Scores assessmentScore;

    public void setAssessmentScore(Scores assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    private boolean showNavigateArrow;

    public void setShowNavigateArrow(boolean showNavigateArrow) {
        this.showNavigateArrow = showNavigateArrow;
    }

    private int cardCount;

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_uploadmediaquestion, container, false);
        initViews(view);
        if (OustAppState.getInstance().getActiveUser() == null) {
            getActivity().finish();
        }
        enableSwipe();
        myHandler = new Handler();
        activeUser = OustAppState.getInstance().getActiveUser();
        getWidth();
        setFontStyle();
        setStartingData();
        setQuestionNo();
        setColors();
        Bundle bundle = getArguments();
        if (bundle != null)
            enableGalleryUpload = bundle.getBoolean("enableGalleryUpload");

        if (enableGalleryUpload) {
            attach_audio.setVisibility(View.VISIBLE);
            attach_vedio.setVisibility(View.VISIBLE);
            attachphoto_layout.setVisibility(View.VISIBLE);
        } else {
            attach_audio.setVisibility(View.GONE);
            attach_vedio.setVisibility(View.GONE);
            attachphoto_layout.setVisibility(View.GONE);

        }

        return view;
    }


    private int count = 0;

    @Override
    public void onResume() {
        try {
            Log.e("PDF", " inside onResume");
            if (isReadMoreOpen && videomediaFile != null && !OustSdkTools.isReadMoreFragmentVisible) {
//            the next two lines are just to handle pdf. because in case of pdf when we launch intent
//            on Pause() and onResume() is called two times once after launching intent an donce when coming
//            back from external app.
                if (isPdfOpen && count == 0) {
                    count++;
                } else {
                    count = 0;
                    isPdfOpen = false;
                    isReadMoreOpen = false;
                    learningModuleInterface.changeOrientationPortrait();
                    Log.e("PDF", " inside onResume and inside first if condition");
                    if (videomediaFile.exists()) {
                        startVedioPlayer();
                        Log.e("PDF", " inside onResume and videomediaFile exists");
                    } else if (isReviewMode && videomediaFile.getPath().contains("amazonaws.com/")) {
                        Log.e("PDF", " inside onResume and videomediaFile does not exists");
                        startVedioPlayer();
                    }
                }
            } else {
                Log.e("PDF", " inside onResume and first if condition failed" + isReadMoreOpen + " ");
            }
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resumeVideoPlayer() {
        if (videomediaFile != null && (!OustSdkTools.isReadMoreFragmentVisible)) {
            isReadMoreOpen = false;
//            OustSdkTools.showToast("onResume() of Module Over View Fragment called");
            if (videomediaFile.exists()) {
                startVedioPlayer();
            } else if (isReviewMode && videomediaFile.getPath().contains("amazonaws.com/")) {
                startVedioPlayer();
            }
        }
    }

    private void initViews(View view) {
        recording_time = view.findViewById(R.id.recording_time);
        record_layout = view.findViewById(R.id.record_layout);
        submit = view.findViewById(R.id.submit);
        delete = view.findViewById(R.id.delete);
        record_icon = view.findViewById(R.id.record_icon);
        playrecordedaudio = view.findViewById(R.id.playrecording);
        pauserecordedaudio = view.findViewById(R.id.pauserecording);
        stoprecordedaudio = view.findViewById(R.id.stoprecording);
        mediaupload_mainlayout = view.findViewById(R.id.mediaupload_mainlayout);
        mediaupload_animviewa = view.findViewById(R.id.mediaupload_animviewa);
        quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
        quiz_backgroundimagea_downloaded = view.findViewById(R.id.quiz_backgroundimagea_downloaded);
        gotopreviousscreen_mainbtn = view.findViewById(R.id.gotopreviousscreen_mainbtn);
        gotonextscreen_mainbtn = view.findViewById(R.id.gotonextscreen_mainbtn);
        questionno_layout = view.findViewById(R.id.questionno_layout);
        mediaupload_mainquestionImage = view.findViewById(R.id.mediaupload_mainquestionImage);
        mediaupload_mainquestionText = view.findViewById(R.id.mediaupload_mainquestionText);
        mediaupload_mainquestionTime = view.findViewById(R.id.mediaupload_mainquestionTime);
        learningcard_coursename = view.findViewById(R.id.learningcard_coursename);
        learningcard_coursenameMaths = view.findViewById(R.id.learningcard_coursenameMaths);
        mediaupload_mainquestion = view.findViewById(R.id.mediaupload_mainquestion);
        mediaupload_mainquestionMaths = view.findViewById(R.id.mediaupload_mainquestionMaths);
        mediaupload_progress = view.findViewById(R.id.mediaupload_progress);
        questionmore_btn = view.findViewById(R.id.questionmore_btn);
        questionaudio_btn = view.findViewById(R.id.questionaudio_btn);
        question_arrowback = view.findViewById(R.id.question_arrowback);
        question_arrowfoword = view.findViewById(R.id.question_arrowfoword);
        showsolution_img = view.findViewById(R.id.showsolution_img);
        mediaupload_timertext = view.findViewById(R.id.mediaupload_timertext);
        learningquiz_imagequestion = view.findViewById(R.id.learningquiz_imagequestion);
        solution_desc = view.findViewById(R.id.solution_desc);
        solution_descMaths = view.findViewById(R.id.solution_desc_maths);
        solution_lable = view.findViewById(R.id.solution_lable);
        solution_readmore = view.findViewById(R.id.solution_readmore);
        solution_readmore_text = view.findViewById(R.id.solution_readmore_text);
        playrecording_layout = view.findViewById(R.id.playrecording_layout);
        audio_attach_ll = view.findViewById(R.id.audio_attach_ll);
        cardprogress_text = view.findViewById(R.id.cardprogress_text);
        ocanim_view = view.findViewById(R.id.ocanim_view);
        uploadprogressLayout = view.findViewById(R.id.uploadprogressLayout);
        mediaupload_progressbar = view.findViewById(R.id.mediaupload_progressbar2);
        uploadprogresstext = view.findViewById(R.id.uploadprogresstext);
        uploadprogress = view.findViewById(R.id.uploadprogress);
        audio_layout = view.findViewById(R.id.audio_layout);
        click_text = view.findViewById(R.id.click_text);
        ref_image = view.findViewById(R.id.ref_image);
        mediaupload_animviewb = view.findViewById(R.id.mediaupload_animviewb);
        mediaupload_rightwrongimage = view.findViewById(R.id.mediaupload_rightwrongimage);
        animoc_layout = view.findViewById(R.id.animoc_layout);
        mediaupload_solutionlayout = view.findViewById(R.id.mediaupload_solutionlayout);
        ocanim_text = view.findViewById(R.id.ocanim_text);
        bottomswipe_view = view.findViewById(R.id.bottomswipe_view);
        rm_share_layout = view.findViewById(R.id.rm_share_layout);
        solution_closebtn = view.findViewById(R.id.solution_closebtn);
        gotonextscreen_btn = view.findViewById(R.id.gotonextscreen_btn);
        mainoption_scrollview = view.findViewById(R.id.mainoption_scrollview);
        showsolution_img = view.findViewById(R.id.showsolution_img);
        bottom_button_layout = view.findViewById(R.id.bottom_button_layout);
        saveImageProgressBar = view.findViewById(R.id.photo_save_progress);
        main_layout = view.findViewById(R.id.main_layout);
        main_layout_bgd = view.findViewById(R.id.main_layout_bgd);
        player_layout = view.findViewById(R.id.player_layout);
        mediaupload_animviewb = view.findViewById(R.id.mediaupload_animviewb);
        attachphoto_layout = view.findViewById(R.id.attachphoto_layout);
        attach_vedio = view.findViewById(R.id.attach_vedio);
        attach_audio = view.findViewById(R.id.attach_audio);
        timer_layout = view.findViewById(R.id.timer_layout);
        record_title_text = view.findViewById(R.id.record_title_text);

        camera_bottom_view = view.findViewById(R.id.camera_bottom_view);
        media_solution_readmore_text = view.findViewById(R.id.media_solution_readmore_text);
        media_solution_readmore = view.findViewById(R.id.media_solution_readmore);

        media_solutionlayout = view.findViewById(R.id.media_solutionlayout);
        solution_desc_review = view.findViewById(R.id.solution_desc_review);
        media_solution_label = view.findViewById(R.id.media_solution_label);

        showsolution_img.setOnClickListener(this);
        gotopreviousscreen_mainbtn.setOnClickListener(this);
        gotonextscreen_mainbtn.setOnClickListener(this);
        submit.setOnClickListener(this);
        delete.setOnClickListener(this);
        record_layout.setOnClickListener(this);
        playrecordedaudio.setOnClickListener(this);
        pauserecordedaudio.setOnClickListener(this);
        stoprecordedaudio.setOnClickListener(this);
        questionmore_btn.setOnClickListener(this);
        attach_audio.setOnClickListener(this);

        camera_frame_Layout = view.findViewById(R.id.camera_frame_Layout);
        ap_camera_frame = view.findViewById(R.id.ap_camera_frame);
        mTextureView = view.findViewById(R.id.texture_view);
        rotate_camera_layout = view.findViewById(R.id.rotate_camera_layout);
        click_photo_layout = view.findViewById(R.id.click_photo_layout);
        preview_ImageView = view.findViewById(R.id.preview_ImageView);
        click_inside_round = view.findViewById(R.id.click_inside_round);
        camera_back_layout = view.findViewById(R.id.camera_back_layout);

        camera_layout = view.findViewById(R.id.camera_layout);
        mu_camera_image_view_layout = view.findViewById(R.id.mu_camera_image_view_layout);

        camera_click = view.findViewById(R.id.camera_click);
        delete_photo = view.findViewById(R.id.delete_photo);
        submit_photo = view.findViewById(R.id.submit_photo);

        camera_click.setOnClickListener(this);
        delete_photo.setOnClickListener(this);
        submit_photo.setOnClickListener(this);
        attachphoto_layout.setOnClickListener(this);
        mu_camera_image_view_layout.setOnClickListener(this);
        camera_back_layout.setOnClickListener(this);

        video_layout = view.findViewById(R.id.video_layout);
        video_view_layout = view.findViewById(R.id.video_view_layout);
        video_click = view.findViewById(R.id.video_click);
        delete_video = view.findViewById(R.id.delete_video);
        submit_video = view.findViewById(R.id.submit_video);
//        videoPreview= (VideoView) view.findViewById(R.id.videoPreview);
        video_click_text = view.findViewById(R.id.video_click_text);
        play_videorec_layout = view.findViewById(R.id.play_videorec_layout);
        stop_video_rec = view.findViewById(R.id.stop_video_rec);
        pause_video_rec = view.findViewById(R.id.pause_video_rec);
        play_video_rec = view.findViewById(R.id.play_video_rec);
        video_preview = view.findViewById(R.id.video_preview);
        playonpreview = view.findViewById(R.id.playonpreview);
        quesvideoLayout = view.findViewById(R.id.quesvideoLayout);
        video_progressbar_layout = view.findViewById(R.id.video_progressbar_layout);
        video_expandbtn = view.findViewById(R.id.video_expandbtn);
        video_stopbtn = view.findViewById(R.id.video_stopbtn);
        video_progressbar = view.findViewById(R.id.video_progressbar);
        downloadvideo_icon = view.findViewById(R.id.downloadvideo_icon);
        lpocimage = view.findViewById(R.id.lpocimage);
        video_timer_layout = view.findViewById(R.id.video_timer_layout);
        video_timer_text = view.findViewById(R.id.video_timer_text);

        video_view_layout.setOnClickListener(this);
        video_click.setOnClickListener(this);
        delete_video.setOnClickListener(this);
        submit_video.setOnClickListener(this);
        play_video_rec.setOnClickListener(this);
        pause_video_rec.setOnClickListener(this);
        stop_video_rec.setOnClickListener(this);
        playonpreview.setOnClickListener(this);
        video_stopbtn.setOnClickListener(this);
        attach_vedio.setOnClickListener(this);

        questionaudio_btn.setOnClickListener(this);
        solution_closebtn.setOnClickListener(this);
        gotonextscreen_btn.setOnClickListener(this);
        click_photo_layout.setOnClickListener(this);
        rotate_camera_layout.setOnClickListener(this);

        setFont();
        setResourceFile();
        setViewForAssessmentReview();

        if (isAssessmentQuestionReviewMode) {
            mediaupload_animviewa.setBackgroundColor(OustSdkApplication.getContext().getResources().getColor(R.color.popupBackGrounnew));
        }


        if (isReviewMode) {
            showsolution_img.setVisibility(View.GONE);
        }
        questionmore_btn.setVisibility(View.INVISIBLE);
    }

    private void setViewForAssessmentReview() {
        try {
            if (isAssessmentQuestionReviewMode && isReviewMode) {
                questionmore_btn.setVisibility(View.GONE);
                learningcard_coursename.setVisibility(View.GONE);
                showsolution_img.setVisibility(View.GONE);

                if (userResponse != null && !userResponse.isEmpty()) {
                    if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))) {
//                        longanswer_layout.setVisibility(View.VISIBLE);
//                        longanswer_text.setText(OustSdkTools.getEmojiDecodedString(userResponse));
                    } else {
                        media_solutionlayout.setVisibility(View.VISIBLE);
//                        myresponse_label.setVisibility(View.VISIBLE);
//                        myresponse_desc.setVisibility(View.VISIBLE);
//                        myresponse_desc.setText(OustSdkTools.getEmojiDecodedString(userResponse));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFont() {
        if (!isReviewMode) {
            record_title_text.setText(OustStrings.getString("recording_time"));
            click_text.setText(OustStrings.getString("capture_image_text"));
            video_click_text.setText(OustStrings.getString("record_video_text"));
        }
        media_solution_label.setText(OustStrings.getString("solution"));
        solution_lable.setText(OustStrings.getString("solution"));
//        myresponse_label.setText(OustStrings.getString("my_response"));
    }

    private void setResourceFile() {

        if (!isAssessmentQuestion) {
            if (!isReviewMode)
                OustSdkTools.setImage(main_layout_bgd, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));
            else {
                questionno_layout.setVisibility(View.GONE);
            }
        } else {
            ref_image.setVisibility(View.GONE);
            questionno_layout.setVisibility(View.GONE);
        }
        //OustSdkTools.setImage(playonpreview, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
        OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.challenge));
        OustSdkTools.setImage(mediaupload_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
        OustSdkTools.setImage(lpocimage, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));
        OustSdkTools.setImage(mediaupload_mainquestionImage, OustSdkApplication.getContext().getResources().getString(R.string.whitequestion_img));
    }

    //    ======================================================================================================
    private long answeredSeconds;
    private boolean timeout = false;

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                timeout = true;
                if (!attachphotoClicked) {
                    mediaupload_timertext.setText("00:00");
                    onTimeOut();
//            if(!ocCalculated) {
//                ocCalculated=true;
//                learningquiz_rightwrongimage.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.thumbsdown));
//                if(questionType==QuestionType.MRQ){
//                    if((questions.getQuestionCategory()!=null)&&(questions.getQuestionCategory()==QuestionCategory.IMAGE_CHOICE)){
//                        calculateMrqImageOc(true,true);
//                    }else {
//                        calculateMrqTextOc(true,true);
//                    }
//                }else {
//                    rightwrongFlipAnimation(false);
//                }
//            }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                long millis = millisUntilFinished;
                answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                mediaupload_timertext.setText(hms);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void onTimeOut() {
        mediaupload_timertext.setText("00:00");

        if (learningModuleInterface != null) {
            learningModuleInterface.closeCourseInfoPopup();
        }

        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
            stopPlayingRecordedAudio();
            record_layout.setVisibility(View.GONE);
            attach_audio.setVisibility(View.GONE);
            if (recording) {
                stopAudioRecording();
                submit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                showConfirmToUploadPopup();
            } else {
                stopAudioRecording();
                submit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                uploadComplete();

            }

        }
        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
            if (camera_frame_Layout.getVisibility() == View.VISIBLE) {
                camera_frame_Layout.setVisibility(View.GONE);
            }
            deleteAndResetCamera();
            resetAndRemoveCamera();
            submit_photo.setVisibility(View.GONE);
            delete_photo.setVisibility(View.GONE);
            attachphoto_layout.setVisibility(View.GONE);
            camera_click.setVisibility(View.GONE);
            mu_camera_image_view_layout.setClickable(false);
        }
        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {

            video_timer_text.setText("00:00:00");
            submit_video.setVisibility(View.GONE);
            delete_video.setVisibility(View.GONE);
            video_view_layout.setClickable(false);
            video_click.setVisibility(View.GONE);
            attach_vedio.setVisibility(View.GONE);
            if (mIsRecordingVideo) {
                stopRecordingVideo();
                submit_video.setVisibility(View.GONE);
                delete_video.setVisibility(View.GONE);
                showConfirmToUploadPopup();
            } else {
                //deleteVideo();
            }
            if (vrecording && vmediaRecorder != null) {
                vmediaRecorder.stop();
            }
            releaseMediaRecorder();
            resetAndRemoveCamera();
            stopPlayingRecordedVideo();
            if (camera_frame_Layout.getVisibility() == View.VISIBLE) {
                camera_frame_Layout.setVisibility(View.GONE);
            }
            if (cdt != null) {
                cdt.cancel();
                cdt = null;
                cnt = 0;
            }
        }

        if (!isAssessmentQuestion && !isShowConfirmPopupVisible)
            rightwrongFlipAnimation(false);

        if (isAssessmentQuestion && !isShowConfirmPopupVisible) {
            learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
            setAnswer("", "", 0, false, 0);
            {
                nextScreenAtOnce();
                learningModuleInterface.gotoNextScreen();
            }
        }
    }

    public void startTimer() {
        if (!questions.isAdaptiveQuestion()) {
            try {
                if (OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] == 0) {
                    timer = new AdaptiveMediaUploadQuestionFragment.CounterClass(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
                    timer.start();
                } else {
                    setPreviousState();
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void setPreviousState() {
        try {
            int min = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] / 60;
            int sec = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] % 60;
            mediaupload_timertext.setText("" + min + ":" + sec);
        } catch (Exception e) {
        }
    }

    //      =======================================================================================
    private void setStartingData() {
        try {
            Log.d(TAG, "setStartingData: ");
            int waitTimer = 1000;
            if (learningcardProgress == 0) {
                waitTimer = 1200;
            }
            if (!isAssessmentQuestion) {
                if (!isReviewMode)
                    startQuestionNOHideAnimation(waitTimer);
                else {
                    getDataFromFirebase();
                    showReviewOptions();
                    if (isAssessmentQuestionReviewMode) {
                        showSolutionForAssessment();
                    } else {
                        showSolution();
                    }
                }
            } else {
                startShowingQuestions();
            }
            if (questions != null) {
                setTitle();
                setQuestionTitle();
            }
            if ((questions.getImage() != null) && (!questions.getImage().isEmpty())) {
                setImageQuestionImage();
            }
            if (!isReviewMode)
                startTimer();
            else {
                mediaupload_timertext.setVisibility(View.GONE);
                if (isReviewMode && courseLevelClass != null && courseLevelClass.getCourseCardClassList() != null
                        && courseLevelClass.getCourseCardClassList().size() > 0) {
                    if (learningcardProgress == (courseLevelClass.getCourseCardClassList().size()) - 1) {
                        gotonextscreen_mainbtn.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSolutionForAssessment() {
        Log.d(TAG, "showSolutionForAssessment: ");
        try {
            if (mainCourseCardClass != null) {
                if (mainCourseCardClass.getQuestionData() != null && mainCourseCardClass.getQuestionData().getSolution() != null
                        && !mainCourseCardClass.getQuestionData().getSolution().isEmpty()) {
                    media_solutionlayout.setVisibility(View.VISIBLE);
                    media_solution_label.setVisibility(View.VISIBLE);
                    solution_desc_review.setVisibility(View.VISIBLE);
                    solution_desc_review.setHtml(mainCourseCardClass.getQuestionData().getSolution());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSolution() {
        try {
            Log.d(TAG, "showSolution: ");
            if (mainCourseCardClass != null) {
                courseCardClass = mainCourseCardClass.getChildCard();
                if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                    if (mainCourseCardClass.getReadMoreData() != null && mainCourseCardClass.getReadMoreData().getDisplayText() != null) {
                        solution_desc_review.setHtml(courseCardClass.getContent());
                        media_solution_readmore.setVisibility(View.VISIBLE);
                        media_solution_readmore_text.setText(mainCourseCardClass.getReadMoreData().getDisplayText());
                        media_solution_readmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 136);

                            }
                        });
                    } else {
                        solution_desc_review.setHtml(courseCardClass.getContent());
                    }
                    media_solutionlayout.setVisibility(View.VISIBLE);
                    media_solution_label.setVisibility(View.VISIBLE);
                    solution_desc_review.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showReviewOptions() {
        Log.d(TAG, "showReviewOptions: ");
        if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
            audio_layout.setVisibility(View.VISIBLE);
            record_title_text.setText(OustStrings.getString("recorded_time"));
            playrecording_layout.setVisibility(View.VISIBLE);
            audio_attach_ll.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
            camera_layout.setVisibility(View.VISIBLE);
            delete_photo.setVisibility(View.GONE);
            submit_photo.setVisibility(View.GONE);
            camera_click.setVisibility(View.GONE);
            attachphoto_layout.setVisibility(View.GONE);
            camera_bottom_view.setVisibility(View.GONE);
        } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
            video_layout.setVisibility(View.VISIBLE);
            submit_video.setVisibility(View.GONE);
            delete_video.setVisibility(View.GONE);
            video_click.setVisibility(View.GONE);
            attach_vedio.setVisibility(View.GONE);
            bottom_button_layout.setVisibility(View.GONE);
            DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(getActivity(), R.color.RedBorder));
        }
        bottomswipe_view.setVisibility(View.VISIBLE);
    }

    public void setImageQuestionImage() {
        try {
            Log.d(TAG, "setImageQuestionImage: ");
            String str = questions.getImage();
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                learningquiz_imagequestion.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            setImageQuestionImageA();
        }
    }

    public void setImageQuestionImageA() {
        try {
            Log.d(TAG, "setImageQuestionImageA: ");
            String str = questions.getImage();
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                learningquiz_imagequestion.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setTitle() {
        Log.d(TAG, "setTitle: ");
        if (courseLevelClass.getLevelName() != null) {
            if (!courseLevelClass.getLevelName().contains(KATEX_DELIMITER)) {
                learningcard_coursename.setText(courseLevelClass.getLevelName().trim());
                learningcard_coursenameMaths.setVisibility(View.GONE);
                learningcard_coursename.setVisibility(View.VISIBLE);
            } else {
                learningcard_coursenameMaths.setText(courseLevelClass.getLevelName().trim());
                learningcard_coursenameMaths.setVisibility(View.VISIBLE);
                learningcard_coursename.setVisibility(View.GONE);
            }
        }
    }

    private void setQuestionTitle() {
        try {
            Log.d(TAG, "setQuestionTitle: ");
            if ((questions.getQuestion() != null)) {
                if (mainCourseCardClass.getMappedLearningCardId() > 0) {
                    if ((mainCourseCardClass.getCaseStudyTitle() != null) && (!mainCourseCardClass.getCaseStudyTitle().isEmpty())) {
                        if (mainCourseCardClass.getCaseStudyTitle().contains(KATEX_DELIMITER)) {
                            mediaupload_mainquestionMaths.setText(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + mainCourseCardClass.getCaseStudyTitle() + "</a>");
                            mediaupload_mainquestionMaths.setVisibility(View.VISIBLE);
                            mediaupload_mainquestion.setVisibility(View.GONE);
                        } else {
                            mediaupload_mainquestionMaths.setVisibility(View.GONE);
                            mediaupload_mainquestion.setVisibility(View.VISIBLE);
                            mediaupload_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + mainCourseCardClass.getCaseStudyTitle() + "</a>");
                        }
                    } else {
                        if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                            mediaupload_mainquestionMaths.setVisibility(View.VISIBLE);
                            mediaupload_mainquestion.setVisibility(View.GONE);

                            mediaupload_mainquestionMaths.setText(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + "CASE STUDY" + "</a>");
                        } else {
                            mediaupload_mainquestionMaths.setVisibility(View.GONE);
                            mediaupload_mainquestion.setVisibility(View.VISIBLE);

                            mediaupload_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + "CASE STUDY" + "</a>");
                        }
                    }
                    mediaupload_mainquestion.setLinkTextColor(OustSdkTools.getColorBack(R.color.white_pressed));
                    mediaupload_mainquestion.setMovementMethod(new AdaptiveMediaUploadQuestionFragment.TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            isCaseletQuestionOptionClicked = true;
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 135);
                        }
                    });
                } else {
                    if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                        mediaupload_mainquestion.setVisibility(View.GONE);
                        mediaupload_mainquestionMaths.setVisibility(View.VISIBLE);
                        mediaupload_mainquestionMaths.setText(questions.getQuestion());
                    } else {
                        OustSdkTools.getSpannedContent(questions.getQuestion(), mediaupload_mainquestion);
                        mediaupload_mainquestion.setVisibility(View.VISIBLE);
                        mediaupload_mainquestionMaths.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            return true;
        }

        abstract public void onLinkClick(String url);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void startQuestionNOHideAnimation(int waitTimer) {
        try {
            Log.d(TAG, "startQuestionNOHideAnimation: ");
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
            anim.setStartOffset(waitTimer);
            mediaupload_mainquestionImage.startAnimation(anim);
            Animation quizout = AnimationUtils.loadAnimation(getActivity(), R.anim.event_animmoveout);
            quizout.setStartOffset(waitTimer);
            mediaupload_mainquestionTime.startAnimation(quizout);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startShowingQuestions();
                    if (!isAssessmentQuestion) {
                        startSpeakQuestion();
                    } else {
                        playAssessmentAudio();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAssessmentAudio() {
        if (questions != null && questions.getAudio() != null && !questions.getAudio().isEmpty()) {
            final String audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + questions.getAudio();
            mediaPlayer = new MediaPlayer();

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(audio);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();

                            Animation scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                            questionaudio_btn.startAnimation(scaleanim);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    musicComplete = true;
                                    questionaudio_btn.setAnimation(null);
                                }
                            });
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            });

        }
    }

    private void startShowingQuestions() {
        Log.d(TAG, "startShowingQuestions: ");
        if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
            audio_layout.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
            camera_layout.setVisibility(View.VISIBLE);
            delete_photo.setVisibility(View.GONE);
            submit_photo.setVisibility(View.GONE);
//                startCamera();
        } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
            video_layout.setVisibility(View.VISIBLE);
            submit_video.setVisibility(View.GONE);
            delete_video.setVisibility(View.GONE);
            DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(getActivity(), R.color.RedBorder));
        }
    }

    //    ==========================================================================
//        text to Speech and question audio
    private void startSpeakQuestion() {
        try {
            Log.d(TAG, "startSpeakQuestion: ");
            if ((questions.getQuestion() != null)) {
                if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String audioPath = questions.getAudio();
                                String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }, 1000);
                } else {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Spanned s1=getSpannedContent(questions.getQuestion());
//                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    createStringfor_speech();
//                                    } else {
//                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                                        if (OustSdkTools.textToSpeech != null) {
//                                            OustSdkTools.stopSpeech();
//                                            questionaudio_btn.setAnimation(null);
//                                        }
//                                    }
                                } catch (Exception e) {
                                }
                            }
                        }, 1000);
                    } else {
                        questionaudio_btn.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void createStringfor_speech() {
        try {
            Log.d(TAG, "createStringfor_speech: ");
            Spanned s1 = getSpannedContent(questions.getQuestion());
            String quesStr = s1.toString().trim();
            String optionStr = "";
            if ((questions.getA() != null)) {
                Spanned s2 = getSpannedContent(questions.getA());
                optionStr += ("\n Choice A \n" + (s2.toString().trim()));
            }
            if ((questions.getB() != null)) {
                Spanned s2 = getSpannedContent(questions.getB());
                optionStr += ("\n Choice B \n " + (s2.toString().trim()));
            }
            if ((questions.getC() != null)) {
                Spanned s2 = getSpannedContent(questions.getC());
                optionStr += ("\n Choice C \n" + (s2.toString().trim()));
            }
            if ((questions.getD() != null)) {
                Spanned s2 = getSpannedContent(questions.getD());
                optionStr += ("\n Choice D \n" + (s2.toString().trim()));
            }
            if ((questions.getE() != null)) {
                Spanned s2 = getSpannedContent(questions.getE());
                optionStr += ("\n Choice E \n" + (s2.toString().trim()));
            }
            if ((questions.getF() != null)) {
                Spanned s2 = getSpannedContent(questions.getF());
                optionStr += ("\n Choice F \n" + (s2.toString().trim()));
            }
            if ((questions.getG() != null)) {
                Spanned s2 = getSpannedContent(questions.getG());
                optionStr += ("\n Choice G \n" + (s2.toString().trim()));
            }
            speakString(quesStr, true);
        } catch (Exception e) {
        }
    }

    private boolean isAppIsInBackground() {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }

    private void speakString(String str, boolean isQuestStr) {
        try {
            if (!isAppIsInBackground()) {
                String newStr = "";
                newStr = str.replaceAll("[_]+", "\n dash \n");
                int n1 = newStr.length();
                if (n1 > 100)
                    n1 = 100;
                boolean isHindi = false;
                for (int i = 0; i < newStr.length(); i++) {
                    int no = newStr.charAt(i);
                    if (no > 2000) {
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
                        textToSpeech.speak(newStr, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        textToSpeech.speak(newStr, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                View view = questionaudio_btn;
                float count = str.length() / 20;
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1, 0.75f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1, 0.75f);
                scaleDownX.setDuration(1000);
                scaleDownY.setRepeatCount((int) count);
                scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
            }
        } catch (Exception e) {
        }
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
        }
        Spanned s1 = Html.fromHtml(s2, null, new OustTagHandler());
        return s1;
    }

    private boolean musicComplete = false;

    private void playDownloadedAudioQues(final String filename) {
        mediaPlayer = new MediaPlayer();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
//                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//                    String audStr = enternalPrivateStorage.readSavedData(filename);
                    File audiofile = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                    if ((audiofile != null) && (audiofile.exists())) {
                        musicComplete = false;
//                        byte[] audBytes = Base64.decode(audStr, 0);
//                        // create temp file that will hold byte array
//                        File tempMp3 = File.createTempFile(filename, null, getActivity().getCacheDir());
//                        tempMp3.deleteOnExit();
//                        FileOutputStream fos = new FileOutputStream(tempMp3);
//                        fos.write(audBytes);
//                        fos.close();
                        questionaudio_btn.setVisibility(View.VISIBLE);

                        // resetting mediaplayer instance to evade problems
                        mediaPlayer.reset();

                        FileInputStream fis = new FileInputStream(audiofile);
                        mediaPlayer.setDataSource(fis.getFD());
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        Animation scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                        questionaudio_btn.startAnimation(scaleanim);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                musicComplete = true;
                                questionaudio_btn.setAnimation(null);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });

    }

    //     =========================================================================================
    private void setQuestionNo() {
        try {
            if (learningcardProgress == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            }
            if (courseLevelClass.getCourseCardClassList() != null) {
                if (learningcardProgress == (courseLevelClass.getCourseCardClassList().size()) - 1 && isReviewMode) {
                    gotonextscreen_mainbtn.setVisibility(View.GONE);
                }
                mediaupload_mainquestionText.setText(OustStrings.getString("question_text") + " " + (learningcardProgress + 1));
                long millis = questions.getMaxtime();
                String hms = String.format("%02d:%02d", millis / 60, millis % 60);
                mediaupload_mainquestionTime.setText(hms);
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + courseLevelClass.getCourseCardClassList().size());
                mediaupload_progress.setMax((courseLevelClass.getCourseCardClassList().size() * 50));
                mediaupload_progress.setProgress((learningcardProgress * 50));
                ObjectAnimator animation = ObjectAnimator.ofInt(mediaupload_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
                animation.setDuration(600);
                animation.setStartDelay(500);
                animation.start();
            } else {
                if (learningcardProgress == (numberOfCards) - 1) {
                    gotonextscreen_mainbtn.setVisibility(View.GONE);
                }
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + numberOfCards);
                mediaupload_progress.setMax((numberOfCards * 50));
                mediaupload_progress.setProgress((learningcardProgress * 50));
                ObjectAnimator animation = ObjectAnimator.ofInt(mediaupload_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
                animation.setDuration(600);
                animation.setStartDelay(500);
                animation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = mainCourseCardClass.getCardColorScheme();
            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    questionaudio_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowback.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowfoword.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    showsolution_img.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                }
                if ((cardColorScheme.getLevelNameColor() != null) && (!cardColorScheme.getLevelNameColor().isEmpty())) {
                    mediaupload_timertext.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    learningcard_coursename.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    cardprogress_text.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                }
                if ((cardColorScheme.getTitleColor() != null) && (!cardColorScheme.getTitleColor().isEmpty())) {
                    mediaupload_timertext.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
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

        }
//            if(mainCourseCardClass.getChildCard()!=null) {
//                cardColorScheme = mainCourseCardClass.getChildCard().getCardColorScheme();
//                if (cardColorScheme != null) {
//                    if ((cardColorScheme.getContentColor() != null) && (!cardColorScheme.getContentColor().isEmpty())) {
//                        solution_desc.setTextColor(Color.parseColor(cardColorScheme.getContentColor()));
//                    }
//                }
//            }

    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.bg_1));
            if (bgImageUrl != null && !bgImageUrl.isEmpty())
                quiz_backgroundimagea_downloaded.setVisibility(View.VISIBLE);
            if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                Picasso.get().load(bgImageUrl).into(quiz_backgroundimagea_downloaded);
            } else {
                Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(quiz_backgroundimagea_downloaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void waitAndGotoNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoNextScreen();
                        nextScreenAtOnce();
                    }
                } catch (Exception e) {
                }
            }
        }, 120);
    }


    private MediaPlayer mediaPlayer;

    private void wrongAnswerSound() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
        }
    }

    private void rightAnswerSound() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            playAudio("answer_correct.mp3");
        } catch (Exception e) {
        }
    }

    public void cancelSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }

    public void vibrateandShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            mediaupload_mainlayout.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    ==============================================================================
//    Record audio
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder;
    private String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private final int RequestPermissionCode = 1;
    private MediaPlayer record_mediaPlayer;
    private File recorded_file = null;
    private CountDownTimer cdt;
    private int cnt = 0;
    private boolean recording = false, paused = false;
    private int length;
    private boolean isAudioPlaying = false;


    private void playRecording(String audioPath) {
        try {
            isAudioPlaying = true;
            stopQuestionAudio();
            if (paused && record_mediaPlayer != null) {
                record_mediaPlayer.seekTo(length);
                record_mediaPlayer.start();
            } else {
                record_mediaPlayer = new MediaPlayer();
                try {
                    if (audioPath != null) {
                        record_mediaPlayer.setDataSource(mediaSource);
                        record_mediaPlayer.prepare();
                    } else {
                        record_mediaPlayer.setDataSource(AudioSavePathInDevice);
                        record_mediaPlayer.prepare();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if (isReviewMode) {
                    long totalDuration = record_mediaPlayer.getDuration() / 1000;
                    int minutes = (int) (totalDuration / 60);

                    int seconds = (int) totalDuration % 60;
                    recording_time.setText(String.format("%02d:%02d", minutes, seconds));
                }
                record_mediaPlayer.start();
                record_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isAudioPlaying = false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void pauseRecording() {
        try {
            if (record_mediaPlayer != null) {
                delete.setClickable(true);
                paused = true;
                record_mediaPlayer.pause();
                length = record_mediaPlayer.getCurrentPosition();
                //Toast.makeText(getActivity(), "audio paused", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopPlayingRecordedAudio() {
        try {
            if (record_mediaPlayer != null) {
                paused = false;
                record_mediaPlayer.reset();
                record_mediaPlayer.stop();
                record_mediaPlayer.release();
                record_mediaPlayer = null;
                delete.setClickable(true);
                //Toast.makeText(getActivity(), "audio stopped", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAttachment = false;

    private void deleteRecording() {
        try {
            record_title_text.setVisibility(View.VISIBLE);
            recording_time.setVisibility(View.VISIBLE);
            recording_time.setText("00:00");
            AudioSavePathInDevice = null;
            delete.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            if (recorded_file != null && recorded_file.exists() && (!isAttachment)) {
                //  recorded_file.delete();
                //Toast.makeText(getActivity(), "audio deleted", Toast.LENGTH_SHORT).show();
            } else {
                isAttachment = false;
                recorded_file = null;
            }
//            record_layout.setClickable(true);
            cnt = 0;
            cdt = null;
            recording = false;
            playrecording_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startRecording() {
        try {
            if (checkPermission()) {

                if (!recording) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    stopQuestionAudio();
                    attach_audio.setClickable(false);
                    playrecording_layout.setVisibility(View.GONE);
                    record_title_text.setVisibility(View.VISIBLE);
                    recording_time.setVisibility(View.VISIBLE);
                    stopPlayingRecordedAudio();
                    deleteRecording();

                    if (cdt != null) {
                        cdt.cancel();
                        cdt = null;
                        cnt = 0;
                    }

                    cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (recording) {
                                cnt++;
                                String time = new Integer(cnt).toString();
                                long millis = cnt;
                                int seconds = (int) (millis / 60);
                                int minutes = seconds / 60;
                                seconds = seconds % 60;
                                recording_time.setText(String.format("%02d:%02d", seconds, millis));
                            }
                            if (!recording && cdt != null) {
                                cdt.cancel();
                                cdt = null;
                                cnt = 0;
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                    recording = true;
                    String fileName = "recordedaudio.mp3";
                    record_icon.setImageResource(R.drawable.ic_pause);
                    try {
                        recorded_file = File.createTempFile(fileName, null, getActivity().getCacheDir());
                        AudioSavePathInDevice = recorded_file.getPath();
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    //Toast.makeText(getActivity(), "Recording started",Toast.LENGTH_LONG).show();
                } else {
                    stopAudioRecording();
                }
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void releaseAudioMediaRecorder() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    // release the recorder object
                    mediaRecorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void stopAudioRecording() {
        try {
            OustStaticVariableHandling.getInstance().setCameraStarted(false);
            recording = false;
            isCameraOpen = false;
            record_icon.setImageResource(R.drawable.ic_mic);
            attach_audio.setClickable(true);
            record_layout.setClickable(true);
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder = null;
            }
            if (!recording && cdt != null) {
                cdt.cancel();
                cdt = null;
                cnt = 0;
            }
            //Toast.makeText(getActivity(), "Recording Completed", Toast.LENGTH_SHORT).show();
            delete.setVisibility(View.VISIBLE);
            delete.setClickable(true);
            submit.setVisibility(View.VISIBLE);
            submit.setClickable(true);
            playrecording_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(OustSdkApplication.getContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(OustSdkApplication.getContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void MediaRecorderReady() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == RequestPermissionCode) {
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    OustSdkTools.createApplicationFolder();
//                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (StoragePermission && RecordPermission) {
//                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                    startRecording();
                }
            }
            if (requestCode == 120) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        OustStaticVariableHandling.getInstance().setCameraStarted(false);
                        startCamera();
                    }
                }
            }
            if (requestCode == 121) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        OustStaticVariableHandling.getInstance().setCameraStarted(false);
                        startCameraForAboveMarshmallow();
                    }
                }
            }
            if (requestCode == 135) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        openReadMorePopup();
                        OustStaticVariableHandling.getInstance().setCameraStarted(false);
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                            pauseRecording();
                        }
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                            removeVideoPlayer();
                            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                                time = simpleExoPlayerView.getPlayer().getCurrentPosition();
                            }
                        }
                        openCaseLet();
                    }
                }
            } else if (requestCode == 136) {
                if (grantResults != null) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                            pauseRecording();
                        }
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                            removeVideoPlayer();
                            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                                time = simpleExoPlayerView.getPlayer().getCurrentPosition();
                            }
                        }
                        openReadMore();
                    }
                }
            }
            if (requestCode == 131) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkForStoragePermission();
                    } else {
                        camera_click.setClickable(true);
                        mu_camera_image_view_layout.setClickable(true);
                    }
                } else {
                    camera_click.setClickable(true);
                    mu_camera_image_view_layout.setClickable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCaseLet() {
        if ((isCaseletQuestionOptionClicked) && (mainCourseCardClass != null) && (mainCourseCardClass.getMappedLearningCardId() > 0)) {
            stopQuestionAudio();
            isReadMoreOpen = true;
            learningModuleInterface.setShareClicked(true);
            ReadMoreData readMoreData = new ReadMoreData();
            readMoreData.setType("CARD_LINK");
            readMoreData.setCardId(("" + mainCourseCardClass.getMappedLearningCardId()));
            readMoreData.setData("" + mainCourseCardClass.getMappedLearningCardId());
            //learningModuleInterface.openReadMoreFragment(readMoreData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
            // adaptiveLearningModuleInterface(readMoreData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
        }
    }

    private boolean isPdfOpen = false;

    private void openReadMore() {
        stopQuestionAudio();
        isReadMoreOpen = true;
        if (mainCourseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf")) {
            isPdfOpen = true;
            learningModuleInterface.setShareClicked(true);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.M) {
                Log.e("PDF", "filename " + mainCourseCardClass.getReadMoreData().getData());
                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(),
                        OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".provider",
                        OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData()));
                Log.e("PDF", "fileUri not null " + fileUri);
                intent.setDataAndType(fileUri, "application/pdf");
            } else {
                File file = OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData());
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            }
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(intent);
        } else {
            DTOReadMore readData = mainCourseCardClass.getReadMoreData();
            learningModuleInterface.setShareClicked(true);
            if (readData.getType().equalsIgnoreCase("IMAGE")) {
                learningModuleInterface.changeOrientationUnSpecific();
            }
            // learningModuleInterface.openReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
        }
    }

    private boolean isAudioPausedFromOpenReadmore = false;

    private void stopQuestionAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isAudioPausedFromOpenReadmore = true;
                questionaudio_btn.setAnimation(null);
                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
            }
        }
        if (OustSdkTools.textToSpeech != null) {
            OustSdkTools.stopSpeech();
            isAudioPausedFromOpenReadmore = true;
            questionaudio_btn.setAnimation(null);
            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
        }
    }

    private void uploadAudioToAWS() {
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, getActivity());

            /*if (timeout) {
                ConfirmToUploadCancel();
            }*/
            if (recorded_file == null || (recorded_file != null && !recorded_file.exists())) {
                Toast.makeText(getActivity(), "File Not Found!", Toast.LENGTH_SHORT).show();
                mediaupload_progressbar.setVisibility(View.GONE);
                return;
            }

            if (learningModuleInterface != null)
                learningModuleInterface.stopTimer();
            cancleTimer();

            attach_audio.setClickable(false);
            record_layout.setClickable(false);
            delete.setClickable(false);
            submit.setClickable(false);

            filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
            stopPlayingRecordedAudio();
            final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Audio/" + filename + ".mp3", recorded_file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        sendResponseForBackend(filename + ".mp3");
                        uploadComplete();
                        delete.setVisibility(View.GONE);
                        submit.setVisibility(View.GONE);
                        attach_audio.setVisibility(View.GONE);
                        record_layout.setVisibility(View.GONE);
//                        OustSdkTools.showToast(OustStrings.getString("file_complete_msg"));
                        addAnswerOnFirebase("mediaUploadCard/Audio/" + filename + ".mp3");
                    }
                    if (TransferState.FAILED.equals(observer.getState())) {
                        if (!isUploadFailedPopupVisible)
                            showUploadFailurePopup();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    long _bytesCurrent = bytesCurrent;
                    long _bytesTotal = bytesTotal;

                    float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                    Log.d("percentage", "" + percentage);
                    uploadprogress.setProgress((int) percentage);
                    uploadprogresstext.setText((int) percentage + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    mediaupload_progressbar.setVisibility(View.GONE);
//                    attach_audio.setClickable(true);
//                    record_layout.setClickable(true);
//                    delete.setClickable(true);
//                    submit.setClickable(true);
                    if (!isUploadFailedPopupVisible)
                        showUploadFailurePopup();
                    Log.e("upload media error", ex.getMessage());
                    //Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void uploadComplete() {
        try {
            mediaupload_progressbar.setVisibility(View.GONE);
            if (recorded_file != null && recorded_file.exists()) {
                // recorded_file.delete();
            }
            recording_time.setVisibility(View.VISIBLE);
            record_title_text.setVisibility(View.VISIBLE);
            recording_time.setText("00:00");
//            attach_audio.setVisibility(View.GONE);
//            record_layout.setVisibility(View.GONE);
            AudioSavePathInDevice = null;
            delete.setClickable(false);
            submit.setClickable(false);
            record_layout.setClickable(false);
            cnt = 0;
            cdt = null;
            playrecording_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


//    ========================================================================================
//    capture image and store

    public void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            OustStaticVariableHandling.getInstance().setCameraStarted(true);
            if (questions != null && questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                showAddPicOption();
            } else if (questions != null && questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                showAddVideoOption();
            } else if (questions != null && questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                showAddAudioOption();
            }
        } else {
            OustStaticVariableHandling.getInstance().setCameraStarted(true);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 131);
        }
    }

//    private final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 101;

    private void showAddVideoOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public void showAddPicOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private final int AUDIO_REQUEST_CODE = 102;

    public void showAddAudioOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, AUDIO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            attachphotoClicked = false;
            learningModuleInterface.setShareClicked(false);
            OustStaticVariableHandling.getInstance().setCameraStarted(false);
            if (timeout) {
                mediaupload_timertext.setText("00:00");
                onTimeOut();
            } else {
                if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (resultCode == RESULT_OK) {
                      /*  attachphotoClicked = false;
                        camera_click.setClickable(true);
                        Uri selectedImageUri = data.getData();
                        String[] projection = {MediaStore.MediaColumns.DATA};
                        CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null, null);
                        Cursor cursor = cursorLoader.loadInBackground();
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        cursor.moveToFirst();
                        String selectedImagePath = cursor.getString(column_index);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);
                        final int REQUIRED_SIZE = 1000;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                        camera_frame_Layout.setVisibility(View.GONE);
                        bitMapToString(bm);*/
                        attachphotoClicked = false;
                        camera_click.setClickable(true);
                        onSelectFromGalleryResult(data, "Image");
                    }
                } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (resultCode == RESULT_OK) {
                      /*  isAttachment = true;
                        Uri mMediaUri = data.getData();
                        String[] projection = {MediaStore.MediaColumns.DATA};
                        CursorLoader cursorLoader = new CursorLoader(getActivity(), mMediaUri, projection, null, null, null);
                        Cursor cursor = cursorLoader.loadInBackground();
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        cursor.moveToFirst();
                        String selectedImagePath = cursor.getString(column_index);
                        videomediaFile = new File(selectedImagePath);
                        previewVideo();*/
                        isAttachment = true;
                        onSelectFromGalleryResult(data, "Video");
//                    InputStream inputStream = getActivity().getContentResolver().openInputStream(mMediaUri);
                    }
                } else if (requestCode == AUDIO_REQUEST_CODE) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (resultCode == RESULT_OK) {
                      /*  isAttachment = true;
                        Uri mMediaUri = data.getData();
                        String[] projection = {MediaStore.MediaColumns.DATA};
                        CursorLoader cursorLoader = new CursorLoader(getActivity(), mMediaUri, projection, null, null, null);
                        Cursor cursor = cursorLoader.loadInBackground();
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        cursor.moveToFirst();
                        String selectedImagePath = cursor.getString(column_index);
                        recorded_file = new File(selectedImagePath);
                        delete.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        record_title_text.setVisibility(View.GONE);
                        recording_time.setVisibility(View.GONE);
                        playrecording_layout.setVisibility(View.VISIBLE);
                        AudioSavePathInDevice = recorded_file.getPath();*/
                        isAttachment = true;
                        onSelectFromGalleryResult(data, "Audio");
//                    InputStream inputStream = getActivity().getContentResolver().openInputStream(mMediaUri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void uploadImageToAWS() {
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, getActivity());

           /* if (timeout) {
                ConfirmToUploadCancel();
            }
            else*/
            if ((captured_image_file == null)) {
                Toast.makeText(getActivity(), "File Not Found!", Toast.LENGTH_SHORT).show();
                mediaupload_progressbar.setVisibility(View.GONE);
                camera_click.setClickable(true);
                mu_camera_image_view_layout.setClickable(true);
                attachphoto_layout.setClickable(true);
                return;
            }

            if (learningModuleInterface != null)
                learningModuleInterface.stopTimer();
            cancleTimer();
            attachphoto_layout.setClickable(false);
            camera_click.setClickable(false);
            delete_photo.setClickable(false);
            submit_photo.setClickable(false);
            filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
            final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Image/" + filename + ".jpg", captured_image_file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        try {
                            if (captured_image_file.exists()) {
                                // captured_image_file.delete();
                                deleteAndResetCamera();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        mu_camera_image_view_layout.setClickable(false);
//                        OustSdkTools.showToast(OustStrings.getString("file_complete_msg"));
                        mediaupload_progressbar.setVisibility(View.GONE);
                        sendResponseForBackend(filename + ".jpg");
                        attachphoto_layout.setVisibility(View.GONE);
                        camera_click.setVisibility(View.GONE);
                        click_text.setText(OustStrings.getString("upload_complete_text"));
                        addAnswerOnFirebase("mediaUploadCard/Image/" + filename + ".jpg");
                    }
                    if (TransferState.FAILED.equals(observer.getState())) {
                        if (!isUploadFailedPopupVisible)
                            showUploadFailurePopup();
                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    long _bytesCurrent = bytesCurrent;
                    long _bytesTotal = bytesTotal;

                    float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                    Log.d("percentage", "" + percentage);
                    uploadprogress.setProgress((int) percentage);
                    uploadprogresstext.setText((int) percentage + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("file upload error", ex.getMessage());
                    //Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    attachphoto_layout.setClickable(true);
//                    camera_click.setClickable(true);
//                    delete_photo.setClickable(true);
//                    submit_photo.setClickable(true);
                    if (!isUploadFailedPopupVisible)
                        showUploadFailurePopup();
                    mediaupload_progressbar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAndResetCamera() {
        try {
            if (captured_image_file != null) {
                captured_image_file = null;
                mu_camera_image_view_layout.setClickable(true);
                delete_photo.setVisibility(View.GONE);
                submit_photo.setVisibility(View.GONE);
                attachphoto_layout.setVisibility(View.VISIBLE);
//            ap_camera_frame.setImageResource(R.color.black_semi_transparent);
                click_text.setText(OustStrings.getString("click_open_camera"));
                click_text.setVisibility(View.VISIBLE);
                preview_ImageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    Capture and upload image to server
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private String imageString, image_file = "image_file.jpg";
    private File captured_image_file;
    boolean inPreview = false;
    private int currentCameraId;
    private MediaRecorder vmediaRecorder;
    boolean vrecording = false;
    //use these variables to store data temp in case of permission
    private ActiveGame activeGame1;

    private void startCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                OustStaticVariableHandling.getInstance().setCameraStarted(false);
                ap_camera_frame.setVisibility(View.GONE);
                camera_frame_Layout.setVisibility(View.GONE);
                mCamera = getCameraInstance();
                mCamera.setDisplayOrientation(90);
                mCameraPreview = new CameraPreview(getActivity(), mCamera);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Size> pictureSizes = parameters.getSupportedPictureSizes();
                List<Size> previewSizes = parameters.getSupportedPreviewSizes();
                DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                Size previewsize = previewSizes.get(0);
                Size picturesize = pictureSizes.get(0);
                for (int i = 0; i < previewSizes.size(); i++) {
                    if ((scrWidth * scrHeight == previewSizes.get(i).width * previewSizes.get(i).height) || (((scrWidth * scrHeight) % (previewSizes.get(i).width * previewSizes.get(i).height)) == 0) || (((previewSizes.get(i).width * previewSizes.get(i).height) % (scrWidth * scrHeight)) == 0)) {
                        previewsize = previewSizes.get(i);
                    }
                }
                for (int i = 0; i < pictureSizes.size(); i++) {
                    if ((scrWidth * scrHeight == pictureSizes.get(i).width * pictureSizes.get(i).height) || (((scrWidth * scrHeight) % (pictureSizes.get(i).width * pictureSizes.get(i).height)) == 0)) {
                        picturesize = pictureSizes.get(i);
                        break;
                    }
                }
//                parameters.setPreviewSize(previewsize.width,previewsize.height);
                parameters.setPictureSize(picturesize.width, picturesize.height);
                parameters.setPreviewSize(previewsize.width, previewsize.height);
//                Camera.Size size = sizes.get(0);
//                for (int i = 0; i < sizes.size(); i++) {
//                    if (sizes.get(i).width > size.width) {
//                Camera.Size size = pictureSizes.get(3);
//                    }
//                }
//                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);
                ap_camera_frame.addView(mCameraPreview);
                inPreview = true;
                camera_frame_Layout.setVisibility(View.VISIBLE);
                ap_camera_frame.setVisibility(View.VISIBLE);
                myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canClickPhoto = true;
                    }
                }, 1000);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 120);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rotateCamera() {
        try {
            resetAndRemoveCamera();
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }

            mCamera.setDisplayOrientation(90);
//        Camera.Parameters parameters = mCamera.getParameters();
//        List<Size> sizes = parameters.getSupportedPictureSizes();
//        Size size = sizes.get(0);
//        for (int i = 0; i < sizes.size(); i++) {
//            if (sizes.get(i).width > size.width) {
//                size = sizes.get(i);
//            }
//        }

            Camera.Parameters parameters = mCamera.getParameters();
            List<Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Size> previewSizes = parameters.getSupportedPreviewSizes();
            DisplayMetrics metrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            Size previewsize = previewSizes.get(0);
            Size picturesize = pictureSizes.get(0);
            for (int i = 0; i < previewSizes.size(); i++) {
                if ((scrWidth * scrHeight == previewSizes.get(i).width * previewSizes.get(i).height) || (((scrWidth * scrHeight) % (previewSizes.get(i).width * previewSizes.get(i).height)) == 0) || (((previewSizes.get(i).width * previewSizes.get(i).height) % (scrWidth * scrHeight)) == 0)) {
                    previewsize = previewSizes.get(i);
                }
            }
            for (int i = 0; i < pictureSizes.size(); i++) {
                if ((scrWidth * scrHeight == pictureSizes.get(i).width * pictureSizes.get(i).height) || (((scrWidth * scrHeight) % (pictureSizes.get(i).width * pictureSizes.get(i).height)) == 0)) {
                    picturesize = pictureSizes.get(i);
                    break;
                }
            }

            parameters.setPictureSize(picturesize.width, picturesize.height);
            parameters.setPreviewSize(previewsize.width, previewsize.height);
            mCamera.setParameters(parameters);

            mCameraPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
            ap_camera_frame.addView(mCameraPreview);
            inPreview = true;
            camera_frame_Layout.setVisibility(View.VISIBLE);
            ap_camera_frame.setVisibility(View.VISIBLE);
            myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canClickPhoto = true;
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAndRemoveCamera() {
        try {
            if (inPreview && mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            ap_camera_frame.removeView(mCameraPreview);
            mCameraPreview = null;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera_frame_Layout.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.i("aspect ratio", "width " + bitmap.getWidth() + " height " + bitmap.getHeight());
                if ((bitmap.getWidth() > bitmap.getHeight()) || (bitmap.getHeight() == bitmap.getWidth())) {
                    Matrix matrix = new Matrix();
                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        matrix.postRotate(270);
                    } else {
                        matrix.postRotate(90);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                try {
                    Matrix matrix = new Matrix();
                    float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                    Matrix matrixMirrorY = new Matrix();
                    matrixMirrorY.setValues(mirrorY);
                    //matrix.postConcat(matrixMirrorY);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                    bitMapToString(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void bitMapToString(Bitmap bitmap) {
        try {
//            if ((bitmap.getWidth() > 250) && (bitmap.getHeight() > 250)) {
//                bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
//            }
            //resetandclosenewCamera();

            saveImageProgressBar.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.GONE);
            camerafacingback = true;
            closeCamera();

            camera_frame_Layout.setVisibility(View.GONE);

//            imageString = Base64.encodeToString(b, Base64.DEFAULT);
//            camera_icon.setImageBitmap(bitmap);
            Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, preview_ImageView.getWidth(), preview_ImageView.getHeight(), false);
            delete_photo.setVisibility(View.VISIBLE);
            submit_photo.setVisibility(View.VISIBLE);
            preview_ImageView.setImageBitmap(inputBitmap);
            preview_ImageView.setVisibility(View.VISIBLE);
            click_text.setVisibility(View.GONE);
            saveImageProgressBar.setVisibility(View.GONE);
            click_photo_layout.setEnabled(true);
            resetAndRemoveCamera();
            captured_image_file = File.createTempFile(image_file, null, getActivity().getCacheDir());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(captured_image_file);
            fos.write(b);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private Size mPreviewSize;
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;
        private List<Size> mSupportedPreviewSizes;


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
            if (mPreviewSize != null) {
                float ratio;
                if (mPreviewSize.height >= mPreviewSize.width)
                    ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
                else
                    ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

                // One of these methods should be used, second method squishes preview slightly
                setMeasuredDimension(width, (int) (width * ratio));
                //        setMeasuredDimension((int) (width * ratio), height);
            }
        }

        // Constructor that obtains context and camera
        @SuppressWarnings("deprecation")
        public CameraPreview(Context context, Camera camera) {
            super(context);
            try {
                this.mCamera = camera;
                this.mSurfaceHolder = this.getHolder();
                this.mSurfaceHolder.addCallback(this);
                this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                for (Size str : mSupportedPreviewSizes) {
                    Log.e("TAG", str.width + "/" + str.height);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                // left blank for now
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            // start preview with new settings
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

//    ==============================================================================================
//      video record and upload

    private final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private boolean videoPaused = false;
    private final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private android.widget.MediaController media_Controller;
    private Handler myHandler;
    private boolean fullScreen = false;
    private boolean isVideoPlaying = true;
    private boolean videoCompleted = false;
    private Uri fileUri;
    private int finalTime, currentTime;

//    private void openPhoneCameraForVideo() {
//        stopQuestionAudio();
//        if(isDeviceSupportCamera()) {
//            LearningMapModuleActivity.isLearningShareClicked = true;
//            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//            if(fileUri!=null){
//                fileUri=null;
//            }
//            fileUri = getOutputMediaFileUri();
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
//        } else {
//            OustSdkTools.showToast("This feature needs camera support in device");
//        }
//    }

    private void finish() {
        vrecording = false;
        releaseMediaRecorder();
        previewVideo();
    }

    private void startCameraAndRecording() {
        try {
            if (vrecording && vmediaRecorder != null) {
                // stop recording and release camera
                vmediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                rotate_camera_layout.setVisibility(View.VISIBLE);
//                click_inside_round.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_round));
//                DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(getActivity(), R.color.RedBorder));
                //OustSdkTools.showToast("Recording Stopped");
                stopVideoRecordingTimer();
                //Exit after saved
                finish();
            } else {
                //Release Camera before MediaRecorder start
                camera_back_layout.setVisibility(View.GONE);
                click_photo_layout.setEnabled(false);
                if (!prepareMediaRecorder()) {
                    Log.e("recorder prepare error", "Fail in prepareMediaRecorder()! Ended -");
                    //Toast.makeText(getActivity(), "Fail in prepareMediaRecorder()! Ended -", Toast.LENGTH_SHORT).show();
                } else {
                    //OustSdkTools.showToast("Recording started");
                    rotate_camera_layout.setVisibility(View.GONE);
                    vmediaRecorder.start();
                    vrecording = true;
                    disableClickFortTwoSec();
                    startVideoRecordingTimer();
                }
//            myButton.setText("STOP");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void disableClickFortTwoSec() {
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                click_photo_layout.setEnabled(true);
                click_photo_layout.setClickable(true);
            }
        }, 2000);
    }

    private void startVideoRecordingTimer() {
        click_inside_round.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_stop));
        cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (vrecording || mIsRecordingVideo) {
                    cnt++;
                    String time = new Integer(cnt).toString();
                    long millis = cnt;
                    int seconds = (int) (millis / 60);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    video_timer_text.setText(String.format("%02d:%02d:%02d", minutes, seconds, millis));
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void stopVideoRecordingTimer() {
        vrecording = false;
        click_inside_round.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.ic_round));
        DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(getActivity(), R.color.RedBorder));
        video_timer_layout.setVisibility(View.GONE);
        if (cdt != null) {
            cdt.cancel();
            video_timer_text.setText("00:00:00");
            cdt = null;
        }
        cnt = 0;

        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                video_click.setClickable(true);
                attach_vedio.setClickable(true);
            }
        }, 1000);
    }

    private boolean prepareMediaRecorder() {
        vmediaRecorder = new MediaRecorder();
        if (mCamera != null) {
            mCamera.unlock();
        } else {
            startCamera();
        }
        vmediaRecorder.setCamera(mCamera);
        if (mCameraPreview == null) {
            mCameraPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
        }

        vmediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        vmediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            vmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            vmediaRecorder.setVideoFrameRate(15);
            vmediaRecorder.setOrientationHint(270);
        } else {
            vmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            vmediaRecorder.setOrientationHint(90);
        }


        vmediaRecorder.setOutputFile(getOutputMediaFile().getPath());
//        vmediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
//        vmediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
        vmediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

        try {
            vmediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseMediaRecorder() {
        try {
            if (vmediaRecorder != null) {
                vmediaRecorder.reset();   // clear recorder configuration
                vmediaRecorder.release(); // release the recorder object
                vmediaRecorder = null;
                // lock camera for later use
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void previewVideo() {
        try {
            video_click_text.setVisibility(View.GONE);
            video_view_layout.setClickable(false);
            delete_video.setClickable(true);
            submit_video.setClickable(true);
            submit_video.setVisibility(View.VISIBLE);
            delete_video.setVisibility(View.VISIBLE);
//            play_videorec_layout.setVisibility(View.VISIBLE);
            Bitmap thumbnail = null;
            if (videomediaFile != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(videomediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            video_preview.setVisibility(View.VISIBLE);
            playonpreview.setClickable(true);
            playonpreview.setVisibility(View.VISIBLE);
            if (thumbnail != null)
                video_preview.setImageBitmap(thumbnail);
            ap_camera_frame.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.GONE);
            resetAndRemoveCamera();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private long time = 0;

    public void startVedioPlayer() {
        try {
            if (videomediaFile != null && !timeout) {
                Log.e("PDF", " inside startVideoPlayer");
                simpleExoPlayerView = new PlayerView(getActivity());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                simpleExoPlayerView.setLayoutParams(new PlayerView.LayoutParams(params));
                simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
                player_layout.addView(simpleExoPlayerView);
                String path = videomediaFile.getPath();
                playonpreview.setVisibility(View.GONE);
                video_preview.setVisibility(View.GONE);
                quesvideoLayout.setVisibility(View.VISIBLE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                OustSdkTools.stopSpeech();
                simpleExoPlayerView.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                simpleExoPlayerView.bringToFront();
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
                trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                LoadControl loadControl = new DefaultLoadControl();
                /*DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
                boolean preferExtensionDecoders = true;
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        OustSdkApplication.useExtensionRenderers()
                                ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity(), drmSessionManager, extensionRendererMode);*/

                RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());

                MediaSourceFactory mediaSourceFactory = new MediaSourceFactory() {
                    @Override
                    public MediaSourceFactory setDrmSessionManagerProvider(@Nullable DrmSessionManagerProvider drmSessionManagerProvider) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmSessionManager(@Nullable DrmSessionManager drmSessionManager) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmHttpDataSourceFactory(@Nullable HttpDataSource.Factory drmHttpDataSourceFactory) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmUserAgent(@Nullable String userAgent) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setLoadErrorHandlingPolicy(@Nullable LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
                        return null;
                    }

                    @Override
                    public int[] getSupportedTypes() {
                        return new int[0];
                    }

                    @Override
                    public MediaSource createMediaSource(MediaItem mediaItem) {
                        return null;
                    }
                };

                AnalyticsCollector analyticsCollector = new AnalyticsCollector(new Clock() {
                    @Override
                    public long currentTimeMillis() {
                        return 0;
                    }

                    @Override
                    public long elapsedRealtime() {
                        return 0;
                    }

                    @Override
                    public long uptimeMillis() {
                        return 0;
                    }

                    @Override
                    public HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback) {
                        return null;
                    }

                    @Override
                    public void onThreadBlocked() {

                    }
                });

                //simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), renderersFactory, trackSelector, loadControl);
                simpleExoPlayer = new SimpleExoPlayer.Builder(getActivity(), renderersFactory, trackSelector, mediaSourceFactory , loadControl, bandwidthMeter, analyticsCollector).build();

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE)
                        .build();

                simpleExoPlayer.setAudioAttributes(audioAttributes,true); ///* handleAudioFocus= */

                MediaSource videoSource;
                File file = new File(path);
                if (file.exists()) {
                    Uri videoUri = Uri.fromFile(file);
                    Log.e("Player", "" + videoUri);
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

                    simpleExoPlayer.seekTo(time);
                    simpleExoPlayerView.setPlayer(simpleExoPlayer);
                    simpleExoPlayer.setMediaSource(videoSource);
                    simpleExoPlayer.prepare();
                } else {
                    if (isReviewMode && mediaSource != null) {
                        Uri videoUri = Uri.parse(mediaSource);
                        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("my_exo_player");
                        videoSource = new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                        //videoSource = new ExtractorMediaSource(videoUri, defaultHttpDataSourceFactory, extractorsFactory, null, null);
                        simpleExoPlayerView.setPlayer(simpleExoPlayer);
                        simpleExoPlayer.setMediaSource(videoSource);
                        simpleExoPlayer.prepare();
                    }
                }

                simpleExoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeVideoPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
//            time = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                simpleExoPlayerView.setVisibility(View.GONE);
                quesvideoLayout.setVisibility(View.GONE);
                simpleExoPlayer.stop();
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                trackSelector = null;
                simpleExoPlayerView = null;
                Log.e("-------", "onPause");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVideoRatio() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        int scrHeight = metrics.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
        float h = (scrWidth * 0.55f);
        params.width = scrWidth;
        params.height = (int) h;
        simpleExoPlayerView.setLayoutParams(params);
    }

    private void setLandscapeVideoRation() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        int scrHeight = metrics.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
        //float h = (scrHeight * 0.f);
        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
        if (mainCourseCardClass.isPotraitModeVideo()) {
            params.height = (scrHeight - size);
        } else {
            params.height = (scrWidth - size);
        }
        params.width = scrHeight;
        simpleExoPlayerView.setLayoutParams(params);
    }

    private void setLandscapeVid() {
        try {
            if (!mainCourseCardClass.isPotraitModeVideo()) {
                learningModuleInterface.changeOrientationLandscape();
            }
            mediaupload_mainquestion.setVisibility(View.GONE);
            ref_image.setVisibility(View.GONE);
//            solution_headertext.setVisibility(View.GONE);
//            solution_desc.setVisibility(View.GONE);
//            learningcard_tuta.setVisibility(View.GONE);
            bottomswipe_view.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVid() {
        try {
            learningModuleInterface.changeOrientationPortrait();
            mediaupload_mainquestion.setVisibility(View.VISIBLE);
            ref_image.setVisibility(View.VISIBLE);
//            if ((mainCourseCardClass != null) && (mainCourseCardClass.getCardTitle() != null) && (!mainCourseCardClass.getCardTitle().isEmpty())) {
//                solution_headertext.setVisibility(View.VISIBLE);
//            }
//            if ((mainCourseCardClass != null) && (mainCourseCardClass.getContent() != null) && (!mainCourseCardClass.getContent().isEmpty())) {
//                solution_desc.setVisibility(View.VISIBLE);
//            }
//            if(containImage){
//                learningcard_tuta.setVisibility(View.VISIBLE);
//            }
            bottomswipe_view.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopPlayingRecordedVideo() {
//        if(videoPreview.isPlaying()) {
//            videoPreview.stopPlayback();
//            videoPaused = false;
//            stopPosition = 0;
//        }
        removeVideoPlayer();
    }

    int stopPosition = 0;

    private void pauseVideo() {
//        stopPosition = videoPreview.getCurrentPosition();
//        videoPreview.pause();
//        videoPaused=true;
        removeVideoPlayer();
    }

    private void uploadVideoToAWS() {
        try {
            //OustSdkTools.showToast("the size is " + length + " kb");
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());

            /*if (timeout) {
                ConfirmToUploadCancel();
            }*/
            if ((videomediaFile == null) || (videomediaFile != null && !videomediaFile.exists())) {
                mediaupload_progressbar.setVisibility(View.GONE);
                Toast.makeText(OustSdkApplication.getContext(), "File Not Found!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (learningModuleInterface != null)
                    learningModuleInterface.stopTimer();
                cancleTimer();

                float length = videomediaFile.length() / 1024f;
//        if(videoPreview.isPlaying()) {
//            videoPreview.stopPlayback();
//            videoPaused = false;
//            stopPosition = 0;
//        }
                delete_video.setClickable(false);
                submit_video.setClickable(false);
                video_click.setClickable(false);
                video_view_layout.setClickable(false);
                attach_vedio.setClickable(false);
                removeVideoPlayer();
                filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
                final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Video/" + filename + ".mp4", videomediaFile);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED.equals(observer.getState())) {
                            if (videomediaFile != null && videomediaFile.exists() && (!isAttachment)) {
                                // videomediaFile.delete();
                            } else {
                                videomediaFile = null;
                                isAttachment = false;
                            }
//                            OustSdkTools.showToast(OustStrings.getString("file_complete_msg"));
                            deleteAndResetVideo();
                            mediaupload_progressbar.setVisibility(View.GONE);
                            delete_video.setVisibility(View.GONE);
                            submit_video.setVisibility(View.GONE);
                            video_click.setVisibility(View.GONE);
                            video_view_layout.setClickable(false);
                            attach_vedio.setVisibility(View.GONE);
                            sendResponseForBackend(filename + ".mp4");
                            video_click_text.setText(OustStrings.getString("upload_complete_text"));
                            addAnswerOnFirebase("mediaUploadCard/Video/" + filename + ".mp4");
                        }

                        if (TransferState.FAILED.equals(observer.getState())) {
                            if (!isUploadFailedPopupVisible)
                                showUploadFailurePopup();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        long _bytesCurrent = bytesCurrent;
                        long _bytesTotal = bytesTotal;
                        float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                        uploadprogress.setVisibility(View.VISIBLE);
                        uploadprogresstext.setVisibility(View.VISIBLE);
                        Log.d("percentage", "" + percentage);
                        uploadprogress.setProgress((int) percentage);
                        uploadprogresstext.setText((int) percentage + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
//                        mediaupload_progressbar.setClickable(true);
                        mediaupload_progressbar.setVisibility(View.GONE);
                        if (!isUploadFailedPopupVisible)
                            showUploadFailurePopup();
//                        delete_video.setClickable(true);
//                        submit_video.setClickable(true);
//                        video_click.setClickable(true);
//                        video_view_layout.setClickable(true);
//                        attach_vedio.setClickable(true);
//                        OustSdkTools.showToast("upload error");
                        //Toast.makeText(getActivity(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void deleteVideo() {
        try {
            playonpreview.setClickable(true);
            if (videomediaFile != null && videomediaFile.exists() && (!isAttachment)) {
                //  videomediaFile.delete();
            } else {
                videomediaFile = null;
                isAttachment = false;
            }
            delete_video.setVisibility(View.GONE);
            submit_video.setVisibility(View.GONE);
            removeVideoPlayer();
            deleteAndResetVideo();
            videoPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAndResetVideo() {
        try {
            video_click_text.setVisibility(View.VISIBLE);
            video_view_layout.setClickable(true);
            play_videorec_layout.setVisibility(View.GONE);
            quesvideoLayout.setVisibility(View.GONE);
            video_preview.setVisibility(View.GONE);
            playonpreview.setVisibility(View.GONE);
//        videoPreview.setVisibility(View.GONE);
            play_videorec_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File videomediaFile;

    private File getOutputMediaFile() {
        try {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            videomediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            return videomediaFile;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
        }
    }

    // =================================================================
//    utility methods
    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private boolean attachphotoClicked = false;
    private boolean isCameraOpen = false;
    private boolean camerafacingback = true;
    private boolean mIsRecordingVideo = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record_layout) {

            record_layout.setClickable(false);
            myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    record_layout.setClickable(true);
                }
            }, 3000);

            isCameraOpen = true;
            if (!attachphotoClicked) {
                isAudioPlaying = false;
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                startRecording();
            }

        } else if (v.getId() == R.id.attach_audio) {
            if (!isCameraOpen && !attachphotoClicked) {
                attachphotoClicked = true;
                isAudioPlaying = false;
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                stopPlayingRecordedAudio();
                deleteRecording();
                playrecording_layout.setVisibility(View.GONE);
                learningModuleInterface.setShareClicked(true);
                checkForStoragePermission();
            }
        } else if (v.getId() == R.id.delete) {
            isAudioPlaying = false;
            submit.setClickable(false);
            submit.setVisibility(View.GONE);
            stopPlayingRecordedAudio();
            deleteRecording();
        } else if (v.getId() == R.id.submit) {
            if (OustSdkTools.checkInternetStatus()) {
                submit.setClickable(false);
                delete.setClickable(false);
                mediaupload_progressbar.setVisibility(View.VISIBLE);

                uploadAudioToAWS();
            } else {
                OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
            }
        } else if (v.getId() == R.id.playrecording) {
            if (!isAudioPlaying) {
                isAudioPlaying = true;
                if (mediaSource != null && !mediaSource.isEmpty()) {
                    playRecording(mediaSource);
                } else {
                    playRecording(null);
                }
            }
        } else if (v.getId() == R.id.pauserecording) {
            isAudioPlaying = false;
            pauseRecording();
        } else if (v.getId() == R.id.stoprecording) {
            isAudioPlaying = false;
            stopPlayingRecordedAudio();
        } else if (v.getId() == R.id.camera_back_layout) {
            isCameraOpen = false;
            ap_camera_frame.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.GONE);
            video_view_layout.setClickable(true);
            video_click.setClickable(true);
            attach_vedio.setClickable(true);
            camera_click.setClickable(true);
            attachphoto_layout.setClickable(true);
            mu_camera_image_view_layout.setClickable(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                stopBackgroundThread();
                resetandclosenewCamera();
            } else {
                resetAndRemoveCamera();
            }
        } else if ((v.getId() == R.id.camera_click) || (v.getId() == R.id.mu_camera_image_view_layout)) {
//            OustSdkTools.oustTouchEffect(camera_click, 100);
//            mCamera.takePicture(null, null, mPicture);
//            startCameraApp();
            if (!attachphotoClicked) {
                if (!isReviewMode) {
                    camera_click.setClickable(false);
                    attachphoto_layout.setClickable(false);
                    mu_camera_image_view_layout.setClickable(false);
                    saveImageProgressBar.setVisibility(View.GONE);
                    OustStaticVariableHandling.getInstance().setCameraStarted(true);
                    click_photo_layout.setEnabled(true);
                    isCameraOpen = true;
                    camera_back_layout.setVisibility(View.VISIBLE);
                    rotate_camera_layout.setVisibility(View.VISIBLE);
                    click_photo_layout.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        canClickPhoto = false;
                        camerafacingback = true;
                        startCameraForAboveMarshmallow();
                    } else {
                        canClickPhoto = false;
                        mTextureView.setVisibility(View.GONE);
                        startCamera();
                    }
                }
            }
        } else if (v.getId() == R.id.attachphoto_layout) {
            if (!isCameraOpen && !attachphotoClicked) {
                attachphotoClicked = true;
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                learningModuleInterface.setShareClicked(true);
                checkForStoragePermission();
            }
        } else if (v.getId() == R.id.submit_photo) {
            if (OustSdkTools.checkInternetStatus()) {
                if (captured_image_file != null) {
                    submit_photo.setClickable(false);
                    delete_photo.setClickable(false);
                    camera_click.setClickable(false);
                    mu_camera_image_view_layout.setClickable(false);
                    attachphoto_layout.setClickable(false);
                    mediaupload_progressbar.setVisibility(View.VISIBLE);

                    uploadImageToAWS();
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
            }
        } else if (v.getId() == R.id.rotate_camera_layout) {
//            ap_camera_frame.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canClickPhoto) {
                    canClickPhoto = false;
                    switchCamera();
                }
            } else {
                canClickPhoto = false;
                rotateCamera();
            }
        } else if (v.getId() == R.id.delete_photo) {
            deleteAndResetCamera();
        } else if (v.getId() == R.id.click_photo_layout) {
            if (canClickPhoto) {
                canClickPhoto = false;
                myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canClickPhoto = true;
                        camera_click.setClickable(true);
                        attachphoto_layout.setClickable(true);
                    }
                }, 1000);

                click_photo_layout.setClickable(false);
                rotate_camera_layout.setVisibility(View.GONE);
                isCameraOpen = false;
                camera_back_layout.setVisibility(View.GONE);
                if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                    OustSdkTools.oustTouchEffect(camera_click, 100);
                    saveImageProgressBar.setVisibility(View.VISIBLE);
                    click_photo_layout.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        stopBackgroundThread();
                        takePicture();
                    } else {
                        if (mCamera != null) {
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
                } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!mIsRecordingVideo) {
                            click_photo_layout.setEnabled(false);
                            stratRecording();
                        } else {
                            stopRecordingVideo();
                        }
                    } else {
                        startCameraAndRecording();
                    }
                }
            }
        } else if ((v.getId() == R.id.video_view_layout) || (v.getId() == R.id.video_click)) {
//            openPhoneCameraForVideo();
            if (!attachphotoClicked) {
                if (!isReviewMode) {
                    deleteVideo();
                    OustStaticVariableHandling.getInstance().setCameraStarted(true);
                    camera_back_layout.setVisibility(View.VISIBLE);
                    rotate_camera_layout.setVisibility(View.VISIBLE);
                    video_timer_layout.setVisibility(View.VISIBLE);
                    video_view_layout.setClickable(false);
                    video_click.setClickable(false);
                    attach_vedio.setClickable(false);
                    click_photo_layout.setEnabled(true);
                    isCameraOpen = true;
                    click_photo_layout.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        camerafacingback = true;
                        canClickPhoto = false;
                        startCameraForAboveMarshmallow();
                        //startBackgroundThread();
                    } else {
                        canClickPhoto = false;
                        mTextureView.setVisibility(View.GONE);
                        startCamera();
                    }
                }
            }
        } else if (v.getId() == R.id.attach_vedio) {
            if (!isCameraOpen && !attachphotoClicked) {
                attachphotoClicked = true;
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                deleteVideo();
                learningModuleInterface.setShareClicked(true);
                checkForStoragePermission();
            }
        } else if (v.getId() == R.id.submit_video) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                compressVideoAlert(videomediaFile);
            } else {
                if (OustSdkTools.checkInternetStatus()) {
                    submit_video.setClickable(false);
                    delete_video.setClickable(false);
                    mediaupload_progressbar.setVisibility(View.VISIBLE);
                    uploadVideoToAWS();
                } else {
                    OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                }
            }

            /*if (OustSdkTools.checkInternetStatus()) {
                submit_video.setClickable(false);
                delete_video.setClickable(false);
                mediaupload_progressbar.setVisibility(View.VISIBLE);
                uploadVideoToAWS();
            } else {
                OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
            }*/
        } else if (v.getId() == R.id.delete_video) {
            deleteVideo();
        } else if (v.getId() == R.id.play_video_rec) {
//            playRecordedVideo();
            startVedioPlayer();
        } else if (v.getId() == R.id.pause_video_rec) {
            pauseVideo();
        } else if (v.getId() == R.id.stop_video_rec) {
            stopPlayingRecordedVideo();
        } else if (v.getId() == R.id.playonpreview) {
            playonpreview.setClickable(false);
//            playRecordedVideo();
            startVedioPlayer();
        } else if (v.getId() == R.id.solution_closebtn) {
            hideSolutionView();
        } else if (v.getId() == R.id.questionaudio_btn) {
            ttsHandling();
        } else if (v.getId() == R.id.gotonextscreen_btn) {
            OustSdkTools.oustTouchEffect(v, 50);
            if (learningModuleInterface != null) {
                learningModuleInterface.gotoNextScreen();
                nextScreenAtOnce();
            }
        } else if (v.getId() == R.id.gotonextscreen_mainbtn) {
            if (learningModuleInterface != null) {
                nextScreenAtOnce();
                learningModuleInterface.gotoNextScreen();
            }
        } else if (v.getId() == R.id.gotopreviousscreen_mainbtn) {
            learningModuleInterface.gotoPreviousScreen();
        } else if (v.getId() == R.id.showsolution_img) {
            if (!isReviewMode) {
                mediaupload_animviewb.setVisibility(View.VISIBLE);
                mediaupload_animviewb.bringToFront();
                showSolution(0, mediaupload_solutionlayout.getHeight());
            }
        } else if (v.getId() == R.id.questionmore_btn) {
            if (mediaupload_solutionlayout.getVisibility() != View.VISIBLE)
                learningModuleInterface.showCourseInfo();
        }
    }

    private boolean isCaseletQuestionOptionClicked = false;

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;
    private boolean tochedScreen = false;

    public void enableSwipe() {
        try {
            mainoption_scrollview.setOnTouchListener(new View.OnTouchListener() {
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
                                        if (learningModuleInterface != null && !isAssessmentQuestion && timeout) {
                                            nextScreenAtOnce();
                                            learningModuleInterface.gotoNextScreen();
                                        }
                                    }
                                }
                            } else if (deltaX < 0 && deltaY > 0) {
                                if ((-deltaX) > deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        learningModuleInterface.gotoPreviousScreen();
                                    }
                                }

                            } else if (deltaX < 0 && deltaY < 0) {
                                if (deltaX < deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        learningModuleInterface.gotoPreviousScreen();
                                    }
                                }
                            } else if (deltaX > 0 && deltaY < 0) {
                                if (deltaX > (-deltaY)) {
                                    if (deltaX > MIN_DISTANCE) {
                                        if (learningModuleInterface != null && !isAssessmentQuestion && timeout) {
                                            nextScreenAtOnce();
                                            learningModuleInterface.gotoNextScreen();
                                        }
                                    }
                                }
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

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    private void setFontStyle() {
        if (mainCourseCardClass != null) {
            if ((mainCourseCardClass.getLanguage() != null) && (mainCourseCardClass.getLanguage().equals("en"))) {
                learningcard_coursename.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                mediaupload_mainquestion.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                solution_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                solution_desc_review.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        }
    }


    private void ttsHandling() {
        OustSdkTools.toucheffect(questionaudio_btn).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                try {
                    if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                        if (musicComplete) {
                            String audioPath = questions.getAudio();
                            String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                            playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                        } else {
                            if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                                mediaPlayer.pause();
                                questionaudio_btn.setAnimation(null);
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            } else if ((mediaPlayer != null)) {
                                String audioPath = questions.getAudio();
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                                isAudioPausedFromOpenReadmore = false;
                            }
                        }
                    } else {
                        if (isQuesAudioPlaying && (!isAudioPausedFromOpenReadmore)) {
                            isQuesAudioPlaying = false;
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            questionaudio_btn.setAnimation(null);
                            if (OustSdkTools.textToSpeech != null) {
                                OustSdkTools.stopSpeech();
                            }
                        } else {
                            isQuesAudioPlaying = true;
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                            isAudioPausedFromOpenReadmore = false;
                            createStringfor_speech();
                        }
                    }
                } catch (Exception e) {
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

    private boolean isQuesAudioPlaying = true;

    @Override
    public void favouriteClicked(boolean isRMFavourite) {
        try {
            learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            FavCardDetails favCardDetails = new FavCardDetails();
            this.isRMFavourite = isRMFavourite;
            if (isRMFavourite) {
                favCardDetails.setCardId("" + mainCourseCardClass.getCardId());
                favCardDetails.setRmId(mainCourseCardClass.getReadMoreData().getRmId());
                favCardDetails.setRmData(mainCourseCardClass.getReadMoreData().getData());
                favCardDetails.setRMCard(true);
                favCardDetails.setRmDisplayText(mainCourseCardClass.getReadMoreData().getDisplayText());
                favCardDetails.setRmScope(mainCourseCardClass.getReadMoreData().getScope());
                favCardDetails.setRmType(mainCourseCardClass.getReadMoreData().getType());

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

    private void sendResponseForBackend(String filename) {
        if (isAssessmentQuestion) {
            int questionXp = 20;
            if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
                questionXp = (int) mainCourseCardClass.getXp();
            }
            learningModuleInterface.setAnswerAndOc(filename, "", questionXp, true, 0);
            setAnswer(filename, "", questionXp, true, 0);
        } else {
            if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
                finalScr = (int) mainCourseCardClass.getXp();
            }
            learningModuleInterface.setAnswerAndOc(filename, "", finalScr, true, 0);
            setAnswer(filename, "", finalScr, true, 0);
        }
//        if (!isAssessmentQuestion)
//            rightwrongFlipAnimation(true);
//        else
//            learningModuleInterface.gotoNextScreen();

        showUploadCompletePopup();
    }

    public void rightwrongFlipAnimation(boolean status) {
        try {
            cancleTimer();
            if (!zeroXpForQCard) {
                if (finalScr == 0) {
                    OustSdkTools.setImage(mediaupload_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                }
            } else {
                ocanim_view.setVisibility(View.GONE);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                questionaudio_btn.setAnimation(null);
            }
            if (status) {
                rightAnswerSound();
            } else {
                learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                setAnswer("", "", 0, false, 0);
            }
            mediaupload_animviewb.setVisibility(View.VISIBLE);
            mediaupload_animviewb.bringToFront();
            mediaupload_solutionlayout.setPivotX((scrWidth / 2));
            if (animoc_layout.getVisibility() == View.VISIBLE) {
                showAllMedia();
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mediaupload_rightwrongimage, "scaleX", 0.0f, 1);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mediaupload_rightwrongimage, "scaleY", 0.0f, 1);
                scaleDownX.setDuration(500);
                scaleDownY.setDuration(500);
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
                        if (zeroXpForQCard) {
                            showSolutionWithAnimation(true);
                        } else {
                            showSolutionWithAnimation(true);
//                            animateOcCoins();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                if (OustSdkTools.textToSpeech != null) {
                    OustSdkTools.stopSpeech();
                }
                ObjectAnimator transAnim = ObjectAnimator.ofFloat(mediaupload_solutionlayout, "translationY", 0, 1800);
                transAnim.setDuration(50);
                transAnim.start();
            } else {
                showSolution(0, mediaupload_solutionlayout.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateOcCoins() {
        try {
            if (mediaupload_solutionlayout.getVisibility() == View.GONE) {
                if (finalScr < 1) {
                    OustSdkTools.setImage(mediaupload_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                } else {
                    playAudio("coins.mp3");
                }
                ValueAnimator animator1 = new ValueAnimator();
                animator1.setObjectValues(0, (finalScr));
                animator1.setDuration(600);
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ocanim_text.setText("" + (((int) animation.getAnimatedValue())));
                    }
                });
                animator1.start();
                animator1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ocanim_text.setText("" + finalScr);
                        showSolutionWithAnimation(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    private void showSolution(int delay, int startPoint) {
        try {
            learningModuleInterface.dismissCardInfo();
            bottomswipe_view.setVisibility(View.VISIBLE);
            mediaupload_solutionlayout.setVisibility(View.VISIBLE);
            if (mainCourseCardClass.isShareToSocialMedia()) {
                rm_share_layout.setVisibility(View.VISIBLE);
            }
            mediaupload_solutionlayout.setPivotY(mediaupload_solutionlayout.getHeight());
            mediaupload_solutionlayout.setPivotX((scrWidth / 2));
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mediaupload_solutionlayout, "translationY", startPoint, 0);
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mediaupload_solutionlayout, "scaleX", 0.0f, 1.0f);
            scaleDownX.setDuration(500);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay(delay);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Exception e) {
        }
    }


    private int currentOC = 0;
    private int increaseFactor = 0;

    private void showSolutionWithAnimation(final boolean status) {
        try {
            if (((mainCourseCardClass.getChildCard() != null) && (mainCourseCardClass.getChildCard().getContent() != null) && (!mainCourseCardClass.getChildCard().getContent().isEmpty()))) {
                if (mediaupload_solutionlayout.getVisibility() == View.GONE) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                    anim.setStartOffset(200);
                    animoc_layout.startAnimation(anim);
                    showSolution(500, 1800);
                    startSpeakSolution();
                    // setCorrectAnswer();
                    animoc_layout.bringToFront();
                    getView().setFocusableInTouchMode(true);
                    getView().requestFocus();
                    getView().setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                                if (mediaupload_animviewb.getVisibility() == View.VISIBLE) {
                                    hideSolutionView();
                                    return true;
                                }
                                return false;
                            }
                            return false;
                        }
                    });
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (learningModuleInterface != null) {
                            {
                                nextScreenAtOnce();
                                learningModuleInterface.gotoNextScreen();
                            }
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void hideSolutionView() {
        try {
            mediaupload_solutionlayout.setPivotX(mediaupload_solutionlayout.getWidth() / 2);
            showsolution_img.setAnimation(null);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(mediaupload_solutionlayout, "translationY", 0, mediaupload_solutionlayout.getHeight());
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(mediaupload_solutionlayout, "scaleX", 1.0f, 0.0f);
            scaleDownX.setDuration(500);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animoc_layout.setVisibility(View.GONE);
                    mediaupload_animviewb.setVisibility(View.GONE);
                    mediaupload_solutionlayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            showJumpAnimOnSolutionImage();
        } catch (Exception e) {
        }
    }

    private void showJumpAnimOnSolutionImage() {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(showsolution_img, "translationY", 0.0f, 10.0f);
            scaleDownX.setDuration(1300);
            scaleDownX.setRepeatCount(ValueAnimator.REVERSE);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setInterpolator(new BounceInterpolator());
            scaleDownX.start();
            //showsolution_img
        } catch (Exception e) {
        }
    }

    private void startSpeakSolution() {
        try {
            if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
            } else {
                if ((courseCardClass.getContent() != null)) {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                                    Spanned s1 = getSpannedContent(courseCardClass.getContent());
//                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                    speakString(s1.toString().trim(), false);
//                                    } else {
//                                        if (OustSdkTools.textToSpeech != null) {
//                                            OustSdkTools.stopSpeech();
//                                            questionaudio_btn.setAnimation(null);
//                                        }
//                                    }
                                }
                            }
                        }, 200);
                    } else {
                        questionaudio_btn.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private boolean coinAnimOver = false;
    int finalScr = 0;
    private Runnable updateOc = new Runnable() {
        public void run() {
            try {
                if ((currentOC == 0) && (finalScr > 0)) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    }
                    playAudio("coins.mp3");
                }
                if (coinAnimOver) {
                    showSolutionWithAnimation(true);
                } else {
                    if (increaseFactor > 0) {
                        currentOC += increaseFactor;
                        if (currentOC < finalScr) {
                            ocanim_text.setText("" + currentOC);
                            if (myHandler != null) {
                                myHandler.postDelayed(this, 25);
                            }
                        } else {
                            ocanim_text.setText("" + finalScr);
                            coinAnimOver = true;
                            if (myHandler != null) {
                                myHandler.postDelayed(this, 200);
                            }
                        }
                    } else {
                        ocanim_text.setText("" + finalScr);
                        coinAnimOver = true;
                        if (myHandler != null) {
                            myHandler.postDelayed(this, 200);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    public String mUrl1 = null;
    private DTOCourseSolutionCard courseCardClass;

    private void showAllMedia() {
        try {
            if (mainCourseCardClass != null) {
                courseCardClass = mainCourseCardClass.getChildCard();
                if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                    if (mainCourseCardClass.getReadMoreData() != null && mainCourseCardClass.getReadMoreData().getDisplayText() != null) {
                        Log.e("ReadMore", mainCourseCardClass.getReadMoreData().getDisplayText());
                        if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                            solution_descMaths.setText(courseCardClass.getContent());
                            solution_descMaths.setVisibility(View.VISIBLE);
                            solution_desc.setVisibility(View.GONE);
                        } else {
                            solution_desc.setHtml(courseCardClass.getContent());
                            solution_desc.setVisibility(View.VISIBLE);
                            solution_descMaths.setVisibility(View.GONE);
                        }
                        solution_readmore.setVisibility(View.VISIBLE);
                        solution_readmore_text.setText(mainCourseCardClass.getReadMoreData().getDisplayText());
                        solution_readmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCaseletQuestionOptionClicked = false;
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 136);
                            }
                        });
                    } else {
                        if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                            solution_descMaths.setText(courseCardClass.getContent());
                            solution_descMaths.setVisibility(View.VISIBLE);
                            solution_desc.setVisibility(View.GONE);
                        } else {
                            solution_desc.setHtml(courseCardClass.getContent());
                            solution_desc.setVisibility(View.VISIBLE);
                            solution_descMaths.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
    public void onPause() {
        super.onPause();
        if (!attachphotoClicked) {
            stopQuestionAudio();
            stopPlayingRecordedAudio();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!attachphotoClicked) {
            stopQuestionAudio();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            stopPlayingRecordedAudio();
            stopPlayingRecordedVideo();
            releaseAudioMediaRecorder();
            releaseMediaRecorder();
            releaseVideoMediaRecorder();
            deleteAndResetCamera();
            stopBackgroundThread();
            closeCamera();
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler = null;
            }
        }
        LearningCardResponceData learningCardResponce = new LearningCardResponceData();
        learningCardResponce.setCardSubmitDateTime(System.currentTimeMillis() + "");
        learningCardResponce.setCourseCardId(Integer.parseInt(cardId));
        learningCardResponce.setCourseId(Integer.parseInt(courseId));
        learningCardResponce.setCourseLevelId(levelID);
        learningCardResponce.setXp((int) questionXp);
        adaptiveLearningModuleInterface.updateCardResponseData(learningCardResponce);
    }

    private void playAudio(String filename) {
        try {
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            mediaPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }

    private void addAnswerOnFirebase(String answer) {
        if ((answer != null) && (!answer.isEmpty())) {
            String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
            OustFirebaseTools.getRootRef().child(node).setValue(answer);
        }
    }

    @Override
    public void onDestroy() {
        try {

            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    String mediaSource = "";

    private void getDataFromFirebase() {
        Log.d(TAG, "getDataFromFirebase: ");
        if (!isAssessmentQuestionReviewMode) {
            String message = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId();
            ValueEventListener userResponseListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (null != dataSnapshot.getValue()) {
                            Map<Object, Object> answerMap = (Map<Object, Object>) dataSnapshot.getValue();
                            mediaSource = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + answerMap.get("answer");
                            setReviewMedia();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(userResponseListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } else {
            if (userResponse != null && !userResponse.isEmpty()) {
                if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                    mediaSource = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + AppConstants.MediaURLConstants.MEDIA_SOURCE_AUDIO + userResponse;
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    mediaSource = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + AppConstants.MediaURLConstants.MEDIA_SOURCE_IMAGE + userResponse;
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                    mediaSource = AppConstants.MediaURLConstants.MEDIA_SOURCE_BASE_URL + AppConstants.MediaURLConstants.MEDIA_SOURCE_VIDEO + userResponse;
                }
            }
            if (mediaSource != null && !mediaSource.isEmpty()) {
                setReviewMedia();
            }
        }
    }

    private void setReviewMedia() {
        try {
            Log.d(TAG, "setReviewMedia: ");
            if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                playRecording(mediaSource);
            } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
//            preview_ImageView
                Picasso.get().load(mediaSource).into(preview_ImageView);
            } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                playonpreview.setVisibility(View.VISIBLE);
                quesvideoLayout.setVisibility(View.VISIBLE);
                videomediaFile = new File(mediaSource);
                startVedioPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//    =================================================================================================

    //    New Camera API
    private String mCameraId;
    private android.util.Size mPreviewSize;
    protected CameraDevice cameraDevice;
    private int mSensorOrientation;
    private final int MAX_PREVIEW_WIDTH = 1920;
    private final int MAX_PREVIEW_HEIGHT = 1080;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private ImageReader reader;
    private final SparseIntArray ORIENTATIONS = new SparseIntArray();

    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        try {
            if (mBackgroundThread != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBackgroundThread.quitSafely();
                }
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCameraForAboveMarshmallow() {
        Log.d(TAG, "startCameraForAboveMarshmallow: ");
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            ap_camera_frame.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.VISIBLE);
            startBackgroundThread();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 121);
        }
    }

    private void openCamera(int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                setUpCameraOutputs(width, height);
                configureTransform(width, height);
                Activity activity = getActivity();
                CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
                try {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 122);
                        return;
                    }
                    manager.openCamera(mCameraId, mStateCallback, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                } catch (Exception e) {
                    throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
                }
            } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                openCameraForVideo(width, height);
            }
        }
    }

    //    ========================================================
//    new camera video
    private MediaRecorder mMediaRecorder;
    private android.util.Size mVideoSize;
    private Surface mRecorderSurface;

    private void openCameraForVideo(int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            try {

                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (camerafacingback) {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            continue;
                        }
                    } else {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                            continue;
                        }
                    }

                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

//                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                    mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                            width, height, mVideoSize);

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    } else {
                        mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    }
                    configureTransform(width, height);
                    releaseVideoMediaRecorder();
                    mMediaRecorder = new MediaRecorder();
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    manager.openCamera(cameraId, mStateCallback, null);
                }
            } catch (CameraAccessException e) {
//                Toast.makeText(getActivity(), "Cannot access the camera.", Toast.LENGTH_SHORT).show();
//                activity.finish();
            } catch (NullPointerException e) {
                // Currently an NPE is thrown when the Camera2API is used but not supported on the
                // device this code runs.
            } catch (Exception e) {
                throw new RuntimeException("Interrupted while trying to lock camera opening.");
            }

        }

    }

    private void setUpMediaRecorder() throws IOException {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Activity activity = getActivity();
                if (null == activity) {
                    return;
                }

                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
//            mNextVideoAbsolutePath = getVideoFilePath(getActivity());
//        }

                File file = getOutputMediaFile();
                mMediaRecorder.setOutputFile(getOutputMediaFile().getPath());
                mMediaRecorder.setVideoEncodingBitRate(10000000);
                mMediaRecorder.setVideoFrameRate(30);
                mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                if (camerafacingback) {
                    mMediaRecorder.setOrientationHint(90);
                } else {
                    mMediaRecorder.setOrientationHint(270);
                }
//            switch (mSensorOrientation) {
//                case SENSOR_ORIENTATION_DEFAULT_DEGREES:
//                    mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
//                    break;
//                case SENSOR_ORIENTATION_INVERSE_DEGREES:
//                    mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
//                    break;
//            }
                mMediaRecorder.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stratRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
                return;
            }
            try {
                closePreviewSession();
                setUpMediaRecorder();
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                assert texture != null;
                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                List<Surface> surfaces = new ArrayList<>();

                // Set up Surface for the camera preview
                Surface previewSurface = new Surface(texture);
                surfaces.add(previewSurface);
                mPreviewRequestBuilder.addTarget(previewSurface);

                // Set up Surface for the MediaRecorder
                mRecorderSurface = mMediaRecorder.getSurface();
                surfaces.add(mRecorderSurface);
                mPreviewRequestBuilder.addTarget(mRecorderSurface);

                // Start a capture session
                // Once the session starts, we can update the UI and start recording
                cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        mCaptureSession = cameraCaptureSession;
                        updatePreview();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // UI
                                mIsRecordingVideo = true;
                                // Start recording
                                disableClickFortTwoSec();
                                startVideoRecordingTimer();
                                try {
                                    mMediaRecorder.start();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    releaseVideoMediaRecorder();
                                    stopVideoRecordingTimer();
                                }
                            }
                        });
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Activity activity = getActivity();
                        if (null != activity) {
//                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, mBackgroundHandler);
            } catch (IOException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void stopRecordingVideo() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // UI
                mIsRecordingVideo = false;
                // Stop recording
                try {
                    mCaptureSession.stopRepeating();
                    mCaptureSession.abortCaptures();
                    closePreviewSession();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                releaseVideoMediaRecorder();
                stopVideoRecordingTimer();
                camerafacingback = true;
                //               reopenCamera();
                closeCamera();
                stopBackgroundThread();

                previewVideo();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void releaseVideoMediaRecorder() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            try {
                if (mMediaRecorder != null) {
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    // release the recorder object
                    mMediaRecorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private static android.util.Size chooseVideoSize(android.util.Size[] choices) {
        for (android.util.Size size : choices) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                    return size;
                }
            }
        }
        return choices[choices.length - 1];
    }
//    ==========================================================

    private void setUpCameraOutputs(int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Activity activity = getActivity();
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

            try {
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (camerafacingback) {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            continue;
                        }
                    } else {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                            continue;
                        }
                    }

                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null) {
                        continue;
                    }

                    android.util.Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

                    int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    boolean swappedDimensions = false;
                    switch (displayRotation) {
                        case Surface.ROTATION_0:
                        case Surface.ROTATION_180:
                            if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                                swappedDimensions = true;
                            }
                            break;
                        case Surface.ROTATION_90:
                        case Surface.ROTATION_270:
                            if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                                swappedDimensions = true;
                            }
                            break;
                    }

                    Point displaySize = new Point();
                    activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                    int rotatedPreviewWidth = width;
                    int rotatedPreviewHeight = height;
                    int maxPreviewWidth = displaySize.x;
                    int maxPreviewHeight = displaySize.y;

                    if (swappedDimensions) {
                        rotatedPreviewWidth = height;
                        rotatedPreviewHeight = width;
                        maxPreviewWidth = displaySize.y;
                        maxPreviewHeight = displaySize.x;
                    }

                    if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                        maxPreviewWidth = MAX_PREVIEW_WIDTH;
                    }

                    if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                        maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                    }


                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                            rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                            maxPreviewHeight, largest);

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mTextureView.setAspectRatio(
                                mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    } else {
                        mTextureView.setAspectRatio(
                                mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    }

                    mCameraId = cameraId;
                    return;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (NullPointerException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public class CompareSizesByArea implements Comparator<android.util.Size> {
        @Override
        public int compare(android.util.Size lhs, android.util.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
            }
            return 0;
        }

    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Activity activity = getActivity();
            if (null == mTextureView || null == mPreviewSize || null == activity) {
                return;
            }
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufferRect = null;
            bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale = Math.max(
                        (float) viewHeight / mPreviewSize.getHeight(),
                        (float) viewWidth / mPreviewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            } else if (Surface.ROTATION_180 == rotation) {
                matrix.postRotate(180, centerX, centerY);
            }
            mTextureView.setTransform(matrix);
        }
    }

    //    for video
    private android.util.Size chooseOptimalSize(android.util.Size[] choices, int textureViewWidth, int textureViewHeight, android.util.Size aspectRatio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Collect the supported resolutions that are at least as big as the preview Surface
            List<android.util.Size> bigEnough = new ArrayList<android.util.Size>();
            int w = aspectRatio.getWidth();
            int h = aspectRatio.getHeight();
            for (android.util.Size option : choices) {
                if (option.getHeight() == option.getWidth() * h / w &&
                        option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                }
            }

            // Pick the smallest of those, assuming we found any
            if (bigEnough.size() > 0) {
                return Collections.min(bigEnough, new CompareSizesByArea());
            } else {
                Log.e("chooseOptimalSize", "Couldn't find any suitable preview size");
                return choices[0];
            }
        }
        return choices[0];
    }

    //    for image
    private android.util.Size chooseOptimalSize(android.util.Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, android.util.Size aspectRatio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Collect the supported resolutions that are at least as big as the preview Surface
            List<android.util.Size> bigEnough = new ArrayList<>();
            // Collect the supported resolutions that are smaller than the preview Surface
            List<android.util.Size> notBigEnough = new ArrayList<>();
            int w = aspectRatio.getWidth();
            int h = aspectRatio.getHeight();
            for (android.util.Size option : choices) {
                if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                        option.getHeight() == option.getWidth() * h / w) {
                    if (option.getWidth() >= textureViewWidth &&
                            option.getHeight() >= textureViewHeight) {
                        bigEnough.add(option);
                    } else {
                        notBigEnough.add(option);
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            if (bigEnough.size() > 0) {
                return Collections.min(bigEnough, new CompareSizesByArea());
            } else if (notBigEnough.size() > 0) {
                return Collections.max(notBigEnough, new CompareSizesByArea());
            } else {
//            Log.e(TAG, "Couldn't find any suitable preview size");
                return choices[0];
            }
        }
        return choices[0];
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NewApi")
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            OustStaticVariableHandling.getInstance().setCameraStarted(false);
            Log.e("Cmera opened", "onOpened");
            cameraDevice = camera;
            rotate_camera_layout.setClickable(true);
            createCameraPreviewSession();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }
    };

    private boolean canClickPhoto = false;

    private void createCameraPreviewSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                closePreviewSession();

                SurfaceTexture texture = mTextureView.getSurfaceTexture();

                if (texture == null) {
                    return;
                }
                texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

                Surface surface = new Surface(texture);
                mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        //The camera is already closed
                        if (null == cameraDevice) {
                            return;
                        }
                        // When the session is ready, we start displaying the preview.
                        mCaptureSession = cameraCaptureSession;
                        myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canClickPhoto = true;
                            }
                        }, 1000);

                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//                    Toast.makeText(getActivity(), "Configuration change", Toast.LENGTH_SHORT).show();
                    }
                }, mBackgroundHandler);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    protected void updatePreview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null == cameraDevice) {
                Log.e("updatePreview", "updatePreview error, return");
            }
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void closePreviewSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
        }
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    private void switchCamera() {
        rotate_camera_layout.setClickable(false);
        if (camerafacingback) {
            camerafacingback = false;
            closeCamera();
            reopenCamera();
        } else {
            camerafacingback = true;
            closeCamera();
            reopenCamera();
        }
    }

    public void reopenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    protected void takePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null == cameraDevice) {
                Log.e("takePicture", "cameraDevice is null");
                return;
            }
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                android.util.Size[] jpegSizes = null;
                if (characteristics != null) {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }
                int width = 640;
                int height = 480;
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
                reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

                List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");

                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = null;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                image = reader.acquireLatestImage();
                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                byte[] bytes = new byte[buffer.capacity()];
                                buffer.get(bytes);

                                onPictureTaken(bytes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        } finally {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if (image != null) {
                                    image.close();
                                }
                            }
                        }
                    }
                };

                reader.setOnImageAvailableListener(readerListener, null);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
//                    Toast.makeText(AndroidCameraApi.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
//                    createCameraPreview();
                    }
                };
                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            try {
                                session.capture(captureBuilder.build(), captureListener, null);
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                    }
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void onPictureTaken(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Log.i("aspect ratio", "width " + bitmap.getWidth() + " height " + bitmap.getHeight());
            if ((bitmap.getWidth() > bitmap.getHeight()) || (bitmap.getHeight() == bitmap.getWidth())) {
                Matrix matrix = new Matrix();
                if (!camerafacingback) {
                    matrix.postRotate(270);
                } else {
                    matrix.postRotate(90);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            } else if (!camerafacingback) {
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
            try {
                Matrix matrix = new Matrix();
                float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                Matrix matrixMirrorY = new Matrix();
                matrixMirrorY.setValues(mirrorY);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                rotatedBitmap.copy(Bitmap.Config.RGB_565, true);
                bitMapToString(rotatedBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void closeCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != mCaptureSession) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
                if (null != cameraDevice) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
        }
    }

    private void resetandclosenewCamera() {
        saveImageProgressBar.setVisibility(View.GONE);
        camera_frame_Layout.setVisibility(View.GONE);
        camerafacingback = true;
        reopenCamera();
        closeCamera();
    }

//    =========================================================================================

    private boolean isUploadFailedPopupVisible = false;

    private void showUploadFailurePopup() {
        try {
            isUploadFailedPopupVisible = true;
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopWithoutBackButton(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(OustStrings.getString("retry"));
            btnNo.setText(OustStrings.getString("skip"));

            popupContent.setText(OustStrings.getString("upload_fail_error"));
            popupTitle.setText(OustStrings.getString("upload_failed"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (OustSdkTools.checkInternetStatus()) {
                        plaayCanclePopup.dismiss();
                        uploadprogress.setProgress(0);
                        uploadprogresstext.setText(0 + "%");
                        mediaupload_progressbar.setVisibility(View.VISIBLE);
                        isUploadFailedPopupVisible = false;
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                            uploadAudioToAWS();
                        } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                            uploadImageToAWS();
                        } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                            uploadVideoToAWS();
                        }
                    } else {
                        OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAssessmentQuestion)
                                rightwrongFlipAnimation(false);

                            if (isAssessmentQuestion) {
                                learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                                setAnswer("", "", 0, false, 0);
                                learningModuleInterface.gotoNextScreen();
                                nextScreenAtOnce();
                            }
                        }
                    }, 500);

                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAssessmentQuestion)
                                rightwrongFlipAnimation(false);

                            if (isAssessmentQuestion) {
                                learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                                setAnswer("", "", 0, false, 0);
                                learningModuleInterface.gotoNextScreen();
                                nextScreenAtOnce();
                            }
                        }
                    }, 500);
                }
            });

        } catch (Exception e) {
            Log.d("POpup exception", e.getMessage() + "");
        }
    }

    private boolean isShowConfirmPopupVisible = false;

    private void showConfirmToUploadPopup() {
        try {
            isShowConfirmPopupVisible = true;
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopWithoutBackButton(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(OustStrings.getString("upload"));
            btnNo.setText(OustStrings.getString("skip"));

            popupContent.setText(OustStrings.getString("media_timeout_msg"));
            popupTitle.setText(OustStrings.getString("media_timeout_heading"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (OustSdkTools.checkInternetStatus()) {
                        plaayCanclePopup.dismiss();
                        isShowConfirmPopupVisible = false;
                        uploadprogress.setProgress(0);
                        uploadprogresstext.setText(0 + "%");
                        mediaupload_progressbar.setVisibility(View.VISIBLE);
                        if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                            uploadAudioToAWS();
                        } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                            if (videomediaFile != null) {
                                compressVideoAlert(videomediaFile);
                            }
                            //uploadVideoToAWS();
                        }
                    } else {
                        OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    isShowConfirmPopupVisible = false;
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmToUploadCancel();
                        }
                    }, 500);

                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    isShowConfirmPopupVisible = false;
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmToUploadCancel();
                        }
                    }, 500);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Popup exception", e.getMessage() + "");
        }
    }

    private void showUploadCompletePopup() {
        try {
            View popUpView = getActivity().getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopWithoutBackButton(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            btnNo.setVisibility(View.GONE);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText("OK");

            popupTitle.setText(OustStrings.getString("upload_complete_text"));
            popupContent.setText(OustStrings.getString("media_upload_completed"));

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    if (!isAssessmentQuestion)
                        rightwrongFlipAnimation(true);
                    else {
                        nextScreenAtOnce();
                        learningModuleInterface.gotoNextScreen();
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    if (!isAssessmentQuestion)
                        rightwrongFlipAnimation(true);
                    else {
                        nextScreenAtOnce();
                        learningModuleInterface.gotoNextScreen();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Popup exception", e.getMessage() + "");
        }
    }

    private void ConfirmToUploadCancel() {
        try {
            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                uploadComplete();
                if (recording) {
                    recording = false;
                    mediaRecorder.stop();
                }
            }

            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                mIsRecordingVideo = false;
                deleteVideo();
                deleteAndResetVideo();
            }

            if (!isAssessmentQuestion)
                rightwrongFlipAnimation(false);

            if (isAssessmentQuestion) {
                learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                setAnswer("", "", 0, false, 0);
                {
                    nextScreenAtOnce();
                    learningModuleInterface.gotoNextScreen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

/*    private void compressVideo(final String inputFile){
        if(getActivity()!=null) {
            try {
                mediaupload_progressbar.setVisibility(View.VISIBLE);
                String fileName = OustMediaTools.getMediaFileName(inputFile);
                File file = new File(inputFile);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getActivity(), Uri.fromFile(file));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                int sec = (int) timeInMillisec / 1000;
                int min = sec / 60;
                retriever.release();
                Log.d(TAG, "video duration : " + min + ":" + sec);
                long size = (long) (800000 * sec * .0075);//bitrate * seconds
                long kilobytes = size / (8);
                long mb = kilobytes / 1024;
                Log.d(TAG, "Original Size: " + Formatter.formatFileSize(getActivity(), file.length()));

                GiraffeCompressor.init(OustSdkApplication.getContext());

                *//*try {
                    Class.forName("com.github.tcking.giraffecompressor.GiraffeCompressor");
                    GiraffeCompressor.init(OustSdkApplication.getContext());
                } catch (ClassNotFoundException e) {
                    OustSdkTools.showToast("Unable to compress Video please try again");
                    mediaupload_progressbar.setVisibility(View.GONE);
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    return;
                }
*//*

                final File out = new File(Environment.getExternalStorageDirectory(), COMPRESSED_FOLDER + fileName);
                if (!out.exists()) {
                    try {
                        out.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                try {
                    GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                            .input(inputFile) //set video to be compressed
                            .output(out) //set compressed video output
                            .bitRate(2073600)//set bitrate Ã§ ÂÃ§Å½â€¡
                            .resizeFactor(1.0f)//set video resize factor Ã¥Ë†â€ Ã¨Â¾Â¨Ã§Å½â€¡Ã§Â¼Â©Ã¦â€Â¾,Ã©Â»ËœÃ¨Â®Â¤Ã¤Â¿ÂÃ¦Å’ÂÃ¥Å½Å¸Ã¥Ë†â€ Ã¨Â¾Â¨Ã§Å½â€¡
                            //.watermark("/sdcard/videoCompressor/watermarker.png")//add watermark(take a long time) Ã¦Â°Â´Ã¥ÂÂ°Ã¥â€ºÂ¾Ã§â€°â€¡(Ã©Å“â‚¬Ã¨Â¦ÂÃ©â€¢Â¿Ã¦â€”Â¶Ã©â€”Â´Ã¥Â¤â€žÃ§Ââ€ )
                            .ready()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<GiraffeCompressor.Result>() {
                                @Override
                                public void onCompleted() {
                                    Log.d(TAG, "GiraffeCompressor oncompleted");
                                    //$.id(R.id.btn_start).enabled(true).text("start compress");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d(TAG, "GiraffeCompressor Error");
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    OustSdkTools.showToast("Unable to compress Video please try again");
                                    mediaupload_progressbar.setVisibility(View.GONE);
                                    return;
                                    // $.id(R.id.btn_start).enabled(true).text("start compress");
                                    //$.id(R.id.tv_console).text("error:"+e.getMessage());

                                }

                                @Override
                                public void onNext(GiraffeCompressor.Result s) {
                                *//*String msg = String.format("compress completed \ntake time:%s \nout put file:%s", s.getCostTime(), s.getOutput());
                                msg = msg + "\ninput file size:"+ Formatter.formatFileSize(getApplication(),inputFile.length());
                                msg = msg + "\nout file size:"+ Formatter.formatFileSize(getApplication(),new File(s.getOutput()).length());
                                System.out.println(msg);*//*
                                    Log.d(TAG, "videomediafile input Size: " + Formatter.formatFileSize(getActivity(), videomediaFile.length()));
                                    Log.d(TAG, "Original Size: " + Formatter.formatFileSize(getActivity(), new File(inputFile).length()));
                                    Log.d(TAG, "output Size: " + Formatter.formatFileSize(getActivity(), out.length()));
                                    videomediaFile = out;
                                    Log.d(TAG, "videomediafile output Size: " + Formatter.formatFileSize(getActivity(), videomediaFile.length()));

                                    if (OustSdkTools.checkInternetStatus()) {
                                        submit_video.setClickable(false);
                                        delete_video.setClickable(false);
                                        uploadprogress.setProgress((int) 0);
                                        uploadprogresstext.setText((int) 0 + "%");
                                        mediaupload_progressbar.setVisibility(View.VISIBLE);
                                        uploadVideoToAWS();
                                    } else {
                                        OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            } catch (SecurityException e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        else
        {
            OustSdkTools.showToast("Unable to prepare please try again");
        }

    }*/

    private void compressVideoAlert(final File filePath) {
        Log.d(TAG, "compressVideoAlert");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.compress_popup, null);
        builder.setView(alertLayout);
        LinearLayout mLinearLayoutCompress, mLinearLayoutOriginal;
        ImageView mImageViewClose;
        CustomTextView mTextViewSize;
        mTextViewSize = alertLayout.findViewById(R.id.textViewSize);
        File file = new File(filePath.toString());
        String sizeMessage = "This file is of " + Formatter.formatFileSize(getActivity(), file.length());
        mLinearLayoutCompress = alertLayout.findViewById(R.id.linearLayoutCompress);
        mLinearLayoutOriginal = alertLayout.findViewById(R.id.linearLayoutOriginal);
        mImageViewClose = alertLayout.findViewById(R.id.imageViewClose);
        mTextViewSize.setText(sizeMessage);

        mLinearLayoutCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On click compress");
                OustSdkTools.createApplicationFolder();
                mAlertDialogForCompress.dismiss();
                File out = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator
                                + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                                + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR,
                        "compress.mp4"
                );
                if (out.exists())
                    out.delete();
                new VideoCompressor().execute();
                //compressVideo(filePath.toString());
            }
        });

        mLinearLayoutOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogForCompress.dismiss();
                if (OustSdkTools.checkInternetStatus()) {
                    mediaupload_progressbar.setVisibility(View.VISIBLE);
                    uploadVideoToAWS();
                } else {
                    OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                }
            }
        });

        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlertDialogForCompress != null) {
                    mAlertDialogForCompress.dismiss();
                    mAlertDialogForCompress = null;
                }
            }
        });

        mAlertDialogForCompress = builder.create();
        mAlertDialogForCompress.setCancelable(false);
        mAlertDialogForCompress.show();
    }

    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "Start video compression");
            mediaupload_progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(videomediaFile.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            //progressBar.setVisibility(View.GONE);
            if (compressed) {
                if (OustSdkTools.checkInternetStatus()) {
                    submit_video.setClickable(false);
                    delete_video.setClickable(false);
                    uploadprogress.setProgress(0);
                    uploadprogresstext.setText(0 + "%");
                    mediaupload_progressbar.setVisibility(View.VISIBLE);

                    // Log.d(TAG, "Original Size: " + Formatter.formatFileSize(getActivity(), videomediaFile.length()));

                    File out = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator
                                    + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                                    + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR,
                            "compress.mp4"
                    );
                    videomediaFile = out;
                    Log.d(TAG, "Compressed Size: " + Formatter.formatFileSize(getActivity(), videomediaFile.length()));
                    uploadVideoToAWS();
                } else {
                    OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
                }
            } else {
                OustSdkTools.showToast("Unable to compress Video please try again");
                mediaupload_progressbar.setVisibility(View.GONE);
            }
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
            learningCardResponce.setCourseCardId(Integer.parseInt(cardId));
            learningCardResponce.setCourseId(Integer.parseInt(courseId));
            learningCardResponce.setCourseLevelId(levelID);
            // learningCardResponce.setXp((int)mainCourseCardClass.getXp());
            adaptiveLearningModuleInterface.updateCardResponseData(learningCardResponce);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        adaptiveLearningModuleInterface.nextScreen();
    }

    private void onSelectFromGalleryResult(Intent data, String mediaType) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Uri path = data.getData();
        if (path != null) {
            if (path.toString().contains("com.google.android.apps.photos")) {
                Log.d(TAG, "From android photos ");
                String filePath = FilePath.getPathFromInputStreamUri(getActivity(), path);

                File original = new File(filePath);
                String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                if (mediaType != null && mediaType.equalsIgnoreCase("Video")) {

                    videomediaFile = new File(filePath);
                    previewVideo();

                } else if (mediaType != null && mediaType.equalsIgnoreCase("Image")) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, options);
                    final int REQUIRED_SIZE = 1000;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(filePath, options);
                    camera_frame_Layout.setVisibility(View.GONE);
                    bitMapToString(bm);

                } else if (mediaType != null && mediaType.equalsIgnoreCase("Audio")) {

                    recorded_file = new File(filePath);
                    delete.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    record_title_text.setVisibility(View.GONE);
                    recording_time.setVisibility(View.GONE);
                    playrecording_layout.setVisibility(View.VISIBLE);
                    AudioSavePathInDevice = recorded_file.getPath();

                }

                //OustSdkTools.showToast("can't select attachment from google photos app");
                //return;
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Log.d(TAG, "from SDK more than Kitkat");
                String filePath = FilePath.getRealPathFromUri(getActivity(), path);
                if (filePath != null) {
                    File original = new File(filePath);
                    String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                    if (mediaType != null && mediaType.equalsIgnoreCase("Video")) {
                        videomediaFile = new File(filePath);
                        previewVideo();
                    } else if (mediaType != null && mediaType.equalsIgnoreCase("Image")) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(filePath, options);
                        final int REQUIRED_SIZE = 1000;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
                        camera_frame_Layout.setVisibility(View.GONE);
                        bitMapToString(bm);
                    } else if (mediaType != null && mediaType.equalsIgnoreCase("Audio")) {

                        recorded_file = new File(filePath);
                        delete.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        record_title_text.setVisibility(View.GONE);
                        recording_time.setVisibility(View.GONE);
                        playrecording_layout.setVisibility(View.VISIBLE);
                        AudioSavePathInDevice = recorded_file.getPath();
                    }
                } else {
                    OustSdkTools.showToast("unable to get file");
                }
            } else {

                String[] proj = {MediaStore.Images.Media.DATA};
                String result = null;

                CursorLoader cursorLoader = new CursorLoader(
                        getActivity(),
                        path, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                    if (mediaType != null && mediaType.equalsIgnoreCase("Video")) {
                        videomediaFile = new File(result);
                        previewVideo();
                    } else if (mediaType != null && mediaType.equalsIgnoreCase("Image")) {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(result, options);
                        final int REQUIRED_SIZE = 1000;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        Bitmap bm = BitmapFactory.decodeFile(result, options);
                        camera_frame_Layout.setVisibility(View.GONE);
                        bitMapToString(bm);

                    } else if (mediaType != null && mediaType.equalsIgnoreCase("Audio")) {

                        recorded_file = new File(result);
                        delete.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        record_title_text.setVisibility(View.GONE);
                        recording_time.setVisibility(View.GONE);
                        playrecording_layout.setVisibility(View.VISIBLE);
                        AudioSavePathInDevice = recorded_file.getPath();
                    }
                }
            }
        }
    }


}