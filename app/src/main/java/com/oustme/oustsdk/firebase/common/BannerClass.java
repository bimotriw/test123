package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 09/03/17.
 */
@Keep
public class BannerClass {
    private Bannertype bannerType;
    private String bannerImg;
    private long assessmentId;
    private String contestId;
    private long courseId;
    private long weightage;
    private boolean hidden;
    private String link;

    public Bannertype getBannerType() {
        return bannerType;
    }

    public void setBannerType(Bannertype bannerType) {
        this.bannerType = bannerType;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getWeightage() {
        return weightage;
    }

    public void setWeightage(long weightage) {
        this.weightage = weightage;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

