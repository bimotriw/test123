package com.oustme.oustsdk.fragments.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.customviews.JumbleWheelCustomLine;
import com.oustme.oustsdk.customviews.JumbleWordCustomCircle;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.response.course.FloatPoint;
import com.oustme.oustsdk.response.course.JumbleWordKeeptrackOfLineModel;
import com.oustme.oustsdk.response.course.JumbleWordUtilityModel;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

/**
 * Created by shilpysamaddar on 14/11/17.
 */

public class JumbleWordFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "JumbleWordFragment";
    
    private RelativeLayout jumbleword_mainlayout, topbar_leftlayout, topbar_timerlayout, jumble_mainquestionlayout, jumble_answer_layout, hint_layout, refresh_layout,
            jumble_wheel_addtext_layout, wheel_tray_layout, solution_board_layout, jumble_questionlayout,
            jumblequestion_solvelayout, popupjumblequestion_solvelayout, question_board_layout, jumble_wheel_layout;

    //    private ImageView learningquiz_rightwrongimage, lpocimage, topbar_question_image, coin_imageview, time_imageview;
//    private TextView preview_textview, mainquestion_text, ocanim_text, jumble_points, timerText, popupmainquestion_text,
//            solve_lable,question_title,popup_solve_lable,downloading_resource_text;
    private RelativeLayout animoc_layout, topbar_question, learningquiz_animviewa, bottom_layer, jumblequestion_submitlayout,
            right_word_layout, confirm_popup_cancel, confirm_popup_submit, topbar_info_layout, wordjumble_exit_layout, w2_reset_layout,
            submit_confirm_background;
    private ScrollView instruction_scrollview;

    private ImageView learningquiz_rightwrongimage, lpocimage, topbar_question_image, coin_imageview, time_imageview, reset_imageview, topbar_exit_imageView,
            topbar_info_image, jumblequestion_submitlayout_bgd, topbar_leftlayout_bgd, topbar_timerlayout_bgd,
            jumblequestion_solvelayout_bgd, bottom_layer_bgd, solution_board_layout_bgd, question_board_layout_bgd,
            hint_layout_bgd, refresh_layout_bgd, popupjumblequestion_solvelayout_bgd, right_word_layout_bgd,
            submit_confirm_background_bgd;
    private TextView preview_textview, mainquestion_text, ocanim_text, jumble_points, timerText, popupmainquestion_text,
            userresponse_text, popup_heading, submit_popup_question, solve_lable, popup_solve_lable,
            downloading_resource_text;
    private KatexView mainquestion_textMaths, popupmainquestion_textMaths;
    private LinearLayout splash_layout, confirm_submitlayout_popup;
    private JumbleWordCustomCircle jumble_wheel;
    private JumbleWheelCustomLine jumble_line;
    private Path linePath;
    private Path drawPath;
    private PointF StartOrgPT;
    private String previewText = "";
    private TextView questionlable, downloadprogresstext, confirmpopup_cancel_layout, confirmpopup_submit_layout;
    private int currentIndex = 0;
    private ImageView quiz_backgroundimagea, confirmpopup_cancel_layout_bgd, confirmpopup_submit_layout_bgd, splash_layout_bgd;
    private boolean comingForFirstTime = false;
    private HtmlTextView instruction_text;

    private int indexforcalculation = 0;

    //    private ArrayList<String> totalOptions;
    private int radius;
    private FloatPoint[] point = new FloatPoint[10];
    private String currentStringOnWheel = "";
    private List<JumbleWordKeeptrackOfLineModel> keeptrackOfLineModelsList;
    private boolean isRefreshClicked = false;
    private String hintString = "";
    private ProgressBar loader;

    private float scoreForWord = 0f;
    private float totalScore = 0f;

    private int numberOfTimesShuffleClicked = 0;
    private int numberOfWrongAttempt = 0;
    private int numberOfTimesHintClicked = 0;
    private int noOfHintAllowed = 0;
    private int hintPercentage = 40;
    private int finalScore = 0;
    private int wrongAttempt = 0;

    private CounterClass timer;

    private LayoutInflater mInflater;
    private List<JumbleWordUtilityModel> jumbleWordModel = new ArrayList<>();

    private DTOQuestions questions;

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    private int totalXp;

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public DTOCourseCard mainCourseCardClass;

    public void setMainCourseCardClass(DTOCourseCard courseCardClass2) {
        try {
            this.mainCourseCardClass = courseCardClass2;
            if (this.mainCourseCardClass.getXp() == 0) {
                this.mainCourseCardClass.setXp(100);
            }
        } catch (Exception e) {
            this.mainCourseCardClass = courseCardClass2;
        }
        questions = this.mainCourseCardClass.getQuestionData();
    }

    private String instruction = "";

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


    private int learningcardProgress = 0;
    private boolean isWordJumble2 = false;

    public void setWordJumble2(boolean wordJumble2) {
        isWordJumble2 = wordJumble2;
    }

    //    private View view;
    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jumbleword, container, false);
        setInitialScreen(view);
        initViews(view);
        return view;
    }

    private void setInitialScreen(View view) {
        loader = view.findViewById(R.id.loader);
        downloadprogresstext = view.findViewById(R.id.downloadprogresstext);
        splash_layout = view.findViewById(R.id.splash_layout);
        OustSdkTools.setImage(splash_layout_bgd, getResources().getString(R.string.splash));
        splash_layout.setVisibility(View.VISIBLE);
    }

    private void startLoader() {
        loader.setVisibility(View.VISIBLE);
        loader.setMax(100);
        downloadprogresstext.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loader.clearAnimation();
        downloadprogresstext.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
    }

    private int scrWidth;
    private int scrHeight;

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    private void initViews(View view) {
        Log.d(TAG, "initViews: ");
        jumbleword_mainlayout = view.findViewById(R.id.jumbleword_mainlayout);
        jumble_mainquestionlayout = view.findViewById(R.id.jumble_mainquestionlayout);
        topbar_leftlayout = view.findViewById(R.id.topbar_leftlayout);
        topbar_leftlayout_bgd = view.findViewById(R.id.topbar_leftlayout_bgd);
        topbar_timerlayout = view.findViewById(R.id.topbar_timerlayout);
        topbar_timerlayout_bgd = view.findViewById(R.id.topbar_timerlayout_bgd);
        topbar_question = view.findViewById(R.id.topbar_question);
        preview_textview = view.findViewById(R.id.preview_textview);
        instruction_text = view.findViewById(R.id.instruction_text);
        instruction_text.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        instruction_scrollview = view.findViewById(R.id.instruction_scrollview);
        wheel_tray_layout = view.findViewById(R.id.wheel_tray_layout);
        jumble_questionlayout = view.findViewById(R.id.jumble_questionlayout);
        question_board_layout = view.findViewById(R.id.question_board_layout);
        question_board_layout_bgd = view.findViewById(R.id.question_board_layout_bgd);
        jumble_answer_layout = view.findViewById(R.id.jumble_answer_layout);
        hint_layout = view.findViewById(R.id.hint_layout);
        hint_layout_bgd = view.findViewById(R.id.hint_layout_bgd);
        refresh_layout = view.findViewById(R.id.refresh_layout);
        refresh_layout_bgd = view.findViewById(R.id.refresh_layout_bgd);
        jumble_wheel = view.findViewById(R.id.jumble_wheel);
        jumble_wheel_addtext_layout = view.findViewById(R.id.jumble_wheel_addtext_layout);
        jumble_line = view.findViewById(R.id.jumble_line);
        solution_board_layout = view.findViewById(R.id.solution_board_layout);
        solution_board_layout_bgd = view.findViewById(R.id.solution_board_layout_bgd);
        jumblequestion_solvelayout = view.findViewById(R.id.jumblequestion_solvelayout);
        jumblequestion_solvelayout_bgd = view.findViewById(R.id.jumblequestion_solvelayout_bgd);
        popupjumblequestion_solvelayout = view.findViewById(R.id.popupjumblequestion_solvelayout);
        popupjumblequestion_solvelayout_bgd = view.findViewById(R.id.popupjumblequestion_solvelayout_bgd);
        jumble_wheel_layout = view.findViewById(R.id.jumble_wheel_layout);
        mainquestion_text = view.findViewById(R.id.mainquestion_text);
        mainquestion_textMaths = view.findViewById(R.id.mainquestion_textMaths);

        learningquiz_rightwrongimage = view.findViewById(R.id.learningquiz_rightwrongimage);
        lpocimage = view.findViewById(R.id.lpocimage);
        ocanim_text = view.findViewById(R.id.ocanim_text);
        jumble_points = view.findViewById(R.id.jumble_points);
        topbar_question_image = view.findViewById(R.id.topbar_question_image);
        timerText = view.findViewById(R.id.timerText);
        questionlable = view.findViewById(R.id.questionlable);
        coin_imageview = view.findViewById(R.id.coin_imageview);
        time_imageview = view.findViewById(R.id.time_imageview);
        popupmainquestion_textMaths = view.findViewById(R.id.popupmainquestion_textMaths);
        popupmainquestion_text = view.findViewById(R.id.popupmainquestion_text);
        learningquiz_animviewa = view.findViewById(R.id.learningquiz_animviewa);
        quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
        bottom_layer = view.findViewById(R.id.bottom_layer);
        bottom_layer_bgd = view.findViewById(R.id.bottom_layer_bgd);
        solve_lable = view.findViewById(R.id.solve_lable);
//        question_title = (TextView) view.findViewById(R.id.question_title);
        popup_solve_lable = view.findViewById(R.id.popup_solve_lable);
        downloading_resource_text = view.findViewById(R.id.downloading_resource_text);
        w2_reset_layout = view.findViewById(R.id.w2_reset_layout);
        reset_imageview = view.findViewById(R.id.reset_imageview);
        topbar_info_image = view.findViewById(R.id.topbar_info_image);
        submit_confirm_background = view.findViewById(R.id.submit_confirm_background);
        submit_confirm_background_bgd = view.findViewById(R.id.submit_confirm_background_bgd);
        jumbleword_mainlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jumbleword_mainlayout.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });

        questionlable.setText(OustStrings.getString("question_text"));
        solve_lable.setText(OustStrings.getString("solve"));
