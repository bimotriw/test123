package com.oustme.oustsdk.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class CertificateData {
    @SerializedName("shortUrl")
    @Expose
    private Object shortUrl;
    @SerializedName("longUrl")
    @Expose
    private Object longUrl;
    @SerializedName("keyName")
    @Expose
    private Object keyName;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("certificateDate")
    @Expose
    private Long certificateDate;
    @SerializedName("certificateUrl")
    @Expose
    private String certificateUrl;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("certificateThumbnail")
    @Expose
    private String certificateThumbnail;
    @SerializedName("certificateFile")
    @Expose
    private Object certificateFile;
    @SerializedName("certificateDescription")
    @Expose
    private Object certificateDescription;

    public Object getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(Object shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Object getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(Object longUrl) {
        this.longUrl = longUrl;
    }

    public Object getKeyName() {
        return keyName;
    }

    public void setKeyName(Object keyName) {
        this.keyName = keyName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Long getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(Long certificateDate) {
        this.certificateDate = certificateDate;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCertificateThumbnail() {
        return certificateThumbnail;
    }

    public void setCertificateThumbnail(String certificateThumbnail) {
        this.certificateThumbnail = certificateThumbnail;
    }

    public Object getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(Object certificateFile) {
        this.certificateFile = certificateFile;
    }

    public Object getCertificateDescription() {
        return certificateDescription;
    }

    public void setCertificateDescription(Object certificateDescription) {
        this.certificateDescription = certificateDescription;
    }

    public static Comparator<CertificateData> sortByDate = (s2, s1) -> {
        if (s1.getCertificateDate() == null) {
            return -1;
        }
        if (s2.getCertificateDate() == null) {
            return 1;
        }
        if (s1.getCertificateDate().equals(s2.getCertificateDate())) {
            return 0;
        }
        return s1.getCertificateDate().compareTo(s2.getCertificateDate());
    };
}
