package com.oustme.oustsdk.layoutFour.components.userOverView;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.UserDetailsApp;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

public class ActiveUserRepository {

    private static ActiveUserRepository instance;
    private ActiveUserModel activeUserModel;
    private MutableLiveData<ActiveUserModel> liveData;
    private UserDetailsApp userDetailsApp;

    public static ActiveUserRepository getInstance() {
        if (instance == null)
            instance = new ActiveUserRepository();
        return instance;
    }

    public MutableLiveData<ActiveUserModel> getActiveUser() {
        liveData = new MutableLiveData<>();
        fetchActiveUser();
        return liveData;
    }

    private void fetchActiveUser() {
        getUserFromFirebase();
    }

    public void getUserFromFirebase() {
        ActiveUser activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        try {
            String userDetailsApi = OustSdkApplication.getContext().getResources().getString(R.string.userDetailsApi);
            userDetailsApi = userDetailsApi.replace("{studentId}", activeUser.getStudentid());
            userDetailsApi = userDetailsApi.replace("{orgId}", OustPreferences.get("tanentid").trim());
            userDetailsApi = HttpManager.getAbsoluteUrl(userDetailsApi);
            Log.d("TAG", "userDetailsApiAPI: " + userDetailsApi);
            ApiCallUtils.doNetworkCall(Request.Method.GET, userDetailsApi, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new GsonBuilder().create();
                    userDetailsApp = gson.fromJson(response.toString(), UserDetailsApp.class);
                    if (userDetailsApp.getUserData().getImageUrl() != null) {
                        activeUser.setAvatar(userDetailsApp.getUserData().getImageUrl());
                        OustAppState.getInstance().getActiveUser().setAvatar(userDetailsApp.getUserData().getImageUrl());
                    }
                    if (userDetailsApp.getUserData().getUsername() != null) {
                        activeUser.setUserDisplayName(userDetailsApp.getUserData().getUsername());
                        OustAppState.getInstance().getActiveUser().setUserDisplayName(userDetailsApp.getUserData().getUsername());
                    }
                    if (userDetailsApp.getUserData().getCertificateCount() != null) {
                        activeUser.setCertificateCount(OustSdkTools.newConvertToLong(userDetailsApp.getUserData().getCertificateCount()));
                    }
                    if (userDetailsApp.getUserData().getBadgesCount() != null) {
                        activeUser.setBadgesCount(OustSdkTools.newConvertToLong(userDetailsApp.getUserData().getBadgesCount()));
                    }
                    Gson gsonUser = new Gson();
                    OustPreferences.save("userdata", gsonUser.toJson(OustAppState.getInstance().getActiveUser()));
                    setLiveData(activeUser);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("userDetailAPI ", "ERROR:: ", error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("userDetailAPI ", "ERROR: ", e);
        }
    }

    private void setLiveData(ActiveUser activeUser) {
        try {
            if (activeUserModel == null)
                activeUserModel = new ActiveUserModel();
            if (activeUser != null) {
                activeUserModel.setUrlAvatar(activeUser.getAvatar());
                activeUserModel.setUserName(activeUser.getUserDisplayName());
                activeUserModel.setBadgeModelHashMap(activeUser.getBadges());
                activeUserModel.setAchievementCount(activeUser.getCertificateCount() + activeUser.getBadgesCount());
            }
            liveData.postValue(activeUserModel);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
