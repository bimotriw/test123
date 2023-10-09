package com.oustme.oustsdk.service.login;

import com.oustme.oustsdk.response.common.SignInResponse;

/**
 * Created by oust on 4/24/19.
 */

public interface OustLoginApiCallBack {
    void onLoginApiComplete();
    void onLoginApiError(String error);
    void onLoginStart();
    void onError(String error);
    void onDownloadProgress(int progress);

}
