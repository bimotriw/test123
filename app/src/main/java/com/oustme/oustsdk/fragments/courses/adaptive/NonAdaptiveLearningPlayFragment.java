package com.oustme.oustsdk.fragments.courses.adaptive;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
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

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.LogoutMsgActivity;
import com.oustme.oustsdk.adapter.common.CustomAreaGrpAdapter;
import com.oustme.oustsdk.adapter.common.CustomBloodGrpAdapter;
import com.oustme.oustsdk.adapter.common.CustomStringArrayAdapter;
import com.oustme.oustsdk.customviews.CustomExoPlayerView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.BitmapCreateListener;
import com.oustme.oustsdk.interfaces.common.CustomVideoControlListener;
import com.oustme.oustsdk.interfaces.course.DialogKeyListener;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.interfaces.course.ReadMoreFavouriteCallBack;
import com.oustme.oustsdk.request.AreaModel;
import com.oustme.oustsdk.request.BloodGrpModel;
import com.oustme.oustsdk.request.CityDataModel;
import com.oustme.oustsdk.request.JobType;
import com.oustme.oustsdk.request.ShiftDataModel;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.ImageChoiceData;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.course.AdaptiveCardDataModel;
import com.oustme.oustsdk.response.course.AdaptiveCourseLevelModel;
import com.oustme.oustsdk.response.course.AdaptiveQuestionData;
import com.oustme.oustsdk.response.course.CardColorScheme;
import com.oustme.oustsdk.response.course.CourseSolutionCard;
import com.oustme.oustsdk.response.course.FavCardDetails;
import com.oustme.oustsdk.response.course.ReadMoreData;
import com.oustme.oustsdk.service.ScreenShotGenerator;
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

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

public class NonAdaptiveLearningPlayFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, ReadMoreFavouriteCallBack, DialogKeyListener,BitmapCreateListener{

    private static final String TAG = "NonAdaptiveLearningPlay";

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
            mainimageoption_layoutb, mainimageoption_layouta, form_submit_btn,accept_button_ll;
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
    private LinearLayout daily_incentive_ll, minGurantee_ll, per_order_earning_ll, monthlyIncentive_ll,
            weekend_incentive_ll, weekly_incentive_ll, add_info_1_ll, add_info_2_ll, tenure_incentive_ll;
    private TextView daily_incentive_text, minGurantee_text, per_order_earning_text, monthlyIncentive_text,
            weekend_incentive_text, weekly_incentive_text, add_info_2_text, tenure_incentive_text, city_name;
    private TextView area_name_1, city_name_1,job_type_name,shift_time_name;
    private LinearLayout longanswer_layout, area_name_ll, city_name_ll;
    private LinearLayout add_info_data_ll;
    private EditText longanswer_editetext;
    private TextView maxanswer_limittext;
    private Button longanswer_submit_btn;

    private boolean isQuesttsEnabled;
    private boolean isRandomizeQuestion = true;
    private boolean isAudioPaused;
    private int totalSetOptions=-1;
    private int noOfAnimationEnd=0;
    private Context mContext;
    private int mWordLength;

    public void setQuesttsEnabled(boolean questtsEnabled) {
        isQuesttsEnabled = questtsEnabled;
    }
    //------------------

    private Handler myHandler, data_Handler, reload_Handler;
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

    private boolean is_Animation_End = false, is_Data_Loaded=false;
    private AlertDialog mAlertDialogLoader;
    private AlertDialog.Builder mAlertBuilder;
    private Timer anim_timer;

    private Runnable data_runnable = new Runnable() {
        public void run() {
            Log.d(TAG,"Not hiding alert - Inside handelr");
            if(mAlertDialogLoader!=null && mAlertDialogLoader.isShowing()){
                Toast.makeText(getContext(),"Please check your internet connection and try again",Toast.LENGTH_LONG).show();
                hideProgressbarAlert();

                if (!is_Data_Loaded) {
                    reload_Handler = new Handler();
                    reload_Handler.postDelayed(reload_runnable, 5000);
                }
            }
        }
    };

