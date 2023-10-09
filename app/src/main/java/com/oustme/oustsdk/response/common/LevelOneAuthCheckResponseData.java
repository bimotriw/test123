package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.CommonResponse;

/**
 * Created by admin on 15/11/17.
 */

@Keep
public class LevelOneAuthCheckResponseData extends CommonResponse{

    private boolean trustedTenant;
    private boolean userExists;
    private String userId;
    private String firebaseToken;
    private String userKey;
    private String role;
    private String apiServerEndpoint;
    private String firebaseEndpoint;
    private String firebaseAppId,firebaseAPIKey,firebaseClientId,firebaseStorageBucket;

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

    public boolean isTrustedTenant() {
        return trustedTenant;
    }

    public void setTrustedTenant(boolean trustedTenant) {
        this.trustedTenant = trustedTenant;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getApiServerEndpoint() {
        return apiServerEndpoint;
    }

    public void setApiServerEndpoint(String apiServerEndpoint) {
        this.apiServerEndpoint = apiServerEndpoint;
    }

    public String getFirebaseEndpoint() {
        return firebaseEndpoint;
    }

    public void setFirebaseEndpoint(String firebaseEndpoint) {
        this.firebaseEndpoint = firebaseEndpoint;
    }
}
