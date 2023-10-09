package com.oustme.oustsdk.question_module.fragment;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.course_ui.CourseContentHandlingInterface;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.ReviewAnswerGridAdapter;
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.OustFillData;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class FIBQuestionFragment extends Fragment {

    String TAG = "FIBQuestionFragment";
    NestedScrollView fib_scrollview;
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
    RelativeLayout fill_blanks_layout;
    FrameLayout question_action_button;
    MediaPlayer answerChosenPlayer;
    String solutionText;
    boolean showSolutionAnswer = true;
    boolean showSolution = false;
    boolean containSubjective = false;

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

    //Common for all modules
    LearningModuleInterface learningModuleInterface;
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
    boolean isLevelCompleted;
    boolean isCourseQuestionIsRight = true;
    boolean isQuestionRightOrWrong = false;

    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    //additional parameters
    int color;
    boolean showNavigateArrow;
    boolean isAssessmentQuestion;
    boolean isSurveyQuestion;
    boolean isVideoCompleted;
    boolean isReplayMode;
    private Scores score;

    boolean isVideoOverlay;

    CustomExoPlayerView customExoPlayerView;
    Context context;
    PopupWindow zoomImagePopup;

    List<String> answerStrings = new ArrayList<>();
    int maxLength = 0;
    int totalOption;
    int mCorrect, mWrong;
    long finalScr;
    LayoutInflater layoutInflater;
    List<View> fillViews = new ArrayList<>();
    ArrayList<View> fillViewsCreated = new ArrayList<>();
    List<OustFillData> emptyViews = new ArrayList<>();
    //EditText previousView = null;
    boolean isCompleted;
    int scrWidth, scrHeight;
    CourseContentHandlingInterface courseContentHandlingInterface;
    boolean proceedOnWrong;

    //justify popup
    ConstraintLayout justifyPopupConstraintLayout;
    TextView justifyPopupHeader;
    EditText justifyPopupEditText;
    TextView justifyPopupLimitText;
    FrameLayout justifyPopupActionButton;
    ImageView justifyPopupCloseIcon;
    CardView justifyCardView;
    private int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    private final int minWordsCount = 0;
    private CardView answerGridLayout;
    private GridView answerGridView;

    public FIBQuestionFragment() {
        // Required empty public constructor
    }

    public static FIBQuestionFragment newInstance() {
        return new FIBQuestionFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_f_i_b_question, container, false);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        fib_scrollview = view.findViewById(R.id.fib_scrollview);
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
        fill_blanks_layout = view.findViewById(R.id.fill_blanks_layout);
        question_action_button = view.findViewById(R.id.question_action_button);
        animMainLayout = view.findViewById(R.id.thumps_layout);
        coinsAnimLayout = view.findViewById(R.id.coin_anim_layout);
        coinsAnimImg = view.findViewById(R.id.coin_image);
        coinsAnimText = view.findViewById(R.id.coin_text);
        question_result_image = view.findViewById(R.id.question_result_image);

        justifyPopupConstraintLayout = view.findViewById(R.id.justify_popup_constraint_layout);
        justifyPopupEditText = view.findViewById(R.id.justify_popup_edit_text);
        justifyPopupHeader = view.findViewById(R.id.justify_popup_header);
        justifyPopupLimitText = view.findViewById(R.id.justify_popup_limit_text);
        justifyPopupActionButton = view.findViewById(R.id.justify_popup_button);
        justifyPopupCloseIcon = view.findViewById(R.id.justify_popup_close_button);
        justifyCardView = view.findViewById(R.id.justify_popup_cardView);
        answerGridLayout = view.findViewById(R.id.answer_grid_layout);
        answerGridView = view.findViewById(R.id.answer_grid_view);

        if (isVideoOverlay()) {
            question_base_frame.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        setIconColors(color, true);
        getScreenMeasure();
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
            try {
                if (mainCourseCardClass == null) {
                    mainCourseCardClass = courseCardClass;
                }
                if (courseCardClass.getChildCard() != null) {
                    solutionText = courseCardClass.getChildCard().getContent();
                }

                if (courseCardClass.getQuestionData() != null) {
                    showSolution = courseCardClass.getQuestionData().isShowSolution();
                    isRandomizeQuestion = courseCardClass.getQuestionData().isRandomize();
                    containSubjective = courseCardClass.getQuestionData().isContainSubjective();
                    if (courseCardClass.getQuestionData().getSolution() != null && !courseCardClass.getQuestionData().getSolution().isEmpty()) {
                        solutionText = courseCardClass.getQuestionData().getSolution();
                    }
                }
//                Log.d(TAG, "solution: " + solutionText + " : " + mainCourseCardClass.getChildCard().getContent());
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

    public void setReviewMode(boolean isReviewMode) {
        this.isReviewMode = isReviewMode;
    }

    public void setExamMode(boolean isExamMode) {
        this.isExamMode = isExamMode;
    }

    //course module
    public void setVideoOverlay(boolean videoOverlay) {
        isVideoOverlay = videoOverlay;
    }

    private boolean isVideoOverlay() {
        return isVideoOverlay;
    }

    private boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
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

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;
    }

    //assessment and survey
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

    private void getScreenMeasure() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }


    public void setData(QuestionBaseModel questionBaseModel) {

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
                }
            }

            if (assessmentContentHandler != null) {
                if (isReviewMode) {
                    assessmentContentHandler.cancelTimerForReview();
                    assessmentContentHandler.showBottomBar();
                }
            }
            if (questions.isMandatory() || isAssessmentQuestion || isCourseQuestion) {
                setIconColors(getResources().getColor(R.color.overlay_container), false);
            }
            if (isAssessmentQuestion && isReviewMode) {
                question_action_button.setVisibility(View.GONE);
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

            fill_blanks_layout.removeAllViews();

            new CreateFIB(getActivity()).execute();
            if (questions.getGumletVideoUrl() != null && !questions.getGumletVideoUrl().isEmpty()) {
                handleVideoQuestion(questions.getGumletVideoUrl());
            } else if (questions.getqVideoUrl() != null && !questions.getqVideoUrl().isEmpty()) {
                handleVideoQuestion(questions.getqVideoUrl());
            } else {
                setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());
                expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));
            }

            question_action_button.setOnClickListener(v -> {
                question_action_button.setEnabled(false);
                handleAnswer(false, false, false);
                new Handler().postDelayed(() -> question_action_button.setEnabled(true), FIVE_HUNDRED_MILLI_SECONDS);
            });
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
                                                    Log.e("FIBQuestion", "Question Image loaded url " + resource + " file " + file);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    OustSdkTools.sendSentryException(e);
                                                    Log.e("FIBQuestion", "Question Image exception url " + e.getMessage());
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

    public void setCourseContentHandlingInterface(CourseContentHandlingInterface courseContentHandlingInterface) {
        this.courseContentHandlingInterface = courseContentHandlingInterface;
    }

    public void setReplayMode(boolean isReplayMode) {
        this.isReplayMode = isReplayMode;
    }

    public class CreateFIB extends AsyncTask<String, String, String> {

        Context mContext;

        public CreateFIB(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            setFIBView();
            inflateViews();
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (!isAssessmentQuestion) {
                ShowPopup.getInstance().showProgressBar(getActivity(), getResources().getString(R.string.please_wait));
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().post(() -> {

                for (int i = 0; i < fillViewsCreated.size(); i++) {
                    if (fillViewsCreated.get(i).getParent() != null) {
                        ((ViewGroup) fillViewsCreated.get(i).getParent()).removeView(fillViewsCreated.get(i)); // <- fix
                    }

                    fill_blanks_layout.addView(fillViewsCreated.get(i));
                }

                ShowPopup.getInstance().dismissProgressDialog();

                try {
                    if (isAssessmentQuestion && isReviewMode && !isExamMode) {
                        answerGridLayout.setVisibility(View.VISIBLE);
                        answerGridView.setVisibility(View.VISIBLE);
                        String[] userAnswers = score.getAnswer().split("#");

                        ReviewAnswerGridAdapter answerGridAdapter = new ReviewAnswerGridAdapter(context, userAnswers, questions.getAnswer(), score, false);
                        answerGridView.setAdapter(answerGridAdapter);
//                    setGridViewHeightBasedOnChildren(answerGridView,4);
                    } else {
                        answerGridLayout.setVisibility(View.GONE);
                        answerGridView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ReviewAnswerGridAdapter listAdapter = (ReviewAnswerGridAdapter) gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight;
        int items = listAdapter.getCount();
        int rows;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }

    private void setFIBView() {
        try {
            layoutInflater = (LayoutInflater) requireActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            Spanned spannedContent = getSpannedContent(questions.getQuestion());
            String questionText = (spannedContent.toString().trim());
            questionText = questionText.replace("____", " ____ ");
            String[] questionStrings = questionText.split(" ");
            String[] options = questions.getAnswer().split("#");
            if (isExamMode && score != null && score.getAnswer() != null) {
                options = score.getAnswer().split("#");
            }
//            Collections.shuffle(Arrays.asList(options));

            for (String option : options) {
                Spanned spanned = getSpannedContent(option);
                answerStrings.add(spanned.toString());
                if (maxLength < spanned.toString().length()) {
                    maxLength = spanned.toString().length();
                }
                totalOption++;
            }

            //maintain dash length equal for all options
            StringBuilder dummyStr = new StringBuilder(" ");
            for (int j = 0; j < maxLength; j++) {
                dummyStr.append("_");
            }

            int value = 0;
            int index = 1;
            while (value < questionStrings.length) {

                if (questionStrings[value].contains("____")) {
                    View fill_view = layoutInflater.inflate(R.layout.fill_layout_et, null);
                    EditText fill_edit_text = fill_view.findViewById(R.id.fill_edit_text);
                    fill_edit_text.getLayoutParams().width = (int) ((dummyStr.length()) * (OustSdkApplication.getContext().getResources().getDimension(R.dimen.ousttext_dimen9)));

                    //following lines restrict number of character in edit text
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(dummyStr.length());
                    fill_edit_text.setFilters(FilterArray);

                    fill_edit_text.setId(100 + index);
                    //fill_edit_text.setOnClickListener(this::onEmptyViewTouch);
                    //handle review and solution answer - do below
                    if (isReviewMode || (isExamMode && score != null && score.getAnswer() != null)) {

                        fill_edit_text.setText(answerStrings.get(index - 1));
                        fill_edit_text.setTextColor(getResources().getColor(R.color.black));
                        fill_edit_text.setTypeface(null, Typeface.BOLD);
                        if (!isExamMode) {
                            fill_edit_text.setEnabled(false);
                        }
                    }
                    fill_edit_text.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                String fillValue = fill_edit_text.getText().toString();
                                ColorStateList colorStateList;
                                if (fillValue.length() > 0) {
                                    colorStateList = ColorStateList.valueOf(color);
                                    setIconColors(color, true);
                                } else {
                                    colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.unselected_text));
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    fill_edit_text.setBackgroundTintList(colorStateList);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fib_scrollview.setOnScrollChangeListener((View.OnScrollChangeListener) (view, i, i1, i2, i3) -> {
                            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        });
                    }

                    index++;
                    fillViews.add(fill_view);
                    OustFillData oustFillData = new OustFillData();
                    oustFillData.setView(fill_view);
                    oustFillData.setIndex(value);
                    emptyViews.add(oustFillData);
                } else {
                    View fillTextView = layoutInflater.inflate(R.layout.fill_layout_tv, null);
                    TextView fill_text_view = fillTextView.findViewById(R.id.fill_text_view);
                    fill_text_view.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    fill_text_view.setText(" " + questionStrings[value]);
                    fillViews.add(fillTextView);
                }
                value++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void inflateViews() {
        try {
            int x = 20;
            int y = (int) getResources().getDimension(R.dimen.oustlayout_dimen35);
            StringBuilder dummyStr = new StringBuilder(" ");

            for (int j = 0; j < maxLength; j++) {
                dummyStr.append("_");
            }

            for (int i = 0; i < fillViews.size(); i++) {
                View view = fillViews.get(i);
                int totalW = fill_blanks_layout.getWidth() - 20;
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) >= totalW) {
                    x = 20;
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen40);
                }
                view.setX(x);
                view.setY(y);
                x += viewW;
                fillViewsCreated.add(view);
            }

            addParamForFillLayout(y);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addParamForFillLayout(final int y) {
        try {
            Objects.requireNonNull(requireActivity()).runOnUiThread(() -> addParamForFillLayoutMain(y));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void addParamForFillLayoutMain(int y) {
        try {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fill_blanks_layout.getLayoutParams();
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen75);
            layoutParams.height = y;
            fill_blanks_layout.setLayoutParams(layoutParams);
            info_type.setVisibility(View.GONE);
            question.setText(getResources().getString(R.string.fill_title));
            //selectEditText();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Spanned getSpannedContent(String content) {
        String contentValue = content.trim();
        try {
            if (contentValue.endsWith("<br />")) {
                contentValue = contentValue.substring(0, contentValue.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return Html.fromHtml(contentValue, null, new OustTagHandler());
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

    public void handleAnswer(boolean isTimeOut, boolean previousArrow, boolean nextArrow) {
        try {
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            StringBuilder answers = new StringBuilder();
            if (!isCompleted) {
                boolean isComplete = true;
                for (int index = 0; index < emptyViews.size(); index++) {
                    View fillView = emptyViews.get(index).getView();
                    EditText editText = fillView.findViewById(100 + index + 1);

                    if (editText != null && editText.getText() != null && !editText.getText().toString().trim().isEmpty()) {
                        if (index == (emptyViews.size() - 1)) {
                            answers.append(editText.getText().toString());
                        } else {
                            answers.append(editText.getText().toString()).append("#");
                        }
                        if (editText.getText().toString().trim().equalsIgnoreCase(questions.getFillAnswers().get(index).trim())) {
                            isQuestionRightOrWrong = true;
                            mCorrect++;
                        } else {
                            isQuestionRightOrWrong = false;
                            mWrong++;
                        }
                    } else {
                        if (!isTimeOut) {
                            isComplete = false;
                            break;
                        } else {
                            mWrong++;
                        }
                    }
                }
                isCourseQuestionIsRight = mWrong <= 0;
                if (isComplete) {
                    isCompleted = true;

                    if (!isAssessmentQuestion) {
                        //course
                        float challengerScore = 0f;
                        if (mainCourseCardClass != null) {
                            challengerScore = (float) mainCourseCardClass.getXp();
                        }
                        float rightWeight = challengerScore / (float) questions.getFillAnswers().size();
                        final float totalXP = rightWeight * mCorrect;
                        finalScr = Math.round(totalXP);
                        if (finalScr < 0) {
                            finalScr = 0;
                        }
                        if (finalScr > challengerScore) {
                            finalScr = (int) challengerScore;
                        }
                        if (mCorrect >= 1) {
                            isQuestionRightOrWrong = true;
                        }
                        if (isTimeOut) {
                            isCourseQuestionIsRight = false;
                        }
                        if (courseContentHandlingInterface != null) {
                            courseContentHandlingInterface.cancelTimer();
                        }
                        if ((questions.isContainSubjective() && (!isTimeOut))) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.disableNextArrow();
                            }
                            showSubjectiveQuestionPopup();
                        } else {
                            saveDataIntoFireBase("");
                        }
                    } else {
                        //assessment
                        float rightWeight = (float) questionXp / (float) questions.getFillAnswers().size();
                        final float totalXP = rightWeight * mCorrect;

                        boolean isCorrect = false;
                        if (mCorrect == questions.getFillAnswers().size()) {
                            isCorrect = true;
                        }

                        finalScr = Math.round(totalXP);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > questionXp)
                            finalScr = questionXp;

                        try {
                            if ((!isCorrect || isTimeOut) && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                                finalScr = 0;
                                answerSubmit(answers.toString(), 0, false, false, "");
                            } else {
                                answerSubmit(answers.toString(), finalScr, false, isCorrect, "");
                            }
                            if (!nextArrow && !previousArrow) {
                                handleNextQuestion(answers.toString(), "", 0, false, 0);
                            } else if (previousArrow) {
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.gotoPreviousScreen();
                                }
                            } else {
                                handleNextQuestion(answers.toString(), "", 0, false, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                } else {
                    if (nextArrow) {
                        handleNextQuestion(answers.toString(), "", 0, false, 0);
                    } else if (previousArrow) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else {
                        mCorrect = 0;
                        mWrong = 0;
                        Toast.makeText(getActivity(), "" + getResources().getString(R.string.fill_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                //course video overlay
                if (!isAssessmentQuestion) {
                    if (isVideoOverlay()) {
                        learningModuleInterface.setVideoOverlayAnswerAndOc(answers.toString(), "", 0, false, 0, cardId);
                        if (isProceedOnWrong()) {
                            learningModuleInterface.closeChildFragment();
                        } else {
                            new Handler().postDelayed(() -> learningModuleInterface.wrongAnswerAndRestartVideoOverlay(), 1500);
                        }
                    } else {
                        handleNextQuestion(answers.toString(), "", 0, false, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showSubjectiveQuestionPopup() {
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
                        saveDataIntoFireBase(OustSdkTools.getEmojiEncodedString(justifyPopupEditText));
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
                    saveDataIntoFireBase("");
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            });
        } catch (
                Exception e) {
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

    private void saveDataIntoFireBase(String subjectiveResponse) {
        if (questions.isThumbsUpDn()) {
            rightAndWrongQuestionAnswer(isQuestionRightOrWrong, subjectiveResponse, questions.getAnswer(), (int) finalScr, isCourseQuestionIsRight, 0);
        } else {
            if (isQuestionRightOrWrong) {
                if (isVideoOverlay()) {
                    learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), subjectiveResponse, (int) finalScr, true, 0, cardId);
                    new Handler().postDelayed(() -> {
                        try {
                            learningModuleInterface.closeChildFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }, 1500);
                } else {
                    handleNextQuestion(questions.getAnswer(), subjectiveResponse, (int) finalScr, isCourseQuestionIsRight, 0);
                }
            } else {
                if (isVideoOverlay()) {
                    learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), subjectiveResponse, 0, false, 0, cardId);
                    if (isProceedOnWrong()) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.closeChildFragment();
                        }
                    } else {
                        if (learningModuleInterface != null) {
                            new Handler().postDelayed(() -> learningModuleInterface.wrongAnswerAndRestartVideoOverlay(), 1500);
                        }
                    }
                } else {
                    showSolutionAnswer = false;
                    handleNextQuestion(questions.getAnswer(), subjectiveResponse, (int) finalScr, isCourseQuestionIsRight, 0);
                }
            }
        }
    }

    private void rightAndWrongQuestionAnswer(boolean status1, String subjectiveResponse, String userAns, int oc, boolean status, long time) {
        if (status1) {
            rightAnswerSound();
            if (isVideoOverlay()) {
                Log.e(TAG, "rightAndWrongQuestionAnswer: if");
                question_result_image.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(question_result_image, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", (int) finalScr, true, 0, cardId);
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
                            //TODO: handle show solution popup
                            if (!isVideoOverlay()) {
                                handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
                            }
                        } else {
                            animateOcCoins(userAns, subjectiveResponse, oc, status, time);
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
                vibrateAndShake();
                wrongAnswerSound();
                if (isVideoOverlay()) {
                    learningModuleInterface.setVideoOverlayAnswerAndOc(questions.getAnswer(), "", 0, false, 0, cardId);
                    if (isProceedOnWrong()) {
                        if (learningModuleInterface != null) {
                            new Handler().postDelayed(() -> learningModuleInterface.closeChildFragment(), 1500);
                        }
                    } else {
                        if (learningModuleInterface != null) {
                            new Handler().postDelayed(() -> learningModuleInterface.wrongAnswerAndRestartVideoOverlay(), 1500);
                        }
                    }
                } else {
                    showSolutionAnswer = false;
                    handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
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
            answerChosenPlayer.setOnPreparedListener(FIBQuestionFragment::onPrepared);
            answerChosenPlayer.start();
            answerChosenPlayer.setOnCompletionListener(FIBQuestionFragment::onCompletion);
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
            checkVideoExist(videoUrl);
            setPlayerForPortrait(customExoPlayerView.getSimpleExoPlayerView());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    void checkVideoExist(String url) {
        try {
            String videoFileName = OustMediaTools.getMediaFileName(OustMediaTools.removeAwsOrCDnUrl(url));
            String rootPath = OustSdkApplication.getContext().getFilesDir() + File.separator + videoFileName;
            Log.d("TAG", "checkVideoMediaExist:-FIB- path:" + rootPath);
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

    private void answerSubmit(String answer, long oc, boolean isTimeOut, boolean isCorrect, String subjectiveResponse) {
        try {
            if (questions != null) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.setAnswerAndOc(answer, subjectiveResponse, (int) oc, isCorrect, 0);
                }
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

    private void animateOcCoins(String userAns, String subjectiveResponse, int oc, boolean status, long time) {
        try {
            ValueAnimator animator1 = new ValueAnimator();
            Log.e("TAG", "animateOcCoins- isCourse: " + isCourseCompleted);
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
                        coinsAnimText.setText("" + finalScr);
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
            if (!isVideoOverlay()) {
                handleNextQuestion(userAns, subjectiveResponse, oc, status, time);
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
                            } else if (!isQuestionRightOrWrong) {
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
                } else {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoNextScreen();
                    }
                }
            }, FOUR_HUNDRED_MILLI_SECONDS);
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
    public void onDetach() {
        super.onDetach();
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

    @Override
    public void onPause() {
        super.onPause();
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
