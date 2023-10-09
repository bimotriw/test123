package com.oustme.oustsdk.activity.common.noticeBoard.model.request;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class NBPostData {
    int postId;
    int nbId;
    long nbPostRewardOC;
    String name;
    String description;
    String bannerImage;
    String video;
    String audio;
    boolean hasAttachment;
    int totalLikes;
    List<NBPostAttachmentsData> attachmentsData;

    public NBPostData() {
    }

    public NBPostData(int postId, int nbId, String name, String description, String bannerImage, String video, String audio, boolean hasAttachment, int totalLikes, List<NBPostAttachmentsData> attachmentsData) {
        this.postId = postId;
        this.nbId = nbId;
        this.name = name;
        this.description = description;
        this.bannerImage = bannerImage;
        this.video = video;
        this.audio = audio;
        this.hasAttachment = hasAttachment;
        this.totalLikes = totalLikes;
        this.attachmentsData = attachmentsData;
    }

    public int getPostId() {
        return postId;
    }

    public NBPostData setPostId(int postId) {
        this.postId = postId;
        return this;
    }

    public int getNbId() {
        return nbId;
    }

    public NBPostData setNbId(int nbId) {
        this.nbId = nbId;
        return this;
    }

    public String getName() {
        return name;
    }

    public NBPostData setName(String name) {
        this.name = name;
        return this;
    }

    public long getNbPostRewardOC() {
        return nbPostRewardOC;
    }

    public void setNbPostRewardOC(long nbPostRewardOC) {
        this.nbPostRewardOC = nbPostRewardOC;
    }

    public String getDescription() {
        return description;
    }

    public NBPostData setDescription(String description) {
        this.description = description;
        return this;

    }

    public String getBannerImage() {
        return bannerImage;
    }

    public NBPostData setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
        return this;
    }

    public String getVideo() {
        return video;
    }

    public NBPostData setVideo(String video) {
        this.video = video;
        return this;
    }

    public String getAudio() {
        return audio;
    }

    public NBPostData setAudio(String audio) {
        this.audio = audio;
        return this;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public NBPostData setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
        return this;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public NBPostData setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
        return this;

    }

    public List<NBPostAttachmentsData> getAttachmentsData() {
        return attachmentsData;
    }

    public NBPostData setAttachmentsData(List<NBPostAttachmentsData> attachmentsData) {
        this.attachmentsData = attachmentsData;
        return this;

    }

    public class NBPostAttachmentsData{
        int attachmentId;
        String postId;
        String fileName;
        String fileType;
        String fileSize;

        public NBPostAttachmentsData(int attachmentId, String postId, String fileName, String fileType, String fileSize) {
            this.attachmentId = attachmentId;
            this.postId = postId;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }

        public NBPostAttachmentsData() {
        }

        public int getAttachmentId() {
            return attachmentId;
        }

        public NBPostAttachmentsData setAttachmentId(int attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        public String getPostId() {
            return postId;
        }

        public NBPostAttachmentsData setPostId(String postId) {
            this.postId = postId;
            return this;
        }

        public String getFileName() {
            return fileName;
        }

        public NBPostAttachmentsData setFileName(String fileName) {
            this.fileName = fileName;
            return this;

        }

        public String getFileType() {
            return fileType;
        }

        public NBPostAttachmentsData setFileType(String fileType) {
            this.fileType = fileType;
            return this;

        }

        public String getFileSize() {
            return fileSize;
        }

        public NBPostAttachmentsData setFileSize(String fileSize) {
            this.fileSize = fileSize;
            return this;
        }
    }
}
