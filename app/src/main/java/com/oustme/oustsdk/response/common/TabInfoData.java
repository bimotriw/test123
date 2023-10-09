package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by oust on 10/30/17.
 */

@Keep
public class TabInfoData {
    String label,type,image;
    String indexName;
    long categoryId;
    long catalogueTabId;
    String catalogueType;
    String tabTags;
    private boolean hideCatalogue;
    private boolean showTodo;

    public String getTabTags() {
        return tabTags;
    }

    public void setTabTags(String tabTags) {
        this.tabTags = tabTags;
    }

    public String getCatalogueType() {
        return catalogueType;
    }

    public void setCatalogueType(String catalogueType) {
        this.catalogueType = catalogueType;
    }

    public long getCatalogueTabId() {
        return catalogueTabId;
    }

    public void setCatalogueTabId(long catalogueTabId) {
        this.catalogueTabId = catalogueTabId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public boolean isHideCatalogue() {
        return hideCatalogue;
    }

    public void setHideCatalogue(boolean hideCatalogue) {
        this.hideCatalogue = hideCatalogue;
    }

    public boolean isShowTodo() {
        return showTodo;
    }

    public void setShowTodo(boolean showTodo) {
        this.showTodo = showTodo;
    }
}
