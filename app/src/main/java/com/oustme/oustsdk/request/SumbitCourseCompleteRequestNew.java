package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class SumbitCourseCompleteRequestNew {
    private String studentid;
    private String timestamp;
    private String courseColnId;
    private long contentPlayListId;
    private long contentPlayListSlotId;
    private long contentPlayListSlotItemId;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}
