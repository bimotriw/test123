package com.oustme.oustsdk.interfaces.common;

import org.json.JSONObject;

/**
 * Created by ItsMe on 6/8/16.
 */
public interface ServerResponseListener {
    void onSuccess(JSONObject jsonObject);

    void onSuccess(JSONObject jsonObject, int requestType);

    void onError(String error);
}
