package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

@Keep
public class TrainingRequestModel {
    private String studentid;
    private String trainingRequestData;
    private String orgId;

    public TrainingRequestModel() {
    }

    public TrainingRequestModel(String studentid, String trainingRequestData) {
        this.studentid = studentid;
        this.trainingRequestData = trainingRequestData;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getTrainingRequestData() {
        return trainingRequestData;
    }

    public void setTrainingRequestData(String trainingRequestData) {
        this.trainingRequestData = trainingRequestData;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
