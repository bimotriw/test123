package com.oustme.oustsdk.activity.common.noticeBoard.view;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;

import java.util.List;

/**
 * Created by oust on 3/8/19.
 */

public interface NBCommentView {
    void onErrorFound();
    void setCommentData(String avatarUrl, String commentedBy, String comment);
    void setOrUpdateAdapter(List<NBReplyData> nbReplyDataList);
    void updateCommentsCount(int count);

    void noReplyAdded();

    void startApiCalls();

    void resetReplyText();
}
