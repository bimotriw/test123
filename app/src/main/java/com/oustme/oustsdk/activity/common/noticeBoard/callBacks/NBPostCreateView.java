package com.oustme.oustsdk.activity.common.noticeBoard.callBacks;

public interface NBPostCreateView {
    void showProgressBar(int type);
    void hideProgressBar(int type);
    void postCreatedSuccessfully(boolean success);
    void postCreateRequest();
    void errorCreatingPost(String msg);
}
