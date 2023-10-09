package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class CurrentGameInfo{
    private String gameid;


    private String grade;

    private String subject;

    private String topic;

    private String moduleId;

    private String moduleName;

    public String getGameid() {
        return gameid;
    }

    public String getGrade() {
        return grade;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
