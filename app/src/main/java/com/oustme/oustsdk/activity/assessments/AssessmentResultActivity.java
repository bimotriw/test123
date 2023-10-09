package com.oustme.oustsdk.activity.assessments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.firebase.assessment.AssesmentResultBadge;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.assessment.AssessmentResultActivityEndInterface;
import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.model.request.Moduledata;
import com.oustme.oustsdk.presenter.assessments.AssessmentResultActivityPresenter;
import com.oustme.oustsdk.request.SendCertificateRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CplDataHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustShareTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

//import io.realm.Realm;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class AssessmentResultActivity extends AppCompatActivity implements View.OnClickListener, AssessmentResultActivityEndInterface {

    private static final String TAG = "AssessmentResultActivit";
    private ImageView assessmentresult_logo, assessmentresult_resultSymbol, shareImage, assessment_bgImg;
    private TextView assessmentresult_resultTitle, assessmentresult_resultText, userright_ans, userwrong_ans,
            userxpvalue, rightCenter, wrongCenter, xpValueCenter, assessmentname_txt,
            txtShare, assessment_totalques, assessment_totlepeople, userright_presentage, participants_text, timetaken_title,
            userright_presentagelable, assessmentresultMessage_text, leaderboard_text, total_ques_text, timetaken_text;
    private LinearLayout assessmentresult_scoreLayout1, assessment_mainlayout, assessmentresult_checkResultLayout;
    private RelativeLayout closebtn_layout, assessmentresult_mainLayout, scoreWaitingLayout, showpresentageLayout;
    private ImageButton assessmentclose_btn;
    private ImageView leaderboardImage, like_imageview;
    private LinearLayout assessmentleaderboardbtn, assessmentshare_btn,assessment_popup_answers,
            assessment_popup_lb;
    private WebView certificate_webview;

    private LinearLayout assessment_result_popup, okbtn;
    private TextView assessment_name, assessment_status_msg, assessment_success_msg, score, total_score, pass_mark, pass_total_score, attempt_mark;
    private ImageView assessment_status_img;
    private View view_bar2;
    private LinearLayout attempt_count_ll;

    private ActiveGame activeGame;
    private ActiveUser activeUser;
    private SubmitRequest submitRequest;
    private PlayResponse playResponse;
    private MediaPlayer mediaPlayer;
    private boolean shouldShowAnswers = true;
    private Gson gson;
    private boolean isCplModule;

    private String courseId, courseColnId;
    private boolean containCertificate = false;
    public AssessmentResultActivityPresenter presenter;
    private float totaltimetaken = 0;
    private TextView result_msg;
    private boolean isFromCourse;
    private boolean courseAssociated;
    private long mappedCourseId;
    private boolean assessmentPass;
    private boolean isMappedCourseDistributed;
    boolean reAttemptAllowed;
    long nAttemptCount,nAttemptAllowedToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.assessment_result);
            try{
                OustSdkTools.setLocale(AssessmentResultActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            setContentView(R.layout.activity_assessmentresult);
            initViews();
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(getApplicationContext());
            }
            OustAppState.getInstance().setAssessmentResultActivityEndInterface(this);
            if (OustAppState.getInstance().getAssessmentFirebaseClass() != null) {
                initEventResult();
            } else {
                AssessmentResultActivity.this.finish();
            }
//            OustGATools.getInstance().reportPageViewToGoogle(AssessmentResultActivity.this, "Assessment Result Page");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("result", "Exception occured in onCreate()", e);
        }
    }

    private void initViews() {
        try {
            assessmentresult_logo = findViewById(R.id.assessmentresult_logo);
//        assessmentresult_resultSymbol = (ImageView) findViewById(R.id.assessmentresult_resultSymbol);
            assessmentresult_resultTitle = findViewById(R.id.assessmentresult_resultTitle);
            assessmentresult_resultText = findViewById(R.id.assessmentresult_resultText);
            assessmentresult_scoreLayout1 = findViewById(R.id.assessmentresult_scoreLayout1);
            userright_ans = findViewById(R.id.userright_ans);
            userwrong_ans = findViewById(R.id.userwrong_ans);
            userxpvalue = findViewById(R.id.userxpvalue);
            rightCenter = findViewById(R.id.rightCenter);
            wrongCenter = findViewById(R.id.wrongCenter);
            xpValueCenter = findViewById(R.id.xpValueCenter);
            assessment_mainlayout = findViewById(R.id.assessment_mainlayout);
//        assessmentresult_checkResultLayout = (LinearLayout) findViewById(R.id.assessmentresult_checkResultLayout);
            closebtn_layout = findViewById(R.id.closebtn_layout);
            assessmentclose_btn = findViewById(R.id.assessmentclose_btn);
            assessmentname_txt = findViewById(R.id.assessmentname_txt);
            assessmentleaderboardbtn = findViewById(R.id.assessmentleaderboardbtn);
            leaderboard_text = findViewById(R.id.leaderboard_text);
            leaderboardImage = findViewById(R.id.leaderboardImage);
            txtShare = findViewById(R.id.txtShare);
            assessmentresult_mainLayout = findViewById(R.id.assessmentresult_mainLayout);
            assessment_totalques = findViewById(R.id.assessment_totalques);
            timetaken_title = findViewById(R.id.timetaken_title);
            total_ques_text = findViewById(R.id.total_ques_text);
            timetaken_text = findViewById(R.id.timetaken_text);
            assessment_totlepeople = findViewById(R.id.assessment_totlepeople);
            participants_text = findViewById(R.id.participants_text);
            assessment_bgImg = findViewById(R.id.assessment_bgImg);
            like_imageview = findViewById(R.id.like_imageview);
            result_msg = findViewById(R.id.result_msg);
            attempt_mark = findViewById(R.id.attempt_mark);
            assessment_result_popup = findViewById(R.id.assessment_result_popup);
            okbtn = findViewById(R.id.okbtn);
            assessment_name = findViewById(R.id.assessment_name);
            assessment_status_msg = findViewById(R.id.assessment_status_msg);
            assessment_success_msg = findViewById(R.id.assessment_success_msg);
            score = findViewById(R.id.score);
            total_score = findViewById(R.id.total_score);
            pass_mark = findViewById(R.id.pass_mark);
            pass_total_score = findViewById(R.id.pass_total_score);
            assessment_status_img = findViewById(R.id.assessment_status_img);
            attempt_count_ll = findViewById(R.id.attempt_count_ll);
            view_bar2 = findViewById(R.id.view_bar2);

            assessment_popup_answers= findViewById(R.id.assessment_popup_answers);
            assessment_popup_lb= findViewById(R.id.assessment_popup_lb);

            assessment_popup_lb.setOnClickListener(this);
            assessment_popup_answers.setOnClickListener(this);
//        scoreWaitingLayout = (RelativeLayout) findViewById(R.id.scoreWaitingLayout);
//        showpresentageLayout = (RelativeLayout) findViewById(R.id.showpresentageLayout);
//        userright_presentage = (TextView) findViewById(R.id.userright_presentage);
//        userright_presentagelable = (TextView) findViewById(R.id.userright_presentagelable);
//        assessmentresultMessage_text = (TextView) findViewById(R.id.assessmentresultMessage_text);
            certificate_webview = findViewById(R.id.certificate_webview);
            assessmentshare_btn = findViewById(R.id.assessmentshare_btn);
            //assessmentshare_btn_background = (ImageView) findViewById(R.id.assessmentshare_btn_background);
            shareImage = findViewById(R.id.shareImage);

            OustSdkTools.setImage(assessmentresult_logo, getResources().getString(R.string.mydesk));
            OustSdkTools.setImage(like_imageview, getResources().getString(R.string.thumbsup_icon));
            assessmentshare_btn.setOnClickListener(this);

//        OustSdkTools.setImage(assessmentresult_resultSymbol, getResources().getString(R.string.win));
            //OustSdkTools.setImage(assessmentshare_btn_background, getResources().getString(R.string.share));
            OustSdkTools.setImage(shareImage, getResources().getString(R.string.share_new_icon));

            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(courseAssociated && mappedCourseId!=0){
                        startCourse();
                    }

                    if(isFromCourse){
                        if( isAssessmentPassed){
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                        }else{
                            if(reAttemptAllowed&&(nAttemptCount>=nAttemptAllowedToPass)){
                                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                            }else{
                                OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                            }

                        }
                    }else{
                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    }

                    try {
                        updateLevel ob = (updateLevel)AssessmentResultActivity.this;
                        ob.updateLevelFromAssessment();
                    }catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    //Re-distributeCPL
                    if(assessment_success_msg.getText().toString().equalsIgnoreCase("You Have Failed This Module")) {
                        Log.d(TAG, "onClick: Assessment failure");
                        if (OustStaticVariableHandling.getInstance().getCplCloseListener() != null) {
                            //int attemptCount = ((int) OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass() - 1) - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
                            OustStaticVariableHandling.getInstance().getCplCloseListener().onAssessmentFail((CplDataHandler.getInstance().getCplAssessmentAttemptCount() + 1), (OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass()) <= (CplDataHandler.getInstance().getCplAssessmentAttemptCount() + 1));
                        }
                    }
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("result", "Exception occured in initViews()", e);
        }
    }

    public interface updateLevel {
        void updateLevelFromAssessment();
    }

    public void initEventResult() {
        try {
            OustAppState.getInstance().setHasPopup(false);
            OustSdkTools.setSnackbarElements(assessment_mainlayout, AssessmentResultActivity.this);
            OustSdkTools.checkInternetStatus();
            shouldShowAnswers = OustAppState.getInstance().getAssessmentFirebaseClass().isShowQuestionsOnCompletion();
            presenter = new AssessmentResultActivityPresenter();
            gson = new GsonBuilder().create();
            Intent CallingIntent = getIntent();
            isCplModule = CallingIntent.getBooleanExtra("isCplModule", false);

            courseId = CallingIntent.getStringExtra("courseId");
            courseColnId = CallingIntent.getStringExtra("courseColnId");
            totaltimetaken = CallingIntent.getFloatExtra("totalTimeTaken", 0);
            containCertificate = CallingIntent.getBooleanExtra("containCertificate", false);
            courseAssociated = CallingIntent.getBooleanExtra("courseAssociated", false);
            mappedCourseId = CallingIntent.getLongExtra("mappedCourseId", 0);
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                nAttemptAllowedToPass = bundle.getLong("nAttemptAllowedToPass", 0);
                nAttemptCount = bundle.getLong("nAttemptCount", 0);
                reAttemptAllowed = bundle.getBoolean("reAttemptAllowed", false);
                isFromCourse = bundle.getBoolean("IS_FROM_COURSE", false);
            }
            if(isFromCourse){
                OustStaticVariableHandling.getInstance().setComingFromAssessment(true);
            }
            activeUser = OustAppState.getInstance().getActiveUser();
            setTextTypeFace();
            if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            } else {
                HttpManager.setBaseUrl();
                OustFirebaseTools.initFirebase();
                String activeUserGet = OustPreferences.get("userdata");
                activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            }
            activeGame = OustSdkTools.getAcceptChallengeData(CallingIntent.getStringExtra("ActiveGame"));
            submitRequest = OustSdkTools.getSubmit(CallingIntent.getStringExtra("SubmitRequest"));
            playResponse = OustAppState.getInstance().getPlayResponse();
            if (isCplModule || OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentRemark()) {
                assessment_result_popup.setVisibility(View.VISIBLE);
                assessment_mainlayout.setVisibility(View.GONE);
                setAssessmentResultData();
            } else {
                assessment_mainlayout.setVisibility(View.VISIBLE);
                if(OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentResultScore())
                {
                    assessmentresult_scoreLayout1.setVisibility(View.VISIBLE);
                }
                else{
                    assessmentresult_scoreLayout1.setVisibility(View.GONE);
                }
                assessment_result_popup.setVisibility(View.GONE);
            }
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getBanner() != null) &&
                    (!OustAppState.getInstance().getAssessmentFirebaseClass().getBanner().isEmpty())) {
                Picasso.get().load(OustAppState.getInstance().getAssessmentFirebaseClass().getBanner()).into(assessmentresult_logo);
            }

            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getBgImg() != null) &&
                    (!OustAppState.getInstance().getAssessmentFirebaseClass().getBgImg().isEmpty())) {
                Picasso.get().load(OustAppState.getInstance().getAssessmentFirebaseClass().getBgImg()).into(assessment_bgImg);
            }

