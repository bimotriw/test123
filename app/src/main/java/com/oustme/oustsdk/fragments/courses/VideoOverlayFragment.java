package com.oustme.oustsdk.fragments.courses;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
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

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.snackbar.Snackbar;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.LogoutMsgActivity;
import com.oustme.oustsdk.customviews.NewCustomExoPlayerView;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.fragments.courses.learningplaynew.LearningPlayFragmentNew;
import com.oustme.oustsdk.interfaces.common.BitmapCreateListener;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReadMoreFavouriteCallBack;
import com.oustme.oustsdk.question_module.fragment.LongQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MCQuestionFragment;
import com.oustme.oustsdk.question_module.fragment.MRQuestionFragment;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.CourseSolutionCard;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOImageChoice;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.service.DownLoadIntentService;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustLogDetailHandler;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class VideoOverlayFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, ReadMoreFavouriteCallBack, DialogKeyListener, BitmapCreateListener {

    private static final String TAG = "VideoOverlayFragment";

    private View learningquiz_animviewb, learningquiz_animviewa;
    private RelativeLayout learningquiz_mainlayout, solution_readmore;
    private TextView learningcard_coursename, learningquiz_timertext, solution_readmore_text;
    private ProgressBar learningcard_progress, video_loader;
    private ImageView learningquiz_mainquestionImage;
    private TextView learningquiz_mainquestionText, learningquiz_mainquestionTime;
    private ScrollView mainoption_scrollview;
    private ImageView learningquiz_imagequestion, questionaudio_btn, choiceACheckBox, choiceBCheckBox,
            choiceCCheckBox, choiceDCheckBox, choiceECheckBox, choiceFCheckBox, image_expandbtna,
            image_expandbtnb, bigimage_expandbtna, bigimage_expandbtnb, image_expandbtnc, image_expandbtnd;
    private RelativeLayout learningquiz_imagequestionlayout, gotonextscreen_btn, questionno_layout, ref_image,
            video_player_layout, quesvideoLayout;
    private TextView optionA, optionB, optionC, optionD, optionE, optionF,
            option_imga, option_imgb, option_imgc, option_imgd, option_imge, option_imgf;
    private LinearLayout learningquiz_textchoiselayout, mainoption_layouta, mainoption_layoutb,
            mainoption_layoutc, mainoption_layoutd, mainoption_layoute, mainoption_layoutf, gotonextscreen_mainbtn;
    private FrameLayout option_layouta, option_layoutb, option_layoutc, option_layoutd, option_layoute,
            option_layoutf;
    private ImageButton solution_closebtn;
    private HtmlTextView learningquiz_mainquestion;
    private FrameLayout framelayout_video_overlay;

//-------------

    private RelativeLayout survey_layout, surveysubmit_btnlayout, seekbar_nolayout, survey_sublayouta, seekbar_back, seekbar_backa, sp_form_ll;
    private TextView surveyoption_a, surveyoption_b, surveyoption_c, surveyoption_d, surveyoption_e;
    private SeekBar survey_seekbar;
    private Spinner sp_form;
    private EditText et_form, aadhar_et_1, aadhar_et_2, aadhar_et_3, et_pan_1, et_pan_2, et_phone_no, et_address1, et_address2, et_address3;
    private TextView tv_dob_form, area_name;
    private CheckBox accept_button;
    private LinearLayout job_type_1_ll, job_type_2_ll, job_type_3_ll, extra_tier_info_ll;
    private LinearLayout aadhar_info_ll, pan_card_ll, phone_info_ll, form_address_ll;
//--------------

    private LinearLayout learningquiz_imagechoiselayout, mainimageoption_layoutd, mainimageoption_layoutc,
            mainimageoption_layoutb, mainimageoption_layouta, form_submit_btn, accept_button_ll;
    private FrameLayout imageoption_layoutd, imageoption_layoutc, imageoption_layoutb, imageoption_layouta;
    private ImageView imageoptionA, imageoptionB, imageoptionC, imageoptionD, choiceimgaeACheckBox,
            choiceimgaeBCheckBox, choiceimgaeCCheckBox, choiceimgaeDCheckBox;

    //-----------------
    private ImageView choicebigimgaeACheckBox, choicebigimgaeBCheckBox, bigimageoptionA, bigimageoptionB, lpocimage;
    private LinearLayout learningquiz_bigimagechoiselayout, mainbigimageoption_layoutb,
            mainbigimageoption_layouta;
    private FrameLayout bigimageoption_layoutb, bigimageoption_layouta;
    //--------------
    private ImageView learningquiz_rightwrongimage, quiz_backgroundimagea, backgroundimage_card, questionmore_btn,
            question_arrowback, question_arrowfoword, showsolution_img;
    private TextView ocanim_text, cardprogress_text, myresponse_desc, myresponse_label;
    private HtmlTextView solution_desc;
    private TextView solution_label;
    private RelativeLayout learningquiz_bigoptionalayout, learningquiz_bigoptionblayout,
            learningquiz_optionalayout, learningquiz_optionblayout, learningquiz_optionclayout,
            learningquiz_optiondlayout, learning_mrqsubmitbutton, learning_mrqimgsubmitbutton,
            learning_mrqbigimgsubmitbutton, animoc_layout, learningquiz_solutionlayout;
    private LinearLayout ocanim_view, bottomswipe_view, gotopreviousscreen_mainbtn, learningquiz_form_layout;
    private ImageView unfavourite;
    private LinearLayout longanswer_layout;
    private EditText longanswer_editetext;
    private TextView maxanswer_limittext;
    private Button longanswer_submit_btn;

    private boolean isQuesttsEnabled;
    private boolean isRandomizeQuestion = true;
    private boolean isAudioPaused;
    private int totalSetOptions = -1;
    private int noOfAnimationEnd = 0;
    private Context mContext;
    private int mWordLength;
    Handler video_overlay_HandlerTimer;
    Runnable runnableTimer;

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }
    //------------------

    private Handler myHandler;
    private int scrWidth, scrHeight;
    private String questionType;
    private String questionCategory;

    private boolean isImageOption = true;
    private boolean hasImageQuestion = false;
    private boolean hasVideoQuestion = false;
    private boolean isBigImageOption = false;
    private boolean isfavouriteClicked = false;
    private Scores assessmentScore;
    private Animation scaleanim;

    private boolean isReviewMode;
    DTOUserCardData userCardData;
    long videoSeekTime;

    public void setAssessmentScore(Scores assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private List<FavCardDetails> favCardDetailsList;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList) {
        this.favCardDetailsList = favCardDetailsList;
    }

    private boolean isRMFavourite;

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }


    boolean isDownloadButtonclicked = false;
    private GifImageView downloadvideo_icon;
    private File tempVideoFileName;
    private boolean isVideoDownloding = false;
    private String videoFileName;
    private String videoFilePath;
    private boolean isFileDownloadServiceStarted;
    private TextView downloadvideo_text;
    private Button show_quality;
    private boolean isFullScreen;
    private ImageView video_landscape_zoom;
    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isSurveyQuestion;

    public void setSurveyQuestion(boolean surveyQuestion) {
        isSurveyQuestion = surveyQuestion;
        Log.d(TAG, "setSurveyQuestion: ");
    }

    private boolean showNavigateArrow;

    public void setShowNavigateArrow(boolean showNavigateArrow) {
        this.showNavigateArrow = showNavigateArrow;
    }

    private boolean videoOverlay = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videooverlay, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews(view);
        initModuViewFragment();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (learningModuleInterface != null)
