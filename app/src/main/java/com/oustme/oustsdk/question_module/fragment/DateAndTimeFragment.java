package com.oustme.oustsdk.question_module.fragment;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.NINE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.oustme.oustsdk.question_module.assessment.AssessmentContentHandlingInterface;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;


public class DateAndTimeFragment extends Fragment {

    private static final String TAG = "DateAndTimeFragment";
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
    TextView choose_date_time;
    FrameLayout question_action_button;
    ImageView question_result_image;
    KatexView question_katex;
    WebView question_description_webView;
    String solutionText;
    boolean showSolutionAnswer = true;

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
    private Scores score;
    private int cardCount;
    long currentSelectedDate;

    boolean isVideoOverlay;
    boolean proceedOnWrong;

    SurveyFunctionsAndClicks surveyFunctionsAndClicks;
    CustomExoPlayerView customExoPlayerView;
    Context context;
    PopupWindow zoomImagePopup;
    private boolean isVideoPlaying;
    String finalVideoUrl;
    TimePickerDialog chosenTimePicker;
    DatePickerDialog chosenDatePicker;

    public DateAndTimeFragment() {
        // Required empty public constructor
    }


    public static DateAndTimeFragment newInstance() {
        return new DateAndTimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            questionBaseViewModel = new ViewModelProvider(requireActivity()).get(QuestionBaseViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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
        View view = inflater.inflate(R.layout.fragment_date_and_time, container, false);

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
        choose_date_time = view.findViewById(R.id.choose_date_time);
        question_action_button = view.findViewById(R.id.question_action_button);
        question_result_image = view.findViewById(R.id.question_result_image);
        question_katex = view.findViewById(R.id.question_katex);
        question_description_webView = view.findViewById(R.id.description_webView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        setIconColors(color, true);
        if (isCourseQuestion) {
            try {
                setQuestionData(questions);
            } catch (ParseException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
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
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            solutionText = courseCardClass.getChildCard().getContent();
            Log.d(TAG, "solution: " + solutionText + " : " + mainCourseCardClass.getChildCard().getContent());
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
            questions = mainCourseCardClass.getQuestionData();
            if (questions == null) {
                if (learningModuleInterface != null) {
                    learningModuleInterface.endActivity();
                }
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
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

    private void setQuestionData(DTOQuestions questions) throws ParseException {
        if (questions != null) {
            if (courseContentHandlingInterface != null) {
                courseContentHandlingInterface.setQuestions(questions);
                if (isReviewMode) {
                    courseContentHandlingInterface.cancelTimer();
                    input_card.setVisibility(View.GONE);
                    question_action_button.setVisibility(View.GONE);
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

            if (questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)) {
                choose_date_time.setHint("Choose Time");
            } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DATE)) {
                choose_date_time.setHint("Choose Date");
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            choose_date_time.setOnClickListener(v -> {
                if (!isReviewMode) {
                    if (questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)) {
                        chosenTimePicker = new TimePickerDialog(Objects.requireNonNull(requireActivity()), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, (timePicker, selectedHour, selectedMinute) -> {
                            String displayTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            SimpleDateFormat displayTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                            try {
                                Date date = timeFormat.parse(displayTime);
                                assert date != null;
                                choose_date_time.setText(displayTimeFormat.format(date));
                                setIconColors(color, true);

                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }

                        }, hour, minute, false);
                        chosenTimePicker.show();
                        Objects.requireNonNull(chosenTimePicker.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_SECURE);

                    } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.DATE)) {
                        chosenDatePicker = new DatePickerDialog(Objects.requireNonNull(requireActivity()), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, (view, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

                            try {
                                Date date = dateFormat.parse(selectedDate);
                                assert date != null;
                                choose_date_time.setText(displayFormat.format(date));
                                setIconColors(color, true);

                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }, year, month, day);
                        chosenDatePicker.show();
                        Objects.requireNonNull(chosenDatePicker.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    }
                }

            });
            question_action_button.setOnClickListener(v -> {
                question_action_button.setEnabled(false);
                setAnswerForDateAndTime(false, false);
                new Handler().postDelayed(() -> question_action_button.setEnabled(true), 1000);
            });

            if (score != null && (isReviewMode || isExamMode)) {
                if (score.getAnswer() != null) {
                    String str = null;
                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(score.getAnswer()));
                    String dateString = formatter2.format(calendar.getTime());

                    if (score.getQuestionType() != null && !score.getQuestionType().isEmpty() && score.getQuestionType().equalsIgnoreCase("DATE")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date value = formatter.parse(dateString);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy"); //this format changeable
                        dateFormatter.setTimeZone(TimeZone.getDefault());
                        assert value != null;
                        str = dateFormatter.format(value);
                    } else if (score.getQuestionType() != null && !score.getQuestionType().isEmpty() && score.getQuestionType().equalsIgnoreCase("TIME")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        Date value = formatter.parse(dateString);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a"); //this format changeable
                        dateFormatter.setTimeZone(TimeZone.getDefault());
                        assert value != null;
                        str = dateFormatter.format(value);
                    }
                    if (str != null) {
                        choose_date_time.setText(str);
                    }
                }
            }
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

    public void setAnswerForDateAndTime(boolean previousArrow, boolean nextArrow) {
        String str = choose_date_time.getText().toString();
        if (questions.getQuestionType().equalsIgnoreCase(QuestionType.TIME)) {
            try {
                if (!str.isEmpty()) {
                    Date currentDate = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(currentDate);
                    String toParse = formattedDate + " " + str;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());
                    Date date = null;
                    date = formatter.parse(toParse);
                    assert date != null;
                    long millis = date.getTime();
                    Date convertedDate = new Date(millis);
                    String requestDate = formatter.format(convertedDate);
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date utcDate = formatter.parse(requestDate);
                        assert utcDate != null;
                        long finalTime = utcDate.getTime();
                        Log.e(TAG, "setAnswerForDateAndTime: " + currentSelectedDate + " -millis- " + millis + " finalTime- " + finalTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    currentSelectedDate = millis;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        } else {
            if (!str.isEmpty()) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                Date date = null;
                try {
                    date = displayFormat.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                assert date != null;
                long millis = date.getTime();
                Date convertedDate = new Date(millis);
                String requestDate = displayFormat.format(convertedDate);
                displayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date utcDate = displayFormat.parse(requestDate);
                    assert utcDate != null;
                    currentSelectedDate = utcDate.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                Log.e(TAG, "setAnswerForDateAndTime: " + currentSelectedDate);
            }
        }
        if (isSurveyQuestion) {
            learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, true, 0);
            learningModuleInterface.gotoNextScreen();
        } else if (isAssessmentQuestion) {
            if (chosenTimePicker != null && chosenTimePicker.isShowing()) {
                chosenTimePicker.cancel();
            }
            if (chosenDatePicker != null && chosenDatePicker.isShowing()) {
                chosenDatePicker.cancel();
            }

            if (!str.isEmpty()) {
                learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", (int) mainCourseCardClass.getXp(), true, 0);
            } else {
                learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
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
            if (!str.isEmpty()) {
                if (questions.isThumbsUpDn()) {
                    rightAnswerSound();
                    question_result_image.setVisibility(View.VISIBLE);
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
                                    learningModuleInterface.setVideoOverlayAnswerAndOc(String.valueOf(currentSelectedDate), "", (int) mainCourseCardClass.getXp(), true, 0, cardId);
                                    learningModuleInterface.closeChildFragment();
                                }
                            } else {
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", (int) mainCourseCardClass.getXp(), true, 0);
                                    handleNextQuestion();
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
                } else {
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setVideoOverlayAnswerAndOc(String.valueOf(currentSelectedDate), "", (int) mainCourseCardClass.getXp(), true, 0, cardId);
                            learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                        }
                    } else {
                        showSolutionAnswer = false;
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", (int) mainCourseCardClass.getXp(), true, 0);
                            handleNextQuestion();
                        }
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
                                learningModuleInterface.setVideoOverlayAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, false, 0, cardId);
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
                                showSolutionAnswer = false;
                                if (learningModuleInterface != null) {
                                    learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, false, 0);
                                    handleNextQuestion();
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

                } else {
                    if (isVideoOverlay()) {
                        if (isProceedOnWrong()) {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.setVideoOverlayAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, false, 0, cardId);
                                learningModuleInterface.closeChildFragment();
                            }
                        } else {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.setVideoOverlayAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, false, 0, cardId);
                                learningModuleInterface.wrongAnswerAndRestartVideoOverlay();
                            }
                        }
                    } else {
                        showSolutionAnswer = false;
                        if (learningModuleInterface != null) {
                            learningModuleInterface.setAnswerAndOc(String.valueOf(currentSelectedDate), "", 0, false, 0);
                            handleNextQuestion();
                        }
                    }
                }
            }
        }
    }

