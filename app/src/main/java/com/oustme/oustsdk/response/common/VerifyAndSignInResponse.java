package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class VerifyAndSignInResponse extends CommonResponse{

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

    private String authType;
    private boolean validTenant;
    private String samlSSOURL;
    private String s3_base_end;
    private String http_img_bucket_cdn;
    private String img_bucket_cdn;
    private String img_bucket_name;
    private String s3_Bucket_Region;
    private String webappUrl;
    private String webAppLink;
    private boolean domainNameAuthentication;
    private String awsS3KeyId;
    private String awsS3KeySecret;

    private String tenantLogo;

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

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public boolean isValidTenant() {
        return validTenant;
    }

    public void setValidTenant(boolean validTenant) {
        this.validTenant = validTenant;
    }

    public String getSamlSSOURL() {
        return samlSSOURL;
    }

    public void setSamlSSOURL(String samlSSOURL) {
        this.samlSSOURL = samlSSOURL;
    }

    public String getS3_base_end() {
        return s3_base_end;
    }

    public void setS3_base_end(String s3_base_end) {
        this.s3_base_end = s3_base_end;
    }

    public String getHttp_img_bucket_cdn() {
        return http_img_bucket_cdn;
    }

    public void setHttp_img_bucket_cdn(String http_img_bucket_cdn) {
        this.http_img_bucket_cdn = http_img_bucket_cdn;
    }

    public String getImg_bucket_cdn() {
        return img_bucket_cdn;
    }

    public void setImg_bucket_cdn(String img_bucket_cdn) {
        this.img_bucket_cdn = img_bucket_cdn;
    }

    public String getImg_bucket_name() {
        return img_bucket_name;
    }

    public void setImg_bucket_name(String img_bucket_name) {
        this.img_bucket_name = img_bucket_name;
    }

    public String getS3_Bucket_Region() {
        return s3_Bucket_Region;
    }

    public void setS3_Bucket_Region(String s3_Bucket_Region) {
        this.s3_Bucket_Region = s3_Bucket_Region;
    }

    public String getWebappUrl() {
        return webappUrl;
    }

    public void setWebappUrl(String webappUrl) {
        this.webappUrl = webappUrl;
    }

    public String getWebAppLink() {
        return webAppLink;
    }

    public void setWebAppLink(String webAppLink) {
        this.webAppLink = webAppLink;
    }

    public boolean isDomainNameAuthentication() {
        return domainNameAuthentication;
    }

    public void setDomainNameAuthentication(boolean domainNameAuthentication) {
        this.domainNameAuthentication = domainNameAuthentication;
    }

    public String getAwsS3KeyId() {
        return awsS3KeyId;
    }

    public void setAwsS3KeyId(String awsS3KeyId) {
        this.awsS3KeyId = awsS3KeyId;
    }

    public String getAwsS3KeySecret() {
        return awsS3KeySecret;
    }

    public void setAwsS3KeySecret(String awsS3KeySecret) {
        this.awsS3KeySecret = awsS3KeySecret;
    }

    public String getTenantLogo() {
        return tenantLogo;
    }

    public void setTenantLogo(String tenantLogo) {
        this.tenantLogo = tenantLogo;
    }
}
