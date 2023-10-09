package com.oustme.oustsdk.tools;

/**
 * Created by shilpysamaddar on 13/04/17.
 */

public class NotificationData {
   private String gcmMessage, collapseKey, title, gcmType,imgUrl,id;
   private String noticeBoardNotificationData;
   private int requestCode;

    public String getNoticeBoardNotificationData() {
        return noticeBoardNotificationData;
    }

    public void setNoticeBoardNotificationData(String noticeBoardNotificationData) {
        this.noticeBoardNotificationData = noticeBoardNotificationData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGcmMessage() {
        return gcmMessage;
    }

    public void setGcmMessage(String gcmMessage) {
        this.gcmMessage = gcmMessage;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGcmType() {
        return gcmType;
    }

    public void setGcmType(String gcmType) {
        this.gcmType = gcmType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
