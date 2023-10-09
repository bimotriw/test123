package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class NewNBPostData {
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
    List<NewNBPostAttachmentsData> attachmentsData;

    public NewNBPostData() {
    }

    public NewNBPostData(int postId, int nbId, String name, String description, String bannerImage, String video, String audio, boolean hasAttachment, int totalLikes, List<NewNBPostAttachmentsData> attachmentsData) {
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

    public NewNBPostData setPostId(int postId) {
        this.postId = postId;
        return this;
    }

    public int getNbId() {
        return nbId;
    }

    public NewNBPostData setNbId(int nbId) {
        this.nbId = nbId;
        return this;
    }

    public String getName() {
        return name;
    }

    public NewNBPostData setName(String name) {
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

    public NewNBPostData setDescription(String description) {
        this.description = description;
        return this;

    }

    public String getBannerImage() {
        return bannerImage;
    }

    public NewNBPostData setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
        return this;
    }

    public String getVideo() {
        return video;
    }

    public NewNBPostData setVideo(String video) {
        this.video = video;
        return this;
    }

    public String getAudio() {
        return audio;
    }

    public NewNBPostData setAudio(String audio) {
        this.audio = audio;
        return this;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public NewNBPostData setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
        return this;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public NewNBPostData setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
        return this;

    }

    public List<NewNBPostAttachmentsData> getAttachmentsData() {
        return attachmentsData;
    }

    public NewNBPostData setAttachmentsData(List<NewNBPostAttachmentsData> attachmentsData) {
        this.attachmentsData = attachmentsData;
        return this;

    }

    public class NewNBPostAttachmentsData{
        int attachmentId;
        String postId;
        String fileName;
        String fileType;
        String fileSize;

        public NewNBPostAttachmentsData(int attachmentId, String postId, String fileName, String fileType, String fileSize) {
            this.attachmentId = attachmentId;
            this.postId = postId;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }

        public NewNBPostAttachmentsData() {
        }

        public int getAttachmentId() {
            return attachmentId;
        }

        public NewNBPostAttachmentsData setAttachmentId(int attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        public String getPostId() {
            return postId;
        }

        public NewNBPostAttachmentsData setPostId(String postId) {
            this.postId = postId;
            return this;
        }

        public String getFileName() {
            return fileName;
        }

        public NewNBPostAttachmentsData setFileName(String fileName) {
            this.fileName = fileName;
            return this;

        }

        public String getFileType() {
            return fileType;
        }

        public NewNBPostAttachmentsData setFileType(String fileType) {
            this.fileType = fileType;
            return this;

        }

        public String getFileSize() {
            return fileSize;
        }

        public NewNBPostAttachmentsData setFileSize(String fileSize) {
            this.fileSize = fileSize;
            return this;
        }
    }
}
