package com.oustme.oustsdk.catalogue_ui.model;

import com.oustme.oustsdk.tools.ActiveUser;

import java.util.ArrayList;
import java.util.HashMap;

public class CatalogueComponentModule {

    String catalogueCategoryName;
    String banner;
    String icon;
    String thumbnail;
    String description;
    boolean isSkillEnabled;
    ActiveUser activeUser;
    HashMap<String, ArrayList<CatalogueModule>> catalogueBaseHashMap;


    public String getCatalogueCategoryName() {
        return catalogueCategoryName;
    }

    public void setCatalogueCategoryName(String catalogueCategoryName) {
        this.catalogueCategoryName = catalogueCategoryName;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSkillEnabled() {
        return isSkillEnabled;
    }

    public void setSkillEnabled(boolean skillEnabled) {
        isSkillEnabled = skillEnabled;
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public HashMap<String, ArrayList<CatalogueModule>> getCatalogueBaseHashMap() {
        return catalogueBaseHashMap;
    }

    public void setCatalogueBaseHashMap(HashMap<String, ArrayList<CatalogueModule>> catalogueBaseHashMap) {
        this.catalogueBaseHashMap = catalogueBaseHashMap;
    }
}
