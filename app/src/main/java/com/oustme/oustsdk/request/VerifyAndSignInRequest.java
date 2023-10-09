package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class VerifyAndSignInRequest extends OustRequest {

    private DeviceInfoData deviceInfoData;
    //private String deviceToken;
    //private String devicePlatformName;
    //private String deviceIdentity;
    //private String appVersion;

    private String password;
    private boolean clientEncryptedPassword;
    private String version;
    private String studentid;
    private String country;
    private String institutionLoginId;

    private String fcmToken;
    private String fbTokenId;
    private String userRole;
    private String orgId;
    private String fname;
    private String lname;
    private String avatar;
    private String phone;
    private String gender;
    private String age;
    private String city;
    private String state;
    private String session;
    private String latitude;
    private String longitude;
    private String authToken;
    private String apiVersion="1.0";

    public DeviceInfoData getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
        this.deviceInfoData = deviceInfoData;
    }

    /*public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDevicePlatformName() {
        return devicePlatformName;
    }

    public void setDevicePlatformName(String devicePlatformName) {
        this.devicePlatformName = devicePlatformName;
    }

    public String getDeviceIdentity() {
        return deviceIdentity;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }*/

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getClientEncryptedPassword() {
        return clientEncryptedPassword;
    }

    public void setClientEncryptedPassword(boolean clientEncryptedPassword) {
        this.clientEncryptedPassword = clientEncryptedPassword;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public boolean isClientEncryptedPassword() {
        return clientEncryptedPassword;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getInstitutionLoginId() {
        return institutionLoginId;
    }

    public void setInstitutionLoginId(String institutionLoginId) {
        this.institutionLoginId = institutionLoginId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFbTokenId() {
        return fbTokenId;
    }

    public void setFbTokenId(String fbTokenId) {
        this.fbTokenId = fbTokenId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
