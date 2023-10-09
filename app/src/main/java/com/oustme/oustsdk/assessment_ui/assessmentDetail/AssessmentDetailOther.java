package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;
import com.oustme.oustsdk.response.assessment.AssessmentPlayResponse;
import com.oustme.oustsdk.room.dto.DTOQuestions;
import com.oustme.oustsdk.tools.ActiveGame;

import java.util.ArrayList;
import java.util.List;

public class AssessmentDetailOther {
    private int type;
    private ActiveGame activeGame;
    private AssessmentFirebaseClass assessmentFirebaseClass;
    private AssessmentPlayResponse assessmentPlayResponse;
    private DTOQuestions realmQuestions;
    private ArrayList<DTOQuestions> questionsArrayList;
    private List<String> mediaList;
    private AssessmentExtraDetails assessmentExtraDetails;
    private boolean nextWorkFail;
    private String conditionalFlowUrl;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ActiveGame getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    public AssessmentFirebaseClass getAssessmentFirebaseClass() {
        return assessmentFirebaseClass;
    }

    public void setAssessmentFirebaseClass(AssessmentFirebaseClass assessmentFirebaseClass) {
        this.assessmentFirebaseClass = assessmentFirebaseClass;
    }

    public AssessmentPlayResponse getAssessmentPlayResponse() {
        return assessmentPlayResponse;
    }

    public void setAssessmentPlayResponse(AssessmentPlayResponse assessmentPlayResponse) {
        this.assessmentPlayResponse = assessmentPlayResponse;
    }

    public DTOQuestions getRealmQuestions() {
        return realmQuestions;
    }

    public void setRealmQuestions(DTOQuestions realmQuestions) {
        this.realmQuestions = realmQuestions;
    }

    public ArrayList<DTOQuestions> getQuestionsArrayList() {
        return questionsArrayList;
    }

    public void setQuestionsArrayList(ArrayList<DTOQuestions> questionsArrayList) {
        this.questionsArrayList = questionsArrayList;
    }

    public List<String> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<String> mediaList) {
        this.mediaList = mediaList;
    }

    public AssessmentExtraDetails getAssessmentExtraDetails() {
        return assessmentExtraDetails;
    }

    public void setAssessmentExtraDetails(AssessmentExtraDetails assessmentExtraDetails) {
        this.assessmentExtraDetails = assessmentExtraDetails;
    }

    public boolean isNextWorkFail() {
        return nextWorkFail;
    }

    public void setNextWorkFail(boolean nextWorkFail) {
        this.nextWorkFail = nextWorkFail;
    }

    public String getConditionalFlowUrl() {
        return conditionalFlowUrl;
    }

    public void setConditionalFlowUrl(String conditionalFlowUrl) {
        this.conditionalFlowUrl = conditionalFlowUrl;
    }
}
