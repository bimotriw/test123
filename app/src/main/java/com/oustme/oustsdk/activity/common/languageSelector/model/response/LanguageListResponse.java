package com.oustme.oustsdk.activity.common.languageSelector.model.response;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class LanguageListResponse implements Serializable {
    private String exceptionData;
    private String error;
    private boolean success;
    private String userDisplayName;
    private int errorCode;
    private String popup;
    private List<Language> languageAndChildCPLList;

    public LanguageListResponse() {

    }

    public LanguageListResponse(String exceptionData, String error, boolean success, String userDisplayName, int errorCode, String popup, List<Language> languageAndChildCPLList) {
        this.exceptionData = exceptionData;
        this.error = error;
        this.success = success;
        this.userDisplayName = userDisplayName;
        this.errorCode = errorCode;
        this.popup = popup;
        this.languageAndChildCPLList = languageAndChildCPLList;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public List<Language> getLanguageList() {
        return languageAndChildCPLList;
    }

    public void setLanguageList(List<Language> languageAndChildCPLList) {
        this.languageAndChildCPLList = languageAndChildCPLList;
    }
}
