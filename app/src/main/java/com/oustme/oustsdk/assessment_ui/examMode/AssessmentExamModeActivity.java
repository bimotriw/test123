package com.oustme.oustsdk.assessment_ui.examMode;

import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;

import android.util.Size;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;


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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.ResultActivity;
import com.oustme.oustsdk.assessment_ui.assessmentCompletion.AssessmentCompleteScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.compression.video.MediaController;
import com.oustme.oustsdk.customviews.AutoFitTextureView;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.QuestionListAdapter;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.fragment.DateAndTimeFragment;
import com.oustme.oustsdk.question_module.fragment.DropDownQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.FIBQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.HotSpotQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.LongQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MCQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MRQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MTFQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MUQuestionFragment;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

public class AssessmentExamModeActivity extends BaseActivity implements LearningModuleInterface, CustomVideoControlListener, AssessmentContentHandlingInterface, ExamModeCallBack {

    RelativeLayout question_base_root;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    ImageView question_list;
    View timer_layout;
    ProgressBar mandatory_timer_progress;
    TextView mandatory_timer_text;
    FrameLayout question_fragment_container;
    RelativeLayout question_list_layout;
    RecyclerView question_list_rv;
    FrameLayout assessment_submit_button;

    private int color;
    private int bgColor;
    ActiveUser activeUser;

    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;
    DTOQuestions questions;

    //Question Audio
    MediaPlayer mediaPlayerForAudio;
    boolean audioEnable;
    boolean isMute = false;
    MenuItem itemAudio;
    boolean isQuestionAudio = false;

    long resumeTime = 0;
    CounterClass timer;
    int timerProgress;
    long answeredSeconds;
    long localQuestionTime;
    long overAllQuestionTime;
    boolean timeExceeded = false;
    boolean isMediaUploadQuestion = false;
    boolean isComingFromCpl = false;

    View bottom_bar;
    LinearLayout view_bottom_nav;
    RelativeLayout view_previous;
    ImageView previous_view;
    RelativeLayout view_comment;
    TextView view_count;
    RelativeLayout view_next;
    ImageView next_view, checklist_remark, hint, checklist_upload;
    boolean isSurveyFromCourse;
    boolean isMultipleCplModule = false;
    private long currentCplId;

    String tempRemarks, remarks, questionMedia;
    boolean isImageSelected, isVideoSelected;
    String userChoosenTask;
    TextView mTextViewUploadProgressText;
    ImageView mImageViewPreviewClose, mImageViewPreviewClose2;
    LinearLayout mLinearLayoutVideoAttachment, mLinearLayoutImageAttachment, mLinearLayoutDocumentAttachment, mLinearLayoutAudioAttachment;
    CustomTextView mediaUploadMsg;
    Drawable mBackgroundDrawable;
    ConstraintLayout mConstraintLayoutPreview, mConstraintLayoutPreview2;
    private View linearLayoutProgressBar;
    private LinearLayout mConstraintLayoutRoot;
    ProgressBar mProgressBar;
    ScrollView scroll_layout;
    private ProgressBar mProgressBarPostCreate;
    private String extension;
    File file;
    private AlertDialog mAlertDialogSaveDiscard, mAlertDialogForCompress;
    String filename;
    private long fileSize;
    Dialog popupWindowHint;
    Dialog popupWindowRemarks;
    PopupWindow popupWindowUpload;
    int width = LinearLayout.LayoutParams.MATCH_PARENT;
    int height = LinearLayout.LayoutParams.MATCH_PARENT;
    View popupViewUpload;

    int UPLOAD_IMAGE = 100;
    int REQUEST_STORAGE = 101;
    int REQUEST_CAMERA = 102;
    int UPLOAD_VIDEO = 104;

    File imageFile;
    File videoFile;
    String videoUrl;
    String imageUrl;
    String uploadFileName;

    Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;

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

