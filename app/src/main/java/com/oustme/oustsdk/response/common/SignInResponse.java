

package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class SignInResponse  extends CommonResponse{
    private String hq;

    private String appleStore;


    private String apiVersionMismatch;

    private String expDate;


    private String devicePlatformName;

    private String avatar;

    private String deviceIdentity;

    private String ha;

    private String googleStore;

    private String session;

    private String email;


    private String studentid;

    private String firebaseToken;

    private String studentKey;

    private String loginType;

    private String apiServerEndpoint;

    private String firebaseEndpoint;

    private boolean logoutButtonEnabled;

    private boolean showGoalSetting;

    private boolean playModeEnabled;

    private String rewardPageLink;

    private String tenantDisplayName;

    private String panelColor;

    private String role;
    private String firebaseAppId,firebaseAPIKey,firebaseClientId,firebaseStorageBucket;

    private String authToken;

    private String department;

    private String businessCircle;

    private String batch;

    private boolean domainNameAuthentication;

    public String getFirebaseAppId() {
        return firebaseAppId;
    }

    public void setFirebaseAppId(String firebaseAppId) {
        this.firebaseAppId = firebaseAppId;
    }

    public String getFirebaseAPIKey() {
        return firebaseAPIKey;
    }

    public void setFirebaseAPIKey(String firebaseAPIKey) {
        this.firebaseAPIKey = firebaseAPIKey;
    }

    public String getFirebaseClientId() {
        return firebaseClientId;
    }

    public void setFirebaseClientId(String firebaseClientId) {
        this.firebaseClientId = firebaseClientId;
    }

    public String getFirebaseStorageBucket() {
        return firebaseStorageBucket;
    }

    public void setFirebaseStorageBucket(String firebaseStorageBucket) {
        this.firebaseStorageBucket = firebaseStorageBucket;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String userRole) {
        this.role = userRole;
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


    public String getApiVersionMismatch() {
        return apiVersionMismatch;
    }

    public void setApiVersionMismatch(String apiVersionMismatch) {
        this.apiVersionMismatch = apiVersionMismatch;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
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

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getApiServerEndpoint() {
        return apiServerEndpoint;
    }

    public String getFirebaseEndpoint() {
        return firebaseEndpoint;
    }

    public void setFirebaseEndpoint(String firebaseEndpoint) {
        this.firebaseEndpoint = firebaseEndpoint;
    }

    public void setApiServerEndpoint(String apiServerEndpoint) {
        this.apiServerEndpoint = apiServerEndpoint;
    }

    public boolean isLogoutButtonEnabled() {
        return logoutButtonEnabled;
    }

    public void setLogoutButtonEnabled(boolean logoutButtonEnabled) {
        this.logoutButtonEnabled = logoutButtonEnabled;
    }

    public boolean isShowGoalSetting() {
        return showGoalSetting;
    }

    public void setShowGoalSetting(boolean showGoalSetting) {
        this.showGoalSetting = showGoalSetting;
    }

    public boolean isPlayModeEnabled() {
        return playModeEnabled;
    }

    public void setPlayModeEnabled(boolean playModeEnabled) {
        this.playModeEnabled = playModeEnabled;
    }

    public String getRewardPageLink() {
        return rewardPageLink;
    }

    public void setRewardPageLink(String rewardPageLink) {
        this.rewardPageLink = rewardPageLink;
    }

    public String getTenantDisplayName() {
        return tenantDisplayName;
    }

    public void setTenantDisplayName(String tenantDisplayName) {
        this.tenantDisplayName = tenantDisplayName;
    }

    public String getPanelColor() {
        return panelColor;
    }

    public void setPanelColor(String panelColor) {
        this.panelColor = panelColor;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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

    public boolean isDomainNameAuthentication() {
        return domainNameAuthentication;
    }

    public void setDomainNameAuthentication(boolean domainNameAuthentication) {
        this.domainNameAuthentication = domainNameAuthentication;
    }
}
