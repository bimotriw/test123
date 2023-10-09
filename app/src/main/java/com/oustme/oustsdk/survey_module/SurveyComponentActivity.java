package com.oustme.oustsdk.survey_module;

import static com.oustme.oustsdk.feed_ui.tools.OustSdkTools.drawableColor;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.course_ui.IntroCardActivity;
import com.oustme.oustsdk.course_ui.IntroScormCardActivity;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.survey.SurveyQuestionBaseActivity;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.survey_module.model.SurveyComponentModule;
import com.oustme.oustsdk.survey_module.model.SurveyModule;
import com.oustme.oustsdk.survey_ui.SurveyPopUpActivity;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SurveyComponentActivity extends BaseActivity implements LearningModuleInterface {


    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    CardView card_component;
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
    WebView survey_desc_webView;
    FrameLayout survey_action_button;
    TextView survey_status_text;
    ProgressBar survey_loader;
    FrameLayout card_layout;
    ImageView img_coin;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    private int color;
    private int bgColor;
    boolean updateComment;
    SurveyComponentViewModel surveyComponentViewModel;
    SurveyComponentModule surveyComponentModule;
    boolean btnClick = false;
    private boolean isMultipleCplEnable = false;
    private boolean introCardViewed = false;
    private long currentCplId;
    private boolean isActivityDestroyed;
    boolean isMicroCourse;
    DTOUserFeedDetails.FeedDetails feed;
    String feedType;
    long timeStamp;
    private HashMap<String, String> landingPageModuleMap;
    long assessmentId;
    long courseId;
    //private FeedClickListener feedClickListener;
    boolean isFeedComment, isFeedAttach, isFeedLikeable, isFeedChange;
    ActiveUser activeUser;

    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_survey_component;
    }

    @Override
    protected void initView() {

        if (OustSdkApplication.getContext() == null) {
            OustSdkApplication.setmContext(getApplicationContext());
        }
        OustSdkTools.setLocale(SurveyComponentActivity.this);
//        OustGATools.getInstance().reportPageViewToGoogle(SurveyComponentActivity.this, "Oust Survey Component Page");

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

        card_component = findViewById(R.id.card_component);
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
        survey_desc_webView = findViewById(R.id.survey_description_webView);
        survey_action_button = findViewById(R.id.survey_action_button);
        survey_status_text = findViewById(R.id.survey_status_text);
        survey_loader = findViewById(R.id.survey_loader);
        card_layout = findViewById(R.id.card_layout);

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

        if (activeUser == null) {
            activeUser = OustAppState.getInstance().getActiveUser();
        }

        setToolbarAndIconColor();
        showLoader();


    }

    @Override
    protected void initData() {
        fetchLayoutData(false);
    }

    @Override
    protected void initListener() {

        try {
            back_button.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //handle network error
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
    private void setToolbarAndIconColor() {

        setSupportActionBar(toolbar);

        Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        survey_action_button.setBackground(OustResourceUtils.setDefaultDrawableColor(actionDrawable));

    }

    private void fetchLayoutData(boolean isResume) {
        Bundle feedBundle = getIntent().getExtras();
        if (feedBundle != null) {
            feed = feedBundle.getParcelable("Feed");
            isFeedComment = feedBundle.getBoolean("FeedComment");
            isFeedAttach = feedBundle.getBoolean("FeedAttach");
//            isFeedLikeable = feedBundle.getBoolean("isFeedLikeable");
            feedType = feedBundle.getString("feedType");
            timeStamp = feedBundle.getLong("timeStamp", 0);
            assessmentId = feedBundle.getLong("AssessmentId");
            courseId = feedBundle.getLong("CourseId");
            landingPageModuleMap = (HashMap<String, String>) feedBundle.getSerializable("deskDataMap");
        }
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isMicroCourse = bundle.getBoolean("isMicroCourse", false);
                currentCplId = bundle.getLong("cplId");
                isMultipleCplEnable = bundle.getBoolean("isMultipleCplModule", false);
            }
            surveyComponentViewModel = new ViewModelProvider(this).get(SurveyComponentViewModel.class);
            surveyComponentViewModel.init(bundle, isResume);
            surveyComponentViewModel.getBaseComponentModuleMutableLiveData().observe(this, surveyComponentModule -> {
                if (surveyComponentModule == null)
                    return;

                this.surveyComponentModule = surveyComponentModule;
                setData(surveyComponentModule);

            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(SurveyComponentModule surveyComponentModule) {

        try {
            if (surveyComponentModule != null) {
                setResumeProgress(surveyComponentModule.getProgress(), surveyComponentModule.getProgressDone() + getResources().getString(R.string.done_text));

                if (btnClick) {

                    if (surveyComponentModule.getDownloadProgress() != null && !surveyComponentModule.getDownloadProgress().isEmpty()) {
                        branding_percentage.setVisibility(View.VISIBLE);
                        branding_percentage.setText(surveyComponentModule.getDownloadProgress());
                    }

                    if (surveyComponentModule.isFetchCompleted()) {
                        btnClick = false;
                        hideFetchingLoader();
                        handleQuestions(surveyComponentModule.getPlayResponse(), surveyComponentModule.getQuestionIndex());
                    }

                } else {

                    if (surveyComponentModule.getSurveyPlayResponse() != null) {
                        if (surveyComponentModule.getSurveyPlayResponse().getAssessmentState() != null &&
                                surveyComponentModule.getSurveyPlayResponse().getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED)) {
                            if (surveyComponentModule.getAssociatedAssessmentId() > 0) {
                                OustStaticVariableHandling.getInstance().setSurveyCompleted(true);
                            }
                            handleSurveyComplete(getResources().getString(R.string.completed_survey_text), false);
                            hideLoader();
                        } else {
                            if (!introCardViewed) {
                                introCardViewed = true;
                                if (surveyComponentModule.isIntroCard()) {
                                    if (surveyComponentModule.getCardClass() != null) {
                                        Log.e(TAG, "setData: if con");
                                        handleCardTransaction(surveyComponentModule.getCardClass());
                                    }
                                }
                            }

                            if (surveyComponentModule.getUserFeed() != null && surveyComponentModule.isFeedComment()) {
                                hideLoader();
                                handleFeedComment(surveyComponentModule.getUserFeed());
                            }
                        }
                    } else {
                        if (surveyComponentModule.isIntroCard()) {
                            if (surveyComponentModule.getCardClass() != null) {
                                Log.e(TAG, "setData: else con");
                                handleCardTransaction(surveyComponentModule.getCardClass());
                            }
                        }

                        if (surveyComponentModule.getUserFeed() != null && surveyComponentModule.isFeedComment()) {
                            handleFeedComment(surveyComponentModule.getUserFeed());
                        }
                    }

                    if (surveyComponentModule.getUserFeed() != null && surveyComponentModule.getUserFeed().getTimeStamp() != 0) {
                        String feedDate = OustSdkTools.milliToDate("" + surveyComponentModule.getUserFeed().getTimeStamp());
                        if (!feedDate.isEmpty()) {
                            survey_feed_date.setVisibility(View.VISIBLE);
                            survey_feed_date.setText(feedDate);

                        }
                    }
                    SurveyModule surveyModule = surveyComponentModule.getSurveyModule();
                    if (surveyModule != null) {
                        //handle hide loader

                        if (surveyModule.getBanner() != null && !surveyModule.getBanner().trim().isEmpty()) {
                            //handle banner image for survey
                            Picasso.get().load(surveyModule.getBanner()).into(survey_image);
                        }

                        if (surveyModule.getNumQuestions() != 0) {
                            survey_qa_lay.setVisibility(View.VISIBLE);
                            String qa_text = surveyModule.getNumQuestions() + " " + getResources().getString(R.string.question_text).toLowerCase();
                            if (surveyModule.getNumQuestions() > 1) {
                                qa_text = surveyModule.getNumQuestions() + " " + getResources().getString(R.string.questions_text).toLowerCase();
                            }
                            survey_qa_text.setText(qa_text);
                        }

                        if (surveyModule.getRewardOC() != 0) {
                            survey_coins_lay.setVisibility(View.VISIBLE);
                            String coins_text = surveyModule.getRewardOC() + " " + getResources().getString(R.string.coins_text).toLowerCase();
                            survey_coins_text.setText(coins_text);
                        }

                        if (surveyModule.getName() != null &&
                                !surveyModule.getName().isEmpty()) {
                            survey_title_full.setText(surveyModule.getName());
                        }

                        if (surveyModule.getContentDuration() != 0 && surveyModule.getContentDuration() > 60) {
                            String totalTime = TimeUnit.SECONDS.toMinutes(surveyModule.getContentDuration()) + " minutes";
                            survey_duration_text.setText(totalTime);
                        } else {
                            String durationText = "1 " + OustSdkApplication.getContext().getResources().getString(R.string.minute);
                            survey_duration_text.setText(durationText);
                        }

                        if (surveyModule.getContentCompletionDeadline() != 0) {
                            Date date = new Date(surveyModule.getContentCompletionDeadline());
                            String surveyDeadLine = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);

                            if (!surveyDeadLine.isEmpty()) {
                                survey_feed_dead_line.setVisibility(View.VISIBLE);
                                String deadLineText = getResources().getString(R.string.deadline);
                                surveyDeadLine = deadLineText + " " + surveyDeadLine.toUpperCase();
                                Spannable spanText = new SpannableString(surveyDeadLine);
                                spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                survey_feed_dead_line.setText(spanText);

                            }
                        } else if (surveyModule.getCompletionDeadline() != null && !surveyModule.getCompletionDeadline().isEmpty()) {
                            Date date = new Date(Long.parseLong(surveyModule.getCompletionDeadline()));
                            String surveyDeadLine = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);

                            if (!surveyDeadLine.isEmpty()) {
                                survey_feed_dead_line.setVisibility(View.VISIBLE);
                                String deadLineText = getResources().getString(R.string.deadline);
                                surveyDeadLine = deadLineText + " " + surveyDeadLine.toUpperCase();
                                Spannable spanText = new SpannableString(surveyDeadLine);
                                spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                survey_feed_dead_line.setText(spanText);

                            }
                        }

                        if (surveyModule.getDescription() != null && !surveyModule.getDescription().trim().isEmpty()) {
                            //handle survey desc
                            if (surveyModule.getDescription().contains("<li>") || surveyModule.getDescription().contains("</li>") ||
                                    surveyModule.getDescription().contains("<ol>") || surveyModule.getDescription().contains("</ol>") ||
                                    surveyModule.getDescription().contains("<p>") || surveyModule.getDescription().contains("</p>")) {
                                survey_desc_webView.setVisibility(View.VISIBLE);
                                survey_description.setVisibility(View.GONE);
                                survey_desc_webView.setBackgroundColor(Color.TRANSPARENT);
                                String text = OustSdkTools.getDescriptionHtmlFormat(surveyModule.getDescription());
                                final WebSettings webSettings = survey_desc_webView.getSettings();
                                // Set the font size (in sp).
                                webSettings.setDefaultFontSize(18);
                                survey_desc_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                            } else {
                                survey_description.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    survey_description.setText(Html.fromHtml(surveyModule.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    survey_description.setText(Html.fromHtml(surveyModule.getDescription()));
                                }
                            }
                        }
                    }
                    setBtnStatus();
                    btnAction();
                    if (!surveyComponentModule.isIntroCard()) {
                        hideLoader();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLoader() {
        branding_mani_layout.setVisibility(View.VISIBLE);
//        survey_loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        branding_mani_layout.setVisibility(View.GONE);
//        survey_loader.setVisibility(View.GONE);
    }

    private void handleCardTransaction(DTOCourseCard cardData) {
        try {
            hideLoader();
            if (cardData != null) {
                Log.e(TAG, "handleCardTransaction: cardData--> " + cardData.getCardType());
                if (cardData.getCardType() != null && !cardData.getCardType().isEmpty()) {
                    Intent intent;
                    if (cardData.getCardType().equalsIgnoreCase("SCORM")) {
                        intent = new Intent(SurveyComponentActivity.this, IntroScormCardActivity.class);
                    } else {
                        intent = new Intent(SurveyComponentActivity.this, IntroCardActivity.class);
                    }
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(cardData);
                    OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SurveyComponentActivity.this, IntroCardActivity.class);
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(cardData);
                    OustStaticVariableHandling.getInstance().setCourseDataStr(courseDataStr);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleFeedComment(DTOUserFeedDetails.FeedDetails feedDetails) {

        updateComment = true;
        Dialog dialog = new Dialog(SurveyComponentActivity.this, R.style.DialogTheme);
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
            final String message = "/userFeedComments/" + "feed" + feedDetails.getFeedId();
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
                                        alertCommentData.setUserKey(OustSdkTools.convertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }

                            String totalComments = "";

                            if (commentsList.size() != 0) {
                                comment_list_rv.setVisibility(View.VISIBLE);
                                no_comments.setVisibility(View.GONE);
                                Collections.sort(commentsList, AlertCommentData.commentSorter);

                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(SurveyComponentActivity.this, commentsList, null);
                                comment_list_rv.setItemAnimator(new DefaultItemAnimator());
                                comment_list_rv.setAdapter(feedCommentAdapter);
                                if (commentsList.size() > 1) {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comments_text);
                                } else {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comment_text);
                                }


                                if (!updateComment) {
                                    feedDetails.setNumComments(commentsList.size());
                                    feed.setNumComments(commentsList.size());
                                    feedDetails.setCommented(true);

                                    Intent intent = new Intent(SurveyComponentActivity.this, FeedUpdatingServices.class);
                                    intent.putExtra("FeedId", feedDetails.getFeedId());
                                    intent.putExtra("FeedCommentSize", commentsList.size());
                                    startService(intent);
                                }
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
                /*if (surveyComponentViewModel != null) {
                    surveyComponentViewModel.handleFeedComment(alertCommentData);
                }*/
                isFeedChange = true;
                sendCommentToFirebase(alertCommentData, "" + feedDetails.getFeedId());
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
//        updateFeedViewed(feed);
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

    @Override
    public void gotoNextScreen() {
        //needed

    }

    @Override
    public void gotoPreviousScreen() {
        //needed

    }

    @Override
    public void changeOrientationPortrait() {
        //can use

    }

    @Override
    public void changeOrientationLandscape() {
        //can use

    }

    @Override
    public void changeOrientationUnSpecific() {
        //can use

    }

    @Override
    public void endActivity() {
        // survey?

    }

    @Override
    public void restartActivity() {
        // survey?
        fetchLayoutData(true);
    }

    @Override
    public void downloadComplete(List<DTOCourseCard> courseCardClassList, boolean b) {

    }

    @Override
    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        //needed

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

    @Override
    public void onBackPressed() {
        try {
            btnClick = false;
            if (isMicroCourse) {
                try {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            if (feed != null) {
                OustStaticVariableHandling.getInstance().setResult_code(1444);
                OustStaticVariableHandling.getInstance().setFeedId(feed.getFeedId());
                OustStaticVariableHandling.getInstance().setFeedChanged(isFeedChange);
                OustStaticVariableHandling.getInstance().setLikeClicked(feed.isLiked());
                OustStaticVariableHandling.getInstance().setNumOfComments(feed.getNumComments());
                OustStaticVariableHandling.getInstance().setNumOfLikes(feed.getNumLikes());
                OustStaticVariableHandling.getInstance().setNumOfShares(feed.getNumShares());
                Intent data = new Intent();
                data.putExtra("FeedPosition", feed.getFeedId());
                data.putExtra("FeedComment", feed.getNumComments());
                data.putExtra("isFeedChange", isFeedChange);
                data.putExtra("isLikeClicked", feed.isLiked());
                data.putExtra("isFeedShareCount", feed.getNumShares());
                data.putExtra("isFeedLikeCount", feed.getNumLikes());
                data.putExtra("isClicked", true);
                data.putExtra("FeedRemove", false);
                setResult(1444, data);
                finish();
            } else if (card_layout.getVisibility() == View.VISIBLE) {
                card_layout.setVisibility(View.GONE);
                removeFragment();
            } else {
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error on click back pressed");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeFragment() {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        Fragment f = getSupportFragmentManager().findFragmentByTag("frag");
        if (f != null)
            transaction.remove(f);
        transaction.commit();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
    }

    private void setBtnStatus() {
        try {

            survey_action_button.setVisibility(View.VISIBLE);
            survey_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void btnAction() {
        survey_action_button.setOnClickListener(view -> {
            // checkAssessmentState();
            if (surveyComponentViewModel != null) {
                showFetchingLoader();
                btnClick = true;
                surveyComponentViewModel.checkSurveyState();
            } else {
                Toast.makeText(SurveyComponentActivity.this, "Sorry! No Data available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResumeProgress(int progress, String progressDone) {

        if (progress > 0) {
            progress_lay.setVisibility(View.VISIBLE);
            completion_percentage_progress.setProgress(progress);
            completion_percentage_done.setText(progressDone);
        } else {
            progress_lay.setVisibility(View.GONE);
        }
    }


    private void showFetchingLoader() {
        survey_action_button.setClickable(false);
        back_button.setClickable(false);
        branding_mani_layout.setVisibility(View.VISIBLE);
        branding_percentage.setVisibility(View.VISIBLE);
        branding_percentage.setText("0%");
    }

    private void hideFetchingLoader() {
        branding_mani_layout.setVisibility(View.GONE);
        survey_action_button.setClickable(true);
        back_button.setClickable(true);
    }

    private void handleQuestions(PlayResponse playResponse, int questionIndex) {
        try {
            if (!isActivityDestroyed) {
                OustAppState.getInstance().setAllQuestionsAttempted(false);
                OustAppState.getInstance().setSurveySubmitted(false);
                OustAppState.getInstance().setSurveyExit(false);
                Intent questionIntent = new Intent(SurveyComponentActivity.this, SurveyQuestionBaseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bgImage", surveyComponentModule.getSurveyModule().getBgImg());
                bundle.putString("moduleName", surveyComponentModule.getSurveyModule().getName());
                bundle.putLong("moduleId", surveyComponentModule.getSurveyModule().getAssessmentId());
                bundle.putBoolean("showNavigationArrow", surveyComponentModule.getSurveyModule().isShowNavigationArrows());
                bundle.putBoolean("isSurvey", true);
                bundle.putString("courseId", String.valueOf(surveyComponentModule.getCourseId()));
                if (surveyComponentModule.getCourseId() > 0) {
                    bundle.putLong("mappedAssessmentId", surveyComponentModule.getMappedAssessmentId());
                }

                bundle.putLong("exitOC", surveyComponentModule.getSurveyModule().getExitOC());
                bundle.putLong("rewardOC", surveyComponentModule.getSurveyModule().getRewardOC());
                bundle.putString("activeGameId", surveyComponentModule.getActiveGameId());
                bundle.putString("startDateTime", surveyComponentModule.getStartDateTime());
                bundle.putBoolean("isMultipleCplModule", isMultipleCplEnable);
                bundle.putLong("currentCplId", currentCplId);

                bundle.putLong("currentCplId", surveyComponentModule.getCplId());
                bundle.putLong("associatedAssessmentId", surveyComponentModule.getAssociatedAssessmentId());

                if (surveyComponentModule.getSurveyPlayResponse() != null) {
                    bundle.putString("userPlayResponse", new Gson().toJson(surveyComponentModule.getSurveyPlayResponse()));
                }
                bundle.putInt("questionIndex", questionIndex);
                bundle.putInt("totalCards", playResponse.getqIdList().size());
                bundle.putSerializable("playResponse", playResponse);
                questionIntent.putExtras(bundle);
                startActivity(questionIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Toast.makeText(context, "Sorry. Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSurveyComplete(String content, boolean isExitSurvey) {
        try {
            if (surveyComponentViewModel != null) {
                surveyComponentViewModel.handleSurveyCompletePopUp(content, isExitSurvey);
                Intent data = new Intent();
                if (surveyComponentModule != null) {

                    long feedID = 0;
                    if (surveyComponentModule.getFeed() != null) {
                        data.putExtra("FeedRemove", true);
                        feedID = surveyComponentModule.getFeed().getFeedId();
                        data.putExtra("FeedPosition", surveyComponentModule.getFeed().getFeedId());
                        data.putExtra("isFeedChange", true);
                    }

                    Intent intent = new Intent(this, SurveyPopUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (isExitSurvey) {
                        intent.putExtra("Coins", surveyComponentModule.getExitOC());
                    } else {
                        intent.putExtra("Coins", surveyComponentModule.getRewardOC());
                    }

                    intent.putExtra("FeedId", feedID);
                    intent.putExtra("isSurvey", true);
                    intent.putExtra("isMicroCourse", isMicroCourse);
                    intent.putExtra("surveyName", surveyComponentModule.getSurveyModule().getName());
                    startActivity(intent);
                }
                setResult(1444, data);
                SurveyComponentActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSurveyComplete = OustAppState.getInstance().isSurveySubmitted();
        boolean isSurveyExit = OustAppState.getInstance().isSurveyExit();
        boolean isSurveyResume = OustAppState.getInstance().isSurveyResume();
        if (isSurveyComplete) {
            OustAppState.getInstance().setSurveySubmitted(false);
            OustAppState.getInstance().setSurveyExit(false);
            String content = OustAppState.getInstance().getExitMessage();
            if (content == null || content.isEmpty()) {
                content = getResources().getString(R.string.completed_survey_text);
            }
            if (surveyComponentModule.getCourseId() > 0) {
                OustStaticVariableHandling.getInstance().setComingFromSurvey(true);
            }
            if (surveyComponentModule.getAssociatedAssessmentId() > 0) {
                OustStaticVariableHandling.getInstance().setSurveyCompleted(true);
            }
            handleSurveyComplete(content, isSurveyExit);
        } else {
            if (isSurveyResume) {
                OustAppState.getInstance().setSurveyResume(false);
                restartActivity();
            }
        }
    }

    @Override
    protected void onDestroy() {
        isActivityDestroyed = true;
        super.onDestroy();
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
