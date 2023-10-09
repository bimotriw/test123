package com.oustme.oustsdk.fragments.courses.learningplaynew;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownloadImage.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownloadImage.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownloadImage.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FIVE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.FOUR_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.ONE_SECOND;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.SIX_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.THREE_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.IntegerConstants.TWO_HUNDRED_MILLI_SECONDS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.adapter.courses.ImageTextAdapter;
import com.oustme.oustsdk.categorySwipe.SwipeFlingAdapterView;
import com.oustme.oustsdk.customviews.CustomScrollView;
import com.oustme.oustsdk.customviews.CustomTouchIndicatorClass;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.HotspotPointData;
import com.oustme.oustsdk.interfaces.common.NewsFeedProgressListener;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReadMoreFavouriteCallBack;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.OustFillData;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseSolutionCard;
import com.oustme.oustsdk.room.dto.DTOHotspotPointData;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.service.DownloadImage;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.oustme.oustsdk.util.StopWatch;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by shilpysamaddar on 11/03/17.
 */

public class LearningPlayFragmentNew extends Fragment implements View.OnTouchListener, View.OnClickListener, ReadMoreFavouriteCallBack, DialogKeyListener {

    private static final String TAG = "LearningPlayFragmentNew";

    private View learningquiz_animviewb, learningquiz_animviewa;
    private RelativeLayout learningquiz_mainlayout, match_option_a_left_layout, match_option_b_left_layout,
            match_option_c_left_layout, match_option_d_left_layout, match_option_a_right_layout,
            match_option_b_right_layout, match_option_d_right_layout, match_option_c_right_layout,
            fill_submit_layout, correct_option_layout, animoc_layout, learning_new_ll,
            gotonextscreen_btn, matchfollowing_layout, solution_readmore;
    private ImageView learningquiz_mainquestionImage, questionmore_btn, question_arrowback,
            question_arrowfoword, showsolution_img, match_option_a_left_image,
            match_option_b_left_image, match_option_c_left_image, match_option_d_left_image,
            match_option_a_right_image, match_option_b_right_image, match_option_c_right_image,
            match_option_d_right_image, questionaudio_btn, unfavourite, lpocimage;
    private TextView learningquiz_mainquestionTime, learningquiz_mainquestion, learningquiz_mainquestionText,
            learningcard_coursename, cardprogress_text, learningquiz_timertext, ocanim_text,
            match_option_a_left_text, match_option_b_left_text, match_option_c_left_text,
            match_option_d_left_text, match_option_a_right_text, match_option_b_right_text,
            match_option_c_right_text, match_option_d_right_text, draglebel_text, solution_label;

    private KatexView learningquiz_mainquestion_maths, learningquiz_mainquestionText_maths, match_option_a_left_text_maths,
            match_option_b_left_text_maths, match_option_c_left_text_maths,
            match_option_d_left_text_maths, match_option_a_right_text_maths, match_option_b_right_text_maths,
            match_option_c_right_text_maths, match_option_d_right_text_maths, draglebel_text_maths, solution_label_maths;

    private ProgressBar learningcard_progress;
    private LinearLayout learningquiz_mainoptionlayout, bottomswipe_view, gotopreviousscreen_mainbtn,
            gotonextscreen_mainbtn, correct_option_main_layout;
    private RelativeLayout fill_blanks_layout, tutorial_scr;
    private GifImageView learning_tutorial_imageView;
    private ImageView quiz_backgroundimagea, learningquiz_rightwrongimage, solution_closebtn, quiz_backgroundimagea_downloaded;

    private HtmlTextView solution_desc;
    private ImageView mImageViewSolution;
    private TextView myresponse_desc, myresponse_label, solution_readmore_text, fill_blanks_title, wrong_right_text;

    private LinearLayout ocanim_view, mainoption_scrollview;

    private RelativeLayout category_layout;
    private SwipeFlingAdapterView flingContainer;

    private TextView category_righttext_layout, category_lefttext_layout, category_optionsleft, fill_bottom_text,
            category_instruction;
    private CustomScrollView fill_main_scrollview;
    private ImageView category_rightimage_layout, category_leftimage_layout;

    private TextView hotspotlabel, hotspotmax_counttext;
    private KatexView hotspotlabelMaths;

    private RelativeLayout hotspot_mainlayout, hotspotlabel_layout, ref_image, questionno_layout;
    private ImageView hotspot_img;
    private CustomTouchIndicatorClass hotspot_imgtouch_indicator;

    private Handler myHandler;
    private int scrWidth, scrHeight;
    private String questionType;
    private boolean isfavouriteClicked = false;
    private RelativeLayout hotspotindicator_layout;

    public int totalAttempt = 0;
    public int optionSelected = 0;
    private int finalScr = 0;

    private String courseId;
    private boolean isRandomizeQuestion;
    private boolean isHotSpotThumbsUpShown = true;
    private boolean isHotSpotThumbsDownShown = true;
    private boolean isThirdFourthHotSpotImage = false; // this is hardcoded value for testing purpose
    private float deviceXFactor, deviceYFactor;
    private CardView CardViewNextCard, CardViewReadmore;
    private LinearLayout layout_progress_hotspot;
    private TextView mTextview_progress_hotspot, mText_progress_failed;

    private View learningquiz_solutionlayout;
    private boolean isThumbsShown = true; //this is hard coded need to
    private boolean isImageDownloaded = false;

    public void setQuestionImageShown(boolean questionImageShown) {
        isQuestionImageShown = questionImageShown;
    }

