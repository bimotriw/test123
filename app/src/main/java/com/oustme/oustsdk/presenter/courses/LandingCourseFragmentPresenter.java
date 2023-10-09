package com.oustme.oustsdk.presenter.courses;

import com.oustme.oustsdk.fragments.courses.LandingCourseFragment;

/**
 * Created by admin on 02/12/17.
 */

public class LandingCourseFragmentPresenter {
    private LandingCourseFragment view;

    public LandingCourseFragmentPresenter(LandingCourseFragment view) {
        this.view = view;
    }

    public void showCourses(int type){
        if(type==1){
            view.showCompletedCourses();
        }else if(type==2){
            view.showPendingCourses();
        }else if(type==4){
            view.createMainList();
        }else {
            view.startCreateList();
        }
    }
}
