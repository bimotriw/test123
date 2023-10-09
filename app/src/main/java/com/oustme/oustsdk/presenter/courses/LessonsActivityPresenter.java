package com.oustme.oustsdk.presenter.courses;

import com.oustme.oustsdk.activity.courses.LessonsActivity;

/**
 * Created by oust on 7/7/17.
 */

public class LessonsActivityPresenter {

    private LessonsActivity view;

    public LessonsActivityPresenter(LessonsActivity view) {
        this.view = view;
    }

    public void hideLoaderandShowError(){
        view.hideWebViewError();
    }
}
