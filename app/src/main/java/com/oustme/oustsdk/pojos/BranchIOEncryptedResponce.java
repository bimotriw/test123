package com.oustme.oustsdk.pojos;

/**
 * Created by admin on 11/04/17.
 */

public class BranchIOEncryptedResponce {
    private String userId;
    private String password;
    private String orgId;
    private String fname;
    private String lname;
    private String profileImage;
    private String emailid;  // Required for sending course certificate to user
    private String rmEmailid; // Required for RM alert

    private String mobile;

    private String application;
    private String applicationId;
    private String appVersion;
    private String userAgent;
    private String deviceId;
    private String tokenId;
    private boolean isAuthorizationReq;
    private String country;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getRmEmailid() {
        return rmEmailid;
    }

    public void setRmEmailid(String rmEmailid) {
        this.rmEmailid = rmEmailid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public boolean getAuthorizationReq() {
        return isAuthorizationReq;
    }

    public void setAuthorizationReq(boolean authorizationReq) {
        isAuthorizationReq = authorizationReq;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
