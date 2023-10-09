package com.oustme.oustsdk.activity.common.noticeBoard.callBacks;

import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;

/**
 * Created by oust on 4/2/19.
 */

public interface NBDeleteListener {
    void onDelete(PostViewData postViewData);
    void onDeleteCancel();
}
