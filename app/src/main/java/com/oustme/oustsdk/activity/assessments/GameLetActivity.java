package com.oustme.oustsdk.activity.assessments;

import static com.oustme.oustsdk.service.DownLoadFilesIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadFilesIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadFilesIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_HTTPS;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.AssessmentCopyResponse;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.fragments.courses.JumbleWordFragment;
import com.oustme.oustsdk.fragments.courses.JumbleWordFragment2;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.PreviousWinnerDetailsResponse;
import com.oustme.oustsdk.response.assessment.QuestionResponce;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.tools.TrackSelectionHelper;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.time.TimeTCPClient;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by admin on 07/12/17.
 */

public class GameLetActivity extends AppCompatActivity implements LearningModuleInterface, View.OnClickListener {
    private static final String TAG = "GameLetActivity";
    private RelativeLayout card_layout, gamelet_mainloader, downloadscreen_layout, score_board_layout,
            instruction_layout, starttransaction_background, strattransaction_layout, progressbar_layout,
            winner_layout, watch_demovideo_layout, demo_video_layout, timer_layout,
            close_video_layout, nointernet_popup;

    private ProgressBar gamelet_progressbar, mygame_loader, demovideo_loader;
    private TextView gameletdownloadtext, nointernet_cancel, timetaken_textview, submittedtime_textview, timer_text;
    private HtmlTextView instruction_text, winner_question;
    private TransferUtility transferUtility;
    private ImageView wordjumble_image, winner_profile_pic, demo_video, loaderback_image,
            close_video, instruction_imageview, winner_layoutimageview, nointernet_popupimage,
            nointernet_cancel_bgd, starttransaction_bgd, score_board_layout_bgd;


    private TextView mygame_scoretext, mygame_timetext, mygame_okbtn, winner_name, winner_solution,
            nointernet_retry, submit_response_text;
    private Button show_quality;
    private RelativeLayout watch_demo_layout;
    private ImageView nointernet_retry_bgd, mygame_okbtn_bgd;

