package com.oustme.oustsdk.question_module.course;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.TWO_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.activity.courses.learningmapmodule.LearningMapModulePresenter;
import com.oustme.oustsdk.activity.courses.learningmapmodule.LearningMapModuleView;
import com.oustme.oustsdk.card_ui.CardFragment;
import com.oustme.oustsdk.card_ui.ScormCardFragment;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.course_ui.fragment.LearningCardDownloadFragment;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.common.ReadmorePopupFragment;
import com.oustme.oustsdk.fragments.courses.FavMode_ReadMoreFragmnet;
import com.oustme.oustsdk.fragments.courses.LearningCard_ResultFragment;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.fragment.CategoryQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.FIBQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.HotSpotQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.LongQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MCQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MRQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MTFQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MUQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.VideoOverlayFragmentNew;
import com.oustme.oustsdk.request.AddFavCardsRequestData;
import com.oustme.oustsdk.request.AddFavReadMoreRequestData;
import com.oustme.oustsdk.request.SubmitCourseCardRequestData;
import com.oustme.oustsdk.request.SubmitCourseLevelCompleteRequest;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CardReadMore;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.SubmitFavouriteCardRequestService;
import com.oustme.oustsdk.service.SubmitLevelCompleteService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.WebViewClass;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class CourseLearningModuleActivity extends AppCompatActivity implements
        LearningMapModuleView, LearningModuleInterface, CourseContentHandlingInterface, CustomVideoControlListener {

    final String TAG = "CourseLearningModule";

    RelativeLayout course_base_root;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    View timer_layout;
    ProgressBar mandatory_timer_progress;
    TextView mandatory_timer_text;
    FrameLayout course_fragment_container;
    View bottom_bar;
    LinearLayout view_bottom_nav;
    RelativeLayout view_previous;
    ImageView previous_view;
    RelativeLayout view_comment;
    TextView view_count;
    RelativeLayout view_next;
    ImageView next_view;

    private int color;
    private int bgColor;


    //intent data
    Bundle bundle;
    int learningId;
    String courseColId;
    int levelNo;
    int reviewModeQuestionNo;
    boolean isReviewMode;
    boolean isReplayMode;
    boolean isDisableLevelCompletePopUp = false;
    boolean enableLevelCompleteAudio = true;
    private boolean isQuestionImageShown;
    boolean isDownloadVideo;
    boolean isDeadlineCrossed;
    long penaltyPercentage;
    boolean isComingFromCPL;
    boolean isCourseCompleted;
    boolean favCardMode;
    boolean isRegularMode;

    //static variables
    String courseDataString;
    String courseLevelString;
    ActiveUser activeUser;
    boolean isScoreDisplaySecondTime;
    int number_of_answered;

    //common variables
    CourseDataClass courseDataClass;
    CourseLevelClass courseLevelClass;
    boolean isCardTTSEnabled;
    boolean isQuizTTSEnabled;
    String courseId;
    String courseName;
    String backgroundImage;
    boolean reachedEnd;
    int levelCardsSize;
    List<DTOCourseCard> courseCardClassList;
    boolean downloadComplete;
    List<LearningCardResponceData> learningCardResponseDataList;
    List<LearningCardResponceData> tempLearningCardResponseDataList;
    Handler myHandler, nudgeHandler;
    DTOQuestions questions;

    List<FavCardDetails> favCardDetailsList;
    String favSavedCourseName;
    String favSavedCourseId;
    List<CardReadMore> cardReadMoreList = new ArrayList<>();
    List<CardReadMore> unFavCardReadMoreList = new ArrayList<>();
    List<Integer> cardIds = new ArrayList<>();
    List<Integer> unFavouriteCardIds = new ArrayList<>();
    LearningMapModulePresenter mPresenter;
    boolean sentDataToServer = false;
    boolean isOnStopAndResume = false;
    boolean isLastCardCompleted = false;
    private boolean fragmentStarted = false;
    boolean isVideoOverlay = false;
    int number_of_video_overlay_questions = 0;
    boolean isLearningCard = false;
    int videoProgress = 0;
    long timeInterval = 0;
    int questionNo = 0;
    boolean continueFormLastLevel = false;
    int responseTimeInSec = 0;
    boolean isLearnCardComplete = true;
    boolean gotCardDataThroughInterface = false;
    boolean isFavourite = false;
    boolean isFavouritePrevious = false;
    boolean isRMFavourite = false;
    boolean isRMFavouritePrevious = false;
    boolean isCurrentCardQuestion = false;
    boolean isScormCard = false;
    boolean resultPageShown = false;
    boolean isRMPresentInList = false;
    boolean isRMPresentInUnFavList = false;
    boolean isCardPresentInList = false;
    boolean isCardPresentInUnFavList = false;
    boolean recreateLp = false;
    boolean pauseAudioPlayer = false;
    boolean reverseTransUsed = false;
    boolean isVideoCard = false;
    private boolean isMicroCourse = false;
    private boolean isSalesMode = false;
    private boolean isComingFromMultiMediaFragment = false;
    private boolean isComingFromCardFragment = false;
    //enable swipe
    private boolean isAudioPlayTrackComplete = false;
    private boolean disableBackButton = false;
    private String cardDirections;
    //timer
    private AnimatorSet scaleDown;
    private CounterClass timer;
    private boolean timeExceeded;
    private long resumeTimeForOpenDiscussionBoard = 0;
    private long answeredSeconds;

    //card and question audio
    MediaPlayer mediaPlayerForAudio;
    private boolean audioEnable;
    private boolean submitOfflineDataSubmit;
    private boolean isMute = false;
    MenuItem itemAudio;

    private int width_toolbar;
    private int height_toolbar;
    private int height_bottom_bar;
    private boolean updateLevelStatus;

    CoordinatorLayout show_answer_layout;
    NestedScrollView show_answer_popup;

    //Show Solution
    ImageView expand_icon;
    TextView solution_content, text_title;
    RelativeLayout solution_action_button;
    LinearLayout math_show_solution_layout;
    LinearLayout katex_show_solution_layout;
    WebViewClass math_solution_layout_txt;
    KatexView katex_show_solution_txt;
    LinearLayout question_card_submit_layout;
    CircularProgressIndicator submit_progress;
    ImageView status_image;
    TextView status_text;
    TextView status_message;
    long timerMax = 0;
    boolean killActivity = false;
    boolean updateReviewList = false;
    boolean openDiscussionBoard = false;
    private boolean backBtnPressed = false;
    private boolean userClickOnBackPressed = false;
    //End
    BottomSheetBehavior sheetBehavior;

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    LearningCardDownloadFragment fragment = new LearningCardDownloadFragment();
    int savedCardID;
    DTOCourseCard currentCourseCardClass;
    DTOUserCardData dtoUserCardDataVideoOverlay;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onStart() {
        OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_course_learning_module);
        initView();
        initData();

        NestedScrollView mBottomSheetLayout = findViewById(R.id.show_answer_popup);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
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

    protected void initView() {
        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(CourseLearningModuleActivity.this);
            }

            OustSdkTools.setLocale(CourseLearningModuleActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(CourseLearningModuleActivity.this, "Course Learning Module Page");
            getColors();
            course_base_root = findViewById(R.id.course_base_root);
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");

            setSupportActionBar(toolbar);

            timer_layout = findViewById(R.id.timer_layout);
            mandatory_timer_progress = findViewById(R.id.mandatory_timer_progress);
            mandatory_timer_text = findViewById(R.id.mandatory_timer_text);
            course_fragment_container = findViewById(R.id.course_fragment_container);

            bottom_bar = findViewById(R.id.bottom_bar);
            view_bottom_nav = findViewById(R.id.view_bottom_nav);
            view_previous = findViewById(R.id.view_previous);
            previous_view = findViewById(R.id.previous_view);
            view_comment = findViewById(R.id.view_comment);
            view_count = findViewById(R.id.view_count);
            view_next = findViewById(R.id.view_next);
            next_view = findViewById(R.id.next_view);

            view_comment.setVisibility(View.INVISIBLE);

            back_button.setOnClickListener(v -> onBackPressed());

            show_answer_layout = findViewById(R.id.show_answer_layout);
            expand_icon = findViewById(R.id.expand_icon);
            text_title = findViewById(R.id.text_title);
            status_image = findViewById(R.id.status_image);
            status_text = findViewById(R.id.status_text);
            status_message = findViewById(R.id.status_message);
            solution_content = findViewById(R.id.solution_content);
            solution_action_button = findViewById(R.id.solution_action_button);
            katex_show_solution_layout = findViewById(R.id.katex_show_solution_layout);
            katex_show_solution_txt = findViewById(R.id.katex_show_solution_txt);
            math_solution_layout_txt = findViewById(R.id.math_solution_layout_txt);
            math_show_solution_layout = findViewById(R.id.math_show_solution_layout);
            show_answer_popup = findViewById(R.id.show_answer_popup);
            question_card_submit_layout = findViewById(R.id.question_card_submit_layout);
            submit_progress = findViewById(R.id.submit_progress);
            sheetBehavior = BottomSheetBehavior.from(show_answer_popup);

            show_answer_layout.setVisibility(View.GONE);

            try {
                Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
                solution_action_button.setBackground(OustResourceUtils.setDefaultDrawableColor(courseActionDrawable));
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            try {
                height_toolbar = toolbar.getLayoutParams().height;
                width_toolbar = toolbar.getLayoutParams().width;
                height_bottom_bar = bottom_bar.getLayoutParams().height;
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
    protected void onResume() {
        super.onResume();
        try {
            Log.d("TAG", "onResume: ");
            if (!fragmentStarted) {
                fragmentStarted = true;
            }

            if (backBtnPressed) {
                backBtnPressed = false;
            }

            if (pauseAudioPlayer) {
                if (mediaPlayerForAudio != null) {
                    if (!mediaPlayerForAudio.isPlaying()) {
                        pauseAudioPlayer();
                    }
                }
            }

            Log.d(TAG, "onResume:isOnstopAndResume-> " + isOnStopAndResume);
            if (isOnStopAndResume) {
                isOnStopAndResume = false;
                sentDataToServer = false;
                if (learningCardResponseDataList != null && learningCardResponseDataList.size() > 0) {
                    learningCardResponseDataList.remove(learningCardResponseDataList.size() - 1);
                }
                //learningCardResponseDataList = new ArrayList<>();
            }

            if (openDiscussionBoard) {
                if (resumeTimeForOpenDiscussionBoard > 0) {
                    startQuestionTimer(resumeTimeForOpenDiscussionBoard);
                    resumeTimeForOpenDiscussionBoard = 0;
                }
                openDiscussionBoard = false;
            } else if (isComingFromCardFragment) {
                if (resumeTimeForOpenDiscussionBoard > 0) {
                    startQuestionTimer(resumeTimeForOpenDiscussionBoard);
                    resumeTimeForOpenDiscussionBoard = 0;
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
            if (isComingFromCardFragment) {
                pauseAudioPlayer();
                if (null != timer) {
                    timer.cancel();
                    mandatory_timer_progress.setProgress(0);
                    stopAnimation(mandatory_timer_progress);
                    stopAnimation(mandatory_timer_text);
                    String timerZeroText = "00:00";
                    mandatory_timer_text.setText(timerZeroText);
                    timerMax = 0;
                    resumeTimeForOpenDiscussionBoard = answeredSeconds;
                }
            } else if (isComingFromMultiMediaFragment) {
                pauseAudioPlayer();
            } else {
                isOnStopAndResume = true;
                resetAudioPlayer();
                if (!OustStaticVariableHandling.getInstance().isLearningShareClicked()) {
                    if (myHandler != null) {
                        myHandler.removeCallbacksAndMessages(null);
                    }
                    if (!backBtnPressed) {
                        backBtnPressed = true;
                        if (isLearningCard || isLearnCardComplete) {
                            try {
                                CardFragment cardFragment = (CardFragment) (getSupportFragmentManager().findFragmentByTag("cardFragment"));
                                if (cardFragment != null) {
                                    videoProgress = cardFragment.calculateVideoProgress();
                                    timeInterval = cardFragment.getTimeInterval();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                        if (isVideoOverlay) {
                            try {
                                VideoOverlayFragmentNew videoOverlayFragmentNew = (VideoOverlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew"));
                                if (videoOverlayFragmentNew != null) {
                                    videoProgress = videoOverlayFragmentNew.calculateVideoProgress();
                                    timeInterval = videoOverlayFragmentNew.getTimeInterval();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                        if (!OustSdkTools.checkInternetStatus() || isSalesMode) {
                            sendDataToServer();
                        }
                    }
                } else {
                    OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
                }
                if (openDiscussionBoard) {
                    if (null != timer) {
                        timer.cancel();
                        mandatory_timer_progress.setProgress(0);
                        stopAnimation(mandatory_timer_progress);
                        stopAnimation(mandatory_timer_text);
                        String timerZeroText = "00:00";
                        mandatory_timer_text.setText(timerZeroText);
                        timerMax = 0;
                    }
                } else {
                    if (null != timer) {
                        timer.cancel();
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
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        try {
            if (OustStaticVariableHandling.getInstance().isVideoOverlayQuestion()) {
                try {
                    resetAudioPlayer();
                    itemAudio.setVisible(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                VideoOverlayFragmentNew videoOverlayFragmentNew = (VideoOverlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew"));
                if (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew") != null) {
                    if (videoOverlayFragmentNew != null) {
                        try {
                            if (!isReviewMode) {
                                OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", (videoOverlayFragmentNew.getCustomExoPlayerView() != null) ? videoOverlayFragmentNew.getCustomExoPlayerView().getSimpleExoPlayer().getCurrentPosition() : 0);
                                videoProgress = videoOverlayFragmentNew.calculateVideoProgress();
                                timeInterval = videoOverlayFragmentNew.getTimeInterval();
                            } else {
                                videoProgress = 0;
                                timeInterval = 0;
                                OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if (videoOverlayFragmentNew.getChildFragmentManager().findFragmentByTag("VideoOverlayNew") != null && videoOverlayFragmentNew.getChildFragmentManager().findFragmentByTag("VideoOverlayNew").isVisible()) {
                            if (isReviewMode) {
                                videoOverlayFragmentNew.closeChildFragment();
                            } else {
                                if (isNotBackButtonPressed) {
                                    videoOverlayFragmentNew.closeChildFragment();
                                } else {
                                    videoOverlayFragmentNew.checkAndRestartVideo();
                                }
                            }
                            isNotBackButtonPressed = false;
                            return;
                        }
                    }
                    if (!isNotBackButtonPressed) {
                        reachedEnd = false;
                    }
                }
                //return;
            }

            if (OustStaticVariableHandling.getInstance().isVideoFullScreen()) {
                Log.d(TAG, "onBackPressed: videoFullScreen");
                CardFragment cardFragment = (CardFragment) (getSupportFragmentManager().findFragmentByTag("cardFragment"));
                if (cardFragment != null) {
                    cardFragment.setPortraitVideoRatioFullScreen2();
                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                }

                ReadmorePopupFragment readFragment = (ReadmorePopupFragment) getSupportFragmentManager().findFragmentByTag("read_fragment");
                if (readFragment != null) {
                    readFragment.setPotraitVid(true);
                    readFragment.setPotraitVideoRatio();
                    OustStaticVariableHandling.getInstance().setVideoFullScreen(false);
                }
            } else if (!disableBackButton) {
                if (isLearningCard || isLearnCardComplete) {
                    try {
                        if (!isScormCard) {
                            CardFragment cardFragment = (CardFragment) (getSupportFragmentManager().findFragmentByTag("cardFragment"));
                            if (cardFragment != null) {
                                videoProgress = cardFragment.calculateVideoProgress();
                                timeInterval = cardFragment.getTimeInterval();
                            }
                            Log.d(TAG, "onBackPressed: videoprogress:" + videoProgress + " --- timeInterval:" + timeInterval);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (!isNotBackButtonPressed && isLastCardCompleted) {
                        resultPageShown = true;
                    }
                }

                if (OustSdkTools.isReadMoreFragmentVisible) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    readMoreDismiss();
                } else {
                    if (!backBtnPressed) {
                        backBtnPressed = true;
                        if (!OustSdkTools.checkInternetStatus() || isSalesMode) {
                            sentDataToServer = false;
                            if (isSalesMode) {
                                toolbar.setVisibility(View.GONE);
                                question_card_submit_layout.setVisibility(View.VISIBLE);
                                submit_progress.setIndicatorColor(color);
                                submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                                userClickOnBackPressed = true;
                                sendDataToServer();
                            } else {
                                sendDataToServer();
                                setEndAnimation();
                            }
                        } else if (isMicroCourse) {
                            if (isLearningCard && isComingFromCardFragment) {
                                toolbar.setVisibility(View.GONE);
                                question_card_submit_layout.setVisibility(View.VISIBLE);
                                submit_progress.setIndicatorColor(color);
                                submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                                userClickOnBackPressed = true;
                                sentDataToServer = false;
                                sendDataToServer();
                            } else {
                                setEndAnimation();
                            }
                            OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                        } else if (isLearningCard && isComingFromCardFragment) {
                            toolbar.setVisibility(View.GONE);
                            question_card_submit_layout.setVisibility(View.VISIBLE);
                            submit_progress.setIndicatorColor(color);
                            submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                            userClickOnBackPressed = true;
                            sentDataToServer = false;
                            sendDataToServer();
                        } else {
                            setEndAnimation();
                        }
                    }
                    if (isReviewMode) {
                        if (favCardMode) {
                            CourseLearningModuleActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
                            CourseLearningModuleActivity.this.finish();
                        }
                    }
                }
            }
            if (!OustSdkTools.checkInternetStatus()) {
                if (!isRegularMode && !isSalesMode) {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(killActivity, updateReviewList);
                }
            }

            if (isComingFromMultiMediaFragment) {
                isComingFromMultiMediaFragment = false;
            }
            if (isComingFromCardFragment) {
                isComingFromCardFragment = false;
            }
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setEndAnimation() {
        try {
            if (!favCardMode) {
                CourseLearningModuleActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected void initData() {
        try {
            mPresenter = new LearningMapModulePresenter(CourseLearningModuleActivity.this);
            sentDataToServer = false;
            isOnStopAndResume = false;
            isLastCardCompleted = false;
            isNotBackButtonPressed = false;

            backBtnPressed = false;

            bundle = getIntent().getExtras();

            if (bundle != null) {
                learningId = Integer.parseInt(bundle.getString("learningId", "0"));
                courseColId = bundle.getString("courseColnId");
                levelNo = bundle.getInt("levelNo", 0);
                reviewModeQuestionNo = bundle.getInt("reviewModeQuestionNo", 0);
                isReviewMode = bundle.getBoolean("isReviewMode", false);
                isReplayMode = bundle.getBoolean("isReplayMode", false);
                isDisableLevelCompletePopUp = bundle.getBoolean("isDisableLevelCompletePopup", false);
                enableLevelCompleteAudio = bundle.getBoolean("enableLevelCompleteAudio", false);
                isDownloadVideo = bundle.getBoolean("isDownloadVideo", false);
                isDeadlineCrossed = bundle.getBoolean("isDeadlineCrossed", false);
                penaltyPercentage = bundle.getLong("penaltyPercentage", 0);
                isComingFromCPL = bundle.getBoolean("isComingFromCpl", false);
                isCourseCompleted = bundle.getBoolean("COURSE_COMPLETED", true);
                favCardMode = bundle.getBoolean("favCardMode", false);
                isRegularMode = bundle.getBoolean("isRegularMode", false);
                isMicroCourse = bundle.getBoolean("isMicroCourse", false);
                isSalesMode = bundle.getBoolean("isSalesMode", false);
                updateLevelStatus = bundle.getBoolean("updateLevelStatus", false);
            }

            courseDataString = OustStaticVariableHandling.getInstance().getCourseDataStr();
            courseLevelString = OustStaticVariableHandling.getInstance().getCourseLevelStr();
            isScoreDisplaySecondTime = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME);
            activeUser = OustAppState.getInstance().getActiveUser();

            OustStaticVariableHandling.getInstance().setCourseLevelStr(null);
            OustStaticVariableHandling.getInstance().setCourseDataStr(null);

            Gson gson = new Gson();
            courseDataClass = gson.fromJson(courseDataString, CourseDataClass.class);
            courseLevelClass = gson.fromJson(courseLevelString, CourseLevelClass.class);

            if (courseDataClass != null) {
                isCardTTSEnabled = courseDataClass.isCardttsEnabled();
                isQuizTTSEnabled = courseDataClass.isQuesttsEnabled();
                courseId = courseDataClass.getCourseId() + "";
                courseName = courseDataClass.getCourseName();
                backgroundImage = courseDataClass.getLpBgImage();
                isQuestionImageShown = courseDataClass.isShowQuestionSymbolForQuestion();

                if (courseDataClass != null) {
                    if (!courseDataClass.isCourseLandScapeMode() || isVideoOverlay) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }

                if (courseDataClass != null) {
                    if (courseDataClass.isDisableScreenShot()) {
                        keepScreenOnSecure();
                    }
                }

                if (!OustPreferences.getAppInstallVariable("hideCourseBulletin") && !courseDataClass.isHideBulletinBoard()) {
                    view_comment.setVisibility(View.VISIBLE);
                } else {
                    view_comment.setVisibility(View.INVISIBLE);
                }
            }

            if (!((activeUser != null) && (activeUser.getStudentid() != null))) {
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
            }

            if (courseLevelClass != null) {
                screen_name.setText(courseLevelClass.getLevelName());
            }
            learningCardResponseDataList = new ArrayList<>();

            initListener();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected void initListener() {
        try {
            myHandler = new Handler();
            //enableSwipe();
            if (activeUser != null && courseDataClass != null) {
                mPresenter.getFavouriteCardsFromFirebase(activeUser.getStudentKey(), courseDataClass.getCourseId() + "");
            }

            previous_view.setOnClickListener(v -> gotoPreviousScreen());
            view_comment.setOnClickListener(v -> {
                Intent commentIntent = new Intent(CourseLearningModuleActivity.this, BulletinBoardQuestionActivity.class);
                if (courseId != null) {
                    commentIntent.putExtra("courseId", "" + courseId);
                }
                if (courseName != null) {
                    commentIntent.putExtra("courseName", courseName);
                }
                OustStaticVariableHandling.getInstance().setLearningShareClicked(true);
                startActivity(commentIntent);
                resumeTimeForOpenDiscussionBoard = answeredSeconds;
                openDiscussionBoard = true;
            });

            next_view.setOnClickListener(v -> gotoNextScreen());
            setStartingFragment();
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
        }
    }

    private void keepScreenOnSecure() {
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateFavCardsFromFB(DataSnapshot dataSnapshot) {
        favCardDetailsList = new ArrayList<>();
        try {
            if (null != dataSnapshot.getValue()) {
                final Map<String, Object> allFavCardMap = (Map<String, Object>) dataSnapshot.getValue();
                if (allFavCardMap.get("courseName") != null) {
                    favSavedCourseName = ((String) allFavCardMap.get("courseName"));
                }
                if (allFavCardMap.get("courseId") != null) {
                    favSavedCourseId = allFavCardMap.get("courseId") + "";
                }
                if (allFavCardMap.get("cards") != null) {
                    Map<String, Object> cardMap;
                    Object o1 = allFavCardMap.get("cards");
                    if (o1 != null && o1.getClass().equals(HashMap.class)) {
                        cardMap = (Map<String, Object>) o1;
                        Map<String, Object> cardDetailsMap;
                        for (String key : cardMap.keySet()) {
                            Object details = cardMap.get(key);
                            if (details != null) {
                                cardDetailsMap = (Map<String, Object>) details;
                                Log.e(TAG, "" + cardDetailsMap.size());
                                FavCardDetails favCardDetails = new FavCardDetails();
                                if (cardDetailsMap.get("cardId") != null)
                                    favCardDetails.setCardId((String) cardDetailsMap.get("cardId"));
                                if (cardDetailsMap.get("imageUrl") != null)
                                    favCardDetails.setCardDescription((String) cardDetailsMap.get("imageUrl"));
                                if (cardDetailsMap.get("cardDescription") != null)
                                    favCardDetails.setCardDescription((String) cardDetailsMap.get("cardDescription"));
                                if (cardDetailsMap.get("cardTitle") != null)
                                    favCardDetails.setCardTitle((String) cardDetailsMap.get("cardTitle"));
                                if (cardDetailsMap.get("audio") != null)
                                    favCardDetails.setAudio((boolean) cardDetailsMap.get("audio"));
                                if (cardDetailsMap.get("video") != null)
                                    favCardDetails.setVideo((boolean) cardDetailsMap.get("video"));
                                favCardDetailsList.add(favCardDetails);
                            }
                        }
                        Log.e(TAG, "" + favCardDetailsList.size());
                    }
                }
                if (allFavCardMap.get("readMore") != null) {
                    Map<String, Object> readMoreMap;
                    Object o1 = allFavCardMap.get("readMore");
                    if (o1 != null && o1.getClass().equals(HashMap.class)) {
                        readMoreMap = (Map<String, Object>) o1;
                        Map<String, Object> rmDetailMap;
                        for (String key : readMoreMap.keySet()) {
                            Object details = readMoreMap.get(key);
                            if (details != null) {
                                rmDetailMap = (Map<String, Object>) details;
                                FavCardDetails favCardDetails = new FavCardDetails();
                                if (rmDetailMap.get("cardId") != null)
                                    favCardDetails.setCardId((String) rmDetailMap.get("cardId"));
                                if (rmDetailMap.get("levelId") != null)
                                    favCardDetails.setLevelId((String) rmDetailMap.get("levelId"));
                                if (rmDetailMap.get("rmId") != null)
                                    favCardDetails.setRmId(OustSdkTools.convertToLong(rmDetailMap.get("rmId")));
                                favCardDetails.setRMCard(true);
                                if (rmDetailMap.get("rmData") != null)
                                    favCardDetails.setRmData((String) rmDetailMap.get("rmData"));
                                if (rmDetailMap.get("rmGumletVideoUrl") != null)
                                    favCardDetails.setRmGumletVideoUrl((String) rmDetailMap.get("rmGumletVideoUrl"));
                                if (rmDetailMap.get("rmScope") != null)
                                    favCardDetails.setRmScope((String) rmDetailMap.get("rmScope"));
                                if (rmDetailMap.get("rmDisplayText") != null)
                                    favCardDetails.setRmDisplayText((String) rmDetailMap.get("rmDisplayText"));
                                if (rmDetailMap.get("rmType") != null)
                                    favCardDetails.setRmType((String) rmDetailMap.get("rmType"));
                                favCardDetailsList.add(favCardDetails);
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
    public void onError() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showLoader() {

    }

    @Override
    public void updateSubmitCardData(boolean levelDataIsUpDated, boolean apiIsFalling, List<LearningCardResponceData> getUserCardResponse, long mappedSurveyId, long mappedAssessmentId) {
        try {
            Log.e(TAG, "updateSubmitCardData--> levelDataIsUpDated --> " + levelDataIsUpDated + "  apiIsFalling---> " + apiIsFalling);
            if (OustSdkTools.checkInternetStatus()) {
                if (tempLearningCardResponseDataList != null && tempLearningCardResponseDataList.size() > 0 && submitOfflineDataSubmit && !apiIsFalling) {
                    submitOfflineDataSubmit = false;
                    learningCardResponseDataList.clear();
                    learningCardResponseDataList.addAll(tempLearningCardResponseDataList);
                    sendCardSubmitRequest(false);
                } else if (levelDataIsUpDated) {
                    toolbar.setVisibility(View.VISIBLE);
                    question_card_submit_layout.setVisibility(View.GONE);
                    showPopup(getUserCardResponse, mappedSurveyId, mappedAssessmentId, "updateDate");
                } else if (apiIsFalling) {
                    toolbar.setVisibility(View.VISIBLE);
                    question_card_submit_layout.setVisibility(View.GONE);
                    showPopup(getUserCardResponse, mappedSurveyId, mappedAssessmentId, "apiFalling");
                } else if (userClickOnBackPressed) {
                    setEndAnimation();
                } else {
                    if (tempLearningCardResponseDataList != null) {
                        tempLearningCardResponseDataList.clear();
                    }
                    if (learningCardResponseDataList != null) {
                        learningCardResponseDataList.clear();
                    }
                    if (cardDirections != null && !cardDirections.isEmpty()) {
                        if (cardDirections.equalsIgnoreCase("previous")) {
                            gotoPreviousScreen();
                        } else {
                            gotoNextScreen();
                        }
                    } else {
                        gotoNextScreen();
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    question_card_submit_layout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPopup(List<LearningCardResponceData> getUserCardResponse, long mappedSurveyId, long mappedAssessmentId, String status) {
        try {
            Dialog showSubmitRetryDialog = new Dialog(CourseLearningModuleActivity.this, R.style.DialogTheme);
            showSubmitRetryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showSubmitRetryDialog.setContentView(R.layout.common_pop_up);
            showSubmitRetryDialog.setCancelable(false);
            Objects.requireNonNull(showSubmitRetryDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            showSubmitRetryDialog.show();

            ImageView info_image = showSubmitRetryDialog.findViewById(R.id.info_image);
            TextView info_title = showSubmitRetryDialog.findViewById(R.id.info_title);
            TextView info_description = showSubmitRetryDialog.findViewById(R.id.info_description);
            LinearLayout info_cancel = showSubmitRetryDialog.findViewById(R.id.info_no);
            LinearLayout save_userData = showSubmitRetryDialog.findViewById(R.id.info_yes);
            TextView info_save_text = showSubmitRetryDialog.findViewById(R.id.info_yes_text);

            String infoDescription;
            if (status != null && status.equalsIgnoreCase("apiFalling")) {
                infoDescription = getResources().getString(R.string.retry_internet_msg);
                info_save_text.setText(getResources().getString(R.string.retry).toUpperCase());
            } else {
                infoDescription = getResources().getString(R.string.level_edited_user_will_re_attempt);
                info_save_text.setText(getResources().getString(R.string.ok).toUpperCase());
            }
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
                if (status != null && status.equalsIgnoreCase("apiFalling")) {
                    toolbar.setVisibility(View.GONE);
                    question_card_submit_layout.setVisibility(View.VISIBLE);
                    submit_progress.setIndicatorColor(color);
                    submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                    learningCardResponseDataList = new ArrayList<>();
                    learningCardResponseDataList.addAll(getUserCardResponse);
                    sendCardSubmitRequest(false);
                } else {
                    OustAppState.getInstance().getLearningCallBackInterface().upDateLevelTime(true, levelNo);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setStartingFragment() {
        try {
            if (courseLevelClass != null) {
                Log.d(TAG, "setStartingFragment: ");
                toolbar.setVisibility(View.GONE);
                bottom_bar.setVisibility(View.GONE);
                if (courseLevelClass != null) {
                    levelCardsSize = courseLevelClass.getCourseCardClassList().size();
                }
                OustStaticVariableHandling.getInstance().setLearningCardResponceDatas(new LearningCardResponceData[levelCardsSize]);

                int[] answerSeconds = new int[levelCardsSize];
                OustStaticVariableHandling.getInstance().setAnswerSeconds(answerSeconds);
                if (courseDataClass != null) {
                    if (!courseDataClass.isCourseLandScapeMode() || isVideoOverlay) {
                        changeOrientationPortrait();
                    }
                }
                fragment.setFavMode(favCardMode);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setCourseDataClass(courseDataClass);
                fragment.setRegularMode(isRegularMode);
                fragment.setDownloadVideo(isDownloadVideo);
                fragment.isComingFromCPl(isComingFromCPL);
                fragment.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                fragment.setMappedSurveyId(courseDataClass.getMappedSurveyId());
                fragment.setLevelRestStatus(updateLevelStatus);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setBackgroundImage(backgroundImage);
                fragmentTransaction.replace(R.id.course_fragment_container, fragment);
                fragmentTransaction.commit();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {
        try {
            Log.d(TAG, "gotoNextScreen: Next button clicked");
            OustStaticVariableHandling.getInstance().setVideoFullScreen(false);

            next_view.setClickable(false);
            next_view.setEnabled(false);
            solution_action_button.setClickable(false);
            solution_action_button.setEnabled(false);
            mandatory_timer_progress.setProgress(0);
            timerMax = 0;
            try {
                if (isLearningCard && isComingFromCardFragment) {
                    Fragment frag = getSupportFragmentManager().findFragmentByTag("cardFragment");
                    if (frag != null) {
                        CardFragment cardFragment = (CardFragment) (frag);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            cardFragment.closePopup();
                        }
                        if (questionNo > 0) {
                            toolbar.setVisibility(View.GONE);
                            question_card_submit_layout.setVisibility(View.VISIBLE);
                            submit_progress.setIndicatorColor(color);
                            submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                            savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            setLearningCardResponse(questionNo - 1, courseCardClass, "next");
                            return;
                        }
                    }
                } else if (isLearningCard && isScormCard) {
                    Fragment frag = getSupportFragmentManager().findFragmentByTag("scormCardFragment");
                    if (frag != null) {
                        if (questionNo > 0) {
                            isLearnCardComplete = true;
                            toolbar.setVisibility(View.GONE);
                            question_card_submit_layout.setVisibility(View.VISIBLE);
                            submit_progress.setIndicatorColor(color);
                            submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                            savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            setLearningCardResponse(questionNo - 1, courseCardClass, "next");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                toolbar.setVisibility(View.VISIBLE);
                question_card_submit_layout.setVisibility(View.GONE);
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            try {
                if (isScormCard) {
                    isLearnCardComplete(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            new Handler().postDelayed(() -> {
                show_answer_layout.setVisibility(View.GONE);
                ViewGroup.LayoutParams lp = course_fragment_container.getLayoutParams();
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                course_fragment_container.setLayoutParams(lp);
                resetAudioPlayer();
                cancelTimer();
                startTransaction();
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoPreviousScreen() {
        try {
            previous_view.setClickable(false);
            previous_view.setEnabled(false);
            try {
                if (isLearningCard && isComingFromCardFragment) {
                    Fragment frag = getSupportFragmentManager().findFragmentByTag("cardFragment");
                    if (frag != null) {
                        CardFragment cardFragment = (CardFragment) (frag);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            cardFragment.closePopup();
                        }
                        if (questionNo > 0) {
                            toolbar.setVisibility(View.GONE);
                            question_card_submit_layout.setVisibility(View.VISIBLE);
                            submit_progress.setIndicatorColor(color);
                            submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                            savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            setLearningCardResponse(questionNo, courseCardClass, "previous");
                            return;
                        }
                    }
                } else if (isLearningCard && isScormCard) {
                    Fragment frag = getSupportFragmentManager().findFragmentByTag("scormCardFragment");
                    if (frag != null) {
                        if (questionNo > 0) {
                            isLearnCardComplete = true;
                            toolbar.setVisibility(View.GONE);
                            question_card_submit_layout.setVisibility(View.VISIBLE);
                            submit_progress.setIndicatorColor(color);
                            submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                            savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                            setLearningCardResponse(questionNo - 1, courseCardClass, "previous");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                toolbar.setVisibility(View.VISIBLE);
                question_card_submit_layout.setVisibility(View.GONE);
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            new Handler().postDelayed(() -> {
                show_answer_layout.setVisibility(View.GONE);
                ViewGroup.LayoutParams lp = course_fragment_container.getLayoutParams();
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                course_fragment_container.setLayoutParams(lp);
                resetAudioPlayer();
                cancelTimer();
                startReverseTransaction();
            }, 400);
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
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
        if (courseDataClass != null) {
            if (courseDataClass.isCourseLandScapeMode() && !isCurrentCardQuestion) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (currentCourseCardClass != null && !currentCourseCardClass.isPotraitModeVideo()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    private boolean isNotBackButtonPressed = false;

    @Override
    public void endActivity() {
        Log.d(TAG, "endActivity: CLM");
        isNotBackButtonPressed = true;
        onBackPressed();
    }

    @Override
    public void restartActivity() {
        Log.d(TAG, "restartActivity: CLM");
        try {
            if (!backBtnPressed) {
                backBtnPressed = true;
                recreateLp = false;
//                sendDataToServer();
            }

            OustAppState.getInstance().getLearningCallBackInterface().restartCourseLearningModuleActivity(true, levelNo);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean reStartLevel) {
        try {
            if (!downloadComplete) {
                downloadComplete = true;
                questionNo = 0;
                this.courseCardClassList = courseCardClassList;
                Collections.sort(courseCardClassList, DTOCourseCard.newsCardSorter);
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if (!isReviewMode) {
                    if (dtoUserCourseData.getUserLevelDataList() != null) {
                        if (courseDataClass != null) {
                            if (courseDataClass.isStartFromLastLevel()) {
                                getCurrentCardNo(dtoUserCourseData, reStartLevel);
                            } else if (dtoUserCourseData.getLastPlayedLevel() == courseLevelClass.getLevelId()) {
                                questionNo = 0;
                            }
                        }
                    }
                    if (courseDataClass != null) {
                        if (isRegularMode || courseDataClass.isSalesMode()) {
                            if (courseDataClass.isSalesMode()) {
                                continueFormLastLevel = true;
                            }
                            if (reviewModeQuestionNo < courseCardClassList.size()) {
                                questionNo = reviewModeQuestionNo;
                            }
                        }
                    }
                } else {
                    if (reviewModeQuestionNo < courseCardClassList.size()) {
                        questionNo = reviewModeQuestionNo;
                    }
                }
                for (int i = 0; i < (questionNo); i++) {
                    OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = new LearningCardResponceData();
                }
                startTimer();
                gotCardDataThroughInterface = true;
                startTransaction();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            Log.d(TAG, "setAnswerAndOc: ");
            gotCardDataThroughInterface = true;
            isLearnCardComplete = true;
            LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)];
            if (learningCardResponceData == null || learningCardResponceData.getCourseId() == 0) {
                learningCardResponceData = new LearningCardResponceData();
                learningCardResponceData.setCourseId(learningId);
                if ((courseColId != null) && (!courseColId.isEmpty())) {
                    learningCardResponceData.setCourseColnId(courseColId);
                }
                learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                learningCardResponceData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
                learningCardResponceData.setCorrect(status);
                learningCardResponceData.setUserAnswer(userAns);
                learningCardResponceData.setUserSubjectiveAns(subjectiveResponse);
                learningCardResponceData.setResponseTime((responseTimeInSec * 1000));
                learningCardResponceData.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
                if (OustSdkTools.checkInternetStatus()) {
                    learningCardResponceData.setLevelCompleted(false);
                } else {
                    learningCardResponceData.setLevelCompleted(true);
                }
            }
            Log.d(TAG, "cardDataIs0: " + (int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            if (!isReviewMode) {
                learningCardResponceData.setXp(oc);
            }

            learningCardResponceData.setCardCompleted(true);
            learningCardResponceData.setVideoCompletionPercentage("100%");

            if (learningCardResponceData.getListNestedVideoQuestion() == null) {
                learningCardResponceData.setListNestedVideoQuestion(new ArrayList<>());
            }
            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)] = learningCardResponceData;

            LearningCardResponceData learningCardResponceData1 = new LearningCardResponceData();
            if (isVideoOverlay) {
                learningCardResponceData1.setListNestedVideoQuestion(learningCardResponceData.getListNestedVideoQuestion());
                learningCardResponceData1.setVideoCompletionPercentage("100");
                isVideoOverlay = false;
            } else {
                learningCardResponceData1 = new LearningCardResponceData();
                learningCardResponceData1.setListNestedVideoQuestion(new ArrayList<>());
            }

            learningCardResponceData1.setCourseId(learningId);
            if ((courseColId != null) && (!courseColId.isEmpty())) {
                learningCardResponceData1.setCourseColnId(courseColId);
            }
            learningCardResponceData1.setCourseLevelId((int) courseLevelClass.getLevelId());
            learningCardResponceData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            Log.d(TAG, "cardDataIs1: " + (int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
            if (!isReviewMode) {
                learningCardResponceData1.setXp(oc);
            }
            learningCardResponceData1.setCardCompleted(true);
            learningCardResponceData1.setVideoCompletionPercentage("100%");
            learningCardResponceData1.setCorrect(status);
            learningCardResponceData1.setUserAnswer(userAns);
            learningCardResponceData1.setUserSubjectiveAns(subjectiveResponse);
            learningCardResponceData1.setResponseTime((responseTimeInSec * 1000));
            learningCardResponceData1.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
            Date date = new Date();
            long l1 = date.getTime();
            learningCardResponceData1.setCardSubmitDateTime("" + l1);
            if (OustSdkTools.checkInternetStatus()) {
                learningCardResponceData1.setLevelCompleted(false);
            } else {
                learningCardResponceData1.setLevelCompleted(true);
            }

            //set answer & OC
            Log.d(TAG, "setAnswerAndOc: learningCardResponseDataList.add" + learningCardResponceData1.getCourseCardId());
            if (OustSdkTools.checkInternetStatus()) {
                if (learningCardResponseDataList.size() >= 1) {
                    sendCardSubmitRequest(true);
                    submitOfflineDataSubmit = true;
                    tempLearningCardResponseDataList = new ArrayList<>();
                    tempLearningCardResponseDataList.add(learningCardResponceData1);
                } else {
                    learningCardResponseDataList.add(learningCardResponceData1);
                    sendDataToServer();
                }
            } else {
                learningCardResponseDataList.add(learningCardResponceData1);
                gotoNextScreen();
            }
            Log.d(TAG, "cardDataIsFinal: " + learningCardResponseDataList.get(0).getCourseCardId());
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showCourseInfo() {
        //no required for new UI

    }

    @Override
    public void saveVideoMediaList(List<String> videoMediaList) {

    }

    @Override
    public void sendCourseDataToServer() {
        //not required

    }

    @Override
    public void dismissCardInfo() {

    }

    @Override
    public void setFavCardDetails(List<FavCardDetails> favCardDetails) {
        this.favCardDetailsList = favCardDetails;
    }

    @Override
    public void setFavoriteStatus(boolean status) {
        this.isFavourite = status;
    }

    @Override
    public void setRMFavouriteStatus(boolean status) {
        this.isRMFavourite = status;
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
        this.disableBackButton = disableBackButton;
    }

    @Override
    public void closeCourseInfoPopup() {

    }

    @Override
    public void stopTimer() {
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void isLearnCardComplete(boolean isLearnCardComplete) {
        this.isLearnCardComplete = isLearnCardComplete;
        Log.d(TAG, "isLearnCardComplete: " + isLearnCardComplete);
        if (isLearnCardComplete && (courseCardClassList.size() == (questionNo))) {
            if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                isLastCardCompleted = false;
                this.isLearnCardComplete = false;
            } else {
                isLastCardCompleted = true;
                if (courseDataClass != null) {
                    if (courseDataClass.isSalesMode()) {
                        setNextIconDrawableGrey();
                        next_view.setEnabled(false);
                    }
                }
            }
        }

        if (isLearnCardComplete && !isVideoOverlay) {
            if (courseDataClass != null && courseDataClass.isShowNudgeMessage() && !courseDataClass.isAutoPlay()) {
                showNudge();
            }
        }
    }

    @Override
    public void closeChildFragment() {
        Log.d(TAG, "closeChildFragment::");
        try {
            isMute = false;
            resetAudioPlayer();
            itemAudio.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        show_answer_layout.setVisibility(View.GONE);
        next_view.setClickable(false);
        next_view.setEnabled(false);
        try {
            VideoOverlayFragmentNew videoOverlayFragmentNew = (VideoOverlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew"));
            if (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew") != null) {
                if (videoOverlayFragmentNew != null && videoOverlayFragmentNew.getChildFragmentManager().findFragmentByTag("VideoOverlayNew") != null) {
                    videoOverlayFragmentNew.closeChildFragment();
                    try {
                        resetAudioPlayer();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    return;
                }
            }
            handleScreenTouchEvent(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            handleScreenTouchEvent(true);
        }
    }

    @Override
    public void setVideoOverlayAnswerAndOc(String userAnswer, String subjectiveResponse, int oc, boolean status, long time, String childCardId) {

        try {
            Log.d(TAG, "setVideoOverlayAnswerAndOc: " + childCardId);
            gotCardDataThroughInterface = true;
            //isAnswerSubmitted = true;
            isLearnCardComplete = true;

            LearningCardResponceData learningCardResponceData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)];
            if (learningCardResponceData == null || learningCardResponceData.getCourseId() == 0) {
                learningCardResponceData = new LearningCardResponceData();
                learningCardResponceData.setCourseId(learningId);
                if ((courseColId != null) && (!courseColId.isEmpty())) {
                    learningCardResponceData.setCourseColnId(courseColId);
                }
                learningCardResponceData.setCourseLevelId((int) courseLevelClass.getLevelId());
                learningCardResponceData.setCourseCardId((int) dtoUserCardDataVideoOverlay.getCardId());
                Log.d(TAG, "cardDataIs2: " + dtoUserCardDataVideoOverlay.getCardId());
                learningCardResponceData.setCorrect(status);
                learningCardResponceData.setUserAnswer(userAnswer);
                learningCardResponceData.setUserSubjectiveAns(subjectiveResponse);
            }
            if (!isReviewMode) {
                learningCardResponceData.setXp(oc);
            }

            learningCardResponceData.setCardCompleted(true);
            learningCardResponceData.setVideoCompletionPercentage("100%");

            if (learningCardResponceData.getListNestedVideoQuestion() == null) {
                learningCardResponceData.setListNestedVideoQuestion(new ArrayList<>());
            }
            learningCardResponceData.setUserVideoViewInterval(OustPreferences.getTimeForNotification("VideoOverlayCardPauseTime"));

            learningCardResponceData.setResponseTime((responseTimeInSec * 1000));
            Date date = new Date();
            long l1 = date.getTime();
            learningCardResponceData.setCardSubmitDateTime("" + l1);
            learningCardResponceData.setUserVideoViewInterval(OustPreferences.getTimeForNotification("VideoOverlayCardPauseTime"));
            learningCardResponceData.setVideoTotalTimeInterval(OustPreferences.getTimeForNotification("VideoOverlayCardTotalVideoTime"));

            number_of_answered = OustPreferences.getSavedInt("VideoOverlayCardNumberOfAnswered");
            int total_questions = number_of_video_overlay_questions;

            if (number_of_answered > 0 && total_questions > 0) {
                try {
                    float percentage = (number_of_answered / (float) total_questions) * 100;
                    learningCardResponceData.setVideoCompletionPercentage("" + (int) percentage);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } else {
//                learningCardResponceData.setCardCompleted(false);
                learningCardResponceData.setVideoCompletionPercentage("0");
            }

            if (number_of_answered < number_of_video_overlay_questions) {
                isLearnCardComplete = false;
            }
            Log.d(TAG, "setVideoOverlayAnswerAndOc: VideoCompletionPercentage:" + learningCardResponceData.getVideoCompletionPercentage());
            LearningCardResponceData videoOverlayLearningCardResponseData = new LearningCardResponceData();
            videoOverlayLearningCardResponseData.setCourseCardId(Integer.parseInt(childCardId));
            Log.d(TAG, "cardDataIs3: " + childCardId);
            if (!isReviewMode) {
                videoOverlayLearningCardResponseData.setXp(oc);
            }
            videoOverlayLearningCardResponseData.setCardCompleted(true);
            videoOverlayLearningCardResponseData.setVideoCompletionPercentage("0%");

            videoOverlayLearningCardResponseData.setCorrect(status);
            videoOverlayLearningCardResponseData.setUserAnswer(userAnswer);
            videoOverlayLearningCardResponseData.setUserSubjectiveAns(subjectiveResponse);
            videoOverlayLearningCardResponseData.setCourseId(learningId);
            videoOverlayLearningCardResponseData.setUserVideoViewInterval(OustPreferences.getTimeForNotification("VideoOverlayCardPauseTime"));
            videoOverlayLearningCardResponseData.setVideoTotalTimeInterval(OustPreferences.getTimeForNotification("VideoOverlayCardTotalVideoTime"));

            if ((courseColId != null) && (!courseColId.isEmpty())) {
                videoOverlayLearningCardResponseData.setCourseColnId(courseColId);
            }
            videoOverlayLearningCardResponseData.setCourseLevelId((int) courseLevelClass.getLevelId());
            long timeCalculation = (System.currentTimeMillis() - OustPreferences.getTimeForNotification("VideoOverlayCardStartTime"));
            int timeSubmit = (int) (timeCalculation / 1000);

            Log.d(TAG, "setVideoOverlayAnswerAndOc: timeCalc:" + timeCalculation + " --- timeSubmit:" + timeSubmit);
            Log.d(TAG, "setVideoOverlayAnswerAndOc: OC:" + oc);
            videoOverlayLearningCardResponseData.setResponseTime((timeSubmit * 1000));
            videoOverlayLearningCardResponseData.setCardSubmitDateTime("" + l1);

            learningCardResponceData.getListNestedVideoQuestion().add(videoOverlayLearningCardResponseData);
            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[(questionNo - 1)] = learningCardResponceData;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void wrongAnswerAndRestartVideoOverlay() {
        Log.d(TAG, "wrongAnswerAndRestartVideoOverlay: ");
        try {
            isMute = false;
            resetAudioPlayer();
            itemAudio.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            VideoOverlayFragmentNew videoOverlayFragmentNew = (VideoOverlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("VideoOverlayFragmentNew"));
            if (videoOverlayFragmentNew != null && videoOverlayFragmentNew.getChildFragmentManager().findFragmentByTag("VideoOverlayNew") != null && videoOverlayFragmentNew.getChildFragmentManager().findFragmentByTag("VideoOverlayNew").isVisible()) {
                Log.d(TAG, "closeChildFragment: childfragment");
                videoOverlayFragmentNew.checkAndRestartVideo();
            }
            handleScreenTouchEvent(true);
        } catch (Exception e) {
            handleScreenTouchEvent(true);
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

        try {
            prepareExoPlayer(audioFile);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void getCurrentCardNo(DTOUserCourseData userCourseData, boolean reStartLevel) {
        Log.d(TAG, "getCurrentCardNo: ");
        for (int n = 0; n < userCourseData.getUserLevelDataList().size(); n++) {
            if (userCourseData.getUserLevelDataList().get(n) != null) {
                if (userCourseData.getUserLevelDataList().get(n).getLevelId() == courseLevelClass.getLevelId()) {
                    if (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo() > 0 && !reStartLevel) {
                        if (userCourseData.getUserLevelDataList().get(n).getCompletePercentage() == 0) {
                            questionNo = 0;
                        } else {
                            questionNo = (userCourseData.getUserLevelDataList().get(n).getCurrentCardNo());
                            if ((questionNo + 1) >= courseCardClassList.size() && (userCourseData.getUserLevelDataList().get(n).isLastCardComplete())) {
                                questionNo = 0;
                            } else {
                                if (questionNo >= courseCardClassList.size()) {
                                    questionNo = courseCardClassList.size() - 1;
                                }
                                //continueFormLastLevel = !isRegularMode;
                                continueFormLastLevel = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void startTimer() {
        myHandler = new Handler();
        responseTimeInSec = 0;
        myHandler.postDelayed(updateTime, 1000);
    }

    private final Runnable updateTime = new Runnable() {
        public void run() {
            try {
                responseTimeInSec++;
                if (myHandler != null) {
                    myHandler.postDelayed(this, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void startTransaction() {
        try {
            destroyNudgeHandler();
            handleScreenTouchEvent(true);
            isVideoOverlay = false;
            videoProgress = 0;
            timeInterval = 0;
            isComingFromMultiMediaFragment = false;
            isComingFromCardFragment = false;
            isLearningCard = false;
            isScormCard = false;
            disableBackButton = false;
            isAudioPlayTrackComplete = false;
            pauseAudioPlayer = false;
            isLastCardCompleted = false;
            audioEnable = false;
            openDiscussionBoard = false;
            answeredSeconds = 0;
            isMute = false;
            isVideoCard = false;
            invalidateOptionsMenu();
            toolbar.setVisibility(View.VISIBLE);
            bottom_bar.setVisibility(View.VISIBLE);
            OustPreferences.saveAppInstallVariable("isRightAnswerClicked", false);
            OustPreferences.save("MRQuestionAnswers", "");
            OustSdkTools.isReadMoreFragmentVisible = false;
            previous_view.setClickable(true);
            previous_view.setEnabled(true);
            next_view.setClickable(true);
            next_view.setEnabled(true);
            solution_action_button.setClickable(true);
            solution_action_button.setEnabled(true);

            //timer progress default
            mandatory_timer_text.setTextColor(getResources().getColor(R.color.primary_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mandatory_timer_progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_correct)));
            }

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left);
            if (!isReviewMode) {
                if (downloadComplete) {
                    if ((questionNo == 0) || ((courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("QUESTION"))) ||
                            (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("LEARNING")) ||
                            (courseCardClassList.get(questionNo - 1).getCardType().equalsIgnoreCase("SCORM"))) {
                        if (questionNo > 0) {
                            savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);

                            if (courseCardClass == null) {
                                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                                if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                                    for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                        if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                            dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                            dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                            RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                            OustSdkTools.showToast("Card data was removed.Please download again");
                                            finish();
                                            break;
                                        }
                                    }
                                }
                            }

                            if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                                courseCardClass = courseCardClassList.get(questionNo - 1);
                            }
                           /* if (!continueFormLastLevel) {
                                setLearningCardResponse(questionNo - 1, courseCardClass, "next");
                            }*/
                            if (courseCardClass != null) {
                                if (courseCardClass.getReadMoreData() != null) {
                                    checkForFavourite("" + courseCardClass.getCardId(), courseCardClass.getReadMoreData().getRmId());
                                }
                                if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                                    checkForFavourite("" + courseCardClass.getCardId(), 0);
                                }
                            }
                        }
                        continueFormLastLevel = false;
                        if (courseLevelClass != null) {
                            if ((courseLevelClass.getCourseCardClassList().size() > questionNo)) {
                                int savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                                currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                                if (currentCourseCardClass == null) {
                                    DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                                    if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                                        for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                            if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                                dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                                dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                                OustSdkTools.showToast("Card data was removed.Please download again");
                                                finish();
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                                    currentCourseCardClass = courseCardClassList.get(questionNo);
                                }
                                if (currentCourseCardClass != null && RoomHelper.getQuestionById(currentCourseCardClass.getqId()) != null) {
                                    currentCourseCardClass.setQuestionCategory(currentCourseCardClass.getQuestionData().getQuestionCategory());
                                    currentCourseCardClass.setQuestionType(currentCourseCardClass.getQuestionData().getQuestionType());
                                }
                                if (currentCourseCardClass != null && (currentCourseCardClass.getReadMoreData() != null)) {
                                    getReadMoreFavouriteStatus(currentCourseCardClass.getReadMoreData().getRmId());
                                }
                                if (currentCourseCardClass != null && currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                                    getCardFavouriteStatus(("" + currentCourseCardClass.getCardId()));
                                }
                                responseTimeInSec = 0;
                                gotCardDataThroughInterface = false;

                                DTOUserCardData dtoUserCardData = getCurrentDTOUserCardData(savedCardID);
                                dtoUserCardDataVideoOverlay = dtoUserCardData;

                                if (courseCardClassList.size() > questionNo && currentCourseCardClass != null && currentCourseCardClass.getCardType() != null) {
                                    currentCourseCardClass.setSequence(courseCardClassList.get(questionNo).getSequence());
                                    if ((currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING"))) {
                                        responseTimeInSec = 0;
                                        isLearningCard = true;
                                        isCurrentCardQuestion = false;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        if (currentCourseCardClass.getMandatoryViewTime() > 0) {
                                            timer_layout.setVisibility(View.VISIBLE);
                                            mandatory_timer_progress.setProgress(0);
                                            mandatory_timer_progress.setMax(100);
                                            setNextIconDrawableGrey();
                                            next_view.setEnabled(false);
                                            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                            isLearnCardComplete(false);
                                            timer = new CounterClass((currentCourseCardClass.getMandatoryViewTime() * 1000), 1000);
                                            if (!isVideoCard(currentCourseCardClass)) {
                                                timer.start();
                                            }
                                        }
                                        setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                        isComingFromCardFragment = true;
                                        CardFragment cardFragment = new CardFragment();
                                        transaction.replace(R.id.course_fragment_container, cardFragment, "cardFragment");
                                        cardFragment.setLevel(courseLevelClass);
                                        cardFragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                        cardFragment.setCard(currentCourseCardClass);
                                        cardFragment.setCourseData(courseDataClass);
                                        cardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                        cardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                        cardFragment.setReviewMode(isReviewMode);
                                        cardFragment.setQuestionNo(questionNo + 1);
                                        cardFragment.setFavouriteCard(isFavourite);
                                        cardFragment.setFavouriteCardList(favCardDetailsList);
                                        cardFragment.setUserCardData(dtoUserCardData);
                                        cardFragment.setCardBgImage(backgroundImage);
                                        cardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                        cardFragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                                        transaction.commit();
                                    } else if ((currentCourseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                        responseTimeInSec = 0;
                                        isLearningCard = true;
                                        isScormCard = true;

                                        next_view.setClickable(true);
                                        next_view.setEnabled(true);
                                        setNextIconDrawableBlack();

                                        isCurrentCardQuestion = false;
                                        OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                        setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                        // TODO: need to handle SCORM card
                                        ScormCardFragment scormCardFragment = new ScormCardFragment();
                                        transaction.replace(R.id.course_fragment_container, scormCardFragment, "scormCardFragment");
                                        scormCardFragment.setLevel(courseLevelClass);
                                        scormCardFragment.setCard(currentCourseCardClass);
                                        scormCardFragment.setCourseData(courseDataClass);
                                        scormCardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                        scormCardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                        scormCardFragment.setReviewMode(isReviewMode);
                                        scormCardFragment.setFavouriteCard(isFavourite);
                                        scormCardFragment.setFavouriteCardList(favCardDetailsList);
                                        scormCardFragment.setUserCardData(dtoUserCardData);
                                        scormCardFragment.setCardBgImage(backgroundImage);
                                        scormCardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                        scormCardFragment.setActiveUser(activeUser);
                                        transaction.commit();

                                        if (currentCourseCardClass.isIfScormEventBased()) {
                                            bottom_bar.setVisibility(View.GONE);
                                        }
                                    } else {
                                        isLearningCard = false;
                                        responseTimeInSec = 0;
                                        isLearnCardComplete = false;
                                        if (isQuestionImageShown) {
                                            // show quetion symbol
                                            next_view.setEnabled(false);
                                            DTOCourseCard finalCurrentCourseCardClass = currentCourseCardClass;
                                            FragmentTransaction transactionCourse = this.getSupportFragmentManager().beginTransaction().setCustomAnimations(
                                                    R.anim.enter_from_right,
                                                    R.anim.exit_to_left);
                                            CourseQuestionMarkFragment courseQuestionMarkFragment = new CourseQuestionMarkFragment();
                                            courseQuestionMarkFragment.setBackGroundImage(backgroundImage);
                                            courseQuestionMarkFragment.setQuestionTimer(finalCurrentCourseCardClass.getQuestionData().getMaxtime());
                                            transactionCourse.replace(R.id.course_fragment_container, courseQuestionMarkFragment);
                                            transactionCourse.commit();
                                            int finalQuestionNo = questionNo;
                                            new Handler().postDelayed(() -> {
                                                try {
                                                    loadQuestionData(finalCurrentCourseCardClass, finalQuestionNo);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                }
                                            }, 1000);
                                        } else {
                                            loadQuestionData(currentCourseCardClass, questionNo);
                                        }
                                    }
                                    mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                                } else {
//                                LearningCardDownloadFragment fragment = new LearningCardDownloadFragment();
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setBackgroundImage(backgroundImage);
                                    fragment.setCardSize(questionNo + 1);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setRegularMode(isRegularMode);
                                    fragment.setDownloadVideo(isDownloadVideo);
                                    fragment.isComingFromCPl(isComingFromCPL);
                                    fragment.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                                    fragment.setMappedSurveyId(courseDataClass.getMappedSurveyId());
                                    fragment.setLevelRestStatus(updateLevelStatus);
                                    transaction.replace(R.id.course_fragment_container, fragment);
                                    transaction.commit();
                                    questionNo--;
                                }
                            } else if (courseCardClassList.size() == questionNo) {
                                isCurrentCardQuestion = false;
                                mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                                resultPageShown = true;
                                resetAudioPlayer();
                                if (isDisableLevelCompletePopUp) {
                                    isLastCardCompleted = true;
                                    endActivity();
                                } else {
                                    isLastCardCompleted = !isVideoOverlay || number_of_answered >= number_of_video_overlay_questions;
                                    questionNo++;
                                    /*if (!OustSdkTools.checkInternetStatus()) {
                                        sentDataToServer = false;
                                        sendDataToServer();
                                    }*/
                                    toolbar.setVisibility(View.GONE);
                                    bottom_bar.setVisibility(View.GONE);
                                    LearningCard_ResultFragment fragment = new LearningCard_ResultFragment();
                                    int totalXp = (int) courseLevelClass.getTotalXp();
                                    int totalOc = (int) courseLevelClass.getTotalOc();
                                    fragment.setCourseTotalXp(totalXp);
                                    fragment.setCourseTotalOc(totalOc);
                                    resultPageShown = true;
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setLearningCardResponceDatas(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas());
                                    fragment.setBackgroundImage(backgroundImage);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setCourseDataClass(courseDataClass);
                                    fragment.setScoreVisibility(isScoreDisplaySecondTime);
                                    fragment.setCourseComplete(isCourseCompleted);
                                    fragment.setEnableLevelCompleteAudio(enableLevelCompleteAudio);
                                    if (isDeadlineCrossed && penaltyPercentage > 0) {
                                        double oc = courseLevelClass.getTotalOc() * (1 - (penaltyPercentage / 100.0));
                                        fragment.setTotalOc((Double.valueOf(oc)).longValue());
                                    } else {
                                        fragment.setTotalOc(courseLevelClass.getTotalOc());
                                    }
                                    transaction.replace(R.id.course_fragment_container, fragment);
                                    transaction.commit();
                                }
                            } else {
                                vibrateAndShake();
                            }
                        }
                        if (questionNo >= (levelCardsSize - 1)) {
                            reachedEnd = true;
                        }
                        if (!isLastCardCompleted) {
                            questionNo++;
                        }
                    } else {
                        vibrateAndShake();
                    }
                }
            } else {
                isCurrentCardQuestion = false;
                int savedCardID = 0;
                if (questionNo >= 0 && questionNo < courseCardClassList.size()) {
                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                    DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (courseCardClass == null) {
                        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                        if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                    dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                    dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                    OustSdkTools.showToast("Card data was removed.Please download again");
                                    finish();
                                    break;
                                }
                            }
                        }
                    }
                    if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                        courseCardClass = courseCardClassList.get(questionNo);
                    }
                    if ((!continueFormLastLevel) && (courseDataClass.isSalesMode()) && (!favCardMode)) {
                        gotCardDataThroughInterface = false;
                        setLearningCardResponse(questionNo, courseCardClass, "next");
                    }
                }
                if (questionNo > 0 && questionNo < courseCardClassList.size()) {
                    savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                    DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (courseCardClass == null) {
                        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                        if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                    dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                    dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                    OustSdkTools.showToast("Card data was removed.Please download again");
                                    finish();
                                    break;
                                }
                            }
                        }
                    }
                    if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                        courseCardClass = courseCardClassList.get(questionNo - 1);
                    }
                    if ((courseCardClass.getReadMoreData() != null)) {
                        checkForFavourite("" + courseCardClass.getCardId(), courseCardClass.getReadMoreData().getRmId());
                    }
                    if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                        checkForFavourite("" + courseCardClass.getCardId(), 0);
                    }
                }
                DTOUserCardData dtoUserCardData = getCurrentDTOUserCardData(savedCardID);
                if ((courseLevelClass.getCourseCardClassList().size() > questionNo)) {
                    continueFormLastLevel = false;
                    savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                    DTOCourseCard currentCourseCardClass;
                    currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (currentCourseCardClass == null) {
                        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                        if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                    dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                    dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                    OustSdkTools.showToast("Card data was removed.Please download again");
                                    finish();
                                    break;
                                }
                            }

                        }
                    }
                    currentCourseCardClass.setSequence(courseCardClassList.get(questionNo).getSequence());
                    if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                        currentCourseCardClass = courseCardClassList.get(questionNo);
                    }
                    if ((courseCardClassList.size() > questionNo)) {
                        if (currentCourseCardClass != null && currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                            getCardFavouriteStatus(("" + currentCourseCardClass.getCardId()));
                        }
                        if (currentCourseCardClass != null && currentCourseCardClass.getReadMoreData() != null) {
                            getReadMoreFavouriteStatus(currentCourseCardClass.getReadMoreData().getRmId());
                        }
                    }
                    if (currentCourseCardClass != null) {
                        if (favCardMode && courseCardClassList.size() > questionNo && currentCourseCardClass.getReadMoreData() != null &&
                                currentCourseCardClass.isReadMoreCard()) {
                            FavMode_ReadMoreFragmnet fragment = new FavMode_ReadMoreFragmnet();
                            transaction.replace(R.id.course_fragment_container, fragment);
                            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                            fragment.setCardBackgroundImage(backgroundImage);
                            fragment.isFavourite(isRMFavourite);
                            fragment.clickedOnPrevious(false);
                            fragment.setCourseLevelClass(courseLevelClass);
                            fragment.setProgressVal(questionNo);
                            fragment.setCourseCardClass(currentCourseCardClass);
                            fragment.setFavCardDetailsList(favCardDetailsList, "" + currentCourseCardClass.getCardId());
                            transaction.commit();
                            questionNo++;
                        } else if (courseCardClassList.size() > questionNo && currentCourseCardClass.getCardType() != null) {
                            if ((currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING"))) {
                                responseTimeInSec = 0;
                                isCurrentCardQuestion = false;

                                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);

                                if (currentCourseCardClass.getMandatoryViewTime() > 0) {
                                    timer_layout.setVisibility(View.VISIBLE);
                                    mandatory_timer_progress.setProgress(0);
                                    mandatory_timer_progress.setMax(100);
                                    setNextIconDrawableGrey();
                                    next_view.setEnabled(false);
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    isLearnCardComplete(false);
                                    timer = new CounterClass((currentCourseCardClass.getMandatoryViewTime() * 1000), 1000);
                                    timer.start();
                                }

                                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                isLearningCard = true;
                                isComingFromCardFragment = true;

                                CardFragment cardFragment = new CardFragment();
                                transaction.replace(R.id.course_fragment_container, cardFragment, "cardFragment");
                                cardFragment.setLevel(courseLevelClass);
                                cardFragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                cardFragment.setCard(currentCourseCardClass);
                                cardFragment.setCourseData(courseDataClass);
                                cardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                cardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                cardFragment.setReviewMode(isReviewMode);
                                cardFragment.setQuestionNo(questionNo + 1);
                                cardFragment.setFavouriteCard(isFavourite);
                                cardFragment.setFavouriteCardList(favCardDetailsList);
                                cardFragment.setUserCardData(dtoUserCardData);
                                cardFragment.setCardBgImage(backgroundImage);
                                cardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                cardFragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                                transaction.commit();
                            } else if ((currentCourseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                responseTimeInSec = 0;
                                isCurrentCardQuestion = false;
                                isScormCard = true;
                                isLearningCard = true;

                                next_view.setClickable(true);
                                next_view.setEnabled(true);
                                setNextIconDrawableBlack();

                                //handle scorm card
                                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                                ScormCardFragment scormCardFragment = new ScormCardFragment();
                                transaction.replace(R.id.course_fragment_container, scormCardFragment, "scormCardFragment");
                                scormCardFragment.setLevel(courseLevelClass);
                                scormCardFragment.setCard(currentCourseCardClass);
                                scormCardFragment.setCourseData(courseDataClass);
                                scormCardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                scormCardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                scormCardFragment.setReviewMode(isReviewMode);
                                scormCardFragment.setFavouriteCard(isFavourite);
                                scormCardFragment.setFavouriteCardList(favCardDetailsList);
                                scormCardFragment.setUserCardData(dtoUserCardData);
                                scormCardFragment.setCardBgImage(backgroundImage);
                                scormCardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                scormCardFragment.setActiveUser(activeUser);
                                transaction.commit();

                                if (currentCourseCardClass.isIfScormEventBased()) {
                                    bottom_bar.setVisibility(View.GONE);
                                }
                            } else {
                                if ((currentCourseCardClass.getQuestionType() != null) &&
                                        (currentCourseCardClass.getQuestionCategory() != null) &&
                                        (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                                    isCurrentCardQuestion = false;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    isVideoOverlay = true;
                                    try {
                                        number_of_video_overlay_questions = currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                    } catch (Exception e) {
                                        number_of_video_overlay_questions = 1;
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                    //handle Video overlay
                                    VideoOverlayFragmentNew fragment = new VideoOverlayFragmentNew();
                                    transaction.replace(R.id.course_fragment_container, fragment, "VideoOverlayFragmentNew");
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    fragment.setActiveUser(activeUser);
                                    fragment.setCourseCardList(courseCardClassList);
                                    transaction.commit();
                                } else if (((currentCourseCardClass.getQuestionType() != null) &&
                                        (currentCourseCardClass.getQuestionType().equals(QuestionType.FILL)))) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();

                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                                        }
                                    }
                                    FIBQuestionFragment fragment = new FIBQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    transaction.commit();
                                } else if (((currentCourseCardClass.getQuestionCategory() != null) &&
                                        (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();

                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                                        }
                                    }
                                    Log.e(TAG, "startTransaction: 1--> " + (questionNo + 1));
                                    HotSpotQuestionFragment fragment = new HotSpotQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    fragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                                    transaction.commit();
                                } else if (currentCourseCardClass.getQuestionData().isMediaUploadQues()) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    // setQuestionNum(questionNo);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();
                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                                        }
                                    }
                                    isComingFromMultiMediaFragment = true;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                        MUQuestionFragment fragment = new MUQuestionFragment();
                                        transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                        fragment.setCourseQuestion(true);
                                        fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                        fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                        fragment.setMainCourseCardClass(currentCourseCardClass);
                                        fragment.setCourseLevelClass(courseLevelClass);
                                        fragment.setQuestionNo(questionNo + 1);
                                        fragment.setReviewMode(isReviewMode);
                                        fragment.setReplayMode(isReplayMode);
                                        fragment.setCourseData(courseDataClass);
                                        fragment.setBgImage(backgroundImage);
                                        fragment.setFavouriteCardList(favCardDetailsList);
                                        fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                        fragment.setIsRMFavourite(isRMFavourite);
                                        fragment.setCourseCompleted(isCourseCompleted);
                                        fragment.setActiveUser(activeUser);
                                        transaction.commit();
                                    }
                                } else if (((currentCourseCardClass.getQuestionCategory() != null) &&
                                        (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH)))) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();

                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                                        }
                                    }
                                    MTFQuestionFragment fragment = new MTFQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    transaction.commit();
                                } else if (currentCourseCardClass.getQuestionData().getQuestionCategory().equals(QuestionCategory.CATEGORY)) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();
                                        view_next.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                                        }
                                    }

                                    CategoryQuestionFragment fragment = new CategoryQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    transaction.commit();
                                } else if ((currentCourseCardClass.getQuestionCategory() == null ||
                                        (currentCourseCardClass.getQuestionCategory() != null
                                                && !currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)))
                                        && (currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) ||
                                        currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    //setQuestionNum(questionNo);
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();
                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                                        }
                                    }
                                    MCQuestionFragment fragment = new MCQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    transaction.commit();
                                } else if ((currentCourseCardClass.getQuestionCategory() == null ||
                                        !currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                                        && (currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MRQ))) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    // setQuestionNum(questionNo);
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();
                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                                        }
                                    }
                                    MRQuestionFragment fragment = new MRQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    transaction.commit();
                                } else if (currentCourseCardClass.getQuestionCategory() != null &&
                                        currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                                    isCurrentCardQuestion = true;
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                                    // setQuestionNum(questionNo);
                                    if (currentCourseCardClass.getQuestionData() != null) {
                                        setNextIconDrawableGrey();
                                        next_view.setEnabled(false);
                                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                                            resetAudioPlayer();
                                            hideTimer();
                                        } else {
                                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                                        }
                                    }
                                    LongQuestionFragment fragment = new LongQuestionFragment();
                                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                                    fragment.setCourseQuestion(true);
                                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                    fragment.setMainCourseCardClass(currentCourseCardClass);
                                    fragment.setCourseLevelClass(courseLevelClass);
                                    fragment.setQuestionNo(questionNo + 1);
                                    fragment.setCourseData(courseDataClass);
                                    fragment.setBgImage(backgroundImage);
                                    fragment.setFavouriteCardList(favCardDetailsList);
                                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                    fragment.setIsRMFavourite(isRMFavourite);
                                    fragment.setCourseCompleted(isCourseCompleted);
                                    fragment.setReviewMode(isReviewMode);
                                    fragment.setReplayMode(isReplayMode);
                                    transaction.commit();
                                }
                            }
                            if (questionNo >= (levelCardsSize - 1)) {
                                reachedEnd = true;
                            }
                            questionNo++;
                            Log.e(TAG, "startTransaction:-- " + questionNo);
                        }
                    }
                } else {
                    finish();
                }
            }
            getFragmentManager().executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isVideoCard(DTOCourseCard cardData) {
        if (cardData.getCardMedia() != null) {
            for (DTOCourseCardMedia cardMedia : cardData.getCardMedia()) {
                if (cardMedia != null) {
                    String mediaType = cardMedia.getMediaType();
                    if ("VIDEO".equals(mediaType) || "YOUTUBE_VIDEO".equals(mediaType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void loadQuestionData(DTOCourseCard currentCourseCardClass, int questionNo) {
        try {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left);
            if (currentCourseCardClass.getQuestionData().isLearningPlayNew()) {
                if (((currentCourseCardClass.getQuestionCategory() != null) &&
                        (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH)))) {
                    isCurrentCardQuestion = true;
                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                    if (currentCourseCardClass.getQuestionData() != null) {
                        setNextIconDrawableGrey();

                        next_view.setEnabled(false);
                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else {
                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                        }
                    }
                    MTFQuestionFragment fragment = new MTFQuestionFragment();
                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                    fragment.setCourseQuestion(true);
                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                    fragment.setMainCourseCardClass(currentCourseCardClass);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setQuestionNo(questionNo + 1);
                    fragment.setCourseData(courseDataClass);
                    fragment.setBgImage(backgroundImage);
                    fragment.setFavouriteCardList(favCardDetailsList);
                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                    fragment.setIsRMFavourite(isRMFavourite);
                    fragment.setCourseCompleted(isCourseCompleted);
                    fragment.setReviewMode(isReviewMode);
                    fragment.setReplayMode(isReplayMode);
                    transaction.commit();
                } else if (currentCourseCardClass.getQuestionData().getQuestionCategory().equals(QuestionCategory.CATEGORY)) {
                    isCurrentCardQuestion = true;
                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                    if (currentCourseCardClass.getQuestionData() != null) {
                        setNextIconDrawableGrey();
                        view_next.setEnabled(false);
                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else {
                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                        }
                    }

                    CategoryQuestionFragment fragment = new CategoryQuestionFragment();
                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                    fragment.setCourseQuestion(true);
                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                    fragment.setMainCourseCardClass(currentCourseCardClass);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setQuestionNo(questionNo + 1);
                    fragment.setCourseData(courseDataClass);
                    fragment.setBgImage(backgroundImage);
                    fragment.setFavouriteCardList(favCardDetailsList);
                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                    fragment.setReviewMode(isReviewMode);
                    fragment.setIsRMFavourite(isRMFavourite);
                    fragment.setCourseCompleted(isCourseCompleted);
                    fragment.setReplayMode(isReplayMode);
                    transaction.commit();
                } else if (((currentCourseCardClass.getQuestionType() != null) &&
                        (currentCourseCardClass.getQuestionType().equals(QuestionType.FILL)))) {
                    isCurrentCardQuestion = true;
                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                    if (currentCourseCardClass.getQuestionData() != null) {
                        setNextIconDrawableGrey();

                        next_view.setEnabled(false);
                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else {
                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                        }
                    }
                    FIBQuestionFragment fragment = new FIBQuestionFragment();
                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                    fragment.setCourseQuestion(true);
                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                    fragment.setMainCourseCardClass(currentCourseCardClass);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setQuestionNo(questionNo + 1);
                    fragment.setCourseData(courseDataClass);
                    fragment.setBgImage(backgroundImage);
                    fragment.setFavouriteCardList(favCardDetailsList);
                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                    fragment.setIsRMFavourite(isRMFavourite);
                    fragment.setCourseCompleted(isCourseCompleted);
                    fragment.setReplayMode(isReplayMode);
                    transaction.commit();
                } else if (((currentCourseCardClass.getQuestionCategory() != null) &&
                        (currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                    isCurrentCardQuestion = true;
                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                    setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                    if (currentCourseCardClass.getQuestionData() != null) {
                        setNextIconDrawableGrey();

                        next_view.setEnabled(false);
                        if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                            resetAudioPlayer();
                            hideTimer();
                        } else {
                            checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                            startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                        }
                    }
                    HotSpotQuestionFragment fragment = new HotSpotQuestionFragment();
                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                    fragment.setCourseQuestion(true);
                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                    fragment.setReviewMode(isReviewMode);
                    fragment.setReplayMode(isReplayMode);
                    fragment.setMainCourseCardClass(currentCourseCardClass);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setQuestionNo(questionNo + 1);
                    fragment.setCourseData(courseDataClass);
                    fragment.setBgImage(backgroundImage);
                    fragment.setFavouriteCardList(favCardDetailsList);
                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                    fragment.setIsRMFavourite(isRMFavourite);
                    fragment.setCourseCompleted(isCourseCompleted);
                    fragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                    transaction.commit();
                }
            } else if (currentCourseCardClass.getQuestionCategory() != null && currentCourseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY)) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));
                if (currentCourseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    view_next.setEnabled(false);
                    if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                        startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                    }

                    try {
                        if (currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion() != null &&
                                currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size() != 0) {
                            number_of_video_overlay_questions = currentCourseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                        } else {
                            number_of_video_overlay_questions = 1;
                        }

                    } catch (Exception e) {
                        number_of_video_overlay_questions = 1;
                    }
                }
                isVideoOverlay = true;
                VideoOverlayFragmentNew fragment = new VideoOverlayFragmentNew();
                transaction.replace(R.id.course_fragment_container, fragment, "VideoOverlayFragmentNew");
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(currentCourseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setActiveUser(activeUser);
                fragment.setUserCardData(dtoUserCardDataVideoOverlay);
                fragment.setCourseCardList(courseCardClassList);
                fragment.setReviewMode(false);
                fragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                transaction.commit();

            } else if (currentCourseCardClass.getQuestionData().isMediaUploadQues()) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                if (currentCourseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    next_view.setEnabled(false);
                    if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                        startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                    }
                }
                isComingFromMultiMediaFragment = true;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    MUQuestionFragment fragment = new MUQuestionFragment();
                    transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                    fragment.setCourseQuestion(true);
                    fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                    fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                    fragment.setMainCourseCardClass(currentCourseCardClass);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setReviewMode(isReviewMode);
                    fragment.setReplayMode(isReplayMode);
                    fragment.setQuestionNo(questionNo + 1);
                    fragment.setCourseData(courseDataClass);
                    fragment.setBgImage(backgroundImage);
                    fragment.setFavouriteCardList(favCardDetailsList);
                    fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                    fragment.setIsRMFavourite(isRMFavourite);
                    fragment.setCourseCompleted(isCourseCompleted);
                    fragment.setActiveUser(activeUser);
                    transaction.commit();
                }
            } else if ((currentCourseCardClass.getQuestionCategory() == null ||
                    !currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                    && (currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) ||
                    currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                if (currentCourseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    next_view.setEnabled(false);
                    if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                        startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                    }
                }
                MCQuestionFragment fragment = new MCQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(currentCourseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setReplayMode(isReplayMode);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                transaction.commit();
            } else if ((currentCourseCardClass.getQuestionCategory() == null ||
                    !currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                    && (currentCourseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MRQ))) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                if (currentCourseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    next_view.setEnabled(false);
                    if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                        startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());
                    }
                }
                MRQuestionFragment fragment = new MRQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(currentCourseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setReplayMode(isReplayMode);
                transaction.commit();
            } else if (currentCourseCardClass.getQuestionCategory() != null &&
                    currentCourseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (currentCourseCardClass.getSequence() - 1));

                if (currentCourseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    next_view.setEnabled(false);
                    if (currentCourseCardClass.getQuestionData().getGumletVideoUrl() != null && !currentCourseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (currentCourseCardClass.getQuestionData().getqVideoUrl() != null && !currentCourseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(currentCourseCardClass.getQuestionData());
                        startQuestionTimer(currentCourseCardClass.getQuestionData().getMaxtime());

                    }
                }
                LongQuestionFragment fragment = new LongQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(currentCourseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setReplayMode(isReplayMode);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLearningCardResponse(int learningCardNo, DTOCourseCard courseCardClass, String direction) {
        try {
            Log.d(TAG, "setLearningCardResponse: questionNo:" + learningCardNo);
            boolean isLearningCardLocal = !courseCardClass.getCardType().equalsIgnoreCase("QUESTION");

            isLearningCard = isLearningCardLocal;
            if (learningCardNo < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length) {
                LearningCardResponceData learningCardResponseData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningCardNo];
                if (learningCardResponseData == null || learningCardResponseData.getCourseId() == 0) {
                    learningCardResponseData = new LearningCardResponceData();
                    learningCardResponseData.setCourseId(learningId);
                    if ((courseColId != null) && (!courseColId.isEmpty())) {
                        learningCardResponseData.setCourseColnId(courseColId);
                    }
                    learningCardResponseData.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getCardId());
                    learningCardResponseData.setXp(0);
                    learningCardResponseData.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
                    if (isLearningCardLocal && (!isReviewMode)) {
                        if (courseDataClass != null) {
                            if (!courseDataClass.isZeroXpForLCard()) {
                                learningCardResponseData.setXp((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getXp());
                            }
                        }

                        learningCardResponseData.setVideoCompletionPercentage("100%");
                        learningCardResponseData.setCardCompleted(true);
                    }
                    learningCardResponseData.setCorrect(true);
                }
                int respTime = learningCardResponseData.getResponseTime() + (responseTimeInSec * 1000);
                learningCardResponseData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponseData.setCardSubmitDateTime("" + l1);
                if (OustSdkTools.checkInternetStatus()) {
                    learningCardResponseData.setLevelCompleted(false);
                } else {
                    learningCardResponseData.setLevelCompleted(true);
                }

                if (isLearningCard) {
                    if (!isLearnCardComplete) {
                        learningCardResponseData.setVideoCompletionPercentage(videoProgress + "%");
                        learningCardResponseData.setCardViewInterval(timeInterval);
                        learningCardResponseData.setCardCompleted(videoProgress >= 100);
                    } else {
                        learningCardResponseData.setVideoCompletionPercentage("100%");
                        learningCardResponseData.setCardViewInterval(0);
                        learningCardResponseData.setCardCompleted(true);
                    }
                }

                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningCardNo] = learningCardResponseData;

                if (!gotCardDataThroughInterface) {
                    LearningCardResponceData learningCardResponseDataOther = new LearningCardResponceData();
                    learningCardResponseDataOther.setCourseId(learningId);
                    if ((courseColId != null) && (!courseColId.isEmpty())) {
                        learningCardResponseDataOther.setCourseColnId(courseColId);
                    }
                    learningCardResponseDataOther.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseDataOther.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getCardId());
                    learningCardResponseDataOther.setXp(0);
                    learningCardResponseDataOther.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
                    if (isLearningCardLocal && (!isReviewMode)) {
                        if (courseDataClass != null) {
                            if (!courseDataClass.isZeroXpForLCard()) {
                                learningCardResponseDataOther.setXp((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getXp());
                            }
                        }

                        if (!isLearnCardComplete) {
                            learningCardResponseDataOther.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponseDataOther.setCardViewInterval(timeInterval);
                            learningCardResponseDataOther.setCardCompleted(videoProgress >= 100);
                        } else {
                            learningCardResponseDataOther.setVideoCompletionPercentage("100%");
                            learningCardResponseDataOther.setCardViewInterval(0);
                            learningCardResponseDataOther.setCardCompleted(true);
                        }
                    }

                    if (OustSdkTools.checkInternetStatus()) {
                        learningCardResponseDataOther.setLevelCompleted(false);
                    } else {
                        learningCardResponseDataOther.setLevelCompleted(true);
                    }

                    learningCardResponseDataOther.setCorrect(true);
                    learningCardResponseDataOther.setResponseTime((responseTimeInSec * 1000));
                    learningCardResponseDataOther.setCardSubmitDateTime("" + l1);
                    isComingFromCardFragment = false;
                    isScormCard = false;
                    if (OustSdkTools.checkInternetStatus()) {
                        if (learningCardResponseDataList.size() >= 1) {
                            cardDirections = direction;
                            sendCardSubmitRequest(true);
                            submitOfflineDataSubmit = true;
                            tempLearningCardResponseDataList = new ArrayList<>();
                            tempLearningCardResponseDataList.add(learningCardResponseDataOther);
                        } else {
                            cardDirections = direction;
                            sentDataToServer = false;
                            learningCardResponseDataList.add(learningCardResponseDataOther);
                            sendDataToServer();
                        }
                    } else {
                        learningCardResponseDataList.add(learningCardResponseDataOther);
                        if (direction.equalsIgnoreCase("next")) {
                            cardDirections = direction;
                            toolbar.setVisibility(View.VISIBLE);
                            question_card_submit_layout.setVisibility(View.GONE);
                            gotoNextScreen();
                        } else if (direction.equalsIgnoreCase("previous")) {
                            cardDirections = direction;
                            toolbar.setVisibility(View.VISIBLE);
                            question_card_submit_layout.setVisibility(View.GONE);
                            gotoPreviousScreen();
                        }
                    }
                }
                responseTimeInSec = 0;
            }
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForFavourite(String cardId, long rmId) {

        try {
            if (isFavourite) {
                if (!isFavouritePrevious) {
                    addFavCardToFirebase(cardId, 0);
                    setFavoriteStatus(true);
                    isFavourite = false;
                    isFavouritePrevious = false;
                }
            } else if (isFavouritePrevious) {
                removeFavCardFromFirebase(cardId);
                setFavoriteStatus(false);
                isFavourite = false;
                isFavouritePrevious = false;
            }
            if (isRMFavourite) {
                if (!isRMFavouritePrevious) {
                    addFavCardToFirebase(cardId, rmId);
                    setRMFavouriteStatus(true);
                    isRMFavourite = false;
                    isRMFavouritePrevious = false;
                }
            } else if (isRMFavouritePrevious) {
                removeRmCardFromFirebase(rmId);
                setRMFavouriteStatus(false);
                isRMFavourite = false;
                isRMFavouritePrevious = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addFavCardToFirebase(String cardId, long rmId) {

        try {
            for (int i = 0; i < favCardDetailsList.size(); i++) {
                if ((favCardDetailsList.get(i).getCardId() != null) && (favCardDetailsList.get(i).getCardId().equalsIgnoreCase(cardId))) {
                    if ((favCardDetailsList.get(i).getRmId() > 0) && (favCardDetailsList.get(i).getRmId() == rmId) && (favCardDetailsList.get(i).isRMCard())) {
                        addReadMoreToList(rmId, favCardDetailsList.get(i).getCardId());

                        Map<String, Object> favRMCardDetails = new HashMap<>();
                        favRMCardDetails.put("rmId", favCardDetailsList.get(i).getRmId());
                        favRMCardDetails.put("rmCard", favCardDetailsList.get(i).isRMCard());
                        favRMCardDetails.put("rmData", favCardDetailsList.get(i).getRmData());
                        favRMCardDetails.put("rmGumletVideoUrl", favCardDetailsList.get(i).getRmGumletVideoUrl());
                        favRMCardDetails.put("rmDisplayText", favCardDetailsList.get(i).getRmDisplayText());
                        favRMCardDetails.put("rmScope", favCardDetailsList.get(i).getRmScope());
                        favRMCardDetails.put("rmType", favCardDetailsList.get(i).getRmType());
                        favRMCardDetails.put("cardId", favCardDetailsList.get(i).getCardId());
                        favRMCardDetails.put("levelId", "" + courseLevelClass.getLevelId());

                        mPresenter.setFavCardDetails(activeUser.getStudentKey(), courseDataClass.getCourseId(), rmId, favRMCardDetails);

                        if (favSavedCourseName == null || !favSavedCourseName.isEmpty()) {
                            mPresenter.setFavCardName(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                        if ((favSavedCourseId == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardId(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                    } else if (!favCardDetailsList.get(i).isRMCard()) {
                        addCardIdToList(cardId);

                        Map<String, Object> favCardDetails = new HashMap<>();
                        favCardDetails.put("audio", favCardDetailsList.get(i).isAudio());
                        favCardDetails.put("video", favCardDetailsList.get(i).isVideo());
                        favCardDetails.put("cardDescription", favCardDetailsList.get(i).getCardDescription());
                        favCardDetails.put("cardId", favCardDetailsList.get(i).getCardId());
                        favCardDetails.put("cardTitle", favCardDetailsList.get(i).getCardTitle());
                        favCardDetails.put("imageUrl", favCardDetailsList.get(i).getImageUrl());
                        favCardDetails.put("mediaType", favCardDetailsList.get(i).getMediaType());
                        favCardDetails.put("levelId", "" + courseLevelClass.getLevelId());

                        mPresenter.setNonRMFavCardDetails(activeUser.getStudentKey(), courseDataClass.getCourseId(), cardId, favCardDetails);

                        if (favSavedCourseName == null || !favSavedCourseName.isEmpty()) {
                            mPresenter.setFavCardName(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }
                        if ((favSavedCourseId == null) || ((favSavedCourseName != null) && (!favSavedCourseName.isEmpty()))) {
                            mPresenter.setFavCardId(activeUser.getStudentKey(), courseDataClass.getCourseId(), courseDataClass.getCourseName());
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addReadMoreToList(long rmId, String cardId) {
        CardReadMore cardRMData = new CardReadMore();
        cardRMData.setRmId((int) rmId);
        cardRMData.setCardId(Integer.parseInt(cardId));
        cardReadMoreList.add(cardRMData);
    }

    private void addCardIdToList(String cardId) {
        cardIds.add(Integer.parseInt(cardId));
    }

    private void removeFavCardFromFirebase(String cardId) {
        try {
            if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
                Iterator<FavCardDetails> favCardDetailsIterator = favCardDetailsList.iterator();
                while (favCardDetailsIterator.hasNext()) {
                    FavCardDetails favCardDetails = favCardDetailsIterator.next();
                    if ((favCardDetails.getCardId() != null) && (favCardDetails.getCardId().equalsIgnoreCase(cardId))) {
                        addToUnFavouriteCardList(cardId);
                        favCardDetailsIterator.remove();
                        mPresenter.removeFavCardFromFB(activeUser.getStudentKey(), courseDataClass.getCourseId(), cardId);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addToUnFavouriteCardList(String cardId) {
        unFavouriteCardIds.add(Integer.parseInt(cardId));
    }

    private void removeRmCardFromFirebase(long rmId) {
        if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
            Iterator<FavCardDetails> favCardDetailsIterator = favCardDetailsList.iterator();
            while (favCardDetailsIterator.hasNext()) {
                FavCardDetails favCardDetails = favCardDetailsIterator.next();
                if ((favCardDetails.getRmId() > 0) && (favCardDetails.getRmId() == rmId) && (favCardDetails.isRMCard())) {
                    addToUnFavRmList(rmId, favCardDetails.getCardId());
                    favCardDetailsIterator.remove();
                    mPresenter.removeRMCardFromFB(activeUser.getStudentKey(), courseDataClass.getCourseId(), rmId);
                }

            }
        }
    }

    private void addToUnFavRmList(long rmId, String cardId) {
        try {
            CardReadMore rms = new CardReadMore();
            rms.setRmId((int) rmId);
            rms.setCardId(Integer.parseInt(cardId));
            unFavCardReadMoreList.add(rms);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void getReadMoreFavouriteStatus(long rmId) {

        isRMFavourite = false;
        isRMFavouritePrevious = false;
        if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
            for (int i = 0; i < favCardDetailsList.size(); i++) {
                if ((favCardDetailsList.get(i).getRmId() > 0) && ((favCardDetailsList.get(i).getRmId() == rmId)) && (favCardDetailsList.get(i).isRMCard())) {
                    isRMFavourite = true;
                    isRMFavouritePrevious = true;
                }
            }
        }
    }

    private void getCardFavouriteStatus(String cardId) {

        try {
            isFavouritePrevious = false;
            isFavourite = false;
            if ((favCardDetailsList != null) && (favCardDetailsList.size() > 0)) {
                for (int i = 0; i < favCardDetailsList.size(); i++) {
                    Log.e("Favourite card ", favCardDetailsList.get(i).getCardId());
                    if ((favCardDetailsList.get(i).getCardId() != null) && ((favCardDetailsList.get(i).getCardId().equalsIgnoreCase(cardId))) && (!favCardDetailsList.get(i).isRMCard())) {
                        isFavouritePrevious = true;
                        isFavourite = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private DTOUserCardData getCurrentDTOUserCardData(long cardId) {
        Log.d(TAG, "getCurrentRealmUsercardData: " + OustStaticVariableHandling.getInstance().getCourseUniqNo());
        DTOUserCardData dtoUserCardData = null;
        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
        if (dtoUserCourseData != null) {
            for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                if (dtoUserCourseData.getUserLevelDataList().get(n) != null && dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                    for (int m = 0; m < dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().size(); m++) {
                        DTOUserCardData userCardData = dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList().get(m);
                        if (userCardData != null) {
                            Log.d(TAG, "getCurrentRealmUsercardData: cardid:" + cardId + " --- roomCardid:" + userCardData.getCardId());
                            if (userCardData.getCardId() == cardId) {
                                Log.d(TAG, "getCurrentRealmUsercardData: found card id:" + userCardData.getCardId());
                                dtoUserCardData = userCardData;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return dtoUserCardData;
    }

    public void sendDataToServer() {
        try {
            Log.d(TAG, "sendDataToServer question:" + questionNo + " -- sentDataToServer: " + sentDataToServer);
            if (questionNo > 0) {
                if (courseCardClassList.size() > (questionNo - 1)) {
                    Log.d(TAG, "list is greater than question");
                    int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                    DTOCourseCard currentCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                    if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                        currentCourseCardClass = courseCardClassList.get(questionNo - 1);
                    }
                    if ((currentCourseCardClass.getReadMoreData() != null)) {
                        checkForFavourite("" + currentCourseCardClass.getCardId(), currentCourseCardClass.getReadMoreData().getRmId());
                        addCurrentRMId(currentCourseCardClass.getReadMoreData().getRmId(), "" + currentCourseCardClass.getCardId());
                        addCurrentRMIdToUnFavourite(currentCourseCardClass.getReadMoreData().getRmId(), "" + currentCourseCardClass.getCardId());
                        sendFavReadMoreToServer();
                        sendUnFavReadMoreToServer();
                    }
                    if (currentCourseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                        checkForFavourite("" + currentCourseCardClass.getCardId(), 0);
                        addCurrentCardId(("" + currentCourseCardClass.getCardId()));
                        addCurrentCardIdToUnFavourite(("" + currentCourseCardClass.getCardId()));
                        sendFavouriteCardToServer();
                        sendUnFavouriteCardToServer();
                    }
                }
            }
            if (!sentDataToServer) {
                sentDataToServer = true;
                if (questionNo > 0) {
                    for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseId() == 0) {
                                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = null;
                            }
                        }
                    }
                    resultPageShown = true;
                    setLearningCardResponseBack(questionNo - 1);
                    saveLearningData();
                } else {
                    if (isMicroCourse) {
                        Log.e(TAG, "sendDataToServer: inside-->");
                        killActivity = true;
                        updateReviewList = false;
                        startUpdatedLearningMap(true, false);
                    } else {
                        killActivity = false;
                        updateReviewList = false;
                        startUpdatedLearningMap(false, false);
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    question_card_submit_layout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCurrentRMId(long rmId, String cardId) {
        try {
            if (!isRMFavouritePrevious) {
                for (int i = 0; i < cardReadMoreList.size(); i++) {
                    if (cardReadMoreList.get(i).getRmId() == rmId) {
                        isRMPresentInList = true;
                    }
                }
                if (!isRMPresentInList) {
                    CardReadMore readMore = new CardReadMore();
                    readMore.setCardId(Integer.parseInt(cardId));
                    readMore.setRmId((int) rmId);
                    cardReadMoreList.add(readMore);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCurrentRMIdToUnFavourite(long rmId, String cardId) {
        try {
            if ((!isRMFavourite) && (isRMFavouritePrevious)) {
                if ((unFavCardReadMoreList != null) && (unFavCardReadMoreList.size() > 0)) {
                    for (int i = 0; i < unFavCardReadMoreList.size(); i++) {
                        if (unFavCardReadMoreList.get(i).getRmId() == rmId) {
                            isRMPresentInUnFavList = true;
                        }
                    }
                }
                if (!isRMPresentInUnFavList) {
                    CardReadMore rms = new CardReadMore();
                    rms.setCardId(Integer.parseInt(cardId));
                    rms.setRmId((int) rmId);
                    unFavCardReadMoreList.add(rms);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFavReadMoreToServer() {
        try {
            if ((cardReadMoreList != null) && (cardReadMoreList.size() > 0)) {
                AddFavReadMoreRequestData addFavRMRequestData = new AddFavReadMoreRequestData();
                addFavRMRequestData.setRmIds(cardReadMoreList);
                addFavRMRequestData.setStudentid(activeUser.getStudentid());

                Gson gson = new Gson();
                String str = gson.toJson(addFavRMRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFavouriteRMRequests");
                requests.add(str);
                OustPreferences.saveLocalNotificationMsg("savedFavouriteRMRequests", requests);

                //need to handle job scheduler instead of service
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnFavReadMoreToServer() {
        try {
            if ((unFavCardReadMoreList != null) && (unFavCardReadMoreList.size() > 0)) {
                AddFavReadMoreRequestData addUnFavRMRequestData = new AddFavReadMoreRequestData();
                addUnFavRMRequestData.setRmIds(unFavCardReadMoreList);
                addUnFavRMRequestData.setStudentid(activeUser.getStudentid());

                Gson gson = new Gson();
                String str = gson.toJson(addUnFavRMRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedUnfavouriteRMRequests");
                requests.add(str);
                OustPreferences.saveLocalNotificationMsg("savedUnfavouriteRMRequests", requests);

                //need to handle job scheduler instead of service
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void addCurrentCardId(String cardId) {
        try {
            if (!isFavouritePrevious) {
                for (int i = 0; i < cardIds.size(); i++) {
                    if (("" + cardIds.get(i)).equalsIgnoreCase(cardId)) {
                        isCardPresentInList = true;
                    }
                }
                if (!isCardPresentInList) {
                    cardIds.add(Integer.parseInt(cardId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void addCurrentCardIdToUnFavourite(String cardId) {
        try {
            if ((!isFavourite) && (isFavouritePrevious)) {
                if ((unFavouriteCardIds != null) && (unFavouriteCardIds.size() > 0)) {
                    for (int i = 0; i < unFavouriteCardIds.size(); i++) {
                        if (("" + unFavouriteCardIds.get(i)).equalsIgnoreCase(cardId)) {
                            isCardPresentInUnFavList = true;
                        }
                    }
                }
                if (!isCardPresentInUnFavList) {
                    if (unFavouriteCardIds == null) {
                        unFavouriteCardIds = new ArrayList<>();
                    }
                    unFavouriteCardIds.add(Integer.parseInt(cardId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendFavouriteCardToServer() {
        try {
            if ((cardIds != null) && (cardIds.size() > 0)) {
                AddFavCardsRequestData addFavCardsRequestData = new AddFavCardsRequestData();
                addFavCardsRequestData.setCardIds(cardIds);
                addFavCardsRequestData.setStudentid(activeUser.getStudentid());

                Gson gson = new Gson();
                String str = gson.toJson(addFavCardsRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedFavouriteCardsRequests");
                requests.add(str);
                OustPreferences.saveLocalNotificationMsg("savedFavouriteCardsRequests", requests);

                //need to handle job scheduler
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendUnFavouriteCardToServer() {
        try {
            if ((unFavouriteCardIds != null) && (unFavouriteCardIds.size() > 0)) {
                AddFavCardsRequestData addFavCardsRequestData = new AddFavCardsRequestData();
                addFavCardsRequestData.setCardIds(unFavouriteCardIds);
                addFavCardsRequestData.setStudentid(activeUser.getStudentid());

                Gson gson = new Gson();
                String str = gson.toJson(addFavCardsRequestData);
                List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedUnfavouriteCardsRequests");
                requests.add(str);
                OustPreferences.saveLocalNotificationMsg("savedUnfavouriteCardsRequests", requests);

                //need to handle job scheduler
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitFavouriteCardRequestService.class);
                OustSdkApplication.getContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLearningCardResponseBack(int learningCardNo) {
        try {
            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length > learningCardNo) {
                LearningCardResponceData learningCardResponseData = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningCardNo];
                if (learningCardResponseData == null) {
                    learningCardResponseData = new LearningCardResponceData();
                    learningCardResponseData.setCourseId(learningId);
                    if ((courseColId != null) && (!courseColId.isEmpty())) {
                        learningCardResponseData.setCourseColnId(courseColId);
                    }
                    learningCardResponseData.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getCardId());
                    learningCardResponseData.setXp(0);
                    learningCardResponseData.setCorrect(false);
                    learningCardResponseData.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
                    if (OustSdkTools.checkInternetStatus()) {
                        learningCardResponseData.setLevelCompleted(false);
                    } else {
                        learningCardResponseData.setLevelCompleted(true);
                    }

                    if (isLearningCard) {
                        if (!isLearnCardComplete) {
                            learningCardResponseData.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponseData.setCardViewInterval(timeInterval);
                            learningCardResponseData.setCardCompleted(videoProgress >= 100);
                        } else {
                            learningCardResponseData.setVideoCompletionPercentage("100%");
                            learningCardResponseData.setCardViewInterval(0);
                            learningCardResponseData.setCardCompleted(true);
                        }
                    } else {
                        if (isVideoOverlay) {
                            learningCardResponseData.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponseData.setCardViewInterval(timeInterval);
                            learningCardResponseData.setCardCompleted(false);
                        } else {
                            learningCardResponseData.setVideoCompletionPercentage("0%");
                            learningCardResponseData.setCardViewInterval(0);
                            learningCardResponseData.setCardCompleted(false);
                        }
                    }
                }

                int respTime = learningCardResponseData.getResponseTime() + (responseTimeInSec * 1000);
                learningCardResponseData.setResponseTime(respTime);
                Date date = new Date();
                long l1 = date.getTime();
                learningCardResponseData.setCardSubmitDateTime("" + l1);
                if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                    learningCardResponseData.setCardCompleted(false);
                }
                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningCardNo] = learningCardResponseData;

                if (!gotCardDataThroughInterface) {
                    LearningCardResponceData learningCardResponseOther = new LearningCardResponceData();
                    learningCardResponseOther.setCourseId(learningId);
                    if ((courseColId != null) && (!courseColId.isEmpty())) {
                        learningCardResponseOther.setCourseColnId(courseColId);
                    }
                    learningCardResponseOther.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseOther.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getCardId());
                    learningCardResponseOther.setXp(0);
                    learningCardResponseOther.setCorrect(false);
                    learningCardResponseOther.setResponseTime((responseTimeInSec * 1000));
                    learningCardResponseOther.setCardSubmitDateTime("" + l1);
                    learningCardResponseOther.setLevelUpdateTimeStampOfApp(OustSdkTools.newConvertToLong(courseLevelClass.getRefreshTimeStamp()));
                    if (OustSdkTools.checkInternetStatus()) {
                        learningCardResponseOther.setLevelCompleted(false);
                    } else {
                        learningCardResponseOther.setLevelCompleted(true);
                    }

                    if (isLearningCard) {
                        if (!isLearnCardComplete) {
                            learningCardResponseOther.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponseOther.setCardViewInterval(timeInterval);
                            learningCardResponseOther.setCardCompleted(videoProgress >= 100);
                        } else {
                            learningCardResponseOther.setVideoCompletionPercentage("100%");
                            learningCardResponseOther.setCardViewInterval(0);
                            learningCardResponseOther.setCardCompleted(true);
                        }
                    } else {
                        learningCardResponseOther.setVideoCompletionPercentage(videoProgress + "%");
                        learningCardResponseOther.setCardViewInterval(timeInterval);
                        learningCardResponseOther.setCardCompleted(false);
                    }

                    boolean dataFound = false;
                    if (learningCardResponseDataList.size() >= 1) {
                        for (LearningCardResponceData learningCardResponceData : learningCardResponseDataList) {
                            if (learningCardResponceData.getCourseCardId() == learningCardResponseOther.getCourseCardId()) {
                                dataFound = true;
                                break;
                            }
                        }
                    }

                    if (!dataFound) {
                        learningCardResponseDataList.add(learningCardResponseOther);
                    }
                } else {
                    if (isLearningCard || isLearnCardComplete) {
                        LearningCardResponceData learningCardResponseObject = new LearningCardResponceData();
                        learningCardResponseObject.setCourseId(learningId);
                        if ((courseColId != null) && (!courseColId.isEmpty())) {
                            learningCardResponseObject.setCourseColnId(courseColId);
                        }
                        learningCardResponseObject.setCourseLevelId((int) courseLevelClass.getLevelId());
                        learningCardResponseObject.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getCardId());
                        learningCardResponseObject.setXp(0);
                        learningCardResponseObject.setCorrect(false);
                        learningCardResponseObject.setResponseTime((responseTimeInSec * 1000));
                        learningCardResponseObject.setCardSubmitDateTime("" + l1);


                        if (isLearningCard && !isLearnCardComplete) {
                            learningCardResponseObject.setVideoCompletionPercentage(videoProgress + "%");
                            learningCardResponseObject.setCardViewInterval(timeInterval);
                            learningCardResponseObject.setCardCompleted(videoProgress >= 100);
                        } else {
                            learningCardResponseObject.setVideoCompletionPercentage("100%");
                            learningCardResponseObject.setCardViewInterval(0);
                            learningCardResponseObject.setCardCompleted(true);
                        }

                        if (isVideoOverlay && number_of_answered < number_of_video_overlay_questions) {
                            learningCardResponseObject.setCardCompleted(false);
                        }

                        boolean dataFound = false;
                        if (learningCardResponseDataList.size() >= 1) {
                            for (LearningCardResponceData learningCardResponceData : learningCardResponseDataList) {
                                if (learningCardResponceData.getCourseCardId() == learningCardResponseObject.getCourseCardId()) {
                                    dataFound = true;
                                    break;
                                }
                            }
                        }

                        if (!dataFound) {
                            learningCardResponseDataList.add(learningCardResponseObject);
                        }
                    }
                }
                if (number_of_answered > 0 && isVideoOverlay) {
                    LearningCardResponceData learningCardResponseData1 = new LearningCardResponceData();
                    learningCardResponseData1.setListNestedVideoQuestion(learningCardResponseData.getListNestedVideoQuestion());
                    learningCardResponseData1.setCourseId(learningId);
                    if ((courseColId != null) && (!courseColId.isEmpty())) {
                        learningCardResponseData1.setCourseColnId(courseColId);
                    }
                    learningCardResponseData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseData1.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get((questionNo - 1)).getCardId());
                    if (!isReviewMode) {
                        if (courseDataClass != null) {
                            if (!courseDataClass.isZeroXpForLCard()) {
                                learningCardResponseData1.setXp((int) courseLevelClass.getCourseCardClassList().get(learningCardNo).getXp());
                            }
                        }
                    }
                    learningCardResponseData1.setCardCompleted(false);
                    learningCardResponseData1.setCorrect(true);
                    learningCardResponseData1.setResponseTime((responseTimeInSec * 1000));
                    learningCardResponseData1.setCardViewInterval(timeInterval);
                    learningCardResponseData1.setVideoTotalTimeInterval(timeInterval);
                    learningCardResponseData1.setVideoCompletionPercentage(videoProgress + "%");
                    Date date1 = new Date();
                    long l11 = date1.getTime();
                    learningCardResponseData1.setCardSubmitDateTime("" + l11);

                    boolean dataFound = false;
                    if (learningCardResponseDataList.size() >= 1) {
                        for (LearningCardResponceData learningCardResponceData : learningCardResponseDataList) {
                            if (learningCardResponceData.getCourseCardId() == learningCardResponseData1.getCourseCardId()) {
                                dataFound = true;
                                break;
                            }
                        }
                    }

                    if (!dataFound) {
                        learningCardResponseDataList.add(learningCardResponseData1);
                    }
                }
            }
            responseTimeInSec = 0;
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveLearningData() {
        try {
            if ((!isReviewMode) || (courseDataClass.isSalesMode())) {
                boolean isLastLevel = false;
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                if (courseDataClass != null) {
                    if (!courseDataClass.isSalesMode() && reachedEnd && (isLearnCardComplete || isRegularMode)) {
                        try {
                            if (dtoUserCourseData.getCurrentLevel() <= courseLevelClass.getSequence()) {
                                for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] == null) {
                                        try {
                                            LearningCardResponceData learningCardResponseData = new LearningCardResponceData();
                                            learningCardResponseData.setCourseId(learningId);
                                            if ((courseColId != null) && (!courseColId.isEmpty())) {
                                                learningCardResponseData.setCourseColnId(courseColId);
                                            }
                                            learningCardResponseData.setCourseLevelId((int) courseLevelClass.getLevelId());
                                            learningCardResponseData.setCourseCardId((int) courseLevelClass.getCourseCardClassList().get(i).getCardId());
                                            learningCardResponseData.setXp(0);
                                            learningCardResponseData.setResponseTime((responseTimeInSec * 1000));
                                            Date date = new Date();
                                            long l1 = date.getTime();
                                            learningCardResponseData.setCardSubmitDateTime("" + l1);

                                            if (isLearningCard) {
                                                if (!isLearnCardComplete) {
                                                    learningCardResponseData.setVideoCompletionPercentage(videoProgress + "%");
                                                    learningCardResponseData.setCardViewInterval(timeInterval);
                                                    learningCardResponseData.setCardCompleted(videoProgress >= 100);
                                                } else {
                                                    learningCardResponseData.setVideoCompletionPercentage("100%");
                                                    learningCardResponseData.setCardViewInterval(0);
                                                    learningCardResponseData.setCardCompleted(true);
                                                }
                                            } else {
                                                learningCardResponseData.setVideoCompletionPercentage("0%");
                                                learningCardResponseData.setCardViewInterval(0);
                                                learningCardResponseData.setCardCompleted(false);
                                            }

                                            OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] = learningCardResponseData;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            OustSdkTools.sendSentryException(e);
                                        }
                                    }
                                }
                                if ((courseLevelClass.getSequence()) == courseDataClass.getCourseLevelClassList().size()) {
                                    isLastLevel = true;
                                }
                                if (isLastLevel && (!resultPageShown)) {
                                    questionNo--;
                                    mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                                }
                                if (courseDataClass != null) {
                                    if (courseDataClass.isSalesMode() && !isRegularMode) {
                                        resultPageShown = true;
                                    }
                                }
                                if (!isLastLevel || resultPageShown) {
                                    if (isLearnCardComplete) {
                                        if (isRegularMode && courseLevelClass.getSequence() < dtoUserCourseData.getUserLevelDataList().size()) {
                                            dtoUserCourseData.getUserLevelDataList().get((int) courseLevelClass.getSequence()).setLocked(false);
                                        }

                                        sendCourseLevelCompleteDataRequest(courseLevelClass);
                                        if ((courseLevelClass.getSequence()) < courseDataClass.getCourseLevelClassList().size()) {
                                            if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence()).isLevelLock())
                                                dtoUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                        } else {
                                            dtoUserCourseData.setCurrentLevel((courseLevelClass.getSequence() + 1));
                                        }
                                    }
                                }
                            }

                            if ((courseLevelClass.getSequence() + 1) < courseDataClass.getCourseLevelClassList().size()) {
                                if (courseDataClass != null) {
                                    if (!courseDataClass.getCourseLevelClassList().get((int) courseLevelClass.getSequence() + 1).isLevelLock()) {
                                        dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                                    }
                                }
                            } else {
                                dtoUserCourseData.setCurrentCompleteLevel((int) (courseLevelClass.getSequence() + 1));
                            }

                            int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setLevelId(courseLevelClass.getLevelId());
                            long totalXp = 0;

                            for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList() != null) {
                                        if ((dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() < (i + 1))) {
                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().add(new DTOUserCardData());
                                        }

                                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                            if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() > 1) {
                                                for (int m = 0; m < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); m++) {
                                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getCardId() == OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId()) {
                                                        //dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setResponceTime(
                                                                (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setNoofAttempt(
                                                                (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getNoofAttempt() + 1));

                                                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardCompleted(true);
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardViewInterval(0);
                                                        } else {
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                                        }


                                                        if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                                        }
                                                        break;
                                                    }
                                                }
                                            } else {
                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setResponceTime(
                                                        (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setNoofAttempt(
                                                        (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getNoofAttempt() + 1));

                                                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(true);
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(0);
                                                } else {
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                                }

                                                if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (!isLearnCardComplete && dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1).getNoofAttempt() == 1) {
                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().remove(dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() - 1);
                            }

                            try {
                                if (dtoUserCourseData.getUserLevelDataList() != null) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList() != null) {
                                        for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                                            totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                                        }
                                    }

                                    if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }

                            long totalOc = courseLevelClass.getTotalOc();
                            long userOC = totalOc;

                            if (isDeadlineCrossed && (penaltyPercentage > 0 && totalOc > 0)) {
                                double oc = totalOc * (1 - (penaltyPercentage / 100.0));
                                userOC = (Double.valueOf(oc)).longValue();
                            }
                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setTotalOc(userOC);

                            totalOc = 0;
                            if (dtoUserCourseData.getUserLevelDataList() != null) {
                                for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                        totalOc += dtoUserCourseData.getUserLevelDataList().get(n).getTotalOc();
                                    }
                                }
                            }

                            if (isLearnCardComplete) {
                                dtoUserCourseData.setTotalOc(totalOc);
                            }

                            if (dtoUserCourseData.getUserLevelDataList() != null) {
                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setIslastCardComplete(isLearnCardComplete);
                            }

                            float totalAttemptedCard = 0;
                            if (dtoUserCourseData.getUserLevelDataList() != null) {
                                for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                        if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                            for (DTOUserCardData dtoUserCardData : dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList()) {
                                                if (dtoUserCardData.isCardCompleted()) {
                                                    totalAttemptedCard++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            float totalCards = dtoUserCourseData.getTotalCards();
                            float percentage = (totalAttemptedCard / totalCards);
                            if (!isLastLevel || resultPageShown) {
                                float percentageValue = percentage * 100;
                                if (isLastLevel) {
                                    percentageValue = 100;
                                }

                                if (dtoUserCourseData.getPresentageComplete() < 100) {
                                    if (courseDataClass.isSurveyMandatory() && percentageValue >= 100 && courseDataClass.getMappedSurveyId() > 0) {
                                        Log.d(TAG, "saveLearningData: Survey mandatory");
                                        if (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                                            dtoUserCourseData.setPresentageComplete(90);
                                        } else {
                                            dtoUserCourseData.setPresentageComplete(95);
                                        }
                                    } else if (percentageValue >= 100 && (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted())) {
                                        dtoUserCourseData.setPresentageComplete(95);
                                    } else {
                                        dtoUserCourseData.setPresentageComplete((long) percentageValue);
                                    }
                                }

                                if ((((long) percentageValue) == 100) && (dtoUserCourseData.getPresentageComplete() < 100)) {
                                    dtoUserCourseData.setCourseCompleted(true);
                                }
                            }
                            if (isLastLevel && (!resultPageShown)) {
                                if (dtoUserCourseData.getPresentageComplete() < 100) {
                                    sendCardSubmitRequest(false);
                                    if (!isRegularMode || isLastCardCompleted) {
                                        if (courseDataClass != null) {
                                            if (courseDataClass.isSurveyMandatory()) {
                                                if (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                                                    dtoUserCourseData.setPresentageComplete(90);
                                                } else {
                                                    dtoUserCourseData.setPresentageComplete(95);
                                                }

                                            } else if (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                                                dtoUserCourseData.setPresentageComplete(95);
                                            } else {
                                                if (!isLastCardCompleted) {
                                                    dtoUserCourseData.setPresentageComplete(99);
                                                    if (courseDataClass.isSalesMode()) {
                                                        dtoUserCourseData.setPresentageComplete(100);
                                                    }
                                                } else {
                                                    dtoUserCourseData.setPresentageComplete(100);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    sendCardSubmitRequest(false);
                                }
                            } else {
                                sendCardSubmitRequest(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        } finally {
                            RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                        }
                    } else {
                        try {
                            sendCardSubmitRequest(false);
                            if (!isVideoOverlay) {
                                int currentLevelNo = (int) (courseLevelClass.getSequence() - 1);
                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setLevelId(courseLevelClass.getLevelId());
                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setIslastCardComplete(false);
                                long totalXp = 0;

                                for (int i = 0; i < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas().length; i++) {
                                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                        if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList() != null) {
                                            if ((dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() < (i + 1))) {
                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().add(new DTOUserCardData());
                                            }
                                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i] != null) {
                                                if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size() > 1) {
                                                    for (int m = 0; m < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); m++) {
                                                        if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getCardId() == OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId()) {
                                                            //dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setResponceTime(
                                                                    (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                                            dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setNoofAttempt(
                                                                    (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getNoofAttempt() + 1));

                                                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardCompleted(true);
                                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardViewInterval(0);
                                                            } else {
                                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                                            }
                                                            Log.d(TAG, "saveLearningData: cardId:" + dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getCardId() + " --- isCompleted:" + dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).isCardCompleted());

                                                            if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                                                dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(m).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                                            }
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardId(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCourseCardId());
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setResponceTime(
                                                            (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getResponceTime() + OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getResponseTime()));
                                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setNoofAttempt(
                                                            (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getNoofAttempt() + 1));

                                                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].isCardCompleted()) {
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardCompleted(true);
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(0);
                                                    } else {
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setCardViewInterval(OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getCardViewInterval());
                                                    }
                                                    Log.d(TAG, "saveLearningData: cardId:" + dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getCardId() + " --- isCompleted:" + dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).isCardCompleted());

                                                    if (dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).getOc() < OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()) {
                                                        dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(i).setOc((OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[i].getXp()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int j = 0; j < dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().size(); j++) {
                                    totalXp += dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getUserCardDataList().get(j).getOc();
                                }
                                if (totalXp > dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).getXp()) {
                                    dtoUserCourseData.getUserLevelDataList().get(currentLevelNo).setXp(totalXp);
                                }
                                float totalAttemptedCard = 0;
                                if (dtoUserCourseData.getUserLevelDataList() != null) {
                                    for (int n = 0; n < dtoUserCourseData.getUserLevelDataList().size(); n++) {
                                        if (dtoUserCourseData.getUserLevelDataList().get(n) != null) {
                                            if (dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList() != null) {
                                                for (DTOUserCardData dtoUserCardData : dtoUserCourseData.getUserLevelDataList().get(n).getUserCardDataList()) {
                                                    if (dtoUserCardData.isCardCompleted()) {
                                                        totalAttemptedCard++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                float totalCards = dtoUserCourseData.getTotalCards();
                                float percentage = (totalAttemptedCard / totalCards);
                                float percentageValue = percentage * 100;

                                if (dtoUserCourseData.getPresentageComplete() < 100) {
                                    if (courseDataClass.isSurveyMandatory() && percentageValue >= 100) {
                                        if (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted()) {
                                            dtoUserCourseData.setPresentageComplete(90);
                                        } else {
                                            dtoUserCourseData.setPresentageComplete(95);
                                        }
                                    } else if (percentageValue >= 100 && (courseDataClass.getMappedAssessmentId() > 0 && !dtoUserCourseData.isMappedAssessmentCompleted())) {
                                        dtoUserCourseData.setPresentageComplete(95);
                                    } else {
                                        dtoUserCourseData.setPresentageComplete((long) percentageValue);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        } finally {
                            RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                        }
                    }
                }
                if (isMicroCourse && (dtoUserCourseData.getPresentageComplete() == 100 || dtoUserCourseData.getPresentageComplete() == 95 || (dtoUserCourseData.getPresentageComplete() == 90 && courseDataClass != null && courseDataClass.isSurveyMandatory()))) {
                    Log.d(TAG, "saveLearningData: micro course last card Completed:" + isLastCardCompleted);
                    killActivity = !isLastCardCompleted;
                    updateReviewList = false;
                    userClickOnBackPressed = true;
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(false, false);
                } else {
                    if (recreateLp) {
                        killActivity = false;
                        updateReviewList = false;
                        startUpdatedLearningMap(false, false);
                    }
                }
            } else {
                if (!favCardMode) {
                    sendCardSubmitRequest(false);
                    killActivity = false;
                    updateReviewList = true;
                    startUpdatedLearningMap(false, true);
                }
            }
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCourseLevelCompleteDataRequest(CourseLevelClass courseLevelClass) {
        try {
            Log.d(TAG, "sendCourseLevelCompleteDataRequest");
            SubmitCourseLevelCompleteRequest submitCourseLevelCompleteRequest = new SubmitCourseLevelCompleteRequest();
            submitCourseLevelCompleteRequest.setCourseId("" + OustStaticVariableHandling.getInstance().getCurrentLearningPathId());
            submitCourseLevelCompleteRequest.setUserId(activeUser.getStudentid());
            if ((courseColId != null) && (!courseColId.isEmpty())) {
                submitCourseLevelCompleteRequest.setCourseColnId(courseColId);
            }
            submitCourseLevelCompleteRequest.setLevelId("" + courseLevelClass.getLevelId());
            Gson gson = new Gson();
            String str = gson.toJson(submitCourseLevelCompleteRequest);
            List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedCourseLevelCompleteRequests");
            requests.add(str);

            OustPreferences.saveLocalNotificationMsg("savedCourseLevelCompleteRequests", requests);

            //need to handle job sechduler
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SubmitLevelCompleteService.class);
            OustSdkApplication.getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sendCardSubmitRequest(boolean offlineData) {
        try {
            boolean removeLastCardData = false;
            if (!isLearnCardComplete) {
                removeLastCardData = true;
            }

            Log.d(TAG, "sendCardSubmitRequest: remove:" + false + " ----- isLearningCard:" + isLearningCard);

            SubmitCourseCardRequestData submitCourseCardRequestData = new SubmitCourseCardRequestData();
            submitCourseCardRequestData.setStudentid(activeUser.getStudentid());

            try {
                submitCourseCardRequestData.setDevicePlatformName("ANDROID");
                submitCourseCardRequestData.setOfflineRequest(offlineData);
                long cplId = OustPreferences.getTimeForNotification("cplId_course");
                if (cplId == 0) {
                    if (isComingFromCPL) {
                        cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                    }
                }
                Log.d(TAG, "sendCardSubmitRequest: course CP, id:" + cplId);
                if (cplId > 0) {
                    submitCourseCardRequestData.setCplId("" + cplId);
                }

                if (courseDataClass != null && courseDataClass.getMappedSurveyId() > 0 && courseDataClass.isSurveyMandatory()) {
                    if (courseDataClass.getMappedSurveyId() > 0) {
                        submitCourseCardRequestData.setMappedSurveyId(courseDataClass.getMappedSurveyId());
                    }
                    if (courseDataClass.getMappedAssessmentId() > 0) {
                        submitCourseCardRequestData.setMappedAssessmentId(courseDataClass.getMappedAssessmentId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if ((removeLastCardData && !isLearningCard) && !isVideoOverlay) {
                Log.d(TAG, "sendCardSubmitRequest: Remove last card details");
                learningCardResponseDataList.remove((learningCardResponseDataList.size() - 1));
            }
            if (learningCardResponseDataList != null && learningCardResponseDataList.size() > 0) {
                submitCourseCardRequestData.setUserCardResponse(learningCardResponseDataList);
                String gcmToken = OustPreferences.get("gcmToken");
                if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                    submitCourseCardRequestData.setDeviceToken(gcmToken);
                }

                if (OustSdkTools.checkInternetStatus()) {
                    if (mPresenter != null) {
                        mPresenter.saveCardData(submitCourseCardRequestData, courseDataClass.getMappedSurveyId(), courseDataClass.getMappedAssessmentId());
                    }
                } else {
                    Gson gson = new Gson();
                    String str = gson.toJson(submitCourseCardRequestData);
                    Log.d(TAG, "sendCardSubmitRequest: " + str);
                    List<String> requests = OustPreferences.getLoacalNotificationMsgs("savedSubmitRequest");
                    requests.add(str);
                    OustPreferences.saveLocalNotificationMsg("savedSubmitRequest", requests);
                    if (userClickOnBackPressed) {
                        setEndAnimation();
                    } else if (cardDirections != null && !cardDirections.isEmpty() && cardDirections.equalsIgnoreCase("previous")) {
                        toolbar.setVisibility(View.VISIBLE);
                        question_card_submit_layout.setVisibility(View.GONE);
                        gotoPreviousScreen();
                    } else if (cardDirections != null && !cardDirections.isEmpty() && cardDirections.equalsIgnoreCase("next")) {
                        toolbar.setVisibility(View.VISIBLE);
                        question_card_submit_layout.setVisibility(View.GONE);
                        gotoNextScreen();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startUpdatedLearningMap(final boolean killActivity, final boolean updateReviewList) {
        try {
            Log.d(TAG, "startUpdatedLearningMap: " + killActivity + " updateReviewList--> " + updateReviewList);
            //killActivity always is coming as false so directly passed false
            if (isRegularMode || isSalesMode) {
                OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(killActivity, updateReviewList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void vibrateAndShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(CourseLearningModuleActivity.this, R.anim.shakescreen_anim);
            course_base_root.startAnimation(shakeAnim);
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startReverseTransaction() {
        try {
            destroyNudgeHandler();
            handleScreenTouchEvent(true);
            isVideoOverlay = false;
            videoProgress = 0;
            timeInterval = 0;
            isLearningCard = false;
            pauseAudioPlayer = false;
            isAudioPlayTrackComplete = false;
            isScormCard = false;
            isLastCardCompleted = false;
            audioEnable = false;
            openDiscussionBoard = false;
            isMute = false;
            disableBackButton = false;
            answeredSeconds = 0;
            isVideoCard = false;
            invalidateOptionsMenu();
            previous_view.setClickable(true);
            previous_view.setEnabled(true);
            OustSdkTools.isReadMoreFragmentVisible = false;
            OustPreferences.saveAppInstallVariable("isRightAnswerClicked", false);
            OustPreferences.save("MRQuestionAnswers", "");

            LearningCardResponceData learningCardResponse = OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1];
            Log.e(TAG, "Reverse " + learningCardResponse);
            if (!(!isCurrentCardQuestion || (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] != null))) {
                OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[questionNo - 1] = new LearningCardResponceData();
            }

            //timer progress default
            mandatory_timer_text.setTextColor(getResources().getColor(R.color.primary_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mandatory_timer_progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_correct)));
            }

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            //TODO: handle transition for reverse
            transaction.setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.fade_in,
                    R.anim.fade_out
            );

            if (downloadComplete) {
                if (questionNo >= levelCardsSize) {
                    responseTimeInSec = 0;
                }
                if (questionNo > 1) {
                    questionNo--;
                    reverseTransUsed = true;
                    if ((courseCardClassList != null) && (courseCardClassList.size() > (questionNo - 1))) {
                        int savedCardID = (int) courseCardClassList.get(questionNo - 1).getCardId();
                        DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                        if (courseCardClass == null) {
                            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                            if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                    if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                        dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                                        dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                                        RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                                        OustSdkTools.showToast("Card data was removed.Please download again");
                                        finish();
                                        break;
                                    }
                                }

                            }
                        }
                        courseCardClass.setSequence(courseCardClassList.get(questionNo - 1).getSequence());
                        if (courseCardClassList.get(questionNo - 1).isReadMoreCard()) {
                            courseCardClass = courseCardClassList.get(questionNo - 1);
                        }
                        if ((courseCardClass != null) && (courseCardClass.getCardType() != null)) {
//                            setLearningCardResponse(questionNo, courseCardClass);
                            if (courseCardClass.getQuestionData() != null) {
                                courseCardClass.setQuestionCategory(courseCardClass.getQuestionData().getQuestionCategory());
                                courseCardClass.setQuestionType(courseCardClass.getQuestionData().getQuestionType());
                            }
                            if (courseCardClassList.size() > questionNo) {
                                savedCardID = (int) courseCardClassList.get(questionNo).getCardId();
                                DTOCourseCard courseCardClassOther = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                                if (courseCardClassList.get(questionNo).isReadMoreCard()) {
                                    courseCardClassOther = courseCardClassList.get(questionNo);
                                }
                                if (courseCardClassOther.getReadMoreData() != null) {
                                    checkForFavourite("" + courseCardClassOther.getCardId(), courseCardClassOther.getReadMoreData().getRmId());
                                }
                                if (courseCardClassOther.getCardType().equalsIgnoreCase("LEARNING")) {
                                    checkForFavourite("" + courseCardClassOther.getCardId(), 0);
                                }
                            }
                            if (courseCardClass.getReadMoreData() != null) {
                                getReadMoreFavouriteStatus(courseCardClass.getReadMoreData().getRmId());
                            }
                            if (courseCardClass.getCardType().equalsIgnoreCase("LEARNING")) {
                                getCardFavouriteStatus("" + courseCardClass.getCardId());
                            }

                            DTOUserCardData dtoUserCardData = getCurrentDTOUserCardData(savedCardID);

                            if (favCardMode && courseCardClassList.size() > questionNo - 1 &&
                                    courseCardClass.getReadMoreData() != null && courseCardClass.isReadMoreCard()) {
                                isCurrentCardQuestion = false;
                                FavMode_ReadMoreFragmnet fragment = new FavMode_ReadMoreFragmnet();
                                transaction.replace(R.id.course_fragment_container, fragment);
                                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                fragment.setCardBackgroundImage(backgroundImage);
                                fragment.setCourseLevelClass(courseLevelClass);
                                fragment.setProgressVal(questionNo - 1);
                                fragment.clickedOnPrevious(true);
                                fragment.isFavourite(isRMFavourite);
                                fragment.setCourseCardClass(courseCardClass);
                                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                                transaction.commit();
                            } else if ((courseCardClass.getCardType().equalsIgnoreCase("LEARNING"))) {
                                courseCardClass.setSequence(courseCardClassList.get(questionNo - 1).getSequence());
                                responseTimeInSec = 0;
                                isCurrentCardQuestion = false;

                                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);

                                if (courseCardClass.getMandatoryViewTime() > 0) {
                                    timer_layout.setVisibility(View.VISIBLE);
                                    mandatory_timer_progress.setProgress(0);
                                    mandatory_timer_progress.setMax(100);
                                    setNextIconDrawableGrey();
                                    next_view.setEnabled(false);
                                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                    isLearnCardComplete(false);
                                    timer = new CounterClass((courseCardClass.getMandatoryViewTime() * 1000), 1000);
                                    if (isReviewMode || !isVideoCard(courseCardClass)) {
                                        timer.start();
                                    }
                                }

                                setQuestionNum((int) (courseCardClass.getSequence() - 1));
                                isLearningCard = true;
                                isComingFromCardFragment = true;
                                CardFragment cardFragment = new CardFragment();
                                transaction.replace(R.id.course_fragment_container, cardFragment, "cardFragment");
                                cardFragment.setLevel(courseLevelClass);
                                cardFragment.setCard(courseCardClass);
                                cardFragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                cardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                cardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                cardFragment.setReviewMode(isReviewMode);
                                cardFragment.setFavouriteCard(isFavourite);
                                cardFragment.setFavouriteCardList(favCardDetailsList);
                                cardFragment.setUserCardData(dtoUserCardData);
                                cardFragment.setCourseData(courseDataClass);
                                cardFragment.setCardBgImage(backgroundImage);
                                cardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                cardFragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                                transaction.commit();

                            } else if ((courseCardClass.getCardType().equalsIgnoreCase("SCORM"))) {
                                responseTimeInSec = 0;
                                isCurrentCardQuestion = false;
                                isScormCard = true;
                                isLearningCard = true;

                                next_view.setClickable(true);
                                next_view.setEnabled(true);
                                setNextIconDrawableBlack();

                                //handle scorm card
                                setQuestionNum((int) (courseCardClass.getSequence() - 1));

                                ScormCardFragment scormCardFragment = new ScormCardFragment();
                                transaction.replace(R.id.course_fragment_container, scormCardFragment, "ScormCardFragment");
                                scormCardFragment.setLevel(courseLevelClass);
                                scormCardFragment.setCard(courseCardClass);
                                scormCardFragment.setCourseData(courseDataClass);
                                scormCardFragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                scormCardFragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                scormCardFragment.setReviewMode(isReviewMode);
                                scormCardFragment.setFavouriteCard(isFavourite);
                                scormCardFragment.setFavouriteCardList(favCardDetailsList);
                                scormCardFragment.setUserCardData(dtoUserCardData);
                                scormCardFragment.setCardBgImage(backgroundImage);
                                scormCardFragment.setShowNudgeMessage(courseDataClass.isShowNudgeMessage());
                                scormCardFragment.setActiveUser(activeUser);
                                transaction.commit();

                                if (courseCardClass.isIfScormEventBased()) {
                                    bottom_bar.setVisibility(View.GONE);
                                }

                            } else {
                                responseTimeInSec = 0;
                                if (courseCardClass.getQuestionType() != null && courseCardClass.getQuestionCategory() != null) {
                                    if (!isReviewMode) {
                                        if ((courseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY))) {
                                            isCurrentCardQuestion = false;
                                            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                            isVideoOverlay = true;
                                            try {
                                                if (courseCardClass.getQuestionData().getListOfVideoOverlayQuestion() != null &&
                                                        courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size() != 0) {
                                                    number_of_video_overlay_questions = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                                } else {
                                                    number_of_video_overlay_questions = 1;
                                                }

                                            } catch (Exception e) {
                                                number_of_video_overlay_questions = 1;
                                            }
                                            //Video Overlay
                                            VideoOverlayFragmentNew fragment = new VideoOverlayFragmentNew();
                                            transaction.replace(R.id.course_fragment_container, fragment, "VideoOverlayFragmentNew");
                                            fragment.setCourseQuestion(true);
                                            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                            fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                            fragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                            fragment.setReviewMode(isReviewMode);
                                            fragment.setMainCourseCardClass(courseCardClass);
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setQuestionNo(questionNo + 1);
                                            fragment.setCourseData(courseDataClass);
                                            fragment.setBgImage(backgroundImage);
                                            fragment.setFavouriteCardList(favCardDetailsList);
                                            fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                            fragment.setIsRMFavourite(isRMFavourite);
                                            fragment.setCourseCompleted(isCourseCompleted);
                                            fragment.setActiveUser(activeUser);
                                            fragment.setCourseCardList(courseCardClassList);
                                            transaction.commit();

                                        } else {
                                            isCurrentCardQuestion = true;
                                            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                            if (isQuestionImageShown) {
                                                // show question symbol
                                                DTOCourseCard finalCourseCardClass = courseCardClass;
                                                FragmentTransaction transactionReverseCourse = this.getSupportFragmentManager().beginTransaction();
                                                //TODO: handle transition for reverse
                                                previous_view.setEnabled(false);
                                                transactionReverseCourse.setCustomAnimations(
                                                        R.anim.enter_from_right,
                                                        R.anim.exit_to_left
                                                );
                                                CourseQuestionMarkFragment courseQuestionMarkFragment = new CourseQuestionMarkFragment();
                                                courseQuestionMarkFragment.setBackGroundImage(backgroundImage);
                                                courseQuestionMarkFragment.setQuestionTimer(finalCourseCardClass.getQuestionData().getMaxtime());
                                                transactionReverseCourse.replace(R.id.course_fragment_container, courseQuestionMarkFragment);
                                                transactionReverseCourse.commit();
                                                int finalQuestionNo = questionNo - 1;
                                                new Handler().postDelayed(() -> {
                                                    try {
                                                        loadReverseQuestionData(finalCourseCardClass, finalQuestionNo);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        OustSdkTools.sendSentryException(e);
                                                    }
                                                }, 1000);
                                            } else {
                                                loadReverseQuestionData(courseCardClass, questionNo - 1);
                                            }
                                        }
                                    } else {
                                        if (courseCardClass.getQuestionCategory().equals(QuestionCategory.VIDEO_OVERLAY)) {
                                            isCurrentCardQuestion = false;
                                            isVideoOverlay = true;
                                            try {
                                                if (courseCardClass.getQuestionData().getListOfVideoOverlayQuestion() != null &&
                                                        courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size() != 0) {
                                                    number_of_video_overlay_questions = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion().size();
                                                } else {
                                                    number_of_video_overlay_questions = 1;
                                                }

                                            } catch (Exception e) {
                                                number_of_video_overlay_questions = 1;
                                            }
                                            // handle Video Overlay
                                            VideoOverlayFragmentNew fragment = new VideoOverlayFragmentNew();
                                            transaction.replace(R.id.course_fragment_container, fragment, "VideoOverlayFragmentNew");
                                            fragment.setCourseQuestion(true);
                                            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                                            fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                                            fragment.setCustomVideoControlInterface(CourseLearningModuleActivity.this);
                                            fragment.setReviewMode(isReviewMode);
                                            fragment.setMainCourseCardClass(courseCardClass);
                                            fragment.setCourseLevelClass(courseLevelClass);
                                            fragment.setQuestionNo(questionNo + 1);
                                            fragment.setCourseData(courseDataClass);
                                            fragment.setBgImage(backgroundImage);
                                            fragment.setFavouriteCardList(favCardDetailsList);
                                            fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                                            fragment.setIsRMFavourite(isRMFavourite);
                                            fragment.setCourseCompleted(isCourseCompleted);
                                            fragment.setActiveUser(activeUser);
                                            fragment.setCourseCardList(courseCardClassList);
                                            transaction.commit();


                                        } else {
                                            //handle other questions
                                            isCurrentCardQuestion = true;
                                            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                                            loadReverseQuestionData(courseCardClass, questionNo - 1);
                                        }
                                    }
                                }

                            }
                            gotCardDataThroughInterface = false;
                            mPresenter.saveCurrentCardNumber(false, courseLevelClass.getLevelId(), questionNo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadReverseQuestionData(DTOCourseCard courseCardClass, int questionNo) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        //TODO: handle transition for reverse
        transaction.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.fade_in,
                R.anim.fade_out
        );

        if (courseCardClass.getQuestionData().isLearningPlayNew()) {
            if (courseCardClass.getQuestionData().getQuestionCategory().equals(QuestionCategory.CATEGORY)) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (courseCardClass.getSequence() - 1));

                if (courseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();
                    view_next.setEnabled(false);
                    if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(courseCardClass.getQuestionData());
                        startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());

                    }
                }

                CategoryQuestionFragment fragment = new CategoryQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setReviewMode(isReviewMode);
                fragment.setReplayMode(isReplayMode);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                transaction.commit();
            } else if (((courseCardClass.getQuestionType() != null) &&
                    (courseCardClass.getQuestionType().equals(QuestionType.FILL)))) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (courseCardClass.getSequence() - 1));
                if (courseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();

                    next_view.setEnabled(false);
                    if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(courseCardClass.getQuestionData());
                        startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());
                    }
                }
                FIBQuestionFragment fragment = new FIBQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setReviewMode(isReviewMode);
                fragment.setReplayMode(isReplayMode);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                transaction.commit();
            } else if (((courseCardClass.getQuestionCategory() != null) &&
                    (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH)))) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (courseCardClass.getSequence() - 1));
                if (courseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();

                    next_view.setEnabled(false);
                    if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(courseCardClass.getQuestionData());
                        startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());
                    }
                }
                MTFQuestionFragment fragment = new MTFQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setReviewMode(isReviewMode);
                fragment.setReplayMode(isReplayMode);
                transaction.commit();
            } else if (((courseCardClass.getQuestionCategory() != null) &&
                    (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                isCurrentCardQuestion = true;
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                setQuestionNum((int) (courseCardClass.getSequence() - 1));
                if (courseCardClass.getQuestionData() != null) {
                    setNextIconDrawableGrey();

                    next_view.setEnabled(false);
                    if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        resetAudioPlayer();
                        hideTimer();
                    } else {
                        checkForQuestionAudio(courseCardClass.getQuestionData());
                        startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());
                    }
                }
                HotSpotQuestionFragment fragment = new HotSpotQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setReviewMode(isReviewMode);
                fragment.setReplayMode(isReplayMode);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                transaction.commit();
            }
        } else if ((courseCardClass.getQuestionData().isMediaUploadQues())) {
            isCurrentCardQuestion = true;
            OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
            setQuestionNum((int) (courseCardClass.getSequence() - 1));

            if (courseCardClass.getQuestionData() != null) {
                setNextIconDrawableGrey();
                next_view.setEnabled(false);
                if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else {
                    checkForQuestionAudio(courseCardClass.getQuestionData());
                    startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());

                }
            }
            isComingFromMultiMediaFragment = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                MUQuestionFragment fragment = new MUQuestionFragment();
                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
                fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setReviewMode(isReviewMode);
                fragment.setReplayMode(isReplayMode);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favCardDetailsList);
                fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setActiveUser(activeUser);
                transaction.commit();
            }
        } else if ((courseCardClass.getQuestionCategory() == null ||
                !courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                && (courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) ||
                courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {

            setQuestionNum((int) (courseCardClass.getSequence() - 1));

            if (courseCardClass.getQuestionData() != null) {
                setNextIconDrawableGrey();
                next_view.setEnabled(false);
                if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else {
                    checkForQuestionAudio(courseCardClass.getQuestionData());
                    startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());

                }
            }

            MCQuestionFragment fragment = new MCQuestionFragment();
            transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
            fragment.setCourseQuestion(true);
            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
            fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
            fragment.setReviewMode(isReviewMode);
            fragment.setReplayMode(isReplayMode);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setBgImage(backgroundImage);
            fragment.setFavouriteCardList(favCardDetailsList);
            fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            transaction.commit();

        } else if ((courseCardClass.getQuestionCategory() == null ||
                !courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                && (courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MRQ))) {
            setQuestionNum((int) (courseCardClass.getSequence() - 1));

            if (courseCardClass.getQuestionData() != null) {
                setNextIconDrawableGrey();
                next_view.setEnabled(false);
                if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else {
                    checkForQuestionAudio(courseCardClass.getQuestionData());
                    startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());

                }
            }

            MRQuestionFragment fragment = new MRQuestionFragment();
            transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
            fragment.setCourseQuestion(true);
            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
            fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
            fragment.setReviewMode(isReviewMode);
            fragment.setReplayMode(isReplayMode);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setBgImage(backgroundImage);
            fragment.setFavouriteCardList(favCardDetailsList);
            fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            transaction.commit();


        } else if (courseCardClass.getQuestionCategory() != null &&
                courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {

            setQuestionNum((int) (courseCardClass.getSequence() - 1));

            if (courseCardClass.getQuestionData() != null) {
                setNextIconDrawableGrey();
                next_view.setEnabled(false);
                if (courseCardClass.getQuestionData().getGumletVideoUrl() != null && !courseCardClass.getQuestionData().getGumletVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    resetAudioPlayer();
                    hideTimer();
                } else {
                    checkForQuestionAudio(courseCardClass.getQuestionData());
                    startQuestionTimer(courseCardClass.getQuestionData().getMaxtime());

                }
            }
            LongQuestionFragment fragment = new LongQuestionFragment();
            transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
            fragment.setCourseQuestion(true);
            fragment.setLearningModuleInterface(CourseLearningModuleActivity.this);
            fragment.setCourseContentHandlingInterface(CourseLearningModuleActivity.this);
            fragment.setReviewMode(isReviewMode);
            fragment.setReplayMode(isReplayMode);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setBgImage(backgroundImage);
            fragment.setFavouriteCardList(favCardDetailsList);
            fragment.setQuestionTTSEnabled(isQuizTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            transaction.commit();
        }
    }

    public void startQuestionTimer(long quesTime) {
        try {
            mandatory_timer_progress.setProgress(0);
            mandatory_timer_progress.setMax(100);
            timeExceeded = false;
            timer_layout.setVisibility(View.VISIBLE);
            timer = new CounterClass((quesTime * 1000), 1000);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideToolbar() {
        bottom_bar.setVisibility(View.GONE);
    }

    @Override
    public void fullHideToolbar() {
        bottom_bar.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        timer_layout.setVisibility(View.GONE);
    }

    @Override
    public void onVideoEnd() {
        try {
            if (questions != null && !isVideoOverlay) {
                timer_layout.setVisibility(View.VISIBLE);
                startQuestionTimer(questions.getMaxtime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showToolbar() {
        bottom_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void fullShowToolbar() {
        bottom_bar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        try {
            if (timerMax != 0 && mandatory_timer_progress.getProgress() != 0) {
                timer_layout.setVisibility(View.VISIBLE);
            } else {
                timer_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class CounterClass extends CountDownTimer {


        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            timerMax = millisInFuture / 1000;
        }

        @Override
        public void onFinish() {
            try {
                Log.d(TAG, "onFinish: CounterClass " + isCurrentCardQuestion + isVideoCard);
                try {
                    CardFragment cardFragment = (CardFragment) (getSupportFragmentManager().findFragmentByTag("cardFragment"));
                    if (cardFragment != null && (cardFragment.isVideoFullScreen() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                        timer_layout.setVisibility(View.GONE);
                    } else {
                        timer_layout.setVisibility(View.INVISIBLE);
                    }
                    timerMax = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                    timer_layout.setVisibility(View.GONE);
                }

                mandatory_timer_progress.setProgress(0);
                stopAnimation(mandatory_timer_progress);
                stopAnimation(mandatory_timer_text);

                if (!isVideoCard && !isCurrentCardQuestion) {
                    setNextIconDrawableBlack();
                    next_view.setEnabled(true);
                    OustStaticVariableHandling.getInstance().setLearniCardSwipeble(true);
                    isLearnCardComplete(true);
                    if (courseDataClass != null && courseDataClass.isAutoPlay()) {
                        gotoNextScreen();
                    }
                } else {
                    if (isCurrentCardQuestion) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("question_frag" + (questionNo));
                        if (questions != null) {
                            if (questions.isLearningPlayNew()) {
                                if ((questions.getQuestionType() != null && (questions.getQuestionType().equals(QuestionType.FILL)))) {
                                    if (frag != null) {
                                        FIBQuestionFragment fibQuestionFragment = (FIBQuestionFragment) (frag);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            fibQuestionFragment.handleAnswer(true, false, false);
                                        }
                                    }
                                } else if (questions.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                                    if (frag != null) {
                                        MTFQuestionFragment mtfQuestionFragment = (MTFQuestionFragment) frag;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            mtfQuestionFragment.scoreHandling(true, false, false);
                                        }
                                    }
                                } else if (questions.getQuestionCategory().equals(QuestionType.HOTSPOT)) {
                                    if (frag != null) {
                                        HotSpotQuestionFragment hotSpotQuestionFragment = (HotSpotQuestionFragment) frag;
                                        hotSpotQuestionFragment.handleTimerEnd();
                                    }
                                } else if (questions.getQuestionCategory().equals(QuestionCategory.CATEGORY)) {
                                    if (frag != null) {
                                        CategoryQuestionFragment categoryQuestionFragment = (CategoryQuestionFragment) frag;
                                        categoryQuestionFragment.calculatePoints(true);
                                    }
                                }
                            } else if (questions.isMediaUploadQues()) {
                                if (frag != null) {
                                    MUQuestionFragment muQuestionFragment = (MUQuestionFragment) frag;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        muQuestionFragment.onTimeOut(false, false);
                                    }
                                }
                            } else if (questions.getQuestionType() != null) {
                                if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ)
                                        || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))
                                        && (questions.getQuestionCategory() == null || !questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))) {
                                    if (frag != null) {
                                        MCQuestionFragment mcQuestionFragment = (MCQuestionFragment) frag;
                                        mcQuestionFragment.setAnswers(1, false, false);
                                    }
                                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)
                                        && (questions.getQuestionCategory() == null || !questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))) {
                                    if (frag != null) {
                                        MRQuestionFragment mrQuestionFragment = (MRQuestionFragment) frag;
                                        mrQuestionFragment.calculateMrqAnswer(true, true, false, false, false);
                                    }
                                } else if (questions.getQuestionCategory() != null
                                        && questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
                                    if (frag != null) {
                                        LongQuestionFragment longQuestionFragment = (LongQuestionFragment) frag;
                                        longQuestionFragment.setAnswerForLongAnswer(false, false);
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
        public void onTick(long millisUntilFinished) {
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

            String hms = String.format(Locale.ENGLISH, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            try {
                int timerProgress = 0;
                if (timerMax > 0) {
                    timerProgress = (int) (answeredSeconds * 100 / timerMax);
                }
                mandatory_timer_progress.setProgress(timerProgress);
                mandatory_timer_text.setText(hms);

                if (!timeExceeded && answeredSeconds <= (0.05 * timerMax)) {
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

    private void stopAnimation(View view) {
        try {
            if (scaleDown != null) {
                scaleDown.removeAllListeners();
                scaleDown.end();
                scaleDown.cancel();
                view.clearAnimation();
                view.setAnimation(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionNum(int questionNo) {

        String currentQuestion = (questionNo + 1) + " / " + courseLevelClass.getCourseCardClassList().size();
        view_count.setText(currentQuestion);

        if (questionNo == 0) {
            previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.unselected_text)));
            previous_view.setEnabled(false);
        } else {
            previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.primary_text)));
            previous_view.setEnabled(true);
        }

    }

    @Override
    public void setVideoCard(boolean isVideoCard) {
        this.isVideoCard = isVideoCard;
        setNextIconDrawableGrey();
        next_view.setEnabled(false);
    }

    @Override
    public void cancelTimer() {
        try {
            if (null != timer)
                timer.cancel();

            stopAnimation(mandatory_timer_progress);
            stopAnimation(mandatory_timer_text);
            String timerZeroText = "00:00";
            mandatory_timer_text.setText(timerZeroText);
            timerMax = 0;
            timer_layout.setVisibility(View.GONE);

            if (!isVideoOverlay) {
                setNextIconDrawableBlack();
                next_view.setEnabled(true);

                if (isReviewMode || courseDataClass.isSalesMode()) {
                    if ((courseLevelClass.getCourseCardClassList().size() < questionNo + 1)) {
                        setNextIconDrawableGrey();
                        next_view.setEnabled(false);
                    }

                    if (questionNo - 1 == 0) {
                        previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.unselected_text)));
                        previous_view.setEnabled(false);
                    } else {
                        previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.primary_text)));
                        previous_view.setEnabled(true);
                    }
                }
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
            File audioFile = new File(OustSdkApplication.getContext().getFilesDir(), audioFilePath);
            if (audioFile.exists()) {
                mediaPlayerForAudio = new MediaPlayer();
                mediaPlayerForAudio.reset();
                FileInputStream fis = new FileInputStream(audioFile);
                mediaPlayerForAudio.setDataSource(fis.getFD());
                mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerForAudio.prepare();
                mediaPlayerForAudio.start();
                audioEnable = true;
                invalidateOptionsMenu();
                mediaPlayerForAudio.setOnCompletionListener(mediaPlayer -> {
                    Log.e(TAG, "onCompletion: ");
                    isAudioPlayTrackComplete = true;
                });
            } else {
                resetAudioPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void checkForQuestionAudio(DTOQuestions questions) {
        Log.d(TAG, "checkForAudio: ");
        resetAudioPlayer();
        if (questions != null) {
            if (!(questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                if (questions.getAudio() != null && !questions.getAudio().isEmpty()) {
                    itemAudio.setVisible(true);
                    initializeQuestionSound(questions.getAudio());
                    audioEnable = true;
                    invalidateOptionsMenu();
                } else {
                    itemAudio.setVisible(false);
                }
            } else {
                resetAudioPlayer();
            }
        }
    }

    @Override
    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    @Override
    public void showNudge() {
        nudgeHandler = new Handler();
        nudgeHandler.postDelayed(() -> {
            try {
                new MaterialTapTargetPrompt.Builder(CourseLearningModuleActivity.this)
                        .setTarget(next_view)
                        .setAutoDismiss(true)
                        .setIcon(R.drawable.ic_arrow_forward_white)
                        .setPrimaryText("")
                        .setFocalColour(color)
//                                .setBackgroundColour(Color.TRANSPARENT)
                        .setBackgroundColour(Color.TRANSPARENT)
                        //.setSecondaryText("Click here to move next")
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }, 3000);
    }

    @Override
    public void handleScreenTouchEvent(boolean isTouchable) {
        try {
            if (!isTouchable) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void disableNextArrow() {
        try {
            setNextIconDrawableGrey();
            next_view.setClickable(false);
            next_view.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startMandatoryViewTimer() {
        try {
            runOnUiThread(() -> {
                if (timer_layout != null) {
                    timer_layout.setVisibility(View.VISIBLE);
                }
            });
            if (timer != null) {
                timer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setAnswerAndOCRequest(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                sentDataToServer = false;
                toolbar.setVisibility(View.GONE);
                question_card_submit_layout.setVisibility(View.VISIBLE);
                submit_progress.setIndicatorColor(color);
                submit_progress.setTrackColor(getResources().getColor(R.color.gray));
                cancelTimer();
            }
            setAnswerAndOc(userAns, subjectiveResponse, oc, status, time);
        } catch (Exception e) {
            toolbar.setVisibility(View.VISIBLE);
            question_card_submit_layout.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enableOrDisableView(View view, boolean viewStatus) {
        try {
            view.setEnabled(viewStatus);
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = 0; i < group.getChildCount(); i++) {
                    enableOrDisableView(group.getChildAt(i), viewStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void showSolutionPopUP(String message, boolean showSolutionAnswer, boolean popupDismiss, boolean submitCardData, String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            if (!popupDismiss) {
                cancelTimer();
                disableNextArrow();
                previous_view.setImageDrawable(OustSdkTools.drawableColor(previous_view.getDrawable(), getResources().getColor(R.color.unselected_text)));
                previous_view.setClickable(false);
                previous_view.setEnabled(false);
            }
            show_answer_layout.setVisibility(View.VISIBLE);
            enableOrDisableView(course_fragment_container, false);

            ViewGroup.LayoutParams lp = course_fragment_container.getLayoutParams();
            lp.height = lp.height - 45;
            course_fragment_container.setLayoutParams(lp);

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            new Handler().postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), FIVE_HUNDRED_MILLI_SECONDS);

            solution_action_button.setBackgroundColor(color);
            text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            text_title.setTextColor(getResources().getColor(R.color.black));

            if (showSolutionAnswer) {
                status_image.setImageResource(R.drawable.ic_filled_tick);
                status_text.setText(getResources().getString(R.string.correct_text).toUpperCase() + "!");
                status_message.setText(getResources().getString(R.string.greatjob_result));
            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_close_circle);
                status_image.setImageDrawable(OustResourceUtils.setDefaultDrawableColor(drawable, getResources().getColor(R.color.error_incorrect)));
                status_text.setText(getResources().getString(R.string.incorrect_txt));
                status_message.setText(getResources().getString(R.string.incorrect_sub_txt));
            }

            expand_icon.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "expand icon clicked " + sheetBehavior.getState());
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        text_title.setTextColor(getResources().getColor(R.color.unselected_text));
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        text_title.setTextColor(getResources().getColor(R.color.black));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    try {
                        if (slideOffset <= 0.5) {
                            text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            text_title.setTextColor(getResources().getColor(R.color.black));
                        } else if (slideOffset > 0.5) {
                            text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            text_title.setTextColor(getResources().getColor(R.color.unselected_text));
                        }
                        expand_icon.setRotation(slideOffset * 180);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });

            solution_action_button.setOnClickListener(v -> {
                try {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (!popupDismiss) {
                        new Handler().postDelayed(() -> {
                            show_answer_layout.setVisibility(View.GONE);
                            if (OustSdkTools.checkInternetStatus()) {
                                if (submitCardData) {
                                    setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                                } else {
                                    Log.e(TAG, "showSolutionPopUP: submitCardData--> " + submitCardData);
                                }
                            } else {
                                setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                            }
                        }, TWO_HUNDRED_MILLI_SECONDS);
                    } else {
                        sheetBehavior.setHideable(true);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        show_answer_layout.setVisibility(View.GONE);
                        enableOrDisableView(course_fragment_container, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            if (message != null && !message.isEmpty() && solution_content != null) {
                if (message.contains(KATEX_DELIMITER)) {
                    katex_show_solution_txt.setTextColor(getResources().getColor(R.color.primary_text));
                    katex_show_solution_txt.setTextDirection(View.TEXT_ALIGNMENT_CENTER);
                    katex_show_solution_txt.setText(message.trim());
                    math_show_solution_layout.setVisibility(View.GONE);
                    solution_content.setVisibility(View.GONE);
                    katex_show_solution_layout.setVisibility(View.VISIBLE);
                } else if (message.contains("<math")) {
                    math_show_solution_layout.setVisibility(View.VISIBLE);
                    katex_show_solution_layout.setVisibility(View.GONE);
                    solution_content.setVisibility(View.GONE);
                    OustSdkTools.getSpannedMathmlContent(message.trim(), math_solution_layout_txt, true);
                } else {
                    OustSdkTools.loadDataFromHtml(message.trim(), solution_content);
                    katex_show_solution_layout.setVisibility(View.GONE);
                    math_show_solution_layout.setVisibility(View.GONE);
                    solution_content.setVisibility(View.VISIBLE);
                }
            }

           /* try {
                view_next.setEnabled(true);
                next_view.setEnabled(true);
                next_view.setClickable(true);
                view_next.setClickable(true);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void hideBottomBar(boolean isHide) {
        try {
            if (isHide) {
                bottom_bar.setVisibility(View.GONE);
            } else {
                bottom_bar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void handleScormCard(boolean isScormEventBased) {

        try {
            if (isScormEventBased) {
                bottom_bar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void handleLandScape() {
        try {
            bottom_bar.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void handleFullScreen(boolean isFullScreen) {
        try {
            if (isFullScreen) {
                bottom_bar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
            } else {
                bottom_bar.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void handlePortrait(boolean isScormEventBased) {

        try {
            if (isScormEventBased) {
                bottom_bar.setVisibility(View.GONE);
            } else {
                bottom_bar.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void hideTimer() {
        try {
            timer_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean checkTimerVisibility() {
        boolean visibility = false;
        try {
            if (timer_layout.getVisibility() == View.VISIBLE || timer_layout.getVisibility() == View.INVISIBLE) {
                visibility = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return visibility;
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

            if ((audio != null) && (!audio.isEmpty())) {
                String mediaPath = audio;
                if (audio.contains("/")) {
                    mediaPath = audio.substring(audio.lastIndexOf("/") + 1);
                }
                String path = "qaudio/" + mediaPath;
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                    if (!file.exists() && OustSdkTools.checkInternetStatus()) {
                        audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + audio;
                        prepareExoPlayerFromUri(Uri.parse(audio));
                    } else {
                        prepareExoPlayer(url);
                    }
                } else {
                    String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                    if (!file.exists() && OustSdkTools.checkInternetStatus()) {
                        audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + audio;
                        prepareExoPlayerFromUri(Uri.parse(audio));
                    } else {
                        prepareExoPlayer(url);
                    }
                }
            }

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
                mediaPlayerForAudio.setDataSource(CourseLearningModuleActivity.this, uri);
                mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerForAudio.prepare();
                mediaPlayerForAudio.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void resetAudioPlayer() {
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

    private void resetAllData() {
        try {
            myHandler = null;
            courseLevelClass = null;
            courseCardClassList = null;
            OustStaticVariableHandling.getInstance().setLearningCardResponceDatas(null);
            activeUser = null;
            courseName = null;
            courseDataClass = null;
            backgroundImage = null;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyNudgeHandler();
        try {
            if (null != timer)
                timer.cancel();
            try {
                resetAudioPlayer();
                if (OustStaticVariableHandling.getInstance().isLearningShareClicked()) {
                    super.onDestroy();
                } else {
                    if (myHandler != null) {
                        myHandler.removeCallbacksAndMessages(null);
                    }
                    if (!backBtnPressed) {
                        backBtnPressed = true;
                        if (!OustSdkTools.checkInternetStatus() || isSalesMode)
                            sendDataToServer();
                    }
                }
                OustStaticVariableHandling.getInstance().setLevelCompleted(false);
                Runtime.getRuntime().gc();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void destroyNudgeHandler() {
        try {
            if (nudgeHandler != null) {
                nudgeHandler.removeCallbacksAndMessages(null);
                nudgeHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setNextIconDrawableGrey() {
        try {
            next_view.setImageDrawable(OustSdkTools.drawableColor(next_view.getDrawable(), getResources().getColor(R.color.unselected_text)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setNextIconDrawableBlack() {
        try {
            next_view.setImageDrawable(OustSdkTools.drawableColor(next_view.getDrawable(), getResources().getColor(R.color.primary_text)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}