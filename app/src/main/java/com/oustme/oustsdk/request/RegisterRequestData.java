package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 15/11/17.
 */

@Keep
public class RegisterRequestData {
    private String password;
    private int grade;
    private String classcode;
    private String email;// TODO Need to get the correct attr name
    private String hq; // hint question
    private String ha; // hint answer
    private String fname;
    private String lname;
    private String avatar;
    private boolean clientEncryptedPassword;
    private DeviceInfoData deviceInfoData;
    private String phone;
    private String gender;
    private int age;
    private String birthDate;
    private String maritalStatus;
    private String location;
    private String loginId;
    private String profileImageUrl;
    private String city;
    private String state;
    private String country;
    private String school;
    private String fbTokenId;
    private String referralCode;
    private String role; // ADMIN,OUST_USER

    //For external user
    private boolean external;
    private String dummyUserId;

    private String orgId;
    private String studentid;
    private String deviceToken;
    private String deviceIdentity;

    private double longitude;

    private double latitude;

    //New language added
    private String language;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHq() {
        return hq;
    }

    public void setHq(String hq) {
        this.hq = hq;
    }

    public String getHa() {
        return ha;
    }

    public void setHa(String ha) {
        this.ha = ha;
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

    public boolean isClientEncryptedPassword() {
        return clientEncryptedPassword;
    }

    public void setClientEncryptedPassword(boolean clientEncryptedPassword) {
        this.clientEncryptedPassword = clientEncryptedPassword;
    }

    public DeviceInfoData getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
        this.deviceInfoData = deviceInfoData;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getFbTokenId() {
        return fbTokenId;
    }

    public void setFbTokenId(String fbTokenId) {
        this.fbTokenId = fbTokenId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getDummyUserId() {
        return dummyUserId;
    }

    public void setDummyUserId(String dummyUserId) {
        this.dummyUserId = dummyUserId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStudentid() {
        return studentid;
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

    public String getDeviceIdentity() {
        return deviceIdentity;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
