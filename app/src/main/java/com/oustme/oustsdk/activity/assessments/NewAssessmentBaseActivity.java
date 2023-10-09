package com.oustme.oustsdk.activity.assessments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.assessment_ui.assessmentCompletion.AssessmentCompleteScreen;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.LearningPlayFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadForKitKatFragment;
import com.oustme.oustsdk.fragments.courses.MediaUploadQuestionFragment;
import com.oustme.oustsdk.fragments.courses.learningplaynew.LearningPlayFragmentNew;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NetworkUtil;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class NewAssessmentBaseActivity extends AppCompatActivity implements OnNetworkChangeListener, LearningModuleInterface, CustomVideoControlListener {


    private static final String TAG = "NewAssessmentBaseActivi";

    private String courseId, courseColnId, imageString;
    private boolean containCertificate;
    private PlayResponse playResponse;
    private AssessmentPlayResponse assessmentPlayResponce;
    private int noofQuesinGame;
    private int questionIndex;
    private long resumeTime = 0;
    private long challengerFinalScore = 0;
    private String startDateTime;
    private List<Scores> scoresList = new ArrayList<>();
    private DTOQuestions questions;
    private ActiveUser activeUser;
    private ActiveGame activeGame;
    private SubmitRequest submitRequest;
    private boolean gameSubmitRequestSent = false;
    private GamePoints gamePoints;
    private TextView txtTimerTextView, assessmenttimer, questionprogresstext;
    //    private long answeredSeconds;
    private CounterClass timer;
    private RelativeLayout assessmentimg_layout;
    private ProgressBar questionprogress;
    private ImageView questiotexttospeech_btn;
    private long questionXp = 20;
    private boolean timePenaltyDisabled = false;
    private boolean isResumeSameQuestion = false;
    private boolean isCplModule;
    private boolean isMultipleCplModule = false;
    private long currentCplId;
    private RelativeLayout header_ll;
    private boolean isFromCourse;
    private boolean isRepeatAudio = false;
    private boolean isComingFromCpl = false;
    private boolean courseAssociated;
    private long mappedCourseId;
    private boolean isMappedCourseDistributed;
    private boolean isSurveyFromCourse;
    private String assessmentBgImg;

    private boolean isEvent;
    private int eventId = 0;
    private UserEventAssessmentData userEventAssessmentData;
    private boolean showAssessmentResultScore;

    boolean reAttemptAllowed = false;
    long nAttemptCount = 0;
    long nAttemptAllowedToPass = 0;
    boolean isSecureSessionOn = true;

    //boolean isDeadlineCrossed = false;
    //long penaltyPercentage = 0;
    long rewardOC = 0;
    private boolean surveyAssociated, surveyMandatory;
    private long mappedSurveyId;
    boolean isMicroCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        OustSdkTools.setLocale(NewAssessmentBaseActivity.this);
        setContentView(R.layout.activity_new_assessment_base);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OustPreferences.saveAppInstallVariable("IsAssessmentPlaying", true);
        isSecureSessionOn = true;
        initViews();
        initQuestion();

        try {
            Log.d(TAG, "onCreate: nModulesCompleted: " + OustPreferences.getTimeForNotification("cplCompletedModules"));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

//        OustGATools.getInstance().reportPageViewToGoogle(NewAssessmentBaseActivity.this, "New Assessment Landing Landing Page");
    }

    private void initViews() {
        Log.d(TAG, "initViews: ");
        txtTimerTextView = (TextView) findViewById(R.id.timer);
        assessmenttimer = (TextView) findViewById(R.id.assessmenttimer);
        assessmentimg_layout = (RelativeLayout) findViewById(R.id.assessmentimg_layout);
        questionprogress = (ProgressBar) findViewById(R.id.questionprogress);
        questionprogresstext = (TextView) findViewById(R.id.questionprogresstext);
        questiotexttospeech_btn = (ImageView) findViewById(R.id.questiotexttospeech_btn);
        header_ll = (RelativeLayout) findViewById(R.id.header);
    }

    public void initQuestion() {
        try {

            Log.d(TAG, "initQuestion: ");

            OustSdkTools.speakInit();
            //oustAnalyticsTools.reportPageViews(this, getString(R.string.pn_question));
            String blink = OustPreferences.get("BranchLinkReceiver");
            //branchLinkReceiver = OustSdkTools.getInstance().getBranchData(blink);
            if (OustAppState.getInstance().isAssessmentGame()) {
                NetworkUtil.setOnNetworkChangeListener(NewAssessmentBaseActivity.this);
            }
            getIntentData();

            setStartingData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getIntentData() {
        Log.d(TAG, "getIntentData: ");
        Intent CallingIntent = getIntent();
        isSurveyFromCourse = CallingIntent.hasExtra("isSurveyFromCourse") && CallingIntent.getBooleanExtra("isSurveyFromCourse", false);
        courseAssociated = CallingIntent.hasExtra("courseAssociated") && CallingIntent.getBooleanExtra("courseAssociated", false);
        mappedCourseId = CallingIntent.hasExtra("mappedCourseId") ? CallingIntent.getLongExtra("mappedCourseId", 0) : 0;
        courseId = CallingIntent.hasExtra("courseId") ? CallingIntent.getStringExtra("courseId") : "";
        isCplModule = CallingIntent.hasExtra("isCplModule") && CallingIntent.getBooleanExtra("isCplModule", false);
        courseColnId = CallingIntent.hasExtra("courseColnId") ? CallingIntent.getStringExtra("courseColnId") : "";
        assessmentBgImg = CallingIntent.hasExtra("bgImage") ? CallingIntent.getStringExtra("bgImage") : "";
        containCertificate = CallingIntent.hasExtra("containCertificate") && CallingIntent.getBooleanExtra("containCertificate", false);
        imageString = CallingIntent.hasExtra("imageString") ? CallingIntent.getStringExtra("imageString") : "";
        questionXp = CallingIntent.hasExtra("questionXp") ? CallingIntent.getLongExtra("questionXp", 20) : 0;
        timePenaltyDisabled = CallingIntent.hasExtra("timePenaltyDisabled") && CallingIntent.getBooleanExtra("timePenaltyDisabled", false);
        currentCplId = CallingIntent.hasExtra("currentCplId") ? CallingIntent.getLongExtra("currentCplId", 0) : 0;
        isMultipleCplModule = CallingIntent.hasExtra("isMultipleCplModule") && CallingIntent.getBooleanExtra("isMultipleCplModule", false);
        Log.e("SCORE", "questionXp " + questionXp);
        this.activeUser = OustAppState.getInstance().getActiveUser();
        this.activeGame = OustSdkTools.getInstance().getAcceptChallengeData(CallingIntent.getStringExtra("ActiveGame"));
        this.playResponse = OustAppState.getInstance().getPlayResponse();
        Gson gson = new GsonBuilder().create();
        try {
            this.gamePoints = gson.fromJson(CallingIntent.getStringExtra("GamePoints"), GamePoints.class);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        assessmentPlayResponce = getAssessmentPlayResp(CallingIntent.hasExtra("assessmentResp") ? CallingIntent.getStringExtra("assessmentResp") : "");
        totalTimeForAssessment = CallingIntent.hasExtra("totalTimeOfAssessment") ? CallingIntent.getIntExtra("totalTimeOfAssessment", 0) : 0;
        isResumeSameQuestion = CallingIntent.hasExtra("resumeSameQuestion") && CallingIntent.getBooleanExtra("resumeSameQuestion", false);
        showAssessmentResultScore = CallingIntent.hasExtra("showAssessmentResultScore") && CallingIntent.getBooleanExtra("showAssessmentResultScore", false);

        if (CallingIntent.hasExtra("secureSessionOn")) {
            isSecureSessionOn = CallingIntent.getBooleanExtra("secureSessionOn", true);
        }
        Log.d(TAG, "getIntentData: securescreen:" + isSecureSessionOn);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nAttemptAllowedToPass = bundle.getLong("nAttemptAllowedToPass", 0);
            nAttemptCount = bundle.getLong("nAttemptCount", 0);
            reAttemptAllowed = bundle.getBoolean("reAttemptAllowed", false);
            isFromCourse = bundle.getBoolean("IS_FROM_COURSE", false);
            isComingFromCpl = bundle.getBoolean("isComingFromCpl", false);
        }
        if (CallingIntent.hasExtra("isMicroCourse")) {
            isMicroCourse = bundle.getBoolean("isMicroCourse", false);
        } else {
            isMicroCourse = false;
        }

        /*if(CallingIntent.hasExtra("isDeadlineCrossed")){
            isDeadlineCrossed = CallingIntent.getBooleanExtra("isDeadlineCrossed", false);
        }
        if(CallingIntent.hasExtra("penaltyPercentage")){
            penaltyPercentage = CallingIntent.getLongExtra("penaltyPercentage", 0);
        }*/
        if (CallingIntent.hasExtra("rewardOC")) {
            rewardOC = CallingIntent.getLongExtra("rewardOC", 0);
        }

        if (CallingIntent.hasExtra("surveyAssociated")) {
            surveyAssociated = CallingIntent.getBooleanExtra("surveyAssociated", false);
        }
        if (CallingIntent.hasExtra("surveyMandatory")) {
            surveyMandatory = CallingIntent.getBooleanExtra("surveyMandatory", false);
        }
        if (CallingIntent.hasExtra("mappedSurveyId")) {
            mappedSurveyId = CallingIntent.getLongExtra("mappedSurveyId", 0);
        }

        userEventAssessmentData = new UserEventAssessmentData();
        if (CallingIntent.hasExtra("isEventLaunch")) {
            isEvent = CallingIntent.getBooleanExtra("isEventLaunch", false);
            eventId = CallingIntent.hasExtra("eventId") ? CallingIntent.getIntExtra("eventId", 0) : 0;
            nCorrect = CallingIntent.hasExtra("nCorrect") ? CallingIntent.getIntExtra("nCorrect", 0) : 0;
            nWrong = CallingIntent.hasExtra("nWrong") ? CallingIntent.getIntExtra("nWrong", 0) : 0;
        } else {
            nCorrect = 0;
            nWrong = 0;
        }
        userEventAssessmentData.setEventId(eventId);
    }

    public void showQuestionProgressMaxVal(int noofQuesinGame) {
        questionprogress.setMax(noofQuesinGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForAudio();

        try {
            if (OustAppState.getInstance().getAssessmentFirebaseClass() != null && OustAppState.getInstance().getAssessmentFirebaseClass().isCourseAssociated() && OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                getUserCourses(OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onPause() {
        if (!OustStaticVariableHandling.getInstance().isCameraStarted()) {
            try {
                OustSdkTools.stopSpeech();
                stopMusicPlay();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if (timer != null) {
                cancelTimer();
            }
            if (!gameSubmitRequestSent) {
                gameSubmitRequestSent = true;
                if (OustAppState.getInstance().isAssessmentGame()) {
                    submitGame();
                } else {
                    if (isFromCourse && isMicroCourse) {
                        try {
                            OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                        }
                    }
                    finish();
                }
            }
        }
        super.onPause();
    }

    private int opponentFinalScore, opponentAnswerCount;

    public void submitGame() {
        Log.d(TAG, "submitGame: ");
        try {
            if ((playResponse != null)) {
                if (OustAppState.getInstance().isAssessmentGame()) {
                    pauseAssessmentGame();
                    return;
                }
                setGameSubmitRequestSent(true);
                for (; questionIndex < noofQuesinGame; questionIndex++) {
                    if (gamePoints != null && gamePoints.getPoints() != null && gamePoints.getPoints().length != 0) {
                        opponentFinalScore += gamePoints.getPoints()[questionIndex].getPoint();
                        if (gamePoints.getPoints()[questionIndex].isCorrect()) {
                            opponentAnswerCount++;
                        }
                    }
                    if (!isResumeSameQuestion) {
                        Scores scores = new Scores();
                        challengerScore = 0;
                        scores.setAnswer("");
                        scores.setCorrect(false);
                        scores.setTime(localquestiontime);
                        scores.setScore(challengerScore);
                        scores.setSubject(questions.getSubject());
                        scores.setTopic(questions.getTopic());
                        scores.setQuestion((int) questions.getQuestionId());
                        scores.setQuestionType(questionType);
                        scores.setQuestionSerialNo((questionIndex + 1));
//                    scores.setScore(0);
                        wrongAnswerCount++;
//                    scores.setTime(0);
                        scores.setXp(0);
                        playResponse.setCorrectAnswerCount(correctAnswerCount);
                        playResponse.setWrongAnswerCount(wrongAnswerCount);
                        scoresList.set(questionIndex, scores);
                    }

                }
                //setFinalAnimToHideQuestion();
                calculateFinalScore();
            } else {
                if (isFromCourse && isMicroCourse) {
                    try {
                        OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                    }
                }
                finish();
            }
        } catch (Exception e) {
            if (isFromCourse && isMicroCourse) {
                try {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                }
            }
            finish();
        }
    }


    public void getUserCourses(final long mappedCourseId) {

        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course" + mappedCourseId;
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() == null) {
                            isMappedCourseDistributed = false;
                            distributeCourse(mappedCourseId);
                            // okbtn.setEnabled(true);
                        } else {
                            Log.d(TAG, "onDataChange: course distributed:");
                            //  okbtn.setClickable(true);
                            isMappedCourseDistributed = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            FirebaseRefClass courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            // OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void distributeCourse(long mappedCourseId) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
        JsonObjectRequest jsonObjReq = null;
        String catalog_distribution_url = "";

        catalog_distribution_url = getResources().getString(R.string.distribut_course_url);
        catalog_distribution_url = catalog_distribution_url.replace("{courseId}", mappedCourseId + "");
        catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("users", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ShowPopup.getInstance().dismissProgressDialog();
                    if (response.optBoolean("success")) {
                        isMappedCourseDistributed = true;
                        //   okbtn.setVisibility(View.VISIBLE);
                        //  mRetryLayout.setVisibility(View.GONE);
                        //okbtn.setClickable(true);
                    }
                } catch (Exception e) {
                    // okbtn.setVisibility(View.GONE);
                    // mRetryLayout.setVisibility(View.VISIBLE);
                    // ShowPopup.getInstance().dismissProgressDialog();
                    // OustSdkTools.showToast(getResources().getString(R.string.error_message));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //  ll_progress.setVisibility(View.GONE);
                //  okbtn.setVisibility(View.GONE);
                // mRetryLayout.setVisibility(View.VISIBLE);
                // ShowPopup.getInstance().dismissProgressDialog();
                //  OustSdkTools.showToast(getResources().getString(R.string.error_message));
            }
        });


    }

    private void pauseAssessmentGame() {
        try {
            if (!isResumeSameQuestion) {
                Scores scores = new Scores();
                challengerScore = 0;
                scores.setAnswer("");
                scores.setCorrect(false);
                scores.setSubject(questions.getSubject());
                scores.setTopic(questions.getTopic());
                scores.setTime(localquestiontime);
                scores.setScore(challengerScore);
                scores.setQuestion((int) questions.getQuestionId());
                scores.setQuestionType(questionType);
                scores.setQuestionSerialNo((questionIndex + 1));
                // add in array
                scores.setScore(challengerScore);
                wrongAnswerCount++;
                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
//            scores.setTime(0);
                scores.setXp(0);
                scoresList.set(questionIndex, scores);
                assessmentPlayResponce.setResumeTime("" + 0);
                questionIndex++;
            }
            if (questionIndex < (noofQuesinGame)) {
                assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex + "", scoresList,
                        activeGame.getGameid(), challengerFinalScore + "", AssessmentState.INPROGRESS);

                if (isResumeSameQuestion) {
                    assessmentPlayResponce.setResumeTime("" + millis / 1000);
                }

                saveAssessmentGame(assessmentPlayResponce, activeUser, null);
                if ((isEvent && OustStaticVariableHandling.getInstance().getOustApiListener() != null) || isFromCourse) {
                    userEventAssessmentData.setAssessmentId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                    userEventAssessmentData.setnTotalQuestions(playResponse.getqIdList().size());

                    int answered = questionIndex + 1;
                    if (isResumeSameQuestion) {
                        answered = questionIndex;
                    }
                    userEventAssessmentData.setnQuestionAnswered(answered);
                    userEventAssessmentData.setnQuestionWrong(nWrong);
                    userEventAssessmentData.setnQuestionCorrect(nCorrect);
                    userEventAssessmentData.setnQuestionSkipped((answered - (nCorrect + nWrong) > 0) ? answered - (nWrong + nCorrect) : 0);
                    userEventAssessmentData.setEventId(eventId);
                    userEventAssessmentData.setUserProgress("InProgress");

                    //userEventAssessmentData.setScore(challengerFinalScore);
                    try {
                        int percentage = (int) ((answered / (float) userEventAssessmentData.getnTotalQuestions()) * 100);
                        userEventAssessmentData.setUserCompletionPercentage(percentage);
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
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    if (!isFromCourse) {
                        if (isCplModule) {
                            UserEventCplData userEventCplData = new UserEventCplData();
                            userEventCplData.setCurrentModuleType("Assessment");
                            userEventCplData.setEventId(eventId);

                            userEventCplData.setCurrentModuleId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                            userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                            final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                            final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                            userEventCplData.setnTotalModules(totalModules);
                            userEventCplData.setnModulesCompleted(completedModules);
                            userEventCplData.setUserProgress("InProgress");
                            userEventCplData.setCurrentModuleProgress("InProgress");
                            userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                            OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                        } else {
                            OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                        }
                    } else {
                        userEventAssessmentData.setEventId(0);
                        OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);
                    }
                }
                if (isFromCourse && isMicroCourse) {
                    try {
                        OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                    }
                }
                finish();
            } else {
                //view.setFinalAnimToHideQuestion();
                calculateFinalScore();
            }
        } catch (Exception e) {
            if (isFromCourse && isMicroCourse) {
                try {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                }
            }
            finish();
        }
    }


    private void setStartingData() {
        if (isSecureSessionOn) {
            keepScreenOnSecure();
        }
        setRateUsPopupVariables();
        setQuizRelatedData(assessmentPlayResponce);
    }

    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
        }
    }

    public void setRateUsPopupVariables() {
        try {
            int noofGame = OustPreferences.getSavedInt("noofGameForRateusPopup");
            if (noofGame <= 10) {
                noofGame++;
                OustPreferences.saveintVar("noofGameForRateusPopup", noofGame);
            }
        } catch (Exception e) {
        }
    }

    private void setQuizRelatedData(AssessmentPlayResponse assessmentPlayResponce) {
        Log.d(TAG, "setQuizRelatedData: ");
        if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
            noofQuesinGame = playResponse.getqIdList().size();
        } else {
            noofQuesinGame = playResponse.getEncrypQuestions().length;
            playResponse = OustSdkTools.getInstance().getDecryptedQuestion(playResponse);
        }

        Log.d(TAG, "setQuizRelatedData: noofquestons:" + noofQuesinGame);
        checkForRepeatedQuestions(assessmentPlayResponce);

        scoresList = new ArrayList<>();
        showQuestionProgressMaxVal(noofQuesinGame);
        questionIndex = 0;
        challengerFinalScore = 0;
        if ((assessmentPlayResponce != null)) {
            if (assessmentPlayResponce.getScoresList() != null) {
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    if (i < assessmentPlayResponce.getScoresList().size() && assessmentPlayResponce.getScoresList().get(i) != null) {
                        scoresList.add(i, assessmentPlayResponce.getScoresList().get(i));
                    } else {
                        scoresList.add(i, new Scores());
                    }
                }
                questionIndex = assessmentPlayResponce.getQuestionIndex();
                Log.d(TAG, "setQuizRelatedData: " + questionIndex);
                challengerFinalScore = assessmentPlayResponce.getChallengerFinalScore();
            }
            resumeTime = assessmentPlayResponce.getResumeTime();
        } else {
            assessmentPlayResponce = new AssessmentPlayResponse();
        }
        showAssessmentGameTop();
        isMusicStop = false;
        initializeSound("");
        startTransaction();
    }

    private void checkForRepeatedQuestions(AssessmentPlayResponse assessmentPlayResponce) {
        try {
            if (assessmentPlayResponce != null && assessmentPlayResponce.getScoresList() != null && assessmentPlayResponce.getScoresList().size() > 0) {
                List<Integer> qIdList = new ArrayList<>();
                Map<Integer, Integer> map = new HashMap<>();
                Map<Integer, Integer> remainMap = new HashMap<>();
                TreeMap<Integer, Integer> sorted = new TreeMap<>();

                List<Scores> myScores = assessmentPlayResponce.getScoresList();
                for (int i = 0; i < myScores.size(); i++) {
                    if (myScores.get(i).getQuestion() != 0) {
                        map.put(myScores.get(i).getQuestion(), i);
                    }
                }
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    if (!map.containsKey(playResponse.getqIdList().get(i))) {
                        remainMap.put(i, playResponse.getqIdList().get(i));
                    }
                }
                sorted.putAll(remainMap);
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    qIdList.add(entry.getKey());
                }
                for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
                    qIdList.add(entry.getValue());
                }

                playResponse.setqIdList(qIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showAssessmentGameTop() {
        Log.d(TAG, "showAssessmentGameTop: ");
        //questionmainheader.setVisibility(View.GONE);
        assessmentimg_layout.setVisibility(View.VISIBLE);
    }

    public void setQutionIndex(int questionIndex, int noofQuesinGame) {
        questionprogress.setProgress((questionIndex + 1));
        questionprogresstext.setText((questionIndex + 1) + "/" + noofQuesinGame);
    }

    private boolean isCurrentCardQuestion = false;


    private void startTransaction() {
        Log.d(TAG, "startTransaction: ");
        try {
            if (questionIndex < playResponse.getqIdList().size()) {
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                    questions = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(questionIndex));
                } else {
                    questions = playResponse.getQuestionsByIndex(questionIndex);
                }
                if (questions != null) {
                    changeOrientationPortrait();
                    setQutionIndex(questionIndex, noofQuesinGame);
                    if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                        OustSdkTools.setLayoutBackgroud(txtTimerTextView, R.drawable.roundedcorner);
                        txtTimerTextView.setTextColor(getResources().getColor(R.color.Orange));
                        txtTimerTextView.setText(getResources().getString(R.string.watch));
                        millis = 0;
                        stopMusicPlay();
                    } else {
                        checkForAudio();
                        if (resumeTime > 0) {
                            startTimer(resumeTime);
                            resumeTime = 0;
                        } else {
                            startTimer(questions.getMaxtime());
                        }
                    }
                    initializeFragment(transaction);
                }
            } else {
                calculateFinalScore();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    protected void initializeFragment(FragmentTransaction transaction) {
        questionType = questions.getQuestionType();
        questionCategory = questions.getQuestionCategory();
        if (questionIndex < playResponse.getqIdList().size()) {
            isCurrentCardQuestion = true;
            if (questions.isLearningPlayNew()) {
                openLearningPlayNewFragment(transaction);
            } else if (questions.isMediaUploadQues()) {
                stopMusicPlay();
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    openMediaKitkatFragment(transaction);
                } else {
                    openMediaFragment(transaction);
                }
            } else {
                openLearningPlayFragment(transaction);
            }
        }
    }

    protected void openLearningPlayFragment(FragmentTransaction transaction) {
        Log.d(TAG, "openLearningPlayFragment: ");
        LearningPlayFragment fragment = new LearningPlayFragment();
        fragment.setLearningModuleInterface(this);
        fragment.setShowNavigateArrow(false);
        transaction.replace(R.id.fragement_container, fragment, "learningPlay_frag" + questionIndex);
        fragment.setAssessmentQuestion(true);
        fragment.setMainCourseCardClass(getCourseCardClass());
        fragment.setLearningcard_progressVal(questionIndex);
        fragment.setCourseLevelClass(getCourseLevelClass());
        initializeQuesScore();
        fragment.setAssessmentScore(scoresList.get(questionIndex));
        fragment.setTotalCards(scoresList.size());
        fragment.setQuestions(questions);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected DTOCourseCard getCourseCardClass() {
        DTOCourseCard courseCardClass = new DTOCourseCard();
        courseCardClass.setXp(questionXp);
        courseCardClass.setBgImg(assessmentBgImg);
        return courseCardClass;
    }

    protected CourseLevelClass getCourseLevelClass() {
        CourseLevelClass courseLevelClass = new CourseLevelClass();
        if (questions != null) {
            courseLevelClass.setLevelName("Level " + (questionIndex + 1));
        }
        return courseLevelClass;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void openMediaFragment(FragmentTransaction transaction) {
        MediaUploadQuestionFragment fragment = new MediaUploadQuestionFragment();
        fragment.setLearningModuleInterface(this);
        fragment.setShowNavigateArrow(false);
        fragment.setAssessmentQuestion(true);
        transaction.replace(R.id.fragement_container, fragment, "media_upload");
        fragment.setMainCourseCardClass(getCourseCardClass());
        fragment.setLearningcard_progressVal(questionIndex);
        fragment.setCourseLevelClass(getCourseLevelClass());
        initializeQuesScore();
        fragment.setAssessmentScore(scoresList.get(questionIndex));
        fragment.setTotalCards(scoresList.size());

        Bundle bundle = new Bundle();
        bundle.putBoolean("enableGalleryUpload", questions.isEnableGalleryUpload());
        fragment.setArguments(bundle);
        fragment.setQuestions(questions);
//                                transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void openMediaKitkatFragment(FragmentTransaction transaction) {
        MediaUploadForKitKatFragment fragment = new MediaUploadForKitKatFragment();
        fragment.setLearningModuleInterface(this);
        fragment.setShowNavigateArrow(false);
        transaction.replace(R.id.fragement_container, fragment, "media_upload");
        fragment.setAssessmentQuestion(true);
        fragment.setMainCourseCardClass(getCourseCardClass());
        fragment.setLearningcard_progressVal(questionIndex);
        fragment.setCourseLevelClass(getCourseLevelClass());
        initializeQuesScore();
        fragment.setAssessmentScore(scoresList.get(questionIndex));
        fragment.setTotalCards(scoresList.size());
        fragment.setQuestions(questions);
//                                transaction.addToBackStack(null);
        if (questions != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("enableGalleryUpload", questions.isEnableGalleryUpload());
            fragment.setArguments(bundle);
        }
        transaction.commit();
    }

    protected void openLearningPlayNewFragment(FragmentTransaction transaction) {
        LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
        fragment.setLearningModuleInterface(this);
        fragment.setAssessmentQuestion(true);
        fragment.setShowNavigateArrow(false);
        transaction.replace(R.id.fragement_container, fragment, "learningplaynew_frag");
        fragment.setMainCourseCardClass(getCourseCardClass());
        fragment.setLearningcard_progressVal(questionIndex);
        fragment.setCourseLevelClass(getCourseLevelClass());
        initializeQuesScore();
        fragment.setAssessmentScore(scoresList.get(questionIndex));
        fragment.setTotalCards(scoresList.size());
        fragment.setQuestions(questions);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void initializeQuesScore() {
        if (scoresList != null && questionIndex == scoresList.size()) {
            Scores scores = new Scores();
            scoresList.add(questionIndex, scores);
        }
    }

    private void checkForAudio() {
        stopMusicPlay();
        if (questions != null) {
            questiotexttospeech_btn.setVisibility(View.GONE);
            if (!(questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A) || questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                if (questions.getAudio() != null && !questions.getAudio().isEmpty()) {
                    isMusicStop = false;
                    //initializeSound("");
                    initializeQuestionSound(questions.getAudio());
                    questiotexttospeech_btn.setVisibility(View.VISIBLE);
                    //animateAudioIcon();
                } else {
                    isMusicStop = false;
                    if (isQuestionAudio) {
                        isQuestionAudio = false;
                        initializeSound("");
                    }
                }
            } else {
                stopMusicPlay();
            }
        }
    }

    private AnimatorSet scaleDown;

    private void animateView(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.75f, 1);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.75f, 1);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);
        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
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

    private boolean isQuestionAudio = false;

    private void initializeQuestionSound(String audio) {
        try {
            isQuestionAudio = true;
            if ((!OustPreferences.getAppInstallVariable("isttsfileinstalled") || (!OustAppState.getInstance().isAssessmentGame())) ||
                    ((OustAppState.getInstance().isAssessmentGame()) && (!OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled()))) {
                getSignedUrl(audio);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initializeSound(String audio) {
        try {

            if ((!OustPreferences.getAppInstallVariable("isttsfileinstalled") || (!OustAppState.getInstance().isAssessmentGame())) ||
                    ((OustAppState.getInstance().isAssessmentGame()) && (!OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled()))) {
                if (OustAppState.getInstance().getAssessmentFirebaseClass().isBackgroundAudioForAssessment()) {
                    if (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList() != null) {
                        if (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList().size() > 0) {
                            playAssessmentBackScore(OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentMediaList().get(0));
                        }
                    } else
                        playAudio("quiz_background.mp3", true);
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
            playQuesAudio(audio);
        } else {
            playAudio("quiz_background.mp3", true);
        }
    }

    public void calculateFinalScore() {
        Log.d(TAG, "calculateFinalScore: ");
        //startGameOverMusic();
        setGameSubmitRequestSent(true);
        try {
            if ((activeGame.getStudentid() != null) && (!activeGame.getStudentid().isEmpty())) {
                activeGame.setStudentid(activeUser.getStudentid());
            }

            if ((activeGame.getChallengerid() != null) && (!activeGame.getChallengerid().isEmpty())) {
                activeGame.setChallengerid(activeUser.getStudentid());
            }
            submitRequest = new SubmitRequest();
            submitRequest.setWinner(findWinner());

            submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
            totaltimetaken = 0;
            for (int i = 0; i < scoresList.size(); i++) {
                totaltimetaken += scoresList.get(i).getTime();
            }
            float value = ((totalTimeForAssessment - totaltimetaken) / totalTimeForAssessment);
            Log.e("SCORE", "value " + value);
            float bonusXp = Math.round(value * questionXp);
            Log.e("SCORE", "bonus XP " + bonusXp);
            Log.e("SCORE", "questionXp " + questionXp);
            Log.e("SCORE", "total time taken " + totaltimetaken);
            Log.e("SCORE", "total time taken " + totalTimeForAssessment);

            if (challengerFinalScore > 0 && bonusXp > 0 && (!timePenaltyDisabled)) {
                challengerFinalScore += Math.round(bonusXp);
                Log.e("SCORE", "challengerFinalScore with Bonus " + challengerFinalScore);
            }
            submitRequest.setTotalscore(challengerFinalScore);
            submitRequest.setScores(scoresList);
            submitRequest.setEndTime(TimeUtils.getCurrentDateAsString());
            submitRequest.setStartTime(startDateTime);
            submitRequest.setMobileNum("");
            submitRequest.setDeepLink("");
            submitRequest.setExternal(false);

            submitRequest.setOpponentid("-1");
            submitRequest.setGroupId("");
            submitRequest.setChallengerid(activeGame.getChallengerid());

            /*if (activeGame.getGameType() == GameType.GROUP) {
                submitRequest.setOpponentid("-1");
                submitRequest.setGroupId(activeGame.getGroupId());
            } else if (activeGame.getGameType() == GameType.ACCEPT
                    && (activeGame.getGroupId() != null &&
                    activeGame.getGroupId().length() > 0)) {
                submitRequest.setGroupId(activeGame.getGroupId());
                submitRequest.setOpponentid("-1");
            } else if (activeGame.getGameType() == GameType.ACCEPT) {
                submitRequest.setGroupId("");
                submitRequest.setOpponentid(activeGame.getChallengerid());
            } else if (activeGame.getGameType() == GameType.MYSTERY
                    && !activeGame.getOpponentid().equalsIgnoreCase("mystery")
                    && !activeGame.getChallengerid().equalsIgnoreCase("mystery")) {
                submitRequest.setGroupId("");
                submitRequest.setOpponentid(activeGame.getChallengerid());
            } else if (activeGame.getGameType() == GameType.MYSTERY && activeGame.getOpponentid().equalsIgnoreCase("mystery")) {
                submitRequest.setGroupId("");
                submitRequest.setOpponentid("-1");
            } else {
                submitRequest.setGroupId("");
                submitRequest.setOpponentid(activeGame.getOpponentid());
            }
            if (activeGame.getGameType() == GameType.MYSTERY
                    && submitRequest.getWinner().equalsIgnoreCase("-1")) {
                submitRequest.setOpponentid("-1");
            }
            if (activeGame.getGameType() == GameType.ACCEPT && (activeGame.getGroupId() != null
                    && activeGame.getGroupId().length() > 0)) {
                submitRequest.setChallengerid(activeGame.getChallengerid());
            } else if (activeGame.getGameType() == GameType.ACCEPT) {
                submitRequest.setChallengerid(activeGame.getOpponentid());
            } else if (activeGame.getGameType() == GameType.MYSTERY &&
                    !activeGame.getOpponentid().equalsIgnoreCase("mystery")
                    && !activeGame.getChallengerid().equalsIgnoreCase("mystery")) {
                submitRequest.setChallengerid(activeGame.getOpponentid());
            } else if (activeGame.getGameType() == GameType.MYSTERY && activeGame.getOpponentid().equalsIgnoreCase("mystery")) {
                submitRequest.setChallengerid(activeGame.getChallengerid());
            } else if (activeGame.getGameType() == GameType.CONTACTSCHALLENGE) {
                String uniqueId = activeGame.getOpponentid();
                //String branchLink=view.getBranchLinkForContactChallenge(activeGame,uniqueId,playResponse.getGameId());
                submitRequest.setOpponentid(activeGame.getOpponentid());
                submitRequest.setMobileNum(activeGame.getMobileNum());
                //submitRequest.setDeepLink(branchLink);
                submitRequest.setChallengerid(activeGame.getChallengerid());
                submitRequest.setExternal(true);
            } else {
                submitRequest.setChallengerid(activeGame.getChallengerid());
            }*/

            String gcmToken = getGcmToken();
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitRequest.setDeviceToken(gcmToken);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            //submitRequest.setAssessmentId("" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId());
            submitRequest.setAssessmentId(OustPreferences.get("current_assessmentId"));
            submitRequest.setContentPlayListId(OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId());
            if (courseId != null) {
                submitRequest.setCourseId(courseId);
            }
            if (courseColnId != null) {
                submitRequest.setCourseColnId(courseColnId);
            }
            assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), scoresList, activeGame.getGameid(),
                    challengerFinalScore + "", submitRequest.getWinner(), submitRequest.getEndTime(), submitRequest.getStartTime(),
                    submitRequest.getChallengerid(), submitRequest.getOpponentid(), AssessmentState.COMPLETED);
            saveComplteAssessmentGame(activeUser);
            assessmentPlayResponce.setResumeTime("" + 0);


            Log.d(TAG, "calculateFinalScore: rewardOC->" + rewardOC);
            if (rewardOC > 0) {
                submitRequest.setRewardOC(rewardOC);
            }
//            saveAndSubmitRequest(submitRequest);
            saveAssessmentGame(assessmentPlayResponce, activeUser, submitRequest);
        } catch (Exception e) {
            if (isFromCourse && isMicroCourse) {
                try {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                }
            }
            finish();
        }
    }

    public String findWinner() {
        Log.d(TAG, "findWinner: ");
        String winner = "-1";
        try {
            if (((activeGame.getGameType() == GameType.MYSTERY)
                    && (!activeGame.getOpponentid().equals("Mystery"))) ||
                    (activeGame.getGameType() == GameType.ACCEPT)) {
                if (correctAnswerCount > opponentAnswerCount) {
                    winner = activeGame.getStudentid();
                } else if (correctAnswerCount < opponentAnswerCount) {
                    if (activeUser.getStudentid().equalsIgnoreCase(activeGame.getOpponentid())) {
                        winner = activeGame.getChallengerid();
                    } else {
                        winner = activeGame.getOpponentid();
                    }
                } else if (correctAnswerCount == opponentAnswerCount) {
                    if (opponentFinalScore > challengerFinalScore) {
                        if ((activeGame.getOpponentid().equalsIgnoreCase(activeUser.getStudentid()))) {
                            winner = activeGame.getChallengerid();
                        } else {
                            winner = activeGame.getOpponentid();
                        }
                    } else if (opponentFinalScore < challengerFinalScore) {
                        winner = activeUser.getStudentid();
                    } else {
                        winner = "TIE";
                    }
                }
                return winner;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return winner;
    }


    public void saveAndSubmitRequest(SubmitRequest submitRequest) {
        Log.d(TAG, "saveAndSubmitRequest: ");
        try {
            Gson gson = new GsonBuilder().create();
            if (!OustAppState.getInstance().isAssessmentGame()) {
                OustPreferences.save("lastgamesubmitrequest", gson.toJson(submitRequest));
            }
            if (!OustSdkTools.checkInternetStatus()) {
                submitRequestProcessFinish(null);
                return;
            }
            if ((isEvent && OustStaticVariableHandling.getInstance().getOustApiListener() != null) || isFromCourse) {
                userEventAssessmentData.setAssessmentId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                int answered = playResponse.getqIdList().size();

                userEventAssessmentData.setnQuestionAnswered(answered);
                userEventAssessmentData.setnTotalQuestions(answered);

                userEventAssessmentData.setnQuestionWrong(nWrong);
                userEventAssessmentData.setnQuestionCorrect(nCorrect);
                userEventAssessmentData.setnQuestionSkipped((answered - (nCorrect + nWrong) > 0) ? answered - (nWrong + nCorrect) : 0);

                userEventAssessmentData.setScore(submitRequest.getTotalscore());
                userEventAssessmentData.setEventId(eventId);
                userEventAssessmentData.setUserProgress("Completed");
                try {
                    userEventAssessmentData.setUserCompletionPercentage(100);
                    userEventAssessmentData.setCompletionDate("" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                int passingMark = (int) OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage();
                int actualPassing = ((nCorrect * 100) / (nCorrect + nWrong));
                boolean assessmentPass = false;
                if (actualPassing >= passingMark) {
                    assessmentPass = true;
                }
                userEventAssessmentData.setPassed(assessmentPass);

                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed(assessmentPass);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                    }

                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                    }
                }

                if (!isFromCourse) {
                    if (isCplModule) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setCurrentModuleType("Assessment");
                        userEventCplData.setEventId(eventId);
                        userEventCplData.setCurrentModuleId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                        userEventCplData.setnTotalModules(totalModules);

                        if ((completedModules + 1) >= totalModules) {
                            userEventCplData.setCompletedOn("" + System.currentTimeMillis());
                            userEventCplData.setUserProgress("Completed");
                            userEventCplData.setnModulesCompleted(totalModules);
                        } else {
                            userEventCplData.setnModulesCompleted(completedModules + 1);
                            userEventCplData.setUserProgress("InProgress");
                        }

                        userEventCplData.setCurrentModuleProgress("Completed");
                        userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                        OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                    } else {
                        OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                    }
                } else {
                    userEventAssessmentData.setEventId(0);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);
                }
            }

            List<Scores> scoresTest = getAllScores(submitRequest.getScores());
            submitRequest.setScores(scoresTest);
            submitScore(submitRequest);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<Scores> getAllScores(List<Scores> scores) {
        Log.d(TAG, "getAllScores: ");
        List<Scores> test = new ArrayList<>();
        if (scores != null) {
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) != null && scores.get(i).getQuestion() != 0)
                    test.add(scores.get(i));
            }
            return test;
        }
        return null;
    }

    public void submitScore(SubmitRequest submitRequest) {
        Log.d(TAG, "submitScore: ");

        final SubmitResponse[] submitResponses = {null};
        try {
            gameSubmitRequestSent = true;
            resetExoPlayer();
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            ShowPopup.getInstance().showProgressBar(NewAssessmentBaseActivity.this, getResources().getString(R.string.calculating_score));
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            try {
                long cplId = OustPreferences.getTimeForNotification("cplId_assessment");
                if (cplId == 0) {
                    if (isCplModule) {
                        cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                    }
                }
                parsedJsonParams.put("contentPlayListId", cplId);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);
            Log.d(TAG, "submitScore: " + parsedJsonParams.toString());
            Log.d(TAG, "submitScore: URL:" + submitGameUrl);


            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ShowPopup.getInstance().dismissProgressDialog();
                    if (response != null) {
                        Log.e("assess response", response.toString());
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitRequestProcessFinish(submitResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    submitResponses[0] = null;
                    ShowPopup.getInstance().dismissProgressDialog();
                    submitRequestProcessFinish(submitResponses[0]);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void clearSubmitRequestSaved() {
        OustPreferences.save("lastgamesubmitrequest", "");
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        Log.d(TAG, "submitRequestProcessFinish: ");
        if ((null != submitResponse) && (submitResponse.isSuccess())) {
            clearSubmitRequestSaved();
            if (OustAppState.getInstance().isAssessmentGame()) {
                assessmentPlayResponce.setAssessmentState(AssessmentState.SUBMITTED);
                assessmentPlayResponce.setResumeTime("" + 0);
                assessmentPlayResponce.setQuestionIndex(0);
                //saveAssessmentGame(assessmentPlayResponce, activeUser);
                deleteComplteAssessmentGame(activeUser);
            }
        }
        OustAppState.getInstance().setPlayResponse(this.playResponse);
        answerProcessFinish(activeGame, activeUser, submitRequest, gamePoints);
    }

    public void answerProcessFinish(ActiveGame activeGame, ActiveUser activeUser, SubmitRequest submitRequest, GamePoints gamePoints) {
        try {
            Log.d(TAG, "answerProcessFinish: ");
            //oustRestClient.cancelAllRequests();
            activeGame.setRematch(false);
            OustAppState.getInstance().setHasPopup(false);
            //disableAcceptContactChallenge();
            Gson gson = new GsonBuilder().create();
            Intent intent;
//            if(OustAppEvents.getInstance().isEventGame()){
//                intent = new Intent(QuestionActivity.this, EventResultActivity.class);
//            }else
            if (OustAppState.getInstance().isAssessmentGame()) {
                totaltimetaken = 0;
                for (int i = 0; i < scoresList.size(); i++) {
                    totaltimetaken += scoresList.get(i).getTime();
                }

                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_NEW_ASSESSMENT_UI)) {
                    intent = new Intent(NewAssessmentBaseActivity.this, AssessmentCompleteScreen.class);
                } else {
                    intent = new Intent(NewAssessmentBaseActivity.this, AssessmentResultActivity.class);
                }
                intent.putExtra("containCertificate", containCertificate);
                intent.putExtra("totalTimeTaken", totaltimetaken);
                intent.putExtra("isCplModule", isCplModule);
                intent.putExtra("IS_FROM_COURSE", isFromCourse);
                intent.putExtra("isSurveyFromCourse", isSurveyFromCourse);
                intent.putExtra("mappedCourseId", mappedCourseId);
                intent.putExtra("courseAssociated", courseAssociated);
                intent.putExtra("showAssessmentResultScore", showAssessmentResultScore);
                intent.putExtra("reAttemptAllowed", reAttemptAllowed);
                intent.putExtra("nAttemptCount", nAttemptCount + 1);
                intent.putExtra("nAttemptAllowedToPass", nAttemptAllowedToPass);
                intent.putExtra("assessmentId", Long.parseLong(OustPreferences.get("current_assessmentId")));
                intent.putExtra("isMicroCourse", isMicroCourse);

                if ((courseId != null) && (!courseId.isEmpty())) {
                    intent.putExtra("courseId", courseId);
                }
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColnId);
                }
            } else {
                intent = new Intent(NewAssessmentBaseActivity.this, ResultActivity.class);
            }

            intent.putExtra("isComingFromCpl", isComingFromCpl);
            intent.putExtra("surveyAssociated", surveyAssociated);
            intent.putExtra("surveyMandatory", surveyMandatory);
            intent.putExtra("mappedSurveyId", mappedSurveyId);
            intent.putExtra("currentCplId", currentCplId);
            intent.putExtra("isMultipleCplModule", isMultipleCplModule);
            intent.putExtra("ActiveUser", gson.toJson(activeUser));
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("GamePoints", gson.toJson(gamePoints));
            intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
            intent.putExtra("ShouldMusicPlay", true);
            startActivity(intent);
            NewAssessmentBaseActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void deleteComplteAssessmentGame(ActiveUser activeUser) {
        Log.d(TAG, "deleteComplteAssessmentGame: ");
        try {
            String node = "/userAssessmentProgress/" + "/completedAssessments/" + activeUser.getStudentKey() + "/assessment" +
                    OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            OustFirebaseTools.getRootRef().child(node).setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String getGcmToken() {
        return OustPreferences.get("gcmToken");
    }

    public void saveComplteAssessmentGame(ActiveUser activeUser) {
        Log.d(TAG, "saveComplteAssessmentGame: ");
        try {
            String node = "/userAssessmentProgress/" + "/completedAssessments/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if (isMultipleCplModule) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + currentCplId + "/contentListMap/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            }
            OustFirebaseTools.getRootRef().child(node).setValue("true");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setGameSubmitRequestSent(boolean gameSubmitRequestSent) {
        Log.d(TAG, "setGameSubmitRequestSent: ");
        this.gameSubmitRequestSent = gameSubmitRequestSent;
    }

    private long localquestiontime = 0;

    public void startTimer(long quesTime) {
        try {
            Log.d(TAG, "startTimer: ");
            timeExceeded = false;
            OustSdkTools.setLayoutBackgroud(txtTimerTextView, R.drawable.roundedcorner);
            txtTimerTextView.setTextColor(getResources().getColor(R.color.Orange));
            localquestiontime = 0;
            timer = new CounterClass(Integer.parseInt(quesTime + "" + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cancelTimer() {
        try {
            if (null != timer)
                timer.cancel();

            stopAnimation(txtTimerTextView);
            txtTimerTextView.setText("00:00");

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean timeExceeded = false;


    @Override
    public void hideToolbar() {
        header_ll.setVisibility(View.GONE);
    }

    @Override
    public void onVideoEnd() {
        if (resumeTime > 0) {
            startTimer(resumeTime);
            resumeTime = 0;
        } else {
            startTimer(questions.getMaxtime());
        }
    }

    @Override
    public void showToolbar() {
        header_ll.setVisibility(View.VISIBLE);
    }

    @Override
    public void fullHideToolbar() {

    }

    @Override
    public void fullShowToolbar() {

    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                Log.d(TAG, "onFinish: ");
                localquestiontime++;
                localquestiontime = questions.getMaxtime();

                txtTimerTextView.setText("00:00");
                stopAnimation(txtTimerTextView);

                /*if (((questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) || (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) ||
                        (questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)))) {*/

                Log.d(TAG, "onFinish: " + questions.isMediaUploadQues() + " --- category: " + ((questions.getQuestionCategory() != null) ? questions.getQuestionCategory() : "null") + " --- type:" + ((questions.getQuestionType() != null) ? questions.getQuestionType() : "null"));
                Log.d(TAG, "onFinish: qType:" + questionType + "qCategory:" + questionCategory);
                if (questions.isMediaUploadQues()) {
                    onTimeOutForMedia();
                } else if (!OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                    Log.d(TAG, "onFinish: questiontype->" + ((questions.getQuestionType() != null) ? "" + questions.getQuestionType() : "null"));
                    if (questions.getQuestionType() != null && questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex);
                        if (frag != null) {
                            LearningPlayFragment learningPlayFragment = (LearningPlayFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex));
                            if (questions.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE)) {
                                learningPlayFragment.calculateMrqImageOc(true, true, false);
                            } else {
                                learningPlayFragment.calculateMrqTextOc(true, true, false);
                            }
                        }
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex);
                        if (frag != null) {
                            LearningPlayFragment learningPlayFragment = (LearningPlayFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex));
                            learningPlayFragment.longAnswerTimeOut();
                        }
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.MATCH)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
                        if (frag != null) {
                            LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
                            learningPlayFragment.optionSelected(null, -1, true);
                        }

                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.CATEGORY)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
                        if (frag != null) {
                            LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
                            learningPlayFragment.calculatepoints(true);
                        }

//                }else if (questions.getQuestionType()!=null && questions.getQuestionType().equalsIgnoreCase(QuestionType.FILL)) {
//                    Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
//                    if (frag != null) {
//                        LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
//                        learningPlayFragment.calculateXp(true);
//                    }

                    } else if (questions.getQuestionType() != null && questions.getQuestionType().equalsIgnoreCase(QuestionType.FILL)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
                        if (frag != null) {
                            LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
                            learningPlayFragment.fillType1Timeout();
                        }

                    } else if (questions.getQuestionType() != null && questions.getQuestionType().equalsIgnoreCase(QuestionType.FILL_1)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
                        if (frag != null) {
                            LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
                            learningPlayFragment.checkToSubmitWordBank(true);
                        }

                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.HOTSPOT)) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningplaynew_frag");
                        if (frag != null) {
                            LearningPlayFragmentNew learningPlayFragment = (LearningPlayFragmentNew) (getSupportFragmentManager().findFragmentByTag("learningplaynew_frag"));
                            learningPlayFragment.calculateHotspotPoints(true);
                        }
                    } else if (questions.getQuestionType() != null &&
                            (questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex);
                        if (frag != null) {
                            LearningPlayFragment learningPlayFragment = (LearningPlayFragment) (getSupportFragmentManager().findFragmentByTag("learningPlay_frag" + questionIndex));
                            String selectedMCQAnswer = learningPlayFragment.getSelectedAnswer();
                            if ((selectedMCQAnswer != null) && (!selectedMCQAnswer.isEmpty())) {
                                if ((questions.getAnswer() != null) && (questions.getAnswer().equalsIgnoreCase(selectedMCQAnswer))) {
                                    learningPlayFragment.waitAndGo(selectedMCQAnswer, true);
                                } else {
                                    learningPlayFragment.waitAndGo(selectedMCQAnswer, false);
                                }
                            } else {
                                calculateQuestionScore("");
                            }
                        }
                    } else {
                        calculateQuestionScore("");
                    }
                } else {
                    calculateQuestionScore("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        public void onTimeOutForMedia() {
            try {
                Log.d(TAG, "onTimeOutForMedia: ");
                OustStaticVariableHandling.getInstance().setLearningShareClicked(false);
                OustSdkTools.isReadMoreFragmentVisible = false;
                Fragment mediaFrag = getSupportFragmentManager().findFragmentByTag("media_upload");
                if (mediaFrag != null) {
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                        MediaUploadForKitKatFragment mediafragmentbelowlolly = (MediaUploadForKitKatFragment) (getSupportFragmentManager().findFragmentByTag("media_upload"));
                        mediafragmentbelowlolly.onTimeOut();
                    } else {
                        MediaUploadQuestionFragment mediafragmentabovelolly = (MediaUploadQuestionFragment) (getSupportFragmentManager().findFragmentByTag("media_upload"));
                        mediafragmentabovelolly.onTimeOut();
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
            try {
                localquestiontime++;
                millis = millisUntilFinished;
                long timeLeft = questions.getMaxtime() * 1000;
//            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                /*String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));*/

                String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                txtTimerTextView.setText(hms);

                if (!timeExceeded && millis < ((timeLeft * 20) / 100)) {
                    timeExceeded = true;
                    txtTimerTextView.setTextColor(getResources().getColor(R.color.redbottoma));
                    OustSdkTools.setLayoutBackgroud(txtTimerTextView, R.drawable.red_assess_timer_background);
                    animateView(txtTimerTextView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            //assessmenttimer.setText(hms);
        }
    }

    private long millis;

    private void playAudio(final String filename, final boolean isPlayingBackground) {

        try {
            if (!isMusicStop) {
                File tempMp3 = new File(getFilesDir(), filename);
                prepareExoPlayerFromFileUri(Uri.fromFile(tempMp3));

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onChange(String status) {
    }

    private AssessmentPlayResponse getAssessmentPlayResp(String assessmentResp) {
        Log.d(TAG, "getAssessmentPlayResp: ");
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(assessmentResp, AssessmentPlayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
        }
    }

    public void saveAssessmentForEachQuestion() {
        Log.d(TAG, "saveAssessmentForEachQuestion: ");
        try {
            assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex + "", scoresList,
                    activeGame.getGameid(), challengerFinalScore + "", AssessmentState.INPROGRESS);
            assessmentPlayResponce.setResumeTime("" + 0);
            saveAssessmentGame(assessmentPlayResponce, activeUser, null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveAssessmentGame(AssessmentPlayResponse userPlayResponse, ActiveUser activeUser, SubmitRequest submitRequest) {
        Log.d(TAG, "saveAssessmentGame: ");
        try {
           /* String node = "";
            node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if (isMultipleCplModule) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + currentCplId + "/contentListMap/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            }else {
                if ((courseId != null) && (!courseId.isEmpty())) {
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/collection/" + courseColnId + "/course/" + courseId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                    } else {
                        node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/course/" + courseId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                    }
                } else {
                    if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                        node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/collection/" + courseColnId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                    }
                }
            }

            Log.d(TAG, "saveAssessmentGame: node:" + node);
            OustFirebaseTools.getRootRef().child(node).setValue(new AssessmentCopyResponse(assessmentPlayResponce), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.i("error", "firebase error");
                    if (databaseError != null) {
                        if (databaseError.getMessage() != null) {
                            Log.i(TAG, "onComplete: " + databaseError.getMessage());
                        }
                        if (databaseError.getDetails() != null) {
                            Log.d(TAG, "onComplete: " + databaseError.getDetails());
                        }
                    }
                }
            });
*/
            userPlayResponse.setStartTime(startDateTime);
            userPlayResponse.setChallengerid(userPlayResponse.getStudentId());
            if (OustAppState.getInstance().getAssessmentFirebaseClass() != null && OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() > 0) {
                userPlayResponse.setTotalQuestion((int) OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions());
            }
            if ((courseId != null) && (!courseId.isEmpty())) {
                userPlayResponse.setCourseId(Long.parseLong(courseId));
            } else if (isMultipleCplModule && OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId() != 0) {
                userPlayResponse.setContentPlayListId(OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId());
            }

            String update_userAssessment_url = OustSdkApplication.getContext().getResources().getString(R.string.update_userAssessment_url) + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            update_userAssessment_url = HttpManager.getAbsoluteUrl(update_userAssessment_url);

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(userPlayResponse);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);


            ApiCallUtils.doNetworkCall(Request.Method.POST, update_userAssessment_url, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            if (response.optBoolean("success")) {
                                Log.e("AssessmentProgress", "Response is success");
                            } else {
                                Log.e("AssessmentProgress", "Response is error");
                            }
                        } else {
                            Log.e("AssessmentProgress", "Response is null");
                        }
                        if (submitRequest != null) {
                            saveAndSubmitRequest(submitRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        if (submitRequest != null) {
                            saveAndSubmitRequest(submitRequest);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("AssessmentProgress", "request or api error");
                    if (submitRequest != null) {
                        saveAndSubmitRequest(submitRequest);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (submitRequest != null) {
                saveAndSubmitRequest(submitRequest);
            }
        }
    }

    @Override
    public void gotoNextScreen() {
        try {
            Log.d(TAG, "gotoNextScreen: ");
            saveAssessmentForEachQuestion();
            cancelTimer();

            if (questionIndex == (noofQuesinGame - 1)) {
                calculateFinalScore();
                return;
            } else {
                questionIndex++;
                startTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void gotoPreviousScreen() {
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

    private float totalTimeForAssessment = 0;
    private float totaltimetaken = 0;

    private int nCorrect = 0;
    private int nWrong = 0;

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            Scores scores = new Scores();
            scores.setAnswer((userAns));
            scores.setCorrect(status);
            scores.setXp(0);
            if (oc < 0) {
                oc = 0;
            }
            if (status) {
                nCorrect++;
            } else {
                nWrong++;
            }
            Log.e("SCORE", "userAns " + userAns);
            Log.e("SCORE", "challengerScore " + oc);
            challengerFinalScore += oc;
            Log.e("SCORE", "challengerFinalScore " + challengerFinalScore);
            Log.e("SCORE", "Time Taken " + localquestiontime);

            scores.setScore(oc);
            scores.setQuestion((int) questions.getQuestionId());

            /*if(questions.getQuestionCategory()!=null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)){
                scores.setQuestionType(QuestionType.UPLOAD_AUDIO);
            }else if(questions.getQuestionCategory()!=null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)){
                scores.setQuestionType(QuestionType.UPLOAD_IMAGE);
            }else if(questions.getQuestionCategory()!=null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)){
                scores.setQuestionType(QuestionType.UPLOAD_VIDEO);
            }else {
                scores.setQuestionType(questions.getQuestionType());
            }*/

            scores.setQuestionType(questions.getQuestionType());
            scores.setQuestionSerialNo((questionIndex + 1));
            scores.setUserSubjectiveAns(subjectiveResponse);
            // add in array
            scores.setTime(localquestiontime);
            scoresList.set(questionIndex, scores);

            Log.e("SCORE", "setAnswerAndOc: questionType:" + questions.getQuestionType());
            //Log.e("SCORE", "setAnswerAndOc: questionType:"+questions.getQuestionType() );

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
        cancelTimer();
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

    private long challengerScore = 0;
    private String questionType, questionCategory;
    private int correctAnswerCount = 0, wrongAnswerCount = 0;

    public void calculateQuestionScore(String userAnswer) {
        try {
            Scores scores = new Scores();
            scores.setAnswer(userAnswer);
            scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());


            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));

//            answeredSeconds = questions.getMaxtime() - answeredSeconds;
            // add in array
            if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                    || (questionType.equals(QuestionType.SURVEY))) {
            } else {
                challengerScore = 0;
                scores.setScore(challengerScore);
                wrongAnswerCount++;
//                }

                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
                challengerFinalScore += challengerScore;
                //view.setChallengerScoreText(challengerFinalScore);
            }
            scores.setTime(localquestiontime);
            scores.setXp(0);
            scoresList.set(questionIndex, scores);

            saveAssessmentForEachQuestion();
            cancelTimer();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        questionIndex++;
        startTransaction();
    }

    @Override
    public void onBackPressed() {
        cancelGame();
    }

    public void cancelGame() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopUp(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            if (OustAppState.getInstance().isAssessmentGame()) {
                popupContent.setText(getResources().getString(R.string.assessment_paused_msg));
                popupTitle.setText(getResources().getString(R.string.warning));
            } else {
                popupContent.setText(getResources().getString(R.string.cancel_game_confirmation));
                popupTitle.setText(getResources().getString(R.string.cancel_game));
            }
            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    cancelTimer();
                    stopMusicPlay();
                    gameSubmitRequestSent = true;
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        submitGame();
                    } else {
                        if (isFromCourse && isMicroCourse) {
                            try {
                                OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                            }
                        }
                        finish();
                    }

                    if (assessmentPlayResponce != null && OustAppState.getInstance().getAssessmentFirebaseClass() != null) {
                        int percent = (int) (((float) assessmentPlayResponce.getQuestionIndex() / (float) OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions()) * 100);
                        if (percent != 0) {
                            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(percent);
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                }

                                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(percent);
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                }
                            }
                        }
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });


        } catch (Exception e) {
            Log.d("POpup exception", e.getMessage() + "");
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private boolean isMusicStop = false;

    public void stopMusicPlay() {
        try {
            isQuestionAudio = true;
            isMusicStop = true;
            resetExoPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        OustPreferences.saveAppInstallVariable("IsAssessmentPlaying", false);

        stopMusicPlay();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        stopMusicPlay();
        resetExoPlayer();
        super.onStop();
    }

    private void getSignedUrl(String audio) {
        audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + audio;
        playQuesAudio(audio);
    }

    private void playQuesAudio(final String signedPath) {
        prepareExoPlayerFromUri(Uri.parse(signedPath));
    }

    private SimpleExoPlayer exoPlayer;

    private void prepareExoPlayerFromUri(Uri uri) {
        isRepeatAudio = false;
        resetExoPlayer();

        /*TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();*/

        exoPlayer = new SimpleExoPlayer.Builder(NewAssessmentBaseActivity.this).build();
        //exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
        //MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.addListener(eventListener);
        exoPlayer.setMediaSource(audioSource);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
        //initMediaControls();
    }

    private void prepareExoPlayerFromFileUri(Uri uri) {
        isRepeatAudio = true;
        resetExoPlayer();

        exoPlayer = new SimpleExoPlayer.Builder(NewAssessmentBaseActivity.this).build();
        //exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), new DefaultLoadControl());
        exoPlayer.addListener(eventListener);
        DataSpec dataSpec = new DataSpec(uri);
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

        MediaSource audioSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));
        //MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, new DefaultExtractorsFactory(), null, null);
        exoPlayer.setMediaSource(audioSource);
        exoPlayer.prepare();
    }

    private void resetExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.removeListener(eventListener);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    //private String TAG="exoplayer";
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {


        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG, "Playback ended!");
                    if (exoPlayer != null && isRepeatAudio) {
                        exoPlayer.seekTo(0);
                    }
                    break;
                case ExoPlayer.STATE_READY:
                    Log.i(TAG, "ExoPlayer ready!");
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
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
            Log.i(TAG, "onPlaybackError: " + error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}