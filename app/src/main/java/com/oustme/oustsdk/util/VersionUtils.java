package com.oustme.oustsdk.util;

import android.os.Build;

public class VersionUtils {
    private int versionNumber;

    public VersionUtils(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean validateVersionNumber(){
        return Build.VERSION.SDK_INT>=this.versionNumber;
    }
}
