package com.oustme.oustsdk.interfaces.common;

import com.oustme.oustsdk.firebase.assessment.AssessmentFirebaseClass;

import java.util.List;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public interface NewLandingInterface {
    void setOcText();
    void setProgressText(List<AssessmentFirebaseClass> assessmentFirebaseClassList);
    void hideBanner();
    void showBanner();
    void showPendingCoursesUI();
    void showAllCourseUI();
    void showAlertIcon();
    void isSearchOn(boolean isSearchOn);
}
