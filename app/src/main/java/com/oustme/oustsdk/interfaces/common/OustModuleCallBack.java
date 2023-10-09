package com.oustme.oustsdk.interfaces.common;

import com.oustme.oustsdk.model.request.Moduledata;

/**
 * Created by oust on 4/26/19.
 */

public interface OustModuleCallBack {
    void onModuleComplete(Moduledata moduledata);
    void onModuleProgress(Moduledata moduledata,int progress);
    void onModuleStatusChange(Moduledata moduledata,String status);
    void onModuleFailed(Moduledata moduledata);
}
