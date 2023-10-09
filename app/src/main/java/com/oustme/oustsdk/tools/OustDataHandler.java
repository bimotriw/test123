package com.oustme.oustsdk.tools;

import com.oustme.oustsdk.firebase.common.CatalogDeatilData;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.CplCollectionData;
import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOCourseCard;
import com.oustme.oustsdk.room.dto.DTOUserFeedDetails;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oust on 11/2/17.
 */

public class OustDataHandler {
    private static OustDataHandler mInstance;
    private boolean paginationReachToEnd = false;
    private ArrayList<CommonLandingData> commonLandingDatas;
    private HashMap<String,CatalogDeatilData> catalogDeatilDatas;
    private boolean isAllCoursesLoaded=false,isAllAssessmentLoaded=false,isAllFeedsLoaded=false;
    private DTOCourseCard courseCardClass;
    private DTOUserFeedDetails.FeedDetails courseCard;
    private DTONewFeed DTONewFeed;
    private CplCollectionData cplCollectionData;
    private long cplId = 0;
    private String feedId;

    public String getFeedId() {
        if (feedId != null && !feedId.isEmpty())
            return feedId;
        return "feed";
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public long getCplId() {
        return cplId;
    }

    public void setCplId(long cplId) {
        this.cplId = cplId;
    }

    public CplCollectionData getCplCollectionData() {
        return cplCollectionData;
    }

    public void setCplCollectionData(CplCollectionData cplCollectionData) {
        this.cplCollectionData = cplCollectionData;
    }

    public DTONewFeed getNewFeed() {
        return DTONewFeed;
    }

    public void setNewFeed(DTONewFeed DTONewFeed) {
        this.DTONewFeed = DTONewFeed;
    }

    private HashMap<String, CommonLandingData> myDeskData = new HashMap<>();
    private HashMap<String, CommonLandingData> commonLandingDataHashMap = new HashMap<>();
    private HashMap<String, CommonLandingData> myAssessmentData = new HashMap<>();

    public void resetData(){
        commonLandingDatas=null;
        catalogDeatilDatas=null;
        isAllCoursesLoaded=false;
        isAllAssessmentLoaded=false;
        isAllFeedsLoaded=false;
        DTONewFeed =null;
        cplCollectionData=null;
        myDeskData = new HashMap<>();
        commonLandingDataHashMap = new HashMap<>();
        myAssessmentData = new HashMap<>();
    }


    private OustDataHandler() {
    }

    public static OustDataHandler getInstance() {
        if (mInstance == null) {
            mInstance = new OustDataHandler();
        }
        return mInstance;
    }

    public void saveData(ArrayList<CommonLandingData> commonLandingDatas) {
        this.commonLandingDatas = commonLandingDatas;
    }

    public void deleteData() {
        this.commonLandingDatas = null;
    }

    public ArrayList<CommonLandingData> getData() {
        return commonLandingDatas;
    }

    public void saveCollectionData(HashMap<String, CatalogDeatilData> catalogDeatilDatas) {
        this.catalogDeatilDatas = catalogDeatilDatas;
    }

    public void deleteCollectionData() {
        this.catalogDeatilDatas = null;
    }

    public HashMap<String, CatalogDeatilData> getCollectionData() {
        return catalogDeatilDatas;
    }

    public boolean isAllCoursesLoaded() {
        return isAllCoursesLoaded;
    }

    public void setAllCoursesLoaded(boolean allCoursesLoaded) {
        isAllCoursesLoaded = allCoursesLoaded;
    }

    public boolean isAllAssessmentLoaded() {
        return isAllAssessmentLoaded;
    }

    public void setAllAssessmentLoaded(boolean allAssessmentLoaded) {
        isAllAssessmentLoaded = allAssessmentLoaded;
    }

    public HashMap<String, CommonLandingData> getMyDeskData() {
        return myDeskData;
    }

    public void setMyDeskData(HashMap<String, CommonLandingData> myDeskData) {
        this.myDeskData = myDeskData;
    }

    public HashMap<String, CommonLandingData> getCommonLandingDataHashMap() {
        return commonLandingDataHashMap;
    }

    public void setCommonLandingDataHashMap(HashMap<String, CommonLandingData> commonLandingDataHashMap) {
        this.commonLandingDataHashMap = commonLandingDataHashMap;
    }

    public HashMap<String, CommonLandingData> getMyAssessmentData() {
        return myAssessmentData;
    }

    public void setMyAssessmentData(HashMap<String, CommonLandingData> myAssessmentData) {
        this.myAssessmentData = myAssessmentData;
    }

    public boolean isPaginationReachToEnd() {
        return paginationReachToEnd;
    }

    public void setPaginationReachToEnd(boolean paginationReachToEnd) {
        this.paginationReachToEnd = paginationReachToEnd;
    }

    public DTOCourseCard getCourseCardClass() {
        return courseCardClass;
    }

    public void setCourseCardClass(DTOCourseCard courseCardClass) {
        this.courseCardClass = courseCardClass;
    }

    public DTOUserFeedDetails.FeedDetails getCourseCard() {
        return courseCard;
    }

    public void setCourseCard(DTOUserFeedDetails.FeedDetails courseCard) {
        this.courseCard = courseCard;
    }

    public boolean isAllFeedsLoaded() {
        return isAllFeedsLoaded;
    }

    public void setAllFeedsLoaded(boolean allFeedsLoaded) {
        isAllFeedsLoaded = allFeedsLoaded;
    }
}
