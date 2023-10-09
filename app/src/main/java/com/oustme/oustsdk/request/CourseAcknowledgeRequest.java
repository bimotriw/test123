package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class CourseAcknowledgeRequest {
    private String studentid;
    private String ackTimestamp;
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

    public int getCourseColnId() {
        return courseColnId;
    }

    public void setCourseColnId(int courseColnId) {
        this.courseColnId = courseColnId;
    }
}
