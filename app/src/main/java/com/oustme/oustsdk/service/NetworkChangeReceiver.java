package com.oustme.oustsdk.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkChangeReceiver.class.getName();
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public NetworkChangeReceiver() {
        super();
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive: for not internet");
        boolean isConnectedToInternet = checkInternet(context);
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnectedToInternet);
        }

    }

    boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}
