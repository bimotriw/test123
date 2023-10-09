package com.oustme.oustsdk.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.profile.model.AchievementsComponentModel;
import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.profile.model.CertificatesResponse;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AchievementsRepository {

    private static AchievementsRepository instance;
    private MutableLiveData<AchievementsComponentModel> liveData;
    private AchievementsComponentModel achievementsComponentModel;
    HashMap<Long, BadgeModel> badgeModelHashMap;
//    private GifImageView gif_loader;

    ActiveUser activeUser;

    AchievementsRepository() {
    }

    public static AchievementsRepository getInstance() {
        if (instance == null)
            instance = new AchievementsRepository();
        return instance;
    }

    public MutableLiveData<AchievementsComponentModel> getLiveData(Bundle bundle) {
//        this.gif_loader = gif_loader;
        liveData = new MutableLiveData<>();
        fetchBundleData(bundle);
        return liveData;
    }

    public MutableLiveData<CertificatesResponse> getCertificatesLiveData(Bundle bundleData, Context context) {
        MutableLiveData<CertificatesResponse> certificateDatumMutableLiveData = new MutableLiveData<>();
        activeUser = OustAppState.getInstance().getActiveUser();
        try {
            if (OustSdkTools.checkInternetStatus()) {
                String settingUrl = OustSdkApplication.getContext().getResources().getString(R.string.user_certificate_api);
                settingUrl = settingUrl.replace("{userId}", activeUser.getStudentid());
//                settingUrl = settingUrl + "?pageNum=" + pageNo;
                settingUrl = HttpManager.getAbsoluteUrl(settingUrl);
                Log.e("TAG", "getCertificatesLiveData: " + settingUrl);
                ApiCallUtils.doNetworkCall(Request.Method.GET, settingUrl, OustSdkTools.getRequestObject(""),
                        new ApiCallUtils.NetworkCallback() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("TAG", "onResponse--: " + response.toString());
//                                gif_loader.setVisibility(View.GONE);
                                try {
                                    CertificatesResponse certificatesResponse = new CertificatesResponse();
                                    if (response.getBoolean("success")) {
                                        Gson gson = new Gson();
                                        certificatesResponse = gson.fromJson(response.toString(), CertificatesResponse.class);
                                    }
                                    //Log.e("TAG", "onResponse: " + certificatesResponse.getError());
                                    certificateDatumMutableLiveData.setValue(certificatesResponse);
                                } catch (JSONException e) {
//                                    gif_loader.setVisibility(View.GONE);
                                    OustSdkTools.showToast(e.getMessage());
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                gif_loader.setVisibility(View.GONE);
                                CertificatesResponse certificatesResponse = new CertificatesResponse();
                                certificatesResponse.setError("error");
                                certificateDatumMutableLiveData.setValue(certificatesResponse);
                                Log.e("TAG", "onErrorResponse: " + error.getLocalizedMessage());
                                OustSdkTools.showToast(error.getMessage());
                            }
                        });

            } else {
//                gif_loader.setVisibility(View.GONE);
                OustSdkTools.showToast(context.getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
//            gif_loader.setVisibility(View.GONE);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return certificateDatumMutableLiveData;
    }

    private void fetchBundleData(Bundle bundle) {
        try {

            if (bundle != null) {
                //in future if required handle bundle or intent data here
            }

            activeUser = OustAppState.getInstance().getActiveUser();

            if (activeUser == null) {
                if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                    activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                }
            }

            if (activeUser != null) {
                achievementsComponentModel = new AchievementsComponentModel();
                badgeModelHashMap = new HashMap<>();

                if (activeUser.getBadges() != null) {
                    badgeModelHashMap = activeUser.getBadges();
                    achievementsComponentModel.setActiveUser(activeUser);
                    achievementsComponentModel.setBadgeModelHashMap(badgeModelHashMap);
                    liveData.postValue(achievementsComponentModel);
//                    gif_loader.setVisibility(View.GONE);
                } else {
                    getUserBadgesFromFirebase();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserBadgesFromFirebase() {
        String message = "/users/" + activeUser.getStudentKey() + "/badges";
        ValueEventListener badgeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Object o1 = dataSnapshot.getValue();
                        if (o1.getClass().equals(ArrayList.class)) {
                            List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                            HashMap<Long, BadgeModel> badgeModelHashMapLocal = new HashMap<>();
                            if (learningList != null) {
                                for (int i = 0; i < learningList.size(); i++) {
                                    if (learningList.get(i) != null) {
                                        HashMap<String, Object> lpMap = (HashMap<String, Object>) learningList.get(i);
                                        if (lpMap != null) {
                                            Gson gson = new Gson();
                                            JsonElement badgeElement = gson.toJsonTree(learningList.get(i));
                                            BadgeModel badgeModel = gson.fromJson(badgeElement, BadgeModel.class);
                                            if (badgeModel != null) {
                                                badgeModelHashMapLocal.put((long) i, badgeModel);
                                            }
                                        }
                                    }
                                }
                                if (OustAppState.getInstance().getActiveUser() != null) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        HashMap<Long, BadgeModel> sortedMap = badgeModelHashMapLocal.entrySet().stream().
                                                sorted(valueComparator).
                                                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                        (e1, e2) -> e1, LinkedHashMap::new));
                                        OustAppState.getInstance().getActiveUser().setBadges(sortedMap);
                                    } else {
                                        OustAppState.getInstance().getActiveUser().setBadges(badgeModelHashMapLocal);
                                    }

                                    Gson gson = new Gson();
                                    OustPreferences.save("userdata", gson.toJson(OustAppState.getInstance().getActiveUser()));
                                }
                                badgeModelHashMap = badgeModelHashMapLocal;
                                achievementsComponentModel.setActiveUser(activeUser);
                                achievementsComponentModel.setBadgeModelHashMap(badgeModelHashMap);
                                liveData.postValue(achievementsComponentModel);
                            }
                        } else if (o1.getClass().equals(HashMap.class)) {
                            final Map<String, Object> badgeListData = (Map<String, Object>) dataSnapshot.getValue();
                            HashMap<Long, BadgeModel> badgeModelHashMapLocal = new HashMap<>();

                            if (badgeListData != null) {
                                for (String moduleId : badgeListData.keySet()) {

                                    final HashMap<String, Object> badgeData = (HashMap<String, Object>) badgeListData.get(moduleId);
                                    Gson gson = new Gson();
                                    JsonElement badgeElement = gson.toJsonTree(badgeData);
                                    BadgeModel badgeModel = gson.fromJson(badgeElement, BadgeModel.class);

                                    if (badgeModel != null) {
                                        badgeModelHashMapLocal.put(Long.parseLong(moduleId), badgeModel);
                                    }
                                }
                            }

                            if (OustAppState.getInstance().getActiveUser() != null) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    HashMap<Long, BadgeModel> sortedMap = badgeModelHashMapLocal.entrySet().stream().
                                            sorted(valueComparator).
                                            collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                    (e1, e2) -> e1, LinkedHashMap::new));
                                    OustAppState.getInstance().getActiveUser().setBadges(sortedMap);
                                } else {
                                    OustAppState.getInstance().getActiveUser().setBadges(badgeModelHashMapLocal);
                                }

                                Gson gson = new Gson();
                                OustPreferences.save("userdata", gson.toJson(OustAppState.getInstance().getActiveUser()));
                            }
                            badgeModelHashMap = badgeModelHashMapLocal;
                            achievementsComponentModel.setActiveUser(activeUser);
                            achievementsComponentModel.setBadgeModelHashMap(badgeModelHashMap);
                            liveData.postValue(achievementsComponentModel);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                Log.e("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).orderByChild("completedOn").addValueEventListener(badgeListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(badgeListener, message));
    }

    public Comparator<Map.Entry<Long, BadgeModel>> valueComparator = (s1, s2) -> {
        if (s1.getValue().getCompletedOnSort() == 0) {
            return -1;
        }
        if (s2.getValue().getCompletedOnSort() == 0) {
            return 1;
        }
        if (s1.getValue().getCompletedOnSort() == s2.getValue().getCompletedOnSort()) {
            return 0;
        }
        return Long.compare(s2.getValue().getCompletedOnSort(), s1.getValue().getCompletedOnSort());
    };
}
