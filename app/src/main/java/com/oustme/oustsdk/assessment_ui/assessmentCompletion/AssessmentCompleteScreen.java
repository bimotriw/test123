package com.oustme.oustsdk.assessment_ui.assessmentCompletion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentDetailScreen;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentExtraDetails;
import com.oustme.oustsdk.course_ui.CourseLearningMapActivity;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.mvvm.BaseActivity;
import com.oustme.oustsdk.question_module.assessment.AssessmentQuestionBaseActivity;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.survey_module.SurveyComponentActivity;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;

public class AssessmentCompleteScreen extends BaseActivity<AssessmentCompletionVM> {

    private static final String TAG = "AssessmentComplete";

    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    FrameLayout leaderBoard_lay;
    ImageView bg_animation;
    ImageView iv_user_complete;
    TextView completed_text;
    TextView assessment_title;
    TextView assessment_completed_date;
    TextView assessment_completed_message;
    TextView assessment_rating;
    LinearLayout show_result_remark;
    LinearLayout show_result_score;
    TextView user_score_text;
    TextView your_percentage_text;
    TextView user_percentage_text;
    TextView user_time_taken;
    TextView participants_count;
    TextView questions_count;
    LinearLayout show_score_progress;
    ProgressBar user_correct_answer_progress;
    TextView user_correct_answer_count;
    ProgressBar user_wrong_answer_progress;
    TextView user_wrong_answer_count;
    LinearLayout assessment_user_remark_info;
    TextView user_remark_time_taken;
    TextView participants_remark_count;
    TextView questions_remark_count;
    TextView warning_info;
    FrameLayout assessment_result_action;
    TextView assessment_status_text;
    TextView passing_score_note;
    TextView next_module_info;


    String toolbarColorCode;
    AssessmentCompletionRepo assessmentCompletionRepo;
    AssessmentCompletionModel testAssessmentCompletionModel = new AssessmentCompletionModel();
    boolean isReAttemptAllowed;
    long assessmentId;
    private ActiveGame activeGame;
    private SubmitRequest submitRequest;
    private PlayResponse playResponse;
    private boolean shouldShowAnswers = false;
    private boolean isTakeAnswer = false;
    private boolean isSurveyFromCourse = false;
    private boolean isMultipleCplModule = false;
    private long currentCplId;
    private boolean isComingFromCpl = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            setContentView(R.layout.activity_assessment_complete_screen);

            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(AssessmentCompleteScreen.this);
            }

            OustSdkTools.setLocale(AssessmentCompleteScreen.this);

            toolbar_lay = findViewById(R.id.toolbar_lay);
            toolbar_close_icon = findViewById(R.id.toolbar_close_icon);
            leaderBoard_lay = findViewById(R.id.leaderBoard_lay);
            bg_animation = findViewById(R.id.bg_animation);
            iv_user_complete = findViewById(R.id.iv_user_complete);
            completed_text = findViewById(R.id.completed_text);
            assessment_title = findViewById(R.id.assessment_title);
            assessment_completed_date = findViewById(R.id.assessment_completed_date);
            assessment_completed_message = findViewById(R.id.assessment_completed_message);
            assessment_rating = findViewById(R.id.assessment_rating);
            show_result_remark = findViewById(R.id.show_result_remark);
            show_result_score = findViewById(R.id.show_result_score);
            user_score_text = findViewById(R.id.user_score_text);
            your_percentage_text = findViewById(R.id.your_percentage_text);
            user_percentage_text = findViewById(R.id.user_percentage_text);
            user_time_taken = findViewById(R.id.user_time_taken);
            participants_count = findViewById(R.id.participants_count);
            questions_count = findViewById(R.id.questions_count);
            show_score_progress = findViewById(R.id.show_score_progress);
            user_correct_answer_progress = findViewById(R.id.user_correct_answer_progress);
            user_correct_answer_count = findViewById(R.id.user_correct_answer_count);
            user_wrong_answer_progress = findViewById(R.id.user_wrong_answer_progress);
            user_wrong_answer_count = findViewById(R.id.user_wrong_answer_count);
            assessment_user_remark_info = findViewById(R.id.assessment_user_remark_info);
            user_remark_time_taken = findViewById(R.id.user_remark_time_taken);
            participants_remark_count = findViewById(R.id.participants_remark_count);
            questions_remark_count = findViewById(R.id.questions_remark_count);
            warning_info = findViewById(R.id.warning_info);
            assessment_result_action = findViewById(R.id.assessment_result_action);
            assessment_status_text = findViewById(R.id.assessment_status_text);
            passing_score_note = findViewById(R.id.passing_score_note);
            next_module_info = findViewById(R.id.next_module_info);