//        question_title.setText(OustStrings.getString("question_text"));
        popup_solve_lable.setText(OustStrings.getString("solve"));
        downloading_resource_text.setText(OustStrings.getString("jumble_downloading_msg"));
        wordjumble_exit_layout = view.findViewById(R.id.wordjumble_exit_layout);
        topbar_info_layout = view.findViewById(R.id.topbar_info_layout);
        popup_heading = view.findViewById(R.id.popup_heading);
        jumblequestion_submitlayout = view.findViewById(R.id.jumblequestion_submitlayout);
        jumblequestion_submitlayout_bgd = view.findViewById(R.id.jumblequestion_submitlayout_bgd);
        right_word_layout = view.findViewById(R.id.right_word_layout);
        right_word_layout_bgd = view.findViewById(R.id.right_word_layout_bgd);
        submit_popup_question = view.findViewById(R.id.submit_popup_question);
        confirmpopup_cancel_layout = view.findViewById(R.id.confirmpopup_cancel_layout);
        confirmpopup_cancel_layout_bgd = view.findViewById(R.id.confirmpopup_cancel_layout_bgd);
        confirmpopup_submit_layout = view.findViewById(R.id.confirmpopup_submit_layout);
        confirmpopup_submit_layout_bgd = view.findViewById(R.id.confirmpopup_submit_layout_bgd);
        confirm_submitlayout_popup = view.findViewById(R.id.confirm_submitlayout_popup);
        confirm_popup_cancel = view.findViewById(R.id.confirm_popup_cancel);
        confirm_popup_submit = view.findViewById(R.id.confirm_popup_submit);
        userresponse_text = view.findViewById(R.id.userresponse_text);
        topbar_exit_imageView = view.findViewById(R.id.topbar_exit_imageView);
        OustSdkTools.setImage(jumblequestion_submitlayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(confirmpopup_cancel_layout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(confirmpopup_submit_layout_bgd, getResources().getString(R.string.bg_word));
//        OustSdkTools.setBackground(confirm_submitlayout_popup, getResources().getString(R.string.splash));
        jumblequestion_submitlayout.setOnClickListener(this);
        confirm_popup_cancel.setOnClickListener(this);
        confirm_popup_submit.setOnClickListener(this);
        topbar_info_layout.setOnClickListener(this);
        wordjumble_exit_layout.setOnClickListener(this);
        right_word_layout.setOnClickListener(this);
        w2_reset_layout.setOnClickListener(this);

        OustSdkTools.setImage(topbar_leftlayout_bgd, (getResources().getString(R.string.bg_word)));
        OustSdkTools.setImage(topbar_timerlayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(jumblequestion_solvelayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(bottom_layer_bgd, getResources().getString(R.string.bottom_layer));
        OustSdkTools.setImage(solution_board_layout_bgd, getResources().getString(R.string.board));
        OustSdkTools.setImage(question_board_layout_bgd, getResources().getString(R.string.instruction_bg));
        OustSdkTools.setImage(hint_layout_bgd, getResources().getString(R.string.hintback));
        OustSdkTools.setImage(refresh_layout_bgd, getResources().getString(R.string.refreshback));
        OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.background_word));//
        OustSdkTools.setImage(popupjumblequestion_solvelayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImageA(coin_imageview, getResources().getString(R.string.coins_word));
        OustSdkTools.setImageA(time_imageview, getResources().getString(R.string.time));
        OustSdkTools.setImageA(topbar_question_image, getResources().getString(R.string.question));
        OustSdkTools.setImage(lpocimage, getResources().getString(R.string.newxp_img));
        OustSdkTools.setImage(learningquiz_rightwrongimage, getResources().getString(R.string.thumbsup));

        jumble_mainquestionlayout.setVisibility(View.VISIBLE);
        animoc_layout = view.findViewById(R.id.animoc_layout);

        setQuestionText();
        startAnimations();
        topBordInitialSetUp();
        getWidth();
        setWordJumblev2Layout();

        jumblequestion_solvelayout.setOnClickListener(this);
        jumble_questionlayout.setOnClickListener(this);
        refresh_layout.setOnClickListener(this);
        hint_layout.setOnClickListener(this);
        topbar_question_image.setOnClickListener(this);
        jumblequestion_submitlayout.setOnClickListener(this);
    }

    private void setWordJumblev2Layout() {
        try {
            Log.d(TAG, "setWordJumblev2Layout: ");
            if (isWordJumble2) {
                wordjumble_exit_layout.setVisibility(View.VISIBLE);
                topbar_exit_imageView.setVisibility(View.VISIBLE);
                jumble_points.setText("" + 100);
                if (!instruction.isEmpty()) {
                    topbar_info_layout.setVisibility(View.VISIBLE);
                }
                OustSdkTools.setImage(topbar_exit_imageView, getResources().getString(R.string.exit_icon));
                OustSdkTools.setImageA(topbar_info_image, getResources().getString(R.string.instruction));
                OustSdkTools.setImageA(reset_imageview, getResources().getString(R.string.shuffle));
                OustSdkTools.setImage(submit_confirm_background_bgd, getResources().getString(R.string.popup_bg));
                OustSdkTools.setImage(right_word_layout_bgd, getResources().getString(R.string.jumble_tick));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionText() {
        try {
            Log.d(TAG, "setQuestionText: ");
            if (questions != null) {
                if(!questions.getQuestion().contains(KATEX_DELIMITER)) {
                    mainquestion_text.setText(questions.getQuestion());
                    popupmainquestion_text.setText(questions.getQuestion());
                    mainquestion_textMaths.setVisibility(View.GONE);
                    popupmainquestion_textMaths.setVisibility(View.GONE);
                    mainquestion_text.setVisibility(View.VISIBLE);
                    popupmainquestion_text.setVisibility(View.VISIBLE);
                }
                else{
                    mainquestion_textMaths.setText(questions.getQuestion());
                    popupmainquestion_textMaths.setText(questions.getQuestion());
                    mainquestion_textMaths.setVisibility(View.VISIBLE);
                    popupmainquestion_textMaths.setVisibility(View.VISIBLE);
                    mainquestion_text.setVisibility(View.GONE);
                    popupmainquestion_text.setVisibility(View.GONE);
                }
            }
            jumble_points.setText("00");
            timerText.setText(questions.getMaxtime() / 60 + ":" + questions.getMaxtime() % 60);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setHintCount() {

    }

    private void startAnimations() {
        Log.d(TAG, "startAnimations: ");
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(splash_layout, "alpha", 1f, 0f);
        fadeOut.setDuration(1200);
        fadeOut.setStartDelay(1000);
        fadeOut.start();
    }

    //===================================================================================
    private int countLine = 1;

    private void startJumbelLayout() {
        try {
            Log.d(TAG, "startJumbelLayout: ");
            
            if (mInflater == null) {
                mInflater = getActivity().getLayoutInflater();
            }
            Spanned s1 = getSpannedContent("");
            if ((questions.getAnswer() != null) && (!questions.getAnswer().isEmpty())) {
                s1 = getSpannedContent(questions.getAnswer());
            }
            String jumbelAnswer = (s1.toString());
            String[] totalOption = jumbelAnswer.split(" ");
            int boxSize = scrWidth / 12;
            int x = 0;
            int xStart = boxSize;
            int yStart = boxSize - 4;
//        int yStart = 10;
            for (int i = 0; i < totalOption.length; i++) {
                String option = totalOption[i];
                List<View> jumbleBoxes = new ArrayList<>();
                JumbleWordUtilityModel model = new JumbleWordUtilityModel();

                LinearLayout background = new LinearLayout(getActivity());
                background.setX(xStart);
                background.setY(yStart);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                background.setLayoutParams(params1);
                background.setId(100 + i);
                background.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < option.length(); j++) {
                    if (countLine < 6) {
                        View jumbleView = mInflater.inflate(R.layout.jumble_answer_textview, null);
                        RelativeLayout charBack = jumbleView.findViewById(R.id.charBack);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) charBack.getLayoutParams();
                        params.width = boxSize;
                        params.height = boxSize;
                        jumbleView.setLayoutParams(params);
                        jumbleView.setX(xStart);
                        jumbleView.setY(yStart);
                        jumbleView.setVisibility(View.VISIBLE);
                        jumbleBoxes.add(jumbleView);
                        jumble_answer_layout.addView(jumbleView);

                        View jumbleViewforbackground = mInflater.inflate(R.layout.jumble_answer_textview, null);
                        jumbleViewforbackground.setX(xStart);
                        jumbleViewforbackground.setY(yStart);
                        jumbleViewforbackground.setVisibility(View.VISIBLE);
                        background.addView(jumbleViewforbackground);

                        x++;
                        xStart += boxSize;
                        if (x > 10) {
                            x = 0;
                            yStart += (boxSize + 4);
                            xStart = boxSize;
                            countLine++;
                        }
                    }
                }

                if (isWordJumble2) {
                    ViewGroup.LayoutParams params = background.getLayoutParams();
                    params.width = ((boxSize * option.length()));
                    background.setLayoutParams(params);
                    background.setOnClickListener(this);
                    jumble_answer_layout.addView(background);
                }

                if (totalOption.length > (i + 1)) {
                    if ((x + totalOption[i + 1].length() > 9) && (x > 0)) {
                        x = 0;
                        yStart += (boxSize + 4);
                        xStart = boxSize;
                        countLine++;
                    } else {
                        x++;
                        xStart += boxSize;
                    }
                }
                if (jumbleBoxes.size() > 0) {
                    model.setJumbleBoxes(jumbleBoxes);
                    model.setString1(option);
                    model.setAttempted(false);
                    model.setBackground(background);
                    jumbleWordModel.add(model);
                }
            }
            jumble_wheel_addtext_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onTouchOfWheelText(v, event);
                    return true;
                }
            });
            comingForFirstTime = true;
            appendExtraCharThenDraw();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //        add two random character if length is less than two
    private void appendExtraCharThenDraw() {
        try {
            boolean wordsleft = false;
            if (!jumbleWordModel.get(currentIndex).isAttempted()) {
                wordsleft = true;
                proceedToNextStep();
            } else {
                for (int i = 0; i < jumbleWordModel.size(); i++) {
                    if (currentIndex < jumbleWordModel.size()) {
                        if (!jumbleWordModel.get(i).isAttempted()) {
                            currentIndex = i;
                            wordsleft = true;
                            proceedToNextStep();
                            break;
                        }
                    }
                }
            }
            if (!wordsleft) {
                if (isWordJumble2) {
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                        (jumbleWordModel.get(currentIndex).getBackground()).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.jumble_confirm_roundedcorner));
//                            OustSdkTools.setLayoutBackgroud((jumbleWordModel.get(currentIndex).getBackground()), R.drawable.jumble_confirm_roundedcorner);
                            wordsended = true;
                            showSubmitLayout(false);
//                            right_word_layout.setVisibility(View.VISIBLE);
//                            OustSdkTools.setBackground(right_word_layout, getResources().getString(R.string.cross));
//                        OustSdkTools.setLayoutBackgroud(right_word_layout,R.drawable.cross);
                        }
                    }, 500);
//                calculatePointForW2();
                } else {
                    calculateOc(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean wordsended = false;

    private void proceedToNextStep() {

        try {
            Log.d(TAG, "proceedToNextStep: ");
            indexforcalculation = currentIndex;
            currentStringOnWheel = jumbleWordModel.get(currentIndex).getString1();
//
////        if (currentStringOnWheel.length() < 8)
////            currentStringOnWheel += addExtraConsonentInString();
////        if (currentStringOnWheel.length() < 8)
////            currentStringOnWheel += addExtraCharInString();
            if (currentStringOnWheel.length() == 2) {
                currentStringOnWheel += addExtraConsonentInString();
                currentStringOnWheel += addExtraVowelInString();
                currentStringOnWheel += addExtraVowelInString();
            } else if (currentStringOnWheel.length() < 5) {
                currentStringOnWheel += addExtraConsonentInString();
                currentStringOnWheel += addExtraVowelInString();
                currentStringOnWheel += addExtraVowelInString();
                if (currentStringOnWheel.length() < 7)
                    currentStringOnWheel += addExtraVowelInString();
            } else if (currentStringOnWheel.length() < 10) {
                for (int i = 0; i <= 5; i++) {
                    if (currentStringOnWheel.length() < 10) {
                        currentStringOnWheel += addExtraVowelInString();
                    }
                    if (currentStringOnWheel.length() < 10) {
                        currentStringOnWheel += addExtraConsonentInString();
                    } else {
                        break;
                    }
                }
            }
//
//=======
//        (jumbleWordModel.get(currentIndex).getBackground()).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.jumble_confirm_roundedcorner));
            OustSdkTools.setLayoutBackgroud((jumbleWordModel.get(currentIndex).getBackground()), R.drawable.jumble_confirm_roundedcorner);
            if (currentStringOnWheel.length() < 8)
                currentStringOnWheel += addExtraCharInString();
            if (currentStringOnWheel.length() < 8)
                currentStringOnWheel += addExtraCharInString();
            getCoordinatesForBottomWheelLayout(currentStringOnWheel);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public String addExtraCharInString() {
        Random random = new Random();
        String alphabetStribg = "abcdefghizklmnopqrstuvwxyz";
        String extraChar = alphabetStribg.charAt(random.nextInt(alphabetStribg.length())) + "";
        return extraChar;
    }

    private void getCoordinatesForBottomWheelLayout(String teststring) {
        try {
            Path path;
            int width = (int) getResources().getDimension(R.dimen.oustlayout_dimen200);
            int height = (int) getResources().getDimension(R.dimen.oustlayout_dimen200);
            radius = width / 2;
            path = new Path();
            path.addCircle(width / 2, height / 2, radius, Path.Direction.CW);
            jumble_wheel.setPath(path);

            PathMeasure measure = new PathMeasure(path, false);
            float length = measure.getLength();
            float speed = length / teststring.length();
            float[] aCoordinates = new float[2];
            measure.getPosTan(measure.getLength() * teststring.length(), aCoordinates, null);
            int counter = 0;
            float distance = 0f;
            while ((distance < length) && (counter < teststring.length())) {
                measure.getPosTan(distance, aCoordinates, null);
                point[counter] = new FloatPoint(aCoordinates[0], aCoordinates[1]);
                counter++;
                distance = distance + speed;
            }
            if (isRefreshClicked || (!comingForFirstTime)) {
                rotateWheel();
                isRefreshClicked = false;
            }
            comingForFirstTime = false;
            Random random = new Random();
            float percentage = hintPercentage;
            float strLength = currentStringOnWheel.length();
            noOfHintAllowed = (int) (strLength * (percentage / 100.0));
            if (noOfHintAllowed < 1) {
                noOfHintAllowed = 1;
            }
            if (noOfHintAllowed >= strLength) {
                noOfHintAllowed = (int) (strLength - 1);
            }
            currentStringOnWheel = scramble(random, currentStringOnWheel);
            drawTextOnBottomWheelLayout(currentStringOnWheel, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void drawTextOnBottomWheelLayout(String teststring, boolean showAnim) {
        try {
            if (mInflater == null) {
                mInflater = getActivity().getLayoutInflater();
            }
            keeptrackOfLineModelsList = new ArrayList<>();
            jumble_wheel_addtext_layout.removeAllViews();
            for (int i = 0; i < teststring.length(); i++) {
                JumbleWordKeeptrackOfLineModel lineModel = new JumbleWordKeeptrackOfLineModel();
                View jumbleTextView = mInflater.inflate(R.layout.jumble_wheel_textlayout, null);
                TextView jumbleText = jumbleTextView.findViewById(R.id.wheel_text);
                jumbleText.setText("" + teststring.charAt(i));
                float x = point[i].getX();
                float y = point[i].getY();
                jumbleTextView.setX(x);
                jumbleTextView.setY(y);
                jumble_wheel_addtext_layout.addView(jumbleTextView);
                if (showAnim) {
                    setAnimation(jumbleTextView, i);
                }
                lineModel.setIndex(i);
                lineModel.setAttemptStatus(0);
                lineModel.setView(jumbleTextView);
                keeptrackOfLineModelsList.add(lineModel);
                jumble_wheel_addtext_layout.getParent().requestDisallowInterceptTouchEvent(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void rotateWheel() {
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(wheel_tray_layout, "rotation", 0f, 360f);
        imageViewObjectAnimator.setDuration(800); // miliseconds
        imageViewObjectAnimator.start();
    }

    private int[] addedPoints = new int[20];
    private int noofPointsAdded = 0;
    private boolean firstTimeMoved = false;
    private int currentCharNo = -1;
    private int lastCharNo = 0;
    private int nextCharNo = 0;

    public boolean onTouchOfWheelText(View v, MotionEvent event) {
        try {
            int action = event.getAction();
            if (!gameOver) {
                if (action == MotionEvent.ACTION_MOVE) {
                    if (StartOrgPT != null && linePath != null) {
                        linePath.reset();
                        linePath.moveTo(StartOrgPT.x, StartOrgPT.y);
                        linePath.lineTo(event.getX(), event.getY());
                        jumble_line.setLinePath(linePath);
                    }
                    for (int i = 0; i < keeptrackOfLineModelsList.size(); i++) {
                        if ((linePath != null) && (drawPath != null)) {
                            View jumbleTextView = keeptrackOfLineModelsList.get(i).getView();
                            TextView jumbleText = jumbleTextView.findViewById(R.id.wheel_text);
                            float local_x = event.getX();
                            float local_y = event.getY();
                            float expectedStartX = jumbleTextView.getX();
                            float expectedStartY = jumbleTextView.getY();
                            float expectedEndX = expectedStartX + jumbleTextView.getWidth();
                            float expectedEndY = expectedStartY + jumbleTextView.getHeight();
                            if ((local_x > expectedStartX) && (local_x < expectedEndX) && (local_y > expectedStartY) && (local_y < expectedEndY)) {
                                if ((currentCharNo < 0) || (currentCharNo != i)) {
                                    currentCharNo = i;
                                    String letter = "";
                                    if (keeptrackOfLineModelsList.get(i).getAttemptStatus() == 0) {
                                        keeptrackOfLineModelsList.get(i).setAttemptStatus(1);
                                        addedPoints[noofPointsAdded] = i;
                                        noofPointsAdded += 1;
                                        lastCharNo = i;
                                        letter = jumbleText.getText().toString();
                                        previewText += jumbleText.getText().toString();
                                        if (previewText.length() > hintString.length()) {
                                            preview_textview.setText(previewText);
                                        }
                                        drawFinalPath();
                                        StartOrgPT = new PointF((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                        linePath.reset();
                                        linePath.moveTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                    } else if (keeptrackOfLineModelsList.get(i).getAttemptStatus() == 1) {
                                        if (((noofPointsAdded > 1) || (firstTimeMoved)) && (lastCharNo == i)) {
                                            firstTimeMoved = true;
                                            //if(noofPointsAdded>1) {
                                            // keeptrackOfLineModelsList.get(i).setAttemptStatus(0);
                                            // }
                                            if (keeptrackOfLineModelsList.size() > (nextCharNo)) {
                                                keeptrackOfLineModelsList.get(nextCharNo).setAttemptStatus(0);
                                            }
                                            noofPointsAdded--;
                                            currentCharNo = i;
                                            if (previewText.length() > 0) {
                                                previewText = (previewText.substring(0, previewText.length() - 1));
                                            }
                                            if (previewText.length() > hintString.length()) {
                                                preview_textview.setText(previewText);
                                            }
                                            letter = jumbleText.getText().toString();
                                            drawFinalPath();
                                            StartOrgPT = new PointF((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                            linePath.reset();
                                            linePath.moveTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                        }
                                    }
//                                Log.e("----------------",""+letter);
//                                Log.e("----------------",""+currentCharNo);
//                                Log.e("----------------",""+noofPointsAdded);
//                                Log.e("----------------",""+keeptrackOfLineModelsList.get(i).getAttemptStatus());
//                                Log.e("-----------------------",""+addedPoints[0]+" "+addedPoints[1]+" "+addedPoints[2]+" "+addedPoints[3]+" "+addedPoints[4]);
//                                Log.e("-----------------------",""+keeptrackOfLineModelsList.get(0).getAttemptStatus()+" "+keeptrackOfLineModelsList.get(1).getAttemptStatus()+" "+keeptrackOfLineModelsList.get(2).getAttemptStatus()+" "+keeptrackOfLineModelsList.get(3).getAttemptStatus()+" "+keeptrackOfLineModelsList.get(4).getAttemptStatus());
//                                Log.e("-----------------------","----------------------");
                                }
                            }

                        }
                    }

                } else if (action == MotionEvent.ACTION_DOWN) {
                    if (!wordsended && !submitPopupShowing) {
                        resetData();
                        previewText = "";
                        for (int i = 0; i < keeptrackOfLineModelsList.size(); i++) {
                            if (keeptrackOfLineModelsList.get(i).getAttemptStatus() == 0) {
                                View jumbleTextView = keeptrackOfLineModelsList.get(i).getView();
                                TextView jumbleText = jumbleTextView.findViewById(R.id.wheel_text);
                                float local_x = event.getX();
                                float local_y = event.getY();
                                float expectedStartX = jumbleTextView.getX();
                                float expectedStartY = jumbleTextView.getY();
                                float expectedEndX = expectedStartX + jumbleTextView.getWidth();
                                float expectedEndY = expectedStartY + jumbleTextView.getHeight();
                                if ((local_x > expectedStartX) && (local_x < expectedEndX) && (local_y > expectedStartY) && (local_y < expectedEndY)) {
                                    right_word_layout.setVisibility(View.GONE);
                                    linePath = new Path();
                                    drawPath = new Path();
                                    linePath.moveTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                    drawPath.moveTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                    StartOrgPT = new PointF((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
                                    keeptrackOfLineModelsList.get(i).setAttemptStatus(1);
                                    noofPointsAdded = 1;
                                    firstTimeMoved = false;
                                    addedPoints[0] = i;
                                    previewText += jumbleText.getText().toString();
                                    if (previewText.length() > hintString.length()) {
                                        preview_textview.setText(previewText);
                                    }
                                }
                            }
                        }
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    if (linePath != null && drawPath != null) {
                        linePath.reset();
                        linePath = new Path();
                        jumble_line.setLinePath(linePath);
                        linePath = null;
                        drawPath.reset();
                        drawPath = new Path();
                        jumble_line.setDrawpath(drawPath);
                        drawPath = null;
                        StartOrgPT = null;
                        addedPoints = new int[20];
                        noofPointsAdded = 0;
                        currentCharNo = -1;

                        textIsDrawnAndFingersUp();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("JumbleWordFragmnet", "" + noofPointsAdded);
        }
        return true;
    }

    private void rightWordTickVisible() {
        right_word_layout.setVisibility(View.VISIBLE);
        OustSdkTools.setImage(right_word_layout_bgd, getResources().getString(R.string.jumble_tick));
    }

    private void drawFinalPath() {
        drawPath = new Path();
        for (int j = 0; j < noofPointsAdded; j++) {
            View jumbleTextView = keeptrackOfLineModelsList.get(addedPoints[j]).getView();
            float expectedStartX = jumbleTextView.getX();
            float expectedStartY = jumbleTextView.getY();
            if (j == 0) {
                drawPath.moveTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
            } else {
                drawPath.lineTo((expectedStartX + (jumbleTextView.getWidth() / 2)), (expectedStartY + (jumbleTextView.getHeight() / 2)));
            }
            if (j > 0) {
                lastCharNo = addedPoints[j - 1];
            }
            nextCharNo = addedPoints[j];
        }
        jumble_line.setDrawpath(drawPath);
    }

    private void resetData() {
        try {
            previewText = "";
            preview_textview.setText(hintString);
            if (linePath != null && drawPath != null) {
                linePath.reset();
                linePath = new Path();
                jumble_line.setLinePath(linePath);
                linePath = null;
                drawPath.reset();
                drawPath = new Path();
                jumble_line.setDrawpath(drawPath);
                drawPath = null;
                StartOrgPT = null;
                addedPoints = new int[20];
                noofPointsAdded = 0;
                currentCharNo = -1;
            }
            if (keeptrackOfLineModelsList != null && keeptrackOfLineModelsList.size() > 0) {
                for (int i = 0; i < keeptrackOfLineModelsList.size(); i++) {
                    keeptrackOfLineModelsList.get(i).setAttemptStatus(0);
                }
            }
            right_word_layout.setVisibility(View.GONE);

            if (jumbleWordModel.get(currentIndex).isAttempted()) {
                preview_textview.setText(jumbleWordModel.get(currentIndex).getUserAnswer());
                OustSdkTools.setImage(right_word_layout_bgd, getResources().getString(R.string.cross));
                right_word_layout.setVisibility(View.VISIBLE);
    //            clearAttemptedWord();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongAttempt() {
        int ansLength = previewText.length();
        resetData();
        if (ansLength > 1) {
            vibrateandShake();
            wrongWordSound();
            numberOfWrongAttempt++;
            wrongAttempt++;
            drawTextOnBottomWheelLayout(currentStringOnWheel, false);
        }
    }

    private void textIsDrawnAndFingersUp() {
        try {
            boolean alreadytextShown = false;
            if (isWordJumble2) {
                if ((previewText.length() == (jumbleWordModel.get(currentIndex).getString1().length()))) {
                    alreadytextShown = true;
                    rightWordTickVisible();
                }
            } else {
                for (int i = 0; i < jumbleWordModel.size(); i++) {
                    if ((previewText.equalsIgnoreCase((jumbleWordModel.get(i).getString1()))) && (!jumbleWordModel.get(i).isAttempted())) {
                        alreadytextShown = true;
                        jumbleWordModel.get(i).setAttempted(true);
                        indexforcalculation = i;
                        showTextOnSolutionBox(previewText, true);
                        previewText = "";
                        preview_textview.setText(previewText);
                    }
                }
            }
            if (!alreadytextShown) {
                wrongAttempt();
            }
            if (((numberOfWrongAttempt + numberOfTimesHintClicked) >= 100) && (!alreadytextShown)) {
                jumbleWordModel.get(currentIndex).setAttempted(true);
                showTextOnSolutionBox(jumbleWordModel.get(currentIndex).getString1(), false);
                previewText = "";
                preview_textview.setText(previewText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void proceedToShowSolutionBox() {
        jumbleWordModel.get(currentIndex).setAttempted(true);
        indexforcalculation = currentIndex;
        showTextOnSolutionBox(previewText, true);
        previewText = "";
        preview_textview.setText(previewText);
        right_word_layout.setVisibility(View.GONE);
    }

    private boolean submitClicked = false;
    private boolean submitPopupShowing = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.jumblequestion_solvelayout) {
            startGameAnimation();
        } else if (v.getId() == R.id.jumble_questionlayout) {
            questionDisAppearAnim();
        }
        if (v.getId() == R.id.refresh_layout) {
            if (numberOfTimesShuffleClicked < 3) {
                isRefreshClicked = true;
                playShuffleMusic();
                previewText = "";
                numberOfTimesShuffleClicked++;
                preview_textview.setText(hintString);
                getCoordinatesForBottomWheelLayout(currentStringOnWheel);
                wrongAttempt++;

            }
        }
        if (v.getId() == R.id.w2_reset_layout) {
            if (isWordJumble2 && (!wordsended)) {
                isRefreshClicked = true;
                playShuffleMusic();
                getCoordinatesForBottomWheelLayout(currentStringOnWheel);
                resetData();
            }
        }
        if (v.getId() == R.id.hint_layout) {
            if (numberOfTimesHintClicked < noOfHintAllowed) {
                playrHintMusic();
                hintString += "" + jumbleWordModel.get(currentIndex).getString1().charAt(numberOfTimesHintClicked);
                preview_textview.setText(hintString);
                numberOfTimesHintClicked++;
                wrongAttempt++;
            }
        }
        if (v.getId() == R.id.topbar_question_image) {
            if (!submitPopupShowing) {
                setQuestionPopup();
                questionAppearAnim();
                playJumblePopupSound();
            }
        }
        if (v.getId() == R.id.topbar_info_layout) {
            if (!submitPopupShowing) {
                setInfoPopup();
                questionAppearAnim();
                playJumblePopupSound();
            }
        }
        if (v.getId() == R.id.right_word_layout) {
            if (previewText != null && (!previewText.isEmpty())) {
                proceedToShowSolutionBox();
            } else {
                clearAttemptedWord();
            }
        }
        if (v.getId() == R.id.jumblequestion_submitlayout) {
            if (!submitPopupShowing) {
                showSubmitLayout(false);
            }
        }
        if (v.getId() == R.id.confirm_popup_cancel) {
            confirm_submitlayout_popup.setVisibility(View.GONE);
            submitPopupShowing = false;
            learningModuleInterface.disableBackButton(false);

        }
        if (v.getId() == R.id.confirm_popup_submit) {
            if (!submitClicked) {
                submitClicked = true;
                calculatePointForW2();
            }
//            calculateOc(true);
//            learningModuleInterface.gotoNextScreen();
        }
        if (v.getId() == R.id.wordjumble_exit_layout) {
            learningModuleInterface.endActivity();
        }
        if ((v.getId()) == 100) {
            selectedWordChanged(0);
        }
        if ((v.getId()) == 101) {
            selectedWordChanged(1);
        }
        if ((v.getId()) == 102) {
            selectedWordChanged(2);
        }
        if ((v.getId()) == 103) {
            selectedWordChanged(3);
        }
        if ((v.getId()) == 104) {
            selectedWordChanged(4);
        }
    }

    private void clearAttemptedWord() {
        try {
            jumbleWordModel.get(currentIndex).setAttempted(false);
            jumbleWordModel.get(currentIndex).setUserAnswer("");
            right_word_layout.setVisibility(View.GONE);
            preview_textview.setText("");
            previewText = "";
            for (int j = 0; j < jumbleWordModel.get(currentIndex).getJumbleBoxes().size(); j++) {
                View jubleView = jumbleWordModel.get(currentIndex).getJumbleBoxes().get(j);
                TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                jumbleText.setVisibility(View.VISIBLE);
                jumbleText.setText("");
                OustSdkTools.setLayoutBackgroud(jumbleText, R.drawable.empty_text);
                showRightAnimation(jubleView, j);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionPopup() {
        instruction_scrollview.setVisibility(View.GONE);
        popup_heading.setText("Question");
        popupmainquestion_text.setVisibility(View.VISIBLE);
        popupmainquestion_text.setText(questions.getQuestion());
    }

    private void setInfoPopup() {
        popupmainquestion_text.setVisibility(View.GONE);
        popup_heading.setText("Instructions");
        instruction_scrollview.setVisibility(View.VISIBLE);
        instruction_text.setHtml(instruction);
    }

    private void showSubmitLayout(boolean jumbleComplete) {
        try {
            submitPopupShowing = true;
            learningModuleInterface.disableBackButton(true);

            SubmitAppearAnim();
            submit_popup_question.setText(questions.getQuestion());
            if (jumbleComplete) {
                confirm_popup_cancel.setVisibility(View.GONE);
            }
            String responsetext = "";
            if (jumbleWordModel != null) {
                for (int i = 0; i < jumbleWordModel.size(); i++) {
                    if (jumbleWordModel.get(i).getUserAnswer() != null && (!jumbleWordModel.get(i).getUserAnswer().isEmpty())) {
                        responsetext += jumbleWordModel.get(i).getUserAnswer();
                    } else {
                        for (int j = 0; j < jumbleWordModel.get(i).getString1().length(); j++) {
                            responsetext += "_";
                        }

                    }
                    responsetext += " ";
                }
                userresponse_text.setText(responsetext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void SubmitAppearAnim() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(confirm_submitlayout_popup, "scaleX", 0f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(confirm_submitlayout_popup, "scaleY", 0f, 1.0f);
        scaleDownX.setDuration(300);
        scaleDownY.setDuration(300);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        confirm_submitlayout_popup.setVisibility(View.VISIBLE);
    }


    private void selectedWordChanged(int index) {
        try {
            if (!submitPopupShowing) {
                resetData();
                if (jumbleWordModel.get(index).isAttempted() && (currentIndex != index || wordsended)) {
                    wordsended = false;
                    if (currentIndex < jumbleWordModel.size()) {
                        jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                    }
                    currentIndex = index;
                    right_word_layout.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(right_word_layout_bgd, getResources().getString(R.string.cross));
                    proceedToNextStep();
                    preview_textview.setText(jumbleWordModel.get(currentIndex).getUserAnswer());
                } else if (currentIndex != index) {
                    previewText = "";
                    preview_textview.setText("");
                    rightWordTickVisible();
                    right_word_layout.setVisibility(View.GONE);
                    jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                    currentIndex = index;
                    appendExtraCharThenDraw();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void questionAppearAnim() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_board_layout, "scaleX", 0f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_board_layout, "scaleY", 0f, 1.0f);
        scaleDownX.setDuration(300);
        scaleDownY.setDuration(300);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        jumble_questionlayout.setVisibility(View.VISIBLE);
    }

    private void questionDisAppearAnim() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(question_board_layout, "scaleX", 1.0f, 0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(question_board_layout, "scaleY", 1.0f, 0f);
        scaleDownX.setDuration(300);
        scaleDownY.setDuration(300);
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
                jumble_questionlayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }


    private void showTextOnSolutionBox(String previewText, boolean userAttempted) {
        try {
            hintString = "";
            jumbleWordModel.get(indexforcalculation).getBackground().setBackgroundDrawable(null);
            jumbleWordModel.get(indexforcalculation).setUserAnswer(previewText);
            for (int j = 0; j < jumbleWordModel.get(indexforcalculation).getJumbleBoxes().size(); j++) {
                View jubleView = jumbleWordModel.get(indexforcalculation).getJumbleBoxes().get(j);
                TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                jumbleText.setVisibility(View.VISIBLE);
                jumbleText.setText("" + previewText.charAt(j));
                OustSdkTools.setLayoutBackgroud(jumbleText, R.drawable.letter_bg);
                showRightAnimation(jubleView, j);
            }
            appendExtraCharThenDraw();
            if (isWordJumble2) {

            } else {
                calculateXpForWord(userAttempted);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void calculateXpForWord(boolean userAttempted) {
        try {
            float weight = totalXp;
            float totalScoreForWord = (weight / (jumbleWordModel.size()));

            if (userAttempted) {
                float negativescore = (totalScoreForWord / 4f);
                scoreForWord = totalScoreForWord - ((numberOfWrongAttempt * negativescore) + (numberOfTimesHintClicked * negativescore) + (numberOfTimesShuffleClicked * 2));
            }
            if (scoreForWord < 0) {
                scoreForWord = 0;
            } else if (scoreForWord > totalScoreForWord) {
                scoreForWord = totalScoreForWord;
            }
            totalScore += scoreForWord;
            numberOfWrongAttempt = 0;
            numberOfTimesHintClicked = 0;
            numberOfTimesShuffleClicked = 0;
            finalScore += Math.round(totalScore);
            if (finalScore > weight) {
                finalScore = (int) weight;
            }
            if (finalScore < 0) {
                finalScore = 0;
            }
            jumble_points.setText("" + (finalScore));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean gameOver = false;

    private void calculateOc(final boolean wordsleft) {
        try {
            if (!gameOver) {
                gameOver = true;
                myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalScore <= 0) {
                            finalScore = 0;
                            answerSubmit(false);
                        } else {
                            answerSubmit(true);
                        }
                    }
                }, 800);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void calculatePointForW2() {
        try {
            int rightUserAnswer = 0;
            for (int i = 0; i < jumbleWordModel.size(); i++) {
                if (jumbleWordModel.get(i).getUserAnswer() != null && (!jumbleWordModel.get(i).getUserAnswer().isEmpty())) {
                    if ((jumbleWordModel.get(i).getString1().equalsIgnoreCase(jumbleWordModel.get(i).getUserAnswer()))) {
                        rightUserAnswer++;
                    }
                }
            }
            float weight = totalXp;
            float totalScoreForWord = (weight / (jumbleWordModel.size()));
            finalScore = Math.round((totalScoreForWord * rightUserAnswer) - (5 * wrongAttempt));
            if (finalScore <= 0) {
                finalScore = 0;
            }
            long totalTimeTaken=calculateTotalTimeTaken();
            learningModuleInterface.setAnswerAndOc("", "", finalScore, true, totalTimeTaken);
            myHandler = new Handler();
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    learningModuleInterface.gotoNextScreen();
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private long calculateTotalTimeTaken(){
        long totalTimeTaken = answeredSeconds * 1000;
        long currentTime = System.currentTimeMillis();
        if ((currentTime == 0) || (totalTimeTaken == 0)) {
        } else {
            totalTimeTaken = currentTime - jumbleStartTime;
            if (totalTimeTaken < 10) {
                totalTimeTaken = answeredSeconds * 1000;
            }
        }
        return totalTimeTaken;
    }

    private void answerSubmit(boolean status) {
        try {
            long totalTimeTaken=calculateTotalTimeTaken();
            learningModuleInterface.setAnswerAndOc("", "", finalScore, true,totalTimeTaken);
            startOverAnimation(status);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void rightwrongFlipAnimation(boolean status) {
        try {
            if (status) {
                rightAnswerSound();
            } else {
                wrongAnswerSound();
                long totalTimeTaken=calculateTotalTimeTaken();
                learningModuleInterface.setAnswerAndOc("", "", 0, false, totalTimeTaken);
                OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
            }

            animoc_layout.setVisibility(View.VISIBLE);
            animoc_layout.bringToFront();
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_rightwrongimage, "scaleX", 0.0f, 1);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_rightwrongimage, "scaleY", 0.0f, 1);
            scaleDownX.setDuration(500);
            scaleDownY.setDuration(500);
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
                    animateOcCoins();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void animateOcCoins() {
        try {
            if ((finalScore > 0)) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                playAudio("coins.mp3");
            }
            ValueAnimator animator1 = new ValueAnimator();
            animator1.setObjectValues(0, (finalScore));
            animator1.setDuration(600);
            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ocanim_text.setText("" + (((int) animation.getAnimatedValue())));
                }
            });
            animator1.start();
            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ocanim_text.setText("" + finalScore);
                    learningModuleInterface.gotoNextScreen();
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

    private Handler myHandler;
    private MediaPlayer mediaPlayer;

    private void stopMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongAnswerSound() {
        stopMediaPlayer();
        playAudio("answer_incorrect.mp3");

    }

    private void rightAnswerSound() {
        stopMediaPlayer();
        playAudio("answer_correct.mp3");
    }

    private void playrHintMusic() {
        stopMediaPlayer();
        playAudio("jumble_hint.mp3");
    }

    private void playShuffleMusic() {
        stopMediaPlayer();
        playAudio("jumble_shuffle.wav");
    }

    private void playJumblePopupSound() {
        stopMediaPlayer();
        playAudio("jumble_popup.wav");
    }

    private void wrongWordSound() {
        stopMediaPlayer();
        playAudio("jumble_wrong.mp3");
    }

//    private void wrongAnswerSound(){
//        try {
//            if(mediaPlayer==null){
//                mediaPlayer=new MediaPlayer();
//            }
//            playAudio("answer_incorrect.mp3");
//        }catch (Exception e){}
//    }
//    private void rightAnswerSound(){
//        try {
//            if(mediaPlayer==null){
//                mediaPlayer=new MediaPlayer();
//            }
//            playAudio("answer_correct.mp3");
//        }catch (Exception e){
//            e.printStackTrace();
            //OustSdkTools.sendSentryException(e);
//        }
//    }

    public String scramble(Random random, String inputString) {
        // Convert your string into a simple char array:
        char[] a = inputString.toCharArray();
        // Scramble the letters using the standard Fisher-Yates shuffle,
        for (int i = 0; i < a.length; i++) {
            int j = random.nextInt(a.length);
            // Swap letters
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }

        return new String(a);
    }

    public String addExtraConsonentInString() {
        Random random = new Random();
        String alphabetStribg = "bcdfghzklmnpqrstvwxyz";
        String extraChar = alphabetStribg.charAt(random.nextInt(alphabetStribg.length())) + "";
        return extraChar;
    }

    public String addExtraVowelInString() {
        Random random = new Random();
        String alphabetStribg = "aeiou";
        String extraChar = alphabetStribg.charAt(random.nextInt(alphabetStribg.length())) + "";
        return extraChar;
    }

    public void vibrateandShake() {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            wheel_tray_layout.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            if (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
        }
        Spanned s1 = Html.fromHtml(s2, null, new OustTagHandler());
        return s1;
    }

    private void playAudio(final String filename) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                    mediaPlayer.reset();
                    FileInputStream fis = new FileInputStream(tempMp3);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });
    }

    private long answeredSeconds;

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

         @Override
        public void onFinish() {
            try {
                if (jumble_questionlayout.getVisibility() == View.VISIBLE) {
                    questionDisAppearAnim();
                }
                if (linePath != null && drawPath != null) {
                    linePath.reset();
                    jumble_line.setLinePath(linePath);
                    linePath = null;
                    drawPath.reset();
                    jumble_line.setDrawpath(drawPath);
                    drawPath = null;
                    StartOrgPT = null;
                }

                if (isWordJumble2) {
                    myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < jumbleWordModel.size(); i++) {
                                if (jumbleWordModel.get(i).getBackground() != null) {
                                    jumbleWordModel.get(i).getBackground().setOnClickListener(null);
                                }
                            }
                            jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                            showSubmitLayout(true);
                        }
                    }, 500);
                } else {
                    try {
                        for (int i = 0; i < jumbleWordModel.size(); i++) {
                            for (int j = 0; j < jumbleWordModel.get(i).getJumbleBoxes().size(); j++) {
                                View jubleView = jumbleWordModel.get(i).getJumbleBoxes().get(j);
                                TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                                jumbleText.setVisibility(View.VISIBLE);
                                jumbleText.setText("" + jumbleWordModel.get(i).getString1().charAt(j));
                                OustSdkTools.setBackground(jumbleText, OustSdkApplication.getContext().getResources().getString(R.string.letter_bg));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    calculateOc(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            /*String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));*/

            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            timerText.setText(hms);
        }
    }

    private long jumbleStartTime = 0;

    public void startTimer() {
        try {
            jumbleStartTime = System.currentTimeMillis();
            timer = new CounterClass(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
        jumble_wheel_addtext_layout.getParent().requestDisallowInterceptTouchEvent(false);
    }

    //===============================================================================
    private void startGameAnimation() {
        try {
            topbar_question.setVisibility(View.VISIBLE);
            if (isWordJumble2) {
                resetLayoutAppearAnim();
            } else {
                hintlayoutAppearAnim();
                refreshlayoutAppearAnim();
            }
            bottomWheelAppearAnnimation();
            topBoardAppearAnim();
            questionlable.setText(OustStrings.getString("solve"));
            mainquestion_text.setVisibility(View.GONE);
            jumble_wheel_layout.setVisibility(View.VISIBLE);
            if (isWordJumble2) {
                jumblequestion_submitlayout.setVisibility(View.VISIBLE);
            }
            jumblequestion_solvelayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hintlayoutAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackin);
        hint_layout.startAnimation(event_animmovein);
        hint_layout.setVisibility(View.VISIBLE);
    }

    private void resetLayoutAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackin);
        w2_reset_layout.startAnimation(event_animmovein);
        w2_reset_layout.setVisibility(View.VISIBLE);
    }

    private void refreshlayoutAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animmovein);
        refresh_layout.startAnimation(event_animmovein);
        refresh_layout.setVisibility(View.VISIBLE);
    }

    public void bottomWheelAppearAnnimation() {
        final android.view.animation.Animation wheelappear = AnimationUtils.loadAnimation(getActivity(), R.anim.wheelappear_anim);
        wheel_tray_layout.startAnimation(wheelappear);
        wheel_tray_layout.setVisibility(View.VISIBLE);
        wheelappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startJumbelLayout();
                startTimer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void topBordInitialSetUp() {
        Log.d(TAG, "topBordInitialSetUp: ");
        ObjectAnimator anim = ObjectAnimator.ofFloat(solution_board_layout, "translationY", 0, 300);
        anim.setDuration(0);
        anim.start();

    }

    private void topBoardAppearAnim() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(solution_board_layout, "translationY", 300, 0);
        fadeOut.setDuration(400);
        fadeOut.start();
    }

    //==========================================================================
    private void startOverAnimation(boolean status) {
        topbar_question.setVisibility(View.GONE);
        jumble_wheel_layout.setVisibility(View.GONE);
        if (isWordJumble2) {
            resetDisappearAnimation();
        } else {
            hintlayoutDisAppearAnim();
            refreshlayoutDisAppearAnim();
        }
        topBoardEndAppearAnim(status);
        bottomWheelDisAppearAnnimation();
    }

    private void resetDisappearAnimation() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackout);
        w2_reset_layout.startAnimation(event_animmovein);
        w2_reset_layout.setVisibility(View.VISIBLE);
    }

    private void hintlayoutDisAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackout);
        hint_layout.startAnimation(event_animmovein);
        hint_layout.setVisibility(View.VISIBLE);
    }

    private void refreshlayoutDisAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animmoveout);
        refresh_layout.startAnimation(event_animmovein);
        refresh_layout.setVisibility(View.VISIBLE);
    }

    public void bottomWheelDisAppearAnnimation() {
        final android.view.animation.Animation wheelappear = AnimationUtils.loadAnimation(getActivity(), R.anim.carddisappear_anim);
        wheelappear.setDuration(600);
        wheel_tray_layout.startAnimation(wheelappear);
        wheel_tray_layout.setVisibility(View.VISIBLE);
    }

    private void topBoardEndAppearAnim(final boolean status) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofFloat(solution_board_layout, "translationY", 0, 300);
            anim.setDuration(400);
            anim.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        wheel_tray_layout.setVisibility(View.GONE);
                        jumble_wheel_addtext_layout.setVisibility(View.GONE);

                        if (isWordJumble2) {
//                    showSubmitLayout(true);
//                        learningModuleInterface.gotoNextScreen();
                        } else {
                            rightwrongFlipAnimation(status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showRightAnimation(View view, int position) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f);
        ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        scaleDownX.setDuration(250);
        scaleDownY.setDuration(250);
        scaleDownZ.setDuration(100);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
        scaleDown.setStartDelay((position * 50));
        scaleDown.start();
    }

    private void setAnimation(View view, int position) {
        try {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.4f, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.4f, 1.0f);
            ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
            scaleDownX.setDuration(250);
            scaleDownY.setDuration(250);
            scaleDownZ.setDuration(250);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
            scaleDown.setStartDelay((position * 70));
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setLayoutBackgroud(View layout, int id) {
        try {
            Drawable d;
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.LOLLIPOP) {
                d = getActivity().getDrawable(id);
            } else {
                d = getActivity().getResources().getDrawable(id);
            }
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(d);
            } else {
                layout.setBackground(d);
            }
        } catch (Exception e) {
        }
    }

    //==================================================================================================
    private float totalResources = 0;
    private float resourcesDownloaded = 0;

    private void downloadIconsFromAws() {

       /* String[] resList = null;
        String imagePath = "";
        imagePath = "AppResources/Android/All/Images/";
        resList = getActivity().getResources().getStringArray(R.array.wordjumble_icons);
        totalResources += resList.length;
        if (resList != null) {
            for (int j = 0; j < resList.length; j++) {
                try {
                    final String filename = resList[j];
                    final File file = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                    if (file != null && !file.exists()) {
                        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
                        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
                        TransferUtility transferUtility = new TransferUtility(s3, getActivity());
                        String resPath = imagePath + filename;
                        TransferObserver transferObserver = transferUtility.download("img.oustme.com", resPath, file);
                        transferObserver.setTransferListener(new TransferListener() {
                            @Override
                            public void onStateChanged(int id, TransferState state) {
                                if (state == TransferState.COMPLETED) {
                                    showDownloadProgress();
                                    file.delete();
                                } else if (state == TransferState.FAILED) {
                                    if (resourcesDownloaded >= totalResources) {
                                        getActivity().finish();
                                        OustSdkTools.showToast("resource download failed");
                                    } else {
                                        showDownloadProgress();
                                    }
                                }
                            }

                            @Override
                            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            }

                            @Override
                            public void onError(int id, Exception ex) {
                                if (resourcesDownloaded >= totalResources) {
                                    OustSdkTools.showToast("resource download failed");
                                    getActivity().finish();
                                } else {
                                    showDownloadProgress();
                                }
                            }
                        });
                    } else {
                        showDownloadProgress();
                    }
                } catch (Exception e) {
                    OustSdkTools.showToast("resource download failed");
                    getActivity().finish();
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }*/
    }

    public void showDownloadProgress() {
        resourcesDownloaded++;
        if (totalResources > 0) {
            float progress = ((resourcesDownloaded / totalResources));
            loader.setProgress(((int) (progress * 100)));
            downloadprogresstext.setText("" + (int) (progress * 100) + "%");
            if (resourcesDownloaded >= (totalResources)) {
                loader.setProgress(100);
                downloadprogresstext.setText("100%");
                OustPreferences.saveAppInstallVariable("isWordJumbleResourcesDownloaded", true);
                //initViews(view);
                hideLoader();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (linePath != null && drawPath != null) {
            linePath.reset();
            linePath = new Path();
            jumble_line.setLinePath(linePath);
            linePath = null;
            drawPath.reset();
            drawPath = new Path();
            jumble_line.setDrawpath(drawPath);
            drawPath = null;
            StartOrgPT = null;
            addedPoints = new int[20];
            noofPointsAdded = 0;
            currentCharNo = -1;
        }
        previewText = "";
        preview_textview.setText(previewText);
        if (keeptrackOfLineModelsList != null) {
            for (int i = 0; i < keeptrackOfLineModelsList.size(); i++) {
                keeptrackOfLineModelsList.get(i).setAttemptStatus(0);
            }
        }
    }

}
