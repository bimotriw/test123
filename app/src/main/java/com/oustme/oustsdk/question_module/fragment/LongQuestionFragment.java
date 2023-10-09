package com.oustme.oustsdk.question_module.fragment;

import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class LongQuestionFragment extends Fragment {

    private static final String TAG = "LongQuestionFragment";
    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    TextView question;
    LinearLayout question_image_lay;
    ImageView question_image;
    RelativeLayout video_lay;
    RelativeLayout media_question_container;
    TextView info_type;
    ImageView expand_icon;
    CardView input_card;
    EditText long_answer_et;
    TextView limit_exceed;
    LinearLayout limit_layout;
    TextView limit_text;
    FrameLayout question_action_button;
    KatexView question_katex;
    WebView question_description_webView;
    String solutionText;
    CircularProgressIndicator long_answer_loader;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;

    //View Model from Assessment and survey
    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;
    AssessmentContentHandlingInterface assessmentContentHandler;

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    int questionNo;
    CourseDataClass courseDataClass;
    String courseId;
    String courseName;
    boolean zeroXpForQCard;
    boolean isQuestionImageShown;
    String backgroundImage;
    List<FavCardDetails> favouriteCardList;
    boolean isQuestionTTSEnabled;
    boolean isRMFavourite;
    boolean isCourseCompleted;
    boolean isReviewMode;
    boolean isExamMode;
    boolean isCourseQuestion;

    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    //additional parameters
    int color;
    boolean showNavigateArrow;
    boolean isAssessmentQuestion;
    boolean isVideoCompleted;
    boolean isSurveyQuestion;
    boolean isReplayMode;
    boolean isLevelCompleted;
    private Scores score;
    private int cardCount;

    boolean isVideoOverlay;
    boolean proceedOnWrong;

    int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    int minWordsCount = 0;

    SurveyFunctionsAndClicks surveyFunctionsAndClicks;
    CustomExoPlayerView customExoPlayerView;
    Context context;
    PopupWindow zoomImagePopup;
    private boolean isVideoPlaying;
    String finalVideoUrl;
    private MediaPlayer answerChosenPlayer;

    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;
    boolean questionLoaded = false;
    private boolean isVideoPaused = false;
    private boolean isPausedProgrammatically = false;

    public LongQuestionFragment() {
        // Required empty public constructor
    }


    public static LongQuestionFragment newInstance() {
        return new LongQuestionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionBaseViewModel = new ViewModelProvider(requireActivity()).get(QuestionBaseViewModel.class);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            if (isSurveyQuestion) {
                surveyFunctionsAndClicks = (SurveyFunctionsAndClicks) context;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_long_question, container, false);

        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        question = view.findViewById(R.id.question);
        video_lay = view.findViewById(R.id.video_lay);
        media_question_container = view.findViewById(R.id.media_question_container);
        question_image_lay = view.findViewById(R.id.question_image_lay);
        question_image = view.findViewById(R.id.question_image);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        input_card = view.findViewById(R.id.input_card);
        long_answer_et = view.findViewById(R.id.long_answer_et);
        limit_exceed = view.findViewById(R.id.limit_exceed);
        limit_layout = view.findViewById(R.id.limit_layout);
        limit_text = view.findViewById(R.id.limit_text);
        question_action_button = view.findViewById(R.id.question_action_button);
        question_katex = view.findViewById(R.id.question_katex);
        question_description_webView = view.findViewById(R.id.description_webView);
        long_answer_loader = view.findViewById(R.id.long_answer_loader);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        setIconColors(color, true);
        if (isCourseQuestion) {
            setQuestionData(questions);
        } else {
            questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
                if (questionBaseModel == null)
                    return;

                this.questionBaseModel = questionBaseModel;
                if (questionBaseModel.getQuestions() != null && questionBaseModel.getQuestions().getQuestionType() != null &&
                        questionBaseModel.getQuestions().getQuestionType().equalsIgnoreCase(QuestionType.LONG_ANSWER)) {
                    if (!questionLoaded) {
                        setData(questionBaseModel);
                        questionLoaded = true;
                    }
                }
            });
        }
    }

    //All modules
    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setMainCourseCardClass(DTOCourseCard courseCardClass) {
        try {
            int savedCardID = (int) courseCardClass.getCardId();
            cardId = "" + savedCardID;
            this.questionXp = courseCardClass.getXp();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            if (mainCourseCardClass.getXp() == 0) {
                mainCourseCardClass.setXp(100);
            }
            try {

                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }
                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }

                if (isRandomizeQuestion) {
                    if (isCourseQuestion) {
                        CourseQuestionHandling.randomizeOption(mainCourseCardClass);
                    } else if (questionBaseViewModel != null) {
                        this.mainCourseCardClass = questionBaseViewModel.randomizeOption(mainCourseCardClass);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        questions = mainCourseCardClass.getQuestionData();
        if (questions == null) {
            if (learningModuleInterface != null) {
                learningModuleInterface.endActivity();
            }
        }

        if (isAssessmentQuestion) {
            mainCourseCardClass.setXp(questionXp);
        } else if (isCourseQuestion) {
            if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                questions.setBgImg(mainCourseCardClass.getBgImg());
            }
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    //course module
    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setVideoOverlay(boolean videoOverlay) {
        isVideoOverlay = videoOverlay;
    }

    private boolean isVideoOverlay() {
        return isVideoOverlay;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    private boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public void setCourseData(CourseDataClass courseDataClass) {
        try {
            this.courseDataClass = courseDataClass;
            this.courseId = "" + courseDataClass.getCourseId();
            this.courseName = "" + courseDataClass.getCourseName();
            this.zeroXpForQCard = courseDataClass.isZeroXpForQCard();
            this.isQuestionImageShown = courseDataClass.isShowQuestionSymbolForQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setBgImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setFavouriteCardList(List<FavCardDetails> favouriteCardList) {
        this.favouriteCardList = favouriteCardList;
        if (this.favouriteCardList == null) {
            this.favouriteCardList = new ArrayList<>();
        }
    }

    public void setQuestionTTSEnabled(boolean isQuestionTTSEnabled) {
        this.isQuestionTTSEnabled = isQuestionTTSEnabled;
    }

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;

    }

    public void setCourseCompleted(boolean isCourseCompleted) {
        this.isCourseCompleted = isCourseCompleted;
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;
    }

    //assessment and survey
    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    public void setShowNavigateArrow(boolean showNavigateArrow) {
        this.showNavigateArrow = showNavigateArrow;
    }

    public void setAssessmentQuestion(boolean isAssessmentQuestion) {
        this.isAssessmentQuestion = isAssessmentQuestion;
    }

    public void setSurveyQuestion(boolean isSurveyQuestion) {
        this.isSurveyQuestion = isSurveyQuestion;
    }

    public void setAssessmentScore(Scores score) {
        this.score = score;
    }

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;

    }

    public void setAssessmentContentHandler(AssessmentContentHandlingInterface assessmentContentHandler) {
        this.assessmentContentHandler = assessmentContentHandler;
    }

    private void setIconColors(int color, boolean isEnabled) {
        Drawable actionDrawable = Objects.requireNonNull(requireActivity()).getResources().getDrawable(R.drawable.button_rounded_ten_bg);
        DrawableCompat.setTint(
                DrawableCompat.wrap(actionDrawable),
                color
        );

        question_action_button.setBackground(actionDrawable);
        question_action_button.setEnabled(isEnabled);
    }


    public void setData(QuestionBaseModel questionBaseModel) {

        try {
            if (isSurveyQuestion && surveyFunctionsAndClicks != null) {
                surveyFunctionsAndClicks.enableSwipe(question_base_frame);
            }
            String currentQuestion = (questionBaseModel.getQuestionIndex() + 1) + "/" + questionBaseModel.getTotalCards();
            if (currentQuestion.contains("/")) {
                String[] spilt = currentQuestion.split("/");
                Spannable spannable = new SpannableString(currentQuestion);
                spannable.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), currentQuestion.length(), 0);
                question_count_num.setText(spannable);
            } else {
                question_count_num.setText(currentQuestion);
            }
            question_count_num.setVisibility(View.VISIBLE);
            if ((questionBaseModel.getBgImage() != null) && (!questionBaseModel.getBgImage().isEmpty())) {
                try {
                    backgroundImage = questionBaseModel.getBgImage();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
//            questions = questionBaseModel.getQuestions();
            setQuestionData(questions);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionData(DTOQuestions questions) {
        if (questions != null) {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.setQuestions(questions);
                if (isReviewMode) {
                    courseContentHandlingInterface.cancelTimer();
                    // input_card.setVisibility(View.GONE);
                    question_action_button.setVisibility(View.GONE);
                    limit_layout.setVisibility(View.GONE);
                    long_answer_loader.setIndicatorColor(color);
                    long_answer_loader.setTrackColor(getResources().getColor(R.color.gray));
                    long_answer_loader.setVisibility(VISIBLE);
                    setAnswerReviewMode();
                } else {
                    long_answer_loader.setVisibility(View.GONE);
                }
            }

            if (assessmentContentHandler != null) {
                if (isReviewMode) {
                    assessmentContentHandler.cancelTimerForReview();
                    assessmentContentHandler.showBottomBar();
                }
            }

            if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {
                //question.setText(questions.getQuestion());
                if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                    question_katex.setTextColor(getResources().getColor(R.color.primary_text));
                    question_katex.setTextColorString("#212121");
                    question_katex.setText(questions.getQuestion());
                    question_katex.setVisibility(View.VISIBLE);
                    question.setVisibility(View.GONE);
                } else {
                    question_katex.setVisibility(View.GONE);
                    if (questions.getQuestion().contains("<li>") || questions.getQuestion().contains("</li>") ||
                            questions.getQuestion().contains("<ol>") || questions.getQuestion().contains("</ol>") ||
                            questions.getQuestion().contains("<p>") || questions.getQuestion().contains("</p>")) {
                        question_description_webView.setVisibility(View.VISIBLE);
                        question.setVisibility(View.GONE);
                        question_description_webView.setBackgroundColor(Color.TRANSPARENT);
                        String text = OustSdkTools.getDescriptionHtmlFormat(questions.getQuestion());
                        final WebSettings webSettings = question_description_webView.getSettings();
                        // Set the font size (in sp).
                        webSettings.setDefaultFontSize(18);
                        question_description_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                    } else {
                        question.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            question.setText(Html.fromHtml(questions.getQuestion(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            question.setText(Html.fromHtml(questions.getQuestion()));
                        }
                    }
                }
            }
            if (questions.isMandatory() || isAssessmentQuestion || isCourseQuestion) {
                setIconColors(getResources().getColor(R.color.overlay_container), false);
            }
            if (isAssessmentQuestion && isReviewMode) {
                question_action_button.setVisibility(View.GONE);
                long_answer_et.setEnabled(false);
                long_answer_et.setHint(requireActivity().getString(R.string.no_response_provided_by_user));
                limit_layout.setVisibility(View.GONE);
            } else if (isExamMode && score != null && score.getAnswer() != null) {
                setIconColors(color, true);
            }

            if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                try {
                    Glide.with(Objects.requireNonNull(requireActivity())).load(questions.getBgImg())
                            .apply(new RequestOptions().override(720, 1280))
                            .into(question_bgImg);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                try {
                    Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage)
                            .apply(new RequestOptions().override(720, 1280))
                            .into(question_bgImg);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            maxWordsCount = questions.getMaxWordCount();
            if (maxWordsCount == 0) {
                maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
            }
            minWordsCount = questions.getMinWordCount();

            long_answer_et.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    long_answer_et.setHint("");
                }
            });

            long_answer_et.setOnClickListener(view -> long_answer_et.setHint(""));

            limit_text.setText(OustStrings.getString("words_left") + (maxWordsCount));

            long_answer_et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String str = OustSdkTools.getEmojiEncodedString(long_answer_et);
                    String[] words = str.split(" ");
                    if (words.length < maxWordsCount) {
                        limit_text.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                        if (words.length >= minWordsCount) {
                            setIconColors(color, true);
                            question_action_button.setEnabled(true);
                        } else {
                            setIconColors(color, false);
                            question_action_button.setEnabled(false);
                        }
                        if ((str.isEmpty())) {
                            limit_text.setText(OustStrings.getString("words_left") + (maxWordsCount));
                        } else {
                            limit_text.setText(OustStrings.getString("words_left") + (maxWordsCount - (words.length)));
                        }
                    } else {
                        setCharLimit(long_answer_et, long_answer_et.getText().length());
                        setIconColors(color, true);
                        question_action_button.setEnabled(true);
                        limit_text.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                        limit_text.setText(OustStrings.getString("words_left") + ": 0");
                    }
                }
            });

            if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                handleVideoQuestion(questions.getGumletVideoUrl());
                Log.d("checkGumletVideo", "LongQuestion: " + questions.getGumletVideoUrl());
            } else if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                handleVideoQuestion(questions.getqVideoUrl());
            } else {
                setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());
                expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));
            }
            question_action_button.setOnClickListener(v -> {
                question_action_button.setEnabled(false);
                setAnswerForLongAnswer(false, false);
                new Handler().postDelayed(() -> question_action_button.setEnabled(true), 2000);
            });
            if (score != null && score.getAnswer() != null) {
                if (isReviewMode) {
                    long_answer_et.setText(score.getAnswer());
                    long_answer_et.setEnabled(false);
                    limit_layout.setVisibility(View.GONE);
                } else if (isExamMode) {
                    long_answer_et.setText(score.getAnswer());
                    long_answer_et.setEnabled(true);
                    limit_layout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setAnswerReviewMode() {
        try {
            if (courseId != null && !courseId.isEmpty()) {
                String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
                ValueEventListener answerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                String longAnswer = (String) dataSnapshot.getValue();
                                if ((longAnswer != null) && (!longAnswer.isEmpty())) {
                                    long_answer_et.setText(OustSdkTools.getEmojiDecodedString(longAnswer));
                                    long_answer_et.setEnabled(false);
                                    limit_layout.setVisibility(View.GONE);
                                    long_answer_loader.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception e) {
                            long_answer_loader.setVisibility(View.GONE);
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        long_answer_loader.setVisibility(View.GONE);
                    }
                };
                OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(answerListener);
            } else {
                long_answer_loader.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            long_answer_loader.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setCharLimit(EditText justifyPopupEditText, int length) {
        try {
            InputFilter inputFilter = new InputFilter.LengthFilter(length);
            justifyPopupEditText.setFilters(new InputFilter[]{inputFilter});
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageQuestionImage(String url, String image) {
        try {
            if (url == null) {
                url = image;
            }
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
                if (!url.contains("oustlearn_")) {
                    url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
                }
                File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    question_image_lay.setVisibility(View.VISIBLE);
                    expand_icon.setVisibility(View.VISIBLE);
//                    question_image.setImageURI(uri);
                    Glide.with(Objects.requireNonNull(requireActivity())).load(uri)
                            .apply(new RequestOptions().override(720, 1280))
                            .into(question_image);
                } else {
                    if ((image != null) && (!image.isEmpty())) {
                        byte[] imageByte = Base64.decode(image, 0);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                        question_image_lay.setVisibility(View.VISIBLE);
                        expand_icon.setVisibility(View.VISIBLE);
//                        question_image.setImageBitmap(decodedByte);
                        Glide.with(Objects.requireNonNull(requireActivity())).load(decodedByte)
                                .apply(new RequestOptions().override(720, 1280))
                                .into(question_image);
                        try {
                            if (decodedByte == null) {
                                Glide.with(Objects.requireNonNull(requireActivity()))
                                        .asBitmap()
                                        .apply(new RequestOptions().override(720, 1280))
                                        .load(image).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                try {
                                                    question_image.setImageBitmap(resource);
                                                    Log.e(TAG, "Question Image loaded url " + resource + " file " + file);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Log.e(TAG, "Question Image exception url " + e.getMessage());
                                                }

                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gifZoomPopup(final Drawable gif) {
        try {
            @SuppressLint("InflateParams")
            View popUpView = Objects.requireNonNull(requireActivity()).getLayoutInflater().inflate(R.layout.gifzoom_popup, null);
            zoomImagePopup = OustSdkTools.createPopUp(popUpView);
            GifImageView gifImageView = popUpView.findViewById(R.id.mainzooming_img);
            gifImageView.setImageDrawable(gif);
            ImageButton imageCloseButton = popUpView.findViewById(R.id.zooming_imgclose_btn);
            final RelativeLayout zoomLayout = popUpView.findViewById(R.id.mainzoomimg_layout);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(400);
            scaleDownY.setDuration(400);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            zoomLayout.setVisibility(View.VISIBLE);
            zoomImagePopup.setOnDismissListener(zoomImagePopup::dismiss);
            imageCloseButton.setOnClickListener(view -> {
                ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                scaleDownX1.setDuration(350);
                scaleDownY1.setDuration(350);
                scaleDownX1.setInterpolator(new DecelerateInterpolator());
                scaleDownY1.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown1 = new AnimatorSet();
                scaleDown1.play(scaleDownX1).with(scaleDownY1);
                scaleDown1.start();
                scaleDown1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            });
            popUpView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ObjectAnimator scaleDownX12 = ObjectAnimator.ofFloat(zoomLayout, "scaleX", 1.0f, 0);
                    ObjectAnimator scaleDownY12 = ObjectAnimator.ofFloat(zoomLayout, "scaleY", 1.0f, 0);
                    scaleDownX12.setDuration(350);
                    scaleDownY12.setDuration(350);
                    scaleDownX12.setInterpolator(new DecelerateInterpolator());
                    scaleDownY12.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown12 = new AnimatorSet();
                    scaleDown12.play(scaleDownX12).with(scaleDownY12);
                    scaleDown12.start();
                    scaleDown12.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (zoomImagePopup.isShowing()) {
                                zoomImagePopup.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnswerForLongAnswer(boolean previousArrow, boolean nextArrow) {
        String str = OustSdkTools.getEmojiEncodedString(long_answer_et);
        String[] mWordLength = str.split(" ");
        if (isSurveyQuestion) {
            learningModuleInterface.setAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, true, 0);
            learningModuleInterface.gotoNextScreen();
        } else if (isAssessmentQuestion) {
            if (!str.isEmpty() && mWordLength.length >= questions.getMinWordCount() && mWordLength.length <= maxWordsCount) {
                learningModuleInterface.setAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", (int) mainCourseCardClass.getXp(), true, 0);
            } else {
                learningModuleInterface.setAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0);
            }
            if (!nextArrow && !previousArrow) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.gotoNextScreen();
                }
            } else if (previousArrow) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.gotoPreviousScreen();
                }
            } else {
                if (learningModuleInterface != null) {
                    learningModuleInterface.gotoNextScreen();
                }
            }
        } else if (isCourseQuestion) {
            addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(long_answer_et));
            if (!str.isEmpty() && mWordLength.length >= questions.getMinWordCount() && mWordLength.length <= maxWordsCount) {
                if (questions.isThumbsUpDn()) {
                    rightAnswerSound();
                    animMainLayout.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(coinsAnimImg, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_result_image, "scaleX", 0.0f, 1);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_result_image, "scaleY", 0.0f, 1);
                    scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownX.setInterpolator(new DecelerateInterpolator());
                    scaleDownY.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();
                    scaleDown.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {

                            if (isVideoOverlay()) {
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.setVideoOverlayAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", (int) mainCourseCardClass.getXp(), true, 0, cardId);
                                    learningModuleInterface.closeChildFragment();
                                }
                            } else {
                                animateOcCoins(false);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                } else {
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setVideoOverlayAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", (int) mainCourseCardClass.getXp(), true, 0, cardId);
                            learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                        }
                    } else {
                        handleNextQuestion(OustSdkTools.getEmojiEncodedString(long_answer_et), "", (int) mainCourseCardClass.getXp(), true, 0);
                    }
                }

            } else {
                if (questions.isThumbsUpDn()) {
                    wrongAnswerSound();
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_result_image, "scaleX", 0.0f, 1);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_result_image, "scaleY", 0.0f, 1);
                    scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
                    scaleDownX.setInterpolator(new DecelerateInterpolator());
                    scaleDownY.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();
                    scaleDown.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (isVideoOverlay()) {
                                learningModuleInterface.setVideoOverlayAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0, cardId);
                                new Handler().postDelayed(() -> {
                                    try {
                                        if (isProceedOnWrong()) {
                                            if (learningModuleInterface != null) {
                                                learningModuleInterface.closeChildFragment();
                                            }
                                        } else {
                                            if (learningModuleInterface != null) {
                                                learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }, 1500);
                            } else {
                                showSolutionAnswer = false;
                                animateOcCoins(true);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                } else {
                    if (isVideoOverlay()) {
                        if (isProceedOnWrong()) {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.setVideoOverlayAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0, cardId);
                                learningModuleInterface.closeChildFragment();
                            }
                        } else {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.setVideoOverlayAnswerAndOc(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0, cardId);
                                learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                            }
                        }
                    } else {
                        showSolutionAnswer = false;
                        handleNextQuestion(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0);
                    }
                }
            }
        }
    }

    private void addAnswerOnFirebase(String answer) {
        if ((answer != null)) {
            String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
            OustFirebaseTools.getRootRef().child(node).setValue(answer);
        }
    }

    private void animateOcCoins(boolean status) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            Log.e(TAG, "animateOcCoins- isCourse: " + isCourseCompleted);
            if ((mainCourseCardClass.getXp() > 0)) {
                if (answerChosenPlayer == null) {
                    answerChosenPlayer = new MediaPlayer();
                }
                isLevelCompleted = isReplayMode || OustStaticVariableHandling.getInstance().isLevelCompleted();
                if (isCourseCompleted || isLevelCompleted) {
                } else {
                    playAudio("coins.mp3");
                }
            }
            if (isCourseCompleted || isLevelCompleted) {
                coinsAnimImg.setVisibility(View.GONE);
                coinsAnimText.setVisibility(View.GONE);
            }
            animator1.setObjectValues(0, (int) (mainCourseCardClass.getXp()));
            animator1.setDuration(SIX_HUNDRED_MILLI_SECONDS);
            animator1.addUpdateListener(valueAnimator -> {
                if (isCourseCompleted || isLevelCompleted) {
                    coinsAnimImg.setVisibility(View.GONE);
                    coinsAnimText.setVisibility(View.GONE);
                } else {
                    coinsAnimImg.setVisibility(View.VISIBLE);
                    coinsAnimText.setVisibility(View.VISIBLE);
                    coinsAnimText.setText("" + (((int) valueAnimator.getAnimatedValue())));
                }
            });
            animator1.start();
            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (isCourseCompleted || isLevelCompleted) {
                        coinsAnimImg.setVisibility(View.GONE);
                        coinsAnimText.setVisibility(View.GONE);
                    } else {
                        coinsAnimImg.setVisibility(View.VISIBLE);
                        coinsAnimText.setVisibility(View.VISIBLE);
                        coinsAnimText.setText("" + mainCourseCardClass.getXp());
                    }
                    if (!isVideoOverlay()) {
                        if (status) {
                            handleNextQuestion(OustSdkTools.getEmojiEncodedString(long_answer_et), "", 0, false, 0);
                        } else {
                            handleNextQuestion(OustSdkTools.getEmojiEncodedString(long_answer_et), "", (int) mainCourseCardClass.getXp(), true, 0);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNextQuestion(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            new Handler().postDelayed(() -> {
                animMainLayout.setVisibility(View.GONE);
                question_result_image.setVisibility(View.GONE);
                if (!isVideoOverlay()) {
                    if (solutionText != null && !solutionText.isEmpty()) {
                        if (showSolution) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, true, userAns, subjectiveResponse, oc, status, time);
                            }
                        } else if (!showSolutionAnswer) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.showSolutionPopUP(solutionText, false, false, true, userAns, subjectiveResponse, oc, status, time);
                            }
                        } else {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.handleScreenTouchEvent(true);
                                courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                            }
                        }
                    } else {
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.handleScreenTouchEvent(true);
                            courseContentHandlingInterface.setAnswerAndOCRequest(userAns, subjectiveResponse, oc, status, time);
                        }
                    }
                    showSolutionAnswer = true;
                }
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void vibrateAndShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            question_base_frame.startAnimation(shakeAnim);
            ((Vibrator) Objects.requireNonNull(requireActivity()).getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongAnswerSound() {
        try {
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAnswerSound() {
        try {
            playAudio("answer_correct.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAudio(final String filename) {
        try {
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            tempMp3.deleteOnExit();
            //cancelSound();
            answerChosenPlayer = new MediaPlayer();
            answerChosenPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            answerChosenPlayer.setDataSource(fis.getFD());
            answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            answerChosenPlayer.prepare();
            answerChosenPlayer.setOnPreparedListener(LongQuestionFragment::onPrepared);
            answerChosenPlayer.start();
            answerChosenPlayer.setOnCompletionListener(LongQuestionFragment::onCompletion);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void onCompletion(MediaPlayer mediaPlayer) {
    }

    private static void onPrepared(MediaPlayer mediaPlayer) {
    }

    public void handleVideoQuestion(String videoUrl) {
        try {
            video_lay.setVisibility(View.VISIBLE);
            question.setVisibility(View.GONE);
            input_card.setVisibility(View.GONE);
            question_action_button.setVisibility(View.GONE);
            info_type.setText(getResources().getString(R.string.assessment_video_start_msg));


            if (!isVideoPlaying) {
                isVideoPlaying = true;
                customExoPlayerView = new CustomExoPlayerView() {
                    @Override
                    public void onAudioComplete() {

                    }

                    @Override
                    public void onVideoComplete() {
                        new Handler().postDelayed(() -> {
                            question.setVisibility(View.VISIBLE);
                            input_card.setVisibility(View.VISIBLE);
                            question_action_button.setVisibility(View.VISIBLE);
                            if (!isSurveyQuestion && !isVideoCompleted) {
                                try {
                                    isVideoCompleted = true;
                                    CustomVideoControlListener customVideoControlListener = (CustomVideoControlListener) context;
                                    customVideoControlListener.onVideoEnd();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            }
                        }, 400);

                    }

                    @Override
                    public void onBuffering() {
                    }

                    @Override
                    public void onVideoError() {
                    }

                    @Override
                    public void onPlayReady() {
                    }

                };
                finalVideoUrl = videoUrl;
                checkVideoExist(finalVideoUrl);
                setPlayerForPortrait(customExoPlayerView.getSimpleExoPlayerView());
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void checkVideoExist(String url) {
        try {
            String videoFileName = OustMediaTools.getMediaFileName(OustMediaTools.removeAwsOrCDnUrl(url));
            String rootPath = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
            Log.d(TAG, "checkVideoMediaExist:-LONG- path:" + rootPath);
            File rootFile = new File(rootPath);
            if (rootFile.exists()) {
                customExoPlayerView.initExoPlayerWithFile(media_question_container, context, rootFile);
            } else {
                customExoPlayerView.initExoPlayer(media_question_container, context, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPlayerForPortrait(StyledPlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                media_question_container.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setPlayerForLandScape(StyledPlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                params.height = scrHeight;
                params.width = scrWidth;
                simpleExoPlayerView.setLayoutParams(params);
                media_question_container.setLayoutParams(params);

                ((CustomVideoControlListener) OustSdkApplication.getContext()).hideToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        try {
            if (customExoPlayerView != null && customExoPlayerView.getSimpleExoPlayerView() != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPlayerForLandScape(customExoPlayerView.getSimpleExoPlayerView());
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPlayerForPortrait(customExoPlayerView.getSimpleExoPlayerView());
                }
            } else
                super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
            resetPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            isVideoPaused = true;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            questionLoaded = true;
            resetPlayer();
        } catch (Exception e) {
            questionLoaded = true;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            try {
                if (finalVideoUrl != null && isVideoPlaying) {
                    if (customExoPlayerView != null) {
                        isPausedProgrammatically = true;
                        customExoPlayerView.performPauseclick();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isVideoPaused) {
                resumeVideoPlayer();
                isVideoPaused = false;
            } else {
                if (isPausedProgrammatically && customExoPlayerView != null) {
                    isPausedProgrammatically = false;
                    customExoPlayerView.performPlayClick();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resumeVideoPlayer() {
        try {
            if (isPausedProgrammatically && customExoPlayerView != null) {
                isPausedProgrammatically = false;
                customExoPlayerView.performPlayClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resetPlayer() {
        try {
            if (customExoPlayerView != null) {
                customExoPlayerView.removeAudioPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void pauseVideoPlayer() {
        try {
            if (customExoPlayerView != null) {
                customExoPlayerView.pausePlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }
}
