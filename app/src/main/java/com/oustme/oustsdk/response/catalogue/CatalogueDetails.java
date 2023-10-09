package com.oustme.oustsdk.response.catalogue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatalogueDetails {
    @SerializedName("catalogueId")
    @Expose
    private long catalogueId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("addedOn")
    @Expose
    private String addedOn;
    @SerializedName("bannerImg")
    @Expose
    private String bannerImg;

    public long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }
}
