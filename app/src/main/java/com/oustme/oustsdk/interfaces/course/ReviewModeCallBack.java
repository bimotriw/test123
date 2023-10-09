package com.oustme.oustsdk.interfaces.course;

public interface ReviewModeCallBack {
    void onMainRowClick(int position);
    void onCardClick(int levelPosition,int cardPosition);
    void onCardClick(int levelPosition,int cardPosition, boolean isRegularMode);
    void onDownloadIconClick(int position);
}