//            learningModuleInterface.changeOrientationPortrait();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (OustSdkTools.textToSpeech != null) {
                OustSdkTools.stopSpeech();
                if (questionaudio_btn != null) {
                    questionaudio_btn.setAnimation(null);
                }
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (myFileDownLoadReceiver != null) {
                if (getActivity() != null) {
                    getActivity().unregisterReceiver(myFileDownLoadReceiver);
                }
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
            if (customExoPlayerView != null) {
                customExoPlayerView.getSimpleExoPlayer().setPlayWhenReady(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        Log.d(TAG, "Onpause");
        if (OustSdkTools.textToSpeech != null) {
            OustSdkTools.stopSpeech();
        }
        if (video_overlay_HandlerTimer != null) {
            video_overlay_HandlerTimer.removeCallbacks(runnableTimer);
        }
        try {
            requireActivity().onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setUserCardData(DTOUserCardData userCardData) {
        try {
            this.userCardData = userCardData;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initViews(View view) {
        if (questions != null) {

            scaleanim = AnimationUtils.loadAnimation(getActivity(), R.anim.learning_audioscaleanim);

            learningquiz_animviewb = view.findViewById(R.id.learningquiz_animviewb);
            learningquiz_animviewa = view.findViewById(R.id.learningquiz_animviewa);
            learningquiz_mainlayout = view.findViewById(R.id.learningquiz_mainlayout);
            //------
            learningcard_coursename = view.findViewById(R.id.learningcard_coursename);
            learningquiz_timertext = view.findViewById(R.id.learningquiz_timertext);
            learningcard_progress = view.findViewById(R.id.learningcard_progress);
//------------------------------
            questionno_layout = view.findViewById(R.id.questionno_layout);
            learningquiz_mainquestionImage = view.findViewById(R.id.learningquiz_mainquestionImage);
            learningquiz_mainquestionText = view.findViewById(R.id.learningquiz_mainquestionText);
            learningquiz_mainquestionTime = view.findViewById(R.id.learningquiz_mainquestionTime);
            video_player_layout = view.findViewById(R.id.video_player_layout);
            quesvideoLayout = view.findViewById(R.id.quesvideoLayout);
            video_loader = view.findViewById(R.id.video_loader);
//---------

            mainoption_scrollview = view.findViewById(R.id.mainoption_scrollview);
            learningquiz_imagequestion = view.findViewById(R.id.learningquiz_imagequestion);
            learningquiz_imagequestionlayout = view.findViewById(R.id.learningquiz_imagequestionlayout);
            learningquiz_mainquestion = view.findViewById(R.id.learningquiz_mainquestion);
            questionaudio_btn = view.findViewById(R.id.questionaudio_btn);
            learningquiz_textchoiselayout = view.findViewById(R.id.learningquiz_textchoiselayout);
            mainoption_layouta = view.findViewById(R.id.mainoption_layouta);
            mainoption_layoutb = view.findViewById(R.id.mainoption_layoutb);
            mainoption_layoutc = view.findViewById(R.id.mainoption_layoutc);
            mainoption_layoutd = view.findViewById(R.id.mainoption_layoutd);
            mainoption_layoute = view.findViewById(R.id.mainoption_layoute);
            mainoption_layoutf = view.findViewById(R.id.mainoption_layoutf);
            learningquiz_form_layout = view.findViewById(R.id.learningquiz_form_layout);
            job_type_1_ll = view.findViewById(R.id.job_type_1_ll);
            job_type_2_ll = view.findViewById(R.id.job_type_2_ll);
            job_type_3_ll = view.findViewById(R.id.job_type_3_ll);
            sp_form = view.findViewById(R.id.sp_form);
            et_form = view.findViewById(R.id.et_form);
            form_submit_btn = view.findViewById(R.id.form_submit_btn);
            tv_dob_form = view.findViewById(R.id.tv_dob_form);
            sp_form_ll = view.findViewById(R.id.sp_form_ll);
            extra_tier_info_ll = view.findViewById(R.id.extra_tier_info_ll);
            area_name = view.findViewById(R.id.area_name);
            accept_button = view.findViewById(R.id.accept_button);
            aadhar_info_ll = view.findViewById(R.id.aadhar_info_ll);
            aadhar_et_1 = view.findViewById(R.id.aadhar_et_1);
            aadhar_et_2 = view.findViewById(R.id.aadhar_et_2);
            aadhar_et_3 = view.findViewById(R.id.aadhar_et_3);
            et_pan_1 = view.findViewById(R.id.et_pan_1);
            et_pan_2 = view.findViewById(R.id.et_pan_2);
            pan_card_ll = view.findViewById(R.id.pan_card_ll);
            et_phone_no = view.findViewById(R.id.et_phone_no);
            phone_info_ll = view.findViewById(R.id.phone_info_ll);
            form_address_ll = view.findViewById(R.id.form_address_ll);
            et_address1 = view.findViewById(R.id.et_address1);
            et_address2 = view.findViewById(R.id.et_address2);
            et_address3 = view.findViewById(R.id.et_address3);

            option_layouta = view.findViewById(R.id.option_layouta);
            option_layoutb = view.findViewById(R.id.option_layoutb);
            option_layoutc = view.findViewById(R.id.option_layoutc);
            option_layoutd = view.findViewById(R.id.option_layoutd);
            option_layoute = view.findViewById(R.id.option_layoute);
            option_layoutf = view.findViewById(R.id.option_layoutf);
            optionA = view.findViewById(R.id.optionA);
            optionB = view.findViewById(R.id.optionB);
            optionC = view.findViewById(R.id.optionC);
            optionD = view.findViewById(R.id.optionD);
            optionE = view.findViewById(R.id.optionE);
            optionF = view.findViewById(R.id.optionF);
            option_imga = view.findViewById(R.id.option_imga);
            option_imgb = view.findViewById(R.id.option_imgb);
            option_imgc = view.findViewById(R.id.option_imgc);
            option_imgd = view.findViewById(R.id.option_imgd);
            option_imge = view.findViewById(R.id.option_imge);
            option_imgf = view.findViewById(R.id.option_imgf);

            choiceACheckBox = view.findViewById(R.id.choiceACheckBox);
            choiceBCheckBox = view.findViewById(R.id.choiceBCheckBox);
            choiceCCheckBox = view.findViewById(R.id.choiceCCheckBox);
            choiceDCheckBox = view.findViewById(R.id.choiceDCheckBox);
            choiceECheckBox = view.findViewById(R.id.choiceECheckBox);
            choiceFCheckBox = view.findViewById(R.id.choiceFCheckBox);
//--------------

            learningquiz_imagechoiselayout = view.findViewById(R.id.learningquiz_imagechoiselayout);
            mainimageoption_layoutd = view.findViewById(R.id.mainimageoption_layoutd);
            mainimageoption_layoutc = view.findViewById(R.id.mainimageoption_layoutc);
            mainimageoption_layoutb = view.findViewById(R.id.mainimageoption_layoutb);
            mainimageoption_layouta = view.findViewById(R.id.mainimageoption_layouta);
            imageoption_layoutd = view.findViewById(R.id.imageoption_layoutd);
            imageoption_layoutc = view.findViewById(R.id.imageoption_layoutc);
            imageoption_layoutb = view.findViewById(R.id.imageoption_layoutb);
            imageoption_layouta = view.findViewById(R.id.imageoption_layouta);
            imageoptionA = view.findViewById(R.id.imageoptionA);
            imageoptionB = view.findViewById(R.id.imageoptionB);
            imageoptionC = view.findViewById(R.id.imageoptionC);
            imageoptionD = view.findViewById(R.id.imageoptionD);
            choiceimgaeACheckBox = view.findViewById(R.id.choiceimgaeACheckBox);
            choiceimgaeBCheckBox = view.findViewById(R.id.choiceimgaeBCheckBox);
            choiceimgaeCCheckBox = view.findViewById(R.id.choiceimgaeCCheckBox);
            choiceimgaeDCheckBox = view.findViewById(R.id.choiceimgaeDCheckBox);
            //-----------------

            choicebigimgaeACheckBox = view.findViewById(R.id.choicebigimgaeACheckBox);
            choicebigimgaeBCheckBox = view.findViewById(R.id.choicebigimgaeBCheckBox);
            learningquiz_bigimagechoiselayout = view.findViewById(R.id.learningquiz_bigimagechoiselayout);
            mainbigimageoption_layoutb = view.findViewById(R.id.mainbigimageoption_layoutb);
            mainbigimageoption_layouta = view.findViewById(R.id.mainbigimageoption_layouta);
            bigimageoption_layoutb = view.findViewById(R.id.bigimageoption_layoutb);
            bigimageoption_layouta = view.findViewById(R.id.bigimageoption_layouta);
            bigimageoptionA = view.findViewById(R.id.bigimageoptionA);
            bigimageoptionB = view.findViewById(R.id.bigimageoptionB);
            lpocimage = view.findViewById(R.id.lpocimage);
//--------------
            learningquiz_solutionlayout = view.findViewById(R.id.learningquiz_solutionlayout);
            learningquiz_rightwrongimage = view.findViewById(R.id.learningquiz_rightwrongimage);
            solution_desc = view.findViewById(R.id.solution_desc);
            solution_readmore_text = view.findViewById(R.id.solution_readmore_text);
            solution_readmore = view.findViewById(R.id.solution_readmore);
            learningquiz_bigoptionalayout = view.findViewById(R.id.learningquiz_bigoptionalayout);
            learningquiz_bigoptionblayout = view.findViewById(R.id.learningquiz_bigoptionblayout);
            learningquiz_optionalayout = view.findViewById(R.id.learningquiz_optionalayout);
            learningquiz_optionblayout = view.findViewById(R.id.learningquiz_optionblayout);
            learningquiz_optionclayout = view.findViewById(R.id.learningquiz_optionclayout);
            learningquiz_optiondlayout = view.findViewById(R.id.learningquiz_optiondlayout);
            ref_image = view.findViewById(R.id.ref_image);
            myresponse_label = view.findViewById(R.id.myresponse_label);
            myresponse_desc = view.findViewById(R.id.myresponse_desc);

            learning_mrqsubmitbutton = view.findViewById(R.id.learning_mrqsubmitbutton);
            learning_mrqimgsubmitbutton = view.findViewById(R.id.learning_mrqimgsubmitbutton);
            learning_mrqbigimgsubmitbutton = view.findViewById(R.id.learning_mrqbigimgsubmitbutton);

            quiz_backgroundimagea = view.findViewById(R.id.quiz_backgroundimagea);
            backgroundimage_card = view.findViewById(R.id.backgroundimage_card);
            ocanim_view = view.findViewById(R.id.ocanim_view);
            animoc_layout = view.findViewById(R.id.animoc_layout);
            ocanim_text = view.findViewById(R.id.ocanim_text);
            questionmore_btn = view.findViewById(R.id.questionmore_btn);
            bottomswipe_view = view.findViewById(R.id.bottomswipe_view);
            cardprogress_text = view.findViewById(R.id.cardprogress_text);
            gotopreviousscreen_mainbtn = view.findViewById(R.id.gotopreviousscreen_mainbtn);
            question_arrowback = view.findViewById(R.id.question_arrowback);
            question_arrowfoword = view.findViewById(R.id.question_arrowfoword);
            showsolution_img = view.findViewById(R.id.showsolution_img);
            image_expandbtna = view.findViewById(R.id.image_expandbtna);
            gotonextscreen_btn = view.findViewById(R.id.gotonextscreen_btn);
            solution_closebtn = view.findViewById(R.id.solution_closebtn);
            bigimage_expandbtnb = view.findViewById(R.id.bigimage_expandbtnb);
            bigimage_expandbtna = view.findViewById(R.id.bigimage_expandbtna);
            image_expandbtnb = view.findViewById(R.id.image_expandbtnb);
            image_expandbtnc = view.findViewById(R.id.image_expandbtnc);
            image_expandbtnd = view.findViewById(R.id.image_expandbtnd);
            gotonextscreen_mainbtn = view.findViewById(R.id.gotonextscreen_mainbtn);
            unfavourite = view.findViewById(R.id.unfavourite);

            longanswer_submit_btn = view.findViewById(R.id.longanswer_submit_btn);
            maxanswer_limittext = view.findViewById(R.id.maxanswer_limittext);
            longanswer_editetext = view.findViewById(R.id.longanswer_editetext);
            longanswer_layout = view.findViewById(R.id.longanswer_layout);
            solution_label = view.findViewById(R.id.solution_label);

            survey_layout = view.findViewById(R.id.survey_layout);
            surveysubmit_btnlayout = view.findViewById(R.id.surveysubmit_btnlayout);
            seekbar_nolayout = view.findViewById(R.id.seekbar_nolayout);
            survey_sublayouta = view.findViewById(R.id.survey_sublayouta);
            seekbar_back = view.findViewById(R.id.seekbar_back);
            seekbar_backa = view.findViewById(R.id.seekbar_backa);

            survey_seekbar = view.findViewById(R.id.survey_seekbar);

            surveyoption_a = view.findViewById(R.id.surveyoption_a);
            surveyoption_b = view.findViewById(R.id.surveyoption_b);
            surveyoption_c = view.findViewById(R.id.surveyoption_c);
            surveyoption_d = view.findViewById(R.id.surveyoption_d);
            surveyoption_e = view.findViewById(R.id.surveyoption_e);

            questionsubans_editetext = view.findViewById(R.id.questionsubans_editetext);
            questionsubans_limittext = view.findViewById(R.id.questionsubans_limittext);
            questionsubans_submit_btn = view.findViewById(R.id.questionsubans_submit_btn);
            questionsubans_header = view.findViewById(R.id.questionsubans_header);
            questionsubans_cardlayout = view.findViewById(R.id.questionsubans_cardlayout);
            questionsubans_card = view.findViewById(R.id.questionsubans_card);

            OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsup));
            OustSdkTools.setImage(learningquiz_mainquestionImage, OustSdkApplication.getContext().getResources().getString(R.string.whitequestion_img));
            OustSdkTools.setImage(lpocimage, OustSdkApplication.getContext().getResources().getString(R.string.newxp_img));
            if (!isAssessmentQuestion)
                OustSdkTools.setImage(quiz_backgroundimagea, OustSdkApplication.getContext().getResources().getString(R.string.bg_1));

            gotonextscreen_btn.setOnClickListener(this);
            image_expandbtna.setOnClickListener(this);
            questionaudio_btn.setOnClickListener(this);
            solution_closebtn.setOnClickListener(this);
            showsolution_img.setOnClickListener(this);
            bigimage_expandbtnb.setOnClickListener(this);
            bigimage_expandbtna.setOnClickListener(this);
            image_expandbtnb.setOnClickListener(this);
            image_expandbtnc.setOnClickListener(this);
            image_expandbtnd.setOnClickListener(this);
            questionmore_btn.setOnClickListener(this);
            gotopreviousscreen_mainbtn.setOnClickListener(this);
            gotonextscreen_mainbtn.setOnClickListener(this);
            unfavourite.setOnClickListener(this);
            surveysubmit_btnlayout.setOnClickListener(this);

            option_layouta.setOnTouchListener(this);
            longanswer_submit_btn.setOnTouchListener(this);
            option_layoutb.setOnTouchListener(this);
            option_layoutc.setOnTouchListener(this);
            option_layoutd.setOnTouchListener(this);
            option_layoute.setOnTouchListener(this);
            option_layoutf.setOnTouchListener(this);
            bigimageoption_layouta.setOnTouchListener(this);
            bigimageoption_layoutb.setOnTouchListener(this);
            imageoption_layouta.setOnTouchListener(this);
            imageoption_layoutb.setOnTouchListener(this);
            imageoption_layoutc.setOnTouchListener(this);
            imageoption_layoutd.setOnTouchListener(this);
            learningquiz_imagequestion.setOnTouchListener(this);
            learningquiz_animviewb.setOnTouchListener(this);
            //solution_desc.setOnTouchListener(this);
            learning_mrqsubmitbutton.setOnTouchListener(this);
            learning_mrqimgsubmitbutton.setOnTouchListener(this);
            learning_mrqbigimgsubmitbutton.setOnTouchListener(this);


            if (isAssessmentQuestion) {
                questionno_layout.setVisibility(View.GONE);
                ref_image.setVisibility(View.GONE);
            }

            questionno_layout.setVisibility(View.GONE);
            setFont();

            quesvideoLayout.setVisibility(View.GONE);
            learningquiz_imagequestionlayout.setVisibility(View.GONE);
            survey_layout.setVisibility(View.GONE);
            learningquiz_textchoiselayout.setVisibility(View.VISIBLE);
            learningquiz_imagechoiselayout.setVisibility(View.GONE);
            learningquiz_bigimagechoiselayout.setVisibility(View.GONE);
            learningquiz_form_layout.setVisibility(View.GONE);

        }

        framelayout_video_overlay = view.findViewById(R.id.framelayout_video_overlay);
        framelayout_video_overlay.setVisibility(View.GONE);

        downloadvideo_icon = view.findViewById(R.id.downloadvideo_icon);
        downloadvideo_text = view.findViewById(R.id.downloadvideo_text);
        show_quality = view.findViewById(R.id.show_quality);
        video_landscape_zoom = view.findViewById(R.id.video_landscape_zoom);
        video_landscape_zoom.setOnClickListener(this);
        try {
            OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        video_overlay_HandlerTimer = new Handler(Looper.getMainLooper());

    }

    private void setFont() {
        longanswer_submit_btn.setText(OustStrings.getString("submit"));
        solution_label.setText(OustStrings.getString("solution"));
        myresponse_label.setText(OustStrings.getString("my_response"));
        questionsubans_editetext.setHint(OustStrings.getString("enter_response_hint"));

    }

    private LearningModuleInterface learningModuleInterface;

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    String courseId;

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    private int learningcardProgress = 0;

    public void setLearningcard_progressVal(int progress) {
        learningcardProgress = progress;
    }

    private DTOQuestions questions;

    public void setQuestions(DTOQuestions questions) {
        this.questions = questions;
        Log.d(TAG, "setQuestions: " + this.questions.getQuestion() + " " + this.questions.getqVideoUrl());

    }

    private boolean zeroXpForQCard;

    public void setZeroXpForQCard(boolean zeroXpForQCard) {
        this.zeroXpForQCard = zeroXpForQCard;
        Log.d(TAG, "setZeroXpForQCard: " + this.zeroXpForQCard);
    }

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    private boolean isAssessmentQuestion = false;

    public void setAssessmentQuestion(boolean assessmentQuestion) {
        isAssessmentQuestion = assessmentQuestion;
    }

    private int cardCount;

    public void setTotalCards(int cardCount) {
        this.cardCount = cardCount;
    }

//    ===============================================================================================

    public void initModuViewFragment() {
        try {
            Log.d(TAG, "initModuViewFragment: ");
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (OustAppState.getInstance().getActiveUser() == null) {
                getActivity().finish();
            }
            videoSeekTime = 0;
            getWidth();
            if (questionmore_btn != null) {
                if (isAssessmentQuestion) {
                    questionmore_btn.setVisibility(View.GONE);
                } else {
                    questionmore_btn.setVisibility(View.VISIBLE);
                }
            }
            showBottemBarForSurvey();
            enableSwipe();
            myHandler = new Handler();
            setStartingData();
            setQuestionNo();
            setColors();
            setFontStyle();
            /*if(isReviewMode){
                showBottombarInReviewMode();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private DTOCourseCard mainCourseCardClass;
    private long questionXp = 20;
    private long mainCardId;

    public void setMainCourseCardClass(DTOCourseCard courseCardClass2) {
        try {
            int savedCardID = (int) courseCardClass2.getCardId();
            this.questionXp = courseCardClass2.getXp();
            this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
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

        mainCardId = mainCourseCardClass.getCardId();
        questions = this.mainCourseCardClass.getQuestionData();
        if (questions == null) {
            if (learningModuleInterface != null) {
                learningModuleInterface.endActivity();
            }
        }

        if (isAssessmentQuestion) {
            mainCourseCardClass.setXp(questionXp);
        }
    }

    private DTOCourseCard randomizeOption(DTOCourseCard courseCardClass) {
        try {
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION") && (courseCardClass.getQuestionData().isRandomize())) {
                DTOQuestions learningQuestionData = courseCardClass.getQuestionData();
                if ((learningQuestionData.getQuestionType() != null) && (learningQuestionData.getQuestionType() != QuestionType.MRQ)) {
                    if (learningQuestionData.getQuestionCategory() != null) {
                        if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE)) {
                            List<DTOImageChoice> optionList = new ArrayList<>();
                            if ((learningQuestionData.getImageChoiceA() != null) && (learningQuestionData.getImageChoiceA().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceA());
                            }
                            if ((learningQuestionData.getImageChoiceB() != null) && (learningQuestionData.getImageChoiceB().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceB());
                            }
                            if ((learningQuestionData.getImageChoiceC() != null) && (learningQuestionData.getImageChoiceC().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceC());
                            }
                            if ((learningQuestionData.getImageChoiceD() != null) && (learningQuestionData.getImageChoiceD().getImageData() != null)) {
                                optionList.add(learningQuestionData.getImageChoiceD());
                            }
                            Collections.shuffle(optionList);
                            int n1 = 0;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceA(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceB(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceC(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setImageChoiceD(optionList.get(n1));
                            }
                        } else if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.MATCH)) {
                            Collections.shuffle(learningQuestionData.getMtfLeftCol());
                        } else {
                            List<String> optionList = new ArrayList<>();
                            if ((learningQuestionData.getA() != null) && (!learningQuestionData.getA().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getA());
                            }
                            if ((learningQuestionData.getB() != null) && (!learningQuestionData.getB().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getB());
                            }
                            if ((learningQuestionData.getC() != null) && (!learningQuestionData.getC().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getC());
                            }
                            if ((learningQuestionData.getD() != null) && (!learningQuestionData.getD().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getD());
                            }
                            if ((learningQuestionData.getE() != null) && (!learningQuestionData.getE().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getE());
                            }
                            if ((learningQuestionData.getF() != null) && (!learningQuestionData.getF().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getF());
                            }
                            if ((learningQuestionData.getG() != null) && (!learningQuestionData.getG().equalsIgnoreCase("dont know"))) {
                                optionList.add(learningQuestionData.getG());
                            }
                            Collections.shuffle(optionList);
                            int n1 = 0;
                            if (optionList.size() > n1) {
                                learningQuestionData.setA(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setB(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setC(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setD(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setE(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setF(optionList.get(n1));
                            }
                            n1++;
                            if (optionList.size() > n1) {
                                learningQuestionData.setG(optionList.get(n1));
                            }
                        }
                    }
                }
                courseCardClass.setQuestionData(learningQuestionData);
            }
        } catch (Exception e) {
        }
        return courseCardClass;
    }


    boolean isVideoCompleted = false;

    public void enableSwipe() {
        mainoption_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Log.d(TAG, "onTouch: action:" + action + " -- isVideoCompleted:" + isVideoCompleted);

                if (!isVideoCompleted) {
                    return false;
                }

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
                                gotoNextScreen();
                            }
                        }
                    } else if (deltaX < 0 && deltaY > 0) {
                        if ((-deltaX) > deltaY) {
                            if ((-deltaX) > MIN_DISTANCE) {
                                gotoPreviousScreen();
                            }
                        }

                    } else if (deltaX < 0 && deltaY < 0) {
                        if (deltaX < deltaY) {
                            if ((-deltaX) > MIN_DISTANCE) {
                                gotoPreviousScreen();
                            }
                        }
                    } else if (deltaX > 0 && deltaY < 0) {
                        if (deltaX > (-deltaY)) {
                            if (deltaX > MIN_DISTANCE) {
                                gotoNextScreen();
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    private void setColors() {
        try {
            DTOCardColorScheme cardColorScheme = mainCourseCardClass.getCardColorScheme();
            if (isSurveyQuestion) {
                quiz_backgroundimagea.setBackgroundColor(OustSdkTools.getColorBack(R.color.QuizBgGray));
            }

            if (cardColorScheme != null) {
                if ((cardColorScheme.getIconColor() != null) && (!cardColorScheme.getIconColor().isEmpty())) {
                    questionmore_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
                    questionaudio_btn.setColorFilter(Color.parseColor(cardColorScheme.getIconColor()));
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

            try {
                String toolbarColorCode = OustPreferences.get("toolbarColorCode");
                int color = OustSdkTools.getColorBack(R.color.Orange);
                if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                    color = Color.parseColor(toolbarColorCode);
                }
                Log.d(TAG, "setColors: " + color + " -- toolbarColorCode:" + toolbarColorCode);
                extra_tier_info_ll.setBackgroundColor(color);

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackgroundImage(String bgImageUrl) {
        try {
            OustSdkTools.setImage(quiz_backgroundimagea, getResources().getString(R.string.bg_1));
            if (bgImageUrl != null && !bgImageUrl.isEmpty()) {
                backgroundimage_card.setVisibility(View.VISIBLE);
                if ((OustSdkTools.checkInternetStatus()) && (OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                    Picasso.get().load(bgImageUrl).into(backgroundimage_card);
                } else {
                    Picasso.get().load(bgImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(backgroundimage_card);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
        }
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    private void setStartingData() {
        try {
            int waitTimer = 1000;
            if (learningcardProgress == 0) {
                waitTimer = 1200;
            }
            if (userCardData != null) {
                if (userCardData.getCardViewInterval() > 0) {
                    if ((userCardData.getCardViewInterval() / 1000) > 1) {
                        videoSeekTime = userCardData.getCardViewInterval() / 1000;
                    } else {
                        videoSeekTime = (userCardData.getCardViewInterval() * 1000);
                    }
                }
            }
            //startQuestionNOHideAnimation(waitTimer);
            if (questions != null) {
                setTitle();
                setQuestionTitle();
                setQuestionType();
                Log.d(TAG, "setStartingData: " + questions.getQuestion());

                List<DTOVideoOverlay> videoOverlayList = questions.getListOfVideoOverlayQuestion();
                extraAdGroupTimesMs = null;
                extraPlayedAdGroups = null;
                if (videoOverlayList != null) {
                    extraAdGroupTimesMs = new long[videoOverlayList.size()];
                    extraPlayedAdGroups = new boolean[videoOverlayList.size()];
                    int i = 0;
                    for (DTOVideoOverlay videoOverlay : videoOverlayList) {
                        extraAdGroupTimesMs[i] = videoOverlay.getTimeDuration();
                        extraPlayedAdGroups[i] = true;
                        i++;
                    }
                }

                Log.d(TAG, "setStartingData: Time:" + extraAdGroupTimesMs.toString());
                if ((questions.getGumletVideoUrl() != null) && (!questions.getGumletVideoUrl().isEmpty())) {
                    hasVideoQuestion = true;
                    Log.d(TAG, "setStartingData: Gurl:" + questions.getGumletVideoUrl());
                    setQuestionVideo(questions.getGumletVideoUrl(), waitTimer);
                } else if ((questions.getqVideoUrl() != null) && (!questions.getqVideoUrl().isEmpty())) {
                    hasVideoQuestion = true;
                    Log.d(TAG, "setStartingData: url:" + questions.getqVideoUrl());
                    setQuestionVideo(questions.getqVideoUrl(), waitTimer);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "setStartingData: exception");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public NewCustomExoPlayerView getCustomExoPlayerView() {
        return customExoPlayerView;
    }

    private NewCustomExoPlayerView customExoPlayerView;

    private void setQuestionVideo(String path, final int waitTimer) {
        videoFilePath = path;
        quesvideoLayout.setVisibility(View.VISIBLE);
        video_player_layout.setVisibility(View.VISIBLE);
        if (learningModuleInterface != null) {
            learningModuleInterface.changeOrientationUnSpecific();
        }
        //OustSdkTools.showToast(getResources().getString(R.string.assessment_video_start_msg));

        AWSMobileClient.getInstance().initialize(getActivity(), new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d(TAG, "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        customExoPlayerView = new NewCustomExoPlayerView() {
            @Override
            public void onAudioComplete() {

            }

            @Override
            public void onVideoComplete() {
                Log.d(TAG, "onVideoComplete: ");
                //startQuestionNOHideAnimation(waitTimer);
                //loadQuestion();
                //notifyActivityToStartTimer();
                isVideoCompleted = true;
                if (isReviewMode) {
                    showBottombarInReviewMode();
                } else {
                    showBottombar();
                }
                stopVideoOverlayTimer();
                setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
            }

            @Override
            public void onBuffering() {
                video_loader.setVisibility(View.VISIBLE);
                setAutoPause();

                if (runnableTimer == null) {
                    Log.d(TAG, "onPlayerStateChanged: Restart video overlay timer");
                    startVideoOverlayTimer();
                }

            }

            @Override
            public void onVideoError() {
                video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onPlayReady() {
                video_loader.setVisibility(View.GONE);
            }

            @Override
            public void onIdle() {
                video_loader.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRestarted() {
                isVideoCompleted = false;
                setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
            }
        };

        String[] url_path_array = path.split("/");
        if (url_path_array != null && url_path_array.length > 0) {
            videoFileName = url_path_array[url_path_array.length - 1];
        }

        Log.d(TAG, "setQuestionVideo: Filename:" + videoFileName);

        isVideoCompleted = false;
        customExoPlayerView.initExoPlayer(video_player_layout, mContext, path, videoFileName, videoSeekTime);
        setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());

        if (extraAdGroupTimesMs != null && extraPlayedAdGroups != null) {
            customExoPlayerView.getSimpleExoPlayerView().setExtraAdGroupMarkers(extraAdGroupTimesMs, extraPlayedAdGroups);
        }
        //timeBarOverlay.setAdGroupTimesMs(extraAdGroupTimesMs, extraPlayedAdGroups, 2);
        //updateProgress();
        //long duration = simpleExoPlayer.getDuration();
        //Log.d(TAG, "startVideoPlayer: Exoplayer duration:"+duration);

        if (runnableTimer == null) {
            Log.d(TAG, "startVideoPlayer: Strating video overlay timer");
            startVideoOverlayTimer();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String path1 = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
            File file = new File(path1);
            Log.d(TAG, "streamStoredVideo: file exist:" + file.exists());
            if (!file.exists()) {
                setDownloadBtn();
            } else {
                downloadvideo_icon.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
            }
        } else {
            setDownloadBtn();
        }
    }

    private void showBottombar() {
        bottomswipe_view.setVisibility(View.VISIBLE);
        gotonextscreen_mainbtn.setVisibility(View.VISIBLE);
    }

    private void showBottombarInReviewMode() {
        bottomswipe_view.setVisibility(View.VISIBLE);
        if (learningcardProgress == 0) {
            gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            gotonextscreen_mainbtn.setVisibility(View.VISIBLE);
        } else if (learningcardProgress == (courseLevelClass.getCourseCardClassList().size()) - 1) {
            gotonextscreen_mainbtn.setVisibility(View.GONE);
            gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
        } else {
            gotonextscreen_mainbtn.setVisibility(View.VISIBLE);
            gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(fragment!=null && fragment.isResumed()){
            //do nothing here if we're showing the fragment
        }else{
            setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT); // otherwise lock in portrait
        }
        super.onConfigurationChanged(newConfig);
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged: ");
        try {
           /* if (customExoPlayerView != null || customExoPlayerView.getSimpleExoPlayerView() != null) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setLandscapeVideoRation(customExoPlayerView.getSimpleExoPlayerView());
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    learningModuleInterface.changeOrientationUnSpecific();
                    setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
                }
            } else*/
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setPotraitVideoRatioFullScreen(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = true;
        ref_image.setVisibility(View.GONE);
        //solution_desc.setVisibility(View.GONE);
        bottomswipe_view.setVisibility(View.GONE);

        if (simpleExoPlayerView != null) {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            float h = metrics.heightPixels;
            params.width = scrWidth;
            params.height = (int) h;
            video_player_layout.setLayoutParams(params);
            simpleExoPlayerView.setLayoutParams(params);
        }
    }

    public void setPortaitVideoRatio(StyledPlayerView simpleExoPlayerView) {
        isFullScreen = false;
        try {
            ref_image.setVisibility(View.VISIBLE);
            //solution_desc.setVisibility(View.GONE);
            bottomswipe_view.setVisibility(View.VISIBLE);

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

        if (isReviewMode) {
            showBottombarInReviewMode();
        }
    }

    public void disableSoftInputFromAppearing(EditText editText, String str, boolean enable) {
        if (enable) {
            int maxLengthofEditText = str.length() - 1;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
        } else {
            editText.setFilters(new InputFilter[]{});
        }
    }

    private void setQuestionNo() {
        try {
            if (learningcardProgress == 0) {
                gotopreviousscreen_mainbtn.setVisibility(View.GONE);
            }
            learningquiz_mainquestionText.setText(OustStrings.getString("question_text") + " " + (learningcardProgress + 1));
            long millis = questions.getMaxtime();
            String hms = String.format("%02d:%02d", millis / 60, millis % 60);
            if (isSurveyQuestion) {
                learningquiz_mainquestionTime.setText("");
            } else {
                learningquiz_mainquestionTime.setText(hms);
            }
            if (cardCount == 0) {
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + courseLevelClass.getCourseCardClassList().size());
                learningcard_progress.setMax((courseLevelClass.getCourseCardClassList().size() * 50));
            } else {
                cardprogress_text.setText("" + (learningcardProgress + 1) + "/" + cardCount);
                learningcard_progress.setMax((cardCount * 50));
            }
            learningcard_progress.setProgress((learningcardProgress * 50));
            ObjectAnimator animation = ObjectAnimator.ofInt(learningcard_progress, "progress", (((learningcardProgress)) * 50), (((learningcardProgress + 1) * 50)));
            animation.setDuration(600);
            animation.setStartDelay(500);
            animation.start();
        } catch (Exception e) {
        }
    }

    private int totalRate = 9;
    private LayoutInflater mInflater;
    private View convertView;

    private View thumbView;

    //-----------------------------------------------------------------------------------------------------
    private void setTitle() {
        try {
            if (courseLevelClass.getLevelName() != null) {
                learningcard_coursename.setText(courseLevelClass.getLevelName().trim());
            }
        } catch (Exception e) {
        }
    }

    private void setQuestionTitle() {
        try {
            Log.d(TAG, "setQuestionTitle: " + questions.getQuestion());
            if ((questions.getQuestion() != null)) {
                if (mainCourseCardClass.getMappedLearningCardId() > 0) {
                    if ((mainCourseCardClass.getCaseStudyTitle() != null) && (!mainCourseCardClass.getCaseStudyTitle().isEmpty())) {
                        learningquiz_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + mainCourseCardClass.getCaseStudyTitle() + "</a>");
                    } else {
                        learningquiz_mainquestion.setHtml(questions.getQuestion() + " <br/> <a href=www.oustme.com>" + "CASE STUDY" + "</a>");
                    }
                    learningquiz_mainquestion.setLinkTextColor(OustSdkTools.getColorBack(R.color.white_pressed));
                    learningquiz_mainquestion.setMovementMethod(new TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String url) {
                            isCaseletQuestionOptionClicked = true;
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 130);
                        }
                    });

                } else {
                    learningquiz_mainquestion.setHtml(questions.getQuestion());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean musicComplete = false;
    private Handler mediaHandler;

    private void playDownloadedAudioQues(final String filename) {
        cancelSound();
        Log.d(TAG, "playDownloadedAudioQues: ");
        mediaPlayer = new MediaPlayer();
        try {
            File audiofile = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            if ((audiofile != null) && (audiofile.exists())) {
                musicComplete = false;
                try {
                    questionaudio_btn.setVisibility(View.VISIBLE);
                    mediaPlayer.reset();
                    FileInputStream fis = new FileInputStream(audiofile);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                    questionaudio_btn.startAnimation(scaleanim);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            musicComplete = true;
                            questionaudio_btn.setAnimation(null);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
//                }
//            });
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
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
            Log.d(TAG, "setQuestionType: " + questionType);
            questionCategory = questions.getQuestionCategory();
            Log.d(TAG, "setQuestionType category: " + questionCategory);
            if (questionType == null) {
                questionType = QuestionType.MRQ;
                learningquiz_textchoiselayout.setVisibility(View.VISIBLE);
            }
            if (questionCategory == null) {
                questionCategory = QuestionCategory.TEXT;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void setImageQuestionImage() {
        try {
            Log.d(TAG, "setImageQuestionImage: ");
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
            }
         /*   else {
                String str = questions.getImage();
                if ((str != null) && (!str.isEmpty())) {
                    byte[] imageByte = Base64.decode(str, 0);
                    GifDrawable gifFromBytes = new GifDrawable(imageByte);
                    learningquiz_imagequestion.setImageDrawable(gifFromBytes);
                }
            }*/
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
                    learningquiz_imagequestion.setImageBitmap(decodedByte);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
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

    public void setImageOptionUrl(String url, ImageView imageView) {
        if (url != null) {
            url = OustMediaTools.removeAwsOrCDnUrl(url);
        }
        //OustSdkTools.showToast("IMAGE FROM CDNPATH imageoptionurl");
        url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
        if (file != null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            imageView.setImageURI(uri);
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

    int noofOption = 0;
    private int totalOption = 7;

    private void showOptionWithAnimA(View view, int position) {
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
        scaleDown.setStartDelay((position * 180));
        scaleDown.start();
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                noOfAnimationEnd++;
                if (totalSetOptions == noOfAnimationEnd + 1) {
                    Log.d(TAG, "onAnimationEnd: ");
                    for (int i = 0; i < learningquiz_textchoiselayout.getChildCount(); i++) {
                        View view = learningquiz_textchoiselayout.getChildAt(i);
                        view.setClickable(true); // Or whatever you want to do with the view.
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            hideKeyboard(longanswer_editetext);
            removeAllData();
            resetAllData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void removeAllData() {
        try {
            Log.e("SPEECH", "removeAllData() Called");

            if (OustSdkTools.textToSpeech != null) {
                Log.e("SPEECH", "removeAllData() OustSdkTools.textToSpeech != null");
                OustSdkTools.stopSpeech();
                if (questionaudio_btn != null)
                    questionaudio_btn.setAnimation(null);
            }

            cancelSound();
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
                myHandler = null;
            }
//            if (mediaHandler != null) {
//                mediaHandler.removeCallbacksAndMessages(null);
//                mediaHandler=null;
//            }
            if (customExoPlayerView != null) {
                customExoPlayerView.removeVideoPlayer();
                customExoPlayerView = null;
            }
        } catch (Exception e) {
            Log.e("SPEECH", "removeAllData() Exception occured", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoNextScreen() {
        try {
            Log.d(TAG, "gotoNextScreen: ");
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);
            if (!isAssessmentQuestion) {
                if (isReviewMode) {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoNextScreen();
                    }
                    removeAllData();
                } else {
                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas() != null) {
                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress] != null) {
                            if (learningModuleInterface != null) {
                                learningModuleInterface.setAnswerAndOc("", "", 100, true, 0);
                                learningModuleInterface.gotoNextScreen();
                            }
                            removeAllData();
                        } else {
                            vibrateandShake();
                        }
                    }
                }
            } else {
                if ((isSurveyQuestion) && (assessmentScore != null)) {
                    //calculateMrqTextOc(true, false, true);
                    if (learningModuleInterface != null)
                        learningModuleInterface.gotoNextScreen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void gotoPreviousScreen() {
        try {
            Log.d(TAG, "gotoPreviousScreen: ");
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", 0);

            if (!isAssessmentQuestion) {
                if (isReviewMode) {
                    if (learningModuleInterface != null) {
                        learningModuleInterface.gotoPreviousScreen();
                    }
                    removeAllData();
                } else {
                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas() != null) {
                        if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress] != null) {
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoPreviousScreen();
                            removeAllData();
                        } else {
                            if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FORM_TYPE)) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoPreviousScreen();
                                removeAllData();
                            } else
                                vibrateandShake();
                        }
                    }
                }
            } else {
                if (isSurveyQuestion) {
                    if (learningModuleInterface != null)
                        learningModuleInterface.gotoPreviousScreen();
                }
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
            learningquiz_imagequestion.setImageBitmap(null);

            questionaudio_btn.setImageBitmap(null);
            choiceACheckBox.setImageBitmap(null);

            choiceBCheckBox.setImageBitmap(null);
            choiceCCheckBox.setImageBitmap(null);
            choiceDCheckBox.setImageBitmap(null);

            choiceECheckBox.setImageBitmap(null);
            choiceFCheckBox.setImageBitmap(null);
            image_expandbtna.setImageBitmap(null);

            image_expandbtnb.setImageBitmap(null);
            bigimage_expandbtna.setImageBitmap(null);
            bigimage_expandbtnb.setImageBitmap(null);

            image_expandbtnc.setImageBitmap(null);
            image_expandbtnd.setImageBitmap(null);

            imageoptionA.setImageBitmap(null);
            imageoptionB.setImageBitmap(null);
            imageoptionC.setImageBitmap(null);
            imageoptionD.setImageBitmap(null);

            choiceimgaeACheckBox.setImageBitmap(null);
            choiceimgaeBCheckBox.setImageBitmap(null);
            choiceimgaeCCheckBox.setImageBitmap(null);
            choiceimgaeDCheckBox.setImageBitmap(null);

            choicebigimgaeACheckBox.setImageBitmap(null);
            choicebigimgaeBCheckBox.setImageBitmap(null);
            bigimageoptionA.setImageBitmap(null);
            bigimageoptionB.setImageBitmap(null);
            lpocimage.setImageBitmap(null);

            learningquiz_rightwrongimage.setImageBitmap(null);
            quiz_backgroundimagea.setImageBitmap(null);
            backgroundimage_card.setImageBitmap(null);
            questionmore_btn.setImageBitmap(null);
            question_arrowback.setImageBitmap(null);
            question_arrowfoword.setImageBitmap(null);
            showsolution_img.setImageBitmap(null);
            unfavourite.setImageBitmap(null);
            System.gc();
        } catch (Exception e) {
        }
    }


    ////====================================================================================================

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                tochedScreen = true;
                x1 = event.getX();
                y1 = event.getY();
            } else if (action == MotionEvent.ACTION_UP) {
                if (tochedScreen) {
                    tochedScreen = false;
                    x2 = event.getX();
                    y2 = event.getY();
                    float deltaX = x1 - x2;
                    float deltaY = y1 - y2;
                    if (deltaX > 0 && deltaY > 0) {
                        if (deltaX > deltaY) {
                            if (deltaX > MIN_DISTANCE) {
                                gotoNextScreen();
                            } else {
                                touchOnOption(v);
                            }
                        } else {
                            touchOnOption(v);
                        }
                    } else if (deltaX < 0 && deltaY > 0) {
                        if ((-deltaX) > deltaY) {
                            if ((-deltaX) > MIN_DISTANCE) {
                                gotoPreviousScreen();
                            } else {
                                touchOnOption(v);
                            }
                        } else {
                            touchOnOption(v);
                        }
                    } else if (deltaX < 0 && deltaY < 0) {
                        if (deltaX < deltaY) {
                            if ((-deltaX) > MIN_DISTANCE) {
                                gotoPreviousScreen();
                            } else {
                                touchOnOption(v);
                            }
                        } else {
                            touchOnOption(v);
                        }
                    } else if (deltaX > 0 && deltaY < 0) {
                        if (deltaX > (-deltaY)) {
                            if (deltaX > MIN_DISTANCE) {
                                gotoNextScreen();
                            } else {
                                touchOnOption(v);
                            }
                        } else {
                            touchOnOption(v);
                        }
                    } else {
                        touchOnOption(v);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
        return true;
    }

    private boolean isCaseletQuestionOptionClicked = false;

    private void touchOnOption(View view) {
        try {
            final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayc);
            final Drawable drawable = new ColorDrawable(color);
            if (animoc_layout.getVisibility() == View.VISIBLE) {
                int id = view.getId();
                if (id == R.id.learningquiz_imagequestion) {
                    try {
                        learningModuleInterface.changeOrientationUnSpecific();
                        OustSdkTools.gifZoomPopup(learningquiz_imagequestion.getDrawable(), getActivity(), this);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private boolean scrolledToBottom = false;

    private MediaPlayer mediaPlayer, defaultMediaPlayer;


    public void cancelSound() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

        } catch (Exception e) {
            Log.e("SPEECH", "cancelSound() exception occured", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void vibrateandShake() {
        try {
            /*Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            learningquiz_mainlayout.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);*/
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //====================================================================================================

    private int finalScr = 0;
    private boolean ocCalculated = false;

    private void playAudio(final String filename) {
        try {
            Log.d(TAG, "playAudio: " + filename);
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), filename);
            tempMp3.deleteOnExit();
            questionaudio_btn.setAnimation(null);
            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
            //cancelSound();
            defaultMediaPlayer = new MediaPlayer();
            defaultMediaPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            defaultMediaPlayer.setDataSource(fis.getFD());
            defaultMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            defaultMediaPlayer.prepare();
            defaultMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d(TAG, "onPrepared: ");
                }
            });
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d(TAG, "playAudio: isplaying");
                isAudioPaused = true;
                mediaPlayer.pause();
            }
            defaultMediaPlayer.start();
            defaultMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer1) {
                    Log.d(TAG, "onCompletion of media of answer correct or incorrect: ");
                    if (isAudioPaused) {
                        try {
                            if (mediaPlayer != null) {
                                mediaPlayer.start();
                                isAudioPaused = false;
                                questionaudio_btn.startAnimation(scaleanim);
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, "playAudio: Exception:" + e.getLocalizedMessage());
        }
    }


    private void showSolutionWithAnimation(final boolean status) {
        try {
            if (((mainCourseCardClass.getChildCard() != null) && (mainCourseCardClass.getChildCard().getContent() != null) && (!mainCourseCardClass.getChildCard().getContent().isEmpty())) ||
                    ((userSubjectiveAns != null) && (!userSubjectiveAns.isEmpty()))) {
                if (learningquiz_solutionlayout.getVisibility() == View.GONE) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                    anim.setStartOffset(200);
                    animoc_layout.startAnimation(anim);
                    showSolution(500, 1800);
                    startSpeakSolution();
                    // setCorrectAnswer();
                    animoc_layout.bringToFront();
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
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (learningModuleInterface != null) {
                            gotoNextScreen();
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void showSolution(int delay, int startPoint) {
        try {
            learningModuleInterface.dismissCardInfo();
            bottomswipe_view.setVisibility(View.VISIBLE);
            learningquiz_solutionlayout.setVisibility(View.VISIBLE);
            learningquiz_solutionlayout.setPivotY(learningquiz_solutionlayout.getHeight());
            learningquiz_solutionlayout.setPivotX((scrWidth / 2));
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", startPoint, 0);
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "scaleX", 0.0f, 1.0f);
            scaleDownX.setDuration(500);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.setStartDelay(delay);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //setCorrectAnswer();
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

    private void hideSolutionView() {
        try {
            learningquiz_solutionlayout.setPivotX(learningquiz_solutionlayout.getWidth() / 2);
            showsolution_img.setAnimation(null);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", 0, learningquiz_solutionlayout.getHeight());
            scaleDownY.setDuration(500);
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "scaleX", 1.0f, 0.0f);
            scaleDownX.setDuration(500);
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
        }
    }


    //======================================================================================================
    private long answeredSeconds;

    @Override
    public void onDialogClose() {
        if (learningModuleInterface != null) {
            learningModuleInterface.changeOrientationPortrait();
        }
    }

    @Override
    public void onBitmapCreated(Bitmap bitmap) {

    }

    @Override
    public void onNoBitmapFound() {
        OustSdkTools.showToast("Error occured while saving your salary payout !");
    }

    @Override
    public void onBitmapSaved(String path) {
        if (path != null)
            OustSdkTools.showToast("Your salary payout has been saved to " + path);
    }

    public void setReviewMode(boolean reviewMode) {
        isReviewMode = reviewMode;
    }

    public void setPreviousState() {
        try {
            int min = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] / 60;
            int sec = OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] % 60;
            learningquiz_timertext.setText("" + min + ":" + sec);
        } catch (Exception e) {
        }
    }

    //======================================================================================================
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;
    private boolean tochedScreen = false;
    //------------------------------------------
    private CourseSolutionCard courseCardClass;

    private boolean isAudioPausedFromOpenReadmore = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 130 || requestCode == 131) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                isAudioPausedFromOpenReadmore = true;
                                questionaudio_btn.setAnimation(null);
                                questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                            }
                        }
                        if (OustSdkTools.textToSpeech != null) {
//                            OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                            OustSdkTools.stopSpeech();
                            isAudioPausedFromOpenReadmore = true;
                            questionaudio_btn.setAnimation(null);
                            questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        }
                        if (requestCode == 130) {
                            openCaseLet();
                        } else if (requestCode == 131) {
                            openReadMore();
                        }

                    }

                }
            } else if (requestCode == 102) {
                if (grantResults != null) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startDownloadingVideo();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            final Snackbar snackbar = Snackbar
                                    .make(learningquiz_mainlayout, Html.fromHtml("<font color=\"#ffffff\">Permission denied to store this video.</font>"), Snackbar.LENGTH_LONG)
                                    .setAction("Grant Access", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                                        }
                                    });
                            snackbar.show();
                        } else {
                            Toast.makeText(getActivity(), "Permission denied to store this video.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCaseLet() {
        if ((isCaseletQuestionOptionClicked) && (mainCourseCardClass != null) && (mainCourseCardClass.getMappedLearningCardId() > 0)) {
            learningModuleInterface.setShareClicked(true);
            DTOReadMore readMoreData = new DTOReadMore();
            readMoreData.setType("CARD_LINK");
            readMoreData.setCardId(("" + mainCourseCardClass.getMappedLearningCardId()));
            readMoreData.setData("" + mainCourseCardClass.getMappedLearningCardId());
            learningModuleInterface.openReadMoreFragment(readMoreData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
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
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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

    private void startSpeakSolution() {
        try {
            if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
            } else {
                if ((courseCardClass.getContent() != null)) {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                                    Spanned s1 = getSpannedContent(courseCardClass.getContent());
//                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                    speakString(s1.toString().trim(), false);
//                                    } else {
//                                        if (OustSdkTools.textToSpeech != null) {
//                                            OustSdkTools.stopSpeech();
//                                            questionaudio_btn.setAnimation(null);
//                                        }
//                                    }
                                }
                            }
                        }, 200);
                    } else {
                        questionaudio_btn.setVisibility(View.GONE);
                    }
                }
            }
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
                gotoNextScreen();
            } else if (id == R.id.gotopreviousscreen_mainbtn) {
                gotoPreviousScreen();
            } else if (id == R.id.gotonextscreen_mainbtn) {
                gotoNextScreen();
            } else if (id == R.id.showsolution_img) {
                learningquiz_animviewb.setVisibility(View.VISIBLE);
                learningquiz_animviewb.bringToFront();
                showSolution(0, learningquiz_solutionlayout.getHeight());
            } else if (id == R.id.image_expandbtna) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionA.getDrawable(), getActivity(), this);

                } catch (Exception e) {
                }
            } else if (id == R.id.image_expandbtnb) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionB.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (id == R.id.image_expandbtnc) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionC.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (id == R.id.image_expandbtnd) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(imageoptionD.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (id == R.id.bigimage_expandbtna) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(bigimageoptionA.getDrawable(), getActivity(), this);
                } catch (Exception e) {
                }
            } else if (id == R.id.bigimage_expandbtnb) {
                try {
                    learningModuleInterface.changeOrientationUnSpecific();
                    OustSdkTools.gifZoomPopup(bigimageoptionB.getDrawable(), getActivity(), this);
                } catch (Exception e) {
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
            } else if (id == R.id.questionmore_btn) {
                if (learningquiz_solutionlayout.getVisibility() != View.VISIBLE)
                    learningModuleInterface.showCourseInfo();
            } else if (id == R.id.solution_closebtn) {
                hideSolutionView();
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
            } else if (id == R.id.surveysubmit_btnlayout) {
                float f1 = survey_seekbar.getProgress() / 10;
                sureveyResponse = ((Math.round(f1)) + 1);
                //submitSurveyResponse(false);
            } else if (id == R.id.surveyoption_a) {
                setServeryOptionBack(v);
                sureveyResponse = 1;
            } else if (id == R.id.surveyoption_b) {
                setServeryOptionBack(v);
                sureveyResponse = 1;
            } else if (id == R.id.surveyoption_c) {
                setServeryOptionBack(v);
                sureveyResponse = 1;
            } else if (id == R.id.surveyoption_d) {
                setServeryOptionBack(v);
                sureveyResponse = 1;
            } else if (id == R.id.surveyoption_e) {
                setServeryOptionBack(v);
                sureveyResponse = 1;
            } else if (id == R.id.video_landscape_zoom) {
                if (!isFullScreen) {
                    setPotraitVideoRatioFullScreen(customExoPlayerView.getSimpleExoPlayerView());
                } else {
                    setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean isAudioPlaying = true;

    private int sureveyResponse = 0;

    private void setServeryOptionBack(View v) {
        OustSdkTools.setLayoutBackgroud(surveyoption_a, R.drawable.loginbtn_corner);
        OustSdkTools.setLayoutBackgroud(surveyoption_b, R.drawable.loginbtn_corner);
        OustSdkTools.setLayoutBackgroud(surveyoption_c, R.drawable.loginbtn_corner);
        OustSdkTools.setLayoutBackgroud(surveyoption_d, R.drawable.loginbtn_corner);
        OustSdkTools.setLayoutBackgroud(surveyoption_e, R.drawable.loginbtn_corner);
        OustSdkTools.setLayoutBackgroud(v, R.drawable.learningoption_backa);
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

    private boolean isAppIsInForeground() {
        return MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities;
    }

    private void speakString(String str, boolean isQuestStr) {
        try {
            Log.e("SPEECH", "speakString() is called");
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
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
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

    //==================================================================================
    private boolean popupShownOnce = false;

    //============================================================================
    private int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    private int minWordsCount = 0;
    private RelativeLayout questionsubans_cardlayout, questionsubans_submit_btn;
    private TextView questionsubans_header, questionsubans_limittext;
    private EditText questionsubans_editetext;
    private CardView questionsubans_card;

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

    public void showKeyboard() {
        try {
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //==============================================================================================================

    private void showBottemBarForSurvey() {
        //if (isSurveyQuestion && showNavigateArrow) {
        bottomswipe_view.setVisibility(View.VISIBLE);
        showsolution_img.setVisibility(View.GONE);
        if ((assessmentScore != null)) {
            gotonextscreen_mainbtn.setVisibility(View.VISIBLE);
        } else {
            gotonextscreen_mainbtn.setVisibility(View.GONE);
        }
        if (cardCount == 0) {
            gotopreviousscreen_mainbtn.setVisibility(View.GONE);
        }
        //}
    }

    private String userSubjectiveAns = "";

    private void addAnswerOnFirebase(String answer) {
        if ((answer != null)) {
            String node = "userCourseResponse/user" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/course" + courseId + "/card" + mainCourseCardClass.getCardId() + "/answer";
            OustFirebaseTools.getRootRef().child(node).setValue(answer);
        }
    }

    public void openExitPopup(final LinearLayout layout, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        exitApiCall();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (layout != null)
                            OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learningoption_backa);
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setTitle("Please Confirm");
        alert.show();
    }

    private void exitApiCall() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                String user_id = activeUser.getStudentid();

                String get_delete_api = OustSdkApplication.getContext().getResources().getString(R.string.delete_user);
                get_delete_api = get_delete_api.replace("{userId}", user_id);
                OustLogDetailHandler.getInstance().setUserForcedOut(true);
                get_delete_api = HttpManager.getAbsoluteUrl(get_delete_api);

                ApiCallUtils.doNetworkCall(Request.Method.DELETE, get_delete_api, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success")) {
                            Intent intent1 = new Intent(OustSdkApplication.getContext(), LogoutMsgActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (questions != null && questions.getExitMessage() != null) {
                                intent1.putExtra("message", questions.getExitMessage());
                            }
                            if (getActivity() != null) {
                                getActivity().startActivity(intent1);
                            }
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "" + error);
                        OustSdkTools.showToast(OustStrings.getString("restart_msg"));
                    }
                });


                /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, get_delete_api, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null && response.optBoolean("success")) {
                            Intent intent1 = new Intent(OustSdkApplication.getContext(), LogoutMsgActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (questions != null && questions.getExitMessage() != null) {
                                intent1.putExtra("message", questions.getExitMessage());
                            }
                            if(getActivity()!=null) {
                                getActivity().startActivity(intent1);
                            }
                            if(getActivity()!=null)
                            {
                                getActivity().finish();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "" + error);
                        OustSdkTools.showToast(OustStrings.getString("restart_msg"));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put("api-key", OustPreferences.get("api_key"));
                            params.put("org-id", OustPreferences.get("tanentid"));
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        return params;
                    }
                };
                jsonObjReq.setShouldCache(false);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 2, 100000f));
                OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");*/
            }
        } catch (Exception e) {
        }
    }

    public int countWords(String sentence) {

       /* int count=0;

        char ch[]= new char[sentence.length()];
        //String [] lines = sentence.split(System.getProperty("line.separator"));
        //count = lines.length;
         for(int i=0;i<sentence.length();i++)
        {
            ch[i]= sentence.charAt(i);
            if( ((i>0)&&(ch[i]!=' ')&&(ch[i-1]==' ')) || ((ch[0]!=' ')&&(i==0)) || ((ch[i]=='\n') && i!=sentence.length()-1) )
                count++;
        }
        return count;*/

        if (sentence == null || sentence.trim().isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(sentence);
        return tokens.countTokens();
    }

    /*public void setVideoOverlay(boolean videoOverlay) {
        this.videoOverlay = videoOverlay;
    }*/
    //private boolean isVideoOverlay = false;

    long[] extraAdGroupTimesMs = null;
    long[] extraAdGroupTimesMs2 = null;
    boolean[] extraPlayedAdGroups = {true, true, true};
    int autoPause = 0;
    double current_exoplayer_sec = 0;
    private Timer video_overlay_timer = null;

    public boolean isQuesttsEnabled() {
        return questtsEnabled;
    }

    private boolean questtsEnabled;

    public List<DTOCourseCard> getCourseCardClassList() {
        return courseCardClassList;
    }

    public void setCourseCardClassList(List<DTOCourseCard> courseCardClassList) {
        this.courseCardClassList = courseCardClassList;
    }

    private List<DTOCourseCard> courseCardClassList = null;


    private void stopVideoOverlayTimer() {
        Log.d(TAG, "stopVideoOverlayTimer: ");
        autoPause = 0;
        if (video_overlay_HandlerTimer != null) {
            video_overlay_HandlerTimer.removeCallbacks(runnableTimer);
        }
//        if (video_overlay_timer != null) {
//            video_overlay_timer.cancel();
//            video_overlay_timer.purge();
//            video_overlay_timer = null;
//        }
        OustStaticVariableHandling.getInstance().setVideoOverlayQuestion(false);
    }

    private void startVideoOverlayTimer() {
        try {
            OustStaticVariableHandling.getInstance().setVideoOverlayQuestion(true);

            Log.d(TAG, "startVideoOverlayTImer: ");
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                public void run() {
            runnableTimer = () -> {
                try {
                    if (customExoPlayerView != null && customExoPlayerView.getPauseButtonVisibility() == View.VISIBLE) {
                        long exoPlayer_position = customExoPlayerView.getSimpleExoPlayer().getCurrentPosition();
                        current_exoplayer_sec = exoPlayer_position / 100;
                        //current_exoplayer_sec = Math.round((current_exoplayer_sec * 10) * 10) / 100.0;

                        //Log.d(TAG, "video overlay exoplayer position: "+exoPlayer_position+" -- Exoplayer_sec:"+current_exoplayer_sec+" -- autoPause:"+autoPause);
                        if (extraAdGroupTimesMs != null) {
                            for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                                //Log.d(TAG, "run: exoplayer:"+exoPlayer_position+" -- Exoplayer_sec:"+current_exoplayer_sec+"-- group:"+extraAdGroupTimesMs[i] / 100);
                                if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                                    autoPause = i + 1;
                                    Message message = video_overlay_Handler.obtainMessage(1, "Start");
                                    message.sendToTarget();
                                    break;
                                }
                            }
                        }
                    }
                    video_overlay_HandlerTimer.postDelayed(runnableTimer, 100);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            };
            video_overlay_HandlerTimer.post(runnableTimer);
//                }
//            }, 0, 100);

//            video_overlay_timer = timer;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Handler video_overlay_Handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try {
                Log.d(TAG, "handleMessage: about to pause");
                if (customExoPlayerView != null) {
                    if (customExoPlayerView.getPauseButtonVisibility() == View.VISIBLE) {
                        customExoPlayerView.performPauseclick();
                    }
                    showVideoOverlayQuestions();
                }

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    String fragmentTag = "VideoOverlay";

    private void showVideoOverlayQuestions() {
        Log.d(TAG, "showVideoOverlayQuestions: " + autoPause + " === zexoxp:" + zeroXpForQCard);
        framelayout_video_overlay.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.anim_slidein);

        /*if(autoPause==0){
            //setAutoPause();
            autoPause=1;
        }*/
        int questionNo = autoPause - 1;

        DTOVideoOverlay videoOverlay = questions.getListOfVideoOverlayQuestion().get(questionNo);
        //CourseCardClass courseCardClass = courseCardClassList.get(1);

        int savedCardID = (int) videoOverlay.getChildCoursecardId();
        DTOCourseCard courseCardClass = videoOverlay.getChildQuestionCourseCard();
        //courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);

        if (RoomHelper.getQuestionById(courseCardClass.getqId()) != null) {
            courseCardClass.setQuestionCategory(courseCardClass.getQuestionData().getQuestionCategory());
            courseCardClass.setQuestionType(courseCardClass.getQuestionData().getQuestionType());
        }

        OustPreferences.saveTimeForNotification("VideoOverlayCardStartTime", System.currentTimeMillis());
        OustPreferences.saveTimeForNotification("VideoOverlayCardPauseTime", extraAdGroupTimesMs[autoPause - 1]);
        OustPreferences.saveTimeForNotification("VideoOverlayCardTotalVideoTime", customExoPlayerView.getSimpleExoPlayer().getDuration());
        if (!isReviewMode) {
            OustPreferences.saveTimeForNotification("VideoOverlayCardCurrentPositionTime", customExoPlayerView.getSimpleExoPlayer().getCurrentPosition());
        }
        OustPreferences.saveintVar("VideoOverlayCardNumberOfAnswered", autoPause);

        Log.d(TAG, "showVideoOverlayQuestions: category:" + courseCardClass.getQuestionCategory() + " -- type:" + courseCardClass.getQuestionType() + " -- cardId:" + savedCardID + " --- duration" + customExoPlayerView.getSimpleExoPlayer().getDuration());

        if (!OustPreferences.getAppInstallVariable("isLayout4")) {
            if (((courseCardClass.getQuestionType() != null) && (courseCardClass.getQuestionType().equals(QuestionType.FILL)) ||
                    (courseCardClass.getQuestionType().equals(QuestionType.FILL_1))) ||
                    ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.MATCH))) ||
                    ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.CATEGORY))) ||
                    ((courseCardClass.getQuestionCategory() != null) && (courseCardClass.getQuestionCategory().equals(QuestionCategory.HOTSPOT)))) {
                LearningPlayFragmentNew fragment = new LearningPlayFragmentNew();
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                fragment.setLearningModuleInterface(learningModuleInterface);
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setLearningcard_progressVal(learningcardProgress);
                fragment.setZeroXpForQCard(zeroXpForQCard);
                fragment.setCardBackgroundImage(cardBackgroundImage);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setCourseId(courseId);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setFavCardDetailsList(favCardDetailsList, "" + courseCardClass.getCardId());
                fragment.setVideoOverlay(true);

            } else {
                OustStaticVariableHandling.getInstance().setLearniCardSwipeble(false);
                LearningPlayFragment fragment = new LearningPlayFragment();
                fragment.setLearningModuleInterface(learningModuleInterface);
                transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
                fragment.setLearningcard_progressVal(learningcardProgress);
                fragment.setQuesttsEnabled(questtsEnabled);
                fragment.setZeroXpForQCard(zeroXpForQCard);
                fragment.setCourseLevelClass(courseLevelClass);
                fragment.setCardBackgroundImage(cardBackgroundImage);
                fragment.setCourseId(courseId);
                fragment.setIsRMFavourite(isRMFavourite);
                fragment.setFavCardDetailsList(favCardDetailsList);
                fragment.setMainCourseCardClass(courseCardClass);
                fragment.setVideoOverlay(true);
                fragment.setProceedOnWrong(mainCourseCardClass.isProceedOnWrong());

                //fragment.setQuestions(currentCourseCardClass.getQuestionData());
            }
            transaction.commit();
        } else {
            if ((questions.getQuestionType().equalsIgnoreCase(QuestionType.MCQ) || questions.getQuestionType().equalsIgnoreCase(QuestionType.TRUE_FALSE)) && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                openMCQFragment(transaction, courseCardClass);
            } else if (questions.getQuestionType().equalsIgnoreCase(QuestionType.MRQ) && (questionCategory == null || !questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                openMRQFragment(transaction, courseCardClass);
            } else {
                if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equals(QuestionCategory.LONG_ANSWER) && !questions.isMediaUploadQues() && !questions.isLearningPlayNew()) {
                    openLongQuestionFragment(transaction, courseCardClass);
                }
            }
        }
    }

    private void openMCQFragment(FragmentTransaction transaction, DTOCourseCard dtoCourseCard) {
        MCQuestionFragment fragment = new MCQuestionFragment();
        transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
        fragment.setAssessmentQuestion(false);
        fragment.setLearningModuleInterface(learningModuleInterface);
        fragment.setShowNavigateArrow(false);
        fragment.setSurveyQuestion(false);
        fragment.setMainCourseCardClass(dtoCourseCard);
        fragment.setCourseLevelClass(courseLevelClass);
        fragment.setQuestions(dtoCourseCard.getQuestionData());
        fragment.setVideoOverlay(true);
        fragment.setProceedOnWrong(mainCourseCardClass.isProceedOnWrong());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openMRQFragment(FragmentTransaction transaction, DTOCourseCard dtoCourseCard) {
        MRQuestionFragment fragment = new MRQuestionFragment();
        transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
        fragment.setAssessmentQuestion(false);
        fragment.setLearningModuleInterface(learningModuleInterface);
        fragment.setShowNavigateArrow(false);
        fragment.setSurveyQuestion(false);
        fragment.setMainCourseCardClass(dtoCourseCard);
        fragment.setCourseLevelClass(courseLevelClass);
        fragment.setQuestions(dtoCourseCard.getQuestionData());
       /* fragment.setVideoOverlay(true);
        fragment.setProceedOnWrong(mainCourseCardClass.isProceedOnWrong());*/
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openLongQuestionFragment(FragmentTransaction transaction, DTOCourseCard dtoCourseCard) {
        LongQuestionFragment fragment = new LongQuestionFragment();
        transaction.replace(R.id.framelayout_video_overlay, fragment, fragmentTag);
        fragment.setAssessmentQuestion(false);
        fragment.setLearningModuleInterface(learningModuleInterface);
        fragment.setShowNavigateArrow(false);
        fragment.setSurveyQuestion(false);
        fragment.setMainCourseCardClass(dtoCourseCard);
        fragment.setCourseLevelClass(courseLevelClass);
        fragment.setQuestions(dtoCourseCard.getQuestionData());
        fragment.setVideoOverlay(true);
        // fragment.setProceedOnWrong(mainCourseCardClass.isProceedOnWrong());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void closeChildFragment() {
        Log.d(TAG, "closeChildFragment: " + current_exoplayer_sec + " --- no of questions:" + autoPause);
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlay");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        //learningview_slideanim

        if (childFragment != null) {
            Log.d(TAG, "closeChildFragment: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();
            //transaction.remove(childFragment).commit();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "run: timertask");
                        Message message = video_overlay_closechild_handler.obtainMessage(1, "Start");
                        message.sendToTarget();

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }, 750);
        } else {
            boolean isPauseAgain = false;
            if (extraAdGroupTimesMs != null) {
                for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                    if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                        autoPause = i + 1;
                        isPauseAgain = true;
                        Message message = video_overlay_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                        break;
                    }
                }
            }

            if (!isPauseAgain) {
                framelayout_video_overlay.setVisibility(View.GONE);
                if (customExoPlayerView != null) {
                    customExoPlayerView.performPlayclick();
                }
            }
        }

    }

    public void wrongAnswerAndRestartVideo() {
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlay");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        if (childFragment != null) {
            Log.d(TAG, "wrongAnswerAndRestartVideo: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();

            long seekTime = 0;
            autoPause--;

            if (autoPause > 0) {
                if (extraAdGroupTimesMs != null) {
                    for (int i = (autoPause - 1); i >= 0; i--) {
                        autoPause--;

                        double sec_ = extraAdGroupTimesMs[i] / 100;
                        if (current_exoplayer_sec == sec_) {

                        } else {
                            if (i == 0) {
                                autoPause = 0;
                                seekTime = 0;
                            } else {
                                seekTime = (extraAdGroupTimesMs[i] / 100) + 1;
                            }
                            break;
                        }
                    }
                }
            } else {
                autoPause = 0;
                current_exoplayer_sec = 0;
            }

            current_exoplayer_sec = seekTime;
            if (customExoPlayerView != null) {
                customExoPlayerView.changeExoplayerSeekTime(seekTime * 100);
            }
            framelayout_video_overlay.setVisibility(View.GONE);
            if (customExoPlayerView != null) {
                customExoPlayerView.performPlayclick();
            }
        }
    }

    public void checkAndRestartVideo() {
        Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlay");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.learningview_reverseanimb, R.anim.slide_in_down);
        if (childFragment != null) {
            Log.d(TAG, "checkAndRestartVideo: " + childFragment.getTag());
            EmptyFragment fragment = new EmptyFragment();
            transaction.replace(R.id.framelayout_video_overlay, fragment, "Empty");
            transaction.commit();

            long seekTime = 0;
            autoPause--;
            if (autoPause > 0) {
                if (extraAdGroupTimesMs != null) {
                    int i = autoPause - 1;

                    double sec_ = extraAdGroupTimesMs[i] / 100;
                    if (current_exoplayer_sec == sec_) {
                        //seekTime = current_exoplayer_sec;
                        /*if(autoPause==0){
                            autoPause = 0;
                            seekTime = 0;
                        }else{
                            seekTime = sec;
                        }*/

                        /*if(i==0){
                            autoPause=0;
                            current_exoplayer_sec = 0;
                            if(customExoPlayerView!=null){
                                customExoPlayerView.changeExoplayerSeekTime(0);
                            }
                            framelayout_video_overlay.setVisibility(View.GONE);
                            if (customExoPlayerView != null) {
                                customExoPlayerView.performPlayclick();
                            }
                        }else{*/
                        Message message = video_overlay_Handler.obtainMessage(1, "Start");
                        message.sendToTarget();
                        //}

                        //seekTime = current_exoplayer_sec;
                    } else {
                        autoPause--;
                        seekTime = extraAdGroupTimesMs[i] / 100 + 1;
                        current_exoplayer_sec = seekTime;
                        if (customExoPlayerView != null) {
                            customExoPlayerView.changeExoplayerSeekTime(seekTime * 100);
                        }
                        framelayout_video_overlay.setVisibility(View.GONE);
                        if (customExoPlayerView != null) {
                            customExoPlayerView.performPlayclick();
                        }
                    }

                    /*for (int i = (autoPause-1); i>=0; i--) {
                        autoPause--;
                        int sec = (int)extraAdGroupTimesMs[i] / 1000;
                        if(current_exoplayer_sec == sec){
                            if(i==0){
                                autoPause = 0;
                                break;
                            }
                        }else{
                            seekTime = sec+1;
                            break;
                        }
                    }*/
                }
            } else {
                autoPause = 0;
                current_exoplayer_sec = 0;
                if (customExoPlayerView != null) {
                    customExoPlayerView.changeExoplayerSeekTime(0);
                }
                framelayout_video_overlay.setVisibility(View.GONE);
                if (customExoPlayerView != null) {
                    customExoPlayerView.performPlayclick();
                }
            }

        }
    }

    private Handler video_overlay_closechild_handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.d(TAG, "handleMessage: VOCH");
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Fragment childFragment = getChildFragmentManager().findFragmentByTag("VideoOverlay");

                if (childFragment != null) {
                    Log.d(TAG, "Handler closeChildFragment: " + childFragment.getTag());
                    transaction.remove(childFragment).commit();
                }

                boolean isPauseAgain = false;
                if (extraAdGroupTimesMs != null) {
                    for (int i = autoPause; i < extraAdGroupTimesMs.length; i++) {
                        if (current_exoplayer_sec == (extraAdGroupTimesMs[i] / 100)) {
                            autoPause = i + 1;
                            isPauseAgain = true;
                            Message message = video_overlay_Handler.obtainMessage(1, "Start");
                            message.sendToTarget();
                            break;
                        }
                    }
                }

                if (!isPauseAgain) {
                    framelayout_video_overlay.setVisibility(View.GONE);
                    if (customExoPlayerView != null) {
                        customExoPlayerView.performPlayclick();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    private void setAutoPause() {
        Log.d(TAG, "setAutoPause: ");
        if (runnableTimer != null) {
            long exoPlayer_position = customExoPlayerView.getSimpleExoPlayer().getCurrentPosition();
            int exoplayer_sec = (int) exoPlayer_position / 100;
            boolean isAutoPauseset = false;

            if (extraAdGroupTimesMs != null) {
                for (int i = 0; i < extraAdGroupTimesMs.length; i++) {
                    int current_sec = (int) (extraAdGroupTimesMs[i] / 100);
                    if (exoplayer_sec <= current_sec) {
                        autoPause = i;
                        isAutoPauseset = true;
                        break;
                    }
                }
            }

            if (!isAutoPauseset) {
                autoPause = (extraAdGroupTimesMs != null) ? extraAdGroupTimesMs.length : 0;
            }

            Log.d(TAG, "setAutoPause:" + autoPause);
        }
    }

    private void setDownloadBtn() {
        Log.d(TAG, "setDownloadBtn: ");
        downloadvideo_icon.setVisibility(View.VISIBLE);
        downloadvideo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startDownloadingVideo();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
            }
        });
    }

    private void startDownloadingVideo() {
        DownloadFiles downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                try {
                    setDownloadingPercentage(Integer.valueOf(progress), message);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                // OustSdkTools.showToast(message);
                tempVideoFileName.delete();
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                    final File finalFile = new File(path);
                    tempVideoFileName.renameTo(finalFile);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });

        if (OustSdkTools.checkInternetStatus()) {
            if (!isVideoDownloding) {
                isVideoDownloding = true;
                isDownloadButtonclicked = true;
                OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                String path = Environment.getExternalStorageDirectory() + "/Android/data/com.oustme.oustapp/files";
                tempVideoFileName = new File(path);
                sendToDownloadService(OustSdkApplication.getContext(), videoFilePath, path, videoFileName, false);
            }
        } else {
            OustSdkTools.showToast(OustStrings.getString("no_internet_connection"));
        }
    }

    private void sendToDownloadService(Context context, String downloadPath, String destn, String fileName, boolean isOustLearn) {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
        //DownLoadIntentService.setContext(this);
        context.startService(intent);
        isFileDownloadServiceStarted = true;
    }

    private void setDownloadingPercentage(int percentage, String message) {
        try {
            if (percentage > 0) {
                if (!isDownloadButtonclicked) {
                    isDownloadButtonclicked = true;
                    OustSdkTools.setDownloadGifImage(downloadvideo_icon);
                }

                if (percentage == 100) {
                    //start_videobutton.setClickable(false);
                    OustPreferences.clear(videoFileName);
                    downloadvideo_text.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isDownloadButtonclicked = false;
                            Drawable drawable = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getResources().getString(R.string.newlp_downloaded));
                            if (drawable != null) {
                                downloadvideo_icon.setImageDrawable(drawable);
                            }
                            //start_videobutton.setClickable(true);
                        }
                    }, 3000);
                } else {
                    downloadvideo_text.setText("" + (percentage) + "%");
                }
            }
        } catch (Exception e) {
        }
    }

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if (getActivity() != null) {
            getActivity().registerReceiver(myFileDownLoadReceiver, intentFilter);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (isFileDownloadServiceStarted) {
                    if (intent.getAction() != null) {
                        try {
                            if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                                setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoFileName;
                                final File finalFile = new File(path);
                                if (tempVideoFileName != null && tempVideoFileName.exists()) {
                                    tempVideoFileName.renameTo(finalFile);
                                }
                            } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                                OustSdkTools.showToast(intent.getStringExtra("MSG") + " Please try again");
                                if (tempVideoFileName != null && tempVideoFileName.exists()) {
                                    tempVideoFileName.delete();
                                }
                                OustSdkTools.setImage(downloadvideo_icon, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));
                                isVideoDownloding = false;
                                isDownloadButtonclicked = false;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
    }
}