    private Runnable reload_runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"Reload runnable");

            if(reload_Handler!=null){
                reload_Handler.removeCallbacks(reload_runnable);
            }

            if (!is_Data_Loaded) {
                mAlertDialogLoader.show();
                mAlertDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                data_Handler = new Handler();
                data_Handler.postDelayed(data_runnable, 10000);
            }
        }
    };

    private TimerTask anim_task = new TimerTask() {
        public void run() {
            Log.d(TAG,"Animation - Inside handelr");
            try {
                if (is_Animation_End) {
                    Message message = anim_Handler.obtainMessage(1, "Start");
                    message.sendToTarget();
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    private Handler anim_Handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            try{
                anim_timer.cancel();
                anim_timer = null;
                if (!is_Data_Loaded) {
                    mAlertDialogLoader.show();
                    mAlertDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    data_Handler = new Handler();
                    data_Handler.postDelayed(data_runnable, 15000);
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void setAssessmentScore(Scores assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    private String cardBackgroundImage;

    public void setCardBackgroundImage(String cardBackgroundImage) {
        this.cardBackgroundImage = cardBackgroundImage;
    }

    private AdaptiveLearningModuleInterface adaptiveLearningModuleInterface;

    public void setAdaptiveLearningModuleInterface(AdaptiveLearningModuleInterface learningModuleInterface) {
        this.adaptiveLearningModuleInterface = learningModuleInterface;
    }

    private List<FavCardDetails> favCardDetailsList;

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList) {
        this.favCardDetailsList = favCardDetailsList;
    }

    private boolean isRMFavourite;

    public void setIsRMFavourite(boolean isRMFavourite) {
        this.isRMFavourite = isRMFavourite;
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learningquiz, container, false);
        initViews(view);
        initModuViewFragment();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (OustSdkTools.textToSpeech != null) {
            OustSdkTools.stopSpeech();
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

            area_name_1 = view.findViewById(R.id.area_name_1);
            city_name_1 = view.findViewById(R.id.city_name_1);
            area_name_ll = view.findViewById(R.id.area_name_ll);
            city_name_ll = view.findViewById(R.id.city_name_ll);

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

            setFont();

            quesvideoLayout.setVisibility(View.GONE);
            learningquiz_imagequestionlayout.setVisibility(View.GONE);
            survey_layout.setVisibility(View.GONE);
            learningquiz_textchoiselayout.setVisibility(View.VISIBLE);
            learningquiz_imagechoiselayout.setVisibility(View.GONE);
            learningquiz_bigimagechoiselayout.setVisibility(View.GONE);
            learningquiz_form_layout.setVisibility(View.GONE);


        }
    }

    private void takeScreenshot() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 141);
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

    private AdaptiveQuestionData questions;

    public void setQuestions(AdaptiveQuestionData questions) {
        this.questions = questions;
        Log.d(TAG, "setQuestions: " + this.questions.getQuestion() + " " + this.questions.getqVideoUrl());

    }

    private boolean zeroXpForQCard;

    public void setZeroXpForQCard(boolean zeroXpForQCard) {
        this.zeroXpForQCard = zeroXpForQCard;
    }

    private AdaptiveCourseLevelModel courseLevelClass;

    public void setCourseLevelClass(AdaptiveCourseLevelModel courseLevelClass) {
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
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private AdaptiveCardDataModel mainCourseCardClass;
    private long questionXp = 20;

    public void setMainCourseCardClass(AdaptiveCardDataModel courseCardClass2) {
        try {
            int savedCardID = (int) courseCardClass2.getCardId();
            this.questionXp = courseCardClass2.getXp();
            this.mainCourseCardClass = courseCardClass2;
            //this.mainCourseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
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

    private AdaptiveCardDataModel randomizeOption(AdaptiveCardDataModel courseCardClass) {
        try {
            if (courseCardClass.getCardType().equalsIgnoreCase("QUESTION") && (courseCardClass.getQuestionData().isRandomize())) {
                AdaptiveQuestionData learningQuestionData = courseCardClass.getQuestionData();
                if ((learningQuestionData.getQuestionType() != null) && (learningQuestionData.getQuestionType() != QuestionType.MRQ)) {
                    if (learningQuestionData.getQuestionCategory() != null) {
                        if (learningQuestionData.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE)) {
                            List<ImageChoiceData> optionList = new ArrayList<>();
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


    public void enableSwipe() {
        mainoption_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
            CardColorScheme cardColorScheme = mainCourseCardClass.getCardColorScheme();
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
            //startQuestionNOHideAnimation(waitTimer);
            if (questions != null) {
                setTitle();
                setQuestionTitle();
                setQuestionType();
                Log.d(TAG, "setStartingData: " + questions.getQuestion());

                if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FORM_TYPE)) {
                    startQuestionNOHideAnimation(waitTimer);
                    showPrevButton();
                    setFormTypeData();
                    loadQuestion();
                    startQuestionNOHideAnimation(waitTimer);
                } else {
                    Log.d(TAG, "setStartingData: not form type");
                    if ((questions.getqVideoUrl() != null) && (!questions.getqVideoUrl().isEmpty())) {
                        hasVideoQuestion = true;
                        setQuestionVideo(questions.getqVideoUrl(), waitTimer);
                    } else {
                        Log.d(TAG, "setStartingData: not video");
                        if ((questions.getImage() != null) && (!questions.getImage().isEmpty()))
                        {
                            hasImageQuestion = true;
                            Log.d(TAG, "setStartingData: havingImage");
                            learningquiz_bigimagechoiselayout.setVisibility(View.VISIBLE);
                            setImageQuestionImage();
                        }
                        startQuestionNOHideAnimation(waitTimer);
                        loadQuestion();
                    }

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "setStartingData: exception");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showPrevButton() {
        bottomswipe_view.setVisibility(View.VISIBLE);
        gotopreviousscreen_mainbtn.setVisibility(View.VISIBLE);
        unfavourite.setVisibility(View.GONE);
        showsolution_img.setVisibility(View.GONE);
        cardprogress_text.setVisibility(View.GONE);
        gotonextscreen_mainbtn.setVisibility(View.GONE);
    }

    private void loadQuestion() {
        Log.d(TAG, "loadQuestion: " + questionCategory);
        if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.IMAGE_CHOICE)))
        {
            isBigImageOption = true;
            isImageOption = true;
            if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageData() != null)) {
                isBigImageOption = false;
                showImageOptions();
            } else {
                showBigImageOptions();
                if (questionType.equals(QuestionType.MRQ)) {
                    choicebigimgaeACheckBox.setVisibility(View.VISIBLE);
                    choicebigimgaeBCheckBox.setVisibility(View.VISIBLE);
                }
            }
            if (isAssessmentQuestion) {
                if (questionType.equals(QuestionType.MCQ)) {
                    setMCQMyImageAnser();
                } else if (questionType.equals(QuestionType.MRQ)) {
                    setMRQMyImageMyAnser();
                }
            }
        } else {
            Log.d(TAG, "loadQuestion: LongAnswer:" + questionCategory);
            isImageOption = false;
            if (questionCategory.equals(QuestionCategory.LONG_ANSWER)) {

                maxWordsCount = questions.getMaxWordCount();
                if (maxWordsCount == 0) {
                    maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
                }
                minWordsCount = questions.getMinWordCount();
//                        if(minWordsCount==0){
//                            minWordsCount=1;
//                        }
                maxanswer_limittext.setText("Words Left : " + maxWordsCount);
                String hintStr = "Type your response\n(minimum words : minWord and maximum words : maxWord)";
                hintStr = hintStr.replace("minWord", ("" + questions.getMinWordCount()));
                hintStr = hintStr.replace("maxWord", ("" + maxWordsCount));
                longanswer_editetext.setHint(hintStr);
                longanswer_editetext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            longanswer_editetext.setHint("");
                        }
                    }
                });
                longanswer_editetext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        longanswer_editetext.setHint("");
                    }
                });
                setAnswerLimitListener();
                if (isAssessmentQuestion) {
                    setLongAnswer();
                }

            } else {
                isImageOption = false;
                showTextOptions();
                if (isAssessmentQuestion && isSurveyQuestion) {
                    if ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE))) {
                        setMCQMyAnser();
                    } else if (questionType.equals(QuestionType.MRQ)) {
                        setMRQMyAnser();
                    }
                }
            }
        }
    }

    private CustomExoPlayerView customExoPlayerView;

    private void setQuestionVideo(String path, final int waitTimer) {
        quesvideoLayout.setVisibility(View.VISIBLE);
        video_player_layout.setVisibility(View.VISIBLE);
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
                startQuestionNOHideAnimation(waitTimer);
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
        customExoPlayerView.initExoPlayer(video_player_layout, mContext, path);
        setPortaitVideoRatio(customExoPlayerView.getSimpleExoPlayerView());
    }

    private void notifyActivityToStartTimer() {
        if (isAssessmentQuestion) {
            try {
                CustomVideoControlListener customVideoControlListener = (CustomVideoControlListener)mContext;
                customVideoControlListener.onVideoEnd();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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

//                ((CustomVideoControlListener)(OustSdkApplication.getContext())).showToolbar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setFormTypeData() {
        try {
            if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("PHONE")) {
                phone_info_ll.setVisibility(View.VISIBLE);
                et_phone_no.requestFocus();
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("AADHAR")) {
//                et_form.setVisibility(View.VISIBLE);
//                et_form.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
//                et_form.setInputType(InputType.TYPE_CLASS_NUMBER);
                aadhar_info_ll.setVisibility(View.VISIBLE);
                aadhar_et_1.requestFocus();
                aadhar_et_1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String output = s.toString();
                        if (output != null && output.length() == 4) {
                            aadhar_et_2.requestFocus();
                        }
                    }
                });

                aadhar_et_2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String output = s.toString();
                        if (output != null && output.length() == 4) {
                            aadhar_et_3.requestFocus();
                        }
                    }
                });


            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("DOB")) {
                tv_dob_form.setVisibility(View.VISIBLE);
                setUserDateOfBirthCalander();
                tv_dob_form.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDateCalender();
                    }
                });
            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("BLOODGROUP")) {
                sp_form.setVisibility(View.VISIBLE);
                sp_form_ll.setVisibility(View.VISIBLE);
                getBloodGroupData();

            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("AREA")) {
                sp_form.setVisibility(View.VISIBLE);
                sp_form_ll.setVisibility(View.VISIBLE);

                city_name_ll.setVisibility(View.VISIBLE);
                city_name_1.setText("" + OustPreferences.get("cityGrpTxt"));

                getAreaGroupData();

            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("CITY")) {
                sp_form.setVisibility(View.VISIBLE);
                sp_form_ll.setVisibility(View.VISIBLE);
                getAllCityData();

            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("SHIFTS")) {
                sp_form.setVisibility(View.VISIBLE);
                sp_form_ll.setVisibility(View.VISIBLE);
                initializeExtraInfoViews();
                getShiftGroupData();

            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("JOBTYPE")) {
                city_name_ll.setVisibility(View.VISIBLE);
                city_name_1.setText("" + OustPreferences.get("cityGrpTxt"));
                area_name_ll.setVisibility(View.VISIBLE);
                area_name_1.setText("" + OustPreferences.get("areaGrpTxt"));

                sp_form.setVisibility(View.VISIBLE);
                sp_form_ll.setVisibility(View.VISIBLE);
                getJobTypes();
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("IFSC")) {
                et_form.setVisibility(View.VISIBLE);
                et_form.setInputType(InputType.TYPE_CLASS_TEXT);
                et_form.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11), new InputFilter.AllCaps()});

                et_form.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try{
                            et_form.setError(null);
                            String output = s.toString();
                            if(output.contains(" ")){
                                et_form.setError("Please avoid space !");
                            }
                            if(output.trim().length()>11){
                                et_form.setText(output.substring(0,12));
                            }
                        }catch (Exception e){}

                    }
                });


            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("PAN")) {
//                pan_card_ll.setVisibility(View.VISIBLE);
//                et_pan_1.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        String str = s.toString();
//                        if (str != null && str.length() == 3) {
//                            et_pan_2.requestFocus();
//                        }
//                    }
//                });
                et_form.setVisibility(View.VISIBLE);
                et_form.setInputType(InputType.TYPE_CLASS_TEXT);
                et_form.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), new InputFilter.AllCaps()});

            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("DL")) {
                et_form.setVisibility(View.VISIBLE);
                et_form.setInputType(InputType.TYPE_CLASS_TEXT);
                et_form.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("BANKACCOUNTNUM")) {
                et_form.setVisibility(View.VISIBLE);
                et_form.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("ADDRESS")) {
                form_address_ll.setVisibility(View.VISIBLE);
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("MCQ")) {
                isImageOption = false;
                hasImageQuestion = false;
                showTextOptions();
                if (myHandler != null) {
                    for(int j = 0; j<learningquiz_textchoiselayout.getChildCount(); j++)
                    {
                        View view = learningquiz_textchoiselayout.getChildAt(j);
                        view.setClickable(false);
                    }
                    myHandler.postDelayed(showAllOption, 300);
                }
            } else {
                et_form.setVisibility(View.VISIBLE);
                et_form.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            form_submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyFormData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initializeExtraInfoViews() {
        daily_incentive_ll = learningquiz_form_layout.findViewById(R.id.daily_incentive_ll);
        minGurantee_ll = learningquiz_form_layout.findViewById(R.id.minGurantee_ll);
        per_order_earning_ll = learningquiz_form_layout.findViewById(R.id.per_order_earning_ll);
        monthlyIncentive_ll = learningquiz_form_layout.findViewById(R.id.monthlyIncentive_ll);
        weekend_incentive_ll = learningquiz_form_layout.findViewById(R.id.weekend_incentive_ll);
        weekly_incentive_ll = learningquiz_form_layout.findViewById(R.id.weekly_incentive_ll);
        add_info_1_ll = learningquiz_form_layout.findViewById(R.id.add_info_1_ll);
        add_info_2_ll = learningquiz_form_layout.findViewById(R.id.add_info_2_ll);
        tenure_incentive_ll = learningquiz_form_layout.findViewById(R.id.tenure_incentive_ll);


        daily_incentive_text = learningquiz_form_layout.findViewById(R.id.daily_incentive_text);
        minGurantee_text = learningquiz_form_layout.findViewById(R.id.minGurantee_text);
        per_order_earning_text = learningquiz_form_layout.findViewById(R.id.per_order_earning_text);
        monthlyIncentive_text = learningquiz_form_layout.findViewById(R.id.monthlyIncentive_text);
        weekend_incentive_text = learningquiz_form_layout.findViewById(R.id.weekend_incentive_text);
        weekly_incentive_text = learningquiz_form_layout.findViewById(R.id.weekly_incentive_text);
        add_info_data_ll = learningquiz_form_layout.findViewById(R.id.add_info_data_ll);
        add_info_2_text = learningquiz_form_layout.findViewById(R.id.add_info_2_text);
        tenure_incentive_text = learningquiz_form_layout.findViewById(R.id.tenure_incentive_text);
        city_name = learningquiz_form_layout.findViewById(R.id.city_name);
        job_type_name= learningquiz_form_layout.findViewById(R.id.job_type_name);
        shift_time_name= learningquiz_form_layout.findViewById(R.id.shift_time_name);

        accept_button_ll= learningquiz_form_layout.findViewById(R.id.accept_button_ll);

    }

    private ArrayList<ShiftDataModel> shiftDataModels;

    private void getShiftGroupData() {
        try {
            final String message = "/areaList/city" + OustPreferences.getTimeForNotification("cityGrpId") + "/area" + OustPreferences.getTimeForNotification("areaGrpId") + "/jobType/jobType" + OustPreferences.getTimeForNotification("selJobShiftId") + "/shiftList";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            HashMap<String, Object> shiftTypeGrpMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (shiftTypeGrpMap != null && shiftTypeGrpMap.size() > 0) {
                                shiftDataModels = new ArrayList<>();
                                for (String shiftKey : shiftTypeGrpMap.keySet()) {
                                    HashMap<String, Object> shiftTypeMap = (HashMap<String, Object>) shiftTypeGrpMap.get(shiftKey);
                                    ShiftDataModel shiftDataModel = new ShiftDataModel();
                                    shiftDataModel.setData(shiftTypeMap);
                                    shiftDataModels.add(shiftDataModel);
                                }
                                getShiftTypeSpinnerData();
                            }
                        } else {
                            OustSdkTools.showToast("Sorry! No data Found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getShiftTypeSpinnerData() {
        if (shiftDataModels != null && shiftDataModels.size() > 0) {
            Collections.sort(shiftDataModels, new Comparator<ShiftDataModel>() {
                @Override
                public int compare(ShiftDataModel o1, ShiftDataModel o2) {
                    return o1.getShift().compareTo(o2.getShift());
                }
            });
        }
        ArrayList<String> shiftTypeStrs = new ArrayList<>();
        if (shiftDataModels != null && shiftDataModels.size() > 1) {
            ShiftDataModel shiftDataModel = new ShiftDataModel();
            shiftDataModel.setShift("Choose your shift");
            shiftDataModels.add(0, shiftDataModel);
        }
        if (shiftDataModels != null) {
            for (int i = 0; i < shiftDataModels.size(); i++) {
                shiftTypeStrs.add(shiftDataModels.get(i).getShift());
            }
        }

        setShiftSpinnerData(shiftTypeStrs);
    }

    private ArrayList<JobType> jobTypeArrayList;

    private void getJobTypes() {
        try {
            final String message = "/areaList/city" + OustPreferences.getTimeForNotification("cityGrpId") + "/area" + OustPreferences.getTimeForNotification("areaGrpId") + "/jobType";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            HashMap<String, Object> jobTypeGrpMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (jobTypeGrpMap != null && jobTypeGrpMap.size() > 0) {
                                jobTypeArrayList = new ArrayList<>();
                                for (String areaKey : jobTypeGrpMap.keySet()) {
                                    HashMap<String, Object> jobTypeMap = (HashMap<String, Object>) jobTypeGrpMap.get(areaKey);
                                    JobType jobType = new JobType();
                                    jobType.setData(jobTypeMap);
                                    jobTypeArrayList.add(jobType);
                                }
                                getJobTypeSpinnerData();
                            }
                        } else {
                            OustSdkTools.showToast("Sorry! No data Found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAllCityData() {
        try {
            Log.d(TAG,"Get All city data");
            is_Data_Loaded = false;
            showProgressbarAlert("Fetching Cities");

            final String message = "/cityList/";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Map<String, Object> allCityMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCityMap != null) {
                            cityDataModels = new ArrayList<>();
                            for (String cityKey : allCityMap.keySet()) {
                                Map<String, Object> lpMap = (Map<String, Object>) allCityMap.get(cityKey);
                                CityDataModel cityDataModel = new CityDataModel();
                                cityDataModel.setId(OustSdkTools.convertToLong(lpMap.get("id")));
                                if (lpMap.get("name") != null)
                                    cityDataModel.setName((String) lpMap.get("name"));
                                cityDataModels.add(cityDataModel);
                            }

                            setCitySpinnerData();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String cityGrpTxt;
    private long cityId;

    private void setCitySpinnerData() {
        Log.d(TAG,"CIty DataLoaded");
        is_Data_Loaded = true;
        hideProgressbarAlert();

        ArrayList<String> cityList = new ArrayList<>();
        if (cityDataModels != null && cityDataModels.size() > 0) {
            Collections.sort(cityDataModels, new Comparator<CityDataModel>() {
                @Override
                public int compare(CityDataModel o1, CityDataModel o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        if (cityDataModels != null && cityDataModels.size() > 1) {
            CityDataModel cityDataModel = new CityDataModel();
            cityDataModel.setId(0);
            cityDataModel.setName("Choose your city");
            cityDataModels.add(0, cityDataModel);
        }

        for (int i = 0; i < cityDataModels.size(); i++) {
            cityList.add(cityDataModels.get(i).getName());
        }
        CustomStringArrayAdapter customAreaGrpAdapter = new CustomStringArrayAdapter(getActivity(), R.layout.sp_item, cityList);
        sp_form.setAdapter(customAreaGrpAdapter);

        sp_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityGrpTxt = cityDataModels.get(position).getName();
                cityId = cityDataModels.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isScreenShotCreated=false;
    private void showAreaAndPaymentOption(ShiftDataModel shiftDataModel) {
        try {
            extra_tier_info_ll.setVisibility(View.VISIBLE);
            accept_button_ll.setVisibility(View.VISIBLE);

            city_name.setText("" + OustPreferences.get("cityGrpTxt"));
            area_name.setText("" + OustPreferences.get("areaGrpTxt"));
            job_type_name.setText(""+OustPreferences.get("selJobShift"));
            shift_time_name.setText(""+shiftDataModel.getShift());

            accept_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked && !isScreenShotCreated){
                        takeScreenshot();
                    }
                }
            });

            if (shiftDataModel != null) {
                if (shiftDataModel.getDailyIncentive() != null && !shiftDataModel.getDailyIncentive().isEmpty()) {
                    daily_incentive_ll.setVisibility(View.VISIBLE);
                    daily_incentive_text.setText(shiftDataModel.getDailyIncentive());
                } else {
                    daily_incentive_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getMinGurantee() != null && !shiftDataModel.getMinGurantee().isEmpty()) {
                    minGurantee_ll.setVisibility(View.VISIBLE);
                    minGurantee_text.setText(shiftDataModel.getMinGurantee());
                } else {
                    minGurantee_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getMonthlyIncentive() != null && !shiftDataModel.getMonthlyIncentive().isEmpty()) {
                    monthlyIncentive_ll.setVisibility(View.VISIBLE);
                    monthlyIncentive_text.setText(shiftDataModel.getMonthlyIncentive());
                } else {
                    monthlyIncentive_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getPerOrderEarning() != null && !shiftDataModel.getPerOrderEarning().isEmpty()) {
                    per_order_earning_ll.setVisibility(View.VISIBLE);
                    per_order_earning_text.setText(shiftDataModel.getPerOrderEarning());
                } else {
                    per_order_earning_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getWeeklyIncentive() != null && !shiftDataModel.getWeeklyIncentive().isEmpty()) {
                    weekly_incentive_ll.setVisibility(View.VISIBLE);
                    weekly_incentive_text.setText(shiftDataModel.getWeeklyIncentive());
                } else {
                    weekly_incentive_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getWeekendIncentive() != null && !shiftDataModel.getWeekendIncentive().isEmpty()) {
                    weekend_incentive_ll.setVisibility(View.VISIBLE);
                    weekend_incentive_text.setText(shiftDataModel.getWeekendIncentive());
                } else {
                    weekend_incentive_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getTenureIncentive() != null && !shiftDataModel.getTenureIncentive().isEmpty()) {
                    tenure_incentive_ll.setVisibility(View.VISIBLE);
                    tenure_incentive_text.setText(shiftDataModel.getTenureIncentive());
                } else {
                    tenure_incentive_ll.setVisibility(View.GONE);
                }
                if (shiftDataModel.getAdditionalInfo1() != null && !shiftDataModel.getAdditionalInfo1().isEmpty()) {
                    add_info_1_ll.setVisibility(View.VISIBLE);
                    String[] array1 = shiftDataModel.getAdditionalInfo1().split("\n");
                    if (shiftDataModel.getAdditionalInfo2() != null && !shiftDataModel.getAdditionalInfo2().isEmpty()) {
                        add_info_2_ll.setVisibility(View.VISIBLE);
                        String[] array2 = shiftDataModel.getAdditionalInfo2().split("\n");
                        setShiftAdditionalData(ArrayUtils.addAll(array1, array2));
                    } else {
                        setShiftAdditionalData(array1);
                        add_info_2_ll.setVisibility(View.GONE);
                    }
                } else {
                    add_info_1_ll.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createScreenshot() {
    }

    private void setShiftAdditionalData(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.shift_additional_row, null);
            TextView info_text = view.findViewById(R.id.info_text);
            info_text.setText(strings[i]);
            add_info_data_ll.addView(view);
        }
    }

    private void verifyFormData() {
        try {
            float challengerScore = 0;
            if (mainCourseCardClass != null) {
                challengerScore = (float) mainCourseCardClass.getXp();
            }
            if (questions.getDropdownType() != null && questions.getDropdownType().equals("BLOODGROUP")) {
                if (questions.isMandatory()) {
                    if (bldGrpTxt.equals("Choose your blood group")) {
                        OustSdkTools.showToast("Please choose your Blood Group !");
                        return;
                    }
                }
                pressNextButton(bldGrpTxt, (int) challengerScore);
            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("JOBTYPE")) {
                if (questions.isMandatory()) {
                    if (selJobShift.equals("Choose your Job preference")) {
                        OustSdkTools.showToast("Please choose your Job Preference !");
                        return;
                    }
                }
                OustPreferences.save("selJobShift", selJobShift);
                OustPreferences.saveTimeForNotification("selJobShiftId", selJobShiftId);
                pressNextButton(selJobShift, (int) challengerScore);
            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("SHIFTS")) {
                if (questions.isMandatory()) {
                    if (shiftTxt.equals("Choose your shift")) {
                        OustSdkTools.showToast("Please choose your shift !");
                        return;
                    }
                }
                if (!accept_button.isChecked()) {
                    OustSdkTools.showToast("Please accept and then continue !");
                    return;
                }
                pressNextButton(shiftTxt, (int) challengerScore);
            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("AREA")) {
                if (questions.isMandatory()) {
                    if (areaGrpTxt.equals("") || areaGrpTxt.equals("Choose your area")) {
                        OustSdkTools.showToast("Please choose your Area Group !");
                        return;
                    }
                }
                OustPreferences.save("areaGrpTxt", areaGrpTxt);
                OustPreferences.saveTimeForNotification("areaGrpId", areaGrpId);
                pressNextButton(areaGrpTxt, (int) challengerScore);
            } else if (questions.getDropdownType() != null && questions.getDropdownType().equals("CITY")) {
                if (questions.isMandatory()) {
                    if (cityGrpTxt.equals("") || cityGrpTxt.equals("Choose your city")) {
                        OustSdkTools.showToast("Please choose your City Group !");
                        return;
                    }
                }
                OustPreferences.save("cityGrpTxt", cityGrpTxt);
                OustPreferences.saveTimeForNotification("cityGrpId", cityId);
                pressNextButton(cityGrpTxt, (int) challengerScore);
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("DOB")) {
                if (questions.isMandatory()) {
                    if (tv_dob_form.getText().toString().equals("Enter DOB here")) {
                        OustSdkTools.showToast("Please choose your date of birth !");
                        return;
                    }
                }
                validateAge(challengerScore);
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("ADDRESS")) {
                et_address1.setError(null);
                et_address2.setError(null);
                et_address3.setError(null);
                String address = "";
                if (et_address1.getText().toString() != null && !et_address1.getText().toString().isEmpty()) {
                    address = address + et_address1.getText().toString();
                    if (et_address2.getText().toString() != null && !et_address2.getText().toString().isEmpty()) {
                        address = address + " " + et_address2.getText().toString();
                        if (et_address3.getText().toString() != null && !et_address3.getText().toString().isEmpty()) {
                            address = address + " " + et_address3.getText().toString();
                            pressNextButton(address, (int) challengerScore);
                        } else {
                            if (questions.isMandatory()) {
                                et_address3.setError("Enter address line 3");
                            } else {
                                pressNextButton(address, (int) challengerScore);
                            }
                        }
                    } else {
                        if (questions.isMandatory()) {
                            et_address2.setError("Enter address line 2");
                        } else {
                            pressNextButton(address, (int) challengerScore);
                        }
                    }
                } else {
                    if (questions.isMandatory()) {
                        et_address1.setError("Enter address line 1");
                    } else {
                        pressNextButton(address, (int) challengerScore);
                    }
                }
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("PHONE")) {
                et_phone_no.setError(null);
                String phoneNum = et_phone_no.getText().toString();
                if (phoneNum != null && phoneNum.length() > 0) {
                    if (phoneNum.trim().length() == 10) {
                        if (phoneNum.startsWith("9") || phoneNum.startsWith("8") || phoneNum.startsWith("7") || phoneNum.startsWith("6")) {
                            pressNextButton(phoneNum, (int) challengerScore);
                        } else {
                            et_phone_no.setError("Please enter a valid 10 digit mobile number!");
                        }
                    } else {
                        et_phone_no.setError("Please enter a valid 10 digit mobile number!");
                    }
                } else {
                    if (questions.isMandatory()) {
                        et_phone_no.setError("Please enter your 10 digit mobile number!");
                    } else {
                        pressNextButton("", (int) challengerScore);
                    }
                }
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("AADHAR")) {
                aadhar_et_1.setError(null);
                aadhar_et_2.setError(null);
                aadhar_et_3.setError(null);

                String aadharNo = "";
                String str1 = aadhar_et_1.getText().toString();
                if (str1 != null && str1.length() == 4) {
                    aadharNo = aadharNo + str1;

                    String str2 = aadhar_et_2.getText().toString();
                    if (str2 != null && str2.length() == 4) {
                        aadharNo = aadharNo + str2;

                        String str3 = aadhar_et_3.getText().toString();
                        if (str3 != null && str3.length() == 4) {
                            aadharNo = aadharNo + str3;
                            pressNextButton(aadharNo, (int) challengerScore);
                        } else {
                            if (questions != null && !questions.isMandatory()) {
                                pressNextButton(aadharNo, (int) challengerScore);
                            } else
                                aadhar_et_3.setError("Please enter !");
                        }

                    } else {
                        if (questions != null && !questions.isMandatory()) {
                            pressNextButton(aadharNo, (int) challengerScore);
                        } else
                            aadhar_et_2.setError("Please enter !");
                    }

                } else {
                    if (questions != null && !questions.isMandatory()) {
                        pressNextButton(aadharNo, (int) challengerScore);
                    } else
                        aadhar_et_1.setError("Please enter !");
                }

            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("IFSC")) {
                et_form.setError(null);
                String ifsc = et_form.getText().toString();
                if (ifsc != null && ifsc.length() > 0) {
                    if (ifsc.trim().length() == 11) {
                        pressNextButton(ifsc, (int) challengerScore);
                    } else {
                        et_form.setError("Please enter a valid IFSC code !");
                    }
                } else {
                    if (questions.isMandatory())
                        et_form.setError("Please enter IFSC code !");
                    else {
                        pressNextButton("", (int) challengerScore);
                    }
                }
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("PAN")) {
                String panNum = et_form.getText().toString();
                if (panNum != null && panNum.length() > 0) {
                    if (panNum.trim().length() == 10) {
                        if (panNum.charAt(3) == 'P' || panNum.charAt(3) == 'p') {
                            pressNextButton(panNum, (int) challengerScore);
                        } else {
                            et_form.setError("Please enter a valid 10 digit pan number!");
                        }
                    } else {
                        et_form.setError("Please enter a valid 10 digit pan number!");
                    }
                } else {
                    if (questions.isMandatory())
                        et_form.setError("Please enter your 10 digit pan number!");
                    else {
                        pressNextButton("", (int) challengerScore);
                    }
                }

//            String str1=et_pan_1.getText().toString();
//            if(str1!=null && str1.trim().length()==3){
//                panNum=panNum+str1;
//
//                String str2 = et_pan_2.getText().toString();
//                if(str2!=null && str2.trim().length()==6){
//                    panNum=panNum+"P"+str2;
//                    pressNextButton(panNum,(int)challengerScore);
//                }else{
//                    if (questions.isMandatory())
//                        et_pan_2.setError("Please enter !");
//                    else {
//                        pressNextButton(panNum, (int) challengerScore);
//                    }
//                }
//            }else{
//                if (questions.isMandatory())
//                    et_pan_1.setError("Please enter !");
//                else {
//                    pressNextButton(panNum, (int) challengerScore);
//                }
//            }

            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("DL")) {
                et_form.setError(null);
                String dlNum = et_form.getText().toString();
                if (dlNum != null && dlNum.length() > 0) {
                    pressNextButton(dlNum, (int) challengerScore);
                } else {
                    if (questions.isMandatory())
                        et_form.setError("Please enter your driving licence number!");
                    else {
                        pressNextButton("", (int) challengerScore);
                    }
                }
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("BANKACCOUNTNUM")) {
                et_form.setError(null);
                String bankAcctNum = et_form.getText().toString();
                if (bankAcctNum != null && bankAcctNum.length() > 0) {
                    pressNextButton(bankAcctNum, (int) challengerScore);
                } else {
                    if (questions.isMandatory())
                        et_form.setError("Please enter your bank account number!");
                    else {
                        pressNextButton("", (int) challengerScore);
                    }
                }
            } else if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("MCQ")) {
                if (userAns != null && !userAns.isEmpty()) {
                    pressNextButton(userAns, (int) challengerScore);
                } else {
                    OustSdkTools.showToast("Please select your answer !");
                }
            } else {
                et_form.setError(null);
                String response = et_form.getText().toString();
                if (response != null && response.length() > 0) {
                    pressNextButton(response, (int) challengerScore);
                } else {
                    if (questions.isMandatory())
                        et_form.setError("Please enter your response!");
                    else {
                        pressNextButton("", (int) challengerScore);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void validateAge(float challengerScore) {
        if (questions.isMandatory()) {
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            if (((nowYear - birthYear) >= 18) && ((nowYear - birthYear) <= 55)) {
                pressNextButton(tv_dob_form.getText().toString(), (int) challengerScore);
            } else {
                String message = "Your DOB : " + tv_dob_form.getText().toString() + " . Is this correct ? You must be between 18-55 years to be a PDP";
                openExitPopup(null, message);
            }
        } else {
            pressNextButton(tv_dob_form.getText().toString(), (int) challengerScore);
        }
    }

    private void pressNextButton(String answer, int challengerScore) {
        learningModuleInterface.setAnswerAndOc(answer, "", challengerScore, true, 0);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(learningModuleInterface!=null)
                {
                    learningModuleInterface.gotoNextScreen();
                }
            }
        }, 500);
    }

    private ArrayList<CityDataModel> cityDataModels;
    private ArrayList<AreaModel> areaModels;

    private void getCityGroupData(final String type) {
        try {

            final String message = "/city/";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Object o = dataSnapshot.getValue();
                            if (o.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                Log.i("list", learningList.toString());
                                for (int i = 0; i < learningList.size(); i++) {
                                    try {
                                        Object object = learningList.get(i);
                                        cityDataModels = new ArrayList<>();
                                        if (object != null) {
                                            Map<String, Object> cityMap = (Map<String, Object>) object;
                                            if (cityMap.containsKey("name")) {
                                                CityDataModel cityDataModel = new CityDataModel();
                                                cityDataModel.setName((String) cityMap.get("name"));
                                                ArrayList<AreaModel> areaModels = new ArrayList<>();
                                                Object areaObjList = cityMap.get("areaList");
                                                List<Object> areaList = (List<Object>) areaObjList;
                                                for (int j = 0; j < areaList.size(); j++) {
                                                    Object areaObj = areaList.get(j);
                                                    if (areaObj != null) {
                                                        Map<String, Object> areaMap = (Map<String, Object>) areaObj;
                                                        if (areaMap != null) {
                                                            try {
                                                                AreaModel areaModel = new AreaModel();
                                                                areaModel.setName((String) areaMap.get("name"));
                                                                if (areaMap.get("tier").getClass().equals(String.class)) {
                                                                    areaModel.setTier((String) areaMap.get("tier"));
                                                                } else if (areaMap.get("tier").getClass().equals(Long.class)) {
                                                                    areaModel.setTier("" + (long) areaMap.get("tier"));
                                                                }
                                                                ArrayList<JobType> jobTypeArrayList = new ArrayList<>();
                                                                Object jobTypeObj = areaMap.get("jobType");
                                                                List<Object> jobList = (List<Object>) jobTypeObj;
                                                                if (jobList != null && jobList.size() > 0) {
                                                                    for (int k = 0; k < jobList.size(); k++) {
                                                                        try {
                                                                            Map<String, Object> jobTypeMap = (Map<String, Object>) jobList.get(k);
                                                                            JobType jobType = new JobType();
                                                                            jobType.setName((String) jobTypeMap.get("name"));
                                                                            List<Object> shiftMap = (List<Object>) jobTypeMap.get("shiftList");
                                                                            if (shiftMap != null && shiftMap.size() > 0) {
                                                                                ArrayList<String> shiftArray = new ArrayList<>();
                                                                                for (int l = 0; l < shiftMap.size(); l++) {
                                                                                    String shift = (String) shiftMap.get(l);
                                                                                    if (shift != null) {
                                                                                        shiftArray.add(shift);
                                                                                    }
                                                                                }
                                                                                jobType.setShiftList(shiftArray);
                                                                                jobTypeArrayList.add(jobType);
                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                        }
                                                                    }
                                                                }
                                                                areaModel.setJobTypeArrayList(jobTypeArrayList);
                                                                areaModels.add(areaModel);

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                            }
                                                        }
                                                    }
                                                }
                                                cityDataModel.setAreaModels(areaModels);
                                                cityDataModels.add(cityDataModel);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }
                                }
                            }

                            if (type.equals("AREA")) {
                                setAreaSpinnerData();
                            } else if (type.equals("SHIFTS")) {
                                filterAreaSpinnerData(type);
                            } else if (type.equals("JOBTYPE")) {
                                filterAreaSpinnerData(type);
                            }
                        } else {
                            OustSdkTools.showToast("Sorry! No data Found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showProgressbarAlert(String text) {
        Log.d(TAG,"Show progress alert");
        try {
            mAlertBuilder = new AlertDialog.Builder(getActivity());
            mAlertBuilder.setCancelable(false);
            LayoutInflater inflater = this.getLayoutInflater();
            View mView = inflater.inflate(R.layout.cpl_loading_progressbar, null);
            TextView textView = mView.findViewById(R.id.textViewLoadMsg);
            textView.setText(text + "...");
            mAlertBuilder.setView(mView);
            mAlertDialogLoader = mAlertBuilder.create();
            mAlertDialogLoader.setCancelable(false);

            anim_timer = new Timer();
            anim_timer.scheduleAtFixedRate(anim_task,0,1000);
        }catch(Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideProgressbarAlert(){
        Log.d(TAG,"hide progress alert");
        if (mAlertDialogLoader != null) {
            mAlertDialogLoader.dismiss();
        }

        if(data_Handler!=null){
            data_Handler.removeCallbacks(data_runnable);
            data_Handler = null;
        }

        if(reload_Handler!=null){
            reload_Handler.removeCallbacks(reload_runnable);
            reload_Handler = null;
        }
    }

    private void getAreaGroupData() {
        try {
            Log.d(TAG,"Get Area data");
            is_Data_Loaded = false;
            showProgressbarAlert("Fetching Areas");

            final String message = "/areaList/city" + OustPreferences.getTimeForNotification("cityGrpId");
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            HashMap<String, Object> areaGrpMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (areaGrpMap != null && areaGrpMap.size() > 0) {
                                areaModels = new ArrayList<>();
                                for (String areaKey : areaGrpMap.keySet()) {
                                    HashMap<String, Object> areaMap = (HashMap<String, Object>) areaGrpMap.get(areaKey);
                                    AreaModel areaModel = new AreaModel();
                                    areaModel.setData(areaMap);
                                    areaModels.add(areaModel);
                                }
                            }
                            setAreaSpinnerData();
                        } else {
                            OustSdkTools.showToast("Sorry! No data Found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String selJobShift = "";
    private long selJobShiftId = 0;

    private void filterAreaSpinnerData(String type) {
        ArrayList<AreaModel> areaModels = cityDataModels.get(0).getAreaModels();
        if (areaModels != null && areaModels.size() > 0) {
            String savedArea = OustPreferences.get("areaGrpTxt");
            if (savedArea != null && !savedArea.isEmpty()) {
                for (int i = 0; i < areaModels.size(); i++) {
                    if (areaModels.get(i).getName().equals(savedArea)) {
                        if (type.equals("JOBTYPE")) {
                            //getJobTypeSpinnerData(areaModels.get(i));
                        } else {
                            filterJobTypes(areaModels.get(i));
                        }
                        break;
                    }
                }
            } else {
                OustSdkTools.showToast("Data not found !");
            }
        }

    }

    private void getJobTypeSpinnerData() {
        ArrayList<String> jobTypeStrs = new ArrayList<>();
        if (jobTypeArrayList != null && jobTypeArrayList.size() > 0) {
            Collections.sort(jobTypeArrayList, new Comparator<JobType>() {
                @Override
                public int compare(JobType o1, JobType o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        if (jobTypeArrayList != null && jobTypeArrayList.size() > 1) {
            JobType jobType = new JobType();
            jobType.setName("Choose your Job preference");
            jobType.setId(0);
            jobTypeArrayList.add(0, jobType);
        }
        if (jobTypeArrayList != null) {
            for (int i = 0; i < jobTypeArrayList.size(); i++) {
                jobTypeStrs.add(jobTypeArrayList.get(i).getName());
            }
        }
        setJobTypeSpinnerData(jobTypeStrs);
    }

    private void setJobTypeSpinnerData(ArrayList<String> jobTypeSpinnerData) {
        CustomStringArrayAdapter customAreaGrpAdapter = new CustomStringArrayAdapter(getActivity(), R.layout.sp_item, jobTypeSpinnerData);

        sp_form.setAdapter(customAreaGrpAdapter);

        sp_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selJobShift = jobTypeArrayList.get(position).getName();
                selJobShiftId = jobTypeArrayList.get(position).getId();
                //Todo later
//                selJobShift = jobTypeSpinnerData.get(position);
//                if (selJobShift.equals("Full Time")) {
//                    job_type_1_ll.setVisibility(View.VISIBLE);
//                    job_type_2_ll.setVisibility(View.GONE);
//                    job_type_3_ll.setVisibility(View.GONE);
//                } else if (selJobShift.equals("Part Time")) {
//                    job_type_1_ll.setVisibility(View.GONE);
//                    job_type_2_ll.setVisibility(View.VISIBLE);
//                    job_type_3_ll.setVisibility(View.GONE);
//                } else if (selJobShift.equals("Weekend")) {
//                    job_type_1_ll.setVisibility(View.GONE);
//                    job_type_2_ll.setVisibility(View.GONE);
//                    job_type_3_ll.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterJobTypes(AreaModel areaModel) {
        ArrayList<JobType> jobTypes = areaModel.getJobTypeArrayList();
        String selJobShift = OustPreferences.get("selJobShift");
        if (selJobShift != null && !selJobShift.isEmpty()) {
            if (jobTypes != null && jobTypes.size() > 0) {
                for (int j = 0; j < jobTypes.size(); j++) {
                    if (jobTypes.get(j).getName().equals(selJobShift)) {
                        setShiftSpinnerData(jobTypes.get(j).getShiftList());
                    }
                }
            }
        } else {
            OustSdkTools.showToast("Data not found !");
        }
    }

    private String shiftTxt = "";

    private void setShiftSpinnerData(final ArrayList<String> shiftSpinnerData) {
        CustomStringArrayAdapter customAreaGrpAdapter = new CustomStringArrayAdapter(getActivity(), R.layout.sp_item, shiftSpinnerData);
        sp_form.setAdapter(customAreaGrpAdapter);

        sp_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shiftTxt = shiftSpinnerData.get(position);
                if (!shiftTxt.equals("Choose your shift")) {
                    showAreaAndPaymentOption(shiftDataModels.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    String areaGrpTxt = "";
    private long areaGrpId = 0;

    private void setAreaSpinnerData() {
        Log.d(TAG,"Area DataLoaded");
        is_Data_Loaded = true;
        hideProgressbarAlert();
        if (areaModels != null && areaModels.size() > 0) {
            Collections.sort(areaModels, new Comparator<AreaModel>() {
                @Override
                public int compare(AreaModel o1, AreaModel o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        if (areaModels != null && areaModels.size() > 1) {
            AreaModel areaModel = new AreaModel();
            areaModel.setName("Choose your area");
            areaModel.setId(0);
            areaModels.add(0, areaModel);
        }
        CustomAreaGrpAdapter customAreaGrpAdapter = new CustomAreaGrpAdapter(getActivity(), R.layout.sp_item, areaModels);

        sp_form.setAdapter(customAreaGrpAdapter);

        sp_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaGrpTxt = areaModels.get(position).getName();
                areaGrpId = areaModels.get(position).getId();
                String tier = areaModels.get(position).getTier();
                if (areaModels.get(position).getHubAddress() != null) {
                    OustPreferences.save("hubAddress", areaModels.get(position).getHubAddress());
                }
                if (tier != null && !tier.isEmpty()) {
                    OustPreferences.save("area_tier", tier);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    ArrayList<BloodGrpModel> bloodGrpModels;

    private void getBloodGroupData() {
        try {

            final String message = "/bloodGroup/";
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Object o = dataSnapshot.getValue();
                            if (o.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                Log.i("list", learningList.toString());
                                bloodGrpModels = new ArrayList<>();
                                BloodGrpModel bloodGrpMdel = new BloodGrpModel(0, "Choose your blood group");
                                bloodGrpModels.add(bloodGrpMdel);
                                for (int i = 1; i < learningList.size(); i++) {

                                    Object o1 = learningList.get(i);
                                    BloodGrpModel bloodGrpModel = new BloodGrpModel(i, (String) o1);
                                    bloodGrpModels.add(bloodGrpModel);
                                }

                                setBloodGrpFormData();

                            }
                        } else {
                            OustSdkTools.showToast("Sorry! No data Found!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myassessmentListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String bldGrpTxt = "";

    private void setBloodGrpFormData() {
        CustomBloodGrpAdapter customBloodGrpAdapter = new CustomBloodGrpAdapter(getActivity(), R.layout.sp_item, bloodGrpModels);
        sp_form.setAdapter(customBloodGrpAdapter);

        sp_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bldGrpTxt = bloodGrpModels.get(position).getBloodGrp();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int birthYear = 0;
    android.app.DatePickerDialog DatePickerDialog;

    private void openDateCalender() {
        DatePickerDialog.setTitle(OustStrings.getString("select_dob"));
        DatePickerDialog.show();

        try {
            DatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            DatePickerDialog.getDatePicker().getTouchables().get(0).performClick();
        } catch (Exception e) {
        }
    }

    public void setUserDateOfBirthCalander() {
        try {
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    birthYear = year;
                    saveSelectedDob(newDate.getTimeInMillis());
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        } catch (Exception e) {
        }
    }

    private void saveSelectedDob(long timeInMillis) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        Date parsedDate = new Date(timeInMillis);
        tv_dob_form.setText(dateFormatter.format(parsedDate));
    }

    private void setFormTypeQuestion() {
        learningquiz_form_layout.setVisibility(View.VISIBLE);
    }

    private void setAnswerLimitListener() {
        longanswer_editetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();//OustSdkTools.getEmojiEncodedString(longanswer_editetext);
                mWordLength = countWords(str);
                Log.d(TAG, "afterTextChanged 2: "+mWordLength);
                if (mWordLength <= maxWordsCount)
                {
                    disableSoftInputFromAppearing(longanswer_editetext, str, false);
                    maxanswer_limittext.setTextColor(OustSdkTools.getColorBack(R.color.whitelight));
                    if (mWordLength >= minWordsCount) {
                        longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
                    } else {
                        longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    }
                    if ((str.isEmpty())) {
                        maxanswer_limittext.setText(OustStrings.getString("words_left") + (maxWordsCount));
                    } else {
                        maxanswer_limittext.setText(OustStrings.getString("words_left") + (maxWordsCount - (mWordLength)));
                    }
                } else {
                    disableSoftInputFromAppearing(longanswer_editetext, str, true);
                    longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    maxanswer_limittext.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                    maxanswer_limittext.setText(OustStrings.getString("words_left") + ": 0");
                }
            }
        });
    }

    public void disableSoftInputFromAppearing(EditText editText, String str, boolean enable)
    {
        if(enable) {
            int maxLengthofEditText = str.length()-1;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
        }
        else
        {
            editText.setFilters(new InputFilter[] {});
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

    private void startQuestionNOHideAnimation(int waitTimer) {
        try {
            if (isAssessmentQuestion) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startShowingQuestion();
//                            startSpeakQuestion();
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 400);
                learningquiz_mainquestionImage.setVisibility(View.GONE);
            } else {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.learningcard_wrongimagehide_anim);
                anim.setStartOffset(waitTimer);
                learningquiz_mainquestionImage.startAnimation(anim);
                Animation quizout = AnimationUtils.loadAnimation(getActivity(), R.anim.event_animmoveout);
                quizout.setStartOffset(waitTimer);
                learningquiz_mainquestionTime.startAnimation(quizout);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.d(TAG,"Question card animation end");
                        is_Animation_End = true;
                        startShowingQuestion();
                        startSpeakQuestion();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void startShowingQuestion() {
        try {
            Log.d(TAG, "startShowingQuestion:questionType " + questionType);
            Log.d(TAG, "startShowingQuestion: questionCate:" + questionCategory);
            if (questionType.equals(QuestionType.FORM_TYPE)) {
                learningquiz_mainquestion.setVisibility(View.VISIBLE);
                setFormTypeQuestion();
            } else {
                if (questionCategory.equals(QuestionCategory.LONG_ANSWER)) {
                    longanswer_layout.setVisibility(View.VISIBLE);
                    longanswer_submit_btn.setVisibility(View.VISIBLE);
                    if (hasImageQuestion) {
                        learningquiz_imagequestionlayout.setVisibility(View.VISIBLE);
                    }
                    learningquiz_mainquestion.setVisibility(View.VISIBLE);
                } else {
                    if (questionType.equals(QuestionType.SURVEY_PS)) {
                        survey_layout.setVisibility(View.VISIBLE);
                        setSurvaryQuesBar();
                        survey_sublayouta.setVisibility(View.VISIBLE);
                        learningquiz_mainquestion.setVisibility(View.VISIBLE);
                        surveysubmit_btnlayout.setVisibility(View.VISIBLE);
                    } else {
                        if (isImageOption) {
                            if (!isBigImageOption) {
                                Log.d(TAG, "startShowingQuestion:learningquiz_imagechoiselayout ");
                                learningquiz_imagechoiselayout.setVisibility(View.VISIBLE);
                                if (myHandler != null) {
                                    myHandler.postDelayed(showAllImageOption, 300);
                                }
                            } else {
                                Log.d(TAG, "startShowingQuestion: learningquiz_bigimagechoiselayout");
                                learningquiz_bigimagechoiselayout.setVisibility(View.VISIBLE);
                                if (myHandler != null) {
                                    myHandler.postDelayed(showAllBigImageOption, 300);
                                }
                            }
                        } else {
                            Log.d(TAG, "startShowingQuestion: learningquiz_textchoiselayout");
                            learningquiz_textchoiselayout.setVisibility(View.VISIBLE);
                            if (myHandler != null) {
                                myHandler.postDelayed(showAllOption, 300);
                            }
                        }
                    }
                }
                if (questions != null && !questions.isExitable())
                    startTimer();
            }

        } catch (Exception e) {
        }
    }

    private int totalRate = 9;
    private LayoutInflater mInflater;
    private View convertView;

    private void setSurvaryQuesBar() {
        try {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            scrWidth = (metrics.widthPixels);
            if (questions.getSurveyPointCount() > 0) {
                totalRate = (questions.getSurveyPointCount() - 1);
            }
            mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.seekbarprogress, null);
            seekbar_nolayout.addView(convertView);
            setSeekbarPoints();
            setThumb(0);
            survey_seekbar.setMax(totalRate * 10);
            survey_seekbar.setProgress(0);
            setProgressText(0);
            setSurveyPointScale();
            survey_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    setThumb(i);
                    setProgressText(i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (seekBar.getProgress() % 10 != 0) {
                        setThumb(seekBar.getProgress());
                        float f1 = ((float) seekBar.getProgress()) / 10f;
                        int newProgress = Math.round(f1);
                        survey_seekbar.setProgress(newProgress * 10);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void setProgressText(float progress) {
        try {
            int size = (int) getActivity().getResources().getDimension(R.dimen.seekbarthump_margin);
            int size1 = (int) getActivity().getResources().getDimension(R.dimen.seekbarthump_margin1);
            int newScrWidth = scrWidth - size;

            float f1 = progress / 10;
            int newProgress = Math.round(f1);

            seekbar_nolayout.removeView(convertView);
            convertView = mInflater.inflate(R.layout.seekbarprogress, null);
            TextView progresstext = convertView.findViewById(R.id.progresstext);
            progresstext.setText("" + newProgress + 1);

            int point = (int) (((progress * newScrWidth) / (totalRate * 10)) - size1);
            if (point < 0) {
                convertView.setX(0);
            } else {
                convertView.setX(point);
            }


//            float f2 = (progress * scrWidth) / (totalRate * 10);
//            if (f2 > (scrWidth - 60)) {
//                f2 = scrWidth - 60;
//                convertView.setX((f2));
//            } else if (f2 <= (0)) {
//                convertView.setX(-10);
//            } else {
//                convertView.setX((f2 - 30));
//            }
            seekbar_nolayout.addView(convertView);
        } catch (Exception e) {
        }
    }

    private View thumbView;

    private void setThumb(float progress) {
        try {
            int size = (int) getActivity().getResources().getDimension(R.dimen.seekbarthump_margin);
            int size1 = (int) getActivity().getResources().getDimension(R.dimen.seekbarthump_margin1);
            int newScrWidth = scrWidth - size;
            if (thumbView != null) {
                seekbar_backa.removeView(thumbView);
            }
            thumbView = mInflater.inflate(R.layout.thumb_layout, null);
            int point = (int) (((progress * newScrWidth) / (totalRate * 10)) - size1);
            if (point < 0) {
                thumbView.setX(0);
            } else {
                thumbView.setX(point);
            }
            seekbar_backa.addView(thumbView);
        } catch (Exception e) {
        }
    }

    private void setSeekbarPoints() {
        int size = (int) getActivity().getResources().getDimension(R.dimen.seekbarthump_margin);
        float seekbarpoints = ((scrWidth - size) / totalRate);
        float point = 0;
        for (int i = 0; i <= totalRate; i++) {
            View convertView1 = mInflater.inflate(R.layout.seekbar_point, null);
//            if((i==0)||(i==(totalRate))){
            TextView survey_pointtext = convertView1.findViewById(R.id.survey_pointtext);
            survey_pointtext.setText(("" + (i + 1)));
//            }
            convertView1.setX(point);
            seekbar_back.addView(convertView1);
            point = point + seekbarpoints;
        }
    }


    private void showOptionWithAnimC(View view, int position) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.4f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.4f, 1.0f);
        ObjectAnimator scaleDownZ = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);
        scaleDownZ.setDuration(150);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY).with(scaleDownZ);
        scaleDown.setStartDelay((position * 100));
        scaleDown.start();
    }


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
                    learningquiz_mainquestion.setMovementMethod(new NonAdaptiveLearningPlayFragment.TextViewLinkHandler() {
                        @Override
                        public void onLinkClick(String mUrl) {
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
                    }, 1000);
                } else {
                    if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && isQuesttsEnabled) {
                        questionaudio_btn.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Spanned s1=getSpannedContent(questions.getQuestion());
//                                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                                    questionaudio_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                                    createStringfor_speech();
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
                        }, 1000);
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
            String url=questions.getImageCDNPath();
            if(url!=null){
                url= OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH image question");
            if(!url.contains("oustlearn_")) {
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
            String url=questions.getImageCDNPath();
            if(url!=null){
                url= OustMediaTools.removeAwsOrCDnUrl(url);
            }
            //OustSdkTools.showToast("IMAGE FROM CDNPATH omage optionA");
            if(!url.contains("oustlearn_")) {
                url = "oustlearn_" + OustMediaTools.getMediaFileName(url);
            }
            File file = new File(OustSdkApplication.getContext().getFilesDir(),url );
            if (file != null && file.exists()) {
                Uri uri = Uri.fromFile(file);
                learningquiz_imagequestion.setImageURI(uri);
            }else {
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

    private void showTextOptions() {
        try {
            Log.d(TAG, "showTextOptions: ");
            totalOption = 2;
            learningquiz_textchoiselayout.setVisibility(View.VISIBLE);
            if ((questions.getA() != null) && (!questions.getA().isEmpty()) && (!questions.getA().equalsIgnoreCase("dont know"))) {
                totalOption = 3;
                OustSdkTools.getSpannedContent(questions.getA(), optionA);
            }
            if ((questions.getB() != null) && (!questions.getB().isEmpty()) && (!questions.getB().equalsIgnoreCase("dont know"))) {
                totalOption = 4;
                OustSdkTools.getSpannedContent(questions.getB(), optionB);
            }
            if ((questions.getC() != null) && (!questions.getC().isEmpty()) && (!questions.getC().equalsIgnoreCase("dont know"))) {
                totalOption = 5;
                OustSdkTools.getSpannedContent(questions.getC(), optionC);
            }
            if ((questions.getD() != null) && (!questions.getD().isEmpty()) && (!questions.getD().equalsIgnoreCase("dont know"))) {
                totalOption = 6;
                totalSetOptions = 6;
                OustSdkTools.getSpannedContent(questions.getD(), optionD);
            }
            if ((questions.getE() != null) && (!questions.getE().isEmpty()) && (!questions.getE().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getE(), optionE);
                totalOption = 7;
            }
            if ((questions.getF() != null) && (!questions.getF().isEmpty()) && (!questions.getF().equalsIgnoreCase("dont know"))) {
                OustSdkTools.getSpannedContent(questions.getF(), optionF);
                totalOption = 8;
            }
            if (questionType.equals(QuestionType.MRQ)) {
                Log.d(TAG, "showTextOptions: ");
                hideAbcOption();
            } else {
                Log.d(TAG, "showTextOptions: hide");
                hideMrqOptions();
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void hideAbcOption() {
        try {
            Log.d(TAG, "hideAbcOption: ");
            choiceACheckBox.setVisibility(View.VISIBLE);
            choiceBCheckBox.setVisibility(View.VISIBLE);
            choiceCCheckBox.setVisibility(View.VISIBLE);
            choiceDCheckBox.setVisibility(View.VISIBLE);
            choiceECheckBox.setVisibility(View.VISIBLE);
            choiceFCheckBox.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void hideMrqOptions() {
        try {
            Log.d(TAG, "hideMrqOptions: ");
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
            totalOption = 2;
            learningquiz_imagechoiselayout.setVisibility(View.VISIBLE);
            if (questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData()!=null) {
                totalOption = 3;
                setLayoutAspectRatiosmall(learningquiz_optionalayout);
                if(questions.getImageChoiceA().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceA().getImageData(), imageoptionA);
                }else {
                    setImageOption(questions.getImageChoiceA().getImageData(), imageoptionA);
                }
            } else {
                mainimageoption_layouta.setVisibility(View.GONE);
            }
            if (questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData()!=null) {
                totalOption = 4;
                setLayoutAspectRatiosmall(learningquiz_optionblayout);
                if(questions.getImageChoiceB().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceB().getImageData(), imageoptionB);
                }else {
                    setImageOption(questions.getImageChoiceB().getImageData(), imageoptionB);
                }
            } else {
                mainimageoption_layoutb.setVisibility(View.GONE);
            }
            if (questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData()!=null) {
                totalOption = 5;
                setLayoutAspectRatiosmall(learningquiz_optionclayout);
                if(questions.getImageChoiceC().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceC().getImageData(), imageoptionC);
                }else {
                    setImageOption(questions.getImageChoiceC().getImageData(), imageoptionC);
                }
            } else {
                mainimageoption_layoutc.setVisibility(View.GONE);
            }
            if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData()!=null) {
                totalOption = 6;
                setLayoutAspectRatiosmall(learningquiz_optiondlayout);
                if(questions.getImageChoiceD().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceD().getImageData(), imageoptionD);
                }else {
                    setImageOption(questions.getImageChoiceD().getImageData(), imageoptionD);
                }
            } else {
                mainimageoption_layoutd.setVisibility(View.GONE);
            }
            if (questions.getE() != null) {
                totalOption = 7;
            }
            if (questions.getF() != null) {
                totalOption = 8;
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
            Log.d(TAG, "showMrqImageOption: ");
            choiceimgaeACheckBox.setVisibility(View.VISIBLE);
            choiceimgaeBCheckBox.setVisibility(View.VISIBLE);
            choiceimgaeCCheckBox.setVisibility(View.VISIBLE);
            choiceimgaeDCheckBox.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    private void showBigImageOptions() {
        try {
            totalOption = 2;
            learningquiz_bigimagechoiselayout.setVisibility(View.VISIBLE);
            if ((questions.getImageChoiceA() != null)) {
                totalOption = 3;
                setLayoutAspectRatio(learningquiz_bigoptionalayout);
                if(questions.getImageChoiceA().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceA().getImageData(), bigimageoptionA);
                }else {
                    setImageOption(questions.getImageChoiceA().getImageData(), bigimageoptionA);
                }
            }
            if ((questions.getImageChoiceB() != null)) {
                totalOption = 4;
                setLayoutAspectRatio(learningquiz_bigoptionblayout);
                if(questions.getImageChoiceB().getImageData()!=null){
                    setImageOptionUrl(questions.getImageChoiceB().getImageData(), bigimageoptionB);
                }else {
                    setImageOption(questions.getImageChoiceB().getImageData(), bigimageoptionB);
                }
            }
        } catch (Exception e) {
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

    public void setImageOptionUrl(String url,ImageView imageView){
        if(url!=null){
            url= OustMediaTools.removeAwsOrCDnUrl(url);
        }
        //OustSdkTools.showToast("IMAGE FROM CDNPATH imageoptionurl");
        url="oustlearn_"+ OustMediaTools.getMediaFileName(url);
        File file = new File(OustSdkApplication.getContext().getFilesDir(),url);
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

    int noofOption = 0;
    private int totalOption = 7;
    private Runnable showAllOption = new Runnable()
    {
        public void run() {
            try {
                if (totalOption > 0) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if (hasImageQuestion || hasVideoQuestion) {
                        showOptionWithAnimA(learningquiz_imagequestionlayout, 1);
                    }
                }
                if (totalOption > 1) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    showOptionWithAnimA(learningquiz_mainquestion, 2);
                }
                if (totalOption > 2) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    showOptionWithAnimA(mainoption_layouta, 3);
                }
                if (totalOption > 3) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion))))
                    {
                        showOptionWithAnimA(learning_mrqsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainoption_layoutb, 4);
                }
                if (totalOption > 4) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion))))
                    {
                        showOptionWithAnimA(learning_mrqsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainoption_layoutc, 5);
                }
                if (totalOption > 5) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion))))
                    {
                        showOptionWithAnimA(learning_mrqsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainoption_layoutd, 6);
                }
                if (totalOption > 6) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion))))
                    {
                        showOptionWithAnimA(learning_mrqsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainoption_layoute, 7);
                }
                if (totalOption > 7) {
                    noofOption++;
                    Log.d(TAG, "run: no of options:"+noofOption);
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion))))
                    {
                        showOptionWithAnimA(learning_mrqsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainoption_layoutf, 8);
                }
            } catch (Exception e) {
            }
        }
    };
    private Runnable showAllImageOption = new Runnable() {
        public void run() {
            try {
                if (totalOption > 0) {
                    noofOption++;
                    if (hasImageQuestion || hasVideoQuestion) {
                        showOptionWithAnimA(learningquiz_imagequestionlayout, 1);
                    }
                }
                if (totalOption > 1) {
                    noofOption++;
                    showOptionWithAnimA(learningquiz_mainquestion, 2);
                }
                if (totalOption > 2) {
                    noofOption++;
                    showOptionWithAnimA(mainimageoption_layouta, 3);
                }
                if (totalOption > 3) {
                    noofOption++;
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion)))) {
                        showOptionWithAnimA(learning_mrqimgsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainimageoption_layoutb, 4);
                }
                if (totalOption > 4) {
                    noofOption++;
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion)))) {
                        showOptionWithAnimA(learning_mrqimgsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainimageoption_layoutc, 5);
                }
                if (totalOption > 5) {
                    noofOption++;
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion)))) {
                        showOptionWithAnimA(learning_mrqimgsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainimageoption_layoutd, 6);
                }
            } catch (Exception e) {
            }
        }
    };
    private Runnable showAllBigImageOption = new Runnable() {
        public void run() {
            try {
                if (totalOption > 0) {
                    noofOption++;
                    if (hasImageQuestion || hasVideoQuestion) {
                        showOptionWithAnimA(learningquiz_imagequestionlayout, 1);
                    }
                }
                if (totalOption > 1) {
                    noofOption++;
                    showOptionWithAnimA(learningquiz_mainquestion, 2);
                }
                if (totalOption > 2) {
                    noofOption++;
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion)))) {
                        showOptionWithAnimA(learning_mrqbigimgsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainbigimageoption_layouta, 3);
                }
                if (totalOption > 3) {
                    noofOption++;
                    if ((((noofOption == totalOption) && (questionType.equals(QuestionType.MRQ))) ||
                            ((noofOption == totalOption) && (isAssessmentQuestion)))) {
                        showOptionWithAnimA(learning_mrqbigimgsubmitbutton, (noofOption + 1));
                    }
                    showOptionWithAnimA(mainbigimageoption_layoutb, 4);
                }
            } catch (Exception e) {
            }
        }
    };

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
                if(totalSetOptions == noOfAnimationEnd+1)
                {
                    Log.d(TAG, "onAnimationEnd: ");
                    for( int i = 0; i < learningquiz_textchoiselayout.getChildCount();  i++ ){
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
            if(anim_timer!=null){
                anim_timer.cancel();
                anim_timer.purge();
                anim_timer = null;
            }
            hideProgressbarAlert();
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

            cancleTimer();
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
            if (!isAssessmentQuestion) {
                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas() != null) {
                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress] != null) {
                        if (learningModuleInterface != null)
                            learningModuleInterface.gotoNextScreen();
                        removeAllData();
                    } else {
                        vibrateandShake();
                    }
                }
            } else {
                if ((isSurveyQuestion) && (assessmentScore != null)) {
                    if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.IMAGE_CHOICE))) {
                        if (((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                            finalScr = (int) mainCourseCardClass.getXp();
                            learningModuleInterface.setAnswerAndOc(selectedMCQAnswer, "", finalScr, true, 0);
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoNextScreen();
                        } else {
                            calculateMrqImageOc(true, false, true);
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoNextScreen();
                        }
                    } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                        finalScr = (int) mainCourseCardClass.getXp();
                        learningModuleInterface.setAnswerAndOc(OustSdkTools.getEmojiEncodedString(longanswer_editetext), "", finalScr, true, 0);
                        if (learningModuleInterface != null)
                            learningModuleInterface.gotoNextScreen();
                    } else if (questionType.equals(QuestionType.SURVEY_PS)) {
                        float f1 = survey_seekbar.getProgress() / 10;
                        sureveyResponse = ((Math.round(f1)) + 1);
                        learningModuleInterface.setAnswerAndOc(("" + sureveyResponse), "", finalScr, true, 0);
                        if (learningModuleInterface != null)
                            learningModuleInterface.gotoNextScreen();
                    } else {
                        if (((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                            finalScr = (int) mainCourseCardClass.getXp();
                            learningModuleInterface.setAnswerAndOc(selectedMCQAnswer, "", finalScr, true, 0);
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoNextScreen();
                        } else {
                            calculateMrqTextOc(true, false, true);
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoNextScreen();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void gotoPreviousScreen() {
        try {
            if (!isAssessmentQuestion) {
                if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas() != null) {
                    if (OustStaticVariableHandling.getInstance().getLearningCardResponceDatas()[learningcardProgress] != null) {
                        if(learningModuleInterface!=null)
                        {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                        removeAllData();
                    } else {
                        if (questionType != null && questionType.equalsIgnoreCase(QuestionType.FORM_TYPE)) {
                            if(learningModuleInterface!=null)
                            {
                                learningModuleInterface.gotoPreviousScreen();
                            }
                            removeAllData();
                        } else
                            vibrateandShake();
                    }
                }
            } else {
                if (isSurveyQuestion) 
                {
                    if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.IMAGE_CHOICE))) {
                        if (((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                            finalScr = (int) mainCourseCardClass.getXp();
                            learningModuleInterface.setAnswerAndOc(selectedMCQAnswer, "", finalScr, true, 0);
                            if(learningModuleInterface!=null)
                            {
                                learningModuleInterface.gotoPreviousScreen();
                            }
                        } else {
                            calculateMrqImageOc(true, false, true);
                            if(learningModuleInterface!=null)
                            {
                                learningModuleInterface.gotoPreviousScreen();
                            }
                        }
                    } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.LONG_ANSWER))) {
                        finalScr = (int) mainCourseCardClass.getXp();
                        learningModuleInterface.setAnswerAndOc(OustSdkTools.getEmojiEncodedString(longanswer_editetext), "", finalScr, true, 0);
                        if(learningModuleInterface!=null)
                        {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else if (questionType.equals(QuestionType.SURVEY_PS)) {
                        float f1 = survey_seekbar.getProgress() / 10;
                        sureveyResponse = ((Math.round(f1)) + 1);
                        learningModuleInterface.setAnswerAndOc(("" + sureveyResponse), "", finalScr, true, 0);
                        if(learningModuleInterface!=null)
                        {
                            learningModuleInterface.gotoPreviousScreen();
                        }
                    } else {
                        if (((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                            finalScr = (int) mainCourseCardClass.getXp();
                            learningModuleInterface.setAnswerAndOc(selectedMCQAnswer, "", finalScr, true, 0);
                            if(learningModuleInterface!=null)
                            {
                                learningModuleInterface.gotoPreviousScreen();
                            }
                        } else {
                            calculateMrqTextOc(true, false, true);
                            if(learningModuleInterface!=null)
                            {
                                learningModuleInterface.gotoPreviousScreen();
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
    private int[] noofAttempt = new int[7];
    private int attemptWrongCount = 0;
    private String userAns = "";

    private void touchOnOption(View view) {
        try {
            final int color = OustSdkTools.getColorBack(R.color.MoreLiteGrayc);
            final Drawable drawable = new ColorDrawable(color);
            if (animoc_layout.getVisibility() == View.VISIBLE) {
                int id = view.getId();
                if (id == R.id.learning_mrqsubmitbutton) {
                    if ((isAssessmentQuestion) && ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                        clickAnimation(learning_mrqsubmitbutton);
                        if ((selectedMCQAnswer != null) && (!selectedMCQAnswer.isEmpty())) {
                            if (questions.getExitOption() != null && questions.getExitOption().equalsIgnoreCase(selectedMCQAnswer)) {
                                String message = "You have selected " + selectedMCQAnswer + " .\n Do you wish to continue ?";
                                openExitPopup(null, message);
                            } else {
                                if ((questions.getAnswer() != null) && (questions.getAnswer().equalsIgnoreCase(selectedMCQAnswer))) {
                                    waitAndGo(selectedMCQAnswer, true);
                                } else {
                                    waitAndGo(selectedMCQAnswer, false);
                                }
                            }
                        }
                    } else {
                        calculateMrqTextOc(true, false, false);
                    }
                } else if ((id == R.id.learning_mrqbigimgsubmitbutton) || (id == R.id.learning_mrqimgsubmitbutton)) {
                    if ((isAssessmentQuestion) && ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE)))) {
                        clickAnimation(learning_mrqbigimgsubmitbutton);
                        clickAnimation(learning_mrqimgsubmitbutton);
                        if ((selectedMCQAnswer != null) && (!selectedMCQAnswer.isEmpty())) {
                            if ((questions.getImageChoiceAnswer() != null) && (selectedMCQAnswer.equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                waitAndGo(selectedMCQAnswer, true);
                            } else {
                                waitAndGo(selectedMCQAnswer, false);
                            }
                        }
                    } else {
                        calculateMrqImageOc(true, false, false);
                    }
                } else if (id == R.id.learningquiz_imagequestion) {
                    try {
                        learningModuleInterface.changeOrientationUnSpecific();
                        OustSdkTools.gifZoomPopup(learningquiz_imagequestion.getDrawable(), getActivity(), this);
                    } catch (Exception e) {
                    }
                } else if (id == R.id.option_layouta) {

                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getA() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("A")) || (questions.getAnswer().contains("a")))) {
                            setMRQRightOptionBack(choiceACheckBox, mainoption_layouta, false, 0);
                        } else {
                            setMRQWrongOptionBack(choiceACheckBox, option_layouta, mainoption_layouta, false, 0);
                        }
                    } else {

                        if (questions.isExitable()) {
                            Log.d(TAG, "touchOnOption: exit queston:");
                            if ((questions.getA() != null) && (questions.getExitOption() != null) && (questions.getA().equals(questions.getExitOption()))) {
                                OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learning_rightanswer_background);
                                String message = "Your response : " + questions.getA() + " .\n Is this correct ?";
                                openExitPopup(mainoption_layouta, message);
                                return;
                            }
                        }
                        if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("MCQ")) {
                            userAns = questions.getA();
                            OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learning_rightanswer_background);
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learningoption_backa);
                        } else {
                            if ((questions.getA() != null) && (questions.getAnswer() != null) && (questions.getA().equals(questions.getAnswer()))) {
                                setMCQRightOptionBack(questions.getA(), mainoption_layouta);
                            } else {
                                setMCQWrongOptionBack(questions.getA(), option_layouta, mainoption_layouta, 0);
                            }
                        }
                    }
                } else if (id == R.id.option_layoutb) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getB() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("B")) || (questions.getAnswer().contains("b")))) {
                            setMRQRightOptionBack(choiceBCheckBox, mainoption_layoutb, false, 1);
                        } else {
                            setMRQWrongOptionBack(choiceBCheckBox, option_layoutb, mainoption_layoutb, false, 1);
                        }
                    } else {
                        if (questions.isExitable()) {
                            if ((questions.getB() != null) && (questions.getExitOption() != null) && (questions.getB().equals(questions.getExitOption()))) {
                                OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learning_rightanswer_background);
                                String message = "Your response : " + questions.getB() + " .\n Is this correct ?";
                                openExitPopup(mainoption_layoutb, message);
                                return;
                            }
                        }
                        if (questions.getAnswerValidationType() != null && questions.getAnswerValidationType().equals("MCQ")) {
                            userAns = questions.getB();
                            OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learningoption_backa);
                            OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learning_rightanswer_background);
                        } else {
                            if ((questions.getB() != null) && (questions.getAnswer() != null) && (questions.getB().equals(questions.getAnswer()))) {
                                setMCQRightOptionBack(questions.getB(), mainoption_layoutb);
                            } else {
                                setMCQWrongOptionBack(questions.getB(), option_layoutb, mainoption_layoutb, 1);
                            }
                        }
                    }
                } else if (id == R.id.option_layoutc) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getC() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("C")) || (questions.getAnswer().contains("c")))) {
                            setMRQRightOptionBack(choiceCCheckBox, mainoption_layoutc, false, 2);
                        } else {
                            setMRQWrongOptionBack(choiceCCheckBox, option_layoutc, mainoption_layoutc, false, 2);
                        }
                    } else {
                        if (questions.isExitable()) {
                            if ((questions.getC() != null) && (questions.getExitOption() != null) && (questions.getC().equals(questions.getExitOption()))) {
                                OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learning_rightanswer_background);
                                String message = "Your response : " + questions.getC() + " .\n Is this correct ?";
                                openExitPopup(mainoption_layoutc, message);
                                return;
                            }
                        }
                        if ((questions.getC() != null) && (questions.getAnswer() != null) && (questions.getC().equals(questions.getAnswer()))) {
                            setMCQRightOptionBack(questions.getC(), mainoption_layoutc);
                        } else {
                            setMCQWrongOptionBack(questions.getC(), option_layoutc, mainoption_layoutc, 2);
                        }
                    }
                } else if (id == R.id.option_layoutd) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getD() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("D")) || (questions.getAnswer().contains("d")))) {
                            setMRQRightOptionBack(choiceDCheckBox, mainoption_layoutd, false, 3);
                        } else {
                            setMRQWrongOptionBack(choiceDCheckBox, option_layoutd, mainoption_layoutd, false, 3);
                        }
                    } else {
                        if (questions.isExitable()) {
                            if ((questions.getD() != null) && (questions.getExitOption() != null) && (questions.getD().equals(questions.getExitOption()))) {
                                OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learning_rightanswer_background);
                                String message = "Your response : " + questions.getD() + " .\n Is this correct ?";
                                openExitPopup(mainoption_layoutd, message);
                                return;
                            }
                        }
                        if ((questions.getD() != null) && (questions.getAnswer() != null) && (questions.getD().equals(questions.getAnswer()))) {
                            setMCQRightOptionBack(questions.getD(), mainoption_layoutd);
                        } else {
                            setMCQWrongOptionBack(questions.getD(), option_layoutd, mainoption_layoutd, 3);
                        }
                    }
                } else if (id == R.id.option_layoute) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getE() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("E")) || (questions.getAnswer().contains("e")))) {
                            setMRQRightOptionBack(choiceECheckBox, mainoption_layoute, false, 4);
                        } else {
                            setMRQWrongOptionBack(choiceECheckBox, option_layoute, mainoption_layoute, false, 4);
                        }
                    } else {
                        if ((questions.getE() != null) && (questions.getAnswer() != null) && (questions.getE().equals(questions.getAnswer()))) {
                            setMCQRightOptionBack(questions.getE(), mainoption_layoute);
                        } else {
                            setMCQWrongOptionBack(questions.getE(), option_layoute, mainoption_layoute, 4);
                        }
                    }
                } else if (id == R.id.option_layoutf) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getF() != null) && (questions.getAnswer() != null) && ((questions.getAnswer().contains("F")) || (questions.getAnswer().contains("f")))) {
                            setMRQRightOptionBack(choiceFCheckBox, mainoption_layoutf, false, 5);
                        } else {
                            setMRQWrongOptionBack(choiceFCheckBox, option_layoutf, mainoption_layoutf, false, 5);
                        }
                    } else {
                        if ((questions.getF() != null) && (questions.getAnswer() != null) && (questions.getF().equals(questions.getAnswer()))) {
                            setMCQRightOptionBack(questions.getF(), mainoption_layoutf);
                        } else {
                            setMCQWrongOptionBack(questions.getF(), option_layoutf, mainoption_layoutf, 5);
                        }
                    }
                } else if (id == R.id.imageoption_layouta) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("A")) || (questions.getImageChoiceAnswer().getImageFileName().contains("a")))) {
                            setMRQRightOptionBack(choiceimgaeACheckBox, mainimageoption_layouta, true, 0);
                        } else {
                            setMRQWrongOptionBack(choiceimgaeACheckBox, imageoption_layouta, mainimageoption_layouta, true, 0);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null) &&
                                    (questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceA().getImageFileName(), mainimageoption_layouta);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceA().getImageFileName(), imageoption_layouta, mainimageoption_layouta, 0);
                            }
                        } else {
                            setMCQRightOptionBack("", mainimageoption_layouta);
                        }
                    }
                } else if (id == R.id.imageoption_layoutb) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceB() != null) && (questions.getImageChoiceB().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("B")) || (questions.getImageChoiceAnswer().getImageFileName().contains("b")))) {
                            setMRQRightOptionBack(choiceimgaeBCheckBox, mainimageoption_layoutb, true, 1);
                        } else {
                            setMRQWrongOptionBack(choiceimgaeBCheckBox, imageoption_layoutb, mainimageoption_layoutb, true, 1);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceB() != null) && (questions.getImageChoiceB().getImageFileName() != null)
                                    && (questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceB().getImageFileName(), mainimageoption_layoutb);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceB().getImageFileName(), imageoption_layoutb, mainimageoption_layoutb, 1);
                            }
                        } else {
                            setMCQRightOptionBack("", mainimageoption_layoutb);
                        }
                    }
                } else if (id == R.id.imageoption_layoutc) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("C")) || (questions.getImageChoiceAnswer().getImageFileName().contains("c")))) {
                            setMRQRightOptionBack(choiceimgaeCCheckBox, mainimageoption_layoutc, true, 2);
                        } else {
                            setMRQWrongOptionBack(choiceimgaeCCheckBox, imageoption_layoutc, mainimageoption_layoutc, true, 2);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageFileName() != null)
                                    && (questions.getImageChoiceC().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceC().getImageFileName(), mainimageoption_layoutc);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceC().getImageFileName(), imageoption_layoutc, mainimageoption_layoutc, 2);
                            }
                        } else {
                            setMCQRightOptionBack("", mainimageoption_layoutc);
                        }
                    }
                } else if (id == R.id.imageoption_layoutd) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceD() != null) && (questions.getImageChoiceD().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("D")) || (questions.getImageChoiceAnswer().getImageFileName().contains("d")))) {
                            setMRQRightOptionBack(choiceimgaeDCheckBox, mainimageoption_layoutd, true, 3);
                        } else {
                            setMRQWrongOptionBack(choiceimgaeDCheckBox, imageoption_layoutd, mainimageoption_layoutd, true, 3);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceD() != null) && (questions.getImageChoiceD().getImageFileName() != null) &&
                                    (questions.getImageChoiceD().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceD().getImageFileName(), mainimageoption_layoutd);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceD().getImageFileName(), imageoption_layoutd, mainimageoption_layoutd, 3);
                            }
                        } else {
                            setMCQRightOptionBack("", mainimageoption_layoutd);
                        }
                    }
                } else if (id == R.id.bigimageoption_layouta) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("A")) || (questions.getImageChoiceAnswer().getImageFileName().contains("a")))) {
                            setMRQRightOptionBack(choicebigimgaeACheckBox, mainbigimageoption_layouta, true, 0);
                        } else {
                            setMRQWrongOptionBack(choicebigimgaeACheckBox, bigimageoption_layouta, mainbigimageoption_layouta, true, 0);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null)
                                    && (questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceA().getImageFileName(), mainbigimageoption_layouta);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceA().getImageFileName(), bigimageoption_layouta, mainbigimageoption_layouta, 0);
                            }
                        } else {
                            setMCQRightOptionBack(questions.getImageChoiceA().getImageFileName(), mainbigimageoption_layoutb);
                        }
                    }
                } else if (id == R.id.bigimageoption_layoutb) {
                    if (questionType.equals(QuestionType.MRQ)) {
                        srollToBottomWithAnimation();
                        if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null)
                                && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null) &&
                                ((questions.getImageChoiceAnswer().getImageFileName().contains("B")) || (questions.getImageChoiceAnswer().getImageFileName().contains("b")))) {
                            setMRQRightOptionBack(choicebigimgaeBCheckBox, mainbigimageoption_layoutb, true, 1);
                        } else {
                            setMRQWrongOptionBack(choicebigimgaeBCheckBox, bigimageoption_layoutb, mainbigimageoption_layoutb, true, 1);
                        }
                    } else {
                        if ((questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                            if ((questions.getImageChoiceB() != null) && (questions.getImageChoiceB().getImageFileName() != null) &&
                                    (questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                                setMCQRightOptionBack(questions.getImageChoiceB().getImageFileName(), mainbigimageoption_layoutb);
                            } else {
                                setMCQWrongOptionBack(questions.getImageChoiceB().getImageFileName(), bigimageoption_layoutb, mainbigimageoption_layoutb, 1);
                            }
                        } else {
                            setMCQRightOptionBack("", mainbigimageoption_layoutb);
                        }
                    }
                } else if (id == R.id.longanswer_submit_btn)
                {
                    String str = longanswer_editetext.getText().toString();//OustSdkTools.getEmojiEncodedString(longanswer_editetext);
                    mWordLength = countWords(str);
                    if (str!=null && (mWordLength >= questions.getMinWordCount()) && (mWordLength <= maxWordsCount))
                    {
                        longanswer_editetext.setEnabled(false);
                        longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                        finalScr = (int) mainCourseCardClass.getXp();
                        addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(longanswer_editetext));
                        answerSubmit(OustSdkTools.getEmojiEncodedString(longanswer_editetext), finalScr, false, true, true);
                        hideKeyboard(longanswer_editetext);
                        clickAnimation(longanswer_submit_btn);
                    } else if (isAssessmentQuestion && ((str == null) || (str != null && str.isEmpty()))) {
                        longanswer_editetext.setEnabled(false);
                        longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                        finalScr = 0;
                        addAnswerOnFirebase("");
                        answerSubmit(OustSdkTools.getEmojiEncodedString(longanswer_editetext), finalScr, false, false, true);
                        hideKeyboard(longanswer_editetext);
                        clickAnimation(longanswer_submit_btn);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void setMRQRightOptionBack(ImageView imageView, LinearLayout layout, boolean isImageCat, int optionNo) {
        if (noofAttempt[optionNo] == 0) {
            noofAttempt[optionNo] = 1;
            imageView.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
            imageView.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
            attemptWrongCount++;
            if (!isAssessmentQuestion) {
                OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
                if (isImageCat) {
                    calculateMrqImageOc(false, false, false);
                } else {
                    calculateMrqTextOc(false, false, false);
                }
            }
        } else if (isAssessmentQuestion) {
            if (attemptWrongCount > 0) {
                attemptWrongCount--;
            }
            noofAttempt[optionNo] = 0;
            imageView.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_checkbox));
            imageView.setColorFilter(OustSdkTools.getColorBack(R.color.white_pressed));
        }
    }

    private void setMRQWrongOptionBack(ImageView imageView, FrameLayout frameLayout, LinearLayout layout, boolean isImageCat, int optionNo) {
        if (noofAttempt[optionNo] == 0) {
            noofAttempt[optionNo] = 1;
            attemptWrongCount++;
            imageView.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
            if (!isAssessmentQuestion) {
                imageView.setColorFilter(OustSdkTools.getColorBack(R.color.Orange));
                final Drawable drawable = new ColorDrawable(OustSdkTools.getColorBack(R.color.MoreLiteGrayc));
                vibrateandShake();
                wrongAnswerSound();
                frameLayout.setForeground(drawable);
                OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learningoption_disablebackground);
            } else {
                imageView.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
            }
        } else if ((isAssessmentQuestion)) {
            if (attemptWrongCount > 0) {
                attemptWrongCount--;
            }
            noofAttempt[optionNo] = 0;
            imageView.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_checkbox));
            imageView.setColorFilter(OustSdkTools.getColorBack(R.color.white_pressed));
        }
    }

    private String selectedMCQAnswer = "";

    private void setMCQWrongOptionBack(String answer, FrameLayout frameLayout, LinearLayout layout, int optionNo) {
        Log.d(TAG, "setMCQWrongOptionBack: ");
        if (isAssessmentQuestion) {
            Log.d(TAG, "setMCQWrongOptionBack: assessment question:");
            resetAllAnswerForMCQ();
            selectedMCQAnswer = answer;
            OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
        } else {
            Log.d(TAG, "setMCQWrongOptionBack: not assessment:");
            if (noofAttempt[optionNo] == 0) {
                noofAttempt[optionNo] = 1;
                attemptWrongCount++;
//                if (!isAssessmentQuestion) {
                final Drawable drawable = new ColorDrawable(OustSdkTools.getColorBack(R.color.MoreLiteGrayc));
                vibrateandShake();
                wrongAnswerSound();
                frameLayout.setForeground(drawable);
                OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learningoption_disablebackground);
//                } else {
//                    waitAndGo(answer, false);
//                    clickAnimation(frameLayout);
//                }
            }
        }
    }

    private void setMCQRightOptionBack(String answer, LinearLayout layout) {
        Log.d(TAG, "setMCQRightOptionBack: ");
        if (isAssessmentQuestion) {
            resetAllAnswerForMCQ();
            selectedMCQAnswer = answer;
            OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
        } else {
            OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
            waitAndGo(answer, true);
            clickAnimation(layout);
        }
    }

    private void resetAllAnswerForMCQ() {
        OustSdkTools.setLayoutBackgrouda(mainoption_layouta, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainoption_layoutb, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainoption_layoutc, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainoption_layoutd, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainoption_layoute, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainoption_layoutf, R.drawable.learningoption_backa);

        OustSdkTools.setLayoutBackgrouda(mainimageoption_layouta, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutb, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutc, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainimageoption_layoutd, R.drawable.learningoption_backa);

        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layouta, R.drawable.learningoption_backa);
        OustSdkTools.setLayoutBackgrouda(mainbigimageoption_layoutb, R.drawable.learningoption_backa);
    }

    private void clickAnimation(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.96f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.98f);
            scaleDownX.setDuration(100);
            scaleDownY.setDuration(100);
            scaleDownX.setRepeatCount(1);
            scaleDownY.setRepeatCount(1);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
        }
    }


    private boolean scrolledToBottom = false;

    private void srollToBottomWithAnimation() {
        if (!scrolledToBottom) {
            scrolledToBottom = true;
            ObjectAnimator yTranslate = ObjectAnimator.ofInt(mainoption_scrollview, "scrollY", mainoption_scrollview.getBottom());
            yTranslate.setDuration(500);
            yTranslate.start();
        }
    }

    private void waitAndGotoNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    gotoNextScreen();
                } catch (Exception e) {
                }
            }
        }, 120);
    }


    private MediaPlayer mediaPlayer, defaultMediaPlayer;

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
        }
    }

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
            Animation shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shakescreen_anim);
            learningquiz_mainlayout.startAnimation(shakeAnim);
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void calculateMrqTextOc(boolean clickONBtn, boolean isTimeOut, boolean showSubjectivePopup) {
        try {
            if (attemptWrongCount > 0 || isTimeOut) {
                String answer = questions.getAnswer();
                int totalOptionNo = 0;
                int myAnswersNo = 0;
                String myAnswer = "";
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((answer.contains("A")) || (answer.contains("a"))) {
                        totalOptionNo++;
                        if (noofAttempt[0] == 1) {
                            myAnswer = myAnswer + "A,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[0] == 1) {
                        myAnswer = myAnswer + "A,";
                    }
                    if ((answer.contains("B")) || (answer.contains("b"))) {
                        totalOptionNo++;
                        if (noofAttempt[1] == 1) {
                            myAnswer = myAnswer + "B,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[1] == 1) {
                        myAnswer = myAnswer + "B,";
                    }
                    if ((answer.contains("C")) || (answer.contains("c"))) {
                        totalOptionNo++;
                        if (noofAttempt[2] == 1) {
                            myAnswer = myAnswer + "C,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[2] == 1) {
                        myAnswer = myAnswer + "C,";
                    }
                    if ((answer.contains("D")) || (answer.contains("d"))) {
                        totalOptionNo++;
                        if (noofAttempt[3] == 1) {
                            myAnswer = myAnswer + "D,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[3] == 1) {
                        myAnswer = myAnswer + "D,";
                    }
                    if ((answer.contains("E")) || (answer.contains("e"))) {
                        totalOptionNo++;
                        if (noofAttempt[4] == 1) {
                            myAnswer = myAnswer + "E,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[4] == 1) {
                        myAnswer = myAnswer + "E,";
                    }
                    if ((answer.contains("F")) || (answer.contains("f"))) {
                        totalOptionNo++;
                        if (noofAttempt[5] == 1) {
                            myAnswer = myAnswer + "F,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[5] == 1) {
                        myAnswer = myAnswer + "F,";
                    }
                }
                if (clickONBtn) {
                    if (myAnswer.length() > 0) {
                        myAnswer = myAnswer.substring(0, myAnswer.length() - 1);
                    }
                    float totalOcForCard = (float) mainCourseCardClass.getXp();
                    int no = attemptWrongCount - myAnswersNo;
                    boolean isCorrect = true;
                    if (isSurveyQuestion) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else {
                        if (myAnswersNo == attemptWrongCount && myAnswersNo == totalOptionNo) {
                            finalScr = (int) totalOcForCard;
                        } else {
                            //for asssemnt partially correct is inCorrect
                            if (isAssessmentQuestion) {
                                isCorrect = false;
                            }
                            float weight1 = totalOcForCard / (totalOption - 2);
                            float weight2 = totalOcForCard / totalOptionNo;
                            finalScr = Math.round(((myAnswersNo * weight2) - (no * weight1)));
                        }
                        if (finalScr < 0) {
                            finalScr = 0;
                        }
                        if (zeroXpForQCard) {
                            finalScr = 0;
                        }
                    }
                    if (showSubjectivePopup) {
                        learningModuleInterface.setAnswerAndOc(myAnswer, "", finalScr, true, 0);
                    } else {
                        if (isAssessmentQuestion && (!isCorrect || isTimeOut) && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())) {
                            finalScr = 0;
                            answerSubmit(myAnswer, 0, isTimeOut, isCorrect, false);
                        } else {
                            answerSubmit(myAnswer, finalScr, isTimeOut, isCorrect, false);
                        }
                    }
                }
            } else {
                if (!isSurveyQuestion) {
                    vibrateandShake();
                }
            }
        } catch (Exception e) {
        }
    }

    public void calculateMrqImageOc(boolean clickOnSubmitBtn, boolean isTimeOut, boolean showSubjectivePopup) {
        try {
            if (attemptWrongCount > 0 || isTimeOut) {
                String answer = "";
                try {
                    answer = questions.getImageChoiceAnswer().getImageFileName();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                int totalOptionNo = 0;
                int myAnswersNo = 0;
                String myAnswer = "";
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((answer.contains("A")) || (answer.contains("a"))) {
                        totalOptionNo++;
//                        if (clickOnSubmitBtn&&(!isAssessmentQuestion)) {
//                            showRightAns(mainimageoption_layouta, choiceimgaeACheckBox);
//                            showRightAns(mainbigimageoption_layouta, choicebigimgaeACheckBox);
//                        }
                        if (noofAttempt[0] == 1) {
                            myAnswer = myAnswer + "A,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[0] == 1) {
                        myAnswer = myAnswer + "A,";
                    }
                    if ((answer.contains("B")) || (answer.contains("b"))) {
                        totalOptionNo++;
//                        if (clickOnSubmitBtn&&(!isAssessmentQuestion)) {
//                            showRightAns(mainimageoption_layoutb, choiceimgaeBCheckBox);
//                            showRightAns(mainbigimageoption_layoutb, choicebigimgaeBCheckBox);
//                        }
                        if (noofAttempt[1] == 1) {
                            myAnswer = myAnswer + "B,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[1] == 1) {
                        myAnswer = myAnswer + "B,";
                    }
                    if ((answer.contains("C")) || (answer.contains("c"))) {
                        totalOptionNo++;
//                        if (clickOnSubmitBtn&&(!isAssessmentQuestion)) {
//                            showRightAns(mainimageoption_layoutc, choiceimgaeCCheckBox);
//                        }
                        if (noofAttempt[2] == 1) {
                            myAnswer = myAnswer + "C,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[2] == 1) {
                        myAnswer = myAnswer + "C,";
                    }
                    if ((answer.contains("D")) || (answer.contains("d"))) {
                        totalOptionNo++;
//                        if (clickOnSubmitBtn&&(!isAssessmentQuestion)) {
//                            showRightAns(mainimageoption_layoutd, choiceimgaeDCheckBox);
//                        }
                        if (noofAttempt[3] == 1) {
                            myAnswer = myAnswer + "D,";
                            myAnswersNo++;
                        }
                    } else if (noofAttempt[3] == 1) {
                        myAnswer = myAnswer + "D,";
                    }
                }
                if (clickOnSubmitBtn) {
                    if (myAnswer.length() > 0) {
                        myAnswer = myAnswer.substring(0, myAnswer.length() - 1);
                    }
                    float totalOcForCard = (float) mainCourseCardClass.getXp();
                    finalScr = 0;
                    boolean isCorrect = true;
                    if (isSurveyQuestion) {
                        finalScr = (int) mainCourseCardClass.getXp();
                        isCorrect = true;
                    } else {
                        int no = attemptWrongCount - myAnswersNo;
                        if (myAnswersNo == attemptWrongCount && myAnswersNo == totalOptionNo) {
                            finalScr = (int) totalOcForCard;
                        } else {
                            //for asssemnt partially correct is inCorrect
                            if (isAssessmentQuestion) {
                                isCorrect = false;
                            }
                            float weight1 = totalOcForCard / (totalOption - 2);
                            float weight2 = totalOcForCard / totalOptionNo;
                            finalScr = Math.round(((myAnswersNo * weight2) - (no * weight1)));
                        }
                        if (finalScr < 0) {
                            finalScr = 0;
                        }
                    }
                    if (zeroXpForQCard) {
                        finalScr = 0;
                    }
                    if (showSubjectivePopup) {
                        learningModuleInterface.setAnswerAndOc(myAnswer, "", finalScr, true, 0);
                    } else {
                        if (isAssessmentQuestion && (!isCorrect || isTimeOut) && (OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking())) {
                            finalScr = 0;
                            answerSubmit(myAnswer, 0, isTimeOut, isCorrect, false);
                        } else {
                            answerSubmit(myAnswer, finalScr, isTimeOut, isCorrect, false);
                        }
                    }
                }
            } else {
                if (!isSurveyQuestion) {
                    vibrateandShake();
                }
            }
        } catch (Exception e) {
        }
    }

    private void setCorrectAnswer() {
        try {
            if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.IMAGE_CHOICE))) {
                if (questionType.equals(QuestionType.MRQ)) {
                    learning_mrqimgsubmitbutton.setVisibility(View.GONE);
                    String answer = questions.getImageChoiceAnswer().getImageFileName();
                    if ((answer != null) && (!answer.isEmpty())) {
                        if ((answer.contains("A")) || (answer.contains("a"))) {
                            showRightAns(mainimageoption_layouta, choiceimgaeACheckBox);
                        } else {
                            hideWrongOption(mainimageoption_layouta);
                        }
                        if ((answer.contains("B")) || (answer.contains("b"))) {
                            showRightAns(mainimageoption_layoutb, choiceimgaeBCheckBox);
                        } else {
                            hideWrongOption(mainimageoption_layoutb);
                        }
                        if ((answer.contains("C")) || (answer.contains("c"))) {
                            showRightAns(mainimageoption_layoutc, choiceimgaeCCheckBox);
                        } else {
                            hideWrongOption(mainimageoption_layoutc);
                        }
                        if ((answer.contains("D")) || (answer.contains("d"))) {
                            showRightAns(mainimageoption_layoutd, choiceimgaeDCheckBox);
                        } else {
                            hideWrongOption(mainimageoption_layoutd);
                        }
                    }
                } else {
                    String answer = questions.getImageChoiceAnswer().getImageFileName();
                    if ((answer != null) && (!answer.isEmpty())) {
                        if ((answer.equalsIgnoreCase(questions.getImageChoiceA().getImageFileName()))) {
                            showRightAnsMCQ(mainimageoption_layouta);
                            showRightAnsMCQ(mainbigimageoption_layouta);
                        } else {
                            hideWrongOption(mainimageoption_layouta);
                            hideWrongOption(mainbigimageoption_layouta);
                        }
                        if ((answer.equalsIgnoreCase(questions.getImageChoiceB().getImageFileName()))) {
                            showRightAnsMCQ(mainimageoption_layoutb);
                            showRightAnsMCQ(mainbigimageoption_layoutb);
                        } else {
                            hideWrongOption(mainimageoption_layoutb);
                            hideWrongOption(mainbigimageoption_layoutb);
                        }
                        if ((answer.equalsIgnoreCase(questions.getImageChoiceC().getImageFileName()))) {
                            showRightAnsMCQ(mainimageoption_layoutc);
                        } else {
                            hideWrongOption(mainimageoption_layoutc);
                        }
                        if ((answer.equalsIgnoreCase(questions.getImageChoiceD().getImageFileName()))) {
                            showRightAnsMCQ(mainimageoption_layoutd);
                        } else {
                            hideWrongOption(mainimageoption_layoutd);
                        }
                    }
                }
            } else {
                if (questionType.equals(QuestionType.MRQ)) {
                    learning_mrqsubmitbutton.setVisibility(View.GONE);
                    String answer = questions.getAnswer();
                    if ((answer != null) && (!answer.isEmpty())) {
                        if ((answer.contains("A")) || (answer.contains("a"))) {
                            showRightAns(mainoption_layouta, choiceACheckBox);
                        } else {
                            hideWrongOption(mainoption_layouta);
                        }
                        if ((answer.contains("B")) || (answer.contains("b"))) {
                            showRightAns(mainoption_layoutb, choiceBCheckBox);
                        } else {
                            hideWrongOption(mainoption_layoutb);
                        }
                        if ((answer.contains("C")) || (answer.contains("c"))) {
                            showRightAns(mainoption_layoutc, choiceCCheckBox);
                        } else {
                            hideWrongOption(mainoption_layoutc);
                        }
                        if ((answer.contains("D")) || (answer.contains("d"))) {
                            showRightAns(mainoption_layoutd, choiceDCheckBox);
                        } else {
                            hideWrongOption(mainoption_layoutd);
                        }
                        if ((answer.contains("E")) || (answer.contains("e"))) {
                            showRightAns(mainoption_layoute, choiceECheckBox);
                        } else {
                            hideWrongOption(mainoption_layoute);
                        }
                        if ((answer.contains("F")) || (answer.contains("f"))) {
                            showRightAns(mainoption_layoutf, choiceFCheckBox);
                        } else {
                            hideWrongOption(mainoption_layoutf);
                        }
                    }
                } else {
                    String answer = questions.getAnswer();
                    if ((answer != null) && (!answer.isEmpty())) {
                        if ((questions.getA() != null) && (answer.equalsIgnoreCase(questions.getA()))) {
                            showRightAnsMCQ(mainoption_layouta);
                        } else {
                            hideWrongOption(mainoption_layouta);
                        }
                        if ((questions.getB() != null) && (answer.equalsIgnoreCase(questions.getB()))) {
                            showRightAnsMCQ(mainoption_layoutb);
                        } else {
                            hideWrongOption(mainoption_layoutb);
                        }
                        if ((questions.getC() != null) && (answer.equalsIgnoreCase(questions.getC()))) {
                            showRightAnsMCQ(mainoption_layoutc);
                        } else {
                            hideWrongOption(mainoption_layoutc);
                        }
                        if ((questions.getD() != null) && (answer.equalsIgnoreCase(questions.getD()))) {
                            showRightAnsMCQ(mainoption_layoutd);
                        } else {
                            hideWrongOption(mainoption_layoutd);
                        }
                        if ((questions.getE() != null) && (answer.equalsIgnoreCase(questions.getE()))) {
                            showRightAnsMCQ(mainoption_layoute);
                        } else {
                            hideWrongOption(mainoption_layoute);
                        }
                        if ((questions.getF() != null) && (answer.equalsIgnoreCase(questions.getD()))) {
                            showRightAnsMCQ(mainoption_layoutf);
                        } else {
                            hideWrongOption(mainoption_layoutf);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void showRightAns(LinearLayout layout, ImageView imageView) {
        imageView.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_answercheck));
        imageView.setColorFilter(OustSdkTools.getColorBack(R.color.LiteGreen));
        OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
    }

    private void showRightAnsMCQ(LinearLayout layout) {
        OustSdkTools.setLayoutBackgrouda(layout, R.drawable.learning_rightanswer_background);
    }

    private void hideWrongOption(LinearLayout layout) {
        layout.setVisibility(View.GONE);
    }


    public void rightwrongFlipAnimation(boolean status, boolean isTimeOut) {
        try {
            cancleTimer();
            if (!zeroXpForQCard) {
                if ((questionCategory.equals(QuestionCategory.LONG_ANSWER)) || (questionType.equals(QuestionType.SURVEY_PS))) {
                    ocanim_view.setVisibility(View.GONE);
                } else if (finalScr == 0) {
                    OustSdkTools.setImage(learningquiz_rightwrongimage, getActivity().getResources().getString(R.string.thumbsdown));
                }
            } else {
                ocanim_view.setVisibility(View.GONE);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                questionaudio_btn.setAnimation(null);
            }
            if (status) {
                if (isTimeOut) {
                    wrongAnswerSound();
                } else
                    rightAnswerSound();
            }
            learningquiz_animviewb.setVisibility(View.VISIBLE);
            learningquiz_animviewb.bringToFront();
            learningquiz_solutionlayout.setPivotX((scrWidth / 2));
            if (animoc_layout.getVisibility() == View.VISIBLE) {
                showAllMedia();
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
                        if (zeroXpForQCard) {
                            showSolutionWithAnimation(true);
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
                if (OustSdkTools.textToSpeech != null) {
                    OustSdkTools.stopSpeech();
                }
                ObjectAnimator transAnim = ObjectAnimator.ofFloat(learningquiz_solutionlayout, "translationY", 0, 1800);
                transAnim.setDuration(50);
                transAnim.start();
            } else {
                showSolution(0, learningquiz_solutionlayout.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //====================================================================================================
    public void waitAndGo(final String userAnswer, final boolean isCorrect) {
        calculateXp(userAnswer, isCorrect, false);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                } catch (Exception e) {}
//            }
//        },180);
    }

    private int finalScr = 0;
    private boolean ocCalculated = false;

    public void calculateXp(String userAnswer, boolean isCorrect, boolean timeOut) {

        if (!ocCalculated) {
            ocCalculated = true;
            try {
                finalScr = 0;
                if (!timeOut) {
                    if (isSurveyQuestion) {
                        finalScr = (int) mainCourseCardClass.getXp();
                    } else if (isCorrect) {
                        float challengerScore = 0f;
                        if (mainCourseCardClass != null) {
                            challengerScore = (float) mainCourseCardClass.getXp();
                        }
                        float finalScore = challengerScore;
                        float tatalOp = totalOption - 2;
                        float attempt = (float) attemptWrongCount;
                        float fraction = (tatalOp - attempt) / tatalOp;
                        finalScore = challengerScore * fraction;
                        finalScr = Math.round(finalScore);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            if (zeroXpForQCard) {
                finalScr = 0;
            }
            answerSubmit(userAnswer, finalScr, timeOut, isCorrect, false);
        }
    }


    private void animateOcCoins() {
        try {
            if (learningquiz_solutionlayout.getVisibility() == View.GONE) {
                if (finalScr < 1) {
                    OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                } else {
                    if ((questionCategory.equals(QuestionCategory.LONG_ANSWER)) || (questionType.equals(QuestionType.SURVEY_PS))) {
                    } else {
                        playAudio("coins.mp3");
                    }
                }
                ValueAnimator animator1 = new ValueAnimator();
                animator1.setObjectValues(0, (finalScr));
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
                        ocanim_text.setText("" + finalScr);
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
        }
    }


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
            if(mediaPlayer!=null && mediaPlayer.isPlaying())
            {
                Log.d(TAG, "playAudio: isplaying");
                isAudioPaused = true;
                mediaPlayer.pause();
            }
            defaultMediaPlayer.start();
            defaultMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer1) {
                    Log.d(TAG, "onCompletion of media of answer correct or incorrect: ");
                    if(isAudioPaused) {
                        try {
                            if(mediaPlayer!=null) {
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
            Log.d(TAG, "playAudio: Exception:"+e.getLocalizedMessage());
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
                    setCorrectAnswer();
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
        if(path!=null)
        OustSdkTools.showToast("Your salary payout has been saved to "+path);
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            learningquiz_timertext.setText("00:00");
            if (OustStaticVariableHandling.getInstance().getLearningquiz_timertext() != null && (OustStaticVariableHandling.getInstance().getLearningquiz_timertext().getVisibility() == View.VISIBLE)) {
                OustStaticVariableHandling.getInstance().getLearningquiz_timertext().setText("00:00");
            }
            if ((OustSdkTools.zoomImagePopup != null) && (OustSdkTools.zoomImagePopup.isShowing())) {
                OustSdkTools.zoomImagePopup.dismiss();
            }
            if (learningModuleInterface != null) {
                learningModuleInterface.changeOrientationPortrait();
                if (!isAssessmentQuestion) {
                    learningModuleInterface.readMoreDismiss();
                }
                learningModuleInterface.closeCourseInfoPopup();
            }
            if ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE))) {
                OustSdkTools.setImage(learningquiz_rightwrongimage, OustSdkApplication.getContext().getResources().getString(R.string.thumbsdown));
                calculateXp("", false, true);
            } else if (questionType.equals(QuestionType.MRQ)) {
                if (questionCategory.equals(QuestionCategory.IMAGE_CHOICE)) {
                    calculateMrqImageOc(true, true, false);
                } else {
                    calculateMrqTextOc(true, true, false);
                }
            } else if (questionCategory.equals(QuestionCategory.LONG_ANSWER)) {
                longAnswerTimeOut();
            } else if (isSurveyQuestion) {
                float f1 = survey_seekbar.getProgress() / 10;
                sureveyResponse = ((Math.round(f1)) + 1);
                submitSurveyResponse(true);
            } else {
                answerSubmit("", 0, true, false, true);
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            answeredSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            String hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            learningquiz_timertext.setText(hms);
            if (OustStaticVariableHandling.getInstance().getLearningquiz_timertext() != null && (OustStaticVariableHandling.getInstance().getLearningquiz_timertext().getVisibility() == View.VISIBLE)) {
                OustStaticVariableHandling.getInstance().getLearningquiz_timertext().setText(hms);
            }
        }
    }

    public void longAnswerTimeOut() {
        longanswer_editetext.setEnabled(false);
        longanswer_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
        String str = longanswer_editetext.getText().toString();//OustSdkTools.getEmojiEncodedString(longanswer_editetext);
        mWordLength = countWords(str);
        if (isAssessmentQuestion && OustAppState.getInstance().getAssessmentFirebaseClass().isDisablePartialMarking()) {
            finalScr = 0;
            answerSubmit(OustSdkTools.getEmojiEncodedString(longanswer_editetext), 0, true, false, true);
            addAnswerOnFirebase("");
        } else {
            if ((str != null && !str.isEmpty()) && (mWordLength >= questions.getMinWordCount()) && (mWordLength <= maxWordsCount)) {
                finalScr = (int) mainCourseCardClass.getXp();
                answerSubmit(OustSdkTools.getEmojiEncodedString(longanswer_editetext), finalScr, true, true, true);
                addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(longanswer_editetext));
            } else {
                finalScr = 0;
                answerSubmit(OustSdkTools.getEmojiEncodedString(longanswer_editetext), 0, true, false, true);
                addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(longanswer_editetext));
            }
        }

    }

    private CounterClass timer;

    public void startTimer() {
        try {
            if (isSurveyQuestion) {
            } else if (!isAssessmentQuestion) {
                timer = new CounterClass(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
                timer.start();
            } else {
                if (OustStaticVariableHandling.getInstance().getAnswerSeconds()[learningcardProgress] == 0) {
                    timer = new CounterClass(Integer.parseInt(questions.getMaxtime() + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
                    timer.start();
                } else {
                    setPreviousState();
                }
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

    public void cancleTimer() {
        try {
            if (null != timer)
                timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //======================================================================================================
    private float x1, x2;
    private float y1, y2;
    private final int MIN_DISTANCE = 50;
    private boolean tochedScreen = false;
    //------------------------------------------
    private CourseSolutionCard courseCardClass;

    private void showAllMedia() {
        try {
            if (mainCourseCardClass != null) {
                courseCardClass = mainCourseCardClass.getChildCard();
                if ((courseCardClass != null) && (courseCardClass.getContent() != null)) {
                    if (mainCourseCardClass.getReadMoreData() != null && mainCourseCardClass.getReadMoreData().getDisplayText() != null) {
                        Log.e("ReadMore", mainCourseCardClass.getReadMoreData().getDisplayText());
                        solution_desc.setHtml(courseCardClass.getContent());
                        solution_readmore_text.setText(mainCourseCardClass.getReadMoreData().getDisplayText());
                        solution_readmore.setVisibility(View.VISIBLE);

                        solution_readmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCaseletQuestionOptionClicked = false;
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 131);
                            }
                        });
                    } else {
                        solution_desc.setHtml(courseCardClass.getContent());
                    }
                    solution_label.setVisibility(View.VISIBLE);
                    solution_desc.setVisibility(View.VISIBLE);
                }
                if ((userSubjectiveAns != null) && (!userSubjectiveAns.isEmpty())) {
                    myresponse_desc.setVisibility(View.VISIBLE);
                    myresponse_desc.setText(userSubjectiveAns);
                    myresponse_label.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


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
            }else if(requestCode==141){

                OustSdkTools.showToast("Downloading your payout structure !");

                ScreenShotGenerator screenShotGenerator=new ScreenShotGenerator(extra_tier_info_ll,this);
                screenShotGenerator.execute();

                isScreenShotCreated=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void openCaseLet() {
        if ((isCaseletQuestionOptionClicked) && (mainCourseCardClass != null) && (mainCourseCardClass.getMappedLearningCardId() > 0)) {
            learningModuleInterface.setShareClicked(true);
            ReadMoreData readMoreData = new ReadMoreData();
            readMoreData.setType("CARD_LINK");
            readMoreData.setCardId(("" + mainCourseCardClass.getMappedLearningCardId()));
            readMoreData.setData("" + mainCourseCardClass.getMappedLearningCardId());
            //learningModuleInterface.openReadMoreFragment(readMoreData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
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
            ReadMoreData readData = mainCourseCardClass.getReadMoreData();
            learningModuleInterface.setShareClicked(true);
            if (readData.getType().equalsIgnoreCase("IMAGE")) {
                learningModuleInterface.changeOrientationUnSpecific();
            }
            //learningModuleInterface.openReadMoreFragment(readData, isRMFavourite, courseId, cardBackgroundImage, mainCourseCardClass);
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
                submitSurveyResponse(false);
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

    private void submitSurveyResponse(boolean timeout) {
        if (sureveyResponse > 0) {
            answerSubmit(("" + sureveyResponse), 0, timeout, true, false);
        } else {
            float f1 = survey_seekbar.getProgress() / 10;
            sureveyResponse = ((Math.round(f1)) + 1);
            if (sureveyResponse > 0) {
                answerSubmit(("" + sureveyResponse), 0, timeout, true, false);
            }
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

    private void answerSubmit(String answer, int oc, boolean isTimeOut, boolean isCorrect, boolean gotoNextScreen) {
        try {
            cancleTimer();
            if (!popupShownOnce) {
                popupShownOnce = true;
                if (questions != null) {
                    if (gotoNextScreen || isTimeOut) {
                        if ((questions.isContainSubjective()) && (!isTimeOut)) {
                            showSubjectiveQuestionPopup(answer, isCorrect);
                        } else {
                            learningModuleInterface.setAnswerAndOc(answer, "", finalScr, isCorrect, 0);
                            if ((isAssessmentQuestion) || (isSurveyQuestion)) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoNextScreen();
                                removeAllData();
                            } else {
                                if (questions != null && !questions.isThumbsUpDn()) {
                                    rightwrongFlipAnimation(true, isTimeOut);
                                } else {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.gotoNextScreen();
                                    }
                                }
                            }
                        }
                    } else {
                        if ((questions.isContainSubjective()) && (!isTimeOut)) {
                            showSubjectiveQuestionPopup(answer, isCorrect);
                        } else {
                            learningModuleInterface.setAnswerAndOc(answer, "", finalScr, isCorrect, 0);
                            if ((isAssessmentQuestion) || (isSurveyQuestion)) {
                                if (learningModuleInterface != null)
                                    learningModuleInterface.gotoNextScreen();
                                removeAllData();
                            } else {
                                if (questions != null && !questions.isThumbsUpDn()) {
                                    rightwrongFlipAnimation(true, isTimeOut);
                                } else {
                                    if (learningModuleInterface != null) {
                                        learningModuleInterface.gotoNextScreen();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    //============================================================================
    private int maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
    private int minWordsCount = 0;
    private RelativeLayout questionsubans_cardlayout, questionsubans_submit_btn;
    private TextView questionsubans_header, questionsubans_limittext;
    private EditText questionsubans_editetext;
    private CardView questionsubans_card;

    private void showSubjectiveQuestionPopup(final String answer, final boolean status) {
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

        if ((questions.getSubjectiveQuestion() != null) && (questions.getSubjectiveQuestion().isEmpty())) {
            questionsubans_header.setText(questions.getSubjectiveQuestion());
            questionsubans_header.setVisibility(View.VISIBLE);
        } else {
            questionsubans_header.setText(OustStrings.getString("explain_your_rationale"));
        }
        maxWordsCount = questions.getMaxWordCount();
        if (maxWordsCount == 0) {
            maxWordsCount = AppConstants.IntegerConstants.MAX_WORD_COUNT;
        }
        minWordsCount = questions.getMinWordCount();
        if (minWordsCount == 0) {
            questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
        }
        questionsubans_limittext.setText("");
        String hintStr = "Type your response\n(Min words : minWord and Max words : maxWord)";
        hintStr = hintStr.replace("minWord", ("" + minWordsCount));
        hintStr = hintStr.replace("maxWord", ("" + maxWordsCount));
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
        questionsubans_limittext.setText(OustStrings.getString("words_left") + (maxWordsCount));
        questionsubans_editetext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str =  editable.toString();//OustSdkTools.getEmojiEncodedString(questionsubans_editetext);
                mWordLength = countWords(str);
                Log.d(TAG, "afterTextChanged: "+mWordLength);
                if (mWordLength <= maxWordsCount)
                {
                    disableSoftInputFromAppearing(questionsubans_editetext, str, false);
                    questionsubans_limittext.setTextColor(OustSdkTools.getColorBack(R.color.DarkGray));
                    if (mWordLength >= minWordsCount) {
                        questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen));
                    } else {
                        questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    }
                    if ((str.isEmpty())) {
                        questionsubans_limittext.setText(OustStrings.getString("words_left") + (maxWordsCount));
                    } else {
                        questionsubans_limittext.setText(OustStrings.getString("words_left") + (maxWordsCount - (mWordLength)));
                    }
                } else {
                    disableSoftInputFromAppearing(questionsubans_editetext, str, true);
                    questionsubans_submit_btn.setBackgroundColor(OustSdkTools.getColorBack(R.color.grayout));
                    questionsubans_limittext.setTextColor(OustSdkTools.getColorBack(R.color.reda));
                    questionsubans_limittext.setText(OustStrings.getString("words_left") + ": 0");
                }
            }
        });
        if ((assessmentScore != null) && (assessmentScore.getUserSubjectiveAns() != null) && (!assessmentScore.getUserSubjectiveAns().isEmpty())) {
            questionsubans_editetext.setText(OustSdkTools.getEmojiDecodedString(assessmentScore.getUserSubjectiveAns()));
        }
        questionsubans_cardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        questionsubans_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    String str = questionsubans_editetext.getText().toString();//OustSdkTools.getEmojiEncodedString(questionsubans_editetext);
                    mWordLength = countWords(str);
                    if ((mWordLength < minWordsCount)) {
                    } else if ((mWordLength <= maxWordsCount)) {
                        learningModuleInterface.disableBackButton(false);
                        questionsubans_cardlayout.setVisibility(View.GONE);
                        hideKeyboard(questionsubans_editetext);
                        learningModuleInterface.setAnswerAndOc(answer, OustSdkTools.getEmojiEncodedString(questionsubans_editetext), finalScr, status, 0);
                        addAnswerOnFirebase(OustSdkTools.getEmojiEncodedString(questionsubans_editetext));
                        userSubjectiveAns = questionsubans_editetext.getText().toString();
                        if ((isAssessmentQuestion) || ((isSurveyQuestion))) {
                            if (learningModuleInterface != null)
                                learningModuleInterface.gotoNextScreen();
                            removeAllData();
                        } else {
                            rightwrongFlipAnimation(status, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });
    }

    public void hideKeyboard(View v) {
        try {
            if(getActivity()!=null) {
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
            if(getActivity()!=null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //==============================================================================================================
    //for assessment set previous answer
    private void setMCQMyAnser() {
        try {
            Log.d(TAG, "setMCQMyAnser: ");
            if (assessmentScore != null) {
                String answer = assessmentScore.getAnswer();
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((questions.getA() != null) && (questions.getAnswer() != null) && (questions.getA().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getA(), mainoption_layouta);
                    } else if ((questions.getB() != null) && (questions.getAnswer() != null) && (questions.getB().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getB(), mainoption_layoutb);
                    } else if ((questions.getC() != null) && (questions.getAnswer() != null) && (questions.getC().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getC(), mainoption_layoutc);
                    } else if ((questions.getD() != null) && (questions.getAnswer() != null) && (questions.getD().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getD(), mainoption_layoutd);
                    } else if ((questions.getE() != null) && (questions.getAnswer() != null) && (questions.getE().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getE(), mainoption_layoute);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void setMCQMyImageAnser() {
        try {
            if (assessmentScore != null) {
                String answer = assessmentScore.getAnswer();
                Log.d(TAG, "setMCQMyImageAnser: " + answer);
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageFileName() != null) &&
                            (questions.getImageChoiceA().getImageFileName().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getImageChoiceA().getImageFileName(), mainimageoption_layouta);
                        setMCQRightOptionBack(questions.getImageChoiceA().getImageFileName(), mainbigimageoption_layouta);
                    } else if ((questions.getImageChoiceB() != null) && (questions.getImageChoiceB().getImageFileName() != null) &&
                            (questions.getImageChoiceB().getImageFileName().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getImageChoiceB().getImageFileName(), mainimageoption_layoutb);
                        setMCQRightOptionBack(questions.getImageChoiceB().getImageFileName(), mainbigimageoption_layoutb);
                    } else if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageFileName() != null) &&
                            (questions.getImageChoiceC().getImageFileName().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getImageChoiceC().getImageFileName(), mainimageoption_layoutc);
                    } else if ((questions.getImageChoiceD() != null) && (questions.getImageChoiceD().getImageFileName() != null) &&
                            (questions.getImageChoiceD().getImageFileName().equalsIgnoreCase(answer))) {
                        setMCQRightOptionBack(questions.getImageChoiceD().getImageFileName(), mainimageoption_layoutd);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void setMRQMyAnser() {
        try {
            if (assessmentScore != null) {
                String answer = assessmentScore.getAnswer();
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((answer.contains("A")) || (answer.contains("a"))) {
                        setMRQRightOptionBack(choiceACheckBox, mainoption_layouta, false, 0);
                    }
                    if ((answer.contains("B")) || (answer.contains("b"))) {
                        setMRQRightOptionBack(choiceBCheckBox, mainoption_layoutb, false, 1);
                    }
                    if ((answer.contains("C")) || (answer.contains("c"))) {
                        setMRQRightOptionBack(choiceCCheckBox, mainoption_layoutc, false, 2);
                    }
                    if ((answer.contains("D")) || (answer.contains("d"))) {
                        setMRQRightOptionBack(choiceDCheckBox, mainoption_layoutd, false, 3);
                    }
                    if ((answer.contains("E")) || (answer.contains("e"))) {
                        setMRQRightOptionBack(choiceECheckBox, mainoption_layoute, false, 4);
                    }
                    if ((answer.contains("F")) || (answer.contains("f"))) {
                        setMRQRightOptionBack(choiceFCheckBox, mainoption_layoutf, false, 5);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void setMRQMyImageMyAnser() {
        try {
            if (assessmentScore != null) {
                String answer = assessmentScore.getAnswer();
                if ((answer != null) && (!answer.isEmpty())) {
                    if ((answer.contains("A")) || (answer.contains("a"))) {
                        setMRQRightOptionBack(choiceimgaeACheckBox, mainimageoption_layouta, true, 0);
                    }
                    if ((answer.contains("B")) || (answer.contains("b"))) {
                        setMRQRightOptionBack(choiceimgaeBCheckBox, mainimageoption_layoutb, true, 1);
                    }
                    if ((answer.contains("C")) || (answer.contains("c"))) {
                        setMRQRightOptionBack(choiceimgaeCCheckBox, mainimageoption_layoutc, true, 2);
                    }
                    if ((answer.contains("D")) || (answer.contains("d"))) {
                        setMRQRightOptionBack(choiceimgaeDCheckBox, mainimageoption_layoutd, true, 3);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void setLongAnswer() {
        if (assessmentScore != null) {
            longanswer_editetext.setText(OustSdkTools.getEmojiDecodedString(assessmentScore.getAnswer()));
        }
    }

    private void setSurveyPointScale() {
        try {
            if (isSurveyQuestion) {
                if (assessmentScore != null) {
                    int surveyPoints = Integer.parseInt(assessmentScore.getAnswer());
                    setThumb(((surveyPoints - 1) * 10));
                    setProgressText(((surveyPoints - 1) * 10));
                    survey_seekbar.setProgress(((surveyPoints - 1) * 10));
                }
            }
        } catch (Exception e) {
        }
    }

    private void showBottemBarForSurvey() {
        if (isSurveyQuestion && showNavigateArrow) {
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
        }
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
                            if(getActivity()!=null) {
                                getActivity().startActivity(intent1);
                            }
                            if(getActivity()!=null)
                            {
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

    public  int countWords(String sentence) {

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

}
