package com.oustme.oustsdk.activity.common.languageSelector.model.response;

public class Language {
    public int languageId;
    public String name;
    public boolean defaultSelected;
    public int childCPLId;

    public Language(int languageId, String name, boolean defaultSelected, int childCPLId) {
        this.languageId = languageId;
        this.name = name;
        this.defaultSelected = defaultSelected;
        this.childCPLId = childCPLId;
    }

    public Language() {
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefaultSelected() {
        return defaultSelected;
    }

    public void setDefaultSelected(boolean defaultSelected) {
        this.defaultSelected = defaultSelected;
    }

    public int getChildCPLId() {
        return childCPLId;
    }

    public void setChildCPLId(int childCPLId) {
        this.childCPLId = childCPLId;
    }
}