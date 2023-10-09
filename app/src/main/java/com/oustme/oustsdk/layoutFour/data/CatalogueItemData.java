package com.oustme.oustsdk.layoutFour.data;

/**
 * Created by JacksonGenerator on 3/8/20.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogueItemData implements Parcelable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("banner")
    private String banner;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("description")
    private String description;
    @JsonProperty("contentId")
    private long contentId;
    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("trendingPoints")
    private int trendingPoints;
    @JsonProperty("numOfEnrolledUsers")
    private int numOfEnrolledUsers;
    @JsonProperty("oustCoins")
    private int oustCoins;
    @JsonProperty("viewStatus")
    private String viewStatus;
    @JsonProperty("categoryItemData")
    private List<CatalogueItemData> categoryItemData;
    @JsonProperty("catalogueId")
    long catalogueId;
    @JsonProperty("catalogueCategoryId")
    long catalogueCategoryId;
    @JsonProperty("numOfModules")
    long numOfModules;
    @JsonProperty("distributeTS")
    String distributeTS;
    @JsonProperty("completionDateAndTime")
    String completionDateAndTime;
    @JsonProperty("contentDuration")
    double contentDuration;
    @JsonProperty("completionPercentage")
    long completionPercentage;
    @JsonProperty("mode")
    String mode;
    @JsonProperty("assessmentScore")
    long assessmentScore;
    @JsonProperty("state")
    String state;
    @JsonProperty("enrolled")
    boolean enrolled;
    @JsonProperty("passed")
    boolean passed;
    @JsonProperty("showAssessmentResultScore")
    boolean showAssessmentResultScore;
    @JsonProperty("recurring")
    boolean recurring;


    protected CatalogueItemData(Parcel in) {
        trendingPoints = in.readInt();
        oustCoins = in.readInt();
        numOfEnrolledUsers = in.readInt();
        categoryItemData = in.createTypedArrayList(CatalogueItemData.CREATOR);
        name = in.readString();
        icon = in.readString();
        thumbnail = in.readString();
        contentId = in.readLong();
        banner = in.readString();
        description = in.readString();
        contentType = in.readString();
        viewStatus = in.readString();
    }

    public CatalogueItemData() {
    }

    public static final Creator<CatalogueItemData> CREATOR = new Creator<CatalogueItemData>() {
        @Override
        public CatalogueItemData createFromParcel(Parcel in) {
            return new CatalogueItemData(in);
        }

        @Override
        public CatalogueItemData[] newArray(int size) {
            return new CatalogueItemData[size];
        }
    };

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

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getTrendingPoints() {
        return trendingPoints;
    }

    public void setTrendingPoints(int trendingPoints) {
        this.trendingPoints = trendingPoints;
    }

    public int getNumOfEnrolledUsers() {
        return numOfEnrolledUsers;
    }

    public void setNumOfEnrolledUsers(int numOfEnrolledUsers) {
        this.numOfEnrolledUsers = numOfEnrolledUsers;
    }

    public int getOustCoins() {
        return oustCoins;
    }

    public void setOustCoins(int oustCoins) {
        this.oustCoins = oustCoins;
    }

    public String getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(String viewStatus) {
        this.viewStatus = viewStatus;
    }

    public List<CatalogueItemData> getCategoryItemData() {
        return categoryItemData;
    }

    public void setCategoryItemData(List<CatalogueItemData> categoryItemData) {
        this.categoryItemData = categoryItemData;
    }

    public long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public long getCatalogueCategoryId() {
        return catalogueCategoryId;
    }

    public void setCatalogueCategoryId(long catalogueCategoryId) {
        this.catalogueCategoryId = catalogueCategoryId;
    }

    public long getNumOfModules() {
        return numOfModules;
    }

    public void setNumOfModules(long numOfModules) {
        this.numOfModules = numOfModules;
    }

    public String getDistributeTS() {
        return distributeTS;
    }

    public void setDistributeTS(String distributeTS) {
        this.distributeTS = distributeTS;
    }

    public String getCompletionDateAndTime() {
        return completionDateAndTime;
    }

    public void setCompletionDateAndTime(String completionDateAndTime) {
        this.completionDateAndTime = completionDateAndTime;
    }

    public double getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(double contentDuration) {
        this.contentDuration = contentDuration;
    }

    public long getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(long completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(long assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isShowAssessmentResultScore() {
        return showAssessmentResultScore;
    }

    public void setShowAssessmentResultScore(boolean showAssessmentResultScore) {
        this.showAssessmentResultScore = showAssessmentResultScore;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(trendingPoints);
        dest.writeInt(oustCoins);
        dest.writeInt(numOfEnrolledUsers);
        dest.writeTypedList(categoryItemData);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(thumbnail);
        dest.writeLong(contentId);
        dest.writeString(banner);
        dest.writeString(description);
        dest.writeString(contentType);
        dest.writeString(viewStatus);
    }
}
