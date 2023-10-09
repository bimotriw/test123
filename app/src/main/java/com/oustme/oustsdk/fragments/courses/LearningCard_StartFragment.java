package com.oustme.oustsdk.fragments.courses;

import static com.oustme.oustsdk.downloadHandler.DownloadForegroundService.START_UPLOAD;
import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadHandler.DownloadForegroundService;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.interfaces.course.LearningModuleInterface;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.room.EntityLevelCardCourseID;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOReadMore;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

public class LearningCard_StartFragment extends Fragment {

    private static final String TAG = "LearningCard_StartFragm";
    ImageView learningcard_downloadprogressimage, start_backgroundimage, start_backgroundimage_downloaded, learningcard_downloadprogressimagea;
    TextView downloaddata_text, nointernetconnection_text, downloadlabel_content;
    RelativeLayout maindownload_layout, nointernetconnection_layout, learningcard_downloadprogresslayout;

    private int noofTries = 0;
    private List<String> mediaList;
    private List<String> pathList;

    private List<String> vMediaList, videoPathList;

    private int mediaSize = 0;

    private Handler myHandler;
    private int cardSize = 0;
    private List<DTOCourseCard> courseCardClassList = new ArrayList<>();

    private boolean isFragmentLive = true;
    private TransferUtility transferUtility;

    private LearningModuleInterface learningModuleInterface;
    private UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler;

    private boolean isReviewMode;
    private boolean isRegularMode = false;
    private DownloadFiles downloadFiles, downloadFiles2;
    private int downLoadCount;

    public void setReviewMode(boolean reviewMode) {
        isReviewMode = reviewMode;
    }

    public void setRegularMode(boolean isRegularMode) {
        this.isRegularMode = isRegularMode;
    }

    private boolean favMode;

    public void setFavMode(boolean favMode) {
        this.favMode = favMode;
    }

    private CourseDataClass courseDataClass;

    public void setCourseDataClass(CourseDataClass courseDataClass) {
        this.courseDataClass = courseDataClass;
    }

    public void setDownloadVideo(boolean downloadVideo) {
        isDownloadVideo = downloadVideo;
    }

