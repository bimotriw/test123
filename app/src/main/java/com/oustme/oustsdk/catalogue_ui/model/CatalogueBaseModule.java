package com.oustme.oustsdk.catalogue_ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CatalogueBaseModule implements Serializable {

    String name;
    String banner;
    String icon;
    String description;
    String contentType;
    long contentId;
    String viewStatus;
    long categoryItemDataCount;
    ArrayList<CatalogueModule> categoryItemData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    public long getCategoryItemDataCount() {
        return categoryItemDataCount;
    }

    public void setCategoryItemDataCount(long categoryItemDataCount) {
        this.categoryItemDataCount = categoryItemDataCount;
    }

    public ArrayList<CatalogueModule> getCategoryItemData() {
        return categoryItemData;
    }

    public void setCategoryItemData(ArrayList<CatalogueModule> categoryItemData) {
        this.categoryItemData = categoryItemData;
    }
}
