package com.oustme.oustsdk.firebase.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

@Keep
public class CourseCollectionFeatureInfo {
    private String name;
    private String description;
    private String icon;
    private String featureIconBgColor;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFeatureIconBgColor() {
        return featureIconBgColor;
    }

    public void setFeatureIconBgColor(String featureIconBgColor) {
        this.featureIconBgColor = featureIconBgColor;
    }
}
