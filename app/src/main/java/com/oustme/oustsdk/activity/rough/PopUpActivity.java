package com.oustme.oustsdk.activity.rough;

import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;



import com.amazonaws.services.chime.sdk.meetings.analytics.EventAnalyticsController;
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAnalyticsObserver;
import com.amazonaws.services.chime.sdk.meetings.analytics.EventAttributeName;
import com.amazonaws.services.chime.sdk.meetings.analytics.EventName;
import com.amazonaws.services.chime.sdk.meetings.analytics.MeetingHistoryEvent;
import com.amazonaws.services.chime.sdk.meetings.analytics.MeetingHistoryEventName;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoConfiguration;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoFacade;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.PrimaryMeetingPromotionObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.SignalUpdate;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.VolumeUpdate;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioMode;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioRecordingPresetOverride;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.AudioStreamType;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.activespeakerdetector.ActiveSpeakerObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.audio.activespeakerpolicy.ActiveSpeakerPolicy;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.contentshare.ContentShareStatus;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.metric.MetricsObserver;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.LocalVideoConfiguration;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoRenderView;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoSource;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoSubscriptionConfiguration;
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver;
import com.amazonaws.services.chime.sdk.meetings.device.DeviceChangeObserver;
import com.amazonaws.services.chime.sdk.meetings.device.MediaDevice;
import com.amazonaws.services.chime.sdk.meetings.realtime.RealtimeObserver;
import com.amazonaws.services.chime.sdk.meetings.realtime.TranscriptEventObserver;
import com.amazonaws.services.chime.sdk.meetings.realtime.datamessage.DataMessageObserver;
import com.amazonaws.services.chime.sdk.meetings.session.Attendee;
import com.amazonaws.services.chime.sdk.meetings.session.CreateAttendeeResponse;
import com.amazonaws.services.chime.sdk.meetings.session.CreateMeetingResponse;
import com.amazonaws.services.chime.sdk.meetings.session.DefaultMeetingSession;
import com.amazonaws.services.chime.sdk.meetings.session.MediaPlacement;
import com.amazonaws.services.chime.sdk.meetings.session.Meeting;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionConfiguration;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionCredentials;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatus;
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionURLs;
import com.amazonaws.services.chime.sdk.meetings.session.URLRewriterKt;
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel;
import com.amazonaws.services.chime.sdk.meetings.utils.logger.Logger;
import com.android.volley.Request;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.oustme.katexview.KatexView;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.oustme.oustsdk.LiveClasses.MeetingActivity;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
import com.oustme.oustsdk.api_sdk.impl.OustApiLauncher;
import com.oustme.oustsdk.api_sdk.models.OustEventResponseData;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.interfaces.common.OustLoginCallBack;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.launcher.OustNewLauncher;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.model.response.course.UserEventCourseData;
import com.oustme.oustsdk.network.ApiClient;
import com.oustme.oustsdk.response.course.AdaptiveCardDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.AdaptiveQuestionData;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PopUpActivity extends AppCompatActivity implements OustLoginCallBack, OustApiListener {
    String url = "http://www.pdf995.com/samples/pdf.pdf";
    String filepath;// = String path =
    public static Context context;
    private List<String> urls;
    private static final String TAG = "PopUpActivity";
    private TextView assessment_name;
    private Button openModule, getModuleStatus, getStatusById, mBtnEventStatus, mBtnLaunchModule,
            mBtnInitResources, mBtnRemoveResources, mBtnOustLogin, mBtnOustLogout, mBtnUserUpdate, mBtnOustRegister,
            mBtnOustLoginWithContext, mBtnOustLogoutWithContext, mBtnRemoveResourcesWithContext,
            mBtnLaunchOust, mBtnOustUpdateLang, mBtnOustGetLanguage;
    private TextView mtxtShowStatus, mProgressAlertMsg;
    private EditText mEtxtUserId, mEtxtEventId, mEtxtLanguage, mEtxtGroup, mEtxtFirstname, mEtxtLastName, mEtxtOrgId;
    private ProgressBar mProgressBar;
    //private final String orgId = "rapido";
    //private final String orgId = "swiggyll";
    //private final String orgId = "oustdev";
    private final String orgId = "qat";
    //private final String orgId = "uat";
    //private final String orgId = "CB";
    //private final String orgId = "oye";

    private AlertDialog mAlertDialogLoader;
    private AlertDialog.Builder mAlertBuilder;
    boolean isAutoInit = false;
    private Button mBtnOustLaunchCatalogue;
    private Button mBtnOustLaunchCard;
    private String userName = "durai2";
    private Button mBtnInitWithoutCallback, mBtnLaunchCourse;
    //OustApiLauncherInterface oustApiLauncherInterface;
    //OustApiLauncherImpl oustApiLauncherImpl;
    //OustApiModuleLauncher oustApiModuleLauncher;
    List<AdaptiveCourseLevelModel> adaptiveCourseLevelModel;
    List<AdaptiveCardDataModel> adaptiveCardDataModel;
    Gson gson = new Gson();

    //KatexView katexView;
    private RelativeLayout rootLayout;
    private PopupWindow mPopupWindow;
    private LinearLayout edit_pic_ll;
    private ImageButton deleted, edit;
    private Button btn, btn2;
    LinearLayout.LayoutParams params;
    int nonexwid, expwid, expht, nonexpht, padding;

    FlexboxLayout layout_survey_10points;
    int numberOfBox = 11;
    CardView.LayoutParams cardviewParams;

    WebView webviewText;

    MeetingSession meetingSession1;
    Button btnStart, btnStop, btnMute, btnUnMute, btnVoice, btnSpeaker, btnParticipants, btnStartShare, btnStopShare, btnOpen;
    private AttendeeInfo[] participantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        /*   katexView = findViewById(R.id.text);
        String text = "$$ c = \\pm\\sqrt{a^2 + b^2} $$";
        katexView.setTextColorString("#FFD700");
        katexView.setText(text);*/

        openModule = findViewById(R.id.openCourse);
        getModuleStatus = findViewById(R.id.get_status);
        getStatusById = findViewById(R.id.getStatusBuId);
        mBtnEventStatus = findViewById(R.id.mBtnEventStatus);
        mBtnLaunchModule = findViewById(R.id.mBtnLaunchModule);
        mtxtShowStatus = findViewById(R.id.mtxtShowStatus);
        mEtxtUserId = findViewById(R.id.mEtxtUserId);
        mEtxtOrgId = findViewById(R.id.mEtxtOrgId);
        mEtxtEventId = findViewById(R.id.mEtxtEventId);
        mBtnInitResources = findViewById(R.id.mBtnInitResources);
        mBtnRemoveResources = findViewById(R.id.mBtnRemoveResources);
        mBtnOustLogin = findViewById(R.id.mBtnOustLogin);
        mBtnUserUpdate = findViewById(R.id.mBtnUserUpdate);
        mBtnOustLogout = findViewById(R.id.mBtnOustLogout);

        mBtnOustLoginWithContext = findViewById(R.id.mBtnOustLoginWithContext);
        mBtnOustLogoutWithContext = findViewById(R.id.mBtnOustLogoutWithContext);
        mBtnRemoveResourcesWithContext = findViewById(R.id.mBtnRemoveResourcesWithContext);

        mBtnOustLaunchCatalogue = findViewById(R.id.mBtnOustLaunchCatalogue);
        mBtnOustLaunchCard = findViewById(R.id.mBtnOustLaunchCard);
        mBtnOustRegister = findViewById(R.id.mBtnOustRegister);
        mEtxtLanguage = findViewById(R.id.mEtxtlanguage);
        //mBtnOustLaunchCPL = findViewById(R.id.mBtnOustLaunchCPL);
        mEtxtGroup = findViewById(R.id.mEtxtGroup);
        //mBtnUpdateLanguage = findViewById(R.id.mBtnUpdateLanguage);
        mBtnInitWithoutCallback = findViewById(R.id.mBtnInitWithoutCallback);
        mBtnLaunchCourse = findViewById(R.id.mBtnLaunchCourse);

        mBtnLaunchOust = findViewById(R.id.mBtnlaunchOust);
        mEtxtFirstname = findViewById(R.id.mEtxtFname);
        mEtxtLastName = findViewById(R.id.mEtxtLname);

        mProgressBar = findViewById(R.id.mprogressbar);
        mProgressBar.setVisibility(View.GONE);

        mBtnOustUpdateLang = findViewById(R.id.mBtnOustUpdateLang);
        mBtnOustGetLanguage = findViewById(R.id.mBtnOustGetLanguage);
        //mProgressBar.bringToFront();

        webviewText = findViewById(R.id.webviewText);
        webviewText.getSettings().setJavaScriptEnabled(true);
        webviewText.getSettings().setBuiltInZoomControls(true);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnMute = findViewById(R.id.btnMute);
        btnUnMute = findViewById(R.id.btnUnMute);
        btnVoice = findViewById(R.id.btnVoice);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnParticipants = findViewById(R.id.btnParticipants);
        btnStartShare = findViewById(R.id.btnStartShare);
        btnStopShare = findViewById(R.id.btnStopShare);
        btnOpen = findViewById(R.id.btn_open);


        /*String textHtml = "<math xmlns=“http://www.w3.org/1998/Math/MathML”><mfrac><mn>1</mn><mn>200</mn></mfrac><msqrt><mo>&#177;</mo><msup><mi>s</mi><mn>2</mn></msup></msqrt></math>";
        webviewText.setBackgroundColor(Color.TRANSPARENT);
        String text = "<html><head>"
                + "<script src=\"https://polyfill.io/v3/polyfill.min.js?features=es6\"></script>\n" +
                "  <script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3.0.1/es5/tex-mml-chtml.js\"></script>"
                + "<style type=\"text/css\">ul,li,body{color: #000;}"
                + "</style></head>"
                + "<body><p>"
                + textHtml
                + "</p></body></html>";

        Log.d(TAG, "onCreate: text:"+text);
        final WebSettings webSettings = webviewText.getSettings();
        webSettings.setDefaultFontSize(20);
        webviewText.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);*/

        //Commit --- changes text api_sdk_2.8.9 - prod build v2.9.43.16 - production

        //oustApiLauncherImpl = new OustApiLauncherImpl();
        //oustApiLauncherInterface = OustApiLauncherInterface.this;

        //OustSdkApplication.setmContext(PopUpActivity.this);


        /*MeetingSession meetingSession = new MeetingSession() {
            @NonNull
            @Override
            public MeetingSessionConfiguration getConfiguration() {
                return null;
            }

            @NonNull
            @Override
            public Logger getLogger() {
                return null;
            }

            @NonNull
            @Override
            public AudioVideoFacade getAudioVideo() {
                return null;
            }

            @NonNull
            @Override
            public EventAnalyticsController getEventAnalyticsController() {
                return null;
            }
        };*/
        //meetingSession.getAudioVideo().start();


        //AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration();
        //meetingSession.getAudioVideo().start(audioVideoConfiguration);

        openModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OustApiLauncher.newInstance().launchOustApiService(PopUpActivity.this, userName, orgId, null);
            }
        });

        getModuleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OustApiLauncher.newInstance().launchOustApiService(PopUpActivity.this, userName, orgId, new OustModuleData("", "", "status"));
            }
        });

        getStatusById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OustModuleData oustModuleData = new OustModuleData();
                oustModuleData.setId("3041");
                oustModuleData.setRequestType("status");
                oustModuleData.setType("COURSE");
                OustApiLauncher.newInstance().launchOustApiService(PopUpActivity.this, userName, orgId, oustModuleData);
            }
        });

        mBtnEventStatus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mEtxtUserId.getText().toString();
                String eventId = mEtxtEventId.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                mtxtShowStatus.setText("");
                if (language == null || language.isEmpty()) {
                    //mtxtShowStatus.setText("language is mandatory");
                    OustApiLauncher.newInstance().getStatus(PopUpActivity.this, userId, orgId, eventId);
                } else {
                    OustApiLauncher.newInstance().getStatusWithLanguage(PopUpActivity.this, userId, orgId, eventId, language);
                }
            }
        });

        mBtnLaunchModule.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mEtxtUserId.getText().toString();
                String eventId = mEtxtEventId.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                mtxtShowStatus.setText("");

                if (language == null || language.isEmpty()) {
                    //mtxtShowStatus.setText("language is mandatory");
                    OustApiLauncher.newInstance().launchModule(PopUpActivity.this, userId, orgId, eventId);
                } else {
                    OustApiLauncher.newInstance().launchModuleWithLanguage(PopUpActivity.this, userId, orgId, eventId, language);
                }
            }
        });

        mBtnInitResources.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                if (isAutoInit) {
                    mAlertBuilder = new AlertDialog.Builder(PopUpActivity.this);
                    mAlertBuilder.setCancelable(false);
                    LayoutInflater inflater = PopUpActivity.this.getLayoutInflater();
                    View mView = inflater.inflate(R.layout.cpl_loading_progressbar, null);
                    mProgressAlertMsg = mView.findViewById(R.id.textViewLoadMsg);
                    mProgressAlertMsg.setText("Initializing Resources");
                    mAlertBuilder.setView(mView);
                    mAlertDialogLoader = mAlertBuilder.create();
                    mAlertDialogLoader.setCancelable(false);
                    mAlertDialogLoader.show();
                    mAlertDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }

                OustApiLauncher.newInstance().initResources(PopUpActivity.this, orgId, PopUpActivity.this);

            }
        });

        mBtnInitWithoutCallback.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("Resource initiated");
                OustApiLauncher.newInstance().initResourcesWithContext(PopUpActivity.this, orgId);
            }
        });

        mBtnRemoveResources.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                //Toast.makeText(PopUpActivity.this,"Under construction",Toast.LENGTH_LONG).show();
                OustApiLauncher.newInstance().removeResources(PopUpActivity.this);
            }
        });

        mBtnRemoveResourcesWithContext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                //Toast.makeText(PopUpActivity.this,"Under construction",Toast.LENGTH_LONG).show();
                OustApiLauncher.newInstance().removeResourcesWithAppContext(getApplicationContext());
            }
        });

        mBtnOustLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                String userId = mEtxtUserId.getText().toString();
                String fName = mEtxtFirstname.getText().toString();
                String lName = mEtxtLastName.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                if (language.length() > 0) {
                    OustApiLauncher.newInstance().oustLoginWithLanguage(PopUpActivity.this, orgId, userId, fName, lName, language);
                } else {
                    OustApiLauncher.newInstance().oustLoginWithContext(PopUpActivity.this, orgId, userId, fName, lName);
                    //mtxtShowStatus.setText("language is mandatory");
                }
            }
        });

        mBtnUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                String userId = mEtxtUserId.getText().toString();
                String fName = mEtxtFirstname.getText().toString();
                String lName = mEtxtLastName.getText().toString();

                if (userId.length() > 0) {

                    if (fName.length() > 0) {
                        OustApiLauncher.newInstance().updateUserDisplayName(PopUpActivity.this, orgId, userId, fName, lName);
                    } else {
                        mtxtShowStatus.setText("first name is mandatory");
                    }


                } else {
                    mtxtShowStatus.setText("User id is mandatory");
                }
            }
        });

        mBtnOustLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().oustLogout(PopUpActivity.this);
            }
        });

        mBtnOustLoginWithContext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                String userId = mEtxtUserId.getText().toString();
                String fName = mEtxtFirstname.getText().toString();
                String lName = mEtxtLastName.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                if (language != null && language.length() > 0) {
                    OustApiLauncher.newInstance().oustLanguageLoginWithAppContext(PopUpActivity.this, orgId, userId, fName, lName, language);
                } else {
                    //OustApiLauncher.newInstance().oustLoginWithAppContext(PopUpActivity.this, orgId, userId);
                    mtxtShowStatus.setText("language is mandatory");
                }
            }
        });

        mBtnOustLogoutWithContext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().oustLogoutwithAppContext(PopUpActivity.this);
            }
        });

        mBtnOustLaunchCatalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtxtShowStatus.setText("");
                String userId = mEtxtUserId.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                if (language != null && !language.isEmpty()) {
                    OustApiLauncher.newInstance().oustLaunchCatalogueWithLanguage(PopUpActivity.this, orgId, userId, language);
                } else {
                    OustApiLauncher.newInstance().oustLaunchCatalogue(PopUpActivity.this, orgId, userId);
                    //mtxtShowStatus.setText("language is mandatory");
                }
            }
        });

        mBtnOustLaunchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String eventId = mEtxtEventId.getText().toString();
                Intent intent = new Intent(PopUpActivity.this, NewCardLauncherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("courseId", new Long(1381));
                intent.putExtra("levelId", new Long(5187));
                intent.putExtra("cardId", new Long(eventId));
                PopUpActivity.this.startActivity(intent);*/
            }
        });

        mBtnOustRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtxtShowStatus.setText("");
                String userId = mEtxtUserId.getText().toString();
                if (userId.length() < 1) {
                    mtxtShowStatus.setText("userId should be mandatory");
                    return;
                }

                String language = mEtxtLanguage.getText().toString();
                if (language.length() < 1) {
                    mtxtShowStatus.setText("language should be mandatory");
                    return;
                }

                OustApiLauncher.newInstance().oustLoginAndLaunch(PopUpActivity.this, orgId, userId, language);
            }
        });

        mBtnLaunchCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mEtxtUserId.getText().toString();
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().launchCourse(PopUpActivity.this, userId, orgId);
            }
        });

        mBtnLaunchOust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {

                    String attendId = "3878d9b2-7fd7-11b3-4ea7-97b07c1cd970",
                            externalUserId = "monish.balaji@betterplace.co.in",
                            joinToken = "Mzg3OGQ5YjItN2ZkNy0xMWIzLTRlYTctOTdiMDdjMWNkOTcwOjQ3YWE1YzJhLTFlZTEtNDM5MC04YjNkLTVmZmNjNjYxNzgwZA";

                    //MeetingSessionCredentials meetingSessionCredentials = new MeetingSessionCredentials(attendId, externalUserId, joinToken);

                    String audioFallbackURL = "wss://haxrp.m2.uw1.app.chime.aws:443/calls/61c9aa69-ec5e-4e1f-b347-c003cac00706";
                    String audioHostURL = "84141d361c79ded239bc334257018266.k.m2.uw1.app.chime.aws:3478";
                    String ingestionURL = "https://data.svc.ue1.ingest.chime.aws/v1/client-events";
                    String signalingURL = "wss://signal.m2.uw1.app.chime.aws/control/61c9aa69-ec5e-4e1f-b347-c003cac00706";
                    String turnControlURL = "https://ccp.cp.ue1.app.chime.aws/v2/turn_sessions";

                    //com.amazonaws.services.chime.sdk.meetings.session.URLRewriterKt.URLRewriter urlRewriter= com.amazonaws.services.chime.sdk.meetings.session.URLRewriterKt.defaultUrlRewriter("");
                    //com.amazonaws.services.chime.sdk.meetings.session.URLRewriter

                    //MeetingSessionURLs meetingSessionURLs = new MeetingSessionURLs(audioFallbackURL, audioHostURL, turnControlURL, signalingURL, com.amazonaws.services.chime.sdk.meetings.session.URLRewriterKt.defaultUrlRewriter(""), ingestionURL);
                    //String meetingId = "f3a7696b-8d26-4b0a-8aac-21dbbe680706";
                    //MeetingSessionConfiguration meetingSessionConfiguration = new MeetingSessionConfiguration(meetingId ,meetingSessionCredentials, meetingSessionURLs);


                    String externalMeetingId = "";
                    String mediaRegion = "us-west-1";
                    String meetingId = "61c9aa69-ec5e-4e1f-b347-c003cac00706";

                    MediaPlacement mediaPlacement = new MediaPlacement(audioFallbackURL, audioHostURL, signalingURL, turnControlURL, ingestionURL);
                    Meeting meeting = new Meeting(externalMeetingId, mediaPlacement, mediaRegion, meetingId);
                    CreateMeetingResponse createMeetingResponse = new CreateMeetingResponse(meeting);

                    Attendee attendee = new Attendee(attendId, externalUserId, joinToken);
                    CreateAttendeeResponse createAttendeeResponse = new CreateAttendeeResponse(attendee);

                    MeetingSessionConfiguration meetingSessionConfiguration = new MeetingSessionConfiguration(createMeetingResponse, createAttendeeResponse);
                    meetingSession1 = new DefaultMeetingSession(meetingSessionConfiguration, new Logger() {
                        @Override
                        public void verbose(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "verbose: " + s1);
                        }

                        @Override
                        public void debug(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "debug: " + s1);
                        }

                        @Override
                        public void info(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "info: " + s1);
                        }

                        @Override
                        public void warn(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "warn: " + s1);
                        }

                        @Override
                        public void error(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "error: " + s1);
                        }

                        @Override
                        public void setLogLevel(@NonNull LogLevel logLevel) {
                            Log.d(TAG, "setLogLevel: " + logLevel);
                        }

                        @NonNull
                        @Override
                        public LogLevel getLogLevel() {
                            Log.d(TAG, "getLogLevel: ");
                            return LogLevel.VERBOSE;
                        }
                    }, PopUpActivity.this);


                    /*meetingSession1 = new MeetingSession() {
                        @NonNull
                        @Override
                        public MeetingSessionConfiguration getConfiguration() {
                            String externalMeetingId = "";
                            String mediaRegion = "us-west-1";
                            String meetingId = "61c9aa69-ec5e-4e1f-b347-c003cac00706";

                            MeetingMediaPlacement mediaPlacement = new MeetingMediaPlacement(audioFallbackURL, audioHostURL, signalingURL, turnControlURL, ingestionURL);
                            Meeting meeting = new Meeting(externalMeetingId, mediaPlacement, mediaRegion, meetingId);
                            CreateMeetingResponse createMeetingResponse = new CreateMeetingResponse(meeting);

                            Attendee attendee = new Attendee(attendId, externalUserId, joinToken);
                            CreateAttendeeResponse createAttendeeResponse = new CreateAttendeeResponse(attendee);

                            MeetingSessionConfiguration meetingSessionConfiguration = new MeetingSessionConfiguration(createMeetingResponse, createAttendeeResponse);


                            return meetingSessionConfiguration;
                        }

                        @NonNull
                        @Override
                        public Logger getLogger() {
                            com.amazonaws.logging.Log awslog = new com.amazonaws.logging.Log() {
                                @Override
                                public boolean isDebugEnabled() {
                                    return true;
                                }

                                @Override
                                public boolean isErrorEnabled() {
                                    return true;
                                }

                                @Override
                                public boolean isInfoEnabled() {
                                    return true;
                                }

                                @Override
                                public boolean isTraceEnabled() {
                                    return true;
                                }

                                @Override
                                public boolean isWarnEnabled() {
                                    return true;
                                }

                                @Override
                                public void trace(Object message) {
                                    Log.d(TAG, "trace: ");
                                }

                                @Override
                                public void trace(Object message, Throwable t) {
                                    Log.d(TAG, "trace: ");
                                }

                                @Override
                                public void debug(Object message) {
                                    Log.d(TAG, "debug: "+message);
                                }

                                @Override
                                public void debug(Object message, Throwable t) {
                                    Log.d(TAG, "debug: t: "+message);
                                }

                                @Override
                                public void info(Object message) {
                                    Log.d(TAG, "info: "+message);
                                }

                                @Override
                                public void info(Object message, Throwable t) {
                                    Log.d(TAG, "info: t: "+message);
                                }

                                @Override
                                public void warn(Object message) {
                                    Log.d(TAG, "warn: "+message);
                                }

                                @Override
                                public void warn(Object message, Throwable t) {
                                    Log.d(TAG, "warn: t: "+message);
                                }

                                @Override
                                public void error(Object message) {
                                    Log.d(TAG, "error: "+message);
                                }

                                @Override
                                public void error(Object message, Throwable t) {
                                    Log.d(TAG, "error: t: "+message);
                                }
                            };
                            return (Logger) awslog;
                        }

                        @NonNull
                        @Override
                        public AudioVideoFacade getAudioVideo() {
                            AudioVideoFacade audioVideoFacade = new AudioVideoFacade() {
                                @Override
                                public void addEventAnalyticsObserver(@NonNull EventAnalyticsObserver eventAnalyticsObserver) {
                                    Log.d(TAG, "addEventAnalyticsObserver: ");
                                }

                                @Override
                                public void removeEventAnalyticsObserver(@NonNull EventAnalyticsObserver eventAnalyticsObserver) {
                                    Log.d(TAG, "removeEventAnalyticsObserver: ");
                                }

                                @NonNull
                                @Override
                                public List<MeetingHistoryEvent> getMeetingHistory() {
                                    Log.d(TAG, "getMeetingHistory: ");
                                    return null;
                                }

                                @NonNull
                                @Override
                                public Map<EventAttributeName, Object> getCommonEventAttributes() {
                                    Log.d(TAG, "getCommonEventAttributes: ");
                                    return null;
                                }

                                @Override
                                public void start() {
                                    Log.d(TAG, "start: ");
                                }

                                @Override
                                public void start(@NonNull AudioVideoConfiguration audioVideoConfiguration) {
                                    Log.d(TAG, "start: audioVideoConfiguration");
                                }

                                @Override
                                public void stop() {
                                    Log.d(TAG, "stop: ");
                                }

                                @Override
                                public void addAudioVideoObserver(@NonNull AudioVideoObserver audioVideoObserver) {
                                    Log.d(TAG, "addAudioVideoObserver: ");
                                }

                                @Override
                                public void removeAudioVideoObserver(@NonNull AudioVideoObserver audioVideoObserver) {
                                    Log.d(TAG, "removeAudioVideoObserver: ");
                                }

                                @Override
                                public void addMetricsObserver(@NonNull MetricsObserver metricsObserver) {
                                    Log.d(TAG, "addMetricsObserver: ");
                                }

                                @Override
                                public void removeMetricsObserver(@NonNull MetricsObserver metricsObserver) {
                                    Log.d(TAG, "removeMetricsObserver: ");
                                }

                                @Override
                                public void startLocalVideo() {
                                    Log.d(TAG, "startLocalVideo: ");
                                }

                                @Override
                                public void startLocalVideo(@NonNull LocalVideoConfiguration localVideoConfiguration) {
                                    Log.d(TAG, "startLocalVideo: localVideoConfiguration");
                                }

                                @Override
                                public void startLocalVideo(@NonNull VideoSource videoSource) {
                                    Log.d(TAG, "startLocalVideo: videoSource");
                                }

                                @Override
                                public void startLocalVideo(@NonNull VideoSource videoSource, @NonNull LocalVideoConfiguration localVideoConfiguration) {
                                    Log.d(TAG, "startLocalVideo: videoSource, localVideoConfiguration");
                                }

                                @Override
                                public void stopLocalVideo() {
                                    Log.d(TAG, "stopLocalVideo: ");
                                }

                                @Override
                                public void startRemoteVideo() {
                                    Log.d(TAG, "startRemoteVideo: ");
                                }

                                @Override
                                public void stopRemoteVideo() {
                                    Log.d(TAG, "stopRemoteVideo: ");
                                }

                                @Override
                                public void updateVideoSourceSubscriptions(@NonNull Map<RemoteVideoSource, VideoSubscriptionConfiguration> map, @NonNull RemoteVideoSource[] remoteVideoSources) {
                                    Log.d(TAG, "updateVideoSourceSubscriptions: ");
                                }

                                @Override
                                public void promoteToPrimaryMeeting(@NonNull MeetingSessionCredentials meetingSessionCredentials, @NonNull PrimaryMeetingPromotionObserver primaryMeetingPromotionObserver) {
                                    Log.d(TAG, "promoteToPrimaryMeeting: ");
                                }

                                @Override
                                public void demoteFromPrimaryMeeting() {
                                    Log.d(TAG, "demoteFromPrimaryMeeting: ");
                                }

                                @Override
                                public void addActiveSpeakerObserver(@NonNull ActiveSpeakerPolicy activeSpeakerPolicy, @NonNull ActiveSpeakerObserver activeSpeakerObserver) {
                                    Log.d(TAG, "addActiveSpeakerObserver: ");
                                }

                                @Override
                                public void removeActiveSpeakerObserver(@NonNull ActiveSpeakerObserver activeSpeakerObserver) {
                                    Log.d(TAG, "removeActiveSpeakerObserver: ");
                                }

                                @Override
                                public void startContentShare(@NonNull ContentShareSource contentShareSource) {
                                    Log.d(TAG, "startContentShare: ");
                                }

                                @Override
                                public void startContentShare(@NonNull ContentShareSource contentShareSource, @NonNull LocalVideoConfiguration localVideoConfiguration) {
                                    Log.d(TAG, "startContentShare: ");
                                }

                                @Override
                                public void stopContentShare() {
                                    Log.d(TAG, "stopContentShare: ");
                                }

                                @Override
                                public void addContentShareObserver(@NonNull ContentShareObserver contentShareObserver) {
                                    Log.d(TAG, "addContentShareObserver: ");
                                }

                                @Override
                                public void removeContentShareObserver(@NonNull ContentShareObserver contentShareObserver) {
                                    Log.d(TAG, "removeContentShareObserver: ");
                                }

                                @Override
                                public void bindVideoView(@NonNull VideoRenderView videoRenderView, int i) {
                                    Log.d(TAG, "bindVideoView: ");
                                }

                                @Override
                                public void unbindVideoView(int i) {
                                    Log.d(TAG, "unbindVideoView: ");
                                }

                                @Override
                                public void addVideoTileObserver(@NonNull VideoTileObserver videoTileObserver) {
                                    Log.d(TAG, "addVideoTileObserver: ");
                                }

                                @Override
                                public void removeVideoTileObserver(@NonNull VideoTileObserver videoTileObserver) {
                                    Log.d(TAG, "removeVideoTileObserver: ");
                                }

                                @Override
                                public void pauseRemoteVideoTile(int i) {
                                    Log.d(TAG, "pauseRemoteVideoTile: ");
                                }

                                @Override
                                public void resumeRemoteVideoTile(int i) {
                                    Log.d(TAG, "resumeRemoteVideoTile: ");
                                }

                                @NonNull
                                @Override
                                public List<MediaDevice> listAudioDevices() {
                                    Log.d(TAG, "listAudioDevices: ");
                                    return null;
                                }

                                @Override
                                public void chooseAudioDevice(@NonNull MediaDevice mediaDevice) {
                                    Log.d(TAG, "chooseAudioDevice: ");
                                }

                                @Nullable
                                @Override
                                public MediaDevice getActiveAudioDevice() {
                                    Log.d(TAG, "getActiveAudioDevice: ");
                                    return null;
                                }

                                @Override
                                public void addDeviceChangeObserver(@NonNull DeviceChangeObserver deviceChangeObserver) {
                                    Log.d(TAG, "addDeviceChangeObserver: ");
                                }

                                @Override
                                public void removeDeviceChangeObserver(@NonNull DeviceChangeObserver deviceChangeObserver) {
                                    Log.d(TAG, "removeDeviceChangeObserver: ");
                                }

                                @Nullable
                                @Override
                                public MediaDevice getActiveCamera() {
                                    Log.d(TAG, "getActiveCamera: ");
                                    return null;
                                }

                                @Override
                                public void switchCamera() {
                                    Log.d(TAG, "switchCamera: ");
                                }

                                @Override
                                public boolean realtimeLocalMute() {
                                    Log.d(TAG, "realtimeLocalMute: ");
                                    return false;
                                }

                                @Override
                                public boolean realtimeLocalUnmute() {
                                    Log.d(TAG, "realtimeLocalUnmute: ");
                                    return false;
                                }

                                @Override
                                public void addRealtimeObserver(@NonNull RealtimeObserver realtimeObserver) {
                                    Log.d(TAG, "addRealtimeObserver: ");
                                }

                                @Override
                                public void removeRealtimeObserver(@NonNull RealtimeObserver realtimeObserver) {
                                    Log.d(TAG, "removeRealtimeObserver: ");
                                }

                                @Override
                                public void realtimeSendDataMessage(@NonNull String s, @NonNull Object o, int i) {
                                    Log.d(TAG, "realtimeSendDataMessage: ");
                                }

                                @Override
                                public void addRealtimeDataMessageObserver(@NonNull String s, @NonNull DataMessageObserver dataMessageObserver) {
                                    Log.d(TAG, "addRealtimeDataMessageObserver: ");
                                }

                                @Override
                                public void removeRealtimeDataMessageObserverFromTopic(@NonNull String s) {
                                    Log.d(TAG, "removeRealtimeDataMessageObserverFromTopic: ");
                                }

                                @Override
                                public boolean realtimeSetVoiceFocusEnabled(boolean b) {
                                    Log.d(TAG, "realtimeSetVoiceFocusEnabled: ");
                                    return false;
                                }

                                @Override
                                public boolean realtimeIsVoiceFocusEnabled() {
                                    Log.d(TAG, "realtimeIsVoiceFocusEnabled: ");
                                    return false;
                                }

                                @Override
                                public void addRealtimeTranscriptEventObserver(@NonNull TranscriptEventObserver transcriptEventObserver) {
                                    Log.d(TAG, "addRealtimeTranscriptEventObserver: ");
                                }

                                @Override
                                public void removeRealtimeTranscriptEventObserver(@NonNull TranscriptEventObserver transcriptEventObserver) {
                                    Log.d(TAG, "removeRealtimeTranscriptEventObserver: ");
                                }
                            };
                            return audioVideoFacade;
                        }

                        @NonNull
                        @Override
                        public EventAnalyticsController getEventAnalyticsController() {
                            EventAnalyticsController eventAnalyticsController = new EventAnalyticsController() {
                                @Override
                                public void publishEvent(@NonNull EventName eventName, @Nullable Map<EventAttributeName, Object> map) {
                                    Log.d(TAG, "publishEvent: ");
                                }

                                @Override
                                public void pushHistory(@NonNull MeetingHistoryEventName meetingHistoryEventName) {
                                    Log.d(TAG, "pushHistory: ");
                                }

                                @NonNull
                                @Override
                                public List<MeetingHistoryEvent> getMeetingHistory() {
                                    Log.d(TAG, "getMeetingHistory: ");
                                    return null;
                                }

                                @NonNull
                                @Override
                                public Map<EventAttributeName, Object> getCommonEventAttributes() {
                                    Log.d(TAG, "getCommonEventAttributes: ");
                                    return null;
                                }

                                @Override
                                public void addEventAnalyticsObserver(@NonNull EventAnalyticsObserver eventAnalyticsObserver) {
                                    Log.d(TAG, "addEventAnalyticsObserver: ");
                                }

                                @Override
                                public void removeEventAnalyticsObserver(@NonNull EventAnalyticsObserver eventAnalyticsObserver) {
                                    Log.d(TAG, "removeEventAnalyticsObserver: ");
                                }
                            };
                            return eventAnalyticsController;
                        }
                    };*/

                    AudioVideoObserver audioVideoObserver = new AudioVideoObserver() {
                        @Override
                        public void onAudioSessionStartedConnecting(boolean b) {
                            Log.d(TAG, "onAudioSessionStartedConnecting: ");
                        }

                        @Override
                        public void onAudioSessionStarted(boolean b) {
                            Log.d(TAG, "onAudioSessionStarted: ");
                        }

                        @Override
                        public void onAudioSessionDropped() {
                            Log.d(TAG, "onAudioSessionDropped: ");
                        }

                        @Override
                        public void onAudioSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                            Log.d(TAG, "onAudioSessionStopped: ");
                        }

                        @Override
                        public void onAudioSessionCancelledReconnect() {
                            Log.d(TAG, "onAudioSessionCancelledReconnect: ");
                        }

                        @Override
                        public void onConnectionRecovered() {
                            Log.d(TAG, "onConnectionRecovered: ");
                        }

                        @Override
                        public void onConnectionBecamePoor() {
                            Log.d(TAG, "onConnectionBecamePoor: ");
                        }

                        @Override
                        public void onVideoSessionStartedConnecting() {
                            Log.d(TAG, "onVideoSessionStartedConnecting: ");
                        }

                        @Override
                        public void onVideoSessionStarted(@NonNull MeetingSessionStatus meetingSessionStatus) {
                            Log.d(TAG, "onVideoSessionStarted: ");
                        }

                        @Override
                        public void onVideoSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                            Log.d(TAG, "onVideoSessionStopped: ");
                        }

                        @Override
                        public void onRemoteVideoSourceUnavailable(@NonNull List<RemoteVideoSource> list) {
                            Log.d(TAG, "onRemoteVideoSourceUnavailable: ");
                        }

                        @Override
                        public void onRemoteVideoSourceAvailable(@NonNull List<RemoteVideoSource> list) {
                            Log.d(TAG, "onRemoteVideoSourceAvailable: ");
                        }

                        @Override
                        public void onCameraSendAvailabilityUpdated(boolean b) {
                            Log.d(TAG, "onCameraSendAvailabilityUpdated: ");
                        }
                    };
                    meetingSession1.getAudioVideo().addAudioVideoObserver(audioVideoObserver);
                    AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration(AudioMode.Mono48K, AudioStreamType.VoiceCall, AudioRecordingPresetOverride.Generic);
                    meetingSession1.getAudioVideo().start(audioVideoConfiguration);

                } else {
                    ActivityCompat.requestPermissions(PopUpActivity.this, new
                            String[]{RECORD_AUDIO, MODIFY_AUDIO_SETTINGS}, 120);
                }


                //meetingSession1.getAudioVideo().start();

                
                /*MeetingSession meetingSession = new DefaultMeetingSession(meetingSessionConfiguration, new Logger() {
                    @Override
                    public void verbose(@NonNull String s, @NonNull String s1) {
                        Log.d(TAG, "verbose: "+s);
                    }

                    @Override
                    public void debug(@NonNull String s, @NonNull String s1) {
                        Log.d(TAG, "debug: "+s);
                    }

                    @Override
                    public void info(@NonNull String s, @NonNull String s1) {
                        Log.d(TAG, "info: "+s);
                    }

                    @Override
                    public void warn(@NonNull String s, @NonNull String s1) {
                        Log.d(TAG, "warn: "+s);
                    }

                    @Override
                    public void error(@NonNull String s, @NonNull String s1) {
                        Log.d(TAG, "error: "+s);
                    }

                    @Override
                    public void setLogLevel(@NonNull LogLevel logLevel) {
                        Log.d(TAG, "setLogLevel: ");
                    }

                    @NonNull
                    @Override
                    public LogLevel getLogLevel() {
                        return null;
                    }
                }, OustSdkApplication.getContext());*/
                /*meetingSession.getAudioVideo().addAudioVideoObserver(audioVideoObserver);
                meetingSession.getAudioVideo().start();*/

                /*String userId = mEtxtUserId.getText().toString();
                mtxtShowStatus.setText("");

                String orgId = mEtxtOrgId.getText().toString();
                OustAuthData authData = new OustAuthData();
                if(userId==null || userId.isEmpty()){
                    mtxtShowStatus.setText("UserId shouldn't be empty");
                    return;
                }
                if(orgId==null || orgId.isEmpty()){
                    mtxtShowStatus.setText("OrgId shouldn't be empty");
                    return;
                }

                authData.setUsername(userId);
                authData.setOrgId(orgId);

                String language = mEtxtLanguage.getText().toString();
                if(language!=null && !language.isEmpty()){
                    authData.setLanguage(language);
                }

                String fname = mEtxtFirstname.getText().toString();
                if(fname!=null && !fname.isEmpty()){
                    authData.setfName(fname);
                }

                String lname = mEtxtLastName.getText().toString();
                if(lname!=null && !lname.isEmpty()){
                    authData.setlName(lname);
                }

                try {
                    OustLauncher.getInstance().launch(PopUpActivity.this, authData, null,PopUpActivity.this);
                } catch (OustException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }*/

            }
        });

        mBtnOustUpdateLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = mEtxtUserId.getText().toString();
                String language = mEtxtLanguage.getText().toString();
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().updateUserPreferredLanguage(PopUpActivity.this, userId, orgId, language);
            }
        });

        mBtnOustGetLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = mEtxtUserId.getText().toString();
                mtxtShowStatus.setText("");
                OustApiLauncher.newInstance().getUserPreferredLanguage(PopUpActivity.this, userId, orgId);
            }
        });


        AudioVideoObserver audioVideoObserver = new AudioVideoObserver() {
            String TAG1 = "audioVideoObserver";

            @Override
            public void onAudioSessionStartedConnecting(boolean b) {
                Log.d(TAG1, "onAudioSessionStartedConnecting: " + b);
            }

            @Override
            public void onAudioSessionStarted(boolean b) {
                Log.d(TAG1, "onAudioSessionStarted: " + b);
            }

            @Override
            public void onAudioSessionDropped() {
                Log.d(TAG1, "onAudioSessionDropped: ");
            }

            @Override
            public void onAudioSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG1, "onAudioSessionStopped: " + meetingSessionStatus.getStatusCode());
            }

            @Override
            public void onAudioSessionCancelledReconnect() {
                Log.d(TAG1, "onAudioSessionCancelledReconnect: ");
            }

            @Override
            public void onConnectionRecovered() {
                Log.d(TAG1, "onConnectionRecovered: ");
            }

            @Override
            public void onConnectionBecamePoor() {
                Log.d(TAG1, "onConnectionBecamePoor: ");
            }

            @Override
            public void onVideoSessionStartedConnecting() {
                Log.d(TAG1, "onVideoSessionStartedConnecting: ");
            }

            @Override
            public void onVideoSessionStarted(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG, "onVideoSessionStarted: " + meetingSessionStatus.getStatusCode());
            }

            @Override
            public void onVideoSessionStopped(@NonNull MeetingSessionStatus meetingSessionStatus) {
                Log.d(TAG, "onVideoSessionStopped: " + meetingSessionStatus.getStatusCode());
            }

            @Override
            public void onRemoteVideoSourceUnavailable(@NonNull List<RemoteVideoSource> list) {
                Log.d(TAG, "onRemoteVideoSourceUnavailable: ");
            }

            @Override
            public void onRemoteVideoSourceAvailable(@NonNull List<RemoteVideoSource> list) {
                Log.d(TAG, "onRemoteVideoSourceAvailable: ");
            }

            @Override
            public void onCameraSendAvailabilityUpdated(boolean b) {
                Log.d(TAG, "onCameraSendAvailabilityUpdated: " + b);
            }
        };

        ContentShareObserver contentShareObserver = new ContentShareObserver() {
            @Override
            public void onContentShareStarted() {
                Log.d(TAG, "onContentShareStarted: ");
            }

            @Override
            public void onContentShareStopped(@NonNull ContentShareStatus contentShareStatus) {
                Log.d(TAG, "onContentShareStopped: ");
            }
        };

        RealtimeObserver realtimeObserver = new RealtimeObserver() {
            @Override
            public void onVolumeChanged(@NonNull VolumeUpdate[] volumeUpdates) {
                Log.d(TAG, "onVolumeChanged: ");
            }

            @Override
            public void onSignalStrengthChanged(@NonNull SignalUpdate[] signalUpdates) {
                Log.d(TAG, "onSignalStrengthChanged: ");
            }

            @Override
            public void onAttendeesJoined(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG, "onAttendeesJoined: ");
                participantsList = attendeeInfos;
            }

            @Override
            public void onAttendeesLeft(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG, "onAttendeesLeft: ");
                participantsList = attendeeInfos;
            }

            @Override
            public void onAttendeesDropped(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG, "onAttendeesDropped: ");
                participantsList = attendeeInfos;
            }

            @Override
            public void onAttendeesMuted(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG, "onAttendeesMuted: ");
                participantsList = attendeeInfos;
            }

            @Override
            public void onAttendeesUnmuted(@NonNull AttendeeInfo[] attendeeInfos) {
                Log.d(TAG, "onAttendeesUnmuted: ");
                participantsList = attendeeInfos;
            }
        };

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {

                    String attendId = "062e6b9d-f7ad-1fcc-8d28-84fe9ba2e644",
                            externalUserId = "duraivel.sellapillai@betterplace.co.in",
                            joinToken = "MDYyZTZiOWQtZjdhZC0xZmNjLThkMjgtODRmZTliYTJlNjQ0OmE4YmQyZTI1LWJlYWItNDY0MS1hOTU5LTQ2ODA2ODNhZTJkMg";

                    //MeetingSessionCredentials meetingSessionCredentials = new MeetingSessionCredentials(attendId, externalUserId, joinToken);

                    String audioFallbackURL = "wss://haxrp.m1.uw1.app.chime.aws:443/calls/6361c4a7-9b02-4a4d-9937-211965e00706";
                    String audioHostURL = "34396dd87722e4c10fffa341efd2c37f.k.m1.uw1.app.chime.aws:3478";
                    String ingestionURL = "https://data.svc.ue1.ingest.chime.aws/v1/client-events";
                    String signalingURL = "wss://signal.m1.uw1.app.chime.aws/control/6361c4a7-9b02-4a4d-9937-211965e00706";
                    String turnControlURL = "https://ccp.cp.ue1.app.chime.aws/v2/turn_sessions";

                    String externalMeetingId = "";
                    String mediaRegion = "us-west-1";
                    String meetingId = "6361c4a7-9b02-4a4d-9937-211965e00706";

                    MediaPlacement mediaPlacement = new MediaPlacement(audioFallbackURL, audioHostURL, signalingURL, turnControlURL, ingestionURL);
                    Meeting meeting = new Meeting(externalMeetingId, mediaPlacement, mediaRegion, meetingId);
                    CreateMeetingResponse createMeetingResponse = new CreateMeetingResponse(meeting);

                    Attendee attendee = new Attendee(attendId, externalUserId, joinToken);
                    CreateAttendeeResponse createAttendeeResponse = new CreateAttendeeResponse(attendee);

                    MeetingSessionConfiguration meetingSessionConfiguration = new MeetingSessionConfiguration(createMeetingResponse, createAttendeeResponse);
                    meetingSession1 = new DefaultMeetingSession(meetingSessionConfiguration, new Logger() {
                        @Override
                        public void verbose(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "verbose: " + s1);
                        }

                        @Override
                        public void debug(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "debug: " + s1);
                        }

                        @Override
                        public void info(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "info: " + s1);
                        }

                        @Override
                        public void warn(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "warn: " + s1);
                        }

                        @Override
                        public void error(@NonNull String s, @NonNull String s1) {
                            Log.d(TAG, "error: " + s1);
                        }

                        @Override
                        public void setLogLevel(@NonNull LogLevel logLevel) {
                            Log.d(TAG, "setLogLevel: " + logLevel);
                        }

                        @NonNull
                        @Override
                        public LogLevel getLogLevel() {
                            Log.d(TAG, "getLogLevel: ");
                            return LogLevel.VERBOSE;
                        }
                    }, PopUpActivity.this);

                    meetingSession1.getAudioVideo().addAudioVideoObserver(audioVideoObserver);
                    meetingSession1.getAudioVideo().addContentShareObserver(contentShareObserver);
                    meetingSession1.getAudioVideo().addRealtimeObserver(realtimeObserver);

                    AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration(AudioMode.Mono48K, AudioStreamType.VoiceCall, AudioRecordingPresetOverride.Generic);
                    meetingSession1.getAudioVideo().start(audioVideoConfiguration);
                    btnMute.setVisibility(View.VISIBLE);

                } else {
                    ActivityCompat.requestPermissions(PopUpActivity.this, new
                            String[]{RECORD_AUDIO, MODIFY_AUDIO_SETTINGS}, 120);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: stopped");
                if (meetingSession1 != null) {
                    meetingSession1.getAudioVideo().stop();
                    btnMute.setVisibility(View.GONE);
                    btnUnMute.setVisibility(View.GONE);
                }
            }
        });

        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnMute");
                if (meetingSession1 != null) {
                    meetingSession1.getAudioVideo().realtimeLocalMute();
                    btnMute.setVisibility(View.GONE);
                    btnUnMute.setVisibility(View.VISIBLE);
                }
            }
        });

        btnUnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnUnMute");
                if (meetingSession1 != null) {
                    meetingSession1.getAudioVideo().realtimeLocalUnmute();
                    btnMute.setVisibility(View.VISIBLE);
                    btnUnMute.setVisibility(View.GONE);
                }
            }
        });

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnVoice");
                if (meetingSession1 != null) {
                    AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration(AudioMode.Mono48K, AudioStreamType.VoiceCall, AudioRecordingPresetOverride.Generic);
                    meetingSession1.getAudioVideo().start(audioVideoConfiguration);

                    btnVoice.setVisibility(View.GONE);
                    btnSpeaker.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnSpeaker");
                if (meetingSession1 != null) {
                    AudioVideoConfiguration audioVideoConfiguration = new AudioVideoConfiguration(AudioMode.Mono48K, AudioStreamType.Music, AudioRecordingPresetOverride.VoiceCommunication);
                    meetingSession1.getAudioVideo().start(audioVideoConfiguration);
                    btnVoice.setVisibility(View.VISIBLE);
                    btnSpeaker.setVisibility(View.GONE);
                }
            }
        });

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(PopUpActivity.this, MeetingActivity.class);
            PopUpActivity.this.startActivity(intent);
        });

        btnParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnParticipants");
            }
        });


        btnStartShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnStartShare");
                if (meetingSession1 != null) {
                    ContentShareSource contentShareSource = new ContentShareSource();
                    LocalVideoConfiguration localVideoConfiguration = new LocalVideoConfiguration();
                    meetingSession1.getAudioVideo().startContentShare(contentShareSource);
                    btnStartShare.setVisibility(View.GONE);
                    btnStopShare.setVisibility(View.VISIBLE);
                }
            }
        });

        btnStopShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnStopShare");
                if (meetingSession1 != null) {
                    meetingSession1.getAudioVideo().stopContentShare();
                    btnStartShare.setVisibility(View.VISIBLE);
                    btnStopShare.setVisibility(View.GONE);
                }
            }
        });


    }

    int selected = 0;
    boolean isFirstTime = true;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStartDownloadingResourses() {
        Log.d(TAG, "onStartDownloadingResourses: ");
    }

    @Override
    public void onProgressChanged(int var1) {
        //Log.d(TAG, "onProgressChanged: "+var1);

        if (isAutoInit) {
            mProgressAlertMsg.setText("Initializing Resources..\n\n" + var1 + "%");
        } else {
            mtxtShowStatus.setText("" + var1 + " %");
        }

    }

    @Override
    public void onError(String var1) {
        Log.d(TAG, "onError: " + var1);
        if (!isAutoInit) {
            mtxtShowStatus.setText(var1);
        }
    }

    @Override
    public void onLoginError(String var1) {
        Log.d(TAG, "onLoginError: " + var1);
        mtxtShowStatus.setText("Login error : " + var1);
    }

    @Override
    public void onLoginProcessStart() {
        Log.d(TAG, "onLoginProcessStart: ");
        mtxtShowStatus.setText("Login Start");
    }

    @Override
    public void onLoginSuccess(boolean var1) {
        Log.d(TAG, "onLoginSuccess: ");
        mtxtShowStatus.setText("Loged In : " + (var1 ? "Success" : "Failed"));
        if (var1 && orgId.equalsIgnoreCase("rapido")) {
            mBtnLaunchCourse.performClick();
        }
    }

    @Override
    public void onNetworkError() {
        Log.d(TAG, "onNetworkError: ");
        mtxtShowStatus.setText("Network error");
    }

    @Override
    public void onOustLoginStatus(String message) {
        Log.d(TAG, "onOustLoginStatus: ");
    }

    @Override
    public void onModuleComplete(OustModuleData oustModuleData) {
        mtxtShowStatus.setText("onModuleComplete");
        Log.d(TAG, "onModuleComplete: " + oustModuleData.getId());
        Log.d(TAG, "onModuleComplete: " + oustModuleData.getRequestType());
        Log.d(TAG, "onModuleComplete: " + oustModuleData.getType());
    }

    @Override
    public void onModuleProgress(OustModuleData oustModuleData, int var2) {
        Log.d(TAG, "onModuleProgress: " + oustModuleData.getId());
        Log.d(TAG, "onModuleProgress: " + oustModuleData.getRequestType());
        Log.d(TAG, "onModuleProgress: " + oustModuleData.getType());
        Log.d(TAG, "onModuleProgress: " + var2);
    }

    @Override
    public void onModuleStatusChange(OustModuleData oustModuleData, String var2) {
        Log.d(TAG, "onModuleStatusChange: " + oustModuleData.getId());
        Log.d(TAG, "onModuleStatusChange: " + oustModuleData.getRequestType());
        Log.d(TAG, "onModuleStatusChange: " + oustModuleData.getType());
        Log.d(TAG, "onModuleStatusChange: " + var2);
    }

    @Override
    public void onModuleFailed(OustModuleData oustModuleData) {
        Log.d(TAG, "onModuleFailed: " + oustModuleData.getId());
        Log.d(TAG, "onModuleFailed: " + oustModuleData.getRequestType());
        Log.d(TAG, "onModuleFailed: " + oustModuleData.getType());
    }

    @Override
    public void onEventModuleStatusChange(OustModuleData oustModuleData, String var2) {
        try {
            //Log.d(TAG, "onEventModuleStatusChange: " + var2);

            //Log.d(TAG, "onEventModuleStatusChange: " + jsonObject.getString("contentType"));
            //Log.d(TAG, "onEventModuleStatusChange: " + jsonObject.getInt("contentId"));
            //Log.d(TAG, "onEventModuleStatusChange: " + jsonObject.getString("contentStatus"));
            //Log.d(TAG, "onEventModuleStatusChange: " + jsonObject.getString("requestType"));

            Gson gson = new Gson();
            String data = gson.toJson(oustModuleData);
            Log.d(TAG, "onEventModuleStatusChange: " + data);

            mtxtShowStatus.setText(var2);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onResourcesInitialized() {
        Log.d(TAG, "onResourcesInitialized");

        if (isAutoInit) {
            isAutoInit = false;
            if (mAlertDialogLoader != null) {
                mAlertDialogLoader.dismiss();
            }
        }
        mtxtShowStatus.setText("Resources Loaded");
    }

    @Override
    public void onResourcesRemoved() {
        Log.d(TAG, "onResourcesRemoved");
        mtxtShowStatus.setText("Resources Removed");
    }

    @Override
    public void onLanguageUpdated() {
        Log.d(TAG, "onLanguageUpdated: ");
        mtxtShowStatus.setText("Language updated");
    }

    @Override
    public void onUserDisplayNameUpdated() {

        Log.d(TAG, "onUserDisplayNameUpdated: ");
        mtxtShowStatus.setText("User display name updated");

    }

    @Override
    public void onUserPreferredLanguage(String languageName) {
        Log.d(TAG, "onUserPreferredLanguage: ");
        mtxtShowStatus.setText("" + languageName);
    }

    @Override
    public void onLogoutSuccess() {
        Log.d(TAG, "onLogoutSuccess");
        mtxtShowStatus.setText("User Logged Out");
    }

    @Override
    public void onCourseComplete() {
        Log.d(TAG, "onCourseComplete: ");
        mtxtShowStatus.setText("Course completed");
    }

    @Override
    public void onCplCompleted() {
        Log.d(TAG, "onCplCompleted: ");
        mtxtShowStatus.setText("CPL completed");
    }

    @Override
    public void onCplFailed() {
        Log.d(TAG, "onCplFailed: ");
        mtxtShowStatus.setText("CPL failed");
    }

    @Override
    public void onEventCourseStatusChange(OustEventResponseData oustEventResponseData, String var2) {
        Log.d(TAG, "onEventCourseStatusChange: " + var2);
        mtxtShowStatus.setText("" + var2);
        try {
            Gson gson = new Gson();
            String data = gson.toJson(oustEventResponseData);
            Log.d(TAG, "onEventCourseStatusChange: " + data);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onCourseProgress(UserEventCourseData userEventCourseData) {
        //Log.d(TAG, "onCourseProgress: "+userEventCourseData.getUserProgress());
        Gson gson = new Gson();
        String data = gson.toJson(userEventCourseData);
        Log.d(TAG, "onCourseProgress: " + data);
        try {
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mtxtShowStatus.setText(data);
    }

    @Override
    public void onAssessmentProgress(UserEventAssessmentData userEventAssessmentData) {
        //Log.d(TAG, "onAssessmentProgress: "+userEventAssessmentData.getUserProgress());
        Gson gson = new Gson();
        String data = gson.toJson(userEventAssessmentData);
        Log.d(TAG, "onAssessmentProgress: " + data);
        try {
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mtxtShowStatus.setText(data);
    }

    @Override
    public void onCPLProgress(UserEventCplData userEventCplData) {
        //Log.d(TAG, "onCPLProgress: "+userEventCplData.getUserProgress());
        Gson gson = new Gson();
        String data = gson.toJson(userEventCplData);
        Log.d(TAG, "onCPLProgress: " + data);
        try {
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        mtxtShowStatus.setText(data);
    }

    @Override
    public void onOustContentLoaded() {
        Log.d(TAG, "onOustContentLoaded: ");
        try {
            mtxtShowStatus.setText("onOustContentLoaded");
            Toast.makeText(getApplicationContext(), "onOustContentLoaded", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*@Override
    public void onUserLoggedIn(boolean var3) {
        Log.d(TAG, "onUserLoggedIn: "+var3);
        mtxtShowStatus.setText("User Logged In :"+var3);
    }*/

    public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ParticipantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ParticipantsAdapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
