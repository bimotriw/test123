package com.oustme.oustsdk.tools;

/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class ApiKeyClass {
    private String encryptedKey;
    private String apiKey;
    private long algorithmNo;

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public long getAlgorithmNo() {
        return algorithmNo;
    }

    public void setAlgorithmNo(long algorithmNo) {
        this.algorithmNo = algorithmNo;
    }
}
