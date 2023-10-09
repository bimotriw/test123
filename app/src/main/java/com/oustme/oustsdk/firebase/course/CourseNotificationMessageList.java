package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import java.util.Map;

@Keep
public class CourseNotificationMessageList {
    private Map<String,CourseNotificationMessage> courseNotificationMessages;

    public Map<String, CourseNotificationMessage> getCourseNotificationMessages() {
        return courseNotificationMessages;
    }

    public void setCourseNotificationMessages(Map<String, CourseNotificationMessage> courseNotificationMessages) {
        this.courseNotificationMessages = courseNotificationMessages;
    }
}
