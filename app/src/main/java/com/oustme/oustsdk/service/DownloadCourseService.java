package com.oustme.oustsdk.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.courses.LessonsActivity;
import com.oustme.oustsdk.activity.courses.newlearnngmap.NewLearningMapActivity;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.request.LearningPathData;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.room.EntityLevelCardCourseID;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserCourseData;
import com.oustme.oustsdk.room.dto.DTOUserLevelData;
import com.oustme.oustsdk.room.dto.DTOVideoOverlay;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.MyLifeCycleHandler;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_COMPLETE;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_ERROR;
import static com.oustme.oustsdk.service.DownLoadIntentService.ACTION_PROGRESS;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

/**
 * Created by shilpysamaddar on 03/04/17.
 */

public class DownloadCourseService extends IntentService {
    private static final String TAG = "DownloadCourseService";
    private CourseDataClass courseDataClass;
    private CourseLevelClass courseLevelClass;
    private long courseUniqNo;
    private String startId;
    private int totalLevels = 0;
    private List<LearningPathData> learningPathDataList;
    private String courseCollectionId;
    private boolean isDownloadingOnlyCourse = false, isDownloadVideo = false;
    private boolean isNotificationShown=false;

    private String downloadlink, fileDestination;
    private String downloadedPath = "";
    private long downloaded = 0;
    private File file;
    private String returnData = null;
    private File cacheDownloadFile;

    private List<DTOCourseCard> courseCardClassList = new ArrayList<>();
    public float progressMain;
    private HashMap<String, String> fileDownloadsList;
    private int downloadCount;
    private DownloadFiles downloadFiles, downloadFiles2;
    private DownloadResultReceiver DownloadResultReceiver;
    int mNoTries;

    public DownloadCourseService() {
        super(DownloadCourseService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            DownloadResultReceiver = new DownloadResultReceiver(new Handler(getMainLooper()));
            Gson gson = new Gson();
            String courseDataStr = intent.getStringExtra("courseDataStr");
            courseCollectionId = intent.getStringExtra("courseCollectionId");
            isDownloadingOnlyCourse = intent.getBooleanExtra("isDownloadingOnlyCourse", false);

            isDownloadVideo = false;
            if(intent.hasExtra("downloadVideo")) {
                isDownloadVideo = intent.getBooleanExtra("downloadVideo", false);
            }

            courseDataClass = gson.fromJson(courseDataStr, CourseDataClass.class);
            fileDownloadsList = new HashMap<>();
            downloadCount = 0;
            downloadLevel();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.d(TAG, e.getLocalizedMessage());

        }
    }

    private int currentLevelNo = -1;

