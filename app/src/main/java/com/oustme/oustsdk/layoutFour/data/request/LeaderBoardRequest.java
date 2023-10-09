package com.oustme.oustsdk.layoutFour.data.request;

public class LeaderBoardRequest {
    
    String studentid;
    String tenantId;
    int cplId;
    int topCount;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

  /*  public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
*/
    public int getCplId() {
        return cplId;
    }

    public void setCplId(int cplId) {
        this.cplId = cplId;
    }

    public int getTopCount() {
        return topCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }
}
