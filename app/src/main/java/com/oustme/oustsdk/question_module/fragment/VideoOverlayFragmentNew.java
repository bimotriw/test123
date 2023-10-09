package com.oustme.oustsdk.question_module.fragment;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.NewCustomExoPlayerView;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.EmptyFragment;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.course.CourseLearningModuleActivity;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.service.DownLoadIntentService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class VideoOverlayFragmentNew extends Fragment implements View.OnClickListener, View.OnTouchListener, DialogKeyListener {

    private static final String TAG = "VideoOverlayFragmentNew";
    int width_toolbar;
    int height_toolbar;
    int height_bottom_bar;
    RelativeLayout question_no_layout, video_player_layout, quesvideoLayout;
    ProgressBar video_loader;
    Context context;
    CardView question_card;
    boolean isDownloadButtonclicked = false;
    GifImageView downloadvideo_icon;
    File tempVideoFileName;
    boolean isVideoDownloding = false;
    String videoFileName;
    String videoFilePath;
    boolean isFileDownloadServiceStarted;
    TextView downloadvideo_text;
    Button show_quality;
    boolean isFullScreen;
    ImageView video_landscape_zoom;

    private MyFileDownloadReceiver myFileDownLoadReceiver;
    private NewCustomExoPlayerView customExoPlayerView;

    boolean isVideoCompleted = false;
    ScrollView mainoption_scrollview;

    boolean tochedScreen = false;

    Handler myHandler;
    int scrWidth, scrHeight;
    String questionType;

    int learningcardProgress = 0;

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    CustomVideoControlListener customVideoControlListener;
    CourseContentHandlingInterface courseContentHandlingInterface;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    int questionNo;
    CourseDataClass courseDataClass;
    boolean zeroXpForQCard;
    String backgroundImage;
    List<FavCardDetails> favouriteCardList;
    boolean isQuestionTTSEnabled;
    boolean isRMFavourite;
    boolean isCourseCompleted;
    boolean isCourseQuestion;
    ActiveUser activeUser;
    ImageView question_bgImg;

    //Common question card
    TextView question_count_num, question;
    WebView question_description_webView;

    DTOCourseSolutionCard courseSolutionCard;
    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    long[] extraAdGroupTimesMs = null;
    boolean[] extraPlayedAdGroups = {true, true, true};
    int autoPause = 0;
    double current_exoplayer_sec = 0;
    boolean hasVideoQuestion = false;
    FrameLayout framelayout_video_overlay;
    List<DTOCourseCard> courseCardClassList;

    String fragmentTag = "VideoOverlayNew";

    private boolean isReviewMode;
    Handler video_overlay_HandlerTimer;
    Runnable runnableTimer;
    DTOUserCardData userCardData;
    long videoSeekTime;
    long time = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videooverlay_new, container, false);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews(view);
        initVideoViewFragment();
    }

    private void initViews(View view) {
        question_no_layout = view.findViewById(R.id.questionno_layout);
        video_player_layout = view.findViewById(R.id.video_player_layout);
        quesvideoLayout = view.findViewById(R.id.quesvideoLayout);
        video_loader = view.findViewById(R.id.video_loader);
        question_card = view.findViewById(R.id.question_card);
        question = view.findViewById(R.id.question);
        question_description_webView = view.findViewById(R.id.description_webView);
        question_count_num = view.findViewById(R.id.question_count_num);

        downloadvideo_icon = view.findViewById(R.id.downloadvideo_icon);
        downloadvideo_text = view.findViewById(R.id.downloadvideo_text);
        show_quality = view.findViewById(R.id.show_quality);
        video_landscape_zoom = view.findViewById(R.id.video_landscape_zoom);
        mainoption_scrollview = view.findViewById(R.id.mainoption_scrollview);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        framelayout_video_overlay = view.findViewById(R.id.framelayout_video_overlay);
        framelayout_video_overlay.setVisibility(View.GONE);
        video_landscape_zoom.setVisibility(View.GONE); // as per the iOS, we are also hiding full screen icon...
        video_landscape_zoom.setOnClickListener(this);
        try {
            OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (isCourseQuestion) {
            setQuestionData(questions);
        }
        customVideoControlListener.hideToolbar();
        question_card.setVisibility(View.GONE);
        video_overlay_HandlerTimer = new Handler(Looper.getMainLooper());
//        video_overlay_Handler = new Handler(Looper.getMainLooper());
    }

    private void setQuestionData(DTOQuestions questions) {
        try {
            if (questions != null) {
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(questions);
                }
                if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {
                    //question.setText(questions.getQuestion());
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
                if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(questions.getBgImg()).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initVideoViewFragment() {
        try {
            Objects.requireNonNull(requireActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (OustAppState.getInstance().getActiveUser() == null) {
                Objects.requireNonNull(requireActivity()).finish();
            }
            videoSeekTime = 0;
            getWidth();
            myHandler = new Handler();
            setStartingData();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setStartingData() {
        try {
            int waitTimer = 1000;
            if (learningcardProgress == 0) {
                waitTimer = 1200;
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
            //startQuestionNOHideAnimation(waitTimer);
            if (questions != null) {
                Log.d(TAG, "setStartingData: " + questions.getQuestion());

                List<DTOVideoOverlay> videoOverlayList = questions.getListOfVideoOverlayQuestion();
                extraAdGroupTimesMs = null;
                extraPlayedAdGroups = null;
                if (videoOverlayList != null) {
                    extraAdGroupTimesMs = new long[videoOverlayList.size()];
                    extraPlayedAdGroups = new boolean[videoOverlayList.size()];
                    int i = 0;
                    for (DTOVideoOverlay videoOverlay : videoOverlayList) {
                        extraAdGroupTimesMs[i] = videoOverlay.getTimeDuration();
                        extraPlayedAdGroups[i] = true;
                        i++;
                    }
                }

                if ((questions.getGumletVideoUrl() != null) && (!questions.getGumletVideoUrl().isEmpty())) {
                    hasVideoQuestion = true;
                    setQuestionVideo(questions.getqVideoUrl(), waitTimer);
                } else if ((questions.getqVideoUrl() != null) && (!questions.getqVideoUrl().isEmpty())) {
                    hasVideoQuestion = true;
                    setQuestionVideo(questions.getqVideoUrl(), waitTimer);
                } else {
                    OustSdkTools.showToast("Video file is not available.Please contact admin");
                    new Handler().postDelayed(() -> Objects.requireNonNull(requireActivity()).finish(), 1000);
                }
                setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
            }
        } catch (Exception e) {
            Log.d(TAG, "setStartingData: exception");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionVideo(String path, final int waitTimer) {
        videoFilePath = path;
        quesvideoLayout.setVisibility(View.VISIBLE);
        video_player_layout.setVisibility(View.VISIBLE);
        customExoPlayerView = new NewCustomExoPlayerView() {
            @Override
            public void onAudioComplete() {

            }

            @Override
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete: ");
                isVideoCompleted = true;
                stopVideoOverlayTimer();
                customVideoControlListener.showToolbar();
                question_card.setVisibility(View.VISIBLE);
                try {
                    if (customExoPlayerView != null) {
                        setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                handleNextQuestion();
            }

            @Override
            public void onBuffering() {
                video_loader.setVisibility(View.VISIBLE);
                setAutoPause();
                if (runnableTimer == null) {
                    Log.d(TAG, "onPlayerStateChanged: Restart video overlay timer");
                    startVideoOverlayTimer();
                }

            }

            @Override
            public void onVideoError() {
                video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onPlayReady() {
                video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onIdle() {
                video_loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRestarted() {
                isVideoCompleted = false;
                setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
            }
        };

        String[] url_path_array = path.split("/");
        if (url_path_array.length > 0) {
            videoFileName = url_path_array[url_path_array.length - 1];
        }

        Log.d(TAG, "setQuestionVideo: Filename:" + videoFileName);

        isVideoCompleted = false;
        customExoPlayerView.initExoPlayer(video_player_layout, context, path, videoFileName, videoSeekTime);
        setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());

        if (extraAdGroupTimesMs != null && extraPlayedAdGroups != null) {
            customExoPlayerView.getSimpleExoPlayerView().setExtraAdGroupMarkers(extraAdGroupTimesMs, extraPlayedAdGroups);
        }

        if (runnableTimer == null) {
            Log.d(TAG, "startVideoPlayer: Strating video overlay timer");
            startVideoOverlayTimer();
        }

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String path1 = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
            File file = new File(path1);
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
//        customExoPlayerView.getTotalTime();
    }

    private void handleNextQuestion() {
        try {
            OustPreferences.saveTimeForNotification("VideoOverlayCardPauseTime", 0);
            OustPreferences.saveTimeForNotification("VideoOverlayCardTotalVideoTime", 0);
//            if (!isReviewMode) {
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
            Log.d(TAG, "current_pos2::: " + customExoPlayerView.getSimpleExoPlayer().getCurrentPosition());
//            }
            OustPreferences.saveintVar("VideoOverlayCardNumberOfAnswered", 0);

            try {
                new Handler().postDelayed(() -> {
                    if (learningModuleInterface != null) {
                        if (isReviewMode) {
                            learningModuleInterface.gotoNextScreen();
                            removeAllData();
                        } else {
                            if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas() != null) {
                                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress] != null) {
                                    if (courseContentHandlingInterface != null) {
                                        courseContentHandlingInterface.setAnswerAndOCRequest("", "", 100, true, 0);
                                    }
                                    removeAllData();
                                }
                            }
                        }

                    }
                }, FOUR_HUNDRED_MILLI_SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public NewCustomExoPlayerView getCustomExoPlayerView() {
        return customExoPlayerView;
    }

    private void showVideoOverlayQuestions() {
        Log.d(TAG, "showVideoOverlayQuestions: " + autoPause + " === zexoxp:" + zeroXpForQCard);
        framelayout_video_overlay.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.anim_slidein);
        int questionNo = autoPause - 1;
        DTOVideoOverlay videoOverlay = questions.getListOfVideoOverlayQuestion().get(questionNo);

        int savedCardID = (int) videoOverlay.getChildCoursecardId();
        DTOCourseCard courseCardClass = videoOverlay.getChildQuestionCourseCard();
        if (RoomHelper.getQuestionById(courseCardClass.getqId()) != null) {
            courseCardClass.setQuestionCategory(courseCardClass.getQuestionData().getQuestionCategory());
            courseCardClass.setQuestionType(courseCardClass.getQuestionData().getQuestionType());
        }

        OustPreferences.saveTimeForNotification("VideoOverlayCardStartTime", System.currentTimeMillis());
        OustPreferences.saveTimeForNotification("VideoOverlayCardPauseTime", extraAdGroupTimesMs[autoPause - 1]);
        if (!isReviewMode) {
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", customExoPlayerView.getSimpleExoPlayer().getCurrentPosition() - 1000);
            Log.d(TAG, "current_pos2: " + customExoPlayerView.getSimpleExoPlayer().getCurrentPosition());
        }
        OustPreferences.saveintVar("VideoOverlayCardNumberOfAnswered", autoPause);
        if (courseCardClass.getQuestionData().isLearningPlayNew()) {

            if (((courseCardClass.getQuestionType() != null) &&
                    (courseCardClass.getQuestionType().equals(QuestionType.FILL)))) {
                try {
                    if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                    } else {
                        ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                FIBQuestionFragment fragment = new FIBQuestionFragment();
                fragment.setCourseQuestion(true);
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setLearningModuleInterface(learningModuleInterface);
                fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setReviewMode(isReviewMode);
                fragment.setFavouriteCardList(favouriteCardList);
                fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setVideoOverlay(true);
                fragment.setProceedOnWrong(questions.isProceedOnWrong());
                transaction.commit();
            } else if (((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY)))) {
                try {
                    if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                    } else {
                        ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                CategoryQuestionFragment fragment = new CategoryQuestionFragment();
//            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag + questionNo);
                fragment.setCourseQuestion(true);
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setLearningModuleInterface(learningModuleInterface);
                fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setReviewMode(isReviewMode);
                fragment.setFavouriteCardList(favouriteCardList);
                fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setVideoOverlay(true);
                fragment.setProceedOnWrong(questions.isProceedOnWrong());
                transaction.commit();
            } else if (((courseCardClass.getQuestionCategory() != null) &&
                    (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH)))) {
                try {
                    if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                    } else {
                        ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                MTFQuestionFragment fragment = new MTFQuestionFragment();
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(learningModuleInterface);
                fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favouriteCardList);
                fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setVideoOverlay(true);
                fragment.setReviewMode(isReviewMode);
                transaction.commit();
            } else if (((courseCardClass.getQuestionCategory() != null) &&
                    (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                try {
                    if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                        ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                    } else {
                        ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                HotSpotQuestionFragment fragment = new HotSpotQuestionFragment();
//                transaction.replace(R.id.course_fragment_container, fragment, "question_frag" + (questionNo + 1));
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setCourseQuestion(true);
                fragment.setLearningModuleInterface(learningModuleInterface);
                fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
                fragment.setReviewMode(isReviewMode);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setQuestionNo(questionNo + 1);
                fragment.setCourseData(courseDataClass);
                fragment.setBgImage(backgroundImage);
                fragment.setFavouriteCardList(favouriteCardList);
                fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setCourseCompleted(isCourseCompleted);
                fragment.setToolbarHeight(width_toolbar, height_toolbar, height_bottom_bar);
                fragment.setVideoOverlay(true);
                fragment.setProceedOnWrong(questions.isProceedOnWrong());
                transaction.commit();
            }
        } else if ((courseCardClass.getQuestionCategory() == null ||
                !courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)
                        && !courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))
                && (courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) ||
                courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {
            try {
                if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                } else {
                    ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            MCQuestionFragment fragment = new MCQuestionFragment();
//            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag + questionNo);
            fragment.setCourseQuestion(true);
            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
            fragment.setLearningModuleInterface(learningModuleInterface);
            fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setBgImage(backgroundImage);
            fragment.setReviewMode(isReviewMode);
            fragment.setFavouriteCardList(favouriteCardList);
            fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            fragment.setVideoOverlay(true);
            fragment.setProceedOnWrong(questions.isProceedOnWrong());
            transaction.commit();
        } else if ((courseCardClass.getQuestionCategory() == null ||
                !courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))
                && (courseCardClass.getQuestionType().equalsIgnoreCase(QuestionType.MRQ))) {
            try {
                if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                } else {
                    ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            MRQuestionFragment fragment = new MRQuestionFragment();
//            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag + questionNo);
            fragment.setCourseQuestion(true);
            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
            fragment.setLearningModuleInterface(learningModuleInterface);
            fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setBgImage(backgroundImage);
            fragment.setFavouriteCardList(favouriteCardList);
            fragment.setReviewMode(isReviewMode);
            fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            fragment.setVideoOverlay(true);
            fragment.setProceedOnWrong(questions.isProceedOnWrong());
            transaction.commit();
        } else if (courseCardClass.getQuestionCategory() != null &&
                courseCardClass.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER)) {
            try {
                if (courseCardClass.getQuestionData().getqVideoUrl() != null && !courseCardClass.getQuestionData().getqVideoUrl().isEmpty()) {
                    ((CourseLearningModuleActivity) requireActivity()).resetAudioPlayer();
                } else {
                    ((CourseLearningModuleActivity) requireActivity()).checkForQuestionAudio(courseCardClass.getQuestionData());

                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            LongQuestionFragment fragment = new LongQuestionFragment();
//            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag + questionNo);
            fragment.setCourseQuestion(true);
            transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
            fragment.setLearningModuleInterface(learningModuleInterface);
            fragment.setCourseContentHandlingInterface(courseContentHandlingInterface);
            fragment.setMainCourseCardClass(courseCardClass);
            fragment.setCourseLevelClass(courseLevelClass);
            fragment.setQuestionNo(questionNo + 1);
            fragment.setCourseData(courseDataClass);
            fragment.setReviewMode(isReviewMode);
            fragment.setBgImage(backgroundImage);
            fragment.setFavouriteCardList(favouriteCardList);
            fragment.setQuestionTTSEnabled(isQuestionTTSEnabled);
            fragment.setIsRMFavourite(isRMFavourite);
            fragment.setCourseCompleted(isCourseCompleted);
            fragment.setVideoOverlay(true);
            fragment.setProceedOnWrong(questions.isProceedOnWrong());
            transaction.commit();
        }

    }

    Handler video_overlay_Handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            try {
                Log.d(TAG, "handleMessage: about to pause");
                if (customExoPlayerView != null) {
                    if (customExoPlayerView.getPauseButtonVisibility() == View.VISIBLE) {
                        customExoPlayerView.performPauseclick();
                    }
                    showVideoOverlayQuestions();
                }

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };

    private void startVideoOverlayTimer() {
        try {
            OustStaticVariableHandling.getInstance().setVideoOverlayQuestion(true);
            Log.d(TAG, "startVideoOverlayTImer: ");
            runnableTimer = () -> {
                try {
                    if (customExoPlayerView != null && customExoPlayerView.getPauseButtonVisibility() == View.VISIBLE) {
                        long exoPlayer_position = customExoPlayerView.getSimpleExoPlayer().getCurrentPosition();
                        current_exoplayer_sec = exoPlayer_position / 100;
                        if (extraAdGroupTimesMs != null) {
                            for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                                if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                                    autoPause = i + 1;
                                    Message message = video_overlay_Handler.obtainMessage(1, "Start");
                                    message.sendToTarget();
                                    break;
                                }
                            }
                        }
                    }
                    video_overlay_HandlerTimer.postDelayed(runnableTimer, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            };
            video_overlay_HandlerTimer.post(runnableTimer);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPortaitVideoRatio(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = false;
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

    private void setPotraitVideoRatioFullScreen(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = true;
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


    private void stopVideoOverlayTimer() {
        Log.d(TAG, "stopVideoOverlayTimer: ");
        autoPause = 0;
        if (video_overlay_HandlerTimer != null) {
            video_overlay_HandlerTimer.removeCallbacks(runnableTimer);
        }
        OustStaticVariableHandling.getInstance().setVideoOverlayQuestion(false);
    }


    private void setAutoPause() {
        Log.d(TAG, "setAutoPause: ");
        if (runnableTimer != null && customExoPlayerView != null) {
            long exoPlayer_position = customExoPlayerView.getSimpleExoPlayer().getCurrentPosition();
            Log.d(TAG, "current_pos1: " + exoPlayer_position);
            int exoplayer_sec = (int) exoPlayer_position / 100;
            boolean isAutoPauseset = false;

            if (extraAdGroupTimesMs != null) {
                for (int i = 0; i < extraAdGroupTimesMs.length; i++) {
                    int current_sec = (int) (extraAdGroupTimesMs[i] / 100);
                    if (exoplayer_sec <= current_sec) {
                        autoPause = i;
                        isAutoPauseset = true;
                        break;
                    }
                }
            }

            if (!isAutoPauseset) {
                autoPause = (extraAdGroupTimesMs != null) ? extraAdGroupTimesMs.length : 0;
            }

            Log.d(TAG, "setAutoPause:" + autoPause);
        }
    }

    private void setDownloadBtn() {
        Log.d(TAG, "setDownloadBtn: ");
        downloadvideo_icon.setVisibility(View.VISIBLE);
        downloadvideo_icon.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startDownloadingVideo();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
        });
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
                OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                String path = OustSdkApplication.getContext().getFilesDir().getPath();
                tempVideoFileName = new File(path);
                sendToDownloadService(OustSdkApplication.getContext(), videoFilePath, path, videoFileName);
            }
        } else {
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    private void sendToDownloadService(Context context, String downloadPath, String destination, String fileName) {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, false);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destination);
        context.startService(intent);
        isFileDownloadServiceStarted = true;
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            if (id == R.id.video_landscape_zoom) {
                if (!isFullScreen) {
                    customVideoControlListener.hideToolbar();
                    question_card.setVisibility(View.GONE);
                    setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
                } else {
                    customVideoControlListener.showToolbar();
                    question_card.setVisibility(View.VISIBLE);
                    setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    @Override
    public void onDialogClose() {

    }

    public void setReviewMode(boolean reviewMode) {
        isReviewMode = reviewMode;
    }

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;
    }

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setCustomVideoControlInterface(CustomVideoControlListener customVideoControlListener) {
        this.customVideoControlListener = customVideoControlListener;
    }

    public void setMainCourseCardClass(DTOCourseCard courseCardClass) {
        try {
            int savedCardID = (int) courseCardClass.getCardId();
            cardId = "" + savedCardID;
            this.questionXp = courseCardClass.getXp();
            courseSolutionCard = courseCardClass.getChildCard();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();

            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }
                if (mainCourseCardClass.getXp() == 0) {
                    mainCourseCardClass.setXp(100);
                }
                questions = mainCourseCardClass.getQuestionData();
                if (questions == null) {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.endActivity();
                    }
                }
                if (isCourseQuestion) {
                    if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                        questions.setBgImg(mainCourseCardClass.getBgImg());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        this.courseDataClass = courseDataClass;
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

    public void setCourseCardList(List<DTOCourseCard> courseCardClassList) {
        this.courseCardClassList = courseCardClassList;
    }

    public void setCourseContentHandlingInterface(CourseLearningModuleActivity courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
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

    public void setUserCardData(DTOUserCardData userCardData) {
        try {
            this.userCardData = userCardData;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class MyFileDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isFileDownloadServiceStarted) {
                    if (intent.getAction() != null) {
                        try {
                            if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                                setDownloadingPercentage(Integer.parseInt(intent.getStringExtra("MSG")), "");
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        }
    }

    Handler video_overlay_closechild_handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                Log.d(TAG, "handleMessage: VOCH");
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlayNew");

                if (childFragment != null) {
                    Log.d(TAG, "Handler closeChildFragment: " + childFragment.getTag());
                    transaction.remove(childFragment).commit();
                }

                boolean isPauseAgain = false;
                if (extraAdGroupTimesMs != null) {
                    for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                        if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                            autoPause = i + 1;
                            isPauseAgain = true;
                            Message message = video_overlay_Handler.obtainMessage(1, "Start");
                            message.sendToTarget();
                            break;
                        }
                    }
                }

                if (!isPauseAgain) {
                    framelayout_video_overlay.setVisibility(View.GONE);
                    if (customExoPlayerView != null) {
                        customExoPlayerView.performPlayclick();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void closeChildFragment() {
        Log.d(TAG, "closeChildFragment: " + current_exoplayer_sec + " --- no of questions:" + autoPause);
//        try {
//            ((CourseLearningModuleActivity) OustSdkApplication.getContext()).resetAudioPlayer();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlayNew");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        //learningview_slideanim

        if (childFragment != null) {
            Log.d(TAG, "closeChildFragment: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();
            //transaction.remove(childFragment).commit();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "run: timertask");
                        Message message = video_overlay_closechild_handler.obtainMessage(1, "Start");
                        message.sendToTarget();

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }, 750);
        } else {
            boolean isPauseAgain = false;
            if (extraAdGroupTimesMs != null) {
                for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                    if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                        autoPause = i + 1;
                        isPauseAgain = true;
                        Message message = video_overlay_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                        break;
                    }
                }
            }

            if (!isPauseAgain) {
                framelayout_video_overlay.setVisibility(View.GONE);
                if (customExoPlayerView != null) {
                    customExoPlayerView.performPlayclick();
                }
            }
        }
        if (courseContentHandlingInterface != null) {
            courseContentHandlingInterface.handleScreenTouchEvent(true);
        }
    }

    public void wrongAnswerAndRestartVideo() {
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlayNew");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        if (childFragment != null) {
            Log.d(TAG, "wrongAnswerAndRestartVideo: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();

            long seekTime = 0;
            autoPause--;

            if (autoPause > 0) {
                if (extraAdGroupTimesMs != null) {
                    for (int i = (autoPause - 1); i >= 0; i--) {
                        autoPause--;

                        double sec_ = extraAdGroupTimesMs[i] / 100;
                        if (current_exoplayer_sec == sec_) {

                        } else {
                            if (i == 0) {
                                autoPause = 0;
                                seekTime = 0;
                            } else {
                                seekTime = (extraAdGroupTimesMs[i] / 100) + 1;
                            }
                            break;
                        }
                    }
                }
            } else {
                autoPause = 0;
                current_exoplayer_sec = 0;
            }

            current_exoplayer_sec = seekTime;
            if (customExoPlayerView != null) {
                customExoPlayerView.changeExoplayerSeekTime(seekTime * 100);
            }
            framelayout_video_overlay.setVisibility(View.GONE);
            if (customExoPlayerView != null) {
                customExoPlayerView.performPlayclick();
            }
        }
        if (courseContentHandlingInterface != null) {
            courseContentHandlingInterface.handleScreenTouchEvent(true);
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
                    OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
                    new Handler().postDelayed(() -> {
                        isDownloadButtonclicked = false;
                        Drawable drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                        if (drawable != null) {
                            downloadvideo_icon.setImageDrawable(drawable);
                        }
                    }, 3000);
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkAndRestartVideo() {
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlayNew");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        if (childFragment != null) {
            Log.d(TAG, "checkAndRestartVideo: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();

            long seekTime = 0;
            autoPause--;
            if (autoPause > 0) {
                if (extraAdGroupTimesMs != null) {
                    int i = autoPause - 1;

                    double sec_ = extraAdGroupTimesMs[i] / 100;
                    if (current_exoplayer_sec == sec_) {
                        //seekTime = current_exoplayer_sec;
                        /*if(autoPause==0){
                            autoPause = 0;
                            seekTime = 0;
                        }else{
                            seekTime = sec;
                        }*/

                        /*if(i==0){
                            autoPause=0;
                            current_exoplayer_sec = 0;
                            if(customExoPlayerView!=null){
                                customExoPlayerView.changeExoplayerSeekTime(0);
                            }
                            framelayout_video_overlay.setVisibility(View.GONE);
                            if (customExoPlayerView != null) {
                                customExoPlayerView.performPlayclick();
                            }
                        }else{*/
                        Message message = video_overlay_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                        //}

                        //seekTime = current_exoplayer_sec;
                    } else {
                        autoPause--;
                        seekTime = extraAdGroupTimesMs[i] / 100 + 1;
                        current_exoplayer_sec = seekTime;
                        if (customExoPlayerView != null) {
                            customExoPlayerView.changeExoplayerSeekTime(seekTime * 100);
                        }
                        framelayout_video_overlay.setVisibility(View.GONE);
                        if (customExoPlayerView != null) {
                            customExoPlayerView.performPlayclick();
                        }
                    }

                    /*for (int i = (autoPause-1); i>=0; i--) {
                        autoPause--;
                        int sec = (int)extraAdGroupTimesMs[i] / 1000;
                        if(current_exoplayer_sec == sec){
                            if(i==0){
                                autoPause = 0;
                                break;
                            }
                        }else{
                            seekTime = sec+1;
                            break;
                        }
                    }*/
                }
            } else {
                autoPause = 0;
                current_exoplayer_sec = 0;
                if (customExoPlayerView != null) {
                    customExoPlayerView.changeExoplayerSeekTime(0);
                }
                framelayout_video_overlay.setVisibility(View.GONE);
                if (customExoPlayerView != null) {
                    customExoPlayerView.performPlayclick();
                }
            }

        }
    }

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if (getActivity() != null) {
            getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
        }
        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        try {
//            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                if (!isFullScreen) {
//                    customVideoControlListener.hideToolbar();
//                    question_card.setVisibility(View.GONE);
//                    setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
//                } else {
//                    customVideoControlListener.showToolbar();
//                    question_card.setVisibility(View.VISIBLE);
//                    setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
//                }
//            }
//            if (tochedScreen) {
//                super.onConfigurationChanged(newConfig);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
    //OustSdkTools.sendSentryException(e);
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        setReceiver();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
            }
            try {

                if (customExoPlayerView != null) {
//                    customExoPlayerView.removeVideoPlayer();
                    customExoPlayerView.performPauseclick();
                    customExoPlayerView = null;
                }
                OustPreferences.clear("VideoOverlayCardNumberOfAnswered");
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

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
    public void onPause() {
        super.onPause();
        try {
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
            }
            if (video_overlay_HandlerTimer != null) {
                video_overlay_HandlerTimer.removeCallbacks(runnableTimer);
            }
            if (!isVideoCompleted) {
                try {
                    Log.d(TAG, "OnPause3");
                    requireActivity().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, "OnPause Error");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OustPreferences.clear("VideoOverlayCardNumberOfAnswered");
    }

    private void removeAllData() {
        try {
            Log.e("SPEECH", "removeAllData() Called");

            if (OustSdkTools.textToSpeech != null) {
                Log.e("SPEECH", "removeAllData() OustSdkTools.textToSpeech != null");
                OustSdkTools.stopSpeech();
            }

//            cancelSound();
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler = null;
            }
            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            Log.e("SPEECH", "removeAllData() Exception occured", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAllData() {
        try {
            myHandler = null;
            courseDataClass = null;
            mainCourseCardClass = null;
            questions = null;
            questionType = null;
            learningModuleInterface = null;

            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public int calculateVideoProgress() {
        try {
            Log.d(TAG, "calculateVideoProgress: ");
            if (customExoPlayerView != null && customExoPlayerView.getSimpleExoPlayerView().getPlayer() != null) {
                if (isVideoCompleted) {
                    return 100;
                }
                time = customExoPlayerView.getSimpleExoPlayerView().getPlayer().getCurrentPosition();
                long total_time = customExoPlayerView.getSimpleExoPlayerView().getPlayer().getDuration();
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
            time = time / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return (time > 0 ? (time - 1) : 0);
        /*return ((time>0)?time:(timeRemaining>0)?timeRemaining:0);*/
    }
}