    private void downloadLevel() {
        try {
            currentLevelNo++;
            if (courseDataClass.getCourseLevelClassList().size() > currentLevelNo) {
                totalLevels = courseDataClass.getCourseLevelClassList().size();
                courseLevelClass = courseDataClass.getCourseLevelClassList().get(currentLevelNo);
                String activeUserGet = OustPreferences.get("userdata");
                ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
                String s1 = "" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                if ((courseCollectionId != null) && (!courseCollectionId.isEmpty())) {
                    s1 = "" + activeUser.getStudentKey() + courseCollectionId + courseDataClass.getCourseId();
                }
                courseUniqNo = Long.parseLong(s1);
                startId = "" + courseDataClass.getCourseId() + "" + courseLevelClass.getLevelId();
                totalCards = courseLevelClass.getCourseCardClassList().size();
                currentCardNo = 0;
                courseCardClassList = new ArrayList<>();
                checkForDownloadedData();
            } else {
                if (isDownloadingOnlyCourse) {
                    if (isAppIsInBackground() && !isNotificationShown) {
                        isNotificationShown = true;
                        Map<String, String> notification = new HashMap<>();
                        notification.put("id", "" + courseDataClass.getCourseId());
                        notification.put("title", OustSdkApplication.getContext().getResources().getString(R.string.course_complete_heading));
                        notification.put("type", String.valueOf(GCMType.COURSE_DISTRIBUTE));
                        notification.put("message", "\"" + courseDataClass.getCourseName() + "\", " +OustSdkApplication.getContext().getResources().getString(R.string.course_complete_msg));
                        //OustNotificationHandler oustNotificationHandler = new OustNotificationHandler(notification, OustSdkApplication.getContext());
                    } else {
                        //OustSdkTools.showToast("\"" + courseDataClass.getCourseName() + "\" Course " + OustStrings.getString("course_complete_msg"));
                    }
                }
                this.stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public boolean isAppIsInBackground() {
        if (MyLifeCycleHandler.stoppedActivities == MyLifeCycleHandler.startedActivities) {
            Log.e(TAG,"isapp true");
            return true;
        } else {
            return false;
        }
    }

    private int currentCardNo = 0;
    private int savedCardID = 0;
    private int totalCards;

    public void checkForDownloadedData() {
        if (currentCardNo < courseLevelClass.getCourseCardClassList().size()) {
            savedCardID = getId(courseLevelClass.getCourseCardClassList().get(currentCardNo).getCardId());
            DTOCourseCard courseCardClass = OustSdkTools.databaseHandler.getCardClass(savedCardID);
            if ((courseCardClass == null) || (courseCardClass != null && courseCardClass.getCardId() == 0)) {
                if (!OustSdkTools.checkInternetStatus()) {
                    this.stopSelf();
                    return;
                }
                downloadCard(courseLevelClass.getCourseCardClassList().get(currentCardNo).getCardId());
            } else {
                incrementandRestart(courseCardClass);
            }
        } else {
            createListOfMedia2();
        }
    }

    private void incrementandRestart(DTOCourseCard courseCardClass) {
        currentCardNo++;
        courseCardClassList.add(courseCardClass);
        updatePercentage();
        checkForDownloadedData();
    }

    private int getId(long cardId) {
        int newCardId = (int) cardId;
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
                courseCardClassList.add(courseCardClass);
                OustSdkTools.databaseHandler.addCardDataClass(courseCardClass, (int) learningCardResponce.getCard().getCardId());
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
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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
    private List<String> videoMediaList, videoPathList;


    private void createListOfMedia2() {
        Log.d(TAG, "createListOfMedia: ");
        try {
            mediaList = new ArrayList<>();
            pathList = new ArrayList<>();

            videoMediaList = new ArrayList<>();
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
                                    //String path= Environment.getExternalStorageDirectory()+"/Android/data/com.oustme.oustapp/files";
                                    //String downloadPath  = CLOUD_FRONT_VIDEO_BASE+videoFileName;
                                    //sendToDownloadService(OustSdkApplication.getContext(), downloadPath, path, videoFileName, false);
                                    videoPathList.add("course/media/video/" + courseCardClass.getCardMedia().get(k).getData());
                                    videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
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
                                        videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
                                    }
                                }
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
                    if (courseCardClass!=null && courseCardClass.getQuestionData() != null && courseCardClass.getQuestionData().getAudio() != null && !courseCardClass.getQuestionData().getAudio().isEmpty()) {
                        courseCardClass.setAudio(courseCardClass.getQuestionData().getAudio());
                    }
                    if (courseCardClass!=null && (courseCardClass.getAudio() != null) && (!courseCardClass.getAudio().isEmpty())) {
                        String audioPath = courseCardClass.getAudio();
                        String s3AudioFileName = audioPath;
                        if (audioPath.contains("/")) {
                            s3AudioFileName = audioPath.substring(audioPath.lastIndexOf("/") + 1);
                        }
                        pathList.add("qaudio/" + s3AudioFileName);
                        mediaList.add(s3AudioFileName);
                    }
                    if( courseCardClass != null && courseCardClass.getChildCard()!= null ){
                        if(courseCardClass.getChildCard().getSolutionType()!=null) {
                            if (courseCardClass.getChildCard().getSolutionType().equalsIgnoreCase("IMAGE")) {
                                mediaList.add(OustMediaTools.getMediaFileName(courseCardClass.getChildCard().getContent()));
                                String path = courseCardClass.getChildCard().getContent();
                                if (path.startsWith("/"))
                                    path = path.replaceFirst("/", "");
                                pathList.add(path);
                            }
                        }
                    }
                    if (courseCardClass!=null && courseCardClass.getqId() != 0) {
                        DTOQuestions q = RoomHelper.getQuestionById(courseCardClass.getqId());
                        OustMediaTools.prepareMediaList(mediaList, pathList, q);
                    }

                    if(courseCardClass.getQuestionData()!=null && courseCardClass.getQuestionCategory()!=null && courseCardClass.getQuestionCategory().equalsIgnoreCase(QuestionCategory.VIDEO_OVERLAY))
                    {
                        List<DTOVideoOverlay> videoOverlayList = courseCardClass.getQuestionData().getListOfVideoOverlayQuestion();
                        for(DTOVideoOverlay videoOverlay:videoOverlayList){
                            try {
                                DTOCourseCard videoOverlayCard = videoOverlay.getChildQuestionCourseCard();
                                //Log.d(TAG, "createListOfMedia2: videoOverlayCard :" + videoOverlayCard.getCardId());
                                //CourseCardClass courseCardClass = courseCardClassList.get(i);
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
                                                videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
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
                                                    videoMediaList.add(courseCardClass.getCardMedia().get(k).getData());
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
                                    DTOQuestions q1 = RoomHelper.getQuestionById(videoOverlayCard.getqId());
                                    OustMediaTools.prepareMediaList(mediaList, pathList, q1);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                }
            }
            removeDuplicates();
            mediaSize = (mediaList.size());

            if(videoMediaList!=null && videoMediaList.size()>0){
                mediaSize = mediaSize + (videoMediaList.size());
            }
            if(mediaSize == 0){
                showMediaDownloadProgress2();
            }
            checkFirstTimeMediaExist(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void downLoad(String fileName1, String pathName, boolean isVideo) {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                this.stopSelf();
                return;
            }
            //String key = pathName;
            String destination = OustSdkApplication.getContext().getFilesDir() + "/";
            boolean isOustLearn = true;

            if(isVideo){
                isOustLearn = false;
                destination = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/";
            }

            fileDownloadsList.put(fileName1, pathName);
            //downloadCount++;
            //downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destination, fileName1, true);
            mNoTries = 0;

            Log.d(TAG, "downLoad: dest:"+destination+" --- fileName:"+fileName1);
            handleDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destination, fileName1, isOustLearn);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private MyFileDownLoadReceiver myFileDownLoadReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        setReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (myFileDownLoadReceiver != null) {
            unregisterReceiver(myFileDownLoadReceiver);
        }
//        updatePercentage();
//        downloadLevel();
    }

    private void setReceiver() {
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
                            checkMediaExist(false);
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

    private void checkMediaExist(boolean shouldIncrement) {
        try {
            Log.d(TAG, "checkMediaExist: medialist sie:" + mediaList.size() + " \nPath List:" + pathList.size());
            HashSet<String> medalistHash = new HashSet<>(mediaList);
            HashSet<String> pathHash = new HashSet<>(pathList);
            for (int i = 0; i < mediaList.size(); i++) {
                Log.i("Media path size ", "media size " + mediaSize+" -- current:"+i);
                if (mediaList.get(i).contains(".zip")) {
                    String newFileName = mediaList.get(i).replace(".zip", "");
                    String unzipLocation = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + newFileName;
                    final File file = new File(unzipLocation);
                    if (!file.exists()) {
                        downLoad(mediaList.get(i), pathList.get(i), false);
                    }
                } else {
                    //EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    String mediaPath = mediaList.get(i);
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        if (!file.exists()) {
                            downLoad(fileName, mediaPath, false);
                        }
                    } else {
                        if (i < mediaList.size() && i < pathList.size()) {
                            String fileName = OustMediaTools.getMediaFileName(mediaPath);
                            String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                            if (!file.exists()) {
                                downLoad(fileName, pathList.get(i), false);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < videoMediaList.size(); i++) {
                Log.i("Media path size ", "media size " + videoMediaList);

                //EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String mediaPath = videoMediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String fileName = OustMediaTools.getMediaFileName(mediaPath);
                    String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                    if (!file.exists()) {
                        downLoad(fileName, mediaPath, true);
                    }else{
                        removeFileFromIndex(i);
                    }
                } else {
                    if (i < videoMediaList.size() && i < videoPathList.size()) {
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downLoad(fileName, videoPathList.get(i), true);
                        }else{
                            removeFileFromIndex(i);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            showMediaDownloadProgress2();
        }
//        if (mediaSize == 0) {
//            showMediaDownloadProgress2();
//            //removeFile();
//        }
    }


    private void checkFirstTimeMediaExist(boolean shouldIncrement) {
        try {
            Log.d(TAG, "checkMediaExist: medialist sie:" + mediaList.size() + " \nPath List:" + pathList.size());
            HashSet<String> medalistHash = new HashSet<>(mediaList);
            HashSet<String> pathHash = new HashSet<>(pathList);
            for (int i = 0; i < mediaList.size(); i++) {
                Log.i("Media path size ", "media size " + mediaSize);
                if (mediaList.get(i).contains(".zip")) {
                    String newFileName = mediaList.get(i).replace(".zip", "");
                    String unzipLocation = Environment.getExternalStorageDirectory() + "/Android/data/"+OustSdkApplication.getContext().getPackageName()+"/files/" + newFileName;
                    final File file = new File(unzipLocation);
                    if (!file.exists()) {
                        downLoad(mediaList.get(i), pathList.get(i), false);
                    }else{
                        removeFileFromIndex(i);
                    }
                }
                else {
                   // EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                    String mediaPath = mediaList.get(i);
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downLoad(fileName, mediaPath, false);
                        }else{
                            removeFileFromIndex(i);
                        }
                    } else {
                        if (i < mediaList.size() && i < pathList.size()) {
                            String fileName = OustMediaTools.getMediaFileName(mediaPath);
                            String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                            File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                            if (!file.exists()) {
                                downLoad(fileName, pathList.get(i), false);
                            }else{
                                removeFileFromIndex(i);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < videoMediaList.size(); i++) {
                Log.i("Media path size ", "media size " + videoMediaList);

                //EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
                String mediaPath = videoMediaList.get(i);
                if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                    mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                    String fileName = OustMediaTools.getMediaFileName(mediaPath);
                    String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                    File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                    if (!file.exists()) {
                        downLoad(fileName, mediaPath, true);
                    }else{
                        removeFileFromIndex(i);
                    }
                } else {
                    if (i < videoMediaList.size() && i < videoPathList.size()) {
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downLoad(fileName, videoPathList.get(i), true);
                        }else{
                            removeFileFromIndex(i);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            showMediaDownloadProgress2();
        }
//        if (mediaSize == 0) {
//            showMediaDownloadProgress2();
//            //removeFile();
//        }
    }





    private void removeDuplicates() {
        HashMap<String, String> map = new HashMap<>();
        if (mediaList != null) {
            for (int i = 0; i < mediaList.size(); i++) {
                if (map.containsKey(mediaList.get(i))) {
                    mediaList.remove(i);
                    pathList.remove(i);
                } else {
                    map.put(mediaList.get(i), pathList.get(i));
                }
            }
        }
    }

    public void saveData(File file, String fileName1) {
        try {
            removeFile();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile() {
        try {
            if (mediaSize>0) {

                mediaSize--;
                showMediaDownloadProgress2();
                checkMediaExist(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFileFromIndex(int pos) {
        try {
            if (mediaSize >0) {
                mediaSize--;
                showMediaDownloadProgress2();
                //checkMediaExist(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void updatePercentage() {
        UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(courseUniqNo);
        try {
            float f1 = (float) totalCards;
            float f2 = (float) currentCardNo;
            float n2 = f2 / f1;
            float n1 = (n2) * 50;
            int progressa = (int) n1;
            float progressCourse = ((float) progressa / totalLevels) + ((float) 100 * (currentLevelNo)) / (totalLevels);
            if (userCourseData.getUserLevelDataList() == null) {
                userCourseData.setUserLevelDataList(new ArrayList<DTOUserLevelData>());
            }
            DTOUserLevelData dtoUserLevelData = new DTOUserLevelData();
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
            for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                    userCourseData.getUserLevelDataList().get(i).setCompletePercentage(progressa);
                    userCourseData.getUserLevelDataList().get(i).setDownloading(true);
                }
            }
            if (progressCourse > 100) {
                progressCourse = 100;
            }
            Log.e(TAG," progressCourse "+progressCourse);
            userCourseData.setDownloadCompletePercentage((int) progressCourse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        RoomHelper.addorUpdateScoreDataClass(userCourseData);
        try {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(LessonsActivity.CourseDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            //currentLevelNo
            sendBroadcast(broadcastIntent);

            Intent broadcastIntent1 = new Intent();
            broadcastIntent1.setAction(NewLearningMapActivity.CourseDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent1.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showMediaDownloadProgress2() {
        int levelNo = 0;
        DTOUserCourseData userCourseData = RoomHelper.getScoreById(courseUniqNo);
        try {
            Log.d(TAG, "showMediaDownloadProgress: mediasize:" + mediaSize);
            float f1 = (float) mediaSize;
            float f2 = (float) (mediaList.size());
            if (((mediaList.size() > 0)) && (mediaSize > 0)) {
                float n1 = (f2 - f1) / f2;
                float currentMedia = (n1) * 50;
                int progressa = (int) (currentMedia + 50);
                float progressCourse = ((float) progressa / totalLevels) + ((float) 100 * (currentLevelNo)) / (totalLevels);
                if (progressCourse > 100) {
                    progressCourse = 100f;
                    progressMain = progressCourse;
                }
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        userCourseData.getUserLevelDataList().get(i).setCompletePercentage(progressa);
                        levelNo = i;
                        break;
                    }
                }
                if (progressCourse > 100) {
                    progressCourse = 100;
                    progressMain = progressCourse;
                }
                userCourseData.setDownloadCompletePercentage((int) progressCourse);
            } else {
                for (int i = 0; i < userCourseData.getUserLevelDataList().size(); i++) {
                    if (userCourseData.getUserLevelDataList().get(i).getLevelId() == courseLevelClass.getLevelId()) {
                        userCourseData.getUserLevelDataList().get(i).setCompletePercentage(100);
                        levelNo = i;
                        break;
                    }
                }
                float progressCourse = ((float) 100 * (currentLevelNo + 1)) / (totalLevels);
                if (progressCourse > 100) {
                    progressCourse = 100;
                    progressMain = progressCourse;
                }
                userCourseData.setDownloadCompletePercentage((int) progressCourse);
                if (currentLevelNo + 1 == totalLevels) {
                    userCourseData.setDownloadCompletePercentage(100);
                    progressMain = 100f;
                }
                this.stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        RoomHelper.addorUpdateScoreDataClass(userCourseData);

        try {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(LessonsActivity.CourseDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);

            Intent broadcastIntent1 = new Intent();
            broadcastIntent1.setAction(NewLearningMapActivity.CourseDownloadReceiver.PROCESS_RESPONSE);
            broadcastIntent1.addCategory(Intent.CATEGORY_DEFAULT);
            if (progressMain == 100) {
                broadcastIntent1.putExtra("PROGRESS", progressMain);
            }
            Log.d(TAG, "showMediaDownloadProgress: progressMain:%f" + progressMain);
            sendBroadcast(broadcastIntent1);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (userCourseData.getUserLevelDataList().get(levelNo).getCompletePercentage() >= 100) {
            downloadLevel();
        }
    }

    public void getlearningCardResponces(final long cardId, final long levelId, final int courseId) {
        Log.d(TAG, "getlearningCardResponces:Downloadcourseservice ");
        final LearningCardResponce[] learningCardResponces = {null};
        try {
            String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
            getCardUrl = getCardUrl.replace("cardId", String.valueOf(cardId));
            getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseId));
            getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(levelId));

            Log.i(TAG, "card Id to be downloaded " + cardId);
            /*new Thread() {
                @Override
                public void run() {
                    try {
                        OustRestClient oustRestClient = new OustRestClient();
                        LearningCardResponce learningCardResponce = oustRestClient.downloadCardData(finalGetCardUrl);

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

            //final Gson gson = new GsonBuilder().create();
            //String jsonParams = gson.toJson(null);
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            final String finalGetCardUrl = getCardUrl;
            Log.d(TAG, "getlearningCardResponces: "+finalGetCardUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, finalGetCardUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JasonData: "+response.toString());
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
                    Log.d(TAG, "onFailure: "+error.getLocalizedMessage());
                    learningCardResponces[0] = null;
                    cardDownloadOver(learningCardResponces[0]);
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleDownLoad(String fileURL, String fileDestinationPath, String fileName, boolean isOustLearn) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                asyncDownload(fileURL, fileDestinationPath, fileName, isOustLearn);
                return null;
            }
        }.execute();

    }

    private void asyncDownload(String fileURL, String fileDestinationPath, String fileName, boolean isOustLearn){
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
            Log.d(TAG, downloaded + " <- " + cacheDownloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        //fireOnUpdate(ON_INIT,"init ...");

        try {

            //File dir = new File(fileDestination);
            File chacheDir = OustSdkApplication.getContext().getCacheDir();
            if (!chacheDir.isDirectory())
                chacheDir.mkdirs();

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if (withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase("http://di5jfel2ggs8k.cloudfront.net/")) {
                returnData = "COMPLETED";
                sendUpdate(1, "completed");
                return;
            }

            /*if (!cacheDownloadFile.exists()) {
                cacheDownloadFile.createNewFile();
            } else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Create new cache file");
            }*/

            Log.d(TAG, "LINK " + downloadlink+"");
            URL url = new URL(downloadlink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (downloaded > 0)
                urlConnection.setRequestProperty("Range", "byte=" + downloaded);
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            if(status==404){
                returnData = "COMPLETED";
                sendUpdate(1, "completed");
                return;
            }

            InputStream inputStream = urlConnection.getInputStream();
            long totalSize = urlConnection.getContentLength();

            Log.d(TAG, "File Internetsize:"+totalSize+"  -- "+fileName);
            OustPreferences.saveTimeForNotification("oustlearn_"+fileName, totalSize);

            if(file.exists() && file.length()>=totalSize) {
                Log.d(TAG, "File Downloadedsize: "+file.length()+" --- file exit return  -- "+fileName);
                sendUpdate(1, "completed");
                //this.stopSelf();
                returnData = "COMPLETED";
                return;//file exist
            } else if(file.exists()){
                Log.d(TAG, "File size not matched, so deleting the previous file: "+file.getName());
                file.delete();
            }

            try {
                if (totalSize <= downloaded && cacheDownloadFile.exists()) {
                    if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                        Log.d(TAG, "cached file Copied before download --size:"+file.length()+" --- Name:"+file.getName());
                        cacheDownloadFile.delete();
                        returnData = "COMPLETED";
                        //Log.d(TAG, "handleDownLoad: download completed:");
                        sendUpdate(1, "completed");
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }

            if(!cacheDownloadFile.exists()){
                cacheDownloadFile.createNewFile();
            }
            else {
                cacheDownloadFile.delete();
                cacheDownloadFile.createNewFile();
                Log.d(TAG, "FILE_DOWNLOAD_TAG: Delete cache file");
            }

            this.downloadedPath = cacheDownloadFile.getAbsolutePath();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutput = new FileOutputStream(cacheDownloadFile);

            while ((bufferLength = bis.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloaded += bufferLength;
                float per = ((float) downloaded / (float) totalSize) * 100;
                String l = "" + (int) per;
                sendUpdate(2, l);
                if (downloaded >= totalSize) {
                    break;
                }
            }

            Log.d(TAG, "DWONLOADED TO " + downloadedPath + " (" + cacheDownloadFile.length() + ")");
            fileOutput.close();
            try {
                if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                    Log.d(TAG, "file Copied, delete cache");
                    cacheDownloadFile.delete();
                }
                else {
                    Log.d(TAG, "handleDownLoad: els part");
                    urlConnection.disconnect();
                    if (cacheDownloadFile.exists()) {
                        cacheDownloadFile.delete();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.d(TAG, "handleDownLoad: downlaoded copying data failed:");
                if(cacheDownloadFile.exists())
                {
                    cacheDownloadFile.delete();
                }
                if(file.exists())
                {
                    file.delete();
                }
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            sendUpdate(1, returnData);
            returnData = "COMPLETED";
            return;

            //commenting
            /*File dir = new File(fileDestination);
            File chacheDir = OustSdkApplication.getContext().getCacheDir();
            if (!chacheDir.isDirectory())
                chacheDir.mkdirs();
            *//*if(!dir.isDirectory()){
                dir.mkdirs();
            }*//*

            String withoutBase = OustMediaTools.removeAwsOrCDnUrl(fileURL);
            String filename2 = OustMediaTools.getMediaFileName(fileURL);
            if (withoutBase.equalsIgnoreCase(filename2) || fileURL.equalsIgnoreCase(AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH)) {
                //sendUpdate(1, "completed");
                returnData = "COMPLETED";
                return;
            }

            if (file.exists()) {
                Log.d(TAG, "File exist return complete");
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

            Log.d(TAG, "LINK " + downloadlink);
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
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            long d = 0;
            long starttime = System.currentTimeMillis();
            while ((bufferLength = bis.read(buffer)) > 0) {
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
            Log.d(TAG, "DWONLOADED TO " + downloadedPath + " (" + cacheDownloadFile.length() + ")");
            fileOutput.close();
            try {
                if (OustSdkTools.fileCopy(file, cacheDownloadFile)) {
                    Log.d(TAG, "file Copied, delete cache");
                    cacheDownloadFile.delete();
                    //file.renameTo(new File("oustlearn_"+file));
                }
                else {
                        Log.d(TAG, "handleDownLoad: els part");
                        urlConnection.disconnect();
                        if (cacheDownloadFile.exists()) {
                            cacheDownloadFile.delete();
                        }
                        if (file.exists()) {
                            file.delete();
                        }
                        handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.d(TAG, "handleDownLoad: downlaoded copying data failed:");
                if(cacheDownloadFile.exists())
                {
                    cacheDownloadFile.delete();
                }
                if(file.delete())
                {
                    file.delete();
                }
               // handleDownLoad(fileURL, fileDestinationPath, fileName, isOustLearn);
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
            sendUpdate(1, returnData);
            returnData = "COMPLETED";
            return;*/
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
                checkMediaExist(false);
                break;

        }
    }

    /*private void sendToDownloadService(String downloadPath, String destn, String fileName, boolean isOustLearn) {
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

    private class DownloadResultReceiver extends ResultReceiver {

        public DownloadResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case 1:
                    Log.d(TAG, "onReceiveResult: completed:" + resultData.getString("MSG"));
                    try {
                        if (resultData.getString("MSG").equalsIgnoreCase("completedDestroy")) {
                            updatePercentage();
                            removeFile();
                            downloadLevel();
                        } else {
                            removeFile();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                    break;

                case 2:
                    //Log.d(TAG, "onReceiveResult: progress:"+resultData.getString("MSG"));
                    break;

                case 3:
                    Log.d(TAG, "onReceiveResult: Error:" + resultData.getString("MSG"));
                    checkMediaExist(false);
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }

}
