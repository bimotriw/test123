package com.oustme.oustsdk.interfaces.common;

/**
 * Created by admin on 26/11/17.
 */

public interface OustLoginCallBack {
    /**
     *
     */
    void onStartDownloadingResourses();
    /**
     *
     */
    void onProgressChanged(int progress);

    /**
     *
     */
    void onError(String message);

    /**
     *
     */
    void onLoginError(String message);

    /**
     *
     */
    void onLoginProcessStart();

    /**
     *
     */
    void onLoginSuccess(boolean status);

    /**
     *
     */
    void onNetworkError();
    void onOustLoginStatus(String message);
    void onOustContentLoaded();
}
