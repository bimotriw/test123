package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 21/03/17.
 */

@Keep
public class FriendProfileResponceAssessmentRow {
    private int assessmentId;
    private String name;
    private String logo;
    private String banner;
    private String userCompletionTime;

    public String getUserCompletionTime() {
        return userCompletionTime;
    }

    public void setUserCompletionTime(String userCompletionTime) {
        this.userCompletionTime = userCompletionTime;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}
