package com.oustme.oustsdk.activity.FFContest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.EventLeaderboardActivity;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.firebase.FFContest.FastestFingerContestData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.question_module.fragment.MCQuestionFragment;
import com.oustme.oustsdk.request.ContestUserDataRequest;
import com.oustme.oustsdk.request.UserF3CQuestionScoreData;
import com.oustme.oustsdk.request.UserF3CScoreData;
import com.oustme.oustsdk.request.UserF3CScoreRequestData_v2;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.service.ContestScoreSubmitService;
import com.oustme.oustsdk.service.SendApiServices;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.NetworkUtil;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustCommonUtils;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.htmlrender.HtmlTextView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by admin on 04/08/17.
 */

public class FFContestPlayActivity extends FragmentActivity implements View.OnClickListener, OnNetworkChangeListener {

    private ImageView ffcplay_useravatar, ffcplay_questionimage, imageoptionA, imageoptionB, imageoptionC, imageoptionD, imageoptionE, imageoptionF, watchline, ffcstart_backimage;

    private RelativeLayout textchoise_mainlayouta, textchoise_mainlayoutb, textchoise_mainlayoutc, textchoise_mainlayoutd,
            textchoise_mainlayoute, textchoise_mainlayoutf, textchoise_sublayouta, textchoise_sublayoutb, textchoise_sublayoutc, textchoise_sublayoutd,
            textchoise_sublayoute, textchoise_sublayoutf, ffcplay_intermediatelayout, ffcplay_mainquestionlayout, mainimage_optionlayouta, intermediate_timelayout,
            mainimage_optionlayoutb, mainimage_optionlayoutc, mainimage_optionlayoutd, mainimage_optionlayoute, ffcplay_pause_animlayout, ffcplay_mainlayout;

    private HtmlTextView ffcplay_questiontext;

    private LinearLayout textchoise_layout, imagechoise_layout;

    private TextView textchoise_a, textchoise_b, textchoise_c, textchoise_d, textchoise_e, textchoise_f, ffcplay_timelefttext, ffcplay_secondtext, timeleft_text,
            imagechoise_labela, imagechoise_labelb, imagechoise_labelc, imagechoise_labeld, imagechoise_labele, imagechoise_labelf, ffcplay_questionnotext, ffcplay_secondlabel,
            ffcplay_timeleftlabel, textchoise_labela, textchoise_labelb, textchoise_labelc, textchoise_labeld, textchoise_labele, textchoise_labelf, ffcplay_username,
            ffcplay_timelefttexta, ffcplay_motivatetext, ffcplay_warmuplabel, timeleft_textlabel;

    private ProgressBar ffcplay_questionprogressbar;
    private ActiveUser activeUser;
    private List<String> qList = new ArrayList<>();
    private boolean isIntermediateScreenOn = false;
    private int scrWidth = 0;
    private long questionStartTime = 0;
    private List<Map<String, Object>> ffcResponceList = new ArrayList<>();

    private long delayTime = 0;
    private long questionTime = 5000;
    private FastestFingerContestData fastestFingerContestData;
    private boolean warmMode = false;
    private LayoutInflater mInflater;
    private String appVersion = "", deviceId = "";
    private CounterClass timer;
    private long restTime = 0;
    private long currentTime = 0;
    private long answerSec = 0;
    private boolean isActivityLive = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            try {
                OustSdkTools.setLocale(FFContestPlayActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            setContentView(R.layout.activity_ffcplay);
            setStartingUI();
            getContestData();
            customizeUI();
            setProfilePic();
            startMusic();
            jumpToQuestion();
//            OustGATools.getInstance().reportPageViewToGoogle(FFContestPlayActivity.this, "FFContest Play Page");

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=============================================================================================================================
    private void setStartingUI() {
        initViews();
        getWidth();
        setListener();
        keepScreenOnSecure();
        setRestCallInfo();
    }

    private void initViews() {
        textchoise_mainlayouta = findViewById(R.id.textchoise_mainlayouta);
        textchoise_mainlayoutb = findViewById(R.id.textchoise_mainlayoutb);
        textchoise_mainlayoutc = findViewById(R.id.textchoise_mainlayoutc);
        textchoise_mainlayoutd = findViewById(R.id.textchoise_mainlayoutd);
        textchoise_mainlayoute = findViewById(R.id.textchoise_mainlayoute);
        textchoise_mainlayoutf = findViewById(R.id.textchoise_mainlayoutf);

        textchoise_sublayouta = findViewById(R.id.textchoise_sublayouta);
        textchoise_sublayoutb = findViewById(R.id.textchoise_sublayoutb);
        textchoise_sublayoutc = findViewById(R.id.textchoise_sublayoutc);
        textchoise_sublayoutd = findViewById(R.id.textchoise_sublayoutd);
        textchoise_sublayoute = findViewById(R.id.textchoise_sublayoute);
        textchoise_sublayoutf = findViewById(R.id.textchoise_sublayoutf);

        mainimage_optionlayouta = findViewById(R.id.mainimage_optionlayouta);
        mainimage_optionlayoutb = findViewById(R.id.mainimage_optionlayoutb);
        mainimage_optionlayoutc = findViewById(R.id.mainimage_optionlayoutc);
        mainimage_optionlayoutd = findViewById(R.id.mainimage_optionlayoutd);
        mainimage_optionlayoute = findViewById(R.id.mainimage_optionlayoute);
        watchline = findViewById(R.id.watchline);
        ffcplay_pause_animlayout = findViewById(R.id.ffcplay_pause_animlayout);


        ffcplay_intermediatelayout = findViewById(R.id.ffcplay_intermediatelayout);
        ffcplay_mainquestionlayout = findViewById(R.id.ffcplay_mainquestionlayout);
        imagechoise_layout = findViewById(R.id.imagechoise_layout);

        textchoise_layout = findViewById(R.id.textchoise_layout);

        textchoise_a = findViewById(R.id.textchoise_a);
        textchoise_b = findViewById(R.id.textchoise_b);
        textchoise_c = findViewById(R.id.textchoise_c);
        textchoise_d = findViewById(R.id.textchoise_d);
        textchoise_e = findViewById(R.id.textchoise_e);
        textchoise_f = findViewById(R.id.textchoise_f);
        timeleft_textlabel = findViewById(R.id.timeleft_textlabel);
        ffcplay_timelefttext = findViewById(R.id.ffcplay_timelefttext);
        ffcplay_secondtext = findViewById(R.id.ffcplay_secondtext);
        ffcplay_username = findViewById(R.id.ffcplay_username);

        imagechoise_labela = findViewById(R.id.imagechoise_labela);
        imagechoise_labelb = findViewById(R.id.imagechoise_labelb);
        imagechoise_labelc = findViewById(R.id.imagechoise_labelc);
        imagechoise_labeld = findViewById(R.id.imagechoise_labeld);
        imagechoise_labele = findViewById(R.id.imagechoise_labele);
        imagechoise_labelf = findViewById(R.id.imagechoise_labelf);
        timeleft_text = findViewById(R.id.timeleft_text);

        ffcplay_questiontext = findViewById(R.id.ffcplay_questiontext);
        try {
            ffcplay_questiontext.setTypeface(OustSdkTools.getTypefaceLight());
        } catch (Exception e) {
        }
        ffcplay_questionnotext = findViewById(R.id.ffcplay_questionnotext);

        ffcplay_useravatar = findViewById(R.id.ffcplay_useravatar);
        ffcplay_questionimage = findViewById(R.id.ffcplay_questionimage);
        ffcplay_timeleftlabel = findViewById(R.id.ffcplay_timeleftlabel);

        imageoptionA = findViewById(R.id.imageoptionA);
        imageoptionB = findViewById(R.id.imageoptionB);
        imageoptionC = findViewById(R.id.imageoptionC);
        imageoptionD = findViewById(R.id.imageoptionD);
        imageoptionE = findViewById(R.id.imageoptionE);
        imageoptionF = findViewById(R.id.imageoptionF);
        ffcstart_backimage = findViewById(R.id.ffcstart_backimage);

        textchoise_labela = findViewById(R.id.textchoise_labela);
        textchoise_labelb = findViewById(R.id.textchoise_labelb);
        textchoise_labelc = findViewById(R.id.textchoise_labelc);
        textchoise_labeld = findViewById(R.id.textchoise_labeld);
        textchoise_labele = findViewById(R.id.textchoise_labele);
        textchoise_labelf = findViewById(R.id.textchoise_labelf);
        ffcplay_warmuplabel = findViewById(R.id.ffcplay_warmuplabel);
        ffcplay_secondlabel = findViewById(R.id.ffcplay_secondlabel);
        ffcplay_timelefttexta = findViewById(R.id.ffcplay_timelefttexta);
        ffcplay_motivatetext = findViewById(R.id.ffcplay_motivatetext);
        ffcplay_mainlayout = findViewById(R.id.ffcplay_mainlayout);
        ffcplay_questionprogressbar = findViewById(R.id.ffcplay_questionprogressbar);
        intermediate_timelayout = findViewById(R.id.intermediate_timelayout);

        setImageOptionImages();
    }

    private void setImageOptionImages() {
        BitmapDrawable bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.contests));
        OustSdkTools.setDrawableToImageView(imageoptionA, bd);
        OustSdkTools.setDrawableToImageView(imageoptionB, bd);
        OustSdkTools.setDrawableToImageView(imageoptionC, bd);
        OustSdkTools.setDrawableToImageView(imageoptionD, bd);
        OustSdkTools.setDrawableToImageView(imageoptionE, bd);
        OustSdkTools.setDrawableToImageView(imageoptionF, bd);
    }

