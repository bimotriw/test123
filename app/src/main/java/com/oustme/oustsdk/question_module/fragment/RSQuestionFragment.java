package com.oustme.oustsdk.question_module.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.question_module.QuestionBaseViewModel;
import com.oustme.oustsdk.question_module.adapter.QuestionOnItemClickListener;
import com.oustme.oustsdk.question_module.course.CourseQuestionHandling;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.question_module.survey.SurveyFunctionsAndClicks;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.utils.OustResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;


public class RSQuestionFragment extends Fragment implements QuestionOnItemClickListener {


    FrameLayout question_base_frame;
    ImageView question_bgImg;
    LinearLayout main_layout;
    TextView question_count_num;
    TextView question;
    WebView question_description_webView;
    LinearLayout question_image_lay;
    ImageView question_image;
    TextView info_type;
    ImageView expand_icon;
    LinearLayout layout_rs_type;
    LinearLayout layout_rs_label;
    TextView text_minLabel;
    TextView text_maxLabel;
    FlexboxLayout rs_flex_box;
    FrameLayout question_action_button;
    ImageView question_result_image;


    //View Model from Assessment and survey
    QuestionBaseViewModel questionBaseViewModel;
    QuestionBaseModel questionBaseModel;

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
    boolean isCourseQuestion;

    String cardId;
    long questionXp;
    boolean isRandomizeQuestion;
    DTOQuestions questions;

    //additional parameters
    int color;
    boolean showNavigateArrow;
    boolean isAssessmentQuestion;
    boolean isSurveyQuestion;
    private Scores score;
    private int cardCount;
    private boolean isRSType;

    boolean videoOverlay;
    boolean proceedOnWrong;

    int numberOfBox = 5;
    int rs_selected = 0;
    boolean isFirstTime = true;
    String rs_selected_value = "";


    private static final String TAG = "RSQuestionFragment";

    SurveyFunctionsAndClicks surveyFunctionsAndClicks;

    public RSQuestionFragment() {
        // Required empty public constructor
    }

    public static RSQuestionFragment newInstance() {
        return new RSQuestionFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionBaseViewModel = new ViewModelProvider(requireActivity()).get(QuestionBaseViewModel.class);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            surveyFunctionsAndClicks = (SurveyFunctionsAndClicks) context;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_s_question, container, false);
        question_base_frame = view.findViewById(R.id.question_base_frame);
        question_bgImg = view.findViewById(R.id.question_bgImg);
        main_layout = view.findViewById(R.id.main_layout);
        question_count_num = view.findViewById(R.id.question_count_num);
        question = view.findViewById(R.id.question);
        question_description_webView = view.findViewById(R.id.description_webView);
        question_image_lay = view.findViewById(R.id.question_image_lay);
        question_image = view.findViewById(R.id.question_image);
        info_type = view.findViewById(R.id.info_type);
        expand_icon = view.findViewById(R.id.expand_icon);
        layout_rs_type = view.findViewById(R.id.layout_rs_type);
        layout_rs_label = view.findViewById(R.id.layout_rs_label);
        text_minLabel = view.findViewById(R.id.text_minLabel);
        text_maxLabel = view.findViewById(R.id.text_maxLabel);
        rs_flex_box = view.findViewById(R.id.rs_flex_box);
        question_action_button = view.findViewById(R.id.question_action_button);
        question_result_image = view.findViewById(R.id.question_result_image);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = OustResourceUtils.getColors();
        setButtonColor(getResources().getColor(R.color.overlay_container), false);
        //info_type.setText(getResources().getString(R.string.mcq_info));
        info_type.setVisibility(View.INVISIBLE);
        questionBaseViewModel.getQuestionModuleMutableLiveData().observe(requireActivity(), questionBaseModel -> {
            if (questionBaseModel == null)
                return;

            this.questionBaseModel = questionBaseModel;
            setData(questionBaseModel);

        });
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

