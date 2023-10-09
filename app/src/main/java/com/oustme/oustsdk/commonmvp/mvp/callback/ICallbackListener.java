package com.oustme.oustsdk.commonmvp.mvp.callback;

public interface ICallbackListener<T> {
    void onSuccess(T data);
    void onFailure(Throwable t);
}
