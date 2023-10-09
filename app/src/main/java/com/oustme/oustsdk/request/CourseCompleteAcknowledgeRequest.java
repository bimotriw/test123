package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class CourseCompleteAcknowledgeRequest {
    private String studentid;
    private String ackTimestamp;
    private String courseId;
    private int courseColnId;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getAckTimestamp() {
        return ackTimestamp;
    }

    public void setAckTimestamp(String ackTimestamp) {
        this.ackTimestamp = ackTimestamp;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(int courseColnId) {
        this.courseColnId = courseColnId;
    }
}
