package com.oustme.oustsdk.survey_module.model;

import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.response.assessment.Scores;
import com.oustme.oustsdk.response.common.PlayResponse;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;

import java.util.List;

public class SurveyComponentModule {

    ComponentToolBar componentToolBar;
    SurveyModule surveyModule;
    boolean isIntroCard;
    boolean isResultCard;
    DTOCourseCard cardClass;
    DTONewFeed feed;
    DTOUserFeedDetails.FeedDetails userFeed;
    boolean isFeedChange;
    boolean isFeedComment;
    AssessmentPlayResponse surveyPlayResponse;
    int progress;
    String progressDone;
    String downloadProgress;
    private long challengerFinalScore;
    private int questionIndex;
    private List<Scores> scoresList;
    private boolean assessmentRunning = false;
    private String startDateTime;
    private String activeGameId;
    private boolean isFetchCompleted;
    PlayResponse playResponse;
    long exitOC;
    long rewardOC;
    long courseId;

    long cplId, associatedAssessmentId;
    long mappedAssessmentId;

    public ComponentToolBar getComponentToolBar() {
        return componentToolBar;
    }

    public void setComponentToolBar(ComponentToolBar componentToolBar) {
        this.componentToolBar = componentToolBar;
    }

    public SurveyModule getSurveyModule() {
        return surveyModule;
    }

    public void setSurveyModule(SurveyModule surveyModule) {
        this.surveyModule = surveyModule;
    }

    public boolean isIntroCard() {
        return isIntroCard;
    }

    public void setIntroCard(boolean introCard) {
        isIntroCard = introCard;
    }

    public boolean isResultCard() {
        return isResultCard;
    }

    public void setResultCard(boolean resultCard) {
        isResultCard = resultCard;
    }

    public DTOCourseCard getCardClass() {
        return cardClass;
    }

    public void setCardClass(DTOCourseCard cardClass) {
        this.cardClass = cardClass;
    }

    public DTONewFeed getFeed() {
        return feed;
    }

    public void setFeed(DTONewFeed feed) {
        this.feed = feed;
    }

    public DTOUserFeedDetails.FeedDetails getUserFeed() {
        return userFeed;
    }

    public void setUserFeed(DTOUserFeedDetails.FeedDetails userFeed) {
        this.userFeed = userFeed;
    }

    public boolean isFeedChange() {
        return isFeedChange;
    }

    public void setFeedChange(boolean feedChange) {
        isFeedChange = feedChange;
    }

    public boolean isFeedComment() {
        return isFeedComment;
    }

    public void setFeedComment(boolean feedComment) {
        isFeedComment = feedComment;
    }

    public AssessmentPlayResponse getSurveyPlayResponse() {
        return surveyPlayResponse;
    }

    public void setSurveyPlayResponse(AssessmentPlayResponse surveyPlayResponse) {
        this.surveyPlayResponse = surveyPlayResponse;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressDone() {
        return progressDone;
    }

    public void setProgressDone(String progressDone) {
        this.progressDone = progressDone;
    }

    public String getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(String downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public long getChallengerFinalScore() {
        return challengerFinalScore;
    }

    public void setChallengerFinalScore(long challengerFinalScore) {
        this.challengerFinalScore = challengerFinalScore;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public List<Scores> getScoresList() {
        return scoresList;
    }

    public void setScoresList(List<Scores> scoresList) {
        this.scoresList = scoresList;
    }

    public boolean isAssessmentRunning() {
        return assessmentRunning;
    }

    public void setAssessmentRunning(boolean assessmentRunning) {
        this.assessmentRunning = assessmentRunning;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getActiveGameId() {
        return activeGameId;
    }

    public void setActiveGameId(String activeGameId) {
        this.activeGameId = activeGameId;
    }

    public boolean isFetchCompleted() {
        return isFetchCompleted;
    }

    public void setFetchCompleted(boolean fetchCompleted) {
        isFetchCompleted = fetchCompleted;
    }

    public PlayResponse getPlayResponse() {
        return playResponse;
    }

    public void setPlayResponse(PlayResponse playResponse) {
        this.playResponse = playResponse;
    }

    public long getExitOC() {
        return exitOC;
    }

    public void setExitOC(long exitOC) {
        this.exitOC = exitOC;
    }

    public long getRewardOC() {
        return rewardOC;
    }

    public void setRewardOC(long rewardOC) {
        this.rewardOC = rewardOC;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public long getAssociatedAssessmentId() {
        return associatedAssessmentId;
    }

    public void setAssociatedAssessmentId(long associatedAssessmentId) {
        this.associatedAssessmentId = associatedAssessmentId;
    }

    public long getMappedAssessmentId() {
        return mappedAssessmentId;
    }

    public void setMappedAssessmentId(long mappedAssessmentId) {
        this.mappedAssessmentId = mappedAssessmentId;
    }
}
