package com.oustme.oustsdk.activity.common.noticeBoard.model.response;

import androidx.annotation.Keep;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.Map;

/**
 * Created by oust on 2/18/19.
 */

@Keep
public class PostUpdateData {
    private long id,updateTs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(long updateTs) {
        this.updateTs = updateTs;
    }

    public void init(Map<String, Object> postMap){
        this.id= OustSdkTools.convertToLong(postMap.get("postId"));
        this.updateTs=OustSdkTools.convertToLong(postMap.get("updatePost"));
    }

    public void initExtra(Map<String, Object> postMap){
        this.id= OustSdkTools.convertToLong(postMap.get("id"));
        this.updateTs=OustSdkTools.convertToLong(postMap.get("updateTime"));
    }
}
