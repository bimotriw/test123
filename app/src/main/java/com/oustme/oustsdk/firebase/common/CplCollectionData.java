package com.oustme.oustsdk.firebase.common;

import androidx.annotation.Keep;

import com.oustme.oustsdk.room.dto.DTOCplCompletedModel;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by oust on 6/13/18.
 */

@Keep
public class CplCollectionData {
    private String cplName, cplDescription;
    private long progress, assignedDate, completedDate;
    private ArrayList<CplModelData> cplModelDataArrayList;
    private HashMap<String, CplModelData> cplModelDataHashMap;
    private String cplId;
    private String banner, bgImage, thumbnail;
    private CplModelData currentCplData;
    private String cplVersion;
    private String introBgImg, introIcon;
    private boolean isListenerSet = false;
    private boolean certificate = false;
    private long oustCoins, earnedCoins;
    private String assessmentMarkerIcon, courseMarkerIcon, feedMarkerIcon, endFlagIcon, pathColor, navIcon, pathStartIcon, markerLockIcon, moduleCompletionImg, cplCompletionImg;
    private boolean progressAfterAssessmentFail;
    private boolean showRateCpl = false;
    private boolean isEnrolled;
    private long enrolledCount;
    private boolean showModuleCompletionAnimation;
    private long updateTime;
    private long userUpdateTime;

    private String cplAddOn;
    private String navIconMovingAudio, navIconStartAudio, cplCompleteAudio, coinAddedAudio;
    private HashMap<Long, DTOCplCompletedModel> cplCompletedModelList;
    private String cplType;
    private String status;
    private boolean disableBackButton;
    private long parentCPLId;
    private long langId;
    private boolean showOnMainScreen;
    private boolean showCplRatingPopUp;
    private String activeChildCPL;
    private long rating;

    public boolean isCertificate() {
        return certificate;
    }

