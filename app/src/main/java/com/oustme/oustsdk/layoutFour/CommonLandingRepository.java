package com.oustme.oustsdk.layoutFour;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oustme.oustsdk.firebase.common.CommonLandingData;
import com.oustme.oustsdk.firebase.common.FirebaseRefClass;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.layoutFour.data.repoData.CommonLanding;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.CommonTools;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonLandingRepository {


    private static final String TAG = "LandingLayoutRyoutepository";
    private static CommonLandingRepository instance;
    private MutableLiveData<CommonLanding> liveData;
    private ActiveUser activeUser;
    private CommonLanding commonLanding;
    private HashMap<String, String> commonInfoMap;
    private HashMap<String, CommonLandingData> commonModuleMap;
    private long pendingCount,coins;

    /* private boolean isCPLShowOnCourse;
     */


    public static CommonLandingRepository getInstance() {
        if (instance == null)
            instance = new CommonLandingRepository();
        return instance;
    }

    public MutableLiveData<CommonLanding> getCommonLandingData() {
        liveData = new MutableLiveData<>();
        fetchCommonLandingData();
        return liveData;
    }

    private void fetchCommonLandingData() {
        activeUser = OustSdkTools.getActiveUserData(OustPreferences.get("userdata"));
        commonInfoMap = new HashMap<>();
        commonModuleMap = new HashMap<>();
        commonLanding = new CommonLanding();
        getUserCourses();
     /*   getUserAssessments();
        getCplData();*/
    }

    public void getUserCourses() {
        Log.e(TAG, "inside getUserCourses() ");
        try {
            final String courseLandingNode = "/landingPage/" + activeUser.getStudentKey() + "/course";
            ValueEventListener courseListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {


                            Object courseLandingObject = dataSnapshot.getValue();

                            if (courseLandingObject.getClass().equals(ArrayList.class)) {

                                ArrayList<Object> courseModuleArrayList = (ArrayList<Object>) courseLandingObject;

                                if (courseModuleArrayList.size() != 0) {

                                    for (int i = 0; i < courseModuleArrayList.size(); i++) {

                                        final Map<String, Object> courseMap = (Map<String, Object>) courseModuleArrayList.get(i);
                                        if (courseMap != null) {
                                            CommonTools commonTools = new CommonTools();
                                            CommonLandingData courseModule = new CommonLandingData();
                                            courseModule = commonTools.getCourseLandingData(courseMap, courseModule);
                                            final String courseBaseNode = "/course/course" + courseModule.getModuleId();
                                            CommonLandingData finalCourseModule = courseModule;
                                            ValueEventListener courseListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        if (dataSnapshot.getValue() != null) {
                                                            CommonTools commonTools = new CommonTools();
                                                            CommonLandingData courseChildModule = new CommonLandingData();
                                                            courseChildModule = commonTools.getCourseCommonData(courseMap, courseChildModule);
                                                            if (courseChildModule != null) {
                                                                String id = "COURSE" + courseChildModule.getModuleId();
                                                                commonInfoMap.put(id, "COURSE");
                                                                CommonLandingData mergeModule = CommonLandingData.mergeObjects(courseChildModule, finalCourseModule);
                                                                if (mergeModule.getMode() != null && mergeModule.getMode().equalsIgnoreCase("ARCHIVED")) {
                                                                    commonModuleMap.remove(id);

                                                                } else {
                                                                    if ((mergeModule.getCompletionPercentage() < 100) &&
                                                                            mergeModule.getCompletionDeadline() != null &&
                                                                            !mergeModule.getCompletionDeadline().isEmpty() &&
                                                                            !mergeModule.getCompletionDeadline().equals("0")) {
                                                                        long completionDeadline = Long.parseLong(mergeModule.getCompletionDeadline());
                                                                        long currentTime = System.currentTimeMillis();
                                                                        if (completionDeadline >= currentTime) {
                                                                            commonModuleMap.put(id, mergeModule);
                                                                            pendingCount++;
                                                                        } else {
                                                                            if (mergeModule.isShowPastDeadlineModulesOnLandingPage()) {

                                                                                commonModuleMap.put(id, mergeModule);
                                                                                pendingCount++;
                                                                            } else {
                                                                                commonModuleMap.remove(id);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        commonModuleMap.put(id, mergeModule);
                                                                        if(mergeModule.getCompletionPercentage()<100){
                                                                            pendingCount++;
                                                                        }else{
                                                                            coins+=mergeModule.getUserOc();
                                                                        }
                                                                    }

                                                                }


                                                            }

                                                        } else {
                                                            String removeId = "COURSE" + finalCourseModule.getModuleId();
                                                            commonInfoMap.remove(removeId);


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
                                            DatabaseReference courseBaseRef = OustFirebaseTools.getRootRef().child(courseBaseNode);
                                            courseBaseRef.keepSynced(true);
                                            courseBaseRef.addListenerForSingleValueEvent(courseListener);
                                            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(courseListener, courseBaseNode));
                                        }

                                    }

                                    commonLanding.setCommonInfoMap(commonInfoMap);
                                    commonLanding.setCommonModuleMap(commonModuleMap);
                                    commonLanding.setPendingCount(pendingCount);
                                    commonLanding.setCoin(coins);
                                    liveData.postValue(commonLanding);


                                }
                            }else if (courseLandingObject.getClass().equals(HashMap.class)) {

                                Map<String,Object> courseModuleArrayList = (Map<String,Object>) courseLandingObject;

                                if (courseModuleArrayList.size() != 0) {

                                    for (String key : courseModuleArrayList.keySet()) {

                                        final Map<String, Object> courseMap = (Map<String, Object>) courseModuleArrayList.get(key);
                                        if (courseMap != null) {
                                            CommonTools commonTools = new CommonTools();
                                            CommonLandingData courseModule = new CommonLandingData();
                                            courseModule = commonTools.getCourseLandingData(courseMap, courseModule);
                                            final String courseBaseNode = "/course/course" + courseModule.getModuleId();
                                            CommonLandingData finalCourseModule = courseModule;
                                            ValueEventListener courseListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        if (dataSnapshot.getValue() != null) {
                                                            CommonTools commonTools = new CommonTools();
                                                            CommonLandingData courseChildModule = new CommonLandingData();
                                                            courseChildModule = commonTools.getCourseCommonData(courseMap, courseChildModule);
                                                            if (courseChildModule != null) {
                                                                String id = "COURSE" + courseChildModule.getModuleId();
                                                                commonInfoMap.put(id, "COURSE");
                                                                CommonLandingData mergeModule = CommonLandingData.mergeObjects(courseChildModule, finalCourseModule);
                                                                if (mergeModule.getMode() != null && mergeModule.getMode().equalsIgnoreCase("ARCHIVED")) {
                                                                    commonModuleMap.remove(id);

                                                                } else {
                                                                    if ((mergeModule.getCompletionPercentage() < 100) &&
                                                                            mergeModule.getCompletionDeadline() != null &&
                                                                            !mergeModule.getCompletionDeadline().isEmpty() &&
                                                                            !mergeModule.getCompletionDeadline().equals("0")) {
                                                                        long completionDeadline = Long.parseLong(mergeModule.getCompletionDeadline());
                                                                        long currentTime = System.currentTimeMillis();
                                                                        if (completionDeadline >= currentTime) {
                                                                            commonModuleMap.put(id, mergeModule);
                                                                            pendingCount++;
                                                                        } else {
                                                                            if (mergeModule.isShowPastDeadlineModulesOnLandingPage()) {

                                                                                commonModuleMap.put(id, mergeModule);
                                                                                pendingCount++;
                                                                            } else {
                                                                                commonModuleMap.remove(id);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        commonModuleMap.put(id, mergeModule);
                                                                        if(mergeModule.getCompletionPercentage()<100){
                                                                            pendingCount++;
                                                                        }else{
                                                                            coins+=mergeModule.getUserOc();
                                                                        }
                                                                    }

                                                                }


                                                            }

                                                        } else {
                                                            String removeId = "COURSE" + finalCourseModule.getModuleId();
                                                            commonInfoMap.remove(removeId);


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
                                            DatabaseReference courseBaseRef = OustFirebaseTools.getRootRef().child(courseBaseNode);
                                            courseBaseRef.keepSynced(true);
                                            courseBaseRef.addListenerForSingleValueEvent(courseListener);
                                            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(courseListener, courseBaseNode));
                                        }

                                    }

                                    commonLanding.setCommonInfoMap(commonInfoMap);
                                    commonLanding.setCommonModuleMap(commonModuleMap);
                                    commonLanding.setPendingCount(pendingCount);
                                    commonLanding.setCoin(coins);
                                    liveData.postValue(commonLanding);


                                }
                            }


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
            DatabaseReference landingCourseRef = OustFirebaseTools.getRootRef().child(courseLandingNode);
            Query query = landingCourseRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(courseListener);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(courseListener, courseLandingNode));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    /*private void getCplData() {
        Log.e(TAG, "inside get cplData() ");
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/cpl";
            Log.e(TAG, "getCplData: " + message);
            ValueEventListener cplDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    extractCplData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            addToFireBaseRefList(message, cplDataListener);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void extractCplData(DataSnapshot dataSnapshot) {
        Log.d(TAG, "extractCplData: ");
        isCPLShowOnCourse = OustPreferences.getAppInstallVariable(AppConstants.StringConstants.SHOW_CPL_COURSE_TAB);
        CommonLandingData CPLcommonLandingData;
        CplCollectionData cplCollectionData = null;
        if (dataSnapshot.getValue() != null) {
            CPLcommonLandingData = new CommonLandingData();
            try {
                Object o1 = dataSnapshot.getValue();
                if (o1.getClass().equals(ArrayList.class)) {
                    List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                    cplCollectionData = new CplCollectionData(learningList);
                } else {

                    Map<String, Object> lpMainMap = new HashMap<>();
                    lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (lpMainMap != null) {
                        cplCollectionData = new CplCollectionData(lpMainMap);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                Log.d(TAG, "extractCplData: " + e.getLocalizedMessage());
            }

            try {
                if (isCPLShowOnCourse) {
                    if (cplCollectionData.getBanner() != null)
                        CPLcommonLandingData.setBanner(cplCollectionData.getBanner());
                    CPLcommonLandingData.setId("CPL" + cplCollectionData.getCplId());
                    CPLcommonLandingData.setType("CPL");
                    CPLcommonLandingData.setIcon(cplCollectionData.getIntroIcon());
                    CPLcommonLandingData.setName(cplCollectionData.getCplName());
                    CPLcommonLandingData.setEnrollCount(cplCollectionData.getEnrolledCount());
                    CPLcommonLandingData.setDescription(cplCollectionData.getCplDescription());
                    CPLcommonLandingData.setOc(cplCollectionData.getOustCoins());
                    CPLcommonLandingData.setUserOc(cplCollectionData.getTotalCoins());
                    CPLcommonLandingData.setAddedOn(cplCollectionData.getAssignedDate() + "");
                    CPLcommonLandingData.setCompletionPercentage(cplCollectionData.getProgress());
                    myDeskData.put("CPL" + CPLcommonLandingData.getCplId(), CPLcommonLandingData);

                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
    }*/

    private void addToFireBaseRefList(String message, ValueEventListener eventListener) {
        OustFirebaseTools.getRootRef().child(message).addValueEventListener(eventListener);
        OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(eventListener, message));
        OustFirebaseTools.getRootRef().child(message).keepSynced(true);
    }
/*
    public void getUserAssessments() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/assessment";
            Log.d(TAG, "getUserAssessments: " + message);
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        int assesmentcount = 0;
                        int tot = (int) dataSnapshot.getChildrenCount();

                        int pend = tot - assesmentcount;
                        if (dataSnapshot.getValue() != null) {
                            //mAssessmentCompletedList = new ArrayList<>();
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> assessmentMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : assessmentMainMap.keySet()) {
                                    if ((assessmentMainMap.get(courseKey) != null)) {
                                        final Map<String, Object> assessmentMap = (Map<String, Object>) assessmentMainMap.get(courseKey);
                                        if (assessmentMap != null) {
                                            if (assessmentMap.get("elementId") != null) {
                                                String id = "ASSESSMENT" + courseKey;
                                                int remainder = id.length() % 2;
                                                myDeskInfoMap.put(id, "ASSESSMENT");
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));

                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(assessmentMap, commonLandingData);
                                                    commonLandingData.setType("ASSESSMENT");
                                                    commonLandingData.setId(id);
                                                    if (assessmentMap != null && assessmentMap.containsKey("completionDate")) {
                                                        if (assessmentMap.get("completionDate").getClass().equals(String.class)) {
                                                            long completePercentage = Long.parseLong((String) assessmentMap.get("completionDate"));
                                                            if (completePercentage != 0) {
                                                                commonLandingData.setCompletionPercentage(100);
                                                            }
                                                        }
                                                    }
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "onDataChange: either hashmap nor arraylist");
                            }

                        }
                        liveData.setValue(myDeskInfoMap);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("ERROR", "" + error.getDetails());
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            FirebaseRefClass assessmentFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void getUserCoursesCollections() {
        try {
            final String message = "/landingPage/" + activeUser.getStudentKey() + "/courseColn";
            Log.d(TAG, "getUserCoursesCollections: " + message);
            ValueEventListener myassessmentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        //  Log.d(TAG, "onDataChange: UserCoursesCollections:"+dataSnapshot.getChildrenCount());
                        if (dataSnapshot.getValue() != null) {
                            Object o1 = dataSnapshot.getValue();
                            CommonTools commonTools = new CommonTools();
                            if (o1.getClass().equals(ArrayList.class)) {
                                List<Object> learningList = (List<Object>) dataSnapshot.getValue();
                                for (int i = 0; i < learningList.size(); i++) {
                                    final Map<String, Object> lpMap = (Map<String, Object>) learningList.get(i);
                                    if (lpMap != null) {
                                        if (lpMap.get("elementId") != null) {
                                            String id = "COLLECTION" + i;
                                            myDeskInfoMap.put(id, "COLLECTION");
                                            if (myDeskData.get(id) != null) {
                                                CommonLandingData commonLandingData = myDeskData.get(id);
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COLLECTION");
                                                commonLandingData.setId(id);
                                                myDeskData.put(id, commonLandingData);
                                            } else {
                                                CommonLandingData commonLandingData = new CommonLandingData();
                                                commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                commonLandingData.setType("COLLECTION");
                                                commonLandingData.setId(id);
                                                myDeskData.put(id, commonLandingData);
                                            }
                                        }
                                    }
                                }
                            } else if (o1.getClass().equals(HashMap.class)) {
                                Map<String, Object> lpMainMap = (Map<String, Object>) dataSnapshot.getValue();
                                for (String courseKey : lpMainMap.keySet()) {
                                    if ((lpMainMap.get(courseKey) != null)) {
                                        final Map<String, Object> lpMap = (Map<String, Object>) lpMainMap.get(courseKey);
                                        if (lpMap != null) {
                                            if (lpMap.get("elementId") != null) {
                                                String id = "COLLECTION" + courseKey;
                                                myDeskInfoMap.put(id, "COLLECTION");
                                                if (myDeskData.get(id) != null) {
                                                    CommonLandingData commonLandingData = myDeskData.get(id);
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                    myDeskData.put(id, commonLandingData);
                                                } else {
                                                    CommonLandingData commonLandingData = new CommonLandingData();
                                                    commonTools.getCourseLandingData(lpMap, commonLandingData);
                                                    commonLandingData.setType("COLLECTION");
                                                    commonLandingData.setId(id);
                                                    myDeskData.put(id, commonLandingData);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        liveData.setValue(myDeskInfoMap);
                    } catch (Exception e) {
                        e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            };
            DatabaseReference gameHistoryRef = OustFirebaseTools.getRootRef().child(message);
            Query query = gameHistoryRef.orderByChild("addedOn");
            query.keepSynced(true);
            query.addValueEventListener(myassessmentListener);
            FirebaseRefClass courseFirebaseRefClass = new FirebaseRefClass(myassessmentListener, message);
            OustAppState.getInstance().getFirebaseRefClassList().add(new FirebaseRefClass(myassessmentListener, message));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }*/


}
