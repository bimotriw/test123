package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 31/08/17.
 */

@Keep
public class PingApiRequest {
    private String studentid;
    private boolean online;
    private DeviceInfoData deviceInfoData;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    private long pingTimestamp;
    private String fcmToken;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public DeviceInfoData getDeviceInfoData() {
        return deviceInfoData;
    }

    public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
        this.deviceInfoData = deviceInfoData;
    }

    public long getPingTimestamp() {
        return pingTimestamp;
    }

    public void setPingTimestamp(long pingTimestamp) {
        this.pingTimestamp = pingTimestamp;
    }
}
