package com.oustme.oustsdk.question_module.fragment;

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
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.QuestionAnswerAdapter;
import com.oustme.oustsdk.question_module.adapter.QuestionOnItemClickListener;
import com.oustme.oustsdk.question_module.adapter.ReviewAnswerGridAdapter;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.model.QuestionAnswerModel;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class MCQuestionFragment extends Fragment implements QuestionOnItemClickListener {

    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    RelativeLayout video_lay;
    RelativeLayout media_question_container;
    TextView question;
    WebView question_description_webView;
    KatexView question_katex;
    LinearLayout question_image_lay;
    ImageView question_image;
    TextView info_type;
    ImageView expand_icon;
    RecyclerView question_answer_rv;
    FrameLayout question_action_button;

    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;

    //justify popup
    ConstraintLayout justifyPopupConstraintLayout;
    TextView justifyPopupHeader;
    EditText justifyPopupEditText;
    TextView justifyPopupLimitText;
    FrameLayout justifyPopupActionButton;
    CardView justifyCardView;
    ImageView justifyPopupCloseIcon;
    private int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    private int minWordsCount = 0;


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
    boolean isLevelCompleted;
    boolean isReviewMode;
    boolean isReplayMode;
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
    boolean isVideoPlaying;
    boolean isSurveyQuestion;
    String solutionText;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;
    boolean containSubjective = false;
    private Scores score;
    private boolean isCourseQuestionIsRight = false;

    boolean videoOverlay;
    boolean proceedOnWrong;
    int finalScr = 0;

    ArrayList<QuestionAnswerModel> answerModels = new ArrayList<>();
    String answerType;
    QuestionAnswerAdapter adapter;

    int totalOption = 0;
    SurveyFunctionsAndClicks surveyFunctionsAndClicks;

    CustomExoPlayerView customExoPlayerView;
    Context context;
    PopupWindow zoomImagePopup;
    DTOCourseSolutionCard courseSolutionCard;
    String finalVideoUrl;
    MediaPlayer answerChosenPlayer;

    private boolean isVideoPaused = false;
    private boolean isPausedProgrammatically = false;
    private GridView answerGridView;
    private CardView answerGridLayout;
    boolean questionLoaded = false;
    ArrayList<String> showAnswers = new ArrayList<>();

    public MCQuestionFragment() {
        // Required empty public constructor
    }

    public static MCQuestionFragment newInstance() {
        return new MCQuestionFragment();
    }

    private static void onPrepared(MediaPlayer mp) {

    }

    private static void onCompletion(MediaPlayer mediaPlayer) {

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
        View view = inflater.inflate(R.layout.fragment_m_c_question, container, false);

        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        video_lay = view.findViewById(R.id.video_lay);
        media_question_container = view.findViewById(R.id.media_question_container);
        question = view.findViewById(R.id.question);
        question_description_webView = view.findViewById(R.id.description_webView);
        question_katex = view.findViewById(R.id.question_katex);
        question_image_lay = view.findViewById(R.id.question_image_lay);
        question_image = view.findViewById(R.id.question_image);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        question_answer_rv = view.findViewById(R.id.question_answer_rv);
        question_action_button = view.findViewById(R.id.question_action_button);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);

        justifyPopupConstraintLayout = view.findViewById(R.id.justify_popup_constraint_layout);
        justifyPopupEditText = view.findViewById(R.id.justify_popup_edit_text);
        justifyPopupHeader = view.findViewById(R.id.justify_popup_header);
        justifyPopupLimitText = view.findViewById(R.id.justify_popup_limit_text);
        justifyPopupActionButton = view.findViewById(R.id.justify_popup_button);
        justifyCardView = view.findViewById(R.id.justify_popup_cardView);
        justifyPopupCloseIcon = view.findViewById(R.id.justify_popup_close_button);
        answerGridView = view.findViewById(R.id.answer_grid_view);
        answerGridLayout = view.findViewById(R.id.answer_grid_layout);

        if (isVideoOverlay()) {
            question_base_frame.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        setButtonColor(getResources().getColor(R.color.overlay_container), false);
        info_type.setText(getResources().getString(R.string.mcq_info));
        info_type.setVisibility(View.VISIBLE);
        if (isCourseQuestion) {
            setQuestionData(questions);
        } else {
            try {
                if (questionBaseViewModel != null) {
                    questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
                        if (questionBaseModel == null)
                            return;
                        this.questionBaseModel = questionBaseModel;
                        if (questionBaseModel.getQuestions() != null && questionBaseModel.getQuestions().getQuestionType() != null &&
                                questionBaseModel.getQuestions().getQuestionType().equalsIgnoreCase(QuestionType.MCQ)) {
                            if (!questionLoaded) {
                                setData(questionBaseModel);
                                questionLoaded = true;
                            }
                        } else if (questionBaseModel.getQuestions() != null && questionBaseModel.getQuestions().getQuestionType() != null &&
                                questionBaseModel.getQuestions().getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE)) {
                            if (!questionLoaded) {
                                setData(questionBaseModel);
                                questionLoaded = true;
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
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
            courseSolutionCard = courseCardClass.getChildCard();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }

                if (mainCourseCardClass.getXp() == 0) {
                    mainCourseCardClass.setXp(100);
                }

                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }
                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
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

        if (isCourseQuestion) {
            if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                questions.setBgImg(mainCourseCardClass.getBgImg());
            }
            setAnswerModels();
        } else if (isAssessmentQuestion) {
            mainCourseCardClass.setXp(questionXp);
        }
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    //course module
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

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setVideoOverlay(boolean videoOverlay) {
        this.videoOverlay = videoOverlay;
    }

    private boolean isVideoOverlay() {
        return videoOverlay;
    }

    private boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
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

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    public void setAssessmentContentHandler(AssessmentContentHandlingInterface assessmentContentHandler) {
        this.assessmentContentHandler = assessmentContentHandler;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setButtonColor(int color, boolean isEnabled) {
        try {
            Drawable actionDrawable = Objects.requireNonNull(requireActivity()).getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(actionDrawable),
                    color
            );
            question_action_button.setBackground(actionDrawable);
            question_action_button.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setJustifyPopupButtonColor(int color, boolean isEnabled) {
        try {
            Drawable actionDrawable = Objects.requireNonNull(requireActivity()).getResources().getDrawable(R.drawable.button_rounded_ten_bg);
            DrawableCompat.setTint(
                    DrawableCompat.wrap(actionDrawable),
                    color
            );
            justifyPopupActionButton.setBackground(actionDrawable);
            justifyPopupActionButton.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnswerModels() {
        try {
            if (questions != null) {
                answerType = "Text";
                if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.IMAGE_CHOICE)) {
                    answerType = "Image";
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if (questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setImage(questions.getImageChoiceA().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setImage(questions.getImageChoiceB().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setImage(questions.getImageChoiceC().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setImage(questions.getImageChoiceD().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceE() != null && questions.getImageChoiceE().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setImage(questions.getImageChoiceE().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.IMAGE_TEXT)) {
                    answerType = "Image_Text";
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if (questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setImage(questions.getImageChoiceA().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setImage(questions.getImageChoiceB().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setImage(questions.getImageChoiceC().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setImage(questions.getImageChoiceD().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceE() != null && questions.getImageChoiceE().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setImage(questions.getImageChoiceE().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if ((questions.getF() != null) && (!questions.getF().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("F");
                        answerModel.setAnswerOption(questions.getF());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                } else {
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getF() != null) && (!questions.getF().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("F");
                        answerModel.setAnswerOption(questions.getF());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(QuestionBaseModel questionBaseModel) {

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
                    Glide.with(Objects.requireNonNull(requireActivity())).load(questionBaseModel.getBgImage())
                            .apply(new RequestOptions().override(720, 1280))
                            .into(question_bgImg);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (questionBaseModel.getAnswerType() != null) {
                answerType = questionBaseModel.getAnswerType();
            }
            if (questionBaseModel.getAnswerModels() != null) {
                answerModels = questionBaseModel.getAnswerModels();
            }
//            questions = questionBaseModel.getQuestions();
            setQuestionData(questionBaseModel.getQuestions());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionData(DTOQuestions questions) {
        try {
            if (questions != null) {
                this.questions = questions;
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.setQuestions(questions);
                    if (isReviewMode) {
                        courseContentHandlingInterface.cancelTimer();
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
                        if (questions.getQuestion().contains("<math")) {
                            question_description_webView.setVisibility(View.VISIBLE);
                            question.setVisibility(View.GONE);
                            question_description_webView.setBackgroundColor(Color.TRANSPARENT);
                            OustSdkTools.getSpannedMathmlContent(questions.getQuestion(), question_description_webView, true);
                        } else if (questions.getQuestion().contains("<li>") || questions.getQuestion().contains("</li>") ||
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
                        } else if (questions.getQuestion().contains("<img")) {
                            OustSdkTools.getSpannedMathmlContent(questions.getQuestion(), question_description_webView, true);
                            question_description_webView.setVisibility(View.VISIBLE);
                            question.setVisibility(View.GONE);
                            question_description_webView.setBackgroundColor(Color.TRANSPARENT);
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
                if (isCourseQuestion || isReviewMode) {
                    question_action_button.setVisibility(View.GONE);
                } else if (isExamMode && score != null && score.getAnswer() != null) {
                    setButtonColor(color, true);
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
                if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                    handleVideoQuestion(questions.getGumletVideoUrl());
                    Log.d("checkGumletVideo", "Mcq Question: " + questions.getGumletVideoUrl());
                } else if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                    handleVideoQuestion(questions.getqVideoUrl());
                } else {
                    setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());
                    expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));
                }
                if (answerType != null && answerType.equalsIgnoreCase("Image")) {
                    question_answer_rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                } else {
                    question_answer_rv.setItemAnimator(new DefaultItemAnimator());
                    question_answer_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                if (answerModels != null && answerModels.size() != 0) {
                    adapter = new QuestionAnswerAdapter();
                    totalOption = answerModels.size();
                    adapter.setClickEvent(this);
                    adapter.setCourseQuestion(isCourseQuestion, isReviewMode);
                    adapter.setAnswer(questions.getAnswer());
                    if (isRandomizeQuestion) {
                        Collections.shuffle(answerModels, new Random());
                    }
                    adapter.setQuestionAnswerModels(answerModels, getActivity(), answerType, score);
                    question_answer_rv.setAdapter(adapter);


                    if (isAssessmentQuestion && isReviewMode && score != null && score.getAnswer() != null) {
                        if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                            answerGridLayout.setVisibility(View.GONE);
                            answerGridView.setVisibility(View.GONE);
                        } else if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                            answerGridLayout.setVisibility(View.GONE);
                            answerGridView.setVisibility(View.GONE);
                        } else {
                            answerGridLayout.setVisibility(View.VISIBLE);
                            answerGridView.setVisibility(View.VISIBLE);
                        }
                        String mPowerAnswerOption = "";
                        showAnswers.clear();
                        for (int i = 0; i < answerModels.size(); i++) {
                            try {
                                if (OustSdkTools.getSpannedContentText(answerModels.get(i).getAnswerOption()).replace(" ", "").equalsIgnoreCase(OustSdkTools.getSpannedContentText(score.getAnswer()).replace(" ", ""))) {
                                    if (!showAnswers.contains(answerModels.get(i).getOption()))
                                        showAnswers.add(answerModels.get(i).getOption());
                                    Log.e("TAG", "setQuestionData: splitAnswers--> " + showAnswers);
                                }
                                if (answerModels.get(i).getAnswerOption().equalsIgnoreCase(questions.getAnswer())) {
                                    mPowerAnswerOption = answerModels.get(i).getOption();
                                    Log.e("TAG", "setQuestionData: splitAnswers<--> " + mPowerAnswerOption);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                        //MCQ and MRQ handling is same, so passing isMRQ flag as true.
                        ReviewAnswerGridAdapter answerGridAdapter = new ReviewAnswerGridAdapter(getContext(), showAnswers, mPowerAnswerOption, score, true);
                        answerGridView.setAdapter(answerGridAdapter);

                    } else {
                        answerGridLayout.setVisibility(View.GONE);
                        answerGridView.setVisibility(View.GONE);
                    }


                    question_action_button.setOnClickListener(v -> {
                        question_action_button.setEnabled(false);
                        setAnswers(1, false, false);
                        new Handler().postDelayed(() -> question_action_button.setEnabled(true), 2000);
                    });
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setAnswers(int count, boolean previousArrow, boolean nextArrow) {
        try {
            isCourseQuestionIsRight = count <= 1;
            finalScr = (int) (questionXp / count);
            int position = OustPreferences.getSavedInt("QuestionAnswerPosition");
            if (position != -1) {
                String answer;
                answer = answerModels.get(OustPreferences.getSavedInt("QuestionAnswerPosition")).getAnswerOption();
                if (!answer.isEmpty()) {
                    OustPreferences.saveintVar("QuestionAnswerPosition", -1);
                    if (isSurveyQuestion) {
                        learningModuleInterface.setAnswerAndOc(answer, "", 0, true, 0);
                        if (questions.isExitable()) {
                            String message = (questions.getExitMessage() != null && !questions.getExitMessage().equalsIgnoreCase("false")) ? questions.getExitMessage() : "Thank you for taking today's survey";
                            if (questions.getExitOption() != null && questions.getExitOption().equalsIgnoreCase(answer)) {
                                learningModuleInterface.onSurveyExit(message);
                            } else {
                                learningModuleInterface.gotoNextScreen();
                            }
                        } else {
                            learningModuleInterface.gotoNextScreen();
                        }
                    } else if (isAssessmentQuestion) {
                        if (questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer)) {
                            learningModuleInterface.setAnswerAndOc(answer, "", (int) mainCourseCardClass.getXp(), true, 0);
                        } else {
                            learningModuleInterface.setAnswerAndOc(answer, "", 0, false, 0);
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
                        if (questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer)) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.cancelTimer();
                            }
                            if ((questions.isContainSubjective())) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.disableNextArrow();
                                }
                                showSubjectiveQuestionPopup(answer);
                            } else {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.handleScreenTouchEvent(false);
                                }
                                if (questions.isThumbsUpDn()) {
                                    rightAndWrongQuestionAnswer(questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer), "");
                                } else {
                                    withOutThumbsUpDn(answer);
                                }
                            }
                        } else {
                            if (questions.isThumbsUpDn()) {
                                rightAndWrongQuestionAnswer(questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer), "");
                            } else {
                                vibrateAndShake();
                                wrongAnswerSound();
                                showSolutionAnswer = false;
                                showSolutionPopup(false);
                                if (isVideoOverlay()) {
                                    try {
                                        if (courseContentHandlingInterface != null) {
                                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                                        }
                                        new Handler().postDelayed(() -> {
                                            try {
                                                learningModuleInterface.closeChildFragment();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                OustSdkTools.sendSentryException(e);
                                            }
                                            learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", 0, false, 0, cardId);
                                            if (isProceedOnWrong()) {
                                                if (learningModuleInterface != null) {
                                                    learningModuleInterface.closeChildFragment();
                                                }
                                            } else {
                                                if (learningModuleInterface != null) {
                                                    learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                                                }
                                            }
                                        }, 500);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (isAssessmentQuestion) {
                    learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
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
                    if (isVideoOverlay()) {
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                        }
                        vibrateAndShake();
                        wrongAnswerSound();
                        showSolutionAnswer = false;
                        showSolutionPopup(false);
                        question_result_image.setVisibility(View.VISIBLE);
                        OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                        learningModuleInterface.setVideoOverlayAnswerAndOc("", "", 0, false, 0, cardId);
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
                        }, 500);
                    } else {
                        vibrateAndShake();
                        wrongAnswerSound();
                        question_result_image.setVisibility(View.VISIBLE);
                        OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                        showSolutionAnswer = false;
                        handleNextQuestion("", "", 0, false, 0);
                    }
                }
            }

            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void withOutThumbsUpDn(String answer) {
        try {
            if (questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer)) {
                if (isVideoOverlay()) {
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.handleScreenTouchEvent(false);
                    }
                    learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", finalScr, true, 0, cardId);
                    learningModuleInterface.closeChildFragment();
                } else {
                    handleNextQuestion(questions.getAnswer(), "", finalScr, isCourseQuestionIsRight, 0);
                }
            } else {
                vibrateAndShake();
                wrongAnswerSound();
                showSolutionAnswer = false;
                showSolutionPopup(false);
                if (isVideoOverlay()) {
                    try {
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                        }
                        learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", 0, false, 0, cardId);
                        if (isProceedOnWrong()) {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.closeChildFragment();
                            }
                        } else {
                            if (learningModuleInterface != null) {
                                new Handler().postDelayed(() -> learningModuleInterface.wrongAnswerAndRestartVideoOverlay(), 500);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else {
                    handleNextQuestion(questions.getAnswer(), "", finalScr, isCourseQuestionIsRight, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSubjectiveQuestionPopup(String answer) {
        try {
            justifyPopupConstraintLayout.setVisibility(View.VISIBLE);
            learningModuleInterface.disableBackButton(true);

            if ((questions.getSubjectiveQuestion() != null) && (!questions.getSubjectiveQuestion().isEmpty())) {
                justifyPopupHeader.setText(questions.getSubjectiveQuestion());
                justifyPopupHeader.setVisibility(View.VISIBLE);
            } else {
                justifyPopupHeader.setText(OustStrings.getString("explain_your_rationale"));
            }
            maxWordsCount = questions.getMaxWordCount();
            if (maxWordsCount == 0) {
                maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
            }
            minWordsCount = questions.getMinWordCount();
            if (minWordsCount == 0) {
                setJustifyPopupButtonColor(getResources().getColor(R.color.overlay_container), false);
            }

            justifyPopupEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    justifyPopupEditText.setHint("");
                }
            });
            justifyPopupEditText.setOnClickListener(view -> justifyPopupEditText.setHint(""));

            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount));
            justifyPopupEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String str = OustSdkTools.getEmojiEncodedString(justifyPopupEditText);
                    String[] words = str.split(" ");
                    if (words.length < maxWordsCount) {
                        justifyPopupLimitText.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                        if (words.length >= minWordsCount) {
                            setJustifyPopupButtonColor(color, true);
                        } else {
                            setJustifyPopupButtonColor(getResources().getColor(R.color.overlay_container), false);
                        }
                        if ((str.isEmpty())) {
                            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount));
                        } else {
                            justifyPopupLimitText.setText(OustStrings.getString("words_left") + (maxWordsCount - (words.length)));
                        }
                    } else {
                        setCharLimit(justifyPopupEditText, justifyPopupEditText.getText().length());
                        setJustifyPopupButtonColor(color, true);
                        justifyPopupLimitText.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                        justifyPopupLimitText.setText(OustStrings.getString("words_left") + ": 0");
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            justifyPopupActionButton.setOnClickListener(view -> {
                try {
                    String str = OustSdkTools.getEmojiEncodedString(justifyPopupEditText);
                    String[] words = str.split(" ");
                    if ((words.length < minWordsCount)) {
                    } else if ((words.length <= maxWordsCount)) {
                        learningModuleInterface.disableBackButton(false);
                        justifyPopupConstraintLayout.setVisibility(View.GONE);
                        hideKeyboard(justifyPopupEditText);
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                        }
                        if (questions.isThumbsUpDn()) {
                            rightAndWrongQuestionAnswer(questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer), OustSdkTools.getEmojiEncodedString(justifyPopupEditText));
                        } else {
                            withOutThumbsUpDn(answer);
                        }
                        Log.e("TAG", "showSubjectiveQuestionPopup: " + justifyPopupEditText.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

            justifyPopupCloseIcon.setOnClickListener(view -> {
                try {
                    learningModuleInterface.disableBackButton(false);
                    justifyPopupConstraintLayout.setVisibility(View.GONE);
                    hideKeyboard(justifyPopupEditText);
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.handleScreenTouchEvent(false);
                    }
                    if (questions.isThumbsUpDn()) {
                        rightAndWrongQuestionAnswer(questions.getAnswer() != null && questions.getAnswer().equalsIgnoreCase(answer), "");
                    } else {
                        withOutThumbsUpDn(answer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });

        } catch (Exception e) {
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

    public void hideKeyboard(View v) {
        try {
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAndWrongQuestionAnswer(boolean status, String emojiEncodedString) {
        if (status) {
            rightAnswerSound();
            Log.e("TAG", "rightAndWrongQuestionAnswer: " + emojiEncodedString);
            if (isVideoOverlay()) {
                question_result_image.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), emojiEncodedString, finalScr, true, 0, cardId);
                new Handler().postDelayed(() -> {
                    try {
                        learningModuleInterface.closeChildFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 1500);
            }
            animMainLayout.setVisibility(View.VISIBLE);
            if (animMainLayout.getVisibility() == View.VISIBLE) {
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
                        if (zeroXpForQCard) {
                            if (!isVideoOverlay()) {
                                handleNextQuestion(questions.getAnswer(), emojiEncodedString, finalScr, isCourseQuestionIsRight, 0);
                            }
                        } else {
                            animateOcCoins(questions.getAnswer(), emojiEncodedString, finalScr, isCourseQuestionIsRight, 0);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                try {
                    if (OustSdkTools.textToSpeech != null) {
                        OustSdkTools.stopSpeech();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            }
        } else {
            vibrateAndShake();
            wrongAnswerSound();
            showSolutionAnswer = false;
            showSolutionPopup(false);
            if (isVideoOverlay()) {
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.handleScreenTouchEvent(false);
                }
                question_result_image.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", 0, false, 0, cardId);
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
                }, 500);
            }
        }
    }

    private void animateOcCoins(String answer, String emojiEncodedString, int finalScr, boolean isCourseQuestionIsRight, int time) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            if ((this.finalScr > 0)) {
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
            Log.e("TAG", "animateOcCoins: isLevelCompleted-->  " + isReplayMode + " isCourseCompleted-> " + isCourseCompleted);
            animator1.setObjectValues(0, (this.finalScr));
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
                        coinsAnimText.setText("" + MCQuestionFragment.this.finalScr);
                    }
                    if (!isVideoOverlay()) {
                        handleNextQuestion(answer, emojiEncodedString, finalScr, isCourseQuestionIsRight, time);
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

    private void stopMediaPlayer() {
        try {
            if (answerChosenPlayer == null) {
                answerChosenPlayer = new MediaPlayer();
            }
            if (answerChosenPlayer.isPlaying()) {
                answerChosenPlayer.stop();
            }
            answerChosenPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSolutionPopup(boolean showSolutionAnswer) {
        try {
            if (solutionText != null && !solutionText.isEmpty()) {
                if (!showSolution && !showSolutionAnswer) {
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.handleScreenTouchEvent(true);
                        courseContentHandlingInterface.showSolutionPopUP(solutionText, false, true, false, "", "", 0, false, 0);
                    }
                } else if (showSolution) {
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.handleScreenTouchEvent(true);
                        courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, true, false, "", "", 0, false, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNextQuestion(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            new Handler().postDelayed(() -> {
                animMainLayout.setVisibility(View.GONE);
                if (isCourseQuestion) {
                    if (!isVideoOverlay()) {
                        if (solutionText != null && !solutionText.isEmpty()) {
                            if (showSolution) {
                                if (courseContentHandlingInterface != null) {
                                    courseContentHandlingInterface.handleScreenTouchEvent(true);
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, true, userAns, subjectiveResponse, oc, status, time);
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
                    }
                } else {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoNextScreen();
                    }
                }
                showSolutionAnswer = true;
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleVideoQuestion(String videoUrl) {
        try {
            video_lay.setVisibility(View.VISIBLE);
            question.setVisibility(View.GONE);
            question_answer_rv.setVisibility(View.GONE);
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
                            question_answer_rv.setVisibility(View.VISIBLE);
                            if (isAssessmentQuestion && isReviewMode) {
                                answerGridLayout.setVisibility(View.VISIBLE);
                                answerGridView.setVisibility(View.VISIBLE);
                            }
                            if (!isCourseQuestion)
                                question_action_button.setVisibility(View.VISIBLE);
                            info_type.setText(getResources().getString(R.string.mcq_info));
                            if (!isSurveyQuestion && !isVideoCompleted) {
                                try {
                                    isVideoCompleted = true;
                                    if (!isReviewMode) {
                                        CustomVideoControlListener customVideoControlListener = (CustomVideoControlListener) context;
                                        customVideoControlListener.onVideoEnd();
                                    }
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
//                Log.e("TAG", "handleVideoQuestion: Video Exist-1-> ");
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
            Log.d("TAG", "checkVideoMediaExist:-MCQ- path:" + rootPath);
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
                                                    Log.e("MCQuestion", "Question Image loaded url " + resource + " file " + file);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Log.e("MCQuestion", "Question Image exception url " + e.getMessage());
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


    @Override
    public void onItemClicked(int count) {
        try {
            if (isCourseQuestion) {
                setAnswers(count, false, false);
            } else {
                if (count > 0) {
                    setButtonColor(color, true);
                } else {
                    setButtonColor(getResources().getColor(R.color.overlay_container), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onAnswerOc(int count) {
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
            stopMediaPlayer();
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAnswerSound() {
        try {
            stopMediaPlayer();
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
            answerChosenPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            answerChosenPlayer.setDataSource(fis.getFD());
            answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            answerChosenPlayer.prepare();
            answerChosenPlayer.setOnPreparedListener(MCQuestionFragment::onPrepared);
            answerChosenPlayer.start();
            answerChosenPlayer.setOnCompletionListener(MCQuestionFragment::onCompletion);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            if (customExoPlayerView != null && customExoPlayerView.getSimpleExoPlayerView() != null && learningModuleInterface != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPlayerForLandScape(customExoPlayerView.getSimpleExoPlayerView());
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPlayerForPortrait(customExoPlayerView.getSimpleExoPlayerView());
                }
            } else {
                if (learningModuleInterface != null) {
                    learningModuleInterface.changeOrientationUnSpecific();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            questionLoaded = true;
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
            if (adapter != null)
                adapter.closePopWindow();
            resetPlayer();
        } catch (Exception e) {
            questionLoaded = true;
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
            if (adapter != null) {
                adapter.closePopWindow();
            }
            isVideoPaused = true;
//            resetPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            questionLoaded = true;
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            if (adapter != null)
                adapter.closePopWindow();

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
            if (adapter != null)
                adapter.closePopWindow();

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
                customExoPlayerView.onPlayReady();
                customExoPlayerView.pausePlayer();
                customExoPlayerView.releasePlayer();
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
