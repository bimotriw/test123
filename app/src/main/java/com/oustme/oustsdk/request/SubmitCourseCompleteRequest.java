package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class SubmitCourseCompleteRequest {
    private String courseId;
    private String userId;
    private String timeStamp;
    private String courseColnId;
    private boolean isCPLCourse;

    private  long contentPlayListId;
    private  long contentPlayListSlotId;
    private long contentPlayListSlotItemId;
    private long cplId;

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(String courseColnId) {
        this.courseColnId = courseColnId;
    }

    public long getContentPlayListId() {
        return contentPlayListId;
    }

    public void setContentPlayListId(long contentPlayListId) {
        this.contentPlayListId = contentPlayListId;
    }

    public long getContentPlayListSlotId() {
        return contentPlayListSlotId;
    }

    public void setContentPlayListSlotId(long contentPlayListSlotId) {
        this.contentPlayListSlotId = contentPlayListSlotId;
    }

    public long getContentPlayListSlotItemId() {
        return contentPlayListSlotItemId;
    }

    public void setContentPlayListSlotItemId(long contentPlayListSlotItemId) {
        this.contentPlayListSlotItemId = contentPlayListSlotItemId;
    }

    public boolean getIsCPLCourse() {
        return isCPLCourse;
    }

    public void setIsCPLCourse(boolean isCPLCourse) {
        this.isCPLCourse = isCPLCourse;
    }
}
