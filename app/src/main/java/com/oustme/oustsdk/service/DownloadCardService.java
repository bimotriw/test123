package com.oustme.oustsdk.service;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.request.LearningPathData;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.room.EntityLevelCardCourseID;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class DownloadCardService extends IntentService {
    private static final String TAG = "DownloadCardService";
    private CourseLevelClass courseLevelClass;
    private long courseUniqNo;
    private List<LearningPathData> learningPathDataList;
    private List<LearningPathData> videoLearningPathDataList;
    private DownloadFiles downloadFiles, downloadFilesVideo;
    private List<DTOCourseCard> courseCardClassList = new ArrayList<>();
    private int filesTodownload;
    private DownloadResultReceiver DownloadResultReceiver;
    private String downloadlink, fileDestination;
    private String downloadedPath = "";
    private long downloaded = 0;
    private File file;
    private String returnData = null;
    private File cacheDownloadFile;
    private int mNoTries;

    private boolean isDownloadVideo;

    public DownloadCardService() {
        super(DownloadCardService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            DownloadResultReceiver = new DownloadResultReceiver(new Handler(getMainLooper()));
            String courseLevelClassStr = intent.getStringExtra("courselevelclassstr");
            String s1 = intent.getStringExtra("courseUniqNo");
            isDownloadVideo = false;
            if (intent.hasExtra("downloadVideo")) {
                isDownloadVideo = intent.getBooleanExtra("downloadVideo", false);
            }

            courseUniqNo = Long.parseLong(s1);
            Gson gson = new Gson();
            if ((courseLevelClassStr != null) && (!courseLevelClassStr.isEmpty())) {
                courseLevelClass = gson.fromJson(courseLevelClassStr, CourseLevelClass.class);
                totalCards = courseLevelClass.getCourseCardClassList().size();
                checkForDownloadedData();
            } else {
                this.stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int currentCardNo = 0;
    private int savedCardID = 0;
    private int totalCards;

    public void checkForDownloadedData() {
        if (currentCardNo < courseLevelClass.getCourseCardClassList().size()) {
            savedCardID = getId(courseLevelClass.getCourseCardClassList().get(currentCardNo).getCardId());
            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            if ((courseCardClass == null)) {
                if (!OustSdkTools.checkInternetStatus()) {
                    this.stopSelf();
                    return;
                }
                downloadCard(courseLevelClass.getCourseCardClassList().get(currentCardNo).getCardId());
            } else {
                incrementandRestart();
            }
        } else {
            createListOfMedia();
        }
    }

    private void incrementandRestart() {
        currentCardNo++;
        checkForDownloadedData();
    }

    private int getId(long cardId) {
        int newCardId = (int) cardId;
//        try {
//            String s2 = startId + "" + cardId;
//            newCardId=Integer.parseInt(s2);
//        }catch (Exception e){}
        return newCardId;
    }

    private void downloadCard(final long cardId) {
        getlearningCardResponces(cardId, courseLevelClass.getLevelId(), courseLevelClass.getLpId());
    }

    int noofAttempt = 0;

    public void cardDownloadOver(LearningCardResponce learningCardResponce) {
        try {
            if ((learningCardResponce != null) && (learningCardResponce.isSuccess())) {
                DTOCourseCard courseCardClass = learningCardResponce.getCard();
//                if ((courseCardClass.getXp() == 0)) {
//                    courseCardClass.setXp(100);
//                }
                courseCardClassList.add(courseCardClass);
                OustSdkTools.databaseHandler.addCardDataClass(courseCardClass, savedCardID);
                currentCardNo++;
                updatePercentage();
                checkForDownloadedData();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            noofAttempt++;
                            if (noofAttempt < 5) {
                                checkForDownloadedData();
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 50000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private int mediaSize = 0;
    private List<String> mediaList, pathList;
    //private List<String> videoMediaList, videoPathList;

    private void createListOfMedia() {
        try {
            downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    checkForDownloadAvailability();
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

            downloadFilesVideo = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
                @Override
                public void onDownLoadProgressChanged(String message, String progress) {

                }

                @Override
                public void onDownLoadError(String message, int errorCode) {
                    checkForVideoDownloadAvailability();
                }

                @Override
                public void onDownLoadStateChanged(String message, int code) {
                    if (code == _COMPLETED) {
                        removeVideoFile();
                    }
                }

                @Override
                public void onAddedToQueue(String id) {

                }

                @Override
                public void onDownLoadStateChangedWithId(String message, int code, String id) {

                }
            });

            learningPathDataList = new ArrayList<>();
            videoLearningPathDataList = new ArrayList<>();
            mediaList = new ArrayList<>();
            pathList = new ArrayList<>();
            //videoMediaList = new ArrayList<>();
            //videoPathList = new ArrayList<>();

            if ((courseCardClassList != null) && (courseCardClassList.size() > 0)) {
                for (int i = 0; i < courseCardClassList.size(); i++) {
                    DTOCourseCard courseCardClass = courseCardClassList.get(i);
                    if ((courseCardClass != null) && (courseCardClass.getCardMedia() != null) && (courseCardClass.getCardMedia().size() > 0)) {
                        for (int k = 0; k < courseCardClass.getCardMedia().size(); k++) {
                            LearningPathData learningPathData = new LearningPathData();
                            switch (courseCardClass.getCardMedia().get(k).getMediaType()) {
                                case "AUDIO":
                                    learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                    learningPathData.setPathData("course/media/audio/" + courseCardClass.getCardMedia().get(k).getData());
                                    learningPathDataList.add(learningPathData);
                                    break;
                                case "IMAGE":
                                    learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                    learningPathData.setPathData("course/media/image/" + courseCardClass.getCardMedia().get(k).getData());
                                    learningPathDataList.add(learningPathData);
                                    break;
                                case "GIF":
                                    learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                    learningPathData.setPathData("course/media/gif/" + courseCardClass.getCardMedia().get(k).getData());
                                    learningPathDataList.add(learningPathData);
                                    break;
                                case "VIDEO": {
                                    if (isDownloadVideo) {
                                        learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                        learningPathData.setPathData("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                        videoLearningPathDataList.add(learningPathData);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (courseCardClass != null && courseCardClass.getChildCard() != null) {
                        if (courseCardClass.getChildCard().getSolutionType() != null) {
                            if (courseCardClass.getChildCard().getSolutionType().equalsIgnoreCase("IMAGE")) {
                                LearningPathData learningPathData = new LearningPathData();
                                learningPathData.setFileName(OustMediaTools.getMediaFileName(courseCardClass.getChildCard().getContent()));
                                String path = courseCardClass.getChildCard().getContent();
                                if (path.startsWith("/"))
                                    path = path.replaceFirst("/", "");
                                learningPathData.setPathData(path);
                                learningPathDataList.add(learningPathData);
                            }
                        }

                    }
                    if ((courseCardClass != null) && (courseCardClass.getQuestionData() != null) && (courseCardClass.getChildCard() != null)) {
                        if ((courseCardClass.getChildCard().getCardMedia() != null) && (courseCardClass.getChildCard().getCardMedia().size() > 0)) {
                            for (int k = 0; k < courseCardClass.getChildCard().getCardMedia().size(); k++) {
                                LearningPathData learningPathData = new LearningPathData();
                                switch (courseCardClass.getChildCard().getCardMedia().get(k).getMediaType()) {
                                    case "AUDIO":
                                        learningPathData.setFileName(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathData.setPathData("course/media/audio/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathDataList.add(learningPathData);
                                        break;
                                    case "IMAGE":
                                        learningPathData.setFileName(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathData.setPathData("course/media/image/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathDataList.add(learningPathData);
                                        break;
                                    case "GIF":
                                        learningPathData.setFileName(courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathData.setPathData("course/media/gif/" + courseCardClass.getChildCard().getCardMedia().get(k).getData());
                                        learningPathDataList.add(learningPathData);
                                        break;
                                    case "VIDEO": {
                                        if (isDownloadVideo) {
                                            learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                            learningPathData.setPathData("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                            videoLearningPathDataList.add(learningPathData);
                                        }
                                        break;
                                    }
                                }

                            }
                        }
                    }

                    if ((courseCardClass != null) && (courseCardClass.getReadMoreData() != null) && (courseCardClass.getReadMoreData().getRmId() > 0)) {
                        String rm_mediaType = courseCardClass.getReadMoreData().getType();
                        LearningPathData learningPathData = new LearningPathData();
                        if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                            learningPathData.setPathData("readmore/file/" + courseCardClass.getReadMoreData().getData());
                            learningPathData.setFileName(courseCardClass.getReadMoreData().getData());
                            learningPathDataList.add(learningPathData);
                        } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                            learningPathData.setPathData("readmore/file/" + courseCardClass.getReadMoreData().getData());
                            learningPathData.setFileName(courseCardClass.getReadMoreData().getData());
                            learningPathDataList.add(learningPathData);
                        }
                    }

                    if ((courseCardClass.getQuestionData() != null) && (courseCardClass.getQuestionData().getAudio() != null) && (!courseCardClass.getQuestionData().getAudio().isEmpty())) {
                        String audioPath = courseCardClass.getQuestionData().getAudio();
                        String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                        LearningPathData learningPathData = new LearningPathData();
                        learningPathData.setFileName(s3AudioFileName);
                        learningPathData.setPathData("qaudio/" + s3AudioFileName);
                        learningPathDataList.add(learningPathData);
                    }
                    if (courseCardClass.getqId() != 0) {
                        DTOQuestions q = RoomHelper.getQuestionById(courseCardClass.getqId());
                        OustMediaTools.prepareMediaList(mediaList, pathList, q);
                        if (mediaList != null && mediaList.size() > 0) {
                            for (int k = 0; k < mediaList.size(); k++) {
                                LearningPathData learningPathData = new LearningPathData();
                                learningPathData.setPathData(OustMediaTools.removeAwsOrCDnUrl(mediaList.get(k)));
                                learningPathData.setFileName(OustMediaTools.getMediaFileName(mediaList.get(k)));
                                learningPathDataList.add(learningPathData);
                            }
                        }
                    }


                    if (courseCardClass.getQuestionData() != null && courseCardClass.getQuestionCategory() != null && courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY)) {
                        List<DTOVideoOverlay> videoOverlayList = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion();
                        for (DTOVideoOverlay videoOverlay : videoOverlayList) {
                            try {
                                DTOCourseCard videoOverlayCard = videoOverlay.getChildQuestionCourseCard();
                                Log.d(TAG, "createListOfMedia: videoOverlayCard :" + videoOverlayCard.getCardId());

                                //CourseCardClass courseCardClass = courseCardClassList.get(i);
                                if ((videoOverlayCard != null) && (videoOverlayCard.getCardMedia() != null) && (videoOverlayCard.getCardMedia().size() > 0)) {
                                    for (int k = 0; k < videoOverlayCard.getCardMedia().size(); k++) {
                                        LearningPathData learningPathData = new LearningPathData();
                                        switch (videoOverlayCard.getCardMedia().get(k).getMediaType()) {
                                            case "AUDIO":
                                                learningPathData.setFileName(videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathData.setPathData("course/media/audio/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathDataList.add(learningPathData);
                                                break;
                                            case "IMAGE":
                                                learningPathData.setFileName(videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathData.setPathData("course/media/image/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathDataList.add(learningPathData);
                                                break;
                                            case "GIF":
                                                learningPathData.setFileName(videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathData.setPathData("course/media/gif/" + videoOverlayCard.getCardMedia().get(k).getData());
                                                learningPathDataList.add(learningPathData);
                                                break;
                                            case "VIDEO": {
                                                if (isDownloadVideo) {
                                                    learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                                    learningPathData.setPathData("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                                    videoLearningPathDataList.add(learningPathData);
                                                }
                                                break;
                                            }

                                        }
                                    }
                                }
                                if ((videoOverlayCard != null) && (videoOverlayCard.getQuestionData() != null) && (videoOverlayCard.getChildCard() != null)) {
                                    if ((videoOverlayCard.getChildCard().getCardMedia() != null) && (videoOverlayCard.getChildCard().getCardMedia().size() > 0)) {
                                        for (int k = 0; k < videoOverlayCard.getChildCard().getCardMedia().size(); k++) {
                                            LearningPathData learningPathData = new LearningPathData();
                                            switch (videoOverlayCard.getChildCard().getCardMedia().get(k).getMediaType()) {
                                                case "AUDIO":
                                                    learningPathData.setFileName(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathData.setPathData("course/media/audio/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathDataList.add(learningPathData);
                                                    break;
                                                case "IMAGE":
                                                    learningPathData.setFileName(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathData.setPathData("course/media/image/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathDataList.add(learningPathData);
                                                    break;
                                                case "GIF":
                                                    learningPathData.setFileName(videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathData.setPathData("course/media/gif/" + videoOverlayCard.getChildCard().getCardMedia().get(k).getData());
                                                    learningPathDataList.add(learningPathData);
                                                    break;
                                                case "VIDEO": {
                                                    if (isDownloadVideo) {
                                                        learningPathData.setFileName(courseCardClass.getCardMedia().get(k).getData());
                                                        learningPathData.setPathData("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                                        videoLearningPathDataList.add(learningPathData);
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                }

                                if ((videoOverlayCard != null) && (videoOverlayCard.getReadMoreData() != null) && (videoOverlayCard.getReadMoreData().getRmId() > 0)) {
                                    String rm_mediaType = videoOverlayCard.getReadMoreData().getType();
                                    LearningPathData learningPathData = new LearningPathData();
                                    if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("PDF"))) {
                                        learningPathData.setPathData("readmore/file/" + videoOverlayCard.getReadMoreData().getData());
                                        learningPathData.setFileName(videoOverlayCard.getReadMoreData().getData());
                                        learningPathDataList.add(learningPathData);
                                    } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                                        learningPathData.setPathData("readmore/file/" + videoOverlayCard.getReadMoreData().getData());
                                        learningPathData.setFileName(videoOverlayCard.getReadMoreData().getData());
                                        learningPathDataList.add(learningPathData);
                                    }
                                }

                                if ((videoOverlayCard.getQuestionData() != null) && (videoOverlayCard.getQuestionData().getAudio() != null) && (!videoOverlayCard.getQuestionData().getAudio().isEmpty())) {
                                    String audioPath = videoOverlayCard.getQuestionData().getAudio();
                                    String s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                                    LearningPathData learningPathData = new LearningPathData();
                                    learningPathData.setFileName(s3AudioFileName);
                                    learningPathData.setPathData("qaudio/" + s3AudioFileName);
                                    learningPathDataList.add(learningPathData);
                                }
                                if (videoOverlayCard.getqId() != 0) {
                                    DTOQuestions q = RoomHelper.getQuestionById(videoOverlayCard.getqId());
                                    OustMediaTools.prepareMediaList(mediaList, pathList, q);
                                    if (mediaList != null && mediaList.size() > 0) {
                                        for (int k = 0; k < mediaList.size(); k++) {
                                            LearningPathData learningPathData = new LearningPathData();
                                            learningPathData.setPathData(OustMediaTools.removeAwsOrCDnUrl(mediaList.get(k)));
                                            learningPathData.setFileName(OustMediaTools.getMediaFileName(mediaList.get(k)));
                                            learningPathDataList.add(learningPathData);
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
            HashSet<LearningPathData> learningPathDataHashSet = new HashSet<>(learningPathDataList);
            learningPathDataList = new ArrayList<>(learningPathDataHashSet);
            mediaSize = (learningPathDataList.size()) + (videoLearningPathDataList.size());
            checkForDownloadAvailability();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    private void setReceiver() {
        Log.d(TAG, "setReceiver: ");
        myFileDownLoadReceiver = new MyFileDownLoadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_COMPLETE);
        intentFilter.addAction(ACTION_ERROR);
        intentFilter.addAction(ACTION_PROGRESS);
        registerReceiver(myFileDownLoadReceiver, intentFilter);

        //LocalBroadcastManager.getInstance(OustSdkApplication.getContext()).registerReceiver(myFileDownLoadReceiver, intentFilter);
    }

    private class MyFileDownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    try {
                        if (intent.getAction().equalsIgnoreCase(ACTION_PROGRESS)) {
                            // mDownLoadUpdateInterface.onDownLoadProgressChanged("Progress", intent.getStringExtra("MSG"));
                            //setDownloadingPercentage(Integer.valueOf(intent.getStringExtra("MSG")), "");
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_COMPLETE)) {
                            removeFile();
                        } else if (intent.getAction().equalsIgnoreCase(ACTION_ERROR)) {
                            checkForDownloadAvailability();
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

    public void downLoad(final String fileName1, String pathName) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                this.stopSelf();
                return;
            }
            String key = pathName;
            file = new File(this.getFilesDir(), "oustlearn_");
            String destn = this.getFilesDir() + "/";
            filesTodownload++;
            Log.d(TAG, "downLoad: filename1:" + fileName1 + " key:" + key + " file:" + file.toString());
            //downloadFiles.startDownLoad(file.toString(), S3_BUCKET_NAME, pathName,false );
            //downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH+pathName, destn, fileName1, true);
            //sendToDownloadService(CLOUD_FRONT_BASE_PATH+pathName, destn, fileName1, true);
            mNoTries = 0;
            handleDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destn, fileName1, true, false);

            /*
             AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());

            if (file != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            saveData(file, fileName1);
                        } else if ((state == TransferState.FAILED) || (state == TransferState.CANCELED)) {
                            checkForDownloadAvailability();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal != 0) {
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.i("Amazon error ", ex.toString());
                    }
                });
            } else {
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void sendToDownloadService(String downloadPath, String destn, String fileName, boolean isOustLearn)
    {
        Intent intent = new Intent(OustSdkApplication.getContext(), DownLoadFilesIntentService.class);
        intent.putExtra(AppConstants.StringConstants.IS_OUST_LEARN, isOustLearn);
        intent.putExtra(AppConstants.StringConstants.FILE_NAME, fileName);
        intent.putExtra(AppConstants.StringConstants.FILE_URL, downloadPath);
        intent.putExtra(AppConstants.StringConstants.FILE_DESTN, destn);
        intent.putExtra(AppConstants.StringConstants.IS_VIDEO, false);

        intent.putExtra("receiver", DownloadResultReceiver);
        //DownLoadIntentService.setContext(this);
        startService(intent);
    }*/

    public void downLoadVideo(final String fileName1, String pathName) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                this.stopSelf();
                return;
            }
            //String path = Environment.getExternalStorageDirectory() + "/oustme/videos/";
            String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/";

            //File file1 = new File(path);
            downloadFilesVideo.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, path, fileName1, false, true);
            /*
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_S3_KEY_ID, BuildConfig.AWS_S3_KEY_SECRET));
            s3.setRegion(com.amazonaws.regions.Region.getRegion(AppConstants.MediaURLConstants.BUCKET_REGION));
            TransferUtility transferUtility = new TransferUtility(s3, OustSdkApplication.getContext());
            String key = pathName;
            if (file1 != null) {
                TransferObserver transferObserver = transferUtility.download("img.oustme.com", key, file1);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            removeVideoFile();
                        } else if ((state == TransferState.FAILED) || (state == TransferState.CANCELED)) {
                            checkForVideoDownloadAvailability();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                    }
                });
            } else {
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForDownloadAvailability() {
        Log.d(TAG, "learningPathDataList: " + learningPathDataList.size());
        if ((learningPathDataList != null) && (learningPathDataList.size() > 0)) {
            filesTodownload = 0;
            //EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String url = "oustlearn_" + learningPathDataList.get(0).getFileName();
            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
            if (!file.exists()) {
                downLoad(learningPathDataList.get(0).getFileName(), learningPathDataList.get(0).getPathData());
            } else {
                removeFile();
            }
        } else {
            checkForVideoDownloadAvailability();
        }
    }

    private void checkForVideoDownloadAvailability() {
        if ((videoLearningPathDataList != null) && (videoLearningPathDataList.size() > 0)) {
            if (ContextCompat.checkSelfPermission(OustSdkApplication.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //String path = Environment.getExternalStorageDirectory() + "/oustme/videos/" + videoLearningPathDataList.get(0).getFileName();
                String path = Environment.getExternalStorageDirectory() + "/Android/data/" + OustSdkApplication.getContext().getPackageName() + "/files/" + videoLearningPathDataList.get(0).getFileName();

                File file = new File(path);
                if (!file.exists()) {
                    downLoadVideo(videoLearningPathDataList.get(0).getFileName(), videoLearningPathDataList.get(0).getPathData());
                } else {
                    removeVideoFile();
                }
            } else {
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("levelNo", "" + courseLevelClass.getLevelId());
                broadcastIntent.setAction("android.intent.action.ATTACH_DATA");
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadcastIntent);
                this.stopSelf();
            }
        } else {
            showMediaDownloadProgress();
        }
    }


    public void saveData(File file, String fileName1) {
        try {
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            String encoded = Base64.encodeToString(bytes, 0);
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
            if (learningPathDataList != null && learningPathDataList.size() > 0) {
                learningPathDataList.remove(0);
                showMediaDownloadProgress();
                checkForDownloadAvailability();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeVideoFile() {
        try {
            videoLearningPathDataList.remove(0);
            showMediaDownloadProgress();
            checkForVideoDownloadAvailability();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void updatePercentage() {
        try {
            UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
            DTOUserCourseData userCourseData = RoomHelper.getScoreById(courseUniqNo);
            float f1 = (float) totalCards;
            float f2 = (float) currentCardNo;
            float n2 = f2 / f1;
            float n1 = (n2) * 50;
            int progressa = (int) n1;
            for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(progressa, userCourseData, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("android.intent.action.ATTACH_DATA");
//            broadcastIntent.setAction(NewLearningMapActivity.MyDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showMediaDownloadProgress() {
        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(courseUniqNo);
        int levelNo = 0;
        try {
            float f2 = (float) mediaSize;
            float f1 = (float) (learningPathDataList.size() + videoLearningPathDataList.size());
            if (((learningPathDataList.size() > 0) || (videoLearningPathDataList.size() > 0)) && (mediaSize > 0)) {
                float n1 = (f2 - f1) / f2;
                float currentMedia = (n1) * 50;
                int progressa = (int) (currentMedia + 50);
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(progressa, userCourseData, i);
                        levelNo = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        userCourseScoreDatabaseHandler.setUserLevelDataCompletePercentage(100, userCourseData, i);
                        levelNo = i;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        try {
            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction(NewLearningMapActivity.MyDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent.setAction("android.intent.action.ATTACH_DATA");
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (userCourseData.getUserLevelDataList().get(levelNo).getCompletePercentage() >= 100) {
            this.stopSelf();
        }
    }

    private void handleDownLoad(String fileURL, String fileDestinationPath, String fileName, boolean isOustLearn, boolean isVideo) {
        if (isVideo) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    asyncDownload(fileURL, fileDestinationPath, fileName, isOustLearn);
                    return null;
                }
            }.execute();

        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    normalownload(fileURL, fileDestinationPath, fileName, isOustLearn);
                    return null;
                }
            }.execute();
        }
    }

    private void asyncDownload(String fileURL, String fileDestinationPath, String fileName, boolean isOustLearn) {
        this.downloadlink = fileURL;
        this.fileDestination = fileDestinationPath;
        if (isOustLearn) {
            file = new File(fileDestination, "oustlearn_" + fileName);
        } else {
            file = new File(fileDestination, fileName);
        }
        cacheDownloadFile = new File(OustSdkApplication.getContext().getCacheDir() + fileName);
        try {
            if (cacheDownloadFile.isFile())
                downloaded = OustSdkTools.getFileSize(cacheDownloadFile);
            else
                downloaded = 0;
            Log.d("FILE_DOWNLOAD_TAG_p", downloaded + " <- " + cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //fireOnUpdate(ON_INIT,"init ...");

        try {
            File dir = new File(fileDestination);
            File chacheDir = OustSdkApplication.getContext().getCacheDir();
            if (!chacheDir.isDirectory())
                chacheDir.mkdirs();
            /*if(!dir.isDirectory()){
                dir.mkdirs();
            }*/

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if (withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                //sendUpdate(1, "completed");
                returnData = "COMPLETED";
                //sendUpdate(1,"COMPLETED");
                return;
            }

            if (file.exists()) {
                Log.d("FILE_DOWNLOAD_TAG", "File exist return complete");
                sendUpdate(1, "completed");
                //this.stopSelf();
                returnData = "COMPLETED";
                return;//file exist
            }

            //fireOnUpdate(ON_INIT, "Started");
            if (!cacheDownloadFile.exists()) {
                cacheDownloadFile.createNewFile();
            } else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Already Downloading");
                // return "ALREADY DOWNLOADING";
            }

            Log.d("FILE_DOWNLOAD_TAG", "LINK " + downloadlink);
            URL url = new URL(downloadlink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (downloaded > 0)
                urlConnection.setRequestProperty("Range", "byte=" + downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            long totalSize = urlConnection.getContentLength();
            if (totalSize <= downloaded) {
                returnData = "COMPLETED";
                Log.d(TAG, "handleDownLoad: download completed:");
                sendUpdate(1, "completed");
                //publishProgress("File checked "+ URLUtil.guessFileName(file.getAbsolutePath(), null, null));
                //this.stopSelf();
                // return returnData;
                return;
            }
            this.downloadedPath = cacheDownloadFile.getAbsolutePath();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            FileOutputStream fileOutput = new FileOutputStream(cacheDownloadFile);
            long d = 0;
            long starttime = System.currentTimeMillis();
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloaded += bufferLength;
                d += bufferLength;
                //String l=" "+Tools.getFileName(file.getAbsolutePath())+" ( "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" )";
                //String l="  "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" ( "+getDownloadSpeed(starttime,d)+" )";
                float per = ((float) downloaded / (float) totalSize) * 100;
                String l = "" + (int) per;
                //Log.d(TAG, "handleDownLoad:Progress: "+l);
                sendUpdate(2, l);
                // publishProgress(l);
                if (downloaded >= totalSize) {
                    break;
                }
            }
            Log.d("FILE_DOWNLOAD_TAG", "DWONLOADED TO " + downloadedPath + " (" + cacheDownloadFile.length() + ")");
            fileOutput.close();
            try {
                if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                    Log.d("FILE_DOWNLOAD_TAG", "file Copied, delete cache");
                    cacheDownloadFile.delete();
                    //file.renameTo(new File("oustlearn_"+file));
                } else {
                    mNoTries++;
                    Log.d(TAG, "handleDownLoad: Else part");
                    urlConnection.disconnect();
                    if (cacheDownloadFile.exists()) {
                        cacheDownloadFile.delete();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn, true);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.d(TAG, "handleDownLoad: downlaoded copying data failed:");
                if (cacheDownloadFile.exists()) {
                    cacheDownloadFile.delete();
                }
                if (file.delete()) {
                    file.delete();
                }
                //handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            sendUpdate(1, returnData);
            returnData = "COMPLETED";
            return;
        } catch (MalformedURLException e) {
            Log.d(TAG, "handleDownLoad: MalformedURLException");
            returnData = null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            //this.stopSelf();
            // publishProgress(e.toString());
        } catch (IOException e) {
            Log.d(TAG, "handleDownLoad: IOException");
            returnData = null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            // publishProgress(e.toString());
        }
    }

    private void normalownload(String fileURL, String fileDestinationPath, String fileName, boolean isOustLearn) {
        this.downloadlink = fileURL;
        this.fileDestination = fileDestinationPath;
        if (isOustLearn) {
            file = new File(fileDestination, "oustlearn_" + fileName);
        } else {
            file = new File(fileDestination, fileName);
        }
        cacheDownloadFile = new File(OustSdkApplication.getContext().getCacheDir() + fileName);
        try {
            if (cacheDownloadFile.isFile())
                downloaded = OustSdkTools.getFileSize(cacheDownloadFile);
            else
                downloaded = 0;
            Log.d("FILE_DOWNLOAD_TAG_p", downloaded + " <- " + cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //fireOnUpdate(ON_INIT,"init ...");

        try {
            File dir = new File(fileDestination);
            File chacheDir = OustSdkApplication.getContext().getCacheDir();
            if (!chacheDir.isDirectory())
                chacheDir.mkdirs();
            /*if(!dir.isDirectory()){
                dir.mkdirs();
            }*/

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if (withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                //sendUpdate(1, "completed");
                returnData = "COMPLETED";
                //sendUpdate(1,"COMPLETED");
                return;
            }

            if (file.exists()) {
                Log.d("FILE_DOWNLOAD_TAG", "File exist return complete");
                sendUpdate(1, "completed");
                //this.stopSelf();
                returnData = "COMPLETED";
                return;//file exist
            }

            //fireOnUpdate(ON_INIT, "Started");
            if (!cacheDownloadFile.exists()) {
                cacheDownloadFile.createNewFile();
            } else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Already Downloading");
                // return "ALREADY DOWNLOADING";
            }

            Log.d("FILE_DOWNLOAD_TAG", "LINK " + downloadlink);
            URL url = new URL(downloadlink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (downloaded > 0)
                urlConnection.setRequestProperty("Range", "byte=" + downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            long totalSize = urlConnection.getContentLength();
            if (totalSize <= downloaded) {
                returnData = "COMPLETED";
                Log.d(TAG, "handleDownLoad: download completed:");
                sendUpdate(1, "completed");
                //publishProgress("File checked "+ URLUtil.guessFileName(file.getAbsolutePath(), null, null));
                //this.stopSelf();
                // return returnData;
                return;
            }
            this.downloadedPath = cacheDownloadFile.getAbsolutePath();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            FileOutputStream fileOutput = new FileOutputStream(cacheDownloadFile);
            long d = 0;
            long starttime = System.currentTimeMillis();
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloaded += bufferLength;
                d += bufferLength;
                //String l=" "+Tools.getFileName(file.getAbsolutePath())+" ( "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" )";
                //String l="  "+Tools.convertMemory(downloaded)+" / "+Tools.convertMemory(totalSize)+" ( "+getDownloadSpeed(starttime,d)+" )";
                float per = ((float) downloaded / (float) totalSize) * 100;
                String l = "" + (int) per;
                //Log.d(TAG, "handleDownLoad:Progress: "+l);
                sendUpdate(2, l);
                // publishProgress(l);
                if (downloaded >= totalSize) {
                    break;
                }
            }
            Log.d("FILE_DOWNLOAD_TAG", "DWONLOADED TO " + downloadedPath + " (" + cacheDownloadFile.length() + ")");
            fileOutput.close();
            try {
                if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                    Log.d("FILE_DOWNLOAD_TAG", "file Copied, delete cache");
                    cacheDownloadFile.delete();
                    //file.renameTo(new File("oustlearn_"+file));
                } else {
                    mNoTries++;
                    Log.d(TAG, "handleDownLoad: Else part");
                    urlConnection.disconnect();
                    if (cacheDownloadFile.exists()) {
                        cacheDownloadFile.delete();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn, false);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.d(TAG, "handleDownLoad: downlaoded copying data failed:");
                if (cacheDownloadFile.exists()) {
                    cacheDownloadFile.delete();
                }
                if (file.delete()) {
                    file.delete();
                }
                //handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            sendUpdate(1, returnData);
            returnData = "COMPLETED";
            return;
        } catch (MalformedURLException e) {
            Log.d(TAG, "handleDownLoad: MalformedURLException");
            returnData = null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            //this.stopSelf();
            // publishProgress(e.toString());
        } catch (IOException e) {
            Log.d(TAG, "handleDownLoad: IOException");
            returnData = null;
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            sendUpdate(3, e.getLocalizedMessage());
            // publishProgress(e.toString());
        }
    }

    private void sendUpdate(int status, String message) {
        switch (status) {
            case 1:
                removeFile();
                break;
            case 2:
                break;
            case 3:
                checkForDownloadAvailability();
                break;

        }
    }

    public void getlearningCardResponces(final long cardId, final long levelId, final int courseId) {
        Log.d(TAG, "getlearningCardResponces: ");
        final LearningCardResponce[] learningCardResponces = {null};
        try {
            //String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCard_url);
            String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
            getCardUrl = getCardUrl.replace("cardId", String.valueOf(cardId));
            getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseId));
            getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(levelId));
            Log.d(TAG, "getlearningCardResponces: " + getCardUrl);
            //getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

//            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        Gson gson = new Gson();
//                        learningCardResponces[0] = gson.fromJson(response.toString(), LearningCardResponce.class);
//                        cardDownloadOver(learningCardResponces[0]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
////                        Gson gson = new Gson();
////                        LearningCardResponce learningCardResponce = gson.fromJson(response.toString(),LearningCardResponce.class);
////                        cardDownloadOver(learningCardResponce,cardId,saveCardId);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    try {
//                        params.put("api-key", OustPreferences.get("api_key"));
//                        params.put("org-id", OustPreferences.get("tanentid"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return params;
//                }
//            };
//            jsonObjReq.setShouldCache(false);
//            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 100000f));
//            OustSdkApplication.getInstance().addToRequestQueue(jsonObjReq, "first");

            /*final Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(null);*/
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JasonData: " + response.toString());
                    learningCardResponces[0] = gson.fromJson(response.toString(), LearningCardResponce.class);

                    EntityLevelCardCourseID entityLevelCardCourseID = new EntityLevelCardCourseID();
                    entityLevelCardCourseID.setCardId(cardId);
                    entityLevelCardCourseID.setCourseId(courseId);
                    entityLevelCardCourseID.setLevelId(levelId);
                    RoomHelper.addLevelCourseCardMap(entityLevelCardCourseID);
                    cardDownloadOver(learningCardResponces[0]);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: " + error.getLocalizedMessage());
                    learningCardResponces[0] = null;
                    cardDownloadOver(learningCardResponces[0]);
                }
            });


            /*final String finalGetCardUrl = getCardUrl;
            new Thread() {
                @Override
                public void run() {
                    try {
                        OustRestClient oustRestClient=new OustRestClient();
                        LearningCardResponce learningCardResponce=oustRestClient.downloadCardData(finalGetCardUrl);
                        EntityLevelCardCourseID realmLevelCardCourseIDModel = new EntityLevelCardCourseID();
                        realmLevelCardCourseIDModel.setCardId((int)cardId);
                        realmLevelCardCourseIDModel.setCourseId((int)courseId);
                        realmLevelCardCourseIDModel.setLevelId((int)levelId);
                        RoomHelper.addLevelCourseCardMap(realmLevelCardCourseIDModel);
                        cardDownloadOver(learningCardResponce);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            }.start();*/


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    private class DownloadResultReceiver extends ResultReceiver {

        public DownloadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 1:
                    Log.d(TAG, "onReceiveResult: completed:" + resultData.getString("MSG"));
                    removeFile();
                    break;

                case 2:
                    //Log.d(TAG, "onReceiveResult: progress:"+resultData.getString("MSG"));
                    break;

                case 3:
                    Log.d(TAG, "onReceiveResult: Error:" + resultData.getString("MSG"));
                    checkForDownloadAvailability();
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }


}