    private void handleNextQuestion() {
        try {
            new Handler().postDelayed(() -> {
                question_result_image.setVisibility(View.GONE);
                if (learningModuleInterface != null) {
                    try {
                        if (solutionText != null && !solutionText.isEmpty()) {
                            if (courseContentHandlingInterface != null) {
                                courseContentHandlingInterface.showSolutionPopUP(solutionText, showSolutionAnswer, false, false, "", "", 0, false, 0);
                            }
                        } else {
                            learningModuleInterface.gotoNextScreen();
                        }
                        showSolutionAnswer = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }, NINE_HUNDRED_MILLI_SECONDS);
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
            answerChosenPlayer.setOnPreparedListener(DateAndTimeFragment::onPrepared);
            answerChosenPlayer.start();
            answerChosenPlayer.setOnCompletionListener(DateAndTimeFragment::onCompletion);
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
                //customExoPlayerView.initExoPlayer(media_question_container, context, videoUrl);
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
            Log.d("TAG", "checkVideoMediaExist:-D&T- path:" + rootPath);
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

            try {
                if (finalVideoUrl != null && isVideoPlaying) {
                    if (customExoPlayerView != null) {
                        isPausedProgrammatically = true;
                        customExoPlayerView.performPauseclick();
                        Log.d(TAG, "onPause: isLauncher: exo_pause onPause");
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

    private boolean isVideoPaused = false;
    private boolean isPausedProgrammatically = false;

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
                    customExoPlayerView.performPauseclick();
                    Log.d(TAG, "onPause: isLauncher: exo_pause onPause::");
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "onResume: isLauncher::" + e.getMessage());
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        Log.d(TAG, "onResume: ");
    }

    public void resumeVideoPlayer() {
        Log.d(TAG, "resumeVideoPlayer: ");
        try {
            if (finalVideoUrl != null && !finalVideoUrl.isEmpty()) {
                Log.d(TAG, "resumeVideoPlayer: inside");
                checkVideoExist(finalVideoUrl);
                setPlayerForPortrait(customExoPlayerView.getSimpleExoPlayerView());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void resetPlayer() {
        try {
            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

}
