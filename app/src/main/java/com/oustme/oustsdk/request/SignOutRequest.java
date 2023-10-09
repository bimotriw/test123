package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class SignOutRequest extends OustRequest {


    private String institutionLoginId;

    private String studentid;

    private String deviceIdentity;

    private String deviceToken;

    private String devicePlatformName;

    private String authToken;


    // Getter Methods

    public String getInstitutionLoginId() {
        return institutionLoginId;
    }

    public String getStudentid() {
        return studentid;
    }

    public String getDeviceIdentity() {
        return deviceIdentity;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDevicePlatformName() {
        return devicePlatformName;
    }

    public String getAuthToken() {
        return authToken;
    }

    // Setter Methods

    public void setInstitutionLoginId(String institutionLoginId) {
        this.institutionLoginId = institutionLoginId;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setDevicePlatformName(String devicePlatformName) {
        this.devicePlatformName = devicePlatformName;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}