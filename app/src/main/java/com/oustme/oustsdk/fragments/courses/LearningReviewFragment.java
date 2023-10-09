package com.oustme.oustsdk.fragments.courses;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.KATEX_DELIMITER;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.katexview.KatexView;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.bulletinboardquestion.BulletinBoardQuestionActivity;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.customviews.CustomTouchIndicatorClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.QuestionOptionData;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
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
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by admin on 17/05/17.
 */

public class LearningReviewFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, ReadMoreFavouriteCallBack, View.OnClickListener, DialogKeyListener {
    private static final String TAG = "LearningReviewFragment";
    private View learningquiz_animviewa;
    private TextView learningcard_coursename, learningquiz_timertext, learningquiz_mainquestion, cardprogress_text;

    private ProgressBar learningcard_progress;
    private ScrollView mainoption_scrollview;

    private ImageView learningquiz_imagequestion, questionaudio_btn;
    private LinearLayout learningquiz_textchoiselayout, mainoption_layouta, mainoption_layoutb,
            mainoption_layoutc, mainoption_layoutd, mainoption_layoute, mainoption_layoutf;


    private TextView optionA;
    private TextView optionB;
    private TextView optionC;
    private TextView optionD;
    private TextView optionE;
    private TextView optionF;

    private KatexView optionAMaths, optionBMaths, optionCMaths, optionDMaths, optionEMaths, optionFMaths;
    private KatexView learningquiz_mainquestion_maths;

    private ImageView choiceACheckBox, choiceBCheckBox, choiceCCheckBox, choiceDCheckBox, choiceECheckBox, choiceFCheckBox;


    private LinearLayout longanswer_layout;
    private TextView longanswer_text, myresponse_label, solution_label, myresponse_desc, solution_readmore_text;

//--------------

    private LinearLayout learningquiz_imagechoiselayout, mainimageoption_layoutd, mainimageoption_layoutc,
            mainimageoption_layoutb, mainimageoption_layouta, gotonextscreen_mainbtn, learningquiz_bigimagechoiselayout,
            mainbigimageoption_layoutb, mainbigimageoption_layouta;

    private ImageView imageoptionA, imageoptionB, imageoptionC, imageoptionD, choiceimgaeACheckBox, choiceimgaeBCheckBox,
            choiceimgaeCCheckBox, choiceimgaeDCheckBox, choicebigimgaeACheckBox, choicebigimgaeBCheckBox, bigimageoptionA,
            bigimageoptionB;

    private HtmlTextView solution_desc;
    private KatexView solution_desc_maths;

    private RelativeLayout learningquiz_imagequestionlayout;
    private RelativeLayout learningquiz_bigoptionalayout;
    private RelativeLayout learningquiz_bigoptionblayout;
    private RelativeLayout learningquiz_optionalayout;
    private RelativeLayout learningquiz_optionblayout;
    private RelativeLayout learningquiz_optionclayout;
    private RelativeLayout learningquiz_optiondlayout;
    private RelativeLayout solution_readmore;

    private LinearLayout gotopreviousscreen_mainbtn;
    private LinearLayout learningquiz_solutionlayout;

    private ImageView question_arrowback, quiz_backgroundimagea, quiz_backgroundimagea_downloaded, questionmore_btn, question_arrowfoword;

    private TextView fillanswertext, match_option_a_left_text, match_option_b_left_text,
            match_option_c_left_text, match_option_d_left_text, match_option_a_right_text,
            match_option_b_right_text, match_option_c_right_text, match_option_d_right_text;


    private ImageView match_option_a_left_image, match_option_b_left_image, match_option_c_left_image,
            match_option_d_left_image, match_option_a_right_image, match_option_b_right_image, match_option_c_right_image,
            match_option_d_right_image;

    private RelativeLayout match_option_a_left_layout, match_option_b_left_layout, match_option_c_left_layout,
            match_option_d_left_layout, match_option_a_right_layout, match_option_b_right_layout,
            match_option_c_right_layout, match_option_d_right_layout, fill_blanks_layout, matchfollowing_layout, bulletin_layout;

    private ImageView image_expandbtna, bigimage_expandbtnb, bigimage_expandbtna, image_expandbtnb, image_expandbtnc, image_expandbtnd;

    private LinearLayout category_layout;


    private TextView category_lefttext_layout, category_righttext_layout, optionsleft_txtlayout1, optionsleft_txtlayout2,
            optionsleft_txtlayout3, optionsleft_txtlayout4, optionsleft_txtlayout5, optionsleft_txtlayout6,
            optionsleft_txtlayout7, optionsleft_txtlayout8, optionsright_textlayout1, optionsright_textlayout2,
            optionsright_textlayout3, optionsright_textlayout4, optionsright_textlayout5, optionsright_textlayout6,
            optionsright_textlayout7, optionsright_textlayout8, my_response_heading;

    private ImageView optionsleft_imglayout1, optionsleft_imglayout2, optionsleft_imglayout3, optionsleft_imglayout4,
            optionsleft_imglayout5, optionsleft_imglayout6, optionsleft_imglayout7, optionsleft_imglayout8,
            optionsright_imglayout1, optionsright_imglayout2, optionsright_imglayout3, optionsright_imglayout4,
            optionsright_imglayout5, optionsright_imglayout6, optionsright_imglayout7, optionsright_imglayout8;

    private ImageView category_rightimage_layout, category_leftimage_layout;

    private RelativeLayout hotspot_mainlayout, hotspotlabel_layout, category_left_layout, category_right_layout;
    private ImageView hotspot_img;
    private CustomTouchIndicatorClass hotspot_imgtouch_indicator;

    private CustomExoPlayerView customExoPlayerView;

    private boolean hasImageQuestion = false;
    private boolean hasVideoQuestion = false;

    private RelativeLayout quesvideoLayout, video_player_layout;
    private ProgressBar video_loader;

    private boolean isQuesttsEnabled;
    private LayoutInflater mInflater;
    private List<DTOQuestionOptionCategory> categoryData = new ArrayList<>();
    private List<DTOQuestionOption> optionData = new ArrayList<>();

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }

    //------------------

    private LinearLayout mediaupload_reviewlayout;
    private RelativeLayout mediareview_imagelayout;

    private boolean isVideoPaused = false;

