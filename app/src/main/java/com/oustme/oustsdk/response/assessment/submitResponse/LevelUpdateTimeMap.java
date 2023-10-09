package com.oustme.oustsdk.response.assessment.submitResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LevelUpdateTimeMap {
    @SerializedName("levelId")
    @Expose
    private Integer levelId;
    @SerializedName("courseLevelUpdateTS")
    @Expose
    private Long courseLevelUpdateTS;

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Long getCourseLevelUpdateTS() {
        return courseLevelUpdateTS;
    }

    public void setCourseLevelUpdateTS(Long courseLevelUpdateTS) {
        this.courseLevelUpdateTS = courseLevelUpdateTS;
    }
}
