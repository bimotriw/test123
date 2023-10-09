package com.oustme.oustsdk.assessment_ui.assessmentDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AssessmentDetailsVMFactory implements ViewModelProvider.Factory {

    private final AssessmentDetailRepo assessmentDetailRepo;

    public AssessmentDetailsVMFactory(AssessmentDetailRepo assessmentDetailRepo) {
        this.assessmentDetailRepo = assessmentDetailRepo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AssessmentDetailVM.class)) {
            return (T) new AssessmentDetailVM(assessmentDetailRepo);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
