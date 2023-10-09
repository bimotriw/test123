package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class LearningPathModule {
    private String name;
    private String description;
    private String grade;
    private long numofLevels;
    private long numOfModule;
    private long numofQues;
    private long proficiency;
    private String lpCompletePercentage;
    private String lpId;
    private String bgImage;
    private boolean joinedCourse;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNumofLevels() {
        return numofLevels;
    }

    public void setNumofLevels(long numofLevels) {
        this.numofLevels = numofLevels;
    }

    public long getNumOfModule() {
        return numOfModule;
    }

    public void setNumOfModule(long numOfModule) {
        this.numOfModule = numOfModule;
    }

    public long getNumofQues() {
        return numofQues;
    }

    public void setNumofQues(long numofQues) {
        this.numofQues = numofQues;
    }

    public String getLpCompletePercentage() {
        return lpCompletePercentage;
    }

    public void setLpCompletePercentage(String lpCompletePercentage) {
        this.lpCompletePercentage = lpCompletePercentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLpId() {
        return lpId;
    }

    public void setLpId(String lpId) {
        this.lpId = lpId;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public long getProficiency() {
        return proficiency;
    }

    public void setProficiency(long proficiency) {
        this.proficiency = proficiency;
    }

    public boolean isJoinedCourse() {
        return joinedCourse;
    }

    public void setJoinedCourse(boolean joinedCourse) {
        this.joinedCourse = joinedCourse;
    }
}
