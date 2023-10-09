package com.oustme.oustsdk.response.common;

import android.util.Log;


import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class ImageChoiceData {
    private static final String TAG = "ImageChoiceData";
    private String imageFileName;
    private String imageData;
    private String imageFileName_CDN_Path;

    public String getImageFileName_CDN_Path() {
        return imageFileName_CDN_Path;
    }

    public void setImageFileName_CDN_Path(String imageFileName_CDN_Path) {
        this.imageFileName_CDN_Path = imageFileName_CDN_Path;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
        //Log.d(TAG, "setImageData: "+imageData);
        setImageFileName_CDN_Path(imageData);
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
