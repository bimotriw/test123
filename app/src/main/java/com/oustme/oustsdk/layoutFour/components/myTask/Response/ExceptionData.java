package com.oustme.oustsdk.layoutFour.components.myTask.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExceptionData {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("oustErrorCode")
    @Expose
    private Integer oustErrorCode;
    @SerializedName("httpErrorCode")
    @Expose
    private Integer httpErrorCode;
    @SerializedName("httpStatus")
    @Expose
    private String httpStatus;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOustErrorCode() {
        return oustErrorCode;
    }

    public void setOustErrorCode(Integer oustErrorCode) {
        this.oustErrorCode = oustErrorCode;
    }

    public Integer getHttpErrorCode() {
        return httpErrorCode;
    }

    public void setHttpErrorCode(Integer httpErrorCode) {
        this.httpErrorCode = httpErrorCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }
}
