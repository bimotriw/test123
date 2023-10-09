package com.oustme.oustsdk.fragments.courses;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.course.JumbleWordKeeptrackOfLineModel;
import com.oustme.oustsdk.response.course.JumbleWordUtilityModel;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by shilpysamaddar on 27/02/18.
 */

public class JumbleWordFragment2 extends Fragment implements View.OnClickListener {

    private RelativeLayout jumbleword_mainlayout, jumble_answer_layout,topbar_timerlayout,topbar_question_layout,
            jumble_wheel_addtext_layout, wheel_tray_layout, solution_board_layout, jumble_questionlayout,
            jumblequestion_solvelayout, popupjumblequestion_solvelayout, question_board_layout, jumble_wheel_layout;
    private RelativeLayout topbar_question, jumblequestion_submitlayout, confirm_popup_cancel, confirm_popup_submit, topbar_info_layout, wordjumble_exit_layout,w2_reset_layout;
    private ScrollView instruction_scrollview;

    private ImageView  topbar_question_image, reset_imageview, topbar_exit_imageView,charcount_imageview,question_board_imageview,
            topbar_info_image,splash_layout,bottom_imageview,solution_board_imageview,submit_confirm_imageview,time_imageview,
            jumblequestion_submitlayout_bgd,jumblequestion_solvelayout_bgd,topbar_timerlayout_bgd,popupjumblequestion_solvelayout_bgd;
    private TextView charcount_textview,timerText,
            userresponse_text, popup_heading, solve_lable, popup_solve_lable;
    private LinearLayout confirm_submitlayout_popup,jumble_mainquestionlayout;

    private TextView questionlable, confirmpopup_cancel_layout, confirmpopup_submit_layout;
    private ImageView quiz_backgroundimagea,undobutton,confirmpopup_cancel_layout_bgd,
            confirmpopup_submit_layout_bgd;
    private HtmlTextView mainquestion_text, popupmainquestion_text,instruction_text,submit_popup_question;

//    private TextView questionlableMaths;

    private int countLine = 1;
    private LayoutInflater mInflater;
    private Handler myHandler;
    private boolean wordsended = false;
    private boolean submitClicked = false;
    private boolean submitPopupShowing = false;
    private List<JumbleWordUtilityModel> jumbleWordModel = new ArrayList<>();
    private List<JumbleWordKeeptrackOfLineModel> keepTrackOfBoxesList=new ArrayList<>();

    private int currentIndex = 0;
    private int finalScore = 0;
    private int wrongAttempt = 0;
    private String currentStringOnWheel = "";

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

    //    private View view;
    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jumbleword2, container, false);
        setInitialScreen(view);
        initViews(view);
        return view;
    }

    private void setInitialScreen(View view) {
        splash_layout = view.findViewById(R.id.splash_layout);
        OustSdkTools.setImage(splash_layout, getResources().getString(R.string.splash));
        splash_layout.setVisibility(View.VISIBLE);
    }

    private void initViews(View view) {
        jumbleword_mainlayout = view.findViewById(R.id.jumbleword_mainlayout);
        jumble_mainquestionlayout = view.findViewById(R.id.jumble_mainquestionlayout);
        topbar_question = view.findViewById(R.id.topbar_question);
        instruction_text = view.findViewById(R.id.instruction_text);
        instruction_text.setTypeface(OustSdkTools.getTypefaceLithoPro());
        topbar_timerlayout = view.findViewById(R.id.topbar_timerlayout);
        topbar_timerlayout_bgd= view.findViewById(R.id.topbar_timerlayout_bgd);
        instruction_scrollview = view.findViewById(R.id.instruction_scrollview);
        wheel_tray_layout = view.findViewById(R.id.wheel_tray_layout);
        jumble_questionlayout = view.findViewById(R.id.jumble_questionlayout);
        question_board_layout = view.findViewById(R.id.question_board_layout);
        question_board_imageview = view.findViewById(R.id.question_board_imageview);
        jumble_answer_layout = view.findViewById(R.id.jumble_answer_layout);
        jumble_wheel_addtext_layout = view.findViewById(R.id.jumble_wheel_addtext_layout);
        solution_board_layout = view.findViewById(R.id.solution_board_layout);
        solution_board_imageview = view.findViewById(R.id.solution_board_imageview);
        jumblequestion_solvelayout = view.findViewById(R.id.jumblequestion_solvelayout);
        jumblequestion_solvelayout_bgd= view.findViewById(R.id.jumblequestion_solvelayout_bgd);
        popupjumblequestion_solvelayout = view.findViewById(R.id.popupjumblequestion_solvelayout);
        popupjumblequestion_solvelayout_bgd= view.findViewById(R.id.popupjumblequestion_solvelayout_bgd);
        jumble_wheel_layout = view.findViewById(R.id.jumble_wheel_layout);
        mainquestion_text = view.findViewById(R.id.mainquestion_text);
        mainquestion_text.setTypeface(OustSdkTools.getTypefaceLithoPro());
        topbar_question_image = view.findViewById(R.id.topbar_question_image);
        questionlable = view.findViewById(R.id.questionlable);
//        questionlableMaths = view.findViewById(R.id.questionlableMaths);
        popupmainquestion_text = view.findViewById(R.id.popupmainquestion_text);
        popupmainquestion_text.setTypeface(OustSdkTools.getTypefaceLithoPro());
        quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
        time_imageview = view.findViewById(R.id.time_imageview);
        solve_lable = view.findViewById(R.id.solve_lable);
        popup_solve_lable = view.findViewById(R.id.popup_solve_lable);
        w2_reset_layout = view.findViewById(R.id.w2_reset_layout);
        reset_imageview = view.findViewById(R.id.reset_imageview);
        charcount_textview = view.findViewById(R.id.charcount_textview);
        charcount_imageview = view.findViewById(R.id.charcount_imageview);
        topbar_info_image = view.findViewById(R.id.topbar_info_image);
        bottom_imageview = view.findViewById(R.id.bottom_imageview);
        submit_confirm_imageview = view.findViewById(R.id.submit_confirm_imageview);
        topbar_question_layout = view.findViewById(R.id.topbar_question_layout);
        undobutton= view.findViewById(R.id.undobutton);
        undobutton.setOnClickListener(this);
        timerText = view.findViewById(R.id.timerText);
        jumbleword_mainlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jumbleword_mainlayout.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });

        questionlable.setText(OustStrings.getString("clueTitleText"));
        solve_lable.setText(OustStrings.getString("solve"));
        popup_solve_lable.setText(OustStrings.getString("solve"));
        wordjumble_exit_layout = view.findViewById(R.id.wordjumble_exit_layout);
        topbar_info_layout = view.findViewById(R.id.topbar_info_layout);
        popup_heading = view.findViewById(R.id.popup_heading);
        jumblequestion_submitlayout = view.findViewById(R.id.jumblequestion_submitlayout);
        jumblequestion_submitlayout_bgd= view.findViewById(R.id.jumblequestion_submitlayout_bgd);
        submit_popup_question = view.findViewById(R.id.submit_popup_question);
        submit_popup_question.setTypeface(OustSdkTools.getTypefaceLithoPro());
        confirmpopup_cancel_layout = view.findViewById(R.id.confirmpopup_cancel_layout);
        confirmpopup_cancel_layout_bgd= view.findViewById(R.id.confirmpopup_cancel_layout_bgd);
        confirmpopup_submit_layout = view.findViewById(R.id.confirmpopup_submit_layout);
        confirmpopup_submit_layout_bgd= view.findViewById(R.id.confirmpopup_submit_layout_bgd);
        confirm_submitlayout_popup = view.findViewById(R.id.confirm_submitlayout_popup);
        confirm_popup_cancel = view.findViewById(R.id.confirm_popup_cancel);
        confirm_popup_submit = view.findViewById(R.id.confirm_popup_submit);
        userresponse_text = view.findViewById(R.id.userresponse_text);
        topbar_exit_imageView = view.findViewById(R.id.topbar_exit_imageView);
        OustSdkTools.setImage(jumblequestion_submitlayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(confirmpopup_cancel_layout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(confirmpopup_submit_layout_bgd, getResources().getString(R.string.bg_word));
        jumblequestion_submitlayout.setOnClickListener(this);
        confirm_popup_cancel.setOnClickListener(this);
        confirm_popup_submit.setOnClickListener(this);
        topbar_info_layout.setOnClickListener(this);
        wordjumble_exit_layout.setOnClickListener(this);
        w2_reset_layout.setOnClickListener(this);
        confirm_submitlayout_popup.setOnClickListener(this);

        OustSdkTools.setImage(jumblequestion_solvelayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(topbar_timerlayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(solution_board_imageview, getResources().getString(R.string.board_wj));
        OustSdkTools.setImage(question_board_imageview, getResources().getString(R.string.board_wj));
        OustSdkTools.setImage(popupjumblequestion_solvelayout_bgd, getResources().getString(R.string.bg_word));
        OustSdkTools.setImage(topbar_question_image, getResources().getString(R.string.question));
        OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.background_word));//
        OustSdkTools.setImage(bottom_imageview,getResources().getString(R.string.bottom_wj));
        OustSdkTools.setImageA(time_imageview, getResources().getString(R.string.time));


        jumble_mainquestionlayout.setVisibility(View.VISIBLE);
        setQuestionText();
        startAnimations();
        topBordInitialSetUp();
        getWidth();
        setWordJumblev2Layout();

        jumblequestion_solvelayout.setOnClickListener(this);
        jumble_questionlayout.setOnClickListener(this);
        topbar_question_image.setOnClickListener(this);
        jumblequestion_submitlayout.setOnClickListener(this);
    }

    private void setWordJumblev2Layout() {
        try {
            wordjumble_exit_layout.setVisibility(View.VISIBLE);
            topbar_exit_imageView.setVisibility(View.VISIBLE);
            OustSdkTools.setImage(topbar_exit_imageView, getResources().getString(R.string.exit_icon));
            OustSdkTools.setImage(topbar_info_image, getResources().getString(R.string.instruction));
            OustSdkTools.setImage(reset_imageview, getResources().getString(R.string.shuffle));
            OustSdkTools.setImage(charcount_imageview,getResources().getString(R.string.charcount));
            OustSdkTools.setImage(submit_confirm_imageview, getResources().getString(R.string.instruction_bg));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionText() {
        if (questions != null) {
            mainquestion_text.setHtml(questions.getQuestion());
            popupmainquestion_text.setHtml(questions.getQuestion());
        }
    }

    private int scrWidth;
    private int scrHeight;

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    private void startAnimations() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(splash_layout, "alpha", 1f, 0f);
        fadeOut.setDuration(1200);
        fadeOut.setStartDelay(1000);
        fadeOut.start();
    }

    private void topBordInitialSetUp() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(jumble_mainquestionlayout, "translationY", 0, 300);
        anim.setDuration(0);
        anim.start();

    }

//    ==================================================================================================
    private long answeredSeconds=0;
    private CounterClass timer;
    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {}

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            answeredSeconds++;
            String hms = String.format("%02d:%02d",answeredSeconds/60,answeredSeconds%60);
            timerText.setText(hms);
        }
    }

    private long jumbleStartTime=0;
    public void startTimer() {
        try {
            jumbleStartTime=System.currentTimeMillis();
            timer = new CounterClass(86000000,1000);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startJumbelLayout() {
        try {
            startTimer();
            if (!instruction.isEmpty()) {
                topbar_info_layout.setVisibility(View.VISIBLE);
            }
            topbar_question_layout.setVisibility(View.VISIBLE);
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
                        jumbleBoxes.add(jumbleView);
                        jumble_answer_layout.addView(jumbleView);

                        showRightAnimation(jumbleView, j);

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

                ViewGroup.LayoutParams params = background.getLayoutParams();
                params.width = ((boxSize * option.length()));
                background.setLayoutParams(params);
                background.setOnClickListener(this);
                jumble_answer_layout.addView(background);

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
            appendExtraCharThenDraw(false);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void appendExtraCharThenDraw(boolean setCharCountToZero) {
        try {
            boolean wordsleft = false;
            if (!jumbleWordModel.get(currentIndex).isAttempted()) {
                wordsleft = true;
                proceedToNextStep(setCharCountToZero);
            } else {
                for (int i = 0; i < jumbleWordModel.size(); i++) {
                    if (currentIndex < jumbleWordModel.size()) {
                        if (!jumbleWordModel.get(i).isAttempted()) {
                            currentIndex = i;
                            wordsleft = true;
                            proceedToNextStep(setCharCountToZero);
                            break;
                        }
                    }
                }
            }
            if (!wordsleft) {
                myHandler = new Handler();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!wordsended) {
                            wordsended = true;
                            showSubmitLayout(false);
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void proceedToNextStep(boolean setCharCountToZero) {
        try {
            currentStringOnWheel = jumbleWordModel.get(currentIndex).getString1();
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
            if(setCharCountToZero){
                charcount_textview.setText("0");
            }else{
                charcount_textview.setText(""+(jumbleWordModel.get(currentIndex).getString1().length()-indexOfSolutionWord));
            }
            OustSdkTools.setLayoutBackgroud((jumbleWordModel.get(currentIndex).getBackground()), R.drawable.jumble_confirm_roundedcorner);
            Random random = new Random();
            currentStringOnWheel = scramble(random, currentStringOnWheel);
            drawBottomBoxes(currentStringOnWheel);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

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

    private void drawBottomBoxes(String currentStringOnWheel) {
        try {
             jumble_wheel_addtext_layout.removeAllViews();
                keepTrackOfBoxesList = new ArrayList<>();
                int boxSize = scrWidth / 6;
                int x = 0;
                int xStart = boxSize / 2;
                int yStart = boxSize - 10;
                for (int j = 0; j < currentStringOnWheel.length(); j++) {
                    JumbleWordKeeptrackOfLineModel boxModel = new JumbleWordKeeptrackOfLineModel();
                    View jumbleView = mInflater.inflate(R.layout.jumble_answer_textviewv2, null);
                    RelativeLayout charBack = jumbleView.findViewById(R.id.charBack);
                    TextView jumbletext = jumbleView.findViewById(R.id.jumbletext);
                    OustSdkTools.setLayoutBackgroud(jumbletext, R.drawable.letter_bg);
                    jumbletext.setText("" + currentStringOnWheel.charAt(j));
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) charBack.getLayoutParams();
                    params.width = boxSize;
                    params.height = boxSize;
                    jumbleView.setLayoutParams(params);
                    jumbleView.setX(xStart);
                    jumbleView.setY(yStart);
                    jumble_wheel_layout.setVisibility(View.VISIBLE);
                    jumble_wheel_addtext_layout.addView(jumbleView);
                    jumbleView.setId(10 + j);
                    jumbleView.setOnClickListener(this);

                    showRightAnimation(jumbleView, j);
                    boxModel.setIndex(j);
                    boxModel.setAttemptStatus(0);
                    boxModel.setView(jumbleView);
                    boxModel.setCurrentChar((""+currentStringOnWheel.charAt(j)));
                    keepTrackOfBoxesList.add(boxModel);

                    x++;
                    xStart += boxSize;
                    if (x > 4) {
                        x = 0;
                        yStart += (boxSize + 4);
                        xStart = boxSize / 2;
                    }
                }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    private boolean undoButtonEnabled=false;
    private void diableUndoButton(){
        undoButtonEnabled=false;
        undobutton.setAlpha(0.4f);
    }
    private void enableUndoButton(){
        undoButtonEnabled=true;
        undobutton.setAlpha(1.0f);
    }
    private void undoButtonPressed(){
        try {
            if ((!wordsended)&&(undoButtonEnabled)) {
                if (keepTrackOfBoxesList != null && keepTrackOfBoxesList.size() > 0) {
                    for (int j = 0; j < keepTrackOfBoxesList.size(); j++) {
                        if (keepTrackOfBoxesList.get(j).getAttemptStatus() == 1) {
                            if (usransr.length() > 0) {
                                String lastChar = usransr.substring(usransr.length() - 1);
                                String usransrSubStr = usransr.substring(0, usransr.length() - 1);
                                if (keepTrackOfBoxesList.get(j).getCurrentChar().equalsIgnoreCase(lastChar)) {
                                    View jubleView = jumbleWordModel.get(currentIndex).getJumbleBoxes().get(usransr.length() - 1);
                                    TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                                    keepTrackOfBoxesList.get(j).setAttemptStatus(0);
                                    jumbleText.setVisibility(View.VISIBLE);
                                    OustSdkTools.setLayoutBackgroud(jumbleText, R.drawable.empty_text);
                                    jumbleText.setText("");
                                    usransr = usransrSubStr;

                                    View jumbleView = keepTrackOfBoxesList.get(j).getView();
                                    TextView jumbletextofbottombox = jumbleView.findViewById(R.id.jumbletext);
                                    jumbletextofbottombox.setText(lastChar);
                                    OustSdkTools.setLayoutBackgroud(jumbletextofbottombox, R.drawable.letter_bg);
                                    indexOfSolutionWord--;
                                    charcount_textview.setText("" + (jumbleWordModel.get(currentIndex).getString1().length() - indexOfSolutionWord));
                                    showAlphaAnimation(jumbleView, 0);
                                    if(indexOfSolutionWord==0){
                                        diableUndoButton();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){}
    }

    private int indexOfSolutionWord=0;
    private String usransr="";
    private void showTextOnSolutionBox(int indexOfWord){
        try {
            if (!wordsended) {
                if (keepTrackOfBoxesList != null && keepTrackOfBoxesList.size() > 0 && jumbleWordModel.get(currentIndex).isAttempted()) {
                    clearAttemptedWord();
                }
                if (keepTrackOfBoxesList != null && keepTrackOfBoxesList.size() > 0) {
                    if (keepTrackOfBoxesList.get(indexOfWord).getAttemptStatus() == 0) {
                        enableUndoButton();
                        keepTrackOfBoxesList.get(indexOfWord).setAttemptStatus(1);
                        View jumbleView = keepTrackOfBoxesList.get(indexOfWord).getView();
                        RelativeLayout charBack = jumbleView.findViewById(R.id.charBack);
                        TextView jumbletextofbottombox = jumbleView.findViewById(R.id.jumbletext);
                        OustSdkTools.setLayoutBackgroud(jumbletextofbottombox, R.drawable.empty_text);

                        View jubleView = jumbleWordModel.get(currentIndex).getJumbleBoxes().get(indexOfSolutionWord);
                        TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                        jumbleText.setVisibility(View.VISIBLE);
                        jumbleText.setText(jumbletextofbottombox.getText().toString());
                        usransr += jumbletextofbottombox.getText().toString();
                        jumbletextofbottombox.setText("");
                        showRightAnimation(jubleView, 0);
                        OustSdkTools.setLayoutBackgroud(jumbleText, R.drawable.letter_bg);
                        indexOfSolutionWord++;
                        charcount_textview.setText("" + (jumbleWordModel.get(currentIndex).getString1().length() - indexOfSolutionWord));
                        if (indexOfSolutionWord == jumbleWordModel.get(currentIndex).getString1().length()) {
                            jumbleWordModel.get(currentIndex).setUserAnswer(usransr);
                            indexOfSolutionWord = 0;
                            diableUndoButton();
                            usransr = "";
                            jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                            jumbleWordModel.get(currentIndex).setAttempted(true);
                            keepTrackOfBoxesList = new ArrayList<>();
                            appendExtraCharThenDraw(false);
                        } else {
                            showAlphaAnimation(jumbleView, 0);
                        }
                    }
                }
            }
        }catch (Exception e){}
    }

    private void clearAttemptedWord() {
        try {
            jumbleWordModel.get(currentIndex).setAttempted(false);
            jumbleWordModel.get(currentIndex).setUserAnswer("");
            indexOfSolutionWord = 0;
            diableUndoButton();
            for (int j = 0; j < jumbleWordModel.get(currentIndex).getJumbleBoxes().size(); j++) {
                View jubleView = jumbleWordModel.get(currentIndex).getJumbleBoxes().get(j);
                TextView jumbleText = jubleView.findViewById(R.id.jumbletext);
                jumbleText.setVisibility(View.VISIBLE);
                jumbleText.setText("");
                OustSdkTools.setLayoutBackgroud(jumbleText, R.drawable.empty_text);
                //showRightAnimation(jubleView, j);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //    =========================================================================================

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.jumblequestion_solvelayout) {
            startGameAnimation();
        } else if (v.getId() == R.id.jumble_questionlayout) {
            questionDisAppearAnim();
        }

        if (v.getId() == R.id.topbar_question_image) {
            if (!submitPopupShowing) {
                playJumblePopupSound();
                setQuestionPopup();
                questionAppearAnim();
            }
        }

        if (v.getId() == R.id.topbar_info_layout) {
            if (!submitPopupShowing) {
                playJumblePopupSound();
                setInfoPopup();
                questionAppearAnim();
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
        if (v.getId() == R.id.w2_reset_layout) {
            if(!wordsended) {
                //playShuffleMusic();
                indexOfSolutionWord = 0;
                diableUndoButton();
                usransr = "";
                keepTrackOfBoxesList=new ArrayList<>();
                playJumblePopupSound();
                clearAttemptedWord();
                appendExtraCharThenDraw(false);
            }
        }
        if (v.getId() == R.id.confirm_popup_submit) {
            if (!submitClicked) {
                submitClicked = true;
                calculatePointForW2();
            }
        }

        if (v.getId() == R.id.wordjumble_exit_layout) {
            learningModuleInterface.endActivity();
        }

        if((v.getId())==10){
            showTextOnSolutionBox(0);
        }
        if((v.getId())==11){
            showTextOnSolutionBox(1);
        }
        if((v.getId())==12){
            showTextOnSolutionBox(2);
        }
        if((v.getId())==13){
            showTextOnSolutionBox(3);
        }
        if((v.getId())==14){
            showTextOnSolutionBox(4);
        }if((v.getId())==15){
            showTextOnSolutionBox(5);
        }
        if((v.getId())==16){
            showTextOnSolutionBox(6);
        }if((v.getId())==17){
            showTextOnSolutionBox(7);
        }
        if((v.getId())==18){
            showTextOnSolutionBox(8);
        }
        if((v.getId())==19){
            showTextOnSolutionBox(9);
        }
        if((v.getId())==20){
            showTextOnSolutionBox(10);
        }
        if(v.getId()==R.id.undobutton){
            undoButtonPressed();
        }
        //undoButtonPressed
        for(int i=0;i<12;i++){
            int tagId=100+i;
            if((v.getId()==tagId)){
                selectedWordChanged(i);
                diableUndoButton();
            }
        }
    }

    private void selectedWordChanged(int index) {
        try {
            if (!submitPopupShowing) {
                if((currentIndex != index) && (!wordsended) && (!jumbleWordModel.get(currentIndex).isAttempted()) && (indexOfSolutionWord>0 || usransr.length()>0)){
                    clearAttemptedWord();
                }
                usransr = "";
                if (jumbleWordModel.get(index).isAttempted() && (currentIndex != index || wordsended)) {
                    wordsended = false;
                    jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                    currentIndex = index;
                    proceedToNextStep(true);
                } else if(currentIndex != index){
                    jumbleWordModel.get(currentIndex).getBackground().setBackgroundDrawable(null);
                    currentIndex = index;
                    appendExtraCharThenDraw(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setInfoPopup() {
        popupmainquestion_text.setVisibility(View.GONE);
        popup_heading.setText("Instructions");
        instruction_scrollview.setVisibility(View.VISIBLE);
        instruction_text.setHtml(instruction);
    }

    private void setQuestionPopup() {
        instruction_scrollview.setVisibility(View.GONE);
        popup_heading.setText("CLUE");
        popupmainquestion_text.setVisibility(View.VISIBLE);
        popupmainquestion_text.setHtml(questions.getQuestion());
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

    //    =============================================================================================
    private String responsetext = "";
    private void showSubmitLayout(boolean jumbleComplete) {
        submitPopupShowing = true;
        learningModuleInterface.disableBackButton(true);

        SubmitAppearAnim();
        submit_popup_question.setHtml(questions.getQuestion());
        if (jumbleComplete) {
            confirm_popup_cancel.setVisibility(View.GONE);
        }
        responsetext = "";
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
            long totalTimeTaken=answeredSeconds*1000;
            long currentTime=System.currentTimeMillis();
            if((currentTime==0)||(totalTimeTaken==0)){}else{
                totalTimeTaken=currentTime-jumbleStartTime;
                if(totalTimeTaken<10){
                    totalTimeTaken=answeredSeconds*1000;
                }
            }
            learningModuleInterface.setAnswerAndOc(responsetext, "", finalScore, true,totalTimeTaken);
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

    //    =========================================================================================
    private void startGameAnimation() {
        topbar_question.setVisibility(View.VISIBLE);
        resetLayoutAppearAnim();
        bottomWheelAppearAnnimation();
        topBoardAppearAnim();
        questionlable.setText(OustStrings.getString("solve"));
        mainquestion_text.setVisibility(View.GONE);
        jumble_wheel_layout.setVisibility(View.VISIBLE);
        jumblequestion_submitlayout.setVisibility(View.VISIBLE);
        jumblequestion_solvelayout.setVisibility(View.GONE);
        undobutton.setVisibility(View.VISIBLE);
    }

    private void resetLayoutAppearAnim() {
        Animation event_animmovein = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.event_animbackin);
        w2_reset_layout.startAnimation(event_animmovein);
        w2_reset_layout.setVisibility(View.VISIBLE);
    }

    private void topBoardAppearAnim() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(jumble_mainquestionlayout, "translationY", 300, 0);
        fadeOut.setDuration(400);
        fadeOut.start();
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                jumble_questionlayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
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
    private void showAlphaAnimation(View view, int position) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.0f);
        ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        scaleDownZ.setDuration(400);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
        scaleDown.setStartDelay((position * 50));
        scaleDown.start();
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

    private void playJumblePopupSound() {
        stopMediaPlayer();
        playAudio("jumble_popup.wav");
    }

    private void playShuffleMusic() {
        stopMediaPlayer();
        playAudio("jumble_shuffle.wav");
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


}
