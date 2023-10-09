package com.oustme.oustsdk.assessment_ui.assessmentCompletion;

import androidx.lifecycle.MutableLiveData;

import com.oustme.oustsdk.mvvm.BaseViewModel;

public class AssessmentCompletionVM extends BaseViewModel {

    private MutableLiveData<AssessmentCompletionModel> assessmentCompletionModelMutableLiveData = new MutableLiveData<>();
    private AssessmentCompletionModel assessmentCompletionModel;
    AssessmentCompletionRepo assessmentCompletionRepo;

    AssessmentCompletionVM( AssessmentCompletionRepo assessmentCompletionRepo) {
        this.assessmentCompletionRepo = assessmentCompletionRepo;

    }

    void loadAssessmenCompletionData() {
        assessmentCompletionModelMutableLiveData = assessmentCompletionRepo.assessmentCompletionModelMutableLiveData;
    }

    MutableLiveData<AssessmentCompletionModel> getAssessmentCompletion() {
        return assessmentCompletionModelMutableLiveData;
    }

    public void getCourseDetails() {
        assessmentCompletionRepo.getCourseDetails();
    }
}
