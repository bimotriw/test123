package com.oustme.oustsdk.firebase.common;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.util.Log;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.activity.common.PopupActivity;
import com.oustme.oustsdk.profile.model.BadgeModel;
import com.oustme.oustsdk.response.common.OustPopupButton;
import com.oustme.oustsdk.response.common.OustPopupCategory;
import com.oustme.oustsdk.response.common.OustPopupType;
import com.oustme.oustsdk.response.common.Popup;
import com.oustme.oustsdk.sqlite.UserCourseScoreDatabaseHandler;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;
import com.oustme.oustsdk.tools.UserDetails;
import com.oustme.oustsdk.tools.UserDetailsApp;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

@Keep
public class FirebaseDataTools {

    UserDetails userDetails = null;

    public void iniitFirebaseListener() {
        loadApiKey();
        checkForAppUpgrade();
        getUserProfileFromFirebase();
        getUserBadgesFromFirebase();
        attachFirebaseRewardPopupListner();
        attachFirebaseGeneralPopupListner();
        attachDatabaseErrorPopupListner();
        fetchCitiesFromFirebase();
        updateUserInfoFromFirebase();
    }

    private void checkForAppUpgrade() {
        String message = "/system/appUpgrade";
        ValueEventListener appConfigListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        Map<String, Object> appUpgradeMap = (Map<String, Object>) snapshot.getValue();
                        if (appUpgradeMap.get("androidVersion") != null) {
                            String appVersion = (String) appUpgradeMap.get("androidVersion");
                            appVersion = appVersion.replace(".", "");
                            int appVersionNo = Integer.parseInt(appVersion);
                            PackageInfo pinfo = OustSdkApplication.getContext().getPackageManager().getPackageInfo(OustSdkApplication.getContext().getPackageName(), 0);
                            String currentAppVersion = pinfo.versionName;
                            currentAppVersion = currentAppVersion.replace(".", "");
                            int currentAppVersionNo = Integer.parseInt(currentAppVersion);
                            if (currentAppVersionNo < appVersionNo) {
                                gotFirebasePopupResponce(((Map<String, Object>) appUpgradeMap.get("popup")), false, true);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(appConfigListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(appConfigListener, message));
    }

    private void loadApiKey() {
        String message = "system/apiKey";
        ValueEventListener apikeyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        final Map<String, Object> apiKeyMap = (Map<String, Object>) snapshot.getValue();
                        OustPreferences.save("oust_apikey", (String) apiKeyMap.get("key"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(apikeyListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(apikeyListener, message));
    }

    public void getUserProfileFromFirebase() {
        Log.d("TAG", "getUserProfileFromFirebase: ");
        if (OustAppState.getInstance().getActiveUser() == null || OustAppState.getInstance().getActiveUser().getStudentKey() == null) {
            String activeUserGet = OustPreferences.get("userdata");
            ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
        }


        try {
            if (OustAppState.getInstance().getActiveUser().getStudentid() != null) {
                String userDetailsApi = OustSdkApplication.getContext().getResources().getString(R.string.userDetails);
                userDetailsApi = userDetailsApi.replace("{studentId}", OustAppState.getInstance().getActiveUser().getStudentid());
                userDetailsApi = HttpManager.getAbsoluteUrl(userDetailsApi);
                Log.d("TAG", "userDetailsApiAPI=: " + userDetailsApi);
                ApiCallUtils.doNetworkCall(Request.Method.GET, userDetailsApi, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new GsonBuilder().create();
                        userDetails = gson.fromJson(response.toString(), UserDetails.class);
                        if (userDetails.getUserData().getAvatar() != null) {
                            OustAppState.getInstance().getActiveUser().setAvatar((String) userDetails.getUserData().getAvatar());
                        }
                        if (userDetails.getUserData().getAge() != 0) {
                            OustAppState.getInstance().getActiveUser().setUserAge((long) userDetails.getUserData().getAge());
                        }
                        if (userDetails.getUserData().getCity() != null && !userDetails.getUserData().getCity().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setUserCity(userDetails.getUserData().getCity());
                        }
                        if (userDetails.getUserData().getCountry() != null && !userDetails.getUserData().getCountry().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setUserCountry(userDetails.getUserData().getCountry());
                        }
                        if (userDetails.getUserData().getEmailId() != null && !userDetails.getUserData().getEmailId().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setEmail(userDetails.getUserData().getEmailId());
                        }
                        if (userDetails.getUserData().getUserGrade() != null) {
                            OustAppState.getInstance().getActiveUser().setUserGrade(userDetails.getUserData().getUserGrade());
                        }
                        if (userDetails.getUserData().getMobile() != null && !userDetails.getUserData().getMobile().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setUserMobile(OustSdkTools.newConvertToLong(userDetails.getUserData().getMobile()));
                        }
                        if (userDetails.getUserData().getSchoolName() != null && !userDetails.getUserData().getSchoolName().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setSchoolName((String) userDetails.getUserData().getSchoolName());
                        }
                        if (userDetails.getUserData().getFname() != null && !userDetails.getUserData().getFname().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setFname(userDetails.getUserData().getFname());
                        }
                        if (userDetails.getUserData().getLname() != null && !userDetails.getUserData().getLname().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setlName(userDetails.getUserData().getLname());
                        }
                        if (userDetails.getUserData().getGoal() != null && !userDetails.getUserData().getGoal().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setGoal(userDetails.getUserData().getGoal());
                        }
                        if (userDetails.getUserData().getDob() != null && !userDetails.getUserData().getDob().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setDob(OustSdkTools.newConvertToLong(userDetails.getUserData().getDob()));
                        }
                        if (userDetails.getUserData().getGender() != null && !userDetails.getUserData().getGender().isEmpty()) {
                            OustAppState.getInstance().getActiveUser().setUserGender(userDetails.getUserData().getGender());
                        }
                        Gson gsonString = new Gson();
                        OustPreferences.save("userdata", gsonString.toJson(OustAppState.getInstance().getActiveUser()));
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("userDetailAPIErro ", "ERROR:: ", error);
                    }
                });
            } else {
                Log.e("userDetailAPI-> ", "ERROR:: activeUserData is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            Log.e("userDetailAPI<- ", "ERROR: ", e);
        }
    }

    public void getUserBadgesFromFirebase() {

        Log.d("TAG", "getUserBadgesFromFirebase: ");
        if (OustAppState.getInstance().getActiveUser() == null || OustAppState.getInstance().getActiveUser().getStudentKey() == null) {
            String activeUserGet = OustPreferences.get("userdata");
            ActiveUser activeUser = OustSdkTools.getActiveUserData(activeUserGet);
            OustAppState.getInstance().setActiveUser(activeUser);
        }


        String message = "/users/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/badges";
        ValueEventListener badgeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {

                        final Map<String, Object> badgeListData = (Map<String, Object>) dataSnapshot.getValue();
                        HashMap<Long, BadgeModel> badgeModelHashMap = new HashMap<>();

                        if (badgeListData != null) {
                            for (String moduleId : badgeListData.keySet()) {

                                final HashMap<String, Object> badgeData = (HashMap<String, Object>) badgeListData.get(moduleId);
                                Gson gson = new Gson();
                                JsonElement badgeElement = gson.toJsonTree(badgeData);
                                BadgeModel badgeModel = gson.fromJson(badgeElement, BadgeModel.class);

                                if (badgeModel != null) {
                                    badgeModelHashMap.put(Long.parseLong(moduleId), badgeModel);
                                }

                            }


                        }

                        if (OustAppState.getInstance().getActiveUser() != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                HashMap<Long, BadgeModel> sortedMap = badgeModelHashMap.entrySet().stream().
                                        sorted(valueComparator).
                                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                (e1, e2) -> e1, LinkedHashMap::new));
                                OustAppState.getInstance().getActiveUser().setBadges(sortedMap);
                            } else {
                                OustAppState.getInstance().getActiveUser().setBadges(badgeModelHashMap);
                            }
                            Gson gson = new Gson();
                            OustPreferences.save("userdata", gson.toJson(OustAppState.getInstance().getActiveUser()));
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                Log.d("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).orderByChild("completedOn").addValueEventListener(badgeListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(badgeListener, message));
    }


    public void attachFirebaseRewardPopupListner() {
        final ValueEventListener rewardPopupDetailListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                System.out.println("dataSnapshot:" + dataSnapshot.getValue());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, Object> popupMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (popupMap == null) {
                                return;
                            }
                            gotFirebasePopupResponce(popupMap, false, false);
                            OustFirebaseTools.getRootRef().child("/popup/" +
                                    OustAppState.getInstance().getActiveUser().getStudentKey() + "/reward/details").setValue(null);
                        } catch (Exception e) {
                        }
                    }
                }, 200);
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                Log.d("FirebaseTools", "onCancelled: attachFirebaseRewardPopupListner");
            }
        };
        String message = "/popup/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/reward/details";
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(rewardPopupDetailListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(rewardPopupDetailListener, message));

    }

    public void attachDatabaseErrorPopupListner() {
        final ValueEventListener errorPopupDetailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                System.out.println("dataSnapshot:" + dataSnapshot.getValue());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Map<String, Object> popupMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (popupMap == null) {
                                return;
                            }
                            gotFirebasePopupResponce(popupMap, true, false);
                            OustFirebaseTools.getRootRef().child("/popup/" +
                                    OustAppState.getInstance().getActiveUser().getStudentKey() + "/error/details").setValue(null);
                        } catch (Exception e) {
                        }
                    }
                }, 200);
            }

            @Override
            public void onCancelled(DatabaseError DatabaseError) {
                Log.d("FirebaseTools", "onCancelled: attachDatabaseErrorPopupListner");
            }
        };
        String message = "/popup/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/error/details";
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(errorPopupDetailsListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(errorPopupDetailsListener, message));
    }

    public void attachFirebaseGeneralPopupListner() {
        final ValueEventListener generalPopupDetailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Map<String, Object> popupMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (popupMap == null) {
                        return;
                    }
                    gotFirebasePopupResponce(popupMap, false, false);
                    OustFirebaseTools.getRootRef().child("/popup/" +
                            OustAppState.getInstance().getActiveUser().getStudentKey() + "/general/details").setValue(null);
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {
                Log.d("FirebaseTools", "onCancelled: attachFirebaseGeneralPopupListner");
            }
        };
        String message = "/popup/" + OustAppState.getInstance().getActiveUser().getStudentKey() + "/general/details";
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(generalPopupDetailsListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(generalPopupDetailsListener, message));
    }

    public void gotFirebasePopupResponce(Map<String, Object> popupMap, boolean isErrorPopup, boolean isModalPopup) {

        try {
            Popup popup = new Popup();
            if (popupMap.get("content") != null) {
                popup.setContent((String) popupMap.get("content"));
            }
            if (popupMap.get("buttons") != null) {
                if (popupMap.get("buttons") instanceof List) {
                    List<Object> list = (List<Object>) popupMap.get("buttons");
                    Map<String, Object> btnMap = (Map<String, Object>) list.get(0);
                    OustPopupButton oustPopupButton = new OustPopupButton();
                    if (btnMap != null) {
                        if (btnMap.get("btnText") != null) {
                            oustPopupButton.setBtnText((String) btnMap.get("btnText"));
                        }
                        if (btnMap.get("btnActionURI") != null) {
                            oustPopupButton.setBtnText((String) btnMap.get("btnActionURI"));
                        }
                        if (btnMap.get("btnActionHttpMethod") != null) {
                            oustPopupButton.setBtnText((String) btnMap.get("btnActionHttpMethod"));
                        }
                        if (btnMap.get("btnActionRequest") != null) {
                            oustPopupButton.setBtnText((String) btnMap.get("btnActionRequest"));
                        }
                    }
                    List<OustPopupButton> btnList = new ArrayList<>();
                    btnList.add(oustPopupButton);
                    popup.setButtons(btnList);
                }
            }
            popup.setErrorPopup(isErrorPopup);
            if (popupMap.get("header") != null) {
                popup.setHeader((String) popupMap.get("header"));
            }
            if (popupMap.get("bgImage") != null) {
                popup.setBgImage((String) popupMap.get("bgImage"));
            }
            if (popupMap.get("category") != null) {
                popup.setCategory((OustPopupCategory.valueOf((String) popupMap.get("category"))));
            }
            if (popupMap.get("type") != null) {
                popup.setType((OustPopupType.valueOf((String) popupMap.get("type"))));
            }
            if (popupMap.get("icon") != null) {
                popup.setIcon((String) popupMap.get("icon"));
            }
            if (popupMap.get("modal") != null) {
                popup.setModal((Boolean) popupMap.get("modal"));
            }
            if (!popup.isModal()) {
                popup.setModal(isModalPopup);
            }
            if (popupMap.get("data") != null) {
                popup.setOustPopupData((Map<String, String>) popupMap.get("data"));
            }
            if (popupMap.get("nButtons") != null) {
                popup.setnButtons(Integer.parseInt(String.valueOf(popupMap.get("nButtons"))));
            }
            if ((popup.getCategory() != null) && (popup.getCategory() == OustPopupCategory.SILENT_ACTION) &&
                    (popup.getType() != null) && (popup.getType() == OustPopupType.COURSE_UNDISTRIBUTE)) {
                if ((popup.getOustPopupData() != null) && (popup.getOustPopupData().get("courseId") != null)) {
                    String s1 = "" + OustAppState.getInstance().getActiveUser().getStudentKey() + "" + popup.getOustPopupData().get("courseId");
                    if ((popup.getOustPopupData().get("courseColnId") != null) && (!popup.getOustPopupData().get("courseColnId").isEmpty())) {
                        s1 = "" + OustAppState.getInstance().getActiveUser().getStudentKey() + "" + popup.getOustPopupData().get("courseColnId") + "" + popup.getOustPopupData().get("courseId");
                    }
                    //int courseUniqNo = Integer.parseInt(s1);
                    long courseUniqNo = Long.parseLong(s1);
                    UserCourseScoreDatabaseHandler userCourseScoreDatabaseHandler = new UserCourseScoreDatabaseHandler();
                    userCourseScoreDatabaseHandler.deleteUserCourseData(courseUniqNo);
                }
            } else {
                if (OustAppState.getInstance().isHasPopup()) {
                    OustAppState.getInstance().pushPopup(popup);
                } else {
                    if (OustStaticVariableHandling.getInstance().isContainerApp()) {
                        OustStaticVariableHandling.getInstance().setOustpopup(popup);
                        Intent intent = new Intent(OustSdkApplication.getContext(), PopupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        OustSdkApplication.getContext().startActivity(intent);
                        if (popup.getType() == OustPopupType.APP_UPGRADE) {
                            OustStaticVariableHandling.getInstance().setForceUpgradePopupVisible(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }
    //============================================================================================================================
    //check for language update

    private void fetchCitiesFromFirebase() {
        String msg4 = "/system/geo/in/city/";
        ValueEventListener cityListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (null != snapshot.getValue()) {
                    Map<String, Object> cityMap = (Map<String, Object>) snapshot.getValue();
                    List<String> cities = new ArrayList<String>(cityMap.keySet());
                    UserBalanceState.getInstance().setCitis(cities);
                } else {
                    UserBalanceState.getInstance().setCitis(Arrays.asList(OustSdkApplication.getContext().getResources().getStringArray(R.array.cities)));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseTools", "onCancelled: " + msg4);
            }
        };
        OustFirebaseTools.getRootRef().child(msg4).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg4).addValueEventListener(cityListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(cityListener, msg4));
    }

    private void updateUserInfoFromFirebase() {
        String message = "/users/" + OustAppState.getInstance().getActiveUser().getStudentKey();
        ValueEventListener xplistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    if (null != snapshot.getValue()) {
                        OustAppState.getInstance().getActiveUser().setXp(snapshot.getValue().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message + "/xp").keepSynced(true);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(xplistener, message + "/xp"));
        OustFirebaseTools.getRootRef().child(message + "/xp").addValueEventListener(xplistener);

        String msg = "/system/userGoal/";
        ValueEventListener goalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (null != snapshot.getValue()) {
                    Map<String, Object> goalMap = (Map<String, Object>) snapshot.getValue();
                    List<String> goals = new ArrayList<String>(goalMap.keySet());
                    UserBalanceState.getInstance().setGoals(goals);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
        OustFirebaseTools.getRootRef().child(msg).keepSynced(true);
        OustFirebaseTools.getRootRef().child(msg).addListenerForSingleValueEvent(goalListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(goalListener, msg));
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