    private String videoFileName = "";
    private String instruction = "";
    private String wjSubmitMsg = "";
    private boolean isWordJumble2 = false;
    private boolean isWordJumble3 = false;
    private long startTime = 0;
    private TrackSelectionHelper trackSelectionHelper;
    private DownloadFiles downLoadFiles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gamelet);
        //changeOrintiationPortrait();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent callingIntent = getIntent();
        String assessmentIdStr = callingIntent.getStringExtra("assessmentId");
        String questionType = callingIntent.getStringExtra("feedType");
        if (questionType.equalsIgnoreCase("GAMELET_WORDJUMBLE_V2")) {
            isWordJumble2 = true;
        } else if (questionType.equalsIgnoreCase("GAMELET_WORDJUMBLE_V3")) {
            isWordJumble2 = true;
            isWordJumble3 = true;
        }
        if ((assessmentIdStr != null) && (!assessmentIdStr.isEmpty())) {
            assessmentId = Long.parseLong(assessmentIdStr);
        }
        initViews();
        activeUser = OustAppState.getInstance().getActiveUser();
        if (OustSdkTools.checkInternetStatus()) {
            getGameState();
            getGameletDetailsfromFirebase();
            gotAssessmentFromFirebase();
//            OustGATools.getInstance().reportPageViewToGoogle(GameLetActivity.this, "GameLet Landing Page");
        } else {
            showCustomPopup(getResources().getString(R.string.retry_internet_msg));
            //GameLetActivity.this.finish();
        }
    }

    private void getPreviousWinnerDetails() {
        String previousWinnerUrl = OustSdkApplication.getContext().getResources().getString(R.string.previous_winner);
        previousWinnerUrl = previousWinnerUrl.replace("{assessmentId}", ("" + assessmentId));
        previousWinnerUrl = HttpManager.getAbsoluteUrl(previousWinnerUrl);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(previousWinnerUrl);

        ApiCallUtils.doNetworkCall(Request.Method.GET, previousWinnerUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                PreviousWinnerDetailsResponse previousWinnerDetailsResponse = gson.fromJson(response.toString(), PreviousWinnerDetailsResponse.class);
                gotPreviousWinnerResponse(previousWinnerDetailsResponse);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, previousWinnerUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                PreviousWinnerDetailsResponse previousWinnerDetailsResponse = gson.fromJson(response.toString(), PreviousWinnerDetailsResponse.class);
                gotPreviousWinnerResponse(previousWinnerDetailsResponse);
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
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
        OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
    }

    private void gotPreviousWinnerResponse(PreviousWinnerDetailsResponse previousWinnerDetailsResponse) {
        if (isWordJumble2) {
            if (previousWinnerDetailsResponse != null && previousWinnerDetailsResponse.getWinnerName() != null) {
                winner_layout.setVisibility(View.VISIBLE);
                winner_name.setText(previousWinnerDetailsResponse.getWinnerName());
                if ((previousWinnerDetailsResponse.getSubmitDateTime() != null) && (!previousWinnerDetailsResponse.getSubmitDateTime().isEmpty())) {
                    submittedtime_textview.setText(getResources().getString(R.string.submitted_time)+" : " + previousWinnerDetailsResponse.getSubmitDateTime());
                    submittedtime_textview.setVisibility(View.VISIBLE);
                }
                if ((previousWinnerDetailsResponse.getTimeTaken() > 10)) {
                    long timeInSec = previousWinnerDetailsResponse.getTimeTaken() / 1000;
                    if (timeInSec > 60) {
                        String timeStr = String.format("%02d:%02d", timeInSec / 60, timeInSec % 60);
                        timetaken_textview.setText(getResources().getString(R.string.time_taken)+" : " + (timeStr) + " min");
                    } else {
                        timetaken_textview.setText(getResources().getString(R.string.time_taken)+" : " + (timeInSec) + "sec");
                    }
                    timetaken_textview.setVisibility(View.VISIBLE);
                }
                if ((previousWinnerDetailsResponse.getQuestion() != null) && (!previousWinnerDetailsResponse.getQuestion().isEmpty())) {
                    winner_question.setHtml(previousWinnerDetailsResponse.getQuestion());
                }
                if ((previousWinnerDetailsResponse.getSolution() != null) && (!previousWinnerDetailsResponse.getSolution().isEmpty())) {
                    winner_solution.setText(previousWinnerDetailsResponse.getSolution());
                }
                setThumbnailImage(previousWinnerDetailsResponse.getWinnerAvatar(), winner_profile_pic);
            }
        }
    }

    private String currentState = "";
    private long user_timetaken = 0;
    private long user_submittime = 0;
    private String user_question = "";
    private String user_answer = "";
    private boolean gotAssessmentStatus = false;
    private boolean gotReattemptStatus = false;

    private void getGameState() {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gotAssessmentStatus = true;
                    try {
                        if (dataSnapshot != null) {
                            final Map<String, Object> assessmentprogressMainMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (assessmentprogressMainMap != null) {
                                currentState = AssessmentState.STARTED;
                                try {
                                    if (assessmentprogressMainMap != null) {
                                        if (assessmentprogressMainMap.get("assessmentState") != null) {
                                            currentState = ((String) assessmentprogressMainMap.get("assessmentState"));
                                        }
                                        if (assessmentprogressMainMap.get("userResponseTime") != null) {
                                            user_timetaken = ((long) assessmentprogressMainMap.get("userResponseTime"));
                                        }
                                        if (assessmentprogressMainMap.get("userSubmitTime") != null) {
                                            user_submittime = ((long) assessmentprogressMainMap.get("userSubmitTime"));
                                        }
                                        if (assessmentprogressMainMap.get("userQuestion") != null) {
                                            user_question = ((String) assessmentprogressMainMap.get("userQuestion"));
                                        }
                                        if (assessmentprogressMainMap.get("userAnswer") != null) {
                                            user_answer = ((String) assessmentprogressMainMap.get("userAnswer"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                    gotReattemptResponse();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    gotAssessmentStatus = true;
                    gotReattemptResponse();
                }
            };
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isReattemptAllowed = false;

    private void getGameletDetailsfromFirebase() {
        try {
            final String message = "/assessment/assessment" + assessmentId;
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    gotReattemptStatus = true;
                    try {
                        final Map<String, Object> assessmentMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (null != assessmentMap) {
                            if (assessmentMap.get("introVideo") != null) {
                                videoFileName = ((String) assessmentMap.get("introVideo"));
                            }
                            if (assessmentMap.get("instruction") != null) {
                                instruction = ((String) assessmentMap.get("instruction"));
                            }
                            if (assessmentMap.get("reattemptAllowed") != null) {
                                isReattemptAllowed = ((boolean) assessmentMap.get("reattemptAllowed"));
                            }
                            if (assessmentMap.get("wjSubmitMsg") != null) {
                                wjSubmitMsg = ((String) assessmentMap.get("wjSubmitMsg"));
                            }
                            if (assessmentMap.get("startDate") != null) {
                                startTime = Long.parseLong((String) assessmentMap.get("startDate"));
                            }
                        }
                    } catch (Exception e) {
                    }
                    gotAssessmentFromFirebase();
                    gotReattemptResponse();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    gotReattemptStatus = true;
                    gotReattemptResponse();
                }
            };
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
        } catch (Exception e) {
        }
    }

    private void gotReattemptResponse() {
        if ((gotAssessmentStatus) && (gotReattemptStatus)) {
            if ((!isReattemptAllowed) && ((currentState != null) && (currentState.equalsIgnoreCase(AssessmentState.SUBMITTED)))) {
                if (user_submittime > 0 && (user_timetaken > 0)) {
                    showCompletedPopup(getResources().getString(R.string.completed_word_jumble), false);
                } else {
                    showCustomPopup(getResources().getString(R.string.completed_word_jumble));
                }
            } else {
                startAssessment();
            }
        }
    }


    private void gotAssessmentFromFirebase() {
        try {
            if (isWordJumble2) {
                OustSdkTools.setImage(wordjumble_image, getResources().getString(R.string.gamelet_bg));
                if (!videoFileName.isEmpty()) {
                    watch_demo_layout.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(demo_video, getResources().getString(R.string.demo));
                }
                if ((instruction != null) && (!instruction.isEmpty())) {
                    instruction_layout.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(instruction_imageview, getResources().getString(R.string.board_wj));
                    instruction_text.setHtml(instruction);
                }
            } else {
                // OustSdkTools.setImage(wordjumble_image, getResources().getString(R.string.splash));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initViews() {
        card_layout = findViewById(R.id.card_layout);
        gameletdownloadtext = findViewById(R.id.gameletdownloadtext);
        progressbar_layout = findViewById(R.id.progressbar_layout);
        gamelet_mainloader = findViewById(R.id.gamelet_mainloader);
        gamelet_progressbar = findViewById(R.id.gamelet_progressbar);
        downloadscreen_layout = findViewById(R.id.downloadscreen_layout);
        loaderback_image = findViewById(R.id.loaderback_image);
        mygame_loader = findViewById(R.id.mygame_loader);
        mygame_okbtn = findViewById(R.id.mygame_okbtn);
        mygame_okbtn_bgd = findViewById(R.id.mygame_okbtn_bgd);
        mygame_timetext = findViewById(R.id.mygame_timetext);
        mygame_scoretext = findViewById(R.id.mygame_scoretext);
        wordjumble_image = findViewById(R.id.wordjumble_image);
        score_board_layout = findViewById(R.id.score_board_layout);
        score_board_layout_bgd = findViewById(R.id.score_board_layout_bgd);
        instruction_imageview = findViewById(R.id.instruction_imageview);
        submittedtime_textview = findViewById(R.id.submittedtime_textview);
        timetaken_textview = findViewById(R.id.timetaken_textview);

        submit_response_text = findViewById(R.id.submit_response_text);
        OustSdkTools.setImage(wordjumble_image, getResources().getString(R.string.splash));


        instruction_layout = findViewById(R.id.instruction_layout);
        watch_demovideo_layout = findViewById(R.id.watch_demovideo_layout);
        demo_video_layout = findViewById(R.id.demo_video_layout);
        demovideo_loader = findViewById(R.id.demovideo_loader);
        show_quality = findViewById(R.id.show_quality);
        watch_demo_layout = findViewById(R.id.watch_demo_layout);
        close_video_layout = findViewById(R.id.close_video_layout);
        demo_video = findViewById(R.id.demo_video);
        close_video = findViewById(R.id.close_video);
        nointernet_popupimage = findViewById(R.id.nointernet_popupimage);
        winner_question = findViewById(R.id.winner_question);
        winner_question.setTypeface(OustSdkTools.getTypefaceLithoPro());

        nointernet_popup = findViewById(R.id.nointernet_popup);
        nointernet_retry = findViewById(R.id.nointernet_retry);
        nointernet_retry_bgd = findViewById(R.id.nointernet_retry_bgd);
        nointernet_cancel = findViewById(R.id.nointernet_cancel);
        nointernet_cancel_bgd = findViewById(R.id.nointernet_cancel_bgd);
        OustSdkTools.setImage(close_video, getResources().getString(R.string.exit_icon));

        instruction_text = findViewById(R.id.instruction_text);
        instruction_text.setTypeface(OustSdkTools.getTypefaceLight());
        starttransaction_background = findViewById(R.id.starttransaction_background);
        starttransaction_bgd = findViewById(R.id.starttransaction_bgd);
        strattransaction_layout = findViewById(R.id.strattransaction_layout);
        timer_text = findViewById(R.id.timer_text);
        timer_layout = findViewById(R.id.timer_layout);
        winner_layout = findViewById(R.id.winner_layout);
        winner_layoutimageview = findViewById(R.id.winner_layoutimageview);
        winner_profile_pic = findViewById(R.id.winner_profile_pic);
        winner_name = findViewById(R.id.winner_name);
        winner_solution = findViewById(R.id.winner_solution);

        watch_demo_layout.setOnClickListener(this);
        close_video_layout.setOnClickListener(this);
        OustSdkTools.setImage(winner_layoutimageview, getResources().getString(R.string.video_bg));
        OustSdkTools.setImage(nointernet_popupimage, getResources().getString(R.string.instruction_bg));
        OustSdkTools.setImage(nointernet_retry_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(nointernet_cancel_bgd, getResources().getString(R.string.bg_word));
    }

    private ActiveGame activeGame;
    private ActiveUser activeUser;
    private long assessmentId;

    public void startAssessment() {
        try {
            activeGame = new ActiveGame();
            if (activeUser != null) {
                getInterNetTime();
            } else {
                GameLetActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startGame() {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest();
            createGameRequest.setAssessmentId(("" + assessmentId));
            createGameRequest.setChallengerid(activeUser.getStudentid());
            createGameRequest.setGuestUser(false);
            createGameRequest.setRematch(false);
            String laungeStr = Locale.getDefault().getLanguage();
            if ((laungeStr != null)) {
                createGameRequest.setAssessmentLanguage(laungeStr);
            }
            activeGame = new ActiveGame();
            createGame(createGameRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void createGame(CreateGameRequest createGameRequest) {
        try {
            String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.create_game);
            createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(createGameRequest);

            ApiCallUtils.doNetworkCall(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                    gotCreateGameRespoce(createGameResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // Log.e("volley error",error.getMessage());
                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                    gotCreateGameRespoce(createGameResponse);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Log.e("volley error",error.getMessage());
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotCreateGameRespoce(CreateGameResponse createGameResponse) {
        try {
            if (createGameResponse != null) {
                if (createGameResponse.isSuccess()) {
                    activeGame.setGameid("" + createGameResponse.getGameid());
                    getPlayresponce(activeGame.getGameid());
                } else {
                    OustSdkTools.handlePopup(createGameResponse);
                    showApiFailUI();
                }
            } else {
                showCustomPopup("Please check your network connection and try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showApiFailUI() {
        OustSdkTools.setImage(starttransaction_bgd, (getResources().getString(R.string.bg_word)));
        starttransaction_bgd.setAlpha(0.5f);
        progressbar_layout.setVisibility(View.GONE);
        strattransaction_layout.setVisibility(View.VISIBLE);
    }

    private PlayResponse playResponse;

    public void getPlayresponce(String gameId) {
        try {
            String playGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(gameId);
            assmntGamePlayRequest.setStudentid(activeUser.getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            String assessmentID = "" + assessmentId;
            if ((assessmentID != null) && (!assessmentID.isEmpty()))
                assmntGamePlayRequest.setAssessmentId(assessmentID);

            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponce();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponce();
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
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotPlayResponce() {
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
                        GameLetActivity.this.finish();
                        OustSdkTools.showToast(OustStrings.getString("unable_fetch_connection_error"));
                    }
                } else {
                    OustSdkTools.handlePopup(playResponse);
                    GameLetActivity.this.finish();
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("unable_fetch_connection_error"));
                GameLetActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int downloadQuestionNo = 0;
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int noofTry = 0;
    private String startDateTime;

    public void startDownloadingQuestions() {
        try {
            // gamelet_progressbar.setMax(totalQuestion);
            incrementDownloadQuestionNO += 10;
            if (incrementDownloadQuestionNO > totalQuestion) {
                incrementDownloadQuestionNO = totalQuestion;
            }
            //download_progressbar.setMax(totalQuestion);
            for (int i = downloadQuestionNo; i < incrementDownloadQuestionNO; i++) {
                DTOQuestions questions1 = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(i), false);
                if ((questions1 != null) && (questions1.getQuestionId() > 0)) {
                    downloadQuestionNo++;
                    updateCompletePresentage();
                } else {
                    getQuestionById(playResponse.getqIdList().get(i));
                }
            }
            if (totalQuestion == 0) {
                downloadQuestionNo++;
                updateCompletePresentage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getQuestionById(final int qID) {
        try {
            String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getquestion_url);
            getQuestionUrl = getQuestionUrl.replace("{qID}", ("" + qID));
            getQuestionUrl = HttpManager.getAbsoluteUrl(getQuestionUrl);
            JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();
            Log.d(TAG, "getQuestionById: " + getQuestionUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getQuestionUrl, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        DTOQuestions questions = getQuestion(response.toString());
                        checkForDownloadComplete(questions, qID);
                    } else {
                        checkForDownloadComplete(null, qID);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getQuestionUrl, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        DTOQuestions questions = getQuestion(response.toString());
                        checkForDownloadComplete(questions, qID);
                    } else {
                        checkForDownloadComplete(null, qID);
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
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
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
                return OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }


    public void checkForDownloadComplete(DTOQuestions questions, int qId) {
        try {

            if (questions != null) {
                OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
                downloadQuestionNo++;
                updateCompletePresentage();
            } else {
                noofTry++;
                if (noofTry < 4) {
                    getQuestionById(qId);
                } else {
                    OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean assessmentRunning = false;
    private boolean isActivityDestroyed;
    private int totalResourses = 0;
    private int downloadedResourses = 0;

    public void updateCompletePresentage() {
        try {
            float percentage = 0;
            if (totalQuestion > 0) {
                if (incrementDownloadQuestionNO == downloadQuestionNo) {
                    if (incrementDownloadQuestionNO == totalQuestion) {
                    } else {
                        startDownloadingQuestions();
                    }
                }
                percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 50;
            }
            float resoursesPercentage = (((float) downloadedResourses / (float) totalResourses) * 50);
            int totalPercent = (int) (resoursesPercentage + percentage);
            if (totalPercent == 50) {
                setAllImages();
            }
            gamelet_progressbar.setProgress(totalPercent);
            if (totalPercent >= 100) {
                gameletdownloadtext.setText(100 + "%");
                if (!isActivityDestroyed) {
                    scoresList = new ArrayList<>();
                    challengerFinalScore = 0;
                    questionIndex = 0;
                    assessmentRunning = true;
                    startDateTime = TimeUtils.getCurrentDateAsString();
                    if (isWordJumble2) {
                        setStartBtnStatus();
                    } else {
                        startTransctions();
                    }
                    card_layout.setVisibility(View.VISIBLE);
                }
            } else {
                gameletdownloadtext.setText((totalPercent) + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean allImagesSet = false;

    private void setAllImages() {
        try {
            if (!allImagesSet) {
                allImagesSet = true;
                OustSdkTools.setImage(nointernet_retry_bgd, getResources().getString(R.string.bg_word));
                OustSdkTools.setImage(demo_video, getResources().getString(R.string.demo));
                OustSdkTools.setImage(starttransaction_bgd, (getResources().getString(R.string.bg_word)));
//                OustSdkTools.setImage(demo_video, getResources().getString(R.string.demo));
                OustSdkTools.setImage(nointernet_cancel_bgd, getResources().getString(R.string.bg_word));
                OustSdkTools.setImage(close_video, getResources().getString(R.string.exit_icon));
                OustSdkTools.setImage(winner_layoutimageview, getResources().getString(R.string.video_bg));
                OustSdkTools.setImage(nointernet_popupimage, getResources().getString(R.string.instruction_bg));
                OustSdkTools.setImage(mygame_okbtn_bgd, getResources().getString(R.string.bg_word));
                OustSdkTools.setImage(score_board_layout_bgd, getResources().getString(R.string.board));
                OustSdkTools.setImage(loaderback_image, getResources().getString(R.string.gamelet_bg));
//                OustSdkTools.setBackground(downloadscreen_layout, getResources().getString(R.string.background_word));
            }
        } catch (Exception e) {
        }
    }

    private boolean gameStarted = false;
    private List<Scores> scoresList;
    private long challengerFinalScore = 0;
    private int questionIndex = 0;
    private DTOQuestions questions;
    private long questionStartTime = 0;

    private void startTransctions() {
        try {
            if (playResponse != null && (playResponse.getqIdList() != null)) {
                if (questionIndex < playResponse.getqIdList().size()) {
                    questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                    FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                    if (questionIndex == 0) {
                        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.alpha_anim);
                    } else {
                        transaction.setCustomAnimations(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                    }
                    questionStartTime = System.currentTimeMillis();
                    if (isWordJumble3) {
                        JumbleWordFragment2 fragment = new JumbleWordFragment2();
                        transaction.replace(R.id.frag_card_layout, fragment, "jumbleword");
                        fragment.setLearningModuleInterface(GameLetActivity.this);
                        if (!instruction.isEmpty()) {
                            fragment.setInstruction(instruction);
                        }
                        fragment.setQuestions(questions);
                        if ((questions != null) && (questions.getQuestion() != null)) {
                            user_question = questions.getQuestion();
                        }
                        fragment.setTotalXp(100);
                        fragment.setLearningcard_progressVal(questionIndex);
                        transaction.commit();
                    } else {
                        JumbleWordFragment fragment = new JumbleWordFragment();
                        transaction.replace(R.id.frag_card_layout, fragment, "jumbleword");
                        fragment.setLearningModuleInterface(GameLetActivity.this);
                        fragment.setWordJumble2(isWordJumble2);
                        if (!instruction.isEmpty()) {
                            fragment.setInstruction(instruction);
                        }
                        fragment.setQuestions(questions);
                        fragment.setTotalXp(100);
                        fragment.setLearningcard_progressVal(questionIndex);
                        transaction.commit();
                    }
                    card_layout.setVisibility(View.VISIBLE);
                } else {
                    removeCards();
                    if (isWordJumble2) {
                        showLoader();
                    } else {
                        showScorePopup();
                    }
                    calculateFinalScore();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=====================================================================================================

    private void saveAssessmentState() {

        AssessmentCopyResponse assessmentPlayResponse = new AssessmentCopyResponse();
        assessmentPlayResponse.setStudentId(activeUser.getStudentid());
        assessmentPlayResponse.setQuestionIndex("0");
        assessmentPlayResponse.setGameId(activeGame.getGameid());
        assessmentPlayResponse.setChallengerFinalScore("0");
        assessmentPlayResponse.setAssessmentState(AssessmentState.SUBMITTED);
        assessmentPlayResponse.setScoresList(null);
        assessmentPlayResponse.setUserAnswer(user_answer);
        assessmentPlayResponse.setUserQuestion(user_question);
        assessmentPlayResponse.setUserResponseTime(user_timetaken);
        assessmentPlayResponse.setUserSubmitTime(user_submittime);

        String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + assessmentId;
        OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponse);
    }

    private void showLoader() {
        downloadscreen_layout.setVisibility(View.VISIBLE);
        submit_response_text.setVisibility(View.VISIBLE);
        submit_response_text.setText(""+getResources().getString(R.string.submitting_response));
        mygame_loader.setVisibility(View.VISIBLE);
        OustSdkTools.setImage(loaderback_image, getResources().getString(R.string.gamelet_bg));

    }

    private boolean isScoreSubmitted = false;

    private void showScorePopup() {
        try {
            OustSdkTools.setImage(mygame_okbtn_bgd, getResources().getString(R.string.bg_word));
            OustSdkTools.setImage(score_board_layout_bgd, getResources().getString(R.string.board));
//            OustSdkTools.setBackground(downloadscreen_layout, getResources().getString(R.string.background_word));
            downloadscreen_layout.setVisibility(View.VISIBLE);
            score_board_layout.setVisibility(View.VISIBLE);
            card_layout.setVisibility(View.GONE);
            int timeTaken = 0;
            int totalScore = 0;
            for (int i = 0; i < scoresList.size(); i++) {
                timeTaken += scoresList.get(i).getTime();
                totalScore += scoresList.get(i).getXp();
            }
            mygame_okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isScoreSubmitted) {
                        GameLetActivity.this.finish();
                    } else {
                        if (networkPopupShown) {
                            networkPopupShown = false;
                            calculateFinalScore();
                        } else {
                            mygame_loader.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            mygame_timetext.setText(getResources().getString(R.string.time_taken).toUpperCase()+" : " + (timeTaken / 1000) + "sec");
            mygame_scoretext.setText(getResources().getString(R.string.your_score).toUpperCase()+" : " + totalScore);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeCards() {
        try {
            Animation moveout_anim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.learningview_slideanim);
            card_layout.startAnimation(moveout_anim);
            moveout_anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    card_layout.setVisibility(View.GONE);
                    FragmentTransaction transaction = GameLetActivity.this.getSupportFragmentManager().beginTransaction();
                    Fragment f = getSupportFragmentManager().findFragmentByTag("jumbleword");
                    if (f != null)
                        transaction.remove(f);
                    transaction.commit();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
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


    public void calculateFinalScore() {
        SubmitRequest submitRequest = new SubmitRequest();
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
            String gcmToken = OustPreferences.get("gcmToken");
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitRequest.setDeviceToken(gcmToken);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            submitRequest.setAssessmentId(("" + assessmentId));
            submitScore(submitRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitScore(final SubmitRequest submitRequest) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                if (isWordJumble2) {
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showNoInternetPopup(submitRequest);
                        }
                    }, 700);
                } else {
                    showCustomPopup("PLease check your network connection and try again.");
                }
                return;
            }
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
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

            /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, submitGameUrl, OustSdkTools.getRequestObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                    submitRequestProcessFinish(submitResponse);
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
            jsonObjReq.setShouldCache(false);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showNoInternetPopup(final SubmitRequest submitRequest) {
        try {
            RelativeLayout nointernet_retry_layout = findViewById(R.id.nointernet_retry_layout);
            OustSdkTools.setImage(nointernet_retry_bgd, getResources().getString(R.string.bg_word));
            RelativeLayout nointernet_cancel_layout = findViewById(R.id.nointernet_cancel_layout);
            nointernet_popup.setVisibility(View.VISIBLE);
            nointernet_retry_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nointernet_popup.setVisibility(View.GONE);
                    submitScore(submitRequest);
                }
            });
            nointernet_cancel_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nointernet_popup.setVisibility(View.GONE);
                    if (mygame_loader.getVisibility() == View.VISIBLE) {
                        myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GameLetActivity.this.finish();
                            }
                        }, 500);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPopupWithMessage(String message) {
        try {
            RelativeLayout nointernet_retry_layout = findViewById(R.id.nointernet_retry_layout);
            OustSdkTools.setImage(nointernet_retry_bgd, getResources().getString(R.string.bg_word));
            RelativeLayout nointernet_cancel_layout = findViewById(R.id.nointernet_cancel_layout);
            nointernet_retry.setText(getResources().getString(R.string.ok));
            TextView nointernet_text = findViewById(R.id.nointernet_text);
            TextView nointernet_textlabel = findViewById(R.id.nointernet_textlabel);
            nointernet_textlabel.setText(getResources().getString(R.string.success));
            nointernet_text.setText(message);
            nointernet_cancel_layout.setVisibility(View.GONE);
            nointernet_popup.setVisibility(View.VISIBLE);
            nointernet_retry_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nointernet_popup.setVisibility(View.GONE);
                    GameLetActivity.this.finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Handler myHandler;

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        try {
            assessmentRunning = false;
            if (submitResponse != null) {
                if (submitResponse.isSuccess()) {
                    isScoreSubmitted = true;
                    if (mygame_loader.getVisibility() == View.VISIBLE) {
                        myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveAssessmentState();
                                if ((wjSubmitMsg != null) && (!wjSubmitMsg.isEmpty())) {
                                    showCompletedPopup(wjSubmitMsg, true);
                                } else {
                                    OustSdkTools.showToast("Response Submitted");
                                    GameLetActivity.this.finish();
                                }
                            }
                        }, 500);
                    }
                } else {
                    OustSdkTools.handlePopup(submitResponse);
                }
            } else {
                showCustomPopup(OustStrings.getString("retry_internet_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean networkPopupShown = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            if (!isBackButtonDisabled) {
                if (watch_demovideo_layout.getVisibility() == View.VISIBLE) {
                    stopVideo();
                    return;
                }
                if (downloadscreen_layout.getVisibility() == View.VISIBLE) {

                } else {
                    FragmentManager readFragment = getSupportFragmentManager();
                    if (readFragment != null) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPopup(String content, boolean gotoSetting) {
        try {
            showApiFailUI();
            watch_demo_layout.setVisibility(View.GONE);
            mygame_loader.setVisibility(View.GONE);
            networkPopupShown = true;
            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText("OK");
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(content);
            if (gotoSetting) {
                popup.setType(OustPopupType.REDIRECT_SETTING_PAGE);
                popup.setCategory(OustPopupCategory.REDIRECT);
                oustPopupButton.setBtnText("GO TO SETTING");
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

    private void showCustomPopup(String content) {
        showApiFailUI();
        final RelativeLayout custom_popup = findViewById(R.id.custom_popup);
        custom_popup.setVisibility(View.VISIBLE);
        ImageView custom_popupimage = findViewById(R.id.custom_popupimage);
        OustSdkTools.setImage(custom_popupimage, getResources().getString(R.string.instruction_bg));

        TextView custompopup_text = findViewById(R.id.custompopup_text);
        custompopup_text.setText(content);

        ImageView custompopup_okbtnimage = findViewById(R.id.custompopup_okbtnimage);
        OustSdkTools.setImage(custompopup_okbtnimage, getResources().getString(R.string.bg_word));
        custompopup_okbtnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameLetActivity.this.finish();
                //custom_popup.setVisibility(View.GONE);
            }
        });

    }


    private void showCompletedPopup(String content, final boolean killActivity) {
        showApiFailUI();
        watch_demo_layout.setVisibility(View.GONE);
        final RelativeLayout jumbleComplted_popup = findViewById(R.id.jumbleComplted_popup);
        jumbleComplted_popup.setVisibility(View.VISIBLE);
        ImageView jumbleComplted_popupimage = findViewById(R.id.jumbleComplted_popupimage);
        OustSdkTools.setImage(jumbleComplted_popupimage, getResources().getString(R.string.instruction_bg));

        TextView jumbleComplted_text = findViewById(R.id.jumbleComplted_text);
        jumbleComplted_text.setText(content);
        HtmlTextView jumbleComplted_cluetext = findViewById(R.id.jumbleComplted_cluetext);
        jumbleComplted_cluetext.setHtml(user_question);
        jumbleComplted_cluetext.setTypeface(OustSdkTools.getTypefaceLithoPro());
        TextView jumbleComplted_youranstext = findViewById(R.id.jumbleComplted_youranstext);
        jumbleComplted_youranstext.setText(user_answer);
        TextView jumbleComplted_timetext = findViewById(R.id.jumbleComplted_timetext);

        if (user_timetaken > 0) {
            if (user_timetaken > 60000) {
                long timeInSec = user_timetaken / 1000;
                String timeStr = String.format("%02d:%02d", timeInSec / 60, timeInSec % 60);
                jumbleComplted_timetext.setText((timeStr) + " min(s)");
            } else {
                String timeStr = String.format("%d.%02d", user_timetaken / 1000, user_timetaken % 1000);
                jumbleComplted_timetext.setText((timeStr) + " sec(s)");
            }
        }

        TextView jumbleComplted_submittext = findViewById(R.id.jumbleComplted_submittext);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date parsedstrtDate = new Date(user_submittime);
        String startdate = dateFormat.format(parsedstrtDate);
        jumbleComplted_submittext.setText(startdate);
        ImageView jumbleComplted_okbtnimage = findViewById(R.id.jumbleComplted_okbtnimage);
        OustSdkTools.setImage(jumbleComplted_okbtnimage, getResources().getString(R.string.bg_word));
        jumbleComplted_okbtnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameLetActivity.this.finish();
            }
        });

    }

    @Override
    public void gotoNextScreen() {
        try {
            if (questionIndex < scoresList.size() && scoresList.get(questionIndex) != null) {
                questionIndex++;
                startTransctions();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void gotoPreviousScreen() {
    }

    @Override
    public void endActivity() {
        if (!isBackButtonDisabled) {
            GameLetActivity.this.finish();
        }
    }

    @Override
    public void restartActivity() {
    }

    @Override
    public void changeOrientationLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void changeOrientationPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void changeOrientationUnSpecific() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {
    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            gamelet_mainloader.setVisibility(View.GONE);
            Scores scores = new Scores();
            if ((userAns != null) && (!userAns.isEmpty())) {
                scores.setAnswer(userAns.toUpperCase());
                user_answer = userAns.toUpperCase();
            } else {
                scores.setAnswer((userAns));
            }
            scores.setCorrect(status);
            scores.setXp(oc);
            scores.setScore(oc);
            if (oc > 99) {
                scores.setCorrect(true);
            }
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questions.getQuestionType());
            scores.setQuestionSerialNo((questionIndex + 1));
            scores.setUserSubjectiveAns(subjectiveResponse);
            // add in array
            scores.setTime(time);
            user_timetaken = (time);
            user_submittime = System.currentTimeMillis();
            scoresList.add(questionIndex, scores);
        } catch (Exception e) {
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

    private boolean isBackButtonDisabled = false;

    @Override
    public void disableBackButton(boolean disableBackButton) {
        this.isBackButtonDisabled = disableBackButton;
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


    //initialize s3 clicent to download course resources
    private void initS3Client() {
        try {
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addGameResoursesToList() {
        String[] resList = getResources().getStringArray(R.array.wordjumble_icons);
        totalResourses = resList.length;
        downLoadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {

            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                // OustSdkTools.showToast(message);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                /*if (code == _COMPLETED) {
                    downloadedResourses++;
                    updateCompletePresentage();
                } else if (code == NO_NETWORK) {
                    showNetworkErrorMessage();
                }*/

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        setReceiver();
        for (int i = 0; i < resList.length; i++) {
            final File file = new File(this.getFilesDir(), resList[i]);
            if ((file != null) && (!file.exists())) {
                downLoad(resList[i]);
            } else {
                downloadedResourses++;
            }
        }
        updateCompletePresentage();
    }

    private int noofTries = 0;

    public void downLoad(final String fileName1) {
        try {
            String key = "AppResources/Android/All/Images/" + fileName1;
            final File file = new File(this.getFilesDir(), fileName1);
            if (file != null) {
//                downLoadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME, key, false, true);
                downLoadFiles.startDownLoad(CLOUD_FRONT_BASE_HTTPS + key, this.getFilesDir() + "/", fileName1, false, false);

             /*
                TransferObserver transferObserver = transferUtility.download(AppConstants.MediaURLConstants.BUCKET_NAME, key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            downloadedResourses++;
                            updateCompletePresentage();
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 4) {
                                showNetworkErrorMessage();
                                return;
                            } else {
                                downLoad(fileName1);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            showNetworkErrorMessage();
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        noofTries++;
                        if (noofTries > 4) {
                            showNetworkErrorMessage();
                        } else {
                            downLoad(fileName1);
                        }
                    }
                });
                */
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showNetworkErrorMessage() {
        if (!isActivityDestroyed) {
            isActivityDestroyed = false;
            OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            GameLetActivity.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.watch_demo_layout) {
            if (!isVideoPlaying) {
                isVideoPlaying = true;
                changeOrientationUnSpecific();
                PlayDemoVideo();
            }
        }
        if (v.getId() == R.id.close_video_layout) {
            stopVideo();
        }
    }

    public void setThumbnailImage(String imagePath, ImageView imageView) {
        try {
            imageView.setVisibility(View.VISIBLE);
            if ((imagePath != null) && (!imagePath.isEmpty())) {
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(imagePath).into(imageView);
                } else {
                    Picasso.get().load(imagePath).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    ======================================================================

    private void startPlayingSignedUrlVideo() {
        if (OustSdkTools.checkInternetStatus()) {
            String filename = videoFileName;
            if (videoFileName != null && videoFileName.contains(".mp4")) {
                filename = videoFileName.replace(".mp4", "");
            }
            String hlspath = "HLS/" + filename + "-HLS-Segment/" + filename + "-master-playlist.m3u8";
            isVideoHlsPresentOnS3(hlspath);
        } else {
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    private void isVideoHlsPresentOnS3(final String keyName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String bucketName = AppConstants.MediaURLConstants.BUCKET_NAME;
                    String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
                    String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
                    AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
                    s3Client.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                    s3Client.getObjectMetadata(bucketName, keyName);
                    playHls(true, keyName);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    playHls(false, "");
                }
            }
        }.start();
    }

    public void playHls(final boolean b, final String s) {
        try {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playHlsOrNormalSignedUrlVideo(b, s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void playHlsOrNormalSignedUrlVideo(boolean ishls, String hlsPath) {
        if (ishls) {
            String signedhlsPath = getSignedUrl(hlsPath);
            ishlsVideo = true;
            if (signedhlsPath != null && (!signedhlsPath.isEmpty())) {
                path = signedhlsPath;
            }
        } else {
            path = getSignedUrl((videoFileName));
        }
    }

    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private String path = null;
    private boolean ishlsVideo = false;
    private PlayerView simpleExoPlayerView;
    private boolean isVideoPlaying = false;

    private void PlayDemoVideo() {
        try {
            if (videoFileName.contains("/HLS/")) {
                ishlsVideo = true;
            }
            path = videoFileName;

            simpleExoPlayerView = new PlayerView(GameLetActivity.this);
            simpleExoPlayerView.setLayoutParams(new PlayerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
            demo_video_layout.addView(simpleExoPlayerView);
            //simpleExoPlayerView.getResources().getConfiguration().get
//          setPotraitVideoRatio();
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            demo_video_layout.setVisibility(View.VISIBLE);
            watch_demovideo_layout.setVisibility(View.VISIBLE);

            /*DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            final DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);*/

            //simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            simpleExoPlayer = new SimpleExoPlayer.Builder(GameLetActivity.this).build();
            MediaSource videoSource;
            Uri videoUri = Uri.parse(path);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(GameLetActivity.this, Util.getUserAgent(GameLetActivity.this, "exoplayer2example"));
            if (ishlsVideo) {
                /*DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter(); //test
                TrackSelection.Factory videoTrackSelectionFactoryA = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelectorA = new DefaultTrackSelector(videoTrackSelectionFactory);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(GameLetActivity.this, Util.getUserAgent(GameLetActivity.this, "exoplayer2example"), bandwidthMeter);
                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);//, dataSourceFactory, 1, null, null);*/

                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
                simpleExoPlayer.seekTo(0);
                simpleExoPlayerView.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setMediaSource(videoSource);
                simpleExoPlayer.prepare();
                if (show_quality.getVisibility() == View.GONE) {
                    show_quality.setVisibility(View.VISIBLE);
                }
            } else {
                File file = new File(path);
                if (file.exists()) {
                    videoUri = Uri.fromFile(file);
//                    Log.e("Player", "" + videoUri);
                    DataSpec dataSpec = new DataSpec(videoUri);
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
                    //videoSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, extractorsFactory, null, null);
                    videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));

                } else {
                    videoUri = Uri.parse(path);
                    //DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("my_exo_player");
                    //videoSource = new ExtractorMediaSource(videoUri, defaultHttpDataSourceFactory, extractorsFactory, null, null);
                    videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));

                }
                simpleExoPlayer.seekTo(0);
                simpleExoPlayerView.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setMediaSource(videoSource);
                simpleExoPlayer.prepare();
            }

            ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.e("-------", "onTracksChanged");
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.e("-------", "onLoadingChanged----" + isLoading);
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.e("-------", "onPlayerStateChanged-----" + playbackState);
                    if (playbackState == Player.STATE_READY) {
                        Log.e("-------", "STATE_READY");
                        if (demovideo_loader.getVisibility() == View.VISIBLE) {
                            demovideo_loader.clearAnimation();
                            demovideo_loader.setVisibility(View.GONE);
                        }
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        Log.e("-------", "STATE_BUFFERING");
                        if (demovideo_loader.getVisibility() == View.GONE) {
                            Animation rotateAnim = AnimationUtils.loadAnimation(GameLetActivity.this, R.anim.rotate_anim);
                            demovideo_loader.startAnimation(rotateAnim);
                            demovideo_loader.setVisibility(View.VISIBLE);
                        }
                    } else if (playbackState == Player.STATE_IDLE) {
                        Log.e("-------", "STATE_IDLE");
                        if (demovideo_loader.getVisibility() == View.GONE) {
                            Animation rotateAnim = AnimationUtils.loadAnimation(GameLetActivity.this, R.anim.rotate_anim);
                            demovideo_loader.startAnimation(rotateAnim);
                            demovideo_loader.setVisibility(View.VISIBLE);
                        }
                    } else if (playbackState == Player.STATE_ENDED) {
                        Log.e("-------", "STATE_ENDED");
                        if (demovideo_loader.getVisibility() == View.VISIBLE) {
                            demovideo_loader.clearAnimation();
                            demovideo_loader.setVisibility(View.GONE);
                        }
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
                    Log.e("-------", "onPlayerError");
                    if (show_quality.getVisibility() == View.VISIBLE) {
                        show_quality.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }


                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Log.e("-------", "onPlaybackParametersChanged");
                }

                @Override
                public void onSeekProcessed() {

                }
            };
            simpleExoPlayer.addListener(eventListener);
            simpleExoPlayer.setPlayWhenReady(true);
            show_quality.setTag(0);
            show_quality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                    if (mappedTrackInfo != null) {
                        trackSelectionHelper.showSelectionDialog(GameLetActivity.this, ((Button) view).getText(), trackSelector.getCurrentMappedTrackInfo(), (int) view.getTag());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopVideo() {
        try {
            isVideoPlaying = false;
            watch_demovideo_layout.setVisibility(View.GONE);
            changeOrientationPortrait();
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
                if (demovideo_loader.getVisibility() == View.VISIBLE) {
                    demovideo_loader.setVisibility(View.GONE);
                }
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                simpleExoPlayer.stop();
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                trackSelector = null;
                //trackSelectionHelper = null;
                Log.e("-------", "removeVideoPlayer");
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getSignedUrl(String objectKey) {
        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += (2000000);
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AppConstants.MediaURLConstants.BUCKET_NAME, objectKey);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setExpiration(expiration);
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
        if (url != null) {
            url.toString().replaceAll("https://", "http://");
        }
        return url.toString();
    }

    private void setPotraitVideoRatio() {
        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.height = scrHeight;
            params.width = scrWidth;
            demo_video_layout.setLayoutParams(params);
            simpleExoPlayerView.setLayoutParams(params);
        }
    }

    private void setLandscapeVideoRation() {
        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.height = scrHeight;
            params.width = scrWidth;
            simpleExoPlayerView.setLayoutParams(params);
            demo_video_layout.setLayoutParams(params);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (simpleExoPlayerView != null || simpleExoPlayer != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setLandscapeVideoRation();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setPotraitVideoRatio();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        stopVideo();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameLetActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
        try {
            unregisterReceiver(myFileDownLoadReceiver);
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        removeQuestionData();
    }

    private void removeQuestionData() {
        try {
            if (playResponse != null && playResponse.getqIdList() != null) {
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    RoomHelper.deleteQuestion(playResponse.getqIdList().get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //===========================================================
    TimeTCPClient client = new TimeTCPClient();
    private long netTime = 0;

    private void getInterNetTime() {
        checkForDeviceNetTime();
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Set timeout of 60 seconds
//                    client.setDefaultTimeout(15000);
//                    // Connecting to time server
//                    // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
//                    // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
//                    String hostName="time.nist.gov";
//                    if((OustPreferences.get("networkTimeProviderHostName")!=null)&&(!OustPreferences.get("networkTimeProviderHostName").isEmpty())){
//                        hostName=OustPreferences.get("networkTimeProviderHostName");
//                    }
//                    client.connect(hostName);
//                    netTime= client.getDate().getTime();
//                    Handler mainHandler = new Handler(OustSdkApplication.getContext().getMainLooper());
//                    Runnable myRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            startGame();
//                            initS3Client();
//                            addGameResoursesToList();
//                        }
//                    };
//                    mainHandler.post(myRunnable);
//                }catch (Exception e){
//                    checkForDeviceNetTime();
//                }
//            }
//        });
    }

    private void checkForDeviceNetTime() {
        try {
            int status = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
            if (status == 1) {
                netTime = System.currentTimeMillis();
                startGame();
                // initS3Client();
                addGameResoursesToList();
            } else {
                showPopup("Failed to get internet time, Please go to device setting and enable network time.", true);
                GameLetActivity.this.finish();
            }
        } catch (Exception e) {
        }
    }

    private CounterClass timer;

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
            showStartButton();
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            remainingTimeInSec--;
            setTimerText();
        }
    }

    private long remainingTimeInSec = 0;

    private void setStartBtnStatus() {
        try {
            OustSdkTools.setImage(starttransaction_bgd, (getResources().getString(R.string.bg_word)));
            progressbar_layout.setVisibility(View.GONE);
            if ((startTime == 0) || (netTime == 0)) {
                showStartButton();
            } else {
                if (netTime < startTime) {
                    remainingTimeInSec = (startTime - netTime) / 1000;
                    setTimerText();
                    timer_layout.setVisibility(View.VISIBLE);
                    startTimer();
                } else {
                    showStartButton();
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showStartButton() {
        timer_layout.setVisibility(View.GONE);
        strattransaction_layout.setVisibility(View.VISIBLE);
        strattransaction_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameStarted) {
                    gameStarted = true;
                    startTransctions();
                }
            }
        });
    }

    private void setTimerText() {
        try {
            if (remainingTimeInSec > 86400) {
                long jumbleRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
                long jumbleRemainingTimeMins = ((remainingTimeInSec % 86400) % 3600) / 60;
                String hms = String.format("%02d : %02d : %02d", remainingTimeInSec / 86400, jumbleRemainingTimeHours, jumbleRemainingTimeMins);
                timer_text.setText(hms);
            } else {
                long jumbleRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
                long jumbleRemainingTimeMins = ((remainingTimeInSec) % 3600) / 60;
                long jumbleRemainingTimeSec = ((remainingTimeInSec) % 3600) % 60;
                String hms = String.format("%02d : %02d : %02d", jumbleRemainingTimeHours, jumbleRemainingTimeMins, jumbleRemainingTimeSec);
                timer_text.setText(hms);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {

                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            Log.d(TAG, "downloadCplMedia: " + "complete");
                            downloadedResourses++;
                            updateCompletePresentage();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            Log.d(TAG, "downloadCplMedia: " + "Error");
                            showNetworkErrorMessage();


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
        if (OustSdkApplication.getContext() != null) {
            try {
                registerReceiver(myFileDownLoadReceiver, intentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }


//===================================================================================================
}
