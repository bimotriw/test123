package com.oustme.oustsdk.activity.common.noticeBoard.callBacks;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;

import java.util.List;

/**
 * Created by oust on 2/22/19.
 */

public interface PostDataRepository {
    void gotAllPostData(List<NBPostData> nbPostDataList);

    //Todo : remove this as we don't need two func for same thing
    void gotPostData(NBPostData nbPostData);
}
