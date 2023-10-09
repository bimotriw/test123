package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

@Keep
public class ExceptionData {

    String message;
    int oustErrorCode;
    int httpErrorCode;
    String httpStatus;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOustErrorCode() {
        return oustErrorCode;
    }

    public void setOustErrorCode(int oustErrorCode) {
        this.oustErrorCode = oustErrorCode;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    public void setHttpErrorCode(int httpErrorCode) {
        this.httpErrorCode = httpErrorCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }
}