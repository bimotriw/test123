package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkApplication;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class OustRequest {
    private String appVersion;

    public String getAppVersion() {
        return OustSdkApplication.getContext().getResources().getString(R.string.app_version);
    }

}
