package com.oustme.oustsdk.compression.libffmpeg;

public interface FFmpegLoadBinaryResponseHandler extends ResponseHandler {

    /**
     * on Fail
     */
    void onFailure();

    /**
     * on Success
     */
    void onSuccess();

}
