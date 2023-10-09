package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.HashMap;
import java.util.List;



@Keep
public class AssessmentPlayResponse {
    private String assessmentState;
    private long challengerFinalScore;
    private String gameId;
    private String questionIndex;
    private String endTime;
    private String startTime;
    private String userResponseTime;
    private String userSubmitTime;
    private List<Scores> scoresList;
    private String studentid;
    private int totalQuestion;
    private String winner;
    private long courseId;
    private long contentPlayListId;
    private String challengerid;
    private String opponentid;
    private String resumeTime;
    private long attemptCount;
    String commentMediaUploadedPath;
    int currentQuestionId;

    public AssessmentPlayResponse() {

    }

    public AssessmentPlayResponse(String id, String quesIndex, List<Scores> scoresList1, String gameId, String challengerFinalScore, String assessmentState) {
        this.studentid = id;
        this.scoresList = scoresList1;
        if (scoresList1 != null) {
            this.totalQuestion = scoresList1.size();
        }
        this.questionIndex = quesIndex;
        this.gameId = gameId;
        try {
            this.challengerFinalScore = Long.parseLong(challengerFinalScore);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        this.assessmentState = assessmentState;
        this.challengerid = id;
    }

    private HashMap<String, Scores> getScoresListFromArray(List<Scores> scoresList1) {
        HashMap<String, Scores> tempScoreMap = new HashMap<>();
        if (scoresList1 != null) {
            for (int i = 0; i < scoresList1.size(); i++) {
                if (scoresList1.get(i) != null) {
                    tempScoreMap.put("ques" + scoresList1.get(i).getQuestion(), tempScoreMap.get(i));
                }
            }
        }
        return tempScoreMap;
    }

    public AssessmentPlayResponse(String studentId, List<Scores> scoresList, String gameId,
                                  String challengerFinalScore, String winner, String endTime,
                                  String startTime, String challengerid, String opponentid,
                                  String assessmentState) {
        this.studentid = studentId;
        this.scoresList = scoresList;//getScoresListFromArray(scoresList);;
        this.gameId = gameId;
        try {
            this.challengerFinalScore = Long.parseLong(challengerFinalScore);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        this.winner = winner;
        this.endTime = endTime;
        this.startTime = startTime;
        this.challengerid = challengerid;
        this.opponentid = opponentid;
        if (scoresList != null) {
            this.totalQuestion = scoresList.size();
        }
        this.assessmentState = assessmentState;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

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
        return studentid;
    }

    public void setStudentId(String studentId) {
        this.studentid = studentId;
    }

    public List<Scores> getScoresList() {

        return scoresList;
    }

    public void setScoresList(List<Scores> scoresList) {
//        HashMap<String, Scores> tempScoreMap = new HashMap<>();
//        if (scoresList != null) {
//            for (int i = 0; i < scoresList.length; i++) {
//                tempScoreMap.put(scoresList[i].getQuestion() + "", scoresList[i]);
//            }
//        }
        this.scoresList = scoresList;
    }

    public int getQuestionIndex() {
        if (questionIndex != null)
            return Integer.parseInt(questionIndex);
        else return 0;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex + "";
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public long getChallengerFinalScore() {
        return challengerFinalScore;
    }

    public void setChallengerFinalScore(long challengerFinalScore) {
        this.challengerFinalScore = challengerFinalScore;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public long getResumeTime() {
        if (resumeTime != null)
            return Long.parseLong(resumeTime);
        else return 0;
    }

    public long getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(long attemptCount) {
        this.attemptCount = attemptCount;
    }

    public void setResumeTime(String resumeTime) {
        this.resumeTime = resumeTime;
    }

    public String getCommentMediaUploadedPath() {
        return commentMediaUploadedPath;
    }

    public void setCommentMediaUploadedPath(String commentMediaUploadedPath) {
        this.commentMediaUploadedPath = commentMediaUploadedPath;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public int getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(int currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }
}
