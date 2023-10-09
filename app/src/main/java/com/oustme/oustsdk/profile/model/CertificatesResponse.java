package com.oustme.oustsdk.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CertificatesResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("exceptionData")
    @Expose
    private String exceptionData;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("userDisplayName")
    @Expose
    private String userDisplayName;
    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("popup")
    @Expose
    private String popup;
    @SerializedName("certificateCount")
    @Expose
    private Integer certificateCount;
    @SerializedName("certificateData")
    @Expose
    private ArrayList<CertificateData> certificateData = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public Integer getCertificateCount() {
        return certificateCount;
    }

    public void setCertificateCount(Integer certificateCount) {
        this.certificateCount = certificateCount;
    }

    public ArrayList<CertificateData> getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(ArrayList<CertificateData> certificateData) {
        this.certificateData = certificateData;
    }
}
