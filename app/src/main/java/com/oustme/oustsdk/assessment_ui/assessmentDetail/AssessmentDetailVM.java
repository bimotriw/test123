package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import androidx.lifecycle.MutableLiveData;

import com.oustme.oustsdk.mvvm.BaseViewModel;

class AssessmentDetailVM extends BaseViewModel {

    private MutableLiveData<AssessmentDetailModel> assessmentDetailModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AssessmentDetailOther> assessmentDetailOtherMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AssessmentProgressbar> assessmentProgressbarMutableLiveData = new MutableLiveData<>();

    AssessmentDetailRepo assessmentDetailRepo;

    AssessmentDetailVM(AssessmentDetailRepo assessmentDetailRepo) {
        this.assessmentDetailRepo = assessmentDetailRepo;
    }

    void loadAssessmentDetailData() {
        assessmentDetailModelMutableLiveData = assessmentDetailRepo.assessmentDetailModelMutableLiveData;
    }

    MutableLiveData<AssessmentDetailModel> getAssessmentDetail() {
        return assessmentDetailModelMutableLiveData;
    }

    void loadAssessmentOtherDetailData() {
        assessmentDetailOtherMutableLiveData = assessmentDetailRepo.assessmentDetailOtherMutableLiveData;
    }

    MutableLiveData<AssessmentDetailOther> getAssessmentOtherDetail() {
        return assessmentDetailOtherMutableLiveData;
    }

    void loadAssessmentProgressData() {
        assessmentProgressbarMutableLiveData = assessmentDetailRepo.assessmentProgressbarMutableLiveData;
    }

    MutableLiveData<AssessmentProgressbar> getAssessmentProgress() {
        return assessmentProgressbarMutableLiveData;
    }

    void layout_StartButton() {
        assessmentDetailRepo.startAssessment();
    }

    void layout_SubmitButton() {
        assessmentDetailRepo.submitAssessmentGame();
    }

    void layout_AnswerButton() {
        assessmentDetailRepo.checkAssessmentState();
    }
}
