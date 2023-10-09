package com.oustme.oustsdk.activity.assessments;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.leaderboard.activity.GroupLBDataActivity;
import com.oustme.oustsdk.customviews.CustomScrollView;
import com.oustme.oustsdk.customviews.OustCheckBox;
import com.oustme.oustsdk.customviews.QuestionOptionBigImageView;
import com.oustme.oustsdk.customviews.QuestionOptionImageView;
import com.oustme.oustsdk.customviews.ScaleImageView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.interfaces.common.OnNetworkChangeListener;
import com.oustme.oustsdk.presenter.assessments.QuestionActivityPresenter;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.OustFillData;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.EntityResourceData;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOResourceData;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.NetworkUtil;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.OustTagHandler;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

public class QuestionActivity extends AppCompatActivity implements OnNetworkChangeListener, View.OnClickListener, View.OnTouchListener {

    private final String TAG = QuestionActivity.class.getName();
    TextView txtTimerTextView, txtTopicName, txtChallengerScore, txtOpponentScore, challengerName,
            opponentName, groupName, txtGroupAvatar, questTitle, questCount, questionprogresstext,
            questionprogresslable, assessmenttimer, assessmentscore, offlinemode_textview, questionTextView;
    Button txtChoiceA, txtChoiceB, txtChoiceD, txtChoiceC, skipques_button, fillconfirm_btn, txtChoiceE, txtChoiceF, txtChoiceG, imagques_skipques_button, bigimagques_skipques_button;
    ImageView challengerAvatarImg, opponentAvatarImg, signature_pad_ImageView, questiotexttospeech_btn, videoquesplaybtn;
    RelativeLayout figureLayout, opponentImgBackground, scrachPadLayout, contentLayout, startLayout,
            quesswipe_refresherlayout, quesswipe_refreshersublayout, header, choiceALayout, choiceBLayout, choiceCLayout, choiceDLayout, choiceELayout, choiceFLayout, choiceGLayout, assessmentimg_layout,
            confirmques_button, quesimag_back, questionmainheader, baseLayout, imgques_confirmques_button,
            offlinemode_layout, bigimgques_confirmques_button, q_camera_image_view_layout,
            q_cameramain_layout, assessment_camera_mainProgress, cameradevider;
    ScaleImageView questionImageView;
    LinearLayout quizmainlayout, fill_layout, choicesLayout, imageques_layout, bigimageques_layout;
    SwipeRefreshLayout quesswipe_refresher;
    ScrollView scrollView;
    OustCheckBox choiceBCheckBox, choiceACheckBox, choiceCCheckBox, choiceDCheckBox, choiceECheckBox,
            choiceFCheckBox, choiceGCheckBox;
    EditText filledittext;
    ProgressBar questionprogress, assessment_camera_progress;
    QuestionOptionImageView imageques_optionA, imageques_optionB, imageques_optionC, imageques_optionD,
            imageques_optionE, imageques_optionF;
    QuestionOptionBigImageView bigimageques_optionA, bigimageques_optionB;
    FrameLayout q_camera_frame;

    RelativeLayout matchfollowing_layout;

    private TextView match_option_a_left_text, match_option_b_left_text, match_option_c_left_text,
            match_option_d_left_text, match_option_a_right_text, match_option_b_right_text, match_option_c_right_text, match_option_d_right_text;
    private TextView questiontetxmatch, draglebel_text;

    private ImageView match_option_a_left_image, match_option_b_left_image, match_option_c_left_image,
            match_option_d_left_image, match_option_a_right_image, match_option_b_right_image,
            match_option_c_right_image, match_option_d_right_image, scrachPadBtn, hideScrachPadBtn;

    RelativeLayout match_option_a_left_layout, mcqQuestionLayout, match_option_b_left_layout, match_option_c_left_layout, match_option_d_left_layout, match_option_a_right_layout, match_option_b_right_layout,
            match_option_c_right_layout, match_option_d_right_layout;

    private CustomScrollView fill_main_scrollview;
    private RelativeLayout fill_blanks_layout;

    private MediaPlayer mediaPlayer;
    private boolean gameSubmitRequestSent = false;

    private QuestionActivityPresenter presenter;
    private long answeredSeconds;
    private CounterClass timer;

    private int decideQuestionType = 0;
    private boolean isVideoPlayingPause = true;
    private String courseId, courseColnId;
    private boolean containCertificate;
    private TextView maxanswer_limittext;
    private EditText longanswer_editetext;
    private LinearLayout longanswer_layout;
    private Button longanswer_submit_btn;

//    ======================================================================
//    media

    private RelativeLayout uploadAudioLayout, audio_layout, record_layout, attach_audio, delete_audio,
            submit_audio, camera_layout, mu_camera_image_view_layout, camera_click, attachphoto_layout,
            delete_photo, submit_photo, camera_frame_Layout, click_photo_layout, video_layout,
            video_view_layout, quesvideoLayout, player_layout, video_click, attach_vedio, delete_video, submit_video,
            video_timer_layout, mediaupload_progressbar;
    private TextView questiontext_media, record_title_text, recording_time, click_text, video_click_text,
            video_timer_text, uploadprogresstext;
    private LinearLayout timer_layout, playrecording_layout;
    private ImageView playrecording, pauserecording, stoprecording, record_icon, preview_ImageView, click_inside_round,
            rotate_camera_layout, camera_back_layout, video_preview, playonpreview;
    private FrameLayout ap_camera_frame;

