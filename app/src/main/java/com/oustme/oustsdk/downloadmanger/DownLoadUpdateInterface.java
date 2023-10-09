package com.oustme.oustsdk.downloadmanger;

import com.oustme.oustsdk.firebase.course.CourseCardClass;

public interface DownLoadUpdateInterface {
    void onDownLoadProgressChanged(String message, String progress);
    void onDownLoadError(String message, int errorCode);
    void onDownLoadStateChanged(String message, int code);
    void onAddedToQueue(String id);
    void onDownLoadStateChangedWithId(String message, int code, String id);
}
