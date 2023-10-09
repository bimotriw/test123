package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 17/04/17.
 */

@Keep
public class AnyOpenRequest {
    private String topic;

    private String subject;

    private String grade;

    private String studentid;

    private String moduleId;

    private String eventCode;

    private int lpId;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }


    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public int getLpId() {
        return lpId;
    }

    public void setLpId(int lpId) {
        this.lpId = lpId;
    }

    @Override
    public String toString() {
        return "AnyOpenRequest{" +
                "topic='" + topic + '\'' +
                ", subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", studentid='" + studentid + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", lpId=" + lpId +
                '}';
    }
}
