package com.oustme.oustsdk.survey_module;

import static com.oustme.oustsdk.downloadmanger.DownloadFiles._COMPLETED;
import static com.oustme.oustsdk.tools.appconstants.AppConstants.StringConstants.CLOUD_FRONT_BASE_PATH;

import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.AssessmentCopyResponse;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.data.handlers.impl.MediaDataDownloader;
import com.oustme.oustsdk.downloadmanger.DownLoadUpdateInterface;
import com.oustme.oustsdk.downloadmanger.DownloadFiles;
import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardClass;
import com.oustme.oustsdk.request.AssmntGamePlayRequest;
import com.oustme.oustsdk.request.CreateGameRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.CreateGameResponse;
import com.oustme.oustsdk.response.assessment.QuestionResponce;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOCourseCardMedia;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.survey_module.model.ComponentToolBar;
import com.oustme.oustsdk.survey_module.model.SurveyComponentModule;
import com.oustme.oustsdk.survey_module.model.SurveyModule;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SurveyComponentRepository {

    private static final String TAG = "SurveyComponentRepo";
    private static SurveyComponentRepository instance;
    private MutableLiveData<SurveyComponentModule> liveData;
    private SurveyComponentModule surveyComponentModule;
    AssessmentPlayResponse surveyPlayResponse;

    //logic variable
    String surveyIdString;
    String surveyTitle;
    DTONewFeed feed;
    DTOUserFeedDetails.FeedDetails userFeed;
    Long feedID, surveyId, courseId, cplId, associatedAssessmentId;
    boolean updateComment;
    boolean isFeedComment, isFeedLikeAble, isFeedChange;
    private long rewardOC, exitOC;
    private boolean isMultipleCplEnable = false;
    private long isCplId;
    private long mappedAssessmentId;

    ActiveUser activeUser;
    ActiveGame activeGame;
    private CourseCardClass introCard;
    private CourseCardClass resultCard;
    private DownloadFiles downloadFiles;
    private int mediaSize = 0;

    List<String> mediaList = new ArrayList<>();
    private PlayResponse playResponse;
    private int downloadQuestionNo = 0;
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int noOfTry = 0;
    private int mediaDownloadCount = 0;


    private SurveyComponentRepository() {
    }

    public static SurveyComponentRepository getInstance() {
        if (instance == null)
            instance = new SurveyComponentRepository();
        return instance;
    }

    public MutableLiveData<SurveyComponentModule> getLiveData(Bundle bundle) {
        liveData = new MutableLiveData<>();
        fetchBundleData(bundle);
        return liveData;
    }

    private void fetchBundleData(Bundle dataBundle) {

        if (dataBundle != null) {
            try {
                surveyIdString = dataBundle.getString("assessmentId");
                surveyTitle = dataBundle.getString("surveyTitle");

                try {
                    feedID = dataBundle.getLong("FeedID", 0);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    feedID = Long.valueOf(dataBundle.getString("FeedID"));
                }
                try {
                    courseId = dataBundle.getLong("courseId", 0);
                    userFeed = dataBundle.getParcelable("Feed");
                    isFeedComment = dataBundle.getBoolean("FeedComment");
                    isFeedLikeAble = dataBundle.getBoolean("isFeedLikeable");
                    isCplId = dataBundle.getLong("cplId", 0);
                    associatedAssessmentId = dataBundle.getLong("associatedAssessmentId", 0);

                    isCplId = dataBundle.getLong("cplId", 0);
                    isMultipleCplEnable = dataBundle.getBoolean("isMultipleCplModule", false);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                try {
                    if (courseId > 0) {
                        mappedAssessmentId = dataBundle.getLong("mappedAssessmentId", 0);
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

        activeUser = OustAppState.getInstance().getActiveUser();
        activeGame = new ActiveGame();

        if (activeUser != null) {

            if ((surveyIdString != null) && (!surveyIdString.isEmpty())) {
                try {
                    surveyId = Long.parseLong(surveyIdString);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    surveyId = Long.valueOf(surveyIdString);
                }
                try {
                    if (surveyId != 0) {
                        getSurveyFromFirebase();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }
    }

    private void getSurveyFromFirebase() {
        try {
            String node = OustSdkApplication.getContext().getResources().getString(R.string.survey_base_node) + surveyId;
            Log.d(TAG, "getSurveyFromFirebase: " + node);
            ValueEventListener surveyListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Log.e(TAG, "Live data change test");
                        if (dataSnapshot.getValue() != null) {
                            final Map<String, Object> surveyBaseMap = (Map<String, Object>) dataSnapshot.getValue();
                            // CommonTools commonTools = new CommonTools();
                            if (surveyBaseMap != null) {
                                Gson gson = new Gson();
                                JsonElement surveyElement = gson.toJsonTree(surveyBaseMap);
                                SurveyModule surveyModule = gson.fromJson(surveyElement, SurveyModule.class);
                                surveyComponentModule = new SurveyComponentModule();
                                // TODO check here for crash
                                if (courseId != null && courseId > 0 && mappedAssessmentId > 0) {
                                    surveyComponentModule.setMappedAssessmentId(mappedAssessmentId);
                                }

                                ComponentToolBar componentToolBar = new ComponentToolBar();
                                componentToolBar.setComponentName(OustSdkApplication.getContext().getResources().getString(R.string.survey_text));
                                surveyComponentModule.setComponentToolBar(componentToolBar);
                                surveyComponentModule.setSurveyModule(surveyModule);
                                if (courseId != null && courseId != 0) {
                                    surveyComponentModule.setCourseId(courseId);
                                }

                                surveyComponentModule.setCplId(isCplId);
                                if (associatedAssessmentId != null)
                                    surveyComponentModule.setAssociatedAssessmentId(associatedAssessmentId);

                                if (userFeed != null && surveyComponentModule != null) {
                                    surveyComponentModule.setUserFeed(userFeed);
                                    surveyComponentModule.setFeedComment(isFeedComment);
                                }

                                if (surveyModule != null) {
                                    if (surveyModule.getDescriptionCard() != null) {
                                        handleCard(surveyModule.getDescriptionCard(), true);
                                    }

                                    exitOC = surveyModule.getExitOC();
                                    rewardOC = surveyModule.getRewardOC();
                                    surveyComponentModule.setExitOC(exitOC);
                                    surveyComponentModule.setRewardOC(rewardOC);

                                    checkForSavedSurvey(activeUser);
                                }

                            } else {
                                //handle base node null object
                                OustSdkTools.showToast("Sorry.Something went wrong");

                            }

                        } else {
                            //handle base node null object
                            OustSdkTools.showToast("No Data available");
                            liveData.postValue(null);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: exception");
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //handle firebase cancelled error
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(surveyListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);

        } catch (Exception e) {
            Log.e(TAG, "Error in get survey " + e.getLocalizedMessage());
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleCard(DTOCourseCard cardData, boolean isIntroCard) {

        try {

            List<String> mediaList = new ArrayList<>();
            List<String> pathList = new ArrayList<>();
            if ((cardData != null) && (cardData.getCardMedia() != null) && (cardData.getCardMedia().size() > 0)) {
                for (DTOCourseCardMedia cardMedia : cardData.getCardMedia()) {
                    if (cardMedia.getData() != null) {
                        switch (cardMedia.getMediaType()) {
                            case "IMAGE":
                                pathList.add("course/media/image/" + cardMedia.getData());
                                mediaList.add(cardMedia.getData());
                                break;
                            case "GIF":
                                pathList.add("course/media/gif/" + cardMedia.getData());
                                mediaList.add(cardMedia.getData());
                                break;
                        }
                    }

                }

            }
           /* if ((cardData != null) && (cardData.getReadMoreData() != null) && (cardData.getReadMoreData().getRmId() > 0)) {
                switch (cardData.getReadMoreData().getType()) {
                    case "PDF":
                    case "IMAGE":
                        pathList.add("readmore/file/" + cardData.getReadMoreData().getData());
                        mediaList.add(cardData.getReadMoreData().getData());
                        break;
                }
            }*/
            checkMediaExist(mediaList, pathList, cardData, isIntroCard);


        } catch (Exception e) {
            Log.e(TAG, "Error in handling card in survey");
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }


    }

    private void checkMediaExist(List<String> mediaList, List<String> pathList, final DTOCourseCard cardData, final boolean isIntroCard) {
        //courseCardClass1 = courseCardClass; <--- Removed to reduce code but need to check with Receiver -->
        //isIntroCard1 = isIntroCard;
        downloadFiles = new DownloadFiles(OustSdkApplication.getContext(), new DownLoadUpdateInterface() {
            @Override
            public void onDownLoadProgressChanged(String message, String progress) {
                Log.d(TAG, "onDownLoadProgressChanged: " + progress);
            }

            @Override
            public void onDownLoadError(String message, int errorCode) {
                Log.d(TAG, "onDownLoadError: message" + message + " errorCode:" + errorCode);
            }

            @Override
            public void onDownLoadStateChanged(String message, int code) {
                if (code == _COMPLETED) {
                    removeFile(cardData, isIntroCard);
                }
            }

            @Override
            public void onAddedToQueue(String id) {

            }

            @Override
            public void onDownLoadStateChangedWithId(String message, int code, String id) {

            }
        });
        mediaSize = 0;
        for (int i = 0; i < mediaList.size(); i++) {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            if (!enternalPrivateStorage.isFileAvialableInInternalStorage("oustlearn_" + mediaList.get(i))) {
                mediaSize++;
                downLoad(mediaList.get(i), pathList.get(i), cardData, isIntroCard);
            }
        }
        if (mediaSize == 0) {
            removeFile(cardData, isIntroCard);
        }
    }

    public void downLoad(final String fileName1, final String pathName, final DTOCourseCard cardData, final boolean isIntroCard) {
        try {
            if ((!OustSdkTools.checkInternetStatus())) {
                cardDownloadComplete(cardData, isIntroCard);
                return;
            }
            surveyComponentModule.setIntroCard(isIntroCard);
            surveyComponentModule.setCardClass(cardData);
            String destination = OustSdkApplication.getContext().getFilesDir() + "/";
            downloadFiles.startDownLoad(CLOUD_FRONT_BASE_PATH + pathName, destination, fileName1, true, false);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void removeFile(DTOCourseCard cardData, boolean isIntroCard) {
        if (mediaSize > 0) {
            mediaSize--;
        }
        if (mediaSize == 0) {
            cardDownloadComplete(cardData, isIntroCard);
        }
    }

    private void cardDownloadComplete(DTOCourseCard cardData, boolean isIntroCard) {

        surveyComponentModule.setIntroCard(isIntroCard);
        surveyComponentModule.setCardClass(cardData);
        //   liveData.postValue(surveyComponentModule);
    }

    public void checkForSavedSurvey(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + surveyId;
            if (isMultipleCplEnable) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + isCplId + "/contentListMap/assessment" + surveyId;
            } else {
                if (courseId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
                } else if (associatedAssessmentId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/assessment" + associatedAssessmentId + "/surveyAssessment" + surveyId;
                }
            }

            Log.d(TAG, "checkForSavedSurvey: node : " + node);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> surveyProgressMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (surveyProgressMap != null) {
                            Gson gson = new Gson();
                            JsonElement surveyProgressElement = gson.toJsonTree(surveyProgressMap);
                            surveyPlayResponse = gson.fromJson(surveyProgressElement, AssessmentPlayResponse.class);
                            surveyComponentModule.setSurveyPlayResponse(surveyPlayResponse);
                            if (surveyPlayResponse != null) {
                                if (surveyPlayResponse.getScoresList() != null && surveyPlayResponse.getScoresList().size() != 0) {
                                    if (surveyPlayResponse.getScoresList().size() == surveyPlayResponse.getTotalQuestion()
                                            && !surveyPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED)) {
                                        setResumeProgressWithPercentage(99);
                                    } else if (surveyPlayResponse.getScoresList().size() < surveyPlayResponse.getTotalQuestion()) {
                                        setResumeProgress(surveyPlayResponse.getScoresList().size(), surveyPlayResponse.getTotalQuestion());
                                    }

                                } else {
                                    setResumeProgress(surveyPlayResponse.getQuestionIndex(), surveyPlayResponse.getTotalQuestion());
                                }

                                if (activeGame != null && surveyPlayResponse.getGameId() != null &&
                                        !surveyPlayResponse.getGameId().isEmpty()) {
                                    activeGame.setGameid(surveyPlayResponse.getGameId());
                                    surveyComponentModule.setActiveGameId(surveyPlayResponse.getGameId());
                                    OustAppState.getInstance().setActiveGame(activeGame);
                                }
                            }

                        }
                        liveData.postValue(surveyComponentModule);

                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        liveData.postValue(surveyComponentModule);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    liveData.postValue(surveyComponentModule);
                }
            };
            OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(assessmentListener);
            OustFirebaseTools.getRootRef().child(node).keepSynced(true);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            liveData.postValue(surveyComponentModule);
        }
    }

    public void sendFeedComment(AlertCommentData alertCommentData) {
        if (alertCommentData != null) {
            alertCommentData.setUserAvatar(activeUser.getAvatar());
            alertCommentData.setUserId(activeUser.getStudentid());
            alertCommentData.setUserKey(Long.parseLong(activeUser.getStudentKey()));
            alertCommentData.setUserDisplayName(activeUser.getUserDisplayName());
            alertCommentData.setNumReply(0);

            sendCommentToFirebase(alertCommentData, "" + userFeed.getFeedId());
            isFeedChange = true;
            surveyComponentModule.setFeedChange(true);
            liveData.postValue(surveyComponentModule);
        }
    }

    private void sendCommentToFirebase(AlertCommentData alertCommentData, String feedId) {
        String message = "/userFeedComments/" + "feed" + feedId;
        DatabaseReference postRef = OustFirebaseTools.getRootRef().child(message);
        DatabaseReference newPostRef = postRef.push();
        newPostRef.setValue(alertCommentData);
        OustFirebaseTools.getRootRef().child(message).keepSynced(false);
        setDataToUserFeedThread(newPostRef.getKey(), feedId);
        updateFeedViewed(userFeed);
    }

    private void setDataToUserFeedThread(String key, String feedId) {
        String message = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "commentThread/" + key;
        OustFirebaseTools.getRootRef().child(message).setValue(true);
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);

        String message1 = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedId + "/" + "isCommented";
        OustFirebaseTools.getRootRef().child(message1).setValue(true);
        OustFirebaseTools.getRootRef().child(message1).keepSynced(true);

    }

    private void updateFeedViewed(DTOUserFeedDetails.FeedDetails mFeed) {
        try {
//            if (!mFeed.isClicked()) {
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
//            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void setResumeProgressWithPercentage(int percentage) {

        if (surveyComponentModule != null) {

            String progressText = percentage + " % ";
            surveyComponentModule.setProgress(percentage);
            surveyComponentModule.setProgressDone(progressText);
            liveData.postValue(surveyComponentModule);
        }


    }

    private void setResumeProgress(int questionIndex, int totalQuestions) {

        if (surveyComponentModule != null) {
            if (surveyComponentModule.getSurveyModule() != null && surveyComponentModule.getSurveyModule().getNumQuestions() != 0) {
                totalQuestions = (int) surveyComponentModule.getSurveyModule().getNumQuestions();
            }

            int progressDone = 0;
            if (questionIndex <= totalQuestions) {
                double value = (questionIndex * 1.0) / totalQuestions;
                progressDone = ((int) (value * 100));

            } else {
                progressDone = 100;

            }
            String progressText = progressDone + " % ";
            surveyComponentModule.setProgress(progressDone);
            surveyComponentModule.setProgressDone(progressText);
            liveData.postValue(surveyComponentModule);
        }


    }

    public void checkSurveyState() {
        try {
            if (surveyComponentModule != null && surveyComponentModule.getSurveyPlayResponse() != null) {
                AssessmentPlayResponse surveyPlayResponse = surveyComponentModule.getSurveyPlayResponse();
                if (surveyPlayResponse.getAssessmentState() != null &&
                        surveyPlayResponse.getGameId() != null &&
                        !surveyPlayResponse.getGameId().isEmpty() && surveyPlayResponse.getScoresList() != null) {
                    if (activeGame != null && surveyPlayResponse.getGameId() != null &&
                            !surveyPlayResponse.getGameId().isEmpty()) {
                        activeGame.setGameid(surveyPlayResponse.getGameId());
                        surveyComponentModule.setActiveGameId(surveyPlayResponse.getGameId());
                        OustAppState.getInstance().setActiveGame(activeGame);
                    }
                    getPlayResponse(surveyPlayResponse.getGameId());
                } else if (activeGame.getGameid() != null && !activeGame.getGameid().isEmpty()) {
                    getPlayResponse(activeGame.getGameid());
                } else {
                    startSurvey();
                }
            } else {
                startSurvey();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getPlayResponse(String gameId) {
        try {
            mediaList = new ArrayList<>();
            String playGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.play_game);
            playGameUrl = HttpManager.getAbsoluteUrl(playGameUrl);

            AssmntGamePlayRequest assmntGamePlayRequest = new AssmntGamePlayRequest();
            assmntGamePlayRequest.setGameid(gameId);
            assmntGamePlayRequest.setStudentid(activeUser.getStudentid());
            assmntGamePlayRequest.setOnlyQId(true);
            assmntGamePlayRequest.setDevicePlatformName("android");
            String assessmentID = "" + surveyId;
            assmntGamePlayRequest.setSurveyid(surveyId);
            assmntGamePlayRequest.setCourseId(courseId);
            if (!assessmentID.isEmpty())
                assmntGamePlayRequest.setAssessmentId(assessmentID);
            final Gson gson = new Gson();
            String jsonParams = gson.toJson(assmntGamePlayRequest);

            ApiCallUtils.doNetworkCall(Request.Method.POST, playGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    playResponse = gson.fromJson(response.toString(), PlayResponse.class);
                    gotPlayResponse();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotPlayResponse() {
        try {
            if (playResponse != null) {
                if (playResponse.isSuccess()) {
                    if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                        downloadQuestionNo = 0;
                        incrementDownloadQuestionNO = 0;
                        totalQuestion = 0;
                        noOfTry = 0;
                        totalQuestion = playResponse.getqIdList().size();
                        startDownloadingQuestions();
                    } else {
                        OustSdkTools.showToast(OustStrings.getString("unable_fetch_connection_error"));
                    }
                } else {
                    OustSdkTools.handlePopup(playResponse);
                }
            } else {
                OustSdkTools.showToast(OustStrings.getString("unable_fetch_connection_error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void startDownloadingQuestions() {
        try {
            incrementDownloadQuestionNO += 10;
            if (incrementDownloadQuestionNO > totalQuestion) {
                incrementDownloadQuestionNO = totalQuestion;
            }
            for (int i = downloadQuestionNo; i < incrementDownloadQuestionNO; i++) {
                if (OustSdkTools.checkInternetStatus()) {
                    getQuestionById(playResponse.getqIdList().get(i));
                } else {

                    DTOQuestions realmQuestions = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));
                    if ((realmQuestions != null) && (realmQuestions.getQuestionId() > 0)) {
                        OustMediaTools.prepareMediaList(mediaList, realmQuestions);
                        updateCompletePercentage();
                    } else {
                        getQuestionById(playResponse.getqIdList().get(i));
                    }

                }
            }
            if (totalQuestion == 0) {
                updateCompletePercentage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getQuestionById(final int qID) {
        try {
            //String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getquestion_url);
            String getQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.getQuestionUrl_V2);
            getQuestionUrl = getQuestionUrl.replace("{QId}", ("" + qID));
            getQuestionUrl = HttpManager.getAbsoluteUrl(getQuestionUrl);
            JSONObject requestParams = OustSdkTools.appendDeviceAndAppInfoInQueryParam();
            Log.d(TAG, "getQuestionById:V2: " + getQuestionUrl);

            ApiCallUtils.doNetworkCall(Request.Method.GET, getQuestionUrl, OustSdkTools.getRequestObjectforJSONObject(requestParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    DTOQuestions questions = getQuestion(response.toString());


                    try {
                        if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                            questions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                            questions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                            questions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                        } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                            questions.setQuestionType(QuestionType.LONG_ANSWER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }

                    checkForDownloadComplete(questions, qID);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public DTOQuestions getQuestion(String jsonString) {
        if ((jsonString != null) && (!jsonString.isEmpty())) {
            try {
                Gson gson = new GsonBuilder().create();

                QuestionResponce questionResponce = gson.fromJson(jsonString, QuestionResponce.class);
                Questions questions = new Questions();
                if (questionResponce != null) {
                    if (questionResponce.getQuestions() != null && questionResponce.getQuestions().size() != 0) {
                        questions = questionResponce.getQuestions().get(0);
                    }
                }

                DTOQuestions decryptQuestion = OustSdkTools.decryptQuestion(questionResponce.getQuestionsList().get(0), null);
                if (questions != null && decryptQuestion != null) {
                    if (questions.getBgImg() != null && !questions.getBgImg().isEmpty()) {
                        decryptQuestion.setBgImg(questions.getBgImg());
                    }

                }
                Log.e(TAG, new Gson().toJson(decryptQuestion));
                return decryptQuestion;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void checkForDownloadComplete(DTOQuestions questions, int qId) {
        if (questions != null) {
            OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
            updateCompletePercentage();
        } else {
            noOfTry++;
            if (noOfTry < 4) {
                getQuestionById(qId);
            } else {
                OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            }
        }
    }

    public void updateCompletePercentage() {
        downloadQuestionNo++;
        String downloadText;
        if (incrementDownloadQuestionNO == downloadQuestionNo) {
            if (incrementDownloadQuestionNO == totalQuestion) {
                downloadText = 100 + "%";
                surveyComponentModule.setDownloadProgress(downloadText);
                liveData.postValue(surveyComponentModule);

                for (int i = 0; i < incrementDownloadQuestionNO; i++) {
                    DTOQuestions realmQuestions = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));
                    if ((realmQuestions != null) && (realmQuestions.getQuestionId() > 0)) {
                        OustMediaTools.prepareMediaList(mediaList, realmQuestions);

                    }
                }
                downloadMediaFiles();

            } else {
                startDownloadingQuestions();
            }
        } else {
            float percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 100;
            if (percentage < 100) {
                downloadText = ((int) percentage) + "%";
            } else {
                downloadText = 100 + "%";
            }

            surveyComponentModule.setDownloadProgress(downloadText);
            liveData.postValue(surveyComponentModule);
        }
    }

    private void downloadMediaFiles() {
        File file;
        String path;
        int i = 0;
        String downloadText;
        if (mediaList != null && mediaList.size() > 0) {

            for (i = 0; i < mediaList.size(); i++) {
                if (mediaList.get(i).equalsIgnoreCase("")) {
                    mediaList.remove(i);
                }
            }
            for (i = 0; i < mediaList.size(); i++) {
                path = OustSdkApplication.getContext().getFilesDir() + "/oustlearn_" + OustMediaTools.getMediaFileName(mediaList.get(i));
                file = new File(path);
                if (!file.exists()) {
                    downloadMedia(mediaList.get(i));
                } else {
                    mediaDownloadCount++;
                    if (mediaDownloadCount == mediaList.size()) {
                        downloadText = 100 + "%";
                        surveyComponentModule.setDownloadProgress(downloadText);
                        liveData.postValue(surveyComponentModule);
                        fetchingDataFinish();
                    } else {
                        float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                        if (percentage < 100) {
                            downloadText = ((int) percentage) + "%";
                        } else {
                            fetchingDataFinish();
                            downloadText = 100 + "%";
                        }
                        surveyComponentModule.setDownloadProgress(downloadText);
                        liveData.postValue(surveyComponentModule);

                    }
                }
            }
        } else {
            fetchingDataFinish();
        }
    }

    private void downloadMedia(String media) {

        new MediaDataDownloader(OustSdkApplication.getContext()) {
            @Override
            public void downloadComplete() {
                mediaDownloadCount++;
                Log.d(TAG, "downloadComplete: mediaDownloadCount:" + mediaDownloadCount);
                String downloadText = "";
                if (mediaDownloadCount == mediaList.size()) {
                    downloadText = 100 + "%";
                    surveyComponentModule.setDownloadProgress(downloadText);
                    liveData.postValue(surveyComponentModule);
                    fetchingDataFinish();
                } else {
                    float percentage = ((float) mediaDownloadCount / (float) mediaList.size()) * 100;
                    if (percentage < 100) {
                        downloadText = ((int) percentage) + "%";
                    } else {
                        downloadText = 100 + "%";

                    }
                    surveyComponentModule.setDownloadProgress(downloadText);
                    liveData.postValue(surveyComponentModule);
                }
            }

            @Override
            public void downFailed(String message) {
                OustSdkTools.showToast(message);
            }
        }.initDownload(media);
    }

    private void fetchingDataFinish() {
        if (surveyComponentModule != null) {
            AssessmentPlayResponse surveyPlayResponse = surveyComponentModule.getSurveyPlayResponse();
            if ((surveyPlayResponse != null) && (surveyPlayResponse.getAssessmentState() != null) &&
                    (surveyPlayResponse.getGameId() != null) && (!surveyPlayResponse.getGameId().isEmpty()) &&
                    (surveyPlayResponse.getScoresList() != null)) {
                surveyComponentModule.setScoresList(surveyPlayResponse.getScoresList());
                surveyComponentModule.setChallengerFinalScore(surveyPlayResponse.getChallengerFinalScore());
                int questionIndex = surveyPlayResponse.getQuestionIndex();
                if (questionIndex >= surveyPlayResponse.getScoresList().size()) {
                    questionIndex = (surveyPlayResponse.getScoresList().size() - 1);
                }
                surveyComponentModule.setQuestionIndex(questionIndex);
            } else {
                surveyComponentModule.setScoresList(new ArrayList<>());
                surveyComponentModule.setChallengerFinalScore(0);
                surveyComponentModule.setQuestionIndex(0);
            }
            surveyComponentModule.setAssessmentRunning(true);
            surveyComponentModule.setStartDateTime(TimeUtils.getCurrentDateAsString());
            surveyComponentModule.setFetchCompleted(true);
            surveyComponentModule.setPlayResponse(playResponse);
            liveData.postValue(surveyComponentModule);

        }

    }

    private void startSurvey() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setAssessmentId(("" + surveyId));
        createGameRequest.setChallengerid(activeUser.getStudentid());
        createGameRequest.setGuestUser(false);
        createGameRequest.setRematch(false);

        if (courseId != 0) {
            createGameRequest.setCourseId(courseId);
        }

        String laungeStr = Locale.getDefault().getLanguage();
        createGameRequest.setAssessmentLanguage(laungeStr);
        activeGame = new ActiveGame();
        createGame(createGameRequest);
    }

    public void createGame(CreateGameRequest createGameRequest) {
        String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.create_game);
        createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
        Gson gson = new GsonBuilder().create();
        String jsonParams = gson.toJson(createGameRequest);
        Log.d(TAG, "createGame: " + jsonParams);

        ApiCallUtils.doNetworkCall(Request.Method.POST, createGameUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CreateGameResponse createGameResponse = gson.fromJson(response.toString(), CreateGameResponse.class);
                gotCreateGameResponse(createGameResponse);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void gotCreateGameResponse(CreateGameResponse createGameResponse) {
        if (createGameResponse != null) {
            if (createGameResponse.isSuccess()) {
                activeGame.setGameid("" + createGameResponse.getGameid());
                surveyComponentModule.setActiveGameId("" + createGameResponse.getGameid());
                OustAppState.getInstance().setActiveGame(activeGame);
                getPlayResponse(activeGame.getGameid());
            } else {
                OustSdkTools.handlePopup(createGameResponse);
            }
        } else {
            OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
        }
    }

    /*public void handleFeedComplete() {
        if (feedID > 0) {
            try {
                String node = "/userFeed/" + activeUser.getStudentKey() + "/feed" + feedID + "/userCompletionPercentage";
                OustFirebaseTools.getRootRef().child(node).setValue("100");
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }*/

    public void handleSurveyCompletePopUp(String content, boolean isExitSurvey) {
        Popup popup = new Popup();
        OustPopupButton oustPopupButton = new OustPopupButton();
        oustPopupButton.setBtnText("OK");
        List<OustPopupButton> btnList = new ArrayList<>();
        btnList.add(oustPopupButton);
        popup.setButtons(btnList);
        popup.setContent(content);
        popup.setCategory(OustPopupCategory.NOACTION);
        OustStaticVariableHandling.getInstance().setOustpopup(popup);

        if (activeUser != null && activeGame != null) {
            surveyPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), "0", null, activeGame.getGameid(), "0", AssessmentState.SUBMITTED);

            AssessmentCopyResponse surveySubmitResponse = new AssessmentCopyResponse();
            surveySubmitResponse.setStudentId(activeUser.getStudentid());
            surveySubmitResponse.setScoresList(null);
            surveySubmitResponse.setQuestionIndex("0");
            surveySubmitResponse.setGameId(activeGame.getGameid());
            surveySubmitResponse.setChallengerFinalScore("0");
            if (courseId != 0) {
                surveySubmitResponse.setCourseId(String.valueOf(courseId));
            }
            surveySubmitResponse.setAssessmentState(AssessmentState.SUBMITTED);

            saveSurveyGame(surveySubmitResponse, activeUser);
        }
    }

    public void saveSurveyGame(AssessmentCopyResponse surveySubmitResponse, ActiveUser activeUser) {

        try {
            Log.d(TAG, "saveAssessmentGame: ");
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + surveyId;
            if (isMultipleCplEnable) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + isCplId + "/contentListMap/assessment" + surveyId;
            } else {
                if (courseId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + surveyId;
                    ;
                } else if (associatedAssessmentId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/assessment" + associatedAssessmentId + "/surveyAssessment" + surveyId;
                }
            }

            OustFirebaseTools.getRootRef().child(node).setValue(surveySubmitResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

}