    private ProgressBar photo_save_progress, uploadprogress;

//    media
//    ======================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            try {
                OustSdkTools.setLocale(QuestionActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            setContentView(R.layout.question);
            initViews();
            initQuestion();
//            OustGATools.getInstance().reportPageViewToGoogle(QuestionActivity.this, "Assessment Question Page");

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void initViews() {

        Log.d(TAG, "initViews: ");

        txtTimerTextView = findViewById(R.id.timer);
        videoquesplaybtn = findViewById(R.id.videoquesplaybtn);
        txtTopicName = findViewById(R.id.txtTopic);
        txtChallengerScore = findViewById(R.id.challengerScore);
        txtOpponentScore = findViewById(R.id.opponentScore);
        txtChoiceA = findViewById(R.id.txtChoiceA);
        txtChoiceB = findViewById(R.id.txtChoiceB);
        txtChoiceC = findViewById(R.id.txtChoiceC);
        txtChoiceD = findViewById(R.id.txtChoiceD);
        skipques_button = findViewById(R.id.skipques_button);
        challengerName = findViewById(R.id.challengerName);
        opponentName = findViewById(R.id.opponentName);
        groupName = findViewById(R.id.txtGroupName);
        challengerAvatarImg = findViewById(R.id.challengerAvatarImg);
        opponentAvatarImg = findViewById(R.id.opponentAvatarImg);
        txtGroupAvatar = findViewById(R.id.txtgroupAvatar);
        questionTextView = findViewById(R.id.questionTV);
        figureLayout = findViewById(R.id.figureLayout);
        opponentImgBackground = findViewById(R.id.opponentImgBackground);
        questionImageView = findViewById(R.id.figure);
        signature_pad_ImageView = findViewById(R.id.signature_pad_bg);
        scrachPadBtn = findViewById(R.id.scrachPadBtn);
        hideScrachPadBtn = (ImageButton) findViewById(R.id.hideScrachPadBtn);
        scrachPadLayout = findViewById(R.id.signature_pad_container);
        contentLayout = findViewById(R.id.contentLayout);
        startLayout = findViewById(R.id.startLayout);
        questTitle = findViewById(R.id.questTitle);
        questCount = findViewById(R.id.questCount);
        quizmainlayout = findViewById(R.id.quizmainlayout);
        quesswipe_refresherlayout = findViewById(R.id.quesswipe_refresherlayout);
        quesswipe_refreshersublayout = findViewById(R.id.quesswipe_refreshersublayout);
        quesswipe_refresher = findViewById(R.id.quesswipe_refresher);
        scrollView = findViewById(R.id.scrollView);
        header = findViewById(R.id.header);
        choiceBCheckBox = findViewById(R.id.choiceBCheckBox);
        choiceACheckBox = findViewById(R.id.choiceACheckBox);
        choiceCCheckBox = findViewById(R.id.choiceCCheckBox);
        choiceDCheckBox = findViewById(R.id.choiceDCheckBox);
        choiceECheckBox = findViewById(R.id.choiceECheckBox);
        choiceFCheckBox = findViewById(R.id.choiceFCheckBox);
        choiceGCheckBox = findViewById(R.id.choiceGCheckBox);
        fill_layout = findViewById(R.id.fill_layout);
        filledittext = findViewById(R.id.filledittext);
        fillconfirm_btn = findViewById(R.id.fillconfirm_btn);
        choiceALayout = findViewById(R.id.choiceALayout);
        choiceBLayout = findViewById(R.id.choiceBLayout);
        choiceCLayout = findViewById(R.id.choiceCLayout);
        choiceDLayout = findViewById(R.id.choiceDLayout);
        choiceELayout = findViewById(R.id.choiceELayout);
        txtChoiceE = findViewById(R.id.txtChoiceE);
        choiceFLayout = findViewById(R.id.choiceFLayout);
        txtChoiceF = findViewById(R.id.txtChoiceF);
        choiceGLayout = findViewById(R.id.choiceGLayout);
        txtChoiceG = findViewById(R.id.txtChoiceG);
        choicesLayout = findViewById(R.id.choicesLayout);
        questionprogress = findViewById(R.id.questionprogress);
        questionprogresstext = findViewById(R.id.questionprogresstext);
        questionprogresslable = findViewById(R.id.questionprogresslable);
        assessmentimg_layout = findViewById(R.id.assessmentimg_layout);
        confirmques_button = findViewById(R.id.confirmques_button);
        quesimag_back = findViewById(R.id.quesimag_back);
        questionmainheader = findViewById(R.id.questionmainheader);
        assessmenttimer = findViewById(R.id.assessmenttimer);
        assessmentscore = findViewById(R.id.assessmentscore);
        maxanswer_limittext = findViewById(R.id.maxanswer_limittext);
        longanswer_editetext = findViewById(R.id.longanswer_editetext);
        longanswer_layout = findViewById(R.id.longanswer_layout);
        longanswer_submit_btn = findViewById(R.id.longanswer_submit_btn);
//        findViewById(R.id.baseLayout)
//        RelativeLayout baseLayout;

        imageques_layout = findViewById(R.id.imageques_layout);
        imageques_optionA = findViewById(R.id.imageques_optionA);
        imageques_optionB = findViewById(R.id.imageques_optionB);
        imageques_optionC = findViewById(R.id.imageques_optionC);
        imageques_optionD = findViewById(R.id.imageques_optionD);
        imageques_optionE = findViewById(R.id.imageques_optionE);
        imageques_optionF = findViewById(R.id.imageques_optionF);
        imagques_skipques_button = findViewById(R.id.imagques_skipques_button);
        imgques_confirmques_button = findViewById(R.id.imgques_confirmques_button);
        offlinemode_layout = findViewById(R.id.offlinemode_layout);
        offlinemode_textview = findViewById(R.id.offlinemode_textview);
        questiotexttospeech_btn = findViewById(R.id.questiotexttospeech_btn);
        bigimageques_layout = findViewById(R.id.bigimageques_layout);
        bigimageques_optionA = findViewById(R.id.bigimageques_optionA);
        bigimageques_optionB = findViewById(R.id.bigimageques_optionB);
        bigimagques_skipques_button = findViewById(R.id.bigimagques_skipques_button);
        bigimgques_confirmques_button = findViewById(R.id.bigimgques_confirmques_button);
        q_camera_image_view_layout = findViewById(R.id.q_camera_image_view_layout);
        q_cameramain_layout = findViewById(R.id.q_cameramain_layout);
        q_camera_frame = findViewById(R.id.q_camera_frame);
        assessment_camera_mainProgress = findViewById(R.id.assessment_camera_mainProgress);
        assessment_camera_progress = findViewById(R.id.assessment_camera_progress);
        cameradevider = findViewById(R.id.cameradevider);
        matchfollowing_layout = findViewById(R.id.matchfollowing_layout);

        match_option_a_left_text = findViewById(R.id.match_option_a_left_text);
        match_option_b_left_text = findViewById(R.id.match_option_b_left_text);
        match_option_c_left_text = findViewById(R.id.match_option_c_left_text);
        match_option_d_left_text = findViewById(R.id.match_option_d_left_text);
        match_option_a_right_text = findViewById(R.id.match_option_a_right_text);
        match_option_b_right_text = findViewById(R.id.match_option_b_right_text);
        match_option_c_right_text = findViewById(R.id.match_option_c_right_text);
        match_option_d_right_text = findViewById(R.id.match_option_d_right_text);
        questiontetxmatch = findViewById(R.id.questiontetxmatch);
        draglebel_text = findViewById(R.id.draglebel_text);

        match_option_a_left_image = findViewById(R.id.match_option_a_left_image);
        match_option_b_left_image = findViewById(R.id.match_option_b_left_image);
        match_option_c_left_image = findViewById(R.id.match_option_c_left_image);
        match_option_d_left_image = findViewById(R.id.match_option_d_left_image);
        match_option_a_right_image = findViewById(R.id.match_option_a_right_image);
        match_option_b_right_image = findViewById(R.id.match_option_b_right_image);
        match_option_c_right_image = findViewById(R.id.match_option_c_right_image);
        match_option_d_right_image = findViewById(R.id.match_option_d_right_image);

        match_option_a_left_layout = findViewById(R.id.match_option_a_left_layout);
        mcqQuestionLayout = findViewById(R.id.mcqQuestionLayout);
        match_option_b_left_layout = findViewById(R.id.match_option_b_left_layout);
        match_option_c_left_layout = findViewById(R.id.match_option_c_left_layout);
        match_option_d_left_layout = findViewById(R.id.match_option_d_left_layout);
        match_option_a_right_layout = findViewById(R.id.match_option_a_right_layout);
        match_option_b_right_layout = findViewById(R.id.match_option_b_right_layout);
        match_option_c_right_layout = findViewById(R.id.match_option_c_right_layout);
        match_option_d_right_layout = findViewById(R.id.match_option_d_right_layout);
        OustSdkTools.setImage(videoquesplaybtn, getResources().getString(R.string.challenge));
        OustSdkTools.setImage(scrachPadBtn, getResources().getString(R.string.learning_expandimage));

        offlinemode_textview.setText(getResources().getString(R.string.offline_mode));
        longanswer_submit_btn.setText(getResources().getString(R.string.submit));
        record_title_text.setText(getResources().getString(R.string.recording_time));
        click_text.setText(getResources().getString(R.string.capture_image_text));
        video_click_text.setText(getResources().getString(R.string.record_video_text));

        fill_main_scrollview = findViewById(R.id.fill_main_scrollview);
        fill_blanks_layout = findViewById(R.id.fill_blanks_layout);

//        =================================================================
//          media
        playonpreview = findViewById(R.id.playonpreview);
        OustSdkTools.setImage(playonpreview, getResources().getString(R.string.challenge));
        uploadAudioLayout = findViewById(R.id.uploadAudioLayout);
        questiontext_media = findViewById(R.id.questiontext_media);
        audio_layout = findViewById(R.id.audio_layout);
        timer_layout = findViewById(R.id.timer_layout);
        record_title_text = findViewById(R.id.record_title_text);
        recording_time = findViewById(R.id.recording_time);
        playrecording_layout = findViewById(R.id.playrecording_layout);
        playrecording = findViewById(R.id.playrecording);
        pauserecording = findViewById(R.id.pauserecording);
        stoprecording = findViewById(R.id.stoprecording);
        record_layout = findViewById(R.id.record_layout);
        attach_audio = findViewById(R.id.attach_audio);
        delete_audio = findViewById(R.id.delete);
        submit_audio = findViewById(R.id.submit);
        record_icon = findViewById(R.id.record_icon);
        camera_layout = findViewById(R.id.camera_layout);
        mu_camera_image_view_layout = findViewById(R.id.mu_camera_image_view_layout);
        click_text = findViewById(R.id.click_text);
        preview_ImageView = findViewById(R.id.preview_ImageView);
        camera_click = findViewById(R.id.camera_click);
        attachphoto_layout = findViewById(R.id.attachphoto_layout);
        delete_photo = findViewById(R.id.delete_photo);
        submit_photo = findViewById(R.id.submit_photo);
        camera_frame_Layout = findViewById(R.id.camera_frame_Layout);
        ap_camera_frame = findViewById(R.id.ap_camera_frame);
        photo_save_progress = findViewById(R.id.photo_save_progress);
        click_photo_layout = findViewById(R.id.click_photo_layout);
        click_inside_round = findViewById(R.id.click_inside_round);
        rotate_camera_layout = findViewById(R.id.rotate_camera_layout);
        camera_back_layout = findViewById(R.id.camera_back_layout);
        video_layout = findViewById(R.id.video_layout);
        video_view_layout = findViewById(R.id.video_view_layout);
        video_click_text = findViewById(R.id.video_click_text);
        quesvideoLayout = findViewById(R.id.quesvideoLayout);
        player_layout = findViewById(R.id.player_layout);
        video_click = findViewById(R.id.video_click);
        attach_vedio = findViewById(R.id.attach_vedio);
        delete_video = findViewById(R.id.delete_video);
        submit_video = findViewById(R.id.submit_video);
        video_preview = findViewById(R.id.video_preview);
        playonpreview = findViewById(R.id.playonpreview);
        video_timer_layout = findViewById(R.id.video_timer_layout);
        video_timer_text = findViewById(R.id.video_timer_text);
        mediaupload_progressbar = findViewById(R.id.mediaupload_progressbar);
        uploadprogress = findViewById(R.id.uploadprogress);
        uploadprogresstext = findViewById(R.id.uploadprogresstext);

        record_layout.setOnClickListener(this);
        attach_audio.setOnClickListener(this);
        delete_audio.setOnClickListener(this);
        submit_audio.setOnClickListener(this);
        playrecording.setOnClickListener(this);
        pauserecording.setOnClickListener(this);
        stoprecording.setOnClickListener(this);

        camera_click.setOnClickListener(this);
        mu_camera_image_view_layout.setOnClickListener(this);
        camera_back_layout.setOnClickListener(this);
        rotate_camera_layout.setOnClickListener(this);
        delete_photo.setOnClickListener(this);
        attachphoto_layout.setOnClickListener(this);
        click_photo_layout.setOnClickListener(this);
        submit_photo.setOnClickListener(this);

        video_view_layout.setOnClickListener(this);
        video_click.setOnClickListener(this);
        attach_vedio.setOnClickListener(this);
        delete_video.setOnClickListener(this);
        playonpreview.setOnClickListener(this);
        submit_video.setOnClickListener(this);
//        media
//        =================================================================
        scrachPadBtn.setOnClickListener(this);
        hideScrachPadBtn.setOnClickListener(this);
        txtChoiceA.setOnClickListener(this);
        txtChoiceB.setOnClickListener(this);
        txtChoiceC.setOnClickListener(this);
        txtChoiceD.setOnClickListener(this);
        txtChoiceE.setOnClickListener(this);
        txtChoiceF.setOnClickListener(this);
        txtChoiceG.setOnClickListener(this);
        skipques_button.setOnClickListener(this);
        confirmques_button.setOnClickListener(this);
        fillconfirm_btn.setOnClickListener(this);
        imagques_skipques_button.setOnClickListener(this);
        bigimagques_skipques_button.setOnClickListener(this);
        imgques_confirmques_button.setOnClickListener(this);
        bigimgques_confirmques_button.setOnClickListener(this);
        questiotexttospeech_btn.setOnClickListener(this);
        scrachPadBtn.setOnClickListener(this);
        hideScrachPadBtn.setOnClickListener(this);
        longanswer_submit_btn.setOnClickListener(this);

        match_option_a_left_layout.setOnTouchListener(this);
        match_option_b_left_layout.setOnTouchListener(this);
        match_option_c_left_layout.setOnTouchListener(this);
        match_option_d_left_layout.setOnTouchListener(this);
        //findViewById(R.id.solution_desc).setOnTouchListener(this);

    }


    @Override
    public void onChange(String status) {
        try {
            boolean netStatus = false;

            if (status == "Connected to Internet with Mobile Data") {
                netStatus = true;
            } else netStatus = status == "Connected to Internet with WIFI";
            if (!netStatus) {
                showAssessmentOfflineMode();
            } else {
                hideAssessmentOfflineMode();
            }
        } catch (Exception e) {
        }
    }

    public void showAssessmentOfflineMode() {
        try {
            offlinemode_layout.setVisibility(View.VISIBLE);
            offlinemode_textview.setText(getResources().getString(R.string.offline_mode));
        } catch (Exception e) {
        }
    }

    public void hideAssessmentOfflineMode() {
        try {
            offlinemode_layout.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }


    public void initQuestion() {
        try {
            OustSdkTools.speakInit();
            //oustAnalyticsTools.reportPageViews(this, getString(R.string.pn_question));
            String blink = OustPreferences.get("BranchLinkReceiver");
            //branchLinkReceiver = OustSdkTools.getInstance().getBranchData(blink);
            if (OustAppState.getInstance().isAssessmentGame()) {
                NetworkUtil.setOnNetworkChangeListener(QuestionActivity.this);
            }
            Intent CallingIntent = getIntent();
            courseId = CallingIntent.getStringExtra("courseId");
            courseColnId = CallingIntent.getStringExtra("courseColnId");
            containCertificate = CallingIntent.getBooleanExtra("containCertificate", false);
            imageString = CallingIntent.getStringExtra("imageString");
            ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
            ActiveGame activeGame = OustSdkTools.getAcceptChallengeData(CallingIntent.getStringExtra("ActiveGame"));
            PlayResponse playResponse = OustAppState.getInstance().getPlayResponse();
            Gson gson = new GsonBuilder().create();
            GamePoints gamePoints = gson.fromJson(CallingIntent.getStringExtra("GamePoints"), GamePoints.class);
            AssessmentPlayResponse assessmentPlayResponce = getAssessmentPlayResp(CallingIntent.getStringExtra("assessmentResp"));
            presenter = new QuestionActivityPresenter(this, gamePoints, activeGame, activeUser, playResponse, assessmentPlayResponce, courseId, courseColnId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private AssessmentPlayResponse getAssessmentPlayResp(String assessmentResp) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(assessmentResp, AssessmentPlayResponse.class);
    }

    private Camera mICamera;
    private CameraPreview mCameraPreview;
    private String imageString;

    private void openfrontCamer() {
        try {
            setStartingLoader();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                q_camera_image_view_layout.setVisibility(View.VISIBLE);
                cameradevider.setVisibility(View.VISIBLE);
                mICamera = getCameraInstance();
                mICamera.setDisplayOrientation(90);
                Camera.Parameters parameters = mICamera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
                Camera.Size size = sizes.get(0);
                for (int i = 0; i < sizes.size(); i++) {
                    if (sizes.get(i).width > size.width) {
                        size = sizes.get(i);
                    }
                }
                parameters.setPictureSize(size.width, size.height);
                mICamera.setParameters(parameters);
                mCameraPreview = new CameraPreview(this, mICamera);
                q_camera_frame.addView(mCameraPreview);
            } else {
//                ActivityCompat.requestPermissions(FormFillingActivity.this, new String[]{Manifest.permission.CAMERA}, RC_PERM_GET_CAMERA);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            // cannot get camera or does not exist
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return camera;
    }

    public void setLongQuestionLayout() {
        choicesLayout.setVisibility(View.GONE);
        bigimageques_layout.setVisibility(View.GONE);
        longanswer_layout.setVisibility(View.VISIBLE);
    }

    public void hideLongQuestionLayout() {
        longanswer_layout.setVisibility(View.GONE);
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;

        // Constructor that obtains context and camera
        @SuppressWarnings("deprecation")
        public CameraPreview(Context context, Camera camera) {
            super(context);
            try {
                this.mCamera = camera;
                this.mSurfaceHolder = this.getHolder();
                this.mSurfaceHolder.addCallback(this);
                this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                // left blank for now
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                // intentionally left blank for a test
            }
        }
    }

    public void setStartingLoader() {
        try {
            ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(assessment_camera_progress, "rotation", 0f, 360f);
            imageViewObjectAnimator.setDuration(3000); // miliseconds
            imageViewObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            imageViewObjectAnimator.start();
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //----------------------------------------------------------------------------------------------------


    public void keepScreenOnSecure() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
        }
    }

    public void setRefresher() {
        try {
            //OustSdkTools.setSnackbarElements(quizmainlayout, QuestionActivity.this);
            quesswipe_refresher.setColorSchemeColors(OustSdkTools.getColorBack(R.color.Orange), OustSdkTools.getColorBack(R.color.LiteGreen));
            quesswipe_refreshersublayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            quesswipe_refresher.post(new Runnable() {
                @Override
                public void run() {
                    quesswipe_refresher.setRefreshing(false);
                    quesswipe_refresherlayout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
        }
    }

    //save variables to show rate us popup after every 5 games
    public void setRateUsPopupVariables() {
        try {
            int noofGame = OustPreferences.getSavedInt("noofGameForRateusPopup");
            if (noofGame <= 10) {
                noofGame++;
                OustPreferences.saveintVar("noofGameForRateusPopup", noofGame);
            }
        } catch (Exception e) {
        }
    }

    //set question progress max value for assessmnet
    public void showQuestionProgressMaxVal(int noofQuesinGame) {
        questionprogress.setMax(noofQuesinGame);
    }


    public void showQuesLoader() {
        try {
            if (quesswipe_refresherlayout != null) {
                quesswipe_refresherlayout.setVisibility(View.VISIBLE);
            }
            if (quesswipe_refresher != null) {
                quesswipe_refresher.setRefreshing(true);
            }
        } catch (Exception e) {
        }
    }

    //methodes to set styles
    public void setFontName() {
        try {
            questionprogresslable.setText(getResources().getString(R.string.question_attempted));
            skipques_button.setText(getResources().getString(R.string.skip));
            imagques_skipques_button.setText(getResources().getString(R.string.skip));
            fillconfirm_btn.setText(getResources().getString(R.string.confirm));
        } catch (Exception e) {
        }
    }

    public void setAnswerChoicesColor() {
        try {
            if (!OustAppState.getInstance().isAssessmentGame()) {
                OustSdkTools.setBtnBackgroud(txtChoiceA, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceB, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceC, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceD, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceE, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceF, R.drawable.btnselecter);
                OustSdkTools.setBtnBackgroud(txtChoiceG, R.drawable.btnselecter);
            }
            OustSdkTools.setBtnBackgroud(skipques_button, R.drawable.skipbtn_selector);
            OustSdkTools.setBtnBackgroud(imagques_skipques_button, R.drawable.skipbtn_selector);
        } catch (Exception e) {
        }
    }

    public void setChallengerName(String name) {
        challengerName.setText(name);
    }

    public void showContactChallengerAvatar(String contactName) {
        try {
            TextView txtChallengerAvatar = findViewById(R.id.txtChallengerAvatar);
            txtChallengerAvatar.setVisibility(View.VISIBLE);
            OustSdkTools.setGroupsIconInActivity(txtChallengerAvatar, contactName);
        } catch (Exception e) {
        }
    }

    public void showNormalChallengerAvatar(String avatar) {
        RelativeLayout challengerAvatar = findViewById(R.id.challengerAvatar);
        challengerAvatar.setVisibility(View.VISIBLE);
        if (OustSdkTools.tempProfile == null) {
            if ((avatar != null) && (!avatar.isEmpty())) {
                Picasso.get().load(avatar).into(challengerAvatarImg);
            }
        } else {
            challengerAvatarImg.setImageBitmap(OustSdkTools.tempProfile);
        }
    }

    public void showGroupAvatar(String groupName) {
        try {
            if (groupName != null) {
                txtGroupAvatar.setVisibility(View.VISIBLE);
                OustSdkTools.setGroupsIconInActivity(txtGroupAvatar, groupName);
            }
        } catch (Exception e) {
        }
    }

    public void showOpponentAvatar(String avatar) {
        try {
            opponentImgBackground.setVisibility(View.VISIBLE);
            if ((avatar != null) && (!avatar.isEmpty())) {
                BitmapDrawable bd = OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image));
                BitmapDrawable bd_loading = OustSdkTools.getImageDrawable(getResources().getString(R.string.profile_image_loading));

                if (avatar.startsWith("http")) {
                    Picasso.get().load(avatar)
                            .placeholder(bd_loading).error(bd)
                            .into(opponentAvatarImg);
                } else {
                    Picasso.get().load(getString(R.string.oust_user_avatar_link) + avatar)
                            .placeholder(bd_loading).error(bd)
                            .into(opponentAvatarImg);
                }
            }
        } catch (Exception e) {
        }
    }

    public void setGroupName(String name) {
        groupName.setVisibility(View.VISIBLE);
        groupName.setText(name);
    }

    public void setOpponenetName(String name) {
        opponentName.setVisibility(View.VISIBLE);
        opponentName.setText(name);
    }

    public void showAssessmentGameTop() {
        questionmainheader.setVisibility(View.GONE);
        assessmentimg_layout.setVisibility(View.VISIBLE);
    }

    public void settxtTopicName(String topicName) {
        txtTopicName.setText(topicName);
    }

    //start game
    public void initializeSound(String audio) {
        try {
            if ((audio != null) && (!audio.isEmpty()) && (OustAppState.getInstance().isAssessmentGame())) {
                playAudioFileOnline(audio);
            } else {
                if ((!OustPreferences.getAppInstallVariable("isttsfileinstalled") || (!OustAppState.getInstance().isAssessmentGame())) ||
                        ((OustAppState.getInstance().isAssessmentGame()) && (!OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled()))) {
                    //mediaPlayer = MediaPlayer.create(this, Uri.fromFile(OustCommonUtils.getAudioFile("quiz_background")));
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer = null;
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setLooping(true);
                    playAudio("quiz_background.mp3", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private boolean musicComplete = true;
    private AnimatorSet scaleDown;

    public void playAudioFileOnline(final String fullPath) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if ((fullPath != null) && (!fullPath.isEmpty()) && (OustAppState.getInstance().isAssessmentGame())) {
                        musicComplete = false;
                        playingOnlineAudio = true;
                        questiotexttospeech_btn.setVisibility(View.VISIBLE);
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(fullPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        startAudioCompleteListener();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        });
    }


    public void startAudioCompleteListener() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleX", 1, 0.75f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleY", 1, 0.75f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);
        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX.setInterpolator(new DecelerateInterpolator());
        scaleDownY.setInterpolator(new DecelerateInterpolator());
        scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                musicComplete = true;
                if (scaleDown != null) {
                    scaleDown.cancel();
                }
            }
        });
    }


    private void startMusic() {
        try {
            if (!OustAppState.getInstance().isSoundDisabled()) {
                if ((mediaPlayer != null) && (!mediaPlayer.isPlaying())) {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
        }
    }

    private void pauseMusic() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        } catch (Exception e) {
        }
    }

    public void startGameOverMusic() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
            mediaPlayer = new MediaPlayer();
            playAudio("game_over.mp3", false);
            if (!OustAppState.getInstance().isSoundDisabled()) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
        }
    }

    public void stopMusicPlay() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
        } catch (Exception e) {
        }
    }

    public void setQutionIndex(int questionIndex, int noofQuesinGame) {
        questTitle.setText(getResources().getString(R.string.question_text) + (questionIndex + 1));
        questCount.setText((questionIndex + 1) + "/" + noofQuesinGame);
        questionprogress.setProgress((questionIndex + 1));
        questionprogresstext.setText((questionIndex + 1) + "/" + noofQuesinGame);
    }

    private String previousQuestionCategory;

    public void showQuestionTitle(final String questionCategory) {
        animateQuestionViewOut(questionCategory);
        animateQuestionTitleIn(questionCategory);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.desideTypeOfQuestionCAtegory();
                animateQuestionTitleOut(questionCategory);
                animateQuestionViewIn(questionCategory);
            }
        }, 1100);
    }

    public void enableAllOption() {
        try {
            txtChoiceA.setEnabled(true);
            txtChoiceB.setEnabled(true);
            txtChoiceC.setEnabled(true);
            txtChoiceD.setEnabled(true);
            txtChoiceE.setEnabled(true);
            txtChoiceF.setEnabled(true);
            txtChoiceG.setEnabled(true);
            confirmques_button.setEnabled(true);
            fillconfirm_btn.setEnabled(true);
            skipques_button.setEnabled(true);
            imagques_skipques_button.setEnabled(true);
            imgques_confirmques_button.setEnabled(true);
            clearAllOption();
        } catch (Exception e) {
        }
    }

    public void disableAllOption() {
        try {
            txtChoiceA.setEnabled(false);
            txtChoiceB.setEnabled(false);
            txtChoiceC.setEnabled(false);
            txtChoiceD.setEnabled(false);
            txtChoiceE.setEnabled(false);
            txtChoiceF.setEnabled(false);
            txtChoiceG.setEnabled(false);
            confirmques_button.setEnabled(false);
            fillconfirm_btn.setEnabled(false);
            skipques_button.setEnabled(false);
            imagques_skipques_button.setEnabled(false);
            imgques_confirmques_button.setEnabled(false);
            selectedNo = 10;
            selectedAnswer = "";
            imageques_optionA.setCheckOption(false);
            imageques_optionB.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            bigimageques_optionA.setCheckOption(false);
            bigimageques_optionB.setCheckOption(false);
        } catch (Exception e) {
        }
    }

    public void startTimer(long quesTime) {
        try {
            timer = new CounterClass(Integer.parseInt(quesTime + getResources().getString(R.string.counterTimer)), getResources().getInteger(R.integer.counterDelay));
            timer.start();
        } catch (Exception e) {
        }
    }

    public void cancleTimer() {
        try {
            if (null != timer)
                timer.cancel();
        } catch (Exception e) {
        }
    }

    public void showTextQuestion(String question) {
        try {
            OustSdkTools.getSpannedContent(question, questiontetxmatch);
            OustSdkTools.getSpannedContent(question, questionTextView);

        } catch (Exception e) {
        }
    }

    public void showImageQuestion(String imgStr) {
        try {
            byte[] decodedString = Base64.decode(imgStr, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            figureLayout.setVisibility(View.VISIBLE);
            questionImageView.setImageBitmap(decodedByte);
        } catch (Exception e) {
        }
    }

    public void hideQuestionImage() {
        figureLayout.setVisibility(View.GONE);
    }

    public void settxtOpponentScore(long score) {
        txtOpponentScore.setText("" + score);
    }

    public void setMRQQuestionLayout(boolean showSkipBtn) {
        hideabcOption();

        choiceACheckBox.setVisibility(View.VISIBLE);
        choiceBCheckBox.setVisibility(View.VISIBLE);
        choiceCCheckBox.setVisibility(View.VISIBLE);
        choiceDCheckBox.setVisibility(View.VISIBLE);
        choiceECheckBox.setVisibility(View.VISIBLE);
        choiceFCheckBox.setVisibility(View.VISIBLE);
        choiceGCheckBox.setVisibility(View.VISIBLE);
        choiceACheckBox.setStatus();
        choiceBCheckBox.setStatus();
        choiceCCheckBox.setStatus();
        choiceDCheckBox.setStatus();
        choiceECheckBox.setStatus();
        choiceGCheckBox.setStatus();
        choiceFCheckBox.setStatus();

        confirmques_button.setVisibility(View.VISIBLE);
        if (showSkipBtn) {
            skipques_button.setVisibility(View.VISIBLE);
        }
        fill_layout.setVisibility(View.GONE);
        imageques_layout.setVisibility(View.GONE);
        bigimageques_layout.setVisibility(View.GONE);
        choicesLayout.setVisibility(View.VISIBLE);
    }

//    public void setFillQuestionLayout(){
//        fill_layout.setVisibility(View.VISIBLE);
//        choicesLayout.setVisibility(View.GONE);
//        imageques_layout.setVisibility(View.GONE);
//        bigimageques_layout.setVisibility(View.GONE);
//        filledittext.setText("");
//        filledittext.setHint(OustStrings.getString("fillEditTxtHint"));
//        try {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            filledittext.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (keyCode == EditorInfo.IME_ACTION_GO || keyCode == EditorInfo.IME_ACTION_DONE ||
//                            event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                        presenter.calculateQuestionScore(filledittext.getText().toString());
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            filledittext.requestFocus();
//        }catch (Exception e){}
//    }


    private List<PointF> fillAnswersPoint;
    private List<String> answerStrs;
    private List<View> fill_views;
    private List<OustFillData> emptyViews;
    private List<OustFillData> answerView;
    private List<String> realAnsStrs;
    private LayoutInflater mInflater;
    public int totalOption = 0;
    private int maxlength = 0;
    private int scrWidth, scrHeight;
    private boolean complete = false;
    private int totalrightattempt = 0;
    private int totalAttempt = 0;

    public void hideFillLayout() {
        fill_main_scrollview.setVisibility(View.GONE);
        fill_blanks_layout.setVisibility(View.GONE);
    }

    private void getWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        scrWidth = metrics.widthPixels;
        scrHeight = metrics.heightPixels;
    }

    public void setFillQuestionLayout(final DTOQuestions questions) {
        try {
            fill_main_scrollview.setVisibility(View.VISIBLE);
            fill_blanks_layout.setVisibility(View.VISIBLE);
            getWidth();
            choicesLayout.setVisibility(View.GONE);
            imageques_layout.setVisibility(View.GONE);
            bigimageques_layout.setVisibility(View.GONE);

            fillAnswersPoint = new ArrayList<>();
            answerStrs = new ArrayList<>();
            fill_views = new ArrayList<>();
            emptyViews = new ArrayList<>();
            answerView = new ArrayList<>();
            realAnsStrs = new ArrayList<>();
            totalOption = 0;
            maxlength = 0;
            complete = false;
            totalrightattempt = 0;
            totalAttempt = 0;

            if (mInflater == null) {
                mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            }
            Spanned s1 = getSpannedContent(questions.getQuestion());
            String fillQuestion = (s1.toString().trim());
            fillQuestion = fillQuestion.replace("_.", "_ .");
            fillQuestion = fillQuestion.replace("_,", "_ ,");
            String[] strings = fillQuestion.split(" ");
            String[] ansStrs = new String[strings.length];
            String[] options = new String[questions.getAnswer().split("#").length];
            options = questions.getAnswer().split("#");
            Collections.shuffle(Arrays.asList(options));

            for (int i = 0; i < options.length; i++) {
                Spanned s3 = getSpannedContent(options[i]);
                answerStrs.add(s3.toString().trim());
                if (maxlength < s3.toString().trim().length()) {
                    maxlength = s3.toString().length();
                }
                totalOption++;
            }

            String dummyStr = "  ";
            for (int j = 0; j < maxlength; j++) {
                dummyStr += "_";
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
                    TextView dummytext = fillTextView1.findViewById(R.id.dummytext);
                    dummytext.setText(dummyStr);

                    TextView fill_mainlayoutb = fillTextView1.findViewById(R.id.fill_mainlayoutb);
                    fill_mainlayoutb.setText(dummyStr);

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
            x = 20;
            y = (int) getResources().getDimension(R.dimen.oustlayout_dimen45);

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
                fillTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!complete) {
                            fill_main_scrollview.setEnableScrolling(false);
                            onViewTouch(v, event, questions);
                            return true;
                        }
                        return false;
                    }
                });
                TextView textView1 = fillTextView.findViewById(R.id.fill_text);
                TextView index_text = fillTextView.findViewById(R.id.index_text);
                index_text.setText("" + j);
                textView1.setTypeface(OustSdkTools.getAvenirLTStdMedium());
                textView1.setText(answerStrs.get(j));
                RelativeLayout fill_answerback = fillTextView.findViewById(R.id.fill_answerback);
                TextView dummytext = fillTextView.findViewById(R.id.dummytext);
                dummytext.setText(dummyStr);
                OustFillData oustFillData = new OustFillData();
                oustFillData.setView(fillTextView);
                oustFillData.setIndex(j);
                answerView.add(oustFillData);
            }
            y += (int) getResources().getDimension(R.dimen.oustlayout_dimen70);
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

