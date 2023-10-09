package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.course.FavCardDetails;

import java.util.List;

@Keep
public class FavouriteCardsCourseData {
    private String courseName;
    private String courseId;
    private List<FavCardDetails> favCardDetailsList;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<FavCardDetails> getFavCardDetailsList() {
        return favCardDetailsList;
    }

    public void setFavCardDetailsList(List<FavCardDetails> favCardDetailsList) {
        this.favCardDetailsList = favCardDetailsList;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
