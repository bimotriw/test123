package com.oustme.oustsdk;

import android.util.Log;


import androidx.annotation.Keep;

import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oust on 1/29/18.
 */

@Keep
public class AssessmentCopyResponse {
    private String studentId;
    private String totalQuestion;
    //private Map<String,Scores> scoresList;
    private List<ScoresCopy> scoresList;
    public String questionIndex;
    private String gameId;
    private String courseId;
    private String challengerFinalScore;
    private String winner;
    private String endTime;
    private String startTime;
    private String challengerid;
    private String opponentid;
    private String assessmentState;
    private long userResponseTime;
    private long userSubmitTime;
    private String userQuestion;
    private String userAnswer;
    private String resumeTime;
    String commentMediaUploadedPath;

    public List<ScoresCopy> getScoresList() {
        return scoresList;
    }

    public void setScoresList(List<ScoresCopy> scoresList) {
        this.scoresList = scoresList;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
//
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getChallengerid() {
        return challengerid;
    }

    public void setChallengerid(String challengerid) {
        this.challengerid = challengerid;
    }

    public String getOpponentid() {
        return opponentid;
    }

    public void setOpponentid(String opponentid) {
        this.opponentid = opponentid;
    }

    public String getAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(String assessmentState) {
        this.assessmentState = assessmentState;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(String questionIndex) {
        this.questionIndex = questionIndex+"";
    }
//
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getChallengerFinalScore() {
        return challengerFinalScore;
    }

    public void setChallengerFinalScore(String challengerFinalScore) {
        this.challengerFinalScore = challengerFinalScore+"";
    }

    public String getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion+"";
    }

    public long getUserResponseTime() {
        return userResponseTime;
    }

    public void setUserResponseTime(long userResponseTime) {
        this.userResponseTime = userResponseTime;
    }

    public long getUserSubmitTime() {
        return userSubmitTime;
    }

    public void setUserSubmitTime(long userSubmitTime) {
        this.userSubmitTime = userSubmitTime;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(String userQuestion) {
        this.userQuestion = userQuestion;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getResumeTime() {
        return resumeTime;
    }

    public void setResumeTime(String resumeTime) {
        this.resumeTime = resumeTime;
    }
    
    public  AssessmentCopyResponse(AssessmentPlayResponse assessmentPlayResponce){
        this.setTotalQuestion("" + assessmentPlayResponce.getTotalQuestion());
        this.setStudentId("" + assessmentPlayResponce.getStudentId());
        this.setAssessmentState("" + assessmentPlayResponce.getAssessmentState());
        this.setWinner(assessmentPlayResponce.getWinner());
        this.setGameId("" + assessmentPlayResponce.getGameId());
        this.setChallengerFinalScore("" + assessmentPlayResponce.getChallengerFinalScore());
        this.setChallengerid("" + assessmentPlayResponce.getChallengerid());
        this.setOpponentid("" + assessmentPlayResponce.getOpponentid());
        this.setEndTime("" + assessmentPlayResponce.getEndTime());
        this.setStartTime("" + assessmentPlayResponce.getStartTime());
        this.setQuestionIndex("" + assessmentPlayResponce.getQuestionIndex());
        this.setResumeTime("" + assessmentPlayResponce.getResumeTime());
        this.setCommentMediaUploadedPath("" + assessmentPlayResponce.getCommentMediaUploadedPath());

        Log.d("GameActivity", "AssessmentCopyResponse: gameid->"+this.getGameId()+" --- challengerId->"+this.getChallengerid()+" --- studentId->"+this.getStudentId());

        List<Scores> scores = assessmentPlayResponce.getScoresList();
        List<ScoresCopy> scoresCopies = new ArrayList<>();
        if (scores != null) {
            for (int j = 0; j < scores.size(); j++) {
                try {
                    if (scores.get(j) != null && scores.get(j).getQuestion() != 0) {
                        ScoresCopy scoresCopy = new ScoresCopy();
                        scoresCopy.setAnswer(scores.get(j).getAnswer());
                        scoresCopy.setCorrect(scores.get(j).isCorrect());
                        scoresCopy.setGrade(scores.get(j).getGrade());
                        scoresCopy.setModuleId(scores.get(j).getModuleId());
                        scoresCopy.setQuestion("" + scores.get(j).getQuestion());
                        scoresCopy.setQuestionSerialNo("" + scores.get(j).getQuestionSerialNo());
                        scoresCopy.setQuestionType(scores.get(j).getQuestionType());
                        scoresCopy.setScore("" + scores.get(j).getScore());
                        scoresCopy.setSubject(scores.get(j).getSubject());
                        scoresCopy.setTime("" + scores.get(j).getTime());
                        scoresCopy.setTopic("" + scores.get(j).getTopic());
                        scoresCopy.setTopic("" + scores.get(j).getTopic());
                        scoresCopy.setUserSubjectiveAns("" + scores.get(j).getUserSubjectiveAns());
                        scoresCopy.setRemarks("" + scores.get(j).getRemarks());
                        scoresCopy.setQuestionMedia("" + scores.get(j).getQuestionMedia());
                        scoresCopy.setSurveyQuestion(scores.get(j).isSurveyQuestion());
                        scoresCopies.add(scoresCopy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }
        }
        this.setScoresList(scoresCopies);
    }

    public AssessmentCopyResponse() {
    }

    public String getCommentMediaUploadedPath() {
        return commentMediaUploadedPath;
    }

    public void setCommentMediaUploadedPath(String commentMediaUploadedPath) {
        this.commentMediaUploadedPath = commentMediaUploadedPath;
    }
}
