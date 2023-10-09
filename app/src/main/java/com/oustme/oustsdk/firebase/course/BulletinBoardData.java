package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/08/17.
 */

@Keep
public class BulletinBoardData {
    private long addedOnDate;
    private long courseId,cplId;
    private String courseName,cplName;
    private long numComments;
    private String question;
    private String userAvatar;
    private String userDisplayName;
    private String userId;
    private long userKey;
    private String quesKey;
    private String devicePlatform;
    private String comment;

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public String getCplName() {
        return cplName;
    }

    public void setCplName(String cplName) {
        this.cplName = cplName;
    }

    public long getAddedOnDate() {
        return addedOnDate;
    }

    public void setAddedOnDate(long addedOnDate) {
        this.addedOnDate = addedOnDate;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuesKey() {
        return quesKey;
    }

    public void setQuesKey(String quesKey) {
        this.quesKey = quesKey;
    }

    public String getDevicePlatform() {
        return devicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        this.devicePlatform = devicePlatform;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getUserKey() {
        return userKey;
    }

    public void setUserKey(long userKey) {
        this.userKey = userKey;
    }
}
