package com.oustme.oustsdk.interfaces.course;

import java.io.Serializable;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

public interface LearningCallBackInterface extends Serializable {
    void startUpdatedLearningMap(boolean killActivity, boolean updateReviewList);

    void restartCourseLearningModuleActivity(boolean restartLevelStatus, int levelId);

    void upDateLevelTime(boolean restartLevelStatus, int levelId);
}
