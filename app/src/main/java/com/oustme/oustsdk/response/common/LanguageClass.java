package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class LanguageClass {
    private String name;
    private String fileName;
    private String languagePerfix;
    private String assetName;
    private int index;
    private long timeStamp;
    private String countryCode;

    private boolean isSelected;
    public boolean getSelected(){
        return isSelected;
    }
    public void setSelected(boolean selected){
        isSelected = selected;
    }
    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguagePerfix() {
        return languagePerfix;
    }

    public void setLanguagePerfix(String languagePerfix) {
        this.languagePerfix = languagePerfix;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
