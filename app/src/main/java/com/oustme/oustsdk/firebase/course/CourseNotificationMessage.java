package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

/**
 * Created by admin on 13/10/16.
 */
@Keep
public class CourseNotificationMessage {
    private String title;
    private String enrolledContent;
    private String content;
    private long lastUpadetTime;
    private String studentKey;
    private String courseId;
    private boolean delete;
    private int noofAttempt;
    private long reminderNotificationInterval;
    private boolean isCollection;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnrolledContent() {
        return enrolledContent;
    }

    public void setEnrolledContent(String enrolledContent) {
        this.enrolledContent = enrolledContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLastUpadetTime() {
        return lastUpadetTime;
    }

    public void setLastUpadetTime(long lastUpadetTime) {
        this.lastUpadetTime = lastUpadetTime;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getNoofAttempt() {
        return noofAttempt;
    }

    public void setNoofAttempt(int noofAttempt) {
        this.noofAttempt = noofAttempt;
    }

    public long getReminderNotificationInterval() {
        return reminderNotificationInterval;
    }

    public void setReminderNotificationInterval(long reminderNotificationInterval) {
        this.reminderNotificationInterval = reminderNotificationInterval;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
