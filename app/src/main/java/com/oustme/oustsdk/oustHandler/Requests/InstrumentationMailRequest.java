package com.oustme.oustsdk.oustHandler.Requests;

public class InstrumentationMailRequest {

    private String userid;
    private long moduleId;
    private String moduleType;
    private String messageDesc;
    private String issuesType;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getMessageDesc() {
        return messageDesc;
    }

    public void setMessageDesc(String messageDesc) {
        this.messageDesc = messageDesc;
    }

    public String getIssuesType() {
        return issuesType;
    }

    public void setIssuesType(String issuesType) {
        this.issuesType = issuesType;
    }
}
