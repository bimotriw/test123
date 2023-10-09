package com.oustme.oustsdk.profile;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.base.BaseActivity;
import com.oustme.oustsdk.course_ui.CourseDetailScreen;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OnSwipeTouchListener;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseCompletionWithBadgeActivity extends BaseActivity {

    RelativeLayout pop_layout;
    FrameLayout pop_up_close_icon;
    NestedScrollView scroll_lay;
    CircleImageView user_avatar;
    TextView user_greeting;
    LinearLayout badge_name_layout;
    TextView badge_name_text;
    CircleImageView congrats_image;
    TextView completed_text;
    TextView content_title;
    TextView content_completed_date;
    LinearLayout layout_ResultScore;
    LinearLayout score_lay;
    TextView user_score_text;
    LinearLayout layout_coins;
    TextView coins_earned;
    TextView user_time_taken;
    TextView participants_count;
    LinearLayout certificate_lay;
    ImageView certificate_icon;
    LinearLayout bottom_nav;
    RelativeLayout previous;
    FrameLayout courseCompletionBtn;
    TextView courseCompletionText;
    TextView count;
    RelativeLayout next;


    //intent data
    int color;
    long courseId;
    String badgeName;
    String badgeIcon;
    int position;
    boolean isMicroCourse;
    HashMap<Long, BadgeModel> badgeModelHashMap;
    private Long[] keySet;
    private boolean isComingFromCourseLearningMapPage;
    long userLevelXp = 0;
    long userLevelOc = 0;
    long completedOn = 0;

    ActiveUser activeUser;


    @Override
    protected int getContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_course_completion_with_badge;
    }

    @Override
    protected void initView() {
        try {
            if (OustSdkApplication.getContext() == null) {
                OustSdkApplication.setmContext(CourseCompletionWithBadgeActivity.this);
            }
            OustSdkTools.setLocale(CourseCompletionWithBadgeActivity.this);
//            OustGATools.getInstance().reportPageViewToGoogle(CourseCompletionWithBadgeActivity.this, "Oust Course Badge List Page");

            pop_layout = findViewById(R.id.pop_layout);
            pop_up_close_icon = findViewById(R.id.pop_up_close_icon);
            scroll_lay = findViewById(R.id.scroll_lay);
            user_avatar = findViewById(R.id.user_avatar);
            user_greeting = findViewById(R.id.user_greeting);
            badge_name_layout = findViewById(R.id.badge_name_layout);
            badge_name_text = findViewById(R.id.badge_name_text);
            congrats_image = findViewById(R.id.congrats_image);
            completed_text = findViewById(R.id.completed_text);
            content_title = findViewById(R.id.content_title);
            content_completed_date = findViewById(R.id.content_completed_date);
            layout_ResultScore = findViewById(R.id.layout_ResultScore);
            score_lay = findViewById(R.id.score_lay);
            user_score_text = findViewById(R.id.user_score_text);
            layout_coins = findViewById(R.id.layout_coins);
            coins_earned = findViewById(R.id.coins_earned);
            courseCompletionBtn = findViewById(R.id.completion_question_action_button);
            courseCompletionText = findViewById(R.id.replay_txt);

            try {
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    coins_earned.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_coins_corn, 0, 0, 0);
                } else {
                    coins_earned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coins_golden, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            user_time_taken = findViewById(R.id.user_time_taken);
            participants_count = findViewById(R.id.participants_count);
            certificate_lay = findViewById(R.id.certificate_lay);
            certificate_icon = findViewById(R.id.certificate_icon);
            bottom_nav = findViewById(R.id.bottom_nav);
            previous = findViewById(R.id.previous);
            count = findViewById(R.id.count);
            next = findViewById(R.id.next);

            color = OustResourceUtils.getColors();
            setIconColors(color, true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setIconColors(int color, boolean b) {
        Drawable actionDrawable = getResources().getDrawable(R.drawable.button_rounded_ten_bg);
        DrawableCompat.setTint(
                DrawableCompat.wrap(actionDrawable),
                color
        );

        courseCompletionBtn.setBackground(actionDrawable);
    }

    @Override
    protected void initData() {

        try {
            Bundle dataBundle = getIntent().getExtras();

            if (dataBundle != null) {
                courseId = dataBundle.getLong("courseId");
                badgeName = dataBundle.getString("badgeName");
                badgeIcon = dataBundle.getString("badgeIcon");
                isMicroCourse = dataBundle.getBoolean("isMicroCourse");
                position = dataBundle.getInt("position");
                isComingFromCourseLearningMapPage = dataBundle.getBoolean("isComingFromCourseLearningMapPage");
                try {
                    badgeModelHashMap = (HashMap<Long, BadgeModel>) dataBundle.getSerializable("badgeModelHashMap");
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            courseCompletionBtn.setOnClickListener(view -> {
                try {
                    Intent taskCourseIntent = new Intent(OustSdkApplication.getContext(), CourseDetailScreen.class);
                    taskCourseIntent.putExtra("learningId", courseId + "");
                    taskCourseIntent.putExtra("isMicroCoursePlay", true);
                    taskCourseIntent.putExtra("catalog_id", "" + courseId);
                    taskCourseIntent.putExtra("catalog_type", "COURSE");
                    taskCourseIntent.putExtra("taskPosition", 0);
                    taskCourseIntent.putExtra("taskCompletion", 0);
                    startActivity(taskCourseIntent);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            });


            activeUser = OustAppState.getInstance().getActiveUser();

            if (badgeName != null && !badgeName.isEmpty()) {
                badge_name_text.setText(badgeName);
                badge_name_layout.setVisibility(View.VISIBLE);
            }

            if (badgeIcon != null && !badgeIcon.isEmpty()) {
                Glide.with(context).load(badgeIcon).into(congrats_image);
            }

            if (badgeModelHashMap != null && badgeModelHashMap.size() != 0) {
                courseCompletionBtn.setVisibility(View.GONE);
                bottom_nav.setVisibility(View.VISIBLE);
                keySet = badgeModelHashMap.keySet().toArray(new Long[badgeModelHashMap.size()]);

                String countText = (position + 1) + "/" + badgeModelHashMap.size();
                count.setVisibility(View.VISIBLE);
                count.setText(countText);

                if (position == 0) {
                    previous.setVisibility(View.INVISIBLE);
                }

                if (position == (badgeModelHashMap.size() - 1)) {
                    next.setVisibility(View.INVISIBLE);
                }
            } else {
                if (isComingFromCourseLearningMapPage) {
                    courseCompletionBtn.setVisibility(View.GONE);
                    bottom_nav.setVisibility(View.GONE);
                } else if (isMicroCourse) {
                    courseCompletionBtn.setVisibility(View.VISIBLE);
                    bottom_nav.setVisibility(View.GONE);
                }
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable bgDrawable = getResources().getDrawable(R.drawable.ic_common_circle);
            certificate_icon.setBackground(OustSdkTools.drawableColor(bgDrawable));

            if (courseId != 0) {
                try {
                    if (OustSdkTools.checkInternetStatus()) {
                        String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                        String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                        courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                        courseBaseurl = courseBaseurl.replace("{courseId}", "" + courseId);
                        courseBaseurl = courseBaseurl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

                        courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                        Log.d(TAG, "getLearningMap: " + courseBaseurl);
                        ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "getLearningMap - onResponse: " + response.toString());
                                Map<String, Object> learningMap = new HashMap<>();
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                                if ((learningMap != null)) {
                                    CourseDataClass courseDataClass = new CourseDataClass();

                                    if (learningMap.get("courseId") != null) {
                                        courseDataClass.setCourseId(OustSdkTools.convertToLong(learningMap.get("courseId")));
                                    }

                                    if (learningMap.get("courseName") != null) {
                                        courseDataClass.setCourseName((String) learningMap.get("courseName"));
                                    }

                                    if (learningMap.get("badgeName") != null) {
                                        courseDataClass.setBadgeName((String) learningMap.get("badgeName"));
                                    }

                                    if (learningMap.get("badgeIcon") != null) {
                                        courseDataClass.setBadgeIcon((String) learningMap.get("badgeIcon"));
                                    }

                                    if (learningMap.get("totalOc") != null) {
                                        courseDataClass.setTotalOc(OustSdkTools.convertToLong(learningMap.get("totalOc")));
                                    }

                                    if (learningMap.get("xp") != null) {
                                        courseDataClass.setXp(OustSdkTools.convertToLong(learningMap.get("xp")));
                                    }

                                    if (learningMap.get("contentDuration") != null) {
                                        courseDataClass.setContentDuration(OustSdkTools.convertToLong(learningMap.get("contentDuration")));
                                    }

                                    if (learningMap.get("enrolledCount") != null) {
                                        courseDataClass.setNumEnrolledUsers(OustSdkTools.convertToLong(learningMap.get("enrolledCount") + ""));
                                    }

                                    getUserProgressData(OustAppState.getInstance().getActiveUser().getStudentid(), courseId, courseDataClass);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getUserProgressData(String studentId, long courseId, CourseDataClass courseDataClass) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                userLevelXp = 0;
                userLevelOc = 0;
                String courseUserProgressBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_user_progress_api);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{org-id}", "" + tenantName);
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{courseId}", "" + courseId);
                courseUserProgressBaseurl = courseUserProgressBaseurl.replace("{userId}", "" + studentId);

                courseUserProgressBaseurl = HttpManager.getAbsoluteUrl(courseUserProgressBaseurl);

                Log.d(TAG, "loadUserDataFromFirebase: " + courseUserProgressBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseUserProgressBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "loadUserDataFromFirebase - onResponse: " + response.toString());
                        Map<String, Object> userDataMap = new HashMap<>();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            userDataMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                        if (userDataMap != null) {
                            if (userDataMap.get("completedOn") != null) {
                                completedOn = OustSdkTools.convertToLong(userDataMap.get("completedOn"));
                            }

                            if (userDataMap.get("levels") != null) {
                                Object o1 = userDataMap.get("levels");
                                if (o1 != null) {
                                    if (o1.getClass().equals(HashMap.class)) {
                                        final Map<String, Object> objectMap = (Map<String, Object>) o1;
                                        for (String levelKey : objectMap.keySet()) {
                                            final Map<String, Object> levelMap = (Map<String, Object>) objectMap.get(levelKey);
                                            if (levelMap != null) {
                                                if (levelMap.get("userLevelOC") != null) {
                                                    userLevelOc += OustSdkTools.convertToLong(levelMap.get("userLevelOC"));
                                                }
                                                if (levelMap.get("userLevelXp") != null) {
                                                    userLevelXp += OustSdkTools.convertToLong(levelMap.get("userLevelXp"));
                                                }
                                            }
                                        }
                                    } else if (o1.getClass().equals(ArrayList.class)) {
                                        List<Object> levelsList = (List<Object>) userDataMap.get("levels");
                                        if (levelsList != null && levelsList.size() > 0) {
                                            for (int i = 0; i < levelsList.size(); i++) {
                                                if (levelsList.get(i) != null) {
                                                    final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                                    if (levelMap != null) {
                                                        if (levelMap.get("userLevelOC") != null) {
                                                            userLevelOc += OustSdkTools.convertToLong(levelMap.get("userLevelOC"));
                                                        }
                                                        if (levelMap.get("userLevelXp") != null) {
                                                            userLevelXp += OustSdkTools.convertToLong(levelMap.get("userLevelXp"));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            setData(courseDataClass);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void initListener() {

        try {
            pop_up_close_icon.setOnClickListener(v -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {

            if (badgeModelHashMap != null && badgeModelHashMap.size() != 0) {

                previous.setOnClickListener(v -> {
                    if (position > 0 && position < badgeModelHashMap.size()) {
                        BadgeModel badgeModel = badgeModelHashMap.get(keySet[position - 1]);
                        if (badgeModel != null) {
                            Intent badgeIntent = new Intent(CourseCompletionWithBadgeActivity.this, CourseCompletionWithBadgeActivity.class);
                            badgeIntent.putExtra("courseId", keySet[position - 1]);
                            badgeIntent.putExtra("position", position - 1);
                            badgeIntent.putExtra("badgeName", badgeModel.getBadgeName());
                            badgeIntent.putExtra("badgeIcon", badgeModel.getBadgeIcon());
                            badgeIntent.putExtra("badgeModelHashMap", badgeModelHashMap);
                            startActivity(badgeIntent);
                            overridePendingTransition(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
                            CourseCompletionWithBadgeActivity.this.finish();
                        }


                    }
                });

                next.setOnClickListener(v -> {
                    if (position >= 0 && position < badgeModelHashMap.size() - 1) {
                        BadgeModel badgeModel = badgeModelHashMap.get(keySet[position + 1]);
                        if (badgeModel != null) {
                            Intent badgeIntent = new Intent(CourseCompletionWithBadgeActivity.this, CourseCompletionWithBadgeActivity.class);
                            badgeIntent.putExtra("courseId", keySet[position + 1]);
                            badgeIntent.putExtra("position", position + 1);
                            badgeIntent.putExtra("badgeName", badgeModel.getBadgeName());
                            badgeIntent.putExtra("badgeIcon", badgeModel.getBadgeIcon());
                            badgeIntent.putExtra("badgeModelHashMap", badgeModelHashMap);
                            startActivity(badgeIntent);
                            overridePendingTransition(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                            CourseCompletionWithBadgeActivity.this.finish();
                        }


                    }
                });

                swipeFunction();
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private void setData(CourseDataClass courseDataClass) {
        try {
            if (courseDataClass.getCourseName() != null && !courseDataClass.getCourseName().isEmpty()) {
                content_title.setText(courseDataClass.getCourseName());
            }

            if (activeUser != null) {
                if (activeUser.getAvatar() != null && !activeUser.getAvatar().isEmpty()) {
                    Picasso.get().load(activeUser.getAvatar()).placeholder(R.drawable.ic_user_avatar).into(user_avatar);
                }

                if (activeUser.getUserDisplayName() != null && !activeUser.getUserDisplayName().isEmpty()) {
                    String userGreetings = getResources().getString(R.string.congratulations_text) + "\n" + activeUser.getUserDisplayName() + "!";
                    user_greeting.setText(userGreetings);
                }
            }

            if (badgeName == null) {
                if (courseDataClass.getBadgeName() != null && !courseDataClass.getBadgeName().isEmpty()) {
                    badge_name_text.setText(courseDataClass.getBadgeName());
                    badge_name_layout.setVisibility(View.VISIBLE);
                }
            }

            if (badgeIcon == null) {
                if (courseDataClass.getBadgeIcon() != null && !courseDataClass.getBadgeIcon().isEmpty()) {
                    Glide.with(context).load(courseDataClass.getBadgeIcon()).into(congrats_image);
                }
            }

            //String completedDate = "on " + new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US).format(new Date(completedOn));
            if (completedOn != 0) {
                String completedDate = getResources().getString(R.string.completed_on) + " " + new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date(completedOn));
                content_completed_date.setText(completedDate);
            }

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(congrats_image,
                    PropertyValuesHolder.ofFloat("scaleX", 1.75f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.75f));
            scaleDown.setDuration(2000);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.start();

            String yourCoinText = userLevelOc + "/" + courseDataClass.getTotalOc();
            if (yourCoinText.contains("/")) {
                String[] spilt = yourCoinText.split("/");
                Spannable yourCoinSpan = new SpannableString(yourCoinText);
                //yourCoinSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourCoinText.length(), 0);
                yourCoinSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                yourCoinSpan.setSpan(new RelativeSizeSpan(1.25f), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                coins_earned.setText(yourCoinSpan);
            } else {
                coins_earned.setText(yourCoinText);
            }

            if (courseDataClass.getTotalOc() == 0 || userLevelOc == 0) {
                layout_coins.setVisibility(View.GONE);
            }

            String yourScoreText = userLevelXp + "/" + courseDataClass.getXp();
            if (yourScoreText.contains("/")) {
                String[] spilt = yourScoreText.split("/");
                Spannable yourScoreSpan = new SpannableString(yourScoreText);
                //yourScoreSpan.setSpan(new RelativeSizeSpan(0.75f), spilt[0].length(), yourScoreText.length(), 0);
                yourScoreSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                yourScoreSpan.setSpan(new RelativeSizeSpan(1.25f), 0, spilt[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                user_score_text.setText(yourScoreSpan);
            } else {
                user_score_text.setText(yourScoreText);
            }

            long enrolledCount = courseDataClass.getNumEnrolledUsers();
            if (enrolledCount == 0) {
                enrolledCount = 1;
            }
            String enrolledText = "" + enrolledCount;
            Spannable enrolledSpan = new SpannableString(enrolledText);
            enrolledSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, enrolledText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            enrolledSpan.setSpan(new RelativeSizeSpan(1.25f), 0, enrolledText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            participants_count.setText(enrolledSpan);


            String timeTakenText = "1 min";
            if (courseDataClass.getContentDuration() != 0) {
                int courseDuration = (int) (courseDataClass.getContentDuration() * 1.0) / 60;
                timeTakenText = courseDuration + " min";
                if (courseDataClass.getContentDuration() < 60) {
                    timeTakenText = "1 min";
                }
            }

            Spannable spanText = new SpannableString(timeTakenText);
            if (timeTakenText.contains(" ")) {
                String[] timeTaken = timeTakenText.split(" ");
                spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_text)), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanText.setSpan(new RelativeSizeSpan(1.25f), 0, timeTaken[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            user_time_taken.setText(spanText);

            if (userLevelXp == 0 || courseDataClass.getXp() == 0) {
                score_lay.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeFunction() {

        try {
            scroll_lay.setOnTouchListener(new OnSwipeTouchListener(CourseCompletionWithBadgeActivity.this) {
                public void onSwipeTop() {

                }

                public void onSwipeRight() {

                    if (position > 0 && position < badgeModelHashMap.size()) {
                        BadgeModel badgeModel = badgeModelHashMap.get(keySet[position - 1]);
                        if (badgeModel != null) {
                            Intent badgeIntent = new Intent(CourseCompletionWithBadgeActivity.this, CourseCompletionWithBadgeActivity.class);
                            badgeIntent.putExtra("courseId", keySet[position - 1]);
                            badgeIntent.putExtra("position", position - 1);
                            badgeIntent.putExtra("badgeName", badgeModel.getBadgeName());
                            badgeIntent.putExtra("badgeIcon", badgeModel.getBadgeIcon());
                            badgeIntent.putExtra("badgeModelHashMap", badgeModelHashMap);
                            startActivity(badgeIntent);
                            overridePendingTransition(R.anim.learningview_reverseanimb, R.anim.learningview_reverseslideanima);
                            CourseCompletionWithBadgeActivity.this.finish();
                        }


                    }
                }

                public void onSwipeLeft() {
                    if (position >= 0 && position < badgeModelHashMap.size() - 1) {
                        BadgeModel badgeModel = badgeModelHashMap.get(keySet[position + 1]);
                        if (badgeModel != null) {
                            Intent badgeIntent = new Intent(CourseCompletionWithBadgeActivity.this, CourseCompletionWithBadgeActivity.class);
                            badgeIntent.putExtra("courseId", keySet[position + 1]);
                            badgeIntent.putExtra("position", position + 1);
                            badgeIntent.putExtra("badgeName", badgeModel.getBadgeName());
                            badgeIntent.putExtra("badgeIcon", badgeModel.getBadgeIcon());
                            badgeIntent.putExtra("badgeModelHashMap", badgeModelHashMap);
                            startActivity(badgeIntent);
                            overridePendingTransition(R.anim.learningview_slideanimb, R.anim.learningview_slideanim);
                            CourseCompletionWithBadgeActivity.this.finish();
                        }


                    }
                }

                public void onSwipeBottom() {
                    CourseCompletionWithBadgeActivity.this.finish();
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}