package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

@Keep
public class CourseRating_Request {
    private String courseId;
    private String userId;
    private int rating;
    private String courseColnId;
    private String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }
}
