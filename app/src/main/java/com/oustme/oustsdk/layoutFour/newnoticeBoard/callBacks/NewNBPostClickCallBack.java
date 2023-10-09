package com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks;

import android.view.View;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.request.NewPostViewData;

public interface NewNBPostClickCallBack {
    void onPostViewed(NewPostViewData postViewData);
    void onPostViewed(int position);
    void onPostLike(NewPostViewData postViewData);
    void onPostComment(NewPostViewData postViewData);
    void onPostCommentDelete(NewPostViewData postViewData);
    void onPostShare(NewPostViewData postViewData, View view);
    void onRequestPermissions(NewPostViewData postViewData, View view);

}
