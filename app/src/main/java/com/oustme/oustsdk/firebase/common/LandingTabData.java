package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

/**
 * Created by admin on 16/10/17.
 */

@Keep
public class LandingTabData {
    private String name;
    private String icon;
    private String type;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
