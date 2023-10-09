package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import java.io.Serializable;

public class NewNbPost implements Serializable {

    private String nbPostRewardOC;

    private String updatedBy;

    private String description;

    private String postId;

    private String updatedOn;

    private String numOfComments;

    private String createdOn;

    private String numOfLikes;

    private String bannerImage;

    private String hasAttachment;

    private String createdBy;

    private String name;

    private String numOfShared;

    private String totalLikes;

    private AttachmentsData[] attachmentsData;

    private String nbId;

    public String getNbPostRewardOC() {
        return nbPostRewardOC;
    }

    public void setNbPostRewardOC(String nbPostRewardOC) {
        this.nbPostRewardOC = nbPostRewardOC;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(String numOfComments) {
        this.numOfComments = numOfComments;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(String numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(String hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfShared() {
        return numOfShared;
    }

    public void setNumOfShared(String numOfShared) {
        this.numOfShared = numOfShared;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public AttachmentsData[] getAttachmentsData() {
        return attachmentsData;
    }

    public void setAttachmentsData(AttachmentsData[] attachmentsData) {
        this.attachmentsData = attachmentsData;
    }

    public String getNbId() {
        return nbId;
    }

    public void setNbId(String nbId) {
        this.nbId = nbId;
    }

    public class AttachmentsData {
        private String fileName;

        private String fileSize;

        private String attachmentId;

        private String postId;

        private String fileType;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public void setAttachmentId(String attachmentId) {
            this.attachmentId = attachmentId;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

    }
}