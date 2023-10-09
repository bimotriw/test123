package com.oustme.oustsdk.tools;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.oustme.oustsdk.interfaces.common.BranchCallback;

/**
 * Created by shilpysamaddar on 14/03/17.
 */

public class MyLifeCycleHandler implements Application.ActivityLifecycleCallbacks {
    public static int startedActivities;
    public static int stoppedActivities;
    private static int liveActivities;
    public static BranchCallback branchCallback;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++startedActivities;
        ++liveActivities;
        try {
            if((MyLifeCycleHandler.liveActivities==1)&&(OustStaticVariableHandling.getInstance().isAppActive())){
                if (branchCallback != null) {
                    branchCallback.onAppResumed();
                }
            }
        }catch (Exception e){}
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stoppedActivities;
        --liveActivities;
    }
}

