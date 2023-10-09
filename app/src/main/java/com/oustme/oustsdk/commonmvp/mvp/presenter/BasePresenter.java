package com.oustme.oustsdk.commonmvp.mvp.presenter;

import com.oustme.oustsdk.commonmvp.mvp.view.BaseView;

public interface BasePresenter<T extends BaseView> {

    void attachView(T view);

    void onDestroy();
}
