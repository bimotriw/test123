package com.oustme.oustsdk.question_module.survey;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.TWO_HUNDRED_MILLI_SECONDS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.fragment.DropDownQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.LongQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MCQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MRQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.RSQuestionFragment;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.util.List;
import java.util.Objects;

public class SurveyQuestionBaseActivity extends BaseActivity implements LearningModuleInterface, SurveyFunctionsAndClicks {

    RelativeLayout question_base_root;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    /* FrameLayout audio_lay;
     ImageView audio_speaker_icon;*/
    View timer_layout;
    ProgressBar mandatory_timer_progress;
    TextView mandatory_timer_text;
    FrameLayout question_fragment_container;
    View bottom_bar;
    LinearLayout view_bottom_nav;
    RelativeLayout view_previous;
    ImageView previous_view;
    RelativeLayout view_comment;
    TextView view_count;
    RelativeLayout view_next;
    ImageView next_view;
    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    TextView branding_message_text;
    //End

    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;
    private int color;
    private int bgColor;
    private boolean disableBackButton;

    //Question Audio
    MediaPlayer mediaPlayerForAudio;
    //Animation audioAnimation;

    //touch listener
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 30;
    private boolean touchedOnce = false;

    //survey
    Dialog surveyReviewDialog;
    private boolean audioEnable;
    MenuItem itemAudio;
    boolean isMute;
    boolean isQuestionAudio = false;

    int transitionPosition = 1;


    @Override
    protected int getContentView() {
        return R.layout.activity_question_base;
    }

    @Override
    protected void initView() {

        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(getApplicationContext());
            }
            OustSdkTools.setLocale(SurveyQuestionBaseActivity.this);
            getColors();
            question_base_root = findViewById(R.id.question_base_root);
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

           /* audio_lay = tool_bar_layout.findViewById(R.id.audio_lay);
            audio_speaker_icon = tool_bar_layout.findViewById(R.id.audio_speaker_icon);*/
            timer_layout = findViewById(R.id.timer_layout);
            mandatory_timer_progress = findViewById(R.id.mandatory_timer_progress);
            mandatory_timer_text = findViewById(R.id.mandatory_timer_text);
            question_fragment_container = findViewById(R.id.question_fragment_container);
            bottom_bar = findViewById(R.id.bottom_bar);
            view_bottom_nav = findViewById(R.id.view_bottom_nav);
            view_previous = findViewById(R.id.view_previous);
            previous_view = findViewById(R.id.previous_view);
            view_comment = findViewById(R.id.view_comment);
            view_count = findViewById(R.id.view_count);
            view_next = findViewById(R.id.view_next);
            next_view = findViewById(R.id.next_view);

            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            branding_message_text = findViewById(R.id.message_text);
            //End

            view_comment.setVisibility(View.INVISIBLE);
            audioEnable = false;

