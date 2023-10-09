package com.oustme.oustsdk.model.response.common;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildCplIdListModel {
    @SerializedName("bannerImg")
    @Expose
    private String bannerImg;
    @SerializedName("bgImg")
    @Expose
    private String bgImg;
    @SerializedName("childCplId")
    @Expose
    private Long childCplId;
    @SerializedName("childCplName")
    @Expose
    private String childCplName;
    @SerializedName("langId")
    @Expose
    private Long langId;
    @SerializedName("cplFound")
    @Expose
    private boolean cplFound;

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public Long getChildCplId() {
        return childCplId;
    }

    public void setChildCplId(Long childCplId) {
        this.childCplId = childCplId;
    }

    public String getChildCplName() {
        return childCplName;
    }

    public void setChildCplName(String childCplName) {
        this.childCplName = childCplName;
    }

    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long langId) {
        this.langId = langId;
    }

    public boolean getCplFound() {
        return cplFound;
    }

    public void setCplFound(boolean cplFound) {
        this.cplFound = cplFound;
    }
}