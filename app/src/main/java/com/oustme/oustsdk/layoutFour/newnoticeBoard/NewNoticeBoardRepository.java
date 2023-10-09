package com.oustme.oustsdk.layoutFour.newnoticeBoard;

import android.content.ClipData;
import android.os.Handler;
import android.util.Log;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewGetAllMainPostDataTask;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.data.handlers.NewGetAllPostDataTask;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNbPost;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewPostUpdateData;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.OustStaticVariableHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewNoticeBoardRepository {

    private static final String TAG = "NewNoticeBoardReposi";
    private static NewNoticeBoardRepository instance;
    private MutableLiveData<ArrayList<NewNBTopicData>> liveData;
    MutableLiveData<List<NewNBPostData>> livePostData;
    List<NewNBTopicData> finalNbTopicList;
    private MutableLiveData<List<NewNbPost>> liveData2;
    private ActiveUser activeUser;
    private HashMap<String, NewNBTopicData> nbTopicDataHashMap;
    private HashMap<String, NewPostUpdateData> nbTopicDataHashMap1;

    int nbTopicDataSizeVal = 0;
    int nbTopicPostSize = 0;
    List<NewNBPostData> nbPostDataTempList = new ArrayList<>();

    private int totalNBTopicCount = 0;

    public static NewNoticeBoardRepository getInstance() {
        if (instance == null)
            instance = new NewNoticeBoardRepository();
        return instance;
    }

    public MutableLiveData<ArrayList<NewNBTopicData>> getNBTopicList() {
        liveData = new MutableLiveData<>();
        fetchNoticeBoardData();
        Log.d("NewNoticeBoardView2", "");
        return liveData;

    }

    public MutableLiveData<List<NewNBPostData>> getNBPostList() {
        livePostData = new MutableLiveData<>();
        return livePostData;
    }

    protected boolean isNotNull(Object o) {
        return o != null;
    }

    protected boolean isNull(Object o) {
        return o == null;
    }

    public void getAllPostsData(List<NewNBTopicData> nbTopicData1) {
        try {
            if (nbTopicData1 != null) {
                for (int i = 0; i < nbTopicData1.size(); i++) {
                    Log.d("NBTopicDetailedP300-->", "" + nbTopicData1.get(i).getPostUpdateDataSize());
                    if (isNull(nbTopicData1.get(i)) || nbTopicData1.get(i).getPostUpdateDataSize() == 0) {
                        try {
                            try {
                                if (OustSdkApplication.getContext().getString(R.string.nb_no_posts_msg) != null) {
                                    // no data found
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    } else {
                        try {
                            OustStaticVariableHandling.getInstance().setNbPostSize(nbTopicData1.get(i).getPostUpdateDataSize());
                            Log.d("NBTopicDetailedSizeP-->", "" + nbTopicData1.get(i).getPostUpdateDataSize());
                        } catch (Exception e) {
                            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                        }
                    }
                    nbTopicPostSize = nbTopicPostSize + nbTopicData1.get(i).getPostUpdateDataSize();
                    new NewGetAllMainPostDataTask(this).execute(nbTopicData1.get(i));
                    Log.d("NBTopicDetailed30P-->", "" + nbTopicData1.get(i));
                    Log.d("NBTopicDetailed30KP-->", "" + nbTopicPostSize);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void fetchNoticeBoardData() {
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        getUserNBTopicData();
//        getNoticeBoardPosts();
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
                                Log.d(TAG, "learningList: " + learningList.size());
                                for (int i = 0; i < learningList.size(); i++) {
                                    Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    Log.d(TAG, "learningListItems: " + learningList.get(i));
                                    if (lpMap != null) {
                                        NewNBTopicData nbTopicData = new NewNBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                try {
                                    if (nbTopicDataHashMap == null) {
                                        nbTopicDataHashMap = new HashMap<>();
                                    }
                                    nbTopicDataHashMap.clear();
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }
                                for (String nBKey : lpMainMap.keySet()) {
                                    Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(nBKey);
                                    if (lpMap != null) {
                                        NewNBTopicData nbTopicData = new NewNBTopicData();
                                        nbTopicData.init(lpMap);
                                        updateNBDataMap(nbTopicData);
                                    }
                                }
                            }
                            getAllNBTopicData();
                        } else {
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
            Query query = gameHistoryRef.orderByChild("assignedOn");
            query.keepSynced(true);
            query.addValueEventListener(noticeBoardListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(noticeBoardListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    private void updateNBDataMap(NewNBTopicData nbTopicData) {
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
                        Log.d("nbtopicData2Message-->", "" + message);
                        ValueEventListener myassessmentListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.getValue() != null) {
                                        totalNBTopicCount++;
                                        Object o1 = dataSnapshot.getValue();
                                        Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                        if (lpMainMap.containsKey("nbId")) {
                                            long id = OustSdkTools.convertToLong(lpMainMap.get("nbId"));
                                            if (nbTopicDataHashMap.containsKey(id + "")) {
                                                NewNBTopicData nbTopicData = nbTopicDataHashMap.get("" + id);
                                                if (nbTopicData != null) {
                                                    nbTopicDataHashMap.put("" + id, nbTopicData.setExtraNBData(nbTopicData, lpMainMap));
                                                }
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
                            public void onCancelled(@NonNull DatabaseError error) {
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

    public Comparator<NewNBTopicData> nbTopicDataComparator = new Comparator<NewNBTopicData>() {
        public int compare(NewNBTopicData s1, NewNBTopicData s2) {
            return Long.valueOf(s2.getAssignedOn()).compareTo(Long.valueOf(s1.getAssignedOn()));
        }
    };

    private void createToalNbData() {
        if (totalNBTopicCount >= nbTopicDataHashMap.size()) {
            ArrayList<NewNBTopicData> nbTopicDataArrayList = new ArrayList<NewNBTopicData>(nbTopicDataHashMap.values());
            if (nbTopicDataArrayList != null && nbTopicDataArrayList.size() > 0) {
                Collections.sort(nbTopicDataArrayList, nbTopicDataComparator);
            }
            liveData.postValue(nbTopicDataArrayList);
            finalNbTopicList = nbTopicDataArrayList;
            Log.d(TAG, "increaseAFV:P" + nbTopicDataArrayList.size());
            Log.d(TAG, "increaseAFV1:P" + finalNbTopicList.size());
//            getNBPostList();
        }
    }

    public void gotAllPostData(List<NewNBPostData> postDataList) {
        nbPostDataTempList.addAll(postDataList);
        nbTopicDataSizeVal = nbTopicDataSizeVal + postDataList.size();
        Log.d(TAG, "increaseBP" + nbTopicPostSize);
        Log.d(TAG, "increaseB:P" + nbTopicDataSizeVal);
        Log.d(TAG, "increaseSize0:P" + postDataList.size());
        Log.d(TAG, "increaseSize:P" + nbPostDataTempList.size());
        if (nbTopicDataSizeVal == nbTopicPostSize) {
            createTotalNbPostData(nbPostDataTempList);
            Log.d(TAG, "increaseA:P" + nbTopicPostSize);
            Log.d(TAG, "increaseTopic:P" + nbTopicDataSizeVal);
            nbTopicPostSize = 0;
            nbTopicDataSizeVal = 0;
            Log.d(TAG, "increaseAF:P" + nbTopicPostSize);
            Log.d(TAG, "increaseTopicF:P" + nbTopicDataSizeVal);
        }
    }

    public Comparator<NewNBPostData> nbPostDataComparator = new Comparator<NewNBPostData>() {
        public int compare(NewNBPostData s1, NewNBPostData s2) {
            return Long.valueOf(s2.getCreatedOn()).compareTo(Long.valueOf(s1.getCreatedOn()));
        }
    };

    private void createTotalNbPostData(List<NewNBPostData> nbPostDataTempList) {
        try {
            List<NewNBPostData> nbPostsDataArrayList = new ArrayList<NewNBPostData>(nbPostDataTempList);
            if (nbPostsDataArrayList != null && nbPostsDataArrayList.size() > 0) {
                Log.d(TAG, "increaseAConverting1P:" + nbTopicPostSize);
                Collections.sort(nbPostsDataArrayList, nbPostDataComparator);
                Log.d(TAG, "increaseAConverting2P:" + nbTopicPostSize);
            }
            livePostData.postValue(nbPostsDataArrayList);
            Log.d(TAG, "increaseAConverting3P:" + nbPostsDataArrayList);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