            setToolbarAndIconColor();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initData() {

        try {
            fetchLayoutData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


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

    private void setToolbarAndIconColor() {
        try {
            // tool_bar_layout.setBackgroundColor(color);
            mandatory_timer_text.setTextColor(getResources().getColor(R.color.primary_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mandatory_timer_progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_correct)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void fetchLayoutData() {
        try {
            Bundle bundle = getIntent().getExtras();
            questionBaseViewModel = new ViewModelProvider(this).get(QuestionBaseViewModel.class);
            questionBaseViewModel.init(bundle);
            questionBaseViewModel.getQuestionModuleMutableLiveData().observe(this, questionBaseModel -> {
                if (questionBaseModel == null)
                    return;
                this.questionBaseModel = questionBaseModel;
                setData(questionBaseModel);

            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setData(QuestionBaseModel questionBaseModel) {
        try {
            audioEnable = false;
            isMute = false;
            invalidateOptionsMenu();
            stopMusicPlay();
            if (questionBaseModel != null) {

                boolean isFinishActivity = false;
                if (questionBaseModel.isSurvey()) {
                    timer_layout.setVisibility(View.GONE);
                    //enableSwipe();
                    if (questionBaseModel.isSurveySubmitted()) {
                        isFinishActivity = true;
                        branding_mani_layout.setVisibility(View.GONE);
                        SurveyQuestionBaseActivity.this.finish();
                    }
                }

                if (!isFinishActivity) {
                    timerForHide();
                    if (questionBaseModel.getModuleName() != null) {
                        screen_name.setText(questionBaseModel.getModuleName());
                        //screen_name.setSelected(true);
                    }

                    if (!questionBaseModel.isShowNavigationArrow()) {
                        bottom_bar.setVisibility(View.GONE);
                    }

                    if (questionBaseModel.getQuestionIndex() == 0) {
                        view_previous.setVisibility(View.INVISIBLE);
                    } else {
                        view_previous.setVisibility(View.VISIBLE);
                    }


                    view_previous.setOnClickListener(v -> gotoPreviousScreen());

                    view_next.setOnClickListener(v -> gotoNextScreen());

                    if (questionBaseModel.getType() == 1) {
                        OustPreferences.save("MRQuestionAnswers", "");
                        String currentQuestion = (questionBaseModel.getQuestionIndex() + 1) + "/" + questionBaseModel.getTotalCards();
                        if (currentQuestion.contains("/")) {
                            String[] spilt = currentQuestion.split("/");
                            Spannable spannable = new SpannableString(currentQuestion);
                            spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), currentQuestion.length(), 0);
                            view_count.setText(spannable);
                        } else {
                            view_count.setText(currentQuestion);
                        }

                        if (questionBaseModel.getQuestions() != null) {
                            if (questionBaseModel.getQuestions().isMandatory()) {
                                view_next.setVisibility(View.INVISIBLE);
                            } else {
                                view_next.setVisibility(View.VISIBLE);
                            }
                        } else {
                            view_next.setVisibility(View.VISIBLE);
                        }

                        startTransaction(questionBaseModel);
                    } else if (questionBaseModel.getType() == 2) {
                        stopMusicPlay();
                        toolbar.setVisibility(View.GONE);
                        question_base_root.setVisibility(View.VISIBLE);
                        branding_mani_layout.setVisibility(View.VISIBLE);
                        question_fragment_container.setVisibility(View.GONE);
                        bottom_bar.setVisibility(View.GONE);
                        branding_message_text.setText(getResources().getString(R.string.submitting_response));
                        questionBaseViewModel.submitGameOnBackPress();
                        reviewSubmitPopup();
                    }
                }
            }

            toolbar.setOnTouchListener((v, event) -> {
                back_button.setVisibility(View.VISIBLE);
                screen_name.setVisibility(View.VISIBLE);
                timerForHide();
                return false;
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startTransaction(QuestionBaseModel questionBaseModel) {
        DTOQuestions questions = questionBaseModel.getQuestions();
        if (questions != null) {

            checkForAudio(questions);
            toolbar.setVisibility(View.VISIBLE);
            question_base_root.setVisibility(View.VISIBLE);
            question_fragment_container.setVisibility(View.VISIBLE);
            bottom_bar.setVisibility(View.VISIBLE);
            branding_mani_layout.setVisibility(View.GONE);

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            if (transitionPosition == 1) {
                transaction.setCustomAnimations(
                        R.anim.enter_from_right,  // enter
                        R.anim.exit_to_left,  // exit
                        R.anim.enter_from_left,   // popEnter
                        R.anim.exit_to_right  // popExit
                );
            } else {
                transaction.setCustomAnimations(
                        R.anim.enter_from_left,  // enter
                        R.anim.exit_to_right,  // exit
                        R.anim.enter_from_right,   // popEnter
                        R.anim.exit_to_left  // popExit
                );
            }

            String questionCategory = questions.getQuestionCategory();
            if (questions.getQuestionType() != null && !questions.isMediaUploadQues() && !questions.isLearningPlayNew()) {
                if (questions.getQuestionType().equalsIgnoreCase(QuestionType.SURVEY_TEN_POINT)) {
                    openRSQFragment(transaction, questions, true);
                } else if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE))) {
                    openMCQFragment(transaction, questions);
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ)) {
                    openMRQFragment(transaction, questions);
                } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DROPDOWN) || (questions.getQuestionCategory() != null
                        && questions.getQuestionCategory().equals(QuestionCategory.DROPDOWN))) {
                    openDropDownQuestionFragment(transaction, questions);
                } else {
                    if (questionCategory != null && questionCategory.equals(QuestionCategory.LONG_ANSWER)) {
                        openLongQuestionFragment(transaction, questions);
                    } else {
                        openRSQFragment(transaction, questions, false);
                    }
                }
            } else {
                openRSQFragment(transaction, questions, false);
            }

        }
    }

    private void openDropDownQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {
        try {
            DropDownQuestionFragment fragment = new DropDownQuestionFragment();
            fragment.setLearningModuleInterface(SurveyQuestionBaseActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setReviewMode(questionBaseModel.isReviewMode());
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openRSQFragment(FragmentTransaction transaction, DTOQuestions questions, boolean isRSType) {
        Log.d(TAG, "openRSQFragment");
        RSQuestionFragment fragment = new RSQuestionFragment();
        fragment.setLearningModuleInterface(SurveyQuestionBaseActivity.this);
        fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
        transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
        fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
        fragment.setSurveyQuestion(questionBaseModel.isSurvey());
        fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
        fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
        fragment.setAssessmentScore(questionBaseModel.getScores());
        fragment.setTotalCards(questionBaseModel.getTotalCards());
        fragment.setRSType(isRSType);
        fragment.setQuestions(questions);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void openMCQFragment(FragmentTransaction transaction, DTOQuestions questions) {

        Log.d(TAG, "OpenMCQ");

        try {
            MCQuestionFragment fragment = new MCQuestionFragment();
            transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
            fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
            fragment.setSurveyQuestion(questionBaseModel.isSurvey());
            fragment.setLearningModuleInterface(SurveyQuestionBaseActivity.this);
            fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
            fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
            fragment.setAssessmentScore(questionBaseModel.getScores());
            fragment.setQuestions(questions);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void openMRQFragment(FragmentTransaction transaction, DTOQuestions questions) {

        Log.d(TAG, "OpenMRQ");
        MRQuestionFragment fragment = new MRQuestionFragment();
        fragment.setLearningModuleInterface(SurveyQuestionBaseActivity.this);
        fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
        transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
        fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
        fragment.setSurveyQuestion(questionBaseModel.isSurvey());
        fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
        fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
        fragment.setAssessmentScore(questionBaseModel.getScores());
        fragment.setQuestions(questions);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void openLongQuestionFragment(FragmentTransaction transaction, DTOQuestions questions) {

        Log.d(TAG, "openLongQuestionFragment");
        LongQuestionFragment fragment = new LongQuestionFragment();
        fragment.setLearningModuleInterface(SurveyQuestionBaseActivity.this);
        fragment.setShowNavigateArrow(questionBaseModel.isShowNavigationArrow());
        transaction.replace(R.id.question_fragment_container, fragment, "learningPlay_frag" + questionBaseModel.getQuestionIndex());
        fragment.setAssessmentQuestion(questionBaseModel.isAssessment());
        fragment.setSurveyQuestion(questionBaseModel.isSurvey());
        fragment.setMainCourseCardClass(questionBaseModel.getCourseCardClass());
        fragment.setCourseLevelClass(questionBaseModel.getCourseLevelClass());
        fragment.setAssessmentScore(questionBaseModel.getScores());
        fragment.setTotalCards(questionBaseModel.getTotalCards());
        fragment.setQuestions(questions);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void gotoNextScreen() {

        try {
            transitionPosition = 1;
            new Handler().postDelayed(() -> questionBaseViewModel.gotoNextScreenForSurvey(), TWO_HUNDRED_MILLI_SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void gotoPreviousScreen() {

        try {
            transitionPosition = 0;
            new Handler().postDelayed(() -> questionBaseViewModel.gotoPreviousScreen(), TWO_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
        if (questionBaseViewModel != null)
            questionBaseViewModel.setAnswerAndOc(userAns, subjectiveResponse, oc, status, time, "", "", false);
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

    }

    @Override
    public void onSurveyExit(String message) {
        questionBaseViewModel.calculateFinalScore(true, message);

    }

    @Override
    public void handleQuestionAudio(boolean play) {

    }

    @Override
    public void handleFragmentAudio(String audioFile) {

    }

    @Override
    public void onBackPressed() {
        cancelGame();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void cancelGame() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.common_pop_up, findViewById(android.R.id.content), false);
            final PopupWindow playCancelPopup = OustSdkTools.createPopUp(popUpView);
            final ImageView info_image = popUpView.findViewById(R.id.info_image);
            final TextView info_title = popUpView.findViewById(R.id.info_title);
            final TextView info_description = popUpView.findViewById(R.id.info_description);
            final LinearLayout info_no = popUpView.findViewById(R.id.info_no);
            final TextView info_no_text = popUpView.findViewById(R.id.info_no_text);
            final LinearLayout info_yes = popUpView.findViewById(R.id.info_yes);
            final TextView info_yes_text = popUpView.findViewById(R.id.info_yes_text);

            info_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));

            Drawable yesDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            info_yes.setBackground(OustResourceUtils.setDefaultDrawableColor(yesDrawable));

            info_yes_text.setText(getResources().getString(R.string.yes));
            info_no_text.setText(getResources().getString(R.string.no));

            info_description.setText(getResources().getString(R.string.pause_and_resume));
            info_title.setText(getResources().getString(R.string.warning));
            info_description.setVisibility(View.VISIBLE);
            OustPreferences.saveAppInstallVariable("isContactPopup", true);

            info_yes.setOnClickListener(view -> {
                playCancelPopup.dismiss();
                if (questionBaseViewModel != null) {
                    OustAppState.getInstance().setDisableBackButton(disableBackButton);
                    OustAppState.getInstance().setAssessmentRunning(true);
                    questionBaseViewModel.submitGameOnBackPress();
                    stopMusicPlay();
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            OustAppState.getInstance().setSurveyResume(true);
                            SurveyQuestionBaseActivity.this.finish();

                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }, 1000);

                }

            });

            info_no.setOnClickListener(view -> playCancelPopup.dismiss());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForAudio(DTOQuestions questions) {
        Log.d(TAG, "checkForAudio: ");
        stopMusicPlay();
        if (questions != null) {
            if (!(questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)
                    || questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I) ||
                    questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V))) {
                if (questions.getAudio() != null && !questions.getAudio().isEmpty()) {
                    audioEnable = true;
                    invalidateOptionsMenu();
                    initializeQuestionSound(questions.getAudio());
                } else {
                    audioEnable = false;
                    if (isQuestionAudio) {
                        isQuestionAudio = false;
                        initializeQuestionSound("");
                    }
                }
            } else {
                stopMusicPlay();
            }
        }
    }

    public void stopMusicPlay() {
        try {
            Log.d(TAG, "stopMusicPlay: ");
            resetAudioPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

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

    private void getSignedUrl(String audio) {
        try {
            if (audio != null && !audio.isEmpty()) {
                audio = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "qaudio/" + audio;
                prepareExoPlayerFromUri(Uri.parse(audio));
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
                mediaPlayerForAudio.setDataSource(SurveyQuestionBaseActivity.this, uri);
                mediaPlayerForAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerForAudio.prepare();
                mediaPlayerForAudio.start();
                //start Animation
             /*   if(itemAudio!=null){
                    itemAudio.getActionView().startAnimation(audioAnimation);
                }*/


            }


            invalidateOptionsMenu();


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAudioPlayer() {
        try {
            isQuestionAudio = true;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void enableSwipe(View view) {
        try {
            view.setOnTouchListener((v, event) -> {
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
                                    boolean isMandatory = false;
                                    if (questionBaseModel != null) {
                                        if (questionBaseModel.getQuestions() != null) {
                                            if (questionBaseModel.getQuestions().isMandatory()) {
                                                isMandatory = true;
                                            }
                                        }
                                    }
                                    if (!isMandatory) {
                                        gotoNextScreen();
                                    }

                                }
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    gotoPreviousScreen();
                                }
                            }

                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    gotoPreviousScreen();
                                }
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    touchedOnce = false;
                                    boolean isMandatory = false;
                                    if (questionBaseModel != null) {
                                        if (questionBaseModel.getQuestions() != null) {
                                            if (questionBaseModel.getQuestions().isMandatory()) {
                                                isMandatory = true;
                                            }
                                        }
                                    }
                                    if (!isMandatory) {
                                        gotoNextScreen();
                                    }
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


    private void reviewSubmitPopup() {
        if (surveyReviewDialog != null && surveyReviewDialog.isShowing()) {
            surveyReviewDialog.dismiss();
        }
        surveyReviewDialog = new Dialog(SurveyQuestionBaseActivity.this, R.style.DialogTheme);
        surveyReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        surveyReviewDialog.setContentView(R.layout.popup_survey_submit);
        surveyReviewDialog.setCancelable(false);
        Objects.requireNonNull(surveyReviewDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        surveyReviewDialog.show();

        LinearLayout survey_review = surveyReviewDialog.findViewById(R.id.survey_review);
        LinearLayout survey_submit = surveyReviewDialog.findViewById(R.id.survey_submit);
        FrameLayout popup_close = surveyReviewDialog.findViewById(R.id.popup_close);
        RelativeLayout layout_loader_new_gif = surveyReviewDialog.findViewById(R.id.layout_loader_new_gif);
        CardView card_layout = surveyReviewDialog.findViewById(R.id.card_layout);
        ImageView thanks_image = surveyReviewDialog.findViewById(R.id.thanks_image);

        Drawable tickDrawable = getResources().getDrawable(R.drawable.tick_survey);
        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        Drawable reviewDrawable = getResources().getDrawable(R.drawable.course_button_bg);

        DrawableCompat.setTint(
                DrawableCompat.wrap(reviewDrawable),
                Color.parseColor("#A4A4A4")
        );

        survey_review.setBackground(reviewDrawable);
        survey_submit.setBackground(OustSdkTools.drawableColor(drawable));
        thanks_image.setImageDrawable(OustSdkTools.drawableColor(tickDrawable));

        survey_review.setOnClickListener(v -> {
            if (surveyReviewDialog.isShowing()) {
                surveyReviewDialog.dismiss();
            }
            questionBaseViewModel.handleReview();
            //  startTransaction(questionBaseModel);
        });

        survey_submit.setOnClickListener(v -> {
            if (surveyReviewDialog.isShowing()) {
                surveyReviewDialog.dismiss();
            }
            layout_loader_new_gif.setVisibility(View.GONE);
            card_layout.setVisibility(View.GONE);
            questionBaseViewModel.calculateFinalScore(false, getResources().getString(R.string.completing_survey_text));
        });

        popup_close.setOnClickListener(v -> {
            if (surveyReviewDialog.isShowing()) {
                surveyReviewDialog.dismiss();
            }
        });

    }

    @Override
    public void onUserInteraction() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (questionBaseViewModel != null) {
            OustAppState.getInstance().setDisableBackButton(disableBackButton);
            OustAppState.getInstance().setAssessmentRunning(true);
            questionBaseViewModel.submitGameOnBackPress();
            stopMusicPlay();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                try {
                    OustAppState.getInstance().setSurveyResume(true);
                    SurveyQuestionBaseActivity.this.finish();

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }, 1000);

        }

    }

    private void timerForHide() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                back_button.setVisibility(View.INVISIBLE);
                screen_name.setVisibility(View.INVISIBLE);
            }
        }.start();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_question_menu, menu);


        itemAudio = menu.findItem(R.id.action_audio);
        Drawable audioDrawable;
        if (isMute) {

            audioDrawable = getResources().getDrawable(R.drawable.ic_audiooff);
            //cancel Animation
            //itemAudio.getActionView().clearAnimation();
        } else {
            audioDrawable = getResources().getDrawable(R.drawable.ic_audio_on);
            //startAnimation
            // itemAudio.getActionView().startAnimation(audioAnimation);
        }
        itemAudio.setIcon(OustResourceUtils.setDefaultDrawableColor(audioDrawable, color));
        itemAudio.setVisible(isAudioEnable());


        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_audio) {
            isMute = false;
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
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isAudioEnable() {
        Log.e("AudioQuestion", "Audio " + audioEnable);
        return audioEnable;
    }

}
