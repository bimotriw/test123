package com.oustme.oustsdk.activity.assessments.learningdiary;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.common.OustFirebaseTools;
import com.oustme.oustsdk.model.request.AddLearningDiaryManually;
import com.oustme.oustsdk.model.request.DetailsModel;
import com.oustme.oustsdk.model.request.LearningDiaryMediaDataList;
import com.oustme.oustsdk.model.response.diary.FilterModel;
import com.oustme.oustsdk.room.RoomHelper;
import com.oustme.oustsdk.room.dto.DTODiaryDetailsModel;
import com.oustme.oustsdk.room.dto.DTOLearningDiary;
import com.oustme.oustsdk.room.dto.DTOMediaList;
import com.oustme.oustsdk.tools.HttpManager;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;
import com.oustme.oustsdk.tools.appconstants.AppConstants;
import com.oustme.oustsdk.util.ApiCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningDiaryPresenter {
    private static final String TAG = "LearningDiaryPresenter";
    LearningDiaryView.LDView ldView;
    private List<DTODiaryDetailsModel> diaryDetailsModelList;
    private int totalCertificateCount;
    private List<DTODiaryDetailsModel> ManualEntryList;
    private Map<String, DTODiaryDetailsModel> APIList;
    private DatabaseReference databaseReference;
    private String mManualDiaryPath;
    private String mFilterFBPath;

    public LearningDiaryPresenter(LearningDiaryView.LDView learningDiaryView) {
        this.ldView = learningDiaryView;
    }

    public void getUserContentFromAPI(final String userId, Context context) {
        ldView.showProgressBar(1);
        String getPointsUrl = OustSdkApplication.getContext().getResources().getString(R.string.friendprofilefetch_url);
        getPointsUrl = getPointsUrl.replace("{studentId}", userId);
        getPointsUrl = HttpManager.getAbsoluteUrl(getPointsUrl);

        ApiCallUtils.doNetworkCall(Request.Method.GET, getPointsUrl, OustSdkTools.getRequestObject(""), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResult: " + response.toString());
                ldView.hideProgressBar(1);
                diaryDetailsModelList = new ArrayList<>();
                extractData(response, userId,context);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ldView.onError("Something not good at server");
                ldView.hideProgressBar(1);
            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.GET, getPointsUrl, new HashMap(), null, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                Log.d(TAG, "onResult: " + tResult.toString());
                ldView.hideProgressBar(1);
                diaryDetailsModelList = new ArrayList<>();
                extractData(tResult, userId);
            }

            @Override
            public void onFailure(int mError) {
                ldView.onError("Something not good at server");
                ldView.hideProgressBar(1);
            }
        });*/

    }

    public void extractOfflineData(String uid) {
        if (uid != null || !uid.isEmpty()) {

            try {
                DTOLearningDiary learningDiaryRealmResp1 = RoomHelper.getLearningDiaryById(uid);
                if (learningDiaryRealmResp1 != null) {
                    if (learningDiaryRealmResp1.get_newsList() != null) {
                        ldView.updateDataFromAPI(learningDiaryRealmResp1.get_newsList(), learningDiaryRealmResp1.get_newsList().size(), 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }

    }

    private void extractData(JSONObject response, String uid,Context context) {
        if (response != null) {
            //Log.d(TAG, "extractData: " + response);
            if (response.optBoolean("success")) {
                JSONArray courseArray = response.optJSONArray("courses");
                if (courseArray != null) {
                    for (int i = 0; i < courseArray.length(); i++) {
                        JSONObject j = courseArray.optJSONObject(i);
                        if (j != null) {
                            DTODiaryDetailsModel detailsModel = new DTODiaryDetailsModel();
                            if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                totalCertificateCount++;
                                detailsModel.setEndTS(j.optString("userCompletionTime"));
                                detailsModel.setType(1);
                                detailsModel.setActivity(j.optString("courseName"));
                                detailsModel.setmBanner(j.optString("bgImg"));
                                detailsModel.setApprovalStatus(""+context.getResources().getString(R.string.completed));
                                detailsModel.setUserLD_Id("cs" + j.optString("courseId"));
                                detailsModel.setSortingTime(Long.valueOf(detailsModel.getEndTS()));
                                detailsModel.setXp(j.optInt("xp", 0));
                                detailsModel.setTotalOc(j.optInt("totalOc", 0));
                                detailsModel.setRewardOC(j.optInt("rewardOC", 0));
                                detailsModel.setCoins(j.optInt("coins", 0));

                                detailsModel.setDataType("COURSE");
                                detailsModel.setLearningDiaryID(""+j.optInt("courseId"));
                                if(j.optString("mode")!=null){
                                    detailsModel.setMode(j.optString("mode"));
                                }
                                diaryDetailsModelList.add(detailsModel);
                            }
                        }
                    }
                }
                JSONArray assessmentArray = response.optJSONArray("assessments");
                if (assessmentArray != null) {
                    for (int i = 0; i < assessmentArray.length(); i++) {
                        JSONObject j = assessmentArray.optJSONObject(i);
                        if (j != null) {
                            DTODiaryDetailsModel detailsModel = new DTODiaryDetailsModel();
                            if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                totalCertificateCount++;
                                // Log.d(TAG, "extractData: AssessmenTime:"+j.optString("userCompletionTime"));
                                // Log.d(TAG, "extractData: AssessmentName:"+j.optString("name"));
                                detailsModel.setEndTS(j.optString("userCompletionTime"));
                                detailsModel.setType(1);
                                detailsModel.setActivity(j.optString("name"));
                                detailsModel.setmBanner(j.optString("banner"));
                                detailsModel.setUserLD_Id("as" + j.optString("assessmentId"));
                                detailsModel.setSortingTime(Long.valueOf(detailsModel.getEndTS()));
                                detailsModel.setTotalOc(j.optInt("totalOc", 0));
                                detailsModel.setQuestionXp(j.optInt("questionXp", 0));
                                detailsModel.setRewardOC(j.optInt("rewardOC", 0));

                                detailsModel.setDataType("ASSESSMENT");

                                if(j.has("passed")) {
                                    detailsModel.setPassed(j.optBoolean("passed"));
                                    if (detailsModel.isPassed()) {
                                        detailsModel.setApprovalStatus(context.getResources().getString(R.string.passed)+"");
                                    } else {
                                        detailsModel.setApprovalStatus(context.getResources().getString(R.string.failed_text)+"");
                                    }
                                }else{
                                    detailsModel.setApprovalStatus(context.getResources().getString(R.string.passed)+"");
                                    detailsModel.setPassed(true);
                                }

                                detailsModel.setCoins(j.optInt("assessmentScore", 0));
                                detailsModel.setLearningDiaryID(""+j.optInt("assessmentId"));

                                diaryDetailsModelList.add(detailsModel);
                            }
                        }
                    }
                }

                JSONArray surveyArray = response.optJSONArray("surveys");
                if (surveyArray != null) {
                    for (int i = 0; i < surveyArray.length(); i++) {
                        JSONObject j = surveyArray.optJSONObject(i);
                        if (j != null) {
                            DTODiaryDetailsModel detailsModel = new DTODiaryDetailsModel();
                            if (j.optString("userCompletionTime") != null && !j.optString("userCompletionTime").isEmpty() && !j.optString("userCompletionTime").equals("null")) {
                                totalCertificateCount++;
                                // Log.d(TAG, "extractData: AssessmenTime:"+j.optString("userCompletionTime"));
                                // Log.d(TAG, "extractData: AssessmentName:"+j.optString("name"));
                                detailsModel.setEndTS(j.optString("userCompletionTime"));
                                detailsModel.setType(1);
                                detailsModel.setActivity(j.optString("name"));
                                detailsModel.setmBanner(j.optString("banner"));
                                detailsModel.setUserLD_Id("as" + j.optString("assessmentId"));
                                detailsModel.setSortingTime(Long.valueOf(detailsModel.getEndTS()));
                                detailsModel.setTotalOc(j.optInt("totalOc", 0));
                                detailsModel.setQuestionXp(j.optInt("questionXp", 0));
                                detailsModel.setRewardOC(j.optInt("rewardOC", 0));
                                detailsModel.setMappedCourseId(j.optLong("mappedCourseId", 0));
                                detailsModel.setApprovalStatus("Completed");
                                detailsModel.setDataType("SURVEY");
                                detailsModel.setPassed(true);
                               /* if(j.has("passed")) {
                                    detailsModel.setPassed(j.optBoolean("passed"));
                                    if (detailsModel.isPassed()) {
                                        detailsModel.setApprovalStatus("Passed");
                                    } else {
                                        detailsModel.setApprovalStatus("Failed");
                                    }
                                }else{
                                    detailsModel.setApprovalStatus("Passed");
                                    detailsModel.setPassed(true);
                                }*/

                                detailsModel.setCoins(j.optInt("assessmentScore", 0));
                                detailsModel.setLearningDiaryID(""+j.optInt("assessmentId"));

                                diaryDetailsModelList.add(detailsModel);
                            }
                        }
                    }
                }
            } else {
                if ((response.optString("error") != null) && (!response.optString("error").isEmpty())) {
                    OustSdkTools.showToast(response.optString("error"));
                }
            }
            if (diaryDetailsModelList != null) {
                ldView.updateDataFromAPI(diaryDetailsModelList, totalCertificateCount, 1);
            }
            DTOLearningDiary learningDiary = new DTOLearningDiary();
            learningDiary.setUid(uid);
            RoomHelper.addorUpdateLearningDiary(learningDiary, diaryDetailsModelList);

        }
    }

    public void getManualData(String mStudentId, String mStudentKey) {
        final Gson mGson = new Gson();
        Log.d(TAG, "getManualData: ");
        mManualDiaryPath = "landingPage/" + mStudentKey + "/learningDiary";
        OustFirebaseTools.getRootRef().child(mManualDiaryPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ManualEntryList = new ArrayList<>();
                try {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        DTODiaryDetailsModel model = new DTODiaryDetailsModel();
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot1.getValue();
                        if (map != null) {
                            if (map.get("endTS") != null) {
                                model.setActivity((String) map.get("activityName"));
                                model.setEndTS((String) map.get("endTS"));
                                model.setStartTS((String) map.get("startTS"));
                                model.setComments((String) map.get("comments"));
                                model.setType(2);
                                model.setDataType("NOTFOUND");
                                model.setUserLD_Id("" + map.get("userLD_Id"));
                                if (map.get("endTS") != null) {
                                    String updateTime = (String) map.get("endTS");
                                    if (updateTime != null) {
                                        model.setSortingTime(Long.valueOf(updateTime));
                                    }
                                } else {
                                    String startTime = (String) map.get("createTS");
                                    if (startTime != null) {
                                        model.setSortingTime(Long.valueOf(startTime));
                                    }
                                }
                                if ("TO_BE_REVIEWED".equalsIgnoreCase((String) map.get("approvalStatus"))) {
                                    model.setEditable(true);
                                    model.setIsdeleted(true);
                                    model.setApprovalStatus("To be Reviewed");
                                } else if ("APPROVED".equalsIgnoreCase((String) map.get("approvalStatus"))) {
                                    model.setApprovalStatus("Approved");
                                } else {
                                    model.setApprovalStatus("Not Approved");
                                }
                                List<DTOMediaList> mediaLists = new ArrayList<>();
                                Object o1 = map.get("learningDiaryMediaDataList");
                                if (o1 != null && o1.getClass().equals(ArrayList.class)) {
                                    List<Object> levelsList = (List<Object>) map.get("learningDiaryMediaDataList");
                                    for (int i = 0; i < levelsList.size(); i++) {
                                        if (levelsList.get(i) != null) {
                                            final HashMap<String, Object> levelMap = (HashMap<String, Object>) levelsList.get(i);
                                            DTOMediaList mediaList = new DTOMediaList();
                                            mediaList.setFileName((String) levelMap.get("fileName"));
                                            mediaList.setFileSize((String) levelMap.get("fileSize"));
                                            mediaList.setFileType((String) levelMap.get("fileType"));
                                            mediaList.setUserLdMedia_Id((long) levelMap.get("userLdMedia_Id"));
                                            mediaLists.add(mediaList);
                                            Log.d(TAG, "onDataChange: MediaList:" + mediaList.getUserLdMedia_Id());
                                            Log.d(TAG, "onDataChange: MediaName:" + mediaList.getFileName());
                                        }
                                    }
                                }
                                if (mediaLists.size() > 0) {
                                    model.setLearningDiaryMediaDataList(mediaLists);
                                } else {
                                    model.setLearningDiaryMediaDataList(null);
                                }
                                if (mediaLists.size() > 0) {
                                    model.setmBanner(AppConstants.StringConstants.S3_BUCKET_IMAGE_URL + mediaLists.get(0).getFileName());
                                }
                                ManualEntryList.add(model);

                                ldView.updateDataFromAPI(ManualEntryList, ManualEntryList.size(), 2);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ldView.onError("Something not good at server");
            }
        });
        OustFirebaseTools.getRootRef().child(mManualDiaryPath).keepSynced(true);
    }

    public void getLastEntryDate(String mStudentKey) {
        String Path = "landingPage/" + mStudentKey + "/learningDiary/lastWorkDiaryEntryEndDateTime";
        OustFirebaseTools.getRootRef().child(Path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    ldView.updateLastEntryDate(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void AddManualData(DTODiaryDetailsModel diaryDetailsModel, String studentId, List<LearningDiaryMediaDataList> learningDiaryMediaDataList)
    {
        ldView.showProgressBar(1);
        ldView.showAlertProgressBar();
        AddLearningDiaryManually addLearningDiaryManually = new AddLearningDiaryManually();
        DetailsModel dataModel = new DetailsModel();

        dataModel.setActivityName(diaryDetailsModel.getActivity());
        dataModel.setComments(diaryDetailsModel.getComments());
        dataModel.setEndTS(diaryDetailsModel.getEndTS());
        dataModel.setStartTS(diaryDetailsModel.getStartTS());
        dataModel.setLearningDiaryMediaDataList(learningDiaryMediaDataList);
        addLearningDiaryManually.setStudentid(studentId);
        addLearningDiaryManually.setDiaryDetailsModel(dataModel);

        Gson gson = new GsonBuilder().create();

        String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.create_learning_diary));
        String requestDataJson = null;
        try {
            requestDataJson = gson.toJson(addLearningDiaryManually);
            Log.d(TAG, "AddManualData: " + requestDataJson);
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }



        ApiCallUtils.doNetworkCall(Request.Method.POST, url, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResult: uplaodto :" + response.toString());
                ldView.hideProgressBar(1);
                ldView.hideAlertDialog();
                ldView.successAdded();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ldView.hideProgressBar(1);
                ldView.hideAlertProgressbar();
                ldView.failureAdded();
            }
        });


    }


    public void UpdateManualData(final DTODiaryDetailsModel diaryDetailsModel, String studentId, List<LearningDiaryMediaDataList> learningDiaryMediaDataList, final boolean isMediaChanged)
    {
        Log.d(TAG, "UpdateManualData: ");
        ldView.showProgressBar(1);
        ldView.showAlertProgressBar();
        AddLearningDiaryManually addLearningDiaryManually = new AddLearningDiaryManually();
        DetailsModel dataModel = new DetailsModel();

        dataModel.setActivityName(diaryDetailsModel.getActivity());
        dataModel.setComments(diaryDetailsModel.getComments());
        dataModel.setEndTS(diaryDetailsModel.getEndTS());
        dataModel.setStartTS(diaryDetailsModel.getStartTS());
        dataModel.setLearningDiaryMediaDataList(learningDiaryMediaDataList);
        dataModel.setApprovalStatus(2 + "");
        dataModel.setUserLD_Id(Long.valueOf(diaryDetailsModel.getUserLD_Id()));
        dataModel.setMediaChanged(isMediaChanged);

        addLearningDiaryManually.setStudentid(studentId);
        addLearningDiaryManually.setDiaryDetailsModel(dataModel);

        Gson gson = new GsonBuilder().create();

        String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.update_learning_diary) + diaryDetailsModel.getUserLD_Id());
        Log.d(TAG, "UpdateManualData: " + url);
        String requestDataJson = null;
        try {
            requestDataJson = gson.toJson(addLearningDiaryManually);
            Log.d(TAG, "AddManualData: " + requestDataJson);

        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        ApiCallUtils.doNetworkCall(Request.Method.PUT, url, OustSdkTools.getRequestObject(requestDataJson), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResult: uplaodto :" + response.toString());
                ldView.hideProgressBar(1);
                ldView.hideAlertDialog();
                ldView.successUpdate();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                ldView.hideProgressBar(1);
                ldView.hideAlertProgressbar();
                ldView.failureUpdate();

            }
        });

        /*ApiClient.jsonRequest(OustSdkApplication.getContext(), Request.Method.PUT, url, jsonObject, json, new ApiClient.NResultListener<JSONObject>() {
            @Override
            public void onResult(int resultCode, JSONObject tResult) {
                Log.d(TAG, "onResult: uplaodto :" + tResult.toString());
                ldView.hideProgressBar(1);
                ldView.hideAlertDialog();
                ldView.successUpdate();
            }

            @Override
            public void onFailure(int mError) {
                ldView.hideProgressBar(1);
                ldView.hideAlertProgressbar();
                ldView.failureUpdate();

            }
        });*/
    }

    public void deleteManualEntry(String userId, final String lD_ID) {
        String url = HttpManager.getAbsoluteUrl(OustSdkApplication.getContext().getResources().getString(R.string.delete_learning_diary) + userId + "/" + lD_ID);
        Log.d(TAG, "deleteManualEntry: " + url);
        ldView.showProgressBar(1);
        JSONObject js = new JSONObject();
        try {
            js.put("name", "anything");
        } catch (JSONException e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        ApiCallUtils.doNetworkCallWithContentType(Request.Method.DELETE, url, OustSdkTools.getRequestObjectforJSONObject(js), new ApiCallUtils.NetworkCallback() {
            @Override
            public void onResponse(JSONObject response) {
                ldView.hideProgressBar(0);
                ldView.successDelete(lD_ID);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                ldView.hideProgressBar(0);
                ldView.onError("Unable to delete Entry!!!");
            }
        });

        // TODO: 2020-04-10 handle with out Activity


        // Make request for JSONObject
        /*JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.DELETE, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ldView.hideProgressBar(0);
                        ldView.successDelete(lD_ID);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                ldView.hideProgressBar(0);
                ldView.onError("Unable to delete Entry!!!");
            }
        }) {

            *//**
         * Passing some request headers
         *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("org-id", OustPreferences.get("tanentid"));
                return headers;
            }

        };

        Volley.newRequestQueue(OustSdkApplication.getContext()).add(jsonObjReq);*/
    }

    public void getFilterData() {
        mFilterFBPath = "system/featureLayoutInfo";
        OustFirebaseTools.getRootRef().child(mFilterFBPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FilterModel> filterModelList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    FilterModel filterModel = new FilterModel();
                    filterModel = dataSnapshot1.getValue(FilterModel.class);
                    filterModelList.add(filterModel);
                }
                if (filterModelList != null)
                    ldView.updateFilters(filterModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ldView.failureFilterData();
            }
        });
    }

    public void removeFBListener() {
        // OustFirebaseTools.getRootRef().child(mManualDiaryPath).removeEventListener();
    }
}
