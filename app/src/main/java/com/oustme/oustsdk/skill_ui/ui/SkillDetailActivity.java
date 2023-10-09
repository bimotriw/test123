package com.oustme.oustsdk.skill_ui.ui;

import static android.graphics.Typeface.BOLD;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.Manifest;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.chart.charts.CombinedChart;
import com.oustme.oustsdk.chart.components.Legend;
import com.oustme.oustsdk.chart.components.LegendEntry;
import com.oustme.oustsdk.chart.components.XAxis;
import com.oustme.oustsdk.chart.components.YAxis;
import com.oustme.oustsdk.chart.data.BarData;
import com.oustme.oustsdk.chart.data.BarDataSet;
import com.oustme.oustsdk.chart.data.BarEntry;
import com.oustme.oustsdk.chart.data.CombinedData;
import com.oustme.oustsdk.chart.data.Entry;
import com.oustme.oustsdk.chart.data.LineData;
import com.oustme.oustsdk.chart.data.LineDataSet;
import com.oustme.oustsdk.chart.formatter.ValueFormatter;
import com.oustme.oustsdk.chart.highlight.Highlight;
import com.oustme.oustsdk.chart.listener.OnChartValueSelectedListener;
import com.oustme.oustsdk.compression.video.Config;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.skill_ui.FullScreenVideoActivity;
import com.oustme.oustsdk.skill_ui.model.CardCommonDataMap;
import com.oustme.oustsdk.skill_ui.model.CardInfo;
import com.oustme.oustsdk.skill_ui.model.CardMediaList;
import com.oustme.oustsdk.skill_ui.model.SkillAnalyticsResponse;
import com.oustme.oustsdk.skill_ui.model.SkillSubmissionRequest;
import com.oustme.oustsdk.skill_ui.model.SkillSubmisssionDataList;
import com.oustme.oustsdk.skill_ui.model.SoccerSkillLevelDataList;
import com.oustme.oustsdk.skill_ui.model.UserSkillData;
import com.oustme.oustsdk.skill_ui.model.UserSkillLevelAnalyticsDataList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.FilePath;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustGATools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.util.InputFilterMinMax;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class SkillDetailActivity extends AppCompatActivity {

    LinearLayout snackbar_lay;
    Toolbar toolbar_lay;
    FrameLayout toolbar_close_icon;
    FrameLayout leadboard_lay;
    TextView skill_title;
    CircleImageView user_avatar;
    FrameLayout image_card;
    ImageView badge_icon;
    TextView current_level;
    CardView level_lay;
    LinearLayout bs_lay;
    TextView best_score;
    LinearLayout idp_lay;
    TextView idp_target;
    TextView idp_target_date;
    TextView skill_category;
    TextView skill_description;
    FrameLayout media_container;
    ImageView skill_video_thumbnail;
    ImageView skill_bg;
    CardView image_container;
    FrameLayout image_container_frame;
    ImageView skill_image_thumbnail;
    ImageView play_button;
    ImageView play_thumbnail;
    RelativeLayout score_lay;
    TextView user_entered_score;
    FrameLayout submit_button;
    LinearLayout image_bottom_lay;
    LinearLayout history_lay;
    CombinedChart skill_chart;
    TextView view_history;
    ImageView select_video;
    LinearLayout upload_lay;
    ImageView upload_video;
    TextView upload_video_text;
    ImageView preview_image_thumbnail;
    ImageView cancel_video;
    CardView preview_container;
    TextView score;
    TextView level;
    TextView submitted_time;
    RelativeLayout skill_watch_lay;
    ImageView skill_watch;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;
    //End

    String skillIdString, categoryName, currentLevelName = "";
    Long skillId, categoryId;
    ActiveUser activeUser;
    UserSkillData skillData, userSkillData;
    ArrayList<SoccerSkillLevelDataList> soccerSkillLevelDataLists;
    long rangeValue;
    long bestScore;

    //dialog
    Dialog scoredialog;

    int PERMISSION_ALL = 131;
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private File videomediaFile;
    String mediaFileName;

    private int count = 0;
    private int levelMax = 0;
    protected String[] dates;
    long startRange = 1;
    long endRange = 100;
    String lastDate = "";
    long lastScore = 0;
    boolean isStopWatchEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OustSdkTools.setLocale(SkillDetailActivity.this);
        setContentView(R.layout.activity_skill_detail);

        skill_watch = findViewById(R.id.skill_watch);
        score_lay = findViewById(R.id.score_lay);
        play_button = findViewById(R.id.play_button);
        play_thumbnail = findViewById(R.id.play_thumbnail);
        user_entered_score = findViewById(R.id.user_entered_score);
        image_bottom_lay = findViewById(R.id.image_bottom_lay);
        submit_button = findViewById(R.id.submit_button);
        history_lay = findViewById(R.id.history_lay);
        skill_watch_lay = findViewById(R.id.skill_watch_lay);
        submitted_time = findViewById(R.id.submitted_time);
        level = findViewById(R.id.level);
        skill_chart = findViewById(R.id.skill_chart);
        score = findViewById(R.id.score);
        preview_container = findViewById(R.id.preview_container);
        cancel_video = findViewById(R.id.cancel_video);
        preview_image_thumbnail = findViewById(R.id.preview_image_thumbnail);
        upload_video_text = findViewById(R.id.upload_video_text);
        upload_video = findViewById(R.id.upload_video);
        upload_lay = findViewById(R.id.upload_lay);
        select_video = findViewById(R.id.select_video);
        view_history = findViewById(R.id.view_history);
        skill_image_thumbnail = findViewById(R.id.skill_image_thumbnail);
        image_container_frame = findViewById(R.id.image_container_frame);
        image_container = findViewById(R.id.image_container);
        skill_bg = findViewById(R.id.skill_bg);
        skill_video_thumbnail = findViewById(R.id.skill_video_thumbnail);
        media_container = findViewById(R.id.media_container);
        skill_description = findViewById(R.id.skill_description);
        skill_category = findViewById(R.id.skill_category);
        idp_target_date = findViewById(R.id.idp_target_date);
        idp_target = findViewById(R.id.idp_target);
        idp_lay = findViewById(R.id.idp_lay);
        best_score = findViewById(R.id.best_score);
        bs_lay = findViewById(R.id.bs_lay);
        level_lay = findViewById(R.id.level_lay);
        current_level = findViewById(R.id.current_level);
        badge_icon = findViewById(R.id.badge_icon);
        image_card = findViewById(R.id.image_card);
        user_avatar = findViewById(R.id.user_avatar);
        skill_title = findViewById(R.id.skill_title);
        leadboard_lay = findViewById(R.id.leadboard_lay);
        toolbar_lay = findViewById(R.id.toolbar_lay);
        snackbar_lay = findViewById(R.id.snackbar_lay);
        toolbar_close_icon = findViewById(R.id.toolbar_close_icon);

        //Branding loader
        branding_mani_layout = findViewById(R.id.branding_main_layout);
        branding_bg = findViewById(R.id.branding_bg);
        branding_icon = findViewById(R.id.brand_loader);
        branding_percentage = findViewById(R.id.percentage_text);
        //End

        String tenantId = OustPreferences.get("tanentid");

        if (tenantId != null && !tenantId.isEmpty()) {
            File brandingBg = new File(OustSdkApplication.getContext().getFilesDir(),
                    ("oustlearn_" + tenantId.toUpperCase().trim() + "splashScreen"));

            if (brandingBg.exists() && OustPreferences.getAppInstallVariable(IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED)) {
                Picasso.get().load(brandingBg).into(branding_bg);
            } else {
                String tenantBgImage = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/bgImage";
                Picasso.get().load(tenantBgImage).into(branding_bg);
            }

            File brandingLoader = new File(OustSdkApplication.getContext().getFilesDir(), ("oustlearn_" + tenantId.toUpperCase().trim() + "splashIcon"));
            if (brandingLoader.exists()) {
                Picasso.get().load(brandingLoader).into(branding_icon);
            } else {
                String tenantIcon = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + "appImages/splash/org/" + (tenantId.toUpperCase().trim()) + "/android/icon";
                Picasso.get().load(tenantIcon).error(getResources().getDrawable(R.drawable.app_icon)).into(branding_icon);
            }
        }

//        OustGATools.getInstance().reportPageViewToGoogle(SkillDetailActivity.this, "Skill Detail Page");
        submit_button.setEnabled(false);
        String toolbarColorCode = OustPreferences.get("toolbarColorCode");
        toolbar_lay.setBackgroundColor(Color.parseColor(toolbarColorCode));
        OustSdkTools.setSnackbarElements(snackbar_lay, SkillDetailActivity.this);
        isStopWatchEnable = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.isStopWatchEnable);

        Bundle dataBundle = getIntent().getExtras();
        if (dataBundle != null) {
            skillIdString = dataBundle.getString("SkillId");
            categoryName = dataBundle.getString("category", "");
            categoryId = dataBundle.getLong("categoryId", 0);
        }


       /* if(isStopWatchEnable){
            skill_watch_lay.setVisibility(VISIBLE);
        }else{
            skill_watch_lay.setVisibility(GONE);
        }*/
        setButtonColor("#A19F9F");
        initData();
    }

    private void setButtonColor(String color) {
        Drawable actionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
        DrawableCompat.setTint(
                DrawableCompat.wrap(actionDrawable),
                Color.parseColor(color)
        );
        submit_button.setBackground(actionDrawable);

    }

    private void initData() {
        activeUser = OustAppState.getInstance().getActiveUser();

        if (activeUser != null) {
            if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                Picasso.get().load(activeUser.getAvatar())
                        .into(user_avatar);
            }


            if ((skillIdString != null) && (!skillIdString.isEmpty())) {
                skillId = Long.parseLong(skillIdString);

                if (skillId != 0) {
                    if (OustSdkTools.checkInternetStatus()) {
                        getUserSkillData(activeUser);
                    }

                    leadboard_lay.setOnClickListener(v -> {
                        Intent leaderIntent = new Intent(SkillDetailActivity.this, SkillLeaderBoardActivity.class);
                        leaderIntent.putExtra("skillId", skillId);
                        startActivity(leaderIntent);
                    });


                }


            }

        } else {
            SkillDetailActivity.this.finish();
        }

        toolbar_close_icon.setOnClickListener(v -> SkillDetailActivity.this.finish());

        skill_watch_lay.setOnClickListener(v -> {

            Intent watchActivity = new Intent(SkillDetailActivity.this, SkillWatchActivity.class);
            startActivity(watchActivity);

        });
    }


    public void getUserSkillData(ActiveUser activeUser) {
        try {
            String node = "/soccerSkill/soccerSkill" + skillId;
            ValueEventListener skillListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {

                        final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                        try {
                            if (skillProgessData != null) {


                                Gson gson = new Gson();
                                HashMap<String, Object> cardCommonDataMap;
                                CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                if (skillProgessData.get("cardCommonDataMap") != null) {
                                    cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                    if (cardCommonDataMap != null) {
                                        for (String id : cardCommonDataMap.keySet()) {

                                            if (id.equals("attemptCount")) {
                                                cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                            } else if (id.equals("userBestScore")) {
                                                cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                            } else {
                                                try {
                                                    long cardId = Long.parseLong(id);
                                                    if (cardId != 0) {
                                                        final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                        JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                        CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                        cardCommonDataMapObject.setCardInfo(cardInfo);

                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                }
                                            }


                                        }
                                    }


                                }
                                JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                skillData = gson.fromJson(skillElement, UserSkillData.class);
                                skillData.setCardCommonDataMap(cardCommonDataMapObject);
                                setData(null, skillData);
                                String node = "/landingPage/" + activeUser.getStudentKey() + "/soccerSkill/soccerSkill" + skillId;
                                //String node = "/landingPage/54266/soccerSkill/soccerSkill" + skillId;
                                ValueEventListener skillListner = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {

                                            final Map<String, Object> skillProgessData = (Map<String, Object>) dataSnapshot.getValue();
                                            try {
                                                if (skillProgessData != null) {


                                                    Gson gson = new Gson();
                                                    HashMap<String, Object> cardCommonDataMap;
                                                    CardCommonDataMap cardCommonDataMapObject = new CardCommonDataMap();
                                                    if (skillProgessData.get("cardCommonDataMap") != null) {
                                                        cardCommonDataMap = (HashMap<String, Object>) skillProgessData.get("cardCommonDataMap");

                                                        if (cardCommonDataMap != null) {
                                                            for (String id : cardCommonDataMap.keySet()) {

                                                                if (id.equals("attemptCount")) {
                                                                    cardCommonDataMapObject.setAttemptCount((long) cardCommonDataMap.get("attemptCount"));

                                                                } else if (id.equals("userBestScore")) {
                                                                    cardCommonDataMapObject.setUserBestScore((long) cardCommonDataMap.get("userBestScore"));
                                                                } else {
                                                                    try {
                                                                        long cardId = Long.parseLong(id);
                                                                        if (cardId != 0) {
                                                                            final HashMap<String, CardInfo> cardInfoHashMap = (HashMap<String, CardInfo>) cardCommonDataMap.get(id);
                                                                            JsonElement cardElement = gson.toJsonTree(cardInfoHashMap);
                                                                            CardInfo cardInfo = gson.fromJson(cardElement, CardInfo.class);
                                                                            cardCommonDataMapObject.setCardInfo(cardInfo);

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                    }
                                                                }


                                                            }
                                                        }


                                                    }
                                                    JsonElement skillElement = gson.toJsonTree(skillProgessData);
                                                    userSkillData = gson.fromJson(skillElement, UserSkillData.class);
                                                    userSkillData.setCardCommonDataMap(cardCommonDataMapObject);
                                                    setData(userSkillData, skillData);

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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
                                OustFirebaseTools.getRootRef().child(node).keepSynced(true);


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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(skillListner);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setData(UserSkillData userSkillData, UserSkillData skillData) {

        if (skillData != null) {
            String skillNametext = skillData.getSoccerSkillName();
            if (skillNametext.contains("<br/>")) {
                skillNametext = skillNametext.replace("<br/>", "");
            }
            if (skillNametext.contains("<br>")) {
                skillNametext = skillNametext.replace("<br>", "");
            }
            skill_title.setText(skillNametext);
            if (categoryName.contains("<br/>")) {
                categoryName = categoryName.replace("<br/>", "");
            }
            if (categoryName.contains("<br>")) {
                categoryName = categoryName.replace("<br>", "");
            }
            String category = getResources().getString(R.string.category_text) + " : " + categoryName;
            int length = skillNametext.length();
            String skillDes = skillData.getSoccerSkillDescription();
            if (skillDes.contains("<br/>")) {
                skillDes = skillDes.replace("<br/>", "");
            }
            if (skillDes.contains("<br>")) {
                skillDes = skillDes.replace("<br>", "");
            }
            String skillName = skillNametext + " : " + skillDes;
            //    skills_name.setText(skillName);
            Spannable yourScoreSpan = new SpannableString(skillName);
            yourScoreSpan.setSpan(new StyleSpan(BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            skill_description.setText(yourScoreSpan);
            skill_category.setText(category);


            if (skillData.getBgImg() != null && !skillData.getBgImg().isEmpty()) {
                Picasso.get().load(skillData.getBgImg()).error(R.drawable.skill_bg).into(skill_bg);
            }

            String imageUrl = "";
            if (skillData.getThumbnailImg() != null && !skillData.getThumbnailImg().isEmpty()) {
                imageUrl = skillData.getThumbnailImg();
                Picasso.get().load(skillData.getThumbnailImg())
                        .into(skill_video_thumbnail);
                Picasso.get().load(skillData.getThumbnailImg())
                        .into(skill_image_thumbnail);
            }

            long cardId = 0;
            if (skillData.getCardCommonDataMap() != null) {

                if (skillData.getCardCommonDataMap().getCardInfo() != null) {
                    cardId = skillData.getCardCommonDataMap().getCardInfo().getCardId();
                    if (skillData.getCardCommonDataMap().getCardInfo().getCardMediaList() != null
                            && skillData.getCardCommonDataMap().getCardInfo().getCardMediaList().size() != 0) {

                        CardMediaList cardMediaList = skillData.getCardCommonDataMap().getCardInfo().getCardMediaList().get(0);

                        if (cardMediaList.getMediaThumbnail() != null && !cardMediaList.getMediaThumbnail().isEmpty()) {
                            imageUrl = cardMediaList.getMediaThumbnail();
                            Picasso.get().load(cardMediaList.getMediaThumbnail())
                                    .into(skill_video_thumbnail);
                            Picasso.get().load(cardMediaList.getMediaThumbnail())
                                    .into(skill_image_thumbnail);
                        } /*else {
                            //prepareVideo(cardMediaList.getData());
                            skill_video_thumbnail.setVisibility(GONE);
                        }*/
                        if (cardMediaList.getMediaType() != null && (cardMediaList.getMediaType().equalsIgnoreCase("VIDEO") || cardMediaList.getMediaType().contains("VIDEO"))) {
                            if (cardMediaList.getMediaPrivacy() != null && cardMediaList.getMediaPrivacy().equalsIgnoreCase("PRIVATE")) {


                                play_button.setVisibility(VISIBLE);
                                play_thumbnail.setVisibility(VISIBLE);

                                play_button.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Private");
                                    startActivity(video);
                                });
                                media_container.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Private");
                                    startActivity(video);
                                });
                                image_container_frame.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Private");
                                    startActivity(video);
                                });


                            } else {


                                play_button.setVisibility(VISIBLE);
                                play_thumbnail.setVisibility(VISIBLE);


                                play_button.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Public");
                                    startActivity(video);
                                });
                                media_container.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Public");
                                    startActivity(video);
                                });
                                image_container_frame.setOnClickListener(v -> {
                                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                                    video.putExtra("videoName", cardMediaList.getData());
                                    video.putExtra("videoType", "Public");
                                    startActivity(video);
                                });


                                // Toast.makeText(SkillDetailActivity.this, "public", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            play_button.setVisibility(GONE);
                            play_thumbnail.setVisibility(GONE);


                        }

                    }
                } else {
                    play_button.setVisibility(GONE);
                    play_thumbnail.setVisibility(GONE);
                }


            } else {
                play_button.setVisibility(GONE);
                play_thumbnail.setVisibility(GONE);
            }

            ArrayList<Long> rangeArrayList = new ArrayList<>();
            if (skillData.getSoccerSkillLevelDataList() != null && skillData.getSoccerSkillLevelDataList().size() != 0) {

                soccerSkillLevelDataLists = skillData.getSoccerSkillLevelDataList();
                levelMax = soccerSkillLevelDataLists.size();
                OustAppState.getInstance().setSoccerSkillLevelDataList(soccerSkillLevelDataLists);
                int i = 0;
                for (int index = 0; index < skillData.getSoccerSkillLevelDataList().size(); index++) {
                    SoccerSkillLevelDataList soccerSkillLevelDataList = skillData.getSoccerSkillLevelDataList().get(index);
                    rangeArrayList.add(soccerSkillLevelDataList.getScoreStartRange());
                    rangeArrayList.add(soccerSkillLevelDataList.getScoreEndRange());


                }
                if (userSkillData != null) {
                    for (int index = 0; index < skillData.getSoccerSkillLevelDataList().size(); index++) {
                        SoccerSkillLevelDataList soccerSkillLevelDataList = skillData.getSoccerSkillLevelDataList().get(index);


                        if (userSkillData.getUserBestScore() >= soccerSkillLevelDataList.getScoreStartRange() &&
                                userSkillData.getUserBestScore() <= soccerSkillLevelDataList.getScoreEndRange()) {
                            i = index;
                            break;
                        } else if (userSkillData.getUserBestScore() < soccerSkillLevelDataList.getScoreStartRange()) {
                            i = index - 1;
                            break;
                        } else if (userSkillData.getUserBestScore() > soccerSkillLevelDataList.getScoreEndRange()) {
                            i = skillData.getSoccerSkillLevelDataList().size() - 1;

                        }

                    }

                    currentLevelName = skillData.getSoccerSkillLevelDataList().get(i).getLevelName();
                    current_level.setText(skillData.getSoccerSkillLevelDataList().get(i).getLevelName());

                    String levelImage = skillData.getSoccerSkillLevelDataList().get(i).getLevelBannerImg();
                    if (levelImage != null && !levelImage.isEmpty()) {
                        image_card.setVisibility(View.VISIBLE);
                        badge_icon.setVisibility(View.VISIBLE);
                        Picasso.get().load(levelImage).into(badge_icon);
                    } else {
                        image_card.setVisibility(View.GONE);
                    }
                }


            }


            if (rangeArrayList.size() != 0) {
                Collections.sort(rangeArrayList);
                startRange = rangeArrayList.get(0);
                endRange = rangeArrayList.get(rangeArrayList.size() - 1);
            }

            long finalStartRange = startRange;
            long finalEndRange = endRange;
            score_lay.setOnClickListener(v -> scorePopUp(finalStartRange, finalEndRange));
            long finalCardId = cardId;

            submit_button.setOnClickListener(v -> {
                if (rangeValue != 0) {
                    if (videomediaFile != null && (mediaFileName == null || mediaFileName.isEmpty())) {
                        //Toast.makeText(SkillDetailActivity.this, "Please upload media first", Toast.LENGTH_SHORT).show();
                    } else {

                        SkillSubmissionRequest skillSubmissionRequest = new SkillSubmissionRequest();
                        skillSubmissionRequest.setStudentid(activeUser.getStudentid());
                        ArrayList<SkillSubmisssionDataList> skillSubmisssionDataListArrayList = new ArrayList<>();
                        SkillSubmisssionDataList skillSubmisssionDataList = new SkillSubmisssionDataList();
                        skillSubmisssionDataList.setCardId(finalCardId);
                        skillSubmisssionDataList.setUserScore(rangeValue);
                        if (mediaFileName != null && !mediaFileName.isEmpty()) {
                            skillSubmisssionDataList.setUserSubmittedMediaFileName(mediaFileName);
                        }
                        skillSubmisssionDataList.setSoccerSkillId(skillData.getSoccerSkillId());
                        long currentMiliSecs = System.currentTimeMillis();
                       /* Date localDate = new Date(currentMiliSecs);
                        SimpleDateFormat requestDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                        String requestDate = requestDateFormat.format(localDate);
                        requestDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        try{
                            Date utcDate = requestDateFormat.parse(requestDate);

                        }catch (Exception e){
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }*/
                        skillSubmisssionDataList.setSubmissionTimeInMillis(currentMiliSecs);
                        skillSubmisssionDataListArrayList.add(skillSubmisssionDataList);
                        skillSubmissionRequest.setSkillSubmissionDataList(skillSubmisssionDataListArrayList);


                        if (skillData.getSoccerSkillLevelDataList() != null && skillData.getSoccerSkillLevelDataList().size() != 0) {
                            for (SoccerSkillLevelDataList soccerSkillLevelDataList : skillData.getSoccerSkillLevelDataList()) {

                                if (rangeValue >= soccerSkillLevelDataList.getScoreStartRange() && rangeValue <= soccerSkillLevelDataList.getScoreEndRange()) {
                                    skillSubmisssionDataList.setSoccerSkillLevelId(soccerSkillLevelDataList.getSoccerSkillLevelId());
                                    break;
                                }
                            }
                        }

                        if (userSkillData != null) {

                            if (userSkillData.getAddedOn() != null && !userSkillData.getAddedOn().isEmpty()) {

                                if (userSkillData.isEnrolled()) {

                                    try {
                                        apiCallForSubmitScore(skillSubmissionRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }

                                } else {

                                    apiCallForEnroll(skillSubmissionRequest);


                                }

                            } else {

                                apiCallForDistribute(skillSubmissionRequest);


                            }

                        } else {

                            apiCallForDistribute(skillSubmissionRequest);
                        }


                    }

                }
            });


            select_video.setOnClickListener(v -> checkForStoragePermission());
            upload_video.setOnClickListener(v -> {

                if (videomediaFile != null) {

                    branding_mani_layout.setVisibility(VISIBLE);
                    OustSdkTools.createApplicationFolder();
                 /*   File out = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator
                                    + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                                    + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR,
                            "compress.mp4"
                    );
           *//*     if (out.exists())
                    out.delete();*/
                    upload_video.setClickable(false);
                    new VideoCompressor().execute();
                }


            });

            preview_container.setOnClickListener(v -> {
                if (videomediaFile != null) {
                    Intent video = new Intent(SkillDetailActivity.this, FullScreenVideoActivity.class);
                    video.putExtra("videoName", videomediaFile.getAbsolutePath());
                    video.putExtra("videoType", "Private");
                    startActivity(video);
                }

            });

            cancel_video.setOnClickListener(v -> {
                showConfirmToUploadPopup();

            });


            if (skillData.isShowLeaderboard()) {
                leadboard_lay.setVisibility(VISIBLE);
            } else {
                leadboard_lay.setVisibility(GONE);
            }


        }

        if (userSkillData != null) {

            bestScore = userSkillData.getUserBestScore();
            best_score.setText(String.valueOf(userSkillData.getUserBestScore()));

           /* submit_button.setOnClickListener(v -> {

            });*/

            if (userSkillData.isVerifyFlag()) {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_tick_done);
                DrawableCompat.setTint(
                        DrawableCompat.wrap(drawable),
                        Color.parseColor("#ffffff")
                );
                best_score.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            } else {
                if (userSkillData.isRedFlag()) {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_red_flag);
                    best_score.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                }

            }
            if (userSkillData.getCategoryId() != 0) {
                categoryId = userSkillData.getCategoryId();

                apiCallForCategory();


            }

            if (userSkillData.getIdpTargetScore() != 0) {
                idp_target.setText("" + userSkillData.getIdpTargetScore());


                if (userSkillData.getIdpTargetDate() != 0) {

                    Date date = new Date(userSkillData.getIdpTargetDate());
                    Date currentDate = new Date();
                    if (date.getTime() >= currentDate.getTime()) {
                        DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        format.setTimeZone(TimeZone.getDefault());
                        String idpTargetDate = format.format(date);
                        idp_target_date.setText(idpTargetDate);
                        idp_lay.setVisibility(VISIBLE);
                        user_avatar.setVisibility(GONE);
                    }

                }

            } else {
                idp_lay.setVisibility(GONE);
                user_avatar.setVisibility(VISIBLE);
            }


            view_history.setOnClickListener(v -> {
                Intent historyIntent = new Intent(SkillDetailActivity.this, SkillHistoryActivity.class);
                assert skillData != null;
                historyIntent.putExtra("skillName", skillData.getSoccerSkillName());
                historyIntent.putExtra("skillId", skillId);
                startActivity(historyIntent);
            });
            level_lay.setOnClickListener(v -> {
                Intent historyIntent = new Intent(SkillDetailActivity.this, SkillHistoryActivity.class);
                assert skillData != null;
                historyIntent.putExtra("skillName", skillData.getSoccerSkillName());
                historyIntent.putExtra("skillId", skillId);
                startActivity(historyIntent);
            });
            bs_lay.setOnClickListener(v -> {
                Intent historyIntent = new Intent(SkillDetailActivity.this, SkillHistoryActivity.class);
                assert skillData != null;
                historyIntent.putExtra("skillName", skillData.getSoccerSkillName());
                historyIntent.putExtra("skillId", skillId);
                startActivity(historyIntent);
            });

            idp_lay.setOnClickListener(v -> {
                Intent historyIntent = new Intent(SkillDetailActivity.this, IdpTargetActivity.class);
                historyIntent.putExtra("skillName", skillData.getSoccerSkillName());
                historyIntent.putExtra("category", categoryName);
                historyIntent.putExtra("skillId", skillId);
                startActivity(historyIntent);
            });

            apiCallForAnalytics();


        }

    }

    private void scorePopUp(long startRange, long endRange) {
        if (scoredialog != null && scoredialog.isShowing()) {
            scoredialog.dismiss();
        }
        scoredialog = new Dialog(SkillDetailActivity.this, R.style.DialogTheme);
        scoredialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        scoredialog.setContentView(R.layout.score_pop_up);
        Objects.requireNonNull(scoredialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        scoredialog.show();

        final EditText usre_score_enter = scoredialog.findViewById(R.id.usre_score_enter);
        final FrameLayout submit_score = scoredialog.findViewById(R.id.submit_score);
        final ImageView background_submit = scoredialog.findViewById(R.id.background_submit);
        final TextView range = scoredialog.findViewById(R.id.range);


        Drawable drawable = getResources().getDrawable(R.drawable.conf_button_bg);
        background_submit.setImageDrawable(OustSdkTools.drawableColor(drawable));
        usre_score_enter.setFilters(new InputFilter[]{new InputFilterMinMax("" + startRange, "" + endRange)});

        String rangeString = getResources().getString(R.string.range_text) + " : " + startRange + " - " + endRange;
        range.setText(rangeString);

        usre_score_enter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String rangeText = s.toString();
                if (!rangeText.isEmpty()) {

                    try {

                        long rangeNumber = Long.parseLong(rangeText);
                        if (rangeNumber >= startRange && rangeNumber <= endRange) {


                        } else {

                            Toast.makeText(SkillDetailActivity.this, "" + getResources().getString(R.string.score_out_range), Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit_score.setOnClickListener(v -> {

            String value = usre_score_enter.getText().toString();


            if (value.isEmpty()) {
                Toast.makeText(SkillDetailActivity.this, "" + getResources().getString(R.string.enter_your_score_text), Toast.LENGTH_SHORT).show();
            } else {
                try {

                    rangeValue = Long.parseLong(value);
                    if (rangeValue >= startRange && rangeValue <= endRange) {
                        user_entered_score.setText(value);
                        submit_button.setClickable(true);
                        submit_button.setEnabled(true);
                        setButtonColor(OustPreferences.get("toolbarColorCode"));
                        upload_lay.setFocusable(true);

                    } else {
                        Toast.makeText(SkillDetailActivity.this, "" + getResources().getString(R.string.score_out_range), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }


                if (scoredialog.isShowing()) {
                    scoredialog.dismiss();
                }
            }


        });

    }

    private void apiCallForDistribute(SkillSubmissionRequest skillSubmissionRequest) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());
        JsonObjectRequest jsonObjReq = null;
        String skill_distribution_url = OustSdkApplication.getContext().getResources().getString(R.string.distribut_soccer_url);
        skill_distribution_url = skill_distribution_url.replace("{soccerSkillId}", "" + skillData.getSoccerSkillId());
        skill_distribution_url = skill_distribution_url + "?categoryId=" + categoryId + "&categoryName=" + categoryName;
        skill_distribution_url = HttpManager.getAbsoluteUrl(skill_distribution_url);
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("users", jsonArray);
            //final soccer_id = catalog_id;
            ApiCallUtils.doNetworkCall(Request.Method.PUT, skill_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                    new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.optBoolean("success")) {

                                    apiCallForEnroll(skillSubmissionRequest);


                                } else {
                                    if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                        OustSdkTools.showToast(response.optString("error"));
                                    } else {
                                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                    }
                                    //  OustSdkTools.showToast("Not able to dis");
                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast(e.getMessage());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void apiCallForEnroll(SkillSubmissionRequest skillSubmissionRequest) {

        String skill_enroll_url = OustSdkApplication.getContext().getResources().getString(R.string.enrol_soccer_url);
        skill_enroll_url = skill_enroll_url.replace("{soccerSkillId}", "" + skillData.getSoccerSkillId());
        skill_enroll_url = skill_enroll_url.replace("{userId}", activeUser.getStudentid());
        skill_enroll_url = HttpManager.getAbsoluteUrl(skill_enroll_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.PUT, skill_enroll_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {


                                apiCallForSubmitScore(skillSubmissionRequest);

                            } else {
                                if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        } catch (Exception e) {

                            OustSdkTools.showToast("" + e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });

    }

    private void apiCallForCategory() {

        long catalogueId = OustPreferences.getTimeForNotification("catalogueId");
        String skill_category_url = OustSdkApplication.getContext().getResources().getString(R.string.get_category_name);
        skill_category_url = skill_category_url.replace("{catalogueId}", "" + catalogueId);
        skill_category_url = skill_category_url.replace("{categoryId}", "" + categoryId);
        skill_category_url = HttpManager.getAbsoluteUrl(skill_category_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.GET, skill_category_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {


                                if (response.optString("name") != null && !response.optString("name").isEmpty()) {
                                    categoryName = response.optString("name");
                                    String category = getResources().getString(R.string.category_text) + " : " + categoryName;
                                    skill_category.setText(category);
                                }


                            }
                        } catch (Exception e) {

                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

    }

    private void apiCallForSubmitScore(SkillSubmissionRequest skillSubmissionRequest) {

        String skill_submit_url = OustSdkApplication.getContext().getResources().getString(R.string.skill_submit_url);
        skill_submit_url = skill_submit_url.replace("{userId}", activeUser.getStudentid());
        skill_submit_url = HttpManager.getAbsoluteUrl(skill_submit_url);
        JSONObject jsonParams = skillSubmissionRequest.toJSON();

        Date currentDate = new Date();
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("ddMMM", Locale.getDefault());
        String currentDateString = displayDateFormat.format(currentDate);
        if (currentDateString.equalsIgnoreCase(lastDate)) {
            if (rangeValue > lastScore) {
                ApiCallUtils.doNetworkCall(Request.Method.PUT, skill_submit_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                        new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.optBoolean("success")) {

                                        if (rangeValue > bestScore) {

                                            boolean isThere = false;

                                            for (SoccerSkillLevelDataList soccerSkillLevelDataList : skillData.getSoccerSkillLevelDataList()) {

                                                if (rangeValue >= soccerSkillLevelDataList.getScoreStartRange() && rangeValue <= soccerSkillLevelDataList.getScoreEndRange()) {
                                                    isThere = true;
                                                    if (currentLevelName == null || !currentLevelName.equalsIgnoreCase(soccerSkillLevelDataList.getLevelName())) {
                                                        levelPopUp(true, soccerSkillLevelDataList.getLevelBannerImg(), soccerSkillLevelDataList.getLevelName());
                                                    } else {
                                                        levelPopUp(false, soccerSkillLevelDataList.getLevelBannerImg(), soccerSkillLevelDataList.getLevelName());
                                                    }

                                                    break;


                                                }
                                            }

                                            if (!isThere) {
                                                levelPopUp(false, null, null);

                                            }

                                        } else {
                                            levelPopUp(false, null, null);
                                        }


                                    } else {
                                        if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                            OustSdkTools.showToast(response.optString("error"));
                                        } else {
                                            OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                        }
                                        final Handler handler = new Handler();
                                        handler.postDelayed(() -> intentData(), 4000);

                                    }
                                } catch (Exception e) {
                                    OustSdkTools.showToast("" + e.getMessage());
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> intentData(), 4000);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                final Handler handler = new Handler();
                                handler.postDelayed(() -> intentData(), 4000);
                            }
                        });
            } else {
                commonPopUp(false, false);
            }
        } else {
            ApiCallUtils.doNetworkCall(Request.Method.PUT, skill_submit_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                    new ApiCallUtils.NetworkCallback() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.optBoolean("success")) {

                                    if (rangeValue > bestScore) {

                                        boolean isThere = false;

                                        for (SoccerSkillLevelDataList soccerSkillLevelDataList : skillData.getSoccerSkillLevelDataList()) {

                                            if (rangeValue >= soccerSkillLevelDataList.getScoreStartRange() && rangeValue <= soccerSkillLevelDataList.getScoreEndRange()) {
                                                isThere = true;
                                                if (currentLevelName == null || !currentLevelName.equalsIgnoreCase(soccerSkillLevelDataList.getLevelName())) {
                                                    levelPopUp(true, soccerSkillLevelDataList.getLevelBannerImg(), soccerSkillLevelDataList.getLevelName());
                                                } else {
                                                    levelPopUp(false, soccerSkillLevelDataList.getLevelBannerImg(), soccerSkillLevelDataList.getLevelName());
                                                }

                                                break;


                                            }
                                        }

                                        if (!isThere) {
                                            levelPopUp(false, null, null);

                                        }

                                    } else {
                                        levelPopUp(false, null, null);
                                    }


                                } else {
                                    if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                        OustSdkTools.showToast(response.optString("error"));
                                    } else {
                                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                    }
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> intentData(), 4000);

                                }
                            } catch (Exception e) {
                                OustSdkTools.showToast("" + e.getMessage());
                                final Handler handler = new Handler();
                                handler.postDelayed(() -> intentData(), 4000);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> intentData(), 4000);
                        }
                    });
        }


    }

    private void apiCallForAnalytics() {

        String skill_analytics_url = OustSdkApplication.getContext().getResources().getString(R.string.skill_analytics_url);
        skill_analytics_url = skill_analytics_url.replace("{userId}", activeUser.getStudentid());
        skill_analytics_url = skill_analytics_url.replace("{soccerSkillId}", "" + skillId);
        skill_analytics_url = skill_analytics_url + "?analyticsType=OVERALL";
        skill_analytics_url = HttpManager.getAbsoluteUrl(skill_analytics_url);
        JSONObject jsonParams = new JSONObject();

        ApiCallUtils.doNetworkCall(Request.Method.GET, skill_analytics_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams),
                new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optBoolean("success")) {


                                SkillAnalyticsResponse skillAnalyticsResponse = new Gson().fromJson(response.toString(), SkillAnalyticsResponse.class);
                                if (skillAnalyticsResponse != null) {
                                    if (skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList() != null
                                            && skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().size() != 0) {


                                        if (skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0) != null &&
                                                skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList() != null
                                                && skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList().size() != 0) {


                                            ArrayList<UserSkillLevelAnalyticsDataList> levelAnalyticsDataList = skillAnalyticsResponse.getUserSoccerSkillAnalyticsDataList().get(0).getUserSkillLevelAnalyticsDataList();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                               /* Collections.sort(userSkillDataArrayList,
                                                                       UserSkillData.skillDataComparator);*/
                                                Collections.sort(levelAnalyticsDataList,
                                                        Comparator.comparingLong(UserSkillLevelAnalyticsDataList::getSubmissionTimeInMillis));
                                            }
                                            ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList = new ArrayList<>();
                                            for (int index = 0; index < levelAnalyticsDataList.size(); index++) {
                                                long submissionTimeResponse = levelAnalyticsDataList.get(index).getSubmissionTimeInMillis();
                                                long responseScore = levelAnalyticsDataList.get(index).getUserScore();
                                                boolean isFound = false;
                                                if (submissionTimeResponse != 0) {

                                                    for (int e = 0; e < updatedLevelAnalyticsDataList.size(); e++) {

                                                        long submissionTimeFilter = updatedLevelAnalyticsDataList.get(e).getSubmissionTimeInMillis();
                                                        if (submissionTimeFilter != 0) {
                                                            String responseDate = getDate(submissionTimeResponse, "ddMMyyyy");
                                                            String filterDate = getDate(submissionTimeFilter, "ddMMyyyy");

                                                            if (responseDate.equalsIgnoreCase(filterDate) && responseScore > updatedLevelAnalyticsDataList.get(e).getUserScore()) {
                                                                isFound = true;
                                                                updatedLevelAnalyticsDataList.set(e, levelAnalyticsDataList.get(index));
                                                            } else if (responseDate.equalsIgnoreCase(filterDate) && responseScore <= updatedLevelAnalyticsDataList.get(e).getUserScore()) {
                                                                isFound = true;
                                                            }


                                                        }

                                                    }

                                                    if (!isFound) {
                                                        updatedLevelAnalyticsDataList.add(levelAnalyticsDataList.get(index));
                                                    }


                                                }


                                            }

                                            if (updatedLevelAnalyticsDataList.size() != 0) {

                                                history_lay.setVisibility(VISIBLE);
                                                media_container.setVisibility(GONE);
                                                image_bottom_lay.setVisibility(GONE);
                                                image_container.setVisibility(VISIBLE);

                                                media_container.setVisibility(GONE);
                                                image_bottom_lay.setVisibility(GONE);
                                                image_container.setVisibility(VISIBLE);

                                                dates = new String[updatedLevelAnalyticsDataList.size() + 1];
                                                for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {
                                                    dates[index] = updatedLevelAnalyticsDataList.get(index).getSubmissionTimeInMillis() + "";
                                                    SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                                    SimpleDateFormat displayDateFormat = new SimpleDateFormat("ddMMM", Locale.getDefault());
                                                    displayDateFormat.setTimeZone(TimeZone.getDefault());
                                                    Date date = responseDate.parse(getDate(updatedLevelAnalyticsDataList.get(index).getSubmissionTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                                                    lastDate = displayDateFormat.format(date);
                                                    lastScore = updatedLevelAnalyticsDataList.get(index).getUserScore();
                                                }
                                                dates[updatedLevelAnalyticsDataList.size()] = "";
                                                count = updatedLevelAnalyticsDataList.size();

                                                skill_chart.getDescription().setEnabled(false);
                                                skill_chart.setBackgroundColor(Color.WHITE);
                                                skill_chart.setDrawGridBackground(false);
                                                skill_chart.setDrawBarShadow(false);
                                                skill_chart.setHighlightFullBarEnabled(false);
                                                skill_chart.setVisibleXRangeMinimum(25);
                                                skill_chart.animateX(1000);
                                                skill_chart.animateY(1000);
                                                skill_chart.animateXY(1000, 1000);

                                                // draw bars behind lines
                                                skill_chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                                                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                                                });

                                                Legend l = skill_chart.getLegend();
                                                l.setWordWrapEnabled(true);
                                                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                                                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                                                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                                                l.setDrawInside(false);
                                                LegendEntry l1 = new LegendEntry("Scores", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.rgb(1, 181, 162));
                                                LegendEntry l2 = new LegendEntry("Levels", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.rgb(248, 120, 0));


                                                l.setCustom(new LegendEntry[]{l1, l2});

                                                YAxis rightAxis = skill_chart.getAxisRight();
                                                rightAxis.setAxisMaximum(levelMax);
                                                rightAxis.setDrawGridLines(false);
                                                rightAxis.setGranularity(10f);
                                                rightAxis.setAxisMinimum(0f);

                                                YAxis leftAxis = skill_chart.getAxisLeft();
                                                leftAxis.setAxisMaximum(endRange);
                                                leftAxis.setDrawGridLines(false);
                                                leftAxis.setGranularityEnabled(false);
                                                rightAxis.setGranularity(1f);
                                                leftAxis.setAxisMinimum(0f);


                                                final int[] position = {0};


                                                XAxis xAxis = skill_chart.getXAxis();
                                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                                xAxis.setAxisMinimum(-2f);
                                                xAxis.setGranularity(5f);
                                                xAxis.setDrawGridLines(false);
                                                xAxis.setGranularityEnabled(true);
                                                xAxis.setLabelRotationAngle(-90);
                                                xAxis.setValueFormatter(new ValueFormatter() {

                                                    @Override
                                                    public String getFormattedValue(float value) {

                                                        float spaceForBar = 10f;
                                                        int valuePosition = (int) (value / spaceForBar);
                                                        int valueReminder = (int) (value % spaceForBar);


                                                        if (valueReminder == 0 && valuePosition < dates.length) {
                                                            String dateFormat = dates[valuePosition];
                                                            try {
                                                                long dateTime = Long.parseLong(dateFormat);
                                                                SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                                                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                                                                displayDateFormat.setTimeZone(TimeZone.getDefault());
                                                                Date date = responseDate.parse(getDate(dateTime, "yyyy-MM-dd HH:mm:ss.SSS"));
                                                                return displayDateFormat.format(date);


                                                            } catch (Exception e) {
                                                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                                                return "";
                                                            }
                                                            //return "";
                                                        } else {
                                                            return "";
                                                        }


                                                        // return dates[(int) value % dates.length];

                                                    }
                                                });

                                                CombinedData data = new CombinedData();
                                                data.setData(generateLineData(updatedLevelAnalyticsDataList));
                                                data.setData(generateBarData(updatedLevelAnalyticsDataList));


                                                xAxis.setAxisMaximum(data.getXMax() + 0.25f);

                                                skill_chart.setData(data);
                                                skill_chart.invalidate();

                                                skill_chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                                    @Override
                                                    public void onValueSelected(Entry e, Highlight h) {

                                                        UserSkillLevelAnalyticsDataList userSoccerSkillDailyAnalyticsDataList = (UserSkillLevelAnalyticsDataList) e.getData();
                                                        long scoreValue = userSoccerSkillDailyAnalyticsDataList.getUserScore();
                                                        int levelIndex = 0;
                                                        for (int index = 0; index < skillData.getSoccerSkillLevelDataList().size(); index++) {
                                                            if (scoreValue >= skillData.getSoccerSkillLevelDataList().get(index).getScoreStartRange() && scoreValue <= skillData.getSoccerSkillLevelDataList().get(index).getScoreEndRange()) {
                                                                levelIndex = index;
                                                                break;
                                                            } else if (scoreValue < skillData.getSoccerSkillLevelDataList().get(index).getScoreStartRange()) {
                                                                levelIndex = index - 1;
                                                                break;
                                                            } else if (scoreValue > skillData.getSoccerSkillLevelDataList().get(index).getScoreEndRange()) {
                                                                levelIndex = skillData.getSoccerSkillLevelDataList().size() - 1;

                                                            }

                                                        }

                                                        String levelName = skillData.getSoccerSkillLevelDataList().get(levelIndex).getLevelName();
                                                        String levelvalue = levelName + "";
                                                        String levelString = levelName + " Level";
                                                        SpannableString ls1 = new SpannableString(levelString);
                                                        ls1.setSpan(new RelativeSizeSpan(0.5f), levelvalue.length(), levelString.length(), 0);
                                                        ls1.setSpan(new StyleSpan(BOLD), 0, levelvalue.length(), 0);
                                                        level.setText(ls1);

                                                        String value = scoreValue + "";
                                                        String scoreString = scoreValue + " " + getResources().getString(R.string.score_text);
                                                        SpannableString ss1 = new SpannableString(scoreString);
                                                        ss1.setSpan(new RelativeSizeSpan(0.5f), value.length(), scoreString.length(), 0);
                                                        ss1.setSpan(new StyleSpan(BOLD), 0, value.length(), 0);
                                                        score.setText(ss1);

                                                        SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                                        SimpleDateFormat displayDate = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
                                                        displayDate.setTimeZone(TimeZone.getDefault());
                                                        try {
                                                            Date responseDateDate = responseDate.parse(getDate(userSoccerSkillDailyAnalyticsDataList.getSubmissionTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                                                            submitted_time.setText(displayDate.format(responseDateDate));

                                                        } catch (ParseException p) {
                                                            p.printStackTrace();
                                                            submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getUserSubmissionDateTime());
                                                        }
                                                        //       submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getEntryDate());

                                                    }

                                                    @Override
                                                    public void onNothingSelected() {

                                                    }
                                                });

                                            }


                                        }


                                    }
                                }


                            } else {
                                if (response.optString("error") != null && !response.optString("error").isEmpty()) {
                                    OustSdkTools.showToast(response.optString("error"));
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                                }

                            }
                        } catch (Exception e) {
                            OustSdkTools.showToast(e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OustSdkTools.showToast(getResources().getString(R.string.something_went_wrong));
                    }
                });

    }

    private LineData generateLineData(ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();
        float spaceForBar = 10f;

        if (updatedLevelAnalyticsDataList != null && updatedLevelAnalyticsDataList.size() != 0 && soccerSkillLevelDataLists != null && soccerSkillLevelDataLists.size() != 0) {
            for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {

                long range = updatedLevelAnalyticsDataList.get(index).getUserScore();
                int levelIndex = 0;
                for (int i = 0; i < soccerSkillLevelDataLists.size(); i++) {


                       /* if (range >= soccerSkillLevelDataLists.get(i).getScoreStartRange() && range <= soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                            entries.add(new Entry(index * spaceForBar, i + 1, updatedLevelAnalyticsDataList.get(index)));
                            //break;
                        }
*/

                    if (range >= soccerSkillLevelDataLists.get(i).getScoreStartRange() && range <= soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                        levelIndex = i;
                        break;
                    } else if (range < soccerSkillLevelDataLists.get(i).getScoreStartRange()) {
                        levelIndex = i - 1;
                        break;
                    } else if (range > soccerSkillLevelDataLists.get(i).getScoreEndRange()) {
                        levelIndex = soccerSkillLevelDataLists.size() - 1;

                    }


                }

                entries.add(new Entry(index * spaceForBar, levelIndex + 1, updatedLevelAnalyticsDataList.get(index)));

            }

        }


        LineDataSet set = new LineDataSet(entries, "" + getResources().getString(R.string.level));
        set.setColor(Color.rgb(248, 120, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(248, 120, 0));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(248, 120, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);


        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(ArrayList<UserSkillLevelAnalyticsDataList> updatedLevelAnalyticsDataList) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        float barWidth = 2f;
        float spaceForBar = 10f;

        if (updatedLevelAnalyticsDataList != null && updatedLevelAnalyticsDataList.size() != 0) {
            for (int index = 0; index < updatedLevelAnalyticsDataList.size(); index++) {
                entries.add(new BarEntry(index * spaceForBar, updatedLevelAnalyticsDataList.get(index).getUserScore(), updatedLevelAnalyticsDataList.get(index)));
            }

            UserSkillLevelAnalyticsDataList userSoccerSkillDailyAnalyticsDataList = updatedLevelAnalyticsDataList.get(updatedLevelAnalyticsDataList.size() - 1);
            long scoreValue = userSoccerSkillDailyAnalyticsDataList.getUserScore();

            int levelIndex = 0;
            for (int index = 0; index < soccerSkillLevelDataLists.size(); index++) {
                if (scoreValue >= soccerSkillLevelDataLists.get(index).getScoreStartRange() &&
                        scoreValue <= soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                    levelIndex = index;
                    break;
                } else if (scoreValue < soccerSkillLevelDataLists.get(index).getScoreStartRange()) {
                    levelIndex = index - 1;
                    break;
                } else if (scoreValue > soccerSkillLevelDataLists.get(index).getScoreEndRange()) {
                    levelIndex = soccerSkillLevelDataLists.size() - 1;

                }

            }

            String levelName = soccerSkillLevelDataLists.get(levelIndex).getLevelName();
            String levelvalue = levelName + "";
            String levelString = levelName + " " + getResources().getString(R.string.level);
            SpannableString ls1 = new SpannableString(levelString);
            ls1.setSpan(new RelativeSizeSpan(0.5f), levelvalue.length(), levelString.length(), 0);
            ls1.setSpan(new StyleSpan(BOLD), 0, levelvalue.length(), 0);
            level.setText(ls1);

            String value = scoreValue + "";
            String scoreString = scoreValue + " " + getResources().getString(R.string.score_text);
            SpannableString ss1 = new SpannableString(scoreString);
            ss1.setSpan(new RelativeSizeSpan(0.5f), value.length(), scoreString.length(), 0);
            ss1.setSpan(new StyleSpan(BOLD), 0, value.length(), 0);
            score.setText(ss1);

            SimpleDateFormat responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat displayDate = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
            displayDate.setTimeZone(TimeZone.getDefault());
            try {
                Date responseDateDate = responseDate.parse(getDate(userSoccerSkillDailyAnalyticsDataList.getSubmissionTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                submitted_time.setText(displayDate.format(responseDateDate));

            } catch (ParseException p) {
                p.printStackTrace();
                submitted_time.setText(userSoccerSkillDailyAnalyticsDataList.getUserSubmissionDateTime());
            }


        }


        BarDataSet set1 = new BarDataSet(entries, "" + getResources().getString(R.string.score_text));
        set1.setColor(Color.rgb(1, 181, 162));
        set1.setDrawValues(false);
        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        // make this BarData object grouped
        // d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }

    public void checkForStoragePermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        if (ContextCompat.checkSelfPermission(SkillDetailActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            intentPopUp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void intentPopUp() {
        Dialog dialog = new Dialog(SkillDetailActivity.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.intent_popuup);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView select_from_gallery = dialog.findViewById(R.id.select_from_gallery);
        TextView record_video = dialog.findViewById(R.id.record_video);
        TextView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);

        select_from_gallery.setOnClickListener(v -> {
            showAddVideoOption();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        record_video.setOnClickListener(v -> {
            recordVideoOption();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        cancel_dialog.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void showAddVideoOption() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra("return-data", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void recordVideoOption() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {

                OustStaticVariableHandling.getInstance().setCameraStarted(false);
                if (resultCode == RESULT_OK) {

                    onSelectFromGalleryResult(data);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Uri path = data.getData();
        if (path != null) {
            if (path.toString().contains("com.google.android.apps.photos")) {
                String filePath = FilePath.getPathFromInputStreamUri(SkillDetailActivity.this, path);

                File original = new File(filePath);
                String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                videomediaFile = new File(filePath);
                previewVideo();

            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String filePath = FilePath.getRealPathFromUri(SkillDetailActivity.this, path);
                if (filePath != null) {
                    File original = new File(filePath);
                    String extension_file = original.getAbsolutePath().substring(original.getAbsolutePath().lastIndexOf("."));
                    videomediaFile = new File(filePath);
                    previewVideo();
                } else {
                    OustSdkTools.showToast(getResources().getString(R.string.unable_to_select_attachment));
                }
            } else {

                String[] proj = {MediaStore.Images.Media.DATA};
                String result = null;

                CursorLoader cursorLoader = new CursorLoader(
                        SkillDetailActivity.this,
                        path, proj, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();

                if (cursor != null) {
                    int column_index =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(column_index);
                    videomediaFile = new File(result);
                    previewVideo();
                }
            }
        }
    }


    private void previewVideo() {
        try {
            mediaFileName = null;
            preview_container.setVisibility(VISIBLE);
            preview_image_thumbnail.setVisibility(VISIBLE);
            upload_video.setClickable(true);
//            delete_video.setVisibility(View.VISIBLE);
//            play_videorec_layout.setVisibility(View.VISIBLE);
            Bitmap thumbnail = null;
            if (videomediaFile != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(videomediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            }
            if (thumbnail != null)
                preview_image_thumbnail.setImageBitmap(thumbnail);

            assert videomediaFile != null;
            upload_video_text.setText(videomediaFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return com.oustme.oustsdk.compression.video.MediaController.getInstance().convertVideo(videomediaFile.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            //progressBar.setVisibility(View.GONE);
            if (compressed) {
                if (OustSdkTools.checkInternetStatus()) {
                    upload_video.setClickable(false);
                    // delete_video.setClickable(false);


                    // Log.d(TAG, "Original Size: " + Formatter.formatFileSize(getActivity(), videomediaFile.length()));

                    File out = new File(
                            Environment.getExternalStorageDirectory()
                                    + File.separator
                                    + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME
                                    + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR,
                            "compress.mp4"
                    );
                    videomediaFile = out;

                    uploadVideoToAWS();
                } else {
                    upload_video.setClickable(true);
                    branding_mani_layout.setVisibility(GONE);
                    OustSdkTools.showToast(getResources().getString(R.string.no_internet_connection));
                }
            } else {
                upload_video.setClickable(true);
                branding_mani_layout.setVisibility(GONE);
                OustSdkTools.showToast(getResources().getString(R.string.upload_fail_msg));
            }
        }
    }

    private void uploadVideoToAWS() {
        try {
            //OustSdkTools.showToast("the size is " + length + " kb");
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 1, true));
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId), clientConfiguration);
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());

            /*if (timeout) {
                ConfirmToUploadCancel();
            }*/
            if (videomediaFile == null || !videomediaFile.exists()) {
                upload_video.setClickable(true);
                Toast.makeText(OustSdkApplication.getContext(), "" + getResources().getString(R.string.unable_to_select_attachment), Toast.LENGTH_SHORT).show();
                branding_mani_layout.setVisibility(GONE);
            } else {

                // delete_video.setClickable(false);

                //   removeVideoPlayer();
                mediaFileName = System.currentTimeMillis() + "_" + activeUser.getStudentKey();
                final TransferObserver observer = transferUtility.upload(AppConstants.MediaURLConstants.BUCKET_NAME, OustPreferences.get("tanentid") + "/skillsUpload/" + mediaFileName + ".mp4", videomediaFile);
                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED.equals(observer.getState())) {
                            branding_mani_layout.setVisibility(GONE);
                            upload_video.setVisibility(GONE);
                            select_video.setVisibility(GONE);
                            cancel_video.setVisibility(VISIBLE);
                            mediaFileName = "https://di5jfel2ggs8k.cloudfront.net/" + OustPreferences.get("tanentid") + "/skillsUpload/" + mediaFileName + ".mp4";

                            // sendResponseForBackend(filename + ".mp4");
                        }

                        if (TransferState.FAILED.equals(observer.getState())) {
                            branding_mani_layout.setVisibility(GONE);
                            upload_video.setClickable(true);
                            Toast.makeText(SkillDetailActivity.this, "" + getResources().getString(R.string.sorry_media_upload_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        long _bytesCurrent = bytesCurrent;
                        long _bytesTotal = bytesTotal;
                        float percentage = ((float) _bytesCurrent / (float) _bytesTotal * 100);
                        String per = "Uploaded " + (int) percentage + " %";
                        branding_mani_layout.setVisibility(VISIBLE);
                        branding_percentage.setText(per);

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        branding_mani_layout.setVisibility(GONE);
                        Toast.makeText(SkillDetailActivity.this, "" + getResources().getString(R.string.sorry_media_upload_failed), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            branding_mani_layout.setVisibility(GONE);
            upload_video.setClickable(true);
        }

    }

    private void levelPopUp(boolean showPopUp, String levelBadge, String levelName) {

        Dialog popUp = new Dialog(SkillDetailActivity.this, R.style.DialogTheme);
        popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUp.setContentView(R.layout.level_pop_up);
        Objects.requireNonNull(popUp.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popUp.show();


        TextView wohoo_text = popUp.findViewById(R.id.wohoo_text);
        TextView below_text = popUp.findViewById(R.id.below_text);
        TextView level_name = popUp.findViewById(R.id.level_name);
        ImageView inside_circle = popUp.findViewById(R.id.inside_circle);
        if (showPopUp && levelBadge != null && !levelBadge.isEmpty()) {
            wohoo_text.setVisibility(VISIBLE);
            below_text.setText("" + getResources().getString(R.string.you_leveled_up));
            below_text.setTextColor(Color.parseColor("#F87800"));
            if (levelName != null) {
                level_name.setText(levelName);
            }
            Glide.with(SkillDetailActivity.this).load(levelBadge).into(inside_circle);
        } else if (showPopUp) {
            wohoo_text.setVisibility(VISIBLE);
            below_text.setText("" + getResources().getString(R.string.you_leveled_up));
            below_text.setTextColor(Color.parseColor("#F87800"));
            if (levelName != null) {
                level_name.setText(levelName);
            }
            Glide.with(SkillDetailActivity.this).load(R.drawable.ic_skill_wohoo).into(inside_circle);
        } else {
            wohoo_text.setVisibility(GONE);
            below_text.setText("" + getResources().getString(R.string.greatjob_result));
            Glide.with(SkillDetailActivity.this).load(R.drawable.ic_skill_great_job).into(inside_circle);
        }

        if (userSkillData != null) {
            final Handler handler = new Handler();
            if (rangeValue >= endRange && (rangeValue < userSkillData.getIdpTargetScore() || userSkillData.getIdpTargetScore() == 0)) {
                handler.postDelayed(() -> commonPopUp(true, false), 2000);

            } else {
                if (userSkillData.getIdpTargetScore() != 0 && rangeValue >= userSkillData.getIdpTargetScore()) {
                    if (userSkillData.getIdpTargetDate() != 0) {
                        Date date = new Date(userSkillData.getIdpTargetDate());
                        Date currentDate = new Date();
                        if (date.getTime() >= currentDate.getTime()) {
                            handler.postDelayed(() -> commonPopUp(false, true), 2000);
                        } else {
                            handler.postDelayed(this::intentData, 4000);
                        }
                    } else {
                        handler.postDelayed(this::intentData, 4000);
                    }

                } else {

                    handler.postDelayed(this::intentData, 4000);
                }
            }
        } else {


            final Handler handler = new Handler();
            handler.postDelayed(this::intentData, 4000);

        }


    }

    private void intentData() {
        Intent intent = new Intent(SkillDetailActivity.this, SkillDetailActivity.class);
        intent.putExtra("SkillId", "" + skillData.getSoccerSkillId());
        intent.putExtra("category", "Skill");
        intent.putExtra("catalog_type", "SOCCER_SKILL");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        SkillDetailActivity.this.finish();
    }

    private void showConfirmToUploadPopup() {
        try {
            View popUpView = getLayoutInflater().inflate(R.layout.rejectgamepopup, null);
            final PopupWindow plaayCanclePopup = OustSdkTools.createPopWithoutBackButton(popUpView);
            final Button btnYes = popUpView.findViewById(R.id.btnYes);
            final Button btnNo = popUpView.findViewById(R.id.btnNo);
            final ImageButton btnClose = popUpView.findViewById(R.id.btnClose);
            TextView popupTitle = popUpView.findViewById(R.id.txtTitle);
            TextView popupContent = popUpView.findViewById(R.id.txtRejectChallengeMsg);

            btnYes.setText(getResources().getString(R.string.yes));
            btnNo.setText(getResources().getString(R.string.no));

            popupContent.setText(getResources().getString(R.string.delete_confirm));
            popupTitle.setText(getResources().getString(R.string.confirmation));

            btnYes.setOnClickListener(view -> {
                videomediaFile = null;
                mediaFileName = null;
                plaayCanclePopup.dismiss();
                select_video.setVisibility(VISIBLE);
                upload_video.setVisibility(VISIBLE);
                cancel_video.setVisibility(GONE);
                preview_container.setVisibility(GONE);
                select_video.setClickable(true);
                upload_video.setClickable(true);
                upload_video_text.setText(getResources().getString(R.string.upload_your_video_text));
            });

            btnNo.setOnClickListener(view -> plaayCanclePopup.dismiss());
            btnClose.setOnClickListener(view -> plaayCanclePopup.dismiss());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d("Popup exception", e.getMessage() + "");
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    private void commonPopUp(boolean isCongrats, boolean isIdp) {
        Dialog commonPopUp = new Dialog(SkillDetailActivity.this, R.style.DialogTheme);
        commonPopUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commonPopUp.setContentView(R.layout.skill_common_popup);
        Objects.requireNonNull(commonPopUp.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        commonPopUp.show();

        ImageView bg_animation = commonPopUp.findViewById(R.id.bg_animation);
        ImageView thumbImage = commonPopUp.findViewById(R.id.thumbImage);
        TextView pop_msg_tv = commonPopUp.findViewById(R.id.pop_msg_tv);
        LinearLayout idp_msg_lay = commonPopUp.findViewById(R.id.idp_msg_lay);
        TextView targe_completed_date = commonPopUp.findViewById(R.id.targe_completed_date);
        TextView skill_name = commonPopUp.findViewById(R.id.skill_name);
        TextView congratation_text = commonPopUp.findViewById(R.id.congratation_text);
        TextView target_date = commonPopUp.findViewById(R.id.target_date);
        LinearLayout next_lay = commonPopUp.findViewById(R.id.next_lay);

        Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
        next_lay.setBackground(OustSdkTools.drawableColor(drawable));

        if (!isIdp) {
            String message = "" + getResources().getString(R.string.keep_try_skill);
            Drawable thumbDrawable = getResources().getDrawable(R.drawable.ic_well_tried_thum);
            Drawable bgDrawable = getResources().getDrawable(R.drawable.bg_wd);
            if (rangeValue >= endRange && isCongrats) {
                message = "" + getResources().getString(R.string.achieved_score_skill);
                thumbDrawable = getResources().getDrawable(R.drawable.congrats_msg);
                congratation_text.setVisibility(VISIBLE);
                bgDrawable = getResources().getDrawable(R.drawable.bg_animation);
            } else {
                congratation_text.setVisibility(GONE);
            }

            bg_animation.setImageDrawable(bgDrawable);
            thumbImage.setImageDrawable(OustSdkTools.drawableColor(thumbDrawable));
            pop_msg_tv.setText(message);
            pop_msg_tv.setVisibility(VISIBLE);


        } else {
            idp_msg_lay.setVisibility(VISIBLE);
            if (skillData != null) {
                String skillNametext = skillData.getSoccerSkillName();
                if (skillNametext.contains("<br/>")) {
                    skillNametext = skillNametext.replace("<br/>", "");
                }
                if (skillNametext.contains("<br>")) {
                    skillNametext = skillNametext.replace("<br>", "");
                }

                skill_name.setText(skillNametext);

                if (userSkillData.getIdpTargetDate() != 0) {

                    Date date = new Date(userSkillData.getIdpTargetDate());
                    DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    format.setTimeZone(TimeZone.getDefault());
                    String idpTargetDate = "TARGET DATE - " + format.format(date);
                    String idpCompletedOn = getResources().getString(R.string.on_text) + " - " + format.format(new Date());
                    target_date.setText(idpTargetDate);
                    targe_completed_date.setText(idpCompletedOn);
                }

            }
        }

        next_lay.setOnClickListener(v -> {

            final Handler handler = new Handler();
            handler.postDelayed(this::intentData, 1000);
            if (commonPopUp.isShowing()) {
                commonPopUp.dismiss();
            }

        });


    }


}
