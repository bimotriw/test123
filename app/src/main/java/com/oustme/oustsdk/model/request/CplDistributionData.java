package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class CplDistributionData {

    private List<String> users;
    private boolean sendSMS;
    private boolean sendEmail;
    private boolean sendNotification;
    private boolean onlyPANIndia;
    private String distributeDateTime;
    boolean reusabilityAllowed;

    public boolean isReusabilityAllowed() {
        return reusabilityAllowed;
    }

    public void setReusabilityAllowed(boolean reusabilityAllowed) {
        this.reusabilityAllowed = reusabilityAllowed;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public boolean isSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public boolean isOnlyPANIndia() {
        return onlyPANIndia;
    }

    public void setOnlyPANIndia(boolean onlyPANIndia) {
        this.onlyPANIndia = onlyPANIndia;
    }

    public String getDistributeDateTime() {
        return distributeDateTime;
    }

    public void setDistributeDateTime(String distributeDateTime) {
        this.distributeDateTime = distributeDateTime;
    }
}