    boolean isDownloadVideo = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learningcardstart, container, false);
        initViews(view);
        userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        initLearningCardView();
        return view;
    }

    private void initViews(View view) {
        Log.d(TAG, "initViews: ");
        learningcard_downloadprogressimage = view.findViewById(R.id.learningcard_downloadprogressimage);
        downloaddata_text = view.findViewById(R.id.downloaddata_text);
        maindownload_layout = view.findViewById(R.id.maindownload_layout);
        nointernetconnection_layout = view.findViewById(R.id.nointernetconnection_layout);
        nointernetconnection_text = view.findViewById(R.id.nointernetconnection_text);
        start_backgroundimage = view.findViewById(R.id.start_backgroundimage);
        start_backgroundimage_downloaded = view.findViewById(R.id.start_backgroundimage_downloaded);
        downloadlabel_content = view.findViewById(R.id.downloadlabel_content);
        learningcard_downloadprogresslayout = view.findViewById(R.id.learningcard_downloadprogresslayout);
        learningcard_downloadprogressimagea = view.findViewById(R.id.learningcard_downloadprogressimagea);

        nointernetconnection_text.setText(getActivity().getResources().getString(R.string.nointernet_message));
        downloadlabel_content.setText(getActivity().getResources().getString(R.string.fetching_data_msg));

        OustSdkTools.setImage(start_backgroundimage, getActivity().getResources().getString(R.string.bg_1));
    }

    public void setLearningModuleInterface(LearningModuleInterface learningModuleInterface) {
        this.learningModuleInterface = learningModuleInterface;
    }

    private String backgroundImage;

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    private boolean isDownloadingstrategyCardByCard = false;

    public void setCardSize(int cardSize) {
        this.cardSize = cardSize;
        if (courseLevelClass != null) {
            if (cardSize < courseLevelClass.getCourseCardClassList().size()) {
                isDownloadingstrategyCardByCard = true;
            }
        }
    }

    private CourseLevelClass courseLevelClass;

    public void setCourseLevelClass(CourseLevelClass courseLevelClass) {
        this.courseLevelClass = courseLevelClass;
    }

    public void initLearningCardView() {
        try {
            Log.d(TAG, "initLearningCardView: ");
            setLogo();
            //initS3Client();
            setViewBackgroundImage();
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
                if (courseLevelClass != null) {
                    cardSize = courseLevelClass.getCourseCardClassList().size();
                }
            }
            if (isRegularMode) {
                Log.d(TAG, "No animation");
                downloadlabel_content.setVisibility(View.GONE);
                learningcard_downloadprogresslayout.setVisibility(View.GONE);
                downloaddata_text.setVisibility(View.GONE);
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
                        setOustIconScaleAnimation();
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

    //initialize s3 clicent to download course resources
    private void initS3Client() {
        Log.d(TAG, "initS3Client: ");
        try {
            String awsKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeyId");
            String awsSecretKeyId = com.oustme.oustsdk.tools.OustPreferences.get("awsS3KeySecret");
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(awsKeyId, awsSecretKeyId));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setViewBackgroundImage() {
        try {
            // OustSdkTools.setImage(start_backgroundimage, getResources().getString(R.string.bg_1));
            if ((backgroundImage != null) && (!backgroundImage.isEmpty())) {
                start_backgroundimage_downloaded.setVisibility(View.VISIBLE);
                Glide.with(Objects.requireNonNull(requireActivity())).load(backgroundImage).diskCacheStrategy(DiskCacheStrategy.DATA).into(start_backgroundimage_downloaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setLogo() {
        Log.d(TAG, "setLogo: ");
        try {
            File file = new File(OustSdkApplication.getContext().getFilesDir(), "oustlearn_" + (OustPreferences.get("tanentid").toUpperCase()) + "splashIcon");
            if (file.exists()) {
                Glide.with(Objects.requireNonNull(requireActivity())).load(file).into(learningcard_downloadprogressimagea);
                learningcard_downloadprogressimage.setVisibility(View.GONE);
                learningcard_downloadprogressimagea.setVisibility(View.VISIBLE);
            } else {
                if (OustStaticVariableHandling.getInstance().isWhiteLabeledApp()) {
                    learningcard_downloadprogressimage.setVisibility(View.GONE);
                    learningcard_downloadprogressimagea.setVisibility(View.VISIBLE);
                    OustSdkTools.setImage(learningcard_downloadprogressimagea, Objects.requireNonNull(requireActivity()).getResources().getString(R.string.app_icon));
                } else {
                    learningcard_downloadprogressimage.setVisibility(View.VISIBLE);
                    learningcard_downloadprogressimagea.setVisibility(View.GONE);
                    OustSdkTools.setImage(learningcard_downloadprogressimage, Objects.requireNonNull(requireActivity()).getResources().getString(R.string.app_icon));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void setOustIconScaleAnimation() {
        Log.d(TAG, "setOustIconScaleAnimation: ");
        if (isRegularMode) {
            Log.d(TAG, "No animation");
            downloadlabel_content.setVisibility(View.GONE);
            learningcard_downloadprogresslayout.setVisibility(View.GONE);
            downloaddata_text.setVisibility(View.GONE);
            return;
        }
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(learningcard_downloadprogresslayout, "scaleX", 1.0f, 0.85f);
                        scaleDownX.setDuration(1200);
                        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
                        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownX.setInterpolator(new LinearInterpolator());
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(learningcard_downloadprogresslayout, "scaleY", 1.0f, 0.85f);
                        scaleDownY.setDuration(1200);
                        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
                        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
                        scaleDownY.setInterpolator(new LinearInterpolator());
                        scaleDownY.start();
                        scaleDownX.start();
                        learningcard_downloadprogresslayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }
                }
            }, 700);
        } catch (Exception e) {
        }
    }

    //set timer to show hint messages while loading data
    private void setHintMessageToShow() {
        Log.d(TAG, "setHintMessageToShow: ");
        myHandler = new Handler();
        hintMessages = OustAppState.getInstance().getHintMessages();
//        Log.d(TAG, "setHintMessageToShow: "+hintMessages.get(0));
        if ((hintMessages != null) && (hintMessages.size() > 0)) {
            myHandler.postDelayed(UpdateVideoTime, 1100);
        }
    }

    private int n1 = 0;
    private int n2 = 0;
    private List<String> hintMessages;
    private Runnable UpdateVideoTime = new Runnable() {
        public void run() {
            if (n1 < 100) {
                if ((hintMessages != null) && (hintMessages.size() > 0)) {
                    try {
                        if (n1 % 2 == 0) {
                            if ((hintMessages.size() > n2) && (hintMessages.get(n2) != null)) {
                                downloadlabel_content.setText(hintMessages.get(n2));
                                ObjectAnimator anim2 = ObjectAnimator.ofFloat(downloadlabel_content, "y", 500, 0.0f);
                                anim2.setDuration(500);
                                anim2.setInterpolator(new DecelerateInterpolator());
                                anim2.start();
                                downloadlabel_content.setVisibility(View.VISIBLE);
                                myHandler.postDelayed(this, 3000);
                                n2++;
                            }
                        } else {
                            ObjectAnimator anim1 = ObjectAnimator.ofFloat(downloadlabel_content, "y", 0.0f, 500);
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
                    }
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        isFragmentLive = true;
        try {
            if (downloaddata_text.getText().toString().equalsIgnoreCase("100%")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            downloadlabel_content.setVisibility(View.GONE);
                            learningModuleInterface.downloadComplete(courseCardClassList, false);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }
        isFragmentLive = false;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        if (myFileDownLoadReceiver != null) {
            if (getActivity() != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myFileDownLoadReceiver);
            }
        }

        if (downloadUpdateReceiver != null) {
            if (getActivity() != null) {
                LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(downloadUpdateReceiver);
            }
        }
    }

    //-------------------------------------------------------------------------
    //check for upadate if true make list of all media and delete all...
    private void checkForUpdate() {
        Log.d(TAG, "checkForUpdate: ");
        try {
            //if no network cotineu with old data
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
                        startFetchingData();
                    }
                } else {
                    startFetchingData();
                }
            } else {
                //else
                if ((dtoUserCourseData != null) && (dtoUserCourseData.getUpdateTS() != null)) {
                    if (dtoUserCourseData.getUpdateTS().equalsIgnoreCase(courseDataClass.getUpdateTs())) {
                        isRefresh = true;
                        deleteAllData();
                    }
                    if (!isRefresh) {
                        startFetchingData();
                    }
                } else {
                    startFetchingData();
                }
            }
        } catch (Exception e) {
            startFetchingData();
        }
    }

    //if true delete all cards and media
    private int savedCardNo;

    private void deleteAllData() {
        Log.d(TAG, "deleteAllData: ");
        try {
            courseCardClassList = new ArrayList<>();
            for (int i = 0; i < courseLevelClass.getCourseCardClassList().size(); i++) {
                savedCardNo = getId(courseLevelClass.getCourseCardClassList().get(i).getCardId());
                DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardNo);
                if ((courseCardClass != null) && (courseCardClass.getCardId() == 0)) {
                } else {
                    courseCardClassList.add(courseCardClass);
                    RoomHelper.deleteCourseCardClass(courseCardClass.getCardId());
                }
            }
            createListOfMediaToDelete();
        } catch (Exception e) {
            startFetchingData();
        }
    }

    private void createListOfMediaToDelete() {
        Log.d(TAG, "createListOfMediaToDelete: ");
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
                        }
                    }
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getChildCard() != null)) {
                        if ((courseCardClass.getChildCard().getCardMedia() != null) && (courseCardClass.getChildCard().getCardMedia().size() > 0)) {
                            for (int k = 0; k < courseCardClass.getChildCard().getCardMedia().size(); k++) {
                                if (courseCardClass.getChildCard().getCardMedia().get(k).getData() != null) {
                                    mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                }
                            }
                        }
                    }
                    if (courseCardClass.getqId() != 0) {
                        DTOQuestions q = courseCardClass.getQuestionData();
                        OustMediaTools.prepareMediaList(mediaList, mediaList, q);
                    }

                }
            }
            mediaSize = mediaList.size();
            deleteAllResourses(videoMediaList);
        } catch (Exception e) {
            startFetchingData();
        }
    }

    private void deleteAllResourses(List<String> videoMediaList) {
        Log.d(TAG, "deleteAllResourses: ");
        try {
            for (int i = 0; i < mediaList.size(); i++) {
                String mediaPath = mediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    mediaPath = OustMediaTools.getMediaFileName(mediaPath);
                }
                //EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + ("oustlearn_" + mediaPath);
                File file = new File(path);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d(TAG, "File deleted " + b);
                }
                //enternalPrivateStorage.deleteMediaFile(("oustlearn_" + mediaPath));
            }
            for (int i = 0; i < videoMediaList.size(); i++) {
                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoMediaList.get(i);
                File file = new File(path);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d(TAG, "File deleted " + b);
                }
            }
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    dtoUserCourseData.getUserLevelDataList().get(i).setCompletePercentage(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        startFetchingData();
    }
