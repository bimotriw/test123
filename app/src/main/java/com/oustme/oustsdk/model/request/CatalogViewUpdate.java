package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

@Keep
public class CatalogViewUpdate {
    private long catalogId;
    private long categoryId;
    private long contentId;
    private String contentType;
    private String studentid;

    public CatalogViewUpdate() {
    }

    public CatalogViewUpdate(long catalogId, long categoryId, long contentId, String contentType, String studentid) {
        this.catalogId = catalogId;
        this.categoryId = categoryId;
        this.contentId = contentId;
        this.contentType = contentType;
        this.studentid = studentid;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(long catalogId) {
        this.catalogId = catalogId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}
