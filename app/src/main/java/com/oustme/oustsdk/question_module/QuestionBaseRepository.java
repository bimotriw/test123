package com.oustme.oustsdk.question_module;

import android.os.Bundle;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.AssessmentCopyResponse;
import com.oustme.oustsdk.BuildConfig;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.ScoresCopy;
import com.oustme.oustsdk.assessment_ui.examMode.QuestionView;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.firebase.course.CourseLevelClass;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.question_module.model.QuestionAnswerModel;
import com.oustme.oustsdk.question_module.model.QuestionBaseModel;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.room.dto.UserEventCplData;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.OustStrings;
import com.oustme.oustsdk.tools.TimeUtils;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class QuestionBaseRepository {

    private static final String TAG = "QuestionBaseRepo";
    private static QuestionBaseRepository instance;
    private MutableLiveData<QuestionBaseModel> liveData;
    private QuestionBaseModel questionBaseModel;

    ActiveUser activeUser;
    ActiveGame activeGame;
    String activeGameId;
    String startDateTime;
    String moduleName;
    long currentCplId;
    String bgImage;
    long moduleId;
    long courseId;
    int questionIndex;
    int totalCards;
    boolean showNavigationArrow;
    boolean isSurvey;
    boolean isAssessment;
    boolean isReviewMode;
    private long exitOC;
    long rewardOC;
    DTOQuestions questions;
    PlayResponse playResponse;

    boolean containCertificate;
    boolean isCplModule;
    int totalTimeOfAssessment;
    boolean isFromCourse;
    boolean isMicroCourse;
    boolean courseAssociated;
    long mappedCourseId;
    boolean timePenaltyDisabled;
    boolean resumeSameQuestion;
    boolean showAssessmentResultScore;
    boolean reAttemptAllowed;
    long questionXp;
    long nAttemptCount;
    long nAttemptAllowedToPass;
    boolean secureSessionOn;
    boolean isEventLaunch;
    int eventId;
    int nCorrect;
    int nWrong;
    boolean surveyAssociated;
    boolean surveyMandatory;
    long mappedSurveyId;
    long mappedAssessmentId;

    long associatedAssessmentId;
    String activeGameString;
    String assessmentResp;
    String submitRequestString;
    GamePoints gamePoints;

    float totalTimeTaken = 0;
    long questionResumeTime = 0;
    long enrolledTime = 0;
    long questionLocalTime = 0;
    String checkMediaVal;
    private boolean isMultipleCplEnable = false;

    List<Scores> scoresList = new ArrayList<>();
    HashMap<Integer, QuestionView> questionViewList = new HashMap<>();
    AssessmentPlayResponse userPlayResponse;
    ArrayList<QuestionAnswerModel> answerModels = new ArrayList<>();
    ArrayList<DTOQuestions> questionsArrayList;

    long challengerFinalScore = 0;
    UserEventAssessmentData userEventAssessmentData;
    SubmitRequest submitRequest;

    //Exam Mode
    private String bannerImage;
    private long duration;
    private boolean examMode;
    boolean lastQuestionDataSubmitted = false;

    private QuestionBaseRepository() {
    }

    public static QuestionBaseRepository getInstance() {
        if (instance == null)
            instance = new QuestionBaseRepository();
        return instance;
    }

    public MutableLiveData<QuestionBaseModel> getLiveData(Bundle bundle) {
        liveData = new MutableLiveData<>();
        challengerFinalScore = 0;
        scoresList = new ArrayList<>();
        fetchBundleData(bundle);
        return liveData;
    }

    public void fetchBundleData(Bundle dataBundle) {
        try {
            challengerFinalScore = 0;
            scoresList = new ArrayList<>();
            submitRequest = new SubmitRequest();
            activeUser = OustAppState.getInstance().getActiveUser();
            activeGame = OustAppState.getInstance().getActiveGame();
            if (dataBundle != null) {
                moduleId = dataBundle.getLong("moduleId");
                try {
                    String id = dataBundle.getString("courseId");
                    if (id != null) {
                        courseId = Long.parseLong(id);
                    } else {
                        courseId = dataBundle.getLong("courseId");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                    courseId = dataBundle.getLong("courseId");
                }

                if (courseId > 0) {
                    if (dataBundle.getLong("mappedAssessmentId") > 0) {
                        mappedAssessmentId = dataBundle.getLong("mappedAssessmentId");
                        Log.d(TAG, "fetchBundleData: mappedAssessmentId:" + mappedAssessmentId);
                    }
                }
                currentCplId = dataBundle.getLong("currentCplId");
                exitOC = dataBundle.getLong("exitOC");
                rewardOC = dataBundle.getLong("rewardOC");
                isMultipleCplEnable = dataBundle.getBoolean("isMultipleCplModule", false);
                activeGameId = dataBundle.getString("activeGameId");
                startDateTime = dataBundle.getString("startDateTime");
                moduleName = dataBundle.getString("moduleName");
                bgImage = dataBundle.getString("bgImage");
                bannerImage = dataBundle.getString("bannerImage");
                questionIndex = dataBundle.getInt("questionIndex");
                totalCards = dataBundle.getInt("totalCards");
                showNavigationArrow = dataBundle.getBoolean("showNavigationArrow");
                isSurvey = dataBundle.getBoolean("isSurvey");
                isAssessment = dataBundle.getBoolean("isAssessment");
                isReviewMode = dataBundle.getBoolean("isReviewMode");
                examMode = dataBundle.getBoolean("examMode");
                duration = dataBundle.getLong("examDuration");
                enrolledTime = dataBundle.getLong("enrolledTime");
                playResponse = (PlayResponse) dataBundle.getSerializable("playResponse");
                userPlayResponse = getUserPlayResponse(dataBundle.getString("userPlayResponse"));
                surveyAssociated = dataBundle.getBoolean("surveyAssociated");
                surveyMandatory = dataBundle.getBoolean("surveyMandatory");
                mappedSurveyId = dataBundle.getLong("mappedSurveyId");
                if (mappedSurveyId > 0) {
                    Log.d(TAG, "fetchBundleData: mappedSurveyId:" + mappedSurveyId);
                }

                associatedAssessmentId = dataBundle.getLong("associatedAssessmentId");
                if (userPlayResponse != null) {
                    scoresList = userPlayResponse.getScoresList();
                } else {
                    scoresList = new ArrayList<>();
                }
                containCertificate = dataBundle.getBoolean("containCertificate", false);
                isCplModule = dataBundle.getBoolean("isCplModule", false);
                totalTimeOfAssessment = dataBundle.getInt("totalTimeOfAssessment");
                isFromCourse = dataBundle.getBoolean("IS_FROM_COURSE", false);
                isMicroCourse = dataBundle.getBoolean("isMicroCourse", false);
                courseAssociated = dataBundle.getBoolean("courseAssociated", false);
                mappedCourseId = dataBundle.getLong("mappedCourseId");
                timePenaltyDisabled = dataBundle.getBoolean("timePenaltyDisabled", false);
                resumeSameQuestion = dataBundle.getBoolean("resumeSameQuestion", false);
                showAssessmentResultScore = dataBundle.getBoolean("showAssessmentResultScore", false);
                reAttemptAllowed = dataBundle.getBoolean("reAttemptAllowed", false);
                questionXp = dataBundle.getLong("questionXp", 20);
                nAttemptCount = dataBundle.getLong("nAttemptCount", 0);
                nAttemptAllowedToPass = dataBundle.getLong("nAttemptAllowedToPass", 0);
                secureSessionOn = dataBundle.getBoolean("secureSessionOn", true);

                if (BuildConfig.DEBUG) {
                    secureSessionOn = false;
                }

                isEventLaunch = dataBundle.getBoolean("isEventLaunch", false);
                eventId = dataBundle.getInt("eventId", 0);
                nCorrect = dataBundle.getInt("nCorrect", 0);
                nWrong = dataBundle.getInt("nWrong", 0);
                activeGameString = dataBundle.getString("ActiveGame");
                assessmentResp = dataBundle.getString("assessmentResp");
                submitRequestString = dataBundle.getString("SubmitRequest");

                Gson gson = new GsonBuilder().create();
                try {
                    this.gamePoints = gson.fromJson(dataBundle.getString("GamePoints"), GamePoints.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

            userEventAssessmentData = new UserEventAssessmentData();
            userEventAssessmentData.setEventId(eventId);
            if (isAssessment) {
                if (activeGameString != null && !activeGameString.isEmpty()) {
                    activeGame = OustSdkTools.getActiveGameData(activeGameString);
                }
                this.playResponse = OustAppState.getInstance().getPlayResponse();

                if (assessmentResp != null && !assessmentResp.isEmpty()) {
                    userPlayResponse = OustSdkTools.getAssessmentPlayResponse(assessmentResp);
                }
                if (userPlayResponse != null) {
                    checkForRepeatedQuestion(userPlayResponse);
                    try {
                        questionIndex = userPlayResponse.getQuestionIndex();
                        questionResumeTime = userPlayResponse.getResumeTime();
                    } catch (Exception e) {
                        questionResumeTime = 0;
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (userPlayResponse.getScoresList() != null && userPlayResponse.getScoresList().size() > 0) {
                        scoresList = userPlayResponse.getScoresList();
                    } else {
                        scoresList = new ArrayList<>();
                    }

                    challengerFinalScore = userPlayResponse.getChallengerFinalScore();
                } else {
                    if (isReviewMode && submitRequestString != null && !submitRequestString.isEmpty()) {
                        SubmitRequest submitRequest = new Gson().fromJson(submitRequestString, SubmitRequest.class);
                        if (submitRequest != null && submitRequest.getScores() != null && submitRequest.getScores().size() != 0) {
                            scoresList = submitRequest.getScores();
                            questionIndex = 0;
                        }
                    } else {
                        scoresList = new ArrayList<>();
                    }
                }
                totalCards = playResponse.getqIdList().size();
            }
            handleTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void checkForRepeatedQuestion(AssessmentPlayResponse assessmentPlayResponse) {
        try {
            if (assessmentPlayResponse.getScoresList() != null && assessmentPlayResponse.getScoresList().size() > 0) {
                List<Integer> qIdList = new ArrayList<>();
                Map<Integer, Integer> map = new HashMap<>();
                Map<Integer, Integer> remainMap = new HashMap<>();
                List<Scores> myScores = assessmentPlayResponse.getScoresList();

                for (int i = 0; i < myScores.size(); i++) {
                    if (myScores.get(i).getQuestion() != 0) {
                        map.put(myScores.get(i).getQuestion(), i);
                    }
                }
                for (int i = 0; i < playResponse.getqIdList().size(); i++) {
                    if (!map.containsKey(playResponse.getqIdList().get(i))) {
                        remainMap.put(i, playResponse.getqIdList().get(i));
                    }
                }
                TreeMap<Integer, Integer> sorted = new TreeMap<>(remainMap);
                for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    qIdList.add(entry.getKey());
                }
                for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
                    qIdList.add(entry.getValue());
                }
                playResponse.setqIdList(qIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void handleTransaction() {
        try {
            ArrayList<DTOQuestions> questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
            if (!isSurvey && isAssessment) {
                try {
                    if (questionsArrayList != null && questionsArrayList.size() > 0 && playResponse != null) {
                        List<Integer> qIdList = new ArrayList<>();
                        for (DTOQuestions dtoQuestions : questionsArrayList) {
                            qIdList.add((int) dtoQuestions.getQuestionId());
                        }
                        playResponse.setqIdList(qIdList);
                        totalCards = playResponse.getqIdList().size();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }
            if (playResponse != null && (playResponse.getqIdList() != null)) {
                questionBaseModel = new QuestionBaseModel();
                questionBaseModel.setModuleName(moduleName);
                questionBaseModel.setBgImage(bgImage);
                questionBaseModel.setTotalCards(totalCards);
                questionBaseModel.setShowNavigationArrow(showNavigationArrow);
                questionBaseModel.setSurvey(isSurvey);
                questionBaseModel.setAssessment(isAssessment);
                questionBaseModel.setReviewMode(isReviewMode);
                questionBaseModel.setSecureSessionOn(secureSessionOn);
                if (examMode) {
                    questionBaseModel.setExamMode(true);
                    questionBaseModel.setDuration(duration);
                    questionBaseModel.setBannerImage(bannerImage);
                    if (enrolledTime > 0) {
                        questionResumeTime = (System.currentTimeMillis() - enrolledTime) / 1000;
                        if (questionResumeTime > duration) {
                            questionResumeTime = 0;
                            questionBaseModel.setTimerEnd(true);
                        } else {
                            questionResumeTime = duration - questionResumeTime;
                        }
                    }
                    if (isReviewMode) {
                        OustStaticVariableHandling.getInstance().setScoresList(scoresList);
                    }
                }
                if (questionIndex >= playResponse.getqIdList().size() && isReviewMode) {
                    questionIndex = 0;
                }
                if (questionIndex < playResponse.getqIdList().size()) {
                    questionBaseModel.setQuestionIndex(questionIndex);
                    try {
                        if (isAssessment && (examMode || isReviewMode) && questionsArrayList != null && questionsArrayList.size() != 0 && questionIndex < questionsArrayList.size()) {
                            questions = questionsArrayList.get(questionIndex);
                        } else {
                            questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                        }
                    } catch (Exception e) {
                        questions = OustSdkTools.databaseHandler.getQuestionById(playResponse.getqIdList().get(questionIndex), false);
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    questionBaseModel.setQuestions(questions);
                    if (questions != null) {
                        if (questions.getBgImg() != null && !questions.getBgImg().isEmpty()) {
                            questionBaseModel.setBgImage(questions.getBgImg());
                        }
                    }
                    questionBaseModel.setCourseCardClass(getCourseCardClass());
                    questionBaseModel.setCourseLevelClass(getCourseLevelClass(questionIndex + 1));
                    questionBaseModel.setQuestionResumeTime(questionResumeTime);
                    questionBaseModel.setScores(getScores(scoresList, questionIndex, questions.getQuestionId()));
                    questionBaseModel.setType(1);
                    questionBaseModel.setShowLoader(false);
                    setAnswerModels();
                } else {
                    if (isSurvey) {
                        OustAppState.getInstance().setAllQuestionsAttempted(true);
                        questionBaseModel.setType(2);
                        questionIndex = 0;
                        questions = null;
                        questionBaseModel.setQuestions(null);
                        questionBaseModel.setCourseCardClass(null);
                        questionBaseModel.setCourseLevelClass(null);
                        questionBaseModel.setScores(null);

                    } else if (isAssessment && !isReviewMode) {
                        questionBaseModel.setShowLoader(true);
                        if (examMode) {
                            questionBaseModel.setType(3);
                        } else {
                            calculateFinalScore(false, "");
                        }
                    }
                }
                liveData.postValue(questionBaseModel);
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private AssessmentPlayResponse getUserPlayResponse(String userPlayResponse) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(userPlayResponse, AssessmentPlayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            return null;
        }
    }

    protected DTOCourseCard getCourseCardClass() {
        DTOCourseCard courseCardClass = new DTOCourseCard();
        courseCardClass.setXp(questionXp);
        return courseCardClass;
    }

    protected CourseLevelClass getCourseLevelClass(int questionIndex) {
        CourseLevelClass courseLevelClass = new CourseLevelClass();
        courseLevelClass.setLevelName("Level " + questionIndex);
        return courseLevelClass;
    }

    protected Scores getScores(List<Scores> scoresList, int questionIndex, long questionId) {
        if (scoresList != null && questionIndex == scoresList.size()) {
            Scores scores = new Scores();
            scoresList.add(questionIndex, scores);
            this.scoresList = scoresList;
            OustStaticVariableHandling.getInstance().setScoresList(scoresList);
            return scores;
        } else {
            if (scoresList != null && scoresList.size() != 0) {
                if (questionIndex < scoresList.size()) {
                    Scores score = scoresList.get(questionIndex);
                    for (Scores scores : scoresList) {
                        if (examMode && scores.getQuestionSerialNo() == questionIndex + 1) {
                            score = scores;
                        } else if (scores.getQuestion() == questionId) {
                            score = scores;
                        }
                    }
                    return score;
                } else {
                    int size = (questionIndex - scoresList.size() + 1);
                    for (int i = 0; i < size; i++) {
                        Scores scores = new Scores();
                        scoresList.add(scores);
                        this.scoresList = scoresList;
                        OustStaticVariableHandling.getInstance().setScoresList(scoresList);
                    }
                    return new Scores();
                }
            } else {
                Scores scores = new Scores();
                try {
                    scoresList = new ArrayList<>();
                    scoresList.add(questionIndex, scores);
                    this.scoresList = scoresList;
                    OustStaticVariableHandling.getInstance().setScoresList(scoresList);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                return scores;
            }
        }
    }

    public void setAnswerModels() {
        try {
            if (questions != null) {
                String answerType = "Text";
                if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.IMAGE_CHOICE)) {
                    answerType = "Image";
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if (questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setImage(questions.getImageChoiceA().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setImage(questions.getImageChoiceB().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setOptionType("Image");
                        answerModel.setImage(questions.getImageChoiceC().getImageData());
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setOptionType("Image");
                        answerModel.setImage(questions.getImageChoiceD().getImageData());
                        answerModels.add(answerModel);
                    }
                    if (questions.getImageChoiceE() != null && questions.getImageChoiceE().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setOptionType("Image");
                        answerModel.setImage(questions.getImageChoiceE().getImageData());
                        answerModels.add(answerModel);
                    }
                } else if (questions.getQuestionCategory() != null && questions.getQuestionCategory().equalsIgnoreCase(QuestionCategory.IMAGE_TEXT)) {
                    answerType = "Image_Text";
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if (questions.getImageChoiceA() != null && questions.getImageChoiceA().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setImage(questions.getImageChoiceA().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceB() != null && questions.getImageChoiceB().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setImage(questions.getImageChoiceB().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceC() != null && questions.getImageChoiceC().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setImage(questions.getImageChoiceC().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceD() != null && questions.getImageChoiceD().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setImage(questions.getImageChoiceD().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if (questions.getImageChoiceE() != null && questions.getImageChoiceE().getImageData() != null) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setImage(questions.getImageChoiceE().getImageData());
                        answerModel.setOptionType("Image");
                        answerModels.add(answerModel);
                    } else if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                    if ((questions.getF() != null) && (!questions.getF().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("F");
                        answerModel.setAnswerOption(questions.getF());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }

                } else {
                    answerModels = new ArrayList<>();
                    QuestionAnswerModel answerModel;
                    if ((questions.getA() != null) && (!questions.getA().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("A");
                        answerModel.setAnswerOption(questions.getA());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getB() != null) && (!questions.getB().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("B");
                        answerModel.setAnswerOption(questions.getB());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getC() != null) && (!questions.getC().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("C");
                        answerModel.setAnswerOption(questions.getC());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getD() != null) && (!questions.getD().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("D");
                        answerModel.setAnswerOption(questions.getD());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getE() != null) && (!questions.getE().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("E");
                        answerModel.setAnswerOption(questions.getE());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                    if ((questions.getF() != null) && (!questions.getF().isEmpty())) {
                        answerModel = new QuestionAnswerModel();
                        answerModel.setOption("F");
                        answerModel.setAnswerOption(questions.getF());
                        answerModel.setOptionType("Text");
                        answerModels.add(answerModel);
                    }
                }
                questionBaseModel.setAnswerModels(answerModels);
                questionBaseModel.setAnswerType(answerType);
                //  liveData.postValue(questionBaseModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void setQuestionLocalTime(long questionLocalTime) {
        this.questionLocalTime = questionLocalTime;
    }

    public void setMediaUpload(String checkMediaVal) {
        this.checkMediaVal = checkMediaVal;
    }

    public void submitGameOnBackPress() {
        try {
            if (questionBaseModel != null)
                if (questionBaseModel.isSurvey()) {
                    if (!OustAppState.getInstance().isDisableBackButton()) {
                        pauseSurveyGame();
                    }
                } else if (questionBaseModel.isAssessment()) {
                    if (playResponse != null) {
                        if (OustAppState.getInstance().isAssessmentGame()) {
                            pauseAssessmentGame();
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void calculateFinalScore(boolean isSurveyExit, String exitMessage) {

        if (questionBaseModel.isSurvey()) {
            OustAppState.getInstance().setAssessmentRunning(false);
            OustAppState.getInstance().setSurveyExit(isSurveyExit);
            OustAppState.getInstance().setExitMessage(exitMessage);
            if (activeUser != null && activeGame != null) {
                try {
                    activeGame.setStudentid(activeUser.getStudentid());
                    activeGame.setChallengerid(activeUser.getStudentid());
                    submitRequest = new SubmitRequest();
                    submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
                    submitRequest.setTotalscore(0);
                    submitRequest.setScores(scoresList);
                    submitRequest.setEndTime(TimeUtils.getCurrentDateAsString());
                    submitRequest.setStartTime(startDateTime);
                    submitRequest.setExternal(false);
                    submitRequest.setChallengerid(activeUser.getStudentid());
                    submitRequest.setGroupId("");
                    submitRequest.setOpponentid("");
                    if (isSurveyExit) {
                        if (exitOC > 0) {
                            submitRequest.setExitOC(exitOC);
                        }
                    } else {
                        if (rewardOC > 0) {
                            submitRequest.setRewardOC(rewardOC);
                        }
                    }
                    String gcmToken = OustPreferences.get("gcmToken");
                    if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                        submitRequest.setDeviceToken(gcmToken);
                    }
                    submitRequest.setStudentid(activeUser.getStudentid());
                    submitRequest.setAssessmentId(("" + moduleId));
                    if (courseId != 0) {
                        submitRequest.setCourseId("" + courseId);
                    }
                    submitScore(submitRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
            }

        } else if (questionBaseModel.isAssessment()) {
            if (activeUser != null && activeGame != null) {
                try {
                    activeGame.setStudentid(activeUser.getStudentid());
                    activeGame.setChallengerid(activeUser.getStudentid());
                    submitRequest = new SubmitRequest();
                    submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
                    submitRequest.setWinner("-1");
                    totalTimeTaken = 0;
                    boolean questionFound;
                    questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
                    if (questionsArrayList != null && questionsArrayList.size() > 0) {
                        for (int i = 0; i < questionsArrayList.size(); i++) {
                            questionFound = false;
                            if (scoresList != null && scoresList.size() > 0) {
                                for (int j = 0; j < scoresList.size(); j++) {
                                    if (questionsArrayList.get(i).getQuestionId() == scoresList.get(j).getQuestion()) {
                                        questionFound = true;
                                        break;
                                    }
                                }
                            }
                            if (!questionFound) {
                                Scores scores = new Scores();
                                scores.setAnswer("");
                                scores.setCorrect(false);
                                scores.setXp(0);
                                scores.setScore(0);
                                scores.setQuestion((int) questionsArrayList.get(i).getQuestionId());
                                scores.setQuestionType(questionsArrayList.get(i).getQuestionType());
                                scores.setQuestionSerialNo(i + 1);
                                scores.setSurveyQuestion(false);
                                scores.setUserSubjectiveAns("");
                                scores.setRemarks("");
                                scores.setQuestionMedia("");
                                scores.setExitStatus("SKIPPED");
                                scores.setTime(0);
                                if (scoresList.size() > i && scoresList.get(i) != null) {
                                    scoresList.set(i, scores);
                                } else if (scoresList.size() - 1 == i && scoresList.get(i) != null) {
                                    scoresList.set(i, scores);
                                } else {
                                    scoresList.add(i, scores);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "calculateFinalScore: scoresList--> " + scoresList.toString());
                    for (int i = 0; i < scoresList.size(); i++) {
                        totalTimeTaken += scoresList.get(i).getTime();
                    }
                    float value = ((totalTimeOfAssessment - totalTimeTaken) / totalTimeOfAssessment);
                    float bonusXp = Math.round(value * questionXp);
                    if (challengerFinalScore > 0 && bonusXp > 0 && (!timePenaltyDisabled)) {
                        challengerFinalScore += Math.round(bonusXp);
                    }
                    submitRequest.setTotalscore(challengerFinalScore);
                    submitRequest.setScores(scoresList);
                    submitRequest.setEndTime(TimeUtils.getCurrentDateAsString());
                    submitRequest.setStartTime(startDateTime);
                    submitRequest.setMobileNum("");
                    submitRequest.setDeepLink("");
                    submitRequest.setExternal(false);
                    if (activeGame.getGameType() == GameType.GROUP) {
                        submitRequest.setOpponentid("-1");
                        submitRequest.setGroupId(activeGame.getGroupId());
                    } else if (activeGame.getGameType() == GameType.ACCEPT && (activeGame.getGroupId() != null && activeGame.getGroupId().length() > 0)) {
                        submitRequest.setGroupId(activeGame.getGroupId());
                        submitRequest.setOpponentid("-1");
                    } else if (activeGame.getGameType() == GameType.ACCEPT) {
                        submitRequest.setGroupId("");
                        submitRequest.setOpponentid(activeGame.getChallengerid());
                    } else if (activeGame.getGameType() == GameType.MYSTERY && !activeGame.getOpponentid().equalsIgnoreCase("mystery")
                            && !activeGame.getChallengerid().equalsIgnoreCase("mystery")) {
                        submitRequest.setGroupId("");
                        submitRequest.setOpponentid(activeGame.getChallengerid());
                    } else if (activeGame.getGameType() == GameType.MYSTERY && activeGame.getOpponentid().equalsIgnoreCase("mystery")) {
                        submitRequest.setGroupId("");
                        submitRequest.setOpponentid("-1");
                    } else {
                        submitRequest.setGroupId("");
                        submitRequest.setOpponentid(activeGame.getOpponentid());
                    }
                    if (activeGame.getGameType() == GameType.MYSTERY && submitRequest.getWinner().equalsIgnoreCase("-1")) {
                        submitRequest.setOpponentid("-1");
                    }
                    if (activeGame.getGameType() == GameType.ACCEPT && (activeGame.getGroupId() != null
                            && activeGame.getGroupId().length() > 0)) {
                        submitRequest.setChallengerid(activeGame.getChallengerid());
                    } else if (activeGame.getGameType() == GameType.ACCEPT) {
                        submitRequest.setChallengerid(activeGame.getOpponentid());
                    } else if (activeGame.getGameType() == GameType.MYSTERY && !activeGame.getOpponentid().equalsIgnoreCase("mystery")
                            && !activeGame.getChallengerid().equalsIgnoreCase("mystery")) {
                        submitRequest.setChallengerid(activeGame.getOpponentid());
                    } else if (activeGame.getGameType() == GameType.MYSTERY && activeGame.getOpponentid().equalsIgnoreCase("mystery")) {
                        submitRequest.setChallengerid(activeGame.getChallengerid());
                    } else if (activeGame.getGameType() == GameType.CONTACTSCHALLENGE) {
                        submitRequest.setOpponentid(activeGame.getOpponentid());
                        submitRequest.setMobileNum(activeGame.getMobileNum());
                        submitRequest.setChallengerid(activeGame.getChallengerid());
                        submitRequest.setExternal(true);
                    } else {
                        submitRequest.setChallengerid(activeGame.getChallengerid());
                    }
                    String gcmToken = OustPreferences.get("gcmToken");
                    if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                        submitRequest.setDeviceToken(gcmToken);
                    }
                    submitRequest.setStudentid(activeUser.getStudentid());
                    submitRequest.setAssessmentId(OustPreferences.get("current_assessmentId"));
                    submitRequest.setContentPlayListId(OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId());
                    if (courseId != 0) {
                        submitRequest.setCourseId(String.valueOf(courseId));
                    }

                    userPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), scoresList, activeGame.getGameid(),
                            challengerFinalScore + "", submitRequest.getWinner(), submitRequest.getEndTime(), submitRequest.getStartTime(),
                            submitRequest.getChallengerid(), submitRequest.getOpponentid(), AssessmentState.COMPLETED);
                    saveCompleteAssessmentGame(activeUser);
                    userPlayResponse.setResumeTime("" + 0);
                    userPlayResponse.setQuestionIndex(questionIndex);
                    try {
                        if (examMode) {
                            userPlayResponse.setCurrentQuestionId((int) questions.getQuestionId());
                        } else {
                            if (scoresList != null && scoresList.size() > 0) {
                                Scores scores = scoresList.get(scoresList.size() - 1);
                                if (scores.getQuestion() != 0) {
                                    userPlayResponse.setCurrentQuestionId((int) scores.getQuestion());
                                } else {
                                    for (int i = 0; i < scoresList.size(); i++) {
                                        if (questionIndex - 1 == i) {
                                            userPlayResponse.setCurrentQuestionId((int) scoresList.get(i).getQuestion());
                                            break;
                                        }
                                    }
                                    if (userPlayResponse.getCurrentQuestionId() == 0) {
                                        userPlayResponse.setCurrentQuestionId((int) questions.getQuestionId());
                                    }
                                }
                            } else {
                                userPlayResponse.setCurrentQuestionId(0);
                            }
                        }
                    } catch (Exception e) {
                        userPlayResponse.setCurrentQuestionId(0);
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                    if (rewardOC > 0) {
                        submitRequest.setRewardOC(rewardOC);
                    }
                    saveAssessmentGame(userPlayResponse, activeUser, submitRequest);
//                    saveAndSubmitRequest(submitRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

            }
        }
    }

    public void saveCompleteAssessmentGame(ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + "completedAssessments/" + activeUser.getStudentKey() + "/assessment" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId();
            OustFirebaseTools.getRootRef().child(node).setValue("true");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void pauseSurveyGame() {
        try {
            AssessmentCopyResponse surveyPlayResponse = new AssessmentCopyResponse();
            surveyPlayResponse.setTotalQuestion("" + totalCards);
            surveyPlayResponse.setStudentId(activeUser.getStudentid());
            surveyPlayResponse.setGameId("" + activeGameId);
            if (courseId != 0) {
                surveyPlayResponse.setCourseId("" + courseId);
            }
            if (userPlayResponse != null) {
                surveyPlayResponse.setChallengerFinalScore("" + userPlayResponse.getChallengerFinalScore());
            } else {
                surveyPlayResponse.setChallengerFinalScore("" + 0);
            }

            surveyPlayResponse.setQuestionIndex("" + questionIndex);
            surveyPlayResponse.setAssessmentState(AssessmentState.INPROGRESS);
            List<ScoresCopy> scoresCopies = new ArrayList<>();
            if (scoresList != null) {
                for (int j = 0; j < scoresList.size(); j++) {
                    try {
                        ScoresCopy scoresCopy = new ScoresCopy();
                        if (scoresList.get(j).getAnswer() != null && !scoresList.get(j).getAnswer().isEmpty() && !scoresList.get(j).getAnswer().contains("null")) {
                            scoresCopy.setAnswer(scoresList.get(j).getAnswer());
                        } else {
                            scoresCopy.setAnswer("");
                        }
                        scoresCopy.setCorrect(scoresList.get(j).isCorrect());
                        scoresCopy.setGrade(scoresList.get(j).getGrade());
                        scoresCopy.setModuleId(scoresList.get(j).getModuleId());
                        scoresCopy.setQuestion("" + scoresList.get(j).getQuestion());
                        scoresCopy.setQuestionSerialNo("" + scoresList.get(j).getQuestionSerialNo());
                        scoresCopy.setQuestionType(scoresList.get(j).getQuestionType());
                        scoresCopy.setScore("" + scoresList.get(j).getScore());
                        scoresCopy.setSubject(scoresList.get(j).getSubject());
                        scoresCopy.setTime("" + scoresList.get(j).getTime());
                        scoresCopy.setTopic("" + scoresList.get(j).getTopic());
                        scoresCopy.setUserSubjectiveAns("" + scoresList.get(j).getUserSubjectiveAns());
                        scoresCopy.setXp("" + scoresList.get(j).getXp());
                        scoresCopy.setRemarks("" + scoresList.get(j).getRemarks());
                        scoresCopy.setQuestionMedia("" + scoresList.get(j).getQuestionMedia());
                        scoresCopies.add(scoresCopy);
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }
                }
            }
            surveyPlayResponse.setScoresList(scoresCopies);
            saveSurveyGame(surveyPlayResponse, activeUser);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveSurveyGame(AssessmentCopyResponse surveyPlayResponse, ActiveUser activeUser) {
        try {
            String node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/surveyAssessment" + moduleId;
            if (isMultipleCplEnable && currentCplId != 0) {
                node = "/userAssessmentProgress/" + activeUser.getStudentKey() + "/multipleCPL/" + currentCplId + "/contentListMap/assessment" + moduleId;
            } else {
                if (courseId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/course" + courseId + "/surveyAssessment" + moduleId;
                } else if (associatedAssessmentId != 0) {
                    node = "/userSurveyProgress/" + activeUser.getStudentKey() + "/assessment" + associatedAssessmentId + "/surveyAssessment" + moduleId;
                }
            }
            OustFirebaseTools.getRootRef().child(node).setValue(surveyPlayResponse);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setAnswerAndOc(String userAns, String subjectiveResponse, int oc,
                               boolean status, long time, String remarks, String questionMedia, boolean questionTimeOut) {
        try {
            Scores scores = new Scores();
            if (userAns != null && !userAns.isEmpty() && !userAns.contains("null")) {
                scores.setAnswer((userAns));
            } else {
                scores.setAnswer("");
            }
            scores.setCorrect(status);
            if (oc < 0) {
                oc = 0;
            }
            if (!questions.isSurveyQuestion()) {
                if (status) {
                    nCorrect++;
                } else {
                    nWrong++;
                }
            }
            challengerFinalScore += oc;
            scores.setXp(oc);
            scores.setScore(oc);
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questions.getQuestionType());
            scores.setQuestionSerialNo((questionIndex + 1));
            scores.setSurveyQuestion(questions.isSurveyQuestion());
            scores.setUserSubjectiveAns(subjectiveResponse);
            scores.setRemarks(remarks);
            scores.setQuestionMedia(questionMedia);
            // add in array
            scores.setTime(time);
            if (questionTimeOut) {
                scores.setExitStatus("TIMEOUT");
            } else {
                scores.setExitStatus("");
            }

            //QuestionView for Exam Mode
            QuestionView questionView = new QuestionView();
            questionView.setQuestionId((int) questions.getQuestionId());
            questionView.setAnswered(true);

            if (scoresList == null)
                scoresList = new ArrayList<>();

            if (questionViewList == null)
                questionViewList = new HashMap<>();
            try {
                if (scoresList.size() > questionIndex && scoresList.get(questionIndex) != null && (scoresList.get(questionIndex).getAnswer() != null || examMode)) {
                    scoresList.set(questionIndex, scores);
                } else if (scoresList.size() - 1 == questionIndex && scoresList.get(questionIndex) != null) {
                    scoresList.set(questionIndex, scores);
                } else {
                    scoresList.add(questionIndex, scores);
                }
            } catch (Exception e) {
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            questionViewList.put(questionView.getQuestionId(), questionView);

            OustStaticVariableHandling.getInstance().setScoresList(scoresList);
            if (examMode) {
                OustStaticVariableHandling.getInstance().setQuestionViewList(questionViewList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoNextScreenForSurvey() {
        try {
            if (questionIndex < scoresList.size() && scoresList.get(questionIndex) != null) {
                questionIndex++;
                handleTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void gotoNextScreenForAssessment() {
        try {
            if (!isReviewMode) {
                saveAssessmentForEachQuestion();
            }

            if (examMode) {
                ArrayList<DTOQuestions> questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
                if (questionViewList != null && questionsArrayList != null && questionsArrayList.size() != 0 && questionsArrayList.size() > questionIndex) {
                    QuestionView questionView;
                    if (questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId()) != null) {
                        questionView = questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId());
                        Objects.requireNonNull(questionView).setViewed(true);
                    } else {
                        questionView = new QuestionView();
                        questionView.setQuestionId((int) questionsArrayList.get(questionIndex).getQuestionId());
                        questionView.setViewed(true);
                        questionViewList.put((int) questionsArrayList.get(questionIndex).getQuestionId(), questionView);
                    }
                    OustStaticVariableHandling.getInstance().setQuestionViewList(questionViewList);
                }
            }
            if (questionIndex < scoresList.size() && scoresList.get(questionIndex) != null) {
                questionIndex++;
                questionResumeTime = 0;
                handleTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleExamModeForAssessment(int position) {
        try {
            if (!isReviewMode) {
                saveAssessmentForEachQuestion();
            }

            if (examMode) {
                ArrayList<DTOQuestions> questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
                if (questionViewList != null && questionsArrayList != null && questionsArrayList.size() != 0 && questionsArrayList.size() > questionIndex) {
                    QuestionView questionView;
                    if (questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId()) != null) {
                        questionView = questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId());
                        Objects.requireNonNull(questionView).setViewed(true);
                    } else {
                        questionView = new QuestionView();
                        questionView.setQuestionId((int) questionsArrayList.get(questionIndex).getQuestionId());
                        questionView.setViewed(true);
                        questionViewList.put((int) questionsArrayList.get(questionIndex).getQuestionId(), questionView);
                    }
                    OustStaticVariableHandling.getInstance().setQuestionViewList(questionViewList);
                }
            }

            questionIndex = position;
            questionResumeTime = 0;
            handleTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void gotoPreviousScreen() {
        if (examMode) {
            ArrayList<DTOQuestions> questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
            if (questionViewList != null && questionsArrayList != null && questionsArrayList.size() != 0 && questionsArrayList.size() > questionIndex) {
                QuestionView questionView;
                if (questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId()) != null) {
                    questionView = questionViewList.get((int) questionsArrayList.get(questionIndex).getQuestionId());
                    Objects.requireNonNull(questionView).setViewed(true);
                } else {
                    questionView = new QuestionView();
                    questionView.setQuestionId((int) questionsArrayList.get(questionIndex).getQuestionId());
                    questionView.setViewed(true);
                    questionViewList.put((int) questionsArrayList.get(questionIndex).getQuestionId(), questionView);
                }
                OustStaticVariableHandling.getInstance().setQuestionViewList(questionViewList);
            }
        }
        if (questionIndex > 0) {
            questionIndex--;
            handleTransaction();
        }
    }

    public void submitScore(SubmitRequest submitRequest) {
        try {
            String submitGameUrl = OustSdkApplication.getContext().getResources().getString(R.string.submit_game);
            if (isAssessment) {
                try {
                    currentCplId = OustPreferences.getTimeForNotification("cplId_assessment");
                    if (currentCplId == 0) {
                        if (isCplModule) {
                            currentCplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                        }
                    }
                    submitRequest.setContentPlayListId(currentCplId);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                if (submitRequest.getCourseId() != null && !submitRequest.getCourseId().isEmpty() && mappedSurveyId > 0) {
                    submitGameUrl = submitGameUrl + "mappedSurveyId=" + mappedSurveyId;
                }
                lastQuestionDataSubmitted = true;
            } else if (isSurvey) {
                if (currentCplId > 0) {
                    submitRequest.setContentPlayListId(currentCplId);
                } else if (submitRequest.getCourseId() != null && !submitRequest.getCourseId().isEmpty() && mappedAssessmentId > 0) {
                    submitGameUrl = submitGameUrl + "mappedAssessmentId=" + mappedAssessmentId;
                }
            }

            try {
                if (submitRequest.getChallengerid() == null || submitRequest.getChallengerid().isEmpty()) {
                    if (activeUser != null && activeUser.getStudentid() != null && !activeUser.getStudentid().isEmpty()) {
                        submitRequest.setChallengerid(activeUser.getStudentid());
                    } else {
                        activeUser = OustAppState.getInstance().getActiveUser();
                        submitRequest.setChallengerid(activeUser.getStudentid());
                    }
                }
            } catch (Exception e) {
                activeUser = OustAppState.getInstance().getActiveUser();
                submitRequest.setChallengerid(activeUser.getStudentid());
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(submitRequest);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);

            submitGameUrl = HttpManager.getAbsoluteUrl(submitGameUrl);
            Log.d(TAG, "submitScore: submitGameUrl-> " + submitGameUrl);
            Log.d(TAG, "submitScore: parsedJsonParams-> " + parsedJsonParams);

            ApiCallUtils.doNetworkCallForSubmitGame(Request.Method.POST, submitGameUrl, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    SubmitResponse submitResponse = gson.fromJson(response.toString(), SubmitResponse.class);
                    submitRequestProcessFinish(submitResponse);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    submitRequestProcessFinish(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        try {
            OustAppState.getInstance().setAssessmentRunning(false);
            if (submitResponse != null) {
                if (submitResponse.isSuccess()) {
                    if (isSurvey) {
                        questionBaseModel.setSurveySubmitted(true);
                        OustAppState.getInstance().setSurveySubmitted(true);
                        liveData.postValue(questionBaseModel);
                    } else if (isAssessment) {
                        OustPreferences.save("lastgamesubmitrequest", "");
                        if (OustAppState.getInstance().isAssessmentGame()) {
                            userPlayResponse.setAssessmentState(AssessmentState.SUBMITTED);
                            userPlayResponse.setResumeTime("" + 0);
                            userPlayResponse.setQuestionIndex(0);
                            //saveAssessmentGame(userPlayResponse, activeUser);
                        }

                        OustAppState.getInstance().setPlayResponse(this.playResponse);
                        assessmentCompleted(activeGame, activeUser, submitRequest, gamePoints);
                    }
                } else {
                    OustSdkTools.handlePopup(submitResponse);
                }
            } else {
                if (isAssessment) {
                    OustPreferences.save("lastgamesubmitrequest", "");
                    if (OustAppState.getInstance().isAssessmentGame()) {
                        if (userPlayResponse != null) {
                            userPlayResponse.setAssessmentState(AssessmentState.SUBMITTED);
                            userPlayResponse.setResumeTime("" + 0);
                            userPlayResponse.setQuestionIndex(0);
                        }
                        //saveAssessmentGame(userPlayResponse, activeUser);
                    }
                    OustAppState.getInstance().setPlayResponse(this.playResponse);
                    assessmentCompleted(activeGame, activeUser, submitRequest, gamePoints);
                } else if (isSurvey) {
                    questionBaseModel.setSurveySubmitted(true);
                    OustAppState.getInstance().setSurveySubmitted(false);
                    liveData.postValue(questionBaseModel);
                }
           /* if (isAssessment) {
                questionBaseModel.setType(4);
                liveData.setValue(questionBaseModel);
            }
            OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));*/
            }
        } catch (Exception e) {
            if (isAssessment) {
                questionBaseModel.setType(4);
                liveData.setValue(questionBaseModel);
            }
            OustSdkTools.showToast(OustStrings.getString("retry_internet_msg"));
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void handleReview() {
        questionIndex = 0;
        handleTransaction();
    }

    public void saveAssessmentForEachQuestion() {
        try {
            userPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), (questionIndex + 1) + "", scoresList,
                    activeGame.getGameid(), challengerFinalScore + "", AssessmentState.INPROGRESS);
            userPlayResponse.setResumeTime("" + 0);
            userPlayResponse.setTotalQuestion(totalCards);
            try {
                if (scoresList != null && scoresList.size() > 0) {
                    Scores scores = scoresList.get(scoresList.size() - 1);
                    userPlayResponse.setCurrentQuestionId((int) scores.getQuestion());
                } else {
                    userPlayResponse.setCurrentQuestionId(0);
                }
            } catch (Exception e) {
                userPlayResponse.setCurrentQuestionId(0);
                e.printStackTrace();
                OustSdkTools.sendSentryException(e);
            }
            saveAssessmentGame(userPlayResponse, activeUser, null);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void pauseAssessmentGame() {
        try {
            Log.d(TAG, "pauseAssessmentGame: lastQuestionDataSubmitted--> " + lastQuestionDataSubmitted + " questionIndex--> " + questionIndex
                    + " scoresList---> " + scoresList.size());
            if (lastQuestionDataSubmitted) {
                lastQuestionDataSubmitted = false;
                return;
            }
            boolean lastQuestionStatus = false;
            if (questions != null) {
                ArrayList<DTOQuestions> questionsArrayList = OustStaticVariableHandling.getInstance().getQuestionsArrayList();
                Scores scores = new Scores();
                scores.setAnswer("");
                scores.setCorrect(false);
                scores.setSubject(questions.getSubject());
                scores.setTopic(questions.getTopic());
                scores.setTime(questionLocalTime);
                scores.setScore(challengerFinalScore);
                scores.setQuestion((int) questions.getQuestionId());
                scores.setQuestionType(questions.getQuestionType());
                scores.setQuestionSerialNo((questionIndex + 1));
                scores.setScore(0);
                scores.setExitStatus("");
                nWrong++;
                playResponse.setCorrectAnswerCount(nCorrect);
                playResponse.setWrongAnswerCount(nWrong);
                scores.setXp(0);
                try {
                    scoresList.set(questionIndex, scores);
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                if (userPlayResponse != null) {
                    userPlayResponse.setResumeTime("" + 0);
                }
                if (questionsArrayList != null && questionsArrayList.size() != 0) {
                    Scores checkLastScore = scoresList.get(scoresList.size() - 1);
                    if (checkLastScore.getQuestion() == questions.getQuestionId()) {
                        lastQuestionStatus = true;
                    }
                }
                questionIndex++;
            }
            if ((questionIndex < playResponse.getqIdList().size()) || lastQuestionStatus) {
                userPlayResponse = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex + "", scoresList,
                        activeGame.getGameid(), challengerFinalScore + "", AssessmentState.INPROGRESS);

                if (questionResumeTime != 0 && questionResumeTime > questionLocalTime) {
                    userPlayResponse.setResumeTime("" + (questionResumeTime - questionLocalTime));
                } else {
                    if (examMode) {
                        userPlayResponse.setResumeTime("" + (questionBaseModel.getDuration() - questionLocalTime));
                    } else {
                        userPlayResponse.setResumeTime("" + (questions.getMaxtime() - questionLocalTime));
                    }
                }

                userPlayResponse.setCommentMediaUploadedPath(checkMediaVal);
                userPlayResponse.setTotalQuestion(totalCards);
                try {
                    if (examMode) {
                        userPlayResponse.setCurrentQuestionId((int) questions.getQuestionId());
                    } else {
                        if (scoresList != null && scoresList.size() > 0) {
                            Scores scores = scoresList.get(scoresList.size() - 1);
                            if (scores.getQuestion() != 0) {
                                userPlayResponse.setCurrentQuestionId((int) scores.getQuestion());
                            } else {
                                for (int i = 0; i < scoresList.size(); i++) {
                                    if (questionIndex - 1 == i) {
                                        userPlayResponse.setCurrentQuestionId((int) scoresList.get(i).getQuestion());
                                        break;
                                    }
                                }
                                if (userPlayResponse.getCurrentQuestionId() == 0) {
                                    userPlayResponse.setCurrentQuestionId((int) questions.getQuestionId());
                                }
                            }
                        } else {
                            userPlayResponse.setCurrentQuestionId(0);
                        }
                    }
                } catch (Exception e) {
                    userPlayResponse.setCurrentQuestionId(0);
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }
                saveAssessmentGame(userPlayResponse, activeUser, null);

                if ((isEventLaunch && OustStaticVariableHandling.getInstance().getOustApiListener() != null) || isFromCourse) {
                    userEventAssessmentData.setAssessmentId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                    userEventAssessmentData.setnTotalQuestions(playResponse.getqIdList().size());

                    int answered = questionIndex + 1;
                    if (resumeSameQuestion) {
                        answered = questionIndex;
                    }
                    userEventAssessmentData.setnQuestionAnswered(answered);
                    userEventAssessmentData.setnQuestionWrong(nWrong);
                    userEventAssessmentData.setnQuestionCorrect(nCorrect);
                    userEventAssessmentData.setnQuestionSkipped(Math.max(answered - (nCorrect + nWrong), 0));
                    userEventAssessmentData.setEventId(eventId);
                    userEventAssessmentData.setUserProgress("InProgress");
                    try {
                        int percentage = (int) ((answered / (float) userEventAssessmentData.getnTotalQuestions()) * 100);
                        userEventAssessmentData.setUserCompletionPercentage(percentage);
                        if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                                OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(percentage);
                                OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                            }

                            if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                                OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(percentage);
                                OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                    }

                    if (!isFromCourse) {
                        if (isCplModule) {
                            UserEventCplData userEventCplData = new UserEventCplData();
                            userEventCplData.setCurrentModuleType("Assessment");
                            userEventCplData.setEventId(eventId);
                            userEventCplData.setCurrentModuleId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                            userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                            final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                            final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                            userEventCplData.setnTotalModules(totalModules);
                            userEventCplData.setnModulesCompleted(completedModules);
                            userEventCplData.setUserProgress("InProgress");
                            userEventCplData.setCurrentModuleProgress("InProgress");
                            userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                            OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                        } else {
                            OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                        }
                    } else {
                        userEventAssessmentData.setEventId(0);
                        OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);
                    }
                }
                if (isFromCourse && isMicroCourse) {
                    try {
                        OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        OustSdkTools.sendSentryException(e1);
                    }
                }
            } else {
                calculateFinalScore(false, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (isFromCourse && isMicroCourse) {
                try {
                    OustAppState.getInstance().getLearningCallBackInterface().startUpdatedLearningMap(true, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    OustSdkTools.sendSentryException(e1);
                }
            }
        }
    }

    public void saveAssessmentGame(AssessmentPlayResponse userPlayResponse, ActiveUser activeUser, SubmitRequest submitRequest) {
        try {
            userPlayResponse.setCommentMediaUploadedPath(null);
            userPlayResponse.setStartTime(startDateTime);
            if (userPlayResponse.getStudentId() != null) {
                userPlayResponse.setChallengerid(userPlayResponse.getStudentId());
            } else if (activeUser != null && activeUser.getStudentid() != null && !activeUser.getStudentid().isEmpty()) {
                userPlayResponse.setChallengerid(activeUser.getStudentid());
            } else {
                activeUser = OustAppState.getInstance().getActiveUser();
                userPlayResponse.setChallengerid(activeUser.getStudentid());
            }
            if (userPlayResponse.getScoresList() == null || userPlayResponse.getScoresList().size() == 0) {
                userPlayResponse.setScoresList(new ArrayList<>());
                userPlayResponse.setQuestionIndex(0);
            }

            if ((courseId != 0)) {
                userPlayResponse.setCourseId(courseId);
            }

            currentCplId = OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId();
            if (currentCplId == 0) {
                currentCplId = OustPreferences.getTimeForNotification("cplId_assessment");
                if (currentCplId == 0) {
                    if (isCplModule) {
                        currentCplId = OustSdkTools.convertToLong(OustPreferences.get("main_cpl_id"));
                    }
                }
            }
            userPlayResponse.setContentPlayListId(currentCplId);

            String update_userAssessment_url = OustSdkApplication.getContext().getResources().getString(R.string.update_userAssessment_url) + moduleId;
            update_userAssessment_url = HttpManager.getAbsoluteUrl(update_userAssessment_url);

            Gson gson = new GsonBuilder().create();
            String jsonParams = gson.toJson(userPlayResponse);
            JSONObject parsedJsonParams = OustSdkTools.getRequestObject(jsonParams);
            Log.d(TAG, "saveAssessmentGame: updateUserAssessmentProgress --> " + parsedJsonParams);

            ApiCallUtils.doNetworkCall(Request.Method.POST, update_userAssessment_url, parsedJsonParams, new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            if (response.optBoolean("success")) {
                                Log.e("AssessmentProgress", "Response is success");
                            } else {
                                Log.e("AssessmentProgress", "Response is error");
                            }
                        } else {
                            Log.e("AssessmentProgress", "Response is null");
                        }
                        if (submitRequest != null) {
                            saveAndSubmitRequest(submitRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        OustSdkTools.sendSentryException(e);
                        if (submitRequest != null) {
                            saveAndSubmitRequest(submitRequest);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("AssessmentProgress", "request or api error");
                    if (submitRequest != null) {
                        saveAndSubmitRequest(submitRequest);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            if (submitRequest != null) {
                saveAndSubmitRequest(submitRequest);
            }
        }
    }

    public void saveAndSubmitRequest(SubmitRequest submitRequest) {
        try {
            Gson gson = new GsonBuilder().create();
            if (!OustAppState.getInstance().isAssessmentGame()) {
                OustPreferences.save("lastgamesubmitrequest", gson.toJson(submitRequest));
            }
            if (!OustSdkTools.checkInternetStatus()) {
                submitRequestProcessFinish(null);
                return;
            }
            if ((isEventLaunch && OustStaticVariableHandling.getInstance().getOustApiListener() != null) || isFromCourse) {
                userEventAssessmentData.setAssessmentId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                int answered = playResponse.getqIdList().size();

                userEventAssessmentData.setnQuestionAnswered(answered);
                userEventAssessmentData.setnTotalQuestions(answered);

                userEventAssessmentData.setnQuestionWrong(nWrong);
                userEventAssessmentData.setnQuestionCorrect(nCorrect);
                userEventAssessmentData.setnQuestionSkipped(Math.max(answered - (nCorrect + nWrong), 0));

                userEventAssessmentData.setScore(submitRequest.getTotalscore());
                userEventAssessmentData.setEventId(eventId);
                userEventAssessmentData.setUserProgress("Completed");
                try {
                    userEventAssessmentData.setUserCompletionPercentage(100);
                    userEventAssessmentData.setCompletionDate("" + System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                    OustSdkTools.sendSentryException(e);
                }

                int passingMark = (int) OustAppState.getInstance().getAssessmentFirebaseClass().getPassPercentage();
                int actualPassing = ((nCorrect * 100) / (nCorrect + nWrong));
                boolean assessmentPass = actualPassing >= passingMark;
                userEventAssessmentData.setPassed(assessmentPass);

                if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate() != null) {
                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule() != null) {
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionPercentage(100);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setCompletionDateAndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCatalogueModule().setPassed(assessmentPass);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                    }

                    if (OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData() != null) {
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().getCommonLandingData().setCompletionPercentage(100);
                        OustStaticVariableHandling.getInstance().getCatalogueModuleUpdate().setUpdated(true);
                    }
                }

                if (!isFromCourse) {
                    if (isCplModule) {
                        UserEventCplData userEventCplData = new UserEventCplData();
                        userEventCplData.setCurrentModuleType("Assessment");
                        userEventCplData.setEventId(eventId);
                        userEventCplData.setCurrentModuleId(Long.parseLong(OustPreferences.get("current_assessmentId")));
                        userEventCplData.setCplid(OustPreferences.getTimeForNotification("cplID"));
                        final int totalModules = (int) OustPreferences.getTimeForNotification("cplTotalModules");
                        final int completedModules = (int) OustPreferences.getTimeForNotification("cplCompletedModules");
                        userEventCplData.setnTotalModules(totalModules);

                        if ((completedModules + 1) >= totalModules) {
                            userEventCplData.setCompletedOn("" + System.currentTimeMillis());
                            userEventCplData.setUserProgress("Completed");
                            userEventCplData.setnModulesCompleted(totalModules);
                        } else {
                            userEventCplData.setnModulesCompleted(completedModules + 1);
                            userEventCplData.setUserProgress("InProgress");
                        }

                        userEventCplData.setCurrentModuleProgress("Completed");
                        userEventCplData.setUserEventAssessmentData(userEventAssessmentData);
                        OustStaticVariableHandling.getInstance().getOustApiListener().onCPLProgress(userEventCplData);
                    } else {
                        OustStaticVariableHandling.getInstance().getOustApiListener().onAssessmentProgress(userEventAssessmentData);
                    }
                } else {
                    userEventAssessmentData.setEventId(0);
                    OustAppState.getInstance().getAssessmentFirebaseClass().setUserEventAssessmentData(userEventAssessmentData);
                }
            }

            List<Scores> scoresTest = getAllScores(submitRequest.getScores());
            submitRequest.setScores(scoresTest);
            submitScore(submitRequest);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private List<Scores> getAllScores(List<Scores> scores) {
        List<Scores> test = new ArrayList<>();
        if (scores != null) {
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i) != null && scores.get(i).getQuestion() != 0)
                    test.add(scores.get(i));
            }
            return test;
        }
        return null;
    }

    private void assessmentCompleted(ActiveGame activeGame, ActiveUser
            activeUser, SubmitRequest submitRequest, GamePoints gamePoints) {
        try {
            activeGame.setRematch(false);
            OustAppState.getInstance().setHasPopup(false);
            questionBaseModel.setContainCertificate(containCertificate);
            long totalTimeTaken = 0;
            if (scoresList != null) {
                for (int i = 0; i < scoresList.size(); i++) {
                    totalTimeTaken += scoresList.get(i).getTime();
                }
            }
            questionBaseModel.setTotalTimeTaken(totalTimeTaken);
            questionBaseModel.setCplModule(isCplModule);
            questionBaseModel.setFromCourse(isFromCourse);
            questionBaseModel.setMappedCourseId(mappedCourseId);
            questionBaseModel.setCourseAssociated(courseAssociated);
            questionBaseModel.setSurveyAssociated(surveyAssociated);
            questionBaseModel.setSurveyMandatory(surveyMandatory);
            questionBaseModel.setMappedSurveyId(mappedSurveyId);
            questionBaseModel.setShowAssessmentResultScore(showAssessmentResultScore);
            questionBaseModel.setReAttemptAllowed(reAttemptAllowed);
            questionBaseModel.setnAttemptCount(nAttemptCount);
            questionBaseModel.setnAttemptAllowedToPass(nAttemptAllowedToPass);
            questionBaseModel.setCourseId(courseId);
            questionBaseModel.setActiveUser(activeUser);
            questionBaseModel.setActiveGame(activeGame);
            questionBaseModel.setSubmitRequest(submitRequest);
            questionBaseModel.setGamePoints(gamePoints);
            questionBaseModel.setType(2);
            liveData.setValue(questionBaseModel);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
