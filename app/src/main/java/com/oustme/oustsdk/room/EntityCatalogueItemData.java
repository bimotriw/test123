package com.oustme.oustsdk.room;


/**
 * Created by JacksonGenerator on 3/8/20.
 */

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class EntityCatalogueItemData {
    @JsonProperty("trendingPoints")
    private int trendingPoints;
    @JsonProperty("oustCoins")
    private int oustCoins;
    @JsonProperty("numOfEnrolledUsers")
    private int numOfEnrolledUsers;
    @JsonProperty("categoryItemData")
    @TypeConverters(TCCatalogueItemData.class)
    private List<EntityCatalogueItemData> categoryItemData;
    @JsonProperty("name")
    private String name;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("contentId")
    @PrimaryKey(autoGenerate = true)
    private long contentId;
    @JsonProperty("parentId")
    private long parentId;
    @JsonProperty("banner")
    private String banner;
    @JsonProperty("description")
    private String description;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("viewStatus")
    private String viewStatus;

    public int getTrendingPoints() {
        return trendingPoints;
    }

    public void setTrendingPoints(int trendingPoints) {
        this.trendingPoints = trendingPoints;
    }

    public int getOustCoins() {
        return oustCoins;
    }

    public void setOustCoins(int oustCoins) {
        this.oustCoins = oustCoins;
    }

    public int getNumOfEnrolledUsers() {
        return numOfEnrolledUsers;
    }

    public void setNumOfEnrolledUsers(int numOfEnrolledUsers) {
        this.numOfEnrolledUsers = numOfEnrolledUsers;
    }

    public List<EntityCatalogueItemData> getCategoryItemData() {
        return categoryItemData;
    }

    public void setCategoryItemData(List<EntityCatalogueItemData> categoryItemData) {
        this.categoryItemData = categoryItemData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
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

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }
}