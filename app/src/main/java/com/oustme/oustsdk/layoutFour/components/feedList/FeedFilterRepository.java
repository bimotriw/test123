package com.oustme.oustsdk.layoutFour.components.feedList;

import android.content.Context;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class FeedFilterRepository {

    private static final String TAG = "FeedFilterRepository";
    private static FeedFilterRepository instance;
    private MutableLiveData<ArrayList<UserFeedFilters.FeedFilter>> mutableLiveData;
    private Context context;
    UserFeedFilters userFeedFiltersR = new UserFeedFilters();

    public static FeedFilterRepository getInstance() {
        if (instance == null) instance = new FeedFilterRepository();
        return instance;
    }

    public MutableLiveData<ArrayList<UserFeedFilters.FeedFilter>> getFeedFiltersData(Context context) {
        this.context = context;
        mutableLiveData = new MutableLiveData<>();
        fetchFeedFiltersViaAPI();
        return mutableLiveData;
    }

    public void fetchFeedFiltersViaAPI() {
        try {
            String userFeedFilter = OustSdkApplication.getContext().getResources().getString(R.string.userFeedFilters);
            userFeedFilter = HttpManager.getAbsoluteUrl(userFeedFilter);
            ApiCallUtils.doNetworkCall(Request.Method.GET, userFeedFilter, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "onResponse: user feeds:" + response.toString());
                        Gson gson = new Gson();
                        userFeedFiltersR = gson.fromJson(response.toString(), UserFeedFilters.class);
                        ArrayList<UserFeedFilters.FeedFilter> feedLists = userFeedFiltersR.getFeedFilterList();
                        mutableLiveData.postValue(feedLists);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onResponse: Error:FeedFilters: " + error.getMessage());
                    mutableLiveData.postValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            mutableLiveData.postValue(null);
        }
    }
}
