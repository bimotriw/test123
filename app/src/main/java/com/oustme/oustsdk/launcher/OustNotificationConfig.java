package com.oustme.oustsdk.launcher;

/**
 * Created by shilpysamaddar on 14/04/17.
 */

public class OustNotificationConfig {

    private String serverKey;
    private String token;
    private PushNotificationType pushNotificationType;

    public String getToken() {
        return token;
    }

    public void setToken(String toke) {
        this.token = toke;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public PushNotificationType getPushNotificationType() {
        return pushNotificationType;
    }

    public void setPushNotificationType(PushNotificationType pushNotificationType) {
        this.pushNotificationType = pushNotificationType;
    }

}
