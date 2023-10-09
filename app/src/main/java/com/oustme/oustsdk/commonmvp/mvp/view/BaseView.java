package com.oustme.oustsdk.commonmvp.mvp.view;

public interface BaseView extends ILoadingView{
    void initPresenter();

    void destroy();
}
