package com.oustme.oustsdk.response.learning_page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingCpl {
    @SerializedName("addedOn")
    @Expose
    private String addedOn;
    @SerializedName("elementId")
    @Expose
    private Integer elementId;
    @SerializedName("cplDescription")
    @Expose
    private String cplDescription;
    @SerializedName("cplName")
    @Expose
    private String cplName;
    @SerializedName("completionPercentage")
    @Expose
    private Integer completionPercentage;
    @SerializedName("updateTime")
    @Expose
    private String updateTime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("activeChildCpl")
    @Expose
    private Boolean activeChildCpl;
    @SerializedName("parentCplId")
    @Expose
    private Integer parentCplId;
    @SerializedName("enrolled")
    @Expose
    private Boolean enrolled;
    @SerializedName("banner")
    @Expose
    private String banner;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("type")
    @Expose
    private String type;

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getCplDescription() {
        return cplDescription;
    }

    public void setCplDescription(String cplDescription) {
        this.cplDescription = cplDescription;
    }

    public String getCplName() {
        return cplName;
    }

    public void setCplName(String cplName) {
        this.cplName = cplName;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActiveChildCpl() {
        return activeChildCpl;
    }

    public void setActiveChildCpl(Boolean activeChildCpl) {
        this.activeChildCpl = activeChildCpl;
    }

    public Integer getParentCplId() {
        return parentCplId;
    }

    public void setParentCplId(Integer parentCplId) {
        this.parentCplId = parentCplId;
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}