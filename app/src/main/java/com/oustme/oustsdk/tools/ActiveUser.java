package com.oustme.oustsdk.tools;

import androidx.annotation.Keep;

import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.profile.model.BadgeModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class ActiveUser implements Serializable {
    private String hq;

    private String appleStore;

    private String subject;

    private String grade;

    private String expDate;

    private String userDisplayName;

    private String devicePlatformName;

    private String avatar;

    private String deviceIdentity;

    private String ha;

    private String googleStore;

    private String session;

    private String email;

    private String studentid;

    private String studentKey;

    private long games;

    private int levelCompPercentage;

    private int level;

    private String levelPercentage;

    private int wins;

    private String topic;

    private String place;

    private String availableOCCount;

    private String cont_win_count;

    private String loss_count;

    private String newAlertCount;

    private String pendingOCCount;

    private String tie_count;

    private String xp;

    private String uid;

    private String newAlert;

    private String deviceToken;

    private String newChallenge;

    private String moduleName;

    private String moduleId;

    private String LoginType;

    private long dob;

    private String schoolName;

    private String userGrade;

    private long userAge;

    private String userGender;

    private String userCity;

    private String userCountry;

    private long userMobile;

    private String fname;

    private String lName;

    private String goal;

    private String department;

    private String businessCircle;

    private String batch;

    private boolean domainNameAuthentication;

    private HashMap<Long, BadgeModel> badges;

    private long certificateCount;

    private HashMap <Long, NotificationResponse> notification;
    private long registerDateTime;
    private long badgesCount;

    public long getBadgesCount() {
        return badgesCount;
    }

    public void setBadgesCount(long badgesCount) {
        this.badgesCount = badgesCount;
    }

    public String getLoginType() {
        return LoginType;
    }

    public void setLoginType(String loginType) {
        LoginType = loginType;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getHq() {
        return hq;
    }

    public void setHq(String hq) {
        this.hq = hq;
    }

    public String getAppleStore() {
        return appleStore;
    }

    public void setAppleStore(String appleStore) {
        this.appleStore = appleStore;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getDevicePlatformName() {
        return devicePlatformName;
    }

    public void setDevicePlatformName(String devicePlatformName) {
        this.devicePlatformName = devicePlatformName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDeviceIdentity() {
        return deviceIdentity;
    }

    public void setDeviceIdentity(String deviceIdentity) {
        this.deviceIdentity = deviceIdentity;
    }

    public String getHa() {
        return ha;
    }

    public void setHa(String ha) {
        this.ha = ha;
    }

    public String getGoogleStore() {
        return googleStore;
    }

    public void setGoogleStore(String googleStore) {
        this.googleStore = googleStore;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public long getGames() {
        return games;
    }

    public void setGames(long games) {
        this.games = games;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelPercentage() {
        return levelPercentage;
    }

    public void setLevelPercentage(String levelPercentage) {
        this.levelPercentage = levelPercentage;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getNewAlert() {
        return newAlert;
    }

    public void setNewAlert(String newAlert) {
        this.newAlert = newAlert;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getTie_count() {
        return tie_count;
    }

    public void setTie_count(String tie_count) {
        this.tie_count = tie_count;
    }

    public String getPendingOCCount() {
        return pendingOCCount;
    }

    public void setPendingOCCount(String pendingOCCount) {
        this.pendingOCCount = pendingOCCount;
    }

    public String getNewAlertCount() {
        return newAlertCount;
    }

    public void setNewAlertCount(String newAlertCount) {
        this.newAlertCount = newAlertCount;
    }

    public String getLoss_count() {
        return loss_count;
    }

    public void setLoss_count(String loss_count) {
        this.loss_count = loss_count;
    }

    public String getCont_win_count() {
        return cont_win_count;
    }

    public void setCont_win_count(String cont_win_count) {
        this.cont_win_count = cont_win_count;
    }

    public String getAvailableOCCount() {
        return availableOCCount;
    }

    public void setAvailableOCCount(String availableOCCount) {
        this.availableOCCount = availableOCCount;
    }

    public String getNewChallenge() {
        return newChallenge;
    }

    public void setNewChallenge(String newChallenge) {
        this.newChallenge = newChallenge;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }

    public int getLevelCompPercentage() {
        return levelCompPercentage;
    }

    public void setLevelCompPercentage(int levelCompPercentage) {
        this.levelCompPercentage = levelCompPercentage;
    }


    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public long getUserAge() {
        return userAge;
    }

    public void setUserAge(long userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public long getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(long userMobile) {
        this.userMobile = userMobile;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBusinessCircle() {
        return businessCircle;
    }

    public void setBusinessCircle(String businessCircle) {
        this.businessCircle = businessCircle;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public long getCertificateCount() {
        return certificateCount;
    }

    public void setCertificateCount(long certificateCount) {
        this.certificateCount = certificateCount;
    }

    @Override
    public String toString() {
        return "ActiveUser{" +
                "hq='" + hq + '\'' +
                ", appleStore='" + appleStore + '\'' +
                ", subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", expDate='" + expDate + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", devicePlatformName='" + devicePlatformName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", deviceIdentity='" + deviceIdentity + '\'' +
                ", ha='" + ha + '\'' +
                ", googleStore='" + googleStore + '\'' +
                ", session='" + session + '\'' +
                ", email='" + email + '\'' +
                ", studentid='" + studentid + '\'' +
                ", studentKey='" + studentKey + '\'' +
                ", games=" + games +
                ", levelCompPercentage=" + levelCompPercentage +
                ", level=" + level +
                ", levelPercentage='" + levelPercentage + '\'' +
                ", wins=" + wins +
                ", topic='" + topic + '\'' +
                ", place='" + place + '\'' +
                ", availableOCCount='" + availableOCCount + '\'' +
                ", cont_win_count='" + cont_win_count + '\'' +
                ", loss_count='" + loss_count + '\'' +
                ", newAlertCount='" + newAlertCount + '\'' +
                ", pendingOCCount='" + pendingOCCount + '\'' +
                ", tie_count='" + tie_count + '\'' +
                ", xp='" + xp + '\'' +
                ", uid='" + uid + '\'' +
                ", newAlert='" + newAlert + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", newChallenge='" + newChallenge + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleId='" + moduleId + '\'' +
                ", LoginType='" + LoginType + '\'' +
                ", dob=" + dob +
                ", schoolName='" + schoolName + '\'' +
                ", userGrade='" + userGrade + '\'' +
                ", userAge=" + userAge +
                ", userGender='" + userGender + '\'' +
                ", userCity='" + userCity + '\'' +
                ", userCountry='" + userCountry + '\'' +
                ", userMobile=" + userMobile +
                ", fname='" + fname + '\'' +
                ", lName='" + lName + '\'' +
                ", goal='" + goal + '\'' +
                ", department='" + department + '\'' +
                ", businessCircle='" + businessCircle + '\'' +
                ", batch='" + batch + '\'' +
                '}';
    }

    public boolean isDomainNameAuthentication() {
        return domainNameAuthentication;
    }

    public void setDomainNameAuthentication(boolean domainNameAuthentication) {
        this.domainNameAuthentication = domainNameAuthentication;
    }

    public HashMap<Long, BadgeModel> getBadges() {
        return badges;
    }

    public void setBadges(HashMap<Long, BadgeModel> badges) {
        this.badges = badges;
    }

    public HashMap<Long, NotificationResponse> getNotification() {
        return notification;
    }

    public void setNotification(HashMap<Long, NotificationResponse> notification) {
        this.notification = notification;
    }

    public long getRegisterDateTime() {
        return registerDateTime;
    }

    public void setRegisterDateTime(long registerDateTime) {
        this.registerDateTime = registerDateTime;
    }
}
