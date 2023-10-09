package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class EventRegistarationRequest {
    private String studentid;
    private String eventId;
    private String emailId;
    private String mobile;
    private String grade;
    private String college;
    private String city;
    private String deviceToken;
    private String participationCode;
    private String deviceIdentity;

    public String getStudentid() {
        return studentid;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEmailid() {
        return emailId;
    }

    public String getMobile() {
        return mobile;
    }

    public String getGrade() {
        return grade;
    }

    public String getCollege() {
        return college;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmailid(String emailid) {
        this.emailId = emailid;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getParticipationCode() {
        return participationCode;
    }

    public void setParticipationCode(String participationCode) {
        this.participationCode = participationCode;
    }

    public String getDeviceIdentity() {
        return deviceIdentity;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    @Override
    public String toString() {
        return "EventRegistarationRequest{" +
                "studentid='" + studentid + '\'' +
                ", eventId='" + eventId + '\'' +
                ", emailId='" + emailId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", grade='" + grade + '\'' +
                ", college='" + college + '\'' +
                ", city='" + city + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", participationCode='" + participationCode + '\'' +
                ", deviceIdentity='" + deviceIdentity + '\'' +
                '}';
    }
}
