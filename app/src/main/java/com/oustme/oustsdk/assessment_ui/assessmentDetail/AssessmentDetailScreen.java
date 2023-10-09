package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.assessments.AssessmentQuestionReviewBaseActivity;
import com.oustme.oustsdk.activity.assessments.NewAssessmentBaseActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.assessment_ui.examMode.AssessmentExamModeActivity;
import com.oustme.oustsdk.data.handlers.impl.MediaDataDownloader;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.mvvm.BaseActivity;
import com.oustme.oustsdk.question_module.assessment.AssessmentQuestionBaseActivity;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AssessmentDetailScreen extends BaseActivity<AssessmentDetailVM> {

    FrameLayout assessment_detail_layout;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    CardView card_view;
    RelativeLayout layout_main;
    ImageView assessment_image;
    LinearLayout assessment_info;
    LinearLayout assessment_duration_lay;
    TextView assessment_duration_text;
    LinearLayout assessment_qa_lay;
    TextView assessment_qa_text;
    LinearLayout assessment_coins_lay;
    TextView assessment_coins_text;
    TextView assessment_distribution_date;
    TextView assessment_title_full;
    TextView assessment_dead_line;
    TextView assessment_attempt_allowed;
    LinearLayout progress_lay;
    TextView completion_percentage_done;
    ProgressBar completion_percentage_progress;
    TextView assessment_description;
    WebView assessment_description_webView;
    LinearLayout assessment_attach_lay;
    TextView assessment_attach_text;
    LinearLayout completion_lay;
    LinearLayout show_result_score;
    TextView user_score_text;
    TextView your_percentage_text;
    TextView user_percentage_text;
    ProgressBar user_correct_answer_progress;
    TextView user_correct_answer_count;
    ProgressBar user_wrong_answer_progress;
    TextView user_wrong_answer_count;
    FrameLayout assessment_action_button;
    TextView assessment_status_text;
    LinearLayout assessment_start_timer;
    TextView assessment_timer_text;
    CircularProgressIndicator circularProgressIndicator;
    LinearLayout assessment_data_loader;
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    ImageView img_coin;

    AssessmentDetailRepo assessmentDetailRepo;
    private AssessmentDetailOther assessmentDetailOther;
    private int mediaDownloadCount = 0;
    long systemTime = 0;
    long remainingTimeInSec;
    boolean isTakeAnswer;
    boolean isButtonEnabled = true;
    ActiveGame activeGame;
    PlayResponse playResponse;
    AssessmentPlayResponse assessmentPlayResponse;
    AssessmentDetailModel assessmentDetailModel;
    private final String TAG = "AssessmentDetail";

    private int color;
    private int bgColor;

    private boolean leaderBoardEnable;
    private boolean isSurveyFromCourse;
    private boolean isMultipleCplModule = false;
    private long currentCplId;
    private boolean isComingFromCPL;

    boolean isMicroCourse;
    boolean isActivityAvailable = true;

    @Override
    protected void onStart() {
        isActivityAvailable = true;
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        isActivityAvailable = false;
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        try {
            getViewModelStore().clear();
            if (assessmentDetailRepo != null) {
                assessmentDetailRepo = null;
                this.viewModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        try {
            circularProgressIndicator.setVisibility(View.VISIBLE);
            assessment_action_button.setVisibility(View.GONE);
            assessment_data_loader.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideLoader() {
        try {
            circularProgressIndicator.setVisibility(View.GONE);
            if (assessmentDetailModel.getStartAssessment() <= systemTime) {
                assessment_action_button.setVisibility(View.VISIBLE);
                assessment_start_timer.setVisibility(View.GONE);
            } else {
                assessment_action_button.setVisibility(View.GONE);
                assessment_start_timer.setVisibility(View.VISIBLE);
            }
            assessment_data_loader.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.activity_assessment_detail_screen);

        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(AssessmentDetailScreen.this);
        }
        OustSdkTools.setLocale(AssessmentDetailScreen.this);
        getColors();

        assessment_detail_layout = findViewById(R.id.assessment_detail_layout);
        toolbar = findViewById(R.id.toolbar_lay);
        screen_name = findViewById(R.id.screen_name);
        back_button = findViewById(R.id.back_button);
        toolbar.setBackgroundColor(bgColor);
        screen_name.setTextColor(color);
        OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        screen_name.setText(getResources().getString(R.string.assessment).toUpperCase());

        card_view = findViewById(R.id.card_view);
        layout_main = findViewById(R.id.layout_main);
        assessment_image = findViewById(R.id.assessment_image);
        assessment_info = findViewById(R.id.assessment_info);
        assessment_duration_lay = findViewById(R.id.assessment_duration_lay);
        assessment_duration_text = findViewById(R.id.assessment_duration_text);
        assessment_qa_lay = findViewById(R.id.assessment_qa_lay);
        assessment_qa_text = findViewById(R.id.assessment_qa_text);
        assessment_coins_lay = findViewById(R.id.assessment_coins_lay);
        assessment_coins_text = findViewById(R.id.assessment_coins_text);
        assessment_distribution_date = findViewById(R.id.assessment_distribution_date);
        assessment_title_full = findViewById(R.id.assessment_title_full);
        assessment_dead_line = findViewById(R.id.assessment_dead_line);
        assessment_attempt_allowed = findViewById(R.id.assessment_attempt_allowed);
        progress_lay = findViewById(R.id.progress_lay);
        completion_percentage_done = findViewById(R.id.completion_percentage_done);
        completion_percentage_progress = findViewById(R.id.completion_percentage_progress);
        assessment_description = findViewById(R.id.assessment_description);
        assessment_description_webView = findViewById(R.id.assessment_description_webView);
        assessment_attach_lay = findViewById(R.id.assessment_attach_lay);
        assessment_attach_text = findViewById(R.id.assessment_attach_text);
        completion_lay = findViewById(R.id.completion_lay);
        show_result_score = findViewById(R.id.show_result_score);
        user_score_text = findViewById(R.id.user_score_text);
        your_percentage_text = findViewById(R.id.your_percentage_text);
        user_percentage_text = findViewById(R.id.user_percentage_text);
        user_correct_answer_progress = findViewById(R.id.user_correct_answer_progress);
        user_correct_answer_count = findViewById(R.id.user_correct_answer_count);
        user_wrong_answer_progress = findViewById(R.id.user_wrong_answer_progress);
        user_wrong_answer_count = findViewById(R.id.user_wrong_answer_count);
        assessment_action_button = findViewById(R.id.assessment_action_button);
        assessment_status_text = findViewById(R.id.assessment_status_text);
        assessment_start_timer = findViewById(R.id.assessment_start_timer);
        assessment_timer_text = findViewById(R.id.assessment_timer_text);
        circularProgressIndicator = findViewById(R.id.assessment_progress_color);
        assessment_data_loader = findViewById(R.id.assessment_data_loader);

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
                File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

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
            circularProgressIndicator.setIndicatorColor(color);
            circularProgressIndicator.setTrackColor(getResources().getColor(R.color.gray));

            showLoader();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        OustStaticVariableHandling.getInstance().setQuestionViewList(null);
//        OustGATools.getInstance().reportPageViewToGoogle(AssessmentDetailScreen.this, "Assessment Detail Page");
        OustSdkTools.speakInit();

        setIconColors();

        viewModel.loadAssessmentDetailData();
        viewModel.getAssessmentDetail().observe(this, new AssessmentDetailObserver());

        viewModel.loadAssessmentOtherDetailData();
        viewModel.getAssessmentOtherDetail().observe(this, new AssessmentDetailOtherObserver());

        viewModel.loadAssessmentProgressData();
        viewModel.getAssessmentProgress().observe(this, new AssessmentProgressObserver());

        assessment_action_button.setOnClickListener(view -> {
            try {
                if (assessment_status_text.getText().toString().equalsIgnoreCase(getResources().getString(R.string.submit))) {
                    viewModel.layout_SubmitButton();
                } else {
                    if (!isButtonEnabled) {
                        AssessmentDetailScreen.this.finish();

                    } else {
                        branding_mani_layout.setVisibility(View.VISIBLE);

                        if (isTakeAnswer) {
                            viewModel.layout_AnswerButton();
                        } else {
                            isMediaStartedDownload = true;
                            viewModel.layout_StartButton();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        });

        back_button.setOnClickListener(v -> onBackPressed());
    }

    @NonNull
    @Override
    protected AssessmentDetailVM createViewModel(Bundle bundle) {
        if (bundle != null) {
            isMicroCourse = bundle.getBoolean("isMicroCourse", false);
            isSurveyFromCourse = bundle.getBoolean("isSurveyFromCourse", false);
            currentCplId = bundle.getLong("currentCplId");
            isComingFromCPL = bundle.getBoolean("isComingFromCpl");
            isMultipleCplModule = bundle.getBoolean("isMultipleCplModule", false);
        }
        if (assessmentDetailRepo != null) {
            getViewModelStore().clear();
            assessmentDetailRepo = null;
            this.viewModel = null;
        }
        assessmentDetailRepo = new AssessmentDetailRepo(bundle);
        AssessmentDetailsVMFactory factory = new AssessmentDetailsVMFactory(assessmentDetailRepo);
        return new ViewModelProvider(AssessmentDetailScreen.this, factory).get(AssessmentDetailVM.class);
    }

    private class AssessmentProgressObserver implements Observer<AssessmentProgressbar> {
        @Override
        public void onChanged(AssessmentProgressbar assessmentProgressbar) {
            Log.d(TAG, "AssessmentProgressObserver: ");
            if (assessmentProgressbar == null) {
                return;
            }
            branding_percentage.setVisibility(View.VISIBLE);
            branding_percentage.setText(assessmentProgressbar.getTxtProgress());
        }
    }

    private class AssessmentDetailOtherObserver implements Observer<AssessmentDetailOther> {
        @Override
        public void onChanged(AssessmentDetailOther assessmentDetailOther) {
            Log.d(TAG, "AssessmentDetailOtherObserver: ");
            if (assessmentDetailOther == null) return;

            if (assessmentDetailOther.isNextWorkFail()) {
                isMediaStartedDownload = false;
                nextWorkFail();
                return;
            }

            if (assessmentDetailOther.getType() == 1) {
                startPopupActivity(assessmentDetailOther.getActiveGame());
            } else if (assessmentDetailOther.getType() == 2) {
                startAssessmentActivity(assessmentDetailOther);
            } else if (assessmentDetailOther.getType() == 3) {
                gotoQuestionPage(assessmentDetailOther);
            } else if (assessmentDetailOther.getType() == 4) {
                startDownloadMedia(assessmentDetailOther);
            } else if (assessmentDetailOther.getType() == 5) {
                gotoAssessmentReviewAnswer(assessmentDetailOther);
            } else if (assessmentDetailOther.getType() == 6) {
                gotoAssessmentConditionalFlow(assessmentDetailOther);
            }
        }
    }

    private void gotoAssessmentConditionalFlow(AssessmentDetailOther assessmentDetailOther) {
        try {
            Intent intent = new Intent(AssessmentDetailScreen.this, ConditionalFormActivity.class);
            intent.putExtra("formUrl", assessmentDetailOther.getConditionalFlowUrl());
            intent.putExtra("gameId", assessmentDetailOther.getActiveGame().getGameid().toString());
            intent.putExtra("assessmentId", String.valueOf(assessmentDetailOther.getAssessmentFirebaseClass().getAsssessemntId()));
            intent.putExtra("assessmentName", assessmentDetailOther.getAssessmentFirebaseClass().getName());
            intent.putExtra("userId", OustAppState.getInstance().getActiveUser().getStudentid());
            startActivity(intent);

            AssessmentDetailScreen.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isMediaStartedDownload = false;
    private boolean isMediaDownloaded = false;

    private void startDownloadMedia(AssessmentDetailOther assessmentDetailOther) {
        Log.d(TAG, "startDownloadMedia: ");
        branding_mani_layout.setVisibility(View.VISIBLE);
        this.assessmentDetailOther = assessmentDetailOther;
        List<String> mediaList = assessmentDetailOther.getMediaList();
        if (mediaList != null && mediaList.size() > 0) {
            for (int i = 0; i < mediaList.size(); i++) {
                String media = mediaList.get(i);
                new MediaDataDownloader(this) {
                    @Override
                    public void downloadComplete() {
                        try {
                            mediaDownloadCount++;

                            String percentageText;
                            if (mediaDownloadCount == mediaList.size()) {
                                percentageText = "100%";
                                branding_percentage.setVisibility(View.VISIBLE);
                                branding_percentage.setText(percentageText);
                                fetchingDataFinish();
                            } else {
                                float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                                if (percentage < 100) {
                                    percentageText = (int) percentage + "%";
                                } else {
                                    percentageText = "100%";
                                }
                                branding_percentage.setVisibility(View.VISIBLE);
                                branding_percentage.setText(percentageText);
                                Log.d(TAG, "downloadComplete: " + percentage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void downFailed(String message) {
                        OustSdkTools.showToast(message);
                    }
                }.initDownload(media);
            }
        } else {
            fetchingDataFinish();
        }
    }

    public void fetchingDataFinish() {
        Log.d(TAG, "fetchingDataFinish: ");
        isMediaDownloaded = true;
        isMediaStartedDownload = false;
        try {
            if (!isActivityAvailable) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        assessmentPlayResponse = assessmentDetailOther.getAssessmentPlayResponse();
        if ((assessmentPlayResponse != null) && (assessmentPlayResponse.getAssessmentState() != null)) {
            gotoQuestionPage(assessmentDetailOther);
        } else {
            if (isTakeAnswer) {
                gotoAssessmentReviewAnswer(assessmentDetailOther);
            } else {
                startAssessmentActivity(assessmentDetailOther);
            }
        }
    }

    private void startPopupActivity(ActiveGame activeGame) {
        Log.d(TAG, "startAssessmentActivity: ");
        Gson gson = new GsonBuilder().create();
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("ActiveGame", gson.toJson(activeGame));
        startActivity(intent);
        AssessmentDetailScreen.this.finish();
    }

    @Override
    public void onBackPressed() {
        try {
            Log.d(TAG, "onBackPressed: isMediaStartedDownload:" + isMediaStartedDownload + " -- isMediaDownloaded:" + isMediaDownloaded);
            if (isMediaStartedDownload) {
                if (!isMediaDownloaded) {
                    return;
                }
            }
            try {

            } catch (Exception e) {

            }

            if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isFromCourse()) {
                UserEventAssessmentData userEventAssessmentData = viewModel.assessmentDetailRepo.userEventAssessmentData;
                if ((OustAppState.getInstance().getAssessmentFirebaseClass() != null) && (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) && (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                    userEventAssessmentData.setUserProgress("COMPLETED");
                } else {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    userEventAssessmentData.setUserProgress("INPROGRESS");
                }
                userEventAssessmentData.setEventId(0);
                if (OustAppState.getInstance().getAssessmentFirebaseClass() != null) {
                    OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);
                }
                if (isMicroCourse) {
                    try {
                        OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

            } else {
                if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isEvent() && OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                    UserEventAssessmentData userEventAssessmentData = viewModel.assessmentDetailRepo.userEventAssessmentData;
                    if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                        userEventAssessmentData.setUserProgress("COMPLETED");
                    } else {
                        userEventAssessmentData.setUserProgress("INPROGRESS");
                    }

                    if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isCplModule()) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setCurrentModuleType("ASSESSMENT");

                        userEventCplData.setCurrentModuleId(userEventAssessmentData.getAssessmentId());
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                        userEventCplData.setnTotalModules(totalModules);

                        if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                            userEventCplData.setCurrentModuleProgress("COMPLETED");
                            if (completedModules >= totalModules) {
                                userEventCplData.setnModulesCompleted(totalModules);
                                userEventCplData.setUserProgress("COMPLETED");
                            } else {
                                userEventCplData.setnModulesCompleted(completedModules + 1);
                                userEventCplData.setUserProgress("INPROGRESS");
                            }
                        } else {
                            userEventCplData.setCurrentModuleProgress("INPROGRESS");
                            userEventCplData.setnModulesCompleted(completedModules);
                            userEventCplData.setUserProgress("INPROGRESS");
                        }

                        userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                        OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                    } else {
                        OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onBackPressed();
    }

    private void startAssessmentActivity(AssessmentDetailOther assessmentDetailOther) {
        try {
            Log.d(TAG, "startAssessmentActivity: ");
            ActiveGame activeGame = assessmentDetailOther.getActiveGame();
            AssessmentFirebaseClass assessmentFirebaseClass = assessmentDetailOther.getAssessmentFirebaseClass();
            AssessmentExtraDetails assessmentExtraDetails = assessmentDetailOther.getAssessmentExtraDetails();
            ArrayList<DTOQuestions> questionsArrayList = assessmentDetailOther.getQuestionsArrayList();
            toolbar.setVisibility(View.VISIBLE);

            try {
                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                eventUpdate.put("ClickedOnNextArrow", true);
                eventUpdate.put("AssessmentID", assessmentFirebaseClass.getAsssessemntId());
                eventUpdate.put("Assessment Name", assessmentFirebaseClass.getName());
                Log.d("TAG", "CleverTap instance: " + eventUpdate.toString());
                if (clevertapDefaultInstance != null) {
                    clevertapDefaultInstance.pushEvent("Assessment_Enroll", eventUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            Gson gson = new GsonBuilder().create();
            Intent intent = new Intent(this, NewAssessmentBaseActivity.class);
            if (OustPreferences.getAppInstallVariable("isLayout4") && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI)) {
                OustStaticVariableHandling.getInstance().setQuestionsArrayList(questionsArrayList);
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.isExamMode() && assessmentFirebaseClass.getDuration() != 0) {
                    intent = new Intent(this, AssessmentExamModeActivity.class);
                    intent.putExtra("examMode", true);
                    intent.putExtra("examDuration", assessmentFirebaseClass.getDuration());
                    intent.putExtra("enrolledTime", assessmentFirebaseClass.getEnrolledTime());
                } else {
                    intent = new Intent(this, AssessmentQuestionBaseActivity.class);
                }
            }
            if (assessmentFirebaseClass != null && assessmentFirebaseClass.getQuestionXp() != 0) {
                intent.putExtra("questionXp", assessmentFirebaseClass.getQuestionXp());
            }

            assert assessmentFirebaseClass != null;
            intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());
            intent.putExtra("bannerImage", assessmentFirebaseClass.getBanner());
            intent.putExtra("moduleName", assessmentFirebaseClass.getName());
            intent.putExtra("moduleId", assessmentFirebaseClass.getAsssessemntId());
            intent.putExtra("containCertificate", assessmentExtraDetails.isContainCertificate());
            intent.putExtra("isCplModule", assessmentExtraDetails.isCplModule());
            intent.putExtra("totalTimeOfAssessment", 0);
            intent.putExtra("currentCplId", currentCplId);
            intent.putExtra("isMultipleCplModule", isMultipleCplModule);
            intent.putExtra("IS_FROM_COURSE", assessmentExtraDetails.isFromCourse());
            intent.putExtra("courseAssociated", assessmentFirebaseClass.isCourseAssociated());
            intent.putExtra("mappedCourseId", assessmentFirebaseClass.getMappedCourseId());
            intent.putExtra("timePenaltyDisabled", assessmentFirebaseClass.isTimePenaltyDisabled());
            intent.putExtra("resumeSameQuestion", assessmentFirebaseClass.isResumeSameQuestion());
            intent.putExtra("showAssessmentResultScore", assessmentFirebaseClass.isShowAssessmentResultScore());
            intent.putExtra("reAttemptAllowed", assessmentExtraDetails.isReAttemptAllowed());
            intent.putExtra("nAttemptCount", assessmentFirebaseClass.getAttemptCount());
            intent.putExtra("nAttemptAllowedToPass", assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
            intent.putExtra("secureSessionOn", assessmentFirebaseClass.isSecureSessionOn());
            intent.putExtra("surveyAssociated", assessmentFirebaseClass.isSurveyAssociated());
            intent.putExtra("isSurveyFromCourse", isSurveyFromCourse);
            intent.putExtra("surveyMandatory", assessmentFirebaseClass.isSurveyMandatory());
            intent.putExtra("mappedSurveyId", assessmentFirebaseClass.getMappedSurveyId());
            intent.putExtra("isMicroCourse", isMicroCourse);
            intent.putExtra("isComingFromCpl", isComingFromCPL);

            long rewardOC = assessmentFirebaseClass.getRewardOc();
            if (rewardOC > 0) {
                long completionDeadline = assessmentFirebaseClass.getCompletionDeadline();
                if (completionDeadline > 0) {
                    long currentTime = System.currentTimeMillis();
                    long penalty = assessmentFirebaseClass.getDefaultPastDeadlineCoinsPenaltyPercentage();
                    Log.d(TAG, "startAssessmentActivity: completionDeadline:" + completionDeadline + " --- currentTime:" + currentTime);
                    if ((completionDeadline < currentTime) && penalty > 0) {
                        double oc = rewardOC * (1 - (penalty / 100.0));
                        Log.d(TAG, "gotoQuestionPage: rewardOc:" + oc);
                        intent.putExtra("rewardOC", (Double.valueOf(oc)).longValue());
                    } else {
                        intent.putExtra("rewardOC", assessmentFirebaseClass.getRewardOc());
                    }
                } else {
                    intent.putExtra("rewardOC", assessmentFirebaseClass.getRewardOc());
                }
            }

            if (assessmentExtraDetails.isEvent()) {
                intent.putExtra("isEventLaunch", true);
                intent.putExtra("eventId", assessmentExtraDetails.getEventId());
            }

            intent.putExtra("nCorrect", assessmentExtraDetails.getnCorrect());
            intent.putExtra("nWrong", assessmentExtraDetails.getnWrong());
            intent.putExtra("isAssessment", true);

            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            if ((assessmentExtraDetails.getCourseId() != null) && (!assessmentExtraDetails.getCourseId().isEmpty())) {
                intent.putExtra("courseId", assessmentExtraDetails.getCourseId());

                if (assessmentExtraDetails.getMappedSurveyId() > 0) {
                    intent.putExtra("mappedSurveyId", assessmentExtraDetails.getMappedSurveyId());
                }
            }

            AssessmentDetailScreen.this.finish();
            startActivity(intent);
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoQuestionPage(AssessmentDetailOther assessmentDetailOther) {
        try {
            Log.d(TAG, "gotoQuestionPage: ");
            if (isTakeAnswer) {
                gotoAssessmentReviewAnswer(assessmentDetailOther);
            } else {
                playResponse = OustAppState.getInstance().getPlayResponse();
                AssessmentFirebaseClass assessmentFirebaseClass = assessmentDetailOther.getAssessmentFirebaseClass();
                AssessmentPlayResponse assessmentPlayResponse = assessmentDetailOther.getAssessmentPlayResponse();
                ActiveGame activeGame = assessmentDetailOther.getActiveGame();
                AssessmentExtraDetails assessmentExtraDetails = assessmentDetailOther.getAssessmentExtraDetails();
                ArrayList<DTOQuestions> questionsArrayList = assessmentDetailOther.getQuestionsArrayList();
                toolbar.setVisibility(View.VISIBLE);

                Gson gson = new GsonBuilder().create();
                Intent intent = new Intent(this, NewAssessmentBaseActivity.class);
                if (OustPreferences.getAppInstallVariable("isLayout4") &&
                        OustPreferences.getAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI)) {
                    OustStaticVariableHandling.getInstance().setQuestionsArrayList(questionsArrayList);
                    if (assessmentFirebaseClass != null && assessmentFirebaseClass.isExamMode() && assessmentFirebaseClass.getDuration() != 0) {
                        intent = new Intent(this, AssessmentExamModeActivity.class);
                        intent.putExtra("examMode", true);
                        intent.putExtra("examDuration", assessmentFirebaseClass.getDuration());
                        intent.putExtra("enrolledTime", assessmentFirebaseClass.getEnrolledTime());
                    } else {
                        intent = new Intent(this, AssessmentQuestionBaseActivity.class);
                    }
                }
                if (assessmentFirebaseClass != null && assessmentFirebaseClass.getQuestionXp() != 0) {
                    intent.putExtra("questionXp", assessmentFirebaseClass.getQuestionXp());
                }
                intent.putExtra("containCertificate", assessmentExtraDetails.isContainCertificate());
                intent.putExtra("isCplModule", assessmentExtraDetails.isCplModule());
                intent.putExtra("currentCplId", currentCplId);
                intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                intent.putExtra("totalTimeOfAssessment", 0);
                intent.putExtra("IS_FROM_COURSE", assessmentExtraDetails.isFromCourse());
                intent.putExtra("ActiveGame", gson.toJson(activeGame));
                assert assessmentFirebaseClass != null;
                intent.putExtra("timePenaltyDisabled", assessmentFirebaseClass.isTimePenaltyDisabled());
                intent.putExtra("isAssessment", true);
                intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());
                intent.putExtra("bannerImage", assessmentFirebaseClass.getBanner());
                intent.putExtra("moduleId", assessmentFirebaseClass.getAsssessemntId());
                intent.putExtra("moduleName", assessmentFirebaseClass.getName());
                intent.putExtra("resumeSameQuestion", assessmentFirebaseClass.isResumeSameQuestion());
                intent.putExtra("courseAssociated", assessmentFirebaseClass.isCourseAssociated());
                intent.putExtra("mappedCourseId", assessmentFirebaseClass.getMappedCourseId());
                intent.putExtra("secureSessionOn", assessmentFirebaseClass.isSecureSessionOn());
                intent.putExtra("surveyAssociated", assessmentFirebaseClass.isSurveyAssociated());
                intent.putExtra("isSurveyFromCourse", isSurveyFromCourse);
                intent.putExtra("surveyMandatory", assessmentFirebaseClass.isSurveyMandatory());
                intent.putExtra("mappedSurveyId", assessmentFirebaseClass.getMappedSurveyId());
                intent.putExtra("isMicroCourse", isMicroCourse);
                intent.putExtra("isComingFromCpl", isComingFromCPL);

                long rewardOC = assessmentFirebaseClass.getRewardOc();
                if (rewardOC > 0) {
                    long completionDeadline = assessmentFirebaseClass.getCompletionDeadline();
                    if (completionDeadline > 0) {
                        long currentTime = System.currentTimeMillis();
                        long penalty = assessmentFirebaseClass.getDefaultPastDeadlineCoinsPenaltyPercentage();
                        Log.d(TAG, "startAssessmentActivity: completion Deadline:" + completionDeadline + " --- currentTime:" + currentTime + " -- penalty:" + penalty);
                        if ((completionDeadline < currentTime) && penalty > 0) {
                            double oc = rewardOC * (1 - (penalty / 100.0));
                            Log.d(TAG, "gotoQuestionPage: rewardOc:" + oc);
                            intent.putExtra("rewardOC", (Double.valueOf(oc)).longValue());
                        } else {
                            intent.putExtra("rewardOC", assessmentFirebaseClass.getRewardOc());
                        }
                    } else {
                        intent.putExtra("rewardOC", assessmentFirebaseClass.getRewardOc());
                    }
                }
                intent.putExtra("showAssessmentResultScore", assessmentFirebaseClass.isShowAssessmentResultScore());
                intent.putExtra("reAttemptAllowed", assessmentExtraDetails.isReAttemptAllowed());
                intent.putExtra("nAttemptCount", assessmentFirebaseClass.getAttemptCount());
                intent.putExtra("nAttemptAllowedToPass", assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
                if (assessmentExtraDetails.isEvent()) {
                    intent.putExtra("isEventLaunch", true);
                    intent.putExtra("eventId", assessmentExtraDetails.getEventId());
                }
                intent.putExtra("nCorrect", assessmentExtraDetails.getnCorrect());
                intent.putExtra("nWrong", assessmentExtraDetails.getnWrong());
                if ((assessmentPlayResponse != null)) {
                    Log.e("SCORE", "assessmentPlayResponse not null");
                    if (!(assessmentPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED))) {
                        intent.putExtra("assessmentResp", gson.toJson(assessmentPlayResponse));
                        Log.e("SCORE", "assessmentPlayResponse not null && state not submitted");
                    }
                    if (assessmentPlayResponse.getScoresList() != null) {
                        Log.e("SCORE", "assessmentPlayResponse not null && ScoreList not null");
                    }
                }
                intent.putExtra("totalCards", playResponse.getqIdList().size());
                if ((assessmentExtraDetails.getCourseId() != null) && (!assessmentExtraDetails.getCourseId().isEmpty())) {
                    intent.putExtra("courseId", assessmentExtraDetails.getCourseId());

                    if (assessmentExtraDetails.getMappedSurveyId() > 0) {
                        intent.putExtra("mappedSurveyId", assessmentExtraDetails.getMappedSurveyId());
                    }
                }
                startActivity(intent);
                AssessmentDetailScreen.this.finish();
            }
            branding_mani_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class AssessmentDetailObserver implements Observer<AssessmentDetailModel> {
        @Override
        public void onChanged(AssessmentDetailModel assessmentDetailModels) {
            try {
                if (circularProgressIndicator != null) {
                    if (circularProgressIndicator.getVisibility() == View.GONE) {
                        showLoader();
                    }
                }
                if (assessmentDetailModels == null) {
                    OustStaticVariableHandling.getInstance().setComingFromAssessment(true);
                    OustStaticVariableHandling.getInstance().setAssessmentError(true);
                    Toast.makeText(AssessmentDetailScreen.this, "" + getResources().getString(R.string.assessment_no_active_error), Toast.LENGTH_SHORT).show();
                    AssessmentDetailScreen.this.finish();
                    return;
                }
                assessmentDetailModel = assessmentDetailModels;
                //Leader board based on org and assessment flag
                boolean isOrgLeaderBoardHide = OustPreferences.getAppInstallVariable("hideAllAssessmentLeaderBoard");
                if (!assessmentDetailModel.isHideLeaderBoard() && !isOrgLeaderBoardHide) {
                    leaderBoardEnable = true;
                    invalidateOptionsMenu();
                }

                //Image - banner
                try {
                    BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.mydesk));
                    if (bd != null) {
                        Log.d(TAG, "onChanged: default image in available");
                        Picasso.get().load(assessmentDetailModel.getImage())
                                .placeholder(bd).error(bd)
                                .into(assessment_image);
                    } else {
                        Log.d(TAG, "onChanged: loading default banner image");
                        Picasso.get().load(assessmentDetailModel.getImage())
                                .into(assessment_image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                String time = "1 " + getResources().getString(R.string.minute);
                if (assessmentDetailModel.getTime() != null) {
                    time = assessmentDetailModel.getTime() + " " + getResources().getString(R.string.minute);
                }
                assessment_duration_text.setText(time.toLowerCase());
                String question = "1 " + getResources().getString(R.string.question_text);
                if (assessmentDetailModel.getQuestions() != null) {
                    try {
                        long questionNum = Long.parseLong(assessmentDetailModel.getQuestions());
                        if (questionNum > 1) {
                            question = questionNum + " " + getResources().getString(R.string.questions_text);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        question = "1 " + getResources().getString(R.string.question_text);
                    }
                }
                assessment_qa_text.setText(question.toLowerCase());
                if (assessmentDetailModel.getCoins() != null && !assessmentDetailModel.getCoins().isEmpty()) {
                    String coins = assessmentDetailModel.getCoins() + " " + getResources().getString(R.string.coins_text);
                    assessment_coins_text.setText(coins);
                } else {
                    assessment_coins_lay.setVisibility(View.GONE);
                }


                if (assessmentDetailModel.getDistributionTime() != null) {
                    assessment_distribution_date.setText(assessmentDetailModel.getDistributionTime());
                } else {
                    assessment_distribution_date.setVisibility(View.GONE);
                }

                //title with html support
                if (assessmentDetailModel.getTitle() != null && !assessmentDetailModel.getTitle().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        assessment_title_full.setText(Html.fromHtml(assessmentDetailModel.getTitle(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        assessment_title_full.setText(Html.fromHtml(assessmentDetailModel.getTitle()));
                    }
                }

                if (assessmentDetailModel.getEndTime() != null) {
                    String deadLineText = getResources().getString(R.string.deadline);
                    String assessmentDeadLine = deadLineText + " " + assessmentDetailModel.getEndTime();
                    Spannable spanText = new SpannableString(assessmentDeadLine);
                    spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assessment_dead_line.setText(spanText);
                } else {
                    assessment_dead_line.setVisibility(View.GONE);
                }

                if (assessmentDetailModel.isReattemptAllowed()) {
                    try {
                        long attemptLeft = assessmentDetailModel.getNoOfAttemptAllowed() - OustAppState.getInstance().getAssessmentFirebaseClass().getAttemptCount();
                        if (attemptLeft > 0) {
                            String reAttemptText = getResources().getString(R.string.attempt_allowed);
                            if (attemptLeft > 1) {
                                reAttemptText = getResources().getString(R.string.attempts_allowed);
                            }
                            String assessmentReAttempt = attemptLeft + " " + reAttemptText;
                            assessment_attempt_allowed.setVisibility(View.VISIBLE);
                            assessment_attempt_allowed.setText(assessmentReAttempt);
                        } else {
                            assessment_attempt_allowed.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else {
                    assessment_attempt_allowed.setVisibility(View.INVISIBLE);
                }

                //description with html support
                if (assessmentDetailModel.getDescription() != null && !assessmentDetailModel.getDescription().isEmpty()) {
                    if (assessmentDetailModel.getDescription().contains("<li>") || assessmentDetailModel.getDescription().contains("</li>") ||
                            assessmentDetailModel.getDescription().contains("<ol>") || assessmentDetailModel.getDescription().contains("</ol>") ||
                            assessmentDetailModel.getDescription().contains("<p>") || assessmentDetailModels.getDescription().contains("</p>")) {
                        assessment_description_webView.setVisibility(View.VISIBLE);
                        assessment_description.setVisibility(View.GONE);
                        assessment_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(assessmentDetailModel.getDescription());
                        final WebSettings webSettings = assessment_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        assessment_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            assessment_description.setText(Html.fromHtml(assessmentDetailModel.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            assessment_description.setText(Html.fromHtml(assessmentDetailModel.getDescription()));
                        }
                    }
                }

                checkForDeviceNetTime();

                if (assessmentDetailModel.getStartAssessment() <= systemTime) {
                    if (assessmentDetailModel.isCompleted()) {
                        assessment_info.setVisibility(View.GONE);
                        assessment_coins_lay.setVisibility(View.GONE);
                        if (assessmentDetailModel.isShowAssessmentResultScore()) {
                            show_result_score.setVisibility(View.VISIBLE);
                            completion_lay.setVisibility(View.VISIBLE);


                            String yourScoreText = assessmentDetailModel.getUserScore();
                            if (yourScoreText != null && !yourScoreText.isEmpty()) {
                                if (yourScoreText.contains("/")) {
                                    String[] spilt = yourScoreText.split("/");
                                    Spannable yourScoreSpan = new SpannableString(yourScoreText);
                                    yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourScoreText.length(), 0);
                                    yourScoreSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), spilt[0].length(), yourScoreText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    user_score_text.setText(yourScoreSpan);
                                }
                            }

                            String yourPercentage = getResources().getString(R.string.your_percentage_text);
                            Spannable spanText = new SpannableString(yourPercentage);
                            spanText.setSpan(new ForegroundColorSpan(Color.RED), (yourPercentage.length() - 1), yourPercentage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            your_percentage_text.setText(spanText);

                            user_percentage_text.setText(assessmentDetailModel.getUserPercentage());


                            user_correct_answer_count.setText(String.valueOf(assessmentDetailModel.getCorrectAnswer()));
                            user_correct_answer_progress.setProgress(assessmentDetailModel.getCorrectAnswerProgress());
                            user_wrong_answer_count.setText(String.valueOf(assessmentDetailModel.getWrongAnswer()));
                            user_wrong_answer_progress.setProgress(assessmentDetailModel.getWrongAnswerProgress());


                        } else {
                            show_result_score.setVisibility(View.GONE);
                            completion_lay.setVisibility(View.GONE);
                        }

                    } else {

                        completion_lay.setVisibility(View.GONE);
                        // non_completion_lay.setVisibility(View.VISIBLE);

                        if (assessmentDetailModel.isInProgress() && assessmentDetailModel.getProgress() > 0) {
                            progress_lay.setVisibility(View.VISIBLE);
                            String completionPercentage = assessmentDetailModel.getProgress() + "% " + getResources().getString(R.string.done_text);
                            completion_percentage_done.setText(completionPercentage);
                            completion_percentage_progress.setProgress(assessmentDetailModel.getProgress());
                        }

                        if (assessmentDetailModel.getShowPastDeadlineModulesOnLandingPage() != null && assessmentDetailModel.getShowPastDeadlineModulesOnLandingPage().equalsIgnoreCase("false")) {
                            if (assessmentDetailModel.getDeadLine() != 0 && System.currentTimeMillis() > assessmentDetailModel.getDeadLine()
                                    && (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() == null)) {
                                OustSdkTools.showToast(getResources().getString(R.string.assessment_no_active_error));
                                AssessmentDetailScreen.this.finish();
                            }

                        }
                    }
                } else {
                    assessment_action_button.setVisibility(View.GONE);
                    assessment_start_timer.setVisibility(View.VISIBLE);
                    completion_lay.setVisibility(View.GONE);
                    remainingTimeInSec = (assessmentDetailModel.getStartAssessment() - systemTime) / 1000;
                    setTimerText();
                    startTimer();
                }


                if (assessmentDetailModel.getStatus() != null && !assessmentDetailModel.getStatus().isEmpty()) {
                    String status_text = "";
                    boolean recurringAssessment = false;
                    if (assessmentDetailModel.getStatus().equalsIgnoreCase("Resume")) {
                        status_text = getResources().getString(R.string.resume);
                    } else if (assessmentDetailModel.getStatus().equalsIgnoreCase("Over")) {
                        isMediaDownloaded = true;
                        isMediaStartedDownload = false;
                        recurringAssessment = assessmentDetailModel.isRecurring();
                        status_text = getResources().getString(R.string.over_text);
                    } else if (assessmentDetailModel.getStatus().equalsIgnoreCase("Answers")) {
                        status_text = getResources().getString(R.string.answer);
                    } else if (assessmentDetailModel.getStatus().equalsIgnoreCase("Submit")) {
                        status_text = getResources().getString(R.string.submit);
                    }
                    assessment_status_text.setText("");
                    if (!recurringAssessment) {
                        assessment_status_text.setText(status_text);
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    } else {
                        assessment_action_button.setEnabled(true);
                        assessment_status_text.setText("");
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                    }


                    if (assessmentDetailModel.isAnswer()) {
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.list_ansr, 0, 0, 0);
                        isTakeAnswer = true;
                        assessment_attempt_allowed.setVisibility(View.INVISIBLE);
                        if (assessmentDetailModel.getActiveGame() != null) {
                            activeGame = assessmentDetailModel.getActiveGame();
                        }
                        playResponse = OustAppState.getInstance().getPlayResponse();
                        if (assessmentDetailModel.getAssessmentPlayResponse() != null) {
                            assessmentPlayResponse = assessmentDetailModel.getAssessmentPlayResponse();
                        }

                    }

                    if (!assessmentDetailModel.isButtonEnabled() && !assessmentDetailModel.isRecurring()) {
                        isButtonEnabled = false;
                        assessment_attempt_allowed.setVisibility(View.INVISIBLE);
                    }
                } else {
                    assessment_status_text.setText("");
                    assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                }

                if (OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                    assessment_attach_lay.setVisibility(View.VISIBLE);
                    String attachment_text = getResources().getString(R.string.attachment) + " : " + getResources().getString(R.string.course_text);
                    assessment_attach_text.setText(attachment_text);
                }

                if (assessmentDetailModel.isAutoStartAssessment()) {
                    Log.d(TAG, "onChanged: auto start");
                    isMediaStartedDownload = true;
                    viewModel.layout_StartButton();
                }
                hideLoader();

                if (OustPreferences.getTimeForNotification("cplId_assessment") > 0 && !isComingFromCPL &&
                        ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() == null))) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.attach_module_cpl));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconColors() {
        Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        assessment_action_button.setBackground(OustSdkTools.drawableColor(actionDrawable));
    }


    private void checkForDeviceNetTime() {
        try {
            int status = android.provider.Settings.System.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
            if (status == 1) {
                systemTime = System.currentTimeMillis();

            } else {
                showPopup(getResources().getString(R.string.system_time_error));
                AssessmentDetailScreen.this.finish();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPopup(String content) {
        try {
            Popup popup = new Popup();
            OustPopupButton oustPopupButton = new OustPopupButton();
            oustPopupButton.setBtnText(getResources().getString(R.string.ok));
            List<OustPopupButton> btnList = new ArrayList<>();
            btnList.add(oustPopupButton);
            popup.setButtons(btnList);
            popup.setContent(content);
            popup.setType(OustPopupType.REDIRECT_SETTING_PAGE);
            popup.setCategory(OustPopupCategory.REDIRECT);
            oustPopupButton.setBtnText(getResources().getString(R.string.go_to_setting));
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            Intent intent = new Intent(this, PopupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startTimer() {
        try {
            CounterClass timer = new CounterClass(remainingTimeInSec * 1000, getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            assessment_start_timer.setVisibility(View.GONE);
            assessment_action_button.setVisibility(View.VISIBLE);
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
//            remainingTimeInSec--;
            remainingTimeInSec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
            setTimerText();
        }
    }

    private void setTimerText() {
        if (remainingTimeInSec > 86400) {
            long assessmentRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
            long assessmentRemainingTimeMins = ((remainingTimeInSec % 86400) % 3600) / 60;
            String hms = String.format(Locale.getDefault(), "%02d : %02d : %02d", remainingTimeInSec / 86400, assessmentRemainingTimeHours, assessmentRemainingTimeMins);
            assessment_timer_text.setText(hms);
        } else {
            long assessmentRemainingTimeHours = (remainingTimeInSec % 86400) / 3600;
            long assessmentRemainingTimeMins = ((remainingTimeInSec) % 3600) / 60;
            long assessmentRemainingTimeSec = ((remainingTimeInSec) % 3600) % 60;
            String hms = String.format(Locale.getDefault(), "%02d : %02d : %02d", assessmentRemainingTimeHours, assessmentRemainingTimeMins, assessmentRemainingTimeSec);
            assessment_timer_text.setText(hms);
        }
    }

    private void gotoAssessmentReviewAnswer(AssessmentDetailOther assessmentDetailOther) {
        try {
            Gson gson = new GsonBuilder().create();
            AssessmentFirebaseClass assessmentFirebaseClass = assessmentDetailOther.getAssessmentFirebaseClass();
            Intent intent = new Intent(this, AssessmentQuestionReviewBaseActivity.class);
            ArrayList<DTOQuestions> questionsArrayList = assessmentDetailOther.getQuestionsArrayList();
            if (OustPreferences.getAppInstallVariable("isLayout4") && OustPreferences.getAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI)) {
                AssessmentPlayResponse assessmentPlayResponse = assessmentDetailOther.getAssessmentPlayResponse();
                intent = new Intent(this, AssessmentQuestionBaseActivity.class);
                OustStaticVariableHandling.getInstance().setQuestionsArrayList(questionsArrayList);
                if (assessmentFirebaseClass.isExamMode()) {
                    intent = new Intent(this, AssessmentExamModeActivity.class);
                }

                intent.putExtra("isAssessment", true);
                intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());
                intent.putExtra("moduleName", assessmentFirebaseClass.getName());
                intent.putExtra("examMode", assessmentFirebaseClass.isExamMode());
                intent.putExtra("isReviewMode", true);
                intent.putExtra("currentCplId", currentCplId);
                intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                if ((assessmentPlayResponse != null)) {
                    Log.e("SCORE", "assessmentPlayResponse not null");
                    if ((assessmentPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED))) {
                        intent.putExtra("assessmentResp", gson.toJson(assessmentPlayResponse));
                        Log.e("SCORE", "assessmentPlayResponse not null && state not submitted");
                    }
                }
            }
            intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("bgImage", assessmentFirebaseClass.getBgImg());
            intent.putExtra("moduleName", assessmentFirebaseClass.getName());
            intent.putExtra("moduleId", assessmentFirebaseClass.getAsssessemntId());
            intent.putExtra("isAssessmentReviewMode", true);
            intent.putExtra("isSurveyFromCourse", isSurveyFromCourse);
            intent.putExtra("comingFromAssessmentLanding", true);
            SubmitRequest submitRequest = getSubmitRequest();
            intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
            intent.putExtra("isComingFromCpl", isComingFromCPL);
            if (submitRequest != null) {
                this.startActivity(intent);
                branding_mani_layout.setVisibility(View.VISIBLE);
                AssessmentDetailScreen.this.finish();

            } else {
                OustSdkTools.showToast("DATA NOT FOUND!!!");
                branding_mani_layout.setVisibility(View.GONE);
                card_view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private SubmitRequest getSubmitRequest() {
        try {
            if (assessmentPlayResponse != null) {
                SubmitRequest submitRequest = new SubmitRequest();
                submitRequest.setWinner(assessmentPlayResponse.getWinner());
                submitRequest.setGameid(assessmentPlayResponse.getGameId());
                submitRequest.setTotalscore(assessmentPlayResponse.getChallengerFinalScore());
                submitRequest.setScores(assessmentPlayResponse.getScoresList());
                submitRequest.setEndTime(assessmentPlayResponse.getEndTime());
                submitRequest.setStartTime(assessmentPlayResponse.getStartTime());
                submitRequest.setChallengerid(assessmentPlayResponse.getChallengerid());
                submitRequest.setOpponentid(assessmentPlayResponse.getOpponentid());
                submitRequest.setAssessmentId(("" + assessmentDetailModel.getAssessmentId()));
                submitRequest.setStudentid(OustAppState.getInstance().getActiveUser().getStudentid());
                return submitRequest;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newlandingmenu, menu);

        MenuItem action_leaderBoard = menu.findItem(R.id.action_leaderBoard);
        Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
        action_leaderBoard.setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color));
        action_leaderBoard.setVisible(isLeaderBoardEnable());

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            backScreen();
            return true;
        } else if (itemId == R.id.action_leaderBoard) {
            if (!OustSdkTools.checkInternetStatus()) {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            } else if (!isMediaStartedDownload) {
                Intent leaderBoardIntent = new Intent(AssessmentDetailScreen.this, CommonLeaderBoardActivity.class);
                leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle containerBundle = new Bundle();
                containerBundle.putString("containerType", "AssessmentLeaderBoard");
                containerBundle.putLong("containerContentId", OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId());
                if (assessmentDetailModel.getTitle() != null && !assessmentDetailModel.getTitle().isEmpty()) {
                    containerBundle.putString("contentName", assessmentDetailModel.getTitle());
                }
                leaderBoardIntent.putExtras(containerBundle);
                startActivity(leaderBoardIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isLeaderBoardEnable() {

        return leaderBoardEnable;

    }

    void backScreen() {
        if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isFromCourse()) {
            UserEventAssessmentData userEventAssessmentData = viewModel.assessmentDetailRepo.userEventAssessmentData;
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) && (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                userEventAssessmentData.setUserProgress("COMPLETED");
            } else {
                OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                userEventAssessmentData.setUserProgress("INPROGRESS");
            }
            userEventAssessmentData.setEventId(0);
            OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);

        } else {
            if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isEvent() && OustStaticVariableHandling.getInstance().getOustApiListener() != null) {
                UserEventAssessmentData userEventAssessmentData = viewModel.assessmentDetailRepo.userEventAssessmentData;
                if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                    userEventAssessmentData.setUserProgress("COMPLETED");
                } else {
                    userEventAssessmentData.setUserProgress("INPROGRESS");
                }

                if (viewModel.assessmentDetailRepo.assessmentExtraDetails.isCplModule()) {
                    UserEventCplData userEventCplData = new UserEventCplData();
                    userEventCplData.setCurrentModuleType("ASSESSMENT");

                    userEventCplData.setCurrentModuleId(userEventAssessmentData.getAssessmentId());
                    userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                    final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                    final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                    userEventCplData.setnTotalModules(totalModules);

                    if (OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) {
                        userEventCplData.setCurrentModuleProgress("COMPLETED");
                        if (completedModules >= totalModules) {
                            userEventCplData.setnModulesCompleted(totalModules);
                            userEventCplData.setUserProgress("COMPLETED");
                        } else {
                            userEventCplData.setnModulesCompleted(completedModules + 1);
                            userEventCplData.setUserProgress("INPROGRESS");
                        }
                    } else {
                        userEventCplData.setCurrentModuleProgress("INPROGRESS");
                        userEventCplData.setnModulesCompleted(completedModules);
                        userEventCplData.setUserProgress("INPROGRESS");
                    }

                    userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                    OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                } else {
                    OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                }
            }
        }
        AssessmentDetailScreen.this.finish();
    }

    public void nextWorkFail() {
        try {
            branding_mani_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
