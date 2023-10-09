package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class OustPopupButton {
    private String btnText;
    private String btnActionURI;
    private String btnActionHttpMethod;

    public String btnActionRequest;


    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnActionURI() {
        return btnActionURI;
    }

    public void setBtnActionURI(String btnActionURI) {
        this.btnActionURI = btnActionURI;
    }

    public String getBtnActionHttpMethod() {
        return btnActionHttpMethod;
    }

    public void setBtnActionHttpMethod(String btnActionHttpMethod) {
        this.btnActionHttpMethod = btnActionHttpMethod;
    }

    public String getBtnActionRequest() {
        return btnActionRequest;
    }

    public void setBtnActionRequest(String btnActionRequest) {
        this.btnActionRequest = btnActionRequest;
    }

    @Override
    public String toString() {
        return "OustPopupButton{" +
                "btnText='" + btnText + '\'' +
                ", btnActionURI='" + btnActionURI + '\'' +
                ", btnActionHttpMethod='" + btnActionHttpMethod + '\'' +
                ", btnActionRequest=" + btnActionRequest +
                '}';
    }
}
