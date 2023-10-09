package com.oustme.oustsdk.tools;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Created by Sharath K
 * This class is dedicated to sharing feed data to other apps
 * Also filtering assessment,course data in @{CommonLandingFilter.java}
 */
public class ThreadPoolProvider {

    private static ThreadPoolProvider mThreadProviderInstance;
    private final ExecutorService mSingleThreadExecutor ;
    private final Handler handler;

    private final ExecutorService mFixedThreadExecutor;
//    private final ExecutorService mCachedThreadExecutor;




    private ThreadPoolProvider() {
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        mFixedThreadExecutor = Executors.newFixedThreadPool(12);
//      mCachedThreadExecutor = Executors.newCachedThreadPool();
    }

    public static ThreadPoolProvider getInstance() {
        if (mThreadProviderInstance == null){
            synchronized (ThreadPoolProvider.class) {
                mThreadProviderInstance = new ThreadPoolProvider();
            }
        }
        return mThreadProviderInstance;
    }



   /* public ExecutorService getCachedThreadExecutor() {
        return mCachedThreadExecutor;
    }*/

    public ExecutorService getFixedThreadExecutor() {
        return mFixedThreadExecutor;
    }

    public ExecutorService getFeedShareSingleThreadExecutor() {
        return mSingleThreadExecutor;
    }

    public Handler getHandler() {
        return handler;
    }

    public void shutDown(){
        if (mSingleThreadExecutor != null ){
            mSingleThreadExecutor.shutdown();
//            mCachedThreadExecutor.shutdown();

        }
        if (mFixedThreadExecutor != null){
            mFixedThreadExecutor.shutdown();
        }
        if (mThreadProviderInstance != null){
            mThreadProviderInstance =null;
        }
    }
}
