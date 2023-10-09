package com.oustme.oustsdk.presenter.assessments;

import android.text.Html;
import android.text.Spanned;

import com.oustme.oustsdk.activity.assessments.QuestionActivity;
import com.oustme.oustsdk.customviews.QuestionOptionImageView;
import com.oustme.oustsdk.firebase.assessment.AssessmentType;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.request.SubmitRequest;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.AssessmentState;
import com.oustme.oustsdk.response.assessment.GamePoints;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.assessment.SubmitResponse;
import com.oustme.oustsdk.response.common.GameType;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.response.common.QuestionCategory;
import com.oustme.oustsdk.response.common.QuestionType;
import com.oustme.oustsdk.room.OustDBBuilder;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.TimeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class QuestionActivityPresenter {
    private QuestionActivity view;
    private GamePoints gamePoints;
    ActiveGame activeGame;
    ActiveUser activeUser;
    private PlayResponse playResponse;
    private SubmitRequest submitRequest;

    private Scores scores;
    private List<Scores> scoresList;
    private int noofQuesinGame;
    private int questionIndex;
    private long challengerFinalScore;
    private String startDateTime;

    private String questionCategory;
    private String questionType;
    private String question;
    private DTOQuestions questions;

    long challengerScore;
    long opponentScore;
    long opponentFinalScore;
    private String courseId, courseColnId;


    private int scrachPadStatus = 0;

    private int correctAnswerCount = 0;
    private int wrongAnswerCount = 0;

    int opponentAnswerCount = 0;
    public int shouldTextSpeechPlay = 1;

    private AssessmentPlayResponse assessmentPlayResponce;


    public QuestionActivityPresenter(QuestionActivity view, GamePoints gamePoints, ActiveGame activeGame, ActiveUser activeUser,
                                     PlayResponse playResponse, AssessmentPlayResponse assessmentPlayResponce, String courseId, String courseColnId) {
        this.view = view;
        this.gamePoints = gamePoints;
        this.activeGame = activeGame;
        this.activeUser = activeUser;
        if ((activeUser != null) && (activeUser.getStudentid() != null)) {
        } else {
            String activeUserGet = OustPreferences.get("userdata");
            this.activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
            HttpManager.setBaseUrl();
            OustFirebaseTools.initFirebase();
        }
        this.playResponse = playResponse;
        this.assessmentPlayResponce = assessmentPlayResponce;
        this.courseId = courseId;
        this.courseColnId = courseColnId;
        setStartingData();
    }

    private void setStartingData() {
        view.keepScreenOnSecure();
        view.setRefresher();
        view.setRateUsPopupVariables();
        scoresList = new ArrayList<>();
        setQuizRelatedData(assessmentPlayResponce);
    }

    private void setQuizRelatedData(AssessmentPlayResponse assessmentPlayResponce) {
        if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
            noofQuesinGame = playResponse.getqIdList().size();
        } else {
            noofQuesinGame = playResponse.getEncrypQuestions().length;
            playResponse = OustSdkTools.getInstance().getDecryptedQuestion(playResponse);
        }

        scoresList = new ArrayList<>();
        view.showQuestionProgressMaxVal(noofQuesinGame);
        questionIndex = 0;
        challengerFinalScore = 0;
        if ((assessmentPlayResponce != null)) {
            if (assessmentPlayResponce.getScoresList() != null) {
                scoresList = assessmentPlayResponce.getScoresList();
                questionIndex = assessmentPlayResponce.getQuestionIndex();
                challengerFinalScore = assessmentPlayResponce.getChallengerFinalScore();
            }
        } else {
            assessmentPlayResponce = new AssessmentPlayResponse();
        }
        view.setFontName();
        view.setAnswerChoicesColor();
        showStartingQuestionData();
        showFirstQuestion();
    }

    private void showStartingQuestionData() {
        startDateTime = TimeUtils.getCurrentDateAsString();
        if (!OustAppState.getInstance().isAssessmentGame()) {
            if (activeUser.getUserDisplayName() != null) {
                view.setChallengerName(activeUser.getUserDisplayName());
            }
            if (activeGame.getGameType() == GameType.ACCEPTCONTACTSCHALLENGE) {
                view.showContactChallengerAvatar(activeGame.getChallengerDisplayName());
            } else {
                view.showNormalChallengerAvatar(activeUser.getAvatar());
            }
            setOpponentAvatar();
            setOpponenetName();
        } else {
            view.showAssessmentGameTop();
        }
        setQuestionInformation();
    }

    private void setOpponentAvatar() {
        switch (activeGame.getGameType()) {
            case GROUP:
                view.showGroupAvatar(activeGame.getOpponentDisplayName());
                break;
            case MYSTERY:
                view.showOpponentAvatar(activeGame.getOpponentAvatar());
                break;
            default:
                if ((activeGame.getGroupId().isEmpty()) && (!activeGame.getGameType().toString().equals("CONTACTSCHALLENGE"))) {
                    view.showOpponentAvatar(activeGame.getOpponentAvatar());
                } else {
                    view.showGroupAvatar(activeGame.getOpponentDisplayName());
                }
        }
    }

    private void setOpponenetName() {
        switch (activeGame.getGameType()) {
            case GROUP:
                view.setGroupName(activeGame.getGroupName());
                break;
            case CONTACTSCHALLENGE:
                if ((activeGame.getOpponentDisplayName() != null)) {
                    view.setGroupName(activeGame.getOpponentDisplayName());
                }
                break;
            default:
                if ((activeGame.getOpponentDisplayName() != null)) {
                    view.setOpponenetName(activeGame.getOpponentDisplayName());
                }
                break;
        }
    }

    private void setQuestionInformation() {
        try {
            String topicName = "";
            String grade1 = "";
            String sub = "";
            String topic = "";
            if (questions != null) {
                sub = questions.getSubject();
                topic = questions.getTopic();
            }
            if (!activeGame.isLpGame()) {
                if (questions != null && (questions.getGrade() != null) && (!questions.getGrade().equalsIgnoreCase("0"))) {
                    grade1 = questions.getGrade();
                } else {
                    grade1 = activeGame.getModuleName();
                }
            } else {
                grade1 = activeGame.getModuleName();
                sub = "";
                topic = "";
            }
            if ((grade1 != null) && (!grade1.isEmpty())) {
                if (((sub != null) && (!sub.isEmpty())) && ((topic != null) && (!topic.isEmpty()))) {
                    topicName = (grade1 + " - " + sub + " - " + topic);
                } else if ((sub != null) && (!sub.isEmpty())) {
                    topicName = (grade1 + " - " + topic);
                } else if ((topic != null) && (!topic.isEmpty())) {
                    topicName = (grade1 + " - " + sub);
                } else {
                    topicName = (grade1);
                }
            } else if ((sub != null) && (!sub.isEmpty())) {
                if ((topic != null) && (!topic.isEmpty())) {
                    topicName = (sub + " - " + topic);
                } else {
                    topicName = (sub);
                }
            } else {
                topicName = (topic);
            }
            view.settxtTopicName(topicName);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    //---------------------------------------
    private void showFirstQuestion() {
        if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
            questions = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(questionIndex));
        } else {
            questions = playResponse.getQuestionsByIndex(questionIndex);
        }
        view.initializeSound(questions.getAudio());
        opponentFinalScore = 0;
        opponentScore = 0;
        checkForQuestionCategory();
    }

    private void checkForQuestionCategory() {
        try {
            if ((playResponse.getqIdList() != null) && (playResponse.getqIdList().size() > 0)) {
                questions = OustDBBuilder.getQuestionById(playResponse.getqIdList().get(questionIndex));
            } else {
                questions = playResponse.getQuestionsByIndex(questionIndex);
            }
            //questions=playResponse.getQuestions()[questionIndex];
//            view.playAudioFileOnline(questions.getAudio());
            questionCategory = questions.getQuestionCategory();
            if (questions.getQuestionType() != null) {
                questionType = questions.getQuestionType();
                if (questionType == null) {
                    questionType = QuestionType.MCQ;
                }
            } else {
                questionType = QuestionType.MCQ;
            }
        } catch (Exception e) {
            questionType = QuestionType.MCQ;
        }
        view.setQutionIndex(questionIndex, noofQuesinGame);
        view.showQuestionTitle(questionCategory);
    }

    public void desideTypeOfQuestionCAtegory() {
        showQuestionandOption();
    }

    public void showQuestionandOption() {
        try {
            //setTextToSpeechLayout();
            view.enableAllOption();
            view.startTimer(questions.getMaxtime());
            if (!questions.getQuestionType().equals(QuestionType.FILL)) {
                question = questions.getQuestion() + "";
            } else {
                question = null;
                view.showTextQuestion("");
            }
            prepareOpponentScore();
            if (question != null) {
                view.showTextQuestion(question);
            }

            if ((questions.getImage() != null) && (!questions.getImage().isEmpty())) {
                if(!questions.getQuestionType().equals(QuestionType.FILL)) {
                    view.showImageQuestion(questions.getImage());
                }else {
                    view.hideQuestionImage();
                }
            } else {
                view.hideQuestionImage();
            }
            showQuestionLayoutNOption();
        } catch (Exception e) {
        }
    }

    private void prepareOpponentScore() {
        try {
            switch (activeGame.getGameType()) {
                case ACCEPT:
                    if ((activeGame.getGroupId() != null) && (activeGame.getGroupId().isEmpty())) {
                        showOpponentScore();
                    }
                    break;
                case MYSTERY:
                    if ((!activeGame.getOpponentid().isEmpty()) && (!activeGame.getOpponentDisplayName().equals("Mystery"))) {
                        showOpponentScore();
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    private void showOpponentScore() {
        if (gamePoints != null && gamePoints.getPoints() != null && gamePoints.getPoints().length != 0) {
            opponentScore = gamePoints.getPoints()[questionIndex].getPoint();
            opponentFinalScore += opponentScore;
            if (gamePoints.getPoints()[questionIndex].isCorrect()) {
                opponentAnswerCount++;
            }
            view.settxtOpponentScore(opponentFinalScore);
        }
    }

    private void showQuestionLayoutNOption() {
        view.hideAllMatchOption();
        view.hideLongQuestionLayout();
        if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.IMAGE_CHOICE))) {
            view.hideFillLayout();
            if ((questions.getImageChoiceC() != null) && (questions.getImageChoiceC().getImageData() != null)) {
                view.showQuestion_ImageOption();
                if (questionType.equals(QuestionType.MRQ)) {
                    view.showAllImageQuestionOptions(questions, true, questions.isSkip());
                } else {
                    view.showAllImageQuestionOptions(questions, false, questions.isSkip());
                }
            } else {
                view.showQuestion_BigImageOption();
                if (questionType.equals(QuestionType.MRQ)) {
                    view.showAllBigImageQuestionOptions(questions, true, questions.isSkip());
                } else {
                    view.showAllBigImageQuestionOptions(questions, false, questions.isSkip());
                }
            }
        } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.MATCH))) {
            view.hideFillLayout();
            view.resetMatchLayout();
            view.setMatchQuestionLayoutSize();
            view.setMatchData(questions);
        } else if ((questionCategory != null) && (questionCategory.equals(QuestionCategory.USR_REC_A))) {
            view.hideFillLayout();
            view.setAudioMediaLayout();
            view.setQuestionCategory(questionCategory);
        } else if((questionCategory != null) && (questionCategory.equals(QuestionCategory.USR_REC_I))){
            view.hideFillLayout();
            view.setImageLayout();
            view.setQuestionCategory(questionCategory);
        } else if((questionCategory != null) && (questionCategory.equals(QuestionCategory.USR_REC_V))){
            view.hideFillLayout();
            view.setVideoLayout();
            view.setQuestionCategory(questionCategory);
        } else if (questionCategory.equals(QuestionCategory.LONG_ANSWER)) {
            view.hideFillLayout();
            view.setLongQuestionLayout();
        } else if (questionType.equals(QuestionType.FILL)) {
            view.setFillQuestionLayout(questions);
        } else {
            view.hideFillLayout();
            if (questionType.equals(QuestionType.MRQ)) {
                view.setMRQQuestionLayout(questions.isSkip());
            } else {
                view.setMCQQuestionLayout(questions.isSkip());
            }
            view.showAllOption(questions);
        }
    }

    //---------------------------------------
