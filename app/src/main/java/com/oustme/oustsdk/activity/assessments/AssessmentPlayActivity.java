package com.oustme.oustsdk.activity.assessments;

import static com.android.volley.Request.Method.GET;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.customviews.TryRippleView;
import com.oustme.oustsdk.data.handlers.impl.MediaDataDownloader;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.request.AssessmentCreateGameRequest;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.ConfirmAssessmentOtpRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.request.SendAssessmentOtpRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.QuestionResponse;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.service.DownLoadFilesIntentService;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class AssessmentPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AssessmentPlayActivity";
    private RelativeLayout assessment_mainlayout, popupforeground, top_layout,
            assessmentquestion_downloadpopup, starting_loader;
    private ImageView assessmentpopup_mainimg, share_imageview, assessment_bgImg, leaderboard_imageview,
            assessmentpopup_review_imageview;
    private TextView assessmentpopup_title, assessmentpopup_content,
            assessmentpopup_scope, assessmentpopup_totalques, total_ques_text, assessmentpopup_totlepeople,
            assessmentpopup_compltetext, assessmentpopup_score, assessmentpopup_attempte, scope_text,
            assessmentpopup_wrong, assessmentpopup_skipped, participants_text,
            download_progresstext, downloadassessment_text, ap_rules_heading, waittimer_text, waittimer_title;
    private TextView assessmentpopup_starttest, assessmentpopup_review_text, download_header_text;
    private TextView assessmentpopup_leaderboardbtn, userright_ans, rightCenter, userwrong_ans, wrongCenter,
            userskipvalue, skipValueCenter, completion_percentage, completion_text, assessmentover_message;
    private ImageButton assessmentpopup_oustbtn;
    private EditText passcode_edittext;
    private LinearLayout passcode_layout, assessmentmainlayout, starttextlayout, btnlayout,
            timer_layout, assessment_question_result, completion_layout, assessover_msg_layout,
            assessment_extra_info_ll, assessmentpopup_review_layout;
    private SwipeRefreshLayout popuprefresher;
    private TryRippleView assesmentPopup_leader_rippleView, s_rippleView, assesmenPopup_start_rippleView,
            assessmentpopup_review_rippleView;
    private ProgressBar download_progressbar;

    private PopupWindow questionDownloadPopup;
    private boolean comingFormOldLandingPage = false;
    private String courseId, courseColnId;
    private boolean containCertificate;
    private boolean isMultipleCplEnable = false;
    private long isCplId;

    private boolean isEnrolled;
    private boolean shouldGotoQuestionView = true;

    private ActiveGame activeGame;
    private ActiveUser activeUser;
    private PlayResponse playResponse;
    private AssessmentPlayResponse assessmentPlayResponce;
    private AssessmentFirebaseClass assessmentFirebaseClass;


    private boolean isComingFromBranchIO = false;
    private String assessmentId;
    private boolean sendOtpRequest = false;
    private boolean isComingFromCPL = false;
    private long startTime = 0;
    private boolean isFromCourse;
    private DownloadResultReceiver DownloadResultReceiver;
    private boolean downloadingQuestion;
    private boolean isActivityResumed;
    private boolean isCourseAttached;
    private long mappedCourseID;
    private boolean isMappedCourseDistributed;
    private boolean isScoreDisplaySecondTime;

    private boolean isEvent;
    private int eventId = 0;
    private UserEventAssessmentData userEventAssessmentData;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.assessmentpopup);
        try {
            OustSdkTools.setLocale(AssessmentPlayActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setContentView(R.layout.activity_assessmentplay);
        //OustSdkTools.initializeRealm(AssessmentPlayActivity.this);

        OustSdkApplication.setmContext(AssessmentPlayActivity.this);
        initViews();
        initPlayAssesmentFragment();

        try {
            Log.d(TAG, "onCreate: nModulesCompleted: " + OustPreferences.getTimeForNotification("cplCompletedModules"));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
//        OustGATools.getInstance().reportPageViewToGoogle(AssessmentPlayActivity.this, "Assessment Landing Page");
    }

    @Override
    protected void onResume() {
        try {
            isReviewAnswerButtonClicked = false;
            downloadQuestionNo = 0;
            isActivityResumed = true;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {
            super.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityResumed = false;
    }

    private void initViews() {
        try {
            Log.d(TAG, "initViews: ");
            assessmentpopup_mainimg = findViewById(R.id.assessmentpopup_mainimg);
            assessmentpopup_leaderboardbtn = findViewById(R.id.assessmentpopup_leaderboardbtn);
            assessmentpopup_oustbtn = findViewById(R.id.assessmentpopup_oustbtn);
            assessmentpopup_title = findViewById(R.id.assessmentpopup_title);
            assessmentpopup_content = findViewById(R.id.assessmentpopup_content);
            assessmentpopup_scope = findViewById(R.id.assessmentpopup_scope);
            scope_text = findViewById(R.id.scope_text);
            assessmentpopup_totalques = findViewById(R.id.assessmentpopup_totalques);
            total_ques_text = findViewById(R.id.total_ques_text);
            assessmentpopup_totlepeople = findViewById(R.id.assessmentpopup_totlepeople);
            participants_text = findViewById(R.id.participants_text);
            passcode_edittext = findViewById(R.id.passcode_edittext);
            passcode_layout = findViewById(R.id.passcode_layout);
            assessmentpopup_starttest = findViewById(R.id.assessmentpopup_starttest);
            assessmentmainlayout = findViewById(R.id.assessmentmainlayout);
//            assessmentpopup_compltetext = (TextView) findViewById(R.id.assessmentpopup_compltetext);
            assessment_mainlayout = findViewById(R.id.assessment_mainlayout);
//            assessmentpopup_score = (TextView) findViewById(R.id.assessmentpopup_score);
//            starttextlayout = (LinearLayout) findViewById(R.id.starttextlayout);
//            assessmentpopup_attempte = (TextView) findViewById(R.id.assessmentpopup_attempte);
            //assessmentpopup_right = (TextView) findViewById(R.id.assessmentpopup_right);
//            assessmentpopup_wrong = (TextView) findViewById(R.id.assessmentpopup_wrong);
//            assessmentpopup_skipped = (TextView) findViewById(R.id.assessmentpopup_skipped);
            assessment_extra_info_ll = findViewById(R.id.assessment_extra_info_ll);
            completion_layout = findViewById(R.id.completion_layout);
            completion_percentage = findViewById(R.id.completion_percentage);
            completion_text = findViewById(R.id.completion_text);

            assessover_msg_layout = findViewById(R.id.assessover_msg_layout);
            assessmentover_message = findViewById(R.id.assessmentover_message);

            assessmentpopup_review_layout = findViewById(R.id.assessmentpopup_review_layout);
            assessmentpopup_review_imageview = findViewById(R.id.assessmentpopup_review_imageview);
            assessmentpopup_review_text = findViewById(R.id.assessmentpopup_review_text);
            assessmentpopup_review_rippleView = findViewById(R.id.assessmentpopup_review_rippleView);

            //scorelayout = (LinearLayout) findViewById(R.id.scorelayout);
            share_imageview = findViewById(R.id.share_imageview);
            assessment_bgImg = findViewById(R.id.assessment_bgImg);
            starting_loader = findViewById(R.id.starting_loader);
            popuprefresher = findViewById(R.id.popuprefresher);
            popupforeground = findViewById(R.id.popupforeground);
            top_layout = findViewById(R.id.top_layout);
            assesmentPopup_leader_rippleView = findViewById(R.id.assesmentPopup_leader_rippleView);
            s_rippleView = findViewById(R.id.assesmenPopup_start_rippleView);
            assessmentquestion_downloadpopup = findViewById(R.id.assessmentquestion_downloadpopup);
            download_progressbar = findViewById(R.id.download_progressbar);
            download_progresstext = findViewById(R.id.download_progresstext);
            downloadassessment_text = findViewById(R.id.downloadassessment_text);
            waittimer_text = findViewById(R.id.waittimer_text);
            waittimer_title = findViewById(R.id.waittimer_title);
            assesmenPopup_start_rippleView = findViewById(R.id.assesmenPopup_start_rippleView);
            btnlayout = findViewById(R.id.btnlayout);
            ap_rules_heading = findViewById(R.id.ap_rules_heading);
            download_header_text = findViewById(R.id.download_header_text);
            assessment_question_result = findViewById(R.id.assessment_question_result);
            skipValueCenter = findViewById(R.id.skipValueCenter);
            userskipvalue = findViewById(R.id.userskipvalue);
            wrongCenter = findViewById(R.id.wrongCenter);
            userwrong_ans = findViewById(R.id.userwrong_ans);
            rightCenter = findViewById(R.id.rightCenter);
            userright_ans = findViewById(R.id.userright_ans);

            rightCenter.setText(R.string.right);
            wrongCenter.setText(R.string.wrong);
            skipValueCenter.setText(getResources().getString(R.string.points_text));
            completion_text.setText(getResources().getString(R.string.complete));
            assessmentpopup_review_text.setText(getResources().getString(R.string.answer));

            leaderboard_imageview = findViewById(R.id.leaderboard_imageview);
            OustSdkTools.setImage(leaderboard_imageview, getResources().getString(R.string.leaderboard_new_icon));
            share_imageview.setVisibility(View.VISIBLE);
            //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.assessment_resume_icon));
            share_imageview.setImageResource(R.drawable.ic_play_without_circle);

            ap_rules_heading.setText(getResources().getString(R.string.assessment_rules));

            timer_layout = findViewById(R.id.timer_layout);

            waittimer_title.setText(getResources().getString(R.string.assessment_starts_in));
            OustSdkTools.setImage(assessmentpopup_mainimg, getResources().getString(R.string.mydesk));
            OustSdkTools.setImage(assessmentpopup_review_imageview, getResources().getString(R.string.assessment_answer_icon));
            assessmentpopup_oustbtn.setOnClickListener(this);
            assessmentpopup_starttest.setOnClickListener(this);
            assesmentPopup_leader_rippleView.setOnClickListener(this);
            assessmentpopup_review_rippleView.setOnClickListener(this);
            assesmenPopup_start_rippleView.setOnClickListener(this);
            btnlayout.setVisibility(View.VISIBLE);

            DownloadResultReceiver = new DownloadResultReceiver(new Handler(getMainLooper()));

//            OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
        } catch (Exception e) {
        }
    }

    private boolean isCplModule;

    public void initPlayAssesmentFragment() {
        try {
            Log.d(TAG, "initPlayAssesmentFragment: ");
            OustSdkTools.speakInit();
            OustSdkTools.setSnackbarElements(assessmentmainlayout, AssessmentPlayActivity.this);
            Intent CallingIntent = getIntent();
            initData();
            activeGame = setGame(activeUser);
            activeGame.setIsLpGame(false);
            assessmentId = CallingIntent.getStringExtra("assessmentId");
            isComingFromCPL = CallingIntent.getBooleanExtra("isComingFromCpl", false);
            courseId = CallingIntent.getStringExtra("courseId");
            isCplId = CallingIntent.getLongExtra("currentCplId", 0);
            isMultipleCplEnable = CallingIntent.getBooleanExtra("isMultipleCplModule", false);

//            isMultipleCplEnable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_MULTIPLE_CPL);

            courseColnId = CallingIntent.getStringExtra("courseColnId");
            isCplModule = CallingIntent.getBooleanExtra("isCplModule", false);
            containCertificate = CallingIntent.getBooleanExtra("containCertificate", false);
            isFromCourse = CallingIntent.getBooleanExtra("IS_FROM_COURSE", false);
            if ((assessmentId != null) && (!assessmentId.isEmpty())) {
                isComingFromBranchIO = true;
                OustPreferences.save("current_assessmentId", assessmentId);
            }
            if (isCplModule) {
                assessment_extra_info_ll.setVisibility(View.GONE);
            } else {
                assessment_extra_info_ll.setVisibility(View.VISIBLE);
            }

            userEventAssessmentData = new UserEventAssessmentData();
            userEventAssessmentData.setAssessmentId(Long.parseLong(assessmentId));

            if (CallingIntent.hasExtra("isEventLaunch")) {
                isEvent = CallingIntent.getBooleanExtra("isEventLaunch", false);
                eventId = CallingIntent.hasExtra("eventId") ? CallingIntent.getIntExtra("eventId", 0) : 0;
            }
            userEventAssessmentData.setEventId(eventId);

            /*if(!isCplModule && isEvent && OustStaticVariableHandling.getInstance().getOustApiListener()!=null){
                OustStaticVariableHandling.getInstance().getOustApiListener().onOustContentLoaded();
            }*/

            comingFormOldLandingPage = CallingIntent.getBooleanExtra("comingFormOldLandingPage", false);
            setLayoutAspectRatiosmall();
            showStartingLoader();
            fetchAssessmentsFromFirebase(assessmentId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public ActiveGame setGame(ActiveUser activeUser) {
        ActiveGame activeGame = new ActiveGame();
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
        activeGame.setGroupId("");
        activeGame.setLevel(activeUser.getLevel());
        activeGame.setLevelPercentage(activeUser.getLevelPercentage());
        activeGame.setWins(activeUser.getWins());
        activeGame.setIsLpGame(false);
        return activeGame;
    }

    private void initData() {
        Log.d(TAG, "initData: ");
        activeUser = OustAppState.getInstance().getActiveUser();
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            this.activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        }
        shouldGotoQuestionView = true;
        OustAppState.getInstance().setAssessmentGame(true);
        OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(false);
    }

    //set banner size
    private void setLayoutAspectRatiosmall() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) top_layout.getLayoutParams();
            float h = (scrWidth * 0.34f);
            int h1 = (int) h;
            params.height = h1;
            top_layout.setLayoutParams(params);
        } catch (Exception e) {
        }
    }

    //show swipr refreshlayout as loader
    public void showStartingLoader() {
        try {
            Log.d(TAG, "showStartingLoader: ");
            starting_loader.setVisibility(View.VISIBLE);
            popuprefresher.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen), OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            popuprefresher.post(new Runnable() {
                @Override
                public void run() {
                    popuprefresher.setRefreshing(true);
                }
            });
            popupforeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideStartingLoader() {
        try {
            starting_loader.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void showStartingLoaderAgain() {
        try {
            starting_loader.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setTextString() {
        try {
            assessmentpopup_starttest.setText(getResources().getString(R.string.start));
            assessmentpopup_leaderboardbtn.setText(getResources().getString(R.string.leader_board_title));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setStartTestBtnStatus(String statusStr) {
        assessmentpopup_starttest.setText(statusStr);
    }

    public void setShareImageVisibility() {
        try {
            share_imageview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBanner(String bannerLink) {
        try {
            BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.mydesk));
            Picasso.get().load(bannerLink)
                    .placeholder(bd).error(bd)
                    .into(assessmentpopup_mainimg);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setShareButton() {
        assessmentpopup_starttest.setText(getResources().getString(R.string.share_text));
        share_imageview.setVisibility(View.VISIBLE);
        //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
        //share_imageview.setVisibility(View.VISIBLE);
    }

    public void setDataForEnrolledAssessment(long score, long attemptedQues, boolean reattemptAllowed) {
        try {
//            assessmentpopup_compltetext.setVisibility(View.VISIBLE);
//            assessmentpopup_compltetext.setText(getResources().getString(R.string.assessment_completed_not_submit"));
//            assessmentpopup_score.setVisibility(View.VISIBLE);
            //scorelayout.setVisibility(View.VISIBLE);
//            starttextlayout.setVisibility(View.GONE);
//            assessmentpopup_score.setText(getResources().getString(R.string.score_text") + " : " + score_text);
//            assessmentpopup_attempte.setText(getResources().getString(R.string.questAnswered") + attemptedQues);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUIForPSYCHOMETRICAssessment() {
        try {
            Log.d(TAG, "setUIForPSYCHOMETRICAssessment: ");
//            assessmentpopup_right.setVisibility(View.GONE);
//            assessmentpopup_wrong.setVisibility(View.GONE);
//            assessmentpopup_skipped.setVisibility(View.GONE);
            assessment_question_result.setVisibility(View.GONE);
            assesmentPopup_leader_rippleView.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void setUIForOtherAssessment(long rightQues, long wrongQues, long skipQues) {
        try {
            isScoreDisplaySecondTime = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SCORE_DISPLAY_SECOND_TIME);
            if (isScoreDisplaySecondTime) {
                assessment_question_result.setVisibility(View.VISIBLE);
                userright_ans.setText("" + rightQues);
                long totalwrong = (wrongQues + skipQues);
                userwrong_ans.setText("" + totalwrong);
                userskipvalue.setText("" + assessmentFirebaseClass.getScore());
            } else {
                assessment_question_result.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUiForOtherAssessmentA(long rightQues, long attemptedQues) {
        try {
            if (attemptedQues != 0) {
                float f1 = (float) rightQues;
                float f2 = (float) attemptedQues;
                float presentage = ((f1 / f2) * 100);
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
//                assessmentpopup_score.setText(getResources().getString(R.string.rightprecentage_label")+" : " +df.format(presentage) + "%");
            } else {
                assessment_question_result.setVisibility(View.GONE);
//                assessmentpopup_score.setVisibility(View.GONE);
            }


//            assessmentpopup_right.setVisibility(View.GONE);
//            assessmentpopup_wrong.setVisibility(View.GONE);
//            assessmentpopup_skipped.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showPasscodeeditText() {
        try {
            passcode_layout.setVisibility(View.VISIBLE);
            passcode_edittext.setHint(getResources().getString(R.string.enter_assessment_code));
        } catch (Exception e) {
        }
    }

    public void hideLeaderBoardOption() {
        try {
            assesmentPopup_leader_rippleView.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    public void setListenForPasscodeEdittext() {
        try {
            passcode_edittext.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == EditorInfo.IME_ACTION_GO || keyCode == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        } catch (Exception e) {
                        }
                        startTestBtnCLick(passcode_edittext.getText().toString());
                    }
                    return false;
                }
            });
        } catch (Exception e) {
        }
    }

    public void startTestBtnCLick(String passcodeText) {
        try {
            Log.d(TAG, "startTestBtnCLick: " + isBackButtonPressed);
            if (!isBackButtonPressed) {
                shouldGotoQuestionView = true;
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.getAssessmentState() != null &&
                        assessmentFirebaseClass.getAssessmentState().equalsIgnoreCase(AssessmentState.OVER)) {
                    Log.d(TAG, "startTestBtnCLick: 1");
                    shareButtonClick();
                } else {
                    if ((!isEnrolled) || ((assessmentFirebaseClass != null) &&
                            (assessmentFirebaseClass.isReattemptAllowed() &&
                                    assessmentFirebaseClass.getAttemptCount() < assessmentFirebaseClass.getNoOfAttemptAllowedToPass()))) {
                        if (!OustSdkTools.checkInternetStatus()) {
                            return;
                        }
                        if ((assessmentFirebaseClass.getScope() != null) && (assessmentFirebaseClass.getScope().equalsIgnoreCase("private"))) {
                            if (assessmentFirebaseClass.getPasscode() != null) {
                                if ((passcodeText != null) && (!passcodeText.isEmpty())) {
                                    checkAssessmentState(passcodeText);
                                } else {
                                    showToastMessage(getResources().getString(R.string.enter_valid_assessment_code));
                                }
                            } else {
                                checkAssessmentState(passcodeText);
                            }
                        } else {
                            checkAssessmentState(passcodeText);
                        }
                    } else if (assessmentPlayResponce != null && assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.INPROGRESS)) {
                        checkAssessmentState(passcodeText);
                    } else {
                        if (assessmentFirebaseClass.isReattemptAllowed() && assessmentFirebaseClass.getAttemptCount() < assessmentFirebaseClass.getNoOfAttemptAllowedToPass()) {
                            showDownloadPopup();
                            createAssessmentGame();
                        } else {
                            shareButtonClick();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkAssessmentState(String passcodeText) {
        try {
            Log.d(TAG, "checkAssessmentState: passcode:" + passcodeText);
            String somet = AssessmentState.SUBMITTED;
            if (assessmentPlayResponce != null) {
                if (assessmentPlayResponce.getAssessmentState() != null) {
                    if (assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.STARTED)) {
                        Log.d(TAG, "checkAssessmentState: Started");
                        showDownloadPopup();
                        createAssessmentGame();
                    } else if (assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.INPROGRESS)) {
                        Log.d(TAG, "checkAssessmentState: INProgress");
                        if ((assessmentPlayResponce.getGameId() != null) && (!assessmentPlayResponce.getGameId().isEmpty()) && (assessmentPlayResponce.getTotalQuestion() > 0)) {
                            Log.d(TAG, "checkAssessmentState: inprogress : id not null");
                            showDownloadPopup();
                            activeGame.setGameid(assessmentPlayResponce.getGameId());
                            checkForSavedResponce(("" + assessmentFirebaseClass.getAsssessemntId()));
                            checkforSavedAssessment(activeUser, assessmentFirebaseClass.getAsssessemntId());
                        } else {
                            Log.d(TAG, "checkAssessmentState: inprogress : id null");
                            showDownloadPopup();
                            createAssessmentGame();
                        }
                    } else if (assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.COMPLETED)) {
                        Log.d(TAG, "checkAssessmentState: Completed");
                        submitLastAssessment();
                    } else if (assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED)) {
                        Log.d(TAG, "checkAssessmentState: submitted");
                        if (assessmentFirebaseClass.isReattemptAllowed()) {
                            showDownloadPopup();
                            createAssessmentGame();
                        } else {
                            isEnrolled = true;
                        }
                    }
                } else {
                    if ((assessmentPlayResponce.getGameId() != null) && (!assessmentPlayResponce.getGameId().isEmpty()) && (assessmentPlayResponce.getTotalQuestion() > 0)) {
                        showDownloadPopup();
                        activeGame.setGameid(assessmentPlayResponce.getGameId());
                        checkForSavedResponce(("" + assessmentFirebaseClass.getAsssessemntId()));
                    } else {
                        showDownloadPopup();
                        if (assessmentFirebaseClass.isEnrolled()) {
                            createAssessmentGame();
                        } else {
                            enrolledAssessmentFirst(passcodeText);
                        }
                    }
                }
            } else {
                if (assessmentFirebaseClass.isOtpVerification()) {
                    if (!sendOtpRequest) {
                        enterMobileNoPopup();
                    } else {
                        showConfirmOptPopup();
                    }
                } else {
                    showDownloadPopup();
                    if (assessmentFirebaseClass.isEnrolled()) {
                        createAssessmentGame();
                    } else {
                        enrolledAssessmentFirst(passcodeText);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void enrolledAssessmentFirst(String passcodeText) {
        try {
            AssessmentCreateGameRequest assessmentCreateGameRequest = new AssessmentCreateGameRequest();
            assessmentCreateGameRequest.setStudentid(activeUser.getStudentid());
            assessmentCreateGameRequest.setPasscode(passcodeText);
            assessmentCreateGameRequest.setAssessmentId((int) assessmentFirebaseClass.getAsssessemntId());
            enrolledAssessment(assessmentCreateGameRequest);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void submitLastAssessment() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            SubmitRequest submitRequest = new SubmitRequest();
            submitRequest.setWinner(assessmentPlayResponce.getWinner());
            submitRequest.setGameid(assessmentPlayResponce.getGameId());
            submitRequest.setTotalscore(assessmentPlayResponce.getChallengerFinalScore());
            submitRequest.setScores(assessmentPlayResponce.getScoresList());
            submitRequest.setEndTime(assessmentPlayResponce.getEndTime());
            submitRequest.setStartTime(assessmentPlayResponce.getStartTime());
            submitRequest.setChallengerid(assessmentPlayResponce.getChallengerid());
            submitRequest.setOpponentid(assessmentPlayResponce.getOpponentid());
            submitRequest.setAssessmentId(("" + assessmentFirebaseClass.getAsssessemntId()));
            if (courseId != null) {
                submitRequest.setCourseId(courseId);
            }
            if (courseColnId != null) {
                submitRequest.setCourseColnId(courseColnId);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            showStartingLoaderAgain();
            submitAssessment(submitRequest);
        } catch (Exception e) {
        }
    }

    private void createAssessmentGame() {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest();
            createGameRequest.setChallengerid(activeUser.getStudentid());
            createGameRequest.setGuestUser(false);
            createGameRequest.setRematch(false);
            createGameRequest.setAssessmentId(assessmentId);
            String laungeStr = Locale.getDefault().getLanguage();
            if ((laungeStr != null)) {
                createGameRequest.setAssessmentLanguage(laungeStr);
            }
            createGame(createGameRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setTotalQuestionText(long totalQuestion) {
        assessmentpopup_totalques.setText("" + totalQuestion);
        total_ques_text.setText(getResources().getString(R.string.total_question));
    }

    public void setEnrolledCount(long enrolledCount) {
        assessmentpopup_totlepeople.setText("" + enrolledCount);
        participants_text.setText(getResources().getString(R.string.participants));
    }

    public void setAssessmentScopeText(String scopeText) {
        assessmentpopup_scope.setText(getResources().getString(R.string.scope));
        scope_text.setText("" + scopeText);
    }

    public void setAssessmentNameText(String nameText) {
        assessmentpopup_title.setText(nameText);
    }

    public void setAssessmentDescriptionText(String descriptionText) {
        assessmentpopup_content.setText(descriptionText);
    }

    public void checkforSavedAssessment(ActiveUser activeUser, long assessmentId) {
        Log.d(TAG, "checkforSavedAssessment: " + assessmentId);
        try {
            nCorrect = 0;
            nWrong = 0;

            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
            //String node = "";
            //node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if (isMultipleCplEnable) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + isCplId + "/contentListMap/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            } else {
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
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            int totalQues = 0;
                            AssessmentPlayResponse assessmentPlayResponce = new AssessmentPlayResponse();
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (assessmentprogressMainMap != null) {
                                if (assessmentprogressMainMap.get("challengerFinalScore") != null) {
                                    if (assessmentprogressMainMap.get("challengerFinalScore").getClass() == Long.class) {
                                        assessmentPlayResponce.setChallengerFinalScore((Long) assessmentprogressMainMap.get("challengerFinalScore"));
                                    } else
                                        assessmentPlayResponce.setChallengerFinalScore(Long.parseLong((String) assessmentprogressMainMap.get("challengerFinalScore")));
                                }
                                if (assessmentprogressMainMap.get("questionIndex") != null) {
                                    int n1 = 0;
                                    if (assessmentprogressMainMap.get("questionIndex").getClass() == Long.class) {
                                        n1 = (int) (long) assessmentprogressMainMap.get("questionIndex");
                                    } else {
                                        n1 = Integer.parseInt((String) assessmentprogressMainMap.get("questionIndex"));
                                    }
                                    assessmentPlayResponce.setQuestionIndex(n1);
                                }
                                if (assessmentprogressMainMap.get("gameId") != null) {
                                    String gameid = "";
                                    if (assessmentprogressMainMap.get("gameId").getClass() == Long.class) {
                                        gameid = String.valueOf(assessmentprogressMainMap.get("gameId"));
                                    } else {
                                        gameid = (String) assessmentprogressMainMap.get("gameId");
                                    }
                                    assessmentPlayResponce.setGameId(gameid);
                                }

                                if (assessmentprogressMainMap.get("commentMediaUploadedPath") != null) {
                                    String commentMediaUploadedPath = "";
                                    if (assessmentprogressMainMap.get("commentMediaUploadedPath").getClass() == Long.class) {
                                        commentMediaUploadedPath = String.valueOf(assessmentprogressMainMap.get("commentMediaUploadedPath"));
                                    } else {
                                        commentMediaUploadedPath = (String) assessmentprogressMainMap.get("commentMediaUploadedPath");
                                    }
                                    assessmentPlayResponce.setCommentMediaUploadedPath(commentMediaUploadedPath);
                                }

                                if (assessmentprogressMainMap.get("studentId") != null) {
                                    assessmentPlayResponce.setStudentId((String) assessmentprogressMainMap.get("studentId"));
                                }
                                if (assessmentprogressMainMap.get("totalQuestion") != null) {
                                    if (assessmentprogressMainMap.get("totalQuestion").getClass() == Long.class) {
                                        totalQues = (int) ((long) assessmentprogressMainMap.get("totalQuestion"));
                                    } else
                                        totalQues = Integer.parseInt((String) assessmentprogressMainMap.get("totalQuestion"));
                                    assessmentPlayResponce.setTotalQuestion(totalQues);
                                }
                                if (assessmentprogressMainMap.get("winner") != null) {
                                    assessmentPlayResponce.setWinner((String) assessmentprogressMainMap.get("winner"));
                                }
                                if (assessmentprogressMainMap.get("endTime") != null) {
                                    assessmentPlayResponce.setEndTime((String) assessmentprogressMainMap.get("endTime"));
                                }
                                if (assessmentprogressMainMap.get("startTime") != null) {
                                    assessmentPlayResponce.setStartTime((String) assessmentprogressMainMap.get("startTime"));
                                }
                                if (assessmentprogressMainMap.get("resumeTime") != null) {
                                    if (assessmentprogressMainMap.get("resumeTime").getClass() == Long.class) {
                                        assessmentPlayResponce.setResumeTime(String.valueOf(assessmentprogressMainMap.get("resumeTime")));
                                    } else
                                        assessmentPlayResponce.setResumeTime((String) assessmentprogressMainMap.get("resumeTime"));
                                }
                                if (assessmentprogressMainMap.get("challengerid") != null) {
                                    assessmentPlayResponce.setChallengerid((String) assessmentprogressMainMap.get("challengerid"));
                                }
                                if (assessmentprogressMainMap.get("opponentid") != null) {
                                    assessmentPlayResponce.setOpponentid((String) assessmentprogressMainMap.get("opponentid"));
                                }
                                String status = "";
                                try {
                                    if (assessmentprogressMainMap.get("assessmentState") != null) {
                                        status = (String) assessmentprogressMainMap.get("assessmentState");
                                        assessmentPlayResponce.setAssessmentState(status);
                                    }
                                } catch (Exception e) {
                                }
                                if (totalQues > 0) {
                                    if (assessmentprogressMainMap.get("scoresList") != null) {
                                        List<Scores> scores = new ArrayList<>();
//                                        if (status != null && status.equals(AssessmentState.SUBMITTED)) {
//
//                                        } else {
                                        List<Object> assessmentprogressScoreList = (List<Object>) assessmentprogressMainMap.get("scoresList");
                                        for (int i = 0; i < assessmentprogressScoreList.size(); i++) {
                                            final Map<String, Object> assessmentScoreMap = (Map<String, Object>) assessmentprogressScoreList.get(i);
                                            Scores scores1 = new Scores();
                                            if ((assessmentScoreMap.get("answer") != null)) {
                                                String answer1 = (String) assessmentScoreMap.get("answer");
                                                answer1 = getUtfStr(answer1);
                                                answer1 = answer1.replaceAll("\\n", "");
                                                answer1 = answer1.replaceAll("\\r", "");
                                                if ((answer1 != null) && (!answer1.isEmpty())) {
                                                    scores1.setAnswer(answer1);
                                                }
                                            }
                                            if ((assessmentScoreMap.get("question") != null)) {
                                                if (assessmentScoreMap.get("question").getClass() == Long.class) {
                                                    scores1.setQuestion((int) ((long) assessmentScoreMap.get("question")));
                                                } else
                                                    scores1.setQuestion(Integer.parseInt((String) assessmentScoreMap.get("question")));
                                            }
                                            if ((assessmentScoreMap.get("questionSerialNo") != null)) {
                                                if (assessmentScoreMap.get("questionSerialNo").getClass() == Long.class) {
                                                    scores1.setQuestionSerialNo((int) ((long) assessmentScoreMap.get("questionSerialNo")));
                                                } else
                                                    scores1.setQuestionSerialNo(Integer.parseInt((String) assessmentScoreMap.get("questionSerialNo")));
                                            }
                                            if ((assessmentScoreMap.get("questionType") != null)) {
                                                scores1.setQuestionType((String) assessmentScoreMap.get("questionType"));
                                            }

                                            if ((assessmentScoreMap.get("questionMedia") != null)) {
                                                scores1.setQuestionMedia((String) assessmentScoreMap.get("questionMedia"));
                                            }

                                            if ((assessmentScoreMap.get("remarks") != null)) {
                                                scores1.setRemarks((String) assessmentScoreMap.get("remarks"));
                                            }

                                            if ((assessmentScoreMap.get("score") != null)) {
                                                if (assessmentScoreMap.get("score").getClass() == Long.class) {
                                                    scores1.setScore(((Long) assessmentScoreMap.get("score")));
                                                } else
                                                    scores1.setScore(Long.parseLong((String) assessmentScoreMap.get("score")));

                                            }
                                            if ((assessmentScoreMap.get("time") != null)) {
                                                if (assessmentScoreMap.get("time").getClass() == Long.class) {
                                                    scores1.setTime(((Long) assessmentScoreMap.get("time")));
                                                } else
                                                    scores1.setTime(Long.parseLong((String) assessmentScoreMap.get("time")));
                                            }
                                            if ((assessmentScoreMap.get("xp") != null)) {
                                                if (assessmentScoreMap.get("xp").getClass() == Long.class) {
                                                    scores1.setXp((int) ((long) assessmentScoreMap.get("xp")));
                                                } else
                                                    scores1.setXp(Integer.parseInt((String) assessmentScoreMap.get("xp")));
                                            }
                                            if ((assessmentScoreMap.get("correct") != null)) {
                                                boolean isCorrect = (boolean) assessmentScoreMap.get("correct");
                                                scores1.setCorrect(isCorrect);
                                                if (isEvent) {
                                                    if (isCorrect) {
                                                        nCorrect++;
                                                    } else {
                                                        nWrong++;
                                                    }
                                                }
                                            }

                                            scores.add(scores1);
                                        }
//                                        }
                                        assessmentPlayResponce.setScoresList(scores);
                                    }
                                }
                                assessmentPlayResponce.setTotalQuestion(totalQues);
                                setAssessmentPlayResponce(assessmentPlayResponce);
                            } else {
                                setAssessmentPlayResponce(null);
                            }
                        } else {
                            setAssessmentPlayResponce(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        setAssessmentPlayResponce(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    setAssessmentPlayResponce(null);
                }
            };
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (!connected) {
                            setAssessmentPlayResponce(null);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    setAssessmentPlayResponce(null);
                }
            };
            OustFirebaseTools.getRootRef().child(".info/connected");
            OustFirebaseTools.getRootRef().child(".info/connected").addListenerForSingleValueEvent(listener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int nCorrect = 0;
    private int nWrong = 0;

    public void setAssessmentPlayResponce(AssessmentPlayResponse assessmentPlayResponce) {
        if (assessmentPlayResponce != null) {
            this.assessmentPlayResponce = assessmentPlayResponce;
        }
        hideStartingLoader();
        setStartButtonStatus();
    }

    public void setStartButtonStatus() {
        try {
            Log.i("start button status", " status");
            if (assessmentPlayResponce != null) {
                if (assessmentPlayResponce.getAssessmentState() != null) {
                    if (assessmentPlayResponce.getAssessmentState().equals(AssessmentState.INPROGRESS)) {
//                            assessmentpopup_compltetext.setVisibility(View.GONE);
                        share_imageview.setVisibility(View.VISIBLE);
                        setStartTestBtnStatus(getResources().getString(R.string.resume));
                        showPercentageLayout();
                        assessover_msg_layout.setVisibility(View.GONE);
                    } else if (assessmentPlayResponce.getAssessmentState().equals(AssessmentState.COMPLETED)) {
                        if ((!isEnrolled) || (assessmentFirebaseClass.isReattemptAllowed())) {
                            showCompletedMsg();
                            setStartTestBtnStatus(getResources().getString(R.string.submit));
                            showPercentageLayout();
                        }
                    } else if (assessmentPlayResponce.getAssessmentState().equals(AssessmentState.SUBMITTED)) {
                        if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                            assessmentpopup_review_layout.setVisibility(View.VISIBLE);
                            assessmentpopup_review_layout.setOnClickListener(this);
                        }
                        if (assessmentFirebaseClass.isReattemptAllowed()) {
                            if (assessmentFirebaseClass.getAttemptCount() >= assessmentFirebaseClass.getNoOfAttemptAllowedToPass()) {
                                share_imageview.setVisibility(View.VISIBLE);
                                //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
                                setShareImageVisibility();
                                showSubmittedMsg();
                                share_imageview.setImageResource(R.drawable.ic_share_new);
                                completion_layout.setVisibility(View.GONE);
                                setStartTestBtnStatus(getResources().getString(R.string.share_text));
                            } else {
                                showSubmittedMsg();
                                completion_layout.setVisibility(View.GONE);
                                setStartTestBtnStatus(getResources().getString(R.string.reattempt));
                            }
                        } else {
                            share_imageview.setVisibility(View.VISIBLE);
                            //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
                            setShareImageVisibility();
                            showSubmittedMsg();
                            share_imageview.setImageResource(R.drawable.ic_share_new);
                            completion_layout.setVisibility(View.GONE);
                            setStartTestBtnStatus(getResources().getString(R.string.share_text));
                        }
                    } else {
                        if (assessmentFirebaseClass.isEnrolled()) {
                            assessover_msg_layout.setVisibility(View.GONE);
                            share_imageview.setVisibility(View.VISIBLE);
                            showPercentageLayout();
                            share_imageview.setImageResource(R.drawable.ic_play_without_circle);
                            //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.assessment_resume_icon));
                            setStartTestBtnStatus(getResources().getString(R.string.resume));
                        }
                    }
                } else {
                    if ((assessmentPlayResponce.getGameId() != null) && (!assessmentPlayResponce.getGameId().isEmpty()) && (assessmentPlayResponce.getTotalQuestion() > 0)) {
                        share_imageview.setVisibility(View.VISIBLE);
                        showPercentageLayout();
                        assessover_msg_layout.setVisibility(View.GONE);
                        share_imageview.setImageResource(R.drawable.ic_play_without_circle);
                        //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.assessment_resume_icon));
                        setStartTestBtnStatus(getResources().getString(R.string.resume));
                    }
                }
            } else {
                if (isEnrolled && assessmentFirebaseClass.isReattemptAllowed()) {
                    if (assessmentFirebaseClass.getAttemptCount() >= assessmentFirebaseClass.getNoOfAttemptAllowedToPass()) {
                        share_imageview.setVisibility(View.VISIBLE);
                        //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
                        setShareImageVisibility();
                        showSubmittedMsg();
                        share_imageview.setImageResource(R.drawable.ic_share_new);
                        completion_layout.setVisibility(View.GONE);
                        setStartTestBtnStatus(getResources().getString(R.string.share_text));
                    } else {
                        showSubmittedMsg();
                        completion_layout.setVisibility(View.GONE);
                        setStartTestBtnStatus(getResources().getString(R.string.reattempt));
                    }
                } else if (isEnrolled) {
                    share_imageview.setVisibility(View.VISIBLE);
                    //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
                    setShareImageVisibility();
                    showSubmittedMsg();
                    share_imageview.setImageResource(R.drawable.ic_share_new);
                    completion_layout.setVisibility(View.GONE);
                    setStartTestBtnStatus(getResources().getString(R.string.share_text));
                }
            }

        } catch (Exception e) {
        }

    }

    private void showSubmittedMsg() {
        assessmentover_message.setText(getResources().getString(R.string.assessment_completed));
        assessover_msg_layout.setVisibility(View.VISIBLE);
        if (assessmentFirebaseClass.getAssessmentOverMessage() != null) {
            assessmentover_message.setText(assessmentFirebaseClass.getAssessmentOverMessage());
        }
    }

    private void showCompletedMsg() {
        assessmentover_message.setText(getResources().getString(R.string.assessment_completed_not_submit));
        assessover_msg_layout.setVisibility(View.VISIBLE);
    }

    private void showPercentageLayout() {
        try {
            completion_layout.setVisibility(View.VISIBLE);
            Log.d(TAG, "showPercentageLayout: Question Index:" + assessmentPlayResponce.getQuestionIndex());
            Log.d(TAG, "showPercentageLayout: N of QNs:" + assessmentFirebaseClass.getNumQuestions());
            int percent = (int) (((float) assessmentPlayResponce.getQuestionIndex() / (float) assessmentFirebaseClass.getNumQuestions()) * 100);
            if (percent == 0) {
                completion_percentage.setVisibility(View.GONE);
                completion_text.setVisibility(View.GONE);
            }
            completion_percentage.setText("" + percent + "%");
            Log.d(TAG, "showPercentageLayout: percenetage:" + percent);
        } catch (Exception e) {
            completion_layout.setVisibility(View.GONE);
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
            }
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            byte[] b = s2.getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            CharBuffer parsed = decoder.decode(bb);
            s1 = "" + parsed;
        } catch (Exception e) {
        }
        return s1;
    }

    public void showToastMessage(String messageStr) {
        OustSdkTools.showToast(messageStr);
    }


    public void showDownloadPopup() {
        try {
            assessmentquestion_downloadpopup.setVisibility(View.VISIBLE);
            downloadassessment_text.setText(getResources().getString(R.string.fetching_question));
        } catch (Exception e) {
        }
    }


    public void hideDownloadPopup() {
        try {
            if ((assessmentquestion_downloadpopup.getVisibility() == View.VISIBLE)) {
                assessmentquestion_downloadpopup.setVisibility(View.GONE);
                this.shouldGotoQuestionView = false;
            }
        } catch (Exception e) {
        }
    }

    public void enrolledAssessment(AssessmentCreateGameRequest assessmentCreateGameRequest) {
        try {
            if ((courseId != null) && (!courseId.isEmpty())) {
                assessmentCreateGameRequest.setCourseId(courseId);
            }
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                assessmentCreateGameRequest.setCourseColnId(courseColnId);
            }
            createAssessmnetGame(assessmentCreateGameRequest);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //enroll assessment
    public void createAssessmnetGame(AssessmentCreateGameRequest assessmentCreateGameRequest) {
        if (((courseId != null) && (!courseId.isEmpty())) || ((courseColnId != null) && (!courseColnId.isEmpty())) || (isEnrolled)) {
            createAssessmentGame();
        } else {
            final CommonResponse[] commonResponses = {null};
            try {
                Gson gson = new GsonBuilder().create();
                String jsonParams = gson.toJson(assessmentCreateGameRequest);
                JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
                String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.createassessment_game);
                createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
                /*JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT, createGameUrl, parsedJsonParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        enrolledAssessmentOver(commonResponses[0]);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("api-key", OustPreferences.get("api_key"));
                            params.put("org-id", OustPreferences.get("tanentid"));
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        return params;
                    }
                };

                jsonReq.setShouldCache(false);
                jsonReq.setRetryPolicy(new DefaultRetryPolicy(45000, 3, 100000f));
                OustSdkApplication.getInstance().addToRequestQueue(jsonReq, "first");*/

                ApiCallUtils.doNetworkCall(Request.Method.PUT, createGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        enrolledAssessmentOver(commonResponses[0]);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


            } catch (Exception e) {
            }
        }
    }

    public void enrolledAssessmentOver(CommonResponse commonResponse) {
        try {
            if (commonResponse != null) {
                if (commonResponse.isSuccess()) {
                    OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolledCount((OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount() + 1));
                    saveAssessmentGame(activeUser, assessmentFirebaseClass.getAsssessemntId());
                    assessmentFirebaseClass.setEnrolled(true);
                    createAssessmentGame();
                } else {
                    hideDownloadPopup();
                    if (commonResponse.getPopup() != null) {
                        showPopup(commonResponse.getPopup(), activeGame);
                    } else {
                        showToastMessage(commonResponse.getError());
                    }
                }
            } else {
                failDueToNetwork();
            }
        } catch (Exception e) {
        }
    }


    public void enrollAssessmentProceeOver(CommonResponse commonResponse) {
        enrolledAssessmentOver(commonResponse);
    }


    public void saveAssessmentGame(ActiveUser activeUser, long assessmentId) {
        try {
            AssessmentPlayResponse assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), 0 + "", null, null, "0", AssessmentState.STARTED);
//            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
            String node = "";
            node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if (isMultipleCplEnable) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + isCplId + "/contentListMap/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            } else {
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
            OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponce);
        } catch (Exception e) {
        }
    }

    public void createGame(CreateGameRequest createGameRequest) {
        final CreateGameResponse[] createGameResponses = {null};
        try {
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(createGameRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.create_game);
            createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
            Log.d(TAG, "createGame: " + jsonParams);

            ApiCallUtils.doNetworkCall(Request.Method.POST, createGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onCreateResponse: " + response.toString());
                    Gson gson = new GsonBuilder().create();
                    createGameResponses[0] = gson.fromJson(response.toString(), CreateGameResponse.class);
                    gotCreateGameRespoce(createGameResponses[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void gotCreateGameRespoce(CreateGameResponse createGameResponse) {
        if (createGameResponse != null) {
            if (createGameResponse.isSuccess()) {
                activeGame.setGameid("" + createGameResponse.getGameid());
                getPlayresponce(activeGame.getGameid(), activeUser, assessmentFirebaseClass.getAsssessemntId());
            } else {
                hideDownloadPopup();
                if (createGameResponse.getPopup() != null) {
                    showPopup(createGameResponse.getPopup(), activeGame);
                } else {
                    showToastMessage(createGameResponse.getError());
                }
            }
        } else {
            failDueToNetwork();
        }
    }

    public void getPlayresponce(String gameId, ActiveUser activeUser, long assessmentId) {
        try {
            assessmentPlayGame(gameId, activeUser.getStudentid(), null, "" + assessmentId, 1);
        } catch (Exception e) {
        }
    }

    public void assessmentPlayGame(String gameid, String studentid, String eventId, String assessmentID, final int type) {
        try {
            Log.d(TAG, "assessmentPlayGame: type->" + type);
            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(gameid);
            assmntGamePlayRequest.setStudentid(studentid);
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setEventCode(eventId);
            assmntGamePlayRequest.setDevicePlatformName("android");
            if ((assessmentID != null) && (!assessmentID.isEmpty()))
                assmntGamePlayRequest.setAssessmentId(assessmentID);

            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            Log.d(TAG, "assessmentPlayGame: " + jsonParams);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String playGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);
            Log.d(TAG, "assessmentPlayGame: " + playGameUrl);


            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        Log.d(TAG, "onPlayResponse: " + response.toString());
                        Gson gson = new GsonBuilder().create();
                        PlayResponse playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                        //Log.d(TAG, "onPlayResponse: " + playResponse.toString());
                        if (type == 1) {
                            gotPlayResponce(playResponse);
                        } else {
                            assessmentFetchResponceOver(playResponse);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //gotPlayResponce(null);
                    try {
                        showToastMessage(getResources().getString(R.string.retry_internet_msg));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            });


        } catch (Exception e) {
        }
    }


    public void gotPlayResponce(PlayResponse playResponse1) {
        Log.d(TAG, "gotPlayResponce: " + OustPreferences.get("api_key"));
        this.playResponse = playResponse1;
        if (playResponse != null) {
            if (playResponse.getPopup() != null) {
                hideDownloadPopup();
                showPopup(playResponse.getPopup(), activeGame);
            } else {
                if ((playResponse.getError() != null) && (!playResponse.getError().isEmpty())) {
                    hideDownloadPopup();
                    showToastMessage(playResponse.getError());
                } else {
                    OustAppState.getInstance().setPlayResponse(this.playResponse);
                    if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                        startDownloadingQuestions(playResponse);
                    } else {
                        hideDownloadPopup();
                        if (playResponse.getEncrypQuestions() != null) {
                            if (shouldGotoQuestionView) {
                                savePlayResonceinToPrivateStorage(playResponse, assessmentFirebaseClass.getAsssessemntId());
                                questionProcessFinish(assessmentFirebaseClass.isSecureAssessment(), activeGame, assessmentFirebaseClass);
                            }
                        } else {
                            showToastMessage(getResources().getString(R.string.unable_fetch_connection_error));
                        }
                    }
                }
            }
        } else {
            hideDownloadPopup();
            showToastMessage(getResources().getString(R.string.unable_fetch_connection_error));
        }
    }

    private int downloadQuestionNo = 0;
    HashMap<String, String> downloadHashMap = new HashMap<>();
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int noofTry = 0;
    int totalTime = 0;
    public HashMap<Integer, Integer> questionListMap = new HashMap<>();

    public void startDownloadingQuestions(PlayResponse playResponse) {
        totalQuestion = playResponse.getqIdList().size();
        incrementDownloadQuestionNO += 3;
        if (incrementDownloadQuestionNO > totalQuestion) {
            incrementDownloadQuestionNO = totalQuestion;
        }
        download_progressbar.setMax(totalQuestion);
        for (int i = 0; i < totalQuestion; i++) {
            DTOQuestions q = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));

            if (OustSdkTools.checkInternetStatus()) {
                getQuestionById(playResponse.getqIdList().get(i), playResponse);
            } else {
                if (q != null && q.getQuestionCardId() != null) {
                    totalTime += q.getMaxtime();
                    downloadHashMap.put(q.getQuestionCardId(), q.getQuestionCardId());
                    // downloadQuestionNo = downloadHashMap.size();
                    downloadQuestionNo++;
                    updateCompletePresentage(playResponse);
                } else {
                    Log.d(TAG, "startDownloadingQuestions: QuestionId:" + playResponse.getqIdList().get(i));
                    getQuestionById(playResponse.getqIdList().get(i), playResponse);
                }
            }
        }
        if (playResponse.getqIdList().size() == 0) {
            updateCompletePresentage(playResponse);
        }
    }

    List<String> mediaList = new ArrayList<>();


    public void getQuestionById(final int qID, final PlayResponse playResponse) {
        try {
            String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getQuestionUrl_V2);
            // String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getquestion_url);
            getQuestionUrl = getQuestionUrl.replace("{QId}", ("" + qID));
            Log.d(TAG, "getQuestionById: " + getQuestionUrl);
            final String finalGetQuestionUrl = getQuestionUrl;
            getQuestionUrl = HttpManager.getAbsoluteUrl(getQuestionUrl);
            /*new Thread() {
                @Override
                public void run() {
                    try {
                        downloadingQuestion = true;
                        OustRestClient oustRestClient = new OustRestClient();
                        DTOQuestions questions = oustRestClient.getQuestionById(finalGetQuestionUrl);
                        totalTime += questions.getMaxtime();
                        Log.e("SCORE", "totalTime " + totalTime);
                        Log.d(TAG, "run: " + questions.getQuestion());
                        gotResponse(questions, qID, playResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }.start();*/
            downloadingQuestion = true;
            ApiCallUtils.doNetworkCall(GET, getQuestionUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    QuestionResponse questionResponce = gson.fromJson(response.toString(), QuestionResponse.class);
                    Log.d(TAG, "onSuccess: " + response.toString());
                    DTOQuestions questions = questionResponce.getQuestions().get(0);
                    totalTime += questions.getMaxtime();
                    Log.e("SCORE", "totalTime " + totalTime);

                    if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                        questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                        questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                        questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                    } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                        questions.setQuestionType(QuestionType.LONG_ANSWER);
                    }

                    Log.d(TAG, "run: " + questions.getQuestion() + "questionType:" + questions.getQuestionType());

                    //Questions questions1 = OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
                    //Log.d(TAG, "onSuccess: "+(new Gson().toJson(questions1)));
                    gotResponse(questions, qID, playResponse);
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

    /*public void getQuestionById2(final int qID, final PlayResponse playResponse) {
        try {
            String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getQuestionUrl_V2);
            // String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getquestion_url);
            getQuestionUrl = getQuestionUrl.replace("{QId}", ("" + qID));
            Log.d(TAG, "getQuestionById: " + getQuestionUrl);
            final String finalGetQuestionUrl = getQuestionUrl;


            ApiClient.jsonRequest(AssessmentPlayActivity.this, GET, getQuestionUrl, new HashMap<String, String>(), null, new ApiClient.NResultListener<JSONObject>() {
                @Override
                public void onResult(int resultCode, JSONObject tResult) {
                    Gson gson = new GsonBuilder().create();
                    QuestionResponse questionResponce = gson.fromJson(tResult.toString(), QuestionResponse.class);
                    Log.d(TAG, "onSuccess: " + tResult.toString());
                    checkForDownloadComplete(questionResponce.getQuestions().get(0), qID, playResponse);
                }

                @Override
                public void onFailure(int mError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/

    public void gotResponse(final DTOQuestions questions, final int qID, final PlayResponse playResponse) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkForDownloadComplete(questions, qID, playResponse);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<String> mediaToDownload;//= new ArrayList<>();

    public void checkForDownloadComplete(DTOQuestions questions, int qId, PlayResponse playResponse) {
        if (questions != null) {
            OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
            //OustMediaTools.prepareMediaList(mediaList, questions);
            Log.d(TAG, "checkForDownloadComplete: adding questions:" + questions.getQuestion());
            Log.d(TAG, "checkForDownloadComplete: " + qId);
            downloadingQuestion = false;
            // startDownloadingQuestions(playResponse);
           /* if(!downloadHashMap.containsKey(qId+"")) {
                downloadHashMap.put(qId + "", qId + "");
                downloadQuestionNo = downloadHashMap.size();
            }*/
            downloadQuestionNo++;
            updateCompletePresentage(playResponse);
        } else {
            noofTry++;
            if (noofTry < 4) {
                getQuestionById(qId, playResponse);
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
        }
    }

    private void updateCompletePresentage(PlayResponse playResponse) {
        download_progressbar.setProgress(downloadQuestionNo);
        Log.d(TAG, "updateCompletePresentage: downloadQuestionNo:" + downloadQuestionNo);
        Log.d(TAG, "updateCompletePresentage: totalQuestion:" + totalQuestion);
        Log.d(TAG, "updateCompletePresentage: incrementDownloadQuestionNO:" + incrementDownloadQuestionNO);
        if (totalQuestion == downloadQuestionNo) {
//            if (incrementDownloadQuestionNO == totalQuestion) {
//                download_progresstext.setText(((int) 100) + "%");
//                downloadMediaFiles(playResponse);
//            } else {
//                startDownloadingQuestions(playResponse);
//            }
            download_progresstext.setText(100 + "%");
            downloadMediaFiles(playResponse);
        } else {
            float percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 100;
            if (percentage < 100) {
                download_progresstext.setText(((int) percentage) + "%");
                Log.d(TAG, "updateCompletePresentage: percentage:" + percentage);
            } else {
                download_progresstext.setText(100 + "%");
            }
        }
    }

    private int mediaDownloadCount = 0;

    private void downloadMediaFiles(PlayResponse playResponse) {
        for (int i = 0; i < totalQuestion; i++) {
            try {
                DTOQuestions dtoQuestions = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(i));
                if (dtoQuestions != null) {
                    OustMediaTools.prepareMediaList(mediaList, dtoQuestions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        downloadassessment_text.setText(getResources().getString(R.string.fetching_media));
        HashSet<String> hashSetMediaList = new HashSet<>(mediaList);
        // mediaList = new ArrayList<>(hashSetMediaList);
        if (mediaList != null && mediaList.size() > 0) {
            download_progressbar.invalidate();
            download_progressbar.setMax(mediaList.size());
            for (int i = 0; i < mediaList.size(); i++)
                downloadMedia(mediaList.get(i));
        } else {
            fetchingDataFinish();
        }
    }

    private class DownloadResultReceiver extends ResultReceiver {
        public DownloadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 1:
                    Log.d(TAG, "onReceiveResult: completed:" + resultData.getString("MSG"));

                    break;

                case 2:
                    //Log.d(TAG, "onReceiveResult: progress:"+resultData.getString("MSG"));
                    break;

                case 3:
                    Log.d(TAG, "onReceiveResult: Error:" + resultData.getString("MSG"));

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

    private void sendToDownloadService(String downloadPath, String destn, String fileName, boolean isOustLearn) {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadFilesIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
        intent.putExtra(AppConstants.StringConstants.IS_VIDEO, false);
        intent.putExtra("receiver", DownloadResultReceiver);
        //DownLoadIntentService.setContext(this);
        startService(intent);
    }

    private void downloadMedia(String media) {
        Log.d(TAG, "downloadMedia: " + media);
        new MediaDataDownloader(this) {
            @Override
            public void downloadComplete() {
                mediaDownloadCount++;
                download_progressbar.setProgress(mediaDownloadCount);
                if (mediaDownloadCount == mediaList.size()) {
                    download_progresstext.setText(100 + "%");
                    fetchingDataFinish();
                } else {
                    float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                    if (percentage < 100) {
                        download_progresstext.setText(((int) percentage) + "%");
                    } else {
                        download_progresstext.setText(100 + "%");
                    }
                }
            }

            @Override
            public void downFailed(String message) {
                //OustSdkTools.showToast(message);
            }
        }.initDownload(media);
    }

    public void fetchingDataFinish() {
        if ((assessmentPlayResponce != null) && (assessmentPlayResponce.getAssessmentState() != null)) {
            questionGetFinish(assessmentFirebaseClass.isSecureAssessment(), assessmentPlayResponce, activeGame, assessmentFirebaseClass);
        } else {
            questionProcessFinish(assessmentFirebaseClass.isSecureAssessment(), activeGame, assessmentFirebaseClass);
        }
    }

    public void questionProcessFinish(boolean isSecureAssessment, ActiveGame activeGame, AssessmentFirebaseClass assessmentFirebaseClass) {
        try {
            Log.d(TAG, "questionProcessFinish: ");
            if (shouldGotoQuestionView && !isReviewAnswerButtonClicked) {
                Gson gson = new GsonBuilder().create();
                Intent intent = new Intent(this, NewAssessmentBaseActivity.class);
                intent.putExtra("containCertificate", containCertificate);
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.getQuestionXp() != 0) {
                    intent.putExtra("questionXp", assessmentFirebaseClass.getQuestionXp());
                }
                intent.putExtra("isCplModule", isCplModule);
                intent.putExtra("totalTimeOfAssessment", totalTime);
                intent.putExtra("IS_FROM_COURSE", isFromCourse);
                intent.putExtra("courseAssociated", assessmentFirebaseClass.isCourseAssociated());
                intent.putExtra("mappedCourseId", assessmentFirebaseClass.getMappedCourseId());
                intent.putExtra("timePenaltyDisabled", assessmentFirebaseClass.isTimePenaltyDisabled());
                intent.putExtra("resumeSameQuestion", assessmentFirebaseClass.isResumeSameQuestion());
                intent.putExtra("reAttemptAllowed", assessmentFirebaseClass.isReattemptAllowed());
                intent.putExtra("nAttemptCount", assessmentFirebaseClass.getAttemptCount());
                intent.putExtra("nAttemptAllowedToPass", assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
                intent.putExtra("currentCplId", isCplId);
                intent.putExtra("isComingFromCpl", isComingFromCPL);
                intent.putExtra("isMultipleCplModule", isMultipleCplEnable);
                intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());

                if (isEvent) {
                    intent.putExtra("isEventLaunch", true);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("nCorrect", nCorrect);
                    intent.putExtra("nWrong", nWrong);
                }

                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                if ((courseId != null) && (!courseId.isEmpty())) {
                    intent.putExtra("courseId", courseId);
                }
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColnId);
                }
                AssessmentPlayActivity.this.finish();
                startActivity(intent);
            } else if (isReviewAnswerButtonClicked && shouldGotoQuestionView) {
                gotoAssessmentReviewAnswer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoAssessmentReviewAnswer() {
        try {
            hideDownloadPopup();
            Gson gson = new GsonBuilder().create();
            Intent intent = new Intent(this, AssessmentQuestionReviewBaseActivity.class);
            intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("comingFromAssessmentLanding", true);
            OustAppState.getInstance().setPlayResponse(this.playResponse);
            SubmitRequest submitRequest = getSubmitRequest();
            intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
            intent.putExtra("currentCplId", isCplId);
            intent.putExtra("isMultipleCplModule", isMultipleCplEnable);
            download_progresstext.setText("0%");
            if (submitRequest != null) {
                this.startActivity(intent);
            } else {
                OustSdkTools.showToast("DATA NOT FOUND!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private SubmitRequest getSubmitRequest() {
        try {
            if (assessmentPlayResponce != null) {
                SubmitRequest submitRequest = new SubmitRequest();
                submitRequest.setWinner(assessmentPlayResponce.getWinner());
                submitRequest.setGameid(assessmentPlayResponce.getGameId());
                submitRequest.setTotalscore(assessmentPlayResponce.getChallengerFinalScore());
                submitRequest.setScores(assessmentPlayResponce.getScoresList());
                submitRequest.setEndTime(assessmentPlayResponce.getEndTime());
                submitRequest.setStartTime(assessmentPlayResponce.getStartTime());
                submitRequest.setChallengerid(assessmentPlayResponce.getChallengerid());
                submitRequest.setOpponentid(assessmentPlayResponce.getOpponentid());
                submitRequest.setAssessmentId(("" + assessmentFirebaseClass.getAsssessemntId()));
                submitRequest.setStudentid(activeUser.getStudentid());
                return submitRequest;
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
        }
    }


    //-----------------------------------
    public void checkForSavedResponce(String assessmentId) {
        String playresponceStr = "";
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String fileNameStr = ("assessment" + assessmentId + ".txt");
            playresponceStr = enternalPrivateStorage.readSavedData(fileNameStr);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        gotSavedPlayResponceStr(playresponceStr);
    }

    public void gotSavedPlayResponceStr(String playresponceStr) {
        try {
            Log.d(TAG, "gotSavedPlayResponceStr: " + playresponceStr);
            if ((playresponceStr != null) && (!playresponceStr.isEmpty())) {
                Log.d(TAG, "gotSavedPlayResponceStr: not null");
                Gson gson1 = new Gson();
                playResponse = gson1.fromJson(playresponceStr, PlayResponse.class);

                if ((playResponse != null) && (playResponse.getEncrypQuestions() != null)) {
                    activeGame.setGameid("" + assessmentPlayResponce.getGameId());
                    if (shouldGotoQuestionView) {
                        OustAppState.getInstance().setPlayResponse(this.playResponse);
                        questionGetFinish(assessmentFirebaseClass.isSecureAssessment(), assessmentPlayResponce, activeGame, assessmentFirebaseClass);
                    }
                } else {
                    Log.d(TAG, "gotSavedPlayResponceStr: not encrypted");
                    fetchPlayresponce(activeGame.getGameid(), activeUser, assessmentFirebaseClass.getAsssessemntId());
                }
            } else {
                Log.d(TAG, "gotSavedPlayResponceStr: null");
                fetchPlayresponce(activeGame.getGameid(), activeUser, assessmentFirebaseClass.getAsssessemntId());
            }
        } catch (Exception e) {
        }
    }

    public void fetchPlayresponce(String gameId, ActiveUser activeUser, long assessmentId) {
        try {
            assessmentPlayGame(gameId, activeUser.getStudentid(), null, "" + assessmentId, 2);

        } catch (Exception e) {
        }
    }

    public void assessmentFetchResponceOver(PlayResponse playResponse) {
        gotAssessmentPlayResponce(playResponse);
    }

    public void gotAssessmentPlayResponce(PlayResponse playResponse1) {
        try {
            this.playResponse = playResponse1;
            if (playResponse != null) {
                if (playResponse.getPopup() != null) {
                    hideDownloadPopup();
                    showPopup(playResponse.getPopup(), activeGame);
                } else {
                    if ((playResponse.getError() != null) && (!playResponse.getError().isEmpty())) {
                        hideDownloadPopup();
                        showToastMessage(playResponse.getError());
                    } else {
                        if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                            OustAppState.getInstance().setPlayResponse(this.playResponse);
                            startDownloadingQuestions(playResponse);
                        } else {
                            hideDownloadPopup();
                            if (playResponse.getEncrypQuestions() != null) {
                                if (shouldGotoQuestionView) {
                                    savePlayResonceinToPrivateStorage(playResponse, assessmentFirebaseClass.getAsssessemntId());
                                    OustAppState.getInstance().setPlayResponse(this.playResponse);
                                    questionGetFinish(assessmentFirebaseClass.isSecureAssessment(), assessmentPlayResponce, activeGame, assessmentFirebaseClass);
                                }
                            } else {
                                showToastMessage(getResources().getString(R.string.unable_fetch_connection_error));
                            }
                        }
                    }
                }
            } else {
                hideDownloadPopup();
                showToastMessage(getResources().getString(R.string.unable_fetch_connection_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void questionGetFinish(boolean isSecureAssessment, AssessmentPlayResponse assessmentPlayResponce, ActiveGame activeGame, AssessmentFirebaseClass assessmentFirebaseClass) {
        try {
            gotoQuestionPage(assessmentPlayResponce, activeGame);
        } catch (Exception e) {
        }
    }

    private void gotoQuestionPage(AssessmentPlayResponse assessmentPlayResponce, ActiveGame activeGame) {
        try {
            if (shouldGotoQuestionView && !isReviewAnswerButtonClicked) {

                Gson gson = new GsonBuilder().create();
                Intent intent = new Intent(AssessmentPlayActivity.this, NewAssessmentBaseActivity.class);
                intent.putExtra("containCertificate", containCertificate);
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.getQuestionXp() != 0) {
                    intent.putExtra("questionXp", assessmentFirebaseClass.getQuestionXp());
                }
                intent.putExtra("isCplModule", isCplModule);
                intent.putExtra("totalTimeOfAssessment", totalTime);
                intent.putExtra("timePenaltyDisabled", assessmentFirebaseClass.isTimePenaltyDisabled());
                intent.putExtra("resumeSameQuestion", assessmentFirebaseClass.isResumeSameQuestion());
                intent.putExtra("courseAssociated", assessmentFirebaseClass.isCourseAssociated());
                intent.putExtra("mappedCourseId", assessmentFirebaseClass.getMappedCourseId());
                intent.putExtra("reAttemptAllowed", assessmentFirebaseClass.isReattemptAllowed());
                intent.putExtra("IS_FROM_COURSE", isFromCourse);
                intent.putExtra("nAttemptCount", assessmentFirebaseClass.getAttemptCount());
                intent.putExtra("nAttemptAllowedToPass", assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
                intent.putExtra("currentCplId", isCplId);
                intent.putExtra("isComingFromCpl", isComingFromCPL);
                intent.putExtra("isMultipleCplModule", isMultipleCplEnable);
                intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());

                if (isEvent) {
                    intent.putExtra("isEventLaunch", true);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("nCorrect", nCorrect);
                    intent.putExtra("nWrong", nWrong);
                }

                if ((assessmentPlayResponce != null)) {
                    Log.e("SCORE", "assessmentPlayResponce not null");
                    if (!(assessmentPlayResponce.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED))) {
                        intent.putExtra("assessmentResp", gson.toJson(assessmentPlayResponce));
                        Log.e("SCORE", "assessmentPlayResponce not null && state not submitted");
                    }
                    if (assessmentPlayResponce.getScoresList() != null) {
                        Log.e("SCORE", "assessmentPlayResponce not null && ScoreList not null");
                    }
                }
                if ((courseId != null) && (!courseId.isEmpty())) {
                    intent.putExtra("courseId", courseId);
                }
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColnId);
                }
                startActivity(intent);
                AssessmentPlayActivity.this.finish();
            } else if (isReviewAnswerButtonClicked && shouldGotoQuestionView) {
                gotoAssessmentReviewAnswer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //==========================================================================================================================================
    private boolean isReviewAnswerButtonClicked = false;

    @Override
    public void onClick(View view) {
        try {
//            YoYo.with(Techniques.BounceIn).duration(100).playOn(view);
            int id = view.getId();
            if (id == R.id.assessmentpopup_oustbtn) {
                onBackPressed();
            } else if (id == R.id.assesmentPopup_leader_rippleView) {
                assesmentPopup_leader_rippleView.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(TryRippleView rippleView) {
                        if (!OustSdkTools.checkInternetStatus()) {
                            return;
                        }
                        Intent intent = new Intent(AssessmentPlayActivity.this, EventLeaderboardActivity.class);
                        intent.putExtra("isassessmentleaderboard", "true");
                        startActivity(intent);
                    }
                });
            } else if (id == R.id.assesmenPopup_start_rippleView) {
                Log.d(TAG, "assesmenPopup_start_rippleView");
                s_rippleView.setOnRippleCompleteListener(new TryRippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(TryRippleView rippleView) {
                        Log.d(TAG, "onComplete:");
                        mediaList = new ArrayList<>();
                        startTestBtnCLick(passcode_edittext.getText().toString());
                    }
                });
            } else if (id == R.id.assessmentpopup_review_rippleView) {
                isReviewAnswerButtonClicked = true;
                shouldGotoQuestionView = true;
                showDownloadPopup();
                createAssessmentGame();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void savePlayResonceinToPrivateStorage(PlayResponse playResponse, long assessmentId) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            Gson gson = new Gson();
            String newPlayresponceStr = gson.toJson(playResponse);
            String fileNameStr = ("assessment" + assessmentId + ".txt");
            enternalPrivateStorage.saveFile(fileNameStr, newPlayresponceStr);
        } catch (Exception e) {
            int n1 = 0;
            int n2 = n1 + 2;
        }
    }


    public void showToastErrorMsg(String error) {
        OustSdkTools.showToast("  " + error + " ");
        AssessmentPlayActivity.this.finish();
    }

    public void failDueToNetwork() {
        try {
            OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            hideDownloadPopup();
        } catch (Exception e) {
        }
    }

    public void showPopup(Popup popup, ActiveGame activeGame) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            Gson gson = new GsonBuilder().create();
            Intent intent = new Intent(AssessmentPlayActivity.this, PopupActivity.class);
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            startActivity(intent);
            AssessmentPlayActivity.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isBackButtonPressed = false;

    public void setBackButtonPressed(boolean isBackButtonPressed) {
        this.isBackButtonPressed = isBackButtonPressed;
    }

    @Override
    public void onBackPressed() {
        try {
            Log.d(TAG, "onBackPressed: ");
            if (isFromCourse) {
                if ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null)
                        && (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                } else {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                }
            }

            this.shouldGotoQuestionView = false;

            if (((isEvent && OustStaticVariableHandling.getInstance().getOustApiListener() != null) || isFromCourse) && OustAppState.getInstance().getAssessmentFirebaseClass() != null) {
                if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                    userEventAssessmentData.setUserProgress("Completed");
                    userEventAssessmentData.setnQuestionSkipped((userEventAssessmentData.getnTotalQuestions() - (nCorrect + nWrong) > 0) ? userEventAssessmentData.getnTotalQuestions() - (nWrong + nCorrect) : 0);
                    userEventAssessmentData.setnQuestionAnswered(userEventAssessmentData.getnTotalQuestions());
                } else {
                    userEventAssessmentData.setUserProgress("InProgress");
                    userEventAssessmentData.setnQuestionSkipped(0);
                    userEventAssessmentData.setnQuestionAnswered(nCorrect + nWrong);
                }
                userEventAssessmentData.setnQuestionWrong(nWrong);
                userEventAssessmentData.setnQuestionCorrect(nCorrect);

                if (!isFromCourse) {
                    if (isCplModule) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setCurrentModuleType("Assessment");
                        userEventCplData.setEventId(eventId);

                        userEventCplData.setCurrentModuleId(userEventAssessmentData.getAssessmentId());
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                        userEventCplData.setnTotalModules(totalModules);

                        if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                            userEventCplData.setCurrentModuleProgress("Completed");
                            if (completedModules >= totalModules) {
                                userEventCplData.setnModulesCompleted(totalModules);
                                userEventCplData.setUserProgress("Completed");
                            } else {
                                userEventCplData.setnModulesCompleted(completedModules + 1);
                                userEventCplData.setUserProgress("InProgress");
                            }
                        } else {
                            userEventCplData.setCurrentModuleProgress("InProgress");
                            userEventCplData.setnModulesCompleted(completedModules);
                            userEventCplData.setUserProgress("InProgress");
                        }

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

            if (assessmentquestion_downloadpopup.getVisibility() == View.VISIBLE) {
                shouldGotoQuestionView = false;
                assessmentquestion_downloadpopup.setVisibility(View.GONE);
            } else {
                isBackButtonPressed = true;
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void killClass() {
        try {
            if (isFromCourse) {
                if ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) && (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                } else {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                }
            }

            AssessmentPlayActivity.this.finish();
        } catch (Exception e) {
        }
    }

    //=====================================================================================================================
    public void checkForAssessmentComplete(ActiveUser activeUser, long assessmentId) {
        try {
            String message = "/userAssessment/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
            if (!comingFormOldLandingPage) {
                message = "/landingPage/" + activeUser.getStudentKey() + "/assessment/" + assessmentId;
            }
            if ((courseId != null) && (!courseId.isEmpty())) {
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn/" + courseColnId + "/courses/course" + courseId + "/mappedAssessment";
                } else {
                    message = "/landingPage/" + activeUser.getStudentKey() + "/course/" + courseId + "/mappedAssessment";
                }
            } else {
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn/" + courseColnId + "/mappedAssessment";
                }
            }
            Log.e("assessmentClass", message);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            final Map<String, Object> assessmentmainSubMap = (Map<String, Object>) dataSnapshot.getValue();
                            extractLandingData(assessmentmainSubMap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    gotAssessmentCompletionSttus();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    gotAssessmentCompletionSttus();
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(assessmentListener);
        } catch (Exception e) {
            gotAssessmentCompletionSttus();
        }
    }

    private void extractLandingData(Map<String, Object> assessmentmainSubMap) {
        try {
            if (assessmentmainSubMap.get("completionDate") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setCompletionDate((String) assessmentmainSubMap.get("completionDate"));
            }
            if (assessmentmainSubMap.get("contentPlayListId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListId")));
            }
            if (assessmentmainSubMap.get("contentPlayListSlotId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListSlotId")));
            }
            if (assessmentmainSubMap.get("contentPlayListSlotItemId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotItemId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListSlotItemId")));
            }
            if (assessmentmainSubMap.get("nQuestionAnswered") != null) {
                int n1 = (int) (OustSdkTools.convertToLong(assessmentmainSubMap.get("nQuestionAnswered")));
                OustAppState.getInstance().getAssessmentFirebaseClass().setQuesAttempted(n1);
                userEventAssessmentData.setnQuestionAnswered(n1);
            }
            if (assessmentmainSubMap.get("nQuestionCorrect") != null) {
                int n1 = (int) (OustSdkTools.convertToLong(assessmentmainSubMap.get("nQuestionCorrect")));
                OustAppState.getInstance().getAssessmentFirebaseClass().setRightQues(n1);
                userEventAssessmentData.setnQuestionCorrect(n1);
            }
            if (assessmentmainSubMap.get("enrolled") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolled((boolean) assessmentmainSubMap.get("enrolled"));
            }
            if (assessmentmainSubMap.get("nQuestionWrong") != null) {
                int n1 = (int) (OustSdkTools.convertToLong(assessmentmainSubMap.get("nQuestionWrong")));
                OustAppState.getInstance().getAssessmentFirebaseClass().setWrongQues(n1);
                userEventAssessmentData.setnQuestionWrong(n1);
            }
            if (assessmentmainSubMap.get("nQuestionSkipped") != null) {
                int n1 = (int) (OustSdkTools.convertToLong(assessmentmainSubMap.get("nQuestionSkipped")));
                OustAppState.getInstance().getAssessmentFirebaseClass().setSkippedQues(n1);
                userEventAssessmentData.setnQuestionSkipped(n1);
            }
            if (assessmentmainSubMap.get("contentPlayListId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListId")));
            }
            if (assessmentmainSubMap.get("contentPlayListSlotId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListSlotId")));
            }
            if (assessmentmainSubMap.get("contentPlayListSlotItemId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotItemId(OustSdkTools.convertToLong(assessmentmainSubMap.get("contentPlayListSlotItemId")));
            }

            if (assessmentmainSubMap.get("attemptCount") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setAttemptCount(OustSdkTools.convertToLong(assessmentmainSubMap.get("attemptCount")));
                if (assessmentFirebaseClass != null) {
                    assessmentFirebaseClass.setAttemptCount(OustSdkTools.convertToLong(assessmentmainSubMap.get("attemptCount")));
                }
            }

            if (assessmentmainSubMap.get("score") != null) {
                Object o3 = assessmentmainSubMap.get("score");
                if (o3.getClass().equals(String.class)) {
                    int n1 = Integer.parseInt((String) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);

                } else if (o3.getClass().equals(Long.class)) {
                    int n1 = (int) ((long) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);

                } else if (o3.getClass().equals(Double.class)) {
                    int n1 = (int) ((double) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotAssessmentCompletionSttus() {
        try {
            //isEnrolled=OustAppState.getInstance().getAssessmentFirebaseClass().isEnrolled();
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) && (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                isEnrolled = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        setAssessmentData();
    }

    //set assessmnet status based on firebase variables
    private void setAssessmentData() {
        try {
            setTextString();
            if ((assessmentFirebaseClass.getBanner() != null) && (!assessmentFirebaseClass.getBanner().isEmpty())) {
                setBanner(assessmentFirebaseClass.getBanner());
            }
            if ((assessmentFirebaseClass.getBgImg() != null) && (!assessmentFirebaseClass.getBgImg().isEmpty())) {
                setBackgroundImg(assessmentFirebaseClass.getBgImg());
            }
            Log.d(TAG, "setAssessmentData: isHideLeaderboard" + assessmentFirebaseClass.isHideLeaderboard());

            if (OustPreferences.getAppInstallVariable("hideAllAssessmentLeaderBoard") || assessmentFirebaseClass.isHideLeaderboard()) {
                hideLeaderBoardOption();
            }

            //if assessment is already enrolled(completed) and reattempted allowed then only show user score_text
            if (isEnrolled && (!assessmentFirebaseClass.isReattemptAllowed())) {
                setDataForEnrolledAssessment(assessmentFirebaseClass.getScore(), assessmentFirebaseClass.getQuesAttempted(), assessmentFirebaseClass.isReattemptAllowed());
                setShareButton();
                //PSYCHOMETRIC assessment is where no xp calulated for question(survey)
                if ((assessmentFirebaseClass.getAssessmentType() != null) && (assessmentFirebaseClass.getAssessmentType() == AssessmentType.PSYCHOMETRIC)) {
                    setUIForPSYCHOMETRICAssessment();
                } else {
                    if (assessmentFirebaseClass.isShowPercentage()) {
                        setUiForOtherAssessmentA(assessmentFirebaseClass.getRightQues(), assessmentFirebaseClass.getQuesAttempted());
                    } else {
                        setUIForOtherAssessment(assessmentFirebaseClass.getRightQues(), assessmentFirebaseClass.getWrongQues(), assessmentFirebaseClass.getSkippedQues());
                    }
                }
                Log.d(TAG, "setAssessmentData:isShowQuestionsOnCompletion: " + assessmentFirebaseClass.isShowQuestionsOnCompletion());
                if (!assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                    // deleteAssessment("" + assessmentFirebaseClass.getAsssessemntId());
                }
            } else if ((assessmentFirebaseClass.getScope() != null) && (assessmentFirebaseClass.getScope().equalsIgnoreCase("private"))) {
                showPasscodeeditText();
                if ((assessmentFirebaseClass.isReattemptAllowed()) && (isEnrolled)) {
                    setUIForOtherAssessment(assessmentFirebaseClass.getRightQues(), assessmentFirebaseClass.getWrongQues(), assessmentFirebaseClass.getSkippedQues());
                    setDataForEnrolledAssessment(assessmentFirebaseClass.getScore(), assessmentFirebaseClass.getQuesAttempted(), assessmentFirebaseClass.isReattemptAllowed());
                }
            } else if ((assessmentFirebaseClass.isReattemptAllowed()) && (isEnrolled)) {
                setUIForOtherAssessment(assessmentFirebaseClass.getRightQues(), assessmentFirebaseClass.getWrongQues(), assessmentFirebaseClass.getSkippedQues());
                setDataForEnrolledAssessment(assessmentFirebaseClass.getScore(), assessmentFirebaseClass.getQuesAttempted(), assessmentFirebaseClass.isReattemptAllowed());
            }
            setListenForPasscodeEdittext();
            setTotalQuestionText(assessmentFirebaseClass.getNumQuestions());
            setEnrolledCount(assessmentFirebaseClass.getEnrolledCount());
            if (assessmentFirebaseClass.getScope() != null) {
                setAssessmentScopeText(assessmentFirebaseClass.getScope().toLowerCase());
            }
            if (assessmentFirebaseClass.getName() != null) {
                setAssessmentNameText(assessmentFirebaseClass.getName());
                try {
                    OustPreferences.save("current_assessmentName", assessmentFirebaseClass.getName());
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            if (assessmentFirebaseClass.getDescription() != null) {
                setAssessmentDescriptionText(assessmentFirebaseClass.getDescription());
            }


            if (assessmentFirebaseClass != null && assessmentFirebaseClass.getAssessmentState() != null &&
                    assessmentFirebaseClass.getAssessmentState().equalsIgnoreCase(AssessmentState.OVER)) {
                hideStartingLoader();
                Log.d(TAG, "setAssessmentData: assessmentOver:" + assessmentFirebaseClass.getAssessmentOverMessage());
                share_imageview.setVisibility(View.VISIBLE);
                //OustSdkTools.setImage(share_imageview, getResources().getString(R.string.share_new_icon));
                setShareImageVisibility();
                completion_layout.setVisibility(View.GONE);
                setStartTestBtnStatus(getResources().getString(R.string.share_text));
                share_imageview.setImageResource(R.drawable.ic_share_new);
                assessmentover_message.setText(getResources().getString(R.string.assessment_no_active_error));
                assessover_msg_layout.setVisibility(View.VISIBLE);
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.getAssessmentOverMessage() != null
                        && (!assessmentFirebaseClass.getAssessmentOverMessage().isEmpty())) {
                    assessmentover_message.setText(assessmentFirebaseClass.getAssessmentOverMessage());
                }
            } else {
                Log.d(TAG, "setAssessmentData:dsdfssd ");
                checkforSavedAssessment(activeUser, assessmentFirebaseClass.getAsssessemntId());
                checkForDeviceNetTime();
            }
            //hiding or showing assessment result score_text
            Log.d(TAG, "setAssessmentData: assessmentFirebaseClass.isShowAssessmentResultScore()" + assessmentFirebaseClass.isShowAssessmentResultScore());
            if (!assessmentFirebaseClass.isShowAssessmentResultScore()) {
                Log.d(TAG, "setAssessmentData: show");
                setUIForPSYCHOMETRICAssessment();
            }

            if (OustPreferences.getTimeForNotification("cplId_assessment") > 0 && !isComingFromCPL &&
                    ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() == null))) {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.attach_module_cpl));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (!assessmentFirebaseClass.isShowAssessmentResultScore()) {
            setUIForPSYCHOMETRICAssessment();
        }
        if (assessmentFirebaseClass.getCompletionDate() == null) {
            setUIForPSYCHOMETRICAssessment();
        }


    }

    private void setBackgroundImg(String bgImg) {
        try {
            Picasso.get().load(bgImg).into(assessment_bgImg);
        } catch (Exception e) {
        }
    }

    //testing
    //======================================================================================================
    private PopupWindow enterMobileNoPOpup, confirmOTPPopup;

    public void enterMobileNoPopup() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.assessmentotp_popuplayout, null);
            enterMobileNoPOpup = OustSdkTools.createPopUp(popUpView);

            final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
            final ImageButton btnClose = popUpView.findViewById(R.id.assessmentOtp_btnClose);
            final EditText edittext_mob = popUpView.findViewById(R.id.edittext_phoneno);
            final EditText edittext_email = popUpView.findViewById(R.id.edittext_mail);
            final TextView confirm_titletxt = popUpView.findViewById(R.id.confirm_titletxt);
            final TextView confirm_subtitletxt = popUpView.findViewById(R.id.confirm_subtitletxt);
            confirm_subtitletxt.setVisibility(View.GONE);

            confirm_titletxt.setText(getResources().getString(R.string.enter_user_details));
            edittext_mob.setHint(getResources().getString(R.string.enter_mobile_number));
            edittext_email.setHint(getResources().getString(R.string.enter_email_id));
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            if ((OustPreferences.get("eventuserphonenumber") != null) && (!OustPreferences.get("eventuserphonenumber").isEmpty())) {
                edittext_mob.setText(OustPreferences.get("eventuserphonenumber"));
            } else {
                if (OustAppState.getInstance().getActiveUser().getUserMobile() > 1000) {
                    edittext_mob.setText(OustAppState.getInstance().getActiveUser().getUserMobile() + "");
                }
            }
            if (OustAppState.getInstance().getActiveUser().getEmail() != null) {
                edittext_email.setText(OustAppState.getInstance().getActiveUser().getEmail());
            }
            edittext_mob.requestFocus();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view, 100);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittext_mob.getWindowToken(), 0);
                    clickOnSendOtpButton(edittext_mob.getText().toString(), edittext_email.getText().toString());
                }
            });
            edittext_mob.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if ((enterMobileNoPOpup != null) && (enterMobileNoPOpup.isShowing())) {
                            enterMobileNoPOpup.dismiss();
                        }
                        return true;
                    }
                    return false;
                }
            });
            edittext_email.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == EditorInfo.IME_ACTION_GO || keyCode == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edittext_email.getWindowToken(), 0);
                        clickOnSendOtpButton(edittext_mob.getText().toString(), edittext_email.getText().toString());
                        return true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        if ((enterMobileNoPOpup != null) && (enterMobileNoPOpup.isShowing())) {
                            enterMobileNoPOpup.dismiss();
                        }
                        return true;
                    }
                    return false;
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittext_mob.getWindowToken(), 0);
                    enterMobileNoPOpup.dismiss();
                }
            });
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void closeMobilePopup() {
        try {
            if ((enterMobileNoPOpup != null) && (enterMobileNoPOpup.isShowing())) {
                enterMobileNoPOpup.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void saveMobileNo(String mobileNo) {
        OustPreferences.save("eventuserphonenumber", mobileNo);
    }

    public void sendOtpRequest(SendAssessmentOtpRequest sendAssessmentOtpRequest) {

        final CommonResponse[] commonResponses = {null};
        try {
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(sendAssessmentOtpRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String assessmentotp_url = OustSdkApplication.getContext().getResources().getString(R.string.assessmentotp_url);
            assessmentotp_url = HttpManager.getAbsoluteUrl(assessmentotp_url);

            /*JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, assessmentotp_url, parsedJsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        sendOtpRequestOver(commonResponses[0]);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonReq.setShouldCache(false);
            jsonReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonReq, "first");*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, assessmentotp_url, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        sendOtpRequestOver(commonResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });


        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
            Log.d("Error", e.toString());
        }
    }

    //    public void sendOtpRequestOver(CommonResponse commonResponse){
//        sendOtpRequestOver(commonResponse);
//    }
    public void sendOtpRequestOver(CommonResponse commonResponse) {
        try {
            hideStartingLoader();
            if (commonResponse != null) {
                if (commonResponse.isSuccess()) {
                    sendOtpRequest = true;
                    showConfirmOptPopup();
                } else {
                    if (commonResponse.getPopup() != null) {
                        showPopup(commonResponse.getPopup(), activeGame);
                    } else if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                        showToastMessage(commonResponse.getError());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void showConfirmOptPopup() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.eventconfirm_popup, null);
            confirmOTPPopup = OustSdkTools.createPopUp(popUpView);

            final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
            final ImageButton btnClose = popUpView.findViewById(R.id.eventOtp_btnClose);
            final EditText editText_otp = popUpView.findViewById(R.id.edittext_otp);
            final TextView otpresendtxt = popUpView.findViewById(R.id.resentotp_txt);
            final TextView confirm_subtitletxt = popUpView.findViewById(R.id.confirm_subtitletxt);
            confirm_subtitletxt.setVisibility(View.GONE);
            editText_otp.requestFocus();

            Spanned contentText = Html.fromHtml(getResources().getString(R.string.click_to_resend));
            otpresendtxt.setText(contentText);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view, 100);
                    if ((editText_otp.getText().toString().length() > 2)) {
                        confirmOTPPopup.dismiss();
                        clickOnConfirmOtpPopup(editText_otp.getText().toString());
                    } else {
                        OustSdkTools.showToast(getResources().getString(R.string.invalid_otp));
                    }
                }
            });
            otpresendtxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OustSdkTools.oustTouchEffect(view, 100);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_otp.getWindowToken(), 0);
                    clickOnSendOtpButton(OustPreferences.get("eventuserphonenumber"), OustAppState.getInstance().getActiveUser().getEmail());
                    confirmOTPPopup.dismiss();
                }
            });
            editText_otp.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    try {
                        if (keyCode == EditorInfo.IME_ACTION_GO || keyCode == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText_otp.getWindowToken(), 0);
                            if ((editText_otp.getText().toString().length() > 2)) {
                                confirmOTPPopup.dismiss();
                                clickOnConfirmOtpPopup(editText_otp.getText().toString());
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.invalid_otp));
                            }
                        } else if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if ((confirmOTPPopup != null) && (confirmOTPPopup.isShowing())) {
                                confirmOTPPopup.dismiss();
                            }
                            return false;
                        }
                    } catch (Exception e) {
                    }
                    return false;
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_otp.getWindowToken(), 0);
                    confirmOTPPopup.dismiss();
                }
            });
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void clickOnConfirmOtpPopup(String otp) {
        try {
            showStartingLoaderAgain();
            otp = otp.trim();
            ConfirmAssessmentOtpRequest confirmAssessmentOtpRequest = new ConfirmAssessmentOtpRequest();
            confirmAssessmentOtpRequest.setStudentid(activeUser.getStudentid());
            confirmAssessmentOtpRequest.setOtp(otp);
            confirmAssessmentOtpRequest.setAssessmentId("" + assessmentFirebaseClass.getAsssessemntId());
            sendConfirmOtpRequest(confirmAssessmentOtpRequest);
        } catch (Exception e) {
        }
    }

    public void sendConfirmOtpRequest(ConfirmAssessmentOtpRequest confirmAssessmentOtpRequest) {
        final CommonResponse[] commonResponses = {null};
        try {
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(confirmAssessmentOtpRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String assessmentconfirmotp_url = OustSdkApplication.getContext().getResources().getString(R.string.assessmentconfirmotp_url);
            assessmentconfirmotp_url = HttpManager.getAbsoluteUrl(assessmentconfirmotp_url);

            /*JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, assessmentconfirmotp_url, parsedJsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        confirmOtpProcessOver(commonResponses[0]);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonReq.setShouldCache(false);
            jsonReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonReq, "first");*/

            ApiCallUtils.doNetworkCall(Request.Method.POST, assessmentconfirmotp_url, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        confirmOtpProcessOver(commonResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });


        } catch (Exception e) {
            Log.e("AssessmentOtpReq ", "ERROR: ", e);
        }
    }

    public void confirmOtpProcessOver(CommonResponse commonResponse) {
        try {
            hideStartingLoader();
            if (commonResponse != null) {
                if (commonResponse.isSuccess()) {
                    showDownloadPopup();
                    if (assessmentFirebaseClass.isEnrolled()) {
                        createAssessmentGame();
                    } else {
                        enrolledAssessmentFirst("");
                    }
                } else {
                    if (commonResponse.getPopup() != null) {
                        showPopup(commonResponse.getPopup(), activeGame);
                    } else if ((commonResponse.getError() != null) && (!commonResponse.getError().isEmpty())) {
                        showToastMessage(commonResponse.getError());
                    }
                }
            }
        } catch (Exception e) {
        }
    }
//=============================================================================================================

    public void submitAssessment(SubmitRequest submitRequest) {
        final SubmitResponse[] submitResponses = {null};
        try {
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            parsedJsonParams.put("contentPlayListId", OustPreferences.getTimeForNotification("cplId_assessment"));
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);


            /*JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, submitGameUrl, parsedJsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitProcessOver(submitResponses[0]);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    try {
                        params.put("api-key", OustPreferences.get("api_key"));
                        params.put("org-id", OustPreferences.get("tanentid"));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    return params;
                }
            };
            jsonReq.setShouldCache(false);
            jsonReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonReq, "first");*/

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((response != null) && (response.length() > 0)) {
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitProcessOver(submitResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }

    }

    public void submitProcessOver(SubmitResponse submitResponse) {
        gotAssessmentSubmitResponce(submitResponse);
    }

    public void gotAssessmentSubmitResponce(SubmitResponse submitResponse) {
        hideStartingLoader();
        if ((submitResponse != null) && (submitResponse.isSuccess())) {
            try {
                isEnrolled = true;
                showSubmittedMsg();
                chnageFireBaseAssessmentStatus(assessmentPlayResponce, activeUser, assessmentFirebaseClass.getAsssessemntId());
                checkForAssessmentComplete(activeUser, assessmentFirebaseClass.getAsssessemntId());
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void chnageFireBaseAssessmentStatus(AssessmentPlayResponse assessmentPlayResponce, ActiveUser activeUser, long assessmentId) {
//        String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
        String node = "";
        node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
        if (isMultipleCplEnable) {
            node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + isCplId + "/contentListMap/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
        } else {
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
        assessmentPlayResponce.setAssessmentState(AssessmentState.SUBMITTED);
        OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponce);
//        String completeAssessmentsNode = "/userAssessmentProgress/" + "/completedAssessments/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
//        OustFirebaseTools.getRootRef().child(completeAssessmentsNode).setValue(null);
    }

    //----------------------------------------------------
//fetch complete assessment if coming form branch io
    public void fetchAssessmentsFromFirebase(String assessmentId) {
        try {
//            Map<String,Object> dataMap = OustAppState.getInstance().getModuleDataMap();
//            if(dataMap!=null){
//                extractAsssemntData(dataMap);
//                OustAppState.getInstance().setModuleDataMap(null);
//            }else {
            final String message = "/assessment/assessment" + assessmentId;
            Log.d(TAG, "fetchAssessmentsFromFirebase: " + message);
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> assessmentMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (null != assessmentMap) {
                            extractAsssemntData(assessmentMap);
                        } else {
                            gotAssessmentFromFirebase(null);
                        }
                    } catch (Exception e) {
                        gotAssessmentFromFirebase(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    gotAssessmentFromFirebase(null);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(myassessmentListener);
        } catch (Exception e) {
            gotAssessmentFromFirebase(null);
        }
    }

    private void extractAsssemntData(Map<String, Object> assessmentMap) {
        AssessmentFirebaseClass assessmentFirebaseClass = new AssessmentFirebaseClass();
        try {
            if (assessmentMap.get("active") != null) {
                assessmentFirebaseClass.setActive((boolean) assessmentMap.get("active"));
            }
            if (assessmentMap.get("reattemptAllowed") != null) {
                assessmentFirebaseClass.setReattemptAllowed((boolean) assessmentMap.get("reattemptAllowed"));
            }
            if (assessmentMap.get("showQuestionsOnCompletion") != null) {
                assessmentFirebaseClass.setShowQuestionsOnCompletion((boolean) assessmentMap.get("showQuestionsOnCompletion"));
                OustPreferences.saveAppInstallVariable("showQuestionsOnCompletion", assessmentFirebaseClass.isShowQuestionsOnCompletion());
            }
            if (assessmentMap.get("cplId") != null) {
                long cplId = OustSdkTools.convertToLong(assessmentMap.get("cplId"));
                OustPreferences.saveTimeForNotification("cplId_assessment", cplId);
            } else {
                OustPreferences.saveTimeForNotification("cplId_assessment", 0);
            }
            if (assessmentMap.get("assessmentId") != null) {
                assessmentFirebaseClass.setAsssessemntId(OustSdkTools.convertToLong(assessmentMap.get("assessmentId")));
            }
            if (assessmentMap.get("isTTSEnabled") != null) {
                assessmentFirebaseClass.setTTSEnabled((boolean) assessmentMap.get("isTTSEnabled"));
            }
            if (assessmentMap.get("backgroundAudioForAssessment") != null) {
                assessmentFirebaseClass.setBackgroundAudioForAssessment((boolean) assessmentMap.get("backgroundAudioForAssessment"));
            }
            if (assessmentMap.get("rules") != null) {
                assessmentFirebaseClass.setRules((List<String>) assessmentMap.get("rules"));
            }
            if (assessmentMap.get("showAssessmentRemark") != null) {
                assessmentFirebaseClass.setShowAssessmentRemark((boolean) assessmentMap.get("showAssessmentRemark"));
            } else {
                assessmentFirebaseClass.setShowAssessmentRemark(false);
            }
            if (assessmentMap.get("showAssessmentResultScore") != null) {

                //assessmentFirebaseClass.setShowAssessmentResultScore(false);
                assessmentFirebaseClass.setShowAssessmentResultScore((boolean) assessmentMap.get("showAssessmentResultScore"));
            } else {
                assessmentFirebaseClass.setShowAssessmentResultScore(false);
            }
            if (assessmentMap.get("assessmentResult") != null) {
                assessmentFirebaseClass.initAssessmentResultBadges((ArrayList<Object>) assessmentMap.get("assessmentResult"));
            }
            if (assessmentMap.get("certificate") != null) {
                assessmentFirebaseClass.setCertificate((boolean) assessmentMap.get("certificate"));
            }
            if (assessmentMap.get("participants") != null) {
                assessmentFirebaseClass.setEnrolledCount((long) assessmentMap.get("participants"));
            }
            if (assessmentMap.get("isSecureAssessment") != null) {
                assessmentFirebaseClass.setSecureAssessment((boolean) assessmentMap.get("isSecureAssessment"));
            }
            if (assessmentMap.get("numQuestions") != null) {
                Log.d(TAG, "extractAsssemntData: " + assessmentMap.get("numQuestions"));
                assessmentFirebaseClass.setNumQuestions(OustSdkTools.convertToLong(assessmentMap.get("numQuestions")));
                userEventAssessmentData.setnTotalQuestions((int) OustSdkTools.convertToLong(assessmentMap.get("numQuestions")));
            }
            if (assessmentMap.get("banner") != null) {
                assessmentFirebaseClass.setBanner((String) assessmentMap.get("banner"));
            }
            if (assessmentMap.get("bgImg") != null) {
                assessmentFirebaseClass.setBgImg((String) assessmentMap.get("bgImg"));
            }
            if (assessmentMap.get("description") != null) {
                assessmentFirebaseClass.setDescription((String) assessmentMap.get("description"));
            }
            if (assessmentMap.get("endDate") != null) {
                assessmentFirebaseClass.setEnddate((String) assessmentMap.get("endDate"));
            }

            if (assessmentMap.get("secureSessionOn") != null) {
                assessmentFirebaseClass.setSecureSessionOn((boolean) assessmentMap.get("secureSessionOn"));
            } else {
                assessmentFirebaseClass.setSecureSessionOn(true);
            }

            if (assessmentMap.get("name") != null) {
                assessmentFirebaseClass.setName((String) assessmentMap.get("name"));
            }
            if (assessmentMap.get("passcode") != null) {
                assessmentFirebaseClass.setPasscode((String) assessmentMap.get("passcode"));
            }
            if (assessmentMap.get("scope") != null) {
                assessmentFirebaseClass.setScope((String) assessmentMap.get("scope"));
            }
            if (assessmentMap.get("questionXp") != null) {
                assessmentFirebaseClass.setQuestionXp(OustSdkTools.convertToLong(assessmentMap.get("questionXp")));
            }
            if (assessmentMap.get("disablePartialMarking") != null) {
                assessmentFirebaseClass.setDisablePartialMarking((boolean) assessmentMap.get("disablePartialMarking"));
            } else {
                assessmentFirebaseClass.setDisablePartialMarking(false);
            }
            if (assessmentMap.get("minPassCorrectAnswer") != null) {
                assessmentFirebaseClass.setMinPassCorrectAnswer((long) assessmentMap.get("minPassCorrectAnswer"));
            }
            if (assessmentMap.get("passPercentage") != null) {
                assessmentFirebaseClass.setPassPercentage(OustSdkTools.convertToLong(assessmentMap.get("passPercentage")));
            }
            try {
                if (assessmentMap.get("type") != null) {
                    assessmentFirebaseClass.setAssessmentType(AssessmentType.valueOf((String) assessmentMap.get("type")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (assessmentMap.get("startDate") != null) {
                startTime = Long.parseLong((String) assessmentMap.get("startDate"));
                assessmentFirebaseClass.setStartdate((String) assessmentMap.get("startDate"));
            }
            if (assessmentMap.get("logo") != null) {
                assessmentFirebaseClass.setLogo((String) assessmentMap.get("logo"));
            }
            if (assessmentMap.get("module") != null) {
                Map<String, Object> language = (Map<String, Object>) assessmentMap.get("module");
                Map<String, String> moduleMap = (Map<String, String>) language.get("language");
                assessmentFirebaseClass.setModulesMap(moduleMap);
            }
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                assessmentFirebaseClass.setHideLeaderboard(true);
            } else {
                if (assessmentMap.get("hideLeaderboard") != null) {
                    assessmentFirebaseClass.setHideLeaderboard((boolean) assessmentMap.get("hideLeaderboard"));
                }
            }
            if (assessmentMap.get("showPercentage") != null) {
                assessmentFirebaseClass.setShowPercentage((boolean) assessmentMap.get("showPercentage"));
            }
            if (assessmentMap.get("otpVerification") != null) {
                assessmentFirebaseClass.setOtpVerification((boolean) assessmentMap.get("otpVerification"));
            }
            if (assessmentMap.get("resultMessage") != null) {
                assessmentFirebaseClass.setResultMessage((String) assessmentMap.get("resultMessage"));
            }
            String status = "";
            try {
                if (assessmentMap.get("state") != null) {
                    status = (String) assessmentMap.get("state");
                    assessmentFirebaseClass.setAssessmentState(status);
                }
            } catch (Exception e) {
            }

            if (assessmentMap.get("assessmentOverMessage") != null) {
                assessmentFirebaseClass.setAssessmentOverMessage((String) assessmentMap.get("assessmentOverMessage"));
            }

            if (assessmentMap.get("resumeSameQuestion") != null) {
                assessmentFirebaseClass.setResumeSameQuestion((boolean) assessmentMap.get("resumeSameQuestion"));
            }

            if (assessmentMap.get("timePenaltyDisabled") != null) {
                assessmentFirebaseClass.setTimePenaltyDisabled((boolean) assessmentMap.get("timePenaltyDisabled"));
            }

            if (assessmentMap.get("assessmentMedia") != null) {
                List<CourseCardMedia> assessmentMediaList = new ArrayList<>();
                ArrayList<Object> assessmentMediaArrayList = (ArrayList<Object>) assessmentMap.get("assessmentMedia");
                for (int i = 0; i < assessmentMediaArrayList.size(); i++) {
                    CourseCardMedia courseCardMedia = new CourseCardMedia();
                    Map<Object, Object> mediaMap = (Map<Object, Object>) assessmentMediaArrayList.get(i);
                    if (mediaMap.get("mediaType") != null)
                        courseCardMedia.setMediaType((String) mediaMap.get("mediaType"));
                    if (mediaMap.get("mediaKey") != null) {
                        courseCardMedia.setData((String) mediaMap.get("mediaKey"));
                    }
                    assessmentMediaList.add(courseCardMedia);
                }
                assessmentFirebaseClass.setAssessmentMediaList(assessmentMediaList);
            }

            if (assessmentMap.get("noOfAttemptAllowedToPass") != null) {
                assessmentFirebaseClass.setNoOfAttemptAllowedToPass(OustSdkTools.convertToLong(assessmentMap.get("noOfAttemptAllowedToPass")));
            }
            if (assessmentMap.get("courseAssociated") != null) {
                isCourseAttached = (boolean) assessmentMap.get("courseAssociated");
                assessmentFirebaseClass.setCourseAssociated((boolean) assessmentMap.get("courseAssociated"));
            } else {
                assessmentFirebaseClass.setCourseAssociated(false);
            }

            if (assessmentMap.get("mappedCourseId") != null) {
                mappedCourseID = (long) assessmentMap.get("mappedCourseId");
                assessmentFirebaseClass.setMappedCourseId((long) assessmentMap.get("mappedCourseId"));
            }

            if (isCourseAttached && mappedCourseID != 0) {
                //getUserCourses(mappedCourseID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (!assessmentFirebaseClass.isShowAssessmentResultScore()) {
            setUIForPSYCHOMETRICAssessment();
        }
        gotAssessmentFromFirebase(assessmentFirebaseClass);
    }

    public void gotAssessmentFromFirebase(AssessmentFirebaseClass assessmentFirebaseClass) {
        if (assessmentFirebaseClass != null) {
            this.assessmentFirebaseClass = assessmentFirebaseClass;
            OustAppState.getInstance().setAssessmentFirebaseClass(assessmentFirebaseClass);
            checkForAssessmentComplete(activeUser, assessmentFirebaseClass.getAsssessemntId());
        } else {
            showToastMessage(getResources().getString(R.string.assessment_no_longer));
            killClass();
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

    //===================================================================================================================================================
    public void shareButtonClick() {
        Log.d(TAG, "shareButtonClick: ");
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AssessmentPlayActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        } else {
            OustShareTools.shareScreenAndBranchIo(AssessmentPlayActivity.this, assessment_mainlayout, getResources().getString(R.string.oust_share_text), OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId() + "");
        }
    }


    public Bitmap getScreenShot(View resultView) {
        Bitmap screenBitmap = OustSdkTools.getInstance().getScreenShot(resultView);
        return screenBitmap;
    }

    //    <----------------------------------------------------------------------------------------------->
//    <----------------------------------------------------------------------------------------------->

    public void deleteAssessment(String assessmentID) {
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String fileName = "assessment" + assessmentID + ".txt";
            enternalPrivateStorage.deleteFile(fileName);
        } catch (Exception e) {
        }
    }

    public void clickOnSendOtpButton(String mobileNo, String email) {
        try {
            if ((mobileNo.length() < 4)) {
                showToastMessage(getResources().getString(R.string.enter_valid_mobile));
                return;
            }
            if (email != null) {
                if (!isValidEmail(email)) {
                    showToastMessage(getResources().getString(R.string.enter_valid_mail));
                    return;
                }
            }
            showStartingLoaderAgain();
            closeMobilePopup();
            saveMobileNo(mobileNo);
            activeUser.setEmail(email);
            OustAppState.getInstance().getActiveUser().setEmail(email);
            mobileNo = mobileNo.trim();
            SendAssessmentOtpRequest sendAssessmentOtpRequest = new SendAssessmentOtpRequest();
            sendAssessmentOtpRequest.setStudentid(activeUser.getStudentid());
            sendAssessmentOtpRequest.setMobile(mobileNo);
            sendAssessmentOtpRequest.setEmailId(email);
            sendAssessmentOtpRequest.setAssessmentId("" + assessmentFirebaseClass.getAsssessemntId());
            sendOtpRequest(sendAssessmentOtpRequest);
        } catch (Exception e) {
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

    private long netTime = 0;

    private void checkForDeviceNetTime() {
        try {
            int status = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
            if (status == 1) {
                netTime = System.currentTimeMillis();
                setBtnStatus();
            } else {
                showPopup("Failed to get internet time, Please go to device setting and enable network time.", true);
                AssessmentPlayActivity.this.finish();
            }
        } catch (Exception e) {
        }
    }

    private void setBtnStatus() {
        if ((startTime == 0) || (netTime == 0)) {
            showButtons();
        } else {
            if (netTime < startTime) {
                btnlayout.setVisibility(View.GONE);
                timer_layout.setVisibility(View.VISIBLE);
                remainingTimeInSec = (startTime - netTime) / 1000;
                setTimerText();
                startTimer();
            } else {
                showButtons();
            }
        }
    }

    private void showButtons() {
        btnlayout.setVisibility(View.VISIBLE);
        timer_layout.setVisibility(View.GONE);
    }

    private CounterClass timer;
    private long remainingTimeInSec = 0;

    public void startTimer() {
        try {
            timer = new CounterClass(remainingTimeInSec * 1000, getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
        }
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            showButtons();
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            remainingTimeInSec--;
            setTimerText();
        }
    }

    private void setTimerText() {
        Log.d(TAG, "setTimerText remainig sec: " + remainingTimeInSec);
        if (remainingTimeInSec > 86400) {
            long assessmentRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
            long assessmentRemainingTimeMins = ((remainingTimeInSec % 86400) % 3600) / 60;
            String hms = String.format("%02d : %02d : %02d", remainingTimeInSec / 86400, assessmentRemainingTimeHours, assessmentRemainingTimeMins);
            waittimer_text.setText(hms);
        } else {
            long assessmentRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
            long assessmentRemainingTimeMins = ((remainingTimeInSec) % 3600) / 60;
            long assessmentRemainingTimeSec = ((remainingTimeInSec) % 3600) % 60;
            String hms = String.format("%02d : %02d : %02d", assessmentRemainingTimeHours, assessmentRemainingTimeMins, assessmentRemainingTimeSec);
            waittimer_text.setText(hms);
        }
    }

    private void showPopup(String content, boolean gotoSetting) {
        try {
            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText(getResources().getString(R.string.ok));
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(content);
            if (gotoSetting) {
                popup.setType(OustPopupType.REDIRECT_SETTING_PAGE);
                popup.setCategory(OustPopupCategory.REDIRECT);
                oustPopupButton.setBtnText(getResources().getString(R.string.go_to_setting));
            } else {
                popup.setCategory(OustPopupCategory.NOACTION);
            }
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(this, PopupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}

