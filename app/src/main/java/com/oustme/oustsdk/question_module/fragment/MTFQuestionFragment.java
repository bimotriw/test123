package com.oustme.oustsdk.question_module.fragment;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
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

import android.view.WindowManager;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.MTFAnswerAdapter;
import com.oustme.oustsdk.question_module.adapter.MTFAnswerCheck;
import com.oustme.oustsdk.question_module.adapter.MTFSelectedAnswerAdapter;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.dragger.OnStartDragListener;
import com.oustme.oustsdk.question_module.dragger.SimpleItemTouchHelperCallback;
import com.oustme.oustsdk.question_module.model.MTFSelectedAnswer;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MTFQuestionFragment extends Fragment implements OnStartDragListener, MTFAnswerCheck {

    String TAG = "MTFQuestion";
    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    TextView question;
    LinearLayout question_image_lay;
    ImageView question_image;
    TextView info_type;
    ImageView expand_icon;
    ImageView question_result_image;
    RecyclerView answer_recyclerview;
    RecyclerView options_recyclerview;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;
    KatexView question_katex;
    WebView question_description_webView;
    TextView insturaction_text;
    String solutionText;
    boolean showSolutionAnswer = true;
    boolean containSubjective = false;
    boolean showSolution = false;

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    int questionNo;
    boolean isReviewMode;
    boolean isExamMode;
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
    boolean isCourseQuestion;
    boolean isCourseQuestionIsRight = true;

    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;
    boolean videoOverlay;

    //additional parameters
    int color;
    boolean showNavigateArrow;
    boolean isAssessmentQuestion;
    boolean isSurveyQuestion;
    boolean isReplayMode;
    boolean isLevelCompleted;
    private Scores score;
    private int cardCount;
    private boolean previousArrow;
    private boolean nextArrow;
    MediaPlayer answerChosenPlayer;

    //View Model from Assessment and survey
    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;
    AssessmentContentHandlingInterface assessmentContentHandler;

    int totalOption = 0;
    int scrWidth, scrHeight;

    List<DTOMTFColumnData> allList = new ArrayList<>();
    List<DTOMTFColumnData> leftList = new ArrayList<>();
    List<DTOMTFColumnData> rightList = new ArrayList<>();
    List<MTFSelectedAnswer> selectedAnswerList = new ArrayList<>();
    ArrayList<String> playSelectedAnswerList = new ArrayList<>();
    String selectedAns = "";

    int totalAttempt = 0;
    int optionSelected = 0;
    long finalScr = 0;
    float assessmentScore = 0;
    boolean wrongAssessmentAnsFound = false;

    MTFAnswerAdapter adapter;
    MTFSelectedAnswerAdapter mtfSelectedAnswerAdapter;
    PopupWindow zoomImagePopup;

    private ItemTouchHelper mItemTouchHelper;

    //justify popup
    ConstraintLayout justifyPopupConstraintLayout;
    TextView justifyPopupHeader;
    EditText justifyPopupEditText;
    TextView justifyPopupLimitText;
    FrameLayout justifyPopupActionButton;
    CardView justifyCardView;
    ImageView justifyPopupCloseIcon;
    private int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    private final int minWordsCount = 0;


    public MTFQuestionFragment() {
        // Required empty public constructor
    }

    public static MTFQuestionFragment newInstance() {
        return new MTFQuestionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionBaseViewModel = new ViewModelProvider(requireActivity()).get(QuestionBaseViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_m_t_f_question, container, false);

        Objects.requireNonNull(requireActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        question = view.findViewById(R.id.question);
        question_image_lay = view.findViewById(R.id.question_image_lay);
        question_image = view.findViewById(R.id.question_image);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.mtf_thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        answer_recyclerview = view.findViewById(R.id.answer_recyclerview);
        options_recyclerview = view.findViewById(R.id.options_recyclerview);
        question_katex = view.findViewById(R.id.question_katex);
        question_description_webView = view.findViewById(R.id.description_webView);
        insturaction_text = view.findViewById(R.id.mtf_instruction);


        justifyPopupConstraintLayout = view.findViewById(R.id.justify_popup_constraint_layout);
        justifyPopupEditText = view.findViewById(R.id.justify_popup_edit_text);
        justifyPopupHeader = view.findViewById(R.id.justify_popup_header);
        justifyPopupLimitText = view.findViewById(R.id.justify_popup_limit_text);
        justifyPopupActionButton = view.findViewById(R.id.justify_popup_button);
        justifyPopupCloseIcon = view.findViewById(R.id.justify_popup_close_button);
        justifyCardView = view.findViewById(R.id.justify_popup_cardView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        info_type.setText(getResources().getString(R.string.drag_match));
        info_type.setVisibility(View.GONE);
        insturaction_text.setText(Html.fromHtml("<b>INSTRUCTIONS: </b>") + getResources().getString(R.string.match_following_info));

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;

        if (isCourseQuestion) {
            setQuestionData(questions);
        } else {
            questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
                if (questionBaseModel == null)
                    return;

                this.questionBaseModel = questionBaseModel;
                setData(questionBaseModel);

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
                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                    Log.d(TAG, "solution: " + solutionText + " : " + mainCourseCardClass.getChildCard().getContent());
                }
                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }
                if (isCourseQuestion) {
                    CourseQuestionHandling.randomizeOption(mainCourseCardClass);
                } else if (questionBaseViewModel != null) {
                    this.mainCourseCardClass = questionBaseViewModel.randomizeOption(mainCourseCardClass);
                }

                if (mainCourseCardClass != null && mainCourseCardClass.getXp() == 0) {
                    mainCourseCardClass.setXp(100);
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
        if (mainCourseCardClass != null && mainCourseCardClass.getQuestionData() != null) {
            questions = mainCourseCardClass.getQuestionData();
        }
        if (questions == null) {
            if (learningModuleInterface != null) {
                learningModuleInterface.endActivity();
            }
        }
        if (isCourseQuestion) {
            if (mainCourseCardClass != null && mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                questions.setBgImg(mainCourseCardClass.getBgImg());
            }
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

    public void setVideoOverlay(boolean videoOverlay) {
        this.videoOverlay = videoOverlay;
    }

    private boolean isVideoOverlay() {
        return videoOverlay;
    }

    public void setCourseCompleted(boolean isCourseCompleted) {
        this.isCourseCompleted = isCourseCompleted;

    }

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;

    }

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
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


    private void setData(QuestionBaseModel questionBaseModel) {
        try {
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
//            questions = questionBaseModel.getQuestions();
            setQuestionData(questions);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionData(DTOQuestions questions) {
        try {
            if (questions != null) {
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
                setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());
                expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));
                setLayoutSize();
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
                                                    Log.e("MTFQuestion", "Question Image loaded url " + resource + " file " + file);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Log.e("MTFQuestion", "Question Image exception url " + e.getMessage());
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

    private void setLayoutSize() {
        try {
            Log.d(TAG, "setMatchQuestionLayoutSize: ");
            totalOption = 0;
            List<DTOMTFColumnData> mtfLeftCol = questions.getMtfLeftCol();
            List<DTOMTFColumnData> mtfRightCol = questions.getMtfRightCol();
            try {
                //L - 0,1,2
                //R - 0,1,2
                //Merge A0=L0. A1=R0, A2=L1, A3=R1, A4=L2, A5=R2
                if (mtfLeftCol != null && mtfLeftCol.size() > 0 && mtfRightCol != null && mtfRightCol.size() > 0) {
                    totalOption = mtfLeftCol.size();
                    int leftSize = mtfLeftCol.size();
                    int rightSize = mtfRightCol.size();
                    int l = 0;
                    int r = 0;

                    allList = new ArrayList<>();
                    leftList = new ArrayList<>();
                    rightList = new ArrayList<>();
                    for (int i = 0; i < leftSize + rightSize; i++) {
                        if (i == 0) {
                            allList.add(mtfLeftCol.get(0));//A0
                            leftList.add(mtfLeftCol.get(0));
                        } else if (i == 1) {
                            allList.add(mtfRightCol.get(0));//A1
                            rightList.add(mtfRightCol.get(0));//A1
                        } else {
                            if (i % 2 == 0) {
                                allList.add(mtfLeftCol.get(l + 1));//A2,A4
                                leftList.add(mtfLeftCol.get(l + 1));//A2,A4
                                l++;
                            } else {
                                allList.add(mtfRightCol.get(r + 1));//A3,A5
                                rightList.add(mtfRightCol.get(r + 1));//A3,A5
                                r++;
                            }
                        }
                    }
                    if (allList.size() != 0) {
                        if (isAssessmentQuestion && isReviewMode) {
                            handleAnswerForReview();
                        } else {
                            if (isCourseQuestion && isReviewMode) {
                                handleAnswerForReview();
                            } else {
                                setOptionAdapter();
                            }
                        }
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

    private void setOptionAdapter() {
        try {
            Collections.shuffle(rightList, new Random());
            adapter = new MTFAnswerAdapter();
            adapter.setOptionsList(leftList, rightList, getActivity(), this);
            adapter.setMTFAnswerCheck(this);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(options_recyclerview);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            options_recyclerview.setLayoutManager(mLayoutManager);
            options_recyclerview.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageOptionFromFile(String imageUrl, ImageView imageView) {
        try {
            String url = imageUrl;
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                GifDrawable gifFromBytes = new GifDrawable(getBytes(Objects.requireNonNull(OustSdkApplication.getContext().getContentResolver().openInputStream(uri))));
                imageView.setImageDrawable(gifFromBytes);
            } else {
                if (OustSdkTools.checkInternetStatus())
                    Glide.with(Objects.requireNonNull(requireActivity())).load(imageUrl).into(imageView);
                else {
                    OustSdkTools.showToast(Objects.requireNonNull(requireActivity()).getString(R.string.no_internet_connection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            setNonGifImage(imageUrl, imageView);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void setNonGifImage(String imageUrl, ImageView imageView) {
        String url = imageUrl;
        Log.d(TAG, "setImageOptionUrl: " + url);
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
        }
        if (!Objects.requireNonNull(url).contains("oustlearn_")) {
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
        }
        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
            if (options.outWidth != -1 && options.outHeight != -1) {
                Log.d(TAG, "setImageOptionUrl: this is proper image");
                if (OustSdkTools.checkInternetStatus()) {
                    Glide.with(Objects.requireNonNull(requireActivity())).load(imageUrl).into(imageView);
                    if (file.exists()) {
                        boolean b = file.delete();
                        Log.e(TAG, "File exists " + b);
                    }
                    downLoad(imageUrl);
                } else {
                    Glide.with(Objects.requireNonNull(requireActivity())).load(uri).into(imageView);
                }
            } else {
                Log.d(TAG, "setImageOptionUrl: this is not proper image");
                Glide.with(Objects.requireNonNull(requireActivity())).load(imageUrl).into(imageView);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.e(TAG, "File exists " + b);
                }
                downLoad(imageUrl);
            }

        } else {
            if (OustSdkTools.checkInternetStatus())
                Glide.with(Objects.requireNonNull(requireActivity())).load(imageUrl).into(imageView);
            else {
                OustSdkTools.showToast(Objects.requireNonNull(requireActivity()).getString(R.string.no_internet_connection));
            }
        }
    }


    public void downLoad(String url) {

        try {
            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    //showDownloadProgress();
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        //removeFile();
                        Log.d(TAG, "Download completed");
                    }

                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            String path = Objects.requireNonNull(requireActivity()).getFilesDir() + "/";
            String filename = OustMediaTools.getMediaFileName(url);
            downloadFiles.startDownLoad(url, path, filename, true, false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageOption(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                GifDrawable gifFromBytes = new GifDrawable(imageByte);
                imgView.setImageDrawable(gifFromBytes);
            }
        } catch (Exception e) {
            setImageOptionBitmap(str, imgView);
        }
    }

    public void setImageOptionBitmap(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        try {
            mItemTouchHelper.startDrag(viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void optionSelected(int leftPosition, int rightPosition) {
        try {
            if (leftList.size() != 0 && leftPosition < leftList.size()) {
                selectedAns = leftList.get(leftPosition).getMtfColDataId();
            }

            boolean isCorrectAns = false;
            if (rightList.size() != 0 && rightPosition < rightList.size()) {
                String answerStr = selectedAns + "," + rightList.get(rightPosition).getMtfColDataId();
                if (playSelectedAnswerList != null) {
                    playSelectedAnswerList.add(answerStr);
                }
                List<String> answerList = questions.getMtfAnswer();
                for (int j = 0; j < answerList.size(); j++) {
                    if (answerList.get(j).contains(answerStr)) {
                        isCorrectAns = true;
                        rightAnswer();
                        handleMergeAnswer(leftPosition, rightPosition);
                        optionSelected++;
                        break;
                    }
                }
                if (!isCorrectAns) {
                    wrongAssessmentAnsFound = true;
                    handleWrongAnswer(leftPosition, rightPosition);
                }
            }
            scoreHandling(false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAnswer() {
        try {
            if (isAssessmentQuestion) {
                float mark = (float) questionXp / (float) (questions.getMtfAnswer().size());
                assessmentScore = assessmentScore + mark;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleMergeAnswer(int leftPosition, int rightPosition) {
        try {
            if (selectedAnswerList == null) {
                selectedAnswerList = new ArrayList<>();
            }
            MTFSelectedAnswer mtfSelectedAnswer = new MTFSelectedAnswer();
            mtfSelectedAnswer.setLeftColumn(leftList.get(leftPosition));
            mtfSelectedAnswer.setRightColumn(rightList.get(rightPosition));
            selectedAnswerList.add(mtfSelectedAnswer);
            if (leftList.size() == rightList.size()) {
                leftList.remove(leftPosition);
                rightList.remove(rightPosition);
                adapter.notifyDataSetChanged();
            }
            if (selectedAnswerList != null && selectedAnswerList.size() != 0) {
                answer_recyclerview.setVisibility(View.VISIBLE);
                if (mtfSelectedAnswerAdapter == null) {
                    mtfSelectedAnswerAdapter = new MTFSelectedAnswerAdapter(selectedAnswerList, getActivity());
                }
                answer_recyclerview.setAdapter(mtfSelectedAnswerAdapter);
                mtfSelectedAnswerAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleAnswerForReview() {
        try {
            if (selectedAnswerList == null) {
                selectedAnswerList = new ArrayList<>();
            }

            if (questions != null) {
                List<String> answerStringList = questions.getMtfAnswer();
                if (answerStringList != null && answerStringList.size() != 0 &&
                        leftList.size() == answerStringList.size() && rightList.size() == answerStringList.size()) {
                    for (String answer : answerStringList) {
                        MTFSelectedAnswer mtfSelectedAnswer = new MTFSelectedAnswer();

                        for (DTOMTFColumnData leftData : leftList) {
                            if (answer.contains(leftData.getMtfColDataId())) {
                                mtfSelectedAnswer.setLeftColumn(leftData);
                                break;
                            }
                        }
                        for (DTOMTFColumnData rightData : rightList) {
                            if (answer.contains(rightData.getMtfColDataId())) {
                                mtfSelectedAnswer.setRightColumn(rightData);
                                break;
                            }
                        }
                        selectedAnswerList.add(mtfSelectedAnswer);
                    }
                }
            }

            if (selectedAnswerList != null && selectedAnswerList.size() != 0) {
                answer_recyclerview.setVisibility(View.VISIBLE);
                options_recyclerview.setVisibility(View.GONE);
                if (mtfSelectedAnswerAdapter == null) {
                    mtfSelectedAnswerAdapter = new MTFSelectedAnswerAdapter(selectedAnswerList, getActivity());
                }
                answer_recyclerview.setAdapter(mtfSelectedAnswerAdapter);
                mtfSelectedAnswerAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleWrongAnswer(int leftPosition, int rightPosition) {
        try {
            if (isCourseQuestion) {
                isCourseQuestionIsRight = false;
                totalAttempt++;
                vibrateAndShake();
            } else {
                optionSelected++;
                float mark = (float) questionXp / (float) ((questions.getMtfAnswer().size() * 2));
                assessmentScore = assessmentScore - mark;
                handleMergeAnswer(leftPosition, rightPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void scoreHandling(boolean isTimeOut, boolean previousArrow, boolean nextArrow) {
        try {
            this.previousArrow = previousArrow;
            this.nextArrow = nextArrow;
            if (isCourseQuestion) {
                if (optionSelected == totalOption) {
                    if (totalAttempt < (totalOption + 1)) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else {
                        int totalXp = (int) mainCourseCardClass.getXp();
//                        finalScr = totalXp - ((10 * (totalAttempt - (optionSelected))));
                        finalScr = totalXp - ((10L * totalAttempt));
                    }

                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    answerSubmit(questions.getAnswer(), finalScr, isTimeOut, true);
                } else if (isTimeOut) {
                    float weight = (float) mainCourseCardClass.getXp() / (float) (totalOption);
                    float rightVal = weight * optionSelected;
                    finalScr = Math.round(rightVal - ((10 * (totalAttempt - (optionSelected)))));
                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    answerSubmit(questions.getAnswer(), finalScr, isTimeOut, false);
                }
            } else {
                String selectedAnswer = "";
                if (optionSelected == totalOption) {
                    if (playSelectedAnswerList != null && playSelectedAnswerList.size() > 0) {
                        for (int i = 0; i < playSelectedAnswerList.size(); i++) {
                            selectedAnswer = String.join(" : ", selectedAnswer, playSelectedAnswerList.get(i));
                        }
                    }
                    calculateScores(isTimeOut, selectedAnswer);
                } else if (isTimeOut) {
                    wrongAssessmentAnsFound = true;
                    calculateScores(true, "");
                } else if (nextArrow || previousArrow) {
                    wrongAssessmentAnsFound = true;
                    calculateScores(false, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void calculateScores(boolean timeOut, String selectedAnswer) {
        Log.d(TAG, "calculateMatchTheColumnScores: selectedAnswer--> " + selectedAnswer);
        //need to check old UI flow
        finalScr = Math.round(assessmentScore);
        if (finalScr < 0) {
            finalScr = 0;
        }
        if (finalScr > questionXp)
            finalScr = questionXp;
        if ((wrongAssessmentAnsFound || timeOut) && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())) {
            finalScr = 0;
            answerSubmit(selectedAnswer, 0, timeOut, false);
        } else {
            answerSubmit(selectedAnswer, Math.round(assessmentScore), timeOut, !wrongAssessmentAnsFound);
        }
    }

    private void answerSubmit(String answer, long oc, boolean isTimeOut, boolean isCorrect) {
        try {
            if (isReviewMode) {
                courseContentHandlingInterface.cancelTimer();
            }
            if (isTimeOut) {
                isCourseQuestionIsRight = false;
            }
            if (isCourseQuestion) {
                if (courseContentHandlingInterface != null) {
                    courseContentHandlingInterface.cancelTimer();
                }
                if ((questions.isContainSubjective() && (!isTimeOut))) {
                    if (courseContentHandlingInterface != null) {
                        courseContentHandlingInterface.disableNextArrow();
                    }
                    showSubjectiveQuestionPopup(answer, oc, isCorrect);
                } else {
                    options_recyclerview.setOnTouchListener((view, motionEvent) -> true);
                    saveDataIntoFireBase(answer, oc, isCorrect, "");
                }
            } else {
                if (learningModuleInterface != null) {
                    learningModuleInterface.setAnswerAndOc(answer, "", (int) oc, isCorrect, 0);
                    if (!nextArrow && !previousArrow) {
                        learningModuleInterface.gotoNextScreen();
                    } else if (previousArrow) {
                        learningModuleInterface.gotoPreviousScreen();
                    } else {
                        learningModuleInterface.gotoNextScreen();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showSubjectiveQuestionPopup(String answer, long oc, boolean isCorrect) {
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
                        Log.e("TAG", "showSubjectiveQuestionPopup: " + justifyPopupEditText.getText().toString());
                        saveDataIntoFireBase(answer, oc, isCorrect, OustSdkTools.getEmojiEncodedString(justifyPopupEditText));
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
                    saveDataIntoFireBase(answer, oc, isCorrect, "");
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

    private void saveDataIntoFireBase(String answer, long oc, boolean isCorrect, String subjctiveResponse) {
        try {
            if (isVideoOverlay()) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.setVideoOverlayAnswerAndOc(answer, subjctiveResponse, (int) oc, isCorrect, 0, cardId);
                }
                new Handler().postDelayed(() -> learningModuleInterface.closeChildFragment(), 1500);
            }

            if (questions.isThumbsUpDn()) {
                handleRightOrWrong(finalScr, isCorrect, subjctiveResponse);
            } else {
                if (isVideoOverlay()) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.closeChildFragment();
                } else {
                    handleNextQuestion(answer, subjctiveResponse, (int) oc, isCourseQuestionIsRight, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleRightOrWrong(long finalScr, boolean status, String subjctiveResponse) {
        Log.e(TAG, "handleRightOrWrong: " + status);
        try {
            if (!zeroXpForQCard) {
                if (finalScr == 0) {
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                }
            }
            if (status) {
                rightAnswerSound();
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
                            Log.e(TAG, "onAnimationEnd: zeroXpForQCard--> " + zeroXpForQCard);
                            if (zeroXpForQCard) {
                                //TODO: handle show solution popup
                                handleNextQuestion(questions.getAnswer(), subjctiveResponse, (int) finalScr, true, 0);
                            } else {
                                animateOcCoins(questions.getAnswer(), subjctiveResponse, (int) finalScr, true, 0);
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
                try {
                    showSolutionAnswer = false;
                    animMainLayout.setVisibility(View.VISIBLE);
                    question_result_image.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                    wrongAnswerSound();
                    handleNextQuestion(questions.getAnswer(), "", (int) finalScr, isCourseQuestionIsRight, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
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
                                    courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, true, userAns, subjectiveResponse, oc, status, time);
                                }
                            } else if (finalScr == 0 || !showSolutionAnswer) {
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
                }
            }, FOUR_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateOcCoins(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            if ((finalScr > 0)) {
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
            animator1.setObjectValues(0, (int) (finalScr));
            animator1.setDuration(SIX_HUNDRED_MILLI_SECONDS);
            animator1.addUpdateListener(valueAnimator -> {
                if (isCourseCompleted || isLevelCompleted) {
                    coinsAnimImg.setVisibility(View.GONE);
                    coinsAnimText.setVisibility(View.GONE);
                } else {
                    if ((int) valueAnimator.getAnimatedValue() > 0) {
                        coinsAnimImg.setVisibility(View.VISIBLE);
                        coinsAnimText.setVisibility(View.VISIBLE);
                        coinsAnimText.setText("" + (((int) valueAnimator.getAnimatedValue())));
                    } else {
                        coinsAnimImg.setVisibility(View.GONE);
                        coinsAnimText.setVisibility(View.GONE);
                    }

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
                        if (finalScr > 0) {
                            coinsAnimImg.setVisibility(View.VISIBLE);
                            coinsAnimText.setVisibility(View.VISIBLE);
                            coinsAnimText.setText("" + finalScr);
                        } else {
                            coinsAnimImg.setVisibility(View.GONE);
                            coinsAnimText.setVisibility(View.GONE);
                        }

                    }
                    //TODO: handle show solution animation
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAudio(final String filename) {
        try {
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            tempMp3.deleteOnExit();
            if (answerChosenPlayer != null) {
                answerChosenPlayer.reset();
                FileInputStream fis = new FileInputStream(tempMp3);
                answerChosenPlayer.setDataSource(fis.getFD());
                answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                answerChosenPlayer.prepare();
                answerChosenPlayer.setOnPreparedListener(MTFQuestionFragment::onPrepared);
                answerChosenPlayer.start();
                answerChosenPlayer.setOnCompletionListener(MTFQuestionFragment::onCompletion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void onPrepared(MediaPlayer mediaPlayer) {
    }

    private static void onCompletion(MediaPlayer mediaPlayer) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }
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

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }
}

