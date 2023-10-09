package com.oustme.oustsdk.activity.common.languageSelector.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CplDistributionRequest {

    @SerializedName("tenantId")
    @Expose
    private String tenantId;
    @SerializedName("languageId")
    @Expose
    private Integer languageId;
    @SerializedName("cplId")
    @Expose
    private Integer cplId;
    @SerializedName("orgId")
    @Expose
    private String orgId;
    @SerializedName("studentid")
    @Expose
    private String studentid;

    boolean reusabilityAllowed;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getCplId() {
        return cplId;
    }

    public void setCplId(Integer cplId) {
        this.cplId = cplId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public boolean isReusabilityAllowed() {
        return reusabilityAllowed;
    }

    public void setReusabilityAllowed(boolean reusabilityAllowed) {
        this.reusabilityAllowed = reusabilityAllowed;
    }
}
