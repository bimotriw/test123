package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class AssmntGamePlayRequest extends OustRequest {
    private String gameid;

    private String studentid;

    private String assessmentId;
    private long courseId;
    private long surveyid;

    private boolean onlyQId;

    private String devicePlatformName;

    private String eventCode;

    public void setGameid(String gameid){
        this.gameid = gameid;
    }
    public String getGameid(){
        return this.gameid;
    }
    public void setStudentid(String studentid){
        this.studentid = studentid;
    }
    public String getStudentid(){
        return this.studentid;
    }
    public void setAssessmentId(String assessmentId){
        this.assessmentId = assessmentId;
    }
    public String getAssessmentId(){
        return this.assessmentId;
    }
    public void setOnlyQId(boolean onlyQId){
        this.onlyQId = onlyQId;
    }
    public boolean getOnlyQId(){
        return this.onlyQId;
    }
    public void setDevicePlatformName(String devicePlatformName){
        this.devicePlatformName = devicePlatformName;
    }
    public String getDevicePlatformName(){
        return this.devicePlatformName;
    }

    public boolean isOnlyQId() {
        return onlyQId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(long surveyid) {
        this.surveyid = surveyid;
    }
}
