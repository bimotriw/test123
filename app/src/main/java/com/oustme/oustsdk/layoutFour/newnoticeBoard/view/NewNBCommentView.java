package com.oustme.oustsdk.layoutFour.newnoticeBoard.view;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBReplyData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBReplyData;

import java.util.List;

public interface NewNBCommentView {
    void onErrorFound();

    void setCommentData(String avatarUrl, String commentedBy, String comment, Long commentOn, String designation, String userRole);

    void setOrUpdateAdapter(List<NewNBReplyData> nbReplyDataList);

    void updateCommentsCount(int count);

    void noReplyAdded();

    void startApiCalls();

    void resetReplyText();
}