//        fill_bottom_text.setText("Drag the words to fill the blanks");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    View movin_view;

    public void onViewTouch(View v, MotionEvent event, final DTOQuestions questions) {
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
                                    isAnsFound = true;
                                    totalAttempt++;
                                    v.setX((int) emptyViews.get(idx).getView().getX());
                                    v.setY((int) emptyViews.get(idx).getView().getY());
                                    v.setOnTouchListener(null);
                                    emptyViews.get(idx).setView(null);
                                    fill_main_scrollview.setEnableScrolling(true);
                                    movin_view = null;
                                    if (questions.getFillAnswers().get(idx).equals(answerStrs.get(id))) {
                                        totalrightattempt++;
                                    }
                                    if (totalAttempt == questions.getFillAnswers().size()) {
                                        presenter.calculateQuestionScoreForFILL(totalrightattempt, answerStrs);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                fill_blanks_layout.removeAllViews();
                                                hideFillLayout();
                                            }
                                        }, 200);
                                    }
                                    break;
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
                            v.setX((int) (StartOrgPT.x));
                            v.setY((int) (StartOrgPT.y));
//                            attemptWrongCount++;
                        }
                        fill_main_scrollview.setEnableScrolling(true);
                        movin_view = null;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setX((int) (StartOrgPT.x));
                        v.setY((int) (StartOrgPT.y));
                        movin_view = null;
                        break;
                }
            }
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

    public void hideAllMatchOption() {
        matchfollowing_layout.setVisibility(View.GONE);
        match_option_a_left_layout.setVisibility(View.GONE);
        match_option_b_left_layout.setVisibility(View.GONE);
        match_option_c_left_layout.setVisibility(View.GONE);
        match_option_d_left_layout.setVisibility(View.GONE);

        match_option_a_right_image.setVisibility(View.GONE);
        match_option_b_right_image.setVisibility(View.GONE);
        match_option_c_right_image.setVisibility(View.GONE);
        match_option_d_right_image.setVisibility(View.GONE);
    }

    public void setMCQQuestionLayout(boolean showSkipBtn) {
        try {
            showabcOption();
            if (OustAppState.getInstance().isAssessmentGame()) {
                confirmques_button.setVisibility(View.VISIBLE);
            }
            choiceACheckBox.setVisibility(View.GONE);
            choiceBCheckBox.setVisibility(View.GONE);
            choiceCCheckBox.setVisibility(View.GONE);
            choiceDCheckBox.setVisibility(View.GONE);
            choiceECheckBox.setVisibility(View.GONE);
            choiceFCheckBox.setVisibility(View.GONE);
            choiceGCheckBox.setVisibility(View.GONE);
            if (showSkipBtn) {
                skipques_button.setVisibility(View.VISIBLE);
            }
            fill_layout.setVisibility(View.GONE);
            imageques_layout.setVisibility(View.GONE);
            bigimageques_layout.setVisibility(View.GONE);
            choicesLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    public void showabcOption() {
        try {
            txtChoiceA.setCompoundDrawablesWithIntrinsicBounds(R.drawable.a, 0, 0, 0);
            txtChoiceB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.b, 0, 0, 0);
            txtChoiceC.setCompoundDrawablesWithIntrinsicBounds(R.drawable.c, 0, 0, 0);
            txtChoiceD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.d, 0, 0, 0);
            txtChoiceE.setCompoundDrawablesWithIntrinsicBounds(R.drawable.e, 0, 0, 0);
            txtChoiceF.setCompoundDrawablesWithIntrinsicBounds(R.drawable.f, 0, 0, 0);
            txtChoiceG.setCompoundDrawablesWithIntrinsicBounds(R.drawable.g, 0, 0, 0);
        } catch (Exception e) {
        }
    }

    public void hideabcOption() {
        try {
            txtChoiceA.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceC.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceD.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceE.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceF.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txtChoiceG.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } catch (Exception e) {
        }
    }

    public void showQuestion_ImageOption() {
        imageques_layout.setVisibility(View.VISIBLE);
        fill_layout.setVisibility(View.GONE);
        choicesLayout.setVisibility(View.GONE);
        bigimageques_layout.setVisibility(View.GONE);
    }

    public void showQuestion_BigImageOption() {
        bigimageques_layout.setVisibility(View.VISIBLE);
        imageques_layout.setVisibility(View.GONE);
        fill_layout.setVisibility(View.GONE);
        choicesLayout.setVisibility(View.GONE);
    }

    public void showAllImageQuestionOptions(DTOQuestions questions1, boolean isMrqQuestion, boolean showSkipButton) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;

        if ((questions1.getImageChoiceA() != null) && (questions1.getImageChoiceA().getImageData() != null)) {
            imageques_optionA.setVisibility(View.VISIBLE);
            imageques_optionA.setMainBiutmap(questions1.getImageChoiceA(), isMrqQuestion, presenter, scrWidth);
        } else {
            imageques_optionA.setVisibility(View.GONE);
        }
        if ((questions1.getImageChoiceB() != null) && (questions1.getImageChoiceB().getImageData() != null)) {
            imageques_optionB.setVisibility(View.VISIBLE);
            imageques_optionB.setMainBiutmap(questions1.getImageChoiceB(), isMrqQuestion, presenter, scrWidth);
        } else {
            imageques_optionB.setVisibility(View.GONE);
        }
        if ((questions1.getImageChoiceC() != null) && (questions1.getImageChoiceC().getImageData() != null)) {
            imageques_optionC.setVisibility(View.VISIBLE);
            imageques_optionC.setMainBiutmap(questions1.getImageChoiceC(), isMrqQuestion, presenter, scrWidth);
        } else {
            imageques_optionC.setVisibility(View.GONE);
        }
        if ((questions1.getImageChoiceD() != null) && (questions1.getImageChoiceD().getImageData() != null)) {
            imageques_optionD.setVisibility(View.VISIBLE);
            imageques_optionD.setMainBiutmap(questions1.getImageChoiceD(), isMrqQuestion, presenter, scrWidth);
        } else {
            imageques_optionD.setVisibility(View.GONE);
        }
        if ((questions1.getImageChoiceE() != null) && (questions1.getImageChoiceE().getImageData() != null)) {
            imageques_optionE.setVisibility(View.VISIBLE);
            imageques_optionE.setMainBiutmap(questions1.getImageChoiceE(), isMrqQuestion, presenter, scrWidth);
        } else {
            imageques_optionE.setVisibility(View.GONE);
        }
        if (isMrqQuestion || OustAppState.getInstance().isAssessmentGame()) {
            imgques_confirmques_button.setVisibility(View.VISIBLE);
        } else {
            imgques_confirmques_button.setVisibility(View.GONE);
        }
        if (showSkipButton) {
            imagques_skipques_button.setVisibility(View.VISIBLE);
        } else {
            imagques_skipques_button.setVisibility(View.GONE);
        }
    }

    public void showAllBigImageQuestionOptions(DTOQuestions questions1, boolean isMrqQuestion, boolean showSkipButton) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int scrWidth = metrics.widthPixels;
        if ((questions1.getImageChoiceA() != null) && (questions1.getImageChoiceA().getImageData() != null)) {
            bigimageques_optionA.setVisibility(View.VISIBLE);
            bigimageques_optionA.setMainBiutmap(questions1.getImageChoiceA(), isMrqQuestion, presenter, scrWidth);
        } else {
            bigimageques_optionA.setVisibility(View.GONE);
        }
        if ((questions1.getImageChoiceB() != null) && (questions1.getImageChoiceB().getImageData() != null)) {
            bigimageques_optionB.setVisibility(View.VISIBLE);
            bigimageques_optionB.setMainBiutmap(questions1.getImageChoiceB(), isMrqQuestion, presenter, scrWidth);
        } else {
            bigimageques_optionB.setVisibility(View.GONE);
        }

        if (isMrqQuestion || OustAppState.getInstance().isAssessmentGame()) {
            bigimgques_confirmques_button.setVisibility(View.VISIBLE);
        } else {
            bigimgques_confirmques_button.setVisibility(View.GONE);
        }
        if (showSkipButton) {
            bigimagques_skipques_button.setVisibility(View.VISIBLE);
        } else {
            bigimagques_skipques_button.setVisibility(View.GONE);
        }
    }

    public void showAllOption(DTOQuestions questions1) {
        try {
            if ((questions1.getA() != null) && (!questions1.getA().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getA(), txtChoiceA);
                choiceALayout.setVisibility(View.VISIBLE);
            } else {
                choiceALayout.setVisibility(View.GONE);
            }
            if ((questions1.getB() != null) && (!questions1.getB().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getB(), txtChoiceB);
                choiceBLayout.setVisibility(View.VISIBLE);
            } else {
                choiceBLayout.setVisibility(View.GONE);
            }
            if ((questions1.getC() != null) && (!questions1.getC().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getC(), txtChoiceC);
                choiceCLayout.setVisibility(View.VISIBLE);
            } else {
                choiceCLayout.setVisibility(View.GONE);
            }
            if ((questions1.getD() != null) && (!questions1.getD().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getD(), txtChoiceD);
                choiceDLayout.setVisibility(View.VISIBLE);
            } else {
                choiceDLayout.setVisibility(View.GONE);
            }
            if ((questions1.getE() != null) && (!questions1.getE().equalsIgnoreCase("dont know")) && (!questions1.getE().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getE(), txtChoiceE);
                choiceELayout.setVisibility(View.VISIBLE);
            } else {
                choiceELayout.setVisibility(View.GONE);
            }
            if ((questions1.getF() != null) && (!questions1.getF().equalsIgnoreCase("dont know")) && (!questions1.getF().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getF(), txtChoiceF);
                choiceFLayout.setVisibility(View.VISIBLE);
            } else {
                choiceFLayout.setVisibility(View.GONE);
            }
            if ((questions1.getG() != null) && (!questions1.getG().equalsIgnoreCase("dont know")) && (!questions1.getG().isEmpty())) {
                OustSdkTools.getSpannedContent(questions1.getG(), txtChoiceG);
                choiceGLayout.setVisibility(View.VISIBLE);
            } else {
                choiceGLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }

    public void resetMatchLayout() {
        match_option_a_left_layout.setVisibility(View.GONE);
        match_option_b_left_layout.setVisibility(View.GONE);
        match_option_c_left_layout.setVisibility(View.GONE);
        match_option_d_left_layout.setVisibility(View.GONE);
        match_option_a_right_layout.setVisibility(View.GONE);
        match_option_b_right_layout.setVisibility(View.GONE);
        match_option_c_right_layout.setVisibility(View.GONE);
        match_option_d_right_layout.setVisibility(View.GONE);
    }

    private List<String> rightChoiceIds;
    private List<String> leftChoiceIds;
    private List<String> answerStrList;
    private List<PointF> leftStartingPoints = new ArrayList<>();

    public void setMatchQuestionLayoutSize() {
        try {
            DownPT = new PointF();
            StartPT = new PointF();
            StartOrgPT = new PointF();
            selectedAns = "";
            touched = false;
            rightChoiceIds = new ArrayList<>();
            leftChoiceIds = new ArrayList<>();
            answerStrList = new ArrayList<>();
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            matchfollowing_layout.setVisibility(View.VISIBLE);
            draglebel_text.setText(getResources().getString(R.string.drag_match));
            draglebel_text.setVisibility(View.VISIBLE);
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

            number0attempted = false;
            number1attempted = false;
            number2attempted = false;
            number3attempted = false;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setMatchData(DTOQuestions questions) {
        try {
            List<DTOMTFColumnData> mtfLeftCol = questions.getMtfLeftCol();
            if ((mtfLeftCol != null) && (mtfLeftCol.size() > 0)) {
                if ((mtfLeftCol.size() > 0) && (mtfLeftCol.get(0) != null) && (mtfLeftCol.get(0).getMtfColData() != null)) {
                    setMatchOption(match_option_a_left_image, match_option_a_left_text, mtfLeftCol.get(0));
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
            List<DTOMTFColumnData> mtfRightCol = questions.getMtfRightCol();
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
            if (leftStartingPoints.size() > 0) {
                if (leftStartingPoints.size() > 0) {
                    match_option_a_left_layout.setX(leftStartingPoints.get(0).x);
                    match_option_a_left_layout.setY(leftStartingPoints.get(0).y);
                }
                if (leftStartingPoints.size() > 1) {
                    match_option_b_left_layout.setX(leftStartingPoints.get(1).x);
                    match_option_b_left_layout.setY(leftStartingPoints.get(1).y);
                }

                if (leftStartingPoints.size() > 2) {
                    match_option_c_left_layout.setX(leftStartingPoints.get(2).x);
                    match_option_c_left_layout.setY(leftStartingPoints.get(2).y);
                }

                if (leftStartingPoints.size() > 3) {
                    match_option_d_left_layout.setX(leftStartingPoints.get(3).x);
                    match_option_d_left_layout.setY(leftStartingPoints.get(3).y);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showOptionWithAnimA(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setMatchOption(ImageView imageView, TextView textView, DTOMTFColumnData mtfColumnData) {
        try {
            if (mtfColumnData.getMtfColMediaType() != null) {
                if (mtfColumnData.getMtfColMediaType().equalsIgnoreCase("IMAGE")) {
                    setImageOption(mtfColumnData.getMtfColData(), imageView);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
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

    public void setImageOptionA(String str, ImageView imgView) {
        try {
            if ((str != null) && (!str.isEmpty())) {
                byte[] imageByte = Base64.decode(str, 0);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                imgView.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    PointF DownPT = new PointF();
    PointF StartPT = new PointF();
    PointF StartOrgPT = new PointF();
    private String selectedAns = "";
    private boolean touched = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (touched) {
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    v.setX((int) (StartPT.x + mv.x));
                    v.setY((int) (StartPT.y + mv.y));
                    StartPT = new PointF(v.getX(), v.getY());
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v.getX() < (match_option_a_right_layout.getX() - 10)) {
                    if (leftChoiceIds.size() > 0 && leftStartingPoints.size() == 0) {
                        leftStartingPoints.add(new PointF(match_option_a_left_layout.getX(), match_option_a_left_layout.getY()));
                    }
                    if (leftChoiceIds.size() > 1 && leftStartingPoints.size() == 1) {
                        leftStartingPoints.add(new PointF(match_option_b_left_layout.getX(), match_option_b_left_layout.getY()));
                    }
                    if (leftChoiceIds.size() > 2 && leftStartingPoints.size() == 2) {
                        leftStartingPoints.add(new PointF(match_option_c_left_layout.getX(), match_option_c_left_layout.getY()));
                    }
                    if (leftChoiceIds.size() > 3 && leftStartingPoints.size() == 3) {
                        leftStartingPoints.add(new PointF(match_option_d_left_layout.getX(), match_option_d_left_layout.getY()));
                    }
                    if (v.getId() == R.id.match_option_a_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(0);
                    } else if (v.getId() == R.id.match_option_b_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(1);
                    } else if (v.getId() == R.id.match_option_c_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(2);
                    } else if (v.getId() == R.id.match_option_d_left_layout) {
                        touched = true;
                        selectedAns = leftChoiceIds.get(3);
                    }

                    v.bringToFront();
                    DownPT.x = event.getX();
                    DownPT.y = event.getY();
                    StartPT = new PointF(v.getX(), v.getY());
                    StartOrgPT = new PointF(v.getX(), v.getY());
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (touched) {
                    float x2 = v.getX() + (match_option_a_right_layout.getHeight() / 2);
                    float y2 = v.getY() + (match_option_a_right_layout.getHeight() / 2);
                    if (x2 > (match_option_a_right_layout.getX())) {
                        if ((!number0attempted) && (y2 > match_option_a_right_layout.getY()) && (y2 < (match_option_a_right_layout.getY() + match_option_a_right_layout.getHeight()))) {
                            optionSelected(v, 0);
                            number0attempted = true;
                        } else if ((!number1attempted) && (y2 > match_option_b_right_layout.getY()) && (y2 < (match_option_b_right_layout.getY() + match_option_b_right_layout.getHeight()))) {
                            optionSelected(v, 1);
                            number1attempted = true;
                        } else if ((!number2attempted) && (y2 > match_option_c_right_layout.getY()) && (y2 < (match_option_c_right_layout.getY() + match_option_c_right_layout.getHeight()))) {
                            optionSelected(v, 2);
                            number2attempted = true;
                        } else if ((!number3attempted) && (y2 > match_option_d_right_layout.getY()) && (y2 < (match_option_d_right_layout.getY() + match_option_d_right_layout.getHeight()))) {
                            optionSelected(v, 3);
                            number3attempted = true;
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

    private boolean number0attempted = false, number1attempted = false, number2attempted = false, number3attempted = false;

    private void optionSelected(View v, int no) {
        if (no == 0) {
            rightAAnswer(v);
            if ((selectedAns != null) && (!selectedAns.isEmpty())) {
                answerStrList.add((selectedAns + "," + rightChoiceIds.get(no)));
            }
        } else if (no == 1) {
            rightBAnswer(v);
            if ((selectedAns != null) && (!selectedAns.isEmpty())) {
                answerStrList.add((selectedAns + "," + rightChoiceIds.get(no)));
            }
        } else if (no == 2) {
            rightCAnswer(v);
            if ((selectedAns != null) && (!selectedAns.isEmpty())) {
                answerStrList.add((selectedAns + "," + rightChoiceIds.get(no)));
            }
        } else if (no == 3) {
            rightDAnswer(v);
            if ((selectedAns != null) && (!selectedAns.isEmpty())) {
                answerStrList.add((selectedAns + "," + rightChoiceIds.get(no)));
            }
        } else {
            wrongAAnswer(v);
        }
        if (((answerStrList.size()) >= rightChoiceIds.size()) || ((answerStrList.size()) >= leftChoiceIds.size())) {
            presenter.calculateQuestionScoreForMatch(answerStrList);
        }
    }

    private void wrongAAnswer(View v) {
        try {
            v.setX((int) (StartOrgPT.x));
            v.setY((int) (StartOrgPT.y));
        } catch (Exception e) {
        }
    }

    private void rightAAnswer(View v) {
        try {
            v.setX((int) (match_option_a_right_layout.getX()));
            v.setY((int) (match_option_a_right_layout.getY()));
        } catch (Exception e) {
        }
    }

    private void rightBAnswer(View v) {
        try {
            v.setX((int) (match_option_b_right_layout.getX()));
            v.setY((int) (match_option_b_right_layout.getY()));
        } catch (Exception e) {
        }
    }

    private void rightCAnswer(View v) {
        try {
            v.setX((int) (match_option_c_right_layout.getX()));
            v.setY((int) (match_option_c_right_layout.getY()));
        } catch (Exception e) {
        }
    }

    private void rightDAnswer(View v) {
        try {
            v.setX((int) (match_option_d_right_layout.getX()));
            v.setY((int) (match_option_d_right_layout.getY()));
        } catch (Exception e) {
        }
    }

    //    ==============================================================================================================
//    Media Record and upload

    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder;
    private static final int RequestPermissionCode = 1;
    private MediaPlayer record_mediaPlayer;
    private File recorded_file = null;
    private CountDownTimer cdt;
    int cnt = 0;
    boolean recording = false, paused = false;
    private int length;
    private boolean attachphotoClicked = false;
    private String filename;
    private boolean timeout = false;
    private boolean isAttachment = false;
    private boolean isMediaPlaying = false;

    String questionCategory;

    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }

    public void setAudioData() {
        AudioSavePathInDevice = null;
        mediaRecorder = null;
        record_mediaPlayer = null;
        recorded_file = null;
        cdt = null;
        cnt = 0;
        recording = false;
        paused = false;
        length = 0;
        attachphotoClicked = false;
        filename = "";
        timeout = false;
        isAttachment = false;
        isMediaPlaying = false;
    }

    //    ===========================================================
//    audio
    public void setAudioMediaLayout() {
        uploadAudioLayout.setVisibility(View.VISIBLE);
        audio_layout.setVisibility(View.VISIBLE);
        delete_audio.setVisibility(View.GONE);
        submit_audio.setVisibility(View.GONE);
        playrecording_layout.setVisibility(View.GONE);
        setAudioData();
    }

    public void clickOnAudioButtons(View view) {
        try {
            int id = view.getId();
            if (id == R.id.record_layout) {
                startAudioRecording();
            } else if (id == R.id.attach_audio) {
                attachphotoClicked = true;
                stopPlayingRecordedAudio();
                deleteRecording();
                playrecording_layout.setVisibility(View.GONE);
                checkForStoragePermission();
            } else if (id == R.id.delete) {
                stopPlayingRecordedAudio();
                deleteRecording();
            } else if (id == R.id.submit) {
                if (OustSdkTools.checkInternetStatus()) {
//                        mediaupload_progressbar.setVisibility(View.VISIBLE);
                    delete_audio.setVisibility(View.GONE);
                    submit_audio.setVisibility(View.GONE);
                    uploadAudioToAWS();
                } else {
                    OustSdkTools.showToast("No internet connection found!");
                }
            } else if (id == R.id.playrecording) {
                if (!isMediaPlaying) {
                    playRecording();
                    isMediaPlaying = true;
                }
            } else if (id == R.id.pauserecording) {
                pauseRecording();
            } else if (id == R.id.stoprecording) {
                stopPlayingRecordedAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void playRecording() {
        try {
            stopQuesAudios();
//            record_layout.setClickable(false);
            if (paused && record_mediaPlayer != null) {
                record_mediaPlayer.seekTo(length);
                record_mediaPlayer.start();
            } else {
                record_mediaPlayer = new MediaPlayer();
                try {
                    record_mediaPlayer.setDataSource(AudioSavePathInDevice);
                    record_mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                record_mediaPlayer.start();
            }
            Toast.makeText(this, "audio Playing", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void pauseRecording() {
        try {
            if (record_mediaPlayer != null) {
                isMediaPlaying = false;
                delete_audio.setClickable(true);
                paused = true;
                record_mediaPlayer.pause();
                length = record_mediaPlayer.getCurrentPosition();
                Toast.makeText(this, "audio paused", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startAudioRecording() {
        try {
            if (checkPermission()) {
                if (!recording) {
                    stopQuesAudios();
                    attach_audio.setClickable(false);
                    playrecording_layout.setVisibility(View.GONE);
                    record_title_text.setVisibility(View.VISIBLE);
                    recording_time.setVisibility(View.VISIBLE);
                    stopPlayingRecordedAudio();
                    deleteRecording();
                    cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (recording) {
                                cnt++;
                                String time = new Integer(cnt).toString();
                                long millis = cnt;
                                int seconds = (int) (millis / 60);
                                int minutes = seconds / 60;
                                seconds = seconds % 60;
                                recording_time.setText(String.format("%02d:%02d", seconds, millis));
                            }
                            if (!recording) {
                                if (cdt != null) {
                                    cdt.cancel();
                                }
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                    recording = true;
                    String fileName = "recordedaudio.mp3";
                    record_icon.setImageResource(R.drawable.ic_pause);
                    try {
                        recorded_file = File.createTempFile(fileName, null, this.getCacheDir());
                        AudioSavePathInDevice = recorded_file.getPath();
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    Toast.makeText(this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    stopRecordingAudio();
                }
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void stopRecordingAudio() {
        recording = false;
        if (cdt != null) {
            cdt.cancel();
            cdt = null;
            cnt = 0;
        }
        record_icon.setImageResource(R.drawable.ic_mic);
        attach_audio.setClickable(true);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder = null;
            Toast.makeText(this, "Recording Completed", Toast.LENGTH_SHORT).show();
        }
        delete_audio.setVisibility(View.VISIBLE);
        submit_audio.setVisibility(View.VISIBLE);
        playrecording_layout.setVisibility(View.VISIBLE);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void stopPlayingRecordedAudio() {
        try {
            if (record_mediaPlayer != null) {
                isMediaPlaying = false;
                paused = false;
                record_mediaPlayer.reset();
                record_mediaPlayer.stop();
                record_mediaPlayer.release();
                record_mediaPlayer = null;
                delete_audio.setClickable(true);
                Toast.makeText(this, "audio stopped", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteRecording() {
        try {
            record_title_text.setVisibility(View.VISIBLE);
            recording_time.setVisibility(View.VISIBLE);
            recording_time.setText("00:00");
            AudioSavePathInDevice = null;
            delete_audio.setVisibility(View.GONE);
            submit_audio.setVisibility(View.GONE);
            if (recorded_file != null && recorded_file.exists() && (!isAttachment)) {
                recorded_file.delete();
                //Toast.makeText(this, "audio deleted", Toast.LENGTH_SHORT).show();
            } else {
                isAttachment = false;
                recorded_file = null;
            }
            record_layout.setClickable(true);
            cnt = 0;
            cdt = null;
            recording = false;
            playrecording_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void MediaRecorderReady() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void stopQuesAudios() {
        OustSdkTools.stopSpeech();
        if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
            mediaPlayer.pause();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (questionCategory != null && questionCategory.equals(QuestionCategory.USR_REC_I)) {
                showAddPicOption();
            } else if (questionCategory != null && questionCategory.equals(QuestionCategory.USR_REC_V)) {
                showAddVideoOption();
            } else if (questionCategory != null && questionCategory.equals(QuestionCategory.USR_REC_A)) {
                showAddAudioOption();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 131);
        }
    }

//    ===================================
//    image

    private Camera mCamera;
    private String image_file = "image_file.png";
    File captured_image_file;
    boolean inPreview = false;
    private int currentCameraId;

    //use these variables to store data temp in case of permission
    private MediaUploadCameraPreview mediaUploadCameraPreview;

    public void setImageLayout() {
        setImageData();
        uploadAudioLayout.setVisibility(View.VISIBLE);
        camera_layout.setVisibility(View.VISIBLE);
        delete_photo.setVisibility(View.GONE);
        submit_photo.setVisibility(View.GONE);
    }

    public void setImageData() {
        mCamera = null;
        captured_image_file = null;
        inPreview = false;
        currentCameraId = 0;
        mediaUploadCameraPreview = null;
        timeout = false;
        attachphotoClicked = false;
        deleteAndResetCamera();
        resetAndRemoveCamera();
    }

    public void clickOnImageButtons(View view) {
        try {
            int id = view.getId();
            if (id == R.id.camera_click || id == R.id.mu_camera_image_view_layout) {
                mu_camera_image_view_layout.setClickable(false);
                camera_back_layout.setVisibility(View.VISIBLE);
                startCamera();
            } else if (id == R.id.camera_back_layout) {
                ap_camera_frame.setVisibility(View.GONE);
                camera_frame_Layout.setVisibility(View.GONE);
                video_view_layout.setClickable(true);
                video_click.setClickable(true);
                camera_click.setClickable(true);
                mu_camera_image_view_layout.setClickable(true);
                resetAndRemoveCamera();
            } else if (id == R.id.rotate_camera_layout) {
                rotateCamera();
            } else if (id == R.id.delete_photo) {
                deleteAndResetCamera();
            } else if (id == R.id.attachphoto_layout) {
                attachphotoClicked = true;
                checkForStoragePermission();
            } else if (id == R.id.click_photo_layout) {
                if (questionCategory != null && questionCategory.equals(QuestionCategory.USR_REC_I)) {
                    OustSdkTools.oustTouchEffect(camera_click, 100);
                    photo_save_progress.setVisibility(View.VISIBLE);
                    click_photo_layout.setEnabled(false);
                    if (mCamera != null) {
                        mCamera.takePicture(null, null, mPicture);
                    }
                } else if (questionCategory.equals(QuestionCategory.USR_REC_V)) {
                    pauseMusic();
                    startCameraAndRecording();
                }
            } else if (id == R.id.submit_photo) {
                if (captured_image_file != null) {
                    camera_click.setClickable(false);
                    mu_camera_image_view_layout.setClickable(false);
                    attachphoto_layout.setClickable(false);
//                        mediaupload_progressbar.setVisibility(View.VISIBLE);
                    uploadImageToAWS();
                } else {
                    OustSdkTools.showToast("No internet connection found!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAndResetCamera() {
        try {
            if (captured_image_file != null) {
                captured_image_file = null;
            }
            mu_camera_image_view_layout.setClickable(true);
            delete_photo.setVisibility(View.GONE);
            submit_photo.setVisibility(View.GONE);
            attachphoto_layout.setVisibility(View.VISIBLE);
            click_text.setText(getResources().getString(R.string.click_open_camera));
            click_text.setVisibility(View.VISIBLE);
            preview_ImageView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void startCamera() {
        try {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                ap_camera_frame.setVisibility(View.GONE);
                camera_frame_Layout.setVisibility(View.GONE);
                mCamera = getMediaCameraInstance();
                mCamera.setDisplayOrientation(90);
                mediaUploadCameraPreview = new MediaUploadCameraPreview(QuestionActivity.this, mCamera);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                DisplayMetrics metrics = this.getResources().getDisplayMetrics();
                int scrWidth = metrics.widthPixels;
                int scrHeight = metrics.heightPixels;
                Camera.Size previewsize = previewSizes.get(0);
                Camera.Size picturesize = pictureSizes.get(0);
                for (int i = 0; i < previewSizes.size(); i++) {
                    if ((scrWidth * scrHeight == previewSizes.get(i).width * previewSizes.get(i).height)
                            || (((scrWidth * scrHeight) % (previewSizes.get(i).width * previewSizes.get(i).height)) == 0)
                            || (((previewSizes.get(i).width * previewSizes.get(i).height) % (scrWidth * scrHeight)) == 0)) {
                        previewsize = previewSizes.get(i);
                    }
                }
                for (int i = 0; i < pictureSizes.size(); i++) {
                    if ((scrWidth * scrHeight == pictureSizes.get(i).width * pictureSizes.get(i).height) || (((scrWidth * scrHeight) % (pictureSizes.get(i).width * pictureSizes.get(i).height)) == 0)) {
                        picturesize = pictureSizes.get(i);
                        break;
                    }
                }
                parameters.setPictureSize(picturesize.width, picturesize.height);
                parameters.setPreviewSize(picturesize.width, picturesize.height);

                mCamera.setParameters(parameters);
                ap_camera_frame.addView(mediaUploadCameraPreview);
                inPreview = true;
                camera_frame_Layout.setVisibility(View.VISIBLE);
                ap_camera_frame.setVisibility(View.VISIBLE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 120);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void rotateCamera() {
        try {
            resetAndRemoveCamera();
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }

            mCamera.setDisplayOrientation(90);

            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int scrWidth = metrics.widthPixels;
            int scrHeight = metrics.heightPixels;
            Camera.Size previewsize = previewSizes.get(0);
            Camera.Size picturesize = pictureSizes.get(0);
            for (int i = 0; i < previewSizes.size(); i++) {
                if ((scrWidth * scrHeight == previewSizes.get(i).width * previewSizes.get(i).height) || (((scrWidth * scrHeight) % (previewSizes.get(i).width * previewSizes.get(i).height)) == 0) || (((previewSizes.get(i).width * previewSizes.get(i).height) % (scrWidth * scrHeight)) == 0)) {
                    previewsize = previewSizes.get(i);
                }
            }
            for (int i = 0; i < pictureSizes.size(); i++) {
                if ((scrWidth * scrHeight == pictureSizes.get(i).width * pictureSizes.get(i).height) || (((scrWidth * scrHeight) % (pictureSizes.get(i).width * pictureSizes.get(i).height)) == 0)) {
                    picturesize = pictureSizes.get(i);
                    break;
                }
            }
            parameters.setPictureSize(picturesize.width, picturesize.height);
            mCamera.setParameters(parameters);

            mediaUploadCameraPreview = new MediaUploadCameraPreview(QuestionActivity.this, mCamera);
            ap_camera_frame.addView(mediaUploadCameraPreview);
            inPreview = true;
            camera_frame_Layout.setVisibility(View.VISIBLE);
            ap_camera_frame.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetAndRemoveCamera() {
        try {
            if (inPreview && mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            ap_camera_frame.removeView(mediaUploadCameraPreview);
            mediaUploadCameraPreview = null;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private Camera getMediaCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                camera_frame_Layout.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.i("aspect ratio", "width " + bitmap.getWidth() + " height " + bitmap.getHeight());
                if ((bitmap.getWidth() > bitmap.getHeight()) || (bitmap.getHeight() == bitmap.getWidth())) {
                    Matrix matrix = new Matrix();
                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        matrix.postRotate(270);
                    } else {
                        matrix.postRotate(90);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                try {
                    Matrix matrix = new Matrix();
                    float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                    Matrix matrixMirrorY = new Matrix();
                    matrixMirrorY.setValues(mirrorY);
                    //matrix.postConcat(matrixMirrorY);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                    bitMapToString(rotatedBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    };

    public void bitMapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            delete_photo.setVisibility(View.VISIBLE);
            submit_photo.setVisibility(View.VISIBLE);
            preview_ImageView.setImageBitmap(bitmap);
            preview_ImageView.setVisibility(View.VISIBLE);
            click_text.setVisibility(View.GONE);
            photo_save_progress.setVisibility(View.GONE);
            click_photo_layout.setEnabled(true);
            resetAndRemoveCamera();
            captured_image_file = File.createTempFile(image_file, null, this.getCacheDir());
            FileOutputStream fos = new FileOutputStream(captured_image_file);
            fos.write(b);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class MediaUploadCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private Camera.Size mPreviewSize;
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;
        private List<Camera.Size> mSupportedPreviewSizes;


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
            if (mPreviewSize != null) {
                float ratio;
                if (mPreviewSize.height >= mPreviewSize.width)
                    ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
                else
                    ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

                // One of these methods should be used, second method squishes preview slightly
                setMeasuredDimension(width, (int) (width * ratio));
                //        setMeasuredDimension((int) (width * ratio), height);
            }
        }

        // Constructor that obtains context and camera
        @SuppressWarnings("deprecation")
        public MediaUploadCameraPreview(Context context, Camera camera) {
            super(context);
            try {
                this.mCamera = camera;
                this.mSurfaceHolder = this.getHolder();
                this.mSurfaceHolder.addCallback(this);
                this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
//                for (Camera.Size str : mSupportedPreviewSizes) {
//                    Log.e("TAG", str.width + "/" + str.height);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                // left blank for now
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            // start preview with new settings
            try {
                Camera.Parameters parameters = mCamera.getParameters();
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    //    ==============================================
//    video
    private File videomediaFile;
    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private PlayerView simpleExoPlayerView;
    private long time = 0;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private MediaRecorder vmediaRecorder;
    private boolean vrecording = false, videoPaused = false;


    public void setVideoLayout() {
        uploadAudioLayout.setVisibility(View.VISIBLE);
        video_layout.setVisibility(View.VISIBLE);
        submit_video.setVisibility(View.GONE);
        delete_video.setVisibility(View.GONE);
        setVideoData();
    }

    public void setVideoData() {
        videomediaFile = null;
        simpleExoPlayer = null;
        time = 0;
        trackSelector = null;
        simpleExoPlayerView = null;
        timeout = false;
        attachphotoClicked = false;
        vmediaRecorder = null;
        vrecording = false;
    }

    public void clickOnVideoIcon(View view) {
        int id = view.getId();
        if (id == R.id.video_view_layout || id == R.id.video_click) {
            video_view_layout.setClickable(false);
            camera_back_layout.setVisibility(View.VISIBLE);
            video_timer_layout.setVisibility(View.VISIBLE);
            click_photo_layout.setEnabled(true);
            deleteVideo();
            startCamera();
        } else if (id == R.id.attach_vedio) {
            deleteVideo();
            attachphotoClicked = true;
            checkForStoragePermission();
        } else if (id == R.id.delete_video) {
            deleteVideo();
        } else if (id == R.id.playonpreview) {
            startVedioPlayer();
        } else if (id == R.id.submit_video) {
            uploadVideoToAWS();
        }

    }

    private void deleteVideo() {
        try {
            if (videomediaFile != null && videomediaFile.exists() && (!isAttachment)) {
                videomediaFile.delete();
            } else {
                videomediaFile = null;
                isAttachment = false;
            }
            delete_video.setVisibility(View.GONE);
            submit_video.setVisibility(View.GONE);
            deleteAndResetVideo();
            removeVideoPlayer();
            videoPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAndResetVideo() {
        try {
            video_click_text.setVisibility(View.VISIBLE);
            video_view_layout.setClickable(true);
            quesvideoLayout.setVisibility(View.GONE);
            video_preview.setVisibility(View.GONE);
            playonpreview.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCameraAndRecording() {
        try {
            if (vrecording && vmediaRecorder != null) {
                // stop recording and release camera
                vmediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                rotate_camera_layout.setVisibility(View.VISIBLE);
                click_inside_round.setImageDrawable(getResources().getDrawable(R.drawable.ic_round));
                DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(this, R.color.RedBorder));
                OustSdkTools.showToast("Recording Stopped");
                video_timer_layout.setVisibility(View.GONE);
                if (cdt != null) {
                    cdt.cancel();
                    video_timer_text.setText("00:00:00");
                    cdt = null;
                    cnt = 0;
                }
                //Exit after saved
                finishVideoRecording();
            } else {
                //Release Camera before MediaRecorder start
                camera_back_layout.setVisibility(View.GONE);
                click_photo_layout.setEnabled(false);
                if (!prepareMediaRecorder()) {
                    // Toast.makeText(this, "Fail in prepareMediaRecorder()! Ended -", Toast.LENGTH_SHORT).show();
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.recording_started));
                    rotate_camera_layout.setVisibility(View.GONE);
                    click_inside_round.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    vmediaRecorder.start();
                    vrecording = true;
                    Handler myHandler = new Handler();
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            click_photo_layout.setEnabled(true);
                            click_photo_layout.setClickable(true);
                        }
                    }, 2000);
                    cdt = new CountDownTimer(Long.MAX_VALUE, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (vrecording) {
                                cnt++;
                                String time = new Integer(cnt).toString();
                                long millis = cnt;
                                int seconds = (int) (millis / 60);
                                int minutes = seconds / 60;
                                seconds = seconds % 60;
                                video_timer_text.setText(String.format("%02d:%02d:%02d", minutes, seconds, millis));
                            }
                        }

                        @Override
                        public void onFinish() {
                        }
                    }.start();
                }
//            myButton.setText("STOP");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void finishVideoRecording() {
        vrecording = false;
        releaseMediaRecorder();
        previewVideo();
    }

    private boolean prepareMediaRecorder() {
        try {

            vmediaRecorder = new MediaRecorder();
            if (mCamera != null) {
                mCamera.unlock();
            } else {
                startCamera();
            }
            vmediaRecorder.setCamera(mCamera);
            if (mediaUploadCameraPreview == null) {
                mediaUploadCameraPreview = new MediaUploadCameraPreview(this.getBaseContext(), mCamera);
            }

            vmediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            vmediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                vmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
                vmediaRecorder.setVideoFrameRate(15);
                vmediaRecorder.setOrientationHint(270);
            } else {
                vmediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
                vmediaRecorder.setOrientationHint(90);
            }


            vmediaRecorder.setOutputFile(getOutputMediaFile().getPath());
//        vmediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
//        vmediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
            vmediaRecorder.setPreviewDisplay(mediaUploadCameraPreview.getHolder().getSurface());

            try {
                vmediaRecorder.prepare();
            } catch (IllegalStateException e) {
                releaseMediaRecorder();
                return false;
            } catch (IOException e) {
                releaseMediaRecorder();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return false;
        }

    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        videomediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        return videomediaFile;
    }

    public void startVedioPlayer() {
        try {
            if (videomediaFile != null) {
                simpleExoPlayerView = new PlayerView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                simpleExoPlayerView.setLayoutParams(new PlayerView.LayoutParams(params));
                simpleExoPlayerView.setBackgroundColor(Color.TRANSPARENT);
                player_layout.addView(simpleExoPlayerView);
                String path = videomediaFile.getPath();
                playonpreview.setVisibility(View.GONE);
                video_preview.setVisibility(View.GONE);
                quesvideoLayout.setVisibility(View.VISIBLE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                OustSdkTools.stopSpeech();
                simpleExoPlayerView.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                simpleExoPlayerView.bringToFront();
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                AdaptiveTrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
                trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                LoadControl loadControl = new DefaultLoadControl();

                /*DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
                boolean preferExtensionDecoders = true;
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        OustSdkApplication.useExtensionRenderers()
                                ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, drmSessionManager, extensionRendererMode);
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(QuestionActivity.this, renderersFactory, trackSelector, loadControl);*/

                RenderersFactory renderersFactory = new DefaultRenderersFactory(QuestionActivity.this);

                MediaSourceFactory mediaSourceFactory = new MediaSourceFactory() {
                    @Override
                    public MediaSourceFactory setDrmSessionManagerProvider(@Nullable DrmSessionManagerProvider drmSessionManagerProvider) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmSessionManager(@Nullable DrmSessionManager drmSessionManager) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmHttpDataSourceFactory(@Nullable HttpDataSource.Factory drmHttpDataSourceFactory) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setDrmUserAgent(@Nullable String userAgent) {
                        return null;
                    }

                    @Override
                    public MediaSourceFactory setLoadErrorHandlingPolicy(@Nullable LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
                        return null;
                    }

                    @Override
                    public int[] getSupportedTypes() {
                        return new int[0];
                    }

                    @Override
                    public MediaSource createMediaSource(MediaItem mediaItem) {
                        return null;
                    }
                };

                AnalyticsCollector analyticsCollector = new AnalyticsCollector(new Clock() {
                    @Override
                    public long currentTimeMillis() {
                        return 0;
                    }

                    @Override
                    public long elapsedRealtime() {
                        return 0;
                    }

                    @Override
                    public long uptimeMillis() {
                        return 0;
                    }

                    @Override
                    public HandlerWrapper createHandler(Looper looper, @Nullable Handler.Callback callback) {
                        return null;
                    }

                    @Override
                    public void onThreadBlocked() {

                    }
                });

                simpleExoPlayer = new SimpleExoPlayer.Builder(QuestionActivity.this, renderersFactory, trackSelector, mediaSourceFactory , loadControl, bandwidthMeter, analyticsCollector).build();

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MOVIE)
                        .build();

                simpleExoPlayer.setAudioAttributes(audioAttributes, true); ///* handleAudioFocus= */

                MediaSource videoSource;
//            Uri videoUri = Uri.parse(path);
                File file = new File(path);
                if (file.exists()) {
                    Uri videoUri = Uri.fromFile(file);
                    Log.e("Player", "" + videoUri);
                    DataSpec dataSpec = new DataSpec(videoUri);
                    final FileDataSource fileDataSource = new FileDataSource();
                    try {
                        fileDataSource.open(dataSpec);
                    } catch (FileDataSource.FileDataSourceException e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    DataSource.Factory factory = new DataSource.Factory() {
                        @Override
                        public DataSource createDataSource() {
                            return fileDataSource;
                        }
                    };

                    videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(fileDataSource.getUri()));
                    //videoSource = new ExtractorMediaSource(fileDataSource.getUri(), factory, extractorsFactory, null, null);

                    simpleExoPlayer.seekTo(time);
                    simpleExoPlayerView.setPlayer(simpleExoPlayer);
                    simpleExoPlayer.setMediaSource(videoSource);
                    simpleExoPlayer.prepare();
                }
                simpleExoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeVideoPlayer() {
        try {
            if (simpleExoPlayerView != null && simpleExoPlayer != null && simpleExoPlayerView.getPlayer() != null) {
//            time = simpleExoPlayerView.getPlayer().getCurrentPosition();
                simpleExoPlayerView.getPlayer().release();
                simpleExoPlayerView.setPlayer(null);
                simpleExoPlayerView.removeAllViews();
                simpleExoPlayerView.setVisibility(View.GONE);
                quesvideoLayout.setVisibility(View.GONE);
                simpleExoPlayer.stop();
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                trackSelector = null;
                simpleExoPlayerView = null;
                Log.e("-------", "onPause");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void releaseMediaRecorder() {
        try {
            if (vmediaRecorder != null) {
                vmediaRecorder.reset();   // clear recorder configuration
                vmediaRecorder.release(); // release the recorder object
                vmediaRecorder = null;
                // lock camera for later use
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void previewVideo() {
        try {
            video_click_text.setVisibility(View.GONE);
            video_view_layout.setClickable(false);
            delete_video.setClickable(true);
            submit_video.setClickable(true);
            submit_video.setVisibility(View.VISIBLE);
            delete_video.setVisibility(View.VISIBLE);
//            play_videorec_layout.setVisibility(View.VISIBLE);
            Bitmap thumbnail = null;
            if (videomediaFile != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(videomediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            video_preview.setVisibility(View.VISIBLE);
            playonpreview.setVisibility(View.VISIBLE);
            if (thumbnail != null)
                video_preview.setImageBitmap(thumbnail);
            ap_camera_frame.setVisibility(View.GONE);
            camera_frame_Layout.setVisibility(View.GONE);
            resetAndRemoveCamera();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//    ====================================


    private final int AUDIO_REQUEST_CODE = 102;

    public void showAddAudioOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, AUDIO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 101;

    private void showAddVideoOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public void showAddPicOption() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            attachphotoClicked = false;
            if (timeout) {
                onTimeOut();
            }

            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    startMusic();
                    camera_click.setClickable(true);
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 1000;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    camera_frame_Layout.setVisibility(View.GONE);
                    bitMapToString(bm);
                }
            } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    isAttachment = true;
                    Uri mMediaUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, mMediaUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    videomediaFile = new File(selectedImagePath);
                    previewVideo();
//                    InputStream inputStream = getActivity().getContentResolver().openInputStream(mMediaUri);
                }
            } else if (requestCode == AUDIO_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    isAttachment = true;
                    Uri mMediaUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, mMediaUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    recorded_file = new File(selectedImagePath);
                    delete_audio.setVisibility(View.VISIBLE);
                    submit_audio.setVisibility(View.VISIBLE);
                    record_title_text.setVisibility(View.GONE);
                    recording_time.setVisibility(View.GONE);
                    playrecording_layout.setVisibility(View.VISIBLE);
                    AudioSavePathInDevice = recorded_file.getPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void uploadAudioToAWS() {
        mediaupload_progressbar.setVisibility(View.VISIBLE);
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
        cancleTimer();
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        TransferUtility transferUtility = new TransferUtility(s3, this);
        if (!recorded_file.exists()) {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
        stopPlayingRecordedAudio();
        final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Audio/" + filename + ".mp3", recorded_file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer.getState())) {
//                    sendResponseForBackend(filename + ".mp3");
                    mediaupload_progressbar.setVisibility(View.GONE);
                    presenter.calculateScoreForMedia(filename + ".mp3");
                    Toast.makeText(QuestionActivity.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                uploadprogress.setProgress((int) percentage);
                uploadprogresstext.setText((int) percentage + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(QuestionActivity.this, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
                mediaupload_progressbar.setVisibility(View.GONE);

            }
        });
    }

    private void uploadImageToAWS() {
        mediaupload_progressbar.setVisibility(View.VISIBLE);
        cancleTimer();
        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
        TransferUtility transferUtility = new TransferUtility(s3, this);
        if ((captured_image_file == null)) {
            Toast.makeText(this, "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
        final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Image/" + filename + ".png", captured_image_file);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED.equals(observer.getState())) {
                    if (captured_image_file != null && captured_image_file.exists()) {
                        captured_image_file.delete();
//                        uploadComplete();\
                        deleteAndResetCamera();
                        mu_camera_image_view_layout.setClickable(false);
                        Toast.makeText(QuestionActivity.this, "" + getResources().getString(R.string.file_complete_msg), Toast.LENGTH_SHORT).show();
//                        mediaupload_progressbar.setVisibility(View.GONE);
                        attachphoto_layout.setVisibility(View.GONE);
                        camera_click.setVisibility(View.GONE);
                        click_text.setText(getResources().getString(R.string.upload_complete_text));
                        mediaupload_progressbar.setVisibility(View.GONE);

                        presenter.calculateScoreForMedia(filename + ".png");
                    }
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;
                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                uploadprogress.setProgress((int) percentage);
                uploadprogresstext.setText((int) percentage + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(QuestionActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                mediaupload_progressbar.setVisibility(View.GONE);
            }
        });

    }

    private void uploadVideoToAWS() {
        mediaupload_progressbar.setVisibility(View.VISIBLE);
        ActiveUser activeUser = OustAppState.getInstance().getActiveUser();
        cancleTimer();
        float length = videomediaFile.length() / 1024f;
        OustSdkTools.showToast("the size is " + length + " kb");

        String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
        String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
        s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
        TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
        if ((videomediaFile == null)) {
            Toast.makeText(OustSdkApplication.getContext(), "File Not Found!", Toast.LENGTH_SHORT).show();
            return;
        }
        removeVideoPlayer();
        filename = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
        final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, "mediaUploadCard/Video/" + filename + ".mp4", videomediaFile);
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED.equals(observer.getState())) {
                    if (videomediaFile != null && videomediaFile.exists() && (!isAttachment)) {
                        videomediaFile.delete();
                    } else {
                        videomediaFile = null;
                        isAttachment = false;
                    }
                    Toast.makeText(QuestionActivity.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                    deleteAndResetVideo();
                    mediaupload_progressbar.setVisibility(View.GONE);
                    delete_video.setVisibility(View.GONE);
                    submit_video.setVisibility(View.GONE);
                    video_click.setVisibility(View.GONE);
                    video_view_layout.setClickable(false);
                    attach_vedio.setVisibility(View.GONE);
                    video_click_text.setText(getResources().getString(R.string.upload_complete_text));
                    presenter.calculateScoreForMedia(filename + ".mp4");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;

                float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                Log.d("percentage", "" + percentage);
                uploadprogress.setProgress((int) percentage);
                uploadprogresstext.setText((int) percentage + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                mediaupload_progressbar.setVisibility(View.GONE);
                Toast.makeText(QuestionActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onTimeOut() {
        delete_audio.setVisibility(View.GONE);
        submit_audio.setVisibility(View.GONE);
        delete_photo.setVisibility(View.GONE);
        submit_photo.setVisibility(View.GONE);
        delete_video.setVisibility(View.GONE);
        submit_video.setVisibility(View.GONE);
        presenter.calculateScoreForMedia("");
    }

    public void disableAllMediaLayout() {

        startMusic();
        stopRecordingAudio();
        stopPlayingRecordedAudio();
        if (vrecording && vmediaRecorder != null) {
            vmediaRecorder.stop();
        }
        releaseMediaRecorder();
        deleteAndResetCamera();
        resetAndRemoveCamera();
        deleteVideo();

        uploadAudioLayout.setVisibility(View.GONE);
        audio_layout.setVisibility(View.GONE);
        delete_audio.setVisibility(View.GONE);
        submit_audio.setVisibility(View.GONE);
        playrecording_layout.setVisibility(View.GONE);
        record_title_text.setVisibility(View.VISIBLE);
        recording_time.setVisibility(View.VISIBLE);
        record_title_text.setText("" + getResources().getString(R.string.recording_time));
        recording_time.setText("00:00");

        video_timer_layout.setVisibility(View.GONE);
        video_timer_text.setText("00:00:00");
        camera_frame_Layout.setVisibility(View.GONE);
        photo_save_progress.setVisibility(View.GONE);
        delete_photo.setVisibility(View.GONE);
        submit_photo.setVisibility(View.GONE);
        preview_ImageView.setImageBitmap(null);
        camera_layout.setVisibility(View.GONE);
        click_text.setText("" + getResources().getString(R.string.capture_image_text));
        click_photo_layout.setClickable(true);

        click_inside_round.setImageDrawable(getResources().getDrawable(R.drawable.ic_round));
        DrawableCompat.setTint(click_inside_round.getDrawable(), ContextCompat.getColor(this, R.color.white_pressed));
        delete_video.setVisibility(View.GONE);
        submit_video.setVisibility(View.GONE);
        playonpreview.setVisibility(View.GONE);
        quesvideoLayout.setVisibility(View.GONE);
        player_layout.removeAllViews();
        video_click_text.setText("" + getResources().getString(R.string.record_video_text));
        video_click_text.setVisibility(View.VISIBLE);
        video_preview.setImageBitmap(null);
        video_layout.setVisibility(View.GONE);
        rotate_camera_layout.setVisibility(View.VISIBLE);

    }


//    =======================================================================================

    private void animateQuestionTitleOut(String questionCategory) {
        try {
            this.previousQuestionCategory = questionCategory;
            android.view.animation.Animation quizin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_quizout);
            quizin.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            this.startLayout.startAnimation(quizin);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void animateQuestionViewIn(final String questionCategory) {
        try {
            android.view.animation.Animation quizin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_quizin);
            if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
                matchfollowing_layout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            } else if ((questionCategory != null) && ((questionCategory == QuestionCategory.USR_REC_A) || (questionCategory == QuestionCategory.USR_REC_V) ||
                    (questionCategory == QuestionCategory.USR_REC_I))) {
                matchfollowing_layout.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                uploadAudioLayout.setVisibility(View.VISIBLE);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                matchfollowing_layout.setVisibility(View.GONE);
            }
            quizin.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
                        matchfollowing_layout.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    } else if ((questionCategory != null) && ((questionCategory.equals(QuestionCategory.USR_REC_A)) || (questionCategory.equals(QuestionCategory.USR_REC_V)) ||
                            (questionCategory.equals(QuestionCategory.USR_REC_I)))) {
                        matchfollowing_layout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.GONE);
                        uploadAudioLayout.setVisibility(View.VISIBLE);
                    } else {
                        scrollView.setVisibility(View.VISIBLE);
                        matchfollowing_layout.setVisibility(View.GONE);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                questiotexttospeech_btn.setVisibility(View.GONE);
                                questiotexttospeech_btn.setAnimation(null);
                            } catch (Exception e) {
                            }
                            presenter.createStringfor_speech();
                        }
                    }, 200);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
                this.matchfollowing_layout.startAnimation(quizin);
            } else if ((questionCategory != null) && ((questionCategory.equals(QuestionCategory.USR_REC_A)) || (questionCategory.equals(QuestionCategory.USR_REC_V)) ||
                    (questionCategory.equals(QuestionCategory.USR_REC_I)))) {
                this.uploadAudioLayout.startAnimation(quizin);
            } else {
                this.scrollView.startAnimation(quizin);
            }

        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void animateQuestionTitleIn(String questionCategory) {
        try {
            android.view.animation.Animation quizin = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.anim_quizin);
            quizin.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    startLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //OustSdkTools.showQuestionLayout(startLayout);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            this.startLayout.startAnimation(quizin);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    private void animateQuestionViewOut(final String questionCategory) {
        try {
            if (conationAudio) {
                stopMusicPlay();
            }
            if (scaleDown != null) {
                scaleDown.cancel();
            }
            conationAudio = false;
            playingOnlineAudio = false;
            musicComplete = true;
            android.view.animation.Animation quizin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_quizout);
            quizin.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if ((previousQuestionCategory != null) && (previousQuestionCategory.equals(QuestionCategory.MATCH))) {
                        matchfollowing_layout.setVisibility(View.GONE);
                    } else if ((previousQuestionCategory != null) && ((previousQuestionCategory.equals(QuestionCategory.USR_REC_A)) || (previousQuestionCategory.equals(QuestionCategory.USR_REC_V)) ||
                            (previousQuestionCategory.equals(QuestionCategory.USR_REC_I)))) {
                        uploadAudioLayout.setVisibility(View.GONE);
                    } else {
                        scrollView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            if ((previousQuestionCategory != null) && (previousQuestionCategory.equals(QuestionCategory.MATCH))) {
                this.matchfollowing_layout.startAnimation(quizin);
            } else if ((previousQuestionCategory != null) && ((previousQuestionCategory.equals(QuestionCategory.USR_REC_A)) || (previousQuestionCategory.equals(QuestionCategory.USR_REC_V)) ||
                    (previousQuestionCategory.equals(QuestionCategory.USR_REC_I)))) {
                this.uploadAudioLayout.startAnimation(quizin);
            } else {
                this.scrollView.startAnimation(quizin);
            }

        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }


    public void showBigIamgeQuestion(String imgStr) {
        try {
            byte[] decodedString = Base64.decode(imgStr, Base64.DEFAULT);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(signature_pad_ImageView);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            signature_pad_ImageView.setImageBitmap(decodedByte);
            mcqQuestionLayout.setVisibility(View.GONE);
            scrachPadLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    public void hideBigIamgeQuestion() {
        try {
            scrachPadLayout.setVisibility(View.GONE);
            mcqQuestionLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //=================================================================

    @Override
    public void onClick(View view) {

        if ((view.getId() == R.id.confirmques_button) || (view.getId() == R.id.fillconfirm_btn)) {
            OustSdkTools.oustTouchEffect(view, 100);
        } else if (view.getId() == R.id.scrachPadBtn) {
            presenter.showBigQuestionImage(null);
        } else if (view.getId() == R.id.hideScrachPadBtn) {
            presenter.hideBigQuestionImage();
        }
        presenter.clickOnOption(view.getId());
        clickOnAudioButtons(view);
        clickOnImageButtons(view);
        clickOnVideoIcon(view);
    }

    private String selectedAnswer = "";

    public void detectOptionClick(int id, String questionType, DTOQuestions questions) {
        try {
            if (id == R.id.txtChoiceA) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceACheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceA.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getA();
                    } else {
                        presenter.calculateQuestionScore(questions.getA());
                    }
                }
            } else if (id == R.id.txtChoiceB) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceBCheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceB.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getB();
                    } else {
                        presenter.calculateQuestionScore(questions.getB());
                    }
                }
            } else if (id == R.id.txtChoiceC) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceCCheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceC.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getC();
                    } else {
                        presenter.calculateQuestionScore(questions.getC());
                    }
                }
            } else if (id == R.id.txtChoiceD) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceDCheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceD.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getD();
                    } else {
                        presenter.calculateQuestionScore(questions.getD());
                    }
                }
            } else if (id == R.id.txtChoiceE) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceECheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceE.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getE();
                    } else {
                        presenter.calculateQuestionScore(questions.getE());
                    }
                }
            } else if (id == R.id.txtChoiceF) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceFCheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceF.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getF();
                    } else {
                        presenter.calculateQuestionScore(questions.getF());
                    }
                }
            } else if (id == R.id.txtChoiceG) {
                if (questionType.equals(QuestionType.MRQ)) {
                    choiceGCheckBox.detectTouch();
                } else {
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        clearAllOption();
                        srollToBottomWithAnimation();
                        txtChoiceG.setBackgroundColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
                        selectedAnswer = questions.getG();
                    } else {
                        presenter.calculateQuestionScore(questions.getG());
                    }
                }
            } else if (id == R.id.skipques_button) {
                presenter.calculateQuestionScore("");
            } else if (id == R.id.confirmques_button) {
                if (questionType.equals(QuestionType.MRQ)) {
                    presenter.calculateQuestionScoreForMRQ(choiceACheckBox.isStatus(), choiceBCheckBox.isStatus(), choiceCCheckBox.isStatus(),
                            choiceDCheckBox.isStatus(), choiceECheckBox.isStatus(), choiceFCheckBox.isStatus(), choiceGCheckBox.isStatus());
                } else {
                    if ((selectedAnswer != null) && (!selectedAnswer.isEmpty())) {
                        OustSdkTools.stopSpeech();
                        presenter.calculateQuestionScore(selectedAnswer);
                    }
                }
            } else if (id == R.id.longanswer_submit_btn) {
                if (longanswer_editetext != null && longanswer_editetext.getText().toString() != null && longanswer_editetext.getText().toString().trim().length() > 0) {
                    presenter.calculateLongAnswerScore(longanswer_editetext.getText().toString());
                } else {
                    OustSdkTools.showToast("" + getResources().getString(R.string.please_enter_response));
                }
            } else if (id == R.id.fillconfirm_btn) {
                if (questionType.equals(QuestionType.FILL)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(filledittext.getWindowToken(), 0);
                    String anserString = filledittext.getText().toString();
                    anserString = anserString.trim();
                    presenter.calculateQuestionScore(anserString);
                }
            } else if (id == R.id.imgques_confirmques_button) {
                if (questionType.equals(QuestionType.MRQ)) {
                    presenter.calculateQuestionScoreForMRQ(imageques_optionA.isCheckOption(), imageques_optionB.isCheckOption(), imageques_optionC.isCheckOption(),
                            imageques_optionD.isCheckOption(), imageques_optionE.isCheckOption(), false, false);
                } else {
                    if ((selectedAnswer != null) && (!selectedAnswer.isEmpty())) {
                        OustSdkTools.stopSpeech();
                        presenter.calculateQuestionScoreForImage(selectedAnswer);
                    }
                }
            } else if (id == R.id.bigimgques_confirmques_button) {
                if (questionType.equals(QuestionType.MRQ)) {
                    presenter.calculateQuestionScoreForMRQ(bigimageques_optionA.isCheckOption(), bigimageques_optionB.isCheckOption(), false, false, false, false, false);
                } else {
                    if ((selectedAnswer != null) && (!selectedAnswer.isEmpty())) {
                        OustSdkTools.stopSpeech();
                        presenter.calculateQuestionScoreForImage(selectedAnswer);
                    }
                }
            } else if (id == R.id.bigimagques_skipques_button) {

            } else if (id == R.id.imagques_skipques_button) {
                presenter.calculateQuestionScore("");
            } else if (id == R.id.questiotexttospeech_btn) {
                if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                    OustPreferences.saveAppInstallVariable("isttssounddisable", false);
                } else {
                    OustPreferences.saveAppInstallVariable("isttssounddisable", true);
                }
                presenter.createStringfor_speech();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private void srollToBottomWithAnimation() {
        ObjectAnimator yTranslate = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getBottom());
        yTranslate.setDuration(500);
        yTranslate.start();
    }

    //set white background for all option
    private void clearAllOption() {
        try {
            if (OustAppState.getInstance().isAssessmentGame()) {
                txtChoiceA.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceB.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceC.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceD.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceE.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceF.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
                txtChoiceG.setBackgroundColor(OustSdkTools.getColorBack(R.color.fulltransparent));
            }
        } catch (Exception e) {
        }
    }

    int selectedNo = 10;

    public void clickOnImageOption(DTOQuestions questions) {
        srollToBottomWithAnimation();
        if (imageques_optionA.isCheckOption() && (selectedNo != 0)) {
            selectedNo = 0;
            imageques_optionB.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceA().getImageFileName();
        } else if (imageques_optionB.isCheckOption() && (selectedNo != 1)) {
            selectedNo = 1;
            imageques_optionA.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceB().getImageFileName();
        } else if (imageques_optionC.isCheckOption() && (selectedNo != 2)) {
            selectedNo = 2;
            imageques_optionA.setCheckOption(false);
            imageques_optionB.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceC().getImageFileName();
        } else if (imageques_optionD.isCheckOption() && (selectedNo != 3)) {
            selectedNo = 3;
            imageques_optionA.setCheckOption(false);
            imageques_optionB.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceD().getImageFileName();
        } else if (imageques_optionE.isCheckOption() && (selectedNo != 4)) {
            selectedNo = 4;
            imageques_optionA.setCheckOption(false);
            imageques_optionB.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionF.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceE().getImageFileName();
        } else if (imageques_optionE.isCheckOption() && (selectedNo != 5)) {
            selectedNo = 5;
            imageques_optionA.setCheckOption(false);
            imageques_optionB.setCheckOption(false);
            imageques_optionC.setCheckOption(false);
            imageques_optionD.setCheckOption(false);
            imageques_optionE.setCheckOption(false);
        }
    }

    public void clickOnBigImageOption(DTOQuestions questions) {
        srollToBottomWithAnimation();
        if (bigimageques_optionA.isCheckOption() && (selectedNo != 0)) {
            selectedNo = 0;
            bigimageques_optionB.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceA().getImageFileName();
        } else if (bigimageques_optionB.isCheckOption() && (selectedNo != 1)) {
            selectedNo = 1;
            bigimageques_optionA.setCheckOption(false);
            selectedAnswer = questions.getImageChoiceB().getImageFileName();
        }
    }


    public void setChallengerScoreText(long score) {
        try {
            txtChallengerScore.setText("" + score);
            assessmentscore.setText("" + score);
        } catch (Exception e) {
        }
    }

    public void setFinalAnimToHideQuestion() {
        try {
            contentLayout.setVisibility(View.GONE);
            //contentLayout.setTranslationX(-1000);
        } catch (Exception e) {
        }
    }


    //==============================================================================================================================================================
    //submit game

    //    public String getBranchLinkForContactChallenge(ActiveGame activeGame,String uniqueId,int gameId){
//        String branchLink="";
//        try {
//            branchLink = OustBranchTools.generateLink(QuestionActivity.this, "Tag1", "Tag2",
//                    "Other", "Share", gameId+"",
//                    activeGame.getChallengerid(), activeGame.getChallengerDisplayName(), activeGame.getChallengerAvatar(),
//                    activeGame.getOpponentid(), activeGame.getOpponentDisplayName(), activeGame.getOpponentAvatar(),
//                    activeGame.getGrade(), activeGame.getSubject(),
//                    activeGame.getModuleId(), uniqueId, "OustMe",
//                    "Study Smarter Rank Higher", "http://oustme.com/images/home/logo.png", "");
//        }catch (Exception e){}
//        return branchLink;
//    }
    public void setGameSubmitRequestSent(boolean gameSubmitRequestSent) {
        this.gameSubmitRequestSent = gameSubmitRequestSent;
    }

    public String getGcmToken() {
        return OustPreferences.get("gcmToken");
    }

    public void saveAndSubmitRequest(SubmitRequest submitRequest) {
        try {
            Gson gson = new GsonBuilder().create();
            if (!OustAppState.getInstance().isAssessmentGame()) {
                OustPreferences.save("lastgamesubmitrequest", gson.toJson(submitRequest));
            }
            showQuesLoader();
            if (!OustSdkTools.checkInternetStatus()) {
                presenter.submitRequestProcessFinish(null);
                return;
            }
            submitScore(submitRequest);
        } catch (Exception e) {
        }
    }

    public void clearSubmitRequestSaved() {
        OustPreferences.save("lastgamesubmitrequest", "");
    }

    public void submitScore(SubmitRequest submitRequest) {

        final SubmitResponse[] submitResponses = {null};
        try {
            gameSubmitRequestSent = true;
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);

            /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, submitGameUrl, parsedJsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitRequestProcessFinish(submitResponses[0]);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    submitResponses[0] = null;
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
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
            jsonObjectRequest.setShouldCache(false);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
            OustSdkApplication.getInstance().addToRequestQueue(jsonObjectRequest, "first");*/

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitRequestProcessFinish(submitResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    submitResponses[0] = null;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        presenter.submitRequestProcessFinish(submitResponse);
    }

    public void showToastErrorMsg(String error) {
        OustSdkTools.showToast("" + error);
    }
    //====================================================================================================================

//    public void disableAcceptContactChallenge() {
//        try {
//            if (branchLinkReceiver != null && branchLinkReceiver.isClickedBranchLink()) {
//                Gson gson = new GsonBuilder().create();
//                branchLinkReceiver.setClickedBranchLink(false);
//                branchLinkReceiver.setRegisteredBranchLink(false);
//                OustPreferences.save("BranchLinkReceiver", gson.toJson(branchLinkReceiver));
//            }
//        }catch (Exception e){
//            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
//        }
//    }

    public void answerProcessFinish(ActiveGame activeGame, ActiveUser activeUser, SubmitRequest submitRequest, GamePoints gamePoints) {
        try {
            //oustRestClient.cancelAllRequests();
            activeGame.setRematch(false);
            OustAppState.getInstance().setHasPopup(false);
            //disableAcceptContactChallenge();
            Gson gson = new GsonBuilder().create();
            Intent intent;
//            if(OustAppEvents.getInstance().isEventGame()){
//                intent = new Intent(QuestionActivity.this, EventResultActivity.class);
//            }else
            if (OustAppState.getInstance().isAssessmentGame()) {
                intent = new Intent(QuestionActivity.this, AssessmentResultActivity.class);
                intent.putExtra("containCertificate", containCertificate);
                if ((courseId != null) && (!courseId.isEmpty())) {
                    intent.putExtra("courseId", courseId);
                }
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    intent.putExtra("courseColnId", courseColnId);
                }
            } else {
                intent = new Intent(QuestionActivity.this, ResultActivity.class);
            }
            intent.putExtra("ActiveUser", gson.toJson(activeUser));
            intent.putExtra("ActiveGame", gson.toJson(activeGame));
            intent.putExtra("GamePoints", gson.toJson(gamePoints));
            intent.putExtra("SubmitRequest", gson.toJson(submitRequest));
            intent.putExtra("ShouldMusicPlay", true);
            startActivity(intent);
            QuestionActivity.this.finish();
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            timeout = true;
            if (!attachphotoClicked) {
                presenter.calculateQuestionScore("");
            }

//            for fill the blank, if on timeout there is a view in the middle bring it back to its actual position
            if (movin_view != null && emptyViews != null && StartOrgPT != null) {
                movin_view.setX(StartOrgPT.x);
                movin_view.setY(StartOrgPT.y);
                for (int idx = 0; idx < emptyViews.size(); idx++) {
                    if (emptyViews.get(idx).getView() != null) {
                        emptyViews.get(idx).getView().findViewById(R.id.fill_mainlayout).setBackgroundColor(getResources().getColor(R.color.fulltransparent));
                    }
                }
            }
            fill_blanks_layout.removeAllViews();
            hideFillLayout();
//
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
            txtTimerTextView.setText(hms);
            assessmenttimer.setText(hms);
        }
    }

    public long getAnswerSecond() {
        return answeredSeconds;
    }
    //===============================================================================================================

    @Override
    public void onBackPressed() {
        try {
            if (presenter != null) {
                presenter.activityBackBtnPressed();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
        }
    }

    public void cancleGame() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopUp(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            if (OustAppState.getInstance().isAssessmentGame()) {
                popupContent.setText(getResources().getString(R.string.assessment_paused_msg));
                popupTitle.setText(getResources().getString(R.string.warning));
            } else {
                popupContent.setText(getResources().getString(R.string.cancel_game_confirmation));
                popupTitle.setText(getResources().getString(R.string.cancel_game));
            }
            OustPreferences.saveAppInstallVariable("isContactPopup", true);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                    cancleTimer();
                    stopMusicPlay();
                    gameSubmitRequestSent = true;
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        presenter.submitGame();
                    } else {
                        QuestionActivity.this.finish();
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    plaayCanclePopup.dismiss();
                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.getMessage() + "");
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    //svae asssessment to firebase

    public void saveAssessmentGame(AssessmentPlayResponse assessmentPlayResponce, ActiveUser activeUser) {
        try {
//            String node="/userAssessmentProgress/"+activeUser.getStudentKey()+"/assessment"+OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            String node = "";
            node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            if ((courseId != null) && (!courseId.isEmpty())) {
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/collection/" + courseColnId + "/course/" + courseId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                } else {
                    node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/course/" + courseId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                }
            } else {
                if ((courseColnId != null) && (!courseColnId.isEmpty())) {
                    node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/collection/" + courseColnId + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
                }
            }
            OustFirebaseTools.getRootRef().child(node).setValue(assessmentPlayResponce);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveComplteAssessmentGame(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + "/completedAssessments/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            OustFirebaseTools.getRootRef().child(node).setValue("true");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void deleteComplteAssessmentGame(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + "/completedAssessments/" + activeUser.getStudentKey() + "/assessment" +
                    OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            OustFirebaseTools.getRootRef().child(node).setValue(null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

//====================================================================================================================================
    //hasVideoQues
    //0 normal flow or start questions after
    //1 has video or audio ques
    //2 back from video and resume
    //0 do nothing
    //5 repate video ques
    //10 error to play or download video

    @Override
    protected void onResume() {
        super.onResume();
        if (decideQuestionType == 1) {
            presenter.showQuestionandOption();
        } else if (decideQuestionType == 2) {
            presenter.resumeVidOrAudioGame();
        } else if (decideQuestionType == 10) {
            QuestionActivity.this.finish();
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    @Override
    protected void onPause() {
        try {
            Log.d(TAG, "onPause: Called !!!");
            super.onPause();
            pauseMusic();
            if (!attachphotoClicked) {
                OustSdkTools.stopSpeech();
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                if (timer != null) {
                    timer.cancel();
                }
                if (!gameSubmitRequestSent) {
                    gameSubmitRequestSent = true;
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        presenter.submitGame();
                    } else {
                        QuestionActivity.this.finish();
                    }
                }
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseMusic();
        OustSdkTools.stopSpeech();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    public void finishActivity() {
        QuestionActivity.this.finish();
    }

    public void gotoVideoPlayAgain() {
        decideQuestionType = 5;
        if (timer != null) {
            timer.cancel();
        }
        pauseMusic();
    }

    public void resumeGame(long maxTime) {
        try {
            //long maximumTime=(maxTime *1000);
            long maximumTime = Integer.parseInt(maxTime + getResources().getString(R.string.counterTimer));
            float f1 = (float) (maximumTime * 0.1);
            maximumTime = maximumTime - (int) f1;
            if ((maximumTime - answeredSeconds) < 0) {
                presenter.calculateQuestionScore("");
            } else {
                timer = new CounterClass((maximumTime - answeredSeconds), getResources().getInteger(R.integer.counterDelay));
                timer.start();
                if (!OustAppState.getInstance().isSoundDisabled()) {
                    if ((mediaPlayer != null) && (!mediaPlayer.isPlaying())) {
                        mediaPlayer.start();
                    }
                }
            }
            decideQuestionType = 4;
        } catch (Exception e) {
        }
    }

    //-----------------------------------------------------
    public void startSpeakQuestion(final String questionStr) {
        try {
            if (OustAppState.getInstance().isAssessmentGame()) {
                if (OustPreferences.getAppInstallVariable("isttsfileinstalled") && (OustAppState.getInstance().getAssessmentFirebaseClass().isTTSEnabled())) {
                    questiotexttospeech_btn.setVisibility(View.VISIBLE);
                    if (OustPreferences.getAppInstallVariable("isttssounddisable")) {
                        questiotexttospeech_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                        speakString(questionStr);
                    } else {
                        questiotexttospeech_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        if (OustSdkTools.textToSpeech != null) {
                            OustSdkTools.stopSpeech();
                            if (scaleDown != null) {
                                scaleDown.cancel();
                            }
                        }
                    }
                } else {
                    questiotexttospeech_btn.setVisibility(View.GONE);
                }
            } else {
                questiotexttospeech_btn.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private boolean conationAudio = false;
    private boolean playingOnlineAudio = false;

    public void clickOnAudioIcon(final String fullPath) {
        if ((fullPath != null) && (!fullPath.isEmpty()) && (OustAppState.getInstance().isAssessmentGame())) {
            conationAudio = true;
            if (scaleDown != null) {
                scaleDown.cancel();
            }
            questiotexttospeech_btn.setVisibility(View.VISIBLE);
            questiotexttospeech_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
            if (musicComplete) {
                if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                    mediaPlayer.stop();
                }
                playingOnlineAudio = true;
                playAudioFileOnline(fullPath);
            } else {
                if (playingOnlineAudio) {
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.pause();
                        questiotexttospeech_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audiooff));
                        if (scaleDown != null) {
                            scaleDown.cancel();
                        }
                    } else if ((mediaPlayer != null)) {
                        mediaPlayer.start();
                        try {
                            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleX", 1, 0.75f);
                            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleY", 1, 0.75f);
                            scaleDownX.setDuration(1000);
                            scaleDownY.setDuration(1000);
                            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                            scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                            scaleDownX.setInterpolator(new DecelerateInterpolator());
                            scaleDownY.setInterpolator(new DecelerateInterpolator());
                            scaleDown = new AnimatorSet();
                            scaleDown.play(scaleDownX).with(scaleDownY);
                            scaleDown.start();
                            questiotexttospeech_btn.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_audio_on));
                        } catch (Exception e) {
                        }
                    }
                } else {
                    if ((mediaPlayer != null) && (mediaPlayer.isPlaying())) {
                        mediaPlayer.stop();
                    }
                    playingOnlineAudio = true;
                    playAudioFileOnline(fullPath);
                }
            }
        }
    }

    private void speakString(String str) {
        try {
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

            float count = str.length() / 20;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleX", 1, 0.75f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(questiotexttospeech_btn, "scaleY", 1, 0.75f);
            scaleDownX.setDuration(1000);
            scaleDownY.setDuration(1000);
            scaleDownX.setRepeatCount((int) count);
            scaleDownY.setRepeatCount((int) count);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setInterpolator(new DecelerateInterpolator());
            scaleDownY.setInterpolator(new DecelerateInterpolator());
            scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);
            scaleDown.start();
        } catch (Exception e) {
        }
    }


//===============================================================================================================================
//s3 public video queestio stream video
//=============================================================================================================================
    //s3 private video, download

    private void playAudio(final String filename, final boolean isPlayingBackground) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    DTOResourceData resourceDataModel = RoomHelper.getResourceDataModel(filename);
                    if (resourceDataModel != null) {
                        String audStr = resourceDataModel.getFile();
                        if ((audStr != null) && (!audStr.isEmpty())) {
                            byte[] audBytes = Base64.decode(audStr, 0);
                            // create temp file that will hold byte array
                            File tempMp3 = File.createTempFile(filename, null, getCacheDir());
                            tempMp3.deleteOnExit();
                            FileOutputStream fos = new FileOutputStream(tempMp3);
                            fos.write(audBytes);
                            fos.close();
                            mediaPlayer.reset();
                            if (isPlayingBackground) {
                                mediaPlayer.setLooping(true);
                            }
                            FileInputStream fis = new FileInputStream(tempMp3);
                            mediaPlayer.setDataSource(fis.getFD());
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });

    }

    public void hideKeyboard() {
        try {
            if (longanswer_editetext != null) {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(longanswer_editetext.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}