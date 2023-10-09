package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 24/07/17.
 */

@Keep
public class ReportAProblemRequest {
    private String problemScreen;
    private String problemDesc;
    private List<String> screenShots;

    public String getProblemScreen() {
        return problemScreen;
    }

    public void setProblemScreen(String problemScreen) {
        this.problemScreen = problemScreen;
    }

    public String getProblemDesc() {
        return problemDesc;
    }

    public void setProblemDesc(String problemDesc) {
        this.problemDesc = problemDesc;
    }

    public List<String> getScreenShots() {
        return screenShots;
    }

    public void setScreenShots(List<String> screenShots) {
        this.screenShots = screenShots;
    }
}
