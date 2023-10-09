package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by oust on 10/30/17.
 */

@Keep
public class CatalogItemData {
    private int id;
    private String banner;
    private String name;
    private String description;
    private String icon;
    private String thumbnail;
    private String viewStatus;
    private long catalogId;
    private long catalogContentId;
    private long catalogCategoryId;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

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

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(long catalogId) {
        this.catalogId = catalogId;
    }

    public long getCatalogContentId() {
        return catalogContentId;
    }

    public void setCatalogContentId(long catalogContentId) {
        this.catalogContentId = catalogContentId;
    }

    public long getCatalogCategoryId() {
        return catalogCategoryId;
    }

    public void setCatalogCategoryId(long catalogCategoryId) {
        this.catalogCategoryId = catalogCategoryId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
