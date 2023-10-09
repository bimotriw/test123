package com.oustme.oustsdk.activity.common.noticeBoard.callBacks;


import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBCommentData;

/**
 * Created by oust on 3/8/19.
 */

public interface CommentDataRepository {
    void gotCommentData(NBCommentData nbCommentData);

}
