package com.oustme.oustsdk.tools;

/*import android.app.Activity;
import android.util.Log;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import com.oustlabs.oustapp.pojos.common.OustApplication;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;*/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created with IntelliJ IDEA.
 * User: rajatsekhar
 * Date: 03/09/15
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class OustGATools {

    private static OustGATools mInstance=null;

    private OustGATools(){}
    public static OustGATools getInstance(){
        if(mInstance==null){
            mInstance=new OustGATools();
        }
        return mInstance;
    }
    private static final String TAG = OustGATools.class.getName();



    public  void reportPageViewToGoogle(Activity activity, String screenName) {

        Tracker tracker = OustSdkApplication.getDefaultTracker(activity.getApplicationContext());

        // This screen name value will remain set on the tracker and sent with
        // hits until it is set to a new value or to null.
        if(screenName!=null&&(!screenName.isEmpty())) {
            if (OustPreferences.get("tanentid") != null) {
                screenName = OustPreferences.get("tanentid") + "_" + screenName;
            }
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
            GoogleAnalytics.getInstance(activity).dispatchLocalHits();
            Log.d(TAG, "page view " + screenName + " reported to google");
            reportFBAnalytics(activity, screenName);
        }
    }

    private FirebaseAnalytics mFirebaseAnalytics;
    public void reportFBAnalytics(Activity activity, String screenName) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        //mFirebaseAnalytics.setCurrentScreen(activity, screenName, null /* class override */);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, screenName);
        //  mFirebaseAnalytics.logEvent(EventType.LOGIN.name(), params);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

        Log.d(TAG, "reportFBAnalytics: FromFB");
    }
    public  void reportPageViewToGoogle(Context context, String screenName) {

        Tracker tracker = OustSdkApplication.getDefaultTracker(context);

        // This screen name value will remain set on the tracker and sent with
        // hits until it is set to a new value or to null.
        if(screenName!=null&&(!screenName.isEmpty())) {
            if (OustPreferences.get("tanentId") != null) {
                screenName = OustPreferences.get("tanentId") + "_" + screenName;
            }
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
            GoogleAnalytics.getInstance(context).dispatchLocalHits();
            Log.d(TAG, "page view " + screenName + " reported to google");
        }
    }


    public  void reportActionToGoogle(Context activity, String event, String action, String studentid) {
        Tracker tracker = OustSdkApplication.getDefaultTracker(activity);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(event)
                .setAction(action)
                .setLabel(studentid)
                .build());

        Log.d(TAG, "action " + action +   " on " + event + " reported to google");

    }


}
