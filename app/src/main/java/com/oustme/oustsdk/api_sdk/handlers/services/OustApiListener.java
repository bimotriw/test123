package com.oustme.oustsdk.api_sdk.handlers.services;

import com.oustme.oustsdk.api_sdk.models.OustEventResponseData;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.model.response.course.UserEventCourseData;
import com.oustme.oustsdk.room.dto.UserEventCplData;

/**
 * Created by oust on 4/30/19.
 */

public interface OustApiListener {
    void onStartDownloadingResourses();

    void onProgressChanged(int var1);

    void onError(String var1);

    void onLoginError(String var1);

    void onLoginProcessStart();

    void onLoginSuccess(boolean var1);

    void onNetworkError();

    void onModuleComplete(OustModuleData oustModuleData);

    void onModuleProgress(OustModuleData oustModuleData, int var2);

    void onModuleStatusChange(OustModuleData oustModuleData, String var2);

    void onModuleFailed(OustModuleData oustModuleData);

    void onEventModuleStatusChange(OustModuleData oustModuleData, String var2);

    void onResourcesInitialized();

    void onResourcesRemoved();

    void onLanguageUpdated();

    void onUserDisplayNameUpdated();

    void onUserPreferredLanguage(String languageName);

    void onLogoutSuccess();

    void onCourseComplete();

    void onCplCompleted();

    void onCplFailed();

    void onEventCourseStatusChange(OustEventResponseData oustEventResponseData, String var2);

    void onCourseProgress(UserEventCourseData userEventCourseData);

    void onAssessmentProgress(UserEventAssessmentData userEventAssessmentData);

    void onCPLProgress(UserEventCplData userEventCplData);

    void onOustContentLoaded();

    void onOustLoginStatus(String message);

}
