package com.oustme.oustsdk.assessment_ui.assessmentCompletion;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AssessmentCompleteVMFactory implements ViewModelProvider.Factory {

    private final AssessmentCompletionRepo assessmentCompletionRepo;

    public AssessmentCompleteVMFactory(AssessmentCompletionRepo assessmentCompletionRepo) {
        this.assessmentCompletionRepo = assessmentCompletionRepo;
    }

    @NonNull
    @Override
    public <T extends ViewModel > T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AssessmentCompletionVM.class)) {
            return (T) new AssessmentCompletionVM(assessmentCompletionRepo);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