    private boolean isQuestionImageShown = true; //this is hard coded need to to come from api
    private boolean isHotSpotAnswerSelectedRight;
    private int noOfTimesClicked;
    private int noOfTimesWrong;
    private boolean isCourseCompleted;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private String cardId;
    private List<FavCardDetails> favCardDetailsList;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList, String cardId) {
        this.favCardDetailsList = favCardDetailsList;
        this.cardId = cardId;
    }

    private boolean isQuesttsEnabled;

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }


    private boolean isRMFavourite;

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void setCourseComeplted(boolean isCourseCompleted) {
        this.isCourseCompleted = isCourseCompleted;
    }

    private boolean isAssessmentQuestion = false;

    public void setAssessmentQuestion(boolean assessmentQuestion) {
        isAssessmentQuestion = assessmentQuestion;
        if (isAssessmentQuestion)
            isThirdFourthHotSpotImage = true;
    }

    private int cardCount;

    Bitmap hotspotimage;
    String media = null;

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
    }

    private Scores assessmentScore;

    public void setAssessmentScore(Scores assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    private boolean showNavigateArrow;

    public void setShowNavigateArrow(boolean showNavigateArrow) {
        this.showNavigateArrow = showNavigateArrow;
    }

    private boolean videoOverlay = false;
    private boolean isRegularMode = false;

    public void setRegularMode(boolean isRegularMode) {
        this.isRegularMode = isRegularMode;
    }

    private boolean isComingFromFeed = false;
    private NewsFeedProgressListener newsFeedProgressListener;

    public void isComingFromFeed(boolean isComingFromFeed) {
        this.isComingFromFeed = isComingFromFeed;
    }

    public void setNewsFeedProgressListener(NewsFeedProgressListener newsFeedProgressListener) {
        this.newsFeedProgressListener = newsFeedProgressListener;
    }

    private boolean proceedOnWrong = true;

    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learningquiznew, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mRootView = view;
        initViews(view);
        initModuViewFragment();
        return view;
    }

    private void initViews(View view) {
        learningquiz_animviewb = view.findViewById(R.id.learningquiz_animviewb);
        learningquiz_animviewa = view.findViewById(R.id.learningquiz_animviewa);
        learningquiz_mainlayout = view.findViewById(R.id.learningquiz_mainlayout);
        learningquiz_mainquestionImage = view.findViewById(R.id.learningquiz_mainquestionImage);
        learningquiz_mainquestionTime = view.findViewById(R.id.learningquiz_mainquestionTime);
        learningquiz_mainquestion = view.findViewById(R.id.learningquiz_mainquestion);
        learningquiz_mainquestionText = view.findViewById(R.id.learningquiz_mainquestionText);

        learningquiz_mainquestion_maths = view.findViewById(R.id.learningquiz_mainquestion_maths);
        learningquiz_mainquestionText_maths = view.findViewById(R.id.learningquiz_mainquestionText_maths);

        solution_desc = view.findViewById(R.id.solution_desc);
        solution_readmore_text = view.findViewById(R.id.solution_readmore_text);
        solution_readmore = view.findViewById(R.id.solution_readmore);
        learningcard_coursename = view.findViewById(R.id.learningcard_coursename);
        match_option_a_left_layout = view.findViewById(R.id.match_option_a_left_layout);
        match_option_b_left_layout = view.findViewById(R.id.match_option_b_left_layout);
        match_option_c_left_layout = view.findViewById(R.id.match_option_c_left_layout);
        match_option_d_left_layout = view.findViewById(R.id.match_option_d_left_layout);
        match_option_a_right_layout = view.findViewById(R.id.match_option_a_right_layout);
        match_option_b_right_layout = view.findViewById(R.id.match_option_b_right_layout);
        match_option_c_right_layout = view.findViewById(R.id.match_option_c_right_layout);
        match_option_d_right_layout = view.findViewById(R.id.match_option_d_right_layout);
        fill_blanks_layout = view.findViewById(R.id.fill_blanks_layout);
        fill_submit_layout = view.findViewById(R.id.fill_submit_layout);
        animoc_layout = view.findViewById(R.id.animoc_layout);
        ocanim_text = view.findViewById(R.id.ocanim_text);
        learningquiz_timertext = view.findViewById(R.id.learningquiz_timertext);
        learningcard_progress = view.findViewById(R.id.learningcard_progress);
        learningquiz_rightwrongimage = view.findViewById(R.id.learningquiz_rightwrongimage);
        learningquiz_solutionlayout = view.findViewById(R.id.solun_quiz_view);
        learningquiz_mainoptionlayout = view.findViewById(R.id.learningquiz_mainoptionlayout);
        questionmore_btn = view.findViewById(R.id.questionmore_btn);
        bottomswipe_view = view.findViewById(R.id.bottomswipe_view);
        cardprogress_text = view.findViewById(R.id.cardprogress_text);
        gotopreviousscreen_mainbtn = view.findViewById(R.id.gotopreviousscreen_mainbtn);
        question_arrowback = view.findViewById(R.id.question_arrowback);
        question_arrowfoword = view.findViewById(R.id.question_arrowfoword);
        showsolution_img = view.findViewById(R.id.showsolution_img);
        quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
        quiz_backgroundimagea_downloaded = view.findViewById(R.id.quiz_backgroundimagea_downloaded);
        gotonextscreen_btn = view.findViewById(R.id.gotonextscreen_btn);
        learning_new_ll = view.findViewById(R.id.learning_new_ll);
        solution_closebtn = (ImageButton) view.findViewById(R.id.solution_closebtn);
        gotonextscreen_mainbtn = view.findViewById(R.id.gotonextscreen_mainbtn);
        mainoption_scrollview = view.findViewById(R.id.mainoption_scrollview);
        unfavourite = view.findViewById(R.id.unfavourite);

        solution_label = view.findViewById(R.id.solution_label);
        solution_label_maths = view.findViewById(R.id.solution_label_maths);

        fill_main_scrollview = view.findViewById(R.id.fill_main_scrollview);
        fill_bottom_text = view.findViewById(R.id.fill_bottom_text);
        fill_blanks_title = view.findViewById(R.id.fill_blanks_title);
        correct_option_layout = view.findViewById(R.id.correct_option_layout);
        wrong_right_text = view.findViewById(R.id.wrong_right_text);
        correct_option_main_layout = view.findViewById(R.id.correct_option_main_layout);
        tutorial_scr = view.findViewById(R.id.tutorial_scr);
        learning_tutorial_imageView = view.findViewById(R.id.learning_tutorial_imageView);
        matchfollowing_layout = view.findViewById(R.id.matchfollowing_layout);
        draglebel_text = view.findViewById(R.id.draglebel_text);

        questionsubans_editetext = view.findViewById(R.id.questionsubans_editetext);
        questionsubans_limittext = view.findViewById(R.id.questionsubans_limittext);
        ref_image = view.findViewById(R.id.ref_image);
        questionno_layout = view.findViewById(R.id.questionno_layout);
        questionsubans_submit_btn = view.findViewById(R.id.questionsubans_submit_btn);
        questionsubans_header = view.findViewById(R.id.questionsubans_header);
        questionsubans_cardlayout = view.findViewById(R.id.questionsubans_cardlayout);
        questionsubans_card = view.findViewById(R.id.questionsubans_card);

        match_option_a_left_image = view.findViewById(R.id.match_option_a_left_image);
        match_option_b_left_image = view.findViewById(R.id.match_option_b_left_image);
        match_option_c_left_image = view.findViewById(R.id.match_option_c_left_image);
        match_option_d_left_image = view.findViewById(R.id.match_option_d_left_image);
        match_option_a_right_image = view.findViewById(R.id.match_option_a_right_image);
        match_option_b_right_image = view.findViewById(R.id.match_option_b_right_image);
        match_option_c_right_image = view.findViewById(R.id.match_option_c_right_image);
        match_option_d_right_image = view.findViewById(R.id.match_option_d_right_image);
        questionaudio_btn = view.findViewById(R.id.questionaudio_btn);

        match_option_a_left_text = view.findViewById(R.id.match_option_a_left_text);
        match_option_b_left_text = view.findViewById(R.id.match_option_b_left_text);
        match_option_c_left_text = view.findViewById(R.id.match_option_c_left_text);
        match_option_d_left_text = view.findViewById(R.id.match_option_d_left_text);
        match_option_a_right_text = view.findViewById(R.id.match_option_a_right_text);
        match_option_b_right_text = view.findViewById(R.id.match_option_b_right_text);
        match_option_c_right_text = view.findViewById(R.id.match_option_c_right_text);
        match_option_d_right_text = view.findViewById(R.id.match_option_d_right_text);

        match_option_a_left_text_maths = view.findViewById(R.id.match_option_a_left_text_maths);
        match_option_b_left_text_maths = view.findViewById(R.id.match_option_b_left_text_maths);
        match_option_c_left_text_maths = view.findViewById(R.id.match_option_c_left_text_maths);
        match_option_d_left_text_maths = view.findViewById(R.id.match_option_d_left_text_maths);
        match_option_a_right_text_maths = view.findViewById(R.id.match_option_a_right_text_maths);
        match_option_b_right_text_maths = view.findViewById(R.id.match_option_b_right_text_maths);
        match_option_c_right_text_maths = view.findViewById(R.id.match_option_c_right_text_maths);
        match_option_d_right_text_maths = view.findViewById(R.id.match_option_d_right_text_maths);

        quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
        quiz_backgroundimagea_downloaded = view.findViewById(R.id.quiz_backgroundimagea_downloaded);
        ocanim_view = view.findViewById(R.id.ocanim_view);
        category_layout = view.findViewById(R.id.category_layout);
        flingContainer = view.findViewById(R.id.frame);
        category_righttext_layout = view.findViewById(R.id.category_righttext_layout);
        category_rightimage_layout = view.findViewById(R.id.category_rightimage_layout);
        category_leftimage_layout = view.findViewById(R.id.category_leftimage_layout);
        category_lefttext_layout = view.findViewById(R.id.category_lefttext_layout);
        category_optionsleft = view.findViewById(R.id.category_optionsleft);
        lpocimage = view.findViewById(R.id.lpocimage);
        category_instruction = view.findViewById(R.id.category_instruction);
        hotspotindicator_layout = view.findViewById(R.id.hotspotindicator_layout);

        mImageViewSolution = view.findViewById(R.id.imageViewSolution);

        OustSdkTools.setImage(learningquiz_mainquestionImage, OustSdkApplication.getContext().getResources().getString(R.string.whitequestion_img));
        OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
        learning_new_ll.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
        OustSdkTools.setImage(lpocimage, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));

        hotspotlabel = view.findViewById(R.id.hotspotlabel);
        hotspotlabelMaths = view.findViewById(R.id.hotspotlabelMaths);

        hotspotlabel_layout = view.findViewById(R.id.hotspotlabel_layout);
        hotspot_mainlayout = view.findViewById(R.id.hotspot_mainlayout);
        hotspotmax_counttext = view.findViewById(R.id.hotspotmax_counttext);
        hotspot_img = view.findViewById(R.id.hotspot_img);
        hotspot_imgtouch_indicator = view.findViewById(R.id.hotspot_imgtouch_indicator);

        myresponse_label = view.findViewById(R.id.myresponse_label);
        myresponse_desc = view.findViewById(R.id.myresponse_desc);

        CardViewNextCard = view.findViewById(R.id.CardViewNextCard);
        CardViewNextCard.setCardBackgroundColor(getContext().getResources().getColor(R.color.Orange));

        CardViewReadmore = view.findViewById(R.id.CardViewReadmore);
        CardViewReadmore.setCardBackgroundColor(getContext().getResources().getColor(R.color.new_gray));

        layout_progress_hotspot = view.findViewById(R.id.layout_progress_hotspot);
        mTextview_progress_hotspot = view.findViewById(R.id.text_progress_hotspot);
        mText_progress_failed = view.findViewById(R.id.text_progress_failed);
        layout_progress_hotspot.setVisibility(View.GONE);

        match_option_a_left_layout.setOnTouchListener(this);
        match_option_b_left_layout.setOnTouchListener(this);
        match_option_c_left_layout.setOnTouchListener(this);
        match_option_d_left_layout.setOnTouchListener(this);

        gotonextscreen_btn.setOnClickListener(this);
        questionmore_btn.setOnClickListener(this);
        solution_closebtn.setOnClickListener(this);
        gotopreviousscreen_mainbtn.setOnClickListener(this);
        gotonextscreen_mainbtn.setOnClickListener(this);
        showsolution_img.setOnClickListener(this);
        questionaudio_btn.setOnClickListener(this);

        setFont();

        if (isAssessmentQuestion) {
            questionno_layout.setVisibility(View.GONE);
            ref_image.setVisibility(View.GONE);
        }

        if (isVideoOverlay()) {
            ref_image.setVisibility(View.GONE);
        }
        //mLearningQuizSolutionLayout = view.findViewById(R.id.)
    }

    private void setFont() {
        category_optionsleft.setText(getResources().getString(R.string.cards_left));
        category_instruction.setText(getResources().getString(R.string.category_instruction_msg));
        hotspotmax_counttext.setText(getResources().getString(R.string.attempt_left_text) + " 3");
        solution_label.setText(getResources().getString(R.string.solution));
        myresponse_label.setText(getResources().getString(R.string.my_response));
        questionsubans_editetext.setHint(getResources().getString(R.string.enter_response_hint));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        learningModuleInterface.changeOrientationPortrait();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (OustSdkTools.textToSpeech != null) {
            OustSdkTools.stopSpeech();
            if (questionaudio_btn != null)
                questionaudio_btn.setAnimation(null);
        }
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard(previousView);
        if (OustSdkTools.textToSpeech != null) {
            OustSdkTools.stopSpeech();
        }
    }


    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setZeroXpForQCard(boolean zeroXpForQCard) {
        this.zeroXpForQCard = zeroXpForQCard;
    }

    private int learningcardProgress = 0;

    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    private DTOQuestions questions;

    public DTOCourseCard mainCourseCardClass;

    private int questionXp = 20;

    public void setMainCourseCardClass(DTOCourseCard courseCardClass2) {
        try {
            Log.d(TAG, "detectCardLayoutAndSetData: card ID: " + courseCardClass.getCardId() + " --- xp:" + mainCourseCardClass.getXp());
            int savedCardID = (int) courseCardClass2.getCardId();
            //RealmCourseCardClass realmCourseCardClass=RealmHelper.getCardById(savedCardID);
            this.mainCourseCardClass = courseCardClass2;
            isRandomizeQuestion = courseCardClass2.getQuestionData().isRandomize();
            if (isRandomizeQuestion) {
                this.mainCourseCardClass = randomizeOption(this.mainCourseCardClass);
            }
            if (this.mainCourseCardClass.getXp() == 0) {
                this.mainCourseCardClass.setXp(100);
            }
        } catch (Exception e) {
            this.mainCourseCardClass = courseCardClass2;
        }
        //long qid=this.mainCourseCardClass.getqId();
        questions = this.mainCourseCardClass.getQuestionData();
        if (mainCourseCardClass != null && mainCourseCardClass.getXp() != 0) {
            questionXp = (int) mainCourseCardClass.getXp();
        }

        if (questions == null) {
            if (learningModuleInterface != null) {
                if (!isVideoOverlay()) {
                    learningModuleInterface.endActivity();
                } else {
                    learningModuleInterface.closeChildFragment();
                }
            }
        }
        if (questions != null) {
            isThumbsShown = questions.isThumbsUpDn();
            isThirdFourthHotSpotImage = !questions.isFullScreenHotSpot();
            isHotSpotThumbsDownShown = questions.isThumbsDown();
            isHotSpotThumbsUpShown = questions.isThumbsUp();
            //isQuestionImageShown = mainCourseCardClass.isShowQuestionSymbolForQuestion();
        }
    }

    private DTOCourseCard randomizeOption(DTOCourseCard courseCardClass) {
        try {
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION") && (courseCardClass.getQuestionData().isRandomize())) {
                //long qid=courseCardClass.getqId();
                DTOQuestions learningQuestionData = courseCardClass.getQuestionData();
                if ((learningQuestionData.getQuestionType() != null) && (learningQuestionData.getQuestionType() != QuestionType.MRQ)) {
                    if (learningQuestionData.getQuestionCategory() != null) {
                        if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                            Collections.shuffle(learningQuestionData.getMtfLeftCol());
                        }
                    }
                }
                courseCardClass.setQuestionData(learningQuestionData);
            }
        } catch (Exception e) {
        }
        return courseCardClass;
    }

    private boolean zeroXpForQCard;

    private String questionCategory;

    public void initModuViewFragment() {
        getWidth();
        if (!isVideoOverlay()) {
            enableSwipe();
        }
        myHandler = new Handler();
        isImageDownloaded = false;
        if (questions != null) {
            questionCategory = questions.getQuestionCategory();
            questionType = questions.getQuestionType();
        }
        if (!isQuestionImageShown) {
            questionno_layout.setVisibility(View.GONE);
        }

        setStartingData();
        setQuestionNo();
        setColors();
    }

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;
    private boolean tochedScreen = false;

    public void enableSwipe() {
        mainoption_scrollview.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                x1 = event.getX();
                y1 = event.getY();
            } else if (action == MotionEvent.ACTION_UP) {
                x2 = event.getX();
                y2 = event.getY();
                float deltaX = x1 - x2;
                float deltaY = y1 - y2;
                if (deltaX > 0 && deltaY > 0) {
                    if (deltaX > deltaY) {
                        if (deltaX > MIN_DISTANCE) {
                            if (isVideoOverlay()) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.closeChildFragment();
                            } else {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoNextScreen();
                            }
                        }
                    }
                } else if (deltaX < 0 && deltaY > 0) {
                    if ((-deltaX) > deltaY) {
                        if ((-deltaX) > MIN_DISTANCE) {
                            if (isVideoOverlay()) {

                            } else {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoPreviousScreen();
                            }
                        }
                    }

                } else if (deltaX < 0 && deltaY < 0) {
                    if (deltaX < deltaY) {
                        if ((-deltaX) > MIN_DISTANCE) {
                            if (isVideoOverlay()) {

                            } else {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoPreviousScreen();
                            }
                        }
                    }
                } else if (deltaX > 0 && deltaY < 0) {
                    if (deltaX > (-deltaY)) {
                        if (deltaX > MIN_DISTANCE) {
                            if (isVideoOverlay()) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.closeChildFragment();
                            } else {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoNextScreen();
                            }
                        }
                    }
                }
            }
            return false;
        });
    }


    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = mainCourseCardClass.getCardColorScheme();
            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowback.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowfoword.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    showsolution_img.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                }
                if ((cardColorScheme.getLevelNameColor() != null) && (!cardColorScheme.getLevelNameColor().isEmpty())) {
                    learningquiz_timertext.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    learningcard_coursename.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    cardprogress_text.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                }
                if ((cardColorScheme.getTitleColor() != null) && (!cardColorScheme.getTitleColor().isEmpty())) {
                    learningquiz_timertext.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                }
                if ((cardColorScheme.getBgImage() != null) && (!cardColorScheme.getBgImage().isEmpty())) {
                    setBackgroundImage(cardColorScheme.getBgImage());
                } else {
                    if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                        setBackgroundImage(cardBackgroundImage);
                    }
                }
            } else {
                if ((cardBackgroundImage != null) && (!cardBackgroundImage.isEmpty())) {
                    setBackgroundImage(cardBackgroundImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            //OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.bg_1));
            learning_new_ll.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
            if (bgImageUrl != null && !bgImageUrl.isEmpty()) {
                quiz_backgroundimagea_downloaded.setVisibility(View.VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(bgImageUrl).into(quiz_backgroundimagea_downloaded);
                } else {
                    Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(quiz_backgroundimagea_downloaded);
                }
            }
        } catch (Exception e) {
        }
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    public ArrayList<View> fillViewsCreated = new ArrayList<>();

    int waitTimer = 0;

    private void setStartingData() {
        try {
            if (isQuestionImageShown) {
                waitTimer = ONE_SECOND;
                if (learningcardProgress == 0) {
                    waitTimer = 1200;
                }
            }

            if (!isRegularMode) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startAnimation();
                            if (questions != null) {
                                setTitle();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, waitTimer);
            } else {
                try {
                    startAnimation();
                    if (questions != null) {
                        setTitle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setQuestionNo() {
        try {
            if (learningcardProgress == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            }
            learningquiz_mainquestionText.setText(getResources().getString(R.string.question_text) + " " + (learningcardProgress + 1));
            long millis = questions.getMaxtime();
            String hms = String.format("%02d:%02d", millis / 60, millis % 60);
            learningquiz_mainquestionTime.setText(hms);
            if (courseLevelClass != null && courseLevelClass.getCourseCardClassList() != null) {
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + courseLevelClass.getCourseCardClassList().size());
                learningcard_progress.setMax((courseLevelClass.getCourseCardClassList().size() * 50));
                learningcard_progress.setProgress((learningcardProgress * 50));

                ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
                animation.setDuration(SIX_HUNDRED_MILLI_SECONDS);
                animation.setStartDelay(FIVE_HUNDRED_MILLI_SECONDS);
                animation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startAnimation() {
        try {
            if (isAssessmentQuestion) {
                if ((questionType != null) && ((questionType.equals(QuestionType.FILL))
                        ||
                        ((questionType.equals(QuestionType.FILL_1)))
                        ||
                        (questionType.equals(QuestionType.FILL_TYPE1)))) {
                    if (questionType.equals(QuestionType.FILL)) {
                        questionType = QuestionType.FILL_TYPE1;
                    }
                    new CreateFillTask(getActivity()).execute();
                }
                loadQuestionData();
            } else {
                if (isRegularMode) {
                    learningquiz_mainquestionImage.setVisibility(View.GONE);
                    learningquiz_mainquestionTime.setVisibility(View.GONE);
                    if ((questionType != null) && ((questionType.equals(QuestionType.FILL)) || ((questionType.equals(QuestionType.FILL_1))) ||
                            (questionType.equals(QuestionType.FILL_TYPE1)))) {
                        if (questionType.equals(QuestionType.FILL)) {
                            questionType = QuestionType.FILL_TYPE1;
                        }
                        new CreateFillTask(getActivity()).execute();
                    }
                    loadQuestionData();
                    return;
                }

                if (isQuestionImageShown) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                    learningquiz_mainquestionImage.startAnimation(anim);
                    android.view.animation.Animation quizout = AnimationUtils.loadAnimation(getActivity(), R.anim.event_animmoveout);
                    learningquiz_mainquestionTime.startAnimation(quizout);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if ((questionType != null) && ((questionType.equals(QuestionType.FILL)) || ((questionType.equals(QuestionType.FILL_1))) ||
                                    (questionType.equals(QuestionType.FILL_TYPE1)))) {
                                if (questionType.equals(QuestionType.FILL)) {
                                    questionType = QuestionType.FILL_TYPE1;
                                }
                                new CreateFillTask(getActivity()).execute();
                            }
                            loadQuestionData();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                } else {
                    questionno_layout.setVisibility(View.GONE);
                    if ((questionType != null) && ((questionType.equals(QuestionType.FILL)) || ((questionType.equals(QuestionType.FILL_1))) ||
                            (questionType.equals(QuestionType.FILL_TYPE1)))) {
                        if (questionType.equals(QuestionType.FILL)) {
                            questionType = QuestionType.FILL_TYPE1;
                        }
                        new CreateFillTask(getActivity()).execute();
                    }
                    loadQuestionData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadQuestionData() {
        Log.d(TAG, "loadQuestionData: " + questionType);
        if ((questionType != null) && ((questionType.equals(QuestionType.FILL))
                || ((questionType.equals(QuestionType.FILL_TYPE1))) ||
                (((questionType.equals(QuestionType.FILL_1)))))) {
            if (questionType.equals(QuestionType.FILL)) {
                questionType = QuestionType.FILL_TYPE1;
            }
            if (!isAssessmentQuestion) {
                startSpeakQuestion();
            }
        } else {
            if (questionCategory != null && (questionCategory.equals(QuestionCategory.MATCH)) || questionCategory != null && (questionCategory.equals(QuestionCategory.CATEGORY))) {
                if (questionCategory != null && (questionCategory.equals(QuestionCategory.MATCH))) {
                    if (OustPreferences.getAppInstallVariable("match_tutorial_shown")) {
                        startTimer();
                        if (!isAssessmentQuestion) {
                            startSpeakQuestion();
                        }
                    }
                }

                if (questionCategory != null && (questionCategory.equals(QuestionCategory.CATEGORY))) {
                    if (OustPreferences.getAppInstallVariable("category_tutorial_shown")) {
                        startTimer();
                        if (!isAssessmentQuestion) {
                            startSpeakQuestion();
                        }
                    }
                }
            } else {
                if (questionCategory != null && questionCategory.equals(QuestionCategory.HOTSPOT)) {
                    //No timer
                } else {
                    startTimer();
                    if (!isAssessmentQuestion) {
                        startSpeakQuestion();
                    }
                }
            }
        }
        if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
            setQuestionTitle();
            setMatchQuestionLayoutSize();

            if (!isAssessmentQuestion && !OustPreferences.getAppInstallVariable("match_tutorial_shown")) {
                showTutorial("MATCH");
            } else {
                startTimer();
            }
        } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.HOTSPOT))) {
            startHotspotQuestion();
        } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.CATEGORY))) {
            OustSdkTools.totalAttempt = 0;
            OustSdkTools.optionSelected = 0;
            if (isAssessmentQuestion) {
                OustSdkTools.isAssessmentQuestion = true;
                OustSdkTools.totalCategoryRight = 0;
            } else {
                OustSdkTools.isAssessmentQuestion = false;
            }
            setQuestionTitle();
            startCategoryLayout();

            if (!isAssessmentQuestion && !OustPreferences.getAppInstallVariable("category_tutorial_shown")) {
                showTutorial("CATEGORY");
            } else {
                startTimer();
            }
        }
    }

    private void showTutorial(final String type) {
        tutorial_scr.setVisibility(View.VISIBLE);
        if (type.equalsIgnoreCase("MATCH"))
            learning_tutorial_imageView.setImageResource(R.drawable.mtf_tutorial);
        else if (type.equalsIgnoreCase("CATEGORY"))
            learning_tutorial_imageView.setImageResource(R.drawable.category_tutorial);

        tutorial_scr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("MATCH")) {
                    OustPreferences.saveAppInstallVariable("match_tutorial_shown", true);
                } else if (type.equalsIgnoreCase("CATEGORY")) {
                    OustPreferences.saveAppInstallVariable("category_tutorial_shown", true);
                }
                tutorial_scr.setVisibility(View.GONE);
                startTimer();

                if (!isAssessmentQuestion) {
                    startSpeakQuestion();
                }
            }
        });
    }

    private void startSpeakQuestion() {
        try {
            Log.d(TAG, "startSpeakQuestion: ");
            if ((questions.getQuestion() != null)) {
                if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String audioPath = questions.getAudio();
                                String s3AudioFileName = audioPath;
                                if (audioPath.contains("/")) {
                                    s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                }
                                playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                            } catch (Exception e) {
                            }
                        }
                    }, FIVE_HUNDRED_MILLI_SECONDS);
                } else {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    Spanned s1 = getSpannedContent(questions.getQuestion());
                                    String quesStr = s1.toString().trim();
                                    speakString(quesStr);
//                                    } else {
//                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
//                                        if (OustSdkTools.textToSpeech != null) {
//                                            OustSdkTools.stopSpeech();
//                                            questionaudio_btn.setAnimation(null);
//                                        }
//                                    }
                                } catch (Exception e) {
                                }
                            }
                        }, FIVE_HUNDRED_MILLI_SECONDS);
                    } else {
                        questionaudio_btn.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void speakString(String str) {
        try {
            if (isAppIsInForeground()) {
                String newStr = "";
                newStr = str.replaceAll("[_]+", "\n dash \n");
                int n1 = newStr.length();
                if (n1 > 100)
                    n1 = 100;
                boolean isHindi = false;
                for (int i = 0; i < newStr.length(); i++) {
                    int no = newStr.charAt(i);
                    if (no > 2000) {
                        isHindi = true;
                        break;
                    }
                }
                TextToSpeech textToSpeech = OustSdkTools.getSpeechEngin();
                if (isHindi) {
                    textToSpeech = OustSdkTools.getHindiSpeechEngin();
                }
                if (textToSpeech != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(newStr, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        textToSpeech.speak(newStr, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                View view = questionaudio_btn;
                float count = str.length() / 20;
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1, 0.75f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1, 0.75f);
                scaleDownX.setDuration(ONE_SECOND);
                scaleDownY.setDuration(ONE_SECOND);
                scaleDownX.setRepeatCount((int) count);
                scaleDownY.setRepeatCount((int) count);
                scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                scaleDownX.setInterpolator(new DecelerateInterpolator());
                scaleDownY.setInterpolator(new DecelerateInterpolator());
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);
                scaleDown.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAppIsInForeground() {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }

    private boolean musicComplete = false;

    private void playDownloadedAudioQues(final String filename) {
        cancelSound();
        mediaPlayer = new MediaPlayer();
        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {

//                    EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//                    String audStr = enternalPrivateStorage.readSavedData(filename);
//                    if ((audStr != null) && (!audStr.isEmpty())) {
//                        musicComplete = false;
//                        try {
//                            byte[] audBytes = Base64.decode(audStr, 0);
//                            // create temp file that will hold byte array
//                            File tempMp3 = File.createTempFile(filename, null, getActivity().getCacheDir());
//                            tempMp3.deleteOnExit();
//                            FileOutputStream fos = new FileOutputStream(tempMp3);
//                            fos.write(audBytes);
//                            fos.close();
//                            questionaudio_btn.setVisibility(View.VISIBLE);
//
//                            // resetting mediaplayer instance to evade problems
//                            mediaPlayer.reset();
//
//                            FileInputStream fis = new FileInputStream(tempMp3);
//                            mediaPlayer.setDataSource(fis.getFD());
//                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//
//                            Animation scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
//                            questionaudio_btn.startAnimation(scaleanim);
//                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mediaPlayer) {
//                                    musicComplete = true;
//                                    questionaudio_btn.setAnimation(null);
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    try {
                        File audiofile = new File(getActivity().getFilesDir(), filename);
                        if (audiofile != null && audiofile.exists()) {
                            questionaudio_btn.setVisibility(View.VISIBLE);
                            mediaPlayer.reset();

                            FileInputStream fis = new FileInputStream(audiofile);
                            mediaPlayer.setDataSource(fis.getFD());
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();

                            Animation scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);
                            questionaudio_btn.startAnimation(scaleanim);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    musicComplete = true;
                                    questionaudio_btn.setAnimation(null);
                                }
                            });
                        }

                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setTitle() {
        try {
            if (courseLevelClass.getLevelName() != null) {
                Log.d(TAG, "setTitle: " + courseLevelClass.getLevelName());
                learningcard_coursename.setText(courseLevelClass.getLevelName());
            }
        } catch (Exception e) {
        }
    }

    private void setQuestionTitle() {
        try {
            if ((questions.getQuestion() != null)) {
                Log.d(TAG, "setQuestionTitle: with out span " + questions.getQuestion() + "Spanned :" + getSpannedContent(questions.getQuestion()));
                if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                    learningquiz_mainquestion_maths.setText(questions.getQuestion());
                    learningquiz_mainquestion_maths.setVisibility(View.VISIBLE);
                } else {
                    OustSdkTools.getSpannedContent(questions.getQuestion(), learningquiz_mainquestion);
                    learningquiz_mainquestion.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
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

    //---------------------

    //=============================================================================================================
    //methode for fill in the blank


    PointF DownPT = new PointF();
    PointF StartPT = new PointF();
    PointF StartOrgPT = new PointF();
    private String selectedAns = "";
    private boolean touched = false;
    private boolean category_touched = false;
    private List<PointF> fillAnswersPoint = new ArrayList<>();

    private List<String> answerStrs = new ArrayList<>();
    private List<View> fill_views = new ArrayList<>();
    private List<OustFillData> emptyViews = new ArrayList<>();
    private List<OustFillData> answerView = new ArrayList<>();
    //    private List<String> realAnsStrs = new ArrayList<>();
    private LayoutInflater mInflater;
    private int attemptWrongCount = 0;
    public int totalOption = 0;
    private int maxlength = 0;
    private View movin_view;
    private boolean hotspotcomplete = false, complete = false;
    private int totalFillAssessmentCorrect = 0;
    private int totalFillAssessmentWrong = 0;
    private int previousFillIndex = 0;
    private EditText previousView = null;

    private void contentAppearAnim() {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_mainoptionlayout, "translationY", learningquiz_mainoptionlayout.getHeight(), 0);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_mainoptionlayout, "alpha", 0.0f, 1.0f);
            scaleDownX.setDuration(THREE_HUNDRED_MILLI_SECONDS);
            scaleDownY.setDuration(SIX_HUNDRED_MILLI_SECONDS);
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
                    ShowPopup.getInstance().dismissProgressDialog();
                    startTimer();
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

    public boolean isVideoOverlay() {
        return videoOverlay;
    }

    public void setVideoOverlay(boolean videoOverlay) {
        this.videoOverlay = videoOverlay;
    }

    public boolean isProceedOnWrong() {
        return proceedOnWrong;
    }

    public void setProceedOnWrong(boolean proceedOnWrong) {
        this.proceedOnWrong = proceedOnWrong;
    }

    public class CreateFillTask extends AsyncTask<String, String, String> {

        private Context mContext;

        public CreateFillTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            startFillLayout();
            inflateToMyViews();
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (!isAssessmentQuestion) {
                ShowPopup.getInstance().showProgressBar(getActivity(), "loading views...Please wait");
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < fillViewsCreated.size(); i++) {
                        fill_blanks_layout.addView(fillViewsCreated.get(i));
                    }

                    if (isAssessmentQuestion) {
                        showFillLayout();
                    } else {
                        myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                            showKeyboard();
                                showFillLayout();
                            }
                        }, ONE_SECOND);
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private void showFillLayout() {
        fill_main_scrollview.setVisibility(View.VISIBLE);
        fill_blanks_layout.setVisibility(View.VISIBLE);
        mainoption_scrollview.setVisibility(View.GONE);
        contentAppearAnim();
    }

    private void startFillLayout() {
        try {
            Log.d(TAG, "startFillLayout: ");
            if (mInflater == null) {
                mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            }
            Spanned s1 = getSpannedContent(questions.getQuestion());
            String fillQuestion = (s1.toString().trim());
//            fillQuestion = fillQuestion.replace("_.", "_ .");
//            fillQuestion = fillQuestion.replace("_,", "_ ,");
            fillQuestion = fillQuestion.replace("____", " ____ ");
            String[] strings = fillQuestion.split(" ");
            String[] ansStrs = new String[strings.length];
            String[] options = new String[questions.getAnswer().split("#").length];
            options = questions.getAnswer().split("#");
            if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                options = questions.getA().split("#");
            }
            Collections.shuffle(Arrays.asList(options));

            for (int i = 0; i < options.length; i++) {
                Spanned s3 = getSpannedContent(options[i]);
                answerStrs.add(s3.toString());
                if (maxlength < s3.toString().length()) {
                    maxlength = s3.toString().length();
                }
                totalOption++;
            }

            //maintain dash length equal for all options
            String dummyStr = " ";
            for (int j = 0; j < maxlength; j++) {
                dummyStr += "_";
            }

            int n1 = 0;
            int fillIndex = 1;
            while (n1 < strings.length) {
                if (strings[n1].contains("____")) {
                    View fillTextView1;
                    if (questionType.equalsIgnoreCase(QuestionType.FILL_TYPE1)) {
                        fillTextView1 = mInflater.inflate(R.layout.fill_emptyedit_layout, null);
                        EditText editText = fillTextView1.findViewById(R.id.fill_edittext);
                        RelativeLayout close_layout = fillTextView1.findViewById(R.id.close_layout);

                        int width = (int) ((dummyStr.length()) * (OustSdkApplication.getContext().getResources().getDimension(R.dimen.ousttext_dimen9)));
                        editText.getLayoutParams().width = width;

//                        following lines restrict number of character in edit text
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(dummyStr.length());
                        editText.setFilters(FilterArray);

                        editText.setId(100 + fillIndex);
                        editText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onEmptyViewTouch(view);

                            }
                        });
                        editText.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View view, int keyCode, KeyEvent event) {
                                if (keyCode == EditorInfo.IME_ACTION_GO || keyCode == EditorInfo.IME_ACTION_DONE ||
                                        event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    selectEditText();
                                    return true;
                                }
                                return false;
                            }
                        });
                        close_layout.setId(50 + fillIndex);
                        close_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onCloseViewTouch(view);
                            }
                        });
                        fillIndex++;
                    } else if (questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                        fillTextView1 = mInflater.inflate(R.layout.fill_emptybox_layout, null);
                        TextView textView = fillTextView1.findViewById(R.id.fill_dummy_text);
                        textView.setText(dummyStr);
                        TextView fill_text = fillTextView1.findViewById(R.id.fill_text);
                        fill_text.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        fillTextView1.setId(100 + fillIndex);
                        fillTextView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onEmptyBoxClick(view);
                            }
                        });
                        if (isAssessmentQuestion) {
                            RelativeLayout close_layout = fillTextView1.findViewById(R.id.close_layout);
                            close_layout.setId(50 + fillIndex);
                            close_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onCloseViewTouch(view);
                                }
                            });
                        }

                        fillIndex++;
                    } else {
                        fillTextView1 = mInflater.inflate(R.layout.fill_emptylayout, null);
                        fillTextView1.setId(100 + fillIndex);
                        fillIndex++;
                        TextView dummytext = fillTextView1.findViewById(R.id.dummytext);
                        dummytext.setText(dummyStr);

                        TextView fill_mainlayoutb = fillTextView1.findViewById(R.id.fill_mainlayoutb);
                        fill_mainlayoutb.setText(dummyStr);
                    }

                    fill_views.add(fillTextView1);
                    OustFillData oustFillData = new OustFillData();
                    oustFillData.setView(fillTextView1);
                    oustFillData.setIndex(n1);
                    emptyViews.add(oustFillData);
                } else {
                    View fillTextView = mInflater.inflate(R.layout.fill_text_layout, null);
                    TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                    textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView1.setText(" " + strings[n1]);
                    fill_views.add(fillTextView);
                }
                n1++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void inflateToMyViews() {
        try {
            int x = 20;
            int y = (int) getResources().getDimension(R.dimen.oustlayout_dimen35);
            String dummyStr = " ";

            for (int j = 0; j < maxlength; j++) {
                dummyStr += "_";
            }

            for (int i = 0; i < fill_views.size(); i++) {
                View view = fill_views.get(i);
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int totalW = metrics.widthPixels - 20;
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = 20;
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen40);
                    view.setX(x);
                    view.setY(y);
                }
                x += viewW;
                fillViewsCreated.add(view);
            }

            if (questionType.equalsIgnoreCase(QuestionType.FILL_TYPE1)) {
                fill_main_scrollview.setEnableScrolling(true);
                addParamForFillLayout(y, 0);
            } else {
                addAnswerInBottomOfFill(x, y, dummyStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addAnswerInBottomOfFill(int x, int y, String dummyStr) {
        try {
            int starty = y;
            int startx = x;

            for (int j = 0; j < answerStrs.size(); j++) {
                View fillTextView = mInflater.inflate(R.layout.fill_textanswer_layout, null);
                fillTextView.setId(1000 + j);
                if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                    fillTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBottomViewClick(view);
                        }
                    });
                } else {
                    fillTextView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (!complete) {
                                fill_main_scrollview.setEnableScrolling(false);
                                onViewTouch(v, event);
                                return true;
                            }
                            return false;
                        }
                    });
                }
                TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                TextView index_text = fillTextView.findViewById(R.id.index_text);
                index_text.setText("" + j);
                textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                textView1.setText(answerStrs.get(j));
                TextView dummytext = fillTextView.findViewById(R.id.dummytext);
                dummytext.setText(dummyStr);
                OustFillData oustFillData = new OustFillData();
                oustFillData.setView(fillTextView);
                oustFillData.setIndex(j);
                answerView.add(oustFillData);
            }

            if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                startx = (int) (int) ((float) (0.02 * scrWidth));
                starty = (int) getResources().getDimension(R.dimen.oustlayout_dimen5);
            } else {
                starty += (int) getResources().getDimension(R.dimen.oustlayout_dimen70);
                startx = (int) ((float) (0.02 * scrWidth));
            }
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int totalW = metrics.widthPixels - 20;
            fillAnswersPoint = new ArrayList<>();

            for (int i = 0; i < answerView.size(); i++) {
                View view = answerView.get(i).getView();
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((startx + viewW) < totalW) {
                    view.setX(startx);
                    view.setY(starty);
                } else {
                    startx = (int) ((float) (0.02 * scrWidth));
                    starty += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(startx);
                    view.setY(starty);
                }
                fillAnswersPoint.add(new PointF(startx, starty));
                startx += viewW + ((int) ((float) (0.01 * scrWidth)));

                if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                    correct_option_layout.addView(view);
                } else {
                    fill_blanks_layout.addView(view);
                }
            }
            addParamForFillLayout(y, starty);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addParamForFillLayout(final int y, final int startY) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addParamForFillLayoutMain(y, startY);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void addParamForFillLayoutMain(int y, int startY) {
        try {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fill_blanks_layout.getLayoutParams();
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen75);
            layoutParams.height = y;
            fill_blanks_layout.setLayoutParams(layoutParams);
            fill_bottom_text.setText(getResources().getString(R.string.fill_hint_msg));

            if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_TYPE1)) {
                fill_bottom_text.setVisibility(View.GONE);
                fill_submit_layout.setVisibility(View.VISIBLE);
                fill_blanks_title.setText(getResources().getString(R.string.fill_title));
                fill_blanks_title.setVisibility(View.VISIBLE);
                fill_blanks_layout.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
                fill_submit_layout.setOnClickListener(this);
//            showKeyboard();
                selectEditText();
                addCorrectViewsInBottom();
            } else if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) correct_option_layout.getLayoutParams();
                startY += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                layoutParams1.height = startY;
                correct_option_layout.setLayoutParams(layoutParams1);

                correct_option_main_layout.setVisibility(View.VISIBLE);
                wrong_right_text.setText("" + getActivity().getResources().getString(R.string.choose_answer));

                fill_blanks_title.setText(getResources().getString(R.string.fill_title));
                fill_blanks_title.setVisibility(View.VISIBLE);
                fill_bottom_text.setVisibility(View.GONE);
                fill_blanks_layout.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
                fill_submit_layout.setVisibility(View.VISIBLE);
                fill_submit_layout.setOnClickListener(this);
                selectNextBox();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onViewTouch(View v, MotionEvent event) {
        try {
            if (!complete) {
                int id = v.getId();
                id = id - 1000;
                if (id < 5) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                            v.setX((int) (StartPT.x + mv.x));
                            v.setY((int) (StartPT.y + mv.y));

                            StartPT = new PointF(StartPT.x + mv.x, StartPT.y + mv.y);
                            for (int idx = 0; idx < emptyViews.size(); idx++) {
                                if (emptyViews.get(idx).getView() != null) {
                                    float local_x = v.getX();
                                    float local_y = v.getY() + v.getHeight() / 2;
                                    float expected_startX = emptyViews.get(idx).getView().getX() - (emptyViews.get(idx).getView().getWidth() / 2);
                                    float expected_endX = emptyViews.get(idx).getView().getX() + (emptyViews.get(idx).getView().getWidth() / 2);
                                    float expected_startY = emptyViews.get(idx).getView().getY() + (emptyViews.get(idx).getView().getHeight() / 2);
                                    float expected_endY = emptyViews.get(idx).getView().getY() - (emptyViews.get(idx).getView().getHeight() / 2);
                                    if (((expected_startX < local_x) && (local_x < expected_endX))
                                            && (local_y < expected_startY) && (local_y > expected_endY)) {
                                        RelativeLayout fill_mainlayout = emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout);
                                        fill_mainlayout.setBackgroundColor(getResources().getColor(R.color.newblack_transparent));
                                    } else {
                                        RelativeLayout fill_mainlayout = emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout);
                                        fill_mainlayout.setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                                    }
                                }
                            }
                            if (StartPT.y < (scrHeight / 2)) {
                                fill_main_scrollview.scrollTo(0, 0);
                                v.setX((int) (StartPT.x));
                                v.setY(StartPT.y + 50);
                            }
                            movin_view = v;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            v.bringToFront();
                            DownPT.x = event.getX();
                            DownPT.y = event.getY();
                            StartPT = new PointF(v.getX(), v.getY());
                            StartOrgPT = new PointF(v.getX(), v.getY());
                            movin_view = null;
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "onViewTouch: ");
                            fill_main_scrollview.setEnableScrolling(true);
                            float local_x = v.getX();
                            float local_y = v.getY();
                            boolean isAnsFound = false;
                            for (int idx = 0; idx < emptyViews.size(); idx++) {
                                if (emptyViews.get(idx).getView() != null) {
                                    RelativeLayout fill_mainlayout = emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout);
                                    emptyViews.get(idx).getView().setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                                    float expected_startX = emptyViews.get(idx).getView().getX() - (emptyViews.get(idx).getView().getWidth() / 2);
                                    float expected_endX = emptyViews.get(idx).getView().getX() + (emptyViews.get(idx).getView().getWidth() / 2);
                                    float expected_startY = emptyViews.get(idx).getView().getY() + (emptyViews.get(idx).getView().getHeight() / 2);
                                    float expected_endY = emptyViews.get(idx).getView().getY() - (emptyViews.get(idx).getView().getHeight() / 2);
                                    if (((expected_startX < local_x) && (local_x < expected_endX)) &&
                                            ((local_y < expected_startY) && (local_y > expected_endY))) {
                                        fill_mainlayout.setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                                        if (isAssessmentQuestion) {
                                            isAnsFound = true;
                                            totalAttempt++;
                                            v.setX((int) emptyViews.get(idx).getView().getX());
                                            v.setY((int) emptyViews.get(idx).getView().getY());
                                            v.setOnTouchListener(null);
                                            emptyViews.get(idx).setView(null);
                                            fill_main_scrollview.setEnableScrolling(true);
                                            movin_view = null;
                                            if (((questions.getFillAnswers().get(idx)).trim()).equalsIgnoreCase((answerStrs.get(id)).trim())) {
                                                totalFillAssessmentCorrect++;
                                            } else {
                                                totalFillAssessmentWrong++;
                                            }
                                            if (totalAttempt == questions.getFillAnswers().size()) {
                                                boolean isQuesRight = false;
                                                if (totalFillAssessmentCorrect == questions.getFillAnswers().size()) {
                                                    isQuesRight = true;
                                                }
                                                float rightWeight = (float) questionXp / (float) questions.getFillAnswers().size();
                                                float weight = (float) questionXp / (float) (questions.getFillAnswers().size() * 2);
                                                final float totalOc = (rightWeight * totalFillAssessmentCorrect) - (weight * totalFillAssessmentWrong);
                                                Handler handler = new Handler();
                                                final boolean finalIsQuesRight = isQuesRight;
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalScr = Math.round(totalOc);
                                                        if (finalScr < 0)
                                                            finalScr = 0;
                                                        if (finalScr > questionXp)
                                                            finalScr = questionXp;
                                                        answerSubmit("", Math.round(totalOc), false, finalIsQuesRight);
//                                                    learningModuleInterface.setAnswerAndOc("", "", (int) totalOc, finalIsQuesRight);
//                                                    learningModuleInterface.gotoNextScreen();
                                                    }
                                                }, TWO_HUNDRED_MILLI_SECONDS);
                                            }
                                        } else {
                                            if ((questions.getFillAnswers().get(idx).trim()).equalsIgnoreCase((answerStrs.get(id)).trim())) {
                                                isAnsFound = true;
                                                v.setX((int) emptyViews.get(idx).getView().getX());
                                                v.setY((int) emptyViews.get(idx).getView().getY());
                                                emptyViews.get(idx).setView(null);
                                                calculateXp(false);
                                                fill_main_scrollview.setEnableScrolling(true);
                                                movin_view = null;
                                                break;
                                            }
                                        }
                                    } else {
                                        fill_mainlayout.setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                                    }
                                }
                            }

                            if (!isAnsFound) {
                                for (int idx = 0; idx < emptyViews.size(); idx++) {
                                    if (emptyViews.get(idx).getView() != null) {
                                        emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout).setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                                    }
                                }
                                fill_main_scrollview.setEnableScrolling(true);
                                movin_view = null;
                                vibrateandShake(v);
                                v.setX((int) (StartOrgPT.x));
                                v.setY((int) (StartOrgPT.y));
                                attemptWrongCount++;
                            }
                            fill_main_scrollview.setEnableScrolling(true);
                            movin_view = null;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            v.setX((int) (StartOrgPT.x));
                            v.setY((int) (StartOrgPT.y));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculateXp(boolean timeOut) {
        try {
            if (!timeOut) {
                totalAttempt++;
                float challengerScore = 0f;
                if (mainCourseCardClass != null) {
                    challengerScore = (float) mainCourseCardClass.getXp();
                }
                if (0 == attemptWrongCount) {
                    finalScr = (int) mainCourseCardClass.getXp();
                } else {
                    finalScr = Math.round(challengerScore - (attemptWrongCount * 10));
                }
                if (finalScr < 0) {
                    finalScr = 0;
                }

                if (totalAttempt == questions.getFillAnswers().size()) {
                    complete = true;
                    changeBackground(R.drawable.fill_shadow_green, true);
                    hideKeyboard(previousView);
                    answerSubmit(questions.getAnswer(), finalScr, timeOut, true);
                }

            } else if (timeOut) {
                if (movin_view != null) {
                    vibrateandShake(movin_view);
                    movin_view.setX(StartOrgPT.x);
                    movin_view.setY(StartOrgPT.y);
                    for (int idx = 0; idx < emptyViews.size(); idx++) {
                        if (emptyViews.get(idx).getView() != null) {
                            emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout).setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                        }
                    }
                }
                totalOption = questions.getFillAnswers().size();
                complete = true;
                fill_main_scrollview.setEnableScrolling(true);

                float weight = (float) mainCourseCardClass.getXp() / (totalOption);
                float rightVal = weight * totalAttempt;
                finalScr = Math.round(rightVal - (10 * attemptWrongCount));
                if (finalScr < 0) {
                    finalScr = 0;
                }
                answerSubmit(questions.getAnswer(), finalScr, timeOut, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //    fill the blank type 1 starts here
    public void onEmptyViewTouch(View view) {
        try {
            if (!complete) {
//                        int id = view.getId();
//                        int index = id - 100;
//                        if (!isAssessmentQuestion && previousFillIndex != index && previousView != null) {
//                            EditText editText = (EditText) view;
//                            if (editText != null && editText.getText() != null && !editText.getText().toString().trim().isEmpty()
//                                    && (editText.getText().toString().trim().equalsIgnoreCase(questions.getFillAnswers().get(index - 1)))){
//                                        previousView.setFocusable(true);
//                                        previousView.setFocusableInTouchMode(true);
//                                        previousView.requestFocus();
//                                        break;
//                            }
//                        }

//                        if (previousFillIndex > 0 && (previousFillIndex != index) && previousView != null) {
//                            if (previousView.getText() != null && !previousView.getText().toString().trim().isEmpty() &&
//                                    ((previousView.getText().toString().trim()).equalsIgnoreCase(questions.getFillAnswers().get(previousFillIndex - 1).trim()))) {
//                                previousView.setText(questions.getFillAnswers().get(previousFillIndex - 1).trim());
//                                calculateXp(false);
//                                changeBackground(R.drawable.fill_shadow_green, true);
//                            } else {
                if (previousView.getText() == null || (previousView.getText() != null && previousView.getText().toString().trim().isEmpty())) {
                    changeBackground(R.drawable.fill_shadow_darkgrey, true);
                } else {
//                                        previousView.setText(questions.getFillAnswers().get(previousFillIndex - 1).trim());
//                                        calculateXp(false);
//                                        previousView.setFocusable(true);
//                                        previousView.setFocusableInTouchMode(true);
//                                        previousView.requestFocus();
//                                        vibrateandShake(previousView);
                    changeBackground(R.drawable.fill_shadow_darkgrey, false);
//                                    attemptWrongCount++;
                }
                previousView.setFocusable(false);
                previousView.setFocusableInTouchMode(false);

                previousFillIndex = view.getId();
                previousFillIndex = previousFillIndex - 100;

                previousView = (EditText) view;
                previousView.setFocusable(true);
                previousView.setFocusableInTouchMode(true);
                if (previousView != null && previousView.getText() != null && !previousView.getText().toString().isEmpty()) {
                    previousView.setSelection(previousView.getText().length());
                }
                previousView.requestFocus();
//                        previousMotionEvent = event;

                changeBackground(R.drawable.fill_shadow_darkgrey, false);

//                        hideKeyboard(previousView);
//                        showKeyboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void checkToSubmit(boolean timeOut) {
        try {
            String answers = "";
            if (!complete) {
                boolean isComplete = true;
                for (int index = 0; index < emptyViews.size(); index++) {
                    View fillTextView = emptyViews.get(index).getView();
                    EditText editText = fillTextView.findViewById(100 + index + 1);

                    if (editText != null && editText.getText() != null && !editText.getText().toString().trim().isEmpty()) {
                        if (index == (emptyViews.size() - 1)) {
                            answers = answers + editText.getText().toString();
                        } else {
                            answers = answers + editText.getText().toString() + "#";
                        }

                        if (editText.getText().toString().trim().equalsIgnoreCase(questions.getFillAnswers().get(index).trim())) {
                            totalFillAssessmentCorrect++;
                        } else {
                            totalFillAssessmentWrong++;
                        }
                    } else {
                        if (!timeOut) {
                            isComplete = false;
                            break;
                        } else {
                            totalFillAssessmentWrong++;
                        }
                    }
                }
                if (isComplete) {
                    complete = true;
//                    hideKeyboard(previousView);
                    previousView.setFocusable(false);
                    previousView.setFocusableInTouchMode(false);
                    previousView.setCursorVisible(false);

                    if (!isAssessmentQuestion) {
                        showCorrectOptions();
                        for (int index = 0; index < emptyViews.size(); index++) {
                            View fillTextView = emptyViews.get(index).getView();
                            EditText editText = fillTextView.findViewById(100 + index + 1);
                            LinearLayout backgroundview = fillTextView.findViewById(R.id.background_view);
                            ImageView imageView = fillTextView.findViewById(R.id.close_imageview);
                            if (editText != null && editText.getText() != null && !editText.getText().toString().trim().isEmpty()) {
                                if (editText.getText().toString().trim().equalsIgnoreCase(questions.getFillAnswers().get(index).trim())) {
                                    imageView.setVisibility(View.GONE);
                                    OustSdkTools.setLayoutBackgrouda(backgroundview, R.drawable.fill_shadow_green);
                                } else {
                                    OustSdkTools.setLayoutBackgrouda(backgroundview, R.drawable.fill_shadow_orange);
                                    imageView.setVisibility(View.GONE);
                                }
                            } else if (timeOut) {
                                OustSdkTools.setLayoutBackgrouda(backgroundview, R.drawable.fill_shadow_orange);
                                imageView.setVisibility(View.GONE);
                            }
                        }
                        float challengerScore = 0f;
                        if (mainCourseCardClass != null) {
                            challengerScore = (float) mainCourseCardClass.getXp();
                        }
                        float rightWeight = challengerScore / (float) questions.getFillAnswers().size();
                        final float totalXP = rightWeight * totalFillAssessmentCorrect;
                        finalScr = Math.round(totalXP);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > challengerScore)
                            finalScr = (int) challengerScore;
                        answerSubmit(questions.getAnswer(), finalScr, false, true);
                    } else {
                        float rightWeight = (float) questionXp / (float) questions.getFillAnswers().size();
                        final float totalXP = rightWeight * totalFillAssessmentCorrect;

                        boolean isCorrect = false;
                        if (totalFillAssessmentCorrect == questions.getFillAnswers().size()) {
                            isCorrect = true;
                        }

                        finalScr = Math.round(totalXP);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > questionXp)
                            finalScr = questionXp;

                        if ((!isCorrect || timeOut) && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                            finalScr = 0;
                            answerSubmit(answers, 0, false, false);
                        } else {
                            answerSubmit(answers, finalScr, false, isCorrect);
                        }
                    }

                } else {
                    totalFillAssessmentCorrect = 0;
                    totalFillAssessmentWrong = 0;
                    vibrateandShake(previousView);
                    OustSdkTools.showToast("Please Compelete All Blanks");
                }
            } else {
                if (!isAssessmentQuestion) {
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null)
                            learningModuleInterface.closeChildFragment();
                    } else {
                        if (learningModuleInterface != null)
                            learningModuleInterface.gotoNextScreen();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showCorrectOptions() {
        try {
            correct_option_main_layout.setVisibility(View.VISIBLE);
            wrong_right_text.setText(totalFillAssessmentCorrect + "  " + getActivity().getResources().getString(R.string.correct_text) + totalFillAssessmentWrong + " " + getActivity().getResources().getString(R.string.wrong));

            View overlay = mInflater.inflate(R.layout.black_overlay, null);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fill_blanks_layout.getLayoutParams();
//            overlay.setX(fill_blanks_layout.getPivotX());
//            overlay.setY(fill_blanks_layout.getY());
//            params.width = scrWidth;
//            params1.height = fill_blanks_layout.getHeight() + (int) getResources().getDimension(R.dimen.oustlayout_dimen50);;
            params.setMargins(0, 0, 0, 0);
            overlay.setLayoutParams(params);
            fill_blanks_layout.addView(overlay);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addCorrectViewsInBottom() {
        try {
            String dummyStr = " ";
            for (int j = 0; j < maxlength; j++) {
                dummyStr += "_";
            }
            for (int j = 0; j < questions.getFillAnswers().size(); j++) {
                View fillTextView = mInflater.inflate(R.layout.fill_textanswer_layout, null);
                RelativeLayout fill_answerback = fillTextView.findViewById(R.id.fill_answerback);
                OustSdkTools.setLayoutBackgroud(fill_answerback, R.drawable.fill_shadow_green);
                TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                textView1.setTextColor(getResources().getColor(R.color.white_pressed));
                TextView index_text = fillTextView.findViewById(R.id.index_text);
                index_text.setText("" + j);
                textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                textView1.setText(questions.getFillAnswers().get(j));
                TextView dummytext = fillTextView.findViewById(R.id.dummytext);
                dummytext.setText(dummyStr);
                OustFillData oustFillData = new OustFillData();
                oustFillData.setView(fillTextView);
                oustFillData.setIndex(j);
                answerView.add(oustFillData);
            }
            int x = (int) (int) ((float) (0.005 * scrWidth));
            int y = (int) getResources().getDimension(R.dimen.oustlayout_dimen5);

            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int totalW = metrics.widthPixels - (int) getResources().getDimension(R.dimen.oustlayout_dimen15);

            for (int i = 0; i < answerView.size(); i++) {
                View view = answerView.get(i).getView();
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = (int) ((float) (0.005 * scrWidth));
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(x);
                    view.setY(y);
                }
                x += viewW + ((int) ((float) (0.01 * scrWidth)));
                correct_option_layout.addView(view);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) correct_option_layout.getLayoutParams();
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
            layoutParams.height = y;
            correct_option_layout.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    public void clickOnSubmit(){
//        if (previousView.getText() != null && !previousView.getText().toString().isEmpty() &&
//                ((previousView.getText().toString().trim()).equalsIgnoreCase(questions.getFillAnswers().get(previousFillIndex-1).trim()))){
//            previousView.setText(questions.getFillAnswers().get(previousFillIndex-1).trim());
//            calculateXp(false);
//            changeBackground(R.drawable.fill_shadow_green, true);
//            selectEditText();
//        } else {
//            if (previousView.getText() == null || (previousView.getText() != null && previousView.getText().toString().trim().isEmpty())) {
//                changeBackground(R.drawable.fill_shadow_darkgrey, true);
//            } else {
//                if (wrongCountMap.get("" + previousFillIndex) <= 0) {
//                    previousView.setText(questions.getFillAnswers().get(previousFillIndex - 1).trim());
//                    changeBackground(R.drawable.fill_shadow_green, true);
//                    calculateXp(false);
//                } else {
//                    previousView.setFocusable(true);
//                    previousView.setFocusableInTouchMode(true);
//                    previousView.requestFocus();
//                    changeBackground(R.drawable.fill_shadow_orange, false);
//                    vibrateandShake(previousView);
//                }
//                wrongCountMap.put("" + previousFillIndex, ((wrongCountMap.get("" + previousFillIndex)) - 1));
//                attemptWrongCount++;
//            }
//            selectEditText();
//        }
//    }

    public void selectEditText() {
        try {
            if (previousFillIndex == 0) {
                View fillTextView = emptyViews.get(previousFillIndex).getView();
                previousView = fillTextView.findViewById(100 + previousFillIndex + 1);
                previousFillIndex = 1;
                onCloseViewTouch(previousView);
                changeBackground(R.drawable.fill_shadow_darkgrey, false);
            } else {
//            for(int index=0; index<emptyViews.size(); index++){
//                View fillTextView = emptyViews.get(index).getView();
//                EditText editText= (EditText) fillTextView.findViewById(100+index+1);
//                if((editText!=null && editText.getText()!=null && !editText.getText().toString().trim().isEmpty()
//                        && (editText.getText().toString().trim().equalsIgnoreCase(questions.getFillAnswers().get(index).trim())))
//                        || (index+1==previousFillIndex)) {
//
//                }else {
                if (previousView.getText() == null || (previousView.getText() != null && previousView.getText().toString().trim().isEmpty())) {
                    changeBackground(R.drawable.fill_shadow_darkgrey, true);
                }
                previousView.setFocusable(false);
                previousView.setFocusableInTouchMode(false);

                previousFillIndex = previousFillIndex + 1;
                if (previousFillIndex > emptyViews.size()) {
                    previousFillIndex = 1;
                }
                View fillTextView = emptyViews.get(previousFillIndex - 1).getView();
                EditText editText = fillTextView.findViewById(100 + previousFillIndex);
                previousView = editText;
                onCloseViewTouch(previousView);
                changeBackground(R.drawable.fill_shadow_darkgrey, false);
//            hideKeyboard(previousView);
//            showKeyboard();
//                    break;
//                }
//        }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void onCloseViewTouch(View view) {
        try {
            if (!complete) {
                int id = view.getId();
                if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                    resetTheViewBack(id - 50);
                } else {
                    if (id < 100) {
                        previousView.setFocusable(false);
                        previousView.setFocusableInTouchMode(false);
                        if (previousView.getText() == null || (previousView.getText() != null && previousView.getText().toString().trim().isEmpty())) {
                            changeBackground(R.drawable.fill_shadow_darkgrey, true);
                        }

                        int index = id - 50;
                        previousFillIndex = index;
                        previousView = emptyViews.get(index - 1).getView().findViewById(100 + index);
                        previousView.setText("");
                        changeBackground(R.drawable.fill_shadow_darkgrey, false);
                    }
                    if (previousView != null) {
                        if (previousView.getText() != null && !previousView.getText().toString().trim().isEmpty()) {
                            previousView.setSelection(previousView.getText().length());
                        }
                        previousView.setFocusable(true);
                        previousView.setFocusableInTouchMode(true);
                        previousView.requestFocus();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void changeBackground(int id, boolean hideclosebutton) {
        try {
            View fillTextView;
            if (previousFillIndex == 0) {
                fillTextView = emptyViews.get(previousFillIndex).getView();
            } else fillTextView = emptyViews.get(previousFillIndex - 1).getView();
            LinearLayout backgroundview = fillTextView.findViewById(R.id.background_view);
            ImageView imageView = fillTextView.findViewById(R.id.close_imageview);
            if (!hideclosebutton) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
            OustSdkTools.setLayoutBackgrouda(backgroundview, id);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    fill the blank word Bank Logic

    public void onEmptyBoxClick(View view) {
        try {
            int id = view.getId();
            if (!emptyViews.get(id - 101).isFilled()) {
                if (!emptyViews.get(selectedBoxIndex - 1).isFilled()) {
                    View fillTextView = emptyViews.get(selectedBoxIndex - 1).getView();
                    LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                    OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_shadow_darkgrey);
                }
                selectedBoxIndex = id - 100;
                View fillTextView = emptyViews.get(selectedBoxIndex - 1).getView();
                LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_answer_selectedbox);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int selectedBoxIndex = 0;
    private boolean onBotomClick = false;

    public void onBottomViewClick(View view) {
        try {
            if (!onBotomClick) {
                onBotomClick = true;
                int id = view.getId();
                if (selectedBoxIndex > 0 && !emptyViews.get(selectedBoxIndex - 1).isFilled() && !answerView.get(id - 1000).isFilled()) {
                    if ((answerStrs.get(id - 1000).equalsIgnoreCase(questions.getFillAnswers().get(selectedBoxIndex - 1))) || (isAssessmentQuestion)) {
                        View fillBottomBox = answerView.get(id - 1000).getView();
                        RelativeLayout fill_answerback = fillBottomBox.findViewById(R.id.fill_answerback);
                        fill_answerback.setVisibility(View.GONE);
                        answerView.get(id - 1000).setFilled(true);

                        rearrangeAllViews(id - 1000);

                        View fillTextView = emptyViews.get(selectedBoxIndex - 1).getView();
                        TextView fill_text = fillTextView.findViewById(R.id.fill_text);
                        fill_text.setText(answerStrs.get(id - 1000));
                        fill_text.setVisibility(View.VISIBLE);
                        LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                        OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_shadow_green);
                        if (isAssessmentQuestion) {
                            ImageView close_imageview = fillTextView.findViewById(R.id.close_imageview);
                            close_imageview.setVisibility(View.VISIBLE);
                        }
                        totalFillAssessmentCorrect++;

                        emptyViews.get(selectedBoxIndex - 1).setFilled(true);
                        emptyViews.get(selectedBoxIndex - 1).setMappedBottomViewIndex(id - 1000);

                        selectNextBox();
                    } else {
                        onBotomClick = false;
                        vibrateandShake(fill_blanks_layout);
                        totalFillAssessmentWrong++;
                    }
                } else {
                    onBotomClick = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rearrangeAllViews(int id) {
        try {
            int currentNo = 0;
            for (int i = 0; i < answerView.size(); i++) {
                if (!answerView.get(i).isFilled()) {
                    View fillBottomNextBox = answerView.get(i).getView();
                    fillBottomNextBox.setX(fillAnswersPoint.get(currentNo).x);
                    fillBottomNextBox.setY(fillAnswersPoint.get(currentNo).y);
                    currentNo++;
                }
            }
            onBotomClick = false;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void selectNextBox() {
        try {
            if (selectedBoxIndex == 0) {
                selectedBoxIndex = 1;
                View fillTextView = emptyViews.get(selectedBoxIndex - 1).getView();
                LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_answer_selectedbox);
            } else {
                boolean emptyBoxStillLeft = false;
                for (int index = 0; index < emptyViews.size(); index++) {
                    if (!emptyViews.get(index).isFilled()) {
                        emptyBoxStillLeft = true;
                        selectedBoxIndex = index + 1;
                        View fillTextView = emptyViews.get(selectedBoxIndex - 1).getView();
                        LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                        OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_answer_selectedbox);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void resetTheViewBack(int id) {
        try {
            int index = id - 1;
            if (emptyViews.get(index).isFilled()) {

                int indexOfBottomView = emptyViews.get(index).getMappedBottomViewIndex();

                emptyViews.get(index).setFilled(false);
                answerView.get(indexOfBottomView).setFilled(false);

                rearrangeAllViews(indexOfBottomView);

                View fillBottomBox = answerView.get(indexOfBottomView).getView();
                RelativeLayout fill_answerback = fillBottomBox.findViewById(R.id.fill_answerback);
                fill_answerback.setVisibility(View.VISIBLE);

                View fillTextView = emptyViews.get(index).getView();
                TextView fill_text = fillTextView.findViewById(R.id.fill_text);
                fill_text.setText("");
                fill_text.setVisibility(View.VISIBLE);
                LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_shadow_darkgrey);
                onEmptyBoxClick(fillTextView);

                if (isAssessmentQuestion) {
                    ImageView close_imageview = fillTextView.findViewById(R.id.close_imageview);
                    close_imageview.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void checkToSubmitWordBank(boolean timeOut) {
        try {
            if (!complete) {
                if (timeOut || totalFillAssessmentCorrect == questions.getFillAnswers().size() || (isAssessmentQuestion)) {
                    complete = true;
                    if (!isAssessmentQuestion) {
                        float challengerScore = 0f;
                        if (mainCourseCardClass != null) {
                            challengerScore = (float) mainCourseCardClass.getXp();
                        }
                        float rightWeight = challengerScore / (float) questions.getFillAnswers().size();
                        float wrongWeight = rightWeight / (float) answerStrs.size();
                        final float totalXP = (rightWeight * totalFillAssessmentCorrect) - (totalFillAssessmentWrong * wrongWeight);
                        finalScr = Math.round(totalXP);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > challengerScore)
                            finalScr = (int) challengerScore;
                        answerSubmit(questions.getAnswer(), finalScr, false, true);
                    } else {
                        totalFillAssessmentCorrect = 0;
                        totalFillAssessmentWrong = 0;

                        for (int i = 0; i < emptyViews.size(); i++) {
                            View fillTextView = emptyViews.get(i).getView();
                            TextView fill_text = fillTextView.findViewById(R.id.fill_text);
                            if (fill_text.getText() != null && !fill_text.getText().toString().isEmpty()) {
                                if (fill_text.getText().toString().equalsIgnoreCase(questions.getFillAnswers().get(i))) {
                                    totalFillAssessmentCorrect++;
                                } else {
                                    totalFillAssessmentWrong++;
                                }
                            }
                        }

                        float rightWeight = (float) questionXp / (float) questions.getFillAnswers().size();
                        float weight = (float) questionXp / (float) (questions.getFillAnswers().size() * 2);
                        final float totalXP = (rightWeight * totalFillAssessmentCorrect) - (weight * totalFillAssessmentWrong);
                        finalScr = Math.round(totalXP);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > questionXp)
                            finalScr = questionXp;

                        boolean isCorrect = false;
                        if (totalFillAssessmentCorrect == questions.getFillAnswers().size()) {
                            isCorrect = true;
                        }
                        if ((!isCorrect || timeOut) && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                            finalScr = 0;
                            answerSubmit(questions.getAnswer(), 0, false, false);
                        } else {
                            answerSubmit(questions.getAnswer(), finalScr, false, isCorrect);
                        }
                    }
                } else {
                    vibrateandShake(fill_blanks_layout);
                    OustSdkTools.showToast("Please Compelete All Blanks");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fillAllLeftViews() {
        try {
            for (int i = 0; i < emptyViews.size(); i++) {
                if (!emptyViews.get(i).isFilled()) {
                    View fillTextView = emptyViews.get(i).getView();
                    TextView fill_text = fillTextView.findViewById(R.id.fill_text);
                    fill_text.setText(questions.getFillAnswers().get(i));
                    fill_text.setVisibility(View.VISIBLE);
                    LinearLayout background_view = fillTextView.findViewById(R.id.background_view);
                    OustSdkTools.setLayoutBackgroud(background_view, R.drawable.fill_answer_selectedbox);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


//    =============================================================================================

    public void rightwrongFlipAnimation(boolean status) {
        try {
            cancleTimer();

            if (!zeroXpForQCard) {
                if (finalScr == 0) {
                    OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                }
            } else {
                ocanim_view.setVisibility(View.GONE);
            }
            if (status) {
                rightAnswerSound();
            } else {
                if (isVideoOverlay()) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.setVideoOverlayAnswerAndOc("", "", 0, false, 0, cardId);
                } else {
                    learningModuleInterface.setAnswerAndOc("", "", 0, false, 0);
                }
            }
            learningquiz_animviewb.setVisibility(View.VISIBLE);
            learningquiz_animviewb.bringToFront();
            learningquiz_solutionlayout.setPivotX((scrWidth / 2));
            if (animoc_layout.getVisibility() == View.VISIBLE) {
                showAllMedia();
                if (!isThumbsShown) {
                    animoc_layout.setVisibility(View.GONE);
                }
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_rightwrongimage, "scaleX", 0.0f, 1);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_rightwrongimage, "scaleY", 0.0f, 1);
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
                            showSolutionWithAnimation(true);
                        } else {
                            animateOcCoins();// animate coins
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                }

                ObjectAnimator transAnim = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", 0, 1800);
                transAnim.setDuration(50);
                transAnim.start();
            } else {
                showSolution(0, learningquiz_solutionlayout.getHeight());
            }

            if (!isValidLbResetPeriod()) {
                ocanim_view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void cancleTimer() {
        try {
            if (null != timerWatch)
                timerWatch.stop();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MediaPlayer mediaPlayer;

    private void wrongAnswerSound() {
        try {
            playAudio("answer_incorrect.mp3");
        } catch (Exception e) {
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

    public void cancelSound() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private long answeredSeconds;

    @Override
    public void onDialogClose() {
        if (learningModuleInterface != null) {
            learningModuleInterface.changeOrientationPortrait();
        }
    }

    public void fillType1Timeout() {
        hideKeyboard(previousView);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkToSubmit(true);
            }
        }, ONE_HUNDRED_MILLI_SECONDS);
    }

    private StopWatch timerWatch;

    private StopWatch.StopwatchCallback stopwatchCallback = new StopWatch.StopwatchCallback() {
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            /*String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));*/

            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            learningquiz_timertext.setText(hms);
            if (OustStaticVariableHandling.getInstance().getLearningquiz_timertext() != null && (OustStaticVariableHandling.getInstance().getLearningquiz_timertext().getVisibility() == View.VISIBLE)) {
                OustStaticVariableHandling.getInstance().getLearningquiz_timertext().setText(hms);
            }
        }

        @Override
        public void onFinish() {
            hotspotcomplete = true;
            if (OustStaticVariableHandling.getInstance().getLearningquiz_timertext() != null && (OustStaticVariableHandling.getInstance().getLearningquiz_timertext().getVisibility() == View.VISIBLE)) {
                OustStaticVariableHandling.getInstance().getLearningquiz_timertext().setText("00:00");
            }
            if ((OustSdkTools.zoomImagePopup != null) && (OustSdkTools.zoomImagePopup.isShowing())) {
                OustSdkTools.zoomImagePopup.dismiss();
            }
            if (learningModuleInterface != null) {
                learningModuleInterface.closeCourseInfoPopup();
            }
            if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
                optionSelected(null, -1, true);
            } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.CATEGORY))) {
                calculatepoints(true); // praveen calculate points
            } else if ((questionType != null) && (questionType.equals(QuestionType.FILL))) {
                calculateXp(true);
            } else if ((questionType != null) && (questionType.equals(QuestionType.FILL_TYPE1))) {
                fillType1Timeout();
            } else if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                fillAllLeftViews();
                checkToSubmitWordBank(true);
            } else if ((questionType != null) && (questionType.equals(QuestionCategory.HOTSPOT))) {
                hotspot_img.setEnabled(false);
                if (!shouldDisplayRightWrong) {
                    calculateHotspotPointsForMCQ(true);
                } else {
                    calculateHotspotPoints(true);
                }
//                OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
//                rightwrongFlipAnimation(false);
            } else {
                OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                //learningquiz_rightwrongimage.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.thumbsdown));
                rightwrongFlipAnimation(false);
            }
        }
    };


    public void startTimer() {
        try {
            if (isVideoOverlay()) {
                Log.d(TAG, "startTimer: Timer not started");
                return;
            }
            if (OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] == 0) {
                if (!isAssessmentQuestion) {
                    timerWatch = new StopWatch(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay), stopwatchCallback);
                    timerWatch.start();
                }
            } else {
                setPreviousState();
            }
        } catch (Exception e) {
        }
    }

    public void setPreviousState() {
        try {
            int min = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] / 60;
            int sec = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] % 60;
            learningquiz_timertext.setText("" + min + ":" + sec);
        } catch (Exception e) {
        }
    }

    private void animateOcCoins() {
        try {
            Log.d(TAG, "animateOcCoins: ");
            if (learningquiz_solutionlayout.getVisibility() == View.GONE) {
                if (finalScr < 1) {
                    OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                } else {
                    playAudio("coins.mp3");
                }
                ValueAnimator animator1 = new ValueAnimator();
                if (isCourseCompleted) {
                    lpocimage.setVisibility(View.GONE);
                    ocanim_text.setVisibility(View.GONE);
                }

                animator1.setObjectValues(0, (finalScr));

                animator1.setDuration(600);
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {

                        if (isCourseCompleted) {
                            lpocimage.setVisibility(View.GONE);
                            ocanim_text.setVisibility(View.GONE);
                        } else {
                            ocanim_text.setText("" + (((int) animation.getAnimatedValue())));
                            lpocimage.setVisibility(View.VISIBLE);
                            ocanim_text.setVisibility(View.VISIBLE);
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
                        if (isCourseCompleted) {
                            lpocimage.setVisibility(View.GONE);
                            ocanim_text.setVisibility(View.GONE);
                        } else {
                            ocanim_text.setText("" + finalScr);
                            lpocimage.setVisibility(View.VISIBLE);
                            ocanim_text.setVisibility(View.VISIBLE);
                        }
                        showSolutionWithAnimation(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playAudio(final String filename) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                    questionaudio_btn.setAnimation(null);
                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                    cancelSound();
                    mediaPlayer = new MediaPlayer();
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

    private void showSolutionWithAnimation(final boolean status) {
        try {
            Log.d(TAG, "showSolutionWithAnimation: ");
            if (mainCourseCardClass != null)
                if (((mainCourseCardClass.getChildCard() != null) && (mainCourseCardClass.getChildCard().getContent() != null) && (!mainCourseCardClass.getChildCard().getContent().isEmpty())) ||
                        ((userSubjectiveAns != null) && (!userSubjectiveAns.isEmpty()))) {

                    if (learningquiz_solutionlayout.getVisibility() == View.GONE) {
                        if (learningquiz_solutionlayout.getVisibility() == View.GONE) {
                            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                            anim.setStartOffset(200);
                            animoc_layout.startAnimation(anim);
                            showSolution(FIVE_HUNDRED_MILLI_SECONDS, 1800);
                            getView().setFocusableInTouchMode(true);
                            getView().requestFocus();
                            getView().setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                                        if (learningquiz_animviewb.getVisibility() == View.VISIBLE) {
                                            hideSolutionView();
                                            return true;
                                        }
                                        return false;
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_TYPE1)) {
                                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                                anim.setStartOffset(TWO_HUNDRED_MILLI_SECONDS);
                                animoc_layout.startAnimation(anim);
                            } else {
                                Log.d(TAG, "run: closing learningplayfragmentnew :" + isVideoOverlay());
                                if (isVideoOverlay()) {
                                    if (learningModuleInterface != null)
                                        learningModuleInterface.closeChildFragment();
                                } else {
                                    if (learningModuleInterface != null)
                                        learningModuleInterface.gotoNextScreen();
                                }
                            }
                        }
                    }, FIVE_HUNDRED_MILLI_SECONDS);
                }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private boolean isWrongAnswer = true;

    private void showSolution(int delay, int startPoint) {
        try {
            fill_submit_layout.setVisibility(View.GONE);

            learningModuleInterface.dismissCardInfo();
            if (isThirdFourthHotSpotImage) {
                bottomswipe_view.setVisibility(View.VISIBLE);
            }
            Log.e("TAG", "getQuestions: show Soluction -|||-> " + questions.isShowSolution());

            if (questions.isShowSolution() /*|| courseCardClass.isSolutionShownOnlyForWrong() && isWrongAnswer */) {
                learningquiz_solutionlayout.setVisibility(View.VISIBLE);
            } else {

                if (isVideoOverlay())
                    learningModuleInterface.closeChildFragment();
                else
                    learningModuleInterface.gotoNextScreen();
            }
            learningquiz_solutionlayout.setPivotY(learningquiz_solutionlayout.getHeight());
            learningquiz_solutionlayout.setPivotX((scrWidth / 2));
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", startPoint, 0);
            scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "scaleX", 0.0f, 1.0f);
            scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay(delay);
            scaleDown.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideSolutionView() {
        try {
            learningquiz_solutionlayout.setPivotX(learningquiz_solutionlayout.getWidth() / 2);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", 0, learningquiz_solutionlayout.getHeight());
            scaleDownY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            showsolution_img.setAnimation(null);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "scaleX", 1.0f, 0.0f);
            scaleDownX.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    animoc_layout.setVisibility(View.GONE);
                    learningquiz_animviewb.setVisibility(View.GONE);
                    learningquiz_solutionlayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            showJumpAnimOnSolutionImage();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showJumpAnimOnSolutionImage() {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(showsolution_img, "translationY", 0.0f, 10.0f);
            scaleDownX.setDuration(1300);
            scaleDownX.setRepeatCount(ValueAnimator.REVERSE);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setInterpolator(new BounceInterpolator());
            scaleDownX.start();
            //showsolution_img
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private DTOCourseSolutionCard courseCardClass;
    private boolean IsImageSolution;

    private void showAllMedia() {
        try {
            courseCardClass = mainCourseCardClass.getChildCard();
            if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                if (mainCourseCardClass.getReadMoreData() != null && mainCourseCardClass.getReadMoreData().getDisplayText() != null) {
                    solution_desc.setHtml(courseCardClass.getContent());
                    solution_readmore_text.setText(mainCourseCardClass.getReadMoreData().getDisplayText());
                    solution_readmore.setVisibility(View.VISIBLE);

                    solution_readmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (OustSdkTools.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);
                            } else {
                                openReadMore();
                            }
                        }
                    });

                } else {
                    solution_desc.setHtml(courseCardClass.getContent());
                }
                if (courseCardClass != null) {
                    if (courseCardClass.getSolutionType().equalsIgnoreCase("TEXT")) {
                        IsImageSolution = false;
                    } else if (courseCardClass.getSolutionType().equalsIgnoreCase("IMAGE")) {
                        IsImageSolution = true;
                        String imageURL = CLOUD_FRONT_BASE_PATH + courseCardClass.getContent();
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(imageURL);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (file != null && file.exists()) {
                            Uri uri = Uri.fromFile(file);
                            mImageViewSolution.setImageURI(uri);
                        } else {
                            Picasso.get().load(imageURL).into(mImageViewSolution);
                        }
                    }
                    solution_label.setVisibility(View.VISIBLE);
                    if (IsImageSolution) {
                        mImageViewSolution.setVisibility(View.VISIBLE);
                        solution_desc.setVisibility(View.GONE);
                    } else {
                        mImageViewSolution.setVisibility(View.GONE);
                        solution_desc.setVisibility(View.VISIBLE);
                    }
                }
               /* else if(courseCardClass!=null)
                {
                    if(courseCardClass.getContent().contains("http")) {
                        IsImageSolution = true;
                        Picasso.with(getContext()).load(questions.getSolution()).into(mImageViewSolution);
                    }
                    else {
                        IsImageSolution = false;
                    }
                    solution_label.setVisibility(View.VISIBLE);
                    if(IsImageSolution)
                    {
                        mImageViewSolution.setVisibility(View.VISIBLE);
                        solution_desc.setVisibility(View.GONE);
                    }
                    else {
                        mImageViewSolution.setVisibility(View.GONE);
                        solution_desc.setVisibility(View.VISIBLE);
                    }
                }*/
            }
            if ((userSubjectiveAns != null) && (!userSubjectiveAns.isEmpty())) {
                myresponse_desc.setVisibility(View.VISIBLE);
                myresponse_desc.setText(userSubjectiveAns);
                myresponse_label.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void openReadMore() {
        if (mainCourseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf")) {
            learningModuleInterface.setShareClicked(true);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            int sdk = Build.VERSION.SDK_INT;
            if (sdk > Build.VERSION_CODES.M) {
                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".provider", OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData()));
                intent.setDataAndType(fileUri, "application/pdf");
            } else {
                File file = OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData());
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            }
            getActivity().startActivity(intent);
        } else {
            DTOReadMore readData = mainCourseCardClass.getReadMoreData();
            learningModuleInterface.setShareClicked(true);
            if (readData.getType().equalsIgnoreCase("IMAGE")) {
                learningModuleInterface.changeOrientationUnSpecific();
            }
            learningModuleInterface.openReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
        }
    }

    private boolean isAudioPausedFromOpenReadmore = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 140) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        OustSdkTools tools = new OustSdkTools();
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                isAudioPausedFromOpenReadmore = true;
                                questionaudio_btn.setAnimation(null);
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            }
                        }
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                            isAudioPausedFromOpenReadmore = true;
                            questionaudio_btn.setAnimation(null);
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }

                    }
                }
            } else if (requestCode == 150) {
                openReadMore();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //=============================================================================================================

    private List<String> rightChoiceIds = new ArrayList<>();
    private List<String> leftChoiceIds = new ArrayList<>();

    private void setMatchQuestionLayoutSize() {
        try {
            Log.d(TAG, "setMatchQuestionLayoutSize: ");
            totalOption = 0;
            matchfollowing_layout.setVisibility(View.VISIBLE);
            draglebel_text.setText(getResources().getString(R.string.drag_match));
            draglebel_text.setVisibility(View.VISIBLE);
            int size = (int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen13);
            int imageWidth = 0;
            if (scrWidth > scrHeight) {
                imageWidth = (scrHeight / 2) - size;
            } else
                imageWidth = (scrWidth / 2) - size;
            float h = (imageWidth * 0.57f);
            int h1 = (int) h;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) match_option_a_left_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_a_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_b_left_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_b_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_c_left_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_c_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_d_left_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_d_left_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_a_right_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_a_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_b_right_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_b_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_c_right_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_c_right_layout.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams) match_option_d_right_layout.getLayoutParams();
            params.height = h1;
            params.width = imageWidth;
            match_option_d_right_layout.setLayoutParams(params);


            List<DTOMTFColumnData> mtfLeftCol = questions.getMtfLeftCol();
            if ((mtfLeftCol != null) && (mtfLeftCol.size() > 0)) {
                if ((mtfLeftCol.size() > 0) && (mtfLeftCol.get(0) != null) && (mtfLeftCol.get(0).getMtfColData() != null)) {
                    if (mtfLeftCol.get(0).getMtfColData().equals(KATEX_DELIMITER)) {
                        setMatchOptionMaths(match_option_a_left_image, match_option_a_left_text_maths, mtfLeftCol.get(0));
                        showOptionWithAnimA(match_option_a_left_layout);
                    } else {
                        setMatchOption(match_option_a_left_image, match_option_a_left_text, mtfLeftCol.get(0));
                        showOptionWithAnimA(match_option_a_left_layout);
                    }
                    leftChoiceIds.add(mtfLeftCol.get(0).getMtfColDataId());
                    totalOption++;
                }
                if ((mtfLeftCol.size() > 1) && (mtfLeftCol.get(1) != null) && (mtfLeftCol.get(1).getMtfColData() != null)) {
                    if (mtfLeftCol.get(1).getMtfColData().equals(KATEX_DELIMITER)) {
                        setMatchOptionMaths(match_option_b_left_image, match_option_b_left_text_maths, mtfLeftCol.get(1));
                        showOptionWithAnimA(match_option_b_left_layout);
                    } else {
                        setMatchOption(match_option_b_left_image, match_option_b_left_text, mtfLeftCol.get(1));
                        showOptionWithAnimA(match_option_b_left_layout);
                    }
                    leftChoiceIds.add(mtfLeftCol.get(1).getMtfColDataId());
                    totalOption++;
                }
                if ((mtfLeftCol.size() > 2) && (mtfLeftCol.get(2) != null) && (mtfLeftCol.get(2).getMtfColData() != null)) {

                    if (mtfLeftCol.get(2).getMtfColData().equals(KATEX_DELIMITER)) {
                        setMatchOptionMaths(match_option_c_left_image, match_option_c_left_text_maths, mtfLeftCol.get(2));
                        showOptionWithAnimA(match_option_c_left_layout);
                    } else {
                        setMatchOption(match_option_c_left_image, match_option_c_left_text, mtfLeftCol.get(2));
                        showOptionWithAnimA(match_option_c_left_layout);
                    }

                    leftChoiceIds.add(mtfLeftCol.get(2).getMtfColDataId());
                    totalOption++;
                }
                if ((mtfLeftCol.size() > 3) && (mtfLeftCol.get(3) != null) && (mtfLeftCol.get(3).getMtfColData() != null)) {

                    if (mtfLeftCol.get(3).getMtfColData().equals(KATEX_DELIMITER)) {
                        setMatchOptionMaths(match_option_d_left_image, match_option_d_left_text_maths, mtfLeftCol.get(3));
                        showOptionWithAnimA(match_option_d_left_layout);
                    } else {
                        setMatchOption(match_option_d_left_image, match_option_d_left_text, mtfLeftCol.get(3));
                        showOptionWithAnimA(match_option_d_left_layout);
                    }

                    leftChoiceIds.add(mtfLeftCol.get(3).getMtfColDataId());
                    totalOption++;
                }
            }
            List<DTOMTFColumnData> mtfRightCol = questions.getMtfRightCol();
            if ((mtfRightCol != null) && (mtfRightCol.size() > 0)) {
                if ((mtfRightCol.size() > 0) && (mtfRightCol.get(0) != null) && (mtfRightCol.get(0).getMtfColData() != null)) {
                    if ((mtfRightCol.get(0).getMtfColData().contains(KATEX_DELIMITER))) {
                        setMatchOptionMaths(match_option_a_right_image, match_option_a_right_text_maths, mtfRightCol.get(0));
                        showOptionWithAnimA(match_option_a_right_layout);
                    } else {
                        setMatchOption(match_option_a_right_image, match_option_a_right_text, mtfRightCol.get(0));
                        showOptionWithAnimA(match_option_a_right_layout);
                    }

                    rightChoiceIds.add(mtfRightCol.get(0).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 1) && (mtfRightCol.get(1) != null) && (mtfRightCol.get(1).getMtfColData() != null)) {
                    if ((mtfRightCol.get(1).getMtfColData().contains(KATEX_DELIMITER))) {
                        setMatchOptionMaths(match_option_b_right_image, match_option_b_right_text_maths, mtfRightCol.get(1));
                        showOptionWithAnimA(match_option_b_right_layout);
                    } else {
                        setMatchOption(match_option_b_right_image, match_option_b_right_text, mtfRightCol.get(1));
                        showOptionWithAnimA(match_option_b_right_layout);
                    }
                    rightChoiceIds.add(mtfRightCol.get(1).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 2) && (mtfRightCol.get(2) != null) && (mtfRightCol.get(2).getMtfColData() != null)) {
                    if ((mtfRightCol.get(2).getMtfColData().contains(KATEX_DELIMITER))) {
                        setMatchOptionMaths(match_option_c_right_image, match_option_c_right_text_maths, mtfRightCol.get(2));
                        showOptionWithAnimA(match_option_c_right_layout);
                    } else {
                        setMatchOption(match_option_c_right_image, match_option_c_right_text, mtfRightCol.get(2));
                        showOptionWithAnimA(match_option_c_right_layout);
                    }

                    rightChoiceIds.add(mtfRightCol.get(2).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 3) && (mtfRightCol.get(3) != null) && (mtfRightCol.get(3).getMtfColData() != null)) {
                    if ((mtfRightCol.get(3).getMtfColData().contains(KATEX_DELIMITER))) {
                        setMatchOptionMaths(match_option_d_right_image, match_option_d_right_text_maths, mtfRightCol.get(3));
                        showOptionWithAnimA(match_option_d_right_layout);
                    } else {
                        setMatchOption(match_option_d_right_image, match_option_d_right_text, mtfRightCol.get(3));
                        showOptionWithAnimA(match_option_d_right_layout);
                    }
                    rightChoiceIds.add(mtfRightCol.get(3).getMtfColDataId());
                }
            }


//            match_option_a_left_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_b_left_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_c_left_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_d_left_text.setMovementMethod(new ScrollingMovementMethod());
//
//            match_option_a_right_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_b_right_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_c_right_text.setMovementMethod(new ScrollingMovementMethod());
//            match_option_d_right_text.setMovementMethod(new ScrollingMovementMethod());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    int animPosition = 0;

    private void showOptionWithAnimA(View view) {
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
        scaleDown.setStartDelay((animPosition * 180));
        animPosition++;
        scaleDown.start();
    }

    private void setMatchOptionMaths(ImageView imageView, KatexView textView, DTOMTFColumnData mtfColumnData) {
        try {
            Log.d(TAG, "setMatchOption: ");
            if (mtfColumnData.getMtfColMediaType() != null) {
                if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                    if (mtfColumnData.getMtfColData() != null && !mtfColumnData.getMtfColData().isEmpty()) {
                        setImageOptionFromFile(mtfColumnData.getMtfColData(), imageView);
                    } else
                        setImageOption(mtfColumnData.getMtfColData(), imageView);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                } else {
                    // OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                    textView.setText(mtfColumnData.getMtfColData());
                    imageView.setVisibility(View.GONE);
                    // textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView.setVisibility(View.VISIBLE);
                }
            } else {
                //OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                textView.setText(mtfColumnData.getMtfColData());
                // textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setMatchOption(ImageView imageView, TextView textView, DTOMTFColumnData mtfColumnData) {
        try {
            Log.d(TAG, "setMatchOption: ");
            if (mtfColumnData.getMtfColMediaType() != null) {
                if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                    if (mtfColumnData.getMtfColData() != null && !mtfColumnData.getMtfColData().isEmpty()) {
                        setImageOptionFromFile(mtfColumnData.getMtfColData(), imageView);
                    } else
                        setImageOption(mtfColumnData.getMtfColData(), imageView);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                } else {
                    OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                    imageView.setVisibility(View.GONE);
                    textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView.setVisibility(View.VISIBLE);
                }
            } else {
                OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
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
            setImageOptionA(str, imgView);
        }
    }

    public void setImageOptionFromFile(String url2, ImageView imageView) {
        try {
            String url = url2;
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH");
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                //  InputStream iStream =  OustSdkApplication.getContext().getContentResolver().openInputStream(uri);
                //  byte[] inputData = getBytes(iStream);
                GifDrawable gifFromBytes = new GifDrawable(getBytes(OustSdkApplication.getContext().getContentResolver().openInputStream(uri)));
                imageView.setImageDrawable(gifFromBytes);
            } else {
                if (OustSdkTools.checkInternetStatus())
                    Picasso.get().load(url2).into(imageView);
                else {
                    OustSdkTools.showToast(getContext().getString(R.string.no_internet_connection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            setNonGifImage(url2, imageView);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void setNonGifImage(String url2, ImageView imageView) {
        String imageURL = url2;
        String url = url2;
        Log.d(TAG, "setImageOptionUrl: " + url);
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
        }
        //OustSdkTools.showToast("IMAGE FROM CDNPATH imageoptionurl");
        if (!url.contains("oustlearn_")) {
            url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
        }
        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        if (file != null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
            if (options.outWidth != -1 && options.outHeight != -1) {
                Log.d(TAG, "setImageOptionUrl: this is proper image");
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(imageURL).into(imageView);
                    if (file.exists()) {
                        file.delete();
                    }
                    downLoad(imageURL);
                } else {
                    Picasso.get().load(uri).into(imageView);
                }
            } else {
                Log.d(TAG, "setImageOptionUrl: this is not proper image");
                Picasso.get().load(imageURL).into(imageView);
                if (file.exists()) {
                    file.delete();
                }
                downLoad(imageURL);
            }

            // imageView.setImageURI(uri);
        } else {
            if (OustSdkTools.checkInternetStatus())
                Picasso.get().load(url2).into(imageView);
            else {
                OustSdkTools.showToast(getContext().getString(R.string.no_internet_connection));
            }
        }
    }

    public void setImageOptionA(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_MOVE) {
                if (touched) {
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    v.setX((int) (StartPT.x + mv.x));
                    v.setY((int) (StartPT.y + mv.y));
                    StartPT = new PointF(v.getX(), v.getY());
                }
            } else if (action == MotionEvent.ACTION_DOWN) {
                if (v.getX() < (match_option_a_right_layout.getX() - 10)) {
                    int id = v.getId();
                    if (id == R.id.match_option_a_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(0);
                    } else if (id == R.id.match_option_b_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(1);
                    } else if (id == R.id.match_option_c_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(2);
                    } else if (id == R.id.match_option_d_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(3);
                    }

                    v.bringToFront();
                    DownPT.x = event.getX();
                    DownPT.y = event.getY();
                    StartPT = new PointF(v.getX(), v.getY());
                    StartOrgPT = new PointF(v.getX(), v.getY());
                }
            } else if (action == MotionEvent.ACTION_UP) {
                if (touched) {
                    float x2 = v.getX() + (match_option_a_right_layout.getHeight() / 2);
                    float y2 = v.getY() + (match_option_a_right_layout.getHeight() / 2);
                    if (x2 > (match_option_a_right_layout.getX())) {
                        if ((y2 > match_option_a_right_layout.getY()) && (y2 < (match_option_a_right_layout.getY() + match_option_a_right_layout.getHeight()))) {
                            optionSelected(v, 0, false);
                        } else if ((y2 > match_option_b_right_layout.getY()) && (y2 < (match_option_b_right_layout.getY() + match_option_b_right_layout.getHeight()))) {
                            optionSelected(v, 1, false);
                        } else if ((y2 > match_option_c_right_layout.getY()) && (y2 < (match_option_c_right_layout.getY() + match_option_c_right_layout.getHeight()))) {
                            optionSelected(v, 2, false);
                        } else if ((y2 > match_option_d_right_layout.getY()) && (y2 < (match_option_d_right_layout.getY() + match_option_d_right_layout.getHeight()))) {
                            optionSelected(v, 3, false);
                        } else {
                            v.setX((int) (StartOrgPT.x));
                            v.setY((int) (StartOrgPT.y));
                        }
                    } else {
                        v.setX((int) (StartOrgPT.x));
                        v.setY((int) (StartOrgPT.y));
                    }
                    touched = false;
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return true;
    }

    private float myAssessmentScore = 0;
    private boolean wrongAssessmentAnsFound = false;
    private boolean isAMtf = false, isBMtf = false, isCMtf = false, isDMtf = false;

    public void optionSelected(View v, int no, boolean timeOut) {
        try {
            if (!timeOut) {
                totalAttempt++;
                boolean isCorrentAns = false;
                String answerStr = selectedAns + "," + rightChoiceIds.get(no);
                List<String> answerList = questions.getMtfAnswer();
                for (int j = 0; j < answerList.size(); j++) {
                    if (answerList.get(j).contains(answerStr)) {
                        isCorrentAns = true;
                        if (no == 0) {
                            rightAAnswer(v);
                        } else if (no == 1) {
                            rightBAnswer(v);
                        } else if (no == 2) {
                            rightCAnswer(v);
                        } else if (no == 3) {
                            rightDAnswer(v);
                        }
                        if (!isAssessmentQuestion)
                            optionSelected++;
                        break;
                    }
                }
                if (!isCorrentAns) {
                    wrongAssessmentAnsFound = true;
                    if (no == 0) {
                        wrongAAnswer(v, match_option_a_right_layout);
                    } else if (no == 1) {
                        wrongBAnswer(v, match_option_b_right_layout);
                    } else if (no == 2) {
                        wrongCAnswer(v, match_option_c_right_layout);
                    } else if (no == 3) {
                        wrongDAnswer(v, match_option_d_right_layout);
                    }
                }
            }
            if (!isAssessmentQuestion) {
                if (optionSelected == totalOption) {
                    if (totalAttempt < (totalOption + 1)) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else {
                        int totalXp = (int) mainCourseCardClass.getXp();
                        finalScr = totalXp - ((10 * (totalAttempt - (optionSelected))));
                    }
                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    bringMatchLayouttoMiddle(timeOut);
                } else if (timeOut) {
                    float weight = (float) mainCourseCardClass.getXp() / (float) (totalOption);
                    float rightVal = weight * optionSelected;
                    finalScr = Math.round(rightVal - ((10 * (totalAttempt - (optionSelected)))));
                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    answerSubmit(questions.getAnswer(), finalScr, timeOut, true);
                }
            } else {
                if (optionSelected == totalOption) {
                    bringMatchLayouttoMiddle(timeOut);
                    calculateMatchTheColumnScores(timeOut);
                } else if (timeOut) {
                    if (optionSelected != totalOption) {
                        wrongAssessmentAnsFound = true;
                    }
                    calculateMatchTheColumnScores(timeOut);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void calculateMatchTheColumnScores(boolean timeOut) {
        Log.d(TAG, "calculateMatchTheColumnScores: ");
        finalScr = Math.round(myAssessmentScore);
        if (finalScr < 0) {
            finalScr = 0;
        }
        if (finalScr > questionXp)
            finalScr = questionXp;
        if ((wrongAssessmentAnsFound || timeOut) && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())) {
            finalScr = 0;
            answerSubmit("", 0, timeOut, false);
        } else {
            answerSubmit("", Math.round(myAssessmentScore), timeOut, !wrongAssessmentAnsFound);
        }
    }

    private void rightAAnswer(View v) {
        try {
            Log.d(TAG, "rightAAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (match_option_a_right_layout.getX()));
                v.setY((int) (match_option_a_right_layout.getY()));
            } else {
                if (!isAMtf) {
                    optionSelected++;
                    isAMtf = true;
                    v.setX((int) (match_option_a_right_layout.getX()));
                    v.setY((int) (match_option_a_right_layout.getY()));
                    float mark = (float) questionXp / (float) (questions.getMtfAnswer().size());
                    myAssessmentScore = myAssessmentScore + mark;
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                }
            }
        } catch (Exception e) {
        }
    }

    private void rightBAnswer(View v) {
        try {
            Log.d(TAG, "rightBAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (match_option_b_right_layout.getX()));
                v.setY((int) (match_option_b_right_layout.getY()));
            } else {
                if (!isBMtf) {
                    optionSelected++;
                    isBMtf = true;
                    v.setX((int) (match_option_b_right_layout.getX()));
                    v.setY((int) (match_option_b_right_layout.getY()));
                    float mark = (float) questionXp / (float) (questions.getMtfAnswer().size());
                    myAssessmentScore = myAssessmentScore + mark;
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                }
            }
        } catch (Exception e) {
        }
    }

    private void rightCAnswer(View v) {
        try {
            Log.d(TAG, "rightCAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (match_option_c_right_layout.getX()));
                v.setY((int) (match_option_c_right_layout.getY()));
            } else {
                if (!isCMtf) {
                    optionSelected++;
                    isCMtf = true;
                    v.setX((int) (match_option_c_right_layout.getX()));
                    v.setY((int) (match_option_c_right_layout.getY()));
                    float mark = (float) questionXp / (float) (questions.getMtfAnswer().size());
                    myAssessmentScore = myAssessmentScore + mark;
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                }
            }

        } catch (Exception e) {
        }
    }

    private void rightDAnswer(View v) {
        try {
            Log.d(TAG, "rightDAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (match_option_d_right_layout.getX()));
                v.setY((int) (match_option_d_right_layout.getY()));
            } else {
                if (!isDMtf) {
                    optionSelected++;
                    isDMtf = true;
                    v.setX((int) (match_option_d_right_layout.getX()));
                    v.setY((int) (match_option_d_right_layout.getY()));
                    float mark = (float) questionXp / (float) (questions.getMtfAnswer().size());
                    myAssessmentScore = myAssessmentScore + mark;
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                }
            }
        } catch (Exception e) {
        }
    }

    private void wrongAAnswer(View v, View v1) {
        try {
            Log.d(TAG, "wrongAAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (StartOrgPT.x));
                v.setY((int) (StartOrgPT.y));
                vibrateandShake(v1);
            } else {
                if (!isAMtf) {
                    isAMtf = true;
                    optionSelected++;
                    float mark = (float) questionXp / (float) ((questions.getMtfAnswer().size() * 2));
                    myAssessmentScore -= mark;
                    v.setX((int) (v1.getX()));
                    v.setY((int) (v1.getY()));
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                    vibrateandShake(v1);
                }
            }
        } catch (Exception e) {
        }
    }

    private void wrongBAnswer(View v, View v1) {
        try {
            Log.d(TAG, "wrongBAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (StartOrgPT.x));
                v.setY((int) (StartOrgPT.y));
                vibrateandShake(v1);
            } else {
                if (!isBMtf) {
                    isBMtf = true;
                    optionSelected++;
                    float mark = (float) questionXp / (float) ((questions.getMtfAnswer().size() * 2));
                    myAssessmentScore -= mark;
                    v.setX((int) (v1.getX()));
                    v.setY((int) (v1.getY()));
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                    vibrateandShake(v1);
                }
            }
        } catch (Exception e) {
        }
    }

    private void wrongCAnswer(View v, View v1) {
        try {
            Log.d(TAG, "wrongCAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (StartOrgPT.x));
                v.setY((int) (StartOrgPT.y));
                vibrateandShake(v1);
            } else {
                if (!isCMtf) {
                    isCMtf = true;
                    optionSelected++;
                    float mark = (float) questionXp / (float) ((questions.getMtfAnswer().size() * 2));
                    myAssessmentScore -= mark;
                    v.setX((int) (v1.getX()));
                    v.setY((int) (v1.getY()));
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                    vibrateandShake(v1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void wrongDAnswer(View v, View v1) {
        try {
            Log.d(TAG, "wrongDAnswer: ");
            if (!isAssessmentQuestion) {
                v.setX((int) (StartOrgPT.x));
                v.setY((int) (StartOrgPT.y));
                vibrateandShake(v1);
            } else {
                if (!isDMtf) {
                    optionSelected++;
                    float mark = (float) questionXp / (float) ((questions.getMtfAnswer().size() * 2));
                    myAssessmentScore -= mark;
                    isDMtf = true;
                    v.setX((int) (v1.getX()));
                    v.setY((int) (v1.getY()));
                } else {
                    v.setX((int) (StartOrgPT.x));
                    v.setY((int) (StartOrgPT.y));
                    vibrateandShake(v1);
                }
            }
        } catch (Exception e) {
        }
    }

    private void bringMatchLayouttoMiddle(final boolean timeOut) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(matchfollowing_layout, "x", 0, -(scrWidth / 4));
        anim1.setDuration(FOUR_HUNDRED_MILLI_SECONDS);
        anim1.setInterpolator(new AccelerateInterpolator());
        anim1.start();
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (questions != null && !isAssessmentQuestion)
                            answerSubmit(questions.getAnswer(), finalScr, timeOut, true);
                    }
                }, FOUR_HUNDRED_MILLI_SECONDS);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void vibrateandShake(View v) {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            learningquiz_mainlayout.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            if (id == R.id.gotonextscreen_btn) {
                OustSdkTools.oustTouchEffect(v, 50);
                if (isVideoOverlay()) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.closeChildFragment();
                } else {
                    if (learningModuleInterface != null)
                        learningModuleInterface.gotoNextScreen();
                }
            } else if (id == R.id.questionmore_btn) {
                if (learningquiz_solutionlayout.getVisibility() != View.VISIBLE)
                    learningModuleInterface.showCourseInfo();
            } else if (id == R.id.solution_closebtn) {
                hideSolutionView();
            } else if (id == R.id.gotopreviousscreen_mainbtn) {
                if (isVideoOverlay()) {

                } else {
                    if (learningModuleInterface != null)
                        learningModuleInterface.gotoPreviousScreen();
                }

            } else if (id == R.id.gotonextscreen_mainbtn) {
                if (isVideoOverlay()) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.closeChildFragment();
                } else {
                    if (learningModuleInterface != null)
                        learningModuleInterface.gotoNextScreen();
                }
            } else if (id == R.id.showsolution_img) {
                learningquiz_animviewb.setVisibility(View.VISIBLE);
                learningquiz_animviewb.bringToFront();
                showSolution(0, learningquiz_solutionlayout.getHeight());
            } else if (id == R.id.unfavourite) {
                if (!isfavouriteClicked) {
                    isfavouriteClicked = true;
                    unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.favourite));
                    unfavourite.setColorFilter(getResources().getColor(R.color.Orange));
                } else {
                    isfavouriteClicked = false;
                    unfavourite.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.unfavourite));
                    unfavourite.setColorFilter(getResources().getColor(R.color.whitea));
                }
            } else if (id == R.id.questionaudio_btn) {
                OustSdkTools.toucheffect(questionaudio_btn).addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        try {
                            if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                                if (musicComplete) {
                                    String audioPath = questions.getAudio();
                                    String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                    playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                } else {
                                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                                        mediaPlayer.pause();
                                        questionaudio_btn.setAnimation(null);
                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                                    } else if ((mediaPlayer != null)) {
                                        String audioPath = questions.getAudio();
                                        questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                        String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                        playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                                    }
                                }
                            } else {
                                if (isAudioPlaying && (!isAudioPausedFromOpenReadmore)) {
                                    isAudioPlaying = false;
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                                    questionaudio_btn.setAnimation(null);
                                    if (OustSdkTools.textToSpeech != null) {
                                        OustSdkTools.stopSpeech();
                                    }
                                } else {
                                    isAudioPausedFromOpenReadmore = false;
                                    isAudioPlaying = true;
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    Spanned s1 = getSpannedContent(questions.getQuestion());
                                    String quesStr = s1.toString().trim();
                                    speakString(quesStr);
//                                    createStringfor_speech();
                                }
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
            } else if (id == R.id.fill_submit_layout) {
                if (previousView != null) {
                    hideKeyboard(previousView);
                    previousView.setFocusable(false);
                    previousView.setFocusableInTouchMode(false);
                }
                clickAnimation(fill_submit_layout);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAudioPlaying = true;

    public abstract class TextViewLinkHandler extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    onLinkClick(link[0].getURL());
                    return false;
                }
            }
            return true;
        }

        abstract public void onLinkClick(String url);
    }

    @Override
    public void favouriteClicked(boolean isRMFavourite) {
        try {
            learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            FavCardDetails favCardDetails = new FavCardDetails();
            this.isRMFavourite = isRMFavourite;
            if (isRMFavourite) {
                favCardDetails.setCardId("" + mainCourseCardClass.getCardId());
                favCardDetails.setRmId(mainCourseCardClass.getReadMoreData().getRmId());
                favCardDetails.setRmData(mainCourseCardClass.getReadMoreData().getData());
                favCardDetails.setRMCard(true);
                favCardDetails.setRmDisplayText(mainCourseCardClass.getReadMoreData().getDisplayText());
                favCardDetails.setRmScope(mainCourseCardClass.getReadMoreData().getScope());
                favCardDetails.setRmType(mainCourseCardClass.getReadMoreData().getType());

                favCardDetailsList.add(favCardDetails);
                learningModuleInterface.setFavCardDetails(favCardDetailsList);

            } else {
                learningModuleInterface.setRMFavouriteStatus(isRMFavourite);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    =========================================================================
//    Category Type Question Layout

    float layout_width = 0f;
    public ImageTextAdapter arrayAdapter;
    private int i = 0;
    private List<DTOQuestionOption> optionData = new ArrayList<>();

    private void startCategoryLayout() {
        try {
            if (OustSdkTools.categoryData != null)
                OustSdkTools.categoryData.clear();
            if (OustSdkTools.optionData != null)
                OustSdkTools.optionData.clear();
            OustSdkTools.categoryData = null;
            OustSdkTools.optionData = null;
            OustSdkTools.totalAttempt = 0;
            OustSdkTools.optionSelected = 0;
            optionData = new ArrayList<>();
            category_layout.setVisibility(View.VISIBLE);
            optionData = questions.getOptions();
            List<DTOQuestionOptionCategory> categoryData = questions.getOptionCategories();
            Collections.shuffle(optionData);
            OustSdkTools.categoryData = categoryData;
            OustSdkTools.optionData = optionData;
            totalOption = optionData.size();
            category_optionsleft.setText(totalOption + " " + getActivity().getResources().getString(R.string.cards_left));

            setcategories(categoryData);

//            main logic of swiping cards are in class FlingCardListener

//            StackView stackView = new StackView(getActivity());
//            stackView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            if(flingContainer!=null){
//                flingContainer.addView(stackView);
//            }

            arrayAdapter = new ImageTextAdapter(getActivity(), optionData);
            flingContainer.setAdapter(arrayAdapter);
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // this is the simplest way to delete an object from the Adapter (/AdapterView)
                    if (optionData.size() > 0) {
                        optionData.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    if (optionData.size() == 0 && i == 0) {
                        Log.e("Category", "LearningPlayFragmentNew optionData.size(): " + optionData.size() + ": about to go inside noCardsLeft");
                        noCradsLeft();
                    }
                    Log.e("Category", "LearningPlayFragmentNew optionData.size(): " + optionData.size());
                }


                @Override
                public void onLeftCardExit(Object dataObject) {
                    //Do something on the left!
                    //You also have access to the original object.
                    //If you want to use it just cast it (String) dataObject
                    Log.e("Category", "LearningPlayFragmentNew left card exit : " + OustSdkTools.optionSelected);
                    if (optionData.size() == 1) {
                        category_optionsleft.setText("1 " + getActivity().getResources().getString(R.string.card_left));
                    } else if (optionData.size() == 0) {
                        category_optionsleft.setText("0 " + getActivity().getResources().getString(R.string.card_left));

                        if (optionData.size() == 0 && i == 0) {
                            Log.e("Category", "LearningPlayFragmentNew left card exit : about to go inside noCardsLeft");
                            noCradsLeft();
                        }

                    } else
                        category_optionsleft.setText(optionData.size() + " " + getActivity().getResources().getString(R.string.cards_left));
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    Log.e("Category", "LearningPlayFragmentNew right card exit : " + OustSdkTools.optionSelected);
                    if (optionData.size() == 1) {
                        category_optionsleft.setText("1 " + getActivity().getResources().getString(R.string.card_left));
                    } else if (optionData.size() == 0) {
                        category_optionsleft.setText("0 " + getActivity().getResources().getString(R.string.card_left));

                        if (optionData.size() == 0 && i == 0) {
                            Log.e("Category", "LearningPlayFragmentNew right card exit : about to go inside noCardsLeft");
                            noCradsLeft();
                        }
                    } else
                        category_optionsleft.setText(optionData.size() + " " + getActivity().getResources().getString(R.string.cards_left));
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    if (optionData.size() == 0 && i == 0) {
                        Log.e("Category", "LearningPlayFragmentNew onAdapterAboutToEmpty : about to go inside noCardsLeft");
                        noCradsLeft();
                    }
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                }
            });


            // Optionally add an OnItemClickListener
           /* flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {

                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void noCradsLeft() {
        i++;
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("Category", "inside noCradsLeft! Peace!");
                calculatepoints(false);
            }
        }, THREE_HUNDRED_MILLI_SECONDS);
    }

    public void setcategories(List<DTOQuestionOptionCategory> categoryData) {
        try {
            if (categoryData != null) {
                if ((categoryData.size() > 0) && (categoryData.get(0).getType().equalsIgnoreCase("text"))) {
                    category_leftimage_layout.setVisibility(View.GONE);
                    category_lefttext_layout.setVisibility(View.VISIBLE);
                    category_lefttext_layout.setText(getStrtoChars(categoryData.get(0).getData()));
                } else if ((categoryData.size() > 0) && (categoryData.get(0).getType().equalsIgnoreCase("image"))) {
                    category_lefttext_layout.setVisibility(View.GONE);
                    category_leftimage_layout.setVisibility(View.VISIBLE);
                    if (categoryData.get(0).getData() != null && !categoryData.get(0).getData().isEmpty()) {
                        setImageOptionFromFile(categoryData.get(0).getData(), category_leftimage_layout);
                    } else
                        setImageOption(categoryData.get(0).getData(), category_leftimage_layout);
                }
                if ((categoryData.size() > 1) && (categoryData.get(1).getType().equalsIgnoreCase("text"))) {
                    category_rightimage_layout.setVisibility(View.GONE);
                    category_righttext_layout.setVisibility(View.VISIBLE);
                    category_righttext_layout.setText(getStrtoChars(categoryData.get(1).getData()));
                } else if ((categoryData.size() > 1) && (categoryData.get(1).getType().equalsIgnoreCase("image"))) {
                    category_righttext_layout.setVisibility(View.GONE);
                    category_rightimage_layout.setVisibility(View.VISIBLE);
                    if (categoryData.get(1).getData() != null && !categoryData.get(1).getData().isEmpty()) {
                        setImageOptionFromFile(categoryData.get(1).getData(), category_rightimage_layout);
                    } else
                        setImageOption(categoryData.get(1).getData(), category_rightimage_layout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String getStrtoChars(String data) {
        String result = "";
        if (data != null && data.length() > 0) {
            for (int i = 0; i < data.length(); i++) {
                result = result + data.charAt(i) + "\n";
            }
        }
        return result;
    }


    //    =======================================================================================
//      flingCard listner is used to swipe cards in category question type specifically


    public void calculatepoints(boolean timeout) { // praveen calculte points one
        try {
            boolean categoryRight = false;
            if (!timeout) {
//                if (OustSdkTools.optionSelected == totalOption) {
                if (!isAssessmentQuestion) {
                    if (OustSdkTools.totalAttempt < (totalOption + 1)) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else {
                        int totalXp = (int) mainCourseCardClass.getXp();
                        finalScr = totalXp - ((10 * (OustSdkTools.totalAttempt - (OustSdkTools.optionSelected))));
                    }
                    if (finalScr < 0) {
                        finalScr = 0;
                    }
                    answerSubmit(questions.getAnswer(), finalScr, timeout, true);
                } else {
                    if (OustSdkTools.totalAttempt == totalOption && OustSdkTools.totalAttempt == OustSdkTools.totalCategoryRight) {
                        categoryRight = true;
                    }
                    float score = 0;
                    if (OustSdkTools.totalCategoryRight != 0) {
                        float rightWeight = ((float) questionXp / (float) totalOption);
                        float weight = ((float) questionXp / (float) (totalOption * 2));
                        score = ((rightWeight * OustSdkTools.totalCategoryRight) - (weight * (OustSdkTools.totalAttempt - (OustSdkTools.totalCategoryRight))));
                        finalScr = Math.round(score);
                        if (finalScr < 0)
                            finalScr = 0;
                        if (finalScr > questionXp)
                            finalScr = questionXp;
//                            score_text = (OustSdkTools.totalCategoryRight / (float) OustSdkTools.totalAttempt) * 20;
                        //learningModuleInterface.setAnswerAndOc("", "", (int) score_text, categoryRight);
                    }

                    if (!categoryRight && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                        finalScr = 0;
                        answerSubmit("", 0, timeout, false);
                    } else {
                        answerSubmit("", Math.round(score), timeout, categoryRight);
                    }
                    //learningModuleInterface.gotoNextScreen();
                }
//                }
            } else if (timeout) {
//                if (!isAssessmentQuestion) {
                if (OustSdkTools.totalAttempt == totalOption && OustSdkTools.totalAttempt == OustSdkTools.totalCategoryRight) {
                    categoryRight = true;
                }
                float weight = (float) mainCourseCardClass.getXp() / (totalOption); // point calculation
                float rightVal = weight * OustSdkTools.optionSelected;
                finalScr = Math.round(rightVal - ((10 * (OustSdkTools.totalAttempt - (OustSdkTools.optionSelected)))));

                if (finalScr < 0) {
                    finalScr = 0;
                }
                if (isAssessmentQuestion && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
                    answerSubmit("", 0, timeout, false);
                } else {
                    answerSubmit(questions.getAnswer(), finalScr, timeout, categoryRight); // praveen final submit
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
         /*   if (OustSdkTools.categoryData != null)
                OustSdkTools.categoryData.clear();
            if (OustSdkTools.optionData != null)
                OustSdkTools.optionData.clear();
            OustSdkTools.totalAttempt = 0;
            OustSdkTools.optionSelected = 0;
            OustSdkTools.totalCategoryRight = 0;*/
            removeAllData();
            resetAllData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
    //=================================================================================
    //hotspot feature methodes
    //==============================================================================================


    private List<DTOHotspotPointData> hotspotPointDataList = new ArrayList<>();
    private boolean[] touchPoint;

    private float bitmapW, bitmapH;
    private boolean isHotSpotV2 = true;
    private float bitmapActualW, bitmapActualH;
    private float ratioW = 0, ratioH = 0;
    private int currentPageNo = 0;
    private int hotspotWrongCount = 0;
    private int hotspotMaxAttempt = 3;
    private int hotspotRightCount = 0;

    private void startHotspotQuestion() {
        try {
            Log.d(TAG, "startHotspotQuestion: ");
            if ((questions != null) && (questions.getHotspotDataList() != null)) {
                hotspotPointDataList = questions.getHotspotDataList();
                touchPoint = new boolean[hotspotPointDataList.size()];
                hotspot_imgtouch_indicator.setList(hotspotPointDataList, touchPoint);
                hotspot_imgtouch_indicator.setThumbShow(true,isHotSpotThumbsDownShown);
                currentPageNo = 0;
                if (mInflater == null) {
                    mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                }
                downloadHotspotImage();
                //setHotSpotImageSize();
                showHotSpotLabelAnim();
                touchForhotspot_img();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadHotspotImage() {
        Log.d(TAG, "downloadHotspotImage: ");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            //bmOptions.inJustDecodeBounds = true;
            String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());

            String url = "oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            Uri uri = Uri.fromFile(file);
            if (!file.exists()) {
                hotspotimage = BitmapFactory.decodeFile(fileURL);
            } else {
                InputStream imageStream = Objects.requireNonNull(requireActivity()).getContentResolver().openInputStream(uri);
                hotspotimage = BitmapFactory.decodeStream(imageStream);
            }


            String path = "course/media/audio/" + questions.getImageCDNPath();
            String media = questions.getImageCDNPath();
            Log.d(TAG, "downloadHotspotImage: " + file.toString());
            Log.d(TAG, "downloadHotspotImage: " + questions.getImageCDNPath());
            Log.d(TAG, "downloadHotspotImage: " + file.exists());

            Log.d("HotspotquestionNoError1", " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (hotspotimage == null) {
                            try (InputStream is = new URL(questions.getImageCDNPath()).openStream()) {
                                hotspotimage = BitmapFactory.decodeStream(is);
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();

            if (hotspotimage == null) {
                Handler handler = new Handler(Looper.getMainLooper());
                String finalFileURL = fileURL;
                File finalFile = file;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("HotspotquestionNoError2", " \nfileURL: " + finalFileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + finalFile);
                            hotspotimage = BitmapFactory.decodeFile(finalFileURL, bmOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }

                    }
                }, 300);
            }

            Log.d("HotspotquestionNoError3", " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage + " \nfile: " + file);
            if (hotspotimage == null) {
                try {
                    hotspotimage = BitmapFactory.decodeFile(fileURL, bmOptions);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            if (hotspotimage == null) {
                try {
                    URL url1 = new URL(questions.getImageCDNPath());
                    hotspotimage = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            Log.d("HotspotquestionNoError4", " \nfileURL: " + fileURL + " \nhotSpot: " + hotspotimage + " \nfilURL: " + questions.getImageCDNPath());
            if (hotspotimage == null) {
                getBitmapFromURL(questions.getImageCDNPath());
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), options);
            if (hotspotimage != null && options.outWidth != -1 && options.outHeight != -1) {
                setHotSpotImageSize(hotspotimage);
            } else {
                layout_progress_hotspot.setVisibility(View.VISIBLE);
                mText_progress_failed.setVisibility(View.GONE);
                mTextview_progress_hotspot.setVisibility(View.GONE);
                layout_progress_hotspot.bringToFront();
                mTextview_progress_hotspot.setText("0%");

                Log.d(TAG, "setHotSpotImageSize: null hotspot");

                if (OustSdkTools.checkInternetStatus()) {
                    if (OustMediaTools.isAwsOrCDnUrl(media)) {
                        media = OustMediaTools.removeAwsOrCDnUrl(media);
                        String fileName = OustMediaTools.getMediaFileName(media);
                        //downLoad(fileName, media);
                    }
                    if (file.exists()) {
                        boolean b = file.delete();
                        Log.d(TAG, "Hotspot image file " + b);
                    }
                    downLoad(questions.getImageCDNPath());
                } else {
                    mText_progress_failed.setVisibility(View.VISIBLE);
                    mTextview_progress_hotspot.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (!isImageDownloaded) {
                isImageDownloaded = true;
                new Handler().postDelayed(() -> {
                    OustSdkTools.showToast("Network seems slow. Unable to download. Please check your network and try again.");
                    Objects.requireNonNull(requireActivity()).finish();
                }, 5000);

            }


        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d("HotspotquestionNoError5", "" + myBitmap);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("HotspotquestionNoError6", "" + "null came");
            return null;
        }
    }

    public void downLoad(String url) {

        Log.d(TAG, "downLoad: Media:" + CLOUD_FRONT_BASE_PATH);
        Log.d(TAG, "downLoad: Media:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
        try {

            DownloadFiles downloadFiles;
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {
                    //showDownloadProgress();
                    Log.d(TAG, "onDownLoad: Message:" + message + " progress:" + progress);
                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        //removeFile();
                    }
                    Log.d(TAG, "onDownLoad: Message:" + message + " progress:" + code);

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

            Log.d(TAG, "onDownLoad: Message: startDownLoad");


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downLoad(String fileName, String path) {
        Log.d(TAG, "downLoad: " + fileName + " -- path:" + path);
        mText_progress_failed.setVisibility(View.GONE);
        mTextview_progress_hotspot.setVisibility(View.VISIBLE);
        setReceiver();
        String destn = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/";
        sendToDownloadService(getContext(), CLOUD_FRONT_BASE_PATH + path, destn, fileName, true);

    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private void setReceiver() {
        Log.d(TAG, "setReceiver: ");
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        //if(OustSdkApplication.getContext()!=null) {
        getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
        //}
    }

    private void sendToDownloadService(Context context, String downloadPath, String destn, String fileName, boolean isOustLearn) {
        try {
            Log.d(TAG, "sendToDownloadService: " + downloadPath + " -- " + destn + " -- " + fileName);
            Intent intent = new Intent(getContext(), DownloadImage.class);
            intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
            intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
            intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
            intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    //Log.d(TAG, "MyFileDownLoadReceiver onReceive: "+intent.getAction());
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            if (intent.hasExtra("MSG")) {
                                mTextview_progress_hotspot.setText(intent.getStringExtra("MSG") + "%");
                            }
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            if (myFileDownLoadReceiver != null) {
                                getActivity().unregisterReceiver(myFileDownLoadReceiver);
                                myFileDownLoadReceiver = null;
                            }
                            layout_progress_hotspot.setVisibility(View.GONE);
                            mTextview_progress_hotspot.setText("0%");
                            setHotSpotImageSize(null);
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            mText_progress_failed.setVisibility(View.VISIBLE);
                            mTextview_progress_hotspot.setVisibility(View.GONE);
                            //OustSdkTools.showToast("Please check your internet connection");
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    }

    Bitmap hotspotIcon;

    private void setHotSpotImageSize(Bitmap hotspotImage) {
        try {
            startTimer();
            if (!isAssessmentQuestion) {
                startSpeakQuestion();
            }

            hotspot_mainlayout.setVisibility(View.VISIBLE);
            if (hotspotImage == null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
                hotspotIcon = BitmapFactory.decodeFile(fileURL, bmOptions);
            } else {
                hotspotIcon = hotspotImage;
            }

            Log.d(TAG, "setHotSpotImageSize: hotspotIcon" + hotspotIcon);

            if (questions.getImagewidth() != null) {
                bitmapActualW = Float.parseFloat(questions.getImagewidth());
                if (bitmapActualW == 0) {
                    bitmapActualW = hotspotIcon.getWidth();
                }
            }
            if (questions.getImagewidth() != null) {
                bitmapActualH = Float.parseFloat(questions.getImageheight());
                if (bitmapActualH == 0) {
                    bitmapActualH = hotspotIcon.getHeight();
                }
            }

            bitmapW = (scrWidth);
            bitmapH = bitmapW * ((float) hotspotIcon.getHeight() / (float) hotspotIcon.getWidth());
            if (isThirdFourthHotSpotImage) {
                hotspot_img.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();

            params.height = ((int) bitmapH);
            params.width = (int) bitmapW;

            hotspot_img.setLayoutParams(params);
            hotspot_img.setImageBitmap(hotspotIcon);
            hotspot_img.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) hotspot_imgtouch_indicator.getLayoutParams();
            params1.height = ((int) bitmapH);
            params1.width = (int) bitmapW;

            hotspot_imgtouch_indicator.setLayoutParams(params1);
            hotspot_imgtouch_indicator.setVisibility(View.VISIBLE);
            hotspot_imgtouch_indicator.setReviewmode();

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) hotspotlabel_layout.getLayoutParams();
            params2.height = (int) bitmapH;
            params2.width = (int) bitmapW;

            hotspotlabel_layout.setLayoutParams(params2);

            if (isThirdFourthHotSpotImage) {
                NormalHotspotImageSetting();
            } else {
                FullScreenHotspotImageSetting();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    int hotSpotImageWidth, hotSpotImageHeight;
    int actualImageWidth, actualimageHeight;

    private void NormalHotspotImageSetting() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotIcon.getWidth();
        actualimageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = hotSpotImageWidth * actualimageHeight / actualImageWidth;
        // hotSpotImageHeight = displayMetrics.heightPixels/4*3;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();

      /*  params.height = ((int) bitmapH);
        params.width = (int)bitmapW;

        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        hotspot_img.setLayoutParams(params);
        hotspot_img.setImageBitmap(hotspotIcon);
        hotspot_img.setVisibility(View.VISIBLE);*/


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
        //File file = new File(fileURL);
        hotspotIcon = BitmapFactory.decodeFile(fileURL, bmOptions);


        int apiStartX = hotspotPointDataList.get(0).getStartX();
        int apiStartY = hotspotPointDataList.get(0).getStartY();

        int apIRectangleWidth = hotspotPointDataList.get(0).getWidth();
        int apiRectangleHeight = hotspotPointDataList.get(0).getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = deviceXFactor;
        // float deviceYFactor = hotSpotImageHeight / (float)actualimageHeight;

        int yAdjustment = (displayMetrics.heightPixels - getStatusBarHeight() - hotSpotImageHeight) / 2;
        final int newStartX = (int) (apiStartX * deviceXFactor);
        final int newStartY = (int) (apiStartY * deviceYFactor);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * deviceXFactor);
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * deviceYFactor);

        //hotspot_imgtouch_indicator.drawRect(hotspotPointDataList, true, deviceXFactor, deviceYFactor);
        int[] endpoints = calculateEndPointsForNormalHotspot(hotspotIcon, hotspotPointDataList.get(0));
        //  hotspot_imgtouch_indicator.drawRect(endpoints[0],endpoints[1],endpoints[2],endpoints[3], true);
    }

    private void FullScreenHotspotImageSetting() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
        //File file = new File(fileURL);
        hotspotIcon = BitmapFactory.decodeFile(fileURL, bmOptions);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = hotspot_mainlayout.getHeight() - getStatusBarHeight();
        hotspot_img.setLayoutParams(params);


        hotspotindicator_layout.setVisibility(View.GONE);
        learningquiz_mainquestion.setVisibility(View.GONE);
        mainoption_scrollview.setVisibility(View.GONE);
        hotspotlabel_layout.setVisibility(View.GONE);
        hotspotmax_counttext.setVisibility(View.GONE);
        ref_image.setVisibility(View.GONE);

        actualImageWidth = hotspotIcon.getWidth();
        actualimageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = hotspot_mainlayout.getHeight(); //*actualimageHeight/ actualImageWidth +getStatusBarHeight();
        // hotSpotImageHeight = displayMetrics.heightPixels-getStatusBarHeight();

        int apiStartX = hotspotPointDataList.get(0).getStartX();
        int apiStartY = hotspotPointDataList.get(0).getStartY();

        int apIRectangleWidth = hotspotPointDataList.get(0).getWidth();
        int apiRectangleHeight = hotspotPointDataList.get(0).getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = hotSpotImageHeight / (float) actualimageHeight;
        float multiplyingFactor = 0;

        if (deviceYFactor > deviceXFactor) {
            multiplyingFactor = deviceXFactor;
        } else {
            multiplyingFactor = deviceYFactor;
        }

        int yAdjustment = (hotspot_mainlayout.getHeight() - (int) (actualimageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;


        //  int yAdjustment = displayMetrics.heightPixels - getStatusBarHeight();

        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);
        //  int newChangedY = newStartY+(int)(newStartY*0.5);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;
        //  int newChangedEndY = newEndY+(int)(newEndY*0.5);

        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();
        bitmapH = bitmapW * ((float) hotspotIcon.getHeight() / (float) hotspotIcon.getWidth());

        if (multiplyingFactor < 1) // this calculation for displaying large image into small screen
        {
            multiplyingFactor = 1.0f;
        }
        params3.height = (int) (multiplyingFactor * hotSpotImageHeight);
        params3.width = (int) (multiplyingFactor * hotSpotImageWidth);

        params3.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        hotspot_img.setLayoutParams(params3);


        int[] endpoints = calculateEndPointsForFullScreenHotspot(hotspotIcon, hotspotPointDataList.get(0));

        // hotspot_imgtouch_indicator.drawRect(endpoints[0],endpoints[1],endpoints[2],endpoints[3], true);
        //hotspot_imgtouch_indicator.drawRect(hotspotPointDataList, true, deviceXFactor, deviceYFactor);

      /*  RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tv=new TextView(getContext());
        tv.setText("Test question");*/
        //hotspot_mainlayout.addView(tv);
    }

    private int[] calculateEndPointsForFullScreenHotspot(Bitmap hotspotIcon, DTOHotspotPointData hotspotPointData) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotIcon.getWidth();
        actualimageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        hotSpotImageHeight = hotspot_mainlayout.getHeight(); //*actualimageHeight/ actualImageWidth +getStatusBarHeight();

        int apiStartX = hotspotPointData.getStartX();
        int apiStartY = hotspotPointData.getStartY();

        int apIRectangleWidth = hotspotPointData.getWidth();
        int apiRectangleHeight = hotspotPointData.getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = hotSpotImageHeight / (float) actualimageHeight;
        float multiplyingFactor = 0;

        if (deviceYFactor > deviceXFactor) {
            multiplyingFactor = deviceXFactor;
        } else {
            multiplyingFactor = deviceYFactor;
        }
        this.deviceYFactor = this.deviceXFactor = multiplyingFactor;

        int yAdjustment = (hotspot_mainlayout.getHeight() - (int) (actualimageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;

        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;

        int[] endPoints = new int[4];
        endPoints[0] = newStartX;
        endPoints[1] = newStartY;
        endPoints[2] = newEndX;
        endPoints[3] = newEndY;
        return endPoints;
    }

    private int[] calculateEndPointsForNormalHotspot(Bitmap hotspotIcon, DTOHotspotPointData hotspotPointData) {
        int apiStartX = hotspotPointData.getStartX();
        int apiStartY = hotspotPointData.getStartY();

        int apIRectangleWidth = hotspotPointData.getWidth();
        int apiRectangleHeight = hotspotPointData.getHeight();

        float deviceXFactor = hotSpotImageWidth / (float) actualImageWidth;
        float deviceYFactor = deviceXFactor;
        this.deviceXFactor = deviceXFactor;
        this.deviceYFactor = deviceYFactor;

        int yAdjustment = (scrHeight - getStatusBarHeight() - hotSpotImageHeight) / 2;
        float minx = (apiStartX * deviceXFactor);
        float minY = (apiStartY * deviceYFactor);

        float maxY = ((apiStartY + apiRectangleHeight) * deviceYFactor);
        float maxX = ((apiStartX + apIRectangleWidth) * deviceXFactor);

        int[] endPoints = new int[4];
        endPoints[0] = (int) (minx);
        endPoints[1] = (int) minY;
        endPoints[2] = (int) maxX;
        endPoints[3] = (int) maxY;
        return endPoints;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getBottomBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void showHotSpotLabelAnim() {
        try {
            if (hotspotPointDataList.size() > currentPageNo) {
                if (hotspotPointDataList.get(currentPageNo).getHpQuestion() != null) {
                    if (hotspotPointDataList.get(currentPageNo).getHpQuestion().contains(KATEX_DELIMITER)) {
                        hotspotlabel.setVisibility(View.GONE);
                        hotspotlabelMaths.setVisibility(View.VISIBLE);
                        hotspotlabelMaths.setText("" + (currentPageNo + 1) + ". " + hotspotPointDataList.get(currentPageNo).getHpQuestion());
                    } else {
                        hotspotlabel.setVisibility(View.VISIBLE);
                        hotspotlabelMaths.setVisibility(View.GONE);
                        hotspotlabel.setText("" + (currentPageNo + 1) + ". " + hotspotPointDataList.get(currentPageNo).getHpQuestion());
                    }
                }
            }
            hotspotMaxAttempt = 3;
            if (!isAssessmentQuestion) {
                setMaxattempt();
            } else {
                hotspotmax_counttext.setVisibility(View.GONE);
            }

            ObjectAnimator moveY;
            hotspotlabel.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
            if (hotspotPointDataList.get(currentPageNo).getHpQuestion().contains(KATEX_DELIMITER)) {
                moveY = ObjectAnimator.ofFloat(hotspotlabelMaths, "x", scrWidth, 0);
            } else {
                moveY = ObjectAnimator.ofFloat(hotspotlabel, "x", scrWidth, 0);
            }

            moveY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            moveY.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean shouldDisplayRightWrong = false;
    private float x11, x21;
    private float y11, y21;

    @SuppressLint("ClickableViewAccessibility")
    private void touchForhotspot_img() {
        try {
            hotspot_img.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    shouldDisplayRightWrong = (!isThirdFourthHotSpotImage && hotspotPointDataList.size() > 1);
                    if (!hotspotcomplete) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            x11 = event.getX();
                            y11 = event.getY();
                            Log.d(TAG, "onTouch:action down");
                        }
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            Log.d(TAG, "onTouch:action move");
                            x21 = event.getX();
                            y21 = event.getY();
                            float deltaX = x11 - x21;
                            float deltaY = y11 - y21;
                            if (deltaX > 0 && deltaY > 0) {
                                if (deltaX > deltaY) {
                                    if (deltaX > MIN_DISTANCE) {
                                        if (learningModuleInterface != null && hotspotcomplete)
                                            if (isVideoOverlay())
                                                learningModuleInterface.closeChildFragment();
                                            else
                                                learningModuleInterface.gotoNextScreen();
                                    }
                                }
                            } else if (deltaX < 0 && deltaY > 0) {
                                if ((-deltaX) > deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        if (learningModuleInterface != null)
                                            if (!isVideoOverlay())
                                                learningModuleInterface.gotoPreviousScreen();
                                    }
                                }

                            } else if (deltaX < 0 && deltaY < 0) {
                                if (deltaX < deltaY) {
                                    if ((-deltaX) > MIN_DISTANCE) {
                                        if (learningModuleInterface != null)
                                            if (!isVideoOverlay())
                                                learningModuleInterface.gotoPreviousScreen();
                                    }
                                }
                            } else if (deltaX > 0 && deltaY < 0) {
                                if (deltaX > (-deltaY)) {
                                    if (deltaX > MIN_DISTANCE) {
                                        if (learningModuleInterface != null && hotspotcomplete)
                                            if (isVideoOverlay())
                                                learningModuleInterface.closeChildFragment();
                                            else
                                                learningModuleInterface.gotoNextScreen();
                                    }
                                }
                            }
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (hotspotMaxAttempt > 0) {
                                Log.d(TAG, "onTouch:action up:" + currentPageNo + " -- size:" + hotspotPointDataList.size() + " -- complte:" + hotspotcomplete);
                                if (currentPageNo <= (hotspotPointDataList.size() - 1) && !hotspotcomplete) {
                                    float touchX = (int) event.getX();
                                    float touchY = (int) event.getY();
                                    boolean rightTouch = false;
                                    int touchNO = -1;

                                    ratioW = bitmapW / bitmapActualW;
                                    ratioH = bitmapH / bitmapActualH;
                                    // float minx = hotspotPointDataList.get(currentPageNo).getStartX() * ratioW;
                                    //  float maxX = ratioW * (hotspotPointDataList.get(currentPageNo).getStartX() + hotspotPointDataList.get(currentPageNo).getWidth());
                                    //  float minY = hotspotPointDataList.get(currentPageNo).getStartY();
                                    //   float maxY = hotspotPointDataList.get(currentPageNo).getStartY() + hotspotPointDataList.get(currentPageNo).getHeight();

                                    float minx, minY, maxX, maxY;

                                    int[] endPoints = new int[4];
                                    if (!isThirdFourthHotSpotImage) {
                                        endPoints = calculateEndPointsForFullScreenHotspot(hotspotIcon, hotspotPointDataList.get(currentPageNo));
                                    } else {
                                        endPoints = calculateEndPointsForNormalHotspot(hotspotIcon, hotspotPointDataList.get(currentPageNo));
                                    }
                                    minx = (float) endPoints[0];
                                    minY = (float) endPoints[1];
                                    maxX = (float) endPoints[2];
                                    maxY = (float) endPoints[3];

                                    if ((touchX > (minx)) && (touchX < (maxX))) {
                                        if ((touchY > (minY)) && (touchY < (maxY))) {
                                            if (!touchPoint[currentPageNo]) {
                                                touchNO = currentPageNo;
                                                touchPoint[currentPageNo] = true;
                                            }
                                            rightTouch = true;
                                        }
                                    }

                                    if (!isThirdFourthHotSpotImage && hotspotPointDataList.size() > 1) {
                                        for (int i = 0; i < hotspotPointDataList.size(); i++) {
                                            endPoints = calculateEndPointsForFullScreenHotspot(hotspotIcon, hotspotPointDataList.get(i));
                                            minx = (float) endPoints[0];
                                            minY = (float) endPoints[1];
                                            maxX = (float) endPoints[2];
                                            maxY = (float) endPoints[3];
                                            if ((touchX > (minx)) && (touchX < (maxX))) {
                                                if ((touchY > (minY)) && (touchY < (maxY))) {
                                                    noOfTimesClicked++;
                                                    if (!isHotSpotAnswerSelectedRight) {
                                                        if (hotspotPointDataList.get(i).isAnswer()) {
                                                            isHotSpotAnswerSelectedRight = true;
                                                        } else {
                                                            Log.d(TAG, "onTouch: Wrong Answer:");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Log.d(TAG, "onTouch:num of time clicked: " + noOfTimesClicked);
                                        Log.d(TAG, "onTouch: selected answer:" + isHotSpotAnswerSelectedRight);
                                        if (isHotSpotAnswerSelectedRight) {
                                            Log.d(TAG, "onTouch: moving next screen");
                                            calculateHotspotPointsForMCQ(false);
                                            return true;
                                            //learningModuleInterface.gotoNextScreen();
                                        }
                                        if ((noOfTimesClicked) > hotspotPointDataList.size()) {
                                            calculateHotspotPointsForMCQ(false);
                                            return true;
                                        }
                                        return true;
                                    }
                                    //
                                    if (!isAssessmentQuestion) {
                                        if (!rightTouch) {
                                            vibrateandShakeView(hotspot_img);
                                            hotspotWrongCount++;
                                            wrongAnswerSound();
                                            HotspotPointData hotspotPointData = new HotspotPointData();
                                            hotspotPointData.setStartX((int) touchX);
                                            hotspotPointData.setStartY((int) touchY);
                                            hotspotPointData.setWidth(60);
                                            if (isHotSpotThumbsDownShown) {
                                                if (!shouldDisplayRightWrong) {
                                                    hotspot_imgtouch_indicator.wrongPoint(hotspotPointData);
                                                }
                                            }
                                            hotspotMaxAttempt--;
                                            if (hotspotMaxAttempt == 0) {
                                                touchPoint[currentPageNo] = true;
                                                float w = ((hotspotPointDataList.get(currentPageNo).getStartX() + (hotspotPointDataList.get(currentPageNo).getWidth() / 2)));
                                                float h = ((hotspotPointDataList.get(currentPageNo).getStartY() + (hotspotPointDataList.get(currentPageNo).getHeight() / 2)));
                                                if (!shouldDisplayRightWrong) {
                                                    showRightPointWithLabel(true, ((int) (w * deviceXFactor)), ((int) (h * deviceYFactor)));
                                                }
                                            }
                                            setMaxattempt();
                                        } else {
                                            if (touchNO != (-1)) {
                                                hotspotRightCount++;
                                                DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                                                hotspotPointData.setStartX((int) touchX);
                                                hotspotPointData.setStartY((int) touchY);
                                                hotspotPointData.setWidth(80);
                                                if (isHotSpotThumbsUpShown) {
                                                    hotspot_imgtouch_indicator.rightPoint(hotspotPointData, touchNO, touchPoint);
                                                }
                                                if ((currentPageNo + 1) < hotspotPointDataList.size()) {
                                                    rightAnswerSound();
                                                }
                                                if (!shouldDisplayRightWrong) {
                                                    showRightPointWithLabel(false, (int) touchX, (int) touchY);
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (learningModuleInterface != null) {
                                                            if (isVideoOverlay())
                                                                learningModuleInterface.closeChildFragment();
                                                            else
                                                                learningModuleInterface.gotoNextScreen();
                                                        }
                                                    }
                                                }, 1500);
                                            }
                                        }
                                    } else if (!hotspotcomplete) {
                                        if (!rightTouch) {
                                            hotspotWrongCount++;
                                        } else {
                                            hotspotRightCount++;
                                        }
                                        touchPoint[currentPageNo] = true;
                                        DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                                        hotspotPointData.setStartX((int) touchX);
                                        hotspotPointData.setStartY((int) touchY);
                                        hotspotPointData.setWidth(80);
                                        hotspot_imgtouch_indicator.assessmentPoint(hotspotPointData, currentPageNo, true, currentPageNo, touchPoint);
                                        if (!shouldDisplayRightWrong) {
                                            showRightPointWithLabel(false, (int) touchX, (int) touchY);
                                        }
                                    }
                                }
                            } else {
                                hotspotmax_counttext.setText((getActivity().getResources().getString(R.string.no_attempt_left)));
                            }
                        }
                    }
                    return true;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showRightPointWithLabel(boolean maxLimitOver, int x, int y) {
        try {
            Log.d(TAG, "showRightPointWithLabel: ");
            if (!isAssessmentQuestion) {
                calculatePointForIndividualHotspot();
                DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
                hotspotPointData.setStartX((int) x);
                hotspotPointData.setStartY((int) y);
                hotspotPointData.setWidth(80);
                hotspot_imgtouch_indicator.rightPoint(hotspotPointData, currentPageNo, touchPoint);
            }

            if ((currentPageNo + 1) == hotspotPointDataList.size()) {
                currentPageNo++;
                calculateHotspotPoints(false);
            } else {
                currentPageNo++;
                hideHotspotLabelAnim(maxLimitOver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideHotspotLabelAnim(boolean timeout) {
        try {
            if (!isAssessmentQuestion) {
                if (!timeout) {
                    hotspotlabel.setTextColor(OustSdkTools.getColorBack(R.color.LiteGreen));
                } else {
                    hotspotlabel.setTextColor(OustSdkTools.getColorBack(R.color.Red));
                }
            }
            ObjectAnimator moveY = ObjectAnimator.ofFloat(hotspotlabel, "x", 0, -scrWidth);
            moveY.setDuration(FIVE_HUNDRED_MILLI_SECONDS);
            moveY.setStartDelay(FIVE_HUNDRED_MILLI_SECONDS);
            moveY.start();
            moveY.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showHotSpotLabelAnim();
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

    private float hotspotMarks = 0;

    public void calculatePointForIndividualHotspot() {
        try {
            Log.d(TAG, "calculatePointForIndividualHotspot: " + hotspotRightCount + " --- xp:" + mainCourseCardClass.getXp() + " --- size:" + hotspotPointDataList.size());
            if (hotspotRightCount > 0) {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotspotRightCount;
                hotspotMarks += rightVal - ((10 * hotspotWrongCount));
            }
            hotspotRightCount = 0;
            hotspotWrongCount = 0;

            Log.d(TAG, "calculatePointForIndividualHotspot: hotspotMarks:" + hotspotMarks);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculateHotspotPoints(boolean timeout) {
        try {
            hotspotcomplete = true;
            boolean isCorrect = false;
            if (!isAssessmentQuestion) {
                isCorrect = true;
                finalScr = Math.round(hotspotMarks);
            } else {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotspotRightCount;
                finalScr = Math.round(rightVal);
            }

            if (isAssessmentQuestion && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())
                    && (hotspotRightCount != hotspotPointDataList.size() || timeout)) {
                finalScr = 0;
                isCorrect = false;
            }

            if (hotspotRightCount == hotspotPointDataList.size()) {
                isCorrect = true;
            }

            if (finalScr < 0) {
                finalScr = 0;
            } else if (finalScr > mainCourseCardClass.getXp()) {
                finalScr = (int) mainCourseCardClass.getXp();
            }

            answerSubmit(questions.getAnswer(), finalScr, timeout, isCorrect);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculateHotspotPointsForMCQ(boolean timeout) {
        try {
            hotspotcomplete = true;
            boolean isCorrect = false;
            if (!isAssessmentQuestion) {
                isCorrect = true;
                finalScr = Math.round(hotspotMarks);
            } else {
                float weight = (float) mainCourseCardClass.getXp() / (hotspotPointDataList.size());
                float rightVal = weight * hotspotRightCount;
                finalScr = Math.round(rightVal);
            }

            if (isAssessmentQuestion && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())
                    && (hotspotRightCount != hotspotPointDataList.size() || timeout)) {
                finalScr = 0;
                isCorrect = false;
            }

            if (hotspotRightCount == hotspotPointDataList.size()) {
                isCorrect = true;
            }

            if (finalScr < 0) {
                finalScr = 0;
            } else if (finalScr > mainCourseCardClass.getXp()) {
                finalScr = (int) mainCourseCardClass.getXp();
            }

            noOfTimesClicked = noOfTimesClicked - 1;
            if (noOfTimesClicked > hotspotPointDataList.size()) {
                finalScr = 0;
            } else {
                finalScr = (int) (mainCourseCardClass.getXp() - ((float) noOfTimesClicked * mainCourseCardClass.getXp() / (hotspotPointDataList.size())));
            }
            Log.d(TAG, "calculateHotspotPointsForMCQ: final:" + finalScr + " -- iscorrect:" + isCorrect);
            answerSubmit(questions.getAnswer(), finalScr, timeout, isCorrect);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean popupShownOnce = false;

    private void answerSubmit(String answer, int oc, boolean isTimeOut, boolean isCorrect) {
        try {
            cancleTimer();
            if (!popupShownOnce) {
                popupShownOnce = true;
                if (questions != null) {
                    if ((questions.isContainSubjective()) && (!isTimeOut)) {
                        showSubjectiveQuestionPopup(answer, isCorrect, oc);
                    } else {
                        if (isVideoOverlay()) {
                            if (learningModuleInterface != null)
                                learningModuleInterface.setVideoOverlayAnswerAndOc(answer, "", oc, isCorrect, 0, cardId);
                        } else {
                            learningModuleInterface.setAnswerAndOc(answer, "", oc, isCorrect, 0);// submit answerand oc
                        }
                        if (!isAssessmentQuestion) {
                            rightwrongFlipAnimation(true);
                        } else {
                            if (isVideoOverlay()) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.closeChildFragment();
                            } else {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoNextScreen();
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

    private void setMaxattempt() {
        if (hotspotMaxAttempt > 0) {
            hotspotmax_counttext.setText((getActivity().getResources().getString(R.string.attempt_left_text) + " " + hotspotMaxAttempt));
        }
    }

    public void vibrateandShakeView(View v) {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            v.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(ONE_HUNDRED_MILLI_SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addLabel(int x, int y, String label) {
        View hotspotlabelview = mInflater.inflate(R.layout.hotspot_label, null);
        TextView textView = hotspotlabelview.findViewById(R.id.hotspot_labeltext);
        textView.setText(label);
        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen100);
        int startPoint = (x - size);
        if (startPoint < 0) {
            startPoint = 0;
        }
        int size1 = (int) getResources().getDimension(R.dimen.oustlayout_dimen16);
        int maxW = (scrWidth) - size1;
        if (startPoint > scrWidth) {
            startPoint = 0;
        }
        if ((startPoint + size) > maxW) {
            startPoint = (maxW - (2 * size));
        }
        hotspotlabelview.setX(startPoint);
        hotspotlabelview.setY((y + 35));
        hotspotlabel_layout.addView(hotspotlabelview);
    }


    //============================================================================
    private int maxWordsCount = 20;
    private int minWordsCount = 0;
    private RelativeLayout questionsubans_cardlayout, questionsubans_submit_btn;
    private TextView questionsubans_header, questionsubans_limittext;
    private EditText questionsubans_editetext;
    private CardView questionsubans_card;

    private void showSubjectiveQuestionPopup(final String answer, final boolean status, final int oc) {
        questionsubans_cardlayout.setVisibility(View.VISIBLE);
        learningModuleInterface.disableBackButton(true);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(questionsubans_card, "x", -720, 0);
        scaleDownX.setDuration(600);
        scaleDownX.setInterpolator(new AccelerateInterpolator());
        scaleDownX.start();
        scaleDownX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //showKeyboard();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        if ((questions.getSubjectiveQuestion() != null) && (!questions.getSubjectiveQuestion().isEmpty())) {
            questionsubans_header.setText(questions.getSubjectiveQuestion());
            questionsubans_header.setVisibility(View.VISIBLE);
        } else {
            questionsubans_header.setText(getActivity().getResources().getString(R.string.explain_your_rationale));
        }
        maxWordsCount = questions.getMaxWordCount();
        if (maxWordsCount == 0) {
            maxWordsCount = 20;
        }
        minWordsCount = questions.getMinWordCount();
        if (minWordsCount == 0) {
            questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
        }
        questionsubans_limittext.setText("");
        String hintStr = getActivity().getResources().getString(R.string.type_here);
        questionsubans_editetext.setHint(hintStr);
        questionsubans_editetext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    questionsubans_editetext.setHint("");
                }
            }
        });
        questionsubans_editetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionsubans_editetext.setHint("");
            }
        });
        questionsubans_limittext.setText(getActivity().getResources().getString(R.string.words_left) + "  " + (maxWordsCount));
        questionsubans_editetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = OustSdkTools.getEmojiEncodedString(questionsubans_editetext);
                String[] words = str.split(" ");
                if (words.length <= maxWordsCount) {
                    if (words.length >= minWordsCount) {
                        questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
                    } else {
                        questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    }
                    questionsubans_limittext.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                    if ((str.isEmpty())) {
                        questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                        questionsubans_limittext.setText(getActivity().getResources().getString(R.string.words_left) + " " + (maxWordsCount));
                    } else {
                        questionsubans_limittext.setText(getActivity().getResources().getString(R.string.words_left) + " " + (maxWordsCount - (words.length)));
                    }
                } else {
                    questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    questionsubans_limittext.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                    questionsubans_limittext.setText(getActivity().getResources().getString(R.string.words_left) + ": 0");
                }
            }
        });
        questionsubans_cardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        questionsubans_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = OustSdkTools.getEmojiEncodedString(questionsubans_editetext);
                String[] words = str.split(" ");
                if ((words.length >= minWordsCount) && (words.length <= maxWordsCount)) {
                    learningModuleInterface.disableBackButton(false);
                    questionsubans_cardlayout.setVisibility(View.GONE);
                    hideKeyboard(questionsubans_editetext);
                    userSubjectiveAns = questionsubans_editetext.getText().toString();
                    addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(questionsubans_editetext));
                    if (isVideoOverlay()) {
                        if (learningModuleInterface != null)
                            learningModuleInterface.setVideoOverlayAnswerAndOc(answer, OustSdkTools.getEmojiEncodedString(questionsubans_editetext), oc, status, 0, cardId);
                    } else {
                        learningModuleInterface.setAnswerAndOc(answer, OustSdkTools.getEmojiEncodedString(questionsubans_editetext), oc, status, 0);
                    }

                    if ((isAssessmentQuestion)) {
                        if (learningModuleInterface != null)
                            if (isVideoOverlay())
                                learningModuleInterface.closeChildFragment();
                            else
                                learningModuleInterface.gotoNextScreen();
                        removeAllData();
                    } else {
                        rightwrongFlipAnimation(status);
                    }
                }
            }
        });
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeAllData() {
        try {
            cancleTimer();
            cancelSound();
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler = null;
            }
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                questionaudio_btn.setAnimation(null);
            }

            if (myFileDownLoadReceiver != null) {
                getActivity().unregisterReceiver(myFileDownLoadReceiver);
                myFileDownLoadReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAllData() {
        try {
            myHandler = null;
            courseCardClass = null;
            mainCourseCardClass = null;
            mediaPlayer = null;
            questions = null;
            questionType = null;
            learningModuleInterface = null;

            learningquiz_mainquestionImage.setImageBitmap(null);
            questionmore_btn.setImageBitmap(null);
            question_arrowback.setImageBitmap(null);
            question_arrowfoword.setImageBitmap(null);
            showsolution_img.setImageBitmap(null);
            match_option_a_left_image.setImageBitmap(null);
            match_option_b_left_image.setImageBitmap(null);
            match_option_c_left_image.setImageBitmap(null);
            match_option_d_left_image.setImageBitmap(null);
            match_option_a_right_image.setImageBitmap(null);
            match_option_b_right_image.setImageBitmap(null);
            match_option_c_right_image.setImageBitmap(null);
            match_option_d_right_image.setImageBitmap(null);
            unfavourite.setImageBitmap(null);
            questionaudio_btn.setImageBitmap(null);
            hotspot_img.setImageBitmap(null);
            System.gc();
        } catch (Exception e) {
        }
    }


    private String userSubjectiveAns = "";

    private void addAnswerOnFirebase(String answer) {
        if ((answer != null)) {
            if (!isAssessmentQuestion) {
                String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
                OustFirebaseTools.getRootRef().child(node).setValue(answer);
            }
        }
    }

    private void clickAnimation(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.96f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.98f);
            scaleDownX.setDuration(ONE_HUNDRED_MILLI_SECONDS);
            scaleDownY.setDuration(ONE_HUNDRED_MILLI_SECONDS);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
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
                    if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FILL_1)) {
                        checkToSubmitWordBank(false);
                    } else {
                        checkToSubmit(false);
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
        }
    }

    private boolean isValidLbResetPeriod() {
        try {
            long courseUniqNo = OustStaticVariableHandling.getInstance().getCourseUniqNo();

            DTOUserCourseData userCourseData = new UserCourseScoreDatabaseHandler().getScoreById(courseUniqNo);
            String lbResetPeriod = OustPreferences.get(AppConstants.StringConstants.LB_RESET_PERIOD);
            lbResetPeriod = lbResetPeriod.toUpperCase();
            String addedOn = userCourseData.getAddedOn();

            switch (lbResetPeriod) {
                case "MONTHLY":
                    return OustSdkTools.isCurrentMonth(addedOn);
                case "QUARTERLY":
                    return OustSdkTools.isCurrentQuater(addedOn);
                case "YEARLY":
                    return OustSdkTools.isCurrentYear(addedOn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

}