//-------------------------------------------------------------------------------

    private void startFetchingData() {
        Log.d(TAG, "startFetchingData: ");
        try {
            boolean downloadedAllCards = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            int levelNo = 0;
            if (!isDownloadingstrategyCardByCard) {
                if (dtoUserCourseData != null && dtoUserCourseData.getUserLevelDataList() != null) {
                    for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                        if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                            //downloadedAllCards = dtoUserCourseData.getUserLevelDataList().get(i).isDownloadedAllCards();
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
            if ((!downloadedAllCards) || (favMode)) {
                mediaList = new ArrayList<>();
                pathList = new ArrayList<>();

                vMediaList = new ArrayList<>();
                videoPathList = new ArrayList<>();

                mediaSize = 0;
                courseCardClassList = new ArrayList<>();
                saveRefreshData();
                //checkForCardExits();//TODO: handle service
                //TODO: invoke service
                try {
                    Gson gson = new Gson();
                    String courseLevelStr = gson.toJson(courseLevelClass);
                    Intent intent = new Intent(getActivity(), DownloadForegroundService.class);
                    intent.setAction(START_UPLOAD);
                    intent.putExtra(DownloadForegroundService.LEVEL_DATA, courseLevelStr);
                    intent.putExtra(DownloadForegroundService.LEVEL_DATA_FRAGMENT, true);
                    intent.putExtra(DownloadForegroundService.COURSE_UNIQUE, ("" + OustStaticVariableHandling.getInstance().getCourseUniqNo()));
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_VIDEO, isDownloadVideo);
                    intent.putExtra(DownloadForegroundService.IS_DOWNLOAD_LEVEL, true);
                    Objects.requireNonNull(requireActivity()).startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            } else {
                downloaddata_text.setText("100%");
                userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(100, dtoUserCourseData, levelNo);
                downloadlabel_content.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    try {
                        if (isFragmentLive) {
                            learningModuleInterface.downloadComplete(courseCardClassList, false);
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

    private void startFetchingDataOffline() {
        Log.d(TAG, "startFetchingDataOffline: ");
        try {
            boolean downloadedAllCards = false;
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            if (!isDownloadingstrategyCardByCard) {
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        if(dtoUserCourseData.getUserLevelDataList().get(i).getCompletePercentage()==100){
                            downloadedAllCards = true;
                        }
                        //downloadedAllCards = dtoUserCourseData.getUserLevelDataList().get(i).isDownloadedAllCards();
                        courseCardClassList = courseLevelClass.getCourseCardClassList();
                    }
                }
            }
            if (!downloadedAllCards) {
                mediaList = new ArrayList<>();
                pathList = new ArrayList<>();
                vMediaList = new ArrayList<>();
                videoPathList = new ArrayList<>();
                mediaSize = 0;
                courseCardClassList = new ArrayList<>();
                //checkForCardExits();
                if ((!OustSdkTools.checkInternetStatus())) {
                    showNoInternetLayout();
                }
            } else {
                downloaddata_text.setText("100%");
                downloadlabel_content.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isFragmentLive) {
                                learningModuleInterface.downloadComplete(courseCardClassList, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }, 700);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //download all cards using rest call
    private int totalDownloadedCards = 0;
    private int downloadStartedCards = 0;

    private void checkForCardExits() {
        Log.d(TAG, "checkForCardExits: ");
        try {
            totalDownloadedCards = downloadStartedCards;
            if (courseLevelClass != null) {
                downloadStartedCards += courseLevelClass.getCourseCardClassList().size();
            }
            if (downloadStartedCards > cardSize) {
                downloadStartedCards = cardSize;
            }

            if (courseLevelClass != null) {
                for (int i = totalDownloadedCards; i < downloadStartedCards; i++) {
                    if (isReviewMode && favMode && courseLevelClass.getCourseCardClassList().get(i).isReadMoreCard()) {
                        if (courseLevelClass.getCourseCardClassList().get(i).getReadMoreData() != null) {
                            DTOCourseCard courseCardClass = new DTOCourseCard();
                            courseCardClass.setReadMoreData(courseLevelClass.getCourseCardClassList().get(i).getReadMoreData());
                            courseCardClass.setReadMoreCard(courseLevelClass.getCourseCardClassList().get(i).isReadMoreCard());
                            courseCardClass.setCardId(courseLevelClass.getCourseCardClassList().get(i).getCardId());
                            courseCardClass.setCardType("LEARNING");
//                        OustSdkTools.databaseHandler.addCardDataClass(courseCardClass,(int)courseCardClass.getCardId());
                            courseCardClassList.add(courseCardClass);
                        }
                    } else {
                        int savedCardID = getId(courseLevelClass.getCourseCardClassList().get(i).getCardId());
                        DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
                        DTOCourseCard dtoCourseCard = RoomHelper.getCourseCardByCardId(savedCardID);
                        if (dtoCourseCard == null) {
                            dtoCourseCard = new DTOCourseCard();
                        }
                        Log.d(TAG, "checkForCardExits: " + courseLevelClass.getCourseCardClassList().get(i).getCardId());
                        //downloadCard(courseLevelClass.getCourseCardClassList().get(i), (int)courseLevelClass.getCourseCardClassList().get(i).getCardId());
                        if ((dtoCourseCard != null) && (dtoCourseCard.getCardId() == 0)) {
                            if ((!OustSdkTools.checkInternetStatus())) {
                                showNoInternetLayout();
                                return;
                            } else {
                                downloadCard(courseLevelClass.getCourseCardClassList().get(i), savedCardID);
                            }
                        } else {
                            DTOQuestions dtoQuestions = dtoCourseCard.getQuestionData();
                            if (dtoQuestions != null) {
                                courseCardClass.setCardId(dtoCourseCard.getCardId());
                                courseCardClass.setQuestionCategory(dtoQuestions.getQuestionCategory());
                                courseCardClass.setQuestionType(dtoQuestions.getQuestionType());
                                courseCardClass.setAudio(courseCardClass.getQuestionData().getAudio());
                                courseCardClass.setCardType(dtoCourseCard.getCardType());
                                //courseCardClass.setQuestionData(null);
                                //courseCardClass.setQuestionData();
                            }
                            if (courseLevelClass.getCourseCardClassList().get(i).getSequence() > 0)
                                courseCardClass.setSequence(courseLevelClass.getCourseCardClassList().get(i).getSequence());
                            courseCardClassList.add(courseCardClass);
                            showDownloadProgress();
                        }
                    }
                }
            }
            checkDownloadOver();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("ReadMore", "   ", e);
        }
    }


    public void downloadCard(final DTOCourseCard courseCardClass, final int saveCardId) {
        Log.d(TAG, "downloadCard: from LearningCard_start" + saveCardId + " ");
        long levelId = 0;
        int courseId = 0;
        if (courseLevelClass != null) {
            levelId = courseLevelClass.getLevelId();
            courseId = courseLevelClass.getLpId();
        }
        String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
        //String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCard_url);
        getCardUrl = getCardUrl.replace("cardId", String.valueOf(courseCardClass.getCardId()));
        getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseId));
        getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(levelId));
        try {
            final String finalGetCardUrl = getCardUrl;
            Log.d(TAG, "downloadCard: " + getCardUrl);
            /*new Thread() {
                @Override
                public void run() {
                    try {
                        OustRestClient oustRestClient = new OustRestClient();
                        Log.d(TAG, "downloadCard: from LearningCard_start: "+finalGetCardUrl);
                        LearningCardResponce learningCardResponce = oustRestClient.downloadCardData(finalGetCardUrl);

                        EntityLevelCardCourseID realmLevelCardCourseIDModel = new EntityLevelCardCourseID();
                        realmLevelCardCourseIDModel.setCardId((int)courseCardClass.getCardId());
                        realmLevelCardCourseIDModel.setCourseId((int)courseId);
                        realmLevelCardCourseIDModel.setLevelId((int)levelId);
                        RoomHelper.addLevelCourseCardMap(realmLevelCardCourseIDModel);

                        gotResponse(learningCardResponce, courseCardClass, saveCardId);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }.start();*/

            /*final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(null);*/
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            int finalCourseId = courseId;
            long finalLevelId = levelId;
            ApiCallUtils.doNetworkCall(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JasonData: " + response.toString());
                    LearningCardResponce learningCardResponce = gson.fromJson(response.toString(), LearningCardResponce.class);

                    EntityLevelCardCourseID entityLevelCardCourseID = new EntityLevelCardCourseID();
                    entityLevelCardCourseID.setCardId(courseCardClass.getCardId());
                    entityLevelCardCourseID.setCourseId(finalCourseId);
                    entityLevelCardCourseID.setLevelId(finalLevelId);
                    RoomHelper.addLevelCourseCardMap(entityLevelCardCourseID);

                    gotResponse(learningCardResponce, courseCardClass, saveCardId);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: " + error.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getLearningCardResponse(JSONObject response) {
        LearningCardResponce learningCardResponce = new LearningCardResponce();
        if (response != null) {
            learningCardResponce.setSuccess(response.optBoolean("success"));
            learningCardResponce.setError(response.optString("error"));
            learningCardResponce.setUserDisplayName(response.optString("userDisplayName"));
            JSONObject cardResponse = response.optJSONObject("card");
            if (cardResponse != null)
                learningCardResponce.setCard(getCourseCardResponse(cardResponse));
        }

    }

    private DTOCourseCard getCourseCardResponse(JSONObject response) {

        DTOCourseCard courseCardClass = new DTOCourseCard();
        courseCardClass.setCardBgImage(response.optString("cardBgImage"));
        courseCardClass.setBgImg(response.optString("bgImg"));
        courseCardClass.setCardBgColor(response.optString("cardBgColor"));
        courseCardClass.setCardId(response.optLong("cardId"));
        courseCardClass.setCardLayout(response.optString("cardLayout"));
        courseCardClass.setCardQuestionColor(response.optString("cardQuestionColor"));
        courseCardClass.setCardSolutionColor(response.optString("cardSolutionColor"));
        courseCardClass.setCardTextColor(response.optString("cardTextColor"));
        courseCardClass.setCardType(response.optString("cardType"));
        courseCardClass.setCardTitle(response.optString("cardTitle"));
        courseCardClass.setqId(response.optLong("qId"));
        courseCardClass.setXp(response.optLong("xp"));
        courseCardClass.setSequence(response.optLong("sequence"));
        courseCardClass.setClCode(response.optString("clCode"));
        courseCardClass.setLanguage(response.optString("language"));
        courseCardClass.setQuestionType(response.optString("questionType"));
        courseCardClass.setQuestionCategory(response.optString("questionCategory"));
        courseCardClass.setReadMoreCard(response.optBoolean("isReadMoreCard"));
        courseCardClass.setContent(response.optString("content"));

        DTOReadMore readMoreData = new DTOReadMore();
        JSONObject readMoreJson = response.optJSONObject("readMoreData");
        if (readMoreJson != null) {
            readMoreData.setCardId(readMoreJson.optString("cardId"));
            readMoreData.setCourseId(readMoreJson.optString("courseId"));
            readMoreData.setData(readMoreJson.optString("data"));
            readMoreData.setDisplayText(readMoreJson.optString("displayText"));
            readMoreData.setLevelId(readMoreJson.optString("levelId"));
            readMoreData.setRmId(readMoreJson.optLong("rmId"));
            readMoreData.setScope(readMoreJson.optString("scope"));
            readMoreData.setType(readMoreJson.optString("type"));
            courseCardClass.setReadMoreData(readMoreData);
        }
        JSONArray cardMediaArray = response.optJSONArray("cardMedia");

        List<CourseCardMedia> cardMediaList = new ArrayList<>();

//        if(cardMediaArray!=null){
//            for(int i=0;i<cardMedias.size();i++){
//                RealmCourseCardMedia courseCardMedia= cardMedias.get(i);
//                CourseCardMedia realmCourseCardMedia=new CourseCardMedia();
//                realmCourseCardMedia.setData(courseCardMedia.getData());
//                realmCourseCardMedia.setFastForwardMedia(courseCardMedia.isFastForwardMedia());
//                realmCourseCardMedia.setMediaDescription(courseCardMedia.getMediaDescription());
//                realmCourseCardMedia.setMediaName(courseCardMedia.getMediaName());
//                realmCourseCardMedia.setMediaPrivacy(courseCardMedia.getMediaPrivacy());
//                realmCourseCardMedia.setMediaThumbnail(courseCardMedia.getMediaThumbnail());
//                realmCourseCardMedia.setMediaType(courseCardMedia.getMediaType());
//                cardMediaList.add(realmCourseCardMedia);
//            }
//            realmCourseCardClass.setCardMedia(cardMediaList);
//        }
//        CourseSolutionCard realmCourseSolutionCard=new CourseSolutionCard();
//        RealmCourseSolutionCard courseSolutionCard=courseCardClass.getChildCard();
//        if(courseSolutionCard!=null){
//            realmCourseSolutionCard.setCardBgColor(courseSolutionCard.getCardBgColor());
//            realmCourseSolutionCard.setCardId(courseSolutionCard.getCardId());
//            realmCourseSolutionCard.setCardLayout(courseSolutionCard.getCardLayout());
//            realmCourseSolutionCard.setCardQuestionColor(courseSolutionCard.getCardQuestionColor());
//            realmCourseSolutionCard.setCardSolutionColor(courseSolutionCard.getCardSolutionColor());
//            realmCourseSolutionCard.setCardTextColor(courseSolutionCard.getCardTextColor());
//            realmCourseSolutionCard.setCardType(courseSolutionCard.getCardType());
//            realmCourseSolutionCard.setContent(courseSolutionCard.getContent());
//            realmCourseSolutionCard.setRewardOc(courseSolutionCard.getRewardOc());
//            realmCourseSolutionCard.setSequence(courseSolutionCard.getSequence());
//
//            CardColorScheme realmCardColorScheme =new CardColorScheme();
//            RealmCardColorScheme cardColorScheme=courseSolutionCard.getCardColorScheme();
//            if(cardColorScheme!=null){
//                realmCardColorScheme.setBgImage(cardColorScheme.getBgImage());
//                realmCardColorScheme.setContentColor(cardColorScheme.getContentColor());
//                realmCardColorScheme.setIconColor(cardColorScheme.getIconColor());
//                realmCardColorScheme.setLevelNameColor(cardColorScheme.getLevelNameColor());
//                realmCardColorScheme.setOptionColor(cardColorScheme.getOptionColor());
//                realmCardColorScheme.setTitleColor(cardColorScheme.getTitleColor());
//                realmCourseSolutionCard.setCardColorScheme(realmCardColorScheme);
//            }
//        }
//        realmCourseCardClass.setChildCard(realmCourseSolutionCard);
//        CardColorScheme realmCardColorScheme =new CardColorScheme();
//        RealmCardColorScheme cardColorScheme=courseCardClass.getCardColorScheme();
//        if(cardColorScheme!=null){
//            realmCardColorScheme.setBgImage(cardColorScheme.getBgImage());
//            realmCardColorScheme.setContentColor(cardColorScheme.getContentColor());
//            realmCardColorScheme.setIconColor(cardColorScheme.getIconColor());
//            realmCardColorScheme.setLevelNameColor(cardColorScheme.getLevelNameColor());
//            realmCardColorScheme.setOptionColor(cardColorScheme.getOptionColor());
//            realmCardColorScheme.setTitleColor(cardColorScheme.getTitleColor());
//            realmCourseCardClass.setCardColorScheme(realmCardColorScheme);
//        }
//        RealmQuestions realmQuestions=RealmHelper.getQuestionById(courseCardClass.getqId());
//        if(realmQuestions!=null) {
//            Questions questions = getQuestionFromRealm(realmQuestions);
//            realmCourseCardClass.setQuestionData(questions);
//        }
//        return realmCourseCardClass;


        return courseCardClass;
    }

    public void gotResponse(final LearningCardResponce learningCardResponce, final DTOCourseCard courseCardClass, final int savedCardID) {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardDownloadOver(learningCardResponce, courseCardClass, savedCardID);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void cardDownloadOver(LearningCardResponce learningCardResponce, final DTOCourseCard courseCardClass1, int savedCardID) {
        try {
            if ((learningCardResponce != null) && (learningCardResponce.isSuccess())) {
                DTOCourseCard courseCardClass = learningCardResponce.getCard();
                OustSdkTools.databaseHandler.addCardDataClass(learningCardResponce.getCard(), savedCardID);

                if (courseCardClass.getQuestionData() != null) {
                    courseCardClass.setQuestionCategory(courseCardClass.getQuestionData().getQuestionCategory());
                    courseCardClass.setQuestionType(courseCardClass.getQuestionData().getQuestionType());
                    courseCardClass.setAudio(courseCardClass.getQuestionData().getAudio());
                    //courseCardClass.setQuestionData(null);
                }

                if (courseCardClass1.getSequence() > 0)
                    courseCardClass.setSequence(courseCardClass1.getSequence());
                courseCardClassList.add(courseCardClass);
                showDownloadProgress();
                checkDownloadOver();
            } else {
                noofTries++;
                if (noofTries > 2) {
                    Log.d(TAG, "cardDownloadOver: " + noofTries);
                    showNetworkErrorMessage();
                } else {
                    downloadCard(courseCardClass1, savedCardID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showNetworkErrorMessage() {
        if (isFragmentLive) {
            isFragmentLive = false;
            Log.e(TAG, "showNetworkErrorMessage: " + getActivity().getResources().getString(R.string.retry_internet_msg));
            Log.d(TAG, "showNetworkErrorMessage: " + getActivity().getResources().getString(R.string.retry_internet_msg));
            OustSdkTools.showToast(getActivity().getResources().getString(R.string.retry_internet_msg));
            getActivity().finish();
        }
    }

    public void checkDownloadOver() {
        Log.d(TAG, "checkDownloadOver: ");
        try {
            if (courseCardClassList.size() >= cardSize) {
                if (!favMode) {
                    Collections.sort(courseCardClassList, cardSort);
                }
                createListOfMedia();
            } else {
                if (courseCardClassList.size() >= downloadStartedCards) {
                    checkForCardExits();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public Comparator<DTOCourseCard> cardSort = new Comparator<DTOCourseCard>() {
        public int compare(DTOCourseCard s1, DTOCourseCard s2) {
            return ((int) s1.getSequence()) - ((int) s2.getSequence());
        }
    };

    private void showDownloadProgress() {
        Log.d(TAG, "showDownloadProgress: ");
        try {
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            float f1 = (float) courseCardClassList.size();
            float f2 = (float) cardSize;
            float n2 = f1 / f2;
            float n1 = (n2) * 50;
            int progressa = (int) n1;
            downloaddata_text.setText(progressa + "%");
            if (!isDownloadingstrategyCardByCard) {
                for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                    if (courseLevelClass != null) {
                        if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                            userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(progressa, dtoUserCourseData, i);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int getId(long cardId) {
        int newCardId = (int) cardId;
//        try {
//            String s2 = NewLearningMapPresenter.cardStartingId + "" + cardId;
//            newCardId=Integer.parseInt(s2);
//        }catch (Exception e){}
        return newCardId;
    }

    //-----------------------------------------------------------------------------------------------------
    //create list of all media and video media seperately
    private void createListOfMedia() {
        Log.d(TAG, "createListOfMedia: ");
        try {
            mediaList = new ArrayList<>();
            pathList = new ArrayList<>();
            vMediaList = new ArrayList<>();
            videoPathList = new ArrayList<>();

            if ((courseCardClassList != null) && (courseCardClassList.size() > 0)) {
                for (int i = 0; i < courseCardClassList.size(); i++) {
                    DTOCourseCard courseCardClass = courseCardClassList.get(i);
                    if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                        for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                            if (courseCardClass.getCardMedia().get(k).getData() != null) {
                                String mediaType = courseCardClass.getCardMedia().get(k).getMediaType();
                                if (mediaType.equalsIgnoreCase("AUDIO")) {
                                    pathList.add("course/media/audio/" + courseCardClass.getCardMedia().get(k).getData());
                                    mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                    pathList.add("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                                    mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                } else if (mediaType.equalsIgnoreCase("GIF")) {
                                    pathList.add("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                                    mediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                    videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                    vMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                }
                            }
                        }
                    }
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getChildCard() != null)) {
                        if ((courseCardClass.getChildCard().getCardMedia() != null) && (courseCardClass.getChildCard().getCardMedia().size() > 0)) {
                            for (int k = 0; k < courseCardClass.getChildCard().getCardMedia().size(); k++) {
                                if (courseCardClass.getChildCard().getCardMedia().get(k).getData() != null) {
                                    String mediaType = courseCardClass.getChildCard().getCardMedia().get(k).getMediaType();
                                    if (mediaType.equalsIgnoreCase("AUDIO")) {
                                        pathList.add("course/media/audio/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                    } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                        pathList.add("course/media/image/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                    } else if (mediaType.equalsIgnoreCase("GIF")) {
                                        pathList.add("course/media/gif/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        mediaList.add(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                    } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                        videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                        vMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                    }
                                }
                            }
                        }
                    }
                    if (courseCardClass != null && courseCardClass.getChildCard() != null) {
                        if (courseCardClass.getChildCard().getSolutionType() != null) {
                            if (courseCardClass.getChildCard().getSolutionType().equalsIgnoreCase("IMAGE")) {
                                mediaList.add(OustMediaTools.getMediaFileName(courseCardClass.getChildCard().getContent()));
                                String path = courseCardClass.getChildCard().getContent();
                                if (path.startsWith("/"))
                                    path = path.replaceFirst("/", "");
                                pathList.add(path);
                            }
                        }
                    }

//                    download ReadMore from s3 if it is private
                    if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getRmId() > 0)) {
                        String rm_mediaType = courseCardClass.getReadMoreData().getType();
                        if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                            pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                            mediaList.add(courseCardClass.getReadMoreData().getData());
                        } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                            pathList.add("readmore/file/" + courseCardClass.getReadMoreData().getData());
                            mediaList.add(courseCardClass.getReadMoreData().getData());
                        }
                    }

                    //if question contain audio download it seperately
                    if ((courseCardClass.getAudio() != null) && (!courseCardClass.getAudio().isEmpty())) {
                        String audioPath = courseCardClass.getAudio();
                        String s3AudioFileName = audioPath;
                        if (audioPath.contains("/")) {
                            s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                        }
                        pathList.add("qaudio/" + s3AudioFileName);
                        mediaList.add(s3AudioFileName);
                    }
                    if (courseCardClass.getqId() != 0) {
                        DTOQuestions q = courseCardClass.getQuestionData();
                        OustMediaTools.prepareMediaList(mediaList, pathList, q);
                    }

                    if (courseCardClass.getQuestionData() != null && courseCardClass.getQuestionCategory() != null && courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY)) {
                        List<DTOVideoOverlay> videoOverlayList = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion();
                        if (videoOverlayList != null)
                            for (DTOVideoOverlay videoOverlay : videoOverlayList) {
                                try {
                                    DTOCourseCard videoOverlayCard = videoOverlay.getChildQuestionCourseCard();
                                    Log.d(TAG, "createListOfMedia: videoOverlayCard :" + videoOverlayCard.getCardId());
                                    if ((videoOverlayCard != null) && (videoOverlayCard.getCardMedia() != null) && (videoOverlayCard.getCardMedia().size() > 0)) {
                                        for (int k = 0; k < videoOverlayCard.getCardMedia().size(); k++) {
                                            if (videoOverlayCard.getCardMedia().get(k).getData() != null) {
                                                String mediaType = videoOverlayCard.getCardMedia().get(k).getMediaType();
                                                if (mediaType.equalsIgnoreCase("AUDIO")) {
                                                    pathList.add("course/media/audio/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                    mediaList.add(videoOverlayCard.getCardMedia().get(k).getData());
                                                } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                                    pathList.add("course/media/image/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                    mediaList.add(videoOverlayCard.getCardMedia().get(k).getData());
                                                } else if (mediaType.equalsIgnoreCase("GIF")) {
                                                    pathList.add("course/media/gif/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                    mediaList.add(videoOverlayCard.getCardMedia().get(k).getData());
                                                } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                                    videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                                    vMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                                }
                                            }
                                        }
                                    }
                                    if ((videoOverlayCard != null) && (videoOverlayCard.getQuestionData() != null) && (videoOverlayCard.getChildCard() != null)) {
                                        if ((videoOverlayCard.getChildCard().getCardMedia() != null) && (videoOverlayCard.getChildCard().getCardMedia().size() > 0)) {
                                            for (int k = 0; k < videoOverlayCard.getChildCard().getCardMedia().size(); k++) {
                                                if (videoOverlayCard.getChildCard().getCardMedia().get(k).getData() != null) {
                                                    String mediaType = videoOverlayCard.getChildCard().getCardMedia().get(k).getMediaType();
                                                    if (mediaType.equalsIgnoreCase("AUDIO")) {
                                                        pathList.add("course/media/audio/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                        mediaList.add(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                                        pathList.add("course/media/image/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                        mediaList.add(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    } else if (mediaType.equalsIgnoreCase("GIF")) {
                                                        pathList.add("course/media/gif/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                        mediaList.add(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                                        videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                                        vMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                                    }
                                                }
                                            }
                                        }
                                    }

//                          download ReadMore from s3 if it is private
                                    if ((videoOverlayCard != null) && (videoOverlayCard.getReadMoreData() != null) && (videoOverlayCard.getReadMoreData().getRmId() > 0)) {
                                        String rm_mediaType = videoOverlayCard.getReadMoreData().getType();
                                        if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                                            pathList.add("readmore/file/" + videoOverlayCard.getReadMoreData().getData());
                                            mediaList.add(videoOverlayCard.getReadMoreData().getData());
                                        } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                                            pathList.add("readmore/file/" + videoOverlayCard.getReadMoreData().getData());
                                            mediaList.add(videoOverlayCard.getReadMoreData().getData());
                                        }
                                    }

                                    //if question contain audio download it seperately
                                    if (videoOverlayCard != null && videoOverlayCard.getQuestionData() != null && videoOverlayCard.getQuestionData().getAudio() != null && !videoOverlayCard.getQuestionData().getAudio().isEmpty()) {
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
                                    }

                                    if (videoOverlayCard != null && videoOverlayCard.getqId() != 0) {
                                        DTOQuestions q1 = videoOverlayCard.getQuestionData();
                                        OustMediaTools.prepareMediaList(mediaList, pathList, q1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }
                    }
                }
            }
            checkMediaExist();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void addGameResoursesToList() {
        String[] resList = getActivity().getResources().getStringArray(R.array.wordjumble_icons);
        for (int i = 0; i < resList.length; i++) {
            pathList.add("AppResources/Android/All/Images/" + resList[i]);
            mediaList.add(resList[i]);
        }
    }


    private void checkMediaExist() {
        Log.d(TAG, "checkMediaExist: ");
        mediaSize = 0;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                Log.d(TAG, "onDownLoadStateChanged: " + message);
                if (code == _COMPLETED) {
                    removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });

        downloadFiles2 = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                //showDownloadProgress();
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: Message:" + message + " errorcode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile();
                }

            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        downLoadCount++;
        Log.d(TAG, "checkMediaExist: medSize:" + mediaList.size() + " path size:" + pathList.size());
        HashSet<String> medalistHash = new HashSet<>(mediaList);
        HashSet<String> pathHash = new HashSet<>(pathList);
        //mediaList = new ArrayList<>(medalistHash);
        //pathList = new ArrayList<>(pathHash);
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).contains(".zip")) {
                String newFileName = mediaList.get(i).replace(".zip", "");
                String unzipLocation = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + newFileName;
                final File file = new File(unzipLocation);
                if (!file.exists()) {
                    mediaSize++;
                    downLoad(mediaList.get(i), pathList.get(i), false);
                }
            } else {
                /*if(OustSdkTools.checkInternetStatus()) {
                    String mediaPath = mediaList.get(i);
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        mediaSize++;
                        downLoad(fileName, mediaPath);
                    } else {
                        mediaSize++;
                        downLoad(mediaList.get(i), pathList.get(i));
                    }
                }else {*/

                EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String mediaPath = mediaList.get(i);
                String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String fileName = OustMediaTools.getMediaFileName(mediaPath);

                    if (!file.exists()) {
                        mediaSize++;
                        downLoad(fileName, mediaPath, false);
                    }
                } else {
                    if (!file.exists()) {
                        mediaSize++;
                        downLoad(mediaList.get(i), pathList.get(i), false);
                    }
                }
                //}
            }
        }

        for (int i = 0; i < vMediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String mediaPath = vMediaList.get(i);
            String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                String fileName = OustMediaTools.getMediaFileName(mediaPath);

                if (!file.exists()) {
                    mediaSize++;
                    downLoad(fileName, mediaPath, true);
                }
            } else {
                if (!file.exists()) {
                    mediaSize++;
                    downLoad(vMediaList.get(i), videoPathList.get(i), true);
                }
            }
        }

        if (mediaSize == 0) {
            removeFile();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setReceiver();
    }

    public void downLoad(final String fileName1, final String pathName, boolean isVideo) {
        Log.d(TAG, "downLoad: path:" + pathName + " fileName:" + fileName1);
        Log.d(TAG, "downLoad: Media:" + CLOUD_FRONT_BASE_PATH);
        Log.d(TAG, "downLoad: Media:" + AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH);
        try {
            downLoadCount++;
            Log.d(TAG, "downLoad: downLoadCount:" + downLoadCount + " mediaSize:" + mediaSize);
            if ((!OustSdkTools.checkInternetStatus())) {
                showNoInternetLayout();
                return;
            }
            //String key = pathName;
            String path;

            if (fileName1.contains(".zip")) {
                path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/";
                //downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, path, fileName1, false);
                if (downLoadCount > 10) {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false);
                    downloadFiles2.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false, false);
                    //downloadFiles2.startDownLoad(path + "/" + fileName1, S3_BUCKET_NAME, pathName, false, true);
                } else {
                    // sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false);
                    downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false, false);
                    //downloadFiles.startDownLoad(path + "/" + fileName1, S3_BUCKET_NAME, pathName, false, true);
                }
            } else {
                boolean isOustLearn = true;
                if (isVideo) {
                    isOustLearn = false;

                }
                path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/";
                if (downLoadCount > 10) {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true);
                    //downloadFiles2.startDownLoad(path+"oustlearn_"+fileName1, S3_BUCKET_NAME, pathName, false, true);
                    downloadFiles2.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, isOustLearn, false);

                } else {
                    //sendToDownloadService(OustSdkApplication.getContext(), CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, true);
                    //downloadFiles.startDownLoad(path+"oustlearn_"+fileName1, S3_BUCKET_NAME, pathName, false, true);
                    downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, isOustLearn, false);
                }

/*
            String path = getActivity().getFilesDir() + "/oustlearn_" + fileName1;
            if (fileName1.contains(".zip")) {
                path = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + fileName1;
*/
            }
            Log.d(TAG, "downLoad: path:" + path + " pathName:" + pathName);

            /*if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1);
                        } else if (state == TransferState.FAILED || state == TransferState.CANCELED) {
                            noofTries++;
                            if (noofTries > 4) {
                                Log.d(TAG, "onStateChanged: more tha 4 times:");
                                showNetworkErrorMessage();
                            } else {
                                Log.d(TAG, "onStateChanged: again start download:");
                                downLoad(fileName1, pathName);
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if ((!OustSdkTools.checkInternetStatus())) {
                            showNoInternetLayout();
                            return;
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d(TAG, "onError: error id:"+id);
                        noofTries++;
                        if (noofTries > 4) {
                            showNetworkErrorMessage();
                        } else {
                            downLoad(fileName1, pathName);
                        }
                    }
                });
            } else {
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;
    private DownloadUpdateReceiver downloadUpdateReceiver;

    private void setReceiver() {
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myFileDownLoadReceiver, intentFilter);
        }

        downloadUpdateReceiver = new DownloadUpdateReceiver();
        IntentFilter receiverIntent = new IntentFilter();
        receiverIntent.addAction(ACTION_COMPLETE);
        receiverIntent.addAction(ACTION_ERROR);
        receiverIntent.addAction(ACTION_PROGRESS);
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(downloadUpdateReceiver, intentFilter);
        }

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.e(TAG,"MyFileDownLoadReceiver");
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                            downloaddata_text.setText(""+Integer.valueOf(intent.getStringExtra("PROGRESS"))+" %");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile();
                            if (intent.getStringExtra("MSG").equalsIgnoreCase("completedDestroy")) {
                                Log.d(TAG, "onReceive: completedDestroy:");
                                downloadlabel_content.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (isFragmentLive) {
                                                learningModuleInterface.downloadComplete(courseCardClassList, false);
//                                if(!OustPreferences.getAppInstallVariable("isWordJumbleResourcesDownloaded")) {
//                                    OustPreferences.saveAppInstallVariable("isWordJumbleResourcesDownloaded",true);
//                                }
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }, 700);
                            }
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            try {
                                Toast.makeText(getActivity(), "" + intent.getStringExtra("MSG"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                            // mDownLoadUpdateInterface.onDownLoadError(intent.getStringExtra("MSG"), _FAILED);

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

    private class DownloadUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    Log.e(TAG,"DownloadUpdate");
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            downloaddata_text.setText(""+Integer.valueOf(intent.getStringExtra("PROGRESS"))+" %");
                            //updatePercentage();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            if (intent.getStringExtra("MSG").equalsIgnoreCase("completedDestroy")) {
                                downloadlabel_content.setVisibility(View.GONE);
                                new Handler().postDelayed(() -> {
                                    try {
                                        if (isFragmentLive) {
                                            courseCardClassList = new ArrayList<>();
                                            for (DTOCourseCard dtoCourseCard : courseLevelClass.getCourseCardClassList()) {
                                                int downloadingCardId = (int) dtoCourseCard.getCardId();
                                                DTOCourseCard cardFromDb = OustSdkTools.databaseHandler.getCardClass(downloadingCardId);
                                                courseCardClassList.add(cardFromDb);

                                            }
                                            learningModuleInterface.downloadComplete(courseCardClassList, false);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }
                                }, 700);
                            }
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

    private void showMediaDownloadProgress() {
        Log.d(TAG, "showMediaDownloadProgress: ");
        try {
            DTOUserCourseData dtoUserCourseData = RoomHelper.getScoreById(OustStaticVariableHandling.getInstance().getCourseUniqNo());
            float f2 = (float) mediaSize;
            float f1 = (float) (mediaList.size());
            if ((mediaList.size() > 0) && (mediaSize > 0)) {
                float n1 = (f1 - f2) / f1;
                float currentMedia = (n1) * 50;
                int progressa = (int) (currentMedia + 50);
                downloaddata_text.setText(progressa + "%");
                if (!isDownloadingstrategyCardByCard) {
                    for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                        if (courseLevelClass != null) {
                            if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(progressa, dtoUserCourseData, i);
                            }
                        }
                    }
                }
            } else {
                downloaddata_text.setText("100%");
                if (!isDownloadingstrategyCardByCard) {
                    for (int i = 0; i < dtoUserCourseData.getUserLevelDataList().size(); i++) {
                        if (courseLevelClass != null) {
                            if (dtoUserCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                                userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(100, dtoUserCourseData, i);
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

    public void saveData(File file, String fileName1) {
        try {
//            if(fileName1.contains(".zip")){
//                String zipFilename =Environment.getExternalStorageDirectory()+"/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + fileName1;
//                String newFileName=fileName1.replace(".zip","");
//                String unzipLocation =Environment.getExternalStorageDirectory()+ "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/"+newFileName+"/";
//                DecompressFast d = new DecompressFast(zipFilename, unzipLocation);
//                d.unzip();
//                file.delete();
//            }
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
//            if (fileName1.contains("pdf")) {
//                byte[] b = FileUtils.readFileToByteArray(file);
//                encoded = Base64.encodeToString(b, Base64.DEFAULT);
//                Log.e("ReadMore", encoded);
//            }
//            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
//            enternalPrivateStorage.saveFile("oustlearn_" + fileName1, encoded);
//            file.delete();
            removeFile();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile() {
        try {
            Log.d(TAG, "removeFile: " + mediaSize);
            if (mediaSize > 0) {
                mediaSize--;
            }
            showMediaDownloadProgress();
            if (downloaddata_text.getText().toString().equalsIgnoreCase("100%")) {
                downloadlabel_content.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isFragmentLive) {
                                Log.d(TAG, "run: downloadcomplete");
                                learningModuleInterface.downloadComplete(courseCardClassList, false);
//                                if(!OustPreferences.getAppInstallVariable("isWordJumbleResourcesDownloaded")) {
//                                    OustPreferences.saveAppInstallVariable("isWordJumbleResourcesDownloaded",true);
//                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 700);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    @Override
    public void onDestroy() {
        try {
            isFragmentLive = false;
            learningcard_downloadprogresslayout.setAnimation(null);
            if (myHandler != null) {
                myHandler.removeCallbacksAndMessages(null);
            }
            resetAllData();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    private void resetAllData() {
        myHandler = null;
        mediaList = null;
        courseCardClassList = null;
        learningModuleInterface = null;
        courseLevelClass = null;
    }

    private void showNoInternetLayout() {
        try {
            maindownload_layout.setVisibility(View.GONE);
            nointernetconnection_layout.setVisibility(View.VISIBLE);
            nointernetconnection_text.setText(getActivity().getResources().getString(R.string.nointernet_message));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        learningModuleInterface.endActivity();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        } catch (Exception e) {
        }
    }

    //---------------
    //save latest time stamp used to decide level update
    private void saveRefreshData() {
        Log.d(TAG, "saveRefreshData: ");
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
        Log.d(TAG, "addData: ");
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

    //-----------------------
    public class DecompressFast {
        private String _zipFile;
        private String _location;

        public DecompressFast(String zipFile, String location) {
            _zipFile = zipFile;
            _location = location;
            _dirChecker("");
        }

        public void unzip() {
            try {
                FileInputStream fin = new FileInputStream(_zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.v("Decompress", "Unzipping " + ze.getName());
                    if (ze.isDirectory()) {
                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        BufferedOutputStream bufout = new BufferedOutputStream(fout);
                        byte[] buffer = new byte[1024];
                        int read = 0;
                        while ((read = zin.read(buffer)) != -1) {
                            bufout.write(buffer, 0, read);
                        }
                        bufout.close();
                        zin.closeEntry();
                        fout.close();
                    }
                }
                zin.close();
                Log.d("Unzip", "Unzipping complete. path :  " + _location);
            } catch (Exception e) {
                Log.e("Decompress", "unzip", e);
                Log.d("Unzip", "Unzipping failed");
            }
        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }

    private void updatePercentage() {
        try {
            DTOUserCourseData userCourseData = RoomHelper.getScoreById((OustStaticVariableHandling.getInstance().getCourseUniqNo()));
            for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    int percentage = userCourseData.getUserLevelDataList().get(i).getCompletePercentage();
                    downloaddata_text.setText(percentage + "%");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
