package com.oustme.oustsdk.api_sdk.models;

import androidx.annotation.Keep;

@Keep
public class OustMultilingualCourseData {

    private String courseChildId;
    private String courseParentId;
    private String courseLanguageId;
    private String courseLanguageName;
    private int eventId;

    public String getCourseChildId() {
        return courseChildId;
    }

    public void setCourseChildId(String courseChildId) {
        this.courseChildId = courseChildId;
    }

    public String getCourseParentId() {
        return courseParentId;
    }

    public void setCourseParentId(String courseParentId) {
        this.courseParentId = courseParentId;
    }

    public String getCourseLanguageId() {
        return courseLanguageId;
    }

    public void setCourseLanguageId(String courseLanguageId) {
        this.courseLanguageId = courseLanguageId;
    }

    public String getCourseLanguageName() {
        return courseLanguageName;
    }

    public void setCourseLanguageName(String courseLanguageName) {
        this.courseLanguageName = courseLanguageName;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
