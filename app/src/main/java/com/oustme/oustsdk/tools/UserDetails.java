package com.oustme.oustsdk.tools;

import com.google.gson.annotations.SerializedName;


public class UserDetails {
    @SerializedName("success")
    private boolean success;
    @SerializedName("exceptionData")
    private String exceptionData;
    @SerializedName("error")
    private String error;
    @SerializedName("userDisplayName")
    private String userDisplayName;
    @SerializedName("errorCode")
    private int errorCode;
    @SerializedName("popup")
    private String popup;
    private UserData userData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public class UserData {
        private String country;

        private String appVersion;

        private String openGamesCount;

        private boolean userStatus;

        private String gender;

        private String enableWeeklyReportByUserInApp;

        private String city;

        private String emailId;

        private String loss_count;

        private String lname;

        private String oc;

        private String devicePlatformName;

        private String tie_count;

        private String lbAddInfo;

        private String inActive;

        private String referralCode;

        private String games;

        private String lpCompPercentage;

        private String alias;

        private String state;

        private String schoolName;

        private String key;

        private String wins;

        private String fname;

        private String goal;

        private String level;

        private String userGrade;

        private String mobile;

        private String userDisplayName;

        private String avatar;

        private String userId;

        private String dob;

        private String grade;

        private String xp;

        private String cont_win_count;

        private String userRole;

        private String region;

        private long age;

        private String mcFirebase;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getOpenGamesCount() {
            return openGamesCount;
        }

        public void setOpenGamesCount(String openGamesCount) {
            this.openGamesCount = openGamesCount;
        }

        public boolean isUserStatus() {
            return userStatus;
        }

        public void setUserStatus(boolean userStatus) {
            this.userStatus = userStatus;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEnableWeeklyReportByUserInApp() {
            return enableWeeklyReportByUserInApp;
        }

        public void setEnableWeeklyReportByUserInApp(String enableWeeklyReportByUserInApp) {
            this.enableWeeklyReportByUserInApp = enableWeeklyReportByUserInApp;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getLoss_count() {
            return loss_count;
        }

        public void setLoss_count(String loss_count) {
            this.loss_count = loss_count;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getOc() {
            return oc;
        }

        public void setOc(String oc) {
            this.oc = oc;
        }

        public String getDevicePlatformName() {
            return devicePlatformName;
        }

        public void setDevicePlatformName(String devicePlatformName) {
            this.devicePlatformName = devicePlatformName;
        }

        public String getTie_count() {
            return tie_count;
        }

        public void setTie_count(String tie_count) {
            this.tie_count = tie_count;
        }

        public String getLbAddInfo() {
            return lbAddInfo;
        }

        public void setLbAddInfo(String lbAddInfo) {
            this.lbAddInfo = lbAddInfo;
        }

        public String getInActive() {
            return inActive;
        }

        public void setInActive(String inActive) {
            this.inActive = inActive;
        }

        public String getReferralCode() {
            return referralCode;
        }

        public void setReferralCode(String referralCode) {
            this.referralCode = referralCode;
        }

        public String getGames() {
            return games;
        }

        public void setGames(String games) {
            this.games = games;
        }

        public String getLpCompPercentage() {
            return lpCompPercentage;
        }

        public void setLpCompPercentage(String lpCompPercentage) {
            this.lpCompPercentage = lpCompPercentage;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getWins() {
            return wins;
        }

        public void setWins(String wins) {
            this.wins = wins;
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

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getUserGrade() {
            return userGrade;
        }

        public void setUserGrade(String userGrade) {
            this.userGrade = userGrade;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUserDisplayName() {
            return userDisplayName;
        }

        public void setUserDisplayName(String userDisplayName) {
            this.userDisplayName = userDisplayName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getXp() {
            return xp;
        }

        public void setXp(String xp) {
            this.xp = xp;
        }

        public String getCont_win_count() {
            return cont_win_count;
        }

        public void setCont_win_count(String cont_win_count) {
            this.cont_win_count = cont_win_count;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public long getAge() {
            return age;
        }

        public void setAge(long age) {
            this.age = age;
        }

        public String getMcFirebase() {
            return mcFirebase;
        }

        public void setMcFirebase(String mcFirebase) {
            this.mcFirebase = mcFirebase;
        }
    }
}


