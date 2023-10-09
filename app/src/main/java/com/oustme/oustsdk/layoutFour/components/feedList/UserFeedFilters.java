package com.oustme.oustsdk.layoutFour.components.feedList;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UserFeedFilters implements Parcelable {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("exceptionData")
    @Expose
    private Object exceptionData;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("userDisplayName")
    @Expose
    private Object userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private int errorCode;
    @SerializedName("popup")
    @Expose
    private Object popup;
    @SerializedName("feedFilterList")
    @Expose
    private ArrayList<FeedFilter> feedFilterList;

    public UserFeedFilters() {

    }

    protected UserFeedFilters(Parcel in) {
        success = in.readByte() != 0;
        errorCode = in.readInt();
        feedFilterList = in.createTypedArrayList(FeedFilter.CREATOR);
    }

    public static final Creator<UserFeedFilters> CREATOR = new Creator<UserFeedFilters>() {
        @Override
        public UserFeedFilters createFromParcel(Parcel in) {
            return new UserFeedFilters(in);
        }

        @Override
        public UserFeedFilters[] newArray(int size) {
            return new UserFeedFilters[size];
        }
    };

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(Object exceptionData) {
        this.exceptionData = exceptionData;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(Object userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object getPopup() {
        return popup;
    }

    public void setPopup(Object popup) {
        this.popup = popup;
    }

    public ArrayList<FeedFilter> getFeedFilterList() {
        return feedFilterList;
    }

    public void setFeedFilterList(ArrayList<FeedFilter> feedFilterList) {
        this.feedFilterList = feedFilterList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeInt(errorCode);
        dest.writeTypedList(feedFilterList);
    }

    public static class FeedFilter implements Parcelable {

        @SerializedName("label")
        @Expose
        public String categoryName;
        @SerializedName("type")
        @Expose
        public int categoryType;
        private boolean isSelected = false;

        public FeedFilter() {

        }

        protected FeedFilter(Parcel in) {
            categoryName = in.readString();
            categoryType = in.readInt();
            isSelected = in.readByte() != 0;
        }

        public static final Creator<FeedFilter> CREATOR = new Creator<FeedFilter>() {
            @Override
            public FeedFilter createFromParcel(Parcel in) {
                return new FeedFilter(in);
            }

            @Override
            public FeedFilter[] newArray(int size) {
                return new FeedFilter[size];
            }
        };

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public int getCategoryType() {
            return categoryType;
        }

        public void setCategoryType(int categoryType) {
            this.categoryType = categoryType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(categoryName);
            dest.writeInt(categoryType);
            dest.writeByte((byte) (isSelected ? 1 : 0));
        }
    }

}