package com.oustme.oustsdk.activity.common.noticeBoard.model.response;


import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by oust on 2/18/19.
 */

@Keep
public class NBPostData {
    private static final String TAG = "NBPostData";
    private long id, nbId, createdOn, updatedOn, likeCount, commentedCount, sharedCount;
    private String title, description, createdBy, bannerBg, icon, tag, audio, video, assigneeRole, updatedBy;
    long nbPostRewardOC;
    private boolean hasLiked, hasCommented, hasShared, hasAttachment;
    private List<Attachment> attachmentsData;
    private boolean isAttachmentDownloaded;
    private List<NBCommentData> nbCommentDataList;

    public List<NBCommentData> getNbCommentDataList() {
        return nbCommentDataList;
    }

    public void setNbCommentDataList(List<NBCommentData> nbCommentDataList) {
        this.nbCommentDataList = nbCommentDataList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNbId() {
        return nbId;
    }

    public void setNbId(long nbId) {
        this.nbId = nbId;
    }

    public long getNbPostRewardOC() {
        return nbPostRewardOC;
    }

    public void setNbPostRewardOC(long nbPostRewardOC) {
        this.nbPostRewardOC = nbPostRewardOC;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getBannerBg() {
        return bannerBg;
    }

    public void setBannerBg(String bannerBg) {
        this.bannerBg = bannerBg;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAssigneeRole() {
        return assigneeRole;
    }

    public void setAssigneeRole(String assigneeRole) {
        this.assigneeRole = assigneeRole;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean hasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public boolean hasCommented() {
        return hasCommented;
    }

    public void setHasCommented(boolean hasCommented) {
        this.hasCommented = hasCommented;
    }

    public boolean hasShared() {
        return hasShared;
    }

    public void setHasShared(boolean hasShared) {
        this.hasShared = hasShared;
    }

    public boolean hasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentedCount() {
        return commentedCount;
    }

    public void setCommentedCount(long commentedCount) {
        this.commentedCount = commentedCount;
    }

    public long getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(long sharedCount) {
        this.sharedCount = sharedCount;
    }

    public List<Attachment> getAttachmentsData() {
        return attachmentsData;
    }

    public void setAttachmentsData(List<Attachment> attachmentsData) {
        this.attachmentsData = attachmentsData;
    }

    public void initUSerData(Map<String, Object> lpMap) {
        try {
            if(lpMap.get("hasCommented")!=null)
            {
                this.hasCommented = OustSdkTools.convertToBoolean(lpMap.get("hasCommented"));
            }
            if(lpMap.get("hasLiked")!=null)
            {
                this.hasLiked = OustSdkTools.convertToBoolean(lpMap.get("hasLiked"));
            }
            if(lpMap.get("hasShared")!=null)
            {
                this.hasShared = OustSdkTools.convertToBoolean(lpMap.get("hasShared"));
            }
            if(lpMap.get("nbId")!=null)
            {
                this.nbId = OustSdkTools.convertToLong(lpMap.get("nbId"));
            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void initBaseData(Map<String, Object> lpMap) {
        try {
            this.createdOn = OustSdkTools.convertToLong(lpMap.get("createdOn"));
            this.title = OustSdkTools.convertToStr(lpMap.get("name"));
            this.description = OustSdkTools.convertToStr(lpMap.get("description"));
            this.createdBy = OustSdkTools.convertToStr(lpMap.get("createdBy"));
            this.bannerBg = OustSdkTools.convertToStr(lpMap.get("bannerImage"));
            this.icon = OustSdkTools.convertToStr(lpMap.get("icon"));
            this.tag = OustSdkTools.convertToStr(lpMap.get("tag"));
            this.audio = OustSdkTools.convertToStr(lpMap.get("audio"));
            this.video = OustSdkTools.convertToStr(lpMap.get("video"));
            this.nbId = OustSdkTools.convertToLong(lpMap.get("nbId"));
            this.assigneeRole = OustSdkTools.convertToStr(lpMap.get("assigneeRole"));
            this.hasAttachment = OustSdkTools.convertToBoolean(lpMap.get("hasAttachment"));
            this.updatedOn = OustSdkTools.convertToLong(lpMap.get("updatedOn"));
            this.updatedBy = OustSdkTools.convertToStr(lpMap.get("updatedBy"));
            this.likeCount = OustSdkTools.convertToLong(lpMap.get("totalLikes"));
            this.sharedCount = OustSdkTools.convertToLong(lpMap.get("totalShares"));
            this.commentedCount = OustSdkTools.convertToLong(lpMap.get("totalComments"));
            this.nbPostRewardOC = OustSdkTools.convertToLong(lpMap.get("nbPostRewardOC"));

            if(lpMap.get("hasAttachment")!=null)
            {
                this.hasAttachment = (boolean)lpMap.get("hasAttachment");
            }
            if(lpMap.get("attachmentsData")!=null)
            {
//                try {
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    HashMap<String, Object> hashMap2 = new HashMap<>();
//                    this.attachmentsData = (ArrayList<Attachment>)lpMap.get("attachmentsData");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                attachmentsData = new ArrayList<>();
                ArrayList<Object> attachmentList= (ArrayList<Object>) lpMap.get("attachmentsData");
                if(attachmentList!=null && attachmentList.size()>0){
                    for(int i=0;i<attachmentList.size();i++){
                        Map<String,Object> attachmentMap = (Map<String, Object>) attachmentList.get(i);
                        Attachment attachment = new Attachment().initData(attachmentMap);
                        attachmentsData.add(attachment);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    public NBPostData incrementCommentCount() {
        this.commentedCount++;
        return this;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public boolean isHasCommented() {
        return hasCommented;
    }

    public boolean isHasShared() {
        return hasShared;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public boolean isAttachmentDownloaded() {
        return isAttachmentDownloaded;
    }

    public void setAttachmentDownloaded(boolean attachmentDownloaded) {
        isAttachmentDownloaded = attachmentDownloaded;
    }

    public void incrementShareCount() {
        this.sharedCount++;
    }

    public void addOfflineComment(NBCommentData nbCommentData){
        if(this.nbCommentDataList==null){
            this.nbCommentDataList = new ArrayList<>();
            nbCommentDataList.add(nbCommentData);
        }else{
            this.nbCommentDataList.add(0,nbCommentData);
        }
    }

    public boolean hasVideo() {
        return this.video != null && !this.video.isEmpty();
    }

    public boolean hasAudio() {
        return this.audio != null && !this.audio.isEmpty();
    }

    public boolean hasBannerBg() {
        return this.bannerBg != null && !this.bannerBg.isEmpty();
    }

    public class Attachment{
        private String fileName;
        private long fileSize;
        private String fileType;
        private int attachmentId;
        private String sizeInString;

        public Attachment() {
        }

        public Attachment(String fileName, long fileSize, String fileType, int attachmentId) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.fileType = fileType;
            this.attachmentId = attachmentId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
            float size = (float) fileSize/1000;
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            if(size>1000)
            {
                this.sizeInString = decimalFormat.format(size/1000)+" MB";
            }else {
                this.sizeInString = decimalFormat.format(size) +" KB";
            }
        }

        public String getSizeInString() {
            return sizeInString;
        }

        public void setSizeInString(String sizeInString) {
            this.sizeInString = sizeInString;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public int getAttachmentId() {
            return attachmentId;
        }

        public void setAttachmentId(int attachmentId) {
            this.attachmentId = attachmentId;
        }


        public Attachment initData(Map<String,Object> lpMap){
            this.fileName = OustSdkTools.convertToStr(lpMap.get("fileName"));
            this.fileType = OustSdkTools.convertToStr(lpMap.get("fileType"));
            this.fileSize = OustSdkTools.convertToLong(lpMap.get("fileSize"));
            if(lpMap.get("fileSize")!=null)
            {
                setFileSize(this.fileSize);
            }
            return this;
        }

    }

}
