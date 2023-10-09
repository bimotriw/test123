package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class SignInRequest extends OustRequest {

    private DeviceInfoData deviceInfoData;

    private String deviceToken;

    private String devicePlatformName;

    private String deviceIdentity;

    private String password;

    private boolean clientEncryptedPassword;

    private String version;

    private String studentid;

    private String country;

    private String institutionLoginId;

    private boolean forceLogin;

    public DeviceInfoData getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
        this.deviceInfoData = deviceInfoData;
    }

    public String getDeviceToken() {
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

    public boolean isForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

    @Override
    public String toString() {
        return "SignInRequest{" +
                "deviceInfoData=" + deviceInfoData +
                ", deviceToken='" + deviceToken + '\'' +
                ", devicePlatformName='" + devicePlatformName + '\'' +
                ", deviceIdentity='" + deviceIdentity + '\'' +
                ", password='" + password + '\'' +
                ", clientEncryptedPassword=" + clientEncryptedPassword +
                ", version='" + version + '\'' +
                ", studentid='" + studentid + '\'' +
                ", country='" + country + '\'' +
                ", institutionLoginId='" + institutionLoginId + '\'' +
                ", forceLogin="+ forceLogin +'\''+
                '}';
    }
}
