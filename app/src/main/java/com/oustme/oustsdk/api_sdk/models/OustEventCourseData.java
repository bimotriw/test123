package com.oustme.oustsdk.api_sdk.models;

import androidx.annotation.Keep;

@Keep
public class OustEventCourseData {

    private String type;
    private String requestType;
    private long courseId;
    private boolean regularMode;
    private int eventId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean isRegularMode() {
        return regularMode;
    }

    public void setRegularMode(boolean regularMode) {
        this.regularMode = regularMode;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
