package com.oustme.oustsdk.survey_module;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.firebase.common.AlertCommentData;
import com.oustme.oustsdk.survey_module.model.SurveyComponentModule;

public class SurveyComponentViewModel extends ViewModel {

    private MutableLiveData<SurveyComponentModule> surveyComponentModuleMutableLiveData;
    private SurveyComponentRepository surveyComponentRepository;
    Bundle bundleData;

    public void init(Bundle bundle,boolean isResume){

        if(surveyComponentModuleMutableLiveData!=null&&!isResume){
            return;
        }

        bundleData = bundle;
        surveyComponentRepository = SurveyComponentRepository.getInstance();
        surveyComponentModuleMutableLiveData = surveyComponentRepository.getLiveData(bundleData);
    }

    public void handleFeedComment(AlertCommentData alertCommentData){
        if(surveyComponentRepository!=null){
            surveyComponentRepository.sendFeedComment(alertCommentData);
        }

    }
    /*public void handleFeedComplete(){
        if(surveyComponentRepository!=null){
            surveyComponentRepository.handleFeedComplete();
        }

    }*/
    public void handleSurveyCompletePopUp(String content,boolean isExitSurvey){
        if(surveyComponentRepository!=null){
            surveyComponentRepository.handleSurveyCompletePopUp(content,isExitSurvey);
        }

    }
    public void checkSurveyState(){
        if(surveyComponentRepository!=null){
            surveyComponentRepository.checkSurveyState();
        }

    }

    public MutableLiveData<SurveyComponentModule> getBaseComponentModuleMutableLiveData(){
        return surveyComponentModuleMutableLiveData;
    }
}