    Dialog uploadProgressDialog;
    ProgressBar upload_percentage_progress;
    CircularProgressIndicator circularProgressIndicator;
    TextView upload_percentage_done;
    TextView upload_message;

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
    TextView upload_text;
    LinearLayout capture_layout;
    ImageView capture_image;
    TextView capture_text;
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
    boolean checkEnabled = false;
    FrameLayout upload_media_layout;
    LinearLayout upload_media_linear, upload_layout;
    FrameLayout question_action_button;
    FrameLayout question_action_cancel;
    ImageView upload_image, close, close1, close2, upload_video;
    String typeName;
    Bitmap inputBitmap;
    TextView title_layout, title_layout1;
    PopupWindow zoomImagePopup;
    long resumeTimeForUpload = 0;
    long tempAnsweredSeconds = 0;
    String hms, tempHms;//checkMediaVal = "";
    boolean isTimerStart;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End
    MCQuestionFragment mcQuestionFragment;
    MRQuestionFragment mrQuestionFragment;
    LongQuestionFragment longQuestionFragment;
    ArrayList<DTOQuestions> questionsArrayList;
    QuestionListAdapter adapter;
    Dialog assessmentReviewDialog;
    boolean killTheApp = false;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_assessment_exam_mode;
    }

    @Override
    protected void initView() {
        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(AssessmentExamModeActivity.this);
            }
            OustSdkTools.setLocale(AssessmentExamModeActivity.this);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getColors();
            question_base_root = findViewById(R.id.question_base_root);

            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            question_list = findViewById(R.id.question_list);
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            setSupportActionBar(toolbar);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            OustResourceUtils.setDefaultDrawableColor(question_list.getDrawable(), color);
            toolbar.setTitle("");

            timer_layout = findViewById(R.id.timer_layout);
            mandatory_timer_progress = findViewById(R.id.mandatory_timer_progress);
            mandatory_timer_text = findViewById(R.id.mandatory_timer_text);
            question_fragment_container = findViewById(R.id.question_fragment_container);
            question_list_layout = findViewById(R.id.question_list_layout);
            question_list_rv = findViewById(R.id.question_list_rv);
            assessment_submit_button = findViewById(R.id.assessment_submit_button);
            audioEnable = false;

            bottom_bar = findViewById(R.id.bottom_bar);
            view_bottom_nav = findViewById(R.id.view_bottom_nav);
            view_previous = findViewById(R.id.view_previous);
            previous_view = findViewById(R.id.previous_view);
            view_comment = findViewById(R.id.view_comment);
            view_count = findViewById(R.id.view_count);
            view_next = findViewById(R.id.view_next);
            next_view = findViewById(R.id.next_view);
            view_comment.setVisibility(View.GONE);
            hint = findViewById(R.id.hint);
            checklist_remark = findViewById(R.id.remark);
            checklist_upload = findViewById(R.id.upload);

            question_list.setVisibility(VISIBLE);

            setIconColor();

            popupViewUpload = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pop_up_layout_upload, null);
            popupWindowUpload = new PopupWindow(popupViewUpload, width, height, true);

            mediaUploadMsg = findViewById(R.id.mediaUploadMsg);

            mImageViewPreviewClose = findViewById(R.id.imageViewPreviewClose);
            mImageViewPreviewClose2 = findViewById(R.id.imageViewPreviewClose2);
            mBackgroundDrawable = getResources().getDrawable(R.drawable.ic_add);
            mBackgroundDrawable.setColorFilter(getResources().getColor(R.color.white_presseda), PorterDuff.Mode.SRC_IN);
            mTextViewUploadProgressText = findViewById(R.id.uploadprogresstext);
            mConstraintLayoutPreview = findViewById(R.id.previewCL);
            mConstraintLayoutPreview2 = findViewById(R.id.previewCL2);
            linearLayoutProgressBar = findViewById(R.id.linearLayoutProgressBar);
            mConstraintLayoutRoot = findViewById(R.id.constraintLayoutRoot);

            mLinearLayoutVideoAttachment = findViewById(R.id.linearLayoutVideoAttachment);
            mLinearLayoutAudioAttachment = findViewById(R.id.linearLayoutAudioAttachment);
            mLinearLayoutDocumentAttachment = findViewById(R.id.linearLayoutDocumentAttachment);
            mLinearLayoutImageAttachment = findViewById(R.id.linearLayoutImageAttachment);


            scroll_layout = findViewById(R.id.scroll_layout);
            mProgressBarPostCreate = findViewById(R.id.progressBarNBcreate);
            upload_media_layout = findViewById(R.id.upload_media_layout);
            upload_media_linear = findViewById(R.id.upload_media_linear);
            upload_layout = findViewById(R.id.upload_layout);
            title_layout = findViewById(R.id.title_layout);
            title_layout1 = findViewById(R.id.title_layout1);
            upload_media_layout.setVisibility(View.GONE);

            upload_image = findViewById(R.id.upload_image);
            upload_video = findViewById(R.id.upload_video);
            question_action_button = findViewById(R.id.question_action_button);
            question_action_cancel = findViewById(R.id.question_action_cancel);
            close = findViewById(R.id.close);
            close1 = findViewById(R.id.close1);
            close2 = findViewById(R.id.close2);

            question_base_frame = findViewById(R.id.question_base_frame);
            question_bgImg = findViewById(R.id.question_bgImg);
            main_layout = findViewById(R.id.main_layout);
            question_count_num = findViewById(R.id.question_count_num);
            question = findViewById(R.id.question);
            question_image_lay = findViewById(R.id.question_image_lay);
            question_image = findViewById(R.id.question_image);
            info_type = findViewById(R.id.info_type);
            expand_icon = findViewById(R.id.expand_icon);
            answer_image_lay = findViewById(R.id.answer_image_lay);
            answer_image = findViewById(R.id.answer_image);
            preview_expand_icon = findViewById(R.id.preview_expand_icon);
            delete_icon = findViewById(R.id.delete_icon);
            image_preview_card = findViewById(R.id.image_preview_card);
            preview_video_lay = findViewById(R.id.preview_video_lay);
            video_container = findViewById(R.id.video_container);
            video_thumbnail = findViewById(R.id.video_thumbnail);
            media_choose_layout = findViewById(R.id.media_choose_layout);
            tap_message = findViewById(R.id.tap_message);
            gallery_layout = findViewById(R.id.gallery_layout);
            upload_text = findViewById(R.id.upload_text);
            capture_layout = findViewById(R.id.capture_layout);
            capture_image = findViewById(R.id.capture_image);
            capture_text = findViewById(R.id.capture_text);
            texture_layout = findViewById(R.id.texture_layout);
            texture_view_camera = findViewById(R.id.texture_view_camera);
            video_timer_layout = findViewById(R.id.video_timer_layout);
            video_timer_text = findViewById(R.id.video_timer_text);
            image_capture_layout = findViewById(R.id.image_capture_layout);
            audio_play_record_lay = findViewById(R.id.audio_play_record_lay);
            audio_play_progress = findViewById(R.id.audio_play_progress);
            play_progress = findViewById(R.id.play_progress);
            play_duration = findViewById(R.id.play_duration);
            audio_action_description = findViewById(R.id.audio_action_description);
            audio_action = findViewById(R.id.audio_action);
            audio_action_image = findViewById(R.id.audio_action_image);
            audio_action_text = findViewById(R.id.audio_action_text);
            audio_delete = findViewById(R.id.audio_delete);
            texture_close = findViewById(R.id.texture_close);
            capture_icon = findViewById(R.id.capture_icon);
            camera_switch = findViewById(R.id.camera_switch);
            question_katex = findViewById(R.id.question_katex);

            camera_switch.setVisibility(View.INVISIBLE);
            texture_layout.setVisibility(View.GONE);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();

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

            question_list.setOnClickListener(v -> {
                try {
                    if (question_list_layout.getVisibility() != VISIBLE) {
                        if (questionsArrayList != null && questionsArrayList.size() != 0) {
                            if (mcQuestionFragment != null) {
                                mcQuestionFragment.pauseVideoPlayer();
                            } else if (mrQuestionFragment != null) {
                                mrQuestionFragment.pauseVideoPlayer();
                            } else if (longQuestionFragment != null) {
                                longQuestionFragment.pauseVideoPlayer();
                            }
                            question_list_layout.setVisibility(VISIBLE);
                            question_list_layout.bringToFront();

                            adapter = new QuestionListAdapter();
                            adapter.setAdapter(AssessmentExamModeActivity.this, questionsArrayList);
                            adapter.setExamModeCallBack(AssessmentExamModeActivity.this);
                            if (questionBaseModel != null) {
                                adapter.setReviewMode(questionBaseModel.isReviewMode());
                            }
                            question_list_rv.setAdapter(adapter);
                            handleAudioForList();
                        }
                    } else {
                        question_list_layout.setVisibility(View.GONE);
                        question_list_rv.removeAllViews();
                        adapter = null;
                        handleAudio();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });

            assessment_submit_button.setOnClickListener(v -> reviewSubmitPopup());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {
        try {
            OustPreferences.saveAppInstallVariable("IsAssessmentPlaying", true);
            OustSdkTools.speakInit();
//            OustGATools.getInstance().reportPageViewToGoogle(AssessmentExamModeActivity.this, "Assessment Question Base Screen");
            fetchLayoutData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        try {

            toolbar.setOnTouchListener((v, event) -> {
                back_button.setVisibility(View.VISIBLE);
                screen_name.setVisibility(View.VISIBLE);
                timerForHide();
                return false;
            });

            back_button.setOnClickListener(v -> onBackPressed());

            hint.setOnClickListener(v -> showPopupWindowForHint(questions.getHint(), ""));

            checklist_remark.setOnClickListener(this::showPopupWindowForRemarks);

            checklist_upload.setOnClickListener(v -> {
                if (imageUrl != null) {
                    media_choose_layout.setVisibility(View.GONE);
                    upload_media_layout.setVisibility(View.VISIBLE);
                    upload_media_linear.setVisibility(View.VISIBLE);
                    image_preview_card.setVisibility(View.VISIBLE);
                    texture_layout.setVisibility(View.GONE);
                    image_capture_layout.setVisibility(View.GONE);
                    title_layout.setVisibility(View.GONE);
                    title_layout1.setVisibility(View.GONE);
                    answer_image_lay.setVisibility(VISIBLE);
                    answer_image.setVisibility(View.VISIBLE);
                    close1.setVisibility(VISIBLE);
                    upload_layout.setVisibility(View.GONE);
                    preview_video_lay.setVisibility(View.GONE);
                    try {

                        Glide.with(AssessmentExamModeActivity.this)
                                .asBitmap()
                                .load(imageUrl).into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        try {
                                            answer_image.setImageBitmap(resource);
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
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                } else if (imageFile != null) {
                    media_choose_layout.setVisibility(View.GONE);
                    upload_media_layout.setVisibility(View.VISIBLE);
                    upload_media_linear.setVisibility(View.VISIBLE);
                    image_preview_card.setVisibility(View.VISIBLE);
                    texture_layout.setVisibility(View.GONE);
                    image_capture_layout.setVisibility(View.GONE);
                    title_layout.setVisibility(View.GONE);
                    title_layout1.setVisibility(View.GONE);
                    answer_image_lay.setVisibility(VISIBLE);
                    answer_image.setVisibility(View.VISIBLE);
                    close1.setVisibility(VISIBLE);
                    upload_layout.setVisibility(View.GONE);
                    preview_video_lay.setVisibility(View.GONE);
                    try {
                        Picasso.get().load(imageFile).into(answer_image);
                        answer_image.setImageBitmap(inputBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                } else if (videoUrl != null) {
                    video_container.setVisibility(View.VISIBLE);
                    media_choose_layout.setVisibility(View.GONE);
                    upload_media_layout.setVisibility(View.VISIBLE);
                    upload_media_linear.setVisibility(View.VISIBLE);
                    texture_layout.setVisibility(View.GONE);
                    image_capture_layout.setVisibility(View.GONE);
                    title_layout.setVisibility(View.GONE);
                    title_layout1.setVisibility(View.INVISIBLE);
                    answer_image_lay.setVisibility(View.VISIBLE);

                    preview_video_lay.setVisibility(View.VISIBLE);
                    image_preview_card.setVisibility(View.GONE);
                    close2.setVisibility(VISIBLE);
                    preview_expand_icon.setVisibility(View.GONE);
                    upload_layout.setVisibility(View.GONE);

                    video_container.setVideoPath(videoUrl);
                    video_container.setMediaController(new android.widget.MediaController(AssessmentExamModeActivity.this));
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

                    resetAudioPlayer();

                }/* else if (checkMediaVal != null && checkMediaVal.length() > 0) {
                    if (checkMediaVal.contains("Image") || checkMediaVal.contains("Video")) {
                        questionMedia = checkMediaVal;
                    }
                    Toast.makeText(AssessmentExamModeActivity.this, "You have already uploaded media", Toast.LENGTH_SHORT).show();
                } */ else {
                    isMediaUploadQuestion = true;
                    upload_media_layout.setVisibility(View.VISIBLE);
                    media_choose_layout.setVisibility(View.VISIBLE);
                    upload_media_linear.setVisibility(View.VISIBLE);
                    answer_image_lay.setVisibility(View.GONE);
                    upload_layout.setVisibility(View.GONE);
                    close.setVisibility(VISIBLE);
                }
            });

            close.setOnClickListener(v -> {
                upload_media_layout.setVisibility(View.GONE);
                upload_media_linear.setVisibility(View.GONE);
                answer_image_lay.setVisibility(View.GONE);
                media_choose_layout.setVisibility(View.GONE);
            });

            close1.setOnClickListener(v -> {
                upload_media_layout.setVisibility(View.GONE);
                upload_media_linear.setVisibility(View.GONE);
                answer_image_lay.setVisibility(View.GONE);
                media_choose_layout.setVisibility(View.GONE);
            });

            close2.setOnClickListener(v -> {
                video_container.setVisibility(View.GONE);
                upload_media_layout.setVisibility(View.GONE);
                upload_media_linear.setVisibility(View.GONE);
                answer_image_lay.setVisibility(View.GONE);
                media_choose_layout.setVisibility(View.GONE);
                if (video_container != null && video_container.isPlaying()) {
                    video_container.stopPlayback();
                    video_container.clearAnimation();
                    video_container.suspend();
                    video_container.setVideoURI(null);
                    video_container.setMediaController(null);

                }
            });

            question_action_button.setOnClickListener(v -> {
                question_action_button.setEnabled(false);
                if (typeName.equalsIgnoreCase("Image")) {
                    if (imageFile != null) {
                        resumeTimeForUpload = timerProgress;
                        tempAnsweredSeconds = answeredSeconds;
                        tempHms = hms;
                        timerPauseRestart();
                        hms = "00:00";
                        timerProgress = 0;
                        answeredSeconds = 0;
                        mandatory_timer_progress.setProgress((int) resumeTimeForUpload);
                        mandatory_timer_text.setText(tempHms);
                        uploadImageToAWS();
                    }
                } else if (typeName.equalsIgnoreCase("Video")) {
                    if (videoFile != null) {
                        resumeTimeForUpload = timerProgress;
                        tempAnsweredSeconds = answeredSeconds;
                        tempHms = hms;
                        timerPauseRestart();
                        hms = "";
                        timerProgress = 0;
                        answeredSeconds = 0;
                        mandatory_timer_progress.setProgress((int) resumeTimeForUpload);
                        mandatory_timer_text.setText(tempHms);
                        Log.e(TAG, "uploadConfirmation:-videoFile-> " + videoFile);
                        compressVideoAndUpload();
                        if (video_container != null && video_container.isPlaying()) {
                            video_container.pause();
                        }

                    }
                }
                new Handler().postDelayed(() -> question_action_button.setEnabled(true), 2000);

            });

            upload_video.setOnClickListener(v -> {
                typeName = "Video";
                selectVideo();
            });

            upload_image.setOnClickListener(v -> {
                typeName = "Image";
                selectImage();
            });

            question_action_cancel.setOnClickListener(v -> {
                upload_media_layout.setVisibility(View.GONE);
                upload_layout.setVisibility(View.GONE);
                imageFile = null;
                videoFile = null;
                inputBitmap = null;
                if (video_container != null && video_container.isPlaying()) {
                    video_container.pause();
                    video_container.clearAnimation();
                    video_container.suspend();
                    video_container.setVideoURI(null);
                    video_container.setMediaController(null); // check once
                }
            });

            answer_image.setOnClickListener(v -> {
                try {
                    gifZoomPopup(answer_image.getDrawable());
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

    public void gifZoomPopup(final Drawable gif) {
        try {
            @SuppressLint("InflateParams")
            View popUpView = getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //handle network connection

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

    private void setIconColor() {
        try {
            mandatory_timer_text.setTextColor(getResources().getColor(R.color.primary_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mandatory_timer_progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_correct)));
            }

            Drawable actionDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            assessment_submit_button.setBackground(OustResourceUtils.setDefaultDrawableColor(actionDrawable));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchLayoutData() {
        questionBaseModel = null;
        if (questionBaseViewModel != null) {
            getViewModelStore().clear();
            questionBaseViewModel = null;
            questionBaseModel = null;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isSurveyFromCourse = bundle.getBoolean("isSurveyFromCourse", false);
            isMultipleCplModule = bundle.getBoolean("isMultipleCplModule", false);
            currentCplId = bundle.getLong("currentCplId");
            isComingFromCpl = bundle.getBoolean("isComingFromCpl");

            Log.e(TAG, "fetchLayoutData: Base -Id-> " + currentCplId + "-->" + isMultipleCplModule);
        }
        questionBaseViewModel = new ViewModelProvider(this).get(QuestionBaseViewModel.class);
        questionBaseViewModel.init(bundle);
        questionBaseViewModel.getQuestionModuleMutableLiveData().observe(this, questionBaseModel -> {
            if (questionBaseModel == null)
                return;

            this.questionBaseModel = questionBaseModel;
            setData(questionBaseModel);

        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData(QuestionBaseModel questionBaseModel) {
        try {
            audioEnable = false;
            isMute = false;
            invalidateOptionsMenu();
            stopMusicPlay();
            if (questionBaseModel != null) {
                timerForHide();
                resumeTime = questionBaseModel.getQuestionResumeTime();
                view_next.setVisibility(View.VISIBLE);
                next_view.setClickable(true);
                next_view.setImageDrawable(OustSdkTools.drawableColor(next_view.getDrawable(), getResources().getColor(R.color.primary_text)));

                if (questionBaseModel.isReviewMode()) {
                    assessment_submit_button.setVisibility(View.GONE);
                }

                if (questionBaseModel.isSecureSessionOn()) {
                    disableScreenCapture();
                }
                try {
                    if (questionBaseModel.getQuestions() != null && questionBaseModel.getQuestions().getTopic() != null) {
                        screen_name.setText(questionBaseModel.getQuestions().getTopic());
                    } else if (questionBaseModel.getModuleName() != null) {
                        screen_name.setText(questionBaseModel.getModuleName());
                    }
                } catch (Exception e) {
                    if (questionBaseModel.getModuleName() != null) {
                        screen_name.setText(questionBaseModel.getModuleName());
                    }
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                if (!isTimerStart) {
                    isTimerStart = true;
                    if (!questionBaseModel.isReviewMode()) {
                        new Handler().postDelayed(() -> {
                            if (resumeTime > 0) {
                                startQuestionTimer(resumeTime);
                                resumeTime = 0;
                            } else {
                                startQuestionTimer(questionBaseModel.getDuration());
                            }
                        }, 500);
                    }
                }

                if (questionBaseModel.isTimerEnd() && questionBaseModel.getType() != 2) {
                    question_base_root.setVisibility(View.GONE);
                    branding_mani_layout.setVisibility(View.VISIBLE);

                    questionBaseViewModel.calculateFinalScore(false, "");
                } else if (questionBaseModel.getType() == 1) {
                    OustPreferences.save("MRQuestionAnswers", "");
                    startTransaction();
                } else if (questionBaseModel.isShowLoader() && questionBaseModel.getType() != 2) {
                    question_base_root.setVisibility(View.GONE);
                    if (questionBaseModel.getType() == 3) {
                        reviewSubmitPopup();
                    } else {
                        branding_mani_layout.setVisibility(View.VISIBLE);
                    }

                } else if (questionBaseModel.getType() == 2) {
                    question_base_root.setVisibility(View.GONE);
                    branding_mani_layout.setVisibility(View.GONE);
                    try {
                        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                        HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                        eventUpdate.put("ClickedOnOver", true);
                        eventUpdate.put("AssessmentID", OustPreferences.get("current_assessmentId"));
                        eventUpdate.put("Assessment Name", OustPreferences.get("current_assessmentName"));
                        Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                        if (clevertapDefaultInstance != null) {
                            clevertapDefaultInstance.pushEvent("Assessment_Completed", eventUpdate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    Intent intent;
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        intent = new Intent(AssessmentExamModeActivity.this, AssessmentCompleteScreen.class);
                        intent.putExtra("containCertificate", questionBaseModel.isContainCertificate());
                        intent.putExtra("totalTimeTaken", questionBaseModel.getTotalTimeTaken());
                        intent.putExtra("isCplModule", questionBaseModel.isCplModule());
                        intent.putExtra("IS_FROM_COURSE", questionBaseModel.isFromCourse());
                        intent.putExtra("mappedCourseId", questionBaseModel.getMappedCourseId());
                        intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                        intent.putExtra("currentCplId", currentCplId);
                        intent.putExtra("examMode", true);
                        intent.putExtra("overAllQuestionTime", overAllQuestionTime);
                        intent.putExtra("courseAssociated", questionBaseModel.isCourseAssociated());
                        intent.putExtra("showAssessmentResultScore", questionBaseModel.isShowAssessmentResultScore());
                        intent.putExtra("reAttemptAllowed", questionBaseModel.isReAttemptAllowed());
                        intent.putExtra("nAttemptCount", questionBaseModel.getnAttemptCount() + 1);
                        intent.putExtra("nAttemptAllowedToPass", questionBaseModel.getnAttemptAllowedToPass());
                        intent.putExtra("assessmentId", Long.parseLong(OustPreferences.get("current_assessmentId")));
                        intent.putExtra("courseId", String.valueOf(questionBaseModel.getCourseId()));
                        intent.putExtra("isComingFromCpl", isComingFromCpl);
                    } else {
                        //need to check when it will be use
                        intent = new Intent(AssessmentExamModeActivity.this, ResultActivity.class);
                    }
                    intent.putExtra("surveyAssociated", questionBaseModel.isSurveyAssociated());
                    intent.putExtra("surveyMandatory", questionBaseModel.isSurveyMandatory());
                    intent.putExtra("isSurveyFromCourse", isSurveyFromCourse);
                    intent.putExtra("mappedSurveyId", questionBaseModel.getMappedSurveyId());

                    Gson gson = new GsonBuilder().create();
                    intent.putExtra("ActiveUser", gson.toJson(questionBaseModel.getActiveUser()));
                    intent.putExtra("ActiveGame", gson.toJson(questionBaseModel.getActiveGame()));
                    intent.putExtra("GamePoints", gson.toJson(questionBaseModel.getGamePoints()));
                    intent.putExtra("SubmitRequest", gson.toJson(questionBaseModel.getSubmitRequest()));
                    intent.putExtra("ShouldMusicPlay", true);
                    startActivity(intent);
                    AssessmentExamModeActivity.this.finish();
                }

                activeUser = questionBaseModel.getActiveUser();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void disableScreenCapture() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startTransaction() {
        isMediaUploadQuestion = false;
        remarks = "";
        questionMedia = "";
        tempRemarks = "";
        imageUrl = null;
        imageFile = null;
        videoFile = null;
        videoUrl = null;
        //checkMediaVal = "";
        back_button.setVisibility(View.VISIBLE);
        screen_name.setVisibility(View.VISIBLE);
        timerForHide();
        localQuestionTime = 0;
        try {
            upload_media_layout.setVisibility(View.GONE);
            upload_layout.setVisibility(View.GONE);
            if (video_container != null && video_container.isPlaying()) {
                video_container.pause();
                video_container.clearAnimation();
                video_container.suspend();
                video_container.setVideoURI(null);
            }
            if (imageFile != null && imageFile.exists()) {
                boolean b = imageFile.delete();
                Log.e("ExamMode", "Image deleted " + b);
                imageFile = null;
            }
            if (videoFile != null && videoFile.exists()) {
                boolean b = videoFile.delete();
                Log.e("ExamMode", "Video deleted " + b);
                videoFile = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        inputBitmap = null;
        uploadFileName = null;

        questions = questionBaseModel.getQuestions();
        question_list_layout.setVisibility(View.GONE);
        question_list_rv.removeAllViews();
        adapter = null;
        if (questions != null) {

            question_base_root.setVisibility(View.VISIBLE);
            branding_mani_layout.setVisibility(View.GONE);
            showBottomBar();
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.learningview_slideanimb,
                    R.anim.learningview_slideanim);

            String questionCategory = questions.getQuestionCategory();
            changeOrientationPortrait();
            if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                stopMusicPlay();
                // hideTimer();
            } else if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                stopMusicPlay();
                // hideTimer();
            } else {
                checkForAudio(questions);
            }
            if (questions.isLearningPlayNew()) {
                //need to check stopMusicPlay() is mandatory to handle here or not.
                Log.e(TAG, "LearningPlayNew");
                if ((questions.getQuestionType() != null && (questions.getQuestionType().equals(QuestionType.FILL)))) {
                    openFIBQuestionFragment(transaction, questions);
                } else if (questions.getQuestionCategory() != null) {
                    if (questions.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                        openMTQuestionFragment(transaction, questions);
                    } else if (questions.getQuestionCategory().equals(QuestionType.HOTSPOT)) {
                        openHOTSPOTQuestionFragment(transaction, questions);
                    }
                }

            } else if (questions.isMediaUploadQues()) {
                //need to check stopMusicPlay() is mandatory to handle here or not.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isMediaUploadQuestion = true;
                    openMUQuestionFragment(transaction, questions);
                }
            } else if (questions.getQuestionType() != null) {
                if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    openMCQFragment(transaction, questions);
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    openMRQFragment(transaction, questions);
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.DATE)) {
                    openDateTimeFragment(transaction, questions);
                } else if (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                    openLongQuestionFragment(transaction, questions);
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DROPDOWN) || (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.DROPDOWN))) {
                    openDropDownQuestionFragment(transaction, questions);
                }
            }


        }
    }

    private void openDropDownQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            DropDownQuestionFragment fragment = new DropDownQuestionFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openHOTSPOTQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            HotSpotQuestionFragment fragment = new HotSpotQuestionFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setTotalCards(questionBaseModel.getTotalCards());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForAudio(DTOQuestions questions) {
        Log.d(TAG, "checkForAudio: ");
        stopMusicPlay();
        if (questions != null) {
            if (!(questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                if (questions.getAudio() != null && !questions.getAudio().isEmpty()) {
                    initializeQuestionSound(questions.getAudio());
                    audioEnable = true;
                    invalidateOptionsMenu();
                } else {
                    audioEnable = false;
                    if (isQuestionAudio) {
                        isQuestionAudio = false;
                        initializeSound();
                    }
                }
            } else {
                stopMusicPlay();
            }
        }
    }

    public void initializeSound() {
        try {
            if ((!OustPreferences.getAppInstallVariable("isttsfileinstalled") || (!OustAppState.getInstance().isAssessmentGame())) ||
                    ((OustAppState.getInstance().isAssessmentGame()) && (!OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled()))) {
                if (OustAppState.getInstance().getAssessmentFirebaseClass().isBackgroundAudioForAssessment()) {
                    if (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList() != null) {
                        if (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList().size() > 0) {
                            playAssessmentBackScore(OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList().get(0));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAssessmentBackScore(CourseCardMedia courseCardMedia) {
        Log.d(TAG, "playAssessmentBackScore: ");
        if (courseCardMedia != null && courseCardMedia.getData() != null) {
            String audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "assessment/media/audio/" + courseCardMedia.getData();
            prepareExoPlayerFromUri(Uri.parse(audio));
        }
    }

    public void stopMusicPlay() {
        try {
            Log.d(TAG, "stopMusicPlay: ");
            resetAudioPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initializeQuestionSound(String audio) {
        try {
            if ((!OustPreferences.getAppInstallVariable("isttsfileinstalled") || (!OustAppState.getInstance().isAssessmentGame())) ||
                    ((OustAppState.getInstance().isAssessmentGame()) && (!OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled()))) {
                getSignedUrl(audio);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getSignedUrl(String audio) {
        try {
            audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + audio;
            prepareExoPlayerFromUri(Uri.parse(audio));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void prepareExoPlayerFromUri(Uri uri) {
        try {
            Log.d(TAG, "prepareExoPlayerFromUri: ");
            resetAudioPlayer();
            if (uri != null) {
                mediaPlayerForAudio = new MediaPlayer();
                mediaPlayerForAudio.reset();
                mediaPlayerForAudio.setDataSource(AssessmentExamModeActivity.this, uri);
                mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerForAudio.prepare();
                mediaPlayerForAudio.start();
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

    @Override
    public void onUserInteraction() {


    }

    private void timerForHide() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                back_button.setVisibility(View.INVISIBLE);
                screen_name.setVisibility(View.INVISIBLE);
            }
        }.start();

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
        itemAudio.setIcon(OustResourceUtils.setDefaultDrawableColor(audioDrawable));
        itemAudio.setVisible(isAudioEnable());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_audio) {
            handleAudio();
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isAudioEnable() {
        return audioEnable;
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

    private void handleAudioForList() {
        try {
            if (mediaPlayerForAudio != null) {
                if (mediaPlayerForAudio.isPlaying()) {
                    mediaPlayerForAudio.pause();
                    isMute = true;
                }
            }
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openMUQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            MUQuestionFragment fragment = new MUQuestionFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            //fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setTotalCards(questionBaseModel.getTotalCards());
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openMTQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            MTFQuestionFragment fragment = new MTFQuestionFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setTotalCards(questionBaseModel.getTotalCards());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openMCQFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            mcQuestionFragment = new MCQuestionFragment();
            transaction.replace(R.id.question_fragment_container, mcQuestionFragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            mcQuestionFragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            mcQuestionFragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            mcQuestionFragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            mcQuestionFragment.setSurveyQuestion(questionBaseModel.isSurvey());
            mcQuestionFragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            mcQuestionFragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            mcQuestionFragment.setAssessmentScore(questionBaseModel.getScores());
            mcQuestionFragment.setQuestions(questions);
            mcQuestionFragment.setReviewMode(questionBaseModel.isReviewMode());
            mcQuestionFragment.setExamMode(questionBaseModel.isExamMode());
            mcQuestionFragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openMRQFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            mrQuestionFragment = new MRQuestionFragment();
            mrQuestionFragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            mrQuestionFragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, mrQuestionFragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            mrQuestionFragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            mrQuestionFragment.setSurveyQuestion(questionBaseModel.isSurvey());
            mrQuestionFragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            mrQuestionFragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            mrQuestionFragment.setAssessmentScore(questionBaseModel.getScores());
            mrQuestionFragment.setQuestions(questions);
            mrQuestionFragment.setReviewMode(questionBaseModel.isReviewMode());
            mrQuestionFragment.setExamMode(questionBaseModel.isExamMode());
            mrQuestionFragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void openLongQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            longQuestionFragment = new LongQuestionFragment();
            longQuestionFragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            longQuestionFragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, longQuestionFragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            longQuestionFragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            longQuestionFragment.setSurveyQuestion(questionBaseModel.isSurvey());
            longQuestionFragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            longQuestionFragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            longQuestionFragment.setAssessmentScore(questionBaseModel.getScores());
            longQuestionFragment.setTotalCards(questionBaseModel.getTotalCards());
            longQuestionFragment.setReviewMode(questionBaseModel.isReviewMode());
            longQuestionFragment.setExamMode(questionBaseModel.isExamMode());
            longQuestionFragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            longQuestionFragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void openDateTimeFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            DateAndTimeFragment fragment = new DateAndTimeFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setTotalCards(questionBaseModel.getTotalCards());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void openFIBQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            FIBQuestionFragment fragment = new FIBQuestionFragment();
            fragment.setLearningModuleInterface(AssessmentExamModeActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setTotalCards(questionBaseModel.getTotalCards());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setExamMode(questionBaseModel.isExamMode());
            fragment.setAssessmentContentHandler(AssessmentExamModeActivity.this);
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void timerPauseRestart() {
        try {
            try {
                if (null != timer) {
                    timer.cancel();
                    timer = null;
                }
                String timerZeroText = "00:00";
                mandatory_timer_text.setText(timerZeroText);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {
        try {
            new Handler().postDelayed(() ->
                            questionBaseViewModel.gotoNextScreenForAssessment()
                    , ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoPreviousScreen() {
        try {
            new Handler().postDelayed(() -> questionBaseViewModel.gotoPreviousScreen(), ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void changeOrientationPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void endActivity() {

    }

    @Override
    public void restartActivity() {

    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {

    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        if (questionBaseViewModel != null) {
            if (questionBaseModel.getQuestions().isSurveyQuestion()) {
                questionBaseViewModel.setAnswerAndOc(userAns, subjectiveResponse, 0, true, localQuestionTime, remarks, questionMedia, false);
            } else {
                if (userAns != null && !userAns.isEmpty()) {
                    questionBaseViewModel.setAnswerAndOc(userAns, subjectiveResponse, oc, status, localQuestionTime, remarks, questionMedia, false);
                } else {
                    questionBaseViewModel.setAnswerAndOc(userAns, subjectiveResponse, oc, status, localQuestionTime, remarks, questionMedia, true);
                }
            }
        }
    }

    @Override
    public void showCourseInfo() {

    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {

    }

    @Override
    public void sendCourseDataToServer() {

    }

    @Override
    public void dismissCardInfo() {

    }

    @Override
    public void setFavCardDetails(List<FavCardDetails> favCardDetails) {

    }

    @Override
    public void setFavoriteStatus(boolean status) {

    }

    @Override
    public void setRMFavouriteStatus(boolean status) {

    }

    @Override
    public void setShareClicked(boolean isShareClicked) {

    }

    @Override
    public void openReadMoreFragment(DTOReadMore readMoreData, boolean isRMFavourite1, String courseId1, String cardBackgroundImage1, DTOCourseCard courseCardClass) {

    }

    @Override
    public void readMoreDismiss() {

    }

    @Override
    public void disableBackButton(boolean disableBackButton) {

    }

    @Override
    public void closeCourseInfoPopup() {

    }

    @Override
    public void stopTimer() {

        try {
            pauseTimer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void isLearnCardComplete(boolean isLearnCardComplete) {

    }

    @Override
    public void closeChildFragment() {

    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {

    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {
    }

    @Override
    public void isSurveyCompleted(boolean surveyCompleted) {
    }

    @Override
    public void onSurveyExit(String message) {
    }


    @Override
    public void handleQuestionAudio(boolean play) {
        handleAudio();
    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    public void startQuestionTimer(long quesTime) {
        try {
            mandatory_timer_progress.setProgress(0);
            mandatory_timer_progress.setMax(100);
            timeExceeded = false;
            localQuestionTime = 0;
            overAllQuestionTime = questionBaseModel.getDuration() - questionBaseModel.getQuestionResumeTime();
            timer = new CounterClass((quesTime * 1000), 1000);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideToolbar() {
    }

    @Override
    public void onVideoEnd() {
    }

    @Override
    public void showToolbar() {

    }

    @Override
    public void fullHideToolbar() {

    }

    @Override
    public void fullShowToolbar() {

    }

    @Override
    public void cancelTimerForReview() {
        try {

            String timerZeroText = "00:00";
            mandatory_timer_text.setText(timerZeroText);

            timer_layout.setVisibility(View.GONE);

            next_view.setImageDrawable(OustSdkTools.drawableColor(next_view.getDrawable(), getResources().getColor(R.color.primary_text)));
            next_view.setEnabled(true);

            if (null != timer)
                timer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showBottomBar() {
        bottom_bar.setVisibility(View.VISIBLE);

        if (questionBaseModel != null && questionBaseModel.getQuestionIndex() == 0) {
            view_previous.setVisibility(View.INVISIBLE);
            previous_view.setEnabled(false);
            previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.unselected_text)));
        } else {
            view_previous.setVisibility(View.VISIBLE);
            previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.primary_text)));
            previous_view.setEnabled(true);
        }

        next_view.setOnClickListener(v -> handledBottomNextArrow());

        previous_view.setOnClickListener(v -> handledBottomPreviousArrow());

        checkHintRemarkUpload();
    }

    private void checkHintRemarkUpload() {
        if (questions != null) {
            if (questions.getHint() != null && questions.getHint().trim().length() > 0) {
                hint.setVisibility(View.VISIBLE);
            } else {
                hint.setVisibility(View.GONE);
            }
            if (questions.isRemarks()) {
                checklist_remark.setVisibility(View.VISIBLE);
            } else {
                checklist_remark.setVisibility(View.GONE);
            }
            if (questions.isUploadMedia()) {
                checklist_upload.setVisibility(View.VISIBLE);
            } else {
                checklist_upload.setVisibility(View.GONE);
            }
        }

        if (questionBaseModel.getScores() != null) {
            if (questionBaseModel.getScores().getQuestionMedia() != null && !questionBaseModel.getScores().getQuestionMedia().isEmpty()) {
                checklist_upload.setVisibility(View.VISIBLE);
                questionMedia = questionBaseModel.getScores().getQuestionMedia();
                if (questionBaseModel.getScores().getQuestionMedia().contains("Video")) {
                    videoUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + questionBaseModel.getScores().getQuestionMedia();
                } else if (questionBaseModel.getScores().getQuestionMedia().contains("Image")) {
                    imageUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + questionBaseModel.getScores().getQuestionMedia();
                }
            } else if (questionBaseModel.isReviewMode()) {
                checklist_upload.setVisibility(View.GONE);
            }

            if (questionBaseModel.getScores().getRemarks() != null && !questionBaseModel.getScores().getRemarks().isEmpty()) {
                checklist_remark.setVisibility(View.VISIBLE);
            } else if (questionBaseModel.isReviewMode()) {
                checklist_remark.setVisibility(View.GONE);
            }
        } else if (questionBaseModel.isReviewMode()) {
            checklist_remark.setVisibility(View.GONE);
            checklist_upload.setVisibility(View.GONE);
        }
    }

    @Override
    public void onQuestionClick(int position) {
        try {
            Log.d(TAG, "onQuestionClick: for Assessment ");
            new Handler().postDelayed(() ->
                            questionBaseViewModel.handleExamModeForAssessment(position)
                    , 500);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                upload_media_layout.setVisibility(View.GONE);
                upload_layout.setVisibility(View.GONE);
                if (video_container != null && video_container.isPlaying()) {
                    video_container.pause();
                    video_container.clearAnimation();
                    video_container.suspend();
                    video_container.setVideoURI(null);
                }
                upload_media_layout.setVisibility(View.GONE);
                texture_layout.setVisibility(View.GONE);
                if (mIsRecordingVideo) {
                    stopRecordingVideo(false);
                }
                if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                    uploadProgressDialog.dismiss();
                }
                mandatory_timer_progress.setProgress(0);
                localQuestionTime++;
                overAllQuestionTime++;
                localQuestionTime = 0;

                bottom_bar.performClick();

                if (popupWindowHint != null && popupWindowHint.isShowing()) {
                    popupWindowHint.dismiss();
                }

                if (popupWindowRemarks != null && popupWindowRemarks.isShowing()) {
                    popupWindowRemarks.dismiss();
                }

                if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                    zoomImagePopup.dismiss();
                }

                //TODO handle score submit when overall timer ends
                questionBaseViewModel.calculateFinalScore(false, "");

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }

        @Override
        public void onTick(long millisUntilFinished) {
            localQuestionTime++;
            overAllQuestionTime++;
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
            hms = String.format(Locale.ENGLISH, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            try {
                timerProgress = (int) (answeredSeconds * 100 / questionBaseModel.getDuration());
                mandatory_timer_progress.setProgress(timerProgress);
                mandatory_timer_text.setText(hms);
                if (!timeExceeded && answeredSeconds <= (0.05 * questionBaseModel.getDuration())) {
                    timeExceeded = true;
                    mandatory_timer_text.setTextColor(getResources().getColor(R.color.error_incorrect));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mandatory_timer_progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.error_incorrect)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void cancelTimer() {
        try {
            if (null != timer)
                timer.cancel();
            String timerZeroText = "00:00";
            mandatory_timer_text.setText(timerZeroText);
            localQuestionTime = 0;
            imageFile = null;
            videoFile = null;
            inputBitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void pauseTimer() {
        try {
            if (null != timer)
                timer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {

        if (upload_media_layout.getVisibility() == VISIBLE) {
            upload_media_layout.setVisibility(View.GONE);
            if (isMediaUploadQuestion) {
                isMediaUploadQuestion = false;
            }
        } else {
            if (isMediaUploadQuestion) {
                isMediaUploadQuestion = false;
            }
            if (questionBaseModel != null && questionBaseModel.isReviewMode()) {
                stopMusicPlay();
                AssessmentExamModeActivity.this.finish();
            } else {
                resumePopUp();
            }
        }
    }

    private void resumePopUp() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.common_pop_up, findViewById(android.R.id.content), false);
            final PopupWindow playCancelPopup = OustSdkTools.createPopUp(popUpView);
            final ImageView info_image = popUpView.findViewById(R.id.info_image);
            final TextView info_title = popUpView.findViewById(R.id.info_title);
            final TextView info_description = popUpView.findViewById(R.id.info_description);
            final LinearLayout info_no = popUpView.findViewById(R.id.info_no);
            final TextView info_no_text = popUpView.findViewById(R.id.info_no_text);
            final LinearLayout info_yes = popUpView.findViewById(R.id.info_yes);
            final TextView info_yes_text = popUpView.findViewById(R.id.info_yes_text);

            info_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));

            Drawable yesDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            info_yes.setBackground(OustResourceUtils.setDefaultDrawableColor(yesDrawable));

            Drawable noDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            info_no.setBackground(OustResourceUtils.setDefaultDrawableColor(noDrawable, getResources().getColor(R.color.overlay_container)));

            info_yes_text.setText(getResources().getString(R.string.yes));
            info_no_text.setText(getResources().getString(R.string.no));

            info_description.setText(getResources().getString(R.string.assessment_paused_msg));
            info_title.setText(getResources().getString(R.string.warning));
            info_description.setVisibility(View.VISIBLE);
            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            info_yes.setOnClickListener(view -> {
                playCancelPopup.dismiss();
                if (questionBaseViewModel != null) {
                    stopMusicPlay();
                    questionBaseViewModel.setQuestionLocalTime(localQuestionTime);
                    questionBaseViewModel.setMediaUpload(questionMedia);
                    killTheApp = true;
                    questionBaseViewModel.submitGameOnBackPress();
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            cancelTimer();
                            AssessmentExamModeActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, 500);
                }
            });

            info_no.setOnClickListener(view -> playCancelPopup.dismiss());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (questionBaseViewModel != null) {
            try {
                if (!isMediaUploadQuestion) {
                    Log.d("ExamMode", "Timer");
                    cancelTimer();
//                    stopBackgroundThread();
                    if (questionBaseViewModel != null && !killTheApp) {
                        questionBaseViewModel.setQuestionLocalTime(localQuestionTime);
                        questionBaseViewModel.setMediaUpload(questionMedia);
                        questionBaseViewModel.submitGameOnBackPress();
                    }
                    finish();
                }
                closeCamera();
                stopBackgroundThread();
                if (mediaPlayerForAudio != null) {
                    mediaPlayerForAudio.isPlaying();
                    mediaPlayerForAudio.pause();
                    isMute = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
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

    private void resetPlayers() {
        try {
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


    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (null != timer) {
                if (!isMediaUploadQuestion) {
                    stopMusicPlay();
                    timer.cancel();
                }
            }
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            resetPlayers();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getViewModelStore().clear();
            if (questionBaseViewModel != null) {
                questionBaseViewModel = null;
                questionBaseModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (null != timer)
                timer.cancel();
            resetPlayers();
            OustPreferences.saveAppInstallVariable("IsAssessmentPlaying", false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void showPopupWindowForHint(String hintText, String closePopup) {
        try {
            String tempHintText;
            popupWindowHint = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
            popupWindowHint.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupWindowHint.setContentView(R.layout.pop_up_layout_hint);
            popupWindowHint.setCancelable(false);
            Objects.requireNonNull(popupWindowHint.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            popupWindowHint.show();
            if (questionBaseModel.isSecureSessionOn()) {
                Objects.requireNonNull(popupWindowHint.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }

            TextView hint_testView = popupWindowHint.findViewById(R.id.titleText);
            TextView got_it = popupWindowHint.findViewById(R.id.got_it);
            WebView hint_webView = popupWindowHint.findViewById(R.id.assessment_hint_webView);

            if (closePopup.equalsIgnoreCase("close")) {
                popupWindowHint.dismiss();
            } else {
                if (hintText != null && !hintText.isEmpty()) {
                    if (hintText.contains("\n")) {
                        tempHintText = hintText.replace("\n", "<br/>");
                    } else {
                        tempHintText = hintText;
                    }
                    if (tempHintText.contains("<li>") || tempHintText.contains("</li>") ||
                            tempHintText.contains("<ol>") || tempHintText.contains("</ol>") ||
                            tempHintText.contains("<p>") || tempHintText.contains("</p>")) {
                        hint_webView.setVisibility(View.VISIBLE);
                        hint_testView.setVisibility(View.GONE);
                        hint_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(tempHintText);
                        final WebSettings webSettings = hint_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        hint_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        hint_testView.setVisibility(VISIBLE);
                        hint_webView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            hint_testView.setText(Html.fromHtml(tempHintText, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            hint_testView.setText(Html.fromHtml(tempHintText));
                        }
                    }
                }

                ImageView close = popupWindowHint.findViewById(R.id.close);
                close.setOnClickListener(v -> popupWindowHint.dismiss());

                got_it.setOnClickListener(v -> popupWindowHint.dismiss());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showPopupWindowForRemarks(final View view) {
        try {
            popupWindowRemarks = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
            popupWindowRemarks.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupWindowRemarks.setContentView(R.layout.pop_up_layout_remarks);
            popupWindowRemarks.setCancelable(false);
            Objects.requireNonNull(popupWindowRemarks.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            popupWindowRemarks.show();
            if (questionBaseModel.isSecureSessionOn()) {
                Objects.requireNonNull(popupWindowRemarks.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }

            EditText edit_comment = popupWindowRemarks.findViewById(R.id.edit_comment);
            LinearLayout dialog_ok = popupWindowRemarks.findViewById(R.id.dialog_ok);
            ImageView close = popupWindowRemarks.findViewById(R.id.close);

            if (questionBaseModel.getScores() != null) {
                if (questionBaseModel.getScores().getRemarks() != null && !questionBaseModel.getScores().getRemarks().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        edit_comment.setText(Html.fromHtml(questionBaseModel.getScores().getRemarks(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        edit_comment.setText(Html.fromHtml(questionBaseModel.getScores().getRemarks()));
                    }
                    edit_comment.setEnabled(false);
                    dialog_ok.setVisibility(View.GONE);
                } else {
                    edit_comment.setEnabled(true);
                    dialog_ok.setVisibility(View.VISIBLE);
                }

            } else {
                edit_comment.setEnabled(true);
                dialog_ok.setVisibility(View.VISIBLE);
            }

            if (tempRemarks != null && tempRemarks.length() > 0) {
                edit_comment.setText(tempRemarks);
            }

            edit_comment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        dialog_ok.setEnabled(true);
                        dialog_ok.setBackground(OustResourceUtils.setDefaultDrawableColor(dialog_ok.getBackground(), color));
                    } else {
                        dialog_ok.setEnabled(false);
                        dialog_ok.setBackground(getResources().getDrawable(R.drawable.button_rounded_ten_bg));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            dialog_ok.setOnClickListener(v -> {
                remarks = edit_comment.getText().toString();
                tempRemarks = edit_comment.getText().toString();
                edit_comment.setText("");
                popupWindowRemarks.dismiss();
            });

            close.setOnClickListener(v -> {
                edit_comment.setText("");
                popupWindowRemarks.dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("NewApi")
    private void checkPermission(boolean isUpload, String typeName) {
        try {
            if (isUpload) {
                if (isPermissionGrantedForUpload()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                REQUEST_STORAGE);
                    }
                } else {
                    if (typeName.equalsIgnoreCase("Image")) {
                        openIntentForImage();
                    } else if (typeName.equalsIgnoreCase("Video")) {
                        openIntentForVideo();
                    }
                }
            } else {
                if (isPermissionGrantedForCapture()) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA);
                } else {
                    if (typeName.equalsIgnoreCase("Image") || typeName.equalsIgnoreCase("Video")) {
                        openCamera(typeName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentExamModeActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, (dialog, item) -> {
            boolean gallery = OustSdkTools.checkPermission(AssessmentExamModeActivity.this);
            boolean camera = OustSdkTools.checkPermissionCamera(AssessmentExamModeActivity.this);
            if (items[item].equals("Take Photo")) {
                userChoosenTask = "Take Photo";
                if (camera)
                    checkPermission(false, "Image");

            } else if (items[item].equals("Choose from Library")) {
                userChoosenTask = "Choose from Library";
                if (gallery)
                    checkPermission(true, "Image");

            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
        builder.setCancelable(true);
    }

    private void selectVideo() {
        final CharSequence[] items = {"Capture Video", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentExamModeActivity.this);
        builder.setTitle("Select Attachment!");
        builder.setItems(items, (dialog, item) -> {
            boolean record = OustSdkTools.checkPermissionRecord(AssessmentExamModeActivity.this);
            boolean gallery = OustSdkTools.checkPermission(AssessmentExamModeActivity.this);
            boolean camera = OustSdkTools.checkPermissionCamera(AssessmentExamModeActivity.this);
            if (items[item].equals("Capture Video")) {
                userChoosenTask = "Capture Video";
                if (record && camera) {
                    checkPermission(false, "Video");
                } else {
                    OustSdkTools.showToast("Please give permission to proceed.");
                }
            } else if (items[item].equals("Choose from Library")) {
                userChoosenTask = "Choose from Library";
                if (gallery)
                    checkPermission(true, "Video");
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void storeImage(Bitmap image, File pictureFile) {
        //File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private void uploadImageToAWS(final File recorded_file, final String mediaType) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast("No Internet connected");
                return;
            }
            extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
            if (extension.equalsIgnoreCase(".mp4") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //compressVideo(recorded_file.toString(), recorded_file, mediaType);
                compressVideoAlert(recorded_file);
            } else {
                ConfirmUpload(recorded_file, mediaType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void ConfirmUpload(final File recorded_file, final String mediaType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentExamModeActivity.this);
        builder.setMessage("Are you sure you want to Upload?");
        builder.setPositiveButton("Confirm", (dialogInterface, i) -> finalUpload(recorded_file, mediaType));

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            mAlertDialogSaveDiscard.dismiss();
            mAlertDialogSaveDiscard = null;
        });
        mAlertDialogSaveDiscard = builder.create();
        mAlertDialogSaveDiscard.show();
        mAlertDialogSaveDiscard.setCancelable(false);
    }

    protected void enableDisableOnclicks(boolean value) {
        if (!value) {
            showProgressBar(2);
            mTextViewUploadProgressText.setVisibility(View.VISIBLE);
        } else {
            hideProgressBar(2);
            mTextViewUploadProgressText.setVisibility(View.GONE);
        }
    }

    public void showProgressBar(int type) {
        switch (type) {
            case 2:
                mProgressBar.setVisibility(View.VISIBLE);
                linearLayoutProgressBar.setVisibility(View.VISIBLE);
                scroll_layout.setVisibility(View.GONE);
                mConstraintLayoutRoot.setVisibility(View.GONE);
                break;
            case 1:
                mProgressBarPostCreate.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void hideProgressBar(int type) {
        switch (type) {
            case 2:
                mProgressBar.setVisibility(View.GONE);
                linearLayoutProgressBar.setVisibility(View.GONE);
                scroll_layout.setVisibility(View.VISIBLE);
                mConstraintLayoutRoot.setVisibility(View.VISIBLE);
                break;
            case 1:
                mProgressBarPostCreate.setVisibility(View.GONE);
                break;
        }
    }


    private void compressVideoAlert(final File filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentExamModeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.compress_popup, null);
        builder.setView(alertLayout);
        LinearLayout mLinearLayoutCompress, mLinearLayoutOriginal;
        ImageView mImageViewClose;
        CustomTextView mTextViewSize;
        mTextViewSize = alertLayout.findViewById(R.id.textViewSize);
        file = filePath;
        String sizeMessage = "This file is of " + Formatter.formatFileSize(this, file.length());
        mLinearLayoutCompress = alertLayout.findViewById(R.id.linearLayoutCompress);
        mLinearLayoutOriginal = alertLayout.findViewById(R.id.linearLayoutOriginal);
        mImageViewClose = alertLayout.findViewById(R.id.imageViewClose);
        mTextViewSize.setText(sizeMessage);

        mLinearLayoutCompress.setOnClickListener(v -> {
            OustSdkTools.createApplicationFolder();
            mAlertDialogForCompress.dismiss();
            if (file.exists()) {
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
                    new VideoCompressor1().execute();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }
        });
        mLinearLayoutOriginal.setOnClickListener(v -> {
            mAlertDialogForCompress.dismiss();
//                finalUpload(filePath, mediaType);
        });

        mImageViewClose.setOnClickListener(v -> {
            mAlertDialogForCompress.dismiss();
            mAlertDialogForCompress = null;
        });

        mAlertDialogForCompress = builder.create();
        mAlertDialogForCompress.setCancelable(false);
        mAlertDialogForCompress.show();
    }

    private void finalUpload(final File recorded_file, final String mediaType) {

        extension = recorded_file.getAbsolutePath().substring(recorded_file.getAbsolutePath().lastIndexOf("."));
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
        String awsKeyId = OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        TransferUtility transferUtility = new TransferUtility(s3, AssessmentExamModeActivity.this);
        if (!recorded_file.exists()) {
            Toast.makeText(AssessmentExamModeActivity.this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        filename = OustMediaTools.getMediaFileName(recorded_file.toString());
        fileSize = recorded_file.length();
        Log.d(TAG, "uploadImageToAWS:fileSize: " + fileSize / 1024 + "KB" + " File Extension:" + extension);
        String key = "mediaUpload/Image/" + filename;
        final TransferObserver observer = transferUtility.upload(AppConstants.StringConstants.S3_BUCKET_NAME, key, recorded_file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer.getState())) {
                    try {
                        if (mediaType.equalsIgnoreCase("VIDEO")) {
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            isVideoSelected = true;
                            isImageSelected = false;
                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                            enableDisableOnclicks(true);
                            filename = null;
                            fileSize = 0;
                            isVideoSelected = false;
                            isImageSelected = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (TransferState.FAILED.equals(observer.getState())) {
                    enableDisableOnclicks(true);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentage = ((float) bytesCurrent / (float) bytesTotal * 100);
                mProgressBar.setProgress((int) percentage);
                mTextViewUploadProgressText.setText((int) percentage + " %");
                Log.d("percentage", "" + percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                filename = null;
                enableDisableOnclicks(true);
                OustSdkTools.showToast(getString(R.string.upload_failed));
            }
        });
    }

    class VideoCompressor1 extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            scroll_layout.setVisibility(View.GONE);
            mConstraintLayoutRoot.setVisibility(View.GONE);
            mediaUploadMsg.setText("" + getResources().getString(R.string.preparing_media));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(file.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                linearLayoutProgressBar.setVisibility(View.GONE);
                scroll_layout.setVisibility(View.VISIBLE);
                mConstraintLayoutRoot.setVisibility(View.VISIBLE);
                file = new File(
                        OustSdkApplication.getContext().getFilesDir(),
                        "compress.mp4"
                );
            }
        }
    }

    private void openIntentForImage() {
        try {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
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
            startActivityForResult(pickVideo, UPLOAD_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCamera(String typeName) {
        try {
            texture_layout.setVisibility(View.VISIBLE);
            image_capture_layout.setVisibility(View.VISIBLE);
            if (texture_view_camera.isAvailable()) {
                openCameraTexture(texture_view_camera.getWidth(), texture_view_camera.getHeight());
            } else {
                texture_view_camera.setSurfaceTextureListener(mSurfaceTextureListener);
            }
            capture_icon.setOnClickListener(v -> {
                OustSdkTools.oustTouchEffect(capture_icon, 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    stopBackgroundThread();
                    if (typeName.equalsIgnoreCase("Image")) {
                        captureImage();
                    } else if (typeName.equalsIgnoreCase("Video")) {
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

                }
            });

            camera_switch.setOnClickListener(v -> {
                try {
                    isCameraFacingRear = !isCameraFacingRear;
                    closeCamera();
                    if (texture_view_camera.isAvailable()) {
                        openCameraTexture(texture_view_camera.getWidth(), texture_view_camera.getHeight());
                    } else {
                        texture_view_camera.setSurfaceTextureListener(mSurfaceTextureListener);
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
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void captureImage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null == mCameraDevice) {
                    return;
                }
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
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

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
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
            title_layout.setVisibility(View.VISIBLE);
            image_preview_card.setVisibility(View.VISIBLE);
            title_layout1.setVisibility(View.GONE);
            close1.setVisibility(View.GONE);
            upload_layout.setVisibility(View.VISIBLE);
            preview_video_lay.setVisibility(View.GONE);
            upload_media_linear.setVisibility(View.VISIBLE);
            upload_media_layout.setVisibility(View.VISIBLE);
            media_choose_layout.setVisibility(View.GONE);
            answer_image.setVisibility(View.VISIBLE);
            inputBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
            answer_image.setImageBitmap(inputBitmap);
            //resetAndRemoveCamera();
            imageFile = File.createTempFile("imageMedia.jpg", null, getCacheDir());
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == UPLOAD_IMAGE) {
                    extractDataFromIntent(data, "UPLOAD_IMAGE");
                    isMediaUploadQuestion = false;
                } else if (requestCode == UPLOAD_VIDEO) {
                    extractDataFromIntent(data, "UPLOAD_VIDEO");
                    isMediaUploadQuestion = false;
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
                String filePath1;
                if (dataUri != null) {
                    Log.e(TAG, "extractDataFromIntent: dataUri--> " + dataUri.getPath());
                    if (fileType.equalsIgnoreCase("UPLOAD_IMAGE")) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(dataUri,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath1 = cursor.getString(columnIndex);
                            //File imgFile = new  File(filePath);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(filePath1, options);
                            final int REQUIRED_SIZE = 1000;
                            int scale = 1;
                            while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                                scale *= 2;
                            options.inSampleSize = scale;
                            options.inJustDecodeBounds = false;
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath1, options);
                            answer_image_lay.setVisibility(View.VISIBLE);
                            title_layout.setVisibility(View.VISIBLE);
                            title_layout1.setVisibility(View.GONE);
                            close1.setVisibility(View.GONE);
                            upload_media_linear.setVisibility(View.VISIBLE);
                            preview_video_lay.setVisibility(View.GONE);
                            upload_media_layout.setVisibility(View.VISIBLE);
                            upload_layout.setVisibility(VISIBLE);
                            image_preview_card.setVisibility(View.VISIBLE);
                            media_choose_layout.setVisibility(View.GONE);
                            inputBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);

                            answer_image.setImageBitmap(inputBitmap);
                            cursor.close();
                            imageFile = File.createTempFile("imageMedia.jpg", null, getCacheDir());
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] b = byteArrayOutputStream.toByteArray();
//                            Picasso.get().load(imageFile).into(answer_image);
                            FileOutputStream fos = new FileOutputStream(imageFile);
                            fos.write(b);
                            fos.flush();
                            fos.close();
                            setButtonColor(color, true);
                        }
                    } else if (fileType.equalsIgnoreCase("UPLOAD_VIDEO")) {
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                        Cursor cursor = getContentResolver().query(dataUri,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            filePath1 = cursor.getString(columnIndex);
                            videoFile = new File(filePath1);
                            uploadFileName = videoFile.getPath();
                            answer_image_lay.setVisibility(View.VISIBLE);
                            title_layout.setVisibility(View.GONE);
                            title_layout1.setVisibility(View.VISIBLE);
                            close2.setVisibility(View.GONE);
                            upload_media_linear.setVisibility(View.VISIBLE);
                            upload_media_layout.setVisibility(View.VISIBLE);
                            upload_layout.setVisibility(VISIBLE);
                            media_choose_layout.setVisibility(View.GONE);
                            Bitmap thumbnail = null;
                            if (videoFile != null) {
                                thumbnail = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                            }
                            handleVideoPreview(thumbnail);
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
            close.setVisibility(VISIBLE);
        }
    }

    private void handleVideoPreview(Bitmap thumbnail) {
        try {
            videoUrl = videoFile.getAbsolutePath();
            preview_video_lay.setVisibility(View.VISIBLE);
            image_preview_card.setVisibility(View.GONE);
            preview_expand_icon.setVisibility(View.GONE);
            title_layout.setVisibility(View.GONE);
            title_layout1.setVisibility(VISIBLE);
            close2.setVisibility(View.GONE);
            close1.setVisibility(View.GONE);
            close.setVisibility(View.GONE);

            if (mediaPlayerForAudio != null) {
                mediaPlayerForAudio.isPlaying();
                mediaPlayerForAudio.pause();
                isMute = true;
            }

            if (thumbnail != null)
                video_thumbnail.setImageBitmap(thumbnail);

            video_container.setVideoPath(videoUrl);
            video_container.setMediaController(new android.widget.MediaController(AssessmentExamModeActivity.this));
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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isPermissionGrantedForUpload() {
        return ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean isPermissionGrantedForCapture() {
        if (isPermissionGrantedForUpload()) {
            return ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(AssessmentExamModeActivity.this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        }
    }

    private void startBackgroundThread() {
        try {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCameraTexture(int textureWidth, int textureHeight) {
        try {
            Activity activity = AssessmentExamModeActivity.this;
            if (activity.isFinishing()) {
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (typeName.equalsIgnoreCase("Image")) {
                    CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                        throw new RuntimeException("Time out waiting to lock camera opening.");
                    }
                    mCameraId = manager.getCameraIdList()[0];
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
                    StreamConfigurationMap map = characteristics
                            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    if (map == null) {
                        throw new RuntimeException("Cannot get available preview sizes");
                    }
                    Size imageSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new MUQuestionFragment.CompareSizesByArea());
                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), textureWidth, textureHeight, imageSize);
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        texture_view_camera.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    } else {
                        texture_view_camera.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    }
                    configureTransform(textureWidth, textureHeight);
                    manager.openCamera(mCameraId, mStateCallback, null);
                } else if (typeName.equalsIgnoreCase("Video")) {
                    openCameraForVideo(textureWidth, textureHeight);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new MUQuestionFragment.CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @SuppressLint("NewApi")
    private void openCameraForVideo(int textureWidth, int textureHeight) {
        try {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            mCameraId = manager.getCameraIdList()[0];

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (map == null) {
                throw new RuntimeException("Cannot get available preview sizes");
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), textureWidth, textureHeight, mVideoSize);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                texture_view_camera.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                texture_view_camera.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }
            configureTransform(textureWidth, textureHeight);
            releaseVideoMediaRecorder();
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(mCameraId, mStateCallback, null);

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
                    mMediaRecorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @SuppressLint("NewApi")
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
                    runOnUiThread(() -> {
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
                    Toast.makeText(AssessmentExamModeActivity.this, "" + getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
                // final Activity activity = AssessmentExamModeActivity.this;

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

    @SuppressLint("NewApi")
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
            try {
                if (mediaPlayerForAudio != null) {
                    mediaPlayerForAudio.isPlaying();
                    mediaPlayerForAudio.pause();
                    isMute = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            texture_layout.setVisibility(View.GONE);
            image_capture_layout.setVisibility(View.GONE);
            upload_layout.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
            close1.setVisibility(View.GONE);
            closeCamera();
            answer_image_lay.setVisibility(View.VISIBLE);
            upload_media_linear.setVisibility(View.VISIBLE);
            upload_media_layout.setVisibility(View.VISIBLE);

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
            Activity activity = AssessmentExamModeActivity.this;
            activity.finish();
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
            Activity activity = AssessmentExamModeActivity.this;
            if (null == texture_view_camera || null == mPreviewSize) {
                return;
            }
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
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
            }
            texture_view_camera.setTransform(matrix);
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
            openCameraTexture(width, height);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setButtonColor(int color, boolean isEnabled) {
        checkEnabled = isEnabled;
        try {
            Drawable actionDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void uploadFailed() {
        try {
            Dialog uploadFailedDialog = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
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

            });

            info_yes.setOnClickListener(v -> {
                if (uploadFailedDialog.isShowing()) {
                    uploadFailedDialog.dismiss();
                }
                if (typeName.equalsIgnoreCase("Image")) {
                    if (imageFile != null) {
                        uploadImageToAWS();
                    }
                } else if (typeName.equalsIgnoreCase("Video")) {
                    if (videoFile != null) {
                        compressVideoAndUpload();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void uploadProgress() {
        try {
            uploadProgressDialog = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
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
            TransferUtility transferUtility = TransferUtility.builder().s3Client(amazonS3).context(AssessmentExamModeActivity.this).build();
            try {
                if (OustAppState.getInstance().getActiveUser().getStudentKey() != null) {
                    uploadFileName = System.currentTimeMillis() + OustAppState.getInstance().getActiveUser().getStudentKey();
                } else {
                    uploadFileName = "" + System.currentTimeMillis();
                }
            } catch (Exception e) {
                uploadFileName = "" + System.currentTimeMillis();
            }
            uploadProgress();
            final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "commentMedia/Image/" + uploadFileName + ".jpg", imageFile);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(observer.getState())) {
                        answer_image_lay.setVisibility(View.GONE);
                        media_choose_layout.setVisibility(View.VISIBLE);
                        close.setVisibility(VISIBLE);
                        upload_message.setText(getResources().getString(R.string.upload_success));
                        isVideoSelected = false;
                        isImageSelected = true;

                        new Handler().postDelayed(() -> {
                            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                uploadProgressDialog.dismiss();
                            }

                            questionMedia = "commentMedia/Image/" + uploadFileName + ".jpg";
                            upload_media_layout.setVisibility(View.GONE);
                            Toast.makeText(AssessmentExamModeActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                        }, 500);
                        try {
                            if (tempAnsweredSeconds > 0) {
                                Log.d("ExamMode", "Timer start on uploadImage temp");
                                startQuestionTimer(tempAnsweredSeconds);
                                tempAnsweredSeconds = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        isMediaUploadQuestion = false;
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
                    upload_media_layout.setVisibility(View.GONE);
                    try {
                        if (tempAnsweredSeconds > 0) {
                            Log.d("ExamMode", "Timer start on upload image failed");
                            startQuestionTimer(tempAnsweredSeconds);
                            tempAnsweredSeconds = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    isMediaUploadQuestion = false;
                    uploadFailed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                uploadProgressDialog.dismiss();
            }
            upload_media_layout.setVisibility(View.GONE);
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
                    Log.d(TAG, "Compressed Size: " + Formatter.formatFileSize(AssessmentExamModeActivity.this, videoFile.length()));
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
            TransferUtility transferUtility = TransferUtility.builder().s3Client(amazonS3).context(AssessmentExamModeActivity.this).build();
            if (videoFile == null || !videoFile.exists()) {
                if (uploadProgressDialog != null && uploadProgressDialog.isShowing())
                    uploadProgressDialog.dismiss();
                Toast.makeText(AssessmentExamModeActivity.this, getResources().getString(R.string.unable_to_select_attachment), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (OustAppState.getInstance().getActiveUser().getStudentKey() != null) {
                        uploadFileName = System.currentTimeMillis() + OustAppState.getInstance().getActiveUser().getStudentKey();
                    } else {
                        uploadFileName = "" + System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    uploadFileName = "" + System.currentTimeMillis();
                }

                Log.e(TAG, "uploadVideoToAWS: --> videoFile -->" + videoFile);
                final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "commentMedia/Video/" + uploadFileName + ".mp4", videoFile);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED.equals(observer.getState())) {
                            if (video_container != null && video_container.isPlaying()) {
                                video_container.stopPlayback();
                                video_container.clearAnimation();
                                video_container.suspend();
                                video_container.setVideoURI(null);
                            }
                            preview_video_lay.setVisibility(View.GONE);
                            answer_image_lay.setVisibility(View.GONE);
                            media_choose_layout.setVisibility(View.VISIBLE);
                            close.setVisibility(VISIBLE);
                            Log.e(TAG, "onStateChanged:uploadFileName--> " + uploadFileName);
                            upload_message.setText(getResources().getString(R.string.upload_success));
                            isVideoSelected = true;
                            isImageSelected = false;

                            new Handler().postDelayed(() -> {
                                if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
                                    uploadProgressDialog.dismiss();
                                }
                                questionMedia = "commentMedia/Video/" + uploadFileName + ".mp4";
                                upload_media_layout.setVisibility(View.GONE);
                                Toast.makeText(AssessmentExamModeActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                            }, 1000);
                            try {
                                if (tempAnsweredSeconds > 0) {
                                    Log.d("ExamMode", "Timer start on upload video temp");
                                    startQuestionTimer(tempAnsweredSeconds);
                                    tempAnsweredSeconds = 0;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            isMediaUploadQuestion = false;
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
                        upload_media_layout.setVisibility(View.GONE);
                        try {
                            if (tempAnsweredSeconds > 0) {
                                Log.d("ExamMode", "Timer start on upload video failed");
                                startQuestionTimer(tempAnsweredSeconds);
                                tempAnsweredSeconds = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        isMediaUploadQuestion = false;
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
            upload_media_layout.setVisibility(View.GONE);
        }
    }

    private void reviewSubmitPopup() {
        try {
            if (assessmentReviewDialog != null && assessmentReviewDialog.isShowing()) {
                assessmentReviewDialog.dismiss();
            }
            assessmentReviewDialog = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
            assessmentReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            assessmentReviewDialog.setContentView(R.layout.common_pop_up);
            assessmentReviewDialog.setCancelable(true);
            Objects.requireNonNull(assessmentReviewDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            assessmentReviewDialog.show();

            LinearLayout info_review = assessmentReviewDialog.findViewById(R.id.info_no);
            TextView info_review_text = assessmentReviewDialog.findViewById(R.id.info_no_text);
            LinearLayout info_submit = assessmentReviewDialog.findViewById(R.id.info_yes);
            TextView info_submit_text = assessmentReviewDialog.findViewById(R.id.info_yes_text);
            CardView card_layout = assessmentReviewDialog.findViewById(R.id.card_layout);
            TextView info_title = assessmentReviewDialog.findViewById(R.id.info_title);
            TextView info_description = assessmentReviewDialog.findViewById(R.id.info_description);
            ImageView info_image = assessmentReviewDialog.findViewById(R.id.info_image);

            Drawable infoDrawable = getResources().getDrawable(R.drawable.tick_survey);
            String infoDescription = getResources().getString(R.string.thanks_qstn_text) + "\n" + getResources().getString(R.string.review_submit_text);
            if (questionsArrayList != null && questionsArrayList.size() != 0) {
                int totalQuestion = questionsArrayList.size();
                int answeredQuestion = 0;

                HashMap<Integer, QuestionView> questionViewHashMap = OustStaticVariableHandling.getInstance().getQuestionViewList();
                if (questionViewHashMap != null && questionViewHashMap.size() != 0) {
                    for (DTOQuestions questions : questionsArrayList) {
                        QuestionView questionView = questionViewHashMap.get((int) questions.getQuestionId());
                        if (questionView != null && questionView.isAnswered()) {
                            answeredQuestion++;
                        }
                    }

                    if (totalQuestion - answeredQuestion <= 0) {
                        infoDrawable = getResources().getDrawable(R.drawable.tick_survey);
                        infoDescription = getResources().getString(R.string.thanks_qstn_text) + "\n" + getResources().getString(R.string.review_submit_text);
                    } else {
                        infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
                        //X questions have not been answered. Make sure you answer all question before submitting. Do you want to Review or Submit ?
                        if ((totalQuestion - answeredQuestion) > 1) {
                            infoDescription = (totalQuestion - answeredQuestion) + " " + getResources().getString(R.string.questions_remaining_exam) + getResources().getString(R.string.review_submit_text);
                        } else {
                            infoDescription = (totalQuestion - answeredQuestion) + " " + getResources().getString(R.string.question_remaining_exam) + getResources().getString(R.string.review_submit_text);
                        }

                    }

                } else {
                    infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
                    infoDescription = (totalQuestion - answeredQuestion) + " " + getResources().getString(R.string.questions_remaining_exam) + getResources().getString(R.string.review_submit_text);
                }

            }

            info_title.setText("End Assessment");
            info_description.setText(infoDescription);
            info_description.setVisibility(VISIBLE);
            info_image.setImageDrawable(infoDrawable);

            Drawable reviewDrawable = getResources().getDrawable(R.drawable.white_btn_bg);
            info_review.setBackground(reviewDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            info_submit.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_review_text.setTextColor(OustResourceUtils.getColors());

            info_review_text.setText(getResources().getString(R.string.review_text));
            info_submit_text.setText(getResources().getString(R.string.submit));

            info_review.setOnClickListener(v -> {
                question_list_layout.setVisibility(View.GONE);
                question_list_rv.removeAllViews();
                adapter = null;
                if (assessmentReviewDialog.isShowing()) {
                    assessmentReviewDialog.dismiss();
                }
                questionBaseViewModel.handleReview();

            });

            info_submit.setOnClickListener(v -> {
                if (assessmentReviewDialog.isShowing()) {
                    assessmentReviewDialog.dismiss();
                }
                submitConfirmationPopup();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void submitConfirmationPopup() {
        try {
            if (assessmentReviewDialog != null && assessmentReviewDialog.isShowing()) {
                assessmentReviewDialog.dismiss();
            }
            assessmentReviewDialog = new Dialog(AssessmentExamModeActivity.this, R.style.DialogTheme);
            assessmentReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            assessmentReviewDialog.setContentView(R.layout.common_pop_up);
            assessmentReviewDialog.setCancelable(true);
            Objects.requireNonNull(assessmentReviewDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            assessmentReviewDialog.show();

            LinearLayout info_review = assessmentReviewDialog.findViewById(R.id.info_no);
            TextView info_review_text = assessmentReviewDialog.findViewById(R.id.info_no_text);
            LinearLayout info_submit = assessmentReviewDialog.findViewById(R.id.info_yes);
            TextView info_submit_text = assessmentReviewDialog.findViewById(R.id.info_yes_text);
            CardView card_layout = assessmentReviewDialog.findViewById(R.id.card_layout);
            TextView info_title = assessmentReviewDialog.findViewById(R.id.info_title);
            TextView info_description = assessmentReviewDialog.findViewById(R.id.info_description);
            ImageView info_image = assessmentReviewDialog.findViewById(R.id.info_image);

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
            String infoDescription = getResources().getString(R.string.submit_confirmation);


            info_title.setText(getResources().getString(R.string.warning));
            info_description.setText(infoDescription);
            info_description.setVisibility(VISIBLE);
            info_image.setImageDrawable(infoDrawable);

            Drawable reviewDrawable = getResources().getDrawable(R.drawable.white_btn_bg);
            info_review.setBackground(reviewDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            info_submit.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_review_text.setTextColor(OustResourceUtils.getColors());

            info_review_text.setText(getResources().getString(R.string.cancel));
            info_submit_text.setText(getResources().getString(R.string.submit));

            info_review.setOnClickListener(v -> {
                question_list_layout.setVisibility(View.GONE);
                question_list_rv.removeAllViews();
                adapter = null;
                if (assessmentReviewDialog.isShowing()) {
                    assessmentReviewDialog.dismiss();
                }
                questionBaseViewModel.handleReview();
                handleAudio();

            });

            info_submit.setOnClickListener(v -> {
                question_list_layout.setVisibility(View.GONE);
                question_list_rv.removeAllViews();
                adapter = null;
                if (assessmentReviewDialog.isShowing()) {
                    assessmentReviewDialog.dismiss();
                }

                card_layout.setVisibility(View.GONE);

                question_base_root.setVisibility(View.GONE);
                branding_mani_layout.setVisibility(View.VISIBLE);

                questionBaseViewModel.calculateFinalScore(false, "");
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadManager(String downloadUrl, String fileName) {
        final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(fileName);
        request.setDescription(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
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

                        if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                            @SuppressLint("Range") String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //update percentage
                            Log.i("DownloadHandler", "Download completed " + uriString);
                            if (uriString != null) {
                                File sourceFile = new File(Uri.parse(uriString).getPath());
                                File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                try {
                                    OustSdkTools.copyFile(sourceFile, destinationFile);
                                    imageFile = destinationFile;
                                    imageUrl = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                        } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                            int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                            int reason = cur.getInt(columnReason);
                            imageUrl = downloadUrl;
                        }
                        // downloadManager.remove(idFromCursor);
                    }
                    cur.close();
                }
            }
        };
        context.getApplicationContext().registerReceiver(receiver, filter);
    }

    private void handledBottomNextArrow() {
        try {
            String questionCategory = questions.getQuestionCategory();
            Fragment frag = getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex());
            if (questions.isLearningPlayNew()) {
                Log.e(TAG, "LearningPlayNew timer end");
                if ((questions.getQuestionType() != null && (questions.getQuestionType().equals(QuestionType.FILL)))) {
                    if (frag != null) {
                        FIBQuestionFragment fibQuestionFragment = (FIBQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (fibQuestionFragment != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                fibQuestionFragment.handleAnswer(false, false, true);
                            }
                        }
                    }
                } else if (questions.getQuestionCategory() != null) {
                    if (questions.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                        if (frag != null) {
                            MTFQuestionFragment mtfQuestionFragment = (MTFQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                            if (mtfQuestionFragment != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mtfQuestionFragment.scoreHandling(false, false, true);
                                }
                            }
                        }
                    }
                }
            } else if (questions.isMediaUploadQues()) {
                if (frag != null) {
                    MUQuestionFragment muQuestionFragment = (MUQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                    if (muQuestionFragment != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            muQuestionFragment.onTimeOut(false, true);
                        }
                    }
                }
            } else if (questions.getQuestionType() != null) {
                if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    if (frag != null) {
                        MCQuestionFragment mcQuestionFragment = (MCQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mcQuestionFragment != null) {
                            mcQuestionFragment.setAnswers(1, false, true);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    if (frag != null) {
                        MRQuestionFragment mrQuestionFragment = (MRQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mrQuestionFragment != null) {
                            mrQuestionFragment.calculateMrqAnswer(true, false, false, false, true);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DATE)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)) {
                    if (frag != null) {
                        DateAndTimeFragment mrQuestionFragment = (DateAndTimeFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mrQuestionFragment != null) {
                            mrQuestionFragment.setAnswerForDateAndTime(false, true);
                        }
                    }
                } else if (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                    if (frag != null) {
                        LongQuestionFragment longQuestionFragment = (LongQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (longQuestionFragment != null) {
                            longQuestionFragment.setAnswerForLongAnswer(false, true);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DROPDOWN) || (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.DROPDOWN))) {
                    if (frag != null) {
                        DropDownQuestionFragment dropDownQuestionFragment = (DropDownQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (dropDownQuestionFragment != null) {
                            dropDownQuestionFragment.setAnswers(1, false, true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handledBottomPreviousArrow() {
        try {
            String questionCategory = questions.getQuestionCategory();
            Fragment frag = getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex());
            if (questions.isLearningPlayNew()) {
                Log.e(TAG, "LearningPlayNew timer end");
                if ((questions.getQuestionType() != null && (questions.getQuestionType().equals(QuestionType.FILL)))) {
                    if (frag != null) {
                        FIBQuestionFragment fibQuestionFragment = (FIBQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (fibQuestionFragment != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                fibQuestionFragment.handleAnswer(false, true, false);
                            }
                        }
                    }
                } else if (questions.getQuestionCategory() != null) {
                    if (questions.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                        if (frag != null) {
                            MTFQuestionFragment mtfQuestionFragment = (MTFQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                            if (mtfQuestionFragment != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mtfQuestionFragment.scoreHandling(false, true, false);
                                }
                            }
                        }
                    }
                }
            } else if (questions.isMediaUploadQues()) {
                if (frag != null) {
                    MUQuestionFragment muQuestionFragment = (MUQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                    if (muQuestionFragment != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            muQuestionFragment.onTimeOut(true, false);
                        }
                    }
                }
            } else if (questions.getQuestionType() != null) {
                if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    if (frag != null) {
                        MCQuestionFragment mcQuestionFragment = (MCQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mcQuestionFragment != null) {
                            mcQuestionFragment.setAnswers(1, true, false);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)
                        && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                    if (frag != null) {
                        MRQuestionFragment mrQuestionFragment = (MRQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mrQuestionFragment != null) {
                            mrQuestionFragment.calculateMrqAnswer(true, false, false, true, false);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DATE)
                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)) {
                    if (frag != null) {
                        DateAndTimeFragment mrQuestionFragment = (DateAndTimeFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (mrQuestionFragment != null) {
                            mrQuestionFragment.setAnswerForDateAndTime(true, false);
                        }
                    }
                } else if (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                    if (frag != null) {
                        LongQuestionFragment longQuestionFragment = (LongQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (longQuestionFragment != null) {
                            longQuestionFragment.setAnswerForLongAnswer(true, false);
                        }
                    }
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DROPDOWN) || (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.DROPDOWN))) {
                    if (frag != null) {
                        DropDownQuestionFragment dropDownQuestionFragment = (DropDownQuestionFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionBaseModel.getQuestionIndex()));
                        if (dropDownQuestionFragment != null) {
                            dropDownQuestionFragment.setAnswers(1, true, false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
