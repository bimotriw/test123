package com.oustme.oustsdk.work_diary;


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
import com.google.gson.reflect.TypeToken;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.model.response.diary.MediaList;
import com.oustme.oustsdk.tools.ActiveUser;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;
import com.oustme.oustsdk.work_diary.model.WorkDiaryComponentModule;
import com.oustme.oustsdk.work_diary.model.WorkDiaryDetailsModel;
import com.oustme.oustsdk.work_diary.model.WorkDiaryFilterModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WorkDiaryRepository {

    private static final String TAG = "WorkDiaryRepository";
    private static WorkDiaryRepository instance;
    private MutableLiveData<WorkDiaryComponentModule> liveData;
    private WorkDiaryComponentModule workDiaryComponentModule;

    ActiveUser activeUser;
    private long latestEntryInMill = 0;
    private boolean isMultipleCplEnable = false;
    private static final long MILLI_SEC_PER_DAY = 86400000;

    private ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList;
    private ArrayList<WorkDiaryDetailsModel> workDiaryDetailsForManualList;
    private ArrayList<WorkDiaryDetailsModel> workDiaryDetailsForModuleList;
    private ArrayList<WorkDiaryDetailsModel> workDiaryDetailsForAll;
    private HashMap<String, HashMap<String, ArrayList<WorkDiaryDetailsModel>>> workDiaryBaseHashMap = new HashMap<>();
    private HashMap<String, ArrayList<WorkDiaryDetailsModel>> workDiaryHashMap = new HashMap<>();
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());


    private WorkDiaryRepository() {
    }

    public static WorkDiaryRepository getInstance() {
        if (instance == null)
            instance = new WorkDiaryRepository();
        return instance;
    }

    public MutableLiveData<WorkDiaryComponentModule> getLiveData(Bundle bundle, boolean isMultipleCpl) {
        liveData = new MutableLiveData<>();
        this.isMultipleCplEnable = isMultipleCpl;
        fetchBundleData(bundle);
        return liveData;
    }

    private void fetchBundleData(Bundle dataBundle) {

        if (dataBundle != null) {
            Log.d(TAG, "No Bundle");
        }
        activeUser = OustAppState.getInstance().getActiveUser();
        try {
            OustFirebaseTools.initFirebase();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
        if (activeUser != null) {
            workDiaryComponentModule = new WorkDiaryComponentModule();
            workDiaryBaseHashMap = new HashMap<>();
            workDiaryHashMap = new HashMap<>();
            if (activeUser.getRegisterDateTime() != 0) {
                latestEntryInMill = activeUser.getRegisterDateTime();
            } else {
                getRegisterDateTime();
            }
            getFiltersFromFirebase();
        }
    }

    public void getRegisterDateTime() {
        String node = "users/" + activeUser.getStudentKey() + "/registerDateTime";
        OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        activeUser.setRegisterDateTime((long) dataSnapshot.getValue());
                        latestEntryInMill = (long) dataSnapshot.getValue();
                        if (OustAppState.getInstance().getActiveUser() != null) {
                            OustAppState.getInstance().getActiveUser().setRegisterDateTime((long) dataSnapshot.getValue());
                            OustPreferences.save("userdata", new Gson().toJson(OustAppState.getInstance().getActiveUser()));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFiltersFromFirebase() {
        ArrayList<WorkDiaryFilterModel> workDiaryFilterModelArrayList = new ArrayList<>();
        String node = "system/featureLayoutInfo";
        OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        WorkDiaryFilterModel workDiaryFilterModel = childData.getValue(WorkDiaryFilterModel.class);
                        if (workDiaryFilterModel != null) {
                            workDiaryFilterModelArrayList.add(workDiaryFilterModel);
                        }
                    }
                    successFilterData(workDiaryFilterModelArrayList);
                } else {
                    successFilterData(workDiaryFilterModelArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                successFilterData(workDiaryFilterModelArrayList);
            }
        });
    }

    public void successFilterData(ArrayList<WorkDiaryFilterModel> workDiaryFilterModelList) {
        try {
            workDiaryComponentModule.setWorkDiaryFilterModelArrayList(workDiaryFilterModelList);
            if (activeUser != null) {
                if (activeUser.getStudentid() != null && activeUser.getStudentKey() != null) {
                    getManualDataFromFireBase();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

    }

    public void getManualDataFromFireBase() {
        String node = "landingPage/" + activeUser.getStudentKey() + "/learningDiary";
        OustFirebaseTools.getRootRef().child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workDiaryDetailsModelArrayList = new ArrayList<>();
                try {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot childDataSnap : dataSnapshot.getChildren()) {
                            WorkDiaryDetailsModel workDiaryDetailsModel = new WorkDiaryDetailsModel();
                            Map<String, Object> map = (Map<String, Object>) childDataSnap.getValue();
                            if (map != null) {
                                if (map.get("endTS") != null) {
                                    workDiaryDetailsModel.setActivityName((String) map.get("activityName"));
                                    workDiaryDetailsModel.setEndTS((String) map.get("endTS"));
                                    workDiaryDetailsModel.setStartTS((String) map.get("startTS"));
                                    workDiaryDetailsModel.setComments((String) map.get("comments"));
                                    workDiaryDetailsModel.setType(2);
                                    workDiaryDetailsModel.setDataType("MY ENTRY");
                                    workDiaryDetailsModel.setDisplayType(OustSdkApplication.getContext().getResources().getString(R.string.my_entry));
                                    workDiaryDetailsModel.setUserLD_Id("" + map.get("userLD_Id"));
                                    workDiaryDetailsModel.setApprovalStatus("" + map.get("approvalStatus"));
                                    List<MediaList> mediaLists = new ArrayList<>();
                                    Object o1 = map.get("learningDiaryMediaDataList");
                                    if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                                        List<Object> levelsList = (List<Object>) map.get("learningDiaryMediaDataList");
                                        for (int i = 0; i < Objects.requireNonNull(levelsList).size(); i++) {
                                            if (levelsList.get(i) != null) {
                                                final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                                MediaList mediaList = new MediaList();
                                                mediaList.setFileName((String) levelMap.get("fileName"));
                                                mediaList.setFileSize((String) levelMap.get("fileSize"));
                                                mediaList.setFileType((String) levelMap.get("fileType"));
                                                mediaList.setUserLdMedia_Id((long) levelMap.get("userLdMedia_Id"));
                                                mediaLists.add(mediaList);
                                            }
                                        }
                                    }
                                    workDiaryDetailsModel.setLearningDiaryMediaDataList(mediaLists);
                                    if (mediaLists.size() > 0) {
                                        workDiaryDetailsModel.setBanner(AppConstants.StringConstants.S3_BUCKET_IMAGE_URL + mediaLists.get(0).getFileName());
                                    }
                                    workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                    storeDataForFilter(workDiaryDetailsModel);

                                }
                            }
                        }

                        if (workDiaryDetailsModelArrayList != null && workDiaryDetailsModelArrayList.size() != 0) {
                            updateData(workDiaryDetailsModelArrayList, 2);
                        }

                        if (OustSdkTools.checkInternetStatus()) {
                            getModuleDataFromAPI();
                        } else {
                            workDiaryBaseHashMap = new HashMap<>();
                            String workDiaryBaseHashMapString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryBaseHashMap");
                            if (workDiaryBaseHashMapString != null && !workDiaryBaseHashMapString.isEmpty()) {
                                try {
                                    workDiaryBaseHashMap = new Gson().fromJson(workDiaryBaseHashMapString, HashMap.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }

                            }

                            String workDiaryDetailsModelArrayListString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryDetailsModelArrayList");
                            if (workDiaryDetailsModelArrayListString != null && !workDiaryDetailsModelArrayListString.isEmpty()) {
                                try {
                                    workDiaryDetailsModelArrayList = new Gson().fromJson(workDiaryDetailsModelArrayListString, ArrayList.class);
                                    updateData(workDiaryDetailsModelArrayList, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }

                            }
                        }
                    } else {
                        if (OustSdkTools.checkInternetStatus()) {
                            getModuleDataFromAPI();
                        } else {
                            workDiaryBaseHashMap = new HashMap<>();
                            String workDiaryBaseHashMapString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryBaseHashMap");
                            if (workDiaryBaseHashMapString != null && !workDiaryBaseHashMapString.isEmpty()) {
                                try {
                                    workDiaryBaseHashMap = new Gson().fromJson(workDiaryBaseHashMapString, HashMap.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }

                            }

                            String workDiaryDetailsModelArrayListString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryDetailsModelArrayList");
                            if (workDiaryDetailsModelArrayListString != null && !workDiaryDetailsModelArrayListString.isEmpty()) {
                                try {
                                    workDiaryDetailsModelArrayList = new Gson().fromJson(workDiaryDetailsModelArrayListString, new TypeToken<List<WorkDiaryDetailsModel>>() {
                                    }.getType());
                                    updateData(workDiaryDetailsModelArrayList, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                    if (OustSdkTools.checkInternetStatus()) {
                        getModuleDataFromAPI();
                    } else {
                        workDiaryBaseHashMap = new HashMap<>();
                        String workDiaryBaseHashMapString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryBaseHashMap");
                        if (workDiaryBaseHashMapString != null && !workDiaryBaseHashMapString.isEmpty()) {
                            try {
                                workDiaryBaseHashMap = new Gson().fromJson(workDiaryBaseHashMapString, HashMap.class);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                            }

                        }

                        String workDiaryDetailsModelArrayListString = OustPreferences.get(activeUser.getStudentKey() + "_workDiaryDetailsModelArrayList");
                        if (workDiaryDetailsModelArrayListString != null && !workDiaryDetailsModelArrayListString.isEmpty()) {
                            try {
                                workDiaryDetailsModelArrayList = new Gson().fromJson(workDiaryDetailsModelArrayListString, new TypeToken<List<WorkDiaryDetailsModel>>() {
                                }.getType());
                                updateData(workDiaryDetailsModelArrayList, 1);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            OustSdkTools.sendSentryException(e1);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //handle error message
            }
        });
        OustFirebaseTools.getRootRef().child(node).keepSynced(true);
    }

    public void updateData(ArrayList<WorkDiaryDetailsModel> workDiaryDetailsModelArrayList, int type) {
        if (type == 1) {
            workDiaryDetailsForModuleList = new ArrayList<>();
            workDiaryDetailsForAll = new ArrayList<>();
            workDiaryDetailsForModuleList.addAll(workDiaryDetailsModelArrayList);
        } else if (type == 2) {
            workDiaryDetailsForManualList = new ArrayList<>();
            workDiaryDetailsForManualList.addAll(workDiaryDetailsModelArrayList);
        }

        if (workDiaryDetailsForModuleList != null && workDiaryDetailsForModuleList.size() > 0) {
            workDiaryDetailsForAll = new ArrayList<>();
            workDiaryDetailsForAll.addAll(workDiaryDetailsForModuleList);
        }

        if (workDiaryDetailsForManualList != null && workDiaryDetailsForAll != null) {
            Collections.sort(workDiaryDetailsForManualList, WorkDiaryDetailsModel.workDiaryDetailsModelComparator);

            for (int i = 0; i < workDiaryDetailsForManualList.size(); i++) {
                if (activeUser.getRegisterDateTime() != 0 && activeUser.getRegisterDateTime() > Long.parseLong(workDiaryDetailsForManualList.get(0).getEndTS())) {
                    latestEntryInMill = activeUser.getRegisterDateTime();
                } else if (latestEntryInMill != 0 && latestEntryInMill > Long.parseLong(workDiaryDetailsForManualList.get(0).getEndTS())) {
                    Log.d(TAG, "Latest " + latestEntryInMill);
                } else {
                    latestEntryInMill = Long.parseLong(workDiaryDetailsForManualList.get(0).getEndTS()) + MILLI_SEC_PER_DAY;
                }

                for (int j = 0; j < workDiaryDetailsForAll.size(); j++) {
                    if (workDiaryDetailsForManualList.get(i).getUserLD_Id().equalsIgnoreCase(workDiaryDetailsForAll.get(j).getUserLD_Id())) {
                        workDiaryDetailsForAll.remove(j);
                    }
                }
            }
        }
        if (workDiaryDetailsForManualList != null && workDiaryDetailsForManualList.size() > 0) {
            if (workDiaryDetailsForAll == null)
                workDiaryDetailsForAll = new ArrayList<>();
            workDiaryDetailsForAll.addAll(workDiaryDetailsForManualList);
            Collections.sort(workDiaryDetailsForManualList, WorkDiaryDetailsModel.workDiaryDetailsModelComparator);
            workDiaryComponentModule.setWorkDiaryDetailsManualArrayList(workDiaryDetailsForManualList);
        }

        if (workDiaryDetailsForAll != null && workDiaryDetailsForAll.size() != 0) {

            Collections.sort(workDiaryDetailsForAll, WorkDiaryDetailsModel.workDiaryDetailsModelComparator);
            workDiaryComponentModule.setWorkDiaryDetailsModelArrayList(workDiaryDetailsForAll);
            workDiaryComponentModule.setWorkDiaryBaseHashMap(workDiaryBaseHashMap);

        } else {
            workDiaryComponentModule.setWorkDiaryDetailsModelArrayList(new ArrayList<>());
            workDiaryComponentModule.setWorkDiaryBaseHashMap(new HashMap<>());
        }
        workDiaryComponentModule.setActiveUser(activeUser);
        workDiaryComponentModule.setLatestEntryInMill(latestEntryInMill);
        liveData.postValue(workDiaryComponentModule);
    }

    public void getModuleDataFromAPI() {
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.friendprofilefetch_url);
        getPointsUrl = getPointsUrl.replace("{studentId}", activeUser.getStudentid());
        getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);
        Log.e(TAG, "getModuleDataFromAPI:getPointsUrl-->  " + getPointsUrl);

        ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                extractDataForModule(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //handle error
            }
        });
    }

    private void extractDataForModule(JSONObject response) {
        workDiaryDetailsModelArrayList = new ArrayList<>();
        try {
            if (response != null) {
                if (response.optBoolean("success")) {
                    JSONArray courseArray = response.optJSONArray("courses");
                    if (courseArray != null) {
                        for (int i = 0; i < courseArray.length(); i++) {
                            JSONObject j = courseArray.optJSONObject(i);
                            if (j != null) {
                                WorkDiaryDetailsModel workDiaryDetailsModel = new WorkDiaryDetailsModel();
                                if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty()
                                        && !j.optString("userCompletionTime").equals("null")) {
                                    workDiaryDetailsModel.setEndTS(j.optString("userCompletionTime"));
                                    workDiaryDetailsModel.setType(1);
                                    workDiaryDetailsModel.setActivityName(j.optString("courseName"));
                                    workDiaryDetailsModel.setBanner(j.optString("icon"));
                                    workDiaryDetailsModel.setAttachedAssessmentId(j.optString("attachedAssessmentId"));
                                    workDiaryDetailsModel.setApprovalStatus("" + OustSdkApplication.getContext().getResources().getString(R.string.completed));
                                    workDiaryDetailsModel.setUserLD_Id("cs" + j.optString("courseId"));
                                    workDiaryDetailsModel.setXp(j.optLong("xp", 0));
                                    workDiaryDetailsModel.setTotalOc(j.optLong("totalOc", 0));
                                    workDiaryDetailsModel.setRewardOC(j.optLong("rewardOC", 0));
                                    workDiaryDetailsModel.setCoins(j.optLong("coins", 0));
                                    workDiaryDetailsModel.setDataType("COURSE");
                                    workDiaryDetailsModel.setCplId(j.optInt("cplId"));
                                    workDiaryDetailsModel.setDisplayType("" + OustSdkApplication.getContext().getResources().getString(R.string.course_text));
                                    workDiaryDetailsModel.setLearningDiaryID("" + j.optInt("courseId"));
                                    workDiaryDetailsModel.setContentDuration(j.optDouble("contentDuration"));
                                    if (j.optString("mode") != null) {
                                        workDiaryDetailsModel.setMode(j.optString("mode"));
                                    }
                                    if (isMultipleCplEnable) {
                                        if (workDiaryDetailsModel.getCplId() == 0) {
                                            workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                            storeDataForFilter(workDiaryDetailsModel);
                                        }
                                    } else {
                                        workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                        storeDataForFilter(workDiaryDetailsModel);
                                    }
                                }
                            }
                        }
                    }
                    JSONArray assessmentArray = response.optJSONArray("assessments");

                    if (assessmentArray != null) {
                        for (int i = 0; i < assessmentArray.length(); i++) {
                            JSONObject j = assessmentArray.optJSONObject(i);
                            if (j != null) {
                                WorkDiaryDetailsModel workDiaryDetailsModel = new WorkDiaryDetailsModel();
                                if (j.optString("userCompletionTime") != null
                                        && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                    workDiaryDetailsModel.setEndTS(j.optString("userCompletionTime"));
                                    workDiaryDetailsModel.setType(1);
                                    workDiaryDetailsModel.setActivityName(j.optString("name"));
                                    workDiaryDetailsModel.setAttachedCourseId(j.optString("attachedCourseId"));
                                    workDiaryDetailsModel.setBanner(j.optString("logo"));
                                    workDiaryDetailsModel.setUserLD_Id("as" + j.optString("assessmentId"));
                                    workDiaryDetailsModel.setTotalOc(j.optLong("totalOc", 0));
                                    workDiaryDetailsModel.setQuestionXp(j.optLong("questionXp", 0));
                                    workDiaryDetailsModel.setRewardOC(j.optLong("rewardOC", 0));
                                    workDiaryDetailsModel.setDataType("ASSESSMENT");
                                    workDiaryDetailsModel.setCplId(j.optInt("cplId"));
                                    workDiaryDetailsModel.setContentDuration(j.optDouble("contentDuration"));
                                    workDiaryDetailsModel.setDisplayType("" + OustSdkApplication.getContext().getResources().getString(R.string.assessment));

                                    if (j.has("showAssessmentResultScore")) {
                                        workDiaryDetailsModel.setShowAssessmentResultScore(j.optBoolean("showAssessmentResultScore"));
                                        if (workDiaryDetailsModel.isShowAssessmentResultScore()) {
                                            if (j.has("passed")) {
                                                workDiaryDetailsModel.setPassed(j.optBoolean("passed"));
                                                if (workDiaryDetailsModel.isPassed()) {
                                                    workDiaryDetailsModel.setApprovalStatus(OustSdkApplication.getContext().getResources().getString(R.string.passed) + "");
                                                } else {
                                                    workDiaryDetailsModel.setApprovalStatus(OustSdkApplication.getContext().getResources().getString(R.string.failed_text) + "");
                                                }
                                            } else {
                                                workDiaryDetailsModel.setApprovalStatus(OustSdkApplication.getContext().getResources().getString(R.string.passed) + "");
                                                workDiaryDetailsModel.setPassed(true);
                                            }
                                        } else {
                                            workDiaryDetailsModel.setApprovalStatus(OustSdkApplication.getContext().getResources().getString(R.string.failed_text) + "");
                                        }
                                    } else {
                                        workDiaryDetailsModel.setApprovalStatus(OustSdkApplication.getContext().getResources().getString(R.string.completed) + "");
                                        workDiaryDetailsModel.setPassed(true);
                                    }


                                    workDiaryDetailsModel.setCoins(j.optLong("assessmentScore", 0));
                                    workDiaryDetailsModel.setLearningDiaryID("" + j.optInt("assessmentId"));

                                    if (isMultipleCplEnable) {
                                        if (workDiaryDetailsModel.getCplId() == 0) {
                                            workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                            storeDataForFilter(workDiaryDetailsModel);
                                        }
                                    } else {
                                        workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                        storeDataForFilter(workDiaryDetailsModel);
                                    }
                                }
                            }
                        }
                    }

                    JSONArray surveyArray = response.optJSONArray("surveys");
                    if (surveyArray != null) {
                        for (int i = 0; i < surveyArray.length(); i++) {
                            JSONObject j = surveyArray.optJSONObject(i);
                            if (j != null) {
                                WorkDiaryDetailsModel workDiaryDetailsModel = new WorkDiaryDetailsModel();
                                if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                    workDiaryDetailsModel.setEndTS(j.optString("userCompletionTime"));
                                    workDiaryDetailsModel.setType(1);
                                    workDiaryDetailsModel.setActivityName(j.optString("name"));
                                    workDiaryDetailsModel.setBanner(j.optString("logo"));
                                    workDiaryDetailsModel.setUserLD_Id("as" + j.optString("assessmentId"));
                                    workDiaryDetailsModel.setTotalOc(j.optLong("totalOc", 0));
                                    workDiaryDetailsModel.setQuestionXp(j.optLong("questionXp", 0));
                                    workDiaryDetailsModel.setRewardOC(j.optLong("rewardOC", 0));
                                    workDiaryDetailsModel.setMappedCourseId(j.optLong("mappedCourseId", 0));
                                    workDiaryDetailsModel.setApprovalStatus("" + OustSdkApplication.getContext().getResources().getString(R.string.completed));
                                    workDiaryDetailsModel.setDataType("SURVEY");
                                    workDiaryDetailsModel.setCplId(j.optInt("cplId"));
                                    workDiaryDetailsModel.setDisplayType("" + OustSdkApplication.getContext().getResources().getString(R.string.survey_text));
                                    workDiaryDetailsModel.setPassed(true);
                                    workDiaryDetailsModel.setContentDuration(j.optDouble("contentDuration"));
                                    workDiaryDetailsModel.setCoins(j.optLong("assessmentScore", 0));
                                    workDiaryDetailsModel.setLearningDiaryID("" + j.optInt("assessmentId"));

                                    if (isMultipleCplEnable) {
                                        if (workDiaryDetailsModel.getCplId() == 0) {
                                            workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                            storeDataForFilter(workDiaryDetailsModel);
                                        }
                                    } else {
                                        workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                        storeDataForFilter(workDiaryDetailsModel);
                                    }
                                }
                            }
                        }
                    }

                    JSONArray cplArray = response.optJSONArray("contentPlayLists");
                    if (cplArray != null) {
                        if (isMultipleCplEnable || cplArray.length() == 1) {
                            for (int i = 0; i < cplArray.length(); i++) {
                                JSONObject j = cplArray.optJSONObject(i);
                                if (j != null) {
                                    WorkDiaryDetailsModel workDiaryDetailsModel = new WorkDiaryDetailsModel();
                                    if (j.optInt("completionTS") != 0) {
                                        workDiaryDetailsModel.setEndTS(j.optString("completionTS"));
                                        workDiaryDetailsModel.setType(1);
                                        workDiaryDetailsModel.setLearningDiaryID("" + j.optInt("cplId"));
                                        workDiaryDetailsModel.setActivityName(j.optString("cplName"));
                                        workDiaryDetailsModel.setBanner(j.optString("banner"));
                                        workDiaryDetailsModel.setThumbnailIcon(j.optString("thumbnailIcon"));
                                        workDiaryDetailsModel.setCompletionPercentage(j.optLong("completionPercentage"));
                                        workDiaryDetailsModel.setEnrollment(j.optString("enrollment"));
                                        workDiaryDetailsModel.setPassed(true);
                                        workDiaryDetailsModel.setCoins(j.optLong("totalCoins", 0));
                                        workDiaryDetailsModel.setUserLD_Id("cpl" + j.optString("cplId"));
                                        workDiaryDetailsModel.setApprovalStatus("" + OustSdkApplication.getContext().getResources().getString(R.string.completed));
                                        workDiaryDetailsModel.setDataType("PLAYLIST");
                                        workDiaryDetailsModel.setDisplayType("" + OustSdkApplication.getContext().getResources().getString(R.string.play_list));

                                        workDiaryDetailsModelArrayList.add(workDiaryDetailsModel);
                                        storeDataForFilter(workDiaryDetailsModel);
                                    }
                                }
                            }
                        }
                    }
                }
                if (workDiaryDetailsModelArrayList != null) {
                    OustPreferences.save(activeUser.getStudentKey() + "_workDiaryDetailsModelArrayList", new Gson().toJson(workDiaryDetailsModelArrayList));
                    updateData(workDiaryDetailsModelArrayList, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    private void storeDataForFilter(WorkDiaryDetailsModel workDiaryDetailsModel) {
        try {
            long filterTime = Long.parseLong(workDiaryDetailsModel.getEndTS());
            Date filterDate = new Date(filterTime);
            String yearKey = yearFormat.format(filterDate);
            String monthKey = monthFormat.format(filterDate);

            if (workDiaryBaseHashMap.size() != 0) {
                if (workDiaryBaseHashMap.get(yearKey) == null) {
                    workDiaryBaseHashMap.put(yearKey, new HashMap<>());
                }
            } else {
                workDiaryBaseHashMap.put(yearKey, new HashMap<>());
            }

            HashMap<String, ArrayList<WorkDiaryDetailsModel>> workDiaryHashMap = new HashMap<>();
            if (workDiaryBaseHashMap.get(yearKey) != null) {
                workDiaryHashMap = workDiaryBaseHashMap.get(yearKey);
                if (workDiaryHashMap == null) {
                    workDiaryHashMap = new HashMap<>();
                }
                if (workDiaryHashMap.size() != 0) {
                    if (workDiaryHashMap.get(monthKey) == null) {
                        workDiaryHashMap.put(monthKey, new ArrayList<>());
                    }

                } else {
                    workDiaryHashMap.put(monthKey, new ArrayList<>());

                }
                Objects.requireNonNull(workDiaryHashMap.get(monthKey)).add(workDiaryDetailsModel);
            }

            workDiaryBaseHashMap.put(yearKey, workDiaryHashMap);
            OustPreferences.save(activeUser.getStudentKey() + "_workDiaryBaseHashMap", new Gson().toJson(workDiaryBaseHashMap));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
