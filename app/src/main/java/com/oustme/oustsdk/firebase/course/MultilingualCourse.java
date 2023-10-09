package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class MultilingualCourse implements Serializable{
    private long courseId;
    private long langId;

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }
}
