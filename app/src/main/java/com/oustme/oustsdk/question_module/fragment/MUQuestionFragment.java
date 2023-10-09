package com.oustme.oustsdk.question_module.fragment;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.graphics.drawable.Drawable;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;

import android.util.Size;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.compression.video.MediaController;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.AutoFitTextureView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.assessment.AssessmentQuestionBaseActivity;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MUQuestionFragment extends Fragment {
    private static final String TAG = "MUQuestionFragment";
    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    TextView question;
    LinearLayout question_image_lay;
    ImageView question_image;
    TextView info_type;
    ImageView expand_icon;
    FrameLayout answer_image_lay;
    ImageView answer_image;
    ImageView preview_expand_icon;
    CardView delete_icon;
    CardView image_preview_card;
    FrameLayout preview_video_lay;
    VideoView video_container;
    ImageView video_thumbnail;
    LinearLayout media_choose_layout;
    TextView tap_message;
    LinearLayout gallery_layout;
    ImageView upload_image;
    TextView upload_text;
    TextView no_media_found;
    LinearLayout capture_layout;
    ImageView capture_image;
    TextView capture_text;
    FrameLayout question_action_button;
    RelativeLayout texture_layout;
    AutoFitTextureView texture_view_camera;
    RelativeLayout video_timer_layout;
    TextView video_timer_text;
    LinearLayout image_capture_layout;
    ImageView texture_close;
    ImageView capture_icon;
    ImageView camera_switch;
    LinearLayout audio_play_record_lay;
    LinearLayout audio_play_progress;
    ProgressBar play_progress;
    TextView play_duration;
    TextView audio_action_description;
    CardView audio_action;
    ImageView audio_action_image;
    TextView audio_action_text;
    CardView audio_delete;
    KatexView question_katex;
    WebView question_description_webView;
    MediaPlayer mediaPlayer;
    String solutionText;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;
    boolean containSubjective = false;

    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;

    //View Model from Assessment and survey
    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    int questionNo;
    CourseDataClass courseDataClass;
    String courseId;
    String courseName;
    boolean zeroXpForQCard;
    boolean isQuestionImageShown;
    String backgroundImage;
    List<FavCardDetails> favouriteCardList;
    boolean isQuestionTTSEnabled;
    boolean isRMFavourite;
    boolean isCourseCompleted;
    boolean isCourseQuestion;
    int finalScr = 0;

    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    //additional parameters
    int color;
    boolean showNavigateArrow;
    boolean isAssessmentQuestion;
    boolean isSurveyQuestion;
    boolean isReviewMode;
    boolean isReplayMode;
    boolean isLevelCompleted;
    boolean isExamMode;
    private boolean nextArrow;
    private boolean previousArrow;
    Scores score;
    int cardCount;
    int progresssetValue;
    ActiveUser activeUser;

    int UPLOAD_IMAGE = 100;
    int REQUEST_STORAGE = 101;
    int REQUEST_CAMERA = 102;
    int UPLOAD_AUDIO = 103;
    int UPLOAD_VIDEO = 104;
    File imageFile;
    File videoFile;
    File audioFile;
    String uploadFileName;
    Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;
    AssessmentContentHandlingInterface assessmentContentHandler;

    //CameraAPI from Android
    String mCameraId;
    CameraDevice mCameraDevice;
    Size mPreviewSize;
    CameraCaptureSession mPreviewSession;
    Semaphore mCameraOpenCloseLock = new Semaphore(1);
    CaptureRequest.Builder mPreviewBuilder;
    Integer mSensorOrientation;
    ImageReader reader;
    boolean isCameraFacingRear = true;
    final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private boolean isCamOpen;
    boolean startedRecording;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //video
    Size mVideoSize;
    MediaRecorder mMediaRecorder;
    Surface mRecorderSurface;
    boolean mIsRecordingVideo = false;

    final String DIRECTORY_NAME = "OUST CAMERA";
    CountDownTimer countDownTimer;
    int count;

    //mediaPlayer for audio
    MediaPlayer responsePlayer;
    MediaRecorder mAudioRecorder;
    Handler mediaSeekHandler;
    boolean mIsAudioRecording = false;
    boolean mIsAudioPlaying = false;
    String audioPathInDevice;
    CountDownTimer audioProgressTimer;
    int progressValue;
    int resumeProgress;
    String videoUrl;
    String audioUrl;

    //Survey
    SurveyFunctionsAndClicks surveyFunctionsAndClicks;

    //Upload progress  Dialog
    Dialog uploadProgressDialog;
    ProgressBar upload_percentage_progress;
    CircularProgressIndicator circularProgressIndicator;
    TextView upload_percentage_done;
    TextView upload_message;

    PopupWindow zoomImagePopup;
    boolean isTimeOut = false;
    CourseContentHandlingInterface courseContentHandlingInterface;
    String uploadedFileName = "";
    long uploadedFileScore = 0;
    boolean uploadedFileStatus = false;
    long uploadedFileTime = 0;

    public MUQuestionFragment() {
        // Required empty public constructor
    }

    public static MUQuestionFragment newInstance() {
        return new MUQuestionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionBaseViewModel = new ViewModelProvider(requireActivity()).get(QuestionBaseViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            surveyFunctionsAndClicks = (SurveyFunctionsAndClicks) context;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmenttexture_layout
        View view = inflater.inflate(R.layout.fragment_m_u_question, container, false);

        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        question = view.findViewById(R.id.question);
        question_image_lay = view.findViewById(R.id.question_image_lay);
        question_image = view.findViewById(R.id.question_image);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        answer_image_lay = view.findViewById(R.id.answer_image_lay);
        answer_image = view.findViewById(R.id.answer_image);
        preview_expand_icon = view.findViewById(R.id.preview_expand_icon);
        delete_icon = view.findViewById(R.id.delete_icon);
        image_preview_card = view.findViewById(R.id.image_preview_card);
        preview_video_lay = view.findViewById(R.id.preview_video_lay);
        video_container = view.findViewById(R.id.video_container);
        video_thumbnail = view.findViewById(R.id.video_thumbnail);
        media_choose_layout = view.findViewById(R.id.media_choose_layout);
        tap_message = view.findViewById(R.id.tap_message);
        gallery_layout = view.findViewById(R.id.gallery_layout);
        upload_image = view.findViewById(R.id.upload_image);
        upload_text = view.findViewById(R.id.upload_text);
        no_media_found = view.findViewById(R.id.no_media_found);
        capture_layout = view.findViewById(R.id.capture_layout);
        capture_image = view.findViewById(R.id.capture_image);
        capture_text = view.findViewById(R.id.capture_text);
        question_action_button = view.findViewById(R.id.question_action_button);
        texture_layout = view.findViewById(R.id.texture_layout);
        texture_view_camera = view.findViewById(R.id.texture_view_camera);
        video_timer_layout = view.findViewById(R.id.video_timer_layout);
        video_timer_text = view.findViewById(R.id.video_timer_text);
        image_capture_layout = view.findViewById(R.id.image_capture_layout);
        audio_play_record_lay = view.findViewById(R.id.audio_play_record_lay);
        audio_play_progress = view.findViewById(R.id.audio_play_progress);
        play_progress = view.findViewById(R.id.play_progress);
        play_duration = view.findViewById(R.id.play_duration);
        audio_action_description = view.findViewById(R.id.audio_action_description);
        audio_action = view.findViewById(R.id.audio_action);
        audio_action_image = view.findViewById(R.id.audio_action_image);
        audio_action_text = view.findViewById(R.id.audio_action_text);
        audio_delete = view.findViewById(R.id.audio_delete);
        texture_close = view.findViewById(R.id.texture_close);
        capture_icon = view.findViewById(R.id.capture_icon);
        camera_switch = view.findViewById(R.id.camera_switch);
        question_katex = view.findViewById(R.id.question_katex);
        question_description_webView = view.findViewById(R.id.description_webView);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            color = OustResourceUtils.getColors();
            setButtonColor(getResources().getColor(R.color.overlay_container), false);
            if (isCourseQuestion) {
                setQuestionData(questions);
            } else {
                questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
                    if (questionBaseModel == null)
                        return;
                    this.questionBaseModel = questionBaseModel;
                    setData(questionBaseModel);
                });
            }

            preview_expand_icon.setOnClickListener(v -> {

                if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    gifZoomPopup(answer_image.getDrawable());
                }
            });

            delete_icon.setOnClickListener(v -> deleteConfirmation());

            audio_delete.setOnClickListener(v -> deleteConfirmation());

            question_action_button.setOnClickListener(v -> {
                question_action_button.setEnabled(false);
                uploadConfirmation(false);
                new Handler().postDelayed(() -> question_action_button.setEnabled(true), 2000);
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    //All modules
    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setMainCourseCardClass(DTOCourseCard courseCardClass) {
        try {
            int savedCardID = (int) courseCardClass.getCardId();
            cardId = "" + savedCardID;
            this.questionXp = courseCardClass.getXp();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            try {
                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }

                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            if (isRandomizeQuestion) {
                if (isCourseQuestion) {
                    CourseQuestionHandling.randomizeOption(mainCourseCardClass);
                } else if (questionBaseViewModel != null) {
                    this.mainCourseCardClass = questionBaseViewModel.randomizeOption(mainCourseCardClass);
                }
            }

            if (mainCourseCardClass != null && mainCourseCardClass.getQuestionData() != null) {
                questions = mainCourseCardClass.getQuestionData();
            } else {
                mainCourseCardClass = courseCardClass;
            }
            if (mainCourseCardClass != null) {
                if (mainCourseCardClass.getXp() == 0) {
                    mainCourseCardClass.setXp(100);
                }
            }
            if (questions == null) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.endActivity();
                }
            }
            if (isCourseQuestion && mainCourseCardClass != null) {
                if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                    questions.setBgImg(mainCourseCardClass.getBgImg());
                }
            } else if (isAssessmentQuestion && mainCourseCardClass != null) {
                mainCourseCardClass.setXp(questionXp);
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    //course module
    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        try {
            this.courseDataClass = courseDataClass;
            this.courseId = "" + courseDataClass.getCourseId();
            this.courseName = "" + courseDataClass.getCourseName();
            this.zeroXpForQCard = courseDataClass.isZeroXpForQCard();
            this.isQuestionImageShown = courseDataClass.isShowQuestionSymbolForQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBgImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setFavouriteCardList(List<FavCardDetails> favouriteCardList) {
        this.favouriteCardList = favouriteCardList;
        if (this.favouriteCardList == null) {
            this.favouriteCardList = new ArrayList<>();
        }
    }

    public void setQuestionTTSEnabled(boolean isQuestionTTSEnabled) {
        this.isQuestionTTSEnabled = isQuestionTTSEnabled;
    }

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    public void setCourseCompleted(boolean isCourseCompleted) {
        this.isCourseCompleted = isCourseCompleted;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;
    }

    //assessment and survey
    public void setShowNavigateArrow(boolean showNavigateArrow) {
        this.showNavigateArrow = showNavigateArrow;
    }

    public void setAssessmentQuestion(boolean isAssessmentQuestion) {
        this.isAssessmentQuestion = isAssessmentQuestion;
    }

    public void setSurveyQuestion(boolean isSurveyQuestion) {
        this.isSurveyQuestion = isSurveyQuestion;
    }

    public void setAssessmentScore(Scores score) {
        this.score = score;
    }

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setButtonColor(int color, boolean isEnabled) {

        try {
            Drawable actionDrawable = Objects.requireNonNull(requireActivity()).getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(actionDrawable),
                    color
            );
            question_action_button.setBackground(actionDrawable);
            question_action_button.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void setData(QuestionBaseModel questionBaseModel) {

        try {
            if (isSurveyQuestion && surveyFunctionsAndClicks != null) {
                surveyFunctionsAndClicks.enableSwipe(question_base_frame);
            }
            String currentQuestion = (questionBaseModel.getQuestionIndex() + 1) + "/" + questionBaseModel.getTotalCards();
            if (currentQuestion.contains("/")) {
                String[] spilt = currentQuestion.split("/");
                Spannable spannable = new SpannableString(currentQuestion);
                spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), currentQuestion.length(), 0);
                question_count_num.setText(spannable);
            } else {
                question_count_num.setText(currentQuestion);
            }
            question_count_num.setVisibility(View.VISIBLE);
            if ((questionBaseModel.getBgImage() != null) && (!questionBaseModel.getBgImage().isEmpty())) {
                try {
                    backgroundImage = questionBaseModel.getBgImage();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            setQuestionData(questions);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setQuestionData(DTOQuestions questions) {
        try {
            isTimeOut = false;
            if (questions != null) {
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(questions);
                    if (isReviewMode) {
                        courseContentHandlingInterface.cancelTimer();
                    }
                }
                if (assessmentContentHandler != null) {
                    if (isReviewMode) {
                        assessmentContentHandler.cancelTimerForReview();
                        assessmentContentHandler.showBottomBar();
                    }
                }
                random = new Random();
                if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {
                    //question.setText(questions.getQuestion());
                    if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                        question_katex.setTextColor(getResources().getColor(R.color.primary_text));
                        question_katex.setTextColorString("#212121");
                        question_katex.setText(questions.getQuestion());
                        question_katex.setVisibility(View.VISIBLE);
                        question.setVisibility(View.GONE);
                    } else {
                        question_katex.setVisibility(View.GONE);
                        if (questions.getQuestion().contains("<li>") || questions.getQuestion().contains("</li>") ||
                                questions.getQuestion().contains("<ol>") || questions.getQuestion().contains("</ol>") ||
                                questions.getQuestion().contains("<p>") || questions.getQuestion().contains("</p>")) {
                            question_description_webView.setVisibility(View.VISIBLE);
                            question.setVisibility(View.GONE);
                            question_description_webView.setBackgroundColor(Color.TRANSPARENT);
                            String text = OustSdkTools.getDescriptionHtmlFormat(questions.getQuestion());
                            final WebSettings webSettings = question_description_webView.getSettings();
                            // Set the font size (in sp).
                            webSettings.setDefaultFontSize(18);
                            question_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                        } else {
                            question.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                question.setText(Html.fromHtml(questions.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                question.setText(Html.fromHtml(questions.getQuestion()));
                            }
                        }
                    }
                }
                if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(questions.getBgImg())
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage)
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());
                expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));

                if (score != null && (isReviewMode || isExamMode)) {
                    media_choose_layout.setVisibility(View.GONE);
                    if (!isExamMode) {
                        question_action_button.setVisibility(View.GONE);
                    }
                    try {
                        if (score.getQuestion() != 0 && questions.getQuestionId() != 0 && score.getQuestion() == questions.getQuestionId()) {
                            if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                                score.setQuestionType(QuestionCategory.UPLOAD_IMAGE);
                            } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                                score.setQuestionType(QuestionCategory.UPLOAD_AUDIO);
                            } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                                score.setQuestionType(QuestionCategory.UPLOAD_VIDEO);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (score.getQuestionType() != null && score.getQuestionType().equalsIgnoreCase(QuestionCategory.UPLOAD_IMAGE)) {
                        if (score.getAnswer() != null && !score.getAnswer().isEmpty()) {
                            answer_image_lay.setVisibility(View.VISIBLE);
                            preview_video_lay.setVisibility(View.GONE);
                            image_preview_card.setVisibility(View.VISIBLE);
                            preview_expand_icon.setVisibility(View.VISIBLE);
                            if (!isExamMode) {
                                delete_icon.setVisibility(View.GONE);
                            }

                            String imageUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "mediaUploadCard/Image/" + score.getAnswer();
                            Log.e(TAG, "setQuestionData: imgUrl -> " + imageUrl);
                            Glide.with(requireContext())
                                    .asBitmap()
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            answer_image.setImageBitmap(resource);
                                            answer_image.buildDrawingCache();
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                        } else {
                            if (!isExamMode) {
                                answer_image_lay.setVisibility(View.GONE);
                                no_media_found.setVisibility(VISIBLE);
                                no_media_found.setText(requireContext().getText(R.string.no_image_uploaded));
                            } else {
                                mediaHandling();
                            }
                        }
                    } else if (score.getQuestionType() != null && score.getQuestionType().equalsIgnoreCase(QuestionCategory.UPLOAD_AUDIO)) {
                        if (score.getAnswer() != null && !score.getAnswer().isEmpty()) {
                            audio_play_record_lay.setVisibility(View.VISIBLE);
                            audioUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "mediaUploadCard/Audio/" + score.getAnswer();
                            Log.e(TAG, "setQuestionData: Audio Url--> " + audioUrl);

                            mIsAudioPlaying = false;
                            mIsAudioRecording = true;
                            setAudioLayoutForRecord();
                        } else {
                            if (!isExamMode) {
                                answer_image_lay.setVisibility(View.GONE);
                                no_media_found.setVisibility(VISIBLE);
                                no_media_found.setText(requireContext().getText(R.string.no_audio_uploaded));
                            } else {
                                mediaHandling();
                            }
                        }
                    } else if (score.getQuestionType() != null && score.getQuestionType().equalsIgnoreCase(QuestionCategory.UPLOAD_VIDEO)) {
                        if (score.getAnswer() != null && !score.getAnswer().isEmpty()) {

                            videoUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "mediaUploadCard/Video/" + score.getAnswer();
                            handleVideoPreview(null);
                        } else {
                            if (!isExamMode) {
                                answer_image_lay.setVisibility(View.GONE);
                                no_media_found.setVisibility(VISIBLE);
                                no_media_found.setText(requireContext().getText(R.string.no_video_uploaded));
                            } else {
                                mediaHandling();
                            }
                        }
                    } else if (!isReviewMode) {
                        mediaHandling();
                    }
                } else {
                    mediaHandling();
                }

                upload_image.setOnClickListener(v -> checkPermission(true));
                capture_image.setOnClickListener(v -> checkPermission(false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void mediaHandling() {
        try {
            media_choose_layout.setVisibility(VISIBLE);
            if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                String tapText = getResources().getString(R.string.tap_capture_image);
                if (questions.isEnableGalleryUpload()) {
                    tapText = getResources().getString(R.string.tap_upload_capture_image);
                    gallery_layout.setVisibility(View.VISIBLE);
                    upload_image.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
                    upload_text.setText(getResources().getString(R.string.upload));
                } else {
                    gallery_layout.setVisibility(View.GONE);
                }
                capture_image.setImageDrawable(getResources().getDrawable(R.drawable.capture_image));
                capture_text.setText(getResources().getString(R.string.camera));
                tap_message.setText(tapText);
            } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                String tapText = getResources().getString(R.string.tap_record_video);
                if (questions.isEnableGalleryUpload()) {
                    tapText = getResources().getString(R.string.tap_upload_record_video);
                    gallery_layout.setVisibility(View.VISIBLE);
                    upload_image.setImageDrawable(getResources().getDrawable(R.drawable.upload_video));
                    upload_text.setText(getResources().getString(R.string.upload));
                } else {
                    gallery_layout.setVisibility(View.GONE);
                }
                capture_image.setImageDrawable(getResources().getDrawable(R.drawable.record_video));
                capture_text.setText(getResources().getString(R.string.record));
                tap_message.setText(tapText);
            } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                String tapText = getResources().getString(R.string.tap_record_audio);
                if (questions.isEnableGalleryUpload()) {
                    tapText = getResources().getString(R.string.tap_upload_record_audio);
                    gallery_layout.setVisibility(View.VISIBLE);
                    upload_image.setImageDrawable(getResources().getDrawable(R.drawable.upload_audio));
                    upload_text.setText(getResources().getString(R.string.upload));
                } else {
                    gallery_layout.setVisibility(View.GONE);
                }
                capture_image.setImageDrawable(getResources().getDrawable(R.drawable.record_audio));
                capture_text.setText(getResources().getString(R.string.record));
                tap_message.setText(tapText);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageQuestionImage(String url, String image) {
        try {
            if (url == null) {
                url = image;
            }
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
                if (!url.contains("oustlearn_")) {
                    url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
                }
                File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    question_image_lay.setVisibility(View.VISIBLE);
                    expand_icon.setVisibility(View.VISIBLE);
//                    question_image.setImageURI(uri);
                    Glide.with(Objects.requireNonNull(requireActivity())).load(uri)
                            .apply(new RequestOptions().override(720, 1280))
                            .into(question_image);
                } else {
                    if ((image != null) && (!image.isEmpty())) {
                        byte[] imageByte = Base64.decode(image, 0);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                        question_image_lay.setVisibility(View.VISIBLE);
                        expand_icon.setVisibility(View.VISIBLE);
//                        question_image.setImageBitmap(decodedByte);
                        Glide.with(Objects.requireNonNull(requireActivity())).load(decodedByte)
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_image);
                        try {
                            if (decodedByte == null) {
                                Glide.with(Objects.requireNonNull(requireActivity()))
                                        .asBitmap()
                                        .apply(new RequestOptions().override(720, 1280))
                                        .load(image).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                try {
                                                    question_image.setImageBitmap(resource);
                                                    Log.e("MUQuestion", "Question Image loaded url " + resource + " file " + file);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Log.e("MUQuestion", "Question Image exception url " + e.getMessage());
                                                }

                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gifZoomPopup(final Drawable gif) {
        try {
            @SuppressLint("InflateParams")
            View popUpView = Objects.requireNonNull(requireActivity()).getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = OustSdkTools.createPopUp(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(gif);
            ImageButton imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
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
            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
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

    private void checkPermission(boolean isUpload) {
        try {
            if (isUpload) {
                if (isPermissionGrantedForUpload()) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE);
                } else {
                    isCamOpen = false;
                    if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                        openIntentForImage();
                    } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                        openIntentForVideo();
                    } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                        openIntentForAudio();
                    }
                }
            } else {
                if (isPermissionGrantedForCapture()) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA);
                } else {
                    if ((questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) ||
                            (questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                        boolean record = OustSdkTools.checkPermissionRecord(getActivity());
                        if (record) {
                            isCamOpen = true;
                            startedRecording = false;
                            openCamera();
                        } else {
                            OustSdkTools.showToast("Please give permission to record audio");
                        }
                    } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                        media_choose_layout.setVisibility(View.GONE);
                        audio_play_record_lay.setVisibility(View.VISIBLE);
                        boolean record = OustSdkTools.checkPermissionRecord(getActivity());
                        if (record) {
                            isCamOpen = true;
                            setAudioLayoutForRecord();
                        } else {
                            OustSdkTools.showToast("Please give permission to record audio");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openIntentForImage() {
        try {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
//            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImage, UPLOAD_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openIntentForVideo() {
        try {
            Intent pickVideo = new Intent(Intent.ACTION_PICK);
            pickVideo.setType("video/*");
//            Intent pickVideo = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickVideo, UPLOAD_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openIntentForAudio() {
        try {
           /* Intent pickAudio = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickAudio, UPLOAD_AUDIO);*/
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, UPLOAD_AUDIO);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCamera() {
        try {
            texture_layout.setVisibility(View.VISIBLE);
            image_capture_layout.setVisibility(View.VISIBLE);
            texture_view_camera.setVisibility(VISIBLE);
            if (texture_view_camera.isAvailable()) {
                if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                    isCameraFacingRear = true;
                    openCameraTexture(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                    configureTransform(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                    Activity activity = getActivity();
                    CameraManager manager = (CameraManager) Objects.requireNonNull(activity).getSystemService(Context.CAMERA_SERVICE);
                    manager.openCamera(mCameraId, mStateCallback, null);
                } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                    isCameraFacingRear = true;
                    openCameraForVideo(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                }
            } else {
                texture_view_camera.setSurfaceTextureListener(mSurfaceTextureListener);
            }
            capture_icon.setOnClickListener(v -> {
                startedRecording = true;
                OustSdkTools.oustTouchEffect(capture_icon, 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    stopBackgroundThread();
                    if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                        captureImage();
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                        if (!mIsRecordingVideo) {
                            startRecordingVideo();
                        } else {
                            stopRecordingVideo(false);
                        }
                    }

                }
            });

            texture_close.setOnClickListener(v -> {
                OustSdkTools.oustTouchEffect(texture_close, 100);
                if (texture_view_camera.getVisibility() == VISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mIsRecordingVideo) {
                            stopRecordingVideo(true);
                        }

                        stopBackgroundThread();
                        closeCamera();
                    }

                    image_capture_layout.setVisibility(View.GONE);
                    texture_layout.setVisibility(View.GONE);
                    isCamOpen = false;
                }
            });

            camera_switch.setOnClickListener(v -> {
                try {
                    if (!startedRecording) {
                        isCameraFacingRear = !isCameraFacingRear;
                        closeCamera();
                        if (texture_view_camera.isAvailable()) {
                            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                                openCameraTexture(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                                configureTransform(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                                Activity activity = getActivity();
                                CameraManager manager = (CameraManager) Objects.requireNonNull(activity).getSystemService(Context.CAMERA_SERVICE);
                                manager.openCamera(mCameraId, mStateCallback, null);
                            } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                                openCameraForVideo(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                            }
                        } else {
                            texture_view_camera.setSurfaceTextureListener(mSurfaceTextureListener);
                        }
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.camera_started_recording));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == UPLOAD_IMAGE) {
                    extractDataFromIntent(data, "UPLOAD_IMAGE");
                } else if (requestCode == UPLOAD_AUDIO) {
                    extractDataFromIntent(data, "UPLOAD_AUDIO");
                } else if (requestCode == UPLOAD_VIDEO) {
                    extractDataFromIntent(data, "UPLOAD_VIDEO");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractDataFromIntent(Intent data, String fileType) {
        try {
            if (data != null && fileType != null && !fileType.isEmpty()) {
                Uri dataUri = data.getData();
                String filePath = null;
                if (dataUri != null) {
                    Log.e(TAG, "extractDataFromIntent: dataUri--> " + dataUri.getPath());
                    if (fileType.equalsIgnoreCase("UPLOAD_IMAGE")) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = Objects.requireNonNull(requireActivity()).getContentResolver().query(dataUri,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath = cursor.getString(columnIndex);
                            //File imgFile = new  File(filePath);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(filePath, options);
                            final int REQUIRED_SIZE = 1000;
                            int scale = 1;
                            while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                                scale *= 2;
                            options.inSampleSize = scale;
                            options.inJustDecodeBounds = false;
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                            answer_image_lay.setVisibility(View.VISIBLE);
                            media_choose_layout.setVisibility(View.GONE);
                            Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                            answer_image.setImageBitmap(inputBitmap);
                            cursor.close();
                            imageFile = File.createTempFile("imageMedia.jpg", null, Objects.requireNonNull(requireActivity()).getCacheDir());
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] b = byteArrayOutputStream.toByteArray();
                            FileOutputStream fos = new FileOutputStream(imageFile);
                            fos.write(b);
                            fos.flush();
                            fos.close();
                            setButtonColor(color, true);
                        }
                    } else if (fileType.equalsIgnoreCase("UPLOAD_AUDIO")) {
                        if (DocumentsContract.isDocumentUri(getActivity(), dataUri)) {
                            String wholeID = DocumentsContract.getDocumentId(dataUri);
                            String[] splits = wholeID.split(":");
                            if (splits.length == 2) {
                                String id = splits[1];
                                String[] column = {MediaStore.Audio.Media.DATA};
                                String sel = MediaStore.Audio.Media._ID + "=?";
                                Cursor cursor = Objects.requireNonNull(requireActivity()).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        column, sel, new String[]{id}, null);
                                if (cursor != null) {
                                    int columnIndex = cursor.getColumnIndex(column[0]);
                                    if (cursor.moveToFirst()) {
                                        filePath = cursor.getString(columnIndex);
                                    }
                                    cursor.close();
                                }
                            }

                            if (filePath == null) {
                                filePath = FilePath.getRealPathFromUri(getActivity(), dataUri);
                                if (filePath != null) {
                                    audioFile = new File(filePath);
                                    audio_play_record_lay.setVisibility(View.VISIBLE);
                                    media_choose_layout.setVisibility(View.GONE);
                                    uploadFileName = audioFile.getPath();
                                    setAudioLayout();
                                    setButtonColor(color, true);
                                }
                            }
                        } else {
                            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                            Cursor cursor = Objects.requireNonNull(requireActivity()).getContentResolver().query(dataUri,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                try {
                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    cursor.moveToFirst();
                                    filePath = cursor.getString(columnIndex);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                    filePath = dataUri.getPath();
                                }
                                cursor.close();
                            }
                        }

                        if (filePath != null) {
                            audioFile = new File(filePath);
                            audio_play_record_lay.setVisibility(View.VISIBLE);
                            media_choose_layout.setVisibility(View.GONE);
                            uploadFileName = audioFile.getPath();
                            setAudioLayout();
                            setButtonColor(color, true);
                        }
                    } else if (fileType.equalsIgnoreCase("UPLOAD_VIDEO")) {
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                        Cursor cursor = Objects.requireNonNull(requireActivity()).getContentResolver().query(dataUri,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath = cursor.getString(columnIndex);
                            videoFile = new File(filePath);
                            uploadFileName = videoFile.getPath();
                            answer_image_lay.setVisibility(View.VISIBLE);
                            media_choose_layout.setVisibility(View.GONE);
                            Bitmap thumbnail = null;
                            if (videoFile != null) {
                                thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                            }

                            handleVideoPreview(thumbnail);

                            setButtonColor(color, true);
                            cursor.close();
                            setButtonColor(color, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e(TAG, "extractDataFromIntent: catch");
            answer_image_lay.setVisibility(View.GONE);
            audio_play_record_lay.setVisibility(View.GONE);
            media_choose_layout.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPermissionGrantedForUpload() {
        return ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean isPermissionGrantedForCapture() {
        if (isPermissionGrantedForUpload()) {
            return ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()),
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        }
    }

    private void startBackgroundThread() {
        try {
            mBackgroundThread = new HandlerThread("CameraBackground", Process.THREAD_PRIORITY_BACKGROUND);
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCameraTexture(int textureWidth, int textureHeight) {
        try {
            Activity activity = getActivity();
            if (null == activity || activity.isFinishing()) {
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                    CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
                    try {
                        for (String cameraId : manager.getCameraIdList()) {
                            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                            if (isCameraFacingRear) {
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
//                                throw new RuntimeException("Cannot get available preview sizes");
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
                            int rotatedPreviewWidth = textureWidth;
                            int rotatedPreviewHeight = textureHeight;
                            int maxPreviewWidth = displaySize.x;
                            int maxPreviewHeight = displaySize.y;

                            if (swappedDimensions) {
                                rotatedPreviewWidth = textureHeight;
                                rotatedPreviewHeight = textureWidth;
                                maxPreviewWidth = displaySize.y;
                                maxPreviewHeight = displaySize.x;
                            }
                            if (maxPreviewHeight > textureHeight) {
                                maxPreviewHeight = textureHeight;
                            }

                            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);

                            int orientation = getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                texture_view_camera.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                            } else {
                                texture_view_camera.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                            }
                            mCameraId = cameraId;
                            return;
                        }
                    } catch (CameraAccessException | NullPointerException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                isCamOpen = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCameraForVideo(int textureWidth, int textureHeight) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Time out waiting to lock camera opening.");
                }
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (isCameraFacingRear) {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            continue;
                        }
                    } else {
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                            continue;
                        }
                    }

                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    if (map == null) {
                        throw new RuntimeException("Cannot get available preview sizes");
                    }
                    mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                            textureWidth, textureHeight, mVideoSize);

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        texture_view_camera.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    } else {
                        texture_view_camera.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    }
                    configureTransform(textureWidth, textureHeight);
                    releaseVideoMediaRecorder();
                    mMediaRecorder = new MediaRecorder();
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    manager.openCamera(cameraId, mStateCallback, null);
                    return;
                }
            }
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private static Size chooseVideoSize(android.util.Size[] choices) {
        for (Size size : choices) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                    return size;
                }
            }
        }
        return choices[choices.length - 1];
    }

    private void releaseVideoMediaRecorder() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();
                mMediaRecorder.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            try {
                if (mMediaRecorder != null) {
                    mMediaRecorder.reset();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void startRecordingVideo() {
        if (null == mCameraDevice || !texture_view_camera.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = texture_view_camera.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            mRecorderSurface = mMediaRecorder.getSurface();
            surfaces.add(mRecorderSurface);
            mPreviewBuilder.addTarget(mRecorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                    Objects.requireNonNull(requireActivity()).runOnUiThread(() -> {
                        // UI
                        mIsRecordingVideo = true;
                        // Start recording
                        startVideoTimer();
                        try {
                            mMediaRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                            releaseVideoMediaRecorder();
                            stopVideoTimer();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(getActivity(), "" + getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }, mBackgroundHandler);
        } catch (IOException | CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setUpMediaRecorder() throws IOException {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final Activity activity = getActivity();
                if (null == activity) {
                    return;
                }

                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                File file = getOutputMediaFile();
                assert file != null;
                mMediaRecorder.setOutputFile(file.getPath());
                mMediaRecorder.setVideoEncodingBitRate(10000000);
                mMediaRecorder.setVideoFrameRate(30);
                mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                if (isCameraFacingRear) {
                    mMediaRecorder.setOrientationHint(90);
                } else {
                    mMediaRecorder.setOrientationHint(270);
                }
                mMediaRecorder.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private File getOutputMediaFile() {
        try {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIRECTORY_NAME);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(DIRECTORY_NAME, "Oops! Failed create " + DIRECTORY_NAME + " directory");
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            videoFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            return videoFile;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
        }
    }

    private void startVideoTimer() {
        try {
            capture_icon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_stop),
                    getResources().getColor(R.color.error_incorrect)));
            video_timer_layout.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mIsRecordingVideo) {
                        count++;
                        long millis = count;
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.SECONDS.toHours(millis),
                                TimeUnit.SECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(millis)),
                                TimeUnit.SECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(millis)));
                        video_timer_text.setText(hms);
                    }
                }

                @Override
                public void onFinish() {
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopVideoTimer() {
        try {
            capture_icon.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_round),
                    getResources().getColor(R.color.white)));
            video_timer_layout.setVisibility(View.GONE);
            if (countDownTimer != null) {
                countDownTimer.cancel();
                video_timer_text.setText("00:00:00");
                countDownTimer = null;
            }
            count = 0;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopRecordingVideo(boolean isClosed) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // UI
                mIsRecordingVideo = false;
                // Stop recording
                try {
                    mPreviewSession.stopRepeating();
                    mPreviewSession.abortCaptures();
                    closePreviewSession();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                releaseVideoMediaRecorder();
                stopVideoTimer();
                isCameraFacingRear = true;
                closeCamera();
                stopBackgroundThread();
                if (!isClosed) {
                    previewVideo();
                } else {
                    videoFile = null;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void previewVideo() {
        try {
            texture_layout.setVisibility(View.GONE);
            image_capture_layout.setVisibility(View.GONE);
            closeCamera();
            answer_image_lay.setVisibility(View.VISIBLE);
            media_choose_layout.setVisibility(View.GONE);
            Bitmap thumbnail = null;
            Log.e(TAG, "previewVideo:-3- " + videoFile);
            if (videoFile != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }


            handleVideoPreview(thumbnail);

            setButtonColor(color, true);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            OustStaticVariableHandling.getInstance().setCameraStarted(false);
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();
            if (null != texture_view_camera) {
                configureTransform(texture_view_camera.getWidth(), texture_view_camera.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    @SuppressLint("NewApi")
    private void startPreview() {
        try {
            if (null == mCameraDevice || !texture_view_camera.isAvailable() || null == mPreviewSize) {
                return;
            }
            closePreviewSession();
            SurfaceTexture texture = texture_view_camera.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.e(TAG, "onConfigureFailed: Failed ");
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    private void closePreviewSession() {
        try {
            if (mPreviewSession != null) {
                mPreviewSession.close();
                mPreviewSession = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @SuppressLint("NewApi")
    private void updatePreview() {
        try {
            if (null == mCameraDevice) {
                return;
            }
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        try {
            builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @SuppressLint("NewApi")
    private void configureTransform(int viewWidth, int viewHeight) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Activity activity = getActivity();
                if (null == texture_view_camera || null == mPreviewSize || null == activity) {
                    return;
                }
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                Matrix matrix = new Matrix();
                RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
                RectF bufferRect;
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
                texture_view_camera.setTransform(matrix);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture,
                                              int width, int height) {
            try {
                if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                    openCameraTexture(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                    configureTransform(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                    Activity activity = getActivity();
                    CameraManager manager = (CameraManager) Objects.requireNonNull(activity).getSystemService(Context.CAMERA_SERVICE);
                    manager.openCamera(mCameraId, mStateCallback, null);
                } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                    openCameraForVideo(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                }
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        }
    };


    @SuppressLint("NewApi")
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @SuppressLint("NewApi")
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

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    public void setAssessmentContentHandler(AssessmentQuestionBaseActivity assessmentContentHandler) {
        this.assessmentContentHandler = assessmentContentHandler;
    }

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }

    public static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(android.util.Size lhs, android.util.Size rhs) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
            }
            return 0;
        }
    }

    @SuppressLint("NewApi")
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

    private android.util.Size chooseOptimalSize(android.util.Size[] choices, int textureViewWidth, int textureViewHeight, android.util.Size aspectRatio) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Collect the supported resolutions that are at least as big as the preview Surface
            List<android.util.Size> bigEnough = new ArrayList<>();
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

    private void captureImage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null == mCameraDevice) {
                    return;
                }
                CameraManager manager = (CameraManager) Objects.requireNonNull(requireActivity()).getSystemService(Context.CAMERA_SERVICE);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
                android.util.Size[] jpegSizes;
                jpegSizes = Objects.requireNonNull(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageFormat.JPEG);
                int width = 640;
                int height = 480;
                if (jpegSizes != null && 0 < jpegSizes.length) {
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }

                reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

                List<Surface> outputSurfaces = new ArrayList<>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(texture_view_camera.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                int rotation = Objects.requireNonNull(requireActivity()).getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                //final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");

                ImageReader.OnImageAvailableListener readerListener = reader -> {
                    try (Image image = reader.acquireLatestImage()) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        onPictureTaken(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                };

                reader.setOnImageAvailableListener(readerListener, null);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                    }
                };
                mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    }
                }, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void onPictureTaken(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if ((bitmap.getWidth() > bitmap.getHeight()) || (bitmap.getHeight() == bitmap.getWidth())) {
                Matrix matrix = new Matrix();
                if (!isCameraFacingRear) {
                    matrix.postRotate(270);
                } else {
                    matrix.postRotate(90);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else if (!isCameraFacingRear) {
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            Bitmap createdBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
            Matrix matrix = new Matrix();
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);
            Bitmap rotatedBitmap = Bitmap.createBitmap(createdBitmap, 0, 0, createdBitmap.getWidth(), createdBitmap.getHeight(), matrix, true);
            rotatedBitmap.copy(Bitmap.Config.RGB_565, true);
            bitMapToString(rotatedBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void bitMapToString(Bitmap bitmap) {
        try {
            texture_layout.setVisibility(View.GONE);
            image_capture_layout.setVisibility(View.GONE);
            closeCamera();
            answer_image_lay.setVisibility(View.VISIBLE);
            media_choose_layout.setVisibility(View.GONE);
            Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
            answer_image.setImageBitmap(inputBitmap);
            //resetAndRemoveCamera();
            imageFile = File.createTempFile("imageMedia.jpg", null, Objects.requireNonNull(requireActivity()).getCacheDir());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] b = byteArrayOutputStream.toByteArray();
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(b);
            fos.flush();
            fos.close();
            setButtonColor(color, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void deleteConfirmation() {
        try {
            Dialog deleteDialog = new Dialog(Objects.requireNonNull(requireActivity()), R.style.DialogTheme);
            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deleteDialog.setContentView(R.layout.common_pop_up);
            deleteDialog.setCancelable(false);
            Objects.requireNonNull(deleteDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            deleteDialog.show();
            //CardView card_layout = deleteDialog.findViewById(R.id.card_layout);
            ImageView info_image = deleteDialog.findViewById(R.id.info_image);
            TextView info_title = deleteDialog.findViewById(R.id.info_title);
            TextView info_description = deleteDialog.findViewById(R.id.info_description);
            LinearLayout info_no = deleteDialog.findViewById(R.id.info_no);
            TextView info_no_text = deleteDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = deleteDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = deleteDialog.findViewById(R.id.info_yes_text);

            info_title.setText(getResources().getString(R.string.warning));
            info_description.setText(getResources().getString(R.string.delete_confirm));
            info_description.setVisibility(View.VISIBLE);
            info_no_text.setText(getResources().getString(R.string.cancel));
            info_yes_text.setText(getResources().getString(R.string.confirm));

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);//change warning drawable
            info_image.setImageDrawable(OustSdkTools.drawableColor(infoDrawable, getResources().getColor(R.color.error_incorrect)));

            info_no.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
                if (isTimeOut) {
                    if (learningModuleInterface != null) {
                        if (isAssessmentQuestion) {
                            learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                            if (!previousArrow && !nextArrow) {
                                handleNextQuestion(false, "", "", 0, false, 0);
                            } else if (previousArrow) {
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.gotoPreviousScreen();
                                }
                            } else {
                                handleNextQuestion(false, "", "", 0, false, 0);
                            }
                        } else {
                            if (questions.isThumbsUpDn()) {
                                rightwrongFlipAnimation(false, uploadFileName);
                            } else {
                                setQuestionSubmitData(false, uploadFileName);
                            }
                        }
                    }
                }
            });

            info_yes.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
                if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    imageFile = null;
                    answer_image_lay.setVisibility(View.GONE);
                    answer_image.setImageDrawable(null);
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                    videoFile = null;
                    answer_image.setImageDrawable(null);
                    video_thumbnail.setImageDrawable(null);
                    if (video_container != null && video_container.isPlaying()) {
                        video_container.stopPlayback();
                        video_container.clearAnimation();
                        video_container.suspend();
                        video_container.setVideoURI(null);
                    }

                    answer_image_lay.setVisibility(View.GONE);
                    preview_video_lay.setVisibility(View.GONE);

                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                    audioFile = null;
                    audio_play_record_lay.setVisibility(View.GONE);

                    if (responsePlayer != null) {
                        if (responsePlayer.isPlaying()) {
                            responsePlayer.pause();
                        }
                        responsePlayer.reset();
                        responsePlayer = null;
                    }

                    if (mAudioRecorder != null) {
                        mAudioRecorder.stop();
                        mAudioRecorder.reset();
                        mAudioRecorder.release();
                        mAudioRecorder = null;
                    }
                }
                mIsAudioPlaying = false;
                mIsAudioRecording = false;
                mediaHandling();
                setButtonColor(getResources().getColor(R.color.overlay_container), false);
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionSubmitData(boolean status, String uploadFileName) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            if (status) {
                if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
                    finalScr = (int) mainCourseCardClass.getXp();
                }
                handleNextQuestion(true, uploadFileName, "", finalScr, true, 0);
            } else {
                showSolutionAnswer = false;
                handleNextQuestion(false, uploadFileName, "", 0, false, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void uploadConfirmation(boolean isTimeOut) {
        try {
            Dialog uploadDialog = new Dialog(Objects.requireNonNull(requireActivity()), R.style.DialogTheme);
            uploadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            uploadDialog.setContentView(R.layout.common_pop_up);
            uploadDialog.setCancelable(false);
            Objects.requireNonNull(uploadDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            uploadDialog.show();
            //CardView card_layout = deleteDialog.findViewById(R.id.card_layout);
            ImageView info_image = uploadDialog.findViewById(R.id.info_image);
            TextView info_title = uploadDialog.findViewById(R.id.info_title);
            TextView info_description = uploadDialog.findViewById(R.id.info_description);
            LinearLayout info_no = uploadDialog.findViewById(R.id.info_no);
            TextView info_no_text = uploadDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = uploadDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = uploadDialog.findViewById(R.id.info_yes_text);

            info_title.setText(getResources().getString(R.string.confirmation));
            info_description.setText(getResources().getString(R.string.upload_confirm_msg));
            info_description.setVisibility(View.VISIBLE);
            info_no_text.setText(getResources().getString(R.string.cancel));
            info_yes_text.setText(getResources().getString(R.string.confirm));

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_info_icon);
            info_image.setImageDrawable(OustSdkTools.drawableColor(infoDrawable));

            info_no.setOnClickListener(v -> {
                if (uploadDialog.isShowing()) {
                    uploadDialog.dismiss();
                }

                if (isTimeOut) {
                    if (learningModuleInterface != null) {
                        if (isAssessmentQuestion) {
                            Log.d(TAG, "onTimeOut: isAssessmentQuestion");
                            learningModuleInterface.setAnswerAndOc(uploadedFileName, "", (int) uploadedFileScore, uploadedFileStatus, uploadedFileTime);
                            if (!previousArrow && !nextArrow) {
                                handleNextQuestion(false, "", "", 0, false, 0);
                            } else if (previousArrow) {
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.gotoPreviousScreen();
                                }
                            } else {
                                handleNextQuestion(false, "", "", 0, false, 0);
                            }
                        } else {
                            if (questions.isThumbsUpDn()) {
                                rightwrongFlipAnimation(false, uploadFileName);
                            } else {
                                setQuestionSubmitData(false, uploadFileName);
                            }
                        }
                    }
                }
            });

            info_yes.setOnClickListener(v -> {
                if (uploadDialog.isShowing()) {
                    uploadDialog.dismiss();
                }

                if (questions != null && (questions.getQuestionCategory() != null) &&
                        (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    if (imageFile != null) {
                        uploadImageToAWS();
                    }
                } else if (questions != null && (questions.getQuestionCategory() != null) &&
                        (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                    if (videoFile != null) {
                        Log.e(TAG, "uploadConfirmation:-videoFile-> " + videoFile);
                        compressVideoAndUpload();
                    }
                } else if (questions != null && (questions.getQuestionCategory() != null) &&
                        (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                    if (audioFile != null) {
                        Log.e(TAG, "uploadConfirmation:-audioFile-> " + audioFile);
                        uploadAudioToAWS();
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void uploadFailed() {
        try {
            Dialog uploadFailedDialog = new Dialog(Objects.requireNonNull(requireActivity()), R.style.DialogTheme);
            uploadFailedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            uploadFailedDialog.setContentView(R.layout.common_pop_up);
            uploadFailedDialog.setCancelable(false);
            Objects.requireNonNull(uploadFailedDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            uploadFailedDialog.show();
            //CardView card_layout = uploadFailedDialog.findViewById(R.id.card_layout);
            ImageView info_image = uploadFailedDialog.findViewById(R.id.info_image);
            TextView info_title = uploadFailedDialog.findViewById(R.id.info_title);
            TextView info_description = uploadFailedDialog.findViewById(R.id.info_description);
            LinearLayout info_no = uploadFailedDialog.findViewById(R.id.info_no);
            TextView info_no_text = uploadFailedDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = uploadFailedDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = uploadFailedDialog.findViewById(R.id.info_yes_text);

            info_title.setText(getResources().getString(R.string.upload_failed));
            info_description.setText(getResources().getString(R.string.sorry_media_upload_failed));
            info_description.setVisibility(View.VISIBLE);
            info_no_text.setText(getResources().getString(R.string.skip));
            info_yes_text.setText(getResources().getString(R.string.upload));

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);//change warning drawable
            info_image.setImageDrawable(OustSdkTools.drawableColor(infoDrawable, getResources().getColor(R.color.error_incorrect)));

            info_no.setOnClickListener(v -> {
                if (uploadFailedDialog.isShowing()) {
                    uploadFailedDialog.dismiss();
                }

                if (learningModuleInterface != null) {
                    if (isAssessmentQuestion) {
                        Log.d(TAG, "onTimeOut: isAssessmentQuestion");
                        learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                        if (!previousArrow && !nextArrow) {
                            handleNextQuestion(false, "", "", 0, false, 0);
                        } else if (previousArrow) {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.gotoPreviousScreen();
                            }
                        } else {
                            handleNextQuestion(false, "", "", 0, false, 0);
                        }
                    } else {
                        if (questions.isThumbsUpDn()) {
                            rightwrongFlipAnimation(false, uploadFileName);
                        } else {
                            setQuestionSubmitData(false, uploadFileName);
                        }
                    }
                }

            });

            info_yes.setOnClickListener(v -> {
                if (uploadFailedDialog.isShowing()) {
                    uploadFailedDialog.dismiss();
                }
                if (questions != null && (questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    if (imageFile != null) {
                        uploadImageToAWS();
                    }
                } else if (questions != null && (questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V))) {
                    if (videoFile != null) {
                        compressVideoAndUpload();
                    }
                } else if (questions != null && (questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A))) {
                    if (audioFile != null) {
                        uploadAudioToAWS();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void uploadProgress() {
        try {
            uploadProgressDialog = new Dialog(Objects.requireNonNull(requireActivity()), R.style.DialogTheme);
            uploadProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            uploadProgressDialog.setContentView(R.layout.common_upload_progress);
            uploadProgressDialog.setCancelable(false);
            Objects.requireNonNull(uploadProgressDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            uploadProgressDialog.show();
            upload_percentage_progress = uploadProgressDialog.findViewById(R.id.upload_percentage_progress);
            circularProgressIndicator = uploadProgressDialog.findViewById(R.id.tenant_progress_color);
            circularProgressIndicator.setIndicatorColor(color);
            circularProgressIndicator.setTrackColor(getResources().getColor(R.color.gray));
            upload_message = uploadProgressDialog.findViewById(R.id.upload_message);
            upload_percentage_done = uploadProgressDialog.findViewById(R.id.upload_percentage_done);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void uploadImageToAWS() {
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 amazonS3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            amazonS3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = TransferUtility.builder().s3Client(amazonS3).context(getActivity()).build();
            if (learningModuleInterface != null && !isExamMode)
                learningModuleInterface.stopTimer();
            if (activeUser != null && activeUser.getStudentKey() != null) {
                uploadFileName = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
            } else {
                uploadFileName = System.currentTimeMillis() + "_" + System.currentTimeMillis();
            }
            uploadProgress();
            final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Image/" + uploadFileName + ".jpg", imageFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        imageFile = null;
                        answer_image_lay.setVisibility(View.GONE);
                        answer_image.setImageDrawable(null);
                        media_choose_layout.setVisibility(View.VISIBLE);
                        storeFileInServer(uploadFileName + ".jpg");
                        upload_message.setText(getResources().getString(R.string.upload_success));
                        submitAnswer("mediaUploadCard/Image/" + uploadFileName + ".jpg");
                        new Handler().postDelayed(() -> {
                            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                uploadProgressDialog.dismiss();
                            }
                            if (isAssessmentQuestion) {
                                if (!previousArrow && !nextArrow) {
                                    handleNextQuestion(false, "", "", 0, false, 0);
                                } else if (previousArrow) {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.gotoPreviousScreen();
                                    }
                                } else {
                                    handleNextQuestion(false, "", "", 0, false, 0);
                                }
                            }
                        }, FIVE_HUNDRED_MILLI_SECONDS);

                    }
                    if (TransferState.FAILED.equals(observer.getState())) {
                        //handle error popup and hide progress layout
                        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                            uploadProgressDialog.dismiss();
                        }
                        uploadFailed();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                    //upload_progress percentage handle both progressBar and progressText
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        upload_percentage_progress.setProgress((int) percentage, true);
                    } else {
                        upload_percentage_progress.setProgress((int) percentage);
                    }
                    String progressText = (int) percentage + " %";
                    upload_percentage_done.setText(progressText);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("file upload error", Objects.requireNonNull(ex.getMessage()));
                    //handle error popup and hide progress layout
                    if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                        uploadProgressDialog.dismiss();
                    }
                    uploadFailed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                uploadProgressDialog.dismiss();
            }
        }
    }

    private void compressVideoAndUpload() {
        try {
            OustSdkTools.createApplicationFolder();
            File out = new File(
                    OustSdkApplication.getContext().getFilesDir(),
                    "compress.mp4"
            );
            if (out.exists()) {
                boolean b = out.delete();
                Log.e(TAG, "File delete " + b);
            }
            if (learningModuleInterface != null)
                learningModuleInterface.stopTimer();
            new VideoCompressor().execute();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Start video compression");
            uploadProgress();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(videoFile.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            //progressBar.setVisibility(View.GONE);
            if (compressed) {
                if (OustSdkTools.checkInternetStatus()) {
                    upload_percentage_progress.setProgress(0);
                    upload_percentage_done.setText(0 + "%");

                    videoFile = new File(
                            OustSdkApplication.getContext().getFilesDir(),
                            "compress.mp4"
                    );
                    Log.d(TAG, "Compressed Size: " + Formatter.formatFileSize(getActivity(), videoFile.length()));
                    uploadVideoToAWS();
                } else {
                    if (uploadProgressDialog != null && uploadProgressDialog.isShowing())
                        uploadProgressDialog.dismiss();
                    uploadFailed();
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                if (uploadProgressDialog != null && uploadProgressDialog.isShowing())
                    uploadProgressDialog.dismiss();
            }
        }
    }

    private void uploadVideoToAWS() {
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 amazonS3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            amazonS3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = TransferUtility.builder().s3Client(amazonS3).context(getActivity()).build();
            if (videoFile == null || !videoFile.getAbsoluteFile().exists()) {
                if (uploadProgressDialog != null && uploadProgressDialog.isShowing())
                    uploadProgressDialog.dismiss();
                Toast.makeText(getActivity(), getResources().getString(R.string.unable_to_select_attachment), Toast.LENGTH_SHORT).show();
            } else {
                if (learningModuleInterface != null && !isExamMode)
                    learningModuleInterface.stopTimer();

                if (activeUser != null && activeUser.getStudentKey() != null) {
                    uploadFileName = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
                } else {
                    uploadFileName = System.currentTimeMillis() + "_" + System.currentTimeMillis();
                }
                Log.e(TAG, "uploadVideoToAWS: --> videoFile -->" + videoFile);
                final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Video/" + uploadFileName + ".mp4", videoFile);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED.equals(observer.getState())) {
                            videoFile = null;
                            answer_image.setImageDrawable(null);
                            video_thumbnail.setImageDrawable(null);
                            if (video_container != null && video_container.isPlaying()) {
                                video_container.stopPlayback();
                                video_container.clearAnimation();
                                video_container.suspend();
                                video_container.setVideoURI(null);
                            }
                            preview_video_lay.setVisibility(View.GONE);
                            answer_image_lay.setVisibility(View.GONE);
                            media_choose_layout.setVisibility(View.VISIBLE);
                            Log.e(TAG, "onStateChanged:uploadFileName--> " + uploadFileName);
                            storeFileInServer(uploadFileName + ".mp4");
                            upload_message.setText(getResources().getString(R.string.upload_success));
                            submitAnswer("mediaUploadCard/Video/" + uploadFileName + ".mp4");
                            new Handler().postDelayed(() -> {
                                if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                    uploadProgressDialog.dismiss();
                                }
                                if (isAssessmentQuestion) {
                                    if (!previousArrow && !nextArrow) {
                                        handleNextQuestion(false, "", "", 0, false, 0);
                                    } else if (previousArrow) {
                                        if (learningModuleInterface != null) {
                                            learningModuleInterface.gotoPreviousScreen();
                                        }
                                    } else {
                                        handleNextQuestion(false, "", "", 0, false, 0);
                                    }
                                }
                            }, FIVE_HUNDRED_MILLI_SECONDS);

                        }
                        if (TransferState.FAILED.equals(observer.getState())) {
                            //handle error popup and hide progress layout
                            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                uploadProgressDialog.dismiss();
                            }
                            uploadFailed();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                        //upload_progress percentage handle both progressBar and progressText
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            upload_percentage_progress.setProgress((int) percentage, true);
                        } else {
                            upload_percentage_progress.setProgress((int) percentage);
                        }
                        String progressText = (int) percentage + " %";
                        upload_percentage_done.setText(progressText);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("file upload error", Objects.requireNonNull(ex.getMessage()));
                        //handle error popup and hide progress layout
                        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                            uploadProgressDialog.dismiss();
                        }
                        uploadFailed();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                uploadProgressDialog.dismiss();
            }
        }
    }

    public void rightwrongFlipAnimation(boolean status, String uploadFileName) {
        try {
            Log.e(TAG, "rightwrongFlipAnimation: status--> " + status);
            if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
                finalScr = (int) mainCourseCardClass.getXp();
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            if (status) {
                rightAnswerSound();
                animMainLayout.setVisibility(View.VISIBLE);
                if (animMainLayout.getVisibility() == View.VISIBLE) {
                    //showAllMedia();
                    OustSdkTools.setImage(coinsAnimImg, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_result_image, "scaleX", 0.0f, 1);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_result_image, "scaleY", 0.0f, 1);
                    scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
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
                                handleNextQuestion(true, uploadFileName, "", (int) finalScr, true, 0);
                            } else {
                                animateOcCoins(uploadFileName, (int) finalScr);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    try {
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

            } else {
                showSolutionAnswer = false;
                vibrateAndShake();
                wrongAnswerSound();
                animMainLayout.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                handleNextQuestion(false, "", "", 0, false, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateOcCoins(String userAns, int oc) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            if ((finalScr > 0)) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                isLevelCompleted = isReplayMode || OustStaticVariableHandling.getInstance().isLevelCompleted();
                if (isCourseCompleted || isLevelCompleted) {
                } else {
                    playAudio("coins.mp3");
                }
            }
            if (isCourseCompleted || isLevelCompleted) {
                coinsAnimImg.setVisibility(View.GONE);
                coinsAnimText.setVisibility(View.GONE);
            }
            animator1.setObjectValues(0, (finalScr));
            animator1.setDuration(SIX_HUNDRED_MILLI_SECONDS);
            animator1.addUpdateListener(valueAnimator -> {
                if (isCourseCompleted || isLevelCompleted) {
                    coinsAnimImg.setVisibility(View.GONE);
                    coinsAnimText.setVisibility(View.GONE);
                } else {
                    coinsAnimImg.setVisibility(View.VISIBLE);
                    coinsAnimText.setVisibility(View.VISIBLE);
                    coinsAnimText.setText("" + (((int) valueAnimator.getAnimatedValue())));
                }
            });
            animator1.start();
            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (isCourseCompleted || isLevelCompleted) {
                        coinsAnimImg.setVisibility(View.GONE);
                        coinsAnimText.setVisibility(View.GONE);
                    } else {
                        coinsAnimImg.setVisibility(View.VISIBLE);
                        coinsAnimText.setVisibility(View.VISIBLE);
                        coinsAnimText.setText("" + finalScr);
                    }
                    handleNextQuestion(false, userAns, "", oc, true, 0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNextQuestion(boolean status1, String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            new Handler().postDelayed(() -> {
                if (learningModuleInterface != null) {
                    animMainLayout.setVisibility(View.GONE);
                    if (isCourseQuestion) {
                        if (solutionText != null && !solutionText.isEmpty()) {
                            if (showSolution) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, true, userAns, subjectiveResponse, oc, status, time);
                                }
                            } else if (!status1 && (uploadFileName == null)) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, false, false, true, userAns, subjectiveResponse, oc, status, time);
                                }
                            } else {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.handleScreenTouchEvent(true);
                                    courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                                }
                            }
                        } else {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.handleScreenTouchEvent(true);
                                courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                            }
                        }
                    } else {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.gotoNextScreen();
                        }
                    }
                    showSolutionAnswer = true;
                }
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void uploadAudioToAWS() {
        try {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 amazonS3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            amazonS3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = TransferUtility.builder().s3Client(amazonS3).context(getActivity()).build();
            if (learningModuleInterface != null && !isExamMode)
                learningModuleInterface.stopTimer();
            if (activeUser != null && activeUser.getStudentKey() != null) {
                uploadFileName = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
            } else {
                uploadFileName = System.currentTimeMillis() + "_" + System.currentTimeMillis();
            }
            String key = "mediaUploadCard/Audio/" + uploadFileName + ".mp3";
            Log.e(TAG, "uploadAudioToAWS: uploadFileName--> " + uploadFileName + "  audioFile--> " + audioFile);
            uploadProgress();
            final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, key, audioFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        audioFile = null;
                        audio_play_record_lay.setVisibility(View.GONE);
                        media_choose_layout.setVisibility(View.VISIBLE);
                        storeFileInServer(uploadFileName + ".mp3");
                        upload_message.setText(getResources().getString(R.string.upload_success));
                        submitAnswer("mediaUploadCard/Audio/" + uploadFileName + ".mp3");
                        new Handler().postDelayed(() -> {
                            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                uploadProgressDialog.dismiss();
                            }
                            if (isAssessmentQuestion) {
                                if (!previousArrow && !nextArrow) {
                                    handleNextQuestion(false, "", "", 0, false, 0);
                                } else if (previousArrow) {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.gotoPreviousScreen();
                                    }
                                } else {
                                    handleNextQuestion(false, "", "", 0, false, 0);
                                }
                            }
                        }, FIVE_HUNDRED_MILLI_SECONDS);

                    }
                    if (TransferState.FAILED.equals(observer.getState())) {
                        //handle error popup and hide progress layout
                        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                            uploadProgressDialog.dismiss();
                        }
                        uploadFailed();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                    //upload_progress percentage handle both progressBar and progressText
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        upload_percentage_progress.setProgress((int) percentage, true);
                    } else {
                        upload_percentage_progress.setProgress((int) percentage);
                    }
                    String progressText = (int) percentage + " %";
                    upload_percentage_done.setText(progressText);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("file upload error", Objects.requireNonNull(ex.getMessage()));
                    //handle error popup and hide progress layout
                    if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                        uploadProgressDialog.dismiss();
                    }
                    uploadFailed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                uploadProgressDialog.dismiss();
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setAudioLayout() {
        try {
            audio_play_progress.setVisibility(View.VISIBLE);
            play_progress.setProgress(0);
            String durationText = "00:00";
            play_duration.setText(durationText);
            audio_action_text.setText(getResources().getString(R.string.play_text));

            mediaSeekHandler = new Handler();
            responsePlayer = new MediaPlayer();
            responsePlayer.setDataSource(uploadFileName);
            responsePlayer.prepare();

            int maxProgress = responsePlayer.getDuration() / 1000;
            play_progress.setMax(maxProgress);
            progressValue = maxProgress;
            progresssetValue = maxProgress;
            handleAudioProgress(maxProgress);

            audio_action.setOnClickListener(v -> {
                if (responsePlayer != null) {
                    if (responsePlayer.isPlaying()) {
                        responsePlayer.pause();
                        audio_action_text.setText(getResources().getString(R.string.play_text));
                        audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_play));
                        audio_action_description.setText(getResources().getString(R.string.pause));
                        if (audioProgressTimer != null) {
                            audioProgressTimer.cancel();
                        }
                    } else {
                        responsePlayer.start();
                        audio_action_text.setText(getResources().getString(R.string.pause_text));
                        audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_pause));
                        audio_action_description.setText(getResources().getString(R.string.playing_text));
                        if (audioProgressTimer != null) {
                            handleAudioProgress(progressValue - resumeProgress);
                            progresssetValue = progressValue - resumeProgress;
                            audioProgressTimer.start();
                        }
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setAudioLayoutForRecord() {
        try {
            audio_play_progress.setVisibility(View.VISIBLE);
            audio_delete.setVisibility(View.INVISIBLE);
            if (!isReviewMode) {
                play_progress.setProgress(0);
                String durationText = "00:00";
                play_duration.setText(durationText);
                audio_action_text.setText(getResources().getString(R.string.record));
                audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_record));
            }

            if (isReviewMode) {
                if (!mIsAudioPlaying) {
                    stopPlayingAudio();
                    if (!isReviewMode) {
                        audio_delete.setVisibility(View.VISIBLE);
                    }
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                        count = 0;
                    }
                    audio_play_progress.setVisibility(View.VISIBLE);
                    play_progress.setProgress(0);
                    String durationTexts = "00:00";
                    play_duration.setText(durationTexts);
                    audio_action_text.setText(getResources().getString(R.string.play_text));
                    audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_play));

                    mediaSeekHandler = new Handler();
                    responsePlayer = new MediaPlayer();
                    try {
                        if (!isReviewMode) {
                            responsePlayer.setDataSource(uploadFileName);
                        } else {
                            responsePlayer.setDataSource(audioUrl);
                        }
                        responsePlayer.prepare();

                        int maxProgress = responsePlayer.getDuration() / 1000;
                        play_progress.setMax(maxProgress);
                        progressValue = maxProgress;
                        progresssetValue = maxProgress;
                        handleAudioProgress(maxProgress);

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    mIsAudioPlaying = true;
                    if (audioFile != null) {
                        setButtonColor(color, true);
                    }
                }
            }

            audio_action.setOnClickListener(v -> {

                if (!mIsAudioRecording) {
                    OustStaticVariableHandling.getInstance().setCameraStarted(false);
                    if (learningModuleInterface != null) {
                        learningModuleInterface.handleQuestionAudio(false);
                    }
                    stopPlayingAudio();
                    deleteRecording();

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                        count = 0;
                    }

                    countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (mIsAudioRecording) {
                                count++;
                                long millis = count;
                                int seconds = (int) (millis / 60);
                                //int minutes = seconds / 60;
                                seconds = seconds % 60;
                                play_duration.setText(String.format("%02d:%02d", seconds, millis));
                            }
                            if (!mIsAudioRecording && countDownTimer != null) {
                                countDownTimer.cancel();
                                countDownTimer = null;
                                count = 0;
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();

                    mIsAudioRecording = true;
                    String fileName = "recordedAudio.mp3";
                    audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_pause));
                    audio_action_text.setText(getResources().getString(R.string.pause_text));
                    audio_action_description.setText(getResources().getString(R.string.record));
                    try {
//                        OustSdkApplication.getContext().getFilesDir().getPath() + "/" + CreateRandomAudioFileName(5) +
                        /*audioFile = File.createTempFile(OustSdkApplication.getContext().getFilesDir().getPath(), fileName, null);
                        audioFile = OustSdkApplication.getContext().getFilesDir().getPath() +fileName;*/
                        audioPathInDevice = OustSdkApplication.getContext().getFilesDir().getPath() + "/" /*+ CreateRandomAudioFileName(5)*/ + fileName;
                        uploadFileName = OustSdkApplication.getContext().getFilesDir().getPath() + "/" /*+ CreateRandomAudioFileName(5)*/ + fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    setAudioRecorder();
                    try {
                        mAudioRecorder.prepare();
                        mAudioRecorder.start();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                } else {
                    if (!mIsAudioPlaying) {
                        stopPlayingAudio();
                        if (!isReviewMode) {
                            audio_delete.setVisibility(View.VISIBLE);
                        }
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                            count = 0;
                        }
                        audio_play_progress.setVisibility(View.VISIBLE);
                        play_progress.setProgress(0);
                        String durationTexts = "00:00";
                        play_duration.setText(durationTexts);
                        audio_action_text.setText(getResources().getString(R.string.play_text));
                        audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_play));

                        mediaSeekHandler = new Handler();
                        responsePlayer = new MediaPlayer();
                        try {
                            if (!isReviewMode) {
                                responsePlayer.setDataSource(uploadFileName);
                            } else {
                                responsePlayer.setDataSource(audioUrl);
                            }
                            responsePlayer.prepare();

                            int maxProgress = responsePlayer.getDuration() / 1000;
                            play_progress.setMax(maxProgress);
                            progressValue = maxProgress;
                            progresssetValue = maxProgress;
                            handleAudioProgress(maxProgress);

                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }

                        mIsAudioPlaying = true;
                        audioFile = new File(audioPathInDevice);
                        if (audioFile != null) {
                            setButtonColor(color, true);
                        }
                    } else {
                        if (responsePlayer != null) {
                            if (responsePlayer.isPlaying()) {
                                responsePlayer.pause();
                                audio_action_text.setText(getResources().getString(R.string.play_text));
                                audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_play));
                                audio_action_description.setText(getResources().getString(R.string.pause));
                                if (audioProgressTimer != null) {
                                    audioProgressTimer.cancel();
                                }
                            } else {
                                responsePlayer.start();
                                audio_action_text.setText(getResources().getString(R.string.pause_text));
                                audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.media_pause));
                                audio_action_description.setText(getResources().getString(R.string.playing_text));
                                if (audioProgressTimer != null) {
                                    handleAudioProgress(progressValue - resumeProgress);
                                    progresssetValue = progressValue - resumeProgress;
                                    audioProgressTimer.start();
                                }
                            }
                        }
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopPlayingAudio() {
        try {
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
                mAudioRecorder.reset();
                mAudioRecorder.release();
                mAudioRecorder = null;
                audio_delete.setVisibility(View.VISIBLE);
            }

            if (audioFile != null) {
                setButtonColor(color, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteRecording() {
        try {
            play_progress.setProgress(0);
            String durationText = "00:00";
            play_duration.setText(durationText);
            audio_action_text.setText(getResources().getString(R.string.record));
            audio_action_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_record));
            audioPathInDevice = null;
            audio_delete.setVisibility(View.INVISIBLE);
            audioFile = null;
            count = 0;
            countDownTimer = null;
            mIsAudioRecording = false;

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAudioRecorder() {
        try {
            mAudioRecorder = new MediaRecorder();
            mAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mAudioRecorder.setOutputFile(audioPathInDevice);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void storeFileInServer(String uploadFileName) {
        try {
            if (isAssessmentQuestion) {
                int questionXp = 20;
                if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
                    questionXp = (int) mainCourseCardClass.getXp();
                }
                learningModuleInterface.setAnswerAndOc(uploadFileName, "", questionXp, true, 0);
            } else {
                if (questions.isThumbsUpDn()) {
                    rightwrongFlipAnimation(true, uploadFileName);
                } else {
                    setQuestionSubmitData(true, uploadFileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void submitAnswer(String answer) {
        try {
            if (answer != null && !answer.isEmpty()) {
                String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
                Log.e(TAG, "submitAnswer:node->  " + node);
                OustFirebaseTools.getRootRef().child(node).setValue(answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleAudioProgress(int maxProgress) {
        try {
            audioProgressTimer = new CountDownTimer(maxProgress * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (responsePlayer != null && responsePlayer.isPlaying()) {
                        progresssetValue--;
                        play_progress.setProgress(progresssetValue);
                        long totalDuration = millisUntilFinished / 1000;
                        resumeProgress++;
                        int minutes = (int) (totalDuration / 60);
                        int seconds = (int) totalDuration % 60;
                        play_duration.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        resumeProgress = 0;
                        play_progress.setProgress(0);
                        play_duration.setText("00:00");
                        audio_action_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.play_text));
                        audio_action_image.setImageDrawable(OustSdkApplication.getContext().getResources().getDrawable(R.drawable.media_play));
                        audio_action_description.setText(OustSdkApplication.getContext().getResources().getString(R.string.pause));
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            };
        } catch (Exception e) {
            Log.e(TAG, "handleAudioProgress: Exception--> " + e.getMessage());
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleVideoPreview(Bitmap thumbnail) {
        try {
            if (videoFile != null) {
                preview_video_lay.setVisibility(View.VISIBLE);
                image_preview_card.setVisibility(View.GONE);
                preview_expand_icon.setVisibility(View.GONE);

                if (thumbnail != null)
                    video_thumbnail.setImageBitmap(thumbnail);

                video_container.setVideoPath(videoFile.getAbsolutePath());
                video_container.setMediaController(new android.widget.MediaController(getActivity()));
                video_container.setOnErrorListener((mp, what, extra) -> {
                    video_thumbnail.setVisibility(VISIBLE);
                    return false;
                });

                video_container.setOnPreparedListener(mp -> {
                    video_thumbnail.setVisibility(View.GONE);
                    mp.setOnVideoSizeChangedListener((mp1, arg1, arg2) -> video_thumbnail.setVisibility(View.GONE));
                });

                video_container.setOnCompletionListener(mp -> video_thumbnail.setVisibility(View.GONE));

                video_container.requestFocus();
                video_container.start();

            } else if (videoUrl != null) {
                answer_image_lay.setVisibility(View.VISIBLE);
                preview_video_lay.setVisibility(View.VISIBLE);
                image_preview_card.setVisibility(View.GONE);
                preview_expand_icon.setVisibility(View.GONE);

                if (!isExamMode && isReviewMode) {
                    delete_icon.setVisibility(View.GONE);
                }


                if (thumbnail != null)
                    video_thumbnail.setImageBitmap(thumbnail);

                video_container.setVideoURI(Uri.parse(videoUrl));
                video_container.setMediaController(new android.widget.MediaController(getActivity()));
                video_container.setOnErrorListener((mp, what, extra) -> {
                    video_thumbnail.setVisibility(VISIBLE);
                    return false;
                });

                video_container.setOnPreparedListener(mp -> {
                    video_thumbnail.setVisibility(View.GONE);
                    mp.setOnVideoSizeChangedListener((mp1, arg1, arg2) -> video_thumbnail.setVisibility(View.GONE));
                });

                video_container.setOnCompletionListener(mp -> video_thumbnail.setVisibility(View.GONE));

                video_container.requestFocus();
                video_container.start();


            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void vibrateAndShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            question_base_frame.startAnimation(shakeAnim);
            ((Vibrator) Objects.requireNonNull(requireActivity()).getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongAnswerSound() {
        try {
            if (mediaPlayer == null) {
                stopMediaPlayer();
                mediaPlayer = new MediaPlayer();
            }
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAnswerSound() {
        try {
            if (mediaPlayer == null) {
                stopMediaPlayer();
                mediaPlayer = new MediaPlayer();
            }
            playAudio("answer_correct.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cancelSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }

    private void stopMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            closeCamera();
            stopBackgroundThread();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            if (isCamOpen) {
                if (mIsRecordingVideo) {
                    mIsRecordingVideo = false;
                    stopVideoTimer();
                }
            }
            resetPlayers();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isCamOpen) {
            startedRecording = false;
            openCamera();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            startBackgroundThread();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            isCamOpen = false;
            resetPlayers();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            isCamOpen = false;
            resetPlayers();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetPlayers() {
        try {
            if (responsePlayer != null) {
                if (responsePlayer.isPlaying()) {
                    responsePlayer.pause();
                }

                responsePlayer.reset();
                responsePlayer = null;
            }

            if (mAudioRecorder != null) {
                mAudioRecorder.reset();
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stopBackgroundThread();
                closeCamera();
                texture_view_camera.setVisibility(View.GONE);
                image_capture_layout.setVisibility(View.GONE);
            }

            if (video_container != null && video_container.isPlaying()) {
                video_container.stopPlayback();
                video_container.clearAnimation();
                video_container.suspend();
                video_container.setVideoURI(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onTimeOut(boolean previousArrow, boolean nextArrow) {
        try {
            this.previousArrow = previousArrow;
            this.nextArrow = nextArrow;
            if (learningModuleInterface != null) {
                learningModuleInterface.closeCourseInfoPopup();
            }

            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            isTimeOut = true;
            if (score != null) {
                if (score.getAnswer() != null && !score.getAnswer().equalsIgnoreCase("")) {
                    uploadedFileName = score.getAnswer();
                } else {
                    uploadedFileName = "";
                }
                if (score.getScore() != 0) {
                    uploadedFileScore = score.getScore();
                } else {
                    uploadedFileScore = 0;
                }
                if (score.getTime() != 0) {
                    uploadedFileTime = score.getTime();
                } else {
                    uploadedFileTime = 0;
                }
                uploadedFileStatus = score.isCorrect();
            }

            if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) {
                handleTimeOutForAudio();
            } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I)) {
                handleTimeOutForImage();
            } else if (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)) {
                handleTimeOutForVideo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void handleTimeOutForAudio() {
        try {
            if (responsePlayer != null) {
                if (responsePlayer.isPlaying()) {
                    responsePlayer.pause();
                }
                responsePlayer.reset();
                responsePlayer = null;
            }
            try {
                if (mAudioRecorder != null) {
                    mAudioRecorder.reset();
                    mAudioRecorder.stop();
                    mAudioRecorder.release();
                    mAudioRecorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (audioProgressTimer != null) {
                audioProgressTimer.cancel();
                audioProgressTimer = null;
                resumeProgress = 0;
                progressValue = 0;
            }
            if (audioFile != null) {
                uploadConfirmation(true);
            } else {
                if (isAssessmentQuestion) {
                    Log.d(TAG, "onTimeOut: isAssessmentQuestion");
                    learningModuleInterface.setAnswerAndOc(uploadedFileName, "", (int) uploadedFileScore, uploadedFileStatus, uploadedFileTime);
                    if (!previousArrow && !nextArrow) {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    } else if (previousArrow) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    }
                } else {
                    if (questions.isThumbsUpDn()) {
                        rightwrongFlipAnimation(false, uploadFileName);
                    } else {
                        setQuestionSubmitData(false, uploadFileName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e(TAG, "Audio error " + e.getCause());
        }
    }

    private void handleTimeOutForImage() {
        try {
            if (texture_view_camera.getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    stopBackgroundThread();
                    closeCamera();
                    texture_view_camera.setVisibility(View.GONE);
                    image_capture_layout.setVisibility(View.GONE);
                }
            }
            if (imageFile != null) {
                uploadConfirmation(true);
            } else {
                if (isAssessmentQuestion) {
                    Log.d(TAG, "onTimeOut: isAssessmentQuestion");
                    learningModuleInterface.setAnswerAndOc(uploadedFileName, "", (int) uploadedFileScore, uploadedFileStatus, uploadedFileTime);
                    if (!previousArrow && !nextArrow) {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    } else if (previousArrow) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    }
                } else {
                    if (questions.isThumbsUpDn()) {
                        rightwrongFlipAnimation(false, uploadFileName);
                    } else {
                        setQuestionSubmitData(false, uploadFileName);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleTimeOutForVideo() {
        Log.e(TAG, "handleTimeOutForVideo: ");
        try {
            if (texture_view_camera.getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mIsRecordingVideo) {
                        stopRecordingVideo(false);
                    }
                    texture_view_camera.setVisibility(View.GONE);
                    image_capture_layout.setVisibility(View.GONE);
                }
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
                count = 0;
            }
            if (videoFile != null) {
                uploadConfirmation(true);
            } else {
                if (isAssessmentQuestion) {
                    Log.d(TAG, "onTimeOut: isAssessmentQuestion");
                    learningModuleInterface.setAnswerAndOc(uploadedFileName, "", (int) uploadedFileScore, uploadedFileStatus, uploadedFileTime);
                    if (!previousArrow && !nextArrow) {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    } else if (previousArrow) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else {
                        handleNextQuestion(false, "", "", 0, false, 0);
                    }
                } else {
                    if (questions.isThumbsUpDn()) {
                        rightwrongFlipAnimation(false, uploadFileName);
                    } else {
                        setQuestionSubmitData(false, uploadFileName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }
}