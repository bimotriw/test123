package com.oustme.oustsdk.course_ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.request.SubmitCourseCardRequestDataV3;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.response.course.LearningCardResponceData;
import com.oustme.oustsdk.room.EntityDownloadedOrNot;
import com.oustme.oustsdk.room.EntityLevelCardCourseID;
import com.oustme.oustsdk.room.EntityLevelCardCourseIDUpdateTime;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTOMTFColumnData;
import com.oustme.oustsdk.room.dto.DTOQuestionOption;
import com.oustme.oustsdk.room.dto.DTOQuestionOptionCategory;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.utils.OustResourceUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LearningCardDownloadFragment extends Fragment {
    final String TAG = "LearningCardDownloadFrg";

    ImageView background_imageview;
    LinearLayout download_progress_layout;
    ImageView loading_icon;
    TextView download_progress_text;
    TextView download_content_text;
    TextView no_connection_text;

    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;
    private boolean favMode;
    private CourseLevelClass courseLevelClass;
    private CourseDataClass courseDataClass;
    private boolean isRegularMode;
    private LearningModuleInterface learningModuleInterface;
    private boolean isDownloadVideo;
    private String backgroundImage;
    private int cardSize;
    private boolean isDownloadCardByCard;
    private Handler myHandler;
    private List<String> hintMessages;
    private List<DTOCourseCard> courseCardClassList = new ArrayList<>();
    private int n1 = 0;
    private int n2 = 0;
    private ArrayList<String> mediaList;
    private ArrayList<String> pathList;
    private ArrayList<String> vMediaList;
    private ArrayList<String> videoPathList;
    private boolean isFragmentLive = true;
    private boolean isComingFromCPL = false;
    private long courseUniqueId;
    private long mappedAssessmentId;
    private long mappedSurveyId;
    private boolean levelRestStatus;
    NotificationManager notificationManager;
    int totalLevelCards = 1;
    int downloadedLevelCards = 0;
    int retry = 2;
    int mediaSize;
    int downloadedMediaSize;
    List<EntityDownloadedOrNot> entityDownloadedOrNot = new ArrayList<>();
    boolean courseDowloaded = false;
    boolean reStartLevel = false;
    Dialog showSubmitRetryDialog;
    boolean currentLevelCompleted = false;
    long currentCardNumber = 0;

    public LearningCardDownloadFragment() {
        // Required empty public constructor
    }

    public static LearningCardDownloadFragment newInstance() {
        return new LearningCardDownloadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learning_card_downlaod, container, false);
        background_imageview = view.findViewById(R.id.background_imageview);
        download_progress_layout = view.findViewById(R.id.download_progress_layout);
        loading_icon = view.findViewById(R.id.loading_icon);
        download_progress_text = view.findViewById(R.id.download_progress_text);
        download_content_text = view.findViewById(R.id.download_content_text);
        no_connection_text = view.findViewById(R.id.no_connection_text);
        initData();
        return view;
    }

    private void initData() {
        try {

            userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            notificationManager = (NotificationManager) Objects.requireNonNull(requireActivity()).getSystemService(Context.NOTIFICATION_SERVICE);
            download_content_text.setText(Objects.requireNonNull(requireActivity()).getResources().getString(R.string.fetching_data_msg));
            OustSdkTools.setImage(background_imageview, Objects.requireNonNull(requireActivity()).getResources().getString(R.string.bg_1));
            setLogo();
            setBackgroundImage();

            DTOUserCourseData userCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if ((cardSize == 0)) {
                if (courseLevelClass != null) {
                    if (courseLevelClass.getCourseCardClassList() != null) {
                        cardSize = courseLevelClass.getCourseCardClassList().size();
                    }
                }
            }
            if (userCourseData != null && userCourseData.getUserLevelDataList() != null) {
                for (int j = 0; j < userCourseData.getUserLevelDataList().size(); j++) {
                    if (courseLevelClass != null) {
                        if (userCourseData.getUserLevelDataList().get(j).getLevelId() == courseLevelClass.getLevelId()) {
                            if (userCourseData.getUserLevelDataList().get(j).getCompletePercentage() == 100) {
                                if ((courseLevelClass != null) && (courseLevelClass.getCourseCardClassList() != null)) {
                                    cardSize = courseLevelClass.getCourseCardClassList().size();
                                }
                            }
                        }
                    }
                }
            } else {
                if (courseLevelClass != null && courseLevelClass.getCourseCardClassList() != null && courseLevelClass.getCourseCardClassList().size() != 0) {
                    cardSize = courseLevelClass.getCourseCardClassList().size();
                }
            }
            if (isRegularMode) {
                download_content_text.setVisibility(View.GONE);
                download_progress_layout.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    try {
                        checkForUpdate();
                        setHintMessageToShow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 500);
            } else {
                new Handler().postDelayed(() -> {
                    try {
                        setIconScaleAnimation();
                        checkForUpdate();
                        setHintMessageToShow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 500);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setFavMode(boolean favMode) {
        this.favMode = favMode;
    }

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void setCourseDataClass(CourseDataClass courseDataClass) {
        this.courseDataClass = courseDataClass;
    }

    public void setRegularMode(boolean isRegularMode) {
        this.isRegularMode = isRegularMode;
    }

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    public void setDownloadVideo(boolean isDownloadVideo) {
        this.isDownloadVideo = isDownloadVideo;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setCardSize(int cardSize) {
        this.cardSize = cardSize;
        if (courseLevelClass != null) {
            if (cardSize < courseLevelClass.getCourseCardClassList().size()) {
                isDownloadCardByCard = true;
            }
        }
    }

    private void setLogo() {
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir().getPath(), "oustlearn_" + (OustPreferences.get("tanentid").toUpperCase()) + "splashIcon");
            if (file.exists()) {
                Glide.with(Objects.requireNonNull(requireActivity())).load(file).into(loading_icon);
                loading_icon.setVisibility(View.VISIBLE);
            } else {
                loading_icon.setVisibility(View.VISIBLE);
                OustSdkTools.setImage(loading_icon, Objects.requireNonNull(requireActivity()).getResources().getString(R.string.app_icon));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setBackgroundImage() {
        try {
            if ((backgroundImage != null) && (!backgroundImage.isEmpty())) {
                background_imageview.setVisibility(View.VISIBLE);
                Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).diskCacheStrategy(DiskCacheStrategy.DATA).into(background_imageview);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setIconScaleAnimation() {

        try {
            new Handler().postDelayed(() -> {
                try {
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(download_progress_layout, "scaleX", 1.0f, 0.85f);
                    scaleDownX.setDuration(1200);
                    scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownX.setInterpolator(new LinearInterpolator());
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(download_progress_layout, "scaleY", 1.0f, 0.85f);
                    scaleDownY.setDuration(1200);
                    scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                    scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                    scaleDownY.setInterpolator(new LinearInterpolator());
                    scaleDownY.start();
                    scaleDownX.start();
                    download_progress_layout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }, 700);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //set timer to show hint messages while loading data
    private void setHintMessageToShow() {
        myHandler = new Handler();
        hintMessages = OustAppState.getInstance().getHintMessages();
        if ((hintMessages != null) && (hintMessages.size() > 0)) {
            myHandler.postDelayed(updateVideoTime, 1100);
        }
    }

    private final Runnable updateVideoTime = new Runnable() {
        public void run() {
            if (n1 < 100) {
                if ((hintMessages != null) && (hintMessages.size() > 0)) {
                    try {
                        if (n1 % 2 == 0) {
                            if ((hintMessages.size() > n2) && (hintMessages.get(n2) != null)) {
                                download_content_text.setText(hintMessages.get(n2));
                                ObjectAnimator anim2 = ObjectAnimator.ofFloat(download_content_text, "y", 500, 0.0f);
                                anim2.setDuration(500);
                                anim2.setInterpolator(new DecelerateInterpolator());
                                anim2.start();
                                download_content_text.setVisibility(View.VISIBLE);
                                myHandler.postDelayed(this, 3000);
                                n2++;
                            }
                        } else {
                            ObjectAnimator anim1 = ObjectAnimator.ofFloat(download_content_text, "y", 0.0f, 500);
                            anim1.setDuration(500);
                            anim1.setInterpolator(new AccelerateInterpolator());
                            anim1.start();
                            myHandler.postDelayed(this, 1000);
                        }
                        if (hintMessages.size() <= n2) {
                            n2 = 0;
                        }
                        n1++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
        }
    };

    //check for update if true make list of all media and delete all...
    private void checkForUpdate() {
        try {
            if ((!OustSdkTools.checkInternetStatus()) || (!OustStaticVariableHandling.getInstance().isNetConnectionAvailable())) {
                startFetchingDataOffline();
                return;
            }
            //if fav mode check for course update
            boolean isRefresh = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (!favMode) {
                if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null)) {
                    for (int k = 0; k < dtoUserCourseData.getUserLevelDataList().size(); k++) {
                        if ((dtoUserCourseData.getUserLevelDataList().get(k).getLevelId() == courseLevelClass.getLevelId())) {
                            if ((dtoUserCourseData.getUserLevelDataList().get(k).getTimeStamp() != null) &&
                                    (!dtoUserCourseData.getUserLevelDataList().get(k).getTimeStamp().equalsIgnoreCase(courseLevelClass.getRefreshTimeStamp())) &&
                                    (!dtoUserCourseData.getUserLevelDataList().get(k).getTimeStamp().equalsIgnoreCase("0"))) {
                                isRefresh = true;

                                try {
                                    RoomHelper.setCurrentCardNumber(dtoUserCourseData, k, 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }

                                deleteAllData();
                            }
                        }
                    }
                    if (!isRefresh) {
                        checkUpdatedTime();
                    }
                } else {
                    checkUpdatedTime();
                }
            } else {
                //else
                if ((dtoUserCourseData != null) && (dtoUserCourseData.getUpdateTS() != null)) {
                    if (dtoUserCourseData.getUpdateTS().equalsIgnoreCase(courseDataClass.getUpdateTs())) {
                        isRefresh = true;
                        deleteAllData();
                    }
                    if (!isRefresh) {
                        checkUpdatedTime();
                    }
                } else {
                    checkUpdatedTime();
                }
            }
        } catch (Exception e) {
            checkUpdatedTime();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAllData() {
        try {
            courseCardClassList = new ArrayList<>();
            for (int i = 0; i < courseLevelClass.getCourseCardClassList().size(); i++) {
                int savedCardNo = (int) courseLevelClass.getCourseCardClassList().get(i).getCardId();
                DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardNo);
                if (courseCardClass != null) {
                    if (courseCardClass.getCardId() != 0) {
                        courseCardClass.setSequence(courseLevelClass.getCourseCardClassList().get(i).getSequence());
                        courseCardClassList.add(courseCardClass);
                        RoomHelper.deleteCourseCardClass(courseCardClass.getCardId());
                    }
                }
            }
            createListOfMediaToDelete();
        } catch (Exception e) {
            checkUpdatedTime();
        }
    }

    private void createListOfMediaToDelete() {
        try {
            mediaList = new ArrayList<>();
            List<String> videoMediaList = new ArrayList<>();
            if ((courseCardClassList != null) && (courseCardClassList.size() > 0)) {
                for (int i = 0; i < courseCardClassList.size(); i++) {
                    DTOCourseCard courseCardClass = courseCardClassList.get(i);
                    if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                        for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                            if (courseCardClass.getCardMedia().get(k).getData() != null) {
                                if ((courseCardClass.getCardMedia().get(k).getMediaType() != null) && (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("VIDEO"))) {
                                    videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                } else {
                                    mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                }
                            }
                            if (courseCardClass.getCardMedia().get(k).getGumletVideoUrl() != null) {
                                if ((courseCardClass.getCardMedia().get(k).getMediaType() != null) && (courseCardClass.getCardMedia().get(k).getMediaType().equalsIgnoreCase("VIDEO"))) {
                                    videoMediaList.add(courseCardClass.getCardMedia().get(k).getGumletVideoUrl());
                                } else {
                                    mediaList.add(courseCardClass.getCardMedia().get(k).getGumletVideoUrl());
                                }
                            }
                        }
                    }
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getChildCard() != null)) {
                        if ((courseCardClass.getChildCard().getCardMedia() != null) && (courseCardClass.getChildCard().getCardMedia().size() > 0)) {
                            for (int k = 0; k < courseCardClass.getChildCard().getCardMedia().size(); k++) {
                                if (courseCardClass.getChildCard().getCardMedia().get(k).getData() != null) {
                                    mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                }
                                if (courseCardClass.getChildCard().getCardMedia().get(k).getGumletVideoUrl() != null) {
                                    mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getGumletVideoUrl());
                                }
                            }
                        }
                    }
                    if (courseCardClass != null && courseCardClass.getqId() != 0) {
                        DTOQuestions q = courseCardClass.getQuestionData();
                        OustMediaTools.prepareMediaList(mediaList, mediaList, q);
                    }

                }
            }
            deleteAllResources(videoMediaList);
        } catch (Exception e) {
            checkUpdatedTime();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void deleteAllResources(List<String> videoMediaList) {
        try {
            String pathLocation = Objects.requireNonNull(requireActivity()).getFilesDir().getPath();
            for (int i = 0; i < mediaList.size(); i++) {
                String mediaPath = mediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    mediaPath = OustMediaTools.getMediaFileName(mediaPath);
                }
                String path = pathLocation + ("oustlearn_" + mediaPath);
                File file = new File(path);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d("LCDF", "File deleted " + b);
                }
            }
            for (int i = 0; i < videoMediaList.size(); i++) {
                String path = pathLocation + videoMediaList.get(i);
                File file = new File(path);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d("LCDF", "File deleted " + b);
                }
            }
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                    // handled here for card edited
                    dtoUserCourseData.getUserLevelDataList().get(i).setDownloading(false);
                    userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(0, dtoUserCourseData, i);
                    RoomHelper.addorUpdateScoreDataClass(dtoUserCourseData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        checkUpdatedTime();
    }

    private void startFetchingDataOffline() {
        try {
            boolean downloadedAllCards = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (!isDownloadCardByCard) {
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        if (dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100) {
                            downloadedAllCards = true;
                        }
                        courseCardClassList = courseLevelClass.getCourseCardClassList();
                    }
                }
            }
            if (!downloadedAllCards) {
                mediaList = new ArrayList<>();
                pathList = new ArrayList<>();
                vMediaList = new ArrayList<>();
                videoPathList = new ArrayList<>();
                courseCardClassList = new ArrayList<>();

                entityDownloadedOrNot = RoomHelper.getDownloadedOrNotS();
                if (entityDownloadedOrNot != null && entityDownloadedOrNot.size() > 0) {
                    for (int i = 0; i < entityDownloadedOrNot.size(); i++) {
                        if (entityDownloadedOrNot.get(i).getCourseId() == courseDataClass.getCourseId() && entityDownloadedOrNot.get(i).getPercentage() == 100) {
                            courseDowloaded = true;
                        }
                    }
                }
                if ((!OustSdkTools.checkInternetStatus()) && !courseDowloaded) {
                    showNoInternetLayout();
                } else {
                    download_progress_text.setText("100%");
                    download_content_text.setVisibility(View.GONE);
                    courseCardClassList = courseLevelClass.getCourseCardClassList();
                    if (courseCardClassList != null && courseCardClassList.size() > 0) {
                        new Handler().postDelayed(() -> {
                            try {
                                if (isFragmentLive) {
                                    learningModuleInterface.downloadComplete(courseCardClassList, reStartLevel);
                                } else {
                                    try {
                                        Objects.requireNonNull(requireActivity()).finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        OustSdkTools.sendSentryException(e);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }, 700);
                    } else {
                        try {
                            Objects.requireNonNull(requireActivity()).finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            } else {
                download_progress_text.setText("100%");
                download_content_text.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    try {
                        if (isFragmentLive) {
                            learningModuleInterface.downloadComplete(courseCardClassList, reStartLevel);
                        } else {
                            try {
                                Objects.requireNonNull(requireActivity()).finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 700);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showNoInternetLayout() {
        try {
            download_progress_layout.setVisibility(View.GONE);
            no_connection_text.setVisibility(View.VISIBLE);
            no_connection_text.setText(Objects.requireNonNull(requireActivity()).getResources().getString(R.string.nointernet_message));
            new Handler().postDelayed(() -> {
                try {
                    learningModuleInterface.endActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkUpdatedTime() {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String courseBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_course_details);
                String tenantName = OustPreferences.get("tanentid").trim();
                courseBaseurl = courseBaseurl.replace("{org-id}", "" + tenantName);
                courseBaseurl = courseBaseurl.replace("{courseId}", "" + courseLevelClass.getLpId());
                courseBaseurl = courseBaseurl.replace("{userId}", "" + OustAppState.getInstance().getActiveUser().getStudentid());

                courseBaseurl = HttpManager.getAbsoluteUrl(courseBaseurl);

                Log.d("TAG", "getLearningMap: " + courseBaseurl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, courseBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "getLearningMap - onResponse: " + response.toString());
                        Map<String, Object> learningMap = new HashMap<>();
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            learningMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                        DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());

                        if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null && dtoUserCourseData.getUserLevelDataList().size() > 0) {
                            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                    currentCardNumber = dtoUserCourseData.getUserLevelDataList().get(i).getCurrentCardNo();
                                    if (dtoUserCourseData.getUserLevelDataList().get(i).isLevelCompleted()) {
                                        currentLevelCompleted = dtoUserCourseData.getUserLevelDataList().get(i).isLevelCompleted();
                                        break;
                                    } else {
                                        currentLevelCompleted = false;
                                    }
                                } else {
                                    currentCardNumber = 0;
                                    currentLevelCompleted = false;
                                }
                            }
                        } else {
                            currentCardNumber = 0;
                            currentLevelCompleted = false;
                        }
                        if ((learningMap != null)) {
                            Object o1 = learningMap.get("levels");
                            if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                                List<Object> levelsList = (List<Object>) learningMap.get("levels");
                                if (levelsList != null && levelsList.size() > 0) {
                                    for (int i = 0; i < levelsList.size(); i++) {
                                        if (levelsList.get(i) != null) {
                                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                            if (levelMap.get("levelId") != null) {
                                                long levelId = OustSdkTools.newConvertToLong(levelMap.get("levelId"));
                                                if (levelId == courseLevelClass.getLevelId()) {
                                                    if (levelMap.get("updateTime") != null) {
                                                        String currentTime = (String) levelMap.get("updateTime");
                                                        if ((currentTime != null) && (!currentTime.isEmpty())) {
                                                            List<EntityLevelCardCourseIDUpdateTime> entityLevelCardCourseIDUpdateTime;
                                                            entityLevelCardCourseIDUpdateTime = RoomHelper.getLevelCourseCardUpdateTimeMap((int) courseLevelClass.getLevelId());
                                                            if (entityLevelCardCourseIDUpdateTime != null) {
                                                                if (entityLevelCardCourseIDUpdateTime.size() > 0) {
                                                                    if (OustAppState.getInstance().getActiveUser().getStudentKey().equalsIgnoreCase(entityLevelCardCourseIDUpdateTime.get(0).getStudentKey())) {
                                                                        if (entityLevelCardCourseIDUpdateTime.size() == 0) {
                                                                            startFetchingData(true);
                                                                        } else {
                                                                            if (OustSdkTools.convertToLong(currentTime) > OustSdkTools.convertToLong(entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime())) {
                                                                                if (!levelRestStatus || currentLevelCompleted) {
                                                                                    showPopup(null, "updateDate", OustSdkTools.convertToLong(entityLevelCardCourseIDUpdateTime.get(0).getUpdateTime()), currentTime, currentLevelCompleted, currentCardNumber);
                                                                                } else {
                                                                                    reStartLevel = true;
                                                                                    courseLevelClass.setRefreshTimeStamp(currentTime);
                                                                                    startFetchingData(true);
                                                                                }
                                                                            } else {
                                                                                startFetchingData(false);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        startFetchingData(true);
                                                                    }
                                                                } else {
                                                                    startFetchingData(true);
                                                                }
                                                            } else {
                                                                startFetchingData(true);
                                                            }
                                                        } else {
                                                            startFetchingData(false);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    startFetchingData(false);
                                }
                            } else {
                                startFetchingData(false);
                            }
                        } else {
                            startFetchingData(false);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        startFetchingData(false);
                    }
                });
            } else {
                startFetchingData(false);
            }
        } catch (Exception e) {
            startFetchingData(false);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void resetApiLevelData(long localSaveTime, SubmitCourseCardRequestDataV3 submitCourseCardRequestData, String currentTime) {
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String submitCard_url_v4 = OustSdkApplication.getContext().getResources().getString(R.string.submitCard_url_v4);
                Gson gson = new GsonBuilder().create();
                final String jsonParams;
                SubmitCourseCardRequestDataV3 submitCourseCardRequestDataV3 = new SubmitCourseCardRequestDataV3();
                if (submitCourseCardRequestData == null) {
                    long cplId = OustPreferences.getTimeForNotification("cplId_course");
                    if (cplId == 0) {
                        if (isComingFromCPL) {
                            cplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                        }
                    }
                    Log.d(TAG, "sendCardSubmitRequest: course CP, id:" + cplId);
                    if (cplId > 0) {
                        submitCourseCardRequestDataV3.setCplId("" + cplId);
                    }

                    String gcmToken = OustPreferences.get("gcmToken");
                    if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                        submitCourseCardRequestDataV3.setDeviceToken(gcmToken);
                    }
                    submitCourseCardRequestDataV3.setStudentid(OustAppState.getInstance().getActiveUser().getStudentid());
                    submitCourseCardRequestDataV3.setOfflineRequest(false);

                    List<LearningCardResponceData> tempLearningCardResponseDataList = new ArrayList<>();
                    LearningCardResponceData learningCardResponseData1 = new LearningCardResponceData();
                    learningCardResponseData1.setListNestedVideoQuestion(new ArrayList<>());

                    learningCardResponseData1.setCourseId(courseLevelClass.getLpId());
                    learningCardResponseData1.setCourseColnId("");
                    learningCardResponseData1.setCourseLevelId((int) courseLevelClass.getLevelId());
                    learningCardResponseData1.setCourseCardId(0);
                    learningCardResponseData1.setXp(0);
                    learningCardResponseData1.setCardCompleted(false);
                    learningCardResponseData1.setVideoCompletionPercentage("");
                    learningCardResponseData1.setCorrect(false);
                    learningCardResponseData1.setUserAnswer("");
                    learningCardResponseData1.setUserSubjectiveAns("");
                    learningCardResponseData1.setResponseTime(0);
                    learningCardResponseData1.setLevelUpdateTimeStampOfApp(localSaveTime);
                    learningCardResponseData1.setCardSubmitDateTime("");
                    learningCardResponseData1.setLevelCompleted(false);
                    tempLearningCardResponseDataList.add(learningCardResponseData1);

                    submitCourseCardRequestDataV3.setUserCardResponse(tempLearningCardResponseDataList);
                    jsonParams = gson.toJson(submitCourseCardRequestDataV3);
                } else {
                    submitCourseCardRequestDataV3 = submitCourseCardRequestData;
                    jsonParams = gson.toJson(submitCourseCardRequestData);
                }

                if (mappedAssessmentId > 0) {
                    submitCard_url_v4 = submitCard_url_v4 + "mappedAssessmentId=" + mappedAssessmentId;
                }
                if (mappedSurveyId > 0) {
                    submitCard_url_v4 = submitCard_url_v4 + "&mappedSurveyId=" + mappedSurveyId;
                }
                submitCard_url_v4 = HttpManager.getAbsoluteUrl(submitCard_url_v4);
                Log.d(TAG, "sendRequest: url:" + submitCard_url_v4);
                Log.d(TAG, "sendRequest: cardSubmitData:" + jsonParams);
                SubmitCourseCardRequestDataV3 finalSubmitCourseCardRequestDataV = submitCourseCardRequestDataV3;
                ApiCallUtils.doNetworkCall(Request.Method.POST, submitCard_url_v4, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optBoolean("success")) {
                            if (showSubmitRetryDialog != null && showSubmitRetryDialog.isShowing()) {
                                showSubmitRetryDialog.dismiss();
                                showSubmitRetryDialog = null;
                            }
                            reStartLevel = true;
                            courseLevelClass.setRefreshTimeStamp(currentTime);
                            startFetchingData(true);
                        } else {
                            showPopup(finalSubmitCourseCardRequestDataV, "apiFalling", localSaveTime, currentTime, currentLevelCompleted, currentCardNumber);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showPopup(finalSubmitCourseCardRequestDataV, "apiFalling", localSaveTime, currentTime, currentLevelCompleted, currentCardNumber);
                    }
                });
            } else {
                startFetchingData(false);
            }
        } catch (Exception e) {
            showPopup(null, "apiFalling", localSaveTime, currentTime, currentLevelCompleted, currentCardNumber);
            e.printStackTrace();
        }
    }

    private void showPopup(SubmitCourseCardRequestDataV3 submitCourseCardRequestDataV3, String status, long localSaveTime, String currentTime, boolean currentLevelCompleted, long currentCardNumber) {
        try {
            if (showSubmitRetryDialog != null && showSubmitRetryDialog.isShowing()) {
                showSubmitRetryDialog.dismiss();
                showSubmitRetryDialog = null;
            }
            showSubmitRetryDialog = new Dialog(requireActivity(), R.style.DialogTheme);
            showSubmitRetryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showSubmitRetryDialog.setContentView(R.layout.common_pop_up);
            showSubmitRetryDialog.setCancelable(false);
            Objects.requireNonNull(showSubmitRetryDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            showSubmitRetryDialog.show();

            ImageView info_image = showSubmitRetryDialog.findViewById(R.id.info_image);
            TextView info_title = showSubmitRetryDialog.findViewById(R.id.info_title);
            TextView info_description = showSubmitRetryDialog.findViewById(R.id.info_description);
            LinearLayout info_cancel = showSubmitRetryDialog.findViewById(R.id.info_no);
            LinearLayout save_userData = showSubmitRetryDialog.findViewById(R.id.info_yes);
            TextView info_save_text = showSubmitRetryDialog.findViewById(R.id.info_yes_text);

            String infoDescription;
            if (status != null && status.equalsIgnoreCase("apiFalling")) {
                infoDescription = getResources().getString(R.string.retry_internet_msg);
                info_save_text.setText(getResources().getString(R.string.retry).toUpperCase());
            } else {
                infoDescription = getResources().getString(R.string.level_edited_user_will_re_attempt);
                info_save_text.setText(getResources().getString(R.string.ok).toUpperCase());
            }
            Drawable infoDrawable = getResources().getDrawable(R.drawable.ic_warning_info);

            info_description.setText(infoDescription);
            info_title.setVisibility(View.GONE);
            info_description.setVisibility(View.VISIBLE);
            info_image.setImageDrawable(infoDrawable);

            Drawable drawable = getResources().getDrawable(R.drawable.course_button_bg);
            save_userData.setBackground(OustResourceUtils.setDefaultDrawableColor(drawable));

            info_cancel.setVisibility(View.GONE);
            info_save_text.setText(getResources().getString(R.string.ok).toUpperCase());

            save_userData.setOnClickListener(view -> {
                if (currentLevelCompleted) {
                    if (showSubmitRetryDialog != null && showSubmitRetryDialog.isShowing()) {
                        showSubmitRetryDialog.dismiss();
                        showSubmitRetryDialog = null;
                    }

                    reStartLevel = true;
                    this.currentLevelCompleted = false;
                    courseLevelClass.setRefreshTimeStamp(currentTime);
                    startFetchingData(true);
                } /*else if (currentCardNumber == 0) {
                    if (showSubmitRetryDialog != null && showSubmitRetryDialog.isShowing()) {
                        showSubmitRetryDialog.dismiss();
                        showSubmitRetryDialog = null;
                    }

                    reStartLevel = true;
                    this.currentLevelCompleted = false;
                    courseLevelClass.setRefreshTimeStamp(currentTime);
                    startFetchingData(true);
                }*/ else if (status != null && status.equalsIgnoreCase("apiFalling")) {
                    resetApiLevelData(localSaveTime, submitCourseCardRequestDataV3, currentTime);
                } else {
                    resetApiLevelData(localSaveTime, submitCourseCardRequestDataV3, currentTime);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startFetchingData(boolean isDeleteCard) {
        try {
            boolean downloadedAllCards = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            int levelNo = 0;
            if (!isDownloadCardByCard) {
                if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                    for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                        if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                            if (dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage() == 100) {
                                downloadedAllCards = true;
                            }
                            courseCardClassList = courseLevelClass.getCourseCardClassList();
                            levelNo = i;
                            break;
                        }
                    }
                } else {
                    courseCardClassList = courseLevelClass.getCourseCardClassList();
                }
            }

            if ((!downloadedAllCards || isDeleteCard) || (favMode)) {
                mediaList = new ArrayList<>();
                pathList = new ArrayList<>();
                vMediaList = new ArrayList<>();
                videoPathList = new ArrayList<>();
                courseCardClassList = new ArrayList<>();
                saveRefreshData();
                //TODO: handle card download
                handleCardDownload(isDeleteCard);

            } else {
                download_progress_text.setText("100%");
                userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(100, dtoUserCourseData, levelNo);
                download_content_text.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    try {
                        if (isFragmentLive) {
                            Collections.sort(courseCardClassList, DTOCourseCard.newsCardSorter);
                            learningModuleInterface.downloadComplete(courseCardClassList, reStartLevel);
                        } else {
                            try {
                                Objects.requireNonNull(requireActivity()).finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }, 700);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Objects.requireNonNull(requireActivity()).finish();
        }
    }

    //save latest time stamp used to decide level update
    private void saveRefreshData() {
        try {
            boolean isRefresh = true;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if ((dtoUserCourseData != null) && (dtoUserCourseData.getUserLevelDataList() != null)) {
                for (int k = 0; k < dtoUserCourseData.getUserLevelDataList().size(); k++) {
                    if (courseLevelClass != null) {
                        if ((dtoUserCourseData.getUserLevelDataList().get(k).getLevelId() == courseLevelClass.getLevelId())) {
                            if (dtoUserCourseData.getUserLevelDataList().get(k).getTimeStamp() == null) {
                                isRefresh = false;
                            } else {
                                userCourseScoreDatabaseHandler.setUserLevelDataTimeStamp(courseLevelClass.getRefreshTimeStamp(), dtoUserCourseData, k);

                            }
                        }
                    }
                }
                if (!isRefresh) {
                    addData();
                }
            } else {
                addData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //set new refreshed timestamp
    private void addData() {
        try {
            DTOUserCourseData userCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if ((userCourseData != null) && (userCourseData.getUserLevelDataList() != null)) {
                for (int k = 0; k < userCourseData.getUserLevelDataList().size(); k++) {
                    if (courseLevelClass != null) {
                        if ((userCourseData.getUserLevelDataList().get(k).getLevelId() == courseLevelClass.getLevelId())) {
                            if (userCourseData.getUserLevelDataList().get(k).getTimeStamp() == null) {
                                userCourseScoreDatabaseHandler.setUserLevelDataTimeStamp(courseLevelClass.getRefreshTimeStamp(), userCourseData, k);
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

    private void handleCardDownload(boolean isDeleteCard) {
        try {
            if (courseLevelClass != null) {
                courseUniqueId = OustStaticVariableHandling.getInstance().getCourseUniqNo();
                showLevelNotification();
                downloadingCards(isDeleteCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void showLevelNotification() {
        try {
            // Build a notification using bytesRead and contentLength

            String title = "Downloading course level " + courseLevelClass.getLevelName();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager);
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(Objects.requireNonNull(requireActivity()), "LEVEL_DOWNLOAD");
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.small_app_icon)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify((int) courseUniqueId, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel("LEVEL_DOWNLOAD", "LEVEL_DOWNLOAD_SERVICE", importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }


    private void downloadingCards(boolean isDeleteCard) {
        try {
            downloadedLevelCards = 0;
            if (courseLevelClass.getCourseCardClassList() != null && courseLevelClass.getCourseCardClassList().size() != 0) {
                totalLevelCards = courseLevelClass.getCourseCardClassList().size();
                for (DTOCourseCard dtoCourseCard : courseLevelClass.getCourseCardClassList()) {
                    int downloadingCardId = (int) dtoCourseCard.getCardId();
                    Log.d("updateTimeVal", "five " + courseLevelClass.getLevelId() + " , " + courseLevelClass.getRefreshTimeStamp());
                    if (isDeleteCard) {
                        OustSdkTools.databaseHandler.deleteCardClass(downloadingCardId);
                    }
                    RoomHelper.addLevelCourseCardUpdateTimeMap(courseLevelClass, OustAppState.getInstance().getActiveUser().getStudentKey());
                    DTOCourseCard cardFromDb = OustSdkTools.databaseHandler.getCardClass(downloadingCardId);
                    if (cardFromDb == null || cardFromDb.getCardId() == 0) {
                        downloadCardDataFromServer(dtoCourseCard, courseLevelClass);
                    } else {
                        DTOQuestions dtoQuestions = dtoCourseCard.getQuestionData();
                        if (dtoQuestions != null) {
                            cardFromDb.setQuestionCategory(dtoQuestions.getQuestionCategory());
                            cardFromDb.setQuestionType(dtoQuestions.getQuestionType());
                            cardFromDb.setAudio(dtoQuestions.getAudio());
                        }
                        cardFromDb.setSequence(dtoCourseCard.getSequence());
                        OustSdkTools.databaseHandler.addCardDataClass(cardFromDb, (int) cardFromDb.getCardId());
                        courseCardClassList.add(cardFromDb);
                        downloadedLevelCards++;
                        updateDownloadPercentage(courseLevelClass);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadCardDataFromServer(DTOCourseCard dtoCourseCard, CourseLevelClass courseLevelClass) {
        try {
            String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
            getCardUrl = getCardUrl.replace("cardId", String.valueOf(dtoCourseCard.getCardId()));
            getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseLevelClass.getLpId()));
            getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(courseLevelClass.getLevelId()));
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    LearningCardResponce learningCardResponse = gson.fromJson(response.toString(), LearningCardResponce.class);

                    EntityLevelCardCourseID entityLevelCardCourseID = new EntityLevelCardCourseID();
                    entityLevelCardCourseID.setCardId(dtoCourseCard.getCardId());
                    entityLevelCardCourseID.setCourseId(courseLevelClass.getLpId());
                    entityLevelCardCourseID.setLevelId(courseLevelClass.getLevelId());
                    RoomHelper.addLevelCourseCardMap(entityLevelCardCourseID);

                    cardDataDownloaded(dtoCourseCard, learningCardResponse, courseLevelClass, null);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    cardDataDownloaded(dtoCourseCard, null, courseLevelClass, error.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void cardDataDownloaded(DTOCourseCard dtoCourseCard, LearningCardResponce learningCardResponse, CourseLevelClass courseLevelClass, String errorMessage) {
        try {

            if (learningCardResponse != null && learningCardResponse.isSuccess()) {
                DTOCourseCard cardDataForDb = learningCardResponse.getCard();
                if (cardDataForDb != null) {
                    DTOQuestions dtoQuestions = cardDataForDb.getQuestionData();
                    if (dtoQuestions != null) {
                        cardDataForDb.setQuestionCategory(dtoQuestions.getQuestionCategory());
                        cardDataForDb.setQuestionType(dtoQuestions.getQuestionType());
                        cardDataForDb.setAudio(dtoQuestions.getAudio());
                    }
                    cardDataForDb.setSequence(dtoCourseCard.getSequence());
                    courseCardClassList.add(cardDataForDb);
                    OustSdkTools.databaseHandler.addCardDataClass(cardDataForDb, (int) cardDataForDb.getCardId());
                    downloadCardMedia(cardDataForDb, courseLevelClass);
                } else {
                    OustSdkTools.showToast("Sorry ! Card data is missing . Please contact your admin.");
                    String cardId = "Card Id: ";
                    String levelId = " Level Id: ";
                    if (dtoCourseCard != null && dtoCourseCard.getCardId() != 0) {
                        cardId = cardId + " " + dtoCourseCard.getCardId();
                    }

                    if (courseLevelClass != null && courseLevelClass.getLevelId() != 0) {
                        levelId = levelId + " " + courseLevelClass.getLevelId();
                    }
                    InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                    if (courseDataClass != null) {
                        instrumentationMailRequest.setModuleId(courseDataClass.getCourseId());
                    }
                    instrumentationMailRequest.setModuleType("COURSE");
                    instrumentationMailRequest.setMessageDesc("Card data is missing. Api details : " + cardId + " " + levelId);
                    instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_CARD_NOT_AVAILABLE.toString());
                    InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                    instrumentationHandler.hitInstrumentationAPI(getActivity(), instrumentationMailRequest);
                }
            } else {
                //TODO: retry for five times
                if (retry > 0) {
                    retry--;
                    downloadCardDataFromServer(dtoCourseCard, courseLevelClass);
                } else {
                    if (isFragmentLive) {
                        isFragmentLive = false;
                        if (!OustSdkTools.checkInternetStatus()) {
                            OustSdkTools.showToast(Objects.requireNonNull(requireActivity()).getResources().getString(R.string.retry_internet_msg));
                        } else {
                            OustSdkTools.showToast("Sorry ! Card data is missing . Please contact your admin.");
                            String cardId = "Card Id:";
                            String levelId = " Level Id: ";
                            if (dtoCourseCard != null && dtoCourseCard.getCardId() != 0) {
                                cardId = cardId + " " + dtoCourseCard.getCardId();
                            }

                            if (courseLevelClass != null && courseLevelClass.getLevelId() != 0) {
                                levelId = levelId + " " + courseLevelClass.getLevelId();
                            }
                            InstrumentationMailRequest instrumentationMailRequest = new InstrumentationMailRequest();
                            if (courseDataClass != null) {
                                instrumentationMailRequest.setModuleId(courseDataClass.getCourseId());
                            }
                            instrumentationMailRequest.setModuleType("COURSE");
                            if (errorMessage == null) {
                                errorMessage = "No Localized message";
                            }
                            if (learningCardResponse != null && learningCardResponse.getError() != null) {
                                instrumentationMailRequest.setMessageDesc("Downloading card id is failed. Api details : " + cardId + " " + levelId + " \n API Error : " + learningCardResponse.getError());
                            } else {
                                instrumentationMailRequest.setMessageDesc("Downloading card id is failed. Either response null or error message null .Api details : " + cardId + " " + levelId + " \n " + errorMessage);
                            }
                            instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_CARD_NOT_AVAILABLE.toString());
                            InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                            instrumentationHandler.hitInstrumentationAPI(getActivity(), instrumentationMailRequest);
                        }
                        Objects.requireNonNull(requireActivity()).finish();
                        cancelUpload();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadCardMedia(DTOCourseCard courseCard, CourseLevelClass courseLevelClass) {
        try {
            //store card media data in list
            if ((courseCard.getCardMedia() != null) && (courseCard.getCardMedia().size() > 0)) {
                for (DTOCourseCardMedia cardMedia : courseCard.getCardMedia()) {
                    if (cardMedia.getData() != null) {
                        String mediaType = cardMedia.getMediaType();
                        String path = "";
                        boolean isVideo = false;
                        if (mediaType.equalsIgnoreCase("AUDIO")) {
                            path = "course/media/audio/" + cardMedia.getData();
                            pathList.add(path);
                            mediaList.add(cardMedia.getData());
                            mediaSize++;
                            checkMediaExists(cardMedia.getData(), path, isVideo, courseLevelClass);
                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                            path = "course/media/image/" + cardMedia.getData();
                            pathList.add(path);
                            mediaList.add(cardMedia.getData());
                            mediaSize++;
                            checkMediaExists(cardMedia.getData(), path, isVideo, courseLevelClass);
                        } else if (mediaType.equalsIgnoreCase("GIF")) {
                            path = "course/media/gif/" + cardMedia.getData();
                            pathList.add(path);
                            mediaList.add(cardMedia.getData());
                            mediaSize++;
                            checkMediaExists(cardMedia.getData(), path, isVideo, courseLevelClass);
                        } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                            if (cardMedia.getGumletVideoUrl() != null && !cardMedia.getGumletVideoUrl().isEmpty()) {
                                path = cardMedia.getGumletVideoUrl();
                                videoPathList.add(path);
                                vMediaList.add(cardMedia.getGumletVideoUrl());
                                isVideo = true;
                                mediaSize++;
                                checkMediaExists(cardMedia.getGumletVideoUrl(), path, isVideo, courseLevelClass);
                            } else {
                                path = "course/media/video/" + cardMedia.getData();
                                videoPathList.add(path);
                                vMediaList.add(cardMedia.getData());
                                isVideo = true;
                                mediaSize++;
                                checkMediaExists(cardMedia.getData(), path, isVideo, courseLevelClass);
                            }
                        }
                    }
                }
            }

            //store child card media data in list
            if ((courseCard.getQuestionData() != null) && (courseCard.getChildCard() != null)) {
                if ((courseCard.getChildCard().getCardMedia() != null) && (courseCard.getChildCard().getCardMedia().size() > 0)) {
                    for (DTOCourseCardMedia courseCardMedia : courseCard.getChildCard().getCardMedia()) {
                        if (courseCardMedia.getData() != null) {
                            String mediaType = courseCardMedia.getMediaType();
                            String path = "";
                            boolean isVideo = false;
                            if (mediaType.equalsIgnoreCase("AUDIO")) {
                                path = "course/media/audio/" + courseCardMedia.getData();
                                pathList.add(path);
                                mediaList.add(courseCardMedia.getData());
                                mediaSize++;
                                checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                            } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                path = "course/media/image/" + courseCardMedia.getData();
                                pathList.add(path);
                                mediaList.add(courseCardMedia.getData());
                                mediaSize++;
                                checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                            } else if (mediaType.equalsIgnoreCase("GIF")) {
                                path = "course/media/gif/" + courseCardMedia.getData();
                                pathList.add(path);
                                mediaList.add(courseCardMedia.getData());
                                mediaSize++;
                                checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                            } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                if (courseCardMedia.getGumletVideoUrl() != null && !courseCardMedia.getGumletVideoUrl().isEmpty()) {
                                    path = courseCardMedia.getGumletVideoUrl();
                                    videoPathList.add(path);
                                    vMediaList.add(courseCardMedia.getGumletVideoUrl());
                                    isVideo = true;
                                    mediaSize++;
                                    checkMediaExists(courseCardMedia.getGumletVideoUrl(), path, isVideo, courseLevelClass);
                                } else {
                                    path = "course/media/video/" + courseCardMedia.getData();
                                    videoPathList.add(path);
                                    vMediaList.add(courseCardMedia.getData());
                                    isVideo = true;
                                    mediaSize++;
                                    checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                }

                            }

                        }
                    }
                }
            }

            //store read more card media data in list
            if ((courseCard.getReadMoreData() != null) && (courseCard.getReadMoreData().getRmId() > 0)) {
                String rm_mediaType = courseCard.getReadMoreData().getType();
                String path = "";
                if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                    path = "readmore/file/" + courseCard.getReadMoreData().getData();
                    pathList.add(path);
                    mediaList.add(courseCard.getReadMoreData().getData());
                    mediaSize++;
                } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                    path = "readmore/file/" + courseCard.getReadMoreData().getData();
                    pathList.add(path);
                    mediaList.add(courseCard.getReadMoreData().getData());
                    mediaSize++;
                }
                checkMediaExists(courseCard.getReadMoreData().getData(), path, false, courseLevelClass);
            }

            //set question audio as card audio
            if (courseCard.getQuestionData() != null && courseCard.getQuestionData().getAudio() != null
                    && !courseCard.getQuestionData().getAudio().isEmpty()) {
                courseCard.setAudio(courseCard.getQuestionData().getAudio());
            }

            if (courseCard.getQuestionData() != null && courseCard.getQuestionData().getGumletVideoUrl() != null
                    && !courseCard.getQuestionData().getGumletVideoUrl().isEmpty() && isDownloadVideo) {
                videoPathList.add(courseCard.getQuestionData().getGumletVideoUrl());
                vMediaList.add(OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getGumletVideoUrl()));
                mediaSize++;
                checkMediaExists(courseCard.getQuestionData().getGumletVideoUrl(), OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getGumletVideoUrl()), true, courseLevelClass);
            } else if (courseCard.getQuestionData() != null && courseCard.getQuestionData().getqVideoUrl() != null
                    && !courseCard.getQuestionData().getqVideoUrl().isEmpty() && isDownloadVideo) {
                videoPathList.add(courseCard.getQuestionData().getqVideoUrl());
                vMediaList.add(OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getqVideoUrl()));
                mediaSize++;
                checkMediaExists(courseCard.getQuestionData().getqVideoUrl(), OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getqVideoUrl()), true, courseLevelClass);
            }

            if (courseCard.getQuestionData() != null) {
                if (courseCard.getQuestionData().getImageCDNPath() != null) {
                    String media = courseCard.getQuestionData().getImageCDNPath();
                    String path = courseCard.getQuestionData().getImageCDNPath();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceA() != null && courseCard.getQuestionData().getImageChoiceA().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceA().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceA().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceB() != null && courseCard.getQuestionData().getImageChoiceB().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceB().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceB().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceC() != null && courseCard.getQuestionData().getImageChoiceC().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceC().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceC().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceD() != null && courseCard.getQuestionData().getImageChoiceD().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceD().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceD().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceE() != null && courseCard.getQuestionData().getImageChoiceE().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceE().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceE().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    mediaSize++;
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getMtfLeftCol() != null) {
                    List<DTOMTFColumnData> mtfColumnDatas = courseCard.getQuestionData().getMtfLeftCol();
                    for (int i = 0; i < mtfColumnDatas.size(); i++) {
                        if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                            if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                                pathList.add("");
                                checkMediaExists(mtfColumnDatas.get(i).getMtfColData(), mtfColumnDatas.get(i).getMtfColData(), false, courseLevelClass);
                            }
                        }
                    }
                }

                if (courseCard.getQuestionData().getMtfRightCol() != null) {
                    List<DTOMTFColumnData> mtfColumnDatas = courseCard.getQuestionData().getMtfRightCol();
                    for (int i = 0; i < mtfColumnDatas.size(); i++) {
                        if (mtfColumnDatas.get(i) != null && mtfColumnDatas.get(i).getMtfColData() != null) {
                            if (!mtfColumnDatas.get(i).getMtfColMediaType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(mtfColumnDatas.get(i).getMtfColData());
                                pathList.add("");
                                checkMediaExists(mtfColumnDatas.get(i).getMtfColData(), mtfColumnDatas.get(i).getMtfColData(), false, courseLevelClass);

                            }
                        }
                    }
                }
                if (courseCard.getQuestionData().getOptionCategories() != null) {
                    List<DTOQuestionOptionCategory> optionCategories = courseCard.getQuestionData().getOptionCategories();
                    for (int i = 0; i < optionCategories.size(); i++) {
                        if (optionCategories.get(i) != null && optionCategories.get(i).getData() != null) {
                            if (!optionCategories.get(i).getType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(optionCategories.get(i).getData());
                                pathList.add("");
                                checkMediaExists(optionCategories.get(i).getData(), optionCategories.get(i).getData(), false, courseLevelClass);

                            }
                        }
                    }
                }
                if (courseCard.getQuestionData().getOptions() != null) {
                    List<DTOQuestionOption> options = courseCard.getQuestionData().getOptions();
                    for (int i = 0; i < options.size(); i++) {
                        if (options.get(i) != null && options.get(i).getData() != null) {
                            if (!options.get(i).getType().equalsIgnoreCase("TEXT")) {
                                mediaList.add(options.get(i).getData());
                                pathList.add("");
                                checkMediaExists(options.get(i).getData(), options.get(i).getData(), false, courseLevelClass);

                            }
                        }
                    }
                }
            }

            //store card audio in list
            if ((courseCard.getAudio() != null) && (!courseCard.getAudio().isEmpty())) {
                String audioPath = courseCard.getAudio();
                String s3AudioFileName = audioPath;
                if (audioPath.contains("/")) {
                    s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                }
                pathList.add("qaudio/" + s3AudioFileName);
                mediaList.add(s3AudioFileName);
                mediaSize++;
                checkMediaExists(s3AudioFileName, "qaudio/" + s3AudioFileName, false, courseLevelClass);
            }

            //store solution image in list
            if (courseCard.getChildCard() != null) {
                if (courseCard.getChildCard().getSolutionType() != null) {
                    if (courseCard.getChildCard().getSolutionType().equalsIgnoreCase("IMAGE")) {
                        mediaList.add(OustMediaTools.getMediaFileName(courseCard.getChildCard().getContent()));
                        String path = courseCard.getChildCard().getContent();
                        if (path.startsWith("/"))
                            path = path.replaceFirst("/", "");
                        pathList.add(path);
                        mediaSize++;
                        checkMediaExists(OustMediaTools.getMediaFileName(courseCard.getChildCard().getContent()), path, false, courseLevelClass);
                    }
                }
            }

            //prepare media list for question
            if (courseCard.getqId() != 0) {
                DTOQuestions q = RoomHelper.getQuestionById(courseCard.getqId());
                OustMediaTools.prepareMediaList(mediaList, pathList, q);
            }

            if (courseCard.getQuestionData() != null && courseCard.getQuestionCategory() != null
                    && courseCard.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY)) {
                List<DTOVideoOverlay> videoOverlayList = courseCard.getQuestionData().getListOfVideoOverlayQuestion();
                for (DTOVideoOverlay videoOverlay : videoOverlayList) {
                    try {
                        DTOCourseCard videoOverlayCard = videoOverlay.getChildQuestionCourseCard();
                        if ((videoOverlayCard != null) && (videoOverlayCard.getCardMedia() != null) && (videoOverlayCard.getCardMedia().size() > 0)) {
                            for (DTOCourseCardMedia courseCardMedia : videoOverlayCard.getCardMedia()) {
                                if (courseCardMedia.getData() != null) {
                                    String mediaType = courseCardMedia.getMediaType();
                                    String path = "";
                                    boolean isVideo = false;
                                    if (mediaType.equalsIgnoreCase("AUDIO")) {
                                        path = "course/media/audio/" + courseCardMedia.getData();
                                        pathList.add(path);
                                        mediaList.add(courseCardMedia.getData());
                                        mediaSize++;
                                        checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                    } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                        path = "course/media/image/" + courseCardMedia.getData();
                                        pathList.add(path);
                                        mediaList.add(courseCardMedia.getData());
                                        mediaSize++;
                                        checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                    } else if (mediaType.equalsIgnoreCase("GIF")) {
                                        path = "course/media/gif/" + courseCardMedia.getData();
                                        pathList.add(path);
                                        mediaList.add(courseCardMedia.getData());
                                        mediaSize++;
                                        checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                    } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                        if (courseCardMedia.getGumletVideoUrl() != null && !courseCardMedia.getGumletVideoUrl().isEmpty()) {
                                            path = courseCardMedia.getGumletVideoUrl();
                                            videoPathList.add(path);
                                            vMediaList.add(courseCardMedia.getGumletVideoUrl());
                                            isVideo = true;
                                            mediaSize++;
                                            checkMediaExists(courseCardMedia.getGumletVideoUrl(), path, isVideo, courseLevelClass);
                                        } else {
                                            path = "course/media/video/" + courseCardMedia.getData();
                                            videoPathList.add(path);
                                            vMediaList.add(courseCardMedia.getData());
                                            isVideo = true;
                                            mediaSize++;
                                            checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                        }
                                    }
                                }

                            }
                        }

                        if ((videoOverlayCard != null) && (videoOverlayCard.getQuestionData() != null) && (videoOverlayCard.getChildCard() != null)) {
                            if ((videoOverlayCard.getChildCard().getCardMedia() != null) && (videoOverlayCard.getChildCard().getCardMedia().size() > 0)) {
                                for (DTOCourseCardMedia courseCardMedia : videoOverlayCard.getChildCard().getCardMedia()) {
                                    if (courseCardMedia.getData() != null) {
                                        String mediaType = courseCardMedia.getMediaType();
                                        String path = "";
                                        boolean isVideo = false;
                                        if (mediaType.equalsIgnoreCase("AUDIO")) {
                                            path = "course/media/audio/" + courseCardMedia.getData();
                                            pathList.add(path);
                                            mediaList.add(courseCardMedia.getData());
                                            mediaSize++;
                                            checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                            path = "course/media/image/" + courseCardMedia.getData();
                                            pathList.add(path);
                                            mediaList.add(courseCardMedia.getData());
                                            mediaSize++;
                                            checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                        } else if (mediaType.equalsIgnoreCase("GIF")) {
                                            path = "course/media/gif/" + courseCardMedia.getData();
                                            pathList.add(path);
                                            mediaList.add(courseCardMedia.getData());
                                            mediaSize++;
                                            checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                        } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                            if (courseCardMedia.getGumletVideoUrl() != null && !courseCardMedia.getGumletVideoUrl().isEmpty()) {
                                                path = courseCardMedia.getGumletVideoUrl();
                                                videoPathList.add(path);
                                                vMediaList.add(courseCardMedia.getGumletVideoUrl());
                                                isVideo = true;
                                                mediaSize++;
                                                checkMediaExists(courseCardMedia.getGumletVideoUrl(), path, isVideo, courseLevelClass);
                                            } else {
                                                path = "course/media/video/" + courseCardMedia.getData();
                                                videoPathList.add(path);
                                                vMediaList.add(courseCardMedia.getData());
                                                isVideo = true;
                                                mediaSize++;
                                                checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if ((videoOverlayCard != null) && (videoOverlayCard.getReadMoreData() != null)
                                && (videoOverlayCard.getReadMoreData().getRmId() > 0)) {
                            String rm_mediaType = videoOverlayCard.getReadMoreData().getType();
                            String path = "";
                            if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                                path = "readmore/file/" + videoOverlayCard.getReadMoreData().getData();
                                pathList.add(path);
                                mediaList.add(videoOverlayCard.getReadMoreData().getData());
                                mediaSize++;
                            } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                                path = "readmore/file/" + videoOverlayCard.getReadMoreData().getData();
                                pathList.add(path);
                                mediaList.add(videoOverlayCard.getReadMoreData().getData());
                                mediaSize++;
                            }
                            checkMediaExists(videoOverlayCard.getReadMoreData().getData(), path, false, courseLevelClass);
                        }

                        //if question contain audio download it separately
                        if (videoOverlayCard != null && videoOverlayCard.getQuestionData() != null &&
                                videoOverlayCard.getQuestionData().getAudio() != null && !videoOverlayCard.getQuestionData().getAudio().isEmpty()) {
                            videoOverlayCard.setAudio(videoOverlayCard.getQuestionData().getAudio());
                        }

                        if (videoOverlayCard != null && (videoOverlayCard.getAudio() != null) && (!videoOverlayCard.getAudio().isEmpty())) {
                            String audioPath = videoOverlayCard.getAudio();
                            String s3AudioFileName = audioPath;
                            if (audioPath.contains("/")) {
                                s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                            }
                            pathList.add("qaudio/" + s3AudioFileName);
                            mediaList.add(s3AudioFileName);
                            mediaSize++;
                            checkMediaExists(s3AudioFileName, "qaudio/" + s3AudioFileName, false, courseLevelClass);
                        }

                        if (videoOverlayCard != null && videoOverlayCard.getqId() != 0) {
                            DTOQuestions q1 = RoomHelper.getQuestionById(videoOverlayCard.getqId());
                            OustMediaTools.prepareMediaList(mediaList, pathList, q1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }

            downloadedLevelCards++;
            updateDownloadPercentage(courseLevelClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkMediaExists(String mediaPath, String path, boolean isVideo, CourseLevelClass courseLevelClass) {
        try {
            String pathLocation = Objects.requireNonNull(requireActivity()).getFilesDir().getPath();
            if (mediaPath != null && !mediaPath.isEmpty() && path != null && !path.isEmpty()) {
                if (mediaPath.contains(".zip")) {
                    String newFileName = mediaPath.replace(".zip", "");
                    String unzipLocation = pathLocation + File.separator + newFileName;
                    final File file = new File(unzipLocation);
                    if (!file.exists()) {
                        downloadFileFromServer(mediaPath, path, isVideo, courseLevelClass);
                    } else {
                        downloadedMediaSize++;
                    }
                } else {
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, mediaPath, isVideo, courseLevelClass);
                        } else {
                            downloadedMediaSize++;
                        }
                    } else {
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, path, isVideo, courseLevelClass);
                        } else {
                            downloadedMediaSize++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadFileFromServer(String fileName, String pathName, boolean isVideo, CourseLevelClass courseLevelClass) {
        try {
            boolean isOustLearn = true;
            if (isVideo) {
                isOustLearn = false;
            }
            if (isOustLearn) {
                fileName = "oustlearn_" + fileName;
            }
            downloadManager(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + pathName, fileName, OustSdkApplication.getContext(), courseLevelClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void downloadManager(String downloadUrl, String fileName, Context context, CourseLevelClass courseLevelClass) {
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadUrl);
        String downloadedFileName = "downloaded_" + fileName;

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(downloadedFileName);
        request.setDescription(downloadedFileName);
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        request.setDestinationUri(destinationUri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + downloadedFileName);
        long ref = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);


        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long downloadReference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadReference == -1)
                    return;

                if (downloadReference == ref) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadReference);

                    Cursor cur = downloadManager.query(query);

                    if (cur.moveToFirst()) {
                        int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                            @SuppressLint("Range") String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));                            //update percentage
                            Log.i("DownloadHandler", "Download completed<-> " + uriString);
                            if (uriString != null) {
                                File sourceFile = new File(Objects.requireNonNull(Uri.parse(uriString).getPath()));
                                File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                try {
                                    OustSdkTools.copyFile(sourceFile, destinationFile);
                                    Log.i("DownloadHandler", "Download completed file<-> " + destinationFile.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    OustSdkTools.sendSentryException(e);
                                }

                            }

                            downloadedMediaSize++;
                            updateDownloadPercentage(courseLevelClass);

                        } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                            int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                            int reason = cur.getInt(columnReason);
                            switch (reason) {

                                case DownloadManager.ERROR_FILE_ERROR:
                                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                case DownloadManager.ERROR_UNKNOWN:
                                case DownloadManager.ERROR_CANNOT_RESUME:
                                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                    //handle error
                                    break;
                                case 404:
                                    break;
                            }
                        }
                        // downloadManager.remove(idFromCursor);
                    }
                    cur.close();
                }
            }
        };
        context.getApplicationContext().registerReceiver(receiver, filter);
    }

    private void updateDownloadPercentage(CourseLevelClass courseLevelClass) {
        try {
            int additionalCardData = 0;
            int additionalDownloadedCardData = 0;
            if (mediaSize > 0) {
                additionalCardData = 1;
                if (mediaSize == downloadedMediaSize) {
                    additionalDownloadedCardData = 1;
                }
            }
            double levelDownloadPercentage = (((downloadedLevelCards + additionalDownloadedCardData) * 1.0) / (totalLevelCards + additionalCardData)) * 100;
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = RoomHelper.getScoreById(courseUniqueId);
            if (userCourseData.getUserLevelDataList() == null) {
                userCourseData.setUserLevelDataList(new ArrayList<>());
            }
            DTOUserLevelData dtoUserLevelData = new DTOUserLevelData();
            if (courseDataClass != null) {
                for (int l = 0; l < courseDataClass.getCourseLevelClassList().size(); l++) {
                    boolean alreadyIn = false;
                    for (int k = 0; k < userCourseData.getUserLevelDataList().size(); k++) {
                        if (userCourseData.getUserLevelDataList().get(k).getLevelId() == courseDataClass.getCourseLevelClassList().get(l).getLevelId()) {
                            alreadyIn = true;
                        }
                    }
                    if (!alreadyIn) {
                        dtoUserLevelData.setLevelId(courseDataClass.getCourseLevelClassList().get(l).getLevelId());
                        dtoUserLevelData.setSequece(courseDataClass.getCourseLevelClassList().get(l).getSequence());
                        userCourseData.getUserLevelDataList().add(dtoUserLevelData);
                    }
                }
            }

            for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    if (levelDownloadPercentage >= 100) {
                        levelDownloadPercentage = 100;
                    }
                    userCourseData.getUserLevelDataList().get(i).setCompletePercentage((int) levelDownloadPercentage);
                    userCourseData.getUserLevelDataList().get(i).setDownloading(true);
                    userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage((int) levelDownloadPercentage, userCourseData, i);
                }
            }

            RoomHelper.addorUpdateScoreDataClass(userCourseData);

            if (levelDownloadPercentage == 100 && mediaSize == downloadedMediaSize) {
                cancelUpload();
                if (courseLevelClass != null) {
                    //TODO: handle close fragment
                    download_progress_text.setText(((int) levelDownloadPercentage) + " %");
                    download_content_text.setVisibility(View.GONE);
                    if (isFragmentLive) {
                        learningModuleInterface.downloadComplete(courseCardClassList, reStartLevel);
                    } else {
                        try {
                            Objects.requireNonNull(requireActivity()).finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            } else {
                if (courseLevelClass != null) {
                    download_progress_text.setText(((int) levelDownloadPercentage) + " %");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cancelUpload() {
        try {
            if (isFragmentLive) {
                NotificationManagerCompat.from(Objects.requireNonNull(requireActivity())).cancel((int) courseUniqueId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            isFragmentLive = false;
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
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
            isFragmentLive = false;
            download_progress_layout.setAnimation(null);
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void isComingFromCPl(boolean isComingFromCPL) {
        this.isComingFromCPL = isComingFromCPL;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }

    public void setMappedSurveyId(long mappedSurveyId) {
        this.mappedSurveyId = mappedSurveyId;
    }

    public void setLevelRestStatus(boolean updateLevelStatus) {
        this.levelRestStatus = updateLevelStatus;
    }
}