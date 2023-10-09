package com.oustme.oustsdk.fragments.courses;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
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
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.AutoFitTextureView;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningQuestionData;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
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
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by shilpysamaddar on 05/02/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MediaUploadNewCameraFragment extends Fragment implements View.OnClickListener {

    //      recording audio
    private TextView mediaupload_mainquestionText, mediaupload_mainquestionTime, learningcard_coursename,
            cardprogress_text, mediaupload_timertext, uploadprogresstext, ocanim_text, video_timer_text, record_title_text;
    private RelativeLayout record_layout, submit, delete, questionno_layout, mediaupload_mainlayout, mediaupload_animviewa, mediaupload_progressbar,
            uploadprogressLayout, audio_layout, ref_image, animoc_layout, mediaupload_solutionlayout, rm_share_layout, gotonextscreen_btn,
            main_layout, video_timer_layout;
    private ImageView record_icon, playrecordedaudio, pauserecordedaudio, stoprecordedaudio, mediaupload_mainquestionImage, quiz_backgroundimagea,
            questionmore_btn, questionaudio_btn, question_arrowback, question_arrowfoword;
    private LinearLayout gotopreviousscreen_mainbtn, gotonextscreen_mainbtn, playrecording_layout, bottomswipe_view, timer_layout;
    private HtmlTextView solution_desc, mediaupload_mainquestion;
    private int scrWidth, scrHeight;
    private ProgressBar mediaupload_progress, uploadprogress, video_progressbar;
    private GifImageView learningquiz_imagequestion;
    private ImageView mediaupload_rightwrongimage;
    private ProgressBar saveImageProgressBar;

    //      camera
    private RelativeLayout camera_layout, click_photo_layout, mu_camera_image_view_layout, camera_click, delete_photo, attachphoto_layout, submit_photo, camera_frame_Layout;
    private ImageView rotate_camera_layout, preview_ImageView, click_inside_round;
    private TextView click_text, solution_lable;
    private FrameLayout ap_camera_frame;

    //      video
    private RelativeLayout video_layout, video_view_layout, video_click, delete_video, submit_video, quesvideoLayout, video_progressbar_layout, attach_vedio, attach_audio, audio_attach_ll;
    //    private VideoView videoPreview;
    private PlayerView simpleExoPlayerView;
    private TextView video_click_text;
    private LinearLayout play_videorec_layout;
    private ImageView play_video_rec, pause_video_rec, stop_video_rec, video_preview, playonpreview, video_expandbtn, video_stopbtn, showsolution_img, camera_back_layout;
    private String filename;
    private GifImageView downloadvideo_icon;

    private int learningcardProgress = 0;
    private ActiveUser activeUser;

    private ImageButton solution_closebtn;
    private ScrollView mainoption_scrollview;
    private ImageView lpocimage;
    private boolean isReadMoreOpen = false;
    private RelativeLayout player_layout, mediaupload_animviewb;

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;
    private boolean tochedScreen = false;

    private boolean isReviewMode = false;
    private Handler myHandler;
    public AutoFitTextureView mTextureView;
    private static String TAG="MediaUploadNew";

    public void setIsReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
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

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    private DTOCourseCard mainCourseCardClass;

    public void setMainCourseCardClass(DTOCourseCard courseCardClass2) {
        try {
            int savedCardID = (int) courseCardClass2.getCardId();
//            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardById(savedCardID);
            if (this.mainCourseCardClass.getXp() == 0) {
                this.mainCourseCardClass.setXp(100);
            }
        } catch (Exception e) {
            this.mainCourseCardClass = courseCardClass2;
        }
//        questions = this.mainCourseCardClass.getQuestionData();
        questions = this.mainCourseCardClass.getQuestionData();
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
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


    private DTOQuestions questions;

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    private boolean isAssessmentQuestion = false;

    public void setAssessmentQuestion(boolean assessmentQuestion) {
        isAssessmentQuestion = assessmentQuestion;
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
        View view = inflater.inflate(R.layout.fragment_uploadmediaquestion, container, false);
        initViews(view);
        if (OustAppState.getInstance().getActiveUser() == null) {
            getActivity().finish();
        }
        enableSwipe();
        myHandler = new Handler();
        activeUser = OustAppState.getInstance().getActiveUser();
        getWidth();
        setStartingData();
        setQuestionNo();
        setColors();
        setFontStyle();
        return view;
    }

    //    ============================================================================================
    private void initViews(View view) {
        mTextureView = view.findViewById(R.id.texture_view);
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
        gotopreviousscreen_mainbtn = view.findViewById(R.id.gotopreviousscreen_mainbtn);
        gotonextscreen_mainbtn = view.findViewById(R.id.gotonextscreen_mainbtn);
        questionno_layout = view.findViewById(R.id.questionno_layout);
        mediaupload_mainquestionImage = view.findViewById(R.id.mediaupload_mainquestionImage);
        mediaupload_mainquestionText = view.findViewById(R.id.mediaupload_mainquestionText);
        mediaupload_mainquestionTime = view.findViewById(R.id.mediaupload_mainquestionTime);
        learningcard_coursename = view.findViewById(R.id.learningcard_coursename);
        mediaupload_mainquestion = view.findViewById(R.id.mediaupload_mainquestion);
        mediaupload_progress = view.findViewById(R.id.mediaupload_progress);
        questionmore_btn = view.findViewById(R.id.questionmore_btn);
        questionaudio_btn = view.findViewById(R.id.questionaudio_btn);
        question_arrowback = view.findViewById(R.id.question_arrowback);
        question_arrowfoword = view.findViewById(R.id.question_arrowfoword);
        showsolution_img = view.findViewById(R.id.showsolution_img);
        mediaupload_timertext = view.findViewById(R.id.mediaupload_timertext);
        learningquiz_imagequestion = view.findViewById(R.id.learningquiz_imagequestion);
        solution_desc = view.findViewById(R.id.solution_desc);
        playrecording_layout = view.findViewById(R.id.playrecording_layout);
        audio_attach_ll = view.findViewById(R.id.audio_attach_ll);
        cardprogress_text = view.findViewById(R.id.cardprogress_text);
        uploadprogressLayout = view.findViewById(R.id.uploadprogressLayout);
        mediaupload_progressbar = view.findViewById(R.id.mediaupload_progressbar);
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
        saveImageProgressBar = view.findViewById(R.id.photo_save_progress);
        main_layout = view.findViewById(R.id.main_layout);
        player_layout = view.findViewById(R.id.player_layout);
        mediaupload_animviewb = view.findViewById(R.id.mediaupload_animviewb);
        attachphoto_layout = view.findViewById(R.id.attachphoto_layout);
        attach_vedio = view.findViewById(R.id.attach_vedio);
        attach_audio = view.findViewById(R.id.attach_audio);
        timer_layout = view.findViewById(R.id.timer_layout);
        record_title_text = view.findViewById(R.id.record_title_text);
        solution_lable = view.findViewById(R.id.solution_lable);

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
    }

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
                                        if (learningModuleInterface != null && !isAssessmentQuestion)
                                            learningModuleInterface.gotoNextScreen();
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
                                        if (learningModuleInterface != null && !isAssessmentQuestion)
                                            learningModuleInterface.gotoNextScreen();
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

    private void setColors() {
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
            if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                Picasso.get().load(bgImageUrl).into(quiz_backgroundimagea);
            } else {
                Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(quiz_backgroundimagea);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFontStyle() {
        if (mainCourseCardClass != null) {
            if ((mainCourseCardClass.getLanguage() != null) && (mainCourseCardClass.getLanguage().equals("en"))) {
                learningcard_coursename.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                mediaupload_mainquestion.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                solution_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        }
    }

    private void setQuestionNo() {
        try {
            if (learningcardProgress == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFont() {
        record_title_text.setText(OustStrings.getString("recording_time"));
        click_text.setText(OustStrings.getString("capture_image_text"));
        video_click_text.setText(OustStrings.getString("record_video_text"));
        solution_lable.setText(OustStrings.getString("solution"));
    }

    private void setResourceFile() {
        if (!isAssessmentQuestion) {
            if (!isReviewMode)
                OustSdkTools.setBackground(main_layout, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));
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

//    ============================================================================================

    private void setStartingData() {
        try {
            int waitTimer = 1000;
            if (learningcardProgress == 0) {
                waitTimer = 1200;
            }
            if (!isAssessmentQuestion) {
                if (!isReviewMode)
                    startQuestionNOHideAnimation(waitTimer);
                else
                    showReviewOptions();
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
                if (isReviewMode) {
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

    private void showReviewOptions() {
        if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
            audio_layout.setVisibility(View.VISIBLE);
            playrecording_layout.setVisibility(View.VISIBLE);
            audio_attach_ll.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            bottomswipe_view.setVisibility(View.VISIBLE);
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

    public void setImageQuestionImage() {
        try {
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
        if (courseLevelClass.getLevelName() != null) {
            learningcard_coursename.setText(courseLevelClass.getLevelName().trim());
        }
    }

    private boolean isCaseletQuestionOptionClicked = false;

    private void setQuestionTitle() {
        try {
            if ((questions.getQuestion() != null)) {
                if (mainCourseCardClass.getMappedLearningCardId() > 0) {
                    if ((mainCourseCardClass.getCaseStudyTitle() != null) && (!mainCourseCardClass.getCaseStudyTitle().isEmpty())) {
                        mediaupload_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + mainCourseCardClass.getCaseStudyTitle() + "</a>");
                    } else {
                        mediaupload_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + "CASE STUDY" + "</a>");
                    }
                    mediaupload_mainquestion.setLinkTextColor(OustSdkTools.getColorBack(R.color.white_pressed));
                    mediaupload_mainquestion.setMovementMethod(new MediaUploadNewCameraFragment.TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
                            isCaseletQuestionOptionClicked = true;
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 135);
                        }
                    });
                } else {
                    OustSdkTools.getSpannedContent(questions.getQuestion(), mediaupload_mainquestion);
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

    private void startQuestionNOHideAnimation(int waitTimer) {
        try {
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
                    startSpeakQuestion();
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

    private void startShowingQuestions() {
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

//    ===============================================================================

    //        text to Speech and question audio
    private void startSpeakQuestion() {
        try {
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
                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                        createStringfor_speech();
                                    } else {
                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                                        if (OustSdkTools.textToSpeech != null) {
                                            OustSdkTools.stopSpeech();
                                            questionaudio_btn.setAnimation(null);
                                        }
                                    }
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
    private MediaPlayer mediaPlayer;

    private void playDownloadedAudioQues(final String filename) {
        mediaPlayer = new MediaPlayer();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    String audStr = enternalPrivateStorage.readSavedData(filename);
                    if ((audStr != null) && (!audStr.isEmpty())) {
                        musicComplete = false;
                        byte[] audBytes = Base64.decode(audStr, 0);
                        // create temp file that will hold byte array
                        File tempMp3 = File.createTempFile(filename, null, getActivity().getCacheDir());
                        tempMp3.deleteOnExit();
                        FileOutputStream fos = new FileOutputStream(tempMp3);
                        fos.write(audBytes);
                        fos.close();
                        questionaudio_btn.setVisibility(View.VISIBLE);

                        // resetting mediaplayer instance to evade problems
                        mediaPlayer.reset();

                        FileInputStream fis = new FileInputStream(tempMp3);
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

//    =================================================================================

    private boolean timeout = false;
    private long answeredSeconds;

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                timeout = true;
//                if (!attachphotoClicked) {
//                    onTimeOut();
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
//                }
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
                /*String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));*/

                String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                mediaupload_timertext.setText(hms);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    private MediaUploadNewCameraFragment.CounterClass timer;

    public void startTimer() {
        try {
            if (OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] == 0) {
                timer = new MediaUploadNewCameraFragment.CounterClass(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
                timer.start();
            } else {
                setPreviousState();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

//    ============================================================================================

    private boolean attachphotoClicked = false;
    private boolean isCameraOpen = false;

    private void deleteVideo() {
        try {
            if (videomediaFile != null && videomediaFile.exists()) {
                videomediaFile.delete();
            }
            delete_video.setVisibility(View.GONE);
            submit_video.setVisibility(View.GONE);
            removeVideoPlayer();
            deleteAndResetVideo();
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

    @Override
    public void onClick(View v) {
        if ((v.getId() == R.id.camera_click) || (v.getId() == R.id.mu_camera_image_view_layout)) {
            if (!attachphotoClicked) {
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                mu_camera_image_view_layout.setClickable(false);
                camera_frame_Layout.setVisibility(View.VISIBLE);
                ap_camera_frame.setVisibility(View.GONE);
                isCameraOpen = true;

                camerafacingback = true;
                startCamera();
            }
        } else if (v.getId() == R.id.click_photo_layout) {
            isCameraOpen = false;
            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                saveImageProgressBar.setVisibility(View.VISIBLE);
                takePicture();
            } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                if (!mIsRecordingVideo) {
                    stratRecording();
                } else {
                    stopRecordingVideo();
                }
            }
        } else if (v.getId() == R.id.rotate_camera_layout) {
            switchCamera();
        } else if (v.getId() == R.id.camera_back_layout) {
            resetandclosenewCamera();
        } else if ((v.getId() == R.id.video_view_layout) || (v.getId() == R.id.video_click)) {
//            openPhoneCameraForVideo();
            if (!attachphotoClicked) {
                OustStaticVariableHandling.getInstance().setCameraStarted(true);
                video_view_layout.setClickable(false);
                camera_back_layout.setVisibility(View.VISIBLE);
                video_timer_layout.setVisibility(View.VISIBLE);
                camera_frame_Layout.setVisibility(View.VISIBLE);
                click_photo_layout.setEnabled(true);
                isCameraOpen = true;
                camerafacingback = true;
                deleteVideo();
                startCamera();
                startBackgroundThread();
            }
        } else if (v.getId() == R.id.playonpreview) {
//            playRecordedVideo();
            startVedioPlayer();
        } else if (v.getId() == R.id.play_video_rec) {
//            playRecordedVideo();
            startVedioPlayer();
        }
    }

    //    ====================================================================================================
    private MediaRecorder mMediaRecorder;
    private Size mVideoSize;
    private Handler mBackgroundHandler;

    private final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private String AudioSavePathInDevice = null;
    private Surface mRecorderSurface;
    private boolean mIsRecordingVideo = false;
    private HandlerThread mBackgroundThread;

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stratRecording() {
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
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
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

    private void stopRecordingVideo() {

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

        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            mMediaRecorder.reset();
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        }

        reopenCamera();
        closeCamera();
        stopBackgroundThread();

        previewVideo();

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
            playonpreview.setVisibility(View.VISIBLE);
            if (thumbnail != null)
                video_preview.setImageBitmap(thumbnail);
            ap_camera_frame.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCameraForVideo(int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            try {

//                String cameraId = manager.getCameraIdList()[0];

                // Choose the sizes for camera preview and video recording
//                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

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
            if(camerafacingback) {
                mMediaRecorder.setOrientationHint(90);
            }else {
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void closePreviewSession() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    //    ==============================================================================================

    private final SparseIntArray ORIENTATIONS = new SparseIntArray();
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected CameraDevice cameraDevice;
    private final int MAX_PREVIEW_WIDTH = 1920;
    private final int MAX_PREVIEW_HEIGHT = 1080;
    //    private ImageReader mImageReader;
    private int mSensorOrientation;
    private String mCameraId;
    private Size mPreviewSize;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private boolean camerafacingback = true;
    private File captured_image_file;
    private String image_file = "image_file.jpeg";

    private void startCamera() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 120);
        }
    }

    private void openCamera(int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                setUpCameraOutputs(width, height);
                configureTransform(width, height);
                Activity activity = getActivity();
                CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
                try {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 120);
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

    private void setUpCameraOutputs(int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
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

                    Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

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

    private void configureTransform(int viewWidth, int viewHeight) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
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

    private ImageReader reader;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
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
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        onPictureTaken(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } finally {
                        if (image != null) {
                            image.close();
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
                    try {
                        session.capture(captureBuilder.build(), captureListener, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    private void resetandclosenewCamera() {
        saveImageProgressBar.setVisibility(View.GONE);
        camera_frame_Layout.setVisibility(View.GONE);
        camerafacingback = true;
        reopenCamera();
        closeCamera();
    }

    private void bitMapToString(Bitmap bitmap) {
        try {
            resetandclosenewCamera();
            delete_photo.setVisibility(View.VISIBLE);
            submit_photo.setVisibility(View.VISIBLE);
            preview_ImageView.setImageBitmap(bitmap);
            preview_ImageView.setVisibility(View.VISIBLE);
            camera_frame_Layout.setVisibility(View.GONE);
            click_text.setVisibility(View.GONE);
            saveImageProgressBar.setVisibility(View.GONE);
            click_photo_layout.setEnabled(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            captured_image_file = File.createTempFile(image_file, null, getActivity().getCacheDir());
            FileOutputStream fos = new FileOutputStream(captured_image_file);
            fos.write(b);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void switchCamera() {
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

    private void closeCamera() {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
        }
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreviewSession();

            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreviewSession() {
        try {
            closePreviewSession();

            SurfaceTexture texture = mTextureView.getSurfaceTexture();

            assert texture != null;
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
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//                    Toast.makeText(getActivity(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Collect the supported resolutions that are at least as big as the preview Surface
            List<Size> bigEnough = new ArrayList<>();
            // Collect the supported resolutions that are smaller than the preview Surface
            List<Size> notBigEnough = new ArrayList<>();
            int w = aspectRatio.getWidth();
            int h = aspectRatio.getHeight();
            for (Size option : choices) {
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

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
            }
            return 0;
        }
    }

//    ===============================================================================================


    private File videomediaFile;
    private final String IMAGE_DIRECTORY_NAME = "HelloCamera";

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


    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private long time = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 120) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        OustStaticVariableHandling.getInstance().setCameraStarted(false);
                        startCamera();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startVedioPlayer() {
        try {
            if (videomediaFile != null) {

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

                RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
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
//            Uri videoUri = Uri.parse(path);
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
                }

                simpleExoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}