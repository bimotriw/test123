package com.oustme.oustsdk.course_ui;

import static android.view.View.VISIBLE;
import static com.oustme.oustsdk.downloadHandler.DownloadForegroundService.START_UPLOAD;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.IS_SPLASH_BACKGROUND_IMAGE_DOWNLOADED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadHandler.DownloadForegroundService;
import com.oustme.oustsdk.feed_ui.adapter.CourseLanguageAdapter;
import com.oustme.oustsdk.feed_ui.adapter.FeedCommentAdapter;
import com.oustme.oustsdk.feed_ui.services.FeedUpdatingServices;
import com.oustme.oustsdk.feed_ui.tools.CoursePresenter;
import com.oustme.oustsdk.feed_ui.tools.CourseView;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.firebase.course.MultilingualCourse;
import com.oustme.oustsdk.layoutFour.components.leaderBoard.CommonLeaderBoardActivity;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.profile.CourseCompletionWithBadgeActivity;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCardColorScheme;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserCardData;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class CourseDetailScreen extends AppCompatActivity implements CourseView {

    private static final String TAG = "CourseDetailScreen";
    private final List<CourseDataClass> courseDataClassList = new ArrayList<>();
    public Comparator<DTOUserLevelData> courseUserCardSorter = (s1, s2) -> (int) s1.getSequece() - (int) s2.getSequece();
    FrameLayout course_detail_lay;
    Toolbar toolbar;
    TextView screen_name;
    ImageView back_button;
    NestedScrollView course_detail_lay_scroll;
    ImageView course_image;
    TextView course_feed_date;
    ImageView course_download;
    TextView course_title_full;
    TextView course_feed_dead_line;
    LinearLayout course_info;
    LinearLayout course_duration_lay;
    TextView course_duration_text;
    LinearLayout course_level_lay;
    TextView course_level_text;
    LinearLayout course_coins_lay;
    TextView course_coins_text;
    TextView course_description;
    WebView course_desc_webView;
    LinearLayout course_attach_lay;
    TextView course_attach_text;
    LinearLayout multilingual_course;
    RecyclerView course_language;
    RelativeLayout course_action_button;
    LinearLayout course_coins_micro_lay;
    TextView course_coins_micro_text;
    TextView course_status_button;
    ImageView img_coin;
    ImageView img_coin_micro;
    ImageView course_delete;
    GifImageView downloadGifImageView;
    RelativeLayout downloadVideoLayout;
    TextView downloadVideoPercentage;

    //Branding loader
    RelativeLayout branding_mani_layout;
    ImageView branding_bg;
    ImageView branding_icon;
    TextView branding_percentage;

    private int color;
    private int bgColor;

    private boolean leaderBoardEnable;

    CourseDataClass courseDataClass;

    AnimationDrawable downloadAnimation;
    private boolean downloadCourseClicked = false;
    private boolean allCourseDownloadStarted = false;
    private int mediaSize = 0;
    boolean isRegularMode = false;
    boolean isMicroCourse = false;
    long userCompletionPercentage;

    private long courseId;
    boolean dialogOpened = false;
    DTOUserFeedDetails.FeedDetails feed;
    private long multilingualCourseId;

    //NewFeed feed;
    boolean isFeedComment, updateComment, isRedirectToBack;
    private boolean courseAlreadyDownloaded = false;
    String courseColnId;
    ActiveUser activeUser;
    private CoursePresenter presenter;
    private DTOUserCourseData mUserCourseData;

    List<MultilingualCourse> multilingualCourseList = new ArrayList<>();
    private HashMap<String, String> languageMap = new HashMap<>();

    private List<DTOUserLevelData> mUserLevelDataList;
    private CourseDownloadReceiver courseDownloadReceiver;
    private MyFileDownLoadReceiver myFileDownLoadReceiver;
    private DownloadReceiver downloadReceiver;
    private HashMap<String, String> landingPageModuleMap;
    private boolean isFeedChange;
    private boolean isArchiveCourse = false;
    boolean enableVideoDownload;
    boolean isEnrolled = false;
    private long taskPosition = -1, taskCompletion;
    private boolean isMicroCoursePlay = false;
    boolean showPastDeadlineModulesOnLandingPage;
    long courseDeadLineTime;
    boolean isInstrumentationHit = false;
    long contentPlayListId = 0;
    int PERMISSION_ALL = 1;
    String courseAddedOn = "";
    String courseDeadLine;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Dialog showDialogDownload;
    int noOfVideos = 0;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        OustSdkTools.setLocale(CourseDetailScreen.this);
        setContentView(R.layout.activity_course_detail_screen);
        try {
            getColors();
            course_detail_lay = findViewById(R.id.course_detail_lay);
            toolbar = findViewById(R.id.toolbar_lay);
            screen_name = findViewById(R.id.screen_name);
            back_button = findViewById(R.id.back_button);
            toolbar.setBackgroundColor(bgColor);
            screen_name.setTextColor(color);
            OustResourceUtils.setDefaultDrawableColor(back_button.getDrawable(), color);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            course_detail_lay_scroll = findViewById(R.id.course_detail_lay_scroll);
            course_image = findViewById(R.id.course_image);
            course_feed_date = findViewById(R.id.course_feed_date);
            course_download = findViewById(R.id.course_download);
            course_title_full = findViewById(R.id.course_title_full);
            course_feed_dead_line = findViewById(R.id.course_feed_dead_line);
            course_info = findViewById(R.id.course_info);
            course_duration_lay = findViewById(R.id.course_duration_lay);
            course_duration_text = findViewById(R.id.course_duration_text);
            course_level_lay = findViewById(R.id.course_level_lay);
            course_level_text = findViewById(R.id.course_level_text);
            course_coins_lay = findViewById(R.id.course_coins_lay);
            course_coins_text = findViewById(R.id.course_coins_text);
            course_description = findViewById(R.id.course_description);
            course_attach_lay = findViewById(R.id.course_attach_lay);
            course_attach_text = findViewById(R.id.course_attach_text);
            multilingual_course = findViewById(R.id.multilingual_course);
            course_language = findViewById(R.id.course_language);
            course_action_button = findViewById(R.id.course_action_button);
            course_status_button = findViewById(R.id.course_status_button);
            course_coins_micro_lay = findViewById(R.id.course_coins_micro_lay);
            course_coins_micro_text = findViewById(R.id.course_coins_micro_text);
            course_desc_webView = findViewById(R.id.course_description_webView);
            course_delete = findViewById(R.id.course_delete);
            downloadGifImageView = findViewById(R.id.download_video_icon);
            downloadVideoPercentage = findViewById(R.id.download_video_text);
            downloadVideoLayout = findViewById(R.id.download_video_layout);


            //Branding loader
            branding_mani_layout = findViewById(R.id.branding_main_layout);
            branding_bg = findViewById(R.id.branding_bg);
            branding_icon = findViewById(R.id.brand_loader);
            branding_percentage = findViewById(R.id.percentage_text);
            //End
            OustPreferences.saveAppInstallVariable("course_download", false);

            try {
                img_coin = findViewById(R.id.img_coin);
                img_coin_micro = findViewById(R.id.img_coin_micro);
                if (OustPreferences.getAppInstallVariable("showCorn")) {
                    img_coin.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                    img_coin_micro.setImageDrawable(OustSdkTools.getImgDrawable(R.drawable.ic_coins_corn));
                } else {
                    img_coin.setImageResource(R.drawable.ic_coins_golden);
                    img_coin_micro.setImageResource(R.drawable.ic_coins_golden);
                }
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
                OustSdkTools.setImage(downloadGifImageView, OustSdkApplication.getContext().getResources().getString(R.string.newlp_notdownload));

            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            screen_name.setText(getResources().getString(R.string.course_text));
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                courseId = OustSdkTools.newConvertToLong(dataBundle.getString("learningId", "0"));
                if (courseId == 0) {
                    courseId = dataBundle.getLong("CourseId");
                }
                try {
                    multilingualCourseId = dataBundle.getLong("multilingualCourseId");
                    taskCompletion = dataBundle.getInt("taskCompletion");
                    taskPosition = dataBundle.getInt("taskPosition");
                    isMicroCoursePlay = dataBundle.getBoolean("isMicroCoursePlay");
                    Log.d("TAG", "onCreate: multilingualCourseId:" + multilingualCourseId + "  --  courseId:" + courseId + " -taskPosition- " + taskPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                courseColnId = dataBundle.getString("courseColnId");
                feed = dataBundle.getParcelable("Feed");
                isFeedComment = dataBundle.getBoolean("FeedComment");
                activeUser = (ActiveUser) dataBundle.getSerializable("ActiveUser");
                landingPageModuleMap = (HashMap<String, String>) dataBundle.getSerializable("deskDataMap");
            }

            if (activeUser == null) {
                activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                OustAppState.getInstance().setActiveUser(activeUser);
            }
            if (activeUser == null) {
                activeUser = OustAppState.getInstance().getActiveUser();
            }
            setIconColors();

            back_button.setOnClickListener(v -> backScreen());

            if (feed != null) {
                if (isFeedComment) {
                    feedComment(feed);
                }
            }
            if (courseId != 0) {
                String id = "" + courseId;
                presenter = new CoursePresenter(CourseDetailScreen.this, id, courseColnId);
                readDataFromFirebaseForCourse(id);
            }

            downloadCourseClicked = false;
            courseAlreadyDownloaded = false;
            course_delete.setOnClickListener(v -> deleteConfirmation());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (activeUser == null) {
                activeUser = OustAppState.getInstance().getActiveUser();
            }
            if (!OustSdkTools.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

            myFileDownLoadReceiver = new MyFileDownLoadReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_COMPLETE);
            intentFilter.addAction(ACTION_ERROR);
            intentFilter.addAction(ACTION_PROGRESS);
            this.registerReceiver(myFileDownLoadReceiver, intentFilter);

            IntentFilter courseDownload_filter = new IntentFilter(CourseDownloadReceiver.PROCESS_RESPONSE);
            courseDownload_filter.addCategory(Intent.CATEGORY_DEFAULT);
            courseDownloadReceiver = new CourseDownloadReceiver();
            this.registerReceiver(courseDownloadReceiver, courseDownload_filter);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            IntentFilter downloadResponse = new IntentFilter(DownloadReceiver.DOWNLOAD_RESPONSE);
            downloadResponse.addCategory(Intent.CATEGORY_DEFAULT);
            downloadReceiver = new DownloadReceiver();
            this.registerReceiver(downloadReceiver, downloadResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setIconColors() {
        try {

            Drawable courseActionDrawable = getResources().getDrawable(R.drawable.course_button_bg);
            course_action_button.setBackground(OustResourceUtils.setDefaultDrawableColor(courseActionDrawable));

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed) {
        try {
            if (!mFeed.isClicked()) {
                long feedId = mFeed.getFeedId();
                String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + AppConstants.StringConstants.IS_FEED_CLICKED;
                OustFirebaseTools.getRootRef().child(message1).setValue(true);
                OustFirebaseTools.getRootRef().child(message1).keepSynced(true);
                DatabaseReference firebase = OustFirebaseTools.getRootRef().child(message1);
                firebase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        currentData.setValue(true);
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

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void readDataFromFirebaseForCourse(final String courseID) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                courseBaseurl = courseBaseurl.replace("{courseId}", "" + courseID);
                courseBaseurl = courseBaseurl.replace("{userId}", "" + activeUser.getStudentid());

                courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                Log.d(TAG, "readDataFromFirebaseForCourse: " + courseBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "readDataFromFirebaseForCourse onResponse: " + response.toString());
                        Map<String, Object> courseMap = new HashMap<>();
                        ObjectMapper mapper = new ObjectMapper();
                        CommonTools commonTools = new CommonTools();
                        try {
                            courseMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            backScreen();
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if (courseMap != null) {
                            if ((courseMap.get("courseId") != null)) {
                                CommonLandingData commonLandingData = new CommonLandingData();
                                commonLandingData.setId("COURSE" + courseID);
                                commonLandingData = commonTools.getCourseCommonData(courseMap, commonLandingData);
                                setCourseData(commonLandingData);
                                extractCourseData(courseMap);
                            } else {
                                Toast.makeText(CourseDetailScreen.this, getResources().getString(R.string.module_no_longer), Toast.LENGTH_SHORT).show();
                                backScreen();
                            }
                        } else {
                            Toast.makeText(CourseDetailScreen.this, getResources().getString(R.string.module_no_longer), Toast.LENGTH_SHORT).show();
                            backScreen();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        backScreen();
                    }
                });
            } else {
                OustSdkTools.showToast(getResources().getString(R.string.please_check_your_internet_connection));
                backScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void feedComment(DTOUserFeedDetails.FeedDetails feed) {
        updateComment = true;
        Dialog dialog = new Dialog(CourseDetailScreen.this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_dialog);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView total_comments_text = dialog.findViewById(R.id.total_comments_text);
        ImageView comment_close = dialog.findViewById(R.id.comment_close);
        EditText comment_text = dialog.findViewById(R.id.comment_text);
        ImageButton send_comment_button = dialog.findViewById(R.id.send_comment_button);
        TextView no_comments = dialog.findViewById(R.id.no_comments);
        RecyclerView comment_list_rv = dialog.findViewById(R.id.comment_list_rv);

        Drawable feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);
        DrawableCompat.setTint(
                DrawableCompat.wrap(feedSendDrawable),
                OustResourceUtils.getColors()
        );
        send_comment_button.setBackground(feedSendDrawable);


        try {
            final String message = "/userFeedComments/" + "feed" + feed.getFeedId();
            ValueEventListener commentsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> allCommentsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (allCommentsMap != null) {
                            ArrayList<AlertCommentData> commentsList = new ArrayList<>();

                            for (String commentKey : allCommentsMap.keySet()) {
                                Object commentDataObject = allCommentsMap.get(commentKey);
                                final Map<String, Object> commentsDataMap = (Map<String, Object>) commentDataObject;
                                if (commentsDataMap != null) {

                                    AlertCommentData alertCommentData = new AlertCommentData();
                                    if (commentsDataMap.get("addedOnDate") != null) {
                                        alertCommentData.setAddedOnDate(OustSdkTools.newConvertToLong(commentsDataMap.get("addedOnDate")));
                                    }

                                    if (commentsDataMap.get("comment") != null) {
                                        alertCommentData.setComment((String) commentsDataMap.get("comment"));
                                    }
                                    if (commentsDataMap.get("userAvatar") != null) {
                                        alertCommentData.setUserAvatar((String) commentsDataMap.get("userAvatar"));
                                    }
                                    if (commentsDataMap.get("userDisplayName") != null) {
                                        alertCommentData.setUserDisplayName((String) commentsDataMap.get("userDisplayName"));
                                    }
                                    if (commentsDataMap.get("userId") != null) {
                                        alertCommentData.setUserId((String) commentsDataMap.get("userId"));
                                    }
                                    if (commentsDataMap.get("userKey") != null) {
                                        alertCommentData.setUserKey(OustSdkTools.newConvertToLong(commentsDataMap.get("userKey")));
                                    }
                                    commentsList.add(alertCommentData);

                                }
                            }

                            String totalComments = "";


                            if (commentsList.size() != 0) {
                                comment_list_rv.setVisibility(VISIBLE);
                                no_comments.setVisibility(View.GONE);
                                Collections.sort(commentsList, AlertCommentData.commentSorter);
                                FeedCommentAdapter feedCommentAdapter = new FeedCommentAdapter(CourseDetailScreen.this, commentsList, activeUser);
                                comment_list_rv.setItemAnimator(new DefaultItemAnimator());
                                comment_list_rv.setAdapter(feedCommentAdapter);
                                if (commentsList.size() > 1) {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comments_text);
                                } else {
                                    totalComments = commentsList.size() + " " + getResources().getString(R.string.comment_text);
                                }

                                if (!updateComment) {
                                    feed.setNumComments(commentsList.size());
                                    feed.setCommented(true);
                                    Intent intent = new Intent(CourseDetailScreen.this, FeedUpdatingServices.class);
                                    intent.putExtra("FeedId", feed.getFeedId());
                                    intent.putExtra("FeedCommentSize", commentsList.size());
                                    startService(intent);

                                }
                                total_comments_text.setText(totalComments);
                            } else {
                                updateComment = false;
                                comment_list_rv.setVisibility(View.GONE);
                                no_comments.setVisibility(VISIBLE);
                            }
                            updateComment = false;
                            total_comments_text.setText(totalComments);

                        } else {
                            updateComment = false;
                        }
                    } else {
                        updateComment = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(commentsListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(commentsListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        comment_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String comment_send_text = comment_text.getText().toString();
                Drawable feedSendDrawable;


                if (comment_send_text.isEmpty()) {
                    send_comment_button.setEnabled(false);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_unselected);
                } else {
                    send_comment_button.setEnabled(true);
                    feedSendDrawable = getResources().getDrawable(R.drawable.ic_send_selected);
                }

                send_comment_button.setBackground(OustResourceUtils.setDefaultDrawableColor(feedSendDrawable));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send_comment_button.setOnClickListener(v -> {

            String comment = comment_text.getText().toString();
            if (!comment.isEmpty()) {

                AlertCommentData alertCommentData = new AlertCommentData();
                alertCommentData.setComment(comment);
                alertCommentData.setAddedOnDate(System.currentTimeMillis());
                alertCommentData.setDevicePlatform("Android");
                alertCommentData.setUserAvatar(activeUser.getAvatar());
                alertCommentData.setUserId(activeUser.getStudentid());
                alertCommentData.setUserKey(OustSdkTools.newConvertToLong(activeUser.getStudentKey()));
                alertCommentData.setUserDisplayName(activeUser.getUserDisplayName());
                alertCommentData.setNumReply(0);

                isFeedChange = true;
                sendCommentToFirebase(alertCommentData, "" + feed.getFeedId());
                comment_text.setText("");

            }

        });

        comment_close.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

    }

    private void sendCommentToFirebase(AlertCommentData alertCommentData, String feedId) {
        String message = "/userFeedComments/x" + "feed" + feedId;
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(alertCommentData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        sendToUserFeedThread(newPostRef.getKey(), feedId);
        updateFeedViewed(feed);
        // updateCommentCount(feedId);
    }

    private void sendToUserFeedThread(String key, String feedId) {
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "commentThread/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

    }


    private void setCourseData(CommonLandingData courseData) {
        try {
            if (courseData.getCourseType() != null && courseData.getCourseType().equalsIgnoreCase("Multilingual")) {
                course_description.setVisibility(View.GONE);
                course_desc_webView.setVisibility(View.GONE);
                course_info.setVisibility(View.GONE);
                leaderBoardEnable = false;
                course_download.setVisibility(View.GONE);
                downloadVideoLayout.setVisibility(View.GONE);
                multilingual_course.setVisibility(VISIBLE);

                multilingualCourseList = courseData.getMultilingualCourseListList();
                getLanguageMapFromFirebase();

                course_action_button.setOnClickListener(v -> {
                    if (isArchiveCourse) {
                        Toast.makeText(CourseDetailScreen.this, "" + getResources().getString(R.string.sorry_archive_by_admin), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent feedIntent = new Intent(CourseDetailScreen.this, CourseDetailScreen.class);
                        feedIntent.putExtra("CourseId", OustSdkTools.newConvertToLong(OustPreferences.get("FeedCourseId")));
                        Bundle feedBundle = new Bundle();
                        feedBundle.putParcelable("Feed", feed);
                        feedBundle.putBoolean("FeedComment", false);
                        feedBundle.putSerializable("ActiveUser", activeUser);
                        feedBundle.putSerializable("deskDataMap", landingPageModuleMap);
                        feedIntent.putExtra("catalog_id", "" + OustSdkTools.newConvertToLong(OustPreferences.get("FeedCourseId")));
                        feedIntent.putExtra("catalog_type", "COURSE");
                        Log.d("TAG", "setCourseData: multilingualCourseId:" + courseId);
                        feedIntent.putExtra("multilingualCourseId", courseId);
                        feedIntent.putExtras(feedBundle);
                        startActivity(feedIntent);
                        isRedirectToBack = true;
                        branding_mani_layout.setVisibility(VISIBLE);
                        toolbar.setVisibility(View.GONE);
                    }
                });

                invalidateOptionsMenu();

            } else {
                course_info.setVisibility(VISIBLE);
                course_description.setVisibility(VISIBLE);
                multilingual_course.setVisibility(View.GONE);

                course_action_button.setOnClickListener(v -> {
                    try {
                        if (isArchiveCourse) {
                            Toast.makeText(CourseDetailScreen.this, "" + getResources().getString(R.string.sorry_archive_by_admin), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("TAG", "course_action_button: ");
                            branding_mani_layout.setVisibility(VISIBLE);

                            if (multilingualCourseId > 0) {
                                toolbar.setVisibility(View.GONE);
                                //TODO
                                distributeChildCourseForMultipleCourse("" + courseId, activeUser.getStudentid(), "" + multilingualCourseId);
                                //enrolledLp("" + courseId, activeUser.getStudentid(), "" + multilingualCourseId);
                            } else {
                                openLearningMapPage("" + courseId);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                });

                course_download.setOnClickListener(v -> {
                    try {
                        downloadAnimation = (AnimationDrawable) course_download.getDrawable();
                        OustPreferences.saveAppInstallVariable("course_download", true);
                        if (enableVideoDownload && noOfVideos > 0) {
                            OustSdkTools.setDownloadGifImage(downloadGifImageView);
                            showDialogForDownload();
                        } else {
                            downloadVideoLayout.setVisibility(View.VISIBLE);
                            course_delete.setVisibility(View.GONE);
                            course_download.setVisibility(View.GONE);
                            downloadVideoPercentage.setText("0");
                            OustSdkTools.setDownloadGifImage(downloadGifImageView);
                            startCourseDownload();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                });

            }

            setCourseDeadline(courseDataClass);


            course_title_full.setText(courseData.getName());

            if (courseData.getDescription() != null && !courseData.getDescription().trim().isEmpty()) {
                if (courseData.getDescription().contains("<li>") || courseData.getDescription().contains("</li>") ||
                        courseData.getDescription().contains("<ol>") || courseData.getDescription().contains("</ol>") ||
                        courseData.getDescription().contains("<p>") || courseData.getDescription().contains("</p>")) {
                    if (courseData.getCourseType() != null && courseData.getCourseType().equalsIgnoreCase("Multilingual")) {
                        course_desc_webView.setVisibility(View.GONE);
                    } else {
                        course_desc_webView.setVisibility(VISIBLE);
                    }
                    course_description.setVisibility(View.GONE);
                    course_desc_webView.setBackgroundColor(Color.TRANSPARENT);
                    String text = OustSdkTools.getDescriptionHtmlFormat(courseData.getDescription());
                    final WebSettings webSettings = course_desc_webView.getSettings();
                    // Set the font size (in sp).
                    webSettings.setDefaultFontSize(18);
                    course_desc_webView.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        course_description.setText(Html.fromHtml(courseData.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        course_description.setText(Html.fromHtml(courseData.getDescription()));
                    }
                }
            }


            if (courseData.getTime() != 0) {
                String courseDurationText = OustSdkTools.getTime(courseData.getTime());
                course_duration_text.setText(courseDurationText + " " + getResources().getString(R.string.minute));
            } else {
                String courseDurationText = "1 " + getResources().getString(R.string.minute);
                course_duration_text.setText(courseDurationText);
            }
            String courseType;
            if (courseData.getCourseType() != null) {
                courseType = courseData.getCourseType();
                if (courseType.equalsIgnoreCase("MICROLEARNING")) {
                    course_level_lay.setVisibility(View.GONE);
                    course_coins_micro_lay.setVisibility(VISIBLE);
                    course_coins_lay.setVisibility(View.GONE);
                } else {
                    course_level_lay.setVisibility(VISIBLE);
                    course_coins_lay.setVisibility(VISIBLE);
                    course_coins_micro_lay.setVisibility(View.GONE);
                }
            } else {
                course_level_lay.setVisibility(VISIBLE);
                course_coins_lay.setVisibility(VISIBLE);
                course_coins_micro_lay.setVisibility(View.GONE);
            }

            String levelText = "1 " + getResources().getString(R.string.level).toLowerCase();
            if (courseData.getCourseLevelsSize() > 1) {
                levelText = courseData.getCourseLevelsSize() + " " + getResources().getString(R.string.levels).toLowerCase();
            }
            Log.e("TAG", "setCourseData: -> " + isMicroCourse + " i-> " + isMicroCoursePlay);
            course_level_text.setText(levelText);

            if (courseData.getTotalOc() > 0) {
                String coin_text = courseData.getTotalOc() + " " + getResources().getString(R.string.coins_text).toLowerCase();
                course_coins_text.setText(coin_text);
                course_coins_micro_text.setText(coin_text);
            } else {
                course_coins_lay.setVisibility(View.GONE);
                course_coins_micro_lay.setVisibility(View.GONE);
            }

            if (courseData.getBanner() != null && !courseData.getBanner().isEmpty()) {
                Glide.with(CourseDetailScreen.this).load(courseData.getBanner()).into(course_image);
            } else if (courseData.getIcon() != null && !courseData.getIcon().isEmpty()) {
                Glide.with(CourseDetailScreen.this).load(courseData.getIcon()).into(course_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showDialogForDownload() {
        try {
            showDialogDownload = new Dialog(CourseDetailScreen.this, R.style.DialogTheme);
            showDialogDownload.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showDialogDownload.setContentView(R.layout.common_pop_up);
            showDialogDownload.setCancelable(false);
            Objects.requireNonNull(showDialogDownload.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            showDialogDownload.show();

            ImageView info_image = showDialogDownload.findViewById(R.id.info_image);
            TextView info_title = showDialogDownload.findViewById(R.id.info_title);
            TextView info_description = showDialogDownload.findViewById(R.id.info_description);
            LinearLayout info_cancel = showDialogDownload.findViewById(R.id.info_no);
            TextView info_cancel_text = showDialogDownload.findViewById(R.id.info_no_text);
            LinearLayout save_userData = showDialogDownload.findViewById(R.id.info_yes);
            TextView info_save_text = showDialogDownload.findViewById(R.id.info_yes_text);

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);
            String infoDescription = getResources().getString(R.string.retry_internet_msg);
            String downloadContent = "This course contains " + noOfVideos + " videos. Are you okay to download the course with videos?";
            if (noOfVideos == 1) {
                downloadContent = "This course contains " + noOfVideos + " video. Are you okay to download the course with video?";
            }
            info_title.setText(downloadContent);
            info_description.setText(infoDescription);
            info_description.setVisibility(View.GONE);
            info_image.setImageDrawable(infoDrawable);

            Drawable reviewDrawable = getResources().getDrawable(R.drawable.white_btn_bg);
            info_cancel.setBackground(reviewDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            save_userData.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_cancel_text.setTextColor(OustResourceUtils.getColors());

            info_cancel_text.setText(getResources().getString(R.string.stream).toUpperCase());
            info_save_text.setText(getResources().getString(R.string.download).toUpperCase());

            info_cancel.setOnClickListener(view -> {
                downloadVideoLayout.setVisibility(View.VISIBLE);
                course_delete.setVisibility(View.GONE);
                course_download.setVisibility(View.GONE);
                downloadVideoPercentage.setText("0");
                showDialogDownload.dismiss();
                enableVideoDownload = true;
                courseDataClass.setEnableVideoDownload(false);
                startCourseDownload();
            });

            save_userData.setOnClickListener(view -> {
                downloadVideoLayout.setVisibility(View.VISIBLE);
                course_delete.setVisibility(View.GONE);
                course_download.setVisibility(View.GONE);
                downloadVideoPercentage.setText("0");
                showDialogDownload.dismiss();
                enableVideoDownload = true;
                dialogOpened = true;
                courseDataClass.setEnableVideoDownload(true);
                startCourseDownload();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void distributeChildCourseForMultipleCourse(final String lpId, final String studentId, final String multilingualID) {
        try {
            String distribution_url = OustSdkApplication.getContext().getResources().getString(R.string.distribut_course_url);
            distribution_url = distribution_url.replace("{courseId}", lpId);
            distribution_url = HttpManager.getAbsoluteUrl(distribution_url);
            JSONObject jsonParams = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(studentId);
            jsonParams.put("users", jsonArray);
            jsonParams.put("reusabilityAllowed", true);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        final CommonResponse[] commonResponses = new CommonResponse[]{null};
                        if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                            String distributedId = "" + response.optLong("distributedId");
                            enrolledLp(distributedId, studentId, multilingualID);
                        } else {
                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                            if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                if (commonResponses[0].getExceptionData().getMessage() != null) {
                                    OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                } else {
                                    OustSdkTools.showToast(getResources().getString(R.string.error_message));
                                }
                            } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                OustSdkTools.showToast(commonResponses[0].getError());
                            } else {
                                OustSdkTools.showToast(getResources().getString(R.string.error_message));
                            }
                            toolbar.setVisibility(VISIBLE);
                            branding_mani_layout.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        toolbar.setVisibility(VISIBLE);
                        branding_mani_layout.setVisibility(View.GONE);
                        OustSdkTools.showToast(getResources().getString(R.string.error_message));
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    toolbar.setVisibility(VISIBLE);
                    branding_mani_layout.setVisibility(View.GONE);
                    OustSdkTools.showToast(getResources().getString(R.string.error_message));
                }
            });

        } catch (Exception e) {
            toolbar.setVisibility(VISIBLE);
            branding_mani_layout.setVisibility(View.GONE);
            OustSdkTools.showToast(getResources().getString(R.string.error_message));
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void enrolledLp(final String lpId, final String studentId, final String multilingualID) {
        final CommonResponse[] commonResponses = new CommonResponse[]{null};
        try {
            String enrolllp_url = OustSdkApplication.getContext().getResources().getString(R.string.enrolllp_url);
            enrolllp_url = enrolllp_url.replace("{courseId}", ("" + lpId));
            enrolllp_url = enrolllp_url.replace("{userId}", studentId);
            enrolllp_url = enrolllp_url.replace("{courseColnId}", "");

            if (multilingualID != null && !multilingualID.isEmpty()) {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", multilingualID);
            } else {
                enrolllp_url = enrolllp_url.replace("{mlCourseId}", "");
            }
            try {
                PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                Log.e(TAG, enrolllp_url);
                enrolllp_url = enrolllp_url + "&devicePlatformName=Android";
                Log.e(TAG, enrolllp_url);
                enrolllp_url = enrolllp_url + "&appVersion=" + pinfo.versionName;
                Log.e(TAG, enrolllp_url);
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
                Log.e(TAG, enrolllp_url);
            }
            enrolllp_url = HttpManager.getAbsoluteUrl(enrolllp_url);

            String finalEnrolllp_url = enrolllp_url;
            Log.d(TAG, "enrolledLp: --> " + enrolllp_url);
            ApiCallUtils.doNetworkCall(Request.Method.POST, enrolllp_url, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                        if (commonResponses[0].isSuccess()) {
                            OustPreferences.save("removeMultiLingual", multilingualID);
                            new Handler().postDelayed(() -> openLearningMapPage(lpId), 1500);
                            if (multilingualID != null && !multilingualID.isEmpty()) {
                                OustSdkTools.removeReminderNotification(multilingualID);
                            }
                        } else {
                            InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                            instrumentationMailRequest.setModuleType("COURSE");
                            instrumentationMailRequest.setModuleId(courseId);
                            instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrolllp_url + "\n Error message : Response returning success as false");
                            instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                            InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                            instrumentationHandler.hitInstrumentationAPI(CourseDetailScreen.this, instrumentationMailRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                        instrumentationMailRequest.setModuleType("COURSE");
                        instrumentationMailRequest.setModuleId(courseId);
                        instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrolllp_url + "\n Error message : " + e.getMessage());
                        instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                        InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                        instrumentationHandler.hitInstrumentationAPI(CourseDetailScreen.this, instrumentationMailRequest);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null && error.getMessage() != null) {
                        Log.d("Network_error", "" + error.getMessage());
                        InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                        instrumentationMailRequest.setModuleType("COURSE");
                        instrumentationMailRequest.setModuleId(courseId);
                        instrumentationMailRequest.setMessageDesc("Course enrollment is not happening. Api details : " + finalEnrolllp_url + "\n API Error message : " + error.getMessage());
                        instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_ENROLLMENT_ISSUE.toString());
                        InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                        instrumentationHandler.hitInstrumentationAPI(CourseDetailScreen.this, instrumentationMailRequest);
                    }
                    enrollFail();
                }
            });

        } catch (Exception e) {
            Log.d("Enroll detail", "");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            enrollFail();
        }
    }

    private void enrollFail() {
        toolbar.setVisibility(VISIBLE);
        branding_mani_layout.setVisibility(View.GONE);
        OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
    }

    private void openLearningMapPage(String lpId) {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnNextArrow", true);
            eventUpdate.put("CourseID", lpId);
            eventUpdate.put("Course Name", courseDataClass.getCourseName());
            Log.d("TAG", "CleverTap instance: " + eventUpdate);

            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Course_Enroll", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            Intent courseLearningIntent = new Intent(CourseDetailScreen.this, CourseLearningMapActivity.class);
            Bundle courseBundle = new Bundle();
            courseBundle.putBoolean("isRegularMode", isRegularMode);
            courseBundle.putLong("learningId", OustSdkTools.newConvertToLong(lpId));
            courseBundle.putBoolean("isMicroCourse", isMicroCourse);
            courseBundle.putBoolean("isMicroCoursePlay", isMicroCoursePlay);

            if (multilingualCourseId > 0) {
                courseBundle.putString("multilingualId", "" + multilingualCourseId);
            }
            courseLearningIntent.putExtras(courseBundle);
            startActivity(courseLearningIntent);
            overridePendingTransition(0, 0);

            toolbar.setVisibility(View.GONE);
            isRedirectToBack = true;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getLanguageMapFromFirebase() {
        try {
            String message = "system/availableLanguages/";
            ValueEventListener learningMapListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (null != dataSnapshot.getValue()) {
                        Object o1 = dataSnapshot.getValue();
                        if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                            List<String> availableLanguages = (List<String>) dataSnapshot.getValue();
                            for (int i = 0; i < availableLanguages.size(); i++) {
                                languageMap.put("" + i, availableLanguages.get(i));
                            }
                        } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                            languageMap = (HashMap<String, String>) dataSnapshot.getValue();
                        } else if (o1 != null) {
                            languageMap = (HashMap<String, String>) dataSnapshot.getValue();
                        }
                    }
                    getAllCourseDetailsFromFirebase();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError DatabaseError) {
                    getAllCourseDetailsFromFirebase();
                }
            };
            OustFirebaseTools.getRootRef().child(message).addListenerForSingleValueEvent(learningMapListener);
            OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        } catch (Exception e) {
            getAllCourseDetailsFromFirebase();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAllCourseDetailsFromFirebase() {
        if (multilingualCourseList != null && multilingualCourseList.size() > 0) {
            for (int i = 0; i < multilingualCourseList.size(); i++) {
                String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
                courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                courseBaseurl = courseBaseurl.replace("{courseId}", "" + multilingualCourseList.get(i).getCourseId());
                courseBaseurl = courseBaseurl.replace("{userId}", "" + activeUser.getStudentid());

                courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                Log.d(TAG, "getAllCourseDetailsFromFirebase: " + courseBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Map<String, Object> learningMap = new HashMap<>();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        if (learningMap != null) {
                            extractCourseData(learningMap);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        } else {
            OustSdkTools.showToast(getResources().getString(R.string.no_data_contact_admin));
            this.finish();
        }
    }

    private void extractCourseData(final Map<String, Object> learningMap) {
        try {
            if (learningMap != null) {
                noOfVideos = 0;
                OustPreferences.saveintVar("video_count", 0);
                courseDataClass = new CourseDataClass();
                if (learningMap.get("courseId") != null) {
                    courseDataClass.setCourseId(OustSdkTools.newConvertToLong(learningMap.get("courseId")));
                }

                String attachment_text = "";
                if (learningMap.get("mappedAssessmentId") != null) {
                    courseDataClass.setMappedAssessmentId(OustSdkTools.newConvertToLong(learningMap.get("mappedAssessmentId")));
                    if (courseDataClass.getMappedAssessmentId() != 0) {
                        attachment_text = getResources().getString(R.string.assessment);
                    }
                }

                if (learningMap.get("mappedSurveyId") != null) {
                    courseDataClass.setMappedSurveyId(OustSdkTools.newConvertToLong(learningMap.get("mappedSurveyId")));

                    if (courseDataClass.getMappedSurveyId() != 0) {
                        if (courseDataClass.getMappedAssessmentId() != 0) {
                            attachment_text = attachment_text + ",";
                        }
                        attachment_text = attachment_text + getResources().getString(R.string.survey_text);
                    }
                }

                if (learningMap.get("contentPlayListId") != null) {
                    contentPlayListId = OustSdkTools.newConvertToLong(learningMap.get("contentPlayListId"));
                }

                if (learningMap.get("cplId") != null) {
                    contentPlayListId = OustSdkTools.newConvertToLong(learningMap.get("cplId"));
                }

                if (learningMap.get("enrolled") != null) {
                    isEnrolled = ((boolean) learningMap.get("enrolled"));
                }

                if (learningMap.get("addedOn") != null) {
                    courseAddedOn = (String) learningMap.get("addedOn");
                    String courseAddedOnStr = OustSdkTools.milliToDate((String) learningMap.get("addedOn"));
                    if (!courseAddedOnStr.isEmpty()) {
                        course_feed_date.setVisibility(VISIBLE);
                        course_feed_date.setText(courseAddedOnStr);
                    }
                }
                if (!attachment_text.isEmpty()) {
                    attachment_text = getResources().getString(R.string.attachment) + ":" + attachment_text;
                    course_attach_lay.setVisibility(VISIBLE);
                    course_attach_text.setText(attachment_text);
                }

                if (learningMap.get("courseName") != null) {
                    courseDataClass.setCourseName((String) learningMap.get("courseName"));
                }
                if (learningMap.get("icon") != null) {
                    courseDataClass.setIcon((String) learningMap.get("icon"));
                }
                if (learningMap.get("bgImg") != null) {
                    courseDataClass.setBgImg((String) learningMap.get("bgImg"));
                }

                if (learningMap.get("hideLeaderboard") != null) {
                    courseDataClass.setHideLeaderBoard((boolean) learningMap.get("hideLeaderboard"));
                } else {
                    courseDataClass.setHideLeaderBoard(false);
                }

                if (learningMap.get("enableVideoDownload") != null) {
                    enableVideoDownload = (boolean) learningMap.get("enableVideoDownload");
                    courseDataClass.setEnableVideoDownload(enableVideoDownload);
                }

                if (OustPreferences.getAppInstallVariable("enableVideoDownload") || courseDataClass.isEnableVideoDownload()) {
                    enableVideoDownload = true;
                    courseDataClass.setEnableVideoDownload(true);
                }
                if (learningMap.get("mode") != null) {
                    courseDataClass.setMode((String) learningMap.get("mode"));
                    if (courseDataClass.getMode() != null && courseDataClass.getMode().equalsIgnoreCase("ARCHIVED")) {
                        isArchiveCourse = true;
                    }
                }

                if (!OustPreferences.getAppInstallVariable("hideAllCourseLeaderBoard") && !courseDataClass.isHideLeaderBoard()) {
                    leaderBoardEnable = true;
                    invalidateOptionsMenu();
                }
                courseDataClass.setNumEnrolledUsers(OustSdkTools.newConvertToLong(learningMap.get("numEnrolledUsers")));
                courseDataClass.setTotalOc(OustSdkTools.newConvertToLong(learningMap.get("totalOc")));

                if (multilingualCourseList != null && languageMap != null && languageMap.size() > 0) {
                    for (int i = 0; i < multilingualCourseList.size(); i++) {
                        if (courseDataClass.getCourseId() == multilingualCourseList.get(i).getCourseId()) {
                            if (languageMap.containsKey("" + multilingualCourseList.get(i).getLangId())) {
                                courseDataClass.setLanguage("" + languageMap.get("" + multilingualCourseList.get(i).getLangId()));
                                //langAvailable.put(languageMap.get("" + multilingualCourseList.get(i).getLangId()), "available");
                                break;
                            }
                        }
                    }
                }

                if (learningMap.get("completionDeadline") != null) {
                    courseDeadLine = (String) learningMap.get("completionDeadline");
                    courseDataClass.setCourseDeadline((String) learningMap.get("completionDeadline"));
                }
                if (learningMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                    courseDataClass.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.newConvertToLong(learningMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
                }
                if (learningMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                    courseDataClass.setShowPastDeadlineModulesOnLandingPage((boolean) learningMap.get("showPastDeadlineModulesOnLandingPage"));
                    showPastDeadlineModulesOnLandingPage = (boolean) learningMap.get("showPastDeadlineModulesOnLandingPage");
                }

                if (learningMap.get("showQuestionSymbolForQuestion") != null) {
                    courseDataClass.setShowQuestionSymbolForQuestion((boolean) learningMap.get("showQuestionSymbolForQuestion"));
                }

                Object o1 = learningMap.get("levels");
                if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                    List<Object> levelsList = (List<Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null && levelsList.size() > 0) {
                        for (int i = 0; i < levelsList.size(); i++) {
                            if (levelsList.get(i) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLpId((int) courseId);
                                if (levelMap.get("levelId") != null) {
                                    courseLevelClass.setLevelId(OustSdkTools.newConvertToLong(levelMap.get("levelId")));
                                }
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                    //  courseTotalOc += (OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }

                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                Log.e("Level Lock", "Level number unlocked " + i + " " + courseLevelClass.isLevelLock());
                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) levelMap.get("cards");
                                        if (cardMap != null) {
                                            for (String key : cardMap.keySet()) {
                                                if (cardMap.get(key) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    assert cardsubMap != null;
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType;
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                    }

                                                    if (cardsubMap.get("isIfScormEventBased") != null) {
                                                        courseCardClass.setIfScormEventBased((boolean) cardsubMap.get("isIfScormEventBased"));
                                                    } else {
                                                        courseCardClass.setIfScormEventBased(false);
                                                    }


                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                    }
                                                    Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                    if (cardColorScheme != null) {
                                                        DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                        if (cardColorScheme.get("contentColor") != null) {
                                                            cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                        }
                                                        if (cardColorScheme.get("bgImage") != null) {
                                                            cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                        }
                                                        if (cardColorScheme.get("iconColor") != null) {
                                                            cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                        }
                                                        if (cardColorScheme.get("levelNameColor") != null) {
                                                            cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                        }
                                                        courseCardClass.setCardColorScheme(cardColorScheme1);
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                    try {
                                                        Object oo2 = cardsubMap.get("cardMedia");
                                                        if (oo2 != null) {
                                                            if (oo2.getClass().equals(ArrayList.class)) {
                                                                List<Object> cardList = (List<Object>) cardsubMap.get("cardMedia");
                                                                if (cardList != null) {
                                                                    for (int l = 0; l < cardList.size(); l++) {
                                                                        if (cardList.get(l) != null) {
                                                                            if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                                noOfVideos++;
                                                                            }
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
                                            }
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    String cardType;
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                    }
                                                    Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                    if (cardColorScheme != null) {
                                                        DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                        if (cardColorScheme.get("contentColor") != null) {
                                                            cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                        }
                                                        if (cardColorScheme.get("bgImage") != null) {
                                                            cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                        }
                                                        if (cardColorScheme.get("iconColor") != null) {
                                                            cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                        }
                                                        if (cardColorScheme.get("levelNameColor") != null) {
                                                            cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                        }
                                                        courseCardClass.setCardColorScheme(cardColorScheme1);
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                    try {
                                                        Object oo2 = cardsubMap.get("cardMedia");
                                                        if (oo2 != null) {
                                                            if (oo2.getClass().equals(ArrayList.class)) {
                                                                List<Object> cardList1 = (List<Object>) cardsubMap.get("cardMedia");
                                                                if (cardList1 != null) {
                                                                    for (int l1 = 0; l1 < cardList1.size(); l1++) {
                                                                        if (cardList1.get(l1) != null) {
                                                                            if (Objects.equals(((HashMap) cardList1.get(l1)).get("mediaType"), "VIDEO") && ((HashMap) cardList1.get(l1)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                                noOfVideos++;
                                                                            }
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
                                            }
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0 && !courseLevelClassList.get(0).isLevelLock()) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        presenter.loadUserDataFromFirebase((int) courseId, courseDataClass, null, courseColnId);
                    }
                } else if (o1 != null && o1.getClass().equals(HashMap.class)) {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null && levelsList.size() > 0) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId((int) courseId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }
                                Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType;
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardsubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardsubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
                                                                        }
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
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType;
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        presenter.loadUserDataFromFirebase((int) courseId, courseDataClass, null, courseColnId);
                    }
                } else {
                    Map<String, Object> levelsList = (Map<String, Object>) learningMap.get("levels");
                    List<CourseLevelClass> courseLevelClassList = new ArrayList<>();
                    if (levelsList != null && levelsList.size() > 0) {
                        for (String s1 : levelsList.keySet()) {
                            if (levelsList.get(s1) != null) {
                                final Map<String, Object> levelMap = (Map<String, Object>) levelsList.get(s1);
                                CourseLevelClass courseLevelClass = new CourseLevelClass();
                                courseLevelClass.setLevelId(Integer.parseInt(s1));
                                courseLevelClass.setLpId((int) courseId);
                                assert levelMap != null;
                                if (levelMap.get("levelDescription") != null) {
                                    courseLevelClass.setLevelDescription((String) levelMap.get("levelDescription"));
                                }
                                if (levelMap.get("downloadStratergy") != null) {
                                    courseLevelClass.setDownloadStratergy((String) levelMap.get("downloadStratergy"));
                                }
                                if (levelMap.get("xp") != null) {
                                    courseLevelClass.setTotalXp(OustSdkTools.newConvertToLong(levelMap.get("xp")));
                                }
                                if (levelMap.get("levelMode") != null) {
                                    courseLevelClass.setLevelMode((String) levelMap.get("levelMode"));
                                }
                                if (levelMap.get("levelThumbnail") != null) {
                                    courseLevelClass.setLevelThumbnail((String) levelMap.get("levelThumbnail"));
                                }
                                if (levelMap.get("levelName") != null) {
                                    courseLevelClass.setLevelName((String) levelMap.get("levelName"));
                                }
                                if (levelMap.get("hidden") != null) {
                                    courseLevelClass.setHidden((boolean) levelMap.get("hidden"));
                                }
                                if (levelMap.get("sequence") != null) {
                                    courseLevelClass.setSequence(OustSdkTools.newConvertToLong(levelMap.get("sequence")));
                                }
                                if (levelMap.get("totalOc") != null) {
                                    courseLevelClass.setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("totalOc")));
                                }
                                if (levelMap.get("updateTime") != null) {
                                    courseLevelClass.setRefreshTimeStamp((String) levelMap.get("updateTime"));
                                }
                                courseLevelClass.setLevelLock(false);
                                if (levelMap.get("levelLock") != null) {
                                    courseLevelClass.setLevelLock((boolean) levelMap.get("levelLock"));
                                }

                                Log.e("Level Lock", "Level number unlocked " + Integer.parseInt(s1) + " " + courseLevelClass.isLevelLock());

                                List<DTOCourseCard> courseCardClassList = new ArrayList<>();
                                if (levelMap.get("cards") != null) {
                                    Object o2 = levelMap.get("cards");
                                    assert o2 != null;
                                    if (o2.getClass().equals(HashMap.class)) {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardsubMap != null;
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }

                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType;
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }
                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardsubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardsubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
                                                                        }
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
                                        }
                                    } else if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> cardList = (List<Object>) levelMap.get("cards");
                                        if (cardList != null) {
                                            for (int l = 0; l < cardList.size(); l++) {
                                                if (cardList.get(l) != null) {
                                                    final Map<String, Object> cardsubMap = (Map<String, Object>) cardList.get(l);
                                                    DTOCourseCard courseCardClass = new DTOCourseCard();
                                                    if (cardsubMap.get("cardBgColor") != null) {
                                                        courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                    }
                                                    if (cardsubMap.get("cardQuestionColor") != null) {
                                                        courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                    }
                                                    if (cardsubMap.get("cardSolutionColor") != null) {
                                                        courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                    }
                                                    if (cardsubMap.get("cardTextColor") != null) {
                                                        courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                    }
                                                    String cardType;
                                                    if (cardsubMap.get("cardType") != null) {
                                                        cardType = (String) cardsubMap.get("cardType");
                                                        courseCardClass.setCardType(cardType);
                                                    }
                                                    if (cardsubMap.get("questionCategory") != null) {
                                                        courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                    }
                                                    if (cardsubMap.get("questionType") != null) {
                                                        courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                    }
                                                    if (cardsubMap.get("content") != null) {
                                                        courseCardClass.setContent((String) cardsubMap.get("content"));
                                                    }
                                                    if (cardsubMap.get("cardTitle") != null) {
                                                        courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                    }
                                                    if (cardsubMap.get("qId") != null) {
                                                        courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                    }
                                                    if (cardsubMap.get("cardId") != null) {
                                                        courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                    }
                                                    if (cardsubMap.get("xp") != null) {
                                                        courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                    }

                                                    if (cardsubMap.get("sequence") != null) {
                                                        courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                    }

                                                    courseCardClassList.add(courseCardClass);
                                                }
                                            }
                                        }
                                    } else {
                                        Map<String, Object> cardMap = (Map<String, Object>) o2;
                                        for (String key : cardMap.keySet()) {
                                            if (cardMap.get(key) != null) {
                                                final Map<String, Object> cardsubMap = (Map<String, Object>) cardMap.get(key);
                                                DTOCourseCard courseCardClass = new DTOCourseCard();
                                                assert cardsubMap != null;
                                                if (cardsubMap.get("cardBgColor") != null) {
                                                    courseCardClass.setCardBgColor((String) cardsubMap.get("cardBgColor"));
                                                }
                                                if (cardsubMap.get("cardQuestionColor") != null) {
                                                    courseCardClass.setCardQuestionColor((String) cardsubMap.get("cardQuestionColor"));
                                                }
                                                if (cardsubMap.get("cardSolutionColor") != null) {
                                                    courseCardClass.setCardSolutionColor((String) cardsubMap.get("cardSolutionColor"));
                                                }
                                                if (cardsubMap.get("cardTextColor") != null) {
                                                    courseCardClass.setCardTextColor((String) cardsubMap.get("cardTextColor"));
                                                }
                                                String cardType;
                                                if (cardsubMap.get("cardType") != null) {
                                                    cardType = (String) cardsubMap.get("cardType");
                                                    courseCardClass.setCardType(cardType);
                                                }
                                                if (cardsubMap.get("content") != null) {
                                                    courseCardClass.setContent((String) cardsubMap.get("content"));
                                                }
                                                if (cardsubMap.get("cardTitle") != null) {
                                                    courseCardClass.setCardTitle((String) cardsubMap.get("cardTitle"));
                                                }
                                                if (cardsubMap.get("qId") != null) {
                                                    courseCardClass.setqId(OustSdkTools.newConvertToLong(cardsubMap.get("qId")));
                                                }
                                                if (cardsubMap.get("cardId") != null) {
                                                    courseCardClass.setCardId(OustSdkTools.newConvertToLong(cardsubMap.get("cardId")));
                                                }
                                                if (cardsubMap.get("xp") != null) {
                                                    courseCardClass.setXp(OustSdkTools.newConvertToLong(cardsubMap.get("xp")));
                                                }
                                                if (cardsubMap.get("questionCategory") != null) {
                                                    courseCardClass.setQuestionCategory((String) cardsubMap.get("questionCategory"));
                                                }
                                                if (cardsubMap.get("questionType") != null) {
                                                    courseCardClass.setQuestionType((String) cardsubMap.get("questionType"));
                                                }

                                                if (cardsubMap.get("sequence") != null) {
                                                    courseCardClass.setSequence(OustSdkTools.newConvertToLong(cardsubMap.get("sequence")));
                                                }
                                                Map<String, Object> cardColorScheme = (Map<String, Object>) cardsubMap.get("cardColorScheme");
                                                if (cardColorScheme != null) {
                                                    DTOCardColorScheme cardColorScheme1 = new DTOCardColorScheme();
                                                    if (cardColorScheme.get("contentColor") != null) {
                                                        cardColorScheme1.setContentColor((String) cardColorScheme.get("contentColor"));
                                                    }
                                                    if (cardColorScheme.get("bgImage") != null) {
                                                        cardColorScheme1.setBgImage((String) cardColorScheme.get("bgImage"));
                                                    }
                                                    if (cardColorScheme.get("iconColor") != null) {
                                                        cardColorScheme1.setIconColor((String) cardColorScheme.get("iconColor"));
                                                    }
                                                    if (cardColorScheme.get("levelNameColor") != null) {
                                                        cardColorScheme1.setLevelNameColor((String) cardColorScheme.get("levelNameColor"));
                                                    }
                                                    courseCardClass.setCardColorScheme(cardColorScheme1);
                                                }
                                                courseCardClassList.add(courseCardClass);
                                                try {
                                                    Object oo2 = cardsubMap.get("cardMedia");
                                                    if (oo2 != null) {
                                                        if (oo2.getClass().equals(ArrayList.class)) {
                                                            List<Object> cardList = (List<Object>) cardsubMap.get("cardMedia");
                                                            if (cardList != null) {
                                                                for (int l = 0; l < cardList.size(); l++) {
                                                                    if (cardList.get(l) != null) {
                                                                        if (Objects.equals(((HashMap) cardList.get(l)).get("mediaType"), "VIDEO") && ((HashMap) cardList.get(l)).get("mediaPrivacy").equals("PRIVATE")) {
                                                                            noOfVideos++;
                                                                        }
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
                                        }
                                    }
                                }
                                courseLevelClass.setCourseCardClassList(courseCardClassList);
                                courseLevelClassList.add(courseLevelClass);
                            }
                        }
                        try {
                            if (courseLevelClassList.get(0).getSequence() == 0) {
                                for (int q = 0; q < courseLevelClassList.size(); q++) {
                                    courseLevelClassList.get(q).setSequence((courseLevelClassList.get(q).getSequence() + 1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        courseDataClass.setCourseLevelClassList(courseLevelClassList);
                        presenter.loadUserDataFromFirebase((int) courseId, courseDataClass, null, courseColnId);
                    }

                }
                OustPreferences.saveintVar("video_count", noOfVideos);
                if (courseDataClass.getLanguage() != null) {
                    courseDataClassList.add(courseDataClass);
                }

                if (learningMap.get("regularMode") != null) {
                    isRegularMode = (boolean) learningMap.get("regularMode");
                }

                String courseType;
                if (learningMap.get("courseType") != null) {
                    courseType = (String) learningMap.get("courseType");
                    if (courseType.equalsIgnoreCase("MICROLEARNING")) {
                        isMicroCourse = true;
                    }
                }

                if (courseDataClassList.size() == multilingualCourseList.size()) {
                    CourseLanguageAdapter adapter = new CourseLanguageAdapter(courseDataClassList, CourseDetailScreen.this);
                    course_language.setAdapter(adapter);
                }
            }
            if (contentPlayListId > 0) {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.attach_module_cpl));
                finish();
            }

            try {
                CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
                HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
                eventUpdate.put("ClickedOnCourse", true);
                eventUpdate.put("CourseID", courseId);
                eventUpdate.put("Course Name", courseDataClass.getCourseName());

                Log.d("TAG", "CleverTap instance: " + eventUpdate);
                if (clevertapDefaultInstance != null) {
                    clevertapDefaultInstance.pushEvent("CourseDetailPage_Click", eventUpdate);
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
    public void updateUserData(final int lpId, final CourseDataClass courseDataClass, Map<String, Object> learningMap) {
        DTOUserCourseData userCourseData = presenter.getUserCourseData();
        boolean CompletedCoursePercentage = false;
        boolean isAllCardCompleted = true;
        boolean courseCompleted;
        boolean isAssessmentStatus = false;
        boolean isSurveyStatus = false;

        try {
            branding_mani_layout.setVisibility(View.GONE);
            if (learningMap != null && learningMap.get("completionPercentage") != null) {
                userCompletionPercentage = OustSdkTools.newConvertToLong(learningMap.get("completionPercentage"));
                Log.e("TAG", "updateUserData: " + userCompletionPercentage);
                Object o3 = learningMap.get("completionPercentage");
                if (o3 != null && o3.getClass().equals(Long.class)) {
                    userCourseData.setPresentageComplete(OustSdkTools.newConvertToLong(learningMap.get("completionPercentage")));
                } else if (o3 != null && o3.getClass().equals(String.class)) {
                    String s3 = (String) o3;
                    userCourseData.setPresentageComplete(OustSdkTools.newConvertToLong(s3));
                } else if (o3 != null && o3.getClass().equals(Double.class)) {
                    Double s3 = (Double) o3;
                    long l = (s3).longValue();
                    userCourseData.setPresentageComplete(l);
                } else {
                    userCourseData.setPresentageComplete(userCompletionPercentage);
                }
            } else {
                userCompletionPercentage = 0;
                userCourseData.setPresentageComplete(0);
            }

            if (courseDataClass != null && courseDataClass.getMappedAssessmentId() != 0) {
                isAssessmentStatus = true;
            }

            if (courseDataClass != null && courseDataClass.getMappedSurveyId() != 0) {
                isSurveyStatus = true;
            }

            if (contentPlayListId > 0 && userCompletionPercentage != 100) {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.attach_module_cpl));
                finish();
                return;
            }
            if (userCourseData.getPresentageComplete() == 100) {
                CompletedCoursePercentage = true;
            }

            if (learningMap != null && learningMap.get("currentCourseLevelId") != null) {
                userCourseData.setCurrentLevelId(OustSdkTools.newConvertToLong(learningMap.get("currentCourseLevelId")));
            }
            if (learningMap != null && learningMap.get("currentCourseCardId") != null) {
                userCourseData.setCurrentCard(OustSdkTools.newConvertToLong(learningMap.get("currentCourseCardId")));
            }
            if (learningMap != null && learningMap.get("courseCompleted") != null) {
                userCourseData.setCourseCompleted((boolean) learningMap.get("courseCompleted"));
                courseCompleted = (boolean) learningMap.get("courseCompleted");
            } else {
                courseCompleted = false;
                userCourseData.setCourseCompleted(false);
            }
            if (learningMap != null && learningMap.get("mappedAssessmentCompleted") != null) {
                userCourseData.setMappedAssessmentCompleted((boolean) learningMap.get("mappedAssessmentCompleted"));
            } else {
                userCourseData.setMappedAssessmentCompleted(false);
            }
            if (learningMap != null && learningMap.get("userOC") != null) {
                userCourseData.setTotalOc(OustSdkTools.newConvertToLong(learningMap.get("userOC")));
            }
            if (courseAddedOn != null) {
                userCourseData.setAddedOn(courseAddedOn);
            }
            if (learningMap != null && learningMap.get("enrolledDateTime") != null) {
                userCourseData.setEnrollmentDate((String) learningMap.get("enrolledDateTime"));
            }

            if (learningMap != null && learningMap.get("rating") != null) {
                int rating = (int) (OustSdkTools.newConvertToLong(learningMap.get("rating")));
                userCourseData.setMyCourseRating(rating);
            } else {
                userCourseData.setMyCourseRating(0);
            }
            if (learningMap != null && learningMap.get("mappedAssessment") != null) {
                Map<String, Object> mappedAssessmentMap = (Map<String, Object>) learningMap.get("mappedAssessment");
                if (mappedAssessmentMap.get("passed") != null) {
                    userCourseData.setMappedAssessmentPassed((boolean) mappedAssessmentMap.get("passed"));
                }
            }

            if (courseDeadLine != null) {
                if (courseDataClass != null) {
                    courseDataClass.setCourseDeadline(courseDeadLine);
                    setCourseDeadline(courseDataClass);
                }
            }

            if (learningMap != null && learningMap.get("contentPlayListId") != null && courseDataClass != null) {
                courseDataClass.setContentPlayListId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListId")));
            }

            if (learningMap != null && learningMap.get("cplId") != null && courseDataClass != null) {
                courseDataClass.setContentPlayListId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListId")));
            } else if (contentPlayListId != 0 && courseDataClass != null) {
                courseDataClass.setContentPlayListId(contentPlayListId);
            }

            if (learningMap != null && learningMap.get("contentPlayListSlotId") != null && courseDataClass != null) {
                courseDataClass.setContentPlayListSlotId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListSlotId")));
            }
            if (learningMap != null && learningMap.get("contentPlayListSlotItemId") != null && courseDataClass != null) {
                courseDataClass.setContentPlayListSlotItemId(OustSdkTools.newConvertToLong(learningMap.get("contentPlayListSlotItemId")));
            }
            if (learningMap != null && learningMap.get("levels") != null) {
                Object o1 = learningMap.get("levels");
                if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                    List<Object> objectList = (List<Object>) o1;
                    List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();
                    for (int k = 0; k < objectList.size(); k++) {
                        if (objectList.get(k) != null) {
                            final Map<String, Object> levelMap = (Map<String, Object>) objectList.get(k);
                            if (userLevelDataList == null) {
                                userLevelDataList = new ArrayList<>();
                            }
                            int courseLevelNo = -1;
                            int levelKeyValue = 0;
                            if (levelMap.get("levelId") != null) {
                                levelKeyValue = (int) OustSdkTools.newConvertToLong(levelMap.get("levelId"));
                            }
                            for (int l = 0; l < userLevelDataList.size(); l++) {
                                if (userLevelDataList.get(l).getLevelId() == levelKeyValue) {
                                    courseLevelNo = l;
                                }
                            }
                            if (courseDataClass != null && courseDataClass.getCourseLevelClassList() != null) {
                                for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                                    if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == levelKeyValue) {
                                        if (courseLevelNo < 0) {
                                            userLevelDataList.add(new DTOUserLevelData());
                                            courseLevelNo = (userLevelDataList.size() - 1);
                                        }
                                        userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                        userLevelDataList.get(courseLevelNo).setLevelId(levelKeyValue);
                                        if (levelMap.get("userLevelOC") != null) {
                                            userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("userLevelOC")));
                                        }
                                        if (levelMap.get("userLevelXp") != null) {
                                            userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.newConvertToLong(levelMap.get("userLevelXp")));
                                        }
                                        if (levelMap.get("locked") != null) {
                                            userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                        } else {
                                            userLevelDataList.get(courseLevelNo).setLocked(true);
                                        }
                                        Object o2 = levelMap.get("cards");
                                        if (o2 != null) {
                                            if (o2.getClass().equals(ArrayList.class)) {
                                                List<Object> objectCardList = (List<Object>) o2;
                                                List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                for (int j = 0; j < objectCardList.size(); j++) {
                                                    if (objectCardList.get(j) != null) {
                                                        final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                        DTOUserCardData userCardData = new DTOUserCardData();
                                                        if (cardMap.get("userCardAttempt") != null) {
                                                            userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                        }
                                                        if (cardMap.get("cardCompleted") != null) {
                                                            userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                            if (isAllCardCompleted) {
                                                                isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                            }
                                                        } else {
                                                            userCardData.setCardCompleted(false);
                                                            isAllCardCompleted = false;
                                                        }

                                                        if (cardMap.get("cardViewInterval") != null) {
                                                            userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                        } else {
                                                            userCardData.setCardViewInterval(0);
                                                        }
                                                        if (cardMap.get("cardId") != null) {
                                                            userCardData.setCardId(OustSdkTools.newConvertToLong(cardMap.get("cardId")));
                                                        }
                                                        userCardDataList.add(userCardData);
                                                    }
                                                }
                                                userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                            } else if (o2.getClass().equals(HashMap.class)) {
                                                final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                                List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                                for (String cardKey : objectCardMap.keySet()) {
                                                    final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                                    DTOUserCardData userCardData = new DTOUserCardData();
                                                    if (cardMap.get("userCardAttempt") != null) {
                                                        userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                    }
                                                    if (cardMap.get("cardCompleted") != null) {
                                                        userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                        if (isAllCardCompleted) {
                                                            isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                        }
                                                    } else {
                                                        userCardData.setCardCompleted(false);
                                                        isAllCardCompleted = false;
                                                    }

                                                    if (cardMap.get("cardViewInterval") != null) {
                                                        userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                    } else {
                                                        userCardData.setCardViewInterval(0);
                                                    }

                                                    Log.d("TAG", "updateUserData: cardid:" + cardKey + " --- viewinterval:" + userCardData.getCardViewInterval());
                                                    userCardData.setCardId(Integer.parseInt(cardKey));
                                                    userCardDataList.add(userCardData);
                                                }
                                                userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    userCourseData.setUserLevelDataList(userLevelDataList);
                } else if (o1.getClass().equals(HashMap.class)) {
                    final Map<String, Object> objectMap = (Map<String, Object>) o1;
                    List<DTOUserLevelData> userLevelDataList = userCourseData.getUserLevelDataList();

                    for (String levelKey : objectMap.keySet()) {
                        final Map<String, Object> levelMap = (Map<String, Object>) objectMap.get(levelKey);
                        if (userLevelDataList == null) {
                            userLevelDataList = new ArrayList<>();
                        }
                        int courseLevelNo = -1;
                        int k = Integer.parseInt(levelKey);
                        for (int l = 0; l < userLevelDataList.size(); l++) {
                            if (userLevelDataList.get(l).getLevelId() == k) {
                                courseLevelNo = l;
                            }
                        }
                        for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                            if (courseDataClass.getCourseLevelClassList().get(i).getLevelId() == k) {
                                if (courseLevelNo < 0) {
                                    userLevelDataList.add(new DTOUserLevelData());
                                    courseLevelNo = (userLevelDataList.size() - 1);
                                }
                                userLevelDataList.get(courseLevelNo).setSequece(courseDataClass.getCourseLevelClassList().get(i).getSequence());
                                userLevelDataList.get(courseLevelNo).setLevelId(k);
                                if (levelMap.get("userLevelOC") != null) {
                                    userLevelDataList.get(courseLevelNo).setTotalOc(OustSdkTools.newConvertToLong(levelMap.get("userLevelOC")));
                                }
                                if (levelMap.get("userLevelXp") != null) {
                                    userLevelDataList.get(courseLevelNo).setXp(OustSdkTools.newConvertToLong(levelMap.get("userLevelXp")));
                                }
                                if (levelMap.get("locked") != null) {
                                    userLevelDataList.get(courseLevelNo).setLocked((boolean) levelMap.get("locked"));
                                } else {
                                    userLevelDataList.get(courseLevelNo).setLocked(true);
                                }
                                Object o2 = levelMap.get("cards");
                                if (o2 != null) {
                                    if (o2.getClass().equals(ArrayList.class)) {
                                        List<Object> objectCardList = (List<Object>) o2;
                                        List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                        for (int j = 0; j < objectCardList.size(); j++) {
                                            if (objectCardList.get(j) != null) {
                                                final Map<String, Object> cardMap = (Map<String, Object>) objectCardList.get(j);
                                                DTOUserCardData userCardData = new DTOUserCardData();
                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                }
                                                if (cardMap.get("cardCompleted") != null) {
                                                    userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                    if (isAllCardCompleted) {
                                                        isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                    }
                                                } else {
                                                    userCardData.setCardCompleted(false);
                                                    isAllCardCompleted = false;
                                                }

                                                if (cardMap.get("cardViewInterval") != null) {
                                                    userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                } else {
                                                    userCardData.setCardViewInterval(0);
                                                }
                                                Log.d("TAG", "updateUserData: cardid:" + j + " --- viewinterval:" + userCardData.getCardViewInterval());

                                                userCardData.setCardId(j);
                                                userCardDataList.add(userCardData);
                                            }
                                        }
                                        userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                    } else if (o2.getClass().equals(HashMap.class)) {
                                        List<DTOUserCardData> userCardDataList = new ArrayList<>();
                                        final Map<String, Object> objectCardMap = (Map<String, Object>) o2;
                                        for (String cardKey : objectCardMap.keySet()) {
                                            final Map<String, Object> cardMap = (Map<String, Object>) objectCardMap.get(cardKey);
                                            DTOUserCardData userCardData = new DTOUserCardData();
                                            if (cardMap != null) {
                                                if (cardMap.get("userCardAttempt") != null) {
                                                    userCardData.setNoofAttempt(OustSdkTools.newConvertToLong(cardMap.get("userCardAttempt")));
                                                }
                                                if (cardMap.get("cardCompleted") != null) {
                                                    userCardData.setCardCompleted((boolean) cardMap.get("cardCompleted"));
                                                    if (isAllCardCompleted) {
                                                        isAllCardCompleted = (boolean) cardMap.get("cardCompleted");
                                                    }
                                                } else {
                                                    userCardData.setCardCompleted(false);
                                                    isAllCardCompleted = false;
                                                }

                                                if (cardMap.get("cardViewInterval") != null) {
                                                    userCardData.setCardViewInterval(OustSdkTools.newConvertToLong(cardMap.get("cardViewInterval")));
                                                } else {
                                                    userCardData.setCardViewInterval(0);
                                                }
                                            }

                                            Log.d("TAG", "updateUserData: cardid:" + cardKey + " --- viewinterval:" + userCardData.getCardViewInterval());

                                            userCardData.setCardId(Integer.parseInt(cardKey));
                                            userCardDataList.add(userCardData);
                                        }
                                        userLevelDataList.get(courseLevelNo).setUserCardDataList(userCardDataList);
                                    }
                                }
                            }
                        }
                    }
                    Collections.sort(userLevelDataList, courseUserCardSorter);
                    userCourseData.setUserLevelDataList(userLevelDataList);
                }
            }

            if (courseDataClass != null) {
                List<DTOUserLevelData> levelDataList = new ArrayList<>();
                if (userCourseData.getUserLevelDataList() == null) {
                    userCourseData.setUserLevelDataList(new ArrayList<>());
                }

                for (int k = 0; k < userCourseData.getUserLevelDataList().size(); k++) {
                    boolean containLevel = false;
                    for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                        if (userCourseData.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            containLevel = true;
                        }
                    }
                    if (containLevel) {
                        boolean alreadyIn = false;
                        for (int n = 0; n < levelDataList.size(); n++) {
                            if (userCourseData.getUserLevelDataList().get(k).getLevelId() == levelDataList.get(n).getLevelId()) {
                                alreadyIn = true;
                            }
                        }
                        if (!alreadyIn) {
                            levelDataList.add(userCourseData.getUserLevelDataList().get(k));
                        }
                    }
                }

                userCourseData.setUserLevelDataList(levelDataList);
                DTOUserLevelData userLevelData;
                for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                    boolean alreadyIn = false;
                    for (int k = 0; k < userCourseData.getUserLevelDataList().size(); k++) {
                        if (userCourseData.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            alreadyIn = true;
                        }
                    }
                    if (!alreadyIn) {
                        userLevelData = new DTOUserLevelData();
                        userLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        userLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());
                        userCourseData.getUserLevelDataList().add(userLevelData);
                    }
                }
            }
            Collections.sort(userCourseData.getUserLevelDataList(), courseUserCardSorter);

            int currentLevel = 0;

            if (courseDataClass != null && courseDataClass.getCourseLevelClassList().size() > 0) {
                for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                    if ((courseDataClass.getCourseLevelClassList() != null) && (courseDataClass.getCourseLevelClassList().get(i) != null)
                            && (userCourseData.getUserLevelDataList() != null) && (userCourseData.getUserLevelDataList().get(i) != null)) {
                        if (!userCourseData.getUserLevelDataList().get(i).isLocked() && !courseDataClass.getCourseLevelClassList().get(i).isLevelLock()) {
                            currentLevel++;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            userCourseData.setCurrentLevel(currentLevel);
            if (courseDataClass != null) {
                if (userCourseData.getCurrentLevel() > (courseDataClass.getCourseLevelClassList().size() + 1)) {
                    userCourseData.setCurrentLevel((courseDataClass.getCourseLevelClassList().size() + 1));
                }
            }
            if (userCourseData.getPresentageComplete() == 100) {
                userCourseData.setCourseComplete(true);
                if (!isMicroCourse) {
                    userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 1); // need to check here for error
                    if (courseDataClass.getMappedAssessmentId() > 0) {
                        if (userCourseData.isMappedAssessmentPassed()) {
                            userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size() + 2);
                        }
                    }
                } else {
                    userCourseData.setCurrentLevel(courseDataClass.getCourseLevelClassList().size());
                }
            } else {
                userCourseData.setCourseComplete(false);
            }
            if (!isEnrolled) {
                userCourseData.setCurrentLevel(0);
            }

            try {
                if (courseDataClass != null && userCourseData.getUserLevelDataList() != null) {
                    if (userCourseData.getCurrentLevel() > 0) {
                        Collections.sort(courseDataClass.getCourseLevelClassList(), CourseLevelClass.levelSorter);
                        if (courseDataClass.getCourseLevelClassList() != null && (courseDataClass.getCourseLevelClassList().size() == 1 || userCourseData.getCurrentLevel() == 1)) {
                            Collections.sort(courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList(), DTOCourseCard.newsCardSorter);
                            for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().size(); l++) {
                                if (courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 1).getCourseCardClassList().get(l).getCardId() == userCourseData.getCurrentCard()) {
                                    userCourseData.getUserLevelDataList().get((int) userCourseData.getCurrentLevel() - 1).setCurrentCardNo(l + 1);
                                }
                            }
                        } else {
                            if (courseDataClass.getCourseLevelClassList() != null) {
                                Collections.sort(courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 2).getCourseCardClassList(), DTOCourseCard.newsCardSorter);
                                for (int l = 0; l < courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 2).getCourseCardClassList().size(); l++) {
                                    if (courseDataClass.getCourseLevelClassList().get((int) userCourseData.getCurrentLevel() - 2).getCourseCardClassList().get(l).getCardId() == userCourseData.getCurrentCard()) {
                                        userCourseData.getUserLevelDataList().get((int) userCourseData.getCurrentLevel() - 2).setCurrentCardNo(l + 1);
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);

            }

            presenter.addUserData(userCourseData);
            presenter.gotLpDataFromFirebase(courseDataClass);

            if (isMicroCourse && userCourseData.getPresentageComplete() > 99 && !isMicroCoursePlay) {
                Intent badgeIntent = new Intent(CourseDetailScreen.this, CourseCompletionWithBadgeActivity.class);
                badgeIntent.putExtra("isMicroCourse", true);
                badgeIntent.putExtra("courseId", courseId);
                startActivity(badgeIntent);
                overridePendingTransition(0, 0);
                try {
                    isRedirectToBack = true;
                    branding_mani_layout.setVisibility(VISIBLE);
                    //CourseDetailScreen.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            } else if (isMicroCoursePlay) {
                openLearningMapPage(String.valueOf(courseId));
            } else {
                course_details_normal();
                if (userCourseData.getPresentageComplete() > 0 || isEnrolled) {
                    if (isArchiveCourse) {
                        Toast.makeText(CourseDetailScreen.this, "" + getResources().getString(R.string.sorry_archive_by_admin), Toast.LENGTH_SHORT).show();
                        backScreen();

                    } else {
                        if (userCourseData.getPresentageComplete() < 100 && !showPastDeadlineModulesOnLandingPage &&
                                System.currentTimeMillis() >= courseDeadLineTime && courseDeadLineTime != 0) {
                            Toast.makeText(CourseDetailScreen.this, "" + getResources().getString(R.string.course_not_available_text), Toast.LENGTH_SHORT).show();
                            backScreen();
                        } else {
                            if (!isFeedComment) {
                                isRedirectToBack = true;
                                openLearningMapPage("" + courseId);
                                try {
                                    isRedirectToBack = true;
                                    branding_mani_layout.setVisibility(VISIBLE);
                                    toolbar.setVisibility(View.GONE);
                                    //CourseDetailScreen.this.finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }
                            } else {
                                branding_mani_layout.setVisibility(View.GONE);
                                toolbar.setVisibility(VISIBLE);
                            }
                        }
                    }
                } else {
                    if (!showPastDeadlineModulesOnLandingPage && System.currentTimeMillis() >= courseDeadLineTime && courseDeadLineTime != 0) {
                        Toast.makeText(CourseDetailScreen.this, "" + getResources().getString(R.string.course_not_available_text), Toast.LENGTH_SHORT).show();
                        backScreen();
                    } else {
                        branding_mani_layout.setVisibility(View.GONE);
                        toolbar.setVisibility(VISIBLE);
                    }
                }
            }

            if (isEnrolled) {
                if (!isAssessmentStatus && !isSurveyStatus) {
                    if (!CompletedCoursePercentage || !courseCompleted) {
                        isInstrumentationHit = true;
                        presenter.hitInstrumentationForCompletion(CourseDetailScreen.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            branding_mani_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setDownloadCourseIcon(DTOUserCourseData userCourseData) {
        try {
            boolean downloaded = true;
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            if ((userCourseData != null) && (userCourseData.getUserLevelDataList() != null) && (userCourseData.getUserLevelDataList().size() > 0)) {
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getCompletePercentage() < 100) {
                        downloaded = false;
                        break;
                    }
                }
            } else {
                downloaded = false;
            }

            if (downloadAnimation != null) {
                downloadAnimation.stop();
                downloadAnimation.selectDrawable(0);
            }

            if (downloaded) {
                mUserLevelDataList = new ArrayList<>();
                mUserCourseData = userCourseData;
                mUserLevelDataList = userCourseData.getUserLevelDataList();
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(1);
                }
                if (activeUser == null) {
                    activeUser = OustAppState.getInstance().getActiveUser();
                }

                enableCourseDataDelete(mUserCourseData);
                String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                long courseUniqueNo = OustSdkTools.newConvertToLong(s1);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
            } else {
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(0);
                }
                courseAlreadyDownloaded = false;
                course_delete.setVisibility(View.GONE);
                course_download.setVisibility(VISIBLE);
                downloadVideoLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void startCourseDownload() {
        try {
            CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
            HashMap<String, Object> eventUpdate = OustSdkTools.getCleverTapEventData();
            eventUpdate.put("ClickedOnDownloadButton", true);
            eventUpdate.put("CourseID", courseId);
            eventUpdate.put("Course Name", courseDataClass.getCourseName());
            Log.d("TAG", "CleverTap instance: " + eventUpdate);

            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.pushEvent("Course_Download", eventUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (!courseAlreadyDownloaded && !downloadCourseClicked) {
                presenter.clickOnCourseDownload();
                downloadCourseClicked = true;
                courseAlreadyDownloaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void setLpOcText(long myTotalOc, long totalOc) {

    }

    @Override
    public void downloadCourse(CourseDataClass courseDataClass) {

        try {
            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
            long courseUniqueNo = OustSdkTools.newConvertToLong(s1);
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
            if (userCourseData.getDownloadCompletePercentage() < 100) {
                userCourseData.setDownloading(true);
                userCourseScoreDatabaseHandler.addUserScoreToRealm(userCourseData, courseUniqueNo);
                startCourseDownloadService(courseDataClass);
                downloadAnimation.start();
            } else {
                if (downloadAnimation != null) {
                    downloadAnimation.stop();
                    downloadAnimation.selectDrawable(0);
                }
                courseAlreadyDownloaded = false;
                course_delete.setVisibility(VISIBLE);
                course_download.setVisibility(View.GONE);
                downloadVideoLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showMessage(String errorMessage) {

    }

    private void startCourseDownloadService(CourseDataClass courseDataClass) {
        try {
            if (!allCourseDownloadStarted) {
                allCourseDownloadStarted = true;
                boolean isServiceRunning = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();
                    String s1 = "" + activeUser.getStudentKey() + "" + courseId;
                    long courseUniqNo = OustSdkTools.newConvertToLong(s1);
                    for (StatusBarNotification notification : barNotifications) {
                        if (notification.getId() == courseUniqNo) {
                            isServiceRunning = true;
                            break;
                        }
                    }
                }


                //TODO: handle intent for download
                if (!isServiceRunning) {
                    Gson gson = new Gson();
                    String courseDataStr = gson.toJson(courseDataClass);

                    Intent intent = new Intent(CourseDetailScreen.this, DownloadForegroundService.class);
                    intent.setAction(START_UPLOAD);
                    intent.putExtra(DownloadForegroundService.COURSE_DATA, courseDataStr);
                    intent.putExtra(DownloadForegroundService.COURSE_ID, courseDataClass.getCourseId());
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_COURSE, true);
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_VIDEO, enableVideoDownload);
                    startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enableCourseDataDelete(DTOUserCourseData userCourseData) {
        try {
            if (mUserLevelDataList != null) {
                for (int i = 0; i < mUserLevelDataList.size(); i++) {
                    if (RoomHelper.checkMapTableExist((int) mUserLevelDataList.get(i).getLevelId())) {
                        userCourseData.setDownloadCompletePercentage(100);
                        if (downloadAnimation != null) {
                            downloadAnimation.stop();
                            downloadAnimation.selectDrawable(1);
                        }
                        course_download.setVisibility(View.GONE);
                        downloadVideoLayout.setVisibility(View.GONE);
                        course_delete.setVisibility(VISIBLE);
                        courseAlreadyDownloaded = true;
                        break;
                    } else {
                        userCourseData.setDownloadCompletePercentage(0);
                        if (downloadAnimation != null) {
                            downloadAnimation.stop();
                            downloadAnimation.selectDrawable(0);
                        }
                        courseAlreadyDownloaded = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    public void courseDownloading() {
        try {
            String s1 = "" + activeUser.getStudentKey() + "" + courseId;
            long courseUniqueNo = OustSdkTools.newConvertToLong(s1);
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = userCourseScoreDatabaseHandler.getScoreById(courseUniqueNo);
            mUserCourseData = userCourseData;
            if (presenter != null) {
                presenter.updateLevelDownloadStatus();
            }
            if (userCourseData != null && userCourseData.isDownloading()) {
                if (userCourseData.getDownloadCompletePercentage() == 100) {
                    if (downloadAnimation != null) {
                        downloadAnimation.stop();
                        downloadAnimation.selectDrawable(1);
                    }
                    mUserCourseData = userCourseData;
                    mUserLevelDataList = new ArrayList<>();
                    mUserLevelDataList = userCourseData.getUserLevelDataList();
                    enableCourseDataDelete(userCourseData);
                } else {
                    if (downloadAnimation != null) {
                        downloadAnimation.start();
                        downloadAnimation.selectDrawable(0);
                    }
                    course_download.setVisibility(VISIBLE);
                    downloadVideoLayout.setVisibility(VISIBLE);
                    course_delete.setVisibility(View.GONE);
                }
            } else {
                setDownloadCourseIcon(userCourseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile(CourseCardClass courseCardClass) {
        try {
            if (mediaSize > 0) {
                mediaSize--;
            }
            if (mediaSize == 0) {
                downloadComplete(courseCardClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void downloadComplete(CourseCardClass courseCardClass) {
        try {
            // OustSdkTools.showToast("Something went wrong");
            course_download.setEnabled(courseCardClass == null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        try {
            if (myFileDownLoadReceiver != null) {
                this.unregisterReceiver(myFileDownLoadReceiver);
            }

            if (courseDownloadReceiver != null) {
                this.unregisterReceiver(courseDownloadReceiver);
            }

            if (downloadReceiver != null) {
                this.unregisterReceiver(downloadReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            courseDownloading();
            if (isRedirectToBack) {
                backScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onBackPressed() {
        backScreen();
    }

    private void backScreen() {
        try {
            if (feed != null) {
                OustStaticVariableHandling.getInstance().setResult_code(1444);
                OustStaticVariableHandling.getInstance().setFeedId(feed.getFeedId());
                OustStaticVariableHandling.getInstance().setFeedChanged(isFeedChange);
                OustStaticVariableHandling.getInstance().setLikeClicked(false);
                OustStaticVariableHandling.getInstance().setNumOfComments(feed.getNumComments());
                OustStaticVariableHandling.getInstance().setNumOfLikes(feed.getNumLikes());
                OustStaticVariableHandling.getInstance().setNumOfShares(feed.getNumShares());
                Intent data = new Intent();
                data.putExtra("FeedPosition", feed.getFeedId());
                data.putExtra("FeedComment", feed.getNumComments());
                data.putExtra("isFeedChange", isFeedChange);
                data.putExtra("isClicked", true);
                setResult(1444, data);
            } else {
                if (taskPosition != -1) {

                    Intent data = new Intent();
                    taskCompletion = OustPreferences.getSavedInt("taskCompletion");
                    data.putExtra("taskCompletion", taskCompletion);
                    data.putExtra("taskPosition", taskPosition);
                    setResult(1993, data);
                    OustPreferences.saveintVar("taskCompletion", 0);
                }
            }
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void updateLevelDownloadStatus(int noofLevels, final CourseDataClass courseDataClass, DTOUserCourseData dtoUserCourseData) {
        try {
            // Log.d(TAG, "updateLevelDownloadStatus:noofLevels: "+noofLevels);
            for (int i = 0; i < noofLevels; i++) {
                if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null) && (dtoUserCourseData.getUserLevelDataList().size() > i) &&
                        (dtoUserCourseData.getUserLevelDataList().get(i) != null) &&
                        ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() > 0) || (dtoUserCourseData.getUserLevelDataList().get(i).isDownloading()))) {


                    if ((dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100)) {
                        dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                        RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        } finally {

            Log.d("CourseDetailScreen ", "Update level download finally");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isRedirectToBack) {
            backScreen();
        }
    }

    private void getColors() {
        try {
            if (OustPreferences.getAppInstallVariable("isLayout4")) {
                color = OustResourceUtils.getColors();
                bgColor = OustResourceUtils.getToolBarBgColor();
            } else {
                bgColor = OustResourceUtils.getColors();
                color = OustResourceUtils.getToolBarBgColor();

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.newlandingmenu, menu);


        MenuItem action_leaderBoard = menu.findItem(R.id.action_leaderBoard);
        Drawable leaderBoardDrawable = getResources().getDrawable(R.drawable.ic_landing_leader_board);
        action_leaderBoard.setIcon(OustResourceUtils.setDefaultDrawableColor(leaderBoardDrawable, color));
        action_leaderBoard.setVisible(isLeaderBoardEnable());


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            backScreen();
            return true;
        } else if (itemId == R.id.action_leaderBoard) {
            Intent leaderBoardIntent = new Intent(CourseDetailScreen.this, CommonLeaderBoardActivity.class);
            leaderBoardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle containerBundle = new Bundle();
            containerBundle.putString("containerType", "courseLeaderBoard");
            containerBundle.putLong("containerContentId", courseDataClass.getCourseId());
            if ((courseDataClass.getBgImg() != null) && (!courseDataClass.getBgImg().isEmpty())) {
                containerBundle.putString("contentBgImage", courseDataClass.getBgImg());
            }
            if (courseDataClass.getCourseName() != null) {
                containerBundle.putString("contentName", courseDataClass.getCourseName());
            }
            leaderBoardIntent.putExtras(containerBundle);
            startActivity(leaderBoardIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    boolean isLeaderBoardEnable() {
        return leaderBoardEnable;
    }

    private void course_details_normal() {
        course_status_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_arrow, 0, 0, 0);
    }

    private void setCourseDeadline(CourseDataClass courseData) {
        try {
            Date date;
            try {
                if (courseData != null) {
                    if (courseData.getCourseDeadline() != null) {
                        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(courseData.getCourseDeadline());
                        if (date != null) {
                            courseDeadLineTime = date.getTime();
                            String courseDeadLine = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);

                            if (!courseDeadLine.isEmpty()) {
                                course_feed_dead_line.setVisibility(VISIBLE);
                                String deadLineText = getResources().getString(R.string.deadline);
                                courseDeadLine = deadLineText + " " + courseDeadLine.toUpperCase();
                                Spannable spanText = new SpannableString(courseDeadLine);
                                spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                course_feed_dead_line.setText(spanText);
                            }
                        }
                    } else {
                        course_feed_dead_line.setVisibility(View.GONE);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
                try {
                    if (courseData.getCourseDeadline() != null && !courseData.getCourseDeadline().isEmpty()) {
                        date = new Date(OustSdkTools.newConvertToLong(courseData.getCourseDeadline()));
                        courseDeadLineTime = date.getTime();
                        String courseDeadLine = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(date);

                        if (!courseDeadLine.isEmpty()) {
                            course_feed_dead_line.setVisibility(VISIBLE);
                            String deadLineText = getResources().getString(R.string.deadline);
                            courseDeadLine = deadLineText + " " + courseDeadLine.toUpperCase();
                            Spannable spanText = new SpannableString(courseDeadLine);
                            spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_incorrect)), 0, deadLineText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            course_feed_dead_line.setText(spanText);

                        }
                    } else {
                        course_feed_dead_line.setVisibility(View.GONE);
                    }
                } catch (Exception es) {
                    es.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public class CourseDownloadReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "learningmap_course_download";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String courseToRestartAfterPermission;
                courseToRestartAfterPermission = intent.getStringExtra("courseId");
                if ((courseToRestartAfterPermission != null) && (!courseToRestartAfterPermission.isEmpty())) {
                    ActivityCompat.requestPermissions(CourseDetailScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                }
                courseDownloading();
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null) {
                    if (intent.getAction() != null) {
                        if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            //removeFile(course);
                            courseDownloading();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            Log.e("TAG", "onReceive: Error-> ");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }

    private void deleteConfirmation() {
        try {
            Dialog deleteDialog = new Dialog(CourseDetailScreen.this, R.style.DialogTheme);
            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deleteDialog.setContentView(R.layout.common_pop_up);
            deleteDialog.setCancelable(false);
            Objects.requireNonNull(deleteDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            deleteDialog.show();
            //CardView card_layout = deleteDialog.findViewById(R.id.card_layout);
            ImageView info_image = deleteDialog.findViewById(R.id.info_image);
            TextView info_title = deleteDialog.findViewById(R.id.info_title);
            TextView info_description = deleteDialog.findViewById(R.id.info_description);
            LinearLayout info_no = deleteDialog.findViewById(R.id.info_no);
            TextView info_no_text = deleteDialog.findViewById(R.id.info_no_text);
            LinearLayout info_yes = deleteDialog.findViewById(R.id.info_yes);
            TextView info_yes_text = deleteDialog.findViewById(R.id.info_yes_text);

            info_title.setText(getResources().getString(R.string.warning));
            info_description.setText(getResources().getString(R.string.module_delete_confirmation));
            info_description.setVisibility(VISIBLE);
            info_no_text.setText(getResources().getString(R.string.cancel));
            info_yes_text.setText(getResources().getString(R.string.confirm));

            info_yes.setBackground(OustResourceUtils.setDefaultDrawableColor(getResources().getDrawable(R.drawable.button_rounded_ten_bg)));

            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);//change warning drawable
            info_image.setImageDrawable(OustSdkTools.drawableColor(infoDrawable, getResources().getColor(R.color.error_incorrect)));

            info_no.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }

            });

            info_yes.setOnClickListener(v -> {
                if (deleteDialog.isShowing()) {
                    deleteDialog.dismiss();
                }
                if (mUserLevelDataList != null && mUserLevelDataList.size() > 0) {
                    for (int i = 0; i < mUserLevelDataList.size(); i++) {
                        try {
                            RoomHelper.getAllCourseInLevel(mUserLevelDataList.get(i).getLevelId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        presenter.updateLevelDownloadStatus();
                        course_delete.setVisibility(View.GONE);
                        course_download.setVisibility(VISIBLE);
                        mUserLevelDataList.get(i).setCompletePercentage(0);
                        mUserCourseData.setDownloadCompletePercentage(0);
                        presenter.addUserData(mUserCourseData);
                    }
                }
                RoomHelper.downloadedOrNot(courseId);
                onBackPressed();
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public class DownloadReceiver extends BroadcastReceiver {
        public static final String DOWNLOAD_RESPONSE = "course_download_percentage";

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra("courseId")) {
                    String courseDownloadId = intent.getStringExtra("courseId");
                    Log.d("coursesIsDownloaded: ", "id: " + courseDownloadId);
                    String s1 = "1" + activeUser.getStudentKey() + "" + courseId;
                    long courseUniqueNo = OustSdkTools.newConvertToLong(s1);
                    if (intent.hasExtra("downloadPercentage") && OustSdkTools.newConvertToLong(courseDownloadId) == courseUniqueNo) {
                        int downValue = 0;
                        try {
                            if (intent.getExtras() != null) {
                                downValue = Math.round(Float.parseFloat(String.valueOf(intent.getExtras().get("downloadPercentage"))));
                            }
                            RoomHelper.addDownloadedOrNot((int) courseId, false, downValue);
                            downloadVideoLayout.setVisibility(View.VISIBLE);
                            downloadVideoPercentage.setVisibility(VISIBLE);
                            downloadVideoPercentage.setText("" + downValue);
                            OustSdkTools.setDownloadGifImage(downloadGifImageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        course_download.setVisibility(View.GONE);
                        course_delete.setVisibility(View.GONE);
                        if (downValue >= 100) {
                            course_delete.setVisibility(View.VISIBLE);
                            downloadVideoLayout.setVisibility(View.GONE);
                            downloadVideoPercentage.setVisibility(View.GONE);
                            RoomHelper.addDownloadedOrNot((int) courseId, true, 100);
                            //Toast.makeText(CourseDetailScreen.this, getResources().getString(R.string.course_complete_heading), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
        }
    }
}
