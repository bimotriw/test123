package com.oustme.oustsdk.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


public class DownLoadingResultReceiver extends ResultReceiver {
    private static final String TAG = "DownLoadingResultReceiv";

    public DownLoadingResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 1:

                break;

            case 2:
                Log.d(TAG, "onReceiveResult: progress:"+resultData.getString("MSG"));
                break;

            case 3:

                break;
        }
        super.onReceiveResult(resultCode, resultData);
    }

}
