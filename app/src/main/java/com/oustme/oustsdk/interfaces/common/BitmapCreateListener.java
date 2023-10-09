package com.oustme.oustsdk.interfaces.common;

import android.graphics.Bitmap;

/**
 * Created by oust on 9/3/18.
 */

public interface BitmapCreateListener {
    void onBitmapCreated(Bitmap bitmap);
    void onNoBitmapFound();
    void onBitmapSaved(String path);
}
