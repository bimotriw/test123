package com.oustme.oustsdk.presenter.assessments;

import com.oustme.oustsdk.fragments.assessments.LandingAssessmentFragment;

/**
 * Created by admin on 02/12/17.
 */

public class LandingAssessmentFragmentPresenter {
    private LandingAssessmentFragment view;
    public LandingAssessmentFragmentPresenter(LandingAssessmentFragment view) {
        this.view = view;
    }

    public void showAssessments(int type){
        if(type==1){
            view.showCompletedAssessments();
        }else if(type==2){
            view.showPendingAssessments();
        }else if(type==4){
            view.createMainList();
        }else {
            view.startCreateList();
        }
    }
}
