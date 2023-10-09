package com.oustme.oustsdk.downloadHandler;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.CourseDataClass;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.oustHandler.InstrumentationHandler;
import com.oustme.oustsdk.oustHandler.Requests.InstrumentationMailRequest;
import com.oustme.oustsdk.oustHandler.dataVariable.IssueTypes;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.course.LearningCardResponce;
import com.oustme.oustsdk.room.EntityLevelCardCourseID;
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
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadForegroundService extends Service {

    private static final String TAG = DownloadForegroundService.class.getSimpleName();

    public static final String START_UPLOAD = "START_UPLOAD";
    public static final String CANCEL_UPLOAD = "CANCEL_UPLOAD";
    public static final String COURSE_DATA = "courseDataStr";
    public static final String LEVEL_DATA = "levelDataStr";
    public static final String LEVEL_DATA_FRAGMENT = "levelDataFragment";
    public static final String COURSE_ID = "courseId";
    public static final String COURSE_UNIQUE = "courseUnique";
    public static final String IS_DOWNLOAD_COURSE = "isDownloadingOnlyCourse";
    public static final String IS_DOWNLOAD_LEVEL = "isDownloadingOnlyLevel";
    public static final String IS_DIRECT_OPEN = "isDirectOpen";
    public static final String IS_DOWNLOAD_VIDEO = "downloadVideo";


    //data variable
    public String courseDataStr;
    public String courseLevelStr;
    public boolean isDownloadingOnlyCourse;
    public boolean isDownloadingOnlyLevel;
    public boolean isDownloadVideo;
    public boolean isFromFragment;
    public boolean isDirectOpen;
    public CourseDataClass courseDataClass;
    public CourseLevelClass courseLevelClass;
    public long courseUniqueId;
    public long courseUniqueIdDownload;
    public int progressPercentage;

    private List<String> mediaList, pathList;
    private List<String> videoMediaList, videoPathList;
    List<DTOCourseCard> courseCardClassList = new ArrayList<>();

    int totalLevel = 1;
    int checkTotalCardCount = 0;
    int downloadedLevel = 0;
    int totalLevelCards = 1;
    int downloadedLevelCards = 0;
    int retry = 2;
    int totalCount = 0, checkTotalCount = 0;
    double downPercentage = 0;

    NotificationManager notificationManager;

    public static final String ACTION_ERROR = "com.oustme.oustsdk.service.action.ERROR";
    public static final String ACTION_COMPLETE = "com.oustme.oustsdk.service.action.COMPLETE";
    public static final String ACTION_PROGRESS = "com.oustme.oustsdk.service.action.PROGRESS";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String action = intent.getAction();

            notificationManager = (NotificationManager) OustSdkApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (action != null && !TextUtils.isEmpty(action)) {
                switch (action) {
                    case START_UPLOAD:
                        mediaList = new ArrayList<>();
                        pathList = new ArrayList<>();

                        videoMediaList = new ArrayList<>();
                        videoPathList = new ArrayList<>();
                        courseCardClassList = new ArrayList<>();

                        courseDataStr = intent.getStringExtra(COURSE_DATA);
                        courseLevelStr = intent.getStringExtra(LEVEL_DATA);
                        isDownloadingOnlyCourse = intent.getBooleanExtra(IS_DOWNLOAD_COURSE, false);
                        isDownloadingOnlyLevel = intent.getBooleanExtra(IS_DOWNLOAD_LEVEL, false);
                        isDownloadVideo = intent.getBooleanExtra(IS_DOWNLOAD_VIDEO, false);
                        isDirectOpen = intent.getBooleanExtra(IS_DIRECT_OPEN, false);
                        isFromFragment = intent.getBooleanExtra(LEVEL_DATA_FRAGMENT, false);

                        String activeUserString = OustPreferences.get("userdata");
                        ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserString);

                        if (!OustSdkTools.checkInternetStatus()) {
                            OustSdkTools.showToast("No network to download");
                            try {
                                cancelUpload(OustSdkApplication.getContext());
//                                ((Activity) OustSdkApplication.getContext()).finish();
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        } else {
                            if (courseDataStr != null && !courseDataStr.isEmpty()) {
                                courseDataClass = new Gson().fromJson(courseDataStr, CourseDataClass.class);
                                if (courseDataClass != null) {
                                    String uniqueID = "" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                                    try {
                                        String uniqueIdDownLoad = "1" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                                        courseUniqueIdDownload = Long.parseLong(uniqueIdDownLoad);
                                        courseUniqueId = Long.parseLong(uniqueID);
                                    } catch (Exception e) {
                                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                    }
                                    showNotification();
                                    downloadCourseLevelData();
                                }
                            }

                            if (courseLevelStr != null && !courseLevelStr.isEmpty() && isDownloadingOnlyLevel) {
                                courseLevelClass = new Gson().fromJson(courseLevelStr, CourseLevelClass.class);
                                if (courseLevelClass != null) {
                                    String courseUniqueString = intent.getStringExtra(COURSE_UNIQUE);
                                    if (courseUniqueString != null) {
                                        courseUniqueIdDownload = Long.parseLong(courseUniqueString);
                                        courseUniqueId = Long.parseLong(courseUniqueString);
                                    }
                                    showLevelNotification();
                                    downloadLevelData();
                                }
                            }
                        }


                        break;
                    case CANCEL_UPLOAD:
                        NotificationManagerCompat.from(this).cancel((int) courseUniqueId);
                        Log.d(TAG, "CANCEL Notification");
                        stopForeground(true);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startUpload(Context context) {
        Intent intent = new Intent(context, DownloadForegroundService.class);
        intent.setAction(START_UPLOAD);
        intent.putExtra(COURSE_DATA, courseDataStr);
        intent.putExtra(COURSE_ID, courseDataClass.getCourseId());
        intent.putExtra(IS_DOWNLOAD_COURSE, isDownloadVideo);
        context.startService(intent);
    }

    public void cancelUpload(Context context) {
        Intent intent = new Intent(context, DownloadForegroundService.class);
        intent.setAction(CANCEL_UPLOAD);
        context.startService(intent);
    }

    private void downloadCourseLevelData() {

        try {
            totalLevel = 1;
            downloadedLevel = 0;
            checkTotalCardCount = 0;
            int totalCardsCount = 0;
            if (courseDataClass.getCourseLevelClassList() != null && courseDataClass.getCourseLevelClassList().size() != 0) {
                totalLevel = courseDataClass.getCourseLevelClassList().size();
                for (int i = 0; i < courseDataClass.getCourseLevelClassList().size(); i++) {
                    totalCardsCount = totalCardsCount + courseDataClass.getCourseLevelClassList().get(i).getCourseCardClassList().size();
                }
                for (CourseLevelClass courseLevelClass : courseDataClass.getCourseLevelClassList()) {
                    downloadedLevel++;
                    totalLevelCards = 1;
                    downloadedLevelCards = 0;
                    if (courseLevelClass.getCourseCardClassList() != null && courseLevelClass.getCourseCardClassList().size() != 0) {
                        totalLevelCards = courseLevelClass.getCourseCardClassList().size();
                        for (DTOCourseCard dtoCourseCard : courseLevelClass.getCourseCardClassList()) {
                            int downloadingCardId = (int) dtoCourseCard.getCardId();
                            DTOCourseCard cardFromDb = OustSdkTools.databaseHandler.getCardClass(downloadingCardId);
                            if (cardFromDb == null || cardFromDb.getCardId() == 0) {
                                downloadCardDataFromServer(dtoCourseCard.getCardId(), courseLevelClass);
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
                                updateDownloadPercentage(courseLevelClass, totalCardsCount);
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

    private void downloadLevelData() {
        try {
            totalLevelCards = 1;
            downloadedLevelCards = 0;
            if (courseLevelClass.getCourseCardClassList() != null && courseLevelClass.getCourseCardClassList().size() != 0) {
                totalLevelCards = courseLevelClass.getCourseCardClassList().size();
                for (DTOCourseCard dtoCourseCard : courseLevelClass.getCourseCardClassList()) {
                    int downloadingCardId = (int) dtoCourseCard.getCardId();
                    DTOCourseCard cardFromDb = OustSdkTools.databaseHandler.getCardClass(downloadingCardId);
                    if (cardFromDb == null || cardFromDb.getCardId() == 0) {
                        downloadCardDataFromServer(dtoCourseCard.getCardId(), courseLevelClass);
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
                        updateDownloadPercentage(courseLevelClass, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void downloadCardDataFromServer(long downloadingCardId, CourseLevelClass courseLevelClass) {

        try {

            String getCardUrl = OustSdkApplication.getContext().getResources().getString(R.string.getCardUrl_V2);
            getCardUrl = getCardUrl.replace("cardId", String.valueOf(downloadingCardId));
            getCardUrl = getCardUrl.replace("{courseId}", String.valueOf(courseLevelClass.getLpId()));
            getCardUrl = getCardUrl.replace("{levelId}", String.valueOf(courseLevelClass.getLevelId()));
            getCardUrl = HttpManager.getAbsoluteUrl(getCardUrl);

            Log.i(TAG, "Downloading card id " + downloadingCardId + " level id " + courseLevelClass.getLevelId() + " course id " + courseLevelClass.getLpId());

            ApiCallUtils.doNetworkCall(Request.Method.GET, getCardUrl, OustSdkTools.getRequestObject(null), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    Log.d(TAG, "onSuccess:JsonData: " + response.toString());
                    LearningCardResponce learningCardResponse = gson.fromJson(response.toString(), LearningCardResponce.class);

                    EntityLevelCardCourseID entityLevelCardCourseID = new EntityLevelCardCourseID();
                    entityLevelCardCourseID.setCardId(downloadingCardId);
                    entityLevelCardCourseID.setCourseId(courseLevelClass.getLpId());
                    entityLevelCardCourseID.setLevelId(courseLevelClass.getLevelId());
                    RoomHelper.addLevelCourseCardMap(entityLevelCardCourseID);

                    cardDataDownloaded(downloadingCardId, learningCardResponse, courseLevelClass, null);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onFailure: " + error.getLocalizedMessage());
                    cardDataDownloaded(downloadingCardId, null, courseLevelClass, error.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void cardDataDownloaded(long downloadingCardId, LearningCardResponce learningCardResponse, CourseLevelClass courseLevelClass, String errorMessage) {
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
                    courseCardClassList.add(cardDataForDb);
                    OustSdkTools.databaseHandler.addCardDataClass(cardDataForDb, (int) cardDataForDb.getCardId());
                    downloadCardMedia(cardDataForDb, courseLevelClass);
                } else {
                    OustSdkTools.showToast("Sorry ! Card data is missing . Please contact your admin.");
                    String cardId = "Card Id:";
                    String levelId = " Level Id: ";
                    if (downloadingCardId != 0) {
                        cardId = cardId + " " + downloadingCardId;
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
                    instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);
                }

            } else {
                //TODO: retry for two times
                if (retry > 0) {
                    retry--;
                    downloadCardDataFromServer(downloadingCardId, courseLevelClass);
                } else {

                    if (!OustSdkTools.checkInternetStatus()) {
                        OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.retry_internet_msg));
                    } else {
                        OustSdkTools.showToast("Sorry ! Card data is missing . Please contact your admin.");
                        String cardId = "Card Id:";
                        String levelId = " Level Id: ";
                        if (downloadingCardId != 0) {
                            cardId = cardId + " " + downloadingCardId;
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
                        OustSdkTools.showToast("Sorry ! Card data is missing . Please contact your admin.");
                        instrumentationMailRequest.setIssuesType(IssueTypes.COURSE_CARD_NOT_AVAILABLE.toString());
                        InstrumentationHandler instrumentationHandler = new InstrumentationHandler();
                        instrumentationHandler.hitInstrumentationAPI(OustSdkApplication.getContext(), instrumentationMailRequest);
                    }
                    if (courseDataClass != null) {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("learningmap_course_download");
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("Error", "Error");
                        sendBroadcast(broadcastIntent);
                    }
                    cancelUpload(OustSdkApplication.getContext());

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
                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                            path = "course/media/image/" + cardMedia.getData();
                            pathList.add(path);
                            mediaList.add(cardMedia.getData());
                        } else if (mediaType.equalsIgnoreCase("GIF")) {
                            path = "course/media/gif/" + cardMedia.getData();
                            pathList.add(path);
                            mediaList.add(cardMedia.getData());
                        } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                            path = "course/media/video/" + cardMedia.getData();
                            videoPathList.add(path);
                            videoMediaList.add(cardMedia.getData());
                            isVideo = true;
                        }

                        checkMediaExists(cardMedia.getData(), path, isVideo, courseLevelClass);
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
                            } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                path = "course/media/image/" + courseCardMedia.getData();
                                pathList.add(path);
                                mediaList.add(courseCardMedia.getData());
                            } else if (mediaType.equalsIgnoreCase("GIF")) {
                                path = "course/media/gif/" + courseCardMedia.getData();
                                pathList.add(path);
                                mediaList.add(courseCardMedia.getData());
                            } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                path = "course/media/video/" + courseCardMedia.getData();
                                videoPathList.add(path);
                                videoMediaList.add(courseCardMedia.getData());
                                isVideo = true;
                            }

                            checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
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
                } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                    path = "readmore/file/" + courseCard.getReadMoreData().getData();
                    pathList.add(path);
                    mediaList.add(courseCard.getReadMoreData().getData());
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
                videoMediaList.add(OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getGumletVideoUrl()));
                checkMediaExists(courseCard.getQuestionData().getGumletVideoUrl(), OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getGumletVideoUrl()), true, courseLevelClass);
            } else if (courseCard.getQuestionData() != null && courseCard.getQuestionData().getqVideoUrl() != null
                    && !courseCard.getQuestionData().getqVideoUrl().isEmpty() && isDownloadVideo) {
                videoPathList.add(courseCard.getQuestionData().getqVideoUrl());
                videoMediaList.add(OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getqVideoUrl()));
                checkMediaExists(courseCard.getQuestionData().getqVideoUrl(), OustMediaTools.removeAwsOrCDnUrl(courseCard.getQuestionData().getqVideoUrl()), true, courseLevelClass);
            }

            if (courseCard.getQuestionData() != null) {
                if (courseCard.getQuestionData().getImageCDNPath() != null) {
                    String media = courseCard.getQuestionData().getImageCDNPath();
                    String path = courseCard.getQuestionData().getImageCDNPath();
                    pathList.add(path);
                    mediaList.add(media);
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceA() != null && courseCard.getQuestionData().getImageChoiceA().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceA().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceA().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceB() != null && courseCard.getQuestionData().getImageChoiceB().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceB().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceB().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceC() != null && courseCard.getQuestionData().getImageChoiceC().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceC().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceC().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceD() != null && courseCard.getQuestionData().getImageChoiceD().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceD().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceD().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
                    checkMediaExists(media, path, false, courseLevelClass);
                }

                if (courseCard.getQuestionData().getImageChoiceE() != null && courseCard.getQuestionData().getImageChoiceE().getImageData() != null) {
                    String media = courseCard.getQuestionData().getImageChoiceE().getImageData();
                    String path = courseCard.getQuestionData().getImageChoiceE().getImageData();
                    pathList.add(path);
                    mediaList.add(media);
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
                                    } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                        path = "course/media/image/" + courseCardMedia.getData();
                                        pathList.add(path);
                                        mediaList.add(courseCardMedia.getData());
                                    } else if (mediaType.equalsIgnoreCase("GIF")) {
                                        path = "course/media/gif/" + courseCardMedia.getData();
                                        pathList.add(path);
                                        mediaList.add(courseCardMedia.getData());
                                    } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                        path = "course/media/video/" + courseCardMedia.getData();
                                        videoPathList.add(path);
                                        videoMediaList.add(courseCardMedia.getData());
                                        isVideo = true;
                                    }
                                    checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
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
                                        } else if (mediaType.equalsIgnoreCase("IMAGE")) {
                                            path = "course/media/image/" + courseCardMedia.getData();
                                            pathList.add(path);
                                            mediaList.add(courseCardMedia.getData());
                                        } else if (mediaType.equalsIgnoreCase("GIF")) {
                                            path = "course/media/gif/" + courseCardMedia.getData();
                                            pathList.add(path);
                                            mediaList.add(courseCardMedia.getData());
                                        } else if (isDownloadVideo && mediaType.equalsIgnoreCase("VIDEO")) {
                                            path = "course/media/video/" + courseCardMedia.getData();
                                            videoPathList.add(path);
                                            videoMediaList.add(courseCardMedia.getData());
                                            isVideo = true;
                                        }
                                        checkMediaExists(courseCardMedia.getData(), path, isVideo, courseLevelClass);
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
                            } else if ((rm_mediaType != null) && (rm_mediaType.equalsIgnoreCase("IMAGE"))) {
                                path = "readmore/file/" + videoOverlayCard.getReadMoreData().getData();
                                pathList.add(path);
                                mediaList.add(videoOverlayCard.getReadMoreData().getData());
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
            updateDownloadPercentage(courseLevelClass, 0);


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkMediaExists(String mediaPath, String path, boolean isVideo, CourseLevelClass courseLevelClass) {
        try {

            if (mediaPath != null && !mediaPath.isEmpty()) {
                if (mediaPath.contains(".zip")) {
                    String newFileName = mediaPath.replace(".zip", "");
                    final File file = new File(OustSdkApplication.getContext().getFilesDir(), newFileName);
                    if (!file.exists()) {
                        downloadFileFromServer(mediaPath, path, isVideo, courseLevelClass);
                    }
                } else {
                    if (OustMediaTools.isAwsOrCDnUrl(mediaPath)) {
                        mediaPath = OustMediaTools.removeAwsOrCDnUrl(mediaPath);
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, mediaPath, isVideo, courseLevelClass);
                        }
                    } else {
                        String fileName = OustMediaTools.getMediaFileName(mediaPath);
                        String url = "oustlearn_" + OustMediaTools.getMediaFileName(mediaPath);
                        File file = new File(OustSdkApplication.getContext().getFilesDir(), url);
                        if (!file.exists()) {
                            downloadFileFromServer(fileName, path, isVideo, courseLevelClass);
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

            boolean isOustLearn = !isVideo;

            if (isOustLearn) {
                fileName = "oustlearn_" + fileName;
            }
            totalCount++;
            if (isDownloadVideo) {
                try {
                    String activeUserString = OustPreferences.get("userdata");
                    ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserString);
                    if (courseDataStr != null && !courseDataStr.isEmpty()) {
                        courseDataClass = new Gson().fromJson(courseDataStr, CourseDataClass.class);
                        if (courseDataClass != null) {
                            String uniqueID = "1" + activeUser.getStudentKey() + "" + courseDataClass.getCourseId();
                            courseUniqueIdDownload = Long.parseLong(uniqueID);
                            Log.d("couresisdownloaded", "Service: " + courseUniqueIdDownload);
                            showCourseNotification((int) downPercentage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
            downloadManager(pathName, fileName, OustSdkApplication.getContext(), courseLevelClass);


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showNotification() {
        try {
            // Build a notification using bytesRead and contentLength

            Context context = getApplicationContext();
            String title = "Downloading course " + courseDataClass.getCourseName();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager, "COURSE_DOWNLOAD", "COURSE_DOWNLOAD_SERVICE");
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "COURSE_DOWNLOAD");
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.small_app_icon)
                    .setAutoCancel(true)
                    .build();
            //notificationManager.notify((int)courseUniqueId,notification);
            Log.d(TAG, "Show Notification");
            startForeground((int) courseUniqueId, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showCourseNotification(int downPercentage1) {
        try {
            // Build a notification using bytesRead and contentLength

            Context context = getApplicationContext();
            String title = "Downloading course " + courseDataClass.getCourseName();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager, "COURSE_FULL_DOWNLOAD", "COURSE_FULL_DOWNLOAD_SERVICE");
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "COURSE_FULL_DOWNLOAD");
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.small_app_icon)
                    .setAutoCancel(true)
                    .setProgress(100, (int) downPercentage1, false)
//                    .setOngoing(true)
                    .build();
            //notificationManager.notify((int)courseUniqueId,notification);
            Log.d(TAG, "Show Notification for course download " + courseUniqueIdDownload);
            startForeground((int) courseUniqueIdDownload, builder.build());
        } catch (Exception e) {
            Log.d(TAG, "Error while showing notification");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void showLevelNotification() {
        try {
            // Build a notification using bytesRead and contentLength

            Context context = getApplicationContext();
            String title = "Downloading course level " + courseLevelClass.getLevelName();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel(notificationManager, "LEVEL_DOWNLOAD", "LEVEL_DOWNLOAD_SERVICE");
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "LEVEL_DOWNLOAD");
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.small_app_icon)
                    .setAutoCancel(true)
                    .build();
            //notificationManager.notify((int)courseUniqueId,notification);
            startForeground((int) courseUniqueId, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }


    public void downloadManager(String downloadUrl, String fileName, Context context, CourseLevelClass courseLevelClass) {
        try {
            if (downloadUrl != null) {
                downloadUrl = AppConstants.MediaURLConstants.CLOUD_FRONT_BASE_PATH + downloadUrl;
                final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(downloadUrl);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle(fileName);
                request.setDescription(fileName);
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        request.setDestinationUri(destinationUri);
                request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, File.separator + fileName);
                long ref = downloadManager.enqueue(request);

                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);


                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long downloadReference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                        if (downloadReference == -1) {
                            return;
                        }

                        if (downloadReference == ref) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(downloadReference);

                            Cursor cur = downloadManager.query(query);

                            if (cur.moveToFirst()) {
                                int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                @SuppressLint("Range") int idFromCursor = cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_ID));

                                if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                                    @SuppressLint("Range") String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                    //update percentage
                                    Log.i("DownloadHandler", "Download completed->" + uriString);
                                    if (uriString != null) {
                                        File sourceFile = new File(Uri.parse(uriString).getPath());
                                        File destinationFile = new File(OustSdkApplication.getContext().getFilesDir(), fileName);
                                        try {
                                            OustSdkTools.copyFile(sourceFile, destinationFile);
                                            Log.i("DownloadHandler", "Download completed file-> " + destinationFile.getAbsolutePath());
                                        } catch (IOException e) {
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }
                                    }
//
                                } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                                    int columnReason = cur.getColumnIndex(DownloadManager.COLUMN_REASON);
                                    Log.i("DownloadHandler", "Download completed->-->" + columnReason);
                                    int reason = cur.getInt(columnReason);
                                    Log.i("DownloadHandler", "Download completed->-<-" + reason);
                                    switch (reason) {

                                        case DownloadManager.ERROR_FILE_ERROR:
                                            //handle error
                                            break;
                                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                            //handle error
                                            break;
                                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                            //handle error
                                            break;

                                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                            //handle error
                                            break;
                                        case DownloadManager.ERROR_UNKNOWN:
                                            //handle error
                                            break;
                                        case DownloadManager.ERROR_CANNOT_RESUME:
                                            //handle error
                                            break;
                                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                            //handle error
                                            break;
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
                            try {
                                checkTotalCount++;
                                if (totalCount >= 1) {
                                    downPercentage = ((double) checkTotalCount / (double) totalCount) * 100;
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("course_download_percentage");
                                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    broadcastIntent.putExtra("downloadPercentage", "" + downPercentage);
                                    broadcastIntent.putExtra("courseId", "" + courseUniqueIdDownload);
                                    sendBroadcast(broadcastIntent);
                                    showCourseNotification((int) downPercentage);
                                    if (totalCount == checkTotalCount) {
                                        Log.d("downloadCheck", "all are downloaded");
                                        totalCount = 0;
                                        checkTotalCount = 0;
                                        downPercentage = 0;
                                        try {
                                            cancelUpload(OustSdkApplication.getContext());
                                            NotificationManagerCompat.from(getApplicationContext()).cancel((int) courseUniqueIdDownload);
                                            Log.d(TAG, "CANCEL Notification for download");
                                            stopForeground(true);
                                        } catch (Exception e) {
                                            Log.d(TAG, "error while CANCEL Notification for download");
                                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
                    }
                };
                context.getApplicationContext().registerReceiver(receiver, filter);
            } else {
                try {
                    checkTotalCount++;
                    if (totalCount >= 1) {
                        downPercentage = ((double) checkTotalCount / (double) totalCount) * 100;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("course_download_percentage");
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("downloadPercentage", "" + downPercentage);
                        broadcastIntent.putExtra("courseId", "" + courseUniqueIdDownload);
                        sendBroadcast(broadcastIntent);
                        showCourseNotification((int) downPercentage);
                        if (totalCount == checkTotalCount) {
                            Log.d("downloadCheck", "all are downloaded");
                            totalCount = 0;
                            checkTotalCount = 0;
                            downPercentage = 0;
                            try {
                                cancelUpload(OustSdkApplication.getContext());
                                NotificationManagerCompat.from(getApplicationContext()).cancel((int) courseUniqueIdDownload);
                                Log.d(TAG, "CANCEL Notification for download");
                                stopForeground(true);
                            } catch (Exception e) {
                                Log.d(TAG, "error while CANCEL Notification for download");
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        }
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

    private void updateDownloadPercentage(CourseLevelClass courseLevelClass, int totalCardsCount) {
        try {
            checkTotalCardCount++;
            double levelPercentage = ((downloadedLevel * 1.0) / totalLevel) * 100;
            double levelCardPercentage = (levelPercentage / totalLevelCards) * downloadedLevelCards;
            double levelDownloadPercentage = ((downloadedLevelCards * 1.0) / totalLevelCards) * 100;
            progressPercentage = (int) (((downloadedLevel - 1) * levelPercentage) + levelCardPercentage);

            if (totalCardsCount != 0) {
                progressPercentage = (int) (((checkTotalCardCount * 1.0) / totalCardsCount) * 100);
            }
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

            if (!isDownloadingOnlyLevel) {
                if (progressPercentage >= 100) {
                    progressPercentage = 100;
                }
                userCourseData.setDownloadCompletePercentage(progressPercentage);
            }

            RoomHelper.addorUpdateScoreDataClass(userCourseData);


            if (!isDownloadingOnlyLevel && progressPercentage == 100) {
                if (!(totalCount >= 1)) {
                    cancelUpload(OustSdkApplication.getContext());
                }
                if (!isDirectOpen && courseDataClass != null) {
                    Intent broadcastIntent = new Intent();
                    if (totalCardsCount != 0) {
                        broadcastIntent.setAction("course_download_percentage");
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("downloadPercentage", "" + progressPercentage);
                        broadcastIntent.putExtra("courseId", "" + courseUniqueIdDownload);
                    } else {
                        broadcastIntent.setAction("course_download_response");
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    }
                    sendBroadcast(broadcastIntent);
                } else if (courseLevelClass != null) {
                    try {
                        Intent broadcastLevelIntent = new Intent();
                        broadcastLevelIntent.setAction("android.intent.action.ATTACH_DATA");
                        broadcastLevelIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        sendBroadcast(broadcastLevelIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }
            } else {
                if (!isFromFragment) {
                    if (courseLevelClass != null) {
                        try {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction("course_download_percentage");
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("downloadPercentage", "" + progressPercentage);
                            broadcastIntent.putExtra("courseId", "" + courseUniqueIdDownload);
                            sendBroadcast(broadcastIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                }
            }


            if (isDownloadingOnlyLevel) {
                if (levelDownloadPercentage == 100) {
                    if (!(totalCount >= 1)) {
                        cancelUpload(OustSdkApplication.getContext());
                    }
                    if (courseLevelClass != null) {
                        try {
                            Intent broadcastLevelIntent = new Intent();
                            broadcastLevelIntent.setAction(ACTION_COMPLETE);
                            broadcastLevelIntent.putExtra("PROGRESS", "" + levelDownloadPercentage);
                            broadcastLevelIntent.putExtra("MSG", "completedDestroy");
                            broadcastLevelIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastLevelIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                } else {
                    if (isFromFragment) {
                        if (courseLevelClass != null) {
                            try {
                                Intent broadcastLevelIntent = new Intent();
                                broadcastLevelIntent.setAction(ACTION_PROGRESS);
                                broadcastLevelIntent.putExtra("PROGRESS", "" + levelDownloadPercentage);
                                broadcastLevelIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastLevelIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
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

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager, String channelId, String channelName) {
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }
}
