package com.oustme.oustsdk.question_module.fragment;

import static android.content.ContentValues.TAG;
import static com.android.volley.Request.Method.GET;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.DropDownListAdapter;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionAnswerModel;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.DropDownListResponse;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class DropDownQuestionFragment extends Fragment implements DropDownListAdapter.DropDownListListener {

    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    RelativeLayout video_lay;
    RelativeLayout media_question_container;
    TextView question;
    KatexView question_katex;
    WebView question_description_webView;
    LinearLayout question_image_lay;
    ImageView question_image;
    TextView info_type;
    ImageView expand_icon;
    RecyclerView question_answer_rv;
    FrameLayout question_action_button;
    CardView dropDownListCardView;
    EditText search_edt;
    ImageView clear_search_img;
    CardView dropDownSelectedTxtLayout;
    TextView dropDownSelectedTxt;

    //Right And Wrong questions thump and coins
    ImageView question_result_image;
    RelativeLayout animMainLayout;
    LinearLayout coinsAnimLayout;
    ImageView coinsAnimImg;
    TextView coinsAnimText;

    //View Model from Assessment and survey
    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;
    AssessmentContentHandlingInterface assessmentContentHandler;
    ArrayList<String> dropDownList = new ArrayList<>();
    ArrayList<String> loadMoreDropDownList = new ArrayList<>();

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
    CourseContentHandlingInterface courseContentHandlingInterface;
    DTOCourseCard mainCourseCardClass;
    CourseLevelClass courseLevelClass;
    boolean zeroXpForQCard;
    String backgroundImage;
    boolean isCourseCompleted;
    boolean isReviewMode;
    boolean isExamMode;
    boolean isCourseQuestion;
    boolean isVideoOverlay;

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
    private Scores score;
    private final boolean isCourseQuestionIsRight = false;
    private boolean proceedOnWrong;
    private int finalScr = 0;
    private int totalDropDownList = 0;
    private String selectedText;

    ArrayList<QuestionAnswerModel> answerModels = new ArrayList<>();
    String answerType;
    DropDownListAdapter adapter;

    int totalOption = 0;
    SurveyFunctionsAndClicks surveyFunctionsAndClicks;

    CustomExoPlayerView customExoPlayerView;
    Context context;
    PopupWindow zoomImagePopup;
    DTOCourseSolutionCard courseSolutionCard;
    String finalVideoUrl;
    MediaPlayer answerChosenPlayer;


    public DropDownQuestionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_drop_down_question, container, false);
        question_base_frame = view.findViewById(R.id.dropDown_question_base_frame);
        question_bgImg = view.findViewById(R.id.dropDown_question_bgImg);
        main_layout = view.findViewById(R.id.dropDown_main_layout);
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
        question_answer_rv = view.findViewById(R.id.dropDown_question_answer_rv);
        question_action_button = view.findViewById(R.id.dropDown_question_action_button);
        dropDownListCardView = view.findViewById(R.id.drop_down_card_view);
        search_edt = view.findViewById(R.id.dropDown_search);
        clear_search_img = view.findViewById(R.id.drop_down_clear_search);
        dropDownSelectedTxtLayout = view.findViewById(R.id.drop_down_selected_cardView);
        dropDownSelectedTxt = view.findViewById(R.id.drop_drop_selected_txt);

        question_result_image = view.findViewById(R.id.question_result_image);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        if (isVideoOverlay()) {
            question_base_frame.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            color = OustResourceUtils.getColors();
            setButtonColor(getResources().getColor(R.color.overlay_container), false);
            info_type.setText(getResources().getString(R.string.mcq_info));
            info_type.setVisibility(View.VISIBLE);
            if (isCourseQuestion) {
                setQuestionData(questions);
            } else {
                try {
                    questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
                        if (questionBaseModel == null)
                            return;
                        this.questionBaseModel = questionBaseModel;
                        if (questionBaseModel.getQuestions() != null) {
                            if (!isReviewMode) {
                                if (questionBaseModel.getQuestions() != null && questionBaseModel.getQuestions().getQuestionType() != null &&
                                        questionBaseModel.getQuestions().getQuestionType().equalsIgnoreCase(QuestionType.DROPDOWN)) {
                                    getDropDownList(questionBaseModel.getQuestions().getDataSource());
                                }
                            } else {
                                setData(questionBaseModel);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            search_edt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        clear_search_img.setVisibility(View.VISIBLE);
                    } else {
                        clear_search_img.setVisibility(View.GONE);
                    }
                    if (adapter != null) {
                        if (dropDownList.size() > 0) {
                            adapter.getFilter().filter(charSequence);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            clear_search_img.setOnClickListener(view1 -> {
                if (adapter != null) {
                    if (dropDownList.size() > 0) {
                        adapter.getFilter().filter("");
                        search_edt.setText("");
                        clear_search_img.setVisibility(View.GONE);
                    }
                }
            });

            question_answer_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    try {
                        if (!recyclerView.canScrollVertically(1)) {
                            //Log.e("TAG", "onScrollStateChanged: ");
                            if (loadMoreDropDownList != null) {
                                if (loadMoreDropDownList.size() != totalDropDownList) {
                                    if (totalDropDownList <= loadMoreDropDownList.size() + 10) {
                                        int size = loadMoreDropDownList.size();
                                        for (int i = size; i < totalDropDownList; i++) {
                                            if (!dropDownList.get(i).isEmpty() && dropDownList.get(i) != null) {
                                                loadMoreDropDownList.add(dropDownList.get(i));
                                            }
                                        }
                                    } else {
                                        int size = loadMoreDropDownList.size();
                                        for (int i = size; i < size + 10; i++) {
                                            if (!dropDownList.get(i).isEmpty() && dropDownList.get(i) != null) {
                                                loadMoreDropDownList.add(dropDownList.get(i));
                                            }
                                        }
                                    }
                                    loadData(loadMoreDropDownList);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadData(ArrayList<String> loadMoreDropDownList) {
        try {
            if (loadMoreDropDownList != null && loadMoreDropDownList.size() != 0) {
                OustStaticVariableHandling.getInstance().setSortPosition(-1);
                if (adapter == null) {
                    adapter = new DropDownListAdapter();
                    totalOption = answerModels.size();
                    adapter.setData(loadMoreDropDownList, getContext(), DropDownQuestionFragment.this, isReviewMode);
                    adapter.setTotalData(dropDownList, getContext(), score);
                    adapter.setExamMode(isExamMode);
                    question_answer_rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    question_answer_rv.setAdapter(adapter);
                    question_action_button.setOnClickListener(v -> {
                        question_action_button.setEnabled(false);
                        if (selectedText != null) {
                            setAnswers(1, false, false);
                            new Handler().postDelayed(() -> question_action_button.setEnabled(true), 2000);
                        }
                    });
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getDropDownList(String dataSource) {
        try {
            dropDownList.clear();
            loadMoreDropDownList.clear();
            String getDropDownListUrl = context.getResources().getString(R.string.getDropDownList);
            getDropDownListUrl = getDropDownListUrl.replace("{DataSource}", ("" + dataSource));
            Log.d(TAG, "getDataSource: " + getDropDownListUrl);

            getDropDownListUrl = HttpManager.getAbsoluteUrl(getDropDownListUrl);

            ApiCallUtils.doNetworkCall(GET, getDropDownListUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new GsonBuilder().create();
                        DropDownListResponse dropDownListResponse = gson.fromJson(response.toString(), DropDownListResponse.class);
                        if (dropDownListResponse != null && dropDownListResponse.getDataSourceList() != null && !dropDownListResponse.getDataSourceList().isEmpty() && dropDownListResponse.getDataSourceList().size() > 0) {
                            dropDownList.addAll(dropDownListResponse.getDataSourceList());
                            Collections.sort(dropDownList, String::compareToIgnoreCase);
                            totalDropDownList = dropDownList.size();
                            if (dropDownList.size() > 0) {
                                if (totalDropDownList > 10) {
                                    for (int i = 0; i < 10; i++) {
                                        if (!dropDownList.get(i).isEmpty() && dropDownList.get(i) != null) {
                                            loadMoreDropDownList.add(dropDownList.get(i));
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < dropDownList.size(); i++) {
                                        if (!dropDownList.get(i).isEmpty() && dropDownList.get(i) != null) {
                                            loadMoreDropDownList.add(dropDownList.get(i));
                                        }
                                    }
                                }
                                setData(questionBaseModel);
                            } else {
                                OustSdkTools.showToast(requireActivity().getString(R.string.no_data_available));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: dropDown list" + error);
                }
            });

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
                    Glide.with(Objects.requireNonNull(requireActivity())).load(questionBaseModel.getBgImage()).into(question_bgImg);
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
                if (isCourseQuestion || isReviewMode) {
                    question_action_button.setVisibility(View.GONE);
                }

                if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(questions.getBgImg()).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    try {
                        Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
                if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                    handleVideoQuestion(questions.getGumletVideoUrl());
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
                if (!isReviewMode) {
                    dropDownListCardView.setVisibility(View.VISIBLE);
                    dropDownSelectedTxtLayout.setVisibility(View.GONE);
                    loadData(loadMoreDropDownList);
                    if (isExamMode) {
                        if (score != null && score.getAnswer() != null) {
                            selectedText = score.getAnswer();
                            setButtonColor(color, true);
                        }
                    }
                } else {
                    dropDownListCardView.setVisibility(View.GONE);
                    dropDownSelectedTxtLayout.setVisibility(View.VISIBLE);
                    if (score != null && score.getAnswer() != null) {
                        dropDownSelectedTxt.setText(score.getAnswer());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnswers(int count, boolean previousArrow, boolean nextArrow) {
        try {
            finalScr = (int) (questionXp / count);
            if (isSurveyQuestion) {
                learningModuleInterface.setAnswerAndOc(selectedText, "", 0, true, 0);
                handleNextQuestion();
            } else if (isAssessmentQuestion) {
                if (selectedText != null) {
                    if (!selectedText.isEmpty()) {
                        learningModuleInterface.setAnswerAndOc(selectedText, "", (int) mainCourseCardClass.getXp(), true, 0);
                    } else {
                        learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                    }
                }
                if (!nextArrow && !previousArrow) {
                    handleNextQuestion();
                } else if (previousArrow) {
                    handlePreviousQuestion();
                } else {
                    handleNextQuestion();
                }
            } else if (isCourseQuestion) {
                if (questions.isThumbsUpDn()) {
                    rightAndWrongQuestionAnswer(selectedText);
                } else {
                    if (!selectedText.isEmpty()) {
                        if (courseContentHandlingInterface != null)
                            courseContentHandlingInterface.handleScreenTouchEvent(false);
                        if (isVideoOverlay()) {
                            learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", finalScr, true, 0, cardId);
                            learningModuleInterface.closeChildFragment();
                        } else {
                            if (learningModuleInterface != null)
                                learningModuleInterface.setAnswerAndOc(questions.getAnswer(), "", finalScr, isCourseQuestionIsRight, 0);
                            handleNextQuestion();
                        }
                    } else {
                        vibrateAndShake();
                        wrongAnswerSound();
                        if (isVideoOverlay()) {
                            try {
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
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handlePreviousQuestion() {
        try {
            new Handler().postDelayed(() -> {
                if (learningModuleInterface != null) {
                    learningModuleInterface.gotoPreviousScreen();
                }
            }, FIVE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rightAndWrongQuestionAnswer(String selectedText) {
        if (!selectedText.isEmpty()) {
            if (courseContentHandlingInterface != null)
                courseContentHandlingInterface.handleScreenTouchEvent(false);
            rightAnswerSound();
            if (isVideoOverlay()) {
                question_result_image.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", finalScr, true, 0, cardId);
                new Handler().postDelayed(() -> {
                    try {
                        learningModuleInterface.closeChildFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 500);
            } else {
                if (learningModuleInterface != null)
                    learningModuleInterface.setAnswerAndOc(questions.getAnswer(), "", finalScr, isCourseQuestionIsRight, 0);
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
                                handleNextQuestion();
                            }
                        } else {
                            animateOcCoins();
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
            if (isVideoOverlay()) {
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

    private void animateOcCoins() {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            if ((finalScr > 0)) {
                if (answerChosenPlayer == null) {
                    answerChosenPlayer = new MediaPlayer();
                }
                if (!isCourseCompleted) {
                    playAudio("coins.mp3");
                }
            }
            if (isCourseCompleted) {
                coinsAnimImg.setVisibility(View.GONE);
                coinsAnimText.setVisibility(View.GONE);
            }
            animator1.setObjectValues(0, (finalScr));
            animator1.setDuration(SIX_HUNDRED_MILLI_SECONDS);
            animator1.addUpdateListener(valueAnimator -> {
                if (isCourseCompleted) {
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
                    if (isCourseCompleted) {
                        coinsAnimImg.setVisibility(View.GONE);
                        coinsAnimText.setVisibility(View.GONE);
                    } else {
                        coinsAnimImg.setVisibility(View.VISIBLE);
                        coinsAnimText.setVisibility(View.VISIBLE);
                        coinsAnimText.setText("" + finalScr);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            if (!isVideoOverlay()) {
                handleNextQuestion();
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
                    question_image.setImageURI(uri);
                } else {
                    if ((image != null) && (!image.isEmpty())) {
                        byte[] imageByte = Base64.decode(image, 0);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                        question_image_lay.setVisibility(View.VISIBLE);
                        expand_icon.setVisibility(View.VISIBLE);
                        question_image.setImageBitmap(decodedByte);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleNextQuestion() {
        try {
            new Handler().postDelayed(() -> {
                if (learningModuleInterface != null) {
                    learningModuleInterface.gotoNextScreen();
                }
            }, FIVE_HUNDRED_MILLI_SECONDS);
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
                            if (!isCourseQuestion)
                                question_action_button.setVisibility(View.VISIBLE);
                            info_type.setText(getResources().getString(R.string.mcq_info));
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
            Log.d("TAG", "checkVideoMediaExist:-DROP- path:" + rootPath);
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
            MediaPlayer answerChosenPlayer = new MediaPlayer();
            answerChosenPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            answerChosenPlayer.setDataSource(fis.getFD());
            answerChosenPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            answerChosenPlayer.prepare();
            answerChosenPlayer.setOnPreparedListener(DropDownQuestionFragment::onPrepared);
            answerChosenPlayer.start();
            answerChosenPlayer.setOnCompletionListener(DropDownQuestionFragment::onCompletion);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void onCompletion(MediaPlayer mediaPlayer) {
    }

    private static void onPrepared(MediaPlayer mediaPlayer) {
    }

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setShowNavigateArrow(boolean showNavigationArrow) {
        this.showNavigateArrow = showNavigationArrow;
    }

    public void setAssessmentQuestion(boolean assessment) {
        this.isAssessmentQuestion = assessment;
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

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    private boolean isVideoOverlay() {
        return isVideoOverlay;
    }

    public void setVideoOverlay(boolean videoOverlay) {
        isVideoOverlay = videoOverlay;
    }

    private boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
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
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
            if (isRandomizeQuestion) {
                if (isCourseQuestion) {
                    CourseQuestionHandling.randomizeOption(mainCourseCardClass);
                } else if (questionBaseViewModel != null) {
                    this.mainCourseCardClass = questionBaseViewModel.randomizeOption(mainCourseCardClass);
                }
            }

            if (mainCourseCardClass != null && mainCourseCardClass.getXp() == 0) {
                mainCourseCardClass.setXp(100);
            }
            if (mainCourseCardClass != null) {
                questions = mainCourseCardClass.getQuestionData();
            }
            if (questions == null) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.endActivity();
                }
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
        }

        if (isCourseQuestion) {
            if (mainCourseCardClass != null) {
                if (mainCourseCardClass.getBgImg() != null && !mainCourseCardClass.getBgImg().isEmpty()) {
                    questions.setBgImg(mainCourseCardClass.getBgImg());
                }
            }
        } else if (isAssessmentQuestion) {
            if (mainCourseCardClass != null) {
                mainCourseCardClass.setXp(questionXp);
            }
        }
    }

    @Override
    public void onItemClicked(String selectedTxt) {
        if (OustStaticVariableHandling.getInstance().getSortPosition() != -1) {
            selectedText = selectedTxt;
            Log.e(TAG, "onItemClicked: selectedTxt--> " + selectedTxt);
            setButtonColor(color, true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (zoomImagePopup != null && zoomImagePopup.isShowing()) {
                zoomImagePopup.dismiss();
            }

            resetPlayer();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void resetPlayer() {
        try {
            if (customExoPlayerView != null) {
                customExoPlayerView.removeAudioPlayer();
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}