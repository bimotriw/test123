package com.oustme.oustsdk.feed_ui.tools;

import android.content.Context;

public class OustSdkApplication /*extends MultiDexApplication*/ {

    private static Context mContext;
    private static OustSdkApplication mInstance;

    public static void setmContext(Context mContext) {
        OustSdkApplication.mContext = mContext;
        mInstance = new OustSdkApplication();
        //Branch.getAutoInstance(mContext);
    }

    public static synchronized Context getContext() {

        return mContext;
    }

    public static synchronized OustSdkApplication getInstance() {
        return mInstance;
    }

}
