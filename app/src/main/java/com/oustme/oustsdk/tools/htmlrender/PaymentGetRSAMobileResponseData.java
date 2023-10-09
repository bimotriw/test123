package com.oustme.oustsdk.tools.htmlrender;

import com.oustme.oustsdk.response.course.CommonResponse;

import java.util.Map;

/**
 * Created by admin on 09/06/17.
 */

public class PaymentGetRSAMobileResponseData extends CommonResponse {
    private String rsaKey;
    private String redirectUrl;
    private String cancelUrl;
    private Map<String,String> params;
    private String transactionUrl;

    public String getRsaKey() {
        return rsaKey;
    }

    public void setRsaKey(String rsaKey) {
        this.rsaKey = rsaKey;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getTransactionUrl() {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl) {
        this.transactionUrl = transactionUrl;
    }
}