//            OustGATools.getInstance().reportPageViewToGoogle(AssessmentCompleteScreen.this, "Assessment Complete Page");

            toolbarColorCode = OustPreferences.get("toolbarColorCode");
            setIconColors();

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                activeGame = OustSdkTools.getAcceptChallengeData(bundle.getString("ActiveGame"));
                submitRequest = OustSdkTools.getSubmit(bundle.getString("SubmitRequest"));
                playResponse = OustAppState.getInstance().getPlayResponse();
                isSurveyFromCourse = bundle.getBoolean("isSurveyFromCourse", false);
                isMultipleCplModule = bundle.getBoolean("isMultipleCplModule", false);
                currentCplId = bundle.getLong("currentCplId");
                isComingFromCpl = bundle.getBoolean("isComingFromCpl");
            }


            viewModel.loadAssessmenCompletionData();
            viewModel.getAssessmentCompletion().observe(this, new AssessmentCompletionObserver());

            toolbar_close_icon.setOnClickListener(v -> {
                try {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    if (viewModel.assessmentCompletionRepo.assessmentExtraDetails.isFromCourse()) {
                        if (viewModel.assessmentCompletionRepo.assessmentCompletionModel.isPassed()) {
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                        } else {
                            if (!viewModel.assessmentCompletionRepo.assessmentCompletionModel.isReAttemptAllowed()) {
                                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                            } else {
                                if (viewModel.assessmentCompletionRepo.assessmentCompletionModel.getnAttemptCount() >= viewModel.assessmentCompletionRepo.assessmentCompletionModel.getnAttemptAllowedToPass()) {
                                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                                }
                            }
                        }
                        Log.d(TAG, "onCreate: AssessmentCompleted:" + OustStaticVariableHandling.getInstance().isAssessmentCompleted());
                        AssessmentCompleteScreen.this.finish();
                    } else {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            leaderBoard_lay.setOnClickListener(v -> {
                if (!OustSdkTools.checkInternetStatus()) {
                    OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
                    return;
                }

                Intent leaderBoardIntent = new Intent(AssessmentCompleteScreen.this, CommonLeaderBoardActivity.class);
                leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle containerBundle = new Bundle();
                containerBundle.putString("containerType", "AssessmentLeaderBoard");
                containerBundle.putLong("containerContentId", OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId());
                if (testAssessmentCompletionModel.getTitle() != null && !testAssessmentCompletionModel.getTitle().isEmpty()) {
                    containerBundle.putString("contentName", testAssessmentCompletionModel.getTitle());
                }
                leaderBoardIntent.putExtras(containerBundle);
                startActivity(leaderBoardIntent);
            });

            assessment_result_action.setOnClickListener(view -> {
                Log.d(TAG, "onCreate: assessment_result_action");
                if (isTakeAnswer) {
                    Gson gson = new GsonBuilder().create();
//                    Intent intent = new Intent(AssessmentCompleteScreen.this, AssessmentQuestionReviewBaseActivity.class);
//                    if (OustPreferences.getAppInstallVariable("isLayout4") &&
//                            OustPreferences.getAppInstallVariable(AppConstants.StringConstants.NEW_ASSESSMENT_UI)) {
                    Intent intent = new Intent(this, AssessmentQuestionBaseActivity.class);
                    intent.putExtra("isAssessment", true);
                    intent.putExtra("bgImage", OustAppState.getInstance().getAssessmentFirebaseClass().getBgImg());
                    intent.putExtra("moduleName", OustAppState.getInstance().getAssessmentFirebaseClass().getName());
                    intent.putExtra("isReviewMode", true);
//                    }
                    intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                    intent.putExtra("currentCplId", currentCplId);
                    intent.putExtra("ActiveUser", gson.toJson(OustAppState.getInstance().getActiveUser()));
                    intent.putExtra("ActiveGame", gson.toJson(activeGame));
                    OustAppState.getInstance().setPlayResponse(this.playResponse);
                    intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
                    startActivity(intent);

                } else {
                    AssessmentCompletionModel assessmentCompletionModel = viewModel.assessmentCompletionRepo.assessmentCompletionModel;
                    if (assessmentCompletionModel != null) {
                        try {
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                            if (assessmentCompletionModel.assessmentExtraDetails.isFromCourse()) {
                                if (assessmentCompletionModel.isPassed()) {
                                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                                } else {
                                    if (!assessmentCompletionModel.isReAttemptAllowed()) {
                                        OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                                    } else {
                                        if (assessmentCompletionModel.getnAttemptCount() >= assessmentCompletionModel.getnAttemptAllowedToPass()) {
                                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                                        }
                                    }
                                }
                                Log.d(TAG, "onCreate: AssessmentCompleted:" + OustStaticVariableHandling.getInstance().isAssessmentCompleted());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }

                        if (assessmentCompletionModel.isPassed()) {
                            if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isCourseAssociated()) {
                                viewModel.getCourseDetails();
                            } else if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated()) {
                                startSurvey(assessmentCompletionModel);
                            } else {
                                AssessmentCompleteScreen.this.finish();
                            }
                        } else {
                            if (assessmentCompletionModel.isReAttemptAllowed() &&
                                    (assessmentCompletionModel.getnAttemptCount() < assessmentCompletionModel.getnAttemptAllowedToPass())) {
                                Intent intent = new Intent(this, AssessmentDetailScreen.class);
                                intent.putExtra("assessmentId", "" + assessmentId);
                                if (assessmentCompletionModel.assessmentExtraDetails != null) {
                                    intent.putExtra("nAttemptCount", assessmentCompletionModel.getnAttemptCount());
                                    intent.putExtra("IS_FROM_COURSE", assessmentCompletionModel.assessmentExtraDetails.isFromCourse());
                                    intent.putExtra("courseId", String.valueOf(assessmentCompletionModel.assessmentExtraDetails.getCourseId()));
                                    intent.putExtra("isMicroCourse", assessmentCompletionModel.assessmentExtraDetails.isMicroCourse());

                                }
                                intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                                intent.putExtra("currentCplId", currentCplId);
                                intent.putExtra("isComingFromCpl", isComingFromCpl);

                                //intent.putExtra("autoStartAssessment", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else if (assessmentCompletionModel.isReAttemptAllowed() &&
                                    (assessmentCompletionModel.getnAttemptCount()) > assessmentCompletionModel.getnAttemptAllowedToPass()) {
                                if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isCourseAssociated()) {
                                    viewModel.getCourseDetails();
                                } else if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated()) {
                                    startSurvey(assessmentCompletionModel);
                                }
                            } else {
                                if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isCourseAssociated()) {
                                    viewModel.getCourseDetails();
                                } else if (assessmentCompletionModel.getAssessmentExtraDetails() != null && assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated()) {
                                    startSurvey(assessmentCompletionModel);
                                }
                            }
                            AssessmentCompleteScreen.this.finish();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @NonNull
    @Override
    protected AssessmentCompletionVM createViewModel(Bundle bundle) {
        if (assessmentCompletionRepo != null) {
            getViewModelStore().clear();
            assessmentCompletionRepo = null;
            this.viewModel = null;
        }
        assessmentCompletionRepo = new AssessmentCompletionRepo(bundle);
        AssessmentCompleteVMFactory factory = new AssessmentCompleteVMFactory(assessmentCompletionRepo);
        return ViewModelProviders.of(this, factory).get(AssessmentCompletionVM.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getViewModelStore().clear();
            if (assessmentCompletionRepo != null) {
                assessmentCompletionRepo = null;
                this.viewModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private class AssessmentCompletionObserver implements Observer<AssessmentCompletionModel> {
        @Override
        public void onChanged(AssessmentCompletionModel assessmentCompletionModel) {
            Log.d(TAG, "AssessmentCompletionObserver: ");
            if (assessmentCompletionModel == null)
                return;

            testAssessmentCompletionModel = assessmentCompletionModel;
            AssessmentExtraDetails assessmentExtraDetails = assessmentCompletionModel.assessmentExtraDetails;
            shouldShowAnswers = OustAppState.getInstance().getAssessmentFirebaseClass().isShowQuestionsOnCompletion();
            if (assessmentCompletionModel.getType() == 2) {
                warning_info.setText(getResources().getString(R.string.complete_assigned_module));
                warning_info.setVisibility(View.VISIBLE);
                startCourse(assessmentExtraDetails.getMappedCourseID());
                return;
            }

            boolean isOrgLeaderBoardHide = OustPreferences.getAppInstallVariable("hideAllAssessmentLeaderBoard");
            if (!OustAppState.getInstance().getAssessmentFirebaseClass().isHideLeaderboard() && !isOrgLeaderBoardHide) {
                leaderBoard_lay.setVisibility(View.VISIBLE);
            }

            assessmentId = assessmentCompletionModel.getAssessmentId();

            //title without html tag support
            if (assessmentCompletionModel.getTitle() != null && !assessmentCompletionModel.getTitle().isEmpty()) {
                assessment_title.setText(assessmentCompletionModel.getTitle());
            }
            //assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
            isReAttemptAllowed = false;
            if (!assessmentCompletionModel.isPassed()) {
                if (assessmentCompletionModel.isReAttemptAllowed() &&
                        (assessmentCompletionModel.getnAttemptCount()) < assessmentCompletionModel.getnAttemptAllowedToPass()) {

                    String warningInfoText = (assessmentCompletionModel.getnAttemptAllowedToPass() - (assessmentCompletionModel.getnAttemptCount()))
                            + " " + getResources().getString(R.string.attempt_left_text);
                    warning_info.setText(warningInfoText);
                    warning_info.setVisibility(View.VISIBLE);
                    assessment_status_text.setText("");
                    assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                    isReAttemptAllowed = true;
                } else {
                    if (assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated() || isSurveyFromCourse) {
                        next_module_info.setVisibility(View.VISIBLE);
                        next_module_info.setText(getResources().getString(R.string.get_started_survey));
                        assessment_status_text.setText("");
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                        isTakeAnswer = false;
                    } else {
                        next_module_info.setVisibility(View.GONE);
                        if (OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                            isTakeAnswer = false;
                        } else {
                            if (shouldShowAnswers) {
                                isTakeAnswer = true;
                                assessment_status_text.setText(getResources().getString(R.string.answer));
                                assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.list_ansr, 0, 0, 0);
                            }
                        }
                    }
                }
            } else {
                if (assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated() || isSurveyFromCourse) {
                    next_module_info.setVisibility(View.VISIBLE);
                    next_module_info.setText(getResources().getString(R.string.get_started_survey));
                    assessment_status_text.setText("");
                    assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                    isTakeAnswer = false;
                } else {
                    next_module_info.setVisibility(View.GONE);
                    if (OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                        isTakeAnswer = false;
                    } else {
                        if (shouldShowAnswers) {
                            isTakeAnswer = true;
                            assessment_status_text.setText(getResources().getString(R.string.answer));
                            assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.list_ansr, 0, 0, 0);
                        }
                    }
                }
            }

            String completedText = getResources().getString(R.string.you_have_completed_text);

            String questionCount = "" + (OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount());
            if (assessmentCompletionModel.isShowAssessmentResultScore() && !questionCount.equalsIgnoreCase("0")) {

                show_result_score.setVisibility(View.VISIBLE);
                show_result_remark.setVisibility(View.VISIBLE);
                assessment_completed_message.setVisibility(View.GONE);
                passing_score_note.setVisibility(View.VISIBLE);
                assessment_user_remark_info.setVisibility(View.GONE);

                if (!assessmentCompletionModel.isPassed()) {
                    //completedText = getResources().getString(R.string.failed_module);
                    iv_user_complete.setImageResource(R.drawable.ic_well_tried_thum);
                    user_percentage_text.setTextColor(getResources().getColor(R.color.error_incorrect));
                }

                if (OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                    warning_info.setText(getResources().getString(R.string.complete_assigned_module));

                    if (!assessmentCompletionModel.isPassed()) {
                        if (assessmentCompletionModel.isReAttemptAllowed() &&
                                (assessmentCompletionModel.getnAttemptCount()) > assessmentCompletionModel.getnAttemptAllowedToPass()) {

                            warning_info.setVisibility(View.VISIBLE);
                            assessment_status_text.setText("");
                            assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);

                        }
                    } else {
                        warning_info.setVisibility(View.VISIBLE);
                        assessment_status_text.setText("");
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                    }
                }

                String yourScoreText = assessmentCompletionModel.getUserScore();
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

                user_percentage_text.setText(assessmentCompletionModel.getUserPercentage());

                user_correct_answer_count.setText(String.valueOf(assessmentCompletionModel.getCorrectAnswer()));
                user_correct_answer_progress.setProgress(assessmentCompletionModel.getCorrectAnswerProgress());
                user_wrong_answer_count.setText(String.valueOf(assessmentCompletionModel.getWrongAnswer()));
                user_wrong_answer_progress.setProgress(assessmentCompletionModel.getWrongAnswerProgress());

                if (assessmentCompletionModel.getRating() != null && !assessmentCompletionModel.getRating().isEmpty()) {
                    String ratingText = assessmentCompletionModel.getRating();
                    String userRatingText = getResources().getString(R.string.your_rating_excellent_text) + " " + ratingText;
                    Spannable spanRatingText = new SpannableString(userRatingText);
                    spanRatingText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), (userRatingText.length() - ratingText.length()), userRatingText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    assessment_rating.setText(spanRatingText);
                    assessment_rating.setVisibility(View.VISIBLE);
                }
            } else {
                show_result_score.setVisibility(View.GONE);
                iv_user_complete.setImageResource(R.drawable.submit_text);
                assessment_completed_message.setVisibility(View.VISIBLE);
                assessment_user_remark_info.setVisibility(View.VISIBLE);
                show_result_remark.setVisibility(View.GONE);
                passing_score_note.setVisibility(View.INVISIBLE);
                warning_info.setVisibility(View.INVISIBLE);

                if (OustAppState.getInstance().getAssessmentFirebaseClass().getMappedCourseId() != 0) {
                    warning_info.setText(getResources().getString(R.string.complete_assigned_module));

                    if (!assessmentCompletionModel.isPassed()) {
                        if (assessmentCompletionModel.isReAttemptAllowed() &&
                                (assessmentCompletionModel.getnAttemptCount()) > assessmentCompletionModel.getnAttemptAllowedToPass()) {
                            warning_info.setVisibility(View.VISIBLE);
                            assessment_status_text.setText("");
                            assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                        }
                    } else {
                        warning_info.setVisibility(View.VISIBLE);
                        assessment_status_text.setText("");
                        assessment_status_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
                    }
                }
            }

            completed_text.setText(completedText);

            if (assessmentCompletionModel.getCompletionDateAndTime() != null && !assessmentCompletionModel.getCompletionDateAndTime().isEmpty()) {
                String completionDateText = getResources().getString(R.string.on_text) + " " + assessmentCompletionModel.getCompletionDateAndTime();
                assessment_completed_date.setText(completionDateText);
            }


            String timeTakenText = assessmentCompletionModel.getTimeTaken();
            SpannableString spannableString = new SpannableString(timeTakenText);
            if (timeTakenText != null && timeTakenText.contains(" ")) {
                String[] timeTaken = timeTakenText.split(" ");
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(1.67f), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            user_time_taken.setText(spannableString);
            user_remark_time_taken.setText(spannableString);
            participants_count.setText(assessmentCompletionModel.getParticipantsCount());
            participants_remark_count.setText(assessmentCompletionModel.getParticipantsCount());

            questionCount = "" + (OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions());
            questions_count.setText(questionCount);
            questions_remark_count.setText(questionCount);


            String passScoreText = "* " + getResources().getString(R.string.passing_score) + " " + OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage() + "%";
            spannableString = new SpannableString(passScoreText);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            passing_score_note.setText(spannableString);


            if (OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage() == 0) {
                passing_score_note.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setIconColors() {
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
        Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        assessment_result_action.setBackground(OustSdkTools.drawableColor(actionDrawable));
    }

    private void startCourse(long mappedCourseId) {
        /*if (OustPreferences.getAppInstallVariable("isLayout4") &&
                OustPreferences.getAppInstallVariable(AppConstants.StringConstants.IS_NEW_LEARNING_MAP_MODE)) {*/
        Intent courseLearningIntent = new Intent(AssessmentCompleteScreen.this, CourseLearningMapActivity.class);
        Bundle courseBundle = new Bundle();
        courseBundle.putLong("learningId", mappedCourseId);
        courseLearningIntent.putExtras(courseBundle);
        courseLearningIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        OustSdkApplication.getContext().startActivity(courseLearningIntent);
        /*} else {
            Intent intent = new Intent(this, NewLearningMapActivity.class);
            intent.putExtra("learningId", mappedCourseId + "");
            intent.putExtra("clickOnStart", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            OustSdkApplication.getContext().startActivity(intent);
        }*/

        AssessmentCompleteScreen.this.finish();
    }

    @Override
    public void onBackPressed() {
        try {
            AssessmentCompletionModel assessmentCompletionModel = viewModel.assessmentCompletionRepo.assessmentCompletionModel;
            if (assessmentCompletionModel != null) {
                try {
                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(false);
                    if (assessmentCompletionModel.assessmentExtraDetails.isFromCourse()) {
                        if (assessmentCompletionModel.isPassed) {
                            OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                        } else {
                            if (!assessmentCompletionModel.isReAttemptAllowed()) {
                                OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                            } else {
                                if (assessmentCompletionModel.getnAttemptCount() >= assessmentCompletionModel.getnAttemptAllowedToPass()) {
                                    OustStaticVariableHandling.getInstance().setAssessmentCompleted(true);
                                }
                            }
                        }
                        Log.d(TAG, "onCreate: AssessmentCompleted:" + OustStaticVariableHandling.getInstance().isAssessmentCompleted());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if (assessmentCompletionModel.isPassed()) {
                    if (assessmentCompletionModel.getAssessmentExtraDetails().isCourseAssociated()) {
                        viewModel.getCourseDetails();
                        return;
                    } else if (assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated()) {
                        startSurvey(assessmentCompletionModel);
                        return;
                    }
                } else {
                    if (assessmentCompletionModel.isReAttemptAllowed() && (assessmentCompletionModel.getnAttemptCount() < assessmentCompletionModel.getnAttemptAllowedToPass())) {
                        Intent intent = new Intent(this, AssessmentDetailScreen.class);
                        intent.putExtra("assessmentId", "" + assessmentId);
                        intent.putExtra("autoStartAssessment", false);
                        if (assessmentCompletionModel.assessmentExtraDetails != null) {
                            intent.putExtra("nAttemptCount", assessmentCompletionModel.getnAttemptCount());
                            intent.putExtra("IS_FROM_COURSE", assessmentCompletionModel.assessmentExtraDetails.isFromCourse());
                            intent.putExtra("courseId", String.valueOf(assessmentCompletionModel.assessmentExtraDetails.getCourseId()));
                            intent.putExtra("isMicroCourse", assessmentCompletionModel.assessmentExtraDetails.isMicroCourse());
                        }
                        intent.putExtra("isMultipleCplModule", isMultipleCplModule);
                        intent.putExtra("currentCplId", currentCplId);
                        intent.putExtra("isComingFromCpl", isComingFromCpl);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        super.onBackPressed();
    }

    private void startSurvey(AssessmentCompletionModel assessmentCompletionModel) {
        if (!OustStaticVariableHandling.getInstance().isSurveyCompleted()) {
            if (OustStaticVariableHandling.getInstance().isSurveyOpened() && !assessmentCompletionModel.getAssessmentExtraDetails().isSurveyMandatory()) {
                closePage();
            } else {
                OustStaticVariableHandling.getInstance().setSurveyOpened(true);
                OustStaticVariableHandling.getInstance().setSurveyCompleted(false);
//                Intent intent = new Intent(this, SurveyDetailActivity.class);
//                if (OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_SURVEY_NEW_UI)) {
                Intent intent = new Intent(OustSdkApplication.getContext(), SurveyComponentActivity.class);
//                }
                //intent.putExtra("surveyTitle", surveyTitle);
                intent.putExtra("assessmentId", "" + assessmentCompletionModel.getAssessmentExtraDetails().getMappedSurveyId());
                intent.putExtra("associatedAssessmentId", assessmentId);
                intent.putExtra("surveyMandatory", assessmentCompletionModel.getAssessmentExtraDetails().isSurveyMandatory());
                startActivity(intent);
            }
        } else {
            closePage();
        }
    }

    private void closePage() {
        OustStaticVariableHandling.getInstance().setSurveyOpened(false);
        OustStaticVariableHandling.getInstance().setSurveyCompleted(false);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AssessmentCompletionModel assessmentCompletionModel = viewModel.assessmentCompletionRepo.assessmentCompletionModel;
            if (assessmentCompletionModel != null && assessmentCompletionModel.assessmentExtraDetails != null && assessmentCompletionModel.getAssessmentExtraDetails().isSurveyAssociated()) {
                if (OustStaticVariableHandling.getInstance().isSurveyCompleted()) {
                    closePage();
                } else if (OustStaticVariableHandling.getInstance().isSurveyOpened() && !assessmentCompletionModel.getAssessmentExtraDetails().isSurveyMandatory()) {
                    closePage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