//show question images
    public void showBigQuestionImage(String imgStr) {
        if ((imgStr != null) && (!imgStr.isEmpty())) {
            scrachPadStatus = 1;
            view.showBigIamgeQuestion(imgStr);
        } else {
            if ((questions.getImage() != null) && (!questions.getImage().isEmpty())) {
                scrachPadStatus = 1;
                view.showBigIamgeQuestion(questions.getImage());
            }
        }
    }

    public void hideBigQuestionImage() {
        scrachPadStatus = 0;
        view.hideBigIamgeQuestion();
    }

    //----------------------------------------
//methode after click on option to detect right/wrong ans
    public void clickOnOption(int id) {
        view.detectOptionClick(id, questionType, questions);
    }

    public void calculateQuestionScore(String userAnswer) {
        try {
            scores = new Scores();
            challengerScore = 0;
            scores.setAnswer(userAnswer);
            scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));

            long answeredSeconds = view.getAnswerSecond();
            answeredSeconds = questions.getMaxtime() - answeredSeconds;
            // add in array
            if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                    || (questionType.equals(QuestionType.SURVEY))) {
            } else {
                if ((null != userAnswer) && (questions.getAnswer() != null) && (userAnswer.equalsIgnoreCase(questions.getAnswer()))) {
                    challengerScore = 0;
                    try {
                        float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
                        if (answeredSeconds <= 9) {
                            challengerScore = 20;
                        } else {
                            challengerScore = 20 - (long) penalty;
                        }
                    } catch (Exception e) {
                    }
                    scores.setScore(challengerScore);
                    scores.setCorrect(true);
                    correctAnswerCount++;

                } else {
                    challengerScore = 0;
                    scores.setScore(challengerScore);
                    wrongAnswerCount++;
                }

                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
                challengerFinalScore = challengerFinalScore + challengerScore;
                view.setChallengerScoreText(challengerFinalScore);
            }
            scores.setTime(answeredSeconds);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
        } catch (Exception e) {
        }
        showNextQuestion();
    }

    public void clickOnImageOption() {
        view.clickOnImageOption(questions);
    }

    public void clickOnBigImageOption() {
        view.clickOnBigImageOption(questions);
    }

    public void calculateQuestionScoreForImage(String answerFileName) {
        try {
            scores = new Scores();
            challengerScore = 0;
            scores.setAnswer(answerFileName);
            scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));

            long answeredSeconds = view.getAnswerSecond();
            answeredSeconds = questions.getMaxtime() - answeredSeconds;
            // add in array
            if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                    || (questionType.equals(QuestionType.SURVEY))) {
            } else {
                if ((null != answerFileName) && (questions.getImageChoiceAnswer() != null) && (questions.getImageChoiceAnswer().getImageFileName() != null)
                        && (answerFileName.equalsIgnoreCase(questions.getImageChoiceAnswer().getImageFileName()))) {
                    challengerScore = 0;
                    try {
                        float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
                        if (answeredSeconds <= 9) {
                            challengerScore = 20;
                        } else {
                            challengerScore = 20 - (long) penalty;
                        }
                    } catch (Exception e) {
                    }
                    scores.setScore(challengerScore);
                    scores.setCorrect(true);
                    correctAnswerCount++;
                } else {
                    challengerScore = 0;
                    scores.setScore(challengerScore);
                    wrongAnswerCount++;
                }

                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
                challengerFinalScore = challengerFinalScore + challengerScore;
                view.setChallengerScoreText(challengerFinalScore);
            }
            scores.setTime(answeredSeconds);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
        } catch (Exception e) {
        }
        showNextQuestion();
    }


    public void calculateQuestionScoreForMRQ(boolean statusA, boolean statusB, boolean statusC, boolean statusD, boolean statusE, boolean statusF, boolean statusG) {
        if ((statusA || statusB || statusC || statusD || statusE || statusF)) {
            try {
                QuestionOptionImageView.clickedOnOption = true;
                String answer = questions.getAnswer();
                try {
                    if (questions.getQuestionType().equals(QuestionType.MRQ)) {
                        answer = questions.getImageChoiceAnswer().getImageFileName();
                    }
                } catch (Exception e) {
                }
                if (answer == null) {
                    answer = "";
                }
                int noofAnswers = 0;
                int correrectAnswered = 0;
                int wrongAnswered = 0;
                int mrqScore = 0;
                String myAnswer = "";
                long answeredSeconds = view.getAnswerSecond();
                if (answer.contains("a") || (answer.contains("A"))) {
                    noofAnswers++;
                    if (statusA) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "A,";
                    }
                } else {
                    if (statusA) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "A,";
                    }
                }
                if (answer.contains("b") || (answer.contains("B"))) {
                    noofAnswers++;
                    if (statusB) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "B,";
                    }
                } else {
                    if (statusB) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "B,";
                    }
                }
                if (answer.contains("c") || (answer.contains("C"))) {
                    noofAnswers++;
                    if (statusC) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "C,";
                    }
                } else {
                    if (statusC) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "C,";
                    }
                }
                if (answer.contains("d") || (answer.contains("D"))) {
                    noofAnswers++;
                    if (statusD) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "D,";
                    }
                } else {
                    if (statusD) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "D,";
                    }
                }
                if (answer.contains("e") || (answer.contains("E"))) {
                    noofAnswers++;
                    if (statusE) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "E,";
                    }
                } else {
                    if (statusE) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "E,";
                    }
                }
                if (answer.contains("f") || (answer.contains("F"))) {
                    noofAnswers++;
                    if (statusF) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "F,";
                    }
                } else {
                    if (statusF) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "F,";
                    }
                }
                if (answer.contains("g") || (answer.contains("G"))) {
                    noofAnswers++;
                    if (statusG) {
                        correrectAnswered++;
                        myAnswer = myAnswer + "G,";
                    }
                } else {
                    if (statusG) {
                        wrongAnswered++;
                        myAnswer = myAnswer + "G,";
                    }
                }
                if (myAnswer.length() > 0) {
                    myAnswer = myAnswer.substring(0, myAnswer.length() - 1);
                }
                scores = new Scores();
                if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                        || (questionType.equals(QuestionType.SURVEY))) {
                } else {
                    if (noofAnswers == 0) {
                        noofAnswers = 1;
                    }
                    float f1 = 20 / noofAnswers;
                    int weight = Math.round(f1);
                    mrqScore = (weight * correrectAnswered) - (weight * wrongAnswered);
                    if (mrqScore > 20) {
                        mrqScore = 20;
                    } else if (mrqScore < 0) {
                        mrqScore = 0;
                    }
                    challengerScore = 0;
                    if ((wrongAnswered == 0) && (correrectAnswered == noofAnswers)) {
                        scores.setCorrect(true);
                        correctAnswerCount++;
                    } else {
                        wrongAnswerCount++;
                    }
                    answeredSeconds = questions.getMaxtime() - answeredSeconds;
                    float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
                    if (answeredSeconds <= 10) {
                        challengerScore = mrqScore;
                    } else {
                        challengerScore = mrqScore - (long) penalty;
                    }
                    if (challengerScore < 0) {
                        challengerScore = 0;
                    }
                    scores.setScore(challengerScore);
                    playResponse.setCorrectAnswerCount(correctAnswerCount);
                    playResponse.setWrongAnswerCount(wrongAnswerCount);
                    challengerFinalScore = challengerFinalScore + challengerScore;
                    view.setChallengerScoreText(challengerFinalScore);
                }
                scores.setAnswer((myAnswer));
                //scores.setCorrect(false);
                scores.setSubject(questions.getSubject());
                scores.setTopic(questions.getTopic());
                scores.setQuestion((int) questions.getQuestionId());
                scores.setQuestionType(questionType);
                scores.setQuestionSerialNo((questionIndex + 1));
                // add in array
                scores.setTime(answeredSeconds);
                scores.setXp(0);
                scoresList.add(questionIndex,scores);
            } catch (Exception e) {
            }
            showNextQuestion();
        }
    }

    public void calculateLongAnswerScore(String myAnswer) {
        try {
            long answeredSeconds = view.getAnswerSecond();
            scores = new Scores();
            scores.setCorrect(true);
            correctAnswerCount++;
            answeredSeconds = questions.getMaxtime() - answeredSeconds;
            playResponse.setCorrectAnswerCount(correctAnswerCount);
            playResponse.setWrongAnswerCount(wrongAnswerCount);
            challengerFinalScore = challengerFinalScore + 20;
            view.setChallengerScoreText(challengerFinalScore);
            scores.setAnswer((myAnswer));
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));
            scores.setScore(20);
            scores.setTime(answeredSeconds);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
        } catch (Exception e) {
        }
        showNextQuestion();
    }


    public void calculateQuestionScoreForMatch(List<String> ansStrList) {
        try {
            QuestionOptionImageView.clickedOnOption = true;
            List<String> answerList = questions.getMtfAnswer();
            int noofAnswers = ansStrList.size();
            int correrectAnswered = 0;
            int wrongAnswered = 0;
            int mrqScore = 0;
            String myAnswer = "";
            for (int i = 0; i < ansStrList.size(); i++) {
                for (int j = 0; j < answerList.size(); j++) {
                    if (answerList.get(j).contains(ansStrList.get(i))) {
                        correrectAnswered++;
                    }
                }
            }
            wrongAnswered = noofAnswers - correrectAnswered;
            long answeredSeconds = view.getAnswerSecond();
            scores = new Scores();
            if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                    || (questionType.equals(QuestionType.SURVEY))) {
            } else {
                if (noofAnswers == 0) {
                    noofAnswers = 1;
                }
                float f1 = 20 / noofAnswers;
                int weight = Math.round(f1);
                mrqScore = (weight * correrectAnswered) - (weight * wrongAnswered);
                if (mrqScore > 20) {
                    mrqScore = 20;
                } else if (mrqScore < 0) {
                    mrqScore = 0;
                }
                challengerScore = 0;
                if ((wrongAnswered == 0) && (correrectAnswered == noofAnswers)) {
                    scores.setCorrect(true);
                    correctAnswerCount++;
                } else {
                    wrongAnswerCount++;
                }
                if (correrectAnswered > 0) {
                    myAnswer = "this is MTF question";
                }
                answeredSeconds = questions.getMaxtime() - answeredSeconds;
                float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
                if (answeredSeconds <= 10) {
                    challengerScore = mrqScore;
                } else {
                    challengerScore = mrqScore - (long) penalty;
                }
                if (challengerScore < 0) {
                    challengerScore = 0;
                }
                scores.setScore(challengerScore);
                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
                challengerFinalScore = challengerFinalScore + challengerScore;
                view.setChallengerScoreText(challengerFinalScore);
            }
            scores.setAnswer((myAnswer));
            //scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));
            // add in array
            scores.setTime(answeredSeconds);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
        } catch (Exception e) {
        }
        showNextQuestion();
    }

    public void calculateQuestionScoreForFILL(int correrectAnswered, List<String> ansStrList) {
        try {
            int noofAnswers = ansStrList.size();
            int wrongAnswered = 0;
            int mrqScore = 0;
            String myAnswer = "";

            wrongAnswered = noofAnswers - correrectAnswered;
            long answeredSeconds = view.getAnswerSecond();
            scores = new Scores();
            if (((OustAppState.getInstance().isAssessmentGame()) && (OustAppState.getInstance().getAssessmentFirebaseClass().getAssessmentType() == AssessmentType.PSYCHOMETRIC))
                    || (questionType.equals(QuestionType.SURVEY))) {
            } else {
                if (noofAnswers == 0) {
                    noofAnswers = 1;
                }
                float f1 = 20 / noofAnswers;
                int weight = Math.round(f1);
                mrqScore = (weight * correrectAnswered) - (weight * wrongAnswered);
                if (mrqScore > 20) {
                    mrqScore = 20;
                } else if (mrqScore < 0) {
                    mrqScore = 0;
                }
                challengerScore = 0;
                if ((wrongAnswered == 0) && (correrectAnswered == noofAnswers)) {
                    scores.setCorrect(true);
                    correctAnswerCount++;
                } else {
                    wrongAnswerCount++;
                }
                if (correrectAnswered > 0) {
                    myAnswer = "this is FILL question";
                }
                answeredSeconds = questions.getMaxtime() - answeredSeconds;
                float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
                if (answeredSeconds <= 10) {
                    challengerScore = mrqScore;
                } else {
                    challengerScore = mrqScore - (long) penalty;
                }
                if (challengerScore < 0) {
                    challengerScore = 0;
                }
                scores.setScore(challengerScore);
                playResponse.setCorrectAnswerCount(correctAnswerCount);
                playResponse.setWrongAnswerCount(wrongAnswerCount);
                challengerFinalScore = challengerFinalScore + challengerScore;
                view.setChallengerScoreText(challengerFinalScore);
            }
            scores.setAnswer((myAnswer));
            //scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));
            // add in array
            scores.setTime(answeredSeconds);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
        } catch (Exception e) {
        }
        showNextQuestion();
    }

    public void calculateScoreForMedia(String filename) {
        long answeredSeconds = view.getAnswerSecond();
        answeredSeconds = questions.getMaxtime() - answeredSeconds;

        scores = new Scores();
        challengerScore = 0;
        if (filename != null && (!filename.isEmpty())) {
            scores.setCorrect(true);
            correctAnswerCount++;
            challengerScore=20;
        } else {
            wrongAnswerCount++;
            challengerScore=0;
        }

        answeredSeconds = questions.getMaxtime() - answeredSeconds;
        float penalty = ((float) answeredSeconds / (float) questions.getMaxtime()) * 10;
        if (answeredSeconds <= 10) {
            challengerScore = 20;
        } else {
            challengerScore = 20 - (long) penalty;
        }
        if (challengerScore < 0) {
            challengerScore = 0;
        }

        scores.setScore(challengerScore);
        playResponse.setCorrectAnswerCount(correctAnswerCount);
        playResponse.setWrongAnswerCount(wrongAnswerCount);
        challengerFinalScore = challengerFinalScore + challengerScore;
        view.setChallengerScoreText(challengerFinalScore);

        scores.setAnswer((filename));
        scores.setSubject(questions.getSubject());
        scores.setTopic(questions.getTopic());
        scores.setQuestion((int) questions.getQuestionId());
        scores.setQuestionType(questionType);
        scores.setQuestionSerialNo((questionIndex + 1));
        // add in array
        scores.setTime(answeredSeconds);
        scores.setXp(0);
        scoresList.add(questionIndex,scores);
        showNextQuestion();
    }

    //public void calculateScoreFor()

    public void showNextQuestion() {
        try {
            saveAssessmentForEachQuestion();
            view.disableAllMediaLayout();
            view.disableAllOption();
            view.hideKeyboard();
            if (scrachPadStatus == 1) {
                scrachPadStatus = 0;
                view.hideBigIamgeQuestion();
            }
            view.cancleTimer();

            if (questions.getAudio() != null && (!questions.getAudio().isEmpty())) {
                view.initializeSound("");
            }
            if (questionIndex == (noofQuesinGame - 1)) {
                view.setFinalAnimToHideQuestion();
                calculateFinalScore();
                return;
            } else {
                questionIndex++;
                checkForQuestionCategory();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void saveAssessmentForEachQuestion() {
        try {
            assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex+"", scoresList,
                    activeGame.getGameid(), challengerFinalScore+"", AssessmentState.INPROGRESS);
            view.saveAssessmentGame(assessmentPlayResponce, activeUser);
        } catch (Exception e) {
        }
    }

    public void calculateFinalScore() {
        view.startGameOverMusic();
        view.setGameSubmitRequestSent(true);
        try {
            if ((activeGame.getStudentid() != null) && (!activeGame.getStudentid().isEmpty())) {
            }
            {
                activeGame.setStudentid(activeUser.getStudentid());
            }
            if ((activeGame.getChallengerid() != null) && (!activeGame.getChallengerid().isEmpty())) {
            }
            {
                activeGame.setChallengerid(activeUser.getStudentid());
            }
            submitRequest = new SubmitRequest();
            submitRequest.setWinner(findWinner());

            submitRequest.setGameid(String.valueOf(playResponse.getGameId()));
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
                String uniqueId = activeGame.getOpponentid();
                //String branchLink=view.getBranchLinkForContactChallenge(activeGame,uniqueId,playResponse.getGameId());
                submitRequest.setOpponentid(activeGame.getOpponentid());
                submitRequest.setMobileNum(activeGame.getMobileNum());
                //submitRequest.setDeepLink(branchLink);
                submitRequest.setChallengerid(activeGame.getChallengerid());
                submitRequest.setExternal(true);
            } else {
                submitRequest.setChallengerid(activeGame.getChallengerid());
            }

            String gcmToken = view.getGcmToken();
            if ((gcmToken != null) && (!gcmToken.isEmpty())) {
                submitRequest.setDeviceToken(gcmToken);
            }
            submitRequest.setStudentid(activeUser.getStudentid());
            if (OustAppState.getInstance().isAssessmentGame()) {
                submitRequest.setAssessmentId("" + OustAppState.getInstance().getAssessmentFirebaseClass().getAsssessemntId());
                submitRequest.setContentPlayListId(OustAppState.getInstance().getAssessmentFirebaseClass().getContentPlayListId());
                if (courseId != null) {
                    submitRequest.setCourseId(courseId);
                }
                if (courseColnId != null) {
                    submitRequest.setCourseColnId(courseColnId);
                }
            }
            if (OustAppState.getInstance().isAssessmentGame()) {
                assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), scoresList, activeGame.getGameid(),
                        challengerFinalScore+"", submitRequest.getWinner(), submitRequest.getEndTime(), submitRequest.getStartTime(),
                        submitRequest.getChallengerid(), submitRequest.getOpponentid(), AssessmentState.COMPLETED);
                view.saveComplteAssessmentGame(activeUser);
                view.saveAssessmentGame(assessmentPlayResponce, activeUser);
            }
            view.saveAndSubmitRequest(submitRequest);
        } catch (Exception e) {
            view.finishActivity();
        }
    }

    public String findWinner() {
        String winner = "-1";
        try {
            if (((activeGame.getGameType() == GameType.MYSTERY) && (!activeGame.getOpponentid().equals("Mystery"))) || (activeGame.getGameType() == GameType.ACCEPT)) {
                if (correctAnswerCount > opponentAnswerCount) {
                    winner = activeGame.getStudentid();
                } else if (correctAnswerCount < opponentAnswerCount) {
                    if (activeUser.getStudentid().equalsIgnoreCase(activeGame.getOpponentid())) {
                        winner = activeGame.getChallengerid();
                    } else {
                        winner = activeGame.getOpponentid();
                    }
                } else if (correctAnswerCount == opponentAnswerCount) {
                    if (opponentFinalScore > challengerFinalScore) {
                        if ((activeGame.getOpponentid().equalsIgnoreCase(activeUser.getStudentid()))) {
                            winner = activeGame.getChallengerid();
                        } else {
                            winner = activeGame.getOpponentid();
                        }
                    } else if (opponentFinalScore < challengerFinalScore) {
                        winner = activeUser.getStudentid();
                    } else {
                        winner = "TIE";
                    }
                }
                return winner;
            }
        } catch (Exception e) {
        }
        return winner;
    }

    public void submitRequestProcessFinish(SubmitResponse submitResponse) {
        if ((null != submitResponse) && (submitResponse.isSuccess())) {
            view.clearSubmitRequestSaved();
            if (OustAppState.getInstance().isAssessmentGame()) {
                assessmentPlayResponce.setAssessmentState(AssessmentState.SUBMITTED);
                view.saveAssessmentGame(assessmentPlayResponce, activeUser);
                view.deleteComplteAssessmentGame(activeUser);
            }
        }
        OustAppState.getInstance().setPlayResponse(this.playResponse);
        view.answerProcessFinish(activeGame, activeUser, submitRequest, gamePoints);
    }

    public void activityBackBtnPressed() {
        if (scrachPadStatus == 1) {
            scrachPadStatus = 0;
            view.hideBigIamgeQuestion();
        } else {
            view.cancleGame();
        }
    }

    public void submitGame() {
        try {
            if ((playResponse != null)) {
                if (OustAppState.getInstance().isAssessmentGame()) {
                    pauseAssessmentGame();
                    return;
                }
                view.setGameSubmitRequestSent(true);
                for (; questionIndex < noofQuesinGame; questionIndex++) {
                    if (gamePoints != null && gamePoints.getPoints() != null && gamePoints.getPoints().length != 0) {
                        opponentFinalScore += gamePoints.getPoints()[questionIndex].getPoint();
                        if (gamePoints.getPoints()[questionIndex].isCorrect()) {
                            opponentAnswerCount++;
                        }
                    }
                    scores = new Scores();
                    challengerScore = 0;
                    scores.setAnswer("");
                    scores.setCorrect(false);
                    scores.setSubject(questions.getSubject());
                    scores.setTopic(questions.getTopic());
                    scores.setQuestion((int) questions.getQuestionId());
                    scores.setQuestionType(questionType);
                    scores.setQuestionSerialNo((questionIndex + 1));
                    scores.setScore(0);
                    wrongAnswerCount++;
                    playResponse.setCorrectAnswerCount(correctAnswerCount);
                    playResponse.setWrongAnswerCount(wrongAnswerCount);
                    scores.setTime(0);
                    scores.setXp(0);
                    scoresList.add(questionIndex,scores);
                }
                view.setFinalAnimToHideQuestion();
                calculateFinalScore();
            } else {
                view.finishActivity();
            }
        } catch (Exception e) {
            view.finishActivity();
        }
    }

    private void pauseAssessmentGame() {
        try {
            scores = new Scores();
            challengerScore = 0;
            scores.setAnswer("");
            scores.setCorrect(false);
            scores.setSubject(questions.getSubject());
            scores.setTopic(questions.getTopic());
            scores.setQuestion((int) questions.getQuestionId());
            scores.setQuestionType(questionType);
            scores.setQuestionSerialNo((questionIndex + 1));
            // add in array
            scores.setScore(0);
            wrongAnswerCount++;
            playResponse.setCorrectAnswerCount(correctAnswerCount);
            playResponse.setWrongAnswerCount(wrongAnswerCount);
            scores.setTime(0);
            scores.setXp(0);
            scoresList.add(questionIndex,scores);
            questionIndex++;
            if (questionIndex < (noofQuesinGame)) {
                assessmentPlayResponce = new AssessmentPlayResponse(activeUser.getStudentid(), questionIndex+"", scoresList,
                        activeGame.getGameid(), challengerFinalScore+"", AssessmentState.INPROGRESS);
                view.saveAssessmentGame(assessmentPlayResponce, activeUser);
                view.finishActivity();
            } else {
                view.setFinalAnimToHideQuestion();
                calculateFinalScore();
            }
        } catch (Exception e) {
            view.finishActivity();
        }
    }

    //----------------------------------------------------------------------------

    public void createStringfor_speech() {
        try {
            if ((questions.getAudio() != null) && (!questions.getAudio().isEmpty())) {
                view.clickOnAudioIcon(questions.getAudio());
            } else {
                Spanned s1 = getSpannedContent(questions.getQuestion());
                String quesStr = s1.toString().trim();
                String optionStr = "";
                if ((questions.getA() != null)) {
                    Spanned s2 = getSpannedContent(questions.getA());
                    optionStr += ("\n Choice A \n" + (s2.toString().trim()));
                }
                if ((questions.getB() != null)) {
                    Spanned s2 = getSpannedContent(questions.getB());
                    optionStr += ("\n Choice B \n " + (s2.toString().trim()));
                }
                if ((questions.getC() != null)) {
                    Spanned s2 = getSpannedContent(questions.getC());
                    optionStr += ("\n Choice C \n" + (s2.toString().trim()));
                }
                if ((questions.getD() != null)) {
                    Spanned s2 = getSpannedContent(questions.getD());
                    optionStr += ("\n Choice D \n" + (s2.toString().trim()));
                }
                if ((questions.getE() != null)) {
                    Spanned s2 = getSpannedContent(questions.getE());
                    optionStr += ("\n Choice E \n" + (s2.toString().trim()));
                }
                if ((questions.getF() != null)) {
                    Spanned s2 = getSpannedContent(questions.getF());
                    optionStr += ("\n Choice F \n" + (s2.toString().trim()));
                }
                if ((questions.getG() != null)) {
                    Spanned s2 = getSpannedContent(questions.getG());
                    optionStr += ("\n Choice G \n" + (s2.toString().trim()));
                }
                if ((questionType.equals(QuestionType.MCQ))) {
                    view.startSpeakQuestion((quesStr + "\n\n Choose one of these  \n\n" + optionStr));
                } else if ((questionType.equals(QuestionType.MRQ))) {
                    view.startSpeakQuestion((quesStr + " \n " + optionStr));
                } else {
                    view.startSpeakQuestion(quesStr);
                }
            }
        } catch (Exception e) {
        }
    }

    private Spanned getSpannedContent(String content) {
        String s2 = content.trim();
        try {
            if (s2.endsWith("<br />")) {
                s2 = s2.substring(0, s2.lastIndexOf("<br />"));
            }
        } catch (Exception e) {
        }
        Spanned s1 = Html.fromHtml(s2);
        return s1;
    }

    //===========================================================
//pause game in case of video or audio question
    public void resumeVidOrAudioGame() {
        try {
            view.resumeGame(questions.getMaxtime());
        } catch (Exception e) {
        }
    }
}
