package com.oustme.oustsdk.tools;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.service.NetworkChangeReceiver;

import io.branch.referral.Branch;
import io.sentry.Sentry;
import io.sentry.android.core.SentryAndroid;

public class OustSdkApplication extends MultiDexApplication {
    private static Context mContext;
    private static OustSdkApplication mInstance;
    private RequestQueue mRequestQueue;
    private static Tracker mTracker;

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();

        SentryAndroid.init(this);
//        Sentry.captureException(new Exception("SDK Exception"));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MultiDex.install(this);
        Branch.getAutoInstance(this);
    }

    public static void setmInstance(OustSdkApplication mInstance) {
        OustSdkApplication.mInstance = mInstance;
    }

    public static synchronized OustSdkApplication getInstance() {
        return mInstance;
    }

    public static synchronized Context getContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        OustSdkApplication.mContext = mContext;
        mInstance = new OustSdkApplication();
        Branch.getAutoInstance(mContext);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    synchronized public static Tracker getDefaultTracker(Context activity) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity.getApplicationContext());
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(activity.getResources().getString(R.string.ga_trackingId));
        }
        return mTracker;
    }

    public void setConnectivityListener(NetworkChangeReceiver.ConnectivityReceiverListener listener) {
        NetworkChangeReceiver.connectivityReceiverListener = listener;
    }
}
