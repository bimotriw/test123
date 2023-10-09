package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 22/03/17.
 */

@Keep
public class UserSettingRequest {
    private String password;
    private int grade;
    private String classcode;
    private String email;// TODO Need to get the correct attr name
    private String hq; // hint question
    private String ha; // hint answer
    private String fname;
    private String lname;
    private String userDisplayName;
    private String avatar;
    private String city;
    private String state;
    private String country;
    private String schoolName;
    private String phone;
    private String gradeStr;
    private String dob;
    private String gender;
    private String avatarImgData;
    private String goal;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
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

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGradeStr() {
        return gradeStr;
    }

    public void setGradeStr(String gradeStr) {
        this.gradeStr = gradeStr;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAvatarImgData() {
        return avatarImgData;
    }

    public void setAvatarImgData(String avatarImgData) {
        this.avatarImgData = avatarImgData;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return "UserSettingRequest{" +
                "password='" + password + '\'' +
                ", grade=" + grade +
                ", classcode='" + classcode + '\'' +
                ", email='" + email + '\'' +
                ", hq='" + hq + '\'' +
                ", ha='" + ha + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", phone='" + phone + '\'' +
                ", gradeStr='" + gradeStr + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", avatarImgData='" + avatarImgData + '\'' +
                ", goal='" + goal + '\'' +
                '}';
    }
}
