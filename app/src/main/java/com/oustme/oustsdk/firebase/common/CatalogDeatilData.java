package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * Created by oust on 11/2/17.
 */

@Keep
public class CatalogDeatilData {
    private String title;
    private long id;
    private ArrayList<CommonLandingData> commonLandingDatas;
    private String description;
    private String icon;
    private String banner;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<CommonLandingData> getCommonLandingDatas() {
        return commonLandingDatas;
    }

    public void setCommonLandingDatas(ArrayList<CommonLandingData> commonLandingDatas) {
        this.commonLandingDatas = commonLandingDatas;
    }
}
