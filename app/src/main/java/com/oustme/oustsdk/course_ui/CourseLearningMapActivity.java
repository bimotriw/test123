package com.oustme.oustsdk.course_ui;

import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.downloadHandler.DownloadForegroundService.START_UPLOAD;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.CommonTools.courseCardSorter;
import static com.oustme.oustsdk.tools.CommonTools.courseLevelSorter;
import static com.oustme.oustsdk.tools.CommonTools.courseUserCardSorter;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_FINISH_ICON;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_LEVEL_INDICATOR_ICON;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.activity.courses.learningmapmodule.AdaptiveLearningMapModuleActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.LearningMapView;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.course_ui.adapter.NonPlayModeAdapter;
import com.oustme.oustsdk.customviews.NewSimpleLine;
import com.oustme.oustsdk.downloadHandler.DownloadForegroundService;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseDesclaimerData;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.interfaces.course.LearningCallBackInterface;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.model.response.course.UserEventCourseData;
import com.oustme.oustsdk.profile.CourseCompletionWithBadgeActivity;
import com.oustme.oustsdk.question_module.course.CourseLearningModuleActivity;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationManager;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.AssessmentNavModel;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.SurveyNavModel;
import com.oustme.oustsdk.room.EntityDownloadedOrNot;
import com.oustme.oustsdk.room.EntityLevelCardCourseIDUpdateTime;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

