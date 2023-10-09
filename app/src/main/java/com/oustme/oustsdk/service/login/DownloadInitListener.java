package com.oustme.oustsdk.service.login;

/**
 * Created by oust on 4/30/19.
 */

public interface DownloadInitListener {
    void onDownloadFailed();
    void onDownloadComplete();
    void onProgressChanged(int progress);
}
