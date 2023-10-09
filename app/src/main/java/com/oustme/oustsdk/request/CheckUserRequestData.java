package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 15/11/17.
 */

@Keep
public class CheckUserRequestData {

    private String orgId;
    private String mobile;
    private String country;

    private String c_userIdentifier;
    private String c_application;
    private String c_applicationId;
    private String c_appVersion;
    private String c_userAgent;
    private String c_deviceId;
    private String c_tokenId;
    private boolean c_isAuthorizationReq;
    private String c_emailID;
    private double longitude;

    private double latitude;
    private String fname;
    private String lname;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getC_userIdentifier() {
        return c_userIdentifier;
    }

    public void setC_userIdentifier(String c_userIdentifier) {
        this.c_userIdentifier = c_userIdentifier;
    }

    public String getC_application() {
        return c_application;
    }

    public void setC_application(String c_application) {
        this.c_application = c_application;
    }

    public String getC_applicationId() {
        return c_applicationId;
    }

    public void setC_applicationId(String c_applicationId) {
        this.c_applicationId = c_applicationId;
    }

    public String getC_appVersion() {
        return c_appVersion;
    }

    public void setC_appVersion(String c_appVersion) {
        this.c_appVersion = c_appVersion;
    }

    public String getC_userAgent() {
        return c_userAgent;
    }

    public void setC_userAgent(String c_userAgent) {
        this.c_userAgent = c_userAgent;
    }

    public String getC_deviceId() {
        return c_deviceId;
    }

    public void setC_deviceId(String c_deviceId) {
        this.c_deviceId = c_deviceId;
    }

    public String getC_tokenId() {
        return c_tokenId;
    }

    public void setC_tokenId(String c_tokenId) {
        this.c_tokenId = c_tokenId;
    }

    public boolean isC_isAuthorizationReq() {
        return c_isAuthorizationReq;
    }

    public void setC_isAuthorizationReq(boolean c_isAuthorizationReq) {
        this.c_isAuthorizationReq = c_isAuthorizationReq;
    }

    public String getC_emailID() {
        return c_emailID;
    }

    public void setC_emailID(String c_emailID) {
        this.c_emailID = c_emailID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
}