public class CourseLearningMapActivity extends BaseActivity implements LearningModuleInterface, LearningMapView,
        LearningCallBackInterface, ReviewModeCallBack {

    Toolbar course_tool_bar;
    ProgressBar user_course_progressbar;
    TextView user_completed_percentage;
    TextView user_coins_earned;
    LinearLayout collapsing_layout;
    TextView course_title;
    LinearLayout course_duration_lay;
    TextView course_remaining_duration;
    ImageView course_download;
    ImageView course_delete;
    LinearLayout mode_switch_layout;
    TextView play_mode_text;
    SwitchMaterial mode_switch;
    TextView review_mode_text;
    ImageView course_bg_image;
    RelativeLayout play_mode;
    ScrollView view_scroll;
    RelativeLayout learning_dynamic_layout;
    NewSimpleLine simple_line_view;
    RelativeLayout dummy_layout;
    RelativeLayout non_play_mode;
    RecyclerView level_rv;
    RelativeLayout learning_action_button;
    RelativeLayout course_level_relativeLayout;
    RelativeLayout course_ack_popup;
    TextView ack_heading;
    TextView ack_content;
    RelativeLayout ack_agree_layout;
    ImageView ack_agree_checkbox;
    TextView ack_agree_checkbox_text;
    RelativeLayout ack_ok_layout;
    TextView ack_ok_button_text;
    LinearLayout course_intro_card_layout;
    GifImageView downloadGifImageView;
    RelativeLayout downloadVideoLayout;
    TextView downloadVideoPercentage;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    Dialog popupWindowEnrollementRetry;
    //End

    //intent data
    private long courseId;
    private String multilingualCourseId;
    private long multiCourseId = 0;
    private boolean isRegularMode;
    private String courseColId;
    private boolean isMultiLingual;
    private boolean isEvent;
    private int eventId = 0;
    private boolean isComingFromCpl = false;
    private boolean isFreeCourse;
    private boolean isReviewMode;
    private boolean isPlayReviewMode;
    private boolean isSalesMode;
    private boolean disableBackButton = false;
    private int color;
    private int bgColor;
    private boolean isLeaderBoardEnable;
    private boolean isCourseCommentEnable;
    private boolean isCertificateEnable;
    private boolean isBadgeEnable;
    private boolean comingFromIntroCard = false;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //common Data
    ActiveUser activeUser;
    CourseModulePresenter courseModulePresenter;
    CourseDataClass courseDataClass;
    boolean isSurveyAttached;
    boolean startSurveyImmediately = true;
    long surveyId = 0;
    boolean showCourseCompletionPopup = false;
    boolean isShowRatingPopUp = true;
    boolean isCourseAlreadyCompleteTemp = false;
    boolean isRateCourseShownOnce = false, isSoundPlayedOnce = false;
    boolean isLocked = false;
    boolean isPurchased = true;
    boolean isSoundTobePlayed = false;
    boolean enableCourseCompleteAudio = false;
    long assessmentId = 0;
    boolean enableVideoDownload;
    Map<String, Object> cardInfo;
    boolean isAnyPopupVisible = false;
    CourseDesclaimerData courseDesclaimerData;
    boolean isCourseAlreadyComplete = false;
    UserEventAssessmentData userEventAssessmentData = new UserEventAssessmentData();
    boolean isCourseCompleted = false;
    boolean isSurveyOpened;
    boolean allCourseDownloadStarted = false;
    boolean downloaded;
    boolean courseAlreadyDownloaded = false;
    boolean downloadCourseClicked = false;
    String language;
    boolean isCallBackHappened = false;
    boolean isScoreDisplaySecondTime;
    boolean isLearningCardOpen = false;
    boolean showFinalAnimation = false;
    boolean isAssessmentCompleted = false;
    boolean isAssessmentError = false;
    boolean isAdaptiveCourse = false;
    boolean isDisableLevelCompletePopup = false;
    boolean enableLevelCompleteAudio = false;
    boolean isEnrolled = false;
    boolean showAcknowledgementPopup = false;
    boolean isActiveLive = true;
    boolean isMicroCourse = false;
    boolean isMicroCoursePlay = false;
    boolean stopPlayer = false;
    int courseCurrentLevel;
    private boolean isMultipleCplEnable = false;
    private String isCplId;
    private boolean isCourseCompletionPopupShown = false;
    String courseAddedOn;
    MediaPlayer completionAudioPlayer;

    //other data
    List<DTOUserLevelData> mUserLevelDataList;
    DTOUserCourseData mUserCourseData;
    MyFileDownLoadReceiver myFileDownLoadReceiver;
    CourseDownloadReceiver courseDownloadReceiver;
    DownloadReceiver downloadReceiver;
    MyDownloadReceiver myDownloadReceiver;
    DTOCourseCard dtoCourseCard;
    int mediaSize = 0;
    int pathSize = 0;
    int downloadedSize = 0;
    int alreadyDownloadSize = 0;
    boolean isResumeClicked = false;
    String levelToRestartAfterPermission;
    AnimationDrawable downloadAnimation;
    PopupWindow courseComplete_popup;
    Dialog certificateEmail_popup;

    String levelUnlockColor, levelLockedColor, currentLevelColor;

    //NonPlay mode
    NonPlayModeAdapter nonPlayModeAdapter;
    List<SearchCourseLevel> filteredSearchCourseLevelList;
    List<SearchCourseLevel> searchCourseLevelList;

    //PlayMode view
    int levelBoxSize;
    LayoutInflater mInflater;
    List<View> levelDescViews = new ArrayList<>();//addedView
    View levelIndicatorView;
    View endLevelView;

    int pathY = 0;
    final String TAG = "CourseLearningMap";

    int currentLevelPosition;
    int currentCardPosition;
    private boolean checkBoxStatus;
    boolean isInstrumentationHit = false;
    DTOUserCourseData userCourseData;
    List<EntityLevelCardCourseIDUpdateTime> entityLevelCardCourseIDUpdateTime = new ArrayList<>();
    Dialog showDialogDownload;
    int noOfVideos = 0;
    boolean dialogOpened = false;
    List<EntityDownloadedOrNot> entityDownloadedOrNot = new ArrayList<>();
    boolean fullCourseDownloaded = false;
    boolean normalCourseDownloaded = false;
    String courseDownloadId;
    long mLastTimeClickedRefresh = 0;
    boolean restartLevelStatus = false;
    boolean updateLevelStatus = false;
    int levelId = 0;
    Dialog showOfflineSubmitPopUp;
    boolean apiLoaded = false;


    @Override
    protected int getContentView() {
        try {
            OustPreferences.saveAppInstallVariable("CLICK", false);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            OustAppState.getInstance().setLearningCallBackInterface(this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return R.layout.activity_course_learning_map;
    }

    @Override
    protected void initView() {
        try {
            getColors();
            if (OustSdkApplication.getContext() == null)
                OustSdkApplication.setmContext(CourseLearningMapActivity.this);

            OustSdkTools.setLocale(CourseLearningMapActivity.this);
            OustPreferences.saveAppInstallVariable("CLICK", false);
            OustPreferences.saveAppInstallVariable("course_download", false);
            course_tool_bar = findViewById(R.id.course_tool_bar);
            user_course_progressbar = findViewById(R.id.user_course_progressbar);
            user_completed_percentage = findViewById(R.id.user_completed_percentage);
            user_coins_earned = findViewById(R.id.user_coins_earned);
            course_level_relativeLayout = findViewById(R.id.course_level_relativeLayout);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            collapsing_layout = findViewById(R.id.collapsing_layout);
            course_title = findViewById(R.id.course_title);
            course_duration_lay = findViewById(R.id.course_duration_lay);
            course_remaining_duration = findViewById(R.id.course_remaining_duration);
            course_download = findViewById(R.id.course_download);
            course_delete = findViewById(R.id.course_delete);
            mode_switch_layout = findViewById(R.id.mode_switch_layout);
            play_mode_text = findViewById(R.id.play_mode_text);
            mode_switch = findViewById(R.id.mode_switch);
            review_mode_text = findViewById(R.id.review_mode_text);
            course_bg_image = findViewById(R.id.course_bg_image);
            play_mode = findViewById(R.id.play_mode);
            view_scroll = findViewById(R.id.view_scroll);
            learning_dynamic_layout = findViewById(R.id.learning_dynamic_layout);
            simple_line_view = findViewById(R.id.simple_line_view);
            dummy_layout = findViewById(R.id.dummy_layout);
            non_play_mode = findViewById(R.id.non_play_mode);
            level_rv = findViewById(R.id.level_rv);
            learning_action_button = findViewById(R.id.learning_action_button);
            course_ack_popup = findViewById(R.id.course_acknowledgment);
            ack_heading = findViewById(R.id.ack_heading);
            ack_content = findViewById(R.id.ack_content);
            ack_agree_layout = findViewById(R.id.ack_agree_layout);
            ack_agree_checkbox = findViewById(R.id.ack_agree_checkbox);
            ack_agree_checkbox_text = findViewById(R.id.ack_agree_checkbox_text);
            ack_ok_layout = findViewById(R.id.ack_ok_layout);
            ack_ok_button_text = findViewById(R.id.ack_ok_button_text);
            course_intro_card_layout = findViewById(R.id.course_intro_card_layout);
            downloadGifImageView = findViewById(R.id.download_video_icon);
            downloadVideoPercentage = findViewById(R.id.download_video_text);
            downloadVideoLayout = findViewById(R.id.download_video_layout);

            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    user_coins_earned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    user_coins_earned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }

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
                OustSdkTools.sendSentryException(e);
            }

            course_tool_bar.setTitle("");
            setSupportActionBar(course_tool_bar);

            setIconColors();
            isCallBackHappened = false;

            play_mode_text.setText(getResources().getString(R.string.play_text));
            review_mode_text.setText(getResources().getString(R.string.review_text));

            try {
                OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
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
    protected void initData() {
        try {
            levelLockedColor = OustPreferences.get(AppConstants.StringConstants.COURSE_LOCK_COLOR);
            levelUnlockColor = OustPreferences.get(AppConstants.StringConstants.COURSE_UNLOCK_COLOR);
            currentLevelColor = OustPreferences.get(AppConstants.StringConstants.CURRENT_LEVEL_COLOR);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isCplId = bundle.getString("currentCplId");
                courseId = bundle.getLong("learningId");
                multilingualCourseId = bundle.getString("multilingualId");
                isRegularMode = bundle.getBoolean("isRegularMode");
                isMultipleCplEnable = bundle.getBoolean("isMultipleCplModule", false);
                courseColId = bundle.getString("courseColnId");
                isMultiLingual = bundle.getBoolean("isMultiLingual", false);
                isMicroCourse = bundle.getBoolean("isMicroCourse", false);
                isMicroCoursePlay = bundle.getBoolean("isMicroCoursePlay", false);
                isEvent = bundle.getBoolean("isEventLaunch", false);
                eventId = bundle.getInt("eventId", 0);
                isComingFromCpl = bundle.getBoolean("isComingFromCpl", false);
                isFreeCourse = bundle.getBoolean("freeCourse", false);
                isShowRatingPopUp = bundle.getBoolean("rateCourse", true);
//                isCourseAlreadyCompleteTemp = bundle.getBoolean("isCourseAlreadyComplete", false);
                if (courseColId != null) {
                    isPurchased = bundle.getBoolean("purchased", false);
                    isLocked = bundle.getBoolean("locked", true);
                }
            }
            Log.e(TAG, "CourseLearingMapActivity:isCplId--> " + isCplId);
            Log.e(TAG, "CourseLearingMapActivity:isMultipleCplEnable-->  " + isMultipleCplEnable);

            if (multilingualCourseId != null) {
                multiCourseId = Long.parseLong(multilingualCourseId);
            }
            activeUser = OustAppState.getInstance().getActiveUser();

            if (isMicroCourse) {
                branding_mani_layout.setVisibility(View.VISIBLE);
                branding_mani_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                course_tool_bar.setVisibility(View.GONE);
                course_level_relativeLayout.setVisibility(View.GONE);
                try {
                    com.oustme.oustsdk.tools.OustSdkTools.setImage(course_bg_image, getResources().getString(R.string.bg_1));
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } else {
                branding_mani_layout.setVisibility(View.GONE);
                course_tool_bar.setVisibility(View.VISIBLE);
                course_level_relativeLayout.setVisibility(View.VISIBLE);
            }

            OustSdkApplication.setmContext(CourseLearningMapActivity.this);

            if (activeUser == null || activeUser.getStudentid() == null) {
                try {
                    String activeUserGet = OustPreferences.get("userdata");
                    activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                    OustFirebaseTools.initFirebase();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                OustAppState.getInstance().setActiveUser(activeUser);
            }

            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;

            courseModulePresenter = new CourseModulePresenter(CourseLearningMapActivity.this, scrWidth, scrHeight, bundle);
            courseModulePresenter.setMicroCourse(isMicroCourse);
            courseModulePresenter.setIsMultipleCplEnable(isMultipleCplEnable);
            courseModulePresenter.setMultipleCPlId(isCplId);
            courseModulePresenter.getBulletinQuesFromFirebase(courseId);

            OustPreferences.save("current_course_id", "" + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {
        try {
            course_download.setOnClickListener(v -> {
                try {
                    try {
                        downloadAnimation = (AnimationDrawable) course_download.getDrawable();
                        RoomHelper.downloadedOrNot(courseId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    courseAlreadyDownloaded = false;
                    downloadCourseClicked = false;
                    if (mUserLevelDataList != null && mUserLevelDataList.size() > 0) {
                        for (int i = 0; i < mUserLevelDataList.size(); i++) {
                            try {
                                RoomHelper.getAllCourseInLevel(mUserLevelDataList.get(i).getLevelId());
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            courseModulePresenter.updateLevelDownloadStatus();
                            course_delete.setVisibility(View.GONE);
                            course_download.setVisibility(View.VISIBLE);
                            mUserLevelDataList.get(i).setCompletePercentage(0);
                            mUserCourseData.setDownloadCompletePercentage(0);
                            courseModulePresenter.addUserData(mUserCourseData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                OustPreferences.saveAppInstallVariable("course_download", true);
                if (enableVideoDownload && noOfVideos > 0) {
                    showDialogForDownload();
                } else {
                    startCourseDownload();
                }
            });

            course_delete.setOnClickListener(v -> deleteConfirmation());
            course_intro_card_layout.setOnClickListener(view -> {
                try {
                    comingFromIntroCard = false;
                    alreadyDownloadSize = 0;
                    branding_mani_layout.setVisibility(View.VISIBLE);
                    checkForDescriptionCardData(cardInfo);
                    course_intro_card_layout.setClickable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            learning_action_button.setOnClickListener(v -> {
                if (isRegularMode) {
                    DTOUserCourseData userCourseDataForMicro = courseModulePresenter.getUserCourseData();
                    CourseDataClass courseDataClassForMicro = courseModulePresenter.getCourseDataClass();
                    if (userCourseDataForMicro != null && courseDataClassForMicro != null &&
                            (userCourseDataForMicro.getPresentageComplete() >= 95)
                            && courseDataClassForMicro.getMappedAssessmentId() > 0) {
                        courseModulePresenter.clickOnAssessmentIcon();
                    } else {
                        isResumeClicked = true;
                        branding_mani_layout.setVisibility(View.VISIBLE);
                        courseModulePresenter.loadUserDataForRegularMode((int) courseId, courseDataClass, courseColId);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showDialogForDownload() {
        try {
            showDialogDownload = new Dialog(CourseLearningMapActivity.this, R.style.DialogTheme);
            showDialogDownload.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showDialogDownload.setContentView(R.layout.common_pop_up);
            showDialogDownload.setCancelable(false);
            Objects.requireNonNull(showDialogDownload.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            showDialogDownload.show();

            ImageView info_image = showDialogDownload.findViewById(R.id.info_image);
            TextView info_title = showDialogDownload.findViewById(R.id.info_title);
            TextView info_description = showDialogDownload.findViewById(R.id.info_description);
            LinearLayout info_cancel = showDialogDownload.findViewById(R.id.info_no);
            TextView info_cancel_text = showDialogDownload.findViewById(R.id.info_no_text);
            LinearLayout save_userData = showDialogDownload.findViewById(R.id.info_yes);
            TextView info_save_text = showDialogDownload.findViewById(R.id.info_yes_text);

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
            String infoDescription = getResources().getString(R.string.retry_internet_msg);

            String downloadContent = "This course contains " + noOfVideos + " videos. Are you okay to download the course with videos?";
            if (noOfVideos == 1) {
                downloadContent = "This course contains " + noOfVideos + " video. Are you okay to download the course with video?";
            }
            info_title.setText(downloadContent);
            info_description.setText(infoDescription);
            info_description.setVisibility(View.GONE);
            info_image.setImageDrawable(infoDrawable);

            Drawable reviewDrawable = getResources().getDrawable(R.drawable.white_btn_bg);
            info_cancel.setBackground(reviewDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            save_userData.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_cancel_text.setTextColor(OustResourceUtils.getColors());

            info_cancel_text.setText(getResources().getString(R.string.stream).toUpperCase());
            info_save_text.setText(getResources().getString(R.string.download).toUpperCase());

            info_cancel.setOnClickListener(view -> {
                showDialogDownload.dismiss();
                enableVideoDownload = false;
                courseDataClass.setEnableVideoDownload(false);
                startCourseDownload();
            });

            save_userData.setOnClickListener(view -> {
                showDialogDownload.dismiss();
                downloadVideoLayout.setVisibility(View.VISIBLE);
                course_delete.setVisibility(View.GONE);
                course_download.setVisibility(View.GONE);
                downloadVideoPercentage.setText("0");
                enableVideoDownload = true;
                courseDataClass.setEnableVideoDownload(true);
                dialogOpened = true;
                try {
                    RoomHelper.addDownloadedOrNot((int) courseId, false, 0);
                    OustSdkTools.setDownloadGifImage(downloadGifImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                downloadCourseClicked = false;
                courseAlreadyDownloaded = false;
                startCourseDownload();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconColors() {
        try {

            Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
            learning_action_button.setBackground(OustResourceUtils.setDefaultDrawableColor(courseActionDrawable));

            if (Build.VERSION.SDK_INT >= 24) {
                ColorStateList trackStates = new ColorStateList(
                        new int[][]{
                                new int[]{color},
                                new int[]{ContextCompat.getColor(context, R.color.unselected_text)}
                        },
                        new int[]{
                                color,
                                ContextCompat.getColor(context, R.color.unselected_text)
                        }
                );
                mode_switch.setTrackTintList(trackStates);
            } else {
                ColorStateList thumbStates = new ColorStateList(
                        new int[][]{
                                new int[]{color},
                                new int[]{getResources().getColor(R.color.unselected_text)}
                        },
                        new int[]{
                                color,
                                getResources().getColor(R.color.unselected_text)
                        }
                );
                mode_switch.setThumbTintList(thumbStates);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.course_menu, menu);

        MenuItem itemBadge = menu.findItem(R.id.action_badge);
        Drawable badgeDrawable = getResources().getDrawable(R.drawable.badge_icon);
        itemBadge.setIcon(OustResourceUtils.setDefaultDrawableColor(badgeDrawable, color));
        itemBadge.setVisible(isBadgeEnable());

        MenuItem itemCertificate = menu.findItem(R.id.action_certificate);
        Drawable certificateDrawable = getResources().getDrawable(R.drawable.certificate_icon);
        itemCertificate.setIcon(OustResourceUtils.setDefaultDrawableColor(certificateDrawable, color));
//        itemCertificate.setVisible(isCertificateEnable());
        itemCertificate.setVisible(false);

        MenuItem itemLeaderBoard = menu.findItem(R.id.action_leaderBoard);
        Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
        itemLeaderBoard.setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color));
        itemLeaderBoard.setVisible(isLeaderBoardEnable());

        MenuItem itemComment = menu.findItem(R.id.action_comment);
        Drawable commentDrawable = getResources().getDrawable(R.drawable.ic_chat_comment);
        itemComment.setIcon(OustResourceUtils.setDefaultDrawableColor(commentDrawable, color));
        itemComment.setVisible(isCourseCommentEnable());

        MenuItem itemClose = menu.findItem(R.id.action_close);
        Drawable closeDrawable = getResources().getDrawable(R.drawable.ic_close_circle);
        itemClose.setIcon(OustResourceUtils.setDefaultDrawableColor(closeDrawable, color));
        itemClose.setVisible(true);
        return true;
    }

    private boolean isLeaderBoardEnable() {
        return isLeaderBoardEnable;
    }

    private boolean isCourseCommentEnable() {
        return isCourseCommentEnable;
    }

    private boolean isCertificateEnable() {
        return isCertificateEnable;
    }

    private boolean isBadgeEnable() {
        return isBadgeEnable;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_leaderBoard) {
            courseModulePresenter.clickOnLeaderBoard();
        } else if (itemId == R.id.action_comment) {
            Intent commentIntent = new Intent(CourseLearningMapActivity.this, BulletinBoardQuestionActivity.class);
            if (courseId != 0) {
                commentIntent.putExtra("courseId", "" + courseId);
            }
            if (courseDataClass != null && courseDataClass.getCourseName() != null) {
                commentIntent.putExtra("courseName", courseDataClass.getCourseName());
            }
            OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
            startActivity(commentIntent);

        } else if (itemId == R.id.action_close) {
            onBackPressed();
        } else if (itemId == R.id.action_certificate) {
            sendCertificatePopup(courseDataClass, false);
        } else if (itemId == R.id.action_badge) {
            sendBadgePopUp(courseDataClass);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendBadgePopUp(CourseDataClass courseDataClass) {
        try {
            Intent badgeIntent = new Intent(this, CourseCompletionWithBadgeActivity.class);
            badgeIntent.putExtra("courseId", courseDataClass.getCourseId());
            badgeIntent.putExtra("badgeName", courseDataClass.getBadgeName());
            badgeIntent.putExtra("badgeIcon", courseDataClass.getBadgeIcon());
            badgeIntent.putExtra("isMicroCourse", false);
            badgeIntent.putExtra("isComingFromCourseLearningMapPage", true);
            startActivity(badgeIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        setReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OustPreferences.saveAppInstallVariable("CLICK", false);
        try {
            course_delete.setVisibility(View.GONE);
            course_download.setVisibility(View.VISIBLE);
            isLearningCardOpen = false;
            showFinalAnimation = false;
            disableBackButton = false;
            if (courseDataClass == null) {
                courseDataClass = courseModulePresenter != null ? courseModulePresenter.getCourseDataClass() : null;
            }
            if (!OustSdkTools.checkInternetStatus() || isMicroCourse) {
                if (courseDataClass != null && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                    if (OustStaticVariableHandling.getInstance().isComingFromAssessment() && courseModulePresenter != null) {
                        OustStaticVariableHandling.getInstance().setComingFromAssessment(false);
                        isAssessmentCompleted = false;
                        isAssessmentError = OustStaticVariableHandling.getInstance().isAssessmentError();
                        OustStaticVariableHandling.getInstance().setAssessmentError(false);

                        if (OustStaticVariableHandling.getInstance().isAssessmentCompleted()) {
                            setCurrentModuleCompleteLevel(95);
                            isAssessmentCompleted = true;

                            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                            long courseUniqueNo = Long.parseLong(s1);

                            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                            userCourseData.setMappedAssessmentCompleted(true);
                            userCourseData.setPresentageComplete(95);
                            userCourseData.setCourseComplete(false);
                            isCourseAlreadyComplete = false;
                            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                        }

                        if (isSurveyAttached && surveyId != 0 && !isSurveyOpened) {
                            if (isAssessmentCompleted) {
                                checkForSavedAssessment(activeUser);
                            }
                        }

                        courseModulePresenter.startLearningMap(false);
                        if (isAssessmentCompleted && isMicroCourse) {
                            endActivity();
                        } else if (isAssessmentError && isMicroCourse) {
                            endActivity();
                        }
                    } else if (OustStaticVariableHandling.getInstance().isComingFromSurvey() && courseModulePresenter != null) {
                        OustStaticVariableHandling.getInstance().setComingFromSurvey(false);
                        try {
                            String node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
                            ValueEventListener surveyProgressListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        final Map<String, Object> assessmentProgressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                        String currentState = AssessmentState.STARTED;
                                        try {
                                            if (assessmentProgressMainMap != null && assessmentProgressMainMap.get("assessmentState") != null) {
                                                currentState = ((String) assessmentProgressMainMap.get("assessmentState"));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                        }

                                        isSurveyOpened = false;
                                        if ((currentState != null) && (currentState.equals(AssessmentState.SUBMITTED))) {
                                            Log.d(TAG, "onDataChange: survey completed");
                                            setCurrentModuleCompleteLevel(100);
                                            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                                            long courseUniqueNo = Long.parseLong(s1);

                                            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                                            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                                            userCourseData.setPresentageComplete(100);
                                            userCourseData.setCourseComplete(true);
//                                        showFinalAnimation = true;
                                            isCourseAlreadyComplete = true;
                                            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }

                                    if (courseModulePresenter != null) {
                                        courseModulePresenter.updateReviewModeStatus();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            };
                            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(surveyProgressListener);
                            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                } else if (OustStaticVariableHandling.getInstance().isComingFromAssessment() && courseModulePresenter != null) {
                    OustStaticVariableHandling.getInstance().setComingFromAssessment(false);
                    isAssessmentCompleted = false;
                    isAssessmentError = OustStaticVariableHandling.getInstance().isAssessmentError();
                    OustStaticVariableHandling.getInstance().setAssessmentError(false);

                    if (OustStaticVariableHandling.getInstance().isAssessmentCompleted()) {
                        setCurrentModuleCompleteLevel(100);
                        isAssessmentCompleted = true;

                        String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                        long courseUniqueNo = Long.parseLong(s1);

                        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                        DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                        userCourseData.setMappedAssessmentCompleted(true);
                        userCourseData.setPresentageComplete(100);
                        userCourseData.setCourseComplete(true);
                        isCourseAlreadyComplete = true;
                        userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    }

                    if (OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                        UserEventCourseData userEventCourseData = new UserEventCourseData();
                        userEventCourseData.setEvenId(eventId);
                        userEventCourseData.setCourseId(courseId);
                        if (isAssessmentCompleted) {
                            userEventCourseData.setUserProgress("COMPLETED");
                            try {
                                userEventCourseData.setCompletionDate("" + System.currentTimeMillis());
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            userEventCourseData.setUserCompletionPercentage(100);
                        } else {
                            userEventCourseData.setUserProgress("INPROGRESS");
                            userEventCourseData.setUserCompletionPercentage(95);
                        }

                        if (isSurveyAttached && surveyId != 0 && !isSurveyOpened) {
                            if (userEventCourseData.getUserCompletionPercentage() == 100 && !courseDataClass.isSurveyMandatory()) {
                                checkForSavedAssessment(activeUser);
                            }
                        } else {
                            if (isComingFromCpl) {
                                UserEventCplData userEventCplData = new UserEventCplData();
                                userEventCplData.setEventId(eventId);
                                userEventCplData.setCurrentModuleId(courseId);
                                userEventCplData.setCurrentModuleType("Course");
                                userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));

                                final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                                final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");

                                userEventCplData.setnTotalModules(totalModules);
                                if (isAssessmentCompleted) {
                                    userEventCplData.setCurrentModuleProgress("Completed");
                                    if (!isCourseAlreadyComplete) {
                                        userEventCplData.setnModulesCompleted(completedModules + 1);
                                    } else {
                                        userEventCplData.setnModulesCompleted(completedModules);
                                    }
                                    if (completedModules >= totalModules) {
                                        userEventCplData.setnModulesCompleted(totalModules);
                                        userEventCplData.setUserProgress("COMPLETED");
                                    } else {
                                        userEventCplData.setUserProgress("INPROGRESS");
                                    }
                                } else {
                                    userEventCplData.setCurrentModuleProgress("INPROGRESS");
                                    userEventCplData.setnModulesCompleted(completedModules);
                                    userEventCplData.setUserProgress("INPROGRESS");
                                }
                                userEventCplData.setUserEventCourseData(userEventCourseData);
                                OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                            } else {
                                OustStaticVariableHandling.getInstance().getOustApiListener().onCourseProgress(userEventCourseData);
                            }
                        }

                    } else {
                        if (isSurveyAttached && surveyId != 0 && !isSurveyOpened) {
                            if (isAssessmentCompleted) {
                                checkForSavedAssessment(activeUser);
                            }
                        } else if (isComingFromCpl && isAssessmentCompleted) {
                            finish();
                        }
                    }

                    if (courseModulePresenter != null) {
                        courseModulePresenter.updateReviewModeStatus();
                    }
                    if (isAssessmentCompleted && isMicroCourse) {
                        endActivity();
                    } else if (isAssessmentError && isMicroCourse) {
                        endActivity();
                    }
                } else if (OustStaticVariableHandling.getInstance().isComingFromSurvey()) {
                    OustStaticVariableHandling.getInstance().setComingFromSurvey(false);
                    if (isComingFromCpl) {
                        finish();
                    }
                } else if (isMicroCourse) {
                    if (courseModulePresenter != null) {
                        courseModulePresenter.getLearningMap(courseId);
                    }
                }
            } else {
                if (courseModulePresenter != null) {
                    branding_mani_layout.setVisibility(VISIBLE);
                    courseModulePresenter.getLearningMap(courseId);
                }
            }
            courseDownloading();

            try {
                if (comingFromIntroCard) {
                    course_intro_card_layout.setClickable(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (isAdaptiveCourse) {
                if (OustPreferences.getAppInstallVariable("IS_LEVEL_PLAYED")) {
                    courseModulePresenter.startLearningMap(false);
                    OustPreferences.saveAppInstallVariable("IS_LEVEL_PLAYED", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: courseLearning Map Activity-->");
        try {
            if (!disableBackButton || isCourseCompleted) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    protected void onDestroy() {
        try {
            showstored_Popup();
            isActiveLive = false;
            OustAppState.getInstance().setLearningCallBackInterface(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }

    private void setReceiver() {
        try {
            myFileDownLoadReceiver = new MyFileDownLoadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_COMPLETE);
            intentFilter.addAction(ACTION_ERROR);
            intentFilter.addAction(ACTION_PROGRESS);
            this.registerReceiver(myFileDownLoadReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            courseDownloadReceiver = new CourseDownloadReceiver();
            IntentFilter courseDownloadFilter = new IntentFilter();
            courseDownloadFilter.addAction(CourseDownloadReceiver.PROCESS_RESPONSE);
            courseDownloadFilter.addCategory(Intent.CATEGORY_DEFAULT);
            this.registerReceiver(courseDownloadReceiver, courseDownloadFilter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            IntentFilter downloadResponse = new IntentFilter(DownloadReceiver.DOWNLOAD_RESPONSE);
            downloadResponse.addCategory(Intent.CATEGORY_DEFAULT);
            downloadReceiver = new DownloadReceiver();
            this.registerReceiver(downloadReceiver, downloadResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            IntentFilter filter = new IntentFilter("android.intent.action.ATTACH_DATA");
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            myDownloadReceiver = new MyDownloadReceiver();
            this.registerReceiver(myDownloadReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showMessage(String errorMessage) {

    }

    @Override
    public void gotoNextScreen() {

    }

    @Override
    public void gotoPreviousScreen() {

    }

    @Override
    public void changeOrientationPortrait() {

    }

    @Override
    public void changeOrientationLandscape() {

    }

    @Override
    public void changeOrientationUnSpecific() {

    }

    @Override
    public void setFontAndText() {

    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setDownloadCourseIcon(DTOUserCourseData userCourseData) {

        try {
            downloaded = true;
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            if ((userCourseData != null) && (userCourseData.getUserLevelDataList() != null) && (userCourseData.getUserLevelDataList().size() > 0)) {
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getCompletePercentage() < 100) {
                        downloaded = false;
                        break;
                    }
                }
            } else {
                downloaded = false;
            }

            if (downloadAnimation != null) {
                downloadAnimation.stop();
                downloadAnimation.selectDrawable(0);
            }

            if (downloaded) {
                mUserLevelDataList = new ArrayList<>();
                mUserCourseData = new DTOUserCourseData();
                mUserCourseData = userCourseData;
                mUserLevelDataList = userCourseData.getUserLevelDataList();
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(1);
                    course_download.setVisibility(View.GONE);
                    course_delete.setVisibility(VISIBLE);
                }
                enableCourseDataDelete();
                userCourseData.setDownloadCompletePercentage(100);
                String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                long courseUniqueNo = Long.parseLong(s1);

                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                courseAlreadyDownloaded = true;
            } else {
                disableCourseDataDelete();
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setReviewModeStatus(boolean isSalesMode) {
        try {
            //handle review mode and play mode

            if (OustPreferences.getAppInstallVariable("disableCourseReviewMode") || (courseDataClass != null && courseDataClass.isDisableReviewMode())) {
                return;
            }

            if (mode_switch_layout.getVisibility() == View.GONE) {

                if (!isSalesMode) {
                    if (!isRegularMode) {
                        mode_switch_layout.setVisibility(View.VISIBLE);
                    }
                    mode_switch.setChecked(isReviewMode);
                    if (mode_switch.isChecked()) {
                        review_mode_text.setTextColor(color);
                        play_mode_text.setTextColor(getResources().getColor(R.color.unselected_text));
                        isReviewMode = true;
                        showCourseReviewLayout();
                        courseModulePresenter.setReviewModeStatus(true);
                    } else {
                        review_mode_text.setTextColor(getResources().getColor(R.color.unselected_text));
                        play_mode_text.setTextColor(color);
                        non_play_mode.setVisibility(View.GONE);
                        course_bg_image.setVisibility(View.VISIBLE);
                        play_mode.setVisibility(View.VISIBLE);
                        isReviewMode = false;
                        courseModulePresenter.setReviewModeStatus(false);
                    }

                    mode_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {

                        if (isChecked) {
                            isReviewMode = true;
                            courseModulePresenter.setReviewModeStatus(true);
                            showCourseReviewLayout();
                        } else {
                            non_play_mode.setVisibility(View.GONE);
                            course_bg_image.setVisibility(View.VISIBLE);
                            play_mode.setVisibility(View.VISIBLE);
                            isReviewMode = false;
                            courseModulePresenter.setReviewModeStatus(false);
                        }
                    });

                    if (!isPlayReviewMode && isReviewMode) {
                        Log.d(TAG, "Review mode fix");
                        isPlayReviewMode = true;
                        mode_switch.setChecked(false);
                        mode_switch.setChecked(true);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    @Override
    public void showLeaderBoard() {
        isLeaderBoardEnable = true;
        invalidateOptionsMenu();
    }

    @Override
    public void showBulletinBoard() {
        isCourseCommentEnable = true;
        invalidateOptionsMenu();
    }

    @Override
    public void showCourseReviewLayout() {
        try {
            //handle show review mode ,sales mode and regular mode layout
            play_mode.setVisibility(View.GONE);
            non_play_mode.setVisibility(View.VISIBLE);
            //Remove Background Image
            course_bg_image.setVisibility(View.GONE);
            if (nonPlayModeAdapter != null) {
                nonPlayModeAdapter.setReviewMode(isReviewMode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateReviewList() {
        Log.d(TAG, "updateReviewList: ");
        if (nonPlayModeAdapter != null) {
            Log.d(TAG, "updateReviewList: nonPlayModeAdapter found");
            nonPlayModeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setText() {
        //handle localization by course data language

    }

    @Override
    public void startCourseDownload() {

        try {
            Log.e(TAG, "startCourseDownload: courseAlreadyDownloaded-> " + courseAlreadyDownloaded + " downloadCourseClicked-> " + downloadCourseClicked);
            if ((!courseAlreadyDownloaded && !downloadCourseClicked)) {
                if (downloadAnimation != null && !downloadAnimation.isRunning()) {
                    downloadAnimation.start();
                }
                courseModulePresenter.clickOnCourseDownload();
                downloadCourseClicked = true;
                courseAlreadyDownloaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setCurrentModuleCompleteLevel(int percentage) {
        try {
            if (percentage >= 100) {
                percentage = 100;
                learning_action_button.setVisibility(View.GONE);
            }

            String completionPercentage = percentage + "%";
            user_completed_percentage.setText(completionPercentage);
            user_course_progressbar.setProgress(percentage);

            OustPreferences.saveintVar("taskCompletion", percentage);

            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(percentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }
                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(percentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }
            }
            if (isRegularMode) {
                DTOUserCourseData dtoUserCourseData = courseModulePresenter.getDTOUserCourseData();
                if (dtoUserCourseData.getPresentageComplete() == 100 || ((dtoUserCourseData.getPresentageComplete() == 90 || dtoUserCourseData.getPresentageComplete() == 95) && dtoUserCourseData.isCourseCompleted())) {
                    if (dtoUserCourseData.getMyCourseRating() == 0) {
                        if (!isCourseAlreadyComplete && !restartLevelStatus) {
                            if (!isRateCourseShownOnce) {
                                isRateCourseShownOnce = true;
                                if (OustSdkTools.checkInternetStatus()) {
                                    courseCompletePopup(courseDataClass);
                                } else {
                                    showOfflineSubmitPopUp();
                                }
                            }
                            setCertificateVisibility(dtoUserCourseData, courseDataClass);
                        }
                    }
                }
            } else {
                if (isMicroCourse && isMicroCoursePlay) {
                    endActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showOfflineSubmitPopUp() {
        try {
            showOfflineSubmitPopUp = new Dialog(CourseLearningMapActivity.this, R.style.DialogTheme);
            showOfflineSubmitPopUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showOfflineSubmitPopUp.setContentView(R.layout.common_pop_up);
            showOfflineSubmitPopUp.setCancelable(false);
            Objects.requireNonNull(showOfflineSubmitPopUp.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            showOfflineSubmitPopUp.show();

            ImageView info_image = showOfflineSubmitPopUp.findViewById(R.id.info_image);
            TextView info_title = showOfflineSubmitPopUp.findViewById(R.id.info_title);
            TextView info_description = showOfflineSubmitPopUp.findViewById(R.id.info_description);
            LinearLayout info_cancel = showOfflineSubmitPopUp.findViewById(R.id.info_no);
            LinearLayout save_userData = showOfflineSubmitPopUp.findViewById(R.id.info_yes);
            TextView info_save_text = showOfflineSubmitPopUp.findViewById(R.id.info_yes_text);

            String infoDescription = getResources().getString(R.string.offline_completion_popUp);
            info_save_text.setText(getResources().getString(R.string.ok).toUpperCase());

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);

            info_description.setText(infoDescription);
            info_title.setVisibility(View.GONE);
            info_description.setVisibility(View.VISIBLE);
            info_image.setImageDrawable(infoDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            save_userData.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_cancel.setVisibility(View.GONE);
            info_save_text.setText(getResources().getString(R.string.ok).toUpperCase());

            save_userData.setOnClickListener(view -> {
                if (showOfflineSubmitPopUp != null && showOfflineSubmitPopUp.isShowing()) {
                    showOfflineSubmitPopUp.dismiss();
                    showOfflineSubmitPopUp = null;
                }
                courseCompletePopup(courseDataClass);
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setLpOcText(long myTotalOc, long totalOc) {
        try {
            String userOcString;
            if (myTotalOc > totalOc) {
                userOcString = totalOc + "/" + totalOc;
            } else {
                userOcString = myTotalOc + "/" + totalOc;
            }

            if (userOcString.contains("/")) {
                String[] spilt = userOcString.split("/");
                Spannable yourCoinSpan = new SpannableString(userOcString);
                yourCoinSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, spilt[0].length(), 0);
                user_coins_earned.setText(yourCoinSpan);
            } else {
                user_coins_earned.setText(userOcString);
            }

            if (myTotalOc == 0 && totalOc == 0) {
                user_coins_earned.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void endActivity() {
        CourseLearningMapActivity.this.finish();
    }

    @Override
    public void gotoLeaderBoard(int currentLearningPathId, String lpName, String bgimg) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            Intent leaderBoardIntent = new Intent(CourseLearningMapActivity.this, CommonLeaderBoardActivity.class);
            leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle containerBundle = new Bundle();
            containerBundle.putString("containerType", "courseLeaderBoard");
            containerBundle.putLong("containerContentId", courseDataClass.getCourseId());
            if ((courseDataClass.getBgImg() != null) && (!courseDataClass.getBgImg().isEmpty())) {
                containerBundle.putString("contentBgImage", courseDataClass.getBgImg());
            }
            if (courseDataClass.getCourseName() != null) {
                containerBundle.putString("contentName", courseDataClass.getCourseName());
            }
            leaderBoardIntent.putExtras(containerBundle);
            startActivity(leaderBoardIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoBulletinBoard(int currentLearningPathId, String lpName) {

    }

    @Override
    public void sendUnlockPathApi(int currentLearningPathId) {
        try {
            branding_mani_layout.setVisibility(View.VISIBLE);
//            branding_mani_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            courseModulePresenter.enrolledLp(currentLearningPathId, OustAppState.getInstance().getActiveUser().getStudentid(), courseColId, multiCourseId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void downloadCourse(CourseDataClass courseDataClass) {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
            long courseUniqueNo = Long.parseLong(s1);
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
            if (userCourseData.getDownloadCompletePercentage() < 100) {
                userCourseData.setDownloading(true);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                startCourseDownloadService(courseDataClass);
                if (downloadAnimation != null && !downloadAnimation.isRunning()) {
                    downloadAnimation.start();
                }
            } else {
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(1);
                    course_download.setVisibility(View.GONE);
                    course_delete.setVisibility(VISIBLE);
                }
                enableCourseDataDelete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showPopup(Popup popup) {

    }

    @Override
    public void hideLoader() {
        //layout_progressbar.setVisibility(View.GONE);
        try {
            if (!isMicroCourse) {
                branding_mani_layout.setVisibility(View.GONE);
            } else {
                branding_mani_layout.setVisibility(View.GONE);
                if (!isCourseCompletionPopupShown) {
                    if (isEnrolled || courseModulePresenter.getEnrolled()) {
                        DTOUserCourseData userCourseDataForMicro = courseModulePresenter.getUserCourseData();
                        CourseDataClass courseDataClassForMicro = courseModulePresenter.getCourseDataClass();
                        if (userCourseDataForMicro != null && courseDataClassForMicro != null &&
                                userCourseDataForMicro.getPresentageComplete() == 90 && courseDataClassForMicro.isSurveyMandatory() && userCourseDataForMicro.isCourseCompleted() && !userCourseDataForMicro.isMappedAssessmentCompleted()
                                && !isMicroCoursePlay) {
                            courseModulePresenter.clickOnAssessmentIcon();
                        } else if (userCourseDataForMicro != null && courseDataClassForMicro != null &&
                                userCourseDataForMicro.getPresentageComplete() >= 95 && courseDataClassForMicro.getMappedAssessmentId() > 0 && userCourseDataForMicro.isCourseCompleted() && !userCourseDataForMicro.isMappedAssessmentCompleted() && !isMicroCoursePlay) {
                            courseModulePresenter.clickOnAssessmentIcon();
                        } else if (userCourseDataForMicro != null && courseDataClassForMicro != null &&
                                userCourseDataForMicro.getPresentageComplete() >= 95 && courseDataClassForMicro.getMappedSurveyId() > 0 && courseDataClassForMicro.isSurveyMandatory() &&
                                !isMicroCoursePlay) {
                            courseModulePresenter.clickOnSurveyIcon();
                        } else {
                            courseModulePresenter.clickOnLevelIcon(0);
                        }

                    } else {
                        courseModulePresenter.enrolledLp(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(),
                                OustAppState.getInstance().getActiveUser().getStudentid(), courseColId, multiCourseId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateLevelDownloadStatus(int noOfLevels, CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            for (int i = 0; i < noOfLevels; i++) {
                if (!((i == (noOfLevels - 1)) && (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0))) {

                    if ((dtoUserCourseData != null) &&
                            (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > i) &&
                            (dtoUserCourseData.getUserLevelDataList().get(i) != null) &&
                            ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() > 0)
                                    || (dtoUserCourseData.getUserLevelDataList().get(i).isDownloading()))) {

                        if ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100)) {
                            dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                            RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                        } else {
                            if ((!dtoUserCourseData.getUserLevelDataList().get(i).isDownloading())) {
                                if (downloadAnimation != null) {
                                    downloadAnimation.stop();
                                    downloadAnimation.selectDrawable(0);
                                }
                            } else {
                                if (downloadAnimation != null && !downloadAnimation.isRunning()) {
                                    downloadAnimation.start();
                                }
                            }
                        }
                    } else {
                        if (downloadAnimation != null) {
                            downloadAnimation.stop();
                            downloadAnimation.selectDrawable(0);
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
    public void setBackLayer(String imagePath) {
        try {
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                Glide.with(CourseLearningMapActivity.this).load(imagePath).diskCacheStrategy(DiskCacheStrategy.DATA).into(course_bg_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setAllLevelList(List<SearchCourseLevel> levelList) {
        filteredSearchCourseLevelList = new ArrayList<>();
        this.searchCourseLevelList = levelList;
    }

    @Override
    public void createLevelList(List<SearchCourseLevel> levelList, boolean isSearchMode) {
        try {
            if ((filteredSearchCourseLevelList != null) && (filteredSearchCourseLevelList.size() > 0)) {
                levelList = filteredSearchCourseLevelList;
            }
            try {
                entityDownloadedOrNot = RoomHelper.getDownloadedOrNotS();
                if (mUserLevelDataList != null) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        if (RoomHelper.checkMapTableExist((int) mUserLevelDataList.get(i).getLevelId())) {
                            normalCourseDownloaded = true;
                            course_delete.setVisibility(View.VISIBLE);
                            Log.d("couresisdownloaded", "check 111");
                            course_download.setVisibility(View.GONE);
                            if (downloadAnimation != null) {
                                downloadAnimation.stop();
                                downloadAnimation.selectDrawable(1);
                                course_download.setVisibility(View.GONE);
                                course_delete.setVisibility(VISIBLE);
                            }
                            course_download.setEnabled(false);
                            break;
                        }
                    }
                } else if (entityDownloadedOrNot != null && entityDownloadedOrNot.size() > 0 && entityDownloadedOrNot.contains(courseId)) {
                    for (int i = 0; i < entityDownloadedOrNot.size(); i++) {
                        if (entityDownloadedOrNot.get(i).getCourseId() == courseId && entityDownloadedOrNot.get(i).getPercentage() == 100) {
                            course_delete.setVisibility(View.VISIBLE);
                            normalCourseDownloaded = true;
                            course_download.setVisibility(View.GONE);
                            downloadVideoLayout.setVisibility(View.GONE);
                            Log.d(TAG, "doenloaded FINE<--<-- " + course_title.getText().toString());
                        }
                    }
                } else {
                    Log.d(TAG, "NO DATA-> downloding starting" + course_title.getText().toString());
                    startCourseDownloadService(courseDataClass);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (levelList.size() > 0) {
                non_play_mode.setVisibility(View.VISIBLE);
                course_bg_image.setVisibility(View.GONE);
                //handle adapter for review mode
                AssessmentNavModel assessmentNavModel = null;
                CourseDataClass courseDataClass = courseModulePresenter.getCourseDataClass();
                if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                    assessmentNavModel = new AssessmentNavModel();
                    assessmentNavModel.setCurrentLearningPathId(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
                    assessmentNavModel.setCourseColnId(courseColId);
                    assessmentNavModel.setCertificate(courseDataClass.isCertificate());
                    assessmentNavModel.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                    assessmentNavModel.setMappedAssessmentName(courseDataClass.getMappedAssessmentName());
                    assessmentNavModel.setMappedAssessmentImage(courseDataClass.getMappedAssessmentImage());
                    assessmentNavModel.setMappedAssessmentPercentage(courseDataClass.getMappedAssessmentPercentage());
                }

                SurveyNavModel surveyNavModel = null;
                if (courseDataClass != null && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                    surveyNavModel = new SurveyNavModel();
                    surveyNavModel.setCurrentLearningPathId(Long.parseLong("" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId()));
                    surveyNavModel.setCourseColnId(courseColId);
                    surveyNavModel.setCertificate(courseDataClass.isCertificate());
                    surveyNavModel.setMappedSurveyId(courseDataClass.getMappedSurveyId());
                    surveyNavModel.setMappedSurveyName(courseDataClass.getMappedSurveyName());
                    surveyNavModel.setMappedSurveyImage(courseDataClass.getMappedSurveyIcon());
                    surveyNavModel.setMappedSurveyPercentage(courseModulePresenter.getUserCourseData().getDownloadCompletePercentage());
                }

                if (nonPlayModeAdapter == null) {
                    Log.d(TAG, "createLevelList: ");
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(CourseLearningMapActivity.this, LinearLayoutManager.VERTICAL, false);
                    level_rv.setLayoutManager(mLayoutManager);
                    nonPlayModeAdapter = new NonPlayModeAdapter();
                    nonPlayModeAdapter.setNonPlayModeAdapter(CourseLearningMapActivity.this, levelList, isSearchMode, assessmentNavModel, surveyNavModel, courseModulePresenter.getUserCourseData(), isSalesMode);
                    nonPlayModeAdapter.setReviewModeCallBack(CourseLearningMapActivity.this);
                    level_rv.setAdapter(nonPlayModeAdapter);
                } else {
                    nonPlayModeAdapter.notifyDateChanges(levelList, isSearchMode, assessmentNavModel, surveyNavModel, courseModulePresenter.getUserCourseData(), isSalesMode);
                }
            } else {
                non_play_mode.setVisibility(View.GONE);
            }
            if (!isMicroCourse) {
                hideLoader();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setCertificateVisibility(DTOUserCourseData userCourseData, CourseDataClass courseDataClass) {
        try {
            if ((userCourseData.isCourseComplete() || (userCourseData.getPresentageComplete() == 100))
                    || (userCourseData.getPresentageComplete() == 95 || userCourseData.isCourseCompleted())) {

                if (courseDataClass.isCertificate()) {
                    //handle certificate icon
                    isCertificateEnable = true;
                    invalidateOptionsMenu();
                } else if (courseDataClass.isEnableBadge()) {
                    isBadgeEnable = true;
                    invalidateOptionsMenu();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void setLpName(String lpName) {
        try {
            course_title.setText(lpName);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    @Override
    public void addLevelIcons(int scrWidth, int scrHeight, int scrollviewHeight, int noOfLevels, int lastLevelNo, CourseDataClass courseDataClass,
                              DTOUserCourseData dtoUserCourseData) {
        try {
            play_mode.setVisibility(View.VISIBLE);
            course_bg_image.setVisibility(View.VISIBLE);
            non_play_mode.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "InflateParams"})
    @Override
    public void addLevelDescription(int scrWidth, int scrHeight, int scrollviewHeight, int noOfLevels, int lastLevelNo, int currentLevelNo, CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            pathY = 120;
            for (int i = 0; i < noOfLevels; i++) {
                View descView;
                this.userCourseData = dtoUserCourseData;
                boolean isCurrentLevel = false;
                if (dtoUserCourseData.getPresentageComplete() > 99 && dtoUserCourseData.isCourseComplete()) {
                    // do nothing
                    Log.d(TAG, "do nothing");
                } else {
                    if (((i + 1) == lastLevelNo) || ((((dtoUserCourseData.getPresentageComplete() > 99) && (dtoUserCourseData.getCurrentCompleteLevel() == (i + 1)))))) {
                        if (!((i == (noOfLevels - 2)) && (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) && courseDataClass.getMappedAssessmentId() > 0) && !dtoUserCourseData.isCourseCompleted()) {
                            isCurrentLevel = true;
                        } else if ((i == (noOfLevels - 1)) && (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0)) {
                            isCurrentLevel = true;
                        } else if ((i == (noOfLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                            isCurrentLevel = true;
                        }
                    }
                }

                if ((levelDescViews != null) && (levelDescViews.size() > i) && (levelDescViews.get(i) != null)) {
                    descView = levelDescViews.get(i);
                    LinearLayout level_card = descView.findViewById(R.id.level_card);
                    FrameLayout level_layout = descView.findViewById(R.id.level_layout);
                    CircleImageView level_bg = descView.findViewById(R.id.level_bg);
                    CircleImageView level_action_bg = descView.findViewById(R.id.level_action_bg);
                    ImageView level_action = descView.findViewById(R.id.level_action);
                    TextView level_name = descView.findViewById(R.id.level_name);
                    TextView level_no = descView.findViewById(R.id.level_no);

                    Drawable levelActionDrawable;
                    Drawable levelBgTintDrawable = getResources().getDrawable(R.drawable.level_action_background);
                    Drawable levelTintDrawable = getResources().getDrawable(R.drawable.level_action_background);

                    if (i == (noOfLevels - 2) && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedAssessmentId() > 0) {
                        if ((courseDataClass.getMappedAssessmentName() != null)) {
                            level_name.setText(courseDataClass.getMappedAssessmentName());
                        }
                        level_no.setText(getResources().getString(R.string.assessment));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if ((dtoUserCourseData.getPresentageComplete() >= 90 && dtoUserCourseData.getPresentageComplete() < 100) &&
                                dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if ((dtoUserCourseData.getPresentageComplete() >= 95 && dtoUserCourseData.getPresentageComplete() < 100) &&
                                dtoUserCourseData.isCourseCompleted() && dtoUserCourseData.isMappedAssessmentCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.assessment))) {
                                openLevelByClick("A", true);
                            }
                        });

                    } else if ((i == (noOfLevels - 1)) && (courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory())) {
                        if ((courseDataClass.getMappedSurveyName() != null)) {
                            level_name.setText(courseDataClass.getMappedSurveyName());
                        }
                        level_no.setText(getResources().getString(R.string.survey_text));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if ((dtoUserCourseData.getPresentageComplete() == 95) && dtoUserCourseData.isCourseCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);

                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.survey_text))) {
                                openLevelByClick("S", true);
                            }
                        });

                    } else if ((i == (noOfLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                        if ((courseDataClass.getMappedAssessmentName() != null)) {
                            level_name.setText(courseDataClass.getMappedAssessmentName());
                        }
                        level_no.setText(getResources().getString(R.string.assessment));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if (dtoUserCourseData.getPresentageComplete() == 95 &&
                                dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.assessment))) {
                                openLevelByClick("A", true);
                            }
                        });

                    } else {
                        boolean isLevelLocked = false;
                        if ((courseDataClass.getCourseLevelClassList().get(i).getLevelName() != null)) {
                            level_name.setText(courseDataClass.getCourseLevelClassList().get(i).getLevelName());
                        }
                        String levelNo = getResources().getString(R.string.level) + " " + (i + 1);
                        if (noOfLevels == 1) {
                            level_no.setVisibility(View.INVISIBLE);
                        } else {
                            level_no.setVisibility(VISIBLE);
                        }
                        level_no.setText(levelNo);

                        if (courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_lock_img), getResources().getColor(R.color.unselected_text));
                            isLevelLocked = true;
                            if (levelUnlockColor != null) {
                                level_layout.setBackgroundColor(Color.parseColor(levelUnlockColor));
                            } else {
                                level_layout.setBackgroundColor(getResources().getColor(R.color.unselected_text));
                            }
                        }

                        if (lastLevelNo < (i + 1)) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_lock_img), getResources().getColor(R.color.unselected_text));
                            if (levelUnlockColor != null) {
                                level_layout.setBackgroundColor(Color.parseColor(levelUnlockColor));
                            } else {
                                level_layout.setBackgroundColor(getResources().getColor(R.color.unselected_text));
                            }

                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.level_bg_lock_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.level_bg_lock_tint)));
                        } else {
                            if (isCurrentLevel) {
                                levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                                level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                                level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                                level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            } else {
                                if (levelLockedColor != null) {
                                    level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                                } else {
                                    level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                                }
                                levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                                level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                                level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            }
                        }
                        level_action.setImageDrawable(levelActionDrawable);
                        boolean finalIsLevelLocked = isLevelLocked;

                        level_card.setOnClickListener(v -> {
                            if (finalIsLevelLocked) {
                                OustSdkTools.showToast(getResources().getString(R.string.level_locked_by_author));
                            } else {
                                String levelText = level_no.getText().toString().toLowerCase();
                                if (!levelText.isEmpty()) {
                                    String levelString = getResources().getString(R.string.level) + " ";
                                    levelText = levelText.replace(levelString.toLowerCase(), "");
                                }
                                openLevelByClick(levelText, true);
                            }
                        });
                    }

                    if (isAdaptiveCourse) {
                        boolean isComplete = OustPreferences.getAppInstallVariable(courseDataClass.getCourseLevelClassList().get(i).getLevelId() + "");
                        if (isComplete) {
                            if (levelLockedColor != null) {
                                level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                            } else {
                                level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            }
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }
                    }
                } else {
                    descView = mInflater.inflate(R.layout.course_level_decriptioin_layout, null);
                    LinearLayout level_card = descView.findViewById(R.id.level_card);
                    FrameLayout level_layout = descView.findViewById(R.id.level_layout);
                    CircleImageView level_bg = descView.findViewById(R.id.level_bg);
                    CircleImageView level_action_bg = descView.findViewById(R.id.level_action_bg);
                    ImageView level_action = descView.findViewById(R.id.level_action);
                    TextView level_name = descView.findViewById(R.id.level_name);
                    TextView level_no = descView.findViewById(R.id.level_no);

                    Drawable levelActionDrawable;
                    Drawable levelBgTintDrawable = getResources().getDrawable(R.drawable.level_action_background);
                    Drawable levelTintDrawable = getResources().getDrawable(R.drawable.level_action_background);

                    if (i == (noOfLevels - 2) && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedAssessmentId() > 0) {
                        if ((courseDataClass.getMappedAssessmentName() != null)) {
                            level_name.setText(courseDataClass.getMappedAssessmentName());
                        }
                        level_no.setText(getResources().getString(R.string.assessment));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if ((dtoUserCourseData.getPresentageComplete() >= 90 && dtoUserCourseData.getPresentageComplete() < 100) &&
                                dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if ((dtoUserCourseData.getPresentageComplete() >= 95 && dtoUserCourseData.getPresentageComplete() < 100) &&
                                dtoUserCourseData.isCourseCompleted() && dtoUserCourseData.isMappedAssessmentCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.assessment))) {
                                openLevelByClick("A", true);
                            }
                        });
                    } else if ((i == (noOfLevels - 1)) && (courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory())) {
                        if ((courseDataClass.getMappedSurveyName() != null)) {
                            level_name.setText(courseDataClass.getMappedSurveyName());
                        }
                        level_no.setText(getResources().getString(R.string.survey_text));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.survey_text))) {
                                openLevelByClick("S", true);
                            }
                        });
                    } else if ((i == (noOfLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                        if ((courseDataClass.getMappedAssessmentName() != null)) {
                            level_name.setText(courseDataClass.getMappedAssessmentName());
                        }
                        level_no.setText(getResources().getString(R.string.assessment));
                        if (levelLockedColor != null) {
                            level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                        }

                        if (dtoUserCourseData.getPresentageComplete() == 95 &&
                                dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                            level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            level_action.setImageDrawable(levelActionDrawable);
                        } else if (dtoUserCourseData.getPresentageComplete() == 100 &&
                                dtoUserCourseData.isCourseCompleted()) {
                            level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            level_action.setImageDrawable(levelActionDrawable);
                        }

                        level_card.setOnClickListener(v -> {
                            String levelText = level_no.getText().toString().toLowerCase();
                            if (levelText.equalsIgnoreCase(getResources().getString(R.string.assessment))) {
                                openLevelByClick("A", true);
                            }
                        });
                    } else {
                        if ((courseDataClass.getCourseLevelClassList().get(i).getLevelName() != null)) {
                            level_name.setText(courseDataClass.getCourseLevelClassList().get(i).getLevelName());
                        }
                        String levelNo = getResources().getString(R.string.level) + " " + (i + 1);
                        if (noOfLevels == 1) {
                            level_no.setVisibility(View.INVISIBLE);
                        } else {
                            level_no.setVisibility(VISIBLE);
                        }
                        level_no.setText(levelNo);

                        boolean isLevelLocked = false;
                        if (courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                            isLevelLocked = true;
                            if (levelUnlockColor != null) {
                                level_layout.setBackgroundColor(Color.parseColor(levelUnlockColor));
                            } else {
                                level_layout.setBackgroundColor(getResources().getColor(R.color.unselected_text));
                            }
                        }
                        if (lastLevelNo < (i + 1)) {
                            levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_lock_img), getResources().getColor(R.color.unselected_text));
                            if (levelUnlockColor != null) {
                                level_layout.setBackgroundColor(Color.parseColor(levelUnlockColor));
                            } else {
                                level_layout.setBackgroundColor(getResources().getColor(R.color.unselected_text));
                            }
                            level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.level_bg_lock_bg)));
                            level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.level_bg_lock_tint)));

                        } else {
                            if (isCurrentLevel) {
                                levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_play_white), getResources().getColor(R.color.white));
                                level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.current_level_play_tint)));
                                level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.current_level_play_bg)));
                                level_layout.setBackgroundColor(getResources().getColor(R.color.current_level_bg));
                            } else {
                                if (levelLockedColor != null) {
                                    level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                                } else {
                                    level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                                }

                                levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                                level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                                level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                            }
                        }
                        level_action.setImageDrawable(levelActionDrawable);

                        if (isAdaptiveCourse) {
                            boolean isComplete = OustPreferences.getAppInstallVariable(courseDataClass.getCourseLevelClassList().get(i).getLevelId() + "");
                            if (isComplete) {
                                if (levelLockedColor != null) {
                                    level_layout.setBackgroundColor(Color.parseColor(levelLockedColor));
                                } else {
                                    level_layout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg_tint));
                                }
                                levelActionDrawable = OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.ic_level_completed), getResources().getColor(R.color.white));
                                level_action_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelBgTintDrawable, getResources().getColor(R.color.completed_level_play_bg)));
                                level_bg.setBackground(OustResourceUtils.setDefaultDrawableColor(levelTintDrawable, getResources().getColor(R.color.completed_level_play_tint)));
                                level_action.setImageDrawable(levelActionDrawable);
                            }
                        }
                        boolean finalIsLevelLocked = isLevelLocked;
                        level_card.setOnClickListener(v -> {
                            if (finalIsLevelLocked) {
                                OustSdkTools.showToast(getResources().getString(R.string.level_locked_by_author));
                            } else {
                                String levelText = level_no.getText().toString().toLowerCase();
                                if (!levelText.isEmpty()) {
                                    String levelString = getResources().getString(R.string.level) + " ";
                                    levelText = levelText.replace(levelString.toLowerCase(), "");
                                }
                                openLevelByClick(levelText, true);
                            }
                        });

                    }
                    descView.setX(getXPosition(70, scrWidth));
                    descView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                    setAnimation(descView, i);
                    levelDescViews.add(descView);
                    learning_dynamic_layout.addView(descView);
                }
                pathY += 70;
            }

            new Handler().postDelayed(() -> {
                if (endLevelView == null) {
                    endLevelView = mInflater.inflate(R.layout.course_finish_icon_layout, null);
                    LinearLayout level_completed_card = endLevelView.findViewById(R.id.level_completed_card);
                    FrameLayout frameLayout = endLevelView.findViewById(R.id.frameLayout);
                    TextView course_complete_text = endLevelView.findViewById(R.id.course_complete_text);
                    ImageView course_ending_icon = endLevelView.findViewById(R.id.course_ending_icon);
                    if (OustPreferences.get(COURSE_FINISH_ICON) != null) {
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + OustMediaTools.getMediaFileName(OustPreferences.get(COURSE_FINISH_ICON)));
                        if (file.exists()) {
                            Uri uri = Uri.fromFile(file);
                            course_ending_icon.setImageURI(uri);
                        }
                    }
                    if (isLocked) {
                        course_ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.course_finish_lock));
                    }
                    if (dtoUserCourseData != null && ((dtoUserCourseData.getPresentageComplete() == 100) ||
                            (dtoUserCourseData.getPresentageComplete() >= 99 && dtoUserCourseData.isCourseCompleted()))) {
                        level_completed_card.setBackgroundColor(getResources().getColor(R.color.transparent));
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg));
                        course_ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.completed));
                        TextViewCompat.setTextAppearance(course_complete_text, R.style.body_2_bold);
                    } else {
                        course_ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.completed_grey));
                        level_completed_card.setBackgroundColor(Color.parseColor("#eceded"));
                        frameLayout.setBackgroundColor(Color.parseColor("#c5c7c7"));
                        course_complete_text.setTextColor(Color.parseColor("#cccccc"));
                        course_complete_text.setTypeface(null, Typeface.NORMAL);
//                        TextViewCompat.setTextAppearance(course_complete_text, R.style.body_2);
                    }
                    endLevelView.setX(getXPosition(75, scrWidth));
                    endLevelView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                    endLevelView.setAlpha(0);
                    setAnimation(endLevelView, noOfLevels);
                    learning_dynamic_layout.addView(endLevelView);
                } else {
                    LinearLayout level_completed_card = endLevelView.findViewById(R.id.level_completed_card);
                    FrameLayout frameLayout = endLevelView.findViewById(R.id.frameLayout);
                    ImageView course_ending_icon = endLevelView.findViewById(R.id.course_ending_icon);
                    TextView course_complete_text = endLevelView.findViewById(R.id.course_complete_text);
                    if (dtoUserCourseData != null && ((dtoUserCourseData.getPresentageComplete() == 100) ||
                            (dtoUserCourseData.getPresentageComplete() >= 99 && dtoUserCourseData.isCourseCompleted()))) {
                        level_completed_card.setBackgroundColor(getResources().getColor(R.color.transparent));
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.completed_level_bg));
                        course_ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.completed));
                        TextViewCompat.setTextAppearance(course_complete_text, R.style.body_2_bold);
                    } else {
                        course_ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.completed_grey));
                        level_completed_card.setBackgroundColor(Color.parseColor("#eceded"));
                        frameLayout.setBackgroundColor(Color.parseColor("#c5c7c7"));
                        course_complete_text.setTextColor(Color.parseColor("#cccccc"));
                        course_complete_text.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }, 2500);

            if (dtoUserCourseData != null && ((dtoUserCourseData.getPresentageComplete() == 100) ||
                    ((dtoUserCourseData.getPresentageComplete() == 90 || dtoUserCourseData.getPresentageComplete() == 95) && dtoUserCourseData.isCourseCompleted()))) {
                if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                    if (dtoUserCourseData.getMyCourseRating() == 0) {
                        if ((showFinalAnimation) && (!isCourseAlreadyComplete) && (!restartLevelStatus)) {
                            if (!isRateCourseShownOnce) {
                                isRateCourseShownOnce = true;
                                isSoundPlayedOnce = false;
                                if (OustSdkTools.checkInternetStatus()) {
                                    courseCompletePopup(courseDataClass);
                                } else {
                                    showOfflineSubmitPopUp();
                                }
                            }
                            setCertificateVisibility(dtoUserCourseData, courseDataClass);
                        }
                    }
                } else {
                    if (dtoUserCourseData.getMyCourseRating() == 0) {
                        if (currentLevelNo == lastLevelNo) {
                            if ((showFinalAnimation) && (!isCourseAlreadyComplete) && (!restartLevelStatus)) {
                                if (!isRateCourseShownOnce) {
                                    isRateCourseShownOnce = true;
                                    isSoundPlayedOnce = false;
                                    if (OustSdkTools.checkInternetStatus()) {
                                        courseCompletePopup(courseDataClass);
                                    } else {
                                        showOfflineSubmitPopUp();
                                    }
                                }
                                setCertificateVisibility(dtoUserCourseData, courseDataClass);
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

    @SuppressLint("InflateParams")
    @Override
    public void setIndicatorPosition(int lastPlayingLevel, int currentLevelNo, int scrWidth, int scrHeight, int scrollViewHeight,
                                     DTOUserCourseData dtoUserCourseData, CourseDataClass courseDataClass) {
        try {
            Log.d(TAG, "setIndicatorPosition: ");
            if (levelIndicatorView != null) {
                if ((dtoUserCourseData.getPresentageComplete() > 99) && (currentLevelNo == lastPlayingLevel)) {
                    courseModulePresenter.setCourseComplete(true);
                    if (levelIndicatorView != null) {
                        if ((dtoUserCourseData.getCurrentCompleteLevel() == 0)) {
                            if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.getMappedAssessmentId() != 0) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else if (courseDataClass.getAssessmentCompletionDate() != null && !courseDataClass.getAssessmentCompletionDate().isEmpty()) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else if (dtoUserCourseData.isCourseCompleted() && dtoUserCourseData.isMappedAssessmentCompleted()) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else {
                                levelIndicatorView.setY(getYPosition((60 + (65 * currentLevelNo)), scrHeight, scrollViewHeight));
                            }
                        } else {
                            if (isCourseAlreadyComplete) {
                                if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.getMappedAssessmentId() != 0 && isAssessmentCompleted) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 3))), scrHeight, scrollViewHeight));

                                } else if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 2))), scrHeight, scrollViewHeight));

                                } else if (courseDataClass.getMappedAssessmentId() != 0 && isAssessmentCompleted) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 2))), scrHeight, scrollViewHeight));

                                } else {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (currentLevelNo))), scrHeight, scrollViewHeight));
                                }
                            } else {
                                levelIndicatorView.setY(getYPosition((50 + (70 * dtoUserCourseData.getCurrentLevel())), scrHeight, scrollViewHeight));
                            }
                        }
                    }
                } else if (dtoUserCourseData.getPresentageComplete() == 90 && courseDataClass.isSurveyMandatory() && dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));
                    }

                } else if (dtoUserCourseData.getPresentageComplete() == 95 && courseDataClass.isSurveyMandatory() && dtoUserCourseData.isCourseCompleted() && courseDataClass.getMappedAssessmentId() > 0) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 2))), scrHeight, scrollViewHeight));
                    }

                } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));
                    }
                } else {
                    courseModulePresenter.setCourseComplete(false);
                    if (dtoUserCourseData.getPresentageComplete() == 95 && courseDataClass != null && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0 &&
                            dtoUserCourseData.isCourseCompleted() && dtoUserCourseData.isMappedAssessmentCompleted()) {
                        levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));
                    } else {
                        if (levelIndicatorView != null) {
                            if (lastPlayingLevel == 0) {
                                levelIndicatorView.setY(getYPosition(60, scrHeight, scrollViewHeight));
                            } else {
                                if (lastPlayingLevel <= 4) {
                                    levelIndicatorView.setY(getYPosition((60 + (65 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                                } else {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                                }
                            }
                        }
                    }
                }
            } else {
                levelIndicatorView = mInflater.inflate(R.layout.course_indicator_view, null);
                ImageView course_indicator_icon = levelIndicatorView.findViewById(R.id.course_indicator_icon);
                if (OustPreferences.get(COURSE_LEVEL_INDICATOR_ICON) != null) {
                    File file = new File(getFilesDir(), "oustlearn_" + OustMediaTools.getMediaFileName(OustPreferences.get(COURSE_LEVEL_INDICATOR_ICON)));
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        Glide.with(this).load(uri).error(getResources().getDrawable(R.drawable.ping)).into(course_indicator_icon);
                    } else {
                        Uri uri = Uri.parse(OustPreferences.get(COURSE_LEVEL_INDICATOR_ICON));
                        Glide.with(this).load(uri).error(getResources().getDrawable(R.drawable.ping)).into(course_indicator_icon);
                    }
                } else {
                    Glide.with(this).load(getResources().getDrawable(R.drawable.ping)).into(course_indicator_icon);
                }

                float startX = (int) ((getResources().getDimension(R.dimen.oustlayout_dimen80)) / 2);
                levelIndicatorView.setX((getXPositionForIndicator(60, scrWidth)) - startX);
                if ((dtoUserCourseData.getPresentageComplete() > 99) && (currentLevelNo == lastPlayingLevel)) {
                    courseModulePresenter.setCourseComplete(true);
                    if (levelIndicatorView != null) {
                        if ((dtoUserCourseData.getCurrentCompleteLevel() == 0)) {
                            if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.getMappedAssessmentId() != 0) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 2))), scrHeight, scrollViewHeight));

                            } else if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else if (courseDataClass.getAssessmentCompletionDate() != null && !courseDataClass.getAssessmentCompletionDate().isEmpty()) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));

                            } else {
                                levelIndicatorView.setY(getYPosition((60 + (65 * currentLevelNo)), scrHeight, scrollViewHeight));
                            }
                        } else {
                            if (isCourseAlreadyComplete) {
                                if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.getMappedAssessmentId() != 0 && isAssessmentCompleted) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 3))), scrHeight, scrollViewHeight));

                                } else if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 2))), scrHeight, scrollViewHeight));

                                } else if (courseDataClass.getMappedAssessmentId() != 0 && isAssessmentCompleted) {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * (OustStaticVariableHandling.getInstance().getCourseLevelCount() + 2))), scrHeight, scrollViewHeight));

                                } else {
                                    levelIndicatorView.setY(getYPosition((50 + (70 * dtoUserCourseData.getCurrentLevel())), scrHeight, scrollViewHeight));
                                }
                            } else {
                                levelIndicatorView.setY(getYPosition((50 + (70 * dtoUserCourseData.getCurrentLevel())), scrHeight, scrollViewHeight));
                            }
                        }
                    }

                } else if (dtoUserCourseData.getPresentageComplete() == 90 && courseDataClass.isSurveyMandatory() && dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));
                    }

                } else if (dtoUserCourseData.getPresentageComplete() == 95 && courseDataClass.isSurveyMandatory() && dtoUserCourseData.isCourseCompleted() && courseDataClass.getMappedAssessmentId() > 0) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 2))), scrHeight, scrollViewHeight));
                    }

                } else if (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted() && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        levelIndicatorView.setY(getYPosition((60 + (69 * (currentLevelNo + 1))), scrHeight, scrollViewHeight));
                    }
                } else {
                    courseModulePresenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        if (lastPlayingLevel == 0) {
                            levelIndicatorView.setY(getYPosition(60, scrHeight, scrollViewHeight));
                        } else {
                            if (lastPlayingLevel <= 4) {
                                levelIndicatorView.setY(getYPosition((60 + (65 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                            } else {
                                levelIndicatorView.setY(getYPosition((50 + (70 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                            }
                        }
                    }
                }
                learning_dynamic_layout.addView(levelIndicatorView);
            }
            if (levelIndicatorView != null) {
                levelIndicatorView.bringToFront();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startLearningPath() {

        //handle level item size - if required need to analyze
        try {
            mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            levelBoxSize = (int) (getResources().getDimension(R.dimen.oustlayout_dimen60) / 2);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void checkMultiLingualCourse() {

        try {
            if (isMultiLingual && multiCourseId > 0 && !isAnyPopupVisible) {
                isMultiLingual = false;

            } else {
                if (isComingFromCpl && !isAnyPopupVisible) {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void setBackLine(int scrWidth, int scrHeight, int scrollViewHeight, int noOfLevels) {
        //handle straight line for old UI
        try {
            simple_line_view.setLayoutParams(new RelativeLayout.LayoutParams(scrWidth, scrollViewHeight));
            simple_line_view.setScreenWH(scrWidth, scrHeight, simple_line_view, noOfLevels);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void bringBackFront() {
        //handle to bring front of straight line
        try {
            simple_line_view.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @SuppressLint("InflateParams")
    @Override
    public void setLearningStartIcon(int scrWidth, int scrHeight, int scrollviewHeight) {
        try {
            //no need to handle start icon for new UI
            Log.i(TAG, "no need to handle start icon for new UI");
            /*view_start_icon = mInflater.inflate(R.layout.view_start_icon, null);
            int levelBoxSizeLocal = (int) (getResources().getDimension(R.dimen.oustlayout_dimen70) / 2);
            float x = (scrWidth * 60.0f / 320) - levelBoxSizeLocal;
            learning_start_icon = view_start_icon.findViewById(R.id.learning_start_icon);
            if (OustPreferences.get(COURSE_START_ICON) != null) {
                File file = new File(OustSdkApplication.getContext().getFilesDir(),
                        "oustlearn_" + OustMediaTools.getMediaFileName(OustPreferences.get(COURSE_START_ICON)));
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    learning_start_icon.setImageURI(uri);
                }
            }
            view_start_icon.setX(x);
            view_start_icon.setY(getYPosition(120, scrHeight, scrollviewHeight));
            learning_dynamic_layout.addView(view_start_icon);
            view_start_icon.bringToFront();
            view_start_icon.setOnClickListener(v -> {
                if (isPurchased) {
                    if (!isLocked) {
                        if (!OustSdkTools.checkInternetStatus()) {
                            return;
                        }
                        courseModulePresenter.clickOnEnrollLp(true);
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.complete_course_unlock));
                    }
                } else {
                    if (isFreeCourse) {
                        OustSdkTools.showToast(getResources().getString(R.string.enroll_course_start));
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.purchase_course));
                    }
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public void addOverLay(int scrWidth, int scrHeight, int scrollviewHeight) {
        //if required handle overlay

    }

    @Override
    public void setLearningStartLabbelIcon(int scrWidth, int scrHeight, int scrollviewHeight) {
        //not required for new UI but we need to think about nudge view

    }

    @Override
    public void setCurrentLevelPosition(int currentLevel, int scrHeight, int scrollViewHeight) {

        try {
            //handle smoothly to current position
            view_scroll.postDelayed(() -> view_scroll.fullScroll(View.FOCUS_DOWN), 100);
            view_scroll.postDelayed(() -> {
                if (currentLevel > 1) {
                    float n1 = scrollViewHeight - scrHeight - getRealYPosition(((currentLevel - 1) * 65), scrHeight);
                    view_scroll.smoothScrollTo(0, (int) n1);
                } else {
                    view_scroll.smoothScrollTo(0, view_scroll.getBottom() + 30);
                }
            }, 500);


            /* view_scroll.scrollTo(0, view_scroll.getBottom());*/
//            view_scroll.postDelayed(() -> view_scroll.fullScroll(View.FOCUS_DOWN), 200);
            courseCurrentLevel = currentLevel;

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onScrollPostMeth() {

    }

    @Override
    public void enableClick() {
        isLearningCardOpen = false;
    }

    public void showRetry() {
        try {
            popupWindowEnrollementRetry = new Dialog(CourseLearningMapActivity.this, R.style.DialogTheme);
            popupWindowEnrollementRetry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupWindowEnrollementRetry.setContentView(R.layout.common_pop_up);
            popupWindowEnrollementRetry.setCancelable(false);
            Objects.requireNonNull(popupWindowEnrollementRetry.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            popupWindowEnrollementRetry.show();
//            Objects.requireNonNull(popupWindowEnrollementRetry.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_SECURE);

            popupWindowEnrollementRetry.show();
            //CardView card_layout = uploadFailedDialog.findViewById(R.id.card_layout);
            ImageView info_image = popupWindowEnrollementRetry.findViewById(R.id.info_image);
            TextView info_title = popupWindowEnrollementRetry.findViewById(R.id.info_title);
            TextView info_description = popupWindowEnrollementRetry.findViewById(R.id.info_description);
            LinearLayout info_cancel = popupWindowEnrollementRetry.findViewById(R.id.info_no);
            TextView info_no_text = popupWindowEnrollementRetry.findViewById(R.id.info_no_text);
            LinearLayout info_reTry = popupWindowEnrollementRetry.findViewById(R.id.info_yes);
            TextView info_retry_text = popupWindowEnrollementRetry.findViewById(R.id.info_yes_text);

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
            String infoDescription = getResources().getString(R.string.retry_internet_msg);


            info_title.setText(getResources().getString(R.string.warning));
            info_description.setText(infoDescription);
            info_description.setVisibility(VISIBLE);
            info_image.setImageDrawable(infoDrawable);

            Drawable reviewDrawable = getResources().getDrawable(R.drawable.white_btn_bg);
            info_cancel.setBackground(reviewDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            info_reTry.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_no_text.setTextColor(OustResourceUtils.getColors());

            info_no_text.setText(getResources().getString(R.string.exit_txt));
            info_retry_text.setText(getResources().getString(R.string.try_again).toUpperCase());

            info_cancel.setOnClickListener(view -> {
                popupWindowEnrollementRetry.dismiss();
                endActivity();
            });

            info_reTry.setOnClickListener(view -> {
                popupWindowEnrollementRetry.dismiss();
                courseModulePresenter.enrolledLp(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(),
                        OustAppState.getInstance().getActiveUser().getStudentid(), courseColId, multiCourseId);
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            popupWindowEnrollementRetry.dismiss();
            endActivity();
        }
    }

    @Override
    public void gotoQuizPage(int currentLearningPathId, CourseDataClass courseDataClass, int levelNo, CourseLevelClass courseLevelClass) {
        try {
            if (!isActiveLive) {
                return;
            }
            if (isAdaptiveCourse) {
                Intent intent = new Intent(CourseLearningMapActivity.this, AdaptiveLearningMapModuleActivity.class);
                intent.putExtra("containCertificate", courseDataClass.isCertificate());
                intent.putExtra("learningId", currentLearningPathId);
                intent.putExtra("levelNo", levelNo);
                intent.putExtra("isReviewMode", isReviewMode);
                intent.putExtra("isComingFromCpl", isComingFromCpl);
                intent.putExtra("isDownloadVideo", enableVideoDownload);
                intent.putExtra("COURSE_COMPLETED", isCourseCompleted);
                if ((courseColId != null) && (!courseColId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColId);
                }
                startActivity(intent);
            } else {
                if ((activeUser == null)) {
                    String activeUserGet = OustPreferences.get("userdata");
                    activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                    OustAppState.getInstance().setActiveUser(activeUser);
                }
                List<EntityLevelCardCourseIDUpdateTime> entityLevelCardCourseIDUpdateTime;
                entityLevelCardCourseIDUpdateTime = RoomHelper.getLevelCourseCardUpdateTimeMap((int) courseLevelClass.getLevelId());
                if (entityLevelCardCourseIDUpdateTime.size() > 0 && courseLevelClass.getRefreshTimeStamp() != null && !courseLevelClass.getRefreshTimeStamp().isEmpty()) {
                    if (OustSdkTools.convertToLong(entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime()) > OustSdkTools.convertToLong(courseLevelClass.getRefreshTimeStamp())) {
                        courseLevelClass.setRefreshTimeStamp(String.valueOf(entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime()));
                    }
                }
                try {
                    CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                    HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                    eventUpdate.put("ClickedOnLevel", true);
                    eventUpdate.put("CourseID", courseId);
                    eventUpdate.put("Course Name", courseDataClass.getCourseName());
                    eventUpdate.put("LevelID", courseLevelClass.getLevelId());

                    Log.d(TAG, "CleverTap instance: " + eventUpdate);

                    if (clevertapDefaultInstance != null) {
                        clevertapDefaultInstance.pushEvent("Course_Level_Start", eventUpdate);
                    }
                    if (SystemClock.elapsedRealtime() - mLastTimeClickedRefresh < 3000) {
                        return;
                    }
                    mLastTimeClickedRefresh = SystemClock.elapsedRealtime();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                Intent intent = new Intent(CourseLearningMapActivity.this, CourseLearningModuleActivity.class);
                Gson gson = new Gson();
                String courseDataStr = gson.toJson(courseDataClass);
                String courseLevelStr = gson.toJson(courseLevelClass);
                OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
                intent.putExtra("containCertificate", courseDataClass.isCertificate());
                intent.putExtra("learningId", ("" + currentLearningPathId));
                intent.putExtra("levelNo", levelNo);

                if (isRegularMode) {
                    intent.putExtra("isRegularMode", true);
                    intent.putExtra("isReviewMode", false);
                } else {
                    if (isMicroCourse) {
                        isReviewMode = false;
                    }
                    if (!restartLevelStatus) {
                        intent.putExtra("isReviewMode", isReviewMode);
                    }
                }
                intent.putExtra("isSalesMode", isSalesMode);
                intent.putExtra("isComingFromCpl", isComingFromCpl);
                intent.putExtra("COURSE_COMPLETED", isCourseCompleted);
                intent.putExtra("isDisableLevelCompletePopup", isDisableLevelCompletePopup);
                intent.putExtra("isMicroCourse", isMicroCourse);
                intent.putExtra("isDownloadVideo", enableVideoDownload);
                intent.putExtra("updateLevelStatus", updateLevelStatus);
                intent.putExtra("enableLevelCompleteAudio", enableLevelCompleteAudio);

                try {
                    long deadline = 0;
                    if (courseDataClass.getCourseDeadline() != null) {
                        deadline = Long.parseLong(courseDataClass.getCourseDeadline());
                    }
                    if (deadline > 0) {
                        long currentTIme = System.currentTimeMillis();
                        long penalty = courseDataClass.getDefaultPastDeadlineCoinsPenaltyPercentage();

                        if (deadline < currentTIme && penalty > 0) {
                            intent.putExtra("isDeadlineCrossed", true);
                            intent.putExtra("penaltyPercentage", penalty);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if ((courseColId != null) && (!courseColId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColId);
                }
                OustSdkTools.newActivityAnimationB(intent, CourseLearningMapActivity.this);
                updateLevelStatus = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setLevelChangeAnim(int currentLevel, int scrWidth, int scrHeight, int scrollViewHeight, int totalNoOfLevels, CourseDataClass courseDataClass) {
        try {
            Log.d(TAG, "setLevelChangeAnim: ");
            OustAppState.getInstance().setHasPopup(true);
            if (!(isCourseAlreadyComplete && isAssessmentCompleted)) {
                //handle indicator animation and event
                levelIndicatorView.animate().translationYBy(-getRealYPosition(65, scrHeight)).setDuration(2000).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            isLearningCardOpen = false;
                            if (currentLevel <= totalNoOfLevels) {
                                if ((currentLevel == (totalNoOfLevels)) && (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0)) {
                                    showFinalAnimation = true;
                                    isSoundTobePlayed = true;
                                } else if ((currentLevel == (totalNoOfLevels)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                                    showFinalAnimation = true;
                                    isSoundTobePlayed = true;
                                } else if ((currentLevel == (totalNoOfLevels - 1)) && (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0)) {
                                    showFinalAnimation = true;
                                    isSoundTobePlayed = true;
                                } else {
                                    if (!isAdaptiveCourse && !isCourseAlreadyCompleteTemp) {
                                        if (courseDataClass.isAutoPlay()) {
                                            openLevelByClick("" + currentLevel, true);
                                        } else {
                                            openLevelByClick("" + currentLevel, false);
                                        }
                                    }
                                }
                            } else {
                                isSoundTobePlayed = true;
                                showFinalAnimation = true;
                            }
                            courseModulePresenter.setLastLevelNo();
                            courseModulePresenter.startLearningMap(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoQuizPageReviewMode(int currentLearningPathId, CourseDataClass courseDataClass, int levelNo, int reviewModeQuestionNo) {
        try {
            disableBackButton = false;
            CourseLevelClass courseLevelClass = new CourseLevelClass();
            if ((filteredSearchCourseLevelList != null) && (filteredSearchCourseLevelList.size() > 0)) {
                SearchCourseLevel searchCourseLevel = filteredSearchCourseLevelList.get(levelNo);
                courseLevelClass.setLevelId(searchCourseLevel.getId());
                courseLevelClass.setLevelName(searchCourseLevel.getName());
                courseLevelClass.setLevelDescription(searchCourseLevel.getDescription());
                courseLevelClass.setRefreshTimeStamp(searchCourseLevel.getRefreshTimeStamp());
                courseLevelClass.setSequence(searchCourseLevel.getSequence());
                courseLevelClass.setLpId(searchCourseLevel.getLpId());
                courseLevelClass.setTotalXp(searchCourseLevel.getXp());
                courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                if (courseDataClass.isSalesMode()) {
                    courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                }
                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                for (int i = 0; i < filteredSearchCourseLevelList.get(levelNo).getSearchCourseCards().size(); i++) {
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setCardId(filteredSearchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getId());
                    courseCardClass.setSequence(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getSequence());
                    courseCardClass.setCardType(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getCardType());
                    courseCardClass.setXp(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getXp());
                    courseCardClassList.add(courseCardClass);
                }
                courseLevelClass.setCourseCardClassList(courseCardClassList);
            } else {
                SearchCourseLevel searchCourseLevel = searchCourseLevelList.get(levelNo);
                courseLevelClass.setLevelId(searchCourseLevel.getId());
                courseLevelClass.setLevelName(searchCourseLevel.getName());
                courseLevelClass.setRefreshTimeStamp(searchCourseLevel.getRefreshTimeStamp());
                courseLevelClass.setLevelDescription(searchCourseLevel.getDescription());
                courseLevelClass.setSequence(searchCourseLevel.getSequence());
                courseLevelClass.setLpId(searchCourseLevel.getLpId());
                courseLevelClass.setTotalXp(searchCourseLevel.getXp());
                courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                if (courseDataClass.isSalesMode()) {
                    courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                }
                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                for (int i = 0; i < searchCourseLevelList.get(levelNo).getSearchCourseCards().size(); i++) {
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setCardId(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getId());
                    courseCardClass.setSequence(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getSequence());
                    courseCardClass.setCardType(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getCardType());
                    courseCardClass.setXp(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getXp());
                    courseCardClassList.add(courseCardClass);
                }
                courseLevelClass.setCourseCardClassList(courseCardClassList);
            }
            if ((activeUser == null)) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            if (levelNo == 0 || levelNo == 1) {
                String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                long courseUniqueNo = Long.parseLong(s1);
                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                if (userCourseData1.getDownloadCompletePercentage() < 100) {
                    startCourseDownloadService(courseDataClass);
                }
            }
            Intent intent = new Intent(CourseLearningMapActivity.this, CourseLearningModuleActivity.class);
            Gson gson = new Gson();
            String courseLevelStr = gson.toJson(courseLevelClass);
            String courseDataStr = gson.toJson(courseDataClass);
            OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
            OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
            intent.putExtra("containCertificate", courseDataClass.isCertificate());
            intent.putExtra("learningId", ("" + currentLearningPathId));
            intent.putExtra("levelNo", levelNo);
            intent.putExtra("reviewModeQuestionNo", reviewModeQuestionNo);
            if (isRegularMode) {
                intent.putExtra("isRegularMode", true);
                intent.putExtra("isReviewMode", false);
            } else {
                if (isMicroCourse) {
                    isReviewMode = false;
                }
                intent.putExtra("isReviewMode", isReviewMode);
            }
            intent.putExtra("isSalesMode", isSalesMode);
            intent.putExtra("COURSE_COMPLETED", isCourseCompleted);
            intent.putExtra("isDisableLevelCompletePopup", isDisableLevelCompletePopup);
            intent.putExtra("isDownloadVideo", enableVideoDownload);
            intent.putExtra("isMicroCourse", isMicroCourse);
            intent.putExtra("enableLevelCompleteAudio", enableLevelCompleteAudio);

            try {
                long deadline = 0;
                if (courseDataClass.getCourseDeadline() != null) {
                    deadline = Long.parseLong(courseDataClass.getCourseDeadline());
                }
                if (deadline > 0) {
                    long currentTIme = System.currentTimeMillis();
                    long penalty = courseDataClass.getDefaultPastDeadlineCoinsPenaltyPercentage();

                    if (deadline < currentTIme && penalty > 0) {
                        Log.d(TAG, "gotoQuizPage: deadline crossed penalty:" + penalty);
                        intent.putExtra("isDeadlineCrossed", true);
                        intent.putExtra("penaltyPercentage", penalty);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if ((courseColId != null) && (!courseColId.isEmpty())) {
                intent.putExtra("courseColnId", courseColId);
            }
            OustSdkTools.newActivityAnimationB(intent, CourseLearningMapActivity.this);

            if (isRegularMode && !isCourseCompleted) {
                learning_action_button.setVisibility(View.VISIBLE);
            } else {
                learning_action_button.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void gotoAssessmentPage(int currentLearningPathId, CourseDataClass courseDataClass) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            Intent intent = new Intent(OustSdkApplication.getContext(), AssessmentDetailScreen.class);
            intent.putExtra("courseId", ("" + currentLearningPathId));

            if ((courseColId != null) && (!courseColId.isEmpty())) {
                intent.putExtra("courseColnId", courseColId);
            }

            if (courseDataClass != null && courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId() > 0) {
                intent.putExtra("mappedSurveyId", "" + courseDataClass.getMappedSurveyId());
            }

            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (dtoUserCourseData.getPresentageComplete() > 99) {
                intent.putExtra("isFromWorkDairy", true);
            }
            intent.putExtra("IS_FROM_COURSE", true);
            intent.putExtra("isMicroCourse", isMicroCourse);
            ActiveGame activeGame = new ActiveGame();
            activeGame.setIsLpGame(false);
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            if (courseDataClass != null) {
                if (courseDataClass.getMappedAssessmentId() > 0) {
                    intent.putExtra("assessmentId", ("" + courseDataClass.getMappedAssessmentId()));
                }
                intent.putExtra("containCertificate", courseDataClass.isCertificate());
            }
            intent.putExtra("isSurveyFromCourse", isSurveyAttached);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startDownloadLevel(CourseLevelClass courseLevelClass, String courseUniqueNoStr) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void extractCourseData(int learningPathId, Map<String, Object> learningMap, Map<String, Object> landingMap) {
        try {
            noOfVideos = 0;
            OustPreferences.saveintVar("video_count", 0);
            courseDataClass = new CourseDataClass();
            if (learningMap != null) {
                branding_mani_layout.setVisibility(View.GONE);
                if (learningMap.get("courseId") != null) {
                    courseId = OustSdkTools.newConvertToLong(learningMap.get("courseId"));
                    courseDataClass.setCourseId(OustSdkTools.newConvertToLong(learningMap.get("courseId")));
                }
                if (learningMap.get("contentDuration") != null) {
                    courseDataClass.setContentDuration(OustSdkTools.newConvertToLong(learningMap.get("contentDuration")));
                }
                if (learningMap.get("showNudgeMessage") != null) {
                    courseDataClass.setShowNudgeMessage((boolean) learningMap.get("showNudgeMessage"));
                }
                if (learningMap.get("courseType") != null) {
                    if (learningMap.get("courseType").toString().equalsIgnoreCase("ADAPTIVE")) {
                        isAdaptiveCourse = true;
                        course_download.setVisibility(View.INVISIBLE);

                    } else {
                        if (!isSalesMode) {
                            course_download.setVisibility(View.VISIBLE);
                        }
                        isAdaptiveCourse = false;
                    }
                }
                if (learningMap.get(AppConstants.StringConstants.SURVEY_ATTACHED) != null) {
                    isSurveyAttached = (boolean) learningMap.get(AppConstants.StringConstants.SURVEY_ATTACHED);
                }

                if (learningMap.get(AppConstants.StringConstants.SURVEY_ID) != null) {
                    surveyId = OustSdkTools.newConvertToLong(learningMap.get(AppConstants.StringConstants.SURVEY_ID));
                    courseDataClass.setMappedSurveyId(OustSdkTools.newConvertToLong(learningMap.get(AppConstants.StringConstants.SURVEY_ID)));
                }

                if (learningMap.get("surveyMandatory") != null) {
                    courseDataClass.setSurveyMandatory((boolean) learningMap.get("surveyMandatory"));
                } else {
                    if (learningMap.get("startSurveyImmediately") != null) {
                        startSurveyImmediately = (boolean) learningMap.get("startSurveyImmediately");
                    }
                }

                courseModulePresenter.setAdaptive(isAdaptiveCourse);

                if (learningMap.get("autoDownload") != null) {
                    courseDataClass.setAutoDownload((boolean) (learningMap.get("autoDownload")));
                }

                if (learningMap.get("addedOn") != null) {
                    courseAddedOn = (String) learningMap.get("addedOn");
                }

                if (learningMap.get("enrolled") != null) {
                    isEnrolled = ((boolean) learningMap.get("enrolled"));
                    disableBackButton = !isEnrolled;
                } else {
                    disableBackButton = false;
                }

                if (learningMap.get("enableLevelCompleteAudio") != null) {
                    enableLevelCompleteAudio = (boolean) learningMap.get("enableLevelCompleteAudio");
                }

                if (learningMap.get("enableCourseCompleteAudio") != null) {
                    enableCourseCompleteAudio = (boolean) learningMap.get("enableCourseCompleteAudio");
                }

                if (!isMicroCourse) {
                    if (learningMap.get("showCourseCompletionPopup") != null) {
                        showCourseCompletionPopup = (boolean) (learningMap.get("showCourseCompletionPopup"));
                        courseDataClass.setShowCourseCompletionPopup(showCourseCompletionPopup);
                    }
                } else {
                    showCourseCompletionPopup = true;
                    courseDataClass.setShowCourseCompletionPopup(showCourseCompletionPopup);
                }

                if (!isMicroCourse) {
                    if (learningMap.get("autoPlay") != null) {
                        courseDataClass.setAutoPlay((boolean) (learningMap.get("autoPlay")));
                    }
                } else {
                    courseDataClass.setAutoPlay(false);
                }
                if (learningMap.get("courseName") != null) {
                    courseDataClass.setCourseName((String) learningMap.get("courseName"));
                }
                if (learningMap.get("updateTime") != null) {
                    courseDataClass.setUpdateTime((String) learningMap.get("updateTime"));
                }
                if (learningMap.get("badgeName") != null) {
                    Log.d(TAG, "extractCourseData: badgeName:" + learningMap.get("badgeName"));
                    courseDataClass.setBadgeName((String) learningMap.get("badgeName"));
                }

                if (learningMap.get("badgeIcon") != null) {
                    Log.d(TAG, "extractCourseData: badgeIcon:" + learningMap.get("badgeIcon"));
                    courseDataClass.setBadgeIcon((String) learningMap.get("badgeIcon"));
                }

                if (learningMap.get("updateTS") != null) {
                    courseDataClass.setUpdateTs((String) learningMap.get("updateTS"));
                }
                if (learningMap.get("bgImg") != null) {
                    courseDataClass.setBgImg((String) learningMap.get("bgImg"));
                }
                if (learningMap.get("disableCourseCompleteAudio") != null) {
                    courseDataClass.setDisableCourseCompleteAudio((Boolean) learningMap.get("disableCourseCompleteAudio"));
                }
                if (learningMap.get("showVirtualCurrency") != null) {
                    courseDataClass.setShowVirtualCoins((boolean) (learningMap.get("showVirtualCurrency")));
                } else {
                    courseDataClass.setShowVirtualCoins(true);
                }
                if (learningMap.get("showRatingPopUp") != null) {
                    courseDataClass.setShowRatingPopUp((boolean) (learningMap.get("showRatingPopUp")));
                    isShowRatingPopUp = courseDataClass.isShowRatingPopUp();
                }
                if (learningMap.get("courseLandScapeMode") != null) {
                    courseDataClass.setCourseLandScapeMode((boolean) (learningMap.get("courseLandScapeMode")));
                }
                if (learningMap.get("mappedAssessmentId") != null) {
                    courseDataClass.setMappedAssessmentId(OustSdkTools.newConvertToLong(learningMap.get("mappedAssessmentId")));
                    assessmentId = OustSdkTools.newConvertToLong(learningMap.get("mappedAssessmentId"));
                }
                if (learningMap.get("salesMode") != null) {
                    courseDataClass.setSalesMode((boolean) learningMap.get("salesMode"));
                    isSalesMode = (boolean) learningMap.get("salesMode");
                    if (isSalesMode) {
                        learning_action_button.setVisibility(View.GONE);
                    }
                }
                if (learningMap.get("numEnrolledUsers") != null) {
                    courseDataClass.setNumEnrolledUsers(OustSdkTools.newConvertToLong(learningMap.get("numEnrolledUsers") + ""));
                }
                if (courseDataClass.getNumEnrolledUsers() == 0 && learningMap.get("enrolledCount") != null) {
                    courseDataClass.setNumEnrolledUsers(OustSdkTools.newConvertToLong(learningMap.get("enrolledCount") + ""));
                }

                enableVideoDownload = false;
                try {
                    if (learningMap.get("enableVideoDownload") != null) {
                        enableVideoDownload = (boolean) learningMap.get("enableVideoDownload");
                        courseDataClass.setEnableVideoDownload(enableVideoDownload);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if (OustPreferences.getAppInstallVariable("enableVideoDownload") || courseDataClass.isEnableVideoDownload()) {
                    enableVideoDownload = true;
                    courseDataClass.setEnableVideoDownload(true);
                }

                if (learningMap.get("regularMode") != null) {
                    isRegularMode = (boolean) learningMap.get("regularMode");
                } else {
                    isRegularMode = false;
                }

                courseDataClass.setRegularMode(isRegularMode);
                courseDataClass.setSalesMode(isSalesMode);
                courseModulePresenter.setRegularMode(isRegularMode);
                courseModulePresenter.setSalesMode(isSalesMode);

                if (!isMicroCourse) {
                    if (learningMap.get("disableLevelCompletePopup") != null) {
                        courseDataClass.setDisableLevelCompletePopup((boolean) learningMap.get("disableLevelCompletePopup"));
                    } else {
                        courseDataClass.setDisableLevelCompletePopup(false);
                    }
                } else {
                    isRegularMode = false;
                    courseDataClass.setRegularMode(false);
                    courseDataClass.setDisableLevelCompletePopup(true);
                    courseModulePresenter.setRegularMode(false);
                }

                isDisableLevelCompletePopup = courseDataClass.isDisableLevelCompletePopup();

                if (learningMap.get("language") != null) {
                    courseDataClass.setLanguage((String) learningMap.get("language"));
                }
                if (learningMap.get("disableReviewMode") != null) {
                    courseDataClass.setDisableReviewMode((boolean) learningMap.get("disableReviewMode"));
                }
                if (learningMap.get("cplId") != null) {
                    long cplId = OustSdkTools.newConvertToLong(learningMap.get("cplId"));
                    OustPreferences.saveTimeForNotification("cplId_course", cplId);
                } else {
                    OustPreferences.saveTimeForNotification("cplId_course", 0);
                }

                if (learningMap.get("mappedAssessmentDetails") != null) {
                    Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learningMap.get("mappedAssessmentDetails");
                    if (mappedAssessmentMap != null) {
                        if (mappedAssessmentMap.get("name") != null) {
                            courseDataClass.setMappedAssessmentName((String) mappedAssessmentMap.get("name"));
                        }
                        if (mappedAssessmentMap.get("id") != null) {
                            courseDataClass.setMappedAssessmentId(OustSdkTools.newConvertToLong(mappedAssessmentMap.get("id")));
                        }
                        if (mappedAssessmentMap.get("enrolled") != null) {
                            courseDataClass.setMappedAssessmentEnrolled((boolean) mappedAssessmentMap.get("enrolled"));
                        }
                        if (mappedAssessmentMap.get("icon") != null) {
                            courseDataClass.setMappedAssessmentImage((String) mappedAssessmentMap.get("icon"));
                        }
                        if (mappedAssessmentMap.get("nQuestionAnswered") != null) {
                            userEventAssessmentData.setnQuestionAnswered((int) OustSdkTools.newConvertToLong(mappedAssessmentMap.get("nQuestionAnswered")));
                        }
                        if (mappedAssessmentMap.get("nQuestionCorrect") != null) {
                            userEventAssessmentData.setnQuestionCorrect((int) OustSdkTools.newConvertToLong(mappedAssessmentMap.get("nQuestionCorrect")));
                        }
                        if (mappedAssessmentMap.get("nQuestionSkipped") != null) {
                            userEventAssessmentData.setnQuestionSkipped((int) OustSdkTools.newConvertToLong(mappedAssessmentMap.get("nQuestionSkipped")));
                        }
                        if (mappedAssessmentMap.get("nQuestionWrong") != null) {
                            userEventAssessmentData.setnQuestionWrong((int) OustSdkTools.newConvertToLong(mappedAssessmentMap.get("nQuestionWrong")));
                        }
                    }

                }

                if (learningMap.get("mappedSurveyDetails") != null) {
                    Map<String, Object> mappedSurveyDetails = (Map<String, Object>) learningMap.get("mappedSurveyDetails");
                    if (mappedSurveyDetails != null) {
                        if (mappedSurveyDetails.get("name") != null) {
                            courseDataClass.setMappedSurveyName((String) mappedSurveyDetails.get("name"));
                        }
                        /*if (mappedSurveyDetails.get("id") != null) {
                            courseDataClass.setMappedSurveyId(OustSdkTools.convertToLong(mappedSurveyDetails.get("id")));
                        }*/
                        if (mappedSurveyDetails.get("icon") != null) {
                            courseDataClass.setMappedSurveyIcon((String) mappedSurveyDetails.get("icon"));
                        }
                        if (mappedSurveyDetails.get("description") != null) {
                            courseDataClass.setMappedSurveyDescription((String) mappedSurveyDetails.get("description"));
                        }
                    }

                }

                if (learningMap.get("zeroXpForLCard") != null) {
                    courseDataClass.setZeroXpForLCard((boolean) learningMap.get("zeroXpForLCard"));
                }
                if (learningMap.get("zeroXpForQCard") != null) {
                    courseDataClass.setZeroXpForQCard((boolean) learningMap.get("zeroXpForQCard"));
                }
                if (learningMap.get("hideLeaderboard") != null) {
                    courseDataClass.setHideLeaderBoard((boolean) learningMap.get("hideLeaderboard"));
                } else {
                    courseDataClass.setHideLeaderBoard(false);
                }
                if (learningMap.get("showQuesInReviewMode") != null) {
                    courseDataClass.setShowQuesInReviewMode((boolean) learningMap.get("showQuesInReviewMode"));
                }
                if (learningMap.get("hideBulletinBoard") != null) {
                    courseDataClass.setHideBulletinBoard((boolean) learningMap.get("hideBulletinBoard"));
                } else {
                    courseDataClass.setHideBulletinBoard(false);
                }
                if (learningMap.get("fullScreenPotraitModeVideo") != null) {
                    courseDataClass.setFullScreenPotraitModeVideo(((boolean) learningMap.get("fullScreenPotraitModeVideo")));
                }
                if (learningMap.get("descriptionCard") != null) {
                    cardInfo = (Map<String, Object>) learningMap.get("descriptionCard");
                }
                if (cardInfo != null) {
                    courseDataClass.setCardInfo(cardInfo);
                    course_intro_card_layout.setVisibility(View.VISIBLE);
                } else {
                    course_intro_card_layout.setVisibility(View.GONE);
                }
                if (learningMap.get("ackPopup") != null) {
                    Map<Object, Object> disClaimerMap = (Map<Object, Object>) learningMap.get("ackPopup");
                    courseDesclaimerData = new CourseDesclaimerData();
                    if (disClaimerMap != null) {
                        if (disClaimerMap.get("body") != null) {
                            courseDesclaimerData.setContent((String) disClaimerMap.get("body"));
                        }
                        if (disClaimerMap.get("buttonLabel") != null) {
                            courseDesclaimerData.setBtnText((String) disClaimerMap.get("buttonLabel"));
                        }
                        if (disClaimerMap.get("checkBoxText") != null) {
                            courseDesclaimerData.setCheckBoxText((String) disClaimerMap.get("checkBoxText"));
                        }
                        if (disClaimerMap.get("header") != null) {
                            courseDesclaimerData.setHeader((String) disClaimerMap.get("header"));
                        }
                    }
                    courseDataClass.setCourseDesclaimerData(courseDesclaimerData);
                }

                if (learningMap.get("tips") != null) {
                    List<String> hintList = new ArrayList<>();
                    Map<String, String> hintMap;
                    Object o1 = learningMap.get("tips");
                    if (Objects.requireNonNull(o1).getClass().equals(HashMap.class)) {
                        hintMap = (Map<String, String>) o1;
                        for (String key : hintMap.keySet()) {
                            hintList.add(hintMap.get(key));
                        }
                    } else if (o1.getClass().equals(ArrayList.class)) {
                        hintList = (ArrayList<String>) o1;
                    }
                    OustAppState.getInstance().setHintMessages(hintList);
                }

                if (learningMap.get("certificate") != null) {
                    courseDataClass.setCertificate((boolean) learningMap.get("certificate"));
                }
                if (learningMap.get("enableBadge") != null) {
                    courseDataClass.setEnableBadge((boolean) learningMap.get("enableBadge"));
                }
                if (learningMap.get("disableScreenShot") != null) {
                    courseDataClass.setDisableScreenShot((boolean) learningMap.get("disableScreenShot"));
                }
                if (learningMap.get("lpBgImageNew") != null) {
                    courseDataClass.setLpBgImage((String) learningMap.get("lpBgImageNew"));
                }
                if (learningMap.get("isTTSEnabledQC") != null) {
                    courseDataClass.setQuesttsEnabled((boolean) learningMap.get("isTTSEnabledQC"));
                }
                if (learningMap.get("isTTSEnabledLC") != null) {
                    courseDataClass.setCardttsEnabled((boolean) learningMap.get("isTTSEnabledLC"));
                }
                if (learningMap.get("icon") != null) {
                    courseDataClass.setIcon((String) learningMap.get("icon"));
                }
                if (learningMap.get("startFromLastLevel") != null) {
                    courseDataClass.setStartFromLastLevel((boolean) learningMap.get("startFromLastLevel"));
                }
                if (learningMap.get("completionDeadline") != null) {
                    courseDataClass.setCourseDeadline((String) learningMap.get("completionDeadline"));
                }
                if (learningMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                    courseDataClass.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.newConvertToLong(learningMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
                }
                if (learningMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                    courseDataClass.setShowPastDeadlineModulesOnLandingPage((boolean) learningMap.get("showPastDeadlineModulesOnLandingPage"));
                }
                if (learningMap.get("showQuestionSymbolForQuestion") != null) {
                    courseDataClass.setShowQuestionSymbolForQuestion((boolean) learningMap.get("showQuestionSymbolForQuestion"));
                }
                if (learningMap.get("totalOc") != null) {
                    courseDataClass.setTotalOc(OustSdkTools.newConvertToLong(learningMap.get("totalOc")));
                }
                if (learningMap.get("removeDataAfterCourseCompletion") != null) {
                    Object o4 = learningMap.get("removeDataAfterCourseCompletion");
                    if (Objects.requireNonNull(o4).getClass().equals(String.class)) {
                        courseDataClass.setRemoveDataAfterCourseCompletion(Boolean.parseBoolean((String) learningMap.get("removeDataAfterCourseCompletion")));
                    } else {
                        courseDataClass.setRemoveDataAfterCourseCompletion((boolean) learningMap.get("removeDataAfterCourseCompletion"));
                    }
                }
                Object o1 = learningMap.get("levels");
                if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                    List<Object> levelsList = (List<Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (int i = 0; i < levelsList.size(); i++) {
                            if (levelsList.get(i) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLpId(learningPathId);
                                if (levelMap.get("levelId") != null) {
                                    courseLevelClass.setLevelId(OustSdkTools.newConvertToLong(levelMap.get("levelId")));
                                }
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    if (Objects.requireNonNull(o2).getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) levelMap.get("cards");
                                        if (cardMap != null) {
                                            for (String key : cardMap.keySet()) {
                                                if (cardMap.get(key) != null) {
                                                    final Map<String, Object> cardSubMap = (Map<String, Object>) cardMap.get(key);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardSubMap != null) {
                                                        if (cardSubMap.get("cardBgColor") != null) {
                                                            courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                        }
                                                        if (cardSubMap.get("cardQuestionColor") != null) {
                                                            courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                        }
                                                        if (cardSubMap.get("questionCategory") != null) {
                                                            courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                        }
                                                        if (cardSubMap.get("questionType") != null) {
                                                            courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                        }
                                                        if (cardSubMap.get("cardSolutionColor") != null) {
                                                            courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                        }
                                                        if (cardSubMap.get("cardTextColor") != null) {
                                                            courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                        }
                                                        String cardType;
                                                        if (cardSubMap.get("cardType") != null) {
                                                            cardType = (String) cardSubMap.get("cardType");
                                                            courseCardClass.setCardType(cardType);
                                                        }
                                                        if (cardSubMap.get("content") != null) {
                                                            courseCardClass.setContent((String) cardSubMap.get("content"));
                                                        }
                                                        if (cardSubMap.get("cardTitle") != null) {
                                                            courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                        }
                                                        if (cardSubMap.get("qId") != null) {
                                                            courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                        }
                                                        if (cardSubMap.get("cardId") != null) {
                                                            courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                        }
                                                        if (cardSubMap.get("isIfScormEventBased") != null) {
                                                            courseCardClass.setIfScormEventBased((boolean) cardSubMap.get("isIfScormEventBased"));
                                                        } else {
                                                            courseCardClass.setIfScormEventBased(false);
                                                        }
                                                        if (cardSubMap.get("xp") != null) {
                                                            courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                        }
                                                        if (cardSubMap.get("sequence") != null) {
                                                            courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                        }
                                                        Map<String, Object> cardColorScheme = (Map<String, Object>) cardSubMap.get("cardColorScheme");
                                                        if (cardColorScheme != null) {
                                                            DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                            if (cardColorScheme.get("contentColor") != null) {
                                                                cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                            }
                                                            if (cardColorScheme.get("bgImage") != null) {
                                                                cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                            }
                                                            if (cardColorScheme.get("iconColor") != null) {
                                                                cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                            }
                                                            if (cardColorScheme.get("levelNameColor") != null) {
                                                                cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                            }
                                                            courseCardClass.setCardColorScheme(cardColorScheme1);
                                                        }
                                                        courseCardClassList.add(courseCardClass);
                                                        try {
                                                            Object oo2 = cardSubMap.get("cardMedia");
                                                            if (oo2 != null) {
                                                                if (oo2.getClass().equals(ArrayList.class)) {
                                                                    List<Object> cardList = (List<Object>) cardSubMap.get("cardMedia");
                                                                    if (cardList != null) {
                                                                        for (int l = 0; l < cardList.size(); l++) {
                                                                            if (cardList.get(l) != null) {
                                                                                if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                                    noOfVideos++;
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
                                                }
                                            }
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardSubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardSubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                    }
                                                    if (cardSubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardSubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardSubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                    }
                                                    if (cardSubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                    }
                                                    if (cardSubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                    }
                                                    String cardType;
                                                    if (cardSubMap.get("cardType") != null) {
                                                        cardType = (String) cardSubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardSubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardSubMap.get("content"));
                                                    }
                                                    if (cardSubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                    }
                                                    if (cardSubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                    }
                                                    if (cardSubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                    }
                                                    if (cardSubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                    }
                                                    if (cardSubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                    }
                                                    Map<String, Object> cardColorScheme = (Map<String, Object>) cardSubMap.get("cardColorScheme");
                                                    if (cardColorScheme != null) {
                                                        DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                        if (cardColorScheme.get("contentColor") != null) {
                                                            cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                        }
                                                        if (cardColorScheme.get("bgImage") != null) {
                                                            cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                        }
                                                        if (cardColorScheme.get("iconColor") != null) {
                                                            cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                        }
                                                        if (cardColorScheme.get("levelNameColor") != null) {
                                                            cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                        }
                                                        courseCardClass.setCardColorScheme(cardColorScheme1);
                                                    }
                                                    courseCardClassList.add(courseCardClass);
                                                    try {
                                                        Object oo2 = cardSubMap.get("cardMedia");
                                                        if (oo2 != null) {
                                                            if (oo2.getClass().equals(ArrayList.class)) {
                                                                List<Object> cardList1 = (List<Object>) cardSubMap.get("cardMedia");
                                                                if (cardList1 != null) {
                                                                    for (int l1 = 0; l1 < cardList1.size(); l1++) {
                                                                        if (cardList1.get(l1) != null) {
                                                                            if (Objects.equals(((HashMap) cardList1.get(l1)).get("mediaType"), "VIDEO") && ((HashMap) cardList1.get(l1)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                                noOfVideos++;
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
                                            }
                                        }
                                    }
                                }
                                Collections.sort(courseCardClassList, courseCardSorter);
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        Collections.sort(courseLevelClassList, courseLevelSorter);
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0 && !courseLevelClassList.get(0).isLevelLock()) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        if (courseLevelClassList.size() != 0) {
                            OustStaticVariableHandling.getInstance().setCourseLevelCount(courseLevelClassList.size());
                        }
                        courseModulePresenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColId);
                    }
                } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId(learningPathId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                // handled here for card edited
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
//                                    Log.d("updateTimeVal", "courseId: " + courseId + " LevelId: " + levelMap.get("levelId") + " Time: " + levelMap.get("updateTime") + " " + courseLevelClass.getRefreshTimeStamp());
                                    try {
                                        entityLevelCardCourseIDUpdateTime = RoomHelper.getLevelCourseCardUpdateTimeMap((int) courseLevelClass.getLevelId());
                                        if (entityLevelCardCourseIDUpdateTime != null && entityLevelCardCourseIDUpdateTime.size() > 0) {
                                            if (entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime() < OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp())) {
                                                // Log.d("updateTimeVal", "Diff Level " + courseLevelClass.getSequence() + " : " + courseLevelClass.getLevelId() + " : " + entityLevelCardCourseIDUpdateTime.get(0).getLevelId() + "<-->" + currentLevelPosition);
//                                                Toast.makeText(CourseLearningMapActivity.this, "Level " + courseLevelClass.getSequence() + " data has been changed/modified.", Toast.LENGTH_SHORT).show();
                                            }
//                                            else {
//                                                Log.d("updateTimeVal", "Same Level " + courseLevelClass.getSequence() + " : " + courseLevelClass.getLevelId() + " : " + entityLevelCardCourseIDUpdateTime.get(0).getLevelId());
//                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    if (Objects.requireNonNull(o2).getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardSubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardSubMap != null;
                                                if (cardSubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                }
                                                if (cardSubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                }
                                                if (cardSubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                }
                                                if (cardSubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardSubMap.get("cardType") != null) {
                                                    cardType = (String) cardSubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardSubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardSubMap.get("content"));
                                                }
                                                if (cardSubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                }
                                                if (cardSubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                }
                                                if (cardSubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                }
                                                if (cardSubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                }
                                                if (cardSubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                }
                                                if (cardSubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                }
                                                if (cardSubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardSubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardSubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(HashMap.class)) {
                                                            Map<String, Object> car = (Map<String, Object>) o2;
                                                            for (String keyy : car.keySet()) {
                                                                if (car.get(keyy) != null) {
                                                                    noOfVideos++;
                                                                }
                                                            }
                                                        } else if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardSubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
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
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardSubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardSubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                    }
                                                    if (cardSubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardSubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardSubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                    }
                                                    String cardType = "";
                                                    if (cardSubMap.get("cardType") != null) {
                                                        cardType = (String) cardSubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardSubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                    }
                                                    if (cardSubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                    }
                                                    if (cardSubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardSubMap.get("content"));
                                                    }
                                                    if (cardSubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                    }
                                                    if (cardSubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                    }
                                                    if (cardSubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                    }
                                                    if (cardSubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                    }
                                                    if (cardSubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    }
                                }
                                Collections.sort(courseCardClassList, courseCardSorter);
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                                // handled here for card edited
                                //RoomHelper.addLevelCourseCardUpdateTimeMap(courseLevelClass);
                            }
                        }
                        Collections.sort(courseLevelClassList, courseLevelSorter);
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        if (courseLevelClassList.size() != 0) {
                            OustStaticVariableHandling.getInstance().setCourseLevelCount(courseLevelClassList.size());
                        }
                        courseModulePresenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColId);
                    }
                } else {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final Map<String, Object> levelMap = (Map<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId(learningPathId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    if (Objects.requireNonNull(o2).getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardSubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardSubMap != null;
                                                if (cardSubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                }
                                                if (cardSubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                }
                                                if (cardSubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                }
                                                if (cardSubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardSubMap.get("cardType") != null) {
                                                    cardType = (String) cardSubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardSubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardSubMap.get("content"));
                                                }
                                                if (cardSubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                }
                                                if (cardSubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                }
                                                if (cardSubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                }
                                                if (cardSubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                }
                                                if (cardSubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                }
                                                if (cardSubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                }
                                                if (cardSubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardSubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardSubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(HashMap.class)) {
                                                            Map<String, Object> car = (Map<String, Object>) o2;
                                                            for (String keyy : car.keySet()) {
                                                                if (car.get(keyy) != null) {
                                                                    noOfVideos++;
                                                                }
                                                            }
                                                        } else if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardSubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
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
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardSubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardSubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                    }
                                                    if (cardSubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardSubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardSubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                    }
                                                    String cardType = "";
                                                    if (cardSubMap.get("cardType") != null) {
                                                        cardType = (String) cardSubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardSubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                    }
                                                    if (cardSubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                    }
                                                    if (cardSubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardSubMap.get("content"));
                                                    }
                                                    if (cardSubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                    }
                                                    if (cardSubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                    }
                                                    if (cardSubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                    }
                                                    if (cardSubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                    }
                                                    if (cardSubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    } else {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardSubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardSubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardSubMap.get("cardBgColor"));
                                                }
                                                if (cardSubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardSubMap.get("cardQuestionColor"));
                                                }
                                                if (cardSubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardSubMap.get("cardSolutionColor"));
                                                }
                                                if (cardSubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardSubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardSubMap.get("cardType") != null) {
                                                    cardType = (String) cardSubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardSubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardSubMap.get("content"));
                                                }
                                                if (cardSubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardSubMap.get("cardTitle"));
                                                }
                                                if (cardSubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardSubMap.get("qId")));
                                                }
                                                if (cardSubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardSubMap.get("cardId")));
                                                }
                                                if (cardSubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardSubMap.get("xp")));
                                                }
                                                if (cardSubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardSubMap.get("questionCategory"));
                                                }
                                                if (cardSubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardSubMap.get("questionType"));
                                                }
                                                if (cardSubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardSubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardSubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardSubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardSubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
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
                                        }
                                    }
                                }
                                Collections.sort(courseCardClassList, courseCardSorter);
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        Collections.sort(courseLevelClassList, courseLevelSorter);
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        if (courseLevelClassList.size() != 0) {
                            OustStaticVariableHandling.getInstance().setCourseLevelCount(courseLevelClassList.size());
                        }
                        courseModulePresenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColId);
                    }

                }
                OustPreferences.saveintVar("video_count", noOfVideos);

                try {
                    String s1 = "1" + activeUser.getStudentKey() + "" + courseId;
                    long courseUniqueNo = Long.parseLong(s1);
                    if (enableVideoDownload && noOfVideos > 0) {
                        entityDownloadedOrNot = RoomHelper.getDownloadedOrNotS();
                        if (entityDownloadedOrNot != null && entityDownloadedOrNot.size() > 0) {
                            for (int i = 0; i < entityDownloadedOrNot.size(); i++) {
                                if (entityDownloadedOrNot.get(i).getCourseId() == courseId) {
                                    if (entityDownloadedOrNot.get(i).getPercentage() == 100) {
                                        fullCourseDownloaded = true;
                                        course_delete.setVisibility(View.VISIBLE);
                                        downloadVideoLayout.setVisibility(View.GONE);
                                        course_download.setVisibility(View.GONE);
                                    } else {
                                        if (courseDownloadId != null && !courseDownloadId.equals("0") && Long.parseLong(courseDownloadId) == courseUniqueNo) {
                                            dialogOpened = true;
                                            downloadVideoLayout.setVisibility(VISIBLE);
                                            downloadGifImageView.setVisibility(VISIBLE);
                                            course_delete.setVisibility(View.GONE);
                                            downloadVideoPercentage.setText("" + entityDownloadedOrNot.get(i).getPercentage());
                                            OustSdkTools.setDownloadGifImage(downloadGifImageView);
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
                setCourseBaseData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void setBulletinQuesFromFirebase(DataSnapshot dataSnapshot) {
        try {
            if (null != dataSnapshot.getValue()) {
                long courseUniqueNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
                final long updatedTime = (long) dataSnapshot.getValue();
                if ((userCourseData != null) && (updatedTime > userCourseData.getBulletinLastUpdatedTime())) {
                    //discuss with bro regarding image handle in toolbar with dot on discussion board
                } else {
                    //discuss with bro regarding image handle in toolbar without dot on discussion board
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateUserData(int learningPathId, CourseDataClass courseDataClass, Map<String, Object> learningMap) {
        DTOUserCourseData userCourseData = courseModulePresenter.getUserCourseData();
        boolean isCompletedCourseForIssue = false;
        boolean isAllCardCompleted = true;
        boolean isAssessmentStatus = true;
        boolean isSurveyStatus = true;
        try {
            if (learningMap != null && learningMap.get("completionPercentage") != null) {
                Object o3 = learningMap.get("completionPercentage");
                assert o3 != null;
                if (o3.getClass().equals(Long.class)) {
                    userCourseData.setPresentageComplete(OustSdkTools.newConvertToLong(learningMap.get("completionPercentage")));
                } else if (o3.getClass().equals(String.class)) {
                    String s3 = (String) o3;
                    userCourseData.setPresentageComplete(Long.parseLong(s3));
                } else if (o3.getClass().equals(Double.class)) {
                    Double s3 = (Double) o3;
                    long l = (Double.valueOf(s3)).longValue();
                    userCourseData.setPresentageComplete(l);
                } else {
                    userCourseData.setPresentageComplete(OustSdkTools.newConvertToLong(learningMap.get("completionPercentage")));
                }
            } else {
                userCourseData.setPresentageComplete(0);
            }

            if (((userCourseData.getPresentageComplete() == 100) ||
                    ((userCourseData.getPresentageComplete() == 90 || userCourseData.getPresentageComplete() == 95) && userCourseData.isCourseCompleted()))) {
                if (!apiLoaded) {
                    isCompletedCourseForIssue = true;
                    isCourseAlreadyComplete = true;
                } else {
                    showFinalAnimation = true;
                    isCourseAlreadyCompleteTemp = true;
                   /* isCourseAlreadyComplete = false;
                    isCourseAlreadyCompleteTemp = true;*/
                }
            } /*else {
                isCompletedCourseForIssue = true;
                isCourseAlreadyComplete = true;
            }*/

            if (!apiLoaded) {
                apiLoaded = true;
            }

            if (learningMap != null && learningMap.get("currentCourseLevelId") != null) {
                userCourseData.setCurrentLevelId(OustSdkTools.newConvertToLong(learningMap.get("currentCourseLevelId")));
            }
            if (learningMap != null && learningMap.get("currentCourseCardId") != null) {
                Log.d(TAG, "updateUserData: currentCourseCardId:" + learningMap.get("currentCourseCardId"));
                userCourseData.setCurrentCard(OustSdkTools.newConvertToLong(learningMap.get("currentCourseCardId")));
            }
            if (learningMap != null && learningMap.get("courseCompleted") != null) {
                userCourseData.setCourseCompleted((boolean) learningMap.get("courseCompleted"));
            } else {
                userCourseData.setCourseCompleted(false);
            }
            if (courseAddedOn != null) {
                userCourseData.setAddedOn(courseAddedOn);
            }
            if (learningMap != null && learningMap.get("enrolledDateTime") != null) {
                userCourseData.setEnrollmentDate((String) learningMap.get("enrolledDateTime"));
            }
            if (learningMap != null && learningMap.get("mappedAssessmentCompleted") != null) {
                userCourseData.setMappedAssessmentCompleted((boolean) learningMap.get("mappedAssessmentCompleted"));
                isAssessmentCompleted = (boolean) learningMap.get("mappedAssessmentCompleted");
                isAssessmentStatus = isAssessmentCompleted;
            } else {
                userCourseData.setMappedAssessmentCompleted(false);
                isAssessmentStatus = true;
            }

            if (learningMap != null && learningMap.get("mappedSurveyCompleted") != null) {
                isSurveyStatus = (boolean) learningMap.get("mappedSurveyCompleted");
            }

            if (learningMap != null && learningMap.get("ackPopup") != null) {
                Map<Object, Object> disClaimerMap = (Map<Object, Object>) learningMap.get("ackPopup");
                courseDesclaimerData = new CourseDesclaimerData();
                if (disClaimerMap != null) {
                    if (disClaimerMap.get("body") != null) {
                        courseDesclaimerData.setContent((String) disClaimerMap.get("body"));
                    }
                    if (disClaimerMap.get("buttonLabel") != null) {
                        courseDesclaimerData.setBtnText((String) disClaimerMap.get("buttonLabel"));
                    }
                    if (disClaimerMap.get("checkBoxText") != null) {
                        courseDesclaimerData.setCheckBoxText((String) disClaimerMap.get("checkBoxText"));
                    }
                    if (disClaimerMap.get("header") != null) {
                        courseDesclaimerData.setHeader((String) disClaimerMap.get("header"));
                    }
                }
                courseDataClass.setCourseDesclaimerData(courseDesclaimerData);
            }

            if (learningMap != null && learningMap.get("descriptionCard") != null) {
                cardInfo = (Map<String, Object>) learningMap.get("descriptionCard");
            }
            if (cardInfo != null) {
                courseDataClass.setCardInfo(cardInfo);
                course_intro_card_layout.setVisibility(View.VISIBLE);
            } else {
                course_intro_card_layout.setVisibility(View.GONE);
            }

            courseModulePresenter.setEnrolled(isEnrolled);
            courseDataClass.setEnrolled(isEnrolled);

            if (learningMap != null && learningMap.get("userOC") != null) {
                userCourseData.setTotalOc(OustSdkTools.newConvertToLong(learningMap.get("userOC")));
            }
            if (learningMap != null && learningMap.get("rating") != null) {
                int rating = (int) (OustSdkTools.newConvertToLong(learningMap.get("rating")));
                userCourseData.setMyCourseRating(rating);
            } else {
                userCourseData.setMyCourseRating(0);
            }
            if (learningMap != null && learningMap.get("mappedAssessment") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learningMap.get("mappedAssessment");
                if (mappedAssessmentMap != null && mappedAssessmentMap.get("passed") != null) {
                    userCourseData.setMappedAssessmentPassed((boolean) mappedAssessmentMap.get("passed"));
                }
            }
            if (learningMap != null && learningMap.get("contentPlayListId") != null) {
                courseDataClass.setContentPlayListId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListId")));
            }
            if (learningMap != null && learningMap.get("contentPlayListSlotId") != null) {
                courseDataClass.setContentPlayListSlotId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListSlotId")));
            }
            if (learningMap != null && learningMap.get("contentPlayListSlotItemId") != null) {
                courseDataClass.setContentPlayListSlotItemId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListSlotItemId")));
            }
            if (learningMap != null && learningMap.get("levels") != null) { //TODO: check here
                Object o1 = learningMap.get("levels");
                assert o1 != null;
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> objectList = (List<Object>) o1;
                    List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();
                    for (int k = 0; k < objectList.size(); k++) {
                        if (objectList.get(k) != null) {
                            final Map<String, Object> levelMap = (Map<String, Object>) objectList.get(k);
                            if (userLevelDataList == null) {
                                userLevelDataList = new ArrayList<>();
                            }
                            int courseLevelNo = -1;
                            int levelKeyValue = 0;
                            if (levelMap.get("levelId") != null) {
                                levelKeyValue = (int) OustSdkTools.newConvertToLong(levelMap.get("levelId"));
                            }
                            for (int l = 0; l < userLevelDataList.size(); l++) {
                                if (userLevelDataList.get(l).getLevelId() == levelKeyValue) {
                                    courseLevelNo = l;
                                }
                            }
                            for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == levelKeyValue) {
                                    if (courseLevelNo < 0) {
                                        userLevelDataList.add(new DTOUserLevelData());
                                        courseLevelNo = (userLevelDataList.size() - 1);
                                    }
                                    userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                    userLevelDataList.get(courseLevelNo).setLevelId(levelKeyValue);
                                    if (levelMap.get("userLevelOC") != null) {
                                        long userLevelOc = OustSdkTools.newConvertToLong(levelMap.get("userLevelOC"));
                                        userLevelDataList.get(courseLevelNo).setTotalOc(userLevelOc);
                                        if (userLevelOc != 0) {
                                            userLevelDataList.get(courseLevelNo).setLevelCompleted(true);
                                            userLevelDataList.get(courseLevelNo).setIslastCardComplete(true);
                                        }
                                    }
                                    if (levelMap.get("userLevelXp") != null) {
                                        userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.newConvertToLong(levelMap.get("userLevelXp")));
                                    }
                                    if (levelMap.get("locked") != null) {
                                        userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                    } else {
                                        userLevelDataList.get(courseLevelNo).setLocked(true);
                                    }
                                    Object o2 = levelMap.get("cards");
                                    if (o2 != null) {
                                        if (o2.getClass().equals(ArrayList.class)) {
                                            List<Object> objectCardList = (List<Object>) o2;
                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                            for (int j = 0; j < objectCardList.size(); j++) {
                                                if (objectCardList.get(j) != null) {
                                                    final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                    DTOUserCardData userCardData = new DTOUserCardData();
                                                    if (cardMap.get("userCardAttempt") != null) {
                                                        userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                    }
                                                    if (cardMap.get("userCardScore") != null) {
                                                        userCardData.setOc(OustSdkTools.newConvertToLong(cardMap.get("userCardScore")));
                                                    }
                                                    if (cardMap.get("cardCompleted") != null) {
                                                        userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                        if (isAllCardCompleted) {
                                                            isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                        }
                                                    } else {
                                                        userCardData.setCardCompleted(false);
                                                        isAllCardCompleted = false;
                                                    }
                                                    if (cardMap.get("cardViewInterval") != null) {
                                                        userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                    } else {
                                                        userCardData.setCardViewInterval(0);
                                                    }
                                                    if (cardMap.get("cardId") != null) {
                                                        userCardData.setCardId(OustSdkTools.newConvertToLong(cardMap.get("cardId")));
                                                    }
                                                    userCardDataList.add(userCardData);
                                                }
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        } else if (o2.getClass().equals(HashMap.class)) {
                                            final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                            for (String cardKey : objectCardMap.keySet()) {
                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                DTOUserCardData userCardData = new DTOUserCardData();
                                                assert cardMap != null;
                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                }

                                                if (cardMap.get("userCardScore") != null) {
                                                    userCardData.setOc(OustSdkTools.newConvertToLong(cardMap.get("userCardScore")));
                                                }
                                                if (cardMap.get("cardCompleted") != null) {
                                                    userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                    if (isAllCardCompleted) {
                                                        isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                    }
                                                } else {
                                                    userCardData.setCardCompleted(false);
                                                    isAllCardCompleted = false;
                                                }
                                                if (cardMap.get("cardViewInterval") != null) {
                                                    userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                } else {
                                                    userCardData.setCardViewInterval(0);
                                                }
                                                userCardData.setCardId(Integer.parseInt(cardKey));
                                                userCardDataList.add(userCardData);
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        }
                                    } else {
                                        userLevelDataList.get(courseLevelNo).setLocked(true);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    userCourseData.setUserLevelDataList(userLevelDataList);
                } else if (o1.getClass().equals(HashMap.class)) { //TODO: check here 2
                    final Map<String, Object> objectMap = (Map<String, Object>) o1;
                    List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();
                    for (String levelKey : objectMap.keySet()) {
                        final Map<String, Object> levelMap = (Map<String, Object>) objectMap.get(levelKey);
                        if (userLevelDataList == null) {
                            userLevelDataList = new ArrayList<>();
                        }
                        int courseLevelNo = -1;
                        int k = Integer.parseInt(levelKey);
                        for (int l = 0; l < userLevelDataList.size(); l++) {
                            if (userLevelDataList.get(l).getLevelId() == k) {
                                courseLevelNo = l;
                            }
                        }
                        for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                            if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                if (courseLevelNo < 0) {
                                    userLevelDataList.add(new DTOUserLevelData());
                                    courseLevelNo = (userLevelDataList.size() - 1);
                                }
                                userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                userLevelDataList.get(courseLevelNo).setLevelId(k);
                                assert levelMap != null;
                                if (levelMap.get("userLevelOC") != null) {
                                    userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("userLevelOC")));

                                }
                                if (levelMap.get("userLevelXp") != null) {
                                    userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.newConvertToLong(levelMap.get("userLevelXp")));
                                    userLevelDataList.get(courseLevelNo).setLevelCompleted(true);
                                }
                                if (levelMap.get("locked") != null) {
                                    userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                } else {
                                    userLevelDataList.get(courseLevelNo).setLocked(true);
                                }
                                Object o2 = levelMap.get("cards");
                                if (o2 != null) {
                                    if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> objectCardList = (List<Object>) o2;
                                        List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                        for (int j = 0; j < objectCardList.size(); j++) {
                                            if (objectCardList.get(j) != null) {
                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                DTOUserCardData userCardData = new DTOUserCardData();
                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                }
                                                if (cardMap.get("userCardScore") != null) {
                                                    userCardData.setOc(OustSdkTools.newConvertToLong(cardMap.get("userCardScore")));
                                                }
                                                if (cardMap.get("cardCompleted") != null) {
                                                    userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                    if (isAllCardCompleted) {
                                                        isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                    }
                                                } else {
                                                    userCardData.setCardCompleted(false);
                                                    isAllCardCompleted = false;
                                                }
                                                if (cardMap.get("cardViewInterval") != null) {
                                                    userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                } else {
                                                    userCardData.setCardViewInterval(0);
                                                }
                                                userCardData.setCardId(j);
                                                userCardDataList.add(userCardData);
                                            }
                                        }
                                        userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                    } else if (o2.getClass().equals(HashMap.class)) { //TODO: check here 3
                                        List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                        final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                        for (String cardKey : objectCardMap.keySet()) {
                                            final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                            DTOUserCardData userCardData = new DTOUserCardData();
                                            assert cardMap != null;
                                            if (cardMap.get("userCardAttempt") != null) {
                                                userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                            }
                                            if (cardMap.get("userCardScore") != null) {
                                                userCardData.setOc(OustSdkTools.newConvertToLong(cardMap.get("userCardScore")));
                                            }
                                            if (cardMap.get("cardCompleted") != null) {
                                                userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                if (isAllCardCompleted) {
                                                    isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                }
                                            } else {
                                                userCardData.setCardCompleted(false);
                                                isAllCardCompleted = false;
                                            }
                                            if (cardMap.get("cardViewInterval") != null) {
                                                userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                            } else {
                                                userCardData.setCardViewInterval(0);
                                            }
                                            userCardData.setCardId(Integer.parseInt(cardKey));
                                            userCardDataList.add(userCardData);
                                        }
                                        userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                    }
                                } else {
                                    userLevelDataList.get(courseLevelNo).setLocked(true);
                                }
                            }
                        }
                    }
                    Collections.sort(userLevelDataList, courseUserCardSorter);
                    userCourseData.setUserLevelDataList(userLevelDataList);
                }
            } else {
                //if there is no distribution data found
                Log.e(TAG, "updateUserData: level distribution data is not found");
            }
            if (userCourseData.getUserLevelDataList() != null) {
                if (userCourseData.getUserLevelDataList().size() > 0) {
                    for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                        if (userCourseData.getUserLevelDataList().get(i).isLevelCompleted() && userCourseData.getUserLevelDataList().get(i).isLastCardComplete()) {
                            Log.e(TAG, "updateUserData: level " + i + " completed..");
                        } else {
                            Log.e(TAG, "updateUserData: level " + i + " not completed..");
                            List<DTOUserLevelData> dtoUserLevelData = userCourseData.getUserLevelDataList();
                            dtoUserLevelData.get(i).setLocked(false);
                            userCourseData.setUserLevelDataList(dtoUserLevelData);
                            break;
                        }
                    }
                }
            }
            if (!isEnrolled) {
                if (courseDesclaimerData != null && !showAcknowledgementPopup) {
                    setDesClaimerPopup(courseDesclaimerData);
                } else if (cardInfo != null) {
                    branding_mani_layout.setVisibility(View.VISIBLE);
                    checkForDescriptionCardData(cardInfo);
                }
            }


            List<DTOUserLevelData> levelDataList = new ArrayList<>();
            if (userCourseData.getUserLevelDataList() == null) {
                userCourseData.setUserLevelDataList(new ArrayList<>());
            }

            for (int k = 0; k < courseDataClass.getCourseLevelClassList().size(); k++) {
                for (int l = 0; l < userCourseData.getUserLevelDataList().size(); l++) {
                    if (userCourseData.getUserLevelDataList().get(l).getLevelId() == courseDataClass.getCourseLevelClassList().get(k).getLevelId()) {
                        boolean alreadyIn = false;
                        for (int n = 0; n < levelDataList.size(); n++) {
                            if (userCourseData.getUserLevelDataList().get(l).getLevelId() == levelDataList.get(n).getLevelId()) {
                                alreadyIn = true;
                            }
                        }
                        if (!alreadyIn) {
                            levelDataList.add(userCourseData.getUserLevelDataList().get(l));
                        }
                    }
                }
            }

            userCourseData.setUserLevelDataList(levelDataList);
            DTOUserLevelData userLevelData;
            for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                boolean alreadyIn = false;
                CourseLevelClass courseLevelClass = courseDataClass.getCourseLevelClassList().get(l);

                for (int k = 0; k < userCourseData.getUserLevelDataList().size(); k++) {
                    if (userCourseData.getUserLevelDataList().get(k).getLevelId() == courseLevelClass.getLevelId()) {
                        alreadyIn = true;

                        DTOUserLevelData dtoUserLevelData = userCourseData.getUserLevelDataList().get(k);
                        try {
                            List<DTOCourseCard> dtoCourseCardList = courseLevelClass.getCourseCardClassList();
                            List<DTOUserCardData> dtoUserCardDataList = dtoUserLevelData.getUserCardDataList();
                            if (dtoUserCardDataList == null || dtoUserCardDataList.isEmpty() || dtoCourseCardList.size() < 1) {

                                dtoUserCardDataList = new ArrayList<>();
                                for (int c = 0; c < dtoCourseCardList.size(); c++) {
                                    DTOCourseCard dtoCourseCard = dtoCourseCardList.get(c);
                                    Log.d(TAG, "updateUserData:level is there, card is adding->" + dtoCourseCard.getCardId());
                                    DTOUserCardData userCardData = new DTOUserCardData(dtoCourseCard.getCardId(), 0, false);
                                    dtoUserCardDataList.add(userCardData);
                                }
                                userCourseData.getUserLevelDataList().get(k).setUserCardDataList(dtoUserCardDataList);

                            } else {
                                /*if(dtoCourseCardList.size() == dtoUserCardDataList.size()){
                                    Log.d(TAG, "updateUserData: all the cards matching in the level");
                                }else{*/
                                for (int c = 0; c < dtoCourseCardList.size(); c++) {
                                    DTOCourseCard dtoCourseCard = dtoCourseCardList.get(c);
                                    boolean isCardFound = false;
                                    for (int d = 0; d < dtoUserCardDataList.size(); d++) {
                                        DTOUserCardData dtoUserCardData = dtoUserCardDataList.get(d);
                                        if (dtoCourseCard.getCardId() == dtoUserCardData.getCardId()) {
                                            isCardFound = true;
                                            break;
                                        }
                                    }
                                    if (!isCardFound) {
                                        Log.d(TAG, "updateUserData: extra card is adding->" + dtoCourseCard.getCardId());
                                        DTOUserCardData userCardData = new DTOUserCardData(dtoCourseCard.getCardId(), 0, false);
                                        dtoUserCardDataList.add(userCardData);
                                        userCourseData.getUserLevelDataList().get(k).setUserCardDataList(dtoUserCardDataList);
                                    }
                                }
                                //}
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }

                if (!alreadyIn) {
                    Log.d(TAG, "updateUserData: isalreadyIn:" + alreadyIn);
                    userLevelData = new DTOUserLevelData();

                    userLevelData.setLevelId(courseLevelClass.getLevelId());
                    userLevelData.setSequece(courseLevelClass.getSequence());

                    List<DTOUserCardData> userCardDataList = new ArrayList<>();
                    for (int j = 0; j < courseLevelClass.getCourseCardClassList().size(); j++) {
                        DTOCourseCard dtoCourseCard = courseLevelClass.getCourseCardClassList().get(j);
                        Log.d(TAG, "updateUserData: card is adding->" + dtoCourseCard.getCardId());
                        DTOUserCardData userCardData = new DTOUserCardData(dtoCourseCard.getCardId(), 0, false);
                        userCardDataList.add(userCardData);
                    }
                    userLevelData.setUserCardDataList(userCardDataList);
                    userCourseData.getUserLevelDataList().add(userLevelData);

                }
            }


            Collections.sort(userCourseData.getUserLevelDataList(), courseUserCardSorter);
            int currentLevel = 0;
            for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                if ((courseDataClass.getCourseLevelClassList() != null) && (courseDataClass.getCourseLevelClassList().get(i) != null)
                        && (userCourseData.getUserLevelDataList() != null) && (userCourseData.getUserLevelDataList().get(i) != null)) {
                    if (!userCourseData.getUserLevelDataList().get(i).isLocked() && !courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                        currentLevel++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

            userCourseData.setCurrentLevel(currentLevel);
            if (userCourseData.getCurrentLevel() > courseDataClass.getCourseLevelClassList().size() + 1) {
                userCourseData.setCurrentLevel((courseDataClass.getCourseLevelClassList().size() + 1));
            }
            userCourseData.setCurrentCompleteLevel((int) userCourseData.getCurrentLevel() - 1);
            if (userCourseData.getPresentageComplete() == 100) {
                isCourseCompleted = true;
                learning_action_button.setVisibility(View.GONE);
                if (isSurveyAttached && surveyId != 0 && !courseDataClass.isSurveyMandatory()) {
                    checkForSavedAssessment(activeUser);
                }
                userCourseData.setCourseComplete(true);
                if (!isMicroCourse) {
                    Log.d(TAG, "updateUserData: setting here");
                    userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1); // need to check here for error
                    if (courseDataClass.getMappedAssessmentId() > 0) {
                        userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2);
                    }
                } else {
                    userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size());
                }
            } else {
                userCourseData.setCourseComplete(false);
            }
            if (!isEnrolled) {
                userCourseData.setCurrentLevel(0);
            } else if (userCourseData.getCurrentLevel() == 0 && isEnrolled) {
                userCourseData.setCurrentLevel(1);
            }

            if (userCourseData.getUserLevelDataList() != null) {
                if (userCourseData.getCurrentLevel() > 0) {
//                    currentLevelPosition = (int) userCourseData.getCurrentLevel();
                    if (currentLevelPosition != userCourseData.getCurrentLevel()) {
                        currentLevelPosition = (int) userCourseData.getCurrentLevel();
                        currentCardPosition = 0;
                    }
                    if (courseDataClass.getCourseLevelClassList() != null && (courseDataClass.getCourseLevelClassList().size() == 1 || userCourseData.getCurrentLevel() == 1)) {
                        for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size(); l++) {
                            if (courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().get(l).getCardId() == userCourseData.getCurrentCard()) {
                                Log.d(TAG, "updateUserData: setCurrentCardNo:" + l);
                                userCourseData.getUserLevelDataList().get((int) userCourseData.getCurrentLevel() - 1).setCurrentCardNo(l + 1);
                                currentCardPosition = l;
                                if (isResumeClicked) {
                                    isResumeClicked = false;
                                    branding_mani_layout.setVisibility(View.GONE);
                                    if (currentLevelPosition <= 0) {
                                        courseModulePresenter.clickOnUserManualRow(0, currentCardPosition, true);
                                    } else {
                                        courseModulePresenter.clickOnUserManualRow(currentLevelPosition - 1, currentCardPosition, true);
                                    }
                                }
                                break;
                            } else {
                                if (l == courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size() - 1) {
                                    if (isResumeClicked) {
                                        isResumeClicked = false;
                                        branding_mani_layout.setVisibility(View.GONE);
                                        if (currentLevelPosition <= 0) {
                                            courseModulePresenter.clickOnUserManualRow(0, currentCardPosition, true);
                                        } else {
                                            courseModulePresenter.clickOnUserManualRow(currentLevelPosition - 1, currentCardPosition, true);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (courseDataClass.getCourseLevelClassList() != null) {
                            for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size(); l++) {
                                if (courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getLevelId() == userCourseData.getCurrentLevel()) {
                                    if (courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().get(l).getCardId() == userCourseData.getCurrentCard()) {
                                        Log.d(TAG, "updateUserData: setCurrentCardNo:" + l);
                                        userCourseData.getUserLevelDataList().get((int) userCourseData.getCurrentLevel() - 1).setCurrentCardNo(l + 1);
                                        currentCardPosition = l;
                                        if (isResumeClicked) {
                                            isResumeClicked = false;
                                            branding_mani_layout.setVisibility(View.GONE);
                                            if (currentLevelPosition <= 0) {
                                                courseModulePresenter.clickOnUserManualRow(0, currentCardPosition, true);
                                            } else {
                                                courseModulePresenter.clickOnUserManualRow(currentLevelPosition - 1, currentCardPosition, true);
                                            }
                                        }
                                        break;
                                    } else {
                                        if (l == courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size() - 1) {
                                            if (isResumeClicked) {
                                                isResumeClicked = false;
                                                branding_mani_layout.setVisibility(View.GONE);
                                                if (currentLevelPosition <= 0) {
                                                    courseModulePresenter.clickOnUserManualRow(0, currentCardPosition, true);
                                                } else {
                                                    courseModulePresenter.clickOnUserManualRow(currentLevelPosition - 1, currentCardPosition, true);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (l == courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size() - 1) {
                                        if (isResumeClicked) {
                                            isResumeClicked = false;
                                            branding_mani_layout.setVisibility(View.GONE);
                                            if (currentLevelPosition <= 0) {
                                                courseModulePresenter.clickOnUserManualRow(0, currentCardPosition, true);
                                            } else {
                                                courseModulePresenter.clickOnUserManualRow(currentLevelPosition - 1, currentCardPosition, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            branding_mani_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            courseModulePresenter.addUserData(userCourseData);
            courseModulePresenter.gotLpDataFromFirebase(courseDataClass);
            branding_mani_layout.setVisibility(View.GONE);
            if (restartLevelStatus && levelId > 0) {
                isLearningCardOpen = false;
                openLevelByClick("" + levelId, true);
                levelId = 0;
                restartLevelStatus = false;
            }
        } catch (Exception e) {
            branding_mani_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        if (!isCompletedCourseForIssue && isAllCardCompleted && isAssessmentStatus && isSurveyStatus && !isInstrumentationHit) {
            /*if(courseDataClass.isSurveyMandatory() && courseDataClass.getMappedSurveyId()>0){
                Log.d(TAG, "updateUserData: Skip hitting instrumentaion api for surevyMandatory");
            }else {
                isInstrumentationHit = true;
                courseModulePresenter.hitInstrumentationForCompletion(CourseLearningMapActivity.this);
            }*/

            isInstrumentationHit = true;
            courseModulePresenter.hitInstrumentationForCompletion(CourseLearningMapActivity.this);
        }
    }

    @Override
    public void showstored_Popup() {

    }

    @Override
    public void hideCertificateLoader() {

    }

    @Override
    public void gotCertificateToMailResponse(String mail, CourseDataClass courseDataClass, CommonResponse commonResponse, boolean isComingFormCourseCompletePopup) {
        try {
            if (certificateEmail_popup != null) {
                certificateEmail_popup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showCoins() {
        user_coins_earned.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotoRegularModeActivity(int learningPathId) {

    }

    @Override
    public void setLearningCardOpen(boolean isLearningCardOpen) {

    }

    @Override
    public void gotoSurveyPage() {
        goToSurvey(surveyId + "", "Survey");
    }

    @Override
    public void restartActivity() {

    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {

    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {

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

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    @Override
    public void onMainRowClick(int position) {
        try {
            if ((filteredSearchCourseLevelList != null) && (filteredSearchCourseLevelList.size() > 0)) {
            } else if ((searchCourseLevelList != null) && searchCourseLevelList.size() > 0 && searchCourseLevelList.size() > position) {
                boolean status = searchCourseLevelList.get(position).isSearchMode();
                for (int j = 0; j < searchCourseLevelList.size(); j++) {
                    searchCourseLevelList.get(j).setSearchMode(false);
                }
                if (!status) {
                    searchCourseLevelList.get(position).setSearchMode(true);
                }
                createLevelList(searchCourseLevelList, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onCardClick(int levelPosition, int cardPosition) {
        courseModulePresenter.clickOnUserManualRow(levelPosition, cardPosition, false);
    }

    @Override
    public void onCardClick(int levelPosition, int cardPosition, boolean isRegularMode) {
        courseModulePresenter.clickOnUserManualRow(levelPosition, cardPosition, isRegularMode);
    }

    @Override
    public void onDownloadIconClick(int position) {

    }

    @Override
    public void startUpdatedLearningMap(boolean killActivity, boolean updateReviewList) {
        Log.d(TAG, "startUpdatedLearningMap:-> killActivity->  " + killActivity);
        try {
            if (courseModulePresenter != null) {
                if (killActivity) {
                    courseModulePresenter.killActivity();
                } else {
                    courseModulePresenter.onResumeCalled(updateReviewList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void restartCourseLearningModuleActivity(boolean restartLevelStatus, int levelId) {
        try {
            this.restartLevelStatus = restartLevelStatus;
            this.levelId = levelId;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void upDateLevelTime(boolean restartLevelStatus, int levelId) {
        try {
            this.restartLevelStatus = restartLevelStatus;
            this.updateLevelStatus = restartLevelStatus;
            this.levelId = levelId;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            //handle downloading progress

                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile(dtoCourseCard);
                            courseDownloading();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            //handle download error
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    public class CourseDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "course_download_response";
        public static final String PROCESS_ERROR = "course_download_error";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                String action = intent.getAction();
                Log.d(TAG, "onReceive CourseDownloadReceiver " + action);

                if (action != null && !TextUtils.isEmpty(action)) {
                    switch (action) {
                        case PROCESS_RESPONSE:
                            courseDownloading();
                            break;

                        case PROCESS_ERROR:
                            if (downloadAnimation != null) {
                                downloadAnimation.stop();
                                downloadAnimation.selectDrawable(0);
                            }
                            mUserCourseData.setDownloading(false);
                            break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    public class MyDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                courseDownloading();
                levelToRestartAfterPermission = "";
                levelToRestartAfterPermission = intent.getStringExtra("levelNo");
                if ((levelToRestartAfterPermission != null) && (!levelToRestartAfterPermission.isEmpty())) {
                    ActivityCompat.requestPermissions(CourseLearningMapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                } else {
                    if (isReviewMode) {
                        Log.d(TAG, "onReceive: non play adapter");
                        if (nonPlayModeAdapter != null) {
                            nonPlayModeAdapter.notifyDateChanges(searchCourseLevelList, false);
                        }
                    } else {
                        courseModulePresenter.updateLevelDownloadStatus();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    public class DownloadReceiver extends BroadcastReceiver {
        public static final String DOWNLOAD_RESPONSE = "course_download_percentage";

        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                try {
                    if (intent.hasExtra("courseId")) {
                        courseDownloadId = intent.getStringExtra("courseId");
                        Log.d("couresisdownloaded ", "id: " + courseDownloadId + " Course id-> " + OustPreferences.get("current_course_id"));
                        courseId = Long.parseLong(OustPreferences.get("current_course_id"));
                        String s1 = "1" + activeUser.getStudentKey() + "" + courseId;
                        long courseUniqueNo = Long.parseLong(s1);
                        if (intent.hasExtra("downloadPercentage") && Long.parseLong(courseDownloadId) == courseUniqueNo) {
                            int downValue = 0;
                            try {
                                Log.d("downloadPercentageIs ", "downloadPercentage: " + intent.getExtras().get("downloadPercentage"));
                                downValue = (int) Math.round(Float.parseFloat(String.valueOf(intent.getExtras().get("downloadPercentage"))));
                                RoomHelper.addDownloadedOrNot((int) courseId, false, downValue);
                                downloadVideoLayout.setVisibility(View.VISIBLE);
                                downloadVideoPercentage.setVisibility(VISIBLE);
                                downloadVideoPercentage.setText("" + downValue);
                                OustSdkTools.setDownloadGifImage(downloadGifImageView);

                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            course_download.setVisibility(View.GONE);
                            course_delete.setVisibility(View.GONE);
                            if (downValue >= 100) {
                                course_delete.setVisibility(View.VISIBLE);
                                downloadVideoLayout.setVisibility(View.GONE);
                                downloadVideoPercentage.setVisibility(View.GONE);
                                RoomHelper.addDownloadedOrNot((int) courseId, true, 100);
                            }
                        }
                    }

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

    public void removeFile(DTOCourseCard courseCardClass) {

        if (mediaSize > 0) {
            mediaSize--;
        }
        if (mediaSize == 0) {
            downloadComplete(courseCardClass);
        }
    }

    public void downloadComplete(DTOCourseCard courseCardClass) {
        try {
            if (courseCardClass != null) {
                isAnyPopupVisible = true;
                //handle intro card
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setDesClaimerPopup(final CourseDesclaimerData courseDesclaimerData) {
        try {
            isAnyPopupVisible = true;
            showAcknowledgementPopup = true;
            course_ack_popup.setVisibility(View.VISIBLE);
            if (courseDesclaimerData != null) {
                if (courseDesclaimerData.getHeader() != null) {
                    String heading = "<u>" + courseDesclaimerData.getHeader() + "</u>";
                    ack_heading.setText(Html.fromHtml(heading));
                }
                if (courseDesclaimerData.getContent() != null) {
                    ack_content.setText(courseDesclaimerData.getContent());
                    ack_content.setMovementMethod(new ScrollingMovementMethod());
                }
                if ((courseDesclaimerData.getCheckBoxText() != null) && (!courseDesclaimerData.getCheckBoxText().isEmpty())) {
                    ack_agree_layout.setVisibility(View.VISIBLE);
                    ack_agree_checkbox_text.setText(courseDesclaimerData.getCheckBoxText());
                } else {
                    ack_agree_layout.setVisibility(View.GONE);
                    setCustomButtonBackground(ack_ok_layout);
                    OustSdkTools.setLayoutBackgroud(ack_ok_layout, R.drawable.disclaimer_button_rounded_corer);
                }
                if ((courseDesclaimerData.getBtnText() != null) && (!courseDesclaimerData.getBtnText().isEmpty())) {
                    ack_ok_button_text.setText(courseDesclaimerData.getBtnText());
                }
            }

            ack_ok_layout.setOnClickListener(v -> {
                if (checkBoxStatus) {
                    courseModulePresenter.sendAcknowledgedRequest(courseColId, courseId);
                    course_ack_popup.setVisibility(View.GONE);
                    if ((cardInfo != null) && (!cardInfo.isEmpty())) {
                        branding_mani_layout.setVisibility(View.VISIBLE);
                        checkForDescriptionCardData(cardInfo);
                    } else {
                        courseModulePresenter.clickOnEnrollLp(true);
                    }
                }
            });
            ack_agree_layout.setOnClickListener(view -> {
                if (!checkBoxStatus) {
                    checkBoxStatus = true;
                    ack_agree_checkbox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_check_box_green));
                    setCustomButtonBackground(ack_ok_layout);
                } else {
                    checkBoxStatus = false;
                    ack_agree_checkbox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_check_box_black));
                    OustSdkTools.setLayoutBackgroud(ack_ok_layout, R.drawable.disclaimer_button_roundedcorner_disabled);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCustomButtonBackground(RelativeLayout button) {
        try {
            if (OustPreferences.get("toolbarColorCode") != null) {
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
                final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.disclaimer_button_rounded_corer);
                final GradientDrawable d1 = (GradientDrawable) ld.findDrawableByLayerId(R.id.custom_roundedcorner);
                int color = 0;

                color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                Log.e("AIRTEL", "THIS IS preference color" + color);
                d1.setColorFilter(color, mode);
                OustSdkTools.setLayoutBackgroudDrawable(button, ld);
            } else {
                OustSdkTools.setLayoutBackgroud(button, R.drawable.disclaimer_button_rounded_corer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForDescriptionCardData(Map<String, Object> cardInfo) {
        Log.d(TAG, "checkForDescriptionCardData: ");
        try {
            DTOCourseCard courseCardClass = new DTOCourseCard();
            if (cardInfo != null) {
                CommonTools commonTools = new CommonTools();
                courseCardClass = commonTools.getCardFromMap(cardInfo);
            }
            openIntroCard(courseCardClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openIntroCard(DTOCourseCard courseCardClass) {
        try {
            List<String> mediaList = new ArrayList<>();
            List<String> pathList = new ArrayList<>();
            if (courseCardClass.getCardMedia() != null && courseCardClass.getCardMedia().size() > 0) {
                for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                    if (courseCardClass.getCardMedia().get(k).getData() != null) {
                        boolean isVideo = false;
                        String path = "";
                        if (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("IMAGE")) {
                            path = "course/media/image/" + courseCardClass.getCardMedia().get(k).getData();
                            pathList.add(path);
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                        } else if (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("GIF")) {
                            path = "course/media/gif/" + courseCardClass.getCardMedia().get(k).getData();
                            pathList.add(path);
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                        } else if (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("AUDIO")) {
                            path = "course/media/audio/" + courseCardClass.getCardMedia().get(k).getData();
                            pathList.add(path);
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                        } else if (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("VIDEO")) {
                            path = "course/media/video/" + courseCardClass.getCardMedia().get(k).getData();
                            pathList.add(path);
                            mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                            isVideo = true;
                        }
                        Log.e(TAG, "openIntroCard: download service");
                        pathSize = pathList.size();
                        checkMediaExists(courseCardClass.getCardMedia().get(k).getData(), path, isVideo, courseCardClass);
                    }
                }
            }
            if (courseCardClass.getReadMoreData() != null && courseCardClass.getReadMoreData().getRmId() > 0) {
                String path = "";
                switch (courseCardClass.getReadMoreData().getType()) {
                    case "PDF":
                    case "IMAGE":
                        path = "readmore/file/" + courseCardClass.getReadMoreData().getData();
                        pathList.add(path);
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                }
                Log.e(TAG, "openIntroCard: Readmore");
                pathSize = pathList.size();
                checkMediaExists(courseCardClass.getReadMoreData().getData(), path, false, courseCardClass);
            }
            if (courseCardClass.getCardType() != null && !courseCardClass.getCardType().isEmpty()) {
                if (courseCardClass.getCardType().equalsIgnoreCase("SCORM")) {
                    comingFromIntroCard = true;
                    branding_mani_layout.setVisibility(View.GONE);
                    Intent introIntent = new Intent(CourseLearningMapActivity.this, IntroScormCardActivity.class);
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(courseCardClass);
                    OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                    startActivity(introIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCourseBaseData() {

        if (courseDataClass != null) {

            String courseDurationText;
            if (courseDataClass.getContentDuration() != 0) {
                courseDurationText = OustSdkTools.getTime(courseDataClass.getContentDuration());
                if (courseDurationText.equalsIgnoreCase("1")) {
                    courseDurationText = courseDurationText + " mins";
                } else {
                    courseDurationText = courseDurationText + " min";
                }
            } else {
                courseDurationText = "1 min";
            }
            course_remaining_duration.setText(courseDurationText);
        }
    }

    public void checkForSavedAssessment(ActiveUser activeUser) {
        try {
            String node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
            ValueEventListener surveyProgressListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        AssessmentPlayResponse assessmentPlayResponse = new AssessmentPlayResponse();
                        final Map<String, Object> assessmentProgressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                        String currentState = AssessmentState.STARTED;
                        try {
                            if (assessmentProgressMainMap != null && assessmentProgressMainMap.get("assessmentState") != null) {
                                currentState = ((String) assessmentProgressMainMap.get("assessmentState"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        assessmentPlayResponse.setAssessmentState(currentState);
                        isSurveyOpened = true;
                        if ((currentState != null) && (currentState.equals(AssessmentState.SUBMITTED))) {
                            Log.d(TAG, "onDataChange: survey completed");

                        } else {
                            startSurvey();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(surveyProgressListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startSurvey() {
        if (startSurveyImmediately) {
            goToSurvey(surveyId + "", "Survey");
        }
    }

    public void goToSurvey(String assessmentId, String surveyTitle) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                intent.putExtra("surveyTitle", surveyTitle);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("isMicroCourse", isMicroCourse);
                if (courseDataClass.isSurveyMandatory() && courseDataClass.getMappedAssessmentId() > 0) {
                    intent.putExtra("mappedAssessmentId", courseDataClass.getMappedAssessmentId());
                }
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCourseDownloadService(CourseDataClass courseDataClass) {
        try {
            if (!allCourseDownloadStarted || dialogOpened) {
                allCourseDownloadStarted = true;
                Log.d(TAG, "startCourseDownloadService: ");

                boolean isServiceRunning = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();
                    String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                    long courseUniqueNo = Long.parseLong(s1);
                    Log.d(TAG, "couresisdownloaded --> " + courseUniqueNo);
                    for (StatusBarNotification notification : barNotifications) {
                        if (notification.getId() == courseUniqueNo) {
                            isServiceRunning = true;
                            Log.d(TAG, "couresisdownloaded --> " + isServiceRunning);
                            break;
                        }
                    }
                }

                //TODO: handle intent for download
                if (!isServiceRunning) {
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(courseDataClass);

                    Intent intent = new Intent(CourseLearningMapActivity.this, DownloadForegroundService.class);
                    intent.setAction(START_UPLOAD);
                    intent.putExtra(DownloadForegroundService.COURSE_DATA, courseDataStr);
                    intent.putExtra(DownloadForegroundService.COURSE_ID, courseDataClass.getCourseId());
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_COURSE, true);
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_VIDEO, enableVideoDownload);
                    startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enableCourseDataDelete() {
        try {
            String s1 = "1" + activeUser.getStudentKey() + "" + courseId;
            long courseUniqueNo = Long.parseLong(s1);
            if (isRegularMode || isSalesMode) {
                dialogOpened = true;
            }
            if (courseDownloadId != null && !courseDownloadId.equals("0") && Long.parseLong(courseDownloadId) == courseUniqueNo || dialogOpened) {
                try {
                    if (enableVideoDownload && noOfVideos > 0) {
                        entityDownloadedOrNot = RoomHelper.getDownloadedOrNotS();
                        if (entityDownloadedOrNot != null && entityDownloadedOrNot.size() > 0) {
                            for (int i = 0; i < entityDownloadedOrNot.size(); i++) {
                                if (entityDownloadedOrNot.get(i).getCourseId() == courseId) {
                                    if (entityDownloadedOrNot.get(i).isDownloadedOrNot()) {
                                        fullCourseDownloaded = true;
                                        course_delete.setVisibility(View.VISIBLE);
                                        downloadVideoLayout.setVisibility(View.GONE);
                                        course_download.setVisibility(View.GONE);
                                    } else {
                                        if (courseDownloadId != null && !courseDownloadId.equals("0") && Long.parseLong(courseDownloadId) == courseUniqueNo) {
                                            dialogOpened = true;
                                            downloadVideoLayout.setVisibility(VISIBLE);
                                            downloadGifImageView.setVisibility(VISIBLE);
                                            course_delete.setVisibility(View.GONE);
                                            downloadVideoPercentage.setText("" + entityDownloadedOrNot.get(i).getPercentage());
                                            OustSdkTools.setDownloadGifImage(downloadGifImageView);
                                        }
                                    }
                                }
                            }
                        } else {
                            disableCourseDataDelete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } else {
                if (mUserLevelDataList != null) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        if (RoomHelper.checkMapTableExist((int) mUserLevelDataList.get(i).getLevelId())) {
//                        if (dialogOpened) {
//                            course_delete.setVisibility(View.GONE);
//                            Log.d("couresisdownloaded", "check 6");
//                        } else {
                            normalCourseDownloaded = true;
                            course_delete.setVisibility(View.VISIBLE);
                            Log.d("couresisdownloaded", "check 1");
//                        }
                            course_download.setVisibility(View.GONE);
                            if (downloadAnimation != null) {
                                downloadAnimation.stop();
                                downloadAnimation.selectDrawable(1);
                                if (isRegularMode) {
                                    course_download.setVisibility(View.GONE);
                                    course_delete.setVisibility(VISIBLE);
                                }
                            }
                            course_download.setEnabled(false);
                            break;
                        } else {

                            if (downloadAnimation != null) {
                                downloadAnimation.stop();
                                downloadAnimation.selectDrawable(0);
                                course_download.setVisibility(VISIBLE);
                                course_delete.setVisibility(View.GONE);
                            }
                            disableCourseDataDelete();
                        }
                    }
                } else {
                    disableCourseDataDelete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void disableCourseDataDelete() {
        try {
            course_delete.setVisibility(View.GONE);
            course_download.setVisibility(View.VISIBLE);
            downloadVideoLayout.setVisibility(View.GONE);
            course_download.setEnabled(true);
            entityDownloadedOrNot = RoomHelper.getDownloadedOrNotS();
            if (entityDownloadedOrNot != null && entityDownloadedOrNot.size() > 0) {
                for (int i = 0; i < entityDownloadedOrNot.size(); i++) {
                    if (entityDownloadedOrNot.get(i).getCourseId() == courseId && entityDownloadedOrNot.get(i).getPercentage() == 100) {
                        course_delete.setVisibility(View.VISIBLE);
                        normalCourseDownloaded = true;
                        course_download.setVisibility(View.GONE);
                        downloadVideoLayout.setVisibility(View.GONE);
                        Log.d("couresisdownloaded", "doenloaded FINE<--<-- " + course_title.getText().toString());
                    }
                }
            }

            if (mUserLevelDataList != null) {
                for (int i = 0; i < mUserLevelDataList.size(); i++) {
                    if (RoomHelper.checkMapTableExist((int) mUserLevelDataList.get(i).getLevelId())) {
                        normalCourseDownloaded = true;
                        course_delete.setVisibility(View.VISIBLE);
                        course_download.setVisibility(View.GONE);
                        downloadVideoLayout.setVisibility(View.GONE);
                        if (downloadAnimation != null) {
                            downloadAnimation.stop();
                            downloadAnimation.selectDrawable(1);
                        }
                        course_download.setEnabled(false);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 102) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    courseModulePresenter.clickOnCourseDownload();
                } else {
                    if (downloadAnimation != null) {
                        downloadAnimation.stop();
                        downloadAnimation.selectDrawable(0);
                    }

                    downloadCourseClicked = false;
                    courseAlreadyDownloaded = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    public void courseCompletePopup(final CourseDataClass courseDataClass) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("CourseCompleted", true);
            eventUpdate.put("CourseID", courseId);
            eventUpdate.put("Course Name", courseDataClass.getCourseName());
            Log.d(TAG, "CleverTap instance: " + eventUpdate.toString());

            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Course_Completed", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
//            final boolean[] doNotShowRatePopup = new boolean[1];
            isCourseCompletionPopupShown = true;
            DTOUserCourseData dtoUserCourseData = courseModulePresenter.getDTOUserCourseData();
            isScoreDisplaySecondTime = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME);

            if (showCourseCompletionPopup) {
                View courseCompletionDialog = getLayoutInflater().inflate(R.layout.course_completion_popup, null);
                courseComplete_popup = OustSdkTools.createPopUp(courseCompletionDialog);

                RelativeLayout pop_layout = courseCompletionDialog.findViewById(R.id.pop_layout);
                FrameLayout pop_up_close_icon = courseCompletionDialog.findViewById(R.id.pop_up_close_icon);
                ImageView leader_board = courseCompletionDialog.findViewById(R.id.leader_board);
                NestedScrollView scroll_lay = courseCompletionDialog.findViewById(R.id.scroll_lay);
                CircleImageView user_avatar = courseCompletionDialog.findViewById(R.id.user_avatar);
                TextView user_greeting = courseCompletionDialog.findViewById(R.id.user_greeting);
                LinearLayout badge_name_layout = courseCompletionDialog.findViewById(R.id.badge_name_layout);
                TextView badge_name_text = courseCompletionDialog.findViewById(R.id.badge_name_text);
                CircleImageView congrats_image = courseCompletionDialog.findViewById(R.id.congrats_image);
                TextView completed_text = courseCompletionDialog.findViewById(R.id.completed_text);
                TextView content_title = courseCompletionDialog.findViewById(R.id.content_title);
                TextView content_completed_date = courseCompletionDialog.findViewById(R.id.content_completed_date);
                TextView content_completed_message = courseCompletionDialog.findViewById(R.id.content_completed_message);
                LinearLayout layout_ResultScore = courseCompletionDialog.findViewById(R.id.layout_ResultScore);
                LinearLayout score_lay = courseCompletionDialog.findViewById(R.id.score_lay);
                TextView user_score_text = courseCompletionDialog.findViewById(R.id.user_score_text);
                LinearLayout layout_coins = courseCompletionDialog.findViewById(R.id.layout_coins);
                TextView coins_earned = courseCompletionDialog.findViewById(R.id.coins_earned);
                try {
                    if (OustPreferences.getAppInstallVariable("showCorn")) {
                        coins_earned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                    } else {
                        coins_earned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                TextView user_time_taken = courseCompletionDialog.findViewById(R.id.user_time_taken);
                TextView participants_count = courseCompletionDialog.findViewById(R.id.participants_count);
                LinearLayout certificate_lay = courseCompletionDialog.findViewById(R.id.certificate_lay);
                ImageView certificate_icon = courseCompletionDialog.findViewById(R.id.certificate_icon);
                FrameLayout content_result_action = courseCompletionDialog.findViewById(R.id.content_result_action);

                Drawable bgDrawable = getResources().getDrawable(R.drawable.ic_common_circle);
                certificate_icon.setBackground(OustSdkTools.drawableColor(bgDrawable));

                if (activeUser != null) {
                    if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                        Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).into(user_avatar);
                    }

                    if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                        String userGreetings = getResources().getString(R.string.congratulations_text) + "\n" + activeUser.getUserDisplayName() + "!";
                        user_greeting.setText(userGreetings);
                    }
                }

                if (courseDataClass.getMappedAssessmentId() != 0) {
                    completed_text.setText("You have completed the learning section of ");
                    content_completed_message.setText(getResources().getString(R.string.get_started_assessment));
                    content_completed_message.setVisibility(VISIBLE);
                } else if (surveyId != 0) {
                    completed_text.setText("You have completed the learning section of ");
                    content_completed_message.setText(getResources().getString(R.string.get_started_survey));
                    content_completed_message.setVisibility(VISIBLE);
                }

                if (courseDataClass.getBadgeName() != null && !courseDataClass.getBadgeName().isEmpty()) {
                    badge_name_layout.setVisibility(VISIBLE);
                    badge_name_text.setText(courseDataClass.getBadgeName());
                }

                if (courseDataClass.getBadgeIcon() != null && !courseDataClass.getBadgeIcon().isEmpty()) {
                    Glide.with(CourseLearningMapActivity.this).load(courseDataClass.getBadgeIcon()).into(congrats_image);
                }

                content_title.setText(courseDataClass.getCourseName());

                String completedDate = "on " + new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(new Date());
                content_completed_date.setText(completedDate);

                long enrolledCount = courseDataClass.getNumEnrolledUsers();
                if (enrolledCount == 0) {
                    enrolledCount = 1;
                }
                String enrolledText = "" + enrolledCount;
                Spannable enrolledSpan = new SpannableString(enrolledText);
                enrolledSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, enrolledText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                enrolledSpan.setSpan(new RelativeSizeSpan(1.25f), 0, enrolledText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                participants_count.setText(enrolledSpan);

                String timeTakenText = "1 min";
                if (courseDataClass.getContentDuration() != 0) {
                    int courseDuration = (int) (courseDataClass.getContentDuration() * 1.0) / 60;
                    timeTakenText = courseDuration + " min";
                    if (courseDataClass.getContentDuration() < 60) {
                        timeTakenText = "1 min";

                    }
                }

                Spannable spanText = new SpannableString(timeTakenText);
                if (timeTakenText.contains(" ")) {
                    String[] timeTaken = timeTakenText.split(" ");
                    spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanText.setSpan(new RelativeSizeSpan(1.25f), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                user_time_taken.setText(spanText);

                if (!courseDataClass.isHideLeaderBoard()) {
                    if (!OustPreferences.getAppInstallVariable("hideAllCourseLeaderBoard")) {
                        leader_board.setVisibility(VISIBLE);
                    } else {
                        leader_board.setVisibility(View.GONE);

                    }
                } else {
                    leader_board.setVisibility(View.GONE);
                }

                if (isComingFromCpl) {
                    certificate_icon.setVisibility(View.GONE);
                    leader_board.setVisibility(View.GONE);
                    layout_ResultScore.setVisibility(View.GONE);
                } else if (isCourseCompleted) {
                    if (isScoreDisplaySecondTime) {
                        layout_ResultScore.setVisibility(VISIBLE);
                    } else {
                        layout_ResultScore.setVisibility(View.GONE);
                    }

                }

                if (courseDataClass.isCertificate()) {
                    certificate_lay.setVisibility(View.GONE);

                    //Not needed
                    /*if (courseDataClass.getMappedAssessmentId() == 0) {
                        certificate_lay.setVisibility(VISIBLE);
                    }*/
                } else {
                    certificate_lay.setVisibility(View.GONE);
                }

                long totalOc = dtoUserCourseData.getTotalOc();
                long courseOc = courseDataClass.getTotalOc();
                coins_earned.setText(("" + dtoUserCourseData.getTotalOc()));

                String yourCoinText = totalOc + "/" + courseOc;
                if (yourCoinText.contains("/")) {
                    String[] spilt = yourCoinText.split("/");
                    Spannable yourCoinSpan = new SpannableString(yourCoinText);
                    yourCoinSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    yourCoinSpan.setSpan(new RelativeSizeSpan(1.25f), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    coins_earned.setText(yourCoinSpan);
                } else {
                    coins_earned.setText(yourCoinText);
                }

                coins_earned.setText(("" + dtoUserCourseData.getTotalOc()));

                if (dtoUserCourseData.getTotalOc() == 0) {
                    layout_coins.setVisibility(View.GONE);
                }

                int myXp = 0;
                int totalXp = 0;
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    myXp += dtoUserCourseData.getUserLevelDataList().get(i).getXp();
                    float totalLevelXp = courseDataClass.getCourseLevelClassList().get(i).getTotalXp();
                    if (totalLevelXp == 0) {
                        for (int l = 0; l < courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size(); l++) {
                            totalLevelXp += courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(l).getXp();
                        }
                    }
                    totalXp += totalLevelXp;
                }

                String yourScoreText = myXp + "/" + totalXp;
                if (yourScoreText.contains("/")) {
                    String[] spilt = yourScoreText.split("/");
                    Spannable yourScoreSpan = new SpannableString(yourScoreText);
                    yourScoreSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    yourScoreSpan.setSpan(new RelativeSizeSpan(1.25f), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    user_score_text.setText(yourScoreSpan);
                } else {
                    user_score_text.setText(yourScoreText);
                }
                if (totalXp == 0) {
                    score_lay.setVisibility(View.GONE);
                }

                courseCompletionDialog.setFocusableInTouchMode(true);
                courseCompletionDialog.requestFocus();

                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(congrats_image,
                        PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.2f));
                scaleDown.setDuration(500);
                scaleDown.setRepeatCount(2);
                scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
                scaleDown.start();

                if (isMicroCourse) {
                    playNewCourseCompleteAudio();
                }

                content_result_action.setOnClickListener(v -> {

                    Log.d(TAG, "courseCompletePopup: ");
                    if ((courseComplete_popup != null) && (courseComplete_popup.isShowing())) {
                        courseComplete_popup.dismiss();
                        try {
                            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                                completionAudioPlayer.stop();
                                stopPlayer = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }

                        if (isComingFromCpl) {
                            if (!isSurveyAttached || surveyId < 1) {
                                if (!isShowRatingPopUp) {
                                    finish();
                                }
                            }
                        }
                    }
                });

                leader_board.setOnClickListener(view -> openCourseLeaderBoard(courseDataClass));

                certificate_lay.setOnClickListener(view -> {
                    if ((courseComplete_popup != null) && (courseComplete_popup.isShowing())) {
                        //doNotShowRatePopup[0] = true;
                        sendCertificatePopup(courseDataClass, true);
                        courseComplete_popup.dismiss();

                        try {
                            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                                completionAudioPlayer.stop();
                                stopPlayer = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                });


                courseComplete_popup.setOnDismissListener(() -> {
                    try {
                        if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                            completionAudioPlayer.stop();
                            stopPlayer = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if ((courseComplete_popup != null)) {
//                        if (!doNotShowRatePopup[0]) {
                        if (isShowRatingPopUp) {
                            showRateCoursePopup(courseDataClass);
                        } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                            //Todo
                            startSurvey();
                        } else {
                            if (isMicroCourse) {
                                courseModulePresenter.pressClickOnAssessment();
                            } else {
                                courseModulePresenter.startShowingReviewModeOption();
                            }
                        }
                       /* } else if (enableVideoDownload) {
                            //TODO: need to handle delete video download
                            //deleteVideoAlertDialog();
                        }*/

                        courseComplete_popup.dismiss();
                    }
                });

                pop_up_close_icon.setOnClickListener(v -> {
                    try {
                        if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                            completionAudioPlayer.stop();
                            stopPlayer = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if ((courseComplete_popup != null)) {
//                        if (!doNotShowRatePopup[0]) {
                        if (isShowRatingPopUp) {
                            showRateCoursePopup(courseDataClass);
                        } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                            //todo
                            startSurvey();
                        }
                        /*} else if (enableVideoDownload) {
                            //TODO: need to handle delete video download
                            //deleteVideoAlertDialog();
                        }*/
                        courseComplete_popup.dismiss();
                    }
                });

                if (!isValidLbResetPeriod()) {
                    layout_ResultScore.setVisibility(View.GONE);
                }


            } else {
                if (isShowRatingPopUp) {
                    showRateCoursePopup(courseDataClass);
                } else if (isSurveyAttached && surveyId != 0) {
                    startSurvey();
                } else if (isComingFromCpl) {
                    finish();
                } else {
                    courseModulePresenter.startShowingReviewModeOption();
                }
            }
            int requestCode = Integer.parseInt("1" + courseDataClass.getCourseId());

            Intent intent = new Intent(CourseLearningMapActivity.this, ReminderNotificationManager.class);
            PendingIntent pendingIntentOnCreate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntentOnCreate = PendingIntent.getBroadcast(CourseLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntentOnCreate = PendingIntent.getBroadcast(CourseLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
            }

            boolean isAlready = (pendingIntentOnCreate != null);
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (isAlready) {
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast(CourseLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntent = PendingIntent.getBroadcast(CourseLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                }
                assert alarmMgr != null;
                alarmMgr.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ((hasFocus && isSoundTobePlayed) || (!hasFocus && isSoundTobePlayed)) {
            playNewCourseCompleteAudio();
        }
    }

    private void playNewCourseCompleteAudio() {
        try {
            Log.d(TAG, "playNewCourseCompleteAudio:------> ");
            isSoundTobePlayed = false;
            if (showCourseCompletionPopup) {
                //if ((showFinalAnimation) && (!isCourseAlreadyComplete)) {
                if (!isSoundPlayedOnce) {
                    isSoundPlayedOnce = true;
                    if (enableCourseCompleteAudio) {
                        Log.d(TAG, "playNewCourseCompleteAudio: enableCourseCompleteAudio->" + enableCourseCompleteAudio);
                        new Handler().postDelayed(() -> {
                            try {
                                //String filename = OustPreferences.get("DEFAULT_COURSE_AUDIO");
                                boolean isPlayDefault = false;
                                if (OustPreferences.get(COURSE_COMPLETION_AUDIO_PATH) != null) {
                                    File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + OustMediaTools.getMediaFileName(OustPreferences.get(COURSE_COMPLETION_AUDIO_PATH)));
                                    if (file.exists()) {
                                        completionAudioPlayer = MediaPlayer.create(CourseLearningMapActivity.this, Uri.fromFile(file));
                                        completionAudioPlayer.start();
                                    } else {
                                        isPlayDefault = true;
                                    }
                                } else {
                                    isPlayDefault = true;
                                }

                                if (isPlayDefault) {
                                    File file2 = new File(OustSdkApplication.getContext().getFilesDir(), "win_game.mp3");
                                    if (file2.exists()) {
                                        completionAudioPlayer = MediaPlayer.create(CourseLearningMapActivity.this, Uri.fromFile(file2));
                                        completionAudioPlayer.start();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }, ONE_HUNDRED_MILLI_SECONDS);
                    }
                }
                //}
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void openCourseLeaderBoard(CourseDataClass courseDataClass) {
        try {
            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                completionAudioPlayer.stop();
                stopPlayer = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (OustSdkTools.checkInternetStatus() && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                Intent leaderBoardIntent = new Intent(CourseLearningMapActivity.this, CommonLeaderBoardActivity.class);
                leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle containerBundle = new Bundle();
                containerBundle.putString("containerType", "courseLeaderBoard");
                containerBundle.putLong("containerContentId", courseDataClass.getCourseId());
                if ((courseDataClass.getBgImg() != null) && (!courseDataClass.getBgImg().isEmpty())) {
                    containerBundle.putString("contentBgImage", courseDataClass.getBgImg());
                }
                if (courseDataClass.getCourseName() != null) {
                    containerBundle.putString("contentName", courseDataClass.getCourseName());
                }
                leaderBoardIntent.putExtras(containerBundle);
                startActivity(leaderBoardIntent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("InflateParams")
    public void sendCertificatePopup(final CourseDataClass courseDataClass, final boolean isComingFormCourseCompletePopup) {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (certificateEmail_popup == null || !certificateEmail_popup.isShowing()) {
                    certificateEmail_popup = new Dialog(this, R.style.DialogTheme);
                    certificateEmail_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    certificateEmail_popup.setContentView(R.layout.dialog_cpl_certificate);
                    Objects.requireNonNull(certificateEmail_popup.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                    final FrameLayout btnOK = certificateEmail_popup.findViewById(R.id.submit_certificates);
                    final ImageView certificatePopUp_btnClose = certificateEmail_popup.findViewById(R.id.closeBtn);
                    final EditText editText_email = certificateEmail_popup.findViewById(R.id.edittext_email);
                    final TextView certificate_titleText = certificateEmail_popup.findViewById(R.id.certifucate_titletxt);
                    setHeavyFont(certificate_titleText);
                    final TextView certificate_txt = certificateEmail_popup.findViewById(R.id.certificatemsg);

                    setMediumFont(certificate_txt);
                    certificate_titleText.setText(getResources().getString(R.string.your_certificate));
                    certificate_txt.setText(getResources().getString(R.string.enter_email_receive_certificate));

                    if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                        editText_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    editText_email.requestFocus();
                    certificateEmail_popup.show();

                    btnOK.setOnClickListener(view -> {
                        OustSdkTools.oustTouchEffect(view, 200);
                        if (OustSdkTools.isValidEmail((editText_email.getText().toString().trim()))) {
                            sendCertificateToEmail(editText_email.getText().toString().trim(), courseDataClass, isComingFormCourseCompletePopup);
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.enter_valid_mail));
                        }
                    });

                    certificatePopUp_btnClose.setOnClickListener(view -> {
                        hideKeyboard(editText_email);
                        certificateEmail_popup.dismiss();
                        if (isComingFormCourseCompletePopup) {
                            if (isShowRatingPopUp) {
                                showRateCoursePopup(courseDataClass);
                            } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                                startSurvey();
                            }
                        }
                    });
                }
            }, ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setHeavyFont(TextView tv) {
        try {
            if (language == null || language.equalsIgnoreCase("en")) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setMediumFont(TextView tv) {
        try {
            if (language == null || language.isEmpty() || language.equalsIgnoreCase("en")) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void sendCertificateToEmail(String mail, CourseDataClass courseDataClass, final boolean isComingFormCourseCompletePopup) {
        try {
            SendCertificateRequest sendCertificateRequest = new SendCertificateRequest();
            sendCertificateRequest.setStudentid(activeUser.getStudentid());
            sendCertificateRequest.setEmailid(mail);
            sendCertificateRequest.setContentId(("" + courseDataClass.getCourseId()));
            sendCertificateRequest.setContentType("COURSE");
            courseModulePresenter.hitCertificateRequestUrl(sendCertificateRequest, courseDataClass, isComingFormCourseCompletePopup);
            if (certificateEmail_popup != null) {
                certificateEmail_popup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (certificateEmail_popup != null) {
                certificateEmail_popup.dismiss();
            }
        }
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showRateCoursePopup(final CourseDataClass courseDataClass) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final int[] ratingNo = new int[1];
                final Dialog popUpView = new Dialog(CourseLearningMapActivity.this, R.style.DialogTheme);
                popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popUpView.setContentView(R.layout.new_coursecomplete_popup);
                Objects.requireNonNull(popUpView.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                popUpView.setCancelable(false);

                final ImageView rateCourse1 = popUpView.findViewById(R.id.popupratecourse_imga);
                final ImageView rateCourse2 = popUpView.findViewById(R.id.popupratecourse_imgb);
                final ImageView rateCourse3 = popUpView.findViewById(R.id.popupratecourse_imgc);
                final ImageView rateCourse4 = popUpView.findViewById(R.id.popupratecourse_imgd);
                final ImageView rateCourse5 = popUpView.findViewById(R.id.popupratecourse_imge);

                final RelativeLayout rate_main_layout = popUpView.findViewById(R.id.rate_main_layout);
                final EditText feedback_editText = popUpView.findViewById(R.id.feedback_edittext);
                final RelativeLayout ok_layout = popUpView.findViewById(R.id.ok_layout);
                final EditText editText_email = popUpView.findViewById(R.id.edittext_email);
                TextView certificateMsg = popUpView.findViewById(R.id.certificatemsg);
                setMediumFont(certificateMsg);
                TextView certificateTitle = popUpView.findViewById(R.id.certificattitle);
                setHeavyFont(certificateTitle);

                Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);

                ok_layout.setBackground(OustResourceUtils.setDefaultDrawableColor(courseActionDrawable, getResources().getColor(R.color.unselected_text)));


                certificateMsg.setText(getResources().getString(R.string.enter_email_receive_certificate));
                certificateTitle.setText(getResources().getString(R.string.your_certificate));
                if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                    editText_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                }

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                feedback_editText.requestFocus();
                ok_layout.setOnClickListener(view -> {
                    if (rate_main_layout.getVisibility() == View.VISIBLE) {
                        if (ratingNo[0] > 0) {
                            try {
                                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                                eventUpdate.put("CourseRated", true);
                                eventUpdate.put("CourseID", courseId);
                                eventUpdate.put("Course Name", courseDataClass.getCourseName());
                                eventUpdate.put("CourseFeedback", feedback_editText.getText().toString().trim());
                                eventUpdate.put("FeedbackRatingSubmitted", ratingNo[0]);

                                Log.d(TAG, "CleverTap instance: " + eventUpdate);
                                if (clevertapDefaultInstance != null) {
                                    clevertapDefaultInstance.pushEvent("Course_Rating_Feedback", eventUpdate);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }

                            hideKeyboard(feedback_editText);
                            popUpView.dismiss();
                            courseModulePresenter.sendCourseRating(ratingNo[0], courseDataClass, feedback_editText.getText().toString().trim(), courseColId);
                            if (isComingFromCpl) {
                                if (!isSurveyAttached || surveyId < 1) {
                                    finish();
                                }
                            }
                        }
                    }
                });

                rateCourse1.setOnClickListener(view -> {
                    enableNextButton(ok_layout);
                    ratingNo[0] = 1;
                    rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                });
                rateCourse2.setOnClickListener(view -> {
                    enableNextButton(ok_layout);
                    ratingNo[0] = 2;
                    rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                });
                rateCourse3.setOnClickListener(view -> {
                    enableNextButton(ok_layout);
                    ratingNo[0] = 3;
                    rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                });
                rateCourse4.setOnClickListener(view -> {
                    enableNextButton(ok_layout);
                    ratingNo[0] = 4;
                    rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                });
                rateCourse5.setOnClickListener(view -> {
                    enableNextButton(ok_layout);
                    ratingNo[0] = 5;
                    rateCourse1.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse2.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse3.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse4.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                    rateCourse5.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                });

                popUpView.setOnDismissListener(dialog -> {
                    if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                        startSurvey();
                    }
                });

                popUpView.show();

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }, ONE_HUNDRED_MILLI_SECONDS);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void enableNextButton(RelativeLayout ok_layout) {
        Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        ok_layout.setBackground(OustSdkTools.drawableColor(courseActionDrawable));
    }

    private boolean isValidLbResetPeriod() {
        try {
            long courseUniqueNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
            DTOUserCourseData userCourseData = new UserCourseScoreDatabaseHandler().getScoreById(courseUniqueNo);

            String lbResetPeriod = OustPreferences.get(AppConstants.StringConstants.LB_RESET_PERIOD);
            lbResetPeriod = lbResetPeriod.toUpperCase();
            String addedOn = userCourseData.getAddedOn();

            switch (lbResetPeriod) {
                case "MONTHLY":
                    return OustSdkTools.isCurrentMonth(addedOn);
                case "QUARTERLY":
                    return OustSdkTools.isCurrentQuater(addedOn);
                case "YEARLY":
                    return OustSdkTools.isCurrentYear(addedOn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return true;
    }

    public void courseDownloading() {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
            long courseUniqueNo = Long.parseLong(s1);

            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
            mUserCourseData = new DTOUserCourseData();
            mUserCourseData = userCourseData;
            courseModulePresenter.updateLevelDownloadStatus();
            if (userCourseData != null && userCourseData.isDownloading()) {
                Log.d(TAG, "courseDownloading :" + userCourseData.getDownloadCompletePercentage() + " " + userCourseData.getId());
                if (userCourseData.getDownloadCompletePercentage() == 100) {
                    if (downloadAnimation != null) {
                        downloadAnimation.stop();
                        downloadAnimation.selectDrawable(1);
                        if (isRegularMode || isSalesMode) {
                            course_download.setVisibility(View.GONE);
                            course_delete.setVisibility(VISIBLE);
                        }
                    }

                    mUserLevelDataList = new ArrayList<>();
                    mUserCourseData = new DTOUserCourseData();
                    mUserCourseData = userCourseData;
                    mUserLevelDataList = userCourseData.getUserLevelDataList();

                    enableCourseDataDelete();

                } else {
                    disableCourseDataDelete();
                    if (downloadAnimation != null && !downloadAnimation.isRunning()) {
                        downloadAnimation.start();
                    }

                }
            } else {
                setDownloadCourseIcon(userCourseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private float getYPosition(float y1, int scrHeight, int scrollViewHeight) {

        float y = scrollViewHeight - (scrHeight * y1 / 480);
        return (y);
    }

    private float getXPosition(float x1, int scrWidth) {
        return ((scrWidth * x1 / 320));
    }

    private float getXPositionForIndicator(float x1, int scrWidth) {
        return scrWidth * x1 / 320;
    }

    private float getRealYPosition(float y1, int scrHeight) {
        return ((scrHeight * y1 / 480));
    }

    private void setAnimation(View view, int position) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f);
            scaleDownX.setDuration(1000);
            scaleDownY.setDuration(1000);
            scaleDownX.setInterpolator(new LinearInterpolator());
            scaleDownY.setInterpolator(new BounceInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay((150L * (position + 1)));
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openLevelByClick(String levelTxtStr, boolean isClick) {
        try {
            int levelNo = 0;
            try {
                Log.d(TAG, "openLevelByClick: isPurchased-> " + isPurchased + " isLocked-> " + isLocked + " isLearningCardOpen-> " + isLearningCardOpen + " isActiveLive-> " + isActiveLive + " levelTxtStr-> " + levelTxtStr);
                if (!isMicroCourse) {
                    if (levelTxtStr != null && (!levelTxtStr.equalsIgnoreCase("A") && !levelTxtStr.equalsIgnoreCase("S"))) {
                        levelNo = Integer.parseInt(levelTxtStr);
                        if (userCourseData.getUserLevelDataList() != null && userCourseData.getUserLevelDataList().size() > 1) {
                            if (userCourseData.getUserLevelDataList().get(levelNo - 1).isLevelCompleted() || userCourseData.getUserLevelDataList().get(levelNo - 1).getCompletePercentage() >= 100) {
                                OustStaticVariableHandling.getInstance().setLevelCompleted(true);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (isPurchased) {
                if (!isLocked) {
                    if (isClick) {
                        if ((!isLearningCardOpen) && (isActiveLive)) {
                            isLearningCardOpen = true;
                            if (levelTxtStr != null && levelTxtStr.equalsIgnoreCase("A")) {
                                courseModulePresenter.clickOnAssessmentIcon();
                            } else if (levelTxtStr != null && levelTxtStr.equalsIgnoreCase("S")) {
                                courseModulePresenter.clickOnSurveyIcon();
                            } else {
                                if (!isMicroCourse) {
                                    courseModulePresenter.clickOnLevelIcon(levelNo);
                                }
                            }
                        }
                    } else {
                        if ((!isLearningCardOpen) && (isActiveLive)) {
                            if (!isMicroCourse) {
                                if (levelTxtStr != null && levelTxtStr.equalsIgnoreCase("1")) {
                                    isLearningCardOpen = true;
                                    courseModulePresenter.clickOnLevelIcon(levelNo);
                                }
                            }
                        }
                        Log.d(TAG, "openLevelByClick: -auto open-> " + isClick);
                    }
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.complete_course_unlock));
                }
            } else {
                if (isFreeCourse) {
                    OustSdkTools.showToast(getResources().getString(R.string.enroll_course_start));
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.purchase_course));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteConfirmation() {
        try {
            Dialog deleteDialog = new Dialog(CourseLearningMapActivity.this, R.style.DialogTheme);
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
            info_description.setText(getResources().getString(R.string.module_delete_confirmation));
            info_description.setVisibility(View.VISIBLE);
            info_no_text.setText(getResources().getString(R.string.cancel));
            info_yes_text.setText(getResources().getString(R.string.confirm));

            info_yes.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);//change warning drawable
            info_image.setImageDrawable(OustSdkTools.drawableColor(infoDrawable, getResources().getColor(R.color.error_incorrect)));

            info_no.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }

            });

            info_yes.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
                if (mUserLevelDataList != null && mUserLevelDataList.size() > 0) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        try {
                            RoomHelper.getAllCourseInLevel(mUserLevelDataList.get(i).getLevelId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseModulePresenter.updateLevelDownloadStatus();
                        course_delete.setVisibility(View.GONE);
                        course_download.setVisibility(View.VISIBLE);
                        mUserLevelDataList.get(i).setCompletePercentage(0);
                        mUserCourseData.setDownloadCompletePercentage(0);
                        courseModulePresenter.addUserData(mUserCourseData);
                    }
                }
                RoomHelper.downloadedOrNot(courseId);
                onBackPressed();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                completionAudioPlayer.stop();
                stopPlayer = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkMediaExists(String mediaPath, String path, boolean isVideo, DTOCourseCard courseCardClass) {
        try {
            if (mediaPath != null && !mediaPath.isEmpty() && path != null && !path.isEmpty()) {
                if (mediaPath.contains(".zip")) {
                    String newFileName = mediaPath.replace(".zip", "");
                    final File file = new File(OustSdkApplication.getContext().getFilesDir(), newFileName);
                    if (!file.exists()) {
                        downloadFileFromServer(mediaPath, path, isVideo, courseCardClass);
                    } else {
                        alreadyDownloadSize++;
                    }
                } else {
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, mediaPath, isVideo, courseCardClass);
                        } else {
                            alreadyDownloadSize++;
                        }
                    } else {
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, path, isVideo, courseCardClass);
                        } else {
                            alreadyDownloadSize++;
                            Log.e(TAG, "checkMediaExists: file exist-->");
                        }
                    }
                }
            }
            if (pathSize == alreadyDownloadSize && !comingFromIntroCard) {
                alreadyDownloadSize = 0;
                comingFromIntroCard = true;
                branding_mani_layout.setVisibility(View.GONE);
                Intent introIntent = new Intent(CourseLearningMapActivity.this, IntroCardActivity.class);
                Gson gson = new Gson();
                String courseDataStr = gson.toJson(courseCardClass);
                OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                startActivity(introIntent);
            } else if (!isEnrolled && comingFromIntroCard) {
                comingFromIntroCard = false;
                courseModulePresenter.clickOnEnrollLp(true);
                course_intro_card_layout.setClickable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadFileFromServer(String fileName, String pathName, boolean isVideo, DTOCourseCard courseCardClass) {
        try {
            boolean isOustLearn = !isVideo;
            if (isOustLearn) {
                fileName = "oustlearn_" + fileName;
            }
            downloadManager(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + pathName, fileName, courseCardClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadManager(String downloadUrl, String fileName, DTOCourseCard courseCardClass) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
                        @SuppressLint("Range") int idFromCursor = cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_ID));
                        if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                            @SuppressLint("Range") String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //update percentage
                            Log.i("DownloadHandler", "Download completed..--<> " + uriString);
                            if (uriString != null) {
                                File sourceFile = new File(Uri.parse(uriString).getPath());
                                File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                try {
                                    OustSdkTools.copyFile(sourceFile, destinationFile);
                                    //TODO handle intro card intent
                                    downloadedSize++;
                                    if (pathSize == downloadedSize && !comingFromIntroCard) {
                                        downloadedSize = 0;
                                        comingFromIntroCard = true;
                                        branding_mani_layout.setVisibility(View.GONE);
                                        Intent introIntent = new Intent(CourseLearningMapActivity.this, IntroCardActivity.class);
                                        Gson gson = new Gson();
                                        String courseDataStr = gson.toJson(courseCardClass);
                                        OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                                        startActivity(introIntent);
                                    } else if (!isEnrolled && comingFromIntroCard) {
                                        comingFromIntroCard = false;
                                        courseModulePresenter.clickOnEnrollLp(true);
                                        course_intro_card_layout.setClickable(true);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }

                        } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                            int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                            int reason = cur.getInt(columnReason);
                            switch (reason) {

                                case DownloadManager.ERROR_FILE_ERROR:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                    //handle error
                                    break;

                                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_UNKNOWN:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_CANNOT_RESUME:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                    //handle error
                                    break;
                                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                    //handle error
                                    break;
                                case 404:
                                    break;

                                default:
                                    throw new IllegalStateException("Unexpected value: " + reason);
                            }
                        }
                    }
                    cur.close();
                }
            }
        };
        context.getApplicationContext().registerReceiver(receiver, filter);
    }
}

