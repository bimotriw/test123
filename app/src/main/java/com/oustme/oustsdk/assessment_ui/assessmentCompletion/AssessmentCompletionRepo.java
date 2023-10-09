package com.oustme.oustsdk.assessment_ui.assessmentCompletion;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.assessment_ui.assessmentDetail.AssessmentExtraDetails;
import com.oustme.oustsdk.firebase.assessment.AssesmentResultBadge;
import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseCardMedia;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.CheckModuleDistributedOrNot;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AssessmentCompletionRepo {

    private static final String TAG = "CompletionRepo";
    private long assessmentId;
    public MutableLiveData<AssessmentCompletionModel> assessmentCompletionModelMutableLiveData = new MutableLiveData<>();
    AssessmentCompletionModel assessmentCompletionModel;
    Bundle dataBundle;
    ActiveUser activeUser;
    ActiveGame activeGame;
    SubmitRequest submitRequest;
    PlayResponse playResponse;

    private float totalTimeTaken = 0;
    private boolean isDataMissMatch = false;
    private boolean examMode;
    private long overAllQuestionTime;
    String activeGameString, submitRequestString;
    AssessmentExtraDetails assessmentExtraDetails = new AssessmentExtraDetails();
    private boolean firebaseCallingFail = false;

    public AssessmentCompletionRepo(Bundle dataBundle) {
        this.dataBundle = dataBundle;
        activeUser = OustAppState.getInstance().getActiveUser();
        playResponse = OustAppState.getInstance().getPlayResponse();
        assessmentCompletionModel = new AssessmentCompletionModel();

        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
            Log.i("AssessmentLogic", "User data available");
        } else {
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
            String activeUserGet = OustPreferences.get("userdata");
            activeUser = OustSdkTools.getActiveUserData(activeUserGet);
        }

        if (dataBundle != null) {
            assessmentExtraDetails.setCplModule(dataBundle.getBoolean("isCplModule", false));
            String courseId;
            try {
                courseId = dataBundle.getString("courseId");
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
                courseId = dataBundle.getLong("courseId") + "";
            }
            String courseColnId = dataBundle.getString("courseColnId");
            //totalTimeTaken = dataBundle.getFloat("totalTimeTaken", 0);
            assessmentExtraDetails.setContainCertificate(dataBundle.getBoolean("containCertificate", false));
            assessmentExtraDetails.setFromCourse(dataBundle.getBoolean("IS_FROM_COURSE", false));
            assessmentExtraDetails.setCourseAssociated(dataBundle.getBoolean("courseAssociated", false));
            assessmentExtraDetails.setMappedCourseID(dataBundle.getLong("mappedCourseId", 0));
            assessmentExtraDetails.setShowAssessmentResultScore(dataBundle.getBoolean("showAssessmentResultScore", false));

            assessmentCompletionModel.setReAttemptAllowed(dataBundle.getBoolean("reAttemptAllowed", false));
            assessmentCompletionModel.setnAttemptCount(dataBundle.getLong("nAttemptCount", 0));
            assessmentCompletionModel.setnAttemptAllowedToPass(dataBundle.getLong("nAttemptAllowedToPass", 0));
            assessmentExtraDetails.setCourseId(courseId);

            assessmentExtraDetails.setSurveyAssociated(dataBundle.getBoolean("surveyAssociated", false));
            assessmentExtraDetails.setSurveyMandatory(dataBundle.getBoolean("surveyMandatory", false));
            assessmentExtraDetails.setMappedSurveyId(dataBundle.getLong("mappedSurveyId", 0));

            activeGameString = dataBundle.getString("ActiveGame");
            submitRequestString = dataBundle.getString("SubmitRequest");
            assessmentId = dataBundle.getLong("assessmentId", 0);
            assessmentCompletionModel.setAssessmentId(dataBundle.getLong("assessmentId", 0));

            assessmentExtraDetails.setMicroCourse(dataBundle.getBoolean("isMicroCourse", false));
            examMode = dataBundle.getBoolean("examMode", false);
            overAllQuestionTime = dataBundle.getLong("overAllQuestionTime");
        }

        if (activeGameString != null && !activeGameString.isEmpty()) {
            activeGame = OustSdkTools.getAcceptChallengeData(activeGameString);
        }

        if (submitRequestString != null && !submitRequestString.isEmpty()) {
            submitRequest = OustSdkTools.getSubmit(submitRequestString);
        }

        if (assessmentExtraDetails.isFromCourse()) {
            OustStaticVariableHandling.getInstance().setComingFromAssessment(true);
        }

        setData();
    }

    private void setData() {
//        String assessmentOverMsg = "" + OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentOverMessage();

        if (OustAppState.getInstance().getAssessmentFirebaseClass().getName() != null) {
            assessmentCompletionModel.setTitle(OustAppState.getInstance().getAssessmentFirebaseClass().getName());
        }

        String completionDateText = dateFormat("" + System.currentTimeMillis()) + "";
        assessmentCompletionModel.setCompletionDateAndTime(completionDateText);
        assessmentCompletionModel.setShowAssessmentResultRemark(OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentRemark());
        assessmentCompletionModel.setShowAssessmentResultScore(OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentResultScore());

        /*if (!assessmentOverMsg.isEmpty() && !assessmentOverMsg.equalsIgnoreCase("null")) {
            assessmentCompletionModel.setCompletedMsg(assessmentOverMsg);
        }*/

        if (submitRequest != null && submitRequest.getScores() != null && submitRequest.getScores().size() != 0) {
            int userCorrectAnswerCount = 0;
            int userWrongAnswerCount = 0;
            int userSurveyQuestionsCount = 0;
            int userTotalScore = 0;
            int qstnAttempt = 0;

            for (Scores scores : submitRequest.getScores()) {
                if (scores.getQuestion() != 0) {
                    if (!scores.isSurveyQuestion()) {
                        if (!scores.isCorrect()) {
                            userWrongAnswerCount++;
                        } else {
                            userCorrectAnswerCount++;
                            userTotalScore += scores.getScore();
                        }
                    } else {
                        userSurveyQuestionsCount++;
                    }
                    qstnAttempt++;
                    totalTimeTaken += scores.getTime();
                }
            }

            if (examMode) {
                totalTimeTaken = overAllQuestionTime;
                if ((userCorrectAnswerCount + userWrongAnswerCount) < (OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount())) {
                    int userAttemptAnswerCount = userCorrectAnswerCount + userWrongAnswerCount;
                    userWrongAnswerCount = userWrongAnswerCount + (int) ((OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount()) - userAttemptAnswerCount);
                }
            }

            if ((OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount()) != (userCorrectAnswerCount + userWrongAnswerCount) && !isDataMissMatch) {
                isDataMissMatch = true;
                fetchAssessmentsFromFirebase(assessmentId);
            } else {
                //   userTotalScore = (int) submitRequest.getTotalscore();
                int totalScore = (int) ((OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount()) * OustAppState.getInstance().getAssessmentFirebaseClass().getQuestionXp());
                String userScore = userTotalScore + "/" + totalScore;
                assessmentCompletionModel.setUserScore(userScore);
                float numQuestion = (OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() - OustAppState.getInstance().getAssessmentFirebaseClass().getSurveyQuestionCount());
                long passPercentage = OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage();
                double correctProgress = ((userCorrectAnswerCount / numQuestion) * 100);
                if (OustAppState.getInstance().getAssessmentFirebaseClass().isExamMode()) {
                    userWrongAnswerCount = (int) (numQuestion - userCorrectAnswerCount);
                }

                double wrongProgress = ((userWrongAnswerCount / numQuestion) * 100);
                double userProgress = ((qstnAttempt / numQuestion) * 100);
                double passScore = (((float) (passPercentage * totalScore)) / 100);
                if (passScore < 1) {
                    passScore = 1.0;
                }

                assessmentCompletionModel.setWrongAnswer(userWrongAnswerCount);
                assessmentCompletionModel.setWrongAnswerProgress((int) wrongProgress);
                assessmentCompletionModel.setCorrectAnswer(userCorrectAnswerCount);
                assessmentCompletionModel.setCorrectAnswerProgress((int) correctProgress);
                assessmentCompletionModel.setUserProgress((int) userProgress);
                assessmentCompletionModel.setUserPercentage(((int) correctProgress + "%"));
                assessmentCompletionModel.setPassScore(((int) passScore + "/" + totalScore));

                if (correctProgress >= passPercentage) {
                    Log.d(TAG, "setData: Passed");
                    assessmentCompletionModel.setPassed(true);
                    assessmentCompletionModel.setStatus("You have completed");
                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setState("COMPLETED");
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed(true);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                        }

                        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                        }
                    }
                } else {
                    Log.d(TAG, "setData: Failed");
                    assessmentCompletionModel.setStatus("Well tried but you did not pass");
                    assessmentCompletionModel.setPassed(false);
                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setState("COMPLETED");
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed(false);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                        }

                        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                            OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                        }
                    }
                }

                ArrayList<AssesmentResultBadge> assesmentResultBadges = OustAppState.getInstance().getAssessmentFirebaseClass().getAssesmentResultBadges();
                if (OustAppState.getInstance().getAssessmentFirebaseClass().isShowAssessmentRemark()) {
                    if (assesmentResultBadges != null && assesmentResultBadges.size() > 0) {
                        String badge = "";
                        for (int j = 0; j < assesmentResultBadges.size(); j++) {
                            if (correctProgress >= assesmentResultBadges.get(j).getFrom() && correctProgress <= assesmentResultBadges.get(j).getTo()) {
                                badge = assesmentResultBadges.get(j).getLabel();
                                break;
                            }
                        }
                        if (badge.length() > 0) {
                            assessmentCompletionModel.setRating(badge);
                        } else {
                            assessmentCompletionModel.setRating("");
                        }
                    }
                } else {
                    assessmentCompletionModel.setRating("");
                }
                assessmentCompletionModel.setTimeTaken(timeFormat(totalTimeTaken));
                int participantCount;
                if (OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount() != 0) {
                    participantCount = (int) OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount();
                } else {
                    participantCount = 1;
                }
                assessmentCompletionModel.setParticipantsCount(participantCount + "");
                if ((firebaseCallingFail || isDataMissMatch) && (OustAppState.getInstance().getAssessmentFirebaseClass().getNumQuestions() != (userCorrectAnswerCount + userWrongAnswerCount))) {
                    assessmentCompletionModel.setShowAssessmentResultScore(false);
                }
                assessmentCompletionModel.setAssessmentExtraDetails(assessmentExtraDetails);
                assessmentCompletionModel.setType(1);
                assessmentCompletionModelMutableLiveData.postValue(assessmentCompletionModel);
            }
        } else {
            assessmentCompletionModel.setTimeTaken(timeFormat(totalTimeTaken));
            int participantCount;
            if (OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount() != 0) {
                participantCount = (int) OustAppState.getInstance().getAssessmentFirebaseClass().getEnrolledCount();
            } else {
                participantCount = 1;
            }
            assessmentCompletionModel.setParticipantsCount(participantCount + "");
            assessmentCompletionModel.setAssessmentExtraDetails(assessmentExtraDetails);
            assessmentCompletionModel.setType(1);
            assessmentCompletionModelMutableLiveData.postValue(assessmentCompletionModel);
        }
    }

    private void fetchAssessmentsFromFirebase(long assessmentId) {
        try {
            final String message = "/assessment/assessment" + assessmentId;
            Log.d(TAG, "fetchAssessmentsFromFirebase: " + message);
            ValueEventListener assessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        final Map<String, Object> assessmentMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (null != assessmentMap) {
                            extractAssessmentData(assessmentMap);
                        } else {
                            gotAssessmentFromFirebase(null);
                        }
                    } catch (Exception e) {
                        gotAssessmentFromFirebase(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    gotAssessmentFromFirebase(null);
                }
            };
            OustFirebaseTools.getRootRef().child(message).addValueEventListener(assessmentListener);

        } catch (Exception e) {
            gotAssessmentFromFirebase(null);
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
                long cplId = OustSdkTools.convertToLong(assessmentMap.get("cplId"));
                OustPreferences.saveTimeForNotification("cplId_assessment", cplId);
            } else {
                OustPreferences.saveTimeForNotification("cplId_assessment", 0);
            }
            if (assessmentMap.get("assessmentId") != null) {
                assessmentFirebaseClass.setAsssessemntId(OustSdkTools.convertToLong(assessmentMap.get("assessmentId")));
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
            if (assessmentMap.get("assessmentResult") != null) {
                assessmentFirebaseClass.initAssessmentResultBadges((ArrayList<Object>) assessmentMap.get("assessmentResult"));
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
                assessmentFirebaseClass.setNumQuestions(OustSdkTools.convertToLong(assessmentMap.get("numQuestions")));
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
                assessmentFirebaseClass.setQuestionXp(OustSdkTools.convertToLong(assessmentMap.get("questionXp")));
            }
            if (assessmentMap.get("rewardOC") != null) {
                assessmentFirebaseClass.setRewardOc(OustSdkTools.convertToLong(assessmentMap.get("rewardOC")));
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
                assessmentFirebaseClass.setPassPercentage(OustSdkTools.convertToLong(assessmentMap.get("passPercentage")));
            }
            try {
                if (assessmentMap.get("type") != null) {
                    if (!Objects.equals(assessmentMap.get("type"), "")) {
                        assessmentFirebaseClass.setAssessmentType(AssessmentType.valueOf((String) assessmentMap.get("type")));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            if (assessmentMap.get("startDate") != null) {
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

            if (assessmentMap.get("hideLeaderboard") != null) {
                assessmentFirebaseClass.setHideLeaderboard((boolean) assessmentMap.get("hideLeaderboard"));
            } else {
                assessmentFirebaseClass.setHideLeaderboard(true);
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
                    }
                    assessmentMediaList.add(courseCardMedia);
                }
                assessmentFirebaseClass.setAssessmentMediaList(assessmentMediaList);
            }

            if (assessmentMap.get("noOfAttemptAllowedToPass") != null) {
                assessmentFirebaseClass.setNoOfAttemptAllowedToPass(OustSdkTools.convertToLong(assessmentMap.get("noOfAttemptAllowedToPass")));
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
                assessmentFirebaseClass.setMappedSurveyId(OustSdkTools.newConvertToLong(assessmentMap.get("mappedSurveyId")));
                assessmentExtraDetails.setMappedSurveyId(OustSdkTools.newConvertToLong(assessmentMap.get("mappedSurveyId")));
            }

            /*if (assessmentExtraDetails.isCourseAttached() && mappedCourseID != 0) {
                //getUserCourses(mappedCourseID);
            }*/

            if (assessmentMap.get("completionDeadline") != null) {
                assessmentFirebaseClass.setCompletionDeadline(OustSdkTools.convertToLong(assessmentMap.get("completionDeadline")));
            }

            if (assessmentMap.get("defaultPastDeadlineCoinsPenaltyPercentage") != null) {
                assessmentFirebaseClass.setDefaultPastDeadlineCoinsPenaltyPercentage(OustSdkTools.convertToLong(assessmentMap.get("defaultPastDeadlineCoinsPenaltyPercentage")));
            }
            if (assessmentMap.get("showPastDeadlineModulesOnLandingPage") != null) {
                assessmentFirebaseClass.setShowPastDeadlineModulesOnLandingPage((boolean) assessmentMap.get("showPastDeadlineModulesOnLandingPage"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        gotAssessmentFromFirebase(assessmentFirebaseClass);
    }

    public void gotAssessmentFromFirebase(AssessmentFirebaseClass assessmentFirebaseClass) {
        if (assessmentFirebaseClass != null) {
            OustAppState.getInstance().setAssessmentFirebaseClass(assessmentFirebaseClass);
            activeUser = OustAppState.getInstance().getActiveUser();
        } else {
            firebaseCallingFail = true;
        }
        setData();
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

    public void getCourseDetails() {
        try {
            long mappedCourseId = assessmentExtraDetails.getMappedCourseID();
            String tenantName = OustPreferences.get("tanentid").trim();
            String checkModuleDistributedUrl = OustSdkApplication.getContext().getResources().getString(R.string.check_module_distributedOrNot);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{orgId}", tenantName);
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{userId}", activeUser.getStudentid());
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleType}", "COURSE");
            checkModuleDistributedUrl = checkModuleDistributedUrl.replace("{moduleId}", String.valueOf(mappedCourseId));

            checkModuleDistributedUrl = HttpManager.getAbsoluteUrl(checkModuleDistributedUrl);
            JSONObject jsonParams = OustSdkTools.getRequestObjectforJSONObject(null);
            Log.e("BranchTools", "checkModuleDistributionOrNot: --> " + checkModuleDistributedUrl);
            ApiCallUtils.doNetworkCall(Request.Method.GET, checkModuleDistributedUrl, jsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optBoolean("success")) {
                        Gson gson = new Gson();
                        CheckModuleDistributedOrNot checkModuleDistributedOrNot = gson.fromJson(response.toString(), CheckModuleDistributedOrNot.class);
                        if (checkModuleDistributedOrNot != null) {
                            if (checkModuleDistributedOrNot.getDistributed()) {
                                if (checkModuleDistributedOrNot.getModuleId() != 0) {
                                    String moduleId = String.valueOf(checkModuleDistributedOrNot.getModuleId());
                                    assessmentCompletionModel.getAssessmentExtraDetails().setMappedCourseID(OustSdkTools.newConvertToLong(moduleId));
                                    startCourse();
                                } else if (checkModuleDistributedOrNot.getMessage() != null && !checkModuleDistributedOrNot.getMessage().isEmpty()) {
                                    OustSdkTools.showToast(checkModuleDistributedOrNot.getMessage());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.check_course_distribute_or_not));
                                }
                            } else {
                                distributeCourse(mappedCourseId);
                                OustSdkTools.showToast("Please wait for course distribution..");
                            }
                        }
                    }
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

    private void distributeCourse(long mappedCourseId) {
        String catalog_distribution_url;
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(OustAppState.getInstance().getActiveUser().getStudentid());

            catalog_distribution_url = OustSdkApplication.getContext().getResources().getString(R.string.distribut_course_url);
            catalog_distribution_url = catalog_distribution_url.replace("{courseId}", mappedCourseId + "");
            catalog_distribution_url = HttpManager.getAbsoluteUrl(catalog_distribution_url);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("users", jsonArray);
            jsonParams.put("reusabilityAllowed", true);

            ApiCallUtils.doNetworkCall(Request.Method.PUT, catalog_distribution_url, OustSdkTools.getRequestObjectforJSONObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        final CommonResponse[] commonResponses = new CommonResponse[]{null};
                        if (response.optBoolean("success") && response.has("distributedId") && response.optLong("distributedId") > 0) {
                            String distributedId = "" + response.optLong("distributedId");
                            assessmentCompletionModel.getAssessmentExtraDetails().setMappedCourseID(OustSdkTools.newConvertToLong(distributedId));
                            startCourse();
                        } else {
                            commonResponses[0] = OustSdkTools.getCommonResponse(response.toString());
                            if (commonResponses[0] != null && commonResponses[0].getExceptionData() != null) {
                                if (commonResponses[0].getExceptionData().getMessage() != null) {
                                    OustSdkTools.showToast(commonResponses[0].getExceptionData().getMessage());
                                } else {
                                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                                }
                            } else if (commonResponses[0] != null && commonResponses[0].getError() != null) {
                                OustSdkTools.showToast(commonResponses[0].getError());
                            } else {
                                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.error_message));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void startCourse() {
        assessmentCompletionModel.setType(2);
        assessmentCompletionModelMutableLiveData.postValue(assessmentCompletionModel);
    }
}