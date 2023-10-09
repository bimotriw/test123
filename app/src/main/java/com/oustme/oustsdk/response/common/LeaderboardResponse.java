package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class LeaderboardResponse {
    private String error;

    private List<Month> month;

    private String data;

    private String userDisplayName;

    private String group;

    private Boolean success;

    private float eventProgress;

    private List<All> all;

    private List<Week> week;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Month> getMonth() {
        return month;
    }

    public void setMonth(List<Month> month) {
        this.month = month;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public float getEventProgress() {
        return eventProgress;
    }

    public void setEventProgress(float eventProgress) {
        this.eventProgress = eventProgress;
    }

    public List<All> getAll() {
        return all;
    }

    public void setAll(List<All> all) {
        this.all = all;
    }

    public List<Week> getWeek() {
        return week;
    }

    public void setWeek(List<Week> week) {
        this.week = week;
    }
}
