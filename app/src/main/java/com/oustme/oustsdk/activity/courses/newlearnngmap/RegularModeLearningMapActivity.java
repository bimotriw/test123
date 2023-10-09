package com.oustme.oustsdk.activity.courses.newlearnngmap;

import static com.oustme.oustsdk.downloadHandler.DownloadForegroundService.START_UPLOAD;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._CANCELED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._FAILED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.COURSE_COMPLETION_AUDIO_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.TOOL_BAR_COLOR_CODE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentPlayActivity;
import com.oustme.oustsdk.activity.assessments.AssessmentResultActivity;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.activity.courses.learningmapmodule.LearningMapModuleActivity;
import com.oustme.oustsdk.adapter.courses.RegularModeAdapter;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.customviews.CustomTextView;
import com.oustme.oustsdk.customviews.MyCustomLayoutManager;
import com.oustme.oustsdk.customviews.NewSimpleLine;
import com.oustme.oustsdk.downloadHandler.DownloadForegroundService;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseDesclaimerData;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.SearchCourseLevel;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.interfaces.course.LearningCallBackInterface;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReviewModeCallBack;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.model.response.course.UserEventCourseData;
import com.oustme.oustsdk.reminderNotification.ReminderNotificationManager;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.AssessmentNavModel;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.survey_ui.SurveyDetailActivity;
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
import com.oustme.oustsdk.tools.filters.LevelFilter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class RegularModeLearningMapActivity extends BaseActivity implements LearningMapView, View.OnClickListener, ReviewModeCallBack, View.OnKeyListener, LearningModuleInterface, LearningCallBackInterface, AssessmentResultActivity.updateLevel, VideoRendererEventListener {
    private static final String TAG = "RegularModeLearningMapA";
    private ProgressBar lpmain_loader;
    private RelativeLayout mainloader_back, learninglevel_back, learninglevel_backa, mainlearning_maplayout, mRelativeLayoutLevelDesc,
            main_usermanual_layout, um_serachviewlayout, course_desclaimer_popup;
    //private  RelativeLayout layout_banner_top;
    private TextView learningmain_lpnametext, currentmodule_completeval, lpoctext,
            description_heading, currentmodule_completetext, okbutton_text, download_course_text;
    private ImageView lpocimage, leaderboard_iv, learningmap_backimg, um_search_button, um_serachclose, course_certificate_iv,
            popup_agree_checkbox, bulletin_iv, course_info_btna, course_info_iv;
    private LinearLayout bulletin_board, leaderboard_button, course_certificate_2, learning_closebtn, course_info_bottom_layout;

    private SwitchCompat reviewmode_switch;
    private RelativeLayout reviewmode_switchlayout, learningmap_subtoplayout, learningmap_toplayout,
            lpoc_ll, learningmap_bottomlayout, popup_agree_layout, popup_ok_layout, desclaimer_popup_inside_roundedcornerlayout, reviewmode_switchmainlayout;
    private TextView reviewmode_text, playmode_text, learningmain_lpnametexta, description_okbutton_text;
    private ImageButton um_serachbackbtn;

    private NewSimpleLine simpleLine;
    private ScrollView viewclass_scrollview;
    private LinearLayout learningmap_networkbarlayout;
    private RecyclerView usermanual_recyclerview;
    private SwipeRefreshLayout swipe_refresh_layout;
    private EditText um_serachedittext;
    private TextView um_notopic_text, popup_headingline, popup_heading, popup_content, popup_agree_checkboxtext,
            popup_okbutton_text, heading, headingline, agree_checkboxtext;

    private RelativeLayout card_layout, feedcard_toplayout, closecard_layout, closecard_layout_left, downloadscreen_layout, download_course_layout;
    private ImageView download_course_img;
    private GifImageView downloding_course_gif;

    private RegularModeLearningMapPresenter presenter;
    private LayoutInflater mInflater;
    private int levelboxSize = 0;
    private View levelIndicatorView;
    private MyDownloadReceiver receiver;
    private CourseDownloadReceiver courseDownloadReceiver;
    private String courseColnId;
    private boolean purchased, isReviewMode, isRegularMode, isSalesMode;
    private boolean locked;
    private boolean allCourseDownloadStarted = false;

    private ActiveUser activeUser;
    private boolean iscertificatebtnclicked = false, isRateCourseShownOnce = false, isSoundPlayedOnce = false, isSoundTobePlayed = false;
    private String learningId, multilingualID;
    private boolean isMultilingual = false;
    private ImageView close_card_right, close_card_left;

    private boolean freeCourse;
    private Map<String, Object> cardInfo;
    private boolean isCourseAlreadyComplete = false;
    private String language;
    private boolean rateCourse = true;
    private DownloadFiles downloadFiles;
    private DTOCourseCard courseCardClass1;
    private boolean downloaded;
    private ImageView mImageViewDeleteCourseData;
    private AlertDialog mAlertDialogForDelete;
    private List<DTOUserLevelData> mUserLevelDataList;
    private DTOUserCourseData mUserCourseData;
    private CustomTextView mTextViewCourseTitle;
    private CustomTextView mTextViewCourseDescription;
    private ConstraintLayout mCourseBannerConstraintLyt;
    private ProgressBar mcompletedProgressBar;
    private LinearLayout mLearningmap_maintoplayout;
    private boolean isLoadingFirstTime = false;
    private boolean showCourseCompletionPopup = false;
    //private ImageView imageviewBack;
    private ImageView imageView_course_banner;
    //private boolean ifScormEventBased = false;

    private boolean enableVideoDownload;
    private boolean isEvent;
    private int eventId = 0;
    private boolean isCallbackHappened = false;
    private boolean isSurveyAttached;
    private boolean isSurveyOpened;
    private long assessmentId = 0;
    private long surveyId = 0;
    private long courseId = 0;
    private boolean startSurveyImmediately = true;
    UserEventAssessmentData userEventAssessmentData = new UserEventAssessmentData();

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private CourseDataClass courseDataClass;
    boolean isCourseCompleted = false;
    private boolean isScoreDisplaySecondTime;
    //private boolean disableCourseCompleteAudio=false;
    boolean enableCourseCompleteAudio = true, enableLevelCompleteAudio = true;
    MediaPlayer completionAudioPlayer;

    boolean isInstrumentationHit = false;

    @Override
    protected void onStart() {
        super.onStart();
        setReceiver();
        if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected int getContentView() {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            OustAppState.getInstance().setLearningCallBackInterface(this);
//            OustGATools.getInstance().reportPageViewToGoogle(RegularModeLearningMapActivity.this, "Course Learning Map Page");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return R.layout.regular_learningmap;
    }

    @Override
    protected void initView() {

        try {
            OustSdkTools.setLocale(RegularModeLearningMapActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        isLoadingFirstTime = true;
        lpmain_loader = findViewById(R.id.lpmain_loader);
        setLoaderColor(lpmain_loader);
        mCourseBannerConstraintLyt = findViewById(R.id.regular_mode_assessment_banner_root_container);
        imageView_course_banner = findViewById(R.id.imageView_course_banner);
        mainloader_back = findViewById(R.id.mainloader_back);
        mImageViewDeleteCourseData = findViewById(R.id.deleteCourseData);
        mImageViewDeleteCourseData.setVisibility(View.INVISIBLE);
        learningmain_lpnametext = findViewById(R.id.learningmain_lpnametext);
        currentmodule_completeval = findViewById(R.id.completedPercentage);
        lpoctext = findViewById(R.id.lpoctext);
        description_heading = findViewById(R.id.description_heading);
        currentmodule_completetext = findViewById(R.id.currentmodule_completetext);
        okbutton_text = findViewById(R.id.okbutton_text);
        popup_okbutton_text = findViewById(R.id.okbutton_text);
        description_okbutton_text = findViewById(R.id.description_okbutton_text);
        reviewmode_text = findViewById(R.id.reviewmode_text);
        playmode_text = findViewById(R.id.playmode_text);
        learningmain_lpnametexta = findViewById(R.id.learningmain_lpnametexta);
        um_notopic_text = findViewById(R.id.um_notopic_text);
        popup_headingline = findViewById(R.id.headingline);
        popup_heading = findViewById(R.id.heading);
        popup_content = findViewById(R.id.content);
        popup_agree_checkboxtext = findViewById(R.id.agree_checkboxtext);
        description_heading = findViewById(R.id.description_heading);
        agree_checkboxtext = findViewById(R.id.agree_checkboxtext);
        headingline = findViewById(R.id.headingline);
        heading = findViewById(R.id.heading);


        um_serachedittext = findViewById(R.id.um_serachedittext);
        learning_closebtn = findViewById(R.id.learning_closebtn);
        //learning_closebtn.bringToFront();

        simpleLine = findViewById(R.id.simplelineview);
        viewclass_scrollview = findViewById(R.id.viewclass_scrollview);
        learninglevel_back = findViewById(R.id.learninglevel_back);
        learninglevel_backa = findViewById(R.id.learninglevel_backa);
        lpocimage = findViewById(R.id.lpocimage);
        leaderboard_button = findViewById(R.id.leaderboard_button);
        leaderboard_iv = findViewById(R.id.leaderboard_iv);
        learningmap_networkbarlayout = findViewById(R.id.learningmap_networkbarlayout);
        learningmap_backimg = findViewById(R.id.learningmap_backimg);
        um_serachbackbtn = findViewById(R.id.um_serachbackbtn);

        //to display downloading content before and after and also while downloading playing gif
        download_course_layout = findViewById(R.id.download_course_layout);
        download_course_img = findViewById(R.id.download_course);
        download_course_text = findViewById(R.id.download_course_text);
        downloding_course_gif = findViewById(R.id.downloding_course_gif);

        mainlearning_maplayout = findViewById(R.id.mainlearning_maplayout);
        main_usermanual_layout = findViewById(R.id.main_usermanual_layout);
        usermanual_recyclerview = findViewById(R.id.usermanual_recyclerview);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        um_search_button = findViewById(R.id.um_search_button);
        um_serachclose = findViewById(R.id.um_serachclose);
        reviewmode_switchlayout = findViewById(R.id.reviewmode_switchlayout);
        reviewmode_switchmainlayout = findViewById(R.id.reviewmode_switchmainlayout);

        card_layout = findViewById(R.id.card_layout);
        feedcard_toplayout = findViewById(R.id.feedcard_toplayout);
        closecard_layout = findViewById(R.id.closecard_layout);
        closecard_layout_left = findViewById(R.id.closecard_layout_left);
        close_card_left = findViewById(R.id.close_card_left);
        close_card_right = findViewById(R.id.close_card_right);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);

        um_serachviewlayout = findViewById(R.id.um_serachviewlayout);
        course_certificate_2 = findViewById(R.id.course_certificate_2);
        course_certificate_iv = findViewById(R.id.course_certificate_iv);
        learningmap_subtoplayout = findViewById(R.id.learningmap_subtoplayout);
        learningmap_toplayout = findViewById(R.id.learningmap_toplayout);
        learningmap_bottomlayout = findViewById(R.id.learningmap_bottomlayout);
        course_desclaimer_popup = findViewById(R.id.course_desclaimer_popup);
        popup_agree_layout = findViewById(R.id.agree_layout);
        popup_ok_layout = findViewById(R.id.ok_layout);
        popup_agree_checkbox = findViewById(R.id.agree_checkbox);
        desclaimer_popup_inside_roundedcornerlayout = findViewById(R.id.desclaimer_popup_inside_roundedcornerlayout);
        course_info_btna = findViewById(R.id.course_info_btna);

        //layout_banner_top = (RelativeLayout) findViewById(R.id.layout_banner_top);
        bulletin_board = findViewById(R.id.bulletin_board);
        bulletin_iv = findViewById(R.id.bulletin_iv);
        lpoc_ll = findViewById(R.id.lpoc_ll);
        lpoc_ll.setVisibility(View.GONE);

        course_info_bottom_layout = findViewById(R.id.course_info_bottom_layout);
        course_info_iv = findViewById(R.id.course_info_iv);

        mTextViewCourseTitle = findViewById(R.id.textViewCourseTitle);
        mTextViewCourseDescription = findViewById(R.id.textViewCourseDescription);

        mcompletedProgressBar = findViewById(R.id.completedProgressBar);
        //OustSdkTools.setDefaultPrograssBar(mcompletedProgressBar);

        /*imageviewBack = findViewById(R.id.imageviewBack);
        imageviewBack.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_arrow_back));*/

        mLearningmap_maintoplayout = findViewById(R.id.learningmap_maintoplayout);

        //OustSdkTools.setImage(leaderboard_iv, getResources().getString(R.string.learning_leaderboard));
        reviewmode_switch = findViewById(R.id.reviewmode_switch);
        leaderboard_button.setOnClickListener(this);
        learning_closebtn.setOnClickListener(this);
        lpocimage.setOnClickListener(this);
        um_search_button.setOnClickListener(this);
        um_serachclose.setOnClickListener(this);
        bulletin_board.setOnClickListener(this);
        um_serachbackbtn.setOnClickListener(this);
        download_course_layout.setOnClickListener(this);

        try {
            OustSdkTools.setImage(learningmap_backimg, getResources().getString(R.string.bg_1));
            //OustSdkTools.setImage(lpocimage, getResources().getString(R.string.coins_icon));
            OustSdkTools.setImage(course_certificate_iv, getResources().getString(R.string.certificate_icona));
            OustSdkTools.setImage(course_info_btna, getResources().getString(R.string.infoicon));
            OustSdkTools.setImage(course_info_iv, getResources().getString(R.string.infoicon));
            //OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bulletinboard));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        course_info_btna.setOnClickListener(this);

        isCallbackHappened = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            unregisterReceiver(receiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mImageViewDeleteCourseData.setVisibility(View.INVISIBLE);
            isLearningCardOpen = false;
            showFinalAnimation = false;
            OustSdkTools.setSnackbarElements(learningmap_networkbarlayout, RegularModeLearningMapActivity.this);
            if (OustStaticVariableHandling.getInstance().isComingFromAssessment() && presenter != null) {
                Log.d(TAG, "onResume: cmoing from assessment");
                OustStaticVariableHandling.getInstance().setComingFromAssessment(false);
                //presenter.startLearningMap(false);

                boolean isAssessmentCompleted = false;
                if (OustStaticVariableHandling.getInstance().isAssessmentCompleted()) {
                    //setCurrentModuleCompleteLevel(100);
                    isAssessmentCompleted = true;

                    String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                    long courseUniqNo = Long.parseLong(s1);
                    UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                    DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                    userCourseData.setMappedAssessmentCompleted(true);
                    userCourseData.setPresentageComplete(100);
                    userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqNo);
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                }
                if (isAssessmentCompleted) {
                    isCallbackHappened = true;
                    if (OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                        UserEventCourseData userEventCourseData = new UserEventCourseData();
                        userEventCourseData.setEvenId(eventId);
                        userEventCourseData.setCourseId(Long.parseLong(learningId));
                        userEventCourseData.setUserProgress("COMPLETED");
                        userEventCourseData.setUserCompletionPercentage(100);

                        CourseDataClass courseDataClass = presenter.getCourseDataClass();
                        if (OustAppState.getInstance().getAssessmentFirebaseClass() != null && OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData() != null) {
                            userEventAssessmentData = OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData();
                            OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(null);
                        } else {
                            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                            long courseUniqNo = Long.parseLong(s1);
                            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);

                            if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0 && userCourseData != null) {
                                //UserEventAssessmentData userEventAssessmentData = new UserEventAssessmentData();
                                userEventAssessmentData.setAssessmentId(courseDataClass.getMappedAssessmentId());
                                //userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());

                                userEventAssessmentData.setUserProgress("DISTRIBUTED");
                                if (userCourseData.getPresentageComplete() >= 100 && userCourseData.isMappedAssessmentCompleted()) {
                                    userEventAssessmentData.setUserCompletionPercentage(100);
                                    userEventAssessmentData.setUserProgress("COMPLETED");
                                    //userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());
                                } else {
                                    userEventAssessmentData.setUserCompletionPercentage(0);
                                    if (userCourseData.getPresentageComplete() >= 95 || courseDataClass.isMappedAssessmentEnrolled()) {
                                        userEventAssessmentData.setUserProgress("INPROGRESS");
                                    }
                                }
                                //userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                            }
                        }
                        if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                            userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                        }

                        if (isSurveyAttached && surveyId != 0 && !isSurveyOpened) {

                            if (userEventCourseData.getUserCompletionPercentage() == 100) {
                                checkforSavedAssessment(activeUser);
                            }
                        } else {
                            if (isComingFromCpl) {
                                UserEventCplData userEventCplData = new UserEventCplData();
                                userEventCplData.setEventId(eventId);
                                userEventCplData.setCurrentModuleId(Long.parseLong(learningId));
                                userEventCplData.setCurrentModuleType("Course");
                                userEventCplData.setCurrentModuleProgress("Completed");
                                userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                                final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                                final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");

                                userEventCplData.setnTotalModules(totalModules);
                                userEventCplData.setnModulesCompleted(completedModules + 1);
                                if (completedModules >= totalModules) {
                                    userEventCplData.setUserProgress("COMPLETED");
                                } else {
                                    userEventCplData.setUserProgress("INPROGRESS");
                                }
                                userEventCplData.setUserEventCourseData(userEventCourseData);
                                OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                                finish();
                            } else {
                                OustStaticVariableHandling.getInstance().getOustApiListener().onCourseProgress(userEventCourseData);
                            }
                        }


                    } else {
                        if (isSurveyAttached && surveyId != 0 && !isSurveyOpened) {

                            checkforSavedAssessment(activeUser);
                        }
                    }
                }
            }

            //setReceiver();
            //courseDownloading();

            if (isLoadingFirstTime) {
                isLoadingFirstTime = false;
            } else {
                //updateReviewList();
                //refreshData();
                presenter.startLearningMap(false);

                /*if(presenter!=null) {
                    int currentLpId = Integer.valueOf(learningId);
                    presenter.getLearningMap(currentLpId);
                }*/
            }
            courseDownloading();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void refreshData() {
        this.realmUserCourseData = RealmHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
    }*/


    @Override
    protected void initData() {
        initMap();
    }

    @Override
    protected void initListener() {
        mImageViewDeleteCourseData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmAlert();
            }
        });
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setFontAndText() {
        learningmain_lpnametext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        currentmodule_completeval.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        lpoctext.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        description_heading.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        currentmodule_completetext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        okbutton_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        popup_okbutton_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        description_okbutton_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        reviewmode_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        playmode_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        learningmain_lpnametexta.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        um_notopic_text.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        popup_headingline.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        popup_heading.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        popup_content.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        popup_agree_checkboxtext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        heading.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        headingline.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        agree_checkboxtext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        setText();
    }

    private void setLoaderColor(ProgressBar progressBar) {
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        Log.d(TAG, "setLoaderColor: " + toolbarColorCode);
        if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(toolbarColorCode), PorterDuff.Mode.SRC_IN);
        }
    }

    private boolean courseAlreadyDownloaded = false;

    @Override
    public void setDownloadCourseIcon(DTOUserCourseData userCourseData) {
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

        download_course_text.setText("");
        download_course_text.setVisibility(View.GONE);
        //download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
        Log.d(TAG, "setDownloadCourseIcon:" + downloaded);
        if (downloaded) {
            mUserLevelDataList = new ArrayList<>();
            mUserCourseData = new DTOUserCourseData();
            mUserCourseData = userCourseData;
            mUserLevelDataList = userCourseData.getUserLevelDataList();
            enableCourseDataDelete();
            download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));

            String colr = OustPreferences.get(TOOL_BAR_COLOR_CODE);
            /*VectorChildFinder vector = new VectorChildFinder(this, R.drawable.ic_download_orange, download_course_img);

            VectorDrawableCompat.VFullPath path1 = vector.findPathByName("path1");
            path1.setFillColor(Color.RED);*/
            // download_course_img.setColorFilter(Color.parseColor(colr));
            //download_course_img.setColorFilter(ContextCompat.getColor(context, R.color.Orange), android.graphics.PorterDuff.Mode.SRC_IN);
            //download_course_img.setColorFilter(ContextCompat.getColor(this, R.color.dGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            downloding_course_gif.setVisibility(View.GONE);
            download_course_img.setVisibility(View.VISIBLE);
            userCourseData.setDownloadCompletePercentage(100);
            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
            //int courseUniqNo = Integer.parseInt(s1);
            long courseUniqNo = Long.parseLong(s1);
            Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);

            userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqNo);
            courseAlreadyDownloaded = true;
        } else {
            disableCourseDataDelete();
            download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
            downloding_course_gif.setVisibility(View.GONE);
            download_course_img.setVisibility(View.VISIBLE);
        }
    }

    private void disableCourseDataDelete() {
        mImageViewDeleteCourseData.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setReviewModeStatus(boolean salesMode) {
        try {
            if (reviewmode_switchlayout.getVisibility() == View.GONE) {
                um_search_button.setVisibility(View.GONE);
                learningmain_lpnametexta.setVisibility(View.GONE);
                //learningmap_subtoplayout.setVisibility(View.VISIBLE);
                um_serachedittext.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
                if (!salesMode) {
                    reviewmode_switchlayout.setVisibility(View.VISIBLE);
                    reviewmode_switch.setChecked(isReviewMode);
                    reviewmode_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewmode_switch.setChecked(true);
                        }
                    });
                    playmode_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reviewmode_switch.setChecked(false);
                        }
                    });
                    reviewmode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                showCourseReviewLayout();
                                isReviewMode = true;
                                presenter.setReviewModeStatus(true);
                            } else {
                                hideCourseReviewLayout();
                                isReviewMode = false;
                                presenter.setReviewModeStatus(false);
                            }
                            reviewmode_switchmainlayout.bringToFront();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showBulletinBoard() {
        mLearningmap_maintoplayout.setVisibility(View.VISIBLE);
        bulletin_board.setVisibility(View.VISIBLE);
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
    public void setText() {
        currentmodule_completetext.setText(getResources().getString(R.string.complete));
        playmode_text.setText(getResources().getString(R.string.play_text));
        reviewmode_text.setText(getResources().getString(R.string.review_text));
        heading.setText(getResources().getString(R.string.undertaking));
        headingline.setText(getResources().getString(R.string.undertaking));
        agree_checkboxtext.setText(getResources().getString(R.string.agree_to_terms));
        okbutton_text.setText(getResources().getString(R.string.ok));
        description_heading.setText(getResources().getString(R.string.descriptions));
        description_okbutton_text.setText(getResources().getString(R.string.ok));
    }

    @Override
    public void showLeaderBoard() {
        mLearningmap_maintoplayout.setVisibility(View.VISIBLE);
        leaderboard_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCoins() {
        mLearningmap_maintoplayout.setVisibility(View.VISIBLE);
        lpoc_ll.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotoRegularModeActivity(int learninPathId) {

    }

    @Override
    public void setLearningCardOpen(boolean isLearningCardOpen) {

    }

    @Override
    public void gotoSurveyPage() {

    }

    private boolean isComingFromCpl = false;

    //    ================================================================================================
    public void initMap() {
        Log.d(TAG, "initMap: ");
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                Log.e(TAG, "active user is not null ");
                OustSdkApplication.setmContext(RegularModeLearningMapActivity.this);
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustFirebaseTools.initFirebase();
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            Intent CallingIntent = getIntent();
//            freeCourse = CallingIntent.getBooleanExtra("freeCourse", false);
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;

            learningId = CallingIntent.getStringExtra("learningId");
            multilingualID = CallingIntent.getStringExtra("multilingualId");
            isMultilingual = CallingIntent.getBooleanExtra("isMultiLingual", false);
            isComingFromCpl = CallingIntent.getBooleanExtra("isComingFromCpl", false);
            rateCourse = CallingIntent.getBooleanExtra("rateCourse", true);
            isReviewMode = false;
            isRegularMode = true;
            isSalesMode = false;
            mainlearning_maplayout.setVisibility(View.VISIBLE);
            main_usermanual_layout.setVisibility(View.GONE);
            purchased = true;
            locked = false;
            if (CallingIntent.getStringExtra("courseColnId") != null) {
                courseColnId = CallingIntent.getStringExtra("courseColnId");
                purchased = CallingIntent.getBooleanExtra("purchased", false);
                locked = CallingIntent.getBooleanExtra("locked", true);
                leaderboard_button.setVisibility(View.GONE);
            }
            Log.d(TAG, "initMap: scr width:" + scrWidth + " scr height:" + scrHeight);
            if (scrWidth > scrHeight) {
                scrHeight = scrWidth;
                scrWidth = metrics.heightPixels;
            }

            if (CallingIntent.hasExtra("isEventLaunch")) {
                isEvent = CallingIntent.getBooleanExtra("isEventLaunch", false);
                eventId = CallingIntent.hasExtra("eventId") ? CallingIntent.getIntExtra("eventId", 0) : 0;
            }

            presenter = new RegularModeLearningMapPresenter(RegularModeLearningMapActivity.this, CallingIntent.getStringExtra("learningId"), scrWidth, scrHeight, CallingIntent, courseColnId);
            setStartingLoader();
            int currentLpId = Integer.valueOf(learningId);
            OustPreferences.save("current_course_id", learningId);
            presenter.getLearningMap(currentLpId);
            presenter.getBulletinQuesFromFirebase(learningId);
            try {
                OustSdkTools.speakInit();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            Log.e(TAG, "caught exception in initMap() ", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showHideCourseInfo() {
        if ((cardInfo != null) && (!cardInfo.isEmpty())) {
            course_info_btna.setVisibility(View.VISIBLE);
            course_info_btna.setOnClickListener(this);
            course_info_bottom_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void checkMultiLingualCourse() {
        if (isMultilingual && Long.parseLong(multilingualID) > 0 && !isAnyIntroPopupVisible) {
            isMultilingual = false;
            presenter.clickOnEnrolleLp(true);
        } else {
            if (isComingFromCpl && !isAnyIntroPopupVisible) {
                presenter.clickOnEnrolleLp(true);
            }
        }
    }

    @Override
    public void showCourseReviewLayout() {
        try {
            main_usermanual_layout.bringToFront();
            learningmap_toplayout.bringToFront();
            learningmap_bottomlayout.bringToFront();
            //learning_closebtn.bringToFront();

            um_search_button.setVisibility(View.VISIBLE);
            Animation event_animmovein = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.alphareverse_anim);
            Animation event_animmoveout = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.alphaanim);
            mainlearning_maplayout.startAnimation(event_animmoveout);
            main_usermanual_layout.startAnimation(event_animmovein);
            event_animmovein.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    main_usermanual_layout.setVisibility(View.VISIBLE);
                    mainlearning_maplayout.setVisibility(View.GONE);
                    showHideCourseInfo();
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

    private boolean checkBoxStatus;

    public void setDesclaimerPopup(final CourseDesclaimerData courseDesclaimerData) {
        try {
            isAnyIntroPopupVisible = true;
            course_desclaimer_popup.setVisibility(View.VISIBLE);
            if (OustPreferences.get("toolbarColorCode") != null) {
                int color = Color.parseColor(OustPreferences.get("toolbarColorCode"));
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.desclaimer_popup_inside_roundedcorner);
                drawable.setStroke(3, color);
                OustSdkTools.setLayoutBackgroudDrawable(desclaimer_popup_inside_roundedcornerlayout, drawable);
            }
            if (courseDesclaimerData != null) {
                if (courseDesclaimerData.getHeader() != null) {
                    popup_heading.setText(courseDesclaimerData.getHeader());
                    popup_headingline.setText(courseDesclaimerData.getHeader());
                }
                if (courseDesclaimerData.getContent() != null) {
                    popup_content.setText(courseDesclaimerData.getContent());
                    popup_content.setMovementMethod(new ScrollingMovementMethod());
                }
                if ((courseDesclaimerData.getCheckBoxText() != null) && (!courseDesclaimerData.getCheckBoxText().isEmpty())) {
                    popup_agree_layout.setVisibility(View.VISIBLE);
                    popup_agree_checkboxtext.setText(courseDesclaimerData.getCheckBoxText());
                } else {
                    popup_agree_layout.setVisibility(View.GONE);
                    setCustomButtonBackground(popup_ok_layout);
                    OustSdkTools.setLayoutBackgroud(popup_ok_layout, R.drawable.disclaimer_button_rounded_corer);
                }
                if ((courseDesclaimerData.getBtnText() != null) && (!courseDesclaimerData.getBtnText().isEmpty())) {
                    popup_okbutton_text.setText(courseDesclaimerData.getBtnText());
                }
            }
            course_desclaimer_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            popup_ok_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBoxStatus) {
                        presenter.sendAcknowledgedRequest(courseColnId, learningId);
                        course_desclaimer_popup.setVisibility(View.GONE);
                        if ((cardInfo != null) && (!cardInfo.isEmpty())) {
                            checkForDescriptionCardData(cardInfo);
                        } else {
                            presenter.clickOnEnrolleLp(true);
                        }
                    }
                }
            });
            popup_agree_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!checkBoxStatus) {
                        checkBoxStatus = true;
                        popup_agree_checkbox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_check_box_green));
                        setCustomButtonBackground(popup_ok_layout);
                    } else {
                        checkBoxStatus = false;
                        popup_agree_checkbox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_check_box_black));
                        OustSdkTools.setLayoutBackgroud(popup_ok_layout, R.drawable.disclaimer_button_roundedcorner_disabled);
                    }
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


    private void hideCourseReviewLayout() {
        try {
            mainlearning_maplayout.bringToFront();
            learningmap_toplayout.bringToFront();
            learningmap_bottomlayout.bringToFront();
            //learning_closebtn.bringToFront();

            um_serachedittext.setText("");
            um_serachviewlayout.setVisibility(View.GONE);
            um_search_button.setVisibility(View.GONE);
            Animation event_animmovein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alphareverse_anim);
            Animation event_animmoveout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alphaanim);
            mainlearning_maplayout.startAnimation(event_animmovein);
            main_usermanual_layout.startAnimation(event_animmoveout);
            event_animmovein.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    main_usermanual_layout.setVisibility(View.GONE);
                    mainlearning_maplayout.setVisibility(View.VISIBLE);
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

    private void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoLeaderBoard(int currentLearningPathId, String lpName, String bgimg) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            Intent intent = new Intent(RegularModeLearningMapActivity.this, EventLeaderboardActivity.class);
            intent.putExtra("lpleaderboardId", "" + currentLearningPathId);
            if ((bgimg != null) && (!bgimg.isEmpty())) {
                intent.putExtra("coursebgImg", bgimg);
            }
            if (lpName != null) {
                intent.putExtra("lpname", lpName);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoBulletinBoard(int currentLearningPathId, String lpName) {
        Intent intent = new Intent(RegularModeLearningMapActivity.this, BulletinBoardQuestionActivity.class);
        intent.putExtra("courseId", "" + currentLearningPathId);
        if (lpName != null) {
            intent.putExtra("courseName", lpName);
        }
        startActivity(intent);
    }

    public void setStartingLoader() {
        try {
            if (isReviewMode) {
                swipe_refresh_layout.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
                swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipe_refresh_layout.setRefreshing(false);
                    }
                });
                swipe_refresh_layout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipe_refresh_layout.setRefreshing(true);
                    }
                });
            } else {
                ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(lpmain_loader, "rotation", 0f, 360f);
                imageViewObjectAnimator.setDuration(1500); // miliseconds
                imageViewObjectAnimator.setRepeatCount(8);
                imageViewObjectAnimator.start();
                mainloader_back.setVisibility(View.VISIBLE);
                mainloader_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private CourseDesclaimerData courseDesclaimerData;

    @Override
    public void extractCourseData(final int learningPathId, Map<String, Object> learingMap, Map<String, Object> landingMap) {
        try {

            courseDataClass = new CourseDataClass();
            if (learingMap.get("courseId") != null) {
                Log.d(TAG, "extractCourseData: courseID" + learingMap.get("courseId"));
                courseDataClass.setCourseId(OustSdkTools.convertToLong(learingMap.get("courseId")));
                courseId = OustSdkTools.convertToLong(learingMap.get("courseId"));
            }

            if (learingMap.get("contentDuration") != null) {
                courseDataClass.setContentDuration((long) learingMap.get("contentDuration"));
            }

            boolean isAdaptiveCourse = false;
            if (learingMap.get("courseType") != null) {
                if (learingMap.get("courseType").toString().equalsIgnoreCase("ADAPTIVE")) {
                    isAdaptiveCourse = true;
                    //  download_course_layout.setVisibility(View.INVISIBLE);

                } /*else {
                    //download_course_layout.setVisibility(View.VISIBLE);
                    isAdaptiveCourse = false;
                }*/
            }

            if (learingMap.get(AppConstants.StringConstants.SURVEY_ATTACHED) != null) {
                isSurveyAttached = (boolean) learingMap.get(AppConstants.StringConstants.SURVEY_ATTACHED);
            }
            if (learingMap.get("startSurveyImmediately") != null) {
                startSurveyImmediately = (boolean) learingMap.get("startSurveyImmediately");
            }

            if (learingMap.get(AppConstants.StringConstants.SURVEY_ID) != null) {
                surveyId = (long) learingMap.get(AppConstants.StringConstants.SURVEY_ID);
            }

            if (learingMap.get("showNudgeMessage") != null) {
                courseDataClass.setShowNudgeMessage((boolean) learingMap.get("showNudgeMessage"));
            }

            presenter.setAdaptive(isAdaptiveCourse);

            if (learingMap.get("autoDownload") != null) {
                courseDataClass.setAutoDownload((boolean) (learingMap.get("autoDownload")));
            }

            if (learingMap.get("showCourseCompletionPopup") != null) {
                showCourseCompletionPopup = (boolean) (learingMap.get("showCourseCompletionPopup"));
                courseDataClass.setShowCourseCompletionPopup(showCourseCompletionPopup);
            }

            if (learingMap.get("autoPlay") != null) {
                courseDataClass.setAutoPlay((boolean) (learingMap.get("autoPlay")));
            }
            if (learingMap.get("courseName") != null) {
                Log.d(TAG, "extractCourseData: coyrsename:" + learingMap.get("courseName"));
                courseDataClass.setCourseName((String) learingMap.get("courseName"));
            }

            if (learingMap.get("badgeName") != null) {
                Log.d(TAG, "extractCourseData: badgeName:" + learingMap.get("badgeName"));
                courseDataClass.setBadgeName((String) learingMap.get("badgeName"));
            }

            if (learingMap.get("badgeIcon") != null) {
                Log.d(TAG, "extractCourseData: badgeIcon:" + learingMap.get("badgeIcon"));
                courseDataClass.setBadgeIcon((String) learingMap.get("badgeIcon"));
            }

            if (learingMap.get("updateTS") != null) {
                Log.d(TAG, "extractCourseData: updateTS:" + learingMap.get("updateTS"));
                courseDataClass.setUpdateTs((String) learingMap.get("updateTS"));
            }
            if (learingMap.get("showVirtualCurrency") != null) {
                Log.d(TAG, "learingMap.get(\"showVirtualCurrency\")->" + learingMap.get("showVirtualCurrency"));
                courseDataClass.setShowVirtualCoins((boolean) (learingMap.get("showVirtualCurrency")));
            } else {
                courseDataClass.setShowVirtualCoins(true);
            }

            if (learingMap.get("showRatingPopUp") != null) {
                courseDataClass.setShowRatingPopUp((boolean) (learingMap.get("showRatingPopUp")));
                rateCourse = courseDataClass.isShowRatingPopUp();
            }

            if (learingMap.get("courseLandScapeMode") != null) {
                courseDataClass.setCourseLandScapeMode((boolean) (learingMap.get("courseLandScapeMode")));
            }

            /*if(learingMap.get("disableDisplayCoins")!=null) {
                courseDataClass.setDisableDisplayCoins((boolean) (learingMap.get("disableDisplayCoins")));
            }else{
                courseDataClass.setDisableDisplayCoins(false);
            }*/

            courseDataClass.setNumEnrolledUsers(OustSdkTools.convertToLong(learingMap.get("numEnrolledUsers") + ""));
            if (courseDataClass.getNumEnrolledUsers() == 0) {
                courseDataClass.setNumEnrolledUsers(OustSdkTools.convertToLong(learingMap.get("enrolledCount") + ""));
            }
            if (learingMap.get("bgImg") != null) {
                courseDataClass.setBgImg((String) learingMap.get("bgImg"));
                Log.d(TAG, "extractCourseData ->Banner: " + courseDataClass.getBgImg());
                Picasso.get().load(courseDataClass.getBgImg())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(imageView_course_banner);

                        /*.into(new Target(){
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Log.d(TAG,"onBitmapLoaded");
                                mCourseBannerConstraintLyt.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                                //layout_banner_top.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Exception e, final Drawable errorDrawable) {
                                Log.d("TAG", "FAILED");
                            }

                            @Override
                            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                Log.d("TAG", "Prepare Load");
                            }
                        });*/
            }
            if (learingMap.get("description") != null) {
                String description = (String) learingMap.get("description");
                if (description != null && !description.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        mTextViewCourseDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        mTextViewCourseDescription.setText(Html.fromHtml(description));
                    }

                }

            }
            /*if (learingMap.get("disableCourseCompleteAudio") != null) {
                courseDataClass.setDisableCourseCompleteAudio((Boolean) learingMap.get("disableCourseCompleteAudio"));
                disableCourseCompleteAudio = courseDataClass.isDisableCourseCompleteAudio();
            }*/

            if (learingMap != null && learingMap.get("enableLevelCompleteAudio") != null) {
                enableLevelCompleteAudio = (boolean) learingMap.get("enableLevelCompleteAudio");
            }
            if (learingMap != null && learingMap.get("enableCourseCompleteAudio") != null) {
                enableCourseCompleteAudio = (boolean) learingMap.get("enableCourseCompleteAudio");
            }

            Log.d(TAG, "updateUserData: enableCourseCompleteAudio:" + enableCourseCompleteAudio + " --- enableLevelCompleteAudio:" + enableLevelCompleteAudio);


            if (learingMap.get("mappedAssessmentId") != null) {
                courseDataClass.setMappedAssessmentId(OustSdkTools.convertToLong(learingMap.get("mappedAssessmentId")));
                assessmentId = OustSdkTools.convertToLong(learingMap.get("mappedAssessmentId"));
            }

            if (learingMap.get("salesMode") != null) {
                isSalesMode = (boolean) learingMap.get("salesMode");
            }
            courseDataClass.setSalesMode(isSalesMode);
            presenter.setSalesMode(isSalesMode);

            if (learingMap.get("enableVideoDownload") != null) {
                enableVideoDownload = (boolean) learingMap.get("enableVideoDownload");
                courseDataClass.setEnableVideoDownload(enableVideoDownload);
            }

            if (learingMap.get("regularMode") != null) {
                isRegularMode = (boolean) learingMap.get("regularMode");
                Log.d(TAG, "IsRegularMode:" + isRegularMode);
                if (isRegularMode) {
                    courseDataClass.setSalesMode(true);
                }
            } else {
                isRegularMode = false;
            }

            courseDataClass.setRegularMode(isRegularMode);
            presenter.setRegularMode(isRegularMode);

            Log.d(TAG, "SalesMode:" + isSalesMode + " -- RegularMode:" + isRegularMode);

            /*if(learingMap.get("ifScormEventBased")!=null){
                courseDataClass.setScormEventBased((boolean) learingMap.get("ifScormEventBased"));
            }else{
                courseDataClass.setScormEventBased(false);
            }
            ifScormEventBased = courseDataClass.isScormEventBased();*/

            /*if(!isComingFromCpl && isEvent && OustStaticVariableHandling.getInstance().getOustApiListener()!=null){
                OustStaticVariableHandling.getInstance().getOustApiListener().onOustContentLoaded();
            }*/

            if (learingMap.get("disableLevelCompletePopup") != null) {
                courseDataClass.setDisableLevelCompletePopup((boolean) learingMap.get("disableLevelCompletePopup"));
            } else {
                courseDataClass.setDisableLevelCompletePopup(false);
            }

            if (learingMap.get("language") != null) {
                courseDataClass.setLanguage((String) learingMap.get("language"));
            }

            /*if (learingMap.get("disableReviewMode") != null) {
                courseDataClass.setDisableReviewMode((boolean) learingMap.get("disableReviewMode"));
            }*/
            courseDataClass.setDisableReviewMode(false);

            if (learingMap.get("cplId") != null) {
                long cplId = OustSdkTools.convertToLong(learingMap.get("cplId"));
                OustPreferences.saveTimeForNotification("cplId_course", cplId);
            } else {
                OustPreferences.saveTimeForNotification("cplId_course", 0);
            }

            if (learingMap.get("mappedAssessmentDetails") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learingMap.get("mappedAssessmentDetails");
                if (mappedAssessmentMap.get("name") != null) {
                    courseDataClass.setMappedAssessmentName((String) mappedAssessmentMap.get("name"));
                }
                if (mappedAssessmentMap.get("id") != null) {
                    courseDataClass.setMappedAssessmentId(OustSdkTools.convertToLong(mappedAssessmentMap.get("id")));
                }
                if (mappedAssessmentMap.get("icon") != null) {
                    courseDataClass.setMappedAssessmentImage((String) mappedAssessmentMap.get("icon"));
                }
            }
            if (learingMap.get("zeroXpForLCard") != null) {
                courseDataClass.setZeroXpForLCard((boolean) learingMap.get("zeroXpForLCard"));
            }
            if (learingMap.get("zeroXpForQCard") != null) {
                courseDataClass.setZeroXpForQCard((boolean) learingMap.get("zeroXpForQCard"));
            }
            if (learingMap.get("hideLeaderboard") != null) {
                courseDataClass.setHideLeaderBoard((boolean) learingMap.get("hideLeaderboard"));
            } else {
                courseDataClass.setHideLeaderBoard(false);
            }

            if (learingMap.get("showQuesInReviewMode") != null) {
                courseDataClass.setShowQuesInReviewMode((boolean) learingMap.get("showQuesInReviewMode"));
            }

            if (learingMap.get("hideBulletinBoard") != null) {
                courseDataClass.setHideBulletinBoard((boolean) learingMap.get("hideBulletinBoard"));
            } else {
                courseDataClass.setHideBulletinBoard(false);
            }

            if (learingMap.get("fullScreenPotraitModeVideo") != null) {
                courseDataClass.setFullScreenPotraitModeVideo(((boolean) learingMap.get("fullScreenPotraitModeVideo")));
            }
            if (learingMap.get("descriptionCard") != null) {
                Log.d(TAG, "extractCourseData: card info:");
                cardInfo = (Map<String, Object>) learingMap.get("descriptionCard");
                isAnyIntroPopupVisible = true;
                Log.i("description ", cardInfo.toString());
            }
            if (learingMap.get("ackPopup") != null) {
                Map<Object, Object> desclaimerMap = (Map<Object, Object>) learingMap.get("ackPopup");
                courseDesclaimerData = new CourseDesclaimerData();
                if (desclaimerMap.get("body") != null) {
                    courseDesclaimerData.setContent((String) desclaimerMap.get("body"));
                }

                if (desclaimerMap.get("buttonLabel") != null) {
                    courseDesclaimerData.setBtnText((String) desclaimerMap.get("buttonLabel"));
                }

                if (desclaimerMap.get("checkBoxText") != null) {
                    courseDesclaimerData.setCheckBoxText((String) desclaimerMap.get("checkBoxText"));
                }

                if (desclaimerMap.get("header") != null) {
                    courseDesclaimerData.setHeader((String) desclaimerMap.get("header"));
                }
            }

            //tips
            if (learingMap.get("tips") != null) {
                List<String> hintList = new ArrayList<>();
                Map<String, String> hintMap = new HashMap<>();
                Object o1 = learingMap.get("tips");
                if (o1.getClass().equals(HashMap.class)) {
                    hintMap = (Map<String, String>) o1;
                    for (String key : hintMap.keySet()) {
                        hintList.add(hintMap.get(key));
                    }
                } else if (o1.getClass().equals(ArrayList.class)) {
                    hintList = (ArrayList<String>) o1;
                }
                OustAppState.getInstance().setHintMessages(hintList);
            }
            if (learingMap.get("certificate") != null) {
                courseDataClass.setCertificate((boolean) learingMap.get("certificate"));
            }
            if (learingMap.get("enableBadge") != null) {
                courseDataClass.setEnableBadge((boolean) learingMap.get("enableBadge"));
            }
            if (learingMap.get("disableScreenShot") != null) {
                courseDataClass.setDisableScreenShot((boolean) learingMap.get("disableScreenShot"));
            }
            if (learingMap.get("lpBgImageNew") != null) {
                courseDataClass.setLpBgImage((String) learingMap.get("lpBgImageNew"));
            }
            if (learingMap.get("isTTSEnabledQC") != null) {
                courseDataClass.setQuesttsEnabled((boolean) learingMap.get("isTTSEnabledQC"));
            }
            if (learingMap.get("isTTSEnabledLC") != null) {
                courseDataClass.setCardttsEnabled((boolean) learingMap.get("isTTSEnabledLC"));
            }
            if (learingMap.get("icon") != null) {
                courseDataClass.setIcon((String) learingMap.get("icon"));
            }
            if (learingMap.get("startFromLastLevel") != null) {
                courseDataClass.setStartFromLastLevel((boolean) learingMap.get("startFromLastLevel"));
            }

            if (learingMap.get("completionDeadline") != null) {
                courseDataClass.setCourseDeadline((String) learingMap.get("completionDeadline"));
            }
            if (learingMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                courseDataClass.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.convertToLong(learingMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
            }
            if (learingMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                courseDataClass.setShowPastDeadlineModulesOnLandingPage((boolean) learingMap.get("showPastDeadlineModulesOnLandingPage"));
            }

            if (learingMap.get("showQuestionSymbolForQuestion") != null) {
                courseDataClass.setShowQuestionSymbolForQuestion((boolean) learingMap.get("showQuestionSymbolForQuestion"));
            }

            if (learingMap.get("totalOc") != null) {
                courseDataClass.setTotalOc(OustSdkTools.convertToLong(learingMap.get("totalOc")));
                Log.e(TAG, "total oc from firebase course node" + learingMap.get("totalOc"));
            }
            if (learingMap.get("removeDataAfterCourseCompletion") != null) {
                Object o4 = learingMap.get("removeDataAfterCourseCompletion");
                if (o4.getClass().equals(String.class)) {
                    courseDataClass.setRemoveDataAfterCourseCompletion(Boolean.valueOf((String) learingMap.get("removeDataAfterCourseCompletion")));
                } else {
                    courseDataClass.setRemoveDataAfterCourseCompletion((boolean) learingMap.get("removeDataAfterCourseCompletion"));
                }
            }
            long courseTotalOc = 0;
            Object o1 = learingMap.get("levels");
            if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                List<Object> levelsList = (List<Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (int i = 0; i < levelsList.size(); i++) {
                        if (levelsList.get(i) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(i);
                            courseLevelClass.setLpId(learningPathId);
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
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
                            }
                            if (levelMap.get("levelThumbnail") != null) {
                                courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                            }
                            if (levelMap.get("hidden") != null) {
                                courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                            }
                            if (levelMap.get("sequence") != null) {
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }

                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }
                            Log.e("Level Lock", "Level number unlocked " + i + " " + courseLevelClass.isLevelLock());
                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) levelMap.get("cards");
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }

                                                if (cardsubMap.get("isIfScormEventBased") != null) {
                                                    courseCardClass.setIfScormEventBased((boolean) cardsubMap.get("isIfScormEventBased"));
                                                } else {
                                                    courseCardClass.setIfScormEventBased(false);
                                                }
                                                Log.d(TAG, "extractCourseData: isIfScormEventBased:" + courseCardClass.isIfScormEventBased());

                                                /*if(cardsubMap.get("proceedOnWrong")!=null){
                                                    courseCardClass.setProceedOnWrong((boolean) cardsubMap.get("proceedOnWrong"));
                                                }else{
                                                    courseCardClass.setProceedOnWrong(true);
                                                }*/

                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
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
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
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
                    Collections.sort(courseLevelClassList, coreseLevelSorter);
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
                    presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }
            } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (String s1 : levelsList.keySet()) {
                        if (levelsList.get(s1) != null) {
                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(Integer.parseInt(s1));
                            courseLevelClass.setLpId(learningPathId);
                            if (levelMap.get("levelDescription") != null) {
                                courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                            }
                            if (levelMap.get("downloadStratergy") != null) {
                                courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                            }
                            if (levelMap.get("xp") != null) {
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
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
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }
                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }
                            Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
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
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                startCourseDownloadService(courseDataClass);
                                                courseCardClassList.add(courseCardClass);
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
                    Collections.sort(courseLevelClassList, coreseLevelSorter);
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
                    presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }
            } else {
                Map<String, Object> levelsList = (Map<String, Object>) learingMap.get("levels");
                List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                if (levelsList != null) {
                    for (String s1 : levelsList.keySet()) {
                        if (levelsList.get(s1) != null) {
                            //Map<String, Object> featuresFromJson = new Gson().fromJson(levelsList.get(s1), new TypeToken<Map<String, Object>>() {}.getType());
                            final Map<String, Object> levelMap = (Map<String, Object>) levelsList.get(s1);
                            CourseLevelClass courseLevelClass = new CourseLevelClass();
                            courseLevelClass.setLevelId(Integer.parseInt(s1));
                            courseLevelClass.setLpId(learningPathId);
                            if (levelMap.get("levelDescription") != null) {
                                courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                            }
                            if (levelMap.get("downloadStratergy") != null) {
                                courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                            }
                            if (levelMap.get("xp") != null) {
                                courseLevelClass.setTotalXp(OustSdkTools.convertToLong(levelMap.get("xp")));
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
                                courseLevelClass.setSequence(OustSdkTools.convertToLong(levelMap.get("sequence")));
                            }
                            if (levelMap.get("totalOc") != null) {
                                courseLevelClass.setTotalOc(OustSdkTools.convertToLong(levelMap.get("totalOc")));
                                courseTotalOc += (OustSdkTools.convertToLong(levelMap.get("totalOc")));
                            }
                            if (levelMap.get("updateTime") != null) {
                                courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                            }
                            courseLevelClass.setLevelLock(false);
                            if (levelMap.get("levelLock") != null) {
                                courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                            }

                            Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                            List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                            if (levelMap.get("cards") != null) {
                                Object o2 = levelMap.get("cards");
                                if (o2.getClass().equals(HashMap.class)) {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
//                                                if(cardsubMap.get("clCode")!=null){
//                                                    try{
//                                                        courseCardClass.setClCode(LearningCardType.valueOf((String) cardsubMap.get("clCode")));
//                                                    }catch (Exception e){}
//                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
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
                                                startCourseDownloadService(courseDataClass);
                                            }
                                        }
                                    }
                                } else if (o2.getClass().equals(ArrayList.class)) {
                                    List<Object> cardList = (List<Object>) levelMap.get("cards");
                                    if (cardList != null) {
                                        for (int l = 0; l < cardList.size(); l++) {
                                            if (cardList.get(l) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }

                                                courseCardClassList.add(courseCardClass);
                                            }
                                        }
                                    }
                                } else {
                                    Map<String, Object> cardMap = (Map<String, Object>) o2;
                                    if (cardMap != null) {
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType = "";
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.convertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.convertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.convertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
//                                                if ((courseCardClass.getXp() == 0)) {
//                                                    courseCardClass.setXp(100);
//                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.convertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
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
                    Collections.sort(courseLevelClassList, coreseLevelSorter);
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
                    presenter.loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap, courseColnId);
                }

            }
            //loadUserDataFromFirebase(learningPathId, courseDataClass, landingMap);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateUserData(final int lpId, final CourseDataClass courseDataClass, Map<String, Object> learingMap) {
        Log.d(TAG, "updateUserData: ");
        DTOUserCourseData userCourseData1 = presenter.getUserCourseData();
        boolean isCompletedCourseForIssue = false;
        boolean isAllCardCompleted = true;
        boolean isAssessmentStatus = true;
        try {
            if (learingMap != null && learingMap.get("completionPercentage") != null) {
                Object o3 = learingMap.get("completionPercentage");
                if (o3.getClass().equals(Long.class)) {
                    userCourseData1.setPresentageComplete((long) learingMap.get("completionPercentage"));
                } else if (o3.getClass().equals(String.class)) {
                    String s3 = (String) o3;
                    userCourseData1.setPresentageComplete(Long.parseLong(s3));

                } else if (o3.getClass().equals(Double.class)) {
                    Double s3 = (Double) o3;
                    long l = (new Double(s3)).longValue();
                    userCourseData1.setPresentageComplete(l);

                }
            } else {
                userCourseData1.setPresentageComplete(0);
            }
            if (learingMap != null && learingMap.get("courseCompleted") != null) {
                userCourseData1.setCourseCompleted((boolean) learingMap.get("courseCompleted"));
            }

            if (userCourseData1.getPresentageComplete() == 100) {
                isCourseAlreadyComplete = true;
                isCompletedCourseForIssue = true;
            } else {
                if (userCourseData1.getPresentageComplete() == 95 && courseDataClass.getMappedAssessmentId() > 0) {
                    isCourseAlreadyComplete = true;
                    isCompletedCourseForIssue = true;
                }
            }
            if (learingMap != null && learingMap.get("currentCourseLevelId") != null) {
                userCourseData1.setCurrentLevelId(OustSdkTools.convertToLong(learingMap.get("currentCourseLevelId")));
            }
            if (learingMap != null && learingMap.get("currentCourseCardId") != null) {
                userCourseData1.setCurrentCard(OustSdkTools.convertToLong(learingMap.get("currentCourseCardId")));
            }

            if (learingMap != null && learingMap.get("courseCompleted") != null) {
                userCourseData1.setCourseCompleted((boolean) learingMap.get("courseCompleted"));
            } else {
                userCourseData1.setCourseCompleted(false);
            }
            if (learingMap != null && learingMap.get("addedOn") != null) {
                userCourseData1.setAddedOn((String) learingMap.get("addedOn"));
            }

            if (learingMap != null && learingMap.get("addedOn") != null) {
                userCourseData1.setAddedOn((String) learingMap.get("addedOn"));
            }

            if (learingMap != null && learingMap.get("mappedAssessmentCompleted") != null) {
                userCourseData1.setMappedAssessmentCompleted((boolean) learingMap.get("mappedAssessmentCompleted"));
                isAssessmentStatus = (boolean) learingMap.get("mappedAssessmentCompleted");
            } else {
                isAssessmentStatus = true;
            }

            boolean isEnrolled = false;
            if (learingMap != null && learingMap.get("enrolled") != null) {
                isEnrolled = ((boolean) learingMap.get("enrolled"));
                courseDataClass.setEnrolled(isEnrolled);
            }

            if (learingMap != null && learingMap.get("userOC") != null) {
                userCourseData1.setTotalOc(OustSdkTools.convertToLong(learingMap.get("userOC")));
            }
            if (learingMap != null && learingMap.get("rating") != null) {
                int rating = (int) (OustSdkTools.convertToLong(learingMap.get("rating")));
                userCourseData1.setMyCourseRating(rating);
            } else {
                userCourseData1.setMyCourseRating(0);
            }
            if (learingMap != null && learingMap.get("mappedAssessment") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learingMap.get("mappedAssessment");
                if (mappedAssessmentMap.get("passed") != null) {
                    userCourseData1.setMappedAssessmentPassed((boolean) mappedAssessmentMap.get("passed"));
                    userEventAssessmentData.setPassed(userCourseData1.isMappedAssessmentPassed());
                }

                if (mappedAssessmentMap.get("enrolled") != null) {
                    courseDataClass.setMappedAssessmentEnrolled((boolean) mappedAssessmentMap.get("enrolled"));
                }

                if (mappedAssessmentMap.get("nQuestionAnswered") != null) {
                    userEventAssessmentData.setnQuestionAnswered((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionAnswered")));
                    userEventAssessmentData.setnTotalQuestions((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionAnswered")));
                }
                if (mappedAssessmentMap.get("userCompletionPercentage") != null) {
                    courseDataClass.setMappedAssessmentPercentage((Long) mappedAssessmentMap.get("userCompletionPercentage"));
                }
                if (mappedAssessmentMap.get("nQuestionCorrect") != null) {
                    userEventAssessmentData.setnQuestionCorrect((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionCorrect")));
                }
                if (mappedAssessmentMap.get("nQuestionSkipped") != null) {
                    userEventAssessmentData.setnQuestionSkipped((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionSkipped")));
                }
                if (mappedAssessmentMap.get("nQuestionWrong") != null) {
                    userEventAssessmentData.setnQuestionWrong((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionWrong")));
                }
            }

            if (learingMap != null && learingMap.get("contentPlayListId") != null) {
                courseDataClass.setContentPlayListId(OustSdkTools.convertToLong(learingMap.get("contentPlayListId")));
            }
            if (learingMap != null && learingMap.get("contentPlayListSlotId") != null) {
                courseDataClass.setContentPlayListSlotId(OustSdkTools.convertToLong(learingMap.get("contentPlayListSlotId")));
            }
            if (learingMap != null && learingMap.get("contentPlayListSlotItemId") != null) {
                courseDataClass.setContentPlayListSlotItemId(OustSdkTools.convertToLong(learingMap.get("contentPlayListSlotItemId")));
            }
            if (learingMap != null && learingMap.get("levels") != null) {
                Object o1 = learingMap.get("levels");
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> objectList = (List<Object>) o1;
                    if (objectList != null) {
                        List<DTOUserLevelData> userLevelDataList = userCourseData1.getUserLevelDataList();
                        for (int k = 0; k < objectList.size(); k++) {
                            if (objectList.get(k) != null) {
                                final Map<String, Object> levelMap = (Map<String, Object>) objectList.get(k);
                                if (userLevelDataList == null) {
                                    userLevelDataList = new ArrayList<>();
                                }
                                int courseLevelNo = -1;
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
                                        if (levelMap.get("userLevelOC") != null) {
                                            userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                        }
                                        if (levelMap.get("userLevelXp") != null) {
                                            userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
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
                                                if (objectCardList != null) {
                                                    List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                    for (int j = 0; j < objectCardList.size(); j++) {
                                                        if (objectCardList.get(j) != null) {
                                                            final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                            DTOUserCardData userCardData = new DTOUserCardData();
                                                            if (cardMap.get("userCardAttempt") != null) {
                                                                userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                            } else {
                                                                userCardData.setNoofAttempt(0);
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
                                                                userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                            } else {
                                                                userCardData.setCardViewInterval(0);
                                                            }

                                                            /*if (userCardData.isCardCompleted()) {
                                                                if (cardMap.get("userCardScore") != null) {
                                                                    //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                                }
                                                                userCardData.setCardId(j);
                                                                userCardDataList.add(userCardData);
                                                            }
                                                            Log.d(TAG, "updateUserData1: cardKey:"+j+" --- cardCompleted:"+userCardData.isCardCompleted());
                                                            }*/

                                                            userCardData.setCardId(j);
                                                            userCardDataList.add(userCardData);
                                                        }
                                                    }
                                                    userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                                }
                                            } else if (o2.getClass().equals(HashMap.class)) {
                                                final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                for (String cardKey : objectCardMap.keySet()) {
                                                    final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                    DTOUserCardData userCardData = new DTOUserCardData();
                                                    if (cardMap.get("userCardAttempt") != null) {
                                                        userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                    } else {
                                                        userCardData.setNoofAttempt(0);
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
                                                        userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                    } else {
                                                        userCardData.setCardViewInterval(0);
                                                    }

                                                    /*if (userCardData.isCardCompleted()) {
                                                        if (cardMap.get("userCardScore") != null) {
                                                            //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                        }
                                                        userCardData.setCardId(Integer.parseInt(cardKey));
                                                        userCardDataList.add(userCardData);
                                                    }

                                                    Log.d(TAG, "updateUserData2: cardKey:"+cardKey+" --- cardCompleted:"+userCardData.isCardCompleted());
                                                    }*/
                                                    userCardData.setCardId(Integer.parseInt(cardKey));
                                                    userCardDataList.add(userCardData);
                                                }
                                                userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        userCourseData1.setUserLevelDataList(userLevelDataList);
                    }
                } else if (o1.getClass().equals(HashMap.class)) {
                    final Map<String, Object> objectMap = (Map<String, Object>) o1;
                    if (objectMap != null) {
                        List<DTOUserLevelData> userLevelDataList = userCourseData1.getUserLevelDataList();

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
                                    if (levelMap.get("userLevelOC") != null) {
                                        userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.convertToLong(levelMap.get("userLevelOC")));
                                    }
                                    if (levelMap.get("userLevelXp") != null) {
                                        userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.convertToLong(levelMap.get("userLevelXp")));
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
                                            if (objectCardList != null) {
                                                for (int j = 0; j < objectCardList.size(); j++) {
                                                    if (objectCardList.get(j) != null) {
                                                        final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                        DTOUserCardData userCardData = new DTOUserCardData();
                                                        if (cardMap.get("userCardAttempt") != null) {
                                                            userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                        } else {
                                                            userCardData.setNoofAttempt(0);
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
                                                            userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                        } else {
                                                            userCardData.setCardViewInterval(0);
                                                        }

                                                        /*if (userCardData.isCardCompleted()) {
                                                            if (cardMap.get("userCardScore") != null) {
                                                                //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                            }
                                                            userCardData.setCardId(j);
                                                            userCardDataList.add(userCardData);
                                                        }
                                                        Log.d(TAG, "updateUserData3: cardKey:"+j+" --- cardCompleted:"+userCardData.isCardCompleted());

                                                        }*/
                                                        userCardData.setCardId(j);
                                                        userCardDataList.add(userCardData);
                                                    }
                                                }
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        } else if (o2.getClass().equals(HashMap.class)) {
                                            List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                            final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                            for (String cardKey : objectCardMap.keySet()) {
                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                DTOUserCardData userCardData = new DTOUserCardData();

                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.convertToLong(cardMap.get("userCardAttempt")));
                                                } else {
                                                    userCardData.setNoofAttempt(0);
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
                                                    userCardData.setCardViewInterval(OustSdkTools.convertToLong(cardMap.get("cardViewInterval")));
                                                } else {
                                                    userCardData.setCardViewInterval(0);
                                                }

                                                /*if (userCardData.isCardCompleted()) {
                                                    if (cardMap.get("userCardScore") != null) {
                                                        //userCardData.setOc((long) cardMap.get("userCardScore"));
                                                    }
                                                    userCardData.setCardId(Integer.parseInt(cardKey));
                                                    userCardDataList.add(userCardData);
                                                }
                                                Log.d(TAG, "updateUserData4: cardKey:"+cardKey+" --- cardCompleted:"+userCardData.isCardCompleted());

                                                }*/

                                                userCardData.setCardId(Integer.parseInt(cardKey));
                                                userCardDataList.add(userCardData);
                                            }
                                            userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(userLevelDataList, courseUserCardSorter);
                        userCourseData1.setUserLevelDataList(userLevelDataList);
                    }
                }

            }
            if (!isEnrolled) {
                if (courseDesclaimerData != null) {
                    setDesclaimerPopup(courseDesclaimerData);
                } else if (cardInfo != null) {
                    checkForDescriptionCardData(cardInfo);
                }
            }

            /*if (courseDataClass != null) {
                List<DTOUserLevelData> levelDataList = new ArrayList<>();
                if (userCourseData1.getUserLevelDataList() == null) {
                    userCourseData1.setUserLevelDataList(new ArrayList<DTOUserLevelData>());
                }

                for (int k = 0; k < userCourseData1.getUserLevelDataList().size(); k++) {
                    boolean containLevel = false;
                    for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                        if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            containLevel = true;
                        }
                    }
                    if (containLevel) {
                        boolean alreadyIn = false;
                        for (int n = 0; n < levelDataList.size(); n++) {
                            if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == levelDataList.get(n).getLevelId()) {
                                alreadyIn = true;
                            }
                        }
                        if (!alreadyIn) {
                            levelDataList.add(userCourseData1.getUserLevelDataList().get(k));
                        }
                    }
                }

                userCourseData1.setUserLevelDataList(levelDataList);
                DTOUserLevelData userLevelData = new DTOUserLevelData();
                for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                    boolean alreadyIn = false;
                    for (int k = 0; k < userCourseData1.getUserLevelDataList().size(); k++) {
                        if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            alreadyIn = true;
                        }
                    }
                    if (!alreadyIn) {
                        userLevelData = new DTOUserLevelData();
                        userLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        userLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());
                        userCourseData1.getUserLevelDataList().add(userLevelData);
                    }
                }
            }*/

            if (courseDataClass != null) {
                List<DTOUserLevelData> levelDataList = new ArrayList<>();
                if (userCourseData1.getUserLevelDataList() == null) {
                    userCourseData1.setUserLevelDataList(new ArrayList<DTOUserLevelData>());
                }

                for (int k = 0; k < courseDataClass.getCourseLevelClassList().size(); k++) {
                    //boolean containLevel = false;
                    for (int l = 0; l < userCourseData1.getUserLevelDataList().size(); l++) {
                        if (userCourseData1.getUserLevelDataList().get(l).getLevelId() == courseDataClass.getCourseLevelClassList().get(k).getLevelId()) {
                            //containLevel = true;
                            //if (containLevel) {
                            boolean alreadyIn = false;
                            for (int n = 0; n < levelDataList.size(); n++) {
                                if (userCourseData1.getUserLevelDataList().get(l).getLevelId() == levelDataList.get(n).getLevelId()) {
                                    alreadyIn = true;
                                }
                            }
                            if (!alreadyIn) {
                                levelDataList.add(userCourseData1.getUserLevelDataList().get(l));
                            }
                            //}
                        }
                    }
                    /*if (containLevel) {
                        boolean alreadyIn = false;
                        for (int n = 0; n < levelDataList.size(); n++) {
                            if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == levelDataList.get(n).getLevelId()) {
                                alreadyIn = true;
                            }
                        }
                        if (!alreadyIn) {
                            levelDataList.add(userCourseData1.getUserLevelDataList().get(k));
                        }
                    }*/
                }

                userCourseData1.setUserLevelDataList(levelDataList);
                DTOUserLevelData userLevelData;
                for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                    boolean alreadyIn = false;
                    CourseLevelClass courseLevelClass = courseDataClass.getCourseLevelClassList().get(l);

                    for (int k = 0; k < userCourseData1.getUserLevelDataList().size(); k++) {
                        if (userCourseData1.getUserLevelDataList().get(k).getLevelId() == courseLevelClass.getLevelId()) {
                            alreadyIn = true;

                            DTOUserLevelData dtoUserLevelData = userCourseData1.getUserLevelDataList().get(k);
                            try {
                                List<DTOCourseCard> dtoCourseCardList = courseLevelClass.getCourseCardClassList();
                                List<DTOUserCardData> dtoUserCardDataList = dtoUserLevelData.getUserCardDataList();
                                if (dtoUserCardDataList == null || dtoUserCardDataList.isEmpty() || dtoCourseCardList.size() < 1) {

                                    dtoUserCardDataList = new ArrayList<DTOUserCardData>();
                                    for (int c = 0; c < dtoCourseCardList.size(); c++) {
                                        DTOCourseCard dtoCourseCard = dtoCourseCardList.get(c);
                                        Log.d(TAG, "updateUserData:level is there, card is adding->" + dtoCourseCard.getCardId());
                                        DTOUserCardData userCardData = new DTOUserCardData(dtoCourseCard.getCardId(), 0, false);
                                        dtoUserCardDataList.add(userCardData);
                                    }
                                    userCourseData1.getUserLevelDataList().get(k).setUserCardDataList(dtoUserCardDataList);

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
                                            userCourseData1.getUserLevelDataList().get(k).setUserCardDataList(dtoUserCardDataList);
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
                        //userLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        //userLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());

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
                        userCourseData1.getUserLevelDataList().add(userLevelData);

                        /*userLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        userLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());
                        userCourseData1.getUserLevelDataList().add(userLevelData);*/
                    }
                }
            }

            Collections.sort(userCourseData1.getUserLevelDataList(), courseUserCardSorter);

            int currentLevel = 0;
            for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                if ((courseDataClass.getCourseLevelClassList() != null) && (courseDataClass.getCourseLevelClassList().get(i) != null)
                        && (userCourseData1.getUserLevelDataList() != null) && (userCourseData1.getUserLevelDataList().get(i) != null)) {
                    if (!userCourseData1.getUserLevelDataList().get(i).isLocked() && !courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                        currentLevel++;
                        Log.d(TAG, "updateUserData: currentLevel:" + currentLevel + " --- level size:" + courseDataClass.getCourseLevelClassList().size());
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            Log.d(TAG, "updateUserData: currentLevel:" + currentLevel + " --- level size:" + courseDataClass.getCourseLevelClassList().size());
            userCourseData1.setCurrentLevel(currentLevel);
            if (userCourseData1.getCurrentLevel() > (courseDataClass.getCourseLevelClassList().size() + 1)) {
                userCourseData1.setCurrentLevel((courseDataClass.getCourseLevelClassList().size() + 1));
            }

            userCourseData1.setCurrentCompleteLevel(currentLevel - 1);

            if (userCourseData1.getPresentageComplete() == 100) {
                isCourseCompleted = true;
                if (isSurveyAttached && surveyId != 0) {
                    checkforSavedAssessment(activeUser);
                }

                userCourseData1.setCourseComplete(true);
                userCourseData1.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1);
                if (courseDataClass.getMappedAssessmentId() > 0) {
                    if (userCourseData1.isMappedAssessmentPassed()) {
                        userCourseData1.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2);
                    }
                }
//                if(userCourseData1.getCurrentCompleteLevel()==0) {
//                    shouldSetCardNo=true;
//                    userCourseData1.setCurrentCompleteLevel((int) userCourseData1.getCurrentLevel());
//                }
            } else {
                userCourseData1.setCourseComplete(false);
            }
            if (!isEnrolled) {
                userCourseData1.setCurrentLevel(0);
            } else if (userCourseData1.getCurrentLevel() == 0 && isEnrolled) {
                userCourseData1.setCurrentLevel(1);
            }
            if (userCourseData1.getUserLevelDataList() != null) {
                if (userCourseData1.getCurrentLevel() > 0) {
                    Collections.sort(courseDataClass.getCourseLevelClassList(), CourseLevelClass.levelSorter);
                    Collections.sort(courseDataClass.getCourseLevelClassList().get((int) userCourseData1.getCurrentLevel() - 1).getCourseCardClassList(), DTOCourseCard.newsCardSorter);
                    for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData1.getCurrentLevel() - 1).getCourseCardClassList().size(); l++) {
                        if (courseDataClass.getCourseLevelClassList().get((int) userCourseData1.getCurrentLevel() - 1).getCourseCardClassList().get(l).getCardId() == userCourseData1.getCurrentCard()) {
                            userCourseData1.getUserLevelDataList().get((int) userCourseData1.getCurrentLevel() - 1).setCurrentCardNo(l);
//                            if((userCourseData1.getPresentageComplete()==100)&&(shouldSetCardNo)){
//                                userCourseData1.getUserLevelDataList().get((int) userCourseData1.getCurrentCompleteLevel()-1).setCurrentCardNo(l);
//                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        presenter.addUserData(userCourseData1);
        presenter.gotLpDataFromFirebase(courseDataClass);

        if (!isCompletedCourseForIssue && isAllCardCompleted && isAssessmentStatus && !isInstrumentationHit) {
            isInstrumentationHit = true;
            presenter.hitInstrumentationForCompletion(RegularModeLearningMapActivity.this);
        }
    }

    public Comparator<DTOUserLevelData> courseUserCardSorter = new Comparator<DTOUserLevelData>() {
        public int compare(DTOUserLevelData s1, DTOUserLevelData s2) {
            return (int) s1.getSequece() - (int) s2.getSequece();
        }
    };


    public Comparator<DTOCourseCard> courseCardSorter = new Comparator<DTOCourseCard>() {
        public int compare(DTOCourseCard s1, DTOCourseCard s2) {
            return (int) s1.getSequence() - (int) s2.getSequence();
        }
    };

    public Comparator<CourseLevelClass> coreseLevelSorter = new Comparator<CourseLevelClass>() {
        public int compare(CourseLevelClass s1, CourseLevelClass s2) {
            return (int) s1.getSequence() - (int) s2.getSequence();
        }
    };

    @Override
    public void hideLoader() {
        try {
            if (isReviewMode) {
                swipe_refresh_layout.setRefreshing(false);
                swipe_refresh_layout.setVisibility(View.GONE);
            }
            mainloader_back.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showRetry() {

    }


//--------------------------------------------------------------------------------
//create recyclerview


    @Override
    public void setReviewMode(boolean reviewMode) {

        isReviewMode = reviewMode;
    }

    private RegularModeAdapter reviewModeAdaptor;
    private List<SearchCourseLevel> searchCourseLevelList;
    private List<SearchCourseLevel> filteredsearchCourseLevelList;

    @Override
    public void setAllLevelList(List<SearchCourseLevel> levelList) {
        Log.d(TAG, "setAllLevelList: " + levelList.size());
        filteredsearchCourseLevelList = new ArrayList<>();
        this.searchCourseLevelList = levelList;
    }

    @Override
    public void createLevelList(List<SearchCourseLevel> levelList, boolean allCourseSearchMode) {
        try {
            Log.d(TAG, "createLevelList: ");
            if ((filteredsearchCourseLevelList != null) && (filteredsearchCourseLevelList.size() > 0)) {
                levelList = filteredsearchCourseLevelList;
            }
            Log.d(TAG, "createLevelList: size:" + levelList.size());
            if ((levelList != null) && (levelList.size() > 0)) {
                usermanual_recyclerview.setVisibility(View.VISIBLE);
                um_notopic_text.setVisibility(View.GONE);

                AssessmentNavModel assessmentNavModel = null;
                CourseDataClass courseDataClass = presenter.getCourseDataClass();
                if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                    assessmentNavModel = new AssessmentNavModel();
                    assessmentNavModel.setCurrentLearningPathId(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
                    assessmentNavModel.setCourseColnId(courseColnId);
                    assessmentNavModel.setCertificate(courseDataClass.isCertificate());
                    assessmentNavModel.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                    assessmentNavModel.setMappedAssessmentName(courseDataClass.getMappedAssessmentName());
                    assessmentNavModel.setMappedAssessmentImage(courseDataClass.getMappedAssessmentImage());
                    assessmentNavModel.setMappedAssessmentPercentage(courseDataClass.getMappedAssessmentPercentage());
                }

                if (reviewModeAdaptor == null) {
                    reviewModeAdaptor = new RegularModeAdapter(RegularModeLearningMapActivity.this, levelList, allCourseSearchMode, assessmentNavModel, language, mUserCourseData, isSalesMode);
                    MyCustomLayoutManager mLayoutManager = new MyCustomLayoutManager(RegularModeLearningMapActivity.this);
                    mLayoutManager.setExtraLayoutSpace(0);
                    reviewModeAdaptor.setReviewModeCallBack(RegularModeLearningMapActivity.this);
                    usermanual_recyclerview.setLayoutManager(mLayoutManager);
                    usermanual_recyclerview.setItemAnimator(new DefaultItemAnimator());
                    usermanual_recyclerview.setAdapter(reviewModeAdaptor);
                    um_serachedittext.setOnKeyListener(this);
                    um_serachedittext.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (um_serachedittext.getText().toString().isEmpty()) {
                                presenter.startLearningMap(false);
                                um_serachclose.setVisibility(View.GONE);
                            } else {
                                um_serachclose.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                } else {
                    usermanual_recyclerview.removeAllViews();
                    reviewModeAdaptor.notifyDateChanges(levelList, allCourseSearchMode, assessmentNavModel);
                }
            } else {
                usermanual_recyclerview.setVisibility(View.GONE);
                um_notopic_text.setVisibility(View.VISIBLE);
                um_notopic_text.setText(getResources().getString(R.string.no_match_found));
            }
            hideLoader();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updateReviewList() {
        if (reviewModeAdaptor != null) {
            reviewModeAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        try {
            if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                filterList(um_serachedittext.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }


    @Override
    public void onMainRowClick(int position) {
        try {
            Log.d(TAG, "onMainRowClick: ");
            if ((filteredsearchCourseLevelList != null) && (filteredsearchCourseLevelList.size() > 0)) {
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
        presenter.clickOnUserManualRow(levelPosition, cardPosition);
    }

    @Override
    public void onCardClick(int levelPosition, int cardPosition, boolean isRegularMode) {
        presenter.clickOnUserManualRow(levelPosition, cardPosition, isRegularMode);
    }

    @Override
    public void onDownloadIconClick(int position) {
        presenter.clickonDownloadIcon(("" + position));
    }

    public void filterList(String searchText) {
        try {
            filteredsearchCourseLevelList = new ArrayList<>();
            if (searchText.isEmpty()) {
//                presenter.startLearningMap();
                um_serachclose.setVisibility(View.GONE);
            } else {
                um_serachclose.setVisibility(View.VISIBLE);
                filteredsearchCourseLevelList = searchCourseLevelList;
                if ((searchCourseLevelList != null) && (searchCourseLevelList.size() > 0)) {
                    LevelFilter levelFilter = new LevelFilter();
                    filteredsearchCourseLevelList = levelFilter.meetCriteria(searchCourseLevelList, searchText);
                }
                createLevelList(filteredsearchCourseLevelList, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public void setLpName(String lpName) {
        try {
           /* learningmain_lpnametext.setSelected(true);
            learningmain_lpnametext.setText(lpName);
            learningmain_lpnametexta.setSelected(true);
            learningmain_lpnametexta.setText(lpName);*/

            mTextViewCourseTitle.setText(lpName);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //-----------------------------------------------------------------------------------------------------
//start building common learning path based on firebase data.
    @Override
    public void setCurrentModuleCompleteLevel(int completePersentage) {
        Log.d(TAG, "Settingup progress level: " + completePersentage + " -- callbacktrigger:" + isCallbackHappened);

        try {
            if (completePersentage > 100) {
                completePersentage = 100;
            }
            currentmodule_completeval.setText("" + completePersentage + "% " + getResources().getString(R.string.completed));
            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(completePersentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }

                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(completePersentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }

            }
            mcompletedProgressBar.setProgress((completePersentage));

            DTOUserCourseData dtoUserCourseData = presenter.getDtoUserCourseData();
            if (dtoUserCourseData.getPresentageComplete() == 100 || (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted())) {
                if (dtoUserCourseData.getMyCourseRating() == 0) {
                    if (!isCourseAlreadyComplete) {
                        if (!isRateCourseShownOnce) {
                            isRateCourseShownOnce = true;
                            courseCompletePopup(courseDataClass);
                            setCertificateVisibility(dtoUserCourseData, courseDataClass);
                        } else {
                            setCertificateVisibility(dtoUserCourseData, courseDataClass);
                        }
                    }
                }
            }

            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(completePersentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }
                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(completePersentage);
                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                }
            }

            /*RealmUserCourseData realmUserCourseData = presenter.getRealmUserCourseData();
            if ((realmUserCourseData.getPresentageComplete() == 100) || (realmUserCourseData.getPresentageComplete() == 95 && realmUserCourseData.isCourseCompleted())) {
                if (realmUserCourseData.getMyCourseRating() == 0) {
                    if ((!isCourseAlreadyComplete)) {
                        if (!isRateCourseShownOnce) {
                            isRateCourseShownOnce = true;
                            isSoundPlayedOnce = false;
                            //showCourseCompletePopup(courseDataClass);
                            courseCompletePopup(courseDataClass);
                        }
                        setCertificateVisibility(realmUserCourseData, courseDataClass);
                    }
                }
            }*/

            /*if(completePersentage>=100){
                presenter.checkIfAutoPlay(null);
            }*/
            /*if(completePersentage<1){
                OustSdkTools.showToast("You need to play first card ");
            }*/

            if (completePersentage == 100 && !isCallbackHappened) {
                isCallbackHappened = true;
                if (isEvent && OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                    UserEventCourseData userEventCourseData = new UserEventCourseData();
                    userEventCourseData.setEvenId(eventId);
                    userEventCourseData.setCourseId(Long.parseLong(learningId));
                    userEventCourseData.setUserProgress("COMPLETED");
                    userEventCourseData.setUserCompletionPercentage(100);

                    //DTOUserCourseData courseDataClass = presenter.getUserCourseData();
                    CourseDataClass courseDataClass = presenter.getCourseDataClass();

                    if (OustAppState.getInstance().getAssessmentFirebaseClass() != null && OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData() != null) {
                        //userEventCourseData.setUserEventAssessmentData(OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData());
                        userEventAssessmentData = OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData();
                        OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(null);
                    } else {
                        String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                        long courseUniqNo = Long.parseLong(s1);
                        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                        DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);

                        if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0 && userCourseData != null) {
                            //UserEventAssessmentData userEventAssessmentData = new UserEventAssessmentData();
                            userEventAssessmentData.setAssessmentId(courseDataClass.getMappedAssessmentId());
                            //userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());

                            userEventAssessmentData.setUserProgress("DISTRIBUTED");
                            if (userCourseData.getPresentageComplete() >= 100 && userCourseData.isMappedAssessmentCompleted()) {
                                userEventAssessmentData.setUserCompletionPercentage(100);
                                userEventAssessmentData.setUserProgress("COMPLETED");
                                userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());
                            } else {
                                userEventAssessmentData.setUserCompletionPercentage(0);
                                if (userCourseData.getPresentageComplete() >= 95 || courseDataClass.isMappedAssessmentEnrolled()) {
                                    userEventAssessmentData.setUserProgress("INPROGRESS");
                                }
                            }
                            //userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                        }
                    }
                    if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                        userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                    }

                    if (isComingFromCpl) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setEventId(eventId);
                        userEventCplData.setCurrentModuleId(Long.parseLong(learningId));
                        userEventCplData.setCurrentModuleType("Course");
                        userEventCplData.setCurrentModuleProgress("Completed");
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");

                        userEventCplData.setnTotalModules(totalModules);
                        userEventCplData.setnModulesCompleted(completedModules + 1);
                        if (completedModules >= totalModules) {
                            userEventCplData.setUserProgress("COMPLETED");
                        } else {
                            userEventCplData.setUserProgress("INPROGRESS");
                        }
                        userEventCplData.setUserEventCourseData(userEventCourseData);
                        OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                        finish();
                    } else {
                        OustStaticVariableHandling.getInstance().getOustApiListener().onCourseProgress(userEventCourseData);
                    }
                } else {
                    if (isComingFromCpl) {
                        finish();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setLpOcText(long mytotalOc, long totalOc) {
        try {
            if (mytotalOc > totalOc) {
                lpoctext.setText("" + totalOc + "/" + totalOc);
            } else {
                lpoctext.setText("" + mytotalOc + "/" + totalOc);
            }
            if (mytotalOc == 0 && totalOc == 0) {
                lpoc_ll.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void startLearningPath() {
        try {
            mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            levelboxSize = (int) (getResources().getDimension(R.dimen.oustlayout_dimen60) / 2);
//            ImageView learningIndiactor = (ImageView) levelIndicatorView.findViewById(R.id.learning_indicator);
//            OustSdkTools.setImage(learningIndiactor, getResources().getString(R.string.learningindicator));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setBackLine(int scrWidth, int scrHeight, int scrollViewHeight, int nooflevels) {
        try {
            simpleLine.setLayoutParams(new RelativeLayout.LayoutParams(scrWidth, scrollViewHeight));
            simpleLine.setScreenWH(scrWidth, scrHeight, simpleLine, nooflevels);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void setBackLayer(String imagePath) {
        try {
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(imagePath).into(learningmap_backimg);
                } else {
                    Picasso.get().load(imagePath).networkPolicy(NetworkPolicy.OFFLINE).into(learningmap_backimg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void bringBackFront() {
        try {
            simpleLine.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private View convertView1;

    @Override
    public void setLearningStartIcon(int scrWidth, int scrHeight, int scrollviewHeight) {
        try {
            convertView1 = mInflater.inflate(R.layout.learning_start, null);
            int levelboxSize1 = (int) (getResources().getDimension(R.dimen.oustlayout_dimen70) / 2);
            float x = (scrWidth * 60 / 320) - levelboxSize1;
            convertView1.setX(x);
            convertView1.setY(getYPosition(120, scrHeight, scrollviewHeight));
            learninglevel_back.addView(convertView1);
            convertView1.bringToFront();
            convertView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (purchased) {
                        if (!locked) {
                            if (!OustSdkTools.checkInternetStatus()) {
                                return;
                            }
                            presenter.clickOnEnrolleLp(true);
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.complete_course_unlock));
                        }
                    } else {
                        if (freeCourse) {
                            OustSdkTools.showToast(getResources().getString(R.string.enroll_course_start));
                        } else {
                            OustSdkTools.showToast(getResources().getString(R.string.purchase_course));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private ImageView startLabel;
    private View startlabelview;

    @Override
    public void setLearningStartLabbelIcon(int scrWidth, int scrHeight, int scrollviewHeight) {
        try {
            startlabelview = mInflater.inflate(R.layout.learningmap_startlabel, null);
            startlabelview.setX(getXPosition(120, scrWidth));
            startlabelview.setY(getYPosition(88, scrHeight, scrollviewHeight));
            startLabel = startlabelview.findViewById(R.id.start_icon);
            TextView startlabel_text = startlabelview.findViewById(R.id.startlabel_text);
            startlabel_text.setText(OustSdkApplication.getContext().getResources().getString(R.string.click_here_start));
            setStartLabelAnimation(startLabel);
            learninglevel_back.addView(startlabelview);
            startlabelview.bringToFront();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private View overlay;

    @Override
    public void addOverLay(int scrWidth, int scrHeight, int scrollviewHeight) {
        try {
            overlay = mInflater.inflate(R.layout.black_overlay, null);
            overlay.setX(learninglevel_back.getX());
            overlay.setY(learninglevel_back.getY());
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) learninglevel_back.getLayoutParams();
            params.width = scrWidth;
            params.height = scrollviewHeight;
            overlay.setLayoutParams(params);
            learninglevel_back.addView(overlay);
            if (convertView1 != null)
                convertView1.bringToFront();
            if (levelIndicatorView != null)
                levelIndicatorView.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideStartLabel() {
        try {
            if (startLabel != null)
                startLabel.setAnimation(null);
            if (startlabelview != null) {
                startlabelview.setVisibility(View.GONE);
                learninglevel_back.removeView(startlabelview);
            }
            if (overlay != null) {
                overlay.setVisibility(View.GONE);
                learninglevel_back.removeView(overlay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setStartLabelAnimation(ImageView startLabel) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(startLabel, "translationX", 0f, 35f);
            scaleDownX.setDuration(900);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new LinearInterpolator());
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(startLabel, "translationX", 35f, 0f);
            scaleDownY.setDuration(900);
            scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setInterpolator(new LinearInterpolator());
            scaleDownY.start();
            scaleDownX.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int pathY = 180;
    private boolean showAnimation = true;

    @Override
    public void addLevelIcons(int scrWidth, int scrHeight, int scrollviewHeight, int noofLevels, int maxloackedLevel, CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            main_usermanual_layout.setVisibility(View.GONE);
            Log.e(TAG, "inside addLevelIcons() ");
            Log.d(TAG, "addLevelIcons: ");
            //Log.e(TAG, "addLevelIcons: "+scrWidth+" Heoght:"+scrHeight+"scrollViewHe:"+scrollviewHeight+" nof evele:"+noofLevels);
            //Log.e(TAG, "addLevelIcons: lockedlevels:"+maxloackedLevel);
            System.out.println("no of levels:" + noofLevels + " maxlockedlevels:" + maxloackedLevel);
            learninglevel_backa.bringToFront();

            pathY = 180;
            boolean currentLevelExist = false;
            boolean lockFound = false;
            for (int i = 0; i < noofLevels; i++) {
                View levelView;
                boolean currentLevel = false;
                if (((i + 1) == maxloackedLevel) || ((((dtoUserCourseData.getPresentageComplete() > 99) && (dtoUserCourseData.getCurrentCompleteLevel() == (i + 1)))))) {
                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                    } else {
                        currentLevel = true;
                        currentLevelExist = true;
                    }
                }
                if ((addedViews != null) && (addedViews.size() > i) && (addedViews.get(i) != null)) {
                    levelView = addedViews.get(i);
                    TextView levelNoText = levelView.findViewById(R.id.learninglevel_text);
                    setHeavyFont(levelNoText);
                    RelativeLayout locklayout = levelView.findViewById(R.id.locklayout);
                    RelativeLayout unlocklayout = levelView.findViewById(R.id.unlocklayout);
                    RelativeLayout mainlevel_back = levelView.findViewById(R.id.mainlevel_back);
                    RelativeLayout level_lock_ll = levelView.findViewById(R.id.level_lock_ll);
                    level_lock_ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLockedLevelToast();
                        }
                    });

                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                    } else {
                        if (maxloackedLevel < (i + 1)) {
                            unlocklayout.setVisibility(View.GONE);
                            locklayout.setVisibility(View.VISIBLE);
                            OustSdkTools.setTxtBackgroud(levelNoText, R.drawable.learing_unlocklevel);
                        } else {
                            if (courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                                level_lock_ll.setVisibility(View.VISIBLE);
                                lockFound = true;
                            } else {
                                level_lock_ll.setVisibility(View.GONE);
                            }
                            float myXp = dtoUserCourseData.getUserLevelDataList().get(i).getXp();
                            float totalXp = courseDataClass.getCourseLevelClassList().get(i).getTotalXp();
                            float rating = 0;
                            if (totalXp > 0) {
                                rating = ((myXp / totalXp) * 5);
                                rating = Math.round(rating);
                            } else {
                                rating = 5;
                            }
                            if (currentLevel) {
                                addCurrentLevel(scrWidth, i, rating, scrHeight, scrollviewHeight, false);
                            } else {
                                unlocklayout.setVisibility(View.VISIBLE);
                                locklayout.setVisibility(View.GONE);
                                OustSdkTools.setTxtBackgroud(levelNoText, R.drawable.learning_lockedlevel);
                                if (rating > 0) {
                                    ImageView levelstar_a = levelView.findViewById(R.id.levelstar_a);
                                    levelstar_a.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 1) {
                                    ImageView levelstar_b = levelView.findViewById(R.id.levelstar_b);
                                    levelstar_b.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 2) {
                                    ImageView levelstar_c = levelView.findViewById(R.id.levelstar_c);
                                    levelstar_c.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 3) {
                                    ImageView levelstar_d = levelView.findViewById(R.id.levelstar_d);
                                    levelstar_d.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 4) {
                                    ImageView levelstar_e = levelView.findViewById(R.id.levelstar_e);
                                    levelstar_e.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                            }
                        }
                        if (currentLevel) {
                            mainlevel_back.setVisibility(View.GONE);
                        } else {
                            mainlevel_back.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    levelView = mInflater.inflate(R.layout.newlearning_levelback, null);
                    try {
                        ImageView coins_icon = levelView.findViewById(R.id.coins_icon);
                        if (OustPreferences.getAppInstallVariable("showCorn")) {
                            coins_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                        } else {
                            OustSdkTools.setImage(coins_icon, OustSdkApplication.getContext().getResources().getString(R.string.coins_icon));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    TextView levelNoText = levelView.findViewById(R.id.learninglevel_text);
                    setHeavyFont(levelNoText);
                    TextView learningassessmentText_text = levelView.findViewById(R.id.learningassessmentText_text);
                    setHeavyFont(learningassessmentText_text);
                    ImageView lplevelocimage = levelView.findViewById(R.id.lplevelocimage);
                    TextView lpleveloctext = levelView.findViewById(R.id.lpleveloctext);
                    setHeavyFont(lpleveloctext);
                    RelativeLayout locklayout = levelView.findViewById(R.id.locklayout);
                    RelativeLayout unlocklayout = levelView.findViewById(R.id.unlocklayout);
                    RelativeLayout mainlevel_back = levelView.findViewById(R.id.mainlevel_back);
                    RelativeLayout level_lock_ll = levelView.findViewById(R.id.level_lock_ll);
                    level_lock_ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showLockedLevelToast();
                        }
                    });

                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                        unlocklayout.setVisibility(View.GONE);
                        locklayout.setVisibility(View.GONE);
                        levelNoText.setVisibility(View.VISIBLE);
                        levelNoText.setText("A");
                        learningassessmentText_text.setVisibility(View.VISIBLE);
                    } else {
                        levelNoText.setText((i + 1) + "");
                        if (courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                            level_lock_ll.setVisibility(View.VISIBLE);
                            lockFound = true;
                        } else {
                            level_lock_ll.setVisibility(View.GONE);
                        }

                        if (maxloackedLevel < (i + 1)) {
                            unlocklayout.setVisibility(View.GONE);
                            locklayout.setVisibility(View.VISIBLE);
                            OustSdkTools.setTxtBackgroud(levelNoText, R.drawable.learing_unlocklevel);
                            if (courseDataClass.getCourseLevelClassList().get(i).getTotalOc() == 0) {
                                lpleveloctext.setVisibility(View.GONE);
                                lplevelocimage.setVisibility(View.GONE);
                                levelNoText.setPadding(0, 0, 0, 0);
                            } else
                                lpleveloctext.setText("" + courseDataClass.getCourseLevelClassList().get(i).getTotalOc());
                        } else {
                            float myXp = dtoUserCourseData.getUserLevelDataList().get(i).getXp();
                            float totalXp = courseDataClass.getCourseLevelClassList().get(i).getTotalXp();
                            float rating = 0;
                            if (totalXp > 0) {
                                rating = ((myXp / totalXp) * 5);
                                rating = Math.round(rating);
                            } else {
                                rating = 5;
                            }
                            if (currentLevel) {
                                addCurrentLevel(scrWidth, i, rating, scrHeight, scrollviewHeight, false);
                            } else {
                                unlocklayout.setVisibility(View.VISIBLE);
                                locklayout.setVisibility(View.GONE);
                                OustSdkTools.setTxtBackgroud(levelNoText, R.drawable.learning_lockedlevel);
                                if (rating > 0) {
                                    ImageView levelstar_a = levelView.findViewById(R.id.levelstar_a);
                                    levelstar_a.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 1) {
                                    ImageView levelstar_b = levelView.findViewById(R.id.levelstar_b);
                                    levelstar_b.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 2) {
                                    ImageView levelstar_c = levelView.findViewById(R.id.levelstar_c);
                                    levelstar_c.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 3) {
                                    ImageView levelstar_d = levelView.findViewById(R.id.levelstar_d);
                                    levelstar_d.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                                if (rating > 4) {
                                    ImageView levelstar_e = levelView.findViewById(R.id.levelstar_e);
                                    levelstar_e.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
                                }
                            }
                        }
                        if (currentLevel) {
                            mainlevel_back.setVisibility(View.GONE);
                        } else {
                            mainlevel_back.setVisibility(View.VISIBLE);
                            levelView.setAlpha(0);
                            setAnimation(levelView, i);
                        }
                    }
                    levelNoText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                final String leveltext = ((TextView) v).getText().toString();
                                detectClickOnLevel(leveltext);
                            } catch (Exception e) {
                            }
                        }
                    });
                    levelView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                    levelView.setX(getXPosition(60, scrWidth));
                    learninglevel_back.addView(levelView);
                    addedViews.add(levelView);
                }
                pathY += 75;
            }
            if (!currentLevelExist) {
                addCurrentLevel(scrWidth, noofLevels, 0, scrHeight, scrollviewHeight, true);
            }
            pathY += 40;
            if (endLevelView == null) {
                endLevelView = mInflater.inflate(R.layout.learning_endicon_layout, null);
                ImageView ending_icon = endLevelView.findViewById(R.id.ending_icon);
                if (lockFound) {
                    ending_icon.setImageDrawable(getResources().getDrawable(R.drawable.course_finish_lock));
                }
                int levelboxSize1 = (int) (getResources().getDimension(R.dimen.oustlayout_dimen90) / 2);
                float x = (scrWidth * 60 / 320) - levelboxSize1;
                endLevelView.setX(x);
                endLevelView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                if (showAnimation) {
                    endLevelView.setAlpha(0);
                    setAnimation(endLevelView, noofLevels);
                }
                learninglevel_back.addView(endLevelView);
            }
            pathY = 180;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLockedLevelToast() {
        OustSdkTools.showToast(getResources().getString(R.string.level_locked_by_author));
    }

    private void setMediumFont(TextView tv) {
        try {
            if (language == null || (language != null && language.isEmpty()) ||
                    (language != null && !language.isEmpty() && language.equalsIgnoreCase("en"))) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setHeavyFont(TextView tv) {
        try {
            if (language == null || ((language != null) && (language.equalsIgnoreCase("en")))) {
                tv.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCurrentLevel(int scrWidth, int levelNo, float rating, int scrHeight, int scrollviewHeight, boolean hideLevel) {
        try {
            if (currentLevelView == null) {
                currentLevelView = mInflater.inflate(R.layout.newlearning_currentlevelback, null);
                RelativeLayout levelback = currentLevelView.findViewById(R.id.levelback);
                if (hideLevel) {
                    levelback.setVisibility(View.GONE);
                } else {
                    levelback.setVisibility(View.VISIBLE);
                }
                TextView levelNoText1 = currentLevelView.findViewById(R.id.learninglevel_text);
                setHeavyFont(levelNoText1);

                int levelboxSize1 = (int) (getResources().getDimension(R.dimen.oustlayout_dimen10) / 2);
                currentLevelView.setX(((getXPosition(60, scrWidth)) - levelboxSize1));
                currentLevelView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                learninglevel_back.addView(currentLevelView);
                currentLevelView.setAlpha(0);
                setAnimation(currentLevelView, levelNo);
                levelNoText1.setText((levelNo + 1) + "");
                levelNoText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String leveltext = ((TextView) v).getText().toString();
                        detectClickOnLevel(leveltext);
                    }
                });
            }
            TextView levelNoText = currentLevelView.findViewById(R.id.learninglevel_text);
            setHeavyFont(levelNoText);

            levelNoText.setText((levelNo + 1) + "");
            RelativeLayout levelback1 = currentLevelView.findViewById(R.id.levelback);
            if (hideLevel) {
                levelback1.setVisibility(View.GONE);
            } else {
                levelback1.setVisibility(View.VISIBLE);
            }
            currentLevelView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
            ImageView levelstar_a = currentLevelView.findViewById(R.id.levelstar_a);
            ImageView levelstar_b = currentLevelView.findViewById(R.id.levelstar_b);
            ImageView levelstar_c = currentLevelView.findViewById(R.id.levelstar_c);
            ImageView levelstar_d = currentLevelView.findViewById(R.id.levelstar_d);
            ImageView levelstar_e = currentLevelView.findViewById(R.id.levelstar_e);
            levelstar_a.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_emptya));
            levelstar_b.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_emptya));
            levelstar_c.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_emptya));
            levelstar_d.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_emptya));
            levelstar_e.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_emptya));
            if (rating > 0) {
                levelstar_a.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
            }
            if (rating > 1) {
                levelstar_b.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
            }
            if (rating > 2) {
                levelstar_c.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
            }
            if (rating > 3) {
                levelstar_d.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
            }
            if (rating > 4) {
                levelstar_e.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.star_filla));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAnimation(View view, int position) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new LinearInterpolator());
            scaleDownY.setInterpolator(new BounceInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay((150 * (position + 1)));
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<View> addedViews = new ArrayList<>();
    private View currentLevelView;
    private View endLevelView;
    private List<View> levelDescViews = new ArrayList<>();
    private boolean showFinalAnimation = false;

    @Override
    public void addLevelDescription(int scrWidth, int scrHeight, int scrollviewHeight, int noofLevels, int lastlevelno, int currentlevleno, final CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            Log.e("RegularMode", "CCPopup - addLevel");
            for (int i = 0; i < noofLevels; i++) {
                Log.e("RegularMode", "CCPopup - addLevel loop" + i);
                View descView;
                if ((levelDescViews != null) && (levelDescViews.size() > i) && (levelDescViews.get(i) != null)) {
                    descView = levelDescViews.get(i);
                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                    } else {
                        TextView downloadcompletetext = descView.findViewById(R.id.downloadcompletetext);
                        setHeavyFont(downloadcompletetext);

                        GifImageView downloadcard_icon = (GifImageView) descView.findViewById(R.id.downloadcard_icon);
                        if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > i) &&
                                (dtoUserCourseData.getUserLevelDataList().get(i) != null) &&
                                ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() > 0) || (dtoUserCourseData.getUserLevelDataList().get(i).isDownloading()))) {
                            downloadcompletetext.setVisibility(View.VISIBLE);
                            downloadcompletetext.setText(dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() + "%");
                            if ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100)) {
                                dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                                downloadcompletetext.setText("");
                                downloadcompletetext.setVisibility(View.GONE);
                            } else {
                                if ((!dtoUserCourseData.getUserLevelDataList().get(i).isDownloading())) {
                                    downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                                } else {
                                    GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.white_to_green_new);
                                    downloadcard_icon.setImageDrawable(gifFromResource);
                                }
                                if (courseDataClass.getCourseLevelClassList().get(i).getDownloadStatus() == 0) {
                                    final int startDownloadNo = i;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                presenter.clickonDownloadIcon("" + (startDownloadNo));
                                            } catch (Exception e) {
                                            }
                                        }
                                    }, 100);
                                }
                            }

                        }
                    }
                } else {
                    descView = mInflater.inflate(R.layout.learninglevel_description, null);
                    mRelativeLayoutLevelDesc = descView.findViewById(R.id.relativeLayoutLevelDesc);

                    final float scale = getResources().getDisplayMetrics().density;
                    int pixels = (int) (scrWidth * scale + 0.5f) - 100;
                    RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(pixels, 90);
                    //  mRelativeLayoutLevelDesc.setLayoutParams(rel_btn);

                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                        TextView leveldescription = descView.findViewById(R.id.leveldescription);
                        setHeavyFont(leveldescription);
                        if ((courseDataClass.getMappedAssessmentName() != null)) {
                            leveldescription.setText(courseDataClass.getMappedAssessmentName());
                        }
                        TextView downloaddetect = descView.findViewById(R.id.downloaddetect);
                        setHeavyFont(downloaddetect);
                        GifImageView downloadcard_icon = descView.findViewById(R.id.downloadcard_icon);
                        TextView downloadcompletetext = descView.findViewById(R.id.downloadcompletetext);
                        setHeavyFont(downloadcompletetext);
                        downloadcard_icon.setVisibility(View.GONE);
                        downloaddetect.setVisibility(View.GONE);
                        downloadcompletetext.setVisibility(View.GONE);
                        descView.setX(getXPosition(125, scrWidth));
                        descView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                        levelDescViews.add(descView);
                        learninglevel_back.addView(descView);
                    } else {
                        TextView leveldescription = descView.findViewById(R.id.leveldescription);
                        setHeavyFont(leveldescription);
                        if ((courseDataClass.getCourseLevelClassList().get(i).getLevelName() != null)) {
                            leveldescription.setText(courseDataClass.getCourseLevelClassList().get(i).getLevelName());
                        } else if ((courseDataClass.getCourseLevelClassList().get(i).getLevelDescription() != null)) {
                            leveldescription.setText(courseDataClass.getCourseLevelClassList().get(i).getLevelDescription());
                        }
                        TextView downloadcompletetext = descView.findViewById(R.id.downloadcompletetext);
                        setHeavyFont(downloadcompletetext);
                        TextView downloaddetect = descView.findViewById(R.id.downloaddetect);
                        setHeavyFont(downloaddetect);
                        downloaddetect.setText("" + i);
                        GifImageView downloadcard_icon = descView.findViewById(R.id.downloadcard_icon);
                        boolean notDownloaded = false;
                        if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > i) &&
                                (dtoUserCourseData.getUserLevelDataList().get(i) != null) &&
                                ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() > 0) || (dtoUserCourseData.getUserLevelDataList().get(i).isDownloading()))) {
                            downloadcompletetext.setVisibility(View.VISIBLE);
                            downloadcompletetext.setText(dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() + "%");
                            if ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100)) {
                                dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                                downloadcompletetext.setText("");
                                downloadcompletetext.setVisibility(View.GONE);
                            } else {
                                if ((!dtoUserCourseData.getUserLevelDataList().get(i).isDownloading())) {
                                    downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                                }
                                if (courseDataClass.getCourseLevelClassList().get(i).getDownloadStatus() == 0) {
                                    final int startDownloadNo = i;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                presenter.clickonDownloadIcon("" + (startDownloadNo));
                                            } catch (Exception e) {
                                            }
                                        }
                                    }, 100);
                                }
                            }

                        } else {
                            if ((courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList() != null)) {
                                notDownloaded = true;
                                try {
                                    for (int k = 0; k < courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size(); k++) {
                                        int savedCardID = getId(courseDataClass.getCourseId(), courseDataClass.getCourseLevelClassList().get(i).getLevelId(),
                                                courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().get(k).getCardId());
                                        if (OustSdkTools.databaseHandler.getCardClass(savedCardID).getCardId() > 0) {
                                            notDownloaded = false;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    notDownloaded = false;
                                }
                            }
                            if (notDownloaded) {
                                downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                                downloadcompletetext.setText("");
                                downloadcompletetext.setVisibility(View.VISIBLE);
                            } else {
                                downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                                downloadcompletetext.setText("");
                                downloadcompletetext.setVisibility(View.GONE);
                            }
                        }
                        presenter.setDwonloadStatus(courseDataClass);
                        descView.setX(getXPosition(125, scrWidth));
                        descView.setY(getYPosition(pathY, scrHeight, scrollviewHeight));
                        levelDescViews.add(descView);
                        learninglevel_back.addView(descView);
                        downloaddetect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (purchased) {
                                    TextView text = (TextView) v;
                                    OustSdkTools.oustTouchEffect(v, 100);
                                    presenter.clickonDownloadIcon(text.getText().toString());
                                } else {
                                    if (freeCourse) {
                                        OustSdkTools.showToast(getResources().getString(R.string.enroll_course_start));
                                    } else {
                                        OustSdkTools.showToast(getResources().getString(R.string.purchase_course));
                                    }
                                }
                            }
                        });
                        pathY += 75;
                    }
                }
            }

            if ((dtoUserCourseData.getPresentageComplete() == 100) || (dtoUserCourseData.getPresentageComplete() == 95 && dtoUserCourseData.isCourseCompleted())) {
                if (dtoUserCourseData.getMyCourseRating() == 0) {
                    if (currentlevleno == lastlevelno) {
                        if ((showFinalAnimation) && (!isCourseAlreadyComplete)) {
                            if (!isRateCourseShownOnce) {
                                isRateCourseShownOnce = true;
                                //showCourseCompletePopup(courseDataClass);
                                //courseCompletePopup(courseDataClass);
                            }
                            //setCertificateVisibility(realmUserCourseData, courseDataClass);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int getId(long courseID, long levelId, long cardId) {
        int newCardId = (int) cardId;
        return newCardId;
    }

    @Override
    public void startDownloadLevel(CourseLevelClass courseLevelClass, String courseUniqueNoStr) {
        try {
           /* Intent intent = new Intent(RegularModeLearningMapActivity.this, DownloadCardService.class);
            Gson gson = new Gson();
            String str = gson.toJson(courseLevelClass);
            intent.putExtra("courselevelclassstr", str);
            intent.putExtra("courseUniqNo", courseUniqNoStr);
            intent.putExtra("downloadVideo", enableVideoDownload);
            startService(intent)*/

            Gson gson = new Gson();
            String courseLevelStr = gson.toJson(courseLevelClass);

            Intent intent = new Intent(context, DownloadForegroundService.class);
            intent.setAction(START_UPLOAD);
            intent.putExtra(DownloadForegroundService.LEVEL_DATA, courseLevelStr);
            intent.putExtra(DownloadForegroundService.COURSE_UNIQUE, courseUniqueNoStr);
            intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_VIDEO, enableVideoDownload);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private float getXPosition(float x1, int scrWidth) {
        float x = (scrWidth * x1 / 320) - levelboxSize;
        Log.d(TAG, "getXPosition: " + x);
        return x;
    }

    private float getYPosition(float y1, int scrHeight, int scrollViewHeight) {
        float y = scrollViewHeight - (scrHeight * y1 / 480);
        Log.d(TAG, "getYPosition: " + (y + levelboxSize));
        return (y + levelboxSize);
    }

    private float getRealYPosition(float y1, int scrHeight) {
        float y = (scrHeight * y1 / 480);
        Log.d(TAG, "getRealYPosition: " + y);
        return (y);
    }

    @Override
    public void setCurrentLevelPosition(int currentLevel, int scrHeight, int scrollViewHeight) {
        Log.d(TAG, "setCurrentLevelPosition: currentlevel:" + currentLevel + "");
        try {
            if (currentLevel > 1) {
                float n1 = scrollViewHeight - scrHeight - getRealYPosition(((currentLevel - 1) * 75), scrHeight);
                viewclass_scrollview.smoothScrollTo(0, (int) n1);
            } else {
                viewclass_scrollview.scrollTo(0, viewclass_scrollview.getBottom());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onScrollPostMeth() {
        try {
            viewclass_scrollview.post(new Runnable() {
                @Override
                public void run() {
                    presenter.onScrollPost();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void detectClickOnLevel(String levelTxtStr) {
        try {
            if (purchased) {
                if (!locked) {
                    if ((!isLearningCardOpen) && (activityLive)) {
                        isLearningCardOpen = true;
                        if (levelTxtStr.equalsIgnoreCase("A")) {
                            presenter.clickOnAssessmentIcon();
                        } else {
                            int levelNo = Integer.parseInt(levelTxtStr);
                            presenter.clickOnLevelIcon(levelNo);
                        }
                    }
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.complete_course_unlock));
                }
            } else {
                if (freeCourse) {
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

    @Override
    public void enableClick() {
        isLearningCardOpen = false;
    }

    @Override
    public void gotoQuizPage(int currentLearningPathId, CourseDataClass courseDataClass, int levelNo, CourseLevelClass courseLevelClass) {
        try {
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            if (levelNo == 0 || levelNo == 1) {
                String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                //int courseUniqNo = Integer.parseInt(s1);
                long courseUniqNo = Long.parseLong(s1);
                Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);

                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                if (userCourseData1.getDownloadCompletePercentage() < 100) {
                    startCourseDownloadService(courseDataClass);
                }
            }
            Log.e("RegularMode", "Go to Quiz");
            Intent intent = new Intent(RegularModeLearningMapActivity.this, LearningMapModuleActivity.class);
            Gson gson = new Gson();
            String courseDataStr = gson.toJson(courseDataClass);
            String courseLevelStr = gson.toJson(courseLevelClass);
            OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
            OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
//            intent.putExtra("courseLevelStr", courseLevelStr);
//            intent.putExtra("courseDataStr", courseDataStr);
            intent.putExtra("containCertificate", courseDataClass.isCertificate());
            intent.putExtra("learningId", ("" + currentLearningPathId));
            intent.putExtra("levelNo", levelNo);
            intent.putExtra("isRegularMode", true);
            intent.putExtra("isReviewMode", false);
            intent.putExtra("isComingFromCpl", isComingFromCpl);
            intent.putExtra("isDisableLevelCompletePopup", courseDataClass.isDisableLevelCompletePopup());
            intent.putExtra("COURSE_COMPLETED", isCourseCompleted);
            //intent.putExtra("ifScormEventBased",ifScormEventBased);
            intent.putExtra("isDownloadVideo", enableVideoDownload);
            intent.putExtra("enableLevelCompleteAudio", enableLevelCompleteAudio);

            try {
                long deadline = Long.parseLong(courseDataClass.getCourseDeadline());
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

            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                intent.putExtra("courseColnId", courseColnId);
            }
            OustSdkTools.newActivityAnimationB(intent, RegularModeLearningMapActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoQuizPageReviewMode(int currentLearningPathId, CourseDataClass courseDataClass, int levelNo, int reviewModeQuestionNo) {
        try {
            CourseLevelClass courseLevelClass = new CourseLevelClass();
            if ((filteredsearchCourseLevelList != null) && (filteredsearchCourseLevelList.size() > 0)) {
                SearchCourseLevel searchCourseLevel = filteredsearchCourseLevelList.get(levelNo);
                courseLevelClass.setLevelId(searchCourseLevel.getId());
                courseLevelClass.setLevelName(searchCourseLevel.getName());
                courseLevelClass.setLevelDescription(searchCourseLevel.getDescription());
                courseLevelClass.setRefreshTimeStamp(searchCourseLevel.getRefreshTimeStamp());
                courseLevelClass.setSequence(searchCourseLevel.getSequence());
                courseLevelClass.setLpId(searchCourseLevel.getLpId());
                if (courseDataClass.isSalesMode()) {

                }
                courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                courseLevelClass.setTotalXp(searchCourseLevel.getXp());
                List<DTOCourseCard> courseCardClassList = new ArrayList<>();

                for (int i = 0; i < filteredsearchCourseLevelList.get(levelNo).getSearchCourseCards().size(); i++) {
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setCardType(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getCardType());
                    courseCardClass.setCardId(filteredsearchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getId());
                    courseCardClass.setSequence(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getSequence());
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
                if (courseDataClass.isSalesMode()) {

                }
                courseLevelClass.setTotalOc(searchCourseLevel.getTotalOc());
                courseLevelClass.setTotalXp(searchCourseLevel.getXp());
                List<DTOCourseCard> courseCardClassList = new ArrayList<>();

                for (int i = 0; i < searchCourseLevelList.get(levelNo).getSearchCourseCards().size(); i++) {
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setCardType(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getCardType());
                    courseCardClass.setCardId(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getId());
                    courseCardClass.setSequence(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getSequence());
                    courseCardClass.setXp(searchCourseLevelList.get(levelNo).getSearchCourseCards().get(i).getXp());
                    courseCardClassList.add(courseCardClass);
                }
                courseLevelClass.setCourseCardClassList(courseCardClassList);
            }
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            if (levelNo == 0 || levelNo == 1) {
                String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                long courseUniqNo = Long.parseLong(s1);
                Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);
                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                if (userCourseData1.getDownloadCompletePercentage() < 100) {
                    startCourseDownloadService(courseDataClass);
                }
            }
            Log.e("RegularMode", "gotoQuizPageReviewMode");
            Intent intent = new Intent(RegularModeLearningMapActivity.this, LearningMapModuleActivity.class);
            Gson gson = new Gson();
            String courseLevelStr = gson.toJson(courseLevelClass);
            String courseDataStr = gson.toJson(courseDataClass);
            OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
            OustStaticVariableHandling.getInstance().setCourseLevelStr(courseLevelStr);
//            intent.putExtra("courseLevelStr", courseLevelStr);
//            intent.putExtra("courseDataStr", courseDataStr);
            intent.putExtra("containCertificate", courseDataClass.isCertificate());
            intent.putExtra("learningId", ("" + currentLearningPathId));
            intent.putExtra("levelNo", levelNo);
            intent.putExtra("reviewModeQuestionNo", reviewModeQuestionNo);
            intent.putExtra("isRegularMode", true);
            intent.putExtra("isReviewMode", false);
            intent.putExtra("isDisableLevelCompletePopup", courseDataClass.isDisableLevelCompletePopup());
            intent.putExtra("COURSE_COMPLETED", isCourseCompleted);
            //intent.putExtra("ifScormEventBased",ifScormEventBased);
            intent.putExtra("isDownloadVideo", enableVideoDownload);
            intent.putExtra("enableLevelCompleteAudio", enableLevelCompleteAudio);

            try {
                long deadline = Long.parseLong(courseDataClass.getCourseDeadline());
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

            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                intent.putExtra("courseColnId", courseColnId);
            }
            OustSdkTools.newActivityAnimationB(intent, RegularModeLearningMapActivity.this);
            //startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoAssessmentPage(int currentLearningPathId, CourseDataClass courseDataClass) {
        Log.d(TAG, "gotoAssessmentPage: ");
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            Intent intent;
            if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                intent = new Intent(RegularModeLearningMapActivity.this, AssessmentDetailScreen.class);
            } else {
                intent = new Intent(RegularModeLearningMapActivity.this, AssessmentPlayActivity.class);
            }
            //Intent intent = new Intent(RegularModeLearningMapActivity.this, AssessmentDetailScreen.class);
            intent.putExtra("courseId", ("" + currentLearningPathId));
            //courseId
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                intent.putExtra("courseColnId", courseColnId);
            }

            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (dtoUserCourseData.getPresentageComplete() > 99) {
                intent.putExtra("isFromWorkDairy", true);
            }

            intent.putExtra("IS_FROM_COURSE", true);
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
            intent.putExtra("assessmentId", ("" + courseDataClass.getMappedAssessmentId()));
//            Gson gson = new Gson();
//            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("containCertificate", courseDataClass.isCertificate());
            intent.putExtra("isSurveyFromCourse", isSurveyAttached);
            startActivity(intent);
//            OustSdkTools.newActivityAnimationB(intent, NewLearningMapActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setIndicatorPosition(int lastPlayingLevel, int currentlevleno, int scrWidth, int scrHeight, int scrollViewHeight, DTOUserCourseData dtoUserCourseData, CourseDataClass courseDataClass) {
        //Log.d(TAG, "setIndicatorPosition: lastPLevel:" + lastPlayingLevel + " currentLvel:" + currentlevleno + " ");
        //Log.d(TAG, "setIndicatorPosition: " + realmUserCourseData.getCurrentLevel());
        try {
            if (levelIndicatorView != null) {
                if ((dtoUserCourseData.getPresentageComplete() > 99) && (currentlevleno == lastPlayingLevel)) {
                    presenter.setCourseComplete(true);
                    if (levelIndicatorView != null) {
                        if ((dtoUserCourseData.getCurrentCompleteLevel() == 0)) {
                            if (courseDataClass.getAssessmentCompletionDate() != null && !courseDataClass.getAssessmentCompletionDate().isEmpty()) {
                                levelIndicatorView.setY(getYPosition((140 + (75 * (currentlevleno + 1))), scrHeight, scrollViewHeight));
                            } else {
                                levelIndicatorView.setY(getYPosition((140 + (75 * currentlevleno)), scrHeight, scrollViewHeight));
                            }
                        } else {
                            levelIndicatorView.setY(getYPosition((140 + (75 * dtoUserCourseData.getLastCompleteLevel())), scrHeight, scrollViewHeight));
                        }
                    }
                } else {
                    presenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        if (lastPlayingLevel == 0) {
                            levelIndicatorView.setY(getYPosition(140, scrHeight, scrollViewHeight));
                        } else {
                            levelIndicatorView.setY(getYPosition((140 + (75 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                        }
                    }
                }
            } else {
                levelIndicatorView = mInflater.inflate(R.layout.larning_indicator, null);
                float startX = (int) ((getResources().getDimension(R.dimen.oustlayout_dimen80)) / 2);
                levelIndicatorView.setX((getXPositiona(60, scrWidth)) - startX);
                if ((dtoUserCourseData.getPresentageComplete() > 99) && (currentlevleno == lastPlayingLevel)) {
                    presenter.setCourseComplete(true);

                    if (levelIndicatorView != null) {
                        if ((dtoUserCourseData.getCurrentCompleteLevel() == 0)) {
                            if (courseDataClass.getAssessmentCompletionDate() != null && !courseDataClass.getAssessmentCompletionDate().isEmpty()) {
                                levelIndicatorView.setY(getYPosition((140 + (75 * (currentlevleno + 1))), scrHeight, scrollViewHeight));
                            } else {
                                levelIndicatorView.setY(getYPosition((140 + (75 * currentlevleno)), scrHeight, scrollViewHeight));
                            }
                        } else {
                            levelIndicatorView.setY(getYPosition((140 + (75 * dtoUserCourseData.getLastCompleteLevel())), scrHeight, scrollViewHeight));
                        }
                    }
                } else {
                    presenter.setCourseComplete(false);
                    if (levelIndicatorView != null) {
                        if (lastPlayingLevel == 0) {
                            levelIndicatorView.setY(getYPosition(140, scrHeight, scrollViewHeight));
                        } else {
                            levelIndicatorView.setY(getYPosition((140 + (75 * lastPlayingLevel)), scrHeight, scrollViewHeight));
                        }
                    }
                }
                learninglevel_back.addView(levelIndicatorView);
            }
            levelIndicatorView.bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private float getXPositiona(float x1, int scrWidth) {
        float x = scrWidth * x1 / 320;
        return x;
    }

    @Override
    public void setCertificateVisibility(DTOUserCourseData dtoUserCourseData, final CourseDataClass courseDataClass) {
        if (dtoUserCourseData.isCourseComplete() || (dtoUserCourseData.getPresentageComplete() == 100)) {
            if (courseDataClass.isCertificate()) {
                course_certificate_2.setVisibility(View.VISIBLE);
                course_certificate_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iscertificatebtnclicked = true;
                        sendCertificatePopup(courseDataClass, false);
                    }
                });
            }
        }
    }


    @Override
    public void setLevelChangeAnim(final int currentLevel, final int scrWidth, final int scrHeight, final int scrollViewHeight, final int totalNoofLevels, final CourseDataClass courseDataClass) {
        try {
            OustAppState.getInstance().setHasPopup(true);
            levelIndicatorView.animate().translationYBy(-getRealYPosition(75, scrHeight)).setDuration(1400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        if (currentLevel <= totalNoofLevels) {
                            if ((currentLevel == (totalNoofLevels)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                                playCourseCompleteAudio();
                                showFinalAnimation = true;
                            } else {
                                detectClickOnLevel("" + currentLevel);
                            }
                        } else {
                            showFinalAnimation = true;
                            playCourseCompleteAudio();
                        }
                        presenter.setLastLevelNo();
                        presenter.startLearningMap(false);
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isLearningCardOpen = false;
    private String levelNoToRestartAfterPermission;

    @Override
    public void onClick(View view) {
        try {
            Log.d(TAG, "onClick: method");
            int id = view.getId();
            if (id == R.id.lpocimage) {
            } else if (id == R.id.leaderboard_button) {
                presenter.clcikOnLeaderBoard();
            } else if (id == R.id.learning_closebtn) {
                RegularModeLearningMapActivity.this.finish();
            } else if (id == R.id.um_serachclose) {
                filterList(um_serachedittext.getText().toString());
            } else if (id == R.id.um_serachbackbtn) {
                um_serachedittext.setText("");
                um_serachviewlayout.setVisibility(View.GONE);
                hideKeyboard(um_serachedittext);
            } else if (id == R.id.um_search_button) {
                if (um_serachviewlayout.getVisibility() == View.GONE) {
                    um_serachedittext.requestFocus();
                    showKeyboard();
                    um_serachviewlayout.setVisibility(View.VISIBLE);
                }
            } else if (id == R.id.bulletin_board) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bulletinboard));
                } else {
                    OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bulletinboard));
                }
                presenter.clickOnBulletinBoard();
            } else if ((id == R.id.course_info_btna) || (id == R.id.course_info_bottom_layout)) {
                Log.d(TAG, "onClick: more info");
                if ((cardInfo != null) && (!cardInfo.isEmpty())) {
                    checkForDescriptionCardData(cardInfo);
                }
            } else if (id == R.id.download_course_layout) {
                Log.d(TAG, "Download start course");
                startCourseDownload();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startCourseDownload() {
        Log.d(TAG, "courseAlreadyDownloaded:" + courseAlreadyDownloaded + " --- downloaCourseClicked:" + downloaCourseClicked);
        if (!courseAlreadyDownloaded && !downloaCourseClicked) {
            presenter.clickOnCourseDownload();
            downloaCourseClicked = true;
            courseAlreadyDownloaded = true;
        }
    }

    private boolean downloaCourseClicked = false;

    @Override
    public void downloadCourse(CourseDataClass courseDataClass) {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
            //int courseUniqNo = (int) Long.parseLong(s1);
            long courseUniqNo = Long.parseLong(s1);
            Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();

            DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
            Log.d(TAG, "downloadCourse: " + userCourseData1.getDownloadCompletePercentage());

            if (userCourseData1.getDownloadCompletePercentage() < 100) {
                userCourseData1.setDownloading(true);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData1, courseUniqNo);
                startCourseDownloadService(courseDataClass);
                GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.white_to_green_new);
                downloding_course_gif.setImageDrawable(gifFromResource);
                download_course_img.setVisibility(View.GONE);
                downloding_course_gif.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCourseDownloadService(CourseDataClass courseDataClass) {
        if (!allCourseDownloadStarted) {
            allCourseDownloadStarted = true;
            Log.d(TAG, "startCourseDownloadService: ");
           /* Intent intent = new Intent(OustSdkApplication.getContext(), DownloadCourseService.class);
            Gson gson = new Gson();
            String str = gson.toJson(courseDataClass);
            intent.putExtra("courseDataStr", str);
            intent.putExtra("courseId", courseDataClass.getCourseId());
            intent.putExtra("isDownloadingOnlyCourse", true);
            intent.putExtra("isRegularMode", true);
            intent.putExtra("downloadVideo", enableVideoDownload);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);*/


            boolean isServiceRunning = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();
                String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                long courseUniqNo = Long.parseLong(s1);
                for (StatusBarNotification notification : barNotifications) {
                    if (notification.getId() == courseUniqNo) {
                        isServiceRunning = true;
                        break;
                    }
                }
            }


            //TODO: handle intent for download
            if (!isServiceRunning) {
                Gson gson = new Gson();
                String courseDataStr = gson.toJson(courseDataClass);

                Intent intent = new Intent(RegularModeLearningMapActivity.this, DownloadForegroundService.class);
                intent.setAction(START_UPLOAD);
                intent.putExtra(DownloadForegroundService.COURSE_DATA, courseDataStr);
                intent.putExtra(DownloadForegroundService.COURSE_ID, courseDataClass.getCourseId());
                intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_COURSE, true);
                intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_VIDEO, enableVideoDownload);
                startService(intent);
            }
        }
    }

    private String coursenoToRestartAfterPermission;


    @Override
    public void updateLevelFromAssessment() {
        Log.d(TAG, "updateLevelFromAssessment: ");
        presenter.startLearningMap(false);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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

    //override is commented for exoplayer upgradation
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
    }

    //override is commented for exoplayer upgradation
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    public class CourseDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "learningmap_course_download";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "onReceive: " + intent.getAction());
                coursenoToRestartAfterPermission = "";
                coursenoToRestartAfterPermission = intent.getStringExtra("courseId");
                if ((coursenoToRestartAfterPermission != null) && (!coursenoToRestartAfterPermission.isEmpty())) {
                    ActivityCompat.requestPermissions(RegularModeLearningMapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }

                if (intent.hasExtra("PROGRESS")) {
                    Log.d(TAG, "onReceive Progress: " + intent.getFloatExtra("PROGRESS", 0));
                    courseDownloadingComplete();
                    return;
                }
                courseDownloading();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void courseDownloadingComplete() {

        try {
            //int courseDownloaded = 100;
            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
            //int courseUniqNo = Integer.parseInt(s1);
            long courseUniqNo = Long.parseLong(s1);
            Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);

            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
            mUserCourseData = new DTOUserCourseData();
            mUserCourseData = userCourseData;
            presenter.updateLevelDownloadStatus();

            if (userCourseData != null && userCourseData.isDownloading()) {
                Log.d(TAG, "courseDownloading :" + userCourseData.getDownloadCompletePercentage());
                download_course_text.setText("");
                download_course_text.setVisibility(View.GONE);
                downloding_course_gif.setVisibility(View.GONE);
                download_course_img.setVisibility(View.VISIBLE);

                mUserLevelDataList = new ArrayList<>();
                mUserCourseData = new DTOUserCourseData();
                mUserCourseData = userCourseData;
                mUserLevelDataList = userCourseData.getUserLevelDataList();

                enableCourseDataDelete();
                download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                presenter.startLearningMap(false);

            } else {
                setDownloadCourseIcon(userCourseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void autoStartCourse(CourseDataClass courseDataClass, int levelPosition, int cardPosition) {
        try {
            if (courseDataClass.isStartFromLastLevel()) {
            }
            presenter.clickOnUserManualRow(levelPosition, cardPosition, isRegularMode);
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
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.clickOnCourseDownload();
                } else {
                    download_course_img.setVisibility(View.VISIBLE);
                    downloding_course_gif.setVisibility(View.GONE);
                    downloaCourseClicked = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*public void courseDownloading(float progress) {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
            int courseUniqNo = Integer.parseInt(s1);
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            UserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
            presenter.updateLevelDownloadStatus();
            if (userCourseData != null && userCourseData.isDownloading())
            {
                if (progress == 100) {
                    download_course_text.setText("");
                    download_course_text.setVisibility(View.GONE);
                    downloding_course_gif.setVisibility(View.GONE);
                    download_course_img.setVisibility(View.VISIBLE);
                    mUserLevelDataList = new ArrayList<>();
                    mUserCourseData = new UserCourseData();
                    mUserCourseData = userCourseData;
                    mUserLevelDataList = userCourseData.getUserLevelDataList();
                    enableCourseDataDelete();
                    download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                } else {
                    disableCourseDataDelete();
                    download_course_text.setText(userCourseData.getDownloadCompletePercentage() + "%");
                    download_course_text.setVisibility(View.VISIBLE);
                    download_course_img.setVisibility(View.GONE);
                    downloding_course_gif.setVisibility(View.VISIBLE);
                }
            } else {
                setDownloadCourseIcon(userCourseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void courseDownloading() {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + learningId;
            //int courseUniqNo = Integer.parseInt(s1);
            long courseUniqNo = Long.parseLong(s1);
            Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);

            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
            mUserCourseData = new DTOUserCourseData();
            mUserCourseData = userCourseData;
            presenter.updateLevelDownloadStatus();
            if (userCourseData != null && userCourseData.isDownloading()) {
                Log.d(TAG, "courseDownloading :" + userCourseData.getDownloadCompletePercentage());
                if (userCourseData.getDownloadCompletePercentage() == 100) {
                    download_course_text.setText("");
                    download_course_text.setVisibility(View.GONE);
                    downloding_course_gif.setVisibility(View.GONE);
                    download_course_img.setVisibility(View.VISIBLE);

                    mUserLevelDataList = new ArrayList<>();
                    mUserCourseData = new DTOUserCourseData();
                    mUserCourseData = userCourseData;
                    mUserLevelDataList = userCourseData.getUserLevelDataList();

                    enableCourseDataDelete();
                    download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                    presenter.startLearningMap(false);
                    //presenter.checkIfAutoPlay(null);
                } else {
                    disableCourseDataDelete();
                    download_course_text.setText(userCourseData.getDownloadCompletePercentage() + "%");
                    download_course_text.setVisibility(View.VISIBLE);
                    download_course_img.setVisibility(View.GONE);
                    downloding_course_gif.setVisibility(View.VISIBLE);
                }
            } else {
                setDownloadCourseIcon(userCourseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enableCourseDataDelete() {
        if (mUserLevelDataList != null)
            for (int i = 0; i < mUserLevelDataList.size(); i++) {
                if (RoomHelper.checkMapTableExist((int) mUserLevelDataList.get(i).getLevelId())) {

                    mImageViewDeleteCourseData.setVisibility(View.VISIBLE);
                    break;
                } else {
                    disableCourseDataDelete();
                }
            }
        else {
            disableCourseDataDelete();
        }
    }


    public class MyDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, "LevelDownloadReceiver:" + intent.toString());
                courseDownloading();
                levelNoToRestartAfterPermission = "";
                levelNoToRestartAfterPermission = intent.getStringExtra("levelNo");
                if ((levelNoToRestartAfterPermission != null) && (!levelNoToRestartAfterPermission.isEmpty())) {
                    ActivityCompat.requestPermissions(RegularModeLearningMapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                } else {
                    if (isReviewMode) {
                        CourseDataClass courseDataClass = presenter.getCourseDataClass();
                        AssessmentNavModel assessmentNavModel = null;
                        if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                            assessmentNavModel = new AssessmentNavModel();
                            assessmentNavModel.setCurrentLearningPathId(OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
                            assessmentNavModel.setCourseColnId(courseColnId);
                            assessmentNavModel.setCertificate(courseDataClass.isCertificate());
                            assessmentNavModel.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                            assessmentNavModel.setMappedAssessmentName(courseDataClass.getMappedAssessmentName());
                            assessmentNavModel.setMappedAssessmentImage(courseDataClass.getMappedAssessmentImage());
                            assessmentNavModel.setMappedAssessmentPercentage(courseDataClass.getMappedAssessmentPercentage());
                        }

                        if (reviewModeAdaptor != null) {
                            reviewModeAdaptor.notifyDateChanges(searchCourseLevelList, false, assessmentNavModel);
                        }
                    } else {
                        presenter.updateLevelDownloadStatus();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @Override
    public void updateLevelDownloadStatus(int noofLevels,
                                          final CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            for (int i = 0; i < noofLevels; i++) {
                View descView;
                if ((levelDescViews != null) && (levelDescViews.size() > i) && (levelDescViews.get(i) != null)) {
                    descView = levelDescViews.get(i);
                    if ((i == (noofLevels - 1)) && (courseDataClass.getMappedAssessmentId() > 0)) {
                    } else {
                        TextView downloadcompletetext = descView.findViewById(R.id.downloadcompletetext);
                        setHeavyFont(downloadcompletetext);
                        GifImageView downloadcard_icon = (GifImageView) descView.findViewById(R.id.downloadcard_icon);
                        if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > i) &&
                                (dtoUserCourseData.getUserLevelDataList().get(i) != null) &&
                                ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() > 0) || (dtoUserCourseData.getUserLevelDataList().get(i).isDownloading()))) {
                            downloadcompletetext.setText(dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() + "%");

                            downloadcompletetext.setVisibility(View.VISIBLE);
                            if ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100)) {
                                dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_download_orange));
                                downloadcompletetext.setText("");
                                downloadcompletetext.setVisibility(View.GONE);
                            } else {
                                if ((!dtoUserCourseData.getUserLevelDataList().get(i).isDownloading())) {
                                    downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                                } else {
                                    GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.white_to_green_new);
                                    downloadcard_icon.setImageDrawable(gifFromResource);
                                }
                            }

                        } else {
                            downloadcard_icon.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {

        }
    }

    private void restartDownload() {
        try {
            DTOUserCourseData dtoUserCourseData = presenter.getDtoUserCourseData();
            if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null)) {
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    if (("" + dtoUserCourseData.getUserLevelDataList().get(i).getLevelId()).equalsIgnoreCase(levelNoToRestartAfterPermission)) {
                        presenter.restartDownload("" + (i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startUpdatedLearningMap(boolean killActivity,
                                        boolean updateReviewList) {
        if (presenter != null) {
            if (killActivity) {
                presenter.killActivity();
            } else {
                presenter.onResumeCalled(updateReviewList);
            }
        }
    }

    @Override
    public void restartCourseLearningModuleActivity(boolean restartLevelStatus, int levelId) {

    }

    @Override
    public void upDateLevelTime(boolean restartLevelStatus, int levelId) {

    }

//    public LearningCallBackInterface learningCallBackInterface=new LearningCallBackInterface() {
//        @Override
//        public void startUpdatedLearningMap(boolean killActivity,boolean updateReviewList) {
//            if(presenter!=null) {
//                if (killActivity) {
//                    presenter.killActivity();
//                } else {
//                    presenter.onResumeCalled(updateReviewList);
//                }
//            }
//        }
//    };

//--------------------------

    public void showLoader() {
        Log.d(TAG, "showLoader: ");
        try {
            /*ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(lpmain_loader, "rotation", 0f, 360f);
            imageViewObjectAnimator.setDuration(1500); // miliseconds
            imageViewObjectAnimator.setRepeatCount(5);
            imageViewObjectAnimator.start();*/
            mainloader_back.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void sendUnlockPathApi(int currentLearningPathId) {
        try {
            hideStartLabel();
            showLoader();
            presenter.enrolledLp(currentLearningPathId, OustAppState.getInstance().getActiveUser().getStudentid(), courseColnId, multilingualID);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showPopup(Popup popup) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            Intent intent = new Intent(RegularModeLearningMapActivity.this, PopupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean activityLive = true;

    @Override
    protected void onDestroy() {
        try {
            showstored_Popup();
            activityLive = false;
            OustAppState.getInstance().setLearningCallBackInterface(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
            if (readFragment != null) {
                getSupportFragmentManager().popBackStack();
                feedcard_toplayout.setVisibility(View.VISIBLE);
                return;
            }
            if (OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
                ModuleOverViewFragment moduleFragment = (ModuleOverViewFragment) (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment"));
                if (getSupportFragmentManager().findFragmentByTag("moduleOverViewFragment") != null) {
                    moduleFragment.setPotraitVid(true);
                    moduleFragment.setPotraitVideoRatio();
                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                }
                return;
            }
            if ((coursecomplete_popup != null) && (coursecomplete_popup.isShowing())) {
                return;
            }
            if ((rateus_popup != null) && (rateus_popup.isShowing())) {
                return;
            }

            if (isEvent && OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                DTOUserCourseData realmUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if (realmUserCourseData != null) {
                    UserEventCourseData userEventCourseData = new UserEventCourseData();
                    if (realmUserCourseData.getPresentageComplete() == 100) {
                        userEventCourseData.setUserCompletionPercentage(100);
                        userEventCourseData.setUserProgress("COMPLETED");
                    } else {
                        userEventCourseData.setUserCompletionPercentage(realmUserCourseData.getPresentageComplete());
                        userEventCourseData.setUserProgress("INPROGRESS");
                    }
                    userEventCourseData.setEvenId(eventId);
                    userEventCourseData.setCourseId(Long.parseLong(learningId));

                    CourseDataClass courseDataClass = presenter.getCourseDataClass();
                    if (OustAppState.getInstance().getAssessmentFirebaseClass() != null && OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData() != null) {
                        //userEventCourseData.setUserEventAssessmentData(OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData());
                        userEventAssessmentData = OustAppState.getInstance().getAssessmentFirebaseClass().getUserEventAssessmentData();
                        OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(null);
                    } else {
                        String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                        long courseUniqNo = Long.parseLong(s1);
                        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                        DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);

                        if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0 && userCourseData != null) {
                            //UserEventAssessmentData userEventAssessmentData = new UserEventAssessmentData();
                            userEventAssessmentData.setAssessmentId(courseDataClass.getMappedAssessmentId());
                            //userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());

                            userEventAssessmentData.setUserProgress("DISTRIBUTED");
                            if (userCourseData.getPresentageComplete() >= 100 && userCourseData.isMappedAssessmentCompleted()) {
                                userEventAssessmentData.setUserCompletionPercentage(100);
                                userEventAssessmentData.setUserProgress("COMPLETED");
                                userEventAssessmentData.setPassed(userCourseData.isMappedAssessmentPassed());
                            } else {
                                if (userCourseData.getPresentageComplete() >= 95 || courseDataClass.isMappedAssessmentEnrolled()) {
                                    userEventAssessmentData.setUserProgress("INPROGRESS");
                                }
                                userEventAssessmentData.setUserCompletionPercentage(0);
                            }
                            //userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                        }
                    }
                    if (courseDataClass != null && courseDataClass.getMappedAssessmentId() > 0) {
                        userEventCourseData.setUserEventAssessmentData(userEventAssessmentData);
                    }

                    if (isComingFromCpl) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setEventId(eventId);
                        userEventCplData.setCurrentModuleId(Long.parseLong(learningId));
                        userEventCplData.setCurrentModuleType("Course");
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");

                        userEventCplData.setnTotalModules(totalModules);
                        if (realmUserCourseData.getPresentageComplete() >= 100) {
                            userEventCplData.setCurrentModuleProgress("Completed");
                            userEventCplData.setnModulesCompleted(completedModules + 1);
                            if (completedModules >= totalModules) {
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
            }

            activityLive = false;
            if (isReviewMode) {
                if (um_serachedittext.getText().toString().length() > 0) {
                    um_serachedittext.setText("");
                } else if (um_serachviewlayout.getVisibility() == View.VISIBLE) {
                    um_serachviewlayout.setVisibility(View.GONE);
                } else {
                    super.onBackPressed();
                }
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    activityLive = true;
                    presenter.clickOnEnrolleLp(true);
                }
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void endActivity() {
        RegularModeLearningMapActivity.this.finish();
    }

    private void playCourseCompleteAudio() {
        try {
//            MediaPlayer mediaPlayer = MediaPlayer.create(NewLearningMapActivity.this, R.raw.win_game);
//            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=================================================================================
    private PopupWindow coursecomplete_popup;

    private void playNewCourseCompleteAudio() {
        try {
            Log.d(TAG, "playNewCourseCompleteAudio: ");
            isSoundTobePlayed = false;
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
                                if (file != null && file.exists()) {
                                    completionAudioPlayer = MediaPlayer.create(RegularModeLearningMapActivity.this, Uri.fromFile(file));
                                    completionAudioPlayer.start();
                                } else {
                                    isPlayDefault = true;
                                }
                            } else {
                                isPlayDefault = true;
                            }

                            if (isPlayDefault) {
                                File file2 = new File(OustSdkApplication.getContext().getFilesDir(), "win_game.mp3");
                                if (file2 != null && file2.exists()) {
                                    completionAudioPlayer = MediaPlayer.create(RegularModeLearningMapActivity.this, Uri.fromFile(file2));
                                    completionAudioPlayer.start();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, 700);
                }
            }
            //}

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isSoundTobePlayed) {
            playNewCourseCompleteAudio();
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "InflateParams"})
    public void courseCompletePopup(final CourseDataClass courseDataClass) {

        final boolean[] dontShowRatePopup = new boolean[1];
        DTOUserCourseData dtoUserCourseData = presenter.getDtoUserCourseData();
        isScoreDisplaySecondTime = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME);

        if (showCourseCompletionPopup) {
            isSoundTobePlayed = true;

            View courseCompletionDialog = getLayoutInflater().inflate(R.layout.course_completion_popup, null);
            coursecomplete_popup = OustSdkTools.createPopUp(courseCompletionDialog);

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
                content_completed_message.setVisibility(View.VISIBLE);
            } else if (surveyId != 0) {
                completed_text.setText("You have completed the learning section of ");
                content_completed_message.setText(getResources().getString(R.string.get_started_survey));
                content_completed_message.setVisibility(View.VISIBLE);
            }

            if (courseDataClass.getBadgeName() != null && !courseDataClass.getBadgeName().isEmpty()) {
                badge_name_layout.setVisibility(View.VISIBLE);
                badge_name_text.setText(courseDataClass.getBadgeName());
            }

            if (courseDataClass.getBadgeIcon() != null && !courseDataClass.getBadgeIcon().isEmpty()) {
                Glide.with(RegularModeLearningMapActivity.this).load(courseDataClass.getBadgeIcon()).into(congrats_image);
            }

            content_title.setText(courseDataClass.getCourseName());

            String completedDate = getResources().getString(R.string.completed_on) + " " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
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
                    leader_board.setVisibility(View.VISIBLE);
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
                    layout_ResultScore.setVisibility(View.VISIBLE);
                } else {
                    layout_ResultScore.setVisibility(View.GONE);
                }

            }

            if (courseDataClass.isCertificate()) {
                certificate_lay.setVisibility(View.VISIBLE);
                if (courseDataClass.getMappedAssessmentId() == 0) {
                    certificate_lay.setVisibility(View.VISIBLE);
                }
            } else {
                certificate_lay.setVisibility(View.GONE);
            }

            long totalOc = dtoUserCourseData.getTotalOc();
            long courseOc = courseDataClass.getTotalOc();

            String yourCoinText = totalOc + "";
            if (courseOc != 0) {
                yourCoinText = totalOc + "/" + courseOc;
                String[] spilt = yourCoinText.split("/");
                Spannable yourCoinSpan = new SpannableString(yourCoinText);
                yourCoinSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                yourCoinSpan.setSpan(new RelativeSizeSpan(1.25f), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                coins_earned.setText(yourCoinSpan);
            } else {
                coins_earned.setText(yourCoinText);
            }

            if (totalOc == 0) {
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
            if (totalXp == 0 || myXp == 0) {
                score_lay.setVisibility(View.GONE);
            }

            courseCompletionDialog.setFocusableInTouchMode(true);
            courseCompletionDialog.requestFocus();

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(congrats_image,
                    PropertyValuesHolder.ofFloat("scaleX", 1.75f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.75f));
            scaleDown.setDuration(2000);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.start();

            //playNewCourseCompleteAudio();

            content_result_action.setOnClickListener(v -> {

                Log.d(TAG, "courseCompletePopup: ");
                try {
                    if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                        completionAudioPlayer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if ((coursecomplete_popup != null) && (coursecomplete_popup.isShowing())) {
                    coursecomplete_popup.dismiss();

                    if (isComingFromCpl) {
                        if (!isSurveyAttached || surveyId < 1) {
                            finish();
                        }
                    }
                }
            });

            leader_board.setOnClickListener(view -> openCourseLeaderboard(courseDataClass));

            certificate_lay.setOnClickListener(view -> {
                try {
                    if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                        completionAudioPlayer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if ((coursecomplete_popup != null) && (coursecomplete_popup.isShowing())) {
                    dontShowRatePopup[0] = true;
                    sendCertificatePopup(courseDataClass, true);
                    coursecomplete_popup.dismiss();
                }
            });

            coursecomplete_popup.setOnDismissListener(() -> {
                try {
                    if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                        completionAudioPlayer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if ((coursecomplete_popup != null)) {
                    if (!dontShowRatePopup[0]) {
                        if (rateCourse) {
                            showRateCoursePopup(courseDataClass);
                        } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                            startSurvey();
                        }
                    }
                    coursecomplete_popup.dismiss();
                }
            });

            pop_up_close_icon.setOnClickListener(v -> {
                try {
                    if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                        completionAudioPlayer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if ((coursecomplete_popup != null)) {
                    if (!dontShowRatePopup[0]) {
                        if (rateCourse) {
                            showRateCoursePopup(courseDataClass);
                        } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                            startSurvey();
                        }
                    }
                    coursecomplete_popup.dismiss();
                }
            });

            if (!isValidLbResetPeriod()) {
                layout_ResultScore.setVisibility(View.GONE);
            }


        } else {
            if (!dontShowRatePopup[0]) {
                if (rateCourse) {
                    showRateCoursePopup(courseDataClass);
                } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                    startSurvey();
                }
            }
        }
        try {

            int requestCode = Integer.parseInt("1" + courseDataClass.getCourseId());

            Intent intent = new Intent(RegularModeLearningMapActivity.this, ReminderNotificationManager.class);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(RegularModeLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(RegularModeLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
            }
            boolean isAlready = (pendingIntent != null);
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (isAlready) {
                PendingIntent pendingIntentCancel;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntentCancel = PendingIntent.getBroadcast(RegularModeLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    pendingIntentCancel = PendingIntent.getBroadcast(RegularModeLearningMapActivity.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                }

                assert alarmMgr != null;
                alarmMgr.cancel(pendingIntentCancel);
                pendingIntentCancel.cancel();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void openCourseLeaderboard(CourseDataClass courseDataClass) {

        try {
            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                completionAudioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (OustSdkTools.checkInternetStatus() && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                Intent intent = new Intent(RegularModeLearningMapActivity.this, EventLeaderboardActivity.class);
                intent.putExtra("lpleaderboardId", ("" + courseDataClass.getCourseId()));
                if ((courseDataClass.getBgImg() != null) && (!courseDataClass.getBgImg().isEmpty())) {
                    intent.putExtra("coursebgImg", courseDataClass.getBgImg());
                }
                startActivity(intent);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //========================================================================================================
    boolean ratedcourse = false;
    private PopupWindow rateus_popup;

    private void showRateCoursePopup(final CourseDataClass courseDataClass) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    final int[] ratingNo = new int[1];
                    final Dialog popUpView = new Dialog(RegularModeLearningMapActivity.this, R.style.DialogTheme);
                    popUpView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    popUpView.setContentView(R.layout.new_coursecomplete_popup);
                    Objects.requireNonNull(popUpView.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    popUpView.setCancelable(false);

                    final ImageView popupratecourse_imga = popUpView.findViewById(R.id.popupratecourse_imga);
                    final ImageView popupratecourse_imgb = popUpView.findViewById(R.id.popupratecourse_imgb);
                    final ImageView popupratecourse_imgc = popUpView.findViewById(R.id.popupratecourse_imgc);
                    final ImageView popupratecourse_imgd = popUpView.findViewById(R.id.popupratecourse_imgd);
                    final ImageView popupratecourse_imge = popUpView.findViewById(R.id.popupratecourse_imge);

                    final RelativeLayout rate_main_layout = popUpView.findViewById(R.id.rate_main_layout);
                    final Button email_back_layout = popUpView.findViewById(R.id.email_back_layout);
                    final Button email_submit_layout = popUpView.findViewById(R.id.email_submit_layout);
                    final EditText feedback_edittext = popUpView.findViewById(R.id.feedback_edittext);
                    final RelativeLayout ok_layout = popUpView.findViewById(R.id.ok_layout);
                    final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
                    TextView certificatemsg = popUpView.findViewById(R.id.certificatemsg);
                    setMediumFont(certificatemsg);
                    TextView certificattitle = popUpView.findViewById(R.id.certificattitle);
                    setHeavyFont(certificattitle);

                    Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(courseActionDrawable),
                            Color.parseColor("#D3D3D3")
                    );
                    ok_layout.setBackground(courseActionDrawable);


                    certificatemsg.setText(getResources().getString(R.string.enter_email_receive_certificate));
                    certificattitle.setText(getResources().getString(R.string.your_certificate));
                    if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                        edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                    }

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    feedback_edittext.requestFocus();
                    ok_layout.setOnClickListener(view -> {
                        if (rate_main_layout.getVisibility() == View.VISIBLE) {
                            if (ratingNo[0] > 0) {
                                hideKeyboard(feedback_edittext);
                                popUpView.dismiss();
                                presenter.sendCourseRating(ratingNo[0], courseDataClass, feedback_edittext.getText().toString().trim(), courseColnId);
                            }
                        }
                    });

                    popupratecourse_imga.setOnClickListener(view -> {
                        enableNextButton(ok_layout);
                        ratingNo[0] = 1;
                        popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    });
                    popupratecourse_imgb.setOnClickListener(view -> {
                        enableNextButton(ok_layout);
                        ratingNo[0] = 2;
                        popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    });
                    popupratecourse_imgc.setOnClickListener(view -> {
                        enableNextButton(ok_layout);
                        ratingNo[0] = 3;
                        popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                        popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    });
                    popupratecourse_imgd.setOnClickListener(view -> {
                        enableNextButton(ok_layout);
                        ratingNo[0] = 4;
                        popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_unrated_star));
                    });
                    popupratecourse_imge.setOnClickListener(view -> {
                        enableNextButton(ok_layout);
                        ratingNo[0] = 5;
                        popupratecourse_imga.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgb.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgc.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imgd.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
                        popupratecourse_imge.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_rated_star));
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
            }
        }, 100);

    }

    private void enableNextButton(RelativeLayout ok_layout) {
        ratedcourse = true;
        Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        ok_layout.setBackground(OustSdkTools.drawableColor(courseActionDrawable));
    }

    //=================================================================================================================
    private PopupWindow certificateemail_popup;

    public void sendCertificatePopup(final CourseDataClass courseDataClass,
                                     final boolean isComingFormCourseCompletePopup) {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (certificateemail_popup != null && certificateemail_popup.isShowing()) {
                        return;
                    } else {
                        View popUpView = getLayoutInflater().inflate(R.layout.certificateemail_popup, null);
                        certificateemail_popup = OustSdkTools.createPopUp(popUpView);
                        final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
                        final ImageButton certificatepopup_btnClose = popUpView.findViewById(R.id.certificatepopup_btnClose);
                        final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
                        final TextView certifucate_titletxt = popUpView.findViewById(R.id.certifucate_titletxt);
                        setHeavyFont(certifucate_titletxt);
                        final TextView certificate_txt = popUpView.findViewById(R.id.certificatemsg);
                        setMediumFont(certificate_txt);
                        certifucate_titletxt.setText(getResources().getString(R.string.your_certificate));
                        certificate_txt.setText(getResources().getString(R.string.enter_email_receive_certificate));
                        certificateloader_layout = popUpView.findViewById(R.id.certificateloader_layout);
                        certificate_loader = popUpView.findViewById(R.id.certificate_loader);
                        if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                            edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
                        }
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        edittext_email.requestFocus();

                        btnOK.setOnClickListener(view -> {
                            OustSdkTools.oustTouchEffect(view, 200);
                            if (isValidEmail((edittext_email.getText().toString().trim()))) {
                                showCertificateLoader();
                                sendCertificatetomail(edittext_email.getText().toString().trim(), courseDataClass, isComingFormCourseCompletePopup);
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.enter_valid_mail));
                            }
                        });

                        certificatepopup_btnClose.setOnClickListener(view -> {
                            hideKeyboard(edittext_email);
                            certificateemail_popup.dismiss();
                            if (isComingFormCourseCompletePopup) {
                                if (rateCourse) {
                                    showRateCoursePopup(courseDataClass);
                                } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                                    startSurvey();
                                }
                            }
                        });
                        LinearLayout certificateanim_layout = popUpView.findViewById(R.id.certificateanim_layout);
                        OustSdkTools.popupAppearEffect(certificateanim_layout);
                    }
                }
            }, 600);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    public void sendCertificatetomail(String mail, CourseDataClass courseDataClass,
                                      final boolean isComingFormCourseCompletePopup) {
        try {
            SendCertificateRequest sendCertificateRequest = new SendCertificateRequest();
            sendCertificateRequest.setStudentid(activeUser.getStudentid());
            sendCertificateRequest.setEmailid(mail);
            sendCertificateRequest.setContentId(("" + courseDataClass.getCourseId()));
            sendCertificateRequest.setContentType("COURSE");
            presenter.hitCertificateRequestUrl(sendCertificateRequest, courseDataClass, isComingFormCourseCompletePopup);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotCertificateToMailResponse(String mail, CourseDataClass
            courseDataClass, CommonResponse commonResponse,
                                             boolean isComingFormCourseCompletePopup) {
        try {
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    if ((certificateemail_popup != null) && (certificateemail_popup.isShowing())) {
                        hideCertificateLoader();
                        certificateemail_popup.dismiss();
                        gotCertificatePopup(mail, courseDataClass, commonResponse, isComingFormCourseCompletePopup);
                    }
                } else {
                    OustSdkTools.handlePopup(commonResponse);
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //popup to certificate mail success
    public void gotCertificatePopup(String mail,
                                    final CourseDataClass courseDataClass, CommonResponse commonResponse,
                                    final boolean isComingFormCourseCompletePopup) {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.info_popup, null);
            final PopupWindow info_popup = OustSdkTools.createPopUp(popUpView);
            final Button btnOK = popUpView.findViewById(R.id.info_okbtn);
            final ImageButton infopopup_btnClose = popUpView.findViewById(R.id.infopopup_btnClose);
            final TextView info_titletxt = popUpView.findViewById(R.id.info_titletxt);
            setHeavyFont(info_titletxt);
            final TextView infomsg = popUpView.findViewById(R.id.infomsg);
            setMediumFont(infomsg);
            info_titletxt.setVisibility(View.GONE);
            infomsg.setText(getResources().getString(R.string.certificate_email_to) + " " + mail);

            infopopup_btnClose.setOnClickListener(view -> {
                info_popup.dismiss();
                if (isComingFormCourseCompletePopup) {
                    if (rateCourse) {
                        showRateCoursePopup(courseDataClass);
                    } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                        startSurvey();
                    }
                }
            });
            btnOK.setOnClickListener(v -> {
                info_popup.dismiss();
                if (isComingFormCourseCompletePopup) {
                    if (rateCourse) {
                        showRateCoursePopup(courseDataClass);
                    } else if (isSurveyAttached && surveyId != 0 && assessmentId == 0) {
                        startSurvey();
                    }
                }
            });
            LinearLayout info_mainLayout = popUpView.findViewById(R.id.info_mainLayout);
            OustSdkTools.popupAppearEffect(info_mainLayout);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean isValidEmail(CharSequence target) {
        Pattern EMAIL_ADDRESS
                = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+");
        return EMAIL_ADDRESS.matcher(target).matches();
    }


    private RelativeLayout certificateloader_layout;
    private ProgressBar certificate_loader;

    public void showCertificateLoader() {
        try {
            certificateloader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(RegularModeLearningMapActivity.this, R.anim.rotate_anim);
            certificate_loader.startAnimation(rotateAnim);
            certificateloader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideCertificateLoader() {
        try {
            hideKeyboard(RegularModeLearningMapActivity.this);
            certificateloader_layout.setVisibility(View.GONE);
            certificate_loader.setAnimation(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //show stored popup during game play
    @Override
    public void showstored_Popup() {
        OustAppState.getInstance().setHasPopup(false);
        try {
            OustAppState.getInstance().setPopupStack(null);
//            Stack<Popup> popupStack = OustAppState.getInstance().getPopupStack();
//            if (popupStack != null) {
//                try {
//                    while (!popupStack.isEmpty()) {
////                                OustStaticVariableHandling.getInstance().setOustpopup(popupStack.pop());
////                                Intent intent = new Intent(NewLearningMapActivity.this, PopupActivity.class);
////                                startActivity(intent);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    ================================================================================================
//    setNotificationIconForBulletinBoard

    private FirebaseRefClass firebaseRefClass1;

    @Override
    public void setBulletinQuesFromFirebase(DataSnapshot dataSnapshot) {
        try {
            if (null != dataSnapshot.getValue()) {
                long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                final long updatedTime = (long) dataSnapshot.getValue();
                if ((userCourseData != null) && (updatedTime > userCourseData.getBulletinLastUpdatedTime())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bb_notification_icon));
                    } else {
                        OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bb_notification_icon));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bulletinboard));
                    } else {
                        OustSdkTools.setImage(bulletin_iv, getResources().getString(R.string.bulletinboard));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//================================================================================================


    private boolean isAnyIntroPopupVisible = false;

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
            Log.d(TAG, "openIntroCard: " + courseCardClass.getContent());
            Log.d(TAG, "openIntroCard: " + courseCardClass.getCardType());
            List<String> mediaList = new ArrayList<>();
            List<String> pathList = new ArrayList<>();
            if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                    if (courseCardClass.getCardMedia().get(k).getData() != null) {
                        switch (courseCardClass.getCardMedia().get(k).getMediaType()) {
                            case "IMAGE":
                                pathList.add("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                            case "GIF":
                                pathList.add("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                                mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                break;
                        }
                    }
                }
            }
            if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getRmId() > 0)) {
                switch (courseCardClass.getReadMoreData().getType()) {
                    case "PDF":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                    case "IMAGE":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                }
            }
            checkMediaExist(mediaList, pathList, courseCardClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int mediaSize = 0;

    private void checkMediaExist
            (List<String> mediaList, List<String> pathList,
             final DTOCourseCard courseCardClass) {
        mediaSize = 0;
        courseCardClass1 = courseCardClass;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                if (errorCode == _FAILED || errorCode == _CANCELED) {
                    downloadscreen_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile(courseCardClass);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        for (int i = 0; i < mediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                mediaSize++;
                downLoad(mediaList.get(i), pathList.get(i), courseCardClass);
            }
        }
        if (mediaSize == 0) {
            removeFile(courseCardClass);
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

    public void downLoad(final String fileName1, final String pathName,
                         final DTOCourseCard courseCardClass) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                downloadscreen_layout.setVisibility(View.GONE);
                return;
            }
            Log.d(TAG, "downLoad:filename1: " + fileName1 + " pathName:" + pathName + " ");
            downloadscreen_layout.setVisibility(View.VISIBLE);
          /*  AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());*/
            final File file = new File(this.getFilesDir(), "oustlearn_");
            String destin = this.getFilesDir() + "/";
            //   downloadFiles.startDownLoad(file.toString(),S3_BUCKET_NAME, pathName , false);
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destin, fileName1, true, false);

            /*Log.d(TAG, "downLoad: file:"+file.toString());
            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1, courseCardClass);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            downloadscreen_layout.setVisibility(View.VISIBLE);
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
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private MyFileDownLoadReceiver myFileDownLoadReceiver;

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
            IntentFilter coursedownload_filter = new IntentFilter(CourseDownloadReceiver.PROCESS_RESPONSE);
            coursedownload_filter.addCategory(Intent.CATEGORY_DEFAULT);
            courseDownloadReceiver = new CourseDownloadReceiver();
            this.registerReceiver(courseDownloadReceiver, coursedownload_filter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            IntentFilter filter = new IntentFilter("android.intent.action.ATTACH_DATA");
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new MyDownloadReceiver();
            this.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // courseDownloading();
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile(courseCardClass1);
                            courseDownloading();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            downloadscreen_layout.setVisibility(View.VISIBLE);
                            // mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

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

    public void saveData(File file, String fileName1, DTOCourseCard courseCardClass) {
        Log.d(TAG, "saveData: file:" + file + " fileName1:" + fileName1);

        try {
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
//            if (fileName1.contains("pdf")) {
//                byte[] b = FileUtils.readFileToByteArray(file);
//                encoded = Base64.encodeToString(b, Base64.DEFAULT);
//                Log.e("ReadMore", encoded);
//            }
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            enternalPrivateStorage.saveFile("oustlearn_" + fileName1, encoded);
//            file.delete();
            removeFile(courseCardClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadComplete(DTOCourseCard courseCardClass) {
        try {
            if (courseCardClass != null) {
                isAnyIntroPopupVisible = true;
                downloadscreen_layout.setVisibility(View.GONE);
                Animation event_animzoomin = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.zomin);
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                transaction.replace(R.id.feed_card_layout, fragment, "moduleOverViewFragment");
                fragment.isComingFromFeed(true);
                fragment.setHideTutorial(true);
                fragment.setProgressVal(0);
                fragment.setCardttsEnabled(false);
                // TODO need to handle here
//                fragment.setCourseCardClass(courseCardClass);
                fragment.setLearningModuleInterface(RegularModeLearningMapActivity.this);
                transaction.addToBackStack(null);
                transaction.commit();
                card_layout.setVisibility(View.VISIBLE);
                feedcard_toplayout.setVisibility(View.VISIBLE);
                card_layout.startAnimation(event_animzoomin);
                if (courseCardClass.getCardMedia().get(0).getMediaType().equalsIgnoreCase("VIDEO")) {
                    close_card_left.setVisibility(View.VISIBLE);
                    closecard_layout_left.setVisibility(View.VISIBLE);
                    closecard_layout.setVisibility(View.GONE);
                    close_card_right.setVisibility(View.GONE);
                    closecard_layout_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack();
                                OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                            }
                        }
                    });
                } else {
                    closecard_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                        card_layout.clearAnimation();
//                        card_layout.setVisibility(View.GONE);
//                        feedcard_toplayout.setVisibility(View.GONE);
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack();
//                            presenter.clickOnEnrolleLp(true);
                            }
                        }
                    });
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {
    }

    @Override
    public void gotoPreviousScreen() {
    }

    @Override
    public void changeOrientationPortrait() {
        feedcard_toplayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationLandscape() {
        feedcard_toplayout.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void restartActivity() {
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {
    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse,
                               int oc, boolean status, long time) {
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
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            card_layout.clearAnimation();
            card_layout.setVisibility(View.GONE);
            feedcard_toplayout.setVisibility(View.GONE);
            presenter.clickOnEnrolleLp(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
    public void openReadMoreFragment(DTOReadMore readMoreData,
                                     boolean isRMFavourite1, String courseId1, String
                                             cardBackgroundImage1, DTOCourseCard courseCardClass) {
        try {
            Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
            if (readFragment != null) {
                return;
            }

            if (!OustSdkTools.isReadMoreFragmentVisible) {
                feedcard_toplayout.setVisibility(View.GONE);
                ReadmorePopupFragment readmorePopupFragment = new ReadmorePopupFragment();
                readmorePopupFragment.showLearnCard(RegularModeLearningMapActivity.this, readMoreData, false, null, courseCardClass.getCardColorScheme().getBgImage(), null, this, courseCardClass, null);
                readmorePopupFragment.isComingfromFeedCard(true);
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                transaction.add(R.id.feed_card_layout, readmorePopupFragment, "read_fragment").addToBackStack(null);
                transaction.commit();
            }
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
                this.unregisterReceiver(myFileDownLoadReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (courseDownloadReceiver != null) {
                this.unregisterReceiver(courseDownloadReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            if (receiver != null) {
                this.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (completionAudioPlayer != null && completionAudioPlayer.isPlaying()) {
                completionAudioPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void readMoreDismiss() {
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
            feedcard_toplayout.setVisibility(View.VISIBLE);
        }
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
    public void wrongAnswerAndRestartVideoOverlay() {

    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String
            subjectiveResponse, int oc, boolean status, long time, String childCardId) {

    }

    private void deleteConfirmAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegularModeLearningMapActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.delete_alert_popup, null);
        builder.setView(alertLayout);
        ImageView mImageViewClose;
        Button buttonDelete;

        mImageViewClose = alertLayout.findViewById(R.id.imageViewClose);
        buttonDelete = alertLayout.findViewById(R.id.deleteConfirmButton);
        CustomTextView customTextViewConfirm = alertLayout.findViewById(R.id.confirmTextView);
        String confirmText = getString(R.string.module_delete_confirmation);
        customTextViewConfirm.setText(Html.fromHtml(confirmText));

        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogForDelete.dismiss();
                mAlertDialogForDelete = null;
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogForDelete.dismiss();
                mAlertDialogForDelete = null;

                String s1 = "" + activeUser.getStudentKey() + "" + learningId;
                //int courseUniqNo = Integer.parseInt(s1);
                long courseUniqNo = Long.parseLong(s1);
                Log.d(TAG, "courseUniqNo: str:" + s1 + " --- int:" + courseUniqNo);

                UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                DTOUserCourseData userCourseData1 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                userCourseData1.setDownloadCompletePercentage(0);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData1, courseUniqNo);

                DTOUserCourseData userCourseData2 = userCourseScoreDatabaseHandler.getScoreById(courseUniqNo);
                Log.d(TAG, "DownloadPesrecnt:" + userCourseData2.getDownloadCompletePercentage());
                if (mUserLevelDataList != null && mUserLevelDataList.size() > 0) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        RoomHelper.getAllCourseInLevel(mUserLevelDataList.get(i).getLevelId());

                        presenter.updateLevelDownloadStatus();
                        mImageViewDeleteCourseData.setVisibility(View.INVISIBLE);
                        mUserLevelDataList.get(i).setCompletePercentage(0);
                        download_course_img.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.newlp_notdownload));
                    }

                    onBackPressed();
                }
            }
        });

        mAlertDialogForDelete = builder.create();
        mAlertDialogForDelete.setCancelable(false);
        mAlertDialogForDelete.show();
    }

    public void DeleteLevelByLevel(int levelNo) {
        try {
           /* if (currentLevelNo > 0)
            {
                if (levelNo <= currentLevelNo) {
                    if ((courseDataClass.getCourseLevelClassList().size() > (levelNo - 1)))
                    {
                        if (realmUserCourseData.isCourseComplete())
                        {
                            userCourseScoreDatabaseHandler.setCurrentCompleteLevel(levelNo, realmUserCourseData);
                            userCourseScoreDatabaseHandler.setLastCompleteLevel(levelNo, realmUserCourseData);
                        }
                        view.gotoQuizPage(OustStaticVariableHandling.getInstance().getCurrentLearningPathId(), courseDataClass, levelNo, courseDataClass.getCourseLevelClassList().get(levelNo - 1));
                    } else {
                        view.enableClick();
                    }
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.complete_level_unlock"));
                    view.enableClick();
                }
            } else {
                view.enableClick();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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

    private boolean isValidLbResetPeriod() {
        try {
            long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();
            DTOUserCourseData userCourseData = new UserCourseScoreDatabaseHandler().getScoreById(courseUniqNo);

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

    //mappedAssessmentDetails
    void getLearningPathMappedAssessmentDetails(final int learningPathId) {
        Log.e(TAG, "inside load data from firebase ");
        String message = "/course/course" + learningPathId + "/mappedAssessmentDetails";
        ValueEventListener learningMapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {
                        Log.e(TAG, "got data from firebase ");
                        final Map<String, Object> mappedAssessmentMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (userEventAssessmentData == null) {
                            userEventAssessmentData = new UserEventAssessmentData();
                        }
                        if (mappedAssessmentMap != null) {
                            if (mappedAssessmentMap.get("nQuestionAnswered") != null) {
                                userEventAssessmentData.setnQuestionAnswered((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionAnswered")));
                            }
                            if (mappedAssessmentMap.get("nQuestionCorrect") != null) {
                                userEventAssessmentData.setnQuestionCorrect((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionCorrect")));
                            }
                            if (mappedAssessmentMap.get("nQuestionSkipped") != null) {
                                userEventAssessmentData.setnQuestionSkipped((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionSkipped")));
                            }
                            if (mappedAssessmentMap.get("nQuestionWrong") != null) {
                                userEventAssessmentData.setnQuestionWrong((int) OustSdkTools.convertToLong(mappedAssessmentMap.get("nQuestionWrong")));
                            }
                        }
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
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(false);
    }

    public void checkforSavedAssessment(ActiveUser activeUser) {
        try {
            String node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            int totalQues = 0;
                            AssessmentPlayResponse assessmentPlayResponse = new AssessmentPlayResponse();
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            String currentState = AssessmentState.STARTED;
                            try {
                                if (assessmentprogressMainMap != null && assessmentprogressMainMap.get("assessmentState") != null) {
                                    currentState = ((String) assessmentprogressMainMap.get("assessmentState"));
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
                                //showPopup(getResources().getString(R.string.completed_survey_text"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    // setStartsurvey_btnStatus();
                    //   setButtonListener();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //  setStartsurvey_btnStatus();
                    // setButtonListener();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startSurvey() {
        if (startSurveyImmediately) {
            gotoSurvey(surveyId + "", "Survey");
        }

    }

    public void gotoSurvey(String assessmentId, String surveyTitle) {
        try {
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                Intent intent = new Intent(OustSdkApplication.getContext(), SurveyDetailActivity.class);
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }
                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                    intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
                }
                intent.putExtra("surveyTitle", surveyTitle);
                intent.putExtra("assessmentId", assessmentId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
