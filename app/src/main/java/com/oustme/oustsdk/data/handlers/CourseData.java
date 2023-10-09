package com.oustme.oustsdk.data.handlers;

import com.oustme.oustsdk.firebase.common.CommonLandingData;

/**
 * Created by oust on 7/31/18.
 */

public interface CourseData {
    void gotUserCourseData(CommonLandingData commonLandingData, long key);
    void gotUserAssessmentData(CommonLandingData commonLandingData, long key);

}
