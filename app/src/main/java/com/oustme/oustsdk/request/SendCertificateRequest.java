package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 07/04/17.
 */

@Keep
public class SendCertificateRequest {
    private String contentId;
    private String studentid;
    private String emailid;
    private String contentType;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
