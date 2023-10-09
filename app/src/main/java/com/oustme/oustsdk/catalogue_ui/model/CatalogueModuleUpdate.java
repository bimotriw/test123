package com.oustme.oustsdk.catalogue_ui.model;

import com.oustme.oustsdk.firebase.common.CommonLandingData;

import java.io.Serializable;

public class CatalogueModuleUpdate implements Serializable {

    int position;
    int parentPosition;
    boolean isUpdated;
    String type;
    CatalogueModule catalogueModule;
    CommonLandingData commonLandingData;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CatalogueModule getCatalogueModule() {
        return catalogueModule;
    }

    public void setCatalogueModule(CatalogueModule catalogueModule) {
        this.catalogueModule = catalogueModule;
    }

    public CommonLandingData getCommonLandingData() {
        return commonLandingData;
    }

    public void setCommonLandingData(CommonLandingData commonLandingData) {
        this.commonLandingData = commonLandingData;
    }
}
