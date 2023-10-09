package com.oustme.oustsdk.room.dto;

import androidx.annotation.Keep;

import com.oustme.oustsdk.model.response.assessment.UserEventAssessmentData;
import com.oustme.oustsdk.model.response.course.UserEventCourseData;

/**
 * Created by Durai on 25/Jun/2020.
 */

@Keep
public class UserEventCplData {
    private long cplid;
    private String completedOn;
    private int nTotalModules;
    private int nModulesCompleted;
    private String userProgress;

    private long currentModuleId;
    private String currentModuleType;
    private String currentModuleProgress;
    private int eventId;

    private UserEventAssessmentData userEventAssessmentData;
    private UserEventCourseData userEventCourseData;

    public void UserEventCplData(){

    }

    public long getCplid() {
        return cplid;
    }

    public void setCplid(long cplid) {
        this.cplid = cplid;
    }

    public String getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(String completedOn) {
        this.completedOn = completedOn;
    }

    public int getnTotalModules() {
        return nTotalModules;
    }

    public void setnTotalModules(int nTotalModules) {
        this.nTotalModules = nTotalModules;
    }

    public int getnModulesCompleted() {
        return nModulesCompleted;
    }

    public void setnModulesCompleted(int nModulesCompleted) {
        this.nModulesCompleted = nModulesCompleted;
    }

    public String getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(String userProgress) {
        this.userProgress = userProgress;
    }

    public long getCurrentModuleId() {
        return currentModuleId;
    }

    public void setCurrentModuleId(long currentModuleId) {
        this.currentModuleId = currentModuleId;
    }

    public String getCurrentModuleType() {
        return currentModuleType;
    }

    public void setCurrentModuleType(String currentModuleType) {
        this.currentModuleType = currentModuleType;
    }

    public String getCurrentModuleProgress() {
        return currentModuleProgress;
    }

    public void setCurrentModuleProgress(String currentModuleProgress) {
        this.currentModuleProgress = currentModuleProgress;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public UserEventAssessmentData getUserEventAssessmentData() {
        return userEventAssessmentData;
    }

    public void setUserEventAssessmentData(UserEventAssessmentData userEventAssessmentData) {
        this.userEventAssessmentData = userEventAssessmentData;
    }

    public UserEventCourseData getUserEventCourseData() {
        return userEventCourseData;
    }

    public void setUserEventCourseData(UserEventCourseData userEventCourseData) {
        this.userEventCourseData = userEventCourseData;
    }
}
