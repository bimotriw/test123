package com.oustme.oustsdk.layoutFour.newnoticeBoard.callBacks;

public interface NewNBPostCreateView {
    void showProgressBar(int type);
    void hideProgressBar(int type);
    void postCreatedSuccessfully(boolean success);
    void postCreateRequest();
    void errorCreatingPost(String msg);
}
