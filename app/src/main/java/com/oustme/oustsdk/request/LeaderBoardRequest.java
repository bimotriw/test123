package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class LeaderBoardRequest {
    private String subject;
    private String period;
    private String classcode;
    private String groupid;
    private String gradeStr;
    private String studentid;
    private String eventCode;
    private String moduleId;
    private String lpId;
    private String assessmentId;
    private String orgID;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGrade() {
        return gradeStr;
    }

    public void setGrade(String grade) {
        this.gradeStr = grade;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getLpId() {
        return lpId;
    }

    public void setLpId(String lpId) {
        this.lpId = lpId;
    }

    public String getGradeStr() {
        return gradeStr;
    }

    public void setGradeStr(String gradeStr) {
        this.gradeStr = gradeStr;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }
}