//    -------------------

    private Handler myHandler;
    private int scrWidth;
    private String questionType;
    private String cardBackgroundImage;
    private LinearLayout linearLayoutGap;

    private RelativeLayout ref_image;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    private int numberOfCards = 0;

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    private String userResponse;

    public void setuserResponseForAssessment(String userResponse) {
        this.userResponse = userResponse;
    }

    private Scores userAnswerOfQuestionOfAssessment;

    public void setUserAnswerOfAssessment(Scores userAnswerOfQuestionOfAssessment) {
        this.userAnswerOfQuestionOfAssessment = userAnswerOfQuestionOfAssessment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        initViews(view);
        initModuViewFragment();
        return view;
    }

    private void initViews(View view) {
        ref_image = view.findViewById(R.id.ref_image);
        linearLayoutGap = view.findViewById(R.id.linearLayoutGap);
        learningquiz_animviewa = (View) view.findViewById(R.id.learningquiz_animviewa);
        learningcard_coursename = (TextView) view.findViewById(R.id.learningcard_coursename);
        learningquiz_timertext = (TextView) view.findViewById(R.id.learningquiz_timertext);
        learningcard_progress = (ProgressBar) view.findViewById(R.id.learningcard_progress);
        mainoption_scrollview = (ScrollView) view.findViewById(R.id.mainoption_scrollview);
        learningquiz_imagequestion = (ImageView) view.findViewById(R.id.learningquiz_imagequestion);
        learningquiz_imagequestionlayout = (RelativeLayout) view.findViewById(R.id.learningquiz_imagequestionlayout);
        learningquiz_mainquestion = (TextView) view.findViewById(R.id.learningquiz_mainquestion);
        learningquiz_mainquestion_maths = view.findViewById(R.id.learningquiz_mainquestion_maths);
        questionaudio_btn = (ImageView) view.findViewById(R.id.questionaudio_btn);
        learningquiz_textchoiselayout = (LinearLayout) view.findViewById(R.id.learningquiz_textchoiselayout);
        mainoption_layouta = (LinearLayout) view.findViewById(R.id.mainoption_layouta);
        mainoption_layoutb = (LinearLayout) view.findViewById(R.id.mainoption_layoutb);
        mainoption_layoutc = (LinearLayout) view.findViewById(R.id.mainoption_layoutc);
        mainoption_layoutd = (LinearLayout) view.findViewById(R.id.mainoption_layoutd);
        mainoption_layoute = (LinearLayout) view.findViewById(R.id.mainoption_layoute);
        mainoption_layoutf = (LinearLayout) view.findViewById(R.id.mainoption_layoutf);
        optionA = (TextView) view.findViewById(R.id.optionA);
        optionB = (TextView) view.findViewById(R.id.optionB);
        optionC = (TextView) view.findViewById(R.id.optionC);
        optionD = (TextView) view.findViewById(R.id.optionD);
        optionE = (TextView) view.findViewById(R.id.optionE);
        optionF = (TextView) view.findViewById(R.id.optionF);

        optionAMaths = view.findViewById(R.id.optionAMaths);
        optionBMaths = view.findViewById(R.id.optionBMaths);
        optionCMaths = view.findViewById(R.id.optionCMaths);
        optionDMaths = view.findViewById(R.id.optionDMaths);
        optionEMaths = view.findViewById(R.id.optionEMaths);
        optionFMaths = view.findViewById(R.id.optionFMaths);

        choiceACheckBox = (ImageView) view.findViewById(R.id.choiceACheckBox);
        choiceBCheckBox = (ImageView) view.findViewById(R.id.choiceBCheckBox);
        choiceCCheckBox = (ImageView) view.findViewById(R.id.choiceCCheckBox);
        choiceDCheckBox = (ImageView) view.findViewById(R.id.choiceDCheckBox);
        choiceECheckBox = (ImageView) view.findViewById(R.id.choiceECheckBox);
        choiceFCheckBox = (ImageView) view.findViewById(R.id.choiceFCheckBox);
        learningquiz_imagechoiselayout = (LinearLayout) view.findViewById(R.id.learningquiz_imagechoiselayout);
        mainimageoption_layoutd = (LinearLayout) view.findViewById(R.id.mainimageoption_layoutd);
        mainimageoption_layoutc = (LinearLayout) view.findViewById(R.id.mainimageoption_layoutc);
        mainimageoption_layoutb = (LinearLayout) view.findViewById(R.id.mainimageoption_layoutb);
        mainimageoption_layouta = (LinearLayout) view.findViewById(R.id.mainimageoption_layouta);
        imageoptionA = (ImageView) view.findViewById(R.id.imageoptionA);
        imageoptionB = (ImageView) view.findViewById(R.id.imageoptionB);
        imageoptionC = (ImageView) view.findViewById(R.id.imageoptionC);
        imageoptionD = (ImageView) view.findViewById(R.id.imageoptionD);
        choiceimgaeACheckBox = (ImageView) view.findViewById(R.id.choiceimgaeACheckBox);
        choiceimgaeBCheckBox = (ImageView) view.findViewById(R.id.choiceimgaeBCheckBox);
        choiceimgaeCCheckBox = (ImageView) view.findViewById(R.id.choiceimgaeCCheckBox);
        choiceimgaeDCheckBox = (ImageView) view.findViewById(R.id.choiceimgaeDCheckBox);
        gotonextscreen_mainbtn = (LinearLayout) view.findViewById(R.id.gotonextscreen_mainbtn);
        choicebigimgaeACheckBox = (ImageView) view.findViewById(R.id.choicebigimgaeACheckBox);
        choicebigimgaeBCheckBox = (ImageView) view.findViewById(R.id.choicebigimgaeBCheckBox);
        learningquiz_bigimagechoiselayout = (LinearLayout) view.findViewById(R.id.learningquiz_bigimagechoiselayout);
        mainbigimageoption_layoutb = (LinearLayout) view.findViewById(R.id.mainbigimageoption_layoutb);
        mainbigimageoption_layouta = (LinearLayout) view.findViewById(R.id.mainbigimageoption_layouta);
        bigimageoptionA = (ImageView) view.findViewById(R.id.bigimageoptionA);
        bigimageoptionB = (ImageView) view.findViewById(R.id.bigimageoptionB);
        learningquiz_solutionlayout = (LinearLayout) view.findViewById(R.id.learningquiz_solutionlayout);
        solution_desc = (HtmlTextView) view.findViewById(R.id.solution_desc);
        solution_desc_maths = view.findViewById(R.id.solution_desc_maths);
        solution_readmore = (RelativeLayout) view.findViewById(R.id.solution_readmore);
        solution_readmore_text = (TextView) view.findViewById(R.id.solution_readmore_text);
        myresponse_label = (TextView) view.findViewById(R.id.myresponse_label);
        myresponse_desc = (TextView) view.findViewById(R.id.myresponse_desc);
        solution_label = (TextView) view.findViewById(R.id.solution_label);
        learningquiz_bigoptionalayout = (RelativeLayout) view.findViewById(R.id.learningquiz_bigoptionalayout);
        learningquiz_bigoptionblayout = (RelativeLayout) view.findViewById(R.id.learningquiz_bigoptionblayout);
        learningquiz_optionalayout = (RelativeLayout) view.findViewById(R.id.learningquiz_optionalayout);
        learningquiz_optionblayout = (RelativeLayout) view.findViewById(R.id.learningquiz_optionblayout);
        learningquiz_optionclayout = (RelativeLayout) view.findViewById(R.id.learningquiz_optionclayout);
        learningquiz_optiondlayout = (RelativeLayout) view.findViewById(R.id.learningquiz_optiondlayout);
        quiz_backgroundimagea = (ImageView) view.findViewById(R.id.quiz_backgroundimagea);
        quiz_backgroundimagea_downloaded = (ImageView) view.findViewById(R.id.quiz_backgroundimagea_downloaded);
        questionmore_btn = (ImageView) view.findViewById(R.id.questionmore_btn);
        cardprogress_text = (TextView) view.findViewById(R.id.cardprogress_text);
        gotopreviousscreen_mainbtn = (LinearLayout) view.findViewById(R.id.gotopreviousscreen_mainbtn);
        question_arrowback = (ImageView) view.findViewById(R.id.question_arrowback);
        question_arrowfoword = (ImageView) view.findViewById(R.id.question_arrowfoword);
        matchfollowing_layout = (RelativeLayout) view.findViewById(R.id.matchfollowing_layout);
        match_option_a_left_text = (TextView) view.findViewById(R.id.match_option_a_left_text);
        match_option_b_left_text = (TextView) view.findViewById(R.id.match_option_b_left_text);
        match_option_c_left_text = (TextView) view.findViewById(R.id.match_option_c_left_text);
        match_option_d_left_text = (TextView) view.findViewById(R.id.match_option_d_left_text);
        match_option_a_right_text = (TextView) view.findViewById(R.id.match_option_a_right_text);
        match_option_b_right_text = (TextView) view.findViewById(R.id.match_option_b_right_text);
        match_option_c_right_text = (TextView) view.findViewById(R.id.match_option_c_right_text);
        match_option_d_right_text = (TextView) view.findViewById(R.id.match_option_d_right_text);
        match_option_a_left_image = (ImageView) view.findViewById(R.id.match_option_a_left_image);
        match_option_b_left_image = (ImageView) view.findViewById(R.id.match_option_b_left_image);
        match_option_c_left_image = (ImageView) view.findViewById(R.id.match_option_c_left_image);
        match_option_d_left_image = (ImageView) view.findViewById(R.id.match_option_d_left_image);
        match_option_a_right_image = (ImageView) view.findViewById(R.id.match_option_a_right_image);
        match_option_b_right_image = (ImageView) view.findViewById(R.id.match_option_b_right_image);
        match_option_c_right_image = (ImageView) view.findViewById(R.id.match_option_c_right_image);
        match_option_d_right_image = (ImageView) view.findViewById(R.id.match_option_d_right_image);
        match_option_a_left_layout = (RelativeLayout) view.findViewById(R.id.match_option_a_left_layout);
        match_option_b_left_layout = (RelativeLayout) view.findViewById(R.id.match_option_b_left_layout);
        match_option_c_left_layout = (RelativeLayout) view.findViewById(R.id.match_option_c_left_layout);
        match_option_d_left_layout = (RelativeLayout) view.findViewById(R.id.match_option_d_left_layout);
        match_option_a_right_layout = (RelativeLayout) view.findViewById(R.id.match_option_a_right_layout);
        match_option_b_right_layout = (RelativeLayout) view.findViewById(R.id.match_option_b_right_layout);
        match_option_c_right_layout = (RelativeLayout) view.findViewById(R.id.match_option_c_right_layout);
        match_option_d_right_layout = (RelativeLayout) view.findViewById(R.id.match_option_d_right_layout);
        fill_blanks_layout = (RelativeLayout) view.findViewById(R.id.fill_blanks_layout);
        fillanswertext = (TextView) view.findViewById(R.id.fillanswertext);

        mediaupload_reviewlayout = (LinearLayout) view.findViewById(R.id.mediaupload_reviewlayout);
        mediareview_imagelayout = (RelativeLayout) view.findViewById(R.id.mediareview_imagelayout);

        image_expandbtna = (ImageView) view.findViewById(R.id.image_expandbtna);
        bigimage_expandbtnb = (ImageView) view.findViewById(R.id.bigimage_expandbtnb);
        bigimage_expandbtna = (ImageView) view.findViewById(R.id.bigimage_expandbtna);
        image_expandbtnb = (ImageView) view.findViewById(R.id.image_expandbtnb);
        image_expandbtnc = (ImageView) view.findViewById(R.id.image_expandbtnc);
        image_expandbtnd = (ImageView) view.findViewById(R.id.image_expandbtnd);

        category_layout = (LinearLayout) view.findViewById(R.id.category_layout1);
        category_left_layout = (RelativeLayout) view.findViewById(R.id.category_left_layout);
        category_right_layout = (RelativeLayout) view.findViewById(R.id.category_right_layout);

        category_lefttext_layout = (TextView) view.findViewById(R.id.category_lefttext_layout);
        category_righttext_layout = (TextView) view.findViewById(R.id.category_righttext_layout);
        category_leftimage_layout = (ImageView) view.findViewById(R.id.category_leftimage_layout);
        category_rightimage_layout = (ImageView) view.findViewById(R.id.category_rightimage_layout);
        optionsleft_txtlayout1 = (TextView) view.findViewById(R.id.optionsleft_txtlayout1);
        optionsleft_txtlayout2 = (TextView) view.findViewById(R.id.optionsleft_txtlayout2);
        optionsleft_txtlayout3 = (TextView) view.findViewById(R.id.optionsleft_txtlayout3);
        optionsleft_txtlayout4 = (TextView) view.findViewById(R.id.optionsleft_txtlayout4);
        optionsleft_txtlayout5 = (TextView) view.findViewById(R.id.optionsleft_txtlayout5);
        optionsleft_txtlayout6 = (TextView) view.findViewById(R.id.optionsleft_txtlayout6);
        optionsleft_txtlayout7 = (TextView) view.findViewById(R.id.optionsleft_txtlayout7);
        optionsleft_txtlayout8 = (TextView) view.findViewById(R.id.optionsleft_txtlayout8);
        optionsright_textlayout1 = (TextView) view.findViewById(R.id.optionsright_textlayout1);
        optionsright_textlayout2 = (TextView) view.findViewById(R.id.optionsright_textlayout2);
        optionsright_textlayout3 = (TextView) view.findViewById(R.id.optionsright_textlayout3);
        optionsright_textlayout4 = (TextView) view.findViewById(R.id.optionsright_textlayout4);
        optionsright_textlayout5 = (TextView) view.findViewById(R.id.optionsright_textlayout5);
        optionsright_textlayout6 = (TextView) view.findViewById(R.id.optionsright_textlayout6);
        optionsright_textlayout7 = (TextView) view.findViewById(R.id.optionsright_textlayout7);
        optionsright_textlayout8 = (TextView) view.findViewById(R.id.optionsright_textlayout8);

        optionsleft_imglayout1 = (ImageView) view.findViewById(R.id.optionsleft_imglayout1);
        optionsleft_imglayout2 = (ImageView) view.findViewById(R.id.optionsleft_imglayout2);
        optionsleft_imglayout3 = (ImageView) view.findViewById(R.id.optionsleft_imglayout3);
        optionsleft_imglayout4 = (ImageView) view.findViewById(R.id.optionsleft_imglayout4);
        optionsleft_imglayout5 = (ImageView) view.findViewById(R.id.optionsleft_imglayout5);
        optionsleft_imglayout6 = (ImageView) view.findViewById(R.id.optionsleft_imglayout6);
        optionsleft_imglayout7 = (ImageView) view.findViewById(R.id.optionsleft_imglayout7);
        optionsleft_imglayout8 = (ImageView) view.findViewById(R.id.optionsleft_imglayout8);
        optionsright_imglayout1 = (ImageView) view.findViewById(R.id.optionsright_imglayout1);
        optionsright_imglayout2 = (ImageView) view.findViewById(R.id.optionsright_imglayout2);
        optionsright_imglayout3 = (ImageView) view.findViewById(R.id.optionsright_imglayout3);
        optionsright_imglayout4 = (ImageView) view.findViewById(R.id.optionsright_imglayout4);
        optionsright_imglayout5 = (ImageView) view.findViewById(R.id.optionsright_imglayout5);
        optionsright_imglayout6 = (ImageView) view.findViewById(R.id.optionsright_imglayout6);
        optionsright_imglayout7 = (ImageView) view.findViewById(R.id.optionsright_imglayout7);
        optionsright_imglayout8 = (ImageView) view.findViewById(R.id.optionsright_imglayout8);
        bulletin_layout = (RelativeLayout) view.findViewById(R.id.bulletin_layout);

        longanswer_layout = (LinearLayout) view.findViewById(R.id.longanswer_layout);
        longanswer_text = (TextView) view.findViewById(R.id.longanswer_text);

        video_player_layout = view.findViewById(R.id.video_player_layout);
        quesvideoLayout = view.findViewById(R.id.quesvideoLayout_review);
        video_loader = view.findViewById(R.id.video_loader);

        my_response_heading = (TextView) view.findViewById(R.id.my_response_heading);

        hotspot_mainlayout = (RelativeLayout) view.findViewById(R.id.hotspot_mainlayout);
        hotspot_img = (ImageView) view.findViewById(R.id.hotspot_img);
        hotspotlabel_layout = (RelativeLayout) view.findViewById(R.id.hotspotlabel_layout);
        hotspot_imgtouch_indicator = (CustomTouchIndicatorClass) view.findViewById(R.id.hotspot_imgtouch_indicator);
        hotspotindicator_layout = view.findViewById(R.id.hotspotindicator_layout);


        if (!isAssessmentQuestion) {
            OustSdkTools.setImage(quiz_backgroundimagea, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));
        } else {
            learningquiz_animviewa.setBackgroundColor(OustSdkApplication.getContext().getResources().getColor(R.color.popupBackGrounnew));
        }


        image_expandbtna.setOnClickListener(this);
        bigimage_expandbtnb.setOnClickListener(this);
        bigimage_expandbtna.setOnClickListener(this);
        image_expandbtnb.setOnClickListener(this);
        image_expandbtnc.setOnClickListener(this);
        image_expandbtnd.setOnClickListener(this);
        questionmore_btn.setOnClickListener(this);
        gotopreviousscreen_mainbtn.setOnClickListener(this);
        gotonextscreen_mainbtn.setOnClickListener(this);
        questionaudio_btn.setOnClickListener(this);
        bulletin_layout.setOnClickListener(this);

        setFont();

    }

    private void setFont() {
        category_lefttext_layout.setText(OustStrings.getString("category_one"));
        category_righttext_layout.setText(OustStrings.getString("category_two"));
        my_response_heading.setText(OustStrings.getString("my_response"));
        solution_label.setText(OustStrings.getString("solution"));
        myresponse_label.setText(OustStrings.getString("my_response"));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private MediaPlayer mediaPlayer;

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                questionaudio_btn.setAnimation(null);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                questionaudio_btn.setAnimation(null);
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }

            if (customExoPlayerView != null) {
                isVideoPaused = true;
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
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
                isVideoPaused = false;
                resumeVideoPlay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resumeVideoPlay() {
        try {
            if ((questions.getqVideoUrl() != null) && (!questions.getqVideoUrl().isEmpty())) {
                hasVideoQuestion = true;
                setQuestionVideo(questions.getqVideoUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            if (customExoPlayerView != null) {
                isVideoPaused = true;
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }

            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    private int learningcardProgress = 0;

    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    private DTOQuestions questions;

    public void setQuestions(DTOQuestions questions) {
//        this.questions = questions;
    }

    private String cardId;
    private List<FavCardDetails> favCardDetailsList;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList, String cardId) {
        this.favCardDetailsList = favCardDetailsList;
        this.cardId = cardId;
    }

    private boolean isAssessmentQuestion = false;

    public void setAssessmentQuestion(boolean assessmentQuestion) {
        isAssessmentQuestion = assessmentQuestion;
    }

    private String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    private String courseName;

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    private boolean isRMFavourite;

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

    private DTOCourseCard mainCourseCardClass;

    public void setMainCourseCardClass(DTOCourseCard courseCardClass2) {
        try {
            //int savedCardID = (int) courseCardClass2.getCardId();
            //this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardById(savedCardID);
            this.mainCourseCardClass = courseCardClass2;
            if (this.mainCourseCardClass.getXp() == 0) {
                this.mainCourseCardClass.setXp(100);
            }
        } catch (Exception e) {
            this.mainCourseCardClass = courseCardClass2;
        }
        this.questions = this.mainCourseCardClass.getQuestionData();
    }

    public void initModuViewFragment() {
        if (OustAppState.getInstance().getActiveUser() == null) {
            getActivity().finish();
        }
        setViewForAssessmentReview();
        getWidth();
        enableSwipe();
        setQuestionNo();
        setColors();
        setFontStyle();
        setStartingData();
    }

    private void setViewForAssessmentReview() {
        try {
            if (isAssessmentQuestion) {
                bulletin_layout.setVisibility(View.GONE);
                questionmore_btn.setVisibility(View.GONE);
                if (userResponse != null && !userResponse.isEmpty()) {
                    if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))) {
                        longanswer_layout.setVisibility(VISIBLE);
                        longanswer_text.setText(OustSdkTools.getEmojiDecodedString(userResponse));
                    } else {
                        learningquiz_solutionlayout.setVisibility(VISIBLE);
                        myresponse_label.setVisibility(VISIBLE);
                        myresponse_desc.setVisibility(VISIBLE);
                        myresponse_desc.setText(OustSdkTools.getEmojiDecodedString(userResponse));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
    }

    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;

    public void enableSwipe() {
        mainoption_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        float deltaX = x1 - x2;
                        float deltaY = y1 - y2;
                        if (deltaX > 0 && deltaY > 0) {
                            if (deltaX > deltaY) {
                                if (deltaX > MIN_DISTANCE) {
                                    learningModuleInterface.gotoNextScreen();
                                }
                            }
                        } else if (deltaX < 0 && deltaY > 0) {
                            if ((-deltaX) > deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    learningModuleInterface.gotoPreviousScreen();
                                }
                            }

                        } else if (deltaX < 0 && deltaY < 0) {
                            if (deltaX < deltaY) {
                                if ((-deltaX) > MIN_DISTANCE) {
                                    learningModuleInterface.gotoPreviousScreen();
                                }
                            }
                        } else if (deltaX > 0 && deltaY < 0) {
                            if (deltaX > (-deltaY)) {
                                if (deltaX > MIN_DISTANCE) {
                                    learningModuleInterface.gotoNextScreen();
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void setQuestionNo() {
        Log.d(TAG, "setQuestionNo: ");
        try {
            if (learningcardProgress == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            }
            if (courseLevelClass.getCourseCardClassList() != null) {
                if (learningcardProgress == (courseLevelClass.getCourseCardClassList().size()) - 1) {
                    gotonextscreen_mainbtn.setVisibility(View.GONE);
                }
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + courseLevelClass.getCourseCardClassList().size());
                learningcard_progress.setMax((courseLevelClass.getCourseCardClassList().size() * 50));
                learningcard_progress.setProgress((learningcardProgress * 50));
                ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
                animation.setDuration(600);
                animation.setStartDelay(500);
                animation.start();
            } else {
                if (learningcardProgress == (numberOfCards) - 1) {
                    gotonextscreen_mainbtn.setVisibility(View.GONE);
                }
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + numberOfCards);
                learningcard_progress.setMax((numberOfCards * 50));
                learningcard_progress.setProgress((learningcardProgress * 50));
                ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
                animation.setDuration(600);
                animation.setStartDelay(500);
                animation.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = mainCourseCardClass.getCardColorScheme();
            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    questionaudio_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowback.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    question_arrowfoword.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                }
                if ((cardColorScheme.getLevelNameColor() != null) && (!cardColorScheme.getLevelNameColor().isEmpty())) {
                    learningquiz_timertext.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    learningcard_coursename.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                    cardprogress_text.setTextColor(Color.parseColor(cardColorScheme.getLevelNameColor()));
                }
                if ((cardColorScheme.getTitleColor() != null) && (!cardColorScheme.getTitleColor().isEmpty())) {
                    learningquiz_timertext.setTextColor(Color.parseColor(cardColorScheme.getTitleColor()));
                }
                if ((cardColorScheme.getOptionColor() != null) && (!cardColorScheme.getOptionColor().isEmpty())) {
                    optionA.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
                    optionB.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
                    optionC.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
                    optionD.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
                    optionE.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
                    optionF.setTextColor(Color.parseColor(cardColorScheme.getOptionColor()));
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
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setFontStyle() {
        if ((mainCourseCardClass.getLanguage() != null) && (mainCourseCardClass.getLanguage().equals("en"))) {
            learningcard_coursename.setTypeface(OustSdkTools.getAvenirLTStdHeavy());
            learningquiz_mainquestion.setTypeface(OustSdkTools.getAvenirLTStdHeavy());

            optionA.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            optionB.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            optionC.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            optionD.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            optionE.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            optionF.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            solution_desc.setTypeface(OustSdkTools.getAvenirLTStdMedium());
            fillanswertext.setTypeface(OustSdkTools.getAvenirLTStdMedium());
        }
    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.bg_1));
            if (bgImageUrl != null && !bgImageUrl.isEmpty()) {
                quiz_backgroundimagea_downloaded.setVisibility(VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(bgImageUrl).into(quiz_backgroundimagea_downloaded);
                } else {
                    Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(quiz_backgroundimagea_downloaded);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    //=========================================================================
    private void setStartingData() {
        try {
            //startQuestionNOHideAnimation(waitTimer);
            if (questions != null) {
                setTitle();
                setQuestionTitle();
                setQuestionType();
                Log.d(TAG, "setStartingData: " + questions.getQuestion());

                Log.d(TAG, "setStartingData: not form type");
                if ((questions.getqVideoUrl() != null) && (!questions.getqVideoUrl().isEmpty())) {
                    hasVideoQuestion = true;
                    setQuestionVideo(questions.getqVideoUrl());
                } else {
                    loadQuestion();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "setStartingData: exception");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void loadQuestion() {
        try {
            if (questions != null) {
                setTitle();
                setQuestionType();
                if ((questionType.equals(QuestionType.FILL)) || (questionType.equals(QuestionType.FILL_1))) {

                } else {
                    setQuestionTitle();
                }
                if ((questions.getImage() != null) && (!questions.getImage().isEmpty())) {
                    setImageQuestionImage();
                }
                if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE))) {
                    if ((questions.getImageChoiceC() != null)) {
                        showImageOptions();
                    } else {
                        showBigImageOptions();
                        if (questionType.equals(QuestionType.MRQ)) {
                            choicebigimgaeACheckBox.setVisibility(VISIBLE);
                            choicebigimgaeBCheckBox.setVisibility(VISIBLE);
                        }
                    }
                    setSubjectiveAnswer(false);
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.MATCH))) {
                    setSubjectiveAnswer(false);
                    setMatchQuestionLayoutSize();
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.HOTSPOT))) {

                    learningquiz_imagequestion.setVisibility(View.GONE);
                    learningquiz_imagequestionlayout.setVisibility(View.GONE);
                    setSubjectiveAnswer(false);
                    startHotspotQuestion();
                    learningquiz_mainquestion.setVisibility(View.GONE);
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.CATEGORY))) {
                    setSubjectiveAnswer(false);
                    setCategoryLayout();
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER))) {
                    setSubjectiveAnswer(true);
                } else if (questionType.equals(QuestionType.FILL) || questionType.equals(QuestionType.FILL_1)) {
                    setSubjectiveAnswer(false);
                    setFillLayout();
                } else if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_I))) {
                    setSubjectiveAnswer(false);
                    getDataFromFirebase();
                    showMediaUploadImage();
                } else if ((questions.getQuestionCategory() != null) && ((questions.getQuestionCategory().equals(QuestionCategory.USR_REC_A)) || (questions.getQuestionCategory().equals(QuestionCategory.USR_REC_V)))) {
                    setSubjectiveAnswer(false);
                    getDataFromFirebase();
                    showAudioVid();
                } else {
                    setSubjectiveAnswer(false);
                    showTextOptions();
                }
                showSolution();
                startSpeakQuestion();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setQuestionVideo(String path) {
        quesvideoLayout.setVisibility(View.VISIBLE);
        video_player_layout.setVisibility(View.VISIBLE);
        hotspot_mainlayout.setVisibility(View.GONE);

        if (learningModuleInterface != null) {
            learningModuleInterface.changeOrientationUnSpecific();
        }
        OustSdkTools.showToast(getResources().getString(R.string.assessment_video_start_msg));

        AWSMobileClient.getInstance().initialize(getActivity(), new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        customExoPlayerView = new CustomExoPlayerView() {
            @Override
            public void onAudioComplete() {

            }

            @Override
            public void onVideoComplete() {
                loadQuestion();
                notifyActivityToStartTimer();
            }

            @Override
            public void onBuffering() {
                video_loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoError() {
                video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onPlayReady() {
                video_loader.setVisibility(View.GONE);
            }

        };
        customExoPlayerView.initExoPlayer(video_player_layout, requireActivity(), path);
        setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
    }

    private void notifyActivityToStartTimer() {
        if (isAssessmentQuestion) {
            try {
                CustomVideoControlListener customVideoControlListener = (CustomVideoControlListener) requireActivity();
                customVideoControlListener.onVideoEnd();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    public void setPortaitVideoRatio(StyledPlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                float h = (scrWidth * 0.60f);
                params.width = scrWidth;
                params.height = (int) h;
                video_player_layout.setLayoutParams(params);
                simpleExoPlayerView.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            if (customExoPlayerView != null || customExoPlayerView.getSimpleExoPlayerView() != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setLandscapeVideoRation(customExoPlayerView.getSimpleExoPlayerView());
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
                }
            } else
                super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLandscapeVideoRation(StyledPlayerView simpleExoPlayerView) {
        try {
            if (simpleExoPlayerView != null) {
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
                params.height = scrHeight;
                params.width = scrWidth;
                simpleExoPlayerView.setLayoutParams(params);
                video_player_layout.setLayoutParams(params);

                ((CustomVideoControlListener) OustSdkApplication.getContext()).hideToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showAudioVid() {
        learningquiz_textchoiselayout.setVisibility(View.GONE);

    }

    //    ================================================================================================

    private List<String> answerStrs = new ArrayList<>();
    public int totalOption = 0;
    private List<View> fill_views = new ArrayList<>();
    private List<OustFillData> emptyViews = new ArrayList<>();
    private List<String> realAnsStrs = new ArrayList<>();
    private List<OustFillData> answerView = new ArrayList<>();
    private List<PointF> fillAnswersPoint = new ArrayList<>();
    private int maxlength = 0;

    private void setFillLayout() {
        try {
            fill_blanks_layout.setVisibility(VISIBLE);
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
//            String[] options = new String[questions.getAnswer().split("#").length];
//            options = questions.getAnswer().split("#");
            String[] options = questions.getFillAnswers().toArray(new String[questions.getFillAnswers().size()]);
            Collections.shuffle(Arrays.asList(options));

            for (int i = 0; i < options.length; i++) {
                Spanned s3 = getSpannedContent(options[i]);
                answerStrs.add(s3.toString().trim());
                if (maxlength < s3.toString().trim().length()) {
                    maxlength = s3.toString().length();
                }
                totalOption++;
            }
//            if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
//                Spanned s3 = getSpannedContent(questions.getA());
//                answerStrs.add(s3.toString().trim());
//                maxlength=s3.toString().length();
//                totalOption++;
//            }
//            if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
//                Spanned s3 = getSpannedContent(questions.getB());
//                answerStrs.add(s3.toString().trim());
//                if(maxlength<s3.toString().trim().length()){
//                    maxlength=s3.toString().length();
//                }
//                totalOption++;
//            }
//            if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
//                Spanned s3 = getSpannedContent(questions.getC());
//                answerStrs.add(s3.toString().trim());
//                if(maxlength<s3.toString().trim().length()){
//                    maxlength=s3.toString().length();
//                }
//                totalOption++;
//            }
//            if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
//                Spanned s3 = getSpannedContent(questions.getD());
//                answerStrs.add(s3.toString().trim());
//                if(maxlength<s3.toString().trim().length()){
//                    maxlength=s3.toString().length();
//                }
//                totalOption++;
//            }
//            if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
//                Spanned s3 = getSpannedContent(questions.getE());
//                answerStrs.add(s3.toString().trim());
//                if(maxlength<s3.toString().trim().length()){
//                    maxlength=s3.toString().length();
//                }
//                totalOption++;
//            }

            String dummyStr = "";
            for (int j = 0; j < maxlength; j++) {
                dummyStr += "m";
            }
            int n1 = 0;
            int fillIndex = 1;
            int length = 0;
            int x = 0;
            int y = 0;
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen70);

            while (n1 < strings.length) {
                length += strings[n1].length();
                if (strings[n1].contains("____")) {
                    if (strings.length <= ansStrs.length) {
                        if (ansStrs.length > n1) {
                            realAnsStrs.add(ansStrs[n1]);
                        }
                    } else {
                        if (ansStrs.length > 0) {
                            realAnsStrs.add(ansStrs[0]);
                        }
                    }
                    View fillTextView1 = mInflater.inflate(R.layout.fill_emptylayout, null);
                    fillTextView1.setId(100 + fillIndex);
                    fillIndex++;
//                    RelativeLayout fill_mainlayout = (RelativeLayout) fillTextView1.findViewById(R.id.fill_mainlayout);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fill_mainlayout.getLayoutParams();
//                    params.width = (int) ((float) (scrWidth * 0.40));
//                    fill_mainlayout.setLayoutParams(params);

                    TextView fill_mainlayoutb = (TextView) fillTextView1.findViewById(R.id.fill_mainlayoutb);
                    fill_mainlayoutb.setText(dummyStr);
                    fill_mainlayoutb.setTextColor(Color.TRANSPARENT);
                    fill_mainlayoutb.setBackground(null);
                    fill_mainlayoutb.setBackgroundColor(Color.TRANSPARENT);
//                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) fill_mainlayoutb.getLayoutParams();
//                    params1.width = (int) ((float) (scrWidth * 0.40));
//                    fill_mainlayoutb.setLayoutParams(params1);

                    fill_views.add(fillTextView1);
                    OustFillData oustFillData = new OustFillData();
                    oustFillData.setView(fillTextView1);
                    oustFillData.setIndex(n1);
                    emptyViews.add(oustFillData);
                } else {
                    View fillTextView = mInflater.inflate(R.layout.fill_text_layout, null);
                    TextView textView1 = (TextView) fillTextView.findViewById(R.id.fill_text);
                    textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView1.setText(" " + strings[n1]);
                    fill_views.add(fillTextView);
                }
                n1++;
            }
            x = 20;
            y = (int) getResources().getDimension(R.dimen.oustlayout_dimen30);
            for (int i = 0; i < fill_views.size(); i++) {
                View view = fill_views.get(i);
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int totalW = metrics.widthPixels - 10;
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = 20;
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(x);
                    view.setY(y);
                }
                x += viewW;
                fill_blanks_layout.addView(view);
            }

            for (int j = 0; j < answerStrs.size(); j++) {
                View fillTextView = mInflater.inflate(R.layout.fill_textanswer_layout, null);
                fillTextView.setId(1000 + j);
//                    fillTextView.setPadding(8,8,8,8);
                TextView textView1 = (TextView) fillTextView.findViewById(R.id.fill_text);
                TextView index_text = (TextView) fillTextView.findViewById(R.id.index_text);
                index_text.setText("" + j);
                textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                textView1.setText(answerStrs.get(j));
                RelativeLayout fill_answerback = (RelativeLayout) fillTextView.findViewById(R.id.fill_answerback);
//                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) fill_answerback.getLayoutParams();
//                params1.width = (int) ((float) (scrWidth * 0.40));
//                fill_answerback.setLayoutParams(fill_width);
                TextView dummytext = (TextView) fillTextView.findViewById(R.id.dummytext);
                dummytext.setText(dummyStr);
                OustSdkTools.setLayoutBackgroud(fill_answerback, R.drawable.fill_wrong);
                OustFillData oustFillData = new OustFillData();
                oustFillData.setView(fillTextView);
                oustFillData.setIndex(j);
                answerView.add(oustFillData);
            }
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen80);
            x = (int) ((float) (0.02 * scrWidth));
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int totalW = metrics.widthPixels - 10;
            fillAnswersPoint = new ArrayList<>();
            for (int i = 0; i < answerView.size(); i++) {
                View view = answerView.get(i).getView();
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int viewW = view.getMeasuredWidth();
                if ((x + viewW) < totalW) {
                    view.setX(x);
                    view.setY(y);
                } else {
                    x = (int) ((float) (0.02 * scrWidth));
                    y += (int) getResources().getDimension(R.dimen.oustlayout_dimen50);
                    view.setX(x);
                    view.setY(y);
                }
                fillAnswersPoint.add(new PointF(x, y));
                x += viewW + ((int) ((float) (0.01 * scrWidth)));
                fill_blanks_layout.addView(view);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fill_blanks_layout.getLayoutParams();
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen80);
            layoutParams.height = y;
            fill_blanks_layout.setLayoutParams(layoutParams);
            setViews(dummyStr);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setViews(String dummyStr) {
        for (int idx = 0; idx < emptyViews.size(); idx++) {
            for (int i = 0; i < answerStrs.size(); i++) {
//                if(answerStrs.get(i).equalsIgnoreCase(questions.getFillAnswers().get(idx))) {
                if ((questions.getFillAnswers().get(idx).trim()).equalsIgnoreCase((answerStrs.get(i)).trim())) {
                    if (answerView.get(i).getView() != null) {
                        RelativeLayout fill_answerback = (RelativeLayout) answerView.get(i).getView().findViewById(R.id.fill_answerback);
//                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) fill_answerback.getLayoutParams();
//                        params1.width = (int) ((float) (scrWidth * 0.45));
//                        fill_answerback.setLayoutParams(params1);
                        TextView dummytext = (TextView) answerView.get(i).getView().findViewById(R.id.dummytext);
                        dummytext.setText(dummyStr);
                        OustSdkTools.setLayoutBackgroud(fill_answerback, R.drawable.fill_right);
                        answerView.get(i).getView().setX((int) emptyViews.get(idx).getView().getX());
                        answerView.get(i).getView().setY((int) emptyViews.get(idx).getView().getY() - 2);
                        answerView.get(i).setView(null);
                        break;
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    private void setSubjectiveAnswer(final boolean isLongAnswer) {
        try {
            if (courseId != null && !courseId.isEmpty()) {
                String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
                ValueEventListener answerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                String longanswerStr = (String) dataSnapshot.getValue();
                                if ((longanswerStr != null) && (!longanswerStr.isEmpty())) {
                                    if (isLongAnswer) {
                                        longanswer_layout.setVisibility(VISIBLE);
                                        longanswer_text.setText(OustSdkTools.getEmojiDecodedString(longanswerStr));
                                    } else {
                                        learningquiz_solutionlayout.setVisibility(VISIBLE);
                                        myresponse_label.setVisibility(VISIBLE);
                                        myresponse_desc.setVisibility(VISIBLE);
                                        myresponse_desc.setText(OustSdkTools.getEmojiDecodedString(longanswerStr));
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                };
                OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(answerListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void showTextOptions() {
        try {
            learningquiz_textchoiselayout.setVisibility(VISIBLE);
            if ((questions.getA() != null) && (!questions.getA().isEmpty()) && (!questions.getA().equalsIgnoreCase("dont know"))) {
                if (questions.getA().contains(KATEX_DELIMITER)) {
                    optionAMaths.setText(questions.getA());
                    optionA.setVisibility(View.GONE);
                    optionAMaths.setVisibility(VISIBLE);
                } else {
                    optionA.setVisibility(VISIBLE);
                    optionAMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getA(), optionA);
                }
                if (questionType.equals(QuestionType.MCQ) || (questionType.equals(QuestionType.TRUE_FALSE))) {
                    if ((questions.getA() != null) && (questions.getAnswer() != null) && (questions.getA().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getA() != null) && (questions.getA().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getA() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("A")) || (questions.getAnswer().contains("a")))) {
                        choiceACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getA() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("A") || userAnswerOfQuestionOfAssessment.getAnswer().contains("a"))) {
                            choiceACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layouta.setVisibility(VISIBLE);
            }
            if ((questions.getB() != null) && (!questions.getB().isEmpty()) && (!questions.getB().equalsIgnoreCase("dont know"))) {
                if (questions.getB().contains(KATEX_DELIMITER)) {
                    optionBMaths.setText(questions.getB());
                    optionB.setVisibility(View.GONE);
                    optionBMaths.setVisibility(VISIBLE);
                } else {
                    optionB.setVisibility(VISIBLE);
                    optionBMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getB(), optionB);
                }
                if (questionType.equals(QuestionType.MCQ) || questionType.equals(QuestionType.TRUE_FALSE)) {
                    if ((questions.getB() != null) && (questions.getAnswer() != null) && (questions.getB().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getB() != null) && (questions.getB().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getB() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("B")) || (questions.getAnswer().contains("b")))) {
                        choiceBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getB() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("B") || userAnswerOfQuestionOfAssessment.getAnswer().contains("b"))) {
                            choiceBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layoutb.setVisibility(VISIBLE);
            }
            if ((questions.getC() != null) && (!questions.getC().isEmpty()) && (!questions.getC().equalsIgnoreCase("dont know"))) {
                if (questions.getC().contains(KATEX_DELIMITER)) {
                    optionCMaths.setText(questions.getC());
                    optionC.setVisibility(View.GONE);
                    optionCMaths.setVisibility(VISIBLE);
                } else {
                    optionC.setVisibility(VISIBLE);
                    optionCMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getC(), optionC);
                }
                if (questionType.equals(QuestionType.MCQ) || questionType.equals(QuestionType.TRUE_FALSE)) {
                    if ((questions.getC() != null) && (questions.getAnswer() != null) && (questions.getC().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getC() != null) && (questions.getC().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getC() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("C")) || (questions.getAnswer().contains("c")))) {
                        choiceCCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceCCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getC() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("C") || userAnswerOfQuestionOfAssessment.getAnswer().contains("c"))) {
                            choiceCCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceCCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layoutc.setVisibility(VISIBLE);
            }
            if ((questions.getD() != null) && (!questions.getD().isEmpty()) && (!questions.getD().equalsIgnoreCase("dont know"))) {
                if (questions.getD().contains(KATEX_DELIMITER)) {
                    optionDMaths.setText(questions.getD());
                    optionD.setVisibility(View.GONE);
                    optionDMaths.setVisibility(VISIBLE);
                } else {
                    optionD.setVisibility(VISIBLE);
                    optionDMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getD(), optionD);
                }
                if (questionType.equals(QuestionType.MCQ) || questionType.equals(QuestionType.TRUE_FALSE)) {
                    if ((questions.getD() != null) && (questions.getAnswer() != null) && (questions.getD().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getD() != null) && (questions.getD().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getD() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("D")) || (questions.getAnswer().contains("d")))) {
                        choiceDCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceDCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getD() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("D") || userAnswerOfQuestionOfAssessment.getAnswer().contains("d"))) {
                            choiceDCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceDCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layoutd.setVisibility(VISIBLE);
            }
            if ((questions.getE() != null) && (!questions.getE().isEmpty()) && (!questions.getE().equalsIgnoreCase("dont know"))) {
                if (questions.getE().contains(KATEX_DELIMITER)) {
                    optionEMaths.setText(questions.getE());
                    optionE.setVisibility(View.GONE);
                    optionEMaths.setVisibility(VISIBLE);
                } else {
                    optionE.setVisibility(VISIBLE);
                    optionEMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getE(), optionE);
                }
                if (questionType.equals(QuestionType.MCQ) || questionType.equals(QuestionType.TRUE_FALSE)) {
                    if ((questions.getE() != null) && (questions.getAnswer() != null) && (questions.getE().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoute, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getE() != null) && (questions.getE().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoute, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getE() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("E")) || (questions.getAnswer().contains("e")))) {
                        choiceECheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceECheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoute, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getE() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("E") || userAnswerOfQuestionOfAssessment.getAnswer().contains("e"))) {
                            choiceECheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceECheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoute, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layoute.setVisibility(VISIBLE);
            }
            if ((questions.getF() != null) && (!questions.getF().isEmpty()) && (!questions.getF().equalsIgnoreCase("dont know"))) {
                if (questions.getD().contains(KATEX_DELIMITER)) {
                    optionFMaths.setText(questions.getF());
                    optionF.setVisibility(View.GONE);
                    optionFMaths.setVisibility(VISIBLE);
                } else {
                    optionF.setVisibility(VISIBLE);
                    optionFMaths.setVisibility(View.GONE);
                    OustSdkTools.getSpannedContent(questions.getF(), optionF);
                }
                if (questionType.equals(QuestionType.MCQ) || questionType.equals(QuestionType.TRUE_FALSE)) {
                    if ((questions.getF() != null) && (questions.getAnswer() != null) && (questions.getF().equals(questions.getAnswer()))) {
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutf, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getF() != null) && (questions.getF().equals(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutf, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getF() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("F")) || (questions.getAnswer().contains("f")))) {
                        choiceFCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceFCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainoption_layoutf, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getF() != null) && (userAnswerOfQuestionOfAssessment.getAnswer().contains("F") || userAnswerOfQuestionOfAssessment.getAnswer().contains("f"))) {
                            choiceFCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceFCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutf, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
                mainoption_layoutf.setVisibility(VISIBLE);
            }
            if (questionType.equals(QuestionType.MRQ)) {
                hideAbcOption();
            } else {
                hideMrqOptions();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //-----------------------------------------------------------------------------------------------------
    private void setTitle() {
        try {
            if ((courseLevelClass.getLevelName() != null)) {
                learningcard_coursename.setText(courseLevelClass.getLevelName().trim());
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setQuestionTitle() {
        try {
            if ((questions.getQuestion() != null)) {
                if (questions.getQuestion().contains(KATEX_DELIMITER)) {
                    learningquiz_mainquestion_maths.setText(questions.getQuestion());
                    learningquiz_mainquestion_maths.setVisibility(VISIBLE);
                    learningquiz_mainquestion.setVisibility(View.GONE);
                } else {
                    OustSdkTools.getSpannedContent(questions.getQuestion(), learningquiz_mainquestion);
                    learningquiz_mainquestion.setVisibility(VISIBLE);
                    learningquiz_mainquestion_maths.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private void startSpeakQuestion() {
        try {
            if ((questions.getQuestion() != null) && !isAssessmentQuestion) {
                if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String audioPath = questions.getAudio();
                                String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                playDownloadedAudioQues("oustlearn_" + s3AudioFileName);
                            } catch (Exception e) {
                            }
                        }
                    }, 1000);
                } else {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    createStringfor_speech();
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                        }, 1000);
                    } else {
                        questionaudio_btn.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean musicComplete = false;

    private void playDownloadedAudioQues(final String filename) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer = new MediaPlayer();
                    File audiofile = new File(OustSdkApplication.getContext().getFilesDir(), filename);
                    if ((audiofile != null) && (audiofile.exists())) {
                        musicComplete = false;
//                        byte[] audBytes = Base64.decode(audStr, 0);
//                        // create temp file that will hold byte array
//                        File tempMp3 = File.createTempFile(filename, null, getActivity().getCacheDir());
//                        tempMp3.deleteOnExit();
//                        FileOutputStream fos = new FileOutputStream(tempMp3);
//                        fos.write(audBytes);
//                        fos.close();
                        questionaudio_btn.setVisibility(VISIBLE);

                        // resetting mediaplayer instance to evade problems
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
                    //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
                }
            }
        });
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            while (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
        }
        Spanned s1 = Html.fromHtml(s2, null, new OustTagHandler());
        return s1;
    }

    private void setQuestionType() {
        try {
            questionType = questions.getQuestionType();
            if (questionType == null) {
                questionType = QuestionType.MRQ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageQuestionImage() {
        try {
            String url = questions.getImageCDNPath();
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH image question");
            if (!url.contains("oustlearn_")) {
                url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                learningquiz_imagequestion.setImageURI(uri);
                learningquiz_imagequestion.setVisibility(VISIBLE);
                learningquiz_imagequestionlayout.setVisibility(VISIBLE);
                learningquiz_imagechoiselayout.setVisibility(VISIBLE);
            } else {
                String str = questions.getImage();
                if ((str != null) && (!str.isEmpty())) {
                    byte[] imageByte = Base64.decode(str, 0);
                    GifDrawable gifFromBytes = new GifDrawable(imageByte);
                    learningquiz_imagequestion.setImageDrawable(gifFromBytes);
                    learningquiz_imagequestion.setVisibility(VISIBLE);
                    learningquiz_imagequestionlayout.setVisibility(VISIBLE);
                    learningquiz_imagechoiselayout.setVisibility(VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            setImageQuestionImageA();
        }
    }


    public void setImageQuestionImageA() {
        try {
            String url = questions.getImageCDNPath();
            if (url != null) {
                url = OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH omage optionA");
            if (!url.contains("oustlearn_")) {
                url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                learningquiz_imagequestion.setImageURI(uri);
            } else {
                String str = questions.getImage();
                if ((str != null) && (!str.isEmpty())) {
                    byte[] imageByte = Base64.decode(str, 0);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    learningquiz_imagequestion.setVisibility(VISIBLE);
                    learningquiz_imagequestion.setImageBitmap(decodedByte);
                    learningquiz_imagequestionlayout.setVisibility(VISIBLE);
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private void hideAbcOption() {
        try {
            choiceACheckBox.setVisibility(VISIBLE);
            choiceBCheckBox.setVisibility(VISIBLE);
            choiceCCheckBox.setVisibility(VISIBLE);
            choiceDCheckBox.setVisibility(VISIBLE);
            choiceECheckBox.setVisibility(VISIBLE);
            choiceFCheckBox.setVisibility(VISIBLE);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void hideMrqOptions() {
        try {
            choiceACheckBox.setVisibility(View.GONE);
            choiceBCheckBox.setVisibility(View.GONE);
            choiceCCheckBox.setVisibility(View.GONE);
            choiceDCheckBox.setVisibility(View.GONE);
            choiceECheckBox.setVisibility(View.GONE);
            choiceFCheckBox.setVisibility(View.GONE);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }

    }

    private void showImageOptions() {
        try {
            learningquiz_imagechoiselayout.setVisibility(VISIBLE);

            if ((questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null))
                if ((questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null)) {
                    setLayoutAspectRatiosmall(learningquiz_optionalayout);
                    if (questions.getImageChoiceA().getImageData() != null) {
                        setImageOptionUrl(questions.getImageChoiceA().getImageData(), imageoptionA);
                    } else {
                        setImageOption(questions.getImageChoiceA().getImageData(), imageoptionA);
                    }
                    mainimageoption_layouta.setVisibility(VISIBLE);

                    if (questionType.equals(QuestionType.MCQ)) {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                                && (questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layouta, R.drawable.learning_rightanswer_background);
                        } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                                && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                            if ((questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                                OustSdkTools.setLayoutBackgrouda(mainimageoption_layouta, R.drawable.learningoption_disablebackground);
                            }
                        }
                    } else if (questionType.equals(QuestionType.MRQ)) {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("A"))
                                        || (questions.getImageChoiceAnswer().getImageFileName().contains("a")))) {
                            choiceimgaeACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceimgaeACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layouta, R.drawable.learning_rightanswer_background);
                        } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                                && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                            if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("A"))
                                    || (userAnswerOfQuestionOfAssessment.getAnswer().contains("a"))) {
                                choiceimgaeACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                                choiceimgaeACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                                OustSdkTools.setLayoutBackgrouda(mainimageoption_layouta, R.drawable.learningoption_disablebackground);

                            }
                        }
                    }
                }

            if ((questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData() != null)) {
                setLayoutAspectRatiosmall(learningquiz_optionblayout);
                if (questions.getImageChoiceB().getImageData() != null) {
                    setImageOptionUrl(questions.getImageChoiceB().getImageData(), imageoptionB);
                } else {
                    setImageOption(questions.getImageChoiceB().getImageData(), imageoptionB);
                }
                mainimageoption_layoutb.setVisibility(VISIBLE);
                if (questionType.equals(QuestionType.MCQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                            && (questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutb, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                            ((questions.getImageChoiceAnswer().getImageFileName().contains("B"))
                                    || (questions.getImageChoiceAnswer().getImageFileName().contains("b")))) {
                        choiceimgaeBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceimgaeBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null && userAnswerOfQuestionOfAssessment.getAnswer() != null
                            && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("B"))
                                || (userAnswerOfQuestionOfAssessment.getAnswer().contains("b"))) {
                            choiceimgaeBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceimgaeBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutb, R.drawable.learningoption_disablebackground);

                        }
                    }
                }
            }

            if ((questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData() != null)) {
                setLayoutAspectRatiosmall(learningquiz_optionclayout);
                if (questions.getImageChoiceC().getImageData() != null && !questions.getImageChoiceC().getImageData().isEmpty()) {
                    if (questions.getImageChoiceC().getImageData() != null) {
                        setImageOptionUrl(questions.getImageChoiceC().getImageData(), imageoptionC);
                    } else {
                        setImageOption(questions.getImageChoiceC().getImageData(), imageoptionC);
                    }
                    mainimageoption_layoutc.setVisibility(VISIBLE);
                }
                if (questionType.equals(QuestionType.MCQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                            && (questions.getImageChoiceC().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutc, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getImageChoiceC().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutc, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                            ((questions.getImageChoiceAnswer().getImageFileName().contains("C"))
                                    || (questions.getImageChoiceAnswer().getImageFileName().contains("c")))) {
                        choiceimgaeCCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceimgaeCCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutc, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("C"))
                                || (userAnswerOfQuestionOfAssessment.getAnswer().contains("c"))) {
                            choiceimgaeCCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceimgaeCCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutc, R.drawable.learningoption_disablebackground);

                        }
                    }
                }
            }

            if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData() != null) {
                setLayoutAspectRatiosmall(learningquiz_optiondlayout);
                if (questions.getImageChoiceD().getImageData() != null && !questions.getImageChoiceD().getImageData().isEmpty()) {
                    if (questions.getImageChoiceD().getImageData() != null) {
                        setImageOptionUrl(questions.getImageChoiceD().getImageData(), imageoptionD);
                    } else {
                        setImageOption(questions.getImageChoiceD().getImageData(), imageoptionD);
                    }
                    mainimageoption_layoutd.setVisibility(VISIBLE);
                }
                if (questionType.equals(QuestionType.MCQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                            && (questions.getImageChoiceD().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutd, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getImageChoiceD().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutd, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                            ((questions.getImageChoiceAnswer().getImageFileName().contains("D"))
                                    || (questions.getImageChoiceAnswer().getImageFileName().contains("d")))) {
                        choiceimgaeDCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choiceimgaeDCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutd, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("D"))
                                || (userAnswerOfQuestionOfAssessment.getAnswer().contains("d"))) {
                            choiceimgaeDCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choiceimgaeDCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutd, R.drawable.learningoption_disablebackground);

                        }
                    }
                }
            }

            if (questionType.equals(QuestionType.MRQ)) {
                showMrqImageOption();
            }

        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void showMrqImageOption() {
        try {
            choiceimgaeACheckBox.setVisibility(VISIBLE);
            choiceimgaeBCheckBox.setVisibility(VISIBLE);
            choiceimgaeCCheckBox.setVisibility(VISIBLE);
            choiceimgaeDCheckBox.setVisibility(VISIBLE);
        } catch (Exception e) {
        }
    }

    private void showBigImageOptions() {
        try {
            learningquiz_bigimagechoiselayout.setVisibility(VISIBLE);

            if ((questions.getImageChoiceA() != null)) {
                setLayoutAspectRatio(learningquiz_bigoptionalayout);
                setImageOption(questions.getImageChoiceA().getImageData(), bigimageoptionA);
                mainbigimageoption_layouta.setVisibility(VISIBLE);
                if (questionType.equals(QuestionType.MCQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                            && (questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layouta, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layouta, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                            ((questions.getImageChoiceAnswer().getImageFileName().contains("A"))
                                    || (questions.getImageChoiceAnswer().getImageFileName().contains("a")))) {
                        choicebigimgaeACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choicebigimgaeACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layouta, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("A"))
                                || (userAnswerOfQuestionOfAssessment.getAnswer().contains("a"))) {
                            choicebigimgaeACheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choicebigimgaeACheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layouta, R.drawable.learningoption_disablebackground);

                        }
                    }
                }
            }

            if ((questions.getImageChoiceB() != null)) {
                setLayoutAspectRatio(learningquiz_bigoptionblayout);
                setImageOption(questions.getImageChoiceB().getImageData(), bigimageoptionB);
                mainbigimageoption_layoutb.setVisibility(VISIBLE);
                if (questionType.equals(QuestionType.MCQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                            && (questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(userAnswerOfQuestionOfAssessment.getAnswer()))) {
                            OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layoutb, R.drawable.learningoption_disablebackground);
                        }
                    }
                } else if (questionType.equals(QuestionType.MRQ)) {
                    if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                            ((questions.getImageChoiceAnswer().getImageFileName().contains("B"))
                                    || (questions.getImageChoiceAnswer().getImageFileName().contains("b")))) {
                        choicebigimgaeBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                        choicebigimgaeBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
                        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layoutb, R.drawable.learning_rightanswer_background);
                    } else if (isAssessmentQuestion && userAnswerOfQuestionOfAssessment != null
                            && userAnswerOfQuestionOfAssessment.getAnswer() != null && !userAnswerOfQuestionOfAssessment.getAnswer().isEmpty()) {
                        if ((userAnswerOfQuestionOfAssessment.getAnswer().contains("B"))
                                || (userAnswerOfQuestionOfAssessment.getAnswer().contains("b"))) {
                            choicebigimgaeBCheckBox.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
                            choicebigimgaeBCheckBox.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                            OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layoutb, R.drawable.learningoption_disablebackground);
                        }
                    }
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void setImageOption(String url, ImageView imgView) {
        try {
            try {
                Glide.with(getActivity())
                        .load(url)
                        .into(imgView);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, "setImageOption: " + e.getLocalizedMessage());
            setImageOptionUrl(url, imgView);
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

    public void setImageOptionUrl(String url, ImageView imageView) {

        try {
            Glide.with(getActivity())
                    .load(url)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    private void setLayoutAspectRatio(RelativeLayout layout) {
        try {
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen20);
            int imageWidth = scrWidth - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            float h = (imageWidth * 0.563f);
            int h1 = (int) h;
            params.height = h1;
            params.width = imageWidth;
            layout.setLayoutParams(params);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setLayoutAspectRatiosmall(RelativeLayout layout) {
        try {
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen20);
            int imageWidth = (scrWidth / 2) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            float h = (imageWidth * 0.563f);
            int h1 = (int) h;
            params.height = h1;
            params.width = imageWidth;
            layout.setLayoutParams(params);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    public void onDestroy() {
        try {
            cancelSound();
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
            }
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                questionaudio_btn.setAnimation(null);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            resetAllData();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    private void resetAllData() {
        myHandler = null;
        courseCardClass = null;
        mainCourseCardClass = null;
        mediaPlayer = null;
        questions = null;
        questionType = null;
        learningModuleInterface = null;
    }

    public void cancelSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }

    private List<String> rightChoiceIds = new ArrayList<>();
    private List<String> leftChoiceIds = new ArrayList<>();

    private void setMatchQuestionLayoutSize() {
        try {
            matchfollowing_layout.setVisibility(VISIBLE);
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen13);
            int imageWidth = (scrWidth / 2) - size;
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

            List<DTOMTFColumnData> mtfRightColMain = questions.getMtfRightCol();
            List<DTOMTFColumnData> mtfRightCol = new ArrayList<>();
            List<String> answerList = questions.getMtfAnswer();
            List<DTOMTFColumnData> mtfLeftCol = questions.getMtfLeftCol();
            for (int i = 0; i < mtfLeftCol.size(); i++) {
                for (int j = 0; j < mtfRightColMain.size(); j++) {
                    String ansStr = "" + mtfLeftCol.get(i).getMtfColDataId() + "," + mtfRightColMain.get(j).getMtfColDataId();
                    for (int k = 0; k < answerList.size(); k++) {
                        if (answerList.get(k).contains(ansStr)) {
                            mtfRightCol.add(mtfRightColMain.get(j));
                        }
                    }
                }
            }
            if ((mtfLeftCol != null) && (mtfLeftCol.size() > 0)) {
                if ((mtfLeftCol.size() > 0) && (mtfLeftCol.get(0) != null) && (mtfLeftCol.get(0).getMtfColData() != null)) {
                    setMatchOption(match_option_a_left_image, match_option_a_left_text, mtfLeftCol.get(0));
                    match_option_a_left_layout.setVisibility(VISIBLE);
                    showOptionWithAnimA(match_option_a_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(0).getMtfColDataId());
                }
                if ((mtfLeftCol.size() > 1) && (mtfLeftCol.get(1) != null) && (mtfLeftCol.get(1).getMtfColData() != null)) {
                    setMatchOption(match_option_b_left_image, match_option_b_left_text, mtfLeftCol.get(1));
                    showOptionWithAnimA(match_option_b_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(1).getMtfColDataId());
                }
                if ((mtfLeftCol.size() > 2) && (mtfLeftCol.get(2) != null) && (mtfLeftCol.get(2).getMtfColData() != null)) {
                    setMatchOption(match_option_c_left_image, match_option_c_left_text, mtfLeftCol.get(2));
                    showOptionWithAnimA(match_option_c_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(2).getMtfColDataId());
                }
                if ((mtfLeftCol.size() > 3) && (mtfLeftCol.get(3) != null) && (mtfLeftCol.get(3).getMtfColData() != null)) {
                    setMatchOption(match_option_d_left_image, match_option_d_left_text, mtfLeftCol.get(3));
                    showOptionWithAnimA(match_option_d_left_layout);
                    leftChoiceIds.add(mtfLeftCol.get(3).getMtfColDataId());
                }
            }
            if ((mtfRightCol != null) && (mtfRightCol.size() > 0)) {
                if ((mtfRightCol.size() > 0) && (mtfRightCol.get(0) != null) && (mtfRightCol.get(0).getMtfColData() != null)) {
                    setMatchOption(match_option_a_right_image, match_option_a_right_text, mtfRightCol.get(0));
                    showOptionWithAnimA(match_option_a_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(0).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 1) && (mtfRightCol.get(1) != null) && (mtfRightCol.get(1).getMtfColData() != null)) {
                    setMatchOption(match_option_b_right_image, match_option_b_right_text, mtfRightCol.get(1));
                    showOptionWithAnimA(match_option_b_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(1).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 2) && (mtfRightCol.get(2) != null) && (mtfRightCol.get(2).getMtfColData() != null)) {
                    setMatchOption(match_option_c_right_image, match_option_c_right_text, mtfRightCol.get(2));
                    showOptionWithAnimA(match_option_c_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(2).getMtfColDataId());
                }
                if ((mtfRightCol.size() > 3) && (mtfRightCol.get(3) != null) && (mtfRightCol.get(3).getMtfColData() != null)) {
                    setMatchOption(match_option_d_right_image, match_option_d_right_text, mtfRightCol.get(3));
                    showOptionWithAnimA(match_option_d_right_layout);
                    rightChoiceIds.add(mtfRightCol.get(3).getMtfColDataId());
                }
            }
        } catch (Exception e) {
        }
    }

    private void showOptionWithAnimA(View view) {
        view.setVisibility(VISIBLE);
    }

    private void setMatchOption(ImageView imageView, TextView textView, DTOMTFColumnData mtfColumnData) {
        try {
            if (mtfColumnData.getMtfColMediaType() != null) {
                if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                    setImageOption(mtfColumnData.getMtfColData(), imageView);
                    imageView.setVisibility(VISIBLE);
                    textView.setVisibility(View.GONE);
                } else if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("AUDIO")) {
                } else {
                    OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                    imageView.setVisibility(View.GONE);
                    textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                    textView.setVisibility(VISIBLE);
                }
            } else {
                OustSdkTools.getSpannedContent(mtfColumnData.getMtfColData(), textView);
                textView.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                imageView.setVisibility(View.GONE);
                textView.setVisibility(VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public String mUrl1 = null;
    private DTOCourseSolutionCard courseCardClass;

    private void showSolution() {
        try {
            if (!isAssessmentQuestion) {
                if (mainCourseCardClass != null) {
                    courseCardClass = mainCourseCardClass.getChildCard();
                    if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                        if (mainCourseCardClass.getReadMoreData() != null && mainCourseCardClass.getReadMoreData().getDisplayText() != null) {
                            if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                                solution_desc_maths.setText(courseCardClass.getContent());
                            } else {
                                solution_desc.setHtml(courseCardClass.getContent());
                            }
                            solution_readmore.setVisibility(VISIBLE);
                            solution_readmore_text.setText(mainCourseCardClass.getReadMoreData().getDisplayText());
                            solution_readmore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 150);
                                }
                            });
                        } else {
                            if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                                solution_desc_maths.setText(courseCardClass.getContent());
                            } else {
                                solution_desc.setHtml(courseCardClass.getContent());
                            }
                        }
                        learningquiz_solutionlayout.setVisibility(VISIBLE);
                        solution_label.setVisibility(VISIBLE);
                        if (courseCardClass.getContent().contains(KATEX_DELIMITER)) {
                            solution_desc_maths.setVisibility(VISIBLE);
                            solution_desc.setVisibility(GONE);
                        } else {
                            solution_desc_maths.setVisibility(View.GONE);
                            solution_desc.setVisibility(VISIBLE);
                        }
                    }
                }

            } else {
                showAssessmentSolution();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void showAssessmentSolution() {
        try {
            if (mainCourseCardClass != null) {
                if (mainCourseCardClass.getQuestionData() != null && mainCourseCardClass.getQuestionData().getSolution() != null
                        && !mainCourseCardClass.getQuestionData().getSolution().isEmpty()) {
                    if (mainCourseCardClass.getQuestionData().getSolution().contains(KATEX_DELIMITER)) {
                        solution_desc_maths.setText(mainCourseCardClass.getQuestionData().getSolution());
                    } else {
                        solution_desc.setHtml(mainCourseCardClass.getQuestionData().getSolution());
                    }
                    solution_label.setVisibility(VISIBLE);
                    if (mainCourseCardClass.getQuestionData().getSolution().contains(KATEX_DELIMITER)) {
                        solution_desc_maths.setVisibility(VISIBLE);
                        solution_desc.setVisibility(GONE);
                    } else {
                        solution_desc_maths.setVisibility(GONE);
                        solution_desc.setVisibility(VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        try {
            if (requestCode == 150) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        OustSdkTools tools = new OustSdkTools();
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                questionaudio_btn.setAnimation(null);
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            }
                        }
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                            questionaudio_btn.setAnimation(null);
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }
                        if (mainCourseCardClass.getReadMoreData().getType().equalsIgnoreCase("pdf")) {
                            learningModuleInterface.setShareClicked(true);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            int sdk = Build.VERSION.SDK_INT;
                            if (sdk > Build.VERSION_CODES.M) {
                                Uri fileUri = FileProvider.getUriForFile(OustSdkApplication.getContext(), OustSdkApplication.getContext().getApplicationContext().getPackageName() + ".provider", OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData()));
                                intent.setDataAndType(fileUri, "application/pdf");
                            } else {
                                File file = OustSdkTools.getDataFromPrivateStorage(mainCourseCardClass.getReadMoreData().getData());
                                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            }
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            getActivity().startActivity(intent);
                        } else {
                            DTOReadMore readData = mainCourseCardClass.getReadMoreData();
                            learningModuleInterface.setShareClicked(true);
                            if (readData.getType().equalsIgnoreCase("IMAGE")) {
                                learningModuleInterface.changeOrientationUnSpecific();
                            }
                            learningModuleInterface.openReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
//
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void createStringfor_speech() {
        try {
            Spanned s1 = getSpannedContent(questions.getQuestion());
            String quesStr = s1.toString().trim();
            String optionStr = "";
            if ((questions.getA() != null)) {
                Spanned s2 = getSpannedContent(questions.getA());
                optionStr += ("\n Choice A \n" + (s2.toString().trim()));
            }
            if ((questions.getB() != null)) {
                Spanned s2 = getSpannedContent(questions.getB());
                optionStr += ("\n Choice B \n " + (s2.toString().trim()));
            }
            if ((questions.getC() != null)) {
                Spanned s2 = getSpannedContent(questions.getC());
                optionStr += ("\n Choice C \n" + (s2.toString().trim()));
            }
            if ((questions.getD() != null)) {
                Spanned s2 = getSpannedContent(questions.getD());
                optionStr += ("\n Choice D \n" + (s2.toString().trim()));
            }
            if ((questions.getE() != null)) {
                Spanned s2 = getSpannedContent(questions.getE());
                optionStr += ("\n Choice E \n" + (s2.toString().trim()));
            }
            if ((questions.getF() != null)) {
                Spanned s2 = getSpannedContent(questions.getF());
                optionStr += ("\n Choice F \n" + (s2.toString().trim()));
            }
            if ((questions.getG() != null)) {
                Spanned s2 = getSpannedContent(questions.getG());
                optionStr += ("\n Choice G \n" + (s2.toString().trim()));
            }
            if ((questionType.equals(QuestionType.MCQ))) {
                speakString((quesStr + "\n\n Choose one of these \n\n " + optionStr), true);
            } else if ((questionType.equals(QuestionType.MRQ))) {
                speakString((quesStr + " \n " + optionStr), true);
            } else {
                speakString(quesStr, true);
            }
        } catch (Exception e) {
        }
    }

    private boolean isAppIsInBackground() {
        if (MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities) {
            return true;
        } else {
            return false;
        }
    }

    private void speakString(String str, boolean isQuestStr) {
        try {
            if (!isAppIsInBackground()) {
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
                scaleDownX.setDuration(1000);
                scaleDownY.setDuration(1000);
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
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.gotopreviousscreen_mainbtn) {
                learningModuleInterface.gotoPreviousScreen();
            } else if (v.getId() == R.id.gotonextscreen_mainbtn) {
                learningModuleInterface.gotoNextScreen();
            } else if (v.getId() == R.id.image_expandbtna) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionA.getDrawable(), getActivity(), this);

                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.image_expandbtnb) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionB.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.image_expandbtnc) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionC.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.image_expandbtnd) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionD.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.bigimage_expandbtna) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(bigimageoptionA.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.bigimage_expandbtnb) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(bigimageoptionA.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (v.getId() == R.id.questionaudio_btn) {
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
                                if (isAudioPlaying) {
                                    isAudioPlaying = false;
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                                    questionaudio_btn.setAnimation(null);
                                    if (OustSdkTools.textToSpeech != null) {
                                        OustSdkTools.stopSpeech();
                                    }
                                } else {
                                    isAudioPlaying = true;
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    createStringfor_speech();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
            } else if (v.getId() == R.id.questionmore_btn) {
                learningModuleInterface.showCourseInfo();

            } else if (v.getId() == R.id.bulletin_layout) {
                Intent intent = new Intent(getActivity(), BulletinBoardQuestionActivity.class);
                if (courseId != null) {
                    intent.putExtra("courseId", "" + courseId);
                }
                if (courseName != null) {
                    intent.putExtra("courseName", courseName);
                }
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean isAudioPlaying = true;

    @Override
    public void onDialogClose() {
        learningModuleInterface.changeOrientationPortrait();
    }

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


//    =================================================================
//    Category Layout

    private List<DTOQuestionOption> categoryOneOptionList = new ArrayList<>();
    private List<DTOQuestionOption> categoryTwoOptionList = new ArrayList<>();

    private void setCategoryLayout() {
        category_layout.setVisibility(VISIBLE);

        optionData = questions.getOptions();
        categoryData = questions.getOptionCategories();

        mInflater = (LayoutInflater) OustSdkApplication.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        setcategories(categoryData);

        sortListOfTwoCategories();

    }

    public void setcategories(List<DTOQuestionOptionCategory> categoryData) {
        try {
            if (categoryData != null) {
                if (categoryData.get(0).getType().equalsIgnoreCase("text")) {
                    category_left_layout.setVisibility(View.GONE);
                    category_lefttext_layout.setVisibility(VISIBLE);
                    category_lefttext_layout.setText(categoryData.get(0).getData());
                } else if ((categoryData.size() > 0) && (categoryData.get(0).getType().equalsIgnoreCase("image"))) {
                    category_lefttext_layout.setVisibility(View.GONE);
                    category_left_layout.setVisibility(VISIBLE);
                    setImageOption(categoryData.get(0).getData(), category_leftimage_layout);
                }
                if (categoryData.get(1).getType().equalsIgnoreCase("text")) {
                    category_right_layout.setVisibility(View.GONE);
                    category_righttext_layout.setVisibility(VISIBLE);
                    category_righttext_layout.setText(categoryData.get(1).getData());
                } else if ((categoryData.size() > 1) && (categoryData.get(1).getType().equalsIgnoreCase("image"))) {
                    category_righttext_layout.setVisibility(View.GONE);
                    category_right_layout.setVisibility(VISIBLE);
                    setImageOption(categoryData.get(1).getData(), category_rightimage_layout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void sortListOfTwoCategories() {
        for (int i = 0; i < optionData.size(); i++) {
            if (optionData.get(i).getOptionCategory().equalsIgnoreCase(categoryData.get(0).getCode())) {
                categoryOneOptionList.add(optionData.get(i));
            } else if (optionData.get(i).getOptionCategory().equalsIgnoreCase(categoryData.get(1).getCode())) {
                categoryTwoOptionList.add(optionData.get(i));
            }
        }

        if (categoryOneOptionList != null) {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//            setReviewViews(categoryOneOptionList, category_leftoption_layout);
//                }
//            },500);
            setLeftViews();
        }
        if (categoryTwoOptionList != null) {
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setReviewViews(categoryTwoOptionList, category_rightoption_layout);
//                }
//            },500);
            setRightViews();
        }
    }


    public void setLeftViews() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < categoryOneOptionList.size(); i++) {
                    if (categoryOneOptionList.get(i).getType().equalsIgnoreCase("text")) {
                        switch (i) {
                            case 0:
                                optionsleft_txtlayout1.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout1.setVisibility(VISIBLE);
                                break;
                            case 1:
                                optionsleft_txtlayout2.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout2.setVisibility(VISIBLE);
                                break;
                            case 2:
                                optionsleft_txtlayout3.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout3.setVisibility(VISIBLE);
                                break;
                            case 3:
                                optionsleft_txtlayout4.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout4.setVisibility(VISIBLE);
                                break;
                            case 4:
                                optionsleft_txtlayout5.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout5.setVisibility(VISIBLE);
                                break;
                            case 5:
                                optionsleft_txtlayout6.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout6.setVisibility(VISIBLE);
                                break;
                            case 6:
                                optionsleft_txtlayout7.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout7.setVisibility(VISIBLE);
                                break;
                            case 7:
                                optionsleft_txtlayout8.setText(categoryOneOptionList.get(i).getData());
                                optionsleft_txtlayout8.setVisibility(VISIBLE);
                                break;
                        }
                    }
                    if (categoryOneOptionList.get(i).getType().equalsIgnoreCase("image")) {
                        switch (i) {
                            case 0:
                                optionsleft_imglayout1.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout1);
                                break;
                            case 1:
                                optionsleft_imglayout2.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout2);
                                break;
                            case 2:
                                optionsleft_imglayout3.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout3);
                                break;
                            case 3:
                                optionsleft_imglayout4.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout4);
                                break;
                            case 4:
                                optionsleft_imglayout5.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout5);
                                break;
                            case 5:
                                optionsleft_imglayout6.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout6);
                                break;
                            case 6:
                                optionsleft_imglayout7.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout7);
                                break;
                            case 7:
                                optionsleft_imglayout8.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryOneOptionList.get(i).getData(), optionsleft_imglayout8);
                                break;
                        }
                    }
                }
            }
        }, 500);
    }

    public void setRightViews() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < categoryTwoOptionList.size(); i++) {
                    if (categoryTwoOptionList.get(i).getType().equalsIgnoreCase("text")) {
                        switch (i) {
                            case 0:
                                optionsright_textlayout1.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout1.setVisibility(VISIBLE);
                                break;
                            case 1:
                                optionsright_textlayout2.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout2.setVisibility(VISIBLE);
                                break;
                            case 2:
                                optionsright_textlayout3.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout3.setVisibility(VISIBLE);
                                break;
                            case 3:
                                optionsright_textlayout4.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout4.setVisibility(VISIBLE);
                                break;
                            case 4:
                                optionsright_textlayout5.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout5.setVisibility(VISIBLE);
                                break;
                            case 5:
                                optionsright_textlayout6.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout6.setVisibility(VISIBLE);
                                break;
                            case 6:
                                optionsright_textlayout7.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout7.setVisibility(VISIBLE);
                                break;
                            case 7:
                                optionsright_textlayout8.setText(categoryTwoOptionList.get(i).getData());
                                optionsright_textlayout8.setVisibility(VISIBLE);
                                break;
                        }
                    }
                    if (categoryTwoOptionList.get(i).getType().equalsIgnoreCase("image")) {
                        switch (i) {
                            case 0:
                                optionsright_imglayout1.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout1);
                                break;
                            case 1:
                                optionsright_imglayout2.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout2);
                                break;
                            case 2:
                                optionsright_imglayout3.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout3);
                                break;
                            case 3:
                                optionsright_imglayout4.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout4);
                                break;
                            case 4:
                                optionsright_imglayout5.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout5);
                                break;
                            case 5:
                                optionsright_imglayout6.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout6);
                                break;
                            case 6:
                                optionsright_imglayout7.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout7);
                                break;
                            case 7:
                                optionsright_imglayout8.setVisibility(VISIBLE);
                                setImageOptionUrl(categoryTwoOptionList.get(i).getData(), optionsright_imglayout8);
                                break;
                        }
                    }
                }
            }
        }, 500);
    }


    FrameLayout convertView;

    private void setReviewViews(List<QuestionOptionData> categoryOptionList, LinearLayout category_option_layout) {
        try {
//            FrameLayout.LayoutParams lv = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
//            lv.setMargins(0, 0, 0, 10);
            for (int i = 0; i < categoryOptionList.size(); i++) {
//                convertView = (FrameLayout) mInflater.inflate(R.layout.reviewcategory_option, null);
//                TextView options_textlayout = (TextView) convertView.findViewById(R.id.options_textlayout);
//                ImageView options_imglayout = (ImageView) convertView.findViewById(R.id.options_imglayout);
//                if (categoryOptionList.get(i).getType().equalsIgnoreCase("TEXT")) {
//                    options_textlayout.setLayoutParams(lv);
//                    options_textlayout.setVisibility(View.VISIBLE);
//                    options_textlayout.setText("" + categoryOptionList.get(i).getData());
//                } else if (categoryOptionList.get(i).getType().equalsIgnoreCase("IMAGE")) {
//                    options_imglayout.setLayoutParams(lv);
//                    options_imglayout.setVisibility(View.VISIBLE);
//                    setImageView(categoryOptionList.get(i).getData(),options_imglayout);
//                }
//                category_option_layout.addView(convertView);
                if (categoryOptionList.get(i).getType().equalsIgnoreCase("TEXT")) {
                    TextView tv = new TextView(getActivity());
                    LinearLayout.LayoutParams lv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                    lv.setMargins(0, 0, 0, 10);
                    tv.setLayoutParams(lv);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackgroundColor(OustSdkTools.getColorBack(R.color.whitea));
                    tv.setTextColor(OustSdkTools.getColorBack(R.color.Gray));
                    tv.setPadding(10, 10, 10, 10);
                    tv.setText("" + categoryOptionList.get(i).getData());
                    category_option_layout.addView(tv);
                } else if (categoryOptionList.get(i).getType().equalsIgnoreCase("IMAGE")) {
                    ImageView imageView = new ImageView(getActivity());
                    LinearLayout.LayoutParams lv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                    lv.setMargins(0, 0, 0, 10);
                    imageView.setLayoutParams(lv);
                    imageView.setBackgroundColor(OustSdkTools.getColorBack(R.color.whitea));
//                    imageView.setPadding(10, 10, 10, 10);
                    if ((categoryOptionList.get(i).getData() != null) && (!categoryOptionList.get(i).getData().isEmpty())) {
                        setImageView(categoryOptionList.get(i).getData(), imageView);
                    }
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    category_option_layout.addView(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setImageView(final String data, ImageView imageView) {
        try {
            byte[] imageByte = Base64.decode(data, 0);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            imageView.setImageBitmap(decodedByte);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
    //===================================================================================================


    List<DTOHotspotPointData> hotspotPointDataList = new ArrayList<>();
    private boolean[] touchPoint;//500 375
    private float bitmapW, bitmapH;
    private float bitmapActualW, bitmapActualH;
    private float ratioW = 0, ratioH = 0;

    private void startHotspotQuestion() {
        try {
            hotspot_mainlayout.setVisibility(VISIBLE);
            hotspotPointDataList = questions.getHotspotDataList();
            touchPoint = new boolean[hotspotPointDataList.size()];
            hotspot_imgtouch_indicator.setList(hotspotPointDataList, touchPoint);
            hotspot_imgtouch_indicator.setThumbShow(true, true);
            if (mInflater == null) {
                mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            }
            if (questions != null && questions.getImageType() != null && questions.getImageType().equalsIgnoreCase("fullscreen")) {
                FullScreenHotspotImageSetting();
            } else {
                setHotSpotImageSize();
            }
            setSpots();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void setHotSpotImageSize() {
        try {
            hotspot_mainlayout.setVisibility(VISIBLE);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
            //File file = new File(fileURL);
            Bitmap hotspotIcon = BitmapFactory.decodeFile(fileURL, bmOptions);
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

            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen16);
            bitmapW = (scrWidth) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();
            bitmapH = (((float) bitmapW * ((float) hotspotIcon.getHeight() / (float) hotspotIcon.getWidth())));
            params.height = ((int) bitmapH);
            hotspot_img.setLayoutParams(params);
            hotspot_img.setImageBitmap(hotspotIcon);
            hotspot_img.setVisibility(VISIBLE);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) hotspot_imgtouch_indicator.getLayoutParams();
            params1.height = ((int) bitmapH);
            hotspot_imgtouch_indicator.setLayoutParams(params1);
            hotspot_imgtouch_indicator.setVisibility(VISIBLE);
            hotspot_imgtouch_indicator.setReviewmode();

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) hotspotlabel_layout.getLayoutParams();
            params2.height = (int) bitmapH;
            hotspotlabel_layout.setLayoutParams(params2);

        } catch (Exception e) {
        }
    }

    private void setSpots() {
        ratioW = bitmapW / bitmapActualW;
        ratioH = bitmapH / bitmapActualH;
        for (int i = 0; i < hotspotPointDataList.size(); i++) {
            touchPoint[i] = true;
            float w = ((hotspotPointDataList.get(i).getStartX() + (hotspotPointDataList.get(i).getWidth() / 2)));
            float h = ((hotspotPointDataList.get(i).getStartY() + (hotspotPointDataList.get(i).getHeight() / 2)));
            DTOHotspotPointData hotspotPointData = new DTOHotspotPointData();
            hotspotPointData.setStartX(((int) (w * ratioW)));
            hotspotPointData.setStartY(((int) (h * ratioH)));
            hotspotPointData.setWidth(80);
            hotspot_imgtouch_indicator.rightPoint(hotspotPointData, i, touchPoint);
            addLabel(((int) (w * ratioW)), ((int) (h * ratioH)), hotspotPointDataList.get(i).getHpLabel());
        }
    }

    private void addLabel(int x, int y, String label) {
        View hotspotlabelview = mInflater.inflate(R.layout.hotspot_label, null);
        TextView textView = (TextView) hotspotlabelview.findViewById(R.id.hotspot_labeltext);
        KatexView katexViewMaths = hotspotlabelview.findViewById(R.id.hotspot_labeltextMaths);
        if (label.contains(KATEX_DELIMITER)) {
            katexViewMaths.setText(label);
            textView.setVisibility(GONE);
            katexViewMaths.setVisibility(VISIBLE);
        } else {
            textView.setText(label);
            textView.setVisibility(VISIBLE);
            katexViewMaths.setVisibility(GONE);
        }

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

    Bitmap hotspotIcon;
    int hotSpotImageWidth, hotSpotImageHeight;
    int actualImageWidth, actualimageHeight;
    private RelativeLayout hotspotindicator_layout;


    private void FullScreenHotspotImageSetting() {
        hotspot_mainlayout.setVisibility(View.VISIBLE);
        hotspot_mainlayout.setPadding(0, 0, 0, 0);

        int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen16);

        bitmapW = (scrWidth) - size;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //bottomswipe_view.setVisibility(View.GONE);
        linearLayoutGap.setPadding(0, 0, 0, 0);
        mainoption_scrollview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        String fileURL = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(questions.getImageCDNPath());
        //File file = new File(fileURL);
        hotspotIcon = BitmapFactory.decodeFile(fileURL, bmOptions);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = displayMetrics.heightPixels - getStatusBarHeight();
        hotspot_img.setLayoutParams(params);
        hotspot_img.setImageBitmap(hotspotIcon);
        hotspot_img.setVisibility(View.VISIBLE);

        hotspotindicator_layout.setVisibility(View.GONE);
        hotspot_imgtouch_indicator.setReviewmode();
        mainoption_scrollview.setPadding(0, 0, 0, 0);

        learningquiz_mainquestion.setVisibility(View.GONE);
        // mainoption_scrollview.setVisibility(View.GONE);
        // hotspotlabel_layout.setVisibility(View.GONE);
        //hotspotmax_counttext.setVisibility(View.GONE);
        ref_image.setVisibility(View.GONE);

        actualImageWidth = hotspotIcon.getWidth();
        actualimageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        ;
        hotSpotImageHeight = displayMetrics.heightPixels; //*actualimageHeight/ actualImageWidth +getStatusBarHeight();
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
       /* int yAdjustment = (displayMetrics.heightPixels - hotSpotImageHeight)/2;
        int xAdjustment = (displayMetrics.widthPixels - hotSpotImageWidth)/2;
*/

        int yAdjustment = (displayMetrics.heightPixels - (int) (actualimageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;


        //  int yAdjustment = displayMetrics.heightPixels - getStatusBarHeight();

        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);
        //  int newChangedY = newStartY+(int)(newStartY*0.5);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;
        //  int newChangedEndY = newEndY+(int)(newEndY*0.5);

        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) hotspot_img.getLayoutParams();
        bitmapH = (float) bitmapW * ((float) hotspotIcon.getHeight() / (float) hotspotIcon.getWidth());

        if (multiplyingFactor < 1) // this calculation for displaying large image into small screen
        {
            multiplyingFactor = 1.0f;
        }
        // params3.height = (int) (multiplyingFactor * hotSpotImageHeight);
        // params3.width = (int)(multiplyingFactor * hotSpotImageWidth);

        params3.height = (int) (hotSpotImageHeight);
        params3.width = (int) (hotSpotImageWidth);

        params3.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params3.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        hotspot_img.setLayoutParams(params3);
        hotspot_mainlayout.setVisibility(View.VISIBLE);
        hotspot_img.setVisibility(View.VISIBLE);


        int[] endpoints = calculateEndPointsForFullScreenHotspot(hotspotIcon, hotspotPointDataList.get(0));

        hotspot_imgtouch_indicator.drawRect(endpoints[0], endpoints[1], endpoints[2], endpoints[3], true);
        //hotspot_imgtouch_indicator.drawRect(hotspotPointDataList, true, deviceXFactor, deviceYFactor);

      /*  RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextView tv=new TextView(getContext());
        tv.setText("Test question");*/
        //hotspot_mainlayout.addView(tv);
    }

    private float deviceXFactor, deviceYFactor;

    private int[] calculateEndPointsForFullScreenHotspot(Bitmap hotspotIcon, DTOHotspotPointData hotspotPointData) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        actualImageWidth = hotspotIcon.getWidth();
        actualimageHeight = hotspotIcon.getHeight();

        hotSpotImageWidth = displayMetrics.widthPixels;
        ;
        hotSpotImageHeight = displayMetrics.heightPixels; //*actualimageHeight/ actualImageWidth +getStatusBarHeight();

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

        int yAdjustment = (displayMetrics.heightPixels - (int) (actualimageHeight * multiplyingFactor)) / 2;
        int xAdjustment = (displayMetrics.widthPixels - (int) (actualImageWidth * multiplyingFactor)) / 2;

        final int newStartX = (int) (apiStartX * multiplyingFactor) + xAdjustment;
        final int newStartY = (int) ((apiStartY * multiplyingFactor) + yAdjustment);

        final int newEndX = (int) ((apiStartX + apIRectangleWidth) * multiplyingFactor) + xAdjustment;
        final int newEndY = (int) ((apiStartY + apiRectangleHeight) * multiplyingFactor) + yAdjustment;

        int endPoints[] = new int[4];
        endPoints[0] = newStartX;
        endPoints[1] = newStartY;
        endPoints[2] = newEndX;
        endPoints[3] = newEndY;
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

    //    =================================================================================================
//    media upload in review_text mode
    private void showMediaUploadImage() {
        mediaupload_reviewlayout.setVisibility(VISIBLE);
        mediareview_imagelayout.setVisibility(VISIBLE);
    }

    private void getDataFromFirebase() {
        String message = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId();
        ValueEventListener userResponseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (null != dataSnapshot.getValue()) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {

            }
        };
        OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(userResponseListener);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }
}