//            android.view.animation.Animation top_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animmovetop);
//            assessmentresult_checkResultLayout.startAnimation(top_anim);
            verifyResult();
            showstored_Popup();
            assessmentleaderboardbtn.setOnClickListener(this);
            closebtn_layout.setOnClickListener(this);
            assessmentclose_btn.setOnClickListener(this);
            if (isCplModule) {
                //getCplAssessmentStatus();
            } else {
                if(!OustPreferences.getAppInstallVariable("showQuestionsOnCompletion")) {
                    //deleteAssessmentMedia();
                }
                getAssessmentStatus();
            }
            Log.e("result", OustAppState.getInstance().getAssessmentFirebaseClass().toString());
        } catch (Exception e) {
            Log.e("result", "Exception occured in initEventResult()", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAssessmentMedia() {
        try {
            if (playResponse != null && playResponse.getqIdList() != null) {
                List<String> mediaList = new ArrayList<>();
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    DTOQuestions q = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(i));
                    if (q != null) {
                        OustMediaTools.prepareMediaList(mediaList, mediaList, q);
                    }
                }
                for (int i = 0; i < mediaList.size(); i++) {
                    String mediaPath = mediaList.get(i);
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        mediaPath = OustMediaTools.getMediaFileName(mediaPath);
                    }
                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    enternalPrivateStorage.deleteMediaFile(("oustlearn_" + mediaPath));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getCplAssessmentStatus() {
        try {
            String message = "/landingPage/" + activeUser.getStudentKey() + "/cpl/" + OustPreferences.getTimeForNotification("cplId_assessment") + "/contentListMap/ASSESSMENT" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            Log.e("-----result", dataSnapshot.toString());
                            final Map<String, Object> assessmentmainSubMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (assessmentmainSubMap.get("pass") != null) {
                                if (isCplModule) {
                                    setAssessmentImageStatus((boolean) assessmentmainSubMap.get("pass"));
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(assessmentListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAssessmentResultData() {
        if(OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentResultScore()){
            findViewById(R.id.assessment_score_ll).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.assessment_score_ll).setVisibility(View.GONE);
        }

        assessment_name.setText(OustAppState.getInstance().getAssessmentFirebaseClass().getName());
        int userCorrectAnswerCount = 0;
        int userWrongAnswerCount = 0;
        int userTotalScore = 0;
        if (submitRequest != null && submitRequest.getScores() != null) {
            for (int i = 0; i < submitRequest.getScores().size(); i++) {
                if (submitRequest.getScores().get(i).getQuestion() != 0) {
//                    userTotalScore += submitRequest.getScores().get(i).getScore();
                    if (!submitRequest.getScores().get(i).isCorrect()) {
                        userWrongAnswerCount++;
                    } else {
                        userCorrectAnswerCount++;
                    }
                }
            }
        }
        userTotalScore = userCorrectAnswerCount + userWrongAnswerCount;

        score.setText("" + userCorrectAnswerCount);
        total_score.setText("" + userTotalScore);
        int passingMark = (int) OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage();
        pass_mark.setText("" + passingMark + "%");
        pass_total_score.setText("100");
        pass_total_score.setVisibility(View.GONE);

        int actualPassing = ((userCorrectAnswerCount * 100) / userTotalScore);
        boolean passed = false;
        assessmentPass = false;
        if (actualPassing >= passingMark)
        {
            assessmentPass = true;
            passed = true;
            if(isFromCourse){
                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
            }
        }
        if(courseAssociated)
        {
            getUserCourses(mappedCourseId);
            // okbtn.setClickable(false);
        }

        try {
            if (passed) {
                // deleteAssessmentMedia();
                //deleteAllQuestion();
                OustSdkTools.setImage(assessment_status_img, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                //assessment_status_img.setColorFilter(ContextCompat.getColor(this, R.color.lgreen), android.graphics.PorterDuff.Mode.SRC_IN);
                assessment_status_msg.setText(getResources().getString(R.string.congratulations_text));
                assessment_success_msg.setText(getResources().getString(R.string.cleared_module));
            } else {
                OustSdkTools.setImage(assessment_status_img, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                //assessment_status_img.setColorFilter(ContextCompat.getColor(this, R.color.Orange), android.graphics.PorterDuff.Mode.SRC_IN);
                assessment_status_msg.setText("");
                assessment_success_msg.setText(getResources().getString(R.string.assess_fail_msg));

                if(OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass()==0){
                    attempt_count_ll.setVisibility(View.GONE);
                    view_bar2.setVisibility(View.GONE);
                }else {
                    attempt_count_ll.setVisibility(View.VISIBLE);
                    view_bar2.setVisibility(View.VISIBLE);
                    int attemptCount = ((int) OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass() - 1) - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
                    //int attemptCount = 2 - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
                    attempt_mark.setText("0" + attemptCount);
                }
            }
            ArrayList<AssesmentResultBadge> assesmentResultBadges=OustAppState.getInstance().getAssessmentFirebaseClass().getAssesmentResultBadges();
            if(OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentRemark()){
                attempt_count_ll.setVisibility(View.GONE);
                view_bar2.setVisibility(View.GONE);
                if(assessment_popup_answers.getVisibility()==View.VISIBLE || assessment_popup_lb.getVisibility()==View.VISIBLE){
                    findViewById(R.id.okbtn_txt).setVisibility(View.VISIBLE);
                }
                if(passingMark==0){
                    assessment_status_msg.setText(getResources().getString(R.string.thank_you_for_participation));
                    findViewById(R.id.pass_mark_ll).setVisibility(View.GONE);
                    findViewById(R.id.view_bar1).setVisibility(View.GONE);
                }
                if(assesmentResultBadges!=null && assesmentResultBadges.size()>0){
                    String badge="";
                    for(int j=0;j<assesmentResultBadges.size();j++){
                        if(actualPassing>=assesmentResultBadges.get(j).getFrom() && actualPassing<=assesmentResultBadges.get(j).getTo()){
                            badge = assesmentResultBadges.get(j).getLabel();
                            setBadge(assesmentResultBadges.get(j).getImage());
                        }
                    }
                    if(badge.length()>0){
                        badge=getResources().getString(R.string.rating)+" : "+badge;
                        assessment_success_msg.setText(badge);
                    }else{
                        assessment_success_msg.setVisibility(View.GONE);
                    }
                }else{
                    assessment_success_msg.setVisibility(View.GONE);
                }

            }


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
        catalog_distribution_url = catalog_distribution_url.replace("{courseId}", mappedCourseId+"");
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
                okbtn.setVisibility(View.GONE);
                mRetryLayout.setVisibility(View.VISIBLE);
                ShowPopup.getInstance().dismissProgressDialog();
                OustSdkTools.showToast(getResources().getString(R.string.error_message));
            }
        });


    }

    private void distributeCourse2(long mappedCourseId) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
        JsonObjectRequest jsonObjReq = null;
        String catalog_distribution_url = "";

        catalog_distribution_url = getResources().getString(R.string.distribut_course_url);
        catalog_distribution_url = catalog_distribution_url.replace("{courseId}", mappedCourseId+"");
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
                        okbtn.setVisibility(View.VISIBLE);
                        mRetryLayout.setVisibility(View.GONE);
                        okbtn.setClickable(true);
                    }
                } catch (Exception e) {
                    okbtn.setVisibility(View.GONE);
                    mRetryLayout.setVisibility(View.VISIBLE);
                    ShowPopup.getInstance().dismissProgressDialog();
                    OustSdkTools.showToast(getResources().getString(R.string.error_message));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //  ll_progress.setVisibility(View.GONE);
                okbtn.setVisibility(View.GONE);
                mRetryLayout.setVisibility(View.VISIBLE);
                ShowPopup.getInstance().dismissProgressDialog();
                OustSdkTools.showToast(getResources().getString(R.string.error_message));
            }
        });


    }

    private LinearLayout mRetryLayout;
    public void getUserCourses(final long mappedCourseId) {
        mRetryLayout = findViewById(R.id.btnRetry);
        mRetryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distributeCourse(mappedCourseId);
            }
        });
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course"+mappedCourseId;
            ValueEventListener myassessmentListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue()==null)
                        {
                            isMappedCourseDistributed = false;
                            distributeCourse(mappedCourseId);
                            // okbtn.setEnabled(true);
                        }
                        else{
                            Log.d(TAG, "onDataChange: course distributed:");
                            okbtn.setClickable(true);
                            isMappedCourseDistributed = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "onCancelled: "+error.getMessage());
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

    public void getUserCourses2(final long mappedCourseId) {
        mRetryLayout = findViewById(R.id.btnRetry);
        mRetryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distributeCourse(mappedCourseId);
            }
        });
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/course"+mappedCourseId;
            ValueEventListener myassessmentListener = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue()==null)
                        {
                            isMappedCourseDistributed = false;
                            distributeCourse(mappedCourseId);
                            // okbtn.setEnabled(true);
                        }
                        else{
                            Log.d(TAG, "onDataChange: course distributed:");
                            okbtn.setClickable(true);
                            isMappedCourseDistributed = true;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "onCancelled: "+error.getMessage());
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

    private void setBadge(String image_str) {
        if(image_str!=null && !image_str.isEmpty())
            Picasso.get().load(image_str).into(assessment_status_img);
    }

    boolean isAssessmentPassed = false;

    private void getAssessmentStatus() {
        try {
            String message = "/landingPage/" + activeUser.getStudentKey() + "/assessment/" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                if ((courseId != null) && (!courseId.isEmpty())) {
                    return;
                } else {
                    message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn/" + courseColnId + "/mappedAssessment";
                }
            } else {
                if ((courseId != null) && (!courseId.isEmpty())) {
                    message = "/landingPage/" + activeUser.getStudentKey() + "/course/" + courseId + "/mappedAssessment";
                }
            }
            Log.e("-----result", message);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            Log.e("-----result", dataSnapshot.toString());
                            final Map<String, Object> assessmentmainSubMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (assessmentmainSubMap.get("passed") != null) {
                                if (isCplModule) {
                                    setAssessmentImageStatus((boolean) assessmentmainSubMap.get("passed"));
                                }
                                if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate()!=null){
                                    if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule()!=null){
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setState("COMPLETED");
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed((boolean) assessmentmainSubMap.get("passed"));
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                    }

                                    if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData()!=null){
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                    }
                                }
                                if (((boolean) assessmentmainSubMap.get("passed"))) {
                                    isAssessmentPassed = true;
                                    saveDataLocallyforCourse();
                                    if (OustAppState.getInstance().getAssessmentFirebaseClass().isCertificate()) {
                                        sendCertificatePopup();
                                    }
                                }
                            }else{
                                if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate()!=null){
                                    if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule()!=null){
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setState("COMPLETED");
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed(false);
                                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                    }
                                }

                                if(OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData()!=null){
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                                    OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(assessmentListener);
        } catch (Exception e) {
            Log.e("result", "Exception occured in getAssessmentStatus()", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setAssessmentImageStatus(boolean passed) {
        try {
            if (passed) {
                OustSdkTools.setImage(assessment_status_img, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                //assessment_status_img.setColorFilter(ContextCompat.getColor(this, R.color.lgreen), android.graphics.PorterDuff.Mode.SRC_IN);
                assessment_status_msg.setText(getResources().getString(R.string.congratulations_text));
                assessment_success_msg.setText(getResources().getString(R.string.cleared_module));
            } else {
                OustSdkTools.setImage(assessment_status_img, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                //assessment_status_img.setColorFilter(ContextCompat.getColor(this, R.color.Orange), android.graphics.PorterDuff.Mode.SRC_IN);
                assessment_status_msg.setText("");
                assessment_success_msg.setText(getResources().getString(R.string.assess_fail_msg));
                //attempt_count_ll.setVisibility(View.VISIBLE);
                //view_bar2.setVisibility(View.VISIBLE);

                if(OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass()==0){
                    attempt_count_ll.setVisibility(View.GONE);
                    view_bar2.setVisibility(View.GONE);

                }else {
                    attempt_count_ll.setVisibility(View.VISIBLE);
                    view_bar2.setVisibility(View.VISIBLE);
                    int attemptCount = ((int) OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass() - 1) - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
                    //int attemptCount = 2 - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
                    attempt_mark.setText("0" + attemptCount);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onDestroy() {
        notifyOthers();
        deleteAllQuestion();
        OustAppState.getInstance().setPlayResponse(null);
        OustAppState.getInstance().setAssessmentResultActivityEndInterface(null);
        presenter = null;
        super.onDestroy();
    }

    public void deleteAllQuestion() {
        try {
            if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    if (playResponse.getqIdList().get(i) > 0) {
                        RoomHelper.deleteQuestion(playResponse.getqIdList().get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("result", "Exception occured in deleteAllQuestion()", e);
        }
    }

    public void setTextTypeFace() {
        try {
            txtShare.setText(getResources().getString(R.string.share_text));
            rightCenter.setText(getResources().getString(R.string.right));
            wrongCenter.setText(getResources().getString(R.string.wrong));
            xpValueCenter.setText(getResources().getString(R.string.points_text));
//            userright_presentagelable.setText(getResources().getString(R.string.rightprecentage_label"));
            if (!OustPreferences.getAppInstallVariable("hideAllAssessmentLeaderBoard") && !OustAppState.getInstance().getAssessmentFirebaseClass().isHideLeaderboard()) {
                assessmentleaderboardbtn.setVisibility(View.VISIBLE);
                assessment_popup_lb.setVisibility(View.VISIBLE);
            }else{
                if(!shouldShowAnswers){
                    assessmentleaderboardbtn.setVisibility(View.GONE);
                }
            }
            if(shouldShowAnswers){
                assessment_popup_answers.setVisibility(View.VISIBLE);
            }
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() != null) &&
                    (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC)) {
                assessmentleaderboardbtn.setVisibility(View.GONE);
            } else {
                if (shouldShowAnswers) {
                    leaderboard_text.setText(getResources().getString(R.string.answer));
//                    leaderboardImage.setImageDrawable(getResources().getDrawable(R.drawable.answer));
                    OustSdkTools.setImage(leaderboardImage, getResources().getString(R.string.assessment_answer_icon));
                } else {
                    leaderboard_text.setText(getResources().getString(R.string.leader_board_title));
                    OustSdkTools.setImage(leaderboardImage, getResources().getString(R.string.leaderboard_new_icon));
//                    leaderboardImage.setImageDrawable(getResources().getDrawable(R.drawable.leaderboards));
                }
            }
            assessment_totlepeople.setText("" + OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount());
            participants_text.setText(getResources().getString(R.string.participants));
            assessment_totalques.setText("" + OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions());
            total_ques_text.setText(getResources().getString(R.string.total_question));

            timetaken_title.setText(getResources().getString(R.string.time_taken));
            int hrs = (int) (totaltimetaken / 3600);
            int minutes = (int) ((totaltimetaken % 3600) / 60);
            int seconds = (int) (totaltimetaken % 60);
            if (hrs > 0) {
//                timetaken_text.setText(""+hrs+":"+minutes+":"+seconds);
                timetaken_text.setText(String.format("%02d:%02d:%02d", hrs, minutes, seconds) + " hrs");
            } else if (minutes > 0) {
//                timetaken_text.setText(""+minutes+":"+seconds+" mins");
                timetaken_text.setText(String.format("%02d:%02d", minutes, seconds) + " mins");
            } else {
//                timetaken_text.setText(""+seconds+" sec");
                timetaken_text.setText(String.format("%02d", seconds) + " sec");
            }

            if (OustAppState.getInstance().getAssessmentFirebaseClass().getName() != null) {
                assessmentname_txt.setText(OustAppState.getInstance().getAssessmentFirebaseClass().getName());
            }
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getResultMessage() != null) &&
                    (!OustAppState.getInstance().getAssessmentFirebaseClass().getResultMessage().isEmpty())) {
//                assessmentresultMessage_text.setVisibility(View.VISIBLE);
//                assessmentresultMessage_text.setText(OustAppState.getInstance().getAssessmentFirebaseClass().getResultMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("result", "Exception occured in setTextTypeFace()", e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void verifyResult() {
        try {
            mediaPlayer = new MediaPlayer();
            playAudio("game_comeback_resultcard.mp3");

//            mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("game_comeback_resultcard")));
//            playMusic();
//            OustSdkTools.setImage(assessmentresult_resultSymbol, getResources().getString(R.string.come_back));
            assessmentresult_resultTitle.setText("" + getResources().getString(R.string.well_done_text));

            String assessmentOverMsg = "" + OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentOverMessage();
            if (assessmentOverMsg != null && !assessmentOverMsg.isEmpty() && !assessmentOverMsg.equalsIgnoreCase("null")) {
                assessmentresult_resultText.setText(assessmentOverMsg);
            } else
                assessmentresult_resultText.setText("" + getResources().getString(R.string.assessment_successfully_completed));

            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC)) {
                assessmentresult_scoreLayout1.setVisibility(View.GONE);
            } else {
                if (OustAppState.getInstance().getAssessmentFirebaseClass().isShowPercentage()) {
                    showAssessmentPresentage();
                } else {
                    setUserScores(userright_ans, userwrong_ans, userxpvalue);
                }
            }
        } catch (Exception e) {
            Log.e("result", "Exception occured in verifyResult()", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void playAudio(final String filename) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    File tempMp3 = new File(getFilesDir(), filename);
                    mediaPlayer.reset();
                    FileInputStream fis = new FileInputStream(tempMp3);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    Log.e("result", "Exception occured in playAudio()", e);
                }
            }
        });

    }

    private void playMusic() {
        mediaPlayer.setLooping(false);
        try {
            if (!OustAppState.getInstance().isSoundDisabled()) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void showAssessmentPresentage() {
        try {
//            scoreWaitingLayout.setVisibility(View.GONE);
//            showpresentageLayout.setVisibility(View.VISIBLE);
            int totalCount = 0;
            int userCorrectAnswerCount = 0;
            float presentage = 0;
            for (int i = 0; i < submitRequest.getScores().size(); i++) {
                if (submitRequest.getScores().get(i).getQuestion() != 0) {
                    if (submitRequest.getScores().get(i).isCorrect()) {
                        userCorrectAnswerCount++;
                    }
                    totalCount++;
                }
            }
            if (totalCount > 0) {
                float f1 = (float) userCorrectAnswerCount;
                float f2 = (float) totalCount;
                presentage = ((f1 / f2) * 100);
            }
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
//            userright_presentage.setText(df.format(presentage) + "%");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("result", "Exception occured in showAssessmentPresentage()", e);
        }
    }


    private void setUserScores(TextView rightUserScore, TextView wrongUserScore, TextView xpValueUserScore) {
        try {
//            scoreWaitingLayout.setVisibility(View.VISIBLE);
//            showpresentageLayout.setVisibility(View.GONE);
            int userCorrectAnswerCount = 0;
            int userWrongAnswerCount = 0;
            int userTotalScore = 0;
            for (int i = 0; i < submitRequest.getScores().size(); i++) {
                if (submitRequest.getScores().get(i).getQuestion() != 0) {
//                    userTotalScore += submitRequest.getScores().get(i).getScore();
                    if (!submitRequest.getScores().get(i).isCorrect()) {
                        userWrongAnswerCount++;
                    } else {
                        userCorrectAnswerCount++;
                    }
                }
            }
            userTotalScore = (int) submitRequest.getTotalscore();
            rightUserScore.setText("" + userCorrectAnswerCount);
            wrongUserScore.setText("" + userWrongAnswerCount);
            xpValueUserScore.setText("" + userTotalScore);

            long minPass = OustAppState.getInstance().getAssessmentFirebaseClass().getMinPassCorrectAnswer();
            result_msg.setVisibility(View.GONE);

//            score_text.setText(""+userCorrectAnswerCount);
//            total_score.setText(""+userTotalScore);

            pass_mark.setText("" + OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage()+"%");
            pass_total_score.setText("100");


//            if(minPass>0) {
//                if (userCorrectAnswerCount >= minPass) {
//                    result_msg.setText(getResources().getString(R.string.assess_pass_msg));
//                } else {
//                    result_msg.setText(getResources().getString(R.string.assess_fail_msg));
//                }
//            } else {
//                result_msg.setVisibility(View.GONE);
//            }

        } catch (Exception e) {
            Log.e("result", "exception in setting score_text on result page", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //=========================================================================================
    //==============================================================================================
//show stored popup during game play
    private void showstored_Popup() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Stack<Popup> popupStack = OustAppState.getInstance().getPopupStack();
                    if (popupStack != null) {
                        try {
                            while (!popupStack.isEmpty()) {
                                OustStaticVariableHandling.getInstance().setOustpopup(popupStack.pop());
                                Intent intent = new Intent(AssessmentResultActivity.this, PopupActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(),
//                                    new Throwable().fillInStackTrace()
//                                            .getStackTrace()[1].getMethodName(), e);
                        }
                    }
                } catch (Exception e) {
                    Log.e("result", "exception in setting showstored_Popup() result page", e);
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        }, 2000);
    }

    //=========================================================================================
    @Override
    public void onClick(View v) {
        try {
            OustSdkTools.oustTouchEffect(v, 100);
            if (v.getId() == R.id.assessmentclose_btn) {
                onBackPressed();
                //AssessmentResultActivity.this.finish();
            } else if (v.getId() == R.id.closebtn_layout) {
                if(isMappedCourseDistributed && courseAssociated && mappedCourseId!=0)
                {
                    startCourse();
                }
                if(isFromCourse){
                    if( isAssessmentPassed){
                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                    }else{
                        if(reAttemptAllowed&&(nAttemptCount>=nAttemptAllowedToPass)){
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                        }else{
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                        }

                    }
                }else{
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                }

                AssessmentResultActivity.this.finish();
            } else if (v.getId() == R.id.assessmentleaderboardbtn) {
                if (shouldShowAnswers) {
                    launchAnswers();
                } else {
                    launchLeaderBoard();
                }
            } else if (v.getId() == R.id.assessmentshare_btn) {
                shareClicked();
            }else if(v.getId()==R.id.assessment_popup_lb){
                launchLeaderBoard();
            }else if(v.getId()==R.id.assessment_popup_answers){
                launchAnswers();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void notifyOthers() {
        if(OustLauncher.getInstance().getOustModuleCallBack()!=null){
            String assessmentId= OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId()+"";
            OustLauncher.getInstance().getOustModuleCallBack().onModuleComplete(new Moduledata(assessmentId,"Assessment","Assessment"));
        }
    }

    private boolean launchLeaderBoard() {
        if (!OustSdkTools.checkInternetStatus()) {
            return true;
        }
        Intent intent1 = new Intent(AssessmentResultActivity.this, EventLeaderboardActivity.class);
        intent1.putExtra("isassessmentleaderboard", "true");
        startActivity(intent1);
        return false;
    }

    private void launchAnswers() {
        Intent intent = new Intent(this, AssessmentQuestionReviewBaseActivity.class);
        intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
        OustAppState.getInstance().setPlayResponse(this.playResponse);
        intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
        this.startActivity(intent);
    }

    private void shareClicked() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AssessmentResultActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        } else {
            OustShareTools.shareScreenAndBranchIo(AssessmentResultActivity.this, assessmentresult_mainLayout, getResources().getString(R.string.oust_share_text), OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId() + "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 123) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        shareClicked();
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
        super.onBackPressed();

        if(!isAssessmentPassed && OustStaticVariableHandling.getInstance().getCplCloseListener()!=null){
            //int attemptCount = ((int) OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass() - 1) - CplDataHandler.getInstance().getCplAssessmentAttemptCount();
            OustStaticVariableHandling.getInstance().getCplCloseListener().onAssessmentFail((CplDataHandler.getInstance().getCplAssessmentAttemptCount()+1), (OustAppState.getInstance().getAssessmentFirebaseClass().getNoOfAttemptAllowedToPass()) <= (CplDataHandler.getInstance().getCplAssessmentAttemptCount() + 1));
        }

        try {
            if ((courseId != null) && (!courseId.isEmpty())) {
                if (isAssessmentPassed) {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(false, false);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try{
            if(isFromCourse){
                if( isAssessmentPassed){
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                }else{
                    if(reAttemptAllowed&&(nAttemptCount>=nAttemptAllowedToPass)){
                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                    }else{
                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    }

                }
            }else{
                OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        startCourse();
    }

    @Override
    public void endActvity(boolean resetProgress) {
        if (presenter != null) {
            if (resetProgress) {
                resetCourseProgress();
            } else {
                saveDataLocallyforCourse();
            }
        }
    }


    public void endActivity() {
        if(isFromCourse){
            if( isAssessmentPassed){
                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
            }else{
                if(reAttemptAllowed&&(nAttemptCount>=nAttemptAllowedToPass)){
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                }else{
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                }

            }
        }else{
            OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
        }

        AssessmentResultActivity.this.finish();
    }

    public void resetCourseProgress() {
        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
        userCourseScoreDatabaseHandler.setCurrentCompleteLevel(1, dtoUserCourseData);
        userCourseScoreDatabaseHandler.setLastCompleteLevel(1, dtoUserCourseData);
        if (OustAppState.getInstance().getLearningCallBackInterface() != null) {
            OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(false, false);
        }
    }


    public void saveDataLocallyforCourse() {
        try {
            if ((courseId != null) && (!courseId.isEmpty())) {
                DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
                dtoUserCourseData.setCurrentCompleteLevel((dtoUserCourseData.getCurrentCompleteLevel() + 1));
                dtoUserCourseData.setMappedAssessmentPassed(true);
                RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
            }
        } catch (Exception e) {
        }
    }

    private RelativeLayout certificateloader_layout;
    private ProgressBar certificate_loader;

    public void showLoader() {
        try {
            certificateloader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(AssessmentResultActivity.this, R.anim.rotate_anim);
            certificate_loader.startAnimation(rotateAnim);
            certificateloader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideLoader() {
        try {
            certificateloader_layout.setVisibility(View.GONE);
            certificate_loader.setAnimation(null);
        } catch (Exception e) {
        }
    }

    private PopupWindow certificateemail_popup;

    public void sendCertificatePopup() {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    View popUpView = getLayoutInflater().inflate(R.layout.certificateemail_popup, null);
                    certificateemail_popup = OustSdkTools.createPopUp(popUpView);

                    final Button btnOK = popUpView.findViewById(R.id.otp_okbtn);
                    final ImageButton certificatepopup_btnClose = popUpView.findViewById(R.id.certificatepopup_btnClose);
                    final EditText edittext_email = popUpView.findViewById(R.id.edittext_email);
                    final TextView certifucate_titletxt = popUpView.findViewById(R.id.certifucate_titletxt);
                    final TextView certificate_txt = popUpView.findViewById(R.id.certificatemsg);
                    certifucate_titletxt.setText(getResources().getString(R.string.your_certificate));
                    certificate_txt.setText(getResources().getString(R.string.enter_email_receive_certificate));
                    certificateloader_layout = popUpView.findViewById(R.id.certificateloader_layout);
                    certificate_loader = popUpView.findViewById(R.id.certificate_loader);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    edittext_email.requestFocus();
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OustSdkTools.oustTouchEffect(view, 200);
                            hideKeyboard(edittext_email);
                            if (isValidEmail(edittext_email.getText().toString().trim())) {
                                showLoader();
                                sendCertificatetomail(edittext_email.getText().toString().trim());
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.enter_valid_mail));
                            }
                        }
                    });

                    certificatepopup_btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hideKeyboard(edittext_email);
                            certificateemail_popup.dismiss();
                        }
                    });
                    LinearLayout certificateanim_layout = popUpView.findViewById(R.id.certificateanim_layout);
                    OustSdkTools.popupAppearEffect(certificateanim_layout);
                }
            }, 500);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void showCertificateLoader() {
        try {
            certificateloader_layout.setVisibility(View.VISIBLE);
            Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
            certificate_loader.startAnimation(rotateAnim);
            certificateloader_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideCertificateLoader() {
        try {
            certificateloader_layout.setVisibility(View.GONE);
            certificate_loader.setAnimation(null);
        } catch (Exception e) {
        }
    }

    public void sendCertificatetomail(String mail) {
        try {
            SendCertificateRequest sendCertificateRequest = new SendCertificateRequest();
            sendCertificateRequest.setStudentid(activeUser.getStudentid());
            sendCertificateRequest.setEmailid(mail);
            if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                sendCertificateRequest.setContentId(("" + courseColnId));
                sendCertificateRequest.setContentType("COURSE_COLN");
            } else if ((courseId != null) && (!courseId.isEmpty())) {
                sendCertificateRequest.setContentId(("" + courseId));
                sendCertificateRequest.setContentType("COURSE");
            } else {
                sendCertificateRequest.setContentId(("" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId()));
                sendCertificateRequest.setContentType("ASSESSMENT");
            }
            if (OustSdkTools.checkInternetStatus()) {
                hitCertificateRequestUrl(sendCertificateRequest);
            }

        } catch (Exception e) {
        }
    }

    private void hitCertificateRequestUrl(SendCertificateRequest sendCertificateRequest) {
        final CommonResponse[] commonResponses = {null};

        JSONObject jsonObject = new JSONObject();
        try {
            showCertificateLoader();
            jsonObject.put("studentid", sendCertificateRequest.getStudentid());
            jsonObject.put("emailid", sendCertificateRequest.getEmailid());
            jsonObject.put("contentType", sendCertificateRequest.getContentType());
            jsonObject.put("contentId", sendCertificateRequest.getContentId());

            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(jsonObject);
            String sendcertificate_url = OustSdkApplication.getContext().getResources().getString(R.string.sendcertificate_url);
            sendcertificate_url = HttpManager.getAbsoluteUrl(sendcertificate_url);

            ApiCallUtils.doNetworkCall(Request.Method.POST, sendcertificate_url, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    gotCertificatetomailResponce(commonResponses[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideCertificateLoader();
                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, sendcertificate_url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideCertificateLoader();
                    commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                    gotCertificatetomailResponce(commonResponses[0]);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideCertificateLoader();
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
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/

        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotCertificatetomailResponce(CommonResponse commonResponse) {
        try {
            hideLoader();
            if ((commonResponse != null)) {
                if (commonResponse.isSuccess()) {
                    if ((certificateemail_popup != null) && (certificateemail_popup.isShowing())) {
                        certificateemail_popup.dismiss();
                    }
                    OustSdkTools.showToast(getResources().getString(R.string.certificate_sent_mail));
                } else {
                    if (commonResponse.getError() != null) {
                        OustSdkTools.showToast(commonResponse.getError());
                    }
                }
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.retry_internet_msg));
            }
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

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // startCourse();
    }

    private void startCourse() {
        if(courseAssociated && mappedCourseId!=0)
        {
            Intent intent = new Intent(OustSdkApplication.getContext(), NewLearningMapActivity.class);
            intent.putExtra("learningId", mappedCourseId+"");
            intent.putExtra("clickOnStart", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
            finish();
        }
    }
}
