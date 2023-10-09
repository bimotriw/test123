package com.oustme.oustsdk.activity.common.noticeBoard.callBacks;

import android.view.View;

import com.oustme.oustsdk.activity.common.noticeBoard.model.request.PostViewData;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;

/**
 * Created by oust on 2/22/19.
 */

public interface NBPostClickCallBack {
    void onPostViewed(PostViewData postViewData);
    void onPostViewed(int position);
    void onPostLike(PostViewData postViewData);
    void onPostComment(PostViewData postViewData);
    void onPostCommentDelete(PostViewData postViewData);
    void onPostShare(PostViewData postViewData, View view);
    void onRequestPermissions(PostViewData postViewData, View view);

}
