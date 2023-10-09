package com.oustme.oustsdk.notification.repository;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.notification.NotificationActivity;
import com.oustme.oustsdk.notification.model.NotificationComponentModel;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.notification.model.RemoveNotificationRequest;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NotificationRepository {
    private static NotificationRepository instance;
    private MutableLiveData<NotificationComponentModel> liveData;
    private NotificationComponentModel notificationComponentModel;
    private ArrayList<NotificationResponse> viewModelHashMap;
    private ArrayList<NotificationResponse> responseArrayList = new ArrayList<>();
    private ArrayList<NotificationResponse> tempResponseArrayList = new ArrayList<>();
    private final ArrayList<NotificationResponse> notExistingDataFromRoomDb = new ArrayList<>();
    private NotificationActivity view;
    private ActiveUser activeUser;

    public static NotificationRepository getInstance() {
        if (instance == null)
            instance = new NotificationRepository();
        return instance;
    }

    public MutableLiveData<NotificationComponentModel> showNotificationRepository(NotificationActivity notificationActivity) {
        this.view = notificationActivity;
        try {
            liveData = new MutableLiveData<>();
            fetchBundleData();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        return liveData;
    }

    private void fetchBundleData() {
        try {
            activeUser = OustAppState.getInstance().getActiveUser();

            if (activeUser == null) {
                if (OustPreferences.get("userdata") != null && !OustPreferences.get("userdata").isEmpty()) {
                    activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
                }
            }

            if (activeUser != null) {
                notificationComponentModel = new NotificationComponentModel();
                viewModelHashMap = new ArrayList<>();
                getNotificationsFromFireBase();
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void getNotificationsFromFireBase() {

        String message = "/landingPage/" + activeUser.getStudentKey() + "/pushNotifications";
        Log.e("TAG", "getNotificationsFromFireBase: " + message);
        ValueEventListener notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null) {
                        if (snapshot.getValue() instanceof ArrayList) {
                            final ArrayList<Object> notificationListData = (ArrayList<Object>) snapshot.getValue();
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            if (notificationListData != null && notificationListData.size() != 0) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String clubkey = snapshot1.getKey() + "_" + activeUser.getStudentKey();

                                    final Object notificationData = snapshot1.getValue();
                                    Gson gson = new Gson();
                                    JsonElement notificationElement = gson.toJsonTree(notificationData);
                                    NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);

                                    if (notificationResponse != null) {
                                        if (notificationResponse.getNotificationId() != null) {
                                            String keyAndId = notificationResponse.getNotificationId() + "_" + activeUser.getStudentKey();
                                            notificationResponse.setKey(keyAndId);
                                            notificationResponse.setNotificationKey(snapshot1.getKey());
                                        } else {
                                            notificationResponse.setKey(clubkey);
                                            notificationResponse.setNotificationKey(snapshot1.getKey());
                                        }
                                        notificationResponse.setFireBase(true);
                                        if (notificationResponse.getRead() != null) {
                                            notificationResponse.setRead(notificationResponse.getRead());
                                        } else {
                                            notificationResponse.setRead(true);
                                        }
                                        notificationResponse.setStatus("dataFound");
                                        notificationResponse.setUserId(activeUser.getStudentKey());
                                        notificationResponseList.add(notificationResponse);
                                    }
                                }
                            }

                            if (OustAppState.getInstance().getActiveUser() != null) {
                                Gson gson = new Gson();
                                OustPreferences.save("userdata", gson.toJson(OustAppState.getInstance().getActiveUser()));
                                if (notificationResponseList.size() > 0) {
                                    notExistingDataFromRoomDb.clear();
                                    responseArrayList.clear();
                                    tempResponseArrayList.clear();
                                    responseArrayList = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                                    if (responseArrayList.size() > 0) {
                                        for (int i = 0; i < notificationResponseList.size(); i++) {
                                            Boolean checkData = RoomHelper.getNotificationById(notificationResponseList.get(i).getKey());
                                            if (!checkData) {
                                                notExistingDataFromRoomDb.add(notificationResponseList.get(i));
                                            }
                                        }
                                        if (notExistingDataFromRoomDb.size() > 0) {
                                            RoomHelper.addNotificationData(notExistingDataFromRoomDb);
                                        }
                                    } else {
                                        RoomHelper.addNotificationData(notificationResponseList);
                                    }
                                }
                                viewModelHashMap = notificationResponseList;
                                notificationComponentModel.setActiveUser(activeUser);
                                notificationComponentModel.setNotificationResponseHashMap(notificationResponseList);
                                liveData.postValue(notificationComponentModel);
                            }
                        } else if (snapshot.getValue() instanceof HashMap) {
                            final HashMap<Long, Object> notificationListData = (HashMap<Long, Object>) snapshot.getValue();
                            ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                            if (notificationListData != null && notificationListData.size() != 0) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String clubkey = snapshot1.getKey() + "_" + activeUser.getStudentKey();

                                    final Object notificationData = snapshot1.getValue();
                                    Gson gson = new Gson();
                                    JsonElement notificationElement = gson.toJsonTree(notificationData);
                                    NotificationResponse notificationResponse = gson.fromJson(notificationElement, NotificationResponse.class);

                                    if (notificationResponse != null) {
                                        notificationResponse.setKey(clubkey);
                                        notificationResponse.setNotificationKey(snapshot1.getKey());
                                        notificationResponse.setFireBase(true);
                                        if (notificationResponse.getRead() != null) {
                                            notificationResponse.setRead(notificationResponse.getRead());
                                        } else {
                                            notificationResponse.setRead(true);
                                        }
                                        notificationResponse.setStatus("dataFound");
                                        notificationResponse.setUserId(activeUser.getStudentKey());
                                        notificationResponseList.add(notificationResponse);
                                    }
                                }
                            }

                            if (OustAppState.getInstance().getActiveUser() != null) {
                                Gson gson = new Gson();
                                OustPreferences.save("userdata", gson.toJson(OustAppState.getInstance().getActiveUser()));
                                if (notificationResponseList.size() > 0) {
                                    notExistingDataFromRoomDb.clear();
                                    responseArrayList.clear();
                                    tempResponseArrayList.clear();
                                    responseArrayList = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                                    if (responseArrayList.size() > 0) {
                                        for (int i = 0; i < notificationResponseList.size(); i++) {
                                            Boolean checkData = RoomHelper.getNotificationById(notificationResponseList.get(i).getKey());
                                            if (!checkData) {
                                                Log.d("TAG", "onDataChange: key-if-> " + notificationResponseList.get(i).getKey());
                                                notExistingDataFromRoomDb.add(notificationResponseList.get(i));
                                            }
                                        }
                                        responseArrayList = (ArrayList<NotificationResponse>) RoomHelper.getNotificationsData(activeUser.getStudentKey());
                                        Collections.reverse(responseArrayList);
                                        tempResponseArrayList = responseArrayList;
                                        if (notificationResponseList.size() > 0 && responseArrayList.size() > 0) {
                                            for (int l = 0; l < notificationResponseList.size(); l++) {
                                                for (int m = 0; m < responseArrayList.size(); m++) {
                                                    if (notificationResponseList.get(l).getKey().equalsIgnoreCase(responseArrayList.get(m).getKey())) {
                                                        tempResponseArrayList.remove(m);
                                                        break;
                                                    }
                                                }
                                            }
                                            for (NotificationResponse notificationResponse : tempResponseArrayList) {
                                                if (notificationResponse.getKey() != null) {
                                                    RoomHelper.deleteOfflineNotifications(notificationResponse.getContentId());
                                                }
                                            }
                                        }

                                        if (notExistingDataFromRoomDb.size() > 0) {
                                            RoomHelper.addNotificationData(notExistingDataFromRoomDb);
                                        }
                                    } else {
                                        RoomHelper.addNotificationData(notificationResponseList);
                                    }
                                }
                                viewModelHashMap = notificationResponseList;
                                notificationComponentModel.setActiveUser(activeUser);
                                notificationComponentModel.setNotificationResponseHashMap(notificationResponseList);
                                liveData.postValue(notificationComponentModel);
                            }
                        }
                    } else {
                        NotificationResponse notificationResponse = new NotificationResponse();
                        notificationResponse.setStatus("noData");
                        ArrayList<NotificationResponse> notificationResponseList = new ArrayList<>();
                        notificationResponseList.add(notificationResponse);
                        notificationComponentModel.setNotificationResponseHashMap(notificationResponseList);
                        liveData.postValue(notificationComponentModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTools", "onCancelled: " + message);
            }
        };
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
        OustFirebaseTools.getRootRef().child(message).orderByChild("updateTime").addValueEventListener(notificationListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(notificationListener, message));
    }

    public void removeNotificationRepository(ArrayList<NotificationResponse> removeNotificationFromFireBase) {
        Log.e("TAG", "removeNotificationRepository: " + activeUser.getStudentid());
        RemoveNotificationRequest removeNotificationRequest = new RemoveNotificationRequest();
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < removeNotificationFromFireBase.size(); i++) {
            if (removeNotificationFromFireBase.get(i).getNotificationKey() != null) {
                ids.add(removeNotificationFromFireBase.get(i).getNotificationKey());
            }
        }
        removeNotificationRequest.setNotificationIdList(ids);
        removeNotificationRequest.setUserId(activeUser.getStudentid());

        try {
            if (OustSdkTools.checkInternetStatus()) {
                String notificationRemoveUrl = OustSdkApplication.getContext().getResources().getString(R.string.remove_fireBase_notification_api);
                notificationRemoveUrl = HttpManager.getAbsoluteUrl(notificationRemoveUrl);
                Gson gson = new GsonBuilder().create();
                String jsonParams = gson.toJson(removeNotificationRequest);

                ApiCallUtils.doNetworkCall(Request.Method.POST, notificationRemoveUrl, OustSdkTools.getRequestObject(jsonParams), new ApiCallUtils.NetworkCallback() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optBoolean("success")) {
                            Log.d("TAG", "onResponse: successfully RemoveNotification");
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

            } else {
                OustSdkTools.showToast(OustSdkApplication.getContext().getResources().getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
