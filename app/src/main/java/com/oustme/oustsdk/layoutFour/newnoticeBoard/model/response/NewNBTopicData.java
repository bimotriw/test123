package com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response;

import android.util.Log;


import androidx.annotation.Keep;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.PostUpdateData;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Keep
public class NewNBTopicData implements Serializable {
    private long id, createdOn, no_of_members, totalPost, assignedOn, nbPostRewardOC;
    private String topic, description, createdBy, tag, bannerBg, icon;
    private ArrayList<NewPostUpdateData> userPostUpdateData, postUpdateData;
    ArrayList<NewNbPost> nbPosts;

    public ArrayList<NewNbPost> getNbPosts() {
        return nbPosts;
    }

    public void setNbPosts(ArrayList<NewNbPost> nbPosts) {
        this.nbPosts = nbPosts;
    }

    private boolean isKeySet = false;

    public boolean isKeySet() {
        return isKeySet;
    }

    public void setKeySet(boolean keySet) {
        isKeySet = keySet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public long getNo_of_members() {
        return no_of_members;
    }

    public void setNo_of_members(long no_of_members) {
        this.no_of_members = no_of_members;
    }

    public long getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(long totalPost) {
        this.totalPost = totalPost;
    }

    public String getTopic() {
        return topic;
    }

    public long getNbPostRewardOC() {
        return nbPostRewardOC;
    }

    public void setNbPostRewardOC(long nbPostRewardOC) {
        this.nbPostRewardOC = nbPostRewardOC;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public ArrayList<NewPostUpdateData> getUserPostUpdateData() {
        return userPostUpdateData;
    }

    public void setUserPostUpdateData(ArrayList<NewPostUpdateData> userPostUpdateData) {
        this.userPostUpdateData = userPostUpdateData;
    }

    public long getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(long assignedOn) {
        this.assignedOn = assignedOn;
    }

    public ArrayList<NewPostUpdateData> getPostUpdateData() {
        return postUpdateData;
    }

    public int getPostUpdateDataSize() {
        if (postUpdateData != null)
            return postUpdateData.size();
        return 0;
    }

    public void setPostUpdateData(ArrayList<NewPostUpdateData> postUpdateData) {
        this.postUpdateData = postUpdateData;
    }

    public void init(Map<String, Object> lpMap) {
        try {
            this.id = OustSdkTools.convertToLong(lpMap.get("nbId"));
            this.assignedOn = OustSdkTools.convertToLong(lpMap.get("assignedOn"));
            if (lpMap.containsKey("postUpdate")) {
                Map<String, Object> userPostMap = (Map<String, Object>) lpMap.get("postUpdate");
                this.userPostUpdateData = new ArrayList<>();
                this.nbPosts = new ArrayList<>();
                for (String postKey : userPostMap.keySet()) {
                    Map<String, Object> postMap = (Map<String, Object>) userPostMap.get(postKey);
                    NewPostUpdateData postUpdateData = new NewPostUpdateData();
                    postUpdateData.init(postMap);
                    this.userPostUpdateData.add(postUpdateData);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public NewNBTopicData setExtraNBData(NewNBTopicData nbTopicData, Map<String, Object> lpMap) {
        try {
            if (lpMap != null) {
                nbTopicData.setBannerBg(OustSdkTools.convertToStr(lpMap.get("bannerImage")));
                nbTopicData.setIcon(OustSdkTools.convertToStr(lpMap.get("icon")));
                nbTopicData.setTopic(OustSdkTools.convertToStr(lpMap.get("name")));
                nbTopicData.setDescription(OustSdkTools.convertToStr(lpMap.get("description")));
                nbTopicData.setNo_of_members(OustSdkTools.convertToLong(lpMap.get("membersCount")));
                nbTopicData.setTotalPost(OustSdkTools.convertToLong(lpMap.get("numOfPost")));
                nbTopicData.setNbPostRewardOC(OustSdkTools.convertToLong(lpMap.get("nbPostRewardOC")));
                nbTopicData.setTag(OustSdkTools.convertToStr(lpMap.get("tag")));
                if (lpMap.containsKey("post")) {
                    Map<String, Object> userPostMap = (Map<String, Object>) lpMap.get("post");
                    nbTopicData.postUpdateData = new ArrayList<>();
                    for (String postKey : userPostMap.keySet()) {
                        Map<String, Object> postMap = (Map<String, Object>) userPostMap.get(postKey);

                        NewPostUpdateData postUpdateData = new NewPostUpdateData();
                        postUpdateData.initExtra(postMap);
                        nbTopicData.postUpdateData.add(postUpdateData);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return nbTopicData;
    }

    public int getNewPostCount() {
        int count = 0;
        try {
            if (this.postUpdateData == null) {
                return 0;
            }
            for (int i = 0; i < this.postUpdateData.size(); i++) {
                if (postUpdateData.get(i) != null) {
                    if (this.userPostUpdateData == null || this.userPostUpdateData.get(i) == null) {
                        count++;
                    } else {
                        if (this.postUpdateData.get(i).getUpdateTs() >= this.userPostUpdateData.get(i).getUpdateTs())
                            count++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return count;
    }
}