    public void setCertificate(boolean certificate) {
        this.certificate = certificate;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getUserUpdateTime() {
        return userUpdateTime;
    }

    public void setUserUpdateTime(long userUpdateTime) {
        this.userUpdateTime = userUpdateTime;
    }

    public String getNavIconMovingAudio() {
        return navIconMovingAudio;
    }

    public void setNavIconMovingAudio(String navIconMovingAudio) {
        this.navIconMovingAudio = navIconMovingAudio;
    }

    public String getNavIconStartAudio() {
        return navIconStartAudio;
    }

    public void setNavIconStartAudio(String navIconStartAudio) {
        this.navIconStartAudio = navIconStartAudio;
    }

    public String getCplCompleteAudio() {
        return cplCompleteAudio;
    }

    public void setCplCompleteAudio(String cplCompleteAudio) {
        this.cplCompleteAudio = cplCompleteAudio;
    }

    public HashMap<Long, DTOCplCompletedModel> getCplCompletedModelList() {
        return cplCompletedModelList;
    }

    public void setCplCompletedModelList(HashMap<Long, DTOCplCompletedModel> cplCompletedModelList) {
        this.cplCompletedModelList = cplCompletedModelList;
    }

    public String getCoinAddedAudio() {
        return coinAddedAudio;
    }

    public void setCoinAddedAudio(String coinAddedAudio) {
        this.coinAddedAudio = coinAddedAudio;
    }

    public boolean isShowModuleCompletionAnimation() {
        return showModuleCompletionAnimation;
    }

    public void setShowModuleCompletionAnimation(boolean showModuleCompletionAnimation) {
        this.showModuleCompletionAnimation = showModuleCompletionAnimation;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    public boolean isShowRateCpl() {
        return showRateCpl;
    }

    public void setShowRateCpl(boolean showRateCpl) {
        this.showRateCpl = showRateCpl;
    }

    public long getOustCoins() {
        return oustCoins;
    }

    public long getEarnedCoins() {
        return earnedCoins;
    }

    public void setEarnedCoins(long earnedCoins) {
        this.earnedCoins = earnedCoins;
    }

    public void setOustCoins(long oustCoins) {
        this.oustCoins = oustCoins;
    }

    public boolean isRateCourse() {
        return rateCourse;
    }

    public void setRateCourse(boolean rateCourse) {
        this.rateCourse = rateCourse;
    }

    private boolean rateCourse;


    public boolean isListenerSet() {
        return isListenerSet;
    }

    public void setListenerSet(boolean listenerSet) {
        isListenerSet = listenerSet;
    }

    public String getIntroBgImg() {
        return introBgImg;
    }

    public void setIntroBgImg(String introBgImg) {
        this.introBgImg = introBgImg;
    }

    public String getIntroIcon() {
        return introIcon;
    }

    public void setIntroIcon(String introIcon) {
        this.introIcon = introIcon;
    }

    public String getCplVersion() {
        return cplVersion;
    }

    public void setCplVersion(String cplVersion) {
        this.cplVersion = cplVersion;
    }

    public String getModuleCompletionImg() {
        return moduleCompletionImg;
    }

    public void setModuleCompletionImg(String moduleCompletionImg) {
        this.moduleCompletionImg = moduleCompletionImg;
    }

    public String getCplCompletionImg() {
        return cplCompletionImg;
    }

    public void setCplCompletionImg(String cplCompletionImg) {
        this.cplCompletionImg = cplCompletionImg;
    }

    public long getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(long enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public CplCollectionData(List<Object> learningList) {
        for (int i = 0; i < learningList.size(); i++) {
            Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
            if (lpMap != null) {
                extractData(i + "", lpMap);
            }
        }
    }

    public CplCollectionData(Map<String, Object> lpMainMap) {
        init(lpMainMap);
    }


    public CplCollectionData() {
    }

    private void init(Map<String, Object> lpMainMap) {
        Map<String, Object> cplMainMap = null;
        String cplkey = "";
        for (Map.Entry<String, Object> entry : lpMainMap.entrySet()) {
            cplMainMap = (Map<String, Object>) entry.getValue();
            cplkey = entry.getKey();
        }
        extractData(cplkey, cplMainMap);
    }

    private void extractData(String cplkey, Map<String, Object> cplMainMap) {
        this.setCplId(cplkey.contains("cpl") ? cplkey.replace("cpl", "") : cplkey);
        this.setAssignedDate(OustSdkTools.convertToLong(cplMainMap.get("assignedOn")));

        if (cplMainMap.containsKey("cplDescription")) {
            this.setCplDescription((String) cplMainMap.get("cplDescription"));
        }
        if (cplMainMap.containsKey("cplName")) {
            this.setCplName((String) cplMainMap.get("cplName"));
        }
        if (cplMainMap.containsKey("banner")) {
            this.setBanner((String) cplMainMap.get("banner"));
        }
        if (cplMainMap.containsKey("participants")) {
            this.setEnrolledCount((long) cplMainMap.get("enrolledUsers"));
        }
        this.setProgress(OustSdkTools.convertToLong(cplMainMap.get("completionPercentage")));

        this.setCompletedDate(OustSdkTools.convertToLong(cplMainMap.get("completedOn")));

        this.setCplCoins(OustSdkTools.convertToLong(cplMainMap.get("earnedCoins")));

        this.setTotalCoins(OustSdkTools.convertToLong(cplMainMap.get("totalCoins")));

        if (cplMainMap.containsKey("currentUserContent")) {
            this.setCurrentCplData(new CplModelData((Map<String, Object>) cplMainMap.get("currentUserContent")));
        }
    }

    public CplModelData getCurrentCplData() {
        return currentCplData;
    }

    public void setCurrentCplData(CplModelData currentCplData) {
        this.currentCplData = currentCplData;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getCplId() {
        return cplId;
    }

    public void setCplId(String cplId) {
        this.cplId = cplId;
    }

    public HashMap<String, CplModelData> getCplModelDataHashMap() {
        return cplModelDataHashMap;
    }

    public void setCplModelDataHashMap(HashMap<String, CplModelData> cplModelDataHashMap) {
        this.cplModelDataHashMap = cplModelDataHashMap;
    }

    private long totalCoins;
    private long cplCoins;

    public long getTotalCoins() {
        return totalCoins;
    }

    public long getCplCoins() {
        return cplCoins;
    }

    public void setCplCoins(long cplCoins) {
        this.cplCoins = cplCoins;
    }

    public void setTotalCoins(long totalCoins) {
        this.totalCoins = totalCoins;
    }

    public String getCplName() {
        return cplName;
    }

    public void setCplName(String cplName) {
        this.cplName = cplName;
    }

    public String getCplDescription() {
        return cplDescription;
    }

    public void setCplDescription(String cplDescription) {
        this.cplDescription = cplDescription;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public ArrayList<CplModelData> getCplModelDataArrayList() {
        return cplModelDataArrayList;
    }

    public void setCplModelDataArrayList(ArrayList<CplModelData> cplModelDataArrayList) {
        this.cplModelDataArrayList = cplModelDataArrayList;
    }

    public long getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(long completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isCplModelHashMapNotNull() {
        return this.cplModelDataHashMap != null && this.cplModelDataHashMap.size() > 0;
    }


    public String getAssessmentMarkerIcon() {
        return assessmentMarkerIcon;
    }

    public void setAssessmentMarkerIcon(String assessmentMarkerIcon) {
        this.assessmentMarkerIcon = assessmentMarkerIcon;
    }

    public String getCourseMarkerIcon() {
        return courseMarkerIcon;
    }

    public void setCourseMarkerIcon(String courseMarkerIcon) {
        this.courseMarkerIcon = courseMarkerIcon;
    }

    public String getFeedMarkerIcon() {
        return feedMarkerIcon;
    }

    public void setFeedMarkerIcon(String feedMarkerIcon) {
        this.feedMarkerIcon = feedMarkerIcon;
    }

    public String getEndFlagIcon() {
        return endFlagIcon;
    }

    public void setEndFlagIcon(String endFlagIcon) {
        this.endFlagIcon = endFlagIcon;
    }

    public String getPathColor() {
        return pathColor;
    }

    public void setPathColor(String pathColor) {
        this.pathColor = pathColor;
    }

    public String getNavIcon() {
        return navIcon;
    }

    public void setNavIcon(String navIcon) {
        this.navIcon = navIcon;
    }

    public String getPathStartIcon() {
        return pathStartIcon;
    }

    public void setPathStartIcon(String pathStartIcon) {
        this.pathStartIcon = pathStartIcon;
    }

    public String getMarkerLockIcon() {
        return markerLockIcon;
    }

    public void setMarkerLockIcon(String markerLockIcon) {
        this.markerLockIcon = markerLockIcon;
    }

    public boolean isProgressAfterAssessmentFail() {
        return progressAfterAssessmentFail;
    }

    public void setProgressAfterAssessmentFail(boolean progressAfterAssessmentFail) {
        this.progressAfterAssessmentFail = progressAfterAssessmentFail;
    }

    public String getCplAddOn() {
        return cplAddOn;
    }

    public void setCplAddOn(String cplAddOn) {
        this.cplAddOn = cplAddOn;
    }

    public String getCplType() {
        return cplType;
    }

    public void setCplType(String cplType) {
        this.cplType = cplType;
    }

    public void initData(Map<String, Object> cplInfoMap) {
        try {
            if (cplInfoMap != null) {
                this.setBgImage(getString(cplInfoMap.get("bgImage")));
                this.setBanner(getString(cplInfoMap.get("banner")));
                this.setIntroBgImg(getString(cplInfoMap.get("introBgImg")));
                this.setIntroIcon(getString(cplInfoMap.get("introIcon")));
                this.setAssessmentMarkerIcon(getString(cplInfoMap.get("assessmentMarkerIcon")));
                this.setCourseMarkerIcon(getString(cplInfoMap.get("courseMarkerIcon")));
                this.setFeedMarkerIcon(getString(cplInfoMap.get("feedMarkerIcon")));
                this.setEndFlagIcon(getString(cplInfoMap.get("endFlagIcon")));
                this.setPathColor(getString(cplInfoMap.get("pathColor")));
                this.setNavIcon(getString(cplInfoMap.get("navIcon")));
                this.setPathStartIcon(getString(cplInfoMap.get("pathStartIcon")));
                this.setMarkerLockIcon(getString(cplInfoMap.get("markerLockIcon")));
                this.setCplVersion(getString(cplInfoMap.get("version")));
                this.setProgressAfterAssessmentFail(getBoolean(cplInfoMap.get("progressAfterAssessmentFail")));
                this.setRateCourse(getBoolean(cplInfoMap.get("rateCourse")));
                this.setOustCoins(OustSdkTools.convertToLong(cplInfoMap.get("oustCoins")));
                this.setCplCompletionImg(getString(cplInfoMap.get("cplCompletionImg")));
                if (cplInfoMap.get("updateTime") != null) {
                    this.setUpdateTime(OustSdkTools.convertToLong(cplInfoMap.get("updateTime")));
                }
                this.setNavIconStartAudio(getString(cplInfoMap.get("navIconStartAudio")));
                this.setNavIconMovingAudio(getString(cplInfoMap.get("navIconMovingAudio")));
                this.setCplCompleteAudio(getString(cplInfoMap.get("cplCompleteAudio")));
                this.setCoinAddedAudio(getString(cplInfoMap.get("coinAddedAudio")));
                this.setCertificate(getBoolean(cplInfoMap.get("certificate")));
                this.setModuleCompletionImg(getString(cplInfoMap.get("moduleCompletionImg")));
                this.setShowModuleCompletionAnimation(getBoolean(cplInfoMap.get("showModuleCompletionAnimation")));
                this.setCplType(getString(cplInfoMap.get("cplType")));
                if (cplInfoMap.get("showOnMainScreen") != null) {
                    this.setShowOnMainScreen((boolean) cplInfoMap.get("showOnMainScreen"));
                } else {
                    this.setShowOnMainScreen(false);
                }

                if (cplInfoMap.get("showCplRatingPopUp") != null) {
                    this.setShowCplRatingPopUp((boolean) cplInfoMap.get("showCplRatingPopUp"));
                } else {
                    this.setShowCplRatingPopUp(false);
                }

                this.setDisableBackButton((cplInfoMap.get("cplDisableBackButton") != null) && getBoolean(cplInfoMap.get("cplDisableBackButton")));
                if (cplInfoMap.get("parentCPLId") != null) {
                    this.setParentCPLId(OustSdkTools.convertToLong(cplInfoMap.get("parentCPLId")));
                } else {
                    this.setParentCPLId(0);
                }
                if (cplInfoMap.get("langId") != null) {
                    this.setLangId(OustSdkTools.convertToLong(cplInfoMap.get("langId")));
                } else {
                    this.setLangId(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    protected boolean getBoolean(Object o) {
        return o != null && (boolean) o;
    }

    protected String getString(Object o) {
        return o != null ? (String) o : "";
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDisableBackButton() {
        return disableBackButton;
    }

    public void setDisableBackButton(boolean disableBackButton) {
        this.disableBackButton = disableBackButton;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getParentCPLId() {
        return parentCPLId;
    }

    public void setParentCPLId(long parentCPLId) {
        this.parentCPLId = parentCPLId;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public boolean getShowOnMainScreen() {
        return showOnMainScreen;
    }

    public void setShowOnMainScreen(boolean showOnMainScreen) {
        this.showOnMainScreen = showOnMainScreen;
    }

    public boolean isShowCplRatingPopUp() {
        return showCplRatingPopUp;
    }

    public void setShowCplRatingPopUp(boolean showCplRatingPopUp) {
        this.showCplRatingPopUp = showCplRatingPopUp;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getActiveChildCPL() {
        return activeChildCPL;
    }

    public void setActiveChildCPL(String activeChildCPL) {
        this.activeChildCPL = activeChildCPL;
    }

    public static Comparator<CplCollectionData> sortByDate = (s1, s2) -> {
        if (s1.getCplAddOn() == null) {
            return -1;
        }
        if (s2.getCplAddOn() == null) {
            return 1;
        }
        if (s1.getCplAddOn().equals(s2.getCplAddOn())) {
            return 0;
        }
        return s1.getCplAddOn().compareTo(s2.getCplAddOn());
    };
}
