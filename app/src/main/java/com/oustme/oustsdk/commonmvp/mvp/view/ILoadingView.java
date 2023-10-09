package com.oustme.oustsdk.commonmvp.mvp.view;

public interface ILoadingView {
    void showProgress();

    void hideProgress();

    void showNoData();

    void showMessage(String errorMessage);
}
