package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;


/**
 * Created by oust on 6/13/18.
 */

@Keep
public class CplModelData {
    private String contentType;
    private CommonLandingData commonLandingData;
    private DTONewFeed DTONewFeed;
    private long startDate;
    private long completedDate;
    private long contentId;
    private boolean isListenerSet;
    private boolean isUnlocked;
    private long cplPoints;
    private boolean pass;
    private long attemptCount;
    private boolean isCompleted;
    private boolean isUploadedToServer;
    private boolean hasDistributed;
    private boolean isModuleCompleted;
    private long totalOcCoins;
    private long userCompletionPercentage;

    public boolean isHasDistributed() {
        return hasDistributed;
    }

    public void setHasDistributed(boolean hasDistributed) {
        this.hasDistributed = hasDistributed;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public long getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(long attemptCount) {
        this.attemptCount = attemptCount;
    }

    public long getCplPoints() {
        return cplPoints;
    }

    public void setCplPoints(long cplPoints) {
        this.cplPoints = cplPoints;
    }

    public CplModelData() {
    }

    public CplModelData(Map<String, Object> currentContentMap) {
        init(currentContentMap);
    }

    private void init(Map<String, Object> currentContentMap) {
        if (currentContentMap != null) {
            this.setContentType((String) currentContentMap.get("contentType"));
            this.setContentId(OustSdkTools.convertToLong(currentContentMap.get("contentId")));
        }
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public boolean isListenerSet() {
        return isListenerSet;
    }

    public void setListenerSet(boolean listenerSet) {
        isListenerSet = listenerSet;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    private long sequence;

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(long completedDate) {
        this.completedDate = completedDate;
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

    public CommonLandingData getCommonLandingData() {
        return commonLandingData;
    }

    public void setCommonLandingData(CommonLandingData commonLandingData) {
        this.commonLandingData = commonLandingData;
    }

    public DTONewFeed getNewFeed() {
        return DTONewFeed;
    }

    public void setNewFeed(DTONewFeed DTONewFeed) {
        this.DTONewFeed = DTONewFeed;
    }

    public boolean isCplCourseModule() {
        try {
            if (this.getContentType() != null)
                return this.getContentType().equalsIgnoreCase("Course");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    public boolean isCplAssessmentModule() {
        try {
            if (this.getContentType() != null)
                return this.getContentType().equalsIgnoreCase("Assessment");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    public boolean isCplSurveyModule() {
        try {
            if (this.getContentType() != null)
                return this.getContentType().equalsIgnoreCase("Survey");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    public boolean isCplFeedModule() {
        try {
            if (this.getContentType() != null)
                return this.getContentType().equalsIgnoreCase("NEWSFEED");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    public boolean isCplCollectionModule() {
        try {
            if (this.getContentType() != null)
                return this.getContentType().equalsIgnoreCase("Collection");
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isUploadedToServer() {
        return isUploadedToServer;
    }

    public void setUploadedToServer(boolean uploadedToServer) {
        isUploadedToServer = uploadedToServer;
    }

    public boolean isModuleCompleted() {
        return isModuleCompleted;
    }

    public void setModuleCompleted(boolean moduleCompleted) {
        isModuleCompleted = moduleCompleted;
    }

    public long getTotalOcCoins() {
        return totalOcCoins;
    }

    public void setTotalOcCoins(long totalOcCoins) {
        this.totalOcCoins = totalOcCoins;
    }

    public long getUserCompletionPercentage() {
        return userCompletionPercentage;
    }

    public void setUserCompletionPercentage(long userCompletionPercentage) {
        this.userCompletionPercentage = userCompletionPercentage;
    }
}