    private void setListener() {
        textchoise_mainlayouta.setOnClickListener(this);
        textchoise_mainlayoutb.setOnClickListener(this);
        textchoise_mainlayoutc.setOnClickListener(this);
        textchoise_mainlayoutd.setOnClickListener(this);
        textchoise_mainlayoute.setOnClickListener(this);
        textchoise_mainlayoutf.setOnClickListener(this);
        mainimage_optionlayouta.setOnClickListener(this);
        mainimage_optionlayoutb.setOnClickListener(this);
        mainimage_optionlayoutc.setOnClickListener(this);
        mainimage_optionlayoutd.setOnClickListener(this);
        mainimage_optionlayoute.setOnClickListener(this);
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
    }

    private void setRestCallInfo() {
        try {
            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
            appVersion = pinfo.versionName;
            deviceId = Settings.Secure.getString(OustSdkApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//====================================================================================================================================
//get contest data and set ui accordingly

    private void getContestData() {
        try {
            Intent callingIntent = getIntent();
            NetworkUtil.setOnNetworkChangeListener(this);
            delayTime = callingIntent.getLongExtra("delayTime", 0);
            Gson gson = new Gson();
            String fastestFingerContestDataStr = callingIntent.getStringExtra("fastestFingerContestData");
            if ((fastestFingerContestDataStr != null) && (!fastestFingerContestDataStr.isEmpty())) {
                fastestFingerContestData = gson.fromJson(fastestFingerContestDataStr, FastestFingerContestData.class);
            }
            mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            warmMode = callingIntent.getBooleanExtra("warmMode", false);
            activeUser = OustAppState.getInstance().getActiveUser();
            questionTime = fastestFingerContestData.getQuestionTime();
            restTime = fastestFingerContestData.getRestTime();
            if (warmMode) {
                ffcplay_warmuplabel.setVisibility(View.VISIBLE);
                qList = fastestFingerContestData.getWarmupQList();
            } else {
                ffcplay_warmuplabel.setVisibility(View.GONE);
                qList = fastestFingerContestData.getqIds();
            }
            ffcplay_questionprogressbar.setMax(qList.size());
            if (warmMode) {
                delayTime = 0;
                OustPreferences.clear("contestScore");
            }
        } catch (NullPointerException e) {
        }
    }

    private void customizeUI() {
        try {
            setProgressBarColor();
            if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
                int color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
                ffcplay_timeleftlabel.setTextColor(color);
                ffcplay_questionnotext.setTextColor(color);
                ffcplay_questiontext.setTextColor(color);
                ffcplay_username.setTextColor(color);
                ffcplay_secondtext.setTextColor(color);
                ffcplay_secondlabel.setTextColor(color);
                ffcplay_timelefttexta.setTextColor(color);
                ffcplay_motivatetext.setTextColor(color);
                timeleft_text.setTextColor(color);
                timeleft_textlabel.setTextColor(color);
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.circle_blackcorner);
                drawable.setStroke(3, color);
                OustSdkTools.setLayoutBackgroudDrawable(intermediate_timelayout, drawable);
            } else {
                GradientDrawable drawable = (GradientDrawable) getApplicationContext().getResources().getDrawable(R.drawable.circle_blackcorner);
                drawable.setStroke(3, OustSdkTools.getColorBack(R.color.whitelight));
                OustSdkTools.setLayoutBackgroudDrawable(intermediate_timelayout, drawable);
            }
            if ((fastestFingerContestData.getBgImage() != null) && (!fastestFingerContestData.getBgImage().isEmpty())) {
                if (OustSdkTools.checkInternetStatus()) {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).into(ffcstart_backimage);
                } else {
                    Picasso.get().load(fastestFingerContestData.getBgImage()).networkPolicy(NetworkPolicy.OFFLINE).into(ffcstart_backimage);
                }
            }
            if ((fastestFingerContestData.getChoiceTxtColor() != null) && (!fastestFingerContestData.getChoiceTxtColor().isEmpty())) {
                int color = Color.parseColor(fastestFingerContestData.getChoiceTxtColor());
                textchoise_a.setTextColor(color);
                textchoise_b.setTextColor(color);
                textchoise_c.setTextColor(color);
                textchoise_d.setTextColor(color);
                textchoise_e.setTextColor(color);
                textchoise_f.setTextColor(color);
                textchoise_labela.setTextColor(color);
                textchoise_labelb.setTextColor(color);
                textchoise_labelc.setTextColor(color);
                textchoise_labeld.setTextColor(color);
                textchoise_labele.setTextColor(color);
                textchoise_labelf.setTextColor(color);
                imagechoise_labela.setTextColor(color);
                imagechoise_labelb.setTextColor(color);
                imagechoise_labelc.setTextColor(color);
                imagechoise_labeld.setTextColor(color);
                imagechoise_labele.setTextColor(color);
            }
            String toolbarColorCode = OustPreferences.get("toolbarColorCode");
            if ((toolbarColorCode != null) && (!toolbarColorCode.isEmpty())) {
                ffcplay_warmuplabel.setTextColor(Color.parseColor(toolbarColorCode));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setProfilePic() {
        if ((activeUser.getAvatar() != null) && (!activeUser.getAvatar().isEmpty()) && (!activeUser.getAvatar().contains("null"))) {
            if (OustSdkTools.checkInternetStatus()) {
                Picasso.get().load(activeUser.getAvatar()).into(ffcplay_useravatar);
            } else {
                Picasso.get().load(activeUser.getAvatar()).networkPolicy(NetworkPolicy.OFFLINE).into(ffcplay_useravatar);
            }
        } else {
            OustSdkTools.setImage(ffcplay_useravatar, getResources().getString(R.string.maleavatar));
        }
        if (activeUser.getUserDisplayName() != null) {
            ffcplay_username.setText(activeUser.getUserDisplayName());
        }
    }

    //====================================================================================================================================
    //play music depend on flag
    private MediaPlayer mediaPlayer;

    private void startMusic() {
        try {
//            if (fastestFingerContestData.isEnableMusic()) {
            mediaPlayer = new MediaPlayer();
            File tempMp3 = new File(OustSdkApplication.getContext().getFilesDir(), "quiz_background.mp3");
            tempMp3.deleteOnExit();
            mediaPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(FFContestPlayActivity::onPrepared);
            mediaPlayer.start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private static void onPrepared(MediaPlayer mediaPlayer) {
    }

    private void stopMusic() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //=======================================================================================================
//detect internet connection in between
    @Override
    public void onChange(String status) {
        boolean netStatus = false;
        switch (status) {
            case "Connected to Internet with Mobile Data":
                netStatus = true;
                break;
            case "Connected to Internet with WIFI":
                netStatus = true;
                break;
            default:
                netStatus = false;
                break;
        }
        if ((!netStatus)) {
            showNoInternetPopup();
        }
    }

    private boolean shownNoInternetPopup = false;

    private void showNoInternetPopup() {
        try {
            if ((!shownNoInternetPopup) && (isActivityLive)) {
                shownNoInternetPopup = true;
                saveData();
                if ((timer != null)) {
                    timer.cancel();
                }
                Popup popup = new Popup();
                OustPopupButton oustPopupButton = new OustPopupButton();
                oustPopupButton.setBtnText("OK");
                List<OustPopupButton> btnList = new ArrayList<>();
                btnList.add(oustPopupButton);
                popup.setButtons(btnList);
                popup.setErrorPopup(true);
                if ((fastestFingerContestData.getNoInternetMsg() != null) && (!fastestFingerContestData.getNoInternetMsg().isEmpty())) {
                    popup.setContent(fastestFingerContestData.getNoInternetMsg());
                } else {
                    popup.setContent(getResources().getString(R.string.ffc_nointernet_msg));
                }
                popup.setCategory(OustPopupCategory.NOACTION);
                OustStaticVariableHandling.getInstance().setOustpopup(popup);
                Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OustSdkApplication.getContext().startActivity(intent);
                FFContestPlayActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //====================================================================================================================================================
//timer related methodes
    public void startTimer(long totalTimea) {
        try {
            ffcplay_timelefttext.setText(("" + (totalTimea / 1000)));
            ffcplay_questionprogressbar.setMax((int) totalTimea);
            ffcplay_questionprogressbar.setProgress((int) totalTimea);
            ObjectAnimator animation = ObjectAnimator.ofInt(ffcplay_questionprogressbar, "progress", ((int) totalTimea), (0));
            animation.setDuration(totalTimea); // 0.5 second
            animation.start();
            timer = new CounterClass(totalTimea, 1000);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            currentTime = 0;
            ffcplay_timelefttext.setText("0");
            timeleft_text.setText("0");
            if (isIntermediateScreenOn) {
                isIntermediateScreenOn = false;
                questionNo++;
                showNextQuestion(true, 0);
            } else {
                setUserAnswer("", false);
                answerSec = 0;
                waitForRestTime(0);
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onTick(long millisUntilFinished) {
            currentTime = millisUntilFinished;
            if (isIntermediateScreenOn) {
                timeleft_text.setText("" + (currentTime / 1000));
                if (!warmMode) {
                    getEnrollCount();
                }
                if ((currentTime / 1000) < 6) {
                    setScaleAnim();
                }
            } else {
                ffcplay_timelefttext.setText("" + (currentTime / 1000));
                if ((currentTime / 1000) <= 5) {
                    setRedProgressBarColor();
                }
            }
        }
    }
//=======================================================================================================
//set dynamic UI methodes

    //set progress bar color red when time left is less then 5 sec
    private void setRedProgressBarColor() {
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.yellow_progress);
        final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
        d1.setColorFilter(OustSdkTools.getColorBack(R.color.clear_red), mode);
        ffcplay_questionprogressbar.setProgressDrawable(ld);
    }

    private void setProgressBarColor() {
        int color = OustSdkTools.getColorBack(R.color.yellow);
        if ((fastestFingerContestData.getProgressBarColor() != null) && (!fastestFingerContestData.getProgressBarColor().isEmpty())) {
            color = Color.parseColor(fastestFingerContestData.getProgressBarColor());
        }
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        final LayerDrawable ld = (LayerDrawable) getApplicationContext().getResources().getDrawable(R.drawable.yellow_progress);
        final ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.customPlayerProgress);
        d1.setColorFilter(color, mode);
        ffcplay_questionprogressbar.setProgressDrawable(ld);
        ffcplay_timelefttext.setTextColor(color);
    }


    //set scale anim when time left is less then 5 sec
    private void setScaleAnim() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(timeleft_text, "scaleX", 0.5f, 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(timeleft_text, "scaleY", 0.5f, 1.0f);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getEnrollCount();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    //called when selected mcq text option
    private void textAnswerSelected() {
        if ((selectedAnswer != null) && (!selectedAnswer.isEmpty())) {
            disableAllOption();
            boolean isCorrect = false;
            long currentTime = FFcontestStartActivity.getCurrentTime();
            answerSec = currentTime - questionStartTime;
            if (((questions.getQuestionType().equalsIgnoreCase("MCQ")) || (questions.getQuestionType().equalsIgnoreCase("TRUE_FALSE")))) {
                if ((selectedAnswer != null) && (questions.getAnswer() != null)) {
                    if (selectedAnswer.equalsIgnoreCase(questions.getAnswer())) {
                        isCorrect = true;
                        setRightAnswer(selectedAnswer);
                    }
                }
            }
            setUserAnswer(selectedAnswer, isCorrect);
            waitForRestTime(0);
        }
    }

    //called when selected mcq image option
    private void ImageAnswerSelected() {
        if ((selectedAnswer != null) && (!selectedAnswer.isEmpty())) {
            disableAllOption();
            boolean isCorrect = false;
            long currentTime = FFcontestStartActivity.getCurrentTime();
            answerSec = currentTime - questionStartTime;
            if (((questions.getQuestionType().equalsIgnoreCase("MCQ")) || (questions.getQuestionType().equalsIgnoreCase("TRUE_FALSE")))) {
                if ((selectedAnswer != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)) {
                    if (selectedAnswer.equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName())) {
                        isCorrect = true;
                        setRightAnswer(selectedAnswer);
                    }
                }
            }
            setUserAnswer(selectedAnswer, isCorrect);
            waitForRestTime(0);
        }
    }

    //reset all option at start of question
    private void resetAllImageOption() {
        try {
            int color = OustSdkTools.getColorBack(R.color.whitelight);
            if ((fastestFingerContestData.getQuestionTxtColor() != null) && (!fastestFingerContestData.getQuestionTxtColor().isEmpty())) {
                color = Color.parseColor(fastestFingerContestData.getQuestionTxtColor());
            }
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionA, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionB, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionC, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionD, null);
            OustSdkTools.setLayoutBackgroudDrawable(imageoptionE, null);
            imagechoise_labela.setTextColor(color);
            imagechoise_labelb.setTextColor(color);
            imagechoise_labelc.setTextColor(color);
            imagechoise_labeld.setTextColor(color);
            imagechoise_labele.setTextColor(color);
            imagechoise_labelf.setTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAllOption() {
        try {
            GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_grayback);
            GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_whiteback);
            if ((fastestFingerContestData.getChoiceBgColorDark() != null) && (!fastestFingerContestData.getChoiceBgColorDark().isEmpty()) &&
                    (fastestFingerContestData.getChoiceBgColorLight() != null) && (!fastestFingerContestData.getChoiceBgColorLight().isEmpty())) {
                int color = Color.parseColor(fastestFingerContestData.getChoiceBgColorDark());
                drawable.setColor(color);
                int color1 = Color.parseColor(fastestFingerContestData.getChoiceBgColorLight());
                drawable1.setColor(color1);
            }
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayouta, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayouta, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutb, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutb, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutc, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutc, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutd, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutd, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoute, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoute, drawable1);

            OustSdkTools.setLayoutBackgroudDrawable(textchoise_mainlayoutf, drawable);
            OustSdkTools.setLayoutBackgroudDrawable(textchoise_sublayoutf, drawable1);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //set back ui when ption is selected
    private void textOptionSelected(RelativeLayout mainLayout, RelativeLayout subLayout) {
        try {
            if ((fastestFingerContestData.getChoiceBgColorSelectedDark() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedDark().isEmpty()) &&
                    (fastestFingerContestData.getChoiceBgColorSelectedLight() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedLight().isEmpty())) {
                GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_darkgaryback);
                GradientDrawable drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_liteblueback);
                drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_darkgaryback);
                int color = Color.parseColor(fastestFingerContestData.getChoiceBgColorSelectedDark());
                drawable.setColor(color);
                drawable1 = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.roundedcorner_liteblueback);
                color = Color.parseColor(fastestFingerContestData.getChoiceBgColorSelectedLight());
                drawable1.setColor(color);
                OustSdkTools.setLayoutBackgroudDrawable(mainLayout, drawable);
                OustSdkTools.setLayoutBackgroudDrawable(subLayout, drawable1);
            } else {
                OustSdkTools.setLayoutBackgroud(mainLayout, R.drawable.roundedcorner_darkgaryback);
                OustSdkTools.setLayoutBackgroud(subLayout, R.drawable.roundedcorner_liteblueback);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void imageOptionSelected(ImageView mainLayout, TextView textView) {
        try {
            if ((fastestFingerContestData.getChoiceBgColorSelectedDark() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedDark().isEmpty()) &&
                    (fastestFingerContestData.getChoiceBgColorSelectedLight() != null) && (!fastestFingerContestData.getChoiceBgColorSelectedLight().isEmpty())) {
                int textColor = OustSdkTools.getColorBack(R.color.splash_screencolora);
                textView.setTextColor(textColor);
                GradientDrawable drawable = (GradientDrawable) OustSdkTools.getImgDrawable(R.drawable.ffc_selectback);
                int color = Color.parseColor(fastestFingerContestData.getChoiceBgColorSelectedDark());
                drawable.setStroke(2, color);
                OustSdkTools.setLayoutBackgroudDrawable(mainLayout, drawable);
            } else {
                textView.setTextColor(OustSdkTools.getColorBack(R.color.splash_screencolora));
                OustSdkTools.setLayoutBackgroudDrawable(mainLayout, OustSdkTools.getImgDrawable(R.drawable.ffc_selectback));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
//================================================================================================================================

    private String selectedAnswer = "";

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textchoise_mainlayouta) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayouta, textchoise_sublayouta);
            selectedAnswer = questions.getA();
            textAnswerSelected();
        } else if (view.getId() == R.id.textchoise_mainlayoutb) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayoutb, textchoise_sublayoutb);
            selectedAnswer = questions.getB();
            textAnswerSelected();
        } else if (view.getId() == R.id.textchoise_mainlayoutc) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayoutc, textchoise_sublayoutc);
            selectedAnswer = questions.getC();
            textAnswerSelected();
        } else if (view.getId() == R.id.textchoise_mainlayoutd) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayoutd, textchoise_sublayoutd);
            selectedAnswer = questions.getD();
            textAnswerSelected();
        } else if (view.getId() == R.id.textchoise_mainlayoute) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayoute, textchoise_sublayoute);
            selectedAnswer = questions.getE();
            textAnswerSelected();
        } else if (view.getId() == R.id.textchoise_mainlayoutf) {
            resetAllOption();
            textOptionSelected(textchoise_mainlayoutf, textchoise_sublayoutf);
            selectedAnswer = questions.getF();
            textAnswerSelected();
        } else if (view.getId() == R.id.mainimage_optionlayouta) {
            resetAllImageOption();
            selectedAnswer = questions.getImageChoiceA().getImageFileName();
            imageOptionSelected(imageoptionA, imagechoise_labela);
            ImageAnswerSelected();
        } else if (view.getId() == R.id.mainimage_optionlayoutb) {
            resetAllImageOption();
            selectedAnswer = questions.getImageChoiceB().getImageFileName();
            imageOptionSelected(imageoptionB, imagechoise_labelb);
            ImageAnswerSelected();
        } else if (view.getId() == R.id.mainimage_optionlayoutc) {
            resetAllImageOption();
            selectedAnswer = questions.getImageChoiceC().getImageFileName();
            imageOptionSelected(imageoptionC, imagechoise_labelc);
            ImageAnswerSelected();
        } else if (view.getId() == R.id.mainimage_optionlayoutd) {
            resetAllImageOption();
            selectedAnswer = questions.getImageChoiceD().getImageFileName();
            imageOptionSelected(imageoptionD, imagechoise_labeld);
            ImageAnswerSelected();
        } else if (view.getId() == R.id.mainimage_optionlayoute) {
            resetAllImageOption();
            selectedAnswer = questions.getImageChoiceE().getImageFileName();
            imageOptionSelected(imageoptionE, imagechoise_labele);
            ImageAnswerSelected();
        }
    }


    private void waitForRestTime(long time) {
        isIntermediateScreenOn = true;
        ffcplay_timelefttext.setVisibility(View.GONE);
        if ((fastestFingerContestData.getIntermediateMessages() != null) && (fastestFingerContestData.getIntermediateMessages().size() > 0)) {
            Random r = new Random();
            int no = r.nextInt(fastestFingerContestData.getIntermediateMessages().size());
            ffcplay_motivatetext.setText(fastestFingerContestData.getIntermediateMessages().get(no));
        }
        ffcplay_timeleftlabel.setText(getResources().getString(R.string.time_left_msg));
        if ((timer != null)) {
            timer.cancel();
        }
        ffcplay_secondtext.setText("" + (answerSec / 1000) + "." + (answerSec % 1000));
        long currentTime1 = FFcontestStartActivity.getCurrentTime();
        long remainingTime = (questionTime - (currentTime1 - questionStartTime + questionDelayedTime));
        long timeToWait = (remainingTime + restTime);
        if (time > 0) {
            timeToWait = time;
        }
        if (timeToWait < 0) {
            timeToWait = 0;
        }
        if (questionNo == (qList.size() - 1)) {
            calculateFinalScore();
        } else {
            showInterMidiateScreenWithAnimation();
            ffcplay_timelefttext.setText(("" + (timeToWait / 1000)));
            timer = new CounterClass(timeToWait, 1000);
            timer.start();
        }
    }

    private void showInterMidiateScreenWithAnimation() {
        ffcplay_intermediatelayout.setVisibility(View.VISIBLE);
        ffcplay_mainquestionlayout.setVisibility(View.VISIBLE);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(ffcplay_mainquestionlayout, "x", 0, -scrWidth);
        moveX.setDuration(400);
        moveX.start();
        ObjectAnimator moveXa = ObjectAnimator.ofFloat(ffcplay_intermediatelayout, "x", scrWidth, 0);
        moveXa.setDuration(400);
        moveXa.start();
        moveXa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ffcplay_intermediatelayout.setVisibility(View.VISIBLE);
                ffcplay_mainquestionlayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void showQuestionScreenWithAnimation() {
        ffcplay_intermediatelayout.setVisibility(View.VISIBLE);
        ffcplay_mainquestionlayout.setVisibility(View.VISIBLE);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(ffcplay_intermediatelayout, "x", 0, -scrWidth);
        moveX.setDuration(400);
        moveX.start();
        ObjectAnimator moveXa = ObjectAnimator.ofFloat(ffcplay_mainquestionlayout, "x", scrWidth, 0);
        moveXa.setDuration(400);
        moveXa.start();
        moveXa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ffcplay_intermediatelayout.setVisibility(View.GONE);
                ffcplay_mainquestionlayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void jumpToQuestion() {
        try {
            if (delayTime > 0) {
                delayTime = FFcontestStartActivity.getCurrentTime() - fastestFingerContestData.getStartTime();
                Log.e("delayTime------", "" + delayTime);
                questionNo = (int) (delayTime / (questionTime + restTime));
                long delayTimeForQuestion = (int) (delayTime % (questionTime + restTime));
                Log.e("delayTimeF------", "" + delayTimeForQuestion);
                Log.e("questionNo------", "" + questionNo);
                String contestScoreStr = OustPreferences.get("contestScore");
                if ((contestScoreStr != null) && (!contestScoreStr.isEmpty())) {
                    Gson gson = new Gson();
                    ffcResponceList = gson.fromJson(OustPreferences.get("contestScore"), new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
                }
                if ((delayTimeForQuestion < questionTime)) {
                    if (ffcResponceList.size() < (questionNo + 1)) {
                        createScoreList(ffcResponceList.size());
                        showNextQuestion(false, (questionTime - delayTimeForQuestion));
                    } else {
                        createScoreList(ffcResponceList.size());
                        getAnswerSecOfPreviousQues();
                        waitForRestTime(((questionTime + restTime) - delayTimeForQuestion));
                    }
                } else {
                    createScoreList(ffcResponceList.size());
                    getAnswerSecOfPreviousQues();
                    waitForRestTime(((questionTime + restTime) - delayTimeForQuestion));
                }
            } else {
                showNextQuestion(false, 0);
            }
        } catch (Exception e) {
            int n1 = 0;
            int n2 = n1 + 2;
        }
    }


    private void getAnswerSecOfPreviousQues() {
        try {
            if (ffcResponceList.size() > 0) {
                if (ffcResponceList.get(questionNo).get("responseTime") != null) {
                    if (ffcResponceList.get(questionNo).get("responseTime").getClass().equals(Double.class)) {
                        double d = (double) ffcResponceList.get(questionNo).get("responseTime");
                        answerSec = (long) d;
                    } else if (ffcResponceList.get(questionNo).get("responseTime").getClass().equals(Long.class)) {
                        answerSec = (long) ffcResponceList.get(questionNo).get("responseTime");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void createScoreList(int startCount) {
        for (int i = startCount; i < questionNo; i++) {
            Map<String, Object> ffcResponce = new HashMap<>();
            ffcResponce.put("answer", "");
            ffcResponce.put("correct", false);
            ffcResponce.put("userId", activeUser.getStudentid());
            ffcResponce.put("userScore", 0);
            ffcResponce.put("avatar", activeUser.getAvatar());
            ffcResponce.put("displayName", activeUser.getUserDisplayName());
            long qID = Long.parseLong(qList.get(i));
            ffcResponce.put("qId", qID);
            long time = 0;
            ffcResponce.put("responseTime", time);
            ffcResponceList.add(ffcResponce);
        }
        saveData();
    }

    private int questionNo;
    private DTOQuestions questions;
    private long questionDelayedTime = 0;

    private void showNextQuestion(boolean showAnimation, long time) {
        rightAnswerCount = 0;
        ffcplay_timelefttext.setVisibility(View.VISIBLE);
        isIntermediateScreenOn = false;
        if (warmMode) {
            long currentTime = FFcontestStartActivity.getCurrentTime();
            long startTime = fastestFingerContestData.getStartTime();
            if ((startTime - currentTime) < fastestFingerContestData.getWarmupSwitchTime()) {
                OustStaticVariableHandling.getInstance().setShowInstructionPopup(true);
                FFContestPlayActivity.this.finish();
                return;
            }
        }
        questionAttempted = false;
        setProgressBarColor();
        removeAllViewAndListener();
        ffcplay_timeleftlabel.setText(getResources().getString(R.string.time_left_camel));
        if (questionNo == qList.size()) {
            FFContestPlayActivity.this.finish();
        } else {
            questionStartTime = FFcontestStartActivity.getCurrentTime();
            //if user joins contest in between
            if (time > 0) {
                questionDelayedTime = (questionTime - time);
                if (questionDelayedTime < 0) {
                    questionDelayedTime = 0;
                }
            } else {
                questionDelayedTime = 0;
            }
            Log.e("showNextQuestion------", "" + questionStartTime);
            if (showAnimation) {
                showQuestionScreenWithAnimation();
                startTimer(questionTime);
            } else {
                if (time > 0) {
                    startTimer(time);
                } else {
                    startTimer(questionTime);
                }
            }
            selectedAnswer = "";
            ffcplay_questionnotext.setText("" + (questionNo + 1) + "/" + qList.size());
            questions = OustSdkTools.databaseHandler.getQuestionById(Integer.parseInt(qList.get(questionNo)), true);
            showQuestion();
        }
    }

    private void showQuestion() {
        try {
            hideAllOption();
            enableAllOption();
            resetAllOption();
            resetAllImageOption();
            showQuestionImage();
            String questionType = questions.getQuestionType();
            if (questionType != null) {
                questionType = QuestionType.MCQ;
            }
            if (questions != null) {
                if ((questions.getQuestionCategory() != null) && (questions.getQuestionCategory().equals(QuestionCategory.IMAGE_CHOICE))) {
                    if ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE))) {
                        showMCQImageOptions();
                    }
                } else {
                    if ((questionType.equals(QuestionType.MCQ)) || (questionType.equals(QuestionType.TRUE_FALSE))) {
                        showMCQTextOptions();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showQuestionImage() {
        try {
            if (questions.getQuestion() != null) {
                ffcplay_questiontext.setHtml(questions.getQuestion());
                ffcplay_questiontext.setVisibility(View.VISIBLE);
            }
            String str = questions.getImage();
            if ((str != null) && (!str.isEmpty())) {
                ffcplay_questionimage.setVisibility(View.VISIBLE);
                setImageOptionA(str, ffcplay_questionimage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void hideAllOption() {
        textchoise_layout.setVisibility(View.GONE);
        textchoise_mainlayouta.setVisibility(View.GONE);
        textchoise_mainlayoutb.setVisibility(View.GONE);
        textchoise_mainlayoutc.setVisibility(View.GONE);
        textchoise_mainlayoutd.setVisibility(View.GONE);
        textchoise_mainlayoute.setVisibility(View.GONE);
        textchoise_mainlayoutf.setVisibility(View.GONE);
        mainimage_optionlayouta.setVisibility(View.GONE);
        mainimage_optionlayoutb.setVisibility(View.GONE);
        mainimage_optionlayoutc.setVisibility(View.GONE);
        mainimage_optionlayoutd.setVisibility(View.GONE);
        mainimage_optionlayoute.setVisibility(View.GONE);
        imagechoise_layout.setVisibility(View.GONE);
        ffcplay_questiontext.setVisibility(View.GONE);
        ffcplay_questionimage.setVisibility(View.GONE);
    }

    private void enableAllOption() {
        textchoise_mainlayouta.setEnabled(true);
        textchoise_mainlayoutb.setEnabled(true);
        textchoise_mainlayoutc.setEnabled(true);
        textchoise_mainlayoutd.setEnabled(true);
        textchoise_mainlayoute.setEnabled(true);
        textchoise_mainlayoutf.setEnabled(true);
        mainimage_optionlayouta.setEnabled(true);
        mainimage_optionlayoutb.setEnabled(true);
        mainimage_optionlayoutc.setEnabled(true);
        mainimage_optionlayoutd.setEnabled(true);
        mainimage_optionlayoute.setEnabled(true);
    }

    private void disableAllOption() {
        textchoise_mainlayouta.setEnabled(false);
        textchoise_mainlayoutb.setEnabled(false);
        textchoise_mainlayoutc.setEnabled(false);
        textchoise_mainlayoutd.setEnabled(false);
        textchoise_mainlayoute.setEnabled(false);
        textchoise_mainlayoutf.setEnabled(false);
        mainimage_optionlayouta.setEnabled(false);
        mainimage_optionlayoutb.setEnabled(false);
        mainimage_optionlayoutc.setEnabled(false);
        mainimage_optionlayoutd.setEnabled(false);
        mainimage_optionlayoute.setEnabled(false);
    }

    private void showMCQTextOptions() {
        try {
            textchoise_layout.setVisibility(View.VISIBLE);
            if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getA(), textchoise_a);
                textchoise_mainlayouta.setVisibility(View.VISIBLE);
            }
            if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getB(), textchoise_b);
                textchoise_mainlayoutb.setVisibility(View.VISIBLE);
            }
            if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getC(), textchoise_c);
                textchoise_mainlayoutc.setVisibility(View.VISIBLE);
            }
            if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getD(), textchoise_d);
                textchoise_mainlayoutd.setVisibility(View.VISIBLE);
            }
            if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getE(), textchoise_e);
                textchoise_mainlayoute.setVisibility(View.VISIBLE);
            }
            if ((questions.getF() != null) && (!questions.getF().isEmpty())) {
                OustSdkTools.getSpannedContent(questions.getF(), textchoise_f);
                textchoise_mainlayoutf.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showMCQImageOptions() {
        try {
            imagechoise_layout.setVisibility(View.VISIBLE);
            if ((questions.getImageChoiceA() != null) && (questions.getImageChoiceA().getImageData() != null) && (questions.getImageChoiceA().getImageFileName() != null)) {
                mainimage_optionlayouta.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionA);
                setImageOptionA(questions.getImageChoiceA().getImageData(), imageoptionA);
            }
            if ((questions.getImageChoiceB() != null) && (questions.getImageChoiceB().getImageData() != null) && (questions.getImageChoiceB().getImageFileName() != null)) {
                mainimage_optionlayoutb.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionB);
                setImageOptionA(questions.getImageChoiceB().getImageData(), imageoptionB);
            }
            if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageData() != null) && (questions.getImageChoiceC().getImageFileName() != null)) {
                mainimage_optionlayoutc.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionC);
                setImageOptionA(questions.getImageChoiceC().getImageData(), imageoptionC);
            }
            if ((questions.getImageChoiceD() != null) && (questions.getImageChoiceD().getImageData() != null) && (questions.getImageChoiceD().getImageFileName() != null)) {
                mainimage_optionlayoutd.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionD);
                setImageOptionA(questions.getImageChoiceD().getImageData(), imageoptionD);
            }
            if ((questions.getImageChoiceE() != null) && (questions.getImageChoiceE().getImageData() != null) && (questions.getImageChoiceE().getImageFileName() != null)) {
                mainimage_optionlayoute.setVisibility(View.VISIBLE);
                setLayoutAspectRatiosmall(imageoptionE);
                setImageOptionA(questions.getImageChoiceE().getImageData(), imageoptionE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLayoutAspectRatiosmall(ImageView layout) {
        try {
            int size = (int) getResources().getDimension(R.dimen.oustlayout_dimen25);
            int imageWidth = (scrWidth / 2) - size;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            float h = (imageWidth * 0.563f);
            int h1 = (int) h;
            params.height = h1;
            params.width = imageWidth;
            layout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setImageOptionA(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //================================================================================================================================================
    //set scores on firebase
    private void setRightAnswer(String answer) {
        if (!warmMode) {
            String node = "f3cQuestionResponse/" + "f3cId" + fastestFingerContestData.getFfcId() + "/qId" + questions.getQuestionId() + "/userKey" + activeUser.getStudentKey();
            Map<String, Object> ffcResponce = new HashMap<>();
            ffcResponce.put("answer", answer);
            ffcResponce.put("correct", true);
            ffcResponce.put("userId", activeUser.getStudentid());
            long totalScore = (questionTime - answerSec);
            ffcResponce.put("userScore", totalScore);
            ffcResponce.put("avatar", activeUser.getAvatar());
            ffcResponce.put("displayName", activeUser.getUserDisplayName());
            ffcResponce.put("qId", questions.getQuestionId());
            ffcResponce.put("responseTime", answerSec);
            ffcResponce.put("timestamp", FFcontestStartActivity.getCurrentTime());
            ffcResponce.put("appV", appVersion);
            ffcResponce.put("deviceId", deviceId);
            ffcResponce.put("devicePlatformName", "Android");
//            OustFirebaseTools.getRootRef().child(node).setValue(ffcResponce);
        }
    }

    private boolean questionAttempted = false;

    private void setUserAnswer(String answer, boolean isCorrect) {
        try {
            if (!warmMode) {
                if (!questionAttempted) {
                    getData();
                    questionAttempted = true;
                    updateUserCountForQuestion(questions.getQuestionId());
                    String node = "f3cUserResponse/f3cId" + fastestFingerContestData.getFfcId() + "/userKey" + activeUser.getStudentKey() + "/qId" + questions.getQuestionId();
                    Map<String, Object> ffcResponce = new HashMap<>();
                    ffcResponce.put("answer", answer);
                    ffcResponce.put("correct", isCorrect);
                    ffcResponce.put("userId", activeUser.getStudentid());
                    if (isCorrect) {
                        long totalScore = (questionTime - answerSec);
                        ffcResponce.put("userScore", totalScore);
                    }
                    ffcResponce.put("avatar", activeUser.getAvatar());
                    ffcResponce.put("displayName", activeUser.getUserDisplayName());
                    ffcResponce.put("qId", questions.getQuestionId());
                    ffcResponce.put("responseTime", ((answerSec > 0) ? answerSec : questionTime));
                    ffcResponce.put("timestamp", FFcontestStartActivity.getCurrentTime());
                    ffcResponce.put("appV", appVersion);
                    ffcResponce.put("deviceId", deviceId);
                    ffcResponce.put("devicePlatformName", "Android");
                    ffcResponceList.add(ffcResponce);
//                OustFirebaseTools.getRootRef().child(node).setValue(ffcResponce);
                    saveData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void calculateFinalScore() {
        if (!warmMode) {
            getData();
            OustStaticVariableHandling.getInstance().setContestOver(true);
            int totalRight = 0;
            long totalScore = 0;
            long totalTimea = 0;
            List<UserF3CQuestionScoreData> questionScoreData = new ArrayList<>();
            for (int i = 0; i < ffcResponceList.size(); i++) {
                try {
                    Map<String, Object> ffcResponce = ffcResponceList.get(i);
                    if (((boolean) ffcResponce.get("correct"))) {
                        totalRight++;
                        long respTime = 0;
                        if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
                            double d = (double) ffcResponce.get("responseTime");
                            respTime = (long) d;
                        } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
                            respTime = (long) ffcResponce.get("responseTime");
                        }
                        totalTimea += respTime;
                    }
                    UserF3CQuestionScoreData userF3CQuestionScoreData = new UserF3CQuestionScoreData();
                    if (ffcResponce.get("qId").getClass().equals(Double.class)) {
                        double d = (double) ffcResponce.get("qId");
                        userF3CQuestionScoreData.setqId(((long) d));
                    } else if (ffcResponce.get("qId").getClass().equals(Long.class)) {
                        userF3CQuestionScoreData.setqId(((long) ffcResponce.get("qId")));
                    }
                    if (ffcResponce.get("responseTime").getClass().equals(Double.class)) {
                        double d = (double) ffcResponce.get("responseTime");
                        userF3CQuestionScoreData.setResponseTime(((long) d));
                    } else if (ffcResponce.get("responseTime").getClass().equals(Long.class)) {
                        userF3CQuestionScoreData.setResponseTime(((long) ffcResponce.get("responseTime")));
                    }
                    if (ffcResponce.get("answer") != null) {
                        userF3CQuestionScoreData.setAnswer((String) ffcResponce.get("answer"));
                    }
                    userF3CQuestionScoreData.setCorrect(((boolean) ffcResponce.get("correct")));
                    questionScoreData.add(userF3CQuestionScoreData);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            long avgTime = 0;
            if (totalRight > 0) {
                avgTime = (totalTimea / totalRight);
                totalScore = ((questionTime * totalRight) + (questionTime - avgTime));
            }
            UserF3CScoreRequestData_v2 userF3CScoreRequestData_v2 = new UserF3CScoreRequestData_v2();
            userF3CScoreRequestData_v2.setQuestionScoreData(questionScoreData);
            UserF3CScoreData userF3CScoreData = new UserF3CScoreData();
            userF3CScoreData.setAvatar(activeUser.getAvatar());
            userF3CScoreData.setAverageTime(avgTime);
            userF3CScoreData.setDisplayName(activeUser.getUserDisplayName());
            userF3CScoreData.setUserId(activeUser.getStudentid());
            userF3CScoreData.setAvatar(activeUser.getAvatar());
            userF3CScoreData.setF3cId(fastestFingerContestData.getFfcId());
            userF3CScoreData.setRightCount(totalRight);
            userF3CScoreData.setScore(totalScore);
            userF3CScoreRequestData_v2.setScoreData(userF3CScoreData);
            Gson gson = new Gson();
            String str = gson.toJson(userF3CScoreRequestData_v2);
            OustPreferences.save("f3contestrequest", str);
            sendUserRigntCount(totalScore, totalRight);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SendApiServices.class);
            OustSdkApplication.getContext().startService(intent);
        }
        stopMusic();
        showEndContestAnimation();
    }


    private void sendUserRigntCount(long score, int totalRight) {
        try {
            if (totalRight > fastestFingerContestData.getLuckyWinnerCorrectCount()) {
                ContestUserDataRequest contestUserDataRequest = new ContestUserDataRequest();
                contestUserDataRequest.setStudentid(activeUser.getStudentid());
                contestUserDataRequest.setF3cId(("" + fastestFingerContestData.getFfcId()));
                contestUserDataRequest.setScore(("" + score));
                Gson gson = new Gson();
                String str = gson.toJson(contestUserDataRequest);
                OustPreferences.save("f3cuser_rightcount_request", str);
            }
        } catch (Exception e) {
        }
    }

    //================================================================================================================================================
    private void showEndContestAnimation() {
        FFContestPlayActivity.this.finish();
//        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ffcplay_mainlayout, "alpha",1.0f,0.0f);
//        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ffcplay_mainlayout, "scaleY",1.0f,0.0f);
//        scaleDownX.setDuration(1000);
//        scaleDownY.setDuration(1000);
//        AnimatorSet scaleDown = new AnimatorSet();
//        scaleDown.play(scaleDownX).with(scaleDownY);
//        scaleDown.start();
//        scaleDown.setStartDelay(800);
//        scaleDown.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {}
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {}
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {}
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    private void saveData() {
        try {
            String listSerializedToJson = new Gson().toJson(ffcResponceList);
            Log.e("------contestScore--", listSerializedToJson);
            OustPreferences.save("contestScore", listSerializedToJson);
        } catch (Exception e) {
        }
    }

    private void getData() {
        try {
            String contestScoreStr = OustPreferences.get("contestScore");
            if ((contestScoreStr != null) && (!contestScoreStr.isEmpty())) {
                Log.e("------contestScore--", contestScoreStr);
                Gson gson = new Gson();
                ffcResponceList = gson.fromJson(OustPreferences.get("contestScore"), new TypeToken<List<Map<String, Object>>>() {
                }.getType());
            }
        } catch (Exception e) {
        }
    }


    private void updateUserCountForQuestion(long qId) {
        String message = "f3cAttemptedQuestionCount/f3cId" + fastestFingerContestData.getFfcId() + "/qID" + qId;
        DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message);
        firebase.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {
                if (DatabaseError != null) {
                    Log.e("", "Firebase counter increment failed. New Count:{}" + dataSnapshot);
                } else {
                    Log.e("", "Firebase counter increment succeeded.");
                }
            }
        });
    }

    private long rightAnswerCount = 0;

    public void getEnrollCount() {
        try {
            if ((questions != null) && (questions.getQuestionId() > 0)) {
                String message = "f3cAttemptedQuestionCount/f3cId" + fastestFingerContestData.getFfcId() + "/qID" + questions.getQuestionId();
                ValueEventListener myFFCListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null) {
                                long currentCount = (long) dataSnapshot.getValue();
                                if ((rightAnswerCount < currentCount) && (currentCount > 0)) {
                                    rightAnswerCount = currentCount;
                                    showRightAnswerCountWithAnimation();
                                }
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
                OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(myFFCListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<View> animView = new ArrayList<>();
    private int animViewCount = 0;

    private void showRightAnswerCountWithAnimation() {
        if (animViewCount == 3) {
            animViewCount--;
            if (animView.size() > 0) {
                ffcplay_pause_animlayout.removeView(animView.remove(0));
            }
        }
        Random r = new Random();
        animViewCount++;
        View convertView = mInflater.inflate(R.layout.ffc_rightanswercount, null);
        TextView ffc_rightcounttext = convertView.findViewById(R.id.ffc_rightcounttext);
        ffc_rightcounttext.setText("" + rightAnswerCount);
        int w = r.nextInt(ffcplay_pause_animlayout.getWidth());
        int h = r.nextInt(ffcplay_pause_animlayout.getHeight());
        if (w > (ffcplay_pause_animlayout.getWidth() - 100)) {
            w -= 100;
        }
        if (h > (ffcplay_pause_animlayout.getHeight() - 100)) {
            h -= 100;
        }
        convertView.setX(w);
        convertView.setY(h);
        setAnimation(convertView);
        ffcplay_pause_animlayout.addView(convertView);
    }

    private void removeAllViewAndListener() {
        while (animView.size() > 0) {
            ffcplay_pause_animlayout.removeView(animView.remove(0));
        }
    }

    private void setAnimation(View view) {
        try {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.6f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.6f);
            ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
            scaleDownX.setDuration(3000);
            scaleDownY.setDuration(3000);
            alphaAnim.setDuration(5000);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY).with(alphaAnim);
            scaleDown.start();
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        if (timer != null) {
            timer.cancel();
        }
        isActivityLive = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if ((timer != null)) {
                timer.cancel();
            }
            if (!warmMode) {
                long currentTime = FFcontestStartActivity.getCurrentTime();
                long totalContestTime = ((fastestFingerContestData.getQuestionTime() * fastestFingerContestData.getqIds().size()) +
                        (fastestFingerContestData.getRestTime() * (fastestFingerContestData.getqIds().size() - 1)));
                long timeToEndContest = (((fastestFingerContestData.getStartTime() + totalContestTime) - currentTime) / 1000);
                if (timeToEndContest > 0) {
                    OustPreferences.saveTimeForNotification("timeToEndContest", timeToEndContest);
                }
                Log.e("--------------------", "" + timeToEndContest);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ContestScoreSubmitService.class);
                this.startService(intent);
            } else {
                OustPreferences.clear("contestScore");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        FFContestPlayActivity.this.finish();
    }
}