            if (isAssessmentQuestion) {
                mainCourseCardClass.setXp(questionXp);
            }
        } catch (Exception e) {
            mainCourseCardClass = courseCardClass;
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

    public void setCourseQuestion(boolean isCourseQuestion) {
        this.isCourseQuestion = isCourseQuestion;

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

    public void setRSType(boolean isRSType) {
        this.isRSType = isRSType;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;

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

    private void setData(QuestionBaseModel questionBaseModel) {
        try {
            Log.d(TAG, "setData: isSurveyQuestion:" + isSurveyQuestion);
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
                    Glide.with(getActivity()).load(questionBaseModel.getBgImage()).into(question_bgImg);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }


            questions = questionBaseModel.getQuestions();
            setQuestionData(questions);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionData(DTOQuestions questions) {

        try {
            if (questions != null) {

                if (questions.getQuestion() != null && !questions.getQuestion().isEmpty()) {
                    //question.setText(questions.getQuestion());
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

                if (isCourseQuestion) {
                    question_action_button.setVisibility(View.GONE);
                }

                if (questions.isMandatory()) {
                    setButtonColor(getResources().getColor(R.color.overlay_container), false);
                }

                if ((questions.getBgImg() != null) && (!questions.getBgImg().isEmpty())) {
                    try {
                        Glide.with(getActivity()).load(questions.getBgImg()).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else if (backgroundImage != null && !backgroundImage.isEmpty()) {
                    try {
                        Glide.with(getActivity()).load(backgroundImage).into(question_bgImg);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                setImageQuestionImage(questions.getImageCDNPath(), questions.getImage());

                expand_icon.setOnClickListener(v -> gifZoomPopup(question_image.getDrawable()));

                setRSValue(questions);

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

                   /* question_image_lay.setMinimumHeight((question_image_lay.getWidth()/3)*2);
                    question_image_lay.requestLayout();*/

                } else {
                    if ((image != null) && (!image.isEmpty())) {
                        byte[] imageByte = Base64.decode(image, 0);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                        question_image_lay.setVisibility(View.VISIBLE);
                        expand_icon.setVisibility(View.VISIBLE);
                        question_image.setImageBitmap(decodedByte);

                       /* question_image_lay.setMinimumHeight((question_image_lay.getWidth()/3)*2);
                        question_image_lay.requestLayout();*/

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
            PopupWindow zoomImagePopup = OustSdkTools.createPopUp(popUpView);
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

    public void setRSValue(DTOQuestions questions) {
        try {

            rs_flex_box.removeAllViews();
            rs_flex_box.setFlexDirection(FlexDirection.ROW);
            rs_flex_box.setAlignContent(AlignContent.CENTER);

            if (isRSType) {
                text_minLabel.setText(questions.getMinLabel());
                text_maxLabel.setText(questions.getMaxLabel());

                int startRange = questions.getStartRange();
                int endRange = questions.getEndRange();

                Log.d(TAG, "setRSValue: start:" + startRange + "---end:" + endRange);
                numberOfBox = endRange;
                if (startRange == 0)
                    numberOfBox++;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );

                try {
                    if (isFirstTime && score != null) {
                        rs_selected_value = score.getAnswer();
                        Log.d(TAG, "setupRSQuestionType: " + rs_selected_value);
                        if ((rs_selected_value != null) && (!rs_selected_value.isEmpty())) {
                            rs_selected = Integer.parseInt(rs_selected_value);
                            isFirstTime = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

                DisplayMetrics displayMetrics = OustSdkApplication.getContext().getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int totalBoxWidth = numberOfBox * (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
                int remainingWidth = width - totalBoxWidth;
                int singleBoxWidth = remainingWidth / numberOfBox;
                int singleSideWidth = singleBoxWidth / 2;

                params.setMargins(singleSideWidth, 0, singleSideWidth, 0);
                layout_rs_label.setLayoutParams(params);

                for (int i = startRange; i <= numberOfBox; i++) {
                    CardView view = (CardView) getLayoutInflater().inflate(R.layout.survey_points, rs_flex_box, false);
                    TextView textView = view.findViewById(R.id.txt_points);
                    textView.setText(String.valueOf(i));

                    textView.setBackgroundColor(getResources().getColor(R.color.white_presseda));
                    textView.setTextColor(getResources().getColor(R.color.textBlack));

                    final int currentPos = i;
                    if (!isFirstTime && rs_selected == currentPos) {
                        String toolBarColor = OustPreferences.get("toolbarColorCode");
                        if (toolBarColor == null || toolBarColor.isEmpty()) {
                            toolBarColor = "#01b5a2";
                        }
                        textView.setBackgroundColor(Color.parseColor(toolBarColor));
                        textView.setTextColor(getResources().getColor(R.color.white_presseda));
                    }

                    textView.setOnClickListener(view1 -> {
                        isFirstTime = false;
                        rs_selected = currentPos;
                        rs_selected_value = "" + rs_selected;
                        onItemClicked(1);
                        setRSValue(questions);
                    });

                    CardView.LayoutParams cardViewParams = new CardView.LayoutParams(
                            (int) getResources().getDimension(R.dimen.oustlayout_dimen25),
                            (int) getResources().getDimension(R.dimen.oustlayout_dimen25)
                    );

                    cardViewParams.setMargins(singleSideWidth, 0, singleSideWidth, 0);
                    view.setLayoutParams(cardViewParams);

                    rs_flex_box.addView(view);
                }

                question_action_button.setOnClickListener(v -> {
                    if (isSurveyQuestion) {
                        learningModuleInterface.setAnswerAndOc(rs_selected_value, "", 0, true, 0);
                        learningModuleInterface.gotoNextScreen();
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }


    @Override
    public void onItemClicked(int count) {

        try {
            if (count > 0) {
                setButtonColor(color, true);
            } else {
                setButtonColor(getResources().getColor(R.color.overlay_container), false);
            }
        } catch (Exception e) {

            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public void onAnswerOc(int count) {

    }
}
