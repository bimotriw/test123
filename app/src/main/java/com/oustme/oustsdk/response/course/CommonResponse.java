package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.ExceptionData;
import com.oustme.oustsdk.response.common.Popup;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class CommonResponse {
    private String error;
    private boolean success;
    private String userDisplayName;
    private Popup popup;
    private ExceptionData exceptionData;

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public Popup getPopup() {
        return popup;
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ExceptionData getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(ExceptionData exceptionData) {
        this.exceptionData = exceptionData;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "error='" + error + '\'' +
                ", success=" + success +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", popup=" + popup +
                '}';
    }
}
