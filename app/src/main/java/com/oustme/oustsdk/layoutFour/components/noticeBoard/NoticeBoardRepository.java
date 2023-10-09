package com.oustme.oustsdk.layoutFour.components.noticeBoard;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeBoardRepository {

    private static final String TAG = "NoticeBoardRepository";
    private static NoticeBoardRepository instance;
    private MutableLiveData<List<NBTopicData>> liveData;
    private ActiveUser activeUser;
    private HashMap<String, NBTopicData> nbTopicDataHashMap;
    private int totalNBTopicCount = 0;

    public static NoticeBoardRepository getInstance() {
        if (instance == null)
            instance = new NoticeBoardRepository();
        return instance;
    }

    public MutableLiveData<List<NBTopicData>> getNBTopicList() {
        liveData = new MutableLiveData<>();
        fetchNoticeBoardData();
        return liveData;
    }

    private void fetchNoticeBoardData() {
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        getUserNBTopicData();
    }


    private void getUserNBTopicData() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/noticeBoard";
            ValueEventListener noticeBoardListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String nBKey : lpMainMap.keySet()) {
                                    Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(nBKey);
                                    if (lpMap != null) {
                                        NBTopicData nbTopicData = new NBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            }
                            getAllNBTopicData();
                        }else{
                            liveData.postValue(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(noticeBoardListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(noticeBoardListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void updateNBDataMap(NBTopicData nbTopicData) {
        if (nbTopicDataHashMap == null)
            nbTopicDataHashMap = new HashMap<>();
        if (nbTopicDataHashMap.containsKey("" + nbTopicData.getId())) {
            nbTopicData.setKeySet(nbTopicDataHashMap.get("" + nbTopicData.getId()).isKeySet());
        } else {
            nbTopicDataHashMap.put(nbTopicData.getId() + "", nbTopicData);
        }
    }

    private void getAllNBTopicData() {
        if (nbTopicDataHashMap != null && nbTopicDataHashMap.size() > 0) {
            for (String key : nbTopicDataHashMap.keySet()) {
                if (!nbTopicDataHashMap.get(key).isKeySet()) {
                    nbTopicDataHashMap.get(key).setKeySet(true);
                    try {
                        final String message = "/noticeBoard/noticeBoard" + nbTopicDataHashMap.get(key).getId();
                        ValueEventListener myassessmentListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.getValue() != null) {
                                        totalNBTopicCount++;
                                        Object o1 = dataSnapshot.getValue();
                                        Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                        if (lpMainMap.containsKey("nbId")) {
                                            long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                            if (nbTopicDataHashMap.containsKey(id + "")) {
                                                NBTopicData nbTopicData = nbTopicDataHashMap.get("" + id);
                                                nbTopicDataHashMap.put("" + id, nbTopicData.setExtraNBData(nbTopicData, lpMainMap));
                                            }
                                        }
                                        createToalNbData();
                                    } else {
                                        totalNBTopicCount++;
                                        createToalNbData();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                totalNBTopicCount++;
                                createToalNbData();
                            }
                        };
                        DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
                        Query query = gameHistoryRef.orderByChild("addedOn");
                        query.keepSynced(true);
                        query.addValueEventListener(myassessmentListener);
                        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                } else {
                    totalNBTopicCount++;
                    createToalNbData();
                }
            }
        } else {
            createToalNbData();
        }

    }


    public Comparator<NBTopicData> nbTopicDataComparator = new Comparator<NBTopicData>() {
        public int compare(NBTopicData s1, NBTopicData s2) {
            return Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));
        }
    };

    private void createToalNbData() {
        if (totalNBTopicCount >= nbTopicDataHashMap.size()) {
            List<NBTopicData> nbTopicDataArrayList = new ArrayList<NBTopicData>(nbTopicDataHashMap.values());
            if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                Collections.sort(nbTopicDataArrayList, nbTopicDataComparator);
            }
            liveData.postValue(nbTopicDataArrayList);
        }
    }
}
