package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import static android.content.ContentValues.TAG;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.request.AssessmentEnrollCreateGameRequest;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.QuestionResponse;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.sqlite.EnternalPrivateStorage;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustMediaTools;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.ShowPopup;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AssessmentDetailRepo {

    public MutableLiveData<AssessmentDetailModel> assessmentDetailModelMutableLiveData = new MutableLiveData<>();
    Bundle dataBundle;
    ActiveUser activeUser;
    Context context;
    AssessmentPlayResponse assessmentPlayResponse;
    AssessmentDetailModel assessmentDetailModel;
    AssessmentDetailOther assessmentDetailOther;
    AssessmentExtraDetails assessmentExtraDetails = new AssessmentExtraDetails();
    AssessmentProgressbar assessmentProgressbar = new AssessmentProgressbar();
    boolean isAssessmentIssues = false;
    private AssessmentFirebaseClass assessmentFirebaseClass;
    ActiveGame activeGame;
    public UserEventAssessmentData userEventAssessmentData;
    String courseColId, assessmentId, courseId, mappedSurveyId;
    private boolean isMultipleCplEnable = false;
    private long isCplId;

    private long startTime = 0;
    private PlayResponse playResponse;
    private boolean isAutoStartAssessment;
    public MutableLiveData<AssessmentDetailOther> assessmentDetailOtherMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<AssessmentProgressbar> assessmentProgressbarMutableLiveData = new MutableLiveData<>();
    private String showPastDeadlineModulesOnLandingPage = null;
    private ArrayList<DTOQuestions> questionsArrayList;
    private boolean isAssessmentCompleted;
    private int downloadQuestionNo = 0;
    private int incrementDownloadQuestionNO = 0;
    private int totalQuestion = 0;
    private int noofTry = 0;
    int totalTime = 0;
    List<String> mediaList = new ArrayList<>();
    int nQuestionWrong = 0;
    int nQuestionCorrect = 0;

    public AssessmentDetailRepo(Bundle dataBundle) {

        this.dataBundle = dataBundle;
        activeUser = OustAppState.getInstance().getActiveUser();

        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            Log.i("AssessmentLogic", "User data available");
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            this.activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        }

        isAutoStartAssessment = false;

        context = OustSdkApplication.getContext();
        OustAppState.getInstance().setAssessmentGame(true);
        OustAppState.getInstance().setShouldLoadGameInfoFrom_LearningNode(false);

        if (dataBundle != null) {
            assessmentId = dataBundle.getString("assessmentId");
            courseId = dataBundle.getString("courseId");
            if (courseId != null && !courseId.isEmpty() && !courseId.equalsIgnoreCase("null")) {
                assessmentExtraDetails.setCourseId(courseId);

                mappedSurveyId = dataBundle.getString("mappedSurveyId");
                if (mappedSurveyId != null && !mappedSurveyId.isEmpty() && !mappedSurveyId.equalsIgnoreCase("null")) {
                    Log.d(TAG, "AssessmentDetailRepo: mappedSurveyId found");
                    assessmentExtraDetails.setMappedSurveyId(Long.parseLong(mappedSurveyId));
                }
            }

            courseColId = dataBundle.getString("courseColnId");
            isCplId = dataBundle.getLong("currentCplId");
            isMultipleCplEnable = dataBundle.getBoolean("isMultipleCplModule", false);

            assessmentExtraDetails.setCplModule(dataBundle.getBoolean("isCplModule", false));
            assessmentExtraDetails.setContainCertificate(dataBundle.getBoolean("containCertificate", false));
            assessmentExtraDetails.setFromCourse(dataBundle.getBoolean("IS_FROM_COURSE", false));
            assessmentExtraDetails.setFeedComment(dataBundle.getBoolean("FeedComment"));
            assessmentExtraDetails.setComingFormOldLandingPage(dataBundle.getBoolean("comingFormOldLandingPage", false));
            assessmentExtraDetails.setEvent(dataBundle.getBoolean("isEventLaunch", false));
            assessmentExtraDetails.setEventId(dataBundle.getInt("eventId", 0));
            isAutoStartAssessment = dataBundle.getBoolean("autoStartAssessment", false);

            if (dataBundle.get("nAttemptCount") != null) {
                assessmentExtraDetails.setnAttemptCount((int) dataBundle.getLong("nAttemptCount", 0));
                Log.d(TAG, "AssessmentDetailRepo: attemptCount: " + assessmentExtraDetails.getnAttemptCount());
            }

            if (dataBundle.get("isFromWorkDairy") != null) {
                assessmentExtraDetails.setFromWorkDairy(dataBundle.getBoolean("isFromWorkDairy", false));
            }
        }

        setGame(activeUser);
        activeGame.setIsLpGame(false);

        if ((assessmentId != null) && (!assessmentId.isEmpty())) {
            assessmentExtraDetails.setComingFromBranchIO(true);
            OustPreferences.save("current_assessmentId", assessmentId);
        }

        userEventAssessmentData = new UserEventAssessmentData();
        assert assessmentId != null;
        userEventAssessmentData.setAssessmentId(Long.parseLong(assessmentId));
        userEventAssessmentData.setEventId(assessmentExtraDetails.getEventId());

        fetchAssessmentsFromFirebase(assessmentId);
    }

    public void setGame(ActiveUser activeUser) {
        Log.d(TAG, "setGame: ");
        try {

            if (activeGame == null) {
                activeGame = new ActiveGame();
            }
            activeGame.setGameid("");
            activeGame.setGames(activeUser.getGames());
            activeGame.setStudentid(activeUser.getStudentid());
            activeGame.setChallengerid(activeUser.getStudentid());
            activeGame.setChallengerDisplayName(activeUser.getUserDisplayName());
            activeGame.setChallengerAvatar(activeUser.getAvatar());
            activeGame.setOpponentAvatar(OustSdkTools.getMysteryAvatar());
            activeGame.setOpponentid("Mystery");
            activeGame.setOpponentDisplayName("Mystery");
            activeGame.setGameType(GameType.MYSTERY);
            activeGame.setGuestUser(false);
            activeGame.setRematch(false);
            activeGame.setGrade(activeUser.getGrade());
            activeGame.setGroupId("");
            activeGame.setSubject(activeUser.getSubject());
            activeGame.setTopic(activeUser.getTopic());
            activeGame.setLevel(activeUser.getLevel());
            activeGame.setLevelPercentage(activeUser.getLevelPercentage());
            activeGame.setWins(activeUser.getWins());
            activeGame.setModuleId(activeUser.getModuleId());
            activeGame.setModuleName(activeUser.getModuleName());

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void fetchAssessmentsFromFirebase(String assessmentId) {
        try {
            String assessmentBaseurl = OustSdkApplication.getContext().getResources().getString(R.string.get_Assessment_details);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            assessmentBaseurl = assessmentBaseurl.replace("{org-id}", "" + tenantName);
            assessmentBaseurl = assessmentBaseurl.replace("{assessmentId}", "" + assessmentId);
            assessmentBaseurl = assessmentBaseurl.replace("{userId}", "" + activeUser.getStudentid());

            assessmentBaseurl = HttpManager.getAbsoluteUrl(assessmentBaseurl);

            Log.d(TAG, "fetchAssessmentsFromFirebase: " + assessmentBaseurl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, assessmentBaseurl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "fetchAssessmentsFromFirebase onResponse: " + response.toString());
                    Map<String, Object> assessmentMap = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assessmentMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        gotAssessmentFromFirebase(null);
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (null != assessmentMap) {
                        extractAssessmentData(assessmentMap);
                    } else {
                        gotAssessmentFromFirebase(null);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    gotAssessmentFromFirebase(null);
                }
            });
        } catch (Exception e) {
            gotAssessmentFromFirebase(null);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractAssessmentData(Map<String, Object> assessmentMap) {
        AssessmentFirebaseClass assessmentFirebaseClass = new AssessmentFirebaseClass();
        //handle object conversion to avoid multiple line implementation
        try {
            if (assessmentMap.get("active") != null) {
                assessmentFirebaseClass.setActive((boolean) assessmentMap.get("active"));
            }
            if (assessmentMap.get("reattemptAllowed") != null) {
                assessmentFirebaseClass.setReattemptAllowed((boolean) assessmentMap.get("reattemptAllowed"));
                assessmentExtraDetails.setReAttemptAllowed((boolean) assessmentMap.get("reattemptAllowed"));
            }
            if (assessmentMap.get("showQuestionsOnCompletion") != null) {
                assessmentFirebaseClass.setShowQuestionsOnCompletion((boolean) assessmentMap.get("showQuestionsOnCompletion"));
                OustPreferences.saveAppInstallVariable("showQuestionsOnCompletion", assessmentFirebaseClass.isShowQuestionsOnCompletion());
            }
            if (assessmentMap.get("cplId") != null) {
                long cplId = OustSdkTools.newConvertToLong(assessmentMap.get("cplId"));
                OustPreferences.saveTimeForNotification("cplId_assessment", cplId);
            } else {
                OustPreferences.saveTimeForNotification("cplId_assessment", 0);
            }
            if (assessmentMap.get("assessmentId") != null) {
                assessmentFirebaseClass.setAsssessemntId(OustSdkTools.newConvertToLong(assessmentMap.get("assessmentId")));
            }
            if (assessmentMap.get("contentDuration") != null) {
                assessmentFirebaseClass.setTotalTime(OustSdkTools.newConvertToLong(assessmentMap.get("contentDuration")));
            }

            if (assessmentMap.get("examMode") != null) {
                //OustAppState.getInstance().getAssessmentFirebaseClass().setExamMode((boolean) assessmentMap.get("examMode"));
                assessmentFirebaseClass.setExamMode((boolean) assessmentMap.get("examMode"));
            }

            if (assessmentMap.get("duration") != null) {
                // OustAppState.getInstance().getAssessmentFirebaseClass().setDuration((long) assessmentMap.get("duration"));
                assessmentFirebaseClass.setDuration(OustSdkTools.newConvertToLong(assessmentMap.get("duration")));
            }
            if (assessmentMap.get("isTTSEnabled") != null) {
                assessmentFirebaseClass.setTTSEnabled((boolean) assessmentMap.get("isTTSEnabled"));
            }
            if (assessmentMap.get("backgroundAudioForAssessment") != null) {
                assessmentFirebaseClass.setBackgroundAudioForAssessment((boolean) assessmentMap.get("backgroundAudioForAssessment"));
            }
            if (assessmentMap.get("rules") != null) {
                assessmentFirebaseClass.setRules((List<String>) assessmentMap.get("rules"));
            }
            if (assessmentMap.get("showAssessmentRemark") != null) {
                assessmentFirebaseClass.setShowAssessmentRemark((boolean) assessmentMap.get("showAssessmentRemark"));
            } else {
                assessmentFirebaseClass.setShowAssessmentRemark(false);
            }

            if (assessmentMap.get("recurring") != null) {
                assessmentFirebaseClass.setRecurring((boolean) assessmentMap.get("recurring"));
            } else {
                assessmentFirebaseClass.setRecurring(false);
            }

            if (assessmentMap.get("frequency") != null) {
                assessmentFirebaseClass.setFrequency(OustSdkTools.newConvertToLong(assessmentMap.get("frequency")));
            } else {
                assessmentFirebaseClass.setFrequency(0);
            }
            if (assessmentMap.get("showAssessmentResultScore") != null) {
                assessmentFirebaseClass.setShowAssessmentResultScore((boolean) assessmentMap.get("showAssessmentResultScore"));
            } else {
                assessmentFirebaseClass.setShowAssessmentResultScore(false);
            }
            if (assessmentMap.get("resultDataList") != null) {
                assessmentFirebaseClass.initAssessmentResultBadges((ArrayList<Object>) assessmentMap.get("resultDataList"));
            }
            if (assessmentMap.get("certificate") != null) {
                assessmentFirebaseClass.setCertificate((boolean) assessmentMap.get("certificate"));
            }
            if (assessmentMap.get("enrolledCount") != null) {
                assessmentFirebaseClass.setEnrolledCount(OustSdkTools.newConvertToLong(assessmentMap.get("enrolledCount")));
            }
            if (assessmentMap.get("secureSessionOn") != null) {
                assessmentFirebaseClass.setSecureSessionOn((boolean) assessmentMap.get("secureSessionOn"));
            } else {
                assessmentFirebaseClass.setSecureSessionOn(true);
            }
            if (assessmentMap.get("isSecureAssessment") != null) {
                assessmentFirebaseClass.setSecureAssessment((boolean) assessmentMap.get("isSecureAssessment"));
            }
            if (assessmentMap.get("numQuestions") != null) {
                assessmentFirebaseClass.setNumQuestions(OustSdkTools.newConvertToLong(assessmentMap.get("numQuestions")));
                userEventAssessmentData.setnTotalQuestions((int) OustSdkTools.newConvertToLong(assessmentMap.get("numQuestions")));
            }
            if (assessmentMap.get("banner") != null) {
                assessmentFirebaseClass.setBanner((String) assessmentMap.get("banner"));
            }
            if (assessmentMap.get("bgImg") != null) {
                assessmentFirebaseClass.setBgImg((String) assessmentMap.get("bgImg"));
            }
            if (assessmentMap.get("description") != null) {
                assessmentFirebaseClass.setDescription((String) assessmentMap.get("description"));
            }
            if (assessmentMap.get("addedOn") != null) {
                assessmentFirebaseClass.setAddedOn((String) assessmentMap.get("addedOn"));
            }
            if (assessmentMap.get("endDate") != null) {
                assessmentFirebaseClass.setEnddate((String) assessmentMap.get("endDate"));
            }
            if (assessmentMap.get("name") != null) {
                assessmentFirebaseClass.setName((String) assessmentMap.get("name"));
            }
            if (assessmentMap.get("passcode") != null) {
                assessmentFirebaseClass.setPasscode((String) assessmentMap.get("passcode"));
            }
            if (assessmentMap.get("scope") != null) {
                assessmentFirebaseClass.setScope((String) assessmentMap.get("scope"));
            }
            if (assessmentMap.get("questionXp") != null) {
                assessmentFirebaseClass.setQuestionXp(OustSdkTools.newConvertToLong(assessmentMap.get("questionXp")));
            }
            if (assessmentMap.get("rewardOC") != null) {
                assessmentFirebaseClass.setRewardOc(OustSdkTools.newConvertToLong(assessmentMap.get("rewardOC")));
            }
            if (assessmentMap.get("disablePartialMarking") != null) {
                assessmentFirebaseClass.setDisablePartialMarking((boolean) assessmentMap.get("disablePartialMarking"));
            } else {
                assessmentFirebaseClass.setDisablePartialMarking(true);
            }
            if (assessmentMap.get("minPassCorrectAnswer") != null) {
                assessmentFirebaseClass.setMinPassCorrectAnswer(OustSdkTools.newConvertToLong(assessmentMap.get("minPassCorrectAnswer")));
            }
            if (assessmentMap.get("passPercentage") != null) {
                assessmentFirebaseClass.setPassPercentage(OustSdkTools.newConvertToLong(assessmentMap.get("passPercentage")));
            }
            try {
                if (assessmentMap.get("type") != null) {
                    assessmentFirebaseClass.setAssessmentType(AssessmentType.valueOf((String) assessmentMap.get("type")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            if (assessmentMap.get("startDate") != null) {
                try {
                    startTime = Long.parseLong((String) Objects.requireNonNull(assessmentMap.get("startDate")));
                } catch (Exception e) {
                    startTime = 0;
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                assessmentFirebaseClass.setStartdate((String) assessmentMap.get("startDate"));
            }
            if (assessmentMap.get("logo") != null) {
                assessmentFirebaseClass.setLogo((String) assessmentMap.get("logo"));
            }
            if (assessmentMap.get("module") != null) {
                Map<String, Object> language = (Map<String, Object>) assessmentMap.get("module");
                Map<String, String> moduleMap = (Map<String, String>) language.get("language");
                assessmentFirebaseClass.setModulesMap(moduleMap);
            }
            if ((courseColId != null) && (!courseColId.isEmpty())) {
                assessmentFirebaseClass.setHideLeaderboard(true);
            } else {
                if (assessmentMap.get("hideLeaderboard") != null) {
                    assessmentFirebaseClass.setHideLeaderboard((boolean) assessmentMap.get("hideLeaderboard"));
                } else {
                    assessmentFirebaseClass.setHideLeaderboard(true);
                }
            }
            if (assessmentMap.get("showPercentage") != null) {
                assessmentFirebaseClass.setShowPercentage((boolean) assessmentMap.get("showPercentage"));
            }
            if (assessmentMap.get("otpVerification") != null) {
                assessmentFirebaseClass.setOtpVerification((boolean) assessmentMap.get("otpVerification"));
            }
            if (assessmentMap.get("resultMessage") != null) {
                assessmentFirebaseClass.setResultMessage((String) assessmentMap.get("resultMessage"));
            }
            String status;
            try {
                if (assessmentMap.get("state") != null) {
                    status = (String) assessmentMap.get("state");
                    assessmentFirebaseClass.setAssessmentState(status);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            if (assessmentMap.get("assessmentOverMessage") != null) {
                assessmentFirebaseClass.setAssessmentOverMessage((String) assessmentMap.get("assessmentOverMessage"));
            }
            if (assessmentMap.get("resumeSameQuestion") != null) {
                assessmentFirebaseClass.setResumeSameQuestion((boolean) assessmentMap.get("resumeSameQuestion"));
            }

            if (assessmentMap.get("timePenaltyDisabled") != null) {
                assessmentFirebaseClass.setTimePenaltyDisabled((boolean) assessmentMap.get("timePenaltyDisabled"));
            }

            if (assessmentMap.get("assessmentMedia") != null) {
                List<CourseCardMedia> assessmentMediaList = new ArrayList<>();
                ArrayList<Object> assessmentMediaArrayList = (ArrayList<Object>) assessmentMap.get("assessmentMedia");
                assert assessmentMediaArrayList != null;
                for (int i = 0; i < assessmentMediaArrayList.size(); i++) {
                    CourseCardMedia courseCardMedia = new CourseCardMedia();
                    Map<Object, Object> mediaMap = (Map<Object, Object>) assessmentMediaArrayList.get(i);
                    if (mediaMap.get("mediaType") != null)
                        courseCardMedia.setMediaType((String) mediaMap.get("mediaType"));
                    if (mediaMap.get("mediaKey") != null) {
                        courseCardMedia.setData((String) mediaMap.get("mediaKey"));
                    } else if (mediaMap.get("mediaName") != null) {
                        courseCardMedia.setData((String) mediaMap.get("mediaName"));
                    }
                    assessmentMediaList.add(courseCardMedia);
                }
                assessmentFirebaseClass.setAssessmentMediaList(assessmentMediaList);
            }

            if (assessmentMap.get("noOfAttemptAllowedToPass") != null) {
                assessmentFirebaseClass.setNoOfAttemptAllowedToPass(OustSdkTools.newConvertToLong(assessmentMap.get("noOfAttemptAllowedToPass")));
                assessmentExtraDetails.setnAttemptAllowedToPass((int) assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
            }
            if (assessmentMap.get("courseAssociated") != null) {
                assessmentExtraDetails.setCourseAssociated((boolean) assessmentMap.get("courseAssociated"));
                assessmentFirebaseClass.setCourseAssociated((boolean) assessmentMap.get("courseAssociated"));
            } else {
                assessmentFirebaseClass.setCourseAssociated(false);
            }

            if (assessmentMap.get("surveyAssociated") != null) {
                assessmentFirebaseClass.setSurveyAssociated((boolean) assessmentMap.get("surveyAssociated"));
                assessmentExtraDetails.setSurveyAssociated((boolean) assessmentMap.get("surveyAssociated"));
            } else {
                assessmentFirebaseClass.setSurveyAssociated(false);
                assessmentExtraDetails.setSurveyAssociated(false);
            }

            if (assessmentMap.get("surveyMandatory") != null) {
                assessmentFirebaseClass.setSurveyMandatory((boolean) assessmentMap.get("surveyMandatory"));
                assessmentExtraDetails.setSurveyMandatory((boolean) assessmentMap.get("surveyMandatory"));
            } else {
                assessmentFirebaseClass.setSurveyMandatory(false);
                assessmentExtraDetails.setSurveyMandatory(false);
            }

            if (assessmentMap.get("mappedCourseId") != null) {
                assessmentFirebaseClass.setMappedCourseId(OustSdkTools.newConvertToLong(assessmentMap.get("mappedCourseId")));
            }

            if (assessmentMap.get("mappedSurveyId") != null) {
                try {
                    long id = OustSdkTools.newConvertToLong(assessmentMap.get("mappedSurveyId"));
                    if (id > 0) {
                        assessmentFirebaseClass.setMappedSurveyId(OustSdkTools.newConvertToLong(assessmentMap.get("mappedSurveyId")));
                        assessmentExtraDetails.setMappedSurveyId(OustSdkTools.newConvertToLong(assessmentMap.get("mappedSurveyId")));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            if (assessmentMap.get("completionDeadline") != null) {
                assessmentFirebaseClass.setCompletionDeadline(OustSdkTools.newConvertToLong(assessmentMap.get("completionDeadline")));
            }

            if (assessmentMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                assessmentFirebaseClass.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.newConvertToLong(assessmentMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
            }
            if (assessmentMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                assessmentFirebaseClass.setShowPastDeadlineModulesOnLandingPage((boolean) assessmentMap.get("showPastDeadlineModulesOnLandingPage"));
                boolean showPastDeadlineModulesOnLandingPageValue = (boolean) assessmentMap.get("showPastDeadlineModulesOnLandingPage");
                if (showPastDeadlineModulesOnLandingPageValue) {
                    showPastDeadlineModulesOnLandingPage = "true";
                } else {
                    showPastDeadlineModulesOnLandingPage = "false";
                }
            }
            if (assessmentMap.get("conditionalFlow") != null) {
                assessmentFirebaseClass.setConditionalFlow((boolean) assessmentMap.get("conditionalFlow"));
            }
            if (assessmentMap.get("conditionalFlowURL") != null) {
                assessmentFirebaseClass.setConditionalFlowURL((String) assessmentMap.get("conditionalFlowURL"));
            }
            if (assessmentMap.get("conditionalFlowUrl") != null) {
                assessmentFirebaseClass.setConditionalFlowURL((String) assessmentMap.get("conditionalFlowUrl"));
            }

            OustAppState.getInstance().setAssessmentFirebaseClass(assessmentFirebaseClass);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        try {
            if (assessmentMap.get("addedOn") != null) {
                assessmentFirebaseClass.setAddedOn((String) assessmentMap.get("addedOn"));
            }

            if (assessmentMap.get("completionDeadline") != null) {
                assessmentFirebaseClass.setContentCompletionDeadline(OustSdkTools.newConvertToLong(assessmentMap.get("completionDeadline")));
            }

            if (assessmentMap.get("completionDate") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setCompletionDate((String) assessmentMap.get("completionDate"));
                assessmentFirebaseClass.setCompletionDate((String) assessmentMap.get("completionDate"));

                if (assessmentFirebaseClass.getFrequency() > 0) {
                    long nextDate = OustSdkTools.newConvertToLong(assessmentFirebaseClass.getCompletionDate()) + (assessmentFirebaseClass.getFrequency() * 86400000);
                    if (System.currentTimeMillis() < nextDate) {
                        assessmentFirebaseClass.setRecurring(false);
                        OustAppState.getInstance().getAssessmentFirebaseClass().setRecurring(false);
                    } else {
                        assessmentFirebaseClass.setRecurring(true);
                        OustAppState.getInstance().getAssessmentFirebaseClass().setRecurring(true);
                    }

                } else {
                    isAssessmentCompleted = assessmentFirebaseClass.getFrequency() != 0;
                }
            } else {
                isAssessmentIssues = true;
            }
            if (assessmentMap.get("contentPlayListId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListId")));
            }
            if (assessmentMap.get("contentPlayListSlotId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListSlotId")));
            }
            if (assessmentMap.get("contentPlayListSlotItemId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotItemId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListSlotItemId")));
            }

            if (assessmentMap.get("enrolled") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolled((boolean) assessmentMap.get("enrolled"));
                assessmentFirebaseClass.setEnrolled((boolean) assessmentMap.get("enrolled"));
            }

            if (assessmentMap.get("enrolledDate") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolledTime(OustSdkTools.newConvertToLong(assessmentMap.get("enrolledDate")));
                assessmentFirebaseClass.setEnrolledTime(OustSdkTools.convertToLong(assessmentMap.get("enrolledDate")));
            }

            if (assessmentMap.get("contentPlayListId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListId")));
            }

            if (OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId() == 0 && isCplId != 0) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListId(isCplId);
            }
            if (assessmentMap.get("contentPlayListSlotId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListSlotId")));
            }
            if (assessmentMap.get("contentPlayListSlotItemId") != null) {
                OustAppState.getInstance().getAssessmentFirebaseClass().setContentPlayListSlotItemId(OustSdkTools.newConvertToLong(assessmentMap.get("contentPlayListSlotItemId")));
            }

            if (assessmentMap.get("score") != null) {
                Object o3 = assessmentMap.get("score");
                assert o3 != null;
                if (o3.getClass().equals(String.class)) {
                    int n1 = Integer.parseInt((String) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);

                } else if (o3.getClass().equals(Long.class)) {
                    int n1 = (int) ((long) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);

                } else if (o3.getClass().equals(Double.class)) {
                    int n1 = (int) ((double) o3);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setScore(n1);
                    userEventAssessmentData.setScore(n1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        gotAssessmentFromFirebase(assessmentFirebaseClass);
    }

    public void gotAssessmentFromFirebase(AssessmentFirebaseClass assessmentFirebaseClass) {
        try {
            if (assessmentFirebaseClass != null) {
                this.assessmentFirebaseClass = assessmentFirebaseClass;
//                OustAppState.getInstance().setAssessmentFirebaseClass(assessmentFirebaseClass);
                activeUser = OustAppState.getInstance().getActiveUser();
                gotAssessmentCompletionStatus();
            } else {
                assessmentDetailModelMutableLiveData.postValue(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotAssessmentCompletionStatus() {
        try {
            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate() != null) &&
                    (!OustAppState.getInstance().getAssessmentFirebaseClass().getCompletionDate().isEmpty())) {
                assessmentExtraDetails.setEnrolled(true);
                OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolled(true);
            }
            setAssessmentData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAssessmentData() {
        Log.d(TAG, "setAssessmentData: ");
        if (assessmentDetailModel == null) {
            assessmentDetailModel = new AssessmentDetailModel();
        }
        try {
            if (assessmentFirebaseClass != null) {
                OustPreferences.saveAppInstallVariable("AssessmentDataLoaded", true);

                assessmentDetailModel.setAssessmentId(assessmentFirebaseClass.getAsssessemntId());
                assessmentDetailModel.setShowAssessmentResultRemark(assessmentFirebaseClass.isShowAssessmentRemark());
                assessmentDetailModel.setShowAssessmentResultScore(assessmentFirebaseClass.isShowAssessmentResultScore());
                assessmentDetailModel.setHideLeaderBoard(assessmentFirebaseClass.isHideLeaderboard());

                if (startTime != 0) {
                    assessmentDetailModel.setStartAssessment(startTime);
                }
              /*  if ((assessmentFirebaseClass.getLogo() != null) && (!assessmentFirebaseClass.getLogo().isEmpty())) {
                    assessmentDetailModel.setImage(assessmentFirebaseClass.getLogo());
                } else*/
                if ((assessmentFirebaseClass.getBanner() != null) && (!assessmentFirebaseClass.getBanner().isEmpty())) {
                    assessmentDetailModel.setImage(assessmentFirebaseClass.getBanner());
                }

                if ((assessmentFirebaseClass.getEnrolledCount() != 0)) {
                    assessmentDetailModel.setEnrolledCount(assessmentFirebaseClass.getEnrolledCount());
                }

                assessmentDetailModel.setIsEnrolled(assessmentFirebaseClass.isEnrolled());

                if ((assessmentFirebaseClass.getNoOfAttemptAllowedToPass() != 0)) {
                    assessmentDetailModel.setNoOfAttemptAllowed(assessmentFirebaseClass.getNoOfAttemptAllowedToPass());
                }

                assessmentDetailModel.setReattemptAllowed(assessmentFirebaseClass.isReattemptAllowed());


                if (assessmentFirebaseClass.getTotalTime() != 0) {
                    String totalTime = OustSdkTools.getTime(assessmentFirebaseClass.getTotalTime());
                    assessmentDetailModel.setTime(totalTime);
                } else {
                    assessmentDetailModel.setTime("1");
                }

                String question_text = assessmentFirebaseClass.getNumQuestions() + "";

                if (assessmentFirebaseClass.getNumQuestions() > 1) {
                    question_text = assessmentFirebaseClass.getNumQuestions() + "";
                }

                assessmentDetailModel.setQuestions(question_text);
                assessmentDetailModel.setTotalScore(String.valueOf(assessmentFirebaseClass.getNumQuestions() * assessmentFirebaseClass.getQuestionXp()));

                if (assessmentFirebaseClass.getRewardOc() != 0) {
                    String coins_text = assessmentFirebaseClass.getRewardOc() + "";
                    assessmentDetailModel.setCoins(coins_text);
                }

                assessmentDetailModel.setCertificate(assessmentFirebaseClass.isCertificate());

                if (assessmentFirebaseClass.getAddedOn() != null && !assessmentFirebaseClass.getAddedOn().isEmpty()) {
                    String distributionDate = dateFormat(assessmentFirebaseClass.getAddedOn());
                    assessmentDetailModel.setDistributionTime(distributionDate);
                }

                if (assessmentFirebaseClass.getName() != null) {
                    assessmentDetailModel.setTitle(assessmentFirebaseClass.getName());

                }

                if (assessmentFirebaseClass.getContentCompletionDeadline() != 0) {
                    String deadLine = dateFormatWithTime("" + assessmentFirebaseClass.getContentCompletionDeadline());
                    assessmentDetailModel.setDeadLine(assessmentFirebaseClass.getContentCompletionDeadline());
                    assessmentDetailModel.setEndTime(deadLine);
                }
                if (assessmentFirebaseClass.getDescription() != null) {
                    assessmentDetailModel.setDescription(assessmentFirebaseClass.getDescription());
                }

                assessmentDetailModel.setRecurring(assessmentFirebaseClass.isRecurring());

                if (assessmentFirebaseClass.getAssessmentState() != null &&
                        assessmentFirebaseClass.getAssessmentState().equalsIgnoreCase(AssessmentState.OVER)) {
                    assessmentDetailModel.setStatus("Over");
                    assessmentDetailModel.setButtonEnabled(false);

                    if (assessmentFirebaseClass != null && assessmentFirebaseClass.getAssessmentOverMessage() != null
                            && (!assessmentFirebaseClass.getAssessmentOverMessage().isEmpty())) {
                        assessmentDetailModel.setDescription(assessmentFirebaseClass.getAssessmentOverMessage());
                    } else {
                        assessmentDetailModel.setDescription(context.getResources().getString(R.string.assessment_no_active_error));
                    }
                } else {
                    checkForSavedAssessment(activeUser, assessmentFirebaseClass);
                }

                assessmentDetailModel.setAutoStartAssessment(isAutoStartAssessment);
                if (activeGame != null) {
                    assessmentDetailModel.setActiveGame(activeGame);
                }
                if (playResponse != null) {
                    assessmentDetailModel.setPlayResponse(playResponse);
                }

                if (assessmentPlayResponse != null) {
                    assessmentDetailModel.setAssessmentPlayResponse(assessmentPlayResponse);
                }
                assessmentDetailModel.setShowPastDeadlineModulesOnLandingPage(showPastDeadlineModulesOnLandingPage);
                assessmentDetailModelMutableLiveData.postValue(assessmentDetailModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForSavedAssessment(ActiveUser activeUser, AssessmentFirebaseClass assessmentFirebaseClass) {
        Log.d(TAG, "checkForSavedAssessment: " + assessmentId);
        try {
            assessmentExtraDetails.setnCorrect(0);
            assessmentExtraDetails.setnWrong(0);

            String assessmentUserProgressUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_user_user_assessment_progress);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{org-id}", "" + tenantName);
            assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{assessmentId}", "" + assessmentFirebaseClass.getAsssessemntId());
            assessmentUserProgressUrl = assessmentUserProgressUrl.replace("{userId}", "" + activeUser.getStudentid());

            assessmentUserProgressUrl = HttpManager.getAbsoluteUrl(assessmentUserProgressUrl);

            Log.d(TAG, "checkForSavedAssessment: " + assessmentUserProgressUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, assessmentUserProgressUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "fetchAssessmentsFromFirebase onResponse: " + response.toString());
                    Map<String, Object> assessmentProgressMainMap = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assessmentProgressMainMap = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (assessmentProgressMainMap != null) {
                        extractAssessmentPlayData(assessmentProgressMainMap, assessmentFirebaseClass);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    failDueToNetwork();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractAssessmentPlayData(Map<String, Object> assessmentProgressMainMap, AssessmentFirebaseClass assessmentFirebaseClass) {
        try {
            int totalQues = 0;
            AssessmentPlayResponse assessmentPlayResponse = new AssessmentPlayResponse();
            if (assessmentProgressMainMap != null) {
                if (assessmentProgressMainMap.get("userFinalScore") != null) {
                    if (Objects.requireNonNull(assessmentProgressMainMap.get("userFinalScore")).getClass() == Long.class) {
                        assessmentPlayResponse.setChallengerFinalScore((Long) assessmentProgressMainMap.get("userFinalScore"));
                    } else {
                        assessmentPlayResponse.setChallengerFinalScore(OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("userFinalScore")));
                    }
                }

                String gameId = "0";
                if (assessmentProgressMainMap.get("gameId") != null) {
                    if (Objects.requireNonNull(assessmentProgressMainMap.get("gameId")).getClass() == Long.class) {
                        gameId = String.valueOf(assessmentProgressMainMap.get("gameId"));
                    } else if (Objects.requireNonNull(assessmentProgressMainMap.get("gameId")).getClass() == Integer.class) {
                        gameId = String.valueOf(assessmentProgressMainMap.get("gameId"));
                    } else {
                        gameId = (String) assessmentProgressMainMap.get("gameId");
                    }
                    assessmentPlayResponse.setGameId(gameId);
                }

                if (assessmentProgressMainMap.get("currentQuestionId") != null) {
                    assessmentPlayResponse.setCurrentQuestionId((int) OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("currentQuestionId")));
                }

                if (assessmentProgressMainMap.get("attemptCount") != null) {
                    Log.d(TAG, "extractLandingData1: attemptCount:" + assessmentProgressMainMap.get("attemptCount"));
                    long attemptCount = OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("attemptCount"));
                    if (assessmentExtraDetails.getnAttemptCount() <= ((int) attemptCount)) {
                        OustAppState.getInstance().getAssessmentFirebaseClass().setAttemptCount(attemptCount);
                        assessmentExtraDetails.setnAttemptCount((int) attemptCount);
                        if (assessmentDetailModel == null) {
                            assessmentDetailModel = new AssessmentDetailModel();
                        }
                        assessmentDetailModel.setNoOfAttemptLeft(attemptCount);
                    }
                }

                if (assessmentProgressMainMap.get("passed") != null) {
                    if (!isAssessmentCompleted) {
                        isAssessmentCompleted = (boolean) assessmentProgressMainMap.get("passed");
                    }
                }

                if (assessmentProgressMainMap.get("studentId") != null) {
                    assessmentPlayResponse.setStudentId((String) assessmentProgressMainMap.get("studentId"));
                }
                if (assessmentProgressMainMap.get("commentMediaUploadedPath") != null) {
                    assessmentPlayResponse.setCommentMediaUploadedPath((String) assessmentProgressMainMap.get("commentMediaUploadedPath"));
                }
                try {
                    if (assessmentProgressMainMap.get("totalQuestion") != null) {
                        if (Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")).getClass() == Long.class) {
                            totalQues = (int) ((long) assessmentProgressMainMap.get("totalQuestion"));
                        } else if (Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")).getClass() == Integer.class) {
                            totalQues = (int) assessmentProgressMainMap.get("totalQuestion");
                        } else {
                            totalQues = Integer.parseInt((String) Objects.requireNonNull(assessmentProgressMainMap.get("totalQuestion")));
                        }
                        if (totalQues == 0 && gameId != null && !gameId.isEmpty() && !gameId.equalsIgnoreCase("0")) {
                            if (assessmentFirebaseClass != null && assessmentFirebaseClass.getNumQuestions() != 0) {
                                totalQues = (int) assessmentFirebaseClass.getNumQuestions();
                                assessmentPlayResponse.setTotalQuestion((int) assessmentFirebaseClass.getNumQuestions());
                            }
                            assessmentPlayResponse.setTotalQuestion(totalQues);
                        } else {
                            assessmentPlayResponse.setTotalQuestion(totalQues);
                        }
                    }
                } catch (Exception e) {
                    assessmentPlayResponse.setTotalQuestion(totalQues);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                if (assessmentProgressMainMap.get("winner") != null) {
                    assessmentPlayResponse.setWinner((String) assessmentProgressMainMap.get("winner"));
                }
                if (assessmentProgressMainMap.get("end_time") != null) {
                    assessmentPlayResponse.setEndTime((String) assessmentProgressMainMap.get("end_time"));
                }
                if (assessmentProgressMainMap.get("start_time") != null) {
                    assessmentPlayResponse.setStartTime((String) assessmentProgressMainMap.get("start_time"));
                }
                if (assessmentProgressMainMap.get("attemptCount") != null) {
                    assessmentPlayResponse.setAttemptCount(OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("attemptCount")));
                }
                if (assessmentProgressMainMap.get("resumeTime") != null) {
                    String resumeTime = (String) assessmentProgressMainMap.get("resumeTime");
                    if (resumeTime != null && !resumeTime.isEmpty()) {
                        if (Objects.requireNonNull(assessmentProgressMainMap.get("resumeTime")).getClass() == Long.class) {
                            assessmentPlayResponse.setResumeTime(String.valueOf(assessmentProgressMainMap.get("resumeTime")));
                        } else {
                            assessmentPlayResponse.setResumeTime((String) assessmentProgressMainMap.get("resumeTime"));
                        }
                    } else {
                        assessmentPlayResponse.setResumeTime("0");
                    }
                }
                if (assessmentProgressMainMap.get("challengerid") != null) {
                    assessmentPlayResponse.setChallengerid((String) assessmentProgressMainMap.get("challengerid"));
                }
                if (assessmentProgressMainMap.get("opponentid") != null) {
                    assessmentPlayResponse.setOpponentid((String) assessmentProgressMainMap.get("opponentid"));
                }
                String status = null;
                try {
                    if (assessmentProgressMainMap.get("assessmentState") != null) {
                        status = (String) assessmentProgressMainMap.get("assessmentState");
                        assessmentPlayResponse.setAssessmentState(status);
                        this.assessmentFirebaseClass.setEnrolled(true);
                        OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                try {
                    int currentQuestionIndex;
                    if (assessmentProgressMainMap.get("questionIndex") != null) {
                        if (Objects.requireNonNull(assessmentProgressMainMap.get("questionIndex")).getClass() == Long.class) {
                            currentQuestionIndex = (int) (long) assessmentProgressMainMap.get("questionIndex");
                        } else {
                            currentQuestionIndex = Integer.parseInt((String) Objects.requireNonNull(assessmentProgressMainMap.get("questionIndex")));
                        }
                        if (assessmentExtraDetails != null && assessmentExtraDetails.isFromWorkDairy() && status != null && status.equalsIgnoreCase("SUBMITTED")) {
                            assessmentPlayResponse.setQuestionIndex(0);
                        } else {
                            if (currentQuestionIndex == 0) {
                                assessmentPlayResponse.setQuestionIndex(currentQuestionIndex);
                            } else {
                                assessmentPlayResponse.setQuestionIndex(currentQuestionIndex - 1);
                            }
                        }
                    }
                } catch (Exception e) {
                    assessmentPlayResponse.setQuestionIndex(0);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                if (assessmentProgressMainMap.get("nQuestionWrong") != null) {
                    int n1 = (int) (OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("nQuestionWrong")));
                    OustAppState.getInstance().getAssessmentFirebaseClass().setWrongQues(n1);
                    userEventAssessmentData.setnQuestionWrong(n1);
                    nQuestionWrong = n1;
                }
                if (assessmentProgressMainMap.get("nQuestionSkipped") != null) {
                    int n1 = (int) (OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("nQuestionSkipped")));
                    OustAppState.getInstance().getAssessmentFirebaseClass().setSkippedQues(n1);
                    userEventAssessmentData.setnQuestionSkipped(n1);
                }
                if (assessmentProgressMainMap.get("nQuestionCorrect") != null) {
                    int n1 = (int) (OustSdkTools.newConvertToLong(assessmentProgressMainMap.get("nQuestionCorrect")));
                    OustAppState.getInstance().getAssessmentFirebaseClass().setRightQues(n1);
                    userEventAssessmentData.setnQuestionCorrect(n1);
                    nQuestionCorrect = n1;
                }

                if (totalQues > 0) {
                    if (assessmentProgressMainMap.get("scoresList") != null) {
                        List<Scores> scores = new ArrayList<>();
                        List<Object> assessmentProgressScoreList = (List<Object>) assessmentProgressMainMap.get("scoresList");
                        if (assessmentProgressScoreList != null && assessmentProgressScoreList.size() > 0) {
                            for (int i = 0; i < assessmentProgressScoreList.size(); i++) {
                                final Map<String, Object> assessmentScoreMap = (Map<String, Object>) assessmentProgressScoreList.get(i);
                                Scores scores1 = new Scores();
                                if ((assessmentScoreMap.get("answer") != null)) {
                                    String answer1 = (String) assessmentScoreMap.get("answer");
                                    answer1 = getUtfStr(answer1);
                                    answer1 = answer1.replaceAll("\\n", "");
                                    answer1 = answer1.replaceAll("\\r", "");
                                    if (!answer1.isEmpty()) {
                                        scores1.setAnswer(answer1);
                                    }
                                }
                                if ((assessmentScoreMap.get("questionId") != null)) {
                                    if (Objects.requireNonNull(assessmentScoreMap.get("questionId")).getClass() == Long.class) {
                                        scores1.setQuestion((int) ((long) assessmentScoreMap.get("questionId")));
                                    } else
                                        scores1.setQuestion((int) OustSdkTools.newConvertToLong(assessmentScoreMap.get("questionId")));
                                }
                                if ((assessmentScoreMap.get("questionSerialNo") != null)) {
                                    if (Objects.requireNonNull(assessmentScoreMap.get("questionSerialNo")).getClass() == Long.class) {
                                        scores1.setQuestionSerialNo((int) ((long) assessmentScoreMap.get("questionSerialNo")));
                                    } else
                                        scores1.setQuestionSerialNo((int) OustSdkTools.newConvertToLong(assessmentScoreMap.get("questionSerialNo")));
                                }
                                if ((assessmentScoreMap.get("questionType") != null)) {
                                    scores1.setQuestionType((String) assessmentScoreMap.get("questionType"));
                                }
                                if ((assessmentScoreMap.get("score") != null)) {
                                    if (Objects.requireNonNull(assessmentScoreMap.get("score")).getClass() == Long.class) {
                                        scores1.setScore(((Long) assessmentScoreMap.get("score")));
                                    } else
                                        scores1.setScore(OustSdkTools.newConvertToLong(assessmentScoreMap.get("score")));
                                }
                                if ((assessmentScoreMap.get("questionTime") != null)) {
                                    if (Objects.requireNonNull(assessmentScoreMap.get("questionTime")).getClass() == Long.class) {
                                        scores1.setTime(((Long) assessmentScoreMap.get("questionTime")));
                                    } else
                                        scores1.setTime(OustSdkTools.newConvertToLong(assessmentScoreMap.get("questionTime")));
                                }
                                if ((assessmentScoreMap.get("xp") != null)) {
                                    if (Objects.requireNonNull(assessmentScoreMap.get("xp")).getClass() == Long.class) {
                                        scores1.setXp((int) ((long) assessmentScoreMap.get("xp")));
                                    } else
                                        scores1.setXp((int) OustSdkTools.newConvertToLong(assessmentScoreMap.get("xp")));
                                }
                                if ((assessmentScoreMap.get("surveyQuestion") != null)) {
                                    scores1.setSurveyQuestion((boolean) assessmentScoreMap.get("surveyQuestion"));
                                }

                                if ((assessmentScoreMap.get("exitStatus") != null)) {
                                    scores1.setExitStatus((String) assessmentScoreMap.get("exitStatus"));
                                } else {
                                    scores1.setExitStatus("");
                                }

                                if ((assessmentScoreMap.get("correct") != null)) {
                                    boolean isCorrect = (boolean) assessmentScoreMap.get("correct");
                                    scores1.setCorrect(isCorrect);
                                    //if(assessmentExtraDetails.isEvent()){
                                    if (isCorrect) {
                                        assessmentExtraDetails.setnCorrect(assessmentExtraDetails.getnCorrect() + 1);
                                    } else {
                                        assessmentExtraDetails.setnWrong(assessmentExtraDetails.getnWrong() + 1);
                                    }
                                    //}
                                }

                                if ((assessmentScoreMap.get("questionMedia") != null)) {
                                    scores1.setQuestionMedia((String) assessmentScoreMap.get("questionMedia"));
                                }

                                if ((assessmentScoreMap.get("remarks") != null)) {
                                    scores1.setRemarks((String) assessmentScoreMap.get("remarks"));
                                }

                                if ((assessmentScoreMap.get("userSubjectiveAns") != null)) {
                                    scores1.setUserSubjectiveAns((String) assessmentScoreMap.get("userSubjectiveAns"));
                                }
                                scores.add(scores1);
                            }
                            assessmentPlayResponse.setScoresList(scores);
                        } else {
                            assessmentPlayResponse.setQuestionIndex(0);
                        }
                    } else {
                        assessmentPlayResponse.setQuestionIndex(0);
                    }
                    try {
                        if (assessmentPlayResponse.getScoresList() != null && assessmentPlayResponse.getScoresList().size() > 1) {
                            Collections.reverse(assessmentPlayResponse.getScoresList());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (userEventAssessmentData != null) {
                        if (userEventAssessmentData.getnQuestionWrong() != assessmentExtraDetails.getnWrong()) {
                            userEventAssessmentData.setnQuestionWrong(assessmentExtraDetails.getnWrong());
                        }

                        if (userEventAssessmentData.getnQuestionCorrect() != assessmentExtraDetails.getnCorrect()) {
                            userEventAssessmentData.setnQuestionCorrect(assessmentExtraDetails.getnCorrect());
                        }
                    }
                }
                assessmentPlayResponse.setTotalQuestion(totalQues);
                if ((assessmentExtraDetails.isFromWorkDairy() || isAssessmentCompleted) && !assessmentPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED)
                        && !assessmentPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.COMPLETED)) {
                    assessmentPlayResponse.setAssessmentState(AssessmentState.OVER);
                    assessmentDetailModel.setShowAssessmentResultScore(false);
                } else if (assessmentExtraDetails.isFromWorkDairy() && assessmentPlayResponse.getAssessmentState().equalsIgnoreCase(AssessmentState.SUBMITTED) && assessmentPlayResponse.getScoresList() == null) {
                    assessmentPlayResponse.setAssessmentState(AssessmentState.OVER);
                    assessmentDetailModel.setShowAssessmentResultScore(false);
                }
                setAssessmentPlayResponse(assessmentPlayResponse);
            } else {
                if (assessmentExtraDetails.isFromWorkDairy() || isAssessmentCompleted) {
                    Log.d(TAG, "onDataChange: From work dairy");
                    assessmentPlayResponse.setAssessmentState(AssessmentState.OVER);
                    assessmentDetailModel.setShowAssessmentResultScore(false);
                    setAssessmentPlayResponse(assessmentPlayResponse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getUtfStr(String s2) {
        String s1 = "";
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            byte[] b = s2.getBytes();
            ByteBuffer bb = ByteBuffer.wrap(b);
            CharBuffer parsed = decoder.decode(bb);
            s1 = "" + parsed;
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return s1;
    }

    public void setAssessmentPlayResponse(AssessmentPlayResponse assessmentPlayResponse) {
        if (assessmentPlayResponse != null) {
            this.assessmentPlayResponse = assessmentPlayResponse;
        }
        setButtonString();
    }

    private void setButtonString() {
        try {
            Log.i("start button status", "assessment status");
            if (assessmentPlayResponse != null && assessmentDetailModel != null) {
                assessmentDetailModel.setInProgress(false);
                assessmentDetailModel.setCompleted(false);
                assessmentDetailModel.setReAttempt(false);
                assessmentDetailModel.setNoOfAttemptLeft(assessmentFirebaseClass.getAttemptCount());
                if (assessmentPlayResponse.getAssessmentState() != null) {
                    if (assessmentPlayResponse.getEndTime() != null && !assessmentPlayResponse.getEndTime().isEmpty() &&
                            !assessmentPlayResponse.getEndTime().equalsIgnoreCase("null")
                            && assessmentPlayResponse.getAssessmentState().equals(AssessmentState.SUBMITTED)) {
                        String completionDateText = context.getResources().getString(R.string.completed_on) + " " + dateFormatForCompletion(assessmentPlayResponse.getEndTime());
                        assessmentDetailModel.setCompletionDate(completionDateText);

                        if (assessmentFirebaseClass.isReattemptAllowed()) {
                            if (assessmentFirebaseClass.getAttemptCount() >= assessmentFirebaseClass.getNoOfAttemptAllowedToPass()) {
                                //show share_text icon
                                assessmentDetailModel.setShare(true);
                                assessmentDetailModel.setStatus("Over");
                                assessmentDetailModel.setCompleted(true);
                                assessmentDetailModel.setButtonEnabled(false);
                                if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                                    assessmentDetailModel.setStatus("Answers");
                                    assessmentDetailModel.setButtonEnabled(true);
                                    assessmentDetailModel.setAnswer(true);
                                }

                            } else {
                                assessmentDetailModel.setStatus("");
                                assessmentDetailModel.setCompleted(false);
                                assessmentDetailModel.setReAttempt(true);
//                                removeUserAssessmentProgress(assessmentId, activeUser);
                            }
                        } else {
                            //show share_text icon
                            if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                                assessmentDetailModel.setStatus("Answers");
                                assessmentDetailModel.setButtonEnabled(true);
                                assessmentDetailModel.setAnswer(true);
                            } else {
                                assessmentDetailModel.setShare(true);
                                assessmentDetailModel.setStatus("Over");
                                assessmentDetailModel.setButtonEnabled(false);
                            }
                            assessmentDetailModel.setCompleted(true);
                        }
                    } else {
                        if (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.INPROGRESS) || (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.STARTED) && assessmentPlayResponse.getGameId() != null && !assessmentPlayResponse.getGameId().isEmpty())) {
                            assessmentDetailModel.setStatus("Resume");
                            assessmentDetailModel.setInProgress(true);
                            long totalQuestions = assessmentFirebaseClass.getNumQuestions();
                            if (assessmentPlayResponse.getTotalQuestion() > 0 && assessmentPlayResponse.getTotalQuestion() > totalQuestions) {
                                totalQuestions = assessmentPlayResponse.getTotalQuestion();
                            }
                            int progress = (int) (((float) assessmentPlayResponse.getQuestionIndex() / (float) totalQuestions) * 100);
                            assessmentDetailModel.setProgress(progress);
                        } else if (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.COMPLETED)) {
                            assessmentDetailModel.setDescription(context.getResources().getString(R.string.assessment_completed_not_submit));
                            assessmentDetailModel.setCompleted(false);
                            if ((!assessmentExtraDetails.isEnrolled()) || (assessmentFirebaseClass.isReattemptAllowed())) {
                                assessmentDetailModel.setStatus("Submit");
                            } else if (assessmentExtraDetails.isFromWorkDairy() || isAssessmentCompleted) {
                                assessmentDetailModel.setDescription("");
                                assessmentDetailModel.setShare(true);
                                assessmentDetailModel.setStatus("Over");
                                assessmentDetailModel.setButtonEnabled(false);
                                assessmentDetailModel.setCompleted(true);
                                if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                                    assessmentDetailModel.setStatus("Answers");
                                    assessmentDetailModel.setButtonEnabled(true);
                                    assessmentDetailModel.setAnswer(true);
                                }
                            } else {
                                assessmentDetailModel.setStatus("Submit");
                            }
                        } else if (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.OVER)) {
                            assessmentDetailModel.setShare(true);
                            assessmentDetailModel.setStatus("Over");
                            assessmentDetailModel.setButtonEnabled(false);
                            assessmentDetailModel.setCompleted(true);
                        } else if (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.SUBMITTED)) {
                            String completionDateText = "";
                            assessmentDetailModel.setCompletionDate(completionDateText);

                            if (assessmentFirebaseClass.isReattemptAllowed()) {
                                if (assessmentFirebaseClass.getAttemptCount() >= assessmentFirebaseClass.getNoOfAttemptAllowedToPass()) {
                                    //show share_text icon
                                    assessmentDetailModel.setShare(true);
                                    assessmentDetailModel.setStatus("Over");
                                    assessmentDetailModel.setButtonEnabled(false);
                                    assessmentDetailModel.setCompleted(true);
                                    if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                                        assessmentDetailModel.setStatus("Answers");
                                        assessmentDetailModel.setButtonEnabled(true);
                                        assessmentDetailModel.setAnswer(true);
                                    }

                                } else {
                                    assessmentDetailModel.setStatus("");
                                    assessmentDetailModel.setCompleted(false);
                                    assessmentDetailModel.setReAttempt(true);
                                }
                            } else {
                                //show share_text icon
                                if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                                    assessmentDetailModel.setStatus("Answers");
                                    assessmentDetailModel.setButtonEnabled(true);
                                    assessmentDetailModel.setAnswer(true);
                                } else {
                                    assessmentDetailModel.setShare(true);
                                    assessmentDetailModel.setStatus("Over");
                                    assessmentDetailModel.setButtonEnabled(false);
                                }
                                assessmentDetailModel.setCompleted(true);
                            }
                        } else {
                            if (assessmentFirebaseClass.isEnrolled()) {
                                assessmentDetailModel.setStatus("Resume");
                                assessmentDetailModel.setInProgress(true);
                                long totalQuestions = assessmentFirebaseClass.getNumQuestions();
                                if (assessmentPlayResponse.getTotalQuestion() > 0 && assessmentPlayResponse.getTotalQuestion() > totalQuestions) {
                                    totalQuestions = assessmentPlayResponse.getTotalQuestion();
                                }
                                int progress = (int) (((float) assessmentPlayResponse.getQuestionIndex() / (float) totalQuestions) * 100);
                                assessmentDetailModel.setProgress(progress);
                            }
                        }
                    }
                } else {
                    if ((assessmentPlayResponse.getGameId() != null) &&
                            (!assessmentPlayResponse.getGameId().isEmpty()) && (assessmentPlayResponse.getTotalQuestion() > 0)) {

                        assessmentDetailModel.setStatus("Resume");
                        assessmentDetailModel.setInProgress(true);
                        long totalQuestions = assessmentFirebaseClass.getNumQuestions();
                        if (assessmentPlayResponse.getTotalQuestion() > 0 && assessmentPlayResponse.getTotalQuestion() > totalQuestions) {
                            totalQuestions = assessmentPlayResponse.getTotalQuestion();
                        }
                        int progress = (int) (((float) assessmentPlayResponse.getQuestionIndex() / (float) totalQuestions) * 100);
                        assessmentDetailModel.setProgress(progress);
                    }
                }

                if (assessmentPlayResponse != null && assessmentPlayResponse.getAssessmentState() != null && (assessmentPlayResponse.getAssessmentState().equals(AssessmentState.SUBMITTED) || isAssessmentCompleted || (assessmentExtraDetails != null && assessmentExtraDetails.isFromWorkDairy())) &&
                        assessmentPlayResponse.getScoresList() != null && assessmentPlayResponse.getScoresList().size() != 0) {
                    int userCorrectAnswerCount = 0;
                    int userWrongAnswerCount = 0;
                    int userSurveyQuestionsCount = 0;
                    int userTotalScore = 0;
                    int userScore = 0;
                    int qstnAttempt = 0;
                    float totaltimetaken = 0;
                    try {
                        for (Scores scores : assessmentPlayResponse.getScoresList()) {
                            if (scores.getQuestion() != 0) {
                                if (!scores.isSurveyQuestion()) {
                                    if (!scores.isCorrect()) {
                                        userWrongAnswerCount++;

                                    } else {
                                        userCorrectAnswerCount++;
                                        userScore += scores.getScore();
                                    }
                                    totaltimetaken += scores.getTime();
                                    qstnAttempt++;
                                } else {
                                    userSurveyQuestionsCount++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    userTotalScore = (int) ((userCorrectAnswerCount) * assessmentFirebaseClass.getQuestionXp());
                    int totalScore = (int) ((assessmentFirebaseClass.getNumQuestions() - userSurveyQuestionsCount) * assessmentFirebaseClass.getQuestionXp());
                    String userScoreText = userTotalScore + "/" + totalScore;
                    assessmentDetailModel.setUserScore(userScoreText);
                    float numQuestion = (assessmentFirebaseClass.getNumQuestions() - userSurveyQuestionsCount);
                    long passPercentage = assessmentFirebaseClass.getPassPercentage();
                    double correctProgress;
                    if (assessmentFirebaseClass.isExamMode()) {
                        userWrongAnswerCount = (int) (numQuestion - userCorrectAnswerCount);
                    }
                    double wrongProgress;
                    double userProgress = ((qstnAttempt / numQuestion) * 100);
                    double passScore = (((float) (passPercentage * totalScore)) / 100);

                    if (userWrongAnswerCount == nQuestionWrong) {
                        assessmentDetailModel.setWrongAnswer(userWrongAnswerCount);
                        wrongProgress = ((userWrongAnswerCount / numQuestion) * 100);
                    } else {
                        assessmentDetailModel.setWrongAnswer(nQuestionWrong);
                        wrongProgress = ((nQuestionWrong / numQuestion) * 100);
                    }
                    assessmentDetailModel.setWrongAnswerProgress((int) wrongProgress);
                    if (userCorrectAnswerCount == nQuestionCorrect) {
                        assessmentDetailModel.setCorrectAnswer(userCorrectAnswerCount);
                        correctProgress = ((userCorrectAnswerCount / numQuestion) * 100);
                    } else {
                        assessmentDetailModel.setCorrectAnswer(nQuestionCorrect);
                        correctProgress = ((nQuestionCorrect / numQuestion) * 100);
                    }
                    assessmentDetailModel.setCorrectAnswerProgress((int) correctProgress);
                    assessmentDetailModel.setUserProgress((int) userProgress);
                    assessmentDetailModel.setUserPercentage(((int) correctProgress + "% "));
                    assessmentDetailModel.setPassScore(((int) passScore + "/" + totalScore));

                    if (correctProgress >= passPercentage) {
                        assessmentDetailModel.setPassed(true);
                        // assessmentDetailModel.setStatus("You have completed");
                        assessmentDetailModel.setShare(true);
                        assessmentDetailModel.setStatus("Over");
                        assessmentDetailModel.setCompleted(true);
                        assessmentDetailModel.setButtonEnabled(false);


                        if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                            assessmentDetailModel.setStatus("Answers");
                            assessmentDetailModel.setButtonEnabled(true);
                            assessmentDetailModel.setAnswer(true);
                        }
                    } else {
                        assessmentDetailModel.setPassed(false);
                    }
                   /* if ((hitCreateGameApi && !isAssessmentCompleted) || (isAssessmentIssues && isAssessmentCompleted)) {
                       createGameForCompletion();
                    }*/
                    assessmentDetailModel.setTimeTaken(timeFormat(totaltimetaken));
                }

                assessmentDetailModel.setParticipantsCount(assessmentFirebaseClass.getEnrolledCount() + "");

                if (activeGame != null) {
                    assessmentDetailModel.setActiveGame(activeGame);
                }
                if (playResponse != null) {
                    assessmentDetailModel.setPlayResponse(playResponse);
                }

                if (assessmentPlayResponse != null) {
                    assessmentDetailModel.setAssessmentPlayResponse(assessmentPlayResponse);
                }
                assessmentDetailModel.setShowPastDeadlineModulesOnLandingPage(showPastDeadlineModulesOnLandingPage);
               /* if (isAssessmentCompleted&&!assessmentStatus.equalsIgnoreCase(AssessmentState.COMPLETED)) {
                    assessmentDetailModel.setShare(true);
                    assessmentDetailModel.setStatus("Over");
                    assessmentDetailModel.setButtonEnabled(false);
                    assessmentDetailModel.setCompleted(true);
                    if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                        assessmentDetailModel.setStatus("Answers");
                        assessmentDetailModel.setButtonEnabled(true);
                        assessmentDetailModel.setAnswer(true);
                    }
                }*/
                assessmentDetailModelMutableLiveData.postValue(assessmentDetailModel);
            } else {
                if (assessmentDetailModel == null) {
                    assessmentDetailModel = new AssessmentDetailModel();
                }
                /*if (isAssessmentCompleted&&!assessmentStatus.equalsIgnoreCase(AssessmentState.COMPLETED)) {
                    assessmentDetailModel.setShare(true);
                    assessmentDetailModel.setStatus("Over");
                    assessmentDetailModel.setButtonEnabled(false);
                    assessmentDetailModel.setCompleted(true);
                    if (assessmentFirebaseClass.isShowQuestionsOnCompletion()) {
                        assessmentDetailModel.setStatus("Answers");
                        assessmentDetailModel.setButtonEnabled(true);
                        assessmentDetailModel.setAnswer(true);
                    }
                    assessmentDetailModelMutableLiveData.postValue(assessmentDetailModel);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String dateFormat(String date) {
        String formattedDate = "";
        try {
            formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(Long.parseLong(date)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return formattedDate;
    }


    private String dateFormatWithTime(String date) {
        String formattedDate = "";
        try {
            formattedDate = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(new Date(Long.parseLong(date)));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return formattedDate;
    }

    private String dateFormatForCompletion(String date) {//2020-12-12 11:36:05 AM
        String formattedDate = "";
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date);
            assert dateTime != null;
            formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return formattedDate;
    }

    public void startAssessment() {
        try {
            Log.d(TAG, "startAssessment: ");
            if (assessmentDetailOther != null) {
                assessmentDetailOther.setNextWorkFail(false);
            }
            if (assessmentPlayResponse != null && assessmentPlayResponse.getAssessmentState() != null && !assessmentPlayResponse.getAssessmentState().equals(AssessmentState.SUBMITTED) && (assessmentPlayResponse.getGameId() != null && !assessmentPlayResponse.getGameId().isEmpty() && !assessmentPlayResponse.getGameId().equals(""))) {
                Log.d(TAG, "startAssessment: assessment resume with gameID:" + assessmentPlayResponse.getGameId());
                if (assessmentFirebaseClass.isConditionalFlow() && assessmentFirebaseClass.getConditionalFlowURL() != null) {
                    assessmentDetailOther = new AssessmentDetailOther();
                    activeGame.setGameid(assessmentPlayResponse.getGameId());
                    assessmentDetailOther.setType(6);
                    assessmentDetailOther.setActiveGame(activeGame);
                    assessmentDetailOther.setAssessmentFirebaseClass(assessmentFirebaseClass);
                    assessmentDetailOther.setConditionalFlowUrl(assessmentFirebaseClass.getConditionalFlowURL());
                    assessmentDetailOtherMutableLiveData.postValue(assessmentDetailOther);
                } else {
                    activeGame.setGameid("" + assessmentPlayResponse.getGameId());
                    getAllQuestionsAtATime(assessmentPlayResponse.getGameId());
                }
            } else if (assessmentPlayResponse != null && assessmentPlayResponse.getAssessmentState() != null && assessmentPlayResponse.getAssessmentState().equals(AssessmentState.SUBMITTED)) {
                Log.d(TAG, "startAssessment: assessment reattempt");
                start_new_enroll();
            } else {
                //assessment start as fresh
                start_new_enroll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*  private void createGameForCompletion() {
          Log.d(TAG, "createGameForCompletion: ");
          try {
              CreateGameRequest createGameRequest = new CreateGameRequest();
              createGameRequest.setChallengerid(activeUser.getStudentid());
              createGameRequest.setGuestUser(false);
              createGameRequest.setRematch(false);
              createGameRequest.setAssessmentId(assessmentId);
              if (courseId != null && !courseId.isEmpty() && !courseId.equalsIgnoreCase("null") && !courseId.equalsIgnoreCase("0")) {
                  try {
                      createGameRequest.setCourseId(Long.parseLong(courseId));
                  } catch (Exception e) {
                      e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                  }
              } else if (isMultipleCplEnable && isCplId != 0) {
                  createGameRequest.setContentPlayListId(isCplId);
              }
              //removeUserAssessmentProgress(assessmentId,activeUser);
              String languageStr = Locale.getDefault().getLanguage();
              createGameRequest.setAssessmentLanguage(languageStr);
              createGameApi(createGameRequest);
          } catch (Exception e) {
              e.printStackTrace();
            OustSdkTools.sendSentryException(e);
          }
      }

      private void createGameApi(CreateGameRequest createGameRequest) {
          try {
              Gson gson = new GsonBuilder().create();
              String jsonParams = gson.toJson(createGameRequest);
              JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
              String createGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.create_game_v3);
              createGameUrl = HttpManager.getAbsoluteUrl(createGameUrl);
              Log.d(TAG, "createGameApi: " + jsonParams);

              ApiCallUtils.doNetworkCallWithErrorBody(Request.Method.POST, createGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                  @Override
                  public void onResponse(JSONObject response) {
                      Log.d(TAG, "onCreateResponse: " + response.toString());
                      isAssessmentIssues = false;
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
  */
    public void failDueToNetwork() {
        try {
            netWorkFail();
            OustSdkTools.showToast(context.getResources().getString(R.string.retry_internet_msg));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void showPopup(Popup popup, ActiveGame activeGame) {
        try {
            OustStaticVariableHandling.getInstance().setOustpopup(popup);
            OustAppState.getInstance().setHasPopup(false);
            assessmentDetailOther = new AssessmentDetailOther();
            assessmentDetailOther.setType(1);
            assessmentDetailOther.setActiveGame(activeGame);
            assessmentDetailOther.setAssessmentExtraDetails(assessmentExtraDetails);
            assessmentDetailOtherMutableLiveData.postValue(assessmentDetailOther);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void netWorkFail() {
        try {
            if (assessmentDetailOther == null) {
                assessmentDetailOther = new AssessmentDetailOther();
            }
            assessmentDetailOther.setNextWorkFail(true);
            assessmentDetailOtherMutableLiveData.postValue(assessmentDetailOther);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void updateCompletePercentage(PlayResponse playResponse) {
        try {
            assessmentProgressbar.setProgress(downloadQuestionNo);

            if (totalQuestion == downloadQuestionNo) {
                assessmentProgressbar.setTxtProgress("100 %");
                assessmentProgressbarMutableLiveData.postValue(assessmentProgressbar);
                downloadMediaFiles(playResponse);
            } else {
                float percentage = ((float) downloadQuestionNo / (float) totalQuestion) * 100;
                if (percentage < 100) {

                    assessmentProgressbar.setTxtProgress((int) percentage + "%");

                } else {
                    assessmentProgressbar.setTxtProgress("100 %");

                }
                assessmentProgressbarMutableLiveData.postValue(assessmentProgressbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void downloadMediaFiles(PlayResponse playResponse) {
        try {
            questionsArrayList = new ArrayList<>();
            for (int i = 0; i < totalQuestion; i++) {
                try {
                    DTOQuestions realmQuestions = RoomHelper.getQuestionById(playResponse.getqIdList().get(i));
                    if (realmQuestions != null) {
                        questionsArrayList.add(realmQuestions);
                        OustMediaTools.prepareMediaList(mediaList, realmQuestions);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            assessmentDetailOther = new AssessmentDetailOther();
            assessmentDetailOther.setType(4);
            assessmentDetailOther.setActiveGame(activeGame);
            assessmentDetailOther.setAssessmentFirebaseClass(assessmentFirebaseClass);
            assessmentDetailOther.setAssessmentPlayResponse(assessmentPlayResponse);
            assessmentDetailOther.setMediaList(mediaList);
            assessmentDetailOther.setAssessmentExtraDetails(assessmentExtraDetails);
            assessmentDetailOther.setQuestionsArrayList(questionsArrayList);
            assessmentDetailOtherMutableLiveData.postValue(assessmentDetailOther);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotResponse(final DTOQuestions questions, final PlayResponse playResponse) {
        try {
            runOnUiThread(() -> checkForDownloadComplete(questions, playResponse));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForDownloadComplete(DTOQuestions questions, PlayResponse playResponse) {
        try {
            if (questions != null) {
                OustSdkTools.databaseHandler.addToRealmQuestions(questions, false);
                questionsArrayList.add(questions);
                downloadQuestionNo++;
                if (questions.isSurveyQuestion()) {
                    OustAppState.getInstance().getAssessmentFirebaseClass().setSurveyQuestionCount(OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount() + 1);
                }
                updateCompletePercentage(playResponse);
            } else {
                noofTry++;
                if (noofTry < 4) {
                    getAllQuestionsAtATime(String.valueOf(playResponse.getGameId()));
                } else {
                    OustSdkTools.showToast(context.getResources().getString(R.string.retry_internet_msg));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private String timeFormat(float totalTimeTaken) {
        String formattedTime;
        int hrs = (int) (totalTimeTaken / 3600);
        int minutes = (int) ((totalTimeTaken % 3600) / 60);
        int seconds = (int) (totalTimeTaken % 60);
        if (hrs > 0) {
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, minutes, seconds) + " hrs";
        } else if (minutes > 0) {
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds) + " mins";
        } else {
            formattedTime = String.format(Locale.getDefault(), "%02d", seconds) + " sec";
        }
        return formattedTime;
    }

    public void checkAssessmentState() {
        try {
            if (assessmentDetailOther != null) {
                assessmentDetailOther.setNextWorkFail(false);
            }
            if (assessmentPlayResponse != null && (assessmentPlayResponse.getGameId() != null) && (!assessmentPlayResponse.getGameId().isEmpty()) && (assessmentPlayResponse.getTotalQuestion() > 0)) {
                activeGame.setGameid(assessmentPlayResponse.getGameId());
                checkForSavedResponse(("" + assessmentFirebaseClass.getAsssessemntId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void checkForSavedResponse(String assessmentId) {
        String playResponseStr = "";
        try {
            EnternalPrivateStorage enternalPrivateStorage = new EnternalPrivateStorage();
            String fileNameStr = ("assessment" + assessmentId + ".txt");
            playResponseStr = enternalPrivateStorage.readSavedData(fileNameStr);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        gotSavedPlayResponseStr(playResponseStr);
    }

    public void gotSavedPlayResponseStr(String playResponseStr) {
        try {
            if ((playResponseStr != null) && (!playResponseStr.isEmpty())) {
                Gson gson = new Gson();
                playResponse = gson.fromJson(playResponseStr, PlayResponse.class);
            } else {
                Log.d(TAG, "gotSavedPlayResponseStr: null");
            }
            fetchPlayResponse(activeGame.getGameid());
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void fetchPlayResponse(String gameId) {
        try {
            getAllQuestionsAtATime(gameId);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitAssessmentGame() {
        try {
            if (!OustSdkTools.checkInternetStatus()) {
                return;
            }
            SubmitRequest submitRequest = new SubmitRequest();
            submitRequest.setWinner(assessmentPlayResponse.getWinner());
            submitRequest.setGameid(assessmentPlayResponse.getGameId());
            submitRequest.setTotalscore(assessmentPlayResponse.getChallengerFinalScore());
            submitRequest.setScores(assessmentPlayResponse.getScoresList());
            submitRequest.setEndTime(assessmentPlayResponse.getEndTime());
            submitRequest.setStartTime(assessmentPlayResponse.getStartTime());
            submitRequest.setOpponentid(assessmentPlayResponse.getOpponentid());
            submitRequest.setAssessmentId(("" + assessmentFirebaseClass.getAsssessemntId()));

            if (assessmentPlayResponse.getChallengerid() != null && !assessmentPlayResponse.getChallengerid().isEmpty()) {
                submitRequest.setChallengerid(assessmentPlayResponse.getChallengerid());
            } else if (activeUser != null && activeUser.getStudentid() != null && !activeUser.getStudentid().isEmpty()) {
                submitRequest.setChallengerid(activeUser.getStudentid());
            } else {
                activeUser = OustAppState.getInstance().getActiveUser();
                submitRequest.setChallengerid(activeUser.getStudentid());
            }

            if (courseId != null) {
                submitRequest.setCourseId(courseId);
            }
            if (courseColId != null) {
                submitRequest.setCourseColnId(courseColId);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            submitAssessment(submitRequest);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void submitAssessment(SubmitRequest submitRequest) {
        Log.d(TAG, "submitAssessment: ");
        final SubmitResponse[] submitResponses = {null};
        try {
            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            try {
                ShowPopup.getInstance().showProgressBar(context, context.getResources().getString(R.string.calculating_score));
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            try {
                submitRequest.setContentPlayListId(isCplId);

                if (submitRequest.getCourseId() != null && !submitRequest.getCourseId().isEmpty() && mappedSurveyId != null && !mappedSurveyId.isEmpty()) {
                    submitGameUrl = submitGameUrl + "mappedSurveyId=" + Long.parseLong(mappedSurveyId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);

            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);
            Log.d(TAG, "submitScore: " + parsedJsonParams);
            Log.d(TAG, "submitScore: URL:" + submitGameUrl);

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    ShowPopup.getInstance().dismissProgressDialog();
                    if (response != null) {
                        Log.d("assessment response", response.toString());
                        Gson gson = new GsonBuilder().create();
                        submitResponses[0] = gson.fromJson(response.toString(), SubmitResponse.class);
                        submitRequestProcessFinish(submitResponses[0]);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    failDueToNetwork();
                    submitResponses[0] = null;
                    ShowPopup.getInstance().dismissProgressDialog();
                    submitRequestProcessFinish(submitResponses[0]);
                }
            });
        } catch (
                Exception e) {
            failDueToNetwork();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        Log.d(TAG, "submitRequestProcessFinish: ");
        if ((null != submitResponse) && (submitResponse.isSuccess())) {
            OustPreferences.save("lastgamesubmitrequest", "");
            if (OustAppState.getInstance().isAssessmentGame()) {
                if (assessmentPlayResponse != null) {
                    assessmentPlayResponse.setAssessmentState(AssessmentState.SUBMITTED);
                    assessmentPlayResponse.setResumeTime("" + 0);
//                checkForAssessmentComplete(activeUser, assessmentFirebaseClass.getAsssessemntId());
                    fetchAssessmentsFromFirebase(String.valueOf(assessmentFirebaseClass.getAsssessemntId()));
                }
            }
        }
        OustAppState.getInstance().setPlayResponse(this.playResponse);
    }

    private void start_new_enroll() {
        try {
            Log.d(TAG, "start_new_enroll: ");
            AssessmentEnrollCreateGameRequest assessmentEnrollCreateGameRequest = new AssessmentEnrollCreateGameRequest();
            assessmentEnrollCreateGameRequest.setUserId(activeUser.getStudentid());

            try {
                if (courseId != null && !courseId.isEmpty() && !courseId.equalsIgnoreCase("null") && !courseId.equalsIgnoreCase("0")) {
                    assessmentEnrollCreateGameRequest.setCourseId(Integer.parseInt(courseId));
                } else if (isCplId != 0) {
                    assessmentEnrollCreateGameRequest.setCplId((int) isCplId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(assessmentEnrollCreateGameRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");

            String enrolCreateGame = OustSdkApplication.getContext().getResources().getString(R.string.enrol_create_api);
            enrolCreateGame = enrolCreateGame.replace("{org-id}", tenantName);
            enrolCreateGame = enrolCreateGame.replace("{assessmentId}", "" + assessmentFirebaseClass.getAsssessemntId());
            enrolCreateGame = HttpManager.getAbsoluteUrl(enrolCreateGame);

            Log.e(TAG, "start_enroll: Assessment -> " + enrolCreateGame + " assessmentCreateGameRequest -> " + jsonParams);
            ApiCallUtils.doNetworkCall(Request.Method.PUT, enrolCreateGame, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    enrollCreateAssessmentOver(response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    failDueToNetwork();
                    enrollCreateAssessmentOver(null);

                }
            });
        } catch (Exception e) {
            failDueToNetwork();
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void enrollCreateAssessmentOver(JSONObject response) {
        try {
            Log.d(TAG, "enrolledAssessmentOver:-->  " + response);
            if (response != null) {
                Map<String, Object> assessmentEnrollData = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    assessmentEnrollData = mapper.readValue(String.valueOf(response), new TypeReference<Map<String, Object>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                if (assessmentEnrollData != null) {
                    OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolledCount((OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount() + 1));
                    if (assessmentEnrollData.get("enrolled") != null) {
                        assessmentFirebaseClass.setEnrolled((boolean) assessmentEnrollData.get("enrolled"));
                        OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolled((boolean) assessmentEnrollData.get("enrolled"));
                    }

                    if (assessmentEnrollData.get("enrolledTime") != null) {
                        OustAppState.getInstance().getAssessmentFirebaseClass().setEnrolledTime(OustSdkTools.newConvertToLong(assessmentEnrollData.get("enrolledTime")));
                        assessmentFirebaseClass.setEnrolledTime(OustSdkTools.newConvertToLong(assessmentEnrollData.get("enrolledTime")));
                    }

                    if (assessmentEnrollData.get("gameId") != null) {
                        activeGame.setGameid("" + assessmentEnrollData.get("gameId"));
                    }

                    if (assessmentFirebaseClass.isConditionalFlow() && assessmentFirebaseClass.getConditionalFlowURL() != null) {
                        assessmentDetailOther = new AssessmentDetailOther();
                        activeGame.setGameid(activeGame.getGameid());
                        assessmentDetailOther.setType(6);
                        assessmentDetailOther.setActiveGame(activeGame);
                        assessmentDetailOther.setAssessmentFirebaseClass(assessmentFirebaseClass);
                        assessmentDetailOther.setConditionalFlowUrl(assessmentFirebaseClass.getConditionalFlowURL());
                        assessmentDetailOtherMutableLiveData.postValue(assessmentDetailOther);
                    } else {
                        getAllQuestionsAtATime(activeGame.getGameid());
                    }
                } else {
                    failDueToNetwork();
                }
            } else {
                failDueToNetwork();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getAllQuestionsAtATime(String gameId) {
        try {
            String assessmentGetQuestionUrl = OustSdkApplication.getContext().getResources().getString(R.string.get_all_assessment_questions);
            String tenantName = OustPreferences.get("tanentid").replaceAll(" ", "");
            assessmentGetQuestionUrl = assessmentGetQuestionUrl.replace("{org-id}", "" + tenantName);
            assessmentGetQuestionUrl = assessmentGetQuestionUrl.replace("{assessmentId}", "" + assessmentId);
            assessmentGetQuestionUrl = assessmentGetQuestionUrl.replace("{gameId}", "" + gameId);

            assessmentGetQuestionUrl = HttpManager.getAbsoluteUrl(assessmentGetQuestionUrl);

            Log.d(TAG, "getAllQuestionsAtATime: " + assessmentGetQuestionUrl);
            // TODO need to check here for Gumlet
            ApiCallUtils.doNetworkCall(Request.Method.GET, assessmentGetQuestionUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    QuestionResponse questionResponse = gson.fromJson(response.toString(), QuestionResponse.class);
                    Log.d(TAG, "onSuccess: " + response);
                    if (questionResponse.getQuestions() != null && questionResponse.getQuestions().size() > 0) {
                        PlayResponse playResponse = new PlayResponse();
                        List<Integer> qIdList = new ArrayList<>();
                        for (DTOQuestions dtoQuestions : questionResponse.getQuestions()) {
                            qIdList.add((int) dtoQuestions.getQuestionId());
                        }
                        playResponse.setGameId(Integer.parseInt(gameId));
                        playResponse.setqIdList(qIdList);
                        totalQuestion = playResponse.getqIdList().size();
                        incrementDownloadQuestionNO += 3;
                        if (incrementDownloadQuestionNO > totalQuestion) {
                            incrementDownloadQuestionNO = totalQuestion;
                        }
                        //download_progressbar.setMax(totalQuestion);
                        assessmentProgressbar.setMax(totalQuestion);
                        assessmentProgressbar.setProgress(0);
                        assessmentProgressbarMutableLiveData.postValue(assessmentProgressbar);
                        questionsArrayList = new ArrayList<>();
                        for (DTOQuestions dtoQuestions : questionResponse.getQuestions()) {
                            totalTime += dtoQuestions.getMaxtime();
                            Log.d(TAG, "run: " + dtoQuestions.getQuestion());
                            try {
                                if (dtoQuestions.getQuestionCategory() != null && dtoQuestions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_A)) {
                                    dtoQuestions.setQuestionType(QuestionType.UPLOAD_AUDIO);
                                } else if (dtoQuestions.getQuestionCategory() != null && dtoQuestions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_I)) {
                                    dtoQuestions.setQuestionType(QuestionType.UPLOAD_IMAGE);
                                } else if (dtoQuestions.getQuestionCategory() != null && dtoQuestions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.USR_REC_V)) {
                                    dtoQuestions.setQuestionType(QuestionType.UPLOAD_VIDEO);
                                } else if (dtoQuestions.getQuestionCategory() != null && dtoQuestions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.LONG_ANSWER)) {
                                    dtoQuestions.setQuestionType(QuestionType.LONG_ANSWER);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                OustSdkTools.sendSentryException(e);
                            }
                            gotResponse(dtoQuestions, playResponse);
                        }
                        OustAppState.getInstance().setPlayResponse(playResponse);
                    } else {
                        netWorkFail();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    netWorkFail();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
