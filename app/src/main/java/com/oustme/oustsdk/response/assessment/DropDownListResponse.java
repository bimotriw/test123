package com.oustme.oustsdk.response.assessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DropDownListResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
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
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private Object popup;
    @SerializedName("dataSourceList")
    @Expose
    private List<String> dataSourceList = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
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

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Object getPopup() {
        return popup;
    }

    public void setPopup(Object popup) {
        this.popup = popup;
    }

    public List<String> getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(List<String> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }
}
