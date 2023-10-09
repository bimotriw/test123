package com.oustme.oustsdk.api_sdk.impl;

import android.app.Activity;

import com.oustme.oustsdk.api_sdk.handlers.services.OustApiListener;
import com.oustme.oustsdk.api_sdk.handlers.services.OustModuleDataHandler;
import com.oustme.oustsdk.api_sdk.models.OustModuleData;
import com.oustme.oustsdk.interfaces.common.OustLoginCallBack;
import com.oustme.oustsdk.interfaces.common.OustModuleCallBack;
import com.oustme.oustsdk.launcher.OustAuthData;
import com.oustme.oustsdk.launcher.OustExceptions.OustException;
import com.oustme.oustsdk.launcher.OustLauncher;
import com.oustme.oustsdk.model.request.Moduledata;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by oust on 4/30/19.
 */

public class OustModuleImpl implements OustLoginCallBack, OustModuleCallBack {

    private static final String TAG = "OustModuleImpl";
    private OustApiListener oustApiListener = null;

    public void launchBasicOust(Activity activity, OustAuthData oustAuthData) {
        this.oustApiListener = (OustApiListener) activity;
        //OustLauncher.getInstance().addOustModuleCallBack(this);
        try {
            OustLauncher.getInstance().launch(activity, oustAuthData, null, this);
        } catch (OustException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void launchOustApi(Activity activity, OustAuthData authData, OustModuleData oustModuleData) {
        OustSdkApplication.setmContext(activity);
        //OustLauncher.getInstance().addOustModuleCallBack(this);
        this.oustApiListener = (OustApiListener) activity;
        new OustModuleDataHandler(activity, authData, oustModuleData);
    }

    @Override
    public void onStartDownloadingResourses() {
        if (oustApiListener != null) {
            oustApiListener.onStartDownloadingResourses();
        }
    }

    @Override
    public void onProgressChanged(int i) {
        if (oustApiListener != null) {
            oustApiListener.onProgressChanged(i);
        }
    }

    @Override
    public void onError(String s) {
        if (oustApiListener != null) {
            oustApiListener.onError(s);
        }
    }

    @Override
    public void onLoginError(String s) {
        if (oustApiListener != null) {
            oustApiListener.onLoginError(s);
        }
    }

    @Override
    public void onLoginProcessStart() {
        if (oustApiListener != null) {
            oustApiListener.onLoginProcessStart();
        }
    }

    @Override
    public void onLoginSuccess(boolean b) {
        if (oustApiListener != null) {
            oustApiListener.onLoginSuccess(b);
        }
    }

    @Override
    public void onNetworkError() {
        if (oustApiListener != null) {
            oustApiListener.onNetworkError();
        }
    }

    @Override
    public void onOustLoginStatus(String message) {

    }

    @Override
    public void onOustContentLoaded() {

    }

    @Override
    public void onModuleComplete(Moduledata moduledata) {
        if (oustApiListener != null) {
            oustApiListener.onModuleComplete( OustModuleData.getModuleData(moduledata));
        }
    }

    @Override
    public void onModuleProgress(Moduledata moduledata, int i) {
        if (oustApiListener != null) {
            oustApiListener.onModuleProgress( OustModuleData.getModuleData(moduledata),i);
        }
    }

    @Override
    public void onModuleStatusChange(Moduledata moduledata, String s) {
        if (oustApiListener != null) {
            oustApiListener.onModuleStatusChange( OustModuleData.getModuleData(moduledata),s);
        }
    }

    @Override
    public void onModuleFailed(Moduledata moduledata) {
        if (oustApiListener != null) {
            oustApiListener.onModuleFailed( OustModuleData.getModuleData(moduledata));
        }
    }

}
