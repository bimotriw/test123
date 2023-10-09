package com.oustme.oustsdk.request;

public class CplRatingPopupRequest {

    private String cplId;
    private String rating;
    private String studentid;
    private String feedback;

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
