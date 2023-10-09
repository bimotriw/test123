package com.oustme.oustsdk.survey_ui;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.AssessmentCopyResponse;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.ScoresCopy;
import com.oustme.oustsdk.data.handlers.impl.MediaDataDownloader;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.LearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.ModuleOverViewFragment;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.QuestionResponce;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.Charsets;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SurveyDetailActivity extends AppCompatActivity implements LearningModuleInterface {


    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    CardView card_view_survey;
    ImageView survey_image;
    TextView survey_feed_date;
    TextView survey_title_full;
    TextView survey_feed_dead_line;
    LinearLayout progress_lay;
    TextView completion_percentage_done;
    ProgressBar completion_percentage_progress;
    LinearLayout survey_info;
    LinearLayout survey_duration_lay;
    TextView survey_duration_text;
    LinearLayout survey_qa_lay;
    TextView survey_qa_text;
    LinearLayout survey_coins_lay;
    TextView survey_coins_text;
    TextView survey_description;
    WebView survey_description_webView;
    FrameLayout survey_action_button;
    TextView survey_status_text;
    ProgressBar survey_loader;
    FrameLayout intro_card_layout;
    FrameLayout result_card_layout;
    RelativeLayout card_layout;
    RelativeLayout bottom_swipe;
    ImageView next_question;
    ImageView previous_question;
    RelativeLayout survey_download_loader;
    ProgressBar survey_download_progressbar;
    ImageView img_coin;

    //branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    //data intent
    String surveyIdString;
    String surveyTitle;
    Long feedID, surveyId, courseId, cplId, associatedAssessmentId;
    boolean reviewsurvey_btn = false;
    private boolean assessmentRunning = false;
    private boolean isRecreate = false;
    private String startDateTime;
    private boolean isSurveyExit;
    private String exitMessage = "";
    private long rewardOC, exitOC;
    private int downloadQuestionNo = 0;
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int questionsCount = 0;
    private int noofTry = 0;
    private int mediaDownloadCount = 0;
    String surveyBgImage;
    private boolean showNavigateArrow;
    private boolean showPastDeadlineModulesOnLandingPage;
    private long defaultPastDeadlineCoinsPenaltyPercentage;
    private long deadlineTime;
    private boolean isActivityDestroyed = false;
    private long challengerFinalScore = 0;
    private int questionIndex = 0;
    private int reviewCardNo = 0;
    private boolean isMultipleCplEnable = false;

    //EntityNewFeed feed;
    DTOUserFeedDetails.FeedDetails feed;
    boolean updateComment;
    //private FeedClickListener feedClickListener;
    boolean isFeedComment, isFeedLikeable;
    private boolean isFeedChange, isLikeClicked;

    ActiveUser activeUser;
    ActiveGame activeGame;
    private DTOQuestions questions;
    private AssessmentPlayResponse assessmentPlayResponse;
    private PlayResponse playResponse;
    private List<Scores> scoresList;
    List<String> mediaList = new ArrayList<>();
    private DTOCourseCard introCard;
    private DTOCourseCard resultCard;
    private DTOCourseCard courseCardClass1;
    private boolean isIntroCard1;
    private DownloadFiles downloadFiles;
    private final String TAG = "SurveyDetailActivity";
    private int mediaSize = 0;
    private boolean allQuestionsAttempted = false;
    private boolean isReviewModeRunning = false;
    private boolean disableBackButton = false;
    private boolean isReviewPopUp = false;

    //touch listner
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 30;
    private boolean touchedOnce = false;

    //dialog
    Dialog reviewdialog;

    //receiver
    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private int color;
    private int bgColor;

    Bundle dataBundle;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        OustSdkTools.setLocale(SurveyDetailActivity.this);
        setContentView(R.layout.activity_survey_detail);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        OustGATools.getInstance().reportPageViewToGoogle(SurveyDetailActivity.this, "Survey Landing Page");

        try {
            getColors();
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            screen_name.setText(getResources().getString(R.string.survey_text).toUpperCase());
            setSupportActionBar(toolbar);

            card_view_survey = findViewById(R.id.card_view_survey);
            survey_image = findViewById(R.id.survey_image);
            survey_feed_date = findViewById(R.id.survey_feed_date);
            survey_title_full = findViewById(R.id.survey_title_full);
            survey_feed_dead_line = findViewById(R.id.survey_feed_dead_line);
            progress_lay = findViewById(R.id.progress_lay);
            completion_percentage_done = findViewById(R.id.completion_percentage_done);
            completion_percentage_progress = findViewById(R.id.completion_percentage_progress);
            survey_info = findViewById(R.id.survey_info);
            survey_duration_lay = findViewById(R.id.survey_duration_lay);
            survey_duration_text = findViewById(R.id.survey_duration_text);
            survey_qa_lay = findViewById(R.id.survey_qa_lay);
            survey_qa_text = findViewById(R.id.survey_qa_text);
            survey_coins_lay = findViewById(R.id.survey_coins_lay);
            survey_coins_text = findViewById(R.id.survey_coins_text);
            survey_description = findViewById(R.id.survey_description);
            survey_description_webView = findViewById(R.id.survey_description_webView);
            survey_action_button = findViewById(R.id.survey_action_button);
            survey_status_text = findViewById(R.id.survey_status_text);
            survey_loader = findViewById(R.id.survey_loader);
            intro_card_layout = findViewById(R.id.intro_card_layout);
            result_card_layout = findViewById(R.id.result_card_layout);
            card_layout = findViewById(R.id.card_layout);
            bottom_swipe = findViewById(R.id.bottom_swipe);
            next_question = findViewById(R.id.next_question);
            previous_question = findViewById(R.id.previous_question);
            survey_download_loader = findViewById(R.id.survey_download_loader);
            survey_download_progressbar = findViewById(R.id.survey_download_progressbar);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End

            try {
                img_coin = findViewById(R.id.img_coin);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    img_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                } else {
                    img_coin.setImageResource(R.drawable.ic_coins_golden);
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

            dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                surveyIdString = dataBundle.getString("assessmentId");
                surveyTitle = dataBundle.getString("surveyTitle");
                timeStamp = dataBundle.getLong("timeStamp", 0);
                feedID = dataBundle.getLong("FeedID", 0);
                courseId = dataBundle.getLong("courseId", 0);
                feed = dataBundle.getParcelable("Feed");
                isFeedComment = dataBundle.getBoolean("FeedComment");
                isFeedLikeable = dataBundle.getBoolean("isFeedLikeable");
                cplId = dataBundle.getLong("cplId", 0);
                associatedAssessmentId = dataBundle.getLong("associatedAssessmentId", 0);

//                isCplId = dataBundle.getLong("cplId");
                isMultipleCplEnable = dataBundle.getBoolean("isMultipleCplModule", false);

//            isMultipleCplEnable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);
            }

            setIconColors();
            initData();

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

    @Override
    protected void onStart() {
        super.onStart();
        setReceiver();
    }

    private void setIconColors() {

        Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        survey_action_button.setBackground(OustResourceUtils.setDefaultDrawableColor(actionDrawable));

    }

    private void initData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();
            activeGame = new ActiveGame();


            if (feed != null) {

                String feedDate = OustSdkTools.milliToDate("" + timeStamp);
                if (!feedDate.isEmpty()) {
                    survey_feed_date.setVisibility(View.VISIBLE);
                    survey_feed_date.setText(feedDate);

                }


                if (isFeedComment) {
                    feedComment(feed);
                }
            }

            if (activeUser != null) {

                if ((surveyIdString != null) && (!surveyIdString.isEmpty())) {

                    surveyId = Long.parseLong(surveyIdString);

                    showLoader();
                    getSurveyCardsFromFirebase();

                }

            } else {
                SurveyDetailActivity.this.finish();
            }


            previous_question.setOnClickListener(v -> gotoPreviousScreen());

            next_question.setOnClickListener(v -> gotoNextScreen());

            back_button.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForSavedSurvey(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + surveyId;
            if (isMultipleCplEnable && cplId != 0) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + cplId + "/contentListMap/assessment" + surveyId;
            } else {
                if (courseId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
                } else if (associatedAssessmentId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/assessment" + associatedAssessmentId + "/surveyAssessment" + surveyId;
                }
            }
            Log.d(TAG, "checkforSavedSurvey: node->" + node);

            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        int totalQues = 0;
                        assessmentPlayResponse = new AssessmentPlayResponse();
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
                        if ((currentState != null) && (currentState.equals(AssessmentState.SUBMITTED))) {
                            if (associatedAssessmentId > 0) {
                                OustStaticVariableHandling.getInstance().setSurveyCompleted(true);
                            }
                            showPopup(getResources().getString(R.string.completed_survey_text), false);
                        } else {
                            survey_action_button.setVisibility(View.VISIBLE);
                            if (assessmentprogressMainMap != null) {
                                int qstnIndex = 0;
                                if (assessmentprogressMainMap.get("challengerFinalScore") != null) {
                                    if (Objects.requireNonNull(assessmentprogressMainMap.get("challengerFinalScore")).getClass() == Long.class) {
                                        assessmentPlayResponse.setChallengerFinalScore(((long) assessmentprogressMainMap.get("challengerFinalScore")));
                                    } else
                                        assessmentPlayResponse.setChallengerFinalScore(Long.parseLong((String) Objects.requireNonNull(assessmentprogressMainMap.get("challengerFinalScore"))));
                                }
                                if (assessmentprogressMainMap.get("questionIndex") != null) {
                                    int n1 = 0;
                                    if (Objects.requireNonNull(assessmentprogressMainMap.get("questionIndex")).getClass() == Long.class) {
                                        n1 = (int) ((long) assessmentprogressMainMap.get("questionIndex"));
                                    } else
                                        n1 = Integer.parseInt((String) Objects.requireNonNull(assessmentprogressMainMap.get("questionIndex")));
                                    assessmentPlayResponse.setQuestionIndex(n1);
                                    qstnIndex = n1;
                                }
                                if (assessmentprogressMainMap.get("gameId") != null) {
                                    assessmentPlayResponse.setGameId((String) assessmentprogressMainMap.get("gameId"));
                                }
                                if (assessmentprogressMainMap.get("studentId") != null) {
                                    assessmentPlayResponse.setStudentId((String) assessmentprogressMainMap.get("studentId"));
                                }
                                if (assessmentprogressMainMap.get("totalQuestion") != null) {
                                    if (Objects.requireNonNull(assessmentprogressMainMap.get("totalQuestion")).getClass() == Long.class) {
                                        totalQues = (int) ((long) assessmentprogressMainMap.get("totalQuestion"));
                                    } else
                                        totalQues = Integer.parseInt((String) Objects.requireNonNull(assessmentprogressMainMap.get("totalQuestion")));
                                    assessmentPlayResponse.setTotalQuestion(totalQues);
                                    setResumeProgress(qstnIndex, totalQues);
                                }
                                if (assessmentprogressMainMap.get("winner") != null) {
                                    assessmentPlayResponse.setWinner((String) assessmentprogressMainMap.get("winner"));
                                }
                                if (assessmentprogressMainMap.get("endTime") != null) {
                                    assessmentPlayResponse.setEndTime((String) assessmentprogressMainMap.get("endTime"));
                                }
                                if (assessmentprogressMainMap.get("startTime") != null) {
                                    assessmentPlayResponse.setStartTime((String) assessmentprogressMainMap.get("startTime"));
                                }
                                if (assessmentprogressMainMap.get("challengerid") != null) {
                                    assessmentPlayResponse.setChallengerid((String) assessmentprogressMainMap.get("challengerid"));
                                }
                                if (assessmentprogressMainMap.get("opponentid") != null) {
                                    assessmentPlayResponse.setOpponentid((String) assessmentprogressMainMap.get("opponentid"));
                                }
                            }
                            if (totalQues > 0) {
                                if (assessmentprogressMainMap.get("scoresList") != null) {
                                    List<Scores> scores = new ArrayList<>();
                                    List<Object> assessmentprogressScoreList = (List<Object>) assessmentprogressMainMap.get("scoresList");
                                    assert assessmentprogressScoreList != null;
                                    for (int i = 0; i < assessmentprogressScoreList.size(); i++) {
                                        final Map<String, Object> assessmentScoreMap = (Map<String, Object>) assessmentprogressScoreList.get(i);
                                        Scores scores1 = new Scores();
                                        if ((assessmentScoreMap.get("answer") != null)) {
                                            String answer1 = (String) assessmentScoreMap.get("answer");
                                            answer1 = getUtfStr(answer1);
                                            answer1 = answer1.replaceAll("\\n", "");
                                            answer1 = answer1.replaceAll("\\r", "");
                                            if (!answer1.isEmpty()) {
                                                scores1.setAnswer(answer1);
                                            }
                                        }
                                        if ((assessmentScoreMap.get("question") != null)) {
                                            if (Objects.requireNonNull(assessmentScoreMap.get("question")).getClass() == Long.class) {
                                                scores1.setQuestion((int) ((long) assessmentScoreMap.get("question")));
                                            } else
                                                scores1.setQuestion(Integer.parseInt((String) Objects.requireNonNull(assessmentScoreMap.get("question"))));
                                        }
                                        if ((assessmentScoreMap.get("questionSerialNo") != null)) {
                                            if (Objects.requireNonNull(assessmentScoreMap.get("questionSerialNo")).getClass() == Long.class) {
                                                scores1.setQuestionSerialNo((int) ((long) assessmentScoreMap.get("questionSerialNo")));
                                            } else
                                                scores1.setQuestionSerialNo(Integer.parseInt((String) Objects.requireNonNull(assessmentScoreMap.get("questionSerialNo"))));

                                        }
                                        if ((assessmentScoreMap.get("questionType") != null)) {
                                            scores1.setQuestionType((String) assessmentScoreMap.get("questionType"));
                                        }
                                        if ((assessmentScoreMap.get("score") != null)) {
                                            if (Objects.requireNonNull(assessmentScoreMap.get("score")).getClass() == Long.class) {
                                                scores1.setScore(((long) assessmentScoreMap.get("score")));
                                            } else
                                                scores1.setScore(Long.parseLong((String) Objects.requireNonNull(assessmentScoreMap.get("score"))));
                                        }
                                        if ((assessmentScoreMap.get("time") != null)) {
                                            scores1.setTime(Long.parseLong((String) Objects.requireNonNull(assessmentScoreMap.get("time"))));
                                        }
                                        if ((assessmentScoreMap.get("correct") != null)) {
                                            scores1.setCorrect((boolean) assessmentScoreMap.get("correct"));
                                        }
                                        if ((assessmentScoreMap.get("xp") != null)) {
                                            if (Objects.requireNonNull(assessmentScoreMap.get("xp")).getClass() == Long.class) {
                                                scores1.setXp((int) ((long) assessmentScoreMap.get("xp")));
                                            } else
                                                scores1.setXp(Integer.parseInt((String) Objects.requireNonNull(assessmentScoreMap.get("xp"))));
                                        }
                                        if ((assessmentScoreMap.get("userSubjectiveAns") != null)) {
                                            scores1.setUserSubjectiveAns(((String) assessmentScoreMap.get("userSubjectiveAns")));
                                        }
                                        scores.add(scores1);
                                    }
                                    assessmentPlayResponse.setScoresList(scores);
                                }
                            }
                            assessmentPlayResponse.setTotalQuestion(totalQues);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    setBtnStatus();
                    btnAction();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    survey_action_button.setVisibility(View.VISIBLE);
                    setBtnStatus();
                    btnAction();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            survey_action_button.setVisibility(View.VISIBLE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public String getUtfStr(String s2) {
        String s1 = "";
        try {
            CharsetDecoder decoder = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                decoder = StandardCharsets.UTF_8.newDecoder();
            } else {
                decoder = Charsets.UTF_8.newDecoder();
            }
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            byte[] b = s2.getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            CharBuffer parsed = decoder.decode(bb);
            s1 = "" + parsed;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return s1;
    }

    private void setBtnStatus() {
        try {
            if (assessmentPlayResponse != null) {
                if (assessmentPlayResponse.getAssessmentState() != null &&
                        assessmentPlayResponse.getGameId() != null &&
                        !assessmentPlayResponse.getGameId().isEmpty() && assessmentPlayResponse.getScoresList() != null) {
                    activeGame.setGameid(assessmentPlayResponse.getGameId());
                }
                survey_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
            } else {
                survey_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void btnAction() {
        survey_action_button.setOnClickListener(view -> {
            checkAssessmentState();
        });
    }

    public void calculateFinalScore() {
        SubmitRequest submitRequest;
        assessmentRunning = false;
        try {
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            submitRequest = new SubmitRequest();
            submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
            submitRequest.setTotalscore(0);
            submitRequest.setScores(scoresList);
            submitRequest.setEndTime(TimeUtils.getCurrentDateAsString());
            submitRequest.setStartTime(startDateTime);
            submitRequest.setExternal(false);
            submitRequest.setChallengerid(activeUser.getStudentid());
            submitRequest.setGroupId("");
            submitRequest.setOpponentid("");

            if (isSurveyExit) {
                if (exitOC > 0) {
                    submitRequest.setExitOC(exitOC);
                }
            } else {
                if (rewardOC > 0) {
                    if (showPastDeadlineModulesOnLandingPage && defaultPastDeadlineCoinsPenaltyPercentage != 0) {
                        double penaltyCoins = ((defaultPastDeadlineCoinsPenaltyPercentage * 1.0) / 100) * rewardOC;
                        rewardOC = rewardOC - (int) penaltyCoins;
                    }
                    submitRequest.setRewardOC(rewardOC);
                }
            }

            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitRequest.setDeviceToken(gcmToken);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            if (courseId != 0) {
                submitRequest.setCourseId(String.valueOf(courseId));
            }
            submitRequest.setAssessmentId(("" + surveyId));
            //UI Change
            card_layout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        /*
            downloadscreen_layout.setVisibility(View.VISIBLE);
            submitresponse_text.setVisibility(View.VISIBLE);*/
            submitScore(submitRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkAssessmentState() {
        try {
            if (assessmentPlayResponse != null) {
                if (assessmentPlayResponse.getAssessmentState() != null &&
                        assessmentPlayResponse.getGameId() != null &&
                        !assessmentPlayResponse.getGameId().isEmpty() && assessmentPlayResponse.getScoresList() != null) {
                    activeGame.setGameid(assessmentPlayResponse.getGameId());
                    showFetchingLoader();
                    getPlayresponse(assessmentPlayResponse.getGameId());
                } else if (activeGame.getGameid() != null && !activeGame.getGameid().isEmpty()) {
                    showFetchingLoader();
                    getPlayresponse(activeGame.getGameid());
                } else {
                    startSurvey();
                }
            } else {
                startSurvey();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getPlayresponse(String gameId) {
        try {
            mediaList = new ArrayList<>();
            String playGameUrl = getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(gameId);
            assmntGamePlayRequest.setStudentid(activeUser.getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            assmntGamePlayRequest.setSurveyid(surveyId);
            assmntGamePlayRequest.setCourseId(courseId);
            String assessmentID = "" + surveyId;
            if (!assessmentID.isEmpty())
                assmntGamePlayRequest.setAssessmentId(assessmentID);
            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponse();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotPlayResponse() {
        try {
            if (playResponse != null) {
                if (playResponse.isSuccess()) {
                    if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                        downloadQuestionNo = 0;
                        incrementDownloadQuestionNO = 0;
                        totalQuestion = 0;
                        noofTry = 0;
                        totalQuestion = playResponse.getqIdList().size();
                        startDownloadingQuestions();
                    } else {
                        SurveyDetailActivity.this.finish();
                        OustSdkTools.showToast(getResources().getString(R.string.unable_fetch_connection_error));
                    }
                } else {
                    OustSdkTools.handlePopup(playResponse);
                    SurveyDetailActivity.this.finish();
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.unable_fetch_connection_error));
                SurveyDetailActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startSurvey() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setAssessmentId(("" + surveyId));
        createGameRequest.setChallengerid(activeUser.getStudentid());
        createGameRequest.setGuestUser(false);
        createGameRequest.setRematch(false);
        String laungeStr = Locale.getDefault().getLanguage();
        if ((laungeStr != null)) {
            createGameRequest.setAssessmentLanguage(laungeStr);
        }
        showFetchingLoader();
        activeGame = new ActiveGame();
        createGame(createGameRequest);
    }

    public void createGame(CreateGameRequest createGameRequest) {
        String createGameUrl = getResources().getString(R.string.create_game);
        createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(createGameRequest);
        Log.d(TAG, "createGame: " + jsonParams);

        ApiCallUtils.doNetworkCall(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                gotCreateGameResponse(createGameResponse);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }

    public void gotCreateGameResponse(CreateGameResponse createGameResponse) {
        if (createGameResponse != null) {
            if (createGameResponse.isSuccess()) {
                activeGame.setGameid("" + createGameResponse.getGameid());
                getPlayresponse(activeGame.getGameid());
            } else {
                OustSdkTools.handlePopup(createGameResponse);
                SurveyDetailActivity.this.finish();
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            SurveyDetailActivity.this.finish();
        }
    }

    public void submitScore(SubmitRequest submitRequest) {
        try {
            String submitGameUrl = getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);

            if (cplId > 0) {
                submitRequest.setContentPlayListId(cplId);
            } else if (associatedAssessmentId > 0) {
                submitRequest.setAssociatedToAssessmentId(associatedAssessmentId);
            }

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            Log.d(TAG, "submitScore: json:" + jsonParams);
            ApiCallUtils.doNetworkCall(Request.Method.POST, submitGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                    submitRequestProcessFinish(submitResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        assessmentRunning = false;
        if (submitResponse != null) {
            if (submitResponse.isSuccess()) {

                /*try {
                    saveFeedSurveyCompleted(activeUser);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }*/

                if (associatedAssessmentId > 0) {
                    OustStaticVariableHandling.getInstance().setSurveyCompleted(true);
                }

                if (isSurveyExit) {
                    showPopup(exitMessage, true);
                } else {
                    showPopup(getResources().getString(R.string.completing_survey_text), false);
                }
            } else {
                OustSdkTools.handlePopup(submitResponse);
                SurveyDetailActivity.this.finish();
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            SurveyDetailActivity.this.finish();
        }
    }

    private void showLoader() {
        survey_loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        survey_loader.setVisibility(View.GONE);
    }

    private void getSurveyCardsFromFirebase() {
        try {
            String node = ("assessment/assessment" + surveyId);
            Log.d(TAG, "getSurveyCardsFromFirebase: " + node);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (assessmentprogressMainMap != null) {
                                if (assessmentprogressMainMap.get("descriptionCard") != null) {
                                    Map<String, Object> introCardInfo = (Map<String, Object>) assessmentprogressMainMap.get("descriptionCard");
                                    introCard = commonTools.getCardFromMap(introCardInfo);
                                }
                                if (assessmentprogressMainMap.get("resultCardInfo") != null) {
                                    Map<String, Object> resultCardInfo = (Map<String, Object>) assessmentprogressMainMap.get("resultCardInfo");
                                    resultCard = commonTools.getCardFromMap(resultCardInfo);
                                }

                                if (assessmentprogressMainMap.get("bgImg") != null) {
                                    surveyBgImage = ((String) assessmentprogressMainMap.get("bgImg"));
                                }

                                if (assessmentprogressMainMap.get("banner") != null) {
                                    String url = (String) assessmentprogressMainMap.get("banner");
                                    if (url != null && !url.trim().isEmpty()) {
                                        //handle banner image for survey
                                        Picasso.get().load(url).into(survey_image);

                                    }
                                }

                                if (assessmentprogressMainMap.get("name") != null) {
                                    String title = (String) assessmentprogressMainMap.get("name");
                                    if (title != null && !title.trim().isEmpty()) {
                                        //handle survey name
                                        survey_title_full.setText(title);
                                    }
                                }

                                if (assessmentprogressMainMap.get("numQuestions") != null) {
                                    long questionCount = OustSdkTools.convertToLong(assessmentprogressMainMap.get("numQuestions"));
                                    if (questionCount != 0) {
                                        questionsCount = (int) questionCount;
                                        survey_qa_lay.setVisibility(View.VISIBLE);
                                        String qa_text = questionCount + " " + getResources().getString(R.string.question_text).toLowerCase();
                                        if (questionCount > 1) {
                                            qa_text = questionCount + " " + getResources().getString(R.string.questions_text).toLowerCase();
                                        }
                                        survey_qa_text.setText(qa_text);
                                    }
                                }

                                if (assessmentprogressMainMap.get("contentDuration") != null) {
                                    long contentDuration = (long) assessmentprogressMainMap.get("contentDuration");
                                    if (contentDuration > 60) {
                                        String totalTime = TimeUnit.SECONDS.toMinutes(contentDuration) + " minutes";
                                        survey_duration_text.setText(totalTime);

                                    } else {
                                        survey_duration_text.setText("1 " + OustSdkApplication.getContext().getResources().getString(R.string.minute));
                                    }
                                } else {
                                    survey_duration_text.setText("1 " + OustSdkApplication.getContext().getResources().getString(R.string.minute));
                                }

                                if (assessmentprogressMainMap.get("description") != null) {
                                    String desc = (String) assessmentprogressMainMap.get("description");
                                    if (desc != null && !desc.trim().isEmpty()) {
                                        //handle survey desc
                                       /* Linkify.addLinks(survey_description, Linkify.ALL);
                                        CharSequence descSequence = Html.fromHtml(desc);
                                        SpannableStringBuilder descBuilder = new SpannableStringBuilder(descSequence);
                                        URLSpan[] urls = descBuilder.getSpans(0, descSequence.length(), URLSpan.class);
                                        for (URLSpan span : urls) {
                                            makeLinkClickable(SurveyDetailActivity.this, descBuilder, span, "Survey");
                                        }
                                        survey_description.setText(descBuilder);
                                        survey_description.setMovementMethod(LinkMovementMethod.getInstance());*/
                                        if (desc.contains("<li>") || desc.contains("</li>") ||
                                                desc.contains("<ol>") || desc.contains("</ol>") ||
                                                desc.contains("<p>") || desc.contains("</p>")) {
                                            survey_description_webView.setVisibility(View.VISIBLE);
                                            survey_description.setVisibility(View.GONE);
                                            survey_description_webView.setBackgroundColor(Color.TRANSPARENT);
                                            String text = OustSdkTools.getDescriptionHtmlFormat(desc);
                                            final WebSettings webSettings = survey_description_webView.getSettings();
                                            // Set the font size (in sp).
                                            webSettings.setDefaultFontSize(18);
                                            survey_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                                        } else {
                                            survey_description.setVisibility(View.VISIBLE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                survey_description.setText(Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT));
                                            } else {
                                                survey_description.setText(Html.fromHtml(desc));
                                            }
                                        }
                                    }
                                }

                                if (assessmentprogressMainMap.get("rewardOC") != null) {
                                    rewardOC = OustSdkTools.convertToLong(assessmentprogressMainMap.get("rewardOC"));
                                    if (rewardOC != 0) {
                                        survey_coins_lay.setVisibility(View.VISIBLE);
                                        String coins_text = rewardOC + " " + getResources().getString(R.string.coins_text).toLowerCase();
                                        survey_coins_text.setText(coins_text);
                                    } else {
                                        survey_coins_lay.setVisibility(View.GONE);
                                    }
                                } else {
                                    survey_coins_lay.setVisibility(View.GONE);
                                }

                                if (assessmentprogressMainMap.get("completionDeadline") != null) {
                                    String deadLine = (String) assessmentprogressMainMap.get("completionDeadline");
                                    if (deadLine != null && !deadLine.isEmpty()) {
                                        try {
                                            Date date = new Date(Long.parseLong(deadLine));
                                            deadlineTime = date.getTime();
                                            String feedDeadLine = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);

                                            if (!feedDeadLine.isEmpty()) {
                                                survey_feed_dead_line.setVisibility(View.VISIBLE);
                                                String deadLineText = getResources().getString(R.string.deadline);
                                                feedDeadLine = deadLineText + " " + feedDeadLine.toUpperCase();
                                                Spannable spanText = new SpannableString(feedDeadLine);
                                                spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                survey_feed_dead_line.setText(spanText);

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }
                                    }

                                }
                                if (assessmentprogressMainMap.get("exitOC") != null) {
                                    exitOC = OustSdkTools.convertToLong(assessmentprogressMainMap.get("exitOC"));
                                }

                                if (assessmentprogressMainMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                                    defaultPastDeadlineCoinsPenaltyPercentage = OustSdkTools.convertToLong(assessmentprogressMainMap.get("defaultPastDeadlineCoinsPenaltyPercentage"));
                                }

                                if (assessmentprogressMainMap.get("showNavigateArrow") != null) {
                                    showNavigateArrow = (boolean) assessmentprogressMainMap.get("showNavigateArrow");
                                }

                                if (assessmentprogressMainMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                                    showPastDeadlineModulesOnLandingPage = (boolean) assessmentprogressMainMap.get("showPastDeadlineModulesOnLandingPage");
                                }

                                if (!showPastDeadlineModulesOnLandingPage && deadlineTime != 0 && deadlineTime < System.currentTimeMillis()) {
                                    Toast.makeText(SurveyDetailActivity.this, "" + getResources().getString(R.string.course_not_available_text), Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                                if (introCard != null) {
                                    openIntroCard(introCard, true);
                                } else {
                                    hideLoader();
                                }
                                checkForSavedSurvey(activeUser);
                            } else {
                                hideFetchingLoader();
                            }
                        } else {
                            hideFetchingLoader();
                            OustSdkTools.showToast("No Data available");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: exception");
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hideFetchingLoader();
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showFetchingLoader() {
        Log.d(TAG, "showFetchingLoader: ");
        try {
            survey_action_button.setClickable(false);
            back_button.setClickable(false);
            branding_mani_layout.setVisibility(View.VISIBLE);
            branding_percentage.setVisibility(View.VISIBLE);
            branding_percentage.setText("0%");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideFetchingLoader() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
            survey_action_button.setClickable(true);
            back_button.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startDownloadingQuestions() {
        try {
            survey_download_progressbar.setMax(totalQuestion);
            incrementDownloadQuestionNO += 10;
            if (incrementDownloadQuestionNO > totalQuestion) {
                incrementDownloadQuestionNO = totalQuestion;
            }
            //download_progressbar.setMax(totalQuestion);
            for (int i = downloadQuestionNo; i < incrementDownloadQuestionNO; i++) {
                if (OustSdkTools.checkInternetStatus()) {
                    getQuestionById(playResponse.getqIdList().get(i));
                } else {
                    DTOQuestions dtoQuestions = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));
                    if ((dtoQuestions != null) && (dtoQuestions.getQuestionId() > 0)) {
                        OustMediaTools.prepareMediaList(mediaList, dtoQuestions);
                        updateCompletePrecentage();
                    } else {
                        getQuestionById(playResponse.getqIdList().get(i));
                    }

                }
            }
            if (totalQuestion == 0) {
                updateCompletePrecentage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getQuestionById(final int qID) {
        try {
            //String getQuestionUrl = getResources().getString(R.string.getquestion_url);
            String getQuestionUrl = getResources().getString(R.string.getQuestionUrl_V2);
            getQuestionUrl = getQuestionUrl.replace("{QId}", ("" + qID));
            getQuestionUrl = HttpManager.getAbsoluteUrl(getQuestionUrl);
            JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();
            Log.d(TAG, "getQuestionById:V2: " + getQuestionUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getQuestionUrl, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    DTOQuestions questions = getQuestion(response.toString());
                    try {
                        if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                            questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                            questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                            questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                            questions.setQuestionType(QuestionType.LONG_ANSWER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    checkForDownloadComplete(questions, qID);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOQuestions getQuestion(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            try {
                Gson gson = new GsonBuilder().create();
                QuestionResponce questionResponce = gson.fromJson(jsonString, QuestionResponce.class);
                surveyBgImage = questionResponce.getQuestions().get(0).getBgImg();
                return OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void checkForDownloadComplete(DTOQuestions questions, int qId) {
        if (questions != null) {
            OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
            updateCompletePrecentage();
        } else {
            noofTry++;
            if (noofTry < 4) {
                getQuestionById(qId);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        }
    }

    public void updateCompletePrecentage() {
        downloadQuestionNo++;
        String downloadText;
        survey_download_progressbar.setProgress(downloadQuestionNo);
        if (incrementDownloadQuestionNO == downloadQuestionNo) {
            if (incrementDownloadQuestionNO == totalQuestion) {
                downloadText = 100 + "%";
                branding_percentage.setVisibility(View.VISIBLE);
                branding_percentage.setText(downloadText);
                for (int i = 0; i < incrementDownloadQuestionNO; i++) {
                    DTOQuestions dtoQuestions = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));
                    if ((dtoQuestions != null) && (dtoQuestions.getQuestionId() > 0)) {
                        OustMediaTools.prepareMediaList(mediaList, dtoQuestions);
                    }
                }
                downloadMediaFiles();

            } else {
                startDownloadingQuestions();
            }
        } else {
            float percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 100;
            if (percentage < 100) {
                downloadText = ((int) percentage) + "%";
            } else {
                downloadText = 100 + "%";
            }
            branding_percentage.setVisibility(View.VISIBLE);
            branding_percentage.setText(downloadText);
        }
    }

    private void downloadMediaFiles() {
        File file;
        String path;
        int i = 0;
        String downloadText;
        if (mediaList != null && mediaList.size() > 0) {
            survey_download_progressbar.invalidate();
            survey_download_progressbar.setMax(mediaList.size());
            for (i = 0; i < mediaList.size(); i++) {
                if (mediaList.get(i).equalsIgnoreCase("")) {
                    mediaList.remove(i);
                }
            }
            for (i = 0; i < mediaList.size(); i++) {
                path = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(mediaList.get(i));
                file = new File(path);
                if (!file.exists()) {
                    downloadMedia(mediaList.get(i));
                } else {
                    mediaDownloadCount++;
                    survey_download_progressbar.setProgress(mediaDownloadCount);
                    if (mediaDownloadCount == mediaList.size()) {
                        downloadText = 100 + "%";
                        branding_percentage.setVisibility(View.VISIBLE);
                        branding_percentage.setText(downloadText);
                        fetchingDataFinish();
                    } else {
                        float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                        if (percentage < 100) {
                            downloadText = ((int) percentage) + "%";
                        } else {
                            fetchingDataFinish();
                            downloadText = 100 + "%";
                        }
                        branding_percentage.setVisibility(View.VISIBLE);
                        branding_percentage.setText(downloadText);
                    }
                    Log.d(TAG, "downloadMediaFiles: file already exists");
                }
            }
        } else {
            fetchingDataFinish();
        }
    }

    private void downloadMedia(String media) {

        new MediaDataDownloader(this) {
            @Override
            public void downloadComplete() {
                mediaDownloadCount++;
                Log.d(TAG, "downloadComplete: mediaDownloadCount:" + mediaDownloadCount);
                survey_download_progressbar.setProgress(mediaDownloadCount);
                String downloadText = "";
                if (mediaDownloadCount == mediaList.size()) {
                    downloadText = 100 + "%";
                    branding_percentage.setVisibility(View.VISIBLE);
                    branding_percentage.setText(downloadText);
                    fetchingDataFinish();
                } else {
                    float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                    if (percentage < 100) {
                        downloadText = ((int) percentage) + "%";
                    } else {
                        downloadText = 100 + "%";

                    }
                    branding_percentage.setVisibility(View.VISIBLE);
                    branding_percentage.setText(downloadText);
                }
            }

            @Override
            public void downFailed(String message) {
                OustSdkTools.showToast(message);
            }
        }.initDownload(media);
    }

    private void fetchingDataFinish() {
        if (!isActivityDestroyed) {
            if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null) &&
                    (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) &&
                    (assessmentPlayResponse.getScoresList() != null)) {
                scoresList = assessmentPlayResponse.getScoresList();
                challengerFinalScore = assessmentPlayResponse.getChallengerFinalScore();
                questionIndex = assessmentPlayResponse.getQuestionIndex();
                if (questionIndex >= scoresList.size()) {
                    questionIndex = (scoresList.size() - 1);
                }
            } else {
                scoresList = new ArrayList<>();
                challengerFinalScore = 0;
                questionIndex = 0;
            }
            assessmentRunning = true;
            startDateTime = TimeUtils.getCurrentDateAsString();

            startTransactions();
            enableSwipe();
        }
    }

    /*public void saveFeedSurveyCompleted(ActiveUser activeUser) {
        Log.d(TAG, "saveFeedSurveyCompleted: " + feedID);
        if (feedID > 0) {
            try {
                String node = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedID + "/userCompletionPercentage";
                OustFirebaseTools.getRootRef().child(node).setValue("100");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }*/

    private void openIntroCard(DTOCourseCard courseCardClass, boolean isIntroCard) {
        try {
            if (isIntroCard) {
                result_card_layout.setVisibility(View.GONE);
                intro_card_layout.setVisibility(View.VISIBLE);
            } else {
                result_card_layout.setVisibility(View.VISIBLE);
                intro_card_layout.setVisibility(View.GONE);
            }
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
                    case "IMAGE":
                        pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                        mediaList.add(courseCardClass.getReadMoreData().getData());
                        break;
                }
            }
            checkMediaExist(mediaList, pathList, courseCardClass, isIntroCard);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkMediaExist(List<String> mediaList, List<String> pathList, final DTOCourseCard courseCardClass, final boolean isIntroCard) {
        courseCardClass1 = courseCardClass;
        isIntroCard1 = isIntroCard;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                Log.d(TAG, "onDownLoadProgressChanged: " + progress);
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: message" + message + " errorCode:" + errorCode);
                //removeFile(courseCardClass, isIntroCard);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile(courseCardClass, isIntroCard);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        mediaSize = 0;
        for (int i = 0; i < mediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                mediaSize++;
                downLoad(mediaList.get(i), pathList.get(i), courseCardClass, isIntroCard);
            }
        }
        if (mediaSize == 0) {
            removeFile(courseCardClass, isIntroCard);
        }
    }

    public void removeFile(DTOCourseCard courseCardClass, boolean isIntroCard) {
        if (mediaSize > 0) {
            mediaSize--;
        }
        if (mediaSize == 0) {
            hideLoader();
            downloadComplete(courseCardClass, isIntroCard);
        }
    }

    public void downloadComplete(DTOCourseCard courseCardClass, boolean isIntroCard) {
        try {
            if (courseCardClass != null) {
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                ModuleOverViewFragment fragment = new ModuleOverViewFragment();
                if (isIntroCard) {
                    transaction.replace(R.id.intro_card_layout, fragment);
                    setCardAppearAnim(intro_card_layout);
                } else {
                    transaction.replace(R.id.result_card_layout, fragment);
                    setCardAppearAnim(result_card_layout);
                }
                fragment.isComingFromFeed(true);
                fragment.setSureveyIntroCard(true);
                fragment.setProgressVal(0);
                fragment.setCardttsEnabled(false);
                // TODO need to handle here
//                fragment.setCourseCardClass(courseCardClass);
                fragment.setLearningModuleInterface(this);
//                transaction.setCustomAnimations(R.anim.alphareverse_anim, R.anim.alpha_anim);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCardAppearAnim(FrameLayout layout) {
     /*   Animation alphaanim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cardappear_anim);
        layout.startAnimation(alphaanim);
        layout.setVisibility(View.VISIBLE);*/
    }

    private void startTransactions() {
        try {
            Log.d(TAG, "startTransactions: ");
            allQuestionsAttempted = false;
            if (playResponse != null && (playResponse.getqIdList() != null)) {
                if (questionIndex < playResponse.getqIdList().size()) {
                    /*if (questionIndex == (playResponse.getqIdList().size() - 1)) {
                        allQuestionsAttempted = true;
                    }*/
                    questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);

                    try {
                        if (OustStaticVariableHandling.getInstance().getMediaPlayer() != null) {
                            if (OustStaticVariableHandling.getInstance().getMediaPlayer().isPlaying()) {
                                OustStaticVariableHandling.getInstance().getMediaPlayer().stop();
                                OustStaticVariableHandling.getInstance().getMediaPlayer().release();

                            }
                            OustStaticVariableHandling.getInstance().setMediaPlayer(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    if (questionIndex == 0) {
                        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.alpha_anim);
                    }
                    LearningPlayFragment fragment = new LearningPlayFragment();
                    fragment.setLearningModuleInterface(SurveyDetailActivity.this);
                    fragment.setCardBackgroundImage(surveyBgImage);
                    fragment.setShowNavigateArrow(showNavigateArrow);
                    transaction.replace(R.id.feed_card_layout, fragment, "frag");
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setXp(20);
                    fragment.setMainCourseCardClass(courseCardClass);
                    fragment.setLearningcard_progressVal(questionIndex);
                    CourseLevelClass courseLevelClass = new CourseLevelClass();
                    if ((surveyTitle != null) && (!surveyTitle.isEmpty())) {
                        courseLevelClass.setLevelName(surveyTitle);
                    } else {
                        courseLevelClass.setLevelName("Survey");
                    }
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setAssessmentQuestion(true);
                    fragment.setSurveyQuestion(true);
                    if (scoresList != null && questionIndex == scoresList.size()) {
                        Scores scores = new Scores();
                        scoresList.add(questionIndex, scores);
                    }
                    assert scoresList != null;
                    fragment.setAssessmentScore(scoresList.get(questionIndex));
                    fragment.setTotalCards(playResponse.getqIdList().size());
                    fragment.setQuestions(questions);
//                    transaction.addToBackStack(null);

                    transaction.commit();

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            hideFetchingLoader();
                            card_layout.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, FIVE_HUNDRED_MILLI_SECONDS);
                } else {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            isReviewModeRunning = false;
                            allQuestionsAttempted = true;
                            pauseAssessmentGame();
                            assessmentRunning = false;
                            assessmentOverPage();
                            assessmentOver();

                            if (OustStaticVariableHandling.getInstance().getMediaPlayer() != null) {
                                if (OustStaticVariableHandling.getInstance().getMediaPlayer().isPlaying()) {
                                    OustStaticVariableHandling.getInstance().getMediaPlayer().stop();
                                    OustStaticVariableHandling.getInstance().getMediaPlayer().reset();
                                }
                                OustStaticVariableHandling.getInstance().getMediaPlayer().release();
                                OustStaticVariableHandling.getInstance().setMediaPlayer(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, 2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startReverseTransactions() {
        try {

            try {
                if (OustStaticVariableHandling.getInstance().getMediaPlayer() != null) {
                    if (OustStaticVariableHandling.getInstance().getMediaPlayer().isPlaying()) {
                        OustStaticVariableHandling.getInstance().getMediaPlayer().stop();
                        OustStaticVariableHandling.getInstance().getMediaPlayer().reset();
                    }
                    OustStaticVariableHandling.getInstance().getMediaPlayer().release();
                    OustStaticVariableHandling.getInstance().setMediaPlayer(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }


            if (playResponse != null && (playResponse.getqIdList() != null)) {
                if (questionIndex < scoresList.size()) {
                    questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
                    LearningPlayFragment fragment = new LearningPlayFragment();
                    fragment.setShowNavigateArrow(showNavigateArrow);
                    fragment.setLearningModuleInterface(SurveyDetailActivity.this);
                    fragment.setCardBackgroundImage(surveyBgImage);
                    transaction.replace(R.id.feed_card_layout, fragment, "frag");
                    DTOCourseCard courseCardClass = new DTOCourseCard();
                    courseCardClass.setXp(20);
                    fragment.setMainCourseCardClass(courseCardClass);
                    fragment.setLearningcard_progressVal(questionIndex);
                    CourseLevelClass courseLevelClass = new CourseLevelClass();
                    if ((surveyTitle != null) && (!surveyTitle.isEmpty())) {
                        courseLevelClass.setLevelName(surveyTitle);
                    } else {
                        courseLevelClass.setLevelName("Survey");
                    }
                    fragment.setSurveyQuestion(true);
                    fragment.setCourseLevelClass(courseLevelClass);
                    fragment.setAssessmentScore(scoresList.get(questionIndex));
                    fragment.setAssessmentQuestion(true);
                    fragment.setTotalCards(playResponse.getqIdList().size());
                    fragment.setQuestions(questions);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    assessmentOverPage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void enableSwipe() {
        try {
            card_layout.setOnTouchListener((v, event) -> {
                int motionEvent = event.getAction();
                if (motionEvent == MotionEvent.ACTION_DOWN) {
                    x1 = event.getX();
                    y1 = event.getY();
                    touchedOnce = true;
                } else if (motionEvent == MotionEvent.ACTION_MOVE) {
                    if (touchedOnce) {
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x1 - x2;
                        float deltaY = y1 - y2;
                        if (deltaX > 0 && deltaY > 0) {
                            if (deltaX > deltaY) {
                                if (deltaX > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    gotoNextQuestion();
                                }
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    if (questionIndex > 0) {
                                        questionIndex--;
                                        startReverseTransactions();
                                    }
                                }
                            }

                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    if (questionIndex > 0) {
                                        questionIndex--;
                                        startReverseTransactions();
                                    }
                                }
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    gotoNextQuestion();
                                }
                            }
                        }
                    }
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoNextScreen() {
        gotoNextQuestion();
    }

    @Override
    public void gotoPreviousScreen() {

        if (questionIndex > 0) {
            questionIndex--;
            startReverseTransactions();
        }

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
        Log.d(TAG, "setAnswerAndOc: ");
        Scores scores = new Scores();
        scores.setAnswer((userAns));
        scores.setCorrect(status);
        scores.setXp(oc);
        scores.setQuestion((int) questions.getQuestionId());
        scores.setQuestionType(questions.getQuestionType());
        scores.setQuestionSerialNo((questionIndex + 1));
        scores.setUserSubjectiveAns(subjectiveResponse);
        // add in array
        //scores.setTime(answeredSeconds);
        if (scoresList.get(questionIndex) != null && scoresList.get(questionIndex).getAnswer() != null) {
            scoresList.set(questionIndex, scores);
        } else if (scoresList.size() - 1 == questionIndex && scoresList.get(questionIndex) != null) {
            scoresList.set(questionIndex, scores);
        } else {
            scoresList.add(questionIndex, scores);
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
        Fragment readFragment = getSupportFragmentManager().findFragmentByTag("read_fragment");
        if (readFragment != null) {
            getSupportFragmentManager().popBackStack();
        }
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
        if (!surveyCompleted) {
            bottom_swipe.setVisibility(View.VISIBLE);
        } else {
            bottom_swipe.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSurveyExit(String message) {
        isSurveyExit = true;
        exitMessage = message;
        calculateFinalScore();

    }

    @Override
    public void handleQuestionAudio(boolean play) {

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    public void downLoad(final String fileName1, final String pathName, final DTOCourseCard courseCardClass, final boolean isIntroCard) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                downloadComplete(courseCardClass, isIntroCard);
                return;
            }
            String destn = this.getFilesDir() + "/";
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destn, fileName1, true, false);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoNextQuestion() {
        if (questionIndex < scoresList.size() && scoresList.get(questionIndex) != null) {
            questionIndex++;
            startTransactions();
        }
    }

    private void pauseAssessmentGame() {
        try {
            AssessmentCopyResponse assessmentPlayResponse1 = new AssessmentCopyResponse();
            assessmentPlayResponse1.setTotalQuestion("" + scoresList.size());
            assessmentPlayResponse1.setStudentId(activeUser.getStudentid());
            assessmentPlayResponse1.setGameId("" + activeGame.getGameid());
            assessmentPlayResponse1.setChallengerFinalScore("" + challengerFinalScore);
            assessmentPlayResponse1.setQuestionIndex("" + questionIndex);
            assessmentPlayResponse1.setAssessmentState(AssessmentState.INPROGRESS);
            List<ScoresCopy> scoresCopies = new ArrayList<>();
            if (scoresList != null) {
                for (int j = 0; j < scoresList.size(); j++) {
                    try {
                        ScoresCopy scoresCopy = new ScoresCopy();
                        scoresCopy.setAnswer(scoresList.get(j).getAnswer());
                        scoresCopy.setCorrect(scoresList.get(j).isCorrect());
                        scoresCopy.setGrade(scoresList.get(j).getGrade());
                        scoresCopy.setModuleId(scoresList.get(j).getModuleId());
                        scoresCopy.setQuestion("" + scoresList.get(j).getQuestion());
                        scoresCopy.setQuestionSerialNo("" + scoresList.get(j).getQuestionSerialNo());
                        scoresCopy.setQuestionType(scoresList.get(j).getQuestionType());
                        scoresCopy.setScore("" + scoresList.get(j).getScore());
                        scoresCopy.setSubject(scoresList.get(j).getSubject());
                        scoresCopy.setTime("" + scoresList.get(j).getTime());
                        scoresCopy.setTopic("" + scoresList.get(j).getTopic());
                        scoresCopy.setUserSubjectiveAns("" + scoresList.get(j).getUserSubjectiveAns());
                        scoresCopy.setXp("" + scoresList.get(j).getXp());
                        scoresCopies.add(scoresCopy);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
            if (courseId != 0) {
                assessmentPlayResponse1.setCourseId(String.valueOf(courseId));
            }
            assessmentPlayResponse1.setScoresList(scoresCopies);
            saveAssessmentGame(assessmentPlayResponse1, activeUser);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveAssessmentGame(AssessmentCopyResponse assessmentPlayResponse, ActiveUser activeUser) {
        try {
            Log.d(TAG, "saveAssessmentGame: ");
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + surveyId;
            if (isMultipleCplEnable && cplId != 0) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + cplId + "/contentListMap/assessment" + surveyId;
            } else {
                if (courseId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
                } else if (associatedAssessmentId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/assessment" + associatedAssessmentId + "/surveyAssessment" + surveyId;
                }
            }
            OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void assessmentOverPage() {
        intro_card_layout.setVisibility(View.GONE);
        card_view_survey.setVisibility(View.VISIBLE);

        removeCards();
        reviewSubmitPopup();
        if ((resultCard != null)) {
            showLoader();
            openIntroCard(resultCard, false);
        }
    }


    private void reviewSubmitPopup() {
        isReviewPopUp = true;
        //survey_status_text.setText(getResources().getString(R.string.review_text));
        int progressDone = 100;
        completion_percentage_progress.setProgress(progressDone);
        String progressText = progressDone + " % " + getResources().getString(R.string.done_text);
        completion_percentage_done.setText(progressText);
        if (reviewdialog != null && reviewdialog.isShowing()) {
            reviewdialog.dismiss();
        }
        reviewdialog = new Dialog(SurveyDetailActivity.this, R.style.DialogTheme);
        reviewdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reviewdialog.setContentView(R.layout.popup_survey_submit);
        Objects.requireNonNull(reviewdialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        reviewdialog.setCancelable(false);
        reviewdialog.show();

        LinearLayout survey_review = reviewdialog.findViewById(R.id.survey_review);
        LinearLayout survey_submit = reviewdialog.findViewById(R.id.survey_submit);
        FrameLayout popup_close = reviewdialog.findViewById(R.id.popup_close);

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        Drawable reviewDrawable = getResources().getDrawable(R.drawable.course_button_bg);

        DrawableCompat.setTint(
                DrawableCompat.wrap(reviewDrawable),
                Color.parseColor("#A4A4A4")
        );

        survey_review.setBackground(reviewDrawable);
        survey_submit.setBackground(OustSdkTools.drawableColor(drawable));

        survey_review.setOnClickListener(v -> {

            questionIndex = reviewCardNo;
            card_view_survey.setVisibility(View.GONE);
            isReviewModeRunning = true;
            startTransactions();
            card_layout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }

        });

        survey_submit.setOnClickListener(v -> {
            calculateFinalScore();
            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }
        });


        popup_close.setOnClickListener(v -> {

            if (reviewdialog.isShowing()) {
                reviewdialog.dismiss();
            }

        });

    }

    private void removeCards() {
        try {
            card_layout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentByTag("frag");
            if (f != null)
                transaction.remove(f);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void assessmentOver() {
        try {
            card_layout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentByTag("first");
            if (f != null)
                transaction.remove(f);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPopup(String content, boolean isExitSurvey) {
        try {

            //saveFeedSurveyCompleted(activeUser);

            long coins = 0;
            if (isExitSurvey) {
                if (exitOC > 0) {
                    coins = exitOC;
                }
            } else {
                if (rewardOC > 0) {
                    coins = rewardOC;
                }

                //showPopup(OustSdkApplication.getContext().getResources().getString(R.string.completing_survey_text"));
            }

            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText("OK");
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(content);
            popup.setCategory(OustPopupCategory.NOACTION);
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(this, SurveyPopUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Coins", coins);
            intent.putExtra("FeedId", feedID);
            intent.putExtra("isSurvey", true);
            intent.putExtra("surveyName", surveyTitle);
            startActivity(intent);

            assessmentPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), "0", null, activeGame.getGameid(), "0", AssessmentState.SUBMITTED);

            AssessmentCopyResponse assessmentCopyResponse1 = new AssessmentCopyResponse();
            assessmentCopyResponse1.setStudentId(activeUser.getStudentid());
            assessmentCopyResponse1.setScoresList(null);
            assessmentCopyResponse1.setQuestionIndex("0");
            assessmentCopyResponse1.setGameId(activeGame.getGameid());
            assessmentCopyResponse1.setChallengerFinalScore("0");
            assessmentCopyResponse1.setAssessmentState(AssessmentState.SUBMITTED);
            assessmentCopyResponse1.setScoresList(null);
            if (courseId != 0) {
                assessmentCopyResponse1.setCourseId(String.valueOf(courseId));
            }
            saveAssessmentGame(assessmentCopyResponse1, activeUser);

            if (feedID != 0) {
                OustStaticVariableHandling.getInstance().setResult_code(1444);
                OustStaticVariableHandling.getInstance().setFeedId(feed.getFeedId());
                OustStaticVariableHandling.getInstance().setFeedChanged(isFeedChange);
                OustStaticVariableHandling.getInstance().setLikeClicked(isLikeClicked);
                OustStaticVariableHandling.getInstance().setNumOfComments(feed.getNumComments());
                OustStaticVariableHandling.getInstance().setNumOfLikes(feed.getNumLikes());
                OustStaticVariableHandling.getInstance().setNumOfShares(feed.getNumShares());
                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", true);
                data.putExtra("isFeedChange", true);
                data.putExtra("isClicked", true);
                setResult(1444, data);

            } else {
                Intent data = new Intent();
                data.putExtra("FeedPosition", feedID);
                data.putExtra("FeedRemove", false);
                data.putExtra("isFeedChange", false);
                setResult(1444, data);
            }

            SurveyDetailActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ((assessmentRunning) && (!allQuestionsAttempted)) {
            pauseAssessmentGame();
            removeCards();
            setBtnStatus();
            assessmentRunning = false;
        } else if (assessmentRunning) {
            pauseAssessmentGame();
            assessmentRunning = false;
            assessmentOverPage();
        } else if (isReviewModeRunning) {
            pauseAssessmentGame();
            int progressDone = 100;
            completion_percentage_progress.setProgress(progressDone);
            String progressText = progressDone + " % " + getResources().getString(R.string.done_text);
            completion_percentage_done.setText(progressText);
            reviewCardNo = questionIndex;
            isReviewModeRunning = false;
            reviewsurvey_btn = true;
            removeCards();
        } else if (!isRecreate) {
            Intent data = new Intent();
            data.putExtra("FeedPosition", feedID);
            data.putExtra("FeedRemove", false);
            data.putExtra("isFeedChange", false);
            setResult(1444, data);

            SurveyDetailActivity.this.finish();
        }
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
        }

    }

    @Override
    public void onBackPressed() {
        try {
            OustStaticVariableHandling.getInstance().setResult_code(1444);
            OustStaticVariableHandling.getInstance().setFeedId(feed.getFeedId());
            OustStaticVariableHandling.getInstance().setFeedChanged(isFeedChange);
            OustStaticVariableHandling.getInstance().setLikeClicked(isLikeClicked);
            OustStaticVariableHandling.getInstance().setNumOfComments(feed.getNumComments());
            OustStaticVariableHandling.getInstance().setNumOfLikes(feed.getNumLikes());
            OustStaticVariableHandling.getInstance().setNumOfShares(feed.getNumShares());
            Intent data = new Intent();
            data.putExtra("FeedPosition", feedID);
            data.putExtra("FeedRemove", false);
            if (feed != null) {
                data.putExtra("FeedPosition", feed.getFeedId());
                data.putExtra("FeedComment", feed.getNumComments());
                data.putExtra("isFeedChange", isFeedChange);
                data.putExtra("isLikeClicked", isLikeClicked);
                data.putExtra("isFeedShareCount", feed.getNumShares());
                data.putExtra("isFeedLikeCount", feed.getNumLikes());
                data.putExtra("isClicked", true);
            }
            setResult(1444, data);

            if (!disableBackButton) {
                if (isReviewPopUp && !isReviewModeRunning) {
                    isReviewPopUp = false;
                    SurveyDetailActivity.this.finish();
                } else if ((assessmentRunning) && (!allQuestionsAttempted)) {
                    cancelGame();
                } else if (assessmentRunning) {
                    pauseAssessmentGame();
                    assessmentRunning = false;
                    assessmentOverPage();
                } else if (isReviewModeRunning) {
                    isReviewModeRunning = false;
                    allQuestionsAttempted = true;
                    pauseAssessmentGame();
                    assessmentRunning = false;
                    assessmentOverPage();
                    assessmentOver();

                } else {
                    SurveyDetailActivity.this.finish();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cancelGame() {
        try {

            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow playCanclePopup = OustSdkTools.createPopUp(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            popupContent.setText(getResources().getString(R.string.pause_and_resume));
            popupTitle.setText(getResources().getString(R.string.warning));
            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            btnYes.setOnClickListener(view -> {
                playCanclePopup.dismiss();
                pauseAssessmentGame();
                card_view_survey.setVisibility(View.VISIBLE);
                removeCards();
                assessmentRunning = false;
                //survey_status_text.setText(getResources().getString(R.string.resume));
                isRecreate = true;
                //recreate();

                showLoader();
                //
                if (dataBundle != null) {
                    Intent restartIntent = new Intent(SurveyDetailActivity.this, SurveyDetailActivity.class);
                    restartIntent.putExtras(dataBundle);
                    startActivity(restartIntent);
                    overridePendingTransition(0, 0);
                    SurveyDetailActivity.this.finish();
                } else {
                    getSurveyCardsFromFirebase();
                }


            });

            btnNo.setOnClickListener(view -> playCanclePopup.dismiss());
            btnClose.setOnClickListener(view -> playCanclePopup.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void feedComment(DTOUserFeedDetails.FeedDetails feed) {
        updateComment = true;
        Dialog dialog = new Dialog(SurveyDetailActivity.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_dialog);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView total_comments_text = dialog.findViewById(R.id.total_comments_text);
        ImageView comment_close = dialog.findViewById(R.id.comment_close);
        EditText comment_text = dialog.findViewById(R.id.comment_text);
        ImageButton send_comment_button = dialog.findViewById(R.id.send_comment_button);
        TextView no_comments = dialog.findViewById(R.id.no_comments);
        RecyclerView comment_list_rv = dialog.findViewById(R.id.comment_list_rv);

        Drawable feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);

        send_comment_button.setBackground(drawableColor(feedSendDrawable));


        try {
            final String message = "/userFeedComments/" + "feed" + feed.getFeedId();
            ValueEventListener commentsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> allCommentsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCommentsMap != null) {
                            ArrayList<AlertCommentData> commentsList = new ArrayList<>();

                            for (String queskey : allCommentsMap.keySet()) {
                                Object commentDataObject = allCommentsMap.get(queskey);
                                final Map<String, Object> commentsDataMap = (Map<String, Object>) commentDataObject;
                                if (commentsDataMap != null) {

                                    AlertCommentData alertCommentData = new AlertCommentData();
                                    if (commentsDataMap.get("addedOnDate") != null) {
                                        alertCommentData.setAddedOnDate((long) commentsDataMap.get("addedOnDate"));
                                    }

                                    if (commentsDataMap.get("comment") != null) {
                                        alertCommentData.setComment((String) commentsDataMap.get("comment"));
                                    }
                                    if (commentsDataMap.get("userAvatar") != null) {
                                        alertCommentData.setUserAvatar((String) commentsDataMap.get("userAvatar"));
                                    }
                                    if (commentsDataMap.get("userDisplayName") != null) {
                                        alertCommentData.setUserDisplayName((String) commentsDataMap.get("userDisplayName"));
                                    }
                                    if (commentsDataMap.get("userId") != null) {
                                        alertCommentData.setUserId((String) commentsDataMap.get("userId"));
                                    }
                                    if (commentsDataMap.get("userKey") != null) {
                                        alertCommentData.setUserKey(com.oustme.oustsdk.tools.OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }

                            String totalComments = "";


                            if (commentsList.size() != 0) {
                                comment_list_rv.setVisibility(View.VISIBLE);
                                no_comments.setVisibility(View.GONE);
                                Collections.sort(commentsList, AlertCommentData.commentSorter);

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(SurveyDetailActivity.this, commentsList, activeUser);
                                comment_list_rv.setItemAnimator(new DefaultItemAnimator());
                                comment_list_rv.setAdapter(feedCommentAdapter);
                                if (commentsList.size() > 1) {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comments_text);
                                } else {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comment_text);
                                }


                                if (!updateComment) {
                                    feed.setNumComments(commentsList.size());
                                    feed.setCommented(true);


                                    Intent intent = new Intent(SurveyDetailActivity.this, FeedUpdatingServices.class);
                                    intent.putExtra("FeedId", feed.getFeedId());
                                    intent.putExtra("FeedCommentSize", commentsList.size());
                                    startService(intent);
                                    //updateCommentCount(""+feed.getFeedId(),commentsList.size());
                                }

                                updateComment = false;


                            } else {
                                updateComment = false;
                                comment_list_rv.setVisibility(View.GONE);
                                no_comments.setVisibility(View.VISIBLE);
                            }
                            updateComment = false;
                            total_comments_text.setText(totalComments);

                        } else {
                            updateComment = false;
                        }
                    } else {
                        updateComment = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(commentsListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(commentsListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String comment_send_text = comment_text.getText().toString();
                Drawable feedSendDrawable;


                if (comment_send_text.isEmpty()) {
                    send_comment_button.setEnabled(false);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);
                } else {
                    send_comment_button.setEnabled(true);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_selected);
                }

                send_comment_button.setBackground(drawableColor(feedSendDrawable));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send_comment_button.setOnClickListener(v -> {

            String comment = comment_text.getText().toString();
            if (!comment.isEmpty()) {

                AlertCommentData alertCommentData = new AlertCommentData();
                alertCommentData.setComment(comment);
                alertCommentData.setAddedOnDate(System.currentTimeMillis());
                alertCommentData.setDevicePlatform("Android");
                alertCommentData.setUserAvatar(activeUser.getAvatar());
                alertCommentData.setUserId(activeUser.getStudentid());
                alertCommentData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
                alertCommentData.setUserDisplayName(activeUser.getUserDisplayName());
                alertCommentData.setNumReply(0);

                sendCommentToFirebase(alertCommentData, "" + feed.getFeedId());
                isFeedChange = true;
                comment_text.setText("");

            }

        });

        comment_close.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void sendCommentToFirebase(AlertCommentData alertCommentData, String feedId) {
        String message = "/userFeedComments/" + "feed" + feedId;
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(alertCommentData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(false);
        setidToUserFeedThread(newPostRef.getKey(), feedId);
        updateFeedViewed(feed);
        // updateCommentCount(feedId);
    }

    private void setidToUserFeedThread(String key, String feedId) {
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "commentThread/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

    }

    private void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed) {
        try {
            if (!mFeed.isClicked()) {
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (DatabaseError != null) {
                            Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                        } else {
                            Log.e("", "Firebase counter increment succeeded.");
                        }
                    }
                });
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
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile(courseCardClass1, isIntroCard1);
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
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

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        registerReceiver(myFileDownLoadReceiver, intentFilter);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        isActivityDestroyed = true;
        super.onDestroy();
    }

    private void setResumeProgress(int qstnIndex, int totalQuestions) {

        if (questionsCount != 0) {
            totalQuestions = questionsCount;
        }

        if (qstnIndex <= totalQuestions) {
            progress_lay.setVisibility(View.VISIBLE);
            double value = (qstnIndex * 1.0) / totalQuestions;
            int progressDone = ((int) (value * 100));
            completion_percentage_progress.setProgress(progressDone);
            String progressText = progressDone + " % " + getResources().getString(R.string.done_text);
            completion_percentage_done.setText(progressText);
        } else {
            int progressDone = 100;
            progress_lay.setVisibility(View.VISIBLE);
            completion_percentage_progress.setProgress(progressDone);
            String progressText = progressDone + " % " + getResources().getString(R.string.done_text);
            completion_percentage_done.setText(progressText);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}