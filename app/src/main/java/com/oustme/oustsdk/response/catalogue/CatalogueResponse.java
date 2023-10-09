package com.oustme.oustsdk.response.catalogue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CatalogueResponse {
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
    @SerializedName("catalogueDetailsList")
    @Expose
    private ArrayList<CatalogueDetails> catalogueDetailsList;
    @SerializedName("totalCatalogues")
    @Expose
    private Integer totalCatalogues;
    @SerializedName("pageSize")
    @Expose
    private Integer pageSize;
    @SerializedName("numCatalogueInPage")
    @Expose
    private Integer numCatalogueInPage;

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

    public ArrayList<CatalogueDetails> getCatalogueDetailsList() {
        return catalogueDetailsList;
    }

    public void setCatalogueDetailsList(ArrayList<CatalogueDetails> catalogueDetailsList) {
        this.catalogueDetailsList = catalogueDetailsList;
    }

    public Integer getTotalCatalogues() {
        return totalCatalogues;
    }

    public void setTotalCatalogues(Integer totalCatalogues) {
        this.totalCatalogues = totalCatalogues;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getNumCatalogueInPage() {
        return numCatalogueInPage;
    }

    public void setNumCatalogueInPage(Integer numCatalogueInPage) {
        this.numCatalogueInPage = numCatalogueInPage;
    }
}
